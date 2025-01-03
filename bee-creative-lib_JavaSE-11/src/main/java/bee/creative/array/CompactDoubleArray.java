package bee.creative.array;

/** Diese Klasse implementiert ein {@link DoubleArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactDoubleArray extends CompactArray<double[], Double> implements DoubleArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactDoubleArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactDoubleArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	public CompactDoubleArray(DoubleArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public double get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, double value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public DoubleArraySection section() {
		return new Section(this);
	}

	/** Dieses Feld speichert das {@code double}-Array. */
	protected double[] array;

	@Override
	protected double[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(double[] array) {
		this.array = array;
	}

	@Override
	protected double[] customNewArray(int length) {
		return new double[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class Section extends DoubleArraySection {

		@Override
		public double[] array() {
			return this.owner.array;
		}

		@Override
		public int offset() {
			return this.owner.from;
		}

		@Override
		public int length() {
			return this.owner.size;
		}

		final CompactDoubleArray owner;

		Section(CompactDoubleArray owner) {
			this.owner = owner;
		}

	}

}
