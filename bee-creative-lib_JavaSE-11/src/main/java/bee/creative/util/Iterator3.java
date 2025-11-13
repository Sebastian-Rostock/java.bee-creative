package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterator} insb. um eine Anbindung an Methoden von {@link Iterators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente. */
public interface Iterator3<E> extends Iterator<E> {

	/** Diese Methode ist eine Abkürzung für {@link Iterators#concatIterator(Iterator, Iterator) Iterators.concat(this, that)}. */
	default Iterator3<E> concat(Iterator<? extends E> that) throws NullPointerException {
		return Iterators.concatIterator(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#iteratorRetainAll(Iterator, Collection) Iterators.retainAll(this, filter)}. */
	default boolean retainAll(Collection<?> filter) throws NullPointerException {
		return Iterators.iteratorRetainAll(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#iteratorRemoveAll(Iterator) Iterators.removeAll(this)}. */
	default boolean removeAll() {
		return Iterators.iteratorRemoveAll(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#iteratorSkip(Iterator, int) Iterators.skip(this, count)}. */
	default int skip(int count) {
		return Iterators.iteratorSkip(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#filteredIterator(Iterator, Filter) Iterators.filter(this, filter)}. */
	default Iterator3<E> filter(Filter<? super E> filter) throws NullPointerException {
		return Iterators.filteredIterator(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#limitedIterator(Iterator, int) Iterators.limit(this, count)}. */
	default Iterator3<E> limit(int count) throws IllegalArgumentException {
		return Iterators.limitedIterator(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translatedIterator(Iterator, Getter) Iterators.translate(this, trans)}. */
	default <GTarget> Iterator3<GTarget> translate(Getter<? super E, ? extends GTarget> trans) throws NullPointerException {
		return Iterators.translatedIterator(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator) Iterators.unique(this)}. */
	default Iterator3<E> unique() {
		return Iterators.uniqueIterator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator, Hasher) Iterators.unique(this, hasher)}. */
	default Iterator3<E> unique(Hasher hasher) throws NullPointerException {
		return Iterators.uniqueIterator(this, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator, Collection) Iterators.unique(this, buffer)}. */
	default Iterator3<E> unique(Collection<E> buffer) throws NullPointerException {
		return Iterators.uniqueIterator(this, buffer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unmodifiableIterator(Iterator) Iterators.unmodifiable(this)}. */
	default Iterator3<E> unmodifiable() {
		return Iterators.unmodifiableIterator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#forEachRemaining(Iterator, Consumer) Iterators.collectAll(this, target)}. */
	default void forEachRemaining(Consumer<? super E> target) throws NullPointerException {
		Iterators.forEachRemaining(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toSet(Iterator) Iterators.toSet(this)}. */
	default Set<E> toSet() {
		return Iterators.toSet(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toList(Iterator) Iterators.toList(this)}. */
	default List<E> toList() {
		return Iterators.toList(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toArray(Iterator) Iterators.toArray(this)}. */
	default Object[] toArray() {
		return Iterators.toArray(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toArray(Iterator, Object[]) Iterators.toArray(this, array)}. */
	default E[] toArray(E[] array) throws NullPointerException {
		return Iterators.toArray(this, array);
	}

}
