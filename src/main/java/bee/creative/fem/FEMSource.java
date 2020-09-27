package bee.creative.fem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.fem.FEMSource.Node;
import bee.creative.lang.Objects;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Comparators;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Bereichen.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMSource implements Items<Node>, Iterable<Node>, Emuable {

	/** Diese Klasse implementiert ein Objekt, das einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen über {@link #compareTo(Node)}
	 * erfolgt gemäß ihrer Startposition.
	 *
	 * @see FEMSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class Node implements Comparable<Node> {

		/** Dieses Feld speichert den leeren Bereich, dessen Komponenten alle {@code 0} sind. */
		public static final Node EMPTY = new Node((char)0, 0, 0);

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Node#end() Endposition}en mit der gegebenen Position vergleicht. Der
		 * Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position kleiner,
		 * gleich bzw. größer der {@link Node#end() Endposition} eines gegebenen Bereichs ist.
		 *
		 * @see Node#end()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für das Ende von {@link Node}. */
		public static Comparable<Node> endingAt(final int index) {
			return new Comparable<Node>() {

				@Override
				public int compareTo(final Node value) {
					return Comparators.compare(index, value.offset + value.length);
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Node#offset() Startposition}en mit der gegebenen Position
		 * vergleicht. Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene
		 * Position kleiner, gleich bzw. größer der {@link Node#offset() Startposition} eines gegebenen Bereichs ist.
		 *
		 * @see Node#offset()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen. */
		public static Comparable<Node> startingAt(final int index) {
			return new Comparable<Node>() {

				@Override
				public int compareTo(final Node value) {
					return Comparators.compare(index, value.offset);
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren Grenzen mit der gegebenen Position vergleicht. Der Rückhabewert der
		 * {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, größer oder gleich {@code 0}, wenn die gegebene Position kleiner der
		 * {@link Node#offset() Startposition} ist, größer der {@link Node#end() Endposition} ist bzw. innerhalb der oder auf den Grenzen des Bereichs liegt.
		 *
		 * @see Node#end()
		 * @see Node#offset()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen. */
		public static Comparable<Node> containing(final int index) {
			return new Comparable<Node>() {

				@Override
				public int compareTo(final Node value) {
					final int start = value.offset;
					return index < start ? -1 : index > (value.length + start) ? +1 : 0;
				}

			};
		}

		/** Dieses Feld speichert den Typ des Bereichs. */
		final char type;

		/** Dieses Feld speichert die Startposition. */
		final int offset;

		/** Dieses Feld speichert die Länge. */
		final int length;

		/** Dieser Konstruktor initialisiert Typ, Startposition und Länge.
		 *
		 * @param type Typ des Bereichs.
		 * @param offset Startposition des Bereichs.
		 * @param length Länge des Bereichs.
		 * @throws IllegalArgumentException Wenn die Länge negativ sind. */
		public Node(final int type, final int offset, final int length) throws IllegalArgumentException {
			if (length < 0) throw new IllegalArgumentException();
			this.type = (char)type;
			this.offset = offset;
			this.length = length;
		}

		FEMSource owner() {

		}

		Node[] elems() {

		}

		Node[] nodes() {

		}

		// oder null
		Node parent() {

		}

		/** Diese Methode gibt den Typ des Bereichs zurück.
		 *
		 * @return Bereichstyp. */
		public final char type() {
			return this.type;
		}

		/** Diese Methode gibt die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Startposition. */
		public final int offset() {
			return this.offset;
		}

		/** Diese Methode gibt die Position dieses Knoten in der zurück, die in der Mitte des dieses Bereichs liegt.
		 *
		 * @return Mittelposition. */
		public final int index() {
		}

		/** Diese Methode gibt die Länge des die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Länge. */
		public final int length() {
			return this.length;
		}

		/** Diese Methode gibt diesen Bereich um den gegebenen Zeichenanzahl vergrößert zurück. Genauer wird die gegebene Anzahl von {@link #offset()} subtrahiert
		 * und zu {@link #end()} addiert.
		 *
		 * @param count Zeichenanzahl zur symmentrischen Vergrößerung.
		 * @return vergrößerter Bereich.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Node inflate(final int count) throws IllegalArgumentException {
			return new Node(this.type, this.offset - count, this.length + (count << 1));
		}

		/** Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück. Der Bereich wird dazu auf die gegebene
		 * Zeichenkette eingegrenzt.
		 *
		 * @param source Zeichenkette.
		 * @return Abschnitt der Zeichenkette.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public final String extract(final String source) throws NullPointerException {
			final int start = this.offset;
			return source.substring(Math.max(start, 0), Math.min(start + this.length, source.length()));
		}

		/** Diese Methode gibt diesen Bereich mit der gegebenen Endposition zurück. Die {@link #offset() Startposition} bleibt erhalten.
		 *
		 * @param end Endposition.
		 * @return neuer Bereich mit der gegebenen Endposition.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Node withEnd(final int end) throws IllegalArgumentException {
			return new Node(this.type, this.offset, end - this.offset);
		}

		/** Diese Methode gibt diesen Bereich mit der gegebenen Startposition zurück. Die {@link #end() Endposition} bleibt erhalten.
		 *
		 * @param start Startposition.
		 * @return neuer Bereich mit der gegebenen Endposition.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Node withStart(final int start) throws IllegalArgumentException {
			return new Node(this.type, start, (this.offset + this.length) - start);
		}

		/** Diese Methode gibt diesen Bereich mit dem gegebenen Bereichstyp zurück.
		 *
		 * @param type Bereichstyp.
		 * @return neuer Bereich. */
		public final Node withType(final int type) {
			return new Node(type, this.offset, this.length);
		}

		@Override
		public final int compareTo(final Node value) {
			return Comparators.compare(this.offset, value.offset);
		}

		@Override
		public final int hashCode() {
			return this.type ^ this.offset ^ this.length;
		}

		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Node)) return false;
			final Node that = (Node)object;
			return (this.offset == that.offset) && (this.length == that.length) && (this.type == that.type);
		}

		@Override
		public final String toString() {
			return "'" + this.type + "'@" + this.offset + "/" + this.length;
		}

	}

	 static class Range  {
	
	
		final char type;
	
		/** Dieses Feld speichert die Startposition. */
		final int offset;
	
		/** Dieses Feld speichert die Länge. */
		final int length;
	
		/** Dieser Konstruktor initialisiert Typ, Startposition und Länge.
		 *
		 * @param type Typ des Bereichs.
		 * @param offset Startposition des Bereichs.
		 * @param length Länge des Bereichs.
		 * @throws IllegalArgumentException Wenn die Länge negativ sind. */
		public Node(final int type, final int offset, final int length) throws IllegalArgumentException {
			if (length < 0) throw new IllegalArgumentException();
			this.type = (char)type;
			this.offset = offset;
			this.length = length;
		}
	
		FEMSource owner() {
	
		}
	
		Node[] elems() {
	
		}
	
		Node[] nodes() {
	
		}
	
		// oder null
		Node parent() {
	
		}
	
		/** Diese Methode gibt den Typ des Bereichs zurück.
		 *
		 * @return Bereichstyp. */
		public final char type() {
			return this.type;
		}
	
		/** Diese Methode gibt die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Startposition. */
		public final int offset() {
			return this.offset;
		}
	
		/** Diese Methode gibt die Position dieses Knoten in der zurück, die in der Mitte des dieses Bereichs liegt.
		 *
		 * @return Mittelposition. */
		public final int index() {
		}
	
		/** Diese Methode gibt die Länge des die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Länge. */
		public final int length() {
			return this.length;
		}
	
		/** Diese Methode gibt diesen Bereich um den gegebenen Zeichenanzahl vergrößert zurück. Genauer wird die gegebene Anzahl von {@link #offset()} subtrahiert
		 * und zu {@link #end()} addiert.
		 *
		 * @param count Zeichenanzahl zur symmentrischen Vergrößerung.
		 * @return vergrößerter Bereich.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Node inflate(final int count) throws IllegalArgumentException {
			return new Node(this.type, this.offset - count, this.length + (count << 1));
		}
	
		/** Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück. Der Bereich wird dazu auf die gegebene
		 * Zeichenkette eingegrenzt.
		 *
		 * @param source Zeichenkette.
		 * @return Abschnitt der Zeichenkette.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public final String extract(final String source) throws NullPointerException {
			final int start = this.offset;
			return source.substring(Math.max(start, 0), Math.min(start + this.length, source.length()));
		}
	
		/** Diese Methode gibt diesen Bereich mit der gegebenen Endposition zurück. Die {@link #offset() Startposition} bleibt erhalten.
		 *
		 * @param end Endposition.
		 * @return neuer Bereich mit der gegebenen Endposition.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Node withEnd(final int end) throws IllegalArgumentException {
			return new Node(this.type, this.offset, end - this.offset);
		}
	
		/** Diese Methode gibt diesen Bereich mit der gegebenen Startposition zurück. Die {@link #end() Endposition} bleibt erhalten.
		 *
		 * @param start Startposition.
		 * @return neuer Bereich mit der gegebenen Endposition.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Node withStart(final int start) throws IllegalArgumentException {
			return new Node(this.type, start, (this.offset + this.length) - start);
		}
	
		/** Diese Methode gibt diesen Bereich mit dem gegebenen Bereichstyp zurück.
		 *
		 * @param type Bereichstyp.
		 * @return neuer Bereich. */
		public final Node withType(final int type) {
			return new Node(type, this.offset, this.length);
		}
	
		@Override
		public final int compareTo(final Node value) {
			return Comparators.compare(this.offset, value.offset);
		}
	
		@Override
		public final int hashCode() {
			return this.type ^ this.offset ^ this.length;
		}
	
		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Node)) return false;
			final Node that = (Node)object;
			return (this.offset == that.offset) && (this.length == that.length) && (this.type == that.type);
		}
	
		@Override
		public final String toString() {
			return "'" + this.type + "'@" + this.offset + "/" + this.length;
		}
	
	}

	/** Dieses Feld speichert den leeren Quelltext ohne Bereiche. */
	public static final FEMSource EMPTY = new FEMSource(0, "", new Node[0]);

	/** Diese Methode gibt einen aufbereiteten Quelltext mit den gegebenen Eigenschaften zurück.
	 *
	 * @see Node
	 * @param mode Quelltextmodus.
	 * @param source Zeichenkette.
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public static FEMSource from(final int mode, final String source, final Node... tokens) throws NullPointerException, IllegalArgumentException {
		FEMSource.checkToken(source, tokens);
		return new FEMSource(mode, source, tokens.clone());
	}

	/** Diese Methode gibt einen aufbereiteten Quelltext mit den gegebenen Eigenschaften zurück.
	 *
	 * @see Node
	 * @param mode Quelltextmodus.
	 * @param source Zeichenkette.
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public static FEMSource from(final int mode, final String source, final List<Node> tokens) throws NullPointerException, IllegalArgumentException {
		return FEMSource.from(mode, source, tokens.toArray(new Node[tokens.size()]));
	}

	static void checkSource(final String source, final Node[] tokens) throws NullPointerException, IllegalArgumentException {
		final int index = tokens.length - 1, length = source.length();
		if ((index >= 0) && (tokens[index].end() > length)) throw new IllegalArgumentException("tokens exceeding");
	}

	static void checkToken(final String source, final Node[] tokens) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		for (final Node token: tokens) {
			final int start = token.offset;
			if (start < offset) throw new IllegalArgumentException("tokens overlapping");
			offset = start + token.length;
		}
		FEMSource.checkSource(source, tokens);
	}

	/** Dieses Feld speichert den Quelltextmodus. */
	final int mode;

	/** Dieses Feld speichert die Zeichenkette. */
	final String string;

	/** Dieses Feld speichert die Bereiche. */
	final Node[] tokens;

	FEMSource(final int mode, final String source, final Node[] tokens) {
		this.mode = mode;
		this.string = source;
		this.tokens = tokens;
	}
 

	/** Diese Methode gibt die Kennung der Methode bzw. des Algorithmus zurück, durch welchen dieser Quelltext aufbereitet wurde.
	 *
	 * @return Quelltextmodus. */
	public final int mode() {
		return this.mode;
	}

	/** Diese Methode gibt die Zeichenkette des Quelltexts zurück.
	 *
	 * @return Zeichenkette. */
	public final String string() {
		return this.string;
	}

	/** Diese Methode gibt den Wurzelknoten zurück.
	 *
	 * @return Bereiche. */
	public final Node root() {
		return this.tokens[0];
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn einer der Bereiche den gegebenen Bereichstyp trägt.
	 *
	 * @param type Bereichstyp.
	 * @return {@code true}, wenn ein Bereich mit dem gegebenen Bereichstyp enthalten ist. */
	public final boolean contains(final int type) {
		if ((type < 0) || (type > 65535)) return false;
		for (final Node token: this.tokens)
			if (token.type == type) return true;
		return false;
	}

	/** Diese Methode gibt die Anzahl der Bereiche zurück.
	 *
	 * @see #get(int)
	 * @see #nodes()
	 * @see #iterator()
	 * @return Anzahl der Bereiche. */
	public final int length() {
		return this.tokens.length;
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #string() Zeichenkette},
	 * der nicht in einem der {@link #nodes() Bereiche} enthalten ist.
	 *
	 * @return normalisierter Quelltext. */
	public final FEMSource normalize() {
		final List<Node> resultTokens = new ArrayList<>(this.tokens.length);
		final StringBuilder resultSource = new StringBuilder();
		int start = 0;
		for (final Node token: this.tokens) {
			final int length = token.length;
			resultSource.append(token.extract(this.string));
			resultTokens.add(new Node(token.type, start, length));
			start += length;
		}
		return FEMSource.from(this.mode, resultSource.toString(), resultTokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit dem gegebenen Quelltextmodus zurück.
	 *
	 * @see #mode()
	 * @param mode Quelltextmodus.
	 * @return aufbereiteten Quelltext. */
	public final FEMSource withMode(final int mode) {
		return new FEMSource(mode, this.string, this.tokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit der gegebenen Zeichenkette zurück.
	 *
	 * @see #string()
	 * @param source Zeichenkette.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Bereiche über die Zeichenkette hinaus gehen. */
	public final FEMSource withSource(final String source) throws NullPointerException, IllegalArgumentException {
		FEMSource.checkSource(source, this.tokens);
		return new FEMSource(this.mode, source, this.tokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit der gegebenen Bereichen zurück.
	 *
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public final FEMSource withTokens(final Node... tokens) throws NullPointerException, IllegalArgumentException {
		FEMSource.checkToken(this.string, tokens);
		return new FEMSource(this.mode, this.string, tokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit der gegebenen Bereichen zurück.
	 *
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public final FEMSource withTokens(final List<Node> tokens) throws NullPointerException, IllegalArgumentException {
		return this.withTokens(tokens.toArray(new Node[tokens.size()]));
	}

	/** Diese Methode gibt den {@code index}-ten Bereich zurück. */
	@Override
	public final Node get(final int index) throws IndexOutOfBoundsException {
		return this.tokens[index];
	}

	@Override
	public long emu() {
		return EMU.fromObject(this) + EMU.fromArray(this.tokens) + EMU.fromAll((Object[])this.tokens);
	}

	@Override
	public final Iterator<Node> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	@Override
	public final int hashCode() {
		return Objects.hashPush(this.string.hashCode(), Objects.hash((Object[])this.tokens));
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMSource)) return false;
		final FEMSource that = (FEMSource)object;
		return this.string.equals(that.string) && Objects.equals(this.tokens, that.tokens);
	}

	@Override
	public final String toString() {
		return this.string;
	}

}