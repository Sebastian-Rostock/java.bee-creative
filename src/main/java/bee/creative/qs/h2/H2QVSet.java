package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import bee.creative.qs.QVSet;
import bee.creative.util.Filter;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert ein {@link QVSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QVSet extends H2QOSet<String, QVSet> implements QVSet {

	static final class Iter extends H2QOIter<String> {

		public Iter(final H2QVSet set) {
			super(set);
		}

		@Override
		public String next(final ResultSet item) throws SQLException {
			return item.getString(1);
		}

	}

	static class Set1 extends H2QVSet {

		final Object set1;

		public Set1(final H2QS owner, final String select, final Object set1) {
			super(owner, select);
			this.set1 = set1;
		}

	}

	static class Set2 extends Set1 {

		final Object set2;

		public Set2(final H2QS owner, final String select, final Object set1, final Object set2) {
			super(owner, select, set1);
			this.set2 = set2;
		}

	}

	static class Save extends H2QVSet {

		public Save(final H2QS owner) {
			super(owner, H2QQ.selectSaveValues());
		}

		@Override
		public H2QNSet nodes() {
			return this.owner.nodes();
		}

	}

	static class Temp extends H2QVSet {

		final int key;

		public Temp(final H2QS owner, final int key) {
			super(owner, H2QQ.selectTempValues(key));
			this.key = key;
		}

		@Override
		protected void finalize() throws Throwable {
			this.owner.execImpl(H2QQ.deleteTempValues(this.key));
		}

		@Override
		public H2QVSet copy() {
			return this;
		}

	}

	static class Order extends Set1 {

		public Order(final H2QVSet set) {
			super(set.owner, H2QQ.selectValuesOrder(set), set);
		}

		@Override
		public H2QVSet order() {
			return this;
		}

	}

	H2QVSet(final H2QS owner, final String select) {
		super(owner, select);
	}

	@Override
	public H2QNSet nodes() {
		return new H2QNSet.Set1(this.owner, H2QQ.selectSaveNodesHavingValues(this), this);
	}

	@Override
	public boolean popAll() {
		return this.owner.execImpl(H2QQ.deleteSaveNodesHavingValues(this));
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
		return new Set2(this.owner, H2QQ.selectUnion(this, this.owner.asQVSet(set)), this, set);
	}

	@Override
	public H2QVSet except(final QVSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectExcept(this, this.owner.asQVSet(set)), this, set);
	}

	@Override
	public H2QVSet intersect(final QVSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectIntersect(this, this.owner.asQVSet(set)), this, set);
	}

	@Override
	public Iterator<String> iterator() {
		return new Iter(this);
	}

}
