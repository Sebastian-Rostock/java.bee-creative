package bee.creative.fem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;

/**
 * Diese Klasse implementiert eine unmodifizierbare Liste von Werten sowie Methoden zur Erzeugung solcher Wertlisten aus nativen Arrays und {@link Iterable}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMArray implements Items<FEMValue>, Iterable<FEMValue>, FEM.ScriptFormatterInput {

	/**
	 * Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Werten einer Wertliste in der Methode {@link FEMArray#export(Collector)}.
	 */
	public static interface Collector {

		/**
		 * Diese Methode fügt den gegebenen Wert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammlen fortgeführt werden soll.
		 * 
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammlen fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll.
		 */
		public boolean push(FEMValue value);

	}

	@SuppressWarnings ("javadoc")
	static final class HashCollector implements Collector {

		public int hash = 0x811C9DC5;

		{}

		@Override
		public boolean push(final FEMValue value) {
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
		public boolean push(final FEMValue value) {
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
		public FEMArray reverse() {
			return this;
		}

		@Override
		public FEMArray compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ConcatArray extends FEMArray {

		final FEMArray __array1;

		final FEMArray __array2;

		ConcatArray(final FEMArray array1, final FEMArray array2) throws IllegalArgumentException {
			super(array1.__length + array2.__length);
			this.__array1 = array1;
			this.__array2 = array2;
		}

		{}

		@Override
		protected FEMValue __get(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.__array1.__length;
			return index2 < 0 ? this.__array1.__get(index) : this.__array2.__get(index2);
		}

		@Override
		protected boolean __export(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.__array1.__length, length2 = offset2 + length;
			if (offset2 >= 0) return this.__array2.__export(target, offset2, length, foreward);
			if (length2 <= 0) return this.__array1.__export(target, offset, length, foreward);
			if (foreward) {
				if (!this.__array1.__export(target, offset, -offset2, foreward)) return false;
				return this.__array2.__export(target, 0, length2, foreward);
			} else {
				if (!this.__array2.__export(target, 0, length2, foreward)) return false;
				return this.__array1.__export(target, offset, -offset2, foreward);
			}
		}

		@Override
		public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.__array1.__length, length2 = offset2 + length;
			if (offset2 >= 0) return this.__array2.section(offset2, length);
			if (length2 <= 0) return this.__array1.section(offset, length);
			return super.section(offset, -offset2).concat(this.__array2.section(0, length2));
		}
	}

	@SuppressWarnings ("javadoc")
	static final class SectionArray extends FEMArray {

		final FEMArray __array;

		final int __offset;

		SectionArray(final FEMArray array, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.__array = array;
			this.__offset = offset;
		}

		{}

		@Override
		protected FEMValue __get(final int index) throws IndexOutOfBoundsException {
			return this.__array.__get(index + this.__offset);
		}

		@Override
		protected boolean __export(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.__array.__export(target, this.__offset + offset2, length2, foreward);
		}

		@Override
		public FEMArray section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.__array.section(this.__offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseArray extends FEMArray {

		final FEMArray __array;

		ReverseArray(final FEMArray array) throws IllegalArgumentException {
			super(array.__length);
			this.__array = array;
		}

		{}

		@Override
		protected FEMValue __get(final int index) throws IndexOutOfBoundsException {
			return this.__array.__get(this.__length - index - 1);
		}

		@Override
		protected boolean __export(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.__array.__export(target, offset, length, !foreward);
		}

		@Override
		public FEMArray concat(final FEMArray value) throws NullPointerException {
			return value.reverse().concat(this.__array).reverse();
		}

		@Override
		public FEMArray section(final int offset, final int length2) throws IllegalArgumentException {
			return this.__array.section(this.__length - offset - length2, length2).reverse();
		}

		@Override
		public FEMArray reverse() {
			return this.__array;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformArray extends FEMArray {

		final FEMValue __value;

		UniformArray(final int length, final FEMValue value) throws IllegalArgumentException {
			super(length);
			this.__value = value;
		}

		{}

		@Override
		protected FEMValue __get(final int index) throws IndexOutOfBoundsException {
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
		public FEMArray reverse() {
			return this;
		}

		@Override
		public FEMArray compact() {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class CompactArray extends FEMArray {

		final FEMValue[] __values;

		CompactArray(final FEMValue[] values) throws IllegalArgumentException {
			super(values.length);
			this.__values = values;
		}

		{}

		@Override
		protected FEMValue __get(final int index) throws IndexOutOfBoundsException {
			return this.__values[index];
		}

		@Override
		public FEMValue[] value() {
			return this.__values.clone();
		}

		@Override
		public FEMArray compact() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert eine leere Wertliste.
	 */
	public static final FEMArray EMPTY = new EmptyArray();

	{}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.<br>
	 * Das gegebene Array wird Kopiert, sodass spätere änderungen am gegebenen Array nicht auf die erzeugte Wertliste übertragen werden.
	 * 
	 * @param values Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist.
	 */
	public static final FEMArray from(final FEMValue... values) throws NullPointerException {
		if (values.length == 0) return FEMArray.EMPTY;
		if (values.length == 1) return FEMArray.from(values[0], 1);
		return new CompactArray(values.clone());
	}

	/**
	 * Diese Methode gibt eine uniforme Wertliste mit der gegebenen Länge zurück, deren Werte alle gleich dem gegebenen sind.
	 * 
	 * @param value Wert.
	 * @param length Länge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	public static final FEMArray from(final FEMValue value, final int length) throws NullPointerException, IllegalArgumentException {
		if (length == 0) return FEMArray.EMPTY;
		if (value == null) throw new NullPointerException("value = null");
		return new UniformArray(length, value);
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see #from(Collection)
	 * @param values Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist.
	 */
	public static final FEMArray from(final Iterable<? extends FEMValue> values) throws NullPointerException {
		final ArrayList<FEMValue> result = new ArrayList<>();
		Iterables.appendAll(result, values);
		return FEMArray.from(result);
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 * 
	 * @see Collection#toArray(Object[])
	 * @see #from(FEMValue...)
	 * @param values Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist.
	 */
	public static final FEMArray from(final Collection<? extends FEMValue> values) throws NullPointerException {
		if (values.size() == 0) return FEMArray.EMPTY;
		return FEMArray.from(values.toArray(new FEMValue[values.size()]));
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
	protected FEMArray(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this.__length = length;
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-ter Wert.
	 */
	protected FEMValue __get(final int index) {
		return null;
	}

	/**
	 * Diese Methode fügt alle Werte im gegebenen Abschnitt in der gegebenen Reigenfolge geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen, wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
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
	 * Diese Methode konvertiert diese Wertliste in ein Array und gibt diese zurück.
	 * 
	 * @return Array mit den Werten dieser Wertliste.
	 */
	public FEMValue[] value() {
		final ValueCollector target = new ValueCollector(this.__length);
		this.export(target);
		return target.array;
	}

	/**
	 * Diese Methode gibt die Länge, d.h. die Anzahl der Werte in der Wertliste zurück.
	 * 
	 * @return Länge der Bytefolge.
	 */
	public final int length() {
		return this.__length;
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste zurück.
	 * 
	 * @param that Wertliste.
	 * @return {@link FEMArray}-Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public FEMArray concat(final FEMArray that) throws NullPointerException {
		if (that.__length == 0) return this;
		if (this.__length == 0) return that;
		return new ConcatArray(this, that);
	}

	/**
	 * Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 * 
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link FEMArray}-Sicht auf einen Abschnitt dieser Wertliste.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Wertliste liegt oder eine negative Länge hätte.
	 */
	public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.__length)) return this;
		if ((offset < 0) || ((offset + length) > this.__length)) throw new IllegalArgumentException();
		if (length == 0) return FEMArray.EMPTY;
		return new SectionArray(this, offset, length);
	}

	/**
	 * Diese Methode gibt eine rückwärts geordnete Sicht auf diese Wertliste zurück.
	 * 
	 * @return rückwärts geordnete {@link FEMArray}-Sicht auf diese Wertliste.
	 */
	public FEMArray reverse() {
		return new ReverseArray(this);
	}

	/**
	 * Diese Methode gibt die {@link #value() Werte dieser Wertliste} in einer performanteren oder zumindest gleichwertigen Wertliste zurück.
	 * 
	 * @see #from(FEMValue...)
	 * @see #value()
	 * @return performanteren Wertliste oder {@code this}.
	 */
	public FEMArray compact() {
		final FEMArray result = this.__length == 1 ? new UniformArray(1, this.__get(0)) : new CompactArray(this.value());
		result.__hash = this.__hash;
		return result;
	}

	/**
	 * Diese Methode gibt die Position des ersten Vorkommens der gegebene Wertliste innerhalb dieser Wertliste zurück.<br>
	 * Die Suche beginnt an der gegebenen Position. Wenn die Wertliste nicht gefunden wird, liefert diese Methode {@code -1}.
	 * 
	 * @param that gesuchte Wertliste.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Wertliste ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist.
	 */
	public final int find(final FEMArray that, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (offset > this.__length)) throw new IllegalArgumentException();
		final int count = that.__length;
		if (count == 0) return offset;
		final FEMValue value = that.__get(0);
		final int length = this.__length - count;
		FIND: for (int i = offset; i < length; i++) {
			if (value.equals(this.__get(i))) {
				for (int i2 = 1; i2 < count; i2++) {
					if (this.__get(i + i2) != that.__get(i2)) {
						continue FIND;
					}
				}
				return i;
			}
		}
		return -1;
	}

	/**
	 * Diese Methode fügt alle Werte dieser Wertliste vom ersten zum letzten geordnet an den gegebenen {@link Collector} an.<br>
	 * Das Anfügen wird vorzeitig abgebrochen , wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 * 
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public final boolean export(final Collector target) throws NullPointerException {
		if (target == null) throw new NullPointerException("target = null");
		if (this.__length == 0) return true;
		return this.__export(target, 0, this.__length, true);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste gleich der gegebenen ist.
	 * 
	 * @param that Wertliste.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public final boolean equals(final FEMArray that) throws NullPointerException {
		final int length = this.__length;
		if (length != that.__length) return false;
		if (this.hashCode() != that.hashCode()) return false;
		for (int i = 0; i < length; i++) {
			if (this.__get(i).equals(that.__get(i))) return false;
		}
		return true;
	}

	/**
	 * Diese Methode gibt diese Wertliste als {@link List} zurück.
	 * 
	 * @see Arrays#asList(Object...)
	 * @return {@link List}.
	 */
	public final List<FEMValue> toList() {
		return Arrays.asList(this.value());
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 */
	@Override
	public final FEMValue get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.__length)) throw new IndexOutOfBoundsException();
		return this.__get(index);
	}

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
		if (!(object instanceof FEMArray)) return false;
		return this.equals(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Iterator<FEMValue> iterator() {
		return Iterators.itemsIterator(this, 0, this.__length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void toScript(final FEM.ScriptFormatter target) throws IllegalArgumentException {
		target.putArray(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return FEM.scriptFormatter().formatData((Object)this);
	}

}
