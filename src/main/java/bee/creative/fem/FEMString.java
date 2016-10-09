package bee.creative.fem;

import java.util.Arrays;
import java.util.Iterator;
import bee.creative.iam.IAMArray;
import bee.creative.mmf.MMFArray;
import bee.creative.util.Comparators;

/** Diese Klasse implementiert eine Zeichenkette, deren Verkettungen, Anschnitte und Umkehrungen als Sichten auf die grundlegenden Zeichenketten realisiert sind.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMString extends FEMValue implements Iterable<Integer> {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Codepoints einer Zeichenkette in der Methode {@link FEMString#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Codepoint an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammeln fortgeführt werden soll.
		 * 
		 * @param value Codepoints.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(int value);

	}

	@SuppressWarnings ("javadoc")
	static final class FindCollector implements Collector {

		public final int that;

		public int index;

		FindCollector(final int that) {
			this.that = that;
		}

		{}

		@Override
		public final boolean push(final int value) {
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
		public final boolean push(final int value) {
			this.hash = (this.hash * 0x01000193) ^ value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8Collector implements Collector {

		public byte[] array;

		public int index;

		UTF8Collector(final int length) {
			this.array = new byte[length];
		}

		{}

		public final void push(final byte value) {
			final int index = this.index;
			byte[] array = this.array;
			if (index >= array.length) {
				array = Arrays.copyOf(this.array, ((index * 3) >> 1) + 4);
				this.array = array;
			}
			array[index] = value;
			this.index = index + 1;
		}

		{}

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			if (value < 128) {
				this.push((byte)value);
			} else if (value < 2048) {
				this.push((byte)(192 | (value >> 6)));
				this.push((byte)(128 | (value & 63)));
			} else if (value < 65536) {
				this.push((byte)(224 | (value >> 12)));
				this.push((byte)(128 | ((value >> 6) & 63)));
				this.push((byte)(128 | (value & 63)));
			} else {
				this.push((byte)(240 | (value >> 18)));
				this.push((byte)(128 | ((value >> 12) & 63)));
				this.push((byte)(128 | ((value >> 6) & 63)));
				this.push((byte)(128 | (value & 63)));
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16Collector implements Collector {

		public char[] array;

		public int index;

		UTF16Collector(final int length) {
			this.array = new char[length];
		}

		{}

		public final void push(final char value) {
			final int index = this.index;
			char[] array = this.array;
			if (index >= array.length) {
				array = Arrays.copyOf(this.array, ((index * 3) >> 1) + 4);
				this.array = array;
			}
			array[index] = value;
			this.index = index + 1;
		}

		{}

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			final int value2 = value - 65536;
			if (value2 < 0) {
				this.push((char)value);
			} else {
				this.push((char)(55296 | (value2 >> 10)));
				this.push((char)(56320 | (value2 & 1023)));
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32Collector implements Collector {

		public int[] array;

		public int index;

		UTF32Collector(final int length) {
			this.array = new int[length];
		}

		{}

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			this.array[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8ArrayString extends FEMString {

		public final MMFArray array;

		UTF8ArrayString(final MMFArray array) {
			super(FEMString._utf8Count_(array));
			this.array = array;
		}

		{}

		@Override
		protected final int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf8Size_(this.array.get(offset));
			}
			return FEMString._utf8Value_(this.array, offset);
		}

		@Override
		protected final boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this.array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMString._utf8Value_(this.array, index))) return false;
					length--;
					index += FEMString._utf8Size_(this.array.get(index));
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this.array.get(index));
				}
				while (length > 0) {
					while (!FEMString._utf8Start_(this.array.get(--index))) {}
					if (!target.push(FEMString._utf8Value_(this.array, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

		@Override
		public final byte[] toBytes() {
			return this.array.toBytes();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16ArrayString extends FEMString {

		public final MMFArray array;

		UTF16ArrayString(final MMFArray array) {
			super(FEMString._utf16Count_(array));
			this.array = array;
		}

		{}

		@Override
		protected final int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf16Size_(this.array.get(offset));
			}
			return FEMString._utf16Value_(this.array, offset);
		}

		@Override
		protected final boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this.array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMString._utf16Value_(this.array, index))) return false;
					length--;
					index += FEMString._utf16Size_(this.array.get(index));
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this.array.get(index));
				}
				while (length > 0) {
					while (!FEMString._utf16Start_(this.array.get(--index))) {}
					if (!target.push(FEMString._utf16Value_(this.array, index))) return false;
					length--;
				}
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32ArrayString extends FEMString {

		public final MMFArray array;

		UTF32ArrayString(final MMFArray array) {
			super(array.length());
			this.array = array;
		}

		{}

		@Override
		protected final int _get_(final int index) throws IndexOutOfBoundsException {
			return this.array.get(index);
		}

		@Override
		public final int[] value() {
			return this.array.toArray();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8CompactString extends FEMString {

		public final byte[] items;

		UTF8CompactString(final byte[] items) throws IllegalArgumentException {
			super(FEMString._utf8Count_(items));
			this.items = items;
		}

		{}

		@Override
		protected final int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf8Size_(this.items[offset]);
			}
			return FEMString._utf8Value_(this.items, offset);
		}

		@Override
		protected final boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this.items[index]);
				}
				while (length > 0) {
					if (!target.push(FEMString._utf8Value_(this.items, index))) return false;
					length--;
					index += FEMString._utf8Size_(this.items[index]);
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this.items[index]);
				}
				while (length > 0) {
					while (!FEMString._utf8Start_(this.items[--index])) {}
					if (!target.push(FEMString._utf8Value_(this.items, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

		@Override
		public final byte[] toBytes() {
			return this.items.clone();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16CompactString extends FEMString {

		public final char[] items;

		UTF16CompactString(final char[] items) throws IllegalArgumentException {
			super(FEMString._utf16Count_(items));
			this.items = items;
		}

		{}

		@Override
		protected final int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf16Size_(this.items[offset]);
			}
			return FEMString._utf16Value_(this.items, offset);
		}

		@Override
		protected final boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this.items[index]);
				}
				while (length > 0) {
					if (!target.push(FEMString._utf16Value_(this.items, index))) return false;
					length--;
					index += FEMString._utf16Size_(this.items[index]);
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this.items[index]);
				}
				while (length > 0) {
					while (!FEMString._utf16Start_(this.items[--index])) {}
					if (!target.push(FEMString._utf16Value_(this.items, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

		@Override
		public final char[] toChars() {
			return this.items.clone();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32CompactString extends FEMString {

		public final int[] items;

		UTF32CompactString(final int[] items) throws IllegalArgumentException {
			super(items.length);
			this.items = items;
		}

		{}

		@Override
		protected final int _get_(final int index) throws IndexOutOfBoundsException {
			return this.items[index];
		}

		@Override
		public final int[] value() {
			return this.items.clone();
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyString extends FEMString {

		EmptyString() {
			super(0);
		}

		{}

		@Override
		public final FEMString reverse() {
			return this;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ConcatString extends FEMString {

		public final FEMString string1;

		public final FEMString string2;

		ConcatString(final FEMString string1, final FEMString string2) throws IllegalArgumentException {
			super(string1._length_ + string2._length_);
			this.string1 = string1;
			this.string2 = string2;
		}

		{}

		@Override
		protected final int _get_(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.string1._length_;
			return index2 < 0 ? this.string1._get_(index) : this.string2._get_(index2);
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.string1._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this.string2._export_(target, offset2, length, foreward);
			if (length2 <= 0) return this.string1._export_(target, offset, length, foreward);
			if (foreward) {
				if (!this.string1._export_(target, offset, -offset2, foreward)) return false;
				return this.string2._export_(target, 0, length2, foreward);
			} else {
				if (!this.string2._export_(target, 0, length2, foreward)) return false;
				return this.string1._export_(target, offset, -offset2, foreward);
			}
		}

		@Override
		public final FEMString section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.string1._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this.string2.section(offset2, length);
			if (length2 <= 0) return this.string1.section(offset, length);
			return super.section(offset, -offset2).concat(this.string2.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SectionString extends FEMString {

		public final FEMString string;

		public final int offset;

		SectionString(final FEMString string, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.string = string;
			this.offset = offset;
		}

		{}

		@Override
		protected final int _get_(final int index) throws IndexOutOfBoundsException {
			return this.string._get_(index + this.offset);
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.string._export_(target, this.offset + offset2, length2, foreward);
		}

		@Override
		public final FEMString section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.string.section(this.offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseString extends FEMString {

		public final FEMString string;

		ReverseString(final FEMString string) throws IllegalArgumentException {
			super(string._length_);
			this.string = string;
		}

		{}

		@Override
		protected final int _get_(final int index) throws IndexOutOfBoundsException {
			return this.string._get_(this._length_ - index - 1);
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.string._export_(target, offset, length, !foreward);
		}

		@Override
		public final FEMString concat(final FEMString value) throws NullPointerException {
			return value.reverse().concat(this.string).reverse();
		}

		@Override
		public final FEMString section(final int offset, final int length2) throws IllegalArgumentException {
			return this.string.section(this._length_ - offset - length2, length2).reverse();
		}

		@Override
		public final FEMString reverse() {
			return this.string;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformString extends FEMString {

		public final int item;

		UniformString(final int length, final int item) throws IllegalArgumentException {
			super(length);
			this.item = item;
		}

		{}

		@Override
		protected final int _get_(final int index) throws IndexOutOfBoundsException {
			return this.item;
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.item)) return false;
				length--;
			}
			return true;
		}

		@Override
		public final FEMString reverse() {
			return this;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 4;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMString> TYPE = FEMType.from(FEMString.ID);

	/** Dieses Feld speichert die leere Zeichenkette. */
	public static final FEMString EMPTY = new EmptyString();

	{}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF32-kodierten Codepoints zurück.
	 * 
	 * @param items UTF32-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMString from(final int[] items) throws NullPointerException {
		if (items.length == 0) return FEMString.EMPTY;
		return new UTF32CompactString(items.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF8-kodierten Codepoints zurück.
	 * 
	 * @param items UTF8-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final byte[] items) throws NullPointerException, IllegalArgumentException {
		if (items.length == 0) return FEMString.EMPTY;
		return new UTF8CompactString(items.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF16-kodierten Codepoints zurück.
	 * 
	 * @param items UTF16-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final char[] items) throws NullPointerException, IllegalArgumentException {
		if (items.length == 0) return FEMString.EMPTY;
		return new UTF16CompactString(items.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen Codepoints zurück.
	 * 
	 * @param string Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code string} {@code null} ist. */
	public static FEMString from(final String string) throws NullPointerException {
		if (string.length() == 0) return FEMString.EMPTY;
		return new UTF16CompactString(string.toCharArray());
	}

	/** Diese Methode gibt eine uniforme Zeichenkette mit der gegebenen Länge zurück, deren Codepoints alle gleich dem gegebenen sind.
	 * 
	 * @param item Codepoint.
	 * @param length Länge.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMString from(final int item, final int length) throws IllegalArgumentException {
		if (length == 0) return FEMString.EMPTY;
		return new UniformString(length, item);
	}

	/** Diese Methode gibt eine Zeichenkette mit den gegebenen Zahlen zurück.<br>
	 * Abhängig davon, ob die Zahlenliste aus {@link MMFArray#mode() INT8/UINT8}-, {@link MMFArray#mode() INT16/UINT16)} oder {@link MMFArray#mode() INT32}-Zahlen
	 * besteht, werden diese als UTF8-, UTF16- bzw. UTF32-kodierte Codepoints interpretiert.
	 * 
	 * @param array Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final MMFArray array) throws NullPointerException, IllegalArgumentException {
		final int mode = array.mode(), length = array.length();
		if (mode == 0) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return FEMString.from(array.get(0), 1);
		switch (mode) {
			case 1:
				return new UTF8ArrayString(array);
			case 2:
				return new UTF16ArrayString(array);
			case 4:
				return new UTF32ArrayString(array);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMString.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMString from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMString.TYPE);
	}

	/** Diese Methode gibt die Anzahl der Token für den UTF8-kodierten Codepoint zurück, der am gegebenen Token beginnt.
	 * 
	 * @param token Token, an dem ein UTF8-kodierter Codepoint beginnt.
	 * @return Anzahl der Token für den UTF8-kodierten Codepoint.
	 * @throws IllegalArgumentException Wenn {@code token} ungültig ist. */
	static int _utf8Size_(final int token) throws IllegalArgumentException {
		switch ((token >> 4) & 15) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return 1;
			case 12:
			case 13:
				return 2;
			case 14:
				return 3;
			case 15:
				return 4;
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Token den Beginn eines UTF8-kodierten Codepoints ist.
	 * 
	 * @param token Token.
	 * @return {@code true}, wenn ein UTF8-kodierter Codepoint am Token beginnt. */
	static boolean _utf8Start_(final int token) {
		return (token & 192) != 128;
	}

	/** Diese Methode gibt den UTF8-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 * 
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF8-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf8Value_(final byte[] array, final int offset) throws IllegalArgumentException {
		switch ((array[offset] >> 4) & 15) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return array[offset] & 127;
			case 12:
			case 13:
				return ((array[offset] & 31) << 6) | (array[offset + 1] & 63);
			case 14:
				return ((array[offset] & 15) << 12) | ((array[offset + 1] & 63) << 6) | (array[offset + 2] & 63);
			case 15:
				return ((array[offset] & 7) << 18) | ((array[offset + 1] & 63) << 12) | ((array[offset + 2] & 63) << 6) | (array[offset + 3] & 63);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den UTF8-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 * 
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF8-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf8Value_(final IAMArray array, final int offset) throws IllegalArgumentException {
		switch ((array.get(offset) >> 4) & 15) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				return array.get(offset) & 127;
			case 12:
			case 13:
				return ((array.get(offset) & 31) << 6) | (array.get(offset + 1) & 63);
			case 14:
				return ((array.get(offset) & 15) << 12) | ((array.get(offset + 1) & 63) << 6) | (array.get(offset + 2) & 63);
			case 15:
				return ((array.get(offset) & 7) << 18) | ((array.get(offset + 1) & 63) << 12) | ((array.get(offset + 2) & 63) << 6) | (array.get(offset + 3) & 63);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Anzahl an UTF8-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 * 
	 * @param array Tokenliste.
	 * @return Anzahl an UTF8-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf8Count_(final byte[] array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length;
		while (index < length) {
			result++;
			index += FEMString._utf8Size_(array[index]);
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt die Anzahl an UTF8-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 * 
	 * @param array Tokenliste.
	 * @return Anzahl an UTF8-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf8Count_(final MMFArray array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEMString._utf8Size_(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt die Anzahl der Token für den UTF16-kodierten Codepoint zurück, der am gegebenen Token beginnt.
	 * 
	 * @param token Token, an dem ein UTF16-kodierter Codepoint beginnt.
	 * @return Anzahl der Token für den UTF16-kodierten Codepoint.
	 * @throws IllegalArgumentException Wenn {@code token} ungültig ist. */
	static int _utf16Size_(final int token) throws IllegalArgumentException {
		final int value = token & 64512;
		if (value == 55296) return 2;
		if (value != 56320) return 1;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Token den Beginn eines UTF16-kodierten Codepoints ist.
	 * 
	 * @param token Token.
	 * @return {@code true}, wenn ein UTF16-kodierter Codepoint am Token beginnt. */
	static boolean _utf16Start_(final int token) {
		return (token & 64512) != 56320;
	}

	/** Diese Methode gibt den UTF16-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 * 
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF16-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf16Value_(final char[] array, final int offset) throws IllegalArgumentException {
		final int token = array[offset], value = token & 64512;
		if (value == 55296) return (((token & 1023) << 10) | (array[offset + 1] & 1023)) + 65536;
		if (value != 56320) return token;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt den UTF16-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 * 
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF16-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf16Value_(final MMFArray array, final int offset) throws IllegalArgumentException {
		final int token = array.get(offset), value = token & 64512;
		if (value == 55296) return (((token & 1023) << 10) | (array.get(offset + 1) & 1023)) + 65536;
		if (value != 56320) return token;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt die Anzahl an UTF16-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 * 
	 * @param array Tokenliste.
	 * @return Anzahl an UTF16-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf16Count_(final char[] array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length;
		while (index < length) {
			result++;
			index += FEMString._utf16Size_(array[index]);
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt die Anzahl an UTF16-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 * 
	 * @param array Tokenliste.
	 * @return Anzahl an UTF16-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int _utf16Count_(final MMFArray array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEMString._utf16Size_(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	{}

	/** Dieses Feld speichert den Streuwert. */
	int _hash_;

	/** Dieses Feld speichert die Länge. */
	protected final int _length_;

	/** Dieser Konstruktor initialisiert die Länge.
	 * 
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	protected FEMString(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this._length_ = length;
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Codepoint zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-ter Codepoint. */
	protected int _get_(final int index) {
		return 0;
	}

	/** Diese Methode fügt alle Codepoints im gegebenen Abschnitt in der gegebenen Reigenfolge geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reigenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde. */
	protected boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
		if (foreward) {
			for (length += offset; offset < length; offset++) {
				if (!target.push(this._get_(offset))) return false;
			}
		} else {
			for (length += offset - 1; offset <= length; length--) {
				if (!target.push(this._get_(length))) return false;
			}
		}
		return true;
	}

	/** Diese Methode gibt die Codepoint in UTF32-Kodierung zurück.
	 * 
	 * @return Array mit den Codepoints in UTF32-Kodierung. */
	public int[] value() {
		final UTF32Collector target = new UTF32Collector(this._length_);
		this.extract(target);
		return target.array;
	}

	/** Diese Methode gibt das {@code index}-te Zeichen als Codepoint zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Zeichen.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public final int get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this._length_)) throw new IndexOutOfBoundsException();
		return this._get_(index);
	}

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Zeichen in der Zeichenkette zurück.
	 * 
	 * @return Länge der Zeichenkette. */
	public final int length() {
		return this._length_;
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Zeichenkette mit der gegebenen Zeichenkette zurück.
	 * 
	 * @param that Zeichenkette.
	 * @return {@link FEMString}-Sicht auf die Verkettung dieser mit der gegebenen Zeichenkette.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public FEMString concat(final FEMString that) throws NullPointerException {
		if (that._length_ == 0) return this;
		if (this._length_ == 0) return that;
		return new ConcatString(this, that);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Zeichenkette zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMString}-Sicht auf einen Abschnitt dieser Zeichenkette.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Zeichenkette liegt oder eine negative Länge hätte. */
	public FEMString section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this._length_)) return this;
		if ((offset < 0) || ((offset + length) > this._length_)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		return new SectionString(this, offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Zeichenkette zurück.
	 * 
	 * @return rückwärts geordnete {@link FEMString}-Sicht auf diese Zeichenkette. */
	public FEMString reverse() {
		return new ReverseString(this);
	}

	/** Diese Methode gibt die {@link #value() Codepoints dieser Zeichenkette} in einer performanteren oder zumindest gleichwertigen Zeichenkette zurück.
	 * 
	 * @see #from(byte[])
	 * @see #value()
	 * @return performanteren Zeichenkette oder {@code this}. */
	public FEMString compact() {
		final FEMString result = this._length_ == 1 ? new UniformString(1, this._get_(0)) : new UTF8CompactString(this.toBytes());
		result._hash_ = this._hash_;
		return result;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Zeichens innerhalb dieser Zeichenkette zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 * 
	 * @param that gesuchtes Zeichen.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Zeichens ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final int that, final int offset) throws IllegalArgumentException {
		final int length = this._length_ - offset;
		if ((offset < 0) || (length < 0)) throw new IllegalArgumentException();
		final FindCollector collector = new FindCollector(that);
		if (this._export_(collector, offset, length, true)) return -1;
		return collector.index + offset;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Zeichenkette innerhalb dieser Zeichenkette zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 * 
	 * @param that gesuchte Zeichenkette.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Zeichenkette ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMString that, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (offset > this._length_)) throw new IllegalArgumentException();
		final int count = that._length_;
		if (count == 0) return offset;
		final int value = that._get_(0), length = this._length_ - count;
		FIND: for (int i = offset; i < length; i++) {
			if (value == this._get_(i)) {
				for (int i2 = 1; i2 < count; i2++) {
					if (this._get_(i + i2) != that._get_(i2)) {
						continue FIND;
					}
				}
				return i;
			}
		}
		return -1;
	}

	/** Diese Methode fügt alle Codepoints dieser Zeichenkette vom ersten zum letzten geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	public final boolean extract(final Collector target) throws NullPointerException {
		if (target == null) throw new NullPointerException("target = null");
		if (this._length_ == 0) return true;
		return this._export_(target, 0, this._length_, true);
	}

	/** Diese Methode gibt den Streuwert zurück.
	 * 
	 * @return Streuwert. */
	public final int hash() {
		int result = this._hash_;
		if (result != 0) return result;
		final int length = this._length_;
		final HashCollector collector = new HashCollector();
		this._export_(collector, 0, length, true);
		this._hash_ = (result = (collector.hash | 1));
		return result;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeichenkette gleich der gegebenen ist.
	 * 
	 * @param that Zeichenkette.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMString that) throws NullPointerException {
		final int length = this._length_;
		if (length != that._length_) return false;
		if (this.hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this._get_(i) != that._get_(i)) return false;
		}
		return true;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Zeichenkette kleiner, gleich oder größer als die
	 * der gegebenen Zeichenkette ist.
	 * 
	 * @param that Zeichenkette.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final int compare(final FEMString that) throws NullPointerException {
		final int length = Math.min(this._length_, that._length_);
		for (int i = 0; i < length; i++) {
			final int result = Comparators.compare(this._get_(i), that._get_(i));
			if (result != 0) return result;
		}
		return Comparators.compare(this._length_, that._length_);
	}

	/** Diese Methode gibt die Codepoint in UTF8-Kodierung zurück.
	 * 
	 * @return Array mit den Codepoints in UTF8-Kodierung. */
	public byte[] toBytes() {
		final UTF8Collector target = new UTF8Collector(this._length_);
		this._export_(target, 0, this._length_, true);
		if (target.array.length == target.index) return target.array;
		return Arrays.copyOf(target.array, target.index);
	}

	/** Diese Methode gibt die Codepoint in UTF16-Kodierung zurück.
	 * 
	 * @return Array mit den Codepoints in UTF16-Kodierung. */
	public char[] toChars() {
		final UTF16Collector target = new UTF16Collector(this._length_);
		this._export_(target, 0, this._length_, true);
		if (target.array.length == target.index) return target.array;
		return Arrays.copyOf(target.array, target.index);
	}

	{}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMString data() {
		return this;
	}

	/** Diese Methode gibt {@link #TYPE} zurück. */
	@Override
	public final FEMType<FEMString> type() {
		return FEMString.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMString result() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMString result(final boolean recursive) {
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
		if (!(object instanceof FEMString)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMString)) return false;
		}
		return this.equals((FEMString)object);
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			int _index_ = 0;

			@Override
			public Integer next() {
				return new Integer(FEMString.this._get_(this._index_++));
			}

			@Override
			public boolean hasNext() {
				return this._index_ < FEMString.this._length_;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final FEMFormatter target) throws IllegalArgumentException {
		target.put(FEMParser.formatString(this.toString()));
	}

	/** Diese Methode gibt diesen Zeichenkette als {@link String} zurück.
	 * 
	 * @return {@link String}. */
	@Override
	public final String toString() {
		return new String(this.toChars());
	}

}