package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code boolean}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class BooleanArraySection extends ArraySection<boolean[]> {

	/** Diese Methode erzeugt eine neue {@link BooleanArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>BooleanArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param array Array.
	 * @return {@link BooleanArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static BooleanArraySection from(final boolean... array) throws NullPointerException {
		return ArraySection.validate(new BooleanArraySection() {

			@Override
			public boolean[] array() {
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

	/** Diese Methode erzeugt eine neue {@link BooleanArraySection} und gibt sie zurück.
	 *
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link BooleanArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static BooleanArraySection from(final boolean[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new BooleanArraySection() {

			@Override
			public boolean[] array() {
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
	protected int customLength(final boolean[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final boolean[] array, final int index) {
		return (array[index] ? 1231 : 1237);
	}

	@Override
	protected boolean customEquals(final boolean[] array1, final boolean[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(final boolean[] array1, final boolean[] array2, final int index1, final int index2) {
		final boolean value1 = array1[index1], value2 = array2[index2];
		if (value1 == value2) return 0;
		if (value2) return -1;
		return 1;
	}

	@Override
	protected void customPrint(final boolean[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link BooleanArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactBooleanArray(this).values()}.
	 *
	 * @return {@link Boolean}-{@link List}. */
	public List<Boolean> asList() {
		return new CompactBooleanArray(this).values();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof BooleanArraySection)) return false;
		return this.defaultEquals((BooleanArraySection)object);
	}

}
