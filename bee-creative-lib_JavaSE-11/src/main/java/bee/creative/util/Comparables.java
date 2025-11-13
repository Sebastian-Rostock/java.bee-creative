package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import java.util.Comparator;
import java.util.List;
import bee.creative.lang.Array;

/** Diese Klasse implementiert mehrere Funktionen zur stabilen binären Suche mit {@link Comparable} als Suchkriterium sowie zur Erzeugung von
 * {@link Comparable3}.
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

	/** Diese Methode liefert das gegebene {@link Comparable3}. Wenn es {@code null} ist, wird {@link #neutralComparable()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <T> Comparable3<T> comparableFrom(Comparable3<? super T> that) {
		if (that == null) return neutralComparable();
		return (Comparable3<T>)that;
	}

	/** Diese Methode liefert das gegebene {@link Comparable} als {@link Comparable3}. Wenn es {@code null} ist, wird {@link #neutralComparable()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <T> Comparable3<T> comparableFrom(Comparable<? super T> that) {
		if (that == null) return neutralComparable();
		if (that instanceof Comparable3<?>) return (Comparable3<T>)that;
		return that::compareTo;
	}

	/** Diese Methode liefert ein {@link Comparable3}, das die gegebene Eingabe und den gegebenen {@link Comparator} zur Berechnung des Navigationswerts
	 * verwendet. Der Navigationswert für ein Element {@code item} ist {@code order.compare(value, item)}. */
	public static <T> Comparable3<T> comparableFrom(T value, Comparator<? super T> order) throws NullPointerException {
		notNull(order);
		return item2 -> order.compare(value, item2);
	}

	/** Diese Methode liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung gleiche der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) == 0}. */
	public static <T> Filter3<T> comparableAsEqFilter(Comparable<? super T> that) throws NullPointerException {
		notNull(that);
		return (item) -> that.compareTo(item) == 0;
	}

	/** Diese Klasse liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung kleiner als die des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) > 0}. */
	public static <T> Filter3<T> comparableAsLtFilter(Comparable<? super T> that) throws NullPointerException {
		notNull(that);
		return (item) -> that.compareTo(item) > 0;
	}

	/** Diese Klasse liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung kleiner als die oder gleich der des gegebenen
	 * {@link Comparable} ist. Die Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) >= 0}. */
	public static <T> Filter3<T> comparableAsLtEqFilter(Comparable<? super T> that) throws NullPointerException {
		notNull(that);
		return (item) -> that.compareTo(item) >= 0;
	}

	/** Diese Klasse implementiert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung größer der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) < 0}.
	 *
	 * @param <T> Typ der Datensätze. */
	public static <T> Filter3<T> comparableAsGtFilter(Comparable<? super T> that) throws NullPointerException {
		notNull(that);
		return (item) -> that.compareTo(item) < 0;
	}

	/** Diese Klasse implementiert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung größer als die oder gleich der des gegebenen
	 * {@link Comparable} ist. Die Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) <= 0}.
	 *
	 * @param <T> Typ der Datensätze. */
	public static <T> Filter3<T> comparableAsGtEqFilter(Comparable<? super T> that) throws NullPointerException {
		notNull(that);
		return (item) -> that.compareTo(item) <= 0;
	}

	/** Diese Methode liefert ein {@link Comparable3}, welches stets den Navigationswert {@code 0} liefert. */
	@SuppressWarnings ("unchecked")
	public static <T> Comparable3<T> neutralComparable() {
		return (Comparable3<T>)neutralComparable;
	}

	/** Diese Methode liefert ein verkettetes {@link Comparable3}, welches den Navigationswert von {@code that1} liefert, sofern dieser ungleich {@code 0} ist,
	 * und sonst den von {@code that2} verwendet. */
	public static <T> Comparable3<T> concatComparable(Comparable<? super T> that1, Comparable<? super T> that2) throws NullPointerException {
		notNull(that1);
		notNull(that2);
		return item -> {
			var result = that1.compareTo(item);
			if (result != 0) return result;
			return that2.compareTo(item);
		};
	}

	/** Diese Methode liefert ein {@link Comparable3}, welches den Navigationswert des gegebenen {@link Comparable} mit umgekehrten Vorzeichen liefert. */
	public static <T> Comparable3<T> reversedComparable(Comparable<? super T> that) throws NullPointerException {
		notNull(that);
		return item -> -that.compareTo(item);
	}

	/** Diese Methode liefert ein übersetztes {@link Comparable3}, welches seine Eingabe über den gegebenen {@link Getter} in die Eingabe des gegebenen
	 * {@link Comparable} überführt und dessen Navigationswert liefert. Der Navigationswert für eine Eingabe {@code item} ist
	 * {@code that.compareTo(trans.get(item))}. */
	public static <T2, T> Comparable3<T> translatedComparable(Comparable<? super T2> that, Getter<? super T, ? extends T2> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return item -> that.compareTo(trans.get(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalizedComparable(Comparable, boolean) optionalizedComparable(that, true)}. */
	public static <T> Comparable3<T> optionalizedComparable(Comparable<? super T> that) throws NullPointerException {
		return optionalizedComparable(that, true);
	}

	/** Diese Methode liefert ein {@link Comparable3}, das den {@link Comparable3#compareTo(Object) Navigationswert} des gegebenen {@link Comparable} liefert,
	 * sofern das abzugleichende Objekt nicht {@code null} ist. Andernfalls wird {@code null} abhängig von {@code first} als kleinster ({@code true}) bzw. größter
	 * ({@code false}) Wert erkannt. */
	public static <T> Comparable3<T> optionalizedComparable(Comparable<? super T> that, boolean first) throws NullPointerException {
		notNull(that);
		return first ? (item -> item == null ? 1 : that.compareTo(item)) : (item -> item == null ? -1 : that.compareTo(item));
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearch(Object[], Comparable, int, int)
	 * @param <T> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist. */
	public static <T> int binarySearch(T[] items, Comparable<? super T> comparable) throws ClassCastException, NullPointerException {
		return binarySearch(items, comparable, 0, items.length);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code minInclIndex < 0} oder {@code maxExclIndex > list.length}. */
	public static <T> int binarySearch(T[] items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkItems(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items[next]);
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
	 * @param <T> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist. */
	public static <T> int binarySearch(List<? extends T> items, Comparable<? super T> comparable) throws NullPointerException, ClassCastException {
		return binarySearch(items, comparable, 0, items.size());
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code minInclIndex < 0} oder {@code maxExclIndex > list.size()}. */
	public static <T> int binarySearch(List<? extends T> items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkList(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
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
	 * @param <T> Typ der Elemente.
	 * @param items {@link Array} als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Array} ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Array} eine {@link IndexOutOfBoundsException} auslöst. */
	public static <T> int binarySearch(Array<? extends T> items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkArray(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
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
	 * @param <T> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist. */
	public static <T> int binarySearchFirst(T[] items, Comparable<? super T> comparable) throws NullPointerException, ClassCastException {
		return binarySearchFirst(items, comparable, 0, items.length);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code minInclIndex < 0} oder {@code maxExclIndex > list.length}. */
	public static <T> int binarySearchFirst(T[] items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkItems(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items[next]);
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((minInclIndex <= from) && (from < maxExclIndex) && (comparable.compareTo(items[from]) == 0)) return from;
		return -(from + 1);
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(List, Comparable, int, int)
	 * @param <T> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist. */
	public static <T> int binarySearchFirst(List<? extends T> items, Comparable<? super T> comparable) throws NullPointerException, ClassCastException {
		return binarySearchFirst(items, comparable, 0, items.size());
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code minInclIndex < 0} oder {@code maxExclIndex > list.size()}. */
	public static <T> int binarySearchFirst(List<? extends T> items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkList(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((minInclIndex <= from) && (from < maxExclIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -(from + 1);
	}

	/** Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items {@link Array} als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Array} ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Array} eine {@link IndexOutOfBoundsException} auslöst. */
	public static <T> int binarySearchFirst(Array<? extends T> items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkArray(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp <= 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		if ((minInclIndex <= from) && (from < maxExclIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -(from + 1);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchLast(Object[], Comparable, int, int)
	 * @param <T> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist. */
	public static <T> int binarySearchLast(T[] items, Comparable<? super T> comparable) throws NullPointerException, ClassCastException {
		return binarySearchLast(items, comparable, 0, items.length);
	}

	/** Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position
	 * eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code minInclIndex < 0} oder {@code maxExclIndex > list.length}. */
	public static <T> int binarySearchLast(T[] items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkItems(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items[next]);
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((minInclIndex <= from) && (from < maxExclIndex) && (comparable.compareTo(items[from]) == 0)) return from;
		return -from - 2;
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @see Comparables#binarySearchLast(List, Comparable, int, int)
	 * @param <T> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist. */
	public static <T> int binarySearchLast(List<? extends T> items, Comparable<? super T> comparable) throws NullPointerException, ClassCastException {
		return binarySearchLast(items, comparable, 0, items.size());
	}

	/** Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code minInclIndex < 0} oder {@code maxExclIndex > list.size()}. */
	public static <T> int binarySearchLast(List<? extends T> items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkList(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((minInclIndex <= from) && (from < maxExclIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -from - 2;
	}

	/** Diese Methode führt auf dem gegebenen {@link Array} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <T> Typ der Elemente.
	 * @param items {@link Array} als Suchraum.
	 * @param minInclIndex Anfang des Suchraums (inklusiv).
	 * @param maxExclIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Array} ist.
	 * @throws IllegalArgumentException Wenn {@code minInclIndex > maxExclIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Array} eine {@link IndexOutOfBoundsException} auslöst. */
	public static <T> int binarySearchLast(Array<? extends T> items, Comparable<? super T> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, ClassCastException, IllegalArgumentException, IndexOutOfBoundsException {
		checkArray(items, comparable, minInclIndex, maxExclIndex);
		int from = minInclIndex, last = maxExclIndex;
		while (from < last) {
			int next = (from + last) >>> 1, comp = comparable.compareTo(items.get(next));
			if (comp < 0) {
				last = next;
			} else {
				from = next + 1;
			}
		}
		from--;
		if ((minInclIndex <= from) && (from < maxExclIndex) && (comparable.compareTo(items.get(from)) == 0)) return from;
		return -from - 2;
	}

	private static final Comparable3<?> neutralComparable = item -> 0;

	private static void checkOrder(int minInclIndex, int maxExclIndex) throws IllegalArgumentException {
		if (minInclIndex > maxExclIndex) throw new IllegalArgumentException("minInclIndex > maxExclIndex");
	}

	private static void checkRange(int length, int minInclIndex, int maxExclIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
		checkOrder(minInclIndex, maxExclIndex);
		if (minInclIndex < 0) throw new IndexOutOfBoundsException("minInclIndex < 0");
		if (maxExclIndex > length) throw new IndexOutOfBoundsException("maxExclIndex > length");
	}

	private static void checkList(List<?> items, Comparable<?> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		notNull(comparable);
		checkRange(items.size(), minInclIndex, maxExclIndex);
	}

	private static void checkArray(Array<?> items, Comparable<?> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, IllegalArgumentException {
		notNull(items);
		notNull(comparable);
		checkOrder(minInclIndex, maxExclIndex);
	}

	private static void checkItems(Object[] items, Comparable<?> comparable, int minInclIndex, int maxExclIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		notNull(comparable);
		checkRange(items.length, minInclIndex, maxExclIndex);
	}

}
