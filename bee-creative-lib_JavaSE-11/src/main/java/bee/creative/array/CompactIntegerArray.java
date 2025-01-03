package bee.creative.array;

/** Diese Klasse implementiert ein {@link IntegerArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactIntegerArray extends CompactArray<int[], Integer> implements IntegerArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactIntegerArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactIntegerArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	public CompactIntegerArray(IntegerArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public int get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, int value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public IntegerArraySection section() {
		return new Section(this);
	}

	/** Dieses Feld speichert das {@code int}-Array. */
	protected int[] array;

	@Override
	protected int[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(int[] array) {
		this.array = array;
	}

	@Override
	protected int[] customNewArray(int length) {
		return new int[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class Section extends IntegerArraySection {

		@Override
		public int[] array() {
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

		final CompactIntegerArray owner;

		Section(CompactIntegerArray owner) {
			this.owner = owner;
		}

	}

}
