package bee.creative.qs.ds;

/** Diese Schnittstelle definiert ein Objekt mit Bezug zu einem {@link #model() Datenmodell}.
 * 
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DO {

	/** Diese Methode liefet das dieses Objekt verwaltende Datenmodell.
	 *
	 * @return Datenmodell. */
	DM model();

}
