package bee.creative.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** Diese Klasse implementiert mehrere Hilfsfunktionen zur stabilen binären Suche mit {@link Comparable} als Suchkriterium sowie zur Erzeugung von
 * {@link Comparable}.
 * <p>
 * <a name="comparables_old"><u>Binäre Suche mit {@link Arrays} und {@link Collections}</u></a>
 * <p>
 * Die Schwächen der Hilfsmethoden {@link Arrays#binarySearch(Object[], Object, Comparator)} und {@link Collections#binarySearch(List, Object, Comparator)}
 * liegen zum einen bei ihrer Beschränkung auf Suchkriterien, die zum Typ der Elemente in den Arrays bzw. Listen kompatibel sein müssen, und zum anderen im
 * Indeterminismus bei merhfach vorkommenden Elementen.
 * <p>
 * <a name="comparables_new"><u>Binäre Suche mit {@link Comparables}</u></a>
 * <p>
 * Die hier in {@link Comparables} implementierten Hilfsmethoden zur binären Suche abstrahieren Suchkriterien als {@link Comparable}, welche zu einem gegebenen
 * Element einen Navigationswert berechnen. Bei einer binären Suche mit einem {@link Comparable} {@code comparable} als Suchkriterium gilt ein Element
 * {@code element} an Position {@code index} als Treffer, wenn der Navigationswert {@code comparable.compareTo(element)} gleich {@code 0} ist. Wenn der
 * Navigationswert dagegen kleier oder größer als {@code 0} ist, wird die binäre Suche bei den Positionen kleier bzw. größer als {@code index} fortgesetzt. Bei
 * einer erfolglosen benären Suche geben die Hilfsmethoden <code>(-(<em>Einfügeposition</em>)-1)</code> zurück, sodass ein positiver Rückgabewert immer einen
 * Treffer signalisiert. Die <em>Einfügeposition</em> ist dabei die Position, an der ein Element seiner Ordnung entsprechend in das Array bzw. die Liste
 * eingefügt werden müsste. Das gegebene Array bzw. die gegebene Liste muss bezüglich der Ordnung des {@link Comparable} aufsteigend sortiert sein.
 * <p>
 * Neben den für merhfach vorkommende Elemente indeterministischen {@code binarySearch()}-Methoden gibt es hier auch die deterministischen
 * {@code binarySearchFirst()}- und {@code binarySearchLast()}-Methoden, welche nach der kleinsten bzw. größten Position eines Treffers suchen.
 *
 * @see Arrays#binarySearch(Object[], Object)
 * @see Arrays#binarySearch(Object[], Object, Comparator)
 * @see Collections#binarySearch(List, Object)
 * @see Collections#binarySearch(List, Object, Comparator)
 * @see Comparable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Comparables {

	/** Diese Schnittstelle definiert eine Methode zum Lesen eines Element zu einem gegebenen Index.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static interface Items<GItem> {

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 *
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist. */
		public GItem get(int index) throws IndexOutOfBoundsException;

	}

	/** Diese Klasse implementiert {@link Comparables#itemsSection(Items, int, int)} */
	@SuppressWarnings ("javadoc")
	public static class ItemsSection<GItem> implements Items<GItem> {

		public final Items<? extends GItem> items;

		public final int fromIndex;

		public final int toIndex;

		public ItemsSection(final Items<? extends GItem> items, final int fromIndex, final int toIndex) {
			Comparables.check(fromIndex, toIndex);
			this.items = Objects.notNull(items);
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
		}

		@Override
		public GItem get(int index) throws IndexOutOfBoundsException {
			if (index < 0) throw new IndexOutOfBoundsException();
			index += this.fromIndex;
			if (index >= this.toIndex) throw new IndexOutOfBoundsException();
			return this.items.get(index);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.items, this.fromIndex, this.toIndex);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#itemsSelection(Items, int[])} */
	@SuppressWarnings ("javadoc")
	public static class ItemsSelection<GItem> implements Items<GItem> {

		public final Items<? extends GItem> items;

		public final int[] indices;

		public ItemsSelection(final Items<? extends GItem> items, final int[] indices) {
			this.items = Objects.notNull(items);
			this.indices = Objects.notNull(indices);
		}

		@Override
		public GItem get(final int index) throws IndexOutOfBoundsException {
			return this.items.get(this.indices[index]);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.items, this.indices);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#defaultComparable(Comparable)} */
	@SuppressWarnings ("javadoc")
	public static class DefaultComparable<GItem> implements Comparable<GItem> {

		public final Comparable<? super GItem> comparable;

		public DefaultComparable(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public int compareTo(final GItem item) {
			return item == null ? 1 : this.comparable.compareTo(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#reverseComparable(Comparable)} */
	@SuppressWarnings ("javadoc")
	public static class ReverseComparable<GItem> implements Comparable<GItem> {

		public final Comparable<? super GItem> comparable;

		public ReverseComparable(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public int compareTo(final GItem item) {
			return -this.comparable.compareTo(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#iterableComparable(Comparable)} */
	@SuppressWarnings ("javadoc")
	public static class IterableComparable<GItem> implements Comparable<Iterable<? extends GItem>> {

		public final Comparable<? super GItem> comparable;

		public IterableComparable(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public int compareTo(final Iterable<? extends GItem> that) {
			for (final GItem item: that) {
				final int result = this.comparable.compareTo(item);
				if (result != 0) return result;
			}
			return 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#chainedComparable(Comparable, Comparable)} */
	@SuppressWarnings ("javadoc")
	public static class ChainedComparable<GItem> implements Comparable<GItem> {

		public final Comparable<? super GItem> comparable1;

		public final Comparable<? super GItem> comparable2;

		public ChainedComparable(final Comparable<? super GItem> comparable1, final Comparable<? super GItem> comparable2) {
			this.comparable1 = Objects.notNull(comparable1);
			this.comparable2 = Objects.notNull(comparable2);
		}

		@Override
		public int compareTo(final GItem item) {
			final int result = this.comparable1.compareTo(item);
			if (result != 0) return result;
			return this.comparable2.compareTo(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable1, this.comparable2);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#translatedComparable(Getter, Comparable)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedComparable<GSource, GTarget> implements Comparable<GSource> {

		public final Getter<? super GSource, ? extends GTarget> toSource;

		public final Comparable<? super GTarget> comparable;

		public TranslatedComparable(final Getter<? super GSource, ? extends GTarget> toSource, final Comparable<? super GTarget> comparable) {
			this.toSource = Objects.notNull(toSource);
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public int compareTo(final GSource item) {
			return this.comparable.compareTo(this.toSource.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.comparable);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#toLowerFilter(Comparable)} */
	@SuppressWarnings ("javadoc")
	static class LowerFilter<GItem> implements Filter<GItem> {

		public final Comparable<? super GItem> comparable;

		public LowerFilter(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.comparable.compareTo(item) >= 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#toHigherFilter(Comparable)} */
	@SuppressWarnings ("javadoc")
	static class HigherFilter<GItem> implements Filter<GItem> {

		public final Comparable<? super GItem> comparable;

		public HigherFilter(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.comparable.compareTo(item) <= 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

	}

	/** Diese Klasse implementiert {@link Comparables#toEqualFilter(Comparable)} */
	@SuppressWarnings ("javadoc")
	static class EqualFilter<GItem> implements Filter<GItem> {

		public final Comparable<? super GItem> comparable;

		public EqualFilter(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.comparable.compareTo(item) == 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}

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
	static void check(final Items<?> items, final Comparable<?> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		Objects.notNull(items);
		Objects.notNull(comparable);
		Comparables.check(fromIndex, toIndex);
	}

	@SuppressWarnings ("javadoc")
	static void check(final List<?> items, final Comparable<?> comparable, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
		Objects.notNull(comparable);
		Comparables.check(items.size(), fromIndex, toIndex);
	}

	@SuppressWarnings ("javadoc")
	static void check(final Object[] items, final Comparable<?> comparable, final int fromIndex, final int toIndex)
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
	public static <GItem> int binarySearch(final GItem[] items, final Comparable<? super GItem> comparable) throws ClassCastException, NullPointerException {
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

	/** Diese Methode führt auf dem gegebenen {@link Items} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die Position des
	 * ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return Position des ersten Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Items} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Items} eine {@link IndexOutOfBoundsException} auslöst. */
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

	/** Diese Methode führt auf dem gegebenen {@link Items} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die kleinste
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return kleinste Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Items} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Items} eine {@link IndexOutOfBoundsException} auslöst. */
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

	/** Diese Methode führt auf dem gegebenen {@link Items} eine binäre Suche mit dem gegebenen {@link Comparable} als Suchkriterium aus und gibt die größte
	 * Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code> zurück.
	 *
	 * @see Comparables
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items} als Suchraum.
	 * @param fromIndex Anfang des Suchraums (inklusiv).
	 * @param toIndex Ende des Suchraums (exklusiv).
	 * @param comparable {@link Comparable}.
	 * @return größte Position eines Treffers oder <code>(-(<em>Einfügeposition</em>)-1)</code>.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code comparable} {@code null} ist.
	 * @throws ClassCastException Wenn der gegebene {@link Comparable} inkompatibel mit den Elementen des gegebenen {@link Items} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}.
	 * @throws IndexOutOfBoundsException Wenn das gegebene {@link Items} eine {@link IndexOutOfBoundsException} auslöst. */
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

	/** Diese Methode gibt einen Abschnitt der gegebenen Elemente zurück. Die Methode {@link Items#get(int)} liefert hierbei {@code items.get(index + fromIndex)}
	 * für alle gültigen Indizes.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @param fromIndex Index des ersten aus {@code items} gelieferten Elements, d.h für {@code get(0)} des erzeigten Abschnitts.
	 * @param toIndex Index nach dem letzten aus {@code items} gelieferten Elements.
	 * @return Auswahl der Elemente.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}. */
	public static <GItem> Items<GItem> itemsSection(final Items<? extends GItem> items, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		return new ItemsSection<>(items, fromIndex, toIndex);
	}

	/** Diese Methode gibt eine beliebig sortierte Sicht auf die Elemente des gegebenen {@link Items}. Die Methode {@link Items#get(int)} liefert hierbei
	 * {@code items.get(indices[index])}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param items Elemente.
	 * @param indices Indices.
	 * @return Auswahl der Elemente.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code indices} {@code null} ist. */
	public static <GItem> Items<GItem> itemsSelection(final Items<? extends GItem> items, final int[] indices) throws NullPointerException {
		return new ItemsSelection<>(items, indices);
	}

	/** Diese Methode gibt einen {@link Comparable} zurück, der {@code null}-Eingaben als minimal betrachtet und alle anderen Eingaben an das gegebene
	 * {@link Comparable} delegiert. Der Navigationswert für eine Eingaben {@code item} ist {@code ((item == null) ? 1 : comparable.compareTo(item))}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param comparable {@link Comparable}.
	 * @return {@code default}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GItem> Comparable<GItem> defaultComparable(final Comparable<? super GItem> comparable) throws NullPointerException {
		return new DefaultComparable<>(comparable);
	}

	/** Diese Methode gibt einen {@link Comparable} zurück, der den Navigationswert des gegebenen {@link Comparable} umkehrt. Der Navigationswert für eine Eingabe
	 * {@code item} ist {@code -comparable.compareTo(item)}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param comparable {@link Comparable}.
	 * @return {@code reverse}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GItem> Comparable<GItem> reverseComparable(final Comparable<? super GItem> comparable) throws NullPointerException {
		return new ReverseComparable<>(comparable);
	}

	/** Diese Methode gibt einen {@link Comparable} zurück, der das gegebenen {@link Comparable} auf jedes Element der iterierbaren Eingabe anwendet und den
	 * ersten Navigationswert ungleich {@code 0} liefert und welcher bei erfolgloser Suche {@code 0} liefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param comparable {@link Comparable}.
	 * @return {@code iterable}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GItem> Comparable<Iterable<? extends GItem>> iterableComparable(final Comparable<? super GItem> comparable) {
		return new IterableComparable<>(comparable);
	}

	/** Diese Methode gibt einen verketteten {@link Comparable} zurück, der den Navigationswert einer Eingabe zuerst über den ersten {@link Comparable} berechnet
	 * und nur dann den zweiten {@link Comparable} verwendet, wenn der erste den Navigationswert {@code 0} ermittelt hat. Der Navigationswert für eine Eingabe
	 * {@code item} ist {@code (comparable1.compareTo(item) != 0 ? comparable1 : comparable2).compareTo(item)}.
	 *
	 * @param <GItem> Typ der Eingabe.
	 * @param comparable1 erster {@link Comparable}.
	 * @param comparable2 zweiter {@link Comparable}.
	 * @return {@code chained}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code comparable1} bzw. {@code comparable2} {@code null} ist. */
	public static <GItem> Comparable<GItem> chainedComparable(final Comparable<? super GItem> comparable1, final Comparable<? super GItem> comparable2)
		throws NullPointerException {
		return new ChainedComparable<>(comparable1, comparable2);
	}

	/** Diese Methode gint einen navigierten {@link Comparable} zurück, der von seiner Eingabe mit dem gegebenen {@link Getter} zur Eingabe des gegebenen
	 * {@link Comparable} navigiert. Der Navigationswert für eine Eingabe {@code item} ist {@code comparable.compareTo(navigator.get(item))}.
	 *
	 * @see Getter
	 * @param <GSource> Typ der Ausgabe des {@link Getter} sowie der Eingabe des gegebenen {@link Comparable}.
	 * @param <GTarget> Typ der Eingabe des {@link Getter}.
	 * @param toSource {@link Getter}.
	 * @param comparable {@link Comparable}.
	 * @return {@code navigated}-{@link Comparable}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code comparable} {@code null} ist. */
	public static <GSource, GTarget> Comparable<GTarget> translatedComparable(final Getter<? super GTarget, ? extends GSource> toSource,
		final Comparable<? super GSource> comparable) throws NullPointerException {
		return new TranslatedComparable<>(toSource, comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung gleich der des gegebenen {@link Comparable} ist.<br>
	 * Die Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) == 0}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code equal}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GItem> Filter<GItem> toEqualFilter(final Comparable<? super GItem> comparable) throws NullPointerException {
		return new EqualFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung kleiner der des gegebenen {@link Comparable} ist.<br>
	 * Die Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) >= 0}.
	 *
	 * @param <GInput> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code lower}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> toLowerFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		return new LowerFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung größer der des gegebenen {@link Comparable} ist.<br>
	 * Die Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) <= 0}.
	 *
	 * @param <GInput> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code higher}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> toHigherFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		return new HigherFilter<>(comparable);
	}

}
