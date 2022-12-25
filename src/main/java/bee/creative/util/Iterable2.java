package bee.creative.util;

import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterable} insb. um eine Anbindung an Methoden von {@link Iterables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Iterable2<GItem> extends Iterable<GItem> {

	@Override
	public Iterator2<GItem> iterator();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concat(Iterable, Iterable) Iterables.concat(this, second)}. */
	public Iterable2<GItem> concat(Iterable<? extends GItem> second) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterables#size(Iterable) Iterables.size(this)}. */
	public int size();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#filter(Iterable, Filter) Iterables.filter(this, filter)}. */
	public Iterable2<GItem> filter(Filter<? super GItem> filter) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterables#limit(Iterable, int) Iterables.limit(this, count)}. */
	public Iterable2<GItem> limit(int count) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link Iterables#repeat(Iterable, int) Iterables.repeat(this, count)}. */
	public Iterable2<GItem> repeat(int count) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link Iterables#translate(Iterable, Getter) Iterables.translate(this, trans)}. */
	public <GItem2> Iterable2<GItem2> translate(Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unique(Iterable) Iterables.unique(this)}. */
	public Iterable2<GItem> unique();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#unmodifiable(Iterable) Iterables.unmodifiable(this)}. */
	public Iterable2<GItem> unmodifiable();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toSet(Iterable) Iterables.toSet(this)}. */
	public Set<GItem> toSet();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toList(Iterable) Iterables.toList(this)}. */
	public List<GItem> toList();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toArray(Iterable) Iterables.toArray(this)}. */
	public Object[] toArray();

	/** Diese Methode ist eine Abkürzung für {@link Iterables#toArray(Iterable, Object[]) Iterables.toArray(this, array)}. */
	public GItem[] toArray(GItem[] array) throws NullPointerException;

}
