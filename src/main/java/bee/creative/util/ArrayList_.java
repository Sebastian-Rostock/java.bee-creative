package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

class ArrayList_<GItem> implements List<GItem>, Deque<GItem>, Cloneable, Serializable {

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 2146674771303938589L;

	protected static class SectionIterator<GItem> {

		final ArrayList_<GItem> array;

		final int offset;

		int length;

		int cursor = 0;

		int result = -1;

		public SectionIterator(final ArrayList_<GItem> array) {
			this.array = array;
			this.offset = 0;
			this.length = array.length;
		}

		public SectionIterator(final ArrayList_<GItem> array, final int offset, final int length) {
			if ((offset < 0) || (length < 0) || ((offset + length) > array.length)) throw new IndexOutOfBoundsException();
			this.array = array;
			this.offset = offset;
			this.length = length;
		}

		{}

		/** Diese Methode überspringt die gegebene Anzahl an Elementen. Eine negative Anzahl navigiert rückwärts.
		 *
		 * @param count Anzahl an Elementen.
		 * @throws IndexOutOfBoundsException Wenn die erreichte Position ungültig ist. */
		protected final void skip(final int count) throws IndexOutOfBoundsException {
			final int index = this.cursor + count;
			if ((index < 0) || (index > this.length)) throw new IndexOutOfBoundsException();
			this.cursor = index;
			this.result = -1;
		}

		/** Diese Methode implementiert {@link ListIterator#hasPrevious()}. */
		protected final boolean hasPrevImpl() {
			return 0 <= this.cursor;
		}

		/** Diese Methode implementiert {@link ListIterator#previous()}. */
		protected final GItem prevImpl() {
			try {
				final int index = this.cursor - 1;
				if (index < 0) throw new NoSuchElementException();
				this.cursor = index;
				this.result = index;
				return this.array.getImpl(this.offset + index);
			} catch (final IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}

		/** Diese Methode implementiert {@link ListIterator#previousIndex()}. */
		protected final int prevIndexImpl() {
			return this.cursor - 1;
		}

		/** Diese Methode implementiert {@link ListIterator#hasNext()}. */
		protected final boolean hasNextImpl() {
			return this.cursor < this.length;
		}

		/** Diese Methode implementiert {@link ListIterator#next()}. */
		protected final GItem nextImpl() {
			final int index = this.cursor;
			if (index >= this.length) throw new NoSuchElementException();
			this.cursor = index + 1;
			this.result = index;
			return this.array.getImpl(this.offset + index);
		}

		/** Diese Methode implementiert {@link ListIterator#nextIndex()}. */
		protected final int nextIndexImpl() {
			return this.cursor;
		}

		/** Diese Methode implementiert {@link ListIterator#set(Object)}. */
		protected final void setImpl(final GItem item) throws IllegalStateException {
			final int index = this.result;
			if (index < 0) throw new IllegalStateException();
			this.array.set(this.offset + index, item);
		}

		/** Diese Methode implementiert {@link ListIterator#add(Object)}. */
		protected final void addImpl(final GItem item) throws IllegalStateException {
			final int index = this.cursor;
			if (!this.array.offerImpl(this.offset + index, item)) throw new IllegalStateException();
			this.cursor = index + 1;
			this.result = -1;
		}

		/** Diese Methode implementiert {@link ListIterator#remove()}. */
		protected final void removeImpl() {
			final int index = this.result;
			if (index < 0) throw new IllegalStateException();
			this.array.deleteImpl(this.offset + index, 1);
			this.result = -1;
			this.length--;
			if (index >= this.cursor) return;
			this.cursor--;
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IterA<GItem> extends SectionIterator<GItem> implements ListIterator<GItem> {

		public IterA(final ArrayList_<GItem> array) {
			super(array);
		}

		public IterA(final ArrayList_<GItem> array, final int offset, final int length) {
			super(array, offset, length);
		}

		{}

		@Override
		public boolean hasNext() {
			return this.hasNextImpl();
		}

		@Override
		public GItem next() {
			return this.nextImpl();
		}

		@Override
		public boolean hasPrevious() {
			return this.hasPrevImpl();
		}

		@Override
		public GItem previous() {
			return this.prevImpl();
		}

		@Override
		public int nextIndex() {
			return this.nextIndexImpl();
		}

		@Override
		public int previousIndex() {
			return this.prevIndexImpl();
		}

		@Override
		public void remove() {
			this.removeImpl();
		}

		@Override
		public void set(final GItem item) {
			this.setImpl(item);
		}

		@Override
		public void add(final GItem item) {
			this.addImpl(item);
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IterD<GItem> extends SectionIterator<GItem> implements Iterator<GItem> {

		public IterD(final ArrayList_<GItem> array) {
			super(array);
		}

		{}

		@Override
		public boolean hasNext() {
			return this.hasPrevImpl();
		}

		@Override
		public GItem next() {
			return this.prevImpl();
		}

		@Override
		public void remove() {
			this.removeImpl();
		}

	}

	static class SubList<GItem> implements List<GItem> {

		final ArrayList_<GItem> items;

		final int offset;

		int length;

		SubList(final ArrayList_<GItem> items, final int offset, final int length) {
			if ((offset < 0) || (length < 0) || ((offset + length) > items.length)) throw new IndexOutOfBoundsException();
			this.items = items;
			this.offset = offset;
			this.length = length;
		}

		/** {@inheritDoc} */
		@Override
		public int size() {
			return this.length;
		}

		/** {@inheritDoc} */
		@Override
		public boolean isEmpty() {
			return this.length == 0;
		}

		/** {@inheritDoc} */
		@Override
		public boolean contains(final Object object) {
			return this.items.containsImpl(object, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public Iterator<GItem> iterator() {
			return new IterA<>(this.items, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public Object[] toArray() {
			return this.items.toArrayImpl(ArrayList_.EMPTY_OBJECTS, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public <T> T[] toArray(final T[] array) {
			return this.items.toArrayImpl(array, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public boolean add(final GItem item) {
			return this.items.addImpl(this.offset + this.length, item);
		}

		@Override
		public boolean remove(final Object o) {

			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean containsAll(final Collection<?> c) {
			return this.items.containsAllImpl(c, this.offset, this.length);
		}

		@Override
		public boolean addAll(final Collection<? extends GItem> objects) {
			return false;
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends GItem> objects) {
			return false;
		}

		/** {@inheritDoc} */
		@Override
		public boolean retainAll(final Collection<?> elements) {
			return Iterables.retainAll((Iterable<?>)this, elements);
		}

		/** {@inheritDoc} */
		@Override
		public boolean removeAll(final Collection<?> elements) {
			return Iterables.removeAll((Iterable<?>)this, elements);
		}

		/** {@inheritDoc} */
		@Override
		public void clear() {
			this.items.deleteImpl(this.offset, this.length);
			this.length = 0;
		}

		/** {@inheritDoc} */
		@Override
		public GItem get(final int index) {
			if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
			return this.items.getImpl(this.offset + index);
		}

		/** {@inheritDoc} */
		@Override
		public GItem set(final int index, final GItem element) {
			if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
			return this.items.setImpl(this.offset + index, element);
		}

		@Override
		public void add(final int index, final GItem element) {
			if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
			this.items.addImpl(this.offset + index, element);
		}

		/** {@inheritDoc} */
		@Override
		public GItem remove(final int index) {
			if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
			return this.items.remove(this.offset + index);
		}

		/** {@inheritDoc} */
		@Override
		public int indexOf(final Object object) {
			final int offset = this.offset, result = this.items.firstIndexImpl(object, offset, this.length);
			return result >= 0 ? result + offset : -1;
		}

		/** {@inheritDoc} */
		@Override
		public int lastIndexOf(final Object object) {
			final int offset = this.offset, result = this.items.lastIndexImpl(object, offset, this.length);
			return result >= 0 ? result + offset : -1;
		}

		/** {@inheritDoc} */
		@Override
		public ListIterator<GItem> listIterator() {
			return new IterA<>(this.items, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public ListIterator<GItem> listIterator(final int index) {
			final IterA<GItem> result = new IterA<>(this.items, this.offset, this.length);
			result.skip(index);
			return result;
		}

		/** {@inheritDoc} */
		@Override
		public List<GItem> subList(final int fromIndex, final int toIndex) {
			final int offset = this.offset;
			return this.items.subList(offset + fromIndex, offset + toIndex);
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return this.items.hashImpl(this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof List<?>)) return false;
			return this.items.equalsImpl((List<?>)object, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return this.items.toStringImpl(this.offset, this.length);
		}

	}

	{}

	/** Dieses Feld speichert den initialwert für {@link #array}. */
	static final Object[] EMPTY_OBJECTS = {};

	/** Dieses Feld speichert die maximale Kapazität. */
	static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

	{}

	static final int getIndex(final Object[] array, final int offset, final int index) {
		final int min = offset + index, max = array.length;
		return min >= max ? min - max : min;
	}

	{}

	/** Dieses Feld speichert die Elemente. */
	transient Object[] array;

	/** Dieses Feld speichert die Position des ersten Elements in {@link #array}. */
	transient int offset;

	/** Dieses Feld speichert die Länge der Liste. */
	transient int length;

	/** Dieser Konstruktor initialisiert die leere Liste. */
	public ArrayList_() {
		this.array = ArrayList_.EMPTY_OBJECTS;
	}

	/** Dieser Konstruktor initialisiert die Liste mit der gegebenen Kapazität.
	 *
	 * @param capacity Kapazität. */
	public ArrayList_(final int capacity) {
		this.array = capacity != 0 ? new Object[capacity] : ArrayList_.EMPTY_OBJECTS;
	}

	{}

	protected final boolean addImpl(final int index, final GItem item) throws OutOfMemoryError {
		if (!this.offerImpl(index, item)) throw new OutOfMemoryError();
		return true;
	}

	protected boolean addAllImpl(final int index, final Collection<? extends GItem> item) throws IndexOutOfBoundsException, OutOfMemoryError {
		final int size = item.size();
		if (!this.insertImpl(index, size)) throw new OutOfMemoryError();
		if (size == 0) return false;
		// TODO
		return false;
	}

	/** Diese Methode fügt die gegebene Anzahl an Elementen an der gegebenen Position ein und gibt nur dann {@code true} zurück, wenn dies erfolgreich war.
	 *
	 * @param index Position, an der die Elemente eingefügt werden sollen.
	 * @param count Anzahl der einzufügenden Elemente.
	 * @return {@code true}, wenn der Speicherbereich eingefügt werden konnte. */
	protected final boolean insertImpl(final int index, final int count) throws IndexOutOfBoundsException {
		// TODO
		return false;
	}

	protected void deleteImpl(final int index, final int count) {
		final Object[] array = this.array;
		final int index2 = index + count, count2 = this.length - index2;
		if (index <= count2) { // vordere hinterschieben
			this.moveRImpl(array, 0, index, count);
			this.clearImpl(array, 0, count);
			offset = getIndex(array, offset, count);
		} else { // hintere vorschieben
			this.moveLImpl(array, 0, index2, count2);
			this.clearImpl(array, index2, count);
		}
	}

	final void moveRImpl(final Object[] array, final int offset, final int length, final int count) {

	}

	final void moveLImpl(final Object[] array, final int offset, final int length, final int count) {

	}

	final void clearImpl(final Object[] array, final int offset, final int length) {
		final int capacity = array.length, min = this.offset + offset, max = min + length;
		for (int i = min, size = max < capacity ? max : capacity; i < size; i++) {
			array[i] = null;
		}
		for (int i = 0, size = max - capacity; i < size; i++) {
			array[i] = null;
		}
	}

	{}

	{}

	static int DONE = 0;

	{}

	@SuppressWarnings ("javadoc")
	private final void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int length = stream.readInt();
		if (length == 0) return;
		final Object[] array = new Object[length];
		this.insertImpl(0, length);
		for (int i = 0; i < length; array[i++] = stream.readObject()) {}
		this.array = array;
		this.length = length;
	}

	@SuppressWarnings ("javadoc")
	private final void writeObject(final ObjectOutputStream stream) throws IOException {
		final Object[] array = this.array;
		final int length = this.length;
		final int capacity = array.length;
		final int min = this.offset, max = min + length;
		for (int i = min, size = max < capacity ? max : capacity; i < size;) {
			stream.writeObject(array[i]);
		}
		for (int i = 0, size = max - capacity; i < size;) {
			stream.writeObject(array[i]);
		}
	}

	/** Diese Methode gibt das Element an der gegebenen Position zurück.<br>
	 * <b>Achtung:</b> Es erfolgt keine Bereichsprüfung!
	 *
	 * @param index Position.
	 * @return Element. */
	protected final GItem getImpl(final int index) {
		final Object[] array = this.array;
		@SuppressWarnings ("unchecked")
		final GItem result = (GItem)array[ArrayList_.getIndex(array, this.offset, index)];
		return result;
	}

	/** Diese Methode ersetzt das Element an der gegebenen Position und gibt alten Wert zurück.<br>
	 * <b>Achtung:</b> Es erfolgt keine Bereichsprüfung!
	 *
	 * @param index Position.
	 * @param item neses Element.
	 * @return altes Element. */
	protected final GItem setImpl(final int index, final GItem item) {
		final Object[] array = this.array;
		final int index2 = ArrayList_.getIndex(array, this.offset, index);
		@SuppressWarnings ("unchecked")
		final GItem result = (GItem)array[index2];
		array[index2] = item;
		return result;
	}

	/** Diese Methode fügt das Element an der gegebenen Position ein.
	 *
	 * @param index Position, an der das Element eingefügt werden soll.
	 * @param item Element.
	 * @return {@code true}, wenn das Element eingefügt werden konnte.
	 * @throws IndexOutOfBoundsException Wenn {@code index} ungültig ist. */
	protected final boolean offerImpl(final int index, final GItem item) throws IndexOutOfBoundsException {
		if (!this.insertImpl(index, 1)) return false;
		final Object[] array = this.array;
		array[ArrayList_.getIndex(array, this.offset, index)] = item;
		return true;
	}

	/** Diese Methode gibt die größte Position des gegebenen Elements innerhalb des gegebenen Abschnitts zurück.<br>
	 * <b>Achtung:</b> Es erfolgt keine Prüfung von {@code offset} und {@code length}.
	 *
	 * @param item gesuchtes Element.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Position des ersten Treffers relativ zum Abschnitt oder {@code -1}. */
	protected final int lastIndexImpl(final Object item, final int offset, final int length) {
		final Object[] array = this.array;
		final int capacity = array.length, min = this.offset + offset, max = min + length;
		// TODO
		if (max <= capacity) {
			for (int i = max; min < i;) {
				if (Objects.equals(array[--i], item)) return i - min;
			}
		} else {
			for (int i = max - capacity; 0 < i;) {
				if (Objects.equals(array[--i], item)) return (i - min) + capacity;
			}
			for (int i = capacity; min < i;) {
				if (Objects.equals(array[--i], item)) return i - min;
			}
		}
		return -1;
	}

	/** Diese Methode gibt die kleinste Position des gegebenen Elements innerhalb des gegebenen Abschnitts zurück.<br>
	 * <b>Achtung:</b> Es erfolgt keine Prüfung von {@code offset} und {@code length}.
	 *
	 * @param object gesuchtes Element.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Position des ersten Treffers relativ zum Abschnitt oder {@code -1}. */
	protected final int firstIndexImpl(final Object object, final int offset, final int length) {
		final Object[] array = this.array;
		final int capacity = array.length, min = this.offset + offset, max = min + length;
		for (int i = min, size = max < capacity ? max : capacity; i < size; i++) {
			if (Objects.equals(array[i], object)) return i - min;
		}
		for (int i = 0, size = max - capacity; i < size; i++) {
			if (Objects.equals(array[i], object)) return (i - min) + capacity;
		}
		return -1;
	}

	/** Diese Methode implementiert {@link #contains(Object)} für den gegebenen Abschnitt.
	 *
	 * @param object gesuchtes Element.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Enthaltensein. */
	protected final boolean containsImpl(final Object object, final int offset, final int length) {
		return this.firstIndexImpl(object, offset, length) >= 0;
	}

	/** Diese Methode implementiert {@link #containsAll(Collection)} für den gegebenen Abschnitt.
	 *
	 * @param objects gesuchte Elemente.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Enthaltensein. */
	protected final boolean containsAllImpl(final Collection<?> objects, final int offset, final int length) {
		for (final Object item: objects) {
			if (!this.containsImpl(item, offset, length)) return false;
		}
		return true;
	}

	/** Diese Methode implementiert {@link #hashCode()} für den gegebenen Abschnitt.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Streuwert. */
	protected final int hashImpl(final int offset, final int length) {
		int result = 1;
		final Object[] array = this.array;
		final int capacity = array.length, min = this.offset + offset, max = min + length;
		for (int i = min, size = max < capacity ? max : capacity; i < size; result = (31 * result) + Objects.hash(array[i++])) {}
		for (int i = 0, size = max - capacity; i < size; result = (31 * result) + Objects.hash(array[i++])) {}
		return result;
	}

	/** Diese Methode implementiert {@link #equals(Object)} für den gegebenen Abschnitt.
	 *
	 * @param that Liste zum Vergleich.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Vergleichswert. */
	protected final boolean equalsImpl(final List<?> that, final int offset, final int length) {
		if (that.size() != length) return false;
		final Object[] array = this.array;
		final int capacity = array.length, min = this.offset + offset, max = min + length;
		final Iterator<?> iter = that.iterator();
		for (int i = min, size = max < capacity ? max : capacity; i < size;) {
			if (!iter.hasNext() || !Objects.equals(array[i++], iter.next())) return false;
		}
		for (int i = 0, size = max - capacity; i < size;) {
			if (!iter.hasNext() || !Objects.equals(array[i++], iter.next())) return false;
		}
		return !iter.hasNext();
	}

	/** Diese Methode kopiert alle Elemente des gegebenen Abschnitts in das gegebene Array und gibt dieses zurück.<br>
	 * Wenn die Kapazität des gegebenen Arrays hierfür nicht ausreicht, wird ein neues mit passender Größe erzeugt. Überflüssige Bereiche werden mit {@code null}
	 * gefüllt.
	 *
	 * @param <T> Typ der Elemente im gegebenen Array.
	 * @param result gegebene Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return gegebenes oder neues Array. */
	@SuppressWarnings ("unchecked")
	protected final <T> T[] toArrayImpl(T[] result, final int offset, final int length) {
		if (result.length < length) {
			final Class<? extends Object[]> clazz = result.getClass();
			result = (T[])(clazz == Object[].class ? new Object[length] : Array.newInstance(clazz.getComponentType(), length));
		}
		this.collectImpl(result, offset, length);
		for (int i = result.length; length < i;) {
			result[--i] = null;
		}
		return result;
	}

	/** Diese Methode implementiert {@link #toString()} für den gegebenen Abschnitt.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Textdarstellung. */
	protected final String toStringImpl(final int offset, final int length) {
		if (length == 0) return "[]";
		final Object[] array = this.array;
		final int capacity = array.length, min = this.offset + offset, max = min + length;
		final StringBuilder result = new StringBuilder().append('[').append(array[min]);
		for (int i = min + 1, size = max < capacity ? max : capacity; i < size; result.append(", ").append(array[i++])) {}
		for (int i = 0, size = max - capacity; i < size; result.append(", ").append(array[i++])) {}
		return result.append(']').toString();
	}

	/** Diese Methode kopiert alle Elemente des gegebenen Abschnitts in das gegebene Array.
	 *
	 * @param target gegebene Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts. */
	protected final void collectImpl(final Object[] target, final int offset, final int length) {
		final Object[] array = this.array;
		final int capacity = array.length;
		final int min = this.offset + offset, max = min + length;
		int index = 0;
		for (int i = min, size = max < capacity ? max : capacity; i < size; target[index++] = array[i++]) {}
		for (int i = 0, size = max - capacity; i < size; target[index++] = array[i++]) {}
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwaltet werden kann.
	 *
	 * @param capacity Anzahl der verwaltbaren Einträge.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Elementen ist. */
	protected final void allocateImpl(final int capacity) throws IllegalArgumentException {
		if (capacity == this.capacityImpl()) return;
		final int length = this.length;
		if (capacity < length) throw new IllegalArgumentException();
		final Object[] array = new Object[capacity];
		this.collectImpl(array, 0, length);
		this.array = array;
		this.offset = 0;
	}

	/** Diese Methode gibt die Anzahl der aktuell verwaltbaren Elemente zurück.
	 *
	 * @return Länge von {@link #array}. */
	protected final int capacityImpl() {
		return this.array.length;
	}

	/** Diese Methode setzt die Kapazität, sodass dieses die gegebene Anzahl an Einträgen verwaltet werden kann, und gibt {@code this} zurück.
	 *
	 * @param capacity Anzahl der maximal verwaltbaren Einträge.
	 * @return {@code this}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als die aktuelle Anzahl an Elementen ist. */
	public ArrayList_<GItem> allocate(final int capacity) {
		this.allocateImpl(capacity);
		return this;
	}

	/** Diese Methode gibt die Anzahl der Elemente zurück, die ohne erneuter Speicherreservierung verwaltet werden kann.
	 *
	 * @return Kapazität. */
	public int capacity() {
		return this.array.length;
	}

	/** Diese Methode verkleinert die Kapazität auf das Minimum und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public ArrayList_<GItem> compact() {
		this.allocateImpl(this.length);
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public GItem get(final int index) {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.getImpl(index);
	}

	/** {@inheritDoc} */
	@Override
	public GItem getFirst() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		return this.getImpl(0);
	}

	/** {@inheritDoc} */
	@Override
	public GItem getLast() {
		final int index = this.length - 1;
		if (index < 0) throw new NoSuchElementException();
		return this.getImpl(index);
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(final GItem item) {
		return this.addImpl(this.length, item);
	}

	/** {@inheritDoc} */
	@Override
	public void add(final int index, final GItem item) {
		this.addImpl(index, item);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final Collection<? extends GItem> items) {
		return this.addAllImpl(this.length, items);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final int index, final Collection<? extends GItem> items) {
		return this.addAllImpl(index, items);
	}

	/** {@inheritDoc} */
	@Override
	public void addFirst(final GItem item) {
		if (!this.offerImpl(0, item)) throw new IllegalStateException();
	}

	/** {@inheritDoc} */
	@Override
	public void addLast(final GItem item) {
		if (!this.offerImpl(this.length, item)) throw new IllegalStateException();
	}

	/** {@inheritDoc} */
	@Override
	public GItem poll() {
		final int count = this.length;
		if (count == 0) return null;
		final GItem result = this.getImpl(0);
		this.deleteImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem pollFirst() {
		final int count = this.length;
		if (count == 0) return null;
		final GItem result = this.getImpl(0);
		this.deleteImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem pollLast() {
		final int index = this.length - 1;
		if (index < 0) return null;
		final GItem result = this.getImpl(index);
		this.deleteImpl(index, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem peek() {
		final int count = this.length;
		if (count == 0) return null;
		return this.getImpl(0);
	}

	/** {@inheritDoc} */
	@Override
	public GItem peekFirst() {
		final int count = this.length;
		if (count == 0) return null;
		return this.getImpl(0);
	}

	/** {@inheritDoc} */
	@Override
	public GItem peekLast() {
		final int index = this.length - 1;
		if (index < 0) return null;
		return this.getImpl(index);
	}

	/** {@inheritDoc} */
	@Override
	public boolean offer(final GItem item) {
		return this.offerImpl(this.length, item);
	}

	/** {@inheritDoc} */
	@Override
	public boolean offerFirst(final GItem item) {
		return this.offerImpl(0, item);
	}

	/** {@inheritDoc} */
	@Override
	public boolean offerLast(final GItem item) {
		return this.offerImpl(this.length, item);
	}

	/** {@inheritDoc} */
	@Override
	public GItem remove() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(0);
		this.deleteImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem remove(final int index) {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		final GItem result = this.getImpl(index);
		this.deleteImpl(index, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(final Object object) {
		final int index = this.lastIndexImpl(object, 0, this.length);
		if (index < 0) return false;
		this.deleteImpl(index, 1);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public GItem removeFirst() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(0);
		this.deleteImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem removeLast() {
		final int index = this.length - 1;
		if (index < 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(index);
		this.deleteImpl(index, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeFirstOccurrence(final Object item) {
		final int index = this.firstIndexImpl(item, 0, this.length);
		if (index < 0) return false;
		this.deleteImpl(index, 1);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeLastOccurrence(final Object item) {
		final int index = this.lastIndexImpl(item, 0, this.length);
		if (index < 0) return false;
		this.deleteImpl(index, 1);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(final Collection<?> items) {
		return Iterators.removeAll(this.iterator(), items);
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(final Collection<?> items) {
		return Iterators.retainAll(this.iterator(), items);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(final Object item) {
		return this.containsImpl(item, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(final Collection<?> items) {
		return this.containsAllImpl(items, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public GItem set(final int index, final GItem item) {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
		return this.setImpl(index, item);
	}

	/** {@inheritDoc} */
	@Override
	public GItem pop() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(0);
		this.deleteImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void push(final GItem item) {
		if (!this.offerImpl(0, item)) throw new IllegalStateException();
	}

	/** {@inheritDoc} */
	@Override
	public GItem element() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		return this.getImpl(0);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> iterator() {
		return new IterA<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<GItem> listIterator() {
		return new IterA<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<GItem> listIterator(final int index) {
		final IterA<GItem> result = new IterA<>(this);
		result.skip(index);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> descendingIterator() {
		return new IterD<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.length;
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.deleteImpl(0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.length == 0;
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(final Object item) {
		return this.firstIndexImpl(item, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(final Object item) {
		return this.lastIndexImpl(item, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public List<GItem> subList(final int fromIndex, final int toIndex) {
		return new SubList<>(this, fromIndex, toIndex - fromIndex);
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return this.toArrayImpl(ArrayList_.EMPTY_OBJECTS, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(final T[] result) {
		return this.toArrayImpl(result, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.hashImpl(0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof List<?>)) return false;
		return this.equalsImpl((List<?>)object, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.toStringImpl(0, this.length);
	}

}
