package bee.creative.qs.dm.h2;

import bee.creative.qs.dm.DNSet;
import bee.creative.qs.h2.H2QNSet;
import bee.creative.qs.h2.H2QQ;

public abstract class H2DNSet extends H2QNSet implements DNSet {

	public final H2DL link;

	@Override
	public H2DL link() {
		return this.link;
	}

	@Override
	public H2DM parent() {
		return this.link.parent;
	}

	protected H2DNSet(H2DL link, H2QQ table) {
		super(link.owner(), table);
		this.link = link;
	}

}
