package bee.creative.iam;

import java.util.Iterator;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;
import bee.creative.util.Objects;

/** Diese Klasse implementiert eine abstrakte Zahlenfolge, welche in einer Liste ({@link IAMListing}) für die Elemente sowie einer Abbildung
 * ({@link IAMMapping}) für die Schlüssel und Werte der Einträge ({@code IAMEntry}) verwendet wird.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class IAMArray implements Iterable<Integer>, Comparable<IAMArray> {

	@SuppressWarnings ("javadoc")
	static final class IntArray extends IAMArray {

		final int[] _array_;

		IntArray(final int[] array) {
			super(array.length);
			this._array_ = array;
		}

		{}

		@Override
		protected final int _get_(final int index) {
			return this._array_[index];
		}

		@Override
		public final int mode() {
			return 4;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ByteArray extends IAMArray {

		final byte[] _array_;

		ByteArray(final byte[] array) {
			super(array.length);
			this._array_ = array;
		}

		{}

		@Override
		protected final int _get_(final int index) {
			return this._array_[index];
		}

		@Override
		public final int mode() {
			return 1;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CharArray extends IAMArray {

		final char[] _array_;

		CharArray(final char[] array) {
			super(array.length);
			this._array_ = array;
		}

		{}

		@Override
		protected final int _get_(final int index) {
			return this._array_[index];
		}

		@Override
		public final int mode() {
			return 2;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ShortArray extends IAMArray {

		final short[] _array_;

		ShortArray(final short[] array) {
			super(array.length);
			this._array_ = array;
		}

		{}

		@Override
		protected final int _get_(final int index) {
			return this._array_[index];
		}

		@Override
		public final int mode() {
			return 2;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyArray extends IAMArray {

		EmptyArray() {
			super(0);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class SectionArray extends IAMArray {

		final IAMArray _array_;

		final int _offset_;

		SectionArray(final IAMArray array, final int offset, final int length) {
			super(length);
			this._array_ = array;
			this._offset_ = offset;
		}

		{}

		@Override
		protected final int _get_(final int index) {
			return this._array_._get_(this._offset_ + index);
		}

		@Override
		protected final IAMArray _section_(final int offset, final int length) {
			return this._array_._section_(this._offset_ + offset, length);
		}

		@Override
		public final int mode() {
			return this._array_.mode();
		}

	}

	{}

	/** Dieses Feld speichert das leere {@link IAMArray}. */
	public static final IAMArray EMPTY = new EmptyArray();

	{}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final int... array) throws NullPointerException {
		if (array.length == 0) return IAMArray.EMPTY;
		return new IntArray(array);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final byte[] array) throws NullPointerException {
		if (array.length == 0) return IAMArray.EMPTY;
		return new ByteArray(array);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final char[] array) throws NullPointerException {
		if (array.length == 0) return IAMArray.EMPTY;
		return new CharArray(array);
	}

	/** Diese Methode gibt ein neues {@link IAMArray} als Sicht auf die gegebenen Zahlen zurück. Änderungen am Inhalt von {@code array} werden auf das gelieferte
	 * {@link IAMArray} übertragen!
	 *
	 * @param array Zahlen.
	 * @return {@link IAMArray}-Sicht auf {@code array}.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static IAMArray from(final short[] array) throws NullPointerException {
		if (array.length == 0) return IAMArray.EMPTY;
		return new ShortArray(array);
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code byte[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @param array Zahlenfolge.
	 * @return Kopie der Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static byte[] toBytes(final IAMArray array) throws NullPointerException {
		final int length = array._length_;
		final byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = (byte)array._get_(i);
		}
		return result;
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code char[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @param array Zahlenfolge.
	 * @return Kopie der Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static char[] toChars(final IAMArray array) throws NullPointerException {
		final int length = array._length_;
		final char[] result = new char[length];
		for (int i = 0; i < length; i++) {
			result[i] = (char)array._get_(i);
		}
		return result;
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code short[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @param array Zahlenfolge.
	 * @return Kopie der Zahlenfolge.
	 * @throws NullPointerException Wenn {@code array} {@code null} ist. */
	public static short[] toShorts(final IAMArray array) throws NullPointerException {
		final int length = array._length_;
		final short[] result = new short[length];
		for (int i = 0; i < length; i++) {
			result[i] = (short)array._get_(i);
		}
		return result;
	}

	{}

	/** Dieses Feld speichert die Länge. */
	protected final int _length_;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge. */
	protected IAMArray(final int length) {
		this._length_ = length;
	}

	{}

	/** Diese Methode implementiert {@link #get(int)} ohne Parameterprüfung.
	 *
	 * @param index Index.
	 * @return {@code index}-te Zahl. */
	protected int _get_(final int index) {
		return 0;
	}

	/** Diese Methode implementiert {@link #section(int, int)} ohne Parameterprüfung.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Abschnitt. */
	protected IAMArray _section_(final int offset, final int length) {
		if (length == 0) return IAMArray.EMPTY;
		return new SectionArray(this, offset, length);
	}

	/** Diese Methode gibt die {@code index}-te Zahl zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 *
	 * @see #length()
	 * @param index Index.
	 * @return {@code index}-te Zahl. */
	public final int get(final int index) {
		if ((index < 0) || (index >= this._length_)) return 0;
		return this._get_(index);
	}

	/** Diese Methode gibt die Länge der Zahlenfolge zurück ({@code 0..1073741823}).
	 *
	 * @see #get(int)
	 * @return Länge. */
	public final int length() {
		return this._length_;
	}

	/** Diese Methode gibt die Größe jeder Zahl dieser Zahlenfolge zurück.<br>
	 * Diese Größe ist {@code 0} für eine unspezifische Zahlenfolgen, {@code 1} für {@code INT8}- sowie {@code UINT8}-Zahlen, {@code 2} für {@code INT16}- sowie
	 * {@code UINT16}-Zahlen und {@code 4} für {@code INT32}-Zahlen.
	 *
	 * @return Größe jeder Zahl dieser Zahlenfolge (0..4). */
	public int mode() {
		return 0;
	}

	/** Diese Methode gibt den Streuwert zurück. <pre>
	 * int result = 0x811C9DC5;
	 * for (int i = 0; i < length(); i++)
	 *   result = (result * 0x01000193) ^ get(i);
	 * return result;
	 * </pre>
	 *
	 * @return Streuwert. */
	public final int hash() {
		int hash = 0x811C9DC5;
		for (int i = 0, size = this._length_; i < size; i++) {
			hash = (hash * 0x01000193) ^ this._get_(i);
		}
		return hash;
	}

	/** Diese Methode gibt nur dann true zurück, wenn diese Zahlenfolge gleich der gegebenen Zahlenfolge ist. <pre>
	 * if (length() != that.length()) return false;
	 * for (int i = 0; i < length(); i++)
	 *   if (get(i) != that.get(i)) return false;
	 * return true;
	 * </pre>
	 *
	 * @param that Zahlenfolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final IAMArray that) throws NullPointerException {
		final int length = this._length_;
		if (length != that._length_) return false;
		for (int i = 0; i < length; i++)
			if (this._get_(i) != that._get_(i)) return false;
		return true;
	}

	/** Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung dieser Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der gegebenen Zahlenfolge ist. <pre>
	 * for (int i = 0, result; i < min(length(), that.length()); i++)
	 *   if ((result = get(i) – that.get(i)) != 0) return result;
	 * return length() – that.length();
	 * </pre>
	 *
	 * @param that Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final int compare(final IAMArray that) throws NullPointerException {
		final int length1 = this._length_, length2 = that._length_;
		for (int i = 0, length = length1 < length2 ? length1 : length2, result; i < length; i++)
			if ((result = Comparators.compare(this._get_(i), that._get_(i))) != 0) return result;
		return length1 - length2;
	}

	/** Diese Methode gibt einen Abschnitt dieser Zahlenfolge ab der gegebenen Position und mit der gegebenen Länge zurück.<br>
	 * Wenn der Abschnitt nicht innerhalb der Zahlenfolge liegt oder die Länge kleiner als {@code 1} ist, wird eine leere Zahlenfolge geliefert.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Abschnitt. */
	public IAMArray section(final int offset, final int length) {
		if ((offset < 0) || (length <= 0) || ((offset + length) > this._length_)) return this._section_(0, 0);
		return this._section_(offset, length);
	}

	/** Diese Methode gibt eine Kopie der Zahlenfolge als {@code int[]} zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge. */
	public final int[] toArray() {
		final int length = this._length_;
		final int[] result = new int[length];
		for (int i = 0; i < length; i++) {
			result[i] = this._get_(i);
		}
		return result;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final Iterator<Integer> iterator() {
		return new Iterator<Integer>() {

			int _index_ = 0;

			@Override
			public boolean hasNext() {
				return this._index_ < IAMArray.this._length_;
			}

			@Override
			public Integer next() {
				return new Integer(IAMArray.this._get_(this._index_++));
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/** {@inheritDoc} */
	@Override
	public final int compareTo(final IAMArray that) {
		return this.compare(that);
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof IAMArray)) return false;
		return this.equals((IAMArray)object);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.length() > 50 ? //
			Objects.formatIterable(false, Iterables.chainedIterable(this.section(0, 45), Iterables.itemIterable(Objects.toStringObject("...")))) : //
			Objects.formatIterable(false, this);
	}
}
