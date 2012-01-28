package bee.creative.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class Compact {

	static void run(final String name, final Runnable runnable) {
		// runnable.run();
		System.out.print(name);
		System.out.print(": ");
		System.out.println(new Tester(runnable));
	}

	public static void main(final String[] args) {
		NavigableSet<Integer> set = new CompactNavigableSet<Integer>(Comparators.naturalComparator());
		set.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
		set = set.subSet(0, true, 10, true);
		System.out.println(set);
		System.out.println(set.descendingSet());
		System.out.println(set.descendingSet().descendingSet());

		NavigableSet<Integer> ascSub;

		System.out.println(ascSub = set.subSet(2, true, 5, true).descendingSet());

		for(final Integer i: set){
			System.out.println(i + ": " + ascSub.contains(i));
		}

		System.out.println(ascSub = set.subSet(2, true, 5, false).descendingSet());

		for(final Integer i: set){
			System.out.println(i + ": " + ascSub.contains(i));
		}

		System.out.println(ascSub = set.subSet(2, false, 5, true).descendingSet());

		for(final Integer i: set){
			System.out.println(i + ": " + ascSub.contains(i));
		}

		System.out.println(ascSub = set.subSet(2, false, 5, false).descendingSet());

		for(final Integer i: set){
			System.out.println(i + ": " + ascSub.contains(i));
		}

		final NavigableSet<Integer> desSet = set.descendingSet();

		// System.out.println(desSet);
		// System.out.println(desSet.subSet(5, true, 2, true));
		// System.out.println(desSet.subSet(5, true, 2, false));
		// System.out.println(desSet.subSet(5, false, 2, true));
		// System.out.println(desSet.subSet(5, false, 2, false));

		if(args != null) return;

		final List<Object> c = new ArrayList<Object>();
		c.addAll(Arrays.asList(Hash.class.getFields()));
		c.addAll(Arrays.asList(Hash.class.getMethods()));
		c.addAll(Arrays.asList(Arrays.class.getFields()));
		c.addAll(Arrays.asList(Arrays.class.getMethods()));
		c.addAll(Arrays.asList(CompactData.class.getFields()));
		c.addAll(Arrays.asList(CompactData.class.getMethods()));

		final HashSet<Object> oldSet = new HashSet<Object>();
		final CompactHashSet<Object> newSet = new CompactHashSet<Object>();

		Compact.run("old.addAll()", new Runnable() {

			@Override
			public void run() {
				oldSet.addAll(c);
			}

		});
		Compact.run("new.addAll()", new Runnable() {

			@Override
			public void run() {
				newSet.addAll(c);
			}

		});

		Compact.run("old.iterator()", new Runnable() {

			@Override
			public void run() {
				Iterators.skip(oldSet.iterator(), -1);
			}

		});
		Compact.run("new.iterator()", new Runnable() {

			@Override
			public void run() {
				Iterators.skip(newSet.iterator(), -1);
			}

		});

		Compact.run("old.addAll()", new Runnable() {

			@Override
			public void run() {
				oldSet.addAll(c);
			}

		});
		Compact.run("new.addAll()", new Runnable() {

			@Override
			public void run() {
				newSet.addAll(c);
			}

		});
		Compact.run("old.clear()", new Runnable() {

			@Override
			public void run() {
				oldSet.clear();
			}

		});
		Compact.run("new.clear()", new Runnable() {

			@Override
			public void run() {
				newSet.clear();
			}

		});

	}

	/**
	 * Diese Klasse implementiert eine abstrakte Sammlung von Elementen, die in einem sortierten {@link Array Array}
	 * verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class CompactData {

		/**
		 * Diese Klasse implementiert ein abstraktes Objekt mit {@link CompactData Compact-Data}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GData> Typ des {@link CompactData Compact-Arrays}.
		 */
		protected static abstract class CompactLink<GData extends CompactData> {

			/**
			 * Dieses Feld speichert das {@link CompactData Compact-Data}.
			 */
			protected final GData data;

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactData Compact-Data}.
			 * 
			 * @param data {@link CompactData Compact-Data}.
			 */
			public CompactLink(final GData data) {
				if(data == null) throw new NullPointerException("Data is null");
				this.data = data;
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten {@link CompactData Compact-Data}-{@link Iterator Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 * @param <GData> Typ des {@link CompactData Compact-Datas}.
		 */
		protected static abstract class CompactIterator<GItem, GData extends CompactData> extends CompactLink<GData>
			implements Iterator<GItem> {

			/**
			 * Dieses Feld speichert den Index des ersten Elements (inklusiv).
			 */
			protected int from;

			/**
			 * Dieses Feld speichert den Index des aktuellen Elements.
			 */
			protected int item;

			/**
			 * Dieses Feld speichert den Index des letztem Elements (exklusiv).
			 */
			protected int last;

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactData Compact-Data} und Indizes.
			 * 
			 * @param data {@link CompactData Compact-Data}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactIterator(final GData data, final int from, final int last) {
				super(data);
				this.from = from;
				this.item = -1;
				this.last = last;
			}

			/**
			 * Diese Methode gibt das {@code index}-te Element zurück.
			 * 
			 * @param index Index.
			 * @return {@code index}-tes Element.
			 */
			protected abstract GItem next(int index);

			/**
			 * Diese Methode entfernt das {@code index}-te Element.
			 * 
			 * @param index Index.
			 */
			protected void remove(final int index) {
				this.data.removeItems(index, 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return this.from < this.last;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				final int item = this.item;
				if(item < 0) throw new IllegalStateException();
				this.remove(item);
				this.item = -1;
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten aufsteigenden {@link CompactData Compact-Data}-{@link Iterator
		 * Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 * @param <GData> Typ des {@link CompactData Compact-Datas}.
		 */
		protected static abstract class CompactAscendingIterator<GItem, GData extends CompactData> extends
			CompactIterator<GItem, GData> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactData Compact-Data} und Indizes.
			 * 
			 * @param data {@link CompactData Compact-Data}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingIterator(final GData data, final int from, final int last) {
				super(data, from - 1, last - 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem next() {
				return this.next(this.item = (this.from = this.from + 1));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				super.remove();
				this.from--;
				this.last--;
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten absteigenden {@link CompactData Compact-Data}-{@link Iterator
		 * Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 * @param <GData> Typ des {@link CompactData Compact-Datas}.
		 */
		protected static abstract class CompactDescendingIterator<GItem, GData extends CompactData> extends
			CompactIterator<GItem, GData> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactData Compact-Data} und Indizes.
			 * 
			 * @param array {@link CompactData Compact-Data}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactDescendingIterator(final GData array, final int from, final int last) {
				super(array, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem next() {
				return this.next(this.item = (this.last = this.last - 1));
			}

		}

		/**
		 * Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactData Compact-Datas}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GData> Typ des {@link CompactData Compact-Datas}.
		 */
		protected static abstract class CompactSubData<GData extends CompactData> extends CompactLink<GData> {

			/**
			 * Dieses Feld speichert das Objekt zur offenen Begrenzung sortierter Listen.
			 */
			protected static final Object OPEN = new Object();

			/**
			 * Dieses Feld speichert das erste Element oder {@link CompactSubData#OPEN}.
			 */
			protected Object fromItem;

			/**
			 * Dieses Feld speichert {@code true}, wenn das erste Element inklusiv ist.
			 */
			protected boolean fromInclusive;

			/**
			 * Dieses Feld speichert das letzte Element oder {@link CompactSubData#OPEN}.
			 */
			protected Object lastItem;

			/**
			 * Dieses Feld speichert {@code true}, wenn das letzte Element inklusiv ist.
			 */
			protected boolean lastInclusive;

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactData Compact-Data} und die Grenzen und deren Inklusion.
			 * 
			 * @param data {@link CompactData Compact-Data}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */
			public CompactSubData(final GData data, final Object fromItem, final boolean fromInclusive,
				final Object lastItem, final boolean lastInclusive) {
				super(data);
				if(fromItem != CompactSubData.OPEN){
					if(lastItem != CompactSubData.OPEN){
						if(data.compare(fromItem, 0, lastItem) > 0) throw new IllegalArgumentException("FromItem > FastItem");
					}else{
						data.compare(fromItem, 0, fromItem);
					}
				}else if(lastItem != CompactSubData.OPEN){
					data.compare(lastItem, 0, lastItem);
				}
				this.fromItem = fromItem;
				this.fromInclusive = fromInclusive;
				this.lastItem = lastItem;
				this.lastInclusive = lastInclusive;
			}

			/**
			 * Diese Methode gibt nr dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und
			 * das {@code index}-te Element zu klein sind.
			 * 
			 * @see CompactSubData#isTooLow(Object)
			 * @param index Index.
			 * @return {@code true}, wenn der gegebene Index zu groß bzw. das {@code index}-te Element zu klein ist.
			 */
			protected final boolean isTooLow(final int index) {
				final GData array = this.data;
				return (index >= array.size) || ((index >= 0) && this.isTooLow(array.items[index]));
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu klein ist. Wenn das erste Element
			 * gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu klein sein. Anderenfalls gilt es als
			 * zu klein, wenn es entweder kleiner als das erste Element ist oder wenn das erste Element exklusiv ist und das
			 * gegebene Element gleich dem ersten Element ist.
			 * 
			 * @see CompactData#compare(Object, int, Object)
			 * @see CompactSubData#fromItem
			 * @see CompactSubData#fromInclusive
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element zu klein ist.
			 */
			protected final boolean isTooLow(final Object key) {
				final Object fromItem = this.fromItem;
				if(fromItem == CompactSubData.OPEN) return false;
				final int comp = this.data.compare(key, 0, fromItem);
				return ((comp < 0) || ((comp == 0) && !this.fromInclusive));
			}

			/**
			 * Diese Methode gibt nr dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und
			 * das {@code index}-te Element zu groß sind.
			 * 
			 * @see CompactSubData#isTooHigh(Object)
			 * @param index Index.
			 * @return {@code true}, wenn der gegebene Index bzw. das {@code index}-te Element zu groß ist.
			 */
			protected final boolean isTooHigh(final int index) {
				final GData array = this.data;
				return (index >= array.size) || ((index >= 0) && this.isTooHigh(array.items[index]));
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu groß ist. Wenn das letzte Element
			 * gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu groß sein. Anderenfalls gilt es als zu
			 * groß, wenn es entweder größer als das letzte Element ist oder wenn das letzte Element exklusiv ist und das
			 * gegebene Element gleich dem letzten Element ist.
			 * 
			 * @see CompactData#compare(Object, int, Object)
			 * @see CompactSubData#lastItem
			 * @see CompactSubData#lastInclusive
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element zu groß ist.
			 */
			protected final boolean isTooHigh(final Object key) {
				final Object lastItem = this.lastItem;
				if(lastItem == CompactSubData.OPEN) return false;
				final int comp = this.data.compare(key, 0, lastItem);
				return ((comp > 0) || ((comp == 0) && !this.lastInclusive));
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich liegt. Die
			 * Inklusitivität des ersten bzw. letzten Elements wird beachtet.
			 * 
			 * @see CompactSubData#isTooLow(int)
			 * @see CompactSubData#isTooHigh(Object)
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element im gültigen Bereich liegt.
			 */
			protected final boolean isInRange(final Object key) {
				return !this.isTooLow(key) && !this.isTooHigh(key);
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich (oder auf dessen
			 * Grenzen) liegt.
			 * 
			 * @see CompactSubData#isInRange(Object)
			 * @see CompactSubData#isInClosedRange(Object)
			 * @param key Element.
			 * @param inclusive {@code true}, wenn die Inklusitivität des ersten bzw. letzten Elements beachtet werden soll.
			 * @return {@code true}, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt.
			 */
			protected final boolean isInRange(final Object key, final boolean inclusive) {
				return (inclusive ? this.isInRange(key) : this.isInClosedRange(key));
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich oder auf dessen
			 * Grenzen liegt. Die Inklusitivität des ersten bzw. letzten Elements ignoriert.
			 * 
			 * @see CompactData#compare(Object, int, Object)
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt.
			 */
			protected final boolean isInClosedRange(final Object key) {
				final GData array = this.data;
				final Object fromItem = this.fromItem, lastItem = this.lastItem;
				return ((fromItem == CompactSubData.OPEN) || (array.compare(key, 0, fromItem) >= 0))
					&& ((lastItem == CompactSubData.OPEN) || (array.compare(key, 0, lastItem) <= 0));
			}

			/**
			 * Diese Methode gibt den Index des ersten Elements zurück.
			 * 
			 * @return Index des ersten Elements.
			 */
			protected final int firstIndex() {
				final GData array = this.data;
				final Object fromItem = this.fromItem;
				if(fromItem == CompactSubData.OPEN) return array.firstIndex();
				if(this.fromInclusive) return array.ceilingIndex(fromItem);
				return array.higherIndex(fromItem);
			}

			/**
			 * Diese Methode gibt den Index des letzten Elements zurück.
			 * 
			 * @return Index des letzten Elements.
			 */
			protected final int lastIndex() {
				final GData array = this.data;
				final Object lastItem = this.lastItem;
				if(lastItem == CompactSubData.OPEN) return array.lastIndex();
				if(this.lastInclusive) return array.floorIndex(lastItem);
				return array.lowerIndex(lastItem);
			}

			/**
			 * Diese Methode gibt den Index des kleinsten Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück.
			 * 
			 * @see NavigableSet#first()
			 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
			 */
			protected final int lowestIndex() {
				final int index = this.firstIndex();
				if(this.isTooHigh(index)) return -index - 1;
				return index;
			}

			/**
			 * Diese Methode gibt den Index des größten Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück.
			 * 
			 * @see NavigableSet#last()
			 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
			 */
			protected final int highestIndex() {
				final int index = this.lastIndex();
				if(this.isTooLow(index)) return -index - 1;
				return index;
			}

			/**
			 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Wenn kein solches
			 * Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
			 * 
			 * @see NavigableSet#lower(Object)
			 * @param item Element.
			 * @return Index des größten Elements, dass kleiner dem gegebenen ist, oder
			 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
			 */
			protected final int lowerIndex(final Object item) {
				if(this.isTooHigh(item)) return this.highestIndex();
				final int index = this.data.lowerIndex(item);
				if(this.isTooLow(index)) return -index - 1;
				return index;
			}

			/**
			 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebenen ist. Wenn kein
			 * solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
			 * 
			 * @see NavigableSet#floor(Object)
			 * @param item Element.
			 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist, oder
			 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
			 */
			protected final int floorIndex(final Object item) {
				if(this.isTooHigh(item)) return this.highestIndex();
				final int index = this.data.floorIndex(item);
				if(this.isTooLow(index)) return -index - 1;
				return index;
			}

			/**
			 * Diese Methode gibt den Index des größten Elements zurück, dass größer oder gleich dem gegebenen ist. Wenn kein
			 * solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
			 * 
			 * @see NavigableSet#ceiling(Object)
			 * @param item Element.
			 * @return Index des größten Elements, dass größer oder gleich dem gegebenen ist, oder
			 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
			 */
			protected final int ceilingIndex(final Object item) {
				if(this.isTooLow(item)) return this.lowestIndex();
				final int index = this.data.ceilingIndex(item);
				if(this.isTooHigh(index)) return -index - 1;
				return index;
			}

			/**
			 * Diese Methode gibt den Index des größten Elements zurück, dass größer dem gegebenen ist. Wenn kein solches
			 * Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
			 * 
			 * @see NavigableSet#higher(Object)
			 * @param item Element.
			 * @return Index des größten Elements, dass größer dem gegebenen ist, oder
			 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
			 */
			protected final int higherIndex(final Object item) {
				if(this.isTooLow(item)) return this.lowestIndex();
				final int index = this.data.higherIndex(item);
				if(this.isTooHigh(index)) return -index - 1;
				return index;
			}

			/**
			 * Diese Methode leert die Teilmenge.
			 */
			protected final void clearItems() {
				final int fromIndex = this.firstIndex(), lastIndex = this.lastIndex();
				if(fromIndex > lastIndex) return;
				this.data.removeItems(fromIndex, (lastIndex - fromIndex) + 1);
			}

			/**
			 * Diese Methode gibt die Anzahl der Elemente in der Teilmenge zurück.
			 * 
			 * @return Anzahl.
			 */
			protected final int countItems() {
				return (this.lastIndex() - this.firstIndex()) + 1;
			}

		}

		/**
		 * Dieses Feld speichert das leere {@link Array Array} der Elemente.
		 */
		protected static final Object[] ITEMS = new Object[0];

		/**
		 * Diese Methode setzt die Länge des gegebenen {@link Array Array} mit der gegebenen Belegung auf die gegebene Länge
		 * und gibt es zurück. Wenn die gegebene Länge {@code 0} ist, wird {@link CompactData#ITEMS} zurück gegeben. Wenn
		 * die Länge des gegebenen {@link Array Arrays} von der gegebenen Länge abweicht, werden ein neues {@link Array
		 * Array} mit der gegebenen Länge erzeugt, die im gegebenen {@link Array Array} belegten Einträge in das neue
		 * {@link Array Array} kopiert und das neue {@link Array Array} zurück gegeben.
		 * 
		 * @param items {@link Array Array}.
		 * @param size Anzahl der belegten Elemente.
		 * @param length Länge bzw. Kapazität.
		 * @return {@link Array Array} der gegebenen Länge.
		 * @throws NullPointerException Wenn des gegebenen {@link Array Array} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Belegung bzw. die gegebene Länge ungültig ist.
		 */
		public static Object[] resizeItems(final Object[] items, final int size, final int length)
			throws NullPointerException, IllegalArgumentException {
			if(items == null) throw new NullPointerException("Items is null");
			if(length < size) throw new IllegalArgumentException("Length < Size");
			if(length == items.length) return items;
			if(length == 0) return CompactData.ITEMS;
			final Object[] objects = new Object[length];
			System.arraycopy(items, 0, objects, 0, size);
			return objects;
		}

		public static boolean containsItem(final Object[] items, final int size, final Object value) {
			if(value == null){
				for(int i = 0; i < size; i++){
					if(items[i] == null) return true;
				}
			}else{
				for(int i = 0; i < size; i++){
					if(value.equals(items[i])) return true;
				}
			}
			return false;
		}

		/**
		 * Diese Methode fügt in das gegebenen {@link Array Array} mit der gegebenen Belegung an der gegebenen Position die
		 * gegebene Anzahl an Elementen ein, setzt die Länge des {@link Array Array} auf die gegebene Länge und gibt es
		 * zurück. Wenn die Länge des gegebenen {@link Array Arrays} von der gegebenen Länge abweicht, werden ein neues
		 * {@link Array Array} mit der gegebenen Länge erzeugt, die im gegebenen {@link Array Array} belegten Einträge in
		 * das neue {@link Array Array} kopiert und das neue {@link Array Array} zurück gegeben.
		 * 
		 * @param items {@link Array Array}.
		 * @param size Anzahl der belegten Elemente.
		 * @param length Länge bzw. Kapazität.
		 * @param index Index bzw. Position.
		 * @param count Anzahl.
		 * @return {@link Array Array} der gegebenen Länge.
		 * @throws NullPointerException Wenn des gegebenen {@link Array Array} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Belegung, die gegebene Länge, der gegebene Index bzw. die
		 *         gegebene Anzah ungültig ist.
		 */
		public static Object[] insertItems(final Object[] items, final int size, final int length, final int index,
			final int count) throws NullPointerException, IllegalArgumentException {
			if(items == null) throw new NullPointerException("Items is null");
			if(length < size) throw new IllegalArgumentException("Length < Size");
			if((index < 0) || (index > size)) throw new IllegalArgumentException("Index out of range: " + index);
			if(count < 0) throw new IllegalArgumentException("Count out of range: " + count);
			if(count == 0) return items;
			if(length != items.length){
				final Object[] objects = new Object[length];
				System.arraycopy(items, 0, objects, 0, index);
				System.arraycopy(items, index, objects, index + count, size - index);
				return objects;
			}else{
				System.arraycopy(items, index, items, index + count, size - index);
			}
			return items;
		}

		/**
		 * Diese Methode fügt in das gegebenen {@link Array Array} mit der gegebenen Belegung an der gegebenen Position die
		 * gegebene Anzahl an Elementen ein, setzt die Länge des {@link Array Array} auf die gegebene Länge und gibt es
		 * zurück. Wenn die gegebene Länge {@code 0} ist, wird {@link CompactData#ITEMS} zurück gegeben. Wenn die Länge des
		 * gegebenen {@link Array Arrays} von der gegebenen Länge abweicht, werden ein neues {@link Array Array} mit der
		 * gegebenen Länge erzeugt, die im gegebenen {@link Array Array} belegten Einträge in das neue {@link Array Array}
		 * kopiert und das neue {@link Array Array} zurück gegeben.
		 * 
		 * @param items {@link Array Array}.
		 * @param size Anzahl der belegten Elemente.
		 * @param length Länge bzw. Kapazität.
		 * @return {@link Array Array} der gegebenen Länge.
		 * @param index Index bzw. Position.
		 * @param count Anzahl.
		 * @throws NullPointerException Wenn des gegebenen {@link Array Array} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Belegung, die gegebene Länge, der gegebene Index bzw. die
		 *         gegebene Anzah ungültig ist.
		 */
		public static Object[] removeItems(final Object[] items, final int size, final int length, final int index,
			final int count) throws NullPointerException, IllegalArgumentException {
			if(items == null) throw new NullPointerException("Items is null");
			if(length < size) throw new IllegalArgumentException("Length < Size");
			if((index < 0) || (index > size)) throw new IllegalArgumentException("Index out of range: " + index);
			if((count < 0) || (count > size)) throw new IllegalArgumentException("Count out of range: " + count);
			if(count == 0) return items;
			if(length != items.length){
				if(length != 0){
					final Object[] objects = new Object[length];
					System.arraycopy(items, 0, objects, 0, index);
					System.arraycopy(items, index + count, objects, index, size - count - index);
					return objects;
				}else return CompactData.ITEMS;
			}else{
				System.arraycopy(items, index + count, items, index, size - count - index);
				Arrays.fill(items, size - count, size, null);
			}
			return items;
		}

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		protected int size;

		/**
		 * Dieses Feld speichert die Elemente.
		 */
		protected Object[] items = CompactData.ITEMS;

		/**
		 * Diese Methode gibt den Index des ersten Elements zurück. Dieser Index kann den Wert {@code size} annehmen.
		 * 
		 * @see NavigableSet#first()
		 * @return Index des ersten Elements.
		 */
		protected final int firstIndex() {
			return 0;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die
		 * Werte {@code -1} und {@code size} annehmen.
		 * 
		 * @see NavigableSet#lower(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner dem gegebenen ist.
		 */
		protected final int lowerIndex(final Object item) {
			final int index = this.itemIndex(item);
			if(index < 0) return -index - 2;
			return index - 1;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index
		 * kann die Werte {@code -1} und {@code size} annehmen.
		 * 
		 * @see NavigableSet#floor(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist.
		 */
		protected final int floorIndex(final Object item) {
			final int index = this.itemIndex(item);
			if(index < 0) return -index - 2;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser
		 * Index kann den Wert {@code size} annehmen.
		 * 
		 * @see NavigableSet#ceiling(Object)
		 * @param item Element.
		 * @return Index des kleinsten Elements, dass größer oder gleich dem gegebenen ist.
		 */
		protected final int ceilingIndex(final Object item) {
			final int index = this.itemIndex(item);
			if(index < 0) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den
		 * Wert {@code size} annehmen.
		 * 
		 * @see NavigableSet#higher(Object)
		 * @param item Element.
		 * @return Index des kleinsten Elements, dass größer dem gegebenen ist.
		 */
		protected final int higherIndex(final Object item) {
			final int index = this.itemIndex(item);
			if(index < 0) return -index - 1;
			return index + 1;
		}

		/**
		 * Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann deb Wert {@code -1} annehmen.
		 * 
		 * @see NavigableSet#last()
		 * @return Index des letzten Elements.
		 */
		protected final int lastIndex() {
			return this.size - 1;
		}

		/**
		 * Diese Methode sucht nach dem gegebenen Objekt und gibt dessen Index oder
		 * <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem der
		 * Eintrag eingefügt werden müsste.
		 * 
		 * @see CompactData#equalsIndex(Object, int)
		 * @see CompactData#compareIndex(Object, int)
		 * @param item Objekt.
		 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected abstract int itemIndex(final Object item);

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Schlüssel {@link Object#equals(Object)
		 * äquivalent} dem Schlüssel des gegebenen Elements ist.
		 * 
		 * @see Object#hashCode()
		 * @see Object#equals(Object)
		 * @see Objects#hash(Object)
		 * @see Objects#equals(Object, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels oder {@code 0}.
		 * @param item Element.
		 * @return {@link Object#equals(Object) Äquivalenz} der Schlüssel.
		 */
		protected abstract boolean equals(Object key, int hash, Object item);

		/**
		 * Diese Methode sucht zuerst binär und danach linear nach einem Eintrag, dessen Schlüssel gleich dem gegebenen
		 * Schlüssel ist und gibt den Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die
		 * <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste. Ein Element {@code element}
		 * ist dann zum gegebenen Schlüssel gleich, wenn
		 * {@code (itemCompare(key, hash, element) == 0) && itemEquals(key, hash, element)}.
		 * 
		 * @see CompactData#equals(Object, int, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int equalsIndex(final Object key, final int hash) {
			Object item;
			final int index = this.compareIndex(key, hash);
			if(index < 0) return index;
			final Object[] items = this.items;
			if(this.equals(key, hash, items[index])) return index;
			for(int next = index + 1, last = this.size; (next < last) && (this.compare(key, hash, item = items[next]) == 0); next++){
				if(this.equals(key, hash, item)) return next;
			}
			for(int next = index - 1; (0 <= next) && (this.compare(key, hash, item = items[next]) == 0); next--){
				if(this.equals(key, hash, item)) return next;
			}
			return -(index + 1);
		}

		/**
		 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn der gegebene Schlüssel
		 * kleiner, gleich bzw. größer als der Schlüssel des gegebenen Elements ist. Die Berechnung kann auf den Schlüsseln
		 * selbst oder ihren {@link Object#hashCode() Streuwerten} beruhen.
		 * 
		 * @see Integer#compare(int, int)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @param item Element.
		 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der der Schlüssel.
		 */
		protected abstract int compare(Object key, int hash, Object item);

		/**
		 * Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen
		 * Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem
		 * der Eintrag eingefügt werden müsste.
		 * 
		 * @see CompactData#compare(Object, int, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int compareIndex(final Object key, final int hash) {
			int from = 0, last = this.size;
			final Object[] items = this.items;
			while(from < last){
				final int next = (from + last) >>> 1;
				final int comp = this.compare(key, hash, items[next]);
				if(comp < 0){
					last = next;
				}else if(comp > 0){
					from = next + 1;
				}else return next;
			}
			return -(from + 1);
		}

		/**
		 * Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das {@link Array Array} ein.
		 * 
		 * @see CompactData#validInsertLength(int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void insertItems(final int index, final int count) throws IllegalArgumentException {
			final int oldSize = this.size, newSize = oldSize + count;
			this.items = CompactData.insertItems(this.items, oldSize, this.validInsertLength(newSize), index, count);
			this.size = newSize;
		}

		/**
		 * Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem {@link Array Array} mit
		 * der gegebenen Länge der Belegung.
		 * 
		 * @see CompactData#validRemoveLength(int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			final int oldSize = this.size, newSize = oldSize - count;
			this.items = CompactData.removeItems(this.items, oldSize, this.validRemoveLength(newSize), index, count);
			this.size = newSize;
		}

		/**
		 * Diese Methode vergrößert die Kapazität des {@link Array Arrays} der Elemente, sodass dieses die gegebene Anzahl
		 * an Elementen verwalten kann.
		 * 
		 * @see CompactData#validAllocateLength(int)
		 * @param count Anzahl.
		 */
		protected void allocateItems(final int count) {
			this.items = CompactData.resizeItems(this.items, this.size, this.validAllocateLength(count));
		}

		/**
		 * Diese Methode verkleinert die Kapazität des {@link Array Arrays} der Elemente auf das Minimum.
		 * 
		 * @see CompactData#validCompactLength()
		 */
		protected void compactItems() {
			this.items = CompactData.resizeItems(this.items, this.size, this.validCompactLength());
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können.
		 * 
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validLength(final int count) {
			final int oldLength = this.items.length;
			if(oldLength >= count) return oldLength;
			final int newLength = oldLength + (oldLength >> 1);
			if(newLength >= count) return newLength;
			return count;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Einfügen von Elementen aufgerufen.
		 * 
		 * @see CompactData#insertItems(int, int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validInsertLength(final int count) {
			return this.validLength(count);
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Entfernen von Elementen aufgerufen.
		 * 
		 * @see CompactData#removeItems(int, int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validRemoveLength(final int count) {
			return this.items.length;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin alle vorhandenen
		 * Elemente verwalten zu können. Sie wird beim Kompaktieren aufgerufen.
		 * 
		 * @see CompactData#size
		 * @see CompactData#compactItems()
		 * @return Länge.
		 */
		protected int validCompactLength() {
			return this.size;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Reservieren von Elementen aufgerufen.
		 * 
		 * @see CompactData#allocateItems(int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validAllocateLength(final int count) {
			return this.validLength(count);
		}

		/**
		 * Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwalten kann.
		 * 
		 * @param count Anzahl.
		 */
		public void allocate(final int count) {
			this.allocateItems(count);
		}

		/**
		 * Diese Methode verkleinert die Kapazität auf das Minimum.
		 */
		public void compact() {
			this.compactItems();
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Set Set}, dessen Daten in einem {@link Array Array} verwaltet
	 * werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class CompactSet<GItem> extends CompactData implements Set<GItem> {

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} für {@link CompactSet Compact-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static final class CompactSetAscendingIterator<GItem> extends
			CompactAscendingIterator<GItem, CompactSet<GItem>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactSet Compact-Set} und Indizes.
			 * 
			 * @param set {@link CompactSet Compact-Set}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactSetAscendingIterator(final CompactSet<GItem> set, final int from, final int last) {
				super(set, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected GItem next(final int index) {
				return this.data.getItem(index);
			}

		}

		/**
		 * Diese Klasse implementiert den absteigenden {@link Iterator Iterator} für {@link CompactSet Compact-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static final class CompactSetDescendingIterator<GItem> extends
			CompactDescendingIterator<GItem, CompactSet<GItem>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactSet Compact-Set} und Indizes.
			 * 
			 * @param set {@link CompactSet Compact-Set}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactSetDescendingIterator(final CompactSet<GItem> set, final int from, final int last) {
				super(set, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected GItem next(final int index) {
				return this.data.getItem(index);
			}

		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set}.
		 */
		public CompactSet() {
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactSet(final int capacity) {
			this.allocate(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set} mit den gegebenen Elementen.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection Elemente.
		 * @throws NullPointerException Wenn die gegebene {@link Collection Collection} {@code null} ist.
		 */
		public CompactSet(final Collection<? extends GItem> collection) {
			if(collection == null) throw new NullPointerException("Collection is null");
			this.allocate(collection.size());
			this.addAll(collection);
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 */
		@SuppressWarnings ("unchecked")
		protected final GItem getItem(final int index) {
			return (GItem)this.items[index];
		}

		/**
		 * Diese Methode setzt das {@code index}-te Element.
		 * 
		 * @param index Index.
		 * @param item Element.
		 */
		protected final void setItem(final int index, final GItem item) {
			this.items[index] = item;
		}

		/**
		 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
		 * 
		 * @see AbstractSet
		 * @return {@link Set Set}.
		 */
		protected final Set<GItem> getItemSet() {
			return new AbstractSet<GItem>() {

				@Override
				public Iterator<GItem> iterator() {
					return CompactSet.this.iterator();
				}

				@Override
				public int size() {
					return CompactSet.this.size();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.removeItems(0, this.size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return new CompactSetAscendingIterator<GItem>(this, 0, this.size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.size == 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem item) {
			int index = this.itemIndex(item);
			if(index >= 0) return false;
			index = -index - 1;
			this.insertItems(index, 1);
			this.setItem(index, item);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GItem> collection) {
			boolean modified = false;
			for(final GItem item: collection)
				if(this.add(item)){
					modified = true;
				}
			return modified;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object item) {
			final int index = this.itemIndex(item);
			if(index < 0) return false;
			this.removeItems(index, 1);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> collection) {
			boolean modified = false;
			for(final Iterator<?> iterator = this.iterator(); iterator.hasNext();){
				if(collection.contains(iterator.next())){
					iterator.remove();
					modified = true;
				}
			}
			return modified;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> collection) {
			boolean modified = false;
			for(final Iterator<?> iterator = this.iterator(); iterator.hasNext();){
				if(!collection.contains(iterator.next())){
					iterator.remove();
					modified = true;
				}
			}
			return modified;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object key) {
			return this.itemIndex(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> collection) {
			for(final Object item: collection)
				if(!this.contains(item)) return false;
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] toArray() {
			return this.getItemSet().toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T> T[] toArray(final T[] a) {
			return this.getItemSet().toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.getItemSet().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Set<?>)) return false;
			return this.getItemSet().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.getItemSet().toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Object#hashCode() Streuwert} basiertes {@link CompactSet Compact-Set}.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static class CompactHashSet<GItem> extends CompactSet<GItem> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set}.
		 */
		public CompactHashSet() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactHashSet(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set} mit den gegebenen Elementen.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection Elemente.
		 */
		public CompactHashSet(final Collection<? extends GItem> collection) {
			super(collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemIndex(final Object key) {
			if(key == null) return this.equalsIndex(null, 0);
			return this.equalsIndex(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			if(key == null) return item == null;
			return key.equals(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			if(item == null) return hash;
			return Integer.compare(hash, item.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link NavigableSet Navigable-Set}, dessen Daten in einem {@link Array Array}
	 * verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static class CompactNavigableSet<GItem> extends CompactSet<GItem> implements NavigableSet<GItem> {

		/**
		 * Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactNavigableSet Compact-Navigable-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static abstract class CompactNavigableSubSet<GItem> extends CompactSubData<CompactNavigableSet<GItem>>
			implements NavigableSet<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet Compact-Navigable-Set} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param set {@link CompactNavigableSet Compact-Navigable-Set}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */
			public CompactNavigableSubSet(final CompactNavigableSet<GItem> set, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(set, fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
			 * 
			 * @see AbstractSet
			 * @return {@link Set Set}.
			 */
			protected final Set<GItem> getItemSet() {
				return new AbstractSet<GItem>() {

					@Override
					public Iterator<GItem> iterator() {
						return CompactNavigableSubSet.this.iterator();
					}

					@Override
					public int size() {
						return CompactNavigableSubSet.this.size();
					}

				};
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.countItems();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isEmpty() {
				return this.countItems() == 0;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final GItem toElement) {
				return this.subSet(fromElement, true, toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> headSet(final GItem toElement) {
				return this.headSet(toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement) {
				return this.tailSet(fromElement, true);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean contains(final Object item) {
				return this.isInRange(item) && this.data.contains(item);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean add(final GItem item) {
				if(!this.isInRange(item)) throw new IllegalArgumentException("Entry out of range");
				return this.data.add(item);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean remove(final Object item) {
				if(!this.isInRange(item)) return false;
				return this.data.remove(item);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean containsAll(final Collection<?> items) {
				for(final Object item: items)
					if(!this.contains(item)) return false;
				return true;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean addAll(final Collection<? extends GItem> items) {
				boolean modified = false;
				for(final GItem item: items)
					if(this.add(item)){
						modified = true;
					}
				return modified;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean retainAll(final Collection<?> items) {
				boolean modified = false;
				final Iterator<GItem> iterator = this.iterator();
				while(iterator.hasNext()){
					if(!items.contains(iterator.next())){
						iterator.remove();
						modified = true;
					}
				}
				return modified;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean removeAll(final Collection<?> items) {
				boolean modified = false;
				final Iterator<?> iterator = this.iterator();
				while(iterator.hasNext()){
					if(items.contains(iterator.next())){
						iterator.remove();
						modified = true;
					}
				}
				return modified;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void clear() {
				this.clearItems();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] toArray() {
				return this.getItemSet().toArray();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public <T> T[] toArray(final T[] a) {
				return this.getItemSet().toArray(a);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int hashCode() {
				return this.getItemSet().hashCode();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean equals(final Object object) {
				if(object == this) return true;
				if(!(object instanceof Set<?>)) return false;
				return this.getItemSet().equals(object);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return this.getItemSet().toString();
			}

		}

		/**
		 * Diese Klasse implementiert die aufsteigende Teilmenge eines {@link CompactNavigableSet Compact-Navigable-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static final class CompactAscendingSubSet<GItem> extends CompactNavigableSubSet<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet Compact-Navigable-Set} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param array {@link CompactNavigableSet Compact-Navigable-Set}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */
			public CompactAscendingSubSet(final CompactNavigableSet<GItem> array, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(array, fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super GItem> comparator() {
				return this.data.comparator;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem first() {
				return this.data.getItemOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem last() {
				return this.data.getItemOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem lower(final GItem entry) {
				return this.data.getItemOrNull(this.lowerIndex(entry));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem floor(final GItem entry) {
				return this.data.getItemOrNull(this.floorIndex(entry));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem ceiling(final GItem entry) {
				return this.data.getItemOrNull(this.ceilingIndex(entry));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem higher(final GItem entry) {
				return this.data.getItemOrNull(this.higherIndex(entry));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem pollFirst() {
				return this.data.pollItem(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem pollLast() {
				return this.data.pollItem(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GItem> iterator() {
				return new CompactSetAscendingIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> descendingSet() {
				return new CompactDescendingSubSet<GItem>(this.data, this.fromItem, this.fromInclusive, this.lastItem,
					this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GItem> descendingIterator() {
				return new CompactSetDescendingIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
				final boolean toInclusive) {
				if(!this.isInRange(fromElement, fromInclusive)) throw new IllegalArgumentException("FromElement out of range");
				if(!this.isInRange(toElement, toInclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactAscendingSubSet<GItem>(this.data, fromElement, fromInclusive, toElement, toInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
				if(!this.isInRange(toElement, inclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactAscendingSubSet<GItem>(this.data, this.fromItem, this.fromInclusive, toElement, inclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
				if(!this.isInRange(fromElement, inclusive)) throw new IllegalArgumentException("FromElement out of range");
				return new CompactAscendingSubSet<GItem>(this.data, fromElement, inclusive, this.lastItem, this.lastInclusive);
			}

		}

		/**
		 * Diese Klasse implementiert die absteigende Teilmenge eines {@link CompactNavigableSet Compact-Navigable-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static final class CompactDescendingSubSet<GItem> extends CompactNavigableSubSet<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet Compact-Navigable-Set} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param array {@link CompactNavigableSet Compact-Navigable-Set}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */

			public CompactDescendingSubSet(final CompactNavigableSet<GItem> array, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(array, fromItem, fromInclusive, lastItem, lastInclusive);

			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super GItem> comparator() {
				return Collections.reverseOrder(this.data.comparator);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem first() {
				return this.data.getItemOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem last() {
				return this.data.getItemOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem lower(final GItem item) {
				return this.data.getItemOrNull(this.higherIndex(item));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem floor(final GItem item) {
				return this.data.getItemOrNull(this.ceilingIndex(item));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem ceiling(final GItem item) {
				return this.data.getItemOrNull(this.floorIndex(item));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem higher(final GItem item) {
				return this.data.getItemOrNull(this.lowerIndex(item));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem pollFirst() {
				return this.data.pollItem(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem pollLast() {
				return this.data.pollItem(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GItem> iterator() {
				return new CompactSetDescendingIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> descendingSet() {
				return new CompactAscendingSubSet<GItem>(this.data, this.fromItem, this.fromInclusive, this.lastItem,
					this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GItem> descendingIterator() {
				return new CompactSetAscendingIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
				final boolean toInclusive) {
				if(!this.isInRange(fromElement, fromInclusive)) throw new IllegalArgumentException("FromElement out of range");
				if(!this.isInRange(toElement, toInclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactDescendingSubSet<GItem>(this.data, toElement, toInclusive, fromElement, fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
				if(!this.isInRange(toElement, inclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactDescendingSubSet<GItem>(this.data, toElement, inclusive, this.lastItem, this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
				if(!this.isInRange(fromElement, inclusive)) throw new IllegalArgumentException("FromElement out of range");
				return new CompactDescendingSubSet<GItem>(this.data, this.fromItem, this.fromInclusive, fromElement, inclusive);
			}

		}

		/**
		 * Dieses Feld speichert den {@link Comparator Comparator}.
		 */
		protected final Comparator<? super GItem> comparator;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator Comparator}.
		 * 
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableSet(final Comparator<? super GItem> comparator) throws NullPointerException {
			super();
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection {@link Collection Collection}.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn die gegebene {@link Collection Collection} bzw. der gegebene {@link Comparator
		 *         Comparator} {@code null} ist.
		 */
		public CompactNavigableSet(final Collection<? extends GItem> collection, final Comparator<? super GItem> comparator)
			throws NullPointerException {
			super(collection);
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set Set} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableSet(final int capacity, final Comparator<? super GItem> comparator)
			throws NullPointerException {
			super(capacity);
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Diese Methode löscht das {@code index}-te Element und gibt es oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Element oder {@code null}.
		 */
		protected final GItem pollItem(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			final GItem item = this.getItem(index);
			this.removeItems(index, 1);
			return item;
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element oder {@code null}.
		 */
		protected final GItem getItemOrNull(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			return this.getItem(index);
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück oder wirft eine {@link NoSuchElementException}.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws NoSuchElementException Wenn der gegebene Index ungültig ist.
		 */
		protected final GItem getItemOrException(final int index) throws NoSuchElementException {
			if((index < 0) || (index >= this.size)) throw new NoSuchElementException();
			return this.getItem(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemIndex(final Object key) {
			return this.compareIndex(key, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			return this.comparator.compare((GItem)key, (GItem)item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Comparator<? super GItem> comparator() {
			return this.comparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem first() {
			return this.getItemOrException(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem lower(final GItem item) {
			return this.getItemOrNull(this.lowerIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem floor(final GItem item) {
			return this.getItemOrNull(this.floorIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem ceiling(final GItem item) {
			return this.getItemOrNull(this.ceilingIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem higher(final GItem item) {
			return this.getItemOrNull(this.higherIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem last() {
			return this.getItemOrException(this.lastIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem pollFirst() {
			return this.pollItem(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem pollLast() {
			return this.pollItem(this.lastIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedSet<GItem> subSet(final GItem fromElement, final GItem toElement) {
			return this.subSet(fromElement, true, toElement, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
			final boolean toInclusive) {
			return new CompactAscendingSubSet<GItem>(this, fromElement, fromInclusive, toElement, toInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedSet<GItem> headSet(final GItem toElement) {
			return this.headSet(toElement, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
			return new CompactAscendingSubSet<GItem>(this, CompactSubData.OPEN, true, toElement, inclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedSet<GItem> tailSet(final GItem fromElement) {
			return this.tailSet(fromElement, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
			return new CompactAscendingSubSet<GItem>(this, fromElement, inclusive, CompactSubData.OPEN, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GItem> descendingSet() {
			return new CompactDescendingSubSet<GItem>(this, CompactSubData.OPEN, true, CompactSubData.OPEN, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> descendingIterator() {
			return new CompactSetDescendingIterator<GItem>(this, 0, this.size);
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link Map Map}, deren Daten in einem {@link Array Array} verwaltet
	 * werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 */
	public abstract static class CompactMap<K, V> extends CompactData implements Map<K, V> {

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <K> Typ der Schlüssel.
		 */
		protected static class CompactMapKeyAscendingIterator<K> extends CompactAscendingIterator<K, CompactMap<K, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapKeyAscendingIterator(final CompactMap<K, ?> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected K next(final int index) {
				return this.data.getKey(index);
			}

		}

		/**
		 * Diese Klasse implementiert den absteigenden {@link Iterator Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <K> Typ der Schlüssel.
		 */
		protected static class CompactMapKeyDescendingIterator<K> extends CompactDescendingIterator<K, CompactMap<K, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapKeyDescendingIterator(final CompactMap<K, ?> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected K next(final int index) {
				return this.data.getKey(index);
			}

		}

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der Werte.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <V> Typ der Werte.
		 */
		protected static class CompactMapValueIterator<V> extends CompactAscendingIterator<V, CompactMap<?, V>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapValueIterator(final CompactMap<?, V> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected V next(final int index) {
				return this.data.getValue(index);
			}

		}

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der {@link java.util.Map.Entry Entries}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <K> Typ der Schlüssel.
		 * @param <V> Typ der Werte.
		 */
		protected static class CompactMapEntryIterator<K, V> extends
			CompactAscendingIterator<Entry<K, V>, CompactMap<K, V>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapEntryIterator(final CompactMap<K, V> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected Entry<K, V> next(final int index) {
				return this.data.getEntry(index);
			}

		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map}.
		 */
		public CompactMap() {
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactMap(final int capacity) {
			this.allocate(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen.
		 * 
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 */
		public CompactMap(final Map<? extends K, ? extends V> map) {
			this.allocate(map.size());
			this.putAll(map);
		}

		/**
		 * Diese Methode gibt den Schlüssel des gegebenen Werts zurück.
		 * 
		 * @param value Wert.
		 * @return Schlüssel.
		 */
		protected abstract K getKey(final V value);

		/**
		 * Diese Methode setzt den Schlüssel des gegebenen Werts.
		 * 
		 * @param key Schlüssel.
		 * @param value Wert.
		 */
		protected abstract void setKey(K key, final V value);

		/**
		 * Diese Methode gibt den Schlüssel des {@code index}-ten Elements zurück.
		 * 
		 * @param index Index.
		 * @return Schlüssel des {@code index}-ten Elements.
		 */
		protected abstract K getKey(int index);

		/**
		 * Diese Methode gibt den Wert des {@code index}-ten Elements zurück.
		 * 
		 * @param index Index.
		 * @return Wert des {@code index}-ten Elements.
		 */
		protected abstract V getValue(int index);

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element
		 */
		protected final Entry<K, V> getEntry(final int index) {
			return new SimpleEntry<K, V>(this.getKey(index), this.getValue(index)) {

				private static final long serialVersionUID = -543360027933297926L;

				@Override
				public V setValue(final V value) {
					final V v = super.setValue(value);
					CompactMap.this.setEntry(index, this.getKey(), value);
					return v;
				}

			};
		}

		/**
		 * Diese Methode setzt Schlüssel und Wert des {@code index}-ten Elements.
		 * 
		 * @param index Index.
		 * @param key Schlüssel.
		 * @param value Wert.
		 */
		protected abstract void setEntry(int index, K key, V value);

		/**
		 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
		 * 
		 * @see AbstractSet
		 * @return {@link Set Set}.
		 */
		protected final Map<K, V> getItemMap() {
			return new AbstractMap<K, V>() {

				@Override
				public Set<Entry<K, V>> entrySet() {
					return CompactMap.this.entrySet();
				}

			};
		}

		/**
		 * Diese Methode sucht zuerst nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt den
		 * Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist
		 * der Index, bei dem der Eintrag eingefügt werden müsste.
		 * 
		 * @see CompactData#equalsIndex(Object, int)
		 * @see CompactData#compareIndex(Object, int)
		 * @param key Syhlüssel.
		 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		@Override
		protected abstract int itemIndex(final Object key);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.removeItems(0, this.size);
			this.compact();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<V> values() {
			return new AbstractCollection<V>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public Iterator<V> iterator() {
					return new CompactMapValueIterator<V>(CompactMap.this, 0, CompactMap.this.size);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<K> keySet() {
			return new AbstractSet<K>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public Iterator<K> iterator() {
					return new CompactMapKeyAscendingIterator<K>(CompactMap.this, 0, CompactMap.this.size);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<K, V>> entrySet() {
			return new AbstractSet<Entry<K, V>>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public Iterator<Entry<K, V>> iterator() {
					return new CompactMapEntryIterator<K, V>(CompactMap.this, 0, CompactMap.this.size);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.size == 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsKey(final Object key) {
			return this.itemIndex(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V get(final Object key) {
			final int index = this.itemIndex(key);
			if(index < 0) return null;
			return this.getValue(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V put(final K key, final V value) {
			int index = this.itemIndex(key);
			if(index >= 0){
				final V item = this.getValue(index);
				this.setEntry(index, this.getKey(index), value);
				return item;
			}
			index = -index - 1;
			this.insertItems(index, 1);
			this.setEntry(index, key, value);
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putAll(final Map<? extends K, ? extends V> map) {
			for(final Entry<? extends K, ? extends V> entry: map.entrySet()){
				this.put(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V remove(final Object key) {
			final int index = this.itemIndex(key);
			if(index < 0) return null;
			final V item = this.getValue(index);
			this.removeItems(index, 1);
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.getItemMap().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Map<?, ?>)) return false;
			return this.getItemMap().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.getItemMap().toString();
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link CompactMap Compact-Map}, deren Werte in einem {@link Array Array}
	 * verwaltet werden und ihren Schlüssel selbst referenzieren. Diese Implementation erlaubt deshalb {@code null} nicht
	 * als Wert.
	 * 
	 * @see CompactItemMap#getKey(Object)
	 * @see CompactItemMap#setKey(Object, Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 */
	public static abstract class CompactItemMap<K, V> extends CompactMap<K, V> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map}.
		 */
		public CompactItemMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactItemMap(final int capacity) {
			super(capacity);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final K getKey(final int index) {
			return this.getKey(this.getValue(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected final V getValue(final int index) {
			return (V)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setEntry(final int index, final K key, final V value) {
			if(value == null) throw new NullPointerException();
			this.items[index] = value;
			this.setKey(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V put(final K key, final V value) {
			if(value == null) throw new NullPointerException();
			return super.put(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			if(value == null) return false;
			return CompactData.containsItem(this.items, this.size, value);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactItemMap
	 * Compact-Item-Map}.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 */
	public static abstract class CompactItemHashMap<K, V> extends CompactItemMap<K, V> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map}.
		 */
		public CompactItemHashMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactItemHashMap(final int capacity) {
			super(capacity);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemIndex(final Object key) {
			if(key == null) return this.equalsIndex(null, 0);
			return this.equalsIndex(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			if(key == null) return this.getKey((V)item) == null;
			return key.equals(this.getKey((V)item));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			final Object value = this.getKey((V)item);
			if(value == null) return hash;
			return Integer.compare(hash, value.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link Map Map}, deren Schlüssel und Werte in je einem {@link Array
	 * Array} verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 */
	public static abstract class CompactEntryMap<K, V> extends CompactMap<K, V> {

		/**
		 * Dieses Feld speichert die Werte.
		 */
		protected Object[] values = CompactData.ITEMS;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map}.
		 */
		public CompactEntryMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactEntryMap(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen.
		 * 
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 */
		public CompactEntryMap(final Map<? extends K, ? extends V> map) {
			super(map);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final K getKey(final V value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setKey(final K key, final V value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected K getKey(final int index) {
			return (K)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected V getValue(final int index) {
			return (V)this.values[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntry(final int index, final K key, final V value) {
			this.items[index] = key;
			this.values[index] = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insertItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			super.insertItems(index, count);
			this.values = CompactData.insertItems(this.values, size, this.items.length, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			super.removeItems(index, count);
			this.values = CompactData.removeItems(this.values, size, this.items.length, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void allocateItems(final int count) {
			super.allocateItems(count);
			this.values = CompactData.resizeItems(this.values, this.size, this.items.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void compactItems() {
			super.compactItems();
			this.values = CompactData.resizeItems(this.values, this.size, this.items.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			return CompactData.containsItem(this.values, this.size, value);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactEntryMap
	 * Compact-Entry-Map}.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 */
	public static class CompactEntryHashMap<K, V> extends CompactEntryMap<K, V> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map}.
		 */
		public CompactEntryHashMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactEntryHashMap(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen.
		 * 
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 */
		public CompactEntryHashMap(final Map<? extends K, ? extends V> map) {
			super(map);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemIndex(final Object key) {
			if(key == null) return this.equalsIndex(null, 0);
			return this.equalsIndex(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			if(key == null) return item == null;
			return key.equals(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			if(item == null) return hash;
			return Integer.compare(hash, item.hashCode());
		}

	}

	public static abstract class CompactNavigableMap<K, V> extends CompactMap<K, V> implements NavigableMap<K, V> {

		protected static abstract class CompactNavigableKeySet<K, GMap extends NavigableMap<K, ?>> extends AbstractSet<K>
			implements NavigableSet<K> {

			GMap map;

			public CompactNavigableKeySet(final GMap map) {
				this.map = map;
			}

			protected final K getKeyOrNull(final Entry<K, ?> entry) {
				if(entry == null) return null;
				return entry.getKey();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super K> comparator() {
				return this.map.comparator();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.map.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isEmpty() {
				return this.map.isEmpty();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public SortedSet<K> subSet(final K fromElement, final K toElement) {
				return this.subSet(fromElement, true, toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> subSet(final K fromElement, final boolean fromInclusive, final K toElement,
				final boolean toInclusive) {
				return this.map.subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public SortedSet<K> headSet(final K toElement) {
				return this.headSet(toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> headSet(final K toElement, final boolean inclusive) {
				return this.map.headMap(toElement, inclusive).navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public SortedSet<K> tailSet(final K fromElement) {
				return this.tailSet(fromElement, true);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> tailSet(final K fromElement, final boolean inclusive) {
				return this.map.tailMap(fromElement, inclusive).navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean contains(final Object key) {
				return this.map.containsKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean remove(final Object key) {
				if(!this.map.containsKey(key)) return false;
				this.map.remove(key);
				return true;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void clear() {
				this.map.clear();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K first() {
				return this.map.firstKey();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K last() {
				return this.map.lastKey();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K lower(final K key) {
				return this.map.lowerKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K floor(final K key) {
				return this.map.floorKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K ceiling(final K key) {
				return this.map.ceilingKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K higher(final K key) {
				return this.map.higherKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K pollFirst() {
				return this.getKeyOrNull(this.map.pollFirstEntry());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K pollLast() {
				return this.getKeyOrNull(this.map.pollLastEntry());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> descendingSet() {
				return this.map.descendingMap().navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<K> descendingIterator() {
				return this.descendingSet().iterator();
			}

		}

		protected static class CompactAscendingKeySet<K> extends CompactNavigableKeySet<K, CompactAscendingSubMap<K, ?>> {

			public CompactAscendingKeySet(final CompactAscendingSubMap<K, ?> map) {
				super(map);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<K> iterator() {
				return new CompactMapKeyAscendingIterator<K>(this.map.data, this.map.firstIndex(), this.map.lastIndex() + 1);
			}

		}

		protected static class CompactDescendingKeySet<K> extends CompactNavigableKeySet<K, CompactDescendingSubMap<K, ?>> {

			public CompactDescendingKeySet(final CompactDescendingSubMap<K, ?> map) {
				super(map);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<K> iterator() {
				return new CompactMapKeyDescendingIterator<K>(this.map.data, this.map.firstIndex(), this.map.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert eine abstrakte Teilmenge einer {@link CompactNavigableMap Compact-Navigable-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <K> Typ der Schlüssel.
		 * @param <V> Typ der Werte.
		 */
		protected static abstract class CompactNavigableSubMap<K, V> extends CompactSubData<CompactNavigableMap<K, V>>
			implements NavigableMap<K, V> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap Compact-Navigable-Map} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableMap Compact-Navigable-Map}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */
			public CompactNavigableSubMap(final CompactNavigableMap<K, V> map, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(map, fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * Diese Methode gibt eine neue {@link Map Map} zurück, das aus dem Einträgen erzeugt wird.
			 * 
			 * @see AbstractMap
			 * @return {@link Map Map}.
			 */
			protected final Map<K, V> getItemMap() {
				return new AbstractMap<K, V>() {

					@Override
					public Set<java.util.Map.Entry<K, V>> entrySet() {
						return CompactNavigableSubMap.this.entrySet();
					}

				};
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.countItems();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isEmpty() {
				return this.countItems() == 0;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> subMap(final K fromElement, final K toElement) {
				return this.subMap(fromElement, true, toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> headMap(final K toElement) {
				return this.headMap(toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> tailMap(final K fromElement) {
				return this.tailMap(fromElement, true);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean containsKey(final Object key) {
				return this.isInRange(key) && this.data.containsKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean containsValue(final Object value) {
				return this.values().contains(value);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public V get(final Object key) {
				if(!this.isInRange(key)) return null;
				return this.data.get(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public V put(final K key, final V value) {
				if(!this.isInRange(key)) throw new IllegalArgumentException("Entry out of range");
				return this.data.put(key, value);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void putAll(final Map<? extends K, ? extends V> map) {
				for(final Entry<? extends K, ? extends V> entry: map.entrySet()){
					this.put(entry.getKey(), entry.getValue());
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public V remove(final Object key) {
				if(!this.isInRange(key)) return null;
				return this.data.remove(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void clear() {
				this.clearItems();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Set<K> keySet() {
				return this.navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Collection<V> values() {
				return new AbstractCollection<V>() {

					@Override
					public int size() {
						return CompactNavigableSubMap.this.size();
					}

					@Override
					public Iterator<V> iterator() {
						return new CompactMapValueIterator<V>(CompactNavigableSubMap.this.data,
							CompactNavigableSubMap.this.firstIndex(), CompactNavigableSubMap.this.lastIndex() + 1);
					}

				};
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Set<Entry<K, V>> entrySet() {
				return new AbstractSet<Entry<K, V>>() {

					@Override
					public int size() {
						return CompactNavigableSubMap.this.size();
					}

					@Override
					public Iterator<Entry<K, V>> iterator() {
						return new CompactMapEntryIterator<K, V>(CompactNavigableSubMap.this.data,
							CompactNavigableSubMap.this.firstIndex(), CompactNavigableSubMap.this.lastIndex() - 1);
					}

				};
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> descendingKeySet() {
				return this.descendingMap().navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int hashCode() {
				return this.getItemMap().hashCode();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean equals(final Object object) {
				if(object == this) return true;
				if(!(object instanceof Set<?>)) return false;
				return this.getItemMap().equals(object);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return this.getItemMap().toString();
			}

		}

		/**
		 * Diese Klasse implementiert die aufsteigende Teilmenge einer {@link CompactNavigableMap Compact-Navigable-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <K> Typ der Schlüssel.
		 * @param <V> Typ der Werte.
		 */
		protected static final class CompactAscendingSubMap<K, V> extends CompactNavigableSubMap<K, V> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap Compact-Navigable-Map} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableMap Compact-Navigable-Map}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */
			public CompactAscendingSubMap(final CompactNavigableMap<K, V> map, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(map, fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super K> comparator() {
				return this.data.comparator;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K firstKey() {
				return this.data.getKeyOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> firstEntry() {
				return this.data.getEntryOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K lastKey() {
				return this.data.getKeyOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> lastEntry() {
				return this.data.getEntryOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K lowerKey(final K key) {
				return this.data.getKeyOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> lowerEntry(final K key) {
				return this.data.getEntryOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K floorKey(final K key) {
				return this.data.getKeyOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> floorEntry(final K key) {
				return this.data.getEntryOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K ceilingKey(final K key) {
				return this.data.getKeyOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> ceilingEntry(final K key) {
				return this.data.getEntryOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K higherKey(final K key) {
				return this.data.getKeyOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> higherEntry(final K key) {
				return this.data.getEntryOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> pollFirstEntry() {
				return this.data.poll(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> pollLastEntry() {
				return this.data.poll(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> descendingMap() {
				return new CompactDescendingSubMap<K, V>(this.data, this.fromItem, this.fromInclusive, this.lastItem,
					this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> navigableKeySet() {
				return new CompactAscendingKeySet<K>(this);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
				final boolean toInclusive) {
				if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("FromElement out of range");
				if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactAscendingSubMap<K, V>(this.data, fromKey, fromInclusive, toKey, toInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
				if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactAscendingSubMap<K, V>(this.data, this.fromItem, this.fromInclusive, toKey, inclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
				if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("FromElement out of range");
				return new CompactAscendingSubMap<K, V>(this.data, fromKey, inclusive, this.lastItem, this.lastInclusive);
			}

		}

		/**
		 * Diese Klasse implementiert die absteigende Teilmenge eines {@link CompactNavigableSet Compact-Navigable-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static final class CompactDescendingSubMap<K, V> extends CompactNavigableSubMap<K, V> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet Compact-Navigable-Set} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableSet Compact-Navigable-Set}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 */

			public CompactDescendingSubMap(final CompactNavigableMap<K, V> map, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(map, fromItem, fromInclusive, lastItem, lastInclusive);

			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super K> comparator() {
				return Collections.reverseOrder(this.data.comparator);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K firstKey() {
				return this.data.getKeyOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> firstEntry() {
				return this.data.getEntryOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K lastKey() {
				return this.data.getKeyOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> lastEntry() {
				return this.data.getEntryOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K lowerKey(final K key) {
				return this.data.getKeyOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> lowerEntry(final K key) {
				return this.data.getEntryOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K floorKey(final K key) {
				return this.data.getKeyOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> floorEntry(final K key) {
				return this.data.getEntryOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K ceilingKey(final K key) {
				return this.data.getKeyOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> ceilingEntry(final K key) {
				return this.data.getEntryOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public K higherKey(final K key) {
				return this.data.getKeyOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> higherEntry(final K key) {
				return this.data.getEntryOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> pollFirstEntry() {
				return this.data.poll(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<K, V> pollLastEntry() {
				return this.data.poll(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> descendingMap() {
				return new CompactAscendingSubMap<K, V>(this.data, this.fromItem, this.fromInclusive, this.lastItem,
					this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<K> navigableKeySet() {
				return new CompactDescendingKeySet<K>(this);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
				final boolean toInclusive) {
				if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("FromElement out of range");
				if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactDescendingSubMap<K, V>(this.data, toKey, toInclusive, fromKey, fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
				if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactDescendingSubMap<K, V>(this.data, toKey, inclusive, this.fromItem, this.fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
				if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("FromElement out of range");
				return new CompactDescendingSubMap<K, V>(this.data, this.lastItem, this.lastInclusive, fromKey, inclusive);
			}

		}

		/**
		 * Dieses Feld speichert den {@link Comparator Comparator}.
		 */
		protected final Comparator<? super K> comparator;

		public CompactNavigableMap(final Comparator<? super K> comparator) {
			this.comparator = comparator;
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element oder {@code null}.
		 */
		protected final K getKeyOrNull(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			return this.getKey(index);
		}

		protected final Entry<K, V> getEntryOrNull(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			return this.getEntry(index);
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück oder wirft eine {@link NoSuchElementException}.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws NoSuchElementException Wenn der gegebene Index ungültig ist.
		 */
		protected final K getKeyOrException(final int index) throws NoSuchElementException {
			if((index < 0) || (index >= this.size)) throw new NoSuchElementException();
			return this.getKey(index);
		}

		protected final Entry<K, V> getEntryOrException(final int index) throws NoSuchElementException {
			if((index < 0) || (index >= this.size)) throw new NoSuchElementException();
			return this.getEntry(index);
		}

		/**
		 * Diese Methode löscht das {@code index}-te Element und gibt es oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Element oder {@code null}.
		 */
		protected final Entry<K, V> poll(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			final Entry<K, V> item = this.getEntry(index);
			this.removeItems(index, 1);
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemIndex(final Object key) {
			return this.compareIndex(key, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Comparator<? super K> comparator() {
			return this.comparator;
		}

		@Override
		public K firstKey() {
			return this.getKeyOrException(this.firstIndex());
		}

		@Override
		public Entry<K, V> firstEntry() {
			return null;
		}

		@Override
		public K lowerKey(final K key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> lowerEntry(final K key) {
			return null;
		}

		@Override
		public K floorKey(final K key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> floorEntry(final K key) {
			return null;
		}

		@Override
		public K ceilingKey(final K key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> ceilingEntry(final K key) {
			return null;
		}

		@Override
		public K higherKey(final K key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> higherEntry(final K key) {
			return null;
		}

		@Override
		public K lastKey() {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> lastEntry() {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> pollFirstEntry() {
			return null;
		}

		@Override
		public java.util.Map.Entry<K, V> pollLastEntry() {
			return null;
		}

		@Override
		public boolean containsValue(final Object value) {
			return false;
		}

		@Override
		public NavigableMap<K, V> descendingMap() {
			return null;
		}

		@Override
		public NavigableSet<K> navigableKeySet() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<K> descendingKeySet() {
			return this.descendingMap().navigableKeySet();
		}

		@Override
		public NavigableMap<K, V> subMap(final K fromKey, final boolean fromInclusive, final K toKey,
			final boolean toInclusive) {
			return null;
		}

		@Override
		public NavigableMap<K, V> headMap(final K toKey, final boolean inclusive) {
			return null;
		}

		@Override
		public NavigableMap<K, V> tailMap(final K fromKey, final boolean inclusive) {
			return null;
		}

		@Override
		public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
			return null;
		}

		@Override
		public SortedMap<K, V> headMap(final K toKey) {
			return null;
		}

		@Override
		public SortedMap<K, V> tailMap(final K fromKey) {
			return null;
		}

	}

	public static class CompactNavigableEntryMap<K, V> extends CompactNavigableMap<K, V> {

		/**
		 * Dieses Feld speichert die Werte.
		 */
		protected Object[] values = CompactData.ITEMS;

		public CompactNavigableEntryMap(final Comparator<? super K> comparator) {
			super(comparator);

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final K getKey(final V value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setKey(final K key, final V value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected K getKey(final int index) {
			return (K)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected V getValue(final int index) {
			return (V)this.values[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntry(final int index, final K key, final V value) {
			this.items[index] = key;
			this.values[index] = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insertItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			super.insertItems(index, count);
			this.values = CompactData.insertItems(this.values, size, this.items.length, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			super.removeItems(index, count);
			this.values = CompactData.removeItems(this.values, size, this.items.length, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void allocateItems(final int count) {
			super.allocateItems(count);
			this.values = CompactData.resizeItems(this.values, this.size, this.items.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void compactItems() {
			super.compactItems();
			this.values = CompactData.resizeItems(this.values, this.size, this.items.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			return CompactData.containsItem(this.values, this.size, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			return this.comparator.compare((K)key, (K)item);
		}

	}

	public static abstract class CompactNavigableItemMap<K, V> extends CompactNavigableMap<K, V> {

		public CompactNavigableItemMap(final Comparator<? super K> comparator) {
			super(comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final K getKey(final int index) {
			return this.getKey(this.getValue(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected final V getValue(final int index) {
			return (V)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setEntry(final int index, final K key, final V value) {
			if(value == null) throw new NullPointerException();
			this.items[index] = value;
			this.setKey(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			return this.comparator.compare((K)key, this.getKey((V)item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V put(final K key, final V value) {
			if(value == null) throw new NullPointerException();
			return super.put(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			if(value == null) return false;
			return CompactData.containsItem(this.items, this.size, value);
		}

	}

}
