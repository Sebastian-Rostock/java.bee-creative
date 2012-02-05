package bee.creative.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Diese Klasse implementiert Hilfsklassen zur Verwaltung von Elemente in {@link Set Sets}, {@link NavigableSet
 * Navigable-Sets}, {@link Map Maps} und {@link NavigableMap Navigable-Maps} mit minimalem Speicherverbrauch.
 * 
 * @author Sebastian Rostock 2012.
 */
public final class Compact {

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
		 * @param <GData> Typ des {@link CompactData Compact-Datas}.
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
				this.data.removeItems(item, 1);
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
			 * Dieses Feld speichert das Objekt zur offenen Begrenzung sortierter Teilmenge.
			 */
			protected static final Object OPEN = new Object();

			/**
			 * Dieses Feld speichert das erste Element oder {@link CompactSubData#OPEN}.
			 */
			protected final Object fromItem;

			/**
			 * Dieses Feld speichert {@code true}, wenn das erste Element inklusiv ist.
			 */
			protected final boolean fromInclusive;

			/**
			 * Dieses Feld speichert das letzte Element oder {@link CompactSubData#OPEN}.
			 */
			protected final Object lastItem;

			/**
			 * Dieses Feld speichert {@code true}, wenn das letzte Element inklusiv ist.
			 */
			protected final boolean lastInclusive;

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactData Compact-Data} und die Grenzen und deren Inklusion.
			 * 
			 * @param data {@link CompactData Compact-Data}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */
			public CompactSubData(final GData data, final Object fromItem, final boolean fromInclusive,
				final Object lastItem, final boolean lastInclusive) throws IllegalArgumentException {
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
			 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und
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
			 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und
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
		 * Diese Methode entferntim gegebenen {@link Array Array} mit der gegebenen Belegung an der gegebenen Position die
		 * gegebene Anzahl an Elementen, setzt die Länge des {@link Array Array} auf die gegebene Länge und gibt es zurück.
		 * Wenn die gegebene Länge {@code 0} ist, wird {@link CompactData#ITEMS} zurück gegeben. Wenn die Länge des
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element in der Belegung des gegebenen
		 * {@link Array Arrays} enthalten ist.
		 * 
		 * @see Object#equals(Object)
		 * @param items {@link Array Array}.
		 * @param size Anzahl der belegten Elemente.
		 * @param value Element.
		 * @return {@code true}, wenn das gegebene Element in der Belegung enthalten ist.
		 * @throws NullPointerException Wenn des gegebenen {@link Array Array} {@code null} ist.
		 */
		public static boolean containsItem(final Object[] items, final int size, final Object value)
			throws NullPointerException {
			if(items == null) throw new NullPointerException("Items is null");
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
		 * {@code (compare(key, hash, element) == 0) && equals(key, hash, element)}.
		 * 
		 * @see CompactData#equals(Object, int, Object)
		 * @see CompactData#compare(Object, int, Object)
		 * @see CompactData#compareIndex(Object, int)
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
		 * @see Comparator#compare(Object, Object)
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
		 * Diese Methode vergrößert die Kapazität des {@link Array Arrays}, sodass dieses die gegebene Anzahl an Elementen
		 * verwalten kann.
		 * 
		 * @see CompactData#validAllocateLength(int)
		 * @param count Anzahl.
		 */
		protected void allocateItems(final int count) {
			this.items = CompactData.resizeItems(this.items, this.size, this.validAllocateLength(count));
		}

		/**
		 * Diese Methode verkleinert die Kapazität des {@link Array Arrays} auf das Minimum für seine Belegung.
		 * 
		 * @see CompactData#validCompactLength()
		 */
		protected void compactItems() {
			this.items = CompactData.resizeItems(this.items, this.size, this.validCompactLength());
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} zurück, um darin die gegebene Anzahl an Elementen
		 * verwalten zu können.
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
		 * Diese Methode gibt die neue Länge für das {@link Array Array} zurück, um darin die gegebene Anzahl an Elementen
		 * verwalten zu können. Sie wird beim Einfügen von Elementen aufgerufen.
		 * 
		 * @see CompactData#validLength(int)
		 * @see CompactData#insertItems(int, int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validInsertLength(final int count) {
			return this.validLength(count);
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} zurück, um darin die gegebene Anzahl an Elementen
		 * verwalten zu können. Sie wird beim Entfernen von Elementen aufgerufen.
		 * 
		 * @see CompactData#removeItems(int, int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validRemoveLength(final int count) {
			return this.items.length;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} zurück, um darin alle vorhandenen Elemente
		 * (Belegung) verwalten zu können. Sie wird beim Kompaktieren aufgerufen.
		 * 
		 * @see CompactData#size
		 * @see CompactData#compactItems()
		 * @return Länge.
		 */
		protected int validCompactLength() {
			return this.size;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} zurück, um darin die gegebene Anzahl an Elementen
		 * verwalten zu können. Sie wird beim Reservieren von Elementen aufgerufen.
		 * 
		 * @see CompactData#validLength(int)
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
		 * @see CompactData#allocate(int)
		 * @param collection Elemente.
		 * @throws NullPointerException Wenn die gegebene {@link Collection Collection} {@code null} ist.
		 */
		public CompactSet(final Collection<? extends GItem> collection) {
			if(collection == null) throw new NullPointerException("Collection is null");
			this.allocate(collection.size());
			this.addAll(collection);
		}

		/**
		 * Diese Methode implementiert {@link Set#addAll(Collection)}.
		 * 
		 * @see Set#addAll(Collection)
		 * @param <GItem> Typ der Elemente.
		 * @param set {@link Set Set}.
		 * @param collection {@link Collection Collection}.
		 * @return {@code true} bei Veränderungen.
		 */
		public static <GItem> boolean addAll(final Set<GItem> set, final Collection<? extends GItem> collection) {
			boolean modified = false;
			for(final GItem item: collection)
				if(set.add(item)){
					modified = true;
				}
			return modified;
		}

		/**
		 * Diese Methode implementiert {@link Set#retainAll(Collection)}.
		 * 
		 * @see Set#retainAll(Collection)
		 * @param <GItem> Typ der Elemente.
		 * @param set {@link Set Set}.
		 * @param collection {@link Collection Collection}.
		 * @return {@code true} bei Veränderungen.
		 */
		public static <GItem> boolean retainAll(final Set<GItem> set, final Collection<?> collection) {
			boolean modified = false;
			for(final Iterator<?> iterator = set.iterator(); iterator.hasNext();){
				if(!collection.contains(iterator.next())){
					iterator.remove();
					modified = true;
				}
			}
			return modified;
		}

		/**
		 * Diese Methode implementiert {@link Set#removeAll(Collection)}.
		 * 
		 * @see Set#removeAll(Collection)
		 * @param <GItem> Typ der Elemente.
		 * @param set {@link Set Set}.
		 * @param collection {@link Collection Collection}.
		 * @return {@code true} bei Veränderungen.
		 */
		public static <GItem> boolean removeAll(final Set<GItem> set, final Collection<?> collection) {
			boolean modified = false;
			for(final Iterator<?> iterator = set.iterator(); iterator.hasNext();){
				if(collection.contains(iterator.next())){
					iterator.remove();
					modified = true;
				}
			}
			return modified;
		}

		/**
		 * Diese Methode implementiert {@link Set#containsAll(Collection)}.
		 * 
		 * @see Set#containsAll(Collection)
		 * @param <GItem> Typ der Elemente.
		 * @param set {@link Set Set}.
		 * @param collection {@link Collection Collection}.
		 * @return {@code true} bei Vollständigkeit.
		 */
		public static <GItem> boolean containsAll(final Set<GItem> set, final Collection<?> collection) {
			for(final Object item: collection)
				if(!set.contains(item)) return false;
			return true;
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
		public boolean contains(final Object key) {
			return this.itemIndex(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GItem> collection) {
			return CompactSet.addAll(this, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> collection) {
			return CompactSet.retainAll(this, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> collection) {
			return CompactSet.removeAll(this, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> collection) {
			return CompactSet.containsAll(this, collection);
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
	 * <p>
	 * Der Speicherverbrauch eines {@link CompactHashSet Compact-Hash-Sets} liegt bei ca. {@code 13%} des
	 * Speicherverbrauchs eines {@link HashSet Hash-Sets}. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen
	 * sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca.
	 * {@code 245%} der Rechenzeit, die ein {@link HashSet Hash-Set} dazu benötigen würde. Bei einer Anzahl von ca.
	 * {@code 100} Elementen benötigen Beide {@link Set Sets} dafür in etwa die gleichen Rechenzeiten. Bei weniger
	 * Elementen ist das {@link CompactHashSet Compact-Hash-Set} schneller, bei mehr Elementen ist das {@link HashSet
	 * Hash-Set} schneller. Für das Finden von Elementen und das Iterieren über die Elemente benötigt das
	 * {@link CompactHashSet Compact-Hash-Set} im Mittel nur noch {@code 75%} der Rechenzeit des {@link HashSet Hash-Sets}
	 * , unabhängig von der Anzahl der Elemente.
	 * <p>
	 * Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit
	 * beim Hinzufügen und Entfernen von Elementen in einem {@link CompactHashSet Compact-Hash-Set} auf ca. {@code 8800z%}
	 * der Rechenzeit, die ein {@link HashSet Hash-Set} hierfür benötigen würde.
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
		 * @see CompactData#allocate(int)
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
	 * verwaltet werden. Der Speicherverbrauch eines {@link CompactNavigableSet Compact-Navigable-Sets} liegt bei ca.
	 * {@code 13%} des Speicherverbrauchs eines {@link TreeSet Tree-Sets}. Die Rechenzeiten beim Hinzufügen und Entfernen
	 * von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im
	 * Mittel auf ca. {@code 208%} der Rechenzeit, die ein {@link TreeSet Tree-Set} dazu benötigen würde. Bei einer Anzahl
	 * von ca. {@code 8000} Elementen benötigen Beide {@link NavigableSet Navigable-Sets} dafür in etwa die gleichen
	 * Rechenzeiten. Bei weniger Elementen ist das {@link CompactNavigableSet Compact-Navigable-Set} schneller, bei mehr
	 * Elementen ist das {@link TreeSet Tree-Set} schneller. Für das Finden von Elementen und das Iterieren über die
	 * Elemente benötigt das {@link CompactNavigableSet Compact-Navigable-Set} im Mittel nur noch {@code 25%} bzw.
	 * {@code 75%} der Rechenzeit des {@link TreeSet Tree-Sets}, unabhängig von der Anzahl der Elemente.
	 * <p>
	 * Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit
	 * beim Hinzufügen und Entfernen von Elementen in einem {@link CompactNavigableSet Compact-Navigable-Set} auf ca.
	 * {@code 3900%} der Rechenzeit, die ein {@link TreeSet Tree-Set} hierfür benötigen würde.
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
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */
			public CompactNavigableSubSet(final CompactNavigableSet<GItem> set, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
				throws IllegalArgumentException {
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
			public void clear() {
				this.clearItems();
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
			public boolean contains(final Object item) {
				return this.isInRange(item) && this.data.contains(item);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean addAll(final Collection<? extends GItem> collection) {
				return CompactSet.addAll(this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean retainAll(final Collection<?> collection) {
				return CompactSet.retainAll(this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean removeAll(final Collection<?> collection) {
				return CompactSet.removeAll(this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean containsAll(final Collection<?> collection) {
				return CompactSet.containsAll(this, collection);
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
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */
			public CompactAscendingSubSet(final CompactNavigableSet<GItem> array, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
				throws IllegalArgumentException {
				super(array, fromItem, fromInclusive, lastItem, lastInclusive);
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
				return this.data.poll(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem pollLast() {
				return this.data.poll(this.highestIndex());
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
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */
			public CompactDescendingSubSet(final CompactNavigableSet<GItem> array, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
				throws IllegalArgumentException {
				super(array, fromItem, fromInclusive, lastItem, lastInclusive);
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
				return this.data.poll(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem pollLast() {
				return this.data.poll(this.lowestIndex());
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
		protected final GItem poll(final int index) {
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
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public abstract static class CompactMap<GKey, GValue> extends CompactData implements Map<GKey, GValue> {

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		protected static final class CompactMapKeyAscendingIterator<GKey> extends
			CompactAscendingIterator<GKey, CompactMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapKeyAscendingIterator(final CompactMap<GKey, ?> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected GKey next(final int index) {
				return this.data.getKey(index);
			}

		}

		/**
		 * Diese Klasse implementiert den absteigenden {@link Iterator Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		protected static final class CompactMapKeyDescendingIterator<GKey> extends
			CompactDescendingIterator<GKey, CompactMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapKeyDescendingIterator(final CompactMap<GKey, ?> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected GKey next(final int index) {
				return this.data.getKey(index);
			}

		}

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator Iterator} der Werte.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <V> Typ der Werte.
		 */
		protected static final class CompactMapValueIterator<V> extends CompactAscendingIterator<V, CompactMap<?, V>> {

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
		 * @param <GKey> Typ der Schlüssel.
		 * @param <V> Typ der Werte.
		 */
		protected static final class CompactMapEntryIterator<GKey, V> extends
			CompactAscendingIterator<Entry<GKey, V>, CompactMap<GKey, V>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap Compact-Map} und Indizes.
			 * 
			 * @param map {@link CompactMap Compact-Map}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactMapEntryIterator(final CompactMap<GKey, V> map, final int from, final int last) {
				super(map, from, last);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected Entry<GKey, V> next(final int index) {
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
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 */
		public CompactMap(final Map<? extends GKey, ? extends GValue> map) {
			this.allocate(map.size());
			this.putAll(map);
		}

		/**
		 * Diese Methode gibt den Schlüssel des gegebenen Werts zurück.
		 * 
		 * @param value Wert.
		 * @return Schlüssel.
		 */
		protected abstract GKey getKey(final GValue value);

		/**
		 * Diese Methode setzt den Schlüssel des gegebenen Werts.
		 * 
		 * @param key Schlüssel.
		 * @param value Wert.
		 */
		protected abstract void setKey(GKey key, final GValue value);

		/**
		 * Diese Methode gibt den Schlüssel des {@code index}-ten Elements zurück.
		 * 
		 * @param index Index.
		 * @return Schlüssel des {@code index}-ten Elements.
		 */
		protected abstract GKey getKey(int index);

		/**
		 * Diese Methode gibt den Wert des {@code index}-ten Elements zurück.
		 * 
		 * @param index Index.
		 * @return Wert des {@code index}-ten Elements.
		 */
		protected abstract GValue getValue(int index);

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element
		 */
		protected final Entry<GKey, GValue> getEntry(final int index) {
			return new SimpleEntry<GKey, GValue>(this.getKey(index), this.getValue(index)) {

				private static final long serialVersionUID = -543360027933297926L;

				@Override
				public GValue setValue(final GValue value) {
					final GValue v = super.setValue(value);
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
		protected abstract void setEntry(int index, GKey key, GValue value);

		/**
		 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
		 * 
		 * @see AbstractSet
		 * @return {@link Set Set}.
		 */
		protected final Map<GKey, GValue> getItemMap() {
			return new AbstractMap<GKey, GValue>() {

				@Override
				public Set<Entry<GKey, GValue>> entrySet() {
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
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GValue> values() {
			return new AbstractCollection<GValue>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public void clear() {
					CompactMap.this.clear();
				}

				@Override
				public Iterator<GValue> iterator() {
					return new CompactMapValueIterator<GValue>(CompactMap.this, 0, CompactMap.this.size);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<GKey> keySet() {
			return new AbstractSet<GKey>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public void clear() {
					CompactMap.this.clear();
				}

				@Override
				public Iterator<GKey> iterator() {
					return new CompactMapKeyAscendingIterator<GKey>(CompactMap.this, 0, CompactMap.this.size);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return new AbstractSet<Entry<GKey, GValue>>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public void clear() {
					CompactMap.this.clear();
				}

				@Override
				public Iterator<Entry<GKey, GValue>> iterator() {
					return new CompactMapEntryIterator<GKey, GValue>(CompactMap.this, 0, CompactMap.this.size);
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
		public GValue get(final Object key) {
			final int index = this.itemIndex(key);
			if(index < 0) return null;
			return this.getValue(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
			int index = this.itemIndex(key);
			if(index >= 0){
				final GValue item = this.getValue(index);
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
		public void putAll(final Map<? extends GKey, ? extends GValue> map) {
			for(final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()){
				this.put(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue remove(final Object key) {
			final int index = this.itemIndex(key);
			if(index < 0) return null;
			final GValue item = this.getValue(index);
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
	 * @see CompactMap#getKey(Object)
	 * @see CompactMap#setKey(Object, Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactItemMap<GKey, GValue> extends CompactMap<GKey, GValue> {

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
		protected final GKey getKey(final int index) {
			return this.getKey(this.getValue(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected final GValue getValue(final int index) {
			return (GValue)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setEntry(final int index, final GKey key, final GValue value) {
			if(value == null) throw new NullPointerException();
			this.items[index] = value;
			this.setKey(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
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
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactItemHashMap<GKey, GValue> extends CompactItemMap<GKey, GValue> {

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
			if(key == null) return this.getKey((GValue)item) == null;
			return key.equals(this.getKey((GValue)item));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			final Object value = this.getKey((GValue)item);
			if(value == null) return hash;
			return Integer.compare(hash, value.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link Map Map}, deren Schlüssel und Werte in je einem {@link Array
	 * Array} verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactEntryMap<GKey, GValue> extends CompactMap<GKey, GValue> {

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
		public CompactEntryMap(final Map<? extends GKey, ? extends GValue> map) {
			super(map);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GKey getKey(final GValue value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setKey(final GKey key, final GValue value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected GKey getKey(final int index) {
			return (GKey)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected GValue getValue(final int index) {
			return (GValue)this.values[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntry(final int index, final GKey key, final GValue value) {
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
	 * Compact-Entry-Map}. Der Speicherverbrauch einer {@link CompactEntryHashMap Compact-Entry-Hash-Map} liegt bei ca.
	 * {@code 28%} des Speicherverbrauchs eines {@link HashMap Hash-Map}.
	 * <p>
	 * Eine {@link HashMap HashMap} ist immer schneller als eine {@link CompactEntryHashMap Compact-Entry-Hash-Map}.
	 * <p>
	 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen
	 * sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 150%}. Bei der erhöhung der Anzahl der Elemente
	 * auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von
	 * Elementen in einer {@link CompactEntryHashMap Compact-Entry-Hash-Map} auf ca. {@code 760%} der Rechenzeit, die eine
	 * {@link HashMap Hash-Map} hierfür benötigen würde.
	 * <p>
	 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map Maps} in etwa die
	 * gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static class CompactEntryHashMap<GKey, GValue> extends CompactEntryMap<GKey, GValue> {

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
		public CompactEntryHashMap(final Map<? extends GKey, ? extends GValue> map) {
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

	/**
	 * Diese Klasse implementiert eine abstrakte {@link NavigableMap Navigable-Map}, deren Daten in einem {@link Array
	 * Array} verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactNavigableMap<GKey, GValue> extends CompactMap<GKey, GValue> implements
		NavigableMap<GKey, GValue> {

		/**
		 * Diese Klasse implementiert die anstrakte Menge der Schlüssel einer {@link NavigableMap Navigable-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GData> Typ der {@link NavigableMap Navigable-Map}.
		 */
		protected static abstract class CompactNavigableKeySet<GKey, GData extends NavigableMap<GKey, ?>> extends
			AbstractSet<GKey> implements NavigableSet<GKey> {

			/**
			 * Dieses Feld speichert die {@link NavigableMap Navigable-Map}.
			 */
			protected final GData data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link NavigableMap Navigable-Map}.
			 * 
			 * @param data {@link NavigableMap Navigable-Map}.
			 * @throws NullPointerException Wenn die gegebene {@link NavigableMap Navigable-Map} {@code null} ist.
			 */
			public CompactNavigableKeySet(final GData data) throws NullPointerException {
				if(data == null) throw new NullPointerException("Data is null");
				this.data = data;
			}

			/**
			 * Diese Methode gibt den Schlüssel des gegebenen {@link java.util.Map.Entry Entries} oder {@code null} zurück.
			 * 
			 * @param entry {@link java.util.Map.Entry Entry}.
			 * @return Schlüssel oder {@code null}.
			 */
			protected final GKey getKeyOrNull(final Entry<GKey, ?> entry) {
				if(entry == null) return null;
				return entry.getKey();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.data.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void clear() {
				this.data.clear();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean isEmpty() {
				return this.data.isEmpty();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super GKey> comparator() {
				return this.data.comparator();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public SortedSet<GKey> subSet(final GKey fromElement, final GKey toElement) {
				return this.subSet(fromElement, true, toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> subSet(final GKey fromElement, final boolean fromInclusive, final GKey toElement,
				final boolean toInclusive) {
				return this.data.subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public SortedSet<GKey> headSet(final GKey toElement) {
				return this.headSet(toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> headSet(final GKey toElement, final boolean inclusive) {
				return this.data.headMap(toElement, inclusive).navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public SortedSet<GKey> tailSet(final GKey fromElement) {
				return this.tailSet(fromElement, true);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> tailSet(final GKey fromElement, final boolean inclusive) {
				return this.data.tailMap(fromElement, inclusive).navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean remove(final Object key) {
				if(!this.data.containsKey(key)) return false;
				this.data.remove(key);
				return true;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean contains(final Object key) {
				return this.data.containsKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey first() {
				return this.data.firstKey();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey last() {
				return this.data.lastKey();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey lower(final GKey key) {
				return this.data.lowerKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey floor(final GKey key) {
				return this.data.floorKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey ceiling(final GKey key) {
				return this.data.ceilingKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey higher(final GKey key) {
				return this.data.higherKey(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey pollFirst() {
				return this.getKeyOrNull(this.data.pollFirstEntry());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey pollLast() {
				return this.getKeyOrNull(this.data.pollLastEntry());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> descendingSet() {
				return this.data.descendingMap().navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GKey> descendingIterator() {
				return this.descendingSet().iterator();
			}

		}

		/**
		 * Diese Klasse implementiert die aufsteigende Menge der Schlüssel einer {@link CompactAscendingSubMap
		 * Compact-Ascending-Sub-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		protected static final class CompactAscendingKeySet<GKey> extends
			CompactNavigableKeySet<GKey, CompactAscendingSubMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactAscendingSubMap Compact-Ascending-Sub-Map}.
			 * 
			 * @param data {@link CompactAscendingSubMap Compact-Ascending-Sub-Map}.
			 * @throws NullPointerException Wenn die gegebene {@link CompactAscendingSubMap Compact-Ascending-Sub-Map}
			 *         {@code null} ist.
			 */
			public CompactAscendingKeySet(final CompactAscendingSubMap<GKey, ?> data) throws NullPointerException {
				super(data);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GKey> iterator() {
				return new CompactMapKeyAscendingIterator<GKey>(this.data.data, this.data.firstIndex(),
					this.data.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert die abfsteigende Menge der Schlüssel einer {@link CompactDescendingSubMap
		 * Compact-Descending-Sub-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		protected static final class CompactDescendingKeySet<GKey> extends
			CompactNavigableKeySet<GKey, CompactDescendingSubMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactDescendingSubMap Compact-Descending-Sub-Map}.
			 * 
			 * @param data {@link CompactDescendingSubMap Compact-Descending-Sub-Map}.
			 * @throws NullPointerException Wenn die gegebene {@link CompactDescendingSubMap Compact-Descending-Sub-Map}
			 *         {@code null} ist.
			 */
			public CompactDescendingKeySet(final CompactDescendingSubMap<GKey, ?> data) throws NullPointerException {
				super(data);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<GKey> iterator() {
				return new CompactMapKeyDescendingIterator<GKey>(this.data.data, this.data.firstIndex(),
					this.data.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert eine abstrakte Teilmenge einer {@link CompactNavigableMap Compact-Navigable-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		protected static abstract class CompactNavigableSubMap<GKey, GValue> extends
			CompactSubData<CompactNavigableMap<GKey, GValue>> implements NavigableMap<GKey, GValue> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap Compact-Navigable-Map} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableMap Compact-Navigable-Map}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */
			public CompactNavigableSubMap(final CompactNavigableMap<GKey, GValue> map, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive) {
				super(map, fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * Diese Methode gibt eine neue {@link Map Map} zurück, das aus dem Einträgen erzeugt wird.
			 * 
			 * @see AbstractMap
			 * @return {@link Map Map}.
			 */
			protected final Map<GKey, GValue> getItemMap() {
				return new AbstractMap<GKey, GValue>() {

					@Override
					public Set<java.util.Map.Entry<GKey, GValue>> entrySet() {
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
			public NavigableMap<GKey, GValue> subMap(final GKey fromElement, final GKey toElement) {
				return this.subMap(fromElement, true, toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> headMap(final GKey toElement) {
				return this.headMap(toElement, false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> tailMap(final GKey fromElement) {
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
			public GValue get(final Object key) {
				if(!this.isInRange(key)) return null;
				return this.data.get(key);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GValue put(final GKey key, final GValue value) {
				if(!this.isInRange(key)) throw new IllegalArgumentException("Entry out of range");
				return this.data.put(key, value);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void putAll(final Map<? extends GKey, ? extends GValue> map) {
				for(final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()){
					this.put(entry.getKey(), entry.getValue());
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GValue remove(final Object key) {
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
			public Set<GKey> keySet() {
				return this.navigableKeySet();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Collection<GValue> values() {
				return new AbstractCollection<GValue>() {

					@Override
					public int size() {
						return CompactNavigableSubMap.this.size();
					}

					@Override
					public Iterator<GValue> iterator() {
						return new CompactMapValueIterator<GValue>(CompactNavigableSubMap.this.data,
							CompactNavigableSubMap.this.firstIndex(), CompactNavigableSubMap.this.lastIndex() + 1);
					}

				};
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Set<Entry<GKey, GValue>> entrySet() {
				return new AbstractSet<Entry<GKey, GValue>>() {

					@Override
					public int size() {
						return CompactNavigableSubMap.this.size();
					}

					@Override
					public Iterator<Entry<GKey, GValue>> iterator() {
						return new CompactMapEntryIterator<GKey, GValue>(CompactNavigableSubMap.this.data,
							CompactNavigableSubMap.this.firstIndex(), CompactNavigableSubMap.this.lastIndex() - 1);
					}

				};
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> descendingKeySet() {
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
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		protected static final class CompactAscendingSubMap<GKey, GValue> extends CompactNavigableSubMap<GKey, GValue> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap Compact-Navigable-Map} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableMap Compact-Navigable-Map}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */
			public CompactAscendingSubMap(final CompactNavigableMap<GKey, GValue> map, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
				throws IllegalArgumentException {
				super(map, fromItem, fromInclusive, lastItem, lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super GKey> comparator() {
				return this.data.comparator;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey firstKey() {
				return this.data.getKeyOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> firstEntry() {
				return this.data.getEntryOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey lastKey() {
				return this.data.getKeyOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> lastEntry() {
				return this.data.getEntryOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey lowerKey(final GKey key) {
				return this.data.getKeyOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> lowerEntry(final GKey key) {
				return this.data.getEntryOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey floorKey(final GKey key) {
				return this.data.getKeyOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> floorEntry(final GKey key) {
				return this.data.getEntryOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey ceilingKey(final GKey key) {
				return this.data.getKeyOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> ceilingEntry(final GKey key) {
				return this.data.getEntryOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey higherKey(final GKey key) {
				return this.data.getKeyOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> higherEntry(final GKey key) {
				return this.data.getEntryOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> pollFirstEntry() {
				return this.data.poll(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> pollLastEntry() {
				return this.data.poll(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> descendingMap() {
				return new CompactDescendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, this.lastItem,
					this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> navigableKeySet() {
				return new CompactAscendingKeySet<GKey>(this);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey,
				final boolean toInclusive) {
				if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("FromElement out of range");
				if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactAscendingSubMap<GKey, GValue>(this.data, fromKey, fromInclusive, toKey, toInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
				if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactAscendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, toKey, inclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
				if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("FromElement out of range");
				return new CompactAscendingSubMap<GKey, GValue>(this.data, fromKey, inclusive, this.lastItem,
					this.lastInclusive);
			}

		}

		/**
		 * Diese Klasse implementiert die absteigende Teilmenge einer {@link CompactNavigableMap Compact-Navigable-Map}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		protected static final class CompactDescendingSubMap<GKey, GValue> extends CompactNavigableSubMap<GKey, GValue> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet Compact-Navigable-Set} und die Grenzen und
			 * deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableSet Compact-Navigable-Set}.
			 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
			 * @param fromInclusive Inklusivität des ersten Elements.
			 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
			 * @param lastInclusive Inklusivität des letzten Elements.
			 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
			 */

			public CompactDescendingSubMap(final CompactNavigableMap<GKey, GValue> map, final Object fromItem,
				final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
				throws IllegalArgumentException {
				super(map, fromItem, fromInclusive, lastItem, lastInclusive);

			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super GKey> comparator() {
				return Collections.reverseOrder(this.data.comparator);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey firstKey() {
				return this.data.getKeyOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> firstEntry() {
				return this.data.getEntryOrException(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey lastKey() {
				return this.data.getKeyOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> lastEntry() {
				return this.data.getEntryOrException(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey lowerKey(final GKey key) {
				return this.data.getKeyOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> lowerEntry(final GKey key) {
				return this.data.getEntryOrNull(this.higherIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey floorKey(final GKey key) {
				return this.data.getKeyOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> floorEntry(final GKey key) {
				return this.data.getEntryOrNull(this.ceilingIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey ceilingKey(final GKey key) {
				return this.data.getKeyOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> ceilingEntry(final GKey key) {
				return this.data.getEntryOrNull(this.floorIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GKey higherKey(final GKey key) {
				return this.data.getKeyOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> higherEntry(final GKey key) {
				return this.data.getEntryOrNull(this.lowerIndex(key));
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> pollFirstEntry() {
				return this.data.poll(this.highestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Entry<GKey, GValue> pollLastEntry() {
				return this.data.poll(this.lowestIndex());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> descendingMap() {
				return new CompactAscendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, this.lastItem,
					this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GKey> navigableKeySet() {
				return new CompactDescendingKeySet<GKey>(this);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey,
				final boolean toInclusive) {
				if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("FromElement out of range");
				if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactDescendingSubMap<GKey, GValue>(this.data, toKey, toInclusive, fromKey, fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
				if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("ToElement out of range");
				return new CompactDescendingSubMap<GKey, GValue>(this.data, toKey, inclusive, this.fromItem, this.fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
				if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("FromElement out of range");
				return new CompactDescendingSubMap<GKey, GValue>(this.data, this.lastItem, this.lastInclusive, fromKey,
					inclusive);
			}

		}

		/**
		 * Dieses Feld speichert den {@link Comparator Comparator}.
		 */
		protected final Comparator<? super GKey> comparator;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit dem gegebenen {@link Comparator Comparator}.
		 * 
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableMap(final Comparator<? super GKey> comparator) throws NullPointerException {
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableMap(final int capacity, final Comparator<? super GKey> comparator)
			throws NullPointerException {
			super(capacity);
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableMap(final Map<? extends GKey, ? extends GValue> map,
			final Comparator<? super GKey> comparator) throws NullPointerException {
			super(map);
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;

		}

		/**
		 * Diese Methode löscht das {@code index}-te Element und gibt es oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Element oder {@code null}.
		 */
		protected final Entry<GKey, GValue> poll(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			final Entry<GKey, GValue> item = this.getEntry(index);
			this.removeItems(index, 1);
			return item;
		}

		/**
		 * Diese Methode gibt den {@code index}-ten Schlüssel oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-ter Schlüssel oder {@code null}.
		 */
		protected final GKey getKeyOrNull(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			return this.getKey(index);
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element oder {@code null}.
		 */
		protected final Entry<GKey, GValue> getEntryOrNull(final int index) {
			if((index < 0) || (index >= this.size)) return null;
			return this.getEntry(index);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten Schlüssel zurück oder wirft eine {@link NoSuchElementException}.
		 * 
		 * @param index Index.
		 * @return {@code index}-ter Schlüssel.
		 * @throws NoSuchElementException Wenn der gegebene Index ungültig ist.
		 */
		protected final GKey getKeyOrException(final int index) throws NoSuchElementException {
			if((index < 0) || (index >= this.size)) throw new NoSuchElementException();
			return this.getKey(index);
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück oder wirft eine {@link NoSuchElementException}.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws NoSuchElementException Wenn der gegebene Index ungültig ist.
		 */
		protected final Entry<GKey, GValue> getEntryOrException(final int index) throws NoSuchElementException {
			if((index < 0) || (index >= this.size)) throw new NoSuchElementException();
			return this.getEntry(index);
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
		public Comparator<? super GKey> comparator() {
			return this.comparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GKey firstKey() {
			return this.getKeyOrException(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> firstEntry() {
			return this.getEntryOrException(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GKey lowerKey(final GKey key) {
			return this.getKeyOrNull(this.lowerIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> lowerEntry(final GKey key) {
			return this.getEntryOrNull(this.lowerIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GKey floorKey(final GKey key) {
			return this.getKeyOrNull(this.floorIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> floorEntry(final GKey key) {
			return this.getEntryOrNull(this.floorIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GKey ceilingKey(final GKey key) {
			return this.getKeyOrNull(this.ceilingIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> ceilingEntry(final GKey key) {
			return this.getEntryOrNull(this.ceilingIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GKey higherKey(final GKey key) {
			return this.getKeyOrNull(this.higherIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> higherEntry(final GKey key) {
			return this.getEntryOrNull(this.higherIndex(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GKey lastKey() {
			return this.getKeyOrException(this.lastIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> lastEntry() {
			return this.getEntryOrException(this.lastIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> pollFirstEntry() {
			return this.poll(this.firstIndex());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Entry<GKey, GValue> pollLastEntry() {
			return this.poll(this.lastIndex());
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
		public NavigableMap<GKey, GValue> descendingMap() {
			return new CompactDescendingSubMap<GKey, GValue>(this, CompactSubData.OPEN, true, CompactSubData.OPEN, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GKey> navigableKeySet() {
			return new CompactNavigableKeySet<GKey, CompactNavigableMap<GKey, ?>>(this) {

				@Override
				public Iterator<GKey> iterator() {
					return new CompactMapKeyAscendingIterator<GKey>(this.data, this.data.firstIndex(), this.data.lastIndex() + 1);
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GKey> descendingKeySet() {
			return this.descendingMap().navigableKeySet();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedMap<GKey, GValue> subMap(final GKey fromKey, final GKey toKey) {
			return this.subMap(fromKey, true, toKey, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey,
			final boolean toInclusive) {
			return new CompactAscendingSubMap<GKey, GValue>(this, fromKey, fromInclusive, toKey, toInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedMap<GKey, GValue> headMap(final GKey toKey) {
			return this.headMap(toKey, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
			return new CompactAscendingSubMap<GKey, GValue>(this, CompactSubData.OPEN, true, toKey, inclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedMap<GKey, GValue> tailMap(final GKey fromKey) {
			return this.tailMap(fromKey, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
			return new CompactAscendingSubMap<GKey, GValue>(this, fromKey, inclusive, CompactSubData.OPEN, true);
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap Compact-Navigable-Map}, deren Daten in einem
	 * {@link Array Array} verwaltet werden und ihren Schlüssel selbst referenzieren. Diese Implementation erlaubt deshalb
	 * {@code null} nicht als Wert.
	 * 
	 * @see CompactMap#getKey(Object)
	 * @see CompactMap#setKey(Object, Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactNavigableItemMap<GKey, GValue> extends CompactNavigableMap<GKey, GValue> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit dem gegebenen {@link Comparator Comparator}.
		 * 
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableItemMap(final Comparator<? super GKey> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableItemMap(final int capacity, final Comparator<? super GKey> comparator)
			throws NullPointerException {
			super(capacity, comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableItemMap(final Map<? extends GKey, ? extends GValue> map,
			final Comparator<? super GKey> comparator) throws NullPointerException {
			super(map, comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final GKey getKey(final int index) {
			return this.getKey(this.getValue(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected final GValue getValue(final int index) {
			return (GValue)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setEntry(final int index, final GKey key, final GValue value) {
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
			return this.comparator.compare((GKey)key, this.getKey((GValue)item));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
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
	 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap Compact-Navigable-Map}, deren Schlüssel und
	 * Werte in je einem {@link Array Array} verwaltet werden. Der Speicherverbrauch einer
	 * {@link CompactNavigableEntryMap Compact-Navigable-Entry-Map} liegt bei ca. {@code 28%} des Speicherverbrauchs eines
	 * {@link TreeMap Tree-Map}.
	 * <p>
	 * Eine {@link TreeMap TreeMap} ist immer schneller als eine {@link CompactNavigableEntryMap}.
	 * <p>
	 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen
	 * sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 160%} der Rechenzeit, die eine {@link TreeMap
	 * Tree-Map} dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5}
	 * Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer
	 * {@link CompactNavigableEntryMap Compact-Navigable-Entry-Map} auf ca. {@code 1050%} der Rechenzeit, die eine
	 * {@link TreeMap Tree-Map} hierfür benötigen würde.
	 * <p>
	 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map Maps} in etwa die
	 * gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.
	 * 
	 * @see CompactMap#getKey(Object)
	 * @see CompactMap#setKey(Object, Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static class CompactNavigableEntryMap<GKey, GValue> extends CompactNavigableMap<GKey, GValue> {

		/**
		 * Dieses Feld speichert die Werte.
		 */
		protected Object[] values = CompactData.ITEMS;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit dem gegebenen {@link Comparator Comparator}.
		 * 
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableEntryMap(final Comparator<? super GKey> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableEntryMap(final int capacity, final Comparator<? super GKey> comparator)
			throws NullPointerException {
			super(capacity, comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map Map} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 * @param comparator {@link Comparator Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator Comparator} {@code null} ist.
		 */
		public CompactNavigableEntryMap(final Map<? extends GKey, ? extends GValue> map,
			final Comparator<? super GKey> comparator) throws NullPointerException {
			super(map, comparator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final GKey getKey(final GValue value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setKey(final GKey key, final GValue value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected GKey getKey(final int index) {
			return (GKey)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected GValue getValue(final int index) {
			return (GValue)this.values[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setEntry(final int index, final GKey key, final GValue value) {
			this.items[index] = key;
			this.values[index] = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			return this.comparator.compare((GKey)key, (GKey)item);
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

}
