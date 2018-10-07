package bee.creative.xml;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public final class XMLCleaner {

	/** Diese Klasse implementiert den Konfigurator für das {@link File} eines {@link XMLCleaner}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public final class FileData extends BaseFileData<FileData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public XMLCleaner closeFileData() {
			return XMLCleaner.this;
		}

		/** {@inheritDoc} */
		@Override
		protected final FileData customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das {@link Charset} eines {@link XMLCleaner}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public final class CharsetData extends BaseCharsetData<CharsetData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public XMLCleaner closeCharsetData() {
			return XMLCleaner.this;
		}

		/** {@inheritDoc} */
		@Override
		protected final CharsetData customThis() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final Pattern listFieldPattern = Pattern.compile("^(\\s+)protected (List<\\S+)( [^;]+);");

	@SuppressWarnings ("javadoc")
	static final String listFieldReplace = "$1 public Array$2$3 = new Array$2();";

	@SuppressWarnings ("javadoc")
	static final Pattern listMethodPattern = Pattern.compile("^(\\s+)public List<");

	@SuppressWarnings ("javadoc")
	static final Pattern itemFieldPattern = Pattern.compile("^(\\s+)(?:protected|private) (.*)");

	@SuppressWarnings ("javadoc")
	static final String itemFieldReplace = "$1public $2";

	@SuppressWarnings ("javadoc")
	static final Pattern itemMethodPattern = Pattern.compile("^(\\s+)public \\S+ (value\\(\\)|([gs]et|is)\\S+)");

	@SuppressWarnings ("javadoc")
	static final Pattern commentPattern = Pattern.compile("^\\s*[*/]");

	/** Dieses Feld speichert den Konfigurator für {@link #openFileData()}. */
	final FileData fileData = new FileData();

	/** Dieses Feld speichert den Konfigurator für {@link #openCharsetData()}. */
	final CharsetData charsetData = new CharsetData();

	/** Diese Methode bereinigt die gewählten Quelltextdateien und gibt {@code this} zurück.
	 *
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn {@link File} oder {@link Charset} unzulässig konfiguriert sind. */
	public final XMLCleaner cleanup() throws IllegalStateException {
		final File path = this.fileData.get();
		final Charset charset = this.charsetData.get();
		if ((path == null) || (charset == null)) throw new IllegalStateException();
		if (path.isDirectory()) {
			final File[] list = path.listFiles();
			if (list == null) return this;
			for (final File file: list) {
				this.cleanup(file, charset);
			}
		} else {
			this.cleanup(path, charset);
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void cleanup(final File file, final Charset charset) throws IllegalStateException {
		try {
			if (!file.isFile() || !file.getName().endsWith(".java")) return;
			final List<String> sourceList = Files.readAllLines(file.toPath(), charset);
			final int count = sourceList.size();
			final List<String> targetList = new ArrayList<>(count);
			final StringBuffer target = new StringBuffer();
			int index = 0;
			Matcher matcher;
			String space = null;
			while (index < count) {
				final String source = sourceList.get(index);
				String value;
				if (source.isEmpty()) {
					value = null;
				} else if (space != null) {
					space = source.startsWith(space) ? null : space;
					value = null;
				} else if ((matcher = XMLCleaner.commentPattern.matcher(source)).find()) {
					value = source;
				} else if ((matcher = XMLCleaner.listMethodPattern.matcher(source)).find()) {
					space = matcher.group(1) + "}";
					value = null;
				} else if ((matcher = XMLCleaner.itemMethodPattern.matcher(source)).find()) {
					space = matcher.group(1) + "}";
					value = null;
				} else if ((matcher = XMLCleaner.listFieldPattern.matcher(source)).find()) {
					target.setLength(0);
					matcher.appendReplacement(target, XMLCleaner.listFieldReplace);
					value = target.toString();
				} else if ((matcher = XMLCleaner.itemFieldPattern.matcher(source)).find()) {
					target.setLength(0);
					matcher.appendReplacement(target, XMLCleaner.itemFieldReplace);
					value = target.toString();
				} else {
					value = source;
				}
				index += 1;
				if (!source.equals(value)) {
					targetList.add("//" + source);
					if (value != null) {
						targetList.add(value);
					}
				} else {
					targetList.add(source);
				}
			}
			Files.write(file.toPath(), targetList, charset);
		} catch (final Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode öffnet den Konfigurator für das {@link File} und gibt ihn zurück. Das {@link File} steht entweder für eine Quelltextdateien oder ein
	 * Verzeichnis mit Quelltextdateien.
	 *
	 * @see Files#readAllLines(Path, Charset)
	 * @see Files#write(Path, Iterable, Charset, OpenOption...)
	 * @return Konfigurator. */
	public final FileData openFileData() {
		return this.fileData;
	}

	/** Diese Methode öffnet den Konfigurator für das {@link Charset} zum Laden und Speichern der Quelltextdateien und gibt ihn zurück.
	 *
	 * @see Files#readAllLines(Path, Charset)
	 * @see Files#write(Path, Iterable, Charset, OpenOption...)
	 * @return Konfigurator. */
	public final CharsetData openCharsetData() {
		return this.charsetData;
	}

}
