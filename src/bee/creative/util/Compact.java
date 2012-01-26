package bee.creative.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public final class Compact {

	static void run(final String name, final Runnable runnable) {
		// runnable.run();
		System.out.print(name);
		System.out.print(": ");
		System.out.println(new Tester(runnable));
	}

	public static void main(final String[] args) {

		final List<Object> c = new ArrayList<Object>();
		c.addAll(Arrays.asList(Hash.class.getFields()));
		c.addAll(Arrays.asList(Hash.class.getMethods()));
		c.addAll(Arrays.asList(Arrays.class.getFields()));
		c.addAll(Arrays.asList(Arrays.class.getMethods()));
		c.addAll(Arrays.asList(CompactBase.class.getFields()));
		c.addAll(Arrays.asList(CompactBase.class.getMethods()));

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
	public static abstract class CompactBase {

		/**
		 * Diese Klasse implementiert einen abstrakten {@link Iterator Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static abstract class CompactIterator<GItem> implements Iterator<GItem> {

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
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactIterator(final int from, final int last) {
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
			protected abstract void remove(int index);

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
		 * Diese Klasse implementiert einen abstrakten aufsteigenden {@link Iterator Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static abstract class CompactAscendingIterator<GItem> extends CompactIterator<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingIterator(final int from, final int last) {
				super(from - 1, last - 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem next() {
				return this.next(this.item = (this.from = this.from + 1));
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten absteigenden {@link Iterator Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected static abstract class CompactDescendingIterator<GItem> extends CompactIterator<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactDescendingIterator(final int from, final int last) {
				super(from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem next() {
				return this.next(this.item = (this.last = this.last - 1));
			}

		}

		protected abstract class CompactSubBase {

			protected Object fromItem;

			protected boolean fromInclusive;

			protected Object lastItem;

			protected boolean lastInclusive;

			public CompactSubBase(final Object fromItem, final boolean fromInclusive, final Object lastItem,
				final boolean lastInclusive) {
				if(fromItem != CompactBase.OPEN){
					if(lastItem != CompactBase.OPEN){
						if(CompactBase.this.compare(fromItem, 0, lastItem) > 0)
							throw new IllegalArgumentException("FromItem > FastItem");
					}else{
						CompactBase.this.compare(fromItem, 0, fromItem);
					}
				}else if(lastItem != CompactBase.OPEN){
					CompactBase.this.compare(lastItem, 0, lastItem);
				}
				this.fromItem = fromItem;
				this.fromInclusive = fromInclusive;
				this.lastItem = lastItem;
				this.lastInclusive = lastInclusive;
			}

			protected final boolean isTooLow(final Object key) {
				final Object item = this.fromItem;
				if(item == CompactBase.OPEN) return false;
				final int comp = CompactBase.this.compare(key, 0, item);
				return ((comp < 0) || ((comp == 0) && !this.fromInclusive));
			}

			protected final boolean isTooHigh(final Object key) {
				final Object item = this.lastItem;
				if(item == CompactBase.OPEN) return false;
				final int comp = CompactBase.this.compare(key, 0, item);
				return ((comp > 0) || ((comp == 0) && !this.lastInclusive));
			}

			protected final boolean isInRange(final Object key) {
				return !this.isTooLow(key) && !this.isTooHigh(key);
			}

			protected final boolean isInRange(final Object key, final boolean inclusive) {
				return inclusive ? this.isInRange(key) : this.isInClosedRange(key);
			}

			protected final boolean isInClosedRange(final Object key) {
				final Object fromItem = this.fromItem, lastItem = this.lastItem;
				return ((fromItem == CompactBase.OPEN) || (CompactBase.this.compare(key, 0, fromItem) >= 0))
					&& ((lastItem == CompactBase.OPEN) || (CompactBase.this.compare(lastItem, 0, key) >= 0));
			}

			/**
			 * Diese Methode gibt den Index des ersten Elements zurück.
			 * 
			 * @see NavigableSet#first()
			 * @return Index des ersten Elements.
			 */
			protected final int firstIndex() {
				final Object item = this.fromItem;
				if(item == CompactBase.OPEN) return CompactBase.this.firstIndex();
				if(this.fromInclusive) return CompactBase.this.ceilingIndex(item);
				return CompactBase.this.higherIndex(item);
			}

			/**
			 * Diese Methode gibt den Index des letzten Elements zurück.
			 * 
			 * @see NavigableSet#last()
			 * @return Index des letzten Elements.
			 */
			protected final int lastIndex() {
				final Object item = this.lastItem;
				if(item == CompactBase.OPEN) return CompactBase.this.lastIndex();
				if(this.lastInclusive) return CompactBase.this.floorIndex(item);
				return CompactBase.this.lowerIndex(item);
			}

			protected final int lowestIndex() {
				final int index = this.firstIndex();
				if((index >= CompactBase.this.size) || this.isTooHigh(CompactBase.this.items[index])) return -index - 1;
				return index;
			}

			protected final int highestIndex() {
				final int index = this.lastIndex();
				if((index < 0) || this.isTooLow(CompactBase.this.items[index])) return -index - 1;
				return index;
			}

			protected final int lowerIndex(final Object item) {
				if(this.isTooHigh(item)) return this.highestIndex();
				final int index = CompactBase.this.lowerIndex(item);
				if(index < 0) return index;
				if(this.isTooLow(item)) return -index - 1;
				return index;
			}

			protected final int floorIndex(final Object item) {
				if(this.isTooHigh(item)) return this.highestIndex();
				final int index = CompactBase.this.floorIndex(item);
				if(index < 0) return index;
				if(this.isTooLow(item)) return -index - 1;
				return index;
			}

			protected final int ceilingIndex(final Object item) {
				if(this.isTooLow(item)) return this.lowestIndex();
				final int index = CompactBase.this.ceilingIndex(item);
				if(index < 0) return index;
				if(this.isTooHigh(item)) return -index - 1;
				return index;
			}

			protected final int higherIndex(final Object item) {
				if(this.isTooLow(item)) return this.lowestIndex();
				final int index = CompactBase.this.higherIndex(item);
				if(index < 0) return index;
				if(this.isTooHigh(item)) return -index - 1;
				return index;
			}

			protected final void clearItems() {
				final int fromIndex = this.firstIndex(), lastIndex = this.lastIndex();
				if(fromIndex > lastIndex) return;
				CompactBase.this.removeItems(fromIndex, (lastIndex - fromIndex) + 1);
			}

			protected final int countItems() {
				return (this.lastIndex() - this.firstIndex()) + 1;
			}

		}

		/**
		 * Dieses Feld speichert das Objekt zur offenen Begrenzung sortierter Listen.
		 */
		protected static final Object OPEN = new Object();

		/**
		 * Dieses Feld speichert das leere {@link Array Array} der Elemente.
		 */
		protected static final Object[] ITEMS = new Object[0];

		/**
		 * Diese Methode setzt die Länge des gegebenen {@link Array Array} mit der gegebenen Belegung auf die gegebene Länge
		 * und gibt es zurück. Wenn die gegebene Länge {@code 0} ist, wird {@link CompactBase#ITEMS} zurück gegeben. Wenn
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
			if(length == 0) return CompactBase.ITEMS;
			final Object[] objects = new Object[length];
			System.arraycopy(items, 0, objects, 0, size);
			return objects;
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
		 * zurück. Wenn die gegebene Länge {@code 0} ist, wird {@link CompactBase#ITEMS} zurück gegeben. Wenn die Länge des
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
				}else return CompactBase.ITEMS;
			}else{
				System.arraycopy(items, index + count, items, index, size - count - index);
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
		protected Object[] items = CompactBase.ITEMS;

		/**
		 * Diese Methode gibt den Index des ersten Elements zurück.
		 * 
		 * @see NavigableSet#first()
		 * @return Index des ersten Elements.
		 */
		protected final int firstIndex() {
			return 0;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist.
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
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist.
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
		 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist.
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
		 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist.
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
		 * Diese Methode gibt den Index des letzten Elements zurück.
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
		 * @see CompactBase#equalsIndex(Object, int)
		 * @see CompactBase#compareIndex(Object, int)
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
		 * @see CompactBase#equals(Object, int, Object)
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
		 * @see CompactBase#compare(Object, int, Object)
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
		 * @see CompactBase#validInsertLength(int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void insertItems(final int index, final int count) throws IllegalArgumentException {
			final int oldSize = this.size, newSize = oldSize + count;
			this.items = CompactBase.insertItems(this.items, oldSize, this.validInsertLength(newSize), index, count);
			this.size = newSize;
		}

		/**
		 * Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem {@link Array Array} mit
		 * der gegebenen Länge der Belegung.
		 * 
		 * @see CompactBase#validRemoveLength(int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			final int oldSize = this.size, newSize = oldSize - count;
			this.items = CompactBase.removeItems(this.items, oldSize, this.validRemoveLength(newSize), index, count);
			this.size = newSize;
		}

		/**
		 * Diese Methode vergrößert die Kapazität des {@link Array Arrays} der Elemente, sodass dieses die gegebene Anzahl
		 * an Elementen verwalten kann.
		 * 
		 * @see CompactBase#validAllocateLength(int)
		 * @param count Anzahl.
		 */
		protected void allocateItems(final int count) {
			this.items = CompactBase.resizeItems(this.items, this.size, this.validAllocateLength(count));
		}

		/**
		 * Diese Methode verkleinert die Kapazität des {@link Array Arrays} der Elemente auf das Minimum.
		 * 
		 * @see CompactBase#validCompactLength()
		 */
		protected void compactItems() {
			this.items = CompactBase.resizeItems(this.items, this.size, this.validCompactLength());
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
		 * @see CompactBase#insertItems(int, int)
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
		 * @see CompactBase#removeItems(int, int)
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
		 * @see CompactBase#size
		 * @see CompactBase#compactItems()
		 * @return Länge.
		 */
		protected int validCompactLength() {
			return this.size;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Reservieren von Elementen aufgerufen.
		 * 
		 * @see CompactBase#allocateItems(int)
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
	public static abstract class CompactSet<GItem> extends CompactBase implements Set<GItem> {

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} für {@link CompactSet Compact-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected final class CompactAscendingSetIterator extends CompactAscendingIterator<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingSetIterator(final int from, final int last) {
				super(from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected GItem next(final int index) {
				return CompactSet.this.item(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void remove(final int index) {
				CompactSet.this.removeItems(index, 1);
			}

		}

		/**
		 * Diese Klasse implementiert den absteigenden {@link Iterator Iterator} für {@link CompactSet Compact-Sets}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected final class CompactDescentingSetIterator extends CompactDescendingIterator<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactDescentingSetIterator(final int from, final int last) {
				super(from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected GItem next(final int index) {
				return CompactSet.this.item(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void remove(final int index) {
				CompactSet.this.removeItems(index, 1);
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
		 * @see CompactBase#allocate(int)
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
		 */
		public CompactSet(final Collection<? extends GItem> collection) {
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
		protected final GItem item(final int index) {
			return (GItem)this.items[index];
		}

		/**
		 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
		 * 
		 * @see AbstractSet
		 * @return {@link Set Set}.
		 */
		protected final Set<GItem> itemSet() {
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
			return new CompactAscendingSetIterator(0, this.size);
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
			this.items[index] = item;
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
			return this.itemSet().toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T> T[] toArray(final T[] a) {
			return this.itemSet().toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.itemSet().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Set<?>)) return false;
			return this.itemSet().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.itemSet().toString();
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
		 * @see CompactBase#allocate(int)
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

	public static class CompactNavigableSet<GItem> extends CompactSet<GItem> implements NavigableSet<GItem> {

		protected abstract class CompactNavigableSubSet extends CompactSubBase implements NavigableSet<GItem> {

			public CompactNavigableSubSet(final Object fromItem, final boolean fromInclusive, final Object lastItem,
				final boolean lastInclusive) {
				super(fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
			 * 
			 * @see AbstractSet
			 * @return {@link Set Set}.
			 */
			protected final Set<GItem> itemSet() {
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
			public Object[] toArray() {
				return this.itemSet().toArray();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public <T> T[] toArray(final T[] a) {
				return this.itemSet().toArray(a);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int hashCode() {
				return this.itemSet().hashCode();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean equals(final Object object) {
				if(object == this) return true;
				if(!(object instanceof Set<?>)) return false;
				return this.itemSet().equals(object);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return this.itemSet().toString();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean contains(final Object o) {
				return this.isInRange(o) && CompactNavigableSet.this.contains(o);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean add(final GItem entry) {
				if(!this.isInRange(entry)) throw new IllegalArgumentException("Entry out of range");
				return CompactNavigableSet.this.add(entry);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean remove(final Object entry) {
				if(!this.isInRange(entry)) return false;
				return CompactNavigableSet.this.remove(entry);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean containsAll(final Collection<?> c) {
				for(final Object entry: c)
					if(!this.contains(entry)) return false;
				return true;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean addAll(final Collection<? extends GItem> c) {
				boolean modified = false;
				for(final GItem entry: c)
					if(this.add(entry)){
						modified = true;
					}
				return modified;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean retainAll(final Collection<?> c) {
				boolean modified = false;
				final Iterator<GItem> iterator = this.iterator();
				while(iterator.hasNext()){
					if(!c.contains(iterator.next())){
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
			public boolean removeAll(final Collection<?> c) {
				boolean modified = false;
				final Iterator<?> iterator = this.iterator();
				while(iterator.hasNext()){
					if(c.contains(iterator.next())){
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

		}

		protected class CompactAscendingSubSet extends CompactNavigableSubSet {

			public CompactAscendingSubSet(final Object fromItem, final boolean fromInclusive, final Object lastItem,
				final boolean lastInclusive) {
				super(fromItem, fromInclusive, lastItem, lastInclusive);

			}

			@Override
			public Comparator<? super GItem> comparator() {
				return null;
			}

			@Override
			public GItem first() {
				return CompactNavigableSet.this.itemOrException(this.lowestIndex());
			}

			@Override
			public GItem last() {
				return CompactNavigableSet.this.itemOrException(this.highestIndex());
			}

			@Override
			public GItem lower(final GItem entry) {
				return CompactNavigableSet.this.itemOrNull(this.lowerIndex(entry));
			}

			@Override
			public GItem floor(final GItem entry) {
				return CompactNavigableSet.this.itemOrNull(this.floorIndex(entry));
			}

			@Override
			public GItem ceiling(final GItem entry) {
				return CompactNavigableSet.this.itemOrNull(this.ceilingIndex(entry));
			}

			@Override
			public GItem higher(final GItem entry) {

				return CompactNavigableSet.this.itemOrNull(this.higherIndex(entry));
			}

			@Override
			public GItem pollFirst() {
				return null;
			}

			@Override
			public GItem pollLast() {
				return null;
			}

			@Override
			public Iterator<GItem> iterator() {
				return null;
			}

			@Override
			public NavigableSet<GItem> descendingSet() {
				return null;
			}

			@Override
			public Iterator<GItem> descendingIterator() {
				return null;
			}

			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
				final boolean toInclusive) {
				if(!this.isInRange(fromElement, fromInclusive)) throw new IllegalArgumentException("fromKey out of range");
				if(!this.isInRange(toElement, toInclusive)) throw new IllegalArgumentException("toKey out of range");
				return new CompactAscendingSubSet(fromElement, fromInclusive, toElement, toInclusive);
			}

			@Override
			public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
				return null;
			}

			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
				return null;
			}

		}

		protected class CompactDescendingSubSet extends CompactNavigableSubSet {

			public CompactDescendingSubSet(final Object fromItem, final boolean fromInclusive, final Object lastItem,
				final boolean lastInclusive) {
				super(fromItem, fromInclusive, lastItem, lastInclusive);

			}

			@Override
			public Comparator<? super GItem> comparator() {
				return null;
			}

			@Override
			public GItem first() {
				return null;
			}

			@Override
			public GItem last() {
				return null;
			}

			@Override
			public GItem lower(final GItem e) {
				return null;
			}

			@Override
			public GItem floor(final GItem e) {
				return null;
			}

			@Override
			public GItem ceiling(final GItem e) {
				return null;
			}

			@Override
			public GItem higher(final GItem e) {
				return null;
			}

			@Override
			public GItem pollFirst() {
				return null;
			}

			@Override
			public GItem pollLast() {
				return null;
			}

			@Override
			public Iterator<GItem> iterator() {
				return null;
			}

			@Override
			public NavigableSet<GItem> descendingSet() {
				return null;
			}

			@Override
			public Iterator<GItem> descendingIterator() {
				return null;
			}

			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
				final boolean toInclusive) {
				return null;
			}

			@Override
			public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
				return null;
			}

			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
				return null;
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
		 */
		public CompactNavigableSet(final Comparator<? super GItem> comparator) {
			super();
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		public CompactNavigableSet(final Collection<? extends GItem> collection, final Comparator<? super GItem> comparator) {
			super(collection);
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Diese Methode löscht das {@code index}-te Element und gibt es zurück. Wenn das {@link CompactSet Compact-Set}
		 * leer ist, wird {@code null} zurück gegeben.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Element oder {@code null}.
		 */
		protected final GItem poll(final int index) {
			if(this.size == 0) return null;
			final GItem item = this.item(index);
			this.removeItems(index, 1);
			return item;
		}

		protected final GItem itemOrNull(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			return this.item(index);
		}

		protected final GItem itemOrException(final int index) {
			if((index < 0) || (index >= this.size)) throw new NoSuchElementException();
			return this.item(index);
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
		@SuppressWarnings ("unchecked")
		@Override
		public GItem first() {
			return this.itemOrException(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem lower(final GItem item) {
			return this.itemOrNull(this.lowerIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem floor(final GItem item) {
			return this.itemOrNull(this.floorIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem ceiling(final GItem item) {
			return this.itemOrNull(this.ceilingIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem higher(final GItem item) {
			return this.itemOrNull(this.higherIndex(item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem last() {
			return this.itemOrException(this.lastIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem pollFirst() {
			return this.poll(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem pollLast() {
			return this.poll(this.lastIndex());
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
			return new CompactAscendingSubSet(fromElement, fromInclusive, toElement, toInclusive);
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
			return new CompactAscendingSubSet(CompactBase.OPEN, true, toElement, inclusive);
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
			return new CompactAscendingSubSet(fromElement, inclusive, CompactBase.OPEN, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GItem> descendingSet() {
			return new CompactDescendingSubSet(CompactBase.OPEN, true, CompactBase.OPEN, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> descendingIterator() {
			return new CompactDescentingSetIterator(0, this.size);
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
	public abstract static class CompactMap<K, V> extends CompactBase implements Map<K, V> {

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactAscendingMapKeyIterator extends CompactAscendingIterator<K> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingMapKeyIterator(final int from, final int last) {
				super(from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected K next(final int index) {
				return CompactMap.this.getKey(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void remove(final int index) {
				CompactMap.this.removeItems(index, 1);
			}

		}

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der Werte.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactAscendingMapValueIterator extends CompactAscendingIterator<V> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingMapValueIterator(final int from, final int last) {
				super(from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected V next(final int index) {
				return CompactMap.this.getValue(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void remove(final int index) {
				CompactMap.this.removeItems(index, 1);
			}

		}

		/**
		 * Diese Klasse implementiert den {@link Iterator Iterator} der {@link java.util.Map.Entry Entries}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactAscendingMapEntryIterator extends CompactAscendingIterator<Entry<K, V>> {

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingMapEntryIterator(final int from, final int last) {
				super(from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected Entry<K, V> next(final int index) {
				return CompactMap.this.getEntry(index);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected void remove(final int index) {
				CompactMap.this.removeItems(index, 1);
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
		 * @see CompactBase#allocate(int)
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

				private static final long serialVersionUID = -2184170070616433736L;

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
		protected final Map<K, V> itemMap() {
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
		 * @see CompactBase#equalsIndex(Object, int)
		 * @see CompactBase#compareIndex(Object, int)
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
					return new CompactAscendingMapValueIterator(0, CompactMap.this.size);
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
					return new CompactAscendingMapKeyIterator(0, CompactMap.this.size);
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
			return this.itemMap().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Map<?, ?>)) return false;
			return this.itemMap().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.itemMap().toString();
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
					return new CompactAscendingMapEntryIterator(0, CompactMap.this.size);
				}

			};
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
		 * @see CompactBase#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactItemMap(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen.
		 * 
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 */
		public CompactItemMap(final Map<? extends K, ? extends V> map) {
			super(map);
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
			for(int i = 0, size = this.size; i < size; i++){
				if(value.equals(this.items[i])) return true;
			}
			return false;
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
		 * @see CompactBase#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactItemHashMap(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen.
		 * 
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 */
		public CompactItemHashMap(final Map<? extends K, ? extends V> map) {
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
		protected Object[] values = CompactBase.ITEMS;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map}.
		 */
		public CompactEntryMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactBase#allocate(int)
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
			this.values = CompactBase.insertItems(this.values, size, this.items.length, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			super.removeItems(index, count);
			this.values = CompactBase.removeItems(this.values, size, this.items.length, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void allocateItems(final int count) {
			super.allocateItems(count);
			this.values = CompactBase.resizeItems(this.values, this.size, this.items.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void compactItems() {
			super.compactItems();
			this.values = CompactBase.resizeItems(this.values, this.size, this.items.length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			if(value == null){
				for(int i = 0, size = this.size; i < size; i++){
					if(this.values[i] == null) return true;
				}
			}else{
				for(int i = 0, size = this.size; i < size; i++){
					if(value.equals(this.values[i])) return true;
				}
			}
			return false;
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
		 * @see CompactBase#allocate(int)
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

	static abstract class CompactSortedMap<K, V> extends CompactMap<K, V> implements SortedMap<K, V> {
	
		/**
		 * Dieses Feld speichert den {@link Comparator Comparator}.
		 */
		protected final Comparator<? super K> comparator;
	
		public CompactSortedMap(final Comparator<? super K> comparator) {
			super();
			this.comparator = comparator;
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
	
		@Override
		public Comparator<? super K> comparator() {
			return null;
		}
	
		@Override
		public K firstKey() {
			if(this.size == 0) throw new NoSuchElementException();
			return this.getKey(0);
		}
	
		@Override
		public K lastKey() {
			final int size = this.size;
			if(size == 0) throw new NoSuchElementException();
			return this.getKey(size - 1);
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

}
