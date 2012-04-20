package bee.creative.compact;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert ein abstraktes {@link Set}, dessen Daten in einem Array verwaltet werden.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente.
 */
public abstract class CompactSet<GItem> extends CompactCollection<GItem> implements Set<GItem> {

	/**
	 * Diese Klasse implementiert ein {@link AbstractSet}, das seine Schnittstelle an ein gegebenes {@link Set} delegiert.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	protected static final class ItemSet<GItem> extends AbstractSet<GItem> {

		/**
		 * Dieses Feld speichert das {@link Set}.
		 */
		protected final Set<GItem> data;

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set}.
		 * 
		 * @param data {@link Set}.
		 */
		public ItemSet(final Set<GItem> data) {
			this.data = data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return this.data.iterator();
		}

	}

	/**
	 * Dieser Konstrukteur initialisiert das {@link Set}.
	 */
	public CompactSet() {
	}

	/**
	 * Dieser Konstrukteur initialisiert das {@link Set} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactSet(final int capacity) {
		this.allocate(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen.
	 * 
	 * @see Set#addAll(Collection)
	 * @see CompactData#allocate(int)
	 * @param collection Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
	 */
	public CompactSet(final Collection<? extends GItem> collection) {
		if(collection == null) throw new NullPointerException("collection is null");
		this.allocate(collection.size());
		this.addAll(collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GItem> iterator() {
		return new CompactCollectionAscendingIterator<GItem>(this, this.firstIndex(), this.lastIndex() + 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(final GItem item) {
		final int index = this.customItemIndex(item);
		if(index >= 0) return false;
		final int i = this.from - index - 1;
		this.customInsert(i + this.from, 1);
		this.setItem(i + this.from, item);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(final Collection<? extends GItem> collection) {
		return Iterables.appendAll(this, collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return new CompactSet.ItemSet<GItem>(this).toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		return new CompactSet.ItemSet<GItem>(this).toArray(a);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return new CompactSet.ItemSet<GItem>(this).hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof Set<?>)) return false;
		return new CompactSet.ItemSet<GItem>(this).equals(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new CompactSet.ItemSet<GItem>(this).toString();
	}

}