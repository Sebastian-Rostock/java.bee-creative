package bee.creative.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Diese Klasse implementiert ein Objekt, das eine Region bzw. einen Bereich eines {@link Array}s beschreibt.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Region {

	/**
	 * Dieses Feld speichert das leere {@link Array}.
	 */
	public static final Object[] VOID_ARRAY = new Object[0];

	/**
	 * Diese Methode gibt die neue Länge für das gegebene {@link Array} zurück, um darin die gegebene Anzahl an Elementen
	 * verwalten zu können. Die Berechnung ist an die in {@link ArrayList} angelehnt.
	 * 
	 * @param list {@link Array}.
	 * @param count Anzahl.
	 * @return Länge.
	 */
	public static int length(final Object[] list, final int count) {
		final int oldLength = list.length;
		if(oldLength >= count) return oldLength;
		final int newLength = oldLength + (oldLength >> 1);
		if(newLength >= count) return newLength;
		return count;
	}

	/**
	 * Diese Methode setzt die Größe des {@link Array}s der gegebenen {@link Region} und gibt eine neue {@link Region}
	 * zurück. Wenn die Größe des gegebenen {@link Array}s von der gegebenen Größe abweicht, werden ein neues
	 * {@link Array} mit passender Größe erzeugt und die Elemente des gegebenen {@link Array}s mittig in das neue
	 * {@link Array} kopiert.
	 * 
	 * @see Region#resize(Object[], int, int, int)
	 * @param region {@link Region}.
	 * @param length neue Größe.
	 * @return neue {@link Region}.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
	 */
	public static Region resize(final Region region, final int length) throws IllegalArgumentException {
		return Region.resize(region.list, region.from, region.size, length);
	}

	/**
	 * Diese Methode setzt die Größe des gegebenen {@link Array}s und gibt eine neue {@link Region} zurück. Wenn die Größe
	 * des gegebenen {@link Array}s von der gegebenen Größe abweicht, werden ein neues {@link Array} mit passender Größe
	 * erzeugt und die Elemente des gegebenen {@link Array}s mittig in das neue {@link Array} kopiert.
	 * 
	 * @param list {@link Array}.
	 * @param from Index des ersten Elements.
	 * @param size Anzahl der Elemente.
	 * @param length neue Größe.
	 * @return neue {@link Region}.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
	 */
	public static Region resize(final Object[] list, final int from, final int size, final int length)
		throws IllegalArgumentException {
		if(size > length) throw new IllegalArgumentException("size > length");
		if(length == 0) return new Region(Region.VOID_ARRAY, 0, 0);
		if(length == list.length) return new Region(list, from, size);
		final Object[] list2 = new Object[length];
		final int from2 = (length - size) / 2;
		System.arraycopy(list, from, list2, from2, size);
		return new Region(list2, from2, size);
	}

	/**
	 * Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das {@link Array} der gegebenen
	 * {@link Region} ein und gibt eine neue {@link Region} zurück. Wenn die Größe des gegebenen {@link Array}s gleich der
	 * gegebenen Größe ist, wird versucht, die wenigen Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl
	 * zu verschieben. Reicht der verfügbare Platz zum Verschieben dieser wenigen Elemente nicht aus, so werden alle
	 * Elemente verschoben und mittig im gegebenen {@link Array} ausgerichtet. Wenn die Größe des gegebenen {@link Array}s
	 * dagegen von der gegebenen Größe abweicht, werden ein neues {@link Array} mit passender Größe erzeugt und die
	 * Elemente des gegebenen {@link Array}s mittig in das neue {@link Array} kopiert.
	 * 
	 * @see Region#insert(Object[], int, int, int, int, int)
	 * @param region {@link Region}.
	 * @param length neue Größe.
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @return neue {@link Region}.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
	 */
	public static Region insert(final Region region, final int length, final int index, final int count) {
		return Region.insert(region.list, region.from, region.size, length, index, count);
	}

	/**
	 * Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das gegebenen {@link Array} ein
	 * und gibt eine neue {@link Region} zurück. Wenn die Größe des gegebenen {@link Array}s gleich der gegebenen Größe
	 * ist, wird versucht, die wenigen Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben.
	 * Reicht der verfügbare Platz zum Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben
	 * und mittig im gegebenen {@link Array} ausgerichtet. Wenn die Größe des gegebenen {@link Array}s dagegen von der
	 * gegebenen Größe abweicht, werden ein neues {@link Array} mit passender Größe erzeugt und die Elemente des gegebenen
	 * {@link Array}s mittig in das neue {@link Array} kopiert.
	 * 
	 * @param list {@link Array}.
	 * @param from Index des ersten Elements.
	 * @param size Anzahl der Elemente.
	 * @param length neue Größe.
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @return neue {@link Region}.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
	 */
	public static Region insert(final Object[] list, final int from, final int size, final int length, final int index,
		final int count) throws IllegalArgumentException {
		final int index2 = index - from;
		if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
		if(count < 0) throw new IllegalArgumentException("count out of range: " + count);
		if(count == 0) return Region.resize(list, from, size, length);
		final int size2 = size + count;
		if(size2 > length) throw new IllegalArgumentException("size + count > length");
		if(length != list.length){
			final Object[] list2 = new Object[length];
			final int from2 = (length - size2) / 2;
			System.arraycopy(list, from, list2, from2, index2);
			System.arraycopy(list, index, list2, from2 + index2 + count, size - index2);
			return new Region(list2, from2, size2);
		}
		if(index2 > (size / 2)){
			if((from + size2) <= length){
				System.arraycopy(list, index, list, index + count, size - index2);
				return new Region(list, from, size2);
			}
			final int from2 = (length - size2) / 2;
			System.arraycopy(list, from, list, from2, index2);
			System.arraycopy(list, index, list, from2 + index2 + count, size - index2);
			final int last = from + size, last2 = from2 + size2;
			if(last2 >= last) return new Region(list, from2, size2);
			Arrays.fill(list, last2, last, null);
			return new Region(list, from2, size2);
		}
		if(from >= count){
			final int from2 = from - count;
			System.arraycopy(list, from, list, from2, index2);
			return new Region(list, from2, size2);
		}
		final int from2 = (length - size2) / 2;
		System.arraycopy(list, index, list, from2 + index2 + count, size - index2);
		System.arraycopy(list, from, list, from2, index2);
		if(from >= from2) return new Region(list, from2, size2);
		Arrays.fill(list, from, from2, null);
		return new Region(list, from2, size2);
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im gegebenen das {@link Array}
	 * der gegebenen {@link Region} und gibt eine neue {@link Region} zurück. Wenn die Größe des gegebenen {@link Array}s
	 * gleich der gegebenen Größe ist, wird versucht, die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich um
	 * die gegebene Anzahl zu verschieben. Wenn die Größe des gegebenen {@link Array}s dagegen von der gegebenen Größe
	 * abweicht, werden ein neues {@link Array} mit passender Größe erzeugt und die Elemente des gegebenen {@link Array}s
	 * mittig in das neue {@link Array} kopiert.
	 * 
	 * @see Region#remove(Object[], int, int, int, int, int)
	 * @param region {@link Region}.
	 * @param length neue Größe.
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @return neue {@link Region}.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
	 */
	public static Region remove(final Region region, final int length, final int index, final int count) {
		return Region.remove(region.list, region.from, region.size, length, index, count);
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im gegebenen {@link Array} und
	 * gibt eine neue {@link Region} zurück. Wenn die Größe des gegebenen {@link Array}s gleich der gegebenen Größe ist,
	 * wird versucht, die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich um die gegebene Anzahl zu
	 * verschieben. Wenn die Größe des gegebenen {@link Array}s dagegen von der gegebenen Größe abweicht, werden ein neues
	 * {@link Array} mit passender Größe erzeugt und die Elemente des gegebenen {@link Array}s mittig in das neue
	 * {@link Array} kopiert.
	 * 
	 * @param list {@link Array}.
	 * @param from Index des ersten Elements.
	 * @param size Anzahl der Elemente.
	 * @param length neue Größe.
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @return neue {@link Region}.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des {@link Array}s führen würden.
	 */
	public static Region remove(final Object[] list, final int size, final int from, final int length, final int index,
		final int count) throws IllegalArgumentException {
		final int index2 = index - from;
		if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
		final int size2 = size - count;
		if((count < 0) || (size2 < 0)) throw new IllegalArgumentException("count out of range: " + count);
		if(count == 0) return Region.resize(list, from, size, length);
		if(size2 > length) throw new IllegalArgumentException("size - count > length");
		if(length == 0) return new Region(Region.VOID_ARRAY, 0, 0);
		if(length != list.length){
			final Object[] list2 = new Object[length];
			final int from2 = (length - size2) / 2;
			System.arraycopy(list, from, list2, from2, index2);
			System.arraycopy(list, index + count, list2, from2 + index2, size2 - index2);
			return new Region(list2, from2, size2);
		}
		if(index2 > (size2 / 2)){
			System.arraycopy(list, index + count, list, index, size2 - index2);
			Arrays.fill(list, from + size2, from + size, null);
			return new Region(list, from, size2);
		}
		int from2 = from + count;
		System.arraycopy(list, from, list, from2, index2);
		Arrays.fill(list, from, from2, null);
		return new Region(list, from2, size2);
	}

	/**
	 * Dieses Feld speichert das {@link Array}.
	 */
	public final Object[] list;

	/**
	 * Dieses Feld speichert den Index des ersten Elements.
	 */
	public final int from;

	/**
	 * Dieses Feld speichert die Anzahl der Elemente.
	 */
	public final int size;

	/**
	 * Dieser Konstrukteur initialisiert die {@link Region}.
	 * 
	 * @param list {@link Array}
	 * @param from Index des ersten Elements.
	 * @param size Anzahl der Elemente.
	 */
	public Region(final Object[] list, final int from, final int size) {
		this.list = list;
		this.from = from;
		this.size = size;
	}

}