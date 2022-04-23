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

	static class Save extends H2QVSet {

		Save(final H2QS owner) {
			super(owner, new H2QQ().push("select V from QN"));
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
			new H2QQ().push("create temporary table ").push(this.table).push(" (V varchar(1G) not null)").update(this.owner);
		}

		@Override
		public H2QVSet copy() {
			return this;
		}

		/** Diese Methode indiziert diese Menge zur schnelleren Suche und gibt {@code this} zurück. */
		public Temp index() {
			new H2QQ().push("create index if not exists ").push(this.table).push("_INDEX_V on ").push(this.table).push(" (V)").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QVSet {

		Order(final H2QVSet that) {
			super(that.owner, new H2QQ().push("select * from (").push(that).push(") order by V"));
		}

		@Override
		public H2QVSet order() {
			return this;
		}

	}

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QVSet(final H2QS owner, final H2QQ select) {
		super(owner, select);
	}

	@Override
	protected String next(final ResultSet item) throws SQLException {
		return item.getString(1);
	}

	@Override
	public H2QNSet nodes() {

		return new H2QNSet(this.owner, new H2QQ().push("select N from QN where V in (").push( this).push( ")"));
	}

	@Override
	public boolean popAll() {
		return new H2QQ().push("delete from QN where V in (").push(this).push(")").update(this.owner);
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
		return new Order(this);
	}

	@Override
	public H2QVSet union(final QVSet set) throws NullPointerException, IllegalArgumentException {
		final H2QVSet that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") union (").push(that).push(")"));
	}

	@Override
	public H2QVSet except(final QVSet set) throws NullPointerException, IllegalArgumentException {
		final H2QVSet that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") except (").push(that).push(")"));
	}

	@Override
	public H2QVSet intersect(final QVSet set) throws NullPointerException, IllegalArgumentException {
		final H2QVSet that = this.owner.asQVSet(set);
		return new H2QVSet(this.owner, new H2QQ().push("(").push(this).push(") intersect (").push(that).push(")"));
	}

}
