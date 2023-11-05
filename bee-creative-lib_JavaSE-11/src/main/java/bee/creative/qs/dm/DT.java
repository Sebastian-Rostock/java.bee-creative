package bee.creative.qs.dm;

import bee.creative.qs.QN;
import bee.creative.util.Property2;
import bee.creative.util.Set2;

/** Diese Schnittstelle definiert einen Datentyp (Domain-type) als {@link #label() beschriftete} und {@link #idents() erkennbare} {@link #instances()
 * Instanzmenge}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
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
		return this.parent().getLink(DT.IDENT_IsTypeWithLabel).asTargetProperty(this.node());
	}

	/** {@inheritDoc}
	 *
	 * @see #IDENT_IsTypeWithIdent */
	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DT.IDENT_IsTypeWithIdent).asTargetSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link QN Hyperknoten} der Instanzen dieses Datentyps. Eine Instanz darf nur einen Datentyp besitzen. Hyperknoten
	 * mit {@link QN#value() Textwert} sind als Instanz nicht zulässig.
	 *
	 * @see DT#IDENT_IsTypeWithInstance
	 * @see DL#asTargetSet(QN)
	 * @return Instanzknoten. */
	default Set2<QN> instances() {
		return this.parent().getLink(DT.IDENT_IsTypeWithInstance).asTargetSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Hyperknoten} der diesen Datentyp als {@link DL#targetTypeAsType() Objektdatentyp} zulassenden
	 * {@link DL Datenfelder}.
	 *
	 * @see DL#IDENT_IsLinkWithTargetType
	 * @see DL#asSourceSet(QN)
	 * @return Objektdatenfeldknoten. */
	default Set2<QN> targetLinks() {
		return this.parent().getLink(DL.IDENT_IsLinkWithTargetType).asSourceSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die diesen Datentyp als {@link DL#targetTypeAsType() Objektdatentyp} zulassenden {@link DL Datenfelder}.
	 *
	 * @see DT#targetLinks()
	 * @see DM#linkTrans()
	 * @return Objektdatenfelder. */
	default Set2<DL> targetLinksAsLinks() {
		return this.targetLinks().translate(this.parent().linkTrans());
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Hyperknoten} der diesen Datentyp als {@link DL#sourceTypeAsType() Subjektdatentyp} zulassenden
	 * {@link DL Datenfelder}.
	 *
	 * @see DL#IDENT_IsLinkWithSourceType
	 * @see DL#asSourceSet(QN)
	 * @return Subjektdatenfeldknoten. */
	default Set2<QN> sourceLinks() {
		return this.parent().getLink(DL.IDENT_IsLinkWithSourceType).asSourceSet(this.node());
	}

	/** Diese Methode erlaubt Zugriff auf die diesen Datentyp als {@link DL#sourceTypeAsType() Subjektdatentyp} zulassenden {@link DL Datenfelder}.
	 *
	 * @see DT#sourceLinks()
	 * @see DM#linkTrans()
	 * @return Subjektdatenfelder. */
	default Set2<DL> sourceLinksAsLinks() {
		return this.sourceLinks().translate(this.parent().linkTrans());
	}

}
