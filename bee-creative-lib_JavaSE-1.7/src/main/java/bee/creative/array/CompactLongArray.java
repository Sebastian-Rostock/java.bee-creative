package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link LongArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactLongArray extends CompactArray<long[], Long> implements LongArray {

	/** Diese Klasse implementiert ein {@link LongArray} als modifizierbare Sicht auf einen Teil eines {@link CompactLongArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactLongSubArray extends CompactSubArray<CompactLongArray, long[], Long> implements LongArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactLongSubArray(final CompactLongArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		@Override
		public LongArraySection section() {
			return new CompactLongSubArraySection(this);
		}

		@Override
		public long get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		@Override
		public void getAll(final int index, final long[] values) {
			this.getAll(index, LongArraySection.from(values));
		}

		@Override
		public void set(final int index, final long value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		@Override
		public void setAll(final int index, final long[] values) {
			this.setAll(index, LongArraySection.from(values));
		}

		@Override
		public void add(final long value) {
			this.add(this.size(), value);
		}

		@Override
		public void addAll(final long[] values) {
			this.addAll(this.size(), values);
		}

		@Override
		public void add(final int index, final long value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public void addAll(final int index, final long[] values) {
			this.addAll(this.size(), LongArraySection.from(values));
		}

		@Override
		public LongArray subArray(final int fromIndex, final int toIndex) {
			return (LongArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link LongArraySection} eines {@link CompactLongArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactLongArraySection extends LongArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactLongArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactLongArraySection(final CompactLongArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public long[] array() {
			return this.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.from;
		}

		@Override
		public int finalIndex() {
			final CompactLongArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link LongArraySection} eines {@link CompactLongSubArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactLongSubArraySection extends LongArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactLongSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactLongSubArraySection(final CompactLongSubArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size();
		}

		@Override
		public long[] array() {
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

	/** Dieses Feld speichert das {@code long}-Array. */
	protected long[] array;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactLongArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactLongArray(final int capacity) throws IllegalArgumentException {
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
	public CompactLongArray(final ArraySection<long[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	protected long[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(final long[] array) {
		this.array = array;
	}

	@Override
	protected long[] customNewArray(final int length) {
		return new long[length];
	}

	@Override
	protected Long customGet(final int index) {
		return Long.valueOf(this.get(index));
	}

	@Override
	protected void customSet(final int index, final Long value) {
		this.set(index, value.longValue());
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	public long get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void getAll(final int index, final long[] values) {
		this.getAll(index, LongArraySection.from(values));
	}

	@Override
	public void set(final int index, final long value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public void setAll(final int index, final long[] values) {
		this.setAll(index, LongArraySection.from(values));
	}

	@Override
	public void add(final long value) {
		this.add(this.size, value);
	}

	@Override
	public void addAll(final long[] values) {
		this.addAll(this.size, values);
	}

	@Override
	public void add(final int index, final long value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	@Override
	public void addAll(final int index, final long[] values) {
		this.addAll(this.size, LongArraySection.from(values));
	}

	@Override
	public long[] array() {
		return this.array;
	}

	@Override
	public LongArraySection section() {
		return new CompactLongArraySection(this);
	}

	@Override
	public LongArray subArray(final int startIndex, final int finalIndex) {
		return new CompactLongSubArray(this, startIndex, finalIndex);
	}

}
