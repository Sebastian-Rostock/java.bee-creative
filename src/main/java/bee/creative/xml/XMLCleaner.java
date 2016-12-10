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

		{}

		/** {@inheritDoc} */
		@Override
		protected final FileData _this_() {
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

		{}

		/** {@inheritDoc} */
		@Override
		protected final CharsetData _this_() {
			return this;
		}

	}

	{}

	@SuppressWarnings ("javadoc")
	static final Pattern _listFieldPattern_ = Pattern.compile("^(\\s+)protected (List<\\S+)( [^;]+);");

	@SuppressWarnings ("javadoc")
	static final String _listFieldReplace_ = "$1 public Array$2$3 = new Array$2();";

	@SuppressWarnings ("javadoc")
	static final Pattern _listMethodPattern_ = Pattern.compile("^\\s+public List<");

	@SuppressWarnings ("javadoc")
	static final Pattern _itemFieldPattern_ = Pattern.compile("^(\\s+)(?:protected|private) (.*)");

	@SuppressWarnings ("javadoc")
	static final String _itemFieldReplace_ = "$1public $2";

	@SuppressWarnings ("javadoc")
	static final Pattern _itemMethodPattern_ = Pattern.compile("^\\s+public \\S+ (value\\(\\)|[gs]et\\S+)");

	@SuppressWarnings ("javadoc")
	static final Pattern _commentPattern_ = Pattern.compile("^\\s*[*/]");

	{}

	/** Dieses Feld speichert den Konfigurator für {@link #openFileData()}. */
	final FileData _fileData_ = new FileData();

	/** Dieses Feld speichert den Konfigurator für {@link #openCharsetData()}. */
	final CharsetData _charsetData_ = new CharsetData();

	{}

	/** Diese Methode bereinigt die gewählten Quelltextdateien und gibt {@code this} zurück.
	 *
	 * @return {@code this}.
	 * @throws IllegalStateException Wenn {@link File} oder {@link Charset} unzulässig konfiguriert sind. */
	public final XMLCleaner cleanup() throws IllegalStateException {
		final File path = this._fileData_.get();
		final Charset charset = this._charsetData_.get();
		if ((path == null) || (charset == null)) throw new IllegalStateException();
		if (path.isDirectory()) {
			final File[] list = path.listFiles();
			if (list == null) return this;
			for (final File file: list) {
				this._cleanup_(file, charset);
			}
		} else {
			this._cleanup_(path, charset);
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void _cleanup_(final File file, final Charset charset) throws IllegalStateException {
		try {
			if (!file.isFile() || !file.getName().endsWith(".java")) return;
			final List<String> sourceList = Files.readAllLines(file.toPath(), charset);
			final int count = sourceList.size();
			final List<String> targetList = new ArrayList<>(count);
			final StringBuffer target = new StringBuffer();
			int index = 0;
			Matcher matcher;
			while (index < count) {
				final String source = sourceList.get(index);
				if (source.isEmpty()) {
					index += 1;
				} else if ((matcher = XMLCleaner._commentPattern_.matcher(source)).find()) {
					index += 1;
				} else if ((matcher = XMLCleaner._listMethodPattern_.matcher(source)).find()) {
					index += 6;
				} else if ((matcher = XMLCleaner._itemMethodPattern_.matcher(source)).find()) {
					index += 3;
				} else if ((matcher = XMLCleaner._listFieldPattern_.matcher(source)).find()) {
					index += 1;
					target.setLength(0);
					matcher.appendReplacement(target, XMLCleaner._listFieldReplace_);
					targetList.add(target.toString());
				} else if ((matcher = XMLCleaner._itemFieldPattern_.matcher(source)).find()) {
					index += 1;
					target.setLength(0);
					matcher.appendReplacement(target, XMLCleaner._itemFieldReplace_);
					targetList.add(target.toString());
				} else {
					index += 1;
					targetList.add(source);
				}
			}
			Files.write(file.toPath(), targetList, charset);
		} catch (final Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode öffnet den Konfigurator für das {@link File} und gibt ihn zurück.<br>
	 * Das {@link File} steht entweder für eine Quelltextdateien oder ein Verzeichnis mit Quelltextdateien.
	 *
	 * @see Files#readAllLines(Path, Charset)
	 * @see Files#write(Path, Iterable, Charset, OpenOption...)
	 * @return Konfigurator. */
	public final FileData openFileData() {
		return this._fileData_;
	}

	/** Diese Methode öffnet den Konfigurator für das {@link Charset} zum Laden und Speichern der Quelltextdateien und gibt ihn zurück.
	 *
	 * @see Files#readAllLines(Path, Charset)
	 * @see Files#write(Path, Iterable, Charset, OpenOption...)
	 * @return Konfigurator. */
	public final CharsetData openCharsetData() {
		return this._charsetData_;
	}

}
