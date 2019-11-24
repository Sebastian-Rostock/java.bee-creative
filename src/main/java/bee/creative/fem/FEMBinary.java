package bee.creative.fem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMArray;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators.BaseIterator;

/** Diese Klasse implementiert eine unveränderliche Bytefolge sowie Methoden zur Erzeugung solcher Bytefolgen aus nativen Arrays.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMBinary extends FEMValue implements Iterable<Byte>, Comparable<FEMBinary>, UseToString {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Bytes einer Bytefolge in der Methode {@link FEMBinary#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Bytewert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 *
		 * @param value Byte.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(byte value);

	}

	static class ItemFinder implements Collector {

		public final byte that;

		public int index;

		ItemFinder(final byte that) {
			this.that = that;
		}

		@Override
		public boolean push(final byte value) {
			if (value == this.that) return false;
			this.index++;
			return true;
		}

	}

	static class HashCollector implements Collector {

		public int hash = Objects.hashInit();

		@Override
		public boolean push(final byte value) {
			this.hash = Objects.hashPush(this.hash, value);
			return true;
		}

	}

	static class ValueCollector implements Collector {

		public final byte[] array;

		public int index;

		public ValueCollector(final byte[] array, final int index) {
			this.array = array;
			this.index = index;
		}

		@Override
		public boolean push(final byte value) {
			this.array[this.index++] = value;
			return true;
		}

	}

	static class StringCollector implements Collector {

		public final char[] array;

		public int index;

		StringCollector(final boolean header, final int length) {
			if (header) {
				this.array = new char[(length << 1) + 2];
				this.array[0] = '0';
				this.array[1] = 'x';
				this.index = 2;
			} else {
				this.array = new char[length << 1];
			}
		}

		@Override
		public boolean push(final byte value) {
			int index = this.index;
			this.array[index] = FEMBinary.toChar((value >> 4) & 0xF);
			++index;
			this.array[index] = FEMBinary.toChar((value >> 0) & 0xF);
			++index;
			this.index = index;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ArrayBinary extends FEMBinary {

		public final IAMArray array;

		public final int offset;

		ArrayBinary(final IAMArray array) throws NullPointerException, IllegalArgumentException {
			this(array, 4, array.length() - 4, Integers.toInt(array.get(3), array.get(2), array.get(1), array.get(0)));
		}

		public ArrayBinary(final IAMArray array, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
			super(length);
			this.array = Objects.notNull(array);
			this.offset = offset;
		}

		public ArrayBinary(final IAMArray array, final int offset, final int length, final int hash) throws NullPointerException, IllegalArgumentException {
			this(array, offset, length);
			this.hash = hash;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)this.array.get(this.offset + index);
		}

		@Override
		protected FEMBinary customSection(final int offset, final int length) {
			return new ArrayBinary(this.array, this.offset + offset, length);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class BufferBinary extends FEMBinary {

		public final ByteBuffer buffer;

		BufferBinary(final ByteBuffer buffer) {
			super(buffer.limit());
			this.buffer = buffer;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.buffer.get(index);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class EmptyBinary extends FEMBinary {

		EmptyBinary() {
			super(0);
		}

		@Override
		public FEMBinary reverse() {
			return this;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class IntegerBinaryBE extends FEMBinary {

		public final long value;

		IntegerBinaryBE(final int length, final long value) {
			super(length);
			this.value = value;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)(this.value >> ((this.length - index - 1) << 3));
		}

		@Override
		public FEMBinary reverse() {
			return new IntegerBinaryLE(this.length, this.value);
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class IntegerBinaryLE extends FEMBinary {

		public final long value;

		IntegerBinaryLE(final int length, final long value) {
			super(length);
			this.value = value;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)(this.value >> (index << 3));
		}

		@Override
		public FEMBinary reverse() {
			return new IntegerBinaryBE(this.length, this.value);
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ConcatBinary extends FEMBinary implements Emuable {

		public final FEMBinary binary1;

		public final FEMBinary binary2;

		ConcatBinary(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1.length + binary2.length);
			this.binary1 = binary1;
			this.binary2 = binary2;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.binary1.length;
			return index2 < 0 ? this.binary1.customGet(index) : this.binary2.customGet(index2);
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.binary1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.binary2.customExtract(target, offset2, length, foreward);
			if (length2 <= 0) return this.binary1.customExtract(target, offset, length, foreward);
			if (foreward) {
				if (!this.binary1.customExtract(target, offset, -offset2, foreward)) return false;
				return this.binary2.customExtract(target, 0, length2, foreward);
			} else {
				if (!this.binary2.customExtract(target, 0, length2, foreward)) return false;
				return this.binary1.customExtract(target, offset, -offset2, foreward);
			}
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.binary1) + EMU.from(this.binary2);
		}

		@Override
		public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.binary1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.binary2.section(offset2, length);
			if (length2 <= 0) return this.binary1.section(offset, length);
			return this.binary1.section(offset, -offset2).concat(this.binary2.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	public static class SectionBinary extends FEMBinary implements Emuable {

		public final FEMBinary binary;

		public final int offset;

		SectionBinary(final FEMBinary binary, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.binary = binary;
			this.offset = offset;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.binary.customGet(index + this.offset);
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.binary.customExtract(target, this.offset + offset2, length2, foreward);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.binary);
		}

		@Override
		public FEMBinary section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.binary.section(this.offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ReverseBinary extends FEMBinary implements Emuable {

		public final FEMBinary binary;

		ReverseBinary(final FEMBinary binary) throws IllegalArgumentException {
			super(binary.length);
			this.binary = binary;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.binary.customGet(this.length - index - 1);
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.binary.customExtract(target, this.length - offset - length, length, !foreward);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.binary);
		}

		@Override
		public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			return this.binary.section(this.length - offset - length, length).reverse();
		}

		@Override
		public FEMBinary reverse() {
			return this.binary;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class UniformBinary extends FEMBinary {

		public final byte item;

		UniformBinary(final int length, final byte item) throws IllegalArgumentException {
			super(length);
			this.item = item;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.item;
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.item)) return false;
				length--;
			}
			return true;
		}

		@Override
		public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			if ((offset == 0) && (length == this.length)) return this;
			if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
			if (length == 0) return FEMBinary.EMPTY;
			return new UniformBinary(length, this.item);
		}

		@Override
		public FEMBinary reverse() {
			return this;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class CompactBinary extends FEMBinary implements Emuable {

		/** Dieses Feld speichert das Array der Bytes, das nicht verändert werden sollte. */
		final byte[] items;

		CompactBinary(final byte[] items) throws IllegalArgumentException {
			super(items.length);
			this.items = items;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.items[index];
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.items);
		}

		@Override
		public byte[] value() {
			return this.items.clone();
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 5;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMBinary> TYPE = FEMType.from(FEMBinary.ID);

	/** Dieses Feld speichert die leere Bytefolge. */
	public static final FEMBinary EMPTY = new EmptyBinary();

	/** Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück. Das gegebene Array wird falls nötig kopiert.
	 *
	 * @param items Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMBinary from(final byte[] items) throws NullPointerException {
		if (items.length == 0) return FEMBinary.EMPTY;
		if (items.length == 1) return new UniformBinary(1, items[0]);
		return new CompactBinary(items.clone());
	}

	/** Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMBinary from(final boolean copy, final byte[] items) throws NullPointerException {
		if (copy) return FEMBinary.from(items);
		return new CompactBinary(items);
	}

	/** Diese Methode gibt eine Bytefolge mit den Bytes im gegebenen Abschnitt zurück. Der gegebene Abschnitt wird falls nötig kopiert.
	 *
	 * @param items Bytes.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMBinary from(final byte[] items, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return new UniformBinary(1, items[offset]);
		final byte[] result = new byte[length];
		System.arraycopy(items, offset, result, 0, length);
		return new CompactBinary(result);
	}

	/** Diese Methode gibt eine uniforme Bytefolge mit der gegebenen Länge zurück, deren Bytes alle gleich dem gegebenen sind.
	 *
	 * @param item Byte.
	 * @param length Länge.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMBinary from(final byte item, final int length) throws IllegalArgumentException {
		if (length == 0) return FEMBinary.EMPTY;
		return new UniformBinary(length, item);
	}

	/** Diese Methode gibt eine neue Bytefolge mit dem in der gegebenen Zeichenkette kodierten Wert zurück. Das Format der Zeichenkette entspricht dem der
	 * {@link #toString() Textdarstellung}.
	 *
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMBinary from(final String string) throws NullPointerException, IllegalArgumentException {
		return FEMBinary.from(string, true);
	}

	/** Diese Methode gibt eine neue Bytefolge mit dem in der gegebenen Zeichenkette kodierten Wert zurück. Das Format der Zeichenkette entspricht dem der
	 * {@link #toString(boolean) Textdarstellung}.
	 *
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @param header {@code true}, wenn die Zeichenkette mit {@code "0x"} beginnt.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMBinary from(final String string, final boolean header) throws NullPointerException, IllegalArgumentException {
		int count = string.length(), index = 0;
		if ((count & 1) != 0) throw new IllegalArgumentException();
		if (header) {
			if ((count < 2) || (string.charAt(0) != '0') || (string.charAt(1) != 'x')) throw new IllegalArgumentException();
			index += 2;
			count -= 2;
		}
		count >>= 1;
		if (count == 0) return FEMBinary.EMPTY;
		final byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++, index += 2) {
			bytes[i] = (byte)((FEMBinary.toDigit(string.charAt(index + 0)) << 4) | (FEMBinary.toDigit(string.charAt(index + 1)) << 0));
		}
		if (count == 1) return new UniformBinary(1, bytes[0]);
		return new CompactBinary(bytes);
	}

	/** Diese Methode überführt die gegebene Dezimalzahl interpretiert als Binärzahl in eine Bytefolge mit der gegebenen Länge sowie der gegebenen Bytereihenfolge
	 * und gibt diese zurück. Bei einer Länge von {@code 3} Byte hat die Dezimalzahl {@code 16909060} ({@code 0x01020304}) die Bytefolge {@code 0x020304} in
	 * {@link ByteOrder#BIG_ENDIAN big-endian} und die die Bytefolge {@code 0x040302} in {@link ByteOrder#LITTLE_ENDIAN little-endian}.
	 *
	 * @see #toInteger(boolean)
	 * @param value Dezimalzahl.
	 * @param length Länge ({@code 0..8})
	 * @param bigEndian {@code true} für {@link ByteOrder#BIG_ENDIAN big-endian}; {@code false} für als {@link ByteOrder#LITTLE_ENDIAN little-endian}.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn die Länge ungültig ist. */
	public static FEMBinary from(final long value, final int length, final boolean bigEndian) throws IllegalArgumentException {
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return new UniformBinary(1, (byte)value);
		if ((length < 0) || (length > 8)) throw new IllegalArgumentException();
		return bigEndian ? new IntegerBinaryBE(length, value) : new IntegerBinaryLE(length, value);
	}

	/** Diese Methode gibt eine Bytefolge mit den gegebenen Zahlen zurück.
	 *
	 * @param buffer Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code buffer} {@code null} ist. */
	public static FEMBinary from(final ByteBuffer buffer) throws NullPointerException {
		if (buffer.limit() == 0) return FEMBinary.EMPTY;
		if (buffer.limit() == 1) return new UniformBinary(1, buffer.get(0));
		return new BufferBinary(buffer);
	}

	/** Diese Methode konvertiert die gegebenen Zahlen in eine Bytefolge und gibt diese zurück.
	 *
	 * @see #from(byte[])
	 * @see Number#byteValue()
	 * @param items Zahlen.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMBinary from(final List<? extends Number> items) throws NullPointerException {
		final int length = items.size();
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return FEMBinary.from(items.get(0).byteValue(), 1);
		final byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = items.get(i).byteValue();
		}
		return FEMBinary.from(false, result);
	}

	/** Diese Methode konvertiert die gegebenen Zahlen in eine Bytefolge und gibt diese zurück.
	 *
	 * @see #from(List)
	 * @see Iterables#toList(Iterable)
	 * @param items Zahlen.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMBinary from(final Iterable<? extends Number> items) throws NullPointerException {
		if (items instanceof FEMBinary) return (FEMBinary)items;
		return FEMBinary.from(Iterables.toList(items));
	}

	/** Diese Methode interpretiert die gegebene Zahlenfolge als Bytefolge und gibt diese zurück. Die ersten vier Byte der Zahlenfolge werden als
	 * {@link #hashCode() Streuwert} und die darauf folgenden Zahlenwerte als Auflistung der Bytes interpretiert. Die {@link IAMArray#mode() Kodierung der
	 * Zahlenwerte} muss eine 8-Bit-Kodierung anzeigen.
	 *
	 * @param array Zahlenfolge.
	 * @return {@link FEMBinary}-Sicht auf die gegebene Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMBinary from(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		final int mode = array.mode();
		if ((mode != IAMArray.MODE_INT8) && (mode != IAMArray.MODE_UINT8)) throw new IllegalArgumentException();
		return new ArrayBinary(array);
	}

	/** Diese Methode gibt die Verkettung der gegebenen Bytefolgen zurück.
	 *
	 * @see #concat(FEMBinary)
	 * @param values Bytefolgen.
	 * @return Verkettung der Bytefolgen.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public static FEMBinary concatAll(final FEMBinary... values) throws NullPointerException {
		final int length = values.length;
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return values[0].data();
		return FEMBinary.concatAll(values, 0, length - 1);
	}

	static FEMBinary concatAll(final FEMBinary[] values, final int min, final int max) throws NullPointerException {
		if (min == max) return values[min];
		final int mid = (min + max) >> 1;
		return FEMBinary.concatAll(values, min, mid).concat(FEMBinary.concatAll(values, mid + 1, max));
	}

	/** Diese Methode gibt das Zeichen zur gegebenen hexadezimalen Ziffer zurück.
	 *
	 * @param hexDigit hexadezimale Ziffer ({@code 0..15}).
	 * @return Zeichen ({@code '0'..'9', 'A'..'F'}).
	 * @throws IllegalArgumentException Wenn {@code hexDigit} ungültig ist. */
	public static char toChar(final int hexDigit) throws IllegalArgumentException {
		final int letter = hexDigit - 10;
		if (letter >= 6) throw new IllegalArgumentException("hexDigit > 15");
		if (letter >= 0) return (char)('A' + letter);
		if (hexDigit >= 0) return (char)('0' + hexDigit);
		throw new IllegalArgumentException("hexDigit < 0");
	}

	/** Diese Methode gibt die hexadezimale Ziffer zum gegebenen Zeichen zurück.
	 *
	 * @param hexChar Zeichen ({@code '0'..'9', 'A'..'F'}).
	 * @return hexadezimale Ziffer ({@code 0..15}).
	 * @throws IllegalArgumentException Wenn {@code hexChar} ungültig ist. */
	public static int toDigit(final int hexChar) throws IllegalArgumentException {
		final int digit = hexChar - '0';
		if (digit < 0) throw new IllegalArgumentException("hexChar < '0'");
		if (digit <= 9) return digit;
		final int lower = hexChar - 'a';
		if (lower > 5) throw new IllegalArgumentException("hexChar > 'f'");
		if (lower >= 0) return lower + 10;
		final int upper = hexChar - 'A';
		if (upper < 0) throw new IllegalArgumentException("'9' < hexChar < 'A'");
		if (upper <= 5) return upper + 10;
		throw new IllegalArgumentException("'F' < hexChar < 'a'");
	}

	/** Dieses Feld speichert den Streuwert oder {@code 0}. Es wird in {@link #hashCode()} initialisiert. */
	protected int hash;

	/** Dieses Feld speichert die Länge. */
	protected final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	protected FEMBinary(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException();
		this.length = length;
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMBinary data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMBinary> type() {
		return FEMBinary.TYPE;
	}

	/** Diese Methode gibt das {@code index}-te Byte zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Byte. */
	protected byte customGet(final int index) {
		return 0;
	}

	/** Diese Methode fügt alle Bytes im gegebenen Abschnitt in der gegebenen Reihenfolge geordnet an den gegebenen {@link Collector} an. Das Anfügen wird
	 * vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reihenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde. */
	protected boolean customExtract(final Collector target, int offset, int length, final boolean foreward) {
		if (foreward) {
			for (length += offset; offset < length; offset++) {
				if (!target.push(this.customGet(offset))) return false;
			}
		} else {
			for (length += offset - 1; offset <= length; length--) {
				if (!target.push(this.customGet(length))) return false;
			}
		}
		return true;
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Bytefolge zurück.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMBinary}-Sicht auf einen Abschnitt dieser Bytefolge. */
	protected FEMBinary customSection(final int offset, final int length) {
		return new SectionBinary(this, offset, length);
	}

	/** Diese Methode konvertiert diese Bytefolge in ein {@code byte[]} und gibt dieses zurück.
	 *
	 * @return Array mit den Bytes dieser Bytefolge. */
	public byte[] value() {
		final ValueCollector target = new ValueCollector(new byte[this.length], 0);
		this.extract(target);
		return target.array;
	}

	/** Diese Methode gibt das {@code index}-te Byte zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Byte.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public final byte get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.customGet(index);
	}

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Bytes in der Bytefolge zurück.
	 *
	 * @return Länge der Bytefolge. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Bytefolge mit der gegebenen Bytefolge zurück.
	 *
	 * @param that Bytefolge.
	 * @return {@link FEMBinary}-Sicht auf die Verkettung dieser mit der gegebenen Bytefolge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public FEMBinary concat(final FEMBinary that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
		return new ConcatBinary(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #section(int, int) this.section(offset, this.length() - offset)}.
	 *
	 * @see #length() */
	public FEMBinary section(final int offset) throws IllegalArgumentException {
		return this.section(offset, this.length - offset);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Bytefolge zurück.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMBinary}-Sicht auf einen Abschnitt dieser Bytefolge.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Bytefolge liegt oder eine negative Länge hätte. */
	public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMBinary.EMPTY;
		return this.customSection(offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Bytefolge zurück.
	 *
	 * @return rückwärts geordnete {@link FEMBinary}-Sicht auf diese Bytefolge. */
	public FEMBinary reverse() {
		if (this.length < 2) return this;
		return new ReverseBinary(this);
	}

	/** Diese Methode gibt die {@link #value() Bytes dieser Bytefolge} in einer performanteren oder zumindest gleichwertigen Bytefolge zurück.
	 *
	 * @see #from(byte[])
	 * @see #value()
	 * @return performantere Bytefolge oder {@code this}. */
	public FEMBinary compact() {
		final FEMBinary result = this.length == 1 ? new UniformBinary(1, this.customGet(0)) : new CompactBinary(this.value());
		result.hash = this.hash;
		return result;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Bytewerts innerhalb dieser Bytefolge zurück. Die Suche beginnt an der gegebenen
	 * Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchter Bytewert.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Bytewerts ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final byte that, final int offset) throws IllegalArgumentException {
		final int length = this.length - offset;
		if (length == 0) return -1;
		if ((offset < 0) || (length < 0)) throw new IllegalArgumentException();
		final ItemFinder finder = new ItemFinder(that);
		if (this.customExtract(finder, offset, length, true)) return -1;
		return finder.index + offset;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Bytefolge innerhalb dieser Bytefolge zurück. Die Suche beginnt an der gegebenen
	 * Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchte Bytefolge.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Bytefolge ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMBinary that, final int offset) throws NullPointerException, IllegalArgumentException {
		final int count = that.length;
		if (count == 1) return this.find(that.customGet(0), offset);
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (count == 0) return offset;
		final int value = that.customGet(0);
		final int length = (this.length - count) + 1;
		FIND: for (int i = offset; i < length; i++) {
			if (value == this.customGet(i)) {
				for (int i2 = 1; i2 < count; i2++) {
					if (this.customGet(i + i2) != that.customGet(i2)) {
						continue FIND;
					}
				}
				return i;
			}
		}
		return -1;
	}

	/** Diese Methode fügt alle Bytes dieser Bytefolge vom ersten zum letzten geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig
	 * abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	public final boolean extract(final Collector target) throws NullPointerException {
		Objects.notNull(target);
		if (this.length == 0) return true;
		return this.customExtract(target, 0, this.length, true);
	}

	/** Diese Methode kopiert alle Bytes dieser Bytefolge vom ersten zum letzten geordnet in den an der gegebenen Position beginnenden Abschnitt des gegebenen
	 * Arrays.
	 *
	 * @param result Array, in welchem der Abschnitt liegt.
	 * @param offset Beginn des Abschnitts.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschitt außerhalb des gegebenen Arrays liegt. */
	public final void extract(final byte[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || ((offset + this.length) > result.length)) throw new IllegalArgumentException();
		this.extract(new ValueCollector(result, offset));
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge gleich der gegebenen ist.
	 *
	 * @param that Bytefolge.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMBinary that) throws NullPointerException {
		final int length = this.length;
		if (length != that.length) return false;
		if (this.hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this.customGet(i) != that.customGet(i)) return false;
		}
		return true;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Bytefolge kleiner, gleich oder größer als die
	 * der gegebenen Bytefolge ist. Die Bytewerte werden als {@code UINT8} verglichen.
	 *
	 * @param that Bytefolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final int compare(final FEMBinary that) throws NullPointerException {
		final int length = Math.min(this.length, that.length);
		for (int i = 0; i < length; i++) {
			final int result = Comparators.compare(this.customGet(i) & 255, that.customGet(i) & 255);
			if (result != 0) return result;
		}
		return Comparators.compare(this.length, that.length);
	}

	/** Diese Methode gibt die Textdarstellung dieser Bytefolge zurück. Die Textdarstellung besteht aus der Zeichenkette {@code "0x"} (header) und den Bytes
	 * dieser Bytefolge vom ersten zum letzten geordnet in hexadezimalen Ziffern, d.h. {@code 0123456789ABCDEF}.
	 *
	 * @param header {@code true}, wenn die Zeichenkette mit {@code "0x"} beginnen soll.
	 * @return Textdarstellung. */
	public final String toString(final boolean header) {
		final StringCollector target = new StringCollector(header, this.length);
		this.extract(target);
		return new String(target.array, 0, target.array.length);
	}

	/** Diese Methode interpretiert diese Bytefolge als Binärzahl mit der gegebenen Bytereihenfolge und gibt diese als Dezimalzahl zurück.
	 *
	 * @see #from(long, int, boolean)
	 * @param bigEndian {@code true} für {@link ByteOrder#BIG_ENDIAN big-endian} und {@code false} für als {@link ByteOrder#LITTLE_ENDIAN little-endian}.
	 * @return Dezimalzahl.
	 * @throws IllegalStateException Wenn die Länge der Bytefolge größer als {@code 8} ist. */
	public final long toInteger(final boolean bigEndian) throws IllegalStateException {
		if (bigEndian) {
			switch (this.length) {
				case 0:
					return 0;
				case 1:
					return this.customGet(0);
				case 2:
					return Integers.toShort(this.customGet(0), this.customGet(1));
				case 3:
					return Integers.toInt(0, this.customGet(0), this.customGet(1), this.customGet(2));
				case 4:
					return Integers.toInt(this.customGet(0), this.customGet(1), this.customGet(2), this.customGet(3));
				case 5:
					return Integers.toLong(this.customGet(0), Integers.toInt(this.customGet(1), this.customGet(2), this.customGet(3), this.customGet(4)));
				case 6:
					return Integers.toLong(Integers.toShort(this.customGet(0), this.customGet(1)),
						Integers.toInt(this.customGet(2), this.customGet(3), this.customGet(4), this.customGet(5)));
				case 7:
					return Integers.toLong(Integers.toInt(0, this.customGet(0), this.customGet(1), this.customGet(2)),
						Integers.toInt(this.customGet(3), this.customGet(4), this.customGet(5), this.customGet(6)));
				case 8:
					return Integers.toLong(Integers.toInt(this.customGet(0), this.customGet(1), this.customGet(2), this.customGet(3)),
						Integers.toInt(this.customGet(4), this.customGet(5), this.customGet(6), this.customGet(7)));
			}
		} else {
			switch (this.length) {
				case 0:
					return 0;
				case 1:
					return this.customGet(0);
				case 2:
					return Integers.toShort(this.customGet(1), this.customGet(0));
				case 3:
					return Integers.toInt(0, this.customGet(2), this.customGet(1), this.customGet(0));
				case 4:
					return Integers.toInt(this.customGet(3), this.customGet(2), this.customGet(1), this.customGet(0));
				case 5:
					return Integers.toLong(this.customGet(4), Integers.toInt(this.customGet(3), this.customGet(2), this.customGet(1), this.customGet(0)));
				case 6:
					return Integers.toLong(Integers.toShort(this.customGet(5), this.customGet(4)),
						Integers.toInt(this.customGet(3), this.customGet(2), this.customGet(1), this.customGet(0)));
				case 7:
					return Integers.toLong(Integers.toInt(0, this.customGet(6), this.customGet(5), this.customGet(4)),
						Integers.toInt(this.customGet(3), this.customGet(2), this.customGet(1), this.customGet(0)));
				case 8:
					return Integers.toLong(Integers.toInt(this.customGet(7), this.customGet(6), this.customGet(5), this.customGet(4)),
						Integers.toInt(this.customGet(3), this.customGet(2), this.customGet(1), this.customGet(0)));
			}
		}
		throw new IllegalStateException();
	}

	/** {@inheritDoc} */
	@Override
	public final FEMBinary result(final boolean deep) {
		return deep ? this.compact() : this;
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		int result = this.hash;
		if (result != 0) return result;
		final HashCollector hasher = new HashCollector();
		this.extract(hasher);
		result = hasher.hash;
		return this.hash = result != 0 ? result : 1;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBinary)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).result().data();
			if (!(object instanceof FEMBinary)) return false;
		}
		return this.equals((FEMBinary)object);
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Byte> iterator() {
		return new BaseIterator<Byte>() {

			int index = 0;

			@Override
			public Byte next() {
				return new Byte(FEMBinary.this.customGet(this.index++));
			}

			@Override
			public boolean hasNext() {
				return this.index < FEMBinary.this.length;
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final FEMBinary that) {
		return this.compare(that);
	}

	/** Diese Methode gibt eine unveränderliche {@link List} als Sicht auf die Bytes dieser Bytefolge zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return {@link List}-Sicht. */
	public final List<Byte> toList() {
		return new AbstractList<Byte>() {

			@Override
			public Byte get(final int index) {
				return new Byte(FEMBinary.this.get(index));
			}

			@Override
			public int size() {
				return FEMBinary.this.length;
			}

		};
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die Bytes dieser Bytefolge enthält. Sie ist die Umkehroperation zu {@link #from(IAMArray)}.
	 *
	 * @return Zahlenfolge mit den kodierten Bytes dieser Bytefolge. */
	public final IAMArray toArray() {
		final byte[] array = new byte[this.length + 4];
		final int hash = this.hash;
		array[0] = (byte)(hash >>> 0);
		array[1] = (byte)(hash >>> 8);
		array[2] = (byte)(hash >>> 16);
		array[3] = (byte)(hash >>> 24);
		this.extract(new ValueCollector(array, 4));
		return IAMArray.from(array);
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return this.toString(true);
	}

}