package bee.creative.fem;

import java.util.Arrays;
import java.util.Iterator;
import bee.creative.fem.FEM.BaseValue;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.iam.IAMArray;
import bee.creative.mmf.MMFArray;

/** Diese Klasse implementiert eine Zeichenkette, deren Verkettungen, Anschnitte und Umkehrungen als Sichten auf die grundlegenden Zeichenkette realisiert sind.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMString extends BaseValue implements Iterable<Integer> {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Codepoints einer Zeichenkette in der Methode {@link FEMString#export(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebene Codepoint an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
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
		public boolean push(final int value) {
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

		public void push(final byte value) {
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
		public boolean push(final int value) {
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

		public void push(final char value) {
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
		public boolean push(final int value) {
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
		public boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			this.array[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8ArrayString extends FEMString {

		final MMFArray _array_;

		UTF8ArrayString(final MMFArray array) {
			super(FEMString._utf8Count_(array));
			this._array_ = array;
		}

		{}

		@Override
		protected int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf8Size_(this._array_.get(offset));
			}
			return FEMString._utf8Value_(this._array_, offset);
		}

		@Override
		protected boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this._array_.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMString._utf8Value_(this._array_, index))) return false;
					length--;
					index += FEMString._utf8Size_(this._array_.get(index));
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this._array_.get(index));
				}
				while (length > 0) {
					while (!FEMString._utf8Start_(this._array_.get(--index))) {}
					if (!target.push(FEMString._utf8Value_(this._array_, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public FEMString compact() {
			return this;
		}

		@Override
		public byte[] toBytes() {
			return this._array_.toBytes();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16ArrayString extends FEMString {

		final MMFArray _array_;

		UTF16ArrayString(final MMFArray array) {
			super(FEMString._utf16Count_(array));
			this._array_ = array;
		}

		{}

		@Override
		protected int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf16Size_(this._array_.get(offset));
			}
			return FEMString._utf16Value_(this._array_, offset);
		}

		@Override
		protected boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this._array_.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMString._utf16Value_(this._array_, index))) return false;
					length--;
					index += FEMString._utf16Size_(this._array_.get(index));
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this._array_.get(index));
				}
				while (length > 0) {
					while (!FEMString._utf16Start_(this._array_.get(--index))) {}
					if (!target.push(FEMString._utf16Value_(this._array_, index))) return false;
					length--;
				}
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32ArrayString extends FEMString {

		final MMFArray _array_;

		UTF32ArrayString(final MMFArray array) {
			super(array.length());
			this._array_ = array;
		}

		{}

		@Override
		protected int _get_(final int index) throws IndexOutOfBoundsException {
			return this._array_.get(index);
		}

		@Override
		public int[] value() {
			return this._array_.toArray();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8CompactString extends FEMString {

		final byte[] _bytes_;

		UTF8CompactString(final byte[] bytes) throws IllegalArgumentException {
			super(FEMString._utf8Count_(bytes));
			this._bytes_ = bytes;
		}

		{}

		@Override
		protected int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf8Size_(this._bytes_[offset]);
			}
			return FEMString._utf8Value_(this._bytes_, offset);
		}

		@Override
		protected boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this._bytes_[index]);
				}
				while (length > 0) {
					if (!target.push(FEMString._utf8Value_(this._bytes_, index))) return false;
					length--;
					index += FEMString._utf8Size_(this._bytes_[index]);
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf8Size_(this._bytes_[index]);
				}
				while (length > 0) {
					while (!FEMString._utf8Start_(this._bytes_[--index])) {}
					if (!target.push(FEMString._utf8Value_(this._bytes_, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public FEMString compact() {
			return this;
		}

		@Override
		public byte[] toBytes() {
			return this._bytes_.clone();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16CompactString extends FEMString {

		final char[] _chars_;

		UTF16CompactString(final char[] chars) throws IllegalArgumentException {
			super(FEMString._utf16Count_(chars));
			this._chars_ = chars;
		}

		{}

		@Override
		protected int _get_(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString._utf16Size_(this._chars_[offset]);
			}
			return FEMString._utf16Value_(this._chars_, offset);
		}

		@Override
		protected boolean _export_(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this._chars_[index]);
				}
				while (length > 0) {
					if (!target.push(FEMString._utf16Value_(this._chars_, index))) return false;
					length--;
					index += FEMString._utf16Size_(this._chars_[index]);
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString._utf16Size_(this._chars_[index]);
				}
				while (length > 0) {
					while (!FEMString._utf16Start_(this._chars_[--index])) {}
					if (!target.push(FEMString._utf16Value_(this._chars_, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public FEMString compact() {
			return this;
		}

		@Override
		public char[] toChars() {
			return this._chars_.clone();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32CompactString extends FEMString {

		final int[] _values_;

		UTF32CompactString(final int[] values) throws IllegalArgumentException {
			super(values.length);
			this._values_ = values;
		}

		{}

		@Override
		protected int _get_(final int index) throws IndexOutOfBoundsException {
			return this._values_[index];
		}

		@Override
		public int[] value() {
			return this._values_.clone();
		}

		@Override
		public FEMString compact() {
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
		public FEMString reverse() {
			return this;
		}

		@Override
		public FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ConcatString extends FEMString {

		final FEMString _string1_;

		final FEMString _string2_;

		ConcatString(final FEMString string1, final FEMString string2) throws IllegalArgumentException {
			super(string1._length_ + string2._length_);
			this._string1_ = string1;
			this._string2_ = string2;
		}

		{}

		@Override
		protected int _get_(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this._string1_._length_;
			return index2 < 0 ? this._string1_._get_(index) : this._string2_._get_(index2);
		}

		@Override
		protected boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this._string1_._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this._string2_._export_(target, offset2, length, foreward);
			if (length2 <= 0) return this._string1_._export_(target, offset, length, foreward);
			if (foreward) {
				if (!this._string1_._export_(target, offset, -offset2, foreward)) return false;
				return this._string2_._export_(target, 0, length2, foreward);
			} else {
				if (!this._string2_._export_(target, 0, length2, foreward)) return false;
				return this._string1_._export_(target, offset, -offset2, foreward);
			}
		}

		@Override
		public FEMString section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this._string1_._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this._string2_.section(offset2, length);
			if (length2 <= 0) return this._string1_.section(offset, length);
			return super.section(offset, -offset2).concat(this._string2_.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SectionString extends FEMString {

		final FEMString _string_;

		final int _offset_;

		SectionString(final FEMString string, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this._string_ = string;
			this._offset_ = offset;
		}

		{}

		@Override
		protected int _get_(final int index) throws IndexOutOfBoundsException {
			return this._string_._get_(index + this._offset_);
		}

		@Override
		protected boolean _export_(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this._string_._export_(target, this._offset_ + offset2, length2, foreward);
		}

		@Override
		public FEMString section(final int offset2, final int length2) throws IllegalArgumentException {
			return this._string_.section(this._offset_ + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseString extends FEMString {

		final FEMString _string_;

		ReverseString(final FEMString string) throws IllegalArgumentException {
			super(string._length_);
			this._string_ = string;
		}

		{}

		@Override
		protected int _get_(final int index) throws IndexOutOfBoundsException {
			return this._string_._get_(this._length_ - index - 1);
		}

		@Override
		protected boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			return this._string_._export_(target, offset, length, !foreward);
		}

		@Override
		public FEMString concat(final FEMString value) throws NullPointerException {
			return value.reverse().concat(this._string_).reverse();
		}

		@Override
		public FEMString section(final int offset, final int length2) throws IllegalArgumentException {
			return this._string_.section(this._length_ - offset - length2, length2).reverse();
		}

		@Override
		public FEMString reverse() {
			return this._string_;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformString extends FEMString {

		final int _value_;

		UniformString(final int length, final int value) throws IllegalArgumentException {
			super(length);
			this._value_ = value;
		}

		{}

		@Override
		protected int _get_(final int index) throws IndexOutOfBoundsException {
			return this._value_;
		}

		@Override
		protected boolean _export_(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this._value_)) return false;
				length--;
			}
			return true;
		}

		@Override
		public FEMString reverse() {
			return this;
		}

		@Override
		public FEMString compact() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 4;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMString> TYPE = FEMType.from(FEMString.ID, "STRING");

	/** Dieses Feld speichert die leere Zeichenkette. */
	public static final FEMString EMPTY = new EmptyString();

	{}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF32-kodierten Codepoints zurück.
	 * 
	 * @param data UTF32-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public static FEMString from(final int[] data) throws NullPointerException {
		if (data.length == 0) return FEMString.EMPTY;
		return new UTF32CompactString(data.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF8-kodierten Codepoints zurück.
	 * 
	 * @param data UTF8-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final byte[] data) throws NullPointerException, IllegalArgumentException {
		if (data.length == 0) return FEMString.EMPTY;
		return new UTF8CompactString(data.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF16-kodierten Codepoints zurück.
	 * 
	 * @param data UTF16-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final char[] data) throws NullPointerException, IllegalArgumentException {
		if (data.length == 0) return FEMString.EMPTY;
		return new UTF16CompactString(data.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen Codepoints zurück.
	 * 
	 * @param data Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist. */
	public static FEMString from(final String data) throws NullPointerException {
		if (data.length() == 0) return FEMString.EMPTY;
		return new UTF16CompactString(data.toCharArray());
	}

	/** Diese Methode gibt eine uniforme Zeichenkette mit der gegebenen Länge zurück, deren Codepoints alle gleich dem gegebenen sind.
	 * 
	 * @param value Codepoint.
	 * @param length Länge.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMString from(final int value, final int length) throws IllegalArgumentException {
		if (length == 0) return FEMString.EMPTY;
		return new UniformString(length, value);
	}

	/** Diese Methode gibt eine Zeichenkette mit den gegebenen Zahlen zurück.<br>
	 * Abhängig davon, ob die Zahlenliste aus {@link MMFArray#mode() INT8/UINT8}-, {@link MMFArray#mode() INT16/UINT16)} oder {@link MMFArray#mode() INT31}-Zahlen
	 * besteht, werden diese als UTF8-, UTF16- bzw. UTF32-kodierte Codepoints interpretiert.
	 * 
	 * @param data Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final MMFArray data) throws NullPointerException, IllegalArgumentException {
		final int mode = data.mode(), length = data.length();
		if (mode == 0) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return FEMString.from(data.get(0), 1);
		switch (mode) {
			case 1:
				return new UTF8ArrayString(data);
			case 2:
				return new UTF16ArrayString(data);
			case 4:
				return new UTF32ArrayString(data);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMString.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMString from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMString.TYPE);
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
		if (value == 55296) return (((token & 1032) << 10) | (array.get(offset + 1) & 1023)) + 65536;
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
		this.export(target);
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

	/** Diese Methode gibt die {@link #value() Bytes dieser Zeichenkette} in einer performanteren oder zumindest gleichwertigen Zeichenkette zurück.
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
	 * Die Suche beginnt an der gegebenen Position. Wenn das Zeichen nicht gefunden wird, liefert diese Methode {@code -1}.
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

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebene Zeichenkette innerhalb dieser Zeichenkette zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Wenn die Zeichenkette nicht gefunden wird, liefert diese Methode {@code -1}.
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
	public final boolean export(final Collector target) throws NullPointerException {
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

	/** Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn die lexikographische Ordnung dieser Zeichenkette kleiner, gleich
	 * oder größer als die der gegebenen Zeichenkette ist.
	 * 
	 * @param that Zeichenkette.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final int compare(final FEMString that) throws NullPointerException {
		final int length = Math.min(this._length_, that._length_);
		for (int i = 0; i < length; i++) {
			final int result = this._get_(i) - that._get_(i);
			if (result != 0) return result;
		}
		return this._length_ - that._length_;
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

	/** {@inheritDoc} */
	@Override
	public final FEMString data() {
		return this;
	}

	/** {@inheritDoc} */
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
		return this;
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

			int __index = 0;

			@Override
			public Integer next() {
				return new Integer(FEMString.this._get_(this.__index++));
			}

			@Override
			public boolean hasNext() {
				return this.__index < FEMString.this._length_;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.put(FEM.formatString(this.toString()));
	}

	/** Diese Methode gibt diesen Zeichenkette als {@link String} zurück.
	 * 
	 * @return {@link String}. */
	@Override
	public final String toString() {
		return new String(this.toChars());
	}

}