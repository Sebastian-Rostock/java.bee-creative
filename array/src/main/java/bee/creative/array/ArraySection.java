package bee.creative.array;

import java.util.Comparator;

/**
 * Diese Klasse implementiert einen abstrakten Abschnitt eines Arrays. Definiert wird ein Abschnitt für ein Array {@link #array()} der Länge {@link #arrayLength()} mit dem Index des ersten Werts im Abschnitt ({@link #startIndex()}) sowie dem Index des ersten Werts nach dem Abschnitt ({@link #finalIndex()}).
 * <p>
 * Die Methoden {@link #hashCode()}, {@link #equals(Object)} und {@link #toString()} reflektieren die Werte im Abschnitt.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]} oder {@code boolean[]}).
 */
public abstract class ArraySection<GArray> implements Comparable<ArraySection<GArray>> {

	/**
	 * Diese Methode validiert die gegebenen {@link ArraySection} und gibt sie zurück.
	 * 
	 * @param <GSection> Typ der {@link ArraySection}.
	 * @param section {@link ArraySection}.
	 * @return {@link ArraySection}.
	 * @throws NullPointerException Wenn {@code section == null} oder {@code section.array() == null}.
	 * @throws IndexOutOfBoundsException Wenn {@code section.startIndex() < 0} oder {@code section.finalIndex() > section.arrayLength()}.
	 * @throws IllegalArgumentException Wenn {@code section.finalIndex() < section.startIndex()}.
	 */
	public static <GSection extends ArraySection<?>> GSection validate(final GSection section) throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException {
		if(section == null) throw new NullPointerException("section is null");
		if(section.array() == null) throw new NullPointerException("array is null");
		if(section.startIndex() < 0) throw new IndexOutOfBoundsException("startIndex < 0");
		if(section.finalIndex() < section.startIndex()) throw new IllegalArgumentException("finalIndex < startIndex");
		if(section.finalIndex() > section.arrayLength()) throw new IndexOutOfBoundsException("finalIndex > arrayLength");
		return section;
	}

	/**
	 * Diese Methode gibt die Länge des gegebenen Arrays zurück.
	 * 
	 * @see ArraySection#arrayLength()
	 * @param array Array.
	 * @return Länge des gegebenen Arrays.
	 */
	protected abstract int arrayLength(GArray array);

	/**
	 * Diese Methode gibt den {@link Object#hashCode() Streuwert} des {@code index}-ten Werts des gegebenen Arrays zurück und wird in {@link ArraySection#hashCode()} verwendet.
	 * 
	 * @param array Array.
	 * @param index Index.
	 * @return {@link Object#hashCode() Streuwert} des {@code index}-ten Werts.
	 */
	protected abstract int hashCode(GArray array, int index);

	/**
	 * Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der {@code index}-ten Werte der gegebenen Arrays zurück und wird in {@link ArraySection#equals(Object)} verwendet.
	 * 
	 * @param array1 Array 1.
	 * @param array2 Array 2.
	 * @param index1 Index für Array 1.
	 * @param index2 Index für Array 2.
	 * @return {@link Object#equals(Object) Äquivalenz} der {@code index}-ten Werte der gegebenen Arrays.
	 */
	protected abstract boolean equals(GArray array1, GArray array2, int index1, int index2);

	/**
	 * Diese Methode gibt den {@link Comparator#compare(Object, Object) Vergleichswert} der {@code index}-ten Werte der gegebenen Arrays zurück und wird in {@link ArraySection#compareTo(ArraySection)} verwendet.
	 * 
	 * @param array1 Array 1.
	 * @param array2 Array 2.
	 * @param index1 Index für Array 1.
	 * @param index2 Index für Array 2.
	 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der {@code index}-ten Werte der gegebenen Arrays.
	 */
	protected abstract int compareTo(GArray array1, GArray array2, int index1, int index2);

	/**
	 * Diese Methode fügt den {@code index}-ten Wert des gegebenen Arrays an den gegebenen {@link StringBuilder} an und wird in {@link ArraySection#toString()} verwendet.
	 * 
	 * @see StringBuilder#append(char)
	 * @see StringBuilder#append(int)
	 * @see StringBuilder#append(long)
	 * @see StringBuilder#append(float)
	 * @see StringBuilder#append(double)
	 * @see StringBuilder#append(boolean)
	 * @param array Array.
	 * @param index Index.
	 * @param target {@link StringBuilder}.
	 */
	protected abstract void toString(GArray array, int index, StringBuilder target);

	/**
	 * Diese Methode gibt die Anzahl der Werte im Abschnitt zurück.
	 * 
	 * @return Anzahl der Werte im Abschnitt.
	 */
	public int size() {
		return this.finalIndex() - this.startIndex();
	}

	/**
	 * Diese Methode gibt das Array zurück.
	 * 
	 * @return Array.
	 */
	public abstract GArray array();

	/**
	 * Diese Methode gibt die Länge des Arrays zurück.
	 * 
	 * @return Länge des Arrays.
	 */
	public int arrayLength() {
		return this.arrayLength(this.array());
	}

	/**
	 * Diese Methode gibt den Index des ersten Werts im Abschnitt zurück.
	 * 
	 * @return Index des ersten Werts im Abschnitt.
	 */
	public abstract int startIndex();

	/**
	 * Diese Methode gibt den Index des ersten Werts nach dem Abschnitt zurück.
	 * 
	 * @return Index des ersten Werts nach dem Abschnitt.
	 */
	public abstract int finalIndex();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 1;
		final GArray array = this.array();
		for(int index = this.startIndex(), finalIndex = this.finalIndex(); index < finalIndex; index++){
			hash = (31 * hash) + this.hashCode(array, index);
		}
		return hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		@SuppressWarnings ("unchecked")
		final ArraySection<GArray> data = (ArraySection<GArray>)object;
		int index = this.startIndex();
		final int delta = data.startIndex() - index;
		final int finalIndex = this.finalIndex();
		if((finalIndex - index) != (((data.finalIndex() + index) - delta))) return false;
		final GArray array1 = this.array(), array2 = data.array();
		for(; index < finalIndex; index++){
			if(!this.equals(array1, array2, index, index + delta)) return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final ArraySection<GArray> section) {
		final GArray array1 = this.array(), array2 = section.array();
		int startIndex1 = this.startIndex();
		int startIndex2 = section.startIndex();
		final int finalIndex1 = this.finalIndex();
		final int finalIndex2 = section.finalIndex();
		final int size1 = this.finalIndex() - startIndex1;
		final int size2 = section.finalIndex() - startIndex2;
		if(size1 < size2){
			for(final int delta = startIndex2 - startIndex1; startIndex1 < finalIndex1; startIndex1++){
				final int comp = this.compareTo(array1, array2, startIndex1, startIndex1 + delta);
				if(comp != 0) return comp;
			}
			return -1;
		}else{
			for(final int delta = startIndex1 - startIndex2; startIndex2 < finalIndex2; startIndex2++){
				final int comp = this.compareTo(array1, array2, startIndex2 + delta, startIndex2);
				if(comp != 0) return comp;
			}
			if(size1 == size2) return 0;
			return 1;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		int index = this.startIndex();
		final int finalIndex = this.finalIndex();
		if(index == finalIndex) return "[]";
		final StringBuilder builder = new StringBuilder();
		builder.append('[');
		final GArray array = this.array();
		this.toString(array, index++, builder);
		for(; index < finalIndex; index++){
			this.toString(array, index, builder.append(", "));
		}
		return builder.append(']').toString();
	}

}
