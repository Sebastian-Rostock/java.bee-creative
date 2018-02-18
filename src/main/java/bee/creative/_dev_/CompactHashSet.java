package bee.creative._dev_;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import bee.creative.util.Comparators;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein {@link Object#hashCode() Streuwert} basiertes {@link CompactSet}. Der Speicherverbrauch eines {@link CompactHashSet}s liegt
 * bei ca. {@code 20%} ({@code 64-Bit}) des Speicherverbrauchs eines {@link HashSet}s.
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl
 * im Mittel auf ca. {@code 245%} der Rechenzeit, die ein {@link HashSet} dazu benötigen würde. Bei einer Anzahl von ca. {@code 100} Elementen benötigen Beide
 * {@link Set}s dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das {@link CompactHashSet} schneller, bei mehr Elementen ist das
 * {@link HashSet} schneller. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit beim Hinzufügen
 * und Entfernen von Elementen in einem {@link CompactHashSet} auf {@code 8827%} der Rechenzeit, die ein {@link HashSet} hierfür benötigen würde.
 * <p>
 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt das {@link CompactHashSet} im Mittel nur noch {@code 75%} der Rechenzeit des
 * {@link HashSet}, unabhängig von der Anzahl der Elemente.
 *
 * @see Object#hashCode()
 * @see Object#equals(Object)
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente.
 * @deprecated {@link bee.creative.util.HashSet} */
public class CompactHashSet<GItem> extends CompactSet<GItem> {

	/** Dieser Konstruktor initialisiert das {@link Set}. */
	public CompactHashSet() {
		super();
	}

	/** Dieser Konstruktor initialisiert das {@link Set} mit der gegebenen Kapazität.
	 *
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität. */
	public CompactHashSet(final int capacity) {
		super(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link Set} mit den gegebenen Elementen.
	 *
	 * @see CompactData#allocate(int)
	 * @see Set#addAll(Collection)
	 * @param collection Elemente. */
	public CompactHashSet(final Collection<? extends GItem> collection) {
		super(collection);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected int customItemIndex(final Object item) {
		if (item == null) return this.defaultItemIndexEquals(null, 0);
		return this.defaultItemIndexEquals(item, Objects.hash(item));
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customItemEquals(final Object key, final int hash, final Object item) {
		if (key == null) return item == null;
		return key.equals(item);
	}

	/** {@inheritDoc} */
	@Override
	protected int customItemCompare(final Object key, final int hash, final Object item) {
		if (item == null) return hash;
		return Comparators.compare(hash, item.hashCode());
	}

}