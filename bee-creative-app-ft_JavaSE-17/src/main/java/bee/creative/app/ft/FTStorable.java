package bee.creative.app.ft;

/** Diese Klasse implementiert ein speicherbares Objekt.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface FTStorable {

	/** Diese Methode speichert die Daten dieses Objekts. */
	void persist();

	/** Diese Methode lädt die Daten dieses Objekts. */
	void restore();

}
