package bee.creative.data;

/**
 * Diese Schnittstelle definiert ein Objekt mit Besitzer.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des Besitzers.
 */
public interface Owned<GOwner> {

	/**
	 * Diese Methode gibt den Besitzer zurück.
	 * 
	 * @return Besitzer.
	 */
	public GOwner owner();

}
