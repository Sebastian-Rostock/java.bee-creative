package bee.creative.array;

import java.util.Comparator;
import java.util.RandomAccess;
import bee.creative.lang.Array2;
import bee.creative.lang.Objects;
import bee.creative.util.AbstractList2;
import bee.creative.util.List2;

/** Diese Klasse implementiert einen abstrakten Abschnitt eines nativen Arrays. Die Methoden {@link #compareTo(ArraySection)}, {@link #hashCode()},
 * {@link #equals(Object)} und {@link #toString()} reflektieren die Elemente im Abschnitt.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]} oder
 *        {@code boolean[]}).
 * @param <GValue> Typ der Werte (z.B. {@link Byte}, {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double} oder
 *        {@link Boolean}). */
public abstract class ArraySection<GArray, GValue> implements Array2<GValue>, Comparable<ArraySection<GArray, GValue>> {

	@Override
	public final GValue get(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > this.length())) throw new IndexOutOfBoundsException(index);
		return this.customGet(this.array(), this.offset() + index);
	}

	/** Diese Methode füllt die gegebene {@link ArraySection} mit den Werten ab der gegebenen Position.
	 *
	 * @param index Position.
	 * @param values {@link ArraySection}.
	 * @throws NullPointerException Wenn die gegebene {@link ArraySection} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public final void getAll(int index, ArraySection<? super GArray, ? super GValue> values) throws NullPointerException, IndexOutOfBoundsException {
		var length = values.length();
		if ((index < 0) || ((index + length) > this.length())) throw new IndexOutOfBoundsException(index);
		System.arraycopy(this.array(), this.offset() + index, values.array(), values.offset(), length);
	}

	/** Diese Methode setzt das {@code index}-te Element. */
	public final void set(int index, GValue value) throws IndexOutOfBoundsException {
		if ((index < 0) || (index > this.length())) throw new IndexOutOfBoundsException(index);
		this.customSet(this.array(), this.offset() + index, value);
	}

	/** Diese Methode kopiert die Werte der gegebenen {@link ArraySection} an die gegebene Position.
	 *
	 * @param index Position.
	 * @param values {@link ArraySection}.
	 * @throws NullPointerException Wenn die gegebene {@link ArraySection} {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index + values.size() > size()}). */
	public final void setAll(int index, ArraySection<? extends GArray, ? extends GValue> values) throws NullPointerException, IndexOutOfBoundsException {
		var length = values.length();
		if ((index < 0) || ((index + length) > this.length())) throw new IndexOutOfBoundsException(index);
		System.arraycopy(values.array(), values.offset(), this.array(), this.offset() + index, length);
	}

	@Override
	public final int size() {
		return this.length();
	}

	/** Diese Methode liefert das native Array.
	 *
	 * @return Array. */
	public abstract GArray array();

	/** Diese Methode liefert den Index des ersten Elements im Abschnitt.
	 *
	 * @return Index des ersten Elements im Abschnitt. */
	public abstract int offset();

	/** Diese Methode liefert die Anzahl der Elemente im Abschnitt.
	 *
	 * @return Anzahl der Elemente im Abschnitt. */
	public abstract int length();

	/** Diese Methode liefert die {@link List2}-Sicht auf die Elemente in diesem Abschnitt. . */
	public final List2<GValue> values() {
		return new Values<>(this);
	}

	public abstract ArraySection<GArray, GValue> section(int offset, int length) throws IllegalArgumentException;

	@Override
	public final int compareTo(ArraySection<GArray, GValue> that) {
		var thisArray = this.array();
		var thatArray = that.array();
		var thisOffset = this.offset();
		var thatOffset = that.offset();
		var thisLength = this.length();
		var thatLength = that.length();
		if (thisLength < thatLength) {
			for (var i = 0; i < thisLength; i++) {
				var result = this.customCompare(thisArray, thatArray, thisOffset + i, thatOffset + i);
				if (result != 0) return result;
			}
			return -1;
		} else {
			for (var i = 0; i < thatLength; i++) {
				var result = this.customCompare(thisArray, thatArray, thisOffset + i, thatOffset + i);
				if (result != 0) return result;
			}
			return thisLength == thatLength ? 0 : 1;
		}
	}

	@Override
	public final int hashCode() {
		var array = this.array();
		var length = this.length();
		var offset = this.offset();
		var result = Objects.hashInit();
		for (var i = 0; i < length; i++) {
			result = Objects.hashPush(result, this.customHash(array, offset + i));
		}
		return result;
	}

	@Override
	public final String toString() {
		var length = this.length();
		if (length == 0) return "[]";
		var array = this.array();
		var offset = this.offset();
		var result = new StringBuilder();
		result.append('[');
		this.customPrint(array, offset + 0, result);
		for (var i = 1; i < length; i++) {
			this.customPrint(array, offset + i, result.append(", "));
		}
		return result.append(']').toString();
	}

	protected static final void checkSection(int offset, int length, int capacity) throws IllegalArgumentException {
		if (((offset | length | capacity) < 0) || ((offset + length) > capacity)) throw new IllegalArgumentException();

	}

	protected final void checkSection(int offset, int length) throws IllegalArgumentException {
		if (((offset | length) < 0) || ((offset + length) > this.length())) throw new IllegalArgumentException();
	}

	/** Diese Methode implementiert {@link #equals(Object)} ohne {@code null}-Prüfung. Sie sollte von Nachfahren in {@link #equals(Object)} aufgerufen werden.
	 *
	 * @param that Abschnitt.
	 * @return {@code true}, wenn dieser Abschnitt gleich dem das gegebenen ist. */
	protected final boolean defaultEquals(ArraySection<GArray, GValue> that) {
		var length = this.length();
		if (length != that.length()) return false;
		var thisArray = this.array();
		var thatArray = that.array();
		var thisOffset = this.offset();
		var thatOffset = that.offset();
		for (var i = 0; i < length; i++) {
			if (!this.customEquals(thisArray, thatArray, thisOffset + i, thatOffset + i)) return false;
			thisOffset++;
		}
		return true;
	}

	/** Diese Methode liefert das {@code index}-te Element des gegebenen Arrays.
	 *
	 * @param array Array.
	 * @param index Index.
	 * @return {@code index}-tes Element. */
	protected abstract GValue customGet(GArray array, int index);

	/** Diese Methode setzt das {@code index}-te Element des gegebenen Arrays.
	 *
	 * @param array Array.
	 * @param index Index.
	 * @param value {@code index}-tes Element. */
	protected abstract void customSet(GArray array, int index, GValue value);

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
	 * @param result {@link StringBuilder}. */
	protected abstract void customPrint(GArray array, int index, StringBuilder result);

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

	static class Values<GValue> extends AbstractList2<GValue> implements RandomAccess {

		@Override
		public GValue get(int index) {
			return this.section.get(index);
		}

		@Override
		public GValue set(int index, GValue item) {
			var result = this.section.get(index);
			this.section.set(index, item);
			return result;
		}

		@Override
		public int size() {
			return this.section.size();
		}

		Values(ArraySection<?, GValue> section) {
			this.section = section;
		}

		final ArraySection<?, GValue> section;

	}

}
