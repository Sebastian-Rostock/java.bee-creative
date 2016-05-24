package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import bee.creative.fem.FEM.BaseValue;
import bee.creative.fem.FEM.ScriptFormatter;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;

/** Diese Klasse implementiert eine unveränderliche Liste von Werten sowie Methoden zur Erzeugung solcher Wertlisten aus nativen Arrays und {@link Iterable}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMArray extends BaseValue implements Items<FEMValue>, Iterable<FEMValue> {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Werten einer Wertliste in der Methode {@link FEMArray#export(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Wert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammeln fortgeführt werden soll.
		 * 
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammeln fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(FEMValue value);

	}

	@SuppressWarnings ("javadoc")
	static final class FindCollector implements Collector {

		public final FEMValue that;

		public int index;

		FindCollector(final FEMValue that) {
			this.that = that;
		}

		{}

		@Override
		public final boolean push(final FEMValue value) {
			if (value.equals(this.that)) return false;
			this.index++;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class HashCollector implements Collector {

		public int hash = 0x811C9DC5;

		{}

		@Override
		public final boolean push(final FEMValue value) {
			this.hash = (this.hash * 0x01000193) ^ value.hashCode();
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ValueCollector implements Collector {

		public final FEMValue[] array;

		public int index;

		ValueCollector(final int length) {
			this.array = new FEMValue[length];
		}

		{}

		@Override
		public final boolean push(final FEMValue value) {
			this.array[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyArray extends FEMArray {

		EmptyArray() throws IllegalArgumentException {
			super(0);
		}

		{}

		@Override
		public final FEMArray reverse() {
			return this;
		}

		@Override
		public final FEMArray compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ConcatArray extends FEMArray {

		final FEMArray _array1_;

		final FEMArray _array2_;

		ConcatArray(final FEMArray array1, final FEMArray array2) throws IllegalArgumentException {
			super(array1._length_ + array2._length_);
			this._array1_ = array1;
			this._array2_ = array2;
		}

		{}

		@Override
		protected final FEMValue _get_(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this._array1_._length_;
			return index2 < 0 ? this._array1_._get_(index) : this._array2_._get_(index2);
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this._array1_._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this._array2_._export_(target, offset2, length, foreward);
			if (length2 <= 0) return this._array1_._export_(target, offset, length, foreward);
			if (foreward) {
				if (!this._array1_._export_(target, offset, -offset2, foreward)) return false;
				return this._array2_._export_(target, 0, length2, foreward);
			} else {
				if (!this._array2_._export_(target, 0, length2, foreward)) return false;
				return this._array1_._export_(target, offset, -offset2, foreward);
			}
		}

		@Override
		public final FEMArray section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this._array1_._length_, length2 = offset2 + length;
			if (offset2 >= 0) return this._array2_.section(offset2, length);
			if (length2 <= 0) return this._array1_.section(offset, length);
			return super.section(offset, -offset2).concat(this._array2_.section(0, length2));
		}
	}

	@SuppressWarnings ("javadoc")
	static final class SectionArray extends FEMArray {

		final FEMArray _array_;

		final int _offset_;

		SectionArray(final FEMArray array, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this._array_ = array;
			this._offset_ = offset;
		}

		{}

		@Override
		protected final FEMValue _get_(final int index) throws IndexOutOfBoundsException {
			return this._array_._get_(index + this._offset_);
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this._array_._export_(target, this._offset_ + offset2, length2, foreward);
		}

		@Override
		public final FEMArray section(final int offset2, final int length2) throws IllegalArgumentException {
			return this._array_.section(this._offset_ + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseArray extends FEMArray {

		final FEMArray _array_;

		ReverseArray(final FEMArray array) throws IllegalArgumentException {
			super(array._length_);
			this._array_ = array;
		}

		{}

		@Override
		protected final FEMValue _get_(final int index) throws IndexOutOfBoundsException {
			return this._array_._get_(this._length_ - index - 1);
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset, final int length, final boolean foreward) {
			return this._array_._export_(target, offset, length, !foreward);
		}

		@Override
		public final FEMArray concat(final FEMArray value) throws NullPointerException {
			return value.reverse().concat(this._array_).reverse();
		}

		@Override
		public final FEMArray section(final int offset, final int length2) throws IllegalArgumentException {
			return this._array_.section(this._length_ - offset - length2, length2).reverse();
		}

		@Override
		public final FEMArray reverse() {
			return this._array_;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformArray extends FEMArray {

		final FEMValue _value_;

		UniformArray(final int length, final FEMValue value) throws IllegalArgumentException {
			super(length);
			this._value_ = value;
		}

		{}

		@Override
		protected final FEMValue _get_(final int index) throws IndexOutOfBoundsException {
			return this._value_;
		}

		@Override
		protected final boolean _export_(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this._value_)) return false;
				length--;
			}
			return true;
		}

		@Override
		public final FEMArray reverse() {
			return this;
		}

		@Override
		public final FEMArray compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static class CompactArray extends FEMArray {

		final FEMValue[] _values_;

		CompactArray(final FEMValue[] values) throws IllegalArgumentException {
			super(values.length);
			this._values_ = values;
		}

		{}

		@Override
		protected final FEMValue _get_(final int index) throws IndexOutOfBoundsException {
			return this._values_[index];
		}

		@Override
		public final FEMValue[] value() {
			return this._values_.clone();
		}

		@Override
		public final FEMArray compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ResultArray extends CompactArray {

		ResultArray(final FEMValue[] values) throws IllegalArgumentException {
			super(values);
		}

		@Override
		public final FEMArray result(final boolean recursive) {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 1;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMArray> TYPE = FEMType.from(FEMArray.ID, "ARRAY");

	/** Dieses Feld speichert eine leere Wertliste. */
	public static final FEMArray EMPTY = new EmptyArray();

	{}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.<br>
	 * Das gegebene Array wird Kopiert, sodass spätere Anderungen am gegebenen Array nicht auf die erzeugte Wertliste übertragen werden.
	 * 
	 * @param values Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist. */
	public static FEMArray from(final FEMValue... values) throws NullPointerException {
		if (values.length == 0) return FEMArray.EMPTY;
		if (values.length == 1) return FEMArray.from(values[0], 1);
		return new CompactArray(values.clone());
	}

	/** Diese Methode gibt eine uniforme Wertliste mit der gegebenen Länge zurück, deren Werte alle gleich dem gegebenen sind.
	 * 
	 * @param value Wert.
	 * @param length Länge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMArray from(final FEMValue value, final int length) throws NullPointerException, IllegalArgumentException {
		if (length == 0) return FEMArray.EMPTY;
		if (value == null) throw new NullPointerException("value = null");
		return new UniformArray(length, value);
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see #from(Collection)
	 * @param values Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist. */
	public static FEMArray from(final Iterable<? extends FEMValue> values) throws NullPointerException {
		final ArrayList<FEMValue> result = new ArrayList<>();
		Iterables.appendAll(result, values);
		return FEMArray.from(result);
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Collection#toArray(Object[])
	 * @see #from(FEMValue...)
	 * @param values Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist. */
	public static FEMArray from(final Collection<? extends FEMValue> values) throws NullPointerException {
		if (values.size() == 0) return FEMArray.EMPTY;
		return FEMArray.from(values.toArray(new FEMValue[values.size()]));
	}

	/** Diese Methode ist eine Abkürzung für {@code FEMContext.DEFAULT().dataFrom(value, FEMArray.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist. */
	public static FEMArray from(final FEMValue value) throws NullPointerException {
		return FEMContext._default_.dataFrom(value, FEMArray.TYPE);
	}

	/** Diese Methode ist eine Abkürzung für {@code context.dataFrom(value, FEMArray.TYPE)}.
	 * 
	 * @param value {@link FEMValue}.
	 * @param context {@link FEMContext}.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code value} bzw. {@code context} {@code null} ist. */
	public static FEMArray from(final FEMValue value, final FEMContext context) throws NullPointerException {
		return context.dataFrom(value, FEMArray.TYPE);
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
	protected FEMArray(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this._length_ = length;
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-ter Wert. */
	protected FEMValue _get_(final int index) {
		return null;
	}

	/** Diese Methode fügt alle Werte im gegebenen Abschnitt in der gegebenen Reigenfolge geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
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

	/** Diese Methode konvertiert diese Wertliste in ein {@code FEMValue[]} und gibt dieses zurück.
	 * 
	 * @return Array mit den Werten dieser Wertliste. */
	public FEMValue[] value() {
		final ValueCollector target = new ValueCollector(this._length_);
		this.export(target);
		return target.array;
	}

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Werte in der Wertliste zurück.
	 * 
	 * @return Länge der Wertliste. */
	public final int length() {
		return this._length_;
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste zurück.
	 * 
	 * @param that Wertliste.
	 * @return {@link FEMArray}-Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public FEMArray concat(final FEMArray that) throws NullPointerException {
		if (that._length_ == 0) return this;
		if (this._length_ == 0) return that;
		return new ConcatArray(this, that);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link FEMArray}-Sicht auf einen Abschnitt dieser Wertliste.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Wertliste liegt oder eine negative Länge hätte. */
	public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this._length_)) return this;
		if ((offset < 0) || ((offset + length) > this._length_)) throw new IllegalArgumentException();
		if (length == 0) return FEMArray.EMPTY;
		return new SectionArray(this, offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Wertliste zurück.
	 * 
	 * @return rückwärts geordnete {@link FEMArray}-Sicht auf diese Wertliste. */
	public FEMArray reverse() {
		return new ReverseArray(this);
	}

	/** Diese Methode gibt die {@link #value() Werte dieser Wertliste} in einer performanteren oder zumindest gleichwertigen Wertliste zurück.
	 * 
	 * @see #from(FEMValue...)
	 * @see #value()
	 * @return performanteren Wertliste oder {@code this}. */
	public FEMArray compact() {
		final FEMArray result = this._length_ == 1 ? new UniformArray(1, this._get_(0)) : new CompactArray(this.value());
		result._hash_ = this._hash_;
		return result;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Werts innerhalb dieser Wertliste zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 * 
	 * @param that gesuchter Wert.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Werts ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMValue that, final int offset) throws IllegalArgumentException {
		final int length = this._length_ - offset;
		if ((offset < 0) || (length < 0)) throw new IllegalArgumentException();
		final FindCollector collector = new FindCollector(that);
		if (this._export_(collector, offset, length, true)) return -1;
		return collector.index + offset;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Wertliste innerhalb dieser Wertliste zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 * 
	 * @param that gesuchte Wertliste.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Wertliste ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMArray that, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (offset > this._length_)) throw new IllegalArgumentException();
		final int count = that._length_;
		if (count == 0) return offset;
		final FEMValue value = that._get_(0);
		final int length = this._length_ - count;
		FIND: for (int i = offset; i < length; i++) {
			if (value.equals(this._get_(i))) {
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

	/** Diese Methode fügt alle Werte dieser Wertliste vom ersten zum letzten geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
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

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste gleich der gegebenen ist.
	 * 
	 * @param that Wertliste.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMArray that) throws NullPointerException {
		final int length = this._length_;
		if (length != that._length_) return false;
		if (this.hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this._get_(i).equals(that._get_(i))) return false;
		}
		return true;
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Wertliste kleiner, gleich oder größer als die der
	 * gegebenen Wertliste ist. Die Werte werden über den gegebenen {@link Comparator} verglichen.
	 * 
	 * @param that Wertliste.
	 * @param order {@link Comparator} zum Vergleichen der Werte.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} bzw. {@code order} {@code null} ist. */
	public final int compare(final FEMArray that, final Comparator<FEMValue> order) throws NullPointerException {
		final int length = Math.min(this._length_, that._length_);
		for (int i = 0; i < length; i++) {
			final int result = order.compare(this._get_(i), that._get_(i));
			if (result != 0) return result;
		}
		return this._length_ - that._length_;
	}

	/** Diese Methode gibt diese Wertliste als {@link List} zurück.
	 * 
	 * @see Arrays#asList(Object...)
	 * @return {@link List}. */
	public final List<FEMValue> toList() {
		return Arrays.asList(this.value());
	}

	{}

	/** Diese Methode gibt den {@code index}-ten Wert zurück. */
	@Override
	public final FEMValue get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this._length_)) throw new IndexOutOfBoundsException();
		return this._get_(index);
	}

	/** {@inheritDoc} */
	@Override
	public final FEMArray data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMArray> type() {
		return FEMArray.TYPE;
	}

	/** {@inheritDoc} */
	@Override
	public FEMArray result(final boolean recursive) {
		if (!recursive) return this;
		final FEMValue[] result = this.value();
		final int length = result.length;
		for (int i = 0; i < length; i++) {
			result[i] = result[i].result(true);
		}
		return new ResultArray(result);
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		return this.hash();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMArray)) return false;
		return this.equals((FEMArray)object);
	}

	/** {@inheritDoc} */
	@Override
	public final Iterator<FEMValue> iterator() {
		return Iterators.itemsIterator(this, 0, this._length_);
	}

	/** {@inheritDoc} */
	@Override
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putArray(this);
	}

}
