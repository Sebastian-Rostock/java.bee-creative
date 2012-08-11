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
import bee.creative.xml.coder.Encoder.EncodeAttribute;
import bee.creative.xml.coder.Encoder.EncodeElement;

/**
 * Diese Klasse implementiert Methoden zur Dekodierung eines XML-Dokuments aus der von einem {@link Encoder} erzeugten,
 * optimierten, binären Darstellung.
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
		 * Diese Methode liest die gegebene Anzahl an {@code byte}s ab der aktuellen Leseposition aus der Eingabe in das
		 * gegebene {@code byte}-Array an die gegebene Position ein und vergrößert die Leseposition um die gegebene Anzahl.
		 * 
		 * @see #index()
		 * @param array {@code byte}-Array.
		 * @param offset Index des ersten gelesenen {@code byte}s.
		 * @param length Anzahl der zulesenden {@code byte}s.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public void read(byte[] array, int offset, int length) throws IOException;

		/**
		 * Diese Methode setzt die Leseposition der Eingabe, ab der via {@link #read(byte[], int, int)} die nächsten
		 * {@code byte}s gelesen werden können.
		 * 
		 * @see #index()
		 * @param index Leseposition.
		 * @throws IOException Wenn die gegebene Position negativ ist oder ein I/O-Fehler auftritt.
		 */
		public void seek(long index) throws IOException;

		/**
		 * Diese Methode gibt die aktuelle Leseposition zurück, ab der via {@link #read(byte[], int, int)} die nächsten
		 * {@code byte}s gelesen werden können.
		 * 
		 * @return Leseposition.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 */
		public long index() throws IOException;

	}

	/**
	 * Diese Schnittstelle definiert eine {@link NodeList} mit {@link DecodeChildAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ChildList extends NodeList {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildAdapter item(int index);

	}

	/**
	 * Diese Schnittstelle definiert eine {@link NodeList} mit {@link DecodeElementAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static interface ElementList extends ChildList {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementAdapter item(int index);

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
	 * Diese Klasse implementiert ein abstraktes Objekt zur Verwaltung mehrerer Elemente, die über einen Index
	 * identifiziert und nachgeladen werden können.
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
			 * Dieses Feld speichert die Anzahl der Bits zur Adressierung der Elemente innerhalb einer {@link DecodePoolPage}
			 * .
			 */
			static final int PAGE_BITS = 7;

			/**
			 * Dieses Feld speichert die maximale Anzahl der Elemente in einer {@link DecodePoolPage}.
			 */
			static final int PAGE_SIZE = 1 << DecodePoolPage.PAGE_BITS;

			/**
			 * Dieses Feld speichert die Bitmaske zur Ermittlung des Index eines Elements innerhalb einer
			 * {@link DecodePoolPage}.
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
		 * Diese Klasse implementiert das {@link Get} für {@link Comparables#binarySearch(Get, Comparable, int, int)} zur
		 * direkten Suche in einem {@link DecodePool}.
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
		 * Diese Klasse implementiert das {@link Get} für {@link Comparables#binarySearch(Get, Comparable, int, int)} zur
		 * indirekten Suche in einem {@link DecodePool}.
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
		int maxSize = 256;

		/**
		 * Dieses Feld speichert die Kapazität.
		 */
		int capacity;

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück. Wenn dieses noch nicht existiert, wird es via
		 * {@link #load(int)} nachgeladen.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws NullPointerException Wenn das von {@link #load(int)} geladene Element {@code null} ist.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		@SuppressWarnings ("unchecked")
		public GItem get(final int index) throws NullPointerException, IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException("index < 0");
			if(index >= this.capacity) throw new IndexOutOfBoundsException("index >= capacity");
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
		 * Diese Methode sucht binäre nach dem ersten Treffer des gegebenen {@link Comparable}s und gibt diesen oder
		 * {@code null} zurück.
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
		 * Diese Methode sucht binäre im gegebenen Suchraum nach dem ersten Treffer des gegebenen {@link Comparable}s und
		 * gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodePoolGetSection
		 * @see Comparables#binarySearch(Get, Comparable, int, int)
		 * @param indices Suchraum.
		 * @param comparable {@link Comparable}.
		 * @return erster Treffer oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene {@link Comparable} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public GItem find(final int[] indices, final Comparable<? super GItem> comparable) throws NullPointerException {
			if(indices == null) throw new NullPointerException(Coder.MESSAGE_NULL_INDICES);
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
		 * Diese Methode entfernt solange überflüssige Elemente, bis die aktuelle Anzahl der Elemente nicht mehr größer der
		 * minimalen Anzahl ist.
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
		 * Diese Methode gibt die minimale Anzahl der Elemente zurück. Wenn die aktuelle Anzahl der Elemente größer der
		 * maximalen Anzahl ist und die Methode {@link #compact()} aufgerufen wird, entfernt diese solange überflüssige
		 * Elemente, bis die aktuelle Anzahl der Elemente gleich der minimalen Anzahl ist.
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
		 * Diese Methode gibt die maximale Anzahl der Elemente zurück. Wenn die aktuelle Anzahl der Elemente nicht kleiner
		 * als die maximalen Anzahl ist und in der Methode {@link #get(int)} ein fehlendes Element erzeugt werden muss, wird
		 * die Methode {@link #compact()} automatisch aufgerufen.
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
		 * Diese Methode gibt die Kapazität zurück. Diese Begrenzt die Menge der Indices der Elemente, da für jeden Index
		 * {@code index} eines Elements gilt:
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
	 * Diese Klasse implementiert einen abstrakten Datensatz, der von einem {@link DecodePool} erzeugt und verwaltet sowie
	 * aus einer {@link DecodeSource} geladen wird.
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
	 * Diese Klasse implementiert einen {@link DecodePool}, dessen Elemente und Kapazität aus eier {@link DecodeSource}
	 * geladen werden.
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
		 * Dieses Feld speichert die Position in der {@link DecodeSource}, an der die Daten dieses {@link DecodeItemPool}s
		 * beginnen.
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
		 * Diese Methode navigiert in der {@link DecodeSource} an die Position des {@code index}-ten Elements. Die Position
		 * ergibt sich aus {@code sourceIndex + 4 + index * itemSize}.
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
		 * Diese Methode gibt die Position zurück, an der die Daten dieses {@link DecodeItemPool}s in der
		 * {@link DecodeSource} beginnen.
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
		 * Diese Methode navigiert in der {@link DecodeSource} an die Position des {@code offset}-ten Werts. Die Position
		 * ergibt sich aus {@code sourceIndex + 4 + itemSize * itemCount + 4 + offset * valueSize}.
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
		protected final String value;

		/**
		 * Dieser Konstrukteur initialisiert Index und {@link String}.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodeItemPool}.
		 * @param value {@link String}.
		 */
		public DecodeValue(final int index, final String value) {
			super(index);
			this.value = value;
		}

		/**
		 * Diese Methode gibt den {@link String} zurück.
		 * 
		 * @return {@link String}.
		 */
		public String value() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "[" + this.index + "]" + Objects.toString(this.value);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DecodePool} der {@link DecodeValue}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeValuePool extends DecodeListPool<DecodeValue> {

		/**
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eines {@link DecodeValue}s über seinen
		 * {@link String}.
		 * 
		 * @see DecodeValue#value()
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
				return this.value.compareTo(input.value);
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
		 * Dieser Konstrukteur initialisiert {@link DecodeSource} sowie {@code Hash-Tabelle} und lädt die Header.
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
		 * Diese Methode gibt das Ergebnis von {@link #findValue(String)} zurück und löst eine
		 * {@link IllegalArgumentException} aus, wenn kein Datensatz gefunden wird.
		 * 
		 * @see #findValue(String)
		 * @param value {@link String}.
		 * @return {@link DecodeValue}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 * @throws IllegalArgumentException Wenn {@link #findValue(String)} {@code null} liefert.
		 */
		public DecodeValue getValue(final String value) throws NullPointerException, IllegalArgumentException {
			final DecodeValue item = this.findValue(value);
			if(item == null) throw new IllegalArgumentException("value not found: " + value);
			return item;
		}

		/**
		 * Diese Methode sucht binäre nach dem {@link DecodeValue} mit dem gegebenen {@link String} und gibt diesen oder
		 * {@code null} zurück. Wenn der {@link #valueHash()} nicht leer ist, wind nur im zum gegebenen {@link String}
		 * ermittelten Index-Array gesucht.
		 * 
		 * @see Coder#hashValue(String)
		 * @see Encoder#ValueComparator
		 * @see DecodePool#find(Comparable)
		 * @see DecodeValue#value()
		 * @param value {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public DecodeValue findValue(final String value) throws NullPointerException {
			if(value == null) throw new NullPointerException(Coder.MESSAGE_NULL_VALUE);
			if(this.itemCount == 0) return null;
			if(this.valueHashMask < 0) return this.find(new DecodeValueComparable(value));
			return this.findValue(this.valueHash.get(Coder.hashValue(value) & this.valueHashMask).values, value);
		}

		/**
		 * Diese Methode sucht im gegebenen Index-Array binäre nach dem {@link DecodeValue} mit dem gegebenen {@link String}
		 * und gibt diesen oder {@code null} zurück.
		 * 
		 * @see Encoder#ValueComparator
		 * @see DecodePool#find(int[], Comparable)
		 * @see DecodeValue#value()
		 * @param indices Index-Array.
		 * @param value {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} bzw. das gegebene Index-Array {@code null} ist.
		 */
		public DecodeValue findValue(final int[] indices, final String value) throws NullPointerException {
			if(indices == null) throw new NullPointerException(Coder.MESSAGE_NULL_INDICES);
			if(value == null) throw new NullPointerException(Coder.MESSAGE_NULL_VALUE);
			if(indices.length == 0) return null;
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
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eines {@link DecodeLabel}s über den Index
		 * seines {@code URI}-{@link DecodeValue}s.
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
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eines {@link DecodeLabel}s über die Indices
		 * seiner {@code URI}- und {@code Name}-{@link DecodeValue}s.
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
		 * Dieser Konstrukteur initialisiert {@link DecodeSource} sowie {@code Hash-Tabelle} und lädt die Header.
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
		 * Diese Methode gibt das Ergebnis von {@link #findUri(int[], int)} zurück und löst eine
		 * {@link IllegalArgumentException} aus, wenn kein {@link DecodeLabel} gefunden wird.
		 * 
		 * @see DecodeLabelPool#findUri(int[], int)
		 * @param indices Index-Array.
		 * @param uri Index des {@code URI}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel}.
		 * @throws IllegalArgumentException Wenn {@link #findUri(int[], int)} {@code null} liefert.
		 */
		public DecodeLabel getUri(final int[] indices, final int uri) throws IllegalArgumentException {
			final DecodeLabel item = this.findUri(indices, uri);
			if(item == null) throw new IllegalArgumentException("uri not found: " + uri);
			return item;
		}

		/**
		 * Diese Methode sucht binär im gegebenen Index-Array nach dem {@link DecodeLabel} mit dem gegebenen Index des
		 * {@code URI}-{@link DecodeValue}s und gibt dieses {@link DecodeLabel} oder {@code null} zurück.
		 * 
		 * @see Encoder#LabelComparator
		 * @see DecodePool#find(int[], Comparable)
		 * @see DecodeLabel#uri()
		 * @param indices Index-Array.
		 * @param uri Index des {@code URI}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public DecodeLabel findUri(final int[] indices, final int uri) throws NullPointerException {
			if(indices == null) throw new NullPointerException(Coder.MESSAGE_NULL_INDICES);
			if(indices.length == 0) return null;
			return this.find(indices, new DecodeLabelUriComparable(uri));
		}

		/**
		 * Diese Methode sucht linear im gegebenen Index-Array nach dem {@link DecodeLabel} mit dem gegebenen Index des
		 * {@code Name}-{@link DecodeValue}s und gibt dieses {@link DecodeLabel} oder {@code null} zurück.
		 * 
		 * @see DecodeLabel#name()
		 * @param indices Index-Array.
		 * @see DecodeLabel#name
		 * @param name Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public DecodeLabel findName(final int[] indices, final int name) {
			if(this.itemCount == 0) return null;
			for(int i = 0, size = indices.length; i < size; i++){
				final DecodeLabel value = this.get(indices[i]);
				if(value.name == name) return value;
			}
			return null;
		}

		/**
		 * Diese Methode sucht binäre nach dem {@link DecodeLabel} mit den gegebenen Indices der {@code URI}- und
		 * {@code Name}-{@link DecodeValue}s und gibt dieses {@link DecodeLabel} oder {@code null} zurück. Wenn der
		 * {@link #labelHash()} nicht leer ist, wind nur im zu den gegebenen Indices ermittelten Index-Array gesucht.
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
			return this.findLabel(this.labelHash.get(Coder.hashLabel(uri, name) & this.labelHashMask).values, uri, name);
		}

		/**
		 * Diese Methode sucht binäre im gegebenen Index-Array nach dem {@link DecodeLabel} mit den gegebenen {@code URI}-
		 * bzw. {@code Name}-Indices und gibt diesen oder {@code null} zurück. Die {@link DecodeLabel} müssen dazum im
		 * {@link DecodeItemPool} aufsteigend sortiert sein.
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
			if(indices == null) throw new NullPointerException(Coder.MESSAGE_NULL_INDICES);
			if(indices.length == 0) return null;
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
		protected final int[] values;

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index Index dieses Datensatzes im {@link DecodeItemPool}.
		 * @param values Indices der Datensätze.
		 */
		public DecodeGroup(final int index, final int[] values) {
			super(index);
			this.values = values;
		}

		/**
		 * Diese Methode gibt die Indices der Datensätze zurück.
		 * 
		 * @return Indices der Datensätze.
		 */
		public int[] indices() {
			return this.values.clone();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, true, "DecodeGroup", "index", this.index, "values", this.values);
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
		 * Dieses Feld speichert den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-
		 * {@link DecodeValue}s.
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
		 * Diese Methode gibt den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s
		 * zurück.
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
		 * Diese Methode gibt den Index der {@link DecodeGroup} der {@link Element#getChildNodes()} zurück. Die Indices in
		 * dieser {@link DecodeGroup} verweisen auf {@link DecodeValue}s bzw. {@link DecodeElement}s, wobei die Indices der
		 * {@link DecodeElement} um die Anzahl der {@link DecodeValue}s im {@link DecodeDocument#valuePool()} verschoben
		 * sind.
		 * 
		 * @see DecodeAdapter#offset()
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
		 * Dieser Konstrukteur initialisiert {@link DecodeSource} sowie {@code xmlns}-Aktivierung und lädt die Header. Die
		 * {@code xmlns}-Aktivierung entscheidet über die Größe der Elemente in der {@link DecodeSource}.
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

	static abstract class DecodeElementIterator implements ListIterator<DecodeElement>, Filter<DecodeElement> {

		int offset;

		int[] values;

		DecodeDocument document;

		DecodeElement next;

		DecodeElement prev;

		int nextIndex;

		int prevIndex;

		DecodeElementIterator(final DecodeDocument document) {
			this.offset = document.valuePool.itemCount;
			this.document = document;
			this.nextIndex = -1;
		}

		/**
		 * Dieser Konstrukteur initialisiert {@link DecodeDocument} und Index der {@link DecodeGroup}.
		 * 
		 * @param document {@link DecodeDocument}.
		 * @param children Index der {@link DecodeGroup} von {@link DecodeElement#children()}.
		 */
		public DecodeElementIterator(final DecodeDocument document, final int children) {
			this(document);
			this.values = document.elementChildrenPool.get(children).values;
			this.doNext();
		}

		public DecodeElementIterator(final DecodeDocument document, final int[] values) {
			this(document);
			this.values = values;
			this.doNext();
		}

		void doNext() {
			this.prev = this.next;
			this.prevIndex = this.nextIndex;
			while(++this.nextIndex < this.values.length){
				final int index = this.values[this.nextIndex] - this.offset;
				if((index >= 0) && this.accept(this.next = this.document.elementNodePool.get(index))) return;
			}
			this.next = null;
		}

		void doPrev() {
			this.next = this.prev;
			this.nextIndex = this.prevIndex;
			while(--this.prevIndex >= 0){
				final int index = this.values[this.prevIndex] - this.offset;
				if((index >= 0) && this.accept(this.prev = this.document.elementNodePool.get(index))) return;
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
			this.doNext();
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
			this.doPrev();
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
		 * /** Dieses Feld speichert den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-
		 * {@link DecodeValue}s.
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
		 * Diese Methode gibt den Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s
		 * zurück.
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
		 * Diese Klasse implementiert das {@link Comparable} zur binären Suche eine {@link DecodeAttribute} mit einem
		 * bestimmten {@code Label}-Index.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class DecodeAttributeComparable implements Comparable<DecodeAttribute> {

			/**
			 * Dieses Feld speichert den {@code Label}-Index.
			 */
			final int label;

			/**
			 * Dieser Konstrukteur initialisiert den {@code Label}-Index.
			 * 
			 * @param label {@code Label}-Index.
			 */
			public DecodeAttributeComparable(final int label) {
				this.label = label;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int compareTo(final DecodeAttribute value) {
				return Comparators.compare(this.label, value.label);
			}

		}

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

		/**
		 * Diese Methode sucht binäre im gegebenen Index-Array nach dem {@link DecodeAttribute} mit dem gegebenen Index des
		 * {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s und gibt dieses
		 * {@link DecodeAttribute} oder {@code null} zurück.
		 * 
		 * @see Encoder#AttributeNameComparator
		 * @see Encoder#AttributeLabelComparator
		 * @see DecodePool#find(int[], Comparable)
		 * @see DecodeAttribute#label()
		 * @param indices Index-Array.
		 * @param label Index des {@code URI/Name}-{@link DecodeLabel}s bzw. des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeAttribute} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public DecodeAttribute findLabel(final int[] indices, final int label) throws NullPointerException {
			if(indices == null) throw new NullPointerException(Coder.MESSAGE_NULL_INDICES);
			if(indices.length == 0) return null;
			return this.find(indices, new DecodeAttributeComparable(label));
		}

	}

	/**
	 * Diese Klasse implementiert eine Zusammenfassung mehrerer {@link DecodePool}s zur Abstraktion eines {@link Document}
	 * s.
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
		 * Dieses Feld speichert den {@code Value}-{@link DecodeValuePool} für {@link DecodeDocument#elementChildrenPool()}
		 * und {@link DecodeAttribute#value()}.
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
		 * Dieses Feld speichert den {@code URI/Prefix}-{@link DecodeLabelPool} für
		 * {@link DecodeDocument#elementXmlnsPool()}.
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
			this.documentElement = Decoder.readInts(source, 1)[0];
		}

		/**
		 * Diese Methode gibt die {@code xmlns}-Aktivierung zurück. Wenn diese Option {@code true} ist, besitzen
		 * {@link EncodeElement}s und {@link EncodeAttribute}s neben einem {@code Name} auch eine {@code URI} und einen
		 * {@code Prefix}.
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
		 * Diese Methode gibt den {@code Value}-{@link DecodeValuePool} für {@link DecodeDocument#elementChildrenPool()} und
		 * {@link DecodeAttribute#value()} zurück.
		 * 
		 * @see Text#getNodeValue()
		 * @see Attr#getNodeValue()
		 * @return {@code Value}-{@link DecodeValuePool} für {@link DecodeDocument#elementChildrenPool()} und
		 *         {@link DecodeAttribute#value()}.
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
		 * Diese Methode gibt den {@code URI/Prefix}-{@link DecodeLabelPool} für {@link DecodeDocument#elementXmlnsPool()}
		 * zurück.
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
				"documentElement", this.documentElement //
				);
		}

	}

	/**
	 * Diese Klasse implementiert die Methoden zum Auslesen eines {@link DecodeDocument}s als Grundlage eines
	 * {@link DecodeDocumentAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAdapter {

		/**
		 * Dieses Feld speichert die leere {@link ElementList}.
		 */
		public static final ElementList VOID_NODE_LIST = new ElementList() {

			@Override
			public DecodeElementAdapter item(final int index) {
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
		 * Dieses Feld speichert die Verschiebung des Indexes der {@link DecodeElement}s in den Indices der
		 * {@link DecodeGroup}. Der Wert entspricht der Anzahl der {@link DecodeValue}s in {@link DecodeDocument#valuePool}.
		 * 
		 * @see #getElementChild(DecodeNodeAdapter, int, int)
		 */
		protected final int offset;

		/**
		 * Dieses Feld speichert den {@link DecodeDocument}.
		 */
		protected final DecodeDocument document;

		/**
		 * Dieses Feld speichert den {@link DecodeDocumentAdapter}.
		 */
		protected final DecodeDocumentAdapter documentAdapter;

		/**
		 * Dieses Feld speichert die {@code xmlns}-Aktivierung.
		 */
		protected final boolean xmlnsEnabled;

		protected final int voidUriIndex;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeDocument}.
		 * 
		 * @param document {@link DecodeDocument}.
		 */
		public DecodeAdapter(final DecodeDocument document) {
			this.offset = document.valuePool.itemCount;
			this.document = document;
			this.xmlnsEnabled = document.xmlnsEnabled;
			this.documentAdapter = new DecodeDocumentAdapter(this);
			final DecodeValue voidUri = document.uriPool.findValue("");
			this.voidUriIndex = ((voidUri == null) ? -1 : voidUri.index);
		}

		/**
		 * Diese Methode gibt die Verschiebung des Indexes der {@link DecodeElement}s in den Indices der {@link DecodeGroup}
		 * zu {@link DecodeElement#children()} zurück. Der Wert entspricht der Anzahl der {@link DecodeValue}s in
		 * {@link DecodeDocument#valuePool()}.
		 * 
		 * @see #getElementChild(DecodeNodeAdapter, int, int)
		 * @return Verschiebung des Indexes der {@link DecodeElement}s.
		 */
		public int offset() {
			return this.offset;
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#uriPool()} zurück.
		 * 
		 * @see DecodeDocument#uriPool()
		 * @see DecodeValuePool#get(int)
		 * @param uriIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue uri(final int uriIndex) throws IndexOutOfBoundsException {
			return this.document.uriPool.get(uriIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#uriPool()} nach dem {@link DecodeValue} mit dem gegebenen
		 * {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeDocument#uriPool()
		 * @see DecodeValuePool#findValue(String)
		 * @param uriString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue uri(final String uriString) throws NullPointerException {
			return this.document.uriPool.findValue(uriString);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#valuePool()} zurück.
		 * 
		 * @see DecodeAdapter#value(int)
		 * @param textValueIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue textValue(final int textValueIndex) {
			return this.value(textValueIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#valuePool()} nach dem {@link DecodeValue} mit dem gegebenen
		 * {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#value(String)
		 * @param textValueString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue textValue(final String textValueString) {
			return this.value(textValueString);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#valuePool()} zurück.
		 * 
		 * @see DecodeDocument#valuePool()
		 * @see DecodeValuePool#get(int)
		 * @param valueIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue value(final int valueIndex) throws IndexOutOfBoundsException {
			return this.document.valuePool.get(valueIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#valuePool()} nach dem {@link DecodeValue} mit dem gegebenen
		 * {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeDocument#valuePool()
		 * @see DecodeValuePool#findValue(String)
		 * @param valueString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue value(final String valueString) throws NullPointerException {
			return this.document.valuePool.findValue(valueString);
		}

		public final String string(final DecodeValue value) {
			if(value == null) return null;
			return value.value;
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#xmlnsNamePool()} zurück.
		 * 
		 * @see DecodeDocument#xmlnsNamePool()
		 * @see DecodeValuePool#get(int)
		 * @param xmlnsNameIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue xmlnsName(final int xmlnsNameIndex) throws IndexOutOfBoundsException {
			return this.document.xmlnsNamePool.get(xmlnsNameIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#xmlnsNamePool()} nach dem {@link DecodeValue} mit dem
		 * gegebenen {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeDocument#xmlnsNamePool()
		 * @see DecodeValuePool#findValue(String)
		 * @param xmlnsNameString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue xmlnsName(final String xmlnsNameString) throws NullPointerException {
			return this.document.xmlnsNamePool.findValue(xmlnsNameString);
		}

		public DecodeLabel xmlnsLabel(final int xmlnsLabelIndex) {
			return this.document.xmlnsLabelPool.get(xmlnsLabelIndex);
		}

		public DecodeLabel xmlnsLabel(final int xmlnsUriIndex, final int xmlnsNameIndex) {
			return this.document.xmlnsLabelPool.findLabel(xmlnsUriIndex, xmlnsNameIndex);
		}

		public final DecodeValue elementUri(final int elementUriIndex) {
			return this.uri(elementUriIndex);
		}

		public final DecodeValue elementUri(final String elementUriString) {
			return this.uri(elementUriString);
		}

		public final DecodeValue elementUri(final DecodeLabel elementLabel) {
			if(elementLabel == null) return null;
			return this.elementUri(elementLabel.uri);
		}

		public final DecodeValue elementName(final int nameIndex) {
			return this.document.elementNamePool.get(nameIndex);
		}

		public final DecodeValue elementName(final String nameString) {
			return this.document.elementNamePool.findValue(nameString);
		}

		public final DecodeValue elementName(final DecodeLabel elementLabel) {
			if(elementLabel == null) return null;
			return this.elementName(elementLabel.name);
		}

		/**
		 * Diese Methode gibt das {@code index}-te {@link DecodeElement} aus {@link DecodeDocument#elementNodePool()}
		 * zurück.
		 * 
		 * @see DecodeDocument#elementNodePool()
		 * @see DecodeElementPool#get(int)
		 * @param elementNodeIndex Index.
		 * @return {@code index}-tes {@link DecodeElement}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeElement elementNode(final int elementNodeIndex) throws IndexOutOfBoundsException {
			return this.document.elementNodePool.get(elementNodeIndex);
		}

		public DecodeLabel elementLabel(final int elementLabelIndex) {
			return this.document.elementLabelPool.get(elementLabelIndex);
		}

		public DecodeItem elementLabel(final DecodeElement elementNode) {
			if(elementNode == null) return null;
			if(!this.xmlnsEnabled) return this.elementName(elementNode.label);
			return this.elementLabel(elementNode.label);
		}

		public DecodeLabel elementLabel(final int elementUriIndex, final int elementNameIndex) {
			return this.document.elementLabelPool.findLabel(elementUriIndex, elementNameIndex);
		}

		/**
		 * Diese Methode gibt das via {@link DecodeElement#label()} referenzierte {@code URI/Name}-{@link DecodeLabel} bzw.
		 * den {@code Name}-{@link DecodeValue} mit der gegebenen {@code URI} und dem gegebenen {@code Name} zurück.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @param elementUriString {@code URI}.
		 * @param elementNameString {@code Name}.
		 * @return {@code URI/Name}-{@link DecodeLabel} bzw. des {@code Name}-{@link DecodeValue} oder {@code null}.
		 */
		public DecodeItem elementLabel(final String elementUriString, final String elementNameString) {
			final DecodeValue elementName = this.elementName(elementNameString);
			if(!this.xmlnsEnabled || (elementName == null)) return elementName;
			return this.elementLabel(this.elementUri(elementUriString), elementName);
		}

		public DecodeLabel elementLabel(final DecodeValue elementUri, final DecodeValue elementName) {
			if((elementUri == null) || (elementName == null)) return null;
			return this.elementLabel(elementUri.index, elementName.index);
		}

		/**
		 * Diese Methode gibt die {@code index}-te {@link DecodeGroup} aus {@link DecodeDocument#elementXmlnsPool()} zurück.
		 * 
		 * @see DecodeDocument#elementXmlnsPool()
		 * @see DecodeGroupPool#get(int)
		 * @param elementXmlnsIndex Index.
		 * @return {@code index}-te {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup elementXmlns(final int elementXmlnsIndex) throws IndexOutOfBoundsException {
			return this.document.elementXmlnsPool.get(elementXmlnsIndex);
		}

		/**
		 * Diese Methode gibt die via {@link DecodeElement#xmlns()} referenzierten {@link DecodeGroup} oder {@code null}
		 * zurück. Wenn das gegebene {@link DecodeElement} {@code null} ist, wird {@code null} zurück gegeben.
		 * 
		 * @see DecodeAdapter#elementXmlns(int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeGroup} zu {@link DecodeElement#xmlns()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeElement#xmlns()} ein ungültiger Index ist.
		 */
		public final DecodeGroup elementXmlns(final DecodeElement elementNode) throws IndexOutOfBoundsException {
			if(elementNode == null) return null;
			return this.elementXmlns(elementNode.xmlns);
		}

		/**
		 * Diese Methode gibt die {@code index}-te {@link DecodeGroup} aus {@link DecodeDocument#elementChildrenPool()}
		 * zurück.
		 * 
		 * @see DecodeDocument#elementChildrenPool()
		 * @see DecodeGroupPool#get(int)
		 * @param elementChildrenIndex Index.
		 * @return {@code index}-te {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup elementChildren(final int elementChildrenIndex) throws IndexOutOfBoundsException {
			return this.document.elementChildrenPool.get(elementChildrenIndex);
		}

		/**
		 * Diese Methode gibt die via {@link DecodeElement#children()} referenzierten {@link DecodeGroup} oder {@code null}
		 * zurück. Wenn das gegebene {@link DecodeElement} {@code null} ist, wird {@code null} zurück gegeben.
		 * 
		 * @see DecodeAdapter#elementChildren(int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeGroup} zu {@link DecodeElement#children()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeElement#children()} ein ungültiger Index ist.
		 */
		public final DecodeGroup elementChildren(final DecodeElement elementNode) throws IndexOutOfBoundsException {
			if(elementNode == null) return null;
			return this.elementChildren(elementNode.children);
		}

		/**
		 * Diese Methode gibt die {@code index}-te {@link DecodeGroup} aus {@link DecodeDocument#elementAttributesPool()}
		 * zurück.
		 * 
		 * @see DecodeDocument#elementAttributesPool()
		 * @see DecodeGroupPool#get(int)
		 * @param elementAttributesIndex Index.
		 * @return {@code index}-te {@link DecodeGroup}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeGroup elementAttributes(final int elementAttributesIndex) throws IndexOutOfBoundsException {
			return this.document.elementAttributesPool.get(elementAttributesIndex);
		}

		/**
		 * Diese Methode gibt die via {@link DecodeElement#attributes()} referenzierten {@link DecodeGroup} oder
		 * {@code null} zurück. Wenn das gegebene {@link DecodeElement} {@code null} ist, wird {@code null} zurück gegeben.
		 * 
		 * @see DecodeAdapter#elementAttributes(int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link DecodeGroup} zu {@link DecodeElement#attributes()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeElement#attributes()} ein ungültiger Index ist.
		 */
		public final DecodeGroup elementAttributes(final DecodeElement elementNode) throws IndexOutOfBoundsException {
			if(elementNode == null) return null;
			return this.elementAttributes(elementNode.attributes);
		}

		public int elementChildIndex(final int[] childNodeIndices, final int childLabelIndex, final int childIndex) {
			if((childIndex < 0) || (childIndex >= childNodeIndices.length)) return -1;
			return this.elementChildIndex_(childNodeIndices, childLabelIndex, childIndex);
		}

		public int elementChildIndex(final int[] childNodeIndices, final DecodeItem childLabelItem, final int childIndex) {
			if((childIndex < 0) || (childIndex >= childNodeIndices.length)) return -1;
			return this.elementChildIndex_(childNodeIndices, childLabelItem, childIndex);
		}

		public int elementChildIndex(final int[] childNodeIndices, final int childUriIndex, final int childNameIndex, final int childIndex) {
			if((childIndex < 0) || (childIndex >= childNodeIndices.length)) return -1;
			return this.elementChildIndex_(childNodeIndices, this.elementLabel(childUriIndex, childNameIndex), childIndex);
		}

		public int elementChildIndex(final int[] childNodeIndices, final String childUriString, final String childNameString, final int childIndex) {
			if((childIndex < 0) || (childIndex >= childNodeIndices.length)) return -1;
			return this.elementChildIndex_(childNodeIndices, this.elementLabel(childUriString, childNameString), childIndex);
		}

		public int elementChildIndex(final int[] childNodeIndices, final DecodeValue childUri, final DecodeValue childName, final int childIndex) {
			if((childIndex < 0) || (childIndex >= childNodeIndices.length)) return -1;
			return this.elementChildIndex_(childNodeIndices, this.elementLabel(childUri, childName), childIndex);
		}

		private int elementChildIndex_(final int[] childNodeIndices, final int childLabelIndex, final int childIndex) {
			for(int i = childIndex, size = childNodeIndices.length; i < size; i++){
				final int index = childNodeIndices[i] - this.offset;
				if(index >= 0){
					final DecodeElement elementNode = this.elementNode(index);
					if(elementNode.label == childLabelIndex) return i;
				}
			}
			return -1;
		}

		private int elementChildIndex_(final int[] childNodeIndices, final DecodeItem childLabelItem, final int childIndex) {
			if(childLabelItem == null) return -1;
			return this.elementChildIndex_(childNodeIndices, childLabelItem.index, childIndex);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#uriPool()} zurück.
		 * 
		 * @see DecodeAdapter#uri(int)
		 * @param attributeUriIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue attributeUri(final int attributeUriIndex) throws IndexOutOfBoundsException {
			return this.uri(attributeUriIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#uriPool()} nach dem {@link DecodeValue} mit dem gegebenen
		 * {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#uri(String)
		 * @param attributeUriString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue attributeUri(final String attributeUriString) throws NullPointerException {
			return this.uri(attributeUriString);
		}

		/**
		 * Diese Methode gibt den via {@link DecodeLabel#uri()} referenzierten {@link DecodeValue} oder {@code null} zurück.
		 * Wenn das gegebene {@link DecodeLabel} {@code null} ist, wird {@code null} zurück gegeben.
		 * 
		 * @see DecodeAdapter#attributeUri(int)
		 * @param attributeLabel {@link DecodeLabel}.
		 * @return {@link DecodeValue} zu {@link DecodeLabel#uri()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeLabel#uri()} ein ungültiger Index ist.
		 */
		public final DecodeValue attributeUri(final DecodeLabel attributeLabel) throws IndexOutOfBoundsException {
			if(attributeLabel == null) return null;
			return this.attributeUri(attributeLabel.uri);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#attributeNamePool()}
		 * zurück.
		 * 
		 * @see DecodeDocument#attributeNamePool()
		 * @see DecodeValuePool#get(int)
		 * @param attributeNameIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue attributeName(final int attributeNameIndex) throws IndexOutOfBoundsException {
			return this.document.attributeNamePool.get(attributeNameIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#attributeNamePool()} nach dem {@link DecodeValue} mit dem
		 * gegebenen {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeDocument#attributeNamePool()
		 * @see DecodeValuePool#findValue(String)
		 * @param attributeNameString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue attributeName(final String attributeNameString) throws NullPointerException {
			return this.document.attributeNamePool.findValue(attributeNameString);
		}

		/**
		 * Diese Methode gibt den via {@link DecodeLabel#name()} referenzierten {@link DecodeValue} oder {@code null}
		 * zurück. Wenn das gegebene {@link DecodeLabel} {@code null} ist, wird {@code null} zurück gegeben.
		 * 
		 * @see DecodeAdapter#attributeName(int)
		 * @param attributeLabel {@link DecodeLabel}.
		 * @return {@link DecodeValue} zu {@link DecodeLabel#name()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeLabel#name()} ein ungültiger Index ist.
		 */
		public final DecodeValue attributeName(final DecodeLabel attributeLabel) throws IndexOutOfBoundsException {
			if(attributeLabel == null) return null;
			return this.attributeName(attributeLabel.name);
		}

		/**
		 * Diese Methode gibt das {@code index}-te {@link DecodeAttribute} aus {@link DecodeDocument#attributeNodePool()}
		 * zurück.
		 * 
		 * @see DecodeDocument#attributeNodePool()
		 * @see DecodeAttributePool#get(int)
		 * @param attributeNodeIndex Index.
		 * @return {@code index}-tes {@link DecodeAttribute}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeAttribute attributeNode(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.document.attributeNodePool.get(attributeNodeIndex);
		}

		/**
		 * Diese Methode sucht binäre im gegebenen Index-Array des {@link DecodeDocument#attributeNodePool()} nach dem
		 * {@link DecodeAttribute} mit dem gegebenen {@link DecodeAttribute#label()} und gibt dieses oder {@code null}
		 * zurück.
		 * 
		 * @see DecodeAttribute#label()
		 * @see DecodeAttributePool#findLabel(int[], int)
		 * @param attributeNodeIndices Index-Array.
		 * @param attributeLabelIndex Index des {@link DecodeAttribute#label()}.
		 * @return {@link DecodeAttribute} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeAttribute attributeNode(final int[] attributeNodeIndices, final int attributeLabelIndex) throws NullPointerException {
			return this.document.attributeNodePool.findLabel(attributeNodeIndices, attributeLabelIndex);
		}

		/**
		 * Diese Methode sucht binäre im gegebenen Index-Array des {@link DecodeDocument#attributeNodePool()} nach dem
		 * {@link DecodeAttribute} mit dem gegebenen {@link DecodeItem} als {@link DecodeAttribute#label()} und gibt dieses
		 * oder {@code null} zurück.
		 * 
		 * @see DecodeAttribute#label()
		 * @see DecodeAttributePool#findLabel(int[], int)
		 * @param attributeNodeIndices Index-Array.
		 * @param attributeLabelItem {@link DecodeItem} des {@link DecodeAttribute#label()}.
		 * @return {@link DecodeAttribute} oder {@code null}.
		 * @throws NullPointerException Wenn das gegebene Index-Array {@code null} ist.
		 */
		public final DecodeAttribute attributeNode(final int[] attributeNodeIndices, final DecodeItem attributeLabelItem) throws NullPointerException {
			if(attributeLabelItem == null) return null;
			return this.attributeNode(attributeNodeIndices, attributeLabelItem.index);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#attributeLabelPool()}
		 * zurück.
		 * 
		 * @see DecodeDocument#attributeLabelPool()
		 * @see DecodeLabelPool#get(int)
		 * @param attributeLabelIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeLabel attributeLabel(final int attributeLabelIndex) throws IndexOutOfBoundsException {
			return this.document.attributeLabelPool.get(attributeLabelIndex);
		}

		/**
		 * Diese Methode gibt da via {@link DecodeAttribute#label()} referenzierte {@code URI/Name}-{@link DecodeLabel} bzw.
		 * den {@code Name}-{@link DecodeValue} oder {@code null} zurück. Wenn das gegebene {@link DecodeAttribute}
		 * {@code null} ist, wird {@code null} zurück gegeben. Wenn {@link DecodeDocument#isXmlnsEnabled()} {@code false}
		 * ist, wird der Index an {@link DecodeAdapter#attributeName(int)} delegiert. Anderenfalls wird er an
		 * {@link DecodeAdapter#attributeLabel(int)} delegiert.
		 * 
		 * @see DecodeAdapter#attributeName(int)
		 * @see DecodeAdapter#attributeLabel(int)
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeItem} zu {@link DecodeAttribute#label()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeAttribute#value()} ein ungültiger Index ist.
		 */
		public final DecodeItem attributeLabel(final DecodeAttribute attributeNode) {
			if(attributeNode == null) return null;
			if(!this.xmlnsEnabled) return this.attributeName(attributeNode.label);
			return this.attributeLabel(attributeNode.label);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#attributeLabelPool()} nach dem {@link DecodeLabel} mit den
		 * gegebenen Indices der {@code URI}- und {@code Name}-{@link DecodeValue}s und gibt dieses oder {@code null}
		 * zurück.
		 * 
		 * @see DecodeLabel#uri()
		 * @see DecodeLabel#name()
		 * @see DecodeLabelPool#findLabel(int, int)
		 * @param attributeUriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @param attributeNameIndex Index des {@code Name}-{@link DecodeValue}s.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public final DecodeLabel attributeLabel(final int attributeUriIndex, final int attributeNameIndex) {
			return this.document.attributeLabelPool.findLabel(attributeUriIndex, attributeNameIndex);
		}

		/**
		 * Diese Methode sucht binäre nach dem {@code URI/Name}-{@link DecodeLabel} bzw. den {@code Name}-
		 * {@link DecodeValue} mit den gegebenen {@code URI}- und {@code Name}-{@link String}s und gibt dieses oder
		 * {@code null} zurück. Wenn {@link DecodeDocument#isXmlnsEnabled()} {@code false} ist, wird nur nach dem
		 * {@code Name}-{@link DecodeValue} gesucht. Anderenfalls wird nach dme {@code URI/Name}-{@link DecodeLabel}
		 * gesucht.
		 * 
		 * @see DecodeAdapter#attributeUri(String)
		 * @see DecodeAdapter#attributeName(String)
		 * @see DecodeAdapter#attributeLabel(DecodeValue, DecodeValue)
		 * @param attributeUriString {@code URI}.
		 * @param attributeNameString {@code Name}.
		 * @return {@code URI/Name}-{@link DecodeLabel} bzw. des {@code Name}-{@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link String}s {@code null} ist.
		 */
		public final DecodeItem attributeLabel(final String attributeUriString, final String attributeNameString) throws NullPointerException {
			final DecodeValue attributeName = this.attributeName(attributeNameString);
			if(!this.xmlnsEnabled || (attributeName == null)) return attributeName;
			return this.attributeLabel(this.attributeUri(attributeUriString), attributeName);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#attributeLabelPool()} nach dem {@link DecodeLabel} mit
		 * Referenzen auf die gegebenen {@code URI}- und {@code Name}-{@link DecodeValue}s und gibt dieses oder {@code null}
		 * zurück. Wenn einer der gegebenen {@link DecodeValue}s {@code null} ist, wird {@code null} zurück gegeben
		 * 
		 * @see DecodeLabel#uri()
		 * @see DecodeLabel#name()
		 * @see DecodeAdapter#attributeLabel(int, int)
		 * @param attributeUri {@code URI}-{@link DecodeValue}.
		 * @param attributeName {@code Name}-{@link DecodeValue}.
		 * @return {@link DecodeLabel} oder {@code null}.
		 */
		public final DecodeLabel attributeLabel(final DecodeValue attributeUri, final DecodeValue attributeName) {
			if((attributeUri == null) || (attributeName == null)) return null;
			return this.attributeLabel(attributeUri.index, attributeName.index);
		}

		/**
		 * Diese Methode gibt den {@code index}-ten {@link DecodeValue} aus {@link DecodeDocument#valuePool()} zurück.
		 * 
		 * @see DecodeAdapter#value(int)
		 * @param attributeValueIndex Index.
		 * @return {@code index}-ter {@link DecodeValue}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final DecodeValue attributeValue(final int attributeValueIndex) throws IndexOutOfBoundsException {
			return this.value(attributeValueIndex);
		}

		/**
		 * Diese Methode sucht binäre im {@link DecodeDocument#valuePool()} nach dem {@link DecodeValue} mit dem gegebenen
		 * {@link String} und gibt diesen oder {@code null} zurück.
		 * 
		 * @see DecodeAdapter#value(String)
		 * @param attributeValueString {@link String}.
		 * @return {@link DecodeValue} oder {@code null}.
		 * @throws NullPointerException Wenn der gegebene {@link String} {@code null} ist.
		 */
		public final DecodeValue attributeValue(final String attributeValueString) {
			return this.value(attributeValueString);
		}

		/**
		 * Diese Methode gibt den via {@link DecodeAttribute#value()} referenzierten {@link DecodeValue} oder {@code null}
		 * zurück. Wenn das gegebene {@link DecodeAttribute} {@code null} ist, wird {@code null} zurück gegeben.
		 * 
		 * @see DecodeAdapter#attributeValue(int)
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link DecodeValue} zu {@link DecodeAttribute#value()} oder {@code null}.
		 * @throws IndexOutOfBoundsException Wenn {@link DecodeAttribute#value()} ein ungültiger Index ist.
		 */
		public final DecodeValue attributeValue(final DecodeAttribute attributeNode) throws IndexOutOfBoundsException {
			if(attributeNode == null) return null;
			return this.attributeValue(attributeNode.value);
		}

		/**
		 * Diese Methode gibt das {@link DecodeDocument} zurück.
		 * 
		 * @return {@link DecodeDocument}.
		 */
		public final DecodeDocument documentNode() {
			return this.document;
		}

		/**
		 * Diese Methode implementiert {@link Node#getOwnerDocument()} und gibt den {@link DecodeDocumentAdapter} zurück.
		 * 
		 * @return {@link Node#getOwnerDocument()}.
		 */
		public final DecodeDocumentAdapter getNodeOwnerDocument() {
			return this.documentAdapter;
		}

		/**
		 * Diese Methode implementiert {@link Text#getNodeValue()}.
		 * 
		 * @param index Index des {@link DecodeValue}s.
		 * @return {@link Text#getNodeValue()}.
		 */
		public final String getTextNodeValue(final int index) {
			return this.textValue(index).value;
		}

		/**
		 * Diese Methode implementiert {@link Element#hasAttributeNS(String, String)}.
		 * 
		 * @see DecodeAdapter#attributeLabel(String, String)
		 * @param parentNodeIndex Index des {@link DecodeElement}s.
		 * @param attributeUriString {@code URI} ({@link Attr#getNamespaceURI()}).
		 * @param attributeNameString {@code Name} ({@link Attr#getLocalName()}).
		 * @return {@link Element#hasAttributeNS(String, String)}.
		 */
		public final boolean hasAttribute(final int parentIndex, final String attributeUriString, final String attributeNameString) {
			return this.hasAttribute(this.elementAttributes(this.elementNode(parentIndex)).values, attributeUriString, attributeNameString);
		}

		public final boolean hasAttribute(final int[] attributeNodeIndices, final String attributeUriString, final String attributeNameString) {
			return (attributeNodeIndices.length != 0)
				&& (this.attributeNode(attributeNodeIndices, this.attributeLabel(attributeUriString, attributeNameString)) != null);
		}

		public final DecodeAttributeAdapter getAttributeNode(final DecodeElementAdapter parentAdapter, final String attributeUriString,
			final String attributeNameString) {
			return this
				.getAttributeNode(parentAdapter, this.elementAttributes(this.elementNode(parentAdapter.index)).values, attributeUriString, attributeNameString);
		}

		public final DecodeAttributeAdapter getAttributeNode(final DecodeElementAdapter parentAdapter, final int[] attributeNodeIndices,
			final String attributeUriString, final String attributeNameString) {
			if(attributeNodeIndices.length == 0) return null;
			final DecodeAttribute attributeNode = this.attributeNode(attributeNodeIndices, this.attributeLabel(attributeUriString, attributeNameString));
			if(attributeNode == null) return null;
			return new DecodeAttributeAdapter(parentAdapter, attributeNode.index);
		}

		static int A;

		public final String getAttributePrefix(final int parentIndex, final int attributeNodeIndex) {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(this.elementNode(parentIndex), this.attributeNode(attributeNodeIndex));
		}

		public final String getAttributePrefix(final int parentIndex, final DecodeAttribute attributeNode) {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(this.elementNode(parentIndex), attributeNode);
		}

		public final String getAttributePrefix(final DecodeElement parentNode, final int attributeNodeIndex) {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(parentNode, this.attributeNode(attributeNodeIndex));
		}

		public final String getAttributePrefix(final DecodeElement parentNode, final DecodeAttribute attributeNode) {
			if(!this.xmlnsEnabled) return null;
			return this.getAttributePrefix_(parentNode, attributeNode);
		}

		private final String getAttributePrefix_(final DecodeElement parentNode, final DecodeAttribute attributeNode) {
			return this._lookupPrefix(parentNode, this.attributeLabel(attributeNode.label).uri);
		}

		/**
		 * Diese Methode implementiert {@link Attr#getLocalName()}.
		 * 
		 * @see DecodeAdapter#attributeNode(int)
		 * @see DecodeAdapter#getAttributeLocalName(DecodeAttribute)
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getLocalName()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeLocalName(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.getAttributeLocalName(this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Attr#getLocalName()}.
		 * 
		 * @see DecodeAdapter#string(DecodeValue)
		 * @see DecodeAdapter#attributeName(DecodeLabel)
		 * @see DecodeAdapter#attributeLabel(int)
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link Attr#getLocalName()}.
		 */
		public final String getAttributeLocalName(final DecodeAttribute attributeNode) {
			if(this.xmlnsEnabled) return this.string(this.attributeName(this.attributeLabel(attributeNode.label)));
			return this.string(this.attributeName(attributeNode.label));
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNamespaceURI()}.
		 * 
		 * @see DecodeAdapter#attributeNode(int)
		 * @see DecodeAdapter#getAttributeNamespaceURI(DecodeAttribute)
		 * @param attributeNodeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getNamespaceURI()}.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final String getAttributeNamespaceURI(final int attributeNodeIndex) throws IndexOutOfBoundsException {
			return this.getAttributeNamespaceURI(this.attributeNode(attributeNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNamespaceURI()}.
		 * 
		 * @see DecodeAdapter#string(DecodeValue)
		 * @see DecodeAdapter#attributeUri(DecodeLabel)
		 * @see DecodeAdapter#attributeLabel(int)
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link Attr#getNamespaceURI()}.
		 */
		public final String getAttributeNamespaceURI(final DecodeAttribute attributeNode) {
			if(!this.xmlnsEnabled) return null;
			final String value = this.string(this.attributeUri(this.attributeLabel(attributeNode.label)));
			if(value.isEmpty()) return null;
			return value;
		}

		public final String getAttributeNodeName(final int parentIndex, final int attributeNodeIndex) {
			return this.getAttributeNodeName(parentIndex, this.attributeNode(attributeNodeIndex));
		}

		public final String getAttributeNodeName(final int parentIndex, final DecodeAttribute attributeNode) {
			if(!this.xmlnsEnabled) return this.getAttributeNodeName_(attributeNode);
			return this.getAttributeNodeName_(this.elementNode(parentIndex), attributeNode);
		}

		public final String getAttributeNodeName(final DecodeElement parentNode, final int attributeNodeIndex) {
			return this.getAttributeNodeName(parentNode, this.attributeNode(attributeNodeIndex));
		}

		public final String getAttributeNodeName(final DecodeElement parentNode, final DecodeAttribute attributeNode) {
			if(!this.xmlnsEnabled) return this.getAttributeNodeName_(attributeNode);
			return this.getAttributeNodeName_(parentNode, attributeNode);
		}

		private final String getAttributeNodeName_(final DecodeAttribute attributeNode) {
			return this.string(this.attributeName(attributeNode.label));
		}

		private final String getAttributeNodeName_(final DecodeElement parentNode, final DecodeAttribute attributeNode) {
			final DecodeLabel attributeLabel = this.attributeLabel(attributeNode.label);
			final String xmlnsName = this._lookupPrefix(parentNode, attributeLabel.uri);
			final String attributeName = this.string(this.attributeName(attributeLabel.name));
			if(xmlnsName == null) return attributeName;
			return xmlnsName + ":" + attributeName;
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNodeValue()}.
		 * 
		 * @see DecodeAdapter#attributeNode(int)
		 * @see DecodeAdapter#getAttributeNodeValue(DecodeAttribute)
		 * @param attributeIndex Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getNodeValue()}.
		 */
		public final String getAttributeNodeValue(final int attributeIndex) {
			return this.getAttributeNodeValue(this.attributeNode(attributeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNodeValue()}.
		 * 
		 * @see DecodeAdapter#string(DecodeValue)
		 * @see DecodeAdapter#attributeValue(int)
		 * @param attributeNode {@link DecodeAttribute}.
		 * @return {@link Attr#getNodeValue()}.
		 */
		public final String getAttributeNodeValue(final DecodeAttribute attributeNode) {
			final DecodeValue attributeValue = this.attributeValue(attributeNode);
			if(attributeValue == null) return "";
			return attributeValue.value;
		}

		public final String getAttributeNodeValue(final int parentIndex, final int attributeLabelIndex) {
			return this.getAttributeNodeValue(this.elementNode(parentIndex), attributeLabelIndex);
		}

		public final String getAttributeNodeValue(final int parentIndex, final DecodeItem attributeLabelItem) {
			return this.getAttributeNodeValue(this.elementNode(parentIndex), attributeLabelItem);
		}

		public final String getAttributeNodeValue(final int parentIndex, final String attributeUriString, final String attributeNameString) {
			return this.getAttributeNodeValue(this.elementNode(parentIndex), attributeUriString, attributeNameString);
		}

		public final String getAttributeNodeValue(final int[] attributeNodeIndices, final int attributeLabelIndex) {
			return this.getAttributeNodeValue(this.attributeNode(attributeNodeIndices, attributeLabelIndex));
		}

		public final String getAttributeNodeValue(final int[] attributeNodeIndices, final DecodeItem attributeLabelItem) {
			return this.getAttributeNodeValue(this.attributeNode(attributeNodeIndices, attributeLabelItem));
		}

		public final String getAttributeNodeValue(final int[] attributeNodeIndices, final String attributeUriString, final String attributeNameString) {
			if(attributeNodeIndices.length == 0) return "";
			return this.getAttributeNodeValue(this.attributeNode(attributeNodeIndices, this.attributeLabel(attributeUriString, attributeNameString)));
		}

		public final String getAttributeNodeValue(final DecodeElement parentNode, final int attributeLabelIndex) {
			return this.getAttributeNodeValue(this.elementAttributes(parentNode).values, attributeLabelIndex);
		}

		public final String getAttributeNodeValue(final DecodeElement parentNode, final DecodeItem attributeLabelItem) {
			return this.getAttributeNodeValue(this.elementAttributes(parentNode).values, attributeLabelItem);
		}

		public final String getAttributeNodeValue(final DecodeElement parentNode, final String attributeUri, final String attributeName) {
			return this.getAttributeNodeValue(this.elementAttributes(parentNode).values, attributeUri, attributeName);
		}

		/**
		 * Diese Methode implementiert {@link Element#hasChildNodes()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link Element#hasChildNodes()}.
		 */
		public final boolean hasChildren(final int elementNodeIndex) {
			return this.hasChildren(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Element#hasChildNodes()}.
		 * 
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link Element#hasChildNodes()}.
		 */
		public final boolean hasChildren(final DecodeElement elementNode) {
			return this.elementChildren(elementNode).values.length != 0;
		}

		/**
		 * Diese Methode implementiert {@link Element#getChildNodes()}.
		 * 
		 * @param elementAdapter {@link DecodeElementAdapter}.
		 * @return {@link Element#getChildNodes()}.
		 */
		public final DecodeElementChildrenAdapter getChildren(final DecodeElementAdapter elementAdapter) {
			return new DecodeElementChildrenAdapter(elementAdapter, this.elementChildren(this.elementNode(elementAdapter.index)).values);
		}

		/**
		 * Diese Methode implementiert {@link Element#hasAttributes()}.
		 * 
		 * @see DecodeAdapter#elementNode(int)
		 * @see DecodeAdapter#elementAttributes(DecodeElement)
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link Element#hasAttributes()}.
		 */
		public final boolean hasAttributes(final int elementNodeIndex) {
			return this.hasAttributes(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Element#hasAttributes()}.
		 * 
		 * @see DecodeAdapter#elementNode(int)
		 * @see DecodeAdapter#elementAttributes(DecodeElement)
		 * @param elementNode Index des {@link DecodeElement}s.
		 * @return {@link Element#hasAttributes()}.
		 */
		public final boolean hasAttributes(final DecodeElement elementNode) {
			return this.elementAttributes(elementNode).values.length != 0;
		}

		/**
		 * Diese Methode implementiert {@link Element#getAttributes()}.
		 * 
		 * @param elementAdapter {@link DecodeElementAdapter}.
		 * @return {@link Element#getAttributes()}.
		 */
		public final DecodeElementAttributesAdapter getAttributes(final DecodeElementAdapter elementAdapter) {
			return new DecodeElementAttributesAdapter(elementAdapter, this.elementAttributes(this.elementNode(elementAdapter.index)).values);
		}

		static int Z;

		/**
		 * Diese Methode implementiert {@link Element#getPrefix()}.
		 * 
		 * @see DecodeAdapter#elementNode(int)
		 * @see DecodeAdapter#getElementPrefix(DecodeElement)
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link Element#getPrefix()}.
		 */
		public final String getElementPrefix(final int elementNodeIndex) {
			return this.getElementPrefix(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Element#getPrefix()}.
		 * 
		 * @see DecodeAdapter#elementLabel(int)
		 * @see DecodeAdapter#lookupPrefix(DecodeElement, int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link Element#getPrefix()}.
		 */
		public final String getElementPrefix(final DecodeElement elementNode) {
			if(!this.xmlnsEnabled) return null;
			return this._lookupPrefix(elementNode, this.elementLabel(elementNode.label).uri);
		}

		/**
		 * Diese Methode implementiert {@link Element#getLocalName()}.
		 * 
		 * @see DecodeAdapter#elementNode(int)
		 * @see DecodeAdapter#getElementLocalName(DecodeElement)
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link Element#getLocalName()}.
		 */
		public final String getElementLocalName(final int elementNodeIndex) {
			return this.getElementLocalName(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Element#getLocalName()}.
		 * 
		 * @see DecodeAdapter#string(DecodeValue)
		 * @see DecodeAdapter#elementName(DecodeLabel)
		 * @see DecodeAdapter#elementLabel(int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link Element#getLocalName()}.
		 */
		public final String getElementLocalName(final DecodeElement elementNode) {
			if(this.xmlnsEnabled) return this.string(this.elementName(this.elementLabel(elementNode.label)));
			return this.string(this.elementName(elementNode.label));
		}

		/**
		 * Diese Methode implementiert {@link Element#getNamespaceURI()}.
		 * 
		 * @see DecodeAdapter#elementNode(int)
		 * @see DecodeAdapter#getElementNamespaceURI(DecodeElement)
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link Element#getNamespaceURI()}.
		 */
		public final String getElementNamespaceURI(final int elementNodeIndex) {
			return this.getElementNamespaceURI(this.elementNode(elementNodeIndex));
		}

		/**
		 * Diese Methode implementiert {@link Element#getNamespaceURI()}.
		 * 
		 * @see DecodeAdapter#string(DecodeValue)
		 * @see DecodeAdapter#elementUri(DecodeLabel)
		 * @see DecodeAdapter#elementLabel(int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link Element#getNamespaceURI()}.
		 */
		public final String getElementNamespaceURI(final DecodeElement elementNode) {
			if(!this.xmlnsEnabled) return null;
			final String value = this.string(this.elementUri(this.elementLabel(elementNode.label)));
			if(value.isEmpty()) return null;
			return value;
		}

		/**
		 * Diese Methode implementiert {@link Element#getNodeName()}.
		 * 
		 * @see DecodeAdapter#elementNode(int)
		 * @see DecodeAdapter#getElementNodeName(DecodeElement)
		 * @param index Index des {@link DecodeElement}s.
		 * @return {@link Element#getNodeName()}.
		 */
		public final String getElementNodeName(final int index) {
			return this.getElementNodeName(this.elementNode(index));
		}

		/**
		 * Diese Methode implementiert {@link Element#getNodeName()}.
		 * 
		 * @see DecodeAdapter#elementName(int)
		 * @see DecodeAdapter#elementLabel(int)
		 * @see DecodeAdapter#lookupPrefix(DecodeElement, int)
		 * @param elementNode {@link DecodeElement}.
		 * @return {@link Element#getNodeName()}.
		 */
		public final String getElementNodeName(final DecodeElement elementNode) {
			if(!this.xmlnsEnabled) return this.string(this.elementName(elementNode.label));
			final DecodeLabel elementLabel = this.elementLabel(elementNode.label);
			final String xmlnsName = this._lookupPrefix(elementNode, elementLabel.uri);
			final String elementName = this.string(this.elementName(elementLabel.name));
			if(xmlnsName == null) return elementName;
			return xmlnsName + ":" + elementName;
		}

		/**
		 * Diese Methode implementiert {@link Element#getTextContent()}.
		 * 
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @return {@link Element#getTextContent()}.
		 */
		public final String getElementTextContent(final int elementNodeIndex) {
			final StringBuffer buffer = new StringBuffer();
			this.getElementTextContent(elementNodeIndex, buffer);
			return buffer.toString();
		}

		/**
		 * Diese Methode implementiert {@link Element#getTextContent()}.
		 * 
		 * @param buffer {@link Element#getTextContent()}.
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 */
		public final void getElementTextContent(final int elementNodeIndex, final StringBuffer buffer) {
			final DecodeGroup elementChildren = this.elementChildren(this.elementNode(elementNodeIndex));
			for(final int childIndex: elementChildren.values){
				final int index = childIndex - this.offset;
				if(index < 0){
					buffer.append(this.textValue(childIndex).value);
				}else{
					this.getElementTextContent(index, buffer);
				}
			}
		}

		/**
		 * Diese Methode implementiert {@link NodeList#item(int)} für {@link Element#getChildNodes()}.
		 * 
		 * @see DecodeAdapter#offset()
		 * @param parentAdapter {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param nodeIndex Index des {@link DecodeValue}s bzw. des {@link DecodeElement}s plus
		 *        {@link DecodeAdapter#offset()}.
		 * @param childIndex Index des {@code Child}-{@link Node}s in {@link Node#getChildNodes()}.
		 * @return {@code Child}-{@link Node} als {@link DecodeTextAdapter} bzw. {@link DecodeElementAdapter}.
		 */
		public final DecodeChildAdapter getElementChild(final DecodeNodeAdapter parentAdapter, final int nodeIndex, final int childIndex) {
			if(nodeIndex < this.offset) return new DecodeTextAdapter(parentAdapter, nodeIndex, childIndex);
			return new DecodeElementAdapter(parentAdapter, nodeIndex - this.offset, childIndex);
		}

		public DecodeElementAdapter getElementChildNode(final DecodeElementAdapter parentAdapter, final String uri, final String name, final int child) {
			if(child < 0) return null;
			return this.getElementChildNode(parentAdapter, this.elementChildren(this.elementNode(parentAdapter.index)).values, uri, name, child);
		}

		public DecodeElementAdapter getElementChildNode(final DecodeElementAdapter parent, final int[] values, final String uri, final String name, final int child) {
			final int child2 = this.elementChildIndex(values, uri, name, child);
			if(child2 < 0) return null;
			return new DecodeElementAdapter(parent, values[child2] - this.offset, child2);
		}

		public final String getElementChildContent(final int elementNodeIndex, final int childLabelIndex, final int childIndex) {
			return this.getElementChildContent(this.elementNode(elementNodeIndex), childLabelIndex, childIndex);
		}

		public final String getElementChildContent(final int elementNodeIndex, final DecodeItem childLabelItem, final int childIndex) {
			if(childLabelItem == null) return "";
			return this.getElementChildContent(elementNodeIndex, childLabelItem.index, childIndex);
		}

		public final String getElementChildContent(final int elementNodeIndex, final String childUriString, final String childNameString, final int childIndex) {
			return this.getElementChildContent(this.elementNode(elementNodeIndex), childUriString, childNameString, childIndex);
		}

		public final String getElementChildContent(final int[] childNodeIndices, final int childLabelIndex, final int childIndex) {
			return this.getElementChildContent_(childNodeIndices, this.elementChildIndex(childNodeIndices, childLabelIndex, childIndex));
		}

		public final String getElementChildContent(final int[] childNodeIndices, final DecodeItem childLabelItem, final int childIndex) {
			return this.getElementChildContent_(childNodeIndices, this.elementChildIndex(childNodeIndices, childLabelItem, childIndex));
		}

		public String getElementChildContent(final int[] childNodeIndices, final String childUriString, final String childNameString, final int childIndex) {
			return this.getElementChildContent_(childNodeIndices, this.elementChildIndex(childNodeIndices, childUriString, childNameString, childIndex));
		}

		public final String getElementChildContent(final DecodeElement elementNode, final int childLabelIndex, final int childIndex) {
			return this.getElementChildContent(this.elementChildren(elementNode).values, childLabelIndex, childIndex);
		}

		public final String getElementChildContent(final DecodeElement elementNode, final DecodeItem childLabelItem, final int childIndex) {
			if(childLabelItem == null) return "";
			return this.getElementChildContent(elementNode, childLabelItem.index, childIndex);
		}

		public String getElementChildContent(final DecodeElement elementNode, final String childUriString, final String childNameString, final int childIndex) {
			return this.getElementChildContent(this.elementChildren(elementNode).values, childUriString, childNameString, childIndex);
		}

		private final String getElementChildContent_(final int[] childNodeIndices, final int childIndex) {
			if(childIndex < 0) return "";
			return this.getElementTextContent(childNodeIndices[childIndex] - this.offset);
		}

		/**
		 * Diese Methode implementiert {@link Element#getFirstChild()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#getFirstChild()}.
		 */
		public DecodeChildAdapter getElementFirstChild(final DecodeElementAdapter parent) {
			final int[] indices = this.elementChildren(this.elementNode(parent.index)).values;
			if(indices.length == 0) return null;
			return this.getElementChild(parent, indices[0], 0);
		}

		/**
		 * Diese Methode implementiert {@link Element#getLastChild()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#getLastChild()}.
		 */
		public DecodeChildAdapter getElementLastChild(final DecodeElementAdapter parent) {
			final int[] indices = this.elementChildren(this.elementNode(parent.index)).values;
			final int index = indices.length - 1;
			if(index < 0) return null;
			return this.getElementChild(parent, indices[index], index);
		}

		/**
		 * Diese Methode implementiert {@link Element#getElementsByTagNameNS(String, String)} bzw.
		 * {@link Document#getElementsByTagNameNS(String, String)}.
		 * 
		 * @see DecodeCollector#MODE_CHILDREN
		 * @see DecodeCollector#MODE_DESCENDANT
		 * @see DecodeCollector#MODE_DESCENDANT_SELF
		 * @see DecodeCollector#collect(DecodeElementAdapter, int)
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}
		 * @param uri {@code Name} ({@link Element#getNamespaceURI()}).
		 * @param name {@code Name} ({@link Element#getLocalName()}).
		 * @param mode Modus der Suche.
		 * @return {@link Element#getElementsByTagNameNS(String, String)} bzw.
		 *         {@link Document#getElementsByTagNameNS(String, String)}.
		 */
		public ElementList elementGetElementsByTagName(final DecodeElementAdapter parent, final String uri, final String name, final int mode) {
			final DecodeCollector collector;
			if(this.xmlnsEnabled){
				if("*".equals(uri)){
					if("*".equals(name)){
						collector = new DecodeCollector(this);
					}else{
						final DecodeValue elementName = this.document.elementNamePool.findValue(name);
						if(elementName == null) return DecodeAdapter.VOID_NODE_LIST;
						collector = new DecodeNameCollector(this, elementName.index);
					}
				}else{
					final DecodeValue elementUri = this.document.uriPool.findValue(uri);
					if(elementUri == null) return DecodeAdapter.VOID_NODE_LIST;
					if("*".equals(name)){
						collector = new DecodeUriCollector(this, elementUri.index);
					}else{
						final DecodeValue elementName = this.document.elementNamePool.findValue(name);
						if(elementName == null) return DecodeAdapter.VOID_NODE_LIST;
						final DecodeLabel elementlLabel = this.document.elementLabelPool.findLabel(elementUri.index, elementName.index);
						if(elementlLabel == null) return DecodeAdapter.VOID_NODE_LIST;
						collector = new DecodeLabelCollector(this, elementlLabel.index);
					}
				}
			}else{
				if("*".equals(name)){
					collector = new DecodeCollector(this);
				}else{
					final DecodeValue elementName = this.document.elementNamePool.findValue(name);
					if(elementName == null) return DecodeAdapter.VOID_NODE_LIST;
					collector = new DecodeLabelCollector(this, elementName.index);
				}
			}
			collector.collect(parent, mode);
			return collector;
		}

		private String _lookupPrefix(final int[] xmlnsIndices, final int uriIndex) {
			final DecodeLabel xmlnsLabel = this.document.xmlnsLabelPool.findUri(xmlnsIndices, uriIndex);
			if(xmlnsLabel == null) return null;
			final DecodeValue xmlnsName = this.xmlnsName(xmlnsLabel.name);
			if(xmlnsName == null) return null;
			final String value = xmlnsName.value;
			if(value.isEmpty()) return null;
			return value;
		}

		private String _lookupPrefix(final DecodeElement elementNode, final int uriIndex) {
			final DecodeGroup elementXmlns = this.elementXmlns(elementNode);
			return this._lookupPrefix(elementXmlns.values, uriIndex);
		}

		/**
		 * @param elementNodeIndex Index des {@link DecodeElement}s.
		 * @param uriIndex Index des {@code URI}-{@link DecodeValue}s.
		 * @return
		 */
		public String lookupPrefix(final int elementNodeIndex, final int uriIndex) {
			if(!this.xmlnsEnabled) return null;
			return this._lookupPrefix(this.elementNode(elementNodeIndex), uriIndex);
		}

		public String lookupPrefix(final int elementNodeIndex, final String uriString) {
			if(!this.xmlnsEnabled) return null;
			final DecodeValue uriValue = this.uri(uriString);
			if(uriValue == null) return null;
			return this._lookupPrefix(this.elementNode(elementNodeIndex), uriValue.index);
		}

		public String lookupPrefix(final int elementNodeIndex, final DecodeValue uriValue) {
			if(!this.xmlnsEnabled || (uriValue == null)) return null;
			return this._lookupPrefix(this.elementNode(elementNodeIndex), uriValue.index);
		}

		public String lookupPrefix(final DecodeElement elementNode, final int uriIndex) {
			if(!this.xmlnsEnabled) return null;
			return this._lookupPrefix(elementNode, uriIndex);
		}

		public String lookupPrefix(final DecodeElement elementNode, final String uriString) {
			if(!this.xmlnsEnabled) return null;
			final DecodeValue uriValue = this.uri(uriString);
			if(uriValue == null) return null;
			return this._lookupPrefix(elementNode, uriValue.index);
		}

		/**
		 * Diese Methode implementiert {@link Element#lookupNamespaceURI(String)} und gibt die {@code URI} zum gegebenen
		 * {@code Prefix} oder {@code null} zurück.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param name {@code Prefix}.
		 * @return {@code URI} oder {@code null}.
		 */
		public String lookupNamespaceURI(final int index, final String name) {
			if(!this.xmlnsEnabled) return null;
			final DecodeValue xmlnsName = this.xmlnsName(name);
			if(xmlnsName == null) return null;
			final DecodeLabel xmlnsLabel = this.document.xmlnsLabelPool.findName(this.elementXmlns(this.elementNode(index)).values, xmlnsName.index);
			if(xmlnsLabel == null) return null;
			return this.string(this.uri(xmlnsLabel.uri));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeAdapter", this.document);
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
			return this.adapter().getNodeOwnerDocument();
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
	 * Diese Klasse implementiert ein {@link Text} als {@link DecodeChildAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeTextAdapter extends DecodeChildAdapter implements Text {

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeValue}s und
		 * {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeValue}s.
		 * @param child {@code Child-Index}.
		 */
		public DecodeTextAdapter(final DecodeNodeAdapter parent, final int index, final int child) {
			super(parent, index, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeTextAdapter asText() {
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
			if(!(object instanceof DecodeTextAdapter)) return false;
			final DecodeTextAdapter data = (DecodeTextAdapter)object;
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
			if(!(object instanceof DecodeTextAdapter)) return false;
			final DecodeTextAdapter data = (DecodeTextAdapter)object;
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
	 * Diese Klasse implementiert einen {@link DecodeNodeAdapter} mit {@code Parent}-{@link DecodeNodeAdapter} und
	 * {@code Child-Index} als Basis von {@link DecodeTextAdapter} und {@link DecodeElementAdapter}.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeChildAdapter extends DecodeNodeAdapter {

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
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeItem}s und
		 * {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeItem}s.
		 * @param child {@code Child-Index}.
		 */
		public DecodeChildAdapter(final DecodeNodeAdapter parent, final int index, final int child) {
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
		 * Diese Methode gibt den Index dieses {@link DecodeChildAdapter}s in den {@link Node#getChildNodes()} des
		 * {@link Node#getParentNode()}s zurück.
		 * 
		 * @return Index dieses {@link DecodeChildAdapter}s im {@link Node#getParentNode()}.
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
		 * Diese Methode gibt nur dann {@code this} zurück, wenn dieses Objekt ein {@link DecodeTextAdapter} ist.
		 * 
		 * @return {@code this} oder {@code null}.
		 */
		public DecodeTextAdapter asText() {
			return null;
		}

		/**
		 * Diese Methode gibt nur dann {@code this} zurück, wenn dieses Objekt ein {@link DecodeElementAdapter} ist.
		 * 
		 * @return {@code this} oder {@code null}.
		 */
		public DecodeElementAdapter asElement() {
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
		public DecodeChildAdapter getPreviousSibling() {
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
		public DecodeChildAdapter getNextSibling() {
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
	 * Diese Klasse implementiert ein {@link Element} als {@link DecodeChildAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementAdapter extends DecodeChildAdapter implements Element {

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeElement}s und
		 * {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeElement}s.
		 * @param child {@code Child-Index}.
		 */
		public DecodeElementAdapter(final DecodeNodeAdapter parent, final int index, final int child) {
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
		public DecodeElementAdapter asElement() {
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
		public DecodeChildAdapter getFirstChild() {
			return this.adapter().getElementFirstChild(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildAdapter getLastChild() {
			return this.adapter().getElementLastChild(this);
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@code Child}-{@link Element} mit dem gegebenen {@code Name} oder
		 * {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param name {@code Name}.
		 * @param child {@code Child}-Index als Beginn der linearen Suche.
		 * @return erstes gefundenes {@code Child}-{@link Element} oder {@code null}.
		 */
		public DecodeElementAdapter getChildNode(final String name, final int child) {
			return this.getChildNodeNS(XMLConstants.NULL_NS_URI, name, child);
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@code Child}-{@link Element} mit der gegebenen {@code URI} und dem
		 * gegebenen {@code Name} oder {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
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
		public DecodeElementAdapter getChildNodeNS(final String uri, final String name, final int child) {
			return this.adapter().getElementChildNode(this, uri, name, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasChildNodes() {
			return this.adapter().hasChildren(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementChildrenAdapter getChildNodes() {
			return this.adapter().getChildren(this);
		}

		/**
		 * Diese Methode gibt den Textwert des ersten gefundenen {@code Child}-{@link Element}s mit dem gegebenen
		 * {@code Name} oder {@code ""} zurück. Die lineare Suche nach dem {@link Element} beginnt ab der gegebenen
		 * Position.
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
		 * Diese Methode gibt den Textwert des ersten gefundenen {@code Child}-{@link Element}s mit der gegebenen
		 * {@code URI} und dem gegebenen {@code Name} oder {@code ""} zurück. Die lineare Suche nach dem {@link Element}
		 * beginnt ab der gegebenen Position.
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
			return this.adapter().getElementChildContent(this.index, uri, name, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementAttributesAdapter getAttributes() {
			return this.adapter().getAttributes(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributes() {
			return this.adapter().hasAttributes(this.index);
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
			return this.adapter().getAttributeNodeValue(this.index, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributeNS(final String uri, final String name) throws DOMException {
			return this.adapter().hasAttribute(this.index, uri, name);
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
		public DecodeAttributeAdapter getAttributeNode(final String name) {
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
		public DecodeAttributeAdapter getAttributeNodeNS(final String uri, final String name) throws DOMException {
			return this.adapter().getAttributeNode(this, uri, name);
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
			return this.adapter().getElementTextContent(this.index);
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
			return uri.equals(this.lookupNamespaceURI(XMLConstants.NULL_NS_URI));
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
			return this.adapter().elementGetElementsByTagName(this, uri, name, DecodeCollector.MODE_DESCENDANT);
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
			if(!(object instanceof DecodeElementAdapter)) return false;
			final DecodeElementAdapter data = (DecodeElementAdapter)object;
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
			if(!(object instanceof DecodeElementAdapter)) return false;
			final DecodeElementAdapter data = (DecodeElementAdapter)object;
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
	 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getChildNodes()}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementChildrenAdapter implements ChildList, Iterable<DecodeChildAdapter> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementAdapter}.
		 */
		protected final DecodeElementAdapter parent;

		/**
		 * Dieses Feld speichert die Indices der {@link DecodeValue}s und {@link DecodeElement}s.
		 * 
		 * @see DecodeAdapter#offset
		 */
		protected final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeElementAdapter} und Indices der {@link DecodeValue}
		 * s und {@link DecodeElement}s.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param indices Indices der {@link DecodeValue}s und {@link DecodeElement}s.
		 */
		public DecodeElementChildrenAdapter(final DecodeElementAdapter parent, final int[] indices) {
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
		 * Diese Methode gibt den {@code Parent}-{@link DecodeElementAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeElementAdapter}.
		 */
		public DecodeElementAdapter getParentAdapter() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeChildAdapter item(final int index) {
			if(index < 0) return null;
			final int[] indices = this.indices;
			if(index >= indices.length) return null;
			return this.parent.adapter().getElementChild(this.parent, indices[index], index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return this.indices.length;
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@link Element} mit dem gegebenen {@code Name} oder {@code null} zurück.
		 * Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getNodeName()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param name {@code Name}.
		 * @param child Index als Beginn der linearen Suche.
		 * @return erstes gefundenes {@link Element} oder {@code null}.
		 */
		public DecodeElementAdapter getNamedItem(final String name, final int child) {
			return this.getNamedItemNS(XMLConstants.NULL_NS_URI, name, child);
		}

		/**
		 * Diese Methode gibt das erste gefundenen {@link Element} mit der gegebenen {@code URI} und dem gegebenen
		 * {@code Name} oder {@code null} zurück. Die lineare Suche beginnt ab der gegebenen Position.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 * @see Element#getChildNodes()
		 * @see Element#getTextContent()
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @param child Index als Beginn der linearen Suche.
		 * @return erstes gefundenes {@link Element} oder {@code null}.
		 */
		public DecodeElementAdapter getNamedItemNS(final String uri, final String name, final int child) {
			return this.parent.adapter().getElementChildNode(this.parent, this.indices, uri, name, child);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<DecodeChildAdapter> iterator() {
			return new Iterator<DecodeChildAdapter>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < DecodeElementChildrenAdapter.this.indices.length;
				}

				@Override
				public DecodeChildAdapter next() {
					return DecodeElementChildrenAdapter.this.item(this.index++);
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
	 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getAttributes()}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeElementAttributesAdapter implements NamedNodeMap, Iterable<DecodeAttributeAdapter> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementAdapter}.
		 */
		protected final DecodeElementAdapter parent;

		/**
		 * Dieses Feld speichert die Indices der {@link DecodeAttribute}s.
		 */
		protected final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeElementAdapter} und Indices der
		 * {@link DecodeAttribute}s.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param indices Indices der {@link DecodeAttribute}s.
		 */
		public DecodeElementAttributesAdapter(final DecodeElementAdapter parent, final int[] indices) {
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
		 * Diese Methode gibt den {@code Parent}-{@link DecodeElementAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeElementAdapter}.
		 */
		public DecodeElementAdapter getParentAdapter() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeAttributeAdapter item(final int index) {
			if(index < 0) return null;
			final int[] indices = this.indices;
			if(index >= indices.length) return null;
			return new DecodeAttributeAdapter(this.parent, indices[index]);
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
		public DecodeAttributeAdapter getNamedItem(final String name) {
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
		public DecodeAttributeAdapter getNamedItemNS(final String uri, final String name) throws DOMException {
			return this.parent.adapter().getAttributeNode(this.parent, this.indices, uri, name);
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
		public Iterator<DecodeAttributeAdapter> iterator() {
			return new Iterator<DecodeAttributeAdapter>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < DecodeElementAttributesAdapter.this.indices.length;
				}

				@Override
				public DecodeAttributeAdapter next() {
					return DecodeElementAttributesAdapter.this.item(this.index++);
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
	 * Diese Klasse implementiert ein {@link Attr} als {@link DecodeNodeAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeAttributeAdapter extends DecodeNodeAdapter implements Attr {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementAdapter}.
		 */
		protected final DecodeElementAdapter parent;

		/**
		 * Dieses Feld speichert den {@link DecodeAttribute}-{@code Index}.
		 */
		protected final int index;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}- {@link DecodeElementAdapter} und {@link DecodeAttribute}-
		 * {@code Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param index {@link DecodeAttribute}-{@code Index}.
		 */
		public DecodeAttributeAdapter(final DecodeElementAdapter parent, final int index) {
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
		 * Diese Methode gibt den {@code Parent}-{@link DecodeElementAdapter} zurück.
		 * 
		 * @return {@code Parent}-{@link DecodeElementAdapter}.
		 */
		public DecodeElementAdapter getParentAdapter() {
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
			return this.adapter().getAttributeNodeValue(this.index);
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
			if(!(object instanceof DecodeAttributeAdapter)) return false;
			final DecodeAttributeAdapter data = (DecodeAttributeAdapter)object;
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
			if(!(object instanceof DecodeAttributeAdapter)) return false;
			final DecodeAttributeAdapter data = (DecodeAttributeAdapter)object;
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
	 * Diese Klasse implementiert ein {@link Document} als {@link DecodeNodeAdapter}, der auf den Daten eines
	 * {@link DecodeAdapter}s arbeitet.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeDocumentAdapter extends DecodeNodeAdapter implements Document, ElementList {

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
				return DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_LIST;
			}

			@Override
			public Object getParameter(final String name) throws DOMException {
				if(DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.contains(name)) return Boolean.TRUE;
				if(DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.contains(name)) return Boolean.FALSE;
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
				final int offset = DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.size();
				if(index < offset) return DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.get(index);
				return DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.get(index - offset);
			}

			@Override
			public int getLength() {
				return DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.size()
					+ DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.size();
			}

			@Override
			public boolean contains(final String str) {
				return DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST.contains(str)
					|| DecodeDocumentAdapter.VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST.contains(str);
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
		 * Dieses Feld speichert den {@link DecodeElementAdapter} für {@link #getDocumentElement()}.
		 */
		protected final DecodeElementAdapter documentElement;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeAdapter}.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 */
		public DecodeDocumentAdapter(final DecodeAdapter adapter) {
			this.adapter = adapter;
			this.documentElement = new DecodeElementAdapter(this, adapter.document.documentElement, 0);
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
		public DecodeElementAdapter item(final int index) {
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
		public DecodeElementAdapter getFirstChild() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementAdapter getLastChild() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeDocumentAdapter getChildNodes() {
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
			return DecodeDocumentAdapter.VOID_DOM_IMPLEMENTATION;
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
			return null;
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
			return this.adapter().elementGetElementsByTagName(this.documentElement, uri, name, DecodeCollector.MODE_DESCENDANT_SELF);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DecodeElementAdapter getDocumentElement() {
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
			return DecodeDocumentAdapter.VOID_DOM_CONFIGURATION;
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
			if(!(object instanceof DecodeDocumentAdapter)) return false;
			final DecodeDocumentAdapter data = (DecodeDocumentAdapter)object;
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
	 * Diese Klasse implementiert ein Objekt zur Suche von {@link Element}s in den {@link Element#getChildNodes()} eines
	 * gegebenen {@link DecodeElementAdapter}. Die Ergebnisse der Suche können dann als {@link NodeList} weiterverwendet
	 * werden.
	 * 
	 * @see Element#getElementsByTagName(String)
	 * @see Element#getElementsByTagNameNS(String, String)
	 * @see Document#getElementsByTagName(String)
	 * @see Document#getElementsByTagNameNS(String, String)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeCollector implements Filter<DecodeElement>, ElementList {

		/**
		 * Dieses Feld speichert den Modus für die Suche auf den {@link DecodeGroup} eines gegebenen
		 * {@link DecodeElementAdapter}s.
		 */
		public static final int MODE_CHILDREN = 0;

		/**
		 * Dieses Feld speichert den Modus für die rekursive Suche auf den {@link DecodeGroup} eines gegebenen
		 * {@link DecodeElementAdapter}s.
		 */
		public static final int MODE_DESCENDANT = 1;

		/**
		 * Dieses Feld speichert den Modus für die rekursive Suche auf den {@link DecodeGroup} eines gegebenen
		 * {@link DecodeElementAdapter}s sowie dem {@link DecodeElementAdapter} selbst.
		 */
		public static final int MODE_DESCENDANT_SELF = 2;

		/**
		 * Dieses Feld speichert den {@link DecodeDocument}.
		 */
		public final DecodeDocument cache;

		/**
		 * Dieses Feld speichert die Verschiebung des Indexes der {@link DecodeElement}s in den Indices der
		 * {@link DecodeGroup}. Der Wert entspricht der Anzahl der {@link DecodeValue}s.
		 */
		public final int offset;

		/**
		 * Dieses Feld speichert die Ergebnisse der Suche.
		 */
		public final List<DecodeElementAdapter> results;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeCollector} mit den Daten des {@link DecodeAdapter}s.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 */
		public DecodeCollector(final DecodeAdapter adapter) {
			this.cache = adapter.document;
			this.offset = adapter.offset;
			this.results = new ArrayList<DecodeElementAdapter>();
		}

		/**
		 * Diese Methode Sucht in den {@link DecodeGroup} des gegebenen {@link DecodeElement}s nach {@link DecodeElement}s,
		 * die von der Methode {@link #accept(DecodeElement)} akzeptiert werden und speichert die
		 * {@link DecodeElementAdapter} der Treffer in die {@link List} {@link #results}.
		 * 
		 * @param parent {@link DecodeElementAdapter}.
		 */
		protected final void collectChildren(final DecodeElementAdapter parent) {
			final DecodeElement elementNode = this.cache.elementNodePool.get(parent.index);
			final DecodeGroup elementChildren = this.cache.elementChildrenPool.get(elementNode.children);
			final int[] indices = elementChildren.values;
			final int count = indices.length;
			if(count == 0) return;
			for(int child = 0, offset = this.offset; child < count; child++){
				final int index = indices[child] - offset;
				if(index >= 0){
					final DecodeElement childNode = this.cache.elementNodePool.get(index);
					if(this.accept(childNode)){
						this.results.add(new DecodeElementAdapter(parent, index, child));
					}
				}
			}
		}

		/**
		 * Diese Methode ruft {@link #collectDescendantSelf(DecodeElementAdapter)} für jeden {@link DecodeElement} in den
		 * {@link DecodeGroup} des gegebenen {@link DecodeElement}s auf.
		 * 
		 * @param parent {@link DecodeElementAdapter}.
		 * @param element {@link DecodeElement}.
		 */
		protected final void collectDescendant(final DecodeElementAdapter parent, final DecodeElement element) {
			final DecodeGroup elementChildren = this.cache.elementChildrenPool.get(element.children);
			final int[] indices = elementChildren.values;
			final int count = indices.length;
			if(count == 0) return;
			for(int child = 0, offset = this.offset; child < count; child++){
				final int index = indices[child] - offset;
				if(index >= 0){
					this.collectDescendantSelf(new DecodeElementAdapter(parent, index, child));
				}
			}
		}

		/**
		 * Diese Methode fügt den gegebenen {@link DecodeElementAdapter}s nur dann in die Ergebnis {@link List}
		 * {@link #results} ein, wenn dieser via {@link #accept(DecodeElement)} akzeptiert wird. Anschließend wird
		 * {@link #collectDescendant(DecodeElementAdapter, DecodeElement)} aufgerufen.
		 * 
		 * @param parent {@link DecodeElementAdapter}.
		 */
		protected final void collectDescendantSelf(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementNodePool.get(parent.index);
			if(this.accept(element)){
				this.results.add(parent);
			}
			this.collectDescendant(parent, element);
		}

		/**
		 * Diese Methode leert die Ergebnisse.
		 */
		public void clear() {
			this.results.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			return true;
		}

		/**
		 * Diese Methode Sucht in den {@link Element#getChildNodes()} des gegebenen {@link DecodeElementAdapter}s nach
		 * {@link Element}s, die von der Methode {@link #accept(DecodeElement)} akzeptiert werden und speichert die Treffer
		 * in die {@link List} {@link #results}. Der Modus entscheiden hierbei, ob die Suche rekursiv ist und ob sie den
		 * gegebenen {@link DecodeElementAdapter} mit einbezieht.
		 * 
		 * @see #MODE_CHILDREN
		 * @see #MODE_DESCENDANT
		 * @see #MODE_DESCENDANT_SELF
		 * @param parent {@link DecodeElementAdapter}.
		 * @param mode Modus der Suche.
		 * @throws IllegalArgumentException Wenn der gegebene Modul ungültig ist.
		 */
		public void collect(final DecodeElementAdapter parent, final int mode) throws IllegalArgumentException {
			switch(mode){
				case MODE_CHILDREN:
					this.collectChildren(parent);
					break;
				case MODE_DESCENDANT:
					this.collectDescendant(parent, this.cache.elementNodePool.get(parent.index));
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
		public DecodeElementAdapter item(final int index) {
			if((index < 0) || (index >= this.results.size())) return null;
			return this.results.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getLength() {
			return this.results.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, this.getClass().getSimpleName(), this.results);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeCollector}, der die {@link Element}s an Hand ihrer {@code URI}
	 * filtert.
	 * 
	 * @see Element#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeUriCollector extends DecodeCollector {

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
		public DecodeUriCollector(final DecodeAdapter adapter, final int uri) {
			super(adapter);
			this.uri = uri;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			final DecodeLabel elementLabel = this.cache.elementLabelPool.get(element.label);
			return elementLabel.uri == this.uri;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeCollector}, der die {@link Element}s an Hand ihres {@code Name}
	 * filtert.
	 * 
	 * @see Element#getLocalName()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeNameCollector extends DecodeCollector {

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
		public DecodeNameCollector(final DecodeAdapter adapter, final int name) {
			super(adapter);
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			final DecodeLabel elementLabel = this.cache.elementLabelPool.get(element.label);
			return elementLabel.name == this.name;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeCollector}, der die {@link Element}s an Hand ihrer {@code URI} und
	 * ihres {@code Name} filtert.
	 * 
	 * @see Element#getLocalName()
	 * @see Element#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeLabelCollector extends DecodeCollector {

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
		public DecodeLabelCollector(final DecodeAdapter adapter, final int label) {
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
	 * Diese Methode liest die gegebene Anzahl an {@code int}s aus der gegebenen {@link DecodeSource} und gibt sie als
	 * Array zurück.
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
	 * Diese Methode liest die gegebene Anzahl an {@code byte}s aus der gegebenen {@link DecodeSource} und gibt sie als
	 * Array zurück.
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
	 * Diese Methode liest das gegebene {@link File} in einen neuen {@link DecodeAdapter} ein und gibt dessen
	 * {@link DecodeDocumentAdapter} zurück.
	 * 
	 * @see DecodeSourceFile
	 * @see RandomAccessFile
	 * @see #decode(DecodeSource)
	 * @param source {@link File}.
	 * @return {@link DecodeDocumentAdapter}.
	 * @throws IOException Wenn eine {@link IOException} auftritt.
	 * @throws NullPointerException Wenn das gegebene {@link File} {@code null} ist.
	 * @throws FileNotFoundException Wenn das gegebene {@link File} nicht existiert.
	 */
	public Document decode(final File source) throws NullPointerException, FileNotFoundException, IOException {
		return this.decode(new DecodeSourceFile(new RandomAccessFile(source, "r")));
	}

	/**
	 * Diese Methode liest die gegebene {@link DecodeSource} in einen neuen {@link DecodeAdapter} ein und gibt dessen
	 * {@link DecodeDocumentAdapter} zurück.
	 * 
	 * @see DecodeAdapter#getNodeOwnerDocument()
	 * @param source {@link DecodeSource}.
	 * @return {@link DecodeDocumentAdapter}.
	 * @throws IOException Wenn das {@link DecodeSource} eine {@link IOException} auslöst.
	 * @throws NullPointerException Wenn die gegebene {@link DecodeSource} {@code null} ist.
	 */
	public DecodeDocumentAdapter decode(final DecodeSource source) throws IOException {
		return new DecodeAdapter(new DecodeDocument(source)).getNodeOwnerDocument();
	}

}
