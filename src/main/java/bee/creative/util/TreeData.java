package bee.creative.util;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// TODO CURSOR = int, das in prevs/next zeigt, wo der Index des gesuchten Eintrags drin steht
// nagativ, wenn prevs, positiv, wenn nexts gemeint
// 0= wenn undefiniert, 1... in nexts, ...-1 in ptevs

class _TreeData_ {
private
class TreeData<GKey, GValue> {

	/** Dieses Feld speichert den initialwert für {@link #prevs} und {@link #nexts}. */
	static final int[] EMPTY_INTEGERS = {-1};

	/** Dieses Feld speichert den initialwert für {@link #keys} und {@link #values}. */
	static final Object[] EMPTY_OBJECTS = {};

	/** Dieses Feld speichert die maximale Kapazität. */
	static final int MAX_CAPACITY = 0x40000000;

	{}

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	Object[] keys = TreeData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf den augmentierten Index eines größeren Eintrags oder {@code -1} ab. Für alle anderen Indizes bildet es auf
	 * den Index des nächsten reservierten Speicherbereiches ab. An Position {@link #capacity()} ist der Verweis auf den Wurzelknoten abgelegt. */
	int[] nexts = TreeData.EMPTY_INTEGERS;

	/** Dieses Feld bildet vom Index eines Eintrags auf den augmentierten Index eines kleineren Eintrags oder {@code -1} ab. */
	int[] prevs = TreeData.EMPTY_INTEGERS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	Object[] values;

	/** Dieses Feld speichert den Index des nächsten freien Speicherbereiches in {@link #nexts}.<br>
	 * Die ungenutzten Speicherbereiche bilden über {@link #nexts} eine einfach verkettete Liste. */
	int entry = 0;

	/** Dieses Feld speichert die Anzahl der Einträge. */
	protected int count = 0;

	public TreeData(final boolean withValues) {
		this.values = withValues ? TreeData.EMPTY_OBJECTS : null;
	}

	{}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public final int capacity() {
		return this.keys.length;
	}

	public final void allocate(final int capacity) throws IllegalArgumentException {

	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public final void compact() {
		this.allocate(this.count);
	}

	protected final GKey key(final int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	protected final GValue value(final int entryIndex) {
		return (GValue)this.values[entryIndex];
	}

	protected final GValue value(final int entryIndex, final GValue value) {
		final Object[] values = this.values;
		final Object result = values[entryIndex];
		values[entryIndex] = value;
		return (GValue)result;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Map#containsKey(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasKeyImpl(final Object key) {
		return 0 <= this.getIndexImpl(key);
	}

	/** Diese Methode sucht einen Eintrag mit dem gegebenen Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Map#containsValue(Object)
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasValueImpl(final Object value) {
		return this.hasValueImpl2(value, this.nexts, this.keys.length);
	}

	@SuppressWarnings ("javadoc")
	final boolean hasValueImpl2(final Object value, final int[] fieldArray, final int fieldIndex) {
		int entryIndex = fieldArray[fieldIndex];
		if (entryIndex == -1) return false;
		entryIndex >>>= 2;
		return Objects.equals(this.values[entryIndex], value) //
			|| this.hasValueImpl2(value, this.prevs, entryIndex) //
			|| this.hasValueImpl2(value, this.nexts, entryIndex);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Set#contains(Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasEntryImpl(final Object key, final Object value) {
		final int index = this.getIndexImpl(key);
		return (index >= 0) && Objects.equals(this.values[index], value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Wert zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#get(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return Wert des gefundenen Eintrags oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue getImpl(final Object key) {
		final int index = this.getIndexImpl(key);
		if (index < 0) return null;
		return (GValue)this.values[index];
	}

	/** Diese Methode gibt den Schlüssel des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Schlüssel oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GKey getKeyImpl(final int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	/** Diese Methode gibt den Wert des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue getValueImpl(final int entryIndex) {
		return (GValue)this.values[entryIndex];
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	protected final int getIndexImpl(final Object key) {
		return this.getIndexImpl2(key);
	}

	@SuppressWarnings ("javadoc")
	final int getIndexImpl2(final Object key) {
		final Object[] keys = this.keys;
		final int[] prevs = this.prevs;
		final int[] nexts = this.nexts;
		int parent = keys.length;
		int result = nexts[parent];
		while (result == -1) {
			result >>>= 2;
			final int compare = this.customCompare(keys[result], key);
			if (compare == 0) return result;
			parent = result;
			result = compare < 0 ? nexts[parent] : prevs[parent];
		}
		return -1;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel, setzt dessen Wert und gibt seinen vorherigen Wert zurück.<br>
	 * Wenn kein solcher Eintrag existierte, wird {@code null} geliefert.
	 *
	 * @see Map#put(Object, Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value neuer Wert des Eintrags.
	 * @return alert Wert des gefundenen Eintrags oder {@code null}. */
	protected final GValue putImpl(final GKey key, final GValue value) {
		final int index = this.putIndexImpl(key);
		return this.putValueImpl(index, value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code false} zurück, wenn ein solcher Eintrag existiert. Wenn kein solcher
	 * Eintrag existierte, wird er erzeugt und {@code true} geliefert.
	 *
	 * @see Set#add(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag erzeugt wurde. */
	protected final boolean putKeyImpl(final GKey key) {
		final int count = this.count;
		this.putIndexImpl(key);
		return count != this.count;
	}

	/** Diese Methode ersetzt den Wert des gegebenen Eintrags und gibt den vorherigen zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param value neuer Wert oder {@code null}.
	 * @return alter Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue putValueImpl(final int entryIndex, final GValue value) {
		final Object[] values = this.values;
		final Object result = values[entryIndex];
		values[entryIndex] = value;
		return (GValue)result;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird er erzeugt.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int putIndexImpl(final GKey key) {
		final int result = this.getIndexImpl2(key);
		if (result >= 0) return result;
		final int count = this.count + 1, capacity = this.capacity();
		if (count > TreeData.MAX_CAPACITY) throw new OutOfMemoryError();
		this.count = count;
		if (count <= capacity) return this.putIndexImpl2(key, this.nexts, this.keys.length);
		final int allocate = count + (count >> 1);
		this.allocate((allocate < 0) || (allocate > TreeData.MAX_CAPACITY) ? TreeData.MAX_CAPACITY : allocate);
		return this.putIndexImpl2(key, this.nexts, this.keys.length);
	}

	// TODO
	// suchen, , einfügen
	@SuppressWarnings ("javadoc")
	final int putIndexImpl2(final GKey key, final int[] fieldArray, final int fieldIndex) {
		final int[] prevs = this.prevs;
		final int[] nexts = this.nexts;
		final int entryIndex = fieldArray[fieldIndex];
		if (entryIndex == -1) {
			final int result = this.entry;
			this.entry = this.nexts[result];
			this.keys[result] = key;
			prevs[result] = -1;
			nexts[result] = -1;
			fieldArray[fieldIndex] = result << 2;
			return result;
		} else {
			final int entryIndex2 = entryIndex >>> 2;
			final int compare = this.customCompare(this.keys[entryIndex2], key);
			if (compare < 0) {
				final int result = this.putIndexImpl2(key, nexts, entryIndex2);

				final int fieldValue = fieldArray[fieldIndex];
				final int itemIndex = fieldValue >>> 2;
				final int itemLevel = fieldValue & 3;

				if (itemLevel == 1) { // item.NEXT war schon tiefer als item.PREV
					// TODO drehen
					// entryIndex = X

					final int _prevIndex = prevs[itemIndex]; // T1
					final int itemNextIndex = nexts[itemIndex];
					// entryNextIndex = Z | 0/1 (next kann tiefer sein oder nicht)
					final int entryNextPrevIndex = prevs[itemNextIndex]; // T23
					final int _nextNextIndex = nexts[itemNextIndex]; // T4

					nexts[itemIndex] = entryNextPrevIndex;
					prevs[itemNextIndex] = itemIndex; // 0 oder 1

					// OKAY
					fieldArray[fieldIndex] = itemNextIndex;

				} else if (itemLevel == 2) { // entry.NEXT ist so hoch wie entry.PREV

					// PASST SO
					fieldArray[fieldIndex] = itemIndex << 2;

				} else { // entry.NEXT ist nun tiefer als entry.PREV

					// PASST SO
					fieldArray[fieldIndex] = (itemIndex << 2) | 1;

				}
				return result;
			} else {
				final int result = this.putIndexImpl2(key, prevs, entryIndex2);

				final int fieldValue = fieldArray[fieldIndex];
				final int itemIndex = fieldValue >>> 2;
				final int itemLevel = fieldValue & 3;

				if (itemLevel == 2) { // entry.PREV war schon tiefer als entry.NEXT
					// TODO drehen

					fieldArray[fieldIndex] = this.prevs[entryIndex2];

				} else if (itemLevel == 1) { // entry.PREV ist so hoch wie entry.NEXT
					fieldArray[fieldIndex] = entryIndex2 << 2;
				} else { // entry.PREV ist nun tiefer als entry.NEXT
					fieldArray[fieldIndex] = (entryIndex2 << 2) | 2;
				}
				return result;
			}
		}
	}

	/** Diese Methode führt die Rechtsrotation des indikrekt gegebenen Knoten durch.<br>
	 * Mit {@code nodes} und {@code parentIndex} wird dazu der Zeiger auf den augmentierten Index des Knoten angegeben */
	@SuppressWarnings ("javadoc")
	static void rotateToNextImpl(final int[] prevs, final int[] nexts, final int[] nodes, final int parentIndex) {
		// balance(node) = -1 & balance(node.prev) = -1
		final int nodeValue = nodes[parentIndex], nodeIndex = nodeValue >>> 2;
		final int nodePrevValue = prevs[nodeIndex], nodePrevIndex = nodePrevValue >>> 2;
		// node -> node.prev -> node.prev.next -> node
		prevs[nodeIndex] = nexts[nodePrevIndex];
		nexts[nodePrevIndex] = (nodeIndex << 2) | 0; // balance(node.prev.next) = 0
		nodes[parentIndex] = nodePrevValue;
		//
		// PROCEDURE RRotation (VAR ptr : NodePtr; (* R-Rotation und entsprechende Korrektur des Balancefaktors *)
		// VAR ptr1 : NodePtr;
		// BEGIN (* RRotation *) (* balance(ptr) = -1 *)
		// ptr1 := prevs[ptr]; (* balance(prevs[ptr]) = -1 *)
		// prevs[ptr] := nexts[ptr1];
		// nexts[ptr1] := ptr;
		// balance(ptr) := 0;
		// ptr := ptr1;
		// END RRotation;
	}

	/** Diese Methode führt die Linksrotation des indikrekt gegebenen Knoten durch.<br>
	 * Mit {@code nodes} und {@code parentIndex} wird dazu der Zeiger auf den augmentierten Index des Knoten angegeben */
	@SuppressWarnings ("javadoc")
	static void rotateToPrevImpl(final int[] prevs, final int[] nexts, final int[] nodes, final int parentIndex) {
		// balance(node) = +1 & balance(node.prev) = +1
		final int nodeValue = nodes[parentIndex], nodeIndex = nodeValue >>> 2;
		final int nodeNextValue = nexts[nodeIndex], nodeNextIndex = nodeNextValue >>> 2;
		// node -> node.next -> node.next.prev -> node
		nexts[nodeIndex] = prevs[nodeNextIndex];
		// balance(node.next.prev) = 0
		prevs[nodeNextIndex] = (nodeIndex << 2) | 0;
		nodes[parentIndex] = nodeNextValue;
	}

	static void rotateToPrevNextImpl(final int[] prevs, final int[] nexts, final int[] nodes, final int parentIndex) {
		// balance(node) = -1 & balance(node.prev) = +1
		final int nodeValue = nodes[parentIndex], nodeIndex = nodeValue >>> 2;
		final int nodePrevValue = prevs[nodeIndex], nodePrevIndex = nodePrevValue >>> 2;
		final int nodePrevNextValue = nexts[nodePrevIndex], nodePrevNextIndex = nodePrevNextValue >>> 2;
		nexts[nodePrevIndex] = prevs[nodePrevNextIndex];
		// balance(node.next.prev) = balance(node.next) == +1 ? -1 : 0 (11, 10, 01, 00 -> 00, 00, 10, 00)
		prevs[nodePrevNextIndex] = (nodePrevIndex << 2) | ((8 >> ((nodePrevNextValue & 3) << 1)) & 3);
		prevs[nodeIndex] = nexts[nodePrevNextIndex];
		if ((nodePrevNextValue & 3) == 2) { // bal = -1
			nexts[nodePrevNextValue] = (nodeIndex << 2) | 1; // bal = +1;
		} else {
			nexts[nodePrevNextValue] = (nodeIndex << 2) | 0; // bal = 0;
		}
		nodes[parentIndex] = nodePrevNextValue;
		
		//
		// PROCEDURE LRRotation (VAR ptr : NodePtr; // (* LR-Rotation und entsprechende Korrektur der Balancefaktoren *)
		// VAR ptr1 , ptr2 : NodePtr;
		// BEGIN (* LRRotation *) (* balance(ptr) = -1 *)
		// ptr1 := prevs[ptr]; (* balance(prevs[ptr]) = +1 *)
		// ptr2 := nexts[ptr1];
		// nexts[ptr1] := prevs[ptr2];
		// prevs[ptr2] := ptr1;
		// prevs[ptr] := nexts[ptr2];
		// nexts[ptr2] := ptr;
		// IF balance(ptr2) = -1 THEN
		// balance(ptr) := 1;
		// ELSE
		// balance(ptr) := 0;
		// END (* IF *);
		// IF balance(ptr2) = +1 THEN
		// balance(ptr1) := -1;
		// ELSE
		// balance(ptr1) := 0;
		// END (* IF *);
		// ptr := ptr2;
		// END LRRotation;
		
	}

	// PROCEDURE Insert ( x : INTEGER; (* Programm P6.9 *)
	// VAR ptr : NodePtr;
	// VAR up : BOOLEAN);
	// (* Suchen , Einfuegen und Ausgleichen im AVL - Baum *)
	//

	
	//
	// PROCEDURE LRotation (VAR ptr : NodePtr;
	// (* L-Rotation und entsprechende Korrektur des Balance-
	// faktors *)
	// VAR ptr1 : NodePtr;
	// BEGIN (* LRotation *) (* balance(ptr) = 1 *)
	// ptr1 := nexts[ptr]; (* balance(nexts[ptr]) = 1 *)
	// nexts[ptr] := prevs[ptr1];
	// prevs[ptr1] := ptr;
	// balance(ptr) := 0;
	// ptr := ptr1;
	// END LRotation;
	//
	// PROCEDURE RLRotation (VAR ptr : NodePtr;
	// (* RL-Rotation und entsprechende Korrektur der Balance-
	// faktoren *)
	// VAR ptr1 , ptr2 : NodePtr;
	// BEGIN (* RLRotation *) (* balance(ptr) = 1 *)
	// ptr1 := nexts[ptr]; (* balance(nexts[ptr]) = -1 *)
	// ptr2 := prevs[ptr1];
	// prevs[ptr1] := nexts[ptr2];
	// nexts[ptr2] := ptr1;
	// nexts[ptr] := prevs[ptr2];
	// prevs[ptr2] := ptr;
	// IF balance(ptr2) = +1 THEN
	// balance(ptr) := -1;
	// ELSE
	// balance(ptr) := 0;
	// END (* IF *);
	// IF balance(ptr2) = -1 THEN
	// balance(ptr1) := 1;
	// ELSE
	// balance(ptr1) := 0;
	// END (* IF *);
	// ptr := ptr2;
	// END RLRotation;
	//
	// BEGIN (* Insert *)
	// IF ptr = NIL THEN (* einfuegen *)
	// ptr := ALLOCATE (ptr, SIZE(Node));
	// up := TRUE; (* Punkt (1) aus Entwurf *)
	// WITH ptr^ DO
	// key := x;
	// count := 1;
	// left := NIL;
	// right := NIL;
	// bal := 0;
	// END (* WITH *);
	// ELSIF ptr#key > x THEN (* x muss in linken Teilbaum *)
	// Insert(x, prevs[ptr], up); (* Punkt (2a) aus Entwurf *)
	// IF up THEN
	// CASE balance(ptr) OF
	// | 1: balance(ptr) := 0; (* Fall (a1) *)
	// up := FALSE;
	// | 0: balance(ptr) := -1; (* Fall (a2) *)
	// | -1: up := FALSE; (* Fall (a3) *)
	// IF balance(prevs[ptr]) = -1 THEN
	// RRotation (ptr);
	// ELSE
	// LRRotation (ptr);
	// END (* IF *);
	// balance(ptr) := 0;
	// END (* CASE *);
	// END (* IF *); (* Punkt (2a) abgeschlossen *)
	// ELSIF ptr#key < x THEN (* x muss in rechten Teilbaum *)
	// Insert(x, nexts[ptr], up); (* Punkt (2b) aus Entwurf *)
	// IF up THEN
	// CASE balance(ptr) OF
	// | -1: balance(ptr) := 0; (* Fall (b1) *)
	// up := FALSE;
	// | 0: balance(ptr) := 1; (* Fall (b2) *)
	// | 1: up := FALSE; (* Fall (b3) *)
	// IF balance(nexts[ptr]) = 1 THEN
	// LRotation (ptr); *)
	// ELSE
	// RLRotation (ptr);
	// END (* IF *);
	// balance(ptr) := 0;
	// END (* CASE *);
	// END (* IF *); (* Punkt (2b) abgeschlossen *)
	// ELSE (* x schon vorhanden *)
	// INC(ptr#count);
	// END (* IF *);
	// END Insert;

	/** Diese Methode aktualisiert die Tiefe des gegebenen Teilbaums und balanziert diesen falls nötig aus. */
	void _calcShape_TREE_(final int[] fieldArray, final int fieldIndex) {
		final int[] prevs = this.prevs;
		final int[] nexts = this.nexts;
		final int _itemIndex = fieldArray[fieldIndex];
		final int _prevIndex = prevs[_itemIndex];
		final int _nextIndex = nexts[_itemIndex];
		int _prevLevel = _prevIndex & 3;
		int _nextLevel = _nextIndex ? _levelArray[_nextIndex] : 0;
		if ((_nextLevel + 1) < _prevLevel) {
			final int _prevPrevIndex = prevs[_prevIndex];
			final int _prevNextIndex = nexts[_prevIndex];
			prevs[_itemIndex] = _prevNextIndex;
			nexts[_prevIndex] = _itemIndex;
			_itemField[0] = _prevIndex;
			_nextLevel = _calcLevel_TREE_(_prevNextIndex ? _levelArray[_prevNextIndex] : 0, _nextLevel);
			_levelArray[_itemIndex] = _nextLevel;
			_prevLevel = _calcLevel_TREE_(_prevPrevIndex ? _levelArray[_prevPrevIndex] : 0, _nextLevel);
			_levelArray[_prevIndex] = _prevLevel;
			return;
		}
		if ((_prevLevel + 1) < _nextLevel) {
			final int _nextPrevIndex = prevs[_nextIndex];
			final int _nextNextIndex = nexts[_nextIndex];
			nexts[_itemIndex] = _nextPrevIndex;
			prevs[_nextIndex] = _itemIndex;
			_itemField[0] = _nextIndex;
			_prevLevel = _calcLevel_TREE_(_prevLevel, _nextPrevIndex ? _levelArray[_nextPrevIndex] : 0);
			_levelArray[_itemIndex] = _prevLevel;
			_nextLevel = _calcLevel_TREE_(_prevLevel, _nextNextIndex ? _levelArray[_nextNextIndex] : 0);
			_levelArray[_nextIndex] = _nextLevel;
			return;
		}
		_levelArray[_itemIndex] = _calcLevel_TREE_(_prevLevel, _nextLevel);
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt den Wert des Eintrags zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#remove(Object)
	 * @param key Schlüssel.
	 * @return Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue popImpl(final Object key) {
		final int entry = this.popIndexImpl(key);
		if (entry < 0) return null;
		final Object[] values = this.values;
		final Object result = values[entry];
		values[entry] = null;
		return (GValue)result;
	}

	/** Diese Methode entfernt den gegeben Schlüssel und liefet nur dann {@code true}, wenn dieser zuvor über {@link #putKeyImpl(Object)} hinzugefügt wurde.
	 *
	 * @see Set#remove(Object)
	 * @param key Schlüssel.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popKeyImpl(final Object key) {
		final int count = this.count;
		this.popIndexImpl(key);
		return count != this.count;
	}

	/** Diese Methode entfernt einen Eintrag mit dem gegebenen Wert.
	 *
	 * @see Map#containsValue(Object)
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefunden und entfernt wurde. */
	protected final boolean popValueImpl(final Object value) {
		// TODO
		return false;
	}

	final int popIndexImpl(final Object _key) {
		return this.popIndexImpl(_key, this.nexts, this.keys.length);
	}

	final int popIndexImpl(final Object  _key, final int[] parentArray, final int parentIndex) {
			final int _itemIndex = parentArray[0];
			if (!_itemIndex) return false;
			final int[] _prevArray = this.prevs;
			final int[] _nextArray = this.nexts;
			int* _levelArray = this._levels_;
			int _compare = KEY_POLICY::compareItem(&_key, this.keys + _itemIndex);
			if (!_compare) {
				final int _prevIndex = _prevArray[_itemIndex];
				final int _nextIndex = _nextArray[_itemIndex];
				if (_nextIndex) {
					if (_prevIndex) {
						this._movIndex_TREE_(_prevIndex, _nextArray + _itemIndex);
					}
					parentArray[0] = _nextIndex;
				} else {
					parentArray[0] = _prevIndex;
				}
				_nextArray[_itemIndex] = this.entry;
				this.entry = _itemIndex;
				this.count--;
				KEY_POLICY::resetItem(_keyArray_ + _itemIndex);
				VALUE_POLICY::resetItem(this.values + _itemIndex);
				return true;
			}
			final bool _result = _compare < 0 ? this._popIndex_TREE_(_key, _prevArray + _itemIndex) : this._popIndex_TREE_(_key, _nextArray + _itemIndex);
			this._calcShape_TREE_(parentArray, _prevArray, _nextArray, _levelArray);
			return _result;
		}

	GKey _lowerKey_TREE_(final GKey _key) {
		final int _itemIndex = this.lowerIndexImpl(_key);
		return _itemIndex ? this.keys + _itemIndex : 0;
	}

	protected final int lowerIndexImpl(final GKey key) {
		return this._nearIndex_TREE_(key, true);
	}

	protected final GKey lowestKeyImpl() {
		final int itemIndex = this.lowestIndexImpl();
		return itemIndex < 0 ? null : (GKey)this.keys[itemIndex];
	}

	TreeMap<K, V> d;

	protected final int lowestIndexImpl() {
		return this.lastIndexImpl(this.nexts[this.keys.length], this.prevs);
	}

	GKey _higherKey_TREE_(final GKey _key) {
		final int _itemIndex = this._higherIndex_TREE_(_key);
		return _itemIndex ? this.keys + _itemIndex : 0;
	}

	int _higherIndex_TREE_(final GKey _key) {
		return this._nearIndex_TREE_(_key, false);
	}

	GKey _highestKey_TREE_() {
		final int _itemIndex = this.highestIndexImpl();
		return _itemIndex ? this.keys + _itemIndex : 0;
	}

	protected final int highestIndexImpl() {
		return this.lastIndexImpl(this.nexts[this.keys.length], this.nexts);
	}

	final int lastIndexImpl(int itemIndex, final int[] pathArray) {
		if (itemIndex == -1) return -1;
		while (true) {
			itemIndex >>>= 2;
			final int pathIndex = pathArray[itemIndex];
			if (pathIndex == -1) return itemIndex;
			itemIndex = pathIndex;
		}
	}

	int _nearIndex_TREE_(final GKey _key, final boolean nearPrev) {
		final GKey[] _keyArray = this.keys;
		final int[] _prevArray = this.prevs;
		final int[] _nextArray = this.nexts;
		int _itemIndex = this._null_;
		int _nearIndex = 0;
		while (_itemIndex) {
			final int _compare = this.customCompare(_key, _keyArray[_itemIndex]);
			if (!_compare) {
				_itemIndex = nearPrev ? this.lastIndexImpl(_prevArray[_itemIndex], _nextArray) : this.lastIndexImpl(_nextArray[_itemIndex], _prevArray);
				if (_itemIndex) return _itemIndex;
				return _nearIndex;
			}
			if (_compare < 0) {
				if (!nearPrev) {
					_nearIndex = _itemIndex;
				}
				_itemIndex = _prevArray[_itemIndex];
			} else {
				if (nearPrev) {
					_nearIndex = _itemIndex;
				}
				_itemIndex = _nextArray[_itemIndex];
			}
		}
		return _nearIndex;
	}

	/** Diese Klasse definiert die Zustandsdaten des @c SUCCursor dieser @c SUCMapping. */
	class ITERATOR {

		/* SUCIterator */
		bool move(final int  _offset) {
			final OBJECT  _object = *_mapping_._object_.get();
			for (int _count = _offset; _count < 0; ++_count) {
				if (MAPPING_POLICY::HASH_ENABLED) {
					_seekPrev_HASH_(_object._hashMask_, _object._hashRootArray_, _object._hashNextArray_);
				} else {
					this._seekItem_TREE_(_object._treeNextArray_, _object._treePrevArray_);
				}
			}
			for (int _count = _offset; _count > 0; --_count) {
				if (MAPPING_POLICY::HASH_ENABLED) {
					_seekNext_HASH_(_object._hashRootArray_, _object._hashNextArray_);
				} else {
					this._seekItem_TREE_(_object._treePrevArray_, _object._treeNextArray_);
				}
			}
			return true;
		}

		void moveToFirst() {
			final OBJECT _object = *_mapping_._object_.get();
			if (MAPPING_POLICY::HASH_ENABLED) {
				_hashRootIndex_ = _object._hashMask_ + 1;
				_seekNext_HASH_ROOT_(_object._hashRootArray_, _object._hashNextArray_);
			} else {
				this._treeRootIndex_ = 0;
				this._treeRootArray_[0] = 0;
				final int _itemIndex = _object._treeRoot_;
				if (_itemIndex) {
					this._seekItem_TREE_ITEM_(_itemIndex, 0, this._treeRootArray_, _object._treePrevArray_);
				}
			}

		}

		void moveToLast() {
			final OBJECT _object = *_mapping_._object_.get();
			if (MAPPING_POLICY::HASH_ENABLED) {
				_hashRootIndex_ = -1;
				_seekPrev_HASH_ROOT_(_object._hashMask_, _object._hashRootArray_, _object._hashNextArray_);
			} else {
				this._treeRootIndex_ = 0;
				this._treeRootArray_[0] = 0;
				final int _itemIndex = _object._treeRoot_;
				if (_itemIndex) {
					this._seekItem_TREE_ITEM_(_itemIndex, 0, this._treeRootArray_, _object._treeNextArray_);
				}
			}
		}

		void _seekItem_TREE_(final int[] _prevArray, final int[] _nextArray) {
			final int _rootIndex = this._treeRootIndex_;
			final int[] _rootArray = this._treeRootArray_;
			final int _itemIndex = _rootArray[_rootIndex];
			if (!_itemIndex) return;
			final int _nextIndex = _nextArray[_itemIndex];
			if (_nextIndex) {
				this._seekItem_TREE_ITEM_(_nextIndex, _rootIndex, _rootArray, _prevArray);
			} else {
				this._seekItem_TREE_ROOT_(_itemIndex, _rootIndex, _rootArray, _prevArray, _nextArray);
			}
		}

		void _seekItem_TREE_ROOT_(int _itemIndex, int _rootIndex, final int[] _rootArray, final int[] _prevArray, final int[] _nextArray) {
			int _gotoIndex;
			while (true) {
				--_rootIndex;
				_gotoIndex = _rootArray[_rootIndex];
				if (!_gotoIndex) {
					break;
				}
				if (_itemIndex == _prevArray[_gotoIndex]) {
					break;
				}
				_itemIndex = _gotoIndex;
			}
			_value_._index_ = _gotoIndex;
			this._treeRootIndex_ = _rootIndex;
		}

		void _seekItem_TREE_ITEM_(int _itemIndex, int _rootIndex, final int[] _rootArray, final int[] _prevArray) {
			while (true) {
				++_rootIndex;
				_rootArray[_rootIndex] = _itemIndex;
				final int _gotoIndex = _prevArray[_itemIndex];
				if (!_gotoIndex) {
					break;
				}
				_itemIndex = _gotoIndex;
			}
			_value_._index_ = _itemIndex;
			this._treeRootIndex_ = _rootIndex;
		}

		int _treeRootIndex_;

		int[] _treeRootArray_ = new int[32];

	};

	protected int customCompare(final Object thatKey, final Object thisKey) {
		return Comparators.compare((Comparable)thisKey, (Comparable)thatKey);
	}

}
}