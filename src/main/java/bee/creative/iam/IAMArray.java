package bee.creative.iam;

import java.util.Iterator;
import java.util.List;
import bee.creative.lang.Objects;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators.BaseIterator;

/** Diese Klasse implementiert eine abstrakte Zahlenfolge, welche in einer Auflistung ({@link IAMListing}) für die Elemente sowie einer Abbildung
 * ({@link IAMMapping}) für die Schlüssel und Werte der Einträge ({@code IAMEntry}) verwendet wird.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMArray implements Iterable<Integer>, Comparable<IAMArray> {

	static class IntArray extends IAMArray {

		final int[] array;

		final int offset;

		IntArray(final int[] array, final int offset, final int length) {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		protected int customGet(final int index) {
			return this.array[this.offset + index];
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			return new IntArray(this.array, this.offset + offset, length);
		}

		@Override
		public int mode() {
			return 4;
		}

		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for (int i = this.offset, l = this.length; l != 0; ++i, --l) {
				hash = (hash * 0x01000193) ^ this.array[i];
			}
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (object instanceof IntArray) return IAMArray.equals(this, (IntArray)object);
			if (object instanceof ByteArray) return IAMArray.equals(this, (ByteArray)object);
			if (object instanceof CharArray) return IAMArray.equals(this, (CharArray)object);
			if (object instanceof ShortArray) return IAMArray.equals(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.equals(this, (SectionArray)object);
			if (object instanceof IAMArray) return IAMArray.equals(this, (IAMArray)object);
			return false;
		}

		@Override
		public int compareTo(final IAMArray object) {
			if (object instanceof IntArray) return IAMArray.compare(this, (IntArray)object);
			if (object instanceof ByteArray) return IAMArray.compare(this, (ByteArray)object);
			if (object instanceof CharArray) return IAMArray.compare(this, (CharArray)object);
			if (object instanceof ShortArray) return IAMArray.compare(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.compare(this, (SectionArray)object);
			return IAMArray.compare(this, object);
		}

	}

	static class ByteArray extends IAMArray {

		final byte[] array;

		final int offset;

		ByteArray(final byte[] array, final int offset, final int length) {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		protected int customGet(final int index) {
			return this.array[this.offset + index];
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			return new ByteArray(this.array, this.offset + offset, length);
		}

		@Override
		public int mode() {
			return 1;
		}

		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for (int i = this.offset, l = this.length; l != 0; ++i, --l) {
				hash = (hash * 0x01000193) ^ this.array[i];
			}
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (object instanceof IntArray) return IAMArray.equals((IntArray)object, this);
			if (object instanceof ByteArray) return IAMArray.equals(this, (ByteArray)object);
			if (object instanceof CharArray) return IAMArray.equals(this, (CharArray)object);
			if (object instanceof ShortArray) return IAMArray.equals(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.equals(this, (SectionArray)object);
			if (object instanceof IAMArray) return IAMArray.equals(this, (IAMArray)object);
			return false;
		}

		@Override
		public int compareTo(final IAMArray object) {
			if (object instanceof IntArray) return -IAMArray.compare((IntArray)object, this);
			if (object instanceof ByteArray) return IAMArray.compare(this, (ByteArray)object);
			if (object instanceof CharArray) return IAMArray.compare(this, (CharArray)object);
			if (object instanceof ShortArray) return IAMArray.compare(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.compare(this, (SectionArray)object);
			return IAMArray.compare(this, object);
		}

	}

	static class CharArray extends IAMArray {

		final char[] array;

		final int offset;

		CharArray(final char[] array, final int offset, final int length) {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		protected int customGet(final int index) {
			return this.array[this.offset + index];
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			return new CharArray(this.array, this.offset + offset, length);
		}

		@Override
		public int mode() {
			return 2;
		}

		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for (int i = this.offset, l = this.length; l != 0; ++i, --l) {
				hash = (hash * 0x01000193) ^ this.array[i];
			}
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (object instanceof IntArray) return IAMArray.equals((IntArray)object, this);
			if (object instanceof ByteArray) return IAMArray.equals((ByteArray)object, this);
			if (object instanceof CharArray) return IAMArray.equals(this, (CharArray)object);
			if (object instanceof ShortArray) return IAMArray.equals(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.equals(this, (SectionArray)object);
			if (object instanceof IAMArray) return IAMArray.equals(this, (IAMArray)object);
			return false;
		}

		@Override
		public int compareTo(final IAMArray object) {
			if (object instanceof IntArray) return -IAMArray.compare((IntArray)object, this);
			if (object instanceof ByteArray) return -IAMArray.compare((ByteArray)object, this);
			if (object instanceof CharArray) return IAMArray.compare(this, (CharArray)object);
			if (object instanceof ShortArray) return IAMArray.compare(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.compare(this, (SectionArray)object);
			return IAMArray.compare(this, object);
		}

	}

	static class ShortArray extends IAMArray {

		final short[] array;

		final int offset;

		ShortArray(final short[] array, final int offset, final int length) {
			super(array.length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		protected int customGet(final int index) {
			return this.array[this.offset + index];
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			return new ShortArray(this.array, this.offset + offset, length);
		}

		@Override
		public int mode() {
			return 2;
		}

		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for (int i = this.offset, l = this.length; l != 0; ++i, --l) {
				hash = (hash * 0x01000193) ^ this.array[i];
			}
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (object instanceof IntArray) return IAMArray.equals((IntArray)object, this);
			if (object instanceof ByteArray) return IAMArray.equals((ByteArray)object, this);
			if (object instanceof CharArray) return IAMArray.equals((CharArray)object, this);
			if (object instanceof ShortArray) return IAMArray.equals(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.equals(this, (SectionArray)object);
			if (object instanceof IAMArray) return IAMArray.equals(this, (IAMArray)object);
			return false;
		}

		@Override
		public int compareTo(final IAMArray object) {
			if (object instanceof IntArray) return -IAMArray.compare((IntArray)object, this);
			if (object instanceof ByteArray) return -IAMArray.compare((ByteArray)object, this);
			if (object instanceof CharArray) return -IAMArray.compare((CharArray)object, this);
			if (object instanceof ShortArray) return IAMArray.compare(this, (ShortArray)object);
			if (object instanceof SectionArray) return IAMArray.compare(this, (SectionArray)object);
			return IAMArray.compare(this, object);
		}

	}

	static class ValueArray extends IAMArray {

		final int item;

		ValueArray(final int item) {
			super(1);
			this.item = item;
		}

		@Override
		protected int customGet(final int index) {
			return this.item;
		}

		@Override
		public int mode() {
			return 4;
		}

	}

	static class EmptyArray extends IAMArray {

		EmptyArray() {
			super(0);
		}

	}

	static class ConcatArray extends IAMArray {

		public final IAMArray array1;

		public final IAMArray array2;

		ConcatArray(final IAMArray array1, final IAMArray array2) {
			super(array1.length + array2.length);
			this.array1 = array1;
			this.array2 = array2;
		}

		@Override
		public int mode() {
			return Math.max(this.array1.mode(), this.array2.mode());
		}

		@Override
		protected int customGet(final int index) {
			final int index2 = index - this.array1.length;
			return index2 < 0 ? this.array1.customGet(index) : this.array2.customGet(index2);
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.section(offset2, length);
			if (length2 <= 0) return this.array1.section(offset, length);
			return this.array1.section(offset, -offset2).concat(this.array2.section(0, length2));
		}

	}

	static class SectionArray extends IAMArray {

		final IAMArray array;

		final int offset;

		SectionArray(final IAMArray array, final int offset, final int length) {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		protected int customGet(final int index) {
			return this.array.customGet(this.offset + index);
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			return this.array.customSection(this.offset + offset, length);
		}

		@Override
		public int mode() {
			return this.array.mode();
		}

		@Override
		public int hashCode() {
			int hash = 0x811C9DC5;
			for (int i = this.offset, l = this.length; l != 0; ++i, --l) {
				hash = (hash * 0x01000193) ^ this.array.customGet(i);
			}
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (object instanceof IntArray) return IAMArray.equals((IntArray)object, this);
			if (object instanceof ByteArray) return IAMArray.equals((ByteArray)object, this);
			if (object instanceof CharArray) return IAMArray.equals((CharArray)object, this);
			if (object instanceof ShortArray) return IAMArray.equals((ShortArray)object, this);
			if (object instanceof SectionArray) return IAMArray.equals(this, (SectionArray)object);
			if (object instanceof IAMArray) return IAMArray.equals(this, (IAMArray)object);
			return false;
		}

		@Override
		public int compareTo(final IAMArray object) {
			if (object instanceof IntArray) return -IAMArray.compare((IntArray)object, this);
			if (object instanceof ByteArray) return -IAMArray.compare((ByteArray)object, this);
			if (object instanceof CharArray) return -IAMArray.compare((CharArray)object, this);
			if (object instanceof ShortArray) return -IAMArray.compare((ShortArray)object, this);
			if (object instanceof SectionArray) return IAMArray.compare(this, (SectionArray)object);
			return IAMArray.compare(this, object);
		}

	}

	/** Dieses Feld speichert das leere {@link IAMArray}. */
	public static final IAMArray EMPTY = new EmptyArray();

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebene Zahlenfolge zurück. Änderungen am Inhalt von {@code array} werden auf das
	 * gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final int... array) throws NullPointerException {
		return IAMArray.from(array, 0, array.length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf einen Abschnitt der gegebenen Zahlenfolge zurück. Änderungen am Inhalt von {@code array}
	 * werden auf das gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @param offset Beginn der Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return {@link IAMArray}-Sicht auf einen Abschnitt von {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebene Abschnitt nicht in {@code array} liegt. */
	public static IAMArray from(final int[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return IAMArray.EMPTY;
		if (length == 1) return new ValueArray(array[offset]);
		return new IntArray(array, offset, length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebene Zahlenfolge zurück. Änderungen am Inhalt von {@code array} werden auf das
	 * gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final byte[] array) throws NullPointerException {
		return IAMArray.from(array, 0, array.length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf einen Abschnitt der gegebenen Zahlenfolge zurück. Änderungen am Inhalt von {@code array}
	 * werden auf das gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @param offset Beginn der Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return {@link IAMArray}-Sicht auf einen Abschnitt von {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebene Abschnitt nicht in {@code array} liegt. */
	public static IAMArray from(final byte[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return IAMArray.EMPTY;
		return new ByteArray(array, offset, length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebene Zahlenfolge zurück. Änderungen am Inhalt von {@code array} werden auf das
	 * gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final char[] array) throws NullPointerException {
		return IAMArray.from(array, 0, array.length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf einen Abschnitt der gegebenen Zahlenfolge zurück. Änderungen am Inhalt von {@code array}
	 * werden auf das gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @param offset Beginn der Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return {@link IAMArray}-Sicht auf einen Abschnitt von {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebene Abschnitt nicht in {@code array} liegt. */
	public static IAMArray from(final char[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return IAMArray.EMPTY;
		return new CharArray(array, offset, length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebene Zahlenfolge zurück. Änderungen am Inhalt von {@code array} werden auf das
	 * gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final short[] array) throws NullPointerException {
		return IAMArray.from(array, 0, array.length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf einen Abschnitt der gegebenen Zahlenfolge zurück. Änderungen am Inhalt von {@code array}
	 * werden auf das gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @param offset Beginn der Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return {@link IAMArray}-Sicht auf einen Abschnitt von {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebene Abschnitt nicht in {@code array} liegt. */
	public static IAMArray from(final short[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return IAMArray.EMPTY;
		return new ShortArray(array, offset, length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} mit den gegebene Zahlen zurück. Änderungen am Inhalt von {@code array} werden nicht auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray} aus den {@link Number#intValue()}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final List<? extends Number> array) throws NullPointerException {
		final int length = array.size();
		final int[] result = new int[length];
		int offset = 0;
		for (final Number item: array) {
			result[offset++] = item.intValue();
		}
		return IAMArray.from(result);
	}

	static boolean equals(final IntArray array1, final IntArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final int[] a1 = array1.array;
		final int[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final IntArray array1, final ByteArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final int[] a1 = array1.array;
		final byte[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final IntArray array1, final CharArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final int[] a1 = array1.array;
		final char[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final IntArray array1, final ShortArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final int[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final IntArray array1, final SectionArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final int[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final IntArray array1, final IAMArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final int[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != array2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final ByteArray array1, final ByteArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final byte[] a1 = array1.array;
		final byte[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final ByteArray array1, final CharArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final byte[] a1 = array1.array;
		final char[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final ByteArray array1, final ShortArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final byte[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final ByteArray array1, final SectionArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final byte[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final ByteArray array1, final IAMArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final byte[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != array2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final CharArray array1, final CharArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final char[] a1 = array1.array;
		final char[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final CharArray array1, final ShortArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final char[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final CharArray array1, final SectionArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final char[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final CharArray array1, final IAMArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final char[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != array2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final ShortArray array1, final ShortArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final short[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2[i2]) return false;
		return true;
	}

	static boolean equals(final ShortArray array1, final SectionArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final short[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1[i1] != a2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final ShortArray array1, final IAMArray array2) {
		int length = array1.length;
		if (length != array2.length) return false;
		final short[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0; length != 0; ++i1, ++i2, --length)
			if (a1[i1] != array2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final SectionArray array1, final SectionArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final IAMArray a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset; l != 0; ++i1, ++i2, --l)
			if (a1.customGet(i1) != a2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final SectionArray array1, final IAMArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		final IAMArray a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0; l != 0; ++i1, ++i2, --l)
			if (a1.customGet(i1) != array2.customGet(i2)) return false;
		return true;
	}

	static boolean equals(final IAMArray array1, final IAMArray array2) {
		int l = array1.length;
		if (l != array2.length) return false;
		for (int i2 = 0; l != 0; ++i2, --l)
			if (array1.customGet(i2) != array2.customGet(i2)) return false;
		return true;
	}

	static int compare(final IntArray array1, final IntArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final int[] a1 = array1.array;
		final int[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final IntArray array1, final ByteArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final int[] a1 = array1.array;
		final byte[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final IntArray array1, final CharArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final int[] a1 = array1.array;
		final char[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final IntArray array1, final ShortArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final int[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final IntArray array1, final SectionArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final int[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final IntArray array1, final IAMArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final int[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = array2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ByteArray array1, final ByteArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final byte[] a1 = array1.array;
		final byte[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ByteArray array1, final CharArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final byte[] a1 = array1.array;
		final char[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ByteArray array1, final ShortArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final byte[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ByteArray array1, final SectionArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final byte[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ByteArray array1, final IAMArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final byte[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = array2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final CharArray array1, final CharArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final char[] a1 = array1.array;
		final char[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final CharArray array1, final ShortArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final char[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final CharArray array1, final SectionArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final char[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final CharArray array1, final IAMArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final char[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = array2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ShortArray array1, final ShortArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final short[] a1 = array1.array;
		final short[] a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2[i2];
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ShortArray array1, final SectionArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final short[] a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = a2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final ShortArray array1, final IAMArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final short[] a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1[i1], v2 = array2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final SectionArray array1, final SectionArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final IAMArray a1 = array1.array;
		final IAMArray a2 = array2.array;
		for (int i1 = array1.offset, i2 = array2.offset, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1.customGet(i1), v2 = a2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final SectionArray array1, final IAMArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		final IAMArray a1 = array1.array;
		for (int i1 = array1.offset, i2 = 0, l = l1 < l2 ? l1 : l2; l != 0; ++i1, ++i2, --l) {
			final int v1 = a1.customGet(i1), v2 = array2.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	static int compare(final IAMArray array1, final IAMArray array2) {
		final int l1 = array1.length, l2 = array2.length;
		for (int i = 0, l = l1 < l2 ? l1 : l2; l != 0; ++i, --l) {
			final int v1 = array1.customGet(i), v2 = array2.customGet(i);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		if (l1 < l2) return -1;
		if (l1 > l2) return +1;
		return 0;
	}

	/** Dieses Feld speichert die Länge. */
	protected final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge. */
	protected IAMArray(final int length) {
		this.length = length;
	}

	/** Diese Methode implementiert {@link #get(int)} ohne Parameterprüfung.
	 *
	 * @param index Index.
	 * @return {@code index}-te Zahl. */
	protected int customGet(final int index) {
		return 0;
	}

	/** Diese Methode implementiert {@link #section(int, int)} ohne Parameterprüfung.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Abschnitt. */
	protected IAMArray customSection(final int offset, final int length) {
		if (length == 0) return IAMArray.EMPTY;
		return new SectionArray(this, offset, length);
	}

	/** Diese Methode gibt die {@code index}-te Zahl zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 *
	 * @see #length()
	 * @param index Index.
	 * @return {@code index}-te Zahl. */
	public final int get(final int index) {
		if ((index < 0) || (index >= this.length)) return 0;
		return this.customGet(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge zurück ({@code 0..1073741823}).
	 *
	 * @see #get(int)
	 * @return Länge. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt die Größe jeder Zahl dieser Zahlenfolge zurück. Diese Größe ist {@code 1} für {@code byte}-Zahlen, {@code 2} für {@code short}-Zahlen
	 * und {@code 4} für {@code int}-Zahlen.
	 *
	 * @return Größe jeder Zahl dieser Zahlenfolge (1, 2 oder 4). */
	public int mode() {
		return 1;
	}

	/** Diese Methode gibt den Streuwert zurück. <pre>
	 * int result = 0x811C9DC5;
	 * for (int i = 0; i < length(); i++)
	 *   result = (result * 0x01000193) ^ get(i);
	 * return result;
	 * </pre>
	 *
	 * @return Streuwert. */
	public final int hash() {
		return this.hashCode();
	}

	/** Diese Methode gibt nur dann true zurück, wenn diese Zahlenfolge gleich der gegebenen Zahlenfolge ist. <pre>
	 * if (this.length() != that.length()) return false;
	 * for (int i = 0; i < length(); i++)
	 *   if (this.get(i) != that.get(i)) return false;
	 * return true;</pre>
	 *
	 * @param that Zahlenfolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final IAMArray that) throws NullPointerException {
		return this.equals(Objects.<Object>notNull(that));
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Zahlenfolge mit der gegebenen Zahlenfolge zurück.
	 *
	 * @param that Zahlenfolge.
	 * @return {@link IAMArray}-Sicht auf die Verkettung dieser Zahlenfolge mit der gegebenen Zahlenfolge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public IAMArray concat(final IAMArray that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
		return new ConcatArray(this, that);
	}

	/** Diese Methode gibt eine Abschrift der Zahlen dieser Zahlenfolge zurück Abhängig von der {@link #mode() Größe der Zahlen} liefert sie dabei
	 * {@link #toBytes() IAMArray.from(this.toBytes())}, {@link #toShorts() IAMArray.from(this.toShorts())} oder {@link #toInts() IAMArray.from(this.toInts())}.
	 *
	 * @return kompaktierte Abschrift der Zahlenfolge. */
	public IAMArray compact() {
		switch (this.mode()) {
			case 1:
				return IAMArray.from(this.toBytes());
			case 2:
				return IAMArray.from(this.toShorts());
			default:
				return IAMArray.from(this.toInts());
		}
	}

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung dieser Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der gegebenen Zahlenfolge ist. <pre>
	 * for (int i = 0, result; i < min(this.length(), that.length()); i++) {
	 *   if (this.get(i) < that.get(i)) return -1;
	 *   if (this.get(i) > that.get(i)) return +1;
	 * }
	 * if (this.length() < that.length()) return -1;
	 * if (this.length() > that.length()) return +1;
	 * return 0;</pre>
	 *
	 * @see #compareTo(IAMArray)
	 * @param that Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public int compare(final IAMArray that) throws NullPointerException {
		return this.compareTo(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #section(int, int) this.section(offset, this.length() - offset)}.
	 *
	 * @see #length() */
	public IAMArray section(final int offset) {
		return this.section(offset, this.length - offset);
	}

	/** Diese Methode gibt einen Abschnitt dieser Zahlenfolge ab der gegebenen Position und mit der gegebenen Länge zurück. Wenn der Abschnitt nicht innerhalb der
	 * Zahlenfolge liegt oder die Länge kleiner als {@code 1} ist, wird eine leere Zahlenfolge geliefert.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Abschnitt. */
	public IAMArray section(final int offset, final int length) {
		if ((offset < 0) || (length <= 0) || ((offset + length) > this.length)) return this.customSection(0, 0);
		return this.customSection(offset, length);
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code int[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public int[] toInts() {
		final int[] result = new int[this.length];
		this.toInts(result, 0);
		return result;
	}

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code int[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der dessen dieser Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void toInts(final int[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		for (int i = 0; i < length; i++) {
			result[i + offset] = this.customGet(i);
		}
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code byte[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public byte[] toBytes() {
		final byte[] result = new byte[this.length];
		this.toBytes(result, 0);
		return result;
	}

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code byte[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der Länge der gegebenen Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void toBytes(final byte[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		for (int i = 0; i < length; i++) {
			result[i + offset] = (byte)this.customGet(i);
		}
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code char[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public char[] toChars() {
		final char[] result = new char[this.length];
		this.toChars(result, 0);
		return result;
	}

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code char[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der Länge der gegebenen Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void toChars(final char[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		for (int i = 0; i < length; i++) {
			result[i + offset] = (char)this.customGet(i);
		}
	}

	/** Diese Methode gibt eine Kopie der gegebenen Zahlenfolge als {@code short[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public short[] toShorts() {
		final short[] result = new short[this.length];
		this.toShorts(result, 0);
		return result;
	}

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code chort[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der Länge der gegebenen Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void toShorts(final short[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		for (int i = 0; i < length; i++) {
			result[i + offset] = (short)this.customGet(i);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Integer> iterator() {
		return new BaseIterator<Integer>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return this.index < IAMArray.this.length;
			}

			@Override
			public Integer next() {
				return new Integer(IAMArray.this.customGet(this.index++));
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int hash = 0x811C9DC5;
		for (int i = 0, l = this.length; l != 0; ++i, --l) {
			hash = (hash * 0x01000193) ^ this.customGet(i);
		}
		return hash;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (object instanceof IntArray) return IAMArray.equals((IntArray)object, this);
		if (object instanceof ByteArray) return IAMArray.equals((ByteArray)object, this);
		if (object instanceof CharArray) return IAMArray.equals((CharArray)object, this);
		if (object instanceof ShortArray) return IAMArray.equals((ShortArray)object, this);
		if (object instanceof SectionArray) return IAMArray.equals((SectionArray)object, this);
		if (object instanceof IAMArray) return IAMArray.equals(this, (IAMArray)object);
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(final IAMArray object) {
		if (object instanceof IntArray) return -IAMArray.compare((IntArray)object, this);
		if (object instanceof ByteArray) return -IAMArray.compare((ByteArray)object, this);
		if (object instanceof CharArray) return -IAMArray.compare((CharArray)object, this);
		if (object instanceof ShortArray) return -IAMArray.compare((ShortArray)object, this);
		if (object instanceof SectionArray) return -IAMArray.compare((SectionArray)object, this);
		return IAMArray.compare(this, object);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.length() > 30 ? //
			Objects.formatIterable(false, Iterables.chainedIterable(this.section(0, 15), Iterables.itemIterable(Objects.toStringObject("...")))) : //
			Objects.formatIterable(false, this);
	}

}
