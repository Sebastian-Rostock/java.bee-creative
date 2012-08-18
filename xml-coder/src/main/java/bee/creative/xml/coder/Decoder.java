package bee.creative.xml.coder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import javax.xml.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import bee.creative.util.Comparables;
import bee.creative.util.Comparables.Get;
import bee.creative.util.Comparators;
import bee.creative.util.Filter;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert Methoden zur Dekodierung eines XML-Dokuments aus der von einem {@link Encoder} erzeugten, optimierten, binären Darstellung.
 * 
 * @see Encoder
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Decoder {

	/**
	 * Diese Schnittstelle definiert die Eingabe eines {@link Decoder}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface DecodeSource {

		/**
		 * Diese Methode liest die gegebene Anzahl an {@code byte}s ab der aktuellen Leseposition aus der Eingabe in das gegebene {@code byte}-Array an die gegebene Position ein und vergrößert die Leseposition um die gegebene Anzahl.
		 * 
		 * @see #index()
		 * @param array {@code byte}-Array.
		 * @param offset Index des ersten gelesenen {@code byte}s.
		 * @param length Anzahl der zulesenden {@code byte}s.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public void read(byte[] array, int offset, int length) throws IOException;

		/**
		 * Diese Methode setzt die Leseposition der Eingabe, ab der via {@link #read(byte[], int, int)} die nächsten {@code byte}s gelesen werden können.
		 * 
		 * @see #index()
		 * @param index Leseposition.
		 * @throws IOException Wenn die gegebene Position negativ ist oder ein I/O-Fehler auftritt.
		 */
		public void seek(long index) throws IOException;

		/**
		 * Diese Methode gibt die aktuelle Leseposition zurück, ab der via {@link #read(byte[], int, int)} die nächsten {@code byte}s gelesen werden können.
		 * 
		 * @return Leseposition.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 */
		public long index() throws IOException;

	}

	/**
	 * Diese Schnittstelle definiert eine {@link NodeList} mit {@link DecodeChildNodeAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ChildList extends NodeList {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodeAdapter item(int index);

	}

	/**
	 * Diese Schnittstelle definiert eine {@link NodeList} mit {@link DecodeElementNodeAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ElementList extends ChildList {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter item(int index);

	}

	/**
	 * Diese Klasse implementiert eine {@link DecodeSource} mit {@link RandomAccessFile}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeSourceFile implements DecodeSource {

		/**
		 * Dieses Feld speichert das {@link RandomAccessFile}.
		 */
		final RandomAccessFile file;

		/**
		 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws NullPointerException Wenn das gegebene {@link RandomAccessFile} {@code null} ist.
		 */
		public DecodeSourceFile(final RandomAccessFile file) throws NullPointerException {
			if(file == null) throw new NullPointerException("file is null");
			this.file = file;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void read(final byte[] values, final int offset, final int count) throws IOException {
			this.file.readFully(values, offset, count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void seek(final long index) throws IOException {
			this.file.seek(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public long index() throws IOException {
			return this.file.getFilePointer();
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung mehrerer Elemente, die über einen Index identifiziert und nachgeladen werden können.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class DecodePool<GItem> implements Iterable<GItem> {

		/**
		 * Diese Klasse implementiert ein Objekt zur Verwaltung einer Teilmenge von Elementen.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class DecodePoolPage {

			/**
			 * Dieses Feld speichert die Anzahl der Bits zur Adressierung der Elemente innerhalb einer {@link DecodePoolPage} .
			 */
			static final int PAGE_BITS = 7;

			/**
			 * Dieses Feld speichert die maximale Anzahl der Elemente in einer {@link DecodePoolPage}.
			 */
			static final int PAGE_SIZE = 1 << DecodePoolPage.PAGE_BITS;

			/**
			 * Dieses Feld speichert die Bitmaske zur Ermittlung des Index eines Elements innerhalb einer {@link DecodePoolPage}.
			 */
			static final int PAGE_MASK = DecodePoolPage.PAGE_SIZE - 1;

			/**
			 * Dieses Feld speichert die Anzahl der Elemente.
			 */
			int size;

			/**
			 * Dieses Feld speichert die Elemente.
			 */
			final Object[] items = new Object[DecodePoolPage.PAGE_SIZE];

		}

		/**
		 * Diese Klasse implementiert den {@link Iterator} über die im {@link DecodePool} enthaltenen Elemente.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @see DecodePool
		 * @param <GItem> Typ der Elemente.
		 */
		static final class DecodePoolIterator<GItem> implements Iterator<GItem> {

			/**
			 * Dieses Feld speichert den Besitzer.
			 */
			final DecodePool<GItem> owner;

			/**
			 * Dieses Feld speichert die nächste {@link DecodePoolPage}.
			 */
			DecodePoolPage nextPage;

			/**
			 * Dieses Feld speichert das nächste Element.
			 */
			GItem nextItem;

			/**
			 * Dieses Feld speichert den Index der nächsten {@link DecodePoolPage}.
			 */
			int nextPageIndex;

			/**
			 * Dieses Feld speichert den Index des nächsten Elements.
			 */
			int nextItemIndex;

			/**
			 * Dieses Feld speichert den Index der letzten {@link DecodePoolPage}.
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
			public DecodePoolIterator(final DecodePool<GItem> owner) throws NullPointerException {
				if(owner == null) throw new NullPointerException("owner is null");
				this.owner = owner;
				this.nextPageIndex = -1;
				this.nextItemIndex = -1;
				this.lastPageIndex = -1;
				this.seekPage();
				if(this.nextPage == null) return;
				this.seekItem();
			}

			/**
			 * Diese Methode navigiert zur nächsten {@link DecodePoolPage}.
			 */
			void seekPage() {
				final DecodePoolPage[] pages = this.owner.pages;
				for(int i = this.nextPageIndex + 1, size = pages.length; i < size; i++){
					final DecodePoolPage page = pages[i];
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
			void seekItem() {
				do{
					final Object[] items = this.nextPage.items;
					for(int i = this.nextItemIndex + 1, size = items.length; i < size; i++){
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
				final int lastPageIndex = this.lastPageIndex;
				if(lastPageIndex < 0) throw new IllegalStateException();
				final DecodePoolPage page = this.owner.pages[lastPageIndex];
				if(page == null) return;
				this.lastPageIndex = -1;
				final int lastItemIndex = this.lastItemIndex;
				final Object item = page.items[lastItemIndex];
				if(item == null) return;
				page.size--;
				page.items[lastItemIndex] = null;
				this.owner.size--;
				if(page.size != 0) return;
				this.owner.pages[lastPageIndex] = null;
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Get} für {@link Comparables#binarySearch(Get, Comparable, int, int)} zur direkten Suche in einem {@link DecodePool}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class DecodePoolGetAll<GItem> implements Get<GItem> {

			/**
			 * Dieses Feld speichert den Besitzer.
			 */
			final DecodePool<GItem> owner;

			/**
			 * Dieser Konstrukteur initialisiert den Besitzer.
			 * 
			 * @param owner Besitzer.
			 */
			public DecodePoolGetAll(final DecodePool<GItem> owner) {
				this.owner = owner;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem get(final int index) throws IndexOutOfBoundsException {
				return this.owner.get(index);
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Get} für {@link Comparables#binarySearch(Get, Comparable, int, int)} zur indirekten Suche in einem {@link DecodePool}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		static final class DecodePoolGetSection<GItem> implements Get<GItem> {

			/**
			 * Dieses Feld speichert den Besitzer.
			 */
			final DecodePool<GItem> owner;

			/**
			 * Dieses Feld speichert den Suchraum.
			 */
			final int[] indices;

			/**
			 * Dieser Konstrukteur initialisiert Besitzer und Suchraum.
			 * 
			 * @param owner Besitzer.
			 * @param indices Suchraum.
			 */
			public DecodePoolGetSection(final DecodePool<GItem> owner, final int[] indices) {
				this.owner = owner;
				this.indices = indices;
			}

			/**
			 * {@inheritDoc}
			 */
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
		 * Dieses Feld speichert die {@link DecodePoolPage}s.
		 */
		DecodePoolPage[] pages = {};

		/**
		 * Dieses Feld speichert die minimale Anzahl der Elemente, auf welche beim Bereinigen zurückgesetzt werden soll.
		 */
		int minSize = 16;

		/**
		 * Dieses Feld speichert die maximale Anzahl.
		 */
		int maxSize = 128;

		/**
		 * Dieses Feld speichert die Kapazität.
		 */
		int capacity;

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück. Wenn dieses noch nicht existiert, wird es via {@link #load(int)} nachgeladen.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws NullPointerException Wenn das von {@link #load(int)} geladene Element {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		@SuppressWarnings ("unchecked")
		public GItem get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			if(index >= this.capacity) throw new IndexOutOfBoundsException( );
			final int pageIndex = index >> DecodePoolPage.PAGE_BITS;
			final int itemIndex = index & DecodePoolPage.PAGE_MASK;
			DecodePoolPage page = this.pages[pageIndex];
			GItem item;
			if(page != null){
				item = (GItem)page.items[itemIndex];
				if(item != null) return item;
				item = this.load(index);
				if(item == null) throw new NullPointerException();
				if(this.size >= this.maxSize){
					this.compact();
					if(page.size == 0){
						Arrays.fill(page.items, null);
						this.pages[pageIndex] = page;
					}
				}
			}else{
				item = this.load(index);
				if(item == null) throw new NullPointerException();
				if(this.size >= this.maxSize){
					this.compact();
				}
				page = new DecodePoolPage();
				this.pages[pageIndex] = page;
			}
			page.items[itemIndex] = item;
			page.size++;
			this.size++;
			return item;
		}

		/**
		 * Diese Methode sucht binäre nach dem ersten Treffer des gegebenen {@link Comparable}s und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodePoolGetAll
		 * @see Comparables#binarySearch(Get, Comparable, int, int)
		 * @param comparable {@link Comparable}.
		 * @return erster Treffer oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Comparable} {@code null} ist.
		 */
		public GItem find(final Comparable<? super GItem> comparable) throws NullPointerException {
			final Get<GItem> get = new DecodePoolGetAll<GItem>(this);
			final int index = Comparables.binarySearch(get, comparable, 0, this.capacity);
			if(index < 0) return null;
			return get.get(index);
		}

		/**
		 * Diese Methode sucht binäre im gegebenen Suchraum nach dem ersten Treffer des gegebenen {@link Comparable}s und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodePoolGetSection
		 * @see Comparables#binarySearch(Get, Comparable, int, int)
		 * @param indices Suchraum.
		 * @param comparable {@link Comparable}.
		 * @return erster Treffer oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Comparable} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public GItem find(final int[] indices, final Comparable<? super GItem> comparable) throws NullPointerException {
			if(indices.length == 0) return null;
			final Get<GItem> get = new DecodePoolGetSection<GItem>(this, indices);
			final int index = Comparables.binarySearch(get, comparable, 0, indices.length);
			if(index < 0) return null;
			return get.get(index);
		}

		/**
		 * Diese Methode lädt das {@code index}-te Element aus dem {@link DecodeSource} und gibt es zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 */
		public abstract GItem load(final int index);

		/**
		 * Diese Methode gibt die aktuelle Anzahl der Elemente zurück.
		 * 
		 * @return aktuelle Anzahl der Elemente.
		 */
		public int size() {
			return this.size;
		}

		/**
		 * Diese Methode entfernt alle Elemente.
		 */
		public void clear() {
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
		public GItem clear(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException("index < 0");
			if(index >= this.capacity) throw new IndexOutOfBoundsException("index >= capacity");
			final int pageIndex = index >> DecodePoolPage.PAGE_BITS;
			final DecodePoolPage page = this.pages[pageIndex];
			if(page == null) return null;
			final int itemIndex = index & DecodePoolPage.PAGE_MASK;
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
		 * Diese Methode gibt nur dann {@code true} zurück, wenn das {@code index}-te Element vorhanden ist.
		 * 
		 * @param index Index.
		 * @return {@code true}, wenn das {@code index}-tes Element geladen ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public boolean exists(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException("index < 0");
			if(index >= this.capacity) throw new IndexOutOfBoundsException("index >= capacity");
			final int pageIndex = index >> DecodePoolPage.PAGE_BITS;
			final DecodePoolPage page = this.pages[pageIndex];
			if(page == null) return false;
			final int itemIndex = index & DecodePoolPage.PAGE_MASK;
			return page.items[itemIndex] == null;
		}

		/**
		 * Diese Methode entfernt solange überflüssige Elemente, bis die aktuelle Anzahl der Elemente nicht mehr größer der minimalen Anzahl ist.
		 */
		public void compact() {
			int count = this.minSize - this.size;
			if(count >= 0) return;
			final DecodePoolPage[] pages = this.pages;
			this.size = this.minSize;
			for(int i = pages.length - 1; 0 <= i; i--){
				final DecodePoolPage page = pages[i];
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
		 * Diese Methode gibt die minimale Anzahl der Elemente zurück. Wenn die aktuelle Anzahl der Elemente größer der maximalen Anzahl ist und die Methode {@link #compact()} aufgerufen wird, entfernt diese solange überflüssige Elemente, bis die aktuelle Anzahl der Elemente gleich der minimalen Anzahl ist.
		 * 
		 * @return minimale Anzahl der Elemente.
		 */
		public int getMinSize() {
			return this.minSize;
		}

		/**
		 * Diese Methode setzt die minimale Anzahl der Elemente.
		 * 
		 * @see #getMinSize()
		 * @param value minimale Anzahl der Elemente.
		 * @throws IllegalArgumentException Wenn die gegebene minimale Anzahl der Elemente kleiner als {@code 0} ist.
		 */
		public void setMinSize(final int value) throws IllegalArgumentException {
			if(this.minSize == value) return;
			if(value < 0) throw new IllegalArgumentException("value < 0");
			this.minSize = value;
		}

		/**
		 * Diese Methode gibt die maximale Anzahl der Elemente zurück. Wenn die aktuelle Anzahl der Elemente nicht kleiner als die maximalen Anzahl ist und in der Methode {@link #get(int)} ein fehlendes Element erzeugt werden muss, wird die Methode {@link #compact()} automatisch aufgerufen.
		 * 
		 * @return maximale Anzahl der Elemente.
		 */
		public int getMaxSize() {
			return this.maxSize;
		}

		/**
		 * Diese Methode setzt die maximale Anzahl der Elemente.
		 * 
		 * @see #getMaxSize()
		 * @param value maximale Anzahl der Elemente.
		 * @throws IllegalArgumentException Wenn die gegebene maximale Anzahl der Elemente kleiner als {@code 0} ist.
		 */
		public void setMaxSize(final int value) {
			if(this.maxSize == value) return;
			if(value < 0) throw new IllegalArgumentException("value < 0");
			this.maxSize = value;
			if(this.size < value) return;
			this.compact();
		}

		/**
		 * Diese Methode gibt die Kapazität zurück. Diese Begrenzt die Menge der Indices der Elemente, da für jeden Index {@code index} eines Elements gilt:
		 * 
		 * <pre>(0 <= index) &amp;&amp; (index < capacity())</pre>
		 * 
		 * @return Kapazität.
		 */
		public int getCapacity() {
			return this.capacity;
		}

		/**
		 * Diese Methode setzt die Kapazität.
		 * 
		 * @param value Kapazität.
		 * @throws IllegalArgumentException Wenn die gegebene Kapazität kleiner als {@code 0} ist.
		 */
		public void setCapacity(final int value) throws IllegalArgumentException {
			if(this.capacity == value) return;
			if(value < 0) throw new IllegalArgumentException("value < 0");
			this.capacity = value;
			final int length = ((value + DecodePoolPage.PAGE_SIZE) - 1) / DecodePoolPage.PAGE_SIZE;
			if(this.pages.length != length){
				this.size = 0;
				this.pages = new DecodePoolPage[length];
			}else{
				this.clear();
			}
		}

		/**
		 * Diese Methode gibt den {@link Iterator} über die im {@link DecodePool} enthaltenen Elemente zurück.
		 */
		@Override
		public Iterator<GItem> iterator() {
			return new DecodePoolIterator<GItem>(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, "DecodeCache", this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz, der von einem {@link DecodePool} erzeugt und verwaltet sowie aus einer {@link DecodeSource} geladen wird.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeItem {

		/**
		 * Dieses Feld speichert den Index dieses Datensatzes im {@link DecodePool}.
		 */
		protected final int index;

		/**
		 * Dieser Konstrukteur initialisiert den Index dieses Datensatzes im {@link DecodePool}.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodePool}.
		 */
		public DecodeItem(final int index) {
			this.index = index;
		}

		/**
		 * Diese Methode gibt den Index dieses Datensatzes im {@link DecodePool} zurück.
		 * 
		 * @return Index dieses Datensatzes.
		 */
		public int index() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DecodeItem)) return false;
			final DecodeItem data = (DecodeItem)object;
			return this.index == data.index;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodePool}, dessen Elemente und Kapazität aus eier {@link DecodeSource} geladen werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class DecodeItemPool<GItem> extends DecodePool<GItem> {

		/**
		 * Dieses Feld speichert die {@link DecodeSource}.
		 */
		protected final DecodeSource source;

		/**
		 * Dieses Feld speichert die Position in der {@link DecodeSource}, an der die Daten dieses {@link DecodeItemPool}s beginnen.
		 */
		protected final long sourceIndex;

		/**
		 * Dieses Feld speichert die Größe eines Elements in der {@link DecodeSource}.
		 */
		protected final int itemSize;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente in der {@link DecodeSource}.
		 */
		protected final int itemCount;

		/**
		 * Dieser Konstrukteur initialisiert die {@link DecodeSource} und die Größe eines Elements.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param itemSize Größe eines Elements.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 * @throws NullPointerException Wenn die gegebene {@link DecodeSource} {@code null} ist.
		 */
		public DecodeItemPool(final DecodeSource source, final int itemSize) throws IOException, NullPointerException {
			if(source == null) throw new NullPointerException("source is null");
			this.source = source;
			this.sourceIndex = source.index();
			final int itemCount = Decoder.readInts(source, 1)[0];
			this.itemSize = itemSize;
			this.itemCount = itemCount;
			this.setCapacity(itemCount);
			this.seekIndex(itemCount);
		}

		/**
		 * Diese Methode navigiert in der {@link DecodeSource} an die Position des {@code index}-ten Elements. Die Position ergibt sich aus {@code sourceIndex + 4 + index * itemSize}.
		 * 
		 * @param index Index.
		 * @throws IOException Wenn die {@link DecodeSource} eine {@link IOException} auslöst.
		 */
		protected final void seekIndex(final int index) throws IOException {
			this.source.seek(this.sourceIndex + 4 + (index * this.itemSize));
		}

		/**
		 * Diese Methode gibt die {@link DecodeSource} zurück.
		 * 
		 * @return {@link DecodeSource}.
		 */
		public DecodeSource source() {
			return this.source;
		}

		/**
		 * Diese Methode gibt die Position zurück, an der die Daten dieses {@link DecodeItemPool}s in der {@link DecodeSource} beginnen.
		 * 
		 * @return Position, an der die Daten dieses {@link DecodeItemPool}s in der {@link DecodeSource} beginnen.
		 */
		public long sourceIndex() {
			return this.sourceIndex;
		}

		/**
		 * Diese Methode gibt die Größe eines Elements in der {@link DecodeSource} zurück.
		 * 
		 * @return Größe eines Elements in der {@link DecodeSource}.
		 */
		public int itemSize() {
			return this.itemSize;
		}

		/**
		 * Diese Methode gibt die Anzahl der Elemente in der {@link DecodeSource} zurück.
		 * 
		 * @return Anzahl der Elemente in der {@link DecodeSource}.
		 */
		public int itemCount() {
			return this.itemCount;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setCapacity(final int value) throws IllegalArgumentException {
			if(value != this.itemCount) throw new IllegalArgumentException("value != itemCount");
			super.setCapacity(value);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodePool}, dessen Elemente aus mehreren Werten bestehen.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class DecodeListPool<GItem> extends DecodeItemPool<GItem> {

		/**
		 * Dieses Feld speichert die Größe eines Werts in der {@link DecodeSource}.
		 */
		protected final int valueSize;

		/**
		 * Dieses Feld speichert die Anzahl der Werte in der {@link DecodeSource}.
		 */
		protected final int valueCount;

		/**
		 * Dieser Konstrukteur initialisiert das {@link DecodeSource} und die Größe eines Werts.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param valueSize Größe eines Werts.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 * @throws NullPointerException Wenn die gegebene {@link DecodeSource} {@code null} ist.
		 */
		public DecodeListPool(final DecodeSource source, final int valueSize) throws IOException, NullPointerException {
			super(source, 4);
			this.valueSize = valueSize;
			final int valueCount = Decoder.readInts(source, 1)[0];
			this.valueCount = valueCount;
			this.seekOffset(valueCount);
		}

		/**
		 * Diese Methode navigiert in der {@link DecodeSource} an die Position des {@code offset}-ten Werts. Die Position ergibt sich aus {@code sourceIndex + 4 + itemSize * itemCount + 4 + offset * valueSize}.
		 * 
		 * @param offset Offset.
		 * @throws IOException Wenn die {@link DecodeSource} eine {@link IOException} auslöst.
		 */
		protected final void seekOffset(final int offset) throws IOException {
			this.source.seek(this.sourceIndex + 4 + (this.itemSize * this.itemCount) + 4 + (offset * this.valueSize));
		}

		/**
		 * Diese Methode gibt die Größe eines Werts in der {@link DecodeSource} zurück.
		 * 
		 * @return Größe eines Werts in der {@link DecodeSource}.
		 */
		public int valueSize() {
			return this.valueSize;
		}

		/**
		 * Diese Methode gibt die Anzahl der Werte in der {@link DecodeSource} zurück.
		 * 
		 * @return Anzahl der Werte in der {@link DecodeSource}.
		 */
		public int valueCount() {
			return this.valueCount;
		}

	}

	/**
	 * Diese Klasse implementiert das {@link DecodeItem} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeValue extends DecodeItem {

		/**
		 * Dieses Feld speichert den {@link String}.
		 */
		protected final String string;

		/**
		 * Dieser Konstrukteur initialisiert Index und {@link String}.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodeItemPool}.
		 * @param string {@link String}.
		 */
		public DecodeValue(final int index, final String string) {
			super(index);
			this.string = string;
		}

		/**
		 * Diese Methode gibt den {@link String} zurück.
		 * 
		 * @return {@link String}.
		 */
		public String string() {
			return this.string;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "[" + this.index + "]" + Objects.toString(this.string);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DecodePool} der {@link DecodeValue}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeValuePool extends DecodeListPool<DecodeValue> {

		/**
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eines {@link DecodeValue}s über seinen {@link String}.
		 * 
		 * @see DecodeValue#string()
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class DecodeValueComparable implements Comparable<DecodeValue> {

			/**
			 * Dieses Feld speichert den {@link String}.
			 */
			final String value;

			/**
			 * Dieser Konstrukteur initialisiert den {@link String} .
			 * 
			 * @param value {@link String}
			 */
			public DecodeValueComparable(final String value) {
				this.value = value;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeValue input) {
				return this.value.compareTo(input.string);
			}

		}

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle}.
		 */
		protected final DecodeGroupPool valueHash;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente im {@link #valueHash()} minus {@code 1}.
		 * 
		 * @see DecodeGroupPool#itemCount()
		 */
		protected final int valueHashMask;

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeSource}sowie {@code Hash-Tabelle} und lädt die Header.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param valueHash {@code Hash-Tabelle}.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 * @throws NullPointerException Wenn die gegebene {@link DecodeSource} {@code null} ist.
		 */
		public DecodeValuePool(final DecodeSource source, final DecodeGroupPool valueHash) throws IOException, NullPointerException {
			super(source, 1);
			this.valueHash = valueHash;
			this.valueHashMask = valueHash.itemCount - 1;
			if((this.valueHashMask & valueHash.itemCount) != 0) throw new IOException(new IllegalArgumentException("valueHash.itemCount is no power of two"));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeValue load(final int index) {
			try{
				this.seekIndex(index);
				final int[] ints = Decoder.readInts(this.source, 2);
				this.seekOffset(ints[0]);
				final int length = ints[1] - ints[0];
				final byte[] bytes = Decoder.readBytes(this.source, length);
				return new DecodeValue(index, Coder.decodeChars(bytes));
			}catch(final IOException e){
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zurück.
		 * 
		 * @return {@code Hash-Tabelle}.
		 */
		public DecodeGroupPool valueHash() {
			return this.valueHash;
		}

		/**
		 * Diese Methode sucht binäre nach dem {@link DecodeValue} mit dem gegebenen {@link String} und gibt diesen oder {@code null} zurück. Wenn der {@link #valueHash()} nicht leer ist, wind nur im zum gegebenen {@link String} ermittelten Index-Array gesucht.
		 * 
		 * @see Coder#hashString(String)
		 * @see Encoder#ValueComparator
		 * @see DecodePool#find(Comparable)
		 * @see DecodeValue#string()
		 * @param value {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public DecodeValue findValue(final String value) throws NullPointerException {
			if(this.itemCount == 0) return null;
			if(this.valueHashMask < 0) return this.find(new DecodeValueComparable(value));
			return this.findValue(this.valueHash.get(Coder.hashString(value) & this.valueHashMask).indices, value);
		}

		/**
		 * Diese Methode sucht im gegebenen Index-Array binäre nach dem {@link DecodeValue} mit dem gegebenen {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see Encoder#ValueComparator
		 * @see DecodePool#find(int[], Comparable)
		 * @see DecodeValue#string()
		 * @param indices Index-Array.
		 * @param value {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public DecodeValue findValue(final int[] indices, final String value) throws NullPointerException {
			if((this.itemCount == 0) || (indices.length == 0)) return null;
			return this.find(indices, new DecodeValueComparable(value));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link DecodeItem} mit {@code URI} und {@code Name}.
	 * 
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeLabel extends DecodeItem {

		/**
		 * Dieses Feld speichert den Index des {@code URI}-{@link DecodeValue}s.
		 */
		protected final int uri;

		/**
		 * Dieses Feld speichert den Index des {@code Name}-{@link DecodeValue}s.
		 */
		protected final int name;

		/**
		 * Dieser Konstrukteur initialisiert die Indizes.
		 * 
		 * @param index Index.
		 * @param uri Index des {@code URI}-{@link DecodeValue}s.
		 * @param name Index des {@code Name}-{@link DecodeValue}s.
		 */
		public DecodeLabel(final int index, final int uri, final int name) {
			super(index);
			this.uri = uri;
			this.name = name;
		}

		/**
		 * Diese Methode gibt den Index des {@code URI}-{@link DecodeValue}s zurück.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return Index des {@code URI}-{@link DecodeValue}s.
		 */
		public int uri() {
			return this.uri;
		}

		/**
		 * Diese Methode gibt den Index des {@code Name}-{@link DecodeValue}s zurück.
		 * 
		 * @see Node#getPrefix()
		 * @see Node#getLocalName()
		 * @return Index des {@code Name}-{@link DecodeValue}s.
		 */
		public int name() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, "DecodeLabel", "index", this.index, "uri", this.uri, "name", this.name);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DecodePool} der {@link DecodeLabel}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeLabelPool extends DecodeItemPool<DecodeLabel> {

		/**
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eines {@link DecodeLabel}s über den Index seines {@code URI}-{@link DecodeValue}s.
		 * 
		 * @see DecodeLabel#uri()
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class DecodeLabelUriComparable implements Comparable<DecodeLabel> {

			/**
			 * Dieses Feld speichert den Index des {@code URI}-{@link DecodeValue}s.
			 */
			final int uri;

			/**
			 * Dieser Konstrukteur initialisiert den Index des {@code URI}-{@link DecodeValue}s.
			 * 
			 * @param uri Index des {@code URI}-{@link DecodeValue}s.
			 */
			public DecodeLabelUriComparable(final int uri) {
				this.uri = uri;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeLabel value) {
				return Comparators.compare(this.uri, value.uri);
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eines {@link DecodeLabel}s über die Indices seiner {@code URI}- und {@code Name}-{@link DecodeValue}s.
		 * 
		 * @see DecodeLabel#uri()
		 * @see DecodeLabel#name()
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class DecodeLabelUriNameComparable implements Comparable<DecodeLabel> {

			/**
			 * Dieses Feld speichert den Index des {@code URI}-{@link DecodeValue}s.
			 */
			final int uri;

			/**
			 * Dieses Feld speichert den Index des {@code Name}-{@link DecodeValue}s.
			 */
			final int name;

			/**
			 * Dieser Konstrukteur initialisiert die Indizes.
			 * 
			 * @param uri Index des {@code URI}-{@link DecodeValue}s.
			 * @param name Index des {@code Name}-{@link DecodeValue}s.
			 */
			public DecodeLabelUriNameComparable(final int uri, final int name) {
				this.uri = uri;
				this.name = name;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeLabel input) {
				final int comp = Comparators.compare(this.name, input.name);
				if(comp != 0) return comp;
				return Comparators.compare(this.uri, input.uri);
			}

		}

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle}.
		 */
		protected final DecodeGroupPool labelHash;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente im {@link #labelHash()} minus {@code 1}.
		 * 
		 * @see DecodeGroupPool#itemCount()
		 */
		protected final int labelHashMask;

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeSource}sowie {@code Hash-Tabelle} und lädt die Header.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param labelHash {@code Hash-Tabelle}.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 */
		public DecodeLabelPool(final DecodeSource source, final DecodeGroupPool labelHash) throws IOException {
			super(source, 8);
			this.labelHash = labelHash;
			this.labelHashMask = labelHash.itemCount - 1;
			if((this.labelHashMask & labelHash.itemCount) != 0) throw new IOException(new IllegalArgumentException("labelHash.itemCount is no power of two"));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeLabel load(final int index) {
			try{
				this.seekIndex(index);
				final int[] ints = Decoder.readInts(this.source, 2);
				return new DecodeLabel(index, ints[0], ints[1]);
			}catch(final IOException e){
				throw new IllegalStateException(e);
			}
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zurück.
		 * 
		 * @return {@code Hash-Tabelle}.
		 */
		public DecodeGroupPool labelHash() {
			return this.labelHash;
		}

		/**
		 * Diese Methode sucht binäre nach dem {@link DecodeLabel} mit den gegebenen Indices der {@code URI}- und {@code Name}-{@link DecodeValue}s und gibt dieses {@link DecodeLabel} oder {@code null} zurück. Wenn der {@link #labelHash()} nicht leer ist, wind nur im zu den gegebenen Indices ermittelten Index-Array gesucht.
		 * 
		 * @see Coder#hashLabel(int, int)
		 * @see Encoder#LabelComparator
		 * @see DecodePool#find(Comparable)
		 * @see DecodeLabel#uri()
		 * @see DecodeLabel#name()
		 * @param uri Index des {@code URI}-{@link DecodeValue}s.
		 * @param name Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public DecodeLabel findLabel(final int uri, final int name) {
			if(this.itemCount == 0) return null;
			if(this.labelHashMask < 0) return this.find(new DecodeLabelUriNameComparable(uri, name));
			return this.findLabel(this.labelHash.get(Coder.hashLabel(uri, name) & this.labelHashMask).indices, uri, name);
		}

		/**
		 * Diese Methode sucht binäre im gegebenen Index-Array nach dem {@link DecodeLabel} mit den gegebenen {@code URI}- bzw. {@code Name}-Indices und gibt diesen oder {@code null} zurück. Die {@link DecodeLabel} müssen dazum im {@link DecodeItemPool} aufsteigend sortiert sein.
		 * 
		 * @see Encoder#LabelComparator
		 * @see DecodePool#find(int[], Comparable)
		 * @see DecodeLabel#uri()
		 * @see DecodeLabel#name()
		 * @param indices Index-Array.
		 * @param uri Index des {@code URI}-{@link DecodeValue}s.
		 * @param name Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public DecodeLabel findLabel(final int[] indices, final int uri, final int name) throws NullPointerException {
			if((this.itemCount == 0) || (indices.length == 0)) return null;
			return this.find(indices, new DecodeLabelUriNameComparable(uri, name));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link DecodeItem} mit Indices auf anderer Datensätze.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeGroup extends DecodeItem {

		/**
		 * Dieses Feld speichert die Indices der Datensätze.
		 */
		protected final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodeItemPool}.
		 * @param indices Indices der Datensätze.
		 */
		public DecodeGroup(final int index, final int[] indices) {
			super(index);
			this.indices = indices;
		}

		/**
		 * Diese Methode gibt die Indices der Datensätze zurück.
		 * 
		 * @return Indices der Datensätze.
		 */
		public int[] indices() {
			return this.indices.clone();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "DecodeGroup", "index", this.index, "indices", this.indices);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DecodeListPool} der {@link DecodeGroup}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeGroupPool extends DecodeListPool<DecodeGroup> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link DecodeSource} und lädt die Header.
		 * 
		 * @param source {@link DecodeSource}.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 */
		public DecodeGroupPool(final DecodeSource source) throws IOException {
			super(source, 4);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeGroup load(final int index) {
			try{
				this.seekIndex(index);
				final int[] ints = Decoder.readInts(this.source, 2);
				this.seekOffset(ints[0]);
				final int length = ints[1] - ints[0];
				return new DecodeGroup(index, Decoder.readInts(this.source, length));
			}catch(final IOException e){
				throw new IllegalStateException(e);
			}
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link DecodeItem} zur Abstraktion eines {@link Element}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElement extends DecodeItem {

		/**
		 * Dieses Feld speichert den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}- {@link DecodeValue}s.
		 */
		protected final int label;

		/**
		 * Dieses Feld speichert den Index der {@code URI/Prefix}-{@link DecodeGroup} oder {@code -1}.
		 */
		protected final int xmlns;

		/**
		 * Dieses Feld speichert den Index der {@link DecodeGroup} der {@link Element#getChildNodes()}.
		 */
		protected final int children;

		/**
		 * Dieses Feld speichert den Index der {@link DecodeGroup} der {@link Element#getAttributes()}.
		 */
		protected final int attributes;

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodeItemPool}.
		 * @param label Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param xmlns Index der {@code URI/Prefix}-{@link DecodeGroup} oder {@code -1}.
		 * @param children Index der {@link DecodeGroup} der {@link Element#getChildNodes()}.
		 * @param attributes Index der {@link DecodeGroup} der {@link Element#getAttributes()}.
		 */
		public DecodeElement(final int index, final int label, final int xmlns, final int children, final int attributes) {
			super(index);
			this.label = label;
			this.xmlns = xmlns;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * Diese Methode gibt den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s zurück.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @return Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 */
		public int label() {
			return this.label;
		}

		/**
		 * Diese Methode gibt den Index der {@code URI/Prefix}-{@link DecodeGroup} oder {@code -1} zurück.
		 * 
		 * @return Index der {@code URI/Prefix}-{@link DecodeGroup} oder {@code -1}.
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		public int xmlns() {
			return this.xmlns;
		}

		/**
		 * Diese Methode gibt den Index der {@link DecodeGroup} der {@link Element#getChildNodes()} zurück. Die Indices in dieser {@link DecodeGroup} verweisen auf {@link DecodeValue}s bzw. {@link DecodeElement}s, wobei die Indices der {@link DecodeElement} um die Anzahl der {@link DecodeValue}s im {@link DecodeDocument#valuePool()} verschoben sind.
		 * 
		 * @see DecodeAdapter#childOffset()
		 * @return Index der {@link DecodeGroup} der {@link Element#getChildNodes()}.
		 */
		public int children() {
			return this.children;
		}

		/**
		 * Diese Methode gibt den Index der {@link DecodeGroup} der {@link Element#getAttributes()} zurück.
		 * 
		 * @return Index der {@link DecodeGroup} der {@link Element#getAttributes()}.
		 */
		public int attributes() {
			return this.attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			if(this.xmlns >= 0)
				return Objects.toStringCall(false, true, "DecodeElement", "index", this.index, "label", this.label, "xmlns", this.xmlns, "attributes", this.attributes,
					"children", this.children);
			return Objects.toStringCall(false, true, "DecodeElement", "index", this.index, "name", this.label, "attributes", this.attributes, "children",
				this.children);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DecodePool} der {@link DecodeElement}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementPool extends DecodeItemPool<DecodeElement> {

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeSource}sowie {@code xmlns}-Aktivierung und lädt die Header. Die {@code xmlns}-Aktivierung entscheidet über die Größe der Elemente in der {@link DecodeSource}.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param xmlnsEnabled {@code xmlns}-Aktivierung.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 */
		public DecodeElementPool(final DecodeSource source, final boolean xmlnsEnabled) throws IOException {
			super(source, xmlnsEnabled ? 16 : 12);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElement load(final int index) {
			try{
				this.seekIndex(index);
				if(this.itemSize == 16){
					final int[] ints = Decoder.readInts(this.source, 4);
					return new DecodeElement(index, ints[0], ints[1], ints[2], ints[3]);
				}else{
					final int[] ints = Decoder.readInts(this.source, 3);
					return new DecodeElement(index, ints[0], -1, ints[1], ints[2]);
				}
			}catch(final IOException e){
				throw new IllegalStateException(e);
			}
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten, filternden {@link ListIterator} über {@link DecodeElement}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeElementIterator implements ListIterator<DecodeElement>, Filter<DecodeElement> {

		/**
		 * Dieses Feld speichert den {@link DecodeAdapter}.}
		 */
		protected final DecodeAdapter adapter;

		/**
		 * Dieses Feld speichert das Index-Array der {@link DecodeElement#children()}.
		 */
		protected final int[] indices;

		/**
		 * Dieses Feld speichert das nachste {@link DecodeElement} oder {@code null}.
		 */
		DecodeElement next;

		/**
		 * Dieses Feld speichert das vorherige {@link DecodeElement} oder {@code null}.
		 */
		DecodeElement prev;

		/**
		 * Dieses Feld speichert den Index des nächsten {@link DecodeElement}s im Index-Array.
		 */
		int nextIndex = -1;

		/**
		 * Dieses Feld speichert den Index des vorherigen {@link DecodeElement}s im Index-Array.
		 */
		int prevIndex;

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und Index des {@link DecodeElement}s.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 * @param elementNodeIndex Index der {@link DecodeElement}s.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeAdapter} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public DecodeElementIterator(final DecodeAdapter adapter, final int elementNodeIndex) throws NullPointerException, IndexOutOfBoundsException {
			this.adapter = adapter;
			this.indices = adapter.children(elementNodeIndex);
			this.gotoNext();
		}

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und Index-Array der {@link DecodeElement#children()}.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public DecodeElementIterator(final DecodeAdapter adapter, final int[] indices) throws NullPointerException {
			if((adapter == null) || (indices == null)) throw new NullPointerException();
			this.adapter = adapter;
			this.indices = indices;
			this.gotoNext();
		}

		/**
		 * Diese Methode navigiert zum nächsten {@link DecodeElement}.
		 */
		void gotoNext() {
			this.prev = this.next;
			this.prevIndex = this.nextIndex;
			while(++this.nextIndex < this.indices.length){
				final int index = this.indices[this.nextIndex] - this.adapter.childOffset;
				if((index >= 0) && this.accept(this.next = this.adapter.elementNode(index))) return;
			}
			this.next = null;
		}

		/**
		 * Diese Methode navigiert zum vorherigen {@link DecodeElement}.
		 */
		void gotoPrev() {
			this.next = this.prev;
			this.nextIndex = this.prevIndex;
			while(--this.prevIndex >= 0){
				final int index = this.indices[this.prevIndex] - this.adapter.childOffset;
				if((index >= 0) && this.accept(this.prev = this.adapter.elementNode(index))) return;
			}
			this.prev = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElement next() {
			final DecodeElement next = this.next;
			if(next == null) throw new NoSuchElementException();
			this.gotoNext();
			return next;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasPrevious() {
			return this.prev != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElement previous() {
			final DecodeElement prev = this.prev;
			if(prev == null) throw new NoSuchElementException();
			this.gotoPrev();
			return prev;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int nextIndex() {
			return this.nextIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int previousIndex() {
			return this.prevIndex;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void set(final DecodeElement e) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final DecodeElement e) {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link DecodeItem} zur Abstraktion eines {@link Attr}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAttribute extends DecodeItem {

		/**
		 * /** Dieses Feld speichert den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}- {@link DecodeValue}s.
		 */
		protected final int label;

		/**
		 * Dieses Feld speichert den Index des {@code Value}-{@link DecodeValue}.
		 */
		protected final int value;

		/**
		 * Dieser Konstrukteur initialisiert die Indizes.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodePool}.
		 * @param label Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param value Index des {@code Value}-{@link DecodeValue}.
		 */
		public DecodeAttribute(final int index, final int label, final int value) {
			super(index);
			this.label = label;
			this.value = value;
		}

		/**
		 * Diese Methode gibt den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s zurück.
		 * 
		 * @see Attr#getNodeName()
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 */
		public int label() {
			return this.label;
		}

		/**
		 * Diese Methode gibt den Index des {@code Value}-{@link DecodeValue} zurück.
		 * 
		 * @see Attr#getNodeValue()
		 * @return Index des {@code Value}-{@link DecodeValue}.
		 */
		public int value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(false, true, "DecodeAttribute", "index", this.index, "label", this.label, "value", this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DecodeItemPool} der {@link DecodeAttribute}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAttributePool extends DecodeItemPool<DecodeAttribute> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link DecodeSource} und lädt die Header.
		 * 
		 * @param source {@link DecodeSource}.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 */
		public DecodeAttributePool(final DecodeSource source) throws IOException {
			super(source, 8);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttribute load(final int index) {
			try{
				this.seekIndex(index);
				final int[] ints = Decoder.readInts(this.source, 2);
				return new DecodeAttribute(index, ints[0], ints[1]);
			}catch(final IOException e){
				throw new IllegalStateException(e);
			}
		}

	}

	/**
	 * Diese Klasse implementiert eine Zusammenfassung mehrerer {@link DecodePool}s zur Abstraktion eines {@link Document}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeDocument {

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #uriPool()}.
		 */
		protected final DecodeGroupPool uriHash;

		/**
		 * Dieses Feld speichert den {@code UIR}-{@link DecodeValuePool} für {@link DecodeLabel#uri()}.
		 */
		protected final DecodeValuePool uriPool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #valuePool()}.
		 */
		protected final DecodeGroupPool valueHash;

		/**
		 * Dieses Feld speichert den {@code Value}-{@link DecodeValuePool} für {@link DecodeDocument#elementChildrenPool()} und {@link DecodeAttribute#value()}.
		 */
		protected final DecodeValuePool valuePool;

		/**
		 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
		 */
		protected final boolean xmlnsEnabled;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #xmlnsNamePool()}.
		 */
		protected final DecodeGroupPool xmlnsNameHash;

		/**
		 * Dieses Feld speichert den {@code Prefix}-{@link DecodeValuePool} für {@link DecodeDocument#xmlnsLabelPool()}.
		 */
		protected final DecodeValuePool xmlnsNamePool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #xmlnsLabelPool()}.
		 */
		protected final DecodeGroupPool xmlnsLabelHash;

		/**
		 * Dieses Feld speichert den {@code URI/Prefix}-{@link DecodeLabelPool} für {@link DecodeDocument#elementXmlnsPool()}.
		 */
		protected final DecodeLabelPool xmlnsLabelPool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #elementNamePool()}.
		 */
		protected final DecodeGroupPool elementNameHash;

		/**
		 * Dieses Feld speichert den {@code Name}-{@link DecodeValuePool} für {@link DecodeElement#label()}.
		 */
		protected final DecodeValuePool elementNamePool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #elementLabelPool()}.
		 */
		protected final DecodeGroupPool elementLabelHash;

		/**
		 * Dieses Feld speichert den {@code URI/Name}-{@link DecodeLabelPool} für {@link DecodeElement#label()}.
		 */
		protected final DecodeLabelPool elementLabelPool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #attributeNamePool()}.
		 */
		protected final DecodeGroupPool attributeNameHash;

		/**
		 * Dieses Feld speichert den {@code Name}-{@link DecodeValuePool} für {@link DecodeAttribute#label()}.
		 */
		protected final DecodeValuePool attributeNamePool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #attributeLabelPool()}.
		 */
		protected final DecodeGroupPool attributeLabelHash;

		/**
		 * Dieses Feld speichert den {@code URI/Name}-{@link DecodeLabelPool} für {@link DecodeAttribute#label()}.
		 */
		protected final DecodeLabelPool attributeLabelPool;

		/**
		 * Dieses Feld speichert den {@link DecodeGroupPool} für {@link DecodeElement#xmlns()}.
		 */
		protected final DecodeGroupPool elementXmlnsPool;

		/**
		 * Dieses Feld speichert den {@link DecodeGroupPool} für {@link DecodeElement#children()}.
		 */
		protected final DecodeGroupPool elementChildrenPool;

		/**
		 * Dieses Feld speichert den {@link DecodeGroupPool} für {@link DecodeElement#attributes()}.
		 */
		protected final DecodeGroupPool elementAttributesPool;

		/**
		 * Dieses Feld speichert den {@link DecodeElementPool}.
		 */
		protected final DecodeElementPool elementNodePool;

		/**
		 * Dieses Feld speichert den {@link DecodeAttributePool}.
		 */
		protected final DecodeAttributePool attributeNodePool;

		/**
		 * Dieses Feld speichert die {@code Hash-Tabelle} zu {@link #navigationPathPool()}.
		 */
		protected final DecodeGroupPool navigationPathHash;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente im {@link #navigationPathHash()} minus {@code 1}.
		 * 
		 * @see DecodeGroupPool#itemCount()
		 */
		protected final int navigationPathHashMask;

		/**
		 * Dieses Feld speichert den {@link DecodeGroupPool} für {@link Document#getElementById(String)}.
		 */
		protected final DecodeGroupPool navigationPathPool;

		/**
		 * Dieses Feld speichert den Index des {@link DecodeElement}s für {@link Document#getDocumentElement()}.
		 */
		protected final int documentElement;

		/**
		 * Dieser Konstrukteur initialisiert die {@link DecodeSource} und lädt die Header.
		 * 
		 * @param source {@link DecodeSource}.
		 * @throws IOException Wenn die gegebene {@link DecodeSource} eine {@link IOException} auslöst.
		 * @throws NullPointerException Wenn die gegebene {@link DecodeSource} {@code null} ist.
		 */
		public DecodeDocument(final DecodeSource source) throws IOException, NullPointerException {
			this.uriHash = new DecodeGroupPool(source);
			this.uriPool = new DecodeValuePool(source, this.uriHash);
			this.valueHash = new DecodeGroupPool(source);
			this.valuePool = new DecodeValuePool(source, this.valueHash);
			this.xmlnsEnabled = this.uriPool.itemCount != 0;
			this.xmlnsNameHash = new DecodeGroupPool(source);
			this.xmlnsNamePool = new DecodeValuePool(source, this.xmlnsNameHash);
			this.xmlnsLabelHash = new DecodeGroupPool(source);
			this.xmlnsLabelPool = new DecodeLabelPool(source, this.xmlnsLabelHash);
			this.elementNameHash = new DecodeGroupPool(source);
			this.elementNamePool = new DecodeValuePool(source, this.elementNameHash);
			this.elementLabelHash = new DecodeGroupPool(source);
			this.elementLabelPool = new DecodeLabelPool(source, this.elementLabelHash);
			this.attributeNameHash = new DecodeGroupPool(source);
			this.attributeNamePool = new DecodeValuePool(source, this.attributeNameHash);
			this.attributeLabelHash = new DecodeGroupPool(source);
			this.attributeLabelPool = new DecodeLabelPool(source, this.attributeLabelHash);
			this.elementXmlnsPool = new DecodeGroupPool(source);
			this.elementChildrenPool = new DecodeGroupPool(source);
			this.elementAttributesPool = new DecodeGroupPool(source);
			this.elementNodePool = new DecodeElementPool(source, this.xmlnsEnabled);
			this.attributeNodePool = new DecodeAttributePool(source);
			this.navigationPathHash = new DecodeGroupPool(source);
			this.navigationPathHashMask = this.navigationPathHash.itemCount - 1;
			if((this.navigationPathHashMask & this.navigationPathHash.itemCount) != 0)
				throw new IOException(new IllegalArgumentException("navigationPathHash.itemCount is no power of two"));
			this.navigationPathPool = new DecodeGroupPool(source);
			this.documentElement = Decoder.readInts(source, 1)[0];
		}

		/**
		 * Diese Methode gibt das Array aller {@link DecodePool}s zurück.
		 * 
		 * @see DecodeDocument#setMinSize(int)
		 * @see DecodeDocument#setMaxSize(int)
		 * @return Array aller {@link DecodePool}s.
		 */
		final DecodePool<?>[] pools() {
			return new DecodePool<?>[]{this.uriHash, this.uriPool, this.valueHash, this.valuePool, this.xmlnsNameHash, this.xmlnsNamePool, this.xmlnsLabelHash,
				this.xmlnsLabelPool, this.elementNameHash, this.elementNamePool, this.elementLabelHash, this.elementLabelPool, this.attributeNameHash,
				this.attributeNamePool, this.attributeLabelHash, this.attributeLabelPool, this.elementXmlnsPool, this.elementChildrenPool, this.elementAttributesPool,
				this.elementNodePool, this.attributeNodePool};
		}

		/**
		 * Diese Methode setzt die minimale Anzahl der Elemente aller {@link DecodePool}s.
		 * 
		 * @see DecodePool#getMinSize()
		 * @see DecodeDocument#uriHash()
		 * @see DecodeDocument#uriPool()
		 * @see DecodeDocument#valueHash()
		 * @see DecodeDocument#valuePool()
		 * @see DecodeDocument#xmlnsNameHash()
		 * @see DecodeDocument#xmlnsNamePool()
		 * @see DecodeDocument#xmlnsLabelHash()
		 * @see DecodeDocument#xmlnsLabelPool()
		 * @see DecodeDocument#elementNameHash()
		 * @see DecodeDocument#elementNamePool()
		 * @see DecodeDocument#elementLabelHash()
		 * @see DecodeDocument#elementLabelPool()
		 * @see DecodeDocument#attributeNameHash()
		 * @see DecodeDocument#attributeNamePool()
		 * @see DecodeDocument#attributeLabelHash()
		 * @see DecodeDocument#attributeLabelPool()
		 * @see DecodeDocument#elementXmlnsPool()
		 * @see DecodeDocument#elementChildrenPool()
		 * @see DecodeDocument#elementAttributesPool()
		 * @see DecodeDocument#elementNodePool()
		 * @see DecodeDocument#attributeNodePool()
		 * @param value minimale Anzahl der Elemente.
		 * @throws IllegalArgumentException Wenn die gegebene minimale Anzahl der Elemente kleiner als {@code 0} ist.
		 */
		public void setMinSize(final int value) throws IllegalArgumentException {
			for(final DecodePool<?> pool: this.pools()){
				pool.setMinSize(value);
			}
		}

		/**
		 * Diese Methode setzt die maximale Anzahl der Elemente aller {@link DecodePool}s.
		 * 
		 * @see DecodePool#getMaxSize()
		 * @see DecodeDocument#uriHash()
		 * @see DecodeDocument#uriPool()
		 * @see DecodeDocument#valueHash()
		 * @see DecodeDocument#valuePool()
		 * @see DecodeDocument#xmlnsNameHash()
		 * @see DecodeDocument#xmlnsNamePool()
		 * @see DecodeDocument#xmlnsLabelHash()
		 * @see DecodeDocument#xmlnsLabelPool()
		 * @see DecodeDocument#elementNameHash()
		 * @see DecodeDocument#elementNamePool()
		 * @see DecodeDocument#elementLabelHash()
		 * @see DecodeDocument#elementLabelPool()
		 * @see DecodeDocument#attributeNameHash()
		 * @see DecodeDocument#attributeNamePool()
		 * @see DecodeDocument#attributeLabelHash()
		 * @see DecodeDocument#attributeLabelPool()
		 * @see DecodeDocument#elementXmlnsPool()
		 * @see DecodeDocument#elementChildrenPool()
		 * @see DecodeDocument#elementAttributesPool()
		 * @see DecodeDocument#elementNodePool()
		 * @see DecodeDocument#attributeNodePool()
		 * @param value maximale Anzahl der Elemente.
		 * @throws IllegalArgumentException Wenn die gegebene maximale Anzahl der Elemente kleiner als {@code 0} ist.
		 */
		public void setMaxSize(final int value) {
			for(final DecodePool<?> pool: this.pools()){
				pool.setMaxSize(value);
			}
		}

		/**
		 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen {@link DecodeElement}s und {@link DecodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen {@code Prefix}.
		 * 
		 * @see Node#getPrefix()
		 * @see Node#getLocalName()
		 * @see Node#getNamespaceURI()
		 * @return {@code xmlns}-Aktivierung.
		 */
		public boolean isXmlnsEnabled() {
			return this.xmlnsEnabled;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #uriPool()} zurück.
		 * 
		 * @see DecodeValuePool#findValue(String)
		 * @return {@code Hash-Tabelle} zu {@link #uriPool()}.
		 */
		public DecodeGroupPool uriHash() {
			return this.uriHash;
		}

		/**
		 * Diese Methode gibt den {@code UIR}-{@link DecodeValuePool} für {@link DecodeLabel#uri()} zurück.
		 * 
		 * @see Node#getNamespaceURI()
		 * @return {@code UIR}-{@link DecodeValuePool} für {@link DecodeLabel#uri()}.
		 */
		public DecodeValuePool uriPool() {
			return this.uriPool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #valuePool()} zurück.
		 * 
		 * @see DecodeValuePool#findValue(String)
		 * @return {@code Hash-Tabelle} zu {@link #valuePool()}.
		 */
		public DecodeGroupPool valueHash() {
			return this.valueHash;
		}

		/**
		 * Diese Methode gibt den {@code Value}-{@link DecodeValuePool} für {@link DecodeDocument#elementChildrenPool()} und {@link DecodeAttribute#value()} zurück.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 * @return {@code Value}-{@link DecodeValuePool} für {@link DecodeDocument#elementChildrenPool()} und {@link DecodeAttribute#value()}.
		 */
		public DecodeValuePool valuePool() {
			return this.valuePool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #xmlnsNamePool()} zurück.
		 * 
		 * @see DecodeValuePool#findValue(String)
		 * @return {@code Hash-Tabelle} zu {@link #xmlnsNamePool()}.
		 */
		public DecodeGroupPool xmlnsNameHash() {
			return this.xmlnsNameHash;
		}

		/**
		 * Diese Methode gibt den {@code Prefix}-{@link DecodeValuePool} für {@link DecodeDocument#xmlnsLabelPool()} zurück.
		 * 
		 * @see Node#getPrefix()
		 * @return {@code Prefix}-{@link DecodeValuePool} für {@link DecodeDocument#xmlnsLabelPool()}.
		 */
		public DecodeValuePool xmlnsNamePool() {
			return this.xmlnsNamePool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #xmlnsLabelPool()} zurück.
		 * 
		 * @see DecodeLabelPool#findLabel(int, int)
		 * @return {@code Hash-Tabelle} zu {@link #xmlnsLabelPool()}.
		 */
		public DecodeGroupPool xmlnsLabelHash() {
			return this.xmlnsLabelHash;
		}

		/**
		 * Diese Methode gibt den {@code URI/Prefix}-{@link DecodeLabelPool} für {@link DecodeDocument#elementXmlnsPool()} zurück.
		 * 
		 * @see Node#getPrefix()
		 * @see Node#getNamespaceURI()
		 * @return {@code URI/Prefix}-{@link DecodeLabelPool} für {@link DecodeDocument#elementXmlnsPool()}.
		 */
		public DecodeLabelPool xmlnsLabelPool() {
			return this.xmlnsLabelPool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #elementNamePool()} zurück.
		 * 
		 * @see DecodeValuePool#findValue(String)
		 * @return {@code Hash-Tabelle} zu {@link #elementNamePool()}.
		 */
		public DecodeGroupPool elementNameHash() {
			return this.elementNameHash;
		}

		/**
		 * Diese Methode gibt den {@code Name}-{@link DecodeValuePool} für {@link DecodeElement#label()} zurück.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getLocalName()
		 * @return {@code Name}-{@link DecodeValuePool} für {@link DecodeElement#label()}.
		 */
		public DecodeValuePool elementNamePool() {
			return this.elementNamePool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #elementLabelPool()} zurück.
		 * 
		 * @see DecodeLabelPool#findLabel(int, int)
		 * @return {@code Hash-Tabelle} zu {@link #elementLabelPool()}.
		 */
		public DecodeGroupPool elementLabelHash() {
			return this.elementLabelHash;
		}

		/**
		 * Diese Methode gibt den {@code URI/Name}-{@link DecodeLabelPool} für {@link DecodeElement#label()} zurück.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @return {@code URI/Name}-{@link DecodeLabelPool} für {@link DecodeElement#label()}.
		 */
		public DecodeLabelPool elementLabelPool() {
			return this.elementLabelPool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #attributeNamePool()} zurück.
		 * 
		 * @see DecodeValuePool#findValue(String)
		 * @return {@code Hash-Tabelle} zu {@link #attributeNamePool()}.
		 */
		public DecodeGroupPool attributeNameHash() {
			return this.attributeNameHash;
		}

		/**
		 * Diese Methode gibt den {@code Name}-{@link DecodeValuePool} für {@link DecodeAttribute#label()} zurück.
		 * 
		 * @see Attr#getNodeName()
		 * @see Attr#getLocalName()
		 * @return {@code Name}-{@link DecodeValuePool} für {@link DecodeAttribute#label()}.
		 */
		public DecodeValuePool attributeNamePool() {
			return this.attributeNamePool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #attributeLabelPool()} zurück.
		 * 
		 * @see DecodeLabelPool#findLabel(int, int)
		 * @return {@code Hash-Tabelle} zu {@link #attributeLabelPool()}.
		 */
		public DecodeGroupPool attributeLabelHash() {
			return this.attributeLabelHash;
		}

		/**
		 * Diese Methode gibt den {@code URI/Name}-{@link DecodeLabelPool} für {@link DecodeAttribute#label()} zurück.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 * @return {@code URI/Name}-{@link DecodeLabelPool} für {@link DecodeAttribute#label()}.
		 */
		public DecodeLabelPool attributeLabelPool() {
			return this.attributeLabelPool;
		}

		/**
		 * Diese Methode gibt den {@link DecodeGroupPool} für {@link DecodeElement#xmlns()} zurück.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 * @return {@link DecodeGroupPool} für {@link DecodeElement#xmlns()}.
		 */
		public DecodeGroupPool elementXmlnsPool() {
			return this.elementXmlnsPool;
		}

		/**
		 * Diese Methode gibt den {@link DecodeGroupPool} für {@link DecodeElement#children()} zurück.
		 * 
		 * @see Element#getChildNodes()
		 * @return {@link DecodeGroupPool} für {@link DecodeElement#children()}.
		 */
		public DecodeGroupPool elementChildrenPool() {
			return this.elementChildrenPool;
		}

		/**
		 * Diese Methode gibt den {@link DecodeGroupPool} für {@link DecodeElement#attributes()} zurück.
		 * 
		 * @see Element#getAttributes()
		 * @return {@link DecodeGroupPool} für {@link DecodeElement#attributes()}.
		 */
		public DecodeGroupPool elementAttributesPool() {
			return this.elementAttributesPool;
		}

		/**
		 * Diese Methode gibt den {@link DecodeElementPool} zurück.
		 * 
		 * @return {@link DecodeElementPool}.
		 */
		public DecodeElementPool elementNodePool() {
			return this.elementNodePool;
		}

		/**
		 * Diese Methode gibt den {@link DecodeAttributePool} zurück.
		 * 
		 * @return {@link DecodeAttributePool}.
		 */
		public DecodeAttributePool attributeNodePool() {
			return this.attributeNodePool;
		}

		/**
		 * Diese Methode gibt die {@code Hash-Tabelle} zu {@link #navigationPathPool()} zurück.
		 * 
		 * @see DecodeValuePool#findValue(String)
		 * @return {@code Hash-Tabelle} zu {@link #navigationPathPool()}.
		 */
		public DecodeGroupPool navigationPathHash() {
			return this.navigationPathHash;
		}

		/**
		 * Diese Methode gibt den {@link DecodeGroupPool} für {@link Document#getElementById(String)} zurück. Der erste Index jeder {@link DecodeGroup} referenziert den {@link DecodeValue} mit der {@code ID} des {@link Element}s und die anderen Indices beschriben die {@code Child}-{@link Node}-Indices zur Navigation beginnend bei {@link Document}{@code .}{@link DecodeDocumentNodeAdapter#getDocumentElement() getDocumentElement}{@code ()}.
		 * 
		 * @see Attr#isId()
		 * @see Document#getElementById(String)
		 * @return {@link DecodeGroupPool} für {@link Document#getElementById(String)}.
		 */
		public DecodeGroupPool navigationPathPool() {
			return this.navigationPathPool;
		}

		/**
		 * Diese Methode gibt den Index des {@link DecodeElement}s für {@link Document#getDocumentElement()} zurück.
		 * 
		 * @see Document#getDocumentElement()
		 * @return Index des {@link DecodeElement}s für {@link Document#getDocumentElement()}.
		 */
		public int documentElement() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "DecodeDocument", //
				"uriPool", this.uriPool, //
				"valuePool", this.valuePool, //
				"xmlnsNamePool", this.xmlnsNamePool, //
				"xmlnsLabelPool", this.xmlnsLabelPool, //
				"elementNamePool", this.elementNamePool, //
				"elementLabelPool", this.elementLabelPool, //
				"attributeNamePool", this.attributeNamePool, //
				"attributeLabelPool", this.attributeLabelPool, //
				"elementXmlnsPool", this.elementXmlnsPool, //
				"elementChildrenPool", this.elementChildrenPool, //
				"elementAttributesPool", this.elementAttributesPool, //
				"elementNodePool", this.elementNodePool, //
				"attributeNodePool", this.attributeNodePool, //
				"navigationPathPool", this.navigationPathPool, //
				"documentElement", this.documentElement //
				);
		}

	}

	/**
	 * Diese Klasse implementiert die Methoden zum Auslesen eines {@link DecodeDocument}s als Grundlage eines {@link DecodeDocumentNodeAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAdapter {

		/**
		 * Diese Klasse implementiert das {@link Comparable} für {@link DecodeAdapter#lookupPrefix_(int[], int)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static class XmlnsUriIndexComparable implements Comparable<DecodeLabel> {

			/**
			 * Dieses Feld speichert den Index des {@code URI}-{@link DecodeValue}s.
			 */
			final int uriIndex;

			/**
			 * Dieser Konstrukteur initialisiert den Index des {@code URI}-{@link DecodeValue}s.
			 * 
			 * @param uriIndex Index des {@code URI}-{@link DecodeValue}s.
			 */
			public XmlnsUriIndexComparable(final int uriIndex) {
				this.uriIndex = uriIndex;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeLabel value) {
				return Comparators.compare(this.uriIndex, value.uri);
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Comparable} für {@link DecodeAdapter#lookupPrefix_(int[], String)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class XmlnsUriStringComparable implements Comparable<DecodeLabel> {

			/**
			 * Dieses Feld speichert {@link DecodeDocument#uriPool()}.
			 */
			final DecodeValuePool uriPool;

			/**
			 * Dieses Feld speichert den {@code URI}-{@link String}.
			 */
			final String uriString;

			/**
			 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und {@code URI}-{@link String}.
			 * 
			 * @param adapter {@link DecodeAdapter}.
			 * @param uriString {@code URI}-{@link String}.
			 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
			 */
			public XmlnsUriStringComparable(final DecodeAdapter adapter, final String uriString) throws NullPointerException {
				if(uriString == null) throw new NullPointerException();
				this.uriPool = adapter.documentNode.uriPool;
				this.uriString = uriString;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeLabel value) {
				return Comparators.compare(this.uriString, this.uriPool.get(value.uri).string);
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Comparable} für {@link DecodeAdapter#attributeNode(int[], int)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static class AttributeLabelComparable implements Comparable<DecodeAttribute> {

			/**
			 * Dieses Feld speichert den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}- {@link DecodeValue}s.
			 */
			final int labelIndex;

			/**
			 * Dieser Konstrukteur initialisiert den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}- {@link DecodeValue}s.
			 * 
			 * @param labelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}- {@link DecodeValue}s.
			 */
			public AttributeLabelComparable(final int labelIndex) {
				this.labelIndex = labelIndex;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeAttribute value) {
				return Comparators.compare(this.labelIndex, value.label);
			}

		}

		/**
		 * Diese Klasse implementiert ein {@link Comparable} für {@link DecodeAdapter#attributeNode(int[], String, String)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static class AttributeNameComparable implements Comparable<DecodeAttribute> {

			/**
			 * Dieses Feld speichert {@link DecodeDocument#attributeNamePool()}.
			 */
			final DecodeValuePool namePool;

			/**
			 * Dieses Feld speichert den {@code Name}-{@link String}.
			 */
			final String nameString;

			/**
			 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und {@code Name}-{@link String}.
			 * 
			 * @param adapter {@link DecodeAdapter}.
			 * @param nameString {@code Name}-{@link String}.
			 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
			 */
			public AttributeNameComparable(final DecodeAdapter adapter, final String nameString) throws NullPointerException {
				if(nameString == null) throw new NullPointerException();
				this.namePool = adapter.documentNode.attributeNamePool;
				this.nameString = nameString;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeAttribute attributeNode) {
				return Comparators.compare(this.nameString, this.namePool.get(attributeNode.label).string);
			}

		}

		/**
		 * Diese Klasse implementiert ein {@link Comparable} für {@link DecodeAdapter#attributeNode(int[], String, String)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static class AttributeUriNameComparable extends AttributeNameComparable {

			/**
			 * Dieses Feld speichert {@link DecodeDocument#uriPool()}.
			 */
			final DecodeValuePool uriPool;

			/**
			 * Dieses Feld speichert den {@code URI}-{@link String}.
			 */
			final String uriString;

			/**
			 * Dieses Feld speichert {@link DecodeDocument#attributeLabelPool()}.
			 */
			final DecodeLabelPool labelPool;

			/**
			 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} sowie {@code URI}- und {@code Name}-{@link String}.
			 * 
			 * @param adapter {@link DecodeAdapter}.
			 * @param uriString {@code URI}-{@link String}.
			 * @param nameString {@code Name}-{@link String}.
			 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
			 */
			public AttributeUriNameComparable(final DecodeAdapter adapter, final String uriString, final String nameString) throws NullPointerException {
				super(adapter, nameString);
				if(uriString == null) throw new NullPointerException();
				this.uriPool = adapter.documentNode.uriPool;
				this.labelPool = adapter.documentNode.attributeLabelPool;
				this.uriString = uriString;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeAttribute value) {
				final DecodeLabel attributeLabel = this.labelPool.get(value.label);
				final int comp = Comparators.compare(this.nameString, this.namePool.get(attributeLabel.name).string);
				if(comp != 0) return comp;
				return Comparators.compare(this.uriString, this.uriPool.get(attributeLabel.uri).string);
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Comparable} für {@link DecodeAdapter#navigationPath(int[], int)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static class NavigationPathIdIndexComparable implements Comparable<DecodeGroup> {

			/**
			 * Dieses Feld speichert den Index des {@code URI }-{@link DecodeValue}s.
			 */
			final int idIndex;

			/**
			 * Dieser Konstrukteur initialisiert den Index des {@code ID}-{@link DecodeValue}s.
			 * 
			 * @param idIndex Index des {@code ID}-{@link DecodeValue}s.
			 */
			public NavigationPathIdIndexComparable(final int idIndex) {
				this.idIndex = idIndex;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeGroup value) {
				return Comparators.compare(this.idIndex, value.indices[0]);
			}

		}

		/**
		 * Diese Klasse implementiert das {@link Comparable} für {@link DecodeAdapter#navigationPath(int[], String)}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class NavigationPathIdStringComparable implements Comparable<DecodeGroup> {

			/**
			 * Dieses Feld speichert {@link DecodeDocument#valuePool()}.
			 */
			final DecodeValuePool valuePool;

			/**
			 * Dieses Feld speichert den {@code ID}-{@link String}.
			 */
			final String idString;

			/**
			 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und {@code ID}-{@link String}.
			 * 
			 * @param adapter {@link DecodeAdapter}.
			 * @param idString {@code ID}-{@link String}.
			 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
			 */
			public NavigationPathIdStringComparable(final DecodeAdapter adapter, final String idString) throws NullPointerException {
				if(idString == null) throw new NullPointerException();
				this.valuePool = adapter.documentNode.valuePool;
				this.idString = idString;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeGroup value) {
				return Comparators.compare(this.idString, this.valuePool.get(value.indices[0]).string);
			}

		}

		/**
		 * Dieses Feld speichert die leere {@link ElementList}.
		 */
		public static final ElementList VOID_NODE_LIST = new ElementList() {

			@Override
			public DecodeElementNodeAdapter item(final int index) {
				return null;
			}

			@Override
			public int getLength() {
				return 0;
			}

		};

		/**
		 * Dieses Feld speichert die leere {@link TypeInfo}.
		 */
		public static final TypeInfo VOID_TYPE_INFO = new TypeInfo() {

			@Override
			public String getTypeName() {
				return null;
			}

			@Override
			public String getTypeNamespace() {
				return null;
			}

			@Override
			public boolean isDerivedFrom(final String typeNamespaceArg, final String typeNameArg, final int derivationMethod) {
				return false;
			}

		};

		/**
		 * Dieses Feld speichert die Verschiebung des Indexes der {@link DecodeElement}s in den Indices der {@link DecodeGroup}. Der Wert entspricht der Anzahl der {@link DecodeValue}s in {@link DecodeDocument#valuePool}.
		 * 
		 * @see #getChildNode(DecodeElementNodeAdapter, int, int)
		 */
		protected final int childOffset;

		/**
		 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
		 */
		protected final boolean xmlnsEnabled;

		/**
		 * Dieses Feld speichert den {@link DecodeDocument}.
		 */
		protected final DecodeDocument documentNode;

		/**
		 * Dieses Feld speichert den {@link DecodeDocumentNodeAdapter}.
		 */
		protected final DecodeDocumentNodeAdapter documentNodeAdapter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeDocument}.
		 * 
		 * @param document {@link DecodeDocument}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeDocument} {@code null} ist.
		 */
		public DecodeAdapter(final DecodeDocument document) throws NullPointerException {
			this.childOffset = document.valuePool.itemCount;
			this.documentNode = document;
			this.xmlnsEnabled = document.xmlnsEnabled;
			this.documentNodeAdapter = new DecodeDocumentNodeAdapter(this);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#uriPool() uriPool}{@code ().}{@link DecodeValuePool#get(int) get}{@code (uriIndex)} zurück.
		 * 
		 * @param uriIndex Index.
		 * @return {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue uri(final int uriIndex) throws IndexOutOfBoundsException {
			return this.documentNode.uriPool.get(uriIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#uriPool() uriPool}{@code ().}{@link DecodeValuePool#findValue(String) findValue}{@code (uriString)} zurück.
		 * 
		 * @param uriString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue uri(final String uriString) throws NullPointerException {
			return this.documentNode.uriPool.findValue(uriString);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#valuePool() valuePool}{@code ().}{@link DecodeValuePool#get(int) get}{@code (valueIndex)} zurück.
		 * 
		 * @param valueIndex Index.
		 * @return {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue value(final int valueIndex) throws IndexOutOfBoundsException {
			return this.documentNode.valuePool.get(valueIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#valuePool() valuePool}{@code ().}{@link DecodeValuePool#findValue(String) findValue}{@code (valueString)} zurück.
		 * 
		 * @param valueString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue value(final String valueString) throws NullPointerException {
			return this.documentNode.valuePool.findValue(valueString);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#xmlnsNamePool() xmlnsNamePool}{@code ().}{@link DecodeValuePool#get(int) get}{@code (xmlnsNameIndex)} zurück.
		 * 
		 * @param xmlnsNameIndex Index.
		 * @return {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue xmlnsName(final int xmlnsNameIndex) throws IndexOutOfBoundsException {
			return this.documentNode.xmlnsNamePool.get(xmlnsNameIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#xmlnsNamePool() xmlnsNamePool}{@code ().}{@link DecodeValuePool#findValue(String) findValue}{@code (xmlnsNameString)} zurück.
		 * 
		 * @param xmlnsNameString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue xmlnsName(final String xmlnsNameString) throws NullPointerException {
			return this.documentNode.xmlnsNamePool.findValue(xmlnsNameString);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#xmlnsLabelPool() xmlnsLabelPool}{@code ().}{@link DecodeLabelPool#get(int) get}{@code (xmlnsLabelIndex)} zurück.
		 * 
		 * @param xmlnsLabelIndex Index.
		 * @return {@link DecodeLabel}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeLabel xmlnsLabel(final int xmlnsLabelIndex) {
			return this.documentNode.xmlnsLabelPool.get(xmlnsLabelIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#xmlnsLabelPool() xmlnsLabelPool}{@code ().}{@link DecodeLabelPool#findLabel(int, int) findLabel}{@code (xmlnsUriIndex, xmlnsNameIndex)} zurück.
		 * 
		 * @param xmlnsUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @param xmlnsNameIndex Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public final DecodeLabel xmlnsLabel(final int xmlnsUriIndex, final int xmlnsNameIndex) {
			return this.documentNode.xmlnsLabelPool.findLabel(xmlnsUriIndex, xmlnsNameIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementNamePool() elementNamePool}{@code ().}{@link DecodeValuePool#get(int) get}{@code (elementNameIndex)} zurück.
		 * 
		 * @param elementNameIndex Index.
		 * @return {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue elementName(final int elementNameIndex) {
			return this.documentNode.elementNamePool.get(elementNameIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementNamePool() elementNamePool}{@code ().}{@link DecodeValuePool#findValue(String) findValue}{@code (elementNameString)} zurück.
		 * 
		 * @param elementNameString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue elementName(final String elementNameString) {
			return this.documentNode.elementNamePool.findValue(elementNameString);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementLabelPool() elementLabelPool}{@code ().}{@link DecodeLabelPool#get(int) get}{@code (elementLabelIndex)} zurück.
		 * 
		 * @param elementLabelIndex Index.
		 * @return {@link DecodeLabel}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeLabel elementLabel(final int elementLabelIndex) {
			return this.documentNode.elementLabelPool.get(elementLabelIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementLabelPool() elementLabelPool}{@code ().}{@link DecodeLabelPool#findLabel(int, int) findLabel}{@code (elementUriIndex, elementNameIndex)} zurück.
		 * 
		 * @param elementUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @param elementNameIndex Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public final DecodeLabel elementLabel(final int elementUriIndex, final int elementNameIndex) {
			return this.documentNode.elementLabelPool.findLabel(elementUriIndex, elementNameIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeNamePool() attributeNamePool}{@code ().}{@link DecodeValuePool#get(int) get}{@code (attributeNameIndex)} zurück.
		 * 
		 * @param attributeNameIndex Index.
		 * @return {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue attributeName(final int attributeNameIndex) throws IndexOutOfBoundsException {
			return this.documentNode.attributeNamePool.get(attributeNameIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeNamePool() attributeNamePool}{@code ().}{@link DecodeValuePool#findValue(String) findValue}{@code (attributeNameString)} zurück.
		 * 
		 * @param attributeNameString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue attributeName(final String attributeNameString) throws NullPointerException {
			return this.documentNode.attributeNamePool.findValue(attributeNameString);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeLabelPool() attributeLabelPool}{@code ().}{@link DecodeLabelPool#get(int) get}{@code (attributeLabelIndex)} zurück.
		 * 
		 * @param attributeLabelIndex Index.
		 * @return {@link DecodeLabel}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeLabel attributeLabel(final int attributeLabelIndex) throws IndexOutOfBoundsException {
			return this.documentNode.attributeLabelPool.get(attributeLabelIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeLabelPool() attributeLabelPool}{@code ().}{@link DecodeLabelPool#findLabel(int, int) findLabel}{@code (attributeUriIndex, attributeNameIndex)} zurück.
		 * 
		 * @param attributeUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @param attributeNameIndex Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public final DecodeLabel attributeLabel(final int attributeUriIndex, final int attributeNameIndex) {
			return this.documentNode.attributeLabelPool.findLabel(attributeUriIndex, attributeNameIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementXmlnsPool() elementXmlnsPool}{@code ().}{@link DecodeGroupPool#get(int) get}{@code (elementXmlnsIndex)} zurück.
		 * 
		 * @param elementXmlnsIndex Index.
		 * @return {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup elementXmlns(final int elementXmlnsIndex) throws IndexOutOfBoundsException {
			return this.documentNode.elementXmlnsPool.get(elementXmlnsIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementXmlnsPool() elementXmlnsPool}{@code ().}{@link DecodeGroupPool#get(int) get}{@code (elementChildrenIndex)} zurück.
		 * 
		 * @param elementChildrenIndex Index.
		 * @return {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup elementChildren(final int elementChildrenIndex) throws IndexOutOfBoundsException {
			return this.documentNode.elementChildrenPool.get(elementChildrenIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementXmlnsPool() elementXmlnsPool}{@code ().}{@link DecodeGroupPool#get(int) get}{@code (elementAttributesIndex)} zurück.
		 * 
		 * @param elementAttributesIndex Index.
		 * @return {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup elementAttributes(final int elementAttributesIndex) throws IndexOutOfBoundsException {
			return this.documentNode.elementAttributesPool.get(elementAttributesIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#elementNodePool() elementNodePool}{@code ().}{@link DecodeElementPool#get(int) get}{@code (elementNodeIndex)} zurück.
		 * 
		 * @param elementNodeIndex Index.
		 * @return {@link DecodeElement}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeElement elementNode(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.documentNode.elementNodePool.get(elementNodeIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeNodePool() attributeNodePool}{@code ().}{@link DecodeAttributePool#get(int) get}{@code (attributeNodeIndex)} zurück.
		 * 
		 * @param attributeNodeIndex Index.
		 * @return {@link DecodeAttribute}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeAttribute attributeNode(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.documentNode.attributeNodePool.get(attributeNodeIndex);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#navigationPathPool() navigationPathPool}{@code ().}{@link DecodeGroupPool#get(int) get}{@code (navigationPathIndex)} zurück.
		 * 
		 * @param navigationPathIndex Index.
		 * @return {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup navigationPath(final int navigationPathIndex) throws IndexOutOfBoundsException {
			return this.documentNode.navigationPathPool.get(navigationPathIndex);
		}

		/**
		 * Diese Methode gibt das {@link DecodeDocument} mit allen {@link DecodePool}s zurück.
		 * 
		 * @return {@link DecodeDocument}.
		 */
		public final DecodeDocument documentNode() {
			return this.documentNode;
		}

		/**
		 * Diese Methode gibt die Verschiebung der Indices der {@link DecodeElement}s in den Indices der {@link DecodeElement#children()} zurück. Der Wert entspricht {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#valuePool() valuePool}{@code ().}{@link DecodeValuePool#itemCount() itemCount}{@code ()}.
		 * 
		 * @see DecodeAdapter#getChildNode(DecodeElementNodeAdapter, int, int)
		 * @return Verschiebung der Indices der {@link DecodeElement}s.
		 */
		public final int childOffset() {
			return this.childOffset;
		}

		/**
		 * Diese Methode sucht linear im gegebenen Index-Array ab der gegebenen Position nach dem {@link DecodeElement} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Position oder {@code -1} zurück.
		 * 
		 * @see DecodeElement#label()
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Index des ersten Treffers oder {@code -1}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final int childIndex(final int[] indices, final int childLabelIndex, final int childIndex) throws NullPointerException {
			if(childIndex < 0) return -1;
			for(int index = childIndex, size = indices.length; index < size; index++){
				final int childNodeIndex = indices[index] - this.childOffset;
				if(childNodeIndex >= 0){
					final DecodeElement elementNode = this.elementNode(childNodeIndex);
					if(elementNode.label == childLabelIndex) return index;
				}
			}
			return -1;
		}

		/**
		 * Diese Methode sucht linear im gegebenen Index-Array ab der gegebenen Position nach dem {@link DecodeElement} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Position oder {@code -1} zurück. Wenn die {@code xmlns}-Aktivierung {@code false} ist, wird nur nach dem {@code Name}-{@link String} gesucht.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childUriString {@code URI}-{@link String}.
		 * @param childNameString {@code Name}-{@link String}.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Index des ersten Treffers oder {@code -1}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final int childIndex(final int[] indices, final String childUriString, final String childNameString, final int childIndex)
			throws NullPointerException {
			if(childIndex < 0) return -1;
			if(this.xmlnsEnabled){
				for(int index = childIndex, size = indices.length; index < size; index++){
					final int elementNodeIndex = indices[index] - this.childOffset;
					if(elementNodeIndex >= 0){
						final DecodeLabel elementLabel = this.elementLabel(this.elementNode(elementNodeIndex).label);
						if(childNameString.equals(this.elementName(elementLabel.name).string) && childUriString.equals(this.uri(elementLabel.uri).string)) return index;
					}
				}
			}else{
				for(int index = childIndex, size = indices.length; index < size; index++){
					final int elementNodeIndex = indices[index] - this.childOffset;
					if(elementNodeIndex >= 0){
						final DecodeValue elementName = this.elementName(this.elementNode(elementNodeIndex).label);
						if(childNameString.equals(elementName.string)) return index;
					}
				}
			}
			return -1;
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeNodePool() attributeNodePool}{@code ().}{@link DecodeAttributePool#find(int[], Comparable) find}{@code (indices, ...)} nach dem {@link DecodeAttribute} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dieses oder {@code null} zurück.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeAttribute} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeAttribute attributeNode(final int[] indices, final int attributeLabelIndex) throws NullPointerException {
			if(indices.length == 0) return null;
			return this.documentNode.attributeNodePool.find(indices, new AttributeLabelComparable(attributeLabelIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#attributeNodePool() attributeNodePool}{@code ().}{@link DecodeAttributePool#find(int[], Comparable) find}{@code (indices, ...)} nach dem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dieses oder {@code null} zurück. Wenn die {@code xmlns}-Aktivierung {@code false} ist, wird nur nach dem {@code Name}-{@link String} gesucht.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return {@link DecodeAttribute} oder {@code null}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final DecodeAttribute attributeNode(final int[] indices, final String attributeUriString, final String attributeNameString)
			throws NullPointerException {
			if(indices.length == 0) return null;
			if(!this.xmlnsEnabled) return this.documentNode.attributeNodePool.find(indices, new AttributeNameComparable(this, attributeNameString));
			return this.documentNode.attributeNodePool.find(indices, new AttributeUriNameComparable(this, attributeUriString, attributeNameString));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#navigationPathPool() navigationPathPool}{@code ().}{@link DecodeGroupPool#find(int[], Comparable) find}{@code (...)} nach der {@link DecodeGroup} mit dem gegebenen Index des {@code ID}-{@link DecodeValue}s als erstes Element seiner {@link DecodeGroup#indices()} und gibt diese oder {@code null} zurück. Wenn das gegebene Index-Array {@code null} ist, wird in allen Elementen von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#navigationPathPool() navigationPool}{@code ()} gesucht.
		 * 
		 * @see Attr#isId()
		 * @see Document#getElementById(String)
		 * @param indices Index-Array oder {@code null}.
		 * @param idIndex Index des {@code ID}-{@link DecodeValue}s.
		 * @return {@link DecodeGroup} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup navigationPath(final int[] indices, final int idIndex) throws IndexOutOfBoundsException {
			final DecodeGroupPool navigationPathPool = this.documentNode.navigationPathPool;
			if(navigationPathPool.itemCount == 0) return null;
			if(indices == null) return this.navigationPath(null, this.value(idIndex).string);
			if(indices.length == 0) return null;
			return navigationPathPool.find(indices, new NavigationPathIdIndexComparable(idIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#navigationPathPool() navigationPathPool}{@code ().}{@link DecodeGroupPool#find(int[], Comparable) find}{@code (...)} nach der {@link DecodeGroup} mit dem gegebenen {@code ID}-{@link DecodeValue}s als erste Referenz seiner {@link DecodeGroup#indices()} und gibt diese oder {@code null} zurück. Wenn das gegebene Index-Array {@code null} ist, wird in allen Elementen von {@code this.}{@link DecodeAdapter#documentNode() documentNode}{@code ().}{@link DecodeDocument#navigationPathPool() navigationPool}{@code ()} gesucht.
		 * 
		 * @see Attr#isId()
		 * @see Document#getElementById(String)
		 * @param indices Index-Array oder {@code null}.
		 * @param idString {@code ID}-{@link String}.
		 * @return {@link DecodeGroup} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeGroup navigationPath(final int[] indices, final String idString) throws NullPointerException {
			final DecodeGroupPool navigationPathPool = this.documentNode.navigationPathPool;
			if(navigationPathPool.itemCount == 0) return null;
			if(indices == null){
				final int navigationPathHashMask = this.documentNode.navigationPathHashMask;
				if(navigationPathHashMask < 0) return navigationPathPool.find(new NavigationPathIdStringComparable(this, idString));
				final int[] indices2 = this.documentNode.navigationPathHash.get(Coder.hashString(idString) & navigationPathHashMask).indices;
				if(indices2.length == 0) return null;
				return navigationPathPool.find(indices2, new NavigationPathIdStringComparable(this, idString));
			}
			if(indices.length == 0) return null;
			return navigationPathPool.find(indices, new NavigationPathIdStringComparable(this, idString));
		}

		/**
		 * Diese Methode implementiert {@link DecodeNodeAdapter#getOwnerDocument()}.
		 * 
		 * @return {@link DecodeNodeAdapter#getOwnerDocument()}.
		 */
		public final DecodeDocumentNodeAdapter getOwnerDocument() {
			return this.documentNodeAdapter;
		}

		/**
		 * Diese Methode implementiert {@link DecodeTextNodeAdapter#getNodeValue()}.
		 * 
		 * @param textNodeIndex Index des {@link DecodeValue}s.
		 * @return {@link DecodeTextNodeAdapter#getNodeValue()}.
		 */
		public final String getTextNodeValue(final int textNodeIndex) {
			return this.value(textNodeIndex).string;
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#children(DecodeElement) children}{@code (this.}{@link DecodeAdapter#elementNode(int) elementNode}{@code (elementNodeIndex))} zurück.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return Indices der {@link DecodeElement#children()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		final int[] children(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.children(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#elementChildren(int) elementChildren}{@code (elementNode.}{@link DecodeElement#children children}{@code ).}{@link DecodeGroup#indices indices} zurück.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return Indices der {@link DecodeElement#children()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		final int[] children(final DecodeElement elementNode) throws NullPointerException {
			return this.elementChildren(elementNode.children).indices;
		}

		/**
		 * Diese Methode implementiert {@link DecodeChildNodesAdapter#item(int)}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param childIndex Index des {@link DecodeChildNodeAdapter}s.
		 * @return {@link DecodeTextNodeAdapter} bzw. {@link DecodeElementNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeChildNodeAdapter getChildNode(final DecodeElementNodeAdapter parentNode, final int childIndex) throws NullPointerException {
			if(childIndex < 0) return null;
			return this.getChildNode(parentNode, this.children(parentNode.index), childIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeChildNodesAdapter#item(int)}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param nodeIndex Index des {@link DecodeValue}s bzw. des {@link DecodeElement}s {@code +} {@link DecodeAdapter#childOffset() childOffset}{@code ()}.
		 * @param childIndex Index des {@link DecodeChildNodeAdapter}s im {@link DecodeChildNodesAdapter}.
		 * @return {@link DecodeTextNodeAdapter} bzw. {@link DecodeElementNodeAdapter}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeChildNodeAdapter getChildNode(final DecodeElementNodeAdapter parentNode, final int nodeIndex, final int childIndex)
			throws NullPointerException {
			if(nodeIndex < this.childOffset) return new DecodeTextNodeAdapter(parentNode, nodeIndex, childIndex);
			return new DecodeElementNodeAdapter(parentNode, nodeIndex - this.childOffset, childIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeChildNodesAdapter#item(int)}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childIndex Index des {@link DecodeChildNodeAdapter}s im Index-Array.
		 * @return {@link DecodeTextNodeAdapter} bzw. {@link DecodeElementNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeChildNodeAdapter getChildNode(final DecodeElementNodeAdapter parentNode, final int[] indices, final int childIndex)
			throws NullPointerException {
			if((childIndex < 0) || (childIndex >= indices.length)) return null;
			return this.getChildNode(parentNode, indices[childIndex], childIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getFirstChild()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @return {@link DecodeElementNodeAdapter#getFirstChild()}.
		 */
		public final DecodeChildNodeAdapter getChildNodeFirst(final DecodeElementNodeAdapter parentNode) {
			final int[] children = this.children(parentNode.index);
			return this.getChildNode(parentNode, children, 0);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getLastChild()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @return {@link DecodeElementNodeAdapter#getLastChild()}.
		 */
		public final DecodeChildNodeAdapter getChildNodeLast(final DecodeElementNodeAdapter parentNode) {
			final int[] children = this.children(parentNode.index);
			return this.getChildNode(parentNode, children, children.length - 1);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#getChildNodeByLabel(DecodeElementNodeAdapter, int[], int, int) getChildNodeByLabel}{@code (..., childLabelIndex, childIndex)} nach einem {@link DecodeElement} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen {@link DecodeElementNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], int, int)
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param childLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param childIndex Beginn der linearen Suche.
		 * @return {@link DecodeElementNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeElementNodeAdapter getChildNodeByLabel(final DecodeElementNodeAdapter parentNode, final int childLabelIndex, final int childIndex)
			throws NullPointerException {
			return this.getChildNodeByLabel(parentNode, this.children(parentNode.index), childLabelIndex, childIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#childIndex(int[], int, int) childIndex}{@code (indices, childLabelIndex, childIndex)} nach einem {@link DecodeElement} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen {@link DecodeElementNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], int, int)
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param childIndex Beginn der linearen Suche.
		 * @return {@link DecodeElementNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeElementNodeAdapter getChildNodeByLabel(final DecodeElementNodeAdapter parentNode, final int[] indices, final int childLabelIndex,
			final int childIndex) throws NullPointerException {
			return this.getChildNodeBy_(parentNode, indices, this.childIndex(indices, childLabelIndex, childIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#getChildNodeByUriName(DecodeElementNodeAdapter, int[], String, String, int) getChildNodeByUriName}{@code (..., childUriString, childNameString, childIndex)} nach einem {@link DecodeElement} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen {@link DecodeElementNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], int, int)
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param childUriString {@code URI}-{@link String}.
		 * @param childNameString {@code Name}-{@link String}.
		 * @param childIndex Beginn der linearen Suche.
		 * @return {@link DecodeElementNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeElementNodeAdapter getChildNodeByUriName(final DecodeElementNodeAdapter parentNode, final String childUriString,
			final String childNameString, final int childIndex) {
			return this.getChildNodeByUriName(parentNode, this.children(parentNode.index), childUriString, childNameString, childIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#childIndex(int[], String, String, int) childIndex}{@code (indices, childUriString, childNameString, childIndex)} nach einem {@link DecodeElement} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen {@link DecodeElementNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], String, String, int)
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childUriString {@code URI}-{@link String}.
		 * @param childNameString {@code Name}-{@link String}.
		 * @param childIndex Beginn der linearen Suche.
		 * @return {@link DecodeElementNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeElementNodeAdapter getChildNodeByUriName(final DecodeElementNodeAdapter parentNode, final int[] indices, final String childUriString,
			final String childNameString, final int childIndex) throws NullPointerException {
			return this.getChildNodeBy_(parentNode, indices, this.childIndex(indices, childUriString, childNameString, childIndex));
		}

		/**
		 * Diese Methode gibt den {@code childIndex}-te {@link DecodeElementNodeAdapter} oder {@code null} zurück.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childIndex Index des {@link DecodeElementNodeAdapter}s im Index-Array.
		 * @return {@link DecodeElementNodeAdapter}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		final DecodeElementNodeAdapter getChildNodeBy_(final DecodeElementNodeAdapter parentNode, final int[] indices, final int childIndex)
			throws NullPointerException {
			if(childIndex < 0) return null;
			return new DecodeElementNodeAdapter(parentNode, indices[childIndex] - this.childOffset, childIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#hasChildNodes()}.
		 * 
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#hasChildNodes()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final boolean hasChildNodes(final int parentIndex) throws IndexOutOfBoundsException {
			return this.children(parentIndex).length != 0;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#hasChildNodes()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @return {@link DecodeElementNodeAdapter#hasChildNodes()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final boolean hasChildNodes(final DecodeElement parentNode) throws NullPointerException {
			return this.children(parentNode).length != 0;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getChildNodes()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @return {@link DecodeElementNodeAdapter#getChildNodes()}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeChildNodesAdapter getChildNodes(final DecodeElementNodeAdapter parentNode) throws NullPointerException {
			return new DecodeChildNodesAdapter(parentNode, this.children(parentNode.index));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#getChildContentByLabel(int[], int, int) getChildContentByLabel}{@code (..., childLabelIndex, childIndex)} nach einem {@link DecodeElement} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Textwert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getChildContentByLabel(int[], int, int)
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param childLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Textwert oder {@code ""}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final String getChildContentByLabel(final int parentIndex, final int childLabelIndex, final int childIndex) throws IndexOutOfBoundsException {
			return this.getChildContentByLabel(this.children(parentIndex), childLabelIndex, childIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#childIndex(int[], int, int) childIndex}{@code (indices, childLabelIndex, childIndex)} nach einem {@link DecodeElement} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Textwert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], int, int)
		 * @see DecodeAdapter#getElementContent(int)
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Textwert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String getChildContentByLabel(final int[] indices, final int childLabelIndex, final int childIndex) throws NullPointerException {
			return this.getChildContentBy_(indices, this.childIndex(indices, childLabelIndex, childIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#getChildContentByLabel(int[], int, int) getChildContentByLabel}{@code (..., childLabelIndex, childIndex)} nach einem {@link DecodeElement} mit dem gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Textwert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getChildContentByLabel(int[], int, int)
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param childLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Textwert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getChildContentByLabel(final DecodeElement parentNode, final int childLabelIndex, final int childIndex) throws NullPointerException {
			return this.getChildContentByLabel(this.children(parentNode), childLabelIndex, childIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#getChildNodeByUriName(DecodeElementNodeAdapter, int[], String, String, int) getChildNodeByUriName}{@code (..., childUriString, childNameString, childIndex)} nach einem {@link DecodeElement} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Textwert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], int, int)
		 * @see DecodeAdapter#getElementContent(int)
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param childUriString {@code URI}-{@link String}.
		 * @param childNameString {@code Name}-{@link String}.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Textwert oder {@code ""}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final String getChildContentByUriName(final int parentIndex, final String childUriString, final String childNameString, final int childIndex)
			throws IndexOutOfBoundsException {
			return this.getChildContentByUriName(this.children(parentIndex), childUriString, childNameString, childIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#childIndex(int[], String, String, int) childIndex}{@code (indices, childUriString, childNameString, childIndex)} nach einem {@link DecodeElement} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Textwert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], String, String, int)
		 * @see DecodeAdapter#getElementContent(int)
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childUriString {@code URI}-{@link String}.
		 * @param childNameString {@code Name}-{@link String}.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Textwert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String getChildContentByUriName(final int[] indices, final String childUriString, final String childNameString, final int childIndex)
			throws NullPointerException {
			return this.getChildContentBy_(indices, this.childIndex(indices, childUriString, childNameString, childIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#getChildNodeByUriName(DecodeElementNodeAdapter, int[], String, String, int) getChildNodeByUriName}{@code (..., childUriString, childNameString, childIndex)} nach einem {@link DecodeElement} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Textwert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#childIndex(int[], int, int)
		 * @see DecodeAdapter#getElementContent(int)
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param childUriString {@code URI}-{@link String}.
		 * @param childNameString {@code Name}-{@link String}.
		 * @param childIndex Beginn der linearen Suche.
		 * @return Textwert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getChildContentByUriName(final DecodeElement parentNode, final String childUriString, final String childNameString, final int childIndex)
			throws NullPointerException {
			return this.getChildContentByUriName(this.children(parentNode), childUriString, childNameString, childIndex);
		}

		/**
		 * Diese Methode gibt den Textwert des {@code childIndex}-ten {@link DecodeElement}s oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getElementContent(int)
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param childIndex Index des {@link DecodeElement}s im Index-Array.
		 * @return Textwert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		final String getChildContentBy_(final int[] indices, final int childIndex) throws NullPointerException {
			if(childIndex < 0) return "";
			return this.getElementContent(indices[childIndex] - this.childOffset);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getPrefix()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#getPrefix()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getElementPrefix(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.getElementPrefix(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getPrefix()}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeElementNodeAdapter#getPrefix()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getElementPrefix(final DecodeElement elementNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupPrefix_(this.xmlns(elementNode), this.elementLabel(elementNode.label).uri);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getLocalName()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#getLocalName()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getElementLocalName(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.getElementLocalName(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getLocalName()}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeElementNodeAdapter#getLocalName()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getElementLocalName(final DecodeElement elementNode) throws NullPointerException {
			if(this.xmlnsEnabled) return this.elementName(this.elementLabel(elementNode.label).name).string;
			return this.elementName(elementNode.label).string;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getNamespaceURI()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#getNamespaceURI()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getElementNamespaceURI(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.getElementNamespaceURI(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getNamespaceURI()}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeElementNodeAdapter#getNamespaceURI()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getElementNamespaceURI(final DecodeElement elementNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			final String value = this.uri(this.elementLabel(elementNode.label).uri).string;
			if(value.isEmpty()) return null;
			return value;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getNodeName()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#getNodeName()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getElementNodeName(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.getElementNodeName(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getNodeName()}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeElementNodeAdapter#getNodeName()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getElementNodeName(final DecodeElement elementNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return this.elementName(elementNode.label).string;
			final DecodeLabel elementLabel = this.elementLabel(elementNode.label);
			final String xmlnsName = this.lookupPrefix_(this.xmlns(elementNode), elementLabel.uri);
			final String elementName = this.elementName(elementLabel.name).string;
			if(xmlnsName == null) return elementName;
			return xmlnsName + ":" + elementName;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getTextContent()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#getTextContent()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getElementContent(final int elementNodeIndex) throws IndexOutOfBoundsException {
			final StringBuffer buffer = new StringBuffer();
			this.getElementContent(elementNodeIndex, buffer);
			return buffer.toString();
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getTextContent()}.
		 * 
		 * @param buffer {@link StringBuffer}.
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @throws NullPointerException Wenn der gegebene {@link StringBuffer} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final void getElementContent(final int elementNodeIndex, final StringBuffer buffer) throws NullPointerException, IndexOutOfBoundsException {
			final int[] childrenIndices = this.children(elementNodeIndex);
			for(final int childIndex: childrenIndices){
				final int index = childIndex - this.childOffset;
				if(index < 0){
					buffer.append(this.value(childIndex).string);
				}else{
					this.getElementContent(index, buffer);
				}
			}
		}

		/**
		 * Diese Methode implementiert {@link DecodeDocumentNodeAdapter#getElementById(String)}.
		 * 
		 * @see Attr#isId()
		 * @param elementIdIndex Index des {@code ID}-{@link DecodeValue}s.
		 * @return {@link DecodeDocumentNodeAdapter#getElementById(String)}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeElementNodeAdapter getElementById(final int elementIdIndex) throws NullPointerException {
			return this.getElementById_(this.navigationPath(null, elementIdIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeDocumentNodeAdapter#getElementById(String)}.
		 * 
		 * @see Attr#isId()
		 * @param elementIdString {@code ID}-{@link String}.
		 * @return {@link DecodeDocumentNodeAdapter#getElementById(String)}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeElementNodeAdapter getElementById(final String elementIdString) throws NullPointerException {
			return this.getElementById_(this.navigationPath(null, elementIdString));
		}

		/**
		 * Diese Methode navigiert der gegebenen {@link DecodeGroup} entsprechend zu einem {@link DecodeElementNodeAdapter} und gibt diesen oder {@code null} zurück. Bis auf den ersten Index beschreiben die Indices der {@link DecodeGroup} die {@code Child}-{@link Node}-Indices zur Navigation beginnend bei {@code this.}{@link DecodeAdapter#getOwnerDocument() getOwnerDocument}{@code ().}{@link DecodeDocumentNodeAdapter#getDocumentElement() getDocumentElement}{@code ()}.
		 * 
		 * @see DecodeDocument#navigationPathPool()
		 * @param navigationPath {@link DecodeGroup} als Navigationspfad oder {@code null}.
		 * @return {@link DecodeElementNodeAdapter} oder {@code null}.
		 */
		final DecodeElementNodeAdapter getElementById_(final DecodeGroup navigationPath) {
			if(navigationPath == null) return null;
			final int[] indices = navigationPath.indices;
			DecodeElementNodeAdapter elementNode = this.documentNodeAdapter.documentElement;
			for(int i = 1, size = indices.length; i < size; i++){
				elementNode = elementNode.getChildNode(indices[i]).asElement();
			}
			return elementNode;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getElementsByTagNameNS(String, String)} bzw. {@link DecodeDocumentNodeAdapter#getElementsByTagNameNS(String, String)}.
		 * 
		 * @see DecodeElementNodeCollector
		 * @see DecodeElementNodeCollector#MODE_CHILDREN
		 * @see DecodeElementNodeCollector#MODE_DESCENDANT
		 * @see DecodeElementNodeCollector#MODE_DESCENDANT_SELF
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param elementLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @param mode Modus der Suche.
		 * @return {@link DecodeElementNodeAdapter#getElementsByTagNameNS(String, String)} bzw. {@link DecodeDocumentNodeAdapter#getElementsByTagNameNS(String, String)}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public final DecodeElementNodeCollector getElementsByLabel(final DecodeElementNodeAdapter parentNode, final int elementLabelIndex, final int mode)
			throws NullPointerException, IllegalArgumentException {
			final DecodeElementNodeCollector collector = new DecodeElementNodeLabelCollector(this, elementLabelIndex);
			collector.collect(parentNode, mode);
			return collector;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getElementsByTagNameNS(String, String)} bzw. {@link DecodeDocumentNodeAdapter#getElementsByTagNameNS(String, String)}.
		 * 
		 * @see DecodeElementNodeCollector
		 * @see DecodeElementNodeCollector#MODE_CHILDREN
		 * @see DecodeElementNodeCollector#MODE_DESCENDANT
		 * @see DecodeElementNodeCollector#MODE_DESCENDANT_SELF
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param elementUriString {@code URI}-{@link String}.
		 * @param elementNameString {@code Name}-{@link String}.
		 * @param mode Modus der Suche.
		 * @return {@link DecodeElementNodeAdapter#getElementsByTagNameNS(String, String)} bzw. {@link DecodeDocumentNodeAdapter#getElementsByTagNameNS(String, String)}.
		 * @throws NullPointerException Wenn eine der Eingabe {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public final ElementList getElementsByUriName(final DecodeElementNodeAdapter parentNode, final String elementUriString, final String elementNameString,
			final int mode) throws NullPointerException, IllegalArgumentException {
			final DecodeElementNodeCollector collector;
			if(this.xmlnsEnabled){
				if("*".equals(elementUriString)){
					if("*".equals(elementNameString)){
						collector = new DecodeElementNodeCollector(this);
					}else{
						final DecodeValue elementName = this.elementName(elementNameString);
						if(elementName == null) return DecodeAdapter.VOID_NODE_LIST;
						collector = new DecodeElementNodeNameCollector(this, elementName.index);
					}
				}else{
					final DecodeValue elementUri = this.uri(elementUriString);
					if(elementUri == null) return DecodeAdapter.VOID_NODE_LIST;
					if("*".equals(elementNameString)){
						collector = new DecodeElementNodeUriCollector(this, elementUri.index);
					}else{
						final DecodeValue elementName = this.elementName(elementNameString);
						if(elementName == null) return DecodeAdapter.VOID_NODE_LIST;
						final DecodeLabel elementlLabel = this.elementLabel(elementUri.index, elementName.index);
						if(elementlLabel == null) return DecodeAdapter.VOID_NODE_LIST;
						collector = new DecodeElementNodeLabelCollector(this, elementlLabel.index);
					}
				}
			}else{
				if("*".equals(elementNameString)){
					collector = new DecodeElementNodeCollector(this);
				}else{
					final DecodeValue elementName = this.elementName(elementNameString);
					if(elementName == null) return DecodeAdapter.VOID_NODE_LIST;
					collector = new DecodeElementNodeLabelCollector(this, elementName.index);
				}
			}
			collector.collect(parentNode, mode);
			return collector;
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * 
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * @throws IndexOutOfBoundsException Wenn einer der gegebenen Indices ungültig ist.
		 */
		public final String getAttributePrefix(final int parentIndex, final int attributeNodeIndex) throws IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(this.elementNode(parentIndex), this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * 
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributePrefix(final int parentIndex, final DecodeAttribute attributeNode) throws NullPointerException, IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(this.elementNode(parentIndex), attributeNode);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributePrefix(final DecodeElement parentNode, final int attributeNodeIndex) throws NullPointerException, IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(parentNode, this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final String getAttributePrefix(final DecodeElement parentNode, final DecodeAttribute attributeNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(parentNode, attributeNode);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getPrefix()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		final String getAttributePrefix_(final DecodeElement parentNode, final DecodeAttribute attributeNode) throws NullPointerException {
			return this.lookupPrefix_(this.xmlns(parentNode), this.attributeLabel(attributeNode.label).uri);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getLocalName()}.
		 * 
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getLocalName()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeLocalName(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.getAttributeLocalName(this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getLocalName()}.
		 * 
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getLocalName()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 */
		public final String getAttributeLocalName(final DecodeAttribute attributeNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return this.getAttributeNodeName_(attributeNode);
			return this.attributeName(this.attributeLabel(attributeNode.label).name).string;
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNamespaceURI()}.
		 * 
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getNamespaceURI()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeNamespaceURI(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributeNamespaceURI_(this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNamespaceURI()}.
		 * 
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNamespaceURI()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 */
		public final String getAttributeNamespaceURI(final DecodeAttribute attributeNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributeNamespaceURI_(attributeNode);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNamespaceURI()}.
		 * 
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNamespaceURI()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 */
		final String getAttributeNamespaceURI_(final DecodeAttribute attributeNode) throws NullPointerException {
			final String uriString = this.uri(this.attributeLabel(attributeNode.label).uri).string;
			if(uriString.isEmpty()) return null;
			return uriString;
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * 
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * @throws IndexOutOfBoundsException Wenn einer der gegebenen Indices ungültig ist.
		 */
		public final String getAttributeNodeName(final int parentIndex, final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.getAttributeNodeName(parentIndex, this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * 
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeNodeName(final int parentIndex, final DecodeAttribute attributeNode) throws NullPointerException, IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return this.getAttributeNodeName_(attributeNode);
			return this.getAttributeNodeName_(this.elementNode(parentIndex), attributeNode);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeNodeName(final DecodeElement parentNode, final int attributeNodeIndex) throws NullPointerException,
			IndexOutOfBoundsException {
			return this.getAttributeNodeName(parentNode, this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final String getAttributeNodeName(final DecodeElement parentNode, final DecodeAttribute attributeNode) throws NullPointerException {
			if(!this.xmlnsEnabled) return this.getAttributeNodeName_(attributeNode);
			return this.getAttributeNodeName_(parentNode, attributeNode);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * 
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 */
		final String getAttributeNodeName_(final DecodeAttribute attributeNode) throws NullPointerException {
			return this.attributeName(attributeNode.label).string;
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeName()}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		final String getAttributeNodeName_(final DecodeElement parentNode, final DecodeAttribute attributeNode) throws NullPointerException {
			final DecodeLabel attributeLabel = this.attributeLabel(attributeNode.label);
			final String xmlnsName = this.lookupPrefix_(this.xmlns(parentNode), attributeLabel.uri);
			final String attributeName = this.attributeName(attributeLabel.name).string;
			if(xmlnsName == null) return attributeName;
			return xmlnsName + ":" + attributeName;
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeValue()}.
		 * 
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeValue()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeValue(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.getAttributeValue(this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodeAdapter#getNodeValue()}.
		 * 
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeAttributeNodeAdapter#getNodeValue()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeAttribute} {@code null} ist.
		 */
		public final String getAttributeValue(final DecodeAttribute attributeNode) throws NullPointerException {
			return this.value(attributeNode.value).string;
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (..., attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Wert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getAttributeValueByLabel(int[], int)
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return Wert oder {@code ""}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@code Parent}-{@link DecodeElement}s ungültig ist.
		 */
		public final String getAttributeValueByLabel(final int parentIndex, final int attributeLabelIndex) throws IndexOutOfBoundsException {
			return this.getAttributeValueByLabel(this.attributes(parentIndex), attributeLabelIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (indices, attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Wert oder {@code ""} zurück.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return Wert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String getAttributeValueByLabel(final int[] indices, final int attributeLabelIndex) throws NullPointerException {
			return this.getAttributeValueBy_(this.attributeNode(indices, attributeLabelIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (..., attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen Wert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getAttributeValueByLabel(int[], int)
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return Wert oder {@code ""}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String getAttributeValueByLabel(final DecodeElement parentNode, final int attributeLabelIndex) throws NullPointerException {
			return this.getAttributeValueByLabel(this.attributes(parentNode), attributeLabelIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (..., attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Wert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getAttributeValueByUriName(int[], String, String)
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return Wert oder {@code ""}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link String}s {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeValueByUriName(final int parentIndex, final String attributeUriString, final String attributeNameString)
			throws NullPointerException, IndexOutOfBoundsException {
			return this.getAttributeValueByUriName(this.attributes(parentIndex), attributeUriString, attributeNameString);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (indices, attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Wert oder {@code ""} zurück.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return Wert oder {@code ""}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final String getAttributeValueByUriName(final int[] indices, final String attributeUriString, final String attributeNameString)
			throws NullPointerException {
			return this.getAttributeValueBy_(this.attributeNode(indices, attributeUriString, attributeNameString));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (..., attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen Wert oder {@code ""} zurück.
		 * 
		 * @see DecodeAdapter#getAttributeValueByUriName(int[], String, String)
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return Wert oder {@code ""}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final String getAttributeValueByUriName(final DecodeElement parentNode, final String attributeUriString, final String attributeNameString)
			throws NullPointerException {
			return this.getAttributeValueByUriName(this.attributes(parentNode), attributeUriString, attributeNameString);
		}

		/**
		 * Diese Methode gibt den Wert des gegebenen implementiert {@link DecodeAttribute}s oder {@code ""} zurück.
		 * 
		 * @param attributeNode {@link DecodeAttribute} oder {@code null}.
		 * @return Wert oder {@code ""}.
		 */
		final String getAttributeValueBy_(final DecodeAttribute attributeNode) {
			if(attributeNode == null) return "";
			return this.getAttributeValue(attributeNode);
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#attributes(DecodeElement) attributes}{@code (this.}{@link DecodeAdapter#elementNode(int) elementNode}{@code (elementNodeIndex))} zurück.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return Indices der {@link DecodeElement#attributes()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		final int[] attributes(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.attributes(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#elementAttributes(int) elementAttributes}{@code (elementNode.}{@link DecodeElement#attributes attributes}{@code ).}{@link DecodeGroup#indices indices} zurück.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return Indices der {@link DecodeElement#attributes()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		final int[] attributes(final DecodeElement elementNode) throws NullPointerException {
			return this.elementAttributes(elementNode.attributes).indices;
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodesAdapter#item(int)}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeNodesAdapter#item(int)}.
		 * @throws NullPointerException Wenn der gegebene {@code Parent}-{@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeAttributeNodeAdapter getAttributeNode(final DecodeElementNodeAdapter parentNode, final int attributeNodeIndex)
			throws NullPointerException {
			return new DecodeAttributeNodeAdapter(parentNode, attributeNodeIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeAttributeNodesAdapter#item(int)}.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#children()}.
		 * @param attributeIndex Index des {@link DecodeAttributeNodeAdapter}s im Index-Array.
		 * @return {@link DecodeAttributeNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@code Parent}-{@link DecodeElementNodeAdapter} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeAttributeNodeAdapter getAttributeNode(final DecodeElementNodeAdapter parentNode, final int[] indices, final int attributeIndex)
			throws NullPointerException {
			if((attributeIndex < 0) || (attributeIndex >= indices.length)) return null;
			return this.getAttributeNode(parentNode, indices[attributeIndex]);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (..., attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen {@link DecodeAttributeNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#getAttributeNodeByLabel(DecodeElementNodeAdapter, int[], int)
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeAttributeNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeAttributeNodeAdapter getAttributeNodeByLabel(final DecodeElementNodeAdapter parentNode, final int attributeLabelIndex)
			throws NullPointerException {
			return this.getAttributeNodeByLabel(parentNode, this.attributes(parentNode.index), attributeLabelIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (indices, attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dessen {@link DecodeAttributeNodeAdapter} oder {@code null} zurück.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeAttributeNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final DecodeAttributeNodeAdapter getAttributeNodeByLabel(final DecodeElementNodeAdapter parentNode, final int[] indices,
			final int attributeLabelIndex) throws NullPointerException {
			return this.getAttributeNodeBy_(parentNode, this.attributeNode(indices, attributeLabelIndex));
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (..., attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen {@link DecodeAttributeNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#getAttributeNodeByUriName(DecodeElementNodeAdapter, int[], String, String)
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return {@link DecodeAttributeNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final DecodeAttributeNodeAdapter getAttributeNodeByUriName(final DecodeElementNodeAdapter parentNode, final String attributeUriString,
			final String attributeNameString) throws NullPointerException {
			return this.getAttributeNodeByUriName(parentNode, this.attributes(parentNode.index), attributeUriString, attributeNameString);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (indices, attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dessen {@link DecodeAttributeNodeAdapter} oder {@code null} zurück.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return {@link DecodeAttributeNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final DecodeAttributeNodeAdapter getAttributeNodeByUriName(final DecodeElementNodeAdapter parentNode, final int[] indices,
			final String attributeUriString, final String attributeNameString) throws NullPointerException {
			return this.getAttributeNodeBy_(parentNode, this.attributeNode(indices, attributeUriString, attributeNameString));
		}

		/**
		 * Diese Methode gibt den {@link DecodeAttributeNodeAdapter} des gegebenen implementiert {@link DecodeAttribute}s oder {@code null} zurück.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param attributeNode {@link DecodeAttribute} oder {@code null}.
		 * @return {@link DecodeAttributeNodeAdapter} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@code Parent}-{@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		final DecodeAttributeNodeAdapter getAttributeNodeBy_(final DecodeElementNodeAdapter parentNode, final DecodeAttribute attributeNode)
			throws NullPointerException {
			if(attributeNode == null) return null;
			return new DecodeAttributeNodeAdapter(parentNode, attributeNode.index);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (..., attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt bei dessen Existenz {@code true} zurück.
		 * 
		 * @see DecodeAdapter#hasAttributeNodeByLabel(int[], int)
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return Existenz des {@link DecodeAttribute}s.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final boolean hasAttributeNodeByLabel(final int parentIndex, final int attributeLabelIndex) throws IndexOutOfBoundsException {
			return this.hasAttributeNodeByLabel(this.attributes(parentIndex), attributeLabelIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (indices, attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt bei dessen Existenz {@code true} zurück.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return Existenz des {@link DecodeAttribute}s.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final boolean hasAttributeNodeByLabel(final int[] indices, final int attributeLabelIndex) throws NullPointerException {
			return this.attributeNode(indices, attributeLabelIndex) != null;
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], int) attributeNode}{@code (..., attributeLabelIndex)} nach einem {@link DecodeAttribute} mit den gegebenen Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt bei dessen Existenz {@code true} zurück.
		 * 
		 * @see DecodeAdapter#hasAttributeNodeByLabel(int[], int)
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeLabelIndex Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return Existenz des {@link DecodeAttribute}s.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final boolean hasAttributeNodeByLabel(final DecodeElement parentNode, final int attributeLabelIndex) throws NullPointerException {
			return this.hasAttributeNodeByLabel(this.attributes(parentNode), attributeLabelIndex);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (..., attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt bei dessen Existenz {@code true} zurück.
		 * 
		 * @param parentIndex Index des {@code Parent}-{@link DecodeElement}s.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return Existenz des {@link DecodeAttribute}s.
		 * @throws NullPointerException Wenn einer der gegebenen {@link String}s {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final boolean hasAttributeNodeByUriName(final int parentIndex, final String attributeUriString, final String attributeNameString)
			throws NullPointerException, IndexOutOfBoundsException {
			return this.hasAttributeNodeByUriName(this.attributes(parentIndex), attributeUriString, attributeNameString);
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (indices, attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt bei dessen Existenz {@code true} zurück.
		 * 
		 * @param indices Index-Array der {@link DecodeElement#attributes()}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return Existenz des {@link DecodeAttribute}s.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final boolean hasAttributeNodeByUriName(final int[] indices, final String attributeUriString, final String attributeNameString)
			throws NullPointerException {
			return this.attributeNode(indices, attributeUriString, attributeNameString) != null;
		}

		/**
		 * Diese Methode sucht via {@code this.}{@link DecodeAdapter#attributeNode(int[], String, String) attributeNode}{@code (..., attributeUriString, attributeNameString)} nach einem {@link DecodeAttribute} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt bei dessen Existenz {@code true} zurück.
		 * 
		 * @param parentNode {@code Parent}-{@link DecodeElement}.
		 * @param attributeUriString {@code URI}-{@link String}.
		 * @param attributeNameString {@code Name}-{@link String}.
		 * @return Existenz des {@link DecodeAttribute}s.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public final boolean hasAttributeNodeByUriName(final DecodeElement parentNode, final String attributeUriString, final String attributeNameString)
			throws NullPointerException {
			return this.hasAttributeNodeByUriName(this.attributes(parentNode), attributeUriString, attributeNameString);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#hasAttributes()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link DecodeElementNodeAdapter#hasAttributes()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final boolean hasAttributeNodes(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.attributes(elementNodeIndex).length != 0;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#getAttributes()}.
		 * 
		 * @param elementNode {@link DecodeElementNodeAdapter}.
		 * @return {@link DecodeElementNodeAdapter#getAttributes()}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public final DecodeAttributeNodesAdapter getAttributeNodes(final DecodeElementNodeAdapter elementNode) throws NullPointerException {
			return new DecodeAttributeNodesAdapter(elementNode, this.attributes(elementNode.index));
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#xmlns(DecodeElement) xmlns}{@code (this.}{@link DecodeAdapter#elementNode(int) elementNode}{@code (elementNodeIndex))} zurück.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return Indices der {@link DecodeElement#xmlns()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		final int[] xmlns(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.xmlns(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode gibt das Ergebnis von {@code this.}{@link DecodeAdapter#elementXmlns(int) elementXmlns}{@code (elementNode.}{@link DecodeElement#xmlns xmlns}{@code ).}{@link DecodeGroup#indices indices} zurück.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return Indices der {@link DecodeElement#xmlns()}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		final int[] xmlns(final DecodeElement elementNode) throws NullPointerException {
			return this.elementXmlns(elementNode.xmlns).indices;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @param xmlnsUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final String lookupPrefix(final int elementNodeIndex, final int xmlnsUriIndex) throws IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupPrefix_(this.xmlns(elementNodeIndex), xmlnsUriIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @param xmlnsUriValue {@code URI}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final String lookupPrefix(final int elementNodeIndex, final String xmlnsUriValue) throws IndexOutOfBoundsException {
			if(!this.xmlnsEnabled || (xmlnsUriValue == null) || xmlnsUriValue.isEmpty()) return null;
			return this.lookupPrefix_(this.xmlns(elementNodeIndex), xmlnsUriValue);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param indices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String lookupPrefix(final int[] indices, final int xmlnsUriIndex) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupPrefix_(indices, xmlnsUriIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param indices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsUriString {@code URI}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String lookupPrefix(final int[] indices, final String xmlnsUriString) throws NullPointerException {
			if(!this.xmlnsEnabled || (xmlnsUriString == null) || xmlnsUriString.isEmpty()) return null;
			return this.lookupPrefix_(indices, xmlnsUriString);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @param xmlnsUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String lookupPrefix(final DecodeElement elementNode, final int xmlnsUriIndex) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupPrefix_(this.xmlns(elementNode), xmlnsUriIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @param xmlnsUriValue {@code URI}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String lookupPrefix(final DecodeElement elementNode, final String xmlnsUriValue) throws NullPointerException {
			if(!this.xmlnsEnabled || (xmlnsUriValue == null) || xmlnsUriValue.isEmpty()) return null;
			return this.lookupPrefix_(this.xmlns(elementNode), xmlnsUriValue);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param indices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		final String lookupPrefix_(final int[] indices, final int xmlnsUriIndex) throws NullPointerException {
			if(indices.length == 0) return null;
			return this.lookupPrefix_(this.documentNode.xmlnsLabelPool.find(indices, new XmlnsUriIndexComparable(xmlnsUriIndex)));
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * 
		 * @param indices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsUriString {@code URI}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupPrefix(String)}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		final String lookupPrefix_(final int[] indices, final String xmlnsUriString) throws NullPointerException {
			if(indices.length == 0) return null;
			return this.lookupPrefix_(this.documentNode.xmlnsLabelPool.find(indices, new XmlnsUriStringComparable(this, xmlnsUriString)));
		}

		/**
		 * Diese Methode gibt den nicht leeren {@link String} des {@link DecodeValue}s zum {@link DecodeLabel#name()} oder {@code null} zurück.
		 * 
		 * @param xmlnsLabel {@code Xmlns}-{@link DecodeLabel} oder {@code null}.
		 * @return licht leerer {@link String} oder null.
		 */
		final String lookupPrefix_(final DecodeLabel xmlnsLabel) {
			if(xmlnsLabel == null) return null;
			final String value = this.xmlnsName(xmlnsLabel.name).string;
			if(value.isEmpty()) return null;
			return value;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @param xmlnsNameIndex Index des {@code Prefix}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final String lookupNamespaceURI(final int elementNodeIndex, final int xmlnsNameIndex) throws IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupNamespaceURI_(this.xmlns(elementNodeIndex), xmlnsNameIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @param xmlnsNameString {@code Prefix}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index des {@link DecodeElement}s ungültig ist.
		 */
		public final String lookupNamespaceURI(final int elementNodeIndex, final String xmlnsNameString) throws IndexOutOfBoundsException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupNamespaceURI_(this.xmlns(elementNodeIndex), xmlnsNameString);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param xmlnsLabelIndices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsNameIndex Index des {@code Prefix}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String lookupNamespaceURI(final int[] xmlnsLabelIndices, final int xmlnsNameIndex) throws NullPointerException {
			if(!this.xmlnsEnabled || (xmlnsLabelIndices.length == 0)) return null;
			return this.lookupNamespaceURI_(xmlnsLabelIndices, xmlnsNameIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param xmlnsLabelIndices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsNameString {@code Prefix}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final String lookupNamespaceURI(final int[] xmlnsLabelIndices, final String xmlnsNameString) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupNamespaceURI_(xmlnsLabelIndices, xmlnsNameString);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @param xmlnsNameIndex Index des {@code Prefix}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String lookupNamespaceURI(final DecodeElement elementNode, final int xmlnsNameIndex) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupNamespaceURI_(this.xmlns(elementNode), xmlnsNameIndex);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @param xmlnsNameValue {@code Prefix}-{@link String}.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws NullPointerException Wenn das gegebene {@link DecodeElement} {@code null} ist.
		 */
		public final String lookupNamespaceURI(final DecodeElement elementNode, final String xmlnsNameValue) throws NullPointerException {
			if(!this.xmlnsEnabled) return null;
			return this.lookupNamespaceURI_(this.xmlns(elementNode), xmlnsNameValue);
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param xmlnsIndices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsNameIndex Index des {@code Prefix}-{@link DecodeValue}s.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		final String lookupNamespaceURI_(final int[] xmlnsIndices, final int xmlnsNameIndex) throws NullPointerException {
			for(final int xmlnsLabelIndex: xmlnsIndices){
				final DecodeLabel xmlnsLabel = this.xmlnsLabel(xmlnsLabelIndex);
				if(xmlnsNameIndex == xmlnsLabel.name) return this.uri(xmlnsLabel.uri).string;
			}
			return null;
		}

		/**
		 * Diese Methode implementiert {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * 
		 * @param xmlnsIndices Indices der {Xmlns}-{@link DecodeLabel}s.
		 * @param xmlnsNameString {@code Prefix}.
		 * @return {@link DecodeElementNodeAdapter#lookupNamespaceURI(String)}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		final String lookupNamespaceURI_(final int[] xmlnsIndices, final String xmlnsNameString) throws NullPointerException {
			if(xmlnsNameString == null) return this.lookupNamespaceURI_(xmlnsIndices, XMLConstants.DEFAULT_NS_PREFIX);
			for(final int xmlnsLabelIndex: xmlnsIndices){
				final DecodeLabel xmlnsLabel = this.xmlnsLabel(xmlnsLabelIndex);
				if(xmlnsNameString.equals(this.xmlnsName(xmlnsLabel.name).string)) return this.uri(xmlnsLabel.uri).string;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeAdapter", this.documentNode);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Node}, dessen Methoden keine Modifikation zulassen.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeNodeAdapter implements Node {

		/**
		 * Diese Methode gibt den {@link DecodeAdapter} zurück.
		 * 
		 * @return {@link DecodeAdapter}.
		 */
		public abstract DecodeAdapter adapter();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefix() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamespaceURI() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getLocalName() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getBaseURI() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getFirstChild() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getLastChild() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getNextSibling() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getPreviousSibling() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributes() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NamedNodeMap getAttributes() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasChildNodes() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildList getChildNodes() {
			return DecodeAdapter.VOID_NODE_LIST;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setPrefix(final String prefix) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setNodeValue(final String nodeValue) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setTextContent(final String textContent) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupPrefix(final String uri) {
			return this.getParentNode().lookupPrefix(uri);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupNamespaceURI(final String prefix) {
			return this.getParentNode().lookupNamespaceURI(prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDefaultNamespace(final String uri) {
			return this.getParentNode().isDefaultNamespace(uri);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getFeature(final String feature, final String version) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSupported(final String feature, final String version) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object getUserData(final String key) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Document getOwnerDocument() {
			return this.adapter().getOwnerDocument();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node cloneNode(final boolean deep) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node removeChild(final Node node) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node appendChild(final Node node) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void normalize() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short compareDocumentPosition(final Node node) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Text} als {@link DecodeChildNodeAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeTextNodeAdapter extends DecodeChildNodeAdapter implements Text {

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeValue}s und {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeValue}s.
		 * @param child {@code Child-Index}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeNodeAdapter} {@code null} ist.
		 */
		public DecodeTextNodeAdapter(final DecodeNodeAdapter parent, final int index, final int child) throws NullPointerException {
			super(parent, index, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeTextNodeAdapter asText() {
			return this;
		}

		/**
		 * Diese Methode gibt den Index des {@link DecodeValue}s im {@link DecodeDocument#valuePool()} zurück.
		 * 
		 * @return Index des {@link DecodeValue}s.
		 */
		public int valueIndex() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short getNodeType() {
			return Node.TEXT_NODE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeName() {
			return "#text";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeValue() throws DOMException {
			return this.adapter().getTextNodeValue(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getData() throws DOMException {
			return this.getNodeValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setData(final String data) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return this.getData().length();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTextContent() throws DOMException {
			return this.getNodeValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getWholeText() {
			return this.getNodeValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String substringData(final int offset, final int count) throws DOMException {
			try{
				return this.getNodeValue().substring(offset, offset + count);
			}catch(final IndexOutOfBoundsException e){
				throw new DOMException(DOMException.INDEX_SIZE_ERR, e.getMessage());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Text splitText(final int offset) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void insertData(final int offset, final String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void deleteData(final int offset, final int count) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void appendData(final String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void replaceData(final int offset, final int count, final String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Text replaceWholeText(final String content) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSameNode(final Node object) {
			return this.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEqualNode(final Node object) {
			if(object == this) return true;
			if(!(object instanceof DecodeTextNodeAdapter)) return false;
			final DecodeTextNodeAdapter data = (DecodeTextNodeAdapter)object;
			return this.index == data.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isElementContentWhitespace() {
			final String nodeValue = this.getNodeValue();
			for(int i = 0, size = nodeValue.length(); i < size; i++){
				final char value = nodeValue.charAt(i);
				if((value > 0x20) || (value < 0x09)) return false;
				if((value != 0x0A) && (value != 0x0D)) return false;
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DecodeTextNodeAdapter)) return false;
			final DecodeTextNodeAdapter data = (DecodeTextNodeAdapter)object;
			return (this.index == data.index) && (this.child == data.child) && this.parent.equals(data.parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.getNodeValue();
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeNodeAdapter} mit {@code Parent}-{@link DecodeNodeAdapter} und {@code Child-Index} als Basis von {@link DecodeTextNodeAdapter} und {@link DecodeElementNodeAdapter}.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeChildNodeAdapter extends DecodeNodeAdapter {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeNodeAdapter}.
		 */
		protected final DecodeNodeAdapter parent;

		/**
		 * Dieses Feld speichert dne Index des {@link DecodeItem}s.
		 */
		protected final int index;

		/**
		 * Dieses Feld speichert den {@code Child-Index}.
		 * 
		 * @see Node#getChildNodes()
		 * @see NodeList#item(int)
		 */
		protected final int child;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeItem}s und {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeItem}s.
		 * @param child {@code Child-Index}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeNodeAdapter} {@code null} ist.
		 */
		public DecodeChildNodeAdapter(final DecodeNodeAdapter parent, final int index, final int child) throws NullPointerException {
			if(parent == null) throw new NullPointerException();
			this.parent = parent;
			this.index = index;
			this.child = child;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAdapter adapter() {
			return this.parent.adapter();
		}

		/**
		 * Diese Methode gibt den Index dieses {@link DecodeChildNodeAdapter}s in den {@link Node#getChildNodes()} des {@link Node#getParentNode()}s zurück.
		 * 
		 * @return Index dieses {@link DecodeChildNodeAdapter}s im {@link Node#getParentNode()}.
		 */
		public int childIndex() {
			return this.child;
		}

		/**
		 * Diese Methode gibt den {@code Parent}-{@link DecodeNodeAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeNodeAdapter}.
		 */
		public DecodeNodeAdapter parentAdapter() {
			return this.parent;
		}

		/**
		 * Diese Methode gibt nur dann {@code this} zurück, wenn dieses Objekt ein {@link DecodeTextNodeAdapter} ist.
		 * 
		 * @return {@code this} oder {@code null}.
		 */
		public DecodeTextNodeAdapter asText() {
			return null;
		}

		/**
		 * Diese Methode gibt nur dann {@code this} zurück, wenn dieses Objekt ein {@link DecodeElementNodeAdapter} ist.
		 * 
		 * @return {@code this} oder {@code null}.
		 */
		public DecodeElementNodeAdapter asElement() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getParentNode() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodeAdapter getPreviousSibling() {
			final DecodeNodeAdapter parent = this.parent;
			if(parent == null) return null;
			final ChildList children = parent.getChildNodes();
			if(children == null) return null;
			return children.item(this.child - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodeAdapter getNextSibling() {
			final DecodeNodeAdapter parent = this.parent;
			if(parent == null) return null;
			final ChildList children = parent.getChildNodes();
			if(children == null) return null;
			return children.item(this.child + 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.index ^ this.child;
		}

	}

	/**
	 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getChildNodes()}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeChildNodesAdapter implements ChildList, Iterable<DecodeChildNodeAdapter> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementNodeAdapter}.
		 */
		protected final DecodeElementNodeAdapter parent;

		/**
		 * Dieses Feld speichert die Indices der {@link DecodeValue}s und {@link DecodeElement}s.
		 * 
		 * @see DecodeAdapter#childOffset
		 */
		protected final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeElementNodeAdapter} und Indices der {@link DecodeValue}s und {@link DecodeElement}s.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Indices der {@link DecodeValue}s und {@link DecodeElement}s.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public DecodeChildNodesAdapter(final DecodeElementNodeAdapter parent, final int[] indices) throws NullPointerException {
			if((parent == null) || (indices == null)) throw new NullPointerException();
			this.parent = parent;
			this.indices = indices;
		}

		/**
		 * Diese Methode gibt die Indices der {@link DecodeValue}s bzw. {@link DecodeElement}s zurück.
		 * 
		 * @see DecodeElement#children()
		 * @return Indices der {@link DecodeValue}s bzw. {@link DecodeElement}s.
		 */
		public int[] getChildIndices() {
			return this.indices.clone();
		}

		/**
		 * Diese Methode gibt den {@code Parent}-{@link DecodeElementNodeAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeElementNodeAdapter}.
		 */
		public DecodeElementNodeAdapter getParentAdapter() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodeAdapter item(final int index) {
			return this.parent.adapter().getChildNode(this.parent, this.indices, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return this.indices.length;
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@link Element} mit dem gegebenen {@code Name} oder {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param name {@code Name}.
		 * @param child Beginn der linearen Suche.
		 * @return erstes gefundenes {@link Element} oder {@code null}.
		 */
		public DecodeElementNodeAdapter getNamedItem(final String name, final int child) {
			return this.getNamedItemNS(XMLConstants.NULL_NS_URI, name, child);
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@link Element} mit der gegebenen {@code URI} und dem gegebenen {@code Name} oder {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param child Beginn der linearen Suche.
		 * @return erstes gefundenes {@link Element} oder {@code null}.
		 */
		public DecodeElementNodeAdapter getNamedItemNS(final String uri, final String name, final int child) {
			return this.parent.adapter().getChildNodeByUriName(this.parent, this.indices, uri, name, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<DecodeChildNodeAdapter> iterator() {
			return new Iterator<DecodeChildNodeAdapter>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < DecodeChildNodesAdapter.this.indices.length;
				}

				@Override
				public DecodeChildNodeAdapter next() {
					return DecodeChildNodesAdapter.this.item(this.index++);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(this);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Element} als {@link DecodeChildNodeAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementNodeAdapter extends DecodeChildNodeAdapter implements Element {

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeElement}s und {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeElement}s.
		 * @param child {@code Child-Index}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeNodeAdapter} {@code null} ist.
		 */
		public DecodeElementNodeAdapter(final DecodeNodeAdapter parent, final int index, final int child) throws NullPointerException {
			super(parent, index, child);
		}

		/**
		 * Diese Methode gibt den Index des {@link DecodeElement}s im {@link DecodeDocument#elementNodePool()} zurück.
		 * 
		 * @return Index des {@link DecodeElement}s.
		 */
		public int elementIndex() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter asElement() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefix() {
			return this.adapter().getElementPrefix(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamespaceURI() {
			return this.adapter().getElementNamespaceURI(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short getNodeType() {
			return Node.ELEMENT_NODE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeName() {
			return this.adapter().getElementNodeName(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeValue() throws DOMException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTagName() {
			return this.getNodeName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getLocalName() {
			return this.adapter().getElementLocalName(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodeAdapter getFirstChild() {
			return this.adapter().getChildNodeFirst(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodeAdapter getLastChild() {
			return this.adapter().getChildNodeLast(this);
		}

		/**
		 * Diese Methode gibt den {@code index}-te {@link DecodeChildNodeAdapter} oder {@code null} zurück.
		 * 
		 * @see DecodeChildNodesAdapter#item(int)
		 * @param index Index.
		 * @return {@code index}-ter {@link DecodeChildNodeAdapter} oder {@code null}.
		 */
		public DecodeChildNodeAdapter getChildNode(final int index) {
			return this.adapter().getChildNode(this, index);
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@code Child}-{@link Element} mit dem gegebenen {@code Name} oder {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param name {@code Name}.
		 * @param child {@code Child}-Index als Beginn der linearen Suche.
		 * @return erstes gefundenes {@code Child}-{@link Element} oder {@code null}.
		 */
		public DecodeElementNodeAdapter getChildNode(final String name, final int child) {
			return this.getChildNodeNS(XMLConstants.NULL_NS_URI, name, child);
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@code Child}-{@link Element} mit der gegebenen {@code URI} und dem gegebenen {@code Name} oder {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param child {@code Child}-Index als Beginn der linearen Suche.
		 * @return erstes gefundenes {@code Child}-{@link Element} oder {@code null}.
		 */
		public DecodeElementNodeAdapter getChildNodeNS(final String uri, final String name, final int child) {
			return this.adapter().getChildNodeByUriName(this, uri, name, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasChildNodes() {
			return this.adapter().hasChildNodes(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildNodesAdapter getChildNodes() {
			return this.adapter().getChildNodes(this);
		}

		/**
		 * Diese Methode gibt den Textwert des ersten gefundenen {@code Child}-{@link Element}s mit dem gegebenen {@code Name} oder {@code ""} zurück. Die lineare Suche nach dem {@link Element} beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param name {@code Name}.
		 * @param child {@code Child}-Index als Beginn der linearen Suche.
		 * @return Textwert des ersten gefundenen {@code Child}-{@link Element}s oder {@code ""}.
		 */
		public String getChildContent(final String name, final int child) {
			return this.getChildContentNS(XMLConstants.NULL_NS_URI, name, child);
		}

		/**
		 * Diese Methode gibt den Textwert des ersten gefundenen {@code Child}-{@link Element}s mit der gegebenen {@code URI} und dem gegebenen {@code Name} oder {@code ""} zurück. Die lineare Suche nach dem {@link Element} beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param child {@code Child}-Index als Beginn der linearen Suche.
		 * @return Textwert des ersten gefundenen {@code Child}-{@link Element}s oder {@code ""}.
		 */
		public String getChildContentNS(final String uri, final String name, final int child) {
			return this.adapter().getChildContentByUriName(this.index, uri, name, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeNodesAdapter getAttributes() {
			return this.adapter().getAttributeNodes(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributes() {
			return this.adapter().hasAttributeNodes(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAttribute(final String name) {
			return this.getAttributeNS(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttribute(final String name) {
			return this.hasAttributeNS(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setAttribute(final String name, final String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAttributeNS(final String uri, final String name) throws DOMException {
			return this.adapter().getAttributeValueByUriName(this.index, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributeNS(final String uri, final String name) throws DOMException {
			return this.adapter().hasAttributeNodeByUriName(this.index, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeNodeAdapter getAttributeNode(final String name) {
			return this.getAttributeNodeNS(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr setAttributeNode(final Attr newAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeNodeAdapter getAttributeNodeNS(final String uri, final String name) throws DOMException {
			return this.adapter().getAttributeNodeByUriName(this, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setIdAttribute(final String name, final boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setIdAttributeNode(final Attr idAttr, final boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTextContent() throws DOMException {
			return this.adapter().getElementContent(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeInfo getSchemaTypeInfo() {
			return DecodeAdapter.VOID_TYPE_INFO;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAttribute(final String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void removeAttributeNS(final String namespaceURI, final String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupPrefix(final String uri) {
			return this.adapter().lookupPrefix(this.index, uri);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupNamespaceURI(final String prefix) {
			return this.adapter().lookupNamespaceURI(this.index, prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDefaultNamespace(final String uri) {
			return uri.equals(this.lookupNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementList getElementsByTagName(final String name) {
			return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementList getElementsByTagNameNS(final String uri, final String name) throws DOMException {
			return this.adapter().getElementsByUriName(this, uri, name, DecodeElementNodeCollector.MODE_DESCENDANT);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSameNode(final Node object) {
			return this.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEqualNode(final Node object) {
			if(object == this) return true;
			if(!(object instanceof DecodeElementNodeAdapter)) return false;
			final DecodeElementNodeAdapter data = (DecodeElementNodeAdapter)object;
			return (this.index == data.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.index ^ this.child;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DecodeElementNodeAdapter)) return false;
			final DecodeElementNodeAdapter data = (DecodeElementNodeAdapter)object;
			return (this.index == data.index) && (this.child == data.child) && this.parent.equals(data.parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "<" + this.getNodeName() + " " + this.getAttributes() + ">";
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Attr} als {@link DecodeNodeAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAttributeNodeAdapter extends DecodeNodeAdapter implements Attr {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementNodeAdapter}.
		 */
		protected final DecodeElementNodeAdapter parent;

		/**
		 * Dieses Feld speichert den {@link DecodeAttribute}-{@code Index}.
		 */
		protected final int index;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}- {@link DecodeElementNodeAdapter} und {@link DecodeAttribute}- {@code Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param index {@link DecodeAttribute}-{@code Index}.
		 * @throws NullPointerException Wenn der gegebene {@code Parent}-{@link DecodeElementNodeAdapter} {@code null} ist.
		 */
		public DecodeAttributeNodeAdapter(final DecodeElementNodeAdapter parent, final int index) throws NullPointerException {
			if(parent == null) throw new NullPointerException();
			this.parent = parent;
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAdapter adapter() {
			return this.parent.adapter();
		}

		/**
		 * Diese Methode gibt den {@code Parent}-{@link DecodeElementNodeAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeElementNodeAdapter}.
		 */
		public DecodeElementNodeAdapter getParentAdapter() {
			return this.parent;
		}

		/**
		 * Diese Methode gibt den Index des {@link DecodeAttribute}s zurück.
		 * 
		 * @return Index des {@link DecodeAttribute}s.
		 */
		public int getAttributeIndex() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefix() {
			return this.adapter().getAttributePrefix(this.parent.index, this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamespaceURI() {
			return this.adapter().getAttributeNamespaceURI(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short getNodeType() {
			return Node.ATTRIBUTE_NODE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeName() {
			return this.adapter().getAttributeNodeName(this.parent.index, this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeValue() throws DOMException {
			return this.adapter().getAttributeValue(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getLocalName() {
			return this.adapter().getAttributeLocalName(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getParentNode() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return this.getNodeName();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getValue() {
			return this.getNodeValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setValue(final String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean getSpecified() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTextContent() throws DOMException {
			return this.getNodeValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element getOwnerElement() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TypeInfo getSchemaTypeInfo() {
			return DecodeAdapter.VOID_TYPE_INFO;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isId() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSameNode(final Node object) {
			return this.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEqualNode(final Node object) {
			if(object == this) return true;
			if(!(object instanceof DecodeAttributeNodeAdapter)) return false;
			final DecodeAttributeNodeAdapter data = (DecodeAttributeNodeAdapter)object;
			return (this.index == data.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DecodeAttributeNodeAdapter)) return false;
			final DecodeAttributeNodeAdapter data = (DecodeAttributeNodeAdapter)object;
			return (this.index == data.index) && this.parent.equals(data.parent);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.getNodeName() + "=" + Objects.toString(this.getNodeValue());
		}

	}

	/**
	 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getAttributes()}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAttributeNodesAdapter implements NamedNodeMap, Iterable<DecodeAttributeNodeAdapter> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementNodeAdapter}.
		 */
		protected final DecodeElementNodeAdapter parent;

		/**
		 * Dieses Feld speichert die Indices der {@link DecodeAttribute}s.
		 */
		protected final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeElementNodeAdapter} und Indices der {@link DecodeAttribute}s.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementNodeAdapter}.
		 * @param indices Indices der {@link DecodeAttribute}s.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public DecodeAttributeNodesAdapter(final DecodeElementNodeAdapter parent, final int[] indices) throws NullPointerException {
			if((parent == null) || (indices == null)) throw new NullPointerException();
			this.parent = parent;
			this.indices = indices;
		}

		/**
		 * Diese Methode gibt die Indices der {@link DecodeAttribute}s zurück.
		 * 
		 * @return Indices der {@link DecodeAttribute}s.
		 */
		public int[] getAttributeIndices() {
			return this.indices.clone();
		}

		/**
		 * Diese Methode gibt den {@code Parent}-{@link DecodeElementNodeAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeElementNodeAdapter}.
		 */
		public DecodeElementNodeAdapter getParentAdapter() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeNodeAdapter item(final int index) {
			return this.parent.adapter().getAttributeNode(this.parent, this.indices, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return this.indices.length;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeNodeAdapter getNamedItem(final String name) {
			return this.getNamedItemNS(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node setNamedItem(final Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeNodeAdapter getNamedItemNS(final String uri, final String name) throws DOMException {
			return this.parent.adapter().getAttributeNodeByUriName(this.parent, this.indices, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node setNamedItemNS(final Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node removeNamedItem(final String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node removeNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<DecodeAttributeNodeAdapter> iterator() {
			return new Iterator<DecodeAttributeNodeAdapter>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < DecodeAttributeNodesAdapter.this.indices.length;
				}

				@Override
				public DecodeAttributeNodeAdapter next() {
					return DecodeAttributeNodesAdapter.this.item(this.index++);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(this);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Document} als {@link DecodeNodeAdapter}, der auf den Daten eines {@link DecodeAdapter}s arbeitet.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeDocumentNodeAdapter extends DecodeNodeAdapter implements Document, ElementList {

		/**
		 * Dieses Feld speichert die leere {@link DOMConfiguration}.
		 */
		public static final DOMConfiguration VOID_DOM_CONFIGURATION = new DOMConfiguration() {

			@Override
			public void setParameter(final String name, final Object value) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

			@Override
			public DOMStringList getParameterNames() {
				return DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_LIST;
			}

			@Override
			public Object getParameter(final String name) throws DOMException {
				if(DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.contains(name)) return Boolean.TRUE;
				if(DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.contains(name)) return Boolean.FALSE;
				throw new DOMException(DOMException.NOT_FOUND_ERR, null);
			}

			@Override
			public boolean canSetParameter(final String name, final Object value) {
				return false;
			}

		};

		/**
		 * Dieses Feld speichert die {@link DOMStringList} der leeren {@link DOMConfiguration}.
		 * 
		 * @see DOMConfiguration#getParameterNames()
		 */
		public static final DOMStringList VOID_DOM_CONFIGURATION_PARAMETER_LIST = new DOMStringList() {

			@Override
			public String item(final int index) {
				final int offset = DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.size();
				if(index < offset) return DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.get(index);
				return DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.get(index - offset);
			}

			@Override
			public int getLength() {
				return DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.size()
					+ DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.size();
			}

			@Override
			public boolean contains(final String str) {
				return DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.contains(str)
					|| DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.contains(str);
			}

		};

		/**
		 * Dieses Feld speichert die {@code true-Parameter} der leeren {@link DOMConfiguration}.
		 * 
		 * @see DOMConfiguration#getParameter(String)
		 */
		public static final List<String> VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST = Collections.unmodifiableList(Arrays.asList("comments",
			"datatype-normalization", "well-formed", "namespaces", "namespace-declarations", "element-content-whitespace"));

		/**
		 * Dieses Feld speichert die {@code false-Parameter} der leeren {@link DOMConfiguration}.
		 * 
		 * @see DOMConfiguration#getParameter(String)
		 */
		public static final List<String> VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST = Collections.unmodifiableList(Arrays.asList("cdata-sections", "entities",
			"split-cdata-sections", "validate", "infoset", "normalize-characters", "canonical-form", "validate-if-schema", "check-character-normalization"));

		/**
		 * Dieses Feld speichert die leere {@link DOMImplementation}.
		 */
		public static final DOMImplementation VOID_DOM_IMPLEMENTATION = new DOMImplementation() {

			@Override
			public boolean hasFeature(final String feature, final String version) {
				return false;
			}

			@Override
			public Object getFeature(final String feature, final String version) {
				return null;
			}

			@Override
			public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

			@Override
			public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

		};

		/**
		 * Dieses Feld speichert den {@link DecodeAdapter}.
		 */
		protected final DecodeAdapter adapter;

		/**
		 * Dieses Feld speichert den {@link DecodeElementNodeAdapter} für {@link #getDocumentElement()}.
		 */
		protected final DecodeElementNodeAdapter documentElement;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeAdapter}.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 * @throws NullPointerException Wenn der gegebene {@link DecodeAdapter} {@code null} ist.
		 */
		public DecodeDocumentNodeAdapter(final DecodeAdapter adapter) throws NullPointerException {
			this.adapter = adapter;
			this.documentElement = new DecodeElementNodeAdapter(this, adapter.documentNode.documentElement, 0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAdapter adapter() {
			return this.adapter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public short getNodeType() {
			return Node.DOCUMENT_NODE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeName() {
			return "#document";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeValue() throws DOMException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getParentNode() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter item(final int index) {
			if(index != 0) return null;
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter getFirstChild() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter getLastChild() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeDocumentNodeAdapter getChildNodes() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasChildNodes() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NamedNodeMap getAttributes() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributes() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTextContent() throws DOMException {
			return this.documentElement.getTextContent();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentType getDoctype() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DOMImplementation getImplementation() {
			return DecodeDocumentNodeAdapter.VOID_DOM_IMPLEMENTATION;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupPrefix(final String namespaceURI) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupNamespaceURI(final String prefix) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDefaultNamespace(final String uri) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element getElementById(final String elementId) {
			return this.adapter().getElementById(elementId);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementList getElementsByTagName(final String name) {
			return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementList getElementsByTagNameNS(final String uri, final String name) {
			return this.adapter().getElementsByUriName(this.documentElement, uri, name, DecodeElementNodeCollector.MODE_DESCENDANT_SELF);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter getDocumentElement() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element createElement(final String tagName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentFragment createDocumentFragment() {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Text createTextNode(final String data) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Comment createComment(final String data) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CDATASection createCDATASection(final String data) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr createAttribute(final String name) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EntityReference createEntityReference(final String name) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getInputEncoding() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getXmlEncoding() {
			return "UTF-8";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean getXmlStandalone() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getXmlVersion() {
			return "1.0";
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXmlVersion(final String xmlVersion) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean getStrictErrorChecking() {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setStrictErrorChecking(final boolean strictErrorChecking) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDocumentURI() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setDocumentURI(final String documentURI) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node adoptNode(final Node source) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DOMConfiguration getDomConfig() {
			return DecodeDocumentNodeAdapter.VOID_DOM_CONFIGURATION;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void normalizeDocument() {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSameNode(final Node object) {
			return this.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEqualNode(final Node object) {
			return this.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Document getOwnerDocument() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.adapter.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DecodeDocumentNodeAdapter)) return false;
			final DecodeDocumentNodeAdapter data = (DecodeDocumentNodeAdapter)object;
			return this.adapter.equals(data.adapter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return this.documentElement.toString();
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Suche von {@link Element}s in den {@link Element#getChildNodes()} eines gegebenen {@link DecodeElementNodeAdapter}. Die Ergebnisse der Suche können dann als {@link NodeList} weiterverwendet werden.
	 * 
	 * @see Element#getElementsByTagName(String)
	 * @see Element#getElementsByTagNameNS(String, String)
	 * @see Document#getElementsByTagName(String)
	 * @see Document#getElementsByTagNameNS(String, String)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementNodeCollector implements Filter<DecodeElement>, ElementList {

		/**
		 * Dieses Feld speichert den Modus für die Suche auf den {@link DecodeElement#children()} eines gegebenen {@link DecodeElementNodeAdapter}s.
		 */
		public static final int MODE_CHILDREN = 0;

		/**
		 * Dieses Feld speichert den Modus für die rekursive Suche auf den {@link DecodeElement#children()} eines gegebenen {@link DecodeElementNodeAdapter}s.
		 */
		public static final int MODE_DESCENDANT = 1;

		/**
		 * Dieses Feld speichert den Modus für die rekursive Suche auf den {@link DecodeElement#children()} eines gegebenen {@link DecodeElementNodeAdapter}s sowie dem {@link DecodeElementNodeAdapter}selbst.
		 */
		public static final int MODE_DESCENDANT_SELF = 2;

		/**
		 * Dieses Feld speichert den {@link DecodeAdapter}.
		 */
		protected final DecodeAdapter adapter;

		/**
		 * Dieses Feld speichert {@link DecodeAdapter#childOffset()}.
		 */
		protected final int childOffset;

		/**
		 * Dieses Feld speichert die Ergebnisse der Suche.
		 */
		protected final List<DecodeElementNodeAdapter> elements;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeElementNodeCollector} mit den Daten des {@link DecodeAdapter}s.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 */
		public DecodeElementNodeCollector(final DecodeAdapter adapter) {
			this.adapter = adapter;
			this.childOffset = adapter.childOffset;
			this.elements = new ArrayList<DecodeElementNodeAdapter>();
		}

		/**
		 * Diese Methode Sucht in den {@link DecodeGroup} des gegebenen {@link DecodeElement}s nach {@link DecodeElement}s, die von der Methode {@link #accept(DecodeElement)} akzeptiert werden und speichert die {@link DecodeElementNodeAdapter} der Treffer in die {@link List} {@link #elements}.
		 * 
		 * @param parent {@link DecodeElementNodeAdapter}.
		 */
		protected final void collectChildren(final DecodeElementNodeAdapter parent) {
			final DecodeElement elementNode = this.adapter.elementNode(parent.index);
			final int[] indices = this.adapter.children(elementNode);
			final int count = indices.length;
			if(count == 0) return;
			for(int child = 0, offset = this.childOffset; child < count; child++){
				final int index = indices[child] - offset;
				if(index >= 0){
					final DecodeElement childNode = this.adapter.elementNode(index);
					if(this.accept(childNode)){
						this.elements.add(new DecodeElementNodeAdapter(parent, index, child));
					}
				}
			}
		}

		/**
		 * Diese Methode ruft {@link #collectDescendantSelf(DecodeElementNodeAdapter)} für jeden {@link DecodeElement} in den {@link DecodeGroup} des gegebenen {@link DecodeElement}s auf.
		 * 
		 * @param parent {@link DecodeElementNodeAdapter}.
		 * @param element {@link DecodeElement}.
		 */
		protected final void collectDescendant(final DecodeElementNodeAdapter parent, final DecodeElement element) {
			final int[] indices = this.adapter.children(element);
			final int count = indices.length;
			if(count == 0) return;
			for(int child = 0, offset = this.childOffset; child < count; child++){
				final int index = indices[child] - offset;
				if(index >= 0){
					this.collectDescendantSelf(new DecodeElementNodeAdapter(parent, index, child));
				}
			}
		}

		/**
		 * Diese Methode fügt den gegebenen {@link DecodeElementNodeAdapter}s nur dann in die Ergebnis {@link List} {@link #elements} ein, wenn dieser via {@link #accept(DecodeElement)} akzeptiert wird. Anschließend wird {@link #collectDescendant(DecodeElementNodeAdapter, DecodeElement)} aufgerufen.
		 * 
		 * @param parent {@link DecodeElementNodeAdapter}.
		 */
		protected final void collectDescendantSelf(final DecodeElementNodeAdapter parent) {
			final DecodeElement element = this.adapter.elementNode(parent.index);
			if(this.accept(element)){
				this.elements.add(parent);
			}
			this.collectDescendant(parent, element);
		}

		/**
		 * Diese Methode leert die Ergebnisse.
		 */
		public void clear() {
			this.elements.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			return true;
		}

		/**
		 * Diese Methode Sucht in den {@link Element#getChildNodes()} des gegebenen {@link DecodeElementNodeAdapter}s nach {@link Element}s, die von der Methode {@link #accept(DecodeElement)} akzeptiert werden und speichert die Treffer in die {@link List} {@link #elements}. Der Modus entscheiden hierbei, ob die Suche rekursiv ist und ob sie den gegebenen {@link DecodeElementNodeAdapter} mit einbezieht.
		 * 
		 * @see #MODE_CHILDREN
		 * @see #MODE_DESCENDANT
		 * @see #MODE_DESCENDANT_SELF
		 * @param parent {@link DecodeElementNodeAdapter}.
		 * @param mode Modus der Suche.
		 * @throws IllegalArgumentException Wenn der gegebene Modus ungültig ist.
		 */
		public void collect(final DecodeElementNodeAdapter parent, final int mode) throws IllegalArgumentException {
			switch(mode){
				case MODE_CHILDREN:
					this.collectChildren(parent);
					break;
				case MODE_DESCENDANT:
					this.collectDescendant(parent, this.adapter.elementNode(parent.index));
					break;
				case MODE_DESCENDANT_SELF:
					this.collectDescendantSelf(parent);
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementNodeAdapter item(final int index) {
			if((index < 0) || (index >= this.elements.size())) return null;
			return this.elements.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return this.elements.size();
		}

		/**
		 * Diese Methode gibt die Ergebnisse der Suche als {@link List} zurück.
		 * 
		 * @return Ergebnisse der Suche als {@link List}.
		 */
		public List<DecodeElementNodeAdapter> getElements() {
			return this.elements;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, this.getClass().getSimpleName(), this.elements);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeElementNodeCollector}, der die {@link Element}s an Hand ihrer {@code URI} filtert.
	 * 
	 * @see Element#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementNodeUriCollector extends DecodeElementNodeCollector {

		/**
		 * Dieses Feld speichert den Index der {@code URI}-{@link DecodeValue}.
		 * 
		 * @see Element#getNamespaceURI()
		 */
		public final int uri;

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und Index der {@code URI}-{@link DecodeValue}.
		 * 
		 * @see Element#getNamespaceURI()
		 * @param adapter {@link DecodeAdapter}.
		 * @param uri Index der {@code URI}-{@link DecodeValue}.
		 */
		public DecodeElementNodeUriCollector(final DecodeAdapter adapter, final int uri) {
			super(adapter);
			this.uri = uri;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			final DecodeLabel elementLabel = this.adapter.elementLabel(element.label);
			return elementLabel.uri == this.uri;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeElementNodeCollector}, der die {@link Element}s an Hand ihres {@code Name} filtert.
	 * 
	 * @see Element#getLocalName()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementNodeNameCollector extends DecodeElementNodeCollector {

		/**
		 * Dieses Feld speichert den Index der {@code Name}-{@link DecodeValue}.
		 * 
		 * @see Element#getLocalName()
		 */
		public final int name;

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und Index der {@code URI}-{@link DecodeValue}.
		 * 
		 * @see Element#getLocalName()
		 * @param adapter {@link DecodeAdapter}.
		 * @param name Index der {@code Name}-{@link DecodeValue}.
		 */
		public DecodeElementNodeNameCollector(final DecodeAdapter adapter, final int name) {
			super(adapter);
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			final DecodeLabel elementLabel = this.adapter.elementLabel(element.label);
			return elementLabel.name == this.name;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeElementNodeCollector}, der die {@link Element}s an Hand ihrer {@code URI} und ihres {@code Name} filtert.
	 * 
	 * @see Element#getLocalName()
	 * @see Element#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementNodeLabelCollector extends DecodeElementNodeCollector {

		/**
		 * Dieses Feld speichert den Index des {@code Element}-{@link DecodeLabel}s.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final int label;

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeAdapter} und Index des {@code Element}-{@link DecodeLabel}s.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @param adapter {@link DecodeAdapter}.
		 * @param label Index des {@code Element}-{@link DecodeLabel}s.
		 */
		public DecodeElementNodeLabelCollector(final DecodeAdapter adapter, final int label) {
			super(adapter);
			this.label = label;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			return element.label == this.label;
		}

	}

	/**
	 * Diese Methode liest die gegebene Anzahl an {@code int}s aus der gegebenen {@link DecodeSource} und gibt sie als Array zurück.
	 * 
	 * @see Decoder#readBytes(DecodeSource, int)
	 * @param source {@link DecodeSource}.
	 * @param count Anzahl der {@code int}s.
	 * @return {@code int}-Array.
	 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
	 */
	protected static final int[] readInts(final DecodeSource source, final int count) throws IOException {
		if(count == 0) return Coder.VOID_INTS;
		final byte[] bytes = Decoder.readBytes(source, count << 2);
		return Coder.decodeIndices(bytes);
	}

	/**
	 * Diese Methode liest die gegebene Anzahl an {@code byte}s aus der gegebenen {@link DecodeSource} und gibt sie als Array zurück.
	 * 
	 * @see DecodeSource#read(byte[], int, int)
	 * @param source {@link DecodeSource}.
	 * @param count Anzahl der {@code byte}s.
	 * @return {@code byte}-Array.
	 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
	 */
	protected static final byte[] readBytes(final DecodeSource source, final int count) throws IOException {
		if(count == 0) return Coder.VOID_BYTES;
		final byte[] bytes = new byte[count];
		source.read(bytes, 0, count);
		return bytes;
	}

	/**
	 * Dieser Konstrukteur initialisiert den {@link Decoder}.
	 */
	public Decoder() {
	}

	/**
	 * Diese Methode liest das gegebene {@link File} in einen neuen {@link DecodeAdapter} ein und gibt dessen {@link DecodeDocumentNodeAdapter} zurück.
	 * 
	 * @see DecodeSourceFile
	 * @see RandomAccessFile
	 * @see #decode(DecodeSource)
	 * @param source {@link File}.
	 * @return {@link DecodeDocumentNodeAdapter}.
	 * @throws IOException Wenn eine {@link IOException} auftritt.
	 * @throws NullPointerException Wenn das gegebene {@link File} {@code null} ist.
	 * @throws FileNotFoundException Wenn das gegebene {@link File} nicht existiert.
	 */
	public DecodeDocumentNodeAdapter decode(final File source) throws NullPointerException, FileNotFoundException, IOException {
		return this.decode(new DecodeSourceFile(new RandomAccessFile(source, "r")));
	}

	/**
	 * Diese Methode liest die gegebene {@link DecodeSource} in einen neuen {@link DecodeAdapter} ein und gibt dessen {@link DecodeDocumentNodeAdapter} zurück.
	 * 
	 * @see DecodeAdapter#getOwnerDocument()
	 * @param source {@link DecodeSource}.
	 * @return {@link DecodeDocumentNodeAdapter}.
	 * @throws IOException Wenn das {@link DecodeSource} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn die gegebene {@link DecodeSource} {@code null} ist.
	 */
	public DecodeDocumentNodeAdapter decode(final DecodeSource source) throws IOException {
		return new DecodeAdapter(new DecodeDocument(source)).getOwnerDocument();
	}

}
