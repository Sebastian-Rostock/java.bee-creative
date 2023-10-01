package bee.creative.app.ft;

/** Diese Schnittstelle definiert ein speicherbares Objekt.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
interface AppStorable {

	/** Diese Methode speichert die Daten dieses Objekts. */
	void persist();

	/** Diese Methode lädt die Daten dieses Objekts. */
	void restore();

}
