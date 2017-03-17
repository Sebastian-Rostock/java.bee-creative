package bee.creative.array;

/** Diese Klasse implementiert ein {@link BooleanArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactBooleanArray extends CompactArray<boolean[], Boolean> implements BooleanArray {

	/** Diese Klasse implementiert ein {@link BooleanArray} als modifizierbare Sicht auf einen Teil eines {@link CompactBooleanArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactBooleanSubArray extends CompactSubArray<CompactBooleanArray, boolean[], Boolean> implements BooleanArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactBooleanSubArray(final CompactBooleanArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public BooleanArraySection section() {
			return new CompactBooleanSubArraySection(this);
		}

		/** {@inheritDoc} */
		@Override
		public boolean get(final int index) {
			return this._owner_.get(this._ownerIndex_(index));
		}

		/** {@inheritDoc} */
		@Override
		public void get(final int index, final boolean[] values) {
			this.get(index, BooleanArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final boolean value) {
			this._owner_.set(this._ownerIndex_(index), value);
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final boolean[] values) {
			this.set(index, BooleanArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void add(final boolean value) {
			this.add(this.size(), value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final boolean[] values) {
			this.add(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final boolean value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final boolean[] values) {
			this.add(this.size(), BooleanArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public BooleanArray subArray(final int fromIndex, final int toIndex) {
			return (BooleanArray)this._ownerSubArray_(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link BooleanArraySection} eines {@link CompactBooleanArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactBooleanArraySection extends BooleanArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactBooleanArray _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactBooleanArraySection(final CompactBooleanArray owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this._owner_._size_;
		}

		/** {@inheritDoc} */
		@Override
		public boolean[] array() {
			return this._owner_._array_;
		}

		/** {@inheritDoc} */
		@Override
		public int startIndex() {
			return this._owner_._from_;
		}

		/** {@inheritDoc} */
		@Override
		public int finalIndex() {
			final CompactBooleanArray owner = this._owner_;
			return owner._from_ + owner._size_;
		}

	}

	/** Diese Klasse implementiert die live {@link BooleanArraySection} eines {@link CompactBooleanSubArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactBooleanSubArraySection extends BooleanArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactBooleanSubArray _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactBooleanSubArraySection(final CompactBooleanSubArray owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this._owner_.size();
		}

		/** {@inheritDoc} */
		@Override
		public boolean[] array() {
			return this._owner_._owner_._array_;
		}

		/** {@inheritDoc} */
		@Override
		public int startIndex() {
			return this._owner_._startIndex_;
		}

		/** {@inheritDoc} */
		@Override
		public int finalIndex() {
			return this._owner_._finalIndex_;
		}

	}

	{}

	/** Dieses Feld speichert das {@code boolean}-Array. */
	protected boolean[] _array_;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactBooleanArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactBooleanArray(final int capacity) throws IllegalArgumentException {
		super(capacity);
	}

	/** Dieser Konstruktor initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als internes Array wird das der gegebenen
	 * {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird {@code 0.5} verwendet.
	 *
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}. */
	public CompactBooleanArray(final ArraySection<boolean[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected boolean[] _array_() {
		return this._array_;
	}

	/** {@inheritDoc} */
	@Override
	protected void _array_(final boolean[] array) {
		this._array_ = array;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean[] _allocArray_(final int length) {
		return new boolean[length];
	}

	/** {@inheritDoc} */
	@Override
	protected Boolean _value_(final int index) {
		return Boolean.valueOf(this.get(index));
	}

	/** {@inheritDoc} */
	@Override
	protected void _value_(final int index, final Boolean value) {
		this.set(index, value.booleanValue());
	}

	/** {@inheritDoc} */
	@Override
	protected int customCapacity() {
		return this._array_.length;
	}

	/** {@inheritDoc} */
	@Override
	public boolean get(final int index) {
		return this._array_[this._inclusiveIndex_(index)];
	}

	/** {@inheritDoc} */
	@Override
	public void get(final int index, final boolean[] values) {
		this.get(index, BooleanArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final boolean value) {
		this._array_[this._inclusiveIndex_(index)] = value;
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final boolean[] values) {
		this.set(index, BooleanArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void add(final boolean value) {
		this.add(this._size_, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final boolean[] values) {
		this.add(this._size_, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final boolean value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final boolean[] values) {
		this.add(this._size_, BooleanArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public boolean[] array() {
		return this._array_;
	}

	/** {@inheritDoc} */
	@Override
	public BooleanArraySection section() {
		return new CompactBooleanArraySection(this);
	}

	/** {@inheritDoc} */
	@Override
	public BooleanArray subArray(final int startIndex, final int finalIndex) {
		return new CompactBooleanSubArray(this, startIndex, finalIndex);
	}

}
