package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.Builders.MapBuilder;
import bee.creative.util.Set2;

public interface DT extends DE {

	default QNSet items() {
		final var model = this.model();
		return model.edges().havingPredicate(model.nodeTypeLink().node()).havingObject(this.node()).subjects();
	}

	/** Diese Methode setzt {@link #node()} als Datentyp des gegebenen {@link QN Hyperknoten}. */
	default void assignToItem(QN item) {
		this.model().nodeTypeLink().setTarget(item, this.node());
	}

	/** Diese Methode setzt {@link #node()} als Datentyp der gegebenen {@link QN Hyperknoten}. */
	default void assignToItems(Iterable<? extends QN> itemSet) {
		var type = this.node();
		this.model().nodeTypeLink().setTargetMap(MapBuilder.<QN, QN>forHashMap().putAllKeys(node -> type, itemSet).get());
	}

	/** Diese Methode liefert {@link DL#node() Feldknoten} die {@link DL Datenfelder}, die von Instanzen dieses Datentyps ausgehen. */
	default Set2<QN> allowedSources() {
		return this.model().linkSourceLink().getSourceProxy(this.node());
	}

	/** Diese Methode liefert die Megne der {@link DL Datenfelder}, die von Instanzen dieses Datentyps ausgehen können. */
	default Set2<DL> allowedSourcesAsLinks() {
		return this.allowedSources().translate(this.model().linkTrans());
	}

	default Set2<QN> targets() {
		return this.model().linkTargetLink().getSourceProxy(this.node());
	}

	/** Diese Methode liefert die {@link DL Datenfelder}, die auf Instanzen dieses Datentyps verweisen können. */
	default Set2<DL> targetsAsLinks() {
		return this.targets().translate(this.model().linkTrans());
	}

}
