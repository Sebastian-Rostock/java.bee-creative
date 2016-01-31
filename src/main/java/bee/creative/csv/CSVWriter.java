package bee.creative.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen Formatter für Daten im {@code CSV}-Format.<br>
 * {@link #getQuote() Maskierungszeichen} und {@link #getComma() Trennzeichen} können eingestellt werden.<br>
 * Als Zeilenende wird die Zeichekette {@code "\r\n"} genutzt.
 * 
 * @see CSVReader
 * @author Sebastian Rostock 2014.
 */
public final class CSVWriter implements Closeable {

	/**
	 * Dieses Feld speichert die Zieldaten.
	 */
	final Writer _writer_;

	/**
	 * Dieses Feld speichert den Maskierungszwang.
	 */
	boolean _force_ = true;

	/**
	 * Dieses Feld speichert das Maskierungszeichen.
	 */
	char _quote_ = '"';

	/**
	 * Dieses Feld speichert das Trennzeichen.
	 */
	char _comma_ = ';';

	/**
	 * Dieses Feld speichert nur dann {@code true}, wenn das {@link #getComma() Trennzeichen} vor dem {@link #writeValue(String) nächsten geschriebenen Wert}
	 * ignoriert werden soll (Zeilenanfang).
	 */
	boolean _ignore_ = true;

	/**
	 * Dieser Konstruktor initialisiert die Ausgabe.<br>
	 * Als {@link #getComma() Trennzeichen} wird {@code ';'} und als {@link #getQuote() Maskierungszeichen} wird {@code '"'} genutzt. Der {@link #getForce()
	 * Maskierungszwang} ist aktiviert.<br>
	 * Die Methoden {@link #useForce(boolean)}, {@link #useQuote(char)}, {@link #useComma(char)}, {@link #writeValue(String)}, {@link #writeEntry(String...)} und
	 * {@link #writeTable(String[][])} synchronisieren auf den gegebenen {@link Writer}.
	 * 
	 * @param writer Ausgabe.
	 * @throws IOException Wenn {@link Writer#flush()} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code writer} {@code null} ist.
	 */
	public CSVWriter(final Writer writer) throws IOException, NullPointerException {
		this._writer_ = writer;
		writer.flush();
	}

	{}

	/**
	 * Diese Methode gibt den Maskierungszwang zurück.<br>
	 * Wenn dieser {@code true} ist, gelten alle {@link #writeValue(String) Werte} als zu maskieren. Andernfalls gelten nur die {@link #writeValue(String) Werte}
	 * als zu maskieren, in denen ein {@link #getComma() Trennzeichen}, ein {@link #getQuote() Maskierungszeichen} oder ein Zeilenumbruch vorkommt.
	 * 
	 * @return Maskierungszwang.
	 */
	public final boolean getForce() {
		return this._force_;
	}

	/**
	 * Diese Methode gibt das Maskierungszeichen zurück.<br>
	 * Maskierte {@link #writeValue(String) Werte} werden in diese Zeichen eingeschlossen und enthalten dieses Zeichen nur gedoppelt.<br>
	 * Wenn das Zeichen {@code '\0'} ist, werden die {@link #writeValue(String) Werte} nicht maskiert. Diese Einstellung aht Vorrang gegebnüber dem
	 * {@link #getForce() Maskierungszwang}.
	 * 
	 * @see #getForce()
	 * @return Maskierungszeichen.
	 */
	public final char getQuote() {
		return this._quote_;
	}

	/**
	 * Diese Methode gibt das Trennzeichen zurück.<br>
	 * Dieses Zeichen steht zwischen den {@link #writeValue(String) Werten} eines {@link #writeEntry(String...) Eintrags}.
	 * 
	 * @return Trennzeichen.
	 */
	public final char getComma() {
		return this._comma_;
	}

	/**
	 * Diese Methode setzt den {@link #getQuote() Maskierungszwang} und gibt {@code this} zurück.
	 * 
	 * @param force Maskierungszwang.
	 * @return {@code this}.
	 */
	public final CSVWriter useForce(final boolean force) {
		synchronized (this._writer_) {
			this._force_ = force;
		}
		return this;
	}

	/**
	 * Diese Methode setzt das {@link #getQuote() Maskierungszeichen} und gibt {@code this} zurück.
	 * 
	 * @param quote Maskierungszeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Maskierungszeichen einem Zeilenumbruch gleicht.
	 */
	public final CSVWriter useQuote(final char quote) throws IllegalArgumentException {
		CSVReader._check_(quote);
		synchronized (this._writer_) {
			this._quote_ = quote;
		}
		return this;
	}

	/**
	 * Diese Methode setzt das {@link #getComma() Trennzeichen} und gibt {@code this} zurück.
	 * 
	 * @param comma Trennzeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Trennzeichen einem Zeilenumbruch gleicht.
	 */
	public final CSVWriter useComma(final char comma) throws IllegalArgumentException {
		CSVReader._check_(comma);
		synchronized (this._writer_) {
			this._comma_ = comma;
		}
		return this;
	}

	/**
	 * Diese Methode schreibt die gegebene Tabelle und gibt this zurück.<br>
	 * Dabei werden die gegebenen {@link #writeEntry(String...) Einträge} in der gegebenen Reihenfolge ausgegeben.
	 * 
	 * @see #writeEntry(String...)
	 * @param entries Tabelle (Liste von Einträgen).
	 * @return {@code this}.
	 * @throws IOException Wenn {@link #writeEntry(String...)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code entries} {@code null} ist oder enthält.
	 */
	public final CSVWriter writeTable(final String[][] entries) throws IOException {
		synchronized (this._writer_) {
			this._writeTable_(entries);
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void _writeTable_(final String[][] values) throws IOException {
		for (final String[] value: values) {
			this._writeEntry_(value);
		}
	}

	/**
	 * Diese Methode schreibt den gegebenen Eintrag und gibt this zurück.<br>
	 * Dabei werden zuerst die gegebenen {@link #writeValue(String) Werte} in der gegebenen Reihenfolge und anschließend ein Zeilenumbruch ({@code "\r\n"})
	 * ausgegeben.
	 * 
	 * @see #writeValue(String)
	 * @param values Eintrag (Liste von Werten).
	 * @return {@code this}.
	 * @throws IOException Wenn {@link #writeValue(String)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 */
	public final CSVWriter writeEntry(final String... values) throws IOException, NullPointerException {
		synchronized (this._writer_) {
			this._writeEntry_(values);
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void _writeEntry_(final String... values) throws IOException {
		for (final String value: values) {
			this._writeValue_(value);
		}
		this._writer_.write("\r\n");
		this._ignore_ = true;
	}

	/**
	 * Diese Methode schreibt den gegebenen Wert und gibt {@code this} zurück.<br>
	 * Die Maskierung des Werts erfolgt nur dann, wenn das {@link #getQuote() Maskierungszeichen} nicht {@code '\0'} ist und der Wert aufgrund seines Inhalts oder
	 * des {@link #getForce() Maskierungszwangs} als zu maskieren gilt.<br>
	 * Das {@link #getComma() Trennzeichen} wird außer beim ersten vor jedem Wert einer Zeile ausgegeben.
	 * 
	 * @see #getForce()
	 * @see #getQuote()
	 * @see #getComma()
	 * @param value Wert.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(int)} bzw. {@link Writer#write(String)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final CSVWriter writeValue(final String value) throws IOException, NullPointerException {
		synchronized (this._writer_) {
			this._writeValue_(value);
		}
		return this;
	}

	@SuppressWarnings ("javadoc")
	final void _writeValue_(final String value) throws IOException {
		final Writer target = this._writer_;
		final char quote = this._quote_;
		final char comma = this._comma_;
		if (this._ignore_) {
			this._ignore_ = false;
		} else {
			target.write(comma);
		}
		final int length = value.length();
		if (length == 0) return;
		boolean mask = false;
		if (quote == 0) {
			mask = false;
		} else if (!this._force_) {
			for (int i = 0; i < length; i++) {
				final char symbol = value.charAt(i);
				if ((symbol == quote) || (symbol == comma) || (symbol == '\r') || (symbol == '\n')) {
					mask = true;
					break;
				}
			}
		} else {
			mask = true;
		}
		if (mask) {
			target.write(quote);
			for (int i = 0; i < length; i++) {
				final char symbol = value.charAt(i);
				if (symbol == quote) {
					target.write(quote);
				}
				target.write(symbol);
			}
			target.write(quote);
		} else {
			target.write(value);
		}
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void close() throws IOException {
		this._writer_.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this._force_, this._quote_, this._comma_, this._writer_);
	}

}
