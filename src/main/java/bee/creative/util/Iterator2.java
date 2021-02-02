package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;

/** Diese Schnittstelle ergänzt einen {@link Iterator} insb. um eine Anbindung an Methoden von {@link Iterators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Iterator2<GItem> extends Iterator<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Iterators#concat(Iterator, Iterator) Iterators.concat(this, that)}. */
	public Iterator2<GItem> concat(final Iterator<? extends GItem> that) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#skip(Iterator, int) Iterators.skip(this, count)}. */
	public int skip(int count);

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toFiltered(Iterator, Filter) Iterators.toFiltered(this, filter)}. */
	public Iterator2<GItem> toFiltered(final Filter<? super GItem> filter) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toLimited(Iterator, int) Iterators.toLimited(this, count)}. */
	public Iterator2<GItem> toLimited(final int count) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toTranslated(Iterator, Getter) Iterators.toTranslated(this, trans)}. */
	public <GTarget> Iterator2<GTarget> toTranslated(final Getter<? super GItem, ? extends GTarget> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toUnique(Iterator) Iterators.toUnique(this)}. */
	public Iterator2<GItem> toUnique();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toUnique(Iterator) Iterators.toUnique(this, buffer)}. */
	public Iterator2<GItem> toUnique(final Collection<GItem> buffer) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toUnmodifiable(Iterator) Iterators.toUnmodifiable(this)}. */
	public Iterator2<GItem> toUnmodifiable();

}
