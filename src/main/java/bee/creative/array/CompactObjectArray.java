package bee.creative.array;

import java.util.Comparator;
import bee.creative.util.Comparators;

/**
 * Diese Klasse implementiert ein {@link ObjectArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der {@link Object}s.
 */
public abstract class CompactObjectArray<GValue> extends CompactArray<GValue[], GValue> implements ObjectArray<GValue>, Comparator<GValue> {

	/**
	 * Diese Klasse implementiert ein {@link ObjectArray} als modifizierbare Sicht auf einen Teil eines {@link CompactObjectArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der {@link Object}s.
	 */
	protected static class CompactObjectSubArray<GValue> extends CompactSubArray<CompactObjectArray<GValue>, GValue[], GValue> implements ObjectArray<GValue> {

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
		public CompactObjectSubArray(final CompactObjectArray<GValue> owner, final int startIndex, final int finalIndex) throws NullPointerException,
			IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectArraySection<GValue> section() {
			return new CompactObjectSubArraySection<GValue>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final int index) {
			return this._owner_.get(this._ownerIndex_(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final GValue[] values) {
			this.get(index, ObjectArraySection.from(this._owner_, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final GValue value) {
			this._owner_.set(this._ownerIndex_(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final GValue[] values) {
			this.set(index, ObjectArraySection.from(this._owner_, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final GValue value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final GValue[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GValue value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GValue[] values) {
			this.add(this.size(), ObjectArraySection.from(this._owner_, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectArray<GValue> subArray(final int fromIndex, final int toIndex) {
			return (ObjectArray<GValue>)this._ownerSubArray_(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link ObjectArraySection} eines {@link CompactObjectArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der {@link Object}s.
	 */
	protected static class CompactObjectArraySection<GValue> extends ObjectArraySection<GValue> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactObjectArray<GValue> _owner_;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactObjectArraySection(final CompactObjectArray<GValue> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int _compareTo_(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
			return Comparators.compare(array1[index1], array2[index2], this._owner_);
		}

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
		public GValue[] array() {
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
			final CompactObjectArray<GValue> owner = this._owner_;
			return owner._from_ + owner._size_;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link ObjectArraySection} eines {@link CompactObjectArray.CompactObjectSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der {@link Object}s.
	 */
	protected static class CompactObjectSubArraySection<GValue> extends ObjectArraySection<GValue> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactObjectSubArray<GValue> _owner_;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactObjectSubArraySection(final CompactObjectSubArray<GValue> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this._owner_ = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int _compareTo_(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
			return Comparators.compare(array1[index1], array2[index2], this._owner_._owner_);
		}

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
		public GValue[] array() {
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
	 * Dieses Feld speichert das {@code GValue}-Array.
	 */
	protected GValue[] _array_;

	/**
	 * Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}.
	 */
	public CompactObjectArray() {
		super();
	}

	/**
	 * Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactObjectArray(final int capacity) throws IllegalArgumentException {
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
	public CompactObjectArray(final ArraySection<GValue[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GValue[] _array_() {
		return this._array_;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _array_(final GValue[] array) {
		this._array_ = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GValue _value_(final int index) {
		return this.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _value_(final int index, final GValue value) {
		this.set(index, value);
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
	public GValue get(final int index) {
		return this._array_[this._inclusiveIndex_(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final GValue[] values) {
		this.get(index, ObjectArraySection.from(this, values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final GValue value) {
		this._array_[this._inclusiveIndex_(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final GValue[] values) {
		this.set(index, ObjectArraySection.from(this, values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final GValue value) {
		this.add(this._size_, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final GValue[] values) {
		this.add(this._size_, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final GValue value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final GValue[] values) {
		this.add(this._size_, ObjectArraySection.from(this, values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue[] array() {
		return this._array_;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectArraySection<GValue> section() {
		return new CompactObjectArraySection<GValue>(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectArray<GValue> subArray(final int startIndex, final int finalIndex) {
		return new CompactObjectSubArray<GValue>(this, startIndex, finalIndex);
	}

}
