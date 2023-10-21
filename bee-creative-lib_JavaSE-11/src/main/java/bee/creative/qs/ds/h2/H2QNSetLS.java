package bee.creative.qs.ds.h2;

import bee.creative.qs.ds.DNSetS;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QQ;

public class H2QNSetLS extends H2QNSetL implements DNSetS {

	public final H2QN target;

	@Override
	public H2QESet edges() {
		return this.link.edges().havingObject(this.target);
	}

	@Override
	public H2QN target() {
		return this.target;
	}

	protected H2QNSetLS(H2DL link, H2QN target) {
		super(link, new H2QQ().push(link.edges().havingObject(target).subjects()));
		this.target = target;
	}

}
