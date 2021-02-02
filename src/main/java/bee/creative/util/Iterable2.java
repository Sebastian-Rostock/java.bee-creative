package bee.creative.util;

import java.util.List;
import java.util.Set;

/** Diese Schnittstelle ergänzt einen {@link Iterable} insb. um eine Anbindung an Methoden von {@link Iterables}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Iterable2<GItem> extends Iterable<GItem> {

	/** Diese Methode ist eine Abkürtung für {@link Iterables#concat(Iterable, Iterable) Iterables.concat(this, second)}. */
	public Iterable<GItem> concat(final Iterable<? extends GItem> second) throws NullPointerException;

	/** Diese Methode ist eine Abkürtung für {@link Iterables#size(Iterable) Iterables.size(this)}. */
	public int size();

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toArray(Iterable) Iterables.toArray(this)}. */
	public Object[] toArray();

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toArray(Iterable, Object[]) Iterables.toArray(this, array)}. */
	public GItem[] toArray(final GItem[] array) throws NullPointerException;

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toFiltered(Iterable, Filter) Iterables.toFiltered(this, filter)}. */
	public Iterable2<GItem> toFiltered(final Filter<? super GItem> filter) throws NullPointerException;

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toLimited(Iterable, int) Iterables.toLimited(this, count)}. */
	public Iterable2<GItem> toLimited(final int count) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toList(Iterable) Iterables.toList(this)}. */
	public List<GItem> toList();

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toRepeated(Iterable, int) Iterables.toRepeated(this, count)}. */
	public Iterable2<GItem> toRepeated(final int count) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toSet(Iterable) Iterables.toSet(this)}. */
	public Set<GItem> toSet();

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toTranslated(Iterable, Getter) Iterables.toTranslated(this, trans)}. */
	public <GItem2> Iterable2<GItem2> toTranslated(final Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toUnique(Iterable) Iterables.toUnique(this)}. */
	public Iterable2<GItem> toUnique();

	/** Diese Methode ist eine Abkürtung für {@link Iterables#toUnmodifiable(Iterable) Iterables.toUnmodifiable(this)}. */
	public Iterable2<GItem> toUnmodifiable();

}
