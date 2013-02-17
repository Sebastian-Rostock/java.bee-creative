package bee.creative.util;

/**
 * Diese Schnittstelle definiert eine Methode zur Erzeugung oder Bereitstellung eines Datensatzes.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ des Datensatzes.
 */
public interface Builder<GValue> {

	/**
	 * Diese Methode gibt einen Datensatz zurück, der neu erzeugt sein kann.
	 * 
	 * @return Datensatz.
	 */
	public GValue create();

}
