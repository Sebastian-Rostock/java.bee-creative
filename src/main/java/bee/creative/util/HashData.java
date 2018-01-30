package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import bee.creative.iam.IAMMapping;

/** Diese abstrakte Klasse implementiert eine transponierte {@link Object#hashCode() streuwertbasierte} Datenhaltung, die als Grundlage einer {@link Map} oder
 * eines {@link Set} verwendet werden kann.<br>
 * Um die Verwaltungsdaten je Eintrag zu minimieren, werden die Tabelle mit den Eigenschaften der Einträge spaltenweise in bis zu fünf Arrays
 * {@link #allocateImpl(int) reserviert} und die Verweise auf die nächsten Einträge in den verketteten Listen über Indexpositionen abgebildet.
 * <p>
 * Über die Methoden {@link #customHash(Object)} und {@link #customEquals(Object, Object)} kann die Berechnung der {@link Object#hashCode() Streuwerte} bzw. der
 * {@link Object#equals(Object) Äquivalenz} von Schlüsseln angepasst werden.
 *
 * @see HashSet
 * @see HashMap
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public abstract class HashData<GKey, GValue> {

	@SuppressWarnings ("javadoc")
	protected static class HashEntry<GKey, GValue> implements Entry<GKey, GValue> {

		protected final HashData<GKey, GValue> entryData;

		protected final int entryIndex;

		public HashEntry(final HashData<GKey, GValue> entryData, final int entryIndex) {
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
			return this.entryData.customHash(this.getKey()) ^ Objects.hash(this.getValue());
		}

		@Override
		public boolean equals(final Object object) {
			if (!(object instanceof Entry)) return false;
			final Entry<?, ?> that = (Entry<?, ?>)object;
			return this.entryData.customEquals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
		}

		@Override
		public String toString() {
			return this.getKey() + "=" + this.getValue();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static abstract class HashIterator<GKey, GValue, GItem> implements Iterator<GItem> {

		protected final HashData<GKey, GValue> entryData;

		/** Dieses Feld speichert den Index des nächsten Eintrags in {@link HashData#keys}. */
		protected int nextIndex;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link HashData#table}. */
		protected int tableIndex = -1;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link HashData#keys}. */
		protected int entryIndex = -1;

		public HashIterator(final HashData<GKey, GValue> entryData) {
			this.entryData = entryData;
			this.nextIndex2();
		}

		/** Diese Methode gibt den nächsten Schlüssel zurück. */
		protected final GKey nextKey() {
			return this.entryData.getKeyImpl(this.nextIndex());
		}

		/** Diese Methode gibt den nächsten Eintrag zurück. */
		protected final HashEntry<GKey, GValue> nextEntry() {
			return new HashEntry<>(this.entryData, this.nextIndex());
		}

		/** Diese Methode gibt den nächsten Wert zurück. */
		protected final GValue nextValue() {
			return this.entryData.getValueImpl(this.nextIndex());
		}

		/** Diese Methode ermitteln den Index des nächsten Eintrags und gibt den des aktuellen zurück. */
		protected final int nextIndex() {
			final int prevIndex = this.nextIndex;
			this.entryIndex = prevIndex;
			if (prevIndex < 0) throw new NoSuchElementException();
			final int nextIndex = this.entryData.nexts[prevIndex];
			if (nextIndex >= 0) {
				this.nextIndex = nextIndex;
			} else {
				this.nextIndex2();
			}
			return prevIndex;
		}

		/** Diese Methode sucht den Index des nächsten Eintrags. */
		final void nextIndex2() {
			final int[] table = this.entryData.table;
			final int length = table.length;
			for (int tableIndex = this.tableIndex + 1; tableIndex < length; ++tableIndex) {
				final int nextIndex = table[tableIndex];
				if (nextIndex >= 0) {
					this.nextIndex = nextIndex;
					this.tableIndex = tableIndex;
					return;
				}
			}
			this.nextIndex = -1;
			this.tableIndex = -1;
		}

		{}

		@Override
		public boolean hasNext() {
			return this.nextIndex >= 0;
		}

		@Override
		public void remove() {
			if (this.entryData.popEntryImpl(this.tableIndex, this.entryIndex)) return;
			throw new IllegalStateException();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class Keys<GKey> extends AbstractSet<GKey> {

		protected final HashData<GKey, ?> entryData;

		public Keys(final HashData<GKey, ?> entryData) {
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
	protected static class KeysIterator<GKey, GValue> extends HashIterator<GKey, GValue, GKey> {

		public KeysIterator(final HashData<GKey, GValue> entryData) {
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

		protected final HashData<?, GValue> entryData;

		public Values(final HashData<?, GValue> entryData) {
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
	protected static class ValuesIterator<GKey, GValue> extends HashIterator<GKey, GValue, GValue> {

		public ValuesIterator(final HashData<GKey, GValue> entryData) {
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

		protected final HashData<GKey, GValue> entryData;

		public Entries(final HashData<GKey, GValue> entryData) {
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
	protected static class EntriesIterator<GKey, GValue> extends HashIterator<GKey, GValue, Entry<GKey, GValue>> {

		public EntriesIterator(final HashData<GKey, GValue> entryData) {
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

		protected final HashData<GKey, GValue> entryData;

		public Mapping(final HashData<GKey, GValue> entryData) {
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

	/** Dieses Feld speichert den initialwert für {@link #table}. */
	private static final int[] EMPTY_TABLE = {-1};

	/** Dieses Feld speichert den initialwert für {@link #nexts} und {@link #hashes}. */
	private static final int[] EMPTY_INTEGERS = {};

	/** Dieses Feld speichert den initialwert für {@link #keys} und {@link #values}. */
	private static final Object[] EMPTY_OBJECTS = {};

	/** Dieses Feld speichert die maximale Kapazität. */
	private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

	{}

	@SuppressWarnings ("javadoc")
	private static void clearNexts(final int[] array) {
		for (int i = 0, size = array.length; i < size; array[i] = ++i) {}
	}

	@SuppressWarnings ("javadoc")
	private static void clearTable(final int[] array) {
		Arrays.fill(array, -1);
	}

	@SuppressWarnings ("javadoc")
	private static void clearObjects(final Object[] array) {
		if (array == null) return;
		Arrays.fill(array, null);
	}

	{}

	/** Dieses Feld bildet vom maskierten Streuwert eines Schlüssels auf den Index des Eintrags ab, dessen Schlüssel den gleichen maskierten Streuwert besitzt.
	 * Die Länge dieser Liste entspricht stets einer Potenz von 2. */
	int[] table = HashData.EMPTY_TABLE;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	Object[] keys = HashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf den Index des nächsten Eintrags ab. Für alle anderen Indizes bildet es auf den Index des nächsten
	 * reservierten Speicherbereiches ab. */
	int[] nexts = HashData.EMPTY_INTEGERS;

	/** Dieses Feld bildet vom Index eines Eintrags auf den Streuwert seines Schlüssels ab oder ist {@code null}. */
	int[] hashes;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	Object[] values;

	/** Dieses Feld speichert die Anzahl der Einträge. */
	int count = 0;

	/** Dieses Feld speichert den Index des nächsten freien Speicherbereiches in {@link #nexts}.<br>
	 * Die ungenutzten Speicherbereiche bilden über {@link #nexts} eine einfach verkettete Liste. */
	int empty = 0;

	/** Dieser Konstruktor initialisiert die streuwertbasierte Datenhaltung.
	 *
	 * @param withValues {@code true} für eine Abbildung von {@link #keys Schlüsseln} auf {@link #values Werte};<br>
	 *        {@code false} für eine Menge von {@link #keys Elementen}.
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel {@link #hashes gepuffert} werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashData(final boolean withValues, final boolean withHashes) {
		this.values = withValues ? HashData.EMPTY_OBJECTS : null;
		this.hashes = withHashes ? HashData.EMPTY_INTEGERS : null;
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

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	protected final void allocateImpl(final int capacity) throws IllegalArgumentException {
		if (capacity < this.count) throw new IllegalArgumentException();
		final int[] oldNexts = this.nexts;
		if (oldNexts.length == capacity) return;
		if (capacity == 0) {
			this.empty = 0;
			this.keys = HashData.EMPTY_OBJECTS;
			this.nexts = HashData.EMPTY_INTEGERS;
			this.values = this.values != null ? HashData.EMPTY_OBJECTS : null;
			this.hashes = this.hashes != null ? HashData.EMPTY_INTEGERS : null;
			this.table = HashData.EMPTY_TABLE;
		} else if (capacity <= HashData.MAX_CAPACITY) {
			final int newMask = IAMMapping.mask(capacity);
			final int[] oldTable = this.table;
			final int[] newTable = new int[newMask + 1];
			final int[] newNexts = new int[capacity];
			final int[] oldHashes = this.hashes;
			final int[] newHashes = oldHashes != null ? new int[capacity] : null;
			final Object[] oldKeys = this.keys;
			final Object[] newKeys = new Object[capacity];
			final Object[] oldValues = this.values;
			final Object[] newValues = oldValues != null ? new Object[capacity] : null;
			HashData.clearTable(newTable);
			HashData.clearNexts(newNexts);
			int newEntry = 0;
			for (int i = 0, size = oldTable.length; i < size; i++) {
				for (int oldIndex = oldTable[i]; 0 <= oldIndex; oldIndex = oldNexts[oldIndex]) {
					final Object key = oldKeys[oldIndex];
					final int hash = oldHashes != null ? oldHashes[oldIndex] : this.customHash(key);
					final int newIndex = hash & newMask;
					newNexts[newEntry] = newTable[newIndex];
					newTable[newIndex] = newEntry;
					newKeys[newEntry] = key;
					if (oldHashes != null) {
						newHashes[newEntry] = hash;
					}
					if (oldValues != null) {
						newValues[newEntry] = oldValues[oldIndex];
					}
					newEntry++;
				}
			}
			this.empty = newEntry;
			this.table = newTable;
			this.keys = newKeys;
			this.nexts = newNexts;
			this.values = newValues;
			this.hashes = newHashes;
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
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final Object[] values = this.values;
		for (int i = table.length - 1; 0 <= i; --i) {
			for (int entryIndex = table[i]; 0 <= entryIndex; entryIndex = nexts[entryIndex]) {
				if (Objects.equals(value, values[entryIndex])) return true;
			}
		}
		return false;
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
		return this.getIndexImpl2(key, this.customHash(key));
	}

	@SuppressWarnings ("javadoc")
	final int getIndexImpl2(final Object key, final int hash) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final int index = hash & (table.length - 1); // Streuwert "hash" maskieren
		final int entry = table[index];
		if (hashes == null) {
			for (int result = entry; 0 <= result; result = nexts[result]) {
				if (this.customEquals(keys[result], key)) return result;
			}
		} else {
			for (int result = entry; 0 <= result; result = nexts[result]) {
				if ((hashes[result] == hash) && this.customEquals(keys[result], key)) return result;
			}
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
		final int hash = this.customHash(key), result = this.getIndexImpl2(key, hash);
		if (result >= 0) return result;
		final int count = this.count + 1, capacity = this.capacityImpl();
		if (count > HashData.MAX_CAPACITY) throw new OutOfMemoryError();
		this.count = count;
		if (count <= capacity) return this.putIndexImpl2(key, hash);
		final int allocate = count + (count >> 1);
		this.allocateImpl((allocate < 0) || (allocate > HashData.MAX_CAPACITY) ? HashData.MAX_CAPACITY : allocate);
		return this.putIndexImpl2(key, hash);
	}

	@SuppressWarnings ("javadoc")
	final int putIndexImpl2(final GKey key, final int hash) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final int index = hash & (table.length - 1), result = this.empty;
		this.empty = nexts[result];
		nexts[result] = table[index];
		table[index] = result;
		this.keys[result] = key;
		if (hashes == null) return result;
		hashes[result] = hash;
		return result;
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
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final Object[] values = this.values;
		for (int tableIndex = table.length - 1; 0 <= tableIndex; --tableIndex) {
			for (int entryIndex = table[tableIndex]; 0 <= entryIndex; entryIndex = nexts[entryIndex]) {
				if (Objects.equals(value, values[entryIndex])) return this.popEntryImpl(tableIndex, entryIndex);
			}
		}
		return false;
	}

	/** Diese Methode entfernt den gegebenen Eintrag.
	 *
	 * @see HashIterator#remove()
	 * @param tableIndex Index der Liste in {@link #table}.
	 * @param entryIndex Index des Elements in {@link #keys}.
	 * @return {@code true}, wenn der Eintrag gefunden und entfernt wurde. */
	protected final boolean popEntryImpl(final int tableIndex, final int entryIndex) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		if ((tableIndex < 0) || (entryIndex < 0)) return false;
		int prevIndex = table[tableIndex];
		if (prevIndex < 0) return false;
		final Object[] values = this.values;
		if (prevIndex == entryIndex) {
			table[tableIndex] = nexts[prevIndex];
			nexts[prevIndex] = this.empty;
			this.keys[prevIndex] = null;
			if (values != null) {
				values[prevIndex] = null;
			}
			this.empty = prevIndex;
			this.count--;
			return true;
		}
		for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (nextIndex == entryIndex) {
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.empty;
				this.keys[nextIndex] = null;
				if (values != null) {
					values[nextIndex] = null;
				}
				this.empty = nextIndex;
				this.count--;
				return true;
			}
		}
		return false;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert und liefet nur dann {@code true}, wenn dieser zuvor über
	 * {@link #putImpl(Object, Object)} hinzugefügt wurde.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popEntryImpl(final Object key, final Object value) {
		final int hash = this.customHash(key);
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final Object[] values = this.values;
		final int index = hash & (table.length - 1);
		int prevIndex = table[index];
		if (prevIndex < 0) return false;
		if (((hashes == null) || (hashes[prevIndex] == hash)) && this.customEquals(keys[prevIndex], key) && Objects.equals(values[prevIndex], value)) {
			table[index] = nexts[prevIndex];
			nexts[prevIndex] = this.empty;
			keys[prevIndex] = null;
			values[prevIndex] = null;
			this.empty = prevIndex;
			this.count--;
			return true;
		}
		for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (((hashes == null) || (hashes[nextIndex] == hash)) && this.customEquals(keys[nextIndex], key) && Objects.equals(values[nextIndex], value)) {
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.empty;
				keys[nextIndex] = null;
				values[nextIndex] = null;
				this.empty = nextIndex;
				this.count--;
				return true;
			}
		}
		return false;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des entfernten Eintrags oder {@code -1}. */
	protected final int popIndexImpl(final Object key) {
		return this.popIndexImpl2(key, this.customHash(key));
	}

	@SuppressWarnings ("javadoc")
	final int popIndexImpl2(final Object key, final int hash) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final int index = hash & (table.length - 1);
		int prevIndex = table[index];
		if (0 <= prevIndex) {
			if (hashes == null) {
				if (this.customEquals(keys[prevIndex], key)) {
					keys[prevIndex] = null;
					table[index] = nexts[prevIndex];
					nexts[prevIndex] = this.empty;
					this.empty = prevIndex;
					this.count--;
					return prevIndex;
				}
				for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
					if (this.customEquals(keys[nextIndex], key)) {
						keys[nextIndex] = null;
						nexts[prevIndex] = nexts[nextIndex];
						nexts[nextIndex] = this.empty;
						this.empty = nextIndex;
						this.count--;
						return nextIndex;
					}
				}
			} else {
				if ((hashes[prevIndex] == hash) && this.customEquals(keys[prevIndex], key)) {
					keys[prevIndex] = null;
					table[index] = nexts[prevIndex];
					nexts[prevIndex] = this.empty;
					this.empty = prevIndex;
					this.count--;
					return prevIndex;
				}
				for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
					if ((hashes[nextIndex] == hash) && this.customEquals(keys[nextIndex], key)) {
						keys[nextIndex] = null;
						nexts[prevIndex] = nexts[nextIndex];
						nexts[nextIndex] = this.empty;
						this.empty = nextIndex;
						this.count--;
						return nextIndex;
					}
				}
			}
		}
		return -1;
	}

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
	protected final void clearImpl() {
		if (this.count == 0) return;
		HashData.clearTable(this.table);
		HashData.clearNexts(this.nexts);
		HashData.clearObjects(this.keys);
		HashData.clearObjects(this.values);
		this.count = 0;
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Schlüssels zurück.
	 *
	 * @param key Schlüssel oder {@code null}.
	 * @return {@link Object#hashCode() Streuwert} des Schlüssels. */
	protected int customHash(final Object key) {
		return Objects.hash(key);
	}

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Schlüssel zurück.
	 *
	 * @param thisKey alter bzw. verwalteter Schlüssel oder {@code null}.
	 * @param thatKey neuer bzw. gesuchter Schlüssel oder {@code null}.
	 * @return Äquivalenz der gegebenen Schlüssel. */
	protected boolean customEquals(final Object thisKey, final Object thatKey) {
		return Objects.equals(thisKey, thatKey);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			final HashData<?, ?> result = (HashData<?, ?>)super.clone();
			if (this.capacityImpl() == 0) {
				result.keys = HashData.EMPTY_OBJECTS;
				result.nexts = HashData.EMPTY_INTEGERS;
				result.table = HashData.EMPTY_TABLE;
				result.values = result.values != null ? HashData.EMPTY_OBJECTS : null;
				result.hashes = result.hashes != null ? HashData.EMPTY_INTEGERS : null;
			} else {
				result.keys = this.keys.clone();
				result.nexts = this.nexts.clone();
				result.table = this.table.clone();
				result.values = result.values == null ? null : this.values.clone();
				result.hashes = result.hashes == null ? null : this.hashes.clone();
			}
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}