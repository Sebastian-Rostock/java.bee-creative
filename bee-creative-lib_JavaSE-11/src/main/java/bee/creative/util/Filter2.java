package bee.creative.util;

import static bee.creative.util.Filters.filterFrom;

/** Diese Schnittstelle definiert einen {@link Filter} mit {@link Filter3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Datensätze. */
public interface Filter2<T> extends Filter<T> {

	/** Diese Methode liefert die {@link Filter3}-Schnittstelle zu {@link #accepts(Object)}. */
	default Filter3<T> asFilter() {
		return filterFrom(this);
	}

}
