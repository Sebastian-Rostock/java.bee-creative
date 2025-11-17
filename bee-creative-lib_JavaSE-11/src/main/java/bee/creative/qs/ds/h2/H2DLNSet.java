package bee.creative.qs.ds.h2;

import bee.creative.qs.ds.DLNSet;
import bee.creative.qs.h2.H2QNSet;

public abstract class H2DLNSet extends H2DSNSet implements DLNSet {

	public final H2DL link;

	@Override
	public H2DL link() {
		return this.link;
	}

	protected H2DLNSet(H2DL link, H2QNSet source) {
		super(source);
		this.link = link;
	}

}
