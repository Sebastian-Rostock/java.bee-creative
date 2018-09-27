package bee.creative.ini;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import bee.creative.io.IO;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein Objekt zum Lesen einer {@code INI}-Datenstruktur über einen {@link Reader}.
 * <p>
 * Die {@code INI}-Datenstruktur ist eine Abfolge beliegig vieler Abschnitte, Eigenschaften, Kommentare und Leerzeilen, welche durch Zeilenumbrüche
 * {@code '\r\n'} voneinander separiert sind. Beim Parsen werden auch einzeln stehenden Zeichen {@code '\r'} und {@code '\n'} als Zeilenumbruch akzeptiert.<br>
 * Ein Abschnitt besteht aus dem Zeichen {@code '['}, dem maskierten Namen des Abschnitts und dem Zeichen {@code ']'}.<br>
 * Eine Eigenschaft besteht aus dem maskierten Schlüssel der Eigenschaft, dem Zeichen {@code '='} und dem maskierten Wert der Eigenschaft.<br>
 * Eine Kommentar besteht aus dem Zeichen {@code ';'} und dem maskierten Text des Kommentars.<br>
 * Die Maskierung der Zeichen {@code '\t'}, {@code '\r'}, {@code '\n'}, {@code '\\'}, {@code '='}, {@code ';'}, {@code '['} und {@code ']'} erfolgt durch das
 * Voranstellen des Zeichens {@code '\\'}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class INIReader implements Closeable {

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link INIReader} und gibt diesen zurück.<br>
	 * Wenn das Objekt ein {@link INIReader} ist, wird dieser geliefert. Andernfalls wird das Objekt in einen {@link Reader} {@link IO#inputReaderFrom(Object)
	 * überführt}.
	 *
	 * @see IO#inputReaderFrom(Object)
	 * @see INIReader#INIReader(Reader)
	 * @param data Objekt.
	 * @return {@link INIReader}.
	 * @throws IOException Wenn der {@link INIReader} nicht erzeugt werden kann. */
	public static INIReader from(final Object data) throws IOException {
		if (data instanceof INIReader) return (INIReader)data;
		return new INIReader(IO.inputReaderFrom(data));
	}

	/** Dieses Feld speichert den {@link Reader}. */
	protected final Reader reader;

	/** Dieses Feld speichert den Puffer für die maskierten Texte. */
	final StringBuilder builder;

	/** Dieser Konstruktor initialisiert den {@link Reader} mit der {@code INI}-Datenstruktur.
	 *
	 * @param reader {@link Reader}.
	 * @throws NullPointerException Wenn {@code reader} {@code null} ist. */
	public INIReader(final Reader reader) throws NullPointerException {
		this.reader = Objects.notNull(reader);
		this.builder = new StringBuilder();
	}

	/** Diese Methode ließt das nächste Element der {@code INI}-Datenstruktur und gibt es zurück. Wenn kein weiteres Element mehr existiert, wird {@code null}
	 * geliefert.
	 *
	 * @return nächstes Element oder {@code null}.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst. */
	public INIToken read() throws IOException {
		synchronized (this.reader) {
			return this.readImpl();
		}
	}

	@SuppressWarnings ("javadoc")
	final INIToken readImpl() throws IOException {
		while (true) {
			final int symbol = this.reader.read();
			switch (symbol) {
				case -1:
					return null;
				case '[':
					final String section = this.readSectionImpl();
					return INIToken.fromSection(section);
				case ';':
					final String comment = this.readCommentImpl();
					return INIToken.fromComment(comment);
				default:
					final String key = this.readKeyImpl(symbol);
					final String value = this.readValueImpl();
					return INIToken.fromProperty(key, value);
				case '\r':
				case '\n':
			}
		}
	}

	/** Diese Methode ließt den Schlüssel einer Eigenschaft und gibt diesen zurück.<br>
	 * Der Schlüssel beginnt mit dem gegebenen Zeichen und Endet vor dem Zeichen {@code '='}.
	 *
	 * @param symbol erstes Zeichen des Schlüssels.
	 * @return Schlüssel einer Eigenschaft.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final String readKeyImpl(int symbol) throws IOException, IllegalArgumentException {
		final Reader source = this.reader;
		final StringBuilder result = this.builder;
		result.setLength(0);
		while (true) {
			switch (symbol) {
				case -1:
				case '\r':
				case '\n':
					throw new IllegalArgumentException();
				case '=':
					return result.toString();
				case '\\':
					symbol = this.readSymbolImpl(source.read());
			}
			result.append((char)symbol);
			symbol = source.read();
		}
	}

	/** Diese Methode ließt den Wert einer Eigenschaft und gibt diesen zurück.
	 *
	 * @return Wert einer Eigenschaft.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final String readValueImpl() throws IOException, IllegalArgumentException {
		final Reader source = this.reader;
		final StringBuilder result = this.builder;
		result.setLength(0);
		while (true) {
			int symbol = source.read();
			switch (symbol) {
				case -1:
				case '\r':
				case '\n':
					return result.toString();
				case '\\':
					symbol = this.readSymbolImpl(source.read());
			}
			result.append((char)symbol);
		}
	}

	/** Diese Methode ließt den Namen eines Abschnitts und gibt diesen zurück.
	 *
	 * @return Name eines Abschnitts.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final String readSectionImpl() throws IOException, IllegalArgumentException {
		final Reader source = this.reader;
		final StringBuilder result = this.builder;
		result.setLength(0);
		while (true) {
			int symbol = source.read();
			switch (symbol) {
				case -1:
				case '\r':
				case '\n':
				case '[':
					throw new IllegalArgumentException();
				case ']':
					symbol = source.read();
					switch (symbol) {
						case -1:
						case '\r':
						case '\n':
							return result.toString();
					}
					throw new IllegalArgumentException();
				case '\\':
					symbol = this.readSymbolImpl(source.read());
			}
			result.append((char)symbol);
		}
	}

	/** Diese Methode gibt das Zeiche zurück, durch die Verkettung von {@code '\'} und dem gegebenen Zeichen maskiert wurde.
	 *
	 * @param symbol Zeichen nach dem {@code '\'}.
	 * @return unmaskiertes Zeichen.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final int readSymbolImpl(final int symbol) throws IllegalArgumentException {
		switch (symbol) {
			case '\\':
			case '=':
			case ';':
			case ']':
			case '[':
				return symbol;
			case 't':
				return '\t';
			case 'r':
				return '\r';
			case 'n':
				return '\n';
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode ließt den Text eines Kommentars und gibt diesen zurück.
	 *
	 * @return Text eines Kommentars.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final String readCommentImpl() throws IOException {
		final Reader source = this.reader;
		final StringBuilder result = this.builder;
		result.setLength(0);
		while (true) {
			int symbol = source.read();
			switch (symbol) {
				case -1:
				case '\r':
				case '\n':
					return result.toString();
				case '\\':
					symbol = this.readSymbolImpl(source.read());
			}
			result.append((char)symbol);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void close() throws IOException {
		synchronized (this.reader) {
			this.reader.close();
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		synchronized (this.reader) {
			return Objects.toInvokeString(this, this.reader);
		}
	}

}
