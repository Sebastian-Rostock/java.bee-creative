package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Comparator2} als {@link BaseObject}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe. */
public abstract class AbstractComparator<GItem> extends BaseObject implements Comparator2<GItem> {

	@Override
	public int compare(final GItem o1, final GItem o2) {
		return 0;
	}

}