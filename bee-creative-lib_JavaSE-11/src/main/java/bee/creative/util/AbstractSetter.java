package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Setter3} als {@link BaseObject}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensatzes.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractSetter<GItem, GValue> extends BaseObject implements Setter3<GItem, GValue> {

	@Override
	public void set(GItem item, GValue value) {
	}

}