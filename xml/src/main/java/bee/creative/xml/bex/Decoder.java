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
 * Diese Klasse implementiert den {@link DocumentView} zu dem in {@link Encoder} beschriebenen Binärformet.
 * 
 * @see Encoder
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Decoder {

	/**
	 * Diese Klasse implementiert den {@link TextView} eines {@link BEXDocumentView}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class BEXTextView implements TextView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		final BEXParentView parent;

		/**
		 * Dieses Feld speichert den Kindknotenindex.
		 */
		final int index;

		/**
		 * Dieses Feld speichert die Referenz auf des Wert.
		 */
		final int valueRef;

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
			return this.parent.document().value(this.valueRef);
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
	static abstract class BEXParentView implements ParentView {

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
	static class BEXElementView extends BEXParentView implements ElementView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		final BEXParentView parent;

		/**
		 * Dieses Feld speichert den Kindknotenindex.
		 */
		final int index;

		/**
		 * Dieses Feld speichert die Referenz auf den URI.
		 */
		final short uriRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Namen.
		 */
		final short nameRef;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste.
		 */
		final int childrenRef;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		final int attributesRef;

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
		public BEXElementView(final BEXParentView parent, final int index, final short uriRef, final short nameRef, final int childrenRef, final int attributesRef) {
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
		public BEXDocumentView document() {
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
		public String uri() {
			return this.parent.document().name(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return this.parent.document().name(this.nameRef);
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
		public AttributesView attributes() {
			return new BEXAttributesView(this, this.attributesRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public TextView asText() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ElementView asElement() {
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public DocumentView asDocument() {
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
	static class BEXElementTextView extends BEXElementView {

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
		public BEXElementTextView(final BEXParentView parent, final int index, final short uriRef, final short nameRef, final int contentRef,
			final int attributesRef) {
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
	static class BEXChildrenView implements ChildrenView {

		/**
		 * Dieses Feld speichert den {@link BEXParentView}.
		 */
		final BEXParentView parent;

		/**
		 * Dieses Feld speichert den Schlüssel der Kindknotenliste.
		 */
		final int childrenRef;

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
		public DocumentView document() {
			return this.parent.document();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXParentView parent() {
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
		public Iterator<ChildView> iterator() {
			return new GetIterator<ChildView>(this, 0, this.size());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link BEXChildrenView} mit genau einem Textknoten als Kindknoten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static class BEXChildrenTextView extends BEXChildrenView {

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
	static final class BEXAttributeView implements AttributeView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		final BEXElementView parent;

		/**
		 * Dieses Feld speichert den Attributknotenindex.
		 */
		final int index;

		/**
		 * Dieses Feld speichert die Referenz auf den URI.
		 */
		final short uriRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Namen.
		 */
		final short nameRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Wert.
		 */
		final int valueRef;

		/**
		 * Dieser Konstruktor initialisiert den {@link BEXAttributeView}.
		 * 
		 * @param parent Elternknoten.
		 * @param index Attributknotenindex.
		 * @param uriRef Referenz auf den URI.
		 * @param nameRef Referenz auf den Namen.
		 * @param valueRef Referenz auf den Wert.
		 */
		public BEXAttributeView(final BEXElementView parent, final int index, final short uriRef, final short nameRef, final int valueRef) {
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
			return this.parent.document().name(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return this.parent.document().name(this.nameRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			return this.parent.document().value(this.valueRef);
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
	static final class BEXAttributesView implements AttributesView {

		/**
		 * Dieses Feld speichert den Elternknoten.
		 */
		final BEXElementView parent;

		/**
		 * Dieses Feld speichert die Referenz auf die Attributknotenliste.
		 */
		final int attributesRef;

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
	 * Diese Klasse implementiert den {@link DocumentView} zu einem {@link Document}, welches über einem {@link Decoder} aus einer Datenbank ausgelesen wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class BEXDocumentView extends BEXParentView implements DocumentView {

		/**
		 * Diese Klasse implementiert einen wiederverwendbaren Block von 1024 Byte, dessen Wiederverwendungen gezählt werden.
		 * 
		 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class Page {

			/**
			 * Dieses Feld speichert die 1024 Byte Nutzdaten.
			 */
			final byte[] data = new byte[1024];

			/**
			 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
			 */
			int uses;

		}

		/**
		 * Dieses Feld speichert die {@link DecodeSource}.
		 */
		final DecodeSource source;

		/**
		 * Dieses Feld speichert den Beginn der Nutzdatenstrukturen in der {@link DecodeSource}.
		 */
		final long offset;

		/**
		 * Dieses Feld speichert die Größe der Nutzdatenstrukturen in der {@link DecodeSource}.
		 */
		final int length;

		/**
		 * Dieses Feld speichert die {@link Page}s.
		 */
		final Page[] pageList;

		/**
		 * Dieses Feld speichert die maximale Anzahl der geladenen {@link Page}s.
		 */
		final int pageLimit;

		/**
		 * Dieses Feld speichert die Anzahl der aktuell geladenen {@link Page}s.
		 */
		int pageCount;

		/**
		 * Dieses Feld speichert die Startpositionen der Namen. Der i-te Name beginnt bei Zeichen nameStarts[i] und Endet vor Zeichen nameStarts[i+1].
		 */
		final int[] nameStarts;

		/**
		 * Dieses Feld speichert den Beginn der Zeichen der Namen relativ zu {@link #offset}.
		 */
		final int nameOffset;

		/**
		 * Dieses Feld speichert die Startpositionen der Werte. Der i-te Wert beginnt bei Zeichen valueStarts[i] und Endet vor Zeichen valueStarts[i+1].
		 */
		final int[] valueStarts;

		/**
		 * Dieses Feld speichert den Beginn der Zeichen der Werte relativ zu {@link #offset}.
		 */
		final int valueOffset;

		/**
		 * Dieses Feld speichert die Startpositionen der Kindknotenlisten. Die i-te Kindknotenliste beginnt bei Kindknoten childrenStarts[i] und Endet vor
		 * Kindknoten childrenStarts[i+1].
		 */
		final int[] childrenStarts;

		/**
		 * Dieses Feld speichert den Beginn der Kindknoten der Kindknotenlisten relativ zu {@link #offset}.
		 */
		final int childrenOffset;

		/**
		 * Dieses Feld speichert die Startpositionen der Attributknotenlisten. Die i-te Attributknotenliste beginnt bei Attributknoten attributesStarts[i] und Endet
		 * vor Attributknoten attributesStarts[i+1].
		 */
		final int[] attributesStarts;

		/**
		 * Dieses Feld speichert den Beginn der Attributknoten der Attributknotenlisten relativ zu {@link #offset}.
		 */
		final int attributesOffset;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste des {@link Document}s.
		 */
		final int childrenRef;

		/**
		 * Dieser Konstruktor initialisiert {@link DecodeSource} und Cachegröße.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param cacheSize maximaler Speicherverbrauch des Caches in Byte.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public BEXDocumentView(final DecodeSource source, final int cacheSize) throws IOException {
			int count, length, cursor = 0;
			{ // file
				this.source = source;
				this.offset = source.index();
			}
			{ // name
				this.nameStarts = this.loadStarts(source);
				count = this.nameStarts.length;
				length = this.nameStarts[count - 1] * 2;
				cursor += (count * 4) + 4;
				this.nameOffset = ((cursor + length + 3) & ~3) - length;
				cursor = this.nameOffset + length;
				source.seek(cursor + this.offset);
			}
			{ // value
				this.valueStarts = this.loadStarts(source);
				count = this.valueStarts.length;
				length = this.valueStarts[count - 1] * 2;
				cursor += (count * 4) + 4;
				this.valueOffset = ((cursor + length + 3) & ~3) - length;
				cursor = this.valueOffset + length;
				source.seek(cursor + this.offset);
			}
			{ // children
				this.childrenStarts = this.loadStarts(source);
				count = this.childrenStarts.length;
				length = this.childrenStarts[count - 1] * 16;
				cursor += (count * 4) + 4;
				this.childrenOffset = (cursor + 15) & ~15;
				cursor = this.childrenOffset + length;
				source.seek(cursor + this.offset);
			}
			{ // attributes
				this.attributesStarts = this.loadStarts(source);
				count = this.attributesStarts.length;
				length = this.attributesStarts[count - 1] * 8;
				cursor += (count * 4) + 4;
				this.attributesOffset = (cursor + 7) & ~7;
				cursor = this.attributesOffset + length;
				source.seek(cursor + this.offset);
			}
			{ // document
				final byte[] array = new byte[16];
				source.read(array, 0, 4);
				this.childrenRef = Bytes.get4(array, 0);
				cursor += 4;
				this.length = cursor;
			}
			{ // page
				this.pageList = new Page[(cursor + 1023) / 1024];
				this.pageLimit = Math.max(cacheSize / 1024, 1);
				this.pageCount = 0;
			}
		}

		/**
		 * Diese Methode lödt die Startpositionen aus der gegebenen {@link DecodeSource} und gibt sie zurück.
		 * 
		 * @param source {@link DecodeSource}.
		 * @return Startpositionen.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		int[] loadStarts(final DecodeSource source) throws IOException {
			final byte[] array = new byte[4];
			source.read(array, 0, 4);
			final int length = Bytes.get4(array, 0) + 1;
			final int[] starts = new int[length];
			for(int i = 0; i < length; i++){
				source.read(array, 0, 4);
				starts[i] = Bytes.get4(array, 0);
			}
			return starts;
		}

		/**
		 * Diese Methode gibt den {@link Page#data Nutzdatenblock} der {@link Page} mit dem gegebenen Index zurück.
		 * 
		 * @param index Index.
		 * @return {@link Page#data Nutzdatenblock}.
		 */
		byte[] data(final int index) {
			final Page[] pageList = this.pageList;
			Page page;
			page = pageList[index];
			if(page != null){ // reuse
				page.uses++;
				return page.data;
			}
			int pageCount = this.pageCount, pageLimit = this.pageLimit;
			if(pageCount >= pageLimit){ // compact
				pageLimit = pageLimit / 2;
				final int size = pageList.length;
				while(pageCount > pageLimit){
					int uses = 0;
					final int maxUses = Integer.MAX_VALUE / pageCount;
					for(int i = 0; i < size; i++){
						page = pageList[i];
						if(page != null){
							uses += (page.uses = Math.min(page.uses, maxUses - i));
						}
					}
					final int minUses = uses / pageCount;
					for(int i = 0; i < size; i++){
						page = pageList[i];
						if((page != null) && ((page.uses -= minUses) <= 0)){
							pageList[i] = null;
							pageCount--;
						}
					}
				}
			}
			page = new Page();
			page.uses = 1;
			final byte[] data = page.data;
			final int offset = index * 1024;
			try{
				final DecodeSource source = this.source;
				source.seek(offset + this.offset);
				source.read(data, 0, Math.min(1024, this.length - offset));
			}catch(final Exception e){
				throw new IllegalStateException(e);
			}
			pageList[index] = page;
			this.pageCount = pageCount + 1;
			return data;
		}

		/**
		 * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück und wird von {@link #name(int)} bzw. {@link #value(int)} verwendet.
		 * 
		 * @param key Schlüssel des Texts.
		 * @param starts Startpositionen ({@link #nameStarts} oder {@link #valueStarts}).
		 * @param offset Beginn der Zeichen relativ zu {@link #offset} ({@link #nameOffset} oder {@link #valueOffset}).
		 * @return Text.
		 */
		String text(final int key, final int[] starts, final int offset) {
			if(key < 0) return null;
			int from = starts[key];
			final int size = starts[key + 1] - from;
			from = (from * 2) + offset;
			int page = from / 1024;
			final char[] text = new char[size];
			from = from & 1023;
			for(int i = 0; i < size; from = 0, page++){
				final byte[] data = this.data(page);
				for(final int length = Math.min(size, (i + 512) - (from / 2)); i < length; i++, from += 2){
					text[i] = (char)Bytes.get2(data, from);
				}
			}
			return new String(text);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#uri()}, {@link BEXAttributeView#name()}, {@link BEXElementView#uri()} und
		 * {@link BEXElementView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		String name(final int key) {
			return this.text(key, this.nameStarts, this.nameOffset);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#value()} und {@link BEXTextView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		String value(final int key) {
			return this.text(key, this.valueStarts, this.valueOffset);
		}

		/**
		 * Diese Methode implementiert {@link BEXChildrenView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Kindknoten.
		 */
		int childrenSize(final int key) {
			if(key < 0) return 0;
			final int[] childrenStarts = this.childrenStarts;
			return childrenStarts[key + 1] - childrenStarts[key];
		}

		/**
		 * Diese Methode implementiert {@link BEXChildrenView#get(int)}.
		 * 
		 * @param parent {@link BEXParentView}.
		 * @param key Schlüssel der Kindknotenliste.
		 * @param index Index des Kindknoten.
		 * @return {@link BEXTextView}, {@link BEXElementView} oder {@code null}.
		 */
		ChildView childrenItem(final BEXParentView parent, final int key, final int index) {
			if(key < 0) return null;
			final int[] starts = this.childrenStarts;
			int offset = starts[key] + index;
			if(offset >= starts[key + 1]) return null;
			offset = (offset * 16) + this.childrenOffset;
			final byte[] data = this.data(offset / 1024);
			offset &= 1023;
			final short uriRef = (short)Bytes.get2(data, offset);
			final short nameRef = (short)Bytes.get2(data, offset + 2);
			final int contentRef = Bytes.get4(data, offset + 4);
			final int childrenRef = Bytes.get4(data, offset + 8);
			final int attributesRef = Bytes.get4(data, offset + 12);
			if(nameRef < 0) return new BEXTextView(parent, index, contentRef);
			if(contentRef < 0) return new BEXElementView(parent, index, uriRef, nameRef, childrenRef, attributesRef);
			return new BEXElementTextView(parent, index, uriRef, nameRef, contentRef, attributesRef);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttributesView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Attributknoten.
		 */
		int attributesSize(final int key) {
			if(key < 0) return 0;
			final int[] attributesStarts = this.attributesStarts;
			return attributesStarts[key + 1] - attributesStarts[key];
		}

		/**
		 * Diese Methode implementiert {@link BEXAttributesView#get(int)}.
		 * 
		 * @param parent {@link BEXParentView}.
		 * @param key Schlüssel der Attributknotenliste.
		 * @param index Index des Attributknoten.
		 * @return {@link BEXAttributeView} oder {@code null}.
		 */
		AttributeView attributesItem(final BEXElementView parent, final int key, final int index) {
			if(key < 0) return null;
			final int[] starts = this.attributesStarts;
			int offset = starts[key] + index;
			if(offset >= starts[key + 1]) return null;
			offset = (offset * 8) + this.attributesOffset;
			final byte[] data = this.data(offset / 1024);
			offset &= 1023;
			final short uriRef = (short)Bytes.get2(data, offset);
			final short nameRef = (short)Bytes.get2(data, offset + 2);
			final int valueRef = Bytes.get4(data, offset + 4);
			return new BEXAttributeView(parent, index, uriRef, nameRef, valueRef);
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
	 * Dieses Feld speichert den maximalen Speicherverbrauch des Caches in Byte.
	 */
	protected int cacheSize = 1024 * 128;

	/**
	 * Diese Methode erzeugt ein {@link Document}, das seine Daten aus der gegebenen {@link DecodeSource} nachlädt, und gibt es zurück.
	 * 
	 * @param source {@link DecodeSource}.
	 * @return {@link DocumentAdapter}.
	 * @throws IOException Wenn beim Schreiben ein Fehler euftritt.
	 */
	public DocumentAdapter decode(final DecodeSource source) throws IOException {
		return new DocumentAdapter(new BEXDocumentView(source, this.cacheSize));
	}

	/**
	 * Diese Methode gibt die maximale Größe des Caches in Byte zurück. Der Initialwert ist 128 KB.
	 * 
	 * @return maximaler Speicherverbrauch des Caches in Byte.
	 */
	public int getCacheSize() {
		return this.cacheSize;
	}

	/**
	 * Diese Methode setzt die maximale Größe des Caches in Byte.
	 * 
	 * @param value maximaler Speicherverbrauch des Caches in Byte.
	 * @throws IllegalArgumentException Wenn die gegebene Größe negativ ist.
	 */
	public void setCacheSize(final int value) throws IllegalArgumentException {
		if(value < 0) throw new IllegalArgumentException("value < 0");
		this.cacheSize = value;
	}

}