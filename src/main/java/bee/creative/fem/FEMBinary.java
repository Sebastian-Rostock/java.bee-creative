package bee.creative.fem;

import java.util.Iterator;
import bee.creative.fem.FEM.BaseValue;
import bee.creative.mmf.MMFArray;

/**
 * Diese Klasse implementiert eine Bytefolge, deren Verkettungen, Anschnitte und Umkehrungen als Sichten auf die grundlegenden Bytefolgen realisiert sind.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMBinary extends BaseValue implements Iterable<Byte> {

	/**
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Bytes einer Bytefolge in der Methode {@link FEMBinary#export(Collector)}.
	 */
	public static interface Collector {

		/**
		 * Diese Methode fügt das gegebene Byte an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 * 
		 * @param value Byte.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll.
		 */
		public boolean push(byte value);

	}

	@SuppressWarnings ("javadoc")
	static final class HashCollector implements Collector {

		public int hash = 0x811C9DC5;

		{}

		@Override
		public boolean push(final byte value) {
			this.hash = (this.hash * 0x01000193) ^ value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ValueCollector implements Collector {

		public final byte[] array;

		public int index;

		ValueCollector(final int length) {
			this.array = new byte[length];
		}

		{}

		@Override
		public boolean push(final byte value) {
			this.array[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ArrayBinary extends FEMBinary {

		final MMFArray _array_;

		ArrayBinary(final MMFArray array) {
			super(array.length());
			this._array_ = array;
		}

		{}

		@Override
		protected byte _get_(final int index) throws IndexOutOfBoundsException {
			return (byte)this._array_.get(index);
		}

		@Override
		public byte[] value() {
			return this._array_.toBytes();
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyBinary extends FEMBinary {

		EmptyBinary() {
			super(0);
		}

		{}

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
	static final class ConcatBinary extends FEMBinary {

		final FEMBinary _binary1_;

		final FEMBinary _binary2_;

		ConcatBinary(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1._length_ + binary2._length_);
			this._binary1_ = binary1;
			this._binary2_ = binary2;
		}

		{}

		@Override
		protected byte _get_(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this._binary1_._length_;
			return index2 < 0 ? this._binary1_._get_(index) : this._binary2_._get_(index2);
		}

		@Override
		protected boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this._binary1_._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this._binary2_._export_(target, offset2, length, foreward);
			if (length2 <= 0) return this._binary1_._export_(target, offset, length, foreward);
			if (foreward) {
				if (!this._binary1_._export_(target, offset, -offset2, foreward)) return false;
				return this._binary2_._export_(target, 0, length2, foreward);
			} else {
				if (!this._binary2_._export_(target, 0, length2, foreward)) return false;
				return this._binary1_._export_(target, offset, -offset2, foreward);
			}
		}

		@Override
		public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this._binary1_._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this._binary2_.section(offset2, length);
			if (length2 <= 0) return this._binary1_.section(offset, length);
			return super.section(offset, -offset2).concat(this._binary2_.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SectionBinary extends FEMBinary {

		final FEMBinary _binary_;

		final int _offset_;

		SectionBinary(final FEMBinary binary, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this._binary_ = binary;
			this._offset_ = offset;
		}

		{}

		@Override
		protected byte _get_(final int index) throws IndexOutOfBoundsException {
			return this._binary_._get_(index + this._offset_);
		}

		@Override
		protected boolean _export_(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this._binary_._export_(target, this._offset_ + offset2, length2, foreward);
		}

		@Override
		public FEMBinary section(final int offset2, final int length2) throws IllegalArgumentException {
			return this._binary_.section(this._offset_ + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseBinary extends FEMBinary {

		final FEMBinary _binary_;

		ReverseBinary(final FEMBinary binary) throws IllegalArgumentException {
			super(binary._length_);
			this._binary_ = binary;
		}

		{}

		@Override
		protected byte _get_(final int index) throws IndexOutOfBoundsException {
			return this._binary_._get_(this._length_ - index - 1);
		}

		@Override
		protected boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			return this._binary_._export_(target, offset, length, !foreward);
		}

		@Override
		public FEMBinary concat(final FEMBinary value) throws NullPointerException {
			return value.reverse().concat(this._binary_).reverse();
		}

		@Override
		public FEMBinary section(final int offset, final int length2) throws IllegalArgumentException {
			return this._binary_.section(this._length_ - offset - length2, length2).reverse();
		}

		@Override
		public FEMBinary reverse() {
			return this._binary_;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformBinary extends FEMBinary {

		final byte _value_;

		UniformBinary(final int length, final byte value) throws IllegalArgumentException {
			super(length);
			this._value_ = value;
		}

		{}

		@Override
		protected byte _get_(final int index) throws IndexOutOfBoundsException {
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
		public FEMBinary reverse() {
			return this;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CompactBinary extends FEMBinary {

		final byte[] _values_;

		CompactBinary(final byte[] values) throws IllegalArgumentException {
			super(values.length);
			this._values_ = values;
		}

		{}

		@Override
		protected byte _get_(final int index) throws IndexOutOfBoundsException {
			return this._values_[index];
		}

		@Override
		public byte[] value() {
			return this._values_.clone();
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Identifikator von {@link #TYPE}.
	 */
	public static final int ID = 5;

	/**
	 * Dieses Feld speichert den {@link #type() Datentyp}.
	 */
	public static final FEMType<FEMBinary> TYPE = FEMType.from(FEMBinary.ID, "BINARY");

	/**
	 * Dieses Feld speichert die leere Bytefolge.
	 */
	public static final FEMBinary EMPTY = new EmptyBinary();

	{}

	/**
	 * Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück.<br>
	 * Das gegebene Array wird hierbei kopiert.
	 * 
	 * @param value Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMBinary from(final byte[] value) throws NullPointerException {
		if (value.length == 0) return FEMBinary.EMPTY;
		if (value.length == 1) return FEMBinary.from(value[0], 1);
		return new CompactBinary(value.clone());
	}

	/**
	 * Diese Methode gibt eine uniforme Bytefolge mit der gegebenen Länge zurück, deren Bytes alle gleich dem gegebenen sind.
	 * 
	 * @param value Byte.
	 * @param length Länge.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	public static final FEMBinary from(final byte value, final int length) throws IllegalArgumentException {
		if (length == 0) return FEMBinary.EMPTY;
		return new UniformBinary(length, value);
	}

	/**
	 * Diese Methode gibt eine neue Bytefolge mit dem in der gegebenen Zeichenkette kodierten Wert zurück.<br>
	 * Das Format der Zeichenkette entspricht dem der {@link #toString() Textdarstellung}.
	 * 
	 * @see #toString()
	 * @param value Zeichenkette.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zeichenkette ungültig ist.
	 */
	public static final FEMBinary from(final String value) throws NullPointerException, IllegalArgumentException {
		final int length = value.length();
		if ((length < 2) || ((length & 1) != 0) || (value.charAt(0) != '0') || (value.charAt(1) != 'x')) throw new IllegalArgumentException();
		final int count = (length >> 1) - 1;
		final byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++) {
			bytes[i] = (byte)( //
				(FEMBinary._toByte_(value.charAt((i << 1) + 0)) << 4) | //
				(FEMBinary._toByte_(value.charAt((i << 1) + 1)) << 0));
		}
		return new CompactBinary(bytes);
	}

	/**
	 * Diese Methode gibt eine Bytefolge mit den gegebenen Zahlen zurück.
	 * 
	 * @param value Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Zahlenfolge nicht als {@link MMFArray#mode() UNI8/UINT8} vorliegt.
	 */
	public static final FEMBinary from(final MMFArray value) throws NullPointerException, IllegalArgumentException {
		if (value.length() == 0) return FEMBinary.EMPTY;
		if (value.mode() == 1) return new ArrayBinary(value);
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMBinary.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public static final FEMBinary from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMBinary.TYPE);
	}

	/**
	 * Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMBinary.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist.
	 */
	public static final FEMBinary from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMBinary.TYPE);
	}

	@SuppressWarnings ("javadoc")
	static final int _toByte_(final int value) throws IllegalArgumentException {
		int value2 = value - '0';
		if ((value2 >= 0) && (value <= 9)) return value2;
		value2 = value - 'A';
		if ((value2 >= 0) && (value <= 5)) return value2 + 10;
		throw new IllegalArgumentException();
	}

	@SuppressWarnings ("javadoc")
	static final char _toChar_(final int value) {
		final int value2 = value - 10;
		if (value2 < 0) return (char)('0' + value);
		return (char)('A' + value2);
	}

	{}

	/**
	 * Dieses Feld speichert den Streuwert.
	 */
	int _hash_;

	/**
	 * Dieses Feld speichert die Länge.
	 */
	protected final int _length_;

	/**
	 * Dieser Konstruktor initialisiert die Länge.
	 * 
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	protected FEMBinary(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this._length_ = length;
	}

	{}

	/**
	 * Diese Methode gibt das {@code index}-te Byte zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Byte.
	 */
	protected byte _get_(final int index) {
		return 0;
	}

	/**
	 * Diese Methode fügt alle Bytes im gegebenen Abschnitt in der gegebenen Reigenfolge geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reigenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 */
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

	/**
	 * Diese Methode konvertiert diese Bytefolge in ein Array und gibt diese zurück.
	 * 
	 * @return Array mit den Bytes dieser Bytefolge.
	 */
	public byte[] value() {
		final ValueCollector target = new ValueCollector(this._length_);
		this.export(target);
		return target.array;
	}

	/**
	 * Diese Methode gibt das {@code index}-te Byte zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Byte.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist.
	 */
	public final byte get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this._length_)) throw new IndexOutOfBoundsException();
		return this._get_(index);
	}

	/**
	 * Diese Methode gibt die Länge, d.h. die Anzahl der Bytes in der Bytefolge zurück.
	 * 
	 * @return Länge der Bytefolge.
	 */
	public final int length() {
		return this._length_;
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Bytefolge mit der gegebenen Bytefolge zurück.
	 * 
	 * @param that Bytefolge.
	 * @return {@link FEMBinary}-Sicht auf die Verkettung dieser mit der gegebenen Bytefolge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public FEMBinary concat(final FEMBinary that) throws NullPointerException {
		if (that._length_ == 0) return this;
		if (this._length_ == 0) return that;
		return new ConcatBinary(this, that);
	}

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Bytefolge zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMBinary}-Sicht auf einen Abschnitt dieser Bytefolge.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Bytefolge liegt oder eine negative Länge hätte.
	 */
	public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this._length_)) return this;
		if ((offset < 0) || ((offset + length) > this._length_)) throw new IllegalArgumentException();
		if (length == 0) return FEMBinary.EMPTY;
		return new SectionBinary(this, offset, length);
	}

	/**
	 * Diese Methode gibt eine rückwärts geordnete Sicht auf diese Bytefolge zurück.
	 * 
	 * @return rückwärts geordnete {@link FEMBinary}-Sicht auf diese Bytefolge.
	 */
	public FEMBinary reverse() {
		return new ReverseBinary(this);
	}

	/**
	 * Diese Methode gibt die {@link #value() Bytes dieser Bytefolge} in einer performanteren oder zumindest gleichwertigen Bytefolge zurück.
	 * 
	 * @see #from(byte[])
	 * @see #value()
	 * @return performanteren Bytefolge oder {@code this}.
	 */
	public FEMBinary compact() {
		final FEMBinary result = this._length_ == 1 ? new UniformBinary(1, this._get_(0)) : new CompactBinary(this.value());
		result._hash_ = this._hash_;
		return result;
	}

	/**
	 * Diese Methode gibt die Position des ersten Vorkommens der gegebene Bytefolge innerhalb dieser Bytefolge zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Wenn die Bytefolge nicht gefunden wird, liefert diese Methode {@code -1}.
	 * 
	 * @param that gesuchte Bytefolge.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Bytefolge ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist.
	 */
	public final int find(final FEMBinary that, final int offset) throws NullPointerException, IllegalArgumentException {
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

	/**
	 * Diese Methode fügt alle Bytes dieser Bytefolge vom ersten zum letzten geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public final boolean export(final Collector target) throws NullPointerException {
		if (target == null) throw new NullPointerException("target = null");
		if (this._length_ == 0) return true;
		return this._export_(target, 0, this._length_, true);
	}

	/**
	 * Diese Methode gibt den Streuwert zurück.
	 * 
	 * @return Streuwert.
	 */
	public final int hash() {
		int result = this._hash_;
		if (result != 0) return result;
		final int length = this._length_;
		final HashCollector collector = new HashCollector();
		this._export_(collector, 0, length, true);
		this._hash_ = (result = (collector.hash | 1));
		return result;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge gleich der gegebenen ist.
	 * 
	 * @param that Bytefolge.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMBinary that) throws NullPointerException {
		final int length = this._length_;
		if (length != that._length_) return false;
		if (this.hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this._get_(i) != that._get_(i)) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn die lexikographische Ordnung dieser Bytefolge kleiner, gleich
	 * oder größer als die der gegebenen Bytefolge ist.
	 * 
	 * @param that Bytefolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final int compare(final FEMBinary that) throws NullPointerException {
		final int length = Math.min(this._length_, that._length_);
		for (int i = 0; i < length; i++) {
			final int result = (this._get_(i) & 255) - (that._get_(i) & 255);
			if (result != 0) return result;
		}
		return this._length_ - that._length_;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FEMBinary data() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FEMType<FEMBinary> type() {
		return FEMBinary.TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBinary)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMBinary)) return false;
		}
		return this.equals((FEMBinary)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<Byte> iterator() {
		return new Iterator<Byte>() {

			int __index = 0;

			@Override
			public Byte next() {
				return new Byte(FEMBinary.this._get_(this.__index++));
			}

			@Override
			public boolean hasNext() {
				return this.__index < FEMBinary.this._length_;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}

	/**
	 * Diese Methode gibt die Textdarstellung dieser Bytefolge zurück.<br>
	 * Die Textdarstellung besteht aus der Zeichenkette {@code "0x"} und den Bytes dieser Bytefolge vom ersten zum letzten geordnet in hexadezimalen Ziffern, d.h.
	 * {@code 0123456789ABCDEF}.
	 * 
	 * @return Textdarstellung.
	 */
	@Override
	public final String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("0x");
		for (int i = 0, length = this._length_; i < length; i++) {
			final int value = this._get_(i);
			result.append(FEMBinary._toChar_((value >> 4) & 0xF)).append(FEMBinary._toChar_((value >> 0) & 0xF));
		}
		return result.toString();
	}

}