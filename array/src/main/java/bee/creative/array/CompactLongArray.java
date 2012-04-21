package bee.creative.array;

/**
 * Diese Klasse implementiert ein {@link LongArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactLongArray extends CompactArray<long[], Long> implements LongArray {

	/**
	 * Diese Klasse implementiert ein {@link LongArray} als modifizierbare Sicht auf einen Teil eines
	 * {@link CompactLongArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactLongSubArray extends CompactSubArray<CompactLongArray, long[], Long> implements
		LongArray {

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
		public CompactLongSubArray(final CompactLongArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LongArraySection section() {
			return new CompactLongSubArraySection(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final long[] values) {
			this.get(index, LongArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final long value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final long[] values) {
			this.set(index, LongArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final long value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final long[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final long value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final long[] values) {
			this.add(this.size(), LongArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LongArray subArray(final int fromIndex, final int toIndex) {
			return (LongArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link LongArraySection} eines {@link CompactLongArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactLongArraySection extends LongArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactLongArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactLongArraySection(final CompactLongArray owner) throws NullPointerException {
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
		public long[] array() {
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
			final CompactLongArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link LongArraySection} eines {@link CompactLongSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactLongSubArraySection extends LongArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactLongSubArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactLongSubArraySection(final CompactLongSubArray owner) throws NullPointerException {
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
		public long[] array() {
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
	 * Dieses Feld speichert das leere {@code long}-Array.
	 */
	protected static final long[] VOID = new long[0];

	/**
	 * Dieses Feld speichert das {@code long}-Array.
	 */
	protected long[] array;

	/**
	 * Dieser Konstrukteur initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition
	 * {@code 0.5}.
	 */
	public CompactLongArray() {
		super();
	}

	/**
	 * Dieser Konstrukteur initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition
	 * {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactLongArray(final int capacity) throws IllegalArgumentException {
		super(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als
	 * internes Array wird das der gegebenen {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird
	 * {@code 0.5} verwendet.
	 * 
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder
	 *         {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}.
	 */
	public CompactLongArray(final ArraySection<long[]> section) throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException {
		super(section);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long[] getArray() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setArray(final long[] array) {
		this.array = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long[] newArray(final int length) {
		if(length == 0) return CompactLongArray.VOID;
		return new long[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long getValue(final int index) {
		return Long.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(final int index, final Long value) {
		this.set(index, value.longValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getLength(final long[] array) {
		return array.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final long[] values) {
		this.get(index, LongArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final long value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final long[] values) {
		this.set(index, LongArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final long value) {
		this.add(this.size, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final long[] values) {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final long value) {
		this.exclusiveIndex(index);
		this.insert(index, 1);
		this.set(this.from + index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final long[] values) {
		this.add(this.size, LongArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LongArraySection section() {
		return new CompactLongArraySection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LongArray subArray(final int startIndex, final int finalIndex) {
		return new CompactLongSubArray(this, startIndex, finalIndex);
	}

}
