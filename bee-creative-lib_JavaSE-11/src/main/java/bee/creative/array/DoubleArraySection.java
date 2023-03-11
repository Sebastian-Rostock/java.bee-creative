package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code double}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class DoubleArraySection extends ArraySection<double[]> {

	/** Diese Methode erzeugt eine neue {@link DoubleArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>DoubleArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param array Array.
	 * @return {@link DoubleArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static DoubleArraySection from(final double... array) throws NullPointerException {
		return ArraySection.validate(new DoubleArraySection() {

			@Override
			public double[] array() {
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

	/** Diese Methode erzeugt eine neue {@link DoubleArraySection} und gibt sie zurück.
	 *
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link DoubleArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static DoubleArraySection from(final double[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new DoubleArraySection() {

			@Override
			public double[] array() {
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
	protected int customLength(final double[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final double[] array, final int index) {
		final long value = Double.doubleToLongBits(array[index]);
		return (int)(value ^ (value >>> 32));
	}

	@Override
	protected boolean customEquals(final double[] array1, final double[] array2, final int index1, final int index2) {
		return Double.doubleToLongBits(array1[index1]) == Double.doubleToLongBits(array2[index2]);
	}

	@Override
	protected int customCompare(final double[] array1, final double[] array2, final int index1, final int index2) {
		return Double.compare(array1[index1], array2[index2]);
	}

	@Override
	protected void customPrint(final double[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link DoubleArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactDoubleArray(this).values()}.
	 *
	 * @return {@link Double}-{@link List}. */
	public List<Double> asList() {
		return new CompactDoubleArray(this).values();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof DoubleArraySection)) return false;
		return this.defaultEquals((DoubleArraySection)object);
	}

}
