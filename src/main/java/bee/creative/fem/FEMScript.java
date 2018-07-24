package bee.creative.fem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import bee.creative.fem.FEMScript.Token;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Comparators;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen aufbereiteten Quelltext als Zeichenkette mit typisierten Bereichen.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class FEMScript implements Items<Token>, Iterable<Token> {

	/** Diese Klasse implementiert ein Objekt, das einen typisierten Bereich einer Zeichenkette. Die Sortierung von Bereichen via {@link #compareTo(Token)}
	 * erfolgt gemäß ihrer Startposition.
	 *
	 * @see FEMScript
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Token implements Comparable<Token> {

		/** Dieses Feld speichert den leeren Bereich, dessen Komponenten alle {@code 0} sind. */
		public static final Token EMPTY = new Token((char)0, 0, 0);

		{}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Token#end() Endposition}en mit der gegebenen Position vergleicht.
		 * Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position
		 * kleiner, gleich bzw. größer der {@link Token#end() Endposition} eines gegebenen Bereichs ist.
		 *
		 * @see Token#end()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für das Ende von {@link Token}. */
		public static Comparable<Token> endingAt(final int index) {
			return new Comparable<Token>() {

				@Override
				public int compareTo(final Token value) {
					return Comparators.compare(index, value.offset + value.length);
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren {@link Token#start() Startposition}en mit der gegebenen Position
		 * vergleicht. Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene
		 * Position kleiner, gleich bzw. größer der {@link Token#start() Startposition} eines gegebenen Bereichs ist.
		 *
		 * @see Token#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen. */
		public static Comparable<Token> startingAt(final int index) {
			return new Comparable<Token>() {

				@Override
				public int compareTo(final Token value) {
					return Comparators.compare(index, value.offset);
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Bereiche zurück, welches deren Grenzen mit der gegebenen Position vergleicht. Der Rückhabewert der
		 * {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, größer oder gleich {@code 0}, wenn die gegebene Position kleiner der
		 * {@link Token#start() Startposition} ist, größer der {@link Token#end() Endposition} ist bzw. innerhalb der oder auf den Grenzen des Bereichs liegt.
		 *
		 * @see Token#end()
		 * @see Token#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Bereichen. */
		public static Comparable<Token> containing(final int index) {
			return new Comparable<Token>() {

				@Override
				public int compareTo(final Token value) {
					final int start = value.offset;
					return index < start ? -1 : index > (value.length + start) ? +1 : 0;
				}

			};
		}

		{}

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
		public Token(final int type, final int offset, final int length) throws IllegalArgumentException {
			if (length < 0) throw new IllegalArgumentException();
			this.type = (char)type;
			this.offset = offset;
			this.length = length;
		}

		{}

		/** Diese Methode gibt den Typ des Bereichs zurück.
		 *
		 * @return Bereichstyp. */
		public final char type() {
			return this.type;
		}

		/** Diese Methode gibt die Position zurück, vor der dieser Bereich endet.
		 *
		 * @return Endposition. */
		public final int end() {
			return this.offset + this.length;
		}

		/** Diese Methode gibt die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Startposition. */
		public final int start() {
			return this.offset;
		}

		/** Diese Methode gibt die Position zurück, die in der Mitte des dieses Bereichs liegt.
		 *
		 * @return Mittelposition. */
		public final int center() {
			return this.offset + (this.length / 2);
		}

		/** Diese Methode gibt die Länge des die Position zurück, an der dieser Bereich beginnt.
		 *
		 * @return Länge. */
		public final int length() {
			return this.length;
		}

		/** Diese Methode gibt diesen Bereich um den gegebenen Zeichenanzahl vergrößert zurück. Genauer wird die gegebene Anzahl von {@link #start()} subtrahiert
		 * und zu {@link #end()} addiert.
		 *
		 * @param count Zeichenanzahl zur symmentrischen Vergrößerung.
		 * @return vergrößerter Bereich.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Token inflate(final int count) throws IllegalArgumentException {
			return new Token(this.type, this.offset - count, this.length + (count << 1));
		}

		/** Diese Methode gibt den durch diesen Bereich beschriebenen Abschnitt der gegebenen Zeichenkette zurück.<br>
		 * Der Bereich wird dazu auf die gegebene Zeichenkette eingegrenzt.
		 *
		 * @param source Zeichenkette.
		 * @return Abschnitt der Zeichenkette.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public final String extract(final String source) throws NullPointerException {
			final int start = this.offset;
			return source.substring(Math.max(start, 0), Math.min(start + this.length, source.length()));
		}

		/** Diese Methode gibt diesen Bereich mit der gegebenen Endposition zurück. Die {@link #start() Startposition} bleibt erhalten.
		 *
		 * @param end Endposition.
		 * @return neuer Bereich mit der gegebenen Endposition.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Token withEnd(final int end) throws IllegalArgumentException {
			return new Token(this.type, this.offset, end - this.offset);
		}

		/** Diese Methode gibt diesen Bereich mit der gegebenen Startposition zurück. Die {@link #end() Endposition} bleibt erhalten.
		 *
		 * @param start Startposition.
		 * @return neuer Bereich mit der gegebenen Endposition.
		 * @throws IllegalArgumentException Wenn der resultierende Bereich eine negative Länge hätte. */
		public final Token withStart(final int start) throws IllegalArgumentException {
			return new Token(this.type, start, (this.offset + this.length) - start);
		}

		/** Diese Methode gibt diesen Bereich mit dem gegebenen Bereichstyp zurück.
		 *
		 * @param type Bereichstyp.
		 * @return neuer Bereich. */
		public final Token withType(final int type) {
			return new Token(type, this.offset, this.length);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final int compareTo(final Token value) {
			return Comparators.compare(this.offset, value.offset);
		}

		/** {@inheritDoc} */
		@Override
		public final int hashCode() {
			return this.type ^ this.offset ^ this.length;
		}

		/** {@inheritDoc} */
		@Override
		public final boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Token)) return false;
			final Token that = (Token)object;
			return (this.offset == that.offset) && (this.length == that.length) && (this.type == that.type);
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return "'" + this.type + "'@" + this.offset + "/" + this.length;
		}

	}

	{}

	/** Dieses Feld speichert den leeren Quelltext ohne Bereiche. */
	public static final FEMScript EMPTY = new FEMScript(0, "", new Token[0]);

	{}

	/** Diese Methode gibt einen aufbereiteten Quelltext mit den gegebenen Eigenschaften zurück.
	 *
	 * @see Token
	 * @param mode Quelltextmodus.
	 * @param source Zeichenkette.
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public static FEMScript from(final int mode, final String source, final Token... tokens) throws NullPointerException, IllegalArgumentException {
		FEMScript.checkToken(source, tokens);
		return new FEMScript(mode, source, tokens.clone());
	}

	/** Diese Methode gibt einen aufbereiteten Quelltext mit den gegebenen Eigenschaften zurück.
	 *
	 * @see Token
	 * @param mode Quelltextmodus.
	 * @param source Zeichenkette.
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public static FEMScript from(final int mode, final String source, final List<Token> tokens) throws NullPointerException, IllegalArgumentException {
		return FEMScript.from(mode, source, tokens.toArray(new Token[tokens.size()]));
	}

	@SuppressWarnings ("javadoc")
	static void checkSource(final String source, final Token[] tokens) throws NullPointerException, IllegalArgumentException {
		final int index = tokens.length - 1, length = source.length();
		if ((index >= 0) && (tokens[index].end() > length)) throw new IllegalArgumentException("tokens exceeding");
	}

	@SuppressWarnings ("javadoc")
	static void checkToken(final String source, final Token[] tokens) throws NullPointerException, IllegalArgumentException {
		int offset = 0;
		for (final Token token: tokens) {
			final int start = token.offset;
			if (start < offset) throw new IllegalArgumentException("tokens overlapping");
			offset = start + token.length;
		}
		FEMScript.checkSource(source, tokens);
	}

	{}

	/** Dieses Feld speichert den Quelltextmodus. */
	final int mode;

	/** Dieses Feld speichert die Zeichenkette. */
	final String source;

	/** Dieses Feld speichert die Bereiche. */
	final Token[] tokens;

	@SuppressWarnings ("javadoc")
	FEMScript(final int mode, final String source, final Token[] tokens) {
		this.mode = mode;
		this.source = source;
		this.tokens = tokens;
	}

	{}

	/** Diese Methode gibt die Verkettung der {@link Token#type() Typen} der {@link #tokens() Bereiche} als Zeichenkette zurück.
	 *
	 * @see Token#type()
	 * @see #tokens()
	 * @return Bereichstypen als Zeichenkette. */
	public final char[] types() {
		final int length = this.tokens.length;
		final char[] types = new char[length];
		for (int i = 0; i < length; i++) {
			types[i] = this.tokens[i].type;
		}
		return types;
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
	public final String source() {
		return this.source;
	}

	/** Diese Methode gibt eine Kopie der Bereiche zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return Bereiche. */
	public final Token[] tokens() {
		return this.tokens.clone();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn einer der Bereiche den gegebenen Bereichstyp trägt.
	 *
	 * @param type Bereichstyp.
	 * @return {@code true}, wenn ein Bereich mit dem gegebenen Bereichstyp enthalten ist. */
	public final boolean contains(final int type) {
		if ((type < 0) || (type > 65535)) return false;
		for (final Token token: this.tokens)
			if (token.type == type) return true;
		return false;
	}

	/** Diese Methode gibt die Anzahl der Bereiche zurück.
	 *
	 * @see #get(int)
	 * @see #tokens()
	 * @see #iterator()
	 * @return Anzahl der Bereiche. */
	public final int length() {
		return this.tokens.length;
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext in normalisierter Form zurück. In dieser gibt es keinen Abschnitt der {@link #source() Zeichenkette},
	 * der nicht in einem der {@link #tokens() Bereiche} enthalten ist.
	 *
	 * @return normalisierter Quelltext. */
	public final FEMScript normalize() {
		final List<Token> resultTokens = new ArrayList<>(this.tokens.length);
		final StringBuilder resultSource = new StringBuilder();
		int start = 0;
		for (final Token token: this.tokens) {
			final int length = token.length;
			resultSource.append(token.extract(this.source));
			resultTokens.add(new Token(token.type, start, length));
			start += length;
		}
		return FEMScript.from(this.mode, resultSource.toString(), resultTokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit dem gegebenen Quelltextmodus zurück.
	 *
	 * @see #mode()
	 * @param mode Quelltextmodus.
	 * @return aufbereiteten Quelltext. */
	public final FEMScript withMode(final int mode) {
		return new FEMScript(mode, this.source, this.tokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit der gegebenen Zeichenkette zurück.
	 *
	 * @see #source()
	 * @param source Zeichenkette.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code source} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Bereiche über die Zeichenkette hinaus gehen. */
	public final FEMScript withSource(final String source) throws NullPointerException, IllegalArgumentException {
		FEMScript.checkSource(source, this.tokens);
		return new FEMScript(this.mode, source, this.tokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit der gegebenen Bereichen zurück.
	 *
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public final FEMScript withTokens(final Token... tokens) throws NullPointerException, IllegalArgumentException {
		FEMScript.checkToken(this.source, tokens);
		return new FEMScript(this.mode, this.source, tokens);
	}

	/** Diese Methode gibt diesen aufbereiteten Quelltext mit der gegebenen Bereichen zurück.
	 *
	 * @param tokens Bereiche.
	 * @return aufbereiteten Quelltext.
	 * @throws NullPointerException Wenn {@code tokens} {@code null} ist oder enthält.
	 * @throws IllegalArgumentException Wenn die gegebenen Bereiche einander überlagern, nicht aufsteigend sortiert sind oder über die Zeichenkette hinaus
	 *         gehen. */
	public final FEMScript withTokens(final List<Token> tokens) throws NullPointerException, IllegalArgumentException {
		return this.withTokens(tokens.toArray(new Token[tokens.size()]));
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Bereich zurück. */
	@Override
	public final Token get(final int index) throws IndexOutOfBoundsException {
		return this.tokens[index];
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Token> iterator() {
		return Iterators.itemsIterator(this, 0, this.length());
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return Objects.hashPush(this.source.hashCode(), Objects.hash((Object[])this.tokens));
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMScript)) return false;
		final FEMScript that = (FEMScript)object;
		return this.source.equals(that.source) && Objects.equals(this.tokens, that.tokens);
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return this.source;
	}

}