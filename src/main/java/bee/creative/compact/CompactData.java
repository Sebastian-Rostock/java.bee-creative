package bee.creative.compact;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import bee.creative.array.Array;
import bee.creative.array.CompactArray;
import bee.creative.array.CompactObjectArray;

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
public abstract class CompactData {

	/** Diese Klasse implementiert das {@link CompactObjectArray} der {@link CompactData}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static final class CompactDataArray extends CompactObjectArray<Object> {

		/** Dieses Feld speichert das leere Array. */
		protected static final Object[] _empty_ = new Object[0];

		{}

		/** {@inheritDoc} */
		@Override
		public int compare(final Object o1, final Object o2) {
			return 0;
		}

		/** {@inheritDoc} */
		@Override
		protected Object[] _allocArray_(final int length) {
			if (length == 0) return CompactDataArray._empty_;
			return new Object[length];
		}

	}

	/** Diese Klasse implementiert ein abstraktes Objekt mit {@link CompactData}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s. */
	protected static abstract class CompactDataOwner<GData extends CompactData> {

		/** Dieses Feld speichert die {@link CompactData}. */
		protected final GData _data_;

		/** Dieser Konstruktor initialisiert die {@link CompactData}.
		 *
		 * @param data {@link CompactData}. */
		public CompactDataOwner(final GData data) {
			if (data == null) throw new NullPointerException("data = null");
			this._data_ = data;
		}

	}

	/** Diese Klasse implementiert einen abstrakten {@link CompactData}-{@link Iterator}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s. */
	protected static abstract class CompactDataIterator<GItem, GData extends CompactData> extends CompactData.CompactDataOwner<GData> implements Iterator<GItem> {

		/** Dieses Feld speichert den Index des ersten Elements (inklusiv). */
		protected int _from_;

		/** Dieses Feld speichert den Index des aktuellen Elements. */
		protected int _item_;

		/** Dieses Feld speichert den Index des letztem Elements (exklusiv). */
		protected int _last_;

		/** Dieser Konstruktor initialisiert {@link CompactData} und Indizes.
		 *
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv). */
		public CompactDataIterator(final GData data, final int from, final int last) {
			super(data);
			this._from_ = from;
			this._item_ = -1;
			this._last_ = last;
		}

		{}

		/** Diese Methode gibt das {@code index}-te Element zurück.
		 *
		 * @param index Index.
		 * @return {@code index}-tes Element. */
		protected abstract GItem _next_(int index);

		/** {@inheritDoc} */
		@Override
		public boolean hasNext() {
			return this._from_ < this._last_;
		}

		/** {@inheritDoc} */
		@Override
		public void remove() {
			final int item = this._item_;
			if (item < 0) throw new IllegalStateException();
			this._data_._remove_(item, 1);
			this._item_ = -1;
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

		{}

		/** {@inheritDoc} */
		@Override
		public GItem next() {
			return this._next_(this._item_ = (this._from_ = this._from_ + 1));
		}

		/** {@inheritDoc} */
		@Override
		public void remove() {
			super.remove();
			this._from_--;
			this._last_--;
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

		{}

		/** {@inheritDoc} */
		@Override
		public GItem next() {
			return this._next_(this._item_ = (this._last_ = this._last_ - 1));
		}

	}

	/** Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactData}s und wird zur realisierung von {@link NavigableSet}s und
	 * {@link NavigableMap} s verwendet.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s. */
	public static abstract class CompactSubData<GData extends CompactData> extends CompactDataOwner<GData> {

		/** Dieses Feld speichert das Objekt zur offenen Begrenzung von Teilmengen. */
		protected static final Object _open_ = new Object();

		{}

		/** Dieses Feld speichert das erste Element oder {@link CompactSubData#_open_}. */
		protected final Object _fromItem_;

		/** Dieses Feld speichert {@code true}, wenn das erste Element inklusiv ist. */
		protected final boolean _fromInclusive_;

		/** Dieses Feld speichert das letzte Element oder {@link CompactSubData#_open_}. */
		protected final Object _lastItem_;

		/** Dieses Feld speichert {@code true}, wenn das letzte Element inklusiv ist. */
		protected final boolean _lastInclusive_;

		/** Dieser Konstruktor initialisiert das {@link CompactData} und die Grenzen und deren Inklusion.
		 *
		 * @param data {@link CompactData}.
		 * @param fromItem erstes Element oder {@link CompactSubData#_open_}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#_open_}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist. */
		public CompactSubData(final GData data, final Object fromItem, final boolean fromInclusive, final Object lastItem, final boolean lastInclusive)
			throws IllegalArgumentException {
			super(data);
			if (fromItem != CompactSubData._open_) {
				if (lastItem != CompactSubData._open_) {
					if (data._itemCompare_(fromItem, 0, lastItem) > 0) throw new IllegalArgumentException("fromItem > lastItem");
				} else {
					data._itemCompare_(fromItem, 0, fromItem);
				}
			} else if (lastItem != CompactSubData._open_) {
				data._itemCompare_(lastItem, 0, lastItem);
			}
			this._fromItem_ = fromItem;
			this._fromInclusive_ = fromInclusive;
			this._lastItem_ = lastItem;
			this._lastInclusive_ = lastInclusive;
		}

		{}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und das {@code index}-te Element zu klein
		 * sind.
		 *
		 * @see CompactSubData#_isTooLow_(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index zu groß bzw. das {@code index}-te Element zu klein ist. */
		protected final boolean _isTooLow_(final int index) {
			final CompactObjectArray<Object> array = this._data_._items_;
			if (index >= array.size()) return true;
			if (index < 0) return false;
			return this._isTooLow_(array.get(index));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu klein ist. Wenn das erste Element gleich {@link CompactSubData#_open_}
		 * ist, kann das gegebene Element nie zu klein sein. Anderenfalls gilt es als zu klein, wenn es entweder kleiner als das erste Element ist oder wenn das
		 * erste Element exklusiv ist und das gegebene Element gleich dem ersten Element ist.
		 *
		 * @see CompactData#_itemCompare_(Object, int, Object)
		 * @see CompactSubData#_fromItem_
		 * @see CompactSubData#_fromInclusive_
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element zu klein ist. */
		protected final boolean _isTooLow_(final Object key) {
			final Object fromItem = this._fromItem_;
			if (fromItem == CompactSubData._open_) return false;
			final int comp = this._data_._itemCompare_(key, 0, fromItem);
			return ((comp < 0) || ((comp == 0) && !this._fromInclusive_));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und das {@code index}-te Element zu groß
		 * sind.
		 *
		 * @see CompactSubData#_isTooHigh_(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index bzw. das {@code index}-te Element zu groß ist. */
		protected final boolean _isTooHigh_(final int index) {
			final CompactObjectArray<Object> array = this._data_._items_;
			if (index >= array.size()) return true;
			if (index < 0) return false;
			return this._isTooHigh_(array.get(index));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu groß ist. Wenn das letzte Element gleich {@link CompactSubData#_open_}
		 * ist, kann das gegebene Element nie zu groß sein. Anderenfalls gilt es als zu groß, wenn es entweder größer als das letzte Element ist oder wenn das
		 * letzte Element exklusiv ist und das gegebene Element gleich dem letzten Element ist.
		 *
		 * @see CompactData#_itemCompare_(Object, int, Object)
		 * @see CompactSubData#_lastItem_
		 * @see CompactSubData#_lastInclusive_
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element zu groß ist. */
		protected final boolean _isTooHigh_(final Object key) {
			final Object lastItem = this._lastItem_;
			if (lastItem == CompactSubData._open_) return false;
			final int comp = this._data_._itemCompare_(key, 0, lastItem);
			return ((comp > 0) || ((comp == 0) && !this._lastInclusive_));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich liegt. Die Inklusitivität des ersten bzw. letzten
		 * Elements wird beachtet.
		 *
		 * @see CompactSubData#_isTooLow_(int)
		 * @see CompactSubData#_isTooHigh_(Object)
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich liegt. */
		protected final boolean _isInRange_(final Object key) {
			return !this._isTooLow_(key) && !this._isTooHigh_(key);
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt.
		 *
		 * @see CompactSubData#_isInRange_(Object)
		 * @see CompactSubData#_isInClosedRange_(Object)
		 * @param key Element.
		 * @param inclusive {@code true}, wenn die Inklusitivität des ersten bzw. letzten Elements beachtet werden soll.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt. */
		protected final boolean _isInRange_(final Object key, final boolean inclusive) {
			return (inclusive ? this._isInRange_(key) : this._isInClosedRange_(key));
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt. Die Inklusitivität des
		 * ersten bzw. letzten Elements ignoriert.
		 *
		 * @see CompactData#_itemCompare_(Object, int, Object)
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt. */
		protected final boolean _isInClosedRange_(final Object key) {
			final GData data = this._data_;
			final Object fromItem = this._fromItem_, lastItem = this._lastItem_;
			return ((fromItem == CompactSubData._open_) || (data._itemCompare_(key, 0, fromItem) >= 0))
				&& ((lastItem == CompactSubData._open_) || (data._itemCompare_(key, 0, lastItem) <= 0));
		}

		/** Diese Methode gibt den Index des ersten Elements zurück.
		 *
		 * @return Index des ersten Elements. */
		protected final int _firstIndex_() {
			final GData data = this._data_;
			final Object fromItem = this._fromItem_;
			if (fromItem == CompactSubData._open_) return data._firstIndex_();
			if (this._fromInclusive_) return data._ceilingIndex_(fromItem);
			return data._higherIndex_(fromItem);
		}

		/** Diese Methode gibt den Index des letzten Elements zurück.
		 *
		 * @return Index des letzten Elements. */
		protected final int _lastIndex_() {
			final GData data = this._data_;
			final Object lastItem = this._lastItem_;
			if (lastItem == CompactSubData._open_) return data._lastIndex_();
			if (this._lastInclusive_) return data._floorIndex_(lastItem);
			return data._lowerIndex_(lastItem);
		}

		/** Diese Methode gibt den Index des kleinsten Elements oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück.
		 *
		 * @see NavigableSet#first()
		 * @return Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int _lowestIndex_() {
			final int index = this._firstIndex_();
			if (this._isTooHigh_(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück.
		 *
		 * @see NavigableSet#last()
		 * @return Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int _highestIndex_() {
			final int index = this._lastIndex_();
			if (this._isTooLow_(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#lower(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int _lowerIndex_(final Object item) {
			if (this._isTooHigh_(item)) return this._highestIndex_();
			final int index = this._data_._lowerIndex_(item);
			if (this._isTooLow_(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#floor(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int _floorIndex_(final Object item) {
			if (this._isTooHigh_(item)) return this._highestIndex_();
			final int index = this._data_._floorIndex_(item);
			if (this._isTooLow_(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass größer oder gleich dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#ceiling(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer oder gleich dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int _ceilingIndex_(final Object item) {
			if (this._isTooLow_(item)) return this._lowestIndex_();
			final int index = this._data_._ceilingIndex_(item);
			if (this._isTooHigh_(index)) return -index - 1;
			return index;
		}

		/** Diese Methode gibt den Index des größten Elements zurück, dass größer dem gegebenen ist. Wenn kein solches Element existiert wird
		 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück gegeben.
		 *
		 * @see NavigableSet#higher(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer dem gegebenen ist, oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
		protected final int _higherIndex_(final Object item) {
			if (this._isTooLow_(item)) return this._lowestIndex_();
			final int index = this._data_._higherIndex_(item);
			if (this._isTooHigh_(index)) return -index - 1;
			return index;
		}

		/** Diese Methode leert die Teilmenge. */
		protected final void _clearItems_() {
			final int fromIndex = this._firstIndex_(), lastIndex = this._lastIndex_();
			if (fromIndex > lastIndex) return;
			this._data_._remove_(fromIndex, (lastIndex - fromIndex) + 1);
		}

		/** Diese Methode gibt die Anzahl der Elemente in der Teilmenge zurück.
		 *
		 * @return Anzahl. */
		protected final int _countItems_() {
			return (this._lastIndex_() - this._firstIndex_()) + 1;
		}

	}

	{}

	/** Dieses Feld speichert das {@link Array} der Elemente. */
	protected final CompactDataArray _items_;

	/** Dieser Konstruktor initialisiert das {@link Array} der Elemente. */
	public CompactData() {
		this._items_ = new CompactDataArray();
	}

	{}

	/** Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das Array ein.
	 *
	 * @see CompactArray#insert(int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind. */
	protected void _insert_(final int index, final int count) throws IllegalArgumentException {
		this._items_.insert(index, count);
	}

	/** Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem Array mit der gegebenen Länge der Belegung.
	 *
	 * @see CompactArray#remove(int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind. */
	protected void _remove_(final int index, final int count) throws IllegalArgumentException {
		this._items_.remove(index, count);
	}

	/** Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @see CompactArray#allocate(int)
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	protected void _allocate_(final int capacity) {
		this._items_.allocate(capacity);
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum.
	 *
	 * @see CompactArray#compact() */
	protected void _compact_() {
		this._items_.compact();
	}

	/** Diese Methode sucht nach dem gegebenen Objekt und gibt dessen Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück. Die
	 * <em>Einfügeposition</em> ist der Index, bei dem der Eintrag eingefügt werden müsste.
	 *
	 * @see CompactData#_itemIndexEquals_(Object, int)
	 * @see CompactData#_itemIndexCompare_(Object, int)
	 * @param item Objekt oder {@code null}.
	 * @return Index oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
	protected abstract int _itemIndex_(final Object item);

	/** Diese Methode sucht zuerst binär und danach linear nach einem Element, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt den Index dieses
	 * Elements oder <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück. Die <em>Einfügeposition</em> ist der Index, bei dem der Eintrag eingefügt werden
	 * müsste. Ein Element {@code element} ist dann zum gegebenen Schlüssel gleich, wenn:
	 * <pre>(customItemCompare(key, hash, element) == 0) && customItemEquals(key, hash, element)</pre>
	 *
	 * @see CompactData#_itemIndexCompare_(Object, int)
	 * @see CompactData#_itemEquals_(Object, int, Object)
	 * @see CompactData#_itemCompare_(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
	protected final int _itemIndexEquals_(final Object key, final int hash) {
		final int index = this._itemIndexCompare_(key, hash);
		if (index < 0) return index;
		final CompactDataArray data = this._items_;
		final Object[] array = data.array();
		final int from = data.startIndex();
		if (this._itemEquals_(key, hash, array[index + from])) return index;
		Object item;
		for (int next = (index + from) - 1; (from <= next) && (this._itemCompare_(key, hash, item = array[next]) == 0); next--) {
			if (this._itemEquals_(key, hash, item)) return next - from;
		}
		for (int next = (index + from) + 1, last = data.finalIndex(); (next < last) && (this._itemCompare_(key, hash, item = array[next]) == 0); next++) {
			if (this._itemEquals_(key, hash, item)) return next - from;
		}
		return -index - 1;
	}

	/** Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen Index oder
	 * <code>(-(<em>Einfügeposition</em>) - 1)</code> zurück. Die <em>Einfügeposition</em> ist der Index, bei dem der Eintrag eingefügt werden müsste.
	 *
	 * @see CompactData#_itemCompare_(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<em>Einfügeposition</em>) - 1)</code>. */
	protected final int _itemIndexCompare_(final Object key, final int hash) {
		final CompactDataArray data = this._items_;
		final Object[] array = data.array();
		final int offset = data.startIndex();
		int from = offset, last = data.finalIndex();
		while (from < last) {
			final int index = (from + last) >>> 1, value = this._itemCompare_(key, hash, array[index]);
			if (value < 0) {
				last = index;
			} else if (value > 0) {
				from = index + 1;
			} else return index - offset;
		}
		return offset - from - 1;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Schlüssel {@link Object#equals(Object) äquivalent} dem Schlüssel des gegebenen Elements
	 * ist.
	 *
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels oder {@code 0}.
	 * @param item Element.
	 * @return {@link Object#equals(Object) Äquivalenz} der Schlüssel. */
	protected abstract boolean _itemEquals_(Object key, int hash, Object item);

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn der gegebene Schlüssel kleiner, gleich bzw. größer als der Schlüssel
	 * des gegebenen Elements ist. Die Berechnung kann auf den Schlüsseln selbst oder ihren {@link Object#hashCode() Streuwerten} beruhen.
	 *
	 * @see Comparator#compare(Object, Object)
	 * @see CompactSubData
	 * @see CompactSubData#_isTooLow_(Object)
	 * @see CompactSubData#_isTooHigh_(Object)
	 * @see CompactSubData#_isInClosedRange_(Object)
	 * @see CompactSubData#CompactSubData(CompactData, Object, boolean, Object, boolean)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @param item Element.
	 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der Schlüssel. */
	protected abstract int _itemCompare_(Object key, int hash, Object item);

	/** Diese Methode gibt den Index des ersten Elements zurück. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#first()
	 * @return Index des ersten Elements. */
	protected final int _firstIndex_() {
		return 0;
	}

	/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die Werte {@code -1} und {@code from+size}
	 * annehmen.
	 *
	 * @see NavigableSet#lower(Object)
	 * @param item Element.
	 * @return Index des größten Elements, dass kleiner dem gegebenen ist. */
	protected final int _lowerIndex_(final Object item) {
		final int index = this._itemIndex_(item);
		if (index < 0) return -index - 2;
		return index - 1;
	}

	/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index kann die Werte {@code -1} und
	 * {@code from+size} annehmen.
	 *
	 * @see NavigableSet#floor(Object)
	 * @param item Element.
	 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist. */
	protected final int _floorIndex_(final Object item) {
		final int index = this._itemIndex_(item);
		if (index < 0) return -index - 2;
		return index;
	}

	/** Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#ceiling(Object)
	 * @param item Element.
	 * @return Index des kleinsten Elements, dass größer oder gleich dem gegebenen ist. */
	protected final int _ceilingIndex_(final Object item) {
		final int index = this._itemIndex_(item);
		if (index < 0) return -index - 1;
		return index;
	}

	/** Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#higher(Object)
	 * @param item Element.
	 * @return Index des kleinsten Elements, dass größer dem gegebenen ist. */
	protected final int _higherIndex_(final Object item) {
		final int index = this._itemIndex_(item);
		if (index < 0) return -index - 1;
		return index + 1;
	}

	/** Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann deb Wert {@code -1} annehmen.
	 *
	 * @see NavigableSet#last()
	 * @return Index des letzten Elements. */
	protected final int _lastIndex_() {
		return this._items_.size() - 1;
	}

	/** Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @see CompactObjectArray#allocate(int)
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public final void allocate(final int capacity) throws IllegalArgumentException {
		this._allocate_(capacity);
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public final void compact() {
		this._compact_();
	}

}