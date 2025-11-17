package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Property3;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert ein Domänenkonstante (eomain-entity) als {@link #node() Hyperknoten} mit Bezug zu einem {@link #parent() Domänenmodell}, einer
 * {@link #labelAsNode() Beschriftung} und {@link #identsAsNodes() Erkennungsmerkmalen}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DE extends DN {

	/** Diese Methode erlaubt Zugriff auf den {@link QN#value() Textwert} der {@link #labelAsNode() Beschriftung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Beschriftungstextwert. */
	default Property3<String> label() {
		return this.labelAsNode().translate(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link QN Hyperknoten} zur Beschriftung dieses Objekts. Dieser Beschriftungsknoten sollte dazu einen
	 * {@link QN#value() Textwert} besitzen oder beschreiben.
	 *
	 * @see DL#asObjectProperty(QN)
	 * @return Beschriftungsknoten. */
	Property3<QN> labelAsNode();

	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} zur {@link #identsAsNodes() Erkennung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Erkennungstextwerte. */
	default Set2<String> idents() {
		return this.identsAsNodes().asTranslatedSet(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN Hyperknoten} zur Erkennung dieses Objekts. Diese Erkennungsknoten sollten dazu einen {@link QN#value()
	 * Textwert} besitzen.
	 *
	 * @see DL#asObjectSet(QN)
	 * @return Erkennungsknotenmenge. */
	Set2<QN> identsAsNodes();

}
