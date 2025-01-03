package bee.creative.array;

import java.util.Comparator;
import bee.creative.lang.Objects;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine {@link ArraySection} für {@code GValue}-Arrays.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GValue> Typ der Elemente. */
public abstract class ObjectArraySection<GValue> extends ArraySection<GValue[], GValue> {

	/** Diese Methode liefert eine {@link ObjectArraySection} zum gegebenen {@code GValue}-Array. */
	@SafeVarargs
	public static <GValue> ObjectArraySection<GValue> from(GValue... array) throws NullPointerException {
		Objects.notNull(array);
		return new ObjectArraySection<>() {

			@Override
			public GValue[] array() {
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

	/** Diese Methode liefert eine {@link ObjectArraySection} zum gegebenen {@code GValue}-Array und {@link Comparator}. */
	@SafeVarargs
	public static <GValue> ObjectArraySection<GValue> from(Comparator<? super GValue> comparator, GValue... array) throws NullPointerException {
		Objects.notNull(comparator);
		return new ObjectArraySection<>() {

			@Override
			protected int customCompare(GValue[] array1, GValue[] array2, int index1, int index2) {
				return Comparators.compare(array1[index1], array2[index2], comparator);
			}

			@Override
			public GValue[] array() {
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

	/** Diese Methode liefert eine {@link ObjectArraySection} zum gegebenene Abschnitt des gegebenen {@code GValue}-Array. */
	public static <GValue> ObjectArraySection<GValue> from(GValue[] array, int offset, int length) throws NullPointerException, IllegalArgumentException {
		ArraySection.checkSection(offset, length, array.length);
		return new ObjectArraySection<>() {

			@Override
			public GValue[] array() {
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

	/** Diese Methode liefert eine {@link ObjectArraySection} zum gegebenene Abschnitt des gegebenen {@code GValue}-Array und {@link Comparator}. */
	public static <GValue> ObjectArraySection<GValue> from(Comparator<? super GValue> comparator, GValue[] array, int offset, int length)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		Objects.notNull(comparator);
		ArraySection.checkSection(offset, length, array.length);
		return new ObjectArraySection<>() {

			@Override
			protected int customCompare(GValue[] array1, GValue[] array2, int index1, int index2) {
				return Comparators.compare(array1[index1], array2[index2], comparator);
			}

			@Override
			public GValue[] array() {
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
	public ObjectArraySection<GValue> section(int offset, int length) throws IllegalArgumentException {
		this.checkSection(offset, length);
		return ObjectArraySection.from(this.array(), offset, length);
	}

	@Override
	@SuppressWarnings ("unchecked")
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof ObjectArraySection<?>)) return false;
		return this.defaultEquals((ObjectArraySection<GValue>)object);
	}

	@Override
	protected GValue customGet(GValue[] array, int index) {
		return array[index];
	}

	@Override
	protected void customSet(GValue[] array, int index, GValue value) {
		array[index] = value;
	}

	@Override
	protected int customHash(GValue[] array, int index) {
		return Objects.hash(array[index]);
	}

	@Override
	protected void customPrint(GValue[] array, int index, StringBuilder target) {
		target.append(array[index]);
	}

	@Override
	protected boolean customEquals(GValue[] array1, GValue[] array2, int index1, int index2) {
		return Objects.equals(array1[index1], array2[index2]);
	}

	@Override
	@SuppressWarnings ({"unchecked", "rawtypes"})
	protected int customCompare(GValue[] array1, GValue[] array2, int index1, int index2) {
		return Comparators.compare((Comparable)array1[index1], (Comparable)array2[index2]);
	}

}
