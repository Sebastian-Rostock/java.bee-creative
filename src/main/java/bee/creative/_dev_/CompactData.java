package bee.creative._dev_;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import bee.creative.array.Array;
import bee.creative.array.CompactArray;
import bee.creative.array.CompactObjectArray;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Sammlung von Elementen, die in einem (sortierten) Array verwaltet werden.
 * <p>
 * Das Einfügen und Entfernen von Elementen verändern in dieser Implementation nicht nur die Größe des mit den Nutzdaten belegte Bereichs im Array, sondern auch
 * dessen Position.
 * <p>
 * Beim Entfernen von Elementen, werden die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich verschoben. Dadurch vergrößert sich entweder die Größe
 * des Leerraums vor oder die die Größe des Leerraums nach dem Nutzdatenbereich. Reicht der verfügbare Leerraum zum Verschieben dieser wenigen Elemente nicht
 * aus, werden alle Elemente verschoben und im Array neu ausgerichtet.
 * <p>
 * Jenachdem, ob der Nutzdatenbereich am Anfang, in der Mitte oder am Ende des Arrays ausgerichtet wird, wird das häufige Einfügen von Elementen am Ende, in der
 * Mitte bzw. am Anfang beschleunigt. Die Änderung der Größe des Arrays führ in jedem Fall zu einer erneuten Ausrichtung.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
abstract class CompactData {

	/** Diese Klasse implementiert das {@link CompactObjectArray} der {@link CompactData}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static final class CompactDataArray extends CompactObjectArray<Object> {

		public CompactDataArray( ) throws NullPointerException {
			super(Object.class);
		}

		/** Dieses Feld speichert das leere Array. */
		static final Object[] empty = new Object[0];
 

		/** {@inheritDoc} */
		@Override
		protected Object[] customNewArray(final int length) {
			if (length == 0) return CompactDataArray.empty;
			return new Object[length];
		}

	}

	/** Diese Klasse implementiert ein abstraktes Objekt mit {@link CompactData}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s. */
	protected static abstract class CompactDataOwner<GData extends CompactData> {

		/** Dieses Feld speichert die {@link CompactData}. */
		protected final GData data;

		/** Dieser Konstruktor initialisiert die {@link CompactData}.
		 *
		 * @param data {@link CompactData}. */
		public CompactDataOwner(final GData data) {
			this.data = Objects.notNull(data);
		}

	}

	/** Diese Klasse implementiert einen abstrakten {@link CompactData}-{@link Iterator}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s. */
	protected static abstract class CompactDataIterator<GItem, GData extends CompactData> extends CompactData.CompactDataOwner<GData> implements Iterator<GItem> {

		/** Dieses Feld speichert den Index des ersten Elements (inklusiv). */
		protected int from;

		/** Dieses Feld speichert den Index des aktuellen Elements. */
		protected int item;

		/** Dieses Feld speichert den Index des letztem Elements (exklusiv). */
		protected int last;

		/** Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 *
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv). */
		public CompactDataIterator(final GData data, final int from, final int last) {
			super(data);
			this.from = from;
			this.item = -1;
			this.last = last;
		}

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 *
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		protected abstract GItem customNext(int index);

		/** {@inheritDoc} */
		@Override
		public boolean hasNext() {
			return this.from < this.last;
		}

		/** {@inheritDoc} */
		@Override
		public void remove() {
			final int item = this.item;
			if (item < 0) throw new IllegalStateException();
			this.data.customRemove(item, 1);
			this.item = -1;
		}

	}

	/** Diese Klasse implementiert einen abstrakten aufsteigenden {@link CompactData}-{@link Iterator}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s. */
	public static abstract class CompactDataAscendingIterator<GItem, GData extends CompactData> extends CompactData.CompactDataIterator<GItem, GData> {

		/** Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 *
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv). */
		public CompactDataAscendingIterator(final GData data, final int from, final int last) {
			super(data, from - 1, last - 1);
		}

		/** {@inheritDoc} */
		@Override
		public GItem next() {
			return this.customNext(this.item = (this.from = this.from + 1));
		}

		/** {@inheritDoc} */
		@Override
		public void remove() {
			super.remove();
			this.from--;
			this.last--;
		}

	}

	/** Diese Klasse implementiert einen abstrakten absteigenden {@link CompactData}-{@link Iterator}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s. */
	public static abstract class CompactDataDescendingIterator<GItem, GData extends CompactData> extends CompactData.CompactDataIterator<GItem, GData> {

		/** Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 *
		 * @param array {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv). */
		public CompactDataDescendingIterator(final GData array, final int from, final int last) {
			super(array, from, last);
		}

		/** {@inheritDoc} */
		@Override
		public GItem next() {
			return this.customNext(this.item = (this.last = this.last - 1));
		}

	}

	/** Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactData}s und wird zur realisierung von {@link NavigableSet}s und
	 * {@link NavigableMap} s verwendet.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s. */
	public static abstract class CompactSubData<GData extends CompactData> extends CompactDataOwner<GData> {

		/** Dieses Feld speichert das Objekt zur offenen Begrenzung von Teilmengen. */
		protected static final Object open = new Object();

		/** Dieses Feld speichert das erste Element oder {@link CompactSubData#open}. */
		protected final Object fromItem;

		/** Dieses Feld speichert {@code true}, wenn das erste Element inklusiv ist. */
		protected final boolean fromInclusive;

		/** Dieses Feld speichert das letzte Element oder {@link CompactSubData#open}. */
		protected final Object lastItem;

		/** Dieses Feld speichert {@code true}, wenn das letzte Element inklusiv ist. */
		protected final boolean lastInclusive;

		/** Dieser Konstruktor initialisiert das {@link CompactData} und die Grenzen und deren Inklusion.
		 *
		 * @param data {@link CompactData}.
		 * @param fromItem erstes Element oder {@link CompactSubData#open}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#open}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist. */
		public CompactSubData(final GData data, final Object fromItem, final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
			throws IllegalArgumentException {
			super(data);
			if (fromItem != CompactSubData.open) {
				if (lastItem != CompactSubData.open) {
					if (data.customItemCompare(fromItem, 0, lastItem) > 0) throw new IllegalArgumentException("fromItem > lastItem");
				} else {
					data.customItemCompare(fromItem, 0, fromItem);
				}
			} else if (lastItem != CompactSubData.open) {
				data.customItemCompare(lastItem, 0, lastItem);
			}
			this.fromItem = fromItem;
			this.fromInclusive = fromInclusive;
			this.lastItem = lastItem;
			this.lastInclusive = lastInclusive;
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und das {@code index}-te Element zu klein
		 * sind.
		 *
		 * @see CompactSubData#defaultIsTooLow(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index zu groß bzw. das {@code index}-te Element zu klein ist. */
		protected final boolean defaultIsTooLow(final int index) {
			final CompactObjectArray<Object> array = this.data.items;
			if (index >= array.size()) return true;
			if (index < 0) return false;
			return this.defaultIsTooLow(array.get(index));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu klein ist. Wenn das erste Element gleich {@link CompactSubData#open} ist,
		 * kann das gegebene Element nie zu klein sein. Anderenfalls gilt es als zu klein, wenn es entweder kleiner als das erste Element ist oder wenn das erste
		 * Element exklusiv ist und das gegebene Element gleich dem ersten Element ist.
		 *
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @see CompactSubData#fromItem
		 * @see CompactSubData#fromInclusive
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element zu klein ist. */
		protected final boolean defaultIsTooLow(final Object key) {
			final Object fromItem = this.fromItem;
			if (fromItem == CompactSubData.open) return false;
			final int comp = this.data.customItemCompare(key, 0, fromItem);
			return ((comp < 0) || ((comp == 0) && !this.fromInclusive));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und das {@code index}-te Element zu groß
		 * sind.
		 *
		 * @see CompactSubData#defaultIsTooHigh(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index bzw. das {@code index}-te Element zu groß ist. */
		protected final boolean defaultIsTooHigh(final int index) {
			final CompactObjectArray<Object> array = this.data.items;
			if (index >= array.size()) return true;
			if (index < 0) return false;
			return this.defaultIsTooHigh(array.get(index));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu groß ist. Wenn das letzte Element gleich {@link CompactSubData#open} ist,
		 * kann das gegebene Element nie zu groß sein. Anderenfalls gilt es als zu groß, wenn es entweder größer als das letzte Element ist oder wenn das letzte
		 * Element exklusiv ist und das gegebene Element gleich dem letzten Element ist.
		 *
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @see CompactSubData#lastItem
		 * @see CompactSubData#lastInclusive
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element zu groß ist. */
		protected final boolean defaultIsTooHigh(final Object key) {
			final Object lastItem = this.lastItem;
			if (lastItem == CompactSubData.open) return false;
			final int comp = this.data.customItemCompare(key, 0, lastItem);
			return ((comp > 0) || ((comp == 0) && !this.lastInclusive));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich liegt. Die Inklusitivität des ersten bzw. letzten
		 * Elements wird beachtet.
		 *
		 * @see CompactSubData#defaultIsTooLow(int)
		 * @see CompactSubData#defaultIsTooHigh(Object)
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich liegt. */
		protected final boolean defaultIsInRange(final Object key) {
			return !this.defaultIsTooLow(key) && !this.defaultIsTooHigh(key);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt.
		 *
		 * @see CompactSubData#defaultIsInRange(Object)
		 * @see CompactSubData#defaultIsInClosedRange(Object)
		 * @param key Element.
		 * @param inclusive {@code true}, wenn die Inklusitivität des ersten bzw. letzten Elements beachtet werden soll.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt. */
		protected final boolean defaultIsInRange(final Object key, final boolean inclusive) {
			return (inclusive ? this.defaultIsInRange(key) : this.defaultIsInClosedRange(key));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt. Die Inklusitivität des
		 * ersten bzw. letzten Elements ignoriert.
		 *
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt. */
		protected final boolean defaultIsInClosedRange(final Object key) {
			final GData data = this.data;
			final Object fromItem = this.fromItem, lastItem = this.lastItem;
			return ((fromItem == CompactSubData.open) || (data.customItemCompare(key, 0, fromItem) >= 0))
				&& ((lastItem == CompactSubData.open) || (data.customItemCompare(key, 0, lastItem) <= 0));
		}

		/** Diese Methode gibt den Index des ersten Elements zurück.
		 *
		 * @return Index des ersten Elements. */
		protected final int defaultFirstIndex() {
			final GData data = this.data;
			final Object fromItem = this.fromItem;
			if (fromItem == CompactSubData.open) return data.defaultFirstIndex();
			if (this.fromInclusive) return data.defaultCeilingIndex(fromItem);
			return data.defaultHigherIndex(fromItem);
		}

		/** Diese Methode gibt den Index des letzten Elements zurück.
		 *
		 * @return Index des letzten Elements. */
		protected final int defaultLastIndex() {
			final GData data = this.data;
			final Object lastItem = this.lastItem;
			if (lastItem == CompactSubData.open) return data.defaultIastIndex();
			if (this.lastInclusive) return data.defaultFloorIndex(lastItem);
			return data.defaultLowerIndex(lastItem);
		}

		/** Diese Methode gibt den Index des kleinsten Elements oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück.
		 *
		 * @see NavigableSet#first()
		 * @return Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int defaultLowestIndex() {
			final int index = this.defaultFirstIndex();
			if (this.defaultIsTooHigh(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück.
		 *
		 * @see NavigableSet#last()
		 * @return Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int defaultHighestIndex() {
			final int index = this.defaultLastIndex();
			if (this.defaultIsTooLow(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#lower(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int defaultLowerIndex(final Object item) {
			if (this.defaultIsTooHigh(item)) return this.defaultHighestIndex();
			final int index = this.data.defaultLowerIndex(item);
			if (this.defaultIsTooLow(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#floor(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int defaultFloorIndex(final Object item) {
			if (this.defaultIsTooHigh(item)) return this.defaultHighestIndex();
			final int index = this.data.defaultFloorIndex(item);
			if (this.defaultIsTooLow(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass größer oder gleich dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#ceiling(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer oder gleich dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int defaultCeilingIndex(final Object item) {
			if (this.defaultIsTooLow(item)) return this.defaultLowestIndex();
			final int index = this.data.defaultCeilingIndex(item);
			if (this.defaultIsTooHigh(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass größer dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#higher(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int defaultHigherIndex(final Object item) {
			if (this.defaultIsTooLow(item)) return this.defaultLowestIndex();
			final int index = this.data.defaultHigherIndex(item);
			if (this.defaultIsTooHigh(index)) return -index - 1;
			return index;
		}

		/** Diese Methode leert die Teilmenge. */
		protected final void defaultClearItems() {
			final int fromIndex = this.defaultFirstIndex(), lastIndex = this.defaultLastIndex();
			if (fromIndex > lastIndex) return;
			this.data.customRemove(fromIndex, (lastIndex - fromIndex) + 1);
		}

		/** Diese Methode gibt die Anzahl der Elemente in der Teilmenge zurück.
		 *
		 * @return Anzahl. */
		protected final int defaultCountItems() {
			return (this.defaultLastIndex() - this.defaultFirstIndex()) + 1;
		}

	}

	/** Dieses Feld speichert das {@link Array} der Elemente. */
	protected final CompactDataArray items;

	/** Dieser Konstruktor initialisiert das {@link Array} der Elemente. */
	public CompactData() {
		this.items = new CompactDataArray();
	}

	/** Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das Array ein.
	 *
	 * @see CompactArray#insert(int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind. */
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		this.items.insert(index, count);
	}

	/** Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem Array mit der gegebenen Länge der Belegung.
	 *
	 * @see CompactArray#remove(int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind. */
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		this.items.remove(index, count);
	}

	/** Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @see CompactArray#allocate(int)
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	protected void customAllocate(final int capacity) {
		this.items.allocate(capacity);
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum.
	 *
	 * @see CompactArray#compact() */
	protected void customCompact() {
		this.items.compact();
	}

	/** Diese Methode sucht nach dem gegebenen Objekt und gibt dessen Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück. Die
	 * <em>Einfügeposition</em> ist der Index, bei dem der Eintrag eingefügt werden müsste.
	 *
	 * @see CompactData#defaultItemIndexEquals(Object, int)
	 * @see CompactData#defaultItemIndexCompare(Object, int)
	 * @param item Objekt oder {@code null}.
	 * @return Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
	protected abstract int customItemIndex(final Object item);

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Schlüssel {@link Object#equals(Object) äquivalent} dem Schlüssel des gegebenen Elements
	 * ist.
	 *
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels oder {@code 0}.
	 * @param item Element.
	 * @return {@link Object#equals(Object) Äquivalenz} der Schlüssel. */
	protected abstract boolean customItemEquals(Object key, int hash, Object item);

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn der gegebene Schlüssel kleiner, gleich bzw. größer als der Schlüssel
	 * des gegebenen Elements ist. Die Berechnung kann auf den Schlüsseln selbst oder ihren {@link Object#hashCode() Streuwerten} beruhen.
	 *
	 * @see Comparator#compare(Object, Object)
	 * @see CompactSubData
	 * @see CompactSubData#defaultIsTooLow(Object)
	 * @see CompactSubData#defaultIsTooHigh(Object)
	 * @see CompactSubData#defaultIsInClosedRange(Object)
	 * @see CompactSubData#CompactSubData(CompactData, Object, boolean, Object, boolean)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @param item Element.
	 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der Schlüssel. */
	protected abstract int customItemCompare(Object key, int hash, Object item);

	/** Diese Methode sucht zuerst binär und danach linear nach einem Element, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt den Index dieses
	 * Elements oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück. Die <em>Einfügeposition</em> ist der Index, bei dem der Eintrag eingefügt werden
	 * müsste. Ein Element {@code element} ist dann zum gegebenen Schlüssel gleich, wenn:
	 * <pre>(customItemCompare(key, hash, element) == 0) && customItemEquals(key, hash, element)</pre>
	 *
	 * @see CompactData#defaultItemIndexCompare(Object, int)
	 * @see CompactData#customItemEquals(Object, int, Object)
	 * @see CompactData#customItemCompare(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
	protected final int defaultItemIndexEquals(final Object key, final int hash) {
		final int index = this.defaultItemIndexCompare(key, hash);
		if (index < 0) return index;
		final CompactDataArray data = this.items;
		final Object[] array = data.array();
		final int from = data.startIndex();
		if (this.customItemEquals(key, hash, array[index + from])) return index;
		Object item;
		for (int next = (index + from) - 1; (from <= next) && (this.customItemCompare(key, hash, item = array[next]) == 0); next--) {
			if (this.customItemEquals(key, hash, item)) return next - from;
		}
		for (int next = (index + from) + 1, last = data.finalIndex(); (next < last) && (this.customItemCompare(key, hash, item = array[next]) == 0); next++) {
			if (this.customItemEquals(key, hash, item)) return next - from;
		}
		return -index - 1;
	}

	/** Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen Index oder
	 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück. Die <em>Einfügeposition</em> ist der Index, bei dem der Eintrag eingefügt werden müsste.
	 *
	 * @see CompactData#customItemCompare(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
	protected final int defaultItemIndexCompare(final Object key, final int hash) {
		final CompactDataArray data = this.items;
		final Object[] array = data.array();
		final int offset = data.startIndex();
		int from = offset, last = data.finalIndex();
		while (from < last) {
			final int index = (from + last) >>> 1, value = this.customItemCompare(key, hash, array[index]);
			if (value < 0) {
				last = index;
			} else if (value > 0) {
				from = index + 1;
			} else return index - offset;
		}
		return offset - from - 1;
	}

	/** Diese Methode gibt den Index des ersten Elements zurück. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#first()
	 * @return Index des ersten Elements. */
	protected final int defaultFirstIndex() {
		return 0;
	}

	/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die Werte {@code -1} und {@code from+size}
	 * annehmen.
	 *
	 * @see NavigableSet#lower(Object)
	 * @param item Element.
	 * @return Index des größten Elements, dass kleiner dem gegebenen ist. */
	protected final int defaultLowerIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if (index < 0) return -index - 2;
		return index - 1;
	}

	/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index kann die Werte {@code -1} und
	 * {@code from+size} annehmen.
	 *
	 * @see NavigableSet#floor(Object)
	 * @param item Element.
	 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist. */
	protected final int defaultFloorIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if (index < 0) return -index - 2;
		return index;
	}

	/** Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#ceiling(Object)
	 * @param item Element.
	 * @return Index des kleinsten Elements, dass größer oder gleich dem gegebenen ist. */
	protected final int defaultCeilingIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if (index < 0) return -index - 1;
		return index;
	}

	/** Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#higher(Object)
	 * @param item Element.
	 * @return Index des kleinsten Elements, dass größer dem gegebenen ist. */
	protected final int defaultHigherIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if (index < 0) return -index - 1;
		return index + 1;
	}

	/** Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann deb Wert {@code -1} annehmen.
	 *
	 * @see NavigableSet#last()
	 * @return Index des letzten Elements. */
	protected final int defaultIastIndex() {
		return this.items.size() - 1;
	}

	/** Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @see CompactObjectArray#allocate(int)
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public final void allocate(final int capacity) throws IllegalArgumentException {
		this.customAllocate(capacity);
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public final void compact() {
		this.customCompact();
	}

}