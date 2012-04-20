package bee.creative.compact;

import java.util.Map;

/**
 * Diese Klasse implementiert eine abstrakte {@link Map}, deren Schlüssel und Werte in je einem Array verwaltet werden.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public abstract class CompactEntryMap<GKey, GValue> extends CompactMap<GKey, GValue> {

	/**
	 * Dieses Feld speichert die Werte.
	 */
	protected Object[] values = CompactData.VOID;

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map}.
	 */
	public CompactEntryMap() {
		super();
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactEntryMap(final int capacity) {
		super(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen.
	 * 
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 */
	public CompactEntryMap(final Map<? extends GKey, ? extends GValue> map) {
		super(map);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GKey getKey(final GValue value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setKey(final GKey key, final GValue value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected GKey getKey(final int index) {
		return (GKey)this.list[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected GValue getValue(final int index) {
		return (GValue)this.values[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setEntry(final int index, final GKey key, final GValue value) {
		this.list[index] = key;
		this.values[index] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		final int from = this.from;
		final int size = this.size;
		this.list = this.defaultInsert(this.list, index, count);
		this.from = from;
		this.size = size;
		this.values = this.defaultInsert(this.values, index, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		final int from = this.from;
		final int size = this.size;
		this.list = this.defaultRemove(this.list, index, count);
		this.from = from;
		this.size = size;
		this.values = this.defaultRemove(this.values, index, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customAllocate(final int count) {
		final int from = this.from;
		final int length = this.defaultLength(this.list, count);
		this.list = this.defaultResize(this.list, length);
		this.from = from;
		this.values = this.defaultResize(this.values, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customCompact() {
		final int from = this.from;
		final int length = this.size;
		this.list = this.defaultResize(this.list, length);
		this.from = from;
		this.values = this.defaultResize(this.values, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(final Object value) {
		return CompactData.indexOf(this.values, this.from, this.size, value) >= 0;
	}

}