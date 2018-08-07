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

	/** Diese Klasse implementiert einen abstrakten {@link Iterator}.
	 *
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ des Elementes. */
	public static abstract class BaseIterator<GItem> implements Iterator<GItem>, UseToString {

	}

	/** Dieses Feld speichert den leeren {@link Iterator}. */
	public static final Iterator<?> EMPTY_ITERATOR = new BaseIterator<Object>() {

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
			return "EMPTY_ITERATOR";
		}

	};

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
		while (iterator.hasNext())
			if (!collection.contains(iterator.next())) return false;
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
		if (count < 0) throw new IllegalArgumentException("count < 0");
		return new BaseIterator<GItem>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return this.index < count;
			}

			@Override
			public GItem next() {
				if (!this.hasNext()) throw new NoSuchElementException();
				this.index++;
				return item;
			}

			@Override
			public void remove() {
				throw (this.hasNext() ? new IllegalStateException() : new UnsupportedOperationException());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("itemIterator", item, count);
			}

		};
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
		Objects.assertNotNull(items);
		Comparables.check(fromIndex, toIndex);
		return new BaseIterator<GItem>() {

			int index = fromIndex;

			@Override
			public boolean hasNext() {
				return this.index < toIndex;
			}

			@Override
			public GItem next() {
				if (this.index == toIndex) throw new NoSuchElementException();
				return items.get(this.index++);
			}

			@Override
			public void remove() {
				if (this.index == fromIndex) throw new IllegalStateException();
				throw new UnsupportedOperationException();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("itemsIterator", items, fromIndex, toIndex);
			}

		};
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
		if (count < 0) throw new IllegalArgumentException("count < 0");
		return new Iterator<Integer>() {

			int value = 0;

			@Override
			public boolean hasNext() {
				return this.value < count;
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
				return Objects.toInvokeString("integerIterator", count);
			}

		};
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
		Objects.assertNotNull(iterator);
		if (count < 0) throw new IllegalArgumentException("count < 0");
		if (count == 0) return Iterators.emptyIterator();
		return new BaseIterator<GItem>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return (this.index < count) && iterator.hasNext();
			}

			@Override
			public GItem next() {
				if (this.index == count) throw new NoSuchElementException();
				this.index++;
				return iterator.next();
			}

			@Override
			public void remove() {
				iterator.remove();
			}

			/** {@inheritDoc} */
			@Override
			public String toString() {
				return Objects.toInvokeString("limitedIterator", count, iterator);
			}

		};
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
		Objects.assertNotNull(filter);
		Objects.assertNotNull(iterator);
		return new BaseIterator<GItem>() {

			Boolean has;

			GItem item;

			@Override
			public boolean hasNext() {
				if (this.has != null) return this.has.booleanValue();
				while (iterator.hasNext()) {
					if (filter.accept(this.item = iterator.next())) {
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
				iterator.remove();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("filteredIterator", filter, iterator);
			}

		};
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
		final Iterator<GItem> iterator2 = Iterators.filteredIterator(Filters.negationFilter(Filters.containsFilter(collection)), iterator);
		return new BaseIterator<GItem>() {

			@Override
			public boolean hasNext() {
				return iterator2.hasNext();
			}

			@Override
			public GItem next() {
				final GItem item = iterator2.next();
				collection.add(item);
				return item;
			}

			@Override
			public void remove() {
				iterator2.remove();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("uniqueIterator", collection, iterator);
			}

		};
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
		Objects.assertNotNull(iterators);
		return new BaseIterator<GItem>() {

			Iterator<? extends GItem> iterator;

			@Override
			public boolean hasNext() {
				while (true) {
					while (this.iterator == null) {
						if (!iterators.hasNext()) return false;
						this.iterator = iterators.next();
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
				return Objects.toInvokeString("chainedIterator", iterators);
			}

		};
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
		Objects.assertNotNull(navigator);
		Objects.assertNotNull(iterator);
		return new BaseIterator<GOutput>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public GOutput next() {
				return navigator.get(iterator.next());
			}

			@Override
			public void remove() {
				iterator.remove();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("navigatedIterator", navigator, iterator);
			}

		};
	}

	/** Diese Methode gibt einen unveränderlichen {@link Iterator} zurück, der die Elemente des gegebenen {@link Iterator} liefert und dessen
	 * {@link Iterator#remove()}-Methode immer eine {@link UnsupportedOperationException} auslöst.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@code unmodifiable}-{@link Iterator}.
	 * @throws NullPointerException Wenn {@code iterator} {@code null} ist. */
	public static <GItem> Iterator<GItem> unmodifiableIterator(final Iterator<? extends GItem> iterator) throws NullPointerException {
		Objects.assertNotNull(iterator);
		return new BaseIterator<GItem>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public GItem next() {
				return iterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("unmodifiableIterator", iterator);
			}

		};
	}

}
