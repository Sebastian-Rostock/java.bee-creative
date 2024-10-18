package bee.creative.array;

import java.util.Comparator;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen abstrakten Abschnitt eines Arrays. Definiert wird ein Abschnitt für ein Array {@link #array()} der Länge
 * {@link #arrayLength()} mit dem Index des ersten Werts im Abschnitt ({@link #startIndex()}) sowie dem Index des ersten Werts nach dem Abschnitt (
 * {@link #finalIndex()}).
 * <p>
 * Die Methoden {@link #hashCode()}, {@link #equals(Object)} und {@link #toString()} reflektieren die Elemente im Abschnitt.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]} oder
 *        {@code boolean[]}). */
public abstract class ArraySection<GArray> implements Comparable<ArraySection<GArray>> {

	/** Diese Methode validiert die gegebenen {@link ArraySection} und gibt sie zurück.
	 *
	 * @param <GSection> Typ der {@link ArraySection}.
	 * @param section {@link ArraySection}.
	 * @return {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}. */
	public static <GSection extends ArraySection<?>> GSection validate(GSection section)
		throws NullPointerException, IndexOutOfBoundsException, IllegalArgumentException {
		Objects.notNull(section.array());
		if (section.startIndex() < 0) throw new IndexOutOfBoundsException("startIndex < 0");
		if (section.finalIndex() < section.startIndex()) throw new IllegalArgumentException("finalIndex < startIndex");
		if (section.finalIndex() > section.arrayLength()) throw new IndexOutOfBoundsException("finalIndex > arrayLength");
		return section;
	}

	/** Diese Methode implementiert {@link #equals(Object)} ohne {@code null}-Prüfung. Sie sollte von Nachfahren in {@link #equals(Object)} aufgerufen werden.
	 *
	 * @param data Abschnitt.
	 * @return {@code true}, wenn dieser Abschnitt gleich dem das gegebenen ist. */
	protected final boolean defaultEquals(ArraySection<GArray> data) {
		var index = this.startIndex();
		var delta = data.startIndex() - index;
		var finalIndex = this.finalIndex();
		if ((finalIndex - index) != (((data.finalIndex() - index) - delta))) return false;
		var array1 = this.array();
		var array2 = data.array();
		while (index < finalIndex) {
			if (!this.customEquals(array1, array2, index, index + delta)) return false;
			index++;
		}
		return true;
	}

	/** Diese Methode gibt die Länge des gegebenen Arrays zurück.
	 *
	 * @see ArraySection#arrayLength()
	 * @param array Array.
	 * @return Länge des gegebenen Arrays. */
	protected abstract int customLength(GArray array);

	/** Diese Methode gibt den {@link Object#hashCode() Streuwert} des {@code index}-ten Elements des gegebenen Arrays zurück und wird in
	 * {@link ArraySection#hashCode()} verwendet.
	 *
	 * @param array Array.
	 * @param index Index.
	 * @return {@link Object#hashCode() Streuwert} des {@code index}-ten Elements. */
	protected abstract int customHash(GArray array, int index);

	/** Diese Methode fügt das {@code index}-ten Element des gegebenen Arrays an den gegebenen {@link StringBuilder} an und wird in
	 * {@link ArraySection#toString()} verwendet.
	 *
	 * @see StringBuilder#append(char)
	 * @see StringBuilder#append(int)
	 * @see StringBuilder#append(long)
	 * @see StringBuilder#append(float)
	 * @see StringBuilder#append(double)
	 * @see StringBuilder#append(boolean)
	 * @param array Array.
	 * @param index Index.
	 * @param target {@link StringBuilder}. */
	protected abstract void customPrint(GArray array, int index, StringBuilder target);

	/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der {@code index}-ten Elemente der gegebenen Arrays zurück und wird in
	 * {@link ArraySection#equals(Object)} verwendet.
	 *
	 * @param array1 Array 1.
	 * @param array2 Array 2.
	 * @param index1 Index für Array 1.
	 * @param index2 Index für Array 2.
	 * @return {@link Object#equals(Object) Äquivalenz} der {@code index}-ten Elemente der gegebenen Arrays. */
	protected abstract boolean customEquals(GArray array1, GArray array2, int index1, int index2);

	/** Diese Methode gibt den {@link Comparator#compare(Object, Object) Vergleichswert} der {@code index}-ten Elemente der gegebenen Arrays zurück und wird in
	 * {@link ArraySection#compareTo(ArraySection)} verwendet.
	 *
	 * @param array1 Array 1.
	 * @param array2 Array 2.
	 * @param index1 Index für Array 1.
	 * @param index2 Index für Array 2.
	 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der {@code index}-ten Elemente der gegebenen Arrays. */
	protected abstract int customCompare(GArray array1, GArray array2, int index1, int index2);

	/** Diese Methode gibt die Anzahl der Elemente im Abschnitt zurück.
	 *
	 * @return Anzahl der Elemente im Abschnitt. */
	public int size() {
		return this.finalIndex() - this.startIndex();
	}

	/** Diese Methode gibt das Array zurück.
	 *
	 * @return Array. */
	public abstract GArray array();

	/** Diese Methode gibt die Länge des Arrays zurück.
	 *
	 * @return Länge des Arrays. */
	public int arrayLength() {
		return this.customLength(this.array());
	}

	/** Diese Methode gibt den Index des ersten Elements im Abschnitt zurück.
	 *
	 * @return Index des ersten Elements im Abschnitt. */
	public abstract int startIndex();

	/** Diese Methode gibt den Index des ersten Elements nach dem Abschnitt zurück.
	 *
	 * @return Index des ersten Elements nach dem Abschnitt. */
	public abstract int finalIndex();

	@Override
	public int compareTo(ArraySection<GArray> section) {
		var array1 = this.array();
		var array2 = section.array();
		var startIndex1 = this.startIndex();
		var startIndex2 = section.startIndex();
		var finalIndex1 = this.finalIndex();
		var finalIndex2 = section.finalIndex();
		var size1 = this.finalIndex() - startIndex1;
		var size2 = section.finalIndex() - startIndex2;
		if (size1 < size2) {
			for (var delta = startIndex2 - startIndex1; startIndex1 < finalIndex1; startIndex1++) {
				var comp = this.customCompare(array1, array2, startIndex1, startIndex1 + delta);
				if (comp != 0) return comp;
			}
			return -1;
		} else {
			for (var delta = startIndex1 - startIndex2; startIndex2 < finalIndex2; startIndex2++) {
				var comp = this.customCompare(array1, array2, startIndex2 + delta, startIndex2);
				if (comp != 0) return comp;
			}
			if (size1 == size2) return 0;
			return 1;
		}
	}

	@Override
	public int hashCode() {
		var hash = 1;
		var array = this.array();
		var finalIndex = this.finalIndex();
		for (var index = this.startIndex(); index < finalIndex; index++) {
			hash = (31 * hash) + this.customHash(array, index);
		}
		return hash;
	}

	@Override
	public String toString() {
		var index = this.startIndex();
		var finalIndex = this.finalIndex();
		if (index == finalIndex) return "[]";
		var builder = new StringBuilder();
		builder.append('[');
		var array = this.array();
		this.customPrint(array, index++, builder);
		for (; index < finalIndex; index++) {
			this.customPrint(array, index, builder.append(", "));
		}
		return builder.append(']').toString();
	}

}
