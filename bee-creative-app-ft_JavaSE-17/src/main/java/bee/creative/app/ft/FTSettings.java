package bee.creative.app.ft;

import java.io.File;
import java.util.Arrays;
import bee.creative.csv.CSVReader;
import bee.creative.csv.CSVWriter;

/** Diese Klasse implementiert die Einstellungen und deren Persistierung.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
class FTSettings implements FTStorable2 {

	/** Dieses Feld speichert das Dateialter in Tagen für {@link FTWindow#refreshInputFiles()}. */
	public final FTOptionInt copyFilesTimeFilter = new FTOptionInt(1800, 100, 20000, 100);

	/** Dieses Feld speichert die Zeitkorrektur in Sekunden für {@link FTWindow#createTargetsWithTimenameFromTime()} und
	 * {@link FTWindow#createTargetsWithTimepathFromTime()}. */
	public final FTOptionInt moveFilesTimeOffset = new FTOptionInt(0, -40000000, 40000000, 3600);

	/** Dieses Feld speichert die Puffergröße des Dateivergleichs für {@link FTWindow#createTableWithClones()}. */
	public final FTOptionInt findClonesTestSize = new FTOptionInt(20971520, 0, 2000000000, 1048576);

	/** Dieses Feld speichert die Puffergröße der Streuwertberechnung für {@link FTWindow#createTableWithClones()}. */
	public final FTOptionInt findClonesHashSize = new FTOptionInt(1048576, 0, 2000000000, 1048576);

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
		this.copyFilesTimeFilter.val = Integer.parseInt(reader.readValue());
		this.moveFilesTimeOffset.val = Integer.parseInt(reader.readValue());
		this.findClonesTestSize.val = Integer.parseInt(reader.readValue());
		this.findClonesHashSize.val = Integer.parseInt(reader.readValue());
	}

	/** Dieses Feld speichert den Dateinamen für {@link #persist()} und {@link #restore()}. */
	private static final File FILENAME = new File("setings.csv.gz").getAbsoluteFile();

	/** Dieses Feld speichert die Spaltennamen der Tabelle. */
	private static final String[] FILEHEAD = {"copyFilesTimeFilter", "moveFilesTimeOffset", "findClonesTestSize", "findClonesHashSize"};

}
