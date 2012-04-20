package bee.creative.array;

import java.util.Arrays;

/**
 * Diese Klasse implementiert ein {@link IntegerArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactIntegerArray extends CompactArray<int[], Integer> implements IntegerArray {

	/**
	 * Diese Klasse implementiert ein {@link IntegerArray} als modifizierbare Sicht auf einen Teil eines
	 * {@link CompactIntegerArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactIntegerSubArray extends CompactSubArray<CompactIntegerArray, int[], Integer> implements
		IntegerArray {

		/**
		 * Dieser Konstrukteur initialisiert Besitzer und Indices.
		 * 
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder
		 *         {@code finalIndex > owner.size()} oder {@code startIndex > finalIndex}).
		 */
		public CompactIntegerSubArray(final CompactIntegerArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IntegerArraySection section() {
			return new CompactIntegerSubArraySection(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final int[] values) {
			this.get(index, IntegerArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final int value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final int[] values) {
			this.set(index, IntegerArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final int value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final int[] values) {
			this.add(this.size(), IntegerArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IntegerArray subArray(final int fromIndex, final int toIndex) {
			return (IntegerArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link IntegerArraySection} eines {@link CompactIntegerArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactIntegerArraySection extends IntegerArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactIntegerArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactIntegerArraySection(final CompactIntegerArray owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int[] array() {
			return this.owner.array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int startIndex() {
			return this.owner.from;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int finalIndex() {
			final CompactIntegerArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link IntegerArraySection} eines {@link CompactIntegerSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactIntegerSubArraySection extends IntegerArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactIntegerSubArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactIntegerSubArraySection(final CompactIntegerSubArray owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.owner.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int[] array() {
			return this.owner.owner.array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int startIndex() {
			return this.owner.startIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int finalIndex() {
			return this.owner.finalIndex;
		}

	}

	/**
	 * Dieses Feld speichert das leere {@code int}-Array.
	 */
	protected static final int[] VOID = new int[0];

	/**
	 * Dieses Feld speichert das {@code int}-Array.
	 */
	protected int[] array;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int[] getArray() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setArray(final int[] array) {
		this.array = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int[] newArray(final int length) {
		if(length == 0) return CompactIntegerArray.VOID;
		return new int[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void clearArray(final int[] array, final int fromIndex, final int toIndex) {
		Arrays.fill(array, fromIndex, toIndex, (int)0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getValue(final int index) {
		return Integer.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(final int index, final Integer value) {
		this.set(index, value.intValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getLength(final int[] array) {
		return array.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final int[] values) {
		this.get(index, IntegerArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final int value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final int[] values) {
		this.set(index, IntegerArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int value) {
		this.add(this.size, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int[] values) {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final int value) {
		this.exclusiveIndex(index);
		this.insert(index, 1);
		this.set(this.from + index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final int[] values) {
		this.add(this.size, IntegerArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IntegerArraySection section() {
		return new CompactIntegerArraySection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IntegerArray subArray(final int startIndex, final int finalIndex) {
		return new CompactIntegerSubArray(this, startIndex, finalIndex);
	}

}
