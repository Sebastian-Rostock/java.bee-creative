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
 * Diese Klasse implementiert das Objekt zur Kofiguration und Erzeugung eines {@link DocumentView} zu dem {@link Document}, dass mit einem {@link Encoder} binär
 * kodiert wurde.
 * 
 * @see Encoder
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Decoder {

	/**
	 * Diese Klasse implementiert ein Objekt zur Vorhaltung von Nutzdaten, deren Wiederverwendungen via {@link #uses} gezählt wird.
	 * 
	 * @see MRUCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class MRUPage {

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
	public static abstract class MRUCache {

		/**
		 * Dieses Feld speichert die Anzahl der aktuell verwalteten {@link MRUPage}s. Dies wird in {@link #set(MRUPage[], int, MRUPage)} modifiziert.
		 */
		protected int pageCount = 0;

		/**
		 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@link MRUPage}s.
		 */
		protected int pageLimit;

		public MRUCache(final int pageLimit) {
			this.pageLimit = Math.max(pageLimit, 1);
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
	public static final class MRUFilePage extends MRUPage {

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
	public static final class MRUFileCache extends MRUCache {

		/**
		 * Dieses Feld speichert die {@link MRUFilePage}s.
		 */
		MRUFilePage[] pages = {};

		/**
		 * Dieses Feld speichert die Größe der Nutzdatenstrukturen in {@link #source}.
		 */
		int length;

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
		 * Diese Methode überspringt den mit den gegebenen Startpositionen indizierten Datenbereich und gibt den Beginn dieses Datenbereichs zurück.
		 * 
		 * @see DecodeSource#index()
		 * @see DecodeSource#seek(long)
		 * @param offsets Startpositionen der Datenobjekte.
		 * @param length Länge eines Datenobjekts.
		 * @return Beginn des Datenbereichs (d.h. {@link DecodeSource#index() Leseposition} vor dem Überspringen).
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die gegebenen Startpositionen {@code null} sind.
		 */
		public int skip(final int[] offsets, final int length) throws IOException, NullPointerException {
			final long index = this.source.index();
			this.source.seek(index + (offsets[offsets.length - 1] * length));
			return (int)(index - this.offset);
		}

		/**
		 * Diese Methode lädt die Startpositionen via {@link DecodeSource#read(byte[], int, int)} aus der {@link #source} und gibt sie zurück.<br>
		 * Die ersten 4 {@code byte} kodieren die um 1 verminderte Anzahl der Startpositionen. Das nächste {@code byte} kodiert die Länge jeder darauf folgenden
		 * Startposition. Die erste Startposition ist implizit 0.
		 * 
		 * @see #source
		 * @return Startpositionen.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public int[] offsets() throws IOException {
			final int size = this.read(4) + 1, length = this.read(1);
			final int[] offsets = new int[size];
			offsets[0] = 0;
			for(int i = 1; i < size; i++){
				offsets[i] = this.read(length);
			}
			return offsets;
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
	public static abstract class MRUItemCache extends MRUCache {

		/**
		 * Dieses Feld speichert die Anzahl der Werte.
		 */
		protected int length;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte.
		 */
		protected final int[] offsets;

		/**
		 * Dieses Feld speichert den Beginn des Datenbereichs mit den Werten.
		 */
		protected int offset;

		/**
		 * Dieses Feld speichert den {@link MRUFileCache} zum Nachladen der Werte.
		 */
		public final MRUFileCache source;

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
			this.source = source;
			this.offsets = source.offsets();
		}

		protected void loadLength(final int itemLength) throws IOException {
			this.offset = this.source.skip(this.offsets, itemLength);
			this.length = this.offsets.length - 1;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Zeichenketten.
	 * 
	 * @see MRUTextValueCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class MRUTextValuePage extends MRUPage {

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
	public static final class MRUTextValueCache extends MRUItemCache {

		/**
		 * Dieses Feld speichert die {@link MRUTextValuePage}s.
		 */
		final MRUTextValuePage[] pages;

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
			this.loadLength(1);
			this.pages = new MRUTextValuePage[((this.length + MRUTextValuePage.SIZE) - 1) / MRUTextValuePage.SIZE];
		}

		/**
		 * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück.
		 * 
		 * @param itemKey Schlüssel des Texts.
		 * @return Text.
		 */
		public String get(final int itemKey) {
			try{
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
				final int[] offsets = this.offsets;
				int offset = offsets[itemKey];
				final int size = offsets[itemKey + 1] - offset;
				offset += this.offset;
				int fileIndex = offset / MRUFilePage.SIZE;
				final byte[] fileData = new byte[size];
				offset = offset & (MRUFilePage.SIZE - 1);
				for(int i = 0; i < size; fileIndex++, offset = 0){
					final int length = Math.min(size - i, MRUFilePage.SIZE - offset);
					System.arraycopy(this.source.get(fileIndex), offset, fileData, i, length);
					i += length;
				}
				final String value = new String(fileData, Encoder.CHARSET);
				page.data[dataIndex] = value;
				return value;
			}catch(Exception e){
				return null;
			}
		}
	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Attributknoten.
	 * 
	 * @see MRUAttrGroupCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class MRUAttrGroupPage extends MRUPage {

		/**
		 * Dieses Feld definiert die Anzahl der Attributknoten in {@link #data}.
		 */
		public static final int SIZE = 64;

		/**
		 * Dieses Feld speichert die Daten der Attributknoten als Auflistung der Referenzen auf die URI, die Namen und die Werte.
		 */
		public final int[] data = new int[MRUAttrGroupPage.SIZE * 3];

	}

	public static final class MRUAttrGroupCache extends MRUItemCache {

		/**
		 * Dieses Feld speichert die {@link MRUAttrGroupPage}s.
		 */
		MRUAttrGroupPage[] pages;

		/**
		 * Dieses Feld speichert die Länge eines Attributknoten in Byte.
		 */
		final int itemLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die URIs in Byte.
		 */
		final int uriRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Namen in Byte.
		 */
		final int nameRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Werte in Byte.
		 */
		final int valueRefLength;

		public MRUAttrGroupCache(final BEXDocumentView owner, final int pageLimit) throws IOException {
			super(owner.fileCache, pageLimit);
			this.uriRefLength = Encoder.computeLength(owner.attrUriCache.length);
			this.nameRefLength = Encoder.computeLength(owner.attrNameCache.length - 1);
			this.valueRefLength = Encoder.computeLength(owner.attrValueCache.length - 1);
			this.itemLength = this.uriRefLength + this.nameRefLength + this.valueRefLength;
			this.loadLength(this.itemLength);
			this.pages = new MRUAttrGroupPage[((this.offsets[this.length] + MRUAttrGroupPage.SIZE) - 1) / MRUAttrGroupPage.SIZE];
		}

		public AttributeView get(final BEXElementView parent, final int groupKey, final int nodeIndex) {
			try{
				final int[] offsets = this.offsets;
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
					int offset = (itemKey * length) + this.offset;
					final int fileIndex = offset / MRUFilePage.SIZE;
					byte[] fileData = this.source.get(fileIndex);
					offset = offset & (MRUFilePage.SIZE - 1);
					final int remain = MRUFilePage.SIZE - offset;
					if(length > remain){
						final byte[] array = this.source.array;
						System.arraycopy(fileData, offset, array, 0, remain);
						System.arraycopy(this.source.get(fileIndex + 1), 0, array, remain, length - remain);
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
				return new BEXAttributeView(parent, nodeIndex, uriRef - 2, nameRef, valueRef);
			}catch(Exception e){
				return null;
			}
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link MRUPage} zur Vorhaltung von Element- und Textknoten.
	 * 
	 * @see MRUElemGroupCache
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class MRUElemGroupPage extends MRUPage {

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
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Element- und Textknoten.
	 * 
	 * @see MRUFileCache
	 * @see MRUElemGroupPage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class MRUElemGroupCache extends MRUItemCache {

		/**
		 * Dieses Feld speichert dei {@link MRUElemGroupPage}s.
		 */
		final MRUElemGroupPage[] pages;

		/**
		 * Dieses Feld speichert die Länge eines Kindknoten in Byte.
		 */
		final int itemLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die URIs in Byte.
		 */
		final int uriRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Namen in Byte.
		 */
		final int nameRefLength;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Kindknotenlisten in Byte.
		 */
		final int contentRefLength;

		/**
		 * Dieses Feld speichert die Anzahl Textwerte als Startreferenz der Kindknotenlisten.
		 */
		final int childrenOffset;

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die Attributknotenlisten in Byte.
		 */
		final int attributesRefLength;

		/**
		 * Dieser Konstruktor initialisiert den {@link MRUElemGroupCache} mit den aus dem gegebenen {@link BEXDocumentView} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocumentView}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link MRUElemGroupPage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocumentView} {@code null} ist.
		 */
		public MRUElemGroupCache(final BEXDocumentView owner, final int pageLimit) throws IOException, NullPointerException {
			super(owner.fileCache, pageLimit);
			this.uriRefLength = Encoder.computeLength(owner.elemUriCache.length);
			this.nameRefLength = Encoder.computeLength(owner.elemNameCache.length);
			this.contentRefLength = Encoder.computeLength((owner.elemValueCache.length + this.offsets.length) - 1);
			this.attributesRefLength = Encoder.computeLength(owner.attrGroupCache.length);
			this.itemLength = this.uriRefLength + this.nameRefLength + this.contentRefLength + this.attributesRefLength;
			this.loadLength(this.itemLength);
			this.pages = new MRUElemGroupPage[((this.offsets[this.length] + MRUElemGroupPage.SIZE) - 1) / MRUElemGroupPage.SIZE];
			this.pageLimit = Math.max(pageLimit, 1);
			this.childrenOffset = owner.elemValueCache.length;
		}

		public ChildView get(final BEXParentView parent, final int groupKey, final int nodeIndex) {
			try{
				final int[] offsets = this.offsets;
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
					offset = (offset * length) + this.offset;
					final int fileIndex = offset / MRUFilePage.SIZE;
					byte[] fileData = this.source.get(fileIndex);
					offset = offset & (MRUFilePage.SIZE - 1);
					final int remain = MRUFilePage.SIZE - offset;
					if(length > remain){
						final byte[] array = this.source.array;
						System.arraycopy(fileData, offset, array, 0, remain);
						System.arraycopy(this.source.get(fileIndex + 1), 0, array, remain, length - remain);
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
				final int childrenRef = contentRef - this.childrenOffset;
				if(nameRef < 0) return new BEXTextView(parent, nodeIndex, contentRef);
				if(contentRef < 0) return new BEXElementView(parent, nodeIndex, uriRef - 2, nameRef, -1, attributesRef);
				if(childrenRef < 0) return new BEXElementTextView(parent, nodeIndex, uriRef - 2, nameRef, contentRef, attributesRef);
				return new BEXElementView(parent, nodeIndex, uriRef - 2, nameRef, childrenRef, attributesRef);
			}catch(Exception e){
				return null;
			}
		}

	}

	/**
	 * Diese Klasse implementiert den {@link TextView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXTextView implements TextView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXParentView parent;

		/**
		 * Dieses Feld speichert den Kindknotenindex.
		 */
		public final int index;

		/**
		 * Dieses Feld speichert die Referenz auf des Wert.
		 */
		public final int valueRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXTextView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param valueRef Referenz auf des Wert.
		 */
		public BEXTextView(final BEXParentView parent, final int index, final int valueRef) {
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
			return this.parent.document().elementValue(this.valueRef);
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
	 * Diese Schnittstelle definiert den {@link ParentView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class BEXParentView implements ParentView {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public abstract BEXDocumentView document();

	}

	/**
	 * Diese Klasse implementiert den {@link ElementView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class BEXElementView extends BEXParentView implements ElementView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXParentView parent;

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
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste. In {@link BEXElementTextView} ist die Referenz auf den Wert des Textknoten.
		 */
		public final int childrenRef;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		public final int attributesRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElementView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param childrenRef Referenz auf die Kindknotenliste.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXElementView(final BEXParentView parent, final int index, final int uriRef, final int nameRef, final int childrenRef, final int attributesRef) {
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
		public final BEXDocumentView document() {
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
			return this.parent.document().elementUri(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String name() {
			return this.parent.document().elementName(this.nameRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildrenView children() {
			return new BEXChildrenView(this, this.childrenRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final AttributesView attributes() {
			return new BEXAttributesView(this, this.attributesRef);
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
	 * Diese Klasse implementiert einen {@link BEXElementView} mit genau einem Textknoten als Kindknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXElementTextView extends BEXElementView {

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXElementTextView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Kindknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param contentRef Referenz auf den Wert des Textknoten.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXElementTextView(final BEXParentView parent, final int index, final int uriRef, final int nameRef, final int contentRef, final int attributesRef) {
			super(parent, index, uriRef, nameRef, contentRef, attributesRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildrenView children() {
			return new BEXChildrenTextView(this, this.childrenRef);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link ChildrenView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class BEXChildrenView implements ChildrenView {

		/**
		 * Dieses Feld speichert den {@link BEXParentView}.
		 */
		public final BEXParentView parent;

		/**
		 * Dieses Feld speichert den Schlüssel der Kindknotenliste.
		 */
		public final int childrenRef;

		/**
		 * Dieses Feld speichert die Länge der Kindknotenliste.
		 */
		int size;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXChildrenView}.
		 * 
		 * @param parent Elternknoten.
		 * @param childrenRef Referenz auf die Kindknotenliste.
		 */
		public BEXChildrenView(final BEXParentView parent, final int childrenRef) {
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
		public final BEXParentView parent() {
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildView get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			return this.parent.document().childrenItem(this.parent, this.childrenRef, index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			final int size = this.size;
			return size >= 0 ? size : (this.size = this.parent.document().childrenSize(this.childrenRef));
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
	 * Diese Klasse implementiert einen {@link BEXChildrenView} mit genau einem Textknoten als Kindknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXChildrenTextView extends BEXChildrenView {

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXChildrenTextView}.
		 * 
		 * @param parent Elternknoten.
		 * @param contentRef Referenz auf den Wert des Textknoten.
		 */
		public BEXChildrenTextView(final BEXParentView parent, final int contentRef) {
			super(parent, contentRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ChildView get(final int index) throws IndexOutOfBoundsException {
			if(index < 0) throw new IndexOutOfBoundsException();
			if(index > 0) return null;
			return new BEXTextView(this.parent, 0, this.childrenRef);
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
	 * Diese Klasse implementiert den {@link AttributeView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXAttributeView implements AttributeView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXElementView parent;

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
		 * Dieser Konstruktor initialisiert den {@link BEXAttributeView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Attributknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param valueRef Referenz auf den Wert.
		 */
		public BEXAttributeView(final BEXElementView parent, final int index, final int uriRef, final int nameRef, final int valueRef) {
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
			return this.parent.document().attributeUri(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return this.parent.document().attributeName(this.nameRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			return this.parent.document().attributeValue(this.valueRef);
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
	 * Diese Klasse implementiert den {@link AttributesView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXAttributesView implements AttributesView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		public final BEXElementView parent;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		public final int attributesRef;

		/**
		 * Dieses Feld speichert die Länge der Attributknotenliste.
		 */
		int size;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXAttributesView}.
		 * 
		 * @param parent Elternknoten.
		 * @param attributesRef Referenz auf die Attributknotenliste.
		 */
		public BEXAttributesView(final BEXElementView parent, final int attributesRef) {
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
			return this.parent.document().attributesItem(this.parent, this.attributesRef, index);
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
			return size >= 0 ? size : (this.size = this.parent.document().attributesSize(this.attributesRef));
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
	 * Diese Klasse implementiert den {@link DocumentView} zu einem {@link Document}, welches über einem {@link Decoder} aus einer {@link DecodeSource} ausgelesen
	 * wird.
	 * 
	 * @see Encoder
	 * @see DecodeSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXDocumentView extends BEXParentView implements DocumentView {

		final MRUFileCache fileCache;

		final MRUTextValueCache attrUriCache;

		final MRUTextValueCache attrNameCache;

		final MRUTextValueCache attrValueCache;

		final MRUAttrGroupCache attrGroupCache;

		final MRUTextValueCache elemUriCache;

		final MRUTextValueCache elemNameCache;

		final MRUTextValueCache elemValueCache;

		final MRUElemGroupCache elemGroupCache;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste des {@link Document}s.
		 */
		final int childrenRef;

		/**
		 * Dieser Konstruktor initialisiert {@link DecodeSource} und Cachegröße.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param cacheSize
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public BEXDocumentView(final DecodeSource source, final Decoder decoder) throws IOException {
			this.fileCache = new MRUFileCache(source, decoder.maxFileCachePages);
			this.attrUriCache = new MRUTextValueCache(this.fileCache, decoder.maxAttrUriCachePages);
			this.attrNameCache = new MRUTextValueCache(this.fileCache, decoder.maxAttrNameCachePages);
			this.attrValueCache = new MRUTextValueCache(this.fileCache, decoder.maxAttrValueCachePages);
			this.attrGroupCache = new MRUAttrGroupCache(this, decoder.maxAttributesCachePages);
			this.elemUriCache = new MRUTextValueCache(this.fileCache, decoder.maxElemUriCachePages);
			this.elemNameCache = new MRUTextValueCache(this.fileCache, decoder.maxElemNameCachePages);
			this.elemValueCache = new MRUTextValueCache(this.fileCache, decoder.maxElemValueCachePages);
			this.elemGroupCache = new MRUElemGroupCache(this, decoder.maxChildrenCachePages);
			this.childrenRef = this.fileCache.read(this.elemGroupCache.contentRefLength) - this.elemValueCache.length - 1;
			this.fileCache.allocate((int)(source.index() - this.fileCache.offset));

			System.err.print("RAM: " + ( //
				(this.fileCache.pages.length * 4) + //
					(this.attrUriCache.pages.length * 4) + //
					(this.attrNameCache.pages.length * 4) + //
					(this.attrValueCache.pages.length * 4) + //
					(this.attrGroupCache.pages.length * 4) + //
					(this.elemUriCache.pages.length * 4) + //
					(this.elemNameCache.pages.length * 4) + //
					(this.elemValueCache.pages.length * 4) + //
					(this.elemGroupCache.pages.length * 4) + //
				4 //
				) + " ");

		}

		/**
		 * Diese Methode implementeirt {@link BEXElementView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String elementUri(final int key) {
			if(key < 0) return null;
			return this.elemUriCache.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElementView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String elementName(final int key) {
			if(key < 0) return null;
			return this.elemNameCache.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXTextView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String elementValue(final int key) {
			if(key < 0) return null;
			return this.elemValueCache.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String attributeUri(final int key) {
			if(key < 0) return null;
			return this.attrUriCache.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String attributeName(final int key) {
			if(key < 0) return null;
			return this.attrNameCache.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String attributeValue(final int key) {
			if(key < 0) return null;
			return this.attrValueCache.get(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXChildrenView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Kindknoten.
		 */
		public int childrenSize(final int key) {
			if(key < 0) return 0;
			final int[] offsets = this.elemGroupCache.offsets;
			return offsets[key + 1] - offsets[key];
		}

		/**
		 * Diese Methode implementiert {@link BEXChildrenView#get(int)}.
		 * 
		 * @param parent {@link BEXParentView}.
		 * @param key Schlüssel der Kindknotenliste.
		 * @param index Index des Kindknoten.
		 * @return {@link BEXTextView}, {@link BEXElementView} oder {@code null}.
		 */
		public ChildView childrenItem(final BEXParentView parent, final int key, final int index) {
			if(key < 0) return null;
			return this.elemGroupCache.get(parent, key, index);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttributesView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Attributknoten.
		 */
		public int attributesSize(final int key) {
			if(key < 0) return 0;
			final int[] offsets = this.attrGroupCache.offsets;
			return offsets[key + 1] - offsets[key];
		}

		/**
		 * Diese Methode implementiert {@link BEXAttributesView#get(int)}.
		 * 
		 * @param parent {@link BEXParentView}.
		 * @param key Schlüssel der Attributknotenliste.
		 * @param index Index des Attributknoten.
		 * @return {@link BEXAttributeView} oder {@code null}.
		 */
		public AttributeView attributesItem(final BEXElementView parent, final int key, final int index) {
			if(key < 0) return null;
			return this.attrGroupCache.get(parent, key, index);
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
			return new BEXChildrenView(this, this.childrenRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXDocumentView document() {
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

	int maxFileCachePages = 128;

	int maxAttrUriCachePages = 32;

	int maxAttrNameCachePages = 32;

	int maxAttrValueCachePages = 64;

	int maxElemUriCachePages = 32;

	int maxElemNameCachePages = 32;

	int maxElemValueCachePages = 64;

	int maxChildrenCachePages = 128;

	int maxAttributesCachePages = 128;

	/**
	 * Diese Methode erzeugt ein {@link Document}, das seine Daten aus der gegebenen {@link DecodeSource} nachlädt, und gibt es zurück.
	 * 
	 * @param source {@link DecodeSource}.
	 * @return {@link DocumentAdapter}.
	 * @throws IOException Wenn beim Schreiben ein Fehler euftritt.
	 */
	public DocumentAdapter decode(final DecodeSource source) throws IOException {
		return new DocumentAdapter(new BEXDocumentView(source, this));
	}

//	public void setCacheSize(final int value) throws IllegalArgumentException {
//		if(value < 0) throw new IllegalArgumentException("value < 0");
//		this.maxFileCachePages = value;
//	}

}