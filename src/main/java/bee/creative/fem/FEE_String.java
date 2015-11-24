package de.fhg.ivi.fee.core.data;

import java.nio.charset.Charset;

import bee.creative.array.CompactByteArray;
import bee.creative.iam.IAMArray;
import bee.creative.mmf.MMFArray;
import de.fhg.ivi.fee.core.FEE;

/**
 * Diese Klasse implementiert eine Zeichenkette.
 * 
 * @author Sebastian Rostock 2015.
 */
public abstract class FEE_String {

	/**
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Codepoints einer Zeichenkette in der Methode
	 * {@link FEE_String#collect(Collector, int, int)}.
	 * 
	 * @author Sebastian Rostock 2015.
	 */
	public static interface Collector {

		/**
		 * Diese Methode fügt den gegebenen Codepoint an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen
		 * fortgeführt werden soll.
		 * 
		 * @param value Codepoint.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll.
		 */
		public boolean push(int value);

	}

	{
	}

	/**
	 * Dieses Feld speichert die leere Zeichenkette.
	 */
	public static final FEE_String EMPTY = //
	new FEE_String() {

		@Override
		public int get(final int index) throws IndexOutOfBoundsException {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public int length() {
			return 0;
		}

	};

	/**
	 * Dieses Feld speichert das UTF8 {@link Charset}.
	 */
	static final Charset CHARSET = //
	Charset.forName("UTF8");

	{
	}

	/**
	 * Diese Methode gibt eine Zeichenkette mit der gegebenen Länge zurück, deren Zeichen alle gleich dem gegebenen Codepoint sind.
	 * 
	 * @param value Codepoint.
	 * @param length Länge.
	 * @return Zeichenkette.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	public static FEE_String valueOf(final int value, final int length) throws IllegalArgumentException {
		if (length == 0) return FEE_String.EMPTY;
		if (length < 0) throw new IllegalArgumentException("length < 0");
		return new FEE_String() {

			@Override
			public int get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				return value;
			}

			@Override
			public int length() {
				return length;
			}

		};
	}

	/**
	 * Diese Methode gibt eine Zeichenkette mit den Codepoints in der gegebenen, UTF8-kodierten Zahlenfolge zurück.
	 * 
	 * @param data Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	public static FEE_String valueOf(byte[] data) throws NullPointerException, IllegalArgumentException {
		if (data.length == 0) return FEE_String.EMPTY;
		final byte[] values = data = data.clone();
		final int length = FEE_String.codepointCount(values);
		return new FEE_String() {

			@Override
			protected boolean collect(final Collector target, int offset, int length) {
				final byte[] array = values;
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEE_String.codepointSize(array[index]);
				}
				while (length > 0) {
					if (!target.push(FEE_String.codepointValue(array, index))) return false;
					length--;
					index += FEE_String.codepointSize(array[index]);
				}
				return true;
			}

			@Override
			public int get(int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				int offset = 0;
				final byte[] array = values;
				while (index > 0) {
					index--;
					offset += FEE_String.codepointSize(array[offset]);
				}
				return FEE_String.codepointValue(array, offset);
			}

			@Override
			public int length() {
				return length;
			}

		};
	}

	/**
	 * Diese Methode gibt eine Zeichenkette mit den Codepoints in der gegebenen, UTF8-kodierten Zahlenfolge zurück.
	 * 
	 * @param data Zahlenfolge.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	public static FEE_String valueOf(MMFArray data) throws NullPointerException, IllegalArgumentException {
		if (data.length() == 0) return FEE_String.EMPTY;
		final MMFArray values = data = data.toINT8();
		final int length = FEE_String.codepointCount(values);
		return new FEE_String() {

			@Override
			protected boolean collect(final Collector target, int offset, int length) {
				final MMFArray array = values;
				int index = 0;
				while (offset > 0) {
					offset--;
					index += FEE_String.codepointSize(array.get(index));
				}
				while (length > 0) {
					if (!target.push(FEE_String.codepointValue(array, index))) return false;
					length--;
					index += FEE_String.codepointSize(array.get(index));
				}
				return true;
			}

			@Override
			public int get(int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				int offset = 0;
				final MMFArray array = values;
				while (index > 0) {
					index--;
					offset += FEE_String.codepointSize(array.get(offset));
				}
				return FEE_String.codepointValue(array, offset);
			}

			@Override
			public int length() {
				return length;
			}

		};
	}

	/**
	 * Diese Methode gibt eine Zeichenkette mit den Codepoints des gegebenen {@link String} zurück.
	 * 
	 * @param data {@link String}.
	 * @return Zeichenkette.
	 * @throws NullPointerException Wenn {@code data} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	public static FEE_String valueOf(final String data) throws NullPointerException {
		if (data.length() == 0) return FEE_String.EMPTY;
		return FEE_String.valueOf(data.getBytes(FEE_String.CHARSET));
	}

	/**
	 * Diese Methode gibt die gegebene Zeichenkette als {@link String} zurück.
	 * 
	 * @param value Zeichenkette oder {@code null}.
	 * @return {@link String} oder {@code null}.
	 */
	public static String toString(final FEE_String value) {
		if (value == null) return null;
		return new String(value.value(), FEE_String.CHARSET);
	}

	{
	}

	/**
	 * Diese Methode gibt die Größe des UTF8-kodierten Codepoints zurück, dessen erstes Byte gegeben ist.
	 * 
	 * @param header erstes Byte der UTF8-Kodierung eines Codepoint.
	 * @return Byteanzahl (1..4).
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	static int codepointSize(final int header) throws IllegalArgumentException {
		switch ((header >> 4) & 15) {
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

	/**
	 * Diese Methode gibt die Anzahl der Codepoints in der gegebenen, UTF8-kodierten Zahlenfolge zurück.
	 * 
	 * @param array Zahlenfolge.
	 * @return Zahlenfolge.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	static int codepointCount(final byte[] array) throws IllegalArgumentException {
		int index = 0;
		int result = 0;
		final int length = array.length;
		while (index < length) {
			result++;
			index += FEE_String.codepointSize(array[index]);
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/**
	 * Diese Methode gibt die Anzahl der Codepoints in der gegebenen, UTF8-kodierten Zahlenfolge zurück.
	 * 
	 * @param array Zahlenfolge.
	 * @return Zahlenfolge.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	static int codepointCount(final MMFArray array) throws IllegalArgumentException {
		int index = 0;
		int result = 0;
		final int length = array.length();
		while (index < length) {
			result++;
			index += FEE_String.codepointSize(array.get(index));
		}
		if (index != length) throw new IllegalArgumentException();
		return result;
	}

	/**
	 * Diese Methode gibt den Codepoint ab der gegebenen Position zurück.
	 * 
	 * @param array Zahlenfolge.
	 * @param offset Position.
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	static int codepointValue(final byte[] array, final int offset) throws IllegalArgumentException {
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
				return ((array[offset] & 7) << 18) | ((array[offset + 1] & 63) << 12) | ((array[offset + 2] & 63) << 6)
					| (array[offset + 3] & 63);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Diese Methode gibt den Codepoint ab der gegebenen Position zurück.
	 * 
	 * @param array Zahlenfolge.
	 * @param offset Position.
	 * @return Codepoint.
	 * @throws IllegalArgumentException Wenn die Kodierung fehlerhaft ist.
	 */
	static int codepointValue(final IAMArray array, final int offset) throws IllegalArgumentException {
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
				return ((array.get(offset) & 7) << 18) | ((array.get(offset + 1) & 63) << 12) | ((array.get(offset + 2) & 63) << 6)
					| (array.get(offset + 3) & 63);
		}
		throw new IllegalArgumentException();
	}

	{
	}

	/**
	 * Diese Methode fügt alle Codepoints im gegebenen Abschnitt geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig
	 * abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 */
	protected boolean collect(final Collector target, int offset, int length) {
		for (length += offset; offset < length; offset++) {
			if (!target.push(this.get(offset))) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt die Zeichenkette als UTF8-kodierte Zahlenfolge zurück.
	 * 
	 * @return UTF8-kodierte Zahlenfolge.
	 */
	public byte[] value() {
		final int length = this.length();
		final CompactByteArray values = new CompactByteArray();
		final Collector collector = new Collector() {

			@Override
			public boolean push(final int value) {
				if (value < 0) throw new IllegalArgumentException();
				final CompactByteArray array = values;
				if (value < 128) {
					array.add((byte) value);
				} else if (value < 2048) {
					array.add((byte) (192 | (value >> 6)));
					array.add((byte) (128 | (value & 63)));
				} else if (value < 65536) {
					array.add((byte) (224 | (value >> 12)));
					array.add((byte) (128 | ((value >> 6) & 63)));
					array.add((byte) (128 | (value & 63)));
				} else {
					array.add((byte) (240 | (value >> 18)));
					array.add((byte) (128 | ((value >> 12) & 63)));
					array.add((byte) (128 | ((value >> 6) & 63)));
					array.add((byte) (128 | (value & 63)));
				}
				return true;
			}

		};
		values.setAlignment(0);
		values.allocate(length);
		this.collect(collector, 0, length);
		return values.toArray();
	}

	/**
	 * Diese Methode gibt das {@code index}-te Zeichen als Codepoint zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Zeichen.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist.
	 */
	public abstract int get(final int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge, d.h. die Anzahl der Zeichen in der Zeichenkette zurück.
	 * 
	 * @return Länge der Zeichenkette.
	 */
	public abstract int length();

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Zeichenkette gleich der gegebenen ist.
	 * 
	 * @param value Zeichenkette.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final boolean equals(final FEE_String value) throws NullPointerException {
		final int length = this.length();
		if (length != value.length()) return false;
		for (int i = 0; i < length; i++)
			if (this.get(i) != value.get(i)) return false;
		return true;
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner als, gleich zu bzw. größer als {@code 0} zurück, wenn die lexikographische Ordnung dieser
	 * Zeichenkette kleiner, gleich oder größer als die der gegebenen Zeichenkette ist.
	 * 
	 * @param value Zeichenkette.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final int compare(final FEE_String value) throws NullPointerException {
		final int length = Math.min(this.length(), value.length());
		for (int i = 0; i < length; i++) {
			final int result = value.get(i) - this.get(i);
			if (result != 0) return result;
		}
		return value.length() - this.length();
	}

	/**
	 * Diese Methode fügt alle Codepoints dieser Zeichenkette geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig
	 * abgebrochen, wenn {@link Collector#push(int)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Codepoints geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public final boolean collect(final Collector target) throws NullPointerException {
		if (target == null) throw new NullPointerException();
		return this.collect(target, 0, this.length());
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Zeichenkette mit der gegebenen Zeichenkette zurück.
	 * 
	 * @param value Zeichenkette.
	 * @return {@link FEE_String}-Sicht auf die Verkettung dieser mit der gegebenen Zeichenkette.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public FEE_String concat(final FEE_String value) throws NullPointerException {
		if (value.length() == 0) return this;
		if (this.length() == 0) return value;
		return new FEE_String() {

			@Override
			protected boolean collect(final Collector target, final int offset, final int length) {
				final int offset2 = offset - FEE_String.this.length(), length2 = offset2 + length;
				if (offset2 >= 0) return value.collect(target, offset2, length);
				else if (length2 <= 0) return FEE_String.this.collect(target, offset, length);
				else {
					if (!FEE_String.this.collect(target, offset, -offset2)) return false;
					return value.collect(target, 0, length2);
				}
			}

			@Override
			public int get(final int index) throws IndexOutOfBoundsException {
				final int index2 = index - FEE_String.this.length();
				return index2 < 0 ? FEE_String.this.get(index) : value.get(index2);
			}

			@Override
			public FEE_String section(final int offset, final int length) throws IllegalArgumentException {
				final int offset2 = offset - FEE_String.this.length(), length2 = offset2 + length;
				if (offset2 >= 0) return value.section(offset2, length);
				if (length2 <= 0) return super.section(offset, length);
				return super.section(offset, -offset2).concat(value.section(0, length2));
			}

			@Override
			public int length() {
				return FEE_String.this.length() + value.length();
			}

		};
	}

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Zeichenkette zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Bytes im Abschnitt.
	 * @return {@link FEE_String}-Sicht auf einen Abschnitt dieser Zeichenkette.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Zeichenkette liegt oder eine negative Länge hätte.
	 */
	public FEE_String section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length())) return this;
		if ((offset < 0) || (length < 0) || ((offset + length) > this.length())) throw new IllegalArgumentException();
		if (length == 0) return FEE_String.EMPTY;
		return new FEE_String() {

			@Override
			protected boolean collect(final Collector target, final int offset2, final int length2) {
				return FEE_String.this.collect(target, offset + offset2, length2);
			}

			@Override
			public int get(final int index) throws IndexOutOfBoundsException {
				if ((index < 0) || (index >= length)) throw new IndexOutOfBoundsException();
				return FEE_String.this.get(index + offset);
			}

			@Override
			public int length() {
				return length;
			}

			@Override
			public FEE_String section(final int offset2, final int length2) throws IllegalArgumentException {
				return FEE_String.this.section(offset + offset2, length2);
			}

		};
	}

	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int length = this.length();
		int hash = 0x811C9DC5;
		for (int i = 0; i < length; i++) {
			hash = (hash * 0x01000193) ^ this.get(i);
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEE_String)) return false;
		return this.equals((FEE_String) object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEE.formatString(FEE_String.toString(this));
	}

}
