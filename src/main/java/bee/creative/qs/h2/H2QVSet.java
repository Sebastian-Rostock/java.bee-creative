package bee.creative.qs.h2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import bee.creative.qs.QNSet;
import bee.creative.qs.QVSet;

public class H2QVSet extends H2QXSet<String, QVSet> implements QVSet {

	static final class Iter extends H2QXIter<String> {

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
			super(owner, H2QS.selectValues());
		}

		@Override
		public QNSet nodes() {
			return this.owner.nodes();
		}

	}

	static class Copy extends H2QVSet {

		final int key;

		public Copy(final H2QS owner, final int key) {
			super(owner, H2QS.selectValuesCopy(key));
			this.key = key;
		}

		@Override
		protected void finalize() throws Throwable {
			this.owner.updateImpl(H2QS.deleteValues(this.key));
		}

		@Override
		public QVSet copy() {
			return this;
		}

	}

	static class Order extends Set1 {

		public Order(final H2QVSet set) {
			super(set.owner, H2QS.selectValuesOrder(set), set);
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
	public QNSet nodes() {
		return new H2QNSet.Set1(this.owner, H2QS.selectValuesNodes(this), this);
	}

	@Override
	public boolean popAll() {
		return this.owner.popAllImpl(this);
	}

	@Override
	public H2QVSet havingState(final boolean state) {
		return state ? this.intersect(this.owner.values()) : this.except(this.owner.values());
	}

	@Override
	public QVSet copy() {
		return this.owner.newValues(this);
	}

	@Override
	public H2QVSet order() {
		return new Order(this);
	}

	@Override
	public H2QVSet union(final QVSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectUnion(this, this.owner.asQVSet(set)), this, set);
	}

	@Override
	public H2QVSet except(final QVSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectExcept(this, this.owner.asQVSet(set)), this, set);
	}

	@Override
	public H2QVSet intersect(final QVSet set) throws NullPointerException, IllegalArgumentException {
		return new Set2(this.owner, H2QS.selectIntersect(this, this.owner.asQVSet(set)), this, set);
	}

	@Override
	public Iterator<String> iterator() {
		return new Iter(this);
	}

}
