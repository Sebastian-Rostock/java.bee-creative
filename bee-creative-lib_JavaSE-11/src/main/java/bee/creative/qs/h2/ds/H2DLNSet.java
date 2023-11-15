package bee.creative.qs.h2.ds;

import bee.creative.qs.ds.DLNSet;
import bee.creative.qs.h2.H2QQ;

public abstract class H2DLNSet extends H2DSNSet implements DLNSet {

	public final H2DL link;

	@Override
	public H2DL link() {
		return this.link;
	}

	protected H2DLNSet(H2DL link, H2QQ table) {
		super(link.owner(), table);
		this.link = link;
	}

}
