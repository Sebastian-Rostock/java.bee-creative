package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Comparable3} als {@link BaseObject}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe. */
public abstract class AbstractComparable<GItem> extends BaseObject implements Comparable3<GItem> {

	@Override
	public int compareTo(GItem item) {
		return 0;
	}

}