package bee.creative.fem;

import java.util.LinkedList;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.util.Parser;

/** Diese Klasse implementiert den Parser zur Zerlegung einer Zeichenkette in Abschnitte eines {@link FEMScript aufbereiteten Quelltexts} im Rahmen gegebener
 * {@link #params() Parameternamen}.
 *
 * @see FEMDomain#parseScript(String, int)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class FEMParser extends Parser {

	Token root = Token.EMPTY;

	/** Dieses Feld speichert die Parameternamen. */
	final LinkedList<String> params = new LinkedList<>();

	/** Dieser Konstruktor initialisiert die Eingabe.
	 *
	 * @param source Eingabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist. */
	public FEMParser(final String source) throws NullPointerException {
		super(source);
	}

	/** Diese Methode gibt den {@link #root(Token) erfassten Wurzelknoten} zurück.
	 *
	 * @return Wurzelknoten. */
	public Token root() {
		return this.root;
	}

	/** Diese Methode setzt den als {@link FEMScript#root()} einsetzbaren Wurzelknoten und gibt {@code this} zurück.
	 *
	 * @param root Wurzelknoten.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code root} {@code null} is. */
	public Parser root(final Token root) throws NullPointerException {
		this.root = Objects.notNull(root);
		return this;
	}

	/** Diese Methode gibt die Liste der aktellen Parameternamen zurück.
	 *
	 * @return Parameternamen. */
	public LinkedList<String> params() {
		return this.params;
	}

	/** Diese Methode setzt die Parameternamen und gibt {@code this} zurück.
	 *
	 * @param params Parameternamen.
	 * @return {@code this}.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist oder enthält.
	 * @throws IllegalStateException Wenn bereits eine Verarbeitung läuft. */
	public Parser params(final List<String> params) throws NullPointerException {
		if (params.contains(null)) throw new NullPointerException();
		this.params.clear();
		this.params.addAll(params);
		return this;
	}

	// TODO inline
	@Deprecated 
	public int get(final String name) throws NullPointerException {
		return this.params.indexOf(name.toString());
	}

	// TODO inline
	@Deprecated 
	public Parser put(final int index, final String name) throws NullPointerException, IllegalArgumentException {
		this.params.add(index, name.toString());
		return this;
	}

	// TODO inline
	@Deprecated 
	public Parser pop(final int count) throws IllegalArgumentException {
		this.params.subList(0, count).clear();
		return this;
	}

}