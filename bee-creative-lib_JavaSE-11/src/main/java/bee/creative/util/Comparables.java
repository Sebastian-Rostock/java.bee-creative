package bee.creative.util;

import java.util.Comparator;
import java.util.List;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert mehrere Funktionen zur stabilen binären Suche mit {@link Comparable} als Suchkriterium sowie zur Erzeugung von
 * {@link Comparable2}.
 * <p>
 * <a name="comparables_old"><u>Binäre Suche mit {@link java.util.Arrays} und {@link java.util.Collections}</u></a>
 * <p>
 * Die Schwächen der Funktionen {@link java.util.Arrays#binarySearch(Object[], Object, Comparator)} und
 * {@link java.util.Collections#binarySearch(List, Object, Comparator)} liegen zum einen bei ihrer Beschränkung auf Suchkriterien, die zum Typ der Elemente in
 * den Arrays bzw. Listen kompatibel sein müssen, und zum anderen im Indeterminismus bei merhfach vorkommenden Elementen.
 * <p>
 * <a name="comparables_new"><u>Binäre Suche mit {@link Comparables}</u></a>
 * <p>
 * Die hier in {@link Comparables} implementierten Funktionen zur binären Suche abstrahieren Suchkriterien als {@link Comparable}, welche zu einem gegebenen
 * Element einen Navigationswert berechnen. Bei einer binären Suche mit einem {@link Comparable} {@code comparable} als Suchkriterium gilt ein Element
 * {@code element} an Position {@code index} als Treffer, wenn der Navigationswert {@code comparable.compareTo(element)} gleich {@code 0} ist. Wenn der
 * Navigationswert dagegen kleier oder größer als {@code 0} ist, wird die binäre Suche bei den Positionen kleier bzw. größer als {@code index} fortgesetzt. Bei
 * einer erfolglosen benären Suche geben die Funktionen <code>(-(<em>Einfügeposition</em>)-1)</code> zurück, sodass ein positiver Rückgabewert immer einen
 * Treffer signalisiert. Die <em>Einfügeposition</em> ist dabei die Position, an der ein Element seiner Ordnung entsprechend in das Array bzw. die Liste
 * eingefügt werden müsste. Das gegebene Array bzw. die gegebene Liste muss bezüglich der Ordnung des {@link Comparable} aufsteigend sortiert sein.
 * <p>
 * Neben den für merhfach vorkommende Elemente indeterministischen {@code binarySearch()}-Funktionen gibt es hier auch die deterministischen
 * {@code binarySearchFirst()}- und {@code binarySearchLast()}-Funktionen, welche nach der kleinsten bzw. größten Position eines Treffers suchen.
 *
 * @see java.util.Arrays#binarySearch(Object[], Object)
 * @see java.util.Arrays#binarySearch(Object[], Object, Comparator)
 * @see java.util.Collections#binarySearch(List, Object)
 * @see java.util.Collections#binarySearch(List, Object, Comparator)
 * @see Comparable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Comparables {

	/** Diese Klasse implementiert ein {@link Comparable2}, welches stets den {@link #compareTo(Object) Navigationswert} {@code 0} liefert. */
	public static class EmptyComparable extends AbstractComparable<Object> {

		public static final Comparable2<?> INSTANCE = new EmptyComparable();

	}

	/** Diese Klasse implementiert ein verkettetes {@link Comparable2}, welches den {@link #compareTo(Object) Navigationswert} eines ersten gegebenen
	 * {@link Comparable} liefert, sofern dieser ungleich {@code 0} ist, und sonst den eines zweiten gegebenen {@link Comparable} verwendet.
	 *
	 * @param <GItem> Typ der Eingabe. */
	public static class ConcatComparable<GItem> extends AbstractComparable<GItem> {

		public final Comparable<? super GItem> that1;

		public final Comparable<? super GItem> that2;

		public ConcatComparable(Comparable<? super GItem> that1, Comparable<? super GItem> that2) {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public int compareTo(GItem item) {
			var result = this.that1.compareTo(item);
			if (result != 0) return result;
			return this.that2.compareTo(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that1, this.that2);
		}

	}

	/** Diese Klasse implementiert ein {@link Comparable2}, welches den {@link #compareTo(Object) Navigationswert} eines gegebenen {@link Comparable} mit
	 * umgekehrten Vorzeichen liefert.
	 *
	 * @param <GItem> Typ der Eingabe. */
	
	public static class ReverseComparable<GItem> extends AbstractComparable<GItem> {

		public final Comparable<? super GItem> comparable;

		public ReverseComparable(Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public int compareTo(GItem item) {
			return -this.comparable.compareTo(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

	}

	/** Diese Klasse implementiert ein übersetztes {@link Comparable2}, welches seine Eingabe über einen gegebenen {@link Getter} in die Eingabe eines gegebenen
	 * {@link Comparable} überführt und dessen {@link #compareTo(Object) Navigationswert} liefert. Der Navigationswert für eine Eingabe {@code item} ist
	 * {@code this.that.compareTo(this.trans.get(item))}. *
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param <GItem2> Typ der Eingabe des gegebenen {@link Comparable}. */
	
	public static class TranslatedComparable<GItem, GItem2> extends AbstractComparable<GItem> {

		public final Comparable<? super GItem2> that;

		public final Getter<? super GItem, ? extends GItem2> trans;

		public TranslatedComparable(Comparable<? super GItem2> that, Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public int compareTo(GItem item) {
			return this.that.compareTo(this.trans.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert ein {@link Comparable2}, welches {@code null}-Eingaben als minimal betrachtet und alle anderen Eingaben an ein gegebenes
	 * {@link Comparable} delegiert. Der Navigationswert für eine Eingaben {@code item} ist {@code ((item == null) ? 1 : this.that.compareTo(item))}.
	 *
	 * @param <GItem> Typ der Eingabe. */
	
	public static class OptionalizedComparable<GItem> extends AbstractComparable<GItem> {

		public final Comparable<? super GItem> that;

		public OptionalizedComparable(Comparable<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public int compareTo(GItem item) {
			return item == null ? 1 : this.that.compareTo(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class ComparatorComparable<GItem> extends AbstractComparable<GItem> {

		public final GItem item;

		public final Comparator<? super GItem> order;

		public ComparatorComparable(GItem item, Comparator<? super GItem> order) {
			this.item = item;
			this.order = Objects.notNull(order);
		}

		@Override
		public int compareTo(GItem item) {
			return this.order.compare(this.item, item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.order);
		}

	}

	static void check(int fromIndex, int toIndex) throws IllegalArgumentException {
		if (fromIndex > toIndex) throw new IllegalArgumentException("fromIndex > toIndex");
	}

	static void check(int length, int fromIndex, int toIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(fromIndex, toIndex);
		if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex < 0");
		if (toIndex > length) throw new IndexOutOfBoundsException("toIndex > length");
	}

	static void check(Array<?> items, Comparable<?> comparable, int fromIndex, int toIndex) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(items);
		Objects.notNull(comparable);
		Comparables.check(fromIndex, toIndex);
	}

	static void check(List<?> items, Comparable<?> comparable, int fromIndex, int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Objects.notNull(comparable);
		Comparables.check(items.size(), fromIndex, toIndex);
	}

	static void check(Object[] items, Comparable<?> comparable, int fromIndex, int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Objects.notNull(comparable);
		Comparables.check(items.length, fromIndex, toIndex);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearch(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist. */
	public static <GItem> int binarySearch(GItem[] items, Comparable<? super GItem> comparable) throws ClassCastException, NullPointerException {
		return Comparables.binarySearch(items, comparable, 0, items.length);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}. */
	public static <GItem> int binarySearch(GItem[] items, Comparable<? super GItem> comparable, int fromIndex, int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items[next]);
			if (comp < 0) {
				last = next;
			} else if (comp > 0) {
				from = next + 1;
			} else return next;
		}
		return -(from + 1);
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearch(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist. */
	public static <GItem> int binarySearch(final List<? extends GItem> items, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearch(items, comparable, 0, items.size());
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}. */
	public static <GItem> int binarySearch(final List<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else if (comp > 0) {
				from = next + 1;
			} else return next;
		}
		return -(from + 1);
	}

	/** Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Array} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Array} eine {@link IndexOutOfBoundsException} auslöst. */
	public static <GItem> int binarySearch(final Array<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else if (comp > 0) {
				from = next + 1;
			} else return next;
		}
		return -(from + 1);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist. */
	public static <GItem> int binarySearchFirst(final GItem[] items, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(items, comparable, 0, items.length);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}. */
	public static <GItem> int binarySearchFirst(final GItem[] items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items[next]);
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items[from]) == 0)) return from;
		return -(from + 1);
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist. */
	public static <GItem> int binarySearchFirst(final List<? extends GItem> items, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(items, comparable, 0, items.size());
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}. */
	public static <GItem> int binarySearchFirst(final List<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -(from + 1);
	}

	/** Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Array} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Array} eine {@link IndexOutOfBoundsException} auslöst. */
	public static <GItem> int binarySearchFirst(final Array<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -(from + 1);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchLast(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist. */
	public static <GItem> int binarySearchLast(final GItem[] items, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(items, comparable, 0, items.length);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}. */
	public static <GItem> int binarySearchLast(final GItem[] items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items[next]);
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items[from]) == 0)) return from;
		return -from - 2;
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchLast(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist. */
	public static <GItem> int binarySearchLast(final List<? extends GItem> items, final Comparable<? super GItem> comparable)
		throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(items, comparable, 0, items.size());
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}. */
	public static <GItem> int binarySearchLast(final List<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -from - 2;
	}

	/** Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Array} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Array} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Array} eine {@link IndexOutOfBoundsException} auslöst. */
	public static <GItem> int binarySearchLast(final Array<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
		final int toIndex) throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(items, comparable, fromIndex, toIndex);
		int from = fromIndex, last = toIndex;
		while (from < last) {
			final int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((fromIndex <= from) && (from < toIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -from - 2;
	}

	/** Diese Methode liefert das {@link EmptyComparable}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Comparable2<GItem> empty() {
		return (Comparable2<GItem>)EmptyComparable.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Comparable} als {@link Comparable2}. Wenn er {@code null} ist, wird das {@link EmptyComparable} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Comparable2<GItem> from(final Comparable<? super GItem> that) {
		if (that == null) return Comparables.empty();
		if (that instanceof Comparable2<?>) return (Comparable2<GItem>)that;
		return Comparables.translate(that, Getters.<GItem>neutralGetter());
	}

	/** Diese Methode gibt einen {@link Comparable} zurück, der den gegebenen {@link Comparator} sowie die gegebene Eingabe zur Berechnung des Navigationswert
	 * verwendet. Die gegebene Eingabe wird als erstes Argument des {@link Comparator} verwendet. Der Navigationswert für ein Element {@code item} ist
	 * {@code comparator.compare(input, item)}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param item erstes Argument des {@link Comparator}.
	 * @param order {@link Comparator}.
	 * @return {@code entry}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist. */
	public static <GItem> Comparable2<GItem> from(final GItem item, final Comparator<? super GItem> order) throws NullPointerException {
		return new ComparatorComparable<>(item, order);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatComparable new ConcatComparable<>(that1, that2)}. */
	public static <GItem> Comparable2<GItem> concat(final Comparable<? super GItem> that1, final Comparable<? super GItem> that2) throws NullPointerException {
		return new ConcatComparable<>(that1, that2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseComparable new ReverseComparable<>(that)}. */
	public static <GItem> Comparable2<GItem> reverse(final Comparable<? super GItem> that) throws NullPointerException {
		return new ReverseComparable<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedComparable new TranslatedComparable<>(that, trans)}. */
	public static <GSource, GTarget> Comparable2<GTarget> translate(final Comparable<? super GSource> that,
		final Getter<? super GTarget, ? extends GSource> trans) throws NullPointerException {
		return new TranslatedComparable<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedComparable new OptionalizedComparable<>(that)}. */
	public static <GItem> Comparable2<GItem> optionalize(final Comparable<? super GItem> that) throws NullPointerException {
		return new OptionalizedComparable<>(that);
	}

}
