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

/** Diese Klasse implementiert ein {@link QNSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QNSet extends H2QOSet<QN, QNSet> implements QNSet {

	static class Save extends H2QNSet {

		Save(final H2QS owner) {
			super(owner, new H2QQ().push("select N from QN"));
		}

		@Override
		public H2QVSet values() {
			return this.owner.values();
		}

	}

	/** Diese Klasse implementiert ein {@link QNSet} als temporäre {@link #index() indizierbare} Tabelle. */
	public static class Temp extends H2QNSet {

		Temp(final H2QS owner) {
			super(owner, null);
			new H2QQ().push("create temporary table ").push(this.table).push(" (N int not null)").update(owner);
		}

		@Override
		public H2QNSet.Temp copy() {
			return this;
		}

		/** Diese Methode indiziert diese Menge zur schnelleren Suche und gibt {@code this} zurück. */
		public H2QNSet.Temp index() {
			new H2QQ().push("create index if not exists ").push(this.table).push("_INDEX_N on ").push(this.table).push(" (N)").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QNSet {

		Order(final H2QNSet that) {
			super(that.owner, new H2QQ().push("select * from (").push(that).push(") order by N"));
		}

		@Override
		public H2QNSet order() {
			return this;
		}

	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QNSet(final H2QS owner, final H2QQ select) {
		super(owner, select);
	}

	@Override
	protected QN next(final ResultSet item) throws SQLException {
		return this.owner.newNode(item.getInt(1));
	}

	@Override
	public boolean popAll() {
		final H2QNSet that = this.copy().index();
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
	public H2QNSet.Temp copy() {
		return this.owner.newNodes(this);
	}

	@Override
	public H2QNSet order() {
		return new Order(this);
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

}
