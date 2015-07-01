package bee.creative.data;

/**
 * Diese Schnittstelle definiert ein Objekt mit Beschriftung, die das Objekt dem Nutzer gegenüber erkennbar macht.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Labeled {

	/**
	 * Diese Methode gibt die Beschriftung zurück, die das Objekt dem Nutzer gegenüber erkennbar macht.
	 * 
	 * @return Beschriftung.
	 */
	public String label();

}
