package bee.creative.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Diese Klasse implementiert mehrere Hilfsfunktionen zur binären Suche mit {@link Comparable}s als Suchkriterium sowie zur Erzeugung von {@link Comparable}s.
 * <p>
 * <a name="comparables_old"><u>Binäre Suche mit {@link Arrays} und {@link Collections}</u></a>
 * <p>
 * Die Schwächen der Hilfsmethoden {@link Arrays#binarySearch(Object[], Object, Comparator)} und {@link Collections#binarySearch(List, Object, Comparator)}
 * liegen zum einen bei ihrer Beschränkung auf Suchkriterien, die zum Typ der Elemente in den Arrays bzw. {@link List}s kompatibel sein müssel, und zum anderen
 * im Indererminismus bei merhfach vorkommenden Elemente.
 * <p>
 * <a name="comparables_new"><u>Binäre Suche mit {@link Comparables}</u></a>
 * <p>
 * Die hier in {@link Comparables} implementierten Hilfsmethoden zur binären Suche abstrahieren Suchkriterien als {@link Comparable}s, welche zu einem gegebenen
 * Element einen Navigationswert berechnen. Bei einer binären Suche mit dem einem {@link Comparable} {@code comparable} als Suchkriterium gilt ein Element
 * {@code element} an Position {@code index} als Treffer, wenn der Navigationswert {@code comparable.compareTo(element)} gleich {@code 0} ist. Wenn der
 * Navigationswert dagegen kleier oder größer als {@code 0} ist, wird die binäre Suche bei den Positionen kleier bzw. größer als {@code index} fortgesetzt. Bei
 * einer erfolglosen benären Suche geben die Hilfsmethoden <code>(-(<i>Einfügeposition</i>)-1)</code> zurück, sodass ein positiver Rückgabewert immer einen
 * Treffer signalisiert. Die <i>Einfügeposition</i> ist dabei die Position, an der ein Element seiner Ordnung entsprechend in das Array bzw. die {@link List}
 * eingefügt werden müsste. Das gegebene Array bzw. die gegebene {@link List} muss bezüglich der Ordnung des {@link Comparable}s aufsteigend sortiert sein.
 * <p>
 * Nebn den für merhfach vorkommenden Elemente indererministischen {@code binarySearch()}-Methoden gibt es hier auch die deterministischen
 * {@code binarySearchFirst()}- und {@code binarySearchLast()}-Methoden, welche nach der kleinsten bzw. größten Position eines Treffers suchen.
 * 
 * @see Arrays#binarySearch(Object[], Object)
 * @see Arrays#binarySearch(Object[], Object, Comparator)
 * @see Collections#binarySearch(List, Object)
 * @see Collections#binarySearch(List, Object, Comparator)
 * @see Comparable
 * @author Sebastian Rostock 2011.
 */
public class Comparables {

	/**
	 * Diese Schnittstelle definiert eine Methode zum Lesen eines Element zu einem gegebenen Index.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static interface Items<GItem> {

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public GItem get(int index) throws IndexOutOfBoundsException;

	}

	{}

	@SuppressWarnings ("javadoc")
	static void check(final Comparable<?> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException("comparable = null");
	}

	@SuppressWarnings ("javadoc")
	static void check(final int fromIndex, final int toIndex) throws IllegalArgumentException {
		if (fromIndex > toIndex) throw new IllegalArgumentException("fromIndex > toIndex");
	}

	@SuppressWarnings ("javadoc")
	static void check(final int length, final int fromIndex, final int toIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
		Comparables.check(fromIndex, toIndex);
		if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex < 0");
		if (toIndex > length) throw new IndexOutOfBoundsException("toIndex > length");
	}

	@SuppressWarnings ("javadoc")
	static void check(final Items<?> items, final Comparable<?> comparable, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException {
		if (items == null) throw new NullPointerException("items = null");
		Comparables.check(comparable);
		Comparables.check(fromIndex, toIndex);
	}

	@SuppressWarnings ("javadoc")
	static void check(final List<?> items, final Comparable<?> comparable, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException, IndexOutOfBoundsException {
		if (items == null) throw new NullPointerException("items = null");
		Comparables.check(comparable);
		Comparables.check(items.size(), fromIndex, toIndex);
	}

	@SuppressWarnings ("javadoc")
	static void check(final Object[] items, final Comparable<?> comparable, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException, IndexOutOfBoundsException {
		if (items == null) throw new NullPointerException("items = null");
		Comparables.check(comparable);
		Comparables.check(items.length, fromIndex, toIndex);
	}

	/**
	 * Diese Methode wählt aus den gegebenen Elementen ein Element mit der kleinsten Ordnung und gibt es zurück. Der Navigationswert eines solchen Elements ist
	 * bezogen auf jedes der gegebenen Elemente immer kleiner oder gleich {@code 0}. Bei erfolgloser Sucht wird {@code null} geliefert.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @return Element mit der kleinsten Ordnung oder {@code null}.
	 */
	public static <GItem extends Comparable<? super GItem>> GItem minItem(final Iterable<? extends GItem> items) {
		final Iterator<? extends GItem> iterator = Iterators.iterator(items);
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			final GItem item = iterator.next();
			if (item.compareTo(result) < 0) {
				result = item;
			}
		}
		return result;
	}

	/**
	 * Diese Methode wählt aus den gegebenen Elementen ein Element mit der größten Ordnung und gibt es zurück. Der Navigationswert eines solchen Elements ist
	 * bezogen auf jedes der gegebenen Elemente immer größer oder gleich {@code 0}. Bei erfolgloser Sucht wird {@code null} geliefert.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @return Element mit der größten Ordnung oder {@code null}.
	 */
	public static <GItem extends Comparable<? super GItem>> GItem maxItem(final Iterable<? extends GItem> items) {
		final Iterator<? extends GItem> iterator = Iterators.iterator(items);
		if (!iterator.hasNext()) return null;
		GItem result = iterator.next();
		while (iterator.hasNext()) {
			final GItem item = iterator.next();
			if (item.compareTo(result) > 0) {
				result = item;
			}
		}
		return result;
	}

	/**
	 * Diese Methode gibt eine beliebig sortierte Sicht auf die Elemente des gegebenen {@link Items}. Die Methode {@link Items#get(int)} liefert hierbei
	 * {@code items.get(indices[index])}.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @param indices Indices.
	 * @return Auswahl der Elemente.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code indices} {@code null} ist.
	 */
	public static <GItem> Items<GItem> itemsSection(final Items<? extends GItem> items, final int[] indices) throws NullPointerException {
		if (items == null) throw new NullPointerException("items = null");
		if (indices == null) throw new NullPointerException("indices = null");
		return new Items<GItem>() {

			@Override
			public GItem get(final int index) throws IndexOutOfBoundsException {
				return items.get(indices[index]);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("itemsSection", items, indices);
			}

		};
	}

	/**
	 * Diese Methode gibt einen Abschnitt der gegebenen Elemente zurück. Die Methode {@link Items#get(int)} liefert hierbei {@code items.get(index + fromIndex)}
	 * für alle gültigen Indizes.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @param fromIndex Index des ersten aus {@code items} gelieferten Elements, d.h für {@code get(0)} des erzeigten Abschnitts.
	 * @param toIndex Index nach dem letzten aus {@code items} gelieferten Elements.
	 * @return Auswahl der Elemente.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 */
	public static <GItem> Items<GItem> itemsSection(final Items<? extends GItem> items, final int fromIndex, final int toIndex) throws NullPointerException,
		IllegalArgumentException {
		if (items == null) throw new NullPointerException("items = null");
		Comparables.check(fromIndex, toIndex);
		return new Items<GItem>() {

			@Override
			public GItem get(int index) throws IndexOutOfBoundsException {
				if (index < 0) throw new IndexOutOfBoundsException();
				index += fromIndex;
				if (index >= toIndex) throw new IndexOutOfBoundsException();
				return items.get(index);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("itemsSection", items, fromIndex, toIndex);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Comparable} zurück, der {@code null}-Elemente als minimal betrachtet und alle anderen Eingaben an einen gegebenen
	 * {@link Comparable} delegiert. Der Navigationswert für ein Element {@code item} ist {@code ((item == null) ? 1 : comparable.compareTo(item))}.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param comparable {@link Comparable}.
	 * @return {@code null}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist.
	 */
	public static <GItem> Comparable<GItem> nullComparable(final Comparable<? super GItem> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException("comparable = null");
		return new Comparable<GItem>() {

			@Override
			public int compareTo(final GItem item) {
				return item == null ? 1 : comparable.compareTo(item);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("nullComparable", comparable);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Comparable} zurück, der den gegebenen {@link Comparator} sowie das gegebene Element zur Berechnung des Navigationswert
	 * verwendet. Das gegebene Element wird als erstes Argument des {@link Comparator}s verwendet. Der Navigationswert für ein Element {@code item2} ist
	 * {@code comparator.compare(item, item2)}.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param item erstes Argument des {@link Comparator}s.
	 * @param comparator {@link Comparator}.
	 * @return {@code entry}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparator} {@code null} ist.
	 */
	public static <GItem> Comparable<GItem> itemComparable(final GItem item, final Comparator<? super GItem> comparator) throws NullPointerException {
		if (comparator == null) throw new NullPointerException("comparator = null");
		return new Comparable<GItem>() {

			@Override
			public int compareTo(final GItem item2) {
				return comparator.compare(item, item2);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("itemComparable", item, comparator);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Comparable} zurück, der den Navigationswert des gegebenen {@link Comparable} umkehrt. Der Navigationswert für ein Element
	 * {@code item} ist {@code - comparable.compareTo(item)}.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param comparable {@link Comparable}.
	 * @return {@code reverse}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist.
	 */
	public static <GItem> Comparable<GItem> reverseComparable(final Comparable<? super GItem> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException("comparable = null");
		return new Comparable<GItem>() {

			@Override
			public int compareTo(final GItem item) {
				return -comparable.compareTo(item);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("reverseComparable", comparable);
			}

		};
	}

	/**
	 * Diese Methode gibt einen verketteten {@link Comparable} zurück, der den Navigationswert eines Elements zuerst über den ersten {@link Comparable} berechnet
	 * und nur dann den zweiten {@link Comparable} verwendet, wenn der erste {@link Comparable} den Navigationswert {@code 0} ermittelt hat. Der Navigationswert
	 * für ein Element {@code item} ist {@code (comparable1.compareTo(item) != 0) ? comparable1.compareTo(item) : comparable2.compareTo(item)}.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param comparable1 erster {@link Comparable}.
	 * @param comparable2 zweiter {@link Comparable}.
	 * @return {@code chained}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable1} bzw. {@code comparable2} {@code null} ist.
	 */
	public static <GItem> Comparable<GItem> chainedComparable(final Comparable<? super GItem> comparable1, final Comparable<? super GItem> comparable2)
		throws NullPointerException {
		if (comparable1 == null) throw new NullPointerException("comparable1 = null");
		if (comparable2 == null) throw new NullPointerException("comparable2 = null");
		return new Comparable<GItem>() {

			@Override
			public int compareTo(final GItem item) {
				final int result = comparable1.compareTo(item);
				if (result != 0) return result;
				return comparable2.compareTo(item);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("chainedComparable", comparable1, comparable2);
			}

		};
	}

	/**
	 * Diese Methode gint einen navigierten {@link Comparable} zurück, der von seinem Element mit dem gegebenen {@link Converter} zum Element des gegebenen
	 * {@link Comparable} navigiert. Der Navigationswert für ein Element {@code item} ist {@code comparable.compareTo(converter.convert(item))}.
	 * 
	 * @see Converter
	 * @param <GItem> Typ der Eingabe des {@link Converter}s sowie der Elemente.
	 * @param <GItem2> Typ der Ausgabe des {@link Converter}s sowie der Elemente des gegebenen {@link Comparable}s.
	 * @param converter {@link Converter}.
	 * @param comparable {@link Comparable}.
	 * @return {@code navigated}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code comparable} {@code null} ist.
	 */
	public static <GItem, GItem2> Comparable<GItem> navigatedComparable(final Converter<? super GItem, ? extends GItem2> converter,
		final Comparable<? super GItem2> comparable) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		if (comparable == null) throw new NullPointerException("comparable = null");
		return new Comparable<GItem>() {

			@Override
			public int compareTo(final GItem item) {
				return comparable.compareTo(converter.convert(item));
			}

			@Override
			public String toString() {
				return Objects.toStringCall("navigatedComparable", converter, comparable);
			}

		};
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearch(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 */
	public static <GItem> int binarySearch(final GItem[] items, final Comparable<? super GItem> comparable) throws ClassCastException, NullPointerException {
		return Comparables.binarySearch(items, comparable, 0, items.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des ersten
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
	public static <GItem> int binarySearch(final GItem[] items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
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

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearch(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 */
	public static <GItem> int binarySearch(final List<? extends GItem> items, final Comparable<? super GItem> comparable) throws NullPointerException,
		ClassCastException {
		return Comparables.binarySearch(items, comparable, 0, items.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
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

	/**
	 * Diese Methode führt auf dem gegebenen {@link Items} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Items} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Items} eine {@link IndexOutOfBoundsException} auslöst.
	 */
	public static <GItem> int binarySearch(final Items<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex, final int toIndex)
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

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 */
	public static <GItem> int binarySearchFirst(final GItem[] items, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearchFirst(items, comparable, 0, items.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste Position
	 * eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
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

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchFirst(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 */
	public static <GItem> int binarySearchFirst(final List<? extends GItem> items, final Comparable<? super GItem> comparable) throws NullPointerException,
		ClassCastException {
		return Comparables.binarySearchFirst(items, comparable, 0, items.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
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

	/**
	 * Diese Methode führt auf dem gegebenen {@link Items} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Items} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Items} eine {@link IndexOutOfBoundsException} auslöst.
	 */
	public static <GItem> int binarySearchFirst(final Items<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
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

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position eines
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchLast(Object[], Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 */
	public static <GItem> int binarySearchLast(final GItem[] items, final Comparable<? super GItem> comparable) throws NullPointerException, ClassCastException {
		return Comparables.binarySearchLast(items, comparable, 0, items.length);
	}

	/**
	 * Diese Methode führt auf dem gegebenen Array eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte Position eines
	 * Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items Array als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen Arrays ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.length}.
	 */
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

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @see Comparables#binarySearchLast(List, Comparable, int, int)
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 */
	public static <GItem> int binarySearchLast(final List<? extends GItem> items, final Comparable<? super GItem> comparable) throws NullPointerException,
		ClassCastException {
		return Comparables.binarySearchLast(items, comparable, 0, items.size());
	}

	/**
	 * Diese Methode führt auf der gegebenen {@link List} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen der gegebenen {@link List} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn {@code fromIndex < 0} oder {@code toIndex > list.size()}.
	 */
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

	/**
	 * Diese Methode führt auf dem gegebenen {@link Items} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code> zurück.
	 * 
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<i>Einfügeposition</i>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Items} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Items} eine {@link IndexOutOfBoundsException} auslöst.
	 */
	public static <GItem> int binarySearchLast(final Items<? extends GItem> items, final Comparable<? super GItem> comparable, final int fromIndex,
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

}
