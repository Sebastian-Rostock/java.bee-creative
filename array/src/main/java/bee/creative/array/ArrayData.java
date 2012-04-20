package bee.creative.array;

import java.util.Arrays;

/**
 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung dynamischer Arrays.
 * <p>
 * Das Einfügen und Entfernen von Elementen verändern in dieser Implementation nicht nur die Größe des mit den Nutzdaten
 * belegte Bereichs im Array, sondern auch dessen Position.
 * <p>
 * Beim Entfernen von Elementen, werden die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich verschoben.
 * Dadurch vergrößert sich entweder die Größe des Leerraums vor oder die die Größe des Leerraums nach dem
 * Nutzdatenbereich.
 * <p>
 * Beim Einfügen von Elementen wird versucht, die wenigen Elemente vor bzw. nach der Einfügeposition zu verschieben.
 * Reicht der verfügbare Leerraum zum Verschieben dieser wenigen Elemente nicht aus, werden alle Elemente verschoben und
 * im Array neu ausgerichtet.
 * <p>
 * Jenachdem, ob der Nutzdatenbereich am Anfang, in der Mitte oder am Ende des Arrays ausgerichtet wird, wird das
 * häufige Einfügen von Elementen am Ende, in der Mitte bzw. am Anfang beschleunigt. Die Änderung der Größe des Arrays
 * führ in jedem Fall zu einer erneuten Ausrichtung.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]},
 *        {@code long[]}, {@code float[]}, {@code double[]} oder {@code boolean[]}).
 */
public abstract class ArrayData<GArray> {

	/**
	 * Dieses Feld speichert den Index des ersten Elements.
	 */
	protected int from;

	/**
	 * Dieses Feld speichert die Anzahl der Elemente.
	 */
	protected int size;

	/**
	 * Diese Methode gibt die neue Länge für das gegebene Array zurück, um darin die gegebene Anzahl an Elementen
	 * verwalten zu können. Die Berechnung ist an die in {@code ArrayList} angelehnt.
	 * 
	 * @param array Array.
	 * @param count Anzahl.
	 * @return Länge.
	 */
	protected final int defaultLength(final GArray array, final int count) {
		final int oldLength = this.getLength(array);
		if(oldLength >= count) return oldLength;
		final int newLength = oldLength + (oldLength >> 1);
		if(newLength >= count) return newLength;
		return count;
	}

	/**
	 * Diese Methode setzt die Größe des gegebenen Arrays und gibt es zurück. Wenn die Größe des gegebenen Arrays von der
	 * gegebenen Größe abweicht, werden ein neues Array mit passender Größe erzeugt, die Elemente des gegebenen Arrays
	 * werden der Ausrichtung entsprechend in das neue Array kopiert und das neue Array zurück gegeben.
	 * 
	 * @see ArrayData#customAlignment(int)
	 * @param array Array.
	 * @param length neue Größe.
	 * @return (neues) Array.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden.
	 */
	protected final GArray defaultResize(final GArray array, final int length) throws IllegalArgumentException {
		final int size = this.size;
		if(size > length) throw new IllegalArgumentException("size > length");
		if(length == this.getLength(array)) return array;
		final GArray array2 = this.newArray(length);
		final int from2 = this.customAlignment(length - size);
		System.arraycopy(array, this.from, array2, from2, size);
		this.from = from2;
		return array2;
	}

	/**
	 * Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das gegebenen Array ein und gibt
	 * das Array zurück. Wenn die Größe des gegebenen Arrays nicht verändert werden muss, wird versucht, die wenigen
	 * Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben. Reicht der verfügbare Platz zum
	 * Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben und der Ausrichtung entsprechend
	 * im gegebenen Array ausgerichtet. Wenn die Größe des gegebenen Arrays dagegen angepasst werden muss, werden ein
	 * neues Array mit passender Größe erzeugt und die Elemente des gegebenen Arrays der Ausrichtung entsprechend in das
	 * neue Array kopiert. Die benötigte Größe wird via {@link ArrayData#defaultLength(Object, int)} ermittelt.
	 * 
	 * @see ArrayData#customAlignment(int)
	 * @see ArrayData#defaultLength(Object, int)
	 * @param array Array.
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @return (neues) Array.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden.
	 */
	protected final GArray defaultInsert(final GArray array, final int index, final int count)
		throws IllegalArgumentException {
		final int from = this.from;
		final int size = this.size;
		final int index2 = index - from;
		if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
		if(count < 0) throw new IllegalArgumentException("count out of range: " + count);
		if(count == 0) return array;
		final int size2 = size + count;
		final int length = this.defaultLength(array, size2);
		this.size = size2;
		if(length != this.getLength(array)){
			final GArray array2 = this.newArray(length);
			final int from2 = this.customAlignment(length - size2);
			System.arraycopy(array, from, array2, from2, index2);
			System.arraycopy(array, index, array2, from2 + index2 + count, size - index2);
			this.from = from2;
			return array2;
		}
		if(index2 > (size / 2)){
			if((from + size2) <= length){
				System.arraycopy(array, index, array, index + count, size - index2);
				return array;
			}
			final int from2 = this.customAlignment(length - size2);
			this.from = from2;
			System.arraycopy(array, from, array, from2, index2);
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			final int last = from + size, last2 = from2 + size2;
			if(last2 >= last) return array;
			this.clearArray(array, last2, last);
			return array;
		}
		if(from >= count){
			final int from2 = from - count;
			this.from = from2;
			System.arraycopy(array, from, array, from2, index2);
			return array;
		}
		final int from2 = this.customAlignment(length - size2);
		this.from = from2;
		System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
		System.arraycopy(array, from, array, from2, index2);
		if(from >= from2) return array;
		this.clearArray(array, from, from2);
		return array;
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im gegebenen Array und gibt das
	 * Array zurück. Es wird versucht, die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich um die gegebene
	 * Anzahl zu verschieben.
	 * 
	 * @see ArrayData#customAlignment(int)
	 * @param array Array.
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @return (neues) Array.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden.
	 */
	protected final GArray defaultRemove(final GArray array, final int index, final int count)
		throws IllegalArgumentException {
		final int from = this.from;
		final int size = this.size;
		final int index2 = index - from;
		if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
		final int size2 = size - count;
		if((count < 0) || (size2 < 0)) throw new IllegalArgumentException("count out of range: " + count);
		if(count == 0) return array;
		this.size = size2;
		if(size2 == 0){
			this.from = this.customAlignment(this.getLength(array));
			this.clearArray(array, from, from + size);
			return array;
		}
		if(index2 > (size2 / 2)){
			System.arraycopy(array, index + count, array, index, size2 - index2);
			this.clearArray(array, from + size2, from + size);
			return array;
		}
		final int from2 = from + count;
		this.from = from2;
		System.arraycopy(array, from, array, from2, index2);
		this.clearArray(array, from, from2);
		return array;
	}

	/**
	 * Diese Methode gibt die Position zurück, an der die Elemente des Arrays ausgerichtet werden sollen. Diese ergibt
	 * sich aus {@code space / 2}.
	 * 
	 * @param space Anzahl der nicht belegten Elemente.
	 * @return Position zur Ausrichtung.
	 */
	protected final int defaultAlignment(final int space) {
		return space / 2;
	}

	/**
	 * Diese Methode fügt die gegebene Anzahl an Elemente ab dem gegebenen Index in das Array ein.
	 * 
	 * @see ArrayData#defaultInsert(Object, int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
	 */
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		this.setArray(this.defaultInsert(this.getArray(), index, count));
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Elementen ab dem gegebenen Index aus dem Array.
	 * 
	 * @see ArrayData#defaultRemove(Object, int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
	 */
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		this.setArray(this.defaultRemove(this.getArray(), index, count));
	}

	/**
	 * Diese Methode vergrößert die Kapazität des Arrays, sodass dieses die gegebene Anzahl an Elementen verwalten kann.
	 * 
	 * @see ArrayData#defaultResize(Object, int)
	 * @param count Anzahl.
	 */
	protected void customAllocate(final int count) {
		this.setArray(this.defaultResize(this.getArray(), this.defaultLength(this.getArray(), count)));
	}

	/**
	 * Diese Methode verkleinert die Kapazität des Arrays auf das Minimum.
	 * 
	 * @see ArrayData#defaultResize(Object, int)
	 */
	protected void customCompact() {
		this.setArray(this.defaultResize(this.getArray(), this.size));
	}

	/**
	 * Diese Methode gibt die Position zurück, an der die Elemente des Arrays ausgerichtet werden sollen. Bei der
	 * Ausrichtung {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von
	 * Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtung {@code space} gilt das gegenteil, da
	 * hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des
	 * Arrays beschleunigt wird.
	 * 
	 * @see ArrayData#defaultInsert(Object, int, int)
	 * @see ArrayData#defaultRemove(Object, int, int)
	 * @see ArrayData#defaultResize(Object, int)
	 * @see ArrayData#defaultAlignment(int)
	 * @param space Anzahl der nicht belegten Elemente.
	 * @return Position zur Ausrichtung ({@code 0..space}).
	 */
	protected int customAlignment(final int space) {
		return this.defaultAlignment(space);
	}

	/**
	 * Diese Methode gibt das Array zurück.
	 * 
	 * @return Array.
	 */
	protected abstract GArray getArray();

	/**
	 * Diese Methode setzt das Array.
	 * 
	 * @param array Array.
	 */
	protected abstract void setArray(GArray array);

	/**
	 * Diese Methode gibt ein neues Array mit der gegebenen Länge zurück.
	 * 
	 * @param length Länge des neuen Arrays.
	 * @return neues Array.
	 */
	protected abstract GArray newArray(int length);

	/**
	 * Diese Methode leert den gegebenen Bereich im gegebenen Array.
	 * 
	 * @see Arrays#fill(Object[], Object)
	 * @param array Array.
	 * @param startIndex Index des ersten Elements im Bereich.
	 * @param finalIndex Index des ersten Elements nach dem Bereich.
	 */
	protected abstract void clearArray(GArray array, int startIndex, int finalIndex);

	/**
	 * Diese Methode gibt die Länge des gegebenen Arrays zurück.
	 * 
	 * @param array Array.
	 * @return Länge des Arrays.
	 */
	protected abstract int getLength(GArray array);

	/**
	 * Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 * 
	 * @param count Anzahl.
	 */
	public final void allocate(final int count) {
		this.customAllocate(count);
	}

	/**
	 * Diese Methode verkleinert die Kapazität auf das Minimum.
	 */
	public final void compact() {
		this.customCompact();
	}

}