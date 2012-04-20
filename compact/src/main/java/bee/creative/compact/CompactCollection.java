package bee.creative.compact;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert eine abstrakte {@link Collection}, deren Elemente in einem Array verwaltet werden.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente.
 */
public abstract class CompactCollection<GItem> extends CompactData implements Collection<GItem> {

	/**
	 * Diese Klasse implementiert den aufsteigenden {@link Iterator} für {@link CompactCollection}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	protected static final class CompactCollectionAscendingIterator<GItem> extends
		CompactDataAscendingIterator<GItem, CompactCollection<GItem>> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactCollection} und Indizes.
		 * 
		 * @param data {@link CompactCollection}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactCollectionAscendingIterator(final CompactCollection<GItem> data, final int from, final int last) {
			super(data, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GItem next(final int index) {
			return this.data.getItem(index);
		}

	}

	/**
	 * Diese Klasse implementiert den absteigenden {@link Iterator} für {@link CompactCollection}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	protected static final class CompactCollectionDescendingIterator<GItem> extends
		CompactDataDescendingIterator<GItem, CompactCollection<GItem>> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactCollection} und Indizes.
		 * 
		 * @param data {@link CompactCollection}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactCollectionDescendingIterator(final CompactCollection<GItem> data, final int from, final int last) {
			super(data, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected GItem next(final int index) {
			return this.data.getItem(index);
		}

	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Collection}.
	 */
	public CompactCollection() {
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Collection} mit der gegebenen Kapazität.
	 * 
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 */
	public CompactCollection(final int capacity) {
		this.allocate(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert die {@link Collection} mit den gegebenen Elementen.
	 * 
	 * @see Collection#addAll(Collection)
	 * @see CompactData#allocate(int)
	 * @param collection Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
	 */
	public CompactCollection(final Collection<? extends GItem> collection) {
		if(collection == null) throw new NullPointerException("collection is null");
		this.allocate(collection.size());
		this.addAll(collection);
	}

	/**
	 * Diese Methode gibt das {@code index}-te Element zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Element.
	 */
	@SuppressWarnings ("unchecked")
	protected final GItem getItem(final int index) {
		return (GItem)this.list[index];
	}

	/**
	 * Diese Methode setzt das {@code index}-te Element.
	 * 
	 * @param index Index.
	 * @param item Element.
	 */
	protected final void setItem(final int index, final GItem item) {
		this.list[index] = item;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.size;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.customRemove(this.from, this.size);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.size == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(final Object item) {
		final int index = this.customItemIndex(item);
		if(index < 0) return false;
		this.customRemove(index, 1);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final Object key) {
		return this.customItemIndex(key) >= 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean retainAll(final Collection<?> collection) {
		return Iterables.retainAll((Iterable<?>)this, collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeAll(final Collection<?> collection) {
		return Iterables.removeAll((Iterable<?>)this, collection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(final Collection<?> collection) {
		return Iterables.containsAll(this, collection);
	}

}