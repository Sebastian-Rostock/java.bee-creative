package bee.creative.array;

/** Diese Klasse implementiert ein {@link LongArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactLongArray extends CompactArray<long[], Long> implements LongArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactLongArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactLongArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	public CompactLongArray(LongArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public long get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, long value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public LongArraySection section() {
		return new Section(this);
	}

	/** Dieses Feld speichert das {@code long}-Array. */
	protected long[] array;

	@Override
	protected long[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(long[] array) {
		this.array = array;
	}

	@Override
	protected long[] customNewArray(int length) {
		return new long[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class Section extends LongArraySection {

		@Override
		public long[] array() {
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

		final CompactLongArray owner;

		Section(CompactLongArray owner) {
			this.owner = owner;
		}

	}

}
