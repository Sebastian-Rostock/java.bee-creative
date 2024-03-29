package bee.creative.array;

import java.util.Comparator;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine {@link ArraySection} für {@link Object}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Elemente.
 * @see ArraySection */
public abstract class ObjectArraySection<GValue> extends ArraySection<GValue[]> {

	/** Diese Methode erzeugt eine neue {@link ObjectArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>ComparableArraySection.from(array, 0, array.length)</pre>
	 *
	 * @param <GValue> Typ der {@link Comparable}.
	 * @param array Array.
	 * @return {@link ObjectArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist. */
	@SafeVarargs
	public static <GValue> ObjectArraySection<GValue> from(final GValue... array) throws NullPointerException {
		return ArraySection.validate(new ObjectArraySection<GValue>() {

			@Override
			public GValue[] array() {
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

	/** Diese Methode erzeugt eine neue {@link ObjectArraySection} und gibt sie zurück. Der Rückgabewert entspricht:
	 * <pre>ObjectArraySection.from(comparator, array, 0, array.length)</pre>
	 *
	 * @param <GValue> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @param array Array.
	 * @return {@link ObjectArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebenen {@link Comparator} {@code null} ist. */
	@SafeVarargs
	public static <GValue> ObjectArraySection<GValue> from(final Comparator<? super GValue> comparator, final GValue... array) throws NullPointerException {
		Objects.notNull(comparator);
		return ArraySection.validate(new ObjectArraySection<GValue>() {

			@Override
			protected int customCompare(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
				return Comparators.compare(array1[index1], array2[index2], comparator);
			}

			@Override
			public GValue[] array() {
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

	/** Diese Methode erzeugt eine neue {@link ObjectArraySection} und gibt sie zurück.
	 *
	 * @param <GValue> Typ der Elemente.
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link ObjectArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static <GValue> ObjectArraySection<GValue> from(final GValue[] array, final int startIndex, final int finalIndex)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		return ArraySection.validate(new ObjectArraySection<GValue>() {

			@Override
			public GValue[] array() {
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

	/** Diese Methode erzeugt eine neue {@link ObjectArraySection} und gibt sie zurück.
	 *
	 * @param <GValue> Typ der Elemente.
	 * @param comparator {@link Comparator}.
	 * @param array Array.
	 * @param startIndex Index des ersten Werts im Abschnitt.
	 * @param finalIndex Index des ersten Werts nach dem Abschnitt.
	 * @return {@link ObjectArraySection}.
	 * @throws NullPointerException Wenn das gegebene Array bzw. der gegebenen {@link Comparator} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn {@code startIndex < 0} oder {@code finalIndex > array.length}.
	 * @throws IllegalArgumentException Wenn {@code finalIndex < startIndex}. */
	public static <GValue> ObjectArraySection<GValue> from(final Comparator<? super GValue> comparator, final GValue[] array, final int startIndex,
		final int finalIndex) throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		Objects.notNull(comparator);
		return ArraySection.validate(new ObjectArraySection<GValue>() {

			@Override
			protected int customCompare(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
				return Comparators.compare(array1[index1], array2[index2], comparator);
			}

			@Override
			public GValue[] array() {
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
	protected int customLength(final GValue[] array) {
		return array.length;
	}

	@Override
	protected int customHash(final GValue[] array, final int index) {
		return Objects.hash(array[index]);
	}

	@Override
	protected void customPrint(final GValue[] array, final int index, final StringBuilder target) {
		target.append(array[index]);
	}

	@Override
	protected boolean customEquals(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
		return Objects.equals(array1[index1], array2[index2]);
	}

	@Override
	@SuppressWarnings ({"unchecked", "rawtypes"})
	protected int customCompare(final GValue[] array1, final GValue[] array2, final int index1, final int index2) {
		return Comparators.compare((Comparable)array1[index1], (Comparable)array2[index2]);
	}

	/** Diese Methode gibt diese {@link ObjectArraySection} als {@link List} zurück und ist eine Abkürzung für {@code new CompactObjectArray<>(this).values()}.
	 *
	 * @return {@code GValue}-{@link List}. */
	public List<GValue> asList() {
		return new CompactObjectArray<>(this).values();
	}

	@Override
	@SuppressWarnings ("unchecked")
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof ObjectArraySection<?>)) return false;
		return this.defaultEquals((ObjectArraySection<GValue>)object);
	}

}
