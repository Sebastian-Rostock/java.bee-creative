package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code short}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class ShortArraySection extends ArraySection<short[]> {

	/** Diese Methode erzeugt eine neue {@link ShortArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
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
	public static ShortArraySection from(final short[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
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

	@Override
	protected int customLength(final short[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final short[] array, final int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(final short[] array1, final short[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(final short[] array1, final short[] array2, final int index1, final int index2) {
		return array1[index1] - array2[index2];
	}

	@Override
	protected void customPrint(final short[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link ShortArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactShortArray(this).values()}.
	 *
	 * @return {@link Short}-{@link List}. */
	public List<Short> asList() {
		return new CompactShortArray(this).values();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof ShortArraySection)) return false;
		return this.defaultEquals((ShortArraySection)object);
	}

}
