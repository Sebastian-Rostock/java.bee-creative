package bee.creative.qs.ds;

import bee.creative.qs.QO;

/** Diese Schnittstelle definiert ein Datenobjekt (Domain-Object) mit Bezug zu einem {@link #parent() Datenmodell}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DO extends QO {

	/** Diese Methode liefet das dieses Objekt verwaltende Datenmodell.
	 *
	 * @return Datenmodell. */
	DM parent();

}
