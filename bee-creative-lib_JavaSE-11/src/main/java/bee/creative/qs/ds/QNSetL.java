package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.util.Iterables;

public interface QNSetL extends QNSet {

	DL link();

	QESet edges();

	QN target();

	QN source();

	default boolean setNode(QN node) {
		return this.setNodes(Iterables.fromItem(node));
	}

	boolean setNodes(Iterable<? extends QN> nodes);

	default boolean putNode(QN node) {
		return this.putNodes(Iterables.fromItem(node));
	}

	boolean putNodes(Iterable<? extends QN> nodes);

	default boolean popNode(QN node) {
		return this.popNodes(Iterables.fromItem(node));
	}

	boolean popNodes(Iterable<? extends QN> nodes);

}
