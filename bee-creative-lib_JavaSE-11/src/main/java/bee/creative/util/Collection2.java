package bee.creative.util;

import static bee.creative.util.Collections.concatCollection;
import static bee.creative.util.Collections.translatedCollection;
import java.util.Collection;

/** Diese Schnittstelle definiert eine {@link Collection} mit {@link Iterator3}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Collection2<E> extends Collection<E>, Filter<Object> {

	/** Diese Methode delegiert an {@link #contains(Object)}. */
	@Override
	default boolean accepts(Object item) {
		return this.contains(item);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#addAll(java.util.Collection, Iterable) Iterables.addAll(this, c)}. */
	default boolean addAll(Iterable<? extends E> c) {
		return Iterables.addAll(this, c);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#retainAll(java.util.Collection, Iterable) Iterables.retainAll(this, c)}. */
	default boolean retainAll(Iterable<?> c) {
		return Iterables.retainAll(this, c);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#removeAll(java.util.Collection, Iterable) Iterables.removeAll(this, c)}. */
	default boolean removeAll(Iterable<?> c) {
		return Iterables.removeAll(this, c);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#replaceAll(java.util.Collection, Iterable) Iterables.replaceAll(this, c)}. */
	default boolean replaceAll(Iterable<? extends E> c) {
		return Iterables.replaceAll(this, c);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#containsAll(java.util.Collection, Iterable) Iterables.containsAll(this, c)}. */
	default boolean containsAll(Iterable<?> c) {
		return Iterables.containsAll(this, c);
	}

	/** Diese Methode ist eine Abkürzung für {@link #asConcatCollection(Collection, boolean) this.asConcatCollection(that, true)}. */
	default Collection2<E> asConcatCollection(Collection<E> that) throws NullPointerException {
		return this.asConcatCollection(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concatCollection(Collection, Collection, boolean) concatCollection(this, that, extendMode)}. */
	default Collection2<E> asConcatCollection(Collection<E> that, boolean extendMode) throws NullPointerException {
		return concatCollection(this, that, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#translatedCollection(Collection, Translator) translatedCollection(this, trans)}. */
	default <E2> Collection2<E2> asTranslatedCollection(Translator<E, E2> trans) throws NullPointerException {
		return translatedCollection(this, trans);
	}

	@Override
	Iterator3<E> iterator();

}
