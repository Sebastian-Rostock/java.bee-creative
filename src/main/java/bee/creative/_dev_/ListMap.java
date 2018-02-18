package bee.creative._dev_;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import bee.creative.util.Filter;
import bee.creative.util.Objects;

// TODO
class ListMap<GKey, GValue> extends ListData<GKey, GValue> implements Map<GKey, GValue> {

	public ListMap() {
		super(true);
	}

	public ListMap(final int capacity) {
		super(true);
		this.allocateImpl(capacity);
	}

	public static <GKey, GValue> Map<GKey, GValue> from(final Filter<Object> filter, final Comparator<? super GKey> comparator) {
		return new java.util.TreeMap(comparator);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.OKAY_countImpl();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.OKAY_countImpl() == 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsKey(final Object key) {
		return this.OKAY_hasKeyImpl(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(final Object value) {
		return this.OKAY_hasValueImpl(value);
	}

	/** {@inheritDoc} */
	@Override
	public GValue get(final Object key) {
		return this.OKAY_getImpl(key);
	}

	/** {@inheritDoc} */
	@Override
	public GValue put(final GKey key, final GValue value) {
		return this.putImpl(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public GValue remove(final Object key) {
		return this.popImpl(key);
	}

	/** {@inheritDoc} */
	@Override
	public void putAll(final Map<? extends GKey, ? extends GValue> m) {
		for (final Map.Entry<? extends GKey, ? extends GValue> entry: m.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.clearImpl();
	}

	/** {@inheritDoc} */
	@Override
	public Set<GKey> keySet() {
		return this.newKeysAImpl();
	}

	/** {@inheritDoc} */
	@Override
	public Collection<GValue> values() {
		return this.newValuesAImpl();
	}

	/** {@inheritDoc} */
	@Override
	public Set<Entry<GKey, GValue>> entrySet() {
		return this.newEntriesAImpl();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.newMappingImpl().hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Map<?, ?>)) return false;
		final Map<?, ?> that = (Map<?, ?>)object;
		if (that.size() != this.size()) return false;
		for (final Entry<?, ?> entry: that.entrySet()) {
			final int entryIndex = this.OKAY_getIndexImpl(entry.getKey());
			if (entryIndex < 0) return false;
			if (!Objects.equals(this.OKAY_getValueImpl(entryIndex), entry.getValue())) return false;
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.newMappingImpl().toString();
	}

}
