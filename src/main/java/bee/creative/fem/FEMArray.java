package bee.creative.fem;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.iam.IAMMapping;
import bee.creative.util.Comparables.Items;
import bee.creative.util.Comparators;
import bee.creative.util.Iterables;
import bee.creative.util.Iterators;
import bee.creative.util.Objects;
import bee.creative.util.Objects.UseToString;

/** Diese Klasse implementiert eine unveränderliche Auflistung von Werten sowie Methoden zur Erzeugung solcher Wertlisten.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMArray extends FEMValue implements Items<FEMValue>, Iterable<FEMValue>, UseToString {

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Werten einer Wertliste in der Methode {@link FEMArray#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Wert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammeln fortgeführt werden soll.
		 *
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammeln fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(FEMValue value);

	}

	@SuppressWarnings ("javadoc")
	static class ItemFinder implements Collector {

		public final FEMValue that;

		public int index;

		ItemFinder(final FEMValue that) {
			this.that = that;
		}

		@Override
		public boolean push(final FEMValue value) {
			if (value.equals(this.that)) return false;
			this.index++;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static class HashCollector implements Collector {

		public int hash = Objects.hashInit();

		@Override
		public boolean push(final FEMValue value) {
			this.hash = Objects.hashPush(this.hash, value.hashCode());
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	static class ValueCollector implements Collector {

		public final FEMValue[] array;

		public int index;

		public ValueCollector(final FEMValue[] array, final int index) {
			this.array = array;
			this.index = index;
		}

		@Override
		public boolean push(final FEMValue value) {
			this.array[this.index++] = value;
			return true;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class EmptyArray extends FEMArray {

		EmptyArray() throws IllegalArgumentException {
			super(0);
		}

		@Override
		public FEMArray reverse() {
			return this;
		}

		@Override
		public FEMArray result(final boolean recursive) {
			return this;
		}

		@Override
		public FEMArray compact(final boolean index) {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ConcatArray extends FEMArray implements Emuable {

		public final FEMArray array1;

		public final FEMArray array2;

		ConcatArray(final FEMArray array1, final FEMArray array2) throws IllegalArgumentException {
			super(array1.length + array2.length);
			this.array1 = array1;
			this.array2 = array2;
		}

		@Override
		protected FEMValue customGet(final int index) throws IndexOutOfBoundsException {
			final int index2 = index - this.array1.length;
			return index2 < 0 ? this.array1.customGet(index) : this.array2.customGet(index2);
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, final int length, final boolean foreward) { // TODO
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.customFind(that, offset2, length, foreward);
			if (length2 <= 0) return this.array1.customFind(that, offset, length, foreward);
			if (foreward) {
				final int result = this.array1.customFind(that, offset, -offset2, foreward);
				if (result >= 0) return result;
				return this.array2.customFind(that, 0, length2, foreward);
			} else {
				final int result = this.array2.customFind(that, 0, length2, foreward);
				if (result >= 0) return result;
				return this.array1.customFind(that, offset, -offset2, foreward);
			}
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.customExtract(target, offset2, length, foreward);
			if (length2 <= 0) return this.array1.customExtract(target, offset, length, foreward);
			if (foreward) {
				if (!this.array1.customExtract(target, offset, -offset2, foreward)) return false;
				return this.array2.customExtract(target, 0, length2, foreward);
			} else {
				if (!this.array2.customExtract(target, 0, length2, foreward)) return false;
				return this.array1.customExtract(target, offset, -offset2, foreward);
			}
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.array1) + EMU.from(this.array2);
		}

		@Override
		public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.section(offset2, length);
			if (length2 <= 0) return this.array1.section(offset, length);
			return this.array1.section(offset, -offset2).concat(this.array2.section(0, length2));
		}

	}

	@SuppressWarnings ("javadoc")
	public static class SectionArray extends FEMArray implements Emuable {

		public final FEMArray array;

		public final int offset;

		SectionArray(final FEMArray array, final int offset, final int length) throws IllegalArgumentException {
			super(length);
			this.array = array;
			this.offset = offset;
		}

		@Override
		protected FEMValue customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.customGet(index + this.offset);
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, final int length, final boolean foreward) {
			return super.customFind(that, offset + this.offset, length, foreward);
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.array.customExtract(target, this.offset + offset, length, foreward);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.array);
		}

		@Override
		public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
			return this.array.section(this.offset + offset, length);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class ReverseArray extends FEMArray implements Emuable {

		public final FEMArray array;

		ReverseArray(final FEMArray array) throws IllegalArgumentException {
			super(array.length);
			this.array = array;
		}

		@Override
		protected FEMValue customGet(final int index) throws IndexOutOfBoundsException {
			return this.array.customGet(this.length - index - 1);
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, final int length, final boolean foreward) {
			return this.array.customExtract(target, this.length - offset - length, length, !foreward);
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.array);
		}

		@Override
		public FEMArray concat(final FEMArray that) throws NullPointerException {
			return that.reverse().concat(this.array).reverse();
		}

		@Override
		public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
			return this.array.section(this.length - offset - length, length).reverse();
		}

		@Override
		public FEMArray reverse() {
			return this.array;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class UniformArray extends FEMArray {

		public final FEMValue item;

		UniformArray(final int length, final FEMValue item) throws IllegalArgumentException {
			super(length);
			this.item = item;
		}

		@Override
		protected FEMValue customGet(final int index) throws IndexOutOfBoundsException {
			return this.item;
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset, int length, final boolean foreward) {
			while (length > 0) {
				if (!target.push(this.item)) return false;
				length--;
			}
			return true;
		}

		@Override
		public FEMArray reverse() {
			return this;
		}

		@Override
		public FEMArray result(final boolean deep) {
			return deep ? new UniformArray2(this.length, this.item) : null;
		}

		@Override
		public FEMArray compact(final boolean index) {
			return this.result(index);
		}

	}

	@SuppressWarnings ("javadoc")
	public static class UniformArray2 extends UniformArray {

		UniformArray2(final int length, final FEMValue item) throws IllegalArgumentException {
			super(length, item.result(true));
		}

		@Override
		public FEMArray result(final boolean deep) {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class CompactArray extends FEMArray implements Emuable {

		/** Dieses Feld speichert das Array der Werte, das nicht verändert werden darf. */
		final FEMValue[] items;

		CompactArray(final FEMValue[] items) throws IllegalArgumentException {
			super(items.length);
			this.items = items;
			final int length = items.length;
			for (int i = 0; i < length; i++) {
				Objects.notNull(items[i]);
			}
		}

		@Override
		protected FEMValue customGet(final int index) throws IndexOutOfBoundsException {
			return this.items[index];
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.fromArray(this.items);
		}

		@Override
		public FEMValue[] value() {
			return this.items.clone();
		}

		@Override
		public FEMArray compact(final boolean index) {
			return index ? super.compact(true) : this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class CompactArray2 extends CompactArray {

		CompactArray2(final FEMValue[] items) throws IllegalArgumentException {
			super(items);
			final int length = items.length;
			for (int i = 0; i < length; i++) {
				items[i] = items[i].result(true);
			}
		}

		@Override
		public FEMArray result(final boolean deep) {
			return this;
		}

	}

	@SuppressWarnings ("javadoc")
	public static class CompactArray3 extends CompactArray2 {

		/** Dieses Feld speichert die Streuwerttabelle zu den Werten in {@link #items}. Die Länge der Zahlenfolge entspricht stets einer um 1 sowie um die Länge der
		 * Wertliste {@code length} erhöhten Potenz von {@code 2}. Diese Potenz verringerte um eins wird als Bitmaske {@code mask} für die Streuwerte der Elemente
		 * eingesetzt. Die erste Zahl enthält die Anzahl der Streuwertbereiche und ist gleich der um {@code 2} erhöhten Bitmaske. */
		final int[] table;

		CompactArray3(final FEMValue[] items) throws IllegalArgumentException {
			super(items);
			final int length = this.length, mask = IAMMapping.mask(length), count = mask + 2;
			final int[] table = new int[count + length], hashes = new int[length];
			for (int i = 0; i < length; i++) {
				final int hash = (items[i].hashCode() & mask) + 1;
				table[hash]++;
				hashes[i] = hash;
			}
			for (int i = 1, offset = count; i < count; i++) {
				final int size = table[i];
				table[i] = offset;
				offset += size;
			}
			for (int i = 0; i < length; i++) {
				final int hash = hashes[i], offset = table[hash];
				table[hash] = offset + 1;
				table[offset] = i;
			}
			table[0] = count;
			this.table = table;
		}

		@Override
		protected int customFind(final FEMValue that, final int offset, int length, final boolean foreward) {
			final int hash = that.hashCode() & (this.table.length - this.length - 2);
			int l = this.table[hash], r = this.table[hash + 1] - 1;
			length += offset;
			if (foreward) {
				for (; l <= r; l++) {
					final int result = this.table[l];
					if (length <= result) return -1;
					if ((offset <= result) && that.equals(this.items[result])) return result;
				}
			} else {
				for (; l <= r; r--) {
					final int result = this.table[r];
					if (result < offset) return -1;
					if ((result < length) && that.equals(this.items[result])) return result;
				}
			}
			return -1;
		}

		@Override
		public long emu() {
			return super.emu() + EMU.fromArray(this.items);
		}

		@Override
		public FEMArray compact(final boolean index) {
			return this;
		}

	}

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int ID = 1;

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMArray> TYPE = FEMType.from(FEMArray.ID);

	/** Dieses Feld speichert die leere Wertliste. */
	public static final FEMArray EMPTY = new EmptyArray();

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück. Das gegebene Array wird kopiert.
	 *
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMArray from(final FEMValue... items) throws NullPointerException {
		if (items.length == 0) return FEMArray.EMPTY;
		if (items.length == 1) return new UniformArray(1, items[0]);
		return new CompactArray(items.clone());
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMArray from(final boolean copy, final FEMValue... items) throws NullPointerException {
		if (copy) return FEMArray.from(items);
		return new CompactArray(items);
	}

	/** Diese Methode gibt eine Wertliste mit den Werten im gegebenen Abschnitt zurück. Der gegebene Abschnitt wird kopiert.
	 *
	 * @param items Werte.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder der Abschnitt {@code null} enthält.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMArray from(final FEMValue[] items, final int offset, final int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMArray.EMPTY;
		if (length == 1) return new UniformArray(1, Objects.notNull(items[offset]));
		final FEMValue[] result = new FEMValue[length];
		System.arraycopy(items, offset, result, 0, length);
		return new CompactArray(result);
	}

	/** Diese Methode gibt eine uniforme Wertliste mit der gegebenen Länge zurück, deren Werte alle gleich dem gegebenen sind.
	 *
	 * @param item Wert.
	 * @param length Länge.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code item} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMArray from(final FEMValue item, final int length) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(item);
		if (length == 0) return FEMArray.EMPTY;
		return new UniformArray(length, item);
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 *
	 * @see Collection#toArray(Object[])
	 * @see #from(FEMValue...)
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMArray from(final List<? extends FEMValue> items) throws NullPointerException {
		if (items.size() == 0) return FEMArray.EMPTY;
		return FEMArray.from(items.toArray(new FEMValue[items.size()]));
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 *
	 * @see #from(List)
	 * @see Iterables#toList(Iterable)
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMArray from(final Iterable<? extends FEMValue> items) throws NullPointerException {
		if (items instanceof FEMArray) return (FEMArray)items;
		return FEMArray.from(Iterables.toList(items));
	}

	/** Diese Methode gibt die Verkettung der gegebenen Wertlisten zurück.
	 *
	 * @see #concat(FEMArray)
	 * @param values Wertlisten.
	 * @return Verkettung der Wertlisten.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public static FEMArray concatAll(final FEMArray... values) throws NullPointerException {
		final int length = values.length;
		if (length == 0) return FEMArray.EMPTY;
		if (length == 1) return values[0].data();
		return FEMArray.concatAll(values, 0, length - 1);
	}

	@SuppressWarnings ("javadoc")
	static FEMArray concatAll(final FEMArray[] values, final int min, final int max) throws NullPointerException {
		if (min == max) return values[min];
		final int mid = (min + max) >> 1;
		return FEMArray.concatAll(values, min, mid).concat(FEMArray.concatAll(values, mid + 1, max));
	}

	/** Dieses Feld speichert den Streuwert oder {@code 0}. Es wird in {@link #hashCode()} initialisiert. */
	protected int hash;

	/** Dieses Feld speichert die Länge. */
	protected final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	protected FEMArray(final int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException();
		this.length = length;
	}

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-ter Wert. */
	protected FEMValue customGet(final int index) {
		return null;
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Wertliste innerhalb dieser Wertliste zurück. Sie Implementiert
	 * {@link #find(FEMArray, int)} ohne Wertebereichsprüfung.
	 *
	 * @param that nicht leere gesuchte Wertliste.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Wertliste ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	protected int customFind(final FEMArray that, final int offset) {
		final FEMValue value = that.customGet(0);
		final int count = (this.length - that.length) + 1;
		for (int result = offset; true;) {
			result = this.customFind(value, result, count - result, true);
			if (result < 0) return -1;
			if (this.customEquals(that, result)) return result;
		}
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Werts im gegebenen Abschnitt zurück. Sie Implementiert {@link #find(FEMValue, int)}
	 * ohne Wertebereichsprüfung.
	 *
	 * @param that gesuchter Wert.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return Position des ersten Vorkommens des gegebenen Werts oder {@code -1}.
	 * @param foreward {@code true}, wenn die Reihenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist. */
	protected int customFind(final FEMValue that, final int offset, final int length, final boolean foreward) {
		final ItemFinder finder = new ItemFinder(that);
		if (this.customExtract(finder, offset, length, foreward)) return -1;
		return finder.index + offset;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Wertliste an der gegebenen Position in dieser Wertliste liegt. Sie Implementiert
	 * {@link #equals(FEMArray)} ohne Wertebereichsprüfung. */
	protected boolean customEquals(final FEMArray that, final int offset) {
		final int length = that.length;
		for (int i = 0; i < length; i++) {
			if (!this.customGet(offset + i).equals(that.customGet(i))) return false;
		}
		return true;
	}

	/** Diese Methode fügt alle Werte im gegebenen Abschnitt in der gegebenen Reihenfolge geordnet an den gegebenen {@link Collector} an. Das Anfügen wird
	 * vorzeitig abgebrochen, wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @param foreward {@code true}, wenn die Reihenfolge forwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
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

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMArray data() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public final FEMType<FEMArray> type() {
		return FEMArray.TYPE;
	}

	/** Diese Methode konvertiert diese Wertliste in ein {@code FEMValue[]} und gibt dieses zurück.
	 *
	 * @return Array mit den Werten dieser Wertliste. */
	public FEMValue[] value() {
		final ValueCollector target = new ValueCollector(new FEMValue[this.length], 0);
		this.extract(target);
		return target.array;
	}

	/** Diese Methode gibt die Länge, d.h. die Anzahl der Werte in der Wertliste zurück.
	 *
	 * @return Länge der Wertliste. */
	public final int length() {
		return this.length;
	}

	/** Diese Methode gibt eine Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste zurück.
	 *
	 * @param that Wertliste.
	 * @return {@link FEMArray}-Sicht auf die Verkettung dieser Wertliste mit der gegebenen Wertliste.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public FEMArray concat(final FEMArray that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
		return new ConcatArray(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #section(int, int) this.section(offset, this.length() - offset)}.
	 *
	 * @see #length() */
	public FEMArray section(final int offset) throws IllegalArgumentException {
		return this.section(offset, this.length - offset);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link FEMArray}-Sicht auf einen Abschnitt dieser Wertliste.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Wertliste liegt oder eine negative Länge hätte. */
	public FEMArray section(final int offset, final int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMArray.EMPTY;
		return new SectionArray(this, offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Wertliste zurück.
	 *
	 * @return rückwärts geordnete {@link FEMArray}-Sicht auf diese Wertliste. */
	public FEMArray reverse() {
		return new ReverseArray(this);
	}

	/** Diese Methode ist eine abkürzung für {@link #compact(boolean) compact(false)}.
	 *
	 * @return performanteren Wertliste oder {@code this}. */
	public final FEMArray compact() {
		return this.compact(false);
	}

	/** Diese Methode gibt die {@link #value() Werte dieser Wertliste} in einer performanteren oder zumindest gleichwertigen Wertliste zurück. Wenn diese
	 * Wertliste diesbezüglich optimiert werden kann, wird grundsätzlich eine Abschrift der {@link #value() Werte} dieser Wertliste analog zu
	 * {@link #from(FEMValue...) from(values())} geliefert. Wenn die Indizierung aktiviert ist, wird auch die Leistungsfähigkeit der {@link #find(FEMValue, int)
	 * Einzelwertsuche} optimiert. Hierbei wird grundsätzlich eine Streuwerttabelle angelegt, welche den Speicherverbrauch der Wertliste vervierfachen kann.
	 *
	 * @param index {@code true}, wenn die Einzelwertsuche beschleunigt werden sollen.
	 * @return performanteren Wertliste oder {@code this}. */
	public FEMArray compact(final boolean index) {
		if (this.length == 0) return FEMArray.EMPTY;
		if (this.length == 1) return index ? new UniformArray2(1, this.customGet(0)) : new UniformArray(1, this.customGet(0));
		return index ? new CompactArray3(this.value()) : new CompactArray(this.value());
	}

	/** Diese Methode gibt die Position des ersten Vorkommens des gegebenen Werts innerhalb dieser Wertliste zurück. Die Suche beginnt an der gegebenen Position.
	 * Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchter Wert.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens des gegebenen Werts ({@code offset..this.length()-1}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMValue that, final int offset) throws NullPointerException, IllegalArgumentException {
		Objects.notNull(that);
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (offset == this.length) return -1;
		return this.customFind(that, offset, this.length - offset, true);
	}

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Wertliste innerhalb dieser Wertliste zurück. Die Suche beginnt an der gegebenen
	 * Position. Bei einer erfolglosen Suche wird {@code -1} geliefert.
	 *
	 * @param that gesuchte Wertliste.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Wertliste ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code offset} ungültig ist. */
	public final int find(final FEMArray that, final int offset) throws NullPointerException, IllegalArgumentException {
		if (that.length == 1) return this.find(that.customGet(0), offset);
		if ((offset < 0) || (offset > this.length)) throw new IllegalArgumentException();
		if (that.length == 0) return offset;
		if (that.length > (this.length - offset)) return -1;
		return this.customFind(that, offset);
	}

	/** Diese Methode fügt alle Werte dieser Wertliste vom ersten zum letzten geordnet an den gegebenen {@link Collector} an. Das Anfügen wird vorzeitig
	 * abgebrochen, wenn {@link Collector#push(FEMValue)} {@code false} liefert.
	 *
	 * @param target {@link Collector}, an den die Werte geordnet angefügt werden.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde.
	 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
	public final boolean extract(final Collector target) throws NullPointerException {
		Objects.notNull(target);
		if (this.length == 0) return true;
		return this.customExtract(target, 0, this.length, true);
	}

	/** Diese Methode kopiert alle Werte dieser Wertliste vom ersten zum letzten geordnet in den an der gegebenen Position beginnenden Abschnitt des gegebenen
	 * Arrays.
	 *
	 * @param result Array, in welchem der Abschnitt liegt.
	 * @param offset Beginn des Abschnitts.
	 * @throws NullPointerException Wenn {@code result} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Abschitt außerhalb des gegebenen Arrays liegt. */
	public final void extract(final FEMValue[] result, final int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || ((offset + this.length) > result.length)) throw new IllegalArgumentException();
		this.extract(new ValueCollector(result, offset));
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste gleich der gegebenen ist.
	 *
	 * @param that Wertliste.
	 * @return Gleichheit.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	public final boolean equals(final FEMArray that) throws NullPointerException {
		final int length = this.length;
		if (length != that.length) return false;
		if (this.hashCode() != that.hashCode()) return false;
		return this.customEquals(that, 0);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Wertliste kleiner, gleich oder größer als die
	 * der gegebenen Wertliste ist. Die Werte werden über den gegebenen {@link Comparator} verglichen.
	 *
	 * @param that Wertliste.
	 * @param order {@link Comparator} zum Vergleichen der Werte.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} bzw. {@code order} {@code null} ist. */
	public final int compare(final FEMArray that, final Comparator<FEMValue> order) throws NullPointerException {
		final int length = Math.min(this.length, that.length);
		for (int i = 0; i < length; i++) {
			final int result = order.compare(this.customGet(i), that.customGet(i));
			if (result < 0) return -1;
			if (result > 0) return +1;
		}
		return Comparators.compare(this.length, that.length);
	}

	/** Diese Methode gibt eine unveränderliche {@link List} als Sicht auf diese Wertliste zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @return {@link List}-Sicht. */
	public final List<FEMValue> toList() {
		return new AbstractList<FEMValue>() {

			@Override
			public FEMValue get(final int index) {
				return FEMArray.this.get(index);
			}

			@Override
			public int size() {
				return FEMArray.this.length;
			}

		};
	}

	/** Diese Methode gibt den {@code index}-ten Wert zurück. */
	@Override
	public final FEMValue get(final int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.customGet(index);
	}

	/** {@inheritDoc} */
	@Override
	public FEMArray result(final boolean deep) {
		if (!deep) return this;
		return new CompactArray2(this.value());
	}

	/** {@inheritDoc} */
	@Override
	public final int hashCode() {
		int result = this.hash;
		if (result != 0) return result;
		final HashCollector collector = new HashCollector();
		this.extract(collector);
		result = collector.hash;
		return this.hash = result != 0 ? result : 1;
	}

	/** {@inheritDoc} */
	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMArray)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMArray)) return false;
		}
		return this.equals((FEMArray)object);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<FEMValue> iterator() {
		return Iterators.itemsIterator(this, 0, this.length);
	}

	/** Diese Methode gibt die Textdarstellung zurück. Diese Besteht aus den in eckige Klammern eingeschlossenen und mit Semikolon separierten Textdarstellungen
	 * der Elemente. */
	@Override
	public String toString() {
		final FEMFormatter target = new FEMFormatter();
		FEMDomain.NORMAL.formatArray(target, this);
		return target.format();
	}

}
