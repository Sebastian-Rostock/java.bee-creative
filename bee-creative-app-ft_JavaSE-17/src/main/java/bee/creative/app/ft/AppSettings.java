package bee.creative.app.ft;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import bee.creative.csv.CSVReader;
import bee.creative.csv.CSVWriter;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterables;
import bee.creative.util.Producer;

/** Diese Klasse implementiert die Einstellungen und deren Persistierung.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class AppSettings implements AppStorable2 {

	public final AppOptionLong filterSize = new AppOptionLong().useMinimum(0).useMaximum(1L << 60).useIncrease(1L << 20).useValue(0);

	public final AppOptionText filterPath = new AppOptionText();

	public final AppOptionTime filterCreate = new AppOptionTime();

	public final AppOptionTime filterChange = new AppOptionTime();

	/** Dieses Feld speichert das Dateialter in Tagen. */
	public final AppOptionLong refreshOffset = new AppOptionLong().useMinimum(100).useMaximum(20000).useIncrease(100).useValue(1800);

	/** Dieses Feld speichert die Puffergröße des Dateivergleichs in Byte. */
	public final AppOptionLong contentTestSize = new AppOptionLong().useMinimum(0).useMaximum(1L << 60).useIncrease(1 << 20).useValue(1 << 24);

	/** Dieses Feld speichert die Puffergröße der Streuwertberechnung in Byte. */
	public final AppOptionLong contentHashSize = new AppOptionLong().useMinimum(0).useMaximum(1L << 60).useIncrease(1 << 20).useValue(1 << 22);

	/** Dieses Feld speichert die Zeitkorrektur in Sekunden. */
	public final AppOptionLong timenameOffset = new AppOptionLong().useMinimum(-40000000).useMaximum(40000000).useIncrease(3600).useValue(0);

	@Override
	public void persist() {
		this.persist(AppSettings.FILENAME);
	}

	@Override
	public void persist(CSVWriter writer) throws Exception {
		writer.writeEntry((Object[])AppSettings.FILEHEAD);
		writer.writeEntry(this.getOptions().translate(Producer::get).toArray());
	}

	private Iterable2<AppOption> getOptions() {
		return Iterables.fromArray(this.filterSize, this.filterPath, this.filterCreate, this.filterChange, this.refreshOffset, this.contentTestSize,
			this.contentHashSize, this.timenameOffset);
	}

	@Override
	public void restore() {
		this.restore(AppSettings.FILENAME);
	}

	@Override
	public void restore(CSVReader reader) throws Exception {
		if (!Arrays.equals(AppSettings.FILEHEAD, reader.readEntry())) return;
		var values = new LinkedList<>(Arrays.asList(reader.readEntry()));
		this.getOptions().collectAll(option -> option.set(values.pollFirst()));
	}

	/** Dieses Feld speichert den Dateinamen für {@link #persist()} und {@link #restore()}. */
	private static final File FILENAME = new File("setings.csv.gz").getAbsoluteFile();

	/** Dieses Feld speichert die Spaltennamen der Tabelle. */
	private static final String[] FILEHEAD =
		{"filterSize", "filterPath", "filterCreate", "filterChange", "refreshOffset", "contentTestSize", "contentHashSize", "timenameOffset"};

}
