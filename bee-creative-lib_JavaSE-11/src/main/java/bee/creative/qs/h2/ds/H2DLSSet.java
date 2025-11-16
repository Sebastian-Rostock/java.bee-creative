package bee.creative.qs.h2.ds;

import bee.creative.qs.ds.DLSSet;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;

public class H2DLSSet extends H2DLNSet implements DLSSet {

	public final H2QN target;

	@Override
	public H2QESet edges() {
		return this.link.edges().havingObject(this.target);
	}

	@Override
	public H2QN object() {
		return this.target;
	}

	protected H2DLSSet(H2DL link, H2QN target) {
		super(link, link.edges().havingObject(target).subjects());
		this.target = target;
	}

}
