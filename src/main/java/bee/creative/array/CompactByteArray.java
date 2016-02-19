package bee.creative.array;

/** Diese Klasse implementiert ein {@link ByteArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactByteArray extends CompactArray<byte[], Byte> implements ByteArray {

	/** Diese Klasse implementiert ein {@link ByteArray} als modifizierbare Sicht auf einen Teil eines {@link CompactByteArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactByteSubArray extends CompactSubArray<CompactByteArray, byte[], Byte> implements ByteArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 * 
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactByteSubArray(final CompactByteArray owner, final int startIndex, final int finalIndex) throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public ByteArraySection section() {
			return new CompactByteSubArraySection(this);
		}

		/** {@inheritDoc} */
		@Override
		public byte get(final int index) {
			return this._owner_.get(this._ownerIndex_(index));
		}

		/** {@inheritDoc} */
		@Override
		public void get(final int index, final byte[] values) {
			this.get(index, ByteArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final byte value) {
			this._owner_.set(this._ownerIndex_(index), value);
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final byte[] values) {
			this.set(index, ByteArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void add(final byte value) {
			this.add(this.size(), value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final byte[] values) {
			this.add(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final byte value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final byte[] values) {
			this.add(this.size(), ByteArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public ByteArray subArray(final int fromIndex, final int toIndex) {
			return (ByteArray)this._ownerSubArray_(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link ByteArraySection} eines {@link CompactByteArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactByteArraySection extends ByteArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactByteArray _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactByteArraySection(final CompactByteArray owner) throws NullPointerException {
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
		public byte[] array() {
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
			final CompactByteArray owner = this._owner_;
			return owner._from_ + owner._size_;
		}

	}

	/** Diese Klasse implementiert die live {@link ByteArraySection} eines {@link CompactByteSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactByteSubArraySection extends ByteArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactByteSubArray _owner_;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactByteSubArraySection(final CompactByteSubArray owner) throws NullPointerException {
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
		public byte[] array() {
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

	/** Dieses Feld speichert das {@code byte}-Array. */
	protected byte[] _array_;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactByteArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactByteArray(final int capacity) throws IllegalArgumentException {
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
	public CompactByteArray(final ArraySection<byte[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected byte[] _array_() {
		return this._array_;
	}

	/** {@inheritDoc} */
	@Override
	protected void _array_(final byte[] array) {
		this._array_ = array;
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] _allocArray_(final int length) {
		return new byte[length];
	}

	/** {@inheritDoc} */
	@Override
	protected Byte _value_(final int index) {
		return Byte.valueOf(this.get(index));
	}

	/** {@inheritDoc} */
	@Override
	protected void _value_(final int index, final Byte value) {
		this.set(index, value.byteValue());
	}

	/** {@inheritDoc} */
	@Override
	protected int _capacity_() {
		return this._array_.length;
	}

	/** {@inheritDoc} */
	@Override
	public byte get(final int index) {
		return this._array_[this._inclusiveIndex_(index)];
	}

	/** {@inheritDoc} */
	@Override
	public void get(final int index, final byte[] values) {
		this.get(index, ByteArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final byte value) {
		this._array_[this._inclusiveIndex_(index)] = value;
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final byte[] values) {
		this.set(index, ByteArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void add(final byte value) {
		this.add(this._size_, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final byte[] values) {
		this.add(this._size_, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final byte value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final byte[] values) {
		this.add(this._size_, ByteArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public byte[] array() {
		return this._array_;
	}

	/** {@inheritDoc} */
	@Override
	public ByteArraySection section() {
		return new CompactByteArraySection(this);
	}

	/** {@inheritDoc} */
	@Override
	public ByteArray subArray(final int startIndex, final int finalIndex) {
		return new CompactByteSubArray(this, startIndex, finalIndex);
	}

}
