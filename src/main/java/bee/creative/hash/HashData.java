package bee.creative.hash;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.iam.IAMMapping;
import bee.creative.util.Objects;

// HashMap: H + 4P + 4I + 3(H + P) + (H + I) + n(H + 3P+I) + b(P)

/** Diese abstrakte Klasse implementiert die Basis einer {@link Object#hashCode() Streuwert}-basierten Abbildung von Schlüsseln auf Werte. Die Einträge der
 * Abbildung besitzen einen nächsten Eintrag, sodass einfach verkettete Listen von Einträgen erzeugt werden können. Der nächste Eintrag eines Eintrags muss dazu
 * mit {@link #getEntryNext(Object)} gelesen und mit {@link #setEntryNext(Object, Object)} geschrieben werden können. Als Schlüssel und Werte sind beliebige
 * Objekte zulässig. Insbesondere ist es möglich, die Werte der Abbildung als Einträge zu verwenden, sofern diese über einen Schlüssel und ein nächsten Element
 * verfügen. Es ist auch möglich für Schlüssel und Wert eines Eintrags das gleiche Objekt zu nutzen.
 * <p>
 * Die Einträge werden in einfach verketteten Listen verwaltet, deren Kopfelemente bzw. Einträge in einer Tabelle hinterlegt werden. Die Methoden
 * {@link #customKeyHash(Object)} muss zu einem gegebenen Schlüssel den {@link Object#hashCode() Streuwert} berechnen, und die Methode
 * {@link #getIndex(int, int)} muss zu einem gegebenen {@link Object#hashCode() Streuwert} den Index des Eintrags in der Tabelle berechnen, in dessen einfach
 * verketteter Liste sich der Eintrag mit dem gegebenen Schlüssen bzw. {@link Object#hashCode() Streuwert} befindet.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
abstract class HashData<GKey, GValue> {

	protected static abstract class HashIterator<GItem> implements Iterator<GItem> {

		HashData<?, ?> hashData;

		int index;

	}

	{}

	static final int[] EMPTY_INTEGERS = {};

	static final int[] EMPTY_TABLE = {-1};

	static final Object[] EMPTY_OBJECTS = {};

	{}

	/** Dieses Feld speichert den Index des nächsten freien Elements in {@link #nexts}. */
	protected int entry = 0;

	/** Dieses Feld speichert die Anzahl der Elemente. */
	protected int count = 0;

	/** Dieses Feld bildet von dem maskierten Streuwert eines Schlüssels auf den um 1 erhöhten Index des Eintrags ab, dessen Schlüssel den gleichen maskierten
	 * Streuwert besitzt. Die Länge dieser Liste entspricht stets einer Potenz von 2. */
	protected int[] heads = HashData.EMPTY_TABLE;

	// XXX keys/ values/ hash hier oder in nachfahren

	protected Object[] keys = HashData.EMPTY_OBJECTS;

	/** Dieses Feld bildet von dem Index eines Eintrags auf den Index des nächsten Eintrags ab. Für alle anderen Werte bildet er auf den Index des nächsten freien
	 * elements ab. */
	protected int[] nexts = HashData.EMPTY_INTEGERS;

	protected Object[] values;

	protected int[] hashes;

	/** Dieser Konstruktor initialisiert die streuwertbasierte Datenhaltung.
	 *
	 * @param withValues {@code true} für eine Abbildung von {@link #keys} auf {@link #values};<br>
	 *        {@code false} für eine Menge von {@link #keys}.
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte der Schlüssel schnell ermittelt werden kann. */
	public HashData(final boolean withValues, final boolean withHashes) {
		this.values = withValues ? HashData.EMPTY_OBJECTS : null;
		this.hashes = withHashes ? HashData.EMPTY_INTEGERS : null;
	}

	{}

	/** Diese Methode gibt die Anzahl der Elementen zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public final int capacity() {
		return this.nexts.length;
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Elemente.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Elementen ist. */
	public final void allocate(final int capacity) throws IllegalArgumentException {
		if (capacity < this.count) throw new IllegalArgumentException();
		final int[] oldHeads = this.heads;
		final int oldCapacity = oldHeads.length;
		if (oldCapacity == capacity) return;
		if (capacity == 0) {
			this.entry = 0;
			this.heads = HashData.EMPTY_TABLE;
			this.nexts = HashData.EMPTY_INTEGERS;
			this.keys = HashData.EMPTY_OBJECTS;
			if (this.hashes != null) {
				this.hashes = HashData.EMPTY_INTEGERS;
			}
			if (this.values != null) {
				this.values = HashData.EMPTY_OBJECTS;
			}
		} else {
			final int newMask = IAMMapping.mask(capacity);
			final int oldEntry = this.entry;
			int newEntry = 0;
			final int[] newHeads = new int[newMask + 1];
			final int[] oldNexts = this.nexts;
			final int[] newNexts = new int[capacity];
			final int[] oldHashes = this.hashes;
			final int[] newHashes = oldHashes == null ? null : new int[capacity];
			final Object[] oldKeys = this.keys;
			final Object[] newKeys = new Object[capacity];
			final Object[] oldValues = this.values;
			final Object[] newValues = oldValues == null ? null : new Object[capacity];

			this.setupEntries(newHeads, newNexts);

			for (int i = 0, size = oldHeads.length; i < size; i++) {
				for (int oldIndex = oldHeads[i]; 0 <= oldIndex; oldIndex = oldNexts[oldIndex]) {
					final Object key = oldKeys[oldIndex];
					final int hash = oldHashes != null ? oldHashes[oldIndex] : this.customKeyHash(key);
					final int newIndex = hash & newMask;
					newEntry = newNexts[oldEntry];
					newNexts[oldEntry] = newHeads[newIndex];
					newHeads[newIndex] = oldEntry;
					newKeys[oldEntry] = key;
					if (newHashes != null) {
						newHashes[oldEntry] = hash;
					}
				}
			}
			this.entry = newEntry;
			this.heads = newHashes;
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

	protected Set<GKey> getKeys() {
		return new AbstractSet<GKey>() {

			@Override
			public int size() {
				return HashData.this.count;
			}

			@Override
			public void clear() {
				HashData.this.clearEntries();
			}

			@Override
			public Iterator<GKey> iterator() {
				return HashData.this.getKeysIterator();
			}

			@Override
			public boolean remove(final Object o) {
				return HashData.this.popKey(o);
			}

			@Override
			public boolean contains(final Object o) {
				return HashData.this.getKey(o);
			}

		};
	}

	protected Iterator<GKey> getKeysIterator() {
		return null;
	}

	protected Collection<GValue> getValues() {
		return new AbstractCollection<GValue>() {

			@Override
			public int size() {
				return HashData.this.count;
			}

			@Override
			public void clear() {
				HashData.this.clearEntries();
			}

			@Override
			public Iterator<GValue> iterator() {
				return HashData.this.getValuesIterator();
			}

		};
	}

	protected Iterator<GValue> getValuesIterator() {
		return null;
	}

	protected Set<Entry<GKey, GValue>> getEntries() {
		return new AbstractSet<Entry<GKey, GValue>>() {

			@Override
			public int size() {
				return HashData.this.count;
			}

			@Override
			public void clear() {
				HashData.this.clearEntries();
			}

			@Override
			public Iterator<Entry<GKey, GValue>> iterator() {
				return HashData.this.getEntriesIterator();
			}

			@Override
			public boolean remove(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				final Object key = entry.getKey();
				final int hash = HashData.this.customKeyHash(key);
				final int index = HashData.this.getEntry(key, hash);
				if (index < 0) return false;
				final Object[] values = HashData.this.values;
				if (!Objects.equals(values[index], entry.getValue())) return false;
				HashData.this.popEntry(key, hash);
				values[index] = null;
				return true;
			}

			@Override
			public boolean contains(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				final int index = HashData.this.getEntry(entry.getKey());
				return (index >= 0) && Objects.equals(HashData.this.values[index], entry.getValue());
			}

		};
	}

	protected Iterator<Entry<GKey, GValue>> getEntriesIterator() {
		return null;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code true} zurückw, enn ein solcher Eintrag existiert.
	 *
	 * @see Map#get(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	@SuppressWarnings ("unchecked")
	protected final boolean getKey(final Object key) {
		return 0 <= this.getEntry(key);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	protected final int getEntry(final Object key) {
		return this.getEntry(key, this.customKeyHash(key));
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Streuwert und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @see Map#get(Object)
	 * @param key Schlüssel des Eintrags.
	 * @param hash Streuwert des Schlüssels.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	@SuppressWarnings ("unchecked")
	final int getEntry(final Object key, final int hash) {
		final int[] heads = this.heads;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final int index = hash & (heads.length - 1);
		final int entry = heads[index];
		for (int result = entry; 0 <= result; result = nexts[result]) {
			if (((hashes == null) || (hashes[result] == hash)) && this.customKeyEquals(keys[result], key)) return result;
		}
		return -1;
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

	protected final boolean putKey(final GKey key) {
		final int count = this.count;
		this.putEntry(key);
		return count != this.count;
	}

	protected final int putEntry(final GKey key) {
		final int hash = this.customKeyHash(key), result = this.getEntry(key, hash);
		if (result >= 0) return result;
		final int count = this.count + 1, capacity = this.capacity();
		this.count = count;
		if (count <= capacity) return this.putEntry(key, hash);
		final int allocate = capacity + (capacity >> 1);
		this.allocate(count > allocate ? count : allocate);
		return this.putEntry(key, hash);
	}

	final int putEntry(final GKey key, final int hash) {
		final int[] heads = this.heads;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final int index = hash & (heads.length - 1), result = this.entry;
		this.entry = nexts[result];
		nexts[result] = heads[index];
		heads[index] = result;
		this.keys[result] = key;
		if (hashes == null) return result;
		hashes[result] = hash;
		return result;
	}

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
	 * @see #popEntry(Object)
	 * @param key Schlüssel.
	 * @return {@code true}, wenn ein Eintrag mit dem Schlüssel existierte. */
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
		return this.popEntry(key, this.customKeyHash(key));
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel sowie Streuwert und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @param hash Streuwert des Schlüssels.
	 * @return Index des entfernten Eintrags oder {@code -1}. */
	final int popEntry(final Object key, final int hash) {
		final int[] heads = this.heads;
		final int[] nexts = this.nexts;
		final int[] hashes = this.hashes;
		final Object[] keys = this.keys;
		final int index = hash & (heads.length - 1);
		final int entry = heads[index];
		if (0 <= entry) {
			if (((hashes == null) || (hashes[entry] == hash)) && this.customKeyEquals(keys[entry], key)) {
				keys[entry] = null;
				heads[index] = nexts[entry];
				nexts[entry] = this.entry;
				this.entry = entry;
				this.count--;
				return entry;
			}
			for (int result = nexts[entry]; 0 <= result; result = nexts[result]) {
				if (((hashes == null) || (hashes[result] == hash)) && this.customKeyEquals(keys[result], key)) {
					keys[result] = null;
					nexts[entry] = nexts[result];
					nexts[result] = this.entry;
					this.entry = result;
					this.count--;
					return result;
				}
			}
		}
		return -1;
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt den Wert des Eintrags zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see #popEntry(Object)
	 * @param key Schlüssel.
	 * @return Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue popValue(final GKey key) {
		final int entry = this.popEntry(key);
		if (entry < 0) return null;
		final Object[] values = this.values;
		if (values == null) return null;
		final Object result = values[entry];
		values[entry] = null;
		return (GValue)result;
	}

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des gegebenen Schlüssels zurück.
	 *
	 * @param key Schlüssel.
	 * @return {@link Object#hashCode() Streuwert} des Schlüssels. */
	protected int customKeyHash(final Object key) {
		int hash = Objects.hash(key);
		hash ^= (hash >>> 20) ^ (hash >>> 12);
		return hash ^ (hash >>> 7) ^ (hash >>> 4);
	}

	/** /** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Schlüssel zurück.
	 *
	 * @param thisKey alter bzw. verwalteter Schlüssel.
	 * @param thatKey neuer bzw. gesuchter Schlüssel.
	 * @return Äquivalenz der gegebenen Schlüssel. */
	protected boolean customKeyEquals(final Object thisKey, final Object thatKey) {
		return Objects.equals(thisKey, thatKey);
	}

	// TODO für entry/entryset
	protected int customValueHash(final Object value) {
		return Objects.hash(value);
	}

	protected boolean customValueEquals(final Object thisValue, final Object thatValue) {
		return Objects.equals(thisValue, thatValue);
	}

	/** Diese Methode entfernt alle Einträge. Hierbei werden die Anzahl der Einträge auf {@code 0} gesetzt und die Tabelle mit {@code null} gefüllt. */
	protected final void clearEntries() {
		if (this.count == 0) return;
		this.entry = 0;
		this.count = 0;
		this.setupEntries(this.heads, this.nexts);
		Arrays.fill(this.keys, null);
		final Object[] values = this.values;
		if (values == null) return;
		Arrays.fill(values, null);
	}

	final void setupEntries(final int[] newHeads, final int[] newNexts) {
		for (int i = 0, size = newHeads.length; i < size; i++) {
			newHeads[i] = -1;
		}
		for (int i = 0, size = newNexts.length; i < size;) {
			newNexts[i] = ++i;
		}
	}

}