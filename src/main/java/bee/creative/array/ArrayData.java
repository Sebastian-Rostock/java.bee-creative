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

	/** Dieses Feld speichert den Index des ersten Elements. */
	protected int _from_;

	/** Dieses Feld speichert die Anzahl der Elemente. */
	protected int _size_;

	{}

	/** Diese Methode gibt das interne Array zurück.
	 *
	 * @return internes Array. */
	protected abstract GArray _array_();

	/** Diese Methode setzt das interne Array.
	 *
	 * @param array Array. */
	protected abstract void _array_(GArray array);

	/** Diese Methode gibt ein neues Array mit der gegebenen Länge zurück.
	 *
	 * @param length Länge des neuen Arrays.
	 * @return neues Array. */
	protected abstract GArray _allocArray_(int length);

	/** Diese Methode leert den gegebenen Bereich im internen Array. Dies ist sinnvoll für Arrays von Objekten.
	 *
	 * @param startIndex Index des ersten Elements im Bereich.
	 * @param finalIndex Index des ersten Elements nach dem Bereich. */
	protected void _clearArray_(final int startIndex, final int finalIndex) {
	}

	/** Diese Methode gibt die Länge des Arrays zurück.
	 *
	 * @return Länge des Arrays. */
	protected abstract int _capacity_();

	/** Diese Methode gibt die Position zurück, an der die Elemente des internen Arrays ausgerichtet werden sollen. Bei der Ausrichtung {@code 0} werden die
	 * Elemente am Anfang des internen Arrays ausgerichtet, wodurch das häufige Einfügen von Elementen am Ende des internen Arrays beschleunigt wird. Für die
	 * relative Ausrichtung {@code space} gilt das gegenteil, da hier die Elemente am Ende des internen Arrays ausgerichtet werden, wodurch das häufige Einfügen
	 * von Elementen am Anfang des internen Arrays beschleunigt wird. Diese ergibt sich aus {@code space / 2}.
	 *
	 * @see ArrayData#_insert_(int, int)
	 * @see ArrayData#_remove_(int, int)
	 * @see ArrayData#_resize_(int)
	 * @param space Anzahl der nicht belegten Elemente.
	 * @return Position zur Ausrichtung ({@code 0..space}). */
	protected int _calcAlign_(final int space) {
		return space / 2;
	}

	/** Diese Methode gibt die neue Kapazität für das interne Array zurück, um darin die gegebene Anzahl an Elementen verwalten zu können.
	 *
	 * @param count Anzahl.
	 * @return Länge. */
	protected int _calcLength_(final int count) {
		final int oldLength = this._capacity_();
		if (oldLength >= count) return oldLength;
		final int newLength = oldLength + (oldLength >> 1);
		if (newLength >= count) return newLength;
		return count;
	}

	/** Diese Methode setzt die Größe des internen Arrays. Wenn die Größe des Arrays von der gegebenen Größe abweicht, werden ein neues Array mit passender Größe
	 * erzeugt, die Elemente der Ausrichtung entsprechend in das neue Array kopiert und das neue Array als internes genutzt.
	 *
	 * @see ArrayData#_calcAlign_(int)
	 * @param length neue Größe.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void _resize_(final int length) throws IllegalArgumentException {
		final int size = this._size_;
		if (size > length) throw new IllegalArgumentException("size > length");
		final int from2 = this._calcAlign_(length - size);
		if (length != this._capacity_()) {
			final GArray array = this._array_();
			final GArray array2 = this._allocArray_(length);
			System.arraycopy(array, this._from_, array2, from2, size);
			this._array_(array2);
		}
		this._from_ = from2;
	}

	/** Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das interne Array ein. Wenn die Größe des internen Arrays nicht verändert
	 * werden muss, wird versucht, die wenigen Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben. Reicht der verfügbare Platz zum
	 * Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben und der Ausrichtung entsprechend im internen Array ausgerichtet. Wenn die
	 * Größe des internen Arrays dagegen angepasst werden muss, werden ein neues Array mit passender Größe erzeugt und die Elemente des internen Arrays der
	 * Ausrichtung entsprechend in das neue Array kopiert. Die benötigte Größe wird via {@link ArrayData#_calcLength_(int)} ermittelt.
	 *
	 * @see ArrayData#_calcAlign_(int)
	 * @see ArrayData#_calcLength_(int)
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void _insert_(final int index, final int count) throws IllegalArgumentException {
		final int from = this._from_;
		final int index2 = index - from;
		if (index2 < 0) throw new IllegalArgumentException("index < from");
		final int size = this._size_;
		if (index2 > size) throw new IllegalArgumentException("index > from + size");
		if (count == 0) return;
		if (count < 0) throw new IllegalArgumentException("count < 0");
		final int size2 = size + count;
		final GArray array = this._array_();
		final int arrayLength = this._capacity_();
		final int array2Length = this._calcLength_(size2);
		this._size_ = size2;
		if (arrayLength != array2Length) {
			final int from2 = this._calcAlign_(array2Length - size2);
			final GArray array2 = this._allocArray_(array2Length);
			System.arraycopy(array, from, array2, from2, index2);
			System.arraycopy(array, index, array2, from2 + index2 + count, size - index2);
			this._from_ = from2;
			this._array_(array2);
			return;
		}
		if (index2 > (size / 2)) {
			if ((from + size2) <= array2Length) {
				System.arraycopy(array, index, array, index + count, size - index2);
				return;
			}
		} else {
			if (from >= count) {
				final int from2 = from - count;
				this._from_ = from2;
				System.arraycopy(array, from, array, from2, index2);
				return;
			}
		}
		final int from2 = this._calcAlign_(array2Length - size2);
		this._from_ = from2;
		if (from2 < from) {
			System.arraycopy(array, from, array, from2, index2);
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			final int last = from + size, last2 = from2 + size2;
			if (last2 < last) {
				this._clearArray_(last2, last);
			}
		} else {
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			System.arraycopy(array, from, array, from2, index2);
			if (from2 > from) {
				this._clearArray_(from, from2);
			}
		}
	}

	/** Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im internen Array. Es wird versucht, die wenigen Elemente vor bzw. nach
	 * dem zu entfernenden Bereich um die gegebene Anzahl zu verschieben.
	 *
	 * @see ArrayData#_calcAlign_(int)
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void _remove_(final int index, final int count) throws IllegalArgumentException {
		final int from = this._from_;
		final int index2 = index - from;
		if (index2 < 0) throw new IllegalArgumentException("index < from");
		final int size = this._size_;
		if (index2 > size) throw new IllegalArgumentException("index > from + size");
		if (count == 0) return;
		if (count < 0) throw new IllegalArgumentException("count < 0");
		final int size2 = size - count;
		if (size2 < 0) throw new IllegalArgumentException("count > size");
		final GArray array = this._array_();
		this._size_ = size2;
		if (size2 == 0) {
			this._from_ = this._calcAlign_(this._capacity_());
			this._clearArray_(from, from + size);
		} else if (index2 > (size2 / 2)) {
			System.arraycopy(array, index + count, array, index, size2 - index2);
			this._clearArray_(from + size2, from + size);
		} else {
			final int from2 = from + count;
			this._from_ = from2;
			System.arraycopy(array, from, array, from2, index2);
			this._clearArray_(from, from2);
		}
	}

	/** Diese Methode gibt die Anzahl der Elementen zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public final int capacity() {
		return this._capacity_();
	}

	/** Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @param capacity Anzahl.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist. */
	public final void allocate(final int capacity) throws IllegalArgumentException {
		if (capacity < 0) throw new IllegalArgumentException("capacity < 0");
		this._resize_(this._calcLength_(capacity));
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum. */
	public final void compact() {
		this._resize_(this._size_);
	}

}