package bee.creative.array;

import java.util.List;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code byte}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @see ArraySection */
public abstract class ByteArraySection extends ArraySection<byte[]> {

	/** Diese Methode erzeugt eine neue {@link ByteArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>ByteArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param array Array.
	 * @return {@link ByteArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	public static ByteArraySection from(final byte... array) throws NullPointerException {
		return ArraySection.validate(new ByteArraySection() {

			@Override
			public byte[] array() {
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

	/** Diese Methode erzeugt eine neue {@link ByteArraySection} und gibt sie zurück.
	 *
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link ByteArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static ByteArraySection from(final byte[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new ByteArraySection() {

			@Override
			public byte[] array() {
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
	protected int customLength(final byte[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final byte[] array, final int index) {
		return array[index];
	}

	@Override
	protected boolean customEquals(final byte[] array1, final byte[] array2, final int index1, final int index2) {
		return array1[index1] == array2[index2];
	}

	@Override
	protected int customCompare(final byte[] array1, final byte[] array2, final int index1, final int index2) {
		return array1[index1] - array2[index2];
	}

	@Override
	protected void customPrint(final byte[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	/** Diese Methode gibt diese {@link ByteArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactByteArray(this).values()}.
	 *
	 * @return {@link Byte}-{@link List}. */
	public List<Byte> asList() {
		return new CompactByteArray(this).values();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof ByteArraySection)) return false;
		return this.defaultEquals((ByteArraySection)object);
	}

}
