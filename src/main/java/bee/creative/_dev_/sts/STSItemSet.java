package bee.creative._dev_.sts;

import java.util.AbstractSet;
import java.util.Iterator;
import bee.creative.array.IntegerArraySection;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert eine abstrakte Menge von {@link STSItem Datensätzen} eines {@link #store() Graphspeichers}, über welche in aufsteigender Ordnung
 * iteriert werden kann.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
@SuppressWarnings ("javadoc")
public abstract class STSItemSet<GItem extends STSItem> extends AbstractSet<GItem> {

	/** Diese Schnittstelle definiert einen laufenden Zeiger, der ägnlich einem {@link Iterator} über eine aufsteigend geordneten Positionsmenge läuft und mit der
	 * Konstanten {@link Integer#MAX_VALUE} das Ende der Iteration anzeigt. */
	protected static interface ItemIndex {

		/** Diese Methode gibt die Position des nächsten Datensatzes zurück.<br>
		 * Wenn keine weitere Position existiert, wird {@link Integer#MAX_VALUE} geliefert.
		 *
		 * @return nächste Position. */
		public int next();

	}

	/** Diese Klasse implementiert einen abstrakten {@link Iterator} über {@link STSItem Datensätze}. */
	protected static abstract class ItemIterator implements Iterator<STSItem> {

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/** Diese Klasse implementiert einen {@link ItemIndex}, der über eine gegebene Positionsliste läuft. */
	protected static class ArrayIndex extends SequenceIndex {

		final int[] indexArray;

		public ArrayIndex(final IntegerArraySection indexArray) {
			super(indexArray.startIndex(), indexArray.size());
			this.indexArray = indexArray.array();
		}

		{}

		@Override
		public int next() {
			final int next = this.index;
			if (next >= this.limit) return Integer.MAX_VALUE;
			this.index = next + 1;
			return this.indexArray[next];
		}

	}

	/** Diese Klasse implementiert einen {@link ItemIndex}, der über die Vereinigungsmenge zweier gegebener Positionsmengen läuft. */
	protected static class UnionIndex extends IntersectionIndex {

		int next1;

		int next2;

		public UnionIndex(final ItemIndex index1, final ItemIndex index2) {
			super(index1, index2);
			this.next1 = index1.next();
			this.next2 = index2.next();
		}

		{}

		@Override
		public int next() {
			final int next1 = this.next1, next2 = this.next2;
			if (next1 < next2) {
				this.next1 = this.index1.next();
				return next1;
			}
			if (next2 < next1) {
				this.next2 = this.index2.next();
				return next2;
			}
			this.next1 = this.index1.next();
			this.next2 = this.index2.next();
			return next1;
		}

	}

	/** Diese Klasse implementiert einen {@link ItemIndex}, der sequenziell über einen gegebenen Bereich von Positionen läuft. */
	static protected class SequenceIndex implements ItemIndex {

		int index;

		int limit;

		public SequenceIndex(final int index, final int count) {
			this.index = index;
			this.limit = index + count;
		}

		{}

		@Override
		public int next() {
			final int next = this.index;
			this.index = next < this.limit ? next + 1 : Integer.MAX_VALUE;
			return next;
		}

	}

	/** Diese Klasse implementiert einen {@link ItemIndex}, der über die Schnittmenge zweier gegebener Positionsmengen läuft. */
	protected static class IntersectionIndex implements ItemIndex {

		final ItemIndex index1;

		final ItemIndex index2;

		public IntersectionIndex(final ItemIndex index1, final ItemIndex index2) {
			this.index1 = index1;
			this.index2 = index2;
		}

		{}

		@Override
		public int next() {
			int next1 = this.index1.next(), next2 = this.index2.next();
			while (next1 != next2) {
				while (next1 < next2) {
					next1 = this.index1.next();
				}
				while (next2 < next1) {
					next2 = this.index2.next();
				}
			}
			return next1;
		}

	}

	{}

	/** Diese Methode gibt {@link #minSize()} der Vereinigungsmenge der gegebenen Mengen zurück. */
	protected static int unionMinSizeImpl(final STSItemSet<?> items1, final STSItemSet<?> items2) {
		return Math.max(items1.minSize(), items2.minSize());
	}

	/** Diese Methode gibt {@link #maxSize()} der Vereinigungsmenge der gegebenen Mengen zurück. */
	protected static int unionMaxSizeImpl(final STSItemSet<?> items1, final STSItemSet<?> items2, final int itemCount) {
		return Math.min(items1.maxSize() + items2.maxSize(), itemCount);
	}

	/** Diese Methode gibt {@link #minSize()} der Schnittmenge der gegebenen Mengen zurück. */
	protected static int intersectionMinSizeImpl(final STSItemSet<?> items1, final STSItemSet<?> items2, final int itemCount) {
		return Math.max((items1.minSize() + items2.minSize()) - itemCount, 0);
	}

	/** Diese Methode gibt {@link #maxSize()} der Schnittmenge der gegebenen Mengen zurück. */
	protected static int intersectionMaxSizeImpl(final STSItemSet<?> items1, final STSItemSet<?> items2) {
		return Math.min(items1.maxSize(), items2.maxSize());
	}

	{}

	/** Dieses Feld speichert den Graphspeicher, der die Elemente dieser Menge verwaltet. */
	protected final STSStore store;

	protected STSItemSet(final STSStore store) {
		this.store = store;
	}

	{}

	/** Diese Methode gibt die Positionsmenge der Datensätze dieser Menge zurück.
	 *
	 * @see #iterator()
	 * @see #customIterator(ItemIndex)
	 * @return Positionsmenge. */
	protected abstract ItemIndex customIndex();

	/** Diese Methode gibt einen Iterator zur gegebenen Positionsmenge zurück.
	 *
	 * @see #iterator()
	 * @see #customIndex()
	 * @param index Positionsmenge.
	 * @return Iterator über Datensätze. */
	protected abstract Iterator<GItem> customIterator(ItemIndex index);

	/** Diese Methode gibt den die Elemente dieser Menge verwaltenden Graphspeicher zurück.
	 *
	 * @return Graphspeicher. */
	public final STSStore store() {
		return this.store;
	}

	/** Diese Methode gibt die Untergrenze für die Anzahl der Datensätze in dieser Menge zurück. Diese kann ohne {@link #iterator() Iteration} über die Datensätze
	 * dieser Menge ermittelt werden.
	 *
	 * @return minimale Datensatzanzahl. */
	public abstract int minSize();

	/** Diese Methode gibt die Obergrenze für die Anzahl der Datensätze in dieser Menge zurück. Diese kann ohne {@link #iterator() Iteration} über die Datensätze
	 * dieser Menge ermittelt werden.
	 *
	 * @return maximale Datensatzanzahl. */
	public abstract int maxSize();

	{}

	/** Diese Methode gibt die Anzahl der Datensätze in dieser Menge zurück. Wenn sich deren {@link #maxSize() Obergrenze} und {@link #minSize() Untergrenze}
	 * unterscheiden, musss zu deren Ermittlung über die Datensätze dieser Menge {@link #iterator() iteriert} werden.
	 *
	 * @return Datensatzanzahl. */
	@Override
	public int size() {
		final int minSize = this.minSize(), maxSize = this.maxSize();
		if (minSize == maxSize) return minSize;
		return Iterables.size(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return (this.minSize() == 0) && (this.customIndex().next() != Integer.MAX_VALUE);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> iterator() {
		return this.customIterator(this.customIndex());
	}

}
