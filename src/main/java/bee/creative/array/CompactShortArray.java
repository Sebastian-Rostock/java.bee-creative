package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link ShortArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactShortArray extends CompactArray<short[], Short> implements ShortArray {

	/** Diese Klasse implementiert ein {@link ShortArray} als modifizierbare Sicht auf einen Teil eines {@link CompactShortArray}.
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

		@Override
		public ShortArraySection section() {
			return new CompactShortSubArraySection(this);
		}

		@Override
		public short get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		@Override
		public void getAll(final int index, final short[] values) {
			this.getAll(index, ShortArraySection.from(values));
		}

		@Override
		public void set(final int index, final short value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		@Override
		public void setAll(final int index, final short[] values) {
			this.setAll(index, ShortArraySection.from(values));
		}

		@Override
		public void add(final short value) {
			this.add(this.size(), value);
		}

		@Override
		public void addAll(final short[] values) {
			this.addAll(this.size(), values);
		}

		@Override
		public void add(final int index, final short value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public void addAll(final int index, final short[] values) {
			this.addAll(this.size(), ShortArraySection.from(values));
		}

		@Override
		public ShortArray subArray(final int fromIndex, final int toIndex) {
			return (ShortArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link ShortArraySection} eines {@link CompactShortArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactShortArraySection extends ShortArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactShortArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactShortArraySection(final CompactShortArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public short[] array() {
			return this.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.from;
		}

		@Override
		public int finalIndex() {
			final CompactShortArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link ShortArraySection} eines {@link CompactShortSubArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactShortSubArraySection extends ShortArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactShortSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactShortSubArraySection(final CompactShortSubArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size();
		}

		@Override
		public short[] array() {
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

	/** Dieses Feld speichert das {@code short}-Array. */
	protected short[] array;

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

	@Override
	protected short[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(final short[] array) {
		this.array = array;
	}

	@Override
	protected short[] customNewArray(final int length) {
		return new short[length];
	}

	@Override
	protected Short customGet(final int index) {
		return Short.valueOf(this.get(index));
	}

	@Override
	protected void customSet(final int index, final Short value) {
		this.set(index, value.shortValue());
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	public short get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void getAll(final int index, final short[] values) {
		this.getAll(index, ShortArraySection.from(values));
	}

	@Override
	public void set(final int index, final short value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public void setAll(final int index, final short[] values) {
		this.setAll(index, ShortArraySection.from(values));
	}

	@Override
	public void add(final short value) {
		this.add(this.size, value);
	}

	@Override
	public void addAll(final short[] values) {
		this.addAll(this.size, values);
	}

	@Override
	public void add(final int index, final short value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	@Override
	public void addAll(final int index, final short[] values) {
		this.addAll(this.size, ShortArraySection.from(values));
	}

	@Override
	public short[] array() {
		return this.array;
	}

	@Override
	public ShortArraySection section() {
		return new CompactShortArraySection(this);
	}

	@Override
	public ShortArray subArray(final int startIndex, final int finalIndex) {
		return new CompactShortSubArray(this, startIndex, finalIndex);
	}

}
