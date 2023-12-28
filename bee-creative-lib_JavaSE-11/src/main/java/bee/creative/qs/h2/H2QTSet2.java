package bee.creative.qs.h2;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import bee.creative.qs.QN;
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
		return new H2QTSet2(this, new H2QTSetNames(names));
	}

	@Override
	public H2QTSet2 index(int... roles) throws NullPointerException, IllegalArgumentException {
		var size = roles.length;
		if (size == 0) return this;
		new H2QTSetNames(this.names(roles));
		new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_").push(0, size, (q, i) -> q.push("C").push(roles[i])).push(" ON ")
			.push(this.table).push(" (C").push(roles[0]).push(1, size, (q, i) -> q.push(", C").push(roles[i])).push(")").update(this.owner);
		return this;
	}

	@Override
	public boolean clear() throws NullPointerException, IllegalArgumentException {
		return new H2QQ().push("DELETE FROM ").push(this.table).update(this.owner);
	}

	@Override
	public boolean insertAll(Iterable<? extends QT> items) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.toQTSet(items, this.names());
		return new H2QQ().push("MERGE INTO ").push(this.table).push(" SELECT * FROM (").push(that).push(")").update(this.owner);
	}

	@Override
	public boolean deleteAll(Iterable<? extends QT> items) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.toQTSet(items, this.names());
		var size = this.names().size();
		return new H2QQ().push("DELETE FROM ").push(this.table).push(" AS A WHERE EXISTS (SELECT 1 FROM (").push(that).push(") AS B WHERE A.C0=B.C0")
			.push(1, size, (q, i) -> q.push(" AND A.C").push(i).push("=B.C").push(i)).push(")").update(this.owner);
	}

	/** Dieser Konstruktor erzeugt eine leere Menge. */
	public H2QTSet2(H2QS owner, H2QTSetNames names) throws NullPointerException, IllegalArgumentException {
		this(owner, names, true);
	}

	/** Dieser Konstruktor erzeugt eine Menge mit den gegebenen Elementdaten. */
	public H2QTSet2(H2QS owner, H2QTSetNames names, QN... items) throws NullPointerException, IllegalArgumentException {
		this(owner, names, null, items);
	}

	/** Dieser Konstruktor erzeugt eine Menge mit den gegebenen Elementen. */
	public H2QTSet2(H2QS owner, H2QTSetNames names, Iterable<? extends QT> items) throws NullPointerException, IllegalArgumentException {
		this(owner, names, items, null);
	}

	private H2QTSet2(H2QS owner, H2QTSetNames names, boolean withPrimaryKey) throws NullPointerException, IllegalArgumentException {
		super(owner, names, null);
		var size = names.size();
		new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (C0 BIGINT NOT NULL")
			.push(1, size, (q, i) -> q.push(", C").push(i).push(" BIGINT NOT NULL"))
			.push(withPrimaryKey ? q -> q.push(", PRIMARY KEY (C0").push(1, size, (qq, i) -> qq.push(", C").push(i)).push("))") : q2 -> q2.push(")")).update(owner);
	}

	private H2QTSet2(H2QS owner, H2QTSetNames names, Iterable<? extends QT> items1, QN[] items2) throws NullPointerException, IllegalArgumentException {
		this(owner, names, true);
		if (items1 instanceof H2QTSet) {
			var that = owner.asQTSet(items1, names.size());
			new H2QQ().push("INSERT INTO ").push(this.table).push(" SELECT * FROM (").push(that).push(")").update(owner);
		} else {
			try {
				var that = new H2QTSet2(owner, names, false);
				var size = names.size();
				try (var stmt = new H2QQ().push("INSERT INTO ").push(that.table).push(" (C0").push(1, size, (q, i) -> q.push(", C").push(i)).push(") VALUES (?")
					.push(1, size, (q, i) -> q.push(", ?")).push(")").prepare(owner)) {
					if (items2 != null) {
						var count = items2.length;
						if ((count % size) != 0) throw new IllegalArgumentException();
						for (var r = 0; r < count; r += size) {
							for (var i = 0; i < size; i++) {
								stmt.setLong(i + 1, owner.asQN(items2[r + i]).key);
							}
							stmt.addBatch();
						}
					} else {
						for (var item: items1) {
							var keys = owner.asQT(item).keys;
							if (keys.length != size) throw new IllegalArgumentException();
							for (var i = 0; i < size; i++) {
								stmt.setLong(i + 1, keys[i]);
							}
							stmt.addBatch();
						}
					}
					stmt.executeBatch();
				}
				new H2QQ().push("INSERT INTO ").push(this.table).push(" SELECT DISTINCT * FROM ").push(that.table).update(owner);
			} catch (SQLException cause) {
				throw new IllegalStateException(cause);
			}
		}
	}

	/** Dieser Konstruktor erzeugt eine Menge mit den gegebenen Elementen und Rollen. */
	private H2QTSet2(H2QTSet2 that, H2QTSetNames names) throws NullPointerException, IllegalArgumentException {
		super(that.owner, names, that.table);
		if (that.names().size() != names.size()) throw new IllegalArgumentException();
	}

}