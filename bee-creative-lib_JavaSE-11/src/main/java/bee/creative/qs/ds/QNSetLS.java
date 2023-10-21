package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;

public interface QNSetLS extends DNSet {

	@Override
	default QN source() {
		return null;
	}

	@Override
	default QESet edges() {
		return this.link().edges().havingObject(this.target());
	}

	@Override
	default boolean setNodes(Iterable<? extends QN> nodes) {
		return this.link().setSourceSet(this.target(), nodes);
	}

	@Override
	default boolean putNodes(Iterable<? extends QN> nodes) {
		return this.link().putSourceSet(this.target(), nodes);
	}

	@Override
	default boolean popNodes(Iterable<? extends QN> nodes) {
		return this.link().popSourceSet(this.target(), nodes);
	}

}
