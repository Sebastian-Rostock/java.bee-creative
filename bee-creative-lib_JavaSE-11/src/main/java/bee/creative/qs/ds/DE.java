package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert ein Domänenkonstante (Domain-Enum) als {@link #node() Hyperknoten} mit Bezug zu einem {@link #parent() Domänenmodell}, einer
 * {@link #labelAsNode() Beschriftung} und {@link #identsAsNodes() Erkennungsmerkmalen}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DE extends DO {

	/** Diese Methode liefert den dieses Objekt repräsentierenden Hyperknoten.
	 *
	 * @return Hyperknoten, über den der Zustand dieses Objets gespeichert ist. */
	QN node();

	/** Diese Methode liefet den Graphspeicher des {@link #node() Hyperknoten}.
	 *
	 * @return Graphspeicher. */
	@Override
	default QS owner() {
		return this.parent().owner();
	}

	/** Diese Methode erlaubt Zugriff auf den {@link QN#value() Textwert} der {@link #labelAsNode() Beschriftung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Beschriftungstextwert. */
	default Property2<String> label() {
		return this.labelAsNode().translate(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf den {@link QN Hyperknoten} zur Beschriftung dieses Objekts. Dieser Beschriftungsknoten sollte dazu einen
	 * {@link QN#value() Textwert} besitzen oder beschreiben.
	 *
	 * @see DL#asTargetProperty(QN)
	 * @return Beschriftungsknoten. */
	Property2<QN> labelAsNode();

	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} zur {@link #identsAsNodes() Erkennung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Erkennungstextwerte. */
	default Set2<String> idents() {
		return this.identsAsNodes().translate(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN Hyperknoten} zur Erkennung dieses Objekts. Diese Erkennungsknoten sollten dazu einen {@link QN#value()
	 * Textwert} besitzen.
	 *
	 * @see DL#asTargetSet(QN)
	 * @return Erkennungsknotenmenge. */
	Set2<QN> identsAsNodes();

}
