package bee.creative.qs.dm;

import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert ein Domänenkonstante (domain-enum) als {@link #node() Hyperknoten} mit Bezug zu einem {@link #parent() Domänenmodell}, einer
 * {@link #label() Beschriftung} und {@link #idents() Erkennungsmerkmalen}.
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

	/** Diese Methode erlaubt Zugriff auf den {@link QN Hyperknoten} zur Beschriftung dieses Objekts. Dieser Beschriftungsknoten sollte dazu einen
	 * {@link QN#value() Textwert} besitzen oder beschreiben.
	 *
	 * @see DL#asTargetProperty(QN)
	 * @return Beschriftungsknoten. */
	Property2<QN> label();

	/** Diese Methode erlaubt Zugriff auf den {@link QN#value() Textwert} der {@link #label() Beschriftung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Beschriftungstextwert. */
	default Property2<String> labelAsString() {
		return this.label().translate(this.owner().valueTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die Menge der {@link QN Hyperknoten} zur Erkennung dieses Objekts. Diese Erkennungsknoten sollten dazu einen
	 * {@link QN#value() Textwert} besitzen.
	 *
	 * @see DL#asTargetSet(QN)
	 * @return Erkennungsknotenmenge. */
	Set2<QN> idents();

	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} zur {@link #idents() Erkennung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Erkennungstextwerte. */
	default Set2<String> identsAsStrings() {
		return this.idents().translate(this.owner().valueTrans());
	}

}
