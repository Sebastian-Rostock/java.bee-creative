package bee.creative._dev_;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert eine abstrakte {@link Collection}, deren Elemente in einem Array verwaltet werden.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
abstract class CompactCollection<GItem> extends CompactData implements Collection<GItem> {

	/** Diese Klasse implementiert den aufsteigenden {@link Iterator} für {@link CompactCollection}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	protected static final class CompactCollectionAscendingIterator<GItem> extends CompactDataAscendingIterator<GItem, CompactCollection<GItem>> {

		/** Dieser Konstruktor initialisiert {@link CompactCollection} und Indizes.
		 *
		 * @param data {@link CompactCollection}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv). */
		public CompactCollectionAscendingIterator(final CompactCollection<GItem> data, final int from, final int last) {
			super(data, from, last);
		}

		/** {@inheritDoc} */
		@Override
		protected GItem customNext(final int index) {
			return this.data.getItem(index);
		}

	}

	/** Diese Klasse implementiert den absteigenden {@link Iterator} für {@link CompactCollection}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	protected static final class CompactCollectionDescendingIterator<GItem> extends CompactDataDescendingIterator<GItem, CompactCollection<GItem>> {

		/** Dieser Konstruktor initialisiert {@link CompactCollection} und Indizes.
		 *
		 * @param data {@link CompactCollection}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv). */
		public CompactCollectionDescendingIterator(final CompactCollection<GItem> data, final int from, final int last) {
			super(data, from, last);
		}

		/** {@inheritDoc} */
		@Override
		protected GItem customNext(final int index) {
			return this.data.getItem(index);
		}

	}

	/** Dieser Konstruktor initialisiert die {@link Collection}. */
	public CompactCollection() {
		super();
	}

	/** Dieser Konstruktor initialisiert die {@link Collection} mit der gegebenen Kapazität.
	 *
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität. */
	public CompactCollection(final int capacity) {
		super();
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert die {@link Collection} mit den gegebenen Elementen.
	 *
	 * @see Collection#addAll(Collection)
	 * @see CompactData#allocate(int)
	 * @param collection Elemente.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist. */
	public CompactCollection(final Collection<? extends GItem> collection) {
		super();
		this.allocate(collection.size());
		this.addAll(collection);
	}

	/** Diese Methode gibt das {@code index}-te Element zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Element. */
	@SuppressWarnings ("unchecked")
	protected final GItem getItem(final int index) {
		return (GItem)this.items.get(index);
	}

	/** Diese Methode setzt das {@code index}-te Element.
	 *
	 * @param index Index.
	 * @param item {@code index}-tes Element. */
	protected final void setItem(final int index, final GItem item) {
		this.items.set(index, item);
	}

	/** Diese Methode kopiert die Werte des gegebenen Arrays an die gegebene Position.
	 *
	 * @param index Index.
	 * @param items {@code index}-tes Elemente. */
	protected final void setItems(final int index, final Object[] items) {
		this.items.set(index, items);
	}

	/** {@inheritDoc} */
	@Override
	public final int size() {
		return this.items.size();
	}

	/** {@inheritDoc} */
	@Override
	public final void clear() {
		this.customRemove(0, this.items.size());
	}

	/** {@inheritDoc} */
	@Override
	public final boolean isEmpty() {
		return this.items.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<GItem> iterator() {
		return new CompactCollectionAscendingIterator<>(this, 0, this.size());
	}

	/** {@inheritDoc} */
	@Override
	public final boolean remove(final Object item) {
		final int index = this.customItemIndex(item);
		if (index < 0) return false;
		this.customRemove(index, 1);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean contains(final Object item) {
		return this.customItemIndex(item) >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean retainAll(final Collection<?> collection) {
		if (this.isEmpty()) return false;
		return Iterables.retainAll((Iterable<?>)this, collection);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean removeAll(final Collection<?> collection) {
		if (this.isEmpty()) return false;
		return Iterables.removeAll((Iterable<?>)this, collection);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean containsAll(final Collection<?> collection) {
		return Iterables.containsAll(this, collection);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.items.values().toString();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return this.items.values().toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(final T[] a) {
		return this.items.values().toArray(a);
	}

}