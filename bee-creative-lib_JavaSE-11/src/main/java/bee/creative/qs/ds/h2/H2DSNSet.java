package bee.creative.qs.ds.h2;

import bee.creative.qs.ds.DSNSet;
import bee.creative.qs.h2.H2QNSet;
import bee.creative.qs.h2.H2QQ;

public abstract class H2DSNSet extends H2QNSet implements DSNSet {

	protected H2DSNSet(H2QNSet source) {
		super(source.owner, new H2QQ().push(source));
	}

}
