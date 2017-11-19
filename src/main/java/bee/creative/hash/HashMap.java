package bee.creative.hash;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** Diese Klasse implementiert eine auf {@link HashData} aufbauende {@link Map}.<br>
 * <b>Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Schlüssel erfolgt nicht wie in {@link Map}
 * beschrieben über {@link Object#hashCode()} bzw. {@link Object#equals(Object)}, sondern über {@link #customHash(Object)} bzw.
 * {@link #customEquals(Object, Object)}.</b>
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public class HashMap<GKey, GValue> extends HashData<GKey, GValue> implements Map<GKey, GValue> {

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit {@link #HashMap(int, boolean) Streuwertpuffer} und der Kapazität {@code 0}. */
	public HashMap() {
		super(true, true);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit {@link #HashMap(int, boolean) Streuwertpuffer} und der gegebenen Kapazität.
	 * 
	 * @param capacity Kapazität. */
	public HashMap(final int capacity) {
		super(true, true);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit der gegebenen Kapazität.
	 * 
	 * @param capacity Kapazität.
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashMap(final int capacity, final boolean withHashes) {
		super(true, withHashes);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link HashMap} mit Kapazität {@code 0}.
	 * 
	 * @param withHashes {@code true}, wenn die Streuwerte der Schlüssel in {@link #hashes} gepuffert werden sollen;<br>
	 *        {@code false}, wenn der Streuwerte eines Schlüssels schnell ermittelt werden kann. */
	public HashMap(final boolean withHashes) {
		super(true, withHashes);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.count;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.count == 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsKey(final Object key) {
		return this.getKey(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(final Object value) {
		if (value == null) {
			for (final Iterator<GValue> iterator = this.getValuesIterator(); iterator.hasNext();) {
				if (iterator.next() == null) return true;
			}
		} else {
			for (final Iterator<GValue> iterator = this.getValuesIterator(); iterator.hasNext();) {
				if (value.equals(iterator.next())) return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final Object key) {
		return this.getValue(key);
	}

	/** {@inheritDoc} */
	@Override
	public GValue put(final GKey key, final GValue value) {
		return this.putValue(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public GValue remove(final Object key) {
		return this.popValue(key);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> map) {
		for (final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()) {
			this.putValue(entry.getKey(), entry.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearEntries();
	}

	/** {@inheritDoc} */
	@Override
	public Set<GKey> keySet() {
		return this.getKeys();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<GValue> values() {
		return this.getValues();
	}

	/** {@inheritDoc} */
	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return this.getEntries();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int result = 0;
		for (final Iterator<?> entries = this.getEntriesIterator(); entries.hasNext();) {
			result += entries.next().hashCode();
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Map<?, ?>)) return false;
		final Map<?, ?> that = (Map<?, ?>)object;
		if (that.size() != this.size()) return false;
		for (final Iterator<Entry<GKey, GValue>> entries = this.getEntriesIterator(); entries.hasNext();) {
			final Entry<?, ?> entry = entries.next();
			final Object key = entry.getKey(), value = entry.getValue();
			if (value == null) {
				if (!((that.get(key) == null) && that.containsKey(key))) return false;
			} else {
				if (!value.equals(that.get(key))) return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getMapping().toString();
	}

}