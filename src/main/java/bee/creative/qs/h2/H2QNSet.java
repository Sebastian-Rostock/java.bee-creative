package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import bee.creative.qs.QN;
import bee.creative.qs.QNSet;
import bee.creative.qs.QVSet;

/** Diese Klasse implementiert ein {@link QNSet} als Sicht auf das ergebnis einer SQL-Anfrage.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class H2QNSet extends H2QXSet<QN, QNSet> implements QNSet {

	static class Iter extends H2QXIter<QN> {

		public Iter(final H2QNSet owner) {
			super(owner);
		}

		@Override
		public QN next(final ResultSet item) throws SQLException {
			return new H2QN(this.owner.owner, item.getInt(1));
		}

	}

	static class Set1 extends H2QNSet {

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

	static class Save extends H2QNSet {

		public Save(final H2QS owner) {
			super(owner, H2QQ.selectNodesSave());
		}

		@Override
		public H2QVSet values() {
			return this.owner.values();
		}

	}

	static class Copy extends H2QNSet {

		final int key;

		public Copy(final H2QS owner, final int key) {
			super(owner, H2QQ.selectCopyNodes(key));
			this.key = key;
		}

		@Override
		protected void finalize() throws Throwable {
			this.owner.execImpl(H2QQ.deleteCopyNodes(this.key));
		}

		@Override
		public H2QNSet copy() {
			return this;
		}

	}

	static class Order extends Set1 {

		public Order(final H2QNSet set) {
			super(set.owner, H2QQ.selectNodesOrder(set), set);
		}

		@Override
		public H2QNSet order() {
			return this;
		}

	}

	H2QNSet(final H2QS owner, final String select) {
		super(owner, select);
	}

	@Override
	public boolean popAll() {
		final H2QS owner = this.owner;
		final H2QNSet set = this.copy();
		return false //
			| owner.execImpl(H2QQ.deleteValuesHavingNodes(set)) //
			| owner.execImpl(H2QQ.deleteEdgesHavingContexts(set)) //
			| owner.execImpl(H2QQ.deleteEdgesHavingPredicates(set)) //
			| owner.execImpl(H2QQ.deleteEdgesHavingSubjects(set)) //
			| owner.execImpl(H2QQ.deleteEdgesHavingObjects(set));
	}

	@Override
	public H2QVSet values() {
		return new H2QVSet.Set1(this.owner, H2QQ.selectNodesValues(this), this);
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
		return new Set2(this.owner, H2QQ.selectUnion(this, this.owner.asQNSet(set)), this, set);
	}

	@Override
	public H2QNSet except(final QNSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectExcept(this, this.owner.asQNSet(set)), this, set);
	}

	@Override
	public H2QNSet intersect(final QNSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QQ.selectIntersect(this, this.owner.asQNSet(set)), this, set);
	}

	@Override
	public Iterator<QN> iterator() {
		return new Iter(this);
	}

}
