package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Filter2} als {@link BaseObject}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public abstract class AbstractFilter<GItem> extends BaseObject implements Filter2<GItem> {

	@Override
	public boolean accept(GItem item) {
		return item != null;
	}

}