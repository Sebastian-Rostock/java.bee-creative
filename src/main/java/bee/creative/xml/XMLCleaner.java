package bee.creative.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import bee.creative.io.IO;

/** Diese Klasse implementiert einen Konfigurator zum Bereinigen der über {@code JAXB} erzeugten {@code java}-Quelltextdateien.
 * <p>
 * Die Bereinigung einer Quelltextdatei umfasst folgende Änderungen:
 * <ul>
 * <li>Leere Zeilen werden entfernt.</li>
 * <li>Mit Kommentaren beginnende Zeilen werden entfernt.</li>
 * <li>Methoden zum Lesen und Schreiben von Feldern werden entfernt.</li>
 * <li>Datenfelder mit {@code private}- oder {@code protected}-Sichtbarkeit werden auf {@code public}-Sichtbarkeit geändert.</li>
 * <li>Datenfelder mit {@link List}-Wert werden auf Datenfeldern mit {@link ArrayList}-Wert geändert und mit einer neuen {@link ArrayList} initialisiert.</li>
 * </ul>
 *
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLCleaner {

	private static final Pattern A = Pattern.compile("\\s*//[^\\r\\n]*[\\r\\n]*");

	private static final Pattern B = Pattern.compile("\\s*(/\\*)([^\\*]*|\\*[^/])*\\*/");

	private static final Pattern C = Pattern.compile("if \\([^}]+\\}");

	private static final Pattern D = Pattern.compile("\\s*public[^({]*[(][^}]*[}]");

	private static final Pattern E = Pattern.compile("protected|private");

	private static final Pattern F = Pattern.compile(" (List<[^;]+);");

	/** Diese Methode bereinigt den Quelltext in der gegebenen Datei und beachtung des gegebenen Zeichensatzes. */
	public static void cleanup(final File file, final Charset charset) throws IOException {
		final String src = IO.readChars(new InputStreamReader(new FileInputStream(file), charset));
		final String res = XMLCleaner.cleanup(src);
		IO.writeChars(new OutputStreamWriter(new FileOutputStream(file), charset), res);
	}

	/** Diese Methode gibt den gegebenen Quelltext bereinigt zurück. Bei der Bereinigung werden Kommentare und Methode entfernt, {@code protected} und
	 * {@code private} Datenfelder auf {@code public} gesetzt und {@link List} Datenfelder mit {@link ArrayList} initialisiert.
	 *
	 * @param code gegebener Quelltext.
	 * @return bereinigter Quelltext. */
	public static String cleanup(final String code) {
		String res = code;
		res = XMLCleaner.A.matcher(res).replaceAll("");
		res = XMLCleaner.B.matcher(res).replaceAll("");
		res = XMLCleaner.C.matcher(res).replaceAll("");
		res = XMLCleaner.D.matcher(res).replaceAll("");
		res = XMLCleaner.E.matcher(res).replaceAll("public");
		res = XMLCleaner.F.matcher(res).replaceAll(" Array$1 = new ArrayList<>();");
		return res;
	}

	Charset charset;

	File filepath;

	/** Diese Methode bereinigt die gewählten Quelltextdateien und gibt {@code this} zurück.
	 *
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn {@link File} oder {@link Charset} unzulässig konfiguriert sind. */
	public final XMLCleaner cleanup() throws IllegalStateException, IOException {
		final File path = this.filepath;
		final Charset charset = this.charset;
		if ((path == null) || (charset == null)) throw new IllegalStateException();
		if (path.isDirectory()) {
			final File[] list = path.listFiles();
			if (list == null) return this;
			for (final File file: list) {
				if (file.isFile() && file.getName().endsWith(".java")) {
					XMLCleaner.cleanup(file, charset);
				}
			}
		} else {
			XMLCleaner.cleanup(path, charset);
		}
		return this;
	}

	public XMLCleaner() {
		this.forCharset().useDEFAULT();
	}

	/** Diese Methode öffnet den Konfigurator für das {@link Charset} zum Laden und Speichern der Quelltextdateien und gibt ihn zurück.
	 *
	 * @see InputStreamReader#InputStreamReader(java.io.InputStream, Charset)
	 * @see OutputStreamWriter#OutputStreamWriter(java.io.OutputStream, Charset)
	 * @return Konfigurator. */
	public CharsetBuilder<XMLCleaner> forCharset() {
		return new CharsetBuilder<XMLCleaner>() {

			@Override
			public Charset get() {
				return XMLCleaner.this.charset;
			}

			@Override
			public void set(final Charset value) {
				XMLCleaner.this.charset = value;
			}

			@Override
			public XMLCleaner owner() {
				return XMLCleaner.this;
			}

		};
	}

	/** Diese Methode öffnet den Konfigurator für das {@link File} und gibt ihn zurück. Das {@link File} steht entweder für eine Quelltextdateien oder ein
	 * Verzeichnis mit Quelltextdateien.
	 *
	 * @see FileInputStream
	 * @see FileOutputStream
	 * @return Konfigurator. */
	public FileBuilder<XMLCleaner> forFilepath() {
		return new FileBuilder<XMLCleaner>() {

			@Override
			public File get() {
				return XMLCleaner.this.filepath;
			}

			@Override
			public void set(final File value) {
				XMLCleaner.this.filepath = value;
			}

			@Override
			public XMLCleaner owner() {
				return XMLCleaner.this;
			}

		};
	}

}
