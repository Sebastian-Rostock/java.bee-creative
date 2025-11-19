package bee.creative.fem;

import static bee.creative.fem.FEMVoid.VALUE;
import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Iterators.iteratorFromArray;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.RandomAccess;
import bee.creative.emu.EMU;
import bee.creative.emu.Emuable;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.UseToString;
import bee.creative.util.AbstractList2;
import bee.creative.util.AbstractSet2;
import bee.creative.util.Comparators;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterables;
import bee.creative.util.Iterator3;
import bee.creative.util.List2;
import bee.creative.util.Map3;

/** Diese Klasse implementiert eine unveränderliche Auflistung von Werten sowie Methoden zur Erzeugung solcher Wertlisten.
 *
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public abstract class FEMArray implements FEMValue, Array<FEMValue>, Iterable2<FEMValue>, UseToString {

	/** Dieses Feld speichert den {@link #type() Datentyp}. */
	public static final FEMType<FEMArray> TYPE = new FEMType<>(FEMArray.TYPE_ID);

	/** Dieses Feld speichert den Identifikator von {@link #TYPE}. */
	public static final int TYPE_ID = 1;

	/** Dieses Feld speichert die leere Wertliste. */
	public static final FEMArray EMPTY = new UniformArray2(0, VALUE);

	/** Diese Methode gibt eine uniforme Wertliste mit der gegebenen Länge zurück, deren Werte alle gleich dem gegebenen sind.
	 *
	 * @param length Länge.
	 * @param item Wert.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code item} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	public static FEMArray from(int length, FEMValue item) throws NullPointerException, IllegalArgumentException {
		notNull(item);
		if (length == 0) return EMPTY;
		return new UniformArray(length, item);
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück und ist eine Abkürzung für {@link #from(boolean, FEMValue...)
	 * FEMArray.from(true, items)}
	 *
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMArray from(FEMValue... items) throws NullPointerException {
		return from(true, items);
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 *
	 * @param copy {@code true}, wenn das gegebene Array kopiert werden soll.
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder enthält. */
	public static FEMArray from(boolean copy, FEMValue... items) throws NullPointerException {
		if (items.length == 0) return FEMArray.EMPTY;
		if (items.length == 1) return new UniformArray(1, items[0]);
		return new CompactArray(copy ? items.clone() : items);
	}

	/** Diese Methode gibt eine Wertliste mit den Werten im gegebenen Abschnitt zurück. Der gegebene Abschnitt wird kopiert.
	 *
	 * @param items Werte.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist oder der Abschnitt {@code null} enthält.
	 * @throws IllegalArgumentException Wenn der Abschnitt ungültig ist. */
	public static FEMArray from(FEMValue[] items, int offset, int length) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IllegalArgumentException();
		if (length == 0) return FEMArray.EMPTY;
		if (length == 1) return new UniformArray(1, notNull(items[offset]));
		var result = new FEMValue[length];
		System.arraycopy(items, offset, result, 0, length);
		return new CompactArray(result);
	}

	/** Diese Methode konvertiert die gegebenen Werte in eine Wertliste und gibt diese zurück.
	 *
	 * @see #from(FEMValue...)
	 * @see Iterables#toArray(Iterable, Object[])
	 * @param items Werte.
	 * @return Wertliste.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static FEMArray from(Iterable<? extends FEMValue> items) throws NullPointerException {
		if (items instanceof FEMArray) return (FEMArray)items;
		return from(Iterables.toArray(items, new FEMValue[0]));
	}

	/** Diese Methode überführt die {@link Entry Einträge} der gegebenen {@link Map Abbildung} in eine {@link #compact(boolean) indizierte Schlüsselliste} sowie
	 * eine {@link #compact() kompaktierte Wertliste} und liefert eine neue Wertliste, die diese beiden Listen in dieser Reihenfolge enthält.
	 *
	 * @see #toMap()
	 * @param entries Abbildung.
	 * @return Wertliste mit Schlüsselliste und Wertliste.
	 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
	public static FEMArray from(Map<? extends FEMValue, ? extends FEMValue> entries) throws NullPointerException {
		if (entries instanceof ItemMap) return ((ItemMap)entries).toArray();
		var keys = new ArrayList<FEMValue>();
		var values = new ArrayList<FEMValue>();
		for (var entry: entries.entrySet()) {
			keys.add(entry.getKey());
			values.add(entry.getValue());
		}
		return from(from(keys).compact(true), from(values).compact());
	}

	/** Diese Methode gibt die Verkettung der gegebenen Wertlisten zurück.
	 *
	 * @see #concat(FEMArray)
	 * @param values Wertlisten.
	 * @return Verkettung der Wertlisten.
	 * @throws NullPointerException Wenn {@code values} {@code null} ist oder enthält. */
	public static FEMArray concatAll(FEMArray... values) throws NullPointerException {
		var length = values.length;
		if (length == 0) return FEMArray.EMPTY;
		if (length == 1) return values[0].data();
		return concatAll(values, 0, length - 1);
	}

	/** Diese Methode gibt {@code this} zurück. */
	@Override
	public final FEMArray data() {
		return this;
	}

	@Override
	public final FEMType<FEMArray> type() {
		return TYPE;
	}

	/** Diese Methode konvertiert diese Wertliste in ein {@code FEMValue[]} und gibt dieses zurück.
	 *
	 * @return Array mit den Werten dieser Wertliste. */
	public FEMValue[] value() {
		final var target = new GetValue(new FEMValue[this.length], 0);
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
	public FEMArray concat(FEMArray that) throws NullPointerException {
		if (that.length == 0) return this;
		if (this.length == 0) return that;
		return ConcatArray.concat(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #section(int, int) this.section(offset, this.length() - offset)}.
	 *
	 * @see #length() */
	public final FEMArray section(int offset) throws IllegalArgumentException {
		return this.section(offset, this.length - offset);
	}

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link FEMArray}-Sicht auf einen Abschnitt dieser Wertliste.
	 * @throws IllegalArgumentException Wenn der Abschnitt nicht innerhalb dieser Wertliste liegt oder eine negative Länge hätte. */
	public final FEMArray section(int offset, int length) throws IllegalArgumentException {
		if ((offset == 0) && (length == this.length)) return this;
		if ((offset < 0) || ((offset + length) > this.length)) throw new IllegalArgumentException();
		if (length == 0) return EMPTY;
		return this.customSection(offset, length);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf diese Wertliste zurück.
	 *
	 * @return rückwärts geordnete {@link FEMArray}-Sicht auf diese Wertliste. */
	public FEMArray reverse() {
		if (this.length < 2) return this;
		return new ReverseArray(this);
	}

	/** Diese Methode ist eine abkürzung für {@link #compact(boolean) compact(false)}.
	 *
	 * @return performanteren Wertliste oder {@code this}. */
	public final FEMArray compact() {
		return this.compact(false);
	}

	/** Diese Methode gibt diese Wertliste mit optimierter Leistungsfähigkeit des {@link #get(int) Wertzugriffs} zurück. Wenn die Wertliste diesbezüglich
	 * optimiert werden kann, wird grundsätzlich eine Abschrift der {@link #value() Werte} dieser Wertliste analog zu {@link #from(FEMValue...) from(values())}
	 * geliefert. Wenn die Indizierung aktiviert ist, wird auch die Leistungsfähigkeit der {@link #find(FEMValue, int) Wertsuche} optimiert. Hierbei wird
	 * grundsätzlich eine Streuwerttabelle angelegt, welche den Speicherverbrauch der Wertliste vervierfachen kann.
	 *
	 * @param index {@code true}, wenn die Einzelwertsuche beschleunigt werden sollen.
	 * @return performantere Wertliste oder {@code this}. */
	public FEMArray compact(boolean index) {
		if (this.isEmpty()) return EMPTY;
		if (this.isUniform()) return index ? new UniformArray2(this.length, this.customGet(0)) : new UniformArray(this.length, this.customGet(0));
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
	public final int find(FEMValue that, int offset) throws NullPointerException, IllegalArgumentException {
		notNull(that);
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
	public final int find(FEMArray that, int offset) throws NullPointerException, IllegalArgumentException {
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
	public final boolean extract(Collector target) throws NullPointerException {
		notNull(target);
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
	public final void extract(FEMValue[] result, int offset) throws NullPointerException, IllegalArgumentException {
		if ((offset < 0) || ((offset + this.length) > result.length)) throw new IllegalArgumentException();
		this.extract(new GetValue(result, offset));
	}

	/** Diese Methode gibt den {@code index}-ten Wert zurück. */
	@Override
	public final FEMValue get(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.customGet(index);
	}

	@Override
	public FEMArray result(boolean deep) {
		return deep ? new CompactArray2(this.value()) : this;
	}

	@Override
	public int hashCode() {
		var collector = new GetHash();
		this.extract(collector);
		var result = collector.hash;
		return result != 0 ? result : -1;
	}

	@Override
	public final boolean equals(Object object) {
		if (object == this) return true;
		if (!(object instanceof FEMArray)) {
			if (!(object instanceof FEMValue)) return false;
			object = ((FEMValue)object).data();
			if (!(object instanceof FEMArray)) return false;
		}
		return this.customEquals((FEMArray)object);
	}

	@Override
	public Iterator3<FEMValue> iterator() {
		return iteratorFromArray(this, 0, this.length);
	}

	/** Diese Methode gibt {@code -1}, {@code 0} bzw. {@code +1} zurück, wenn die lexikographische Ordnung dieser Wertliste kleiner, gleich oder größer als die
	 * der gegebenen Wertliste ist. Die Werte werden über den gegebenen {@link Comparator} verglichen.
	 *
	 * @param that Wertliste.
	 * @param order {@link Comparator} zum Vergleichen der Werte.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code that} bzw. {@code order} {@code null} ist. */
	public int compareTo(FEMArray that, Comparator<FEMValue> order) throws NullPointerException {
		var length = Math.min(this.length, that.length);
		for (var i = 0; i < length; i++) {
			var result = order.compare(this.customGet(i), that.customGet(i));
			if (result < 0) return -1;
			if (result > 0) return +1;
		}
		return Comparators.compare(this.length, that.length);
	}

	/** Diese Methode gibt eine unveränderliche {@link Map} als Sicht auf die Schlüssel- und Wertlisten zurück, aus denen diese Wertliste besteht.<br>
	 * Sie ist damit die Umkehroperation zu {@link #from(Map)}. Der {@link Entry#getKey() Schlüssel} eines {@link Entry Eintrags} befindet sich in {@code keys} an
	 * der Position, an der sich in {@code values} der zugeordnete {@link Entry#getValue() Wert} befindet. Die Schlüssel sollten zur effizienten Suche
	 * {@link #compact(boolean) indiziert} sein.
	 *
	 * @return {@link Map}-Sicht.
	 * @throws IllegalArgumentException Wenn diese Wertliste nicth aus zwei Wertlisten besteht oder die Längen dieser Wertlisten ungleich sind. */
	public final Map3<FEMValue, FEMValue> toMap() throws IllegalArgumentException {
		if (this.length != 2) throw new IllegalArgumentException();
		var keys1 = this.customGet(0);
		var values1 = this.customGet(1);
		if (!(keys1 instanceof FEMArray) || !(values1 instanceof FEMArray)) throw new IllegalArgumentException();
		var keys2 = (FEMArray)keys1;
		var values2 = (FEMArray)values1;
		if (keys2.length != values2.length) throw new IllegalArgumentException();
		return new ItemMap(keys2, values2);
	}

	/** Diese Methode gibt eine unveränderliche {@link List} als Sicht auf diese Wertliste zurück.
	 *
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return {@link List}-Sicht. */
	public final List2<FEMValue> toList() {
		return new ItemList(this);
	}

	/** Diese Methode gibt die Textdarstellung zurück. Diese Besteht aus den in eckige Klammern eingeschlossenen und mit Semikolon separierten Textdarstellungen
	 * der Elemente. */
	@Override
	public String toString() {
		var target = new FEMPrinter();
		FEMDomain.DEFAULT.printArray(target, this);
		return target.print();
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste leer ist.
	 *
	 * @return {@code true} bei Leerheit. */
	public final boolean isEmpty() {
		return this.length == 0;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die Indizierung aktiviert, d.h die Leistungsfähigkeit der {@link #find(FEMValue, int) Wertsuche}
	 * optimiert ist.
	 *
	 * @return {@code true} bei Indizierung. */
	public boolean isIndexed() {
		return false;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste keine sich unterscheidenden Werte enthält.
	 *
	 * @return {@code true} bei Uniformität. */
	public boolean isUniform() {
		return this.isEmpty() || this.extract(new GetUniform(this.customGet(0)));
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die Kompaktierung aktiviert, d.h die Leistungsfähigkeit des {@link #get(int) Wertzugriffs} optimiert
	 * ist.
	 *
	 * @return {@code true} bei Kompaktierung. */
	public boolean isCompacted() {
		return false;
	}

	/** Diese Schnittstelle definiert ein Objekt zum geordneten Sammeln von Werten einer Wertliste in der Methode {@link FEMArray#extract(Collector)}. */
	public static interface Collector {

		/** Diese Methode fügt den gegebenen Wert an das Ende der Sammlung an und gibt nur dann {@code true} zurück, wenn das Sammeln fortgeführt werden soll.
		 *
		 * @param value Wert.
		 * @return {@code true}, wenn das Sammeln fortgeführt werden soll, bzw. {@code false}, wenn es abgebrochen werden soll. */
		public boolean push(FEMValue value);

	}

	/** Diese Klasse implementiert eine abstrakte {@link FEMArray Wertliste} mit {@link #hash Streuwertpuffer}.
	 *
	 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class HashArray extends FEMArray {

		@Override
		public int hashCode() {
			var result = this.hash;
			if (result != 0) return result;
			return this.hash = super.hashCode();
		}

		/** Dieses Feld speichert den Streuwert oder {@code 0}. Es wird in {@link #hashCode()} initialisiert. */
		protected int hash;

		/** Dieser Konstruktor initialisiert die Länge.
		 *
		 * @param length Länge.
		 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
		protected HashArray(int length) throws IllegalArgumentException {
			super(length);
		}

	}

	/** Dieses Feld speichert die Länge. */
	protected final int length;

	/** Dieser Konstruktor initialisiert die Länge.
	 *
	 * @param length Länge.
	 * @throws IllegalArgumentException Wenn {@code length < 0} ist. */
	protected FEMArray(int length) throws IllegalArgumentException {
		if (length < 0) throw new IllegalArgumentException();
		this.length = length;
	}

	/** Diese Methode gibt den {@code index}-ten Wert zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-ter Wert. */
	protected abstract FEMValue customGet(int index);

	/** Diese Methode gibt die Position des ersten Vorkommens der gegebenen Wertliste innerhalb dieser Wertliste zurück. Sie Implementiert
	 * {@link #find(FEMArray, int)} ohne Wertebereichsprüfung.
	 *
	 * @param that nicht leere gesuchte Wertliste.
	 * @param offset Position, an der die Suche beginnt ({@code 0..this.length()}).
	 * @return Position des ersten Vorkommens der gegebene Wertliste ({@code offset..this.length()-that.length()}) oder {@code -1}.
	 * @throws NullPointerException Wenn {@code that} {@code null} ist. */
	protected int customFind(FEMArray that, int offset) {
		var value = that.customGet(0);
		var count = (this.length - that.length) + 1;
		for (var result = offset; true; result++) {
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
	 * @param foreward {@code true}, wenn die Reihenfolge vorwärts ist, bzw. {@code false}, wenn sie rückwärts ist. */
	protected int customFind(FEMValue that, int offset, int length, boolean foreward) {
		var finder = new ItemFinder(that);
		if (this.customExtract(finder, offset, length, foreward)) return -1;
		return foreward ? (finder.index + offset) : (length - finder.index - 1);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Wertliste gleich der gegebenen ist. Sie Implementiert {@link #equals(Object)}. **/
	protected boolean customEquals(FEMArray that) throws NullPointerException {
		if (this == that) return true;
		if ((this.length != that.length) || (this.hashCode() != that.hashCode())) return false;
		return this.customEquals(that, 0);
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn die gegebenen Wertliste an der gegebenen Position in dieser Wertliste liegt. */
	protected boolean customEquals(FEMArray that, int offset) {
		var length = that.length;
		for (var i = 0; i < length; i++) {
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
	 * @param foreward {@code true}, wenn die Reihenfolge vorwärts ist, bzw. {@code false}, wenn sie rückwärts ist.
	 * @return {@code false}, wenn das Anfügen vorzeitig abgebrochen wurde. */
	protected boolean customExtract(Collector target, int offset, int length, boolean foreward) {
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

	/** Diese Methode gibt eine Sicht auf einen Abschnitt dieser Wertliste zurück. Sie Implementiert {@link #section(int, int)} ohne Wertebereichsprüfung.
	 *
	 * @param offset Position, an welcher der Abschnitt beginnt.
	 * @param length Anzahl der Werte im Abschnitt.
	 * @return {@link FEMArray}-Sicht auf einen Abschnitt dieser Wertliste. */
	protected FEMArray customSection(int offset, int length) {
		return new SectionArray(this, offset, length);
	}

	static FEMArray concatAll(FEMArray[] values, int min, int max) throws NullPointerException {
		if (min == max) return values[min];
		var mid = (min + max) >> 1;
		return concatAll(values, min, mid).concat(concatAll(values, mid + 1, max));
	}

	int findLast(Object key) {
		if ((this.length == 0) || !(key instanceof FEMValue)) return -1;
		return this.customFind((FEMValue)key, 0, this.length, false);
	}

	int findFirst(Object key) {
		if ((this.length == 0) || !(key instanceof FEMValue)) return -1;
		return this.customFind((FEMValue)key, 0, this.length, true);
	}

	static class ConcatArray extends HashArray implements Emuable {

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.array1) + EMU.from(this.array2);
		}

		@Override
		public boolean isIndexed() {
			return this.array1.isIndexed() && this.array2.isIndexed();
		}

		@Override
		protected FEMValue customGet(int index) throws IndexOutOfBoundsException {
			var index2 = index - this.array1.length;
			return index2 < 0 ? this.array1.customGet(index) : this.array2.customGet(index2);
		}

		@Override
		protected int customFind(FEMValue that, int offset1, int length, boolean foreward) {
			final int length1 = this.array1.length, offset2 = offset1 - length1, length2 = offset2 + length;
			if (offset2 >= 0) {
				var result = this.array2.customFind(that, offset2, length, foreward);
				if (result >= 0) return result + length1;
				return result;
			}
			if (length2 <= 0) return this.array1.customFind(that, offset1, length, foreward);
			if (foreward) {
				var result = this.array1.customFind(that, offset1, -offset2, foreward);
				if (result >= 0) return result;
				result = this.array2.customFind(that, 0, length2, foreward);
				if (result >= 0) return result + length1;
				return result;
			} else {
				final var result = this.array2.customFind(that, 0, length2, foreward);
				if (result >= 0) return result + length1;
				return this.array1.customFind(that, offset1, -offset2, foreward);
			}
		}

		@Override
		protected boolean customExtract(final Collector target, final int offset1, final int length, final boolean foreward) {
			final int offset2 = offset1 - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.customExtract(target, offset2, length, foreward);
			if (length2 <= 0) return this.array1.customExtract(target, offset1, length, foreward);
			if (foreward) {
				if (!this.array1.customExtract(target, offset1, -offset2, foreward)) return false;
				return this.array2.customExtract(target, 0, length2, foreward);
			} else {
				if (!this.array2.customExtract(target, 0, length2, foreward)) return false;
				return this.array1.customExtract(target, offset1, -offset2, foreward);
			}
		}

		@Override
		protected FEMArray customSection(final int offset, final int length) {
			final int offset2 = offset - this.array1.length, length2 = offset2 + length;
			if (offset2 >= 0) return this.array2.section(offset2, length);
			if (length2 <= 0) return this.array1.section(offset, length);
			return this.array1.customSection(offset, -offset2).concat(this.array2.customSection(0, length2));
		}

		static int size(FEMArray array) {
			for (var size = 0; true; size++) {
				if (array instanceof ConcatArray2) {
					array = ((ConcatArray)array).array2;
				} else if (array instanceof ConcatArray) {
					array = ((ConcatArray)array).array1;
				} else return size;
			}
		}

		static ConcatArray concat(FEMArray array1, FEMArray array2) throws IllegalArgumentException {
			var size1 = size(array1);
			var size2 = size(array2);
			if ((size1 + 1) < size2) {
				var ca2 = (ConcatArray)array2;
				if (!(ca2 instanceof ConcatArray1)) return concat(concat(array1, ca2.array1), ca2.array2);
				var ca21 = (ConcatArray)ca2.array1;
				return concat(concat(array1, ca21.array1), concat(ca21.array2, ca2.array2));
			}
			if ((size2 + 1) < size1) {
				var ca1 = (ConcatArray)array1;
				if (!(ca1 instanceof ConcatArray2)) return concat(ca1.array1, concat(ca1.array2, array2));
				var ca12 = (ConcatArray)ca1.array2;
				return concat(concat(ca1.array1, ca12.array1), concat(ca12.array2, array2));
			}
			if (size1 > size2) return new ConcatArray1(array1, array2);
			if (size1 < size2) return new ConcatArray2(array1, array2);
			return new ConcatArray(array1, array2);
		}

		final FEMArray array1;

		final FEMArray array2;

		ConcatArray(FEMArray array1, FEMArray array2) throws IllegalArgumentException {
			super(array1.length + array2.length);
			this.array1 = array1;
			this.array2 = array2;
		}

	}

	static class ConcatArray1 extends ConcatArray {

		ConcatArray1(FEMArray array1, FEMArray array2) throws IllegalArgumentException {
			super(array1, array2);
		}

	}

	static class ConcatArray2 extends ConcatArray {

		ConcatArray2(FEMArray array1, FEMArray array2) throws IllegalArgumentException {
			super(array1, array2);
		}

	}

	static class SectionArray extends HashArray implements Emuable {

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.array);
		}

		@Override
		public boolean isIndexed() {
			return this.array.isIndexed();
		}

		@Override
		protected FEMValue customGet(int index) throws IndexOutOfBoundsException {
			return this.array.customGet(index + this.offset);
		}

		@Override
		protected int customFind(FEMValue that, int offset, int length, boolean foreward) {
			var result = this.array.customFind(that, offset + this.offset, length, foreward);
			return result >= 0 ? result - this.offset : -1;
		}

		@Override
		protected boolean customExtract(Collector target, int offset, int length, boolean foreward) {
			return this.array.customExtract(target, offset + this.offset, length, foreward);
		}

		@Override
		protected FEMArray customSection(int offset2, int length2) {
			return this.array.customSection(this.offset + offset2, length2);
		}

		final FEMArray array;

		final int offset;

		SectionArray(FEMArray array, int offset, int length) throws IllegalArgumentException {
			super(length);
			this.array = array;
			this.offset = offset;
		}

	}

	static class ReverseArray extends HashArray implements Emuable {

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.array);
		}

		@Override
		public FEMArray reverse() {
			return this.array;
		}

		@Override
		public boolean isIndexed() {
			return this.array.isIndexed();
		}

		@Override
		public boolean isUniform() {
			return this.array.isUniform();
		}

		@Override
		protected FEMValue customGet(int index) throws IndexOutOfBoundsException {
			return this.array.customGet(this.length - index - 1);
		}

		@Override
		protected int customFind(FEMValue that, int offset, int length, boolean foreward) {
			var result = this.array.customFind(that, this.length - offset - length, length, !foreward);
			return result >= 0 ? this.length - result - 1 : -1;
		}

		@Override
		protected boolean customExtract(Collector target, int offset, int length, boolean foreward) {
			return this.array.customExtract(target, this.length - offset - length, length, !foreward);
		}

		@Override
		protected FEMArray customSection(int offset2, int length2) {
			return this.array.customSection(this.length - offset2 - length2, length2).reverse();
		}

		final FEMArray array;

		ReverseArray(FEMArray array) throws IllegalArgumentException {
			super(array.length);
			this.array = array;
		}

	}

	static class UniformArray extends HashArray {

		@Override
		public FEMArray reverse() {
			return this;
		}

		@Override
		public FEMArray result(boolean deep) {
			return deep ? new UniformArray2(this.length, this.value) : this;
		}

		@Override
		public FEMArray compact(boolean index) {
			return this.result(index);
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		public boolean isUniform() {
			return true;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		protected FEMValue customGet(int index) throws IndexOutOfBoundsException {
			return this.value;
		}

		@Override
		protected int customFind(FEMValue that, int offset, int length, boolean foreward) {
			return this.value.equals(that) ? offset : -1;
		}

		@Override
		protected boolean customExtract(Collector target, int offset, int length, boolean foreward) {
			while (length > 0) {
				if (!target.push(this.value)) return false;
				length--;
			}
			return true;
		}

		final FEMValue value;

		UniformArray(int length, FEMValue value) throws IllegalArgumentException {
			super(length);
			this.value = value;
		}

	}

	static class UniformArray2 extends UniformArray {

		@Override
		public FEMArray result(boolean deep) {
			return this;
		}

		UniformArray2(int length, FEMValue item) throws IllegalArgumentException {
			super(length, item.result(true));
		}

	}

	static class CompactArray extends HashArray implements Emuable {

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.fromArray(this.items);
		}

		@Override
		public FEMValue[] value() {
			return this.items.clone();
		}

		@Override
		public FEMArray result(boolean deep) {
			return deep ? new CompactArray2(this.items) : this;
		}

		@Override
		public FEMArray compact(boolean index) {
			if (this.isEmpty()) return EMPTY;
			if (this.isUniform()) return index ? new UniformArray2(this.length, this.customGet(0)) : new UniformArray(this.length, this.customGet(0));
			return index ? new CompactArray3(this.items) : this;
		}

		@Override
		public boolean isCompacted() {
			return true;
		}

		@Override
		protected FEMValue customGet(int index) throws IndexOutOfBoundsException {
			return this.items[index];
		}

		/** Dieses Feld speichert das Array der Werte, das nicht verändert werden darf. */
		final FEMValue[] items;

		CompactArray(FEMValue[] items) throws IllegalArgumentException {
			this(items.length, items);
			for (var item: items) {
				notNull(item);
			}
		}

		CompactArray(int length, FEMValue[] items) throws IllegalArgumentException {
			super(length);
			this.items = items;
		}

	}

	static class CompactArray2 extends CompactArray {

		@Override
		public FEMArray result(boolean deep) {
			return this;
		}

		CompactArray2(FEMValue[] items) throws IllegalArgumentException {
			super(items.length, items);
			for (var i = 0; i < items.length; i++) {
				items[i] = items[i].result(true);
			}
		}

	}

	static class CompactArray3 extends CompactArray2 {

		@Override
		public long emu() {
			return super.emu() + EMU.fromArray(this.items);
		}

		@Override
		public FEMArray compact(boolean index) {
			return this;
		}

		@Override
		public boolean isIndexed() {
			return true;
		}

		@Override
		protected int customFind(FEMValue that, int offset, int length, boolean foreward) {
			var hash = that.hashCode() & (this.table[0] - 2);
			var l = this.table[hash];
			var r = this.table[hash + 1] - 1;
			length += offset;
			if (foreward) {
				for (; l <= r; l++) {
					var result = this.table[l];
					if (length <= result) return -1;
					if ((offset <= result) && that.equals(this.items[result])) return result;
				}
			} else {
				for (; l <= r; r--) {
					var result = this.table[r];
					if (result < offset) return -1;
					if ((result < length) && that.equals(this.items[result])) return result;
				}
			}
			return -1;
		}

		/** Dieses Feld speichert die Streuwerttabelle zu den Werten in {@link #items}. Die Länge der Zahlenfolge entspricht stets einer um 1 sowie um die Länge der
		 * Wertliste {@code length} erhöhten Potenz von {@code 2}. Diese Potenz verringerte um eins wird als Bitmaske für die Streuwerte der Elemente eingesetzt.
		 * Die erste Zahl enthält die Anzahl der Streuwertbereiche und ist gleich der um {@code 2} erhöhten Bitmaske. Die Struktur ist damit
		 * {@code (count[1], range[count-1], index[length])} mit {@code mask = count - 2}. */
		final int[] table;

		CompactArray3(FEMValue[] items) throws IllegalArgumentException {
			super(items);
			var length = this.length;
			var mask = Objects.hashMask(length);
			var count = mask + 2;
			var table = new int[count + length];
			var hashes = new int[length];
			for (var i = 0; i < length; i++) {
				var hash = (items[i].hashCode() & mask) + 1;
				table[hash]++;
				hashes[i] = hash;
			}
			var offset = count;
			for (var i = 1; i < count; i++) {
				var size = table[i];
				table[i] = offset;
				offset += size;
			}
			for (var i = 0; i < length; i++) {
				var hash = hashes[i];
				offset = table[hash];
				table[hash] = offset + 1;
				table[offset] = i;
			}
			table[0] = count;
			this.table = table;
		}

	}

	static class ItemMap implements Map3<FEMValue, FEMValue>, Emuable {

		public final FEMArray keys;

		public final FEMArray values;

		public ItemMap(FEMArray keys, FEMArray values) {
			this.keys = keys;
			this.values = values;
		}

		@Override
		public int size() {
			return this.keys.length;
		}

		@Override
		public boolean isEmpty() {
			return this.size() == 0;
		}

		@Override
		public boolean containsKey(Object key) {
			return this.keys.findFirst(key) >= 0;
		}

		@Override
		public boolean containsValue(Object value) {
			return this.values.findFirst(value) >= 0;
		}

		@Override
		public FEMValue get(Object key) {
			var index = this.keys.findFirst(key);
			return index >= 0 ? this.values.get(index) : null;
		}

		@Override
		public FEMValue put(FEMValue key, FEMValue value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FEMValue remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends FEMValue, ? extends FEMValue> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Keys keySet() {
			return new Keys();
		}

		@Override
		public Values values() {
			return new Values();
		}

		@Override
		public EntrySet entrySet() {
			return new EntrySet();
		}

		@Override
		public int hashCode() {
			return new EntryMap().hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof Map<?, ?>)) return false;
			var that = (Map<?, ?>)object;
			if (that.size() != this.size()) return false;
			for (var entry: that.entrySet()) {
				if (!this.containsEntry(entry)) return false;
			}
			return true;
		}

		public FEMArray toArray() {
			return from(this.keys, this.values);
		}

		@Override
		public String toString() {
			return new EntryMap().toString();
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.fromAll(this.keys, this.values);
		}

		boolean containsEntry(Entry<?, ?> entry) {
			return (entry != null) && this.containsEntry(entry.getKey(), entry.getValue());
		}

		boolean containsEntry(Object key, Object value) {
			if (!(value instanceof FEMValue)) return false;
			var index = this.keys.findFirst(key);
			return (index >= 0) && this.values.get(index).equals(value);
		}

		class Keys extends AbstractSet2<FEMValue> {

			@Override
			public boolean contains(Object o) {
				return ItemMap.this.containsKey(o);
			}

			@Override
			public Iterator3<FEMValue> iterator() {
				return ItemMap.this.keys.iterator();
			}

			@Override
			public int size() {
				return ItemMap.this.keys.length;
			}

		}

		class Values extends AbstractSet2<FEMValue> {

			@Override
			public boolean contains(Object o) {
				return ItemMap.this.containsValue(o);
			}

			@Override
			public Iterator3<FEMValue> iterator() {
				return ItemMap.this.values.iterator();
			}

			@Override
			public int size() {
				return ItemMap.this.values.length;
			}

		}

		class EntrySet extends AbstractSet2<Entry<FEMValue, FEMValue>> {

			@Override
			public boolean contains(Object o) {
				return (o instanceof Entry<?, ?>) && ItemMap.this.containsEntry((Entry<?, ?>)o);
			}

			@Override
			public Iterator3<Entry<FEMValue, FEMValue>> iterator() {
				return new EntryIter();
			}

			@Override
			public int size() {
				return ItemMap.this.keys.length;
			}

		}

		class EntryMap extends AbstractMap<FEMValue, FEMValue> {

			@Override
			public EntrySet entrySet() {
				return new EntrySet();
			}

		}

		class EntryIter implements Iterator3<Entry<FEMValue, FEMValue>> {

			@Override
			public boolean hasNext() {
				return this.keys.hasNext() && this.values.hasNext();
			}

			@Override
			public Entry<FEMValue, FEMValue> next() {
				return new SimpleImmutableEntry<>(this.keys.next(), this.values.next());
			}

			final Iterator<FEMValue> keys = ItemMap.this.keys.iterator();

			final Iterator<FEMValue> values = ItemMap.this.values.iterator();

		}

	}

	static class ItemList extends AbstractList2<FEMValue> implements RandomAccess, Emuable {

		public final FEMArray items;

		public ItemList(FEMArray array) {
			this.items = array;
		}

		@Override
		public FEMValue get(int index) {
			return this.items.get(index);
		}

		@Override
		public int size() {
			return this.items.length;
		}

		@Override
		public Iterator3<FEMValue> iterator() {
			return this.items.iterator();
		}

		@Override
		public boolean contains(Object o) {
			return this.indexOf(o) >= 0;
		}

		@Override
		public int indexOf(Object o) {
			return this.items.findFirst(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return this.items.findLast(o);
		}

		@Override
		public List<FEMValue> subList(int fromIndex, int toIndex) {
			return this.items.section(fromIndex, toIndex - fromIndex).toList();
		}

		@Override
		public long emu() {
			return EMU.fromObject(this) + EMU.from(this.items);
		}

	}

	static class ItemFinder implements Collector {

		public final FEMValue value;

		public int index;

		public ItemFinder(FEMValue value) {
			this.value = value;
		}

		@Override
		public boolean push(FEMValue value) {
			if (this.value.equals(value)) return false;
			this.index++;
			return true;
		}

	}

	static class GetHash implements Collector {

		public int hash = Objects.hashInit();

		@Override
		public boolean push(FEMValue value) {
			this.hash = Objects.hashPush(this.hash, value.hashCode());
			return true;
		}

	}

	static class GetValue implements Collector {

		public final FEMValue[] array;

		public int index;

		public GetValue(FEMValue[] array, int index) {
			this.array = array;
			this.index = index;
		}

		@Override
		public boolean push(FEMValue value) {
			this.array[this.index++] = value;
			return true;
		}

	}

	static class GetUniform implements Collector {

		public FEMValue value;

		public GetUniform(FEMValue value) {
			this.value = value;
		}

		@Override
		public boolean push(FEMValue value) {
			return this.value.equals(value);
		}

	}

}
