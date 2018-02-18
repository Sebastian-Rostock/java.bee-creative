package bee.creative._dev_;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert ein abstraktes {@link Set}, dessen Daten in einem Array verwaltet werden.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class CompactSet<GItem> extends CompactCollection<GItem> implements Set<GItem> {

	/** Diese Klasse implementiert das {@link AbstractSet} eines {@link Set}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ den Elemente. */
	protected static final class CompactSetItems<GItem> extends AbstractSet<GItem> {

		/** Dieses Feld speichert das {@link Set}. */
		protected final Set<GItem> set;

		/** Dieser Konstruktor initialisiert das {@link Set}.
		 *
		 * @param set {@link Set}.
		 * @throws NullPointerException Wennd as gegebene {@link Set} {@code null} ist. */
		public CompactSetItems(final Set<GItem> set) throws NullPointerException {
			this.set = Objects.assertNotNull(set);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this.set.size();
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> iterator() {
			return this.set.iterator();
		}

	}

	{}

	/** Dieser Konstruktor initialisiert das {@link Set}. */
	public CompactSet() {
	}

	/** Dieser Konstruktor initialisiert das {@link Set} mit der gegebenen Kapazität.
	 *
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität. */
	public CompactSet(final int capacity) {
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link Set} mit den gegebenen Elementen.
	 *
	 * @see Set#addAll(Collection)
	 * @see CompactData#allocate(int)
	 * @param collection Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist. */
	public CompactSet(final Collection<? extends GItem> collection) {
		this.allocate(collection.size());
		this.addAll(collection);
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final boolean add(final GItem item) {
		int index = this.customItemIndex(item);
		if (index >= 0) return false;
		index = -index - 1;
		this.customInsert(index, 1);
		this.setItem(index, item);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean addAll(final Collection<? extends GItem> collection) {
		return Iterables.addAll(this, collection);
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return new CompactSetItems<GItem>(this).hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof Set)) return false;
		return new CompactSetItems<GItem>(this).equals(object);
	}

}