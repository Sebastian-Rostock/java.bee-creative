package bee.creative.data.cache;

/**
 * Diese Klasse implementiert ein Objekt zur Vorhaltung von Nutzdaten, deren Wiederverwendungen via {@link #uses} gezählt wird.
 * 
 * @see MFUCache
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class MFUCachePage {

	/**
	 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
	 */
	public int uses = 1;

}