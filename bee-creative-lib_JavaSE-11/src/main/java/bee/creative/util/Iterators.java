package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.HashSet2.hashSetFrom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Iterator}.
 *
 * @see Iterables
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterators {

	/** Diese Methode liefert den gegebenen {@link Iterator} als {@link Iterator3}. Wenn er {@code null} ist, wird der {@link #emptyIterator()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <E> Iterator3<E> iteratorFrom(final Iterator<? extends E> that) {
		if (that == null) return emptyIterator();
		if (that instanceof Iterator3<?>) return (Iterator3<E>)that;
		return translatedIterator(that, neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link #iteratorFromItem(Object, int) iteratorFromItem(item, 1)}. */
	public static <E> Iterator3<E> iteratorFromItem(E item) {
		return iteratorFromItem(item, 1);
	}

	/** Diese Methode liefert einen {@link Iterator3}, der das gegebene Element die gegebene Anzahl mal liefert. **/
	public static <E> Iterator3<E> iteratorFromItem(E item, int count) throws IllegalArgumentException {
		return count == 0 ? emptyIterator() : iteratorFromArray(index -> item, 0, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#iterableFromArray(Object...) Iterables.fromArray(items).iterator()}. */
	@SafeVarargs
	public static <E> Iterator3<E> iteratorFromArray(E... items) throws NullPointerException {
		return Iterables.iterableFromArray(items).iterator();
	}

	/** Diese Methode liefert ein {@link Iterable3}, das die Elemente des gegebenen Array im gegebenen Abschnit liefert. */
	public static <E> Iterator3<E> iteratorFromArray(E[] items, int minInclIndex, int maxExclIndex) throws NullPointerException, IllegalArgumentException {
		if ((minInclIndex < 0) || (items.length < maxExclIndex)) throw new IllegalArgumentException();
		return iteratorFromArray(index -> items[index], minInclIndex, maxExclIndex);
	}

	/** Diese Methode liefert ein {@link Iterable3}, das die Elemente des gegebenen {@link Array} im gegebenen Abschnit liefert. */
	public static <E> Iterator3<E> iteratorFromArray(Array<? extends E> items, int minInclIndex, int maxExclIndex)
		throws NullPointerException, IllegalArgumentException {
		notNull(items);
		if (maxExclIndex < minInclIndex) throw new IllegalArgumentException();
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return this.index < maxExclIndex;
			}

			@Override
			public E next() {
				if (this.index >= maxExclIndex) throw new NoSuchElementException();
				return items.get(this.index++);
			}

			int index = minInclIndex;

		};
	}

	/** Diese Methode ist eine Abkürzung für {@link #iteratorFromRange(int, int) iteratorFromRange(0, count)}. */
	public static Iterator3<Integer> iteratorFromCount(int count) throws IllegalArgumentException {
		return iteratorFromRange(0, count);
	}

	/** Diese Methode liefert ein {@link Iterable3}, das die {@link Integer} im gegebenen Wertebereich liefert. */
	public static Iterator3<Integer> iteratorFromRange(int minIncl, int maxExcl) throws IllegalArgumentException {
		if (maxExcl < minIncl) throw new IllegalArgumentException();
		return iteratorFromArray(index -> index, minIncl, maxExcl);
	}

	/** Diese Methode liefert einen {@link Iterator3}, der keine Elemente liefert. */
	@SuppressWarnings ("unchecked")
	public static <E> Iterator3<E> emptyIterator() {
		return (Iterator3<E>)emptyIterator;
	}

	private static final Iterator3<?> emptyIterator = iteratorFromCount(0);

	/** Diese Methode ist eine Abkürzung für {@link #concatAllIterator(Iterator) concatAllIterator(iteratorFromArray(iter1, iter2))}. */
	public static <E> Iterator3<E> concatIterator(Iterator<? extends E> iter1, Iterator<? extends E> iter2) {
		return concatAllIterator(iteratorFromArray(iter1, iter2));
	}

	/** Diese Methode liefert einen verkettenden {@link Iterator3}, der alle Elemente der gegebenen Iteratoren in der gegebenen Reihenfolge liefert. Diese
	 * Iteratoren dürfen {@code null} sein. */
	public static <E> Iterator3<E> concatAllIterator(Iterator<? extends Iterator<? extends E>> that) throws NullPointerException {
		notNull(that);
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				while (true) {
					while (this.iter == null) {
						if (!that.hasNext()) return false;
						this.iter = that.next();
					}
					if (this.iter.hasNext()) return true;
					this.iter = null;
				}
			}

			@Override
			public E next() {
				if (this.iter == null) throw new NoSuchElementException();
				return this.iter.next();
			}

			@Override
			public void remove() {
				if (this.iter == null) throw new IllegalStateException();
				this.iter.remove();
			}

			Iterator<? extends E> iter;

		};
	}

	public static class ConcatIterator<T> extends AbstractIterator<T> {

	}

	/** Diese Methode liefert das {@code index}-te Elemente des gegebenen {@link Iterator} oder löst eine {@link NoSuchElementException} aus.
	 *
	 * @see Iterators#iteratorSkip(Iterator, int) */
	public static <T> T get(final Iterator<? extends T> iter, final int index) throws NullPointerException, NoSuchElementException {
		if ((index < 0) || (iteratorSkip(iter, index) != 0) || !iter.hasNext()) throw new NoSuchElementException();
		return iter.next();
	}

	/** Diese Methode gibt die Anzahl der vom gegebenen {@link Iterator} gelieferten Elemente zurück. */
	public static int size(final Iterator<?> iter) {
		return -iteratorSkip(iter, -1) - 1;
	}

	/** Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die Anzahl der noch zu überspringenden
	 * Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene Iterator {@link Iterator#hasNext() anzeigt}, dass er keine weiteren Elemente
	 * mehr liefern kann. Wenn die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen Iterator zurück
	 * gegeben. Damit bestimmt {@code (-skip(iter, -1) - 1)} die Anzahl der Elemente des gegebenen Iterator. */
	public static int iteratorSkip(final Iterator<?> iter, int count) throws NullPointerException {
		notNull(iter);
		while ((count != 0) && iter.hasNext()) {
			count--;
			iter.next();
		}
		return count;
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterator} in die gegebene {@link Collection} ein und gibt nur dann {@code true} zurück, wenn
	 * Elemente eingefügt wurden.
	 *
	 * @see Collection#addAll(Collection) */
	public static <T> boolean addAll(final Collection<T> target, final Iterator<? extends T> source) throws NullPointerException {
		notNull(target);
		var modified = false;
		while (source.hasNext()) {
			if (target.add(source.next())) {
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur dann
	 * {@code true} zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection) */
	public static boolean iteratorRetainAll(final Iterator<?> target, final Collection<?> filter) throws NullPointerException {
		return iteratorRemoveAll(filteredIterator(target, Filters.filterFromItems(filter).negate()));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection) */
	public static boolean retainAll(final Collection<?> target, final Iterator<?> filter) throws NullPointerException {
		final var filter2 = new HashSet2<>();
		addAll(filter2, filter);
		return target.retainAll(filter2);
	}

	/** Diese Methode {@link Iterator#remove() entfernt} alle Elemente des gegebenen {@link Iterator} und gibt nur dann {@code true} zurück, wenn Elemente
	 * entfernt wurden.
	 *
	 * @return {@code true} bei Veränderungen am {@link Iterator}. */
	public static boolean iteratorRemoveAll(final Iterator<?> target) throws NullPointerException {
		if (!target.hasNext()) return false;
		do {
			target.next();
			target.remove();
		} while (target.hasNext());
		return true;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}, die in der gegebenen {@link Collection} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#removeAll(Collection) */
	public static boolean removeAll(final Iterator<?> target, final Collection<?> filter) throws NullPointerException {
		return iteratorRemoveAll(filteredIterator(target, Filters.filterFromItems(filter)));
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator} aus der gegebenen {@link Collection} und gibt nur dann {@code true} zurück, wenn
	 * Elemente entfernt wurden.
	 *
	 * @see Collection#removeAll(Collection)
	 * @return {@code true} bei Veränderungen an der {@link Collection}. */
	public static boolean removeAll(final Collection<?> target, final Iterator<?> filter) throws NullPointerException {
		final var filter2 = new HashSet2<>();
		addAll(filter2, filter);
		return target.removeAll(filter2);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterator} in der gegebenen {@link Collection} enthalten sind.
	 *
	 * @see Collection#containsAll(Collection)
	 * @return {@code true} bei vollständiger Inklusion. */
	public static boolean containsAll(final Collection<?> target, final Iterator<?> filter) throws NullPointerException {
		notNull(target);
		while (filter.hasNext()) {
			if (!target.contains(filter.next())) return false;
		}
		return true;
	}

	/** Diese Methode übergibt alle Elemente des gegebene {@link Iterator} an den gegebenen {@link Consumer}. */
	public static <T> void forEachRemaining(final Iterator<? extends T> source, final Consumer<? super T> target) throws NullPointerException {
		notNull(target);
		while (source.hasNext()) {
			target.set(source.next());
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link UnionIterator new UnionIterator<>(order, iter1, iter2)}. */
	public static <T> Iterator3<T> union(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
		throws NullPointerException {
		return new UnionIterator<>(order, iter1, iter2);
	}

	/** Diese Methode liefert einen {@link Iterator3}, welcher die aufsteigend geordnete Vereinigung der Elemente der gegebenen Iteratoren liefert und welcher das
	 * Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @see #emptyIterator()
	 * @see #union(Comparator, Iterator, Iterator) */
	public static <T> Iterator3<T> unionAll(final Comparator<? super T> order, final Iterator<? extends Iterator<? extends T>> iters)
		throws NullPointerException {
		if (!iters.hasNext()) return emptyIterator();
		Iterator3<T> result = iteratorFrom(iters.next());
		while (iters.hasNext()) {
			result = union(order, result, iters.next());
		}
		return result;
	}

	/** Diese Klasse implementiert einen {@link Iterator3}, der die aufsteigend geordnete Vereinigung der Elemente zweier gegebener Iteratoren liefert und welcher
	 * das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen
	 * Ordnung liefern.
	 *
	 * @param <T> Typ der Elemente. */

	public static class UnionIterator<T> extends AbstractIterator<T> {

		public final Comparator<? super T> order;

		public final Iterator<? extends T> iter1;

		public final Iterator<? extends T> iter2;

		protected T item1;

		protected T item2;

		public UnionIterator(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2) throws NullPointerException {
			this.order = notNull(order);
			this.iter1 = iter1;
			this.iter2 = iter2;
			this.item1 = this.next1();
			this.item2 = this.next2();
		}

		@Override
		public boolean hasNext() {
			return (this.item1 != null) || (this.item2 != null);
		}

		@Override
		public T next() {
			final T item1 = this.item1, item2 = this.item2;
			if (item1 == null) {
				if (item2 == null) throw new NoSuchElementException();
				this.item2 = this.next2();
				return item2;
			}
			if (item2 == null) {
				if (item1 == null) throw new NoSuchElementException();
				this.item1 = this.next1();
				return item1;
			}
			final var order = this.order.compare(item1, item2);
			if (order < 0) {
				this.item1 = this.next1();
				return item1;
			}
			if (order > 0) {
				this.item2 = this.next2();
				return item2;
			}
			this.item1 = this.next1();
			this.item2 = this.next2();
			return item1;
		}

		T next1() {
			return this.iter1.hasNext() ? this.iter1.next() : null;
		}

		T next2() {
			return this.iter2.hasNext() ? this.iter2.next() : null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptIterator new ExceptIterator<>(order, iter1, iter2)}. */
	public static <T> Iterator3<T> except(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
		throws NullPointerException {
		return new ExceptIterator<>(order, iter1, iter2);
	}

	/** Diese Klasse implementiert einen {@link Iterator3}, der aufsteigend geordnete die Elemente eines ersten gegebenen Iterators ohne denen eines zweiten
	 * gegebenen Iterators liefert und welcher das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu
	 * aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @param <T> Typ der Elemente. */

	public static class ExceptIterator<T> extends AbstractIterator<T> {

		public final Comparator<? super T> order;

		public final Iterator<? extends T> iter1;

		public final Iterator<? extends T> iter2;

		protected T item1;

		protected T item2;

		public ExceptIterator(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2) throws NullPointerException {
			this.order = notNull(order);
			this.iter1 = iter1;
			this.iter2 = iter2;
			this.item2 = this.next2();
			this.item1 = this.next1();
		}

		@Override
		public boolean hasNext() {
			return this.item1 != null;
		}

		@Override
		public T next() {
			final var item = this.item1;
			if (item == null) throw new NoSuchElementException();
			this.item1 = this.next1();
			return item;
		}

		T next1() {
			while (this.iter1.hasNext()) {
				final T item1 = this.iter1.next();
				if (this.item2 == null) return item1;
				final var order = this.order.compare(item1, this.item2);
				if (order < 0) return item1;
				if (order == 0) {
					this.item2 = this.next2();
				}
			}
			return null;
		}

		T next2() {
			return this.iter2.hasNext() ? this.iter2.next() : null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Methode liefert einen {@link Iterator3}, der aufsteigend geordnete nur die Elemente liefert, die von den beiden gegebenen Iteratoren geliefert
	 * werden und der das Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu bezüglich der gegebenen Ordnung
	 * aufsteigend geordnet liefern. */
	public static <T> Iterator3<T> intersect(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
		throws NullPointerException {
		return new IntersectIterator<>(order, iter1, iter2);
	}

	/** Diese Klasse implementiert einen {@link Iterator3}, der aufsteigend geordnete nur die Elemente liefert, die von beiden gegebenen Iteratoren geliefert
	 * werden und und welcher das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend
	 * bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @param <T> Typ der Elemente. */

	public static class IntersectIterator<T> extends AbstractIterator<T> {

		public final Comparator<? super T> order;

		public final Iterator<? extends T> iter1;

		public final Iterator<? extends T> iter2;

		protected T item;

		public IntersectIterator(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
			throws NullPointerException {
			this.iter1 = iter1;
			this.iter2 = notNull(iter2);
			this.order = notNull(order);
			this.item = this.next0();
		}

		@Override
		public boolean hasNext() {
			return this.item != null;
		}

		@Override
		public T next() {
			final var item = this.item;
			if (item == null) throw new NoSuchElementException();
			this.item = this.next0();
			return item;
		}

		T next0() {
			if (!this.iter1.hasNext()) return null;
			T item1 = this.iter1.next();
			if (!this.iter2.hasNext()) return null;
			T item2 = this.iter2.next();
			while (true) {
				final var order = this.order.compare(item1, item2);
				if (order == 0) return item1;
				if (order < 0) {
					if (!this.iter1.hasNext()) return null;
					item1 = this.iter1.next();
				} else {
					if (!this.iter2.hasNext()) return null;
					item2 = this.iter2.next();
				}
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Methode liefert einen {@link Iterator3}, welcher den aufsteigend geordneten Schnitt der Elemente der gegebenen Iteratoren liefert und welcher das
	 * Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @see #emptyIterator()
	 * @see #intersect(Comparator, Iterator, Iterator) */
	public static <T> Iterator3<T> intersectAll(final Comparator<? super T> order, final Iterator<? extends Iterator<? extends T>> iters)
		throws NullPointerException {
		if (!iters.hasNext()) return emptyIterator();
		Iterator3<T> result = iteratorFrom(iters.next());
		while (iters.hasNext()) {
			result = intersect(order, result, iters.next());
		}
		return result;
	}

	/** Diese Methode liefert einen begrenzenden {@link Iterator3}, der die Elemente des gegebenen {@link Iterator} bis zu einer gegebenen maximalen Anzahl
	 * liefert. */
	public static <T> Iterator3<T> limitedIterator(Iterator<? extends T> that, int limit) throws NullPointerException, IllegalArgumentException {
		notNull(that);
		if (limit < 0) throw new IllegalArgumentException();
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return (this.count < limit) && that.hasNext();
			}

			@Override
			public T next() {
				if (this.count >= limit) throw new NoSuchElementException();
				this.count++;
				return that.next();
			}

			@Override
			public void remove() {
				that.remove();
			}

			int count;

		};
	}

	/** Diese Methode liefert einen filternden {@link Iterator3}, der nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterator}
	 * liefert. */
	public static <T> Iterator3<T> filteredIterator(Iterator<? extends T> that, Filter<? super T> filter) throws NullPointerException {
		notNull(that);
		notNull(filter);
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				if (this.hasNext != null) return this.hasNext;
				while (that.hasNext()) {
					if (filter.accepts(this.next = that.next())) return this.hasNext = true;
				}
				return this.hasNext = false;
			}

			@Override
			public T next() {
				if (!this.hasNext()) throw new NoSuchElementException();
				this.hasNext = null;
				return this.next;
			}

			@Override
			public void remove() {
				if (this.hasNext != null) throw new IllegalStateException();
				that.remove();
			}

			Boolean hasNext;

			T next;

		};
	}

	/** Diese Methode ist eine Abkürzung für {@link #uniqueIterator(Iterator, Collection) uniqueIterator(that, new HashSet2<>())}. */
	public static <T> Iterator3<T> uniqueIterator(Iterator<? extends T> that) throws NullPointerException {
		return uniqueIterator(that, new HashSet2<>());
	}

	/** Diese Methode ist eine Abkürzung für {@link #uniqueIterator(Iterator, Collection) uniqueIterator(that, hashSetFrom(hasher))}. */
	public static <T> Iterator3<T> uniqueIterator(Iterator<? extends T> that, Hasher hasher) throws NullPointerException {
		return uniqueIterator(that, hashSetFrom(hasher));
	}

	/** Diese Methode liefert einen {@link Iterator3}, der kein Element des gegebenen {@link Iterator} mehrfach liefert. Zur Duplikaterkennung werden die Elemente
	 * in die gegebenen {@link Collection} eingefügt. Sie ist eine Abkürzung für {@link #filteredIterator(Iterator, Filter) filteredIterator(that,
	 * buffer::add)}. */
	public static <T> Iterator3<T> uniqueIterator(Iterator<? extends T> that, Collection<? super T> buffer) throws NullPointerException {
		notNull(buffer);
		return filteredIterator(that, buffer::add);
	}

	/** Diese Methode liefert einen übersetzten {@link Iterator3}, der die über den gegebenen {@link Getter} umgewandelte Elemente des gegebenen {@link Iterator}
	 * liefert. */
	public static <T2, T> Iterator3<T> translatedIterator(Iterator<? extends T2> that, Getter<? super T2, ? extends T> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return that.hasNext();
			}

			@Override
			public T next() {
				return trans.get(that.next());
			}

			@Override
			public void remove() {
				that.remove();
			}
		};
	}

	/** Diese Methode liefert einen unveränderlichen {@link Iterator3}, der beim Entfernen stets eine {@link UnsupportedOperationException} auslöst. */
	public static <T> Iterator3<T> unmodifiableIterator(Iterator<? extends T> that) throws NullPointerException {
		notNull(that);
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return that.hasNext();
			}

			@Override
			public T next() {
				return that.next();
			}

		};
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link Set}. Hierbei wird eine über {@link #addAll(Collection, Iterator)} befülltes
	 * {@link HashSet2} geliefert. */
	public static <T> Set<T> toSet(Iterator<T> source) throws NullPointerException {
		var result = new HashSet2<T>();
		addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterator} als {@link List}. Hierbei wird eine über {@link #addAll(Collection, Iterator)} befüllte
	 * {@link ArrayList} geliefert. */
	public static <T> List<T> toList(Iterator<T> source) throws NullPointerException {
		var result = new ArrayList<T>();
		addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterator} als Array. Dazu wird der Iterator in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray() geliefert} wird. */
	public static Object[] toArray(Iterator<?> source) throws NullPointerException {
		return toList(source).toArray();
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterator} als Array. Dazu wird der Iterator in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray(Object[]) geliefert} wird. */
	public static <T> T[] toArray(Iterator<? extends T> source, T[] array) throws NullPointerException {
		return toList(source).toArray(array);
	}

}
