package bee.creative.util;

import java.util.NoSuchElementException;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Objects.UseToString;

/** Diese Klasse implementiert einen abstrakten {@link Iterator3} als {@link BaseObject} mit {@link UseToString}-Markierung.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public abstract class AbstractIterator<GItem>  implements Iterator3<GItem>, UseToString {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public GItem next() {
		throw new NoSuchElementException();
	}

}