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
	public H2QNSet nodes() {
		return new H2QNSet(this.owner, new H2QQ().push("select N from QN where V in (").push(this).push(")"));
	}

	@Override
	public void nodes(final Setter<String, QN> nodes) {
		try (final ResultSet rset = new H2QQ().push("select V, N from QN where V in (").push(this).push(")").select(this.owner)) {
			while (rset.next()) {
				nodes.set(rset.getString(1), this.owner.newNode(rset.getInt(2)));
			}
		} catch (final SQLException cause) {
			throw new IllegalStateException(cause);
		}
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
	public H2QVSet index() {
		return this.copy().index();
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

	/** Dieser Konstruktor initialisiert den Graphspeicher sowie die Anfrage des {@code VIEW} (oder {@code null}). */
	protected H2QVSet(final H2QS owner, final H2QQ select) {
		super(owner, select);
	}

	@Override
	protected String item(final ResultSet item) throws SQLException {
		return item.getString(1);
	}

	static class Save extends H2QVSet {

		public Save(final H2QS owner) {
			super(owner, new H2QQ().push("select V from QN"));
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

		public Temp(final H2QS owner) {
			super(owner, null);
			new H2QQ().push("create temporary table ").push(this.table).push(" (V varchar(1G) not null)").update(this.owner);
		}

		@Override
		public H2QVSet copy() {
			return this;
		}

		@Override
		public H2QVSet index() {
			new H2QQ().push("create index if not exists ").push(this.table).push("_INDEX_V on ").push(this.table).push(" (V)").update(this.owner);
			return this;
		}

	}

	static class Order extends H2QVSet {

		public Order(final H2QVSet that) {
			super(that.owner, new H2QQ().push("select * from (").push(that).push(") order by V"));
		}

		@Override
		public H2QVSet order() {
			return this;
		}

	}

}
