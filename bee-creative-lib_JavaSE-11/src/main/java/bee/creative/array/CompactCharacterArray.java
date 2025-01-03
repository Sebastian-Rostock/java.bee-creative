package bee.creative.array;

/** Diese Klasse implementiert ein {@link CharacterArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactCharacterArray extends CompactArray<char[], Character> implements CharacterArray {

	/** Dieser Konstruktor initialisiert das Array mit {@link #capacity() Kapazität} {@code 0} und {@link #getAlignment() Ausrichtungsposition} {@code 0.5}. */
	public CompactCharacterArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen {@link #capacity() Kapazität} und {@link #getAlignment() Ausrichtungsposition}. */
	public CompactCharacterArray(int capacity, float alignmen) throws IllegalArgumentException {
		super(capacity, alignmen);
	}

	/** Dieser Konstruktor initialisiert Array und Ausrichtung mit den Daten der gegebenen {@link ArraySection}. Als internes Array wird das der gegebenen
	 * {@link ArraySection} verwendet. Als relative Ausrichtungsposition wird {@code 0.5} verwendet.
	 *
	 * @param section {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}. */
	public CompactCharacterArray(CharacterArraySection section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	public char get(int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void set(int index, char value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public CharacterArraySection section() {
		return new Section(this);
	}

	/** Dieses Feld speichert das {@code char}-Array. */
	protected char[] array;

	@Override
	protected char[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(char[] array) {
		this.array = array;
	}

	@Override
	protected char[] customNewArray(int length) {
		return new char[length];
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	static class Section extends CharacterArraySection {

		@Override
		public char[] array() {
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

		final CompactCharacterArray owner;

		Section(CompactCharacterArray owner) {
			this.owner = owner;
		}

	}

}
