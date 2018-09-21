package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Objects.UseToString;

/** Diese Klasse implementiert grundlegende {@link Iterator}.
 *
 * @see Iterator
 * @see Iterable
 * @see Iterables
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterators {

	/** Diese Klasse implementiert einen abstrakten {@link Iterator} mit {@link UseToString}.
	 *
	 * @param <GItem> Typ des Elementes. */
	public static abstract class BaseIterator<GItem> implements Iterator<GItem>, UseToString {

	}

	/** Diese Klasse implementiert {@link Iterators#itemIterator(Object, int)}. */
	@SuppressWarnings ("javadoc")
	public static class ItemIterator<GItem> extends BaseIterator<GItem> {

		public final GItem item;

		public final int count;

		protected int index = 0;

		public ItemIterator(final int count, final GItem item) {
			if (count < 0) throw new IllegalArgumentException();
			this.item = item;
			this.count = count;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.count;
		}

		@Override
		public GItem next() {
			if (!this.hasNext()) throw new NoSuchElementException();
			this.index++;
			return this.item;
		}

		@Override
		public void remove() {
			throw (this.hasNext() ? new IllegalStateException() : new UnsupportedOperationException());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.count);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#itemsIterator(Items, int, int)}. */
	@SuppressWarnings ("javadoc")
	public static class ItemsIterator<GItem> extends BaseIterator<GItem> {

		public final int fromIndex;

		public final int toIndex;

		public final Items<? extends GItem> items;

		int index;

		public ItemsIterator(final int fromIndex, final int toIndex, final Items<? extends GItem> items) {
			Comparables.check(fromIndex, toIndex);

			this.items = Objects.assertNotNull(items);
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.index = fromIndex;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.toIndex;
		}

		@Override
		public GItem next() {
			if (this.index == this.toIndex) throw new NoSuchElementException();
			return this.items.get(this.index++);
		}

		@Override
		public void remove() {
			if (this.index == this.fromIndex) throw new IllegalStateException();
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.items, this.fromIndex, this.toIndex);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#emptyIterator()}. */
	@SuppressWarnings ("javadoc")
	public static class EmptyIterator extends BaseIterator<Object> {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#integerIterator(int)}. */
	@SuppressWarnings ("javadoc")
	public static class IntegerIterator implements Iterator<Integer> {

		public final int count;

		int value = 0;

		public IntegerIterator(final int count) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		@Override
		public boolean hasNext() {
			return this.value < this.count;
		}

		@Override
		public Integer next() {
			return Integer.valueOf(this.value++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#limitedIterator(int, Iterator)}. */
	@SuppressWarnings ("javadoc")
	public static class LimitedIterator<GItem> extends BaseIterator<GItem> {

		public final int count;

		public final Iterator<? extends GItem> iterator;

		protected int index = 0;

		public LimitedIterator(final int count, final Iterator<? extends GItem> iterator) {
			if (count < 0) throw new IllegalArgumentException();
			this.iterator = Objects.assertNotNull(iterator);
			this.count = count;
		}

		@Override
		public boolean hasNext() {
			return (this.index < this.count) && this.iterator.hasNext();
		}

		@Override
		public GItem next() {
			if (this.index == this.count) throw new NoSuchElementException();
			this.index++;
			return this.iterator.next();
		}

		@Override
		public void remove() {
			this.iterator.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count, this.iterator);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#uniqueIterator(Collection, Iterator)}. */
	@SuppressWarnings ("javadoc")
	public static class UniqueIterator<GItem> extends BaseIterator<GItem> {

		public final Collection<GItem> collection;

		public final Iterator<GItem> iterator;

		public UniqueIterator(final Collection<GItem> collection, final Iterator<? extends GItem> iterator) {
			this.collection = collection;
			this.iterator = Iterators.filteredIterator(Filters.negationFilter(Filters.containsFilter(collection)), iterator);
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public GItem next() {
			final GItem item = this.iterator.next();
			this.collection.add(item);
			return item;
		}

		@Override
		public void remove() {
			this.iterator.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterator);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#filteredIterator(Filter, Iterator)}. */
	@SuppressWarnings ("javadoc")
	public static class FilteredIterator<GItem> extends BaseIterator<GItem> {

		public final Filter<? super GItem> filter;

		public final Iterator<? extends GItem> iterator;

		protected Boolean has;

		protected GItem item;

		public FilteredIterator(final Filter<? super GItem> filter, final Iterator<? extends GItem> iterator) {
			this.filter = Objects.assertNotNull(filter);
			this.iterator = Objects.assertNotNull(iterator);
		}

		@Override
		public boolean hasNext() {
			if (this.has != null) return this.has.booleanValue();
			while (this.iterator.hasNext()) {
				if (this.filter.accept(this.item = this.iterator.next())) {
					this.has = Boolean.TRUE;
					return true;
				}
			}
			this.has = Boolean.FALSE;
			return false;
		}

		@Override
		public GItem next() {
			if (!this.hasNext()) throw new NoSuchElementException();
			this.has = null;
			return this.item;
		}

		@Override
		public void remove() {
			if (this.has != null) throw new IllegalStateException();
			this.iterator.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter, this.iterator);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#chainedIterator(Iterator)}. */
	@SuppressWarnings ("javadoc")
	public static class ChainedIterator<GItem> extends BaseIterator<GItem> {

		public final Iterator<? extends Iterator<? extends GItem>> iterators;

		protected Iterator<? extends GItem> iterator;

		public ChainedIterator(final Iterator<? extends Iterator<? extends GItem>> iterators) {
			this.iterators = Objects.assertNotNull(iterators);
		}

		@Override
		public boolean hasNext() {
			while (true) {
				while (this.iterator == null) {
					if (!this.iterators.hasNext()) return false;
					this.iterator = this.iterators.next();
				}
				if (this.iterator.hasNext()) return true;
				this.iterator = null;
			}
		}

		@Override
		public GItem next() {
			if (this.iterator == null) throw new NoSuchElementException();
			return this.iterator.next();
		}

		@Override
		public void remove() {
			if (this.iterator == null) throw new IllegalStateException();
			this.iterator.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterators);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#navigatedIterator(Getter, Iterator)}. */
	@SuppressWarnings ("javadoc")
	public static class NavigatedIterator<GInput, GOutput> extends BaseIterator<GOutput> {

		public final Getter<? super GInput, ? extends GOutput> navigator;

		public final Iterator<? extends GInput> iterator;

		public NavigatedIterator(final Getter<? super GInput, ? extends GOutput> navigator, final Iterator<? extends GInput> iterator) {
			this.navigator = Objects.assertNotNull(navigator);
			this.iterator = Objects.assertNotNull(iterator);
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public GOutput next() {
			return this.navigator.get(this.iterator.next());
		}

		@Override
		public void remove() {
			this.iterator.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.navigator, this.iterator);
		}

	}

	/** Diese Klasse implementiert {@link Iterators#unmodifiableIterator(Iterator)}. */
	@SuppressWarnings ("javadoc")
	public static class UnmodifiableIterator<GItem> extends BaseIterator<GItem> {

		public final Iterator<? extends GItem> iterator;

		public UnmodifiableIterator(final Iterator<? extends GItem> iterator) {
			this.iterator = Objects.assertNotNull(iterator);
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public GItem next() {
			return this.iterator.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterator);
		}

	}

	/** Dieses Feld speichert den leeren {@link Iterator}. */
	public static final Iterator<?> EMPTY_ITERATOR = new EmptyIterator();

	/** Diese Methode gibt das {@code index}-te Elemente des gegebenen {@link Iterator} zurück.
	 *
	 * @param <GItem> Typ des Elements.
	 * @see Iterators#skip(Iterator, int)
	 * @param iterator {@link Iterator}.
	 * @param index Index.
	 * @return {@code index}-tes Element.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist.
	 * @throws NoSuchElementException Wenn kein {@code index}-tes Element existiert. */
	public static <GItem> GItem get(final Iterator<? extends GItem> iterator, final int index) throws NullPointerException, NoSuchElementException {
		if ((index < 0) || (Iterators.skip(iterator, index) != 0) || !iterator.hasNext()) throw new NoSuchElementException();
		return iterator.next();
	}

	/** Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die Anzahl der noch zu überspringenden
	 * Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene {@link Iterator} via {@link Iterator#hasNext()} anzeigt, dass er keine
	 * weiteren Elemente mehr liefern kann. Wenn die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen
	 * {@link Iterator} zurück gegeben. Damit bestimmt {@code (-Iterators.skip(iterator, -1) - 1)} die Anzahl der Elemente des gegebenen {@link Iterator}.
	 *
	 * @see Iterator#hasNext()
	 * @param iterator {@link Iterator}.
	 * @param count Anzahl der zu überspringenden Elemente.
	 * @return Anzahl der noch zu überspringenden Elemente.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist. */
	public static int skip(final Iterator<?> iterator, int count) throws NullPointerException {
		Objects.assertNotNull(iterator);
		while ((count != 0) && iterator.hasNext()) {
			count--;
			iterator.next();
		}
		return count;
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterator} in die gegebene {@link Collection} ein und gibt nur dann {@code true} zurück, wenn
	 * Elemente eingefügt wurden.
	 *
	 * @see Collection#addAll(Collection)
	 * @param <GItem> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static <GItem> boolean addAll(final Collection<GItem> collection, final Iterator<? extends GItem> iterator) throws NullPointerException {
		Objects.assertNotNull(collection);
		boolean modified = false;
		while (iterator.hasNext()) {
			if (collection.add(iterator.next())) {
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur dann
	 * {@code true} zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection)
	 * @param iterator {@link Iterator}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static boolean retainAll(final Iterator<?> iterator, final Collection<?> collection) throws NullPointerException {
		Objects.assertNotNull(collection);
		boolean modified = false;
		while (iterator.hasNext()) {
			if (!collection.contains(iterator.next())) {
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static boolean retainAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		Objects.assertNotNull(collection);
		final List<Object> list = new ArrayList<>();
		Iterators.addAll(list, iterator);
		return collection.retainAll(list);
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator} und gibt nur dann {@code true} zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Iterator#remove()
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist. */
	public static boolean removeAll(final Iterator<?> iterator) throws NullPointerException {
		boolean modified = false;
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
			modified = true;
		}
		return modified;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}, die in der gegebenen {@link Collection} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection)
	 * @param iterator {@link Iterator}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static boolean removeAll(final Iterator<?> iterator, final Collection<?> collection) throws NullPointerException {
		Objects.assertNotNull(collection);
		boolean modified = false;
		while (iterator.hasNext()) {
			if (collection.contains(iterator.next())) {
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator} aus der gegebenen {@link Collection} und gibt nur dann {@code true} zurück, wenn
	 * Elemente entfernt wurden.
	 *
	 * @see Collection#removeAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static boolean removeAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		Objects.assertNotNull(collection);
		boolean modified = false;
		while (iterator.hasNext()) {
			if (collection.remove(iterator.next())) {
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterator} in der gegebenen {@link Collection} enthalten sind.
	 *
	 * @see Collection#containsAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei vollständiger Inklusion.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static boolean containsAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		Objects.assertNotNull(collection);
		while (iterator.hasNext()) {
			if (!collection.contains(iterator.next())) return false;
		}
		return true;
	}

	/** Diese Methode gibt den gegebenen {@link Iterator} oder {@link #EMPTY_ITERATOR} zurück. Wenn {@code iterator} {@code null} ist, wird
	 * {@link #EMPTY_ITERATOR} geliefert.
	 *
	 * @see Iterators#emptyIterator()
	 * @param <GItem> Typ der Elemente.
	 * @param iterator {@link Iterator} oder {@code null}.
	 * @return {@link Iterator} oder {@link #EMPTY_ITERATOR}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterator<GItem> iterator(final Iterator<? extends GItem> iterator) {
		if (iterator == null) return Iterators.emptyIterator();
		return (Iterator<GItem>)iterator;
	}

	/** Diese Methode gibt den {@link Iterator} des gegebenen {@link Iterable} oder {@link #EMPTY_ITERATOR} zurück. Wenn {@code iterable} {@code null} ist, wird
	 * {@link #EMPTY_ITERATOR} geliefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable} oder {@code null}.
	 * @return {@link Iterable#iterator()} oder {@link #EMPTY_ITERATOR}. */
	public static <GItem> Iterator<GItem> iterator(final Iterable<? extends GItem> iterable) {
		if (iterable == null) return Iterators.emptyIterator();
		return Iterators.iterator(iterable.iterator());
	}

	/** Diese Methode gibt einen {@link Iterator} zurück, der einmalig das gegebenen Element liefert.
	 *
	 * @see #itemIterator(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @return {@code item}-{@link Iterator}. */
	public static <GItem> Iterator<GItem> itemIterator(final GItem item) {
		return Iterators.itemIterator(item, 1);
	}

	/** Diese Methode gibt einen {@link Iterator} zurück, der das gegebenen Element die gegebene Anzahl mal liefert.
	 *
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @param count Anzahl der iterierbaren Elemente.
	 * @return {@code item}-{@link Iterator}.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterator<GItem> itemIterator(final GItem item, final int count) throws IllegalArgumentException {
		if (count == 0) return Iterators.emptyIterator();
		return new ItemIterator<>(count, item);
	}

	/** Diese Methode gibt einen {@link Iterator} über die Elemente eines Abschnitts der gegebenen {@link Items} zurück.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link Items}.
	 * @param fromIndex Index des ersten Elements, dass vom erzeugten {@link Iterator} geliefert wird.
	 * @param toIndex Index nach dem letzten Element, dass vom erzeugten {@link Iterator} geliefert wird.
	 * @return {@link Items}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code fromIndex > toIndex}. */
	public static <GItem> Iterator<GItem> itemsIterator(final Items<? extends GItem> items, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		return new ItemsIterator<>(fromIndex, toIndex, items);
	}

	/** Diese Methode gibt den leeren {@link Iterator} zurück.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @return {@link #EMPTY_ITERATOR}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterator<GItem> emptyIterator() {
		return (Iterator<GItem>)Iterators.EMPTY_ITERATOR;
	}

	/** Diese Methode gibt einen {@link Iterator} zurück, der die gegebene Anzahl an {@link Integer} ab dem Wert {@code 0} liefert, d.h {@code 0}, {@code 1}, ...,
	 * {@code count-1}.
	 *
	 * @param count Anzahl.
	 * @return {@link Integer}-{@link Iterator}.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static Iterator<Integer> integerIterator(final int count) throws IllegalArgumentException {
		return new IntegerIterator(count);
	}

	/** Diese Methode gibt einen {@link Iterator} zurück, der die gegebene maximale Anzahl an Elementen des gegebenen {@link Iterator} liefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param count Maximale Anzahl der vom gegebenen {@link Iterator} gelieferten Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@code limited}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterator<GItem> limitedIterator(final int count, final Iterator<? extends GItem> iterator)
		throws NullPointerException, IllegalArgumentException {
		if (count == 0) return Iterators.emptyIterator();
		return new LimitedIterator<>(count, iterator);
	}

	/** Diese Methode gibt einen filternden {@link Iterator} zurück, der nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterator}
	 * liefert.
	 *
	 * @see Filter#accept(Object)
	 * @param <GItem> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @param filter {@link Filter}.
	 * @return {@code filtered}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code filter} bzw. {@code iterator} {@code null} ist. */
	public static <GItem> Iterator<GItem> filteredIterator(final Filter<? super GItem> filter, final Iterator<? extends GItem> iterator)
		throws NullPointerException {
		return new FilteredIterator<>(filter, iterator);
	}

	/** Diese Methode gibt einen {@link Iterator} zurück, der kein Element des gegebenen {@link Iterator} mehrfach liefert. Die vom erzeugten {@link Iterator}
	 * gelieferten Elemente werden zur Erkennung von Mehrfachvorkommen in ein {@link HashSet2} eingefügt.
	 *
	 * @see #uniqueIterator(Collection, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@code unique}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist. */
	public static <GItem> Iterator<GItem> uniqueIterator(final Iterator<? extends GItem> iterator) throws NullPointerException {
		return Iterators.uniqueIterator(new HashSet2<GItem>(), Objects.assertNotNull(iterator));
	}

	/** Diese Methode gibt einen {@link Iterator} zurück, der kein Element des gegebenen {@link Iterator} mehrfach liefert. Die vom erzeugten {@link Iterator}
	 * gelieferten Elemente werden zur Erkennung von Mehrfachvorkommen in die gegebenen {@link Collection} eingefügt.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param collection {@link Collection} zum Ausschluss von Mehrfachvorkommen.
	 * @param iterator {@link Iterator}.
	 * @return {@code unique}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code collection} {@code null} ist. */
	public static <GItem> Iterator<GItem> uniqueIterator(final Collection<GItem> collection, final Iterator<? extends GItem> iterator)
		throws NullPointerException {
		return new UniqueIterator<>(collection, iterator);
	}

	/** Diese Methode gibt einen verketteten {@link Iterator} zurück, der alle Elemente der gegebenen {@link Iterator} in der gegebenen Reihenfolge liefert.
	 *
	 * @see #chainedIterator(Iterable)
	 * @param <GItem> Typ der Elemente.
	 * @param iterator1 erster {@link Iterator} oder {@code null}.
	 * @param iterator2 zweiter {@link Iterator} oder {@code null}.
	 * @return {@code chained}-{@link Iterator}. */
	public static <GItem> Iterator<GItem> chainedIterator(final Iterator<? extends GItem> iterator1, final Iterator<? extends GItem> iterator2) {
		return Iterators.chainedIterator(Arrays.asList(iterator1, iterator2));
	}

	/** Diese Methode gibt einen verketteten {@link Iterator} zurück, der alle Elemente der gegebenen {@link Iterator} in der gegebenen Reihenfolge liefert. Das
	 * gegebene {@link Iterable} darf {@code null} liefern.
	 *
	 * @see #chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable} über die {@link Iterator}.
	 * @return {@code chained}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> Iterator<GItem> chainedIterator(final Iterable<? extends Iterator<? extends GItem>> iterable) throws NullPointerException {
		return Iterators.chainedIterator(iterable.iterator());
	}

	/** Diese Methode gibt einen verketteten {@link Iterator} zurück, der alle Elemente der gegebenen {@link Iterator} in der gegebenen Reihenfolge liefert. Der
	 * gegebene {@link Iterator} darf {@code null} liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterators {@link Iterator}, dessen Elemente ({@link Iterator}) verkettet werden.
	 * @return {@code chained}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterators} {@code null} ist. */
	public static <GItem> Iterator<GItem> chainedIterator(final Iterator<? extends Iterator<? extends GItem>> iterators) throws NullPointerException {
		return new ChainedIterator<>(iterators);
	}

	/** Diese Methode gibt einen umwandelnden {@link Iterator} zurück, der die vom gegebenen {@link Getter} konvertierten Elemente des gegebenen {@link Iterator}
	 * liefert.
	 *
	 * @see Getter#get(Object)
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Getter} sowie der Elemente des gegebenen {@link Iterator}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Getter} sowie der Elemente des erzeugten {@link Iterator}.
	 * @param iterator {@link Iterator}.
	 * @param navigator {@link Getter} nur Navigation.
	 * @return {@code navigated}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} bzw. {@code navigator} {@code null} ist. */
	public static <GInput, GOutput> Iterator<GOutput> navigatedIterator(final Getter<? super GInput, ? extends GOutput> navigator,
		final Iterator<? extends GInput> iterator) throws NullPointerException {
		return new NavigatedIterator<>(navigator, iterator);
	}

	/** Diese Methode gibt einen unveränderlichen {@link Iterator} zurück, der die Elemente des gegebenen {@link Iterator} liefert und dessen
	 * {@link Iterator#remove()}-Methode immer eine {@link UnsupportedOperationException} auslöst.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@code unmodifiable}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist. */
	public static <GItem> Iterator<GItem> unmodifiableIterator(final Iterator<? extends GItem> iterator) throws NullPointerException {
		return new UnmodifiableIterator<>(iterator);
	}

}
