package bee.creative.util;

import java.util.Map;
import java.util.Set;

// TODO CURSOR = int, das in prevs/next zeigt, wo der Index des gesuchten Eintrags drin steht
// nagativ, wenn prevs, positiv, wenn nexts gemeint
// 0= wenn undefiniert, 1... in nexts, ...-1 in ptevs

class _TreeData_ {

	// Die Algorithmen zur Balanzierung sind dem "Skriptum Informatik - eine konventionelle Einführung" von Hans-Jürgen Appelrath und Jochen Ludewig entnommen.
	static class TreeData<GKey, GValue> {

		/** Dieses Feld speichert den initialwert für {@link #prevs} und {@link #nexts}. */
		static final int[] EMPTY_INTEGERS = {-1};

		/** Dieses Feld speichert den initialwert für {@link #keys} und {@link #values}. */
		static final Object[] EMPTY_OBJECTS = {};

		/** Dieses Feld speichert die maximale Kapazität. */
		static final int MAX_CAPACITY = 0x40000000;

		{}

		/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
		Object[] keys = TreeData.EMPTY_OBJECTS;

		/** Dieses Feld bildet vom Index eines Eintrags auf den augmentierten Index eines größeren Eintrags oder {@code -1} ab. Für alle anderen Indizes bildet es
		 * auf den Index des nächsten reservierten Speicherbereiches ab. An Position {@link #capacityImpl()} ist der Verweis auf den Wurzelknoten abgelegt. */
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
		protected final int capacityImpl() {
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

		/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code false} zurück, wenn ein solcher Eintrag existiert. Wenn kein
		 * solcher Eintrag existierte, wird er erzeugt und {@code true} geliefert.
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
			final int count = this.count + 1, capacity = this.capacityImpl();

			if (count <= capacity) return this.putIndexImpl2(key, this.nexts, capacity) & 0x7FFFFFFF;
			final int allocate = count + (count >> 1);
			this.allocate((allocate < 0) || (allocate > TreeData.MAX_CAPACITY) ? TreeData.MAX_CAPACITY : allocate);
			return this.putIndexImpl2(key, this.nexts, this.capacityImpl()) & 0x7FFFFFFF;
		}

		// TODO
		// suchen, , einfügen
		@SuppressWarnings ("javadoc")
		final int putIndexImpl2(final GKey key, final int[] fields, final int fieldIndex) {
			final int[] prevs = this.prevs, nexts = this.nexts;
			final int nodeValue = fields[fieldIndex];
			if (nodeValue == -1) { // leres Blatt überschreiben
				final Object[] keys = this.keys;
				final int count = this.count + 1;
				final int result = this.entry;
				if (count > keys.length) throw new OutOfMemoryError();
				this.count = count;
				this.entry = nexts[result];
				keys[result] = key;
				prevs[result] = -1;
				nexts[result] = -1;
				fields[fieldIndex] = (result << 2) | 0;
				return result | 0x80000000; // negativ, da Teilbaum zu balanziren
			}
			final int nodePosition = TreeData.getNodePosition(nodeValue);
			final int compare = this.customCompare(this.keys[nodePosition], key);
			if (compare > 0) { // positiv, wenn im rechten Teilbaum einzufügen
				final int result = this.putIndexImpl2(key, prevs, nodePosition);
				if (result >= 0) return result; // positiv, wenn Teilbaum bereits balanziert
				final int nodeValue2 = fields[fieldIndex]; // kann durch einfügen anders sein
				final int nodeBalance2 = TreeData.getNodeBalance(nodeValue2);
				final int nodePosition2 = TreeData.getNodePosition(nodeValue2);
				if (nodeBalance2 == 1) { // wenn rechtslastig
					fields[fieldIndex] = TreeData.getNodeValue(nodePosition2, 0);
					return result & 0x7FFFFFFF; // positiv, da Teilbaum bereits balanziert
				}
				if (nodeBalance2 == 2) { // wenn linkslastig
					fields[fieldIndex] = TreeData.getNodeValue( //
						TreeData.getNodeBalance(prevs[nodePosition2]) == 2 // wenn linkslastig
							? TreeData.rotateToNextImpl(prevs, nexts, nodePosition2) //
							: TreeData.rotateToPrevNextImpl(prevs, nexts, nodePosition2), //
						0);
					return result & 0x7FFFFFFF;
				}
				fields[fieldIndex] = TreeData.getNodeValue(nodePosition2, 2);
				return result;
			}
			if (compare < 0) { // negativ, wenn im rechten Teilbaum einzufügen
				final int result = this.putIndexImpl2(key, nexts, nodePosition);
				if (result >= 0) return result; // positiv, wenn Teilbaum bereits balanziert
				final int nodeValue2 = fields[fieldIndex]; // kann durch einfügen anders sein
				final int nodeBalance2 = TreeData.getNodeBalance(nodeValue2);
				final int nodePosition2 = TreeData.getNodePosition(nodeValue2);
				if (nodeBalance2 == 2) { // wenn linkslastig
					fields[fieldIndex] = TreeData.getNodeValue(nodePosition2, 0);
					return result & 0x7FFFFFFF; // positiv, da Teilbaum bereits balanziert
				}
				if (nodeBalance2 == 1) { // wenn rechtslastig
					fields[fieldIndex] = TreeData.getNodeValue( //
						TreeData.getNodeBalance(nexts[nodePosition2]) == 1 // wenn rechtslastig
							? TreeData.rotateToPrevImpl(prevs, nexts, nodePosition2) //
							: TreeData.rotateToNextPrevImpl(prevs, nexts, nodePosition2), //
						0);
					return result & 0x7FFFFFFF;
				}
				fields[fieldIndex] = TreeData.getNodeValue(nodePosition2, 1);
				return result;
			}
			return nodePosition; // positiv, da Teilbaum unverändert
		}

		static int getNodeValue(final int nodeIndex, final int nodeBalance) {
			return (nodeIndex << 2) | nodeBalance;
		}

		/** Diese Methode gibt die Balanz des gegebenen Knoten zurück.
		 *
		 * @param nodeValue Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return Knotenbalanz mit {@code 0} und {@code 3} für gleichgewicht, {@code 1} für rechtslastig und {@code 2} für linkslastig. */
		static int getNodeBalance(final int nodeValue) {
			return nodeValue & 3;
		}

		/** Diese Methode gibt die Position des gegebenen Knoten zurück.
		 *
		 * @param nodeValue Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return Knotenposition zum Zugriff auf Eigenschaften des Knoten als Index in {@link #keys}, {@link #prevs} oder {@link #nexts}. */
		static int getNodePosition(final int nodeValue) {
			return nodeValue >>> 2;
		}

		/** Diese Methode gibt die inversen Balanz zur gegebenen zurück.<br>
		 * Diese sollte vor seiner Verwendung noch maskiert werden, bspw. mit {@code & 1} oder {@code & 2}.
		 *
		 * @param nodeBalance Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return inverse Knotenbalanz mit {@code 0} und {@code 4} für gleichgewicht, {@code 2} für rechtslastig und {@code 1} für linkslastig. */
		static int getInverseBalance(final int nodeBalance) {
			return 4 >> nodeBalance;
		}

		/** Diese Methode führt die Rechtsrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodePosition {@link #getNodePosition(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		static int rotateToNextImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
			// balance(node) = -2 & balance(node.prev) = -1
			final int nodePrevPosition = TreeData.getNodePosition(prevs[nodePosition]);
			prevs[nodePosition] = nexts[nodePrevPosition];
			nexts[nodePrevPosition] = (nodePosition << 2) | 0;
			return nodePrevPosition;
		}

		/** Diese Methode führt die Rechtslinksrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodePosition {@link #getNodePosition(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		static int rotateToNextPrevImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
			// balance(node) = +1 & balance(node.next) = -1
			final int nodeNextPosition = TreeData.getNodePosition(nexts[nodePosition]);
			final int nodeNextPrevValue = prevs[nodeNextPosition];
			final int nodeNextPrevPosition = TreeData.getNodePosition(nodeNextPrevValue);
			final int nodeNextPrevInverseBalance = TreeData.getInverseBalance(TreeData.getNodeBalance(nodeNextPrevValue));
			prevs[nodeNextPosition] = nexts[nodeNextPrevPosition];
			nexts[nodeNextPrevPosition] = TreeData.getNodeValue(nodeNextPosition, nodeNextPrevInverseBalance & 1);
			nexts[nodePosition] = prevs[nodeNextPrevPosition];
			prevs[nodeNextPrevPosition] = TreeData.getNodeValue(nodePosition, nodeNextPrevInverseBalance & 2);
			return nodeNextPrevPosition;
		}

		/** Diese Methode führt die Linksrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodePosition {@link #getNodePosition(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		static int rotateToPrevImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
			// balance(node) = +1 & balance(node.next) = +1
			final int nodeNextPosition = TreeData.getNodePosition(nexts[nodePosition]);
			nexts[nodePosition] = prevs[nodeNextPosition];
			prevs[nodeNextPosition] = TreeData.getNodeValue(nodePosition, 0);
			return nodeNextPosition;
		}

		/** Diese Methode führt die Linksrechtsrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodePosition {@link #getNodePosition(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		static int rotateToPrevNextImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
			// balance(node) = -1 & balance(node.prev) = +1
			final int nodePrevPosition = TreeData.getNodePosition(prevs[nodePosition]);
			final int nodePrevNextValue = nexts[nodePrevPosition];
			final int nodePrevNextPosition = TreeData.getNodePosition(nodePrevNextValue);
			final int nodeNextPrevInverseBalance = TreeData.getInverseBalance(TreeData.getNodeBalance(nodePrevNextValue));
			nexts[nodePrevPosition] = prevs[nodePrevNextPosition];
			prevs[nodePrevNextPosition] = TreeData.getNodeValue(nodePrevPosition, nodeNextPrevInverseBalance & 2);
			prevs[nodePosition] = nexts[nodePrevNextPosition];
			nexts[nodePrevNextPosition] = TreeData.getNodeValue(nodePosition, nodeNextPrevInverseBalance & 1);
			return nodePrevNextPosition;
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
			return 0; // this.popIndexImpl(_key, this.nexts, this.keys.length);
		}

		// final int popIndexImpl(final Object _key, final int[] parentArray, final int parentIndex) {
		// final int _itemIndex = parentArray[0];
		// if (!_itemIndex) return false;
		// final int[] _prevArray = this.prevs;
		// final int[] _nextArray = this.nexts;
		// int* _levelArray = this._levels_;
		// int _compare = KEY_POLICY::compareItem(&_key, this.keys + _itemIndex);
		// if (!_compare) {
		// final int _prevIndex = _prevArray[_itemIndex];
		// final int _nextIndex = _nextArray[_itemIndex];
		// if (_nextIndex) {
		// if (_prevIndex) {
		// this._movIndex_TREE_(_prevIndex, _nextArray + _itemIndex);
		// }
		// parentArray[0] = _nextIndex;
		// } else {
		// parentArray[0] = _prevIndex;
		// }
		// _nextArray[_itemIndex] = this.entry;
		// this.entry = _itemIndex;
		// this.count--;
		// KEY_POLICY::resetItem(_keyArray_ + _itemIndex);
		// VALUE_POLICY::resetItem(this.values + _itemIndex);
		// return true;
		// }
		// final bool _result = _compare < 0 ? this._popIndex_TREE_(_key, _prevArray + _itemIndex) : this._popIndex_TREE_(_key, _nextArray + _itemIndex);
		// this._calcShape_TREE_(parentArray, _prevArray, _nextArray, _levelArray);
		// return _result;
		// }
		//
		// GKey _lowerKey_TREE_(final GKey _key) {
		// final int _itemIndex = this.lowerIndexImpl(_key);
		// return _itemIndex ? this.keys + _itemIndex : 0;
		// }
		//
		// protected final int lowerIndexImpl(final GKey key) {
		// return this._nearIndex_TREE_(key, true);
		// }
		//
		// protected final GKey lowestKeyImpl() {
		// final int itemIndex = this.lowestIndexImpl();
		// return itemIndex < 0 ? null : (GKey)this.keys[itemIndex];
		// }
		//
		// TreeMap<K, V> d;
		//
		// protected final int lowestIndexImpl() {
		// return this.lastIndexImpl(this.nexts[this.keys.length], this.prevs);
		// }
		//
		// GKey _higherKey_TREE_(final GKey _key) {
		// final int _itemIndex = this._higherIndex_TREE_(_key);
		// return _itemIndex ? this.keys + _itemIndex : 0;
		// }
		//
		// int _higherIndex_TREE_(final GKey _key) {
		// return this._nearIndex_TREE_(_key, false);
		// }
		//
		// GKey _highestKey_TREE_() {
		// final int _itemIndex = this.highestIndexImpl();
		// return _itemIndex ? this.keys + _itemIndex : 0;
		// }
		//
		// protected final int highestIndexImpl() {
		// return this.lastIndexImpl(this.nexts[this.keys.length], this.nexts);
		// }
		//
		// final int lastIndexImpl(int itemIndex, final int[] pathArray) {
		// if (itemIndex == -1) return -1;
		// while (true) {
		// itemIndex >>>= 2;
		// final int pathIndex = pathArray[itemIndex];
		// if (pathIndex == -1) return itemIndex;
		// itemIndex = pathIndex;
		// }
		// }
		//
		// int _nearIndex_TREE_(final GKey _key, final boolean nearPrev) {
		// final GKey[] _keyArray = this.keys;
		// final int[] _prevArray = this.prevs;
		// final int[] _nextArray = this.nexts;
		// int _itemIndex = this._null_;
		// int _nearIndex = 0;
		// while (_itemIndex) {
		// final int _compare = this.customCompare(_key, _keyArray[_itemIndex]);
		// if (!_compare) {
		// _itemIndex = nearPrev ? this.lastIndexImpl(_prevArray[_itemIndex], _nextArray) : this.lastIndexImpl(_nextArray[_itemIndex], _prevArray);
		// if (_itemIndex) return _itemIndex;
		// return _nearIndex;
		// }
		// if (_compare < 0) {
		// if (!nearPrev) {
		// _nearIndex = _itemIndex;
		// }
		// _itemIndex = _prevArray[_itemIndex];
		// } else {
		// if (nearPrev) {
		// _nearIndex = _itemIndex;
		// }
		// _itemIndex = _nextArray[_itemIndex];
		// }
		// }
		// return _nearIndex;
		// }
		//
		// /** Diese Klasse definiert die Zustandsdaten des @c SUCCursor dieser @c SUCMapping. */
		// class ITERATOR {
		//
		// /* SUCIterator */
		// bool move(final int _offset) {
		// final OBJECT _object = *_mapping_._object_.get();
		// for (int _count = _offset; _count < 0; ++_count) {
		// if (MAPPING_POLICY::HASH_ENABLED) {
		// _seekPrev_HASH_(_object._hashMask_, _object._hashRootArray_, _object._hashNextArray_);
		// } else {
		// this._seekItem_TREE_(_object._treeNextArray_, _object._treePrevArray_);
		// }
		// }
		// for (int _count = _offset; _count > 0; --_count) {
		// if (MAPPING_POLICY::HASH_ENABLED) {
		// _seekNext_HASH_(_object._hashRootArray_, _object._hashNextArray_);
		// } else {
		// this._seekItem_TREE_(_object._treePrevArray_, _object._treeNextArray_);
		// }
		// }
		// return true;
		// }
		//
		// void moveToFirst() {
		// final OBJECT _object = *_mapping_._object_.get();
		// if (MAPPING_POLICY::HASH_ENABLED) {
		// _hashRootIndex_ = _object._hashMask_ + 1;
		// _seekNext_HASH_ROOT_(_object._hashRootArray_, _object._hashNextArray_);
		// } else {
		// this._treeRootIndex_ = 0;
		// this._treeRootArray_[0] = 0;
		// final int _itemIndex = _object._treeRoot_;
		// if (_itemIndex) {
		// this._seekItem_TREE_ITEM_(_itemIndex, 0, this._treeRootArray_, _object._treePrevArray_);
		// }
		// }
		//
		// }
		//
		// void moveToLast() {
		// final OBJECT _object = *_mapping_._object_.get();
		// if (MAPPING_POLICY::HASH_ENABLED) {
		// _hashRootIndex_ = -1;
		// _seekPrev_HASH_ROOT_(_object._hashMask_, _object._hashRootArray_, _object._hashNextArray_);
		// } else {
		// this._treeRootIndex_ = 0;
		// this._treeRootArray_[0] = 0;
		// final int _itemIndex = _object._treeRoot_;
		// if (_itemIndex) {
		// this._seekItem_TREE_ITEM_(_itemIndex, 0, this._treeRootArray_, _object._treeNextArray_);
		// }
		// }
		// }
		//
		// void _seekItem_TREE_(final int[] _prevArray, final int[] _nextArray) {
		// final int _rootIndex = this._treeRootIndex_;
		// final int[] _rootArray = this._treeRootArray_;
		// final int _itemIndex = _rootArray[_rootIndex];
		// if (!_itemIndex) return;
		// final int _nextIndex = _nextArray[_itemIndex];
		// if (_nextIndex) {
		// this._seekItem_TREE_ITEM_(_nextIndex, _rootIndex, _rootArray, _prevArray);
		// } else {
		// this._seekItem_TREE_ROOT_(_itemIndex, _rootIndex, _rootArray, _prevArray, _nextArray);
		// }
		// }
		//
		// void _seekItem_TREE_ROOT_(int _itemIndex, int _rootIndex, final int[] _rootArray, final int[] _prevArray, final int[] _nextArray) {
		// int _gotoIndex;
		// while (true) {
		// --_rootIndex;
		// _gotoIndex = _rootArray[_rootIndex];
		// if (!_gotoIndex) {
		// break;
		// }
		// if (_itemIndex == _prevArray[_gotoIndex]) {
		// break;
		// }
		// _itemIndex = _gotoIndex;
		// }
		// _value_._index_ = _gotoIndex;
		// this._treeRootIndex_ = _rootIndex;
		// }
		//
		// void _seekItem_TREE_ITEM_(int _itemIndex, int _rootIndex, final int[] _rootArray, final int[] _prevArray) {
		// while (true) {
		// ++_rootIndex;
		// _rootArray[_rootIndex] = _itemIndex;
		// final int _gotoIndex = _prevArray[_itemIndex];
		// if (!_gotoIndex) {
		// break;
		// }
		// _itemIndex = _gotoIndex;
		// }
		// _value_._index_ = _itemIndex;
		// this._treeRootIndex_ = _rootIndex;
		// }
		//
		// int _treeRootIndex_;
		//
		// int[] _treeRootArray_ = new int[32];
		//
		// };

		protected int customCompare(final Object thatKey, final Object thisKey) {
			return Comparators.compare((Comparable)thisKey, (Comparable)thatKey);
		}

	}
}