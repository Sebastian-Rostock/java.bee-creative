package bee.creative.array;

/** Diese Klasse implementiert ein {@link FloatArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactFloatArray extends CompactArray<float[], Float> implements FloatArray {

	/** Diese Klasse implementiert ein {@link FloatArray} als modifizierbare Sicht auf einen Teil eines {@link CompactFloatArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactFloatSubArray extends CompactSubArray<CompactFloatArray, float[], Float> implements FloatArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactFloatSubArray(final CompactFloatArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public FloatArraySection section() {
			return new CompactFloatSubArraySection(this);
		}

		/** {@inheritDoc} */
		@Override
		public float get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/** {@inheritDoc} */
		@Override
		public void get(final int index, final float[] values) {
			this.get(index, FloatArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final float value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/** {@inheritDoc} */
		@Override
		public void set(final int index, final float[] values) {
			this.set(index, FloatArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public void add(final float value) {
			this.add(this.size(), value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final float[] values) {
			this.add(this.size(), values);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final float value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/** {@inheritDoc} */
		@Override
		public void add(final int index, final float[] values) {
			this.add(this.size(), FloatArraySection.from(values));
		}

		/** {@inheritDoc} */
		@Override
		public FloatArray subArray(final int fromIndex, final int toIndex) {
			return (FloatArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link FloatArraySection} eines {@link CompactFloatArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactFloatArraySection extends FloatArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactFloatArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactFloatArraySection(final CompactFloatArray owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this.owner = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this.owner.size;
		}

		/** {@inheritDoc} */
		@Override
		public float[] array() {
			return this.owner.array;
		}

		/** {@inheritDoc} */
		@Override
		public int startIndex() {
			return this.owner.from;
		}

		/** {@inheritDoc} */
		@Override
		public int finalIndex() {
			final CompactFloatArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link FloatArraySection} eines {@link CompactFloatSubArray}s.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactFloatSubArraySection extends FloatArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactFloatSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactFloatSubArraySection(final CompactFloatSubArray owner) throws NullPointerException {
			if (owner == null) throw new NullPointerException("owner = null");
			this.owner = owner;
		}

		{}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this.owner.size();
		}

		/** {@inheritDoc} */
		@Override
		public float[] array() {
			return this.owner.owner.array;
		}

		/** {@inheritDoc} */
		@Override
		public int startIndex() {
			return this.owner.startIndex;
		}

		/** {@inheritDoc} */
		@Override
		public int finalIndex() {
			return this.owner.finalIndex;
		}

	}

	{}

	/** Dieses Feld speichert das {@code float}-Array. */
	protected float[] array;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactFloatArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactFloatArray(final int capacity) throws IllegalArgumentException {
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
	public CompactFloatArray(final ArraySection<float[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected float[] customGetArray() {
		return this.array;
	}

	/** {@inheritDoc} */
	@Override
	protected void customSetArray(final float[] array) {
		this.array = array;
	}

	/** {@inheritDoc} */
	@Override
	protected float[] customNewArray(final int length) {
		return new float[length];
	}

	/** {@inheritDoc} */
	@Override
	protected Float customGet(final int index) {
		return Float.valueOf(this.get(index));
	}

	/** {@inheritDoc} */
	@Override
	protected void customSet(final int index, final Float value) {
		this.set(index, value.floatValue());
	}

	/** {@inheritDoc} */
	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	/** {@inheritDoc} */
	@Override
	public float get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/** {@inheritDoc} */
	@Override
	public void get(final int index, final float[] values) {
		this.get(index, FloatArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final float value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/** {@inheritDoc} */
	@Override
	public void set(final int index, final float[] values) {
		this.set(index, FloatArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public void add(final float value) {
		this.add(this.size, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final float[] values) {
		this.add(this.size, values);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final float value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final float[] values) {
		this.add(this.size, FloatArraySection.from(values));
	}

	/** {@inheritDoc} */
	@Override
	public float[] array() {
		return this.array;
	}

	/** {@inheritDoc} */
	@Override
	public FloatArraySection section() {
		return new CompactFloatArraySection(this);
	}

	/** {@inheritDoc} */
	@Override
	public FloatArray subArray(final int startIndex, final int finalIndex) {
		return new CompactFloatSubArray(this, startIndex, finalIndex);
	}

}
