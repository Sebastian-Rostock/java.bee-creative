package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Methode zur Erzeugung bzw. Bereitstellung eines (neuen) Datensatzes vom Typ
 * <code>GData</code>.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ des Datensatzes.
 */
public interface Builder<GData> {

	/**
	 * Diese Methode gibt eine einen (neuen) Datensatz zurück.
	 * 
	 * @return (neuer) Datensatz.
	 */
	public GData build();

}
