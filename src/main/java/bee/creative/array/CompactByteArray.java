package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link ByteArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactByteArray extends CompactArray<byte[], Byte> implements ByteArray {

	/** Diese Klasse implementiert ein {@link ByteArray} als modifizierbare Sicht auf einen Teil eines {@link CompactByteArray}.
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
		public CompactByteSubArray(final CompactByteArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		@Override
		public ByteArraySection section() {
			return new CompactByteSubArraySection(this);
		}

		@Override
		public byte get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		@Override
		public void getAll(final int index, final byte[] values) {
			this.getAll(index, ByteArraySection.from(values));
		}

		@Override
		public void set(final int index, final byte value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		@Override
		public void setAll(final int index, final byte[] values) {
			this.setAll(index, ByteArraySection.from(values));
		}

		@Override
		public void add(final byte value) {
			this.add(this.size(), value);
		}

		@Override
		public void addAll(final byte[] values) {
			this.addAll(this.size(), values);
		}

		@Override
		public void add(final int index, final byte value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public void addAll(final int index, final byte[] values) {
			this.addAll(this.size(), ByteArraySection.from(values));
		}

		@Override
		public ByteArray subArray(final int fromIndex, final int toIndex) {
			return (ByteArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link ByteArraySection} eines {@link CompactByteArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactByteArraySection extends ByteArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactByteArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactByteArraySection(final CompactByteArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public byte[] array() {
			return this.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.from;
		}

		@Override
		public int finalIndex() {
			final CompactByteArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link ByteArraySection} eines {@link CompactByteSubArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactByteSubArraySection extends ByteArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactByteSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactByteSubArraySection(final CompactByteSubArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size();
		}

		@Override
		public byte[] array() {
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

	/** Dieses Feld speichert das {@code byte}-Array. */
	protected byte[] array;

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

	@Override
	protected byte[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(final byte[] array) {
		this.array = array;
	}

	@Override
	protected byte[] customNewArray(final int length) {
		return new byte[length];
	}

	@Override
	protected Byte customGet(final int index) {
		return Byte.valueOf(this.get(index));
	}

	@Override
	protected void customSet(final int index, final Byte value) {
		this.set(index, value.byteValue());
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	public byte get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void getAll(final int index, final byte[] values) {
		this.getAll(index, ByteArraySection.from(values));
	}

	@Override
	public void set(final int index, final byte value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public void setAll(final int index, final byte[] values) {
		this.setAll(index, ByteArraySection.from(values));
	}

	@Override
	public void add(final byte value) {
		this.add(this.size, value);
	}

	@Override
	public void addAll(final byte[] values) {
		this.addAll(this.size, values);
	}

	@Override
	public void add(final int index, final byte value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	@Override
	public void addAll(final int index, final byte[] values) {
		this.addAll(this.size, ByteArraySection.from(values));
	}

	@Override
	public byte[] array() {
		return this.array;
	}

	@Override
	public ByteArraySection section() {
		return new CompactByteArraySection(this);
	}

	@Override
	public ByteArray subArray(final int startIndex, final int finalIndex) {
		return new CompactByteSubArray(this, startIndex, finalIndex);
	}

}
