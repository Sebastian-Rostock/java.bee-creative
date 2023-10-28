package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

public interface DT extends DE {

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für den {@link DT Datentyp} von {@link DT}. */
	String IDENT_IsType = "DM:IsType";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DE#label()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithLabel = "DM:IsTypeWithLabel";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DE#idents()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithIdent = "DM:IsTypeWithIdent";

	/** Dieses Feld speichert den Textwert eines {@link DE#idents() Erkennungsknoten} für das {@link DT#instances()}-{@link DL Datenfeld}. */
	String IDENT_IsTypeWithInstance = "DM:IsTypeWithInstance";

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsTypeWithLabel */
	@Override
	default Property2<QN> label() {
		return this.parent().getLink(DT.IDENT_IsTypeWithLabel).asTargetProp(this.node());
	}

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsTypeWithIdent */
	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DT.IDENT_IsTypeWithIdent).getTargetSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die Menge der {@link QN Hyperknoten} der Instanzen dieses Datentyps.
	 *
	 * @see DT#IDENT_IsTypeWithInstance
	 * @return Instanzknotenmenge. */
	default Set2<QN> instances() {
		return this.parent().getLink(DT.IDENT_IsTypeWithInstance).getTargetSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Feldknoten} der diesen Datentyp als {@link DL#targetTypes() Ziel} erwünschenden {@link DL
	 * Datenfelder}.
	 *
	 * @see DL#IDENT_IsLinkWithTargetType
	 * @return Zielfeldknoten. */
	default Set2<QN> targetLinks() {
		return this.parent().getLink(DL.IDENT_IsLinkWithTargetType).asSourceSet(this.node());
	}

	/** Diese Methode liefert die diesen Datentyp als {@link DL#targetTypes() Ziel} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#linkTrans()
	 * @return Zieldatenfelder. */
	default Set2<DL> targetLinksAsLinks() {
		return this.targetLinks().translate(this.parent().linkTrans());
	}

	/** Diese Methode liefert die {@link DL#node() Feldknoten} der diesen Datentyp als {@link DL#sourceTypes() Quelle} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DL#IDENT_IsLinkWithSourceType
	 * @return Quellfeldknoten. */
	default Set2<QN> sourceLinks() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSourceType).asSourceSet(this.node());
	}

	/** Diese Methode liefert die diesen Datentyp als {@link DL#sourceTypes() Quelle} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#linkTrans()
	 * @return Quelldatenfelder. */
	default Set2<DL> sourceLinksAsLinks() {
		return this.sourceLinks().translate(this.parent().linkTrans());
	}

}
