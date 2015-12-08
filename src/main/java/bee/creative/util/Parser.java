package bee.creative.util;

/**
 * Diese Klasse implementiert ein Objekt zum Parsen einer Zeichenkette.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Parser {

	/**
	 * Dieses Feld speichert die aktuelle Position.
	 */
	private int __index;

	/**
	 * Dieses Feld speichert die Zeichen der Eingabe.
	 */
	private char[] __chars;

	/**
	 * Dieses Feld speichert die Ausgabe.
	 */
	private final StringBuffer __target = new StringBuffer();

	/**
	 * Dieses Feld speichert das aktuelle Zeichen oder {@code -1}.
	 */
	private int __symbol;

	/**
	 * Dieses Feld speichert die Eingabe.
	 */
	private String __source;

	/**
	 * Dieses Feld speichert die Anzahl der Zeichen in der Eingabe.
	 */
	private int __length;

	/**
	 * Dieser Konstruktor initialisiert die leere Eingabe.
	 */
	public Parser() {
		this.source("");
	}

	/**
	 * Dieser Konstruktor initialisiert die Eingabe.
	 * 
	 * @param source Eingabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public Parser(final String source) throws NullPointerException {
		this.source(source);
	}

	{}

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
		if (index < 0) throw new IndexOutOfBoundsException();
		if (index < this.__length) return this.__symbol = this.__chars[this.__index = index];
		this.__index = this.__length;
		return this.__symbol = -1;
	}

	/**
	 * Diese Methode überspring das {@link #symbol() aktuelle Zeichen}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 * 
	 * @see #take()
	 * @see #index()
	 * @see #symbol()
	 * @see #target()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}.
	 */
	public final int skip() {
		final int index = this.__index + 1, symbol = index < this.__length ? this.__chars[index] : -1;
		this.__index = index;
		this.__symbol = symbol;
		return symbol;
	}

	/**
	 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code 0} zurück.
	 * 
	 * @see #seek(int)
	 */
	public final void reset() {
		this.seek(0);
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
		return this.__index;
	}

	/**
	 * Diese Methode gibt die Nummer des aktuellen Zeichens ({@code char}) oder {@code -1} zurück. Der Rückgabewert ist nur dann {@code -1}, wenn das Ende der
	 * {@link #source() Eingabe} erreicht wurde.
	 * 
	 * @see #skip()
	 * @see #take()
	 * @see #index()
	 * @return aktuelles Zeichen oder {@code -1}.
	 */
	public final int symbol() {
		return this.__symbol;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} gleich {@code 0} und damit am Anfang der {@link #source()
	 * Eingabe} ist.
	 * 
	 * @see #seek(int)
	 * @see #reset()
	 * @see #index()
	 * @return {@code true}, wenn die aktuelle Position minimal ist.
	 */
	public final boolean isReset() {
		return this.__index == 0;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn das {@link #symbol() aktuelle Zeichen} kleiner {@code 0} und damit die {@link #index() aktuelle
	 * Position} am Ende der {@link #source() Eingabe} ist.
	 * 
	 * @see #seek(int)
	 * @see #index()
	 * @see #length()
	 * @see #symbol()
	 * @return {@code true}, wenn die aktuelle Position maximal ist.
	 */
	public final boolean isParsed() {
		return this.__symbol < 0;
	}

	{}

	/**
	 * Diese Methode übernimmt das {@link #symbol() aktuelle Zeichen} in die {@link #target() Ausgabe}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 * Wenn sich die {@link #index() aktuelle Position} bereits am Ende der {@link #source() Eingabe} befindet, wird kein Zeichen in die Ausgabe übernommen.
	 * 
	 * @see #take(int)
	 * @see #skip()
	 * @see #index()
	 * @see #symbol()
	 * @see #target()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}.
	 */
	public final int take() {
		this.take(this.__symbol);
		return this.skip();
	}

	/**
	 * Diese Methode übernimmt das gegebene Zeichen in die {@link #target() Ausgabe}, sofern diese nicht negativ ist.
	 * 
	 * @see #take()
	 * @see #target()
	 * @param symbol Zeichen.
	 */
	public final void take(final int symbol) {
		if (symbol < 0) return;
		this.__target.append((char)symbol);
	}

	/**
	 * Diese Methode übernimmt die gegebene Zeichenkette in die {@link #target() Ausgabe}.
	 * 
	 * @param symbols Zeichenkette.
	 * @see #take()
	 * @see #target()
	 * @throws NullPointerException Wenn die Zeichenkette {@code null} ist.
	 */
	public final void take(final String symbols) throws NullPointerException {
		if (symbols == null) throw new NullPointerException("symbols = null");
		this.__target.append(symbols.toString());
	}

	/**
	 * Diese Methode leert die {@link #target() Ausgabe}.
	 * 
	 * @see #take()
	 * @see #take(int)
	 * @see #take(String)
	 * @see #target()
	 */
	public final void clear() {
		this.__target.setLength(0);
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
	public final String target() {
		return this.__target.toString();
	}

	/**
	 * Diese Methode setzt die Ausgabe.
	 * 
	 * @param value Ausgabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	protected void string(final String value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		this.__target.setLength(0);
		this.__target.append(value);
	}

	{}

	/**
	 * Diese Methode gibt die Länge der {@link #source() Eingabe} zurück.
	 * 
	 * @see #seek(int)
	 * @see #index()
	 * @see #source()
	 * @return Länge der Eingabe.
	 */
	public final int length() {
		return this.__length;
	}

	/**
	 * Diese Methode gibt die Eingabe zurück.
	 * 
	 * @return Eingabe.
	 */
	public final String source() {
		return this.__source;
	}

	/**
	 * Diese Methode setzt die Eingabe und ruft {@link #reset()} auf.
	 * 
	 * @param source Eingabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	protected void source(final String source) throws NullPointerException {
		if (source == null) throw new NullPointerException("source = null");
		this.__length = (this.__chars = (this.__source = source).toCharArray()).length;
		this.reset();
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.__source);
	}

}