package bee.creative.util;

/**
 * Diese Schnittstelle definiert einen Zeiger bzw. Verweis auf einen Datensatz.
 * 
 * @see Pointers
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
	 * Diese Methode gibt den via {@link Objects#hash(Object)} berechneten {@link Object#hashCode() Streuwert} des Datensatzes zurück.
	 * 
	 * @return {@link Object#hashCode() Streuwert} des Datensatzes.
	 */
	@Override
	public int hashCode();

	/**
	 * Diese Methode gibt die via {@link Objects#equals(Object, Object)} berechnete {@link Object#equals(Object) Äquivalenz} der Datensätze dieses und des
	 * gegebenenen {@link Pointer}s zurück.
	 * 
	 * @param object {@link Pointer}.
	 * @return {@link Object#equals(Object) Äquivalenz} der Datensätze.
	 */
	@Override
	public boolean equals(Object object);

}
