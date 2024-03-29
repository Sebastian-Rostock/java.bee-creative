package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link BooleanArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactBooleanArray extends CompactArray<boolean[], Boolean> implements BooleanArray {

	/** Diese Klasse implementiert ein {@link BooleanArray} als modifizierbare Sicht auf einen Teil eines {@link CompactBooleanArray}. */
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

		@Override
		public BooleanArraySection section() {
			return new CompactBooleanSubArraySection(this);
		}

		@Override
		public boolean get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		@Override
		public void getAll(final int index, final boolean[] values) {
			this.getAll(index, BooleanArraySection.from(values));
		}

		@Override
		public void set(final int index, final boolean value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		@Override
		public void setAll(final int index, final boolean[] values) {
			this.setAll(index, BooleanArraySection.from(values));
		}

		@Override
		public void add(final boolean value) {
			this.add(this.size(), value);
		}

		@Override
		public void add(final boolean[] values) {
			this.add(this.size(), values);
		}

		@Override
		public void add(final int index, final boolean value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public void add(final int index, final boolean[] values) {
			this.addAll(this.size(), BooleanArraySection.from(values));
		}

		@Override
		public BooleanArray subArray(final int fromIndex, final int toIndex) {
			return (BooleanArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link BooleanArraySection} eines {@link CompactBooleanArray}. */
	protected static class CompactBooleanArraySection extends BooleanArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactBooleanArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactBooleanArraySection(final CompactBooleanArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public boolean[] array() {
			return this.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.from;
		}

		@Override
		public int finalIndex() {
			final CompactBooleanArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link BooleanArraySection} eines {@link CompactBooleanSubArray}. */
	protected static class CompactBooleanSubArraySection extends BooleanArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactBooleanSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactBooleanSubArraySection(final CompactBooleanSubArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size();
		}

		@Override
		public boolean[] array() {
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

	/** Dieses Feld speichert das {@code boolean}-Array. */
	protected boolean[] array;

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

	@Override
	protected boolean[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(final boolean[] array) {
		this.array = array;
	}

	@Override
	protected boolean[] customNewArray(final int length) {
		return new boolean[length];
	}

	@Override
	protected Boolean customGet(final int index) {
		return Boolean.valueOf(this.get(index));
	}

	@Override
	protected void customSet(final int index, final Boolean value) {
		this.set(index, value.booleanValue());
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	public boolean get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void getAll(final int index, final boolean[] values) {
		this.getAll(index, BooleanArraySection.from(values));
	}

	@Override
	public void set(final int index, final boolean value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public void setAll(final int index, final boolean[] values) {
		this.setAll(index, BooleanArraySection.from(values));
	}

	@Override
	public void add(final boolean value) {
		this.add(this.size, value);
	}

	@Override
	public void add(final boolean[] values) {
		this.add(this.size, values);
	}

	@Override
	public void add(final int index, final boolean value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	@Override
	public void add(final int index, final boolean[] values) {
		this.addAll(this.size, BooleanArraySection.from(values));
	}

	@Override
	public boolean[] array() {
		return this.array;
	}

	@Override
	public BooleanArraySection section() {
		return new CompactBooleanArraySection(this);
	}

	@Override
	public BooleanArray subArray(final int startIndex, final int finalIndex) {
		return new CompactBooleanSubArray(this, startIndex, finalIndex);
	}

}
