package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QTSet;
import bee.creative.qs.QVSet;
import bee.creative.qs.h2.H2QTSet.Names;
import bee.creative.util.Filter;
import bee.creative.util.Iterables;
import bee.creative.util.Setter;

/** Diese Klasse implementiert ein {@link QNSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QNSet extends H2QOSet<QN, QNSet> implements QNSet {

	@Override
	public boolean popAll() {
		var that = this.index();
		return this.owner.markPopValue(new H2QQ().push("DELETE FROM QN WHERE N IN (").push(that).push(")").update(this.owner)) //
			| new H2QQ().push("DELETE FROM QE WHERE C IN (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("DELETE FROM QE WHERE P IN (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("DELETE FROM QE WHERE S IN (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("DELETE FROM QE WHERE O IN (").push(that).push(")").update(this.owner);
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet(this.owner, new H2QQ().push("SELECT V FROM QN WHERE N IN (").push(this).push(")"));
	}

	@Override
	public void values(Setter<? super QN, ? super String> values) {
		try (var rset = new H2QQ().push("SELECT N, V FROM QN WHERE N IN (").push(this).push(")").select(this.owner)) {
			while (rset.next()) {
				values.set(this.owner.newNode(rset.getInt(1)), rset.getString(2));
			}
		} catch (SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public QTSet tuples(String name) throws NullPointerException, IllegalArgumentException {
		return new H2QTSet(this.owner, new Names(name), new H2QQ().push("SELECT N C0 FROM (").push(this).push(")"));
	}

	@Override
	public H2QNSet havingValue() {
		return this.intersect(this.owner.nodes());
	}

	@Override
	public H2QNSet havingValues(QVSet values) throws NullPointerException, IllegalArgumentException {
		return this.intersect(values.nodes());
	}

	@Override
	public H2QNSet havingState(boolean state) {
		return state ? this.intersect(this.owner.nodes()) : this.except(this.owner.nodes());
	}

	@Override
	public H2QNSet copy() {
		return this.owner.newNodes(this);
	}

	@Override
	public H2QNSet copy(Filter<? super QN> filter) throws NullPointerException {
		return this.owner.newNodes(Iterables.filter(this, filter));
	}

	@Override
	public H2QNSet order() {
		return new Order(this);
	}

	@Override
	public H2QNSet index() {
		return this.copy().index();
	}

	@Override
	public H2QNSet union(QNSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") UNION (").push(that).push(")"));
	}

	@Override
	public H2QNSet except(QNSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") EXCEPT (").push(that).push(")"));
	}

	@Override
	public H2QNSet intersect(QNSet set) throws NullPointerException, IllegalArgumentException {
		var that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") INTERSECT (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert {@link #owner Graphspeicher} und {@link #table Tabelle}. Wenn letztre {@code null} ist, wird sie über
	 * {@link H2QQ#H2QQ(H2QS)} erzeugt. Die Tabelle muss die Spalten {@code (N BIGINT NOT NULL)} besitzen. */
	protected H2QNSet(H2QS owner, H2QQ table) {
		super(owner, table);
	}

	@Override
	protected QN customItem(ResultSet item) throws SQLException {
		return this.owner.newNode(item.getInt(1));
	}

	static class Main extends H2QNSet {

		public Main(H2QS owner) {
			super(owner, new H2QQ().push("SELECT N FROM QN"));
		}

		@Override
		public H2QNSet index() {
			return this;
		}

		@Override
		public H2QVSet values() {
			return this.owner.values();
		}

	}

	static class Temp extends H2QNSet {

		public Temp(H2QS owner) {
			super(owner, null);
			new H2QQ().push("CREATE TEMPORARY TABLE ").push(this.table).push(" (N BIGINT NOT NULL)").update(owner);
		}

		@Override
		public H2QNSet.Temp copy() {
			return this;
		}

		@Override
		public H2QNSet.Temp index() {
			new H2QQ().push("CREATE INDEX IF NOT EXISTS ").push(this.table).push("_INDEX_N ON ").push(this.table).push(" (N)").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QNSet {

		public Order(H2QNSet that) {
			super(that.owner, new H2QQ().push("SELECT * FROM (").push(that).push(") ORDER BY N"));
		}

		@Override
		public H2QNSet.Order order() {
			return this;
		}

	}

}
