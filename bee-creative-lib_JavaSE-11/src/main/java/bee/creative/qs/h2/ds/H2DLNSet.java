package bee.creative.qs.h2.ds;

import bee.creative.qs.ds.DLNSet;
import bee.creative.qs.h2.H2QNSet;

public abstract class H2DLNSet extends H2DSNSet implements DLNSet {

	public final H2DL link;

	@Override
	public H2DL link() {
		return this.link;
	}

	protected H2DLNSet(H2QNSet source, H2DL link) {
		super(source);
		this.link = link;
	}

}
