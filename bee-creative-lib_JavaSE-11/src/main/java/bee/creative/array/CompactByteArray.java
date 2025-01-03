package bee.creative.array;

/** Diese Klasse implementiert ein {@link ByteArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactByteArray extends CompactArray<byte[], Byte> implements ByteArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactByteArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactByteArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	public CompactByteArray(ByteArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public byte get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, byte value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public ByteArraySection section() {
		return new Section(this);
	}

	/** Dieses Feld speichert das {@code byte}-Array. */
	protected byte[] array;

	@Override
	protected byte[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(byte[] array) {
		this.array = array;
	}

	@Override
	protected byte[] customNewArray(int length) {
		return new byte[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class Section extends ByteArraySection {

		@Override
		public byte[] array() {
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

		final CompactByteArray owner;

		Section(CompactByteArray owner) {
			this.owner = owner;
		}

	}

}
