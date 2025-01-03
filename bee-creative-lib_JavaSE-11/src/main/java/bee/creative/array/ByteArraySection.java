package bee.creative.array;

import bee.creative.lang.Objects;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code byte}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class ByteArraySection extends ArraySection<byte[], Byte> {

	/** Diese Methode liefert eine {@link ByteArraySection} zum gegebenen {@code byte}-Array. */
	public static ByteArraySection from(byte... array) throws NullPointerException {
		Objects.notNull(array);
		return new ByteArraySection() {

			@Override
			public byte[] array() {
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

	/** Diese Methode liefert eine {@link ByteArraySection} zum gegebenene Abschnitt des gegebenen {@code byte}-Arrays. */
	public static ByteArraySection from(byte[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new ByteArraySection() {

			@Override
			public byte[] array() {
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
	public ByteArraySection section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return ByteArraySection.from(this.array(), offset, length);
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof ByteArraySection)) return false;
		return this.defaultEquals((ByteArraySection)object);
	}

	@Override
	protected Byte customGet(byte[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(byte[] array, int index, Byte value) {
		array[index] = value;
	}

	@Override
	protected int customHash(byte[] array, int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(byte[] array1, byte[] array2, int index1, int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(byte[] array1, byte[] array2, int index1, int index2) {
		return array1[index1] - array2[index2];
	}

	@Override
	protected void customPrint(byte[] array, int index, StringBuilder result) {
		result.append(array[index]);
	}

}
