package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code int}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IntegerArraySection extends ArraySection<int[], Integer> {

	/** Diese Methode liefert eine {@link IntegerArraySection} zum gegebenen {@code int}-Array. */
	public static IntegerArraySection from(int... array) throws NullPointerException {
		Objects.notNull(array);
		return new IntegerArraySection() {

			@Override
			public int[] array() {
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

	/** Diese Methode liefert eine {@link IntegerArraySection} zum gegebenene Abschnitt des gegebenen {@code int}-Arrays. */
	public static IntegerArraySection from(int[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new IntegerArraySection() {

			@Override
			public int[] array() {
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
	public IntegerArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return IntegerArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof IntegerArraySection)) return false;
		return this.defaultEquals((IntegerArraySection)object);
	}

	@Override
	protected Integer customGet(int[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(int[] array, int index, Integer value) {
		array[index] = value;
	}

	@Override
	protected int customHash(int[] array, int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(int[] array1, int[] array2, int index1, int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(int[] array1, int[] array2, int index1, int index2) {
		int value1 = array1[index1], value2 = array2[index2];
		if (value1 == value2) return 0;
		if (value1 < value2) return -1;
		return 1;
	}

	@Override
	protected void customPrint(int[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

}
