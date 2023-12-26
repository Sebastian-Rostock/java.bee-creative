package bee.creative.qs.h2;

import bee.creative.qs.QN;
import bee.creative.qs.QNSet2;

public class H2QNSet2 extends H2QNSet implements QNSet2 {

	@Override
	public H2QNSet2 copy() {
		return this;
	}

	@Override
	public H2QNSet2 index() {
		new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_N ON ").push(this.table).push(" (N)").update(this.owner);
		return this;
	}

	@Override
	public boolean clear() throws NullPointerException, IllegalArgumentException {
		return new H2QQ().push("DELETE FROM ").push(this.table).update(this.owner);
	}

	@Override
	public boolean insertAll(Iterable<? extends QN> items) throws NullPointerException, IllegalArgumentException {
		var that = items instanceof H2QNSet ? this.owner.asQNSet(items) : this.owner.newNodes(items);
		return new H2QQ().push("MERGE INTO ").push(this.index().table).push(" SELECT N FROM (").push(that.index()).push(")").update(this.owner);
	}

	@Override
	public boolean deleteAll(Iterable<? extends QN> items) throws NullPointerException, IllegalArgumentException {
		var that = items instanceof H2QNSet ? this.owner.asQNSet(items) : this.owner.newNodes(items);
		return new H2QQ().push("DELETE FROM ").push(this.index().table).push(" AS A WHERE EXISTS ((").push(that.index()).push(") AS B WHERE A.N=B.N)")
			.update(this.owner);
	}

	H2QNSet2(H2QS owner) {
		super(owner, null);
		new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (N BIGINT NOT NULL, PRIMARY KEY (N))").update(owner);
	}

}