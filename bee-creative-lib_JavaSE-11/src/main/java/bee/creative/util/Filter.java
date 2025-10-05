package bee.creative.util;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Datensätze über {@link Filter#accepts(Object)} akzeptieren oder ablehnen kann.
 *
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Datensätze. */
public interface Filter<T> {

	/** Diese Methode liefert nur dann {@code true}, wenn der gegebene Datensatz akzeptiert wird.
	 *
	 * @param item Datensatz.
	 * @return Akzeptanz. */
	boolean accepts(T item);

}
