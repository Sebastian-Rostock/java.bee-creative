package bee.creative.qs.ds.h2;

import bee.creative.qs.QN;
import bee.creative.qs.ds.DH;
import bee.creative.qs.ds.DL;
import bee.creative.qs.ds.DM;
import bee.creative.qs.ds.DT;
import bee.creative.qs.h2.H2QESet;
import bee.creative.qs.h2.H2QN;
import bee.creative.qs.h2.H2QS;
import bee.creative.util.Set2;
import bee.creative.util.Translator2;

public class H2DM implements DM {

	public H2QN model;

	public H2QN context;

	@Override
	public H2QN model() {
		return this.model;
	}

	@Override
	public H2QESet edges() {
		return this.context.owner.edges().havingContext(this.context);
	}

	@Override
	public H2QS owner() {
		return this.context.owner;
	}

	@Override
	public H2QN context() {
		return this.context;
	}

	@Override
	public DH history() {
		return null;
	}

	@Override
	public DL getLink(String ident) {
		return null;
	}

	@Override
	public DT getType(String ident) {
		return null;
	}

	@Override
	public void updateIdents() {
	}

	@Override
	public Set2<String> asString(Set2<QN> nodes) {
		return null;
	}

}
