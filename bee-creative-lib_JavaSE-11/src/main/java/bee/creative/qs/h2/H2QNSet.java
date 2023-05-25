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
		final H2QNSet that = this.index();
		return false //
			| new H2QQ().push("delete from QN where N in (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("delete from QE where C in (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("delete from QE where P in (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("delete from QE where S in (").push(that).push(")").update(this.owner) //
			| new H2QQ().push("delete from QE where O in (").push(that).push(")").update(this.owner);
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet(this.owner, new H2QQ().push("select V from QN where N in (").push(this).push(")"));
	}

	@Override
	public void values(final Setter<QN, String> values) {
		try (final ResultSet rset = new H2QQ().push("select N, V from QN where N in (").push(this).push(")").select(this.owner)) {
			while (rset.next()) {
				values.set(this.owner.newNode(rset.getInt(1)), rset.getString(2));
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public QTSet tuples(final String name) throws NullPointerException, IllegalArgumentException {
		return new H2QTSet(this.owner, new Names(name), new H2QQ().push("select N C0 from (").push(this).push(")"));
	}

	@Override
	public H2QNSet having(final Filter<? super QN> filter) throws NullPointerException {
		return this.owner.newNodes(Iterables.filter(this, filter));
	}

	@Override
	public H2QNSet havingValue() {
		return this.intersect(this.owner.nodes());
	}

	@Override
	public H2QNSet havingValues(final QVSet values) throws NullPointerException, IllegalArgumentException {
		return this.intersect(values.nodes());
	}

	@Override
	public H2QNSet havingState(final boolean state) {
		return state ? this.intersect(this.owner.nodes()) : this.except(this.owner.nodes());
	}

	@Override
	public H2QNSet copy() {
		return this.owner.newNodes(this);
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
	public H2QNSet union(final QNSet set) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") union (").push(that).push(")"));
	}

	@Override
	public H2QNSet except(final QNSet set) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") except (").push(that).push(")"));
	}

	@Override
	public H2QNSet intersect(final QNSet set) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(set);
		return new H2QNSet(this.owner, new H2QQ().push("(").push(this).push(") intersect (").push(that).push(")"));
	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QNSet(final H2QS owner, final H2QQ select) {
		super(owner, select);
	}

	@Override
	protected QN item(final ResultSet item) throws SQLException {
		return this.owner.newNode(item.getInt(1));
	}

	static final class Save extends H2QNSet {

		public Save(final H2QS owner) {
			super(owner, new H2QQ().push("select N from QN"));
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

	static final class Temp extends H2QNSet {

		public Temp(final H2QS owner) {
			super(owner, null);
			new H2QQ().push("create temporary table ").push(this.table).push(" (N int not null)").update(owner);
		}

		@Override
		public H2QNSet.Temp copy() {
			return this;
		}

		@Override
		public H2QNSet.Temp index() {
			new H2QQ().push("create index if not exists ").push(this.table).push("_INDEX_N on ").push(this.table).push(" (N)").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QNSet {

		public Order(final H2QNSet that) {
			super(that.owner, new H2QQ().push("select * from (").push(that).push(") order by N"));
		}

		@Override
		public H2QNSet.Order order() {
			return this;
		}

	}

}
