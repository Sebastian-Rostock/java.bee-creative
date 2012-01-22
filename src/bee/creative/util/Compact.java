package bee.creative.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;

public final class Compact {

	/**
	 * Diese Klasse implementiert eine abstrakte Sammlung von Elementen, die in einem sortierten {@link Array Array}
	 * verwaltet werden.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class CompactList {

		/**
		 * Diese Klasse implementiert den {@link Iterator} der Elemente.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		protected abstract class CompactItemIterator<GItem> implements Iterator<GItem> {

			/**
			 * Dieses Feld speichert den Index des nächsten Elements.
			 */
			private int next = 0;

			/**
			 * Dieses Feld speichert den Index des letzten Elements.
			 */
			private int last = -1;

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
			public final boolean hasNext() {
				return this.next < CompactList.this.size;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final GItem next() {
				final int next = this.next;
				this.last = next;
				final GItem item = this.next(next);
				this.next = next + 1;
				return item;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public final void remove() {
				if(this.last < 0) throw new IllegalStateException();
				CompactList.this.removeItems(this.last, 1);
				this.last = -1;
			}

		}

		/**
		 * Dieses Feld speichert das leere {@link Array Array} der Elemente.
		 */
		protected static final Object[] ITEMS = new Object[0];

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		protected int size;

		/**
		 * Dieses Feld speichert die Elemente.
		 */
		protected Object[] items = CompactList.ITEMS;

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der gegebene Schlüssel {@link Object#equals(Object)
		 * äquivalent} dem Schlüssel des gegebenen Elements ist.
		 * 
		 * @see Object#hashCode()
		 * @see Object#equals(Object)
		 * @see Objects#hash(Object)
		 * @see Objects#equals(Object, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels oder {@code 0}.
		 * @param item Element.
		 * @return {@link Object#equals(Object) Äquivalenz} der Schlüssel.
		 */
		protected abstract boolean equals(Object key, int hash, Object item);

		/**
		 * Diese Methode sucht zuerst binär und danach linear nach einem Eintrag, dessen Schlüssel gleich dem gegebenen
		 * Schlüssel ist und gibt den Index dieses Elements oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die
		 * <i>Einfügeposition</i> ist der Index, bei dem der Eintrag eingefügt werden müsste. Ein Element {@code element}
		 * ist dann zum gegebenen Schlüssel gleich, wenn
		 * {@code (itemCompare(key, hash, element) == 0) && itemEquals(key, hash, element)}.
		 * 
		 * @see CompactList#equals(Object, int, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected int equalsSearch(final Object key, final int hash) {
			Object item;
			final int index = this.compareSearch(key, hash);
			if(index < 0) return index;
			final Object[] items = this.items;
			if(this.equals(key, hash, items[index])) return index;
			for(int next = index + 1, last = this.size; (next < last) && (this.compare(key, hash, item = items[next]) == 0); next++){
				if(this.equals(key, hash, item)) return next;
			}
			for(int next = index - 1; (0 <= next) && (this.compare(key, hash, item = items[next]) == 0); next--){
				if(this.equals(key, hash, item)) return next;
			}
			return -(index + 1);
		}

		/**
		 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn der gegebene Schlüssel
		 * kleiner, gleich bzw. größer als der Schlüssel des gegebenen Elements ist. Die Berechnung kann auf den Schlüsseln
		 * selbst oder ihren {@link Object#hashCode() Streuwerten} beruhen.
		 * 
		 * @see Integer#compare(int, int)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @param item Element.
		 * @return {@link Comparator#compare(Object, Object) Vergleichswert} der der Schlüssel.
		 */
		protected abstract int compare(Object key, int hash, Object item);

		/**
		 * Diese Methode sucht benär nach einem Eintrag, dessen Schlüssel gleich dem gegebenen Schlüssel ist und gibt dessen
		 * Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück. Die <i>Einfügeposition</i> ist der Index, bei dem
		 * der Eintrag eingefügt werden müsste.
		 * 
		 * @see CompactList#compare(Object, int, Object)
		 * @param key Schlüssel.
		 * @param hash {@link Object#hashCode() Streuwert} des Schlüssels.
		 * @return Index des Eintrags oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected int compareSearch(final Object key, final int hash) {
			int from = 0, last = this.size;
			final Object[] items = this.items;
			while(from < last){
				final int next = (from + last) >>> 1;
				final int comp = this.compare(key, hash, items[next]);
				if(comp < 0){
					last = next;
				}else if(comp > 0){
					from = next + 1;
				}else return next;
			}
			return -(from + 1);
		}

		/**
		 * Diese Methode fügt die gegebene Anzahl an Einträgen ab dem gegebenen Index in das {@link Array Array} ein.
		 * 
		 * @see CompactList#validInsertLength(int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void insertItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			if((index < 0) || (index > size)) throw new IllegalArgumentException("Index out of range: " + index);
			if(count < 0) throw new IllegalArgumentException("Count out of range: " + count);
			if(count == 0) return;
			final Object[] oldItems = this.items;
			final int oldLength = oldItems.length;
			final int newLength = this.validInsertLength(size + count);
			if(oldLength != newLength){
				final Object[] newItems = new Object[newLength];
				System.arraycopy(oldItems, 0, newItems, 0, index);
				System.arraycopy(oldItems, index, newItems, index + count, size - index);
				this.items = newItems;
			}else{
				System.arraycopy(oldItems, index, oldItems, index + count, size - index);
			}
			this.size = size + count;
		}

		/**
		 * Diese Methode entfernt die gegebene Anzahl an Einträgen ab dem gegebenen Index aus dem {@link Array Array} mit
		 * der gegebenen Länge der Belegung.
		 * 
		 * @see CompactList#validRemoveLength(int)
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn der gegebene Index bzw. die gegebene Anzahl ungültig sind.
		 */
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			if((index < 0) || (index > size)) throw new IllegalArgumentException("Index out of range: " + index);
			if((count < 0) || (count > size)) throw new IllegalArgumentException("Count out of range: " + count);
			if(count == 0) return;
			final Object[] oldItems = this.items;
			final int newSize = size - count;
			final int oldLength = oldItems.length;
			final int newLength = this.validRemoveLength(newSize);
			if(oldLength != newLength){
				if(newLength != 0){
					final Object[] newItems = new Object[newLength];
					System.arraycopy(oldItems, 0, newItems, 0, index);
					System.arraycopy(oldItems, index + count, newItems, index, newSize - index);
					this.items = newItems;
				}else{
					this.items = CompactList.ITEMS;
				}
			}else{
				System.arraycopy(oldItems, index + count, oldItems, index, newSize - index);
			}
			this.size = newSize;
		}

		/**
		 * Diese Methode vergrößert die Kapazität des {@link Array Arrays} der Elemente, sodass dieses die gegebene Anzahl
		 * an Elementen verwalten kann.
		 * 
		 * @see CompactList#validAllocateLength(int)
		 * @param count Anzahl.
		 */
		protected void allocateItems(final int count) {
			final Object[] items = this.items;
			final int oldLength = items.length;
			final int newLength = this.validAllocateLength(count);
			if(oldLength == newLength) return;
			if(newLength != 0){
				System.arraycopy(items, 0, this.items = new Object[newLength], 0, this.size);
			}else{
				this.items = CompactList.ITEMS;
			}
		}

		/**
		 * Diese Methode verkleinert die Kapazität des {@link Array Arrays} der Elemente auf das Minimum.
		 * 
		 * @see CompactList#validCompactLength()
		 */
		protected void compactItems() {
			final Object[] items = this.items;
			final int oldLength = items.length;
			final int newLength = this.validCompactLength();
			if(oldLength == newLength) return;
			if(newLength != 0){
				System.arraycopy(items, 0, this.items = new Object[newLength], 0, newLength);
			}else{
				this.items = CompactList.ITEMS;
			}
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können.
		 * 
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validLength(final int count) {
			final int oldLength = this.items.length;
			if(oldLength >= count) return oldLength;
			final int newLength = oldLength + (oldLength >> 1);
			if(newLength >= count) return newLength;
			return count;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Einfügen von Elementen aufgerufen.
		 * 
		 * @see CompactList#insertItems(int, int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validInsertLength(final int count) {
			return this.validLength(count);
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Entfernen von Elementen aufgerufen.
		 * 
		 * @see CompactList#removeItems(int, int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validRemoveLength(final int count) {
			return this.items.length;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin alle vorhandenen
		 * Elemente verwalten zu können. Sie wird beim Kompaktieren aufgerufen.
		 * 
		 * @see CompactList#size
		 * @see CompactList#compactItems()
		 * @return Länge.
		 */
		protected int validCompactLength() {
			return this.size;
		}

		/**
		 * Diese Methode gibt die neue Länge für das {@link Array Array} der Elemente zurück, um darin die gegebene Anzahl
		 * an Elementen verwalten zu können. Sie wird beim Reservieren von Elementen aufgerufen.
		 * 
		 * @see CompactList#allocateItems(int)
		 * @param count Anzahl.
		 * @return Länge.
		 */
		protected int validAllocateLength(final int count) {
			return this.validLength(count);
		}

		/**
		 * Diese Methode vergrößert die Kapazität, sodass dieses die gegebene Anzahl an Elementen verwalten kann.
		 * 
		 * @param count Anzahl.
		 */
		public void allocate(final int count) {
			this.allocateItems(count);
		}

		/**
		 * Diese Methode verkleinert die Kapazität auf das Minimum.
		 */
		public void compact() {
			this.compactItems();
		}

	}

	public static void main(final String[] args) {

		final List<Object> c = new ArrayList<Object>();
		c.addAll(Arrays.asList(Hash.class.getFields()));
		c.addAll(Arrays.asList(Hash.class.getMethods()));
		c.addAll(Arrays.asList(Arrays.class.getFields()));
		c.addAll(Arrays.asList(Arrays.class.getMethods()));

		final Set<Object> set1 = new CompactHashSetEx<Object>();
		final Set<Object> set2 = new HashSet<Object>();

		final Runnable runnable1 = new Runnable() {

			@Override
			public void run() {
				set1.addAll(c);
			}

		};
		final Runnable runnable2 = new Runnable() {

			@Override
			public void run() {
				set2.addAll(c);
			}

		};

		runnable1.run();
		runnable2.run();

		set1.clear();
		set2.clear();

		System.out.println(new Tester(runnable1));
		System.out.println(new Tester(runnable2));

		final List l1 = new ArrayList(set1);
		final List l2 = new ArrayList(set2);
		System.out.println(l1.size());
		System.out.println(l2.size());

		for(final Object i: l2){
			l1.remove(i);
		}

		System.out.println(l1);
		System.out.println(l1.size());

		System.out.println(set1.containsAll(set2));
		System.out.println(set2.containsAll(set1));

		System.out.println(Objects.toString(c.size()));
		System.out.println(Objects.toString(set1.size()));
		System.out.println(Objects.toString(set2.size()));

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Set Set}, dessen Daten von einem {@link Array Array} verwaltet
	 * werden.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <E> Typ der Elemente.
	 */
	public static abstract class CompactSet<E> extends CompactList implements Set<E> {

		/**
		 * Diese Klasse implementiert den {@link Iterator Iterator}.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactSetItemIterator extends CompactItemIterator<E> {

			/**
			 * {@inheritDoc}
			 */
			@SuppressWarnings ("unchecked")
			@Override
			protected E next(final int index) {
				return (E)CompactSet.this.items[index];
			}

		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set}.
		 */
		public CompactSet() {
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection Elemente.
		 */
		public CompactSet(final Collection<? extends E> collection) {
			this.allocate(collection.size());
			this.addAll(collection);
		}

		/**
		 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
		 * 
		 * @see AbstractSet
		 * @return {@link Set Set}.
		 */
		protected final Set<E> itemSet() {
			return new AbstractSet<E>() {

				@Override
				public Iterator<E> iterator() {
					return CompactSet.this.iterator();
				}

				@Override
				public int size() {
					return CompactSet.this.size();
				}

			};
		}

		/**
		 * Diese Methode sucht nach dem gegebenen Schlüssel und gibt dessen Index oder
		 * <code>(-(<i>Einfügeposition</i>) - 1)</code> zurück.
		 * 
		 * @see CompactList#equalsSearch(Object, int)
		 * @see CompactList#compareSearch(Object, int)
		 * @param key Schlüssel.
		 * @return Index oder <code>(-(<i>Einfügeposition</i>) - 1)</code>.
		 */
		protected abstract int itemSearch(final Object key);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.removeItems(0, this.size);
			this.compact();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<E> iterator() {
			return new CompactSetItemIterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.size == 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final E item) {
			int index = this.itemSearch(item);
			if(index >= 0) return false;
			index = -index - 1;
			this.insertItems(index, 1);
			this.items[index] = item;
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends E> collection) {
			boolean modified = false;
			for(final E item: collection)
				if(this.add(item)){
					modified = true;
				}
			return modified;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object item) {
			final int index = this.itemSearch(item);
			if(index < 0) return false;
			this.removeItems(index, 1);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> collection) {
			boolean modified = false;
			for(final Iterator<?> iterator = this.iterator(); iterator.hasNext();){
				if(collection.contains(iterator.next())){
					iterator.remove();
					modified = true;
				}
			}
			return modified;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> collection) {
			boolean modified = false;
			for(final Iterator<?> iterator = this.iterator(); iterator.hasNext();){
				if(!collection.contains(iterator.next())){
					iterator.remove();
					modified = true;
				}
			}
			return modified;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object key) {
			return this.itemSearch(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> collection) {
			for(final Object item: collection)
				if(!this.contains(item)) return false;
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object[] toArray() {
			return this.itemSet().toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public <T> T[] toArray(final T[] a) {
			return this.itemSet().toArray(a);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.itemSet().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Set<?>)) return false;
			return this.itemSet().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.itemSet().toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Object#hashCode() Streuwert} basiertes {@link CompactSet Compact-Set}. Diese
	 * 
	 * @see Object#hashCode()
	 * @see Object#equals(Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <E> Typ der Elemente.
	 */
	public static class CompactHashSet<E> extends CompactSet<E> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set}.
		 */
		public CompactHashSet() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection Elemente.
		 */
		public CompactHashSet(final Collection<? extends E> collection) {
			super(collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemSearch(final Object key) {
			if(key == null) return this.equalsSearch(null, 0);
			return this.equalsSearch(key, key.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			if(key == null) return item == null;
			return key.equals(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			if(item == null) return hash;
			return Integer.compare(hash, item.hashCode());
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Object#hashCode() Streuwert} basiertes {@link CompactSet Compact-Set}. Das
	 * Verhalten dieser Implementation weicht von dem eines {@link HashSet Hash-Sets} ab, wenn {@link Array Arrays} als
	 * Elemente verwendet werden, da deren {@link Object#hashCode() Streuwerte} und {@link Object#equals(Object)
	 * Äquivalenzen} anders ermittelt werden, als in einem {@link HashSet Hash-Set}.
	 * 
	 * @see Objects#hash(Object)
	 * @see Objects#equals(Object, Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <E> Typ der Elemente.
	 */
	public static class CompactHashSetEx<E> extends CompactSet<E> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set}.
		 */
		public CompactHashSetEx() {
			super();
		}

		/**
		 * Dieser Konstrukteur initialisiert das {@link Set} mit den gegebenen Elementen.
		 * 
		 * @see Set#addAll(Collection)
		 * @param collection Elemente.
		 */
		public CompactHashSetEx(final Collection<? extends E> collection) {
			super(collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemSearch(final Object key) {
			return this.equalsSearch(key, Objects.hash(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			return Objects.equals(key, item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			return Integer.compare(hash, Objects.hash(item));
		}

	}

	public abstract static class CompactMap<K, V> extends CompactList implements Map<K, V> {

		/**
		 * Diese Klasse implementiert den {@link Iterator Iterator} der Schlüssel.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactMapKeyIterator extends CompactItemIterator<K> {

			/**
			 * {@inheritDoc}
			 */
			@SuppressWarnings ("unchecked")
			@Override
			protected K next(final int index) {
				return CompactMap.this.getKey(index);
			}

		}

		/**
		 * Diese Klasse implementiert den {@link Iterator Iterator} der Werte.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactMapValueIterator extends CompactItemIterator<V> {

			/**
			 * {@inheritDoc}
			 */
			@SuppressWarnings ("unchecked")
			@Override
			protected V next(final int index) {
				return CompactMap.this.getValue(index);
			}

		}

		/**
		 * Diese Klasse implementiert den {@link Iterator Iterator} der {@link Entry Entries}.
		 * 
		 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		protected class CompactMapEntryIterator extends CompactItemIterator<Entry<K, V>> {

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected Entry<K, V> next(final int index) {
				return new SimpleEntry<K, V>(CompactMap.this.getKey(index), CompactMap.this.getValue(index)) {

					private static final long serialVersionUID = -2184170070616433736L;

					@Override
					public V setValue(final V value) {
						final V v = super.setValue(value);
						CompactMap.this.setValue(index, value);
						return v;
					}

				};
			}

		}

		/**
		 * Diese Methode gibt ein neues {@link Set Set} zurück, das aus dem {@link Iterator Iterator} erzeugt wird.
		 * 
		 * @see AbstractSet
		 * @return {@link Set Set}.
		 */
		protected final Map<K, V> itemMap() {
			return new AbstractMap<K, V>() {

				Set<Entry<K, V>> entrySet = CompactMap.this.entrySet();

				@Override
				public Set<Entry<K, V>> entrySet() {
					return this.entrySet;
				}

			};
		}

		protected abstract int itemSearch(final Object key);

		protected abstract K getKey(int index);

		protected abstract V getValue(int index);

		protected abstract void setValue(int index, V value);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.size;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.removeItems(0, this.size);
			this.compact();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<V> values() {
			return new AbstractCollection<V>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public Iterator<V> iterator() {
					return new CompactMapValueIterator();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<K> keySet() {
			return new AbstractSet<K>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public Iterator<K> iterator() {
					return new CompactMapKeyIterator();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<K, V>> entrySet() {
			return new AbstractSet<Entry<K, V>>() {

				@Override
				public int size() {
					return CompactMap.this.size;
				}

				@Override
				public Iterator<Entry<K, V>> iterator() {
					return new CompactMapEntryIterator();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.size == 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsKey(final Object key) {
			return this.itemSearch(key) >= 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V get(final Object key) {
			final int index = this.itemSearch(key);
			if(index < 0) return null;
			return this.getValue(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V put(final K key, final V value) {
			int index = this.itemSearch(key);
			if(index >= 0){
				final V item = this.getValue(index);
				this.setValue(index, value);
				return item;
			}
			index = -index - 1;
			this.insertItems(index, 1);
			this.setValue(index, value);
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void putAll(final Map<? extends K, ? extends V> map) {
			for(final Entry<? extends K, ? extends V> entry: map.entrySet()){
				this.put(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V remove(final Object key) {
			final int index = this.itemSearch(key);
			if(index < 0) return null;
			final V item = this.getValue(index);
			this.removeItems(index, 1);
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.itemMap().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof Map<?, ?>)) return false;
			return this.itemMap().equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.itemMap().toString();
		}

	}

	public static abstract class CompactSetMap<K, V> extends CompactMap<K, V> {

		
		
	}

	public static abstract class CompactValueMap<K, V> extends CompactMap<K, V> {

		protected Object[] values = CompactList.ITEMS;

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void insertItems(final int index, final int count) throws IllegalArgumentException {
			final int size = this.size;
			super.insertItems(index, count);
			final Object[] oldValues = this.values;
			final int oldLength = oldValues.length;
			final int newLength = this.items.length;
			if(oldLength != newLength){
				final Object[] newValues = new Object[newLength];
				System.arraycopy(oldValues, 0, newValues, 0, index);
				System.arraycopy(oldValues, index, newValues, index + count, size - index);
				this.values = newValues;
			}else{
				System.arraycopy(oldValues, index, oldValues, index + count, size - index);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeItems(final int index, final int count) throws IllegalArgumentException {
			super.removeItems(index, count);
			final int size = this.size;
			final Object[] oldValues = this.values;
			final int oldLength = oldValues.length;
			final int newLength = this.items.length;
			if(oldLength != newLength){
				if(newLength != 0){
					final Object[] newValues = new Object[newLength];
					System.arraycopy(oldValues, 0, newValues, 0, index);
					System.arraycopy(oldValues, index + count, newValues, index, size - index);
					this.values = newValues;
				}else{
					this.values = CompactList.ITEMS;
				}
			}else{
				System.arraycopy(oldValues, index + count, oldValues, index, size - index);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void allocateItems(final int count) {
			super.allocateItems(count);
			this.resizeValues();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void compactItems() {
			super.compactItems();
			this.resizeValues();
		}

		protected void resizeValues() {
			final int length = this.items.length;
			final Object[] values = this.values;
			if(values.length == length) return;
			if(length != 0){
				System.arraycopy(values, 0, this.values = new Object[length], 0, this.size);
			}else{
				this.values = CompactList.ITEMS;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected K getKey(final int index) {
			return (K)this.items[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected V getValue(final int index) {
			return (V)this.values[index];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void setValue(final int index, final V value) {
			this.values[index] = value;
		}

	}

	static class CompactSortedSet<E> extends CompactSet<E> implements NavigableSet<E> {

		protected class CompactSortedSubSet extends AbstractSet<E> implements SortedSet<E> {

			Object fromItem;

			Object lastItem;

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Comparator<? super E> comparator() {
				return CompactSortedSet.this.comparator;
			}

			@Override
			public SortedSet<E> subSet(final E fromElement, final E toElement) {
				return null;
			}

			@Override
			public SortedSet<E> headSet(final E toElement) {

				return null;
			}

			@Override
			public SortedSet<E> tailSet(final E fromElement) {
				return null;
			}

			@Override
			public E first() {

				return null;
			}

			@Override
			public E last() {
				return null;
			}

			@Override
			public Iterator<E> iterator() {
				return null;
			}

			@Override
			public int size() {
				return 0;
			}

		}

		/**
		 * Dieses Feld speichert den {@link Comparator Comparator}.
		 */
		protected final Comparator<? super E> comparator;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Comparator Comparator}.
		 * 
		 * @param comparator {@link Comparator Comparator}.
		 */
		public CompactSortedSet(final Comparator<? super E> comparator) {
			super();
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		public CompactSortedSet(final Collection<? extends E> collection, final Comparator<? super E> comparator) {
			super(collection);
			if(comparator == null) throw new NullPointerException("Comparator is null");
			this.comparator = comparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected int itemSearch(final Object key) {
			return this.compareSearch(key, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected boolean equals(final Object key, final int hash, final Object item) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		protected int compare(final Object key, final int hash, final Object item) {
			return this.comparator.compare((E)key, (E)item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Comparator<? super E> comparator() {
			return this.comparator;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public E first() {
			if(this.size == 0) throw new NoSuchElementException();
			return (E)this.items[0];
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public E last() {
			final int size = this.size;
			if(size == 0) throw new NoSuchElementException();
			return (E)this.items[size - 1];
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedSet<E> subSet(final E fromElement, final E toElement) {
			return this.subSet(fromElement, true, toElement, false);
		}

		@Override
		public NavigableSet<E> subSet(final E fromElement, final boolean fromInclusive, final E toElement,
			final boolean toInclusive) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedSet<E> headSet(final E toElement) {
			return this.headSet(toElement, false);
		}

		@Override
		public NavigableSet<E> headSet(final E toElement, final boolean inclusive) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SortedSet<E> tailSet(final E fromElement) {
			return this.tailSet(fromElement, true);
		}

		@Override
		public NavigableSet<E> tailSet(final E fromElement, final boolean inclusive) {
			return null;
		}

		@Override
		public E lower(final E e) {
			return null;
		}

		@Override
		public E floor(final E e) {
			return null;
		}

		@Override
		public E ceiling(final E e) {
			return null;
		}

		@Override
		public E higher(final E e) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public E pollFirst() {
			if(this.size == 0) return null;
			final Object item = this.items[0];
			this.removeItems(0, 1);
			return (E)item;
		}

		@Override
		public E pollLast() {
			return null;
		}

		@Override
		public NavigableSet<E> descendingSet() {
			return null;
		}

		@Override
		public Iterator<E> descendingIterator() {
			return null;
		}

	}

}
