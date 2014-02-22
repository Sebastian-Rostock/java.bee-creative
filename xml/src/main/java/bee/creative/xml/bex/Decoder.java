package bee.creative.xml.bex;

import java.io.IOException;
import java.util.Iterator;
import org.w3c.dom.Document;
import bee.creative.util.Bytes;
import bee.creative.util.Iterators.GetIterator;
import bee.creative.util.Objects;
import bee.creative.xml.adapter.DocumentAdapter;
import bee.creative.xml.view.AttributeView;
import bee.creative.xml.view.AttributesView;
import bee.creative.xml.view.ChildView;
import bee.creative.xml.view.ChildrenView;
import bee.creative.xml.view.DocumentView;
import bee.creative.xml.view.ElementView;
import bee.creative.xml.view.ParentView;
import bee.creative.xml.view.TextView;

/**
 * Diese Klasse implementiert das Objekt zur Dekodierung eines dem {@link Document}s, dass mit einem {@link Encoder} binär kodiert wurde.
 * 
 * @see Encoder
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Decoder {

	/**
	 * Diese Klasse implementiert ein Objekt zur Vorhaltung von Nutzdaten, deren Wiederverwendungen via {@link #uses} gezählt wird.
	 * 
	 * @see MRUCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class MRUPage {

		/**
		 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
		 */
		public int uses = 1;

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von {@link MRUPage}s.
	 * 
	 * @see MRUPage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class MRUCache {

		/**
		 * Dieses Feld speichert die Anzahl der aktuell verwalteten {@link MRUPage}s. Dies wird in {@link #set(MRUPage[], int, MRUPage)} modifiziert.
		 */
		protected int pageCount;

		/**
		 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@link MRUPage}s.
		 */
		protected int pageLimit;

		/**
		 * Dieser Konstruktor initialisiert {@link #pageLimit}.
		 * 
		 * @param pageLimit {@link #pageLimit}.
		 */
		public MRUCache(final int pageLimit) {
			this.pageLimit = Math.max(pageLimit, 1);
			this.pageCount = 0;
		}

		/**
		 * Diese Methode setzt die {@code index}-te {@link MRUPage} und verdrängt ggf. überzählige {@link MRUPage}s. <br>
		 * Es wird nicht geprüft, ob die gegebene bzw. die {@code index}-te {@link MRUPage} im gegebenen Array {@code null} ist.
		 * 
		 * @param pages Array der {@link MRUPage}s, in welchen aktuell {@link #pageCount} {@link MRUPage}s verwaltet werden.
		 * @param index Index der zusetzenden {@link MRUPage}.
		 * @param page zusetzende {@link MRUPage}.
		 */
		protected final void set(final MRUPage[] pages, final int index, final MRUPage page) {
			int pageCount = this.pageCount, pageLimit = this.pageLimit;
			if(pageCount >= pageLimit){
				pageLimit = (pageLimit + 1) / 2;
				final int size = pages.length;
				while(pageCount > pageLimit){
					int uses = 0;
					{
						final int maxUses = Integer.MAX_VALUE / pageCount;
						for(int i = 0; i < size; i++){
							final MRUPage item = pages[i];
							if(item != null){
								uses += (item.uses = Math.min(item.uses, maxUses - i));
							}
						}
					}
					{
						final int minUses = uses / pageCount;
						for(int i = 0; i < size; i++){
							final MRUPage item = pages[i];
							if((item != null) && ((item.uses -= minUses) <= 0)){
								pages[i] = null;
								pageCount--;
							}
						}
					}
				}
			}
			this.pageCount = pageCount + 1;
			pages[index] = page;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Teilen einer {@link DecodeSource}.
	 * 
	 * @see MRUFileCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUFilePage extends MRUPage {

		/**
		 * Dieses Feld definiert die Anzahl der Byte in {@link #data}.
		 */
		public static final int SIZE = 1024;

		/**
		 * Dieses Feld speichert die Nutzdaten als einen Auszug einer {@link DecodeSource}.
		 */
		public final byte[] data = new byte[MRUFilePage.SIZE];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Auszügen einer {@link DecodeSource}.
	 * 
	 * @see MRUFilePage
	 * @see DecodeSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUFileCache extends MRUCache {

		/**
		 * Dieses Feld speichert die {@link MRUFilePage}s.
		 */
		protected MRUFilePage[] pages = {};

		/**
		 * Dieses Feld speichert die Größe der Nutzdatenstrukturen in {@link #source}.
		 */
		protected int length;

		/**
		 * Dieses Feld speichert einen allgemein nutzbaren Lesepuffer mit 16 {@code byte}.
		 */
		public final byte[] array = new byte[16];

		/**
		 * Dieses Feld speichert den Beginn der Nutzdatenstrukturen in {@link #source}.
		 */
		public final long offset;

		/**
		 * Dieses Feld speichert die {@link DecodeSource}.
		 */
		public final DecodeSource source;

		/**
		 * Dieser Konstruktor initialisiert {@link #source}, {@link #offset} und {@link #pageLimit}. Die {@link #pages} sowie die {@link #length} werden via
		 * {@link #allocate(int)} gesetzt.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MRUFilePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link DecodeSource} {@code null} ist.
		 */
		public MRUFileCache(final DecodeSource source, final int pageLimit) throws IOException, NullPointerException {
			super(pageLimit);
			this.source = source;
			this.offset = source.index();
		}

		/**
		 * Diese Methode gibt den {@link MRUFilePage#data Nutzdatenblock} der {@code index}-ten {@link MRUFilePage} zurück. Diese wird bei Bedarf aus der
		 * {@link #source} nachgeladen. Die Vergrängung überzähliger {@link MRUFilePage}s erfolgt gemäß {@link #set(MRUPage[], int, MRUPage)}.
		 * 
		 * @param pageIndex Index der {@link MRUFilePage}.
		 * @return {@link MRUFilePage#data Nutzdatenblock}.
		 */
		public byte[] get(final int pageIndex) {
			final MRUFilePage[] pages = this.pages;
			{
				final MRUFilePage page = pages[pageIndex];
				if(page != null){
					page.uses++;
					return page.data;
				}
			}
			{
				final MRUFilePage page = new MRUFilePage();
				final byte[] data = page.data;
				final int offset = pageIndex * MRUFilePage.SIZE;
				try{
					final DecodeSource source = this.source;
					source.seek(this.offset + offset);
					source.read(data, 0, Math.min(MRUFilePage.SIZE, this.length - offset));
				}catch(final Exception e){
					throw new IllegalStateException(e);
				}
				this.set(pages, pageIndex, page);
				return data;
			}
		}

		/**
		 * Diese Methode liest die gegebene Anzahl an {@code byte} via {@link DecodeSource#read(byte[], int, int)} aus der {@link #source} und gibt diese als
		 * {@code int} interpreteirt zurück.
		 * 
		 * @see Decoder#get(byte[], int, int)
		 * @see DecodeSource#read(byte[], int, int)
		 * @param size Anzahl der {@code byte}s.
		 * @return {@code byte}s interpretiert als {@code int}.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public int read(final int size) throws IOException {
			final byte[] array = this.array;
			this.source.read(array, 0, size);
			return Decoder.get(array, 0, size);
		}

		/**
		 * Diese Methode setzt die Größe der Nutzdatenstrukturen in {@link #source} und erzeugt dazu die passende Anzahl an {@link MRUFilePage}s.
		 * 
		 * @param length Größe der Nutzdatenstrukturen in {@link #source}.
		 */
		public void allocate(final int length) {
			this.pages = new MRUFilePage[((length + MRUFilePage.SIZE) - 1) / MRUFilePage.SIZE];
			this.pageCount = 0;
			this.length = length;
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Werten unterschiedlicher Größe, die aus einem {@link MRUFileCache} nachgeladen
	 * werden.
	 * 
	 * @see MRUFileCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class MRUItemCache extends MRUCache {

		/**
		 * Dieses Feld speichert den {@link MRUFileCache} zum Nachladen der Werte.
		 */
		protected final MRUFileCache fileCache;

		/**
		 * Dieses Feld speichert den Beginn des Datenbereichs mit den Werten.
		 */
		protected int fileOffset;

		/**
		 * Dieses Feld speichert die Anzahl der Werte.
		 */
		protected int itemCount;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte.
		 */
		protected int[] itemOffset;

		/**
		 * Dieser Konstruktor initialisiert den {@link MRUItemCache} mit den aus dem gegebenen {@link MRUFileCache} geladenen Informatioen.
		 * 
		 * @param source {@link MRUFileCache}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MRUTextValuePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link MRUFileCache} {@code null} ist.
		 */
		public MRUItemCache(final MRUFileCache source, final int pageLimit) throws IOException, NullPointerException {
			super(pageLimit);
			this.fileCache = source;
		}

		/**
		 * Diese Methode lädt {@link #itemOffset} und bestimmt {@link #itemCount}.
		 * 
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		protected void loadItemOffset() throws IOException {
			final MRUFileCache fileCache = this.fileCache;
			final int size = fileCache.read(4) + 1, length = fileCache.read(1);
			final int[] offsets = new int[size];
			offsets[0] = 0;
			for(int i = 1; i < size; i++){
				offsets[i] = fileCache.read(length);
			}
			this.itemCount = size - 1;
			this.itemOffset = offsets;
		}

		/**
		 * Diese Methode lädt {@link #fileOffset} und überspringt den mit {@link #itemOffset} indizierten Datenbereich.
		 * 
		 * @param itemLength Länge eines Datenobjekts.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 */
		protected void loadFileOffset(final int itemLength) throws IOException {
			final MRUFileCache fileCache = this.fileCache;
			final int[] itemOffset = this.itemOffset;
			final long index = fileCache.source.index();
			fileCache.source.seek(index + (itemOffset[itemOffset.length - 1] * itemLength));
			this.fileOffset = (int)(index - fileCache.offset);
		}

		/**
		 * Diese Methode gibt die Größe des Datensatzes mit dem gegebenen Schlüssel zurück.
		 * 
		 * @param itemKey Schlüssel
		 * @return Größe des Datensatzes.
		 */
		public int size(final int itemKey) {
			if(itemKey < 0) return 0;
			final int[] offsets = this.itemOffset;
			return offsets[itemKey + 1] - offsets[itemKey];
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Zeichenketten.
	 * 
	 * @see MRUTextValueCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUTextValuePage extends MRUPage {

		/**
		 * Dieses Feld definiert die Anzahl der Zeichenketten in {@link #data}.
		 */
		static public final int SIZE = 32;

		/**
		 * Dieses Feld speichert die vorgehaltenen Zeichenketten.
		 */
		public final String[] data = new String[MRUTextValuePage.SIZE];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Zeichenketten, die aus einem {@link MRUFileCache} nachgeladen werden.
	 * 
	 * @see MRUFileCache
	 * @see MRUTextValuePage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUTextValueCache extends MRUItemCache {

		/**
		 * Dieses Feld speichert die {@link MRUTextValuePage}s.
		 */
		protected final MRUTextValuePage[] pages;

		/**
		 * Dieser Konstruktor initialisiert den {@link MRUTextValueCache} mit den aus dem gegebenen {@link MRUFileCache} geladenen Informatioen.
		 * 
		 * @param source {@link MRUFileCache}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MRUTextValuePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link MRUFileCache} {@code null} ist.
		 */
		public MRUTextValueCache(final MRUFileCache source, final int pageLimit) throws IOException, NullPointerException {
			super(source, pageLimit);
			this.loadItemOffset();
			this.loadFileOffset(1);
			this.pages = new MRUTextValuePage[((this.itemCount + MRUTextValuePage.SIZE) - 1) / MRUTextValuePage.SIZE];
		}

		/**
		 * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück.
		 * 
		 * @param itemKey Schlüssel des Texts.
		 * @return Text.
		 */
		public String valueItem(final int itemKey) {
			if((itemKey < 0) || (itemKey >= this.itemCount)) return null;
			final MRUTextValuePage[] pages = this.pages;
			final int pageIndex = itemKey / MRUTextValuePage.SIZE, dataIndex = itemKey & (MRUTextValuePage.SIZE - 1);
			MRUTextValuePage page = pages[pageIndex];
			if(page != null){
				final String value = page.data[dataIndex];
				if(value != null){
					page.uses++;
					return value;
				}
			}else{
				page = new MRUTextValuePage();
				this.set(pages, pageIndex, page);
			}
			final int[] offsets = this.itemOffset;
			int offset = offsets[itemKey];
			final int size = offsets[itemKey + 1] - offset;
			offset += this.fileOffset;
			int fileIndex = offset / MRUFilePage.SIZE;
			final byte[] fileData = new byte[size];
			offset = offset & (MRUFilePage.SIZE - 1);
			for(int i = 0; i < size; fileIndex++, offset = 0){
				final int length = Math.min(size - i, MRUFilePage.SIZE - offset);
				System.arraycopy(this.fileCache.get(fileIndex), offset, fileData, i, length);
				i += length;
			}
			final String value = new String(fileData, Encoder.CHARSET);
			page.data[dataIndex] = value;
			return value;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Attributknoten.
	 * 
	 * @see MRUAttrGroupCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUAttrGroupPage extends MRUPage {

		/**
		 * Dieses Feld definiert die Anzahl der Attributknoten in {@link #data}.
		 */
		public static final int SIZE = 64;

		/**
		 * Dieses Feld speichert die Daten der Attributknoten als Auflistung der Referenzen auf die URI, die Namen und die Werte.
		 */
		public final int[] data = new int[MRUAttrGroupPage.SIZE * 3];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Attributknoten, die aus einem {@link MRUFileCache} nachgeladen werden.
	 * 
	 * @see MRUFileCache
	 * @see MRUAttrGroupPage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUAttrGroupCache extends MRUItemCache {

		/**
		 * Dieses Feld speichert die {@link MRUAttrGroupPage}s.
		 */
		protected final MRUAttrGroupPage[] pages;

		/**
		 * Dieses Feld speichert die Länge eines Attributknoten in Byte.
		 */
		protected final int itemLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die URIs in Byte.
		 */
		protected final int uriRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Namen in Byte.
		 */
		protected final int nameRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Werte in Byte.
		 */
		protected final int valueRefLength;

		/**
		 * Dieser Konstruktor initialisiert den {@link MRUAttrGroupCache} mit den aus dem gegebenen {@link BEXDocuNode} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocuNode}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MRUAttrGroupPage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocuNode} {@code null} ist.
		 */
		public MRUAttrGroupCache(final BEXDocuNode owner, final int pageLimit) throws IOException {
			super(owner.fileCache, pageLimit);
			this.loadItemOffset();
			this.uriRefLength = Encoder.lengthOf(owner.attrUriCache.itemCount);
			this.nameRefLength = Encoder.lengthOf(owner.attrNameCache.itemCount - 1);
			this.valueRefLength = Encoder.lengthOf(owner.attrValueCache.itemCount - 1);
			this.itemLength = this.uriRefLength + this.nameRefLength + this.valueRefLength;
			this.loadFileOffset(this.itemLength);
			this.pages = new MRUAttrGroupPage[((this.itemOffset[this.itemCount] + MRUAttrGroupPage.SIZE) - 1) / MRUAttrGroupPage.SIZE];
		}

		/**
		 * Diese Methode gibt den referenzierten Attributknoten zurück.
		 * 
		 * @param parent Elternknoten.
		 * @param groupKey Schlüssel der Attributknotenliste.
		 * @param nodeIndex Index des Attributknoten.
		 * @return Attributknoten oder {@code null}.
		 */
		public AttributeView item(final BEXElemNode parent, final int groupKey, final int nodeIndex) {
			if((groupKey < 0) || (groupKey >= this.itemCount) || (nodeIndex < 0)) return null;
			final int[] offsets = this.itemOffset;
			final int itemKey = offsets[groupKey] + nodeIndex;
			if(itemKey >= offsets[groupKey + 1]) return null;
			final int pageIndex = itemKey / MRUAttrGroupPage.SIZE, dataIndex = itemKey & (MRUAttrGroupPage.SIZE - 1);
			final MRUAttrGroupPage[] pages = this.pages;
			MRUAttrGroupPage page = pages[pageIndex];
			int uriRef, nameRef, valueRef;
			final int[] pageData;
			if(page != null){
				page.uses++;
				pageData = page.data;
				uriRef = pageData[dataIndex + (MRUAttrGroupPage.SIZE * 0)];
			}else{
				page = new MRUAttrGroupPage();
				pageData = page.data;
				uriRef = 0;
				this.set(pages, pageIndex, page);
			}
			if(uriRef != 0){
				nameRef = pageData[dataIndex + (MRUAttrGroupPage.SIZE * 1)];
				valueRef = pageData[dataIndex + (MRUAttrGroupPage.SIZE * 2)];
			}else{
				int length = this.itemLength;
				int offset = (itemKey * length) + this.fileOffset;
				final int fileIndex = offset / MRUFilePage.SIZE;
				byte[] fileData = this.fileCache.get(fileIndex);
				offset = offset & (MRUFilePage.SIZE - 1);
				final int remain = MRUFilePage.SIZE - offset;
				if(length > remain){
					final byte[] array = this.fileCache.array;
					System.arraycopy(fileData, offset, array, 0, remain);
					System.arraycopy(this.fileCache.get(fileIndex + 1), 0, array, remain, length - remain);
					fileData = array;
					offset = 0;
				}
				uriRef = Decoder.get(fileData, offset, length = this.uriRefLength) + 1;
				nameRef = Decoder.get(fileData, offset = offset + length, length = this.nameRefLength);
				valueRef = Decoder.get(fileData, offset + length, this.valueRefLength);
				pageData[dataIndex + (MRUAttrGroupPage.SIZE * 0)] = uriRef;
				pageData[dataIndex + (MRUAttrGroupPage.SIZE * 1)] = nameRef;
				pageData[dataIndex + (MRUAttrGroupPage.SIZE * 2)] = valueRef;
			}
			return new BEXAttrNode(parent, nodeIndex, uriRef - 2, nameRef, valueRef);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Element- und Textknoten.
	 * 
	 * @see MRUElemGroupCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUElemGroupPage extends MRUPage {

		/**
		 * Dieses Feld definiert die Anzahl der Element- und Textknoten in {@link #data}.
		 */
		public static final int SIZE = 64;

		/**
		 * Dieses Feld speichert die Daten der Element- und Textknoten als Auflistung der Referenzen auf die URI, die Namen, die Kindknotenlisten und die
		 * Attributknotenlisten.
		 */
		public final int[] data = new int[MRUElemGroupPage.SIZE * 4];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Element- und Textknoten, die aus einem {@link MRUFileCache} nachgeladen werden.
	 * 
	 * @see MRUFileCache
	 * @see MRUElemGroupPage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MRUElemGroupCache extends MRUItemCache {

		/**
		 * Dieses Feld speichert dei {@link MRUElemGroupPage}s.
		 */
		protected final MRUElemGroupPage[] pages;

		/**
		 * Dieses Feld speichert die Länge eines Kindknoten in Byte.
		 */
		protected final int itemLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die URIs in Byte.
		 */
		protected final int uriRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Namen in Byte.
		 */
		protected final int nameRefLength;

		/**
		 * Dieses Feld speichert die Anzahl Textwerte als Startreferenz der Kindknotenlisten.
		 */
		protected final int valueRefOffset;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Kindknotenlisten in Byte.
		 */
		protected final int contentRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Attributknotenlisten in Byte.
		 */
		protected final int attributesRefLength;

		/**
		 * Dieser Konstruktor initialisiert den {@link MRUElemGroupCache} mit den aus dem gegebenen {@link BEXDocuNode} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocuNode}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MRUElemGroupPage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocuNode} {@code null} ist.
		 */
		public MRUElemGroupCache(final BEXDocuNode owner, final int pageLimit) throws IOException, NullPointerException {
			super(owner.fileCache, pageLimit);
			this.loadItemOffset();
			this.uriRefLength = Encoder.lengthOf(owner.elemUriCache.itemCount);
			this.nameRefLength = Encoder.lengthOf(owner.elemNameCache.itemCount);
			this.contentRefLength = Encoder.lengthOf((owner.elemValueCache.itemCount + this.itemOffset.length) - 1);
			this.attributesRefLength = Encoder.lengthOf(owner.attrGroupCache.itemCount);
			this.itemLength = this.uriRefLength + this.nameRefLength + this.contentRefLength + this.attributesRefLength;
			this.loadFileOffset(this.itemLength);
			this.pages = new MRUElemGroupPage[((this.itemOffset[this.itemCount] + MRUElemGroupPage.SIZE) - 1) / MRUElemGroupPage.SIZE];
			this.pageLimit = Math.max(pageLimit, 1);
			this.valueRefOffset = owner.elemValueCache.itemCount;
		}

		/**
		 * Diese Methode gibt den referenzierten Kindknoten zurück.
		 * 
		 * @param parent Elternknoten.
		 * @param groupKey Schlüssel der Kindknotenliste.
		 * @param nodeIndex Index des Kindknoten.
		 * @return Attributknoten oder {@code null}.
		 */
		public ChildView item(final BEXNode parent, final int groupKey, final int nodeIndex) {
			if((groupKey < 0) || (groupKey >= this.itemCount) || (nodeIndex < 0)) return null;
			final int[] offsets = this.itemOffset;
			int offset = offsets[groupKey] + nodeIndex;
			if(offset >= offsets[groupKey + 1]) return null;
			final int pageIndex = offset / MRUElemGroupPage.SIZE, dataIndex = offset & (MRUElemGroupPage.SIZE - 1);
			final MRUElemGroupPage[] pages = this.pages;
			MRUElemGroupPage page = pages[pageIndex];
			final int[] pageData;
			int uriRef, nameRef, contentRef, attributesRef;
			if(page != null){
				page.uses++;
				pageData = page.data;
				uriRef = pageData[dataIndex + (MRUElemGroupPage.SIZE * 0)];
			}else{
				this.set(pages, pageIndex, page = new MRUElemGroupPage());
				pageData = page.data;
				uriRef = 0;
			}
			if(uriRef != 0){
				nameRef = pageData[dataIndex + (MRUElemGroupPage.SIZE * 1)];
				contentRef = pageData[dataIndex + (MRUElemGroupPage.SIZE * 2)];
				attributesRef = pageData[dataIndex + (MRUElemGroupPage.SIZE * 3)];
			}else{
				int length = this.itemLength;
				offset = (offset * length) + this.fileOffset;
				final int fileIndex = offset / MRUFilePage.SIZE;
				byte[] fileData = this.fileCache.get(fileIndex);
				offset = offset & (MRUFilePage.SIZE - 1);
				final int remain = MRUFilePage.SIZE - offset;
				if(length > remain){
					final byte[] array = this.fileCache.array;
					System.arraycopy(fileData, offset, array, 0, remain);
					System.arraycopy(this.fileCache.get(fileIndex + 1), 0, array, remain, length - remain);
					fileData = array;
					offset = 0;
				}
				uriRef = Decoder.get(fileData, offset, length = this.uriRefLength) + 1;
				offset += length;
				nameRef = Decoder.get(fileData, offset, length = this.nameRefLength) - 1;
				offset += length;
				contentRef = Decoder.get(fileData, offset, length = this.contentRefLength) - 1;
				offset += length;
				attributesRef = Decoder.get(fileData, offset, this.attributesRefLength) - 1;
				pageData[dataIndex + (MRUElemGroupPage.SIZE * 0)] = uriRef;
				pageData[dataIndex + (MRUElemGroupPage.SIZE * 1)] = nameRef;
				pageData[dataIndex + (MRUElemGroupPage.SIZE * 2)] = contentRef;
				pageData[dataIndex + (MRUElemGroupPage.SIZE * 3)] = attributesRef;
			}
			final int childrenRef = contentRef - this.valueRefOffset;
			if(nameRef < 0) return new BEXTextNode(parent, nodeIndex, contentRef);
			if(contentRef < 0) return new BEXElemNode(parent, nodeIndex, uriRef - 2, nameRef, -1, attributesRef);
			if(childrenRef < 0) return new BEXElemTextView(parent, nodeIndex, uriRef - 2, nameRef, contentRef, attributesRef);
			return new BEXElemNode(parent, nodeIndex, uriRef - 2, nameRef, childrenRef, attributesRef);
		}

	}

	/**
	 * Diese Schnittstelle definiert den {@link ParentView} eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BEXNode implements ParentView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract BEXDocuNode document();

	}

	/**
	 * Diese Klasse implementiert den {@link TextView} eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXTextNode implements TextView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXNode parent;

		/**
		 * Dieses Feld speichert den Kindknotenindex.
		 */
		public final int index;

		/**
		 * Dieses Feld speichert die Referenz auf des Wert.
		 */
		public final int valueRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXTextNode}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param valueRef Referenz auf des Wert.
		 */
		public BEXTextNode(final BEXNode parent, final int index, final int valueRef) {
			this.parent = parent;
			this.index = index;
			this.valueRef = valueRef;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentView document() {
			return this.parent.document();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ParentView parent() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			return this.parent.document().elemNodeValue(this.valueRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TextView asText() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementView asElement() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.value());
		}

	}

	/**
	 * Diese Klasse implementiert den {@link AttributeView} eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXAttrNode implements AttributeView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXElemNode parent;

		/**
		 * Dieses Feld speichert den Attributknotenindex.
		 */
		public final int index;

		/**
		 * Dieses Feld speichert die Referenz auf den URI.
		 */
		public final int uriRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Namen.
		 */
		public final int nameRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Wert.
		 */
		public final int valueRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXAttrNode}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Attributknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param valueRef Referenz auf den Wert.
		 */
		public BEXAttrNode(final BEXElemNode parent, final int index, final int uriRef, final int nameRef, final int valueRef) {
			this.parent = parent;
			this.index = index;
			this.uriRef = uriRef;
			this.nameRef = nameRef;
			this.valueRef = valueRef;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentView document() {
			return this.parent.document();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementView parent() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String uri() {
			return this.parent.document().attrNodeUri(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return this.parent.document().attrNodeName(this.nameRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			return this.parent.document().attrNodeValue(this.valueRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.name(), this.value());
		}

	}

	/**
	 * Diese Klasse implementiert den {@link AttributesView} eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXAttrList implements AttributesView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXElemNode parent;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		public final int attributesRef;

		/**
		 * Dieses Feld speichert die Länge der Attributknotenliste.
		 */
		int size;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXAttrList}.
		 * 
		 * @param parent Elternknoten.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXAttrList(final BEXElemNode parent, final int attributesRef) {
			this.parent = parent;
			this.attributesRef = attributesRef;
			this.size = -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentView document() {
			return this.parent.document();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementView parent() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AttributeView get(final int index) throws IndexOutOfBoundsException {
			return this.parent.document().attrGroupItem(this.parent, this.attributesRef, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public AttributeView get(final String uri, final String name) throws NullPointerException {
			for(final AttributeView attributeView: this){
				if(name.equals(attributeView.name())) return attributeView;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			final int size = this.size;
			return size >= 0 ? size : (this.size = this.parent.document().attrGroupSize(this.attributesRef));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<AttributeView> iterator() {
			return new GetIterator<AttributeView>(this, 0, this.size());
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ElementView} eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class BEXElemNode extends BEXNode implements ElementView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXNode parent;

		/**
		 * Dieses Feld speichert den Kindknotenindex.
		 */
		public final int index;

		/**
		 * Dieses Feld speichert die Referenz auf den URI.
		 */
		public final int uriRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Namen.
		 */
		public final int nameRef;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste. In {@link BEXElemTextView} ist die Referenz auf den Wert des Textknoten.
		 */
		public final int childrenRef;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		public final int attributesRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemNode}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param childrenRef Referenz auf die Kindknotenliste.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXElemNode(final BEXNode parent, final int index, final int uriRef, final int nameRef, final int childrenRef, final int attributesRef) {
			this.parent = parent;
			this.index = index;
			this.uriRef = uriRef;
			this.nameRef = nameRef;
			this.childrenRef = childrenRef;
			this.attributesRef = attributesRef;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final BEXDocuNode document() {
			return this.parent.document();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final ParentView parent() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int index() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String uri() {
			return this.parent.document().elemNodeUri(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String name() {
			return this.parent.document().elemNodeName(this.nameRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildrenView children() {
			return new BEXElemList(this, this.childrenRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final AttributesView attributes() {
			return new BEXAttrList(this, this.attributesRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final TextView asText() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final ElementView asElement() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final DocumentView asDocument() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.name(), this.children(), this.attributes());
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ChildrenView} eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class BEXElemList implements ChildrenView {

		/**
		 * Dieses Feld speichert den {@link BEXNode}.
		 */
		public final BEXNode parent;

		/**
		 * Dieses Feld speichert den Schlüssel der Kindknotenliste.
		 */
		public final int childrenRef;

		/**
		 * Dieses Feld speichert die Länge der Kindknotenliste.
		 */
		protected int size;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemList}.
		 * 
		 * @param parent Elternknoten.
		 * @param childrenRef Referenz auf die Kindknotenliste.
		 */
		public BEXElemList(final BEXNode parent, final int childrenRef) {
			this.parent = parent;
			this.childrenRef = childrenRef;
			this.size = -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final DocumentView document() {
			return this.parent.document();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final BEXNode parent() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildView get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			return this.parent.document().elemGroupItem(this.parent, this.childrenRef, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			final int size = this.size;
			return size >= 0 ? size : (this.size = this.parent.document().elemGroupSize(this.childrenRef));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Iterator<ChildView> iterator() {
			return new GetIterator<ChildView>(this, 0, this.size());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link BEXElemNode} mit genau einem Textknoten als Kindknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXElemTextView extends BEXElemNode {

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemTextView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param contentRef Referenz auf den Wert des Textknoten.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXElemTextView(final BEXNode parent, final int index, final int uriRef, final int nameRef, final int contentRef, final int attributesRef) {
			super(parent, index, uriRef, nameRef, contentRef, attributesRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildrenView children() {
			return new BEXElemTextList(this, this.childrenRef);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link BEXElemList} mit genau einem Textknoten als Kindknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXElemTextList extends BEXElemList {

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemTextList}.
		 * 
		 * @param parent Elternknoten.
		 * @param contentRef Referenz auf den Wert des Textknoten.
		 */
		public BEXElemTextList(final BEXNode parent, final int contentRef) {
			super(parent, contentRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildView get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			if(index > 0) return null;
			return new BEXTextNode(this.parent, 0, this.childrenRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return 1;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link DocumentView} zu einem {@link Document}, welches über einem {@link Decoder} aus einer {@link DecodeSource} ausgelesen
	 * wird.
	 * 
	 * @see Encoder
	 * @see DecodeSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXDocuNode extends BEXNode implements DocumentView {

		/**
		 * Dieses Feld speichert den {@link MRUFileCache} zur Verwaltung von Auszügen einer {@link DecodeSource}.
		 */
		final MRUFileCache fileCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXAttrNode#uri()}.
		 */
		final MRUTextValueCache attrUriCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXAttrNode#name()}.
		 */
		final MRUTextValueCache attrNameCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXAttrNode#value()}.
		 */
		final MRUTextValueCache attrValueCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Attributknotenlisten für {@link BEXElemNode#attributes()}.
		 */
		final MRUAttrGroupCache attrGroupCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXElemNode#uri()}.
		 */
		final MRUTextValueCache elemUriCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXElemNode#name()}.
		 */
		final MRUTextValueCache elemNameCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXTextNode#value()}.
		 */
		final MRUTextValueCache elemValueCache;

		/**
		 * Dieses Feld speichert den {@link MRUTextValueCache} zur Verwaltung der Kindknotenlisten für {@link BEXElemNode#children()} und
		 * {@link BEXDocuNode#children()}.
		 */
		final MRUElemGroupCache elemGroupCache;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste des {@link Document}s.
		 */
		final int childrenRef;

		/**
		 * Dieser Konstruktor initialisiert {@link DecodeSource} und Konfigurationsgrößen.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param decoder {@link Decoder} zur Konfiguration.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public BEXDocuNode(final DecodeSource source, final Decoder decoder) throws IOException {
			this.fileCache = new MRUFileCache(source, decoder.maxFileCachePages);
			this.attrUriCache = new MRUTextValueCache(this.fileCache, decoder.maxAttrUriCachePages);
			this.attrNameCache = new MRUTextValueCache(this.fileCache, decoder.maxAttrNameCachePages);
			this.attrValueCache = new MRUTextValueCache(this.fileCache, decoder.maxAttrValueCachePages);
			this.attrGroupCache = new MRUAttrGroupCache(this, decoder.maxAttrGroupCachePages);
			this.elemUriCache = new MRUTextValueCache(this.fileCache, decoder.maxElemUriCachePages);
			this.elemNameCache = new MRUTextValueCache(this.fileCache, decoder.maxElemNameCachePages);
			this.elemValueCache = new MRUTextValueCache(this.fileCache, decoder.maxElemValueCachePages);
			this.elemGroupCache = new MRUElemGroupCache(this, decoder.maxElemGroupCachePages);
			this.childrenRef = this.fileCache.read(this.elemGroupCache.contentRefLength) - this.elemValueCache.itemCount - 1;
			this.fileCache.allocate((int)(source.index() - this.fileCache.offset));
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNode#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String attrNodeUri(final int key) {
			return this.attrUriCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNode#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String attrNodeName(final int key) {
			return this.attrNameCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNode#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String attrNodeValue(final int key) {
			return this.attrValueCache.valueItem(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttrList#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Attributknoten.
		 */
		public int attrGroupSize(final int key) {
			return this.attrGroupCache.size(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttrList#get(int)}.
		 * 
		 * @param parent {@link BEXNode}.
		 * @param key Schlüssel der Attributknotenliste.
		 * @param index Index des Attributknoten.
		 * @return {@link BEXAttrNode} oder {@code null}.
		 */
		public AttributeView attrGroupItem(final BEXElemNode parent, final int key, final int index) {
			return this.attrGroupCache.item(parent, key, index);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElemNode#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String elemNodeUri(final int key) {
			return this.elemUriCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElemNode#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String elemNodeName(final int key) {
			return this.elemNameCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXTextNode#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String elemNodeValue(final int key) {
			return this.elemValueCache.valueItem(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXElemList#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Kindknoten.
		 */
		public int elemGroupSize(final int key) {
			return this.elemGroupCache.size(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXElemList#get(int)}.
		 * 
		 * @param parent {@link BEXNode}.
		 * @param key Schlüssel der Kindknotenliste.
		 * @param index Index des Kindknoten.
		 * @return {@link BEXTextNode}, {@link BEXElemNode} oder {@code null}.
		 */
		public ChildView elemGroupItem(final BEXNode parent, final int key, final int index) {
			return this.elemGroupCache.item(parent, key, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementView element(final String id) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildrenView children() {
			return new BEXElemList(this, this.childrenRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXDocuNode document() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementView asElement() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentView asDocument() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupURI(final String prefix) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupPrefix(final String uri) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.children());
		}

	}

	/**
	 * Diese Methode ließt die gegebene Anzahl an {@code byte} ab der gegebenen Position aus dem gegebenen {@code byte}-Array und gib diese als {@code int}
	 * interpretiert zurück.
	 * 
	 * @see Bytes#get1(byte[], int)
	 * @see Bytes#get2(byte[], int)
	 * @see Bytes#get3(byte[], int)
	 * @see Bytes#get4(byte[], int)
	 * @param array {@code byte}-Array.
	 * @param index Index.
	 * @param size Anzahl an {@code byte} (0..4).
	 * @return {@code byte}s interpretiert als {@code int}.
	 * @throws NullPointerException Wenn das gegebene {@code byte}-Array {@code null} ist.
	 * @throws IllegalArgumentException Wenn Anzahl oder Position ungültig sind.
	 */
	public static final int get(final byte[] array, final int index, final int size) throws NullPointerException, IllegalArgumentException {
		switch(size){
			case 0:
				return 0;
			case 1:
				return Bytes.get1(array, index);
			case 2:
				return Bytes.get2(array, index);
			case 3:
				return Bytes.get3(array, index);
			case 4:
				return Bytes.get4(array, index);
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@code byte}-Blöcke einer {@link DecodeSource}.
	 */
	int maxFileCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNode#uri()}.
	 */
	int maxAttrUriCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNode#name()}.
	 */
	int maxAttrNameCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNode#value()}.
	 */
	int maxAttrValueCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Attributknoten-Blöcke zu {@link BEXElemNode#attributes()}.
	 */
	int maxAttrGroupCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNode#uri()}.
	 */
	int maxElemUriCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNode#name()}.
	 */
	int maxElemNameCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNode#value()}.
	 */
	int maxElemValueCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Kindknoten-Blöcke zu {@link BEXElemNode#children()} und
	 * {@link BEXDocuNode#children()}.
	 */
	int maxElemGroupCachePages;

	/**
	 * Dieser Konstruktor initialisiert die maximalen Anzahlen der vorgehaltenen Datenblöcke wie folgt:
	 * 
	 * <pre>
	 * setMaxFileCachePages(128);
	 * setMaxAttrUriCachePages(32);
	 * setMaxAttrNameCachePages(32);
	 * setMaxAttrValueCachePages(64);
	 * setMaxAttrGroupCachePages(128);
	 * setMaxElemUriCachePages(32);
	 * setMaxElemNameCachePages(32);
	 * setMaxElemValueCachePages(64);
	 * setMaxElemGroupCachePages(128);
	 * </pre>
	 */
	public Decoder() {
		this.setMaxFileCachePages(128);
		this.setMaxAttrUriCachePages(32);
		this.setMaxAttrNameCachePages(32);
		this.setMaxAttrValueCachePages(64);
		this.setMaxAttrGroupCachePages(128);
		this.setMaxElemUriCachePages(32);
		this.setMaxElemNameCachePages(32);
		this.setMaxElemValueCachePages(64);
		this.setMaxElemGroupCachePages(128);
	}

	/**
	 * Diese Methode erzeugt ein {@link Document}, das seine Daten aus der gegebenen {@link DecodeSource} nachlädt, und gibt es zurück.
	 * 
	 * @param source {@link DecodeSource}.
	 * @return {@link DocumentAdapter}.
	 * @throws IOException Wenn beim Schreiben ein Fehler euftritt.
	 */
	public DocumentAdapter decode(final DecodeSource source) throws IOException {
		return new DocumentAdapter(new BEXDocuNode(source, this));
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten {@code byte}-Blöcke einer {@link DecodeSource} zurück. Jeder Block enthält bis zu
	 * {@value MRUFilePage#SIZE} Byte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxFileCachePages() {
		return this.maxFileCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten {@code byte}-Blöcke einer {@link DecodeSource}. Jeder Block enthält bis zu
	 * {@value MRUFilePage#SIZE} Byte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxFileCachePages(final int value) {
		this.maxFileCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNode#uri()} zurück. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} URIs.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrUriCachePages() {
		return this.maxAttrUriCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNode#uri()}. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} URIs.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrUriCachePages(final int value) {
		this.maxAttrUriCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNode#name()} zurück. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Namen.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrNameCachePages() {
		return this.maxAttrNameCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNode#name()}. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Namen.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrNameCachePages(final int value) {
		this.maxAttrNameCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNode#value()} zurück. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Werte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrValueCachePages() {
		return this.maxAttrValueCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNode#value()}. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Werte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrValueCachePages(final int value) {
		this.maxAttrValueCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Attributknoten-Blöcke zu {@link BEXElemNode#attributes()} zurück. Jeder Block enthält
	 * bis zu {@value MRUAttrGroupPage#SIZE} Kindknoten.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrGroupCachePages() {
		return this.maxAttrGroupCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Attributknoten-Blöcke zu {@link BEXElemNode#attributes()}. Jeder Block enthält bis zu
	 * {@value MRUAttrGroupPage#SIZE} Kindknoten.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrGroupCachePages(final int value) {
		this.maxAttrGroupCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNode#uri()} zurück. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} URIs.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemUriCachePages() {
		return this.maxElemUriCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNode#uri()}. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} URIs.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemUriCachePages(final int value) {
		this.maxElemUriCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNode#name()} zurück. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Namen.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemNameCachePages() {
		return this.maxElemNameCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNode#name()}. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Namen.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemNameCachePages(final int value) {
		this.maxElemNameCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNode#value()} zurück. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Werte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemValueCachePages() {
		return this.maxElemValueCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNode#value()}. Jeder Block enthält bis zu
	 * {@value MRUTextValuePage#SIZE} Werte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemValueCachePages(final int value) {
		this.maxElemValueCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Kindknoten-Blöcke zu {@link BEXElemNode#children()} und {@link BEXDocuNode#children()}
	 * zurück. Jeder Block enthält bis zu {@value MRUElemGroupPage#SIZE} Kindknoten.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemGroupCachePages() {
		return this.maxElemGroupCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Kindknoten-Blöcke zu {@link BEXElemNode#children()} und {@link BEXDocuNode#children()}
	 * . Jeder Block enthält bis zu {@value MRUElemGroupPage#SIZE} Kindknoten.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemGroupCachePages(final int value) {
		this.maxElemGroupCachePages = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCallFormat(true, true, this, "maxFileCachePages", this.maxFileCachePages, "maxAttrUriCachePages", this.maxAttrUriCachePages,
			"maxAttrNameCachePages", this.maxAttrNameCachePages, "maxAttrValueCachePages", this.maxAttrValueCachePages, "maxAttrGroupCachePages",
			this.maxAttrGroupCachePages, "maxElemUriCachePages", this.maxElemUriCachePages, "maxElemNameCachePages", this.maxElemNameCachePages,
			"maxElemValueCachePages", this.maxElemValueCachePages, "maxElemGroupCachePages", this.maxElemGroupCachePages);
	}

}