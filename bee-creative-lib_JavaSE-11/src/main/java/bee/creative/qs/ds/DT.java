package bee.creative.qs.ds;

import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.Builders.MapBuilder;

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

	/** Diese Methode liefert die {@link DL Datenfelder}, die von Instanzen dieses Datentyps ausgehen. */
	DLSet sourceLinks();

	/** Diese Methode liefert die {@link DL Datenfelder}, die auf Instanzen dieses Datentyps verweisen. */
	default DLSet targetLinks() {
		final var model = this.model();
		return model.nodesAsLinks(model.linkTargetLink().getSourceSet(this.node()));
	}

}
