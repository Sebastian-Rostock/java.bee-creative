package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Consumer3} als {@link BaseObject}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Werts. */
public abstract class AbstractConsumer<GValue> extends BaseObject implements Consumer3<GValue> {

	@Override
	public void set(final GValue value) {
	}

}