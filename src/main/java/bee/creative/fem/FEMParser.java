package bee.creative.fem;

import java.util.ArrayList;
import java.util.List;
import bee.creative.fem.FEMScript.Token;
import bee.creative.util.Parser;

/** Diese Klasse implementiert den Parser, der eine Zeichenkette in einen aufbereiteten Quelltext überführt. Ein solcher Quelltext kann anschließend mit einem
 * {@link FEMCompiler} in Werte und Funktionen überführt werden.
 * <p>
 * Die Erzeugung von {@link Token Bereichen} erfolgt gemäß dieser Regeln:
 * <ul>
 * <li>Die Zeichen {@code '/'}, {@code '\''} und {@code '\"'} erzeugen je einen Bereich, der das entsprechende Zeichen als Bereichstyp verwendet, mit dem
 * Zeichen beginnt und endet sowie das Zeichen zwischen dem ersten und letzten nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der
 * Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.</li>
 * <li>Das Zeichen <code>'&lt;'</code> erzeugen einen Bereich, der mit dem Zeichen <code>'&gt;'</code> endet und beide Zeichen zwischen dem ersten und letzten
 * jeweils nur in Paaren enthalten darf. Wenn eine dieser Regeln verletzt wird, endet der Bereich an der Stelle des Fehlers und hat den Bereichstyp {@code '?'}.
 * Andernfalls hat er den Bereichstyp {@code '!'}.</li>
 * <li>Jedes der Zeichen {@code '$'}, {@code ';'}, {@code ':'}, {@code '('}, {@code ')'}, <code>'{'</code> und <code>'}'</code> erzeugt eine eigene Bereich, der
 * das entsprechende Zeichen als Bereichstyp verwendet.</li>
 * <li>Sequenzen aus Zeichen kleiner gleich dem Leerzeichen werden zu Bereichen mit dem Bereichstyp {@code '_'}.</li>
 * <li>Alle restlichen Zeichenfolgen werden zu Bereichen mit dem Bereichstyp {@code '.'}.</li>
 * </ul>
 * <p>
 * Die von {@link Parser} geerbten Methoden sollte nicht während der öffentlichen Methoden dieser Klasse aufgerufen werden.
 *
 * @see #parseRanges()
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMParser extends Parser {

	/** Dieses Feld speichert die bisher ermittelten Bereiche. */
	final List<Token> tokens = new ArrayList<>();

	{}

	public final List<Token> tokens() {
		return this.tokens;
	}

	public final void setToken(final int index, final int type) {
		if (index < 0) return;
		final Token range = this.tokens.get(index);
		this.tokens.set(index, new Token((char)type, range.offset, range.length));
	}

	/** Diese Methode erzeugt einen neuen Bereich ab der {@link #index() aktuellen Position} mit dem gegebenen Bereichstyp sowie der Länge {@code 1} und gibt
	 * dessen Position in {@link #tokens()} zurück. Sie ist eine Abkürzung für {@code this.putRange(type, this.index(), 1)}.
	 *
	 * @see #putToken(int, int, int)
	 * @param type Typ des Bereichs.
	 * @return Position des Bereichs oder {@code -1}. */
	public final int putToken(final int type) {
		return this.putToken(type, this.index(), 1);
	}

	/** Diese Methode fügt eine neue Bereich mit dem gegebenen Bereichstyp, der an der gegebenen Position Beginnt und vor der {@link #index() aktuellen Position}
	 * endet.
	 *
	 * @param type Typ des Bereichs.
	 * @param offset Start des Bereichs.
	 * @return Position des Bereichs oder {@code -1}. */
	public final int putToken(final int type, final int offset) {
		return this.putToken(type, offset, this.index() - offset);
	}

	public final int putToken(final int type, final int offset, final int length) {
		return this.putToken(new Token((char)type, offset, length));
	}

	public final int putToken(final Token range) {
		if ((range == null) || (range.length() == 0)) return -1;
		final int result = this.tokens.size();
		this.tokens.add(result, range);
		return result;
	}

	/** Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
	 *
	 * @param value Eingabe.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public final FEMParser useSource(final String value) throws NullPointerException {
		this.source(value);
		return this;
	}

}