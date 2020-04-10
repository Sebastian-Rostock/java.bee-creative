package bee.creative.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/** Diese Klasse implementiert eine auf {@link AbstractHashData} aufbauende {@link Map} mit geringem {@link AbstractHashData Speicherverbrauch}.
 * <p>
 * <b>Achtung:</b> Die Ermittlung von {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object) Äquivalenz} der Schlüssel erfolgt nicht wie in
 * {@link Map} beschrieben über die Methoden der Schlüssel, sondern über die Methoden {@link #customHash(Object)} bzw. {@link #customEqualsKey(int, Object)}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://cureativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte. */
public abstract class AbstractHashMap<GKey, GValue> extends AbstractHashData<GKey, GValue> implements Map<GKey, GValue> {

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	public void allocate(final int capacity) throws IllegalArgumentException {
		this.allocateImpl(capacity);
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreservierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.capacityImpl();
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public void compact() {
		this.allocateImpl(this.countImpl());
	}

	@Override
	public int size() {
		return this.countImpl();
	}

	@Override
	public boolean isEmpty() {
		return this.countImpl() == 0;
	}

	@Override
	public boolean containsKey(final Object key) {
		return this.hasKeyImpl(key);
	}

	@Override
	public boolean containsValue(final Object value) {
		return this.hasValueImpl(value);
	}

	@Override
	public GValue get(final Object key) {
		return this.getImpl(key);
	}

	@Override
	public GValue put(final GKey key, final GValue value) {
		return this.putImpl(key, value);
	}

	@Override
	public GValue remove(final Object key) {
		return this.popImpl(key);
	}

	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> map) {
		for (final Entry<? extends GKey, ? extends GValue> entry: map.entrySet()) {
			this.putImpl(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		this.clearImpl();
	}

	@Override
	public Set<GKey> keySet() {
		return this.newKeysImpl();
	}

	@Override
	public Collection<GValue> values() {
		return this.newValuesImpl();
	}

	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return this.newEntriesImpl();
	}

	@Override
	public int hashCode() {
		return this.newEntriesImpl().hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Map<?, ?>)) return false;
		final Map<?, ?> that = (Map<?, ?>)object;
		if (that.size() != this.size()) return false;
		for (final Entry<?, ?> entry: that.entrySet()) {
			if (!this.hasEntryImpl(entry.getKey(), entry.getValue())) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.newMappingImpl().toString();
	}

}