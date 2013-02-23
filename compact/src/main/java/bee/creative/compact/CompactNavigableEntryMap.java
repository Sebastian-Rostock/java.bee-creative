package bee.creative.compact;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import bee.creative.array.Array;

/**
 * Diese Klasse implementiert eine abstrakte {@link CompactNavigableMap}, deren Schlüssel und Werte in je einem Array verwaltet werden. Der Speicherverbrauch einer {@link CompactNavigableEntryMap} liegt bei ca. {@code 28%} des Speicherverbrauchs einer {@link TreeMap}.
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl im Mittel auf ca. {@code 160%} der Rechenzeit, die eine {@link TreeMap} dazu benötigen würde. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit beim Hinzufügen und Entfernen von Elementen in einer {@link CompactNavigableEntryMap} auf ca. {@code 1050%} der Rechenzeit, die eine {@link TreeMap} hierfür benötigen würde.
 * <p>
 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map}s in etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der Elemente.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public class CompactNavigableEntryMap<GKey, GValue> extends CompactNavigableMap<GKey, GValue> {

	/**
	 * Dieses Feld speichert das {@link Array} der Werte.
	 */
	protected final CompactDataArray values;

	/**
	 * Dieser Konstrukteur initialisiert die {@link Map} mit dem gegebenen {@link Comparator}.
	 * 
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} bzw. die gegebene {@link Map} {@code null} ist.
	 */
	public CompactNavigableEntryMap(final Comparator<? super GKey> comparator) throws NullPointerException {
		super(comparator);
		this.values = new CompactDataArray();
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
	public CompactNavigableEntryMap(final int capacity, final Comparator<? super GKey> comparator) throws IllegalArgumentException, NullPointerException {
		this(comparator);
		this.allocate(capacity);
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
	public CompactNavigableEntryMap(final Map<? extends GKey, ? extends GValue> map, final Comparator<? super GKey> comparator) throws NullPointerException {
		this(comparator);
		if(map == null) throw new NullPointerException("map is null");
		this.allocate(map.size());
		this.putAll(map);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected GKey getKey(final int index) {
		return (GKey)this.items.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings ("unchecked")
	@Override
	protected GValue getValue(final int index) {
		return (GValue)this.values.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setEntry(final int index, final GKey key, final GValue value) {
		this.items.set(index, key);
		this.values.set(index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		super.customInsert(index, count);
		this.values.insert(index, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		super.customRemove(index, count);
		this.values.remove(index, count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customAllocate(final int count) {
		super.customAllocate(count);
		this.values.allocate(count);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void customCompact() {
		super.customCompact();
		this.values.compact();
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
	public boolean containsValue(final Object value) {
		return this.values.values().contains(value);
	}

}