package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Objects.UseToString;

/** Diese Klasse implementiert einen abstrakten {@link Iterator2} als {@link BaseObject} mit {@link UseToString}-Markierung.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class AbstractIterator<GItem> extends BaseObject implements Iterator2<GItem>, UseToString {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public GItem next() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(final Collection<?> filter) throws NullPointerException {
		return Iterators.retainAll(this, filter);
	}

	@Override
	public boolean removeAll() {
		return Iterators.removeAll(this);
	}

	@Override
	public Iterator2<GItem> concat(final Iterator<? extends GItem> second) throws NullPointerException {
		return Iterators.concat(this, second);
	}

	@Override
	public int skip(final int count) {
		return Iterators.skip(this, count);
	}

	@Override
	public Iterator2<GItem> filter(final Filter<? super GItem> filter) throws NullPointerException {
		return Iterators.filter(this, filter);
	}

	@Override
	public Iterator2<GItem> limit(final int count) throws IllegalArgumentException {
		return Iterators.limit(this, count);
	}

	@Override
	public <GTarget> Iterator2<GTarget> translate(final Getter<? super GItem, ? extends GTarget> trans) throws NullPointerException {
		return Iterators.translate(this, trans);
	}

	@Override
	public Iterator2<GItem> unique() {
		return Iterators.unique(this);
	}

	@Override
	public Iterator2<GItem> unique(final Hasher hasher) throws NullPointerException {
		return Iterators.unique(this, hasher);
	}

	@Override
	public Iterator2<GItem> unique(final Collection<GItem> buffer) throws NullPointerException {
		return Iterators.unique(this, buffer);
	}

	@Override
	public Iterator2<GItem> unmodifiable() {
		return Iterators.unmodifiable(this);
	}

	@Override
	public void collectAll(final Consumer<? super GItem> target) throws NullPointerException {
		Iterators.collectAll(this, target);
	}

	@Override
	public Set<GItem> toSet() {
		return Iterators.toSet(this);
	}

	@Override
	public List<GItem> toList() {
		return Iterators.toList(this);
	}

	@Override
	public Object[] toArray() {
		return Iterators.toArray(this);
	}

	@Override
	public GItem[] toArray(final GItem[] array) throws NullPointerException {
		return Iterators.toArray(this, array);
	}

}