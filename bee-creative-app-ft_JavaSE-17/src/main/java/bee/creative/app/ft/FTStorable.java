package bee.creative.app.ft;

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
abstract class FTStorable {

	/** Diese Methode speichert die Daten dieses Objekts über {@link #persist(String)} in eine .csv.gz-Datei. */
	public abstract void persist();

	/** Diese Methode speichert die Daten dieses Objekts über {@link #persist(CSVWriter)} in die gegebene .csv.gz-Datei. */
	public void persist(final String filepath) {
		try (var stream = new GZIPOutputStream(new FileOutputStream(filepath))) {
			try (var writer = new CSVWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8))) {
				this.persist(writer);
			}
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	/** Diese Methode speichert die Daten dieses Objekts über den gegebenen {@link CSVWriter}. */
	public abstract void persist(CSVWriter writer) throws Exception;

	/** Diese Methode lädt die Daten dieses Objekts über {@link #restore(String)} aus einer .csv.gz-Datei. */
	public abstract void restore();

	/** Diese Methode lädt die Daten dieses Objekts über {@link #restore(CSVReader)} aus der gegebenen .csv.gz-Datei. */
	public void restore(final String filepath) {
		try (var stream = new GZIPInputStream(new FileInputStream(filepath))) {
			try (var reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				this.restore(reader);
			}
		} catch (final Exception error) {
			error.printStackTrace();
		}
	}

	/** Diese Methode lädt die Daten dieses Objekts über den gegebenen {@link CSVReader}. */
	public abstract void restore(CSVReader reader) throws Exception;

}
