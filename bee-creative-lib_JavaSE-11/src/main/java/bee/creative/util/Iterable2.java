package bee.creative.util;

import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterable} insb. um eine Anbindung an Methoden von {@link Iterables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Iterable2<GItem> extends Iterable<GItem> {

	@Override
	Iterator2<GItem> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#size(Iterable) Iterables.size(this)}. */
	default int size() {
		return Iterables.size(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concat(Iterable, Iterable) Iterables.concat(this, that)}. */
	default Iterable2<GItem> concat(final Iterable<? extends GItem> that) throws NullPointerException {
		return Iterables.concat(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#limit(Iterable, int) Iterables.limit(this, count)}. */
	default Iterable2<GItem> limit(final int count) throws IllegalArgumentException {
		return Iterables.limit(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#filter(Iterable, Filter) Iterables.filter(this, filter)}. */
	default Iterable2<GItem> filter(final Filter<? super GItem> filter) throws NullPointerException {
		return Iterables.filter(this, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unique(Iterable) Iterables.unique(this)}. */
	default Iterable2<GItem> unique() {
		return Iterables.unique(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#repeat(Iterable, int) Iterables.repeat(this, count)}. */
	default Iterable2<GItem> repeat(final int count) throws IllegalArgumentException {
		return Iterables.repeat(this, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translate(Iterable, Getter) Iterables.translate(this, trans)}. */
	default <GItem2> Iterable2<GItem2> translate(final Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
		return Iterables.translate(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unmodifiable(Iterable) Iterables.unmodifiable(this)}. */
	default Iterable2<GItem> unmodifiable() {
		return Iterables.unmodifiable(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#collectAll(Iterable, Consumer) Iterables.collectAll(this, target)}. */
	default void collectAll(final Consumer<? super GItem> target) throws NullPointerException {
		Iterables.collectAll(this, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toSet(Iterable) Iterables.toSet(this)}. */
	default Set<GItem> toSet() {
		return Iterables.toSet(this);
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
