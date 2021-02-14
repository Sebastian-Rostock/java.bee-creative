package bee.creative.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import bee.creative.array.CompactIntegerArray;
import bee.creative.lang.Objects;
import bee.creative.util.Comparables.Items;

/** Diese Klasse implementiert ein Objekt zum Parsen einer Zeichenkette in hierarchische {@link Token Abschnitte}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Parser {

	/** Diese Klasse implementiert einen {@link #type() typisierten} Abschnitt einer {@link #source() Zeichenkette}. Die {@link #compareTo(Token) Ordnung} von
	 * Abschnitten folgt aus ihrer {@link #start() Startposition}. Abschnitte können zudem über {@link #tokens() untergeordnete Kindabschnitte} einen
	 * syntaktischen Baum bilden und einen beliebigen {@link #value() Wert} besitzen.
	 * <p>
	 * Die Methoden {@link #get(int)}, {@link #size()}, {@link #tokens()} und {@link #iterator()} beziehen sich auf die Kindabschnitte. Die Methoden
	 * {@link #start()}, {@link #end()}, {@link #length()}, {@link #hashCode()} und {@link #equals(Object)} beziehen sich dagegen nur auf Lage und Größe des
	 * Abschnitts.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Token implements Items<Token>, Iterable<Token>, Comparable<Token> {

		/** Dieses Feld speichert den leeren Abschnitt. */
		public static final Token EMPTY = new Token("", 0, 0, new Token[0]);

		/** Diese Methode gibt einen neuen Abschnitt mit den gegebenen Eigenschaften zurück.
		 *
		 * @param source Eingabe, deren Abschnitt beschrieben wird.
		 * @param offset Abschnittsbeginn.
		 * @param length Abschnittslänge.
		 * @param tokens Kindabschnitte.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} bzw. {@code tokens} {@code null} ist oder enthält.
		 * @throws IllegalArgumentException Wenn der Abschnitt außerhalb der Eingabe liegt oder seine Länge negativ ist. */
		public static Token from(final String source, final int offset, final int length, final Token... tokens)
			throws NullPointerException, IllegalArgumentException {
			return new Token(source, offset, length, tokens.clone());
		}

		/** Diese Methode gibt einen neuen Abschnitt mit den gegebenen Eigenschaften zurück.
		 *
		 * @param source Eingabe, deren Abschnitt beschrieben wird.
		 * @param offset Abschnittsbeginn.
		 * @param length Abschnittslänge.
		 * @param type Abschnittstyp.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der Abschnitt außerhalb der Eingabe liegt oder seine Länge negativ ist. */
		public static Token from(final String source, final int offset, final int length, final int type) throws NullPointerException, IllegalArgumentException {
			return Token.from(source, offset, length, Token.EMPTY.tokens).type(type);
		}

		/** Diese Methode gibt einen neuen Abschnitt mit den gegebenen Eigenschaften zurück.
		 *
		 * @param source Eingabe, deren Abschnitt beschrieben wird.
		 * @param offset Abschnittsbeginn.
		 * @param length Abschnittslänge.
		 * @param type Abschnittstyp.
		 * @param tokens Kindabschnitte.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} bzw. {@code tokens} {@code null} ist oder enthält.
		 * @throws IllegalArgumentException Wenn der Abschnitt außerhalb der Eingabe liegt oder seine Länge negativ ist. */
		public static Token from(final String source, final int offset, final int length, final int type, final Token... tokens)
			throws NullPointerException, IllegalArgumentException {
			return Token.from(source, offset, length, tokens).type(type);
		}

		/** Diese Methode gibt einen neuen Abschnitt mit den gegebenen Eigenschaften zurück.
		 *
		 * @param source Eingabe, deren Abschnitt beschrieben wird.
		 * @param offset Abschnittsbeginn.
		 * @param length Abschnittslänge.
		 * @param type Abschnittstyp.
		 * @param tokens Kindabschnitte.
		 * @return Abschnitt.
		 * @throws NullPointerException Wenn {@code source} bzw. {@code tokens} {@code null} ist oder enthält.
		 * @throws IllegalArgumentException Wenn der Abschnitt außerhalb der Eingabe liegt oder seine Länge negativ ist. */
		public static Token from(final String source, final int offset, final int length, final int type, final Iterable<Token> tokens)
			throws NullPointerException, IllegalArgumentException {
			return Token.from(source, offset, length, Iterables.toArray(tokens, Token.EMPTY.tokens)).type(type);
		}

		/** Diese Methode gibt ein {@link Comparable} für Abschnitt zurück, welches deren {@link Token#end() Endposition} mit der gegebenen Position vergleicht. Der
		 * Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene Position kleiner,
		 * gleich bzw. größer der {@link Token#end() Endposition} eines gegebenen Abschnitts ist.
		 *
		 * @see Token#end()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Endpositionen von Abschnitten. */
		public static Comparable2<Token> endingAt(final int index) {
			return new AbstractComparable<Token>() {

				@Override
				public int compareTo(final Token value) {
					return Comparators.compare(index, value.end());
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Abschnitte zurück, welches deren {@link Token#start() Startposition} mit der gegebenen Position
		 * vergleicht. Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene
		 * Position kleiner, gleich bzw. größer der {@link Token#start() Startposition} eines gegebenen Abschnitts ist.
		 *
		 * @see Token#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Abschnitten. */
		public static Comparable2<Token> startingAt(final int index) {
			return new AbstractComparable<Token>() {

				@Override
				public int compareTo(final Token value) {
					return Comparators.compare(index, value.start());
				}

			};
		}

		/** Diese Methode gibt ein {@link Comparable} für Abschnitte zurück, welches deren Grenzen mit der gegebenen Position vergleicht. Der Rückhabewert der
		 * {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, größer oder gleich {@code 0}, wenn die gegebene Position kleiner der
		 * {@link Token#start() Startposition} ist, größer oder gleich der {@link Token#end() Endposition} ist bzw. innerhalb des Abschnitts liegt.
		 *
		 * @see Token#end()
		 * @see Token#start()
		 * @see Comparables
		 * @see Comparators#compare(int, int)
		 * @param index Position.
		 * @return {@link Comparable} für Startposition von Abschnitten. */
		public static Comparable2<Token> containing(final int index) {
			return new AbstractComparable<Token>() {

				@Override
				public int compareTo(final Token value) {
					return index < value.start() ? -1 : index < value.end() ? 0 : +1;
				}

			};
		}

		/** Dieses Feld speichert die Eingabe. */
		private final String source;

		/** Dieses Feld speichert den Abschnittsbeginn. */
		private final int offset;

		/** Dieses Feld speichert die Abschnittslänge. */
		private final int length;

		/** Dieses Feld speichert die Kindabschnitte. */
		private final Token[] tokens;

		/** Dieses Feld speichert den Abschnittswert. */
		private Object value;

		/** Dieses Feld speichert den Abschnittstyp. */
		private char type;

		private Token(final String source, final int offset, final int length, final Token[] tokens) throws NullPointerException, IllegalArgumentException {
			if ((offset < 0) || (length < 0) || (source.length() < (offset + length))) throw new IllegalArgumentException();
			if (Arrays.asList(tokens).contains(null)) throw new NullPointerException();
			this.source = Objects.notNull(source);
			this.offset = offset;
			this.length = length;
			this.tokens = tokens;
			Arrays.sort(tokens);
		}

		/** Diese Methode gibt den Typ des Abschnitts zurück.
		 *
		 * @return Abschnittstyp. */
		public char type() {
			return this.type;
		}

		/** Diese Methode setzt den {@link #type() Abschnittstyp} und gibt this zurück.
		 *
		 * @param type Abschnittstyp.
		 * @return {@code this}. */
		public Token type(final int type) {
			this.type = (char)type;
			return this;
		}

		/** Diese Methode die Anzahl der {@link #tokens() Kindabschnitte} zurück.
		 *
		 * @return Kindabschnittanzahl. */
		public int size() {
			return this.tokens.length;
		}

		public int find(final int index) {
			return this.find(Token.containing(index));
		}

		public int find(final Comparable2<Token> comp) {
			return Comparables.binarySearch(this, comp, 0, this.size());
		}

		/** Diese Methode liefet die Position des ersten Zeichens nach dem Abschnitt.
		 *
		 * @see #start()
		 * @see #length()
		 * @return Endposition. */
		public int end() {
			return this.offset + this.length;
		}

		/** Diese Methode liefet die Position des ersten Zeichens im Abschnitt.
		 *
		 * @see #end()
		 * @see #length()
		 * @return Startposition. */
		public int start() {
			return this.offset;
		}

		/** Diese Methode gibt den Wert des Abschnitts zurück. Dieser kann bspw. das Ergebnis der Interpretation dieses Abschnitts enthalten.
		 *
		 * @return Abschnittswert. */
		public Object value() {
			return this.value;
		}

		/** Diese Methode setzt den {@link #value() Abschnittswert} und gibt {@code this} zurück.
		 *
		 * @param value Abschnittswert.
		 * @return {@code this}. */
		public Token value(final Object value) {
			this.value = value;
			return this;
		}

		/** Diese Methode gibt die Kindabschnitte zurück.
		 *
		 * @return Kindabschnitte. */
		public Token[] tokens() {
			return this.tokens.clone();
		}

		/** Diese Methode gibt die Länge des Abschnitts zurück.
		 *
		 * @see #end()
		 * @see #start()
		 * @return Abschnittslänge. */
		public int length() {
			return this.length;
		}

		/** Diese Methode gibt die Zeichenkette zurück, die als Eingabe des Parsert eingesetzt wurde und auf welche sich die Positionsangaben dieses Abschnitts
		 * beziehen.
		 *
		 * @return Eingabezeichenkette. */
		public String source() {
			return this.source;
		}

		@Override
		public Token get(final int index) throws IndexOutOfBoundsException {
			return this.tokens[index];
		}

		@Override
		public Iterator<Token> iterator() {
			return Iterators.fromItems(this, 0, this.size());
		}

		@Override
		public int compareTo(final Token value) {
			return Comparators.compare(this.offset, value.offset);
		}

		@Override
		public int hashCode() {
			return Objects.hashPush(Objects.hashPush(Objects.hashPush(Objects.hashInit(), this.type), this.offset), this.length);
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Token)) return false;
			final Token that = (Token)object;
			return (this.type == that.type) && (this.offset == that.offset) && (this.length == that.length);
		}

		/** Diese Methode liefert die Zeichenkette innerhalb des Abschnitt.
		 *
		 * @see #end()
		 * @see #start()
		 * @see #source()
		 * @see String#substring(int, int) */
		@Override
		public String toString() {
			return this.source.substring(this.start(), this.end());
		}

	}

	/** Diese Klasse implementiert das Ergebnis der Übersetzung einer Zeichenkett in eine Hierarchie typisierter Abschnitte.
	 *
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Result implements Items<Token>, Iterable<Token> {

		/** Dieses Feld speichert das leere Ergebnis ohne Abschnitte. */
		public static final Result EMPTY = new Result(Token.EMPTY, new Token[0]);

		/** Diese Methode gibt ein Ergebnis mit den gegebenen Eigenschaften zurück.
		 *
		 * @see Token
		 * @param root Wurzelknoten.
		 * @param tokens Abschnittsliste.
		 * @return Ergebnis.
		 * @throws NullPointerException Wenn {@code root} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält. */
		public static Result from(final Token root, final Token... tokens) throws NullPointerException {
			return new Result(Objects.notNull(root), tokens.clone());
		}

		/** Diese Methode gibt ein Ergebnis mit den gegebenen Eigenschaften zurück.
		 *
		 * @see Token
		 * @param root Wurzelknoten.
		 * @param tokens Abschnittsliste.
		 * @return Ergebnis.
		 * @throws NullPointerException Wenn {@code root} {@code null} ist bzw. {@code tokens} {@code null} ist oder enthält. */
		public static Result from(final Token root, final List<Token> tokens) throws NullPointerException, IllegalArgumentException {
			return Result.from(root, tokens.toArray(new Token[tokens.size()]));
		}

		private final Token root;

		private final Token[] tokens;

		private Result(final Token root, final Token[] tokens) {
			if (Arrays.asList(tokens).contains(null)) throw new NullPointerException();
			this.root = Objects.notNull(root);
			this.tokens = tokens;
			Arrays.sort(tokens);
		}

		/** Diese Methode gibt den Wurzelknoten der Abschnittshierarchie zurück. Der damit aufgespannte Abschnittsbaum verwendet die {@link #tokens() Abschnitte}
		 * dieses Ergebnisses als Blätter und verbindet diese zu sementischen Knoten.
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

		public int[] path(final int index) {
			final CompactIntegerArray res = new CompactIntegerArray(10);
			final Comparable2<Token> comp = Token.containing(index);
			for (Token tok = this.root; true;) {
				final int pos = tok.find(comp);
				if (pos < 0) return res.toArray();
				res.add(pos);
				tok = tok.get(pos);
			}
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
			return Iterators.fromItems(this, 0, this.size());
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.source()) ^ Objects.hash(this.root) ^ Objects.hash((Object[])this.tokens);
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Result)) return false;
			final Result that = (Result)object;
			return Objects.equals(this.source(), that.source()) && Objects.equals(this.root, that.root) && Objects.equals(this.tokens, that.tokens);
		}

		/** Diese Methode liefert die {@link #source() Eingabezeichenkette}. */
		@Override
		public String toString() {
			return this.source();
		}

	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Zeichen für einen Zeilenumbruch steht.
	 *
	 * @param sym Zeichen.
	 * @return {@code true} bei einem Zeilenumbruch-Zeichen (line feed, line tabulation, form feed, carriage return, next line, line separator, paragraph
	 *         separator) ; {@code false} sonst. */
	public static boolean isLinebreak(final int sym) {
		switch (sym) {
			case 0x000A: // line feed
			case 0x000B: // line tabulation
			case 0x000C: // form feed
			case 0x000D: // carriage return
			case 0x0085: // next line
			case 0x2028: // line separator
			case 0x2029: // paragraph separator
				return true;
		}
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Zeichen für Leerraum steht.
	 *
	 * @param sym Zeichen.
	 * @return {@code true} bei einem Leerraum-Zeichen (character tabulation, line feed, line tabulation, form feed, carriage return, space, next line, no-break
	 *         space, ogham space mark, en quad, em quad, en space, em space, three-per-em space, four-per-em space, six-per-em space, figure space, punctuation
	 *         space, thin space, hair space, line separator, paragraph separator, narrow no-break space, medium mathematical space, zero width space, zero width
	 *         non-joiner, zero width joiner, word joiner, ideographic space) ; {@code false} sonst. */
	public static boolean isWhitespace(final int sym) {
		switch (sym) {
			case 0x0009: // character tabulation
			case 0x000A: // line feed
			case 0x000B: // line tabulation
			case 0x000C: // form feed
			case 0x000D: // carriage return
			case 0x0020: // space
			case 0x0085: // next line
			case 0x00A0: // no-break space
			case 0x1680: // ogham space mark
			case 0x2000: // en quad
			case 0x2001: // em quad
			case 0x2002: // en space
			case 0x2003: // em space
			case 0x2004: // three-per-em space
			case 0x2005: // four-per-em space
			case 0x2006: // six-per-em space
			case 0x2007: // figure space
			case 0x2008: // punctuation space
			case 0x2009: // thin space
			case 0x200A: // hair space
			case 0x2028: // line separator
			case 0x2029: // paragraph separator
			case 0x202F: // narrow no-break space
			case 0x205F: // medium mathematical space
			case 0x200B: // zero width space
			case 0x200C: // zero width non-joiner
			case 0x200D: // zero width joiner
			case 0x2060: // word joiner
			case 0x3000: // ideographic space
				return true;
		}
		return false;
	}

	/** Dieses Feld speichert die aktuelle Position. */
	private int index;

	/** Dieses Feld speichert das aktuelle Zeichen oder {@code -1}. */
	private int symbol;

	/** Dieses Feld speichert die Anzahl der Zeichen in der Eingabe. */
	private final int length;

	/** Dieses Feld speichert die Zeichen der Eingabe. */
	private final char[] chars;

	/** Dieses Feld speichert die Eingabe. */
	private final String source;

	/** Dieses Feld speichert die Ausgabe. */
	private final StringBuilder target = new StringBuilder(0);

	/** Dieses Feld speichert die erfassten Abschnitte. */
	private final LinkedList<Token> tokens = new LinkedList<>();

	/** Dieser Konstruktor initialisiert die Eingabe.
	 *
	 * @param source Eingabe.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist. */
	public Parser(final String source) throws NullPointerException {
		this.chars = source.toCharArray();
		this.length = source.length();
		this.source = source;
		this.reset();
	}

	/** Diese Methode setzt die {@link #index() aktuelle Position} und gibt das {@link #symbol() aktuelle Zeichen} zurück.
	 *
	 * @see #index()
	 * @see #symbol()
	 * @param index Position.
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist. */
	public final int seek(final int index) throws IndexOutOfBoundsException {
		if (index < 0) throw new IndexOutOfBoundsException();
		if (index < this.length) return this.symbol = this.chars[this.index = index];
		this.index = this.length;
		return this.symbol = -1;
	}

	/** Diese Methode überspring das {@link #symbol() aktuelle Zeichen}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 *
	 * @see #take()
	 * @see #index()
	 * @see #symbol()
	 * @see #target()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}. */
	public final int skip() {
		return this.seek(this.index + 1);
	}

	/** Diese Methode setzt die {@link #index() aktuelle Position} auf {@code 0} zurück.
	 *
	 * @see #seek(int) */
	public final void reset() {
		this.seek(0);
	}

	/** Diese Methode gibt die aktuelle Position zurück.
	 *
	 * @see #skip()
	 * @see #take()
	 * @see #symbol()
	 * @return aktuelle Position. */
	public final int index() {
		return this.index;
	}

	/** Diese Methode gibt die Nummer des aktuellen Zeichens ({@code char}) oder {@code -1} zurück. Der Rückgabewert ist nur dann {@code -1}, wenn das Ende der
	 * {@link #source() Eingabe} erreicht wurde.
	 *
	 * @see #skip()
	 * @see #take()
	 * @see #index()
	 * @return aktuelles Zeichen oder {@code -1}. */
	public final int symbol() {
		return this.symbol;
	}

	/** Diese Methode gibt die Auflistung aller {@link #push(Token) erfassten Abschnitte} zurück.
	 *
	 * @return Abschnittsliste. */
	public final LinkedList<Token> tokens() {
		return this.tokens;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} gleich {@code 0} und damit am Anfang der {@link #source()
	 * Eingabe} ist.
	 *
	 * @see #seek(int)
	 * @see #reset()
	 * @see #index()
	 * @return {@code true}, wenn die aktuelle Position minimal ist. */
	public final boolean isReset() {
		return this.index == 0;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das {@link #symbol() aktuelle Zeichen} kleiner {@code 0} und damit die {@link #index() aktuelle
	 * Position} am Ende der {@link #source() Eingabe} ist.
	 *
	 * @see #seek(int)
	 * @see #index()
	 * @see #length()
	 * @see #symbol()
	 * @return {@code true}, wenn die aktuelle Position maximal ist. */
	public final boolean isParsed() {
		return this.symbol < 0;
	}

	/** Diese Methode übernimmt das {@link #symbol() aktuelle Zeichen} in die {@link #target() Ausgabe}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 * Wenn sich die {@link #index() aktuelle Position} bereits am Ende der {@link #source() Eingabe} befindet, wird kein Zeichen in die Ausgabe übernommen.
	 *
	 * @see #take(int)
	 * @see #skip()
	 * @see #index()
	 * @see #symbol()
	 * @see #target()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}. */
	public final int take() {
		this.take(this.symbol);
		return this.skip();
	}

	/** Diese Methode übernimmt das gegebene Zeichen in die {@link #target() Ausgabe}, sofern diese nicht negativ ist.
	 *
	 * @see #take()
	 * @see #target()
	 * @param symbol Zeichen. */
	public final void take(final int symbol) {
		if (symbol < 0) return;
		this.target.append((char)symbol);
	}

	/** Diese Methode übernimmt die gegebene Zeichenkette in die {@link #target() Ausgabe}.
	 *
	 * @param symbols Zeichenkette.
	 * @see #take()
	 * @see #target()
	 * @throws NullPointerException Wenn die Zeichenkette {@code null} ist. */
	public final void take(final String symbols) throws NullPointerException {
		this.target.append(symbols.toString());
	}

	/** Diese Methode erzeugt einen an der {@link #index() aktuellen Position} beginnenden und ein Zeichen langen Abschnitt und gibt diesen Abschnitt zurück. Sie
	 * ist eine Abkürzung für {@link #make(int, int, int) this.make(type, this.index(), 1)}.
	 *
	 * @param type Abschnittstyp.
	 * @return neuer Abschnitt. */
	public final Token make(final int type) throws IllegalArgumentException {
		return this.make(type, this.index(), 1);
	}

	/** Diese Methode erzeugt einen an der gegebenen Position beginnenden und an der {@link #index() aktuellen Position} endenden Abschnitt und gibt diesen
	 * zurück. Sie ist eine Abkürzung für {@link #make(int, int, int) this.make(type, offset, this.index() - offset)}.
	 *
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @return neuer Abschnitt. */
	public final Token make(final int type, final int offset) throws IllegalArgumentException {
		return this.make(type, offset, this.index() - offset);
	}

	/** Diese Methode erzeugt einen neuen Abschnitt mit den gegebenen Eigenschaften und gibt diesen zurück.. Sie ist eine Abkürzung für
	 * {@link Token#from(String, int, int, int) Token.from(this.source(), offset, length, type)}.
	 *
	 * @param type Abschnittstyp
	 * @param offset Abschnittsbeginn.
	 * @param length Abschnittslänge.
	 * @return neuer Abschnitt. */
	public final Token make(final int type, final int offset, final int length) throws IllegalArgumentException {
		return Token.from(this.source(), offset, length, type);
	}

	/** Diese Methode erzeugt einen an der gegebenen Position beginnenden und an der {@link #index() aktuellen Position} endenden Abschnitt und gibt diesen
	 * zurück. Sie ist eine Abkürzung für {@link #make(int, int, int, Iterable) this.make(type, offset, this.index() - offset, tokens)}.
	 *
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @param tokens Kindabschnitte.
	 * @return neuer Abschnitt. */
	public final Token make(final int type, final int offset, final List<Token> tokens) throws NullPointerException, IllegalArgumentException {
		return this.make(type, offset, this.index() - offset, tokens);
	}

	/** Diese Methode erzeugt einen neuen Abschnitt mit den gegebenen Eigenschaften. Sie ist eine Abkürzung für {@link Token#from(String, int, int, int, Iterable)
	 * Token.from(this.source(), offset, length, type, tokens)}.
	 *
	 * @param type Abschnittstyp
	 * @param offset Abschnittsbeginn.
	 * @param length Abschnittslänge.
	 * @param tokens Kindabschnitte.
	 * @return neuer Abschnitt. */
	public final Token make(final int type, final int offset, final int length, final Iterable<Token> tokens)
		throws NullPointerException, IllegalArgumentException {
		return Token.from(this.source(), offset, length, type, tokens);
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make(type))}.
	 *
	 * @see #make(int)
	 * @param type Abschnittstyp.
	 * @return Abschnitt. */
	public final Token push(final int type) throws IllegalArgumentException {
		return this.push(this.make(type));
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make(type, offset))}.
	 *
	 * @see #make(int, int)
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @return Abschnitt oder {@code null}. */
	public final Token push(final int type, final int offset) throws IllegalArgumentException {
		return this.push(this.make(type, offset));
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make(type, offset, length))}.
	 *
	 * @see #make(int, int, int)
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @param length Abschnittslänge.
	 * @return Abschnitt oder {@code null}. */
	public final Token push(final int type, final int offset, final int length) {
		return this.push(this.make(type, offset, length));
	}

	/** Diese Methode erfasst den gegebenen {@link Token Abschnitt} und gibt ihn zurück. Er wird nur dann an die {@link #tokens() Auflistung aller erfassten
	 * Abschnitte} angefügt, wenn er nich {@code null} und nicht {@link Token#length() leer} ist.
	 *
	 * @param token Abschnitt oder {@code null}.
	 * @return Abschnitt oder {@code null}. */
	public final Token push(final Token token) {
		if ((token == null) || (token.length() == 0)) return token;
		this.tokens.add(token);
		return token;
	}

	/** Diese Methode leert die {@link #target() Ausgabe}.
	 *
	 * @see #take()
	 * @see #take(int)
	 * @see #take(String)
	 * @see #target() */
	public final void clear() {
		this.target.setLength(0);
	}

	/** Diese Methode gibt die über {@link #take()}, {@link #take(int)} bzw. {@link #take(String)} gesammelten Zeichen als {@link String} zurück.
	 *
	 * @see #skip()
	 * @see #take()
	 * @see #take(int)
	 * @see #take(String)
	 * @see #clear()
	 * @see #symbol()
	 * @return Ausgabe. */
	public final String target() {
		return this.target.toString();
	}

	/** Diese Methode gibt die Länge der {@link #source() Eingabe} zurück.
	 *
	 * @see #seek(int)
	 * @see #index()
	 * @see #source()
	 * @return Länge der Eingabe. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt die Eingabe zurück.
	 *
	 * @return Eingabe. */
	public final String source() {
		return this.source;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.source);
	}

}