package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterator} insb. um eine Anbindung an Methoden von {@link Iterators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente. */
public interface Iterator2<E> extends Iterator<E> {

	/** Diese Methode ist eine Abkürzung für {@link Iterators#concat(Iterator, Iterator) Iterators.concat(this, that)}. */
	default Iterator2<E> concat(Iterator<? extends E> that) throws NullPointerException {
		return Iterators.concat(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#retainAll(Iterator, Collection) Iterators.retainAll(this, filter)}. */
	default boolean retainAll(Collection<?> filter) throws NullPointerException {
		return Iterators.retainAll(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#removeAll(Iterator) Iterators.removeAll(this)}. */
	default boolean removeAll() {
		return Iterators.removeAll(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#skip(Iterator, int) Iterators.skip(this, count)}. */
	default int skip(int count) {
		return Iterators.skip(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#filter(Iterator, Filter) Iterators.filter(this, filter)}. */
	default Iterator2<E> filter(Filter<? super E> filter) throws NullPointerException {
		return Iterators.filter(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#limit(Iterator, int) Iterators.limit(this, count)}. */
	default Iterator2<E> limit(int count) throws IllegalArgumentException {
		return Iterators.limit(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translate(Iterator, Getter) Iterators.translate(this, trans)}. */
	default <GTarget> Iterator2<GTarget> translate(Getter<? super E, ? extends GTarget> trans) throws NullPointerException {
		return Iterators.translate(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unique(Iterator) Iterators.unique(this)}. */
	default Iterator2<E> unique() {
		return Iterators.unique(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unique(Iterator, Hasher) Iterators.unique(this, hasher)}. */
	default Iterator2<E> unique(Hasher hasher) throws NullPointerException {
		return Iterators.unique(this, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unique(Iterator, Collection) Iterators.unique(this, buffer)}. */
	default Iterator2<E> unique(Collection<E> buffer) throws NullPointerException {
		return Iterators.unique(this, buffer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unmodifiable(Iterator) Iterators.unmodifiable(this)}. */
	default Iterator2<E> unmodifiable() {
		return Iterators.unmodifiable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#collectAll(Iterator, Consumer) Iterators.collectAll(this, target)}. */
	default void collectAll(Consumer<? super E> target) throws NullPointerException {
		Iterators.collectAll(this, target);
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
