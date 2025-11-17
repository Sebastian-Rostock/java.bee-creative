package bee.creative.qs.ds.h2;

import bee.creative.qs.ds.DE;
import bee.creative.qs.h2.H2QN;

public abstract class H2DE extends H2DN implements DE {

	@Override
	public String toString() {
		return this.label().get();
	}

	protected H2DE(H2DM parent, H2QN node) {
		super(parent, node);
	}

}
