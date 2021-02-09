package bee.creative.util;

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
	public Iterator2<GItem> iterator() {
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
	public Iterable2<GItem> limit(int count) throws IllegalArgumentException {
		return Iterables.limit(this, count);
	}

	@Override
	public Iterable2<GItem> filter(Filter<? super GItem> filter) throws NullPointerException {
		return Iterables.filter(this, filter);
	}

	@Override
	public Iterable2<GItem> unique() {
		return Iterables.unique(this);
	}

	@Override
	public Iterable2<GItem> repeat(int count) throws IllegalArgumentException {
		return Iterables.repeat(this, count);
	}

	@Override
	public <GItem2> Iterable2<GItem2> translate(Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
		return Iterables.translate(this, trans);
	}

	@Override
	public Iterable2<GItem> unmodifiable() {
		return Iterables.unmodifiable(this);
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