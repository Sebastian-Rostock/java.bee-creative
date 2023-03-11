package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code float}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class FloatArraySection extends ArraySection<float[]> {

	/** Diese Methode erzeugt eine neue {@link FloatArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>FloatArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param array Array.
	 * @return {@link FloatArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static FloatArraySection from(final float... array) throws NullPointerException {
		return ArraySection.validate(new FloatArraySection() {

			@Override
			public float[] array() {
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

	/** Diese Methode erzeugt eine neue {@link FloatArraySection} und gibt sie zurück.
	 *
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link FloatArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static FloatArraySection from(final float[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new FloatArraySection() {

			@Override
			public float[] array() {
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
	protected int customLength(final float[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final float[] array, final int index) {
		return Float.floatToIntBits(array[index]);
	}

	@Override
	protected boolean customEquals(final float[] array1, final float[] array2, final int index1, final int index2) {
		return Float.floatToIntBits(array1[index1]) == Float.floatToIntBits(array2[index2]);
	}

	@Override
	protected int customCompare(final float[] array1, final float[] array2, final int index1, final int index2) {
		return Float.compare(array1[index1], array2[index2]);
	}

	@Override
	protected void customPrint(final float[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link FloatArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactFloatArray(this).values()}.
	 *
	 * @return {@link Float}-{@link List}. */
	public List<Float> asList() {
		return new CompactFloatArray(this).values();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FloatArraySection)) return false;
		return this.defaultEquals((FloatArraySection)object);
	}

}
