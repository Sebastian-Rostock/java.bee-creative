package bee.creative.qs.h2;

import java.util.Arrays;
import java.util.List;
import bee.creative.qs.QT;
import bee.creative.qs.QTSet2;

public class H2QTSet2 extends H2QTSet implements QTSet2 {

	@Override
	public H2QTSet2 copy() {
		return this;
	}

	@Override
	public H2QTSet2 withNames(String... names) throws NullPointerException, IllegalArgumentException {
		return this.withNames(Arrays.asList(names));
	}

	@Override
	public H2QTSet2 withNames(List<String> names) throws NullPointerException, IllegalArgumentException {
		if (this.names().equals(names)) return this;
		var names2 = new H2QTSetNames(names);
		if (this.names.size() != names2.size()) throw new IllegalArgumentException();
		return new H2QTSet2(this.owner, names2, this.table);
	}

	@Override
	public H2QTSet2 index(int... roles) throws NullPointerException, IllegalArgumentException {
		if (roles.length == 0) return this;
		var size = new H2QTSetNames(this.names(roles)).size();
		var qry = new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_");
		for (var i = 0; i < size; i++) {
			qry.push("C").push(roles[i]);
		}
		qry.push(" ON ").push(this.table).push(" (C").push(roles[0]);
		for (var i = 1; i < size; i++) {
			qry.push(", C").push(roles[i]);
		}
		qry.push(")").update(this.owner);
		return this;
	}

	@Override
	public boolean clear() throws NullPointerException, IllegalArgumentException {
		return new H2QQ().push("DELETE FROM ").push(this.table).update(this.owner);
	}

	@Override
	public boolean insertAll(Iterable<? extends QT> items) throws NullPointerException, IllegalArgumentException {
		if (items instanceof H2QTSet) {
			var that = owner.asQTSet(items, names());

		}
	}

	@Override
	public boolean deleteAll(Iterable<? extends QT> items) throws NullPointerException, IllegalArgumentException {
	}

	H2QTSet2(H2QS owner, H2QTSetNames names) {
		super(owner, names, null);
		var size = names.size();
		var qry = new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (C0 BIGINT NOT NULL");
		for (var i = 1; i < size; i++) {
			qry.push(", C").push(i).push(" BIGINT NOT NULL");
		}
		qry.push(", PRIMARY KEY (C0");
		for (var i = 1; i < size; i++) {
			qry.push(", C").push(i);
		}
		qry.push("))").update(owner);
	}

	H2QTSet2(H2QS owner, H2QTSetNames names, H2QQ table) {
		super(owner, names, table);
	}

}