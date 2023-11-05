package bee.creative.qs.dm.h2;

import bee.creative.qs.QN;
import bee.creative.qs.dm.DL;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;

public class H2DL extends H2DE implements DL {

	@Override
	public H2QESet edges() {
		return this.parent.edges().havingPredicate(this.node);
	}

	@Override
	public H2DNSetS getSources(QN target) throws NullPointerException, IllegalArgumentException {
		return new H2DNSetS(this, this.node.owner.asQN(target));
	}

	@Override
	public H2DNSetT getTargets(QN source) throws NullPointerException, IllegalArgumentException {
		return new H2DNSetT(this, this.node.owner.asQN(source));
	}

	protected H2DL(H2DM model, H2QN node) {
		super(model, node);
	}

}
