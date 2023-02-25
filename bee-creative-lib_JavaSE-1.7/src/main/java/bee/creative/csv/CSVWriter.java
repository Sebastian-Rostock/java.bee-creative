package bee.creative.csv;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import bee.creative.io.IO;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Formatter für Daten im {@code CSV}-Format. {@link #getQuote() Maskierungszeichen} und {@link #getComma() Trennzeichen}
 * können eingestellt werden. Als Zeilenende wird die Zeichekette {@code "\r\n"} genutzt.
 *
 * @see CSVReader
 * @author Sebastian Rostock 2014. */
public class CSVWriter implements Closeable, Flushable {

	/** Diese Methode erzeugt aus dem gegebenen Objekt einen {@link CSVWriter} und gibt diesen zurück. Wenn das Objekt ein {@link CSVWriter} ist, wird dieser
	 * geliefert. Andernfalls wird das Objekt in einen {@link Writer} {@link IO#outputWriterFrom(Object) überführt}.
	 *
	 * @see IO#outputWriterFrom(Object)
	 * @see CSVWriter#CSVWriter(Writer)
	 * @param data Objekt.
	 * @return {@link CSVWriter}.
	 * @throws IOException Wenn der {@link CSVWriter} nicht erzeugt werden kann. */
	public static CSVWriter from(final Object data) throws IOException {
		if (data instanceof CSVWriter) return (CSVWriter)data;
		return new CSVWriter(IO.outputWriterFrom(data));
	}

	/** Dieses Feld speichert die Zieldaten. */
	protected final Writer writer;

	/** Dieses Feld speichert den Maskierungszwang. */
	boolean force = true;

	/** Dieses Feld speichert das Maskierungszeichen. */
	char quote = '"';

	/** Dieses Feld speichert das Trennzeichen. */
	char comma = ';';

	/** Dieses Feld speichert nur dann {@code true}, wenn das {@link #getComma() Trennzeichen} vor dem {@link #writeValue(Object) nächsten geschriebenen Wert}
	 * ignoriert werden soll (Zeilenanfang). */
	boolean ignore = true;

	/** Dieser Konstruktor initialisiert die Ausgabe. Als {@link #getComma() Trennzeichen} wird {@code ';'} und als {@link #getQuote() Maskierungszeichen} wird
	 * {@code '"'} genutzt. Der {@link #getForce() Maskierungszwang} ist aktiviert. Die Methoden {@link #useForce(boolean)}, {@link #useQuote(char)},
	 * {@link #useComma(char)}, {@link #writeValue(Object)}, {@link #writeEntry(String...)} und {@link #writeTable(Object[][])} synchronisieren auf den gegebenen
	 * {@link Writer}.
	 *
	 * @param writer Ausgabe.
	 * @throws IOException Wenn {@link Writer#flush()} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code writer} {@code null} ist. */
	public CSVWriter(final Writer writer) throws IOException, NullPointerException {
		writer.flush();
		this.writer = writer;
	}

	/** Diese Methode gibt den Maskierungszwang zurück. Wenn dieser {@code true} ist, gelten alle {@link #writeValue(Object) Werte} als zu maskieren. Andernfalls
	 * gelten nur die {@link #writeValue(Object) Werte} als zu maskieren, in denen ein {@link #getComma() Trennzeichen}, ein {@link #getQuote()
	 * Maskierungszeichen} oder ein Zeilenumbruch vorkommt.
	 *
	 * @return Maskierungszwang. */
	public boolean getForce() {
		synchronized (this.writer) {
			return this.force;
		}
	}

	/** Diese Methode gibt das Maskierungszeichen zurück. Maskierte {@link #writeValue(Object) Werte} werden in diese Zeichen eingeschlossen und enthalten dieses
	 * Zeichen nur gedoppelt. Wenn das Zeichen {@code '\0'} ist, werden die {@link #writeValue(Object) Werte} nicht maskiert. Diese Einstellung aht Vorrang
	 * gegebnüber dem {@link #getForce() Maskierungszwang}.
	 *
	 * @see #getForce()
	 * @return Maskierungszeichen. */
	public char getQuote() {
		synchronized (this.writer) {
			return this.quote;
		}
	}

	/** Diese Methode gibt das Trennzeichen zurück. Dieses Zeichen steht zwischen den {@link #writeValue(Object) Werten} eines {@link #writeEntry(String...)
	 * Eintrags}.
	 *
	 * @return Trennzeichen. */
	public char getComma() {
		synchronized (this.writer) {
			return this.comma;
		}
	}

	/** Diese Methode setzt den {@link #getQuote() Maskierungszwang} und gibt {@code this} zurück.
	 *
	 * @param force Maskierungszwang.
	 * @return {@code this}. */
	public CSVWriter useForce(final boolean force) {
		synchronized (this.writer) {
			this.force = force;
		}
		return this;
	}

	/** Diese Methode setzt das {@link #getQuote() Maskierungszeichen} und gibt {@code this} zurück.
	 *
	 * @param quote Maskierungszeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Maskierungszeichen einem Zeilenumbruch gleicht. */
	public CSVWriter useQuote(final char quote) throws IllegalArgumentException {
		CSVReader.check(quote);
		synchronized (this.writer) {
			this.quote = quote;
		}
		return this;
	}

	/** Diese Methode setzt das {@link #getComma() Trennzeichen} und gibt {@code this} zurück.
	 *
	 * @param comma Trennzeichen.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn das Trennzeichen einem Zeilenumbruch gleicht. */
	public CSVWriter useComma(final char comma) throws IllegalArgumentException {
		CSVReader.check(comma);
		synchronized (this.writer) {
			this.comma = comma;
		}
		return this;
	}

	/** Diese Methode schreibt die gegebene Tabelle und gibt this zurück. Dabei werden die gegebenen {@link #writeEntry(String...) Einträge} in der gegebenen
	 * Reihenfolge ausgegeben.
	 *
	 * @see #writeEntry(Object...)
	 * @param entries Tabelle (Liste von Einträgen).
	 * @return {@code this}.
	 * @throws IOException Wenn {@link #writeEntry(String...)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code entries} {@code null} ist oder enthält. */
	public CSVWriter writeTable(final Object[][] entries) throws IOException {
		synchronized (this.writer) {
			this.writeTableImpl(entries);
		}
		return this;
	}

	void writeTableImpl(final Object[][] values) throws IOException {
		for (final Object[] value: values) {
			this.writeEntryImpl(value);
		}
	}

	/** Diese Methode schreibt den abschließenden Zeilenumbruch ({@code "\r\n"}) des aktuellen Eintrags und gibt {@code this} zurück.
	 *
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(String)} eine entsprechende Ausnahme auslöst. */
	public CSVWriter writeEntry() throws IOException {
		synchronized (this.writer) {
			this.writeEntryImpl();
		}
		return this;
	}

	void writeEntryImpl() throws IOException {
		this.writer.write("\r\n");
		this.ignore = true;
	}

	/** Diese Methode ist eine Abkürzung für {@code this.writeValue(values).writeEntry()}.
	 *
	 * @see #writeValue(Object...)
	 * @see #writeEntry()
	 * @param values Werte.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link #writeValue(String...)} bzw. {@link #writeEntry()} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public CSVWriter writeEntry(final Object... values) throws IOException, NullPointerException {
		synchronized (this.writer) {
			this.writeEntryImpl(values);
		}
		return this;
	}

	void writeEntryImpl(final Object[] values) throws IOException {
		this.writeValueImpl(values);
		this.writeEntryImpl();
	}

	/** Diese Methode schreibt den gegebenen Wert und gibt {@code this} zurück. Die Maskierung des Werts erfolgt nur dann, wenn das {@link #getQuote()
	 * Maskierungszeichen} nicht {@code '\0'} ist und der Wert aufgrund seines Inhalts oder des {@link #getForce() Maskierungszwangs} als zu maskieren gilt. Das
	 * {@link #getComma() Trennzeichen} wird außer beim ersten vor jedem Wert einer Zeile ausgegeben.
	 *
	 * @see #getForce()
	 * @see #getQuote()
	 * @see #getComma()
	 * @param value Wert, der über {@link Object#toString()} in seine Textdarstellung überführt wird.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(int)} bzw. {@link Writer#write(String)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public CSVWriter writeValue(final Object value) throws IOException, NullPointerException {
		synchronized (this.writer) {
			this.writeValueImpl(value.toString());
		}
		return this;
	}

	void writeValueImpl(final String value) throws IOException {
		final Writer target = this.writer;
		final char quote = this.quote;
		final char comma = this.comma;
		if (this.ignore) {
			this.ignore = false;
		} else {
			target.write(comma);
		}
		final int length = value.length();
		boolean mask = false;
		if (quote == 0) {
			mask = false;
		} else if (!this.force) {
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

	/** Diese Methode ist eine Abkürzung für {@code this.writeValue(values[0]).writeValue(values[1])...}.
	 *
	 * @see #writeValue(Object)
	 * @param values Werte.
	 * @return {@code this}.
	 * @throws IOException Wenn {@link Writer#write(int)} bzw. {@link Writer#write(String)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public CSVWriter writeValue(final Object... values) throws IOException, NullPointerException {
		synchronized (this.writer) {
			this.writeValueImpl(values);
		}
		return this;
	}

	void writeValueImpl(final Object... values) throws IOException, NullPointerException {
		for (final Object value: values) {
			this.writeValueImpl(value.toString());
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (this.writer) {
			this.writer.close();
		}
	}

	@Override
	public void flush() throws IOException {
		synchronized (this.writer) {
			this.writer.flush();
		}
	}

	@Override
	public String toString() {
		synchronized (this.writer) {
			return Objects.toInvokeString(this, this.force, this.quote, this.comma, this.writer);
		}
	}

}
