package bee.creative.qs.h2;

import java.sql.SQLException;
import bee.creative.qs.QE;
import bee.creative.qs.QESet2;

public class H2QESet2 extends H2QESet implements QESet2 {

	@Override
	public H2QESet2 copy() {
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
		var that = this.toQESet(items);
		return new H2QQ().push("MERGE INTO ").push(this.table).push(" SELECT * FROM (").push(that).push(")").update(this.owner);
	}

	@Override
	public boolean deleteAll(Iterable<? extends QE> items) throws NullPointerException, IllegalArgumentException {
		var that = this.toQESet(items);
		return new H2QQ().push("DELETE FROM ").push(this.table).push(" AS A WHERE EXISTS (SELECT 1 FROM (").push(that)
			.push(") AS B WHERE A.C=B.C AND A.P=B.P AND A.S=B.S AND A.O=B.O)").update(this.owner);
	}

	/** Dieser Konstruktor erzeugt eine leere Menge. */
	public H2QESet2(H2QS owner) throws NullPointerException {
		this(owner, true);
	}

	/** Dieser Konstruktor erzeugt eine Menge mit den gegebenen Elementen. */
	public H2QESet2(H2QS owner, Iterable<? extends QE> items) throws NullPointerException, IllegalArgumentException {
		this(owner);
		if (items instanceof H2QESet) {
			var that = owner.asQESet(items);
			new H2QQ().push("INSERT INTO ").push(this.table).push(" SELECT * FROM (").push(that).push(")").update(owner);
		} else {
			var that = new H2QESet2(owner, false);
			try (var stmt = new H2QQ().push("INSERT INTO ").push(that.table).push(" (C, P, S, O) VALUES (?, ?, ?, ?)").prepare(owner)) {
				for (var item: items) {
					var edge = owner.asQE(item);
					stmt.setLong(1, edge.context);
					stmt.setLong(2, edge.predicate);
					stmt.setLong(3, edge.subject);
					stmt.setLong(4, edge.object);
					stmt.addBatch();
				}
				stmt.executeBatch();
				new H2QQ().push("INSERT INTO ").push(this.table).push(" SELECT DISTINCT * FROM ").push(that.table).update(owner);
			} catch (SQLException cause) {
				throw new IllegalStateException(cause);
			}
		}
	}

	private H2QESet2(H2QS owner, boolean withPrimaryKey) throws NullPointerException {
		super(owner, null);
		new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (C BIGINT NOT NULL, P BIGINT NOT NULL, S BIGINT NOT NULL, O BIGINT NOT NULL")
			.push(withPrimaryKey ? ", PRIMARY KEY (C, P, S, O))" : ")").update(owner);
	}

	private H2QESet toQESet(Iterable<? extends QE> items) {
		return items instanceof H2QESet ? this.owner.asQESet(items) : new H2QESet2(this.owner, items);
	}

}