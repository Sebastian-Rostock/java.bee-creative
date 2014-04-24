package bee.creative.xml.bex;

import java.io.IOException;
import java.util.Iterator;
import org.w3c.dom.Document;
import bee.creative.data.Data.DataSource;
import bee.creative.util.Bytes;
import bee.creative.util.Iterators.GetIterator;
import bee.creative.util.Objects;
import bee.creative.xml.adapter.DocumentAdapter;
import bee.creative.xml.view.NodeListView;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert das Objekt zur Dekodierung eines dem {@link Document}s, dass mit einem {@link Encoder} binär kodiert wurde.
 * 
 * @see Encoder
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Decoder {

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Werten unterschiedlicher Größe, die aus einem {@link MFUDataSourceCache}
	 * nachgeladen werden.
	 * 
	 * @see MFUDataSourceCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class MFUItemCache extends MFUCache {

		/**
		 * Dieses Feld speichert den {@link MFUDataSourceCache} zum Nachladen der Werte.
		 */
		protected final DataSource fileCache;

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
		 * Dieser Konstruktor initialisiert den {@link MFUItemCache} mit den aus dem gegebenen {@link MFUDataSourceCache} geladenen Informatioen.
		 * 
		 * @param source {@link MFUDataSourceCache}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MFUTextValuePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link MFUDataSourceCache} {@code null} ist.
		 */
		public MFUItemCache(final DataSource source, final int pageLimit) throws IOException, NullPointerException {
			super(pageLimit);
			this.fileCache = source;
		}

		/**
		 * Diese Methode lädt {@link #itemOffset} und bestimmt {@link #itemCount}.
		 * 
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		protected void loadItemOffset() throws IOException {
			final DataSource fileCache = this.fileCache;
			final int size = fileCache.readInt() + 1, length = fileCache.readUnsignedByte();
			final int[] offsets = new int[size];
			offsets[0] = 0;
			for(int i = 1; i < size; i++){
				offsets[i] = fileCache.readInt(length);
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
			final DataSource fileCache = this.fileCache;
			final int[] itemOffset = this.itemOffset;
			this.fileOffset = (int)fileCache.index();
			fileCache.seek(fileOffset + (itemOffset[itemOffset.length - 1] * itemLength));
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
	 * Diese Klasse implementiert eine {@link MFUCachePage} zur Vorhaltung von Zeichenketten.
	 * 
	 * @see MFUTextValueCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MFUTextValuePage extends MFUCachePage {

		/**
		 * Dieses Feld definiert die Anzahl der Zeichenketten in {@link #data}.
		 */
		static public final int SIZE = 32;

		/**
		 * Dieses Feld speichert die vorgehaltenen Zeichenketten.
		 */
		public final String[] data = new String[MFUTextValuePage.SIZE];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Zeichenketten, die aus einem {@link MFUDataSourceCache} nachgeladen werden.
	 * 
	 * @see MFUDataSourceCache
	 * @see MFUTextValuePage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MFUTextValueCache extends MFUItemCache {

		/**
		 * Dieses Feld speichert die {@link MFUTextValuePage}s.
		 */
		protected final MFUTextValuePage[] pages;

		/**
		 * Dieser Konstruktor initialisiert den {@link MFUTextValueCache} mit den aus dem gegebenen {@link MFUDataSourceCache} geladenen Informatioen.
		 * 
		 * @param source {@link MFUDataSourceCache}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MFUTextValuePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link MFUDataSourceCache} {@code null} ist.
		 */
		public MFUTextValueCache(final BEXDocuNode owner, final int pageLimit) throws IOException, NullPointerException {
			super(owner.fileCache, pageLimit);
			this.loadItemOffset();
			this.loadFileOffset(1);
			this.pages = new MFUTextValuePage[((this.itemCount + MFUTextValuePage.SIZE) - 1) / MFUTextValuePage.SIZE];
		}

		/**
		 * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück.
		 * 
		 * @param itemKey Schlüssel des Texts.
		 * @return Text.
		 */
		public String valueItem(final int itemKey) {

			if((itemKey < 0) || (itemKey >= this.itemCount)) return null;
			final MFUTextValuePage[] pages = this.pages;
			final int pageIndex = itemKey / MFUTextValuePage.SIZE, dataIndex = itemKey & (MFUTextValuePage.SIZE - 1);
			MFUTextValuePage page = pages[pageIndex];
			if(page != null){
				final String value = page.data[dataIndex];
				if(value != null){
					page.uses++;
					return value;
				}
			}else{
				page = new MFUTextValuePage();
				this.set(pages, pageIndex, page);
			}
			final int[] offsets = this.itemOffset;
			int offset = offsets[itemKey];
			final int size = offsets[itemKey + 1] - offset;
			offset += this.fileOffset;
			final byte[] fileData = new byte[size];

			try{
				fileCache.seek(offset);
				fileCache.readFully(fileData);
			}catch(IOException e){
				throw new IllegalArgumentException(e);
			}

			//
			// int fileIndex = offset / MFUDataSourceCachePage.SIZE;
			// offset = offset & (MFUDataSourceCachePage.SIZE - 1);
			// for(int i = 0; i < size; fileIndex++, offset = 0){
			// final int length = Math.min(size - i, MFUDataSourceCachePage.SIZE - offset);
			// System.arraycopy(this.fileCache.data(fileIndex), offset, fileData, i, length);
			// i += length;
			// }
			final String value = new String(fileData, Encoder.CHARSET);
			page.data[dataIndex] = value;
			return value;
		}

	}

	// TODO enfernen
	/**
	 * Diese Klasse implementiert eine {@link MFUCachePage} zur Vorhaltung von Attributknoten.
	 * 
	 * @see MFUAttrGroupCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MFUAttrGroupPage extends MFUCachePage {

		/**
		 * Dieses Feld definiert die Anzahl der Attributknoten in {@link #data}.
		 */
		public static final int SIZE = 64;

		/**
		 * Dieses Feld speichert die Daten der Attributknoten als Auflistung der Referenzen auf die URI, die Namen und die Werte.
		 */
		public final int[] data = new int[MFUAttrGroupPage.SIZE * 3];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Attributknoten, die aus einem {@link MFUDataSourceCache} nachgeladen werden.
	 * 
	 * @see MFUDataSourceCache
	 * @see MFUAttrGroupPage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MFUAttrGroupCache extends MFUItemCache {

		/**
		 * Dieses Feld speichert einen allgemein nutzbaren Lesepuffer mit 16 {@code byte}.
		 */
		public final byte[] array = new byte[16];

		/**
		 * Dieses Feld speichert die {@link MFUAttrGroupPage}s.
		 */
		protected final MFUAttrGroupPage[] pages;

		/**
		 * Dieses Feld speichert die Länge eines Attributknoten in Byte.
		 */
		protected final int itemLength;

		// TODO refs zusammenfassen

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
		 * Dieser Konstruktor initialisiert den {@link MFUAttrGroupCache} mit den aus dem gegebenen {@link BEXDocuNode} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocuNode}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MFUAttrGroupPage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocuNode} {@code null} ist.
		 */
		public MFUAttrGroupCache(final BEXDocuNode owner, final int pageLimit) throws IOException {
			super(owner.fileCache, pageLimit);
			this.loadItemOffset();
			this.uriRefLength = Bytes.lengthOf(owner.attrUriCache.itemCount);
			this.nameRefLength = Bytes.lengthOf(owner.attrNameCache.itemCount - 1);
			this.valueRefLength = Bytes.lengthOf(owner.attrValueCache.itemCount - 1);
			this.itemLength = this.uriRefLength + this.nameRefLength + this.valueRefLength;
			this.loadFileOffset(this.itemLength);
			this.pages = new MFUAttrGroupPage[((this.itemOffset[this.itemCount] + MFUAttrGroupPage.SIZE) - 1) / MFUAttrGroupPage.SIZE];
		}

		final byte[] buff = new byte[16];
		
		/**
		 * Diese Methode gibt den referenzierten Attributknoten zurück.
		 * 
		 * @param parent Elternknoten.
		 * @param groupKey Schlüssel der Attributknotenliste.
		 * @param nodeIndex Index des Attributknoten.
		 * @return Attributknoten oder {@code null}.
		 */
		public BEXAttrNodeView item(final BEXElemNodeView parent, final int groupKey, final int nodeIndex) {
			if((groupKey < 0) || (groupKey >= this.itemCount) || (nodeIndex < 0)) return null;
			final int[] offsets = this.itemOffset;
			final int itemKey = offsets[groupKey] + nodeIndex;
			if(itemKey >= offsets[groupKey + 1]) return null;
			final int pageIndex = itemKey / MFUAttrGroupPage.SIZE, dataIndex = itemKey & (MFUAttrGroupPage.SIZE - 1);
			final MFUAttrGroupPage[] pages = this.pages;
			MFUAttrGroupPage page = pages[pageIndex];
			int uriRef, nameRef, valueRef;
			final int[] pageData;
			if(page != null){
				page.uses++;
				pageData = page.data;
				uriRef = pageData[dataIndex + (MFUAttrGroupPage.SIZE * 0)];
			}else{
				page = new MFUAttrGroupPage();
				pageData = page.data;
				uriRef = 0;
				this.set(pages, pageIndex, page);
			}
			if(uriRef != 0){
				nameRef = pageData[dataIndex + (MFUAttrGroupPage.SIZE * 1)];
				valueRef = pageData[dataIndex + (MFUAttrGroupPage.SIZE * 2)];
			}else{
				int length = this.itemLength;
				int offset = (itemKey * length) + this.fileOffset;
				byte[] fileData = buff;
				
				try{
					fileCache.seek(offset);
					fileCache.readFully(fileData, 0, length);
				}catch(IOException e){
					throw new IllegalArgumentException(e);
				}
				
				offset=0;
//				byte[] fileData = this.fileCache.data(fileIndex);
//
//				
//				final int fileIndex = offset / MFUDataSourceCachePage.SIZE;
//				
//				
//				offset = offset & (MFUDataSourceCachePage.SIZE - 1);
//				final int remain = MFUDataSourceCachePage.SIZE - offset;
//				if(length > remain){
//					final byte[] array = this.array;
//					System.arraycopy(fileData, offset, array, 0, remain);
//					System.arraycopy(this.fileCache.data(fileIndex + 1), 0, array, remain, length - remain);
//					fileData = array;
//					offset = 0;
//				}
				uriRef = Bytes.getInt(fileData, offset, length = this.uriRefLength) + 1;
				nameRef = Bytes.getInt(fileData, offset = offset + length, length = this.nameRefLength);
				valueRef = Bytes.getInt(fileData, offset + length, this.valueRefLength);
				pageData[dataIndex + (MFUAttrGroupPage.SIZE * 0)] = uriRef;
				pageData[dataIndex + (MFUAttrGroupPage.SIZE * 1)] = nameRef;
				pageData[dataIndex + (MFUAttrGroupPage.SIZE * 2)] = valueRef;
			}
			return new BEXAttrNodeView(parent, nodeIndex, uriRef - 2, nameRef, valueRef);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MFUCachePage} zur Vorhaltung von Element- und Textknoten.
	 * 
	 * @see MFUElemGroupCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MFUElemGroupPage extends MFUCachePage {

		/**
		 * Dieses Feld definiert die Anzahl der Element- und Textknoten in {@link #data}.
		 */
		public static final int SIZE = 64;

		/**
		 * Dieses Feld speichert die Daten der Element- und Textknoten als Auflistung der Referenzen auf die URI, die Namen, die Kindknotenlisten und die
		 * Attributknotenlisten.
		 */
		public final int[] data = new int[MFUElemGroupPage.SIZE * 4];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Element- und Textknoten, die aus einem {@link MFUDataSourceCache} nachgeladen
	 * werden.
	 * 
	 * @see MFUDataSourceCache
	 * @see MFUElemGroupPage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class MFUElemGroupCache extends MFUItemCache {

		/**
		 * Dieses Feld speichert einen allgemein nutzbaren Lesepuffer mit 16 {@code byte}.
		 */
		public final byte[] array = new byte[16];

		/**
		 * Dieses Feld speichert dei {@link MFUElemGroupPage}s.
		 */
		protected final MFUElemGroupPage[] pages;

		/**
		 * Dieses Feld speichert die Länge eines Kindknoten in Byte.
		 */
		protected final int itemLength;

		// TODO refs zusammenfassen

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
		 * Dieser Konstruktor initialisiert den {@link MFUElemGroupCache} mit den aus dem gegebenen {@link BEXDocuNode} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocuNode}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MFUElemGroupPage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocuNode} {@code null} ist.
		 */
		public MFUElemGroupCache(final BEXDocuNode owner, final int pageLimit) throws IOException, NullPointerException {
			super(owner.fileCache, pageLimit);
			this.loadItemOffset();
			this.uriRefLength = Bytes.lengthOf(owner.elemUriCache.itemCount);
			this.nameRefLength = Bytes.lengthOf(owner.elemNameCache.itemCount);
			this.contentRefLength = Bytes.lengthOf((owner.elemValueCache.itemCount + this.itemOffset.length) - 1);
			this.attributesRefLength = Bytes.lengthOf(owner.attrGroupCache.itemCount);
			this.itemLength = this.uriRefLength + this.nameRefLength + this.contentRefLength + this.attributesRefLength;
			this.loadFileOffset(this.itemLength);
			this.pages = new MFUElemGroupPage[((this.itemOffset[this.itemCount] + MFUElemGroupPage.SIZE) - 1) / MFUElemGroupPage.SIZE];
			this.pageLimit = Math.max(pageLimit, 1);
			this.valueRefOffset = owner.elemValueCache.itemCount;
		}
		final byte[] buff = new byte[16];
		
		/**
		 * Diese Methode gibt den referenzierten Kindknoten zurück.
		 * 
		 * @param parent Elternknoten.
		 * @param groupKey Schlüssel der Kindknotenliste.
		 * @param nodeIndex Index des Kindknoten.
		 * @return Attributknoten oder {@code null}.
		 */
		public BEXNodeView item(final BEXNodeView parent, final int groupKey, final int nodeIndex) {
			if((groupKey < 0) || (groupKey >= this.itemCount) || (nodeIndex < 0)) return null;
			final int[] offsets = this.itemOffset;
			int offset = offsets[groupKey] + nodeIndex;
			if(offset >= offsets[groupKey + 1]) return null;
			final int pageIndex = offset / MFUElemGroupPage.SIZE, dataIndex = offset & (MFUElemGroupPage.SIZE - 1);
			final MFUElemGroupPage[] pages = this.pages;
			MFUElemGroupPage page = pages[pageIndex];
			final int[] pageData;
			int uriRef, nameRef, contentRef, attributesRef;
			if(page != null){
				page.uses++;
				pageData = page.data;
				uriRef = pageData[dataIndex + (MFUElemGroupPage.SIZE * 0)];
			}else{
				this.set(pages, pageIndex, page = new MFUElemGroupPage());
				pageData = page.data;
				uriRef = 0;
			}
			if(uriRef != 0){
				nameRef = pageData[dataIndex + (MFUElemGroupPage.SIZE * 1)];
				contentRef = pageData[dataIndex + (MFUElemGroupPage.SIZE * 2)];
				attributesRef = pageData[dataIndex + (MFUElemGroupPage.SIZE * 3)];
			}else{
				int length = this.itemLength;
				offset = (offset * length) + this.fileOffset;
				byte[] fileData = buff;
				
				try{
					fileCache.seek(offset);
					fileCache.readFully(fileData, 0, length);
				}catch(IOException e){
					throw new IllegalArgumentException(e);
				}
				offset = 0;
				
//				final int fileIndex = offset / MFUDataSourceCachePage.SIZE;
//				byte[] fileData = this.fileCache.data(fileIndex);
//				offset = offset & (MFUDataSourceCachePage.SIZE - 1);
//				final int remain = MFUDataSourceCachePage.SIZE - offset;
//				if(length > remain){
//					final byte[] array = this.array;
//					System.arraycopy(fileData, offset, array, 0, remain);
//					System.arraycopy(this.fileCache.data(fileIndex + 1), 0, array, remain, length - remain);
//					fileData = array;
//					offset = 0;
//				}
				uriRef = Bytes.getInt(fileData, offset, length = this.uriRefLength) + 1;
				offset += length;
				nameRef = Bytes.getInt(fileData, offset, length = this.nameRefLength) - 1;
				offset += length;
				contentRef = Bytes.getInt(fileData, offset, length = this.contentRefLength) - 1;
				offset += length;
				attributesRef = Bytes.getInt(fileData, offset, this.attributesRefLength) - 1;
				pageData[dataIndex + (MFUElemGroupPage.SIZE * 0)] = uriRef;
				pageData[dataIndex + (MFUElemGroupPage.SIZE * 1)] = nameRef;
				pageData[dataIndex + (MFUElemGroupPage.SIZE * 2)] = contentRef;
				pageData[dataIndex + (MFUElemGroupPage.SIZE * 3)] = attributesRef;
			}
			final int childrenRef = contentRef - this.valueRefOffset;
			if(nameRef < 0) return new BEXTextNodeView(parent, nodeIndex, contentRef);
			if(contentRef < 0) return new BEXElemNodeView(parent, nodeIndex, uriRef - 2, nameRef, -1, attributesRef);
			if(childrenRef < 0) return new BEXElemTextNodeView(parent, nodeIndex, uriRef - 2, nameRef, contentRef, attributesRef);
			return new BEXElemNodeView(parent, nodeIndex, uriRef - 2, nameRef, childrenRef, attributesRef);
		}

	}

	/**
	 * Diese Klasse implementiert den abstrakten {@link NodeView} für die Knoten eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BEXNodeView implements NodeView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String uri() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			return -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract BEXNodeView parent();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeView element(final String id) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXElemNodeListView children() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXAttrListView attributes() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXDocuNode document() {
			return this.parent().document();
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

	}

	/**
	 * Diese Klasse implementiert den Textknoten eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXTextNodeView extends BEXNodeView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXNodeView parent;

		/**
		 * Dieses Feld speichert den Kindknotenindex.
		 */
		public final int index;

		/**
		 * Dieses Feld speichert die Referenz auf des Wert.
		 */
		public final int valueRef;

		/**
		 * Dieser Konstruktor initialisiert den Textknoten.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param valueRef Referenz auf den Wert.
		 */
		public BEXTextNodeView(final BEXNodeView parent, final int index, final int valueRef) {
			this.parent = parent;
			this.index = index;
			this.valueRef = valueRef;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return NodeView.TYPE_TEXT;
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
		public int index() {
			return this.index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeView parent() {
			return this.parent;
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
	 * Diese Klasse implementiert den Elementknoten eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class BEXElemNodeView extends BEXNodeView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXNodeView parent;

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
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste. In {@link BEXElemTextNodeView} ist dies die Referenz auf den Wert des Textknoten.
		 */
		public final int contentRef;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		public final int attributesRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemNodeView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param contentRef Referenz auf die Kindknotenliste.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXElemNodeView(final BEXNodeView parent, final int index, final int uriRef, final int nameRef, final int contentRef, final int attributesRef) {
			this.parent = parent;
			this.index = index;
			this.uriRef = uriRef;
			this.nameRef = nameRef;
			this.contentRef = contentRef;
			this.attributesRef = attributesRef;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int type() {
			return NodeView.TYPE_ELEMENT;
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
		public final BEXNodeView parent() {
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
		public BEXElemNodeListView children() {
			return new BEXElemNodeListView(this, this.contentRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final BEXAttrListView attributes() {
			return new BEXAttrListView(this, this.attributesRef);
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
	 * Diese Klasse implementiert die Kindknotenliste eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class BEXElemNodeListView implements NodeListView {

		/**
		 * Dieses Feld speichert den {@link BEXNodeView}.
		 */
		public final BEXNodeView owner;

		/**
		 * Dieses Feld speichert den Schlüssel der Kindknotenliste.
		 */
		public final int childrenRef;

		/**
		 * Dieses Feld speichert die Länge der Kindknotenliste.
		 */
		protected int size;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemNodeListView}.
		 * 
		 * @param owner Elternknoten.
		 * @param childrenRef Referenz auf die Kindknotenliste.
		 */
		public BEXElemNodeListView(final BEXNodeView owner, final int childrenRef) {
			this.owner = owner;
			this.childrenRef = childrenRef;
			this.size = -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final BEXNodeView owner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeView get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			return this.owner.document().elemGroupItem(this.owner, this.childrenRef, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeView get(final String uri, final String name, final int index) throws NullPointerException {
			for(int i = index, size = this.size(); index < size; i++){
				final BEXNodeView item = this.get(i);
				if((item.type() == NodeView.TYPE_ELEMENT) && Objects.equals(name, item.name()) && Objects.equals(uri, item.uri())) return item;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			final int size = this.size;
			return size >= 0 ? size : (this.size = this.owner.document().elemGroupSize(this.childrenRef));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Iterator<NodeView> iterator() {
			return new GetIterator<NodeView>(this, 0, this.size());
		}

	}

	/**
	 * Diese Klasse implementiert einen Elementknoten mit genau einem Textknoten als Kindknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXElemTextNodeView extends BEXElemNodeView {

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemTextNodeView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param contentRef Referenz auf den Wert des Textknoten.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXElemTextNodeView(final BEXNodeView parent, final int index, final int uriRef, final int nameRef, final int contentRef, final int attributesRef) {
			super(parent, index, uriRef, nameRef, contentRef, attributesRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			return this.parent.document().elemNodeValue(this.contentRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXElemTextNodeListView children() {
			return new BEXElemTextNodeListView(this, this.contentRef);
		}

	}

	/**
	 * Diese Klasse implementiert einen Kindknotenliste mit genau einem Textknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXElemTextNodeListView extends BEXElemNodeListView {

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElemTextNodeListView}.
		 * 
		 * @param owner Elternknoten.
		 * @param contentRef Referenz auf den Wert des Textknoten.
		 */
		public BEXElemTextNodeListView(final BEXNodeView owner, final int contentRef) {
			super(owner, contentRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXTextNodeView get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			if(index > 0) return null;
			return new BEXTextNodeView(this.owner, 0, this.childrenRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeView get(final String uri, final String name, final int index) throws NullPointerException {
			return null;
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
	 * Diese Klasse implementiert den Attributknoten eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXAttrNodeView extends BEXNodeView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXElemNodeView parent;

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
		 * Dieser Konstruktor initialisiert den {@link BEXAttrNodeView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Attributknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param valueRef Referenz auf den Wert.
		 */
		public BEXAttrNodeView(final BEXElemNodeView parent, final int index, final int uriRef, final int nameRef, final int valueRef) {
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
		public int type() {
			return NodeView.TYPE_ATTRIBUTE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXElemNodeView parent() {
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
	 * Diese Klasse implementiert die Attributknotenliste eines {@link BEXDocuNode}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXAttrListView implements NodeListView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXElemNodeView owner;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		public final int attributesRef;

		/**
		 * Dieses Feld speichert die Länge der Attributknotenliste.
		 */
		int size;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXAttrListView}.
		 * 
		 * @param owner Elternknoten.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXAttrListView(final BEXElemNodeView owner, final int attributesRef) {
			this.owner = owner;
			this.attributesRef = attributesRef;
			this.size = -1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXElemNodeView owner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXAttrNodeView get(final int index) throws IndexOutOfBoundsException {
			return this.owner.document().attrGroupItem(this.owner, this.attributesRef, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXAttrNodeView get(final String uri, final String name, final int index) throws NullPointerException {
			for(int i = index, size = this.size(); i < size; i++){
				final BEXAttrNodeView item = this.get(i);
				if(Objects.equals(name, item.name()) && Objects.equals(uri, item.uri())) return item;
			}
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			final int size = this.size;
			return size >= 0 ? size : (this.size = this.owner.document().attrGroupSize(this.attributesRef));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<NodeView> iterator() {
			return new GetIterator<NodeView>(this, 0, this.size());
		}

	}

	/**
	 * Diese Klasse implementiert den Dokumentknoten zu einem {@link Document}, welches über einem {@link Decoder} aus einer {@link DataSource} ausgelesen wird.
	 * 
	 * @see Encoder
	 * @see DataSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXDocuNode extends BEXNodeView {

		/**
		 * Dieses Feld speichert die {@link DataSource}.
		 */
		final DataSource fileCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXAttrNodeView#uri()}.
		 */
		final MFUTextValueCache attrUriCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXAttrNodeView#name()}.
		 */
		final MFUTextValueCache attrNameCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXAttrNodeView#value()}.
		 */
		final MFUTextValueCache attrValueCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Attributknotenlisten für {@link BEXElemNodeView#attributes()}.
		 */
		final MFUAttrGroupCache attrGroupCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXElemNodeView#uri()}.
		 */
		final MFUTextValueCache elemUriCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXElemNodeView#name()}.
		 */
		final MFUTextValueCache elemNameCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Zeichenketten für {@link BEXTextNodeView#value()}.
		 */
		final MFUTextValueCache elemValueCache;

		/**
		 * Dieses Feld speichert den {@link MFUTextValueCache} zur Verwaltung der Kindknotenlisten für {@link BEXElemNodeView#children()} und
		 * {@link BEXDocuNode#children()}.
		 */
		final MFUElemGroupCache elemGroupCache;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste des {@link Document}s.
		 */
		final int childrenRef;

		/**
		 * Dieser Konstruktor initialisiert {@link DataSource} und Konfigurationsgrößen.
		 * 
		 * @param source {@link DataSource}.
		 * @param decoder {@link Decoder} zur Konfiguration.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public BEXDocuNode(final DataSource source, final Decoder decoder) throws IOException {
			this.fileCache = source;
			this.attrUriCache = new MFUTextValueCache(this, decoder.maxAttrUriCachePages);
			this.attrNameCache = new MFUTextValueCache(this, decoder.maxAttrNameCachePages);
			this.attrValueCache = new MFUTextValueCache(this, decoder.maxAttrValueCachePages);
			this.attrGroupCache = new MFUAttrGroupCache(this, decoder.maxAttrGroupCachePages);
			this.elemUriCache = new MFUTextValueCache(this, decoder.maxElemUriCachePages);
			this.elemNameCache = new MFUTextValueCache(this, decoder.maxElemNameCachePages);
			this.elemValueCache = new MFUTextValueCache(this, decoder.maxElemValueCachePages);
			this.elemGroupCache = new MFUElemGroupCache(this, decoder.maxElemGroupCachePages);
			this.childrenRef = this.fileCache.readInt(this.elemGroupCache.contentRefLength) - this.elemValueCache.itemCount - 1;
			// this.fileCache.allocate((int)(source.index() - this.fileCache.offset()));
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNodeView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String attrNodeUri(final int key) {
			return this.attrUriCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNodeView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String attrNodeName(final int key) {
			return this.attrNameCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNodeView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String attrNodeValue(final int key) {
			return this.attrValueCache.valueItem(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttrListView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Attributknoten.
		 */
		public int attrGroupSize(final int key) {
			return this.attrGroupCache.size(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttrListView#get(int)}.
		 * 
		 * @param parent Elternknoten.
		 * @param key Schlüssel der Attributknotenliste.
		 * @param index Index des Attributknoten.
		 * @return {@link BEXAttrNodeView} oder {@code null}.
		 */
		public BEXAttrNodeView attrGroupItem(final BEXElemNodeView parent, final int key, final int index) {
			return this.attrGroupCache.item(parent, key, index);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElemNodeView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String elemNodeUri(final int key) {
			return this.elemUriCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElemNodeView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String elemNodeName(final int key) {
			return this.elemNameCache.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXTextNodeView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String elemNodeValue(final int key) {
			return this.elemValueCache.valueItem(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXElemNodeListView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Kindknoten.
		 */
		public int elemGroupSize(final int key) {
			return this.elemGroupCache.size(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXElemNodeListView#get(int)}.
		 * 
		 * @param parent {@link BEXNodeView}.
		 * @param key Schlüssel der Kindknotenliste.
		 * @param index Index des Kindknoten.
		 * @return {@link BEXTextNodeView}, {@link BEXElemNodeView} oder {@code null}.
		 */
		public BEXNodeView elemGroupItem(final BEXNodeView parent, final int key, final int index) {
			return this.elemGroupCache.item(parent, key, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			return NodeView.TYPE_DOCUMENT;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeView parent() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXElemNodeListView children() {
			return new BEXElemNodeListView(this, this.childrenRef);
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
		public String toString() {
			return Objects.toStringCall(this, this.children());
		}

	}

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@code byte}-Blöcke einer {@link DataSource}.
	 */
	int maxFileCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNodeView#uri()}.
	 */
	int maxAttrUriCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNodeView#name()}.
	 */
	int maxAttrNameCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNodeView#value()}.
	 */
	int maxAttrValueCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Attributknoten-Blöcke zu {@link BEXElemNodeView#attributes()}.
	 */
	int maxAttrGroupCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNodeView#uri()}.
	 */
	int maxElemUriCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNodeView#name()}.
	 */
	int maxElemNameCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNodeView#value()}.
	 */
	int maxElemValueCachePages;

	/**
	 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten Kindknoten-Blöcke zu {@link BEXElemNodeView#children()} und
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
	 * Diese Methode erzeugt ein {@link Document}, das seine Daten aus der gegebenen {@link DataSource} nachlädt, und gibt es zurück.
	 * 
	 * @param source {@link DataSource}.
	 * @return {@link DocumentAdapter}.
	 * @throws IOException Wenn beim Schreiben ein Fehler euftritt.
	 */
	public DocumentAdapter decode(final DataSource source) throws IOException {
		return new DocumentAdapter(new BEXDocuNode(source, this));
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten {@code byte}-Blöcke einer {@link DataSource} zurück. Jeder Block enthält bis zu
	 * {@value MFUDataSourceCachePage#SIZE} Byte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxFileCachePages() {
		return this.maxFileCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten {@code byte}-Blöcke einer {@link DataSource}. Jeder Block enthält bis zu
	 * {@value MFUDataSourceCachePage#SIZE} Byte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxFileCachePages(final int value) {
		this.maxFileCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNodeView#uri()} zurück. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} URIs.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrUriCachePages() {
		return this.maxAttrUriCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNodeView#uri()}. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} URIs.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrUriCachePages(final int value) {
		this.maxAttrUriCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNodeView#name()} zurück. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Namen.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrNameCachePages() {
		return this.maxAttrNameCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNodeView#name()}. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Namen.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrNameCachePages(final int value) {
		this.maxAttrNameCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNodeView#value()} zurück. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Werte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrValueCachePages() {
		return this.maxAttrValueCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNodeView#value()}. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Werte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrValueCachePages(final int value) {
		this.maxAttrValueCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Attributknoten-Blöcke zu {@link BEXElemNodeView#attributes()} zurück. Jeder Block
	 * enthält bis zu {@value MFUAttrGroupPage#SIZE} Kindknoten.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrGroupCachePages() {
		return this.maxAttrGroupCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Attributknoten-Blöcke zu {@link BEXElemNodeView#attributes()}. Jeder Block enthält bis
	 * zu {@value MFUAttrGroupPage#SIZE} Kindknoten.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrGroupCachePages(final int value) {
		this.maxAttrGroupCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNodeView#uri()} zurück. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} URIs.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemUriCachePages() {
		return this.maxElemUriCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNodeView#uri()}. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} URIs.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemUriCachePages(final int value) {
		this.maxElemUriCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNodeView#name()} zurück. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Namen.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemNameCachePages() {
		return this.maxElemNameCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNodeView#name()}. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Namen.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemNameCachePages(final int value) {
		this.maxElemNameCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNodeView#value()} zurück. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Werte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemValueCachePages() {
		return this.maxElemValueCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNodeView#value()}. Jeder Block enthält bis zu
	 * {@value MFUTextValuePage#SIZE} Werte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemValueCachePages(final int value) {
		this.maxElemValueCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Kindknoten-Blöcke zu {@link BEXElemNodeView#children()} und
	 * {@link BEXDocuNode#children()} zurück. Jeder Block enthält bis zu {@value MFUElemGroupPage#SIZE} Kindknoten.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemGroupCachePages() {
		return this.maxElemGroupCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Kindknoten-Blöcke zu {@link BEXElemNodeView#children()} und
	 * {@link BEXDocuNode#children()} . Jeder Block enthält bis zu {@value MFUElemGroupPage#SIZE} Kindknoten.
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