package bee.creative.util;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Objekte über {@link Filter#accept(Object)} akzeptieren oder ablehnen kann.
 *
 * @see Filters
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente. */
public interface Filter<GItem> {

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die Eingabe akzeptiert wird.
	 *
	 * @param item Eingabe.
	 * @return Eingabeakzeptanz. */
	public boolean accept(GItem item);

}
