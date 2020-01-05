package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert ein {@link CharacterArray} als {@link CompactArray}.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class CompactCharacterArray extends CompactArray<char[], Character> implements CharacterArray {

	/** Diese Klasse implementiert ein {@link CharacterArray} als modifizierbare Sicht auf einen Teil eines {@link CompactCharacterArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactCharacterSubArray extends CompactSubArray<CompactCharacterArray, char[], Character> implements CharacterArray {

		/** Dieser Konstruktor initialisiert Besitzer und Indices.
		 *
		 * @param owner Besitzer.
		 * @param startIndex Index des ersten Werts im Teil-{@link Array}.
		 * @param finalIndex Index des ersten Werts nach dem Teil-{@link Array}.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn die gegebenen Indices ungültig sind ({@code startIndex < 0} oder {@code finalIndex > owner.size()} oder
		 *         {@code startIndex > finalIndex}). */
		public CompactCharacterSubArray(final CompactCharacterArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		@Override
		public CharacterArraySection section() {
			return new CompactCharacterSubArraySection(this);
		}

		@Override
		public char get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		@Override
		public void get(final int index, final char[] values) {
			this.getAll(index, CharacterArraySection.from(values));
		}

		@Override
		public void set(final int index, final char value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		@Override
		public void set(final int index, final char[] values) {
			this.setAll(index, CharacterArraySection.from(values));
		}

		@Override
		public void add(final char value) {
			this.add(this.size(), value);
		}

		@Override
		public void add(final char[] values) {
			this.add(this.size(), values);
		}

		@Override
		public void add(final int index, final char value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		@Override
		public void add(final int index, final char[] values) {
			this.addAll(this.size(), CharacterArraySection.from(values));
		}

		@Override
		public CharacterArray subArray(final int fromIndex, final int toIndex) {
			return (CharacterArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/** Diese Klasse implementiert die live {@link CharacterArraySection} eines {@link CompactCharacterArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactCharacterArraySection extends CharacterArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactCharacterArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactCharacterArraySection(final CompactCharacterArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size;
		}

		@Override
		public char[] array() {
			return this.owner.array;
		}

		@Override
		public int startIndex() {
			return this.owner.from;
		}

		@Override
		public int finalIndex() {
			final CompactCharacterArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/** Diese Klasse implementiert die live {@link CharacterArraySection} eines {@link CompactCharacterSubArray}.
	 *
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	protected static class CompactCharacterSubArraySection extends CharacterArraySection {

		/** Dieses Feld speichert den Besitzer. */
		protected final CompactCharacterSubArray owner;

		/** Dieser Konstruktor initialisiert den Besitzer.
		 *
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist. */
		public CompactCharacterSubArraySection(final CompactCharacterSubArray owner) throws NullPointerException {
			this.owner = Objects.notNull(owner);
		}

		@Override
		public int size() {
			return this.owner.size();
		}

		@Override
		public char[] array() {
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

	/** Dieses Feld speichert das {@code char}-Array. */
	protected char[] array;

	/** Dieser Konstruktor initialisiert das Array mit der Kapazität {@code 0} und der relativen Ausrichtungsposition {@code 0.5}. */
	public CompactCharacterArray() {
		super();
	}

	/** Dieser Konstruktor initialisiert das Array mit der gegebenen Kapazität und der relativen Ausrichtungsposition {@code 0.5}.
	 *
	 * @see ArrayData#allocate(int)
	 * @param capacity Kapazität.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public CompactCharacterArray(final int capacity) throws IllegalArgumentException {
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
	public CompactCharacterArray(final ArraySection<char[]> section) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		super(section);
	}

	@Override
	protected char[] customGetArray() {
		return this.array;
	}

	@Override
	protected void customSetArray(final char[] array) {
		this.array = array;
	}

	@Override
	protected char[] customNewArray(final int length) {
		return new char[length];
	}

	@Override
	protected Character customGet(final int index) {
		return Character.valueOf(this.get(index));
	}

	@Override
	protected void customSet(final int index, final Character value) {
		this.set(index, value.charValue());
	}

	@Override
	protected int customGetCapacity() {
		return this.array.length;
	}

	@Override
	public char get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	@Override
	public void get(final int index, final char[] values) {
		this.getAll(index, CharacterArraySection.from(values));
	}

	@Override
	public void set(final int index, final char value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	@Override
	public void set(final int index, final char[] values) {
		this.setAll(index, CharacterArraySection.from(values));
	}

	@Override
	public void add(final char value) {
		this.add(this.size, value);
	}

	@Override
	public void add(final char[] values) {
		this.add(this.size, values);
	}

	@Override
	public void add(final int index, final char value) {
		this.insert(index, 1);
		this.set(index, value);
	}

	@Override
	public void add(final int index, final char[] values) {
		this.addAll(this.size, CharacterArraySection.from(values));
	}

	@Override
	public char[] array() {
		return this.array;
	}

	@Override
	public CharacterArraySection section() {
		return new CompactCharacterArraySection(this);
	}

	@Override
	public CharacterArray subArray(final int startIndex, final int finalIndex) {
		return new CompactCharacterSubArray(this, startIndex, finalIndex);
	}

}
