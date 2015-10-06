package bee.creative.ini;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert ein Objekt zum Lesen einer {@code INI}-Datenstruktur über einen {@link Reader}.
 * <p>
 * Die {@code INI}-Datenstruktur ist eine Abfolge beliegig vieler Abschnitte, Eigenschaften, Kommentare und Leerzeilen, welche durch Zeilenumbrüche
 * {@code '\r\n'} voneinander separiert sind.<br>
 * Ein Abschnitt besteht aus dem Zeichen {@code '['}, dem maskierten Namen des Abschnitts und dem Zeichen {@code ']'}.<br>
 * Eine Eigenschaft besteht aus dem maskierten Schlüssel der Eigenschaft, dem Zeichen {@code '='} und dem maskierten Wert der Eigenschaft.<br>
 * Eine Kommentar besteht aus dem Zeichen {@code ';'} und dem maskierten Text des Kommentars.<br>
 * Die Maskierung der Zeichen {@code '\t'}, {@code '\r'}, {@code '\n'}, {@code '\\'}, {@code '='}, {@code ';'}, {@code '['} und {@code ']'} erfolgt durch das
 * Voranstellen des Zeichens {@code '\\'}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class INIReader implements Closeable {

	/**
	 * Dieses Feld speichert den {@link Reader}.
	 */
	final Reader reader;

	/**
	 * Dieses Feld speichert den Puffer für die maskierten Texte.
	 */
	final StringBuilder builder;

	/**
	 * Dieser Konstruktor initialisiert das {@link File} mit der {@code INI}-Datenstruktur.
	 * 
	 * @param file {@link File}.
	 * @throws IOException Wenn {@link FileReader#FileReader(File)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code file} {@code null} ist.
	 */
	public INIReader(final File file) throws IOException, NullPointerException {
		this(new BufferedReader(new FileReader(file)));
	}

	/**
	 * Dieser Konstruktor initialisiert den {@link Reader} mit der {@code INI}-Datenstruktur.
	 * 
	 * @param reader {@link Reader}.
	 * @throws NullPointerException Wenn {@code reader} {@code null} ist.
	 */
	public INIReader(final Reader reader) throws NullPointerException {
		if (reader == null) throw new NullPointerException("reader = null");
		this.reader = reader;
		this.builder = new StringBuilder();
	}

	{}

	/**
	 * Diese Methode ließt das nächste Element der {@code INI}-Datenstruktur und gibt es zurück. Wenn kein weiteres Element mehr existiert, wird {@code null}
	 * geliefert.
	 * 
	 * @return nächstes Element oder {@code null}.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 */
	public INIToken read() throws IOException {
		while (true) {
			final int symbol = this.reader.read();
			switch (symbol) {
				case -1:
					return null;
				case '[':
					final String section = this.readSection();
					return INIToken.sectionToken(section);
				case ';':
					final String comment = this.readComment();
					return INIToken.commentToken(comment);
				default:
					final String key = this.readKey(symbol);
					final String value = this.readValue();
					return INIToken.propertyToken(key, value);
				case '\r':
				case '\n': // skip
			}
		}
	}

	/**
	 * Diese Methode ließt den Schlüssel einer Eigenschaft und gibt diesen zurück.<br>
	 * Der Schlüssel beginnt mit dem gegebenen Zeichen und Endet vor dem Zeichen {@code '='}.
	 * 
	 * @param symbol erstes Zeichen des Schlüssels.
	 * @return Schlüssel einer Eigenschaft.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist.
	 */
	String readKey(int symbol) throws IOException, IllegalArgumentException {
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
					symbol = this.readSymbol(source.read());
			}
			result.append((char)symbol);
			symbol = source.read();
		}
	}

	/**
	 * Diese Methode ließt den Wert einer Eigenschaft und gibt diesen zurück.
	 * 
	 * @return Wert einer Eigenschaft.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist.
	 */
	String readValue() throws IOException, IllegalArgumentException {
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
					symbol = this.readSymbol(source.read());
			}
			result.append((char)symbol);
		}
	}

	/**
	 * Diese Methode ließt den Namen eines Abschnitts und gibt diesen zurück.
	 * 
	 * @return Name eines Abschnitts.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist.
	 */
	String readSection() throws IOException, IllegalArgumentException {
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
					symbol = this.readSymbol(source.read());
			}
			result.append((char)symbol);
		}
	}

	/**
	 * Diese Methode gibt das Zeiche zurück, durch die Verkettung von {@code '\'} und dem gegebenen Zeichen maskiert wurde.
	 * 
	 * @param symbol Zeichen nach dem {@code '\'}.
	 * @return unmaskiertes Zeichen.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist.
	 */
	int readSymbol(final int symbol) throws IllegalArgumentException {
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

	/**
	 * Diese Methode ließt den Text eines Kommentars und gibt diesen zurück.
	 * 
	 * @return Text eines Kommentars.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist.
	 */
	String readComment() throws IOException {
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
					symbol = this.readSymbol(source.read());
			}
			result.append((char)symbol);
		}
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws IOException {
		this.reader.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.reader);
	}

}
