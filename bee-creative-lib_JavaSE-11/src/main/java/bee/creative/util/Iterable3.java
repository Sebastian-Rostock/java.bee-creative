package bee.creative.util;

import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterable} insb. um eine Anbindung an Methoden von {@link Iterables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Iterable3<GItem> extends Iterable<GItem> {

	@Override
	Iterator3<GItem> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#size(Iterable) Iterables.size(this)}. */
	default int size() {
		return Iterables.size(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concatIterable(Iterable, Iterable) Iterables.concat(this, that)}. */
	default Iterable3<GItem> concat(final Iterable<? extends GItem> that) throws NullPointerException {
		return Iterables.concatIterable(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#limitedIterable(Iterable, int) Iterables.limit(this, count)}. */
	default Iterable3<GItem> limit(final int count) throws IllegalArgumentException {
		return Iterables.limitedIterable(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#filteredIterable(Iterable, Filter) Iterables.filter(this, filter)}. */
	default Iterable3<GItem> filter(final Filter<? super GItem> filter) throws NullPointerException {
		return Iterables.filteredIterable(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unique(Iterable) Iterables.unique(this)}. */
	default Iterable3<GItem> unique() {
		return Iterables.unique(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#repeatedIterable(Iterable, int) Iterables.repeat(this, count)}. */
	default Iterable3<GItem> repeat(final int count) throws IllegalArgumentException {
		return Iterables.repeatedIterable(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translatedIterable(Iterable, Getter) Iterables.translate(this, trans)}. */
	default <GItem2> Iterable3<GItem2> translate(final Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
		return Iterables.translatedIterable(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unmodifiable(Iterable) Iterables.unmodifiable(this)}. */
	default Iterable3<GItem> unmodifiable() {
		return Iterables.unmodifiable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#forEach(Iterable, Consumer) Iterables.collectAll(this, target)}. */
	default void collectAll(final Consumer<? super GItem> target) throws NullPointerException {
		Iterables.forEach(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#iterableToSet(Iterable) Iterables.toSet(this)}. */
	default Set<GItem> toSet() {
		return Iterables.iterableToSet(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toList(Iterable) Iterables.toList(this)}. */
	default List<GItem> toList() {
		return Iterables.toList(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toArray(Iterable) Iterables.toArray(this)}. */
	default Object[] toArray() {
		return Iterables.toArray(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toArray(Iterable, Object[]) Iterables.toArray(this, array)}. */
	default GItem[] toArray(final GItem[] array) throws NullPointerException {
		return Iterables.toArray(this, array);
	}

}
