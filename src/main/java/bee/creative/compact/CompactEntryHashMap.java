package bee.creative.compact;

import java.util.HashMap;
import java.util.Map;
import bee.creative.util.Comparators;

/**
 * Diese Klasse implementiert eine {@link Object#hashCode() Streuwert} basiertes {@link CompactEntryMap}. Der Speicherverbrauch einer
 * {@link CompactEntryHashMap} liegt bei ca. {@code 28%} des Speicherverbrauchs einer {@link HashMap}.
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl
 * im Mittel auf ca. {@code 150%}. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit beim
 * Hinzufügen und Entfernen von Elementen in einer {@link CompactEntryHashMap} auf {@code 760%} der Rechenzeit, die eine {@link HashMap} hierfür benötigen
 * würde.
 * <p>
 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt beide {@link Map}s in etwa die gleichen Rechenzeiten, unabhängig von der Anzahl der
 * Elemente.
 * 
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GKey> Typ der Schlüssel.
 * @param <GValue> Typ der Werte.
 */
public class CompactEntryHashMap<GKey, GValue> extends CompactEntryMap<GKey, GValue> {

	/**
	 * Dieser Konstruktor initialisiert die {@link Map}.
	 */
	public CompactEntryHashMap() {
		super();
	}

	/**
	 * Dieser Konstruktor initialisiert die {@link Map} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactEntryHashMap(final int capacity) {
		super(capacity);
	}

	/**
	 * Dieser Konstruktor initialisiert die {@link Map} mit den gegebenen Elementen.
	 * 
	 * @see Map#putAll(Map)
	 * @param map Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Map} {@code null} ist.
	 */
	public CompactEntryHashMap(final Map<? extends GKey, ? extends GValue> map) {
		super(map);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customItemIndex(final Object key) {
		if (key == null) return this.defaultEqualsIndex(null, 0);
		return this.defaultEqualsIndex(key, key.hashCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean customItemEquals(final Object key, final int hash, final Object item) {
		if (key == null) return item == null;
		return key.equals(item);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		if (item == null) return hash;
		return Comparators.compare(hash, item.hashCode());
	}

}