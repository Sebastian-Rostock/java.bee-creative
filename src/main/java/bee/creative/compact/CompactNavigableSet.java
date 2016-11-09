package bee.creative.compact;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link NavigableSet}, dessen Daten in einem Array verwaltet werden. Der Speicherverbrauch eines {@link CompactNavigableSet}
 * liegt bei ca. {@code 13%} des Speicherverbrauchs eines {@link TreeSet}s.
 * <p>
 * Die Rechenzeiten beim Hinzufügen und Entfernen von Elementen sind von der Anzahl der Elemente abhängig und erhöhen sich bei einer Verdoppelung dieser Anzahl
 * im Mittel auf ca. {@code 208%} der Rechenzeit, die ein {@link TreeSet} dazu benötigen würde. Bei einer Anzahl von ca. {@code 8000} Elementen benötigen Beide
 * {@link NavigableSet} dafür in etwa die gleichen Rechenzeiten. Bei weniger Elementen ist das {@link CompactNavigableSet} schneller, bei mehr Elementen ist das
 * {@link TreeSet} schneller. Bei der erhöhung der Anzahl der Elemente auf das {@code 32}-fache ({@code 5} Verdopplungen) steigt die Rechenzeit beim Hinzufügen
 * und Entfernen von Elementen in einem {@link CompactNavigableSet} auf ca. {@code 3900%} der Rechenzeit, die ein {@link TreeSet} hierfür benötigen würde.
 * <p>
 * Für das Finden von Elementen und das Iterieren über die Elemente benötigt das {@link CompactNavigableSet} im Mittel nur noch {@code 25%} bzw. {@code 75%} der
 * Rechenzeit des {@link TreeSet}s, unabhängig von der Anzahl der Elemente.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
@SuppressWarnings ("javadoc")
public class CompactNavigableSet<GItem> extends CompactSet<GItem> implements NavigableSet<GItem> {

	/** Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactNavigableSet}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	protected static abstract class CompactNavigableSubSet<GItem> extends CompactSubData<CompactNavigableSet<GItem>> implements NavigableSet<GItem> {

		/** Dieser Konstruktor initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
		 *
		 * @param set {@link CompactNavigableSet}.
		 * @param fromItem erstes Element oder {@link CompactSubData#_open_}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#_open_}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist. */
		public CompactNavigableSubSet(final CompactNavigableSet<GItem> set, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) throws IllegalArgumentException {
			super(set, fromItem, fromInclusive, lastItem, lastInclusive);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this._countItems_();
		}

		/** {@inheritDoc} */
		@Override
		public void clear() {
			this._clearItems_();
		}

		/** {@inheritDoc} */
		@Override
		public boolean isEmpty() {
			return this._countItems_() == 0;
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> subSet(final GItem fromElement, final GItem toElement) {
			return this.subSet(fromElement, true, toElement, false);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> headSet(final GItem toElement) {
			return this.headSet(toElement, false);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> tailSet(final GItem fromElement) {
			return this.tailSet(fromElement, true);
		}

		/** {@inheritDoc} */
		@Override
		public boolean add(final GItem item) {
			if (!this._isInRange_(item)) throw new IllegalArgumentException("item out of range");
			return this._data_.add(item);
		}

		/** {@inheritDoc} */
		@Override
		public boolean addAll(final Collection<? extends GItem> collection) {
			return Iterables.appendAll(this, collection);
		}

		/** {@inheritDoc} */
		@Override
		public boolean retainAll(final Collection<?> collection) {
			return Iterables.retainAll((Iterable<?>)this, collection);
		}

		/** {@inheritDoc} */
		@Override
		public boolean remove(final Object item) {
			if (!this._isInRange_(item)) return false;
			return this._data_.remove(item);
		}

		/** {@inheritDoc} */
		@Override
		public boolean removeAll(final Collection<?> collection) {
			return Iterables.removeAll((Iterable<?>)this, collection);
		}

		/** {@inheritDoc} */
		@Override
		public boolean contains(final Object item) {
			return this._isInRange_(item) && this._data_.contains(item);
		}

		/** {@inheritDoc} */
		@Override
		public boolean containsAll(final Collection<?> collection) {
			return Iterables.containsAll(this, collection);
		}

		/** {@inheritDoc} */
		@Override
		public Object[] toArray() {
			return new CompactSetItems<GItem>(this).toArray();
		}

		/** {@inheritDoc} */
		@Override
		public <T> T[] toArray(final T[] a) {
			return new CompactSetItems<GItem>(this).toArray(a);
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return new CompactSetItems<GItem>(this).hashCode();
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof Set<?>)) return false;
			return new CompactSetItems<GItem>(this).equals(object);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return new CompactSetItems<GItem>(this).toString();
		}

	}

	/** Diese Klasse implementiert die aufsteigende Teilmenge eines {@link CompactNavigableSet}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	protected static final class CompactAscendingSubSet<GItem> extends CompactNavigableSubSet<GItem> {

		/** Dieser Konstruktor initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
		 *
		 * @param array {@link CompactNavigableSet}.
		 * @param fromItem erstes Element oder {@link CompactSubData#_open_}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#_open_}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist. */
		public CompactAscendingSubSet(final CompactNavigableSet<GItem> array, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) throws IllegalArgumentException {
			super(array, fromItem, fromInclusive, lastItem, lastInclusive);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> iterator() {
			return new CompactCollectionAscendingIterator<GItem>(this._data_, this._firstIndex_(), this._lastIndex_() + 1);
		}

		/** {@inheritDoc} */
		@Override
		public Comparator<? super GItem> comparator() {
			return this._data_.comparator;
		}

		/** {@inheritDoc} */
		@Override
		public GItem first() {
			return this._data_.getItemOrException(this._lowestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public GItem last() {
			return this._data_.getItemOrException(this._highestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public GItem lower(final GItem entry) {
			return this._data_.getItemOrNull(this._lowerIndex_(entry));
		}

		/** {@inheritDoc} */
		@Override
		public GItem floor(final GItem entry) {
			return this._data_.getItemOrNull(this._floorIndex_(entry));
		}

		/** {@inheritDoc} */
		@Override
		public GItem ceiling(final GItem entry) {
			return this._data_.getItemOrNull(this._ceilingIndex_(entry));
		}

		/** {@inheritDoc} */
		@Override
		public GItem higher(final GItem entry) {
			return this._data_.getItemOrNull(this._higherIndex_(entry));
		}

		/** {@inheritDoc} */
		@Override
		public GItem pollFirst() {
			return this._data_.poll(this._lowestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public GItem pollLast() {
			return this._data_.poll(this._highestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> descendingSet() {
			return new CompactNavigableSet.CompactDescendingSubSet<GItem>(this._data_, this._fromItem_, this._fromInclusive_, this._lastItem_, this._lastInclusive_);
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> descendingIterator() {
			return new CompactCollectionDescendingIterator<GItem>(this._data_, this._firstIndex_(), this._lastIndex_() + 1);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement, final boolean toInclusive) {
			if (!this._isInRange_(fromElement, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
			if (!this._isInRange_(toElement, toInclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this._data_, fromElement, fromInclusive, toElement, toInclusive);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
			if (!this._isInRange_(toElement, inclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this._data_, this._fromItem_, this._fromInclusive_, toElement, inclusive);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
			if (!this._isInRange_(fromElement, inclusive)) throw new IllegalArgumentException("fromElement out of range");
			return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this._data_, fromElement, inclusive, this._lastItem_, this._lastInclusive_);
		}

	}

	/** Diese Klasse implementiert die absteigende Teilmenge eines {@link CompactNavigableSet}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	protected static final class CompactDescendingSubSet<GItem> extends CompactNavigableSet.CompactNavigableSubSet<GItem> {

		/** Dieser Konstruktor initialisiert das {@link CompactNavigableSet} und die Grenzen und deren Inklusion.
		 *
		 * @param array {@link CompactNavigableSet}.
		 * @param fromItem erstes Element oder {@link CompactSubData#_open_}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#_open_}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist. */
		public CompactDescendingSubSet(final CompactNavigableSet<GItem> array, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) throws IllegalArgumentException {
			super(array, fromItem, fromInclusive, lastItem, lastInclusive);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> iterator() {
			return new CompactCollectionDescendingIterator<GItem>(this._data_, this._firstIndex_(), this._lastIndex_() + 1);
		}

		/** {@inheritDoc} */
		@Override
		public Comparator<? super GItem> comparator() {
			return Collections.reverseOrder(this._data_.comparator);
		}

		/** {@inheritDoc} */
		@Override
		public GItem first() {
			return this._data_.getItemOrException(this._highestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public GItem last() {
			return this._data_.getItemOrException(this._lowestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public GItem lower(final GItem item) {
			return this._data_.getItemOrNull(this._higherIndex_(item));
		}

		/** {@inheritDoc} */
		@Override
		public GItem floor(final GItem item) {
			return this._data_.getItemOrNull(this._ceilingIndex_(item));
		}

		/** {@inheritDoc} */
		@Override
		public GItem ceiling(final GItem item) {
			return this._data_.getItemOrNull(this._floorIndex_(item));
		}

		/** {@inheritDoc} */
		@Override
		public GItem higher(final GItem item) {
			return this._data_.getItemOrNull(this._lowerIndex_(item));
		}

		/** {@inheritDoc} */
		@Override
		public GItem pollFirst() {
			return this._data_.poll(this._highestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public GItem pollLast() {
			return this._data_.poll(this._lowestIndex_());
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> descendingSet() {
			return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this._data_, this._fromItem_, this._fromInclusive_, this._lastItem_, this._lastInclusive_);
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> descendingIterator() {
			return new CompactCollectionAscendingIterator<GItem>(this._data_, this._firstIndex_(), this._lastIndex_() + 1);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement, final boolean toInclusive) {
			if (!this._isInRange_(fromElement, fromInclusive)) throw new IllegalArgumentException("fromElement out of range");
			if (!this._isInRange_(toElement, toInclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableSet.CompactDescendingSubSet<GItem>(this._data_, toElement, toInclusive, fromElement, fromInclusive);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
			if (!this._isInRange_(toElement, inclusive)) throw new IllegalArgumentException("toElement out of range");
			return new CompactNavigableSet.CompactDescendingSubSet<GItem>(this._data_, toElement, inclusive, this._lastItem_, this._lastInclusive_);
		}

		/** {@inheritDoc} */
		@Override
		public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
			if (!this._isInRange_(fromElement, inclusive)) throw new IllegalArgumentException("fromElement out of range");
			return new CompactNavigableSet.CompactDescendingSubSet<GItem>(this._data_, this._fromItem_, this._fromInclusive_, fromElement, inclusive);
		}

	}

	{}

	/** Dieses Feld speichert den {@link Comparator}. */
	protected final Comparator<? super GItem> comparator;

	/** Dieser Konstruktor initialisiert den {@link Comparator}.
	 *
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist. */
	public CompactNavigableSet(final Comparator<? super GItem> comparator) throws NullPointerException {
		super();
		if (comparator == null) throw new NullPointerException("comparator = null");
		this.comparator = comparator;
	}

	/** Dieser Konstruktor initialisiert das {@link Set} mit der gegebenen Kapazität und dem gegebenen {@link Comparator}.
	 *
	 * @see CompactData#allocate(int)
	 * @param capacity Kapazität.
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn der gegebene {@link Comparator} {@code null} ist. */
	public CompactNavigableSet(final int capacity, final Comparator<? super GItem> comparator) throws NullPointerException {
		this(comparator);
		this.allocate(capacity);
	}

	/** Dieser Konstruktor initialisiert das {@link Set} mit den gegebenen Elementen und dem gegebenen {@link Comparator}.
	 *
	 * @see Set#addAll(Collection)
	 * @param collection {@link Collection}.
	 * @param comparator {@link Comparator}.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} bzw. der gegebene {@link Comparator} {@code null} ist. */
	public CompactNavigableSet(final Collection<? extends GItem> collection, final Comparator<? super GItem> comparator) throws NullPointerException {
		this(comparator);
		if (collection == null) throw new NullPointerException("collection = null");
		this.allocate(collection.size());
		this.addAll(collection);
	}

	{}

	/** Diese Methode löscht das {@code index}-te Element und gibt es oder {@code null} zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-te Element oder {@code null}. */
	protected final GItem poll(final int index) {
		if ((index < 0) || (index >= this.size())) return null;
		final GItem item = this.getItem(index);
		this._remove_(index, 1);
		return item;
	}

	/** Diese Methode gibt das {@code index}-te Element oder {@code null} zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Element oder {@code null}. */
	protected final GItem getItemOrNull(final int index) {
		if ((index < 0) || (index >= this.size())) return null;
		return this.getItem(index);
	}

	/** Diese Methode gibt das {@code index}-te Element zurück oder wirft eine {@link NoSuchElementException}.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Element.
	 * @throws NoSuchElementException Wenn der gegebene Index ungültig ist. */
	protected final GItem getItemOrException(final int index) throws NoSuchElementException {
		if ((index < 0) || (index >= this.size())) throw new NoSuchElementException();
		return this.getItem(index);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected final int _itemIndex_(final Object key) {
		return this._itemIndexCompare_(key, 0);
	}

	/** {@inheritDoc} */
	@Override
	protected final boolean _itemEquals_(final Object key, final int hash, final Object item) {
		return false;
	}

	/** {@inheritDoc} */
	@SuppressWarnings ("unchecked")
	@Override
	protected final int _itemCompare_(final Object key, final int hash, final Object item) {
		return this.comparator.compare((GItem)key, (GItem)item);
	}

	/** {@inheritDoc} */
	@Override
	public Comparator<? super GItem> comparator() {
		return this.comparator;
	}

	/** {@inheritDoc} */
	@Override
	public GItem first() {
		return this.getItemOrException(this._firstIndex_());
	}

	/** {@inheritDoc} */
	@Override
	public GItem lower(final GItem item) {
		return this.getItemOrNull(this._lowerIndex_(item));
	}

	/** {@inheritDoc} */
	@Override
	public GItem floor(final GItem item) {
		return this.getItemOrNull(this._floorIndex_(item));
	}

	/** {@inheritDoc} */
	@Override
	public GItem ceiling(final GItem item) {
		return this.getItemOrNull(this._ceilingIndex_(item));
	}

	/** {@inheritDoc} */
	@Override
	public GItem higher(final GItem item) {
		return this.getItemOrNull(this._higherIndex_(item));
	}

	/** {@inheritDoc} */
	@Override
	public GItem last() {
		return this.getItemOrException(this._lastIndex_());
	}

	/** {@inheritDoc} */
	@Override
	public GItem pollFirst() {
		return this.poll(this._firstIndex_());
	}

	/** {@inheritDoc} */
	@Override
	public GItem pollLast() {
		return this.poll(this._lastIndex_());
	}

	/** {@inheritDoc} */
	@Override
	public SortedSet<GItem> subSet(final GItem fromElement, final GItem toElement) {
		return this.subSet(fromElement, true, toElement, false);
	}

	/** {@inheritDoc} */
	@Override
	public NavigableSet<GItem> subSet(final GItem fromElement, final boolean fromInclusive, final GItem toElement, final boolean toInclusive) {
		return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this, fromElement, fromInclusive, toElement, toInclusive);
	}

	/** {@inheritDoc} */
	@Override
	public SortedSet<GItem> headSet(final GItem toElement) {
		return this.headSet(toElement, false);
	}

	/** {@inheritDoc} */
	@Override
	public NavigableSet<GItem> headSet(final GItem toElement, final boolean inclusive) {
		return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this, CompactSubData._open_, true, toElement, inclusive);
	}

	/** {@inheritDoc} */
	@Override
	public SortedSet<GItem> tailSet(final GItem fromElement) {
		return this.tailSet(fromElement, true);
	}

	/** {@inheritDoc} */
	@Override
	public NavigableSet<GItem> tailSet(final GItem fromElement, final boolean inclusive) {
		return new CompactNavigableSet.CompactAscendingSubSet<GItem>(this, fromElement, inclusive, CompactSubData._open_, true);
	}

	/** {@inheritDoc} */
	@Override
	public NavigableSet<GItem> descendingSet() {
		return new CompactNavigableSet.CompactDescendingSubSet<GItem>(this, CompactSubData._open_, true, CompactSubData._open_, true);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> descendingIterator() {
		return new CompactCollectionDescendingIterator<GItem>(this, 0, this.size());
	}

}