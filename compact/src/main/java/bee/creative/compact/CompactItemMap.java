package bee.creative.compact;

import java.util.Map;

/**
 * Diese Klasse implementiert eine abstrakte {@link CompactMap}, deren Werte in einem Array verwaltet werden und ihren
 * Schlüssel selbst referenzieren. Diese Implementation erlaubt deshalb {@code null} nicht als Wert.
 * 
 * @see CompactMap#getKey(Object)
 * @see CompactMap#setKey(Object, Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public abstract class CompactItemMap<GKey, GValue> extends CompactMap<GKey, GValue> {

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map}.
	 */
	public CompactItemMap() {
		super();
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactItemMap(final int capacity) {
		super(capacity);
	}

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
		return (GValue)this.list[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void setEntry(final int index, final GKey key, final GValue value) {
		if(value == null) throw new NullPointerException();
		this.list[index] = value;
		this.setKey(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue put(final GKey key, final GValue value) {
		if(value == null) throw new NullPointerException();
		return super.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(final Object value) {
		if(value == null) return false;
		return CompactData.indexOf(this.list, this.from, this.size, value) >= 0;
	}

}