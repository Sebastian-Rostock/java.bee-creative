package bee.creative.qs.ds.h2;

import bee.creative.qs.ds.QNSetL;
import bee.creative.qs.h2.H2QNSet;
import bee.creative.qs.h2.H2QQ;

public abstract class H2QNSetL extends H2QNSet implements QNSetL {

	public final H2DL link;

	@Override
	public H2DL link() {
		return this.link;
	}

	protected H2QNSetL(H2DL link, H2QQ table) {
		super(link.owner(), table);
		this.link = link;
	}

}
