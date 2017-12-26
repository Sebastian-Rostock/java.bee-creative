package bee.creative.util;

import java.nio.channels.NetworkChannel;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import bee.creative.iam.IAMMapping;
import bee.creative.util.HashData.Entries;
import bee.creative.util.HashData.EntriesIterator;
import bee.creative.util.HashData.HashEntry;
import bee.creative.util.HashData.HashIterator;
import bee.creative.util.HashData.Keys;
import bee.creative.util.HashData.KeysIterator;
import bee.creative.util.HashData.Mapping;
import bee.creative.util.HashData.Values;
import bee.creative.util.HashData.ValuesIterator;

// TODO CURSOR = int, das in prevs/next zeigt, wo der Index des gesuchten Eintrags drin steht
// nagativ, wenn prevs, positiv, wenn nexts gemeint
// 0= wenn undefiniert, 1... in nexts, ...-1 in ptevs

class _TreeData_ {

	// Die Algorithmen zur Balanzierung sind dem "Skriptum Informatik - eine konventionelle Einführung" von Hans-Jürgen Appelrath und Jochen Ludewig entnommen.
	static class TreeData<GKey, GValue> {

		@SuppressWarnings ("javadoc")
		protected static class TreeEntry<GKey, GValue> implements Entry<GKey, GValue> {

			protected final TreeData<GKey, GValue> entryData;

			protected final int entryIndex;

			public TreeEntry(final TreeData<GKey, GValue> entryData, final int entryIndex) {
				this.entryData = entryData;
				this.entryIndex = entryIndex;
			}

			{}

			@Override
			public GKey getKey() {
				return this.entryData.getKeyImpl(this.entryIndex);
			}

			@Override
			public GValue getValue() {
				return this.entryData.getValueImpl(this.entryIndex);
			}

			@Override
			public GValue setValue(final GValue value) {
				return this.entryData.putValueImpl(this.entryIndex, value);
			}

			@Override
			public int hashCode() {
				return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
			}

			@Override
			public boolean equals(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> that = (Entry<?, ?>)object;
				return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
			}

			@Override
			public String toString() {
				return this.getKey() + "=" + this.getValue();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static abstract class TreeIterator<GKey, GValue, GItem> implements Iterator<GItem> {

			protected final TreeData<GKey, GValue> entryData;

			/** Dieses Feld speichert den Index des nächsten Eintrags in {@link HashData#keys}. */
			protected int nextIndex;

			/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link HashData#keys}. */
			protected int entryIndex = -1;

			public TreeIterator(final TreeData<GKey, GValue> entryData) {
				this.entryData = entryData;
			}

			/** Diese Methode gibt den nächsten Schlüssel zurück. */
			protected final GKey nextKey() {
				return this.entryData.getKeyImpl(this.nextIndex());
			}

			/** Diese Methode gibt den nächsten Eintrag zurück. */
			protected final TreeEntry<GKey, GValue> nextEntry() {
				return new TreeEntry<>(this.entryData, this.nextIndex());
			}

			/** Diese Methode gibt den nächsten Wert zurück. */
			protected final GValue nextValue() {
				return this.entryData.getValueImpl(this.nextIndex());
			}

			/** Diese Methode ermitteln den Index des nächsten Eintrags und gibt den des aktuellen zurück. */
			protected final int nextIndex() {
				return getNodePosition(nextNode());
			}

			protected final int nextNode() {
				// TODO
				return 0;
			}
			
			{}

			@Override
			public boolean hasNext() {
				return this.nextIndex >= 0;
			}

			@Override
			public void remove() {
				// if (this.entryData.popEntryImpl(this.tableIndex, this.entryIndex)) return;
				throw new IllegalStateException();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class Keys<GKey> extends AbstractSet<GKey> {

			protected final TreeData<GKey, ?> entryData;

			public Keys(final TreeData<GKey, ?> entryData) {
				this.entryData = entryData;
			}

			{}

			@Override
			public int size() {
				return this.entryData.count;
			}

			@Override
			public void clear() {
				this.entryData.clearEntries();
			}

			@Override
			public Iterator<GKey> iterator() {
				return this.entryData.newKeysIteratorImpl();
			}

			@Override
			public boolean remove(final Object item) {
				return this.entryData.popKeyImpl(item);
			}

			@Override
			public boolean contains(final Object item) {
				return this.entryData.hasKeyImpl(item);
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class KeysIterator<GKey, GValue> extends TreeIterator<GKey, GValue, GKey> {

			public KeysIterator(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public GKey next() {
				return this.nextKey();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class Values<GValue> extends AbstractCollection<GValue> {

			protected final TreeData<?, GValue> entryData;

			public Values(final TreeData<?, GValue> entryData) {
				this.entryData = entryData;
			}

			{}

			@Override
			public int size() {
				return this.entryData.count;
			}

			@Override
			public void clear() {
				this.entryData.clearEntries();
			}

			@Override
			public Iterator<GValue> iterator() {
				return this.entryData.newValuesIteratorImpl();
			}

			@Override
			public boolean remove(final Object o) {
				return this.entryData.popValueImpl(o);
			}

			@Override
			public boolean contains(final Object o) {
				return this.entryData.hasValueImpl(o);
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class ValuesIterator<GKey, GValue> extends TreeIterator<GKey, GValue, GValue> {

			public ValuesIterator(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public GValue next() {
				return this.nextValue();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class Entries<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

			protected final TreeData<GKey, GValue> entryData;

			public Entries(final TreeData<GKey, GValue> entryData) {
				this.entryData = entryData;
			}

			{}

			@Override
			public int size() {
				return this.entryData.count;
			}

			@Override
			public void clear() {
				this.entryData.clearEntries();
			}

			@Override
			public Iterator<Entry<GKey, GValue>> iterator() {
				return this.entryData.newEntriesIteratorImpl();
			}

			@Override
			public boolean remove(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				return this.entryData.popEntryImpl(entry.getKey(), entry.getValue());
			}

			@Override
			public boolean contains(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				return this.entryData.hasEntryImpl(entry.getKey(), entry.getValue());
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class EntriesIterator<GKey, GValue> extends TreeIterator<GKey, GValue, Entry<GKey, GValue>> {

			public EntriesIterator(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public Entry<GKey, GValue> next() {
				return this.nextEntry();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class Mapping<GKey, GValue> extends AbstractMap<GKey, GValue> {

			protected final TreeData<GKey, GValue> entryData;

			public Mapping(final TreeData<GKey, GValue> entryData) {
				this.entryData = entryData;
			}

			{}

			@Override
			public int size() {
				return this.entryData.count;
			}

			@Override
			public void clear() {
				this.entryData.clearEntries();
			}

			@Override
			public boolean isEmpty() {
				return this.entryData.count == 0;
			}

			@Override
			public boolean containsKey(final Object key) {
				return this.entryData.hasKeyImpl(key);
			}

			@Override
			public boolean containsValue(final Object value) {
				return this.values().contains(value);
			}

			@Override
			public GValue get(final Object key) {
				return this.entryData.getImpl(key);
			}

			@Override
			public Set<GKey> keySet() {
				return this.entryData.newKeysImpl();
			}

			@Override
			public GValue put(final GKey key, final GValue value) {
				return this.entryData.putImpl(key, value);
			}

			@Override
			public GValue remove(final Object key) {
				return this.entryData.popImpl(key);
			}

			@Override
			public Values<GValue> values() {
				return this.entryData.newValuesImpl();
			}

			@Override
			public Entries<GKey, GValue> entrySet() {
				return this.entryData.newEntriesImpl();
			}

		}

		{}

		/** Dieses Feld speichert den initialwert für {@link #prevs} und {@link #nexts}. */
		private static final int[] EMPTY_INTEGERS = {-1};

		/** Dieses Feld speichert den initialwert für {@link #keys} und {@link #values}. */
		private static final Object[] EMPTY_OBJECTS = {};

		/** Dieses Feld speichert die maximale Kapazität. */
		private static final int MAX_CAPACITY = 0x40000000;

		{}

		/** Diese Methode gibt den Wert des Knoten mit den gegebenen Eigenschaften zurück.
		 * 
		 * @param nodeIndex Knotenposition.
		 * @param nodeBalance Knotenbalanz mit {@code 0} für gleichgewicht, {@code 1} für rechtslastig und {@code 2} für linkslastig.
		 * @return Knotenwert als Element aus {@link #prevs} oder {@link #nexts}. */
		private static int getNodeValue(final int nodeIndex, final int nodeBalance) {
			return (nodeIndex << 2) | nodeBalance;
		}

		public boolean popEntryImpl(Object key, Object value) {
			// TODO
			return false;
		}

		/** Diese Methode gibt die Balanz des gegebenen Knoten zurück.
		 *
		 * @param nodeValue Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return Knotenbalanz mit {@code 0} und {@code 3} für gleichgewicht, {@code 1} für rechtslastig und {@code 2} für linkslastig. */
		private static int getNodeBalance(final int nodeValue) {
			return nodeValue & 3;
		}

		/** Diese Methode gibt die Position des gegebenen Knoten zurück.
		 *
		 * @param nodeValue Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return Knotenposition zum Zugriff auf Eigenschaften des Knoten als Index in {@link #keys}, {@link #prevs} oder {@link #nexts}. */
		private static int getNodePosition(final int nodeValue) {
			return nodeValue >>> 2;
		}

		/** Diese Methode gibt die inversen Balanz zur gegebenen zurück.<br>
		 * Diese sollte vor seiner Verwendung noch maskiert werden, bspw. mit {@code & 1} oder {@code & 2}.
		 *
		 * @param nodeBalance Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return inverse Knotenbalanz mit {@code 0} und {@code 4} für gleichgewicht, {@code 2} für rechtslastig und {@code 1} für linkslastig. */
		private static int getInverseBalance(final int nodeBalance) {
			return 4 >> nodeBalance;
		}

		/** Diese Methode führt die Rechtsrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodePosition {@link #getNodePosition(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		private static int rotateRightImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
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
		private static int rotateRightLeftImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
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
		private static int rotateLeftImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
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
		private static int rotateLeftRightImpl(final int[] prevs, final int[] nexts, final int nodePosition) {
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

		@SuppressWarnings ("javadoc")
		private static void clearNexts(final int[] array) {
			for (int i = 0, size = array.length; i < size;) {
				array[i] = ++i;
			}
		}

		@SuppressWarnings ("javadoc")
		private static void clearPrevs(final int[] array) {
			Arrays.fill(array, -1);
		}

		@SuppressWarnings ("javadoc")
		private static void clearObjects(final Object[] array) {
			if (array == null) return;
			Arrays.fill(array, null);
		}

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
		int count = 0;

		public TreeData(final boolean withValues) {
			this.values = withValues ? TreeData.EMPTY_OBJECTS : null;
		}

		{}

		/** Diese Methode gibt die Anzahl der Einträge zurück.
		 *
		 * @return Anzahl der Einträge. */
		protected final int countImpl() {
			return this.count;
		}

		/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
		 *
		 * @return Kapazität. */
		protected final int capacityImpl() {
			return this.keys.length;
		}

		protected final void allocateImpl(final int capacity) throws IllegalArgumentException {
			if (capacity < this.count) throw new IllegalArgumentException();
			final Object[] oldKeys = this.keys;
			if (oldKeys.length == capacity) return;
			if (capacity == 0) {
				this.entry = 0;
				this.keys = EMPTY_OBJECTS;
				this.nexts = EMPTY_INTEGERS;
				this.prevs = EMPTY_INTEGERS;
				this.values = this.values != null ? EMPTY_OBJECTS : null;
			} else if (capacity <= MAX_CAPACITY) {
				final int[] oldNexts = this.nexts;
				final int[] newNexts = new int[capacity + 1];
				final int[] oldPrevs = this.prevs;
				final int[] newPrevs = new int[capacity];
				final Object[] newKeys = new Object[capacity];
				final Object[] oldValues = this.values;
				final Object[] newValues = oldValues != null ? new Object[capacity] : null;
				clearNexts(newNexts);
				clearPrevs(newPrevs);

				int newEntry = 0;
				newNexts[capacity] = allocateImpl2(oldNexts, newNexts, oldPrevs, newPrevs, oldKeys, newKeys, oldNexts[oldKeys.length], 0);

				this.entry = newEntry;
				this.keys = newKeys;
				this.prevs = newPrevs;
				this.nexts = newNexts;
				this.values = newValues;
			} else throw new OutOfMemoryError();
		}

		private int allocateImpl2(int[] oldNexts, int[] newNexts, int[] oldPrevs, int[] newPrevs, Object[] oldKeys, Object[] newKeys,
// TODO
			int oldNodeValue, int newNodePosition) {

			if (oldNodeValue == -1) return newNodePosition; // nächste freie zelle liefern
			int oldNodeBalance = getNodeBalance(oldNodeValue);
			int oldNodePosition = getNodePosition(oldNodeValue);

			newKeys[newNodePosition] = oldKeys[oldNodePosition];

			int newPrevValue = allocateImpl2(oldNexts, newNexts, oldPrevs, newPrevs, oldKeys, newKeys, oldNexts[oldNodePosition]

				, //
				allocateImpl2(oldNexts, newNexts, oldPrevs, newPrevs, oldKeys, newKeys, //
					oldPrevs[oldNodePosition], //
					newNodePosition + 1));

			return getNodeValue(newNodePosition, oldNodeBalance);
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
			this.allocateImpl((allocate < 0) || (allocate > TreeData.MAX_CAPACITY) ? TreeData.MAX_CAPACITY : allocate);
			return this.putIndexImpl2(key, this.nexts, this.capacityImpl()) & 0x7FFFFFFF;
		}

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
							? TreeData.rotateRightImpl(prevs, nexts, nodePosition2) //
							: TreeData.rotateLeftRightImpl(prevs, nexts, nodePosition2), //
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
							? TreeData.rotateLeftImpl(prevs, nexts, nodePosition2) //
							: TreeData.rotateRightLeftImpl(prevs, nexts, nodePosition2), //
						0);
					return result & 0x7FFFFFFF;
				}
				fields[fieldIndex] = TreeData.getNodeValue(nodePosition2, 1);
				return result;
			}
			return nodePosition; // positiv, da Teilbaum unverändert
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

		/** Diese Methode gibt das {@link Set} der Schlüssel in {@link #keys} zurück.
		 *
		 * @return Schlüssel. */
		protected final Keys<GKey> newKeysImpl() {
			return new Keys<>(this);
		}

		/** Diese Methode gibt den {@link Iterator} über die Schlüssel in {@link #keys} zurück.
		 *
		 * @return Interator für {@link #newKeysImpl()}. */
		protected final KeysIterator<GKey, GValue> newKeysIteratorImpl() {
			return new KeysIterator<>(this);
		}

		/** Diese Methode gibt die {@link Collection} der Werte in {@link #values} zurück.
		 *
		 * @return Werte. */
		protected final Values<GValue> newValuesImpl() {
			return new Values<>(this);
		}

		/** Diese Methode gibt den {@link Iterator} über die Werte in {@link #values} zurück.
		 *
		 * @return Interator für {@link #newValuesImpl()}. */
		protected final ValuesIterator<GKey, GValue> newValuesIteratorImpl() {
			return new ValuesIterator<>(this);
		}

		/** Diese Methode gibt das {@link Set} der Einträge zurück.
		 *
		 * @return Einträge. */
		protected final Entries<GKey, GValue> newEntriesImpl() {
			return new Entries<>(this);
		}

		/** Diese Methode gibt den {@link Iterator} über die Einträge zurück.
		 *
		 * @return Interator für {@link #newEntriesImpl()}. */
		protected final EntriesIterator<GKey, GValue> newEntriesIteratorImpl() {
			return new EntriesIterator<>(this);
		}

		/** Diese Methode gibt die {@link Map} zu den Schlüsseln in {@link #keys} und Werten in {@link #values} zurück.
		 *
		 * @return Abbildung. */
		protected final Mapping<GKey, GValue> newMappingImpl() {
			return new Mapping<>(this);
		}

		/** Diese Methode entfernt alle Einträge. Hierbei werden die Anzahl der Einträge auf {@code 0} gesetzt und die Tabellen initialisiert. */
		protected final void clearEntries() {
			if (this.count == 0) return;
			clearPrevs(this.prevs);
			clearNexts(this.nexts);
			clearObjects(this.keys);
			clearObjects(this.values);
			this.count = 0;
		}

		/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Schlüssel kleienr als, gleich bzw. größer als der
		 * zweite Schlüssel ist.
		 *
		 * @see Comparators#compare(Comparable, Comparable)
		 * @param thisKey alter bzw. verwalteter Schlüssel oder {@code null}.
		 * @param thatKey neuer bzw. gesuchter Schlüssel oder {@code null}.
		 * @return Vergleichswert der gegebenen Schlüssel. */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		protected int customCompare(final Object thatKey, final Object thisKey) {
			return Comparators.compare((Comparable)thisKey, (Comparable)thatKey);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected Object clone() throws CloneNotSupportedException {
			try {
				final TreeData<?, ?> result = (TreeData<?, ?>)super.clone();
				if (this.capacityImpl() == 0) {
					result.keys = TreeData.EMPTY_OBJECTS;
					result.nexts = TreeData.EMPTY_INTEGERS;
					result.prevs = TreeData.EMPTY_INTEGERS;
					result.values = result.values != null ? TreeData.EMPTY_OBJECTS : null;
				} else {
					result.keys = this.keys.clone();
					result.nexts = this.nexts.clone();
					result.prevs = this.prevs.clone();
					result.values = result.values == null ? null : this.values.clone();
				}
				return result;
			} catch (final Exception cause) {
				throw new CloneNotSupportedException(cause.getMessage());
			}
		}

	}
}