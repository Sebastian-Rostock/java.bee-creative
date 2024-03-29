package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link IntegerArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactIntegerArray extends CompactArray<int[], Integer> implements IntegerArray {

	/** Diese Klasse implementiert ein {@link IntegerArray} als modifizierbare Sicht auf einen Teil eines {@link CompactIntegerArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactIntegerSubArray extends CompactSubArray<CompactIntegerArray, int[], Integer> implements IntegerArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactIntegerSubArray(final CompactIntegerArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		@Override
		public IntegerArraySection section() {
			return new CompactIntegerSubArraySection(this);
		}

		@Override
		public int get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		@Override
		public void getAll(final int index, final int[] values) {
			this.getAll(index, IntegerArraySection.from(values));
		}

		@Override
		public void set(final int index, final int value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		@Override
		public void setAll(final int index, final int[] values) {
			this.setAll(index, IntegerArraySection.from(values));
		}

		@Override
		public void add(final int value) {
			this.add(this.size(), value);
		}

		@Override
		public void addAll(final int[] values) {
			this.addAll(this.size(), values);
		}

		@Override
		public void add(final int index, final int value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public void addAll(final int index, final int[] values) {
			this.addAll(this.size(), IntegerArraySection.from(values));
		}

		@Override
		public IntegerArray subArray(final int fromIndex, final int toIndex) {
			return (IntegerArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link IntegerArraySection} eines {@link CompactIntegerArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactIntegerArraySection extends IntegerArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactIntegerArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactIntegerArraySection(final CompactIntegerArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public int[] array() {
			return this.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.from;
		}

		@Override
		public int finalIndex() {
			final CompactIntegerArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link IntegerArraySection} eines {@link CompactIntegerSubArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactIntegerSubArraySection extends IntegerArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactIntegerSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactIntegerSubArraySection(final CompactIntegerSubArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size();
		}

		@Override
		public int[] array() {
			return this.owner.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.startIndex;
		}

		@Override
		public int finalIndex() {
			return this.owner.finalIndex;
		}

	}

	/** Dieses Feld speichert das {@code int}-Array. */
	protected int[] array;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactIntegerArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactIntegerArray(final int capacity) throws IllegalArgumentException {
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
	public CompactIntegerArray(final ArraySection<int[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	protected int[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(final int[] array) {
		this.array = array;
	}

	@Override
	protected int[] customNewArray(final int length) {
		return new int[length];
	}

	@Override
	protected Integer customGet(final int index) {
		return Integer.valueOf(this.get(index));
	}

	@Override
	protected void customSet(final int index, final Integer value) {
		this.set(index, value.intValue());
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	public int get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void getAll(final int index, final int[] values) {
		this.getAll(index, IntegerArraySection.from(values));
	}

	@Override
	public void set(final int index, final int value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public void setAll(final int index, final int[] values) {
		this.setAll(index, IntegerArraySection.from(values));
	}

	@Override
	public void add(final int value) {
		this.add(this.size, value);
	}

	@Override
	public void addAll(final int[] values) {
		this.addAll(this.size, values);
	}

	@Override
	public void add(final int index, final int value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	@Override
	public void addAll(final int index, final int[] values) {
		this.addAll(this.size, IntegerArraySection.from(values));
	}

	@Override
	public int[] array() {
		return this.array;
	}

	@Override
	public IntegerArraySection section() {
		return new CompactIntegerArraySection(this);
	}

	@Override
	public IntegerArray subArray(final int startIndex, final int finalIndex) {
		return new CompactIntegerSubArray(this, startIndex, finalIndex);
	}

}
