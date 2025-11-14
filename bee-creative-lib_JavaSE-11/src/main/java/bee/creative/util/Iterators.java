package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Filters.filterFromItems;
import static bee.creative.util.Getters.neutralGetter;
import static bee.creative.util.HashSet2.hashSetFrom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import bee.creative.lang.Array;

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

	/** Diese Methode ist eine Abkürzung für {@link #iteratorFromArray(Array, int, int) Iterables.fromArray(items).iterator()}. */
	@SafeVarargs
	public static <E> Iterator3<E> iteratorFromArray(E... items) throws NullPointerException {
		return iteratorFromArray(items, 0, items.length);
	}

	/** Diese Methode liefert ein {@link Iterator3}, das die Elemente des gegebenen Array im gegebenen Abschnit liefert. */
	public static <E> Iterator3<E> iteratorFromArray(E[] items, int minInclIndex, int maxExclIndex) throws NullPointerException, IllegalArgumentException {
		if ((minInclIndex < 0) || (items.length < maxExclIndex)) throw new IllegalArgumentException();
		return iteratorFromArray(index -> items[index], minInclIndex, maxExclIndex);
	}

	/** Diese Methode liefert ein {@link Iterator3}, das die Elemente des gegebenen {@link Array} im gegebenen Abschnitt liefert. */
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

	/** Diese Methode liefert ein {@link Iterator3}, das die {@link Integer} im gegebenen Wertebereich liefert. */
	public static Iterator3<Integer> iteratorFromRange(int minInclIndex, int maxExclIndex) throws IllegalArgumentException {
		if (maxExclIndex < minInclIndex) throw new IllegalArgumentException();
		return iteratorFromArray(index -> index, minInclIndex, maxExclIndex);
	}

	/** Diese Methode liefert einen {@link Iterator3}, der keine Elemente liefert. */
	@SuppressWarnings ("unchecked")
	public static <E> Iterator3<E> emptyIterator() {
		return (Iterator3<E>)emptyIterator;
	}

	/** Diese Methode ist eine Abkürzung für {@link #concatIterator(Iterator) concatAllIterator(iteratorFromArray(iter1, iter2))}. */
	public static <E> Iterator3<E> concatIterator(Iterator<? extends E> iter1, Iterator<? extends E> iter2) {
		return concatIterator(iteratorFromArray(iter1, iter2));
	}

	/** Diese Methode liefert einen verkettenden {@link Iterator3}, der alle Elemente der gegebenen Iteratoren in der gegebenen Reihenfolge liefert. Diese
	 * Iteratoren dürfen {@code null} sein. */
	public static <E> Iterator3<E> concatIterator(Iterator<? extends Iterator<? extends E>> that) throws NullPointerException {
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

	/** Diese Methode liefert einen begrenzenden {@link Iterator3}, der die Elemente des gegebenen {@link Iterator} bis zu einer gegebenen maximalen Anzahl
	 * liefert. */
	public static <E> Iterator3<E> limitedIterator(Iterator<? extends E> that, int limit) throws NullPointerException, IllegalArgumentException {
		notNull(that);
		if (limit < 0) throw new IllegalArgumentException();
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return (this.count < limit) && that.hasNext();
			}

			@Override
			public E next() {
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
	public static <E> Iterator3<E> filteredIterator(Iterator<? extends E> that, Filter<? super E> filter) throws NullPointerException {
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
			public E next() {
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

			E next;

		};
	}

	/** Diese Methode ist eine Abkürzung für {@link #uniqueIterator(Iterator, Collection) uniqueIterator(that, new HashSet2<>())}. */
	public static <E> Iterator3<E> uniqueIterator(Iterator<? extends E> that) throws NullPointerException {
		return uniqueIterator(that, new HashSet2<>());
	}

	/** Diese Methode ist eine Abkürzung für {@link #uniqueIterator(Iterator, Collection) uniqueIterator(that, hashSetFrom(hasher))}. */
	public static <E> Iterator3<E> uniqueIterator(Iterator<? extends E> that, Hasher hasher) throws NullPointerException {
		return uniqueIterator(that, hashSetFrom(hasher));
	}

	/** Diese Methode liefert einen {@link Iterator3}, der kein Element des gegebenen {@link Iterator} mehrfach liefert. Zur Duplikaterkennung werden die Elemente
	 * in die gegebenen {@link Collection} eingefügt. Sie ist eine Abkürzung für {@link #filteredIterator(Iterator, Filter) filteredIterator(that,
	 * buffer::add)}. */
	public static <E> Iterator3<E> uniqueIterator(Iterator<? extends E> that, Collection<? super E> buffer) throws NullPointerException {
		notNull(buffer);
		return filteredIterator(that, buffer::add);
	}

	/** Diese Methode liefert einen übersetzten {@link Iterator3}, der die über den gegebenen {@link Getter} umgewandelte Elemente des gegebenen {@link Iterator}
	 * liefert. */
	public static <E2, E> Iterator3<E> translatedIterator(Iterator<? extends E2> that, Getter<? super E2, ? extends E> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return that.hasNext();
			}

			@Override
			public E next() {
				return trans.get(that.next());
			}

			@Override
			public void remove() {
				that.remove();
			}

		};
	}

	/** Diese Methode liefert einen unveränderlichen {@link Iterator3}, der beim Entfernen stets eine {@link UnsupportedOperationException} auslöst. */
	public static <E> Iterator3<E> unmodifiableIterator(Iterator<? extends E> that) throws NullPointerException {
		notNull(that);
		return new Iterator3<>() {

			@Override
			public boolean hasNext() {
				return that.hasNext();
			}

			@Override
			public E next() {
				return that.next();
			}

		};
	}

	/** Diese Methode liefert das {@code index}-te Elemente des gegebenen {@link Iterator} oder löst eine {@link NoSuchElementException} aus. */
	public static <E> E get(Iterator<? extends E> iter, int index) throws NullPointerException, NoSuchElementException {
		if ((index < 0) || (skip(iter, index) != 0) || !iter.hasNext()) throw new NoSuchElementException();
		return iter.next();
	}

	/** Diese Methode gibt die Anzahl der vom gegebenen {@link Iterator} gelieferten Elemente zurück. */
	public static int size(Iterator<?> iter) {
		return -skip(iter, -1) - 1;
	}

	/** Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die Anzahl der noch zu überspringenden
	 * Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene Iterator {@link Iterator#hasNext() anzeigt}, dass er keine weiteren Elemente
	 * mehr liefern kann. Wenn die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen Iterator zurück
	 * gegeben. Damit bestimmt {@code (-skip(iter, -1) - 1)} die Anzahl der Elemente des gegebenen Iterator. */
	public static int skip(Iterator<?> iter, int count) throws NullPointerException {
		while (iter.hasNext() && (count != 0)) {
			count--;
			iter.next();
		}
		return count;
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterator} in die gegebene {@link Collection} ein und gibt nur dann {@code true} zurück, wenn
	 * Elemente eingefügt wurden.
	 *
	 * @see Collection#addAll(Collection) */
	public static <E> boolean addAll(Collection<E> target, Iterator<? extends E> source) throws NullPointerException {
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
	public static boolean retainAll(Iterator<?> target, Collection<?> filter) throws NullPointerException {
		return removeAll(filteredIterator(target, filterFromItems(filter).negate()));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection) */
	public static boolean retainAll(Collection<?> target, Iterator<?> filter) throws NullPointerException {
		return target.retainAll(toSet(filter));
	}

	/** Diese Methode {@link Iterator#remove() entfernt} alle Elemente des gegebenen {@link Iterator} und gibt nur dann {@code true} zurück, wenn Elemente
	 * entfernt wurden.
	 *
	 * @return {@code true} bei Veränderungen am {@link Iterator}. */
	public static boolean removeAll(Iterator<?> target) throws NullPointerException {
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
	public static boolean removeAll(Iterator<?> target, Collection<?> filter) throws NullPointerException {
		return removeAll(filteredIterator(target, filterFromItems(filter)));
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator} aus der gegebenen {@link Collection} und gibt nur dann {@code true} zurück, wenn
	 * Elemente entfernt wurden.
	 *
	 * @see Collection#removeAll(Collection)
	 * @return {@code true} bei Veränderungen an der {@link Collection}. */
	public static boolean removeAll(Collection<?> target, Iterator<?> filter) throws NullPointerException {
		return target.removeAll(toSet(filter));
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterator} in der gegebenen {@link Collection} enthalten sind.
	 *
	 * @see Collection#containsAll(Collection)
	 * @return {@code true} bei vollständiger Inklusion. */
	public static boolean containsAll(Collection<?> target, Iterator<?> filter) throws NullPointerException {
		notNull(target);
		while (filter.hasNext()) {
			if (!target.contains(filter.next())) return false;
		}
		return true;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link Set}. Hierbei wird eine über {@link #addAll(Collection, Iterator)} befülltes
	 * {@link HashSet2} geliefert. */
	public static <E> Set<E> toSet(Iterator<? extends E> source) throws NullPointerException {
		var result = new HashSet2<E>();
		addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterator} als {@link List}. Hierbei wird eine über {@link #addAll(Collection, Iterator)} befüllte
	 * {@link ArrayList} geliefert. */
	public static <E> List<E> toList(Iterator<? extends E> source) throws NullPointerException {
		var result = new ArrayList<E>();
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
	public static <E> E[] toArray(Iterator<?> source, E[] array) throws NullPointerException {
		return toList(source).toArray(array);
	}

	private static final Iterator3<?> emptyIterator = iteratorFromCount(0);

}
