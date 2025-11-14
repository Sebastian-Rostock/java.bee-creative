package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import bee.creative.array.CompactIntegerArray;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Parsen zur Umwandlung einer Zeichenkette in hierarchische {@link Token Abschnitte}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Parser {

	/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Zeichen für einen Zeilenumbruch steht.
	 *
	 * @param sym Zeichen.
	 * @return {@code true} bei einem Zeilenumbruch-Zeichen (line feed, line tabulation, form feed, carriage return, next line, line separator, paragraph
	 *         separator) ; {@code false} sonst. */
	public static boolean isLinebreak(int sym) {
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
	public static boolean isWhitespace(int sym) {
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

	/** Dieser Konstruktor initialisiert {@link #source()}. */
	public Parser(String source) throws NullPointerException {
		this.useSource(Source.sourceFrom(source));
	}

	/** Diese Methode liefert die Eingabe. */
	public final Source source() {
		return this.source;
	}

	/** Diese Methode setzt die {@link #index() aktuelle Position} und gibt das {@link #symbol() aktuelle Zeichen} zurück. Wenn die Position ungültig ist, wird
	 * sie auf das Ende der Eingabe gesetzt.
	 *
	 * @see #index()
	 * @see #symbol()
	 * @param index Position.
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}. */
	public final int seek(int index) {
		return this.select(this.startIndex + index);
	}

	/** Diese Methode überspring das {@link #symbol() aktuelle Zeichen}, navigiert zum nächsten Zeichen und gibt dieses zurück.
	 *
	 * @see #take()
	 * @see #index()
	 * @see #symbol()
	 * @see #target()
	 * @return {@link #symbol() aktuelles Zeichen} oder {@code -1}. */
	public final int skip() {
		return this.select(this.index + 1);
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
		return this.index - this.startIndex;
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
		return this.index() == 0;
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
	public final void take(int symbol) {
		if (symbol < 0) return;
		this.target.append((char)symbol);
	}

	/** Diese Methode übernimmt die gegebene Zeichenkette in die {@link #target() Ausgabe}.
	 *
	 * @param symbols Zeichenkette.
	 * @see #take()
	 * @see #target()
	 * @throws NullPointerException Wenn die Zeichenkette {@code null} ist. */
	public final void take(String symbols) throws NullPointerException {
		this.target.append(symbols.toString());
	}

	/** Diese Methode erzeugt und liefert einen an der {@link #index() aktuellen Position} beginnenden und ein Zeichen langen Abschnitt. */
	public final Token make() {
		return Token.from(this.source).useStart(this.index()).useLength(1);
	}

	/** Diese Methode erzeugt einen an der {@link #index() aktuellen Position} beginnenden und ein Zeichen langen Abschnitt und gibt diesen Abschnitt zurück. Sie
	 * ist eine Abkürzung für {@link #make(int, int, int) this.make(type, this.index(), 1)}.
	 *
	 * @param type Abschnittstyp.
	 * @return neuer Abschnitt. */
	@Deprecated
	public final Token make(int type) throws IllegalArgumentException {
		return Token.from(this.source).useType(type).useStart(this.index()).useLength(1);
	}

	/** Diese Methode erzeugt einen an der gegebenen Position beginnenden und an der {@link #index() aktuellen Position} endenden Abschnitt und gibt diesen
	 * zurück. Sie ist eine Abkürzung für {@link #make(int, int, int) this.make(type, offset, this.index() - offset)}.
	 *
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @return neuer Abschnitt. */
	@Deprecated
	public final Token make(int type, int offset) throws IllegalArgumentException {
		return Token.from(this.source).useType(type).useStart(offset).useLength(this.index() - offset);
	}

	/** Diese Methode liefert einen neuen Abschnitt mit den gegebenen Eigenschaften.
	 *
	 * @param type Abschnittstyp
	 * @param offset Abschnittsbeginn.
	 * @param length Abschnittslänge.
	 * @return neuer Abschnitt. */
	@Deprecated
	public final Token make(int type, int offset, int length) throws IllegalArgumentException {
		return Token.from(this.source).useType(type).useStart(offset).useLength(length);
	}

	/** Diese Methode erzeugt einen an der gegebenen Position beginnenden sowie an der {@link #index() aktuellen Position} endenden Abschnitt und gibt diesen
	 * zurück. Sie ist eine Abkürzung für {@link #make(int, int, int, Iterable) this.make(type, offset, this.index() - offset, tokens)}.
	 *
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @param tokens Kindabschnitte.
	 * @return neuer Abschnitt. */
	@Deprecated
	public Token make(final int type, final int offset, final List<Token> tokens) throws NullPointerException, IllegalArgumentException {
		return this.make(type, offset, this.index() - offset, tokens);
	}

	@Deprecated
	public Token make(final int type, final int offset, final int length, final Iterable<Token> tokens) throws NullPointerException, IllegalArgumentException {
		return Token.from(this.source).useType(type).useStart(offset).useLength(length).addAll(tokens);
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make(type))}.
	 *
	 * @see #make(int)
	 * @param type Abschnittstyp.
	 * @return Abschnitt. */
	@Deprecated
	public final Token push(int type) throws IllegalArgumentException {
		return this.push(Token.from(this.source).useType(type).useStart(this.index()).useLength(1));
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make(type, offset))}.
	 *
	 * @see #make(int, int)
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @return Abschnitt oder {@code null}. */
	@Deprecated
	public final Token push(int type, int offset) throws IllegalArgumentException {
		return this.push(Token.from(this.source).useType(type).useStart(offset).useLength(this.index() - offset));
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make(type, offset, length))}.
	 *
	 * @see #make(int, int, int)
	 * @param type Abschnittstyp.
	 * @param offset Abschnittsbeginn.
	 * @param length Abschnittslänge.
	 * @return Abschnitt oder {@code null}. */
	@Deprecated
	public final Token push(int type, int offset, int length) {
		return this.push(Token.from(this.source).useType(type).useStart(offset).useLength(length));
	}

	/** Diese Methode ist eine Abkürzung für {@link #push(Token) this.push(this.make())}. */
	public final Token push() {
		return this.push(this.make());
	}

	/** Diese Methode ergänzt die {@link #tokens() Liste der erfassten Abschnitte} um den gegebenen, sofern dieser nilcht {@code null} ist, und liefert diesen. */
	public final Token push(Token token) {
		if (token == null) return token;
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
		return this.finalIndex - this.startIndex;
	}

	/** Diese Methode setzt die Eingabe, {@link #reset() setzt} die aktuelle Position auf {@code 0} und liefert {@code this}. */
	public final Parser useSource(Source source) throws NullPointerException {
		this.array = source.chars;
		this.startIndex = source.offset;
		this.finalIndex = source.offset + source.length;
		this.source = source;
		this.reset();
		return this;
	}

	@Override
	public String toString() {
		return this.source().toString();
	}

	/** Diese Klasse implementiert einen {@link #getType() typisierten} Abschnitt einer {@link #source() Zeichenkette}. Abschnitte können zudem über
	 * {@link #children() untergeordnete Kindabschnitte} einen syntaktischen Baum bilden und beliebige {@link #values() benannte Werte} besitzen.
	 *
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class Token implements Comparable<Token> {

		/** Dieses Feld speichert den leeren Abschnitt. */
		public static final Token EMPTY = Token.from(Source.EMPTY);

		/** Diese Methode liefert einen Abschnitt, der die gegebene Zeichenkette enthält. */
		public static Token from(Source source) throws NullPointerException {
			return new Token(source).useLength(source.length);
		}

		/** Diese Methode liefert ein {@link Comparable}, welches die {@link Token#end() Endpositionen} eines {@link Token Abschnitts} mit der gegebenen Position
		 * vergleicht. Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene
		 * Position kleiner, gleich bzw. größer der Endposition des Abschnitts ist.
		 *
		 * @param index Position.
		 * @return {@link Comparable} für die Endpositionen von Abschnitten. */
		public static Comparable3<Token> endingAt(int index) {
			return value -> Comparators.compare(index, value.end());
		}

		/** Diese Methode liefert ein {@link Comparable}, welches die {@link Token#start() Startposition} eines {@link Token Abschnitts} mit der gegebenen Position
		 * vergleicht. Der Rückhabewert der {@link Comparable#compareTo(Object) Navigationsmethode} ist kleiner, gleich oder größer {@code 0}, wenn die gegebene
		 * Position kleiner, gleich bzw. größer der Startposition des Abschnitts ist.
		 *
		 * @param index Position.
		 * @return {@link Comparable} für die Startposition von Abschnitten. */
		public static Comparable3<Token> startingAt(int index) {
			return value -> Comparators.compare(index, value.start());
		}

		/** Diese Methode liefert ein {@link Comparable3}, welches die Grenzen eines {@link Token Abschnitts} mit der gegebenen Position vergleicht und einen
		 * {@link Comparable#compareTo(Object) Navigationswert} kleiner, gleich oder größer {@code 0} liefert, wenn die gegebene Position kleiner als die
		 * {@link Token#start() Startposition} des Abschnitts ist, innerhalb des Abschnitts liegt bzw. größer oder gleich der {@link Token#end() Endposition} des
		 * Abschnitts ist.
		 *
		 * @param index Position.
		 * @return {@link Comparable} für den Inhaltsbereich von Abschnitten. */
		public static Comparable3<Token> containing(int index) {
			return value -> index < value.start() ? -1 : index < value.end() ? 0 : +1;
		}

		/** Diese Methode liefet den Typ des Abschnitts. */
		public int getType() {
			return this.type;
		}

		/** Diese Methode liefert den {@link #values() Wert} zum gegebenen Schlüssel oder {@code null}. */
		public Object getValue(Object key) {
			return this.values().get(key);
		}

		/** Diese Methode liefert eine Abbildung mit den Werten des Abschnitts. */
		public Map2<Object, Object> values() {
			return ProxyMap.from(() -> this.values, value -> this.values = value);
		}

		/** Diese Methode liefet die Liste der Kindabschnitte. */
		public List2<Token> children() {
			return ProxyList.from(() -> this.children, value -> this.children = value);
		}

		/** Diese Methode liefet die Position des ersten Zeichens nach dem Abschnitt. */
		public int end() {
			return this.offset + this.length;
		}

		/** Diese Methode liefet die Position des ersten Zeichens im Abschnitt. */
		public int start() {
			return this.offset;
		}

		/** Diese Methode liefet die Länge des Abschnitts. */
		public int length() {
			return this.length;
		}

		/** Diese Methode liefert die Eingabe des {@link Parser}, auf die sich die dieser Abschnitt bezieht. */
		public Source source() {
			return this.source;
		}

		/** Diese Methode liefert die Position des {@link #children() Kindabschnitts}, der die gegebene Position {@link #containing(int) enthält} und ist eine
		 * Abkürzung für {@link #find(Comparable) this.find(Token.containing(index))}. */
		public int find(int index) {
			return this.find(Token.containing(index));
		}

		/** Diese Methode liefert die Position des {@link #children() Kindabschnitts} zur gegebenen {@link Comparable Navigationsmethode} und ist eine Abkürzung für
		 * {@link Comparables#binarySearch(List, Comparable) Comparables.binarySearch(this.children(), comp)}.
		 *
		 * @see #startingAt(int)
		 * @see #endingAt(int)
		 * @see #containing(int) */
		public int find(Comparable<Token> comp) {
			return this.children != null ? Comparables.binarySearch(this.children, comp) : -1;
		}

		/** Diese Methode ergänzt die {@link #children() Liste der Kindabschnitte} um den gegebenen Kindabschnitt und liefert {@code this}. */
		public Token add(Token child) {
			this.children().add(child);
			return this;
		}

		/** Diese Methode ergänzt die {@link #children() Liste der Kindabschnitte} um die gegebenen Kindabschnitte und liefert {@code this}. */
		public Token addAll(Iterable<Token> children) {
			this.children().addAll(children);
			return this;
		}

		/** Diese Methode setzt den {@link #getType() Abschnittstyp} und liefert {@code this}. */
		public Token useType(int type) {
			this.type = type;
			return this;
		}

		/** Diese Methode setzt den {@link #values() Abschnittswert} zum gegebenen Schlüssel und liefert {@code this}. */
		public Token useValue(Object key, Object value) {
			this.values().put(key, value);
			return this;
		}

		/** Diese Methode setzt die {@link #end() Endposition} und liefert {@code this}. Wenn die {@link #start() Startposition} nicht beibehalten werden kann, wird
		 * sie auf die Endposition verschoben. */
		public Token useEnd(int end) throws IllegalArgumentException {
			if ((end < 0) || (this.source.length() < end)) throw new IllegalArgumentException();
			this.length = end - (this.offset = Math.min(this.offset, end));
			return this;
		}

		/** Diese Methode setzt die {@link #start() Startposition} und liefert {@code this}. Wenn die {@link #end() Endposition} nicht beibehalten werden kann, wird
		 * sie auf die Startposition verschoben. */
		public Token useStart(int start) {
			if ((start < 0) || (this.source.length() < start)) throw new IllegalArgumentException();
			this.length = Math.max(0, (this.offset + this.length) - (this.offset = start));
			return this;
		}

		/** Diese Methode setzt die {@link #length() Abschnittslänge} und liefert {@code this}. Die {@link #start() Startposition} bleibt erhalten. */
		public Token useLength(int length) throws IllegalArgumentException {
			if ((length < 0) || (this.source.length() < (this.offset + length))) throw new IllegalArgumentException();
			this.length = length;
			return this;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.type, this.offset, this.length);
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof Token)) return false;
			var that = (Token)object;
			return (this.type == that.type) && (this.offset == that.offset) && (this.length == that.length);
		}

		@Override
		public int compareTo(Token that) {
			return this.offset - that.offset;
		}

		/** Diese Methode liefert die Zeichenkette im Abschnitt. */
		@Override
		public String toString() {
			return this.toSource().toString();
		}

		/** Diese Methode liefert die Zeichenkette im Abschnitt. */
		public Source toSource() {
			return this.source.section(this.offset, this.length);
		}

		private final Source source;

		private int offset;

		private int length;

		private int type;

		private Map<Object, Object> values;

		private List<Token> children;

		private Token(Source source) {
			this.source = source;
		}

		@Deprecated
		public int type() {
			return this.getType();
		}

		@Deprecated
		public Object value() {
			return this.getValue("");
		}

		@Deprecated
		public int size() {
			return this.children().size();
		}

		@Deprecated
		public Token type(int v) {
			return this.useType(this.type);
		}

		@Deprecated
		public Token get(int i) {
			return this.children().get(i);
		}

		@Deprecated
		public Token value(Object v) {
			return this.useValue("", v);
		}

	}

	/** Diese Klasse implementiert das Ergebnis der Übersetzung einer Zeichenkett in eine Hierarchie typisierter Abschnitte.
	 *
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static final class Result {

		/** Dieses Feld speichert das leere Ergebnis ohne Abschnitte. */
		public static final Result EMPTY = Result.from(Token.EMPTY);

		/** Diese Methode liefert ein Ergebnis mit dem gegebenen Wurzelknoten und der gegebenen Abschnittsliste. */
		public static Result from(Token root, Token... tokens) throws NullPointerException {
			return Result.from(root, Arrays.asList(tokens));
		}

		/** Diese Methode liefert ein Ergebnis mit dem gegebenen Wurzelknoten und der gegebenen Abschnittsliste. */
		public static Result from(Token root, List<Token> tokens) throws NullPointerException, IllegalArgumentException {
			return new Result(Objects.notNull(root), tokens);
		}

		/** Diese Methode liefert den Wurzelknoten der Abschnittshierarchie. Der damit aufgespannte Abschnittsbaum verwendet die {@link #tokens() Abschnitte} dieses
		 * Ergebnisses als Blätter und verbindet diese zu sementischen Knoten. */
		public Token root() {
			return this.root;
		}

		/** Diese Methode liefert die {@link Token#source() Zeichenkette} des {@link #root() Wurzelknoten}. */
		public Source source() {
			return this.root.source();
		}

		/** Diese Methode liefert die Liste der Abschnitte. */
		public List<Token> tokens() {
			return this.tokens;
		}

		/** Diese Methode liefert die Position des {@link #tokens() Abschnitts}, der die gegebene Position {@link Token#containing(int) enthält} und ist eine
		 * Abkürzung für {@link #find(Comparable) this.find(Token.containing(index))}. */
		public int find(int index) {
			return this.find(Token.containing(index));
		}

		/** Diese Methode liefert die Position des {@link #tokens() Abschnitts} zur gegebenen {@link Comparable Navigationsmethode} und ist eine Abkürzung für
		 * {@link Comparables#binarySearch(List, Comparable) Comparables.binarySearch(this.tokens(), comp)}. */
		public int find(Comparable<Token> comp) {
			return Comparables.binarySearch(this.tokens(), comp);
		}

		/** Diese Methode liefert die vom {@link #root() Wurzelknoten} ausgehende Liste der {@link Token#find(Comparable) Positionen} der jeweiligen
		 * {@link Token#children() Kindkabschnitte}, die die gegebene Position {@link Token#containing(int) enthalten}. Für die gelieferten Positionen {@code res}
		 * gilt: <pre>
		 *  res[0] = this.root().find(index);
		 *  res[1] = this.root().get(res[0]).find(index);
		 *  res[2] = this.root().get(res[0]).get(res[1]).find(index);
		 *  ...
		 *  </pre> */
		public int[] path(int index) {
			var buffer = new CompactIntegerArray(10, 0f);
			var comp = Token.containing(index);
			for (var token = this.root; true;) {
				var position = token.find(comp);
				if (position < 0) return buffer.toArray();
				buffer.add(position);
				token = token.children.get(position);
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.root, this.tokens);
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof Result)) return false;
			var that = (Result)object;
			return Objects.equals(this.root, that.root) && Objects.equals(this.tokens, that.tokens);
		}

		@Override
		public String toString() {
			return this.source().toString();
		}

		private final Token root;

		private final List<Token> tokens;

		private Result(Token root, List<Token> tokens) {
			this.root = root;
			this.tokens = new ArrayList<>(tokens);
		}

	}

	/** Dieses Feld speichert die Eingabe. */
	private Source source;

	/** Dieses Feld speichert die Zeichen der Eingabe. */
	private char[] array;

	private int startIndex;

	/** Dieses Feld speichert die Anzahl der Zeichen in der Eingabe. */
	private int finalIndex;

	/** Dieses Feld speichert die aktuelle Position. */
	private int index;

	/** Dieses Feld speichert das aktuelle Zeichen oder {@code -1}. */
	private int symbol;

	/** Dieses Feld speichert die Ausgabe. */
	private final StringBuilder target = new StringBuilder(0);

	/** Dieses Feld speichert die erfassten Abschnitte. */
	private final LinkedList<Token> tokens = new LinkedList<>();

	private int select(int index) {
		if ((this.startIndex <= index) && (index < this.finalIndex)) return this.symbol = this.array[this.index = index];
		this.index = this.finalIndex;
		return this.symbol = -1;
	}

}