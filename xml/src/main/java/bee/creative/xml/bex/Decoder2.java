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
 * Diese Klasse implementiert den {@link DocumentView} zu dem in {@link Encoder2} beschriebenen Binärformet.
 * 
 * @see Encoder2
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
 class Decoder2 {

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
		final int uriRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Namen.
		 */
		final int nameRef;

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
			return this.parent.document().elementUri(this.uriRef);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
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
		final int uriRef;

		/**
		 * Dieses Feld speichert die Referenz auf den Namen.
		 */
		final int nameRef;

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
	 * Diese Klasse implementiert den {@link DocumentView} zu einem {@link Document}, welches über einem {@link Decoder2} aus einer Datenbank ausgelesen wird.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class BEXDocumentView extends BEXParentView implements DocumentView {

		static class MRUPage {

			/**
			 * Dieses Feld speichert die Anzahl der Wiederverwendungen.
			 */
			int uses;

		}

		static class MRUPool {

			int pageLimit;

			int pageCount;

			final void set(final MRUPage[] pages, final int index, final MRUPage page) {
				int pageCount = this.pageCount;
				int pageLimit = this.pageLimit;
				if(pageCount >= pageLimit){
					pageLimit = (pageLimit + 1) / 2;
					final int size = pages.length;
					while(pageCount > pageLimit){
						int uses = 0;
						final int maxUses = Integer.MAX_VALUE / pageCount;
						for(int i = 0; i < size; i++){
							final MRUPage item = pages[i];
							if(item != null){
								uses += (item.uses = Math.min(item.uses, maxUses - i));
							}
						}
						final int minUses = uses / pageCount;
						for(int i = 0; i < size; i++){
							final MRUPage item = pages[i];
							if((item != null) && ((item.uses -= minUses) <= 0)){
								pages[i] = null;
								pageCount--;
							}
						}
					}
					this.pageCount = pageCount;
				}
				pages[index] = page;
			}

		}

		/**
		 * Diese Klasse implementiert eine {@link MRUPage} der {@link DecodeSource}.
		 * 
		 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		static final class FilePage extends MRUPage {

			/**
			 * Dieses Feld definiert die Anzahl der Byte in {@link #data}.
			 */
			static final int SIZE = 1024;

			/**
			 * Dieses Feld speichert die Nutzdaten.
			 */
			final byte[] data = new byte[FilePage.SIZE];

		}

		static final class ValuePage extends MRUPage {

			/**
			 * Dieses Feld definiert die Anzahl der {@link String}s in {@link #data}.
			 */
			static final int SIZE = 32;

			/**
			 * Dieses Feld speichert die Nutzdaten.
			 */
			final String[] data = new String[ValuePage.SIZE];

		}

		static final class AttributesPage extends MRUPage {

			static final int SIZE = 64;

			// uriRef, nameRef, valueRef
			final int[] data = new int[AttributesPage.SIZE * 3];

		}

		final class AttributesPool extends MRUPool {

			final AttributesPage[] pages;

			final int size;

			final int index;

			final int[] offsets;

			final int length;

			final int uriRefOffset;

			final int uriRefLength;

			final int nameRefOffset;

			final int nameRefLength;

			final int valueRefOffset;

			final int valueRefLength;

			public AttributesPool(final DecodeSource source) throws IOException {
				final BEXDocumentView owner = BEXDocumentView.this;
				this.offsets = BEXDocumentView.this.readOffsets(source);
				this.uriRefLength = Encoder2.computeLength(owner._attrUriPool.size);
				this.uriRefOffset = 0;
				this.nameRefLength = Encoder2.computeLength(owner._attrNamePool.size - 1);
				this.nameRefOffset = this.uriRefLength;
				this.valueRefLength = Encoder2.computeLength(owner._attrValuePool.size - 1);
				this.valueRefOffset = this.uriRefLength + this.nameRefLength;
				this.length = this.uriRefLength + this.nameRefLength + this.valueRefLength;
				this.index = owner.skipEntries(source, this.offsets, this.length);
				this.size = this.offsets.length - 1;
				this.pages = new AttributesPage[((this.offsets[this.size] + AttributesPage.SIZE) - 1) / AttributesPage.SIZE];
				this.pageLimit = 15;
			}

			// AttributeView get(final BEXElementView parent, final int key, final int index) {
			// if(key < 0) return null;
			// final int[] offsets = this.offsets;
			// int offset = offsets[key] + index;
			// if(offset >= offsets[key + 1]) return null;

			// ValuePage[] pages = this.pages;
			//
			// int length = this.length;
			//
			// offset = (offset * length) + this.index;
			// final int page = offset / FilePage.SIZE;
			// byte[] data = BEXDocumentView.this.data(page);
			// offset &= 1023;
			// final int remain = 1024 - offset;
			// if(length > remain){
			// final byte[] array = this.array;
			// System.arraycopy(data, offset, array, 0, remain);
			// System.arraycopy(this.data(page + 1), 0, array, remain, length - remain);
			// data = array;
			// offset = 0;
			// }
			// final int uriRef = Decoder2.get(data, offset, length = this.attributesUriRefLength) - 1;
			// offset += length;
			// final int nameRef = Decoder2.get(data, offset, length = this.attributesNameRefLength);
			// offset += length;
			// final int valueRef = Decoder2.get(data, offset, this.attributesValueRefLength);
			// return new BEXAttributeView(parent, index, uriRef, nameRef, valueRef);
			// }

		}

		final class ValuePool extends MRUPool {

			final int size;

			final ValuePage[] pages;

			final int index;

			final int[] offsets;

			public ValuePool(final DecodeSource source) throws IOException {
				this.offsets = BEXDocumentView.this.readOffsets(source);
				this.index = BEXDocumentView.this.skipEntries(source, this.offsets, 1);
				this.size = this.offsets.length - 1;
				this.pages = new ValuePage[((this.size + ValuePage.SIZE) - 1) / ValuePage.SIZE];
				this.pageLimit = 15;
			}

			/**
			 * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück.
			 * 
			 * @param key Schlüssel des Texts.
			 * @return Text.
			 */
			String get(final int key) {
				if(key < 0) return null;
				final ValuePage[] valuePages = this.pages;
				int index = key / ValuePage.SIZE;
				ValuePage valuePage = valuePages[index];
				if(valuePage == null){
					this.set(valuePages, index, valuePage = new ValuePage());
				}
				index = key & (ValuePage.SIZE - 1);
				String valueData = valuePage.data[index];
				if(valueData == null){
					final int[] offsets = this.offsets;
					int offset = offsets[key];
					final int size = offsets[key + 1] - offset;
					offset += this.index;
					int page = offset / FilePage.SIZE;
					final byte[] text = new byte[size];
					offset = offset & (FilePage.SIZE - 1);
					for(int i = 0; i < size; page++, offset = 0){
						final int length = Math.min(size - i, FilePage.SIZE - offset);
						System.arraycopy(BEXDocumentView.this.data(page), offset, text, i, length);
						i += length;
					}
					valueData = new String(text, Encoder2.TextItem.CHARSET);
					valuePage.data[index] = valueData;
				}else{
					valuePage.uses++;
				}
				return valueData;
			}
		}

		/**
		 * Dieses Feld speichert die {@link DecodeSource}.
		 */
		final DecodeSource source;

		final byte[] array;

		/**
		 * Dieses Feld speichert den Beginn der Nutzdatenstrukturen in der {@link DecodeSource}.
		 */
		final long offset;

		/**
		 * Dieses Feld speichert die Größe der Nutzdatenstrukturen in der {@link DecodeSource}.
		 */
		final int length;

		/**
		 * Dieses Feld speichert die {@link FilePage}s.
		 */
		final FilePage[] pageList;

		/**
		 * Dieses Feld speichert die maximale Anzahl der geladenen {@link FilePage}s.
		 */
		final int pageLimit;

		/**
		 * Dieses Feld speichert die Anzahl der aktuell geladenen {@link FilePage}s.
		 */
		int pageCount;

		/**
		 * Dieses Feld speichert die Startpositionen der Attributknotenlisten. Die i-te Attributknotenliste beginnt bei Attributknoten attributesStarts[i] und Endet
		 * vor Attributknoten attributesStarts[i+1].
		 */
		final int[] attributesOffsets;

		/**
		 * Dieses Feld speichert die Referenz auf die Kindknotenliste des {@link Document}s.
		 */
		final int childrenRef;

		final int elemValueCount;

		/**
		 * Dieses Feld speichert den Beginn der Kindknoten der Kindknotenlisten relativ zu {@link #offset}.
		 */
		final int childrenPool;

		final int childrenLength;

		final int childrenUriRefLength;

		final int childrenNameRefLength;

		final int childrenContentRefLength;

		final int childrenAttributesRefLength;

		/**
		 * Dieses Feld speichert die Startpositionen der Kindknotenlisten. Die i-te Kindknotenliste beginnt bei Kindknoten childrenStarts[i] und Endet vor
		 * Kindknoten childrenStarts[i+1].
		 */
		final int[] childrenOffsets;

		final int attributesPool;

		final int attributesLength;

		final int attributesUriRefLength;

		final int attributesNameRefLength;

		final int attributesValueRefLength;

		ValuePool _attrUriPool;

		ValuePool _attrNamePool;

		ValuePool _attrValuePool;

		ValuePool _elemUriPool;

		ValuePool _elemNamePool;

		ValuePool _elemValuePool;

		/**
		 * Dieser Konstruktor initialisiert {@link DecodeSource} und Cachegröße.
		 * 
		 * @param source {@link DecodeSource}.
		 * @param cacheSize maximaler Speicherverbrauch des Caches in Byte.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		public BEXDocumentView(final DecodeSource source, final int cacheSize) throws IOException {
			this.source = source;
			this.offset = source.index();
			this.array = new byte[16];
			this._attrUriPool = new ValuePool(source);
			this._attrNamePool = new ValuePool(source);
			this._attrValuePool = new ValuePool(source);
			this._elemUriPool = new ValuePool(source);
			this._elemNamePool = new ValuePool(source);
			this._elemValuePool = new ValuePool(source);
			this.elemValueCount = this._elemValuePool.size;

			this.attributesOffsets = this.readOffsets(source);
			this.attributesUriRefLength = Encoder2.computeLength(this._attrUriPool.size);
			this.attributesNameRefLength = Encoder2.computeLength(this._attrNamePool.size - 1);
			this.attributesValueRefLength = Encoder2.computeLength(this._attrValuePool.size - 1);
			this.attributesLength = this.attributesUriRefLength + this.attributesNameRefLength + this.attributesValueRefLength;
			this.attributesPool = this.skipEntries(source, this.attributesOffsets, this.attributesLength);

			this.childrenOffsets = this.readOffsets(source);
			this.childrenUriRefLength = Encoder2.computeLength(this._elemUriPool.size);
			this.childrenNameRefLength = Encoder2.computeLength(this._elemNamePool.size);
			this.childrenContentRefLength = Encoder2.computeLength((this._elemValuePool.size + this.childrenOffsets.length) - 1);
			this.childrenAttributesRefLength = Encoder2.computeLength(this.attributesOffsets.length - 1);
			this.childrenLength = this.childrenUriRefLength + this.childrenNameRefLength + this.childrenContentRefLength + this.childrenAttributesRefLength;
			this.childrenPool = this.skipEntries(source, this.childrenOffsets, this.childrenLength);
			this.childrenRef = this.read(source, this.childrenContentRefLength) - this.elemValueCount - 1;
			this.length = (int)(source.index() - this.offset);
			this.pageList = new FilePage[(this.length + 1023) / 1024];
			this.pageLimit = Math.max(cacheSize / 1024, 1);
			this.pageCount = 0;
		}

		/**
		 * Diese Methode gibt den {@link FilePage#data Nutzdatenblock} der {@link FilePage} mit dem gegebenen Index zurück.
		 * 
		 * @param index Index.
		 * @return {@link FilePage#data Nutzdatenblock}.
		 */
		byte[] data(final int index) {
			final FilePage[] pageList = this.pageList;
			FilePage page;
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
			page = new FilePage();
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

		int read(final DecodeSource source, final int size) throws IOException {
			final byte[] array = this.array;
			source.read(array, 0, size);
			return Decoder2.get(array, 0, size);
		}

		/**
		 * Diese Methode lödt die Startpositionen aus der gegebenen {@link DecodeSource} und gibt sie zurück.
		 * 
		 * @param source {@link DecodeSource}.
		 * @return Startpositionen.
		 * @throws IOException Wenn beim Lesen ein Fehler auftritt.
		 */
		int[] readOffsets(final DecodeSource source) throws IOException {
			final int size = this.read(source, 4) + 1, length = this.read(source, 1);
			final int[] offsets = new int[size];
			offsets[0] = 0;
			for(int i = 1; i < size; i++){
				offsets[i] = this.read(source, length);
			}
			return offsets;
		}

		int skipEntries(final DecodeSource source, final int[] offsets, final int length) throws IOException {
			final long index = source.index();
			source.seek(index + (offsets[offsets.length - 1] * length));
			return (int)(index - this.offset);
		}

		// /**
		// * Diese Methode gibt den Text mit dem gegebenen Schlüssel zurück.
		// *
		// * @param key Schlüssel des Texts.
		// * @param offsets Startpositionen.
		// * @param pool Beginn der Zeichen relativ zu {@link #offset}.
		// * @return Text.
		// */
		// String text(final int key, final int[] offsets, final int pool) {
		// if(key < 0) return null;
		// int offset = offsets[key];
		// final int size = offsets[key + 1] - offset;
		// offset += pool;
		// int page = offset / 1024;
		// final byte[] text = new byte[size];
		// offset = offset & 1023;
		// for(int i = 0; i < size; page++, offset = 0){
		// final int length = Math.min(size - i, 1024 - offset);
		// System.arraycopy(this.data(page), offset, text, i, length);
		// i += length;
		// }
		// return new String(text, Encoder2.TextItem.CHARSET);
		// }

		/**
		 * Diese Methode implementeirt {@link BEXElementView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		String elementUri(final int key) {
			return this._elemUriPool.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXElementView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		String elementName(final int key) {
			return this._elemNamePool.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXTextView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		String elementValue(final int key) {
			return this._elemValuePool.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#uri()}.
		 * 
		 * @param key Schlüssel des URI.
		 * @return URI.
		 */
		String attributeUri(final int key) {
			return this._attrUriPool.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#name()}.
		 * 
		 * @param key Schlüssel des Namen.
		 * @return Name.
		 */
		String attributeName(final int key) {
			return this._attrNamePool.get(key);
		}

		/**
		 * Diese Methode implementeirt {@link BEXAttributeView#value()}.
		 * 
		 * @param key Schlüssel des Werts.
		 * @return Wert.
		 */
		String attributeValue(final int key) {
			return this._attrValuePool.get(key);
		}

		/**
		 * Diese Methode implementiert {@link BEXChildrenView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Kindknoten.
		 */
		int childrenSize(final int key) {
			if(key < 0) return 0;
			final int[] offsets = this.childrenOffsets;
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
		ChildView childrenItem(final BEXParentView parent, final int key, final int index) {
			if(key < 0) return null;

			final int[] offsets = this.childrenOffsets;
			int offset = offsets[key] + index;
			if(offset >= offsets[key + 1]) return null;
			int length = this.childrenLength;
			offset = (offset * length) + this.childrenPool;
			final int page = offset / 1024;
			byte[] data = this.data(page);
			offset &= 1023;
			final int remain = 1024 - offset;
			if(length > remain){
				final byte[] array = this.array;
				System.arraycopy(data, offset, array, 0, remain);
				System.arraycopy(this.data(page + 1), 0, array, remain, length - remain);
				data = array;
				offset = 0;
			}
			final int uriRef = Decoder2.get(data, offset, length = this.childrenUriRefLength) - 1;
			offset += length;
			final int nameRef = Decoder2.get(data, offset, length = this.childrenNameRefLength) - 1;
			offset += length;
			final int contentRef = Decoder2.get(data, offset, length = this.childrenContentRefLength) - 1;
			offset += length;
			final int childrenRef = contentRef - this.elemValueCount;
			final int attributesRef = Decoder2.get(data, offset, this.childrenAttributesRefLength) - 1;
			if(nameRef < 0) return new BEXTextView(parent, index, contentRef);
			if(contentRef < 0) return new BEXElementView(parent, index, uriRef, nameRef, -1, attributesRef);
			if(childrenRef < 0) return new BEXElementTextView(parent, index, uriRef, nameRef, contentRef, attributesRef);
			return new BEXElementView(parent, index, uriRef, nameRef, childrenRef, attributesRef);
		}

		/**
		 * Diese Methode implementiert {@link BEXAttributesView#size()}.
		 * 
		 * @param key Schlüssel der Kindknotenliste.
		 * @return Anzahl der Attributknoten.
		 */
		int attributesSize(final int key) {
			if(key < 0) return 0;
			final int[] offsets = this.attributesOffsets;
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
		AttributeView attributesItem(final BEXElementView parent, final int key, final int index) {
			if(key < 0) return null;
			final int[] offsets = this.attributesOffsets;
			int offset = offsets[key] + index;
			if(offset >= offsets[key + 1]) return null;
			int length = this.attributesLength;
			offset = (offset * length) + this.attributesPool;
			final int page = offset / 1024;
			byte[] data = this.data(page);
			offset &= 1023;
			final int remain = 1024 - offset;
			if(length > remain){
				final byte[] array = this.array;
				System.arraycopy(data, offset, array, 0, remain);
				System.arraycopy(this.data(page + 1), 0, array, remain, length - remain);
				data = array;
				offset = 0;
			}
			final int uriRef = Decoder2.get(data, offset, length = this.attributesUriRefLength) - 1;
			offset += length;
			final int nameRef = Decoder2.get(data, offset, length = this.attributesNameRefLength);
			offset += length;
			final int valueRef = Decoder2.get(data, offset, this.attributesValueRefLength);
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

	static int get(final byte[] array, final int index, final int size) {
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

}