package bee.creative.array;

/** Diese Klasse implementiert ein {@link ShortArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactShortArray extends CompactArray<short[], Short> implements ShortArray {

	/** Diese Klasse implementiert ein {@link ShortArray} als modifizierbare Sicht auf einen Teil eines {@link CompactShortArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactShortSubArray extends CompactSubArray<CompactShortArray, short[], Short> implements ShortArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactShortSubArray(final CompactShortArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public ShortArraySection section() {
			return new CompactShortSubArraySection(this);
		}

		/** {@inheritDoc} */
		@Override
		public short get(final int index) {
			return this._owner_.get(this._ownerIndex_(index));
		}

		/** {@inheritDoc} */
		@Override
		public void get(final int index, final short[] values) {
			this.get(index, ShortArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final short value) {
			this._owner_.set(this._ownerIndex_(index), value);
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final short[] values) {
			this.set(index, ShortArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void add(final short value) {
			this.add(this.size(), value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final short[] values) {
			this.add(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final short value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final short[] values) {
			this.add(this.size(), ShortArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public ShortArray subArray(final int fromIndex, final int toIndex) {
			return (ShortArray)this._ownerSubArray_(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link ShortArraySection} eines {@link CompactShortArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactShortArraySection extends ShortArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactShortArray _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactShortArraySection(final CompactShortArray owner) throws NullPointerException {
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
		public short[] array() {
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
			final CompactShortArray owner = this._owner_;
			return owner._from_ + owner._size_;
		}

	}

	/** Diese Klasse implementiert die live {@link ShortArraySection} eines {@link CompactShortSubArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactShortSubArraySection extends ShortArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactShortSubArray _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactShortSubArraySection(final CompactShortSubArray owner) throws NullPointerException {
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
		public short[] array() {
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

	/** Dieses Feld speichert das {@code short}-Array. */
	protected short[] _array_;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactShortArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactShortArray(final int capacity) throws IllegalArgumentException {
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
	public CompactShortArray(final ArraySection<short[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected short[] _array_() {
		return this._array_;
	}

	/** {@inheritDoc} */
	@Override
	protected void _array_(final short[] array) {
		this._array_ = array;
	}

	/** {@inheritDoc} */
	@Override
	protected short[] _allocArray_(final int length) {
		return new short[length];
	}

	/** {@inheritDoc} */
	@Override
	protected Short _value_(final int index) {
		return Short.valueOf(this.get(index));
	}

	/** {@inheritDoc} */
	@Override
	protected void _value_(final int index, final Short value) {
		this.set(index, value.shortValue());
	}

	/** {@inheritDoc} */
	@Override
	protected int customCapacity() {
		return this._array_.length;
	}

	/** {@inheritDoc} */
	@Override
	public short get(final int index) {
		return this._array_[this._inclusiveIndex_(index)];
	}

	/** {@inheritDoc} */
	@Override
	public void get(final int index, final short[] values) {
		this.get(index, ShortArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final short value) {
		this._array_[this._inclusiveIndex_(index)] = value;
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final short[] values) {
		this.set(index, ShortArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void add(final short value) {
		this.add(this._size_, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final short[] values) {
		this.add(this._size_, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final short value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final short[] values) {
		this.add(this._size_, ShortArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public short[] array() {
		return this._array_;
	}

	/** {@inheritDoc} */
	@Override
	public ShortArraySection section() {
		return new CompactShortArraySection(this);
	}

	/** {@inheritDoc} */
	@Override
	public ShortArray subArray(final int startIndex, final int finalIndex) {
		return new CompactShortSubArray(this, startIndex, finalIndex);
	}

}
