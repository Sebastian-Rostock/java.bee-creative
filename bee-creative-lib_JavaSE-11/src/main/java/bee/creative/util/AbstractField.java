package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Field3} als {@link BaseObject}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractField<GItem, GValue> extends BaseObject implements Field3<GItem, GValue> {

	@Override
	public GValue get(GItem item) {
		return null;
	}

	@Override
	public void set(GItem item, GValue value) {
	}

}