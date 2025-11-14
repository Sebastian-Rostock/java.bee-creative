package bee.creative.util;

import static bee.creative.util.Iterators.concatIterator;
import static bee.creative.util.Iterators.filteredIterator;
import static bee.creative.util.Iterators.limitedIterator;
import static bee.creative.util.Iterators.translatedIterator;
import static bee.creative.util.Iterators.uniqueIterator;
import static bee.creative.util.Iterators.unmodifiableIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterator} insb. um eine Anbindung an Methoden von {@link Iterators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente. */
public interface Iterator3<E> extends Iterator<E> {

	/** Diese Methode ist eine Abkürzung für {@link Iterators#skip(Iterator, int) Iterators.skip(this, count)}. */
	default int skip(int count) {
		return Iterators.skip(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#limitedIterator(Iterator, int) limitedIterator(this, limit)}. */
	default Iterator3<E> limit(int limit) throws IllegalArgumentException {
		return limitedIterator(this, limit);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#filteredIterator(Iterator, Filter) filteredIterator(this, filter)}. */
	default Iterator3<E> filter(Filter<? super E> filter) throws NullPointerException {
		return filteredIterator(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#concatIterator(Iterator, Iterator) concatIterator(this, that)}. */
	default Iterator3<E> concat(Iterator<? extends E> that) throws NullPointerException {
		return concatIterator(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator) uniqueIterator(this)}. */
	default Iterator3<E> unique() {
		return uniqueIterator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator, Hasher) uniqueIterator(this, hasher)}. */
	default Iterator3<E> unique(Hasher hasher) throws NullPointerException {
		return uniqueIterator(this, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator, Collection) uniqueIterator(this, buffer)}. */
	default Iterator3<E> unique(Collection<E> buffer) throws NullPointerException {
		return uniqueIterator(this, buffer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translatedIterator(Iterator, Getter) translatedIterator(this, trans)}. */
	default <GTarget> Iterator3<GTarget> translate(Getter<? super E, ? extends GTarget> trans) throws NullPointerException {
		return translatedIterator(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unmodifiableIterator(Iterator) unmodifiableIterator(this)}. */
	default Iterator3<E> unmodifiable() {
		return unmodifiableIterator(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#retainAll(Iterator, Collection) Iterators.retainAll(this, filter)}. */
	default boolean retainAll(Collection<?> filter) throws NullPointerException {
		return Iterators.retainAll(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#removeAll(Iterator) Iterators.removeAll(this)}. */
	default boolean removeAll() {
		return Iterators.removeAll(this);
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
	default <E2> E2[] toArray(E2[] array) throws NullPointerException {
		return Iterators.toArray(this, array);
	}

}
