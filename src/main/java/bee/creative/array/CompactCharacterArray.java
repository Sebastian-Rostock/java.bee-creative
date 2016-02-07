package bee.creative.array;

/**
 * Diese Klasse implementiert ein {@link CharacterArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactCharacterArray extends CompactArray<char[], Character> implements CharacterArray {

	/**
	 * Diese Klasse implementiert ein {@link CharacterArray} als modifizierbare Sicht auf einen Teil eines {@link CompactCharacterArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactCharacterSubArray extends CompactSubArray<CompactCharacterArray, char[], Character> implements CharacterArray {

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
		public CompactCharacterSubArray(final CompactCharacterArray owner, final int startIndex, final int finalIndex) throws NullPointerException,
			IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CharacterArraySection section() {
			return new CompactCharacterSubArraySection(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public char get(final int index) {
			return this._owner_.get(this._ownerIndex_(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final char[] values) {
			this.get(index, CharacterArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final char value) {
			this._owner_.set(this._ownerIndex_(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final char[] values) {
			this.set(index, CharacterArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final char value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final char[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final char value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final char[] values) {
			this.add(this.size(), CharacterArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CharacterArray subArray(final int fromIndex, final int toIndex) {
			return (CharacterArray)this._ownerSubArray_(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link CharacterArraySection} eines {@link CompactCharacterArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactCharacterArraySection extends CharacterArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactCharacterArray _owner_;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactCharacterArraySection(final CompactCharacterArray owner) throws NullPointerException {
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
		public char[] array() {
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
			final CompactCharacterArray owner = this._owner_;
			return owner._from_ + owner._size_;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link CharacterArraySection} eines {@link CompactCharacterSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactCharacterSubArraySection extends CharacterArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactCharacterSubArray _owner_;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactCharacterSubArraySection(final CompactCharacterSubArray owner) throws NullPointerException {
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
		public char[] array() {
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
	 * Dieses Feld speichert das {@code char}-Array.
	 */
	protected char[] _array_;

	/**
	 * Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}.
	 */
	public CompactCharacterArray() {
		super();
	}

	/**
	 * Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactCharacterArray(final int capacity) throws IllegalArgumentException {
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
	public CompactCharacterArray(final ArraySection<char[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected char[] _array_() {
		return this._array_;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _array_(final char[] array) {
		this._array_ = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected char[] _allocArray_(final int length) {
		return new char[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Character _value_(final int index) {
		return Character.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _value_(final int index, final Character value) {
		this.set(index, value.charValue());
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
	public char get(final int index) {
		return this._array_[this._inclusiveIndex_(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final char[] values) {
		this.get(index, CharacterArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final char value) {
		this._array_[this._inclusiveIndex_(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final char[] values) {
		this.set(index, CharacterArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final char value) {
		this.add(this._size_, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final char[] values) {
		this.add(this._size_, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final char value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final char[] values) {
		this.add(this._size_, CharacterArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char[] array() {
		return this._array_;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterArraySection section() {
		return new CompactCharacterArraySection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterArray subArray(final int startIndex, final int finalIndex) {
		return new CompactCharacterSubArray(this, startIndex, finalIndex);
	}

}
