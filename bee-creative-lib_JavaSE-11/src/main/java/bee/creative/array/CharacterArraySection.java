package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code char}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class CharacterArraySection extends ArraySection<char[], Character> {

	/** Diese Methode liefert eine {@link CharacterArraySection} zum gegebenen {@code char}-Array. */
	public static CharacterArraySection from(char... array) throws NullPointerException {
		Objects.notNull(array);
		return new CharacterArraySection() {

			@Override
			public char[] array() {
				return array;
			}

			@Override
			public int offset() {
				return 0;
			}

			@Override
			public int length() {
				return array.length;
			}

		};
	}

	/** Diese Methode liefert eine {@link CharacterArraySection} zum gegebenene Abschnitt des gegebenen {@code char}-Arrays. */
	public static CharacterArraySection from(char[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, offset, array.length);
		return new CharacterArraySection() {

			@Override
			public char[] array() {
				return array;
			}

			@Override
			public int offset() {
				return offset;
			}

			@Override
			public int length() {
				return length;
			}

		};
	}

	@Override
	public CharacterArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return CharacterArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof CharacterArraySection)) return false;
		return this.defaultEquals((CharacterArraySection)object);
	}

	@Override
	protected Character customGet(char[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(char[] array, int index, Character value) {
		array[index] = value;
	}

	@Override
	protected int customHash(char[] array, int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(char[] array1, char[] array2, int index1, int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(char[] array1, char[] array2, int index1, int index2) {
		return array1[index1] - array2[index2];
	}

	@Override
	protected void customPrint(char[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

}
