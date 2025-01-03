package bee.creative.array;

/** Diese Klasse implementiert ein {@link FloatArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactFloatArray extends CompactArray<float[], Float> implements FloatArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactFloatArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactFloatArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	public CompactFloatArray(FloatArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public float get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, float value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public FloatArraySection section() {
		return new Section(this);
	}

	/** Dieses Feld speichert das {@code float}-Array. */
	protected float[] array;

	@Override
	protected float[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(float[] array) {
		this.array = array;
	}

	@Override
	protected float[] customNewArray(int length) {
		return new float[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class Section extends FloatArraySection {

		@Override
		public float[] array() {
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

		final CompactFloatArray owner;

		Section(CompactFloatArray owner) {
			this.owner = owner;
		}

	}

}
