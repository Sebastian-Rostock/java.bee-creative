package bee.creative.array;

/**
 * Diese Klasse implementiert eine {@link ArraySection} für {@code long}-Arrays.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection
 */
public abstract class LongArraySection extends ArraySection<long[]> {

	/**
	 * Diese Methode erzeugt eine neue {@link LongArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * 
	 * <pre>LongArraySection.from(array, 0, array.length)</pre>
	 * 
	 * @param array Array.
	 * @return {@link LongArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 */
	public static LongArraySection from(final long... array) throws NullPointerException {
		return ArraySection.validate(new LongArraySection() {

			@Override
			public long[] array() {
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

	/**
	 * Diese Methode erzeugt eine neue {@link LongArraySection} und gibt sie zurück.
	 * 
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link LongArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}.
	 */
	public static LongArraySection from(final long[] array, final int startIndex, final int finalIndex) throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException {
		return ArraySection.validate(new LongArraySection() {

			@Override
			public long[] array() {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int _arrayLength_(final long[] array) {
		return array.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int _hashCode_(final long[] array, final int index) {
		final long value = array[index];
		return (int)(value ^ (value >>> 32));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean _equals_(final long[] array1, final long[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int _compareTo_(final long[] array1, final long[] array2, final int index1, final int index2) {
		final long value1 = array1[index1], value2 = array2[index2];
		if (value1 == value2) return 0;
		if (value1 < value2) return -1;
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void _toString_(final long[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof LongArraySection)) return false;
		return this._equals_((LongArraySection)object);
	}

}
