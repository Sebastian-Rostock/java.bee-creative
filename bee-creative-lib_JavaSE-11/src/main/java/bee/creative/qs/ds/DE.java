package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.qs.QS;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert ein Domänenkonstante (domain-enum) als {@link #node() Hyperknoten} mit Bezug zu einem {@link #parent() Domänenmodell} sowie
 * mit {@link #label() Beschriftung} und {@link #idents() Erkennungsmerkmalen}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface DE extends DO {

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DE#label()}-{@link DL Datenfeld}. */
	String IDENT_IsEnumWithLabel = "DM:IsEnumWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DE#idents()}-{@link DL Datenfeld}. */
	String IDENT_IsEnumWithIdent = "DM:IsEnumWithIdent";

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
	 * @see DE#IDENT_IsEnumWithLabel
	 * @see DL#asTargetProp(QN)
	 * @return Beschriftungsknoten. */
	default Property2<QN> label() {
		return this.parent().getLink(DE.IDENT_IsEnumWithLabel).asTargetProp(this.node());
	}

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
	 * @see DE#IDENT_IsEnumWithIdent
	 * @see DL#asTargetSet(QN)
	 * @return Erkennungsknotenmenge. */
	default Set2<QN> idents() {
		return this.parent().getLink(DE.IDENT_IsEnumWithIdent).getTargetSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN#value() Textwerte} zur {@link #idents() Erkennung} dieses Objekts.
	 *
	 * @see QS#valueTrans()
	 * @return Erkennungstextwerte. */
	default Set2<String> identsAsStrings() {
		return this.idents().translate(this.owner().valueTrans());
	}

}
