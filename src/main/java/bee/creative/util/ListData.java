package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import bee.creative.array.ArrayData;

/*
 * Idee: keys besteht aus partitionen mit je 64 elementen | ein int[] (pages) bildet den suchindex über den partitionen | die elemente im index sowie in den
 * partitionen sind jeweils mittig angeordnet, sodass nur die wenigen vorderen/hinteren elemente beim einfügen verschoben werden müssen | wenn zwischen zwei
 * vollen, vor der vollen ersten oder hinter der vollen letzten partitionen eingefügt werden soll, wird eine neue partition in keys benutzt | die frei
 * gewordenen partitionen müssen im index verwealtet werden | in den keys werden spezielle objekte LO und HI für die leeren zellen am anfang/ende eingesetzt |
 * ähnliches gilt für spezielle zahlen im index | die navigable api könnte umgesetzt werden | di niederen 5 bit der page adresse enthalten die anzahl der
 * unbenutzen zellen einer page| page und index müssen in ihrer mitte stets ein element haben
 */

class ListData<GKey, GValue> {

	{}

	private static final int[] EMPTY_TABLE = {0, 0};

	/** Dieses Feld speichert den initialwert für {@link #keys} und {@link #values}. */
	private static final Object[] EMPTY_OBJECTS = {};

	/** Dieses Feld speichert die maximale Kapazität. */
	private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

	private static final int HI_PAGE = -1;

	private static final int LO_PAGE = -2;

	private static final Object HI_OBJECT = new Object();

	private static final Object LO_OBJECT = new Object();

	{}

	@SuppressWarnings ("javadoc")
	private static void clearObjects(final Object[] array) {
		if (array == null) return;
		Arrays.fill(array, null);
	}

	/** Diese Methode rotiert den gegebenen Abschnitt einer Zahlenliste in die gegebene Richtung.
	 * <p>
	 * TODO Fehlerbehandlung
	 *
	 * @param array Zahlenliste.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @param rotate Anzahl der Elemente, um die rotiert werden soll. Positive Werte rotieren in Richtung der hohen Positionen, negative in Richtung der niedrigen
	 *        Positionen. */
	static void rotate(final int[] array, final int offset, final int length, final int rotate) {
		final int lo, hi;
		if (rotate < 0) {
			lo = -rotate;
			hi = length + rotate;
		} else {
			hi = rotate;
			lo = length - rotate;
		}
		if (lo < hi) {
			if (lo < 0) throw new IllegalArgumentException();
			if (lo == 0) return;

			if (lo == 1) {
				ListData.rotateLo1(array, offset, length);
			}
			if (lo == 2) {
				ListData.rotateLo2(array, offset, length);
			}
		} else {
			if (hi < 0) throw new IllegalArgumentException();
			if (hi == 0) return;

		}

	}

	static void rotateLo1(final int[] array, final int offset, final int length) {
		final int count = length - 1;
		final int item = array[offset];
		System.arraycopy(array, offset + 1, array, offset, count);
		array[offset + count] = item;
	}

	static void rotateLo2(final int[] array, final int offset, final int length) {
		final int count = length - 2;
		final int item = array[offset], item2 = array[offset + 1];
		System.arraycopy(array, offset + 2, array, offset, count);
		array[offset + count] = item;
		array[offset + count + 1] = item2;
	}

	static void rotateLoN(final int[] array, final int startOffset, final int finalIndex, final int count) {

		// [123][45678] -(rot 3)-> [45678][123]

		// bsp 5
		// 123456789
		// 623451789
		// 673451289
		// 678451239
		// 678951234

	}

	{}

	// an positionen [0, 1] steht der von-bis-bereich, der in "pages" mit verweisen gefüllt ist
	// an positionen [n, n + 1] steht der von-bis-bereich, der in der "n/64"-ten Page in "keys" belegt ist
	// unbeutzte von-bis-bereiche zeigen auf die Mitte einer Seite
	int[] table = ListData.EMPTY_TABLE;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Schlüssel ab. eine leere page enthält im ersten element einen integer, der auf die nächste freie
	 * seite zeigt Für alle anderen Indizes bildet es auf {@code null} ab. */
	Object[] keys = ListData.EMPTY_OBJECTS;

	/** Dieses Feld bildet vom Index eines Eintrags auf dessen Wert ab. Für alle anderen Indizes bildet es auf {@code null} ab. */
	Object[] values;

	/** Dieses Feld speichert die Anzahl der Einträge ab {@link #empty}. */
	int count = 0;

	public ListData(final boolean withValues) {
		this.values = withValues ? ListData.EMPTY_OBJECTS : null;
	}

	{}

	/** Diese Methode gibt die Anzahl der Einträge zurück.
	 *
	 * @return Anzahl der Einträge. */
	protected final int OKAY_countImpl() {
		return this.count;
	}

	/** Diese Methode gibt die Anzahl der Einträge zurück, die ohne erneuter Speicherreervierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	protected final int OKAY_capacityImpl() {
		return this.keys.length;
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Einträgen ist. */
	protected final void allocateImpl(final int capacity) throws IllegalArgumentException {
		final int count = this.count;
		if (capacity < count) throw new IllegalArgumentException();
		final Object[] oldKeys = this.keys;
		if (oldKeys.length == capacity) return;
		if (capacity == 0) {
			this.table = ListData.EMPTY_TABLE;
			this.keys = ListData.EMPTY_OBJECTS;
			this.values = this.values != null ? ListData.EMPTY_OBJECTS : null;
		} else if (capacity <= ListData.MAX_CAPACITY) {
			// TODO

		} else throw new OutOfMemoryError();
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Map#containsKey(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean OKAY_hasKeyImpl(final Object key) {
		return 0 <= this.getIndexImpl(key);
	}

	/** Diese Methode sucht einen Eintrag mit dem gegebenen Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Map#containsValue(Object)
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean hasValueImpl(final Object value) {
		final Object[] values = this.values;
		for (int lo = this.empty, hi = lo + this.count; lo < hi; lo++) {
			if (Objects.equals(values[lo], value)) return true;
		}
		return false;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel sowie Wert und gibt nur dann {@code true} zurück, wenn ein solcher Eintrag existiert.
	 *
	 * @see Set#contains(Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefundenen wurde. */
	protected final boolean OKAY_hasEntryImpl(final Object key, final Object value) {
		final int index = this.getIndexImpl(key);
		return (index >= 0) && Objects.equals(this.values[index], value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Wert zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#get(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return Wert des gefundenen Eintrags oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue OKAY_getImpl(final Object key) {
		final int index = this.getIndexImpl(key);
		if (index < 0) return null;
		return (GValue)this.values[index];
	}

	/** Diese Methode gibt den Schlüssel des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Schlüssel oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GKey OKAY_getKeyImpl(final int entryIndex) {
		return (GKey)this.keys[entryIndex];
	}

	/** Diese Methode gibt den Wert des gegebenen Eintrags zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @return Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue OKAY_getValueImpl(final int entryIndex) {
		return (GValue)this.values[entryIndex];
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code -1} geliefert.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen Eintrags oder {@code -1}. */
	protected final int getIndexImpl(final Object key) {
		final int[] table = this.table;
		final Object[] keys = this.keys;
		// 1) Binäre Suche nach der passenden Bereich
		int minIndex = table[0], maxIndex = table[1];
		while (minIndex != maxIndex) {
			final int midIndex = (minIndex + maxIndex) >>> 1;
			// 1.1) Abgleich des Letzten Elements einer Page, sodass "minIndex" die Page nennt, in welcher der Treffer enthalten sein muss
			final int result = table[(midIndex << 1) + 1] - 1;
			final int this2that = this.customCompare(keys[result], key);
			if (this2that > 0) {
				maxIndex = midIndex;
			} else if (this2that < 0) {
				minIndex = midIndex + 1;
			} else return result;
		}
		// 2) Binäre Suche nach dem passenden Element
		final int minRange = table[minIndex], maxRange = table[minIndex + 1];
		int minItem = minRange, maxItem = maxRange;
		while (minItem != maxItem) {
			final int result = (minItem + maxItem) >>> 1;
			final int this2that = this.customCompare(keys[result], key);
			if (this2that > 0) {
				maxItem = result;
			} else if (this2that > 0) {
				minItem = result + 1;
			} else return result;
		}
		return -1;
	}

	/** Diese Methode gibt den Index des ersten Elements zurück. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#first()
	 * @return Index des ersten Elements. */
	protected final int getFirstIndex() {
		if (this.count == 0) return -1;
		final int[] table = this.table;
		return table[table[0] << 1];
	}

	/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die Werte {@code -1} und {@code from+size}
	 * annehmen.
	 *
	 * @see NavigableSet#lower(Object)
	 * @param key Element.
	 * @return Index des größten Elements, dass kleiner dem gegebenen ist. */
	protected final int getLowerIndex(final Object key) {
		final int index = this.getIndexImpl(key);
		if (index < 0) return -index - 2;
		return index - 1;
	}

	/** Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index kann die Werte {@code -1} und
	 * {@code from+size} annehmen.
	 *
	 * @see NavigableSet#floor(Object)
	 * @param key Element.
	 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist. */
	protected final int getFloorIndex(final Object key) {
		final int index = this.getIndexImpl(key);
		if (index < 0) return -index - 2;
		return index;
	}

	/** Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#ceiling(Object)
	 * @param key Element.
	 * @return Index des kleinsten Elements, dass größer oder gleich dem gegebenen ist. */
	protected final int getCeilingIndex(final Object key) {
		final int index = this.getIndexImpl(key);
		if (index < 0) return -index - 1;
		return index;
	}

	/** Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den Wert {@code size} annehmen.
	 *
	 * @see NavigableSet#higher(Object)
	 * @param key Element.
	 * @return Index des kleinsten Elements, dass größer dem gegebenen ist. */
	protected final int getHigherIndex(final Object key) {
		final int index = this.getIndexImpl(key);
		if (index < 0) return -index - 1;
		return index + 1;
	}

	/** Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann den Wert {@code -1} annehmen.
	 *
	 * @see NavigableSet#last()
	 * @return Index des letzten Elements. */
	protected final int getIastIndex() {
		if (this.count == 0) return -1;
		final int[] table = this.table;
		return table[(table[1] << 1) - 1] - 1;
	}

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index >= size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index >= size}). */
	protected final int getInclusiveIndex(final int index) throws IndexOutOfBoundsException {
		// if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		// if (index >= this.size) throw new IndexOutOfBoundsException("index >= size()");
		// return this.from + index;
		return 0;
	}

	/** Diese Methode gibt die gegebenen Position als Index des internen Arrays zurück. Wenn ({@code index < 0} oder {@code index > size}) wird eine
	 * {@link IndexOutOfBoundsException} ausgelöst.
	 *
	 * @param index Position.
	 * @return Index des internen Array ({@code index + from}).
	 * @throws IndexOutOfBoundsException Wenn die gegebene Position ungültig ist ({@code index < 0} oder {@code index > size}). */
	protected final int getExclusiveIndex(final int index) throws IndexOutOfBoundsException {
		// if (index < 0) throw new IndexOutOfBoundsException("index < 0");
		// if (index > this.count) throw new IndexOutOfBoundsException("index > size");
		// return this.entry + index;
		return 0;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel, setzt dessen Wert und gibt seinen vorherigen Wert zurück.<br>
	 * Wenn kein solcher Eintrag existierte, wird {@code null} geliefert.
	 *
	 * @see Map#put(Object, Object)
	 * @param key Schlüssel des Eintrags.
	 * @param value neuer Wert des Eintrags.
	 * @return alert Wert des gefundenen Eintrags oder {@code null}. */
	protected final GValue putImpl(final GKey key, final GValue value) {
		final int index = this.putIndexImpl(key);
		return this.putValueImpl(index, value);
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt nur dann {@code false} zurück, wenn ein solcher Eintrag existiert. Wenn kein solcher
	 * Eintrag existierte, wird er erzeugt und {@code true} geliefert.
	 *
	 * @see Set#add(Object)
	 * @param key Schlüssel des Eintrags.
	 * @return {@code true}, wenn der Eintrag erzeugt wurde. */
	protected final boolean putKeyImpl(final GKey key) {
		final int count = this.count;
		this.putIndexImpl(key);
		return count != this.count;
	}

	/** Diese Methode ersetzt den Wert des gegebenen Eintrags und gibt den vorherigen zurück.
	 *
	 * @param entryIndex Index eines Eintrags.
	 * @param value neuer Wert oder {@code null}.
	 * @return alter Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue putValueImpl(final int entryIndex, final GValue value) {
		final Object[] values = this.values;
		final Object result = values[entryIndex];
		values[entryIndex] = value;
		return (GValue)result;
	}

	/** Diese Methode sucht den Eintrag mit dem gegebenen Schlüssel und gibt dessen Position zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird er erzeugt.
	 *
	 * @param key Schlüssel des Eintrags.
	 * @return Index des gefundenen oder erzeugten Eintrags. */
	protected final int putIndexImpl(final GKey key) {
		// TODO
		// final int result = this.getIndexImpl(key);
		// if (result >= 0) return result;
		// final int count = this.count + 1, capacity = this.capacityImpl();
		// if (count <= capacity) return this.putIndexImpl2(key);
		// final int allocate = count + (count >> 1);
		// this.allocateImpl((allocate < 0) || (allocate > ListData.MAX_CAPACITY) ? ListData.MAX_CAPACITY : allocate);
		// return this.putIndexImpl2(key);
		return 0;
	}

	/** Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das interne Array ein. Wenn die Größe des internen Arrays nicht verändert
	 * werden muss, wird versucht, die wenigen Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben. Reicht der verfügbare Platz zum
	 * Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben und der Ausrichtung entsprechend im internen Array ausgerichtet. Wenn die
	 * Größe des internen Arrays dagegen angepasst werden muss, werden ein neues Array mit passender Größe erzeugt und die Elemente des internen Arrays der
	 * Ausrichtung entsprechend in das neue Array kopiert. Die benötigte Größe wird via {@link ArrayData#customNewCapacity(int)} ermittelt.
	 *
	 * @see ArrayData#customNewFrom(int)
	 * @see ArrayData#customNewCapacity(int)
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		// TODO
		// final int from = this.from;
		// final int index2 = index - from;
		// if (index2 < 0) throw new IllegalArgumentException("index < from");
		// final int size = this.size;
		// if (index2 > size) throw new IllegalArgumentException("index > from + size");
		// if (count == 0) return;
		// if (count < 0) throw new IllegalArgumentException("count < 0");
		// final int size2 = size + count;
		// final GArray array = this.customGetArray();
		// final int arrayLength = this.customGetCapacity();
		// final int array2Length = this.customNewCapacity(size2);
		// this.size = size2;
		// if (arrayLength != array2Length) {
		// final int from2 = this.customNewFrom(array2Length - size2);
		// final GArray array2 = this.customNewArray(array2Length);
		// System.arraycopy(array, from, array2, from2, index2);
		// System.arraycopy(array, index, array2, from2 + index2 + count, size - index2);
		// this.from = from2;
		// this.customSetArray(array2);
		// return;
		// }
		// if (index2 > (size / 2)) {
		// if ((from + size2) <= array2Length) {
		// System.arraycopy(array, index, array, index + count, size - index2);
		// return;
		// }
		// } else {
		// if (from >= count) {
		// final int from2 = from - count;
		// this.from = from2;
		// System.arraycopy(array, from, array, from2, index2);
		// return;
		// }
		// }
		// final int from2 = this.customNewFrom(array2Length - size2);
		// this.from = from2;
		// if (from2 < from) {
		// System.arraycopy(array, from, array, from2, index2);
		// System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
		// final int last = from + size, last2 = from2 + size2;
		// if (last2 < last) {
		// this.customClearArray(last2, last);
		// }
		// } else {
		// System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
		// System.arraycopy(array, from, array, from2, index2);
		// if (from2 > from) {
		// this.customClearArray(from, from2);
		// }
		// }
	}

	/** Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im internen Array. Es wird versucht, die wenigen Elemente vor bzw. nach
	 * dem zu entfernenden Bereich um die gegebene Anzahl zu verschieben.
	 *
	 * @see ArrayData#customNewFrom(int)
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden. */
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		// TODO
		// final int from = this.from;
		// final int index2 = index - from;
		// if (index2 < 0) throw new IllegalArgumentException("index < from");
		// final int size = this.size;
		// if (index2 > size) throw new IllegalArgumentException("index > from + size");
		// if (count == 0) return;
		// if (count < 0) throw new IllegalArgumentException("count < 0");
		// final int size2 = size - count;
		// if (size2 < 0) throw new IllegalArgumentException("count > size");
		// final GArray array = this.customGetArray();
		// this.size = size2;
		// if (size2 == 0) {
		// this.from = this.customNewFrom(this.customGetCapacity());
		// this.customClearArray(from, from + size);
		// } else if (index2 > (size2 / 2)) {
		// System.arraycopy(array, index + count, array, index, size2 - index2);
		// this.customClearArray(from + size2, from + size);
		// } else {
		// final int from2 = from + count;
		// this.from = from2;
		// System.arraycopy(array, from, array, from2, index2);
		// this.customClearArray(from, from2);
		// }
	}

	/** Diese Methode entfernt den Eintrag mit dem gegebenen Schlüssel und gibt den Wert des Eintrags zurück.<br>
	 * Wenn kein solcher Eintrag existiert, wird {@code null} geliefert.
	 *
	 * @see Map#remove(Object)
	 * @param key Schlüssel.
	 * @return Wert oder {@code null}. */
	@SuppressWarnings ("unchecked")
	protected final GValue popImpl(final Object key) {
		final int entry = this.getIndexImpl(key);
		if (entry < 0) return null;
		final Object result = this.values[entry];
		this.popIndexImpl(entry);
		return (GValue)result;
	}

	/** Diese Methode entfernt den gegeben Schlüssel und liefet nur dann {@code true}, wenn dieser zuvor über {@link #putKeyImpl(Object)} hinzugefügt wurde.
	 *
	 * @see Set#remove(Object)
	 * @param key Schlüssel.
	 * @return {@code true}, wenn der Eintrag existierte. */
	protected final boolean popKeyImpl(final Object key) {
		final int entry = this.getIndexImpl(key);
		if (entry < 0) return false;
		this.popIndexImpl(entry);
		return true;
	}

	/** Diese Methode entfernt einen Eintrag mit dem gegebenen Wert.
	 *
	 * @see Map#containsValue(Object)
	 * @param value Wert des Eintrags.
	 * @return {@code true}, wenn der Eintrag gefunden und entfernt wurde. */
	protected final boolean popValueImpl(final Object value) {
		// TODO
		return false;
	}

	boolean popEntryImpl(final Object key, final Object value) {
		// TODO
		return false;
	}

	final void popIndexImpl(final int index) {

	}

	final void popRangeImpl(final int lo, final int hi) {

	}

	/** Diese Methode gibt das {@link Set} der Schlüssel in {@link #keys} zurück.
	 *
	 * @return Schlüssel. */
	protected final Keys<GKey> newKeysAImpl() {
		return new KeysA<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Schlüssel in {@link #keys} zurück.
	 *
	 * @return Interator für {@link #newKeysAImpl()}. */
	protected final KeysIteratorA<GKey, GValue> newKeysIteratorAImpl() {
		return new KeysIteratorA<>(this);
	}

	/** Diese Methode gibt die {@link Collection} der Werte in {@link #values} zurück.
	 *
	 * @return Werte. */
	protected final Values<GValue> newValuesAImpl() {
		return new Values<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Werte in {@link #values} zurück.
	 *
	 * @return Interator für {@link #newValuesAImpl()}. */
	protected final ValuesIteratorA<GKey, GValue> newValuesIteratorImpl() {
		return new ValuesIteratorA<>(this);
	}

	/** Diese Methode gibt das {@link Set} der Einträge zurück.
	 *
	 * @return Einträge. */
	protected final Entries<GKey, GValue> newEntriesAImpl() {
		return new Entries<>(this);
	}

	/** Diese Methode gibt den {@link Iterator} über die Einträge zurück.
	 *
	 * @return Interator für {@link #newEntriesAImpl()}. */
	protected final EntriesIteratorA<GKey, GValue> newEntriesIteratorImpl() {
		return new EntriesIteratorA<>(this);
	}

	/** Diese Methode gibt die {@link Map} zu den Schlüsseln in {@link #keys} und Werten in {@link #values} zurück.
	 *
	 * @return Abbildung. */
	protected final Mapping<GKey, GValue> newMappingImpl() {
		return new Mapping<>(this);
	}

	/** Diese Methode entfernt alle Einträge. Hierbei werden die Anzahl der Einträge auf {@code 0} gesetzt und die Tabellen initialisiert. */
	protected final void clearImpl() {
		if (this.count == 0) return;
		ListData.clearObjects(this.keys);
		ListData.clearObjects(this.values);
		this.count = 0;
		// TODO
	}

	/** Diese Methode gibt eine Zahl kleiner als, gleich oder größer als {@code 0} zurück, wenn der erste Schlüssel kleienr als, gleich bzw. größer als der zweite
	 * Schlüssel ist.
	 *
	 * @see Comparators#compare(Comparable, Comparable)
	 * @param thisKey alter bzw. verwalteter Schlüssel oder {@code null}.
	 * @param thatKey neuer bzw. gesuchter Schlüssel oder {@code null}.
	 * @return Vergleichswert der gegebenen Schlüssel. */
	@SuppressWarnings ({"unchecked", "rawtypes"})
	protected int customCompare(final Object thisKey, final Object thatKey) {
		return Comparators.compare((Comparable)thisKey, (Comparable)thatKey);
	}

	{}
	{}
	{}

	@SuppressWarnings ("javadoc")
	protected static class TreeEntry<GKey, GValue> implements Entry<GKey, GValue> {

		protected final ListData<GKey, GValue> entryData;

		protected final int entryIndex;

		public TreeEntry(final ListData<GKey, GValue> entryData, final int entryIndex) {
			this.entryData = entryData;
			this.entryIndex = entryIndex;
		}

		{}

		@Override
		public GKey getKey() {
			return this.entryData.OKAY_getKeyImpl(this.entryIndex);
		}

		@Override
		public GValue getValue() {
			return this.entryData.OKAY_getValueImpl(this.entryIndex);
		}

		@Override
		public GValue setValue(final GValue value) {
			return this.entryData.putValueImpl(this.entryIndex, value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
		}

		@Override
		public boolean equals(final Object object) {
			if (!(object instanceof Entry)) return false;
			final Entry<?, ?> that = (Entry<?, ?>)object;
			return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getValue(), that.getValue());
		}

		@Override
		public String toString() {
			return this.getKey() + "=" + this.getValue();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static abstract class TreeIterator<GKey, GValue, GItem> implements Iterator<GItem> {

		protected final ListData<GKey, GValue> entryData;

		/** Dieses Feld speichert den Index des nächsten Eintrags in {@link HashData#keys}. */
		protected int nodeIndex;

		/** Dieses Feld speichert den Index des aktuellen Eintrags in {@link HashData#keys}. */
		protected int entryIndex = -1;

		public TreeIterator(final ListData<GKey, GValue> entryData) {
			this.entryData = entryData;
		}

		/** Diese Methode gibt den vorherigen Schlüssel zurück. */
		protected final GKey prevKey() {
			return this.entryData.OKAY_getKeyImpl(this.prevIndex());
		}

		/** Diese Methode gibt den vorherigen Eintrag zurück. */
		protected final TreeEntry<GKey, GValue> prevEntry() {
			return new TreeEntry<>(this.entryData, this.prevIndex());
		}

		/** Diese Methode gibt den vorherigen Wert zurück. */
		protected final GValue prevValue() {
			return this.entryData.OKAY_getValueImpl(this.prevIndex());
		}

		/** Diese Methode ermitteln den Index des vorherigen Eintrags und gibt den des aktuellen zurück. */
		protected final int prevIndex() {
			// TODO
			return 0;
		}

		protected final int prevNode() {
			// TODO
			return 0;
		}

		protected final void prevRemove() {
			throw new UnsupportedOperationException();
		}

		/** Diese Methode gibt den nächsten Schlüssel zurück. */
		protected final GKey nextKey() {
			return this.entryData.OKAY_getKeyImpl(this.nextIndex());
		}

		/** Diese Methode gibt den nächsten Eintrag zurück. */
		protected final TreeEntry<GKey, GValue> nextEntry() {
			return new TreeEntry<>(this.entryData, this.nextIndex());
		}

		/** Diese Methode gibt den nächsten Wert zurück. */
		protected final GValue nextValue() {
			return this.entryData.OKAY_getValueImpl(this.nextIndex());
		}

		/** Diese Methode ermitteln den Index des nächsten Eintrags und gibt den des aktuellen zurück. */
		protected final int nextIndex() {
			// TODO
			return 0;
		}

		protected final int nextNode() {
			// TODO
			return 0;
		}

		protected final void nextRemove() {
			throw new UnsupportedOperationException();
		}

		{}

		@Override
		public boolean hasNext() {
			return this.nodeIndex >= 0;
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class Keys<GKey> extends AbstractSet<GKey> implements NavigableSet<GKey> {

		protected final ListData<GKey, ?> entryData;

		public Keys(final ListData<GKey, ?> entryData) {
			this.entryData = entryData;
		}

		{}

		@Override
		public int size() {
			return this.entryData.count;
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public Iterator<GKey> iterator() {
			return this.entryData.newKeysIteratorAImpl();
		}

		@Override
		public boolean remove(final Object item) {
			return this.entryData.popKeyImpl(item);
		}

		@Override
		public boolean contains(final Object item) {
			return this.entryData.OKAY_hasKeyImpl(item);
		}

		@Override
		public Comparator<? super GKey> comparator() {
			return null;
		}

		@Override
		public GKey first() {
			return null;
		}

		@Override
		public GKey last() {
			return null;
		}

		@Override
		public GKey lower(final GKey e) {
			return null;
		}

		@Override
		public GKey floor(final GKey e) {
			return null;
		}

		@Override
		public GKey ceiling(final GKey e) {
			return null;
		}

		@Override
		public GKey higher(final GKey e) {
			return null;
		}

		@Override
		public GKey pollFirst() {
			return null;
		}

		@Override
		public GKey pollLast() {
			return null;
		}

		@Override
		public NavigableSet<GKey> descendingSet() {
			return null;
		}

		@Override
		public Iterator<GKey> descendingIterator() {
			return null;
		}

		@Override
		public NavigableSet<GKey> subSet(final GKey fromElement, final boolean fromInclusive, final GKey toElement, final boolean toInclusive) {
			return null;
		}

		@Override
		public NavigableSet<GKey> headSet(final GKey toElement, final boolean inclusive) {
			return null;
		}

		@Override
		public NavigableSet<GKey> tailSet(final GKey fromElement, final boolean inclusive) {
			return null;
		}

		@Override
		public SortedSet<GKey> subSet(final GKey fromElement, final GKey toElement) {
			return null;
		}

		@Override
		public SortedSet<GKey> headSet(final GKey toElement) {
			return null;
		}

		@Override
		public SortedSet<GKey> tailSet(final GKey fromElement) {
			return null;
		}

	}

	protected static class KeysA<GKey> extends Keys<GKey> {

		public KeysA(final ListData<GKey, ?> entryData) {
			super(entryData);
		}

	}

	protected static class KeysD<GKey> extends Keys<GKey> {

		public KeysD(final ListData<GKey, ?> entryData) {
			super(entryData);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class KeysIteratorA<GKey, GValue> extends TreeIterator<GKey, GValue, GKey> {

		public KeysIteratorA(final ListData<GKey, GValue> entryData) {
			super(entryData);
		}

		{}

		@Override
		public GKey next() {
			return this.nextKey();
		}

		@Override
		public void remove() {
			this.nextRemove();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class KeysIteratorD<GKey, GValue> extends TreeIterator<GKey, GValue, GKey> {

		public KeysIteratorD(final ListData<GKey, GValue> entryData) {
			super(entryData);
		}

		{}

		@Override
		public GKey next() {
			return this.prevKey();
		}

		@Override
		public void remove() {
			this.prevRemove();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class Values<GValue> extends AbstractCollection<GValue> {

		protected final ListData<?, GValue> entryData;

		public Values(final ListData<?, GValue> entryData) {
			this.entryData = entryData;
		}

		{}

		@Override
		public int size() {
			return this.entryData.count;
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public Iterator<GValue> iterator() {
			return this.entryData.newValuesIteratorImpl();
		}

		@Override
		public boolean remove(final Object o) {
			return this.entryData.popValueImpl(o);
		}

		@Override
		public boolean contains(final Object o) {
			return this.entryData.hasValueImpl(o);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class ValuesIteratorA<GKey, GValue> extends TreeIterator<GKey, GValue, GValue> {

		public ValuesIteratorA(final ListData<GKey, GValue> entryData) {
			super(entryData);
		}

		{}

		@Override
		public GValue next() {
			return this.nextValue();
		}

		@Override
		public void remove() {
			this.nextRemove();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class Entries<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

		protected final ListData<GKey, GValue> entryData;

		public Entries(final ListData<GKey, GValue> entryData) {
			this.entryData = entryData;
		}

		{}

		@Override
		public int size() {
			return this.entryData.count;
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public Iterator<Entry<GKey, GValue>> iterator() {
			return this.entryData.newEntriesIteratorImpl();
		}

		@Override
		public boolean remove(final Object object) {
			if (!(object instanceof Entry)) return false;
			final Entry<?, ?> entry = (Entry<?, ?>)object;
			return this.entryData.popEntryImpl(entry.getKey(), entry.getValue());
		}

		@Override
		public boolean contains(final Object object) {
			if (!(object instanceof Entry)) return false;
			final Entry<?, ?> entry = (Entry<?, ?>)object;
			return this.entryData.OKAY_hasEntryImpl(entry.getKey(), entry.getValue());
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class EntriesIteratorA<GKey, GValue> extends TreeIterator<GKey, GValue, Entry<GKey, GValue>> {

		public EntriesIteratorA(final ListData<GKey, GValue> entryData) {
			super(entryData);
		}

		{}

		@Override
		public Entry<GKey, GValue> next() {
			return this.nextEntry();
		}

		@Override
		public void remove() {
			this.nextRemove();
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class Mapping<GKey, GValue> extends AbstractMap<GKey, GValue> implements NavigableMap<GKey, GValue> {

		protected final ListData<GKey, GValue> entryData;

		public Mapping(final ListData<GKey, GValue> entryData) {
			this.entryData = entryData;
		}

		{}

		@Override
		public int size() {
			return this.entryData.count;
		}

		@Override
		public void clear() {
			this.entryData.clearImpl();
		}

		@Override
		public boolean isEmpty() {
			return this.entryData.count == 0;
		}

		@Override
		public boolean containsKey(final Object key) {
			return this.entryData.OKAY_hasKeyImpl(key);
		}

		@Override
		public boolean containsValue(final Object value) {
			return this.values().contains(value);
		}

		@Override
		public GValue get(final Object key) {
			return this.entryData.OKAY_getImpl(key);
		}

		@Override
		public Set<GKey> keySet() {
			return this.entryData.newKeysAImpl();
		}

		@Override
		public GValue put(final GKey key, final GValue value) {
			return this.entryData.putImpl(key, value);
		}

		@Override
		public GValue remove(final Object key) {
			return this.entryData.popImpl(key);
		}

		@Override
		public Values<GValue> values() {
			return this.entryData.newValuesAImpl();
		}

		@Override
		public Entries<GKey, GValue> entrySet() {
			return this.entryData.newEntriesAImpl();
		}

		@Override
		public Comparator<? super GKey> comparator() {
			return null;
		}

		@Override
		public GKey firstKey() {
			return null;
		}

		@Override
		public GKey lastKey() {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> lowerEntry(final GKey key) {
			return null;
		}

		@Override
		public GKey lowerKey(final GKey key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> floorEntry(final GKey key) {
			return null;
		}

		@Override
		public GKey floorKey(final GKey key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> ceilingEntry(final GKey key) {
			return null;
		}

		@Override
		public GKey ceilingKey(final GKey key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> higherEntry(final GKey key) {
			return null;
		}

		@Override
		public GKey higherKey(final GKey key) {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> firstEntry() {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> lastEntry() {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> pollFirstEntry() {
			return null;
		}

		@Override
		public java.util.Map.Entry<GKey, GValue> pollLastEntry() {
			return null;
		}

		@Override
		public NavigableMap<GKey, GValue> descendingMap() {
			return null;
		}

		@Override
		public NavigableSet<GKey> navigableKeySet() {
			return null;
		}

		@Override
		public NavigableSet<GKey> descendingKeySet() {
			return null;
		}

		@Override
		public NavigableMap<GKey, GValue> subMap(final GKey fromKey, final boolean fromInclusive, final GKey toKey, final boolean toInclusive) {
			return null;
		}

		@Override
		public NavigableMap<GKey, GValue> headMap(final GKey toKey, final boolean inclusive) {
			return null;
		}

		@Override
		public NavigableMap<GKey, GValue> tailMap(final GKey fromKey, final boolean inclusive) {
			return null;
		}

		@Override
		public SortedMap<GKey, GValue> subMap(final GKey fromKey, final GKey toKey) {
			return null;
		}

		@Override
		public SortedMap<GKey, GValue> headMap(final GKey toKey) {
			return null;
		}

		@Override
		public SortedMap<GKey, GValue> tailMap(final GKey fromKey) {
			return null;
		}

	}

	{}

	/** {@inheritDoc} */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			final ListData<?, ?> result = (ListData<?, ?>)super.clone();
			if (this.OKAY_capacityImpl() == 0) {
				result.keys = ListData.EMPTY_OBJECTS;
				result.values = result.values != null ? ListData.EMPTY_OBJECTS : null;
			} else {
				result.keys = this.keys.clone();
				result.values = result.values == null ? null : this.values.clone();
			}
			return result;
		} catch (final Exception cause) {
			throw new CloneNotSupportedException(cause.getMessage());
		}
	}

}
