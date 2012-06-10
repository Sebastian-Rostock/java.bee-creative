package bee.creative.array;

/**
 * Diese Klasse implementiert ein {@link DoubleArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactDoubleArray extends CompactArray<double[], Double> implements DoubleArray {

	/**
	 * Diese Klasse implementiert ein {@link DoubleArray} als modifizierbare Sicht auf einen Teil eines
	 * {@link CompactDoubleArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactDoubleSubArray extends CompactSubArray<CompactDoubleArray, double[], Double> implements
		DoubleArray {

		/**
		 * Dieser Konstrukteur initialisiert Besitzer und Indices.
		 * 
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder
		 *         {@code finalIndex > owner.size()} oder {@code startIndex > finalIndex}).
		 */
		public CompactDoubleSubArray(final CompactDoubleArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DoubleArraySection section() {
			return new CompactDoubleSubArraySection(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public double get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final double[] values) {
			this.get(index, DoubleArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final double value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final double[] values) {
			this.set(index, DoubleArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final double value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final double[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final double value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final double[] values) {
			this.add(this.size(), DoubleArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DoubleArray subArray(final int fromIndex, final int toIndex) {
			return (DoubleArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link DoubleArraySection} eines {@link CompactDoubleArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactDoubleArraySection extends DoubleArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactDoubleArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactDoubleArraySection(final CompactDoubleArray owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
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
		public double[] array() {
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
			final CompactDoubleArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link DoubleArraySection} eines {@link CompactDoubleSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactDoubleSubArraySection extends DoubleArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactDoubleSubArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactDoubleSubArraySection(final CompactDoubleSubArray owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException();
			this.owner = owner;
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
		public double[] array() {
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

	/**
	 * Dieses Feld speichert das leere {@code double}-Array.
	 */
	protected static final double[] VOID = new double[0];

	/**
	 * Dieses Feld speichert das {@code double}-Array.
	 */
	protected double[] array;

	/**
	 * Dieser Konstrukteur initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition
	 * {@code 0.5}.
	 */
	public CompactDoubleArray() {
		super();
	}

	/**
	 * Dieser Konstrukteur initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition
	 * {@code 0.5}.
	 * 
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
	 */
	public CompactDoubleArray(final int capacity) throws IllegalArgumentException {
		super(capacity);
	}

	/**
	 * Dieser Konstrukteur initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als
	 * internes Array wird das der gegebenen {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird
	 * {@code 0.5} verwendet.
	 * 
	 * @see ArrayData#allocate(int)
	 * @see ArraySection#validate(ArraySection)
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder
	 *         {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}.
	 */
	public CompactDoubleArray(final ArraySection<double[]> section) throws NullPointerException,
		IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] getArray() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setArray(final double[] array) {
		this.array = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double[] newArray(final int length) {
		if(length == 0) return CompactDoubleArray.VOID;
		return new double[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Double getValue(final int index) {
		return Double.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(final int index, final Double value) {
		this.set(index, value.doubleValue());
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
	public double get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final double[] values) {
		this.get(index, DoubleArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final double value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final double[] values) {
		this.set(index, DoubleArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final double value) {
		this.add(this.size, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final double[] values) {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final double value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final double[] values) {
		this.add(this.size, DoubleArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] array() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleArraySection section() {
		return new CompactDoubleArraySection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DoubleArray subArray(final int startIndex, final int finalIndex) {
		return new CompactDoubleSubArray(this, startIndex, finalIndex);
	}

}
