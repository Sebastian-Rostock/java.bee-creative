package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Filtermethode, die gegebene Objekte via {@link Filter#accept(Object)} akzeptieren oder ablehnen kann.
 * 
 * @see Filters
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GInput> Typ der Eingabe.
 */
public interface Filter<GInput> {

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn die Eingabe akzeptiert wird.
	 * 
	 * @param input Eingabe.
	 * @return Eingabeakzeptanz.
	 */
	public boolean accept(GInput input);

}
