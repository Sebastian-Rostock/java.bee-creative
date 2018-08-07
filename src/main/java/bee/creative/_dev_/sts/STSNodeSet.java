package bee.creative._dev_.sts;

import java.util.Iterator;
import bee.creative.array.IntegerArraySection;

/** Diese Klasse implementiert eine abstract Menge von {@link STSNode Knoten} eines {@link #store() Graphspeichers}, über welche in aufsteigender Ordnung
 * iteriert werden kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public abstract class STSNodeSet extends STSItemSet<STSNode> {

	/** Diese Klasse implementiert die Menge aller Knoten eines Graphspeichers.
	 *
	 * @see STSStore#getNodeSet() */
	protected static class StoreNodeSet extends STSNodeSet {

		public StoreNodeSet(final STSStore store) {
			super(store);
		}

		@Override
		protected ItemIndex customIndex() {
			return new SequenceIndex(this.store.customGetNodeIndex(), this.store.customGetNodeCount());
		}

		@Override
		public boolean contains(final Object item) {
			return (item instanceof STSNode) && this.store.contains((STSNode)item);
		}

		@Override
		public int minSize() {
			return this.store.customGetNodeCount();
		}

		@Override
		public int maxSize() {
			return this.store.customGetNodeCount();
		}

		@Override
		public STSNodeSet toUnion(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return this;
		}

		@Override
		public STSNodeSet toIntersection(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return that;
		}

	}

	/** Diese Klasse implementiert eine Menge mit einem Knoten. */
	protected static class SingleNodeSet extends STSNodeSet {

		final STSNode node;

		public SingleNodeSet(final STSNode node) {
			super(node.store);
			this.node = node;
		}

		@Override
		protected ItemIndex customIndex() {
			return new SequenceIndex(this.node.index, 1);
		}

		@Override
		public boolean contains(final Object item) {
			return this.node.equals(item);
		}

		@Override
		public int minSize() {
			return 1;
		}

		@Override
		public int maxSize() {
			return 1;
		}

	}

	/** Diese Klasse implementiert die leere Knotenmenge. */
	protected static class EmptyNodeSet extends STSNodeSet {

		public EmptyNodeSet(final STSStore store) {
			super(store);
		}

		@Override
		protected ItemIndex customIndex() {
			return new EmptyIndex();
		}

		@Override
		public boolean contains(final Object item) {
			return false;
		}

		@Override
		public int minSize() {
			return 0;
		}

		@Override
		public int maxSize() {
			return 0;
		}

		@Override
		public STSNodeSet toUnion(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return that;
		}

		@Override
		public STSNodeSet toIntersection(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return this;
		}

	}

	/** Diese Klasse implementiert die Menge der Knoten zu einer gegebenen {@link IntegerArraySection Positionsliste}.<br>
	 * <b>Die Positionsliste muss aufsteigend geordnet und Duplikatfrei sein!</b> */
	protected static class ArrayNodeSet extends STSNodeSet {

		final IntegerArraySection items;

		public ArrayNodeSet(final STSStore store, final IntegerArraySection items) {
			super(store);
			this.items = items;
		}

		@Override
		protected ItemIndex customIndex() {
			return new ArrayIndex(this.items);
		}

		@Override
		public boolean contains(final Object item) {
			return (item instanceof STSNode) && this.containsImpl(this.items, (STSNode)item);
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

	/** Diese Klasse implementiert die Vereinigungsmenge zweier Knotenmengen. */
	protected static class UnionNodeSet extends STSNodeSet {

		final STSNodeSet items1;

		final STSNodeSet items2;

		public UnionNodeSet(final STSNodeSet items1, final STSNodeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = items2;
		}

		@Override
		protected ItemIndex customIndex() {
			return new UnionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
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

	/** Diese Klasse implementiert die Schnittmenge zweier Knotenmengen. */
	protected static class IntersectionNodeSet extends STSNodeSet {

		final STSNodeSet items1;

		final STSNodeSet items2;

		public IntersectionNodeSet(final STSNodeSet items1, final STSNodeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = items2;
		}

		@Override
		protected ItemIndex customIndex() {
			return new IntersectionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) && this.items2.contains(item);
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

	protected STSNodeSet(final STSStore store) {
		super(store);
	}

	/** Diese Methode gibt die Vereinigungsmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Vereinigungsmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSNodeSet toUnion(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
		if (!this.store.contains(that)) throw new IllegalArgumentException();
		if (that instanceof EmptyNodeSet) return this;
		if (that instanceof StoreNodeSet) return that;
		return new UnionNodeSet(this, that);
	}

	/** Diese Methode gibt die Schnittmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Schnittmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSNodeSet toIntersection(final STSNodeSet that) throws NullPointerException, IllegalArgumentException {
		if (!this.store.contains(that)) throw new IllegalArgumentException();
		if (that instanceof EmptyNodeSet) return that;
		if (that instanceof StoreNodeSet) return this;
		return new IntersectionNodeSet(this, that);
	}

	/** {@inheritDoc} */
	@Override
	protected Iterator<STSNode> customIterator(final ItemIndex index) {
		return new Iterator<STSNode>() {

			STSNode result;

			{
				this.next();
			}

			@Override
			public STSNode next() {
				final STSNode result = this.result;
				final int next = index.next();
				this.result = next != Integer.MAX_VALUE ? STSNodeSet.this.store.customGetNode(next) : null;
				return result;
			}

			@Override
			public boolean hasNext() {
				return this.result != null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

}
