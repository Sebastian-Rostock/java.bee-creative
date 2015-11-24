package bee.creative.fem;

import java.util.AbstractList;
import java.util.Iterator;
import bee.creative.mmf.MMFArray;

/**
 * Diese Klasse implementiert eine Bytefolge.
 * 
 * @author Sebastian Rostock 2011.
 */
public abstract class FEMBinary implements Iterable<Byte> {

	/**
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Bytes einer Bytefolge in der Methode
	 * {@link FEMBinary#export(Collector, int, int, boolean)}.
	 * 
	 * @author Sebastian Rostock 2015.
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
	static private final class HashCollector implements Collector {

		public int hash = 0x811C9DC5;

		{}

		public HashCollector() {
		}

		@Override
		public boolean push(final byte value) {
			this.hash = (this.hash * 0x01000193) ^ value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static private final class ValueCollector implements Collector {

		public int index;

		public final byte[] value;

		public ValueCollector(final int length) {
			this.value = new byte[length];
		}

		{}

		@Override
		public boolean push(final byte value) {
			this.value[this.index++] = value;
			return true;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die leere Bytefolge.
	 */
	public static final FEMBinary EMPTY = new FEMBinary(0) {

		@Override
		public byte get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public FEMBinary reverse() {
			return this;
		}

		@Override
		public FEMBinary compact() {
			return this;
		}

	};

	{}

	/**
	 * Diese Methode gibt eine Bytefolge mit den gegebenen Bytes zurück.<br>
	 * Das gegebene Array wird hierbei kopiert.
	 * 
	 * @param data Bytes.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMBinary valueOf(final byte[] data) throws NullPointerException {
		if (data.length == 0) return FEMBinary.EMPTY;
		if (data.length == 1) return FEMBinary.valueOf(data[0], 1);
		return compactBinary(data.clone());
	}

	/**
	 * Diese Methode gibt eine uniforme Bytefolge mit der gegebenen Länge zurück, deren Bytes alle gleich dem gegebenen Wert sind.
	 * 
	 * @param value Wert aller Bytes.
	 * @param length Länge.
	 * @return Bytefolge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	public static FEMBinary valueOf(final byte value, final int length) throws IllegalArgumentException {
		if (length == 0) return FEMBinary.EMPTY;
		return new FEMBinary(length) {

			@Override
			protected boolean export(final Collector target, final int offset, int length, final boolean foreward) {
				while (length > 0) {
					if (!target.push(value)) return false;
					length--;
				}
				return true;
			}

			@Override
			public byte get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
				return value;
			}

			@Override
			public FEMBinary reverse() {
				return this;
			}

			@Override
			public FEMBinary compact() {
				return this;
			}

		};
	}

	/**
	 * Diese Methode gibt eine Bytefolge mit den gegebenen Zahlen zurück.
	 * 
	 * @param data Zahlenfolge.
	 * @return Bytefolge.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 */
	public static FEMBinary valueOf(final MMFArray data) throws NullPointerException {
		if (data.length() == 0) return FEMBinary.EMPTY;
		return arrayBinary(data.toINT8());
	}

	public static FEMBinary valueOf(final String data) {

		return null;
	}

	static FEMBinary arrayBinary(final MMFArray array) throws IllegalArgumentException {
		return new FEMBinary(array.length()) {

			@Override
			public byte[] value() {
				return array.toBytes();
			}

			@Override
			public byte get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
				return (byte)array.get(index);
			}

		};
	}

	static FEMBinary compactBinary(final byte[] values) throws IllegalArgumentException {
		return new FEMBinary(values.length) {

			@Override
			public byte[] value() {
				return values.clone();
			}

			@Override
			public byte get(final int index) throws IndexOutOfBoundsException {
				return values[index];
			}

			@Override
			public FEMBinary compact() {
				return this;
			}

		};

	}

	{}

	private int hash;

	/**
	 * Dieses Feld speichert die Länge.
	 */
	protected final int length;

	/**
	 * Dieser Konstruktor initialisiert die Länge.
	 * 
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	FEMBinary(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this.length = length;
	}

	{}

	/**
	 * Diese Methode fügt alle Bytes im gegebenen Abschnitt in der gegebenen Reigenfolge geordnet an den gegebenen {@link Collector} an. Das Anfügen wird
	 * vorzeitig abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reigenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 */
	protected boolean export(final Collector target, int offset, int length, final boolean foreward) {
		if (foreward) {
			for (length += offset; offset < length; offset++) {
				if (!target.push(this.get(offset))) return false;
			}
		} else {
			for (length += offset - 1; offset <= length; length--) {
				if (!target.push(this.get(length))) return false;
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
		final int length = this.length;
		final ValueCollector collector = new ValueCollector(length);
		this.export(collector, 0, length, true);
		return collector.value;
	}

	/**
	 * Diese Methode gibt das {@code index}-te Byte zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Byte.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist.
	 */
	public abstract byte get(final int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge, d.h. die Anzahl der Bytes in der Bytefolge zurück.
	 * 
	 * @return Länge der Bytefolge.
	 */
	public final int length() {
		return this.length;
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Bytefolge mit der gegebenen Bytefolge zurück.
	 * 
	 * @param value Bytefolge.
	 * @return {@link FEMBinary}-Sicht auf die Verkettung dieser mit der gegebenen Bytefolge.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public FEMBinary concat(final FEMBinary value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		if (value.length == 0) return this;
		if (this.length == 0) return value;
		return new ConcatBinary(this, value);
	}

	@SuppressWarnings ("javadoc")
	private static final class ConcatBinary extends FEMBinary {

		private final FEMBinary binary1;

		private final FEMBinary binary2;

		public ConcatBinary(final FEMBinary binary1, final FEMBinary binary2) throws IllegalArgumentException {
			super(binary1.length + binary2.length);
			this.binary1 = binary1;
			this.binary2 = binary2;
		}

		{}

		@Override
		protected boolean export(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.binary1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.binary2.export(target, offset2, length, foreward);
			if (length2 <= 0) return this.binary1.export(target, offset, length, foreward);
			if (foreward) {
				if (!this.binary1.export(target, offset, -offset2, foreward)) return false;
				return this.binary2.export(target, 0, length2, foreward);
			} else {
				if (!this.binary2.export(target, 0, length2, foreward)) return false;
				return this.binary1.export(target, offset, -offset2, foreward);
			}
		}

		@Override
		public byte get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.binary1.length;
			return index2 < 0 ? this.binary1.get(index) : this.binary2.get(index2);
		}

		@Override
		public FEMBinary section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.binary1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.binary2.section(offset2, length);
			if (length2 <= 0) return super.section(offset, length);
			return super.section(offset, -offset2).concat(this.binary2.section(0, length2));
		}
	}

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Bytefolge zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEMBinary}-Sicht auf einen Abschnitt dieser Bytefolge.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Bytefolge liegt oder eine negative Länge hätte.
	 */
	public FEMBinary section(final int offset, int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMBinary.EMPTY;
		return new SectionBinary(this, offset, length);
	}

	@SuppressWarnings ("javadoc")
	private static final class SectionBinary extends FEMBinary {

		private final int offset;

		private final FEMBinary binary;

		public SectionBinary(final FEMBinary binary, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.binary = binary;
			this.offset = offset;
		}

		{}

		@Override
		protected boolean export(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.binary.export(target, this.offset + offset2, length2, foreward);
		}

		@Override
		public byte get(final int index) throws IndexOutOfBoundsException {
			if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
			return this.binary.get(index + this.offset);
		}

		@Override
		public FEMBinary section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.binary.section(this.offset + offset2, length2);
		}

	}

	/**
	 * Diese Methode gibt eine rückwärts geordnete Sicht auf diese Bytefolge zurück.
	 * 
	 * @return rückwärts geordnete {@link FEMBinary}-Sicht auf diese Bytefolge.
	 */
	public FEMBinary reverse() {
		return new FEMBinary(this.length) {

			@Override
			protected boolean export(final Collector target, final int offset, final int length, final boolean foreward) {
				return super.export(target, offset, length, !foreward);
			}

			@Override
			public byte get(final int index) throws IndexOutOfBoundsException {
				return FEMBinary.this.get(this.length - index - 1);
			}

			@Override
			public FEMBinary concat(final FEMBinary value) throws NullPointerException {
				return value.reverse().concat(FEMBinary.this).reverse();
			}

			@Override
			public FEMBinary section(final int offset, final int length2) throws IllegalArgumentException {
				return FEMBinary.this.section(this.length - offset - 1, length2).reverse();
			}

			@Override
			public FEMBinary reverse() {
				return FEMBinary.this;
			}

		};
	}

	public FEMBinary compact() {
		if (this.length == 1) return FEMBinary.valueOf(this.get(0), 1);
		return compactBinary(this.value());
	}

	public int find(final FEMBinary binary, final int offset) throws NullPointerException {

		// TODO
	}

	/**
	 * Diese Methode fügt alle Bytes dieser Bytefolge vom ersten zum letzten geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig
	 * abgebrochen, wenn {@link Collector#push(byte)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Bytes geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public final boolean export(final Collector collector) throws NullPointerException {
		if (collector == null) throw new NullPointerException("collector = null");
		return this.export(collector, 0, this.length, true);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Bytefolge gleich der gegebenen ist.
	 * 
	 * @param value Bytefolge.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final boolean equals(final FEMBinary value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		final int length = this.length;
		if (length != value.length) return false;
		int i = this.hash;
		if (i != 0) {
			final int i2 = value.hash;
			if ((i2 != 0) && (i != i2)) return false;
		}
		for (i = 0; i < length; i++)
			if (this.get(i) != value.get(i)) return false;
		return true;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn die lexikographische Ordnung dieser Bytefolge kleiner, gleich
	 * oder größer als die der gegebenen Bytefolge ist.
	 * 
	 * @param value Bytefolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEMBinary value) throws NullPointerException {
		if (value == null) throw new NullPointerException("value = null");
		final int length = Math.min(this.length, value.length);
		for (int i = 0; i < length; i++) {
			final int result = (value.get(i) & 255) - (this.get(i) & 255);
			if (result != 0) return result;
		}
		return value.length - this.length;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		int result = this.hash;
		if (result != 0) return result;
		final int length = this.length;
		final HashCollector collector = new HashCollector();
		this.export(collector, 0, length, true);
		result = collector.hash;
		if (result == 0) {
			result = 1;
		}
		this.hash = result;
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
				return new Byte(FEMBinary.this.get(this.index++));
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatBinary(this);
	}

}