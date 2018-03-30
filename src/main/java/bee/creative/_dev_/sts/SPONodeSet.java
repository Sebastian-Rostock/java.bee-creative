package bee.creative._dev_.sts;

import java.util.Iterator;
import bee.creative.util.Objects;

@SuppressWarnings ("javadoc")
public abstract class SPONodeSet extends STSItemSet<STSNode> {

	protected static class UnionNodeSet extends IntersectionNodeSet {

		public UnionNodeSet(final SPONodeSet items1, final SPONodeSet items2) {
			super(items1, items2);
		}

		{}

		@Override
		public int minSize() {
			return Math.max(this.items1.minSize(), this.items2.minSize());
		}

		@Override
		public int maxSize() {
			return Math.min(this.items1.maxSize() + this.items2.maxSize(), this.store().customNodeCount());
		}

		@Override
		protected IndexIterator customIndex() {
			return new UnionIndexIterator(this.items1.customIndex(), this.items2.customIndex());
		}

	}

	protected static class IntersectionNodeSet extends SPONodeSet {

		final SPONodeSet items1;

		final SPONodeSet items2;

		public IntersectionNodeSet(final SPONodeSet items1, final SPONodeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = Objects.assertNotNull(items2);
		}

		{}

		@Override
		public int minSize() {
			return Math.max(this.items1.minSize(), this.items2.minSize());
		}

		@Override
		public int maxSize() {
			return Math.min(this.items1.maxSize() + this.items2.maxSize(), this.store.customNodeCount());
		}

		@Override
		protected ItemIndex customIndex() {
			return new UnionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

	}

	protected SPONodeSet(final SPOStore store) {
		super(store);
	}

	public SPONodeSet toUnion(final SPONodeSet that) throws NullPointerException, IllegalArgumentException {
		if (this.store == that.store) return new UnionNodeSet(this, that);
		throw new IllegalArgumentException();
	}

	public SPONodeSet toIntersection(final SPONodeSet that) {

		return null;
	}

	@Override
	protected Iterator<STSNode> customIterator(final ItemIndex index) {
		return this.store.customNodeIterator(index);
	}

}
