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

	static class Set1 extends H2QNSet {

		final Object set1;

		Set1(final H2QS owner, final String select, final Object set1) {
			super(owner, select);
			this.set1 = set1;
		}

	}

	static class Set2 extends Set1 {

		final Object set2;

		Set2(final H2QS owner, final String select, final Object set1, final Object set2) {
			super(owner, select, set1);
			this.set2 = set2;
		}

	}

	static class Save extends H2QNSet {

		Save(final H2QS owner) {
			super(owner, "select N from QN");
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
			this.owner.exec("create table " + this.name + " (N int not null)");
		}

		@Override
		public H2QNSet copy() {
			return this;
		}

		/** Diese Methode indiziert diese Menge zur schnelleren Suche und gibt {@code this} zurück. */
		public Temp index() {
			this.owner.exec("create index " + this.name + "_INDEX_N on " + this.name + " (N)");
			return this;
		}

	}

	static class Order extends Set1 {

		Order(final H2QNSet that) {
			super(that.owner, "select * from " + that.name + " order by N", that);
		}

		@Override
		public H2QNSet order() {
			return this;
		}

	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QNSet(final H2QS owner, final String select) {
		super(owner, select);
	}

	@Override
	protected QN next(final ResultSet item) throws SQLException {
		return this.owner.newNode(item.getInt(1));
	}

	@Override
	public boolean popAll() {
		final H2QNSet that = this.copy();
		return this.owner.exec("delete from QN where N in (select * from " + that.name + ")")
			| this.owner.exec("delete from QE where exists (select N from " + that.name + " where C=N or P=N or S=N or O=N)");
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet.Set1(this.owner, "select V from QN where N in (select * from " + this.name + ")", this);
	}

	@Override
	public QTSet tuples(final String name) throws NullPointerException, IllegalArgumentException {
		return new H2QTSet.Set1(this.owner, new Names(name), "select N C0 from " + this.name, this);
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
	public H2QNSet union(final QNSet set) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") union (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QNSet except(final QNSet set) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") except (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QNSet intersect(final QNSet set) throws NullPointerException, IllegalArgumentException {
		final H2QNSet that = this.owner.asQNSet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") intersect (select * from " + that.name + ")", this, that);
	}

}
