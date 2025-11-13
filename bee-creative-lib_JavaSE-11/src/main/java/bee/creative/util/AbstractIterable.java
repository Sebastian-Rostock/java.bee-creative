package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Objects.UseToString;

/** Diese Klasse implementiert ein abstraktes {@link Iterable3} als {@link BaseObject} mit {@link UseToString}-Merkierung.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class AbstractIterable<GItem> extends BaseObject implements Iterable3<GItem>, UseToString {

	@Override
	public Iterator3<GItem> iterator() {
		return Iterators.emptyIterator();
	}

}