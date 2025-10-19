package bee.creative.ini;

import static bee.creative.io.IO.charReaderFrom;
import static bee.creative.lang.Objects.notNull;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import bee.creative.io.IO;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein Objekt zum Lesen einer {@code INI}-Datenstruktur über einen {@link Reader}.
 * <p>
 * Die {@code INI}-Datenstruktur ist eine Abfolge beliegig vieler Abschnitte, Eigenschaften, Kommentare und Leerzeilen, welche durch Zeilenumbrüche
 * {@code '\r\n'} voneinander separiert sind. Beim Parsen werden auch einzeln stehenden Zeichen {@code '\r'} und {@code '\n'} als Zeilenumbruch akzeptiert. Ein
 * Abschnitt besteht aus dem Zeichen {@code '['}, dem maskierten Namen des Abschnitts und dem Zeichen {@code ']'}. Eine Eigenschaft besteht aus dem maskierten
 * Schlüssel der Eigenschaft, dem Zeichen {@code '='} und dem maskierten Wert der Eigenschaft. Eine Kommentar besteht aus dem Zeichen {@code ';'} und dem
 * maskierten Text des Kommentars. Die Maskierung der Zeichen {@code '\t'}, {@code '\r'}, {@code '\n'}, {@code '\\'}, {@code '='}, {@code ';'}, {@code '['} und
 * {@code ']'} erfolgt durch das Voranstellen des Zeichens {@code '\\'}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class INIReader implements Closeable {

	/** Diese Methode liefert den zu {@code source} erzeugten {@link INIReader}.
	 * 
	 * @see #INIReader(Reader)
	 * @see IO#charReaderFrom(Object) */
	public static INIReader iniReaderFrom(Object source) throws IOException {
		if (source instanceof INIReader) return (INIReader)source;
		return new INIReader(charReaderFrom(source));
	}

	/** Dieser Konstruktor initialisiert den {@link Reader} mit der {@code INI}-Datenstruktur.
	 *
	 * @param reader {@link Reader}.
	 * @throws NullPointerException Wenn {@code reader} {@code null} ist. */
	public INIReader(Reader reader) throws NullPointerException {
		this.reader = notNull(reader);
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

	@Override
	public void close() throws IOException {
		synchronized (this.reader) {
			this.reader.close();
		}
	}

	@Override
	public String toString() {
		synchronized (this.reader) {
			return Objects.toInvokeString(this, this.reader);
		}
	}

	/** Dieses Feld speichert den {@link Reader}. */
	protected final Reader reader;

	/** Dieses Feld speichert den Puffer für die maskierten Texte. */
	final StringBuilder builder;

	INIToken readImpl() throws IOException {
		while (true) {
			var symbol = this.reader.read();
			switch (symbol) {
				case -1:
					return null;
				case '[':
					var section = this.readSectionImpl();
					return INIToken.iniSectionFrom(section);
				case ';':
					var comment = this.readCommentImpl();
					return INIToken.iniCommentFrom(comment);
				default:
					var key = this.readKeyImpl(symbol);
					var value = this.readValueImpl();
					return INIToken.iniPropertyProm(key, value);
				case '\r':
				case '\n':
			}
		}
	}

	/** Diese Methode ließt den Schlüssel einer Eigenschaft und gibt diesen zurück. Der Schlüssel beginnt mit dem gegebenen Zeichen und Endet vor dem Zeichen
	 * {@code '='}.
	 *
	 * @param symbol erstes Zeichen des Schlüssels.
	 * @return Schlüssel einer Eigenschaft.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	String readKeyImpl(int symbol) throws IOException, IllegalArgumentException {
		var source = this.reader;
		var result = this.builder;
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
	String readValueImpl() throws IOException, IllegalArgumentException {
		var source = this.reader;
		var result = this.builder;
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
	String readSectionImpl() throws IOException, IllegalArgumentException {
		var source = this.reader;
		var result = this.builder;
		result.setLength(0);
		while (true) {
			var symbol = source.read();
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
	int readSymbolImpl(int symbol) throws IllegalArgumentException {
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
	String readCommentImpl() throws IOException {
		var source = this.reader;
		var result = this.builder;
		result.setLength(0);
		while (true) {
			var symbol = source.read();
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

}
