package bee.creative.qs.h2.ds;

import bee.creative.qs.ds.DSNSet;
import bee.creative.qs.h2.H2QNSet;
import bee.creative.qs.h2.H2QQ;
import bee.creative.qs.h2.H2QS;

public abstract class H2DSNSet extends H2QNSet implements DSNSet {

	protected H2DSNSet(H2QS owner, H2QQ table) {
		super(owner, table);
	}

}
