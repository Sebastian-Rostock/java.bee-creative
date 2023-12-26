package bee.creative.qs.h2;

import bee.creative.qs.QVSet2;

public class H2QVSet2 extends H2QVSet implements QVSet2 {

	@Override
	public H2QVSet2 copy() {
		return this;
	}

	@Override
	public H2QVSet2 index() {
		new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_V ON ").push(this.table).push(" (V)").update(this.owner);
		return this;
	}

	@Override
	public boolean clear() throws NullPointerException, IllegalArgumentException {
		return new H2QQ().push("DELETE FROM ").push(this.table).update(this.owner);
	}

	@Override
	public boolean insertAll(Iterable<? extends String> items) throws NullPointerException, IllegalArgumentException {
		if (items instanceof H2QVSet) {
			var that = (H2QVSet)items;
			if (that.owner == this.owner) return this.insertAllImpl(that);
		}
		return this.insertAllImpl(this.owner.newValues(items));
	}

	@Override
	public boolean deleteAll(Iterable<? extends String> items) throws NullPointerException, IllegalArgumentException {
		if (items instanceof H2QVSet) {
			var that = (H2QVSet)items;
			if (that.owner == this.owner) return this.deleteAllImpl(that);
		}
		return this.deleteAllImpl(this.owner.newValues(items));
	}

	H2QVSet2(H2QS owner) {
		super(owner, null);
		new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (V VARCHAR(1G) NOT NULL)").update(this.owner);
	}

	private boolean insertAllImpl(H2QVSet that) {
		return new H2QQ().push("MERGE INTO ").push(this.index().table).push(" SELECT V FROM (").push(that.index()).push(")").update(this.owner);
	}

	private boolean deleteAllImpl(H2QVSet that) {
		return new H2QQ().push("DELETE FROM ").push(this.index().table).push(" AS A WHERE EXISTS ((").push(that.index()).push(") AS B WHERE A.V=B.V)")
			.update(this.owner);
	}

}