package bee.creative.qs.h2.ds;

import bee.creative.qs.ds.DNSetT;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QQ;

public class H2DNSetT extends H2DNSet implements DNSetT {

	public final H2QN source;

	@Override
	public H2QESet edges() {
		return this.link.edges().havingSubject(this.source);
	}

	@Override
	public H2QN source() {
		return this.source;
	}

	protected H2DNSetT(H2DL link, H2QN source) {
		super(link, new H2QQ().push(link.edges().havingSubject(source).objects()));
		this.source = source;
	}

}
