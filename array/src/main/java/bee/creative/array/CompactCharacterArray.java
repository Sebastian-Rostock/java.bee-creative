package bee.creative.array;

import java.util.Arrays;

/**
 * Diese Klasse implementiert ein {@link CharacterArray} als {@link CompactArray}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class CompactCharacterArray extends CompactArray<char[], Character> implements CharacterArray {

	/**
	 * Diese Klasse implementiert ein {@link CharacterArray} als modifizierbare Sicht auf einen Teil eines
	 * {@link CompactCharacterArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactCharacterSubArray extends CompactSubArray<CompactCharacterArray, char[], Character> implements
		CharacterArray {

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
		public CompactCharacterSubArray(final CompactCharacterArray owner, final int startIndex, final int finalIndex)
			throws NullPointerException, IndexOutOfBoundsException {
			super(owner, startIndex, finalIndex);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CharacterArraySection section() {
			return new CompactCharacterSubArraySection(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public char get(final int index) {
			return this.owner.get(this.ownerIndex(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void get(final int index, final char[] values) {
			this.get(index, CharacterArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final char value) {
			this.owner.set(this.ownerIndex(index), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final int index, final char[] values) {
			this.set(index, CharacterArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final char value) {
			this.add(this.size(), value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final char[] values) {
			this.add(this.size(), values);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final char value) {
			this.insert(index, 1);
			this.set(index, value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final char[] values) {
			this.add(this.size(), CharacterArraySection.from(values));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CharacterArray subArray(final int fromIndex, final int toIndex) {
			return (CharacterArray)this.ownerSubArray(fromIndex, toIndex);
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link CharacterArraySection} eines {@link CompactCharacterArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactCharacterArraySection extends CharacterArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactCharacterArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactCharacterArraySection(final CompactCharacterArray owner) throws NullPointerException {
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
		public char[] array() {
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
			final CompactCharacterArray owner = this.owner;
			return owner.from + owner.size;
		}

	}

	/**
	 * Diese Klasse implementiert die live {@link CharacterArraySection} eines {@link CompactCharacterSubArray}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	protected static class CompactCharacterSubArraySection extends CharacterArraySection {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		protected final CompactCharacterSubArray owner;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CompactCharacterSubArraySection(final CompactCharacterSubArray owner) throws NullPointerException {
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
		public char[] array() {
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
	 * Dieses Feld speichert das leere {@code char}-Array.
	 */
	protected static final char[] VOID = new char[0];

	/**
	 * Dieses Feld speichert das {@code char}-Array.
	 */
	protected char[] array;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected char[] getArray() {
		return this.array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setArray(final char[] array) {
		this.array = array;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected char[] newArray(final int length) {
		if(length == 0) return CompactCharacterArray.VOID;
		return new char[length];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void clearArray(final char[] array, final int fromIndex, final int toIndex) {
		Arrays.fill(array, fromIndex, toIndex, (char)0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Character getValue(final int index) {
		return Character.valueOf(this.get(index));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(final int index, final Character value) {
		this.set(index, value.charValue());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getLength(final char[] array) {
		return array.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char get(final int index) {
		return this.array[this.inclusiveIndex(index)];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int index, final char[] values) {
		this.get(index, CharacterArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final char value) {
		this.array[this.inclusiveIndex(index)] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(final int index, final char[] values) {
		this.set(index, CharacterArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final char value) {
		this.add(this.size, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final char[] values) {
		this.add(this.size, values);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final char value) {
		this.exclusiveIndex(index);
		this.insert(index, 1);
		this.set(this.from + index, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final int index, final char[] values) {
		this.add(this.size, CharacterArraySection.from(values));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterArraySection section() {
		return new CompactCharacterArraySection(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterArray subArray(final int startIndex, final int finalIndex) {
		return new CompactCharacterSubArray(this, startIndex, finalIndex);
	}

}
