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
	 * Diese Klasse implementiert ein Objekt zur Vorhaltung von Nutzdaten, deren Wiederverwendungen via {@link #uses} gezählt wird.
	 * 
	 * @see AbstractPool
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class AbstractPage {

		/**
		 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
		 */
		public int uses = 1;

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Werten unterschiedlicher Größe, die aus einer {@link DataSource} nachgeladen
	 * werden. Die verdrängung überzähliger vorgehaltener Daten erfolgt gemäß einer <i>most frequently used</i> Strategie.
	 * 
	 * @see DataSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static abstract class AbstractPool {

		/**
		 * Dieses Feld speichert die {@link DataSource} zum Nachladen der Werte.
		 */
		protected final DataSource source;

		/**
		 * Dieses Feld speichert den Beginn des Datenbereichs mit den Werten.
		 */
		protected int offset;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte.
		 */
		protected int[] itemOffset;

		/**
		 * Dieses Feld speichert die Anzahl der Werte.
		 */
		protected int itemCount;

		/**
		 * Dieses Feld speichert die maximale Anzahl der gleichzeitig verwalteten {@link AbstractPage}s.
		 */
		protected int pageLimit;

		/**
		 * Dieses Feld speichert die Anzahl der aktuell verwalteten {@link AbstractPage}s. Dies wird in {@link #set(AbstractPage[], int, AbstractPage)} modifiziert.
		 */
		protected int pageCount;

		/**
		 * Dieser Konstruktor initialisiert den {@link AbstractPool} mit den aus dem gegebenen {@link DataSource} geladenen Informatioen.
		 * 
		 * @param source {@link DataSource}.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link TextValuePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link DataSource} {@code null} ist.
		 */
		public AbstractPool(final DataSource source, final int pageLimit) throws IOException, NullPointerException {
			if(source == null) throw new NullPointerException();
			this.source = source;
			this.pageLimit = Math.max(pageLimit, 1);
			this.pageCount = 0;
		}

		/**
		 * Diese Methode setzt die {@code index}-te {@link AbstractPage} und verdrängt ggf. überzählige {@link AbstractPage}s. <br>
		 * Es wird nicht geprüft, ob die gegebene bzw. die {@code index}-te {@link AbstractPage} im gegebenen Array {@code null} ist.
		 * 
		 * @param pages Array der {@link AbstractPage}s, in welchen aktuell {@link #pageCount} {@link AbstractPage}s verwaltet werden.
		 * @param index Index der zusetzenden {@link AbstractPage}.
		 * @param page zusetzende {@link AbstractPage}.
		 */
		protected final void set(final AbstractPage[] pages, final int index, final AbstractPage page) {
			int pageCount = this.pageCount, pageLimit = this.pageLimit;
			if(pageCount >= pageLimit){
				pageLimit = pageLimit < 0 ? 1 : (pageLimit + 1) / 2;
				final int size = pages.length;
				while(pageCount > pageLimit){
					int uses = 0;
					final int maxUses = Integer.MAX_VALUE / pageCount;
					for(int i = 0; i < size; i++){
						final AbstractPage item = pages[i];
						if(item != null){
							uses += (item.uses = Math.min(item.uses, maxUses - i));
						}
					}
					final int minUses = uses / pageCount;
					for(int i = 0; i < size; i++){
						final AbstractPage item = pages[i];
						if((item != null) && ((item.uses -= minUses) <= 0)){
							pages[i] = null;
							pageCount--;
						}
					}
				}
			}
			this.pageCount = pageCount + 1;
			pages[index] = page;
		}

		/**
		 * Diese Methode lädt {@link #offset} und überspringt den mit {@link #itemOffset} indizierten Datenbereich.
		 * 
		 * @param itemLength Länge eines Datenobjekts.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 */
		protected final void loadFileOffset(final int itemLength) throws IOException {
			final DataSource fileCache = this.source;
			final int[] itemOffset = this.itemOffset;
			this.offset = (int)fileCache.index();
			fileCache.seek(this.offset + (itemOffset[itemOffset.length - 1] * itemLength));
		}

		/**
		 * Diese Methode lädt {@link #itemOffset} und bestimmt {@link #itemCount}.
		 * 
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		protected final void loadItemOffset() throws IOException {
			final DataSource fileCache = this.source;
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

		/**
		 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten {@link AbstractPage}s zurück.
		 * 
		 * @return maximale Anzahl der gleichzeitig verwalteten {@link AbstractPage}s.
		 */
		public int getPageLimit() {
			return this.pageLimit;
		}

		/**
		 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten {@link AbstractPage}s. <br>
		 * Wenn die Anzahl der aktuell verwalteten {@link AbstractPage}s die maximale Anzahl überschreitet, wird die hälfte der {@link AbstractPage}s entfernt.
		 * 
		 * @param value maximale Anzahl der gleichzeitig verwalteten {@link AbstractPage}s.
		 */
		public void setPageLimit(final int value) {
			this.pageLimit = value;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link AbstractPage} zur Vorhaltung von Zeichenketten.
	 * 
	 * @see TextValuePool
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class TextValuePage extends AbstractPage {

		/**
		 * Dieses Feld definiert die Anzahl der Zeichenketten in {@link #data}.
		 */
		static public final int BITS = 5;

		/**
		 * Dieses Feld speichert die vorgehaltenen Zeichenketten.
		 */
		public final String[] data = new String[1 << TextValuePage.BITS];

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Zeichenketten, die aus eienr {@link DataSource} nachgeladen werden.
	 * 
	 * @see TextValuePage
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class TextValuePool extends AbstractPool {

		/**
		 * Dieses Feld speichert die {@link TextValuePage}s.
		 */
		final TextValuePage[] pages;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 * @param pageLimit maximale Anzahl der gleichzeitig verwalteten {@link TextValuePage}s.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn die {@link DataSource} {@code null} ist.
		 */
		public TextValuePool(final BEXDocuNode owner, final int pageLimit) throws IOException, NullPointerException {
			super(owner.source, pageLimit);
			this.loadItemOffset();
			this.loadFileOffset(1);
			this.pages = new TextValuePage[((this.itemCount + (1 << TextValuePage.BITS)) - 1) >> TextValuePage.BITS];
		}

		/**
		 * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück.
		 * 
		 * @param itemKey Schlüssel des Texts.
		 * @return Text.
		 */
		public String valueItem(final int itemKey) {

			if((itemKey < 0) || (itemKey >= this.itemCount)) return null;
			final TextValuePage[] pages = this.pages;
			final int pageIndex = itemKey >> TextValuePage.BITS, dataIndex = itemKey & ((1 << TextValuePage.BITS) - 1);
			TextValuePage page = pages[pageIndex];
			if(page != null){
				final String value = page.data[dataIndex];
				if(value != null){
					page.uses++;
					return value;
				}
			}else{
				page = new TextValuePage();
				this.set(pages, pageIndex, page);
			}
			final int[] offsets = this.itemOffset;
			final int offset = offsets[itemKey], length = offsets[itemKey + 1] - offset;
			final byte[] array = new byte[length-1];
			try{
				final DataSource source = this.source;
				source.seek(offset + this.offset);
				source.readFully(array);
			}catch(final IOException e){
				throw new IllegalArgumentException(e);
			}
			final String value = new String(array, Encoder.CHARSET);
			page.data[dataIndex] = value;
			return value;
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Attributknoten, die aus einem {@link DataSource} nachgeladen werden.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class AttrGroupPool extends AbstractPool {

		/**
		 * Dieses Feld speichert einen allgemein nutzbaren Lesepuffer mit 16 {@code byte}.
		 */
		final byte[] array = new byte[16];

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die URIs, Namen und Werte sowie deren Summe in Byte.
		 */
		final int lengths;

		/**
		 * Dieser Konstruktor initialisiert den {@link AttrGroupPool} mit den aus dem gegebenen {@link BEXDocuNode} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocuNode}.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocuNode} {@code null} ist.
		 */
		public AttrGroupPool(final BEXDocuNode owner) throws IOException {
			super(owner.source, 0);
			this.loadItemOffset();
			final int uriLength = Bytes.lengthOf(owner.attrUriPool.itemCount);
			final int nameLength = Bytes.lengthOf(owner.attrNamePool.itemCount - 1);
			final int valueLength = Bytes.lengthOf(owner.attrValuePool.itemCount - 1);
			this.lengths = (uriLength << 15) | (nameLength << 10) | (valueLength << 5) | (uriLength + nameLength + valueLength);
			this.loadFileOffset(this.lengths & 31);
		}

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
			final byte[] array = this.array;
			final int lengths = this.lengths;
			int length = lengths & 31, offset;
			try{
				this.source.seek((itemKey * length) + this.offset);
				this.source.readFully(array, 0, length);
			}catch(final IOException e){
				throw new IllegalArgumentException(e);
			}
			final int uriRef = Bytes.getInt(array, 0, offset = (lengths >> 15) & 31) + 1;
			final int nameRef = Bytes.getInt(array, offset, length = (lengths >> 10) & 31);
			final int valueRef = Bytes.getInt(array, offset + length, (lengths >> 5) & 31);
			return new BEXAttrNodeView(parent, nodeIndex, uriRef - 2, nameRef, valueRef);
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur Verwaltung und Vorhaltung von Element- und Textknoten, die aus einem {@link DataSource} nachgeladen werden.
	 * 
	 * @see DataSource
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class ElemGroupPool extends AbstractPool {

		/**
		 * Dieses Feld speichert einen allgemein nutzbaren Lesepuffer mit 16 {@code byte}.
		 */
		final byte[] array = new byte[16];

		/**
		 * Dieses Feld speichert die Länge der Referenzen auf die URIs, Namen, Kindknotenlisten und Attributknotenlisten sowie deren Summe in Byte.
		 */
		final int lengths;

		/**
		 * Dieses Feld speichert die Anzahl Textwerte als Startreferenz der Kindknotenlisten.
		 */
		final int contentRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link ElemGroupPool} mit den aus dem gegebenen {@link BEXDocuNode} geladenen Informatioen.
		 * 
		 * @param owner {@link BEXDocuNode}.
		 * @throws IOException Wenn ein I/O-Fehler auftritt.
		 * @throws NullPointerException Wenn der {@link BEXDocuNode} {@code null} ist.
		 */
		public ElemGroupPool(final BEXDocuNode owner) throws IOException, NullPointerException {
			super(owner.source, 0);
			this.loadItemOffset();
			final int uriLength = Bytes.lengthOf(owner.elemUriPool.itemCount);
			final int nameLength = Bytes.lengthOf(owner.elemNamePool.itemCount);
			final int contentLength = Bytes.lengthOf((owner.elemValuePool.itemCount + this.itemOffset.length) - 1);
			final int attributesLength = Bytes.lengthOf(owner.attrGroupPool.itemCount);
			final int itemLength = uriLength + nameLength + contentLength + attributesLength;
			this.lengths = (uriLength << 20) | (nameLength << 15) | (contentLength << 10) | (attributesLength << 5) | itemLength;
			this.loadFileOffset(itemLength);
			this.contentRef = owner.elemValuePool.itemCount;
		}

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
			final int itemKey = offsets[groupKey] + nodeIndex;
			if(itemKey >= offsets[groupKey + 1]) return null;
			final byte[] array = this.array;
			final int lengths = this.lengths;
			int length = lengths & 31, offset;
			try{
				final DataSource source = this.source;
				source.seek((itemKey * length) + this.offset);
				source.readFully(array, 0, length);
			}catch(final IOException e){
				throw new IllegalArgumentException(e);
			}
			final int uriRef = Bytes.getInt(array, 0, offset = (lengths >> 20) & 31) - 1;
			final int nameRef = Bytes.getInt(array, offset, length = (lengths >> 15) & 31) - 1;
			final int contentRef = Bytes.getInt(array, offset = offset + length, length = (lengths >> 10) & 31) - 1;
			final int childrenRef = contentRef - this.contentRef;
			final int attributesRef = Bytes.getInt(array, offset + length, (lengths >> 5) & 31) - 1;
			if(nameRef < 0) return new BEXTextNodeView(parent, nodeIndex, contentRef);
			if(contentRef < 0) return new BEXElemNodeView(parent, nodeIndex, uriRef, nameRef, -1, attributesRef);
			if(childrenRef < 0) return new BEXElemTextNodeView(parent, nodeIndex, uriRef, nameRef, contentRef, attributesRef);
			return new BEXElemNodeView(parent, nodeIndex, uriRef, nameRef, childrenRef, attributesRef);
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
		final DataSource source;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Zeichenketten für {@link BEXAttrNodeView#uri()}.
		 */
		final TextValuePool attrUriPool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Zeichenketten für {@link BEXAttrNodeView#name()}.
		 */
		final TextValuePool attrNamePool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Zeichenketten für {@link BEXAttrNodeView#value()}.
		 */
		final TextValuePool attrValuePool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Attributknotenlisten für {@link BEXElemNodeView#attributes()}.
		 */
		final AttrGroupPool attrGroupPool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Zeichenketten für {@link BEXElemNodeView#uri()}.
		 */
		final TextValuePool elemUriPool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Zeichenketten für {@link BEXElemNodeView#name()}.
		 */
		final TextValuePool elemNamePool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Zeichenketten für {@link BEXTextNodeView#value()}.
		 */
		final TextValuePool elemValuePool;

		/**
		 * Dieses Feld speichert den {@link TextValuePool} zur Verwaltung der Kindknotenlisten für {@link BEXElemNodeView#children()} und
		 * {@link BEXDocuNode#children()}.
		 */
		final ElemGroupPool elemGroupPool;

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
			this.source = source;
			this.attrUriPool = new TextValuePool(this, decoder.maxAttrUriCachePages);
			this.attrNamePool = new TextValuePool(this, decoder.maxAttrNameCachePages);
			this.attrValuePool = new TextValuePool(this, decoder.maxAttrValueCachePages);
			this.attrGroupPool = new AttrGroupPool(this);
			this.elemUriPool = new TextValuePool(this, decoder.maxElemUriCachePages);
			this.elemNamePool = new TextValuePool(this, decoder.maxElemNameCachePages);
			this.elemValuePool = new TextValuePool(this, decoder.maxElemValueCachePages);
			this.elemGroupPool = new ElemGroupPool(this);
			this.childrenRef = this.source.readInt((this.elemGroupPool.lengths >> 10) & 31) - this.elemValuePool.itemCount - 1;
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNodeView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String attrNodeUri(final int key) {
			return this.attrUriPool.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNodeView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String attrNodeName(final int key) {
			return this.attrNamePool.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttrNodeView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String attrNodeValue(final int key) {
			return this.attrValuePool.valueItem(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttrListView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Attributknoten.
		 */
		public int attrGroupSize(final int key) {
			return this.attrGroupPool.size(key);
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
			return this.attrGroupPool.item(parent, key, index);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElemNodeView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		public String elemNodeUri(final int key) {
			return this.elemUriPool.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElemNodeView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		public String elemNodeName(final int key) {
			return this.elemNamePool.valueItem(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXTextNodeView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		public String elemNodeValue(final int key) {
			return this.elemValuePool.valueItem(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXElemNodeListView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Kindknoten.
		 */
		public int elemGroupSize(final int key) {
			return this.elemGroupPool.size(key);
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
			return this.elemGroupPool.item(parent, key, index);
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
	 * Dieser Konstruktor initialisiert die maximalen Anzahlen der vorgehaltenen Datenblöcke wie folgt:
	 * 
	 * <pre>
	 * setMaxAttrUriCachePages(32);
	 * setMaxAttrNameCachePages(32);
	 * setMaxAttrValueCachePages(64);
	 * setMaxElemUriCachePages(32);
	 * setMaxElemNameCachePages(32);
	 * setMaxElemValueCachePages(64);
	 * </pre>
	 */
	public Decoder() {
		this.setMaxAttrUriCachePages(32);
		this.setMaxAttrNameCachePages(32);
		this.setMaxAttrValueCachePages(64);
		this.setMaxElemUriCachePages(32);
		this.setMaxElemNameCachePages(32);
		this.setMaxElemValueCachePages(64);
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
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNodeView#uri()} zurück. Jeder Block enthält bis zu
	 * {@code 32} URIs.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrUriCachePages() {
		return this.maxAttrUriCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXAttrNodeView#uri()}. Jeder Block enthält bis zu {@code 32}
	 * URIs.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrUriCachePages(final int value) {
		this.maxAttrUriCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNodeView#name()} zurück. Jeder Block enthält bis zu
	 * {@code 32} Namen.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrNameCachePages() {
		return this.maxAttrNameCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXAttrNodeView#name()}. Jeder Block enthält bis zu {@code 32}
	 * Namen.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrNameCachePages(final int value) {
		this.maxAttrNameCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNodeView#value()} zurück. Jeder Block enthält bis zu
	 * {@code 32} Werte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxAttrValueCachePages() {
		return this.maxAttrValueCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXAttrNodeView#value()}. Jeder Block enthält bis zu {@code 32}
	 * Werte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxAttrValueCachePages(final int value) {
		this.maxAttrValueCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNodeView#uri()} zurück. Jeder Block enthält bis zu
	 * {@code 32} URIs.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemUriCachePages() {
		return this.maxElemUriCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten URI-Blöcke zu {@link BEXElemNodeView#uri()}. Jeder Block enthält bis zu {@code 32}
	 * URIs.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemUriCachePages(final int value) {
		this.maxElemUriCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNodeView#name()} zurück. Jeder Block enthält bis zu
	 * {@code 32} Namen.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemNameCachePages() {
		return this.maxElemNameCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Name-Blöcke zu {@link BEXElemNodeView#name()}. Jeder Block enthält bis zu {@code 32}
	 * Namen.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemNameCachePages(final int value) {
		this.maxElemNameCachePages = value;
	}

	/**
	 * Diese Methode gibt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNodeView#value()} zurück. Jeder Block enthält bis zu
	 * {@code 32} Werte.
	 * 
	 * @return maximale Anzahl.
	 */
	public int getMaxElemValueCachePages() {
		return this.maxElemValueCachePages;
	}

	/**
	 * Diese Methode setzt die maximale Anzahl der gleichzeitig verwalteten Wert-Blöcke zu {@link BEXTextNodeView#value()}. Jeder Block enthält bis zu {@code 32}
	 * Werte.
	 * 
	 * @param value maximale Anzahl.
	 */
	public void setMaxElemValueCachePages(final int value) {
		this.maxElemValueCachePages = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCallFormat(true, true, this, "maxFileCachePages", this.maxAttrUriCachePages, "maxAttrNameCachePages", this.maxAttrNameCachePages,
			"maxAttrValueCachePages", this.maxAttrValueCachePages, "maxElemUriCachePages", this.maxElemUriCachePages, "maxElemNameCachePages",
			this.maxElemNameCachePages, "maxElemValueCachePages", this.maxElemValueCachePages);
	}

}