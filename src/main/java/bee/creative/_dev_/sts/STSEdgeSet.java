package bee.creative._dev_.sts;

import java.util.Iterator;
import bee.creative.array.IntegerArraySection;

/** Diese Klasse implementiert eine abstract Menge von {@link STSEdge Kanten} eines {@link #store() Graphspeichers}, über welche in aufsteigender Ordnung
 * iteriert werden kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
  abstract class STSEdgeSet extends STSItemSet<STSEdge> {

	/** Diese Klasse implementiert die Menge aller Kanten eines Graphspeichers.
	 *
	 * @see STSStore#getEdgeSet() */
	protected static class StoreEdgeSet extends STSEdgeSet {

		public StoreEdgeSet(final STSStore store) {
			super(store);
		}

		@Override
		protected ItemIndex customIndex() {
			return new SequenceIndex(this.store.customGetEdgeIndex(), this.store.customGetEdgeCount());
		}

		@Override
		public boolean contains(final Object item) {
			return (item instanceof STSEdge) && this.store.contains((STSEdge)item);
		}

		@Override
		public int minSize() {
			return this.store.customGetEdgeCount();
		}

		@Override
		public int maxSize() {
			return this.store.customGetEdgeCount();
		}

		@Override
		public STSEdgeSet toUnion(final STSEdgeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return this;
		}

		@Override
		public STSEdgeSet toIntersection(final STSEdgeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return that;
		}

	}

	/** Diese Klasse implementiert eine Menge mit einre Kante. */
	protected static class SingleEdgeSet extends STSEdgeSet {

		final STSEdge edge;

		public SingleEdgeSet(final STSEdge node) {
			super(node.store);
			this.edge = node;
		}

		@Override
		protected ItemIndex customIndex() {
			return new SequenceIndex(this.edge.index, 1);
		}

		@Override
		public boolean contains(final Object item) {
			return this.edge.equals(item);
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

	/** Diese Klasse implementiert die leere Kantenmenge. */
	protected static class EmptyEdgeSet extends STSEdgeSet {

		public EmptyEdgeSet(final STSStore store) {
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
		public STSEdgeSet toUnion(final STSEdgeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return that;
		}

		@Override
		public STSEdgeSet toIntersection(final STSEdgeSet that) throws NullPointerException, IllegalArgumentException {
			if (!this.store.contains(that)) throw new IllegalArgumentException();
			return this;
		}

	}

	/** Diese Klasse implementiert die Menge der Kanten zu einer gegebenen {@link IntegerArraySection Positionsliste}. <b>Die Positionsliste muss aufsteigend
	 * geordnet und Duplikatfrei sein!</b> */
	protected static class ArrayEdgeSet extends STSEdgeSet {

		final IntegerArraySection items;

		public ArrayEdgeSet(final STSStore store, final IntegerArraySection items) {
			super(store);
			this.items = items;
		}

		@Override
		protected ItemIndex customIndex() {
			return new ArrayIndex(this.items);
		}

		@Override
		public boolean contains(final Object item) {
			return (item instanceof STSEdge) && this.containsImpl(this.items, (STSEdge)item);
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

	/** Diese Klasse implementiert die Vereinigungsmenge zweier gegebener Kantenmengen. */
	protected static class UnionEdgeSet extends STSEdgeSet {

		final STSEdgeSet items1;

		final STSEdgeSet items2;

		public UnionEdgeSet(final STSEdgeSet items1, final STSEdgeSet items2) {
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
			return STSItemSet.unionMaxSizeImpl(this.items1, this.items2, this.store.customGetEdgeCount());
		}

	}

	/** Diese Klasse implementiert die Schnittmenge zweier gegebener Kantenmengen. */
	protected static class IntersectionEdgeSet extends STSEdgeSet {

		final STSEdgeSet items1;

		final STSEdgeSet items2;

		public IntersectionEdgeSet(final STSEdgeSet items1, final STSEdgeSet items2) {
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
			return STSItemSet.intersectionMinSizeImpl(this.items1, this.items2, this.store.customGetEdgeCount());
		}

		@Override
		public int maxSize() {
			return STSItemSet.intersectionMaxSizeImpl(this.items1, this.items2);
		}

	}

	protected STSEdgeSet(final STSStore store) {
		super(store);
	}

	/** Diese Methode gibt die Vereinigungsmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Vereinigungsmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSEdgeSet toUnion(final STSEdgeSet that) throws NullPointerException, IllegalArgumentException {
		if (this.store == that.store) return new UnionEdgeSet(this, that);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Schnittmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Schnittmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSEdgeSet toIntersection(final STSEdgeSet that) {
		if (this.store == that.store) return new IntersectionEdgeSet(this, that);
		throw new IllegalArgumentException();
	}

	/** {@inheritDoc} */
	@Override
	protected Iterator<STSEdge> customIterator(final ItemIndex index) {
		return new Iterator<STSEdge>() {

			STSEdge result;

			{
				this.next();
			}

			@Override
			public STSEdge next() {
				final STSEdge result = this.result;
				final int next = index.next();
				this.result = next != Integer.MAX_VALUE ? STSEdgeSet.this.store.customGetEdge(next) : null;
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
