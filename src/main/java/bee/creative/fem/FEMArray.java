package bee.creative.fem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import bee.creative.fem.Scripts.ScriptFormatter;
import bee.creative.fem.Scripts.ScriptFormatterInput;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert eine unmodifizierbare Liste von Werten sowie Methoden zur Erzeugung solcher Wertlisten aus nativen Arrays und {@link Iterable}.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class FEMArray implements Items<FEMValue>, Iterable<FEMValue>, ScriptFormatterInput {

	static final class ScopeArray extends FEMArray {

		private final FEMScope data;

		ScopeArray(FEMScope data) throws IllegalArgumentException {
			super(data.size());
			this.data = data;
		}

		@Override
		protected FEMValue get__(final int index) {
			return data.get(index);
		}
	}

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

		public int hash;

		{}

		public HashCollector() {
			this.hash = 0x811C9DC5;
		}

		@Override
		public boolean push(final FEMValue value) {
			this.hash = (this.hash * 0x01000193) ^ value.hashCode();
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ValueCollector implements Collector {

		public int index;

		public final FEMValue[] value;

		public ValueCollector(final int length) {
			this.value = new FEMValue[length];
		}

		{}

		@Override
		public boolean push(final FEMValue value) {
			this.value[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class EmptyArray extends FEMArray {

		public EmptyArray() throws IllegalArgumentException {
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

		final FEMArray array1;

		final FEMArray array2;

		public ConcatArray(final FEMArray array1, final FEMArray array2) throws IllegalArgumentException {
			super(array1.length + array2.length);
			this.array1 = array1;
			this.array2 = array2;
		}

		{}

		@Override
		protected FEMValue get__(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.array1.length;
			return index2 < 0 ? this.array1.get__(index) : this.array2.get__(index2);
		}

		@Override
		protected boolean export__(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.export__(target, offset2, length, foreward);
			if (length2 <= 0) return this.array1.export__(target, offset, length, foreward);
			if (foreward) {
				if (!this.array1.export__(target, offset, -offset2, foreward)) return false;
				return this.array2.export__(target, 0, length2, foreward);
			} else {
				if (!this.array2.export__(target, 0, length2, foreward)) return false;
				return this.array1.export__(target, offset, -offset2, foreward);
			}
		}

		@Override
		public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.section(offset2, length);
			if (length2 <= 0) return super.section(offset, length);
			return super.section(offset, -offset2).concat(this.array2.section(0, length2));
		}
	}

	@SuppressWarnings ("javadoc")
	static final class SectionArray extends FEMArray {

		final int offset;

		final FEMArray array;

		public SectionArray(final FEMArray array, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		{}

		@Override
		protected FEMValue get__(final int index) throws IndexOutOfBoundsException {
			return this.array.get__(index + this.offset);
		}

		@Override
		protected boolean export__(final Collector target, final int offset2, final int length2, final boolean foreward) {
			return this.array.export__(target, this.offset + offset2, length2, foreward);
		}

		@Override
		public FEMArray section(final int offset2, final int length2) throws IllegalArgumentException {
			return this.array.section(this.offset + offset2, length2);
		}

	}

	@SuppressWarnings ("javadoc")
	static final class ReverseArray extends FEMArray {

		final FEMArray array;

		public ReverseArray(final FEMArray array) throws IllegalArgumentException {
			super(array.length);
			this.array = array;
		}

		{}

		@Override
		protected FEMValue get__(final int index) throws IndexOutOfBoundsException {
			return this.array.get__(this.length - index - 1);
		}

		@Override
		protected boolean export__(final Collector target, final int offset, final int length, final boolean foreward) {
			return super.export__(target, offset, length, !foreward);
		}

		@Override
		public FEMArray concat(final FEMArray value) throws NullPointerException {
			return value.reverse().concat(this.array).reverse();
		}

		@Override
		public FEMArray section(final int offset, final int length2) throws IllegalArgumentException {
			return this.array.section(this.length - offset - 1, length2).reverse();
		}

		@Override
		public FEMArray reverse() {
			return this.array;
		}

	}

	@SuppressWarnings ("javadoc")
	static final class UniformArray extends FEMArray {

		final FEMValue value;

		public UniformArray(final int length, final FEMValue value) throws IllegalArgumentException {
			super(length);
			this.value = value;
		}

		{}

		@Override
		protected FEMValue get__(final int index) throws IndexOutOfBoundsException {
			return this.value;
		}

		@Override
		protected boolean export__(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.value)) return false;
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

		final FEMValue[] values;

		public CompactArray(final FEMValue[] values) throws IllegalArgumentException {
			super(values.length);
			this.values = values;
		}

		{}

		@Override
		public FEMValue[] value() {
			return this.values.clone();
		}

		@Override
		protected FEMValue get__(final int index) throws IndexOutOfBoundsException {
			return this.values[index];
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
	 * Diese Methode gibt eine Wertliste als Sicht auf die zugesicherten Parameterwerte des gegebenen Ausführungskontexts zurück.
	 * 
	 * @param scope Ausführungskontext.
	 * @return {@link FEMArray} der Parameterwerte.
	 * @throws NullPointerException Wenn {@code scope} {@code null} ist.
	 */
	public static FEMArray from(final FEMScope scope) throws NullPointerException {
		if (scope.size() == 0) return FEMArray.EMPTY;
		return new ScopeArray(scope);
	}

	/**
	 * Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.<br>
	 * Das gegebene Array wird Kopiert, sodass spätere änderungen am gegebenen Array nicht auf die erzeugte Wertliste übertragen werden.
	 * 
	 * @param values Werte.
	 * @return {@link FEMArray}.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist.
	 */
	public static FEMArray from(final FEMValue... values) throws NullPointerException {
		if (values.length == 0) return FEMArray.EMPTY;
		if (values.length == 1) return FEMArray.from(values[0], 1);
		return new CompactArray(values.clone());
	}

	/**
	 * Diese Methode gibt eine uniforme Wertliste mit der gegebenen Länge zurück, deren Werte alle gleich dem gegebenen Wert sind.
	 * 
	 * @param value Wert.
	 * @param length Länge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist.
	 */
	public static FEMArray from(final FEMValue value, final int length) throws NullPointerException, IllegalArgumentException {
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
	public static FEMArray from(final Iterable<? extends FEMValue> values) throws NullPointerException {
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
	public static FEMArray from(final Collection<? extends FEMValue> values) throws NullPointerException {
		if (values.size() == 0) return FEMArray.EMPTY;
		return FEMArray.from(values.toArray(new FEMValue[values.size()]));
	}

	{}

	/**
	 * Dieses Feld speichert den Streuwert.
	 */
	int hash;

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
	FEMArray(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException("length < 0");
		this.length = length;
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-ter Wert.
	 */
	protected FEMValue get__(final int index) {
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
	protected boolean export__(final Collector target, int offset, int length, final boolean foreward) {
		if (foreward) {
			for (length += offset; offset < length; offset++) {
				if (!target.push(this.get__(offset))) return false;
			}
		} else {
			for (length += offset - 1; offset <= length; length--) {
				if (!target.push(this.get__(length))) return false;
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
		final int length = this.length;
		final ValueCollector collector = new ValueCollector(length);
		this.export__(collector, 0, length, true);
		return collector.value;
	}

	/**
	 * Diese Methode gibt die Länge, d.h. die Anzahl der Werte in der Wertliste zurück.
	 * 
	 * @return Länge der Bytefolge.
	 */
	public final int length() {
		return this.length;
	}

	/**
	 * Diese Methode gibt eine Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste zurück.
	 * 
	 * @param that Wertliste.
	 * @return {@link FEMArray}-Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 */
	public FEMArray concat(final FEMArray that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
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
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
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
		final FEMArray result = this.length == 1 ? new UniformArray(1, this.get__(0)) : new CompactArray(this.value());
		result.hash = this.hash;
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
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		final int count = that.length;
		if (count == 0) return offset;
		final FEMValue value = that.get__(0);
		final int length = this.length - count;
		FIND: for (int i = offset; i < length; i++) {
			if (value.equals(this.get__(i))) {
				for (int i2 = 1; i2 < count; i2++) {
					if (this.get__(offset + i2) != that.get__(i2)) {
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
	 * @param collector {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist.
	 */
	public final boolean export(final Collector collector) throws NullPointerException {
		return this.export__(collector, 0, this.length, true);
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste gleich der gegebenen ist.
	 * 
	 * @param that Wertliste.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public final boolean equals(final FEMArray that) throws NullPointerException {
		final int length = this.length;
		if (length != that.length) return false;
		int i = this.hash;
		if (i != 0) {
			final int i2 = that.hash;
			if ((i2 != 0) && (i != i2)) return false;
		}
		for (i = 0; i < length; i++) {
			if (this.get__(i).equals(that.get__(i))) return false;
		}
		return true;
	}

	{}

	/**
	 * Diese Methode gibt den {@code index}-ten Wert zurück.
	 */
	@Override
	public final FEMValue get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.get__(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		int result = this.hash;
		if (result != 0) return result;
		final int length = this.length;
		final HashCollector collector = new HashCollector();
		this.export__(collector, 0, length, true);
		this.hash = (result = (collector.hash | 1));
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
		return new Iterator<FEMValue>() {

			int index = 0;

			@Override
			public FEMValue next() {
				return FEMArray.this.get__(this.index++);
			}

			@Override
			public boolean hasNext() {
				return this.index < FEMArray.this.length;
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
	public final void toScript(final ScriptFormatter target) throws IllegalArgumentException {
		target.putArray(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return FEM.formatArray(this);
	}

}
