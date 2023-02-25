package bee.creative.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import bee.creative.io.IO;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Parser für Daten im {@code CSV}-Format. {@link #getQuote() Maskierungszeichen} und {@link #getComma() Trennzeichen} können
 * eingestellt werden. Die Zeichen {@code '\r'} und {@code '\n'} werden außerhalb maskierter Werte immer als Ende einer Zeile und damit auch Ende eines
 * {@link #readEntry() Eintrags} erkannt. Das Ende der Eingabe gilt auch als Ende einer Zeile. Leere Zeilen werden ignoriert.
 *
 * @see CSVWriter
 * @author Sebastian Rostock 2014. */
public class CSVReader implements Closeable {

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link CSVReader} und gibt diesen zurück. Wenn das Objekt ein {@link CSVReader} ist, wird dieser
	 * geliefert. Andernfalls wird das Objekt in einen {@link Reader} {@link IO#inputReaderFrom(Object) überführt}.
	 *
	 * @see IO#inputReaderFrom(Object)
	 * @see CSVReader#CSVReader(Reader)
	 * @param data Objekt.
	 * @return {@link CSVReader}.
	 * @throws IOException Wenn der {@link CSVReader} nicht erzeugt werden kann. */
	public static CSVReader from(final Object data) throws IOException {
		if (data instanceof CSVReader) return (CSVReader)data;
		return new CSVReader(IO.inputReaderFrom(data));
	}

	static void check(final char symbol) throws IllegalArgumentException {
		if ((symbol == '\r') || (symbol == '\n')) throw new IllegalArgumentException();
	}

	/** Dieses Feld speichert die Quelldaten. */
	protected final Reader reader;

	/** Dieses Feld speichert den Puffer für die Werte. */
	final StringBuilder value;

	/** Dieses Feld speichert den Puffer für die Einträge. */
	final ArrayList<String> entry;

	/** Dieses Feld speichert das Maskierungszeichen. */
	char quote = '"';

	/** Dieses Feld speichert das Trennzeichen. */
	char comma = ';';

	/** Dieses Feld speichert das zuletzt gelesene Zeichen. */
	int symbol;

	/** Dieser Konstruktor initialisiert die Eingabe. Als {@link #getComma() Trennzeichen} wird {@code ';'} und als {@link #getQuote() Maskierungszeichen} wird
	 * {@code '"'} genutzt. Die Methoden {@link #useQuote(char)}, {@link #useComma(char)}, {@link #readValue()}, {@link #readEntry()} und {@link #readTable()}
	 * synchronisieren auf den gegebenen {@link Reader}.
	 *
	 * @param reader Eingabe.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code reader} {@code null} ist. */
	public CSVReader(final Reader reader) throws IOException, NullPointerException {
		this.reader = reader;
		this.value = new StringBuilder();
		this.entry = new ArrayList<>();
		// Leere Zeilen zu Beginn ignorieren.
		int symbol = reader.read();
		while ((symbol == '\r') || (symbol == '\n')) {
			symbol = reader.read();
		}
		this.symbol = symbol;
	}

	/** Diese Methode gibt das Maskierungszeichen zurück. Maskierte {@link #readValue() Werte} werden in diese Zeichen eingeschlossen und enthalten dieses Zeichen
	 * nur gedoppelt.
	 *
	 * @see #readValue()
	 * @return Maskierungszeichen. */
	public char getQuote() {
		synchronized (this.reader) {
			return this.quote;
		}
	}

	/** Diese Methode gibt das Trennzeichen zurück. Dieses Zeichen steht zwischen den {@link #readValue() Werten} eines {@link #readEntry() Eintrags}.
	 *
	 * @see #readValue()
	 * @return Trennzeichen. */
	public char getComma() {
		synchronized (this.reader) {
			return this.comma;
		}
	}

	/** Diese Methode setzt das {@link #getQuote() Maskierungszeichen} und gibt {@code this} zurück.
	 *
	 * @see #getQuote()
	 * @param quote Maskierungszeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Maskierungszeichen einem Zeilenumbruch gleicht. */
	public CSVReader useQuote(final char quote) throws IllegalArgumentException {
		CSVReader.check(quote);
		synchronized (this.reader) {
			this.quote = quote;
		}
		return this;
	}

	/** Diese Methode setzt das {@link #getComma() Trennzeichen} und gibt {@code this} zurück.
	 *
	 * @see #getComma()
	 * @param comma Trennzeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Trennzeichen einem Zeilenumbruch gleicht. */
	public CSVReader useComma(final char comma) throws IllegalArgumentException {
		CSVReader.check(comma);
		synchronized (this.reader) {
			this.comma = comma;
		}
		return this;
	}

	/** Diese Methode ließt alle Einträge als Tabelle und gibt diese zurück. Die gelieferte Tabelle besteht dabei aus den {@link #readEntry() nächsten Einträgen}
	 * bis zum Ende der Eingabe. Wenn es keinen weiteren Eintrag gibt, wird eine leere Tabelle geliefert.
	 *
	 * @see #readEntry()
	 * @return Tabelle (Liste von Einträgen).
	 * @throws IOException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst. */
	public String[][] readTable() throws IOException, IllegalArgumentException {
		synchronized (this.reader) {
			return this.readTableImpl();
		}
	}

	String[][] readTableImpl() throws IOException, IllegalArgumentException {
		final ArrayList<String[]> result = new ArrayList<>();
		while (true) {
			final String[] entry = this.readEntryImpl();
			if (entry == null) return result.toArray(new String[result.size()][]);
			result.add(entry);
		}
	}

	/** Diese Methode ließt den nächsten Eintrag und gibt ihn zurück. Der gelieferte Eintrag besteht dabei aus den {@link #readValue() nächsten Werten} bis zum
	 * Ende der Zeile. Wenn es keinen weiteren Eintrag gibt, wird {@code null} geliefert.
	 *
	 * @see #readValue()
	 * @return Eintrag (Liste von Werten).
	 * @throws IOException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst. */
	public String[] readEntry() throws IOException, IllegalArgumentException {
		synchronized (this.reader) {
			return this.readEntryImpl();
		}
	}

	String[] readEntryImpl() throws IOException, IllegalArgumentException {
		final ArrayList<String> result = this.entry;
		try {
			while (true) {
				final String value = this.readValueImpl();
				if (value == null) return result.isEmpty() ? null : result.toArray(new String[result.size()]);
				result.add(value);
			}
		} finally {
			result.clear();
		}
	}

	/** Diese Methode ließt den nächsten Wert und gibt ihn zurück. Wenn es auf der aktuellen Zeile keinen weiteren Wert gibt, wird {@code null} geliefert. Der
	 * nächste Wert nach {@code null} ist der erste Wert der nächsten Zeile. Wenn der Wert nicht in {@link #getQuote() Maskierungszeichen} eingeschlossen ist,
	 * endet er spätentens am Ende der Eingabe, am Ende der Zeile oder an einem {@link #getComma() Trennzeichen}. Andernfalls endet er nach dem ersten
	 * {@link #getQuote() Maskierungszeichen}, dem kein weiteres {@link #getQuote() Maskierungszeichen} folgt.
	 *
	 * @see #getQuote()
	 * @see #getComma()
	 * @return Wert oder {@code null}.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Maskierung des Werts nicth vor der Eingabe endet. */
	public String readValue() throws IOException, IllegalArgumentException {
		synchronized (this.reader) {
			return this.readValueImpl();
		}
	}

	String readValueImpl() throws IOException, IllegalArgumentException {
		final char quote = this.quote, comma = this.comma;
		final Reader reader = this.reader;
		final StringBuilder result = this.value;
		int symbol = this.symbol;
		try {
			// Zeilenende erkennen.
			while ((symbol == '\r') || (symbol == '\n')) {
				symbol = reader.read();
			}
			if (symbol != this.symbol) return null;
			// Maskierung erkennen.
			if (symbol == quote) {
				symbol = reader.read();
				while (symbol >= 0) {
					if (symbol == quote) {
						symbol = reader.read();
						if (symbol != quote) {
							if (symbol == comma) {
								symbol = reader.read();
							}
							return result.toString().intern();
						}
					}
					result.append((char)symbol);
					symbol = reader.read();
				}
				throw new IllegalArgumentException();
			}
			// Wertende erkennen.
			while ((symbol >= 0) && (symbol != comma) && (symbol != '\r') && (symbol != '\n')) {
				result.append((char)symbol);
				symbol = reader.read();
			}
			if (symbol == comma) {
				symbol = reader.read();
			}
			return result.toString().intern();
		} finally {
			this.symbol = symbol;
			result.setLength(0);
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
			return Objects.toInvokeString(this, this.quote, this.comma, this.reader);
		}
	}

}