package bee.creative.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

class ArrayList_<GItem> implements List<GItem>, Deque<GItem>, Cloneable, Serializable {

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
			this.array.removeImpl(this.offset + index, 1);
			this.result = -1;
			this.length--;
			if (index >= this.cursor) return;
			this.cursor--;
		}

	}

	@SuppressWarnings ("javadoc")
	protected static class IteratorA<GItem> extends SectionIterator<GItem> implements ListIterator<GItem> {

		public IteratorA(final ArrayList_<GItem> array) {
			super(array);
		}

		public IteratorA(final ArrayList_<GItem> array, final int offset, final int length) {
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
	protected static class IteratorD<GItem> extends SectionIterator<GItem> implements Iterator<GItem> {

		public IteratorD(final ArrayList_<GItem> array) {
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

	static class SectionList<GItem> implements List<GItem> {

		final ArrayList_<GItem> items;

		final int offset;

		int length;

		SectionList(final ArrayList_<GItem> items, final int offset, final int length) {
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
			return new IteratorA<>(this.items, this.offset, this.length);
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
			this.items.removeImpl(this.offset, this.length);
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
			final int offset = this.offset, result = this.items.firstIndexOfImpl(object, offset, this.length);
			return result >= 0 ? result + offset : -1;
		}

		/** {@inheritDoc} */
		@Override
		public int lastIndexOf(final Object object) {
			final int offset = this.offset, result = this.items.lastIndexOfImpl(object, offset, this.length);
			return result >= 0 ? result + offset : -1;
		}

		/** {@inheritDoc} */
		@Override
		public ListIterator<GItem> listIterator() {
			return new IteratorA<>(this.items, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public ListIterator<GItem> listIterator(final int index) {
			final IteratorA<GItem> result = new IteratorA<>(this.items, this.offset, this.length);
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
			return this.items.hashCodeImpl(this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(final Object object) {
			return this.items.equalsImpl(object, this.offset, this.length);
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return this.items.toStringImpl(this.offset, this.length);
		}

	}

	/** Dieses Feld speichert das serialVersionUID. */
	private static final long serialVersionUID = 6729907505658682274L;

	/** Dieses Feld speichert den initialwert für {@link #items}. */
	static final Object[] EMPTY_OBJECTS = {};

	/** Dieses Feld speichert die maximale Kapazität. */
	static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

	transient Object[] items;

	transient int length;

	transient int offset;

	public ArrayList_() {
		this.items = ArrayList_.EMPTY_OBJECTS;
	}

	public ArrayList_(final int capacity) {
		this.items = capacity != 0 ? new Object[capacity] : ArrayList_.EMPTY_OBJECTS;
	}

	{}

	static int calcPos(final Object[] array, final int offset, final int index) {
		final int min = offset + index, max = array.length;
		return min >= max ? min - max : min;
	}

	/** Diese Methode gibt das Element an der gegebenen Position zurück.<br>
	 * <b>Achtung:</b> Es erfolgt keine Bereichsprüfung!
	 *
	 * @param index Position.
	 * @return Element. */
	protected final GItem getImpl(final int index) {
		final Object[] array = this.items;
		@SuppressWarnings ("unchecked")
		final GItem result = (GItem)array[ArrayList_.calcPos(array, this.offset, index)];
		return result;
	}

	/** Diese Methode ersetzt das Element an der gegebenen Position und gibt alten Wert zurück.<br>
	 * <b>Achtung:</b> Es erfolgt keine Bereichsprüfung!
	 *
	 * @param index Position.
	 * @param item neses Element.
	 * @return altes Element. */
	protected final GItem setImpl(final int index, final GItem item) {
		final Object[] array = this.items;
		final int pos = ArrayList_.calcPos(array, this.offset, index);
		@SuppressWarnings ("unchecked")
		final GItem result = (GItem)array[pos];
		array[pos] = item;
		return result;
	}

	/** Diese Methode fügt das Element an der gegebenen Position ein.
	 *
	 * @param index Position, an der das Element eingefügt werden soll.
	 * @param item Element.
	 * @return {@code true}, wenn das Element eingefügt werden konnte. */
	protected final boolean offerImpl(final int index, final GItem item) throws IndexOutOfBoundsException {
		if (!this.insertImpl(index, 1)) return false;
		final Object[] array = this.items;
		array[ArrayList_.calcPos(array, this.offset, index)] = item;
		return true;
	}

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

	/** Diese Methode gibt die kleinste Position des gegebenen Elements innerhalb des gegebenen Abschnitts zurück.<br>
	 * Achtung: Es erfolgt keine Prüfung von {@code offset} und {@code length}.
	 *
	 * @param object gesuchtes Element.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Position des ersten Treffers relativ zum Abschnitt oder {@code -1}. */
	protected final int firstIndexOfImpl(final Object object, final int offset, final int length) {
		final Object[] array = this.items;
		final int capacity = array.length;
		final int min = this.offset + offset;
		int max = min + length;
		if (max <= capacity) {
			for (int i = min; i < max; i++) {
				if (Objects.equals(array[i], object)) return i - min;
			}
		} else {
			for (int i = min; i < capacity; i++) {
				if (Objects.equals(array[i], object)) return i - min;
			}
			max -= capacity;
			for (int i = 0; i < max; i++) {
				if (Objects.equals(array[i], object)) return (i - min) + capacity;
			}
		}
		return -1;
	}

	/** Diese Methode gibt die größte Position des gegebenen Elements innerhalb des gegebenen Abschnitts zurück.<br>
	 * Achtung: Es erfolgt keine Prüfung von {@code offset} und {@code length}.
	 *
	 * @param item gesuchtes Element.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Position des ersten Treffers relativ zum Abschnitt oder {@code -1}. */
	protected final int lastIndexOfImpl(final Object item, final int offset, final int length) {
		final Object[] array = this.items;
		final int capacity = array.length;
		final int min = this.offset + offset;
		final int max = min + length;
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

	/** Diese Methode kopiert alle Elemente des gegebenen Abschnitts in das gegebene Array und gibt dieses zurück. Wenn das gegebene Array hierfür zuklein ist,
	 * wird ein neues mit passender Größe erzeugt. Überflüssige Bereiche werden mit {@code null} gefüllt.
	 *
	 * @param <T> Typ der Elemente im gegebenen Array.
	 * @param result gegebene Array.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return gegebenes oder neues Array. */
	@SuppressWarnings ("unchecked")
	protected final <T> T[] toArrayImpl(T[] result, final int offset, final int length) {
		final Object[] array = this.items;
		final int capacity = array.length;
		final int min = this.offset + offset;
		int max = min + length, j = 0;
		if (result.length < length) {
			final Class<? extends Object[]> clazz = result.getClass();
			result = (T[])(clazz == Object[].class ? new Object[length] : Array.newInstance(clazz.getComponentType(), length));
		}
		if (max <= capacity) {
			for (int i = min; i < max; i++, j++) {
				result[j] = (T)array[i];
			}
		} else {
			for (int i = min; i < capacity; i++, j++) {
				result[j] = (T)array[i];
			}
			max -= capacity;
			for (int i = 0; i < max; i++, j++) {
				result[j] = (T)array[i];
			}
		}
		for (int i = result.length; j < i;) {
			result[--i] = null;
		}
		return result;
	}

	final void checkIntern(final int index) {
		if ((index < 0) || (index >= this.length)) throw new IndexOutOfBoundsException();
	}

	final void checkExtern(final int index) {
		if ((index < 0) || (index > this.length)) throw new IndexOutOfBoundsException();
	}

	/** Diese Methode implementiert {@link #hashCode()} für den gegebenen Abschnitt.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Streuwert. */
	protected final int hashCodeImpl(final int offset, final int length) {
		final ListIterator<GItem> iter = new IteratorA<>(this, offset, length);
		int result = 1;
		while (iter.hasNext()) {
			result = (31 * result) + Objects.hash(iter.next());
		}
		return result;
	}

	/** Diese Methode implementiert {@link #equals(Object)} für den gegebenen Abschnitt.
	 *
	 * @param object Objekt zum Vergleich.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Vergleichswert. */
	protected final boolean equalsImpl(final Object object, final int offset, final int length) {
		if (object == this) return true;
		if (!(object instanceof List<?>)) return false;
		final List<?> that = (List<?>)object;
		if (that.size() != length) return false;
		final Iterator<?> thisIter = new IteratorA<>(this, offset, length), thatIter = that.iterator();
		while (thisIter.hasNext() && thatIter.hasNext()) {
			if (!Objects.equals(thisIter.next(), thatIter.next())) return false;
		}
		return thisIter.hasNext() == thatIter.hasNext();
	}

	/** Diese Methode implementiert {@link #toString()} für den gegebenen Abschnitt.
	 *
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Textdarstellung. */
	protected final String toStringImpl(final int offset, final int length) {
		final ListIterator<GItem> iter = new IteratorA<>(this, offset, length);
		if (!iter.hasNext()) return "[]";
		final StringBuilder result = new StringBuilder().append('[').append(iter.next());
		while (iter.hasNext()) {
			result.append(", ").append(iter.next());
		}
		return result.append(']').toString();
	}

	/** Diese Methode implementiert {@link #contains(Object)} für den gegebenen Abschnitt.
	 *
	 * @param object gesuchtes Element.
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Enthaltensein. */
	protected final boolean containsImpl(final Object object, final int offset, final int length) {
		return this.firstIndexOfImpl(object, offset, length) >= 0;
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

	protected void allocateImpl(final int count) {
		final int length = this.length;
		if (count < length) throw new IllegalArgumentException();
		if (!this.insertImpl(length, count - this.items.length)) throw new OutOfMemoryError();
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

	protected void removeImpl(final int offset2, final int length) {
	}

	@SuppressWarnings ({"unchecked", "javadoc"})
	private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
		final int count = stream.readInt();
		if (count == 0) return;
		final Object[] _items = new Object[count];
		this.insertImpl(0, count);
		for (int i = 0; i < count; i++) {
			this.items[i] = stream.readObject();
		}
	}

	@SuppressWarnings ("javadoc")
	private void writeObject(final ObjectOutputStream stream) throws IOException {
		stream.writeInt(this.length);
		int max = Math.max(this.offset + this.length, this.items.length);
		for (int i = this.offset; i < max; i++) {
			stream.writeObject(this.items[i]);
		}
		max = Math.max((this.offset + this.length) - this.items.length, 0);
		for (int i = 0; i < max; i++) {
			stream.writeObject(this.items[i]);
		}
	}

	{}

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
	public GItem pollFirst() {
		final int count = this.length;
		if (count == 0) return null;
		final GItem result = this.getImpl(0);
		this.removeImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem pollLast() {
		final int index = this.length - 1;
		if (index < 0) return null;
		final GItem result = this.getImpl(index);
		this.removeImpl(index, 1);
		return result;
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
	public GItem removeFirst() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(0);
		this.removeImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem removeLast() {
		final int index = this.length - 1;
		if (index < 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(index);
		this.removeImpl(index, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeFirstOccurrence(final Object item) {
		final int index = this.firstIndexOfImpl(item, 0, this.length);
		if (index < 0) return false;
		this.removeImpl(index, 1);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeLastOccurrence(final Object item) {
		final int index = this.lastIndexOfImpl(item, 0, this.length);
		if (index < 0) return false;
		this.removeImpl(index, 1);
		return true;
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
	public GItem poll() {
		final int count = this.length;
		if (count == 0) return null;
		final GItem result = this.getImpl(0);
		this.removeImpl(0, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public GItem pop() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(0);
		this.removeImpl(0, 1);
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
	public boolean offer(final GItem item) {
		return this.offerImpl(this.length, item);
	}

	/** {@inheritDoc} */
	@Override
	public void push(final GItem item) {
		if (!this.offerImpl(0, item)) throw new IllegalStateException();
	}

	/** {@inheritDoc} */
	@Override
	public GItem remove() {
		final int count = this.length;
		if (count == 0) throw new NoSuchElementException();
		final GItem result = this.getImpl(0);
		this.removeImpl(0, 1);
		return result;
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
		return new IteratorA<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<GItem> descendingIterator() {
		return new IteratorD<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<GItem> listIterator() {
		return new IteratorA<>(this);
	}

	/** {@inheritDoc} */
	@Override
	public ListIterator<GItem> listIterator(final int index) {
		final IteratorA<GItem> result = new IteratorA<>(this);
		result.skip(index);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.length;
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.removeImpl(0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.length == 0;
	}

	/** {@inheritDoc} */
	@Override
	public int indexOf(final Object item) {
		return this.firstIndexOfImpl(item, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public int lastIndexOf(final Object item) {
		return this.lastIndexOfImpl(item, 0, this.length);
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
	public boolean retainAll(final Collection<?> items) {
		return Iterables.retainAll((Iterable<?>)this, items);
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(final Collection<?> items) {
		return Iterables.removeAll((Iterable<?>)this, items);
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

	{}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(final Collection<? extends GItem> item) {
		return this.addAllImpl(this.length, item);
	}

	@Override
	public boolean addAll(final int index, final Collection<? extends GItem> item) {
		return this.addAllImpl(index, item);
	}

	static void clearArray(final Object[] array, final int offset, final int length) {
		Arrays.fill(array, offset, offset + length, null);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings ("unchecked")
	public GItem get(final int index) {
		this.checkIntern(index);
		final Object[] array = this.items;
		final int offset = this.offset + index, capacity = array.length;
		return (GItem)array[offset >= capacity ? offset - capacity : offset];
	}

	@Override
	public GItem set(final int index, final GItem element) {
		this.checkIntern(index);
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public GItem remove(final int index) {
		this.checkIntern(index);
		final GItem result = this.getImpl(index);
		this.removeImpl(index, 1);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(final Object object) {
		final int index = this.lastIndexOfImpl(object, 0, this.length);
		if (index < 0) return false;
		this.removeImpl(index, 1);
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public List<GItem> subList(final int fromIndex, final int toIndex) {
		return new SectionList<>(this, fromIndex, toIndex - fromIndex);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.hashCodeImpl(0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(final Object object) {
		return this.equalsImpl(object, 0, this.length);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.toStringImpl(0, this.length);
	}

}
