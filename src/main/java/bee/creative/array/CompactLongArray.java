package bee.creative.array;

/**
 * Diese Klasse implementiert ein {@link LongArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactLongArray extends CompactArray<long[], Long> implements LongArray {

	/**
	 * Diese Klasse implementiert ein {@link LongArray} als modifizierbare Sicht auf einen Teil eines {@link CompactLongArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactLongSubArray extends CompactSubArray<CompactLongArray, long[], Long> implements LongArray {

		/**
		 * Dieser Konstruktor initialisiert Besitzer und Indices.
		 * 
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}).
		 */
		public CompactLongSubArray(final CompactLongArray owner, final int startIndex, final int finalIndex) throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

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
			return this._owner_.get(this._ownerIndex_(index));
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
			this._owner_.set(this._ownerIndex_(index), value);
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
			return (LongArray)this._ownerSubArray_(fromIndex, toIndex);
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
		protected final CompactLongArray _owner_;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactLongArraySection(final CompactLongArray owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this._owner_._size_;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long[] array() {
			return this._owner_._array_;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int startIndex() {
			return this._owner_._from_;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int finalIndex() {
			final CompactLongArray owner = this._owner_;
			return owner._from_ + owner._size_;
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
		protected final CompactLongSubArray _owner_;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactLongSubArraySection(final CompactLongSubArray owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this._owner_.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long[] array() {
			return this._owner_._owner_._array_;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int startIndex() {
			return this._owner_._startIndex_;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int finalIndex() {
			return this._owner_._finalIndex_;
		}

	}

	{}

	/**
	 * Dieses Feld speichert das {@code long}-Array.
	 */
	protected long[] _array_;

	/**
	 * Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}.
	 */
	public CompactLongArray() {
		super();
	}

	/**
	 * Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactLongArray(final int capacity) throws IllegalArgumentException {
		super(capacity);
	}

	/**
	 * Dieser Konstruktor initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als internes Array wird das der gegebenen
	 * {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird {@code 0.5} verwendet.
	 * 
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}.
	 */
	public CompactLongArray(final ArraySection<long[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long[] _array_() {
		return this._array_;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _array_(final long[] array) {
		this._array_ = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected long[] _allocArray_(final int length) {
		return new long[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long _value_(final int index) {
		return Long.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _value_(final int index, final Long value) {
		this.set(index, value.longValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int _capacity_() {
		return this._array_.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long get(final int index) {
		return this._array_[this._inclusiveIndex_(index)];
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
		this._array_[this._inclusiveIndex_(index)] = value;
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
		this.add(this._size_, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final long[] values) {
		this.add(this._size_, values);
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
		this.add(this._size_, LongArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long[] array() {
		return this._array_;
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
