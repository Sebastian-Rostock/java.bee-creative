package bee.creative.emu;

/** Diese Schnittstelle definiert eine Methode zur Ermittlung des geschätzten Speicherverbrauchs des Objekts. Dieser Speicherverbrauch sollte den der
 * ausschließlich intern verwalteten Objekte mit einschließen.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Emuable {

	/** Diese Methode gibt den geschätzten Speicherverbrauch dieses Objekts zurück.
	 *
	 * @return Speicherverbrauch. */
	long emu();

}
