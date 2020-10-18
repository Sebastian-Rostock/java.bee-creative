package bee.creative.fem;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterators;
import bee.creative.util.Parser.Token;

/** Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Abschnitten.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMScript implements Items<Token>, Iterable<Token> {

	/** Dieses Feld speichert den leeren Quelltext ohne Abschnitte. */
	public static final FEMScript EMPTY = new FEMScript(Token.EMPTY, new Token[0]);

	/** Diese Methode gibt einen aufbereiteten Quelltext mit den gegebenen Eigenschaften zurück.
	 *
	 * @see Token
	 * @param root Wurzelknoten.
	 * @param tokens Abschnitte.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code root} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält. */
	public static FEMScript from(final Token root, final Token... tokens) throws NullPointerException {
		return new FEMScript(Objects.notNull(root), tokens.clone());
	}

	/** Diese Methode gibt einen aufbereiteten Quelltext mit den gegebenen Eigenschaften zurück.
	 *
	 * @see Token
	 * @param root Wurzelknoten.
	 * @param tokens Abschnitte.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code root} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält. */
	public static FEMScript from(final Token root, final List<Token> tokens) throws NullPointerException, IllegalArgumentException {
		return FEMScript.from(root, tokens.toArray(new Token[tokens.size()]));
	}

	private final Token root;

	private final Token[] tokens;

	private FEMScript(final Token root, final Token[] tokens) {
		if (Arrays.asList(tokens).contains(null)) throw new NullPointerException();
		this.root = Objects.notNull(root);
		this.tokens = tokens;
	}

	/** Diese Methode gibt den Wurzelknoten der Abschnittshierarchie zurück. Der damit aufgespannte Abschnittsbaum verwendet die {@link #tokens() Abschnitte}
	 * dieses aufbereiteten Quelltexts als Blätter und verbindet diese zu sementischen Knoten.
	 *
	 * @return Wurzelknoten. */
	public Token root() {
		return this.root;
	}

	/** Diese Methode gibt die Anzahl der Abschnitte zurück.
	 *
	 * @see #get(int)
	 * @see #tokens()
	 * @see #iterator()
	 * @return Anzahl der Abschnitte. */
	public int size() {
		return this.tokens.length;
	}

	/** Diese Methode gibt die Verkettung der {@link Token#type() Abschnittstypen} der {@link #tokens() Abschnitte} als Zeichenkette zurück.
	 *
	 * @see Token#type()
	 * @see #tokens()
	 * @return Abschnittstypen als Zeichenkette. */
	public String types() {
		final int length = this.tokens.length;
		final char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = this.tokens[i].type();
		}
		return new String(result);
	}

	/** Diese Methode liefert die {@link Token#source() Eingabe} des {@link #root() Wurzelknoten}.
	 *
	 * @return Eingabezeichenkette. */
	public String source() {
		return this.root.source();
	}

	/** Diese Methode gibt eine Kopie der Abschnitte zurück.
	 *
	 * @see #get(int)
	 * @see #size()
	 * @see #iterator()
	 * @return Abschnitte. */
	public Token[] tokens() {
		return this.tokens.clone();
	}

	@Override
	public Token get(final int index) throws IndexOutOfBoundsException {
		return this.tokens[index];
	}

	@Override
	public Iterator<Token> iterator() {
		return Iterators.itemsIterator(this, 0, this.size());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.source()) ^ Objects.hash(this.root) ^ Objects.hash((Object[])this.tokens);
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMScript)) return false;
		final FEMScript that = (FEMScript)object;
		return Objects.equals(this.source(), that.source()) && Objects.equals(this.root, that.root) && Objects.equals(this.tokens, that.tokens);
	}

	/** Diese Methode liefert die {@link #source() Eingabezeichenkette}. */
	@Override
	public String toString() {
		return this.source();
	}

}