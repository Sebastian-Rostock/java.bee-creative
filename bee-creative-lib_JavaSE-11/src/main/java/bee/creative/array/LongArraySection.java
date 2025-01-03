package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code long}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class LongArraySection extends ArraySection<long[], Long> {

	/** Diese Methode liefert eine {@link LongArraySection} zum gegebenen {@code long}-Array. */
	public static LongArraySection from(long... array) throws NullPointerException {
		Objects.notNull(array);
		return new LongArraySection() {

			@Override
			public long[] array() {
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

	/** Diese Methode liefert eine {@link LongArraySection} zum gegebenene Abschnitt des gegebenen {@code long}-Arrays. */
	public static LongArraySection from(long[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new LongArraySection() {

			@Override
			public long[] array() {
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
	public LongArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return LongArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof LongArraySection)) return false;
		return this.defaultEquals((LongArraySection)object);
	}

	@Override
	protected Long customGet(long[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(long[] array, int index, Long value) {
		array[index] = value;
	}

	@Override
	protected int customHash(long[] array, int index) {
		long value = array[index];
		return (int)(value ^ (value >>> 32));
	}

	@Override
	protected boolean customEquals(long[] array1, long[] array2, int index1, int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(long[] array1, long[] array2, int index1, int index2) {
		long value1 = array1[index1], value2 = array2[index2];
		if (value1 == value2) return 0;
		if (value1 < value2) return -1;
		return 1;
	}

	@Override
	protected void customPrint(long[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

}
