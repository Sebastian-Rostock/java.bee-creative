package bee.creative.qs.h2;

import java.sql.SQLException;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet2;

public class H2QNSet2 extends H2QNSet implements QNSet2 {

	@Override
	public H2QNSet2 copy() {
		return this;
	}

	@Override
	public boolean clear() throws NullPointerException, IllegalArgumentException {
		return new H2QQ().push("DELETE FROM ").push(this.table).update(this.owner);
	}

	@Override
	public boolean insertAll(Iterable<? extends QN> items) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.toQNSet(items);
		return new H2QQ().push("MERGE INTO ").push(this.table).push(" SELECT * FROM (").push(that).push(")").update(this.owner);
	}

	@Override
	public boolean deleteAll(Iterable<? extends QN> items) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.toQNSet(items);
		return new H2QQ().push("DELETE FROM ").push(this.table).push(" AS A WHERE EXISTS (SELECT 1 FROM (").push(that).push(") AS B WHERE A.N=B.N)")
			.update(this.owner);
	}

	/** Dieser Konstruktor erzeugt eine leere Menge. */
	public H2QNSet2(H2QS owner) throws NullPointerException {
		this(owner, true);
	}

	/** Dieser Konstruktor erzeugt eine Menge mit den gegebenen Elementen. */
	public H2QNSet2(H2QS owner, Iterable<? extends QN> items) throws NullPointerException, IllegalArgumentException {
		this(owner);
		if (items instanceof H2QNSet) {
			var that = owner.asQNSet(items);
			new H2QQ().push("INSERT INTO ").push(this.table).push(" SELECT * FROM (").push(that).push(")").update(owner);
		} else {
			try {
				var that = new H2QNSet2(owner, false);
				try (var stmt = new H2QQ().push("INSERT INTO ").push(that.table).push(" (N) VALUES (?)").prepare(owner)) {
					for (var item: items) {
						var node = owner.asQN(item);
						stmt.setLong(1, node.key);
						stmt.addBatch();
					}
					stmt.executeBatch();
				}
				new H2QQ().push("INSERT INTO ").push(this.table).push(" SELECT DISTINCT * FROM ").push(that.table).update(owner);
			} catch (SQLException cause) {
				throw new IllegalStateException(cause);
			}
		}
	}

	private H2QNSet2(H2QS owner, boolean withPrimaryKey) throws NullPointerException {
		super(owner, null);
		new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (N BIGINT NOT NULL").push(withPrimaryKey ? ", PRIMARY KEY (N))" : ")").update(owner);
	}

}