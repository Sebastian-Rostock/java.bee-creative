package bee.creative.util;

/**
 * Diese Klasse implementiert ein Objekt zum Parsen der Zeichen eines {@link String}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class StringParser {

	/**
	 * Dieses Feld speichert die Eingabe.
	 */
	protected final String source;

	/**
	 * Dieses Feld speichert die Ausgabe.
	 */
	final StringBuffer target;

	/**
	 * Dieses Feld speichert die aktuelle Position.
	 */
	int index;

	/**
	 * Dieses Feld speichert die Anzahl der Zeichen.
	 */
	int length;

	/**
	 * Dieses Feld speichert das aktuelle Zeichen oder {@code -1}.
	 */
	int symbol;

	/**
	 * Dieser Konstruktor initialisiert die Eingabe.
	 * 
	 * @param source Eingabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public StringParser(final String source) throws NullPointerException {

		this.source = source;
		this.length = source.length();
		this.target = new StringBuffer();
		this.seek(0);
	}

	/**
	 * Diese Methode setzt die {@link #index() aktuelle Position} und gibt das das {@link #symbol() aktuelle Zeichen} oder {@code -1} zurück.
	 * 
	 * @see #index()
	 * @see #symbol()
	 * @param index Position.
	 * @return {@link #symbol() aktuelle Zeichen} oder {@code -1}.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist.
	 */
	public final int seek(int index) throws IndexOutOfBoundsException {
		if(index < 0) throw new IndexOutOfBoundsException("Index out of range: " + index);
		if(index < this.length) return this.symbol = this.source.codePointAt(this.index = index);
		this.index = this.length;
		return this.symbol = -1;
	}

	/**
	 * Diese Methode überspring das {@link #symbol() aktuelle Zeichen}, navigiert zum nächsten Zeichen und gibt dieses oder {@code -1} zurück.
	 * 
	 * @see #take()
	 * @see #index()
	 * @see #symbol()
	 * @see #string()
	 * @return {@link #symbol() aktuelle Zeichen} oder {@code -1}.
	 */
	public final int skip() {
		if(this.symbol < 0) return -1;
		final int index = this.index, symbol = index < this.length ? this.source.charAt(index) : -1;
		this.index = index + 1;
		this.symbol = symbol;
		return symbol;
	}

	/**
	 * Diese Methode übernimmt das {@link #symbol() aktuelle Zeichen} in die {@link #string() Ausgabe}, navigiert zum nächsten Zeichen und gibt dieses oder {@code -1} zurück.
	 * 
	 * @see #skip()
	 * @see #index()
	 * @see #symbol()
	 * @see #string()
	 * @return {@link #symbol() aktuelle Zeichen} oder {@code -1}.
	 */
	public final int take() {
		this.target.append((char)this.symbol);
		return this.skip();
	}

	/**
	 * Diese Methode übernimmt das gegebene Zeichen in die {@link #string() Ausgabe}.
	 * 
	 * @param symbol Zeichen.
	 * @see #take()
	 * @see #string()
	 */
	public final void take(final char symbol) {
		this.target.append(symbol);
	}

	/**
	 * Diese Methode leert die {@link #string() Ausgabe}.
	 * 
	 * @see #take()
	 * @see #string()
	 */
	public final void clear() {
		this.target.setLength(0);
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
	 * Diese Methode gibt die via {@link #take()} gesammelten Zeichen als {@link String} zurück.
	 * 
	 * @see #skip()
	 * @see #take()
	 * @see #clear()
	 * @see #symbol()
	 * @return Ausgabe.
	 */
	public final String string() {
		return this.target.toString();
	}

	/**
	 * Diese Methode gibt das aktuelle Zeichen oder {@code -1} zurück. Der Rückgabewert ist nur dann {@code -1}, wenn das Ende der Eingabe erreicht wurde.
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.source);
	}

}