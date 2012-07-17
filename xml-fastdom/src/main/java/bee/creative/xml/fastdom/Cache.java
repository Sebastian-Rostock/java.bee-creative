package bee.creative.xml.fastdom;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Get;

public abstract class Cache<GItem> implements Iterable<GItem> {

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung einer Teilmenge von Elementen.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class CachePage {

		static final int PAGE_BITS = 7;

		/**
		 * Dieses Feld speichert die maximale Anzahl der Elemente in einer {@link CachePage}.
		 */
		public static final int PAGE_SIZE = 1 << CachePage.PAGE_BITS;

		static final int PAGE_MASK = CachePage.PAGE_SIZE - 1;

		static final CachePage[] VOID_PAGES = {};

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		int size;

		/**
		 * Dieses Feld speichert die Elemente.
		 */
		final Object[] items = new Object[CachePage.PAGE_SIZE];

	}

	/**
	 * Diese Klasse implementiert den {@link Iterator} über die im {@link Cache} enthaltenen Elemente.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Cache
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class CacheIterator<GItem> implements Iterator<GItem> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		final Cache<GItem> owner;

		/**
		 * Dieses Feld speichert die nächste {@link CachePage}.
		 */
		CachePage nextPage;

		/**
		 * Dieses Feld speichert das nächste Element.
		 */
		GItem nextItem;

		/**
		 * Dieses Feld speichert den Index der nächsten {@link CachePage}.
		 */
		int nextPageIndex;

		/**
		 * Dieses Feld speichert den Index des nächsten Elements.
		 */
		int nextItemIndex;

		/**
		 * Dieses Feld speichert den Index der letzten {@link CachePage}.
		 */
		int lastPageIndex;

		/**
		 * Dieses Feld speichert den Index des letzten Elements.
		 */
		int lastItemIndex;

		/**
		 * Dieser Konstrukteur initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @throws NullPointerException Wenn der gegebene Besitzer {@code null} ist.
		 */
		public CacheIterator(final Cache<GItem> owner) throws NullPointerException {
			if(owner == null) throw new NullPointerException("owner is null");
			this.owner = owner;
			nextPageIndex=-1;
			nextItemIndex=-1;
			this.lastPageIndex = -1;
			this.seekPage();
			if(this.nextPage == null) return;
			this.seekItem();
		}

		/**
		 * Diese Methode navigiert zur nächsten {@link CachePage}.
		 */
		private void seekPage() {
			final CachePage[] pages = this.owner.pages;
			for(int i = this.nextPageIndex+1, size = pages.length; i < size; i++){
				final CachePage page = pages[i];
				if(page != null){
					this.nextPage = page;
					this.nextPageIndex = i;
					return;
				}
			}
			this.nextPage = null;
			this.nextPageIndex = -1;
		}

		/**
		 * Diese Methode navigiert zur nächsten Element.
		 */
		@SuppressWarnings ("unchecked")
		private void seekItem() {
			do{
				final Object[] items = this.nextPage.items;
				for(int i = this.nextItemIndex+1, size = items.length; i < size; i++){
					final Object item = items[i];
					if(item != null){
						this.nextItem = (GItem)item;
						this.nextItemIndex = i;
						return;
					}
				}
				this.seekPage();
			}while(this.nextPage != null);
			this.nextItem = null;
			this.nextItemIndex = -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.nextItem != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem next() {
			final GItem item = this.nextItem;
			if(item == null) throw new NoSuchElementException();
			this.lastPageIndex = this.nextPageIndex;
			this.lastItemIndex = this.nextItemIndex;
			this.seekItem();
			return item;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			final int removePageIndex = this.lastPageIndex;
			if(removePageIndex < 0) throw new IllegalStateException();
			final CachePage page = this.owner.pages[removePageIndex];
			if(page == null) throw new IllegalStateException();
			this.owner.size--;
			if(page.size == 1){
				this.owner.pages[removePageIndex] = null;
			}else{
				page.size--;
				page.items[this.lastItemIndex] = null;
			}
			this.lastPageIndex = -1;
		}

	}

	static public final class CacheGetAll<GItem> implements Get<GItem> {

		final Cache<GItem> owner;

		public CacheGetAll(final Cache<GItem> owner) {
			this.owner = owner;
		}

		@Override
		public GItem get(final int index) throws IndexOutOfBoundsException {
			return this.owner.get(index);
		}

	}

	static public final class CacheGetSection<GItem> implements Get<GItem> {

		final Cache<GItem> owner;

		final int[] indices;

		public CacheGetSection(final Cache<GItem> owner, final int[] indices) {
			this.owner = owner;
			this.indices = indices;
		}

		@Override
		public GItem get(final int index) throws IndexOutOfBoundsException {
			return this.owner.get(this.indices[index]);
		}

	}

	/**
	 * Dieses Feld speichert die Anzahl der Elemente.
	 */
	int size;

	/**
	 * Dieses Feld speichert die {@link CachePage}s.
	 */
	CachePage[] pages;

	/**
	 * Dieses Feld speichert die minimale Anzahl der Elemente, auf welche beim Bereinigen zurückgesetzt werden soll.
	 */
	int minSize;

	/**
	 * Dieses Feld speichert die maximale Anzahl.
	 */
	int maxSize;

	/**
	 * Dieses Feld speichert die Kapazität.
	 */
	int capacity;

	/**
	 * Dieser Konstrukteur initialisiert minimale und maximal Anzahl, Kapazität und {@link CacheItemFactory}.
	 * 
	 * @param minSize minimale Anzahl.
	 * @param maxSize maximale Anzahl.
	 * @param capacity Kapazität.
	 * @param factory {@link CacheItemFactory}.
	 * @throws NullPointerException Wenn die gegebene {@link CacheItemFactory} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene minimale Anzahl, die gegebene maximale Anzahl bzw. die gegebene
	 *         Kapazität ungültig sind: {@code (minSize < 0)}, {@code (maxSize < 0)} oder {@code (capacity < 0)}.
	 */
	public Cache(final int minSize, final int maxSize, final int capacity) throws NullPointerException,
		IllegalArgumentException {
		this.pages = CachePage.VOID_PAGES;
		this.setMinSize(minSize);
		this.setMaxSize(maxSize);
		this.setCapacity(capacity);
	}

	/**
	 * Diese Methode erzeugt das {@code index}-te Element und gibt es zurück. Der Rückgabewert {@code null} ist nicht
	 * zulässig.
	 * 
	 * @param index Index.
	 * @return Eleent.
	 */
	public abstract GItem createItem(int index);

	/**
	 * Diese Methode gibt das {@code index}-te Element zurück. Wenn dieses noch nicht existiert, wird es via
	 * {@link #createItem(int)} erzeugt.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Element oder {@code null}.
	 * @throws NullPointerException Wenn das von {@link #createItem(int)} erzeugte Element {@code null} ist.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	@SuppressWarnings ("unchecked")
	public final GItem get(final int index) throws NullPointerException, IndexOutOfBoundsException {
		if(index < 0) throw new IndexOutOfBoundsException("index < 0");
		if(index >= this.capacity) throw new IndexOutOfBoundsException("index >= capacity");
		final int pageIndex = index >> CachePage.PAGE_BITS;
		final int itemIndex = index & CachePage.PAGE_MASK;
		CachePage page = this.pages[pageIndex];
		GItem item;
		if(page != null){
			item = (GItem)page.items[itemIndex];
			if(item != null) return item;
			item = this.createItem(index);
			if(item == null) throw new NullPointerException();
			if(this.size >= this.maxSize){
				this.compact();
				if(page.size == 0){
					Arrays.fill(page.items, null);
					this.pages[pageIndex] = page;
				}
			}
		}else{
			item = this.createItem(index);
			if(item == null) throw new NullPointerException();
			if(this.size >= this.maxSize){
				this.compact();
			}
			page = new CachePage();
			this.pages[pageIndex] = page;
		}
		page.items[itemIndex] = item;
		page.size++;
		this.size++;
		return item;
	}

	/**
	 * Diese Methode sucht binäre nach dem ersten Treffer des gegebenen {@link Comparable}s und gibt diesen oder
	 * {@code null} zurück.
	 * 
	 * @see CacheGetAll
	 * @see Comparables#binarySearch(Get, Comparable, int, int)
	 * @param comparable {@link Comparable}.
	 * @return erster Treffer oder {@code null}.
	 */
	public final GItem find(final Comparable<? super GItem> comparable) {
		final Get<GItem> get = new CacheGetAll<GItem>(this);
		final int index = Comparables.binarySearch(get, comparable, 0, this.capacity);
		if(index < 0) return null;
		return get.get(index);
	}

	/**
	 * Diese Methode sucht binäre im gegebenen Suchraum nach dem ersten Treffer des gegebenen {@link Comparable}s und gibt
	 * diesen oder {@code null} zurück.
	 * 
	 * @param indices Suchraum.
	 * @see CacheGetSection
	 * @see Comparables#binarySearch(Get, Comparable, int, int)
	 * @param comparable {@link Comparable}.
	 * @return erster Treffer oder {@code null}.
	 */
	public final GItem find(final int[] indices, final Comparable<? super GItem> comparable) {
		final Get<GItem> get = new CacheGetSection<GItem>(this, indices);
		final int index = Comparables.binarySearch(get, comparable, 0, indices.length);
		if(index < 0) return null;
		return get.get(index);
	}

	/**
	 * Diese Methode gibt die aktuelle Anzahl der Elemente zurück.
	 * 
	 * @return aktuelle Anzahl der Elemente.
	 */
	public final int size() {
		return this.size;
	}

	/**
	 * Diese Methode entfernt alle Elemente.
	 */
	public final void clear() {
		if(this.size == 0) return;
		Arrays.fill(this.pages, null);
		this.size = 0;
	}

	/**
	 * Diese Methode entfernt das {@code index}-te Element und gibt es oder {@code null} zurück.
	 * 
	 * @param index Index.
	 * @return {@code index}-tes Element oder {@code null}.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	@SuppressWarnings ("unchecked")
	public final GItem clear(final int index) throws IndexOutOfBoundsException {
		if(index < 0) throw new IndexOutOfBoundsException("index < 0");
		if(index >= this.capacity) throw new IndexOutOfBoundsException("index >= capacity");
		final int pageIndex = index >> CachePage.PAGE_BITS;
		final CachePage page = this.pages[pageIndex];
		if(page == null) return null;
		final int itemIndex = index & CachePage.PAGE_MASK;
		final GItem item = (GItem)page.items[itemIndex];
		if(item == null) return null;
		page.items[itemIndex] = null;
		page.size--;
		this.size--;
		if(page.size != 0) return item;
		this.pages[pageIndex] = null;
		return item;
	}

	/**
	 * Diese Methode entfernt solange überflüssige Elemente, bis die aktuelle Anzahl der Elemente nicht mehr größer der
	 * minimalen Anzahl ist.
	 */
	public final void compact() {
		int count = this.minSize - this.size;
		if(count >= 0) return;
		final CachePage[] pages = this.pages;
		this.size = this.minSize;
		for(int i = pages.length - 1; 0 <= i; i--){
			final CachePage page = pages[i];
			if(page != null){
				count += page.size;
				if(count <= 0){
					page.size = 0;
					pages[i] = null;
					if(count == 0) return;
				}else{
					final Object[] items = page.items;
					int size = page.size;
					page.size = count;
					for(int i2 = items.length - 1; 0 <= i2; i2--){
						if(items[i2] != null){
							items[i2] = null;
							size--;
							if(size == count) return;
						}
					}
					throw new AssertionError();
				}
			}
		}
	}

	/**
	 * Diese Methode gibt die minimale Anzahl der Elemente zurück. Wenn die aktuelle Anzahl der Elemente größer der
	 * maximalen Anzahl ist und die Methode {@link #compact()} aufgerufen wird, entfernt diese solange überflüssige
	 * Elemente, bis die aktuelle Anzahl der Elemente gleich der minimalen Anzahl ist.
	 * 
	 * @return minimale Anzahl der Elemente.
	 */
	public final int getMinSize() {
		return this.minSize;
	}

	public final void setMinSize(final int value) throws IllegalArgumentException {
		if(this.minSize == value) return;
		if(value < 0) throw new IllegalArgumentException("value < 0");
		this.minSize = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der Elemente zurück. Wenn die aktuelle Anzahl der Elemente nicht kleiner als
	 * die maximalen Anzahl ist und in der Methode {@link #get(int)} ein fehlendes Element erzeugt werden muss, wird die
	 * Methode {@link #compact()} automatisch aufgerufen.
	 * 
	 * @return maximale Anzahl der Elemente.
	 */
	public final int getMaxSize() {
		return this.maxSize;
	}

	public final void setMaxSize(final int value) {
		if(this.maxSize == value) return;
		if(value < 0) throw new IllegalArgumentException("value < 0");
		this.maxSize = value;
		if(this.size < value) return;
		this.compact();
	}

	/**
	 * Diese Methode gibt die Kapazität zurück. Diese Begrenzt die Menge der Indices der Elemente, da für jeden Index
	 * {@code index} eines Elements gilt:
	 * 
	 * <pre>(0 <= index) &amp;&amp; (index < capacity())</pre>
	 * 
	 * @return Kapazität.
	 */
	public final int getCapacity() {
		return this.capacity;
	}

	public final void setCapacity(final int value) throws IllegalArgumentException {
		if(this.capacity == value) return;
		if(value < 0) throw new IllegalArgumentException("value < 0");
		this.capacity = value;
		final int pageCount = ((value + CachePage.PAGE_SIZE) - 1) / CachePage.PAGE_SIZE;
		if(this.pages.length != pageCount){
			this.size = 0;
			this.pages = new CachePage[pageCount];
		}else{
			this.clear();
		}
	}

	/**
	 * Diese Methode gibt den {@link Iterator} über die im {@link Cache} enthaltenen Elemente zurück.
	 */
	@Override
	public Iterator<GItem> iterator() {
		return new CacheIterator<GItem>(this);
	}

}
