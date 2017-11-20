package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import bee.creative.iam.IAMMapping;

/** Diese abstrakte Klasse implementiert eine {@link Object#hashCode() Streuwert}-basierte Datenhaltung, die als Grundlage einer {@link Map} oder eines
 * {@link Set} verwendet werden kann.<br>
 * Um die Verwaltungsdaten je Eintrag zu minimieren, wird die Tabelle mit den Eigenschaften der Einträge spaltenweise in bis zu fünf Arrays
 * {@link #allocate(int) reserviert} und werden die Verweise auf die nächsten Einträge in den verketteten Listen über Indexpositionen abgebildet.
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

		protected final HashData<?, ?> entryData;

		protected final int entryIndex;

		public HashEntry(final HashData<?, ?> entryData, final int entryIndex) {
			this.entryData = entryData;
			this.entryIndex = entryIndex;
		}

		{}

		@Override
		@SuppressWarnings ("unchecked")
		public GKey getKey() {
			return (GKey)this.entryData.keys[this.entryIndex];
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue getValue() {
			return (GValue)this.entryData.values[this.entryIndex];
		}

		@Override
		public GValue setValue(final GValue value) {
			final GValue result = this.getValue();
			this.entryData.values[this.entryIndex] = value;
			return result;
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
	protected static abstract class HashIterator<GItem> implements Iterator<GItem> {

		protected final HashData<?, ?> entryData;

		/** Dieses Feld speichert den Index des nächsten Eintrags in {@link HashData#keys}. */
		protected int nextIndex;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link HashData#table}. */
		protected int tableIndex = -1;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link HashData#keys}. */
		protected int entryIndex = -1;

		public HashIterator(final HashData<?, ?> entryData) {
			this.entryData = entryData;
			this.seekEntry();
		}

		/** Diese Methode sucht den Index des nächsten Eintrags. */
		private final void seekEntry() {
			final int[] tableArray = this.entryData.table;
			final int tableLength = tableArray.length;
			for (int tableIndex = this.tableIndex + 1; tableIndex < tableLength; ++tableIndex) {
				final int entryIndex = tableArray[tableIndex];
				if (entryIndex >= 0) {
					this.tableIndex = tableIndex;
					this.nextIndex = entryIndex;
					return;
				}
			}
			this.nextIndex = -1;
			this.tableIndex = -1;
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
				this.seekEntry();
			}
			return prevIndex;
		}

		{}

		@Override
		public boolean hasNext() {
			return this.nextIndex >= 0;
		}

		@Override
		public void remove() {
			if (this.entryData.popEntry(this.tableIndex, this.entryIndex)) return;
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
			this.entryData.clearEntries();
		}

		@Override
		public Iterator<GKey> iterator() {
			return this.entryData.getKeysIterator();
		}

		@Override
		public boolean remove(final Object item) {
			return this.entryData.popKey(item);
		}

		@Override
		public boolean contains(final Object item) {
			return this.entryData.getKey(item);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class KeysIterator<GKey> extends HashIterator<GKey> {

		public KeysIterator(final HashData<GKey, ?> entryData) {
			super(entryData);
		}

		{}

		@Override
		@SuppressWarnings ("unchecked")
		public GKey next() {
			return (GKey)this.entryData.keys[this.nextIndex()];
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
			this.entryData.clearEntries();
		}

		@Override
		public Iterator<GValue> iterator() {
			return this.entryData.getValuesIterator();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ValuesIterator<GValue> extends HashIterator<GValue> {

		public ValuesIterator(final HashData<?, GValue> entryData) {
			super(entryData);
		}

		{}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue next() {
			return (GValue)this.entryData.values[this.nextIndex()];
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
			this.entryData.clearEntries();
		}

		@Override
		public Iterator<Entry<GKey, GValue>> iterator() {
			return this.entryData.getEntriesIterator();
		}

		@Override
		public boolean remove(final Object object) {
			if (!(object instanceof Entry)) return false;
			final Entry<?, ?> entry = (Entry<?, ?>)object;
			return this.entryData.popEntry(entry.getKey(), entry.getValue());
		}

		@Override
		public boolean contains(final Object object) {
			if (!(object instanceof Entry)) return false;
			final Entry<?, ?> entry = (Entry<?, ?>)object;
			return this.entryData.getEntry(entry.getKey(), entry.getValue());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class EntriesIterator<GKey, GValue> extends HashIterator<Entry<GKey, GValue>> {

		public EntriesIterator(final HashData<GKey, GValue> entryData) {
			super(entryData);
		}

		{}

		@Override
		public Entry<GKey, GValue> next() {
			return new HashEntry<>(this.entryData, this.nextIndex());
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
			this.entryData.clearEntries();
		}

		@Override
		public boolean isEmpty() {
			return this.entryData.count == 0;
		}

		@Override
		public boolean containsKey(final Object key) {
			return this.entryData.getKey(key);
		}

		@Override
		public boolean containsValue(final Object value) {
			return this.values().contains(value);
		}

		@Override
		public GValue get(final Object key) {
			return this.entryData.getValue(key);
		}

		@Override
		public Set<GKey> keySet() {
			return this.entryData.getKeys();
		}

		@Override
		public GValue put(final GKey key, final GValue value) {
			return this.entryData.putValue(key, value);
		}

		@Override
		public GValue remove(final Object key) {
			return this.entryData.popValue(key);
		}

		@Override
		public Values<GValue> values() {
			return this.entryData.getValues();
		}

		@Override
		public Entries<GKey, GValue> entrySet() {
			return this.entryData.getEntries();
		}

	}

	{}

	/** Dieses Feld speichert den initialwert für {@link #table}. */
	private static final int[] EMPTY_TABLE = {-1};

	/** Dieses Feld speichert den initialwert für {@link #nexts} und {@link #hashes}. */
	private static final int[] EMPTY_INTEGERS = {};

	/** Dieses Feld speichert den initialwert für {@link #keys} und {@link #values}. */
	private static final Object[] EMPTY_OBJECTS = {};

	{}

	@SuppressWarnings ("javadoc")
	private static void clearNexts(final int[] array) {
		for (int i = 0, size = array.length; i < size;) {
			array[i] = ++i;
		}
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

	/** Dieses Feld bildet von dem Index eines Eintrags auf dessen Schlüssel ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	protected Object[] keys = HashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet von dem Index eines Eintrags auf den Index des nächsten Eintrags ab. Für alle anderen Indizes bildet es auf den Index des nächsten
	 * freien Elements ab. */
	protected int[] nexts = HashData.EMPTY_INTEGERS;

	/** Dieses Feld bildet von dem Index eines Eintrags auf dessen Wert ab oder ist {@code null}. Für alle anderen Indizes bildet es auf {@code null} ab. */
	protected Object[] values;

	/** Dieses Feld bildet von dem Index eines Eintrags auf den Streuwert seines Schlüssels ab oder ist {@code null}. */
	protected int[] hashes;

	/** Dieses Feld bildet von dem maskierten Streuwert eines Schlüssels auf den um 1 erhöhten Index des Eintrags ab, dessen Schlüssel den gleichen maskierten
	 * Streuwert besitzt. Die Länge dieser Liste entspricht stets einer Potenz von 2. */
	protected int[] table = HashData.EMPTY_TABLE;

	/** Dieses Feld speichert den Index des nächsten freien Elements in {@link #nexts}. */
	protected int entry = 0;

	/** Dieses Feld speichert die Anzahl der Einträge. */
	protected int count = 0;

	/** Dieser Konstruktor initialisiert die streuwertbasierte Datenhaltung.
	 *
	 * @param withValues {@code true} für eine Abbildung von {@link #keys} auf {@link #values};<br>
	 *        {@code false} für eine Menge von {@link #keys}.
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashData(final boolean withValues, final boolean withHashes) {
		this.values = withValues ? HashData.EMPTY_OBJECTS : null;
		this.hashes = withHashes ? HashData.EMPTY_INTEGERS : null;
	}

	{}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public final int capacity() {
		return this.nexts.length;
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	public final void allocate(final int capacity) throws IllegalArgumentException {
		if (capacity < this.count) throw new IllegalArgumentException();
		final int[] oldNexts = this.nexts;
		if (oldNexts.length == capacity) return;
		if (capacity == 0) {
			this.entry = 0;
			this.keys = HashData.EMPTY_OBJECTS;
			this.nexts = HashData.EMPTY_INTEGERS;
			this.values = this.values != null ? HashData.EMPTY_OBJECTS : null;
			this.hashes = this.hashes != null ? HashData.EMPTY_INTEGERS : null;
			this.table = HashData.EMPTY_TABLE;
		} else {
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
			this.entry = newEntry;
			this.table = newTable;
			this.keys = newKeys;
			this.nexts = newNexts;
			this.values = newValues;
			this.hashes = newHashes;
		}
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public final void compact() {
		this.allocate(this.count);
	}

	/** Diese Methode gibt das {@link Set} der Schlüssel in {@link #keys} zurück.
	 *
	 * @return Schlüssel. */
	protected final Keys<GKey> getKeys() {
		return new Keys<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Schlüssel in {@link #keys} zurück.
	 *
	 * @return Interator für {@link #getKeys()}. */
	protected final KeysIterator<GKey> getKeysIterator() {
		return new KeysIterator<>(this);
	}

	/** Diese Methode gibt die {@link Collection} der Werte in {@link #values} zurück.
	 *
	 * @return Werte. */
	protected final Values<GValue> getValues() {
		return new Values<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Werte in {@link #values} zurück.
	 *
	 * @return Interator für {@link #getValues()}. */
	protected final ValuesIterator<GValue> getValuesIterator() {
		return new ValuesIterator<>(this);
	}

	/** Diese Methode gibt das {@link Set} der Einträge zurück.
	 *
	 * @return Einträge. */
	protected final Entries<GKey, GValue> getEntries() {
		return new Entries<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Einträge zurück.
	 *
	 * @return Interator für {@link #getEntries()}. */
	protected final EntriesIterator<GKey, GValue> getEntriesIterator() {
		return new EntriesIterator<>(this);
	}

	/** Diese Methode gibt die {@link Map} zu den Schlüsseln in {@link #keys} und Werten in {@link #values} zurück.
	 *
	 * @return Abbildung. */
	protected final Mapping<GKey, GValue> getMapping() {
		return new Mapping<>(this);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Set#contains(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean getKey(final Object key) {
		return 0 <= this.getEntry(key);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	protected final int getEntry(final Object key) {
		return this.getEntry(key, this.customHash(key));
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Streuwert und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param hash Streuwert des Schlüssels.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	protected final int getEntry(final Object key, final int hash) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final int index = hash & (table.length - 1);
		final int entry = table[index];
		for (int result = entry; 0 <= result; result = nexts[result]) {
			if (((hashes == null) || (hashes[result] == hash)) && this.customEquals(keys[result], key)) return result;
		}
		return -1;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Set#contains(Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean getEntry(final Object key, final Object value) {
		final int index = this.getEntry(key);
		return (index >= 0) && Objects.equals(this.values[index], value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Wert zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#get(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return Wert des gefundenen Eintrags oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue getValue(final Object key) {
		final int index = this.getEntry(key);
		if (index < 0) return null;
		return (GValue)this.values[index];
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code false} zurück, wenn ein solcher Eintrag existiert. Wenn kein solcher
	 * Eintrag existierte, wird er erzeugt und {@code true} geliefert.
	 *
	 * @see Set#add(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag erzeugt wurde. */
	protected final boolean putKey(final GKey key) {
		final int count = this.count;
		this.putEntry(key);
		return count != this.count;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird er erzeugt.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int putEntry(final GKey key) {
		final int hash = this.customHash(key), result = this.getEntry(key, hash);
		if (result >= 0) return result;
		final int count = this.count + 1, capacity = this.capacity();
		this.count = count;
		if (count <= capacity) return this.putEntry(key, hash);
		final int allocate = capacity + (capacity >> 1);
		this.allocate(count > allocate ? count : allocate);
		return this.putEntry(key, hash);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Streuwert und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird er erzeugt.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param hash Streuwert des Schlüssels.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int putEntry(final GKey key, final int hash) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final int index = hash & (table.length - 1), result = this.entry;
		this.entry = nexts[result];
		nexts[result] = table[index];
		table[index] = result;
		this.keys[result] = key;
		if (hashes == null) return result;
		hashes[result] = hash;
		return result;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel, setzt dessen Wert und gibt seinen vorheriten Wert zurück.<br>
	 * Wenn kein solcher Eintrag existierte, wird {@code null} geliefert.
	 *
	 * @see Map#put(Object, Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value neuer Wert des Eintrags.
	 * @return alert Wert des gefundenen Eintrags oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue putValue(final GKey key, final GValue value) {
		final int index = this.putEntry(key);
		final Object[] values = this.values;
		final Object result = values[index];
		values[index] = value;
		return (GValue)result;
	}

	/** Diese Methode entfernt den gegeben Schlüssel und liefet nur dann {@code true}, wenn dieser zuvor über {@link #putKey(Object)} hinzugefügt wurde.
	 *
	 * @see Set#remove(Object)
	 * @param key Schlüssel.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popKey(final Object key) {
		final int count = this.count;
		this.popEntry(key);
		return count != this.count;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des entfernten Eintrags oder {@code -1}. */
	protected final int popEntry(final Object key) {
		return this.popEntry(key, this.customHash(key));
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel sowie Streuwert und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param hash Streuwert des Schlüssels.
	 * @return Index des entfernten Eintrags oder {@code -1}. */
	protected final int popEntry(final Object key, final int hash) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final int index = hash & (table.length - 1);
		int prevIndex = table[index];
		if (0 <= prevIndex) {
			if (((hashes == null) || (hashes[prevIndex] == hash)) && this.customEquals(keys[prevIndex], key)) {
				keys[prevIndex] = null;
				table[index] = nexts[prevIndex];
				nexts[prevIndex] = this.entry;
				this.entry = prevIndex;
				this.count--;
				return prevIndex;
			}
			for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
				if (((hashes == null) || (hashes[nextIndex] == hash)) && this.customEquals(keys[nextIndex], key)) {
					keys[nextIndex] = null;
					nexts[prevIndex] = nexts[nextIndex];
					nexts[nextIndex] = this.entry;
					this.entry = nextIndex;
					this.count--;
					return nextIndex;
				}
			}
		}
		return -1;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel sowie dem gegebenen Wert und liefet nur dann {@code true}, wenn dieser zuvor über
	 * {@link #putValue(Object, Object)} hinzugefügt wurde.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popEntry(final Object key, final Object value) {
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
			nexts[prevIndex] = this.entry;
			keys[prevIndex] = null;
			values[prevIndex] = null;
			this.entry = prevIndex;
			this.count--;
			return true;
		}
		for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (((hashes == null) || (hashes[nextIndex] == hash)) && this.customEquals(keys[nextIndex], key) && Objects.equals(values[nextIndex], value)) {
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.entry;
				keys[nextIndex] = null;
				values[nextIndex] = null;
				this.entry = nextIndex;
				this.count--;
				return true;
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
	protected final boolean popEntry(final int tableIndex, final int entryIndex) {
		final int[] table = this.table;
		final int[] nexts = this.nexts;
		if ((tableIndex < 0) || (entryIndex < 0)) return false;
		int prevIndex = table[tableIndex];
		if (prevIndex < 0) return false;
		if (prevIndex == entryIndex) {
			table[tableIndex] = nexts[prevIndex];
			nexts[prevIndex] = this.entry;
			this.keys[prevIndex] = null;
			this.values[prevIndex] = null;
			this.entry = prevIndex;
			this.count--;
			return true;
		}
		for (int nextIndex = nexts[prevIndex]; 0 <= nextIndex; prevIndex = nextIndex, nextIndex = nexts[nextIndex]) {
			if (nextIndex == entryIndex) {
				nexts[prevIndex] = nexts[nextIndex];
				nexts[nextIndex] = this.entry;
				this.keys[nextIndex] = null;
				this.values[nextIndex] = null;
				this.entry = nextIndex;
				this.count--;
				return true;
			}
		}
		return false;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt den Wert des Eintrags zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#remove(Object)
	 * @param key Schlüssel.
	 * @return Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue popValue(final Object key) {
		final int entry = this.popEntry(key);
		if (entry < 0) return null;
		final Object[] values = this.values;
		if (values == null) return null;
		final Object result = values[entry];
		values[entry] = null;
		return (GValue)result;
	}

	/** Diese Methode entfernt alle Einträge. Hierbei werden die Anzahl der Einträge auf {@code 0} gesetzt und die Tabellen initialisiert. */
	protected final void clearEntries() {
		if (this.count == 0) return;
		HashData.clearTable(this.table);
		HashData.clearNexts(this.nexts);
		HashData.clearObjects(this.keys);
		HashData.clearObjects(this.values);
		this.count = 0;
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Schlüssels zurück.
	 *
	 * @param key Schlüssel.
	 * @return {@link Object#hashCode() Streuwert} des Schlüssels. */
	protected int customHash(final Object key) {
		return Objects.hash(key);
	}

	/** /** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Schlüssel zurück.
	 *
	 * @param thisKey alter bzw. verwalteter Schlüssel.
	 * @param thatKey neuer bzw. gesuchter Schlüssel.
	 * @return Äquivalenz der gegebenen Schlüssel. */
	protected boolean customEquals(final Object thisKey, final Object thatKey) {
		return Objects.equals(thisKey, thatKey);
	}

}