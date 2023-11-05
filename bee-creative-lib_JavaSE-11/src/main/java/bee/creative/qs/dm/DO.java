package bee.creative.qs.dm;

import bee.creative.qs.QO;

/** Diese Schnittstelle definiert ein Domänenobjekt (domain-object) mit Bezug zu einem {@link #parent() Domänenmodell}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DO extends QO {

	/** Diese Methode liefet das dieses Objekt verwaltende Domänenmodell.
	 *
	 * @return Domänenmodell. */
	DM parent();

}
