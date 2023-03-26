package bee.creative.app.ft;

import java.util.Arrays;
import bee.creative.csv.CSVReader;
import bee.creative.csv.CSVWriter;

/** Diese Klasse implementiert die Einstellungen und deren Persistierung.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTSettings extends FTStorable {

	/** Dieses Feld speichert den Dateinamen für {@link #persist()} und {@link #restore()}. */
	public static final String FILENAME = "setings.csv.gz";

	/** Dieses Feld speichert das Dateialter in Tagen für {@link FTWindow#refreshInputFiles()}. */
	public final FTLongOption copyFilesTimeFilter = new FTLongOption(1800, 100, 20000, 100);

	/** Dieses Feld speichert die Zeitkorrektur in Sekunden für {@link FTWindow#createTargetsWithTimenameFromTime()} und {@link FTWindow#createTargetsWithTimepathFromTime()}. */
	public final FTLongOption moveFilesTimeOffset = new FTLongOption(0L, -40000000, 40000000, 3600);

	/** Dieses Feld speichert die Puffergröße des Dateivergleichs für {@link FTWindow#createTableWithClones()}. */
	public final FTLongOption findClonesTestSize = new FTLongOption(20971520, 0, 1000000000000000000L, 1048576);

	/** Dieses Feld speichert die Puffergröße der Streuwertberechnung für {@link FTWindow#createTableWithClones()}. */
	public final FTLongOption findClonesHashSize = new FTLongOption(1048576, 0, 1000000000000000000L, 1048576);

	@Override
	public void persist() {
		this.persist(FTSettings.FILENAME);
	}

	@Override
	public void persist(final CSVWriter writer) throws Exception {
		writer.writeEntry((Object[])FTSettings.FILEHEAD);
		writer.writeEntry(this.copyFilesTimeFilter.val, this.moveFilesTimeOffset.val, this.findClonesTestSize.val, this.findClonesHashSize.val);
	}

	@Override
	public void restore() {
		this.restore(FTSettings.FILENAME);
	}

	@Override
	public void restore(final CSVReader reader) throws Exception {
		if (!Arrays.equals(FTSettings.FILEHEAD, reader.readEntry())) return;
		this.copyFilesTimeFilter.val = Long.parseLong(reader.readValue());
		this.moveFilesTimeOffset.val = Long.parseLong(reader.readValue());
		this.findClonesTestSize.val = Long.parseLong(reader.readValue());
		this.findClonesHashSize.val = Long.parseLong(reader.readValue());
	}

	private static final String[] FILEHEAD = {"copyFilesTimeFilter", "moveFilesTimeOffset", "findClonesTestSize", "findClonesHashSize"};

}
