package bee.creative._dev_.sts;

import java.util.Iterator;
import bee.creative._dev_.sts.STSItemSet.ItemIndex;
import bee.creative._dev_.sts.STSItemSet.SequenceIndex;
import bee.creative.array.IntegerArraySection;

/** Diese Klasse implementiert eine abstract Menge von {@link STSNode Knoten}, über welche in aufsteigender Ordnung iteriert werden kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class STSNodeSet extends STSItemSet<STSNode> {

	@SuppressWarnings ("javadoc")
	protected static class ArrayNodeSet extends STSNodeSet {

		final IntegerArraySection items;

		public ArrayNodeSet(final STSStore store, final IntegerArraySection items) {
			super(store);
			this.items = items;
		}

		{}

		@Override
		protected ItemIndex customIndex() {
			return new ArrayIndex(this.items);
		}

		@Override
		public int minSize() {
			return this.items.size();
		}

		@Override
		public int maxSize() {
			return this.items.size();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class UnionNodeSet extends STSNodeSet {

		final STSNodeSet items1;

		final STSNodeSet items2;

		public UnionNodeSet(final STSNodeSet items1, final STSNodeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = items2;
		}

		{}

		@Override
		protected ItemIndex customIndex() {
			return new UnionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

		@Override
		public int minSize() {
			return STSItemSet.unionMinSizeImpl(this.items1, this.items2);
		}

		@Override
		public int maxSize() {
			return STSItemSet.unionMaxSizeImpl(this.items1, this.items2, this.store.customGetNodeCount());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class SectionNodeSet extends STSNodeSet {

		final int index;

		final int count;

		public SectionNodeSet(final STSStore store, final int index, final int count) {
			super(store);
			this.index = index;
			this.count = count;
		}

		{}

		@Override
		public int minSize() {
			return this.count;
		}

		@Override
		public int maxSize() {
			return this.count;
		}

		@Override
		protected ItemIndex customIndex() {
			return new SequenceIndex(this.index, this.count);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IntersectionNodeSet extends STSNodeSet {

		final STSNodeSet items1;

		final STSNodeSet items2;

		public IntersectionNodeSet(final STSNodeSet items1, final STSNodeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = items2;
		}

		{}

		@Override
		protected ItemIndex customIndex() {
			return new IntersectionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

		@Override
		public int minSize() {
			return STSItemSet.intersectionMinSizeImpl(this.items1, this.items2, this.store.customGetNodeCount());
		}

		@Override
		public int maxSize() {
			return STSItemSet.intersectionMaxSizeImpl(this.items1, this.items2);
		}

	}

	{}

	@SuppressWarnings ("javadoc")
	protected STSNodeSet(final STSStore store) {
		super(store);
	}

	{}

	/** Diese Methode gibt die Vereinigungsmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Vereinigungsmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSNodeSet toUnion(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
		if (this.store == that.store) return new UnionNodeSet(that, this);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Schnittmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Schnittmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSNodeSet toIntersection(final STSNodeSet that) {
		if (this.store == that.store) return new IntersectionNodeSet(that, this);
		throw new IllegalArgumentException();
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected Iterator<STSNode> customIterator(final ItemIndex index) {
		return this.store.customGetNodeIterator(index);
	}

}
