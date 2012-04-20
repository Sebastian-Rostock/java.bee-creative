package bee.creative.compact;

import java.util.Map;
import bee.creative.util.Comparators;

/**
 * Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactItemMap}.
 * 
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public abstract class CompactItemHashMap<GKey, GValue> extends CompactItemMap<GKey, GValue> {

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map}.
	 */
	public CompactItemHashMap() {
		super();
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactItemHashMap(final int capacity) {
		super(capacity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customItemIndex(final Object key) {
		if(key == null) return this.equalsIndex(null, 0);
		return this.equalsIndex(key, key.hashCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected boolean customItemEquals(final Object key, final int hash, final Object item) {
		if(key == null) return this.getKey((GValue)item) == null;
		return key.equals(this.getKey((GValue)item));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		final Object value = this.getKey((GValue)item);
		if(value == null) return hash;
		return Comparators.compare(hash, value.hashCode());
	}

}