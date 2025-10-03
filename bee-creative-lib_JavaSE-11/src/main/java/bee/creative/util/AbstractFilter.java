package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Filter} als {@link BaseObject}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Datensätze. */
public abstract class AbstractFilter<ITEM> extends BaseObject implements Filter<ITEM> {

	@Override
	public boolean accept(ITEM item) {
		return item != null;
	}

}