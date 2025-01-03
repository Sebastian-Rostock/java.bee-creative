package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code float}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FloatArraySection extends ArraySection<float[], Float> {

	/** Diese Methode liefert eine {@link FloatArraySection} zum gegebenen {@code float}-Array. */
	public static FloatArraySection from(float... array) throws NullPointerException {
		Objects.notNull(array);
		return new FloatArraySection() {

			@Override
			public float[] array() {
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
	public static FloatArraySection from(float[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new FloatArraySection() {

			@Override
			public float[] array() {
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
	public FloatArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return FloatArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FloatArraySection)) return false;
		return this.defaultEquals((FloatArraySection)object);
	}

	@Override
	protected Float customGet(float[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(float[] array, int index, Float value) {
		array[index] = value;
	}

	@Override
	protected int customHash(float[] array, int index) {
		return Float.floatToIntBits(array[index]);
	}

	@Override
	protected boolean customEquals(float[] array1, float[] array2, int index1, int index2) {
		return Float.floatToIntBits(array1[index1]) == Float.floatToIntBits(array2[index2]);
	}

	@Override
	protected int customCompare(float[] array1, float[] array2, int index1, int index2) {
		return Float.compare(array1[index1], array2[index2]);
	}

	@Override
	protected void customPrint(float[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

}
