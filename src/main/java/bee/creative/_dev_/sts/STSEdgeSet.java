package bee.creative._dev_.sts;

import java.util.Iterator;
import bee.creative.array.IntegerArraySection;

/** Diese Klasse implementiert eine abstract Menge von {@link STSEdge Kanten}, über welche in aufsteigender Ordnung iteriert werden kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class STSEdgeSet extends STSItemSet<STSEdge> {

	/** Diese Klasse implementiert die Menge der Kanten zu einer gegebenen Positionsliste.<br>
	 * <b>Die Positionsliste muss aufsteigend geordnet und Duplikatfrei sein!</b> */
	@SuppressWarnings ("javadoc")
	protected static class ArrayEdgeSet extends STSEdgeSet {

		final IntegerArraySection items;

		public ArrayEdgeSet(final STSStore store, final IntegerArraySection items) {
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

	/** Diese Klasse implementiert die Vereinigungsmenge zweier gegebener Kantenmengen. */
	@SuppressWarnings ("javadoc")
	protected static class UnionEdgeSet extends STSEdgeSet {

		final STSEdgeSet items1;

		final STSEdgeSet items2;

		public UnionEdgeSet(final STSEdgeSet items1, final STSEdgeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = items2;
		}

		{}

		@Override
		public int minSize() {
			return STSItemSet.unionMinSizeImpl(this.items1, this.items2);
		}

		@Override
		public int maxSize() {
			return STSItemSet.unionMaxSizeImpl(this.items1, this.items2, this.store.customGetEdgeCount());
		}

		@Override
		protected ItemIndex customIndex() {
			return new UnionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

	}

	/** Diese Klasse implementiert die Menge der Kanten zu einer Positionsliste, die aus einem ersten Element und einer Elementanzahl rekonstruiert wird. */
	@SuppressWarnings ("javadoc")
	protected static class SectionEdgeSet extends STSEdgeSet {

		final int index;

		final int count;

		public SectionEdgeSet(final STSStore store, final int index, final int count) {
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

	/** Diese Klasse implementiert die Schnittmenge zweier gegebener Kantenmengen. */
	@SuppressWarnings ("javadoc")
	protected static class IntersectionEdgeSet extends STSEdgeSet {

		final STSEdgeSet items1;

		final STSEdgeSet items2;

		public IntersectionEdgeSet(final STSEdgeSet items1, final STSEdgeSet items2) {
			super(items1.store);
			this.items1 = items1;
			this.items2 = items2;
		}

		{}

		@Override
		public int minSize() {
			return STSItemSet.intersectionMinSizeImpl(this.items1, this.items2, this.store.customGetEdgeCount());
		}

		@Override
		public int maxSize() {
			return STSItemSet.intersectionMaxSizeImpl(this.items1, this.items2);
		}

		@Override
		protected ItemIndex customIndex() {
			return new IntersectionIndex(this.items1.customIndex(), this.items2.customIndex());
		}

	}

	{}

	@SuppressWarnings ("javadoc")
	protected STSEdgeSet(final STSStore store) {
		super(store);
	}

	{}

	/** Diese Methode gibt die Vereinigungsmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Vereinigungsmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSEdgeSet toUnion(final STSEdgeSet that) throws NullPointerException, IllegalArgumentException {
		if (this.store == that.store) return new UnionEdgeSet(that, this);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Schnittmenge dieser und der gegebenen Menge zurück.
	 *
	 * @param that Menge.
	 * @return Schnittmenge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn that von einem anderen {@link #store() Graphspeicher verwaltet wird}. */
	public STSEdgeSet toIntersection(final STSEdgeSet that) {
		if (this.store == that.store) return new IntersectionEdgeSet(that, this);
		throw new IllegalArgumentException();
	}

	{}

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
