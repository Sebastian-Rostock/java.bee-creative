package bee.creative.fem;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import bee.creative.iam.IAMArray;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;

/** Diese Klasse implementiert eine unveränderliche Zeichenkette sowie Methoden zur Erzeugung solcher Zeichenketten aus nativen Arrays.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMString extends FEMValue implements Iterable<Integer>, Comparable<FEMString>, UseToString {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Codepoints einer Zeichenkette in der Methode {@link FEMString#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Codepoint an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammeln fortgeführt werden soll.
		 *
		 * @param value Codepoints.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(int value);

	}

	@SuppressWarnings ("javadoc")
	static final class ValueFinder implements Collector {

		public final int that;

		public int index;

		ValueFinder(final int that) {
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
	static final class StringHasher implements Collector {

		public int hash = Objects.hashInit();

		{}

		@Override
		public final boolean push(final int value) {
			this.hash = Objects.hashPush(this.hash, value);
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8Counter implements Collector {

		public int count;

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			if (value < 128) {
				this.count += 1;
			} else if (value < 2048) {
				this.count += 2;
			} else if (value < 65536) {
				this.count += 3;
			} else {
				this.count += 4;
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8Encoder implements Collector {

		public final byte[] array;

		public int index;

		UTF8Encoder(final byte[] array, final int index) {
			this.array = array;
			this.index = index;
		}

		{}

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			int index = this.index;
			if (value < 128) {
				this.array[index++] = (byte)value;
			} else if (value < 2048) {
				this.array[index++] = (byte)(192 | (value >> 6));
				this.array[index++] = (byte)(128 | (value & 63));
			} else if (value < 65536) {
				this.array[index++] = (byte)(224 | (value >> 12));
				this.array[index++] = (byte)(128 | ((value >> 6) & 63));
				this.array[index++] = (byte)(128 | (value & 63));
			} else {
				this.array[index++] = (byte)(240 | (value >> 18));
				this.array[index++] = (byte)(128 | ((value >> 12) & 63));
				this.array[index++] = (byte)(128 | ((value >> 6) & 63));
				this.array[index++] = (byte)(128 | (value & 63));
			}
			this.index = index;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16Counter implements Collector {

		public int count;

		{}

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			if (value < 65536) {
				this.count += 1;
			} else {
				this.count += 2;
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16Encoder implements Collector {

		public final char[] array;

		public int index;

		UTF16Encoder(final char[] array, final int index) {
			this.array = array;
			this.index = index;
		}

		{}

		@Override
		public final boolean push(final int value) {
			if (value < 0) throw new IllegalArgumentException();
			final int value2 = value - 65536;
			int index = this.index;
			if (value2 < 0) {
				this.array[index++] = (char)value;
			} else {
				this.array[index++] = (char)(55296 | (value2 >> 10));
				this.array[index++] = (char)(56320 | (value2 & 1023));
			}
			this.index = index;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32Encoder implements Collector {

		public final int[] array;

		public int index;

		public UTF32Encoder(final int[] array, final int index) {
			this.array = array;
			this.index = index;
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

		public final IAMArray array;

		UTF8ArrayString(final int length, final IAMArray array) {
			super(length);
			if (length > array.length()) throw new IllegalStateException();
			this.array = array;
		}

		{}

		@Override
		protected final int customGet(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString.utf8Size(this.array.get(offset));
			}
			return FEMString.utf8Codepoint(this.array, offset);
		}

		@Override
		protected final boolean customExtract(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString.utf8Size(this.array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMString.utf8Codepoint(this.array, index))) return false;
					length--;
					index += FEMString.utf8Size(this.array.get(index));
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString.utf8Size(this.array.get(index));
				}
				while (length > 0) {
					while (!FEMString.utf8Header(this.array.get(--index))) {}
					if (!target.push(FEMString.utf8Codepoint(this.array, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16ArrayString extends FEMString {

		public final IAMArray array;

		UTF16ArrayString(final int length, final IAMArray array) {
			super(length);
			if (length > array.length()) throw new IllegalStateException();
			this.array = array;
		}

		{}

		@Override
		protected final int customGet(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString.utf16Size(this.array.get(offset));
			}
			return FEMString.utf16Codepoint(this.array, offset);
		}

		@Override
		protected final boolean customExtract(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString.utf16Size(this.array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEMString.utf16Codepoint(this.array, index))) return false;
					length--;
					index += FEMString.utf16Size(this.array.get(index));
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString.utf16Size(this.array.get(index));
				}
				while (length > 0) {
					while (!FEMString.utf16Header(this.array.get(--index))) {}
					if (!target.push(FEMString.utf16Codepoint(this.array, index))) return false;
					length--;
				}
			}
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF32ArrayString extends FEMString {

		public final IAMArray array;

		UTF32ArrayString(final int length, final IAMArray array) {
			super(length);
			if (length > array.length()) throw new IllegalStateException();
			this.array = array;
		}

		{}

		@Override
		protected final int customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.get(index);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF8CompactString extends FEMString {

		/** Dieses Feld speichert das Array der UTF-8-Token, das nicht verändert werden darf. */
		public final byte[] items;

		UTF8CompactString(final byte[] items) throws IllegalArgumentException {
			super(FEMString.utf8Length(items));
			this.items = items;
		}

		{}

		@Override
		protected final int customGet(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString.utf8Size(this.items[offset]);
			}
			return FEMString.utf8Codepoint(this.items, offset);
		}

		@Override
		protected final boolean customExtract(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString.utf8Size(this.items[index]);
				}
				while (length > 0) {
					if (!target.push(FEMString.utf8Codepoint(this.items, index))) return false;
					length--;
					index += FEMString.utf8Size(this.items[index]);
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString.utf8Size(this.items[index]);
				}
				while (length > 0) {
					while (!FEMString.utf8Header(this.items[--index])) {}
					if (!target.push(FEMString.utf8Codepoint(this.items, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UTF16CompactString extends FEMString {

		/** Dieses Feld speichert das Array der UTF-16-Token, das nicht verändert werden darf. */
		public final char[] items;

		UTF16CompactString(final char[] items) throws IllegalArgumentException {
			super(FEMString.utf16Length(items));
			this.items = items;
		}

		{}

		@Override
		protected final int customGet(int index) throws IndexOutOfBoundsException {
			int offset = 0;
			while (index > 0) {
				index--;
				offset += FEMString.utf16Size(this.items[offset]);
			}
			return FEMString.utf16Codepoint(this.items, offset);
		}

		@Override
		protected final boolean customExtract(final Collector target, int offset, int length, final boolean foreward) {
			if (foreward) {
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEMString.utf16Size(this.items[index]);
				}
				while (length > 0) {
					if (!target.push(FEMString.utf16Codepoint(this.items, index))) return false;
					length--;
					index += FEMString.utf16Size(this.items[index]);
				}
			} else {
				int index = 0;
				offset += length;
				while (offset > 0) {
					offset--;
					index += FEMString.utf16Size(this.items[index]);
				}
				while (length > 0) {
					while (!FEMString.utf16Header(this.items[--index])) {}
					if (!target.push(FEMString.utf16Codepoint(this.items, index))) return false;
					length--;
				}
			}
			return true;
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class UTF32CompactString extends FEMString {

		/** Dieses Feld speichert das Array der UTF-32-Token, das nicht verändert werden darf. */
		public final int[] items;

		UTF32CompactString(final int[] items) throws IllegalArgumentException {
			super(items.length);
			this.items = items;
		}

		{}

		@Override
		protected final int customGet(final int index) throws IndexOutOfBoundsException {
			return this.items[index];
		}

		@Override
		public final FEMString compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class EmptyString extends FEMString {

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
	public static final class ConcatString extends FEMString {

		public final FEMString string1;

		public final FEMString string2;

		ConcatString(final FEMString string1, final FEMString string2) throws IllegalArgumentException {
			super(string1.length + string2.length);
			this.string1 = string1;
			this.string2 = string2;
		}

		{}

		@Override
		protected final int customGet(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.string1.length;
			return index2 < 0 ? this.string1.customGet(index) : this.string2.customGet(index2);
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.string1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.string2.customExtract(target, offset2, length, foreward);
			if (length2 <= 0) return this.string1.customExtract(target, offset, length, foreward);
			if (foreward) {
				if (!this.string1.customExtract(target, offset, -offset2, foreward)) return false;
				return this.string2.customExtract(target, 0, length2, foreward);
			} else {
				if (!this.string2.customExtract(target, 0, length2, foreward)) return false;
				return this.string1.customExtract(target, offset, -offset2, foreward);
			}
		}

		@Override
		public final FEMString section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.string1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.string2.section(offset2, length);
			if (length2 <= 0) return this.string1.section(offset, length);
			return this.string1.section(offset, -offset2).concat(this.string2.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class SectionString extends FEMString {

		public final FEMString string;

		public final int offset;

		SectionString(final FEMString string, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.string = string;
			this.offset = offset;
		}

		{}

		@Override
		protected final int customGet(final int index) throws IndexOutOfBoundsException {
			return this.string.customGet(index + this.offset);
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.string.customExtract(target, this.offset + offset2, length2, foreward);
		}

		@Override
		public final FEMString section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.string.section(this.offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class ReverseString extends FEMString {

		public final FEMString string;

		ReverseString(final FEMString string) throws IllegalArgumentException {
			super(string.length);
			this.string = string;
		}

		{}

		@Override
		protected final int customGet(final int index) throws IndexOutOfBoundsException {
			return this.string.customGet(this.length - index - 1);
		}

		@Override
		protected final boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.string.customExtract(target, offset, length, !foreward);
		}

		@Override
		public final FEMString concat(final FEMString value) throws NullPointerException {
			return value.reverse().concat(this.string).reverse();
		}

		@Override
		public final FEMString section(final int offset, final int length2) throws IllegalArgumentException {
			return this.string.section(this.length - offset - length2, length2).reverse();
		}

		@Override
		public final FEMString reverse() {
			return this.string;
		}

	}

	@SuppressWarnings ("javadoc")
	public static final class UniformString extends FEMString {

		public final int item;

		UniformString(final int length, final int item) throws IllegalArgumentException {
			super(length);
			this.item = item;
		}

		{}

		@Override
		protected final int customGet(final int index) throws IndexOutOfBoundsException {
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

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF32-kodierten Codepoints zurück.<br>
	 * Das gegebene Array wird kopiert.
	 *
	 * @param items UTF32-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMString from(final int[] items) throws NullPointerException {
		if (items.length == 0) return FEMString.EMPTY;
		if (items.length == 1) return new UniformString(1, items[0]);
		return new UTF32CompactString(items.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF32-kodierten Codepoints zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items UTF32-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMString from(final boolean copy, final int[] items) throws NullPointerException {
		if (copy) return FEMString.from(items);
		return new UTF32CompactString(items);
	}

	/** Diese Methode gibt eine Zeichenkette mit den UTF32-kodierte Codepoints im gegebenen Abschnitt zurück.<br>
	 * Der gegebene Abschnitt wird kopiert.
	 *
	 * @param items UTF32-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(final int[] items, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return new UniformString(1, items[offset]);
		final int[] result = new int[length];
		System.arraycopy(items, offset, result, 0, length);
		return new UTF32CompactString(result);
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF8-kodierten Codepoints zurück.<br>
	 * Das gegebene Array wird kopiert.
	 *
	 * @param items UTF8-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final byte[] items) throws NullPointerException, IllegalArgumentException {
		if (items.length == 0) return FEMString.EMPTY;
		return new UTF8CompactString(items.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF8-kodierten Codepoints zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items UTF8-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final boolean copy, final byte[] items) throws NullPointerException, IllegalArgumentException {
		if (copy) return FEMString.from(items);
		return new UTF8CompactString(items);
	}

	/** Diese Methode gibt eine Zeichenkette mit den UTF8-kodierte Codepoints im gegebenen Abschnitt zurück.<br>
	 * Der gegebene Abschnitt wird kopiert.
	 *
	 * @param items UTF8-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(final byte[] items, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		final byte[] result = new byte[length];
		System.arraycopy(items, offset, result, 0, length);
		return new UTF8CompactString(result);
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF16-kodierten Codepoints zurück.<br>
	 * Das gegebene Array wird kopiert.
	 *
	 * @param items UTF16-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final char[] items) throws NullPointerException, IllegalArgumentException {
		if (items.length == 0) return FEMString.EMPTY;
		return new UTF16CompactString(items.clone());
	}

	/** Diese Methode eine Zeichenkette mit den gegebenen UTF16-kodierten Codepoints zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items UTF16-kodierte Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final boolean copy, final char[] items) throws NullPointerException, IllegalArgumentException {
		if (copy) return FEMString.from(items);
		return new UTF16CompactString(items);
	}

	/** Diese Methode gibt eine Zeichenkette mit den UTF16-kodierte Codepoints im gegebenen Abschnitt zurück.<br>
	 * Der gegebene Abschnitt wird kopiert.
	 *
	 * @param items UTF16-kodierte Codepoints.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMString from(final char[] items, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMString.EMPTY;
		final char[] result = new char[length];
		System.arraycopy(items, offset, result, 0, length);
		return new UTF16CompactString(result);
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

	/** Diese Methode konvertiert die gegebenen Codepoints in eine Zeichenkette und gibt diese zurück.
	 *
	 * @see #from(int[])
	 * @see Number#intValue()
	 * @param items Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMString from(final List<? extends Number> items) throws NullPointerException {
		final int length = items.size();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return FEMString.from(items.get(0).intValue(), 1);
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = items.get(i).intValue();
		}
		return FEMString.from(false, result);
	}

	/** Diese Methode konvertiert die gegebenen Codepoints in eine Zeichenkette und gibt diese zurück.
	 *
	 * @see #from(List)
	 * @see Iterables#toList(Iterable)
	 * @param items Codepoints.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMString from(final Iterable<? extends Number> items) throws NullPointerException {
		return FEMString.from(Iterables.toList(items));
	}

	/** Diese Methode ist eine Abkürzung für {@code from(array, false)} und die Umkehroperation zu {@link #toArray(int)}.
	 *
	 * @see #from(IAMArray, boolean)
	 * @param array Zahlenfolge.
	 * @return {@link FEMString}-Sicht auf die gegebene Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final IAMArray array) throws NullPointerException, IllegalArgumentException {
		final int length = array.length();
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return FEMString.from(array.get(0), 1);
		switch (array.mode()) {
			case 1:
				return new UTF8ArrayString(FEMString.utf8Length(array), array);
			case 2:
				return new UTF16ArrayString(FEMString.utf16Length(array), array);
			case 4:
				return new UTF32ArrayString(length, array);
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt eine Zeichenkette mit den gegebenen Zahlen zurück. Sie ist die Umkehroperation zu {@link #toArray(int, boolean)}.<br>
	 * Wenn die gegebene Zahlenliste aus {@link IAMArray#mode() 1}, {@link IAMArray#mode() 2} oder {@link IAMArray#mode() 4} Byte großen Zahlen besteht, werden
	 * diese als UTF8-, UTF16- bzw. UTF32-kodierte Codepoints interpretiert.
	 * <p>
	 * Bei einer strukturierten Zahlenfolge geben die Zahlen in den ersten vier Byte der Zahlenfolge die Länge der Zeichenkette an. Diese Zahlen werden gemäß
	 * <em>little-endian</em> Semantik zusammengeführt. Die letzte Zahl einer solchen Zahlenfolge ist stets {@code 0}. Die Zahlen zwischen Längenangabe und
	 * Nullterminierung enthalten dann die Codepoints. Eine unstrukturierte Zahlenfolge ehthält dagegen nur die Codepoints.
	 *
	 * @param array Zahlenfolge.
	 * @param structured {@code true}, wenn die Zahlenfolge strukturiert ist.
	 * @return {@link FEMString}-Sicht auf die gegebene Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public static FEMString from(final IAMArray array, final boolean structured) throws NullPointerException, IllegalArgumentException {
		if (!structured) return FEMString.from(array);
		final int count = array.length(), length;
		switch (array.mode()) {
			case 1:
				if (count < 5) throw new IllegalArgumentException();
				length = (array.get(0) << 0) | (array.get(1) << 8) | (array.get(2) << 16) | (array.get(3) << 24);
				if (length < 0) throw new IllegalArgumentException();
				if (length == 0) return FEMString.EMPTY;
				if (length == 1) return FEMString.from(FEMString.utf8Codepoint(array, 4), 1);
				return new UTF8ArrayString(length, array.section(4, count - 5));
			case 2:
				if (count < 3) throw new IllegalArgumentException();
				length = (array.get(0) << 0) | (array.get(1) << 16);
				if (length == 0) return FEMString.EMPTY;
				if (length == 1) return FEMString.from(FEMString.utf16Codepoint(array, 2), 1);
				return new UTF16ArrayString(length, array.section(2, count - 3));
			case 4:
				if (count < 2) throw new IllegalArgumentException();
				length = array.get(0);
				if (length < 0) throw new IllegalArgumentException();
				if (length == 0) return FEMString.EMPTY;
				if (length == 1) return FEMString.from(array.get(0), 1);
				return new UTF32ArrayString(length, array.section(1, count - 2));
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

	/** Diese Methode gibt die Verkettung der gegebenen Zeichenketten zurück.
	 *
	 * @see #concat(FEMString)
	 * @param values Zeichenketten.
	 * @return Verkettung der Zeichenketten.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public static FEMString concatAll(final FEMString... values) throws NullPointerException {
		final int length = values.length;
		if (length == 0) return FEMString.EMPTY;
		if (length == 1) return values[0].data();
		return FEMString.concatAll(values, 0, length - 1);
	}

	@SuppressWarnings ("javadoc")
	static FEMString concatAll(final FEMString[] values, final int min, final int max) throws NullPointerException {
		if (min == max) return values[min];
		final int mid = (min + max) >> 1;
		return FEMString.concatAll(values, min, mid).concat(FEMString.concatAll(values, mid + 1, max));
	}

	/** Diese Methode gibt die Anzahl der Token für den UTF8-kodierten Codepoint zurück, der am gegebenen Token beginnt.
	 *
	 * @param item Token, an dem ein UTF8-kodierter Codepoint beginnt.
	 * @return Anzahl der Token für den UTF8-kodierten Codepoint.
	 * @throws IllegalArgumentException Wenn {@code token} ungültig ist. */
	static int utf8Size(final int item) throws IllegalArgumentException {
		switch ((item >> 4) & 15) {
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
	 * @param item Token.
	 * @return {@code true}, wenn ein UTF8-kodierter Codepoint am Token beginnt. */
	static boolean utf8Header(final int item) {
		return (item & 192) != 128;
	}

	/** Diese Methode gibt die Anzahl an UTF8-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param array Tokenliste.
	 * @return Anzahl an UTF8-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf8Length(final byte[] array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length;
		while (index < length) {
			result++;
			index += FEMString.utf8Size(array[index]);
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt die Anzahl an UTF8-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param array Tokenliste.
	 * @return Anzahl an UTF8-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf8Length(final IAMArray array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEMString.utf8Size(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt den UTF8-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 *
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF8-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf8Codepoint(final byte[] array, final int offset) throws IllegalArgumentException {
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
	static int utf8Codepoint(final IAMArray array, final int offset) throws IllegalArgumentException {
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

	/** Diese Methode gibt die Anzahl der Token für den UTF16-kodierten Codepoint zurück, der am gegebenen Token beginnt.
	 *
	 * @param item Token, an dem ein UTF16-kodierter Codepoint beginnt.
	 * @return Anzahl der Token für den UTF16-kodierten Codepoint.
	 * @throws IllegalArgumentException Wenn {@code token} ungültig ist. */
	static int utf16Size(final int item) throws IllegalArgumentException {
		final int value = item & 64512;
		if (value == 55296) return 2;
		if (value != 56320) return 1;
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Token den Beginn eines UTF16-kodierten Codepoints ist.
	 *
	 * @param item Token.
	 * @return {@code true}, wenn ein UTF16-kodierter Codepoint am Token beginnt. */
	static boolean utf16Header(final int item) {
		return (item & 64512) != 56320;
	}

	/** Diese Methode gibt die Anzahl an UTF16-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param array Tokenliste.
	 * @return Anzahl an UTF16-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf16Length(final char[] array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length;
		while (index < length) {
			result++;
			index += FEMString.utf16Size(array[index]);
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt die Anzahl an UTF16-kodierten Codepoints in der gegebenen Tokenliste zurück.
	 *
	 * @param array Tokenliste.
	 * @return Anzahl an UTF16-kodierten Codepoints.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf16Length(final IAMArray array) throws IllegalArgumentException {
		int index = 0, result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEMString.utf16Size(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/** Diese Methode gibt den UTF16-kodierten Codepoint zurück, der an der gegebenen Position beginnt.
	 *
	 * @param array Tokenliste.
	 * @param offset Position des Tokens, an dem der UTF16-kodierte Codepoint beginnt..
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	static int utf16Codepoint(final char[] array, final int offset) throws IllegalArgumentException {
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
	static int utf16Codepoint(final IAMArray array, final int offset) throws IllegalArgumentException {
		final int token = array.get(offset), value = token & 64512;
		if (value == 55296) return (((token & 1023) << 10) | (array.get(offset + 1) & 1023)) + 65536;
		if (value != 56320) return token;
		throw new IllegalArgumentException();
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
	protected FEMString(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this.length = length;
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Codepoint zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-ter Codepoint. */
	protected int customGet(final int index) {
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

	/** Diese Methode gibt die Codepoint in UTF32-Kodierung zurück.
	 *
	 * @return Array mit den Codepoints in UTF32-Kodierung. */
	public final int[] value() {
		final UTF32Encoder encoder = new UTF32Encoder(new int[this.length], 0);
		this.extract(encoder);
		return encoder.array;
	}

	/** Diese Methode gibt das {@code index}-te Zeichen als Codepoint zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Zeichen.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	public final int get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.customGet(index);
	}

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Zeichen in der Zeichenkette zurück.
	 *
	 * @return Länge der Zeichenkette. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Zeichenkette mit der gegebenen Zeichenkette zurück.
	 *
	 * @param that Zeichenkette.
	 * @return {@link FEMString}-Sicht auf die Verkettung dieser mit der gegebenen Zeichenkette.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public FEMString concat(final FEMString that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
		return new ConcatString(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@code this.section(offset, this.length - offset)}.
	 *
	 * @see #length()
	 * @see #section(int, int) */
	@SuppressWarnings ("javadoc")
	public FEMString section(final int offset) throws IllegalArgumentException {
		return this.section(offset, this.length - offset);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Zeichenkette zurück.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMString}-Sicht auf einen Abschnitt dieser Zeichenkette.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Zeichenkette liegt oder eine negative Länge hätte. */
	public FEMString section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
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
		final FEMString result = this.length == 1 ? new UniformString(1, this.customGet(0)) : new UTF8CompactString(this.toBytes());
		result.hash = this.hash;
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
		final int length = this.length - offset;
		if ((offset < 0) || (length < 0)) throw new IllegalArgumentException();
		final ValueFinder finder = new ValueFinder(that);
		if (this.customExtract(finder, offset, length, true)) return -1;
		return finder.index + offset;
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

	/** Diese Methode fügt alle Codepoints dieser Zeichenkette vom ersten zum letzten geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	public final boolean extract(final Collector target) throws NullPointerException {
		Objects.assertNotNull(target);
		if (this.length == 0) return true;
		return this.customExtract(target, 0, this.length, true);
	}

	/** Diese Methode kopiert alle Codepoints dieser Zeichenkette vom ersten zum letzten geordnet in den an der gegebenen Position beginnenden Abschnitt des
	 * gegebenen Arrays.
	 *
	 * @param result Array, in welchem der Abschnitt liegt.
	 * @param offset Beginn des Abschnitts.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschitt außerhalb des gegebenen Arrays liegt. */
	public final void extract(final int[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || ((offset + this.length) > result.length)) throw new IllegalArgumentException();
		this.extract(new UTF32Encoder(result, offset));
	}

	/** Diese Methode gibt den Streuwert zurück.
	 *
	 * @return Streuwert. */
	public final int hash() {
		int result = this.hash;
		if (result != 0) return result;
		final StringHasher hasher = new StringHasher();
		this.extract(hasher);
		result = hasher.hash;
		return this.hash = result != 0 ? result : 1;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeichenkette gleich der gegebenen ist.
	 *
	 * @param that Zeichenkette.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMString that) throws NullPointerException {
		final int length = this.length;
		if (length != that.length) return false;
		if (this.hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this.customGet(i) != that.customGet(i)) return false;
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
		final int length = Math.min(this.length, that.length);
		for (int i = 0; i < length; i++) {
			final int result = Comparators.compare(this.customGet(i), that.customGet(i));
			if (result != 0) return result;
		}
		return Comparators.compare(this.length, that.length);
	}

	/** Diese Methode gibt eine unveränderliche {@link List} als Sicht auf die Codepoints dieser Zeichenkette zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return {@link List}-Sicht. */
	public final List<Integer> toList() {
		return new AbstractList<Integer>() {

			@Override
			public Integer get(final int index) {
				return new Integer(FEMString.this.get(index));
			}

			@Override
			public int size() {
				return FEMString.this.length;
			}

		};
	}

	/** Diese Methode gibt die Codepoint in UTF8-Kodierung zurück.
	 *
	 * @return Array mit den Codepoints in UTF8-Kodierung. */
	public final byte[] toBytes() {
		final UTF8Counter counter = new UTF8Counter();
		this.extract(counter);
		final UTF8Encoder encoder = new UTF8Encoder(new byte[counter.count], 0);
		this.extract(encoder);
		return encoder.array;
	}

	/** Diese Methode gibt die Codepoint in UTF16-Kodierung zurück.
	 *
	 * @return Array mit den Codepoints in UTF16-Kodierung. */
	public final char[] toChars() {
		final UTF16Counter counter = new UTF16Counter();
		this.extract(counter);
		final UTF16Encoder encoder = new UTF16Encoder(new char[counter.count], 0);
		this.extract(encoder);
		return encoder.array;
	}

	/** Diese Methode ist eine Abkürzung für {@code toArray(mode, false)} und die Umkehroperation zu {@link #from(IAMArray)}.
	 *
	 * @param mode Kodierungskennung mit {@code 1} für {@code UTF8}, {@code 2} für {@code UTF16} und {@code 4} für {@code UTF32}.
	 * @return Zahlenfolge mit der entsprechend kodierten Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public final IAMArray toArray(final int mode) throws IllegalArgumentException {
		switch (mode) {
			case 1:
				return IAMArray.from(this.toBytes());
			case 2:
				return IAMArray.from(this.toChars());
			case 4:
				return IAMArray.from(this.value());
		}
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt eine Zahlenfolge zurück, welche die kodierten Codepoints dieser Zeichenkette enthält. Sie ist die Umkehroperation zu
	 * {@link #from(IAMArray, boolean)}.
	 *
	 * @see #value()
	 * @see #toBytes()
	 * @see #toChars()
	 * @param mode Kodierungskennung mit {@code 1} für {@code UTF8}, {@code 2} für {@code UTF16} und {@code 4} für {@code UTF32}.
	 * @param structured {@code true}, wenn die Zahlenfolge strukturiert sein soll.
	 * @return Zahlenfolge mit der entsprechend kodierten Zeichenkette.
	 * @throws IllegalArgumentException Wenn die Kodierung ungültig ist. */
	public final IAMArray toArray(final int mode, final boolean structured) throws IllegalArgumentException {
		if (!structured) return this.toArray(mode);
		switch (mode) {
			case 1: {
				final UTF8Counter counter = new UTF8Counter();
				this.extract(counter);
				final UTF8Encoder encoder = new UTF8Encoder(new byte[counter.count + 5], 4);
				this.extract(encoder);
				encoder.array[0] = (byte)(this.length >>> 0);
				encoder.array[1] = (byte)(this.length >>> 8);
				encoder.array[2] = (byte)(this.length >>> 16);
				encoder.array[3] = (byte)(this.length >>> 24);
				return IAMArray.from(encoder.array);
			}
			case 2: {
				final UTF16Counter counter = new UTF16Counter();
				this.extract(counter);
				final UTF16Encoder encoder = new UTF16Encoder(new char[counter.count + 3], 2);
				this.extract(encoder);
				encoder.array[0] = (char)(this.length >>> 0);
				encoder.array[1] = (char)(this.length >>> 16);
				return IAMArray.from(encoder.array);
			}
			case 4: {
				final UTF32Encoder encoder = new UTF32Encoder(new int[this.length + 2], 1);
				this.extract(encoder);
				encoder.array[0] = this.length;
				return IAMArray.from(encoder.array);
			}
		}
		throw new IllegalArgumentException();
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

			int index = 0;

			@Override
			public Integer next() {
				return new Integer(FEMString.this.customGet(this.index++));
			}

			@Override
			public boolean hasNext() {
				return this.index < FEMString.this.length;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final FEMString o) {
		return this.compare(o);
	}

	/** Diese Methode gibt diesen Zeichenkette als {@link String} zurück.
	 *
	 * @return {@link String}. */
	@Override
	public final String toString() {
		return new String(this.toChars());
	}

}