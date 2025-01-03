package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code boolean}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class BooleanArraySection extends ArraySection<boolean[], Boolean> {

	/** Diese Methode liefert eine {@link BooleanArraySection} zum gegebenen {@code boolean}-Array. */
	public static BooleanArraySection from(boolean... array) throws NullPointerException {
		Objects.notNull(array);
		return new BooleanArraySection() {

			@Override
			public boolean[] array() {
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

	/** Diese Methode liefert eine {@link BooleanArraySection} zum gegebenene Abschnitt des gegebenen {@code byte}-Arrays. */
	public static BooleanArraySection from(boolean[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new BooleanArraySection() {

			@Override
			public boolean[] array() {
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
	public BooleanArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return BooleanArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof BooleanArraySection)) return false;
		return this.defaultEquals((BooleanArraySection)object);
	}

	@Override
	protected Boolean customGet(boolean[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(boolean[] array, int index, Boolean value) {
		array[index] = value;
	}

	@Override
	protected int customHash(boolean[] array, int index) {
		return (array[index] ? 1231 : 1237);
	}

	@Override
	protected boolean customEquals(boolean[] array1, boolean[] array2, int index1, int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(boolean[] array1, boolean[] array2, int index1, int index2) {
		var value1 = array1[index1];
		var value2 = array2[index2];
		if (value1 == value2) return 0;
		if (value2) return -1;
		return 1;
	}

	@Override
	protected void customPrint(boolean[] array, int index, StringBuilder result) {
		result.append(array[index]);
	}

}
