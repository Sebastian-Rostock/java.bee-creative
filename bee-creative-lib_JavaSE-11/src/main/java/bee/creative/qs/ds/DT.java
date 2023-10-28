package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.util.Set2;

public interface DT extends DE {

	/** Diese Methode erlaubt Zugriff auf die Menge der {@link QN Hyperknoten} der Instanzen dieses Datentyps.
	 *
	 * @see DM#IDENT_IsTypeWithInstance
	 * @return Instanzknotenmenge. */
	default Set2<QN> instances() {
		return this.parent().getLink(DM.IDENT_IsTypeWithInstance).getTargetSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Feldknoten} der diesen Datentyp als {@link DL#targetTypes() Ziel} erwünschenden {@link DL
	 * Datenfelder}.
	 *
	 * @see DM#IDENT_IsLinkWithTargetType
	 * @return Zielfeldknoten. */
	default Set2<QN> targetLinks() {
		return this.parent().getLink(DM.IDENT_IsLinkWithTargetType).asSourceSet(this.node());
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
	 * @see DM#IDENT_IsLinkWithSourceType
	 * @return Quellfeldknoten. */
	default Set2<QN> sourceLinks() {
		return this.parent().getLink(DM.IDENT_IsLinkWithSourceType).asSourceSet(this.node());
	}

	/** Diese Methode liefert die diesen Datentyp als {@link DL#sourceTypes() Quelle} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#linkTrans()
	 * @return Quelldatenfelder. */
	default Set2<DL> sourceLinksAsLinks() {
		return this.sourceLinks().translate(this.parent().linkTrans());
	}

}
