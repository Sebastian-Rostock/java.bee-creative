package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code char}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class CharacterArraySection extends ArraySection<char[]> {

	/** Diese Methode erzeugt eine neue {@link CharacterArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>CharacterArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param array Array.
	 * @return {@link CharacterArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static CharacterArraySection from(final char... array) throws NullPointerException {
		return ArraySection.validate(new CharacterArraySection() {

			@Override
			public char[] array() {
				return array;
			}

			@Override
			public int startIndex() {
				return 0;
			}

			@Override
			public int finalIndex() {
				return array.length;
			}

		});
	}

	/** Diese Methode erzeugt eine neue {@link CharacterArraySection} und gibt sie zurück.
	 *
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link CharacterArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static CharacterArraySection from(final char[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new CharacterArraySection() {

			@Override
			public char[] array() {
				return array;
			}

			@Override
			public int startIndex() {
				return startIndex;
			}

			@Override
			public int finalIndex() {
				return finalIndex;
			}

		});
	}

	@Override
	protected int customLength(final char[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final char[] array, final int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(final char[] array1, final char[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(final char[] array1, final char[] array2, final int index1, final int index2) {
		return array1[index1] - array2[index2];
	}

	@Override
	protected void customPrint(final char[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link CharacterArraySection} als {@link List} zurück und ist eine Abkürzung für
	 * {@code new CompactCharacterArray(this).values()}.
	 *
	 * @return {@link Character}-{@link List}. */
	public List<Character> asList() {
		return new CompactCharacterArray(this).values();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof CharacterArraySection)) return false;
		return this.defaultEquals((CharacterArraySection)object);
	}

}
