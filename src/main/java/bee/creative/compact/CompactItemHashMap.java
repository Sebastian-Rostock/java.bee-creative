package bee.creative.compact;

import java.util.Map;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactItemMap}.
 *
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 * @deprecated {@link bee.creative.util.HashMap} */
public abstract class CompactItemHashMap<GKey, GValue> extends CompactItemMap<GKey, GValue> {

	/** Dieser Konstruktor initialisiert die {@link Map}. */
	public CompactItemHashMap() {
		super();
	}

	/** Dieser Konstruktor initialisiert die {@link Map} mit der gegebenen Kapazität.
	 *
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactItemHashMap(final int capacity) throws IllegalArgumentException {
		super(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link Map} mit den gegebenen Elementen.
	 *
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist. */
	public CompactItemHashMap(final Map<? extends GKey, ? extends GValue> map) throws NullPointerException {
		super(map);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int customItemIndex(final Object key) {
		if (key == null) return this.defaultItemIndexEquals(null, 0);
		return this.defaultItemIndexEquals(key, key.hashCode());
	}

	/** {@inheritDoc} */
	@SuppressWarnings ("unchecked")
	@Override
	protected boolean customItemEquals(final Object key, final int hash, final Object item) {
		if (key == null) return this.customGetKey((GValue)item) == null;
		return key.equals(this.customGetKey((GValue)item));
	}

	/** {@inheritDoc} */
	@SuppressWarnings ("unchecked")
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		final Object value = this.customGetKey((GValue)item);
		if (value == null) return hash;
		return Comparators.compare(hash, value.hashCode());
	}

}