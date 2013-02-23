package bee.creative.compact;

import java.util.Comparator;
import java.util.Map;

/**
 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap}, deren Daten in einem Array verwaltet werden und ihren Schlüssel selbst referenzieren. Diese Implementation erlaubt deshalb {@code null} nicht als Wert.
 * 
 * @see CompactNavigableItemMap#getKey(Object)
 * @see CompactNavigableItemMap#setKey(Object, Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public abstract class CompactNavigableItemMap<GKey, GValue> extends CompactNavigableMap<GKey, GValue> {

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit dem gegebenen {@link Comparator}.
	 * 
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public CompactNavigableItemMap(final Comparator<? super GKey> comparator) throws NullPointerException {
		super(comparator);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität und dem gegebenen {@link Comparator}.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @param comparator {@link Comparator}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public CompactNavigableItemMap(final int capacity, final Comparator<? super GKey> comparator) throws IllegalArgumentException, NullPointerException {
		super(capacity, comparator);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen und dem gegebenen {@link Comparator}.
	 * 
	 * @see CompactData#allocate(int)
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} bzw. die gegebene {@link Map} {@code null} ist.
	 */
	public CompactNavigableItemMap(final Map<? extends GKey, ? extends GValue> map, final Comparator<? super GKey> comparator) throws NullPointerException {
		super(map, comparator);
	}

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
		if(value == null) throw new NullPointerException();
		this.items.set(index, value);
		this.setKey(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		return this.comparator.compare((GKey)key, this.getKey((GValue)item));
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
		return this.items.values().contains(value);
	}

}