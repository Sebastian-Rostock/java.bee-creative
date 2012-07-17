package bee.creative.xml.fastdom;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import bee.creative.array.ArrayCopy;
import bee.creative.util.Comparators;
import bee.creative.util.Filter;
import bee.creative.util.Objects;
import bee.creative.xml.fastdom.Decoder.DecodeCache.DecodeDocumentCache;
import bee.creative.xml.fastdom.Encoder.EncodeValue;

/**
 * Diese Klasse implementiert Methoden zur Dekodierung eines XML-Dokuments aus einer optimierten binären Darstellung.
 * 
 * @see Encoder
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Decoder {

	/**
	 * Diese Klasse implementiert einen abstrakten Datensatz, der von einem {@link DecodeCache} erzeugt, aus einem
	 * {@link RandomAccessFile} geladen und in einem {@link Cache} verwaltet wird.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeItem {

		/**
		 * Dieses Feld speichert den Index dieses Datensatzes im {@link Cache}.
		 */
		public final int index;

		/**
		 * Dieser Konstrukteur initialisiert den Index dieses Datensatzes im {@link Cache}.
		 * 
		 * @param index Index dieses Datensatzes im {@link Cache}.
		 */
		public DecodeItem(final int index) {
			this.index = index;
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
	 * Diese Klasse implementiert einen {@link DecodeCache}, dessen Elemente in einem {@link Cache} gespeichert werden.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class DecodeItemPool<GItem> extends DecodeCache {

		/**
		 * Dieses Feld speichert die Größe eines Elements.
		 */
		public final int itemSize;

		/**
		 * Dieses Feld speichert die Anzahl der Elemente.
		 */
		public final int itemCount;

		/**
		 * Dieses Feld speichert den {@link Cache} der Elemente.
		 */
		public final Cache<GItem> itemCache;

		/**
		 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile} und die Größe eines Elements.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @param itemSize Größe eines Elements.
		 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
		 */
		public DecodeItemPool(final RandomAccessFile file, final int itemSize) throws IOException {
			super(file);
			final int itemCount = this.readIndex();
			file.skipBytes(itemSize * itemCount);
			this.itemSize = itemSize;
			this.itemCount = itemCount;
			this.itemCache = this.createItemCache();

		}

		/**
		 * Diese Methode navigiert im {@link RandomAccessFile} an die Position des {@code index}-ten Elements. Die Position
		 * ergibt sich aus {@code fileIndex + 4 + index * itemSize}.
		 * 
		 * @param index Index.
		 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
		 */
		protected final void seekItem(final int index) throws IOException {
			this.file.seek(this.fileIndex + (index * this.itemSize) + 4);
		}

		protected abstract GItem createItem(final int index);

		/**
		 * Diese Methode erzeugt den {@link Cache} der Elemente und gibt ihn zurück. Sie wird im Konstrukteur aufgerufen.
		 * 
		 * @return {@link Cache} der Elemente.
		 */
		protected Cache<GItem> createItemCache() {
			return new Cache<GItem>(1024,  4096, this.itemCount) {

				@Override
				public GItem createItem(int index) {
					return DecodeItemPool.this.createItem(index);
				}

			};
		}

		/**
		 * Diese Methode gibt das {@code index}-te Element zurück.
		 * 
		 * @param index Index.
		 * @return {@code index}-tes Element.
		 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
		 */
		public final GItem get(final int index) throws IndexOutOfBoundsException {
			return this.itemCache.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(true, this.getClass().getSimpleName(), this.itemCache);
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link DecodeItem} mit Indices auf anderer Datensätze.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static abstract class DecodeList extends DecodeItem {

		/**
		 * Dieses Feld speichert die Indices der Datensätze.
		 */
		public final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index Index dieses Datensatzes im {@link Cache}.
		 * @param indices Indices der Datensätze.
		 */
		public DecodeList(final int index, final int[] indices) {
			super(index);
			this.indices = indices;
		}

	}

	/**
	 * Diese Klasse implementiert das {@link DecodeItem} zur Abstraktion eines {@link String}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeValue extends DecodeItem {

		/**
		 * Dieses Feld speichert den {@link String}.
		 */
		public final String value;

		/**
		 * Dieser Konstrukteur initialisiert Index und {@link String}.
		 * 
		 * @param index Index dieses Datensatzes im {@link Cache}.
		 * @param value {@link String}.
		 */
		public DecodeValue(final int index, final String value) {
			super(index);
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeValue", this.index, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert {@link DecodeItem} mit {@code URI} und {@code Name}.
	 * 
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeLabel extends DecodeItem {

		/**
		 * Dieses Feld speichert den Index des {@code URI}-{@link DecodeValue}s.
		 * 
		 * @see Node#getNamespaceURI()
		 */
		public final int uri;

		/**
		 * Dieses Feld speichert den Index des {@code Name}-{@link DecodeValue}s.
		 * 
		 * @see Node#getLocalName()
		 */
		public final int name;

		/**
		 * Dieser Konstrukteur initialisiert die Indizes.
		 * 
		 * @param index Index.
		 * @param uriChars Index des {@code URI}-{@link DecodeValue}s ({@link Node#getNamespaceURI()}).
		 * @param nameChars Index des {@code Name}-{@link DecodeValue} ({@link Node#getLocalName()}).
		 */
		public DecodeLabel(final int index, final int uriChars, final int nameChars) {
			super(index);
			this.uri = uriChars;
			this.name = nameChars;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeLabel", this.index, this.uri, this.name);
		}

	}

	/**
	 * Diese Klasse implementiert das {@link Element}-{@link DecodeItem}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElement extends DecodeItem {

		/**
		 * Dieses Feld speichert den Index des {@link DecodeLabel}s.
		 * 
		 * @see Element#getLocalName()
		 * @see Element#getNamespaceURI()
		 */
		public final int label;

		/**
		 * Dieses Feld speichert den Index der {@link DecodeElementXmlns}.
		 * 
		 * @see Element#lookupPrefix(String)
		 * @see Element#lookupNamespaceURI(String)
		 */
		public final int xmlns;

		/**
		 * Dieses Feld speichert den Index der {@link DecodeElementChildren}.
		 * 
		 * @see Element#getChildNodes()
		 */
		public final int children;

		/**
		 * Dieses Feld speichert den Index der {@link DecodeElementAttributes}.
		 * 
		 * @see Element#getAttributes()
		 */
		public final int attributes;

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index Index dieses Datensatzes im {@link Cache}.
		 * @param label Index des {@link DecodeLabel}s ({@link Element#getLocalName()}, {@link Element#getNamespaceURI()}).
		 * @param xmlns Index des {@link DecodeElementXmlns} ({@link Element#lookupPrefix(String)},
		 *        {@link Element#lookupNamespaceURI(String)}).
		 * @param children Index der {@link DecodeElementChildren} ({@link Element#getChildNodes()}).
		 * @param attributes Index der {@link DecodeElementAttributes} ({@link Element#getAttributes()}).
		 */
		public DecodeElement(final int index, final int label, final int xmlns, final int children, final int attributes) {
			super(index);
			this.label = label;
			this.xmlns = xmlns;
			this.children = children;
			this.attributes = attributes;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeElement", this.index, this.label, this.xmlns, this.children, this.attributes);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link DecodeList} von {@link DecodeLabel}s.
	 * 
	 * @see Node#getLocalName()
	 * @see Node#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementXmlns extends DecodeList {

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index Index dieses {@link DecodeItem}s.
		 * @param indices Indices der {@link DecodeLabel}s.
		 */
		public DecodeElementXmlns(final int index, final int[] indices) {
			super(index, indices);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeElementXmlns", this.index, this.indices);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link DecodeList} von {@link DecodeItem}s.
	 * 
	 * @see Element#getChildNodes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementChildren extends DecodeList {

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index den Index dieses Datensatzes im {@link Cache}.
		 * @param indices Indices der {@link DecodeValue}s und {@link DecodeElement}s.
		 */
		public DecodeElementChildren(final int index, final int[] indices) {
			super(index, indices);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeElementChildren", this.index, this.indices);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link DecodeList} von {@link DecodeAttribute}s.
	 * 
	 * @see Element#getAttributes()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementAttributes extends DecodeList {

		/**
		 * Dieser Konstrukteur initialisiert die Indices.
		 * 
		 * @param index den Index dieses Datensatzes im {@link Cache}.
		 * @param indices Indices der {@link DecodeAttribute}s.
		 */
		public DecodeElementAttributes(final int index, final int[] indices) {
			super(index, indices);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeElementAttributes", this.index, this.indices);
		}

	}

	/**
	 * Diese Klasse implementiert das {@link Attr}-{@link DecodeItem}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeAttribute extends DecodeItem {

		/**
		 * Dieses Feld speichert den Index des {@link DecodeLabel}s.
		 * 
		 * @see Attr#getLocalName()
		 * @see Attr#getNamespaceURI()
		 */
		public final int label;

		/**
		 * Dieses Feld speichert den Index des {@code Value}-{@link DecodeValue}.
		 * 
		 * @see Attr#getNodeValue()
		 */
		public final int value;

		/**
		 * Dieser Konstrukteur initialisiert die Indizes.
		 * 
		 * @param index Index dieses Datensatzes im {@link Cache}.
		 * @param label Index des {@code Item}-{@link DecodeLabel}s ({@link Attr#getLocalName()},
		 *        {@link Attr#getNamespaceURI()}).
		 * @param value Index des {@code Value}-{@link DecodeValue} ({@link Attr#getNodeValue()}).
		 */
		public DecodeAttribute(final int index, final int label, final int value) {
			super(index);
			this.label = label;
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("DecodeAttribute", this.index, this.label, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert die Caches der {@link DecodeItem}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	// TODO: API erweitern mit sichtbarket/get/set für caches, cahceoptionen/factory...
	public static abstract class DecodeCache {

		/**
		 * Diese Klasse implementiert einen {@link DecodeCache}, dessen Elemente aus mehreren Werten bestehen.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		public static abstract class DecodeListCache<GItem> extends DecodeItemPool<GItem> {

			/**
			 * Dieses Feld speichert die Größe eines Werts.
			 */
			public final int valueSize;

			/**
			 * Dieses Feld speichert die Anzahl der Werte in den Elementen.
			 */
			public final int valueCount;

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile} und die Größe eines Werts.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @param valueSize Größe eines Werts.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			DecodeListCache(final RandomAccessFile file, final int valueSize) throws IOException {
				super(file, 4);
				this.valueSize = valueSize;
				final int valueCount = this.readIndex();
				this.valueCount = valueCount;
				file.skipBytes(valueSize * valueCount);
			}

			/**
			 * Diese Methode navigiert im {@link RandomAccessFile} an die Position des {@code index}-ten Werts. Die Position
			 * ergibt sich aus {@code fileIndex + 4 + itemSize * itemCount + 4 + index * valueSize}.
			 * 
			 * @param index Index.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			protected final void seekValue(final int index) throws IOException {
				this.file.seek(this.fileIndex + 4 + (this.itemSize * this.itemCount) + 4 + (index * this.valueSize));
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeCache} der {@link DecodeValue}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeCharsCache extends DecodeListCache<DecodeValue> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeCharsCache(final RandomAccessFile file) throws IOException {
				super(file, 1);
			}

			/**
			 * Diese Methode gibt das Ergebnis von {@link #findValue(String)} zurück und löst eine
			 * {@link IllegalArgumentException} aus, wenn kein Datensatz gefunden wird.
			 * 
			 * @see DecodeValue#value
			 * @see DecodeCharsCache#findValue(String)
			 * @param value {@link String}.
			 * @return {@link DecodeValue}.
			 * @throws IllegalArgumentException Wenn {@link #findValue(String)} {@code null} liefert.
			 */
			public DecodeValue getValue(final String value) throws IllegalArgumentException {
				final DecodeValue item = this.findValue(value);
				if(item == null) throw new IllegalArgumentException("value not found: " + value);
				return item;
			}

			/**
			 * Diese Methode sucht binäre nach dem {@link DecodeValue} mit dem gegebenen {@link String} und gibt diesen oder
			 * {@code null} zurück. Die {@link DecodeValue} müssen dazum im {@link Cache} nach ihrem {@link String}
			 * aufsteigend sortiert sein.
			 * 
			 * @see DecodeValue#value
			 * @param value {@link String}.
			 * @return {@link DecodeValue} oder {@code null}.
			 */
			public DecodeValue findValue(final String value) {
				if(this.itemCount == 0) return null;
				return this.itemCache.find(new Comparable<DecodeValue>() {

					@Override
					public int compareTo(final DecodeValue input) {
						return value.compareTo(input.value);
					}

				});
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeValue createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex2();
					this.seekValue(ints[0]);
					final int length = ints[1] - ints[0];
					final byte[] bytes = new byte[length];
					DecodeCache.read(this.file, bytes, 0, length);
					return new DecodeValue(index, new String(bytes, EncodeValue.CHARSET));
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeCache} der {@link DecodeLabel}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeLabelCache extends DecodeItemPool<DecodeLabel> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeLabelCache(final RandomAccessFile file) throws IOException {
				super(file, 8);
			}

			/**
			 * Diese Methode gibt das Ergebnis von {@link #findUri(int[], int)} zurück und löst eine
			 * {@link IllegalArgumentException} aus, wenn kein Datensatz gefunden wird.
			 * 
			 * @see DecodeLabel#name
			 * @see Cache#find(int[], Comparable)
			 * @param indices Index-Array.
			 * @param uri {@code URI}-Index.
			 * @return {@link DecodeLabel}.
			 * @throws IllegalArgumentException Wenn {@link #findUri(int[], int)} {@code null} liefert.
			 */
			public DecodeLabel getUri(final int[] indices, final int uri) throws IllegalArgumentException {
				final DecodeLabel item = this.findUri(indices, uri);
				if(item == null) throw new IllegalArgumentException("uri not found: " + uri);
				return item;
			}

			/**
			 * Diese Methode sucht binär im gegebenen Index-Array nach dem {@link DecodeLabel} mit dem gegebenen {@code URI}
			 * -Index und gibt diesen oder {@code null} zurück.
			 * 
			 * @see DecodeLabel#name
			 * @see Cache#find(int[], Comparable)
			 * @param indices Index-Array.
			 * @param uri {@code URI}-Index.
			 * @return {@link DecodeLabel} oder {@code null}.
			 */
			public DecodeLabel findUri(final int[] indices, final int uri) {
				return this.itemCache.find(indices, new Comparable<DecodeLabel>() {

					@Override
					public int compareTo(final DecodeLabel value) {
						return Comparators.compare(uri, value.uri);
					}

				});
			}

			/**
			 * Diese Methode sucht linear im gegebenen Index-Array nach dem {@link DecodeLabel} mit dem gegebenen {@code Name}
			 * -Index und gibt diesen oder {@code null} zurück.
			 * 
			 * @param indices Index-Array.
			 * @see DecodeLabel#name
			 * @param name {@code Name}-Index.
			 * @return {@link DecodeLabel} oder {@code null}.
			 */
			public DecodeLabel findName(final int[] indices, final int name) {
				if(this.itemCount == 0) return null;
				final Cache<DecodeLabel> cache = this.itemCache;
				for(int i = 0, size = indices.length; i < size; i++){
					final DecodeLabel value = cache.get(indices[i]);
					if(value.name == name) return value;
				}
				return null;
			}

			/**
			 * Diese Methode sucht binäre nach dem {@link DecodeLabel} mit den gegebenen {@code URI}- bzw. {@code Name}
			 * -Indices und gibt diesen oder {@code null} zurück. Die {@link DecodeLabel} müssen dazum im {@link Cache}
			 * aufsteigend sortiert sein.
			 * 
			 * @see Encoder#LabelComparator
			 * @see DecodeItem#index
			 * @see DecodeLabel#uri
			 * @see DecodeLabel#name
			 * @param uri {@code URI}-Index.
			 * @param name {@code Name}-Index.
			 * @return {@link DecodeLabel} oder {@code null}.
			 */
			public DecodeLabel findLabel(final int uri, final int name) {
				if(this.itemCount == 0) return null;
				return this.itemCache.find(new Comparable<DecodeLabel>() {

					@Override
					public int compareTo(final DecodeLabel input) {
						final int comp = Comparators.compare(name, input.name);
						if(comp != 0) return comp;
						return Comparators.compare(uri, input.uri);
					}

				});
			}

			/**
			 * Diese Methode sucht binäre im gegebenen Index-Array nach dem {@link DecodeLabel} mit den gegebenen {@code URI}-
			 * bzw. {@code Name}-Indices und gibt diesen oder {@code null} zurück. Die {@link DecodeLabel} müssen dazum im
			 * {@link Cache} aufsteigend sortiert sein.
			 * 
			 * @see Encoder#LabelComparator
			 * @see DecodeItem#index
			 * @see DecodeLabel#uri
			 * @see DecodeLabel#name
			 * @param indices Index-Array.
			 * @param uri {@code URI}-Index.
			 * @param name {@code Name}-Index.
			 * @return {@link DecodeLabel} oder {@code null}.
			 */
			public DecodeLabel findLabel(final int[] indices, final int uri, final int name) {
				if(this.itemCount == 0) return null;
				return this.itemCache.find(indices, new Comparable<DecodeLabel>() {

					@Override
					public int compareTo(final DecodeLabel input) {
						final int comp = Comparators.compare(name, input.name);
						if(comp != 0) return comp;
						return Comparators.compare(uri, input.uri);
					}

				});
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeLabel createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex2();
					return new DecodeLabel(index, ints[0], ints[1]);
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeCache} der {@link DecodeElement}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeElementCache extends DecodeItemPool<DecodeElement> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeElementCache(final RandomAccessFile file) throws IOException {
				super(file, 16);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeElement createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex4();
					return new DecodeElement(index, ints[0], ints[1], ints[2], ints[3]);
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeListCache} zu {@link DecodeElementXmlns}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeElementXmlnsCache extends DecodeListCache<DecodeElementXmlns> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeElementXmlnsCache(final RandomAccessFile file) throws IOException {
				super(file, 4);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeElementXmlns createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex2();
					this.seekValue(ints[0]);
					final int length = ints[1] - ints[0];
					final int[] indices = this.readIndices(length);
					return new DecodeElementXmlns(index, indices);
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeListCache} zu {@link DecodeElementChildren}.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeElementChildrenCache extends DecodeListCache<DecodeElementChildren> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeElementChildrenCache(final RandomAccessFile file) throws IOException {
				super(file, 4);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeElementChildren createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex2();
					this.seekValue(ints[0]);
					final int length = ints[1] - ints[0];
					final int[] indices = this.readIndices(length);
					return new DecodeElementChildren(index, indices);
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeCache} der {@link DecodeElementAttributes}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeElementAttributesCache extends DecodeListCache<DecodeElementAttributes> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeElementAttributesCache(final RandomAccessFile file) throws IOException {
				super(file, 4);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeElementAttributes createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex2();
					this.seekValue(ints[0]);
					final int length = ints[1] - ints[0];
					final int[] indices = this.readIndices(length);
					return new DecodeElementAttributes(index, indices);
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeCache} der {@link DecodeAttribute}s.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeAttributeCache extends DecodeItemPool<DecodeAttribute> {

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeAttributeCache(final RandomAccessFile file) throws IOException {
				super(file, 8);
			}

			/**
			 * Diese Methode sucht binäre im gegebenen Index-Array nach dem {@link DecodeAttribute} mit den gegebenen
			 * {@code Label}- -Index und gibt dieses oder {@code null} zurück. Die {@link DecodeAttribute} müssen dazum im
			 * Index-Array aufsteigend sortiert sein.
			 * 
			 * @see Encoder#LabelComparator
			 * @see DecodeItem#index
			 * @see DecodeLabel#uri
			 * @see DecodeLabel#name
			 * @param indices Index-Array.
			 * @param label {@code Label}-Index.
			 * @return {@link DecodeAttribute} oder {@code null}.
			 */
			public DecodeAttribute findLabel(final int[] indices, final int label) {
				if(this.itemCount == 0) return null;
				return this.itemCache.find(indices, new Comparable<DecodeAttribute>() {

					@Override
					public int compareTo(final DecodeAttribute value) {
						return Comparators.compare(label, value.label);
					}

				});
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public DecodeAttribute createItem(final int index) {
				try{
					this.seekItem(index);
					final int[] ints = this.readIndex2();
					return new DecodeAttribute(index, ints[0], ints[1]);
				}catch(final IOException e){
					throw new IllegalStateException(e);
				}
			}

		}

		/**
		 * Diese Klasse implementiert den {@link DecodeCache} des {@link DecodeDocumentAdapter}s als Sammlung aller
		 * {@link DecodeCache}s, die zur Abbildung eines {@link Document}s verwendet werden.
		 * 
		 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public static final class DecodeDocumentCache extends DecodeCache {

			/**
			 * Dieses Feld speichert den {@code UIR}-{@link DecodeCharsCache}.
			 * 
			 * @see Node#getNamespaceURI()
			 */
			public final DecodeCharsCache uriCharsCache;

			/**
			 * Dieses Feld speichert den {@code Value}-{@link DecodeCharsCache}.
			 * 
			 * @see Node#getNodeValue()
			 */
			public final DecodeCharsCache valuePool;

			/**
			 * Dieses Feld speichert den {@code Prefix}-{@link DecodeCharsCache}.
			 * 
			 * @see Node#getPrefix()
			 */
			public final DecodeCharsCache xmlnsCharsCache;

			/**
			 * Dieses Feld speichert den {@code URI/Prefix}-{@link DecodeLabelCache}.
			 * 
			 * @see Node#getPrefix()
			 * @see Node#getNamespaceURI()
			 */
			public final DecodeLabelCache xmlnsLabelCache;

			/**
			 * Dieses Feld speichert den {@link DecodeElementCache}.
			 * 
			 * @see Element
			 */
			public final DecodeElementCache elementCache;

			/**
			 * Dieses Feld speichert den {@code Element}-{@link DecodeCharsCache}.
			 * 
			 * @see Element#getLocalName()
			 */
			public final DecodeCharsCache elementCharsCache;

			/**
			 * Dieses Feld speichert den {@code URI/Element}-{@link DecodeLabelCache}.
			 * 
			 * @see Element#getLocalName()
			 * @see Element#getNamespaceURI()
			 */
			public final DecodeLabelCache elementLabelCache;

			/**
			 * Dieses Feld speichert den {@link DecodeElementXmlnsCache}.
			 * 
			 * @see Element#lookupPrefix(String)
			 * @see Element#lookupNamespaceURI(String)
			 */
			public final DecodeElementXmlnsCache elementXmlnsCache;

			/**
			 * Dieses Feld speichert den {@link DecodeElementChildrenCache}.
			 * 
			 * @see Element#getChildNodes()
			 */
			public final DecodeElementChildrenCache elementChildrenCache;

			/**
			 * Dieses Feld speichert den {@link DecodeElementAttributesCache}.
			 * 
			 * @see Element#getAttributes()
			 */
			public final DecodeElementAttributesCache elementAttributesCache;

			/**
			 * Dieses Feld speichert den {@link DecodeAttributeCache}.
			 * 
			 * @see Attr
			 */
			public final DecodeAttributeCache attributeCache;

			/**
			 * Dieses Feld speichert den {@code Attribute}-{@link DecodeCharsCache}.
			 * 
			 * @see Attr#getLocalName()
			 */
			public final DecodeCharsCache attributeCharsCache;

			/**
			 * Dieses Feld speichert den {@code URI/Attribute}-{@link DecodeLabelCache}.
			 * 
			 * @see Attr#getLocalName()
			 * @see Attr#getNamespaceURI()
			 */
			public final DecodeLabelCache attributeLabelCache;

			/**
			 * Dieses Feld speichert den Index des {@link DecodeElement}s für {@link Document#getDocumentElement()}.
			 */
			public final int documentElement;

			/**
			 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
			 * 
			 * @param file {@link RandomAccessFile}.
			 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
			 */
			public DecodeDocumentCache(final RandomAccessFile file) throws IOException {
				super(file);
				this.uriCharsCache = new DecodeCharsCache(file);
				this.xmlnsCharsCache = new DecodeCharsCache(file);
				this.elementCharsCache = new DecodeCharsCache(file);
				this.attributeCharsCache = new DecodeCharsCache(file);
				this.valuePool = new DecodeCharsCache(file);
				this.xmlnsLabelCache = new DecodeLabelCache(file);
				this.elementLabelCache = new DecodeLabelCache(file);
				this.attributeLabelCache = new DecodeLabelCache(file);
				this.elementXmlnsCache = new DecodeElementXmlnsCache(file);
				this.elementChildrenCache = new DecodeElementChildrenCache(file);
				this.elementAttributesCache = new DecodeElementAttributesCache(file);
				this.elementCache = new DecodeElementCache(file);
				this.attributeCache = new DecodeAttributeCache(file);
				this.documentElement = this.readIndex();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(true, true, "InputDocumentCache", //
					"documentElement", this.documentElement,//
					"uriCharsCache", this.uriCharsCache,//
					"xmlnsCharsCache", this.xmlnsCharsCache,//
					"elementCharsCache", this.elementCharsCache,//
					"attributeCharsCache", this.attributeCharsCache,//
					"valueCharsCache", this.valuePool,//
					"xmlnsLabelCache", this.xmlnsLabelCache,//
					"elementLabelCache", this.elementLabelCache,//
					"attributeLabelCache", this.attributeLabelCache,//
					"elementChildrenCache", this.elementChildrenCache,//
					"elementAttributesCache", this.elementAttributesCache,//
					"elementCache", this.elementCache,//
					"attributeCache", this.attributeCache //
					);
			}

		}

		/**
		 * Dieses Feld speichert den {@code byte}-Puffer zum Laden geringer Datenmengen.
		 */
		protected static final byte[] BYTES = new byte[16];

		/**
		 * Dieses Feld speichert den {@code int}-Puffer zum Laden geringer Datenmengen.
		 */
		protected static final int[] INTS = new int[4];

		static public final void read(final RandomAccessFile file, final byte[] value, final int offset, final int count)
			throws IOException {
			for(int read = 0; read != count;){
				read += file.read(value, offset + read, count - read);
			}
		}

		protected final int readIndex() throws IOException {
			final byte[] bytes = DecodeCache.BYTES;
			DecodeCache.read(this.file, bytes, 0, 4);
			return ArrayCopy.get4(bytes, 0);
		}

		protected final int[] readIndex2() throws IOException {
			final int[] ints = DecodeCache.INTS;
			final byte[] bytes = DecodeCache.BYTES;
			DecodeCache.read(this.file, bytes, 0, 8);
			ArrayCopy.copy(bytes, 0, ints, 0, 2);
			return ints;
		}

		protected final int[] readIndex4() throws IOException {
			final int[] ints = DecodeCache.INTS;
			final byte[] bytes = DecodeCache.BYTES;
			DecodeCache.read(this.file, bytes, 0, 16);
			ArrayCopy.copy(bytes, 0, ints, 0, 4);
			return ints;
		}

		protected final int[] readIndices(final int count) throws IOException {
			final int[] ints = new int[count];
			final int length = count << 2;
			final byte[] bytes = new byte[length];
			DecodeCache.read(this.file, bytes, 0, length);
			ArrayCopy.copy(bytes, 0, ints, 0, count);
			return ints;
		}

		/**
		 * Dieses Feld speichert das {@link RandomAccessFile}.
		 */
		public final RandomAccessFile file;

		/**
		 * Dieses Feld speichert die Position im {@link RandomAccessFile}, an der das Laden begonnen hat.
		 */
		public final long fileIndex;

		/**
		 * Dieser Konstrukteur initialisiert das {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
		 */
		public DecodeCache(final RandomAccessFile file) throws IOException {
			this.file = file;
			this.fileIndex = file.getFilePointer();
		}

	}

	/**
	 * Diese Klasse implementiert die Methoden zum Auslesen eines {@link DecodeDocumentCache}s als Grundlage eines
	 * {@link DecodeDocumentAdapter}s.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeAdapter {

		/**
		 * Dieses Feld speichert die leere {@link NodeList}.
		 */
		static public final NodeList VOID_NODE_LIST = new NodeList() {

			@Override
			public Node item(final int index) {
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
		 * Dieses Feld speichert den {@link DecodeDocumentCache}.
		 */
		public final DecodeDocumentCache cache;

		/**
		 * Dieses Feld speichert die Verschiebung des Indexes der {@link DecodeElement}s in den Indices der
		 * {@link DecodeElementChildren}. Der Wert entspricht der Anzahl der {@link DecodeText}s.
		 */
		public final int offset;

		/**
		 * Dieses Feld speichert den {@link DecodeDocumentAdapter}.
		 */
		public final DecodeDocumentAdapter adapter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeDocumentCache}.
		 * 
		 * @param cache {@link DecodeDocumentCache}.
		 */
		public DecodeAdapter(final DecodeDocumentCache cache) {
			this.cache = cache;
			this.offset = this.cache.valuePool.itemCount;
			this.adapter = new DecodeDocumentAdapter(this);
		}

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeDocumentCache} mit dem gegebenen {@link RandomAccessFile}.
		 * 
		 * @param file {@link RandomAccessFile}.
		 * @throws IOException Wenn das gegebene {@link RandomAccessFile} eine {@link IOException} auslöst.
		 */
		public DecodeAdapter(final RandomAccessFile file) throws IOException {
			this(new DecodeDocumentCache(file));
		}

		/**
		 * Diese Methode implementiert {@link Text#getData()}.
		 * 
		 * @param index Index des {@link DecodeValue}s.
		 * @return {@link Text#getData()}.
		 */
		public String textGetData(final int index) {
			final DecodeValue textValue = this.cache.valuePool.get(index);
			return textValue.value;
		}

		/**
		 * Diese Methode implementiert {@link Text#getLength()}.
		 * 
		 * @param index Index des {@link DecodeValue}s.
		 * @return {@link Text#getLength()}.
		 */
		public int textGetLength(final int index) {
			return this.textGetData(index).length();
		}

		/**
		 * Diese Methode implementiert {@link Element#getPrefix()}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @return {@link Element#getPrefix()}.
		 */
		public String elementGetPrefix(final int index) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeElementXmlns elementXmlns = this.cache.elementXmlnsCache.get(element.xmlns);
			final DecodeLabel nodeLabel = this.cache.elementLabelCache.get(element.label);
			final DecodeLabel xmlnsLabel = this.cache.xmlnsLabelCache.getUri(elementXmlns.indices, nodeLabel.uri);
			final DecodeValue xmlnsChars = this.cache.xmlnsCharsCache.get(xmlnsLabel.name);
			final String xmlnsValue = xmlnsChars.value;
			if(xmlnsValue.isEmpty()) return null;
			return xmlnsValue;
		}

		/**
		 * Diese Methode implementiert {@link Element#getNodeName()}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @return {@link Element#getNodeName()}.
		 */
		public String elementGetNodeName(final int index) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeElementXmlns elementXmlns = this.cache.elementXmlnsCache.get(element.xmlns);
			final DecodeLabel elementLabel = this.cache.elementLabelCache.get(element.label);
			final DecodeValue elementLabelName = this.cache.elementCharsCache.get(elementLabel.name);
			final DecodeLabel elementXmlnsLabel = this.cache.xmlnsLabelCache.getUri(elementXmlns.indices, elementLabel.uri);
			final DecodeValue elementXmlnsLabelName = this.cache.xmlnsCharsCache.get(elementXmlnsLabel.name);
			final String elementXmlnsLabelNameValue = elementXmlnsLabelName.value;
			if(elementXmlnsLabelNameValue.isEmpty()) return elementLabelName.value;
			return elementXmlnsLabelNameValue + ":" + elementLabelName.value;
		}

		/**
		 * Diese Methode implementiert {@link Element#getLocalName()}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @return {@link Element#getLocalName()}.
		 */
		public String elementGetLocalName(final int index) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeLabel nodeLabel = this.cache.elementLabelCache.get(element.label);
			final DecodeValue elementChars = this.cache.elementCharsCache.get(nodeLabel.name);
			return elementChars.value;
		}

		/**
		 * Diese Methode implementiert {@link Element#getNamespaceURI()}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @return {@link Element#getNamespaceURI()}.
		 */
		public String elementGetNamespaceURI(final int index) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeLabel elementLabel = this.cache.elementLabelCache.get(element.label);
			final DecodeValue uriChars = this.cache.uriCharsCache.get(elementLabel.uri);
			final String uriValue = uriChars.value;
			if(uriValue.isEmpty()) return null;
			return uriValue;
		}

		/**
		 * Diese Methode implementiert {@link Element#getElementsByTagName(String)} bzw.
		 * {@link Document#getElementsByTagName(String)}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}
		 * @param name {@code Name} ({@link Element#getLocalName()}).
		 * @param include {@code true}, wenn der gegebene {@code Parent}-{@link DecodeElementAdapter} in die Suche
		 *        einbezogen werden soll.
		 * @return {@link Element#getElementsByTagName(String)} bzw. {@link Document#getElementsByTagName(String)}.
		 */
		public NodeList elementGetElementsByTagName(final DecodeElementAdapter parent, final String name,
			final boolean include) {
			return this.elementGetElementsByTagName(parent, XMLConstants.NULL_NS_URI, name, include);
		}

		/**
		 * Diese Methode implementiert {@link Element#getElementsByTagNameNS(String, String)} bzw.
		 * {@link Document#getElementsByTagNameNS(String, String)}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}
		 * @param uri {@code Name} ({@link Element#getNamespaceURI()}).
		 * @param name {@code Name} ({@link Element#getLocalName()}).
		 * @param include {@code true}, wenn der gegebene {@code Parent}-{@link DecodeElementAdapter} in die Suche
		 *        einbezogen werden soll.
		 * @return {@link Element#getElementsByTagNameNS(String, String)} bzw.
		 *         {@link Document#getElementsByTagNameNS(String, String)}.
		 */
		public NodeList elementGetElementsByTagName(final DecodeElementAdapter parent, final String uri, final String name,
			final boolean include) {
			final DecodeElementCollector collector;
			if("*".equals(uri)){
				if("*".equals(name)){
					collector = new DecodeElementCollector(this);
				}else{
					final DecodeValue nameChars = this.cache.elementCharsCache.findValue(name);
					if(nameChars == null) return DecodeAdapter.VOID_NODE_LIST;
					collector = new DecodeElementNameCollector(this, nameChars.index);
				}
			}else{
				final DecodeValue uriChars = this.cache.uriCharsCache.findValue(uri);
				if(uriChars == null) return DecodeAdapter.VOID_NODE_LIST;
				if("*".equals(name)){
					collector = new DecodeElementUriCollector(this, uriChars.index);
				}else{
					final DecodeValue nameChars = this.cache.elementCharsCache.findValue(name);
					if(nameChars == null) return DecodeAdapter.VOID_NODE_LIST;
					final DecodeLabel elementlLabel = this.cache.elementLabelCache.findLabel(uriChars.index, nameChars.index);
					if(elementlLabel == null) return DecodeAdapter.VOID_NODE_LIST;
					collector = new DecodeElementLabelCollector(this, elementlLabel.index);
				}
			}
			collector.collect(parent, include);
			return collector;
		}

		/**
		 * Diese Methode implementiert {@link Element#getFirstChild()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#getFirstChild()}.
		 */
		public Node elementGetFirstChild(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementChildren elementChildren = this.cache.elementChildrenCache.get(element.children);
			final int[] indices = elementChildren.indices;
			if(indices.length == 0) return null;
			return this.elementGetChildNodesItem(parent, indices[0], 0);
		}

		/**
		 * Diese Methode implementiert {@link Element#getLastChild()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#getLastChild()}.
		 */
		public Node elementGetLastChild(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementChildren elementChildren = this.cache.elementChildrenCache.get(element.children);
			final int[] indices = elementChildren.indices;
			final int index = indices.length - 1;
			if(index < 0) return null;
			return this.elementGetChildNodesItem(parent, indices[index], index);
		}

		/**
		 * Diese Methode implementiert {@link Element#hasChildNodes()}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @return {@link Element#hasChildNodes()}.
		 */
		public boolean elementHasChildNodes(final int index) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeElementChildren elementChildren = this.cache.elementChildrenCache.get(element.children);
			return elementChildren.indices.length != 0;
		}

		/**
		 * Diese Methode implementiert {@link Element#getChildNodes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#getChildNodes()}.
		 */
		public DecodeElementChildrenAdapter elementGetChildNodes(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementChildren elementChildren = this.cache.elementChildrenCache.get(element.children);
			return new DecodeElementChildrenAdapter(parent, elementChildren.indices);
		}

		/**
		 * Diese Methode implementiert {@link NodeList#item(int)} für {@link Node#getChildNodes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeValue}s bzw. des {@link DecodeElement}s {@code + cache.textCache.itemCount}.
		 * @param child Index des {@code Child}-{@link Node}s in {@link Node#getChildNodes()}.
		 * @return {@code Child}-{@link Node} als {@link DecodeTextAdapter} bzw. {@link DecodeElementAdapter}.
		 */
		public Node elementGetChildNodesItem(final DecodeNodeAdapter parent, final int index, final int child) {
			final int offset = this.offset;
			if(index < offset) return new DecodeTextAdapter(parent, index, child);
			return new DecodeElementAdapter(parent, index - offset, child);
		}

		/**
		 * Diese Methode implementiert {@link Element#hasAttribute(String)}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param name {@code Name} ({@link Attr#getLocalName()}).
		 * @return {@link Element#hasAttribute(String)}.
		 */
		public boolean elementHasAttribute(final int index, final String name) {
			return this.elementHasAttribute(index, XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * Diese Methode implementiert {@link Element#getAttribute(String)}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param name {@code Name} ({@link Attr#getLocalName()}).
		 * @return {@link Element#getAttribute(String)}.
		 */
		public String elementGetAttribute(final int index, final String name) {
			return this.elementGetAttribute(index, XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * Diese Methode implementiert {@link Element#hasAttributeNS(String, String)}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param uri {@code URI} ({@link Attr#getNamespaceURI()}).
		 * @param name {@code Name} ({@link Attr#getLocalName()}).
		 * @return {@link Element#hasAttributeNS(String, String)}.
		 */
		public boolean elementHasAttribute(final int index, final String uri, final String name) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeElementAttributes elementAttributes = this.cache.elementAttributesCache.get(element.attributes);
			if(elementAttributes.indices.length == 0) return false;
			final DecodeValue uriChars = this.cache.uriCharsCache.findValue(uri);
			if(uriChars == null) return false;
			final DecodeValue nameChars = this.cache.attributeCharsCache.findValue(name);
			if(nameChars == null) return false;
			final DecodeLabel attributeLabel = this.cache.attributeLabelCache.findLabel(uriChars.index, nameChars.index);
			if(attributeLabel == null) return false;

			final DecodeAttribute attribute =
				this.cache.attributeCache.findLabel(elementAttributes.indices, attributeLabel.index);

			return attribute != null;
		}

		/**
		 * Diese Methode implementiert {@link Element#getAttributeNodeNS(String, String)}.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param uri {@code URI} ({@link Attr#getNamespaceURI()}).
		 * @param name {@code Name} ({@link Attr#getLocalName()}).
		 * @return {@link Element#getAttributeNodeNS(String, String)}.
		 */
		public String elementGetAttribute(final int index, final String uri, final String name) {
			final DecodeElement element = this.cache.elementCache.get(index);
			final DecodeElementAttributes elementAttributes = this.cache.elementAttributesCache.get(element.attributes);
			final int[] indices = elementAttributes.indices;
			if(indices.length == 0) return "";
			final DecodeValue uriChars = this.cache.uriCharsCache.findValue(uri);
			if(uriChars == null) return "";
			final DecodeValue nameChars = this.cache.attributeCharsCache.findValue(name);
			if(nameChars == null) return "";
			final DecodeLabel attributeLabel = this.cache.attributeLabelCache.findLabel(uriChars.index, nameChars.index);
			if(attributeLabel == null) return "";
			final DecodeAttribute attribute = this.cache.attributeCache.findLabel(indices, attributeLabel.index);
			final DecodeValue attributeChars = this.cache.valuePool.get(attribute.value);
			return attributeChars.value;
		}

		/**
		 * Diese Methode implementiert {@link NamedNodeMap#item(int)} für {@link Element#getAttributes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param index Index des {@link DecodeAttribute}s.
		 * @return {@link DecodeAttributeAdapter}.
		 */
		public Node elementGetAttributesItem(final DecodeElementAdapter parent, final int index) {
			return new DecodeAttributeAdapter(parent, index);
		}

		/**
		 * Diese Methode implementiert {@link NamedNodeMap#getNamedItem(String)} für {@link Element#getAttributes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param name {@code Name}.
		 * @return {@link DecodeAttributeAdapter}.
		 */
		public Attr elementGetAttributesNamedItem(final DecodeElementAdapter parent, final String name) {
			return this.elementGetAttributesNamedItem(parent, XMLConstants.NULL_NS_URI, name);
		}

		/**
		 * Diese Methode implementiert {@link NamedNodeMap#getNamedItemNS(String, String)} für
		 * {@link Element#getAttributes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param uri {@code URI}.
		 * @param name {@code Name}.
		 * @return {@link DecodeAttributeAdapter}.
		 */
		public Attr elementGetAttributesNamedItem(final DecodeElementAdapter parent, final String uri, final String name) {
			// TODO
			return null;
		}

		/**
		 * Diese Methode implementiert {@link Element#hasAttributes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#hasAttributes()}.
		 */
		public boolean elementHasAttributes(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementAttributes elementAttributes = this.cache.elementAttributesCache.get(element.attributes);
			return elementAttributes.indices.length != 0;
		}

		/**
		 * Diese Methode implementiert {@link Element#getAttributes()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @return {@link Element#getAttributes()}.
		 */
		public NamedNodeMap elementGetAttributes(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementAttributes elementAttributes = this.cache.elementAttributesCache.get(element.attributes);
			return new DecodeElementAttributesAdapter(parent, elementAttributes.indices);
		}

		/**
		 * Diese Methode implementiert {@link Element#lookupPrefix(String)} und gibt den {@code Prefix} zur gegebenen
		 * {@code URI} oder {@code null} zurück.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param uri {@code URI}.
		 * @return {@code Prefix} oder {@code null}.
		 */
		public String elementLookupPrefix(final int index, final String uri) {
			final DecodeValue uriChars = this.cache.uriCharsCache.findValue(uri);
			if(uriChars == null) return null;
			final DecodeElement node = this.cache.elementCache.get(index);
			final DecodeElementXmlns nodeXmlns = this.cache.elementXmlnsCache.get(node.xmlns);
			final DecodeLabel xmlnsLabel = this.cache.xmlnsLabelCache.findUri(nodeXmlns.indices, uriChars.index);
			if(xmlnsLabel == null) return null;
			final DecodeValue xmlnsChars = this.cache.xmlnsCharsCache.get(xmlnsLabel.name);
			return xmlnsChars.value;
		}

		/**
		 * Diese Methode implementiert {@link Element#lookupNamespaceURI(String)} und gibt die {@code URI} zum gegebenen
		 * {@code Prefix} oder {@code null} zurück.
		 * 
		 * @param index Index des {@link DecodeElement}s.
		 * @param name {@code Prefix}.
		 * @return {@code URI} oder {@code null}.
		 */
		public String elementLookupNamespaceURI(final int index, final String name) {
			final DecodeValue nameChars = this.cache.xmlnsCharsCache.findValue(name);
			if(nameChars == null) return null;
			final DecodeElement node = this.cache.elementCache.get(index);
			final DecodeElementXmlns nodeXmlns = this.cache.elementXmlnsCache.get(node.xmlns);
			final DecodeLabel xmlnsLabel = this.cache.xmlnsLabelCache.findName(nodeXmlns.indices, nameChars.index);
			if(xmlnsLabel == null) return null;
			final DecodeValue uriChars = this.cache.uriCharsCache.get(xmlnsLabel.uri);
			return uriChars.value;
		}

		/**
		 * Diese Methode implementiert {@link Attr#getPrefix()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param index Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getPrefix()}.
		 */
		public String attributeGetPrefix(final DecodeElementAdapter parent, final int index) {
			final DecodeAttribute attribute = this.cache.attributeCache.get(index);
			final DecodeLabel attributeLabel = this.cache.attributeLabelCache.get(attribute.label);
			if(attributeLabel.uri == 0) return null;
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementXmlns elementXmlns = this.cache.elementXmlnsCache.get(element.xmlns);
			final DecodeLabel xmlnsLabel = this.cache.xmlnsLabelCache.getUri(elementXmlns.indices, attributeLabel.uri);
			final DecodeValue xmlnsChars = this.cache.xmlnsCharsCache.get(xmlnsLabel.name);
			final String xmlnsValue = xmlnsChars.value;
			if(xmlnsValue.isEmpty()) return null;
			return xmlnsValue;
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNodeName()}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param index Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getNodeName()}.
		 */
		public String attributeGetNodeName(final DecodeElementAdapter parent, final int index) {
			final DecodeAttribute attribute = this.cache.attributeCache.get(index);
			final DecodeLabel attributeLabel = this.cache.attributeLabelCache.get(attribute.label);
			final DecodeValue attributeChars = this.cache.attributeCharsCache.get(attributeLabel.name);
			if(attributeLabel.uri == 0) return attributeChars.value;
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			final DecodeElementXmlns elementXmlns = this.cache.elementXmlnsCache.get(element.xmlns);
			final DecodeLabel xmlnsLabel = this.cache.xmlnsLabelCache.getUri(elementXmlns.indices, attributeLabel.uri);
			final DecodeValue xmlnsChars = this.cache.xmlnsCharsCache.get(xmlnsLabel.name);
			final String xmlnsValue = xmlnsChars.value;
			if(xmlnsValue.isEmpty()) return attributeChars.value;
			return xmlnsValue + ":" + attributeChars.value;
		}

		/**
		 * Diese Methode implementiert {@link Attr#getLocalName()}.
		 * 
		 * @param index Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getLocalName()}.
		 */
		public String attributeLabelName(final int index) {
			final DecodeAttribute attribute = this.cache.attributeCache.get(index);
			final DecodeLabel attributeLabel = this.cache.attributeLabelCache.get(attribute.label);
			final DecodeValue attributeChars = this.cache.attributeCharsCache.get(attributeLabel.name);
			return attributeChars.value;
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNamespaceURI()}.
		 * 
		 * @param index Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getNamespaceURI()}.
		 */
		public String attributeGetNamespaceURI(final int index) {
			final DecodeAttribute attribute = this.cache.attributeCache.get(index);
			final DecodeLabel attributeLabel = this.cache.attributeLabelCache.get(attribute.label);
			final DecodeValue uriChars = this.cache.uriCharsCache.get(attributeLabel.uri);
			final String uriValue = uriChars.value;
			if(uriValue.isEmpty()) return null;
			return uriValue;
		}

		/**
		 * Diese Methode implementiert {@link Attr#getNodeValue()}.
		 * 
		 * @param index Index des {@link DecodeAttribute}s.
		 * @return {@link Attr#getNodeValue()}.
		 */
		public String attributeGetNodeValue(final int index) {
			final DecodeAttribute attribute = this.cache.attributeCache.get(index);
			final DecodeValue valueChars = this.cache.valuePool.get(attribute.value);
			return valueChars.value;
		}

		/**
		 * Diese Methode gibt das {@link Document} zurück.
		 * 
		 * @return {@link Document}.
		 */
		public DecodeDocumentAdapter document() {
			return this.adapter;
		}
		
		@Override
		public String toString() {
			return cache.toString();
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
		public NodeList getChildNodes() {
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
			return this.adapter().document();
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
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeText}s und
		 * {@code Child-Index}.
		 * 
		 * @param parent {@code Parent}-{@link DecodeNodeAdapter}.
		 * @param index Index des {@link DecodeText}s.
		 * @param child {@code Child-Index}.
		 */
		public DecodeTextAdapter(final DecodeNodeAdapter parent, final int index, final int child) {
			super(parent, index, child);
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
			return this.adapter().textGetData(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getData() throws DOMException {
			return this.adapter().textGetData(this.index);
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
			return this.adapter().textGetLength(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getTextContent() throws DOMException {
			return this.adapter().textGetData(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getWholeText() {
			return this.adapter().textGetData(this.index);
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
			return Objects.toStringCall("InputDomText", this.index);
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
		public final DecodeNodeAdapter parent;

		/**
		 * Dieses Feld speichert dne Index des {@link DecodeItem}s.
		 */
		public final int index;

		/**
		 * Dieses Feld speichert den {@code Child-Index}.
		 * 
		 * @see Node#getChildNodes()
		 * @see NodeList#item(int)
		 */
		public final int child;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeNodeAdapter}, Index des {@link DecodeText}s und
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
		public Node getPreviousSibling() {
			final Node parent = this.parent;
			if(parent == null) return null;
			final NodeList children = parent.getChildNodes();
			if(children == null) return null;
			return children.item(this.child - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getNextSibling() {
			final Node parent = this.parent;
			if(parent == null) return null;
			final NodeList children = parent.getChildNodes();
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
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefix() {
			return this.adapter().elementGetPrefix(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamespaceURI() {
			return this.adapter().elementGetNamespaceURI(this.index);
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
			return this.adapter().elementGetNodeName(this.index);
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
			return this.adapter().elementGetNodeName(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getLocalName() {
			return this.adapter().elementGetLocalName(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getFirstChild() {
			return this.adapter().elementGetFirstChild(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getLastChild() {
			return this.adapter().elementGetLastChild(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeList getChildNodes() {
			return this.adapter().elementGetChildNodes(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasChildNodes() {
			return this.adapter().elementHasChildNodes(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NamedNodeMap getAttributes() {
			return this.adapter().elementGetAttributes(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributes() {
			return this.adapter().elementHasAttributes(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getAttribute(final String name) {
			return this.adapter().elementGetAttribute(this.index, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttribute(final String name) {
			return this.adapter().elementHasAttribute(this.index, name);
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
			return this.adapter().elementGetAttribute(this.index, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasAttributeNS(final String uri, final String name) throws DOMException {
			return this.adapter().elementHasAttribute(this.index, uri, name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value)
			throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Attr getAttributeNode(final String name) {
			return this.adapter().elementGetAttributesNamedItem(this, name);
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
		public Attr getAttributeNodeNS(final String uri, final String name) throws DOMException {
			return this.adapter().elementGetAttributesNamedItem(this, uri, name);
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
		public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean isId)
			throws DOMException {
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
			return null;
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
			final String value = this.adapter().elementLookupPrefix(this.index, uri);
			if((value == null) || value.isEmpty()) return null;
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String lookupNamespaceURI(final String prefix) {
			return this.adapter().elementLookupNamespaceURI(this.index, prefix);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isDefaultNamespace(final String uri) {
			return uri.equals(this.adapter().elementLookupNamespaceURI(this.index, XMLConstants.NULL_NS_URI));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeList getElementsByTagName(final String name) {
			return this.adapter().elementGetElementsByTagName(this, name, false);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeList getElementsByTagNameNS(final String uri, final String name) throws DOMException {
			return this.adapter().elementGetElementsByTagName(this, uri, name, false);
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
			return "< {" + this.getNamespaceURI() + "}" + this.getLocalName() + " " + this.getAttributes() + " >";
		}

	}

	/**
	 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getChildNodes()}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementChildrenAdapter implements NodeList, Iterable<Node> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementAdapter}.
		 */
		public final DecodeElementAdapter parent;

		/**
		 * Dieses Feld speichert die Indices der {@link DecodeText}s und {@link DecodeElement}s.
		 * 
		 * @see DecodeAdapter#offset
		 */
		public final int[] indices;

		/**
		 * Dieser Konstrukteur initialisiert {@code Parent}-{@link DecodeElementAdapter} und Indices der {@link DecodeText}s
		 * und {@link DecodeElement}s.
		 * 
		 * @param parent {@code Parent}-{@link DecodeElementAdapter}.
		 * @param indices Indices der {@link DecodeText}s und {@link DecodeElement}s.
		 */
		public DecodeElementChildrenAdapter(final DecodeElementAdapter parent, final int[] indices) {
			this.parent = parent;
			this.indices = indices;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node item(final int index) {
			if(index < 0) return null;
			final int[] indices = this.indices;
			if(index >= indices.length) return null;
			return this.parent.adapter().elementGetChildNodesItem(this.parent, indices[index], index);
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
		public Iterator<Node> iterator() {
			return new Iterator<Node>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < DecodeElementChildrenAdapter.this.indices.length;
				}

				@Override
				public Node next() {
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
	public static final class DecodeElementAttributesAdapter implements NamedNodeMap, Iterable<Node> {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementAdapter}.
		 */
		public final DecodeElementAdapter parent;

		/**
		 * Dieses Feld speichert die Indices der {@link DecodeAttribute}s.
		 */
		public final int[] indices;

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
		 * {@inheritDoc}
		 */
		@Override
		public Node item(final int index) {
			if(index < 0) return null;
			final int[] indices = this.indices;
			if(index >= indices.length) return null;
			return this.parent.adapter().elementGetAttributesItem(this.parent, indices[index]);
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
		public Node getNamedItem(final String name) {
			return this.parent.adapter().elementGetAttributesNamedItem(this.parent, name);
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
		public Node getNamedItemNS(final String uri, final String name) throws DOMException {
			return this.parent.adapter().elementGetAttributesNamedItem(this.parent, uri, name);
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
		public Iterator<Node> iterator() {
			return new Iterator<Node>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < DecodeElementAttributesAdapter.this.indices.length;
				}

				@Override
				public Node next() {
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
	public static class DecodeElementCollector implements Filter<DecodeElement>, NodeList {

		/**
		 * Dieses Feld speichert den {@link DecodeDocumentCache}.
		 */
		public final DecodeDocumentCache cache;

		/**
		 * Dieses Feld speichert die Verschiebung des Indexes der {@link DecodeElement}s in den Indices der
		 * {@link DecodeElementChildren}. Der Wert entspricht der Anzahl der {@link DecodeText}s.
		 */
		public final int offset;

		/**
		 * Dieses Feld speichert die Ergebnisse der Suche.
		 */
		public final List<DecodeElementAdapter> results;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeElementCollector} mit den Daten des {@link DecodeAdapter}s.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 */
		public DecodeElementCollector(final DecodeAdapter adapter) {
			this.cache = adapter.cache;
			this.offset = adapter.offset;
			this.results = new ArrayList<DecodeElementAdapter>();
		}

		/**
		 * Diese Methode Sucht in den {@link Element#getChildNodes()} des gegebenen {@link DecodeElementAdapter}s nach
		 * {@link Element}s, die von der Methode {@link #accept(DecodeElement)} akzeptiert werden und speichert die Treffer
		 * in die {@link List} {@link #results}. Der gegebene {@link DecodeElementAdapter} wird hierbei in die Suche mit
		 * einbezogen.
		 * 
		 * @param parent {@link DecodeElementAdapter}.
		 */
		protected final void collectChild(final DecodeElementAdapter parent) {
			final DecodeElement element = this.cache.elementCache.get(parent.index);
			if(this.accept(element)){
				this.results.add(parent);
			}
			this.collectChildren(parent, element);
		}

		/**
		 * Diese Methode Sucht in den {@link DecodeElementChildren} des gegebenen {@link DecodeElement}s nach
		 * {@link DecodeElement}s, die von der Methode {@link #accept(DecodeElement)} akzeptiert werden und speichert die
		 * {@link DecodeElementAdapter} der Treffer in die {@link List} {@link #results}.
		 * 
		 * @param parent {@link DecodeElementAdapter}.
		 * @param element {@link DecodeElement}.
		 */
		protected final void collectChildren(final DecodeElementAdapter parent, final DecodeElement element) {
			final DecodeElementChildren elementChildren = this.cache.elementChildrenCache.get(element.children);
			final int[] indices = elementChildren.indices;
			final int count = indices.length;
			if(count == 0) return;
			for(int child = 0, offset = this.offset; child < count; child++){
				final int index = indices[child] - offset;
				if(index >= 0){
					this.collectChild(new DecodeElementAdapter(parent, index, child));
				}
			}
		}

		/**
		 * Diese Methode leert die Ergebnisse.
		 */
		public final void clear() {
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
		 * in die {@link List} {@link #results}.
		 * 
		 * @param parent {@link DecodeElementAdapter}.
		 * @param include {@code true}, wenn der gegebene {@link DecodeElementAdapter} selbst in die Suche einbezogen werden
		 *        soll.
		 */
		public final void collect(final DecodeElementAdapter parent, final boolean include) {
			if(include){
				this.collectChild(parent);
			}else{
				this.collectChildren(parent, this.cache.elementCache.get(parent.index));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Node item(final int index) {
			if((index < 0) || (index >= this.results.size())) return null;
			return this.results.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final int getLength() {
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
	 * Diese Klasse implementiert einen {@link DecodeElementCollector}, der die {@link Element}s an Hand ihrer {@code URI}
	 * filtert.
	 * 
	 * @see Element#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementUriCollector extends DecodeElementCollector {

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
		public DecodeElementUriCollector(final DecodeAdapter adapter, final int uri) {
			super(adapter);
			this.uri = uri;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			final DecodeLabel elementLabel = this.cache.elementLabelCache.get(element.label);
			return elementLabel.uri == this.uri;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeElementCollector}, der die {@link Element}s an Hand ihres
	 * {@code Name} filtert.
	 * 
	 * @see Element#getLocalName()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementNameCollector extends DecodeElementCollector {

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
		public DecodeElementNameCollector(final DecodeAdapter adapter, final int name) {
			super(adapter);
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final DecodeElement element) {
			final DecodeLabel elementLabel = this.cache.elementLabelCache.get(element.label);
			return elementLabel.name == this.name;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link DecodeElementCollector}, der die {@link Element}s an Hand ihrer {@code URI}
	 * und ihres {@code Name} filtert.
	 * 
	 * @see Element#getLocalName()
	 * @see Element#getNamespaceURI()
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeElementLabelCollector extends DecodeElementCollector {

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
		public DecodeElementLabelCollector(final DecodeAdapter adapter, final int label) {
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
	 * Diese Klasse implementiert ein {@link Attr} als {@link DecodeNodeAdapter}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class DecodeAttributeAdapter extends DecodeNodeAdapter implements Attr {

		/**
		 * Dieses Feld speichert den {@code Parent}-{@link DecodeElementAdapter}.
		 */
		public final DecodeElementAdapter parent;

		/**
		 * Dieses Feld speichert den {@link DecodeAttribute}-{@code Index}.
		 */
		public final int index;

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
		 * {@inheritDoc}
		 */
		@Override
		public String getPrefix() {
			return this.adapter().attributeGetPrefix(this.parent, this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNamespaceURI() {
			return this.adapter().attributeGetNamespaceURI(this.index);
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
			return this.adapter().attributeGetNodeName(this.parent, this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getNodeValue() throws DOMException {
			return this.adapter().attributeGetNodeValue(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getLocalName() {
			return this.adapter().attributeLabelName(this.index);
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
			return "{" + this.getNamespaceURI() + "}" + this.getLocalName() + "=" + Objects.toString(this.getNodeValue());
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Document} als {@link DecodeNodeAdapter}, der auf den Daten eines
	 * {@link DecodeAdapter}s arbeitet.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static class DecodeDocumentAdapter extends DecodeNodeAdapter implements Document, NodeList {

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
		public static final List<String> VOID_DOM_CONFIGURATION_PARAMETER_TRUE_LIST = Collections.unmodifiableList(Arrays
			.asList("comments", "datatype-normalization", "well-formed", "namespaces", "namespace-declarations",
				"element-content-whitespace"));

		/**
		 * Dieses Feld speichert die {@code false-Parameter} der leeren {@link DOMConfiguration}.
		 * 
		 * @see DOMConfiguration#getParameter(String)
		 */
		public static final List<String> VOID_DOM_CONFIGURATION_PARAMETER_FALSE_LIST = Collections.unmodifiableList(Arrays
			.asList("cdata-sections", "entities", "split-cdata-sections", "validate", "infoset", "normalize-characters",
				"canonical-form", "validate-if-schema", "check-character-normalization"));

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
			public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId)
				throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

			@Override
			public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype)
				throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

		};

		/**
		 * Dieses Feld speichert den {@link DecodeAdapter}.
		 */
		final DecodeAdapter adapter;

		/**
		 * Dieses Feld speichert den {@link DecodeElementAdapter} für {@link #getDocumentElement()}.
		 */
		final DecodeElementAdapter documentElement;

		/**
		 * Dieser Konstrukteur initialisiert den {@link DecodeAdapter}.
		 * 
		 * @param adapter {@link DecodeAdapter}.
		 */
		public DecodeDocumentAdapter(final DecodeAdapter adapter) {
			this.adapter = adapter;
			this.documentElement = new DecodeElementAdapter(this, adapter.cache.documentElement, 0);
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
		public Node item(final int index) {
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
		public Node getFirstChild() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Node getLastChild() {
			return this.documentElement;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeList getChildNodes() {
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
		public NodeList getElementsByTagName(final String name) {
			return this.adapter().elementGetElementsByTagName(this.documentElement, name, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NodeList getElementsByTagNameNS(final String uri, final String name) {
			return this.adapter().elementGetElementsByTagName(this.documentElement, uri, name, true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element getDocumentElement() {
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
		public ProcessingInstruction createProcessingInstruction(final String target, final String data)
			throws DOMException {
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
	 * Dieser Konstrukteur initialisiert den {@link Decoder}.
	 */
	public Decoder() {
	}

	/**
	 * Diese Methode liest das gegebene {@link RandomAccessFile} in einen neuen {@link DecodeAdapter} ein und gibt dessen
	 * {@link DecodeDocumentAdapter} zurück.
	 * 
	 * @param source {@link RandomAccessFile}.
	 * @return {@link DecodeDocumentAdapter}.
	 * @throws IOException Wenn das {@link RandomAccessFile} eine {@link IOException} auslöst.
	 */
	public DecodeDocumentAdapter decode(final RandomAccessFile source) throws IOException {
		return new DecodeAdapter(source).document();
	}

}
