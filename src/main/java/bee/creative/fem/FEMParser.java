package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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

	/** Dieses Feld speichert die Parameternamen. */
	final List<String> params = new LinkedList<>();

	/** Diese Methode gibt die Auflistung aller {@link #putToken(Token) erfassten Bereiche} zurück.
	 *
	 * @return Bereichsliste. */
	public List<Token> tokens() {
		return this.tokens;
	}

	/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
	 *
	 * @return Parameternamen. */
	public List<String> params() {
		return this.params;
	}

	/** Diese Methode ersetzt den Bereich an der gegebenen Position in der {@link #tokens() Auflistung aller erfassten Bereiche} durch einen gleichwertigen mit
	 * dem gegebenen Bereichstyp und gibt {@code this} zurück. Wenn die Position negativ ist, wird {@code this} unverändert geliefert.
	 *
	 * @param index Position.
	 * @param type Typ des Bereichs.
	 * @return {@code this}. */
	public FEMParser setToken(final int index, final int type) {
		if (index < 0) return this;
		return this.setToken(index, this.tokens.get(index).withType(type));
	}

	/** Diese Methode ersetzt den Bereich an der gegebenen Position in der {@link #tokens() Auflistung aller erfassten Bereiche} durch den gegebenen und gibt
	 * {@code this} zurück. Wenn die Position negativ bzw. der Bereich {@code null} ist, wird {@code this} unverändert geliefert.
	 *
	 * @param index Position.
	 * @param token Bereich.
	 * @return {@code this}. */
	public FEMParser setToken(final int index, final Token token) {
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
	public int putToken(final int type) throws IllegalArgumentException {
		return this.putToken(type, this.index(), 1);
	}

	/** Diese Methode erfasst einen vor der {@link #index() aktuellen Position} endenden Bereich und gibt seine Position in der {@link #tokens() Auflistung aller
	 * erfassten Bereiche} zurück. Sie ist eine Abkürzung für {@code this.putToken(type, offset, this.index() - offset)}.
	 *
	 * @param type Typ des Bereichs.
	 * @param offset Startposition des Bereichs.
	 * @return Position des Bereichs oder {@code -1}.
	 * @throws IllegalArgumentException Wenn die Startposition ungültig ist. */
	public int putToken(final int type, final int offset) throws IllegalArgumentException {
		return this.putToken(type, offset, this.index() - offset);
	}

	/** Diese Methode ist eine Abkürzung für {@code this.putToken(new Token(type, offset, length))}.
	 *
	 * @param type Typ des Bereichs.
	 * @param offset Startposition des Bereichs.
	 * @param length Länge des Bereichs.
	 * @return Position des Bereichs oder {@code -1}.
	 * @throws IllegalArgumentException Wenn die Startposition ungültig ist. */
	public int putToken(final int type, final int offset, final int length) throws IllegalArgumentException {
		return this.putToken(new Token(type, offset, length));
	}

	/** Diese Methode erfasst den gegebenen {@link Token Bereich} und gibt seine Position in der {@link #tokens() Auflistung aller erfassten Bereiche} zurück.
	 * Wenn der Bereiche {@code null} oder {@link Token#length() leer} ist, wird {@code -1} geliefert.
	 *
	 * @see #tokens()
	 * @see #setToken(int, int)
	 * @param token Bereiche.
	 * @return Position des Bereichs oder {@code -1}. */
	public int putToken(final Token token) {
		if ((token == null) || (token.length() == 0)) return -1;
		final int result = this.tokens.size();
		this.tokens.add(result, token);
		return result;
	}

	/** Diese Methode setzt die Eingabe, ruft {@link #reset()} auf und gibt {@code this} zurück.
	 *
	 * @param source Eingabe.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
	public FEMParser useSource(final String source) throws NullPointerException {
		this.source(source);
		return this;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Parameternames in {@link #params()} zurück.
	 *
	 * @see List#indexOf(Object)
	 * @param name Parametername.
	 * @return Position oder {@code -1}.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist. */
	public int getParam(final String name) throws NullPointerException {
		return this.params.indexOf(name.toString());
	}

	/** Diese Methode fügt den gegebenen Parameternamen an der gegebenen Position in {@link #params()} ein und gibt {@code this} zurück.
	 *
	 * @see List#add(int, Object)
	 * @param index Einfügelosition.
	 * @param name Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code name} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code index} ungültig ist. */
	public FEMParser putParam(final int index, final String name) throws NullPointerException, IllegalArgumentException {
		this.params.add(index, name.toString());
		return this;
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public FEMParser useParams(final String... value) throws NullPointerException {
		return this.useParams(Arrays.asList(value));
	}

	/** Diese Methode setzt die initialen Parameternamen und gibt {@code this} zurück.
	 *
	 * @param value Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public FEMParser useParams(final List<String> value) throws NullPointerException {
		if (value.contains(null)) throw new NullPointerException();
		this.params.clear();
		this.params.addAll(value);
		return this;
	}

	/** Diese Methode entfernt die gegebene Anzahl der an Parameternamen ab dem BEginn der Liste aus {@link #params()} und gibt {@code this} zurück.
	 *
	 * @param count Anzahl.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl ungültig ist. */
	public FEMParser popParams(final int count) throws IllegalArgumentException {
		this.params.subList(0, count).clear();
		return this;
	}

}