package bee.creative.util;

/**
 * Diese Klasse implementiert ein Objekt zum Parsen eines Abschnitts einer Zeichenkette.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Parser {

	/**
	 * Dieses Feld speichert die aktuelle Position.
	 */
	private int index;

	/**
	 * Dieses Feld speichert die Ausgabe.
	 */
	private final StringBuffer target;

	/**
	 * Dieses Feld speichert das aktuelle Zeichen oder {@code -1}.
	 */
	private int symbol;

	/**
	 * Dieses Feld speichert die Eingabe.
	 */
	private final String source;

	/**
	 * Dieses Feld speichert den Beginn des Abschnitts, auf dem der {@link Parser} arbeitet.
	 */
	private final int offset;

	/**
	 * Dieses Feld speichert die Anzahl der Zeichen in den Abschnitt, auf dem der {@link Parser} arbeitet.
	 */
	private final int length;

	/**
	 * Dieser Konstruktor initialisiert die Eingabe auf dem dieser {@link Parser} arbeitet.
	 * 
	 * @param source Eingabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public Parser(final String source) throws NullPointerException {
		this(source, 0, source.length());
	}

	/**
	 * Dieser Konstruktor initialisiert die Eingabe sowie Beginn und Länge des Abschnitts, auf dem dieser {@link Parser} arbeitet.
	 * 
	 * @see #source()
	 * @see #offset()
	 * @see #length()
	 * @param source Eingabe.
	 * @param offset Beginn des Abschnitts
	 * @param length Länge des Abschnitts.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 * @throws IllegalArgumentException Wenn Beginn ider Länge ungültig sind.
	 */
	public Parser(final String source, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if(source == null) throw new NullPointerException();
		if((offset < 0) || (length < 0) || ((offset + length) > source.length())) throw new IllegalArgumentException();
		this.source = source;
		this.offset = offset;
		this.length = offset + length;
		this.target = new StringBuffer();
		this.reset();
	}

	/**
	 * Diese Methode setzt die {@link #index() aktuelle Position} und gibt das {@link #symbol() aktuelle Zeichen} zurück.
	 * 
	 * @see #index()
	 * @see #symbol()
	 * @param index Position.
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist.
	 */
	public final int seek(final int index) throws IndexOutOfBoundsException {
		if(index < this.offset) throw new IndexOutOfBoundsException("Index out of range: " + index);
		if(index < this.length) return this.symbol = this.source.codePointAt(this.index = index);
		this.index = this.length;
		return this.symbol = -1;
	}

	/**
	 * Diese Methode überspring das {@link #symbol() aktuelle Zeichen}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 * 
	 * @see #take()
	 * @see #index()
	 * @see #symbol()
	 * @see #string()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}.
	 */
	public final int skip() {
		final int index = this.index + 1, symbol = index < this.length ? this.source.charAt(index) : -1;
		this.index = index;
		this.symbol = symbol;
		return symbol;
	}

	/**
	 * Diese Methode übernimmt das {@link #symbol() aktuelle Zeichen} in die {@link #string() Ausgabe}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 * Wenn sich die {@link #index() aktuelle Position} bereits am Ende des zu bearbeitenden Bereichs der {@link #source() Eingabe} befindet, wird kein Zeichen in
	 * die Ausgabe übernommen.
	 * 
	 * @see #take(int)
	 * @see #skip()
	 * @see #index()
	 * @see #symbol()
	 * @see #string()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}.
	 */
	public final int take() {
		this.take(this.symbol);
		return this.skip();
	}

	/**
	 * Diese Methode übernimmt das gegebene Zeichen in die {@link #string() Ausgabe}, sofern diese nicht negativ ist.
	 * 
	 * @see #take()
	 * @see #string()
	 * @param symbol Zeichen.
	 */
	public final void take(final int symbol) {
		if(symbol < 0) return;
		this.target.append((char)symbol);
	}

	/**
	 * Diese Methode übernimmt die gegebene Zeichenkette in die {@link #string() Ausgabe}.
	 * 
	 * @param symbols Zeichenkette.
	 * @see #take()
	 * @see #string()
	 * @throws NullPointerException Wenn die Zeichenkette {@code null} ist.
	 */
	public final void take(final String symbols) throws NullPointerException {
		this.target.append(symbols.toString());
	}

	/**
	 * Diese Methode leert die {@link #string() Ausgabe}.
	 * 
	 * @see #take()
	 * @see #take(int)
	 * @see #take(String)
	 * @see #string()
	 */
	public final void clear() {
		this.target.setLength(0);
	}

	/**
	 * Diese Methode setzt die {@link #index() aktuelle Position} auf den {@link #offset() Anfang des Abschnitts} der {@link #source() Eingabe} zurück.
	 * 
	 * @see #seek(int)
	 * @see #offset()
	 */
	public final void reset() {
		this.seek(this.offset);
	}

	/**
	 * Diese Methode gibt die aktuelle Position zurück.
	 * 
	 * @see #skip()
	 * @see #take()
	 * @see #symbol()
	 * @return aktuelle Position.
	 */
	public final int index() {
		return this.index;
	}

	/**
	 * Diese Methode gibt die via {@link #take()}, {@link #take(int)} bzw. {@link #take(String)} gesammelten Zeichen als {@link String} zurück.
	 * 
	 * @see #skip()
	 * @see #take()
	 * @see #take(int)
	 * @see #take(String)
	 * @see #clear()
	 * @see #symbol()
	 * @return Ausgabe.
	 */
	public final String string() {
		return this.target.toString();
	}

	/**
	 * Diese Methode gibt den Beginn des Abschnitts der {@link #source() Eingabe} zurück, auf dem dieser {@link Parser} arbeitet.
	 * 
	 * @see #seek(int)
	 * @see #reset()
	 * @see #index()
	 * @see #length()
	 * @return Beginn des Abschnitts der Eingabe.
	 */
	public final int offset() {
		return this.offset;
	}

	/**
	 * Diese Methode gibt die Länge des Abschnitts der {@link #source() Eingabe} zurück, auf dem dieser {@link Parser} arbeitet.
	 * 
	 * @see #seek(int)
	 * @see #index()
	 * @see #offset()
	 * @see #source()
	 * @return Länge des Abschnitts der Eingabe.
	 */
	public final int length() {
		return this.length - this.offset;
	}

	/**
	 * Diese Methode gibt die Nummer des aktuellen Zeichens ({@code char}) oder {@code -1} zurück. Der Rückgabewert ist nur dann {@code -1}, wenn das Ende des
	 * Abschnitts der Eingabe erreicht wurde.
	 * 
	 * @see #skip()
	 * @see #take()
	 * @see #index()
	 * @return aktuelles Zeichen oder {@code -1}.
	 */
	public final int symbol() {
		return this.symbol;
	}

	/**
	 * Diese Methode gibt die Eingabe zurück.
	 * 
	 * @return Eingabe.
	 */
	public final String source() {
		return this.source;
	}

	/**
	 * Diese Methode gibt eine Zeichenkette mit dem Abschnitt der {@link #source() Eingabe} zurück, auf dem dieser {@link Parser} arbeitet.
	 * 
	 * @return Zeichenkette.
	 */
	public final String section() {
		return this.source.substring(this.offset, this.offset + this.length);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} am {@link #offset() Anfang} des zu bearbeitenden Abschnitts
	 * der {@link #source() Eingabe} ist.
	 * 
	 * @see #seek(int)
	 * @see #reset()
	 * @see #index()
	 * @see #offset()
	 * @return {@code true}, wenn die aktuelle Position minimal ist.
	 */
	public final boolean isReset() {
		return this.index == this.offset;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} am Ende des des zu bearbeitenden Abschnitts der
	 * {@link #source() Eingabe} und das {@link #symbol() aktuelle Zeichen} damit {@code -1} ist.
	 * 
	 * @see #seek(int)
	 * @see #index()
	 * @see #length()
	 * @see #symbol()
	 * @return {@code true}, wenn die aktuelle Position maximal ist.
	 */
	public final boolean isParsed() {
		return this.symbol < 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.source);
	}

}