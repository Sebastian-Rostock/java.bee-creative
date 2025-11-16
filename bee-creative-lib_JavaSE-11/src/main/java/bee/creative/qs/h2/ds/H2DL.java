package bee.creative.qs.h2.ds;

import bee.creative.qs.QN;
import bee.creative.qs.ds.DL;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;

public class H2DL extends H2DE implements DL {

	@Override
	public H2QESet edges() {
		return this.parent.edges().havingPredicate(this.node);
	}

	@Override
	public H2DLSSet getSubjects(QN object) throws NullPointerException, IllegalArgumentException {
		return new H2DLSSet(this, this.node.owner.asQN(object));
	}

	@Override
	public H2DLTSet getObjects(QN subject) throws NullPointerException, IllegalArgumentException {
		return new H2DLTSet(this, this.node.owner.asQN(subject));
	}

	protected H2DL(H2DM parent, H2QN node) {
		super(parent, node);
	}

}
