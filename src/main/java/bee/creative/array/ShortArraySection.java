package bee.creative.array;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code short}-Arrays.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class ShortArraySection extends ArraySection<short[]> {

	/** Diese Methode erzeugt eine neue {@link ShortArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * 
	 * <pre>ShortArraySection.from(array, 0, array.length)</pre>
	 * 
	 * @param array Array.
	 * @return {@link ShortArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static ShortArraySection from(final short... array) throws NullPointerException {
		return ArraySection.validate(new ShortArraySection() {

			@Override
			public short[] array() {
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

	/** Diese Methode erzeugt eine neue {@link ShortArraySection} und gibt sie zurück.
	 * 
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link ShortArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static ShortArraySection from(final short[] array, final int startIndex, final int finalIndex) throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException {
		return ArraySection.validate(new ShortArraySection() {

			@Override
			public short[] array() {
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
	protected int _arrayLength_(final short[] array) {
		return array.length;
	}

	/** {@inheritDoc} */
	@Override
	protected int _hashCode_(final short[] array, final int index) {
		return array[index];
	}

	/** {@inheritDoc} */
	@Override
	protected boolean _equals_(final short[] array1, final short[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	/** {@inheritDoc} */
	@Override
	protected int _compareTo_(final short[] array1, final short[] array2, final int index1, final int index2) {
		return array1[index1] - array2[index2];
	}

	/** {@inheritDoc} */
	@Override
	protected void _toString_(final short[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof ShortArraySection)) return false;
		return this._equals_((ShortArraySection)object);
	}

}
