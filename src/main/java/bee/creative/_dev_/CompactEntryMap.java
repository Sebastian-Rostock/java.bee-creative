package bee.creative._dev_;

import java.util.Map;
import bee.creative.array.Array;

/** Diese Klasse implementiert eine abstrakte {@link Map}, deren Schlüssel und Werte in je einem Array verwaltet werden.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 * @deprecated {@link bee.creative.util.XX_HashMap} */
public abstract class CompactEntryMap<GKey, GValue> extends CompactMap<GKey, GValue> {

	/** Dieses Feld speichert das {@link Array} der Werte. */
	protected final CompactDataArray values;

	/** Dieser Konstruktor initialisiert die {@link Map}. */
	public CompactEntryMap() {
		super();
		this.values = new CompactDataArray();
	}

	/** Dieser Konstruktor initialisiert die {@link Map} mit der gegebenen Kapazität.
	 *
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactEntryMap(final int capacity) throws IllegalArgumentException {
		this();
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link Map} mit den gegebenen Elementen.
	 *
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist. */
	public CompactEntryMap(final Map<? extends GKey, ? extends GValue> map) {
		this();
		this.allocate(map.size());
		this.putAll(map);
	}

	{}

	/** {@inheritDoc} */
	@SuppressWarnings ("unchecked")
	@Override
	protected GKey customGetKey(final int index) {
		return (GKey)this.items.get(index);
	}

	/** {@inheritDoc} */
	@SuppressWarnings ("unchecked")
	@Override
	protected GValue customGetValue(final int index) {
		return (GValue)this.values.get(index);
	}

	/** {@inheritDoc} */
	@Override
	protected void customSetEntry(final int index, final GKey key, final GValue value) {
		this.items.set(index, key);
		this.values.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		super.customInsert(index, count);
		this.values.insert(index, count);
	}

	/** {@inheritDoc} */
	@Override
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		super.customRemove(index, count);
		this.values.remove(index, count);
	}

	/** {@inheritDoc} */
	@Override
	protected void customAllocate(final int count) {
		super.customAllocate(count);
		this.values.allocate(count);
	}

	/** {@inheritDoc} */
	@Override
	protected void customCompact() {
		super.customCompact();
		this.values.compact();
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsValue(final Object value) {
		return this.values.values().contains(value);
	}

}