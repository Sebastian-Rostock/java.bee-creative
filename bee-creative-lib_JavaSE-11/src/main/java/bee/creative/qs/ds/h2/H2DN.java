package bee.creative.qs.ds.h2;

import bee.creative.lang.Objects;
import bee.creative.qs.ds.DN;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;

public abstract class H2DN implements DN {

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
		if (!(object instanceof H2DN)) return false;
		var that = (H2DN)object;
		return Objects.equals(this.node, that.node);
	}

	@Override
	public String toString() {
		return this.node().toString();
	}

	protected H2DN(H2DM parent, H2QN node) {
		this.node = node;
		this.parent = parent;
	}

}
