package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Getter3} als {@link BaseObject}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractGetter<GItem, GValue> extends BaseObject implements Getter3<GItem, GValue> {

	@Override
	public GValue get(GItem item) {
		return null;
	}

}