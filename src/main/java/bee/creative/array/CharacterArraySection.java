package bee.creative.array;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code char}-Arrays.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class CharacterArraySection extends ArraySection<char[]> {

	/** Diese Methode erzeugt eine neue {@link CharacterArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * 
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
	public static CharacterArraySection from(final char[] array, final int startIndex, final int finalIndex) throws NullPointerException,
		IndexOutOfBoundsException, IllegalArgumentException {
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

	{}

	/** {@inheritDoc} */
	@Override
	protected int _arrayLength_(final char[] array) {
		return array.length;
	}

	/** {@inheritDoc} */
	@Override
	protected int _hashCode_(final char[] array, final int index) {
		return array[index];
	}

	/** {@inheritDoc} */
	@Override
	protected boolean _equals_(final char[] array1, final char[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	/** {@inheritDoc} */
	@Override
	protected int _compareTo_(final char[] array1, final char[] array2, final int index1, final int index2) {
		return array1[index1] - array2[index2];
	}

	/** {@inheritDoc} */
	@Override
	protected void _toString_(final char[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof CharacterArraySection)) return false;
		return this._equals_((CharacterArraySection)object);
	}

}
