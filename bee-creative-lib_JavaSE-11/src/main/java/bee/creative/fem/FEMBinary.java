package bee.creative.fem;

import java.nio.ByteOrder;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Integers;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.AbstractIterator;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;

/** Diese Klasse implementiert eine unveränderliche Bytefolge sowie Methoden zur Erzeugung solcher Bytefolgen aus nativen Arrays.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMBinary implements FEMValue, Iterable<Byte>, Comparable<FEMBinary>, UseToString {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Bytes einer Bytefolge in der Methode {@link FEMBinary#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Bytewert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 *
		 * @param value Byte.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(byte value);

	}

	static class ItemList extends AbstractList<Byte> implements RandomAccess {

		public final FEMBinary items;

		public ItemList(final FEMBinary items) {
			this.items = items;
		}

		@Override
		public Byte get(final int index) {
			return this.items.get(index);
		}

		@Override
		public int size() {
			return this.items.length;
		}

		@Override
		public Iterator<Byte> iterator() {
			return this.items.iterator();
		}

		@Override
		public boolean contains(final Object o) {
			return this.indexOf(o) >= 0;
		}

		@Override
		public int indexOf(final Object o) {
			return this.items.findFirst(o);
		}

		@Override
		public int lastIndexOf(final Object o) {
			return this.items.findLast(o);
		}

		@Override
		public List<Byte> subList(final int fromIndex, final int toIndex) {
			return this.items.section(fromIndex, toIndex - fromIndex).toList();
		}

	}

	static class ItemFinder implements Collector {

		public final byte value;

		public int index;

		ItemFinder(final byte value) {
			this.value = value;
		}

		@Override
		public boolean push(final byte value) {
			if (this.value == value) return false;
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
			var index = this.index;
			this.array[index] = FEMBinary.toChar((value >> 4) & 0xF);
			++index;
			this.array[index] = FEMBinary.toChar((value >> 0) & 0xF);
			++index;
			this.index = index;
			return true;
		}

	}

	static class UniformCollector implements Collector {

		public byte value;

		public UniformCollector(final byte value) {
			this.value = value;
		}

		@Override
		public boolean push(final byte value) {
			return this.value == value;
		}

	}

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

	/** Diese Klasse implementiert eine abstrakte {@link FEMBinary Bytefolge} mit {@link #hash Streuwertpuffer}.
	 *
	 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class HashBinary extends FEMBinary {

		/** Dieses Feld speichert den Streuwert oder {@code 0}. Es wird in {@link #hashCode()} initialisiert. */
		protected int hash;

		/** Dieser Konstruktor initialisiert die Länge.
		 *
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
		protected HashBinary(final int length) throws IllegalArgumentException {
			super(length);
		}

		@Override
		public int hashCode() {
			final var result = this.hash;
			if (result != 0) return result;
			return this.hash = super.hashCode();
		}

	}

	public static class ConcatBinary extends HashBinary implements Emuable {

		static int size(FEMBinary array) {
			for (var size = 0; true; size++) {
				if (array instanceof ConcatBinary2) {
					array = ((ConcatBinary)array).binary2;
				} else if (array instanceof ConcatBinary) {
					array = ((ConcatBinary)array).binary1;
				} else return size;
			}
		}

		static ConcatBinary from(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			final int size1 = ConcatBinary.size(binary1), size2 = ConcatBinary.size(binary2);
			if ((size1 + 1) < size2) {
				final var cb2 = (ConcatBinary)binary2;
				if (!(cb2 instanceof ConcatBinary1)) return ConcatBinary.from(ConcatBinary.from(binary1, cb2.binary1), cb2.binary2);
				final var cb21 = (ConcatBinary)cb2.binary1;
				return ConcatBinary.from(ConcatBinary.from(binary1, cb21.binary1), ConcatBinary.from(cb21.binary2, cb2.binary2));
			}
			if ((size2 + 1) < size1) {
				final var cb1 = (ConcatBinary)binary1;
				if (!(cb1 instanceof ConcatBinary2)) return ConcatBinary.from(cb1.binary1, ConcatBinary.from(cb1.binary2, binary2));
				final var cb12 = (ConcatBinary)cb1.binary2;
				return ConcatBinary.from(ConcatBinary.from(cb1.binary1, cb12.binary1), ConcatBinary.from(cb12.binary2, binary2));
			}
			if (size1 > size2) return new ConcatBinary1(binary1, binary2);
			if (size1 < size2) return new ConcatBinary2(binary1, binary2);
			return new ConcatBinary(binary1, binary2);
		}

		public final FEMBinary binary1;

		public final FEMBinary binary2;

		ConcatBinary(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1.length + binary2.length);
			this.binary1 = binary1;
			this.binary2 = binary2;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			final var index2 = index - this.binary1.length;
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

	public static class ConcatBinary1 extends ConcatBinary {

		ConcatBinary1(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1, binary2);
		}

	}

	public static class ConcatBinary2 extends ConcatBinary {

		ConcatBinary2(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1, binary2);
		}

	}

	public static class SectionBinary extends HashBinary implements Emuable {

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
		protected int customFind(final byte that, final int offset, final int length, final boolean foreward) {
			final var result = this.binary.customFind(that, offset + this.offset, length, foreward);
			return result >= 0 ? result - this.offset : -1;
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

	public static class ReverseBinary extends HashBinary implements Emuable {

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
		protected int customFind(final byte that, final int offset, final int length, final boolean foreward) {
			final var result = this.binary.customFind(that, this.length - offset - length, length, !foreward);
			return result >= 0 ? this.length - result - 1 : -1;
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

		@Override
		public boolean isUniform() {
			return this.binary.isUniform();
		}

	}

	public static class UniformBinary extends HashBinary {

		public final byte value;

		UniformBinary(final int length, final byte value) throws IllegalArgumentException {
			super(length);
			this.value = value;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.value;
		}

		@Override
		protected int customFind(final byte that, final int offset, final int length, final boolean foreward) {
			return this.value == that ? offset : -1;
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.value)) return false;
				length--;
			}
			return true;
		}

		@Override
		protected FEMBinary customSection(final int offset, final int length) {
			return new UniformBinary(length, this.value);
		}

		@Override
		public FEMBinary reverse() {
			return this;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

		@Override
		public boolean isUniform() {
			return true;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

	}

	public static class CompactBinary extends HashBinary implements Emuable {

		/** Dieses Feld speichert das Array der Bytes, das nicht verändert werden sollte. */
		final byte[] array;

		final int offset;

		CompactBinary(final byte[] array) {
			this(array, 0, array.length);
		}

		CompactBinary(final byte[] array, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.array = Objects.notNull(array);
			this.offset = offset;
		}

		@Override
		protected byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.array[this.offset + index];
		}

		@Override
		protected FEMBinary customSection(final int offset, final int length) {
			return new CompactBinary(this.array, this.offset + offset, length);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + ((this.offset == 0) && (this.length == this.array.length) ? EMU.from(this.array) : 0);
		}

		@Override
		public byte[] value() {
			return Arrays.copyOfRange(this.array, this.offset, this.offset + this.length);
		}

		@Override
		public FEMBinary compact() {
			if ((this.offset == 0) && (this.length == this.array.length)) return this;
			return super.compact();
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

	}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 5;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMBinary> TYPE = FEMType.from(FEMBinary.ID);

	/** Dieses Feld speichert die leere Bytefolge. */
	public static final FEMBinary EMPTY = new UniformBinary(0, (byte)0);

	/** Diese Methode ist eine Abkürzung von {@link #from(int, byte) FEMBinary.from(length, 0)}.
	 *
	 * @param length Länge.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMBinary from(final int length) throws IllegalArgumentException {
		return FEMBinary.from(length, (byte)0);
	}

	/** Diese Methode gibt eine uniforme Bytefolge mit der gegebenen Länge zurück.
	 *
	 * @param length Länge.
	 * @param item Wert jedes Byte der Bytefolge.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMBinary from(final int length, final byte item) throws IllegalArgumentException {
		if (length == 0) return FEMBinary.EMPTY;
		return new UniformBinary(length, item);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(boolean, byte[]) FEMBinary.from(true, items)}.
	 *
	 * @param items Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMBinary from(final byte[] items) throws NullPointerException {
		return FEMBinary.from(true, items);
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
		final var result = new byte[length];
		System.arraycopy(items, offset, result, 0, length);
		return new CompactBinary(result);
	}

	/** Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMBinary from(final boolean copy, final byte[] items) throws NullPointerException {
		final var length = items.length;
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return new UniformBinary(1, items[0]);
		return new CompactBinary(copy ? items.clone() : items);
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
		return FEMBinary.from(true, string);
	}

	/** Diese Methode gibt eine neue Bytefolge mit dem in der gegebenen Zeichenkette kodierten Wert zurück. Das Format der Zeichenkette entspricht dem der
	 * {@link #toString(boolean) Textdarstellung}.
	 *
	 * @param header {@code true}, wenn die Zeichenkette mit {@code "0x"} beginnt.
	 * @param string Zeichenkette.
	 * @see #toString()
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMBinary from(final boolean header, final String string) throws NullPointerException, IllegalArgumentException {
		int count = string.length(), index = 0;
		if ((count & 1) != 0) throw new IllegalArgumentException();
		if (header) {
			if ((count < 2) || (string.charAt(0) != '0') || (string.charAt(1) != 'x')) throw new IllegalArgumentException();
			index += 2;
			count -= 2;
		}
		count >>= 1;
		if (count == 0) return FEMBinary.EMPTY;
		final var bytes = new byte[count];
		for (var i = 0; i < count; i++, index += 2) {
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

	/** Diese Methode konvertiert die gegebenen Zahlen in eine Bytefolge und gibt diese zurück.
	 *
	 * @see #from(byte[])
	 * @see Number#byteValue()
	 * @param items Zahlen.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMBinary from(final List<? extends Number> items) throws NullPointerException {
		final var length = items.size();
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return FEMBinary.from(1, items.get(0).byteValue());
		final var result = new byte[length];
		for (var i = 0; i < length; i++) {
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

	/** Diese Methode gibt die Verkettung der gegebenen Bytefolgen zurück.
	 *
	 * @see #concat(FEMBinary)
	 * @param values Bytefolgen.
	 * @return Verkettung der Bytefolgen.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public static FEMBinary concatAll(final FEMBinary... values) throws NullPointerException {
		final var length = values.length;
		if (length == 0) return FEMBinary.EMPTY;
		if (length == 1) return values[0].data();
		return FEMBinary.concatAll(values, 0, length - 1);
	}

	static FEMBinary concatAll(final FEMBinary[] values, final int min, final int max) throws NullPointerException {
		if (min == max) return values[min];
		final var mid = (min + max) >> 1;
		return FEMBinary.concatAll(values, min, mid).concat(FEMBinary.concatAll(values, mid + 1, max));
	}

	/** Diese Methode gibt das Zeichen zur gegebenen hexadezimalen Ziffer zurück.
	 *
	 * @param hexDigit hexadezimale Ziffer ({@code 0..15}).
	 * @return Zeichen ({@code '0'..'9', 'A'..'F'}).
	 * @throws IllegalArgumentException Wenn {@code hexDigit} ungültig ist. */
	public static char toChar(final int hexDigit) throws IllegalArgumentException {
		final var letter = hexDigit - 10;
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
		final var digit = hexChar - '0';
		if (digit < 0) throw new IllegalArgumentException("hexChar < '0'");
		if (digit <= 9) return digit;
		final var lower = hexChar - 'a';
		if (lower > 5) throw new IllegalArgumentException("hexChar > 'f'");
		if (lower >= 0) return lower + 10;
		final var upper = hexChar - 'A';
		if (upper < 0) throw new IllegalArgumentException("'9' < hexChar < 'A'");
		if (upper <= 5) return upper + 10;
		throw new IllegalArgumentException("'F' < hexChar < 'a'");
	}

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

	/** Diese Methode gibt das {@code index}-te Byte zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Byte. */
	protected abstract byte customGet(final int index);

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Bytefolge innerhalb dieser Bytefolge zurück. Sie Implementiert
	 * {@link #find(FEMBinary, int)} ohne Wertebereichsprüfung.
	 *
	 * @param that nicht leere gesuchte Bytefolge.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Bytefolge ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	protected int customFind(final FEMBinary that, final int offset) {
		final var value = that.customGet(0);
		final var count = (this.length - that.length) + 1;
		for (var result = offset; true; result++) {
			result = this.customFind(value, result, count - result, true);
			if (result < 0) return -1;
			if (this.customEquals(that, result)) return result;
		}
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Bytes im gegebenen Abschnitt zurück. Sie Implementiert {@link #find(byte, int)} ohne
	 * Wertebereichsprüfung.
	 *
	 * @param that gesuchtes Zeichen.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return Position des ersten Vorkommens des gegebenen Bytes oder {@code -1}.
	 * @param foreward {@code true}, wenn die Reihenfolge vorwärts ist, bzw. {@code false}, wenn sie rückwärts ist. */
	protected int customFind(final byte that, final int offset, final int length, final boolean foreward) {
		final var finder = new ItemFinder(that);
		if (this.customExtract(finder, offset, length, foreward)) return -1;
		return foreward ? (finder.index + offset) : (length - finder.index - 1);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge gleich der gegebenen ist. Sie Implementiert {@link #equals(Object)}. */
	protected boolean customEquals(final FEMBinary that) throws NullPointerException {
		final var length = this.length;
		if ((length != that.length) || (this.hashCode() != that.hashCode())) return false;
		return this.customEquals(that, 0);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Bytefolge an der gegebenen Position in dieser Bytefolge liegt. */
	protected boolean customEquals(final FEMBinary that, final int offset) {
		final var length = that.length;
		for (var i = 0; i < length; i++) {
			if (this.customGet(offset + i) != that.customGet(i)) return false;
		}
		return true;
	}

	/** Diese Methode fügt alle Bytes im gegebenen Abschnitt in der gegebenen Reihenfolge geordnet an den gegebenen {@link Collector} an. Das Anfügen wird
	 * vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @param foreward {@code true}, wenn die Reihenfolge vorwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
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

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMBinary data() {
		return this;
	}

	@Override
	public final FEMType<FEMBinary> type() {
		return FEMBinary.TYPE;
	}

	/** Diese Methode konvertiert diese Bytefolge in ein {@code byte[]} und gibt dieses zurück.
	 *
	 * @return Array mit den Bytes dieser Bytefolge. */
	public byte[] value() {
		final var target = new ValueCollector(new byte[this.length], 0);
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
		return ConcatBinary.from(this, that);
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

	/** Diese Methode gibt diese Bytefolge mit optimierter Leistungsfähigkeit des {@link #get(int) Bytezugriffs} zurück. Wenn die Bytefolge diesbezüglich
	 * optimiert werden kann, wird grundsätzlich eine Abschrift der {@link #value() Bytes} dieser Bytefolge analog zu {@link #from(byte[]) from(values())}
	 * geliefert.
	 *
	 * @return performantere Bytefolge oder {@code this}. */
	public FEMBinary compact() {
		if (this.isEmpty()) return FEMBinary.EMPTY;
		if (this.isUniform()) return new UniformBinary(this.length, this.customGet(0));
		return new CompactBinary(this.value());
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Bytewerts innerhalb dieser Bytefolge zurück. Die Suche beginnt an der gegebenen
	 * Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchter Bytewert.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Bytewerts ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final byte that, final int offset) throws IllegalArgumentException {
		if (offset == this.length) return -1;
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (offset == this.length) return -1;
		return this.customFind(that, offset, this.length - offset, true);
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
		if (that.length == 1) return this.find(that.customGet(0), offset);
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (that.length == 0) return offset;
		if (that.length > (this.length - offset)) return -1;
		return this.customFind(that, offset);
	}

	int findLast(final Object key) {
		if ((this.length == 0) || !(key instanceof Byte)) return -1;
		return this.customFind((Byte)key, 0, this.length, false);
	}

	int findFirst(final Object key) {
		if ((this.length == 0) || !(key instanceof Byte)) return -1;
		return this.customFind((Byte)key, 0, this.length, true);
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

	@Override
	public final FEMBinary result(boolean deep) {
		return deep ? this.compact() : this;
	}

	@Override
	public int hashCode() {
		final var hasher = new HashCollector();
		this.extract(hasher);
		final var result = hasher.hash;
		return result != 0 ? result : -11;
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBinary)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMBinary)) return false;
		}
		return this.customEquals((FEMBinary)object);
	}

	@Override
	public final Iterator<Byte> iterator() {
		return new AbstractIterator<>() {

			int index = 0;

			@Override
			public Byte next() {
				return FEMBinary.this.customGet(this.index++);
			}

			@Override
			public boolean hasNext() {
				return this.index < FEMBinary.this.length;
			}

		};
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Bytefolge kleiner, gleich oder größer als die
	 * der gegebenen Bytefolge ist. Die Bytewerte werden als {@code UINT8} verglichen.
	 *
	 * @param that Bytefolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	@Override
	public final int compareTo(final FEMBinary that) throws NullPointerException {
		final var length = Math.min(this.length, that.length);
		for (var i = 0; i < length; i++) {
			final var result = Comparators.compare(this.customGet(i) & 255, that.customGet(i) & 255);
			if (result != 0) return result;
		}
		return Comparators.compare(this.length, that.length);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge leer ist.
	 *
	 * @return {@code true} bei Leerheit. */
	public final boolean isEmpty() {
		return this.length == 0;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge keine sich unterscheidenden Bytewerte enthält.
	 *
	 * @return {@code true} bei Uniformität. */
	public boolean isUniform() {
		return this.isEmpty() || this.extract(new UniformCollector(this.customGet(0)));
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die Kompaktierung aktiviert, d.h die Leistungsfähigkeit des {@link #get(int) Bytezugriffs} optimiert
	 * ist.
	 *
	 * @return {@code true} bei Kompaktierung. */
	public boolean isCompacted() {
		return false;
	}

	/** Diese Methode gibt eine unveränderliche {@link List} als Sicht auf die Bytes dieser Bytefolge zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return {@link List}-Sicht. */
	public final List<Byte> toList() {
		return new ItemList(this);
	}

	@Override
	public final String toString() {
		return this.toString(true);
	}

	/** Diese Methode gibt die Textdarstellung dieser Bytefolge zurück. Die Textdarstellung besteht aus der Zeichenkette {@code "0x"} (header) und den Bytes
	 * dieser Bytefolge vom ersten zum letzten geordnet in hexadezimalen Ziffern, d.h. {@code 0123456789ABCDEF}.
	 *
	 * @param header {@code true}, wenn die Zeichenkette mit {@code "0x"} beginnen soll.
	 * @return Textdarstellung. */
	public final String toString(final boolean header) {
		final var target = new StringCollector(header, this.length);
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

}