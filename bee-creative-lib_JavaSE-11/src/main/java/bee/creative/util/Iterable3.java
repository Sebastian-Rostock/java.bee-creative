package bee.creative.util;

import static bee.creative.util.Iterables.concatIterable;
import static bee.creative.util.Iterables.filteredIterable;
import static bee.creative.util.Iterables.limitedIterable;
import static bee.creative.util.Iterables.repeatedIterable;
import static bee.creative.util.Iterables.translatedIterable;
import static bee.creative.util.Iterables.uniqueIterable;
import static bee.creative.util.Iterables.unmodifiableIterable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterable} insb. um eine Anbindung an Methoden von {@link Iterables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Elemente. */
public interface Iterable3<T> extends Iterable<T> {

	/** Diese Methode ist eine Abkürzung für {@link Iterables#limitedIterable(Iterable, int) limitedIterable(this, limit)}. */
	default Iterable3<T> limit(int limit) throws IllegalArgumentException {
		return limitedIterable(this, limit);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#filteredIterable(Iterable, Filter) filteredIterable(this, filter)}. */
	default Iterable3<T> filter(Filter<? super T> filter) throws NullPointerException {
		return filteredIterable(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#repeatedIterable(Iterable, int) repeatedIterable(this, count)}. */
	default Iterable3<T> repeat(int count) throws IllegalArgumentException {
		return repeatedIterable(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concatIterable(Iterable, Iterable) concatIterable(this, that)}. */
	default Iterable3<T> concat(Iterable<? extends T> that) throws NullPointerException {
		return concatIterable(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#uniqueIterable(Iterable) uniqueIterable(this)}. */
	default Iterable3<T> unique() {
		return uniqueIterable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterators#uniqueIterator(Iterator, Hasher) uniqueIterable(this, hasher)}. */
	default Iterable3<T> unique(Hasher hasher) throws NullPointerException {
		return uniqueIterable(this, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translatedIterable(Iterable, Getter) translatedIterable(this, trans)}. */
	default <T2> Iterable3<T2> translate(Getter<? super T, ? extends T2> trans) throws NullPointerException {
		return translatedIterable(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unmodifiableIterable(Iterable) unmodifiableIterable(this)}. */
	default Iterable3<T> unmodifiable() {
		return unmodifiableIterable(this);
	}

	@Override
	Iterator3<T> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#size(Iterable) Iterables.size(this)}. */
	default int size() {
		return Iterables.size(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toSet(Iterable) Iterables.toSet(this)}. */
	default Set<T> toSet() {
		return Iterables.toSet(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toList(Iterable) Iterables.toList(this)}. */
	default List<T> toList() {
		return Iterables.toList(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toArray(Iterable) Iterables.toArray(this)}. */
	default Object[] toArray() {
		return Iterables.toArray(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toArray(Iterable, Object[]) Iterables.toArray(this, array)}. */
	default <E> E[] toArray(E[] array) throws NullPointerException {
		return Iterables.toArray(this, array);
	}

}
