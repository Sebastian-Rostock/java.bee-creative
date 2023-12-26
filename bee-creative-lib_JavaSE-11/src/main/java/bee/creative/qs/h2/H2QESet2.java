package bee.creative.qs.h2;

import bee.creative.qs.QE;
import bee.creative.qs.QESet2;

public class H2QESet2 extends H2QESet implements QESet2 {

	@Override
	public H2QESet2 copy() {
		return this;
	}

	@Override
	public H2QESet2 index() {
		return this;
	}

	@Override
	public H2QESet2 index(String cols) throws NullPointerException, IllegalArgumentException {
		if ((cols.length() != 4) || ((cols.indexOf('C') | cols.indexOf('P') | cols.indexOf('S') | cols.indexOf('O')) < 0)) throw new IllegalArgumentException();
		new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_").push(cols).push(" ON ").push(this.table).push(" (").push(cols.charAt(0))
			.push(", ").push(cols.charAt(1)).push(", ").push(cols.charAt(2)).push(", ").push(cols.charAt(3)).push(")").update(this.owner);
		return this;
	}

	@Override
	public boolean clear() throws NullPointerException, IllegalArgumentException {
		return new H2QQ().push("DELETE FROM ").push(this.table).update(this.owner);
	}

	@Override
	public boolean insertAll(Iterable<? extends QE> items) throws NullPointerException, IllegalArgumentException {
		var that = items instanceof H2QESet ? this.owner.asQESet(items) : this.owner.newEdges(items);
		return new H2QQ().push("MERGE INTO ").push(this.table).push(" SELECT * FROM (").push(that.index()).push(")").update(this.owner);
	}

	@Override
	public boolean deleteAll(Iterable<? extends QE> items) throws NullPointerException, IllegalArgumentException {
		var that = items instanceof H2QESet ? this.owner.asQESet(items) : this.owner.newEdges(items);
		return new H2QQ().push("DELETE FROM ").push(this.table).push(" AS A WHERE EXISTS ((").push(that.index())
			.push(") AS B WHERE A.C=B.C AND A.P=B.P AND A.S=B.S AND A.O=B.O)").update(this.owner);
	}

	H2QESet2(H2QS owner) {
		super(owner, null);
		new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table)
			.push(" (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL, PRIMARY KEY (C, P, S, O))").update(owner);
	}

}