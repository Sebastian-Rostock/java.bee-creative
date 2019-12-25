package bee.creative.iam;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Objects;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators.BaseIterator;
// TODO equals und compare

/** Diese Klasse implementiert eine abstrakte Zahlenfolge, welche in einer Auflistung ({@link IAMListing}) für die Elemente sowie einer Abbildung
 * ({@link IAMMapping}) für die Schlüssel und Werte der Einträge ({@code IAMEntry}) verwendet wird.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
/** Diese Klasse implementiert . Diese Schnittstelle definiert .
 *
 * @author [cc-by] 2019 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMArray implements Iterable<Integer>, Comparable<IAMArray> {

	/** Diese Klasse implementiert die Zahlenfolge für {@link IAMArray#MODE_INT8}. */
	public static class INT8Array extends IAMArray implements Emuable {

		/** Dieses Feld speichert das leere {@link INT8Array}. */
		public static final INT8Array EMPTY = new INT8Array(new byte[0]);

		final byte[] array;

		final int offset;

		/** Dieser Konstruktor initialisiert das Array.
		 *
		 * @param array Array.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public INT8Array(final byte[] array) throws NullPointerException {
			this(array, 0, array.length);
		}

		INT8Array(final byte[] array, final int offset, final int length) throws IllegalArgumentException {
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
			if (length == 0) return INT8Array.EMPTY;
			return new INT8Array(this.array, this.offset + offset, length);
		}

		@Override
		protected boolean customIsCompact() {
			return (this.offset == 0) && (this.length == this.array.length);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + (this.customIsCompact() ? EMU.fromArray(this.array) : 0);
		}

		@Override
		public byte mode() {
			return IAMArray.MODE_INT8;
		}

	}

	/** Diese Klasse implementiert die Zahlenfolge für {@link IAMArray#MODE_INT16}. */
	public static class INT16Array extends IAMArray implements Emuable {

		/** Dieses Feld speichert das leere {@link INT16Array}. */
		public static final INT16Array EMPTY = new INT16Array(new short[0]);

		final short[] array;

		final int offset;

		/** Dieser Konstruktor initialisiert das Array.
		 *
		 * @param array Array.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public INT16Array(final short[] array) throws NullPointerException {
			this(array, 0, array.length);
		}

		INT16Array(final short[] array, final int offset, final int length) throws IllegalArgumentException {
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
			if (length == 0) return INT16Array.EMPTY;
			return new INT16Array(this.array, this.offset + offset, length);
		}

		@Override
		protected boolean customIsCompact() {
			return (this.offset == 0) && (this.length == this.array.length);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + (this.customIsCompact() ? EMU.fromArray(this.array) : 0);
		}

		@Override
		public byte mode() {
			return IAMArray.MODE_INT16;
		}

	}

	/** Diese Klasse implementiert die Zahlenfolge für {@link IAMArray#MODE_INT32}. */
	public static class INT32Array extends IAMArray implements Emuable {

		/** Dieses Feld speichert das leere {@link INT32Array}. */
		public static final INT32Array EMPTY = new INT32Array(new int[0]);

		final int[] array;

		final int offset;

		/** Dieser Konstruktor initialisiert das Array.
		 *
		 * @param array Array.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public INT32Array(final int[] array) throws NullPointerException {
			this(array, 0, array.length);
		}

		INT32Array(final int[] array, final int offset, final int length) throws IllegalArgumentException {
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
			if (length == 0) return INT32Array.EMPTY;
			return new INT32Array(this.array, this.offset + offset, length);
		}

		@Override
		protected boolean customIsCompact() {
			return (this.offset == 0) && (this.length == this.array.length);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + (this.customIsCompact() ? EMU.fromArray(this.array) : 0);
		}

		@Override
		public byte mode() {
			return IAMArray.MODE_INT32;
		}

	}

	/** Diese Klasse implementiert die Zahlenfolge für {@link IAMArray#MODE_UINT8}. */
	public static class UINT8Array extends INT8Array {

		/** Dieses Feld speichert das leere {@link UINT8Array}. */
		public static final UINT8Array EMPTY = new UINT8Array(new byte[0]);

		/** Dieser Konstruktor initialisiert das Array.
		 *
		 * @param array Array.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public UINT8Array(final byte[] array) throws NullPointerException {
			super(array);
		}

		UINT8Array(final byte[] array, final int offset, final int length) throws IllegalArgumentException {
			super(array, offset, length);

		}

		@Override
		protected int customGet(final int index) {
			return super.customGet(index) & 0xFF;
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			if (length == 0) return UINT8Array.EMPTY;
			return new UINT8Array(this.array, this.offset + offset, length);
		}

		@Override
		public byte mode() {
			return IAMArray.MODE_UINT8;
		}

		@Override
		public INT8Array compactINT8() {
			return this;
		}

	}

	/** Diese Klasse implementiert die Zahlenfolge für {@link IAMArray#MODE_UINT16}. */
	public static class UINT16Array extends INT16Array {

		/** Dieses Feld speichert das leere {@link UINT16Array}. */
		public static final UINT16Array EMPTY = new UINT16Array(new short[0]);

		/** Dieser Konstruktor initialisiert das Array.
		 *
		 * @param array Array.
		 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
		public UINT16Array(final short[] array) throws NullPointerException {
			super(array);
		}

		UINT16Array(final short[] array, final int offset, final int length) throws IllegalArgumentException {
			super(array, offset, length);
		}

		@Override
		protected int customGet(final int index) {
			return super.customGet(index) & 0xFFFF;
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			if (length == 0) return UINT16Array.EMPTY;
			return new UINT16Array(this.array, this.offset + offset, length);
		}

		@Override
		public byte mode() {
			return IAMArray.MODE_UINT16;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ConcatArray extends IAMArray {

		private static final byte[] MODES = { //
			IAMArray.MODE_INT8, IAMArray.MODE_INT16, IAMArray.MODE_INT32, IAMArray.MODE_INT16, IAMArray.MODE_INT32, //
			IAMArray.MODE_INT16, IAMArray.MODE_INT16, IAMArray.MODE_INT32, IAMArray.MODE_INT16, IAMArray.MODE_INT32, //
			IAMArray.MODE_INT32, IAMArray.MODE_INT32, IAMArray.MODE_INT32, IAMArray.MODE_INT32, IAMArray.MODE_INT32, //
			IAMArray.MODE_INT16, IAMArray.MODE_INT16, IAMArray.MODE_INT32, IAMArray.MODE_UINT8, IAMArray.MODE_UINT16, //
			IAMArray.MODE_INT32, IAMArray.MODE_INT32, IAMArray.MODE_INT32, IAMArray.MODE_UINT16, IAMArray.MODE_UINT16};

		public final IAMArray array1;

		public final IAMArray array2;

		public ConcatArray(final IAMArray array1, final IAMArray array2) throws IllegalArgumentException {
			super(array1.length + array2.length);
			this.array1 = array1;
			this.array2 = array2;
		}

		@Override
		public byte mode() {
			final int mode1 = this.array1.mode(), mode2 = this.array2.mode();
			return ConcatArray.MODES[(mode1 * 5) + mode2];
		}

		@Override
		protected int customGet(final int index) {
			final int index2 = index - this.array1.length;
			return index2 < 0 ? this.array1.customGet(index) : this.array2.customGet(index2);
		}

		@Override
		protected void customGet(final int index, final int[] array, final int offset, final int length) {
			final int count1 = this.array1.length - index, count2 = length - count1;
			if (count1 <= 0) {
				this.array2.customGet(-count1, array, offset, length);
			} else if (count2 <= 0) {
				this.array1.customGet(index, array, offset, length);
			} else {
				this.array1.customGet(index, array, offset, count1);
				this.array2.customGet(0, array, offset + count1, count2);
			}
		}

		@Override
		protected void customGet(final int index, final byte[] array, final int offset, final int length) {
			final int count1 = this.array1.length - index, count2 = length - count1;
			if (count1 <= 0) {
				this.array2.customGet(-count1, array, offset, length);
			} else if (count2 <= 0) {
				this.array1.customGet(index, array, offset, length);
			} else {
				this.array1.customGet(index, array, offset, count1);
				this.array2.customGet(0, array, offset + count1, count2);
			}
		}

		@Override
		protected void customGet(final int index, final short[] array, final int offset, final int length) {
			final int count1 = this.array1.length - index, count2 = length - count1;
			if (count1 <= 0) {
				this.array2.customGet(-count1, array, offset, length);
			} else if (count2 <= 0) {
				this.array1.customGet(index, array, offset, length);
			} else {
				this.array1.customGet(index, array, offset, count1);
				this.array2.customGet(0, array, offset + count1, count2);
			}
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			final int count1 = this.array1.length - offset, count2 = length - count1;
			if (count1 <= 0) return this.array2.section(-count1, length);
			if (count2 <= 0) return this.array1.section(offset, length);
			return this.array1.section(offset, count1).concat(this.array2.section(0, count2));
		}

		@Override
		protected boolean customEquals(final int index, final IAMArray that, final int offset, final int length) {
			final int count1 = this.array1.length - offset, count2 = length - count1;
			if (count1 <= 0) return this.array2.customEquals(-count1, that, offset, length);
			if (count2 <= 0) return this.array1.customEquals(index, that, offset, length);
			return this.array1.customEquals(index, that, offset, count1) && this.array2.customEquals(0, that, offset + count1, count2);
		}

		@Override
		protected int customCompare(final int index, final IAMArray that, final int offset, final int length) {
			final int count1 = this.array1.length - offset, count2 = length - count1;
			if (count1 <= 0) return this.array2.customCompare(-count1, that, offset, length);
			if (count2 <= 0) return this.array1.customCompare(index, that, offset, length);
			final int result = this.array1.customCompare(index, that, offset, count1);
			if (result != 0) return result;
			return this.array2.customCompare(0, that, offset + count1, count2);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class SectionArray extends IAMArray {

		public final IAMArray array;

		public final int offset;

		public SectionArray(final IAMArray array, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		public byte mode() {
			return this.array.mode();
		}

		@Override
		protected int customGet(final int index) {
			return this.array.customGet(this.offset + index);
		}

		@Override
		protected void customGet(final int index, final int[] array, final int offset, final int length) {
			this.array.customGet(this.offset + index, array, offset, length);
		}

		@Override
		protected void customGet(final int index, final byte[] array, final int offset, final int length) {
			this.array.customGet(this.offset + index, array, offset, length);
		}

		@Override
		protected void customGet(final int index, final short[] array, final int offset, final int length) {
			this.array.customGet(this.offset + index, array, offset, length);
		}

		@Override
		protected boolean customEquals(final int index, final IAMArray that, final int offset, final int length) {
			return that.customEquals(offset, this.array, this.offset + index, length);
		}

		@Override
		protected int customCompare(final int index, final IAMArray that, final int offset, final int length) {
			return -that.customCompare(offset, this.array, this.offset + index, length);
		}

		@Override
		protected IAMArray customSection(final int offset, final int length) {
			return this.array.customSection(this.offset + offset, length);
		}

	}

	/** Dieses Feld identifiziert die Kodierung für 8-Bit-Binärzahlen. Diese erlaubt Zahlen von {@code -128} bis {@code +127}. */
	public static final byte MODE_INT8 = 0;

	/** Dieses Feld identifiziert die Kodierung für 16-Bit-Binärzahlen. Diese erlaubt Zahlen von {@code -32768} bis {@code +32767}. */
	public static final byte MODE_INT16 = 1;

	/** Dieses Feld identifiziert die Kodierung für 32-Bit-Binärzahlen. */
	public static final byte MODE_INT32 = 2;

	/** Dieses Feld identifiziert die Kodierung für vorzeichenlose 8-Bit-Binärzahlen. Diese erlaubt Zahlen von {@code 0} bis {@code 255}. */
	public static final byte MODE_UINT8 = 3;

	/** Dieses Feld identifiziert die Kodierung für vorzeichenlose 16-Bit-Binärzahlen. Diese erlaubt Zahlen von {@code 0} bis {@code 65535}. */
	public static final byte MODE_UINT16 = 4;

	/** Dieses Feld speichert ein leeres {@link IAMArray}. */
	public static final IAMArray EMPTY = INT32Array.EMPTY;

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebene Zahlenfolge zurück. Änderungen am Inhalt von {@code array} werden auf das
	 * gelieferte {@link IAMArray} übertragen!
	 *
	 * @param array Zahlenfolge.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static INT32Array from(final int... array) throws NullPointerException {
		return IAMArray.from(array, 0, array.length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} mit den gegebene Zahlen zurück. Änderungen am Inhalt von {@code array} werden nicht auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray} aus den {@link Number#intValue()}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static INT32Array from(final Number[] array) throws NullPointerException {
		return IAMArray.from(Arrays.asList(array));
	}

	/** Diese Methode gibt ein neues {@link IAMArray} mit den gegebene Zahlen zurück. Änderungen am Inhalt von {@code array} werden nicht auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray} aus den {@link Number#intValue()}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static INT32Array from(final List<? extends Number> array) throws NullPointerException {
		final int length = array.size();
		final int[] result = new int[length];
		int offset = 0;
		for (final Number item: array) {
			result[offset++] = item.intValue();
		}
		return IAMArray.from(result);
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
	public static INT32Array from(final int[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return INT32Array.EMPTY;
		return new INT32Array(array, offset, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, byte[]) IAMArray.from(true, array)}. */
	public static INT8Array from(final byte[] array) throws NullPointerException {
		return IAMArray.from(true, array);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[], int, int) IAMArray.from(signed, array, 0, array.length)}. */
	public static INT8Array from(final boolean signed, final byte[] array) throws NullPointerException {
		return IAMArray.from(signed, array, 0, array.length);
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
	public static INT8Array from(final byte[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return INT8Array.EMPTY;
		return new INT8Array(array, offset, length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf einen Abschnitt der gegebenen Zahlenfolge zurück. Änderungen am Inhalt von {@code array}
	 * werden auf das gelieferte {@link IAMArray} übertragen!
	 *
	 * @param signed {@code true}, für {@link #MODE_INT8}-Kodierung; {@code false} für {@link #MODE_UINT8}-Kodierung.
	 * @param array Zahlenfolge.
	 * @param offset Beginn der Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return {@link IAMArray}-Sicht auf einen Abschnitt von {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebene Abschnitt nicht in {@code array} liegt. */
	public static INT8Array from(final boolean signed, final byte[] array, final int offset, final int length)
		throws NullPointerException, IllegalArgumentException {
		if (signed) return IAMArray.from(array, offset, length);
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return UINT8Array.EMPTY;
		return new UINT8Array(array, offset, length);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[]) IAMArray.from(true, array)}. */
	public static INT16Array from(final short[] array) throws NullPointerException {
		return IAMArray.from(true, array);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[], int, int) IAMArray.from(signed, array, 0, array.length)}. */
	public static INT16Array from(final boolean signed, final short[] array) throws NullPointerException {
		return IAMArray.from(signed, array, 0, array.length);
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
	public static INT16Array from(final short[] array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return INT16Array.EMPTY;
		return new INT16Array(array, offset, length);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf einen Abschnitt der gegebenen Zahlenfolge zurück. Änderungen am Inhalt von {@code array}
	 * werden auf das gelieferte {@link IAMArray} übertragen!
	 *
	 * @param signed {@code true}, für {@link #MODE_INT16}-Kodierung; {@code false} für {@link #MODE_UINT16}-Kodierung.
	 * @param array Zahlenfolge.
	 * @param offset Beginn der Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return {@link IAMArray}-Sicht auf einen Abschnitt von {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebene Abschnitt nicht in {@code array} liegt. */
	public static INT16Array from(final boolean signed, final short[] array, final int offset, final int length)
		throws NullPointerException, IllegalArgumentException {
		if (signed) return IAMArray.from(array, offset, length);
		if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IllegalArgumentException();
		if (length == 0) return UINT16Array.EMPTY;
		return new UINT16Array(array, offset, length);
	}

	/** Dieses Feld speichert die Länge. */
	public final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge. */
	protected IAMArray(final int length) throws IllegalArgumentException {
		if ((length & 0xC0000000) != 0) throw new IllegalArgumentException();
		this.length = length;
	}

	/** Diese Methode implementiert {@link #get(int)} ohne Parameterprüfung.
	 *
	 * @param index Index.
	 * @return {@code index}-te Zahl. */
	protected abstract int customGet(final int index);

	/** Diese Methode implementiert {@link #get(int[], int)} ohne Parameterprüfung.
	 *
	 * @param index Beginn des Bereichs in {@code this}.
	 * @param array Array, in welches die Zahlen geschrieben werden.
	 * @param offset Beginn des Bereichs in {@code array}.
	 * @param length Länge des Bereichs in {@code array}. */
	protected void customGet(final int index, final int[] array, final int offset, final int length) {
		for (int i1 = index, i2 = offset, l = length; 0 < l; i2++, i1++, l--) {
			array[i2] = this.customGet(i1);
		}
	}

	/** Diese Methode implementiert {@link #get(byte[], int)} ohne Parameterprüfung.
	 *
	 * @param index Beginn des Bereichs in {@code this}.
	 * @param array Array, in welches die Zahlen geschrieben werden.
	 * @param offset Beginn des Bereichs in {@code array}.
	 * @param length Länge des Bereichs in {@code array}. */
	protected void customGet(final int index, final byte[] array, final int offset, final int length) {
		for (int i1 = index, i2 = offset, l = length; 0 < l; i2++, i1++, l--) {
			array[i2] = (byte)this.customGet(i1);
		}
	}

	/** Diese Methode implementiert {@link #get(short[], int)} ohne Parameterprüfung.
	 *
	 * @param index Beginn des Bereichs in {@code this}.
	 * @param array Array, in welches die Zahlen geschrieben werden.
	 * @param offset Beginn des Bereichs in {@code array}.
	 * @param length Länge des Bereichs in {@code array}. */
	protected void customGet(final int index, final short[] array, final int offset, final int length) {
		for (int i1 = index, i2 = offset, l = length; 0 < l; i2++, i1++, l--) {
			array[i2] = (short)this.customGet(i1);
		}
	}

	/** Diese Methode implementiert {@link #equals(IAMArray)} ohne Parameterprüfung.
	 *
	 * @param index Beginn des Bereichs in {@code this}.
	 * @param that Zahlenfolge, mit welcher diese vergleichen wird.
	 * @param offset Beginn des Bereichs in {@code array}.
	 * @param length Länge des Bereichs in {@code array}.
	 * @return Vergleichswert. */
	protected boolean customEquals(final int index, final IAMArray that, final int offset, final int length) {
		for (int i1 = index, i2 = offset, l = length; 0 < l; i2++, i1++, l--) {
			if (this.customGet(i1) != that.customGet(i2)) return false;
		}
		return true;
	}

	/** Diese Methode implementiert {@link #compare(IAMArray)} ohne Parameterprüfung.
	 *
	 * @param index Beginn des Bereichs in {@code this}.
	 * @param that Zahlenfolge, mit welcher diese vergleichen wird.
	 * @param offset Beginn des Bereichs in {@code array}.
	 * @param length Länge des Bereichs in {@code array}.
	 * @return Vergleichswert. */
	protected int customCompare(final int index, final IAMArray that, final int offset, final int length) {
		for (int i1 = index, i2 = offset, l = length; 0 < l; i2++, i1++, l--) {
			final int v1 = this.customGet(i1), v2 = that.customGet(i2);
			if (v1 < v2) return -1;
			if (v1 > v2) return +1;
		}
		return 0;
	}

	/** Diese Methode implementiert {@link #section(int, int)} ohne Parameterprüfung. */
	protected IAMArray customSection(final int offset, final int length) {
		return new SectionArray(this, offset, length);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn {@link #compact()} {@code this} liefern soll.
	 *
	 * @return Kompaktheit. */
	protected boolean customIsCompact() {
		return false;
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

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code int[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der Länge dieser Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void get(final int[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		this.customGet(0, result, offset, length);
	}

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code byte[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der Länge dieser Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void get(final byte[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		this.customGet(0, result, offset, length);
	}

	/** Diese Methode kopiert diese Zahlenfolge an die gegebene Position in das gegebenen {@code chort[]}.
	 *
	 * @param result Ergebnis.
	 * @param offset Beginn des Bereichs mit der Länge dieser Zahlenfolge.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn diese Zahlenfolge nicht in den gegebenen Bereich passt. */
	public void get(final short[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		final int length = this.length;
		if ((offset < 0) || ((offset + length) > result.length)) throw new IllegalArgumentException();
		this.customGet(0, result, offset, length);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge zurück ({@code 0..1073741823}).
	 *
	 * @see #get(int)
	 * @return Länge. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt die Kodierung der Zahl dieser Zahlenfolge zurück.
	 *
	 * @see #MODE_INT8
	 * @see #MODE_INT16
	 * @see #MODE_INT32
	 * @see #MODE_UINT8
	 * @see #MODE_UINT16
	 * @return Zahlenkodierung. */
	public abstract byte mode();

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
		return (this.length == that.length) && this.customEquals(0, that, 0, this.length);
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
	public final int compare(final IAMArray that) throws NullPointerException {
		final int l1 = this.length, l2 = that.length;
		if (l1 < l2) {
			final int result = this.customCompare(0, that, 0, l1);
			return result != 0 ? result : -1;
		}
		if (l1 > l2) {
			final int result = this.customCompare(0, that, 0, l2);
			return result != 0 ? result : +1;
		}
		return this.customCompare(0, that, 0, l2);
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

	/** Diese Methode gibt die Zahlen dieser Zahlenfolge Abhängig von ihrer {@link #mode() Kodierung} in einer performanteren oder zumindest gleichwertigen
	 * Zahlenfolge zurück.
	 *
	 * @see #concat(IAMArray)
	 * @see #section(int, int)
	 * @return performantere Zahlenfolge oder {@code this}. */
	public IAMArray compact() {
		if (this.customIsCompact()) return this;
		switch (this.mode()) {
			case MODE_INT8:
				return this.compactINT8();
			case MODE_INT16:
				return this.compactINT16();
			case MODE_UINT8:
				return this.compactUINT8();
			case MODE_UINT16:
				return this.compactUINT16();
			default:
				return this.compactINT32();
		}
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, byte[]) IAMArray.from(true, this.toBytes())}. */
	public INT8Array compactINT8() {
		return IAMArray.from(true, this.toBytes());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[]) IAMArray.from(true, this.toShorts())}. */
	public INT16Array compactINT16() {
		return IAMArray.from(true, this.toShorts());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(int[]) IAMArray.from(this.toInts())}. */
	public INT32Array compactINT32() {
		return IAMArray.from(this.toInts());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, byte[]) IAMArray.from(false, this.toBytes())}. */
	public INT8Array compactUINT8() {
		return IAMArray.from(false, this.toBytes());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, short[]) IAMArray.from(false, this.toShorts())}. */
	public INT16Array compactUINT16() {
		return IAMArray.from(false, this.toShorts());
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
		return (offset < 0) || (length <= 0) || ((offset + length) > this.length) ? this.customSection(0, 0) : this.customSection(offset, length);
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code int[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public int[] toInts() {
		final int[] result = new int[this.length];
		this.get(result, 0);
		return result;
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code byte[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public byte[] toBytes() {
		final byte[] result = new byte[this.length];
		this.get(result, 0);
		return result;
	}

	/** Diese Methode gibt eine Kopie der gegebenen Zahlenfolge als {@code short[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public short[] toShorts() {
		final short[] result = new short[this.length];
		this.get(result, 0);
		return result;
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
	public final int hashCode() {
		int hash = Objects.hashInit();
		for (int i = 0, l = this.length; l != 0; ++i, --l) {
			hash = Objects.hashPush(hash, this.customGet(i));
		}
		return hash;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		return (object == this) || ((object instanceof IAMArray) && this.equals((IAMArray)object));
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final IAMArray object) {
		return this.compare(object);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.length > 30 ? //
			Objects.formatIterable(false, Iterables.chainedIterable(this.section(0, 15), Iterables.itemIterable(Objects.toStringObject("...")))) : //
			Objects.formatIterable(false, this);
	}

}
