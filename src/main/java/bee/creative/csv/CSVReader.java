package bee.creative.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import bee.creative.util.IO;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Parser für Daten im {@code CSV}-Format.<br>
 * {@link #getQuote() Maskierungszeichen} und {@link #getComma() Trennzeichen} können eingestellt werden.<br>
 * Die Zeichen {@code '\r'} und {@code '\n'} werden außerhalb maskierter Werte immer als Ende einer Zeile und damit auch Ende eines {@link #readEntry()
 * Eintrags} erkannt. Das Ende der Eingabe gilt auch als Ende einer Zeile. Leere Zeilen werden ignoriert.
 *
 * @see CSVWriter
 * @author Sebastian Rostock 2014. */
public final class CSVReader implements Closeable {

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link CSVReader} und gibt diesen zurück.<br>
	 * Wenn das Objekt ein {@link CSVReader} ist, wird dieser geliefert. Andernfalls wird das Objekt in einen {@link Reader} {@link IO#inputReaderFrom(Object)
	 * überführt}.
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

	{}

	@SuppressWarnings ("javadoc")
	static void _check_(final char symbol) throws IllegalArgumentException {
		if ((symbol == '\r') || (symbol == '\n')) throw new IllegalArgumentException();
	}

	{}

	/** Dieses Feld speichert die Quelldaten. */
	final Reader _reader_;

	/** Dieses Feld speichert den Puffer für die Werte. */
	final StringBuilder _value_;

	/** Dieses Feld speichert den Puffer für die Einträge. */
	final ArrayList<String> _entry_;

	/** Dieses Feld speichert das Maskierungszeichen. */
	char _quote_ = '"';

	/** Dieses Feld speichert das Trennzeichen. */
	char _comma_ = ';';

	/** Dieses Feld speichert das zuletzt gelesene Zeichen. */
	int _symbol_;

	/** Dieser Konstruktor initialisiert die Eingabe.<br>
	 * Als {@link #getComma() Trennzeichen} wird {@code ';'} und als {@link #getQuote() Maskierungszeichen} wird {@code '"'} genutzt.<br>
	 * Die Methoden {@link #useQuote(char)}, {@link #useComma(char)}, {@link #readValue()}, {@link #readEntry()} und {@link #readTable()} synchronisieren auf den
	 * gegebenen {@link Reader}.
	 *
	 * @param reader Eingabe.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code reader} {@code null} ist. */
	public CSVReader(final Reader reader) throws IOException, NullPointerException {
		this._reader_ = reader;
		this._symbol_ = reader.read();
		this._value_ = new StringBuilder();
		this._entry_ = new ArrayList<>();
	}

	{}

	/** Diese Methode gibt das Maskierungszeichen zurück.<br>
	 * Maskierte {@link #readValue() Werte} werden in diese Zeichen eingeschlossen und enthalten dieses Zeichen nur gedoppelt.
	 *
	 * @see #readValue()
	 * @return Maskierungszeichen. */
	public final char getQuote() {
		return this._quote_;
	}

	/** Diese Methode gibt das Trennzeichen zurück.<br>
	 * Dieses Zeichen steht zwischen den {@link #readValue() Werten} eines {@link #readEntry() Eintrags}.
	 *
	 * @see #readValue()
	 * @return Trennzeichen. */
	public final char getComma() {
		return this._comma_;
	}

	/** Diese Methode setzt das {@link #getQuote() Maskierungszeichen} und gibt {@code this} zurück.
	 *
	 * @see #getQuote()
	 * @param quote Maskierungszeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Maskierungszeichen einem Zeilenumbruch gleicht. */
	public final CSVReader useQuote(final char quote) throws IllegalArgumentException {
		CSVReader._check_(quote);
		synchronized (this._reader_) {
			this._quote_ = quote;
		}
		return this;
	}

	/** Diese Methode setzt das {@link #getComma() Trennzeichen} und gibt {@code this} zurück.
	 *
	 * @see #getComma()
	 * @param comma Trennzeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Trennzeichen einem Zeilenumbruch gleicht. */
	public final CSVReader useComma(final char comma) throws IllegalArgumentException {
		CSVReader._check_(comma);
		synchronized (this._reader_) {
			this._comma_ = comma;
		}
		return this;
	}

	/** Diese Methode ließt alle Einträge als Tabelle und gibt diese zurück.<br>
	 * Die gelieferte Tabelle besteht dabei aus den {@link #readEntry() nächsten Einträgen} bis zum Ende der Eingabe.<br>
	 * Wenn es keinen weiteren Eintrag gibt, wird eine leere Tabelle geliefert.
	 *
	 * @see #readEntry()
	 * @return Tabelle (Liste von Einträgen).
	 * @throws IOException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst. */
	public final String[][] readTable() throws IOException, IllegalArgumentException {
		synchronized (this._reader_) {
			return this._readTable_();
		}
	}

	@SuppressWarnings ("javadoc")
	final String[][] _readTable_() throws IOException, IllegalArgumentException {
		final ArrayList<String[]> result = new ArrayList<>();
		while (true) {
			final String[] entry = this._readEntry_();
			if (entry == null) return result.toArray(new String[result.size()][]);
			result.add(entry);
		}
	}

	/** Diese Methode ließt den nächsten Eintrag und gibt ihn zurück.<br>
	 * Der gelieferte Eintrag besteht dabei aus den {@link #readValue() nächsten Werten} bis zum Ende der Zeile.<br>
	 * Wenn es keinen weiteren Eintrag gibt, wird {@code null} geliefert.
	 *
	 * @see #readValue()
	 * @return Eintrag (Liste von Werten).
	 * @throws IOException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link #readValue()} eine entsprechende Ausnahme auslöst. */
	public final String[] readEntry() throws IOException, IllegalArgumentException {
		synchronized (this._reader_) {
			return this._readEntry_();
		}
	}

	@SuppressWarnings ("javadoc")
	final String[] _readEntry_() throws IOException, IllegalArgumentException {
		final Reader reader = this._reader_;
		int symbol = this._symbol_;
		if (symbol < 0) return null;
		final ArrayList<String> result = this._entry_;
		try {
			result.add(this._readValue_());
			final char comma = this._comma_;
			symbol = this._symbol_;
			while (symbol == comma) {
				this._symbol_ = reader.read();
				result.add(this._readValue_());
				symbol = this._symbol_;
			}
			while ((symbol == '\r') || (symbol == '\n')) {
				symbol = reader.read();
			}
			return result.toArray(new String[result.size()]);
		} finally {
			this._symbol_ = symbol;
			result.clear();
		}
	}

	/** Diese Methode ließt den nächsten Wert und gibt ihn zurück.<br>
	 * Wenn es auf der aktuellen Zeile keinen weiteren Wert gibt, wird {@code ""} geliefert. Wenn der Wert nicht in {@link #getQuote() Maskierungszeichen}
	 * eingeschlossen ist, endet er spätentens am Ende der Eingabe, am Ende der Zeile oder an einem {@link #getComma() Trennzeichen}. Andernfalls endet er nach
	 * dem ersten {@link #getQuote() Maskierungszeichen}, dem kein weiteres {@link #getComma() Maskierungszeichen} folgt.<br>
	 *
	 * @see #getQuote()
	 * @see #getComma()
	 * @return Wert oder {@code null}.
	 * @throws IOException Wenn {@link Reader#read()} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn die Maskierung des Werts nicth vor der Eingabe endet. */
	public final String readValue() throws IOException, IllegalArgumentException {
		synchronized (this._value_) {
			return this._readValue_();
		}
	}

	@SuppressWarnings ("javadoc")
	final String _readValue_() throws IOException, IllegalArgumentException {
		final Reader reader = this._reader_;
		final StringBuilder result = this._value_;
		int symbol = this._symbol_;
		try {
			final char quote = this._quote_;
			if (symbol == quote) {
				symbol = reader.read();
				while (symbol >= 0) {
					if (symbol == quote) {
						symbol = reader.read();
						if (symbol != quote) return result.toString().intern();
						result.append(quote);
					} else {
						result.append((char)symbol);
					}
					symbol = reader.read();
				}
				throw new IllegalArgumentException();
			} else {
				final char comma = this._comma_;
				while ((symbol >= 0) && (symbol != comma) && (symbol != '\r') && (symbol != '\n')) {
					result.append((char)symbol);
					symbol = reader.read();
				}
			}
			return result.toString().intern();
		} finally {
			this._symbol_ = symbol;
			result.setLength(0);
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
	public String toString() {
		return Objects.toInvokeString(this, this._quote_, this._comma_, this._reader_);
	}

}