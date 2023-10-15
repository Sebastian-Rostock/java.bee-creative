package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.util.AbstractTranslator;
import bee.creative.util.Getter;
import bee.creative.util.Property2;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

public interface DT extends DE {

	/** Diese Methode gibt das zurück.
	 *
	 * @param nodeAsType Diese Methode liefet den {@link DL Datentyp} mit dem gegebenen {@link DT#node() Typknoten}.
	 * @return */
	static Translator2<QN, DT> trans(Getter<QN, DT> nodeAsType) {
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

	@Override
	default Property2<QN> label() {
		return this.parent().getLink(DM.LINK_IDENT_IsTypeWithLabel).asTargetField().toProperty(this.node());
	}

	@Override
	default Set2<QN> idents() {
		return this.parent().getLink(DM.LINK_IDENT_IsTypeWithIdent).getTargetProxy(this.node());
	}

	default QNSetL instances() {
		return this.parent().getLink(DM.LINK_IDENT_IsTypeWithInstance).getTargetSet(this.node());
	}

	/** Diese Methode setzt {@link #node()} als Datentyp des gegebenen {@link QN Hyperknoten}. */
	default void assign(QN item) {
		this.instances().putNode(item);
	}

	/** Diese Methode setzt {@link #node()} als Datentyp der gegebenen {@link QN Hyperknoten}. */
	default void assignAll(Iterable<? extends QN> itemSet) {
		this.instances().putNodes(itemSet);
	}

	/** Diese Methode liefert die {@link DL#node() Feldknoten} der von {@link #instances() Instanzen} dieses Datentyps ausgehenden {@link DL Datenfelder}.
	 *
	 * @return Subjektdatenfeldknoten. */
	default Set2<QN> sourceLinks() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithSourceType).getSourceProxy(this.node());
	}

	/** Diese Methode liefert die von {@link #instances() Instanzen} dieses Datentyps ausgehenden {@link DL Datenfelder}.
	 *
	 * @return Subjektdatenfelder. */
	default Set2<DL> sourceLinksAsLinks() {
		return this.sourceLinks().translate(this.parent().linkTrans());
	}

	default Set2<QN> targetLinks() {
		return this.parent().getLink(DM.LINK_IDENT_IsLinkWithTargetType).getSourceProxy(this.node());
	}

	default Set2<DL> targetLinksAsLinks() {
		return this.targetLinks().translate(this.parent().linkTrans());
	}

}
