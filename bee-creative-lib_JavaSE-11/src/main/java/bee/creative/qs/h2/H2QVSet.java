package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QN;
import bee.creative.qs.QVSet;
import bee.creative.util.Filter;
import bee.creative.util.Iterables;
import bee.creative.util.Setter;

/** Diese Klasse implementiert ein {@link QVSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QVSet extends H2QOSet<String, QVSet> implements QVSet {

	@Override
	public boolean putAll() {
		return this.owner.markPutValue(
			new H2QQ().push("MERGE INTO QN (N, V) KEY (V) SELECT NEXT VALUE FOR QV_SEQ AS N, V FROM (").push(this.index()).push(")").update(this.owner));
	}

	@Override
	public boolean popAll() {
		return this.owner.markPopValue(new H2QQ().push("DELETE FROM QN WHERE V IN (").push(this).push(")").update(this.owner));
	}

	@Override
	public H2QNSet nodes() {
		return new H2QNSet(this.owner, new H2QQ().push("SELECT N FROM QN WHERE V IN (").push(this).push(")"));
	}

	@Override
	public void nodes(Setter<? super String, ? super QN> nodes) {
		try (var rset = new H2QQ().push("SELECT V, N FROM QN WHERE V IN (").push(this).push(")").select(this.owner)) {
			while (rset.next()) {
				nodes.set(rset.getString(1), this.owner.newNode(rset.getLong(2)));
			}
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public H2QVSet havingState(boolean state) {
		return state ? this.intersect(this.owner.values()) : this.except(this.owner.values());
	}

	@Override
	public H2QVSet copy() {
		return this.owner.newValues(this);
	}

	@Override
	public H2QVSet copy(Filter<? super String> filter) throws NullPointerException {
		return this.owner.newValues(Iterables.filter(this, filter));
	}

	@Override
	public H2QVSet order() {
		return new Order(this);
	}

	@Override
	public H2QVSet index() {
		return this.copy().index();
	}

	@Override
	public H2QVSet union(QVSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QVSet except(QVSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QVSet intersect(QVSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (V VARCHAR(1G) NOT NULL)} besitzen. */
	protected H2QVSet(H2QS owner, H2QQ table) {
		super(owner, table);
	}

	@Override
	protected String customItem(ResultSet item) throws SQLException {
		return item.getString(1);
	}

	static class Main extends H2QVSet {

		public Main(H2QS owner) {
			super(owner, new H2QQ().push("SELECT V FROM QN"));
		}

		@Override
		public H2QVSet index() {
			return this;
		}

		@Override
		public H2QNSet nodes() {
			return this.owner.nodes();
		}

	}

	static class Temp extends H2QVSet {

		public Temp(H2QS owner) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (V VARCHAR(1G) NOT NULL)").update(this.owner);
		}

		@Override
		public H2QVSet copy() {
			return this;
		}

		@Override
		public H2QVSet index() {
			new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_V ON ").push(this.table).push(" (V)").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QVSet {

		public Order(H2QVSet that) {
			super(that.owner, new H2QQ().push("SELECT * FROM (").push(that).push(") ORDER BY V"));
		}

		@Override
		public H2QVSet order() {
			return this;
		}

	}

}
