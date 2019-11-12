package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code int}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class IntegerArraySection extends ArraySection<int[]> {

	/** Diese Methode erzeugt eine neue {@link IntegerArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>IntegerArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param array Array.
	 * @return {@link IntegerArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static IntegerArraySection from(final int... array) throws NullPointerException {
		return ArraySection.validate(new IntegerArraySection() {

			@Override
			public int[] array() {
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

	/** Diese Methode erzeugt eine neue {@link IntegerArraySection} und gibt sie zurück.
	 *
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link IntegerArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static IntegerArraySection from(final int[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new IntegerArraySection() {

			@Override
			public int[] array() {
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

	/** {@inheritDoc} */
	@Override
	protected int customLength(final int[] array) {
		return array.length;
	}

	/** {@inheritDoc} */
	@Override
	protected int customHash(final int[] array, final int index) {
		return array[index];
	}

	/** {@inheritDoc} */
	@Override
	protected boolean customEquals(final int[] array1, final int[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	/** {@inheritDoc} */
	@Override
	protected int customCompare(final int[] array1, final int[] array2, final int index1, final int index2) {
		final int value1 = array1[index1], value2 = array2[index2];
		if (value1 == value2) return 0;
		if (value1 < value2) return -1;
		return 1;
	}

	/** {@inheritDoc} */
	@Override
	protected void customPrint(final int[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link IntegerArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactIntegerArray(this).values()}.
	 * 
	 * @return {@link Integer}-{@link List}. */
	public List<Integer> asList() {
		return new CompactIntegerArray(this).values();
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof IntegerArraySection)) return false;
		return this.defaultEquals((IntegerArraySection)object);
	}

}
