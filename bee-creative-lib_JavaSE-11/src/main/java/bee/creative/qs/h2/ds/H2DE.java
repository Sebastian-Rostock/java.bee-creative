package bee.creative.qs.h2.ds;

import bee.creative.lang.Objects;
import bee.creative.qs.ds.DE;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;

public abstract class H2DE implements DE {

	public final H2QN node;

	public final H2DM parent;

	@Override
	public H2QN node() {
		return this.node;
	}

	@Override
	public H2QS owner() {
		return this.node.owner;
	}

	@Override
	public H2DM parent() {
		return this.parent;
	}


	@Override
	public int hashCode() {
		return Objects.hash(this.node);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof H2DE)) return false;
		var that = (H2DE)object;
		return Objects.equals(this.node, that.node);
	}

	@Override
	public String toString() {
		return this.labelAsString().get();
	}

	protected H2DE(H2DM parent, H2QN node) {
		this.parent = parent;
		this.node = node;
	}

}
