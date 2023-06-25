package bee.creative.app.ft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import bee.creative.csv.CSVReader;
import bee.creative.csv.CSVWriter;

/** Diese Klasse implementiert ein als .csv.gz-Datei speicherbares Objekt.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface FTStorable2 extends FTStorable {

	/** Diese Methode speichert die Daten dieses Objekts über {@link #persist(File)} in eine .csv.gz-Datei. */
	@Override
	void persist();

	/** Diese Methode speichert die Daten dieses Objekts über {@link #persist(CSVWriter)} in die gegebene .csv.gz-Datei. */
	default void persist(final File file) {
		try (var stream = new GZIPOutputStream(new FileOutputStream(file))) {
			try (var writer = new CSVWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) {
				this.persist(writer);
			}
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	/** Diese Methode speichert die Daten dieses Objekts über den gegebenen {@link CSVWriter}. */
	void persist(CSVWriter writer) throws Exception;

	/** Diese Methode lädt die Daten dieses Objekts über {@link #restore(File)} aus einer .csv.gz-Datei. */
	@Override
	void restore();

	/** Diese Methode lädt die Daten dieses Objekts über {@link #restore(CSVReader)} aus der gegebenen .csv.gz-Datei. */
	default void restore(final File file) {
		if (!file.isFile() || (file.length() == 0)) return;
		try (var stream = new GZIPInputStream(new FileInputStream(file))) {
			try (var reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				this.restore(reader);
			}
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	/** Diese Methode lädt die Daten dieses Objekts über den gegebenen {@link CSVReader}. */
	void restore(CSVReader reader) throws Exception;

}
