package bee.creative.array;

/** Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung dynamischer Arrays.
 * <p>
 * Das Einfügen und Entfernen von Elementen verändern in dieser Implementation nicht nur die Größe des mit den Nutzdaten belegten Bereichs im internen Array,
 * sondern auch dessen Position.
 * <p>
 * Beim Entfernen von Elementen, werden die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich verschoben. Dadurch vergrößert sich entweder die Größe
 * des Leerraums vor oder die Größe des Leerraums nach dem Nutzdatenbereich. Reicht der verfügbare Leerraum zum Verschieben dieser wenigen Elemente nicht aus,
 * werden alle Elemente verschoben und im internen Array neu ausgerichtet.
 * <p>
 * Jenachdem, ob der Nutzdatenbereich am Anfang, in der Mitte oder am Ende des internen Arrays ausgerichtet wird, wird das häufige Einfügen von Elementen am
 * Ende, in der Mitte bzw. am Anfang beschleunigt. Die Änderung der Größe des internen Arrays führ in jedem Fall zu einer erneuten Ausrichtung.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GArray> Typ des internen Arrays ({@code byte[]}, {@code char[]}, {@code short[]}, {@code int[]}, {@code long[]}, {@code float[]}, {@code double[]}
 *        oder {@code boolean[]}). */
public abstract class ArrayData<GArray> {

	/** Diese Methode gibt die Anzahl der Elementen zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public final int capacity() {
		return this.customGetCapacity();
	}

	/** Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public final void allocate(int capacity) throws IllegalArgumentException {
		if (capacity < 0) throw new IllegalArgumentException("capacity < 0");
		this.customSetCapacity(this.customNewCapacity(capacity));
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public final void compact() {
		this.customSetCapacity(this.size);
	}

	/** Dieses Feld speichert den Index des ersten Elements. */
	protected int from;

	/** Dieses Feld speichert die Anzahl der Elemente. */
	protected int size;

	/** Diese Methode gibt die Position zurück, an der die Elemente des internen Arrays ausgerichtet werden sollen. Bei der Ausrichtung {@code 0} werden die
	 * Elemente am Anfang des internen Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des internen Arrays beschleunigt wird. Für die
	 * relative Ausrichtung {@code space} gilt das gegenteil, da hier die Elemente am Ende des internen Arrays ausgerichtet werden, wodurch das häufige Einfügen
	 * von Elementen am Anfang des internen Arrays beschleunigt wird. Diese ergibt sich aus {@code space / 2}.
	 *
	 * @see ArrayData#customInsert(int, int)
	 * @see ArrayData#customRemove(int, int)
	 * @see ArrayData#customSetCapacity(int)
	 * @param space Anzahl der nicht belegten Elemente.
	 * @return Position zur Ausrichtung ({@code 0..space}). */
	protected int customNewFrom(int space) {
		return space / 2;
	}

	/** Diese Methode gibt ein neues Array mit der gegebenen Länge zurück.
	 *
	 * @param length Länge des neuen Arrays.
	 * @return neues Array. */
	protected abstract GArray customNewArray(int length);

	/** Diese Methode gibt das interne Array zurück.
	 *
	 * @return internes Array. */
	protected abstract GArray customGetArray();

	/** Diese Methode setzt das interne Array.
	 *
	 * @param array Array. */
	protected abstract void customSetArray(GArray array);

	/** Diese Methode leert den gegebenen Bereich im internen Array. Dies ist sinnvoll für Arrays von Objekten.
	 *
	 * @param startIndex Index des ersten Elements im Bereich.
	 * @param finalIndex Index des ersten Elements nach dem Bereich. */
	protected void customClearArray(int startIndex, int finalIndex) {
	}

	/** Diese Methode gibt die Länge des Arrays zurück.
	 *
	 * @return Länge des Arrays. */
	protected abstract int customGetCapacity();

	/** Diese Methode setzt die Größe des internen Arrays. Wenn die Größe des Arrays von der gegebenen Größe abweicht, werden ein neues Array mit passender Größe
	 * erzeugt, die Elemente der Ausrichtung entsprechend in das neue Array kopiert und das neue Array als internes genutzt.
	 *
	 * @see ArrayData#customNewFrom(int)
	 * @param length neue Größe.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void customSetCapacity(int length) throws IllegalArgumentException {
		var size = this.size;
		if (size > length) throw new IllegalArgumentException("size > length");
		var from2 = this.customNewFrom(length - size);
		if (length != this.customGetCapacity()) {
			var array = this.customGetArray();
			var array2 = this.customNewArray(length);
			System.arraycopy(array, this.from, array2, from2, size);
			this.customSetArray(array2);
		}
		this.from = from2;
	}

	/** Diese Methode gibt die neue Kapazität für das interne Array zurück, um darin die gegebene Anzahl an Elementen verwalten zu können.
	 *
	 * @param count Anzahl.
	 * @return Länge. */
	protected int customNewCapacity(int count) {
		var oldLength = this.customGetCapacity();
		if (oldLength >= count) return oldLength;
		var newLength = oldLength + (oldLength >> 1);
		if (newLength >= count) return newLength;
		return count;
	}

	/** Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das interne Array ein. Wenn die Größe des internen Arrays nicht verändert
	 * werden muss, wird versucht, die wenigen Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben. Reicht der verfügbare Platz zum
	 * Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben und der Ausrichtung entsprechend im internen Array ausgerichtet. Wenn die
	 * Größe des internen Arrays dagegen angepasst werden muss, werden ein neues Array mit passender Größe erzeugt und die Elemente des internen Arrays der
	 * Ausrichtung entsprechend in das neue Array kopiert. Die benötigte Größe wird über {@link ArrayData#customNewCapacity(int)} ermittelt.
	 *
	 * @see ArrayData#customNewFrom(int)
	 * @see ArrayData#customNewCapacity(int)
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void customInsert(int index, int count) throws IllegalArgumentException {
		var from = this.from;
		var index2 = index - from;
		if (index2 < 0) throw new IllegalArgumentException("index < from");
		var size = this.size;
		if (index2 > size) throw new IllegalArgumentException("index > from + size");
		if (count == 0) return;
		if (count < 0) throw new IllegalArgumentException("count < 0");
		var size2 = size + count;
		var array = this.customGetArray();
		var arrayLength = this.customGetCapacity();
		var array2Length = this.customNewCapacity(size2);
		this.size = size2;
		if (arrayLength != array2Length) {
			var from2 = this.customNewFrom(array2Length - size2);
			var array2 = this.customNewArray(array2Length);
			System.arraycopy(array, from, array2, from2, index2);
			System.arraycopy(array, index, array2, from2 + index2 + count, size - index2);
			this.from = from2;
			this.customSetArray(array2);
			return;
		}
		if (index2 > (size / 2)) {
			if ((from + size2) <= array2Length) {
				System.arraycopy(array, index, array, index + count, size - index2);
				return;
			}
		} else {
			if (from >= count) {
				var from2 = from - count;
				this.from = from2;
				System.arraycopy(array, from, array, from2, index2);
				return;
			}
		}
		var from2 = this.customNewFrom(array2Length - size2);
		this.from = from2;
		if (from2 < from) {
			System.arraycopy(array, from, array, from2, index2);
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			var last = from + size;
			var last2 = from2 + size2;
			if (last2 < last) {
				this.customClearArray(last2, last);
			}
		} else {
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			System.arraycopy(array, from, array, from2, index2);
			if (from2 > from) {
				this.customClearArray(from, from2);
			}
		}
	}

	/** Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im internen Array. Es wird versucht, die wenigen Elemente vor bzw. nach
	 * dem zu entfernenden Bereich um die gegebene Anzahl zu verschieben.
	 *
	 * @see ArrayData#customNewFrom(int)
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		var from = this.from;
		var index2 = index - from;
		if (index2 < 0) throw new IllegalArgumentException("index < from");
		var size = this.size;
		if (index2 > size) throw new IllegalArgumentException("index > from + size");
		if (count == 0) return;
		if (count < 0) throw new IllegalArgumentException("count < 0");
		var size2 = size - count;
		if (size2 < 0) throw new IllegalArgumentException("count > size");
		var array = this.customGetArray();
		this.size = size2;
		if (size2 == 0) {
			this.from = this.customNewFrom(this.customGetCapacity());
			this.customClearArray(from, from + size);
		} else if (index2 > (size2 / 2)) {
			System.arraycopy(array, index + count, array, index, size2 - index2);
			this.customClearArray(from + size2, from + size);
		} else {
			var from2 = from + count;
			this.from = from2;
			System.arraycopy(array, from, array, from2, index2);
			this.customClearArray(from, from2);
		}
	}

}