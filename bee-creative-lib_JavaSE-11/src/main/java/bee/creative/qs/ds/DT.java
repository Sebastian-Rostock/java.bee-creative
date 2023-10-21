package bee.creative.qs.ds;

import java.util.Set;
import bee.creative.qs.QN;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Getter;
import bee.creative.util.Property2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

public interface DT extends DE {

	/** Diese Methode liefert einen {@link Translator2}, der einen {@link QN Hyperknoten} bidirektional in ein {@link DL Datentyp} übersetzt.
	 *
	 * @param nodeAsType Methode zur Übersetzung eines {@link DT#node() Typknoten} in das zugehörige {@link DL Datentyp}.
	 * @return Hyperknoten-Datentyp-Übersetzer. */
	static Translator2<QN, DT> typeTrans(Getter<QN, DT> nodeAsType) {
		return new AbstractTranslator<>() {

			@Override
			public boolean isTarget(Object object) {
				return object instanceof DT;
			}

			@Override
			public boolean isSource(Object object) {
				return object instanceof QN;
			}

			@Override
			public DT toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? nodeAsType.get((QN)object) : null;
			}

			@Override
			public QN toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return object != null ? ((DT)object).node() : null;
			}

		};
	}

	/** {@inheritDoc}
	 *
	 * @see DM#LINK_IDENT_IsTypeWithLabel */
	@Override
	default Property2<QN> label() {
		return this.parent().getLink(DM.LINK_IDENT_IsTypeWithLabel).asTargetField().toProperty(this.node());
	}

	/** {@inheritDoc}
	 *
	 * @see DM#LINK_IDENT_IsTypeWithIdent */
	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DM.LINK_IDENT_IsTypeWithIdent).getTargets(this.node()).asSet();
	}

	/** Diese Methode erlaubt Zugriff auf die Menge der {@link QN Hyperknoten} der Instanzen dieses Datentyps.
	 *
	 * @see DM#LINK_IDENT_IsTypeWithInstance
	 * @return Instanzknotenmenge. */
	default Set2<QN> instances() {
		return this.parent().getLink(DM.LINK_IDENT_IsTypeWithInstance).getTargets(this.node()).asSet();
	}

	/** Diese Methode erlaubt Zugriff auf die {@link DL#node() Feldknoten} der diesen Datentyp als {@link DL#targetTypes() Ziel} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#LINK_IDENT_IsLinkWithTargetType
	 * @return Zielfeldknoten. */
	default Set2<QN> targetLinks() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetType).getSources(this.node()).asSet();
	}

	/** Diese Methode liefert die diesen Datentyp als {@link DL#targetTypes() Ziel} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#asLinks(Set)
	 * @return Zieldatenfelder. */
	default Set2<DL> targetLinksAsLinks() {
		return this.parent().asLinks(this.targetLinks());
	}

	/** Diese Methode liefert die {@link DL#node() Feldknoten} der diesen Datentyp als {@link DL#sourceTypes() Quelle} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#LINK_IDENT_IsLinkWithSourceType
	 * @return Quellfeldknoten. */
	default Set2<QN> sourceLinks() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceType).getSources(this.node()).asSet();
	}

	/** Diese Methode liefert die diesen Datentyp als {@link DL#sourceTypes() Quelle} erwünschenden {@link DL Datenfelder}.
	 *
	 * @see DM#asLinks(Set)
	 * @return Quelldatenfelder. */
	default Set2<DL> sourceLinksAsLinks() {
		return this.parent().asLinks(this.sourceLinks());
	}

}
