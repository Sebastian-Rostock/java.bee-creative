package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
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
	public boolean retainAll(Collection<?> filter) throws NullPointerException {
		return false;
	}

	@Override
	public boolean removeAll() {
		return false;
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
	public Iterator2<GItem> toFiltered(final Filter<? super GItem> filter) throws NullPointerException {
		return Iterators.toFiltered(this, filter);
	}

	@Override
	public Iterator2<GItem> toLimited(final int count) throws IllegalArgumentException {
		return Iterators.toLimited(this, count);
	}

	@Override
	public <GTarget> Iterator2<GTarget> translate(final Getter<? super GItem, ? extends GTarget> trans) throws NullPointerException {
		return Iterators.translate(this, trans);
	}

	@Override
	public Iterator2<GItem> toUnique() {
		return Iterators.toUnique(this);
	}

	@Override
	public Iterator2<GItem> toUnique(final Collection<GItem> buffer) throws NullPointerException {
		return Iterators.toUnique(this, buffer);
	}

	@Override
	public Iterator2<GItem> toUnmodifiable() {
		return Iterators.toUnmodifiable(this);
	}

}