package bee.creative.compact;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap}, deren Schlüssel und Werte in je einem Array
 * verwaltet werden. Der Speicherverbrauch einer {@link CompactNavigableEntryMap} liegt bei ca. {@code 28%} des
 * Speicherverbrauchs einer {@link TreeMap}.
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen
 * sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 160%} der Rechenzeit, die eine {@link TreeMap}
 * dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen)
 * steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer {@link CompactNavigableEntryMap} auf ca.
 * {@code 1050%} der Rechenzeit, die eine {@link TreeMap} hierfür benötigen würde.
 * <p>
 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map}s in etwa die gleichen
 * Rechenzeiten, unabhängig von der Anzahl der Elemente.
 * 
 * @see CompactMap#getKey(Object)
 * @see CompactMap#setKey(Object, Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public class CompactNavigableEntryMap<GKey, GValue> extends CompactNavigableMap<GKey, GValue> {

	/**
	 * Dieses Feld speichert die Werte.
	 */
	protected Object[] values = CompactData.VOID;

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit dem gegebenen {@link Comparator}.
	 * 
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public CompactNavigableEntryMap(final Comparator<? super GKey> comparator) throws NullPointerException {
		super(comparator);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit der gegebenen Kapazität und dem gegebenen {@link Comparator}.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public CompactNavigableEntryMap(final int capacity, final Comparator<? super GKey> comparator)
		throws NullPointerException {
		super(capacity, comparator);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit den gegebenen Elementen und dem gegebenen {@link Comparator}.
	 * 
	 * @see CompactData#allocate(int)
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist.
	 */
	public CompactNavigableEntryMap(final Map<? extends GKey, ? extends GValue> map,
		final Comparator<? super GKey> comparator) throws NullPointerException {
		super(map, comparator);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final GKey getKey(final GValue value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void setKey(final GKey key, final GValue value) {
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
	@SuppressWarnings ("unchecked")
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		return this.comparator.compare((GKey)key, (GKey)item);
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