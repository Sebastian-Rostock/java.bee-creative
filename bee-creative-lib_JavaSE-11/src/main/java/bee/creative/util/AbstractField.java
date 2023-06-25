package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Field2} als {@link BaseObject}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe.
 * @param <GValue> Typ des Werts der Eigenschaft. */
public abstract class AbstractField<GItem, GValue> extends BaseObject implements Field2<GItem, GValue> {

	@Override
	public GValue get(final GItem item) {
		return null;
	}

	@Override
	public void set(final GItem item, final GValue value) {
	}

}