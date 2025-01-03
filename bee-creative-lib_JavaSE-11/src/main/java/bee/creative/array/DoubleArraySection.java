package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code double}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class DoubleArraySection extends ArraySection<double[], Double> {

	/** Diese Methode liefert eine {@link FloatArraySection} zum gegebenen {@code float}-Array. */
	public static DoubleArraySection from(double... array) throws NullPointerException {
		Objects.notNull(array);
		return new DoubleArraySection() {

			@Override
			public double[] array() {
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

	/** Diese Methode liefert eine {@link FloatArraySection} zum gegebenene Abschnitt des gegebenen {@code float}-Arrays. */
	public static DoubleArraySection from(double[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new DoubleArraySection() {

			@Override
			public double[] array() {
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
	public DoubleArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return DoubleArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof DoubleArraySection)) return false;
		return this.defaultEquals((DoubleArraySection)object);
	}

	@Override
	protected Double customGet(double[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(double[] array, int index, Double value) {
		array[index] = value;
	}

	@Override
	protected int customHash(double[] array, int index) {
		return Double.hashCode(array[index]);
	}

	@Override
	protected boolean customEquals(double[] array1, double[] array2, int index1, int index2) {
		return Double.doubleToLongBits(array1[index1]) == Double.doubleToLongBits(array2[index2]);
	}

	@Override
	protected int customCompare(double[] array1, double[] array2, int index1, int index2) {
		return Double.compare(array1[index1], array2[index2]);
	}

	@Override
	protected void customPrint(double[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

}
