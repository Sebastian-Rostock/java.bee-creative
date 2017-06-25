package bee.creative.fem;

import java.util.Iterator;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparators;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;

/** Diese Klasse implementiert eine unveränderliche Bytefolge sowie Methoden zur Erzeugung solcher Bytefolgen aus nativen Arrays.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMBinary extends FEMValue implements Iterable<Byte>, UseToString {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Bytes einer Bytefolge in der Methode {@link FEMBinary#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Bytewert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 *
		 * @param value Byte.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(byte value);

	}

	@SuppressWarnings ("javadoc")
	static final class FindCollector implements Collector {

		public final byte that;

		public int index;

		FindCollector(final byte that) {
			this.that = that;
		}

		{}

		@Override
		public final boolean push(final byte value) {
			if (value == this.that) return false;
			this.index++;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class HashCollector implements Collector {

		public int hash = 0x811C9DC5;

		{}

		@Override
		public final boolean push(final byte value) {
			this.hash = (this.hash * 0x01000193) ^ value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class StringCollector implements Collector {

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

		{}

		@Override
		public final boolean push(final byte value) {
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
	static final class ValueCollector implements Collector {

		public final byte[] array;

		public int index;

		public ValueCollector(final byte[] array, final int index) {
			this.array = array;
			this.index = index;
		}

		{}

		@Override
		public final boolean push(final byte value) {
			this.array[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ArrayBinary extends FEMBinary {

		public final MMFArray array;

		ArrayBinary(final MMFArray array) {
			super(array.length());
			this.array = array;
		}

		{}

		@Override
		protected final byte customGet(final int index) throws IndexOutOfBoundsException {
			return (byte)this.array.get(index);
		}

		@Override
		public final byte[] value() {
			return this.array.toBytes();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyBinary extends FEMBinary {

		EmptyBinary() {
			super(0);
		}

		{}

		@Override
		public final FEMBinary reverse() {
			return this;
		}

		@Override
		public final FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ConcatBinary extends FEMBinary {

		public final FEMBinary binary1;

		public final FEMBinary binary2;

		ConcatBinary(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1.length + binary2.length);
			this.binary1 = binary1;
			this.binary2 = binary2;
		}

		{}

		@Override
		protected final byte customGet(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.binary1.length;
			return index2 < 0 ? this.binary1.customGet(index) : this.binary2.customGet(index2);
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
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
		public final FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.binary1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.binary2.section(offset2, length);
			if (length2 <= 0) return this.binary1.section(offset, length);
			return this.binary1.section(offset, -offset2).concat(this.binary2.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SectionBinary extends FEMBinary {

		public final FEMBinary binary;

		public final int offset;

		SectionBinary(final FEMBinary binary, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.binary = binary;
			this.offset = offset;
		}

		{}

		@Override
		protected final byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.binary.customGet(index + this.offset);
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.binary.customExtract(target, this.offset + offset2, length2, foreward);
		}

		@Override
		public final FEMBinary section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.binary.section(this.offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseBinary extends FEMBinary {

		public final FEMBinary binary;

		ReverseBinary(final FEMBinary binary) throws IllegalArgumentException {
			super(binary.length);
			this.binary = binary;
		}

		{}

		@Override
		protected final byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.binary.customGet(this.length - index - 1);
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.binary.customExtract(target, this.length - offset - length, length, !foreward);
		}

		@Override
		public final FEMBinary concat(final FEMBinary that) throws NullPointerException {
			return that.reverse().concat(this.binary).reverse();
		}

		@Override
		public final FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			return this.binary.section(this.length - offset - length, length).reverse();
		}

		@Override
		public final FEMBinary reverse() {
			return this.binary;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformBinary extends FEMBinary {

		public final byte item;

		UniformBinary(final int length, final byte item) throws IllegalArgumentException {
			super(length);
			this.item = item;
		}

		{}

		@Override
		protected final byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.item;
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.item)) return false;
				length--;
			}
			return true;
		}

		@Override
		public final FEMBinary reverse() {
			return this;
		}

		@Override
		public final FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CompactBinary extends FEMBinary {

		public final byte[] items;

		CompactBinary(final byte[] items) throws IllegalArgumentException {
			super(items.length);
			this.items = items;
		}

		{}

		@Override
		protected final byte customGet(final int index) throws IndexOutOfBoundsException {
			return this.items[index];
		}

		@Override
		public final byte[] value() {
			return this.items.clone();
		}

		@Override
		public final FEMBinary compact() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 5;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMBinary> TYPE = FEMType.from(FEMBinary.ID);

	/** Dieses Feld speichert die leere Bytefolge. */
	public static final FEMBinary EMPTY = new EmptyBinary();

	{}

	/** Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück.<br>
	 * Das gegebene Array wird kopiert.
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

	/** Diese Methode gibt eine Bytefolge mit den Bytes im gegebenen Abschnitt zurück.<br>
	 * Der gegebene Abschnitt wird kopiert.
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

	/** Diese Methode gibt eine neue Bytefolge mit dem in der gegebenen Zeichenkette kodierten Wert zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString() Textdarstellung}.
	 *
	 * @see #toString()
	 * @param string Zeichenkette.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist. */
	public static FEMBinary from(final String string) throws NullPointerException, IllegalArgumentException {
		return FEMBinary.from(string, true);
	}

	/** Diese Methode gibt eine neue Bytefolge mit dem in der gegebenen Zeichenkette kodierten Wert zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString(boolean) Textdarstellung}.
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

	/** Diese Methode gibt eine Bytefolge mit den gegebenen Zahlen zurück.
	 *
	 * @param array Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zahlenfolge nicht als {@link MMFArray#mode() UNI8/UINT8} vorliegt. */
	public static FEMBinary from(final MMFArray array) throws NullPointerException, IllegalArgumentException {
		if (array.length() == 0) return FEMBinary.EMPTY;
		if (array.mode() == 1) return new ArrayBinary(array);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMBinary.TYPE)}.
	 *
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMBinary from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMBinary.TYPE);
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

	@SuppressWarnings ("javadoc")
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

	{}

	/** Dieses Feld speichert den Streuwert. */
	int hash;

	/** Dieses Feld speichert die Länge. */
	protected final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	protected FEMBinary(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this.length = length;
	}

	{}

	/** Diese Methode gibt das {@code index}-te Byte zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Byte. */
	protected byte customGet(final int index) {
		return 0;
	}

	/** Diese Methode fügt alle Bytes im gegebenen Abschnitt in der gegebenen Reigenfolge geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reigenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
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
		return new SectionBinary(this, offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Bytefolge zurück.
	 *
	 * @return rückwärts geordnete {@link FEMBinary}-Sicht auf diese Bytefolge. */
	public FEMBinary reverse() {
		return new ReverseBinary(this);
	}

	/** Diese Methode gibt die {@link #value() Bytes dieser Bytefolge} in einer performanteren oder zumindest gleichwertigen Bytefolge zurück.
	 *
	 * @see #from(byte[])
	 * @see #value()
	 * @return performanteren Bytefolge oder {@code this}. */
	public FEMBinary compact() {
		final FEMBinary result = this.length == 1 ? new UniformBinary(1, this.customGet(0)) : new CompactBinary(this.value());
		result.hash = this.hash;
		return result;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Bytewerts innerhalb dieser Bytefolge zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchter Bytewert.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Bytewerts ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final byte that, final int offset) throws IllegalArgumentException {
		final int length = this.length - offset;
		if ((offset < 0) || (length < 0)) throw new IllegalArgumentException();
		final FindCollector collector = new FindCollector(that);
		if (this.customExtract(collector, offset, length, true)) return -1;
		return collector.index + offset;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Bytefolge innerhalb dieser Bytefolge zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchte Bytefolge.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Bytefolge ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMBinary that, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		final int count = that.length;
		if (count == 0) return offset;
		final int value = that.customGet(0), length = this.length - count;
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

	/** Diese Methode fügt alle Bytes dieser Bytefolge vom ersten zum letzten geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	public final boolean extract(final Collector target) throws NullPointerException {
		Objects.assertNotNull(target);
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

	/** Diese Methode gibt den Streuwert zurück.
	 *
	 * @return Streuwert. */
	public final int hash() {
		int result = this.hash;
		if (result != 0) return result;
		final int length = this.length;
		final HashCollector collector = new HashCollector();
		this.customExtract(collector, 0, length, true);
		this.hash = (result = (collector.hash | 1));
		return result;
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

	/** Diese Methode gibt die Textdarstellung dieser Bytefolge zurück.<br>
	 * Die Textdarstellung besteht aus der Zeichenkette {@code "0x"} (header) und den Bytes dieser Bytefolge vom ersten zum letzten geordnet in hexadezimalen
	 * Ziffern, d.h. {@code 0123456789ABCDEF}.
	 *
	 * @param header {@code true}, wenn die Zeichenkette mit {@code "0x"} beginnen soll.
	 * @return Textdarstellung. */
	public final String toString(final boolean header) {
		final StringCollector target = new StringCollector(header, this.length);
		this.extract(target);
		return new String(target.array, 0, target.array.length);
	}

	{}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMBinary data() {
		return this;
	}

	/** Diese Methode gibt {@link #TYPE} zurück. */
	@Override
	public final FEMType<FEMBinary> type() {
		return FEMBinary.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMBinary result() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMBinary result(final boolean recursive) {
		if (!recursive) return this;
		return this.compact();
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash();
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
		return new Iterator<Byte>() {

			int index = 0;

			@Override
			public Byte next() {
				return new Byte(FEMBinary.this.customGet(this.index++));
			}

			@Override
			public boolean hasNext() {
				return this.index < FEMBinary.this.length;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return this.toString(true);
	}

}