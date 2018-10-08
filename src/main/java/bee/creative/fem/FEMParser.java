package bee.creative.fem;

import java.util.ArrayList;
import java.util.List;
import bee.creative.fem.FEMScript.Token;
import bee.creative.util.Parser;

/** Diese Klasse implementiert den Parser zur Zerlegung einer Zeichenkette in {@link Token Bereiche} eines {@link FEMScript aufbereiteten Quelltexts}.
 *
 * @see FEMDomain#parseScript(String, int)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMParser extends Parser {

	/** Dieses Feld speichert die bisher ermittelten Bereiche. */
	final List<Token> tokens = new ArrayList<>();

	/** Diese Methode gibt die Auflistung aller {@link #putToken(Token) erfassten Bereiche} zurück.
	 *
	 * @return Bereichsliste. */
	public List<Token> tokens() {
		return this.tokens;
	}

	/** Diese Methode ersetzt den Bereich an der gegebenen Position in der {@link #tokens() Auflistung aller erfassten Bereiche} durch einen gleichwertigen mit
	 * dem gegebenen Bereichstyp und gibt {@code this} zurück. Wenn die Position negativ ist, wird {@code this} unverändert geliefert.
	 *
	 * @param index Position.
	 * @param type Typ des Bereichs.
	 * @return {@code this}. */
	public FEMParser setToken(int index, int type) {
		if (index < 0) return this;
		return this.setToken(index, this.tokens.get(index).withType(type));
	}

	/** Diese Methode ersetzt den Bereich an der gegebenen Position in der {@link #tokens() Auflistung aller erfassten Bereiche} durch den gegebenen und gibt
	 * {@code this} zurück. Wenn die Position negativ bzw. der Bereich {@code null} ist, wird {@code this} unverändert geliefert.
	 *
	 * @param index Position.
	 * @param token Bereich.
	 * @return {@code this}. */
	public FEMParser setToken(int index, Token token) {
		if ((index < 0) || (token == null)) return this;
		this.tokens.set(index, token);
		return this;
	}

	/** Diese Methode erfasst einen an der {@link #index() aktuellen Position} beginnenden Bereich der Länge {@code 1} und gibt seine Position in der
	 * {@link #tokens() Auflistung aller erfassten Bereiche} zurück. Sie ist eine Abkürzung für {@code this.putToken(type, this.index(), 1)}.
	 *
	 * @see #putToken(int, int, int)
	 * @param type Typ des Bereichs.
	 * @return Position des Bereichs oder {@code -1}.
	 * @throws IllegalArgumentException Wenn die Startposition ungültig ist. */
	public int putToken(int type) throws IllegalArgumentException {
		return this.putToken(type, this.index(), 1);
	}

	/** Diese Methode erfasst einen vor der {@link #index() aktuellen Position} endenden Bereich und gibt seine Position in der {@link #tokens() Auflistung aller
	 * erfassten Bereiche} zurück. Sie ist eine Abkürzung für {@code this.putToken(type, offset, this.index() - offset)}.
	 *
	 * @param type Typ des Bereichs.
	 * @param offset Startposition des Bereichs.
	 * @return Position des Bereichs oder {@code -1}.
	 * @throws IllegalArgumentException Wenn die Startposition ungültig ist. */
	public int putToken(int type, int offset) throws IllegalArgumentException {
		return this.putToken(type, offset, this.index() - offset);
	}

	/** Diese Methode ist eine Abkürzung für {@code this.putToken(new Token(type, offset, length))}.
	 *
	 * @param type Typ des Bereichs.
	 * @param offset Startposition des Bereichs.
	 * @param length Länge des Bereichs.
	 * @return Position des Bereichs oder {@code -1}.
	 * @throws IllegalArgumentException Wenn die Startposition ungültig ist. */
	public int putToken(int type, int offset, int length) throws IllegalArgumentException {
		return this.putToken(new Token(type, offset, length));
	}

	/** Diese Methode erfasst den gegebenen {@link Token Bereich} und gibt seine Position in der {@link #tokens() Auflistung aller erfassten Bereiche} zurück.
	 * Wenn der Bereiche {@code null} oder {@link Token#length() leer} ist, wird {@code -1} geliefert.
	 *
	 * @see #tokens()
	 * @see #setToken(int, int)
	 * @param token Bereiche.
	 * @return Position des Bereichs oder {@code -1}. */
	public int putToken(Token token) {
		if ((token == null) || (token.length() == 0)) return -1;
		int result = this.tokens.size();
		this.tokens.add(result, token);
		return result;
	}

	/** Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
	 *
	 * @param source Eingabe.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMParser useSource(String source) throws NullPointerException {
		this.source(source);
		return this;
	}

}