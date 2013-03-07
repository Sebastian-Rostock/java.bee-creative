package bee.creative.compact;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import bee.creative.array.Array;
import bee.creative.array.CompactArray;
import bee.creative.array.CompactObjectArray;

/**
 * Diese Klasse implementiert eine abstrakte Sammlung von Elementen, die in einem (sortierten) Array verwaltet werden.
 * <p>
 * Das Einfügen und Entfernen von Elementen verändern in dieser Implementation nicht nur die Größe des mit den Nutzdaten belegte Bereichs im Array, sondern auch dessen Position.
 * <p>
 * Beim Entfernen von Elementen, werden die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich verschoben. Dadurch vergrößert sich entweder die Größe des Leerraums vor oder die die Größe des Leerraums nach dem Nutzdatenbereich. Reicht der verfügbare Leerraum zum Verschieben dieser wenigen Elemente nicht aus, werden alle Elemente verschoben und im Array neu ausgerichtet.
 * <p>
 * Jenachdem, ob der Nutzdatenbereich am Anfang, in der Mitte oder am Ende des Arrays ausgerichtet wird, wird das häufige Einfügen von Elementen am Ende, in der Mitte bzw. am Anfang beschleunigt. Die Änderung der Größe des Arrays führ in jedem Fall zu einer erneuten Ausrichtung.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class CompactData {

	/**
	 * Diese Klasse implementiert das {@link CompactObjectArray} der {@link CompactData}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static final class CompactDataArray extends CompactObjectArray<Object> {

		/**
		 * Dieses Feld speichert das leere Array.
		 */
		protected static final Object[] VOID = new Object[0];

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int compare(final Object o1, final Object o2) {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Object[] newArray(final int length) {
			if(length == 0) return CompactDataArray.VOID;
			return new Object[length];
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt mit {@link CompactData}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	protected static abstract class CompactDataOwner<GData extends CompactData> {

		/**
		 * Dieses Feld speichert die {@link CompactData}.
		 */
		protected final GData data;

		/**
		 * Dieser Konstruktor initialisiert die {@link CompactData}.
		 * 
		 * @param data {@link CompactData}.
		 */
		public CompactDataOwner(final GData data) {
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
	protected static abstract class CompactDataIterator<GItem, GData extends CompactData> extends CompactData.CompactDataOwner<GData> implements Iterator<GItem> {

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
		 * Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 * 
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactDataIterator(final GData data, final int from, final int last) {
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
			this.data.customRemove(item, 1);
			this.item = -1;
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten aufsteigenden {@link CompactData}-{@link Iterator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	public static abstract class CompactDataAscendingIterator<GItem, GData extends CompactData> extends CompactData.CompactDataIterator<GItem, GData> {

		/**
		 * Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 * 
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactDataAscendingIterator(final GData data, final int from, final int last) {
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
	public static abstract class CompactDataDescendingIterator<GItem, GData extends CompactData> extends CompactData.CompactDataIterator<GItem, GData> {

		/**
		 * Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 * 
		 * @param array {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactDataDescendingIterator(final GData array, final int from, final int last) {
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
	 * Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactData}s und wird zur realisierung von {@link NavigableSet}s und {@link NavigableMap}s verwendet.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	public static abstract class CompactSubData<GData extends CompactData> extends CompactDataOwner<GData> {

		/**
		 * Dieses Feld speichert das Objekt zur offenen Begrenzung von Teilmengen.
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
		 * Dieser Konstruktor initialisiert das {@link CompactData} und die Grenzen und deren Inklusion.
		 * 
		 * @param data {@link CompactData}.
		 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
		 */
		public CompactSubData(final GData data, final Object fromItem, final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
			throws IllegalArgumentException {
			super(data);
			if(fromItem != CompactSubData.OPEN){
				if(lastItem != CompactSubData.OPEN){
					if(data.customItemCompare(fromItem, 0, lastItem) > 0) throw new IllegalArgumentException("fromItem > lastItem");
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und das {@code index}-te Element zu klein sind.
		 * 
		 * @see CompactSubData#isTooLow(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index zu groß bzw. das {@code index}-te Element zu klein ist.
		 */
		protected final boolean isTooLow(final int index) {
			final CompactObjectArray<Object> array = this.data.items;
			if(index >= array.size()) return true;
			if(index < 0) return false;
			return this.isTooLow(array.get(index));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu klein ist. Wenn das erste Element gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu klein sein. Anderenfalls gilt es als zu klein, wenn es entweder kleiner als das erste Element ist oder wenn das erste Element exklusiv ist und das gegebene Element gleich dem ersten Element ist.
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und das {@code index}-te Element zu groß sind.
		 * 
		 * @see CompactSubData#isTooHigh(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index bzw. das {@code index}-te Element zu groß ist.
		 */
		protected final boolean isTooHigh(final int index) {
			final CompactObjectArray<Object> array = this.data.items;
			if(index >= array.size()) return true;
			if(index < 0) return false;
			return this.isTooHigh(array.get(index));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu groß ist. Wenn das letzte Element gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu groß sein. Anderenfalls gilt es als zu groß, wenn es entweder größer als das letzte Element ist oder wenn das letzte Element exklusiv ist und das gegebene Element gleich dem letzten Element ist.
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich liegt. Die Inklusitivität des ersten bzw. letzten Elements wird beachtet.
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt.
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt. Die Inklusitivität des ersten bzw. letzten Elements ignoriert.
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
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Wenn kein solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#lower(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner dem gegebenen ist, oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int lowerIndex(final Object item) {
			if(this.isTooHigh(item)) return this.highestIndex();
			final int index = this.data.lowerIndex(item);
			if(this.isTooLow(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebenen ist. Wenn kein solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#floor(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist, oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int floorIndex(final Object item) {
			if(this.isTooHigh(item)) return this.highestIndex();
			final int index = this.data.floorIndex(item);
			if(this.isTooLow(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass größer oder gleich dem gegebenen ist. Wenn kein solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#ceiling(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer oder gleich dem gegebenen ist, oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int ceilingIndex(final Object item) {
			if(this.isTooLow(item)) return this.lowestIndex();
			final int index = this.data.ceilingIndex(item);
			if(this.isTooHigh(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass größer dem gegebenen ist. Wenn kein solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#higher(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer dem gegebenen ist, oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
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
	 * Dieses Feld speichert das {@link Array} der Elemente.
	 */
	protected final CompactDataArray items;

	/**
	 * Dieser Konstruktor initialisiert das {@link Array} der Elemente.
	 */
	public CompactData() {
		this.items = new CompactDataArray();
	}

	/**
	 * Diese Methode sucht zuerst binär und danach linear nach einem Element, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt den Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste. Ein Element {@code element} ist dann zum gegebenen Schlüssel gleich, wenn:
	 * 
	 * <pre>(customItemCompare(key, hash, element) == 0) && customItemEquals(key, hash, element)</pre>
	 * 
	 * @see CompactData#defaultCompareIndex(Object, int)
	 * @see CompactData#customItemEquals(Object, int, Object)
	 * @see CompactData#customItemCompare(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	protected final int defaultEqualsIndex(final Object key, final int hash) {
		final int index = this.defaultCompareIndex(key, hash);
		if(index < 0) return index;
		final CompactDataArray data = this.items;
		final Object[] array = data.array();
		final int from = data.startIndex();
		if(this.customItemEquals(key, hash, array[index + from])) return index;
		Object item;
		for(int next = (index + from) - 1; (from <= next) && (this.customItemCompare(key, hash, item = array[next]) == 0); next--){
			if(this.customItemEquals(key, hash, item)) return next - from;
		}
		for(int next = (index + from) + 1, last = data.finalIndex(); (next < last) && (this.customItemCompare(key, hash, item = array[next]) == 0); next++){
			if(this.customItemEquals(key, hash, item)) return next - from;
		}
		return -index - 1;
	}

	/**
	 * Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste.
	 * 
	 * @see CompactData#customItemCompare(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	protected final int defaultCompareIndex(final Object key, final int hash) {
		final CompactDataArray data = this.items;
		final Object[] array = data.array();
		final int offset = data.startIndex();
		int from = offset, last = data.finalIndex();
		while(from < last){
			final int index = (from + last) >>> 1, value = this.customItemCompare(key, hash, array[index]);
			if(value < 0){
				last = index;
			}else if(value > 0){
				from = index + 1;
			}else return index - offset;
		}
		return offset - from - 1;
	}

	/**
	 * Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das Array ein.
	 * 
	 * @see CompactArray#insert(int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
	 */
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		this.items.insert(index, count);
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem Array mit der gegebenen Länge der Belegung.
	 * 
	 * @see CompactArray#remove(int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
	 */
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		this.items.remove(index, count);
	}

	/**
	 * Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 * 
	 * @see CompactArray#allocate(int)
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	protected void customAllocate(final int capacity) {
		this.items.allocate(capacity);
	}

	/**
	 * Diese Methode verkleinert die Kapazität auf das Minimum.
	 * 
	 * @see CompactArray#compact()
	 */
	protected void customCompact() {
		this.items.compact();
	}

	/**
	 * Diese Methode sucht nach dem gegebenen Objekt und gibt dessen Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste.
	 * 
	 * @see CompactData#defaultEqualsIndex(Object, int)
	 * @see CompactData#defaultCompareIndex(Object, int)
	 * @param item Objekt oder {@code null}.
	 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	protected abstract int customItemIndex(final Object item);

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Schlüssel {@link Object#equals(Object) äquivalent} dem Schlüssel des gegebenen Elements ist.
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
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn der gegebene Schlüssel kleiner, gleich bzw. größer als der Schlüssel des gegebenen Elements ist. Die Berechnung kann auf den Schlüsseln selbst oder ihren {@link Object#hashCode() Streuwerten} beruhen.
	 * 
	 * @see Comparator#compare(Object, Object)
	 * @see CompactSubData
	 * @see CompactSubData#isTooLow(Object)
	 * @see CompactSubData#isTooHigh(Object)
	 * @see CompactSubData#isInClosedRange(Object)
	 * @see CompactSubData#CompactSubData(CompactData, Object, boolean, Object, boolean)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @param item Element.
	 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der Schlüssel.
	 */
	protected abstract int customItemCompare(Object key, int hash, Object item);

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
	 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die Werte {@code -1} und {@code from+size} annehmen.
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
	 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index kann die Werte {@code -1} und {@code from+size} annehmen.
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
	 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
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
	 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
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
	 * Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann deb Wert {@code -1} annehmen.
	 * 
	 * @see NavigableSet#last()
	 * @return Index des letzten Elements.
	 */
	protected final int lastIndex() {
		return this.items.size() - 1;
	}

	/**
	 * Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 * 
	 * @see CompactObjectArray#allocate(int)
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public final void allocate(final int capacity) throws IllegalArgumentException {
		this.customAllocate(capacity);
	}

	/**
	 * Diese Methode verkleinert die Kapazität auf das Minimum.
	 */
	public final void compact() {
		this.customCompact();
	}

}