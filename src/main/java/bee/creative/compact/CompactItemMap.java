package bee.creative.compact;

import java.util.Map;

/**
 * Diese Klasse implementiert eine abstrakte {@link CompactMap}, deren Werte in einem Array verwaltet werden und ihren Schlüssel selbst referenzieren. Diese
 * Implementation erlaubt deshalb {@code null} nicht als Wert.
 * 
 * @see CompactItemMap#getKey(Object)
 * @see CompactItemMap#setKey(Object, Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public abstract class CompactItemMap<GKey, GValue> extends CompactMap<GKey, GValue> {

	/**
	 * Dieser Konstruktor initialisiert die {@link Map}.
	 */
	public CompactItemMap() {
		super();
	}

	/**
	 * Dieser Konstruktor initialisiert die {@link Map} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactItemMap(final int capacity) throws IllegalArgumentException {
		super(capacity);
	}

	/**
	 * Dieser Konstruktor initialisiert die {@link Map} mit den gegebenen Elementen.
	 * 
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist.
	 */
	public CompactItemMap(final Map<? extends GKey, ? extends GValue> map) throws NullPointerException {
		super(map);
	}

	{}

	/**
	 * Diese Methode gibt den Schlüssel des gegebenen Werts zurück.
	 * 
	 * @param value Wert.
	 * @return Schlüssel.
	 */
	protected abstract GKey getKey(final GValue value);

	/**
	 * Diese Methode setzt den Schlüssel des gegebenen Werts.
	 * 
	 * @param key Schlüssel.
	 * @param value Wert.
	 */
	protected abstract void setKey(GKey key, final GValue value);

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final GKey getKey(final int index) {
		return this.getKey(this.getValue(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected final GValue getValue(final int index) {
		return (GValue)this.items.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void setEntry(final int index, final GKey key, final GValue value) {
		if (value == null) throw new NullPointerException("value = null");
		this.items.set(index, value);
		this.setKey(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue put(final GKey key, final GValue value) {
		if (value == null) throw new NullPointerException("value = null");
		return super.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(final Object value) {
		if (value == null) return false;
		return this.items.values().contains(value);
	}

}