package bee.creative.ini;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import bee.creative.util.IO;
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
public final class INIReader implements Closeable {

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

	{}

	/** Dieses Feld speichert den {@link Reader}. */
	final Reader _reader_;

	/** Dieses Feld speichert den Puffer für die maskierten Texte. */
	final StringBuilder _builder_;

	/** Dieser Konstruktor initialisiert den {@link Reader} mit der {@code INI}-Datenstruktur.
	 * 
	 * @param reader {@link Reader}.
	 * @throws NullPointerException Wenn {@code reader} {@code null} ist. */
	public INIReader(final Reader reader) throws NullPointerException {
		if (reader == null) throw new NullPointerException("reader = null");
		this._reader_ = reader;
		this._builder_ = new StringBuilder();
	}

	{}

	/** Diese Methode ließt das nächste Element der {@code INI}-Datenstruktur und gibt es zurück. Wenn kein weiteres Element mehr existiert, wird {@code null}
	 * geliefert.
	 * 
	 * @return nächstes Element oder {@code null}.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst. */
	public final INIToken read() throws IOException {
		while (true) {
			final int symbol = this._reader_.read();
			switch (symbol) {
				case -1:
					return null;
				case '[':
					final String section = this._readSection_();
					return INIToken.fromSection(section);
				case ';':
					final String comment = this._readComment_();
					return INIToken.fromComment(comment);
				default:
					final String key = this._readKey_(symbol);
					final String value = this._readValue_();
					return INIToken.fromProperty(key, value);
				case '\r':
				case '\n': // skip
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
	final String _readKey_(int symbol) throws IOException, IllegalArgumentException {
		final Reader source = this._reader_;
		final StringBuilder result = this._builder_;
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
					symbol = this._readSymbol_(source.read());
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
	final String _readValue_() throws IOException, IllegalArgumentException {
		final Reader source = this._reader_;
		final StringBuilder result = this._builder_;
		result.setLength(0);
		while (true) {
			int symbol = source.read();
			switch (symbol) {
				case -1:
				case '\r':
				case '\n':
					return result.toString();
				case '\\':
					symbol = this._readSymbol_(source.read());
			}
			result.append((char)symbol);
		}
	}

	/** Diese Methode ließt den Namen eines Abschnitts und gibt diesen zurück.
	 * 
	 * @return Name eines Abschnitts.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final String _readSection_() throws IOException, IllegalArgumentException {
		final Reader source = this._reader_;
		final StringBuilder result = this._builder_;
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
					symbol = this._readSymbol_(source.read());
			}
			result.append((char)symbol);
		}
	}

	/** Diese Methode gibt das Zeiche zurück, durch die Verkettung von {@code '\'} und dem gegebenen Zeichen maskiert wurde.
	 * 
	 * @param symbol Zeichen nach dem {@code '\'}.
	 * @return unmaskiertes Zeichen.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	final int _readSymbol_(final int symbol) throws IllegalArgumentException {
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
	final String _readComment_() throws IOException {
		final Reader source = this._reader_;
		final StringBuilder result = this._builder_;
		result.setLength(0);
		while (true) {
			int symbol = source.read();
			switch (symbol) {
				case -1:
				case '\r':
				case '\n':
					return result.toString();
				case '\\':
					symbol = this._readSymbol_(source.read());
			}
			result.append((char)symbol);
		}
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final void close() throws IOException {
		this._reader_.close();
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._reader_);
	}

}
