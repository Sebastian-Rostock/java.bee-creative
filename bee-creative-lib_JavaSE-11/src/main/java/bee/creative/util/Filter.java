package bee.creative.util;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Datensätze über {@link Filter#accept(Object)} akzeptieren oder ablehnen kann.
 *
 * @see Filters
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Datensätze. */
public interface Filter<ITEM> {

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Datensatz akzeptiert wird.
	 *
	 * @param item Datensätze.
	 * @return Akzeptanz. */
	boolean accept(ITEM item);

}
