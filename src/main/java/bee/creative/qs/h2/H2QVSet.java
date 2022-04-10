package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import bee.creative.qs.QVSet;
import bee.creative.util.Filter;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QVSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QVSet extends H2QOSet<String, QVSet> implements QVSet {

	static class Set1 extends H2QVSet {

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

	static class Save extends H2QVSet {

		Save(final H2QS owner) {
			super(owner, "select V from QN");
		}

		@Override
		public H2QNSet nodes() {
			return this.owner.nodes();
		}

	}

	/** Diese Klasse implementiert ein {@link QVSet} als temporäre {@link #index() indizierbare} Tabelle. */
	public static class Temp extends H2QVSet {

		Temp(final H2QS owner) {
			super(owner, null);
			this.owner.exec("create table " + this.name + " (V varchar(1G) not null)");
		}

		@Override
		public H2QVSet copy() {
			return this;
		}

		/** Diese Methode indiziert diese Menge zur schnelleren Suche und gibt {@code this} zurück. */
		public Temp index() {
			this.owner.exec("create index if not exists " + this.name + "_INDEX_V on " + this.name + " (V)");
			return this;
		}

	}

	static class Order extends Set1 {

		Order(final H2QS owner, final String select, final Object set1) {
			super(owner, select, set1);
		}

		@Override
		public H2QVSet order() {
			return this;
		}

	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QVSet(final H2QS owner, final String select) {
		super(owner, select);
	}

	@Override
	protected String next(final ResultSet item) throws SQLException {
		return item.getString(1);
	}

	@Override
	public H2QNSet nodes() {
		return new H2QNSet.Set1(this.owner, "select N from QN where V in (select * from " + this.name + ")", this);
	}

	@Override
	public boolean popAll() {
		return this.owner.exec("delete from QN where V in (select * from " + this.name + ")");
	}

	@Override
	public H2QVSet having(final Filter<? super String> filter) throws NullPointerException {
		return this.owner.newValues(Iterables.filter(this, filter));
	}

	@Override
	public H2QVSet havingState(final boolean state) {
		return state ? this.intersect(this.owner.values()) : this.except(this.owner.values());
	}

	@Override
	public H2QVSet copy() {
		return this.owner.newValues(this);
	}

	@Override
	public H2QVSet order() {
		return new Order(this.owner, "select * from " + this.name + " order by V", this);
	}

	@Override
	public H2QVSet union(final QVSet set) throws NullPointerException, IllegalArgumentException {
		final H2QVSet that = this.owner.asQVSet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") union (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QVSet except(final QVSet set) throws NullPointerException, IllegalArgumentException {
		final H2QVSet that = this.owner.asQVSet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") except (select * from " + that.name + ")", this, that);
	}

	@Override
	public H2QVSet intersect(final QVSet set) throws NullPointerException, IllegalArgumentException {
		final H2QVSet that = this.owner.asQVSet(set);
		return new Set2(this.owner, "(select * from " + this.name + ") intersect (select * from " + that.name + ")", this, that);
	}

}
