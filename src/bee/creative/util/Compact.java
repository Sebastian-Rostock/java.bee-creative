package bee.creative.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Diese Klasse implementiert Hilfsklassen zur Verwaltung von Elemente in {@link Set}s, {@link NavigableSet}s,
 * {@link Map}s und {@link NavigableMap}s mit minimalem Speicherverbrauch.
 * 
 * @author Sebastian Rostock 2012.
 */
public final class Compact {

	// TODO Prüfung
	/**
	 * Diese Klasse implementiert eine abstrakte Sammlung von Elementen, die in einem (sortierten) {@link Array} verwaltet
	 * werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class CompactData {

		/**
		 * Diese Klasse implementiert ein abstraktes Objekt mit {@link CompactData}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GData> Typ des {@link CompactData}s.
		 */
		static abstract class CompactLink<GData extends CompactData> {

			/**
			 * Dieses Feld speichert die {@link CompactData}.
			 */
			protected final GData data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactData}.
			 * 
			 * @param data {@link CompactData}.
			 */
			public CompactLink(final GData data) {
				if(data == null) throw new NullPointerException("data is null");
				this.data = data;
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten {@link CompactData}-{@link Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 * @param <GData> Typ des {@link CompactData}s.
		 */
		static abstract class CompactIterator<GItem, GData extends CompactData> extends CompactLink<GData> implements
			Iterator<GItem> {

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
			 * Dieser Konstrukteur initialisiert {@link CompactData} und Indizes.
			 * 
			 * @param data {@link CompactData}.
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
				int move = this.data.from;
				this.data.customRemove(item, 1);
				move = this.data.from - move;
				this.from += move;
				this.item = -1;
				this.last += move;
			}

		}

		/**
		 * Diese Klasse implementiert einen abstrakten aufsteigenden {@link CompactData}-{@link Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 * @param <GData> Typ des {@link CompactData}s.
		 */
		static abstract class CompactAscendingIterator<GItem, GData extends CompactData> extends
			CompactIterator<GItem, GData> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactData} und Indizes.
			 * 
			 * @param data {@link CompactData}.
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
		 * Diese Klasse implementiert einen abstrakten absteigenden {@link CompactData}-{@link Iterator}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 * @param <GData> Typ des {@link CompactData}s.
		 */
		static abstract class CompactDescendingIterator<GItem, GData extends CompactData> extends
			CompactIterator<GItem, GData> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactData} und Indizes.
			 * 
			 * @param array {@link CompactData}.
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
		 * Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactData}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GData> Typ des {@link CompactData}s.
		 */
		static abstract class CompactSubData<GData extends CompactData> extends CompactLink<GData> {

			/**
			 * Dieses Feld speichert das Objekt zur offenen Begrenzung sortierter Teilmengen.
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
			 * Dieser Konstrukteur initialisiert das {@link CompactData} und die Grenzen und deren Inklusion.
			 * 
			 * @param data {@link CompactData}.
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
						if(data.customItemCompare(fromItem, 0, lastItem) > 0)
							throw new IllegalArgumentException("fromItem > lastItem");
					}else{
						data.customItemCompare(fromItem, 0, fromItem);
					}
				}else if(lastItem != CompactSubData.OPEN){
					data.customItemCompare(lastItem, 0, lastItem);
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
				final GData data = this.data;
				return (index > data.lastIndex()) || ((index >= data.firstIndex()) && this.isTooLow(data.list[index]));
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu klein ist. Wenn das erste Element
			 * gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu klein sein. Anderenfalls gilt es als
			 * zu klein, wenn es entweder kleiner als das erste Element ist oder wenn das erste Element exklusiv ist und das
			 * gegebene Element gleich dem ersten Element ist.
			 * 
			 * @see CompactData#customItemCompare(Object, int, Object)
			 * @see CompactSubData#fromItem
			 * @see CompactSubData#fromInclusive
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element zu klein ist.
			 */
			protected final boolean isTooLow(final Object key) {
				final Object fromItem = this.fromItem;
				if(fromItem == CompactSubData.OPEN) return false;
				final int comp = this.data.customItemCompare(key, 0, fromItem);
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
				final GData data = this.data;
				return (index > data.lastIndex()) || ((index >= data.firstIndex()) && this.isTooHigh(data.list[index]));
			}

			/**
			 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu groß ist. Wenn das letzte Element
			 * gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu groß sein. Anderenfalls gilt es als zu
			 * groß, wenn es entweder größer als das letzte Element ist oder wenn das letzte Element exklusiv ist und das
			 * gegebene Element gleich dem letzten Element ist.
			 * 
			 * @see CompactData#customItemCompare(Object, int, Object)
			 * @see CompactSubData#lastItem
			 * @see CompactSubData#lastInclusive
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element zu groß ist.
			 */
			protected final boolean isTooHigh(final Object key) {
				final Object lastItem = this.lastItem;
				if(lastItem == CompactSubData.OPEN) return false;
				final int comp = this.data.customItemCompare(key, 0, lastItem);
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
			 * @see CompactData#customItemCompare(Object, int, Object)
			 * @param key Element.
			 * @return {@code true}, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt.
			 */
			protected final boolean isInClosedRange(final Object key) {
				final GData data = this.data;
				final Object fromItem = this.fromItem, lastItem = this.lastItem;
				return ((fromItem == CompactSubData.OPEN) || (data.customItemCompare(key, 0, fromItem) >= 0))
					&& ((lastItem == CompactSubData.OPEN) || (data.customItemCompare(key, 0, lastItem) <= 0));
			}

			/**
			 * Diese Methode gibt den Index des ersten Elements zurück.
			 * 
			 * @return Index des ersten Elements.
			 */
			protected final int firstIndex() {
				final GData data = this.data;
				final Object fromItem = this.fromItem;
				if(fromItem == CompactSubData.OPEN) return data.firstIndex();
				if(this.fromInclusive) return data.ceilingIndex(fromItem);
				return data.higherIndex(fromItem);
			}

			/**
			 * Diese Methode gibt den Index des letzten Elements zurück.
			 * 
			 * @return Index des letzten Elements.
			 */
			protected final int lastIndex() {
				final GData data = this.data;
				final Object lastItem = this.lastItem;
				if(lastItem == CompactSubData.OPEN) return data.lastIndex();
				if(this.lastInclusive) return data.floorIndex(lastItem);
				return data.lowerIndex(lastItem);
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
				this.data.customRemove(fromIndex, (lastIndex - fromIndex) + 1);
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
		 * Dieses Feld speichert das leere {@link Array}.
		 */
		public static final Object[] VOID = new Object[0];

		protected static final int indexOf(final Object[] list, int from, final int size, final Object item) {
			if(item == null){
				for(final int last = from + size; from < last; from++){
					if(list[from] == null) return from;
				}
			}else{
				for(final int last = from + size; from < last; from++){
					if(item.equals(list[from])) return from;
				}
			}
			return -1;
		}

		/**
		 * Dieses Feld speichert die Elemente.
		 */
		protected Object[] list = CompactData.VOID;

		/**
		 * Dieses Feld speichert den Index des ersten Elements.
		 */
		protected int from;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		protected int size;

		/**
		 * Diese Methode gibt die neue Länge für das gegebene {@link Array} zurück, um darin die gegebene Anzahl an
		 * Elementen verwalten zu können. Die Berechnung ist an die in {@link ArrayList} angelehnt.
		 * 
		 * @param list {@link Array}.
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected final int defaultLength(final Object[] list, final int count) {
			final int oldLength = list.length;
			if(oldLength >= count) return oldLength;
			final int newLength = oldLength + (oldLength >> 1);
			if(newLength >= count) return newLength;
			return count;
		}

		/**
		 * Diese Methode setzt die Größe des gegebenen {@link Array}s und gibt es zurück. Wenn die Größe des gegebenen
		 * {@link Array}s von der gegebenen Größe abweicht, werden ein neues {@link Array} mit passender Größe erzeugt, die
		 * Elemente des gegebenen {@link Array}s mittig in das neue {@link Array} kopiert und das neue {@link Array} zurück
		 * gegeben.
		 * 
		 * @param list {@link Array}.
		 * @param length neue Größe.
		 * @return (neues) {@link Array}.
		 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
		 */
		protected final Object[] defaultResize(final Object[] list, final int length) throws IllegalArgumentException {
			final int size = this.size;
			if(size > length) throw new IllegalArgumentException("size > length");
			if(length == 0) return CompactData.VOID;
			if(length == list.length) return list;
			final Object[] list2 = new Object[length];
			final int from2 = (length - size) / 2;
			System.arraycopy(list, this.from, list2, from2, size);
			this.from = from2;
			return list2;
		}

		/**
		 * Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das gegebenen {@link Array} ein
		 * und gibt das {@link Array} zurück. Wenn die Größe des gegebenen {@link Array}s nicht verändert werden muss, wird
		 * versucht, die wenigen Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben. Reicht
		 * der verfügbare Platz zum Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben und
		 * mittig im gegebenen {@link Array} ausgerichtet. Wenn die Größe des gegebenen {@link Array}s dagegen angepasst
		 * werden muss, werden ein neues {@link Array} mit passender Größe erzeugt und die Elemente des gegebenen
		 * {@link Array}s mittig in das neue {@link Array} kopiert. Die benötigte Größe wird via
		 * {@link CompactData#defaultLength(Object[], int)} ermittelt.
		 * 
		 * @see CompactData#defaultLength(Object[], int)
		 * @param list {@link Array}.
		 * @param index Index des ersten neuen Elements.
		 * @param count Anzahl der neuen Elemente.
		 * @return (neues) {@link Array}.
		 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
		 */
		protected final Object[] defaultInsert(final Object[] list, final int index, final int count)
			throws IllegalArgumentException {
			final int from = this.from;
			final int size = this.size;
			final int index2 = index - from;
			if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
			if(count < 0) throw new IllegalArgumentException("count out of range: " + count);
			if(count == 0) return list;
			final int size2 = size + count;
			final int length = this.defaultLength(list, size2);
			this.size = size2;
			if(length != list.length){
				final Object[] list2 = new Object[length];
				final int from2 = (length - size2) / 2;
				System.arraycopy(list, from, list2, from2, index2);
				System.arraycopy(list, index, list2, from2 + index2 + count, size - index2);
				this.from = from2;
				return list2;
			}
			if(index2 > (size / 2)){
				if((from + size2) <= length){
					System.arraycopy(list, index, list, index + count, size - index2);
					return list;
				}
				final int from2 = (length - size2) / 2;
				this.from = from2;
				System.arraycopy(list, from, list, from2, index2);
				System.arraycopy(list, index, list, from2 + index2 + count, size - index2);
				final int last = from + size, last2 = from2 + size2;
				if(last2 >= last) return list;
				Arrays.fill(list, last2, last, null);
				return list;
			}
			if(from >= count){
				final int from2 = from - count;
				this.from = from2;
				System.arraycopy(list, from, list, from2, index2);
				return list;
			}
			final int from2 = (length - size2) / 2;
			this.from = from2;
			System.arraycopy(list, index, list, from2 + index2 + count, size - index2);
			System.arraycopy(list, from, list, from2, index2);
			if(from >= from2) return list;
			Arrays.fill(list, from, from2, null);
			return list;
		}

		/**
		 * Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im gegebenen {@link Array} und
		 * gibt das {@link Array} zurück. Es wird versucht, die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich
		 * um die gegebene Anzahl zu verschieben.
		 * 
		 * @param list {@link Array}.
		 * @param index Index des ersten entfallenden Elements.
		 * @param count Anzahl der entfallende Elemente.
		 * @return (neues) {@link Array}.
		 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
		 */
		protected final Object[] defaultRemove(final Object[] list, final int index, final int count)
			throws IllegalArgumentException {
			final int from = this.from;
			final int size = this.size;
			final int index2 = index - from;
			if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
			final int size2 = size - count;
			if((count < 0) || (size2 < 0)) throw new IllegalArgumentException("count out of range: " + count);
			if(count == 0) return list;
			this.size = size2;
			if(size2 == 0){
				this.from = list.length / 2;
				Arrays.fill(list, from, from + size, null);
				return list;
			}
			if(index2 > (size2 / 2)){
				System.arraycopy(list, index + count, list, index, size2 - index2);
				Arrays.fill(list, from + size2, from + size, null);
				return list;
			}
			final int from2 = from + count;
			this.from = from2;
			System.arraycopy(list, from, list, from2, index2);
			Arrays.fill(list, from, from2, null);
			return list;
		}

		/**
		 * Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das {@link Array} ein.
		 * 
		 * @see CompactData#defaultInsert(Object[], int, int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void customInsert(final int index, final int count) throws IllegalArgumentException {
			this.list = this.defaultInsert(this.list, index, count);
		}

		/**
		 * Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem {@link Array} mit der
		 * gegebenen Länge der Belegung.
		 * 
		 * @see CompactData#defaultRemove(Object[], int, int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void customRemove(final int index, final int count) throws IllegalArgumentException {
			this.list = this.defaultRemove(this.list, index, count);
		}

		/**
		 * Diese Methode vergrößert die Kapazität des {@link Array}s, sodass dieses die gegebene Anzahl an Elementen
		 * verwalten kann.
		 * 
		 * @see CompactData#defaultResize(Object[], int)
		 * @param count Anzahl.
		 */
		protected void customAllocate(final int count) {
			this.list = this.defaultResize(this.list, this.defaultLength(this.list, count));
		}

		/**
		 * Diese Methode verkleinert die Kapazität des {@link Array}s auf das Minimum für seine Belegung.
		 * 
		 * @see CompactData#defaultResize(Object[], int)
		 */
		protected void customCompact() {
			this.list = this.defaultResize(this.list, this.size);
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
		protected abstract int customItemIndex(final Object item);

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
		protected abstract boolean customItemEquals(Object key, int hash, Object item);

		/**
		 * Diese Methode sucht zuerst binär und danach linear nach einem Eintrag, dessen Schlüssel gleich dem gegebenen
		 * Schlüssel ist und gibt den Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die
		 * <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste. Ein Element {@code element}
		 * ist dann zum gegebenen Schlüssel gleich, wenn {@code (compare(key, hash, element) == 0) &&
		 * equals(key, hash, element)}.
		 * 
		 * @see CompactData#customItemEquals(Object, int, Object)
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @see CompactData#compareIndex(Object, int)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int equalsIndex(final Object key, final int hash) {
			Object item;
			final int index = this.compareIndex(key, hash);
			if(index < 0) return index;
			final Object[] list = this.list;
			if(this.customItemEquals(key, hash, list[index])) return index;
			for(int next = index + 1, last = this.from + this.size; (next < last)
				&& (this.customItemCompare(key, hash, item = list[next]) == 0); next++){
				if(this.customItemEquals(key, hash, item)) return next;
			}
			for(int next = index - 1, from = this.from; (from <= next)
				&& (this.customItemCompare(key, hash, item = list[next]) == 0); next--){
				if(this.customItemEquals(key, hash, item)) return next;
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
		protected abstract int customItemCompare(Object key, int hash, Object item);

		/**
		 * Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen
		 * Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem
		 * der Eintrag eingefügt werden müsste.
		 * 
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int compareIndex(final Object key, final int hash) {
			int from = this.from, last = from + this.size;
			final Object[] list = this.list;
			while(from < last){
				final int next = (from + last) >>> 1;
				final int comp = this.customItemCompare(key, hash, list[next]);
				if(comp < 0){
					last = next;
				}else if(comp > 0){
					from = next + 1;
				}else return next;
			}
			return -(from + 1);
		}

		/**
		 * Diese Methode gibt den Index des ersten Elements zurück. Dieser Index kann den Wert {@code from+size} annehmen.
		 * 
		 * @see NavigableSet#first()
		 * @return Index des ersten Elements.
		 */
		protected final int firstIndex() {
			return this.from;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die
		 * Werte {@code from-1} und {@code from+size} annehmen.
		 * 
		 * @see NavigableSet#lower(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner dem gegebenen ist.
		 */
		protected final int lowerIndex(final Object item) {
			final int index = this.customItemIndex(item);
			if(index < 0) return -index - 2;
			return index - 1;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index
		 * kann die Werte {@code from-1} und {@code from+size} annehmen.
		 * 
		 * @see NavigableSet#floor(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist.
		 */
		protected final int floorIndex(final Object item) {
			final int index = this.customItemIndex(item);
			if(index < 0) return -index - 2;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser
		 * Index kann den Wert {@code from+size} annehmen.
		 * 
		 * @see NavigableSet#ceiling(Object)
		 * @param item Element.
		 * @return Index des kleinsten Elements, dass größer oder gleich dem gegebenen ist.
		 */
		protected final int ceilingIndex(final Object item) {
			final int index = this.customItemIndex(item);
			if(index < 0) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den
		 * Wert {@code from+size} annehmen.
		 * 
		 * @see NavigableSet#higher(Object)
		 * @param item Element.
		 * @return Index des kleinsten Elements, dass größer dem gegebenen ist.
		 */
		protected final int higherIndex(final Object item) {
			final int index = this.customItemIndex(item);
			if(index < 0) return -index - 1;
			return index + 1;
		}

		/**
		 * Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann deb Wert {@code from-1} annehmen.
		 * 
		 * @see NavigableSet#last()
		 * @return Index des letzten Elements.
		 */
		protected final int lastIndex() {
			return (this.from + this.size) - 1;
		}

		/**
		 * Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwalten kann.
		 * 
		 * @param count Anzahl.
		 */
		public final void allocate(final int count) {
			this.customAllocate(count);
		}

		/**
		 * Diese Methode verkleinert die Kapazität auf das Minimum.
		 */
		public final void compact() {
			this.customCompact();
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link Collection}, deren Elemente in einem {@link Array} verwaltet
	 * werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	static abstract class CompactCollection<GItem> extends CompactData implements Collection<GItem> {

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator} für {@link CompactCollection}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class CompactAscendingCollectionIterator<GItem> extends
			CompactAscendingIterator<GItem, CompactCollection<GItem>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactCollection} und Indizes.
			 * 
			 * @param data {@link CompactCollection}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactAscendingCollectionIterator(final CompactCollection<GItem> data, final int from, final int last) {
				super(data, from, last);
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
		 * Diese Klasse implementiert den absteigenden {@link Iterator} für {@link CompactCollection}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class CompactDescendingCollectionIterator<GItem> extends
			CompactDescendingIterator<GItem, CompactCollection<GItem>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactCollection} und Indizes.
			 * 
			 * @param data {@link CompactCollection}.
			 * @param from Index des ersten Elements (inklusiv).
			 * @param last Index des letzten Elements (exklusiv).
			 */
			public CompactDescendingCollectionIterator(final CompactCollection<GItem> data, final int from, final int last) {
				super(data, from, last);
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
		 * Dieser Konstrukteur initialisiert die {@link Collection}.
		 */
		public CompactCollection() {
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Collection} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactCollection(final int capacity) {
			this.allocate(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Collection} mit den gegebenen Elementen.
		 * 
		 * @see Collection#addAll(Collection)
		 * @see CompactData#allocate(int)
		 * @param collection Elemente.
		 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
		 */
		public CompactCollection(final Collection<? extends GItem> collection) {
			if(collection == null) throw new NullPointerException("collection is null");
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
			return (GItem)this.list[index];
		}

		/**
		 * Diese Methode setzt das {@code index}-te Element.
		 * 
		 * @param index Index.
		 * @param item Element.
		 */
		protected final void setItem(final int index, final GItem item) {
			this.list[index] = item;
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
			this.customRemove(this.from, this.size);
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
		public boolean remove(final Object item) {
			final int index = this.customItemIndex(item);
			if(index < 0) return false;
			this.customRemove(index, 1);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object key) {
			return this.customItemIndex(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> collection) {
			return Iterables.retainAll((Iterable<?>)this, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> collection) {
			return Iterables.removeAll((Iterable<?>)this, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> collection) {
			return Iterables.containsAll(this, collection);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List}, deren Daten in einem {@link Array} verwaltet werden.
	 * <p>
	 * Der Speicherverbrauch einer {@link CompactList} liegt bei ca. {@code 100%} des Speicherverbrauchs einer
	 * {@link ArrayList}. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente
	 * abhängig liegen im Mittel bei {@code 50%} der Rechenzeit, die eine {@link ArrayList} dazu benötigen würde.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class CompactList<GItem> extends CompactCollection<GItem> implements List<GItem>, RandomAccess {

		/**
		 * Diese Klasse implementiert eine {@link AbstractList} mit {@link RandomAccess}, die ihre Schnittstelle an eine
		 * {@link CompactList} delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class ItemList<GItem> extends AbstractList<GItem> implements RandomAccess {

			/**
			 * Dieses Feld speichert die {@link CompactList}.
			 */
			protected final CompactList<GItem> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactList}.
			 * 
			 * @param data {@link CompactList}.
			 */
			public ItemList(final CompactList<GItem> data) {
				this.data = data;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem get(final int index) {
				return this.data.get(index);
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
			protected void removeRange(final int fromIndex, final int toIndex) {
				this.data.customRemove(this.data.from + fromIndex, toIndex - fromIndex);
			}

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int customItemIndex(final Object item) {
			return this.indexOf(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean customItemEquals(final Object key, final int hash, final Object item) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int customItemCompare(final Object key, final int hash, final Object item) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem get(final int index) {
			if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
			return this.getItem(this.from + index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem set(final int index, final GItem element) {
			if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
			final int i = this.from + index;
			final GItem item = this.getItem(i);
			this.setItem(i, element);
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem e) {
			this.add(this.size, e);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GItem element) {
			if((index < 0) || (index > this.size)) throw new IndexOutOfBoundsException();
			this.customInsert(this.from + index, 1);
			this.setItem(this.from + index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GItem> collection) {
			return this.addAll(this.size, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final int index, final Collection<? extends GItem> collection) {
			if((index < 0) || (index > this.size)) throw new IndexOutOfBoundsException();
			final int count = collection.size();
			if(count == 0) return false;
			this.customInsert(this.from + index, count);
			final Iterator<? extends GItem> iterator = collection.iterator();
			int from = this.from + index;
			final int last = from + count;
			while((from < last) && iterator.hasNext()){
				this.setItem(from++, iterator.next());
			}
			while(from < last){
				this.setItem(from++, null);
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem remove(final int index) {
			if((index < 0) || (index >= this.size)) throw new IndexOutOfBoundsException();
			final int i = this.from + index;
			final GItem item = this.getItem(i);
			this.customRemove(i, 1);
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			final int index = CompactData.indexOf(this.list, this.from, this.size, o);
			if(index < 0) return -1;
			return index - this.from;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			if(o == null){
				for(int from = this.from - 1, last = from + this.size; from < last; last--){
					if(this.getItem(last) == null) return last - this.from;
				}
			}else{
				for(int from = this.from - 1, last = from + this.size; from < last; last--){
					if(o.equals(this.getItem(last))) return last - this.from;
				}
			}
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return new CompactAscendingCollectionIterator<GItem>(this, this.firstIndex(), this.lastIndex() + 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GItem> listIterator() {
			return new ItemList<GItem>(this).listIterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GItem> listIterator(final int index) {
			return new ItemList<GItem>(this).listIterator(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GItem> subList(final int fromIndex, final int toIndex) {
			return new ItemList<GItem>(this).subList(fromIndex, toIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] toArray() {
			return new ItemList<GItem>(this).toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T> T[] toArray(final T[] a) {
			return new ItemList<GItem>(this).toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return new ItemList<GItem>(this).hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof List<?>)) return false;
			return new ItemList<GItem>(this).equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return new ItemList<GItem>(this).toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Set}, dessen Daten in einem {@link Array} verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class CompactSet<GItem> extends CompactCollection<GItem> implements Set<GItem> {

		/**
		 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an ein gegebenes {@link Set}
		 * delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class ItemSet<GItem> extends AbstractSet<GItem> {

			/**
			 * Dieses Feld speichert das {@link Set}.
			 */
			protected final Set<GItem> data;

			/**
			 * Dieser Konstrukteur initialisiert das {@link Set}.
			 * 
			 * @param data {@link Set}.
			 */
			public ItemSet(final Set<GItem> data) {
				this.data = data;
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
			public Iterator<GItem> iterator() {
				return this.data.iterator();
			}

		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set}.
		 */
		public CompactSet() {
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactSet(final int capacity) {
			this.allocate(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen.
		 * 
		 * @see Set#addAll(Collection)
		 * @see CompactData#allocate(int)
		 * @param collection Elemente.
		 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
		 */
		public CompactSet(final Collection<? extends GItem> collection) {
			if(collection == null) throw new NullPointerException("collection is null");
			this.allocate(collection.size());
			this.addAll(collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return new CompactAscendingCollectionIterator<GItem>(this, this.firstIndex(), this.lastIndex() + 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem item) {
			final int index = this.customItemIndex(item);
			if(index >= 0) return false;
			final int i = this.from - index - 1;
			this.customInsert(i + this.from, 1);
			this.setItem(i + this.from, item);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GItem> collection) {
			return Iterables.appendAll(this, collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] toArray() {
			return new CompactSet.ItemSet<GItem>(this).toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T> T[] toArray(final T[] a) {
			return new CompactSet.ItemSet<GItem>(this).toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return new CompactSet.ItemSet<GItem>(this).hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Set<?>)) return false;
			return new ItemSet<GItem>(this).equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return new CompactSet.ItemSet<GItem>(this).toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Object#hashCode() Streuwert} basiertes {@link CompactSet}.
	 * <p>
	 * Der Speicherverbrauch eines {@link CompactHashSet}s liegt bei ca. {@code 13%} des Speicherverbrauchs eines
	 * {@link HashSet}s. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente
	 * abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 245%} der Rechenzeit, die
	 * ein {@link HashSet} dazu benötigen würde. Bei einer Anzahl von ca. {@code 100} Elementen benötigen Beide
	 * {@link Set}s dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das {@link CompactHashSet}
	 * schneller, bei mehr Elementen ist das {@link HashSet} schneller. Für das Finden von Elementen und das Iterieren
	 * über die Elemente benötigt das {@link CompactHashSet} im Mittel nur noch {@code 75%} der Rechenzeit des
	 * {@link HashSet}s, unabhängig von der Anzahl der Elemente.
	 * <p>
	 * Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit
	 * beim Hinzufügen und Entfernen von Elementen in einem {@link CompactHashSet} auf {@code 8827%} der Rechenzeit, die
	 * ein {@link HashSet} hierfür benötigen würde.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static class CompactHashSet<GItem> extends CompactSet<GItem> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set}.
		 */
		public CompactHashSet() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactHashSet(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen.
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
		protected int customItemIndex(final Object key) {
			if(key == null) return this.equalsIndex(null, 0);
			return this.equalsIndex(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean customItemEquals(final Object key, final int hash, final Object item) {
			if(key == null) return item == null;
			return key.equals(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int customItemCompare(final Object key, final int hash, final Object item) {
			if(item == null) return hash;
			return Comparators.compare(hash, item.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link NavigableSet}, dessen Daten in einem {@link Array} verwaltet werden. Der
	 * Speicherverbrauch eines {@link CompactNavigableSet} liegt bei ca. {@code 13%} des Speicherverbrauchs eines
	 * {@link TreeSet}s. Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente
	 * abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 208%} der Rechenzeit, die
	 * ein {@link TreeSet} dazu benötigen würde. Bei einer Anzahl von ca. {@code 8000} Elementen benötigen Beide
	 * {@link NavigableSet} dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das
	 * {@link CompactNavigableSet} schneller, bei mehr Elementen ist das {@link TreeSet} schneller. Für das Finden von
	 * Elementen und das Iterieren über die Elemente benötigt das {@link CompactNavigableSet} im Mittel nur noch
	 * {@code 25%} bzw. {@code 75%} der Rechenzeit des {@link TreeSet}s, unabhängig von der Anzahl der Elemente.
	 * <p>
	 * Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit
	 * beim Hinzufügen und Entfernen von Elementen in einem {@link CompactNavigableSet} auf ca. {@code 3900%} der
	 * Rechenzeit, die ein {@link TreeSet} hierfür benötigen würde.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static class CompactNavigableSet<GItem> extends CompactSet<GItem> implements NavigableSet<GItem> {

		/**
		 * Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactNavigableSet}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static abstract class CompactNavigableSubSet<GItem> extends CompactSubData<CompactNavigableSet<GItem>> implements
			NavigableSet<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
			 * 
			 * @param set {@link CompactNavigableSet}.
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
				if(!this.isInRange(item)) throw new IllegalArgumentException("entry out of range");
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
				return Iterables.appendAll(this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean retainAll(final Collection<?> collection) {
				return Iterables.retainAll((Iterable<?>)this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean removeAll(final Collection<?> collection) {
				return Iterables.removeAll((Iterable<?>)this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean containsAll(final Collection<?> collection) {
				return Iterables.containsAll(this, collection);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Object[] toArray() {
				return new CompactSet.ItemSet<GItem>(this).toArray();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public <T> T[] toArray(final T[] a) {
				return new CompactSet.ItemSet<GItem>(this).toArray(a);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int hashCode() {
				return new CompactSet.ItemSet<GItem>(this).hashCode();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean equals(final Object object) {
				if(object == this) return true;
				if(!(object instanceof Set<?>)) return false;
				return new ItemSet<GItem>(this).equals(object);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return new CompactSet.ItemSet<GItem>(this).toString();
			}

		}

		/**
		 * Diese Klasse implementiert die aufsteigende Teilmenge eines {@link CompactNavigableSet}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class CompactAscendingSubSet<GItem> extends CompactNavigableSubSet<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
			 * 
			 * @param array {@link CompactNavigableSet}.
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
				return new CompactAscendingCollectionIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
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
				return new CompactDescendingCollectionIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
				final boolean toInclusive) {
				if(!this.isInRange(fromElement, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
				if(!this.isInRange(toElement, toInclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactAscendingSubSet<GItem>(this.data, fromElement, fromInclusive, toElement, toInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
				if(!this.isInRange(toElement, inclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactAscendingSubSet<GItem>(this.data, this.fromItem, this.fromInclusive, toElement, inclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
				if(!this.isInRange(fromElement, inclusive)) throw new IllegalArgumentException("fromElement out of range");
				return new CompactAscendingSubSet<GItem>(this.data, fromElement, inclusive, this.lastItem, this.lastInclusive);
			}

		}

		/**
		 * Diese Klasse implementiert die absteigende Teilmenge eines {@link CompactNavigableSet}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class CompactDescendingSubSet<GItem> extends CompactNavigableSubSet<GItem> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
			 * 
			 * @param array {@link CompactNavigableSet}.
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
				return new CompactDescendingCollectionIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
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
				return new CompactAscendingCollectionIterator<GItem>(this.data, this.firstIndex(), this.lastIndex() + 1);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement,
				final boolean toInclusive) {
				if(!this.isInRange(fromElement, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
				if(!this.isInRange(toElement, toInclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactDescendingSubSet<GItem>(this.data, toElement, toInclusive, fromElement, fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
				if(!this.isInRange(toElement, inclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactDescendingSubSet<GItem>(this.data, toElement, inclusive, this.lastItem, this.lastInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
				if(!this.isInRange(fromElement, inclusive)) throw new IllegalArgumentException("fromElement out of range");
				return new CompactDescendingSubSet<GItem>(this.data, this.fromItem, this.fromInclusive, fromElement, inclusive);
			}

		}

		/**
		 * Dieses Feld speichert den {@link Comparator}.
		 */
		protected final Comparator<? super GItem> comparator;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableSet(final Comparator<? super GItem> comparator) throws NullPointerException {
			super();
			if(comparator == null) throw new NullPointerException("comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection {@link Collection}.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn die gegebene {@link Collection} bzw. der gegebene {@link Comparator}
		 *         {@code null} ist.
		 */
		public CompactNavigableSet(final Collection<? extends GItem> collection, final Comparator<? super GItem> comparator)
			throws NullPointerException {
			this(comparator);
			if(collection == null) throw new NullPointerException("collection is null");
			this.allocate(collection.size());
			this.addAll(collection);
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableSet(final int capacity, final Comparator<? super GItem> comparator)
			throws NullPointerException {
			this(comparator);
			this.allocate(capacity);
		}

		/**
		 * Diese Methode löscht das {@code index}-te Element und gibt es oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-te Element oder {@code null}.
		 */
		protected final GItem poll(final int index) {
			final int i = index - this.from;
			if((i < 0) || (i >= this.size)) return null;
			final GItem item = this.getItem(index);
			this.customRemove(index, 1);
			return item;
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element oder {@code null} zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element oder {@code null}.
		 */
		protected final GItem getItemOrNull(final int index) {
			final int i = index - this.from;
			if((i < 0) || (i >= this.size)) return null;
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
			final int i = index - this.from;
			if((i < 0) || (i >= this.size)) throw new NoSuchElementException();
			return this.getItem(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final int customItemIndex(final Object key) {
			return this.compareIndex(key, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final boolean customItemEquals(final Object key, final int hash, final Object item) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected final int customItemCompare(final Object key, final int hash, final Object item) {
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
			return new CompactDescendingCollectionIterator<GItem>(this, this.firstIndex(), this.lastIndex() + 1);
		}

	}

	// TODO Prüfung
	/**
	 * Diese Klasse implementiert eine abstrakte {@link Map}, deren Daten in einem {@link Array} verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public abstract static class CompactMap<GKey, GValue> extends CompactData implements Map<GKey, GValue> {

		/**
		 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an die Schlüssel einer
		 * {@link CompactMap} delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		static final class KeySet<GKey> extends AbstractSet<GKey> {

			/**
			 * Dieses Feld speichert die {@link CompactMap}.
			 */
			protected final CompactMap<GKey, ?> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactMap}.
			 * 
			 * @param data {@link CompactMap}.
			 */
			public KeySet(final CompactMap<GKey, ?> data) {
				this.data = data;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.data.size;
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
			public Iterator<GKey> iterator() {
				return new CompactMapKeyAscendingIterator<GKey>(this.data, this.data.firstIndex(), this.data.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an die Einträge einer
		 * {@link CompactMap} delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		static final class EntrySet<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

			/**
			 * Dieses Feld speichert die {@link CompactMap}.
			 */
			protected final CompactMap<GKey, GValue> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactMap}.
			 * 
			 * @param data {@link CompactMap}.
			 */
			public EntrySet(final CompactMap<GKey, GValue> data) {
				this.data = data;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.data.size;
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
			public Iterator<Entry<GKey, GValue>> iterator() {
				return new CompactMapEntryIterator<GKey, GValue>(this.data, this.data.firstIndex(), this.data.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert eine {@link AbstractMap}, die ihre Schnittstelle an eine gegebene {@link Map}
		 * delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		static final class ItemMap<GKey, GValue> extends AbstractMap<GKey, GValue> {

			/**
			 * Dieses Feld speichert die {@link Map}.
			 */
			protected final Map<GKey, GValue> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link Map}.
			 * 
			 * @param data {@link Map}.
			 */
			public ItemMap(final Map<GKey, GValue> data) {
				this.data = data;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Set<Entry<GKey, GValue>> entrySet() {
				return this.data.entrySet();
			}

		}

		/**
		 * Diese Klasse implementiert das {@link SimpleEntry} einer {@link CompactMap}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		static final class ItemEntry<GKey, GValue> extends SimpleEntry<GKey, GValue> {

			/**
			 * Dieses Feld speichert die {@code SerialVersionUID}.
			 */
			private static final long serialVersionUID = -543360027933297926L;

			/**
			 * Dieses Feld speichert die {@link CompactMap}.
			 */
			protected final CompactMap<GKey, GValue> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactMap} und den Index.
			 * 
			 * @param data {@link CompactMap}.
			 * @param index Index.
			 */
			public ItemEntry(final CompactMap<GKey, GValue> data, final int index) {
				super(data.getKey(index), data.getValue(index));
				this.data = data;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GValue setValue(final GValue value) {
				final GKey key = this.getKey();
				final GValue result = super.setValue(value);
				final int index = this.data.customItemIndex(this.getKey());
				if(index < 0) throw new IllegalStateException();
				this.data.setEntry(index, key, value);
				return result;
			}

		}

		/**
		 * Diese Klasse implementiert ein {@link AbstractCollection}, das seine Schnittstelle an die Werte einer
		 * {@link CompactMap} delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ der Werte.
		 */
		static final class ValueCollection<GValue> extends AbstractCollection<GValue> {

			/**
			 * Dieses Feld speichert die {@link CompactMap}.
			 */
			protected final CompactMap<?, GValue> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactMap}.
			 * 
			 * @param data {@link CompactMap}.
			 */
			public ValueCollection(final CompactMap<?, GValue> data) {
				this.data = data;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int size() {
				return this.data.size;
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
			public Iterator<GValue> iterator() {
				return new CompactMapValueIterator<GValue>(this.data, this.data.firstIndex(), this.data.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		static final class CompactMapKeyAscendingIterator<GKey> extends CompactAscendingIterator<GKey, CompactMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
			 * 
			 * @param map {@link CompactMap}.
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
		 * Diese Klasse implementiert den absteigenden {@link Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		static final class CompactMapKeyDescendingIterator<GKey> extends
			CompactDescendingIterator<GKey, CompactMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
			 * 
			 * @param map {@link CompactMap}.
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
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator} der Werte.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <V> Typ der Werte.
		 */
		static final class CompactMapValueIterator<V> extends CompactAscendingIterator<V, CompactMap<?, V>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
			 * 
			 * @param map {@link CompactMap}.
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
		 * Diese Klasse implementiert den aufsteigenden {@link Iterator} der {@link ItemEntry}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <V> Typ der Werte.
		 */
		static final class CompactMapEntryIterator<GKey, V> extends
			CompactAscendingIterator<Entry<GKey, V>, CompactMap<GKey, V>> {

			/**
			 * Dieser Konstrukteur initialisiert {@link CompactMap} und Indizes.
			 * 
			 * @param map {@link CompactMap}.
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
		 * Dieser Konstrukteur initialisiert die {@link Map}.
		 */
		public CompactMap() {
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactMap(final int capacity) {
			this.allocate(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen.
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
			return new ItemEntry<GKey, GValue>(this, index);
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
		protected abstract int customItemIndex(final Object key);

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
			this.customRemove(this.from, this.size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GValue> values() {
			return new ValueCollection<GValue>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<GKey> keySet() {
			return new KeySet<GKey>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return new EntrySet<GKey, GValue>(this);
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
			return this.customItemIndex(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final Object key) {
			final int index = this.customItemIndex(key);
			if(index < 0) return null;
			return this.getValue(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
			int index = this.customItemIndex(key);
			if(index >= 0){
				final GValue item = this.getValue(index);
				this.setEntry(index, this.getKey(index), value);
				return item;
			}
			index = -index - 1;
			this.customInsert(index, 1);
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
			final int index = this.customItemIndex(key);
			if(index < 0) return null;
			final GValue item = this.getValue(index);
			this.customRemove(index, 1);
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return new ItemMap<GKey, GValue>(this).hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Map<?, ?>)) return false;
			return new ItemMap<GKey, GValue>(this).equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return new ItemMap<GKey, GValue>(this).toString();
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link CompactMap}, deren Werte in einem {@link Array} verwaltet werden
	 * und ihren Schlüssel selbst referenzieren. Diese Implementation erlaubt deshalb {@code null} nicht als Wert.
	 * 
	 * @see CompactMap#getKey(Object)
	 * @see CompactMap#setKey(Object, Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactItemMap<GKey, GValue> extends CompactMap<GKey, GValue> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map}.
		 */
		public CompactItemMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
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
			return (GValue)this.list[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setEntry(final int index, final GKey key, final GValue value) {
			if(value == null) throw new NullPointerException();
			this.list[index] = value;
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
			return CompactData.indexOf(this.list, this.from, this.size, value) >= 0;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactItemMap}.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactItemHashMap<GKey, GValue> extends CompactItemMap<GKey, GValue> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map}.
		 */
		public CompactItemHashMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
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
		protected int customItemIndex(final Object key) {
			if(key == null) return this.equalsIndex(null, 0);
			return this.equalsIndex(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected boolean customItemEquals(final Object key, final int hash, final Object item) {
			if(key == null) return this.getKey((GValue)item) == null;
			return key.equals(this.getKey((GValue)item));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int customItemCompare(final Object key, final int hash, final Object item) {
			final Object value = this.getKey((GValue)item);
			if(value == null) return hash;
			return Comparators.compare(hash, value.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link Map}, deren Schlüssel und Werte in je einem {@link Array Array}
	 * verwaltet werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactEntryMap<GKey, GValue> extends CompactMap<GKey, GValue> {

		/**
		 * Dieses Feld speichert die Werte.
		 */
		protected Object[] values = CompactData.VOID;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map}.
		 */
		public CompactEntryMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactEntryMap(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen.
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
			return (GKey)this.list[index];
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
			this.list[index] = key;
			this.values[index] = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customInsert(final int index, final int count) throws IllegalArgumentException {
			final int from = this.from;
			final int size = this.size;
			this.list = this.defaultInsert(this.list, index, count);
			this.from = from;
			this.size = size;
			this.values = this.defaultInsert(this.values, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customRemove(final int index, final int count) throws IllegalArgumentException {
			final int from = this.from;
			final int size = this.size;
			this.list = this.defaultRemove(this.list, index, count);
			this.from = from;
			this.size = size;
			this.values = this.defaultRemove(this.values, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customAllocate(final int count) {
			final int from = this.from;
			final int length = this.defaultLength(this.list, count);
			this.list = this.defaultResize(this.list, length);
			this.from = from;
			this.values = this.defaultResize(this.values, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customCompact() {
			final int from = this.from;
			final int length = this.size;
			this.list = this.defaultResize(this.list, length);
			this.from = from;
			this.values = this.defaultResize(this.values, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			return CompactData.indexOf(this.values, this.from, this.size, value) >= 0;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactEntryMap}. Der
	 * Speicherverbrauch einer {@link CompactEntryHashMap} liegt bei ca. {@code 28%} des Speicherverbrauchs eines
	 * {@link HashMap}.
	 * <p>
	 * Eine {@link HashMap} ist immer schneller als eine {@link CompactEntryHashMap}.
	 * <p>
	 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen
	 * sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 150%}. Bei der erhöhung der Anzahl der Elemente
	 * auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von
	 * Elementen in einer {@link CompactEntryHashMap} auf {@code 760%} der Rechenzeit, die eine {@link HashMap} hierfür
	 * benötigen würde.
	 * <p>
	 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map} in etwa die gleichen
	 * Rechenzeiten, unabhängig von der Anzahl der Elemente.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static class CompactEntryHashMap<GKey, GValue> extends CompactEntryMap<GKey, GValue> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map}.
		 */
		public CompactEntryHashMap() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 */
		public CompactEntryHashMap(final int capacity) {
			super(capacity);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen.
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
		protected int customItemIndex(final Object key) {
			if(key == null) return this.equalsIndex(null, 0);
			return this.equalsIndex(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean customItemEquals(final Object key, final int hash, final Object item) {
			if(key == null) return item == null;
			return key.equals(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int customItemCompare(final Object key, final int hash, final Object item) {
			if(item == null) return hash;
			return Comparators.compare(hash, item.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link NavigableMap}, deren Daten in einem {@link Array} verwaltet
	 * werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactNavigableMap<GKey, GValue> extends CompactMap<GKey, GValue> implements
		NavigableMap<GKey, GValue> {

		/**
		 * Diese Klasse implementiert die anstrakte Menge der Schlüssel einer {@link NavigableMap}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GData> Typ der {@link NavigableMap}.
		 */
		static abstract class AbstractNavigableKeySet<GKey, GData extends NavigableMap<GKey, ?>> extends AbstractSet<GKey>
			implements NavigableSet<GKey> {

			/**
			 * Dieses Feld speichert die {@link NavigableMap}.
			 */
			protected final GData data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link NavigableMap}.
			 * 
			 * @param data {@link NavigableMap}.
			 * @throws NullPointerException Wenn die gegebene {@link NavigableMap} {@code null} ist.
			 */
			public AbstractNavigableKeySet(final GData data) throws NullPointerException {
				if(data == null) throw new NullPointerException("data is null");
				this.data = data;
			}

			/**
			 * Diese Methode gibt den Schlüssel des gegebenen {@link ItemEntry}s oder {@code null} zurück.
			 * 
			 * @param entry {@link ItemEntry}.
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

		static final class CompactNavigableKeySet<GKey> extends AbstractNavigableKeySet<GKey, CompactNavigableMap<GKey, ?>> {

			public CompactNavigableKeySet(final CompactNavigableMap<GKey, ?> data) throws NullPointerException {
				super(data);
			}

			@Override
			public Iterator<GKey> iterator() {
				return new CompactMapKeyAscendingIterator<GKey>(this.data, this.data.firstIndex(), this.data.lastIndex() + 1);
			}

		}

		/**
		 * Diese Klasse implementiert die aufsteigende Menge der Schlüssel einer {@link CompactAscendingSubMap}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		static final class CompactAscendingKeySet<GKey> extends
			AbstractNavigableKeySet<GKey, CompactAscendingSubMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactAscendingSubMap}.
			 * 
			 * @param data {@link CompactAscendingSubMap}.
			 * @throws NullPointerException Wenn die gegebene {@link CompactAscendingSubMap} {@code null} ist.
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
		 * Diese Klasse implementiert die abfsteigende Menge der Schlüssel einer {@link CompactDescendingSubMap}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 */
		static final class CompactDescendingKeySet<GKey> extends
			AbstractNavigableKeySet<GKey, CompactDescendingSubMap<GKey, ?>> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactDescendingSubMap}.
			 * 
			 * @param data {@link CompactDescendingSubMap}.
			 * @throws NullPointerException Wenn die gegebene {@link CompactDescendingSubMap} {@code null} ist.
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
		 * Diese Klasse implementiert eine abstrakte Teilmenge einer {@link CompactNavigableMap}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		static abstract class CompactNavigableSubMap<GKey, GValue> extends
			CompactSubData<CompactNavigableMap<GKey, GValue>> implements NavigableMap<GKey, GValue> {

			static final class EntrySet<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

				protected final CompactNavigableSubMap<GKey, GValue> data;

				public EntrySet(final CompactNavigableSubMap<GKey, GValue> data) {
					this.data = data;
				}

				@Override
				public int size() {
					return this.data.size();
				}

				@Override
				public Iterator<Entry<GKey, GValue>> iterator() {
					return new CompactMapEntryIterator<GKey, GValue>(this.data.data, this.data.firstIndex(),
						this.data.lastIndex() - 1);
				}
			}

			static final class ValueCollection<GValue> extends AbstractCollection<GValue> {

				protected final CompactNavigableSubMap<?, GValue> data;

				public ValueCollection(final CompactNavigableSubMap<?, GValue> data) {
					this.data = data;
				}

				@Override
				public int size() {
					return this.data.size();
				}

				@Override
				public Iterator<GValue> iterator() {
					return new CompactMapValueIterator<GValue>(this.data.data, this.data.firstIndex(), this.data.lastIndex() + 1);
				}
			}

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap} und die Grenzen und deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableMap}.
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
				return new ValueCollection<GValue>(this);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Set<Entry<GKey, GValue>> entrySet() {
				return new EntrySet<GKey, GValue>(this);
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
				return new ItemMap<GKey, GValue>(this).hashCode();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean equals(final Object object) {
				if(object == this) return true;
				if(!(object instanceof Set<?>)) return false;
				return new ItemMap<GKey, GValue>(this).equals(object);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return new ItemMap<GKey, GValue>(this).toString();
			}

		}

		/**
		 * Diese Klasse implementiert die aufsteigende Teilmenge einer {@link CompactNavigableMap} .
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		static final class CompactAscendingSubMap<GKey, GValue> extends CompactNavigableSubMap<GKey, GValue> {

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap} und die Grenzen und deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableMap}.
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
				if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
				if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactAscendingSubMap<GKey, GValue>(this.data, fromKey, fromInclusive, toKey, toInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
				if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactAscendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, toKey, inclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
				if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("fromElement out of range");
				return new CompactAscendingSubMap<GKey, GValue>(this.data, fromKey, inclusive, this.lastItem,
					this.lastInclusive);
			}

		}

		/**
		 * Diese Klasse implementiert die absteigende Teilmenge einer {@link CompactNavigableMap}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		static final class CompactDescendingSubMap<GKey, GValue> extends CompactNavigableSubMap<GKey, GValue> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
			 * 
			 * @param map {@link CompactNavigableSet}.
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
				if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
				if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactDescendingSubMap<GKey, GValue>(this.data, toKey, toInclusive, fromKey, fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
				if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("toElement out of range");
				return new CompactDescendingSubMap<GKey, GValue>(this.data, toKey, inclusive, this.fromItem, this.fromInclusive);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
				if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("fromElement out of range");
				return new CompactDescendingSubMap<GKey, GValue>(this.data, this.lastItem, this.lastInclusive, fromKey,
					inclusive);
			}

		}

		/**
		 * Dieses Feld speichert den {@link Comparator}.
		 */
		protected final Comparator<? super GKey> comparator;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit dem gegebenen {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableMap(final Comparator<? super GKey> comparator) throws NullPointerException {
			if(comparator == null) throw new NullPointerException("comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableMap(final int capacity, final Comparator<? super GKey> comparator)
			throws NullPointerException {
			super(capacity);
			if(comparator == null) throw new NullPointerException("comparator is null");
			this.comparator = comparator;
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableMap(final Map<? extends GKey, ? extends GValue> map,
			final Comparator<? super GKey> comparator) throws NullPointerException {
			super(map);
			if(comparator == null) throw new NullPointerException("comparator is null");
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
			this.customRemove(index, 1);
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
		protected int customItemIndex(final Object key) {
			return this.compareIndex(key, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean customItemEquals(final Object key, final int hash, final Object item) {
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
			return new CompactNavigableKeySet<GKey>(this);
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
	 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap}, deren Daten in einem {@link Array} verwaltet
	 * werden und ihren Schlüssel selbst referenzieren. Diese Implementation erlaubt deshalb {@code null} nicht als Wert.
	 * 
	 * @see CompactMap#getKey(Object)
	 * @see CompactMap#setKey(Object, Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	public static abstract class CompactNavigableItemMap<GKey, GValue> extends CompactNavigableMap<GKey, GValue> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit dem gegebenen {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableItemMap(final Comparator<? super GKey> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableItemMap(final int capacity, final Comparator<? super GKey> comparator)
			throws NullPointerException {
			super(capacity, comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
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
			return (GValue)this.list[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final void setEntry(final int index, final GKey key, final GValue value) {
			if(value == null) throw new NullPointerException();
			this.list[index] = value;
			this.setKey(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int customItemCompare(final Object key, final int hash, final Object item) {
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
			return CompactData.indexOf(this.list, this.from, this.size, value) >= 0;
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap}, deren Schlüssel und Werte in je einem
	 * {@link Array} verwaltet werden. Der Speicherverbrauch einer {@link CompactNavigableEntryMap} liegt bei ca.
	 * {@code 28%} des Speicherverbrauchs einer {@link TreeMap}.
	 * <p>
	 * Eine {@link TreeMap} ist immer schneller als eine {@link CompactNavigableEntryMap}.
	 * <p>
	 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen
	 * sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 160%} der Rechenzeit, die eine {@link TreeMap}
	 * dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen)
	 * steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer {@link CompactNavigableEntryMap} auf ca.
	 * {@code 1050%} der Rechenzeit, die eine {@link TreeMap} hierfür benötigen würde.
	 * <p>
	 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map}s in etwa die gleichen
	 * Rechenzeiten, unabhängig von der Anzahl der Elemente.
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
		protected Object[] values = CompactData.VOID;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit dem gegebenen {@link Comparator}.
		 * 
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableEntryMap(final Comparator<? super GKey> comparator) throws NullPointerException {
			super(comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @param capacity Kapazität.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
		 */
		public CompactNavigableEntryMap(final int capacity, final Comparator<? super GKey> comparator)
			throws NullPointerException {
			super(capacity, comparator);
		}

		/**
		 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen und dem gegebenen
		 * {@link Comparator}.
		 * 
		 * @see CompactData#allocate(int)
		 * @see Map#putAll(Map)
		 * @param map Elemente.
		 * @param comparator {@link Comparator}.
		 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
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
			return (GKey)this.list[index];
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
			this.list[index] = key;
			this.values[index] = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int customItemCompare(final Object key, final int hash, final Object item) {
			return this.comparator.compare((GKey)key, (GKey)item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customInsert(final int index, final int count) throws IllegalArgumentException {
			final int from = this.from;
			final int size = this.size;
			this.list = this.defaultInsert(this.list, index, count);
			this.from = from;
			this.size = size;
			this.values = this.defaultInsert(this.values, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customRemove(final int index, final int count) throws IllegalArgumentException {
			final int from = this.from;
			final int size = this.size;
			this.list = this.defaultRemove(this.list, index, count);
			this.from = from;
			this.size = size;
			this.values = this.defaultRemove(this.values, index, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customAllocate(final int count) {
			final int from = this.from;
			final int length = this.defaultLength(this.list, count);
			this.list = this.defaultResize(this.list, length);
			this.from = from;
			this.values = this.defaultResize(this.values, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void customCompact() {
			final int from = this.from;
			final int length = this.size;
			this.list = this.defaultResize(this.list, length);
			this.from = from;
			this.values = this.defaultResize(this.values, length);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsValue(final Object value) {
			return CompactData.indexOf(this.values, this.from, this.size, value) >= 0;
		}

	}

}
