package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterator} insb. um eine Anbindung an Methoden von {@link Iterators}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Iterator2<GItem> extends Iterator<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Iterators#concat(Iterator, Iterator) Iterators.concat(this, that)}. */
	public Iterator2<GItem> concat(Iterator<? extends GItem> that) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#retainAll(Iterator, Collection) Iterators.retainAll(this, filter)}. */
	public boolean retainAll(Collection<?> filter) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#removeAll(Iterator) Iterators.removeAll(this)}. */
	public boolean removeAll();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#skip(Iterator, int) Iterators.skip(this, count)}. */
	public int skip(int count);

	/** Diese Methode ist eine Abkürzung für {@link Iterators#filter(Iterator, Filter) Iterators.filter(this, filter)}. */
	public Iterator2<GItem> filter(Filter<? super GItem> filter) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#limit(Iterator, int) Iterators.limit(this, count)}. */
	public Iterator2<GItem> limit(int count) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#translate(Iterator, Getter) Iterators.translate(this, trans)}. */
	public <GTarget> Iterator2<GTarget> translate(Getter<? super GItem, ? extends GTarget> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unique(Iterator) Iterators.unique(this)}. */
	public Iterator2<GItem> unique();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unique(Iterator, Hasher) Iterators.unique(this, hasher)}. */
	public Iterator2<GItem> unique(Hasher hasher) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unique(Iterator, Collection) Iterators.unique(this, buffer)}. */
	public Iterator2<GItem> unique(Collection<GItem> buffer) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#unmodifiable(Iterator) Iterators.unmodifiable(this)}. */
	public Iterator2<GItem> unmodifiable();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#collectAll(Iterator, Consumer) Iterators.collectAll(this, target)}. */
	public void collectAll(final Consumer<? super GItem> target) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toSet(Iterator) Iterators.toSet(this)}. */
	public Set<GItem> toSet();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toList(Iterator) Iterators.toList(this)}. */
	public List<GItem> toList();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toArray(Iterator) Iterators.toArray(this)}. */
	public Object[] toArray();

	/** Diese Methode ist eine Abkürzung für {@link Iterators#toArray(Iterator, Object[]) Iterators.toArray(this, array)}. */
	public GItem[] toArray(GItem[] array) throws NullPointerException;

}
