package bee.creative.qs.ds;

import bee.creative.qs.QESet;
import bee.creative.qs.QN;

public interface QNSetLT extends DNSet {

	@Override
	default QN target() {
		return null;
	}

	@Override
	default QESet edges() {
		return this.link().edges().havingSubject(this.source());
	}

	@Override
	default boolean setNodes(Iterable<? extends QN> nodes) {
		return this.link().setTargetSet(this.source(), nodes);
	}

	@Override
	default boolean putNodes(Iterable<? extends QN> nodes) {
		return this.link().putTargetSet(this.source(), nodes);
	}

	@Override
	default boolean popNodes(Iterable<? extends QN> nodes) {
		return this.link().popTargetSet(this.source(), nodes);
	}

}
