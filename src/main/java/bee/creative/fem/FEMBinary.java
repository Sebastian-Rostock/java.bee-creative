package bee.creative.fem;

import java.util.Iterator;
import bee.creative.mmf.MMFArray;

/**
 * Diese Klasse implementiert eine Bytefolge, deren Verkettungen, Anschnitte und Umkehrungen als Sichten auf die grundlegenden Bytefolgen realisiert sind.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMBinary implements Iterable<Byte> {

	/**
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Bytes einer Bytefolge in der Methode {@link FEMBinary#export(Collector)}.
	 */
	public static interface Collector {

		/**
		 * Diese Methode fügt das gegebene Byte an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 * 
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll.
		 */
		public boolean push(byte value);

	}

	@SuppressWarnings ("javadoc")
	static final class HashCollector implements Collector {

		public int hash = 0x811C9DC5;

		@Override
		public boolean push(final byte value) {
			this.hash = (this.hash * 0x01000193) ^ value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ValueCollector implements Collector {

		public int index;

		public final byte[] value;

		ValueCollector(final int length) {
			this.value = new byte[length];
		}

		{}

		@Override
		public boolean push(final byte value) {
			this.value[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ArrayBinary extends FEMBinary {

		final MMFArray __array;

		ArrayBinary(final MMFArray array) {
			super(array.length());
			this.__array = array;
		}

		{}

		@Override
		protected byte __get(final int index) throws IndexOutOfBoundsException {
			return (byte)this.__array.get(index);
		}

		@Override
		public byte[] value() {
			return this.__array.toBytes();
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

		final FEMBinary __binary1;

		final FEMBinary __binary2;

		ConcatBinary(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1.__length + binary2.__length);
			this.__binary1 = binary1;
			this.__binary2 = binary2;
		}

		{}

		@Override
		protected byte __get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.__binary1.__length;
			return index2 < 0 ? this.__binary1.__get(index) : this.__binary2.__get(index2);
		}

		@Override
		protected boolean __export(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.__binary1.__length, length2 = offset2 + length;
			if (offset2 >= 0) return this.__binary2.__export(target, offset2, length, foreward);
			if (length2 <= 0) return this.__binary1.__export(target, offset, length, foreward);
			if (foreward) {
				if (!this.__binary1.__export(target, offset, -offset2, foreward)) return false;
				return this.__binary2.__export(target, 0, length2, foreward);
			} else {
				if (!this.__binary2.__export(target, 0, length2, foreward)) return false;
				return this.__binary1.__export(target, offset, -offset2, foreward);
			}
		}

		@Override
		public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.__binary1.__length, length2 = offset2 + length;
			if (offset2 >= 0) return this.__binary2.section(offset2, length);
			if (length2 <= 0) return super.section(offset, length);
			return super.section(offset, -offset2).concat(this.__binary2.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SectionBinary extends FEMBinary {

		final FEMBinary __binary;

		final int __offset;

		SectionBinary(final FEMBinary binary, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.__binary = binary;
			this.__offset = offset;
		}

		{}

		@Override
		protected byte __get(final int index) throws IndexOutOfBoundsException {
			return this.__binary.__get(index + this.__offset);
		}

		@Override
		protected boolean __export(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.__binary.__export(target, this.__offset + offset2, length2, foreward);
		}

		@Override
		public FEMBinary section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.__binary.section(this.__offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseBinary extends FEMBinary {

		final FEMBinary __binary;

		ReverseBinary(final FEMBinary binary) throws IllegalArgumentException {
			super(binary.__length);
			this.__binary = binary;
		}

		{}

		@Override
		protected byte __get(final int index) throws IndexOutOfBoundsException {
			return this.__binary.__get(this.__length - index - 1);
		}

		@Override
		protected boolean __export(final Collector target, final int offset, final int length, final boolean foreward) {
			return super.__export(target, offset, length, !foreward);
		}

		@Override
		public FEMBinary concat(final FEMBinary value) throws NullPointerException {
			return value.reverse().concat(this.__binary).reverse();
		}

		@Override
		public FEMBinary section(final int offset, final int length2) throws IllegalArgumentException {
			return this.__binary.section(this.__length - offset - 1, length2).reverse();
		}

		@Override
		public FEMBinary reverse() {
			return this.__binary;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformBinary extends FEMBinary {

		final byte __value;

		UniformBinary(final int length, final byte value) throws IllegalArgumentException {
			super(length);
			this.__value = value;
		}

		{}

		@Override
		protected byte __get(final int index) throws IndexOutOfBoundsException {
			return this.__value;
		}

		@Override
		protected boolean __export(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.__value)) return false;
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

		final byte[] __values;

		CompactBinary(final byte[] values) throws IllegalArgumentException {
			super(values.length);
			this.__values = values;
		}

		{}

		@Override
		protected byte __get(final int index) throws IndexOutOfBoundsException {
			return this.__values[index];
		}

		@Override
		public byte[] value() {
			return this.__values.clone();
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die leere Bytefolge.
	 */
	public static final FEMBinary EMPTY = new EmptyBinary();

	{}

	/**
	 * Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück.<br>
	 * Das gegebene Array wird hierbei kopiert.
	 * 
	 * @param data Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMBinary from(final byte[] data) throws NullPointerException {
		if (data.length == 0) return FEMBinary.EMPTY;
		if (data.length == 1) return FEMBinary.from(data[0], 1);
		return new CompactBinary(data.clone());
	}

	/**
	 * Diese Methode gibt eine uniforme Bytefolge mit der gegebenen Länge zurück, deren Bytes alle gleich dem gegebenen Wert sind.
	 * 
	 * @param value Wert aller Bytes.
	 * @param length Länge.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	public static FEMBinary from(final byte value, final int length) throws IllegalArgumentException {
		if (length == 0) return FEMBinary.EMPTY;
		return new UniformBinary(length, value);
	}

	/**
	 * Diese Methode gibt eine Bytefolge mit den gegebenen Zahlen zurück.
	 * 
	 * @param data Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMBinary from(final MMFArray data) throws NullPointerException {
		if (data.length() == 0) return FEMBinary.EMPTY;
		return new ArrayBinary(data.toINT8());
	}

	{}

	/**
	 * Dieses Feld speichert den Streuwert.
	 */
	int __hash;

	/**
	 * Dieses Feld speichert die Länge.
	 */
	protected final int __length;

	/**
	 * Dieser Konstruktor initialisiert die Länge.
	 * 
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	FEMBinary(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this.__length = length;
	}

	{}

	/**
	 * Diese Methode gibt das {@code index}-te Byte zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Byte.
	 */
	protected byte __get(final int index) {
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
	protected boolean __export(final Collector target, int offset, int length, final boolean foreward) {
		if (foreward) {
			for (length += offset; offset < length; offset++) {
				if (!target.push(this.__get(offset))) return false;
			}
		} else {
			for (length += offset - 1; offset <= length; length--) {
				if (!target.push(this.__get(length))) return false;
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
		final int length = this.__length;
		final ValueCollector collector = new ValueCollector(length);
		this.__export(collector, 0, length, true);
		return collector.value;
	}

	/**
	 * Diese Methode gibt das {@code index}-te Byte zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Byte.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist.
	 */
	public final byte get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.__length)) throw new IndexOutOfBoundsException();
		return this.__get(index);
	}

	/**
	 * Diese Methode gibt die Länge, d.h. die Anzahl der Bytes in der Bytefolge zurück.
	 * 
	 * @return Länge der Bytefolge.
	 */
	public final int length() {
		return this.__length;
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Bytefolge mit der gegebenen Bytefolge zurück.
	 * 
	 * @param that Bytefolge.
	 * @return {@link FEMBinary}-Sicht auf die Verkettung dieser mit der gegebenen Bytefolge.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public FEMBinary concat(final FEMBinary that) throws NullPointerException {
		if (that.__length == 0) return this;
		if (this.__length == 0) return that;
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
		if ((offset == 0) && (length == this.__length)) return this;
		if ((offset < 0) || ((offset + length) > this.__length)) throw new IllegalArgumentException();
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
		final FEMBinary result = this.__length == 1 ? new UniformBinary(1, this.__get(0)) : new CompactBinary(this.value());
		result.__hash = this.__hash;
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
		if ((offset < 0) || (offset > this.__length)) throw new IllegalArgumentException();
		final int count = that.__length;
		if (count == 0) return offset;
		final int value = that.__get(0), length = this.__length - count;
		FIND: for (int i = offset; i < length; i++) {
			if (value == this.__get(i)) {
				for (int i2 = 1; i2 < count; i2++) {
					if (this.__get(offset + i2) != that.__get(i2)) {
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
		return this.__export(target, 0, this.__length, true);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge gleich der gegebenen ist.
	 * 
	 * @param that Bytefolge.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMBinary that) throws NullPointerException {
		final int length = this.__length;
		if (length != that.__length) return false;
		if (hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this.__get(i) != that.__get(i)) return false;
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
		final int length = Math.min(this.__length, that.__length);
		for (int i = 0; i < length; i++) {
			final int result = (this.__get(i) & 255) - (that.__get(i) & 255);
			if (result != 0) return result;
		}
		return this.__length - that.__length;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		int result = this.__hash;
		if (result != 0) return result;
		final int length = this.__length;
		final HashCollector collector = new HashCollector();
		this.__export(collector, 0, length, true);
		this.__hash = (result = (collector.hash | 1));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMBinary)) return false;
		return this.equals((FEMBinary)object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<Byte> iterator() {
		return new Iterator<Byte>() {

			int index = 0;

			@Override
			public Byte next() {
				return new Byte(FEMBinary.this.__get(this.index++));
			}

			@Override
			public boolean hasNext() {
				return this.index < FEMBinary.this.__length;
			}

			@Override
			public void remove() {
				throw new IllegalStateException();
			}

		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return FEM.formatBinary(this);
	}

}