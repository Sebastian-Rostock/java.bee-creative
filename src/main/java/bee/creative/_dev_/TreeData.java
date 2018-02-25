package bee.creative._dev_;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import bee.creative.util.Comparators;
import bee.creative.util.Objects;

// TODO CURSOR = int, das in prevs/next zeigt, wo der Index des gesuchten Eintrags drin steht
// nagativ, wenn prevs, positiv, wenn nexts gemeint
// 0= wenn undefiniert, 1... in nexts, ...-1 in ptevs


	// Die Algorithmen zur Balanzierung sind dem "Skriptum Informatik - eine konventionelle Einführung" von Hans-Jürgen Appelrath und Jochen Ludewig entnommen.
	class TreeData<GKey, GValue> {

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

			/** Dieses Feld speichert den Index des nächsten Eintrags in {@link XX_HashData#keys}. */
			protected int nodeIndex;

			/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link XX_HashData#keys}. */
			protected int entryIndex = -1;

			public TreeIterator(final TreeData<GKey, GValue> entryData) {
				this.entryData = entryData;
			}

			/** Diese Methode gibt den vorherigen Schlüssel zurück. */
			protected final GKey prevKey() {
				return this.entryData.getKeyImpl(this.prevIndex());
			}

			/** Diese Methode gibt den vorherigen Eintrag zurück. */
			protected final TreeEntry<GKey, GValue> prevEntry() {
				return new TreeEntry<>(this.entryData, this.prevIndex());
			}

			/** Diese Methode gibt den vorherigen Wert zurück. */
			protected final GValue prevValue() {
				return this.entryData.getValueImpl(this.prevIndex());
			}

			/** Diese Methode ermitteln den Index des vorherigen Eintrags und gibt den des aktuellen zurück. */
			protected final int prevIndex() {
				return TreeData.getNodeIndex(this.prevNode());
			}

			protected final int prevNode() {
				// TODO
				return 0;
			}

			protected final void prevRemove() {
				throw new UnsupportedOperationException();
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
				return TreeData.getNodeIndex(this.nextNode());
			}

			protected final int nextNode() {
				// TODO
				return 0;
			}

			protected final void nextRemove() {
				throw new UnsupportedOperationException();
			}

			{}

			@Override
			public boolean hasNext() {
				return this.nodeIndex >= 0;
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class Keys<GKey> extends AbstractSet<GKey> implements NavigableSet<GKey> {

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
				this.entryData.clearImpl();
			}

			@Override
			public Iterator<GKey> iterator() {
				return this.entryData.newKeysIteratorAImpl();
			}

			@Override
			public boolean remove(final Object item) {
				return this.entryData.popKeyImpl(item);
			}

			@Override
			public boolean contains(final Object item) {
				return this.entryData.hasKeyImpl(item);
			}

			@Override
			public Comparator<? super GKey> comparator() {
				return null;
			}

			@Override
			public GKey first() {
				return null;
			}

			@Override
			public GKey last() {
				return null;
			}

			@Override
			public GKey lower(final GKey e) {
				return null;
			}

			@Override
			public GKey floor(final GKey e) {
				return null;
			}

			@Override
			public GKey ceiling(final GKey e) {
				return null;
			}

			@Override
			public GKey higher(final GKey e) {
				return null;
			}

			@Override
			public GKey pollFirst() {
				return null;
			}

			@Override
			public GKey pollLast() {
				return null;
			}

			@Override
			public NavigableSet<GKey> descendingSet() {
				return null;
			}

			@Override
			public Iterator<GKey> descendingIterator() {
				return null;
			}

			@Override
			public NavigableSet<GKey> subSet(final GKey fromElement, final boolean fromInclusive, final GKey toElement, final boolean toInclusive) {
				return null;
			}

			@Override
			public NavigableSet<GKey> headSet(final GKey toElement, final boolean inclusive) {
				return null;
			}

			@Override
			public NavigableSet<GKey> tailSet(final GKey fromElement, final boolean inclusive) {
				return null;
			}

			@Override
			public SortedSet<GKey> subSet(final GKey fromElement, final GKey toElement) {
				return null;
			}

			@Override
			public SortedSet<GKey> headSet(final GKey toElement) {
				return null;
			}

			@Override
			public SortedSet<GKey> tailSet(final GKey fromElement) {
				return null;
			}

		}

		protected static class KeysA<GKey> extends Keys<GKey> {

			public KeysA(final TreeData<GKey, ?> entryData) {
				super(entryData);
			}

		}

		protected static class KeysD<GKey> extends Keys<GKey> {

			public KeysD(final TreeData<GKey, ?> entryData) {
				super(entryData);
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class KeysIteratorA<GKey, GValue> extends TreeIterator<GKey, GValue, GKey> {

			public KeysIteratorA(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public GKey next() {
				return this.nextKey();
			}

			@Override
			public void remove() {
				this.nextRemove();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class KeysIteratorD<GKey, GValue> extends TreeIterator<GKey, GValue, GKey> {

			public KeysIteratorD(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public GKey next() {
				return this.prevKey();
			}

			@Override
			public void remove() {
				this.prevRemove();
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
				this.entryData.clearImpl();
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
		protected static class ValuesIteratorA<GKey, GValue> extends TreeIterator<GKey, GValue, GValue> {

			public ValuesIteratorA(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public GValue next() {
				return this.nextValue();
			}

			@Override
			public void remove() {
				this.nextRemove();
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
				this.entryData.clearImpl();
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
		protected static class EntriesIteratorA<GKey, GValue> extends TreeIterator<GKey, GValue, Entry<GKey, GValue>> {

			public EntriesIteratorA(final TreeData<GKey, GValue> entryData) {
				super(entryData);
			}

			{}

			@Override
			public Entry<GKey, GValue> next() {
				return this.nextEntry();
			}

			@Override
			public void remove() {
				this.nextRemove();
			}

		}

		@SuppressWarnings ("javadoc")
		protected static class Mapping<GKey, GValue> extends AbstractMap<GKey, GValue> implements NavigableMap<GKey, GValue> {

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
				this.entryData.clearImpl();
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
				return this.entryData.newKeysAImpl();
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
				return this.entryData.newValuesAImpl();
			}

			@Override
			public Entries<GKey, GValue> entrySet() {
				return this.entryData.newEntriesAImpl();
			}

			@Override
			public Comparator<? super GKey> comparator() {
				return null;
			}

			@Override
			public GKey firstKey() {
				return null;
			}

			@Override
			public GKey lastKey() {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> lowerEntry(final GKey key) {
				return null;
			}

			@Override
			public GKey lowerKey(final GKey key) {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> floorEntry(final GKey key) {
				return null;
			}

			@Override
			public GKey floorKey(final GKey key) {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> ceilingEntry(final GKey key) {
				return null;
			}

			@Override
			public GKey ceilingKey(final GKey key) {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> higherEntry(final GKey key) {
				return null;
			}

			@Override
			public GKey higherKey(final GKey key) {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> firstEntry() {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> lastEntry() {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> pollFirstEntry() {
				return null;
			}

			@Override
			public java.util.Map.Entry<GKey, GValue> pollLastEntry() {
				return null;
			}

			@Override
			public NavigableMap<GKey, GValue> descendingMap() {
				return null;
			}

			@Override
			public NavigableSet<GKey> navigableKeySet() {
				return null;
			}

			@Override
			public NavigableSet<GKey> descendingKeySet() {
				return null;
			}

			@Override
			public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey, final boolean toInclusive) {
				return null;
			}

			@Override
			public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
				return null;
			}

			@Override
			public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
				return null;
			}

			@Override
			public SortedMap<GKey, GValue> subMap(final GKey fromKey, final GKey toKey) {
				return null;
			}

			@Override
			public SortedMap<GKey, GValue> headMap(final GKey toKey) {
				return null;
			}

			@Override
			public SortedMap<GKey, GValue> tailMap(final GKey fromKey) {
				return null;
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

		/** Diese Methode gibt die Position des gegebenen Knoten zurück.
		 *
		 * @param nodeValue Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return Knotenposition zum Zugriff auf Eigenschaften des Knoten als Index in {@link #keys}, {@link #prevs} oder {@link #nexts}. */
		private static int getNodeIndex(final int nodeValue) {
			return nodeValue >>> 2;
		}

		/** Diese Methode gibt die Balanz des gegebenen Knoten zurück.
		 *
		 * @param nodeValue Knotenwert als Element aus {@link #prevs} oder {@link #nexts}.
		 * @return Knotenbalanz mit {@code 0} und {@code 3} für gleichgewicht, {@code 1} für rechtslastig und {@code 2} für linkslastig. */
		private static int getNodeBalance(final int nodeValue) {
			return nodeValue & 3;
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
		 * @param nodeIndex {@link #getNodeIndex(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		private static int rotateRightImpl(final int[] prevs, final int[] nexts, final int nodeIndex) {
			// balance(node) = -2 & balance(node.prev) = -1
			final int nodePrevIndex = TreeData.getNodeIndex(prevs[nodeIndex]);
			prevs[nodeIndex] = nexts[nodePrevIndex];
			nexts[nodePrevIndex] = (nodeIndex << 2) | 0;
			return nodePrevIndex;
		}

		/** Diese Methode führt die Rechtslinksrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodeIndex {@link #getNodeIndex(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		private static int rotateRightLeftImpl(final int[] prevs, final int[] nexts, final int nodeIndex) {
			// balance(node) = +1 & balance(node.next) = -1
			final int nodeNextIndex = TreeData.getNodeIndex(nexts[nodeIndex]);
			final int nodeNextPrevValue = prevs[nodeNextIndex];
			final int nodeNextPrevIndex = TreeData.getNodeIndex(nodeNextPrevValue);
			final int nodeNextPrevInverseBalance = TreeData.getInverseBalance(TreeData.getNodeBalance(nodeNextPrevValue));
			prevs[nodeNextIndex] = nexts[nodeNextPrevIndex];
			nexts[nodeNextPrevIndex] = TreeData.getNodeValue(nodeNextIndex, nodeNextPrevInverseBalance & 1);
			nexts[nodeIndex] = prevs[nodeNextPrevIndex];
			prevs[nodeNextPrevIndex] = TreeData.getNodeValue(nodeIndex, nodeNextPrevInverseBalance & 2);
			return nodeNextPrevIndex;
		}

		/** Diese Methode führt die Linksrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodeIndex {@link #getNodeIndex(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		private static int rotateLeftImpl(final int[] prevs, final int[] nexts, final int nodeIndex) {
			// balance(node) = +1 & balance(node.next) = +1
			final int nodeNextIndex = TreeData.getNodeIndex(nexts[nodeIndex]);
			nexts[nodeIndex] = prevs[nodeNextIndex];
			prevs[nodeNextIndex] = TreeData.getNodeValue(nodeIndex, 0);
			return nodeNextIndex;
		}

		/** Diese Methode führt die Linksrechtsrotation des gegebenen Knoten durch und gibt den Index des diesen ersetzenden Knoten zurück.
		 *
		 * @param prevs {@link #prevs}.
		 * @param nexts {@link #nexts}.
		 * @param nodeIndex {@link #getNodeIndex(int) Index} des Knoten, der rotiert werden soll.
		 * @return neuer Index des Knoten, der den gegebenen ersetzt. */
		private static int rotateLeftRightImpl(final int[] prevs, final int[] nexts, final int nodeIndex) {
			// balance(node) = -1 & balance(node.prev) = +1
			final int nodePrevIndex = TreeData.getNodeIndex(prevs[nodeIndex]);
			final int nodePrevNextValue = nexts[nodePrevIndex];
			final int nodePrevNextIndex = TreeData.getNodeIndex(nodePrevNextValue);
			final int nodeNextPrevInverseBalance = TreeData.getInverseBalance(TreeData.getNodeBalance(nodePrevNextValue));
			nexts[nodePrevIndex] = prevs[nodePrevNextIndex];
			prevs[nodePrevNextIndex] = TreeData.getNodeValue(nodePrevIndex, nodeNextPrevInverseBalance & 2);
			prevs[nodeIndex] = nexts[nodePrevNextIndex];
			nexts[nodePrevNextIndex] = TreeData.getNodeValue(nodeIndex, nodeNextPrevInverseBalance & 1);
			return nodePrevNextIndex;
		}

		@SuppressWarnings ("javadoc")
		private static void clearNexts(final int[] array) {
			for (int i = array.length - 1; i >= 0; array[i] = --i) {}
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

		/** Diese Methode gibt die Anzahl der aktuell verwaltbaren Einträge zurück.
		 *
		 * @return Kapazität. */
		protected final int capacityImpl() {
			return this.keys.length;
		}

		/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
		 *
		 * @param capacity Anzahl der maximal verwaltbaren Einträge.
		 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
		protected final void allocateImpl(final int capacity) throws IllegalArgumentException {
			final int count = this.count;
			if (capacity < count) throw new IllegalArgumentException();
			final Object[] oldKeys = this.keys;
			if (oldKeys.length == capacity) return;
			if (capacity == 0) {
				this.entry = 0;
				this.keys = TreeData.EMPTY_OBJECTS;
				this.nexts = TreeData.EMPTY_INTEGERS;
				this.prevs = TreeData.EMPTY_INTEGERS;
				this.values = this.values != null ? TreeData.EMPTY_OBJECTS : null;
			} else if (capacity <= TreeData.MAX_CAPACITY) {
				final int[] oldNexts = this.nexts;
				final int[] newNexts = new int[capacity + 1];
				final int[] oldPrevs = this.prevs;
				final int[] newPrevs = new int[capacity];
				final Object[] newKeys = new Object[capacity];
				final Object[] oldValues = this.values;
				final Object[] newValues = oldValues != null ? new Object[capacity] : null;
				TreeData.clearNexts(newNexts);
				TreeData.clearPrevs(newPrevs);
				int newEntry = capacity - 1;
				final int[] nodeValues = new int[32];
				int swapValues = 0, pushValues = 0, levelIndex = 0;
				nodeValues[0] = oldNexts[oldKeys.length];
				while (levelIndex >= 0) {
					final int nodeValue = nodeValues[levelIndex];
					if (~nodeValue == 0) {
						levelIndex--;
					} else {
						final int selectMask = 1 << levelIndex, nodeIndex = TreeData.getNodeIndex(nodeValue);
						if ((pushValues & selectMask) != 0) {
							final int nodeBalance = TreeData.getNodeBalance(nodeValue);
							newPrevs[newEntry] = nodeValues[levelIndex + 1]; // 4. pop newPrev
							newNexts[newEntry] = nodeValues[--levelIndex]; // 5. pop newNext, pop oldNode
							nodeValues[levelIndex] = TreeData.getNodeValue(newEntry, nodeBalance); // 6. push newNode
							newKeys[newEntry] = oldKeys[nodeIndex];
							if (oldValues != null) {
								newValues[newEntry] = oldValues[nodeIndex];
							}
							final int resetMask = ~(-1 << levelIndex);
							swapValues &= resetMask;
							pushValues &= resetMask;
							levelIndex--;
							newEntry--;
						} else if ((swapValues & selectMask) != 0) {
							nodeValues[levelIndex] = nodeValues[++levelIndex]; // 2. swap oldNode and newNext
							nodeValues[levelIndex] = nodeValue;
							nodeValues[++levelIndex] = oldPrevs[nodeIndex]; // 3. push oldPrev
							pushValues |= selectMask << 1;

						} else {
							nodeValues[++levelIndex] = oldNexts[nodeIndex]; // 1. push oldNext
							swapValues |= selectMask;
						}
					}
				}
				newNexts[capacity] = nodeValues[0];
				this.entry = newEntry;
				this.keys = newKeys;
				this.prevs = newPrevs;
				this.nexts = newNexts;
				this.values = newValues;
			} else throw new OutOfMemoryError();
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
			if (~entryIndex == 0) return false;
			entryIndex = TreeData.getNodeIndex(entryIndex);
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
			int nodeValue = nexts[keys.length];
			while (~nodeValue != 0) {
				final int nodeIndex = TreeData.getNodeIndex(nodeValue);
				final int compare = this.customCompare(keys[nodeIndex], key);
				if (compare == 0) return nodeIndex;
				nodeValue = compare < 0 ? prevs[nodeIndex] : nexts[nodeIndex];
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
			if (~nodeValue == 0) { // leres Blatt überschreiben
				final Object[] keys = this.keys;
				final int count = this.count + 1;
				final int result = this.entry;
				if (count > keys.length) throw new OutOfMemoryError();
				this.count = count;
				this.entry = nexts[result];
				keys[result] = key;
				prevs[result] = -1;
				nexts[result] = -1;
				fields[fieldIndex] = TreeData.getNodeValue(result, 0);
				return result | 0x80000000; // negativ, da Teilbaum zu balanziren
			}
			final int nodeIndex = TreeData.getNodeIndex(nodeValue);
			final int compare = this.customCompare(this.keys[nodeIndex], key);
			if (compare < 0) { // negativ, wenn im rechten Teilbaum einzufügen
				final int result = this.putIndexImpl2(key, prevs, nodeIndex);
				if (result >= 0) return result; // positiv, wenn Teilbaum bereits balanziert
				final int nodeValue2 = fields[fieldIndex]; // kann durch einfügen anders sein
				final int nodeBalance2 = TreeData.getNodeBalance(nodeValue2);
				final int nodeIndex2 = TreeData.getNodeIndex(nodeValue2);
				if (nodeBalance2 == 1) { // wenn rechtslastig
					fields[fieldIndex] = TreeData.getNodeValue(nodeIndex2, 0);
					return result & 0x7FFFFFFF; // positiv, da Teilbaum bereits balanziert
				}
				if (nodeBalance2 == 2) { // wenn linkslastig
					fields[fieldIndex] = TreeData.getNodeValue( //
						TreeData.getNodeBalance(prevs[nodeIndex2]) == 2 // wenn linkslastig
							? TreeData.rotateRightImpl(prevs, nexts, nodeIndex2) //
							: TreeData.rotateLeftRightImpl(prevs, nexts, nodeIndex2), //
						0);
					return result & 0x7FFFFFFF;
				}
				fields[fieldIndex] = TreeData.getNodeValue(nodeIndex2, 2);
				return result;
			}
			if (compare > 0) { // positiv, wenn im rechten Teilbaum einzufügen
				final int result = this.putIndexImpl2(key, nexts, nodeIndex);
				if (result >= 0) return result; // positiv, wenn Teilbaum bereits balanziert
				final int nodeValue2 = fields[fieldIndex]; // kann durch einfügen anders sein
				final int nodeBalance2 = TreeData.getNodeBalance(nodeValue2);
				final int nodeIndex2 = TreeData.getNodeIndex(nodeValue2);
				if (nodeBalance2 == 2) { // wenn linkslastig
					fields[fieldIndex] = TreeData.getNodeValue(nodeIndex2, 0);
					return result & 0x7FFFFFFF; // positiv, da Teilbaum bereits balanziert
				}
				if (nodeBalance2 == 1) { // wenn rechtslastig
					fields[fieldIndex] = TreeData.getNodeValue( //
						TreeData.getNodeBalance(nexts[nodeIndex2]) == 1 // wenn rechtslastig
							? TreeData.rotateLeftImpl(prevs, nexts, nodeIndex2) //
							: TreeData.rotateRightLeftImpl(prevs, nexts, nodeIndex2), //
						0);
					return result & 0x7FFFFFFF;
				}
				fields[fieldIndex] = TreeData.getNodeValue(nodeIndex2, 1);
				return result;
			}
			return nodeIndex; // positiv, da Teilbaum unverändert
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

		boolean popEntryImpl(final Object key, final Object value) {
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
		protected final Keys<GKey> newKeysAImpl() {
			return new KeysA<>(this);
		}

		/** Diese Methode gibt den {@link Iterator} über die Schlüssel in {@link #keys} zurück.
		 *
		 * @return Interator für {@link #newKeysAImpl()}. */
		protected final KeysIteratorA<GKey, GValue> newKeysIteratorAImpl() {
			return new KeysIteratorA<>(this);
		}

		/** Diese Methode gibt die {@link Collection} der Werte in {@link #values} zurück.
		 *
		 * @return Werte. */
		protected final Values<GValue> newValuesAImpl() {
			return new Values<>(this);
		}

		/** Diese Methode gibt den {@link Iterator} über die Werte in {@link #values} zurück.
		 *
		 * @return Interator für {@link #newValuesAImpl()}. */
		protected final ValuesIteratorA<GKey, GValue> newValuesIteratorImpl() {
			return new ValuesIteratorA<>(this);
		}

		/** Diese Methode gibt das {@link Set} der Einträge zurück.
		 *
		 * @return Einträge. */
		protected final Entries<GKey, GValue> newEntriesAImpl() {
			return new Entries<>(this);
		}

		/** Diese Methode gibt den {@link Iterator} über die Einträge zurück.
		 *
		 * @return Interator für {@link #newEntriesAImpl()}. */
		protected final EntriesIteratorA<GKey, GValue> newEntriesIteratorImpl() {
			return new EntriesIteratorA<>(this);
		}

		/** Diese Methode gibt die {@link Map} zu den Schlüsseln in {@link #keys} und Werten in {@link #values} zurück.
		 *
		 * @return Abbildung. */
		protected final Mapping<GKey, GValue> newMappingImpl() {
			return new Mapping<>(this);
		}

		/** Diese Methode entfernt alle Einträge. Hierbei werden die Anzahl der Einträge auf {@code 0} gesetzt und die Tabellen initialisiert. */
		protected final void clearImpl() {
			if (this.count == 0) return;
			TreeData.clearPrevs(this.prevs);
			TreeData.clearNexts(this.nexts);
			TreeData.clearObjects(this.keys);
			TreeData.clearObjects(this.values);
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

		public void print() {
			System.out.println("map: " + this.countImpl());
			this.printImpl(this.nexts[this.keys.length], 1);
		}

		private void printImpl(final int nodeValue, final int level) {
			if (~nodeValue == 0) {
				// System.out.append('>');
				// for (int i = 0; i < level; i++) System.out.append(" ");
				// System.out.println("0 null");
			} else {
				final int nodeIndex = TreeData.getNodeIndex(nodeValue);
				final int nodeBalance = TreeData.getNodeBalance(nodeValue);
				this.printImpl(this.prevs[nodeIndex], level + 1);
				System.out.append('>');
				for (int i = 0; i < level; i++) {
					System.out.append("        ");
				}
				System.out.print(Integer.toBinaryString(4 + nodeBalance).substring(1));
				System.out.print(" ");
				System.out.print(Objects.toString(this.keys[nodeIndex]));
				System.out.print(" -> ");
				System.out.println(Objects.toString(this.values[nodeIndex]));
				this.printImpl(this.nexts[nodeIndex], level + 1);
			}
		}

	}

