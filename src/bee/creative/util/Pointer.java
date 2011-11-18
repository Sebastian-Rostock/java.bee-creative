package bee.creative.util;

/**
 * Diese Schnittstelle definiert einen Verweis auf einen Datensatz.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ des Datensatzes.
 */
public interface Pointer<GData> {

	/**
	 * Diese Methode gibt den Datensatz zurück.
	 * 
	 * @return Datensatz.
	 */
	public GData data();

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des Datensatzes zurück.
	 * 
	 * @return {@link Object#hashCode() Streuwert} des Datensatzes.
	 */
	@Override
	public int hashCode();

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} dieses und des des gegebenene Verweises zurück.
	 * Verweise sind äquivalent, wenn ihre Datensätze {@link Object#equals(Object) äquivalent} sind.
	 * 
	 * @param obj Objekt.
	 * @return {@link Object#equals(Object) Äquivalenz}.
	 */
	@Override
	public boolean equals(Object obj);

}
