package bee.creative.array;

/** Diese Klasse implementiert ein {@link ShortArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactShortArray extends CompactArray<short[], Short> implements ShortArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactShortArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactShortArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	public CompactShortArray(ShortArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public short get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, short value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public ShortArraySection section() {
		return new CompactShortArraySection(this);
	}

	/** Dieses Feld speichert das {@code short}-Array. */
	protected short[] array;

	@Override
	protected short[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(short[] array) {
		this.array = array;
	}

	@Override
	protected short[] customNewArray(int length) {
		return new short[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class CompactShortArraySection extends ShortArraySection {

		@Override
		public short[] array() {
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

		final CompactShortArray owner;

		CompactShortArraySection(CompactShortArray owner) {
			this.owner = owner;
		}

	}

}
