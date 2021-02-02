package bee.creative.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Objects.UseToString;

/** Diese Klasse implementiert ein abstraktes {@link Iterable2} als {@link BaseObject} mit {@link UseToString}-Merkierung.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class AbstractIterable<GItem> extends BaseObject implements Iterable2<GItem>, UseToString {

	@Override
	public Iterator<GItem> iterator() {
		return Iterators.empty();
	}

	@Override
	public int size() {
		return Iterables.size(this);
	}

	@Override
	public Iterable<GItem> concat(Iterable<? extends GItem> second) throws NullPointerException {
		return null;
	}

	@Override
	public Iterable2<GItem> toLimited(int count) throws IllegalArgumentException {
		return Iterables.toLimited(this, count);
	}

	@Override
	public Iterable2<GItem> toFiltered(Filter<? super GItem> filter) throws NullPointerException {
		return Iterables.toFiltered(this, filter);
	}

	@Override
	public Iterable2<GItem> toUnique() {
		return Iterables.toUnique(this);
	}

	@Override
	public Iterable2<GItem> toRepeated(int count) throws IllegalArgumentException {
		return Iterables.toRepeated(this, count);
	}

	@Override
	public <GItem2> Iterable2<GItem2> toTranslated(Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
		return Iterables.toTranslated(this, trans);
	}

	@Override
	public Iterable2<GItem> toUnmodifiable() {
		return Iterables.toUnmodifiable(this);
	}

	@Override
	public Set<GItem> toSet() {
		return Iterables.toSet(this);
	}

	@Override
	public List<GItem> toList() {
		return Iterables.toList(this);
	}

	@Override
	public Object[] toArray() {
		return Iterables.toArray(this);
	}

	@Override
	public GItem[] toArray(GItem[] array) throws NullPointerException {
		return Iterables.toArray(this, array);
	}

}