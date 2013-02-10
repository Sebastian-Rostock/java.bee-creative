package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Methode zur Erzeugung eines Datensatzes.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GData> Typ des Datensatzes.
 */
public interface Builder<GData> {

	/**
	 * Diese Methode erzeugt einen Datensatz und gibt ihn zurück.
	 * 
	 * @return Datensatz.
	 */
	public GData create();

}
