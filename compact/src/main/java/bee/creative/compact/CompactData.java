package bee.creative.compact;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

/**
 * Diese Klasse implementiert eine abstrakte Sammlung von Elementen, die in einem (sortierten) Array verwaltet werden.
 * <p>
 * Das Einfügen und Entfernen von Elementen verändern in dieser Implementation nicht nur die Größe des mit den Nutzdaten
 * belegte Bereichs im Array, sondern auch dessen Position.
 * <p>
 * Beim Entfernen von Elementen, werden die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich verschoben.
 * Dadurch vergrößert sich entweder die Größe des Leerraums vor oder die die Größe des Leerraums nach dem
 * Nutzdatenbereich. Reicht der verfügbare Leerraum zum Verschieben dieser wenigen Elemente nicht aus, werden alle
 * Elemente verschoben und im Array neu ausgerichtet.
 * <p>
 * Jenachdem, ob der Nutzdatenbereich am Anfang, in der Mitte oder am Ende des Arrays ausgerichtet wird, wird das
 * häufige Einfügen von Elementen am Ende, in der Mitte bzw. am Anfang beschleunigt. Die Änderung der Größe des Arrays
 * führ in jedem Fall zu einer erneuten Ausrichtung.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class CompactData {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt mit {@link CompactData}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	protected static abstract class CompactDataOwner<GData extends CompactData> {

		/**
		 * Dieses Feld speichert die {@link CompactData}.
		 */
		protected final GData data;

		/**
		 * Dieser Konstrukteur initialisiert die {@link CompactData}.
		 * 
		 * @param data {@link CompactData}.
		 */
		public CompactDataOwner(final GData data) {
			if(data == null) throw new NullPointerException("data is null");
			this.data = data;
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link CompactData}-{@link Iterator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	protected static abstract class CompactDataIterator<GItem, GData extends CompactData> extends
		CompactData.CompactDataOwner<GData> implements Iterator<GItem> {

		/**
		 * Dieses Feld speichert den Index des ersten Elements (inklusiv).
		 */
		protected int from;

		/**
		 * Dieses Feld speichert den Index des aktuellen Elements.
		 */
		protected int item;

		/**
		 * Dieses Feld speichert den Index des letztem Elements (exklusiv).
		 */
		protected int last;

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactData} und Indizes.
		 * 
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactDataIterator(final GData data, final int from, final int last) {
			super(data);
			this.from = from;
			this.item = -1;
			this.last = last;
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 */
		protected abstract GItem next(int index);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.from < this.last;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			final int item = this.item;
			if(item < 0) throw new IllegalStateException();
			int move = this.data.from;
			this.data.customRemove(item, 1);
			move = this.data.from - move;
			this.from += move;
			this.item = -1;
			this.last += move;
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten aufsteigenden {@link CompactData}-{@link Iterator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	public static abstract class CompactDataAscendingIterator<GItem, GData extends CompactData> extends
		CompactData.CompactDataIterator<GItem, GData> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactData} und Indizes.
		 * 
		 * @param data {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactDataAscendingIterator(final GData data, final int from, final int last) {
			super(data, from - 1, last - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem next() {
			return this.next(this.item = (this.from = this.from + 1));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			super.remove();
			this.from--;
			this.last--;
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten absteigenden {@link CompactData}-{@link Iterator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	public static abstract class CompactDataDescendingIterator<GItem, GData extends CompactData> extends
		CompactData.CompactDataIterator<GItem, GData> {

		/**
		 * Dieser Konstrukteur initialisiert {@link CompactData} und Indizes.
		 * 
		 * @param array {@link CompactData}.
		 * @param from Index des ersten Elements (inklusiv).
		 * @param last Index des letzten Elements (exklusiv).
		 */
		public CompactDataDescendingIterator(final GData array, final int from, final int last) {
			super(array, from, last);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem next() {
			return this.next(this.item = (this.last = this.last - 1));
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte Teilmenge eines {@link CompactData}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des {@link CompactData}s.
	 */
	public static abstract class CompactSubData<GData extends CompactData> extends CompactData.CompactDataOwner<GData> {

		/**
		 * Dieses Feld speichert das Objekt zur offenen Begrenzung von Teilmengen.
		 */
		protected static final Object OPEN = new Object();

		/**
		 * Dieses Feld speichert das erste Element oder {@link CompactSubData#OPEN}.
		 */
		protected final Object fromItem;

		/**
		 * Dieses Feld speichert {@code true}, wenn das erste Element inklusiv ist.
		 */
		protected final boolean fromInclusive;

		/**
		 * Dieses Feld speichert das letzte Element oder {@link CompactSubData#OPEN}.
		 */
		protected final Object lastItem;

		/**
		 * Dieses Feld speichert {@code true}, wenn das letzte Element inklusiv ist.
		 */
		protected final boolean lastInclusive;

		/**
		 * Dieser Konstrukteur initialisiert das {@link CompactData} und die Grenzen und deren Inklusion.
		 * 
		 * @param data {@link CompactData}.
		 * @param fromItem erstes Element oder {@link CompactSubData#OPEN}.
		 * @param fromInclusive Inklusivität des ersten Elements.
		 * @param lastItem letztes Element oder {@link CompactSubData#OPEN}.
		 * @param lastInclusive Inklusivität des letzten Elements.
		 * @throws IllegalArgumentException Wenn das gegebene erste Element größer als das gegebene letzte Element ist.
		 */
		public CompactSubData(final GData data, final Object fromItem, final boolean fromInclusive, final Object lastItem,
			final boolean lastInclusive) throws IllegalArgumentException {
			super(data);
			if(fromItem != CompactSubData.OPEN){
				if(lastItem != CompactSubData.OPEN){
					if(data.customItemCompare(fromItem, 0, lastItem) > 0)
						throw new IllegalArgumentException("fromItem > lastItem");
				}else{
					data.customItemCompare(fromItem, 0, fromItem);
				}
			}else if(lastItem != CompactSubData.OPEN){
				data.customItemCompare(lastItem, 0, lastItem);
			}
			this.fromItem = fromItem;
			this.fromInclusive = fromInclusive;
			this.lastItem = lastItem;
			this.lastInclusive = lastInclusive;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und
		 * das {@code index}-te Element zu klein sind.
		 * 
		 * @see CompactSubData#isTooLow(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index zu groß bzw. das {@code index}-te Element zu klein ist.
		 */
		protected final boolean isTooLow(final int index) {
			final GData data = this.data;
			return (index > data.lastIndex()) || ((index >= data.firstIndex()) && this.isTooLow(data.list[index]));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu klein ist. Wenn das erste Element
		 * gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu klein sein. Anderenfalls gilt es als zu
		 * klein, wenn es entweder kleiner als das erste Element ist oder wenn das erste Element exklusiv ist und das
		 * gegebene Element gleich dem ersten Element ist.
		 * 
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @see CompactSubData#fromItem
		 * @see CompactSubData#fromInclusive
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element zu klein ist.
		 */
		protected final boolean isTooLow(final Object key) {
			final Object fromItem = this.fromItem;
			if(fromItem == CompactSubData.OPEN) return false;
			final int comp = this.data.customItemCompare(key, 0, fromItem);
			return ((comp < 0) || ((comp == 0) && !this.fromInclusive));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Index zu groß ist oder der Index gültig und
		 * das {@code index}-te Element zu groß sind.
		 * 
		 * @see CompactSubData#isTooHigh(Object)
		 * @param index Index.
		 * @return {@code true}, wenn der gegebene Index bzw. das {@code index}-te Element zu groß ist.
		 */
		protected final boolean isTooHigh(final int index) {
			final GData data = this.data;
			return (index > data.lastIndex()) || ((index >= data.firstIndex()) && this.isTooHigh(data.list[index]));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element zu groß ist. Wenn das letzte Element
		 * gleich {@link CompactSubData#OPEN} ist, kann das gegebene Element nie zu groß sein. Anderenfalls gilt es als zu
		 * groß, wenn es entweder größer als das letzte Element ist oder wenn das letzte Element exklusiv ist und das
		 * gegebene Element gleich dem letzten Element ist.
		 * 
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @see CompactSubData#lastItem
		 * @see CompactSubData#lastInclusive
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element zu groß ist.
		 */
		protected final boolean isTooHigh(final Object key) {
			final Object lastItem = this.lastItem;
			if(lastItem == CompactSubData.OPEN) return false;
			final int comp = this.data.customItemCompare(key, 0, lastItem);
			return ((comp > 0) || ((comp == 0) && !this.lastInclusive));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich liegt. Die
		 * Inklusitivität des ersten bzw. letzten Elements wird beachtet.
		 * 
		 * @see CompactSubData#isTooLow(int)
		 * @see CompactSubData#isTooHigh(Object)
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich liegt.
		 */
		protected final boolean isInRange(final Object key) {
			return !this.isTooLow(key) && !this.isTooHigh(key);
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich (oder auf dessen
		 * Grenzen) liegt.
		 * 
		 * @see CompactSubData#isInRange(Object)
		 * @see CompactSubData#isInClosedRange(Object)
		 * @param key Element.
		 * @param inclusive {@code true}, wenn die Inklusitivität des ersten bzw. letzten Elements beachtet werden soll.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich (oder auf dessen Grenzen) liegt.
		 */
		protected final boolean isInRange(final Object key, final boolean inclusive) {
			return (inclusive ? this.isInRange(key) : this.isInClosedRange(key));
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das gegebene Element im gültigen Bereich oder auf dessen
		 * Grenzen liegt. Die Inklusitivität des ersten bzw. letzten Elements ignoriert.
		 * 
		 * @see CompactData#customItemCompare(Object, int, Object)
		 * @param key Element.
		 * @return {@code true}, wenn das gegebene Element im gültigen Bereich oder auf dessen Grenzen liegt.
		 */
		protected final boolean isInClosedRange(final Object key) {
			final GData data = this.data;
			final Object fromItem = this.fromItem, lastItem = this.lastItem;
			return ((fromItem == CompactSubData.OPEN) || (data.customItemCompare(key, 0, fromItem) >= 0))
				&& ((lastItem == CompactSubData.OPEN) || (data.customItemCompare(key, 0, lastItem) <= 0));
		}

		/**
		 * Diese Methode gibt den Index des ersten Elements zurück.
		 * 
		 * @return Index des ersten Elements.
		 */
		protected final int firstIndex() {
			final GData data = this.data;
			final Object fromItem = this.fromItem;
			if(fromItem == CompactSubData.OPEN) return data.firstIndex();
			if(this.fromInclusive) return data.ceilingIndex(fromItem);
			return data.higherIndex(fromItem);
		}

		/**
		 * Diese Methode gibt den Index des letzten Elements zurück.
		 * 
		 * @return Index des letzten Elements.
		 */
		protected final int lastIndex() {
			final GData data = this.data;
			final Object lastItem = this.lastItem;
			if(lastItem == CompactSubData.OPEN) return data.lastIndex();
			if(this.lastInclusive) return data.floorIndex(lastItem);
			return data.lowerIndex(lastItem);
		}

		/**
		 * Diese Methode gibt den Index des kleinsten Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück.
		 * 
		 * @see NavigableSet#first()
		 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int lowestIndex() {
			final int index = this.firstIndex();
			if(this.isTooHigh(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück.
		 * 
		 * @see NavigableSet#last()
		 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int highestIndex() {
			final int index = this.lastIndex();
			if(this.isTooLow(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Wenn kein solches
		 * Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#lower(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner dem gegebenen ist, oder
		 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int lowerIndex(final Object item) {
			if(this.isTooHigh(item)) return this.highestIndex();
			final int index = this.data.lowerIndex(item);
			if(this.isTooLow(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebenen ist. Wenn kein
		 * solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#floor(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist, oder
		 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int floorIndex(final Object item) {
			if(this.isTooHigh(item)) return this.highestIndex();
			final int index = this.data.floorIndex(item);
			if(this.isTooLow(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass größer oder gleich dem gegebenen ist. Wenn kein
		 * solches Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#ceiling(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer oder gleich dem gegebenen ist, oder
		 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int ceilingIndex(final Object item) {
			if(this.isTooLow(item)) return this.lowestIndex();
			final int index = this.data.ceilingIndex(item);
			if(this.isTooHigh(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode gibt den Index des größten Elements zurück, dass größer dem gegebenen ist. Wenn kein solches
		 * Element existiert wird <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück gegeben.
		 * 
		 * @see NavigableSet#higher(Object)
		 * @param item Element.
		 * @return Index des größten Elements, dass größer dem gegebenen ist, oder
		 *         <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected final int higherIndex(final Object item) {
			if(this.isTooLow(item)) return this.lowestIndex();
			final int index = this.data.higherIndex(item);
			if(this.isTooHigh(index)) return -index - 1;
			return index;
		}

		/**
		 * Diese Methode leert die Teilmenge.
		 */
		protected final void clearItems() {
			final int fromIndex = this.firstIndex(), lastIndex = this.lastIndex();
			if(fromIndex > lastIndex) return;
			this.data.customRemove(fromIndex, (lastIndex - fromIndex) + 1);
		}

		/**
		 * Diese Methode gibt die Anzahl der Elemente in der Teilmenge zurück.
		 * 
		 * @return Anzahl.
		 */
		protected final int countItems() {
			return (this.lastIndex() - this.firstIndex()) + 1;
		}

	}

	/**
	 * Dieses Feld speichert das leere Array.
	 */
	protected static final Object[] VOID = new Object[0];

	/**
	 * Diese Methode sucht das gegebene Element im gegebenen Array linear und gibt den Index des ersten Treffers oder oder
	 * {@code -1} zurück.
	 * 
	 * @see Object#equals(Object)
	 * @param list Array.
	 * @param from Index des ersten Elements.
	 * @param size Anzahl der Elemente.
	 * @param item gesuchtes Element.
	 * @return Index des ersten Treffers oder {@code -1}.
	 */
	protected static final int indexOf(final Object[] list, int from, final int size, final Object item) {
		if(item == null){
			for(final int last = from + size; from < last; from++){
				if(list[from] == null) return from;
			}
		}else{
			for(final int last = from + size; from < last; from++){
				if(item.equals(list[from])) return from;
			}
		}
		return -1;
	}

	/**
	 * Dieses Feld speichert die Elemente.
	 */
	protected Object[] list = CompactData.VOID;

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
	 * verwalten zu können.
	 * 
	 * @param list Array.
	 * @param count Anzahl.
	 * @return Länge.
	 */
	protected final int defaultLength(final Object[] list, final int count) {
		final int oldLength = list.length;
		if(oldLength >= count) return oldLength;
		final int newLength = oldLength + (oldLength >> 1);
		if(newLength >= count) return newLength;
		return count;
	}

	/**
	 * Diese Methode setzt die Größe des gegebenen Arrays und gibt es zurück. Wenn die Größe des gegebenen Arrays von der
	 * gegebenen Größe abweicht, werden ein neues Array mit passender Größe erzeugt, die Elemente des gegebenen Arrays
	 * mittig in das neue Array kopiert und das neue Array zurück gegeben.
	 * 
	 * @see CompactData#customAlignment(int)
	 * @param list Array.
	 * @param length neue Größe.
	 * @return (neues) Array.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden.
	 */
	protected final Object[] defaultResize(final Object[] list, final int length) throws IllegalArgumentException {
		final int size = this.size;
		if(size > length) throw new IllegalArgumentException("size > length");
		if(length == 0) return CompactData.VOID;
		if(length == list.length) return list;
		final Object[] list2 = new Object[length];
		final int from2 = this.customAlignment(length - size);
		System.arraycopy(list, this.from, list2, from2, size);
		this.from = from2;
		return list2;
	}

	/**
	 * Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position in das gegebenen Array ein und gibt
	 * das Array zurück. Wenn die Größe des gegebenen Arrays nicht verändert werden muss, wird versucht, die wenigen
	 * Elemente vor bzw. nach dem gegebenen Index um die gegebene Anzahl zu verschieben. Reicht der verfügbare Platz zum
	 * Verschieben dieser wenigen Elemente nicht aus, so werden alle Elemente verschoben und mittig im gegebenen Array
	 * ausgerichtet. Wenn die Größe des gegebenen Arrays dagegen angepasst werden muss, werden ein neues Array mit
	 * passender Größe erzeugt und die Elemente des gegebenen Arrays mittig in das neue Array kopiert. Die benötigte Größe
	 * wird via {@link CompactData#defaultLength(Object[], int)} ermittelt.
	 * 
	 * @see CompactData#customAlignment(int)
	 * @see CompactData#defaultLength(Object[], int)
	 * @param array Array.
	 * @param index Index des ersten neuen Elements.
	 * @param count Anzahl der neuen Elemente.
	 * @return (neues) Array.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden.
	 */
	protected final Object[] defaultInsert(final Object[] array, final int index, final int count)
		throws IllegalArgumentException {
		final int from = this.from;
		final int index2 = index - from;
		if(index2 < 0) throw new IllegalArgumentException("index < from");
		final int size = this.size;
		if(index2 > size) throw new IllegalArgumentException("index > from + size");
		if(count == 0) return array;
		if(count < 0) throw new IllegalArgumentException("count < 0");
		final int size2 = size + count;
		final int array2Length = this.defaultLength(array, size2);
		this.size = size2;
		if(array2Length != array.length){
			final Object[] array2 = new Object[array2Length];
			final int from2 = this.customAlignment(array2Length - size2);
			System.arraycopy(array, from, array2, from2, index2);
			System.arraycopy(array, index, array2, from2 + index2 + count, size - index2);
			this.from = from2;
			return array2;
		}
		if(index2 > (size / 2)){
			if((from + size2) <= array2Length){
				System.arraycopy(array, index, array, index + count, size - index2);
				return array;
			}
		}else{
			if(from >= count){
				final int from2 = from - count;
				this.from = from2;
				System.arraycopy(array, from, array, from2, index2);
				return array;
			}
		}
		final int from2 = this.defaultAlignment(array2Length - size2);
		this.from = from2;
		if(from2 < from){
			System.arraycopy(array, from, array, from2, index2);
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			final int last = from + size, last2 = from2 + size2;
			if(last2 < last){
				Arrays.fill(array, last2, last, null);
			}
		}else{
			System.arraycopy(array, index, array, from2 + index2 + count, size - index2);
			System.arraycopy(array, from, array, from2, index2);
			if(from2 > from){
				Arrays.fill(array, from, from2, null);
			}
		}
		return array;
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Elementen ab der gegebenen Position im gegebenen Array und gibt das
	 * Array zurück. Es wird versucht, die wenigen Elemente vor bzw. nach dem zu entfernenden Bereich um die gegebene
	 * Anzahl zu verschieben.
	 * 
	 * @see CompactData#customAlignment(int)
	 * @param list Array.
	 * @param index Index des ersten entfallenden Elements.
	 * @param count Anzahl der entfallende Elemente.
	 * @return (neues) Array.
	 * @throws IllegalArgumentException Wenn die Eingaben zu einem Zugriff außerhalb des Arrays führen würden.
	 */
	protected final Object[] defaultRemove(final Object[] list, final int index, final int count)
		throws IllegalArgumentException {
		final int from = this.from;
		final int size = this.size;
		final int index2 = index - from;
		if((index2 < 0) || (index2 > size)) throw new IllegalArgumentException("index out of range: " + index);
		final int size2 = size - count;
		if((count < 0) || (size2 < 0)) throw new IllegalArgumentException("count out of range: " + count);
		if(count == 0) return list;
		this.size = size2;
		if(size2 == 0){
			this.from = this.customAlignment(list.length);
			Arrays.fill(list, from, from + size, null);
			return list;
		}
		if(index2 > (size2 / 2)){
			System.arraycopy(list, index + count, list, index, size2 - index2);
			Arrays.fill(list, from + size2, from + size, null);
			return list;
		}
		final int from2 = from + count;
		this.from = from2;
		System.arraycopy(list, from, list, from2, index2);
		Arrays.fill(list, from, from2, null);
		return list;
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
	 * Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das Array ein.
	 * 
	 * @see CompactData#defaultInsert(Object[], int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
	 */
	protected void customInsert(final int index, final int count) throws IllegalArgumentException {
		this.list = this.defaultInsert(this.list, index, count);
	}

	/**
	 * Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem Array mit der gegebenen
	 * Länge der Belegung.
	 * 
	 * @see CompactData#defaultRemove(Object[], int, int)
	 * @param index Index.
	 * @param count Anzahl.
	 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
	 */
	protected void customRemove(final int index, final int count) throws IllegalArgumentException {
		this.list = this.defaultRemove(this.list, index, count);
	}

	/**
	 * Diese Methode vergrößert die Kapazität des Arrays, sodass dieses die gegebene Anzahl an Elementen verwalten kann.
	 * 
	 * @see CompactData#defaultResize(Object[], int)
	 * @param count Anzahl.
	 */
	protected void customAllocate(final int count) {
		this.list = this.defaultResize(this.list, this.defaultLength(this.list, count));
	}

	/**
	 * Diese Methode verkleinert die Kapazität des Arrays auf das Minimum für seine Belegung.
	 * 
	 * @see CompactData#defaultResize(Object[], int)
	 */
	protected void customCompact() {
		this.list = this.defaultResize(this.list, this.size);
	}

	/**
	 * Diese Methode gibt die Position zurück, an der die Elemente des Arrays ausgerichtet werden sollen. Bei der
	 * Ausrichtung {@code 0} werden die Elemente am Anfang des Arrays ausgerichtet, wodurch das häufige Einfügen von
	 * Elementen am Ende des Arrays beschleunigt wird. Für die relative Ausrichtung {@code space} gilt das gegenteil, da
	 * hier die Elemente am Ende des Arrays ausgerichtet werden, wodurch das häufige Einfügen von Elementen am Anfang des
	 * Arrays beschleunigt wird.
	 * 
	 * @see CompactData#defaultInsert(Object[], int, int)
	 * @see CompactData#defaultRemove(Object[], int, int)
	 * @see CompactData#defaultResize(Object[], int)
	 * @see CompactData#defaultAlignment(int)
	 * @param space Anzahl der nicht belegten Elemente.
	 * @return Position zur Ausrichtung ({@code 0..space}).
	 */
	protected int customAlignment(final int space) {
		return this.defaultAlignment(space);
	}

	/**
	 * Diese Methode sucht nach dem gegebenen Objekt und gibt dessen Index oder
	 * <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem der Eintrag
	 * eingefügt werden müsste.
	 * 
	 * @see CompactData#equalsIndex(Object, int)
	 * @see CompactData#compareIndex(Object, int)
	 * @param item Objekt.
	 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	protected abstract int customItemIndex(final Object item);

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Schlüssel {@link Object#equals(Object)
	 * äquivalent} dem Schlüssel des gegebenen Elements ist.
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels oder {@code 0}.
	 * @param item Element.
	 * @return {@link Object#equals(Object) Äquivalenz} der Schlüssel.
	 */
	protected abstract boolean customItemEquals(Object key, int hash, Object item);

	/**
	 * Diese Methode sucht zuerst binär und danach linear nach einem Eintrag, dessen Schlüssel gleich dem gegebenen
	 * Schlüssel ist und gibt den Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die
	 * <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste. Ein Element {@code element} ist
	 * dann zum gegebenen Schlüssel gleich, wenn {@code (compare(key, hash, element) == 0) &&
	 * equals(key, hash, element)}.
	 * 
	 * @see CompactData#customItemEquals(Object, int, Object)
	 * @see CompactData#customItemCompare(Object, int, Object)
	 * @see CompactData#compareIndex(Object, int)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	protected final int equalsIndex(final Object key, final int hash) {
		Object item;
		final int index = this.compareIndex(key, hash);
		if(index < 0) return index;
		final Object[] list = this.list;
		if(this.customItemEquals(key, hash, list[index])) return index;
		for(int next = index + 1, last = this.from + this.size; (next < last)
			&& (this.customItemCompare(key, hash, item = list[next]) == 0); next++){
			if(this.customItemEquals(key, hash, item)) return next;
		}
		for(int next = index - 1, from = this.from; (from <= next)
			&& (this.customItemCompare(key, hash, item = list[next]) == 0); next--){
			if(this.customItemEquals(key, hash, item)) return next;
		}
		return -(index + 1);
	}

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn der gegebene Schlüssel kleiner,
	 * gleich bzw. größer als der Schlüssel des gegebenen Elements ist. Die Berechnung kann auf den Schlüsseln selbst oder
	 * ihren {@link Object#hashCode() Streuwerten} beruhen.
	 * 
	 * @see Comparator#compare(Object, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @param item Element.
	 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der Schlüssel.
	 */
	protected abstract int customItemCompare(Object key, int hash, Object item);

	/**
	 * Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen
	 * Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem
	 * der Eintrag eingefügt werden müsste.
	 * 
	 * @see CompactData#customItemCompare(Object, int, Object)
	 * @param key Schlüssel.
	 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
	 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
	 */
	protected final int compareIndex(final Object key, final int hash) {
		int from = this.from, last = from + this.size;
		final Object[] list = this.list;
		while(from < last){
			final int next = (from + last) >>> 1;
			final int comp = this.customItemCompare(key, hash, list[next]);
			if(comp < 0){
				last = next;
			}else if(comp > 0){
				from = next + 1;
			}else return next;
		}
		return -(from + 1);
	}

	/**
	 * Diese Methode gibt den Index des ersten Elements zurück. Dieser Index kann den Wert {@code from+size} annehmen.
	 * 
	 * @see NavigableSet#first()
	 * @return Index des ersten Elements.
	 */
	protected final int firstIndex() {
		return this.from;
	}

	/**
	 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner dem gegebenen ist. Dieser Index kann die
	 * Werte {@code from-1} und {@code from+size} annehmen.
	 * 
	 * @see NavigableSet#lower(Object)
	 * @param item Element.
	 * @return Index des größten Elements, dass kleiner dem gegebenen ist.
	 */
	protected final int lowerIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if(index < 0) return -index - 2;
		return index - 1;
	}

	/**
	 * Diese Methode gibt den Index des größten Elements zurück, dass kleiner oder gleich dem gegebene ist. Dieser Index
	 * kann die Werte {@code from-1} und {@code from+size} annehmen.
	 * 
	 * @see NavigableSet#floor(Object)
	 * @param item Element.
	 * @return Index des größten Elements, dass kleiner oder gleich dem gegebenen ist.
	 */
	protected final int floorIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if(index < 0) return -index - 2;
		return index;
	}

	/**
	 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer oder gleich dem gegebene ist. Dieser Index
	 * kann den Wert {@code from+size} annehmen.
	 * 
	 * @see NavigableSet#ceiling(Object)
	 * @param item Element.
	 * @return Index des kleinsten Elements, dass größer oder gleich dem gegebenen ist.
	 */
	protected final int ceilingIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if(index < 0) return -index - 1;
		return index;
	}

	/**
	 * Diese Methode gibt den Index des kleinsten Elements zurück, dass größer dem gegebene ist. Dieser Index kann den
	 * Wert {@code from+size} annehmen.
	 * 
	 * @see NavigableSet#higher(Object)
	 * @param item Element.
	 * @return Index des kleinsten Elements, dass größer dem gegebenen ist.
	 */
	protected final int higherIndex(final Object item) {
		final int index = this.customItemIndex(item);
		if(index < 0) return -index - 1;
		return index + 1;
	}

	/**
	 * Diese Methode gibt den Index des letzten Elements zurück. Dieser Index kann deb Wert {@code from-1} annehmen.
	 * 
	 * @see NavigableSet#last()
	 * @return Index des letzten Elements.
	 */
	protected final int lastIndex() {
		return (this.from + this.size) - 1;
	}

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