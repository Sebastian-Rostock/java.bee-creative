package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code short}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class ShortArraySection extends ArraySection<short[], Short> {

	/** Diese Methode liefert eine {@link ShortArraySection} zum gegebenen {@code short}-Array. */
	public static ShortArraySection from(short... array) throws NullPointerException {
		Objects.notNull(array);
		return new ShortArraySection() {

			@Override
			public short[] array() {
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

	/** Diese Methode liefert eine {@link ShortArraySection} zum gegebenene Abschnitt des gegebenen {@code byte}-Arrays. */
	public static ShortArraySection from(short[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new ShortArraySection() {

			@Override
			public short[] array() {
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
	public ShortArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return ShortArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof ShortArraySection)) return false;
		return this.defaultEquals((ShortArraySection)object);
	}

	@Override
	protected Short customGet(short[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(short[] array, int index, Short value) {
		array[index] = value;
	}

	@Override
	protected int customHash(short[] array, int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(short[] array1, short[] array2, int index1, int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(short[] array1, short[] array2, int index1, int index2) {
		return array1[index1] - array2[index2];
	}

	@Override
	protected void customPrint(short[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

}
