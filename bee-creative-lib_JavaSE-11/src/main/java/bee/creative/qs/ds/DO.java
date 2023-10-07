package bee.creative.qs.ds;

import bee.creative.qs.QO;
import bee.creative.qs.QS;

/** Diese Schnittstelle definiert ein Objekt mit Bezug zu einem {@link #model() Datenmodell}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DO extends QO {

	@Override
	default QS owner() {
		return this.model().owner();
	}

	/** Diese Methode liefet das dieses Objekt verwaltende Datenmodell.
	 *
	 * @return Datenmodell. */
	DM model();

}
