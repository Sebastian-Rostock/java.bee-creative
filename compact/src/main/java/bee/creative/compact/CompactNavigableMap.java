package bee.creative.compact;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Diese Klasse implementiert eine abstrakte {@link NavigableMap}, deren Daten in einem Array verwaltet werden.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
@SuppressWarnings ("javadoc")
public abstract class CompactNavigableMap<GKey, GValue> extends CompactMap<GKey, GValue> implements NavigableMap<GKey, GValue> {

	/**
	 * Diese Klasse implementiert die anstrakte Menge der Schlüssel einer {@link NavigableMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GData> Typ der {@link NavigableMap}.
	 */
	protected static abstract class AbstractNavigableKeySet<GKey, GData extends NavigableMap<GKey, ?>> extends AbstractSet<GKey> implements NavigableSet<GKey> {

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
		public NavigableSet<GKey> subSet(final GKey fromElement, final boolean fromInclusive, final GKey toElement, final boolean toInclusive) {
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
	 * Diese Klasse implementiert die aufsteigende Menge der Schlüssel einer {@link CompactNavigableMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 */
	protected static final class CompactNavigableKeySet<GKey> extends CompactNavigableMap.AbstractNavigableKeySet<GKey, CompactNavigableMap<GKey, ?>> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactNavigableMap}.
		 * 
		 * @param data {@link CompactNavigableMap}.
		 * @throws NullPointerException Wenn die gegebene {@link CompactNavigableMap} {@code null} ist.
		 */
		public CompactNavigableKeySet(final CompactNavigableMap<GKey, ?> data) throws NullPointerException {
			super(data);
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
	 * Diese Klasse implementiert die aufsteigende Menge der Schlüssel einer {@link CompactAscendingSubMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 */
	protected static final class CompactAscendingKeySet<GKey> extends
		CompactNavigableMap.AbstractNavigableKeySet<GKey, CompactNavigableMap.CompactAscendingSubMap<GKey, ?>> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactAscendingSubMap}.
		 * 
		 * @param data {@link CompactAscendingSubMap}.
		 * @throws NullPointerException Wenn die gegebene {@link CompactAscendingSubMap} {@code null} ist.
		 */
		public CompactAscendingKeySet(final CompactNavigableMap.CompactAscendingSubMap<GKey, ?> data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GKey> iterator() {
			return new CompactMapKeyAscendingIterator<GKey>(this.data.data, this.data.firstIndex(), this.data.lastIndex() + 1);
		}

	}

	/**
	 * Diese Klasse implementiert die abfsteigende Menge der Schlüssel einer {@link CompactDescendingSubMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 */
	protected static final class CompactDescendingKeySet<GKey> extends
		CompactNavigableMap.AbstractNavigableKeySet<GKey, CompactNavigableMap.CompactDescendingSubMap<GKey, ?>> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactDescendingSubMap}.
		 * 
		 * @param data {@link CompactDescendingSubMap}.
		 * @throws NullPointerException Wenn die gegebene {@link CompactDescendingSubMap} {@code null} ist.
		 */
		public CompactDescendingKeySet(final CompactNavigableMap.CompactDescendingSubMap<GKey, ?> data) throws NullPointerException {
			super(data);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GKey> iterator() {
			return new CompactMapKeyDescendingIterator<GKey>(this.data.data, this.data.firstIndex(), this.data.lastIndex() + 1);
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte Teilmenge einer {@link CompactNavigableMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	protected static abstract class CompactNavigableSubMap<GKey, GValue> extends CompactSubData<CompactNavigableMap<GKey, GValue>> implements
		NavigableMap<GKey, GValue> {

		/**
		 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an die Einträge einer {@link CompactNavigableSubMap} delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 */
		protected static final class EntrySet<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

			/**
			 * Dieses Feld speichert die {@link CompactNavigableSubMap}.
			 */
			protected final CompactNavigableMap.CompactNavigableSubMap<GKey, GValue> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableSubMap}.
			 * 
			 * @param data {@link CompactNavigableSubMap}.
			 */
			public EntrySet(final CompactNavigableMap.CompactNavigableSubMap<GKey, GValue> data) {
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
			public void clear() {
				this.data.clear();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Iterator<Entry<GKey, GValue>> iterator() {
				return new CompactMapEntryIterator<GKey, GValue>(this.data.data, this.data.firstIndex(), this.data.lastIndex() - 1);
			}
		}

		/**
		 * Diese Klasse implementiert ein {@link AbstractCollection}, das seine Schnittstelle an die Werte einer {@link CompactNavigableSubMap} delegiert.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ der Werte.
		 */
		protected static final class ValueCollection<GValue> extends AbstractCollection<GValue> {

			/**
			 * Dieses Feld speichert die {@link CompactNavigableSubMap}.
			 */
			protected final CompactNavigableMap.CompactNavigableSubMap<?, GValue> data;

			/**
			 * Dieser Konstrukteur initialisiert die {@link CompactNavigableSubMap}.
			 * 
			 * @param data {@link CompactNavigableSubMap}.
			 */
			public ValueCollection(final CompactNavigableMap.CompactNavigableSubMap<?, GValue> data) {
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
			public void clear() {
				this.data.clear();
			}

			/**
			 * {@inheritDoc}
			 */
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
		public CompactNavigableSubMap(final CompactNavigableMap<GKey, GValue> map, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) {
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
		public Set<GKey> keySet() {
			return this.navigableKeySet();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return new CompactNavigableSubMap.EntrySet<GKey, GValue>(this);
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
		public Collection<GValue> values() {
			return new CompactNavigableSubMap.ValueCollection<GValue>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return new CompactMapItems<GKey, GValue>(this).hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Set<?>)) return false;
			return new CompactMapItems<GKey, GValue>(this).equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return new CompactMapItems<GKey, GValue>(this).toString();
		}

	}

	/**
	 * Diese Klasse implementiert die aufsteigende Teilmenge einer {@link CompactNavigableMap} .
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class CompactAscendingSubMap<GKey, GValue> extends CompactNavigableMap.CompactNavigableSubMap<GKey, GValue> {

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
		public CompactAscendingSubMap(final CompactNavigableMap<GKey, GValue> map, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) throws IllegalArgumentException {
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
			return new CompactNavigableMap.CompactDescendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, this.lastItem, this.lastInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GKey> navigableKeySet() {
			return new CompactNavigableMap.CompactAscendingKeySet<GKey>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey, final boolean toInclusive) {
			if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
			if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this.data, fromKey, fromInclusive, toKey, toInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
			if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, toKey, inclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
			if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("fromElement out of range");
			return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this.data, fromKey, inclusive, this.lastItem, this.lastInclusive);
		}

	}

	/**
	 * Diese Klasse implementiert die absteigende Teilmenge einer {@link CompactNavigableMap}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 */
	protected static final class CompactDescendingSubMap<GKey, GValue> extends CompactNavigableMap.CompactNavigableSubMap<GKey, GValue> {

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

		public CompactDescendingSubMap(final CompactNavigableMap<GKey, GValue> map, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) throws IllegalArgumentException {
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
			return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this.data, this.fromItem, this.fromInclusive, this.lastItem, this.lastInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableSet<GKey> navigableKeySet() {
			return new CompactNavigableMap.CompactDescendingKeySet<GKey>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey, final boolean toInclusive) {
			if(!this.isInRange(fromKey, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
			if(!this.isInRange(toKey, toInclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableMap.CompactDescendingSubMap<GKey, GValue>(this.data, toKey, toInclusive, fromKey, fromInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
			if(!this.isInRange(toKey, inclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableMap.CompactDescendingSubMap<GKey, GValue>(this.data, toKey, inclusive, this.fromItem, this.fromInclusive);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
			if(!this.isInRange(fromKey, inclusive)) throw new IllegalArgumentException("fromElement out of range");
			return new CompactNavigableMap.CompactDescendingSubMap<GKey, GValue>(this.data, this.lastItem, this.lastInclusive, fromKey, inclusive);
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
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität und dem gegebenen {@link Comparator}.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @param comparator {@link Comparator}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public CompactNavigableMap(final int capacity, final Comparator<? super GKey> comparator) throws IllegalArgumentException, NullPointerException {
		this(comparator);
		this.allocate(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen und dem gegebenen {@link Comparator}.
	 * 
	 * @see CompactData#allocate(int)
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} bzw. die gegebene {@link Map} {@code null} ist.
	 */
	public CompactNavigableMap(final Map<? extends GKey, ? extends GValue> map, final Comparator<? super GKey> comparator) throws NullPointerException {
		this(comparator);
		if(map == null) throw new NullPointerException("map is null");
		this.allocate(map.size());
		this.putAll(map);
	}

	/**
	 * Diese Methode löscht das {@code index}-te Element und gibt es oder {@code null} zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-te Element oder {@code null}.
	 */
	protected final Entry<GKey, GValue> poll(final int index) {
		if((index < 0) || (index >= this.size())) return null;
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
		if((index < 0) || (index >= this.size())) return null;
		return this.getKey(index);
	}

	/**
	 * Diese Methode gibt das {@code index}-te Element oder {@code null} zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Element oder {@code null}.
	 */
	protected final Entry<GKey, GValue> getEntryOrNull(final int index) {
		if((index < 0) || (index >= this.size())) return null;
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
		if((index < 0) || (index >= this.size())) throw new NoSuchElementException();
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
		if((index < 0) || (index >= this.size())) throw new NoSuchElementException();
		return this.getEntry(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customItemIndex(final Object key) {
		return this.defaultCompareIndex(key, 0);
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
		return new CompactNavigableMap.CompactDescendingSubMap<GKey, GValue>(this, CompactSubData.OPEN, true, CompactSubData.OPEN, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NavigableSet<GKey> navigableKeySet() {
		return new CompactNavigableMap.CompactNavigableKeySet<GKey>(this);
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
	public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey, final boolean toInclusive) {
		return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this, fromKey, fromInclusive, toKey, toInclusive);
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
		return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this, CompactSubData.OPEN, true, toKey, inclusive);
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
		return new CompactNavigableMap.CompactAscendingSubMap<GKey, GValue>(this, fromKey, inclusive, CompactSubData.OPEN, true);
	}

}