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
			return this.owner.get(this.ownerIndex(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final GValue[] values) {
			this.get(index, ObjectArraySection.from(this.owner, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final GValue value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final GValue[] values) {
			this.set(index, ObjectArraySection.from(this.owner, values));
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
			this.add(this.size(), ObjectArraySection.from(this.owner, values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ObjectArray<GValue> subArray(final int fromIndex, final int toIndex) {
			return (ObjectArray<GValue>)this.ownerSubArray(fromIndex, toIndex);
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
		protected final CompactObjectArray<GValue> owner;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactObjectArraySection(final CompactObjectArray<GValue> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this.owner = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compareTo(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
			return Comparators.compare(array1[index1], array2[index2], this.owner);
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
		public GValue[] array() {
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
			final CompactObjectArray<GValue> owner = this.owner;
			return owner.from + owner.size;
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
		protected final CompactObjectSubArray<GValue> owner;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactObjectSubArraySection(final CompactObjectSubArray<GValue> owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this.owner = owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compareTo(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
			return Comparators.compare(array1[index1], array2[index2], this.owner.owner);
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
		public GValue[] array() {
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

	{}

	/**
	 * Dieses Feld speichert das {@code GValue}-Array.
	 */
	protected GValue[] array;

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
	protected GValue[] getArray() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setArray(final GValue[] array) {
		this.array = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected GValue getValue(final int index) {
		return this.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(final int index, final GValue value) {
		this.set(index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getArrayLength() {
		return this.array.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue get(final int index) {
		return this.array[this.inclusiveIndex(index)];
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
		this.array[this.inclusiveIndex(index)] = value;
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
		this.add(this.size, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final GValue[] values) {
		this.add(this.size, values);
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
		this.add(this.size, ObjectArraySection.from(this, values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GValue[] array() {
		return this.array;
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
