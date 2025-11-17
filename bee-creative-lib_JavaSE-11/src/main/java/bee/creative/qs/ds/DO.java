package bee.creative.qs.ds;

import bee.creative.qs.QO;
import bee.creative.qs.QS;

/** Diese Schnittstelle definiert ein Domänenobjekt (domain-object) mit Bezug zu einem {@link #parent() Domänenmodell}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DO extends QO {

	/** Diese Methode liefet das dieses Objekt verwaltende Domänenmodell.
	 *
	 * @return Domänenmodell. */
	DM parent();

	@Override
	default QS owner() {
		return this.parent().owner();
	}

}
