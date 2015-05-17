package bee.creative.bex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import bee.creative.bex.BEX.BEXBaseFile;
import bee.creative.bex.BEX.BEXBaseList;
import bee.creative.bex.BEX.BEXBaseNode;
import bee.creative.iam.IAMEncoder;
import bee.creative.util.Comparators;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueMap;

public class BEXEncoder extends IAMEncoder {

	public static void main(final String[] args) throws Exception {
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setCoalescing(true);
		dbf.setIgnoringComments(true);
		dbf.setXIncludeAware(true);
		final DocumentBuilder db = dbf.newDocumentBuilder();
		final Document doc = db.parse(new File("bex.xml"));
		final BEXFileEncoder encoder = new BEXFileEncoder();
		final BEXNodeEncoder e = encoder.putRoot(doc);
		System.out.println(e);

	}

	/**
	 * Diese Klasse implementiert einen Datensatz, dem ein identifizierender {@link #key} zugeordnet werden kann.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static class BEXItem {

		/**
		 * Dieses Feld speichert den Schlüssel zur Referenzierung.
		 */
		public int key;

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "@" + this.key;
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link UniqueSet} zur Verwaltung einzigartiger {@link BEXItem}s mit beliebigen Nutzdaten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der {@link BEXItem}s.
	 */
	static abstract class BEXItemMap<GIn, GItem extends BEXItem> extends UniqueMap<GIn, GItem> {

		public List<GItem> items = new ArrayList<>();

		{}

		public GItem put(final GItem item) {
			this.items.add(item);
			return item;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void reuse(final GIn input, final GItem output) {
			output.key--;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.items);
		}

	}

	static class BEXTextItem extends BEXItem {

		public static final Comparator<BEXTextItem> COMPARATOR = new Comparator<BEXEncoder.BEXTextItem>() {

			@Override
			public int compare(final BEXTextItem o1, final BEXTextItem o2) {
				if (o1.text.isEmpty()) return -1;
				if (o2.text.isEmpty()) return +1;
				return Comparators.compare(o1.key, o2.key);
			}

		};

		/**
		 * Dieses Feld speichert den Textwert.
		 */
		public String text;

		/**
		 * Dieser Konstruktor initialisiert den Textwert.
		 * 
		 * @param text Textwert.
		 */
		public BEXTextItem(final String text) {
			this.text = text != null ? text : "";
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "@" + this.key + ":" + Objects.toString(this.text);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link BEXItemMap} der {@link BEXTextItem}s zur verwaltung einzigartiger Textwerte.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class BEXTextPool extends BEXItemMap<String, BEXTextItem> {

		public BEXTextPool() {
			this.get(null);
		}

		@Override
		public BEXTextItem get(final String input) throws NullPointerException {
			return super.get(input != null ? input : "");
		}

		@Override
		protected BEXTextItem compile(final String input) {
			return this.put(new BEXTextItem(input));
		}

	}

	static final class BEXGroupItem extends BEXItem {

		public static final Comparator<BEXGroupItem> COMPARATOR = new Comparator<BEXGroupItem>() {

			@Override
			public int compare(final BEXGroupItem o1, final BEXGroupItem o2) {
				if (o1.items.isEmpty()) return -1;
				if (o2.items.isEmpty()) return +1;
				return Comparators.compare(o1.key, o2.key);
			}

		};

		public int offset;

		/**
		 * Dieses Feld speichert die Kind- bzw. Attributknotenliste.<br>
		 * ein Knoten besteht immer aus 5 auf einander folgenden Elementen: uri, name, value/content/children, attributes, parent.
		 */
		public List<BEXItem> items;

		/**
		 * Dieser Konstruktor initialisiert die Kind- bzw. Attributknotenliste.
		 * 
		 * @param group Kind- bzw. Attributknotenliste.
		 */
		public BEXGroupItem(final List<BEXItem> group) {
			this.items = group;
		}

		public BEXGroupItem(final int i) {
		}

		{}

		@Override
		public String toString() {
			return "@" + this.key + ":" + Objects.toString(this.items);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link BEXItemMap} der {@link BEXGroupItem}s zur Verwaltung einzigartiger Kind- bzw. Attributknotenlisten.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class BEXGrpupPool extends BEXItemMap<List<BEXItem>, BEXGroupItem> {

		public BEXGrpupPool() {
			this.get(Collections.<BEXItem>emptyList());
		}

		@Override
		protected BEXGroupItem compile(final List<BEXItem> input) {
			return this.put(new BEXGroupItem(input));
		}

	}

	static final class BEXNodeData extends BEXItem {

		public BEXGroupItem list;

		public int index;

		protected BEXNodeData(final BEXGroupItem list, final int index) {
			this.list = list;
			this.index = index;
		}

		{}

		@Override
		public int hashCode() {
			return this.index;
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXNodeData)) return false;
			final BEXNodeData data = (BEXNodeData)object;
			return (this.index == data.index) && (this.list == data.list);
		}

	}

	{}

	public static final class BEXFileEncoder extends BEXBaseFile {

		{}

		/**
		 * Dieses Feld speichert alle via {@link #newNode(int)} erzeugten Knoten.
		 */
		final List<BEXNodeEncoder> nodeList = new ArrayList<>();

		/**
		 * Dieses Feld speichert den virtuellen Knoten des Dokuments.
		 */
		final BEXNodeEncoder docuNode = new BEXNodeEncoder(this, BEX.BEX_ELEM_NODE, 0);

		{}

		/**
		 * Dieses Feld speichert den Puffer zur Zusammenfassung benachbarter Textknoten.
		 */
		final StringBuilder textValue = new StringBuilder();

		BEXTextPool attrUriText = new BEXTextPool();

		BEXTextPool attrNameText = new BEXTextPool();

		BEXTextPool attrValueText = new BEXTextPool();

		BEXTextPool chldUriText = new BEXTextPool();

		BEXTextPool chldNameText = new BEXTextPool();

		BEXTextPool chldValueText = new BEXTextPool();

		BEXGrpupPool attrTable = new BEXGrpupPool();

		BEXGrpupPool chldTable = new BEXGrpupPool();

		{}

		protected boolean putActive;

		protected boolean attrUriEnabled = true;

		protected boolean attrParentEnabled;

		protected boolean chldUriEnabled = true;

		protected boolean chldParentEnabled;

		{}

		/**
		 * Diese Methode fügt einen neuen Knoten mit der gegebenen Typkennung an {@link #nodeList} an und gibt ihn zurück.
		 * 
		 * @param type Typkennung ({@link BEX#BEX_ELEM_NODE}, {@link BEX#BEX_TEXT_NODE}, {@link BEX#BEX_ATTR_NODE}).
		 * @return Knoten.
		 */
		BEXNodeEncoder newNode(final int type) {
			final BEXNodeEncoder result = new BEXNodeEncoder(this, type, this.nodeList.size());
			this.nodeList.add(result);
			return result;
		}

		public BEXNode putRoot(final File file) throws IOException, SAXException, NullPointerException, IllegalStateException {
			try (FileInputStream stream = new FileInputStream(file)) {
				return this.putRoot(new InputSource(stream), XMLReaderFactory.createXMLReader());
			}
		}

		public BEXNodeEncoder putRoot(final Document node) throws NullPointerException, IllegalStateException {
			if (!this.docuNode.children.isEmpty()) throw new IllegalStateException();
			final BEXNodeEncoder result = this.docuNode.addElem(node.getDocumentElement());
			result.parent = null;
			return result;
		}

		public BEXNode putRoot(final InputSource source, final XMLReader reader) throws IOException, SAXException, NullPointerException, IllegalStateException {
			if (!this.docuNode.children.isEmpty()) throw new IllegalStateException();
			reader.setContentHandler(new ContentHandler() {

				@Override
				public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
				}

				@Override
				public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
				}

				@Override
				public void startDocument() throws SAXException {
				}

				@Override
				public void skippedEntity(final String name) throws SAXException {
				}

				@Override
				public void setDocumentLocator(final Locator locator) {
				}

				@Override
				public void processingInstruction(final String target, final String data) throws SAXException {
				}

				@Override
				public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
					BEXFileEncoder.this.textValue.append(ch, start, length);
				}

				@Override
				public void endPrefixMapping(final String prefix) throws SAXException {
				}

				@Override
				public void endElement(final String uri, final String localName, final String qName) throws SAXException {
				}

				@Override
				public void endDocument() throws SAXException {
				}

				@Override
				public void characters(final char[] ch, final int start, final int length) throws SAXException {
					BEXFileEncoder.this.textValue.append(ch, start, length);
				}

			});
			reader.parse(source);
			return null;
		}

		public BEXNodeEncoder putRoot(final String uri, final String name) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (!this.docuNode.children.isEmpty()) throw new IllegalStateException();
			final BEXNodeEncoder result = this.docuNode.addElem(uri, name);
			result.parent = null;
			return result;
		}

		{}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn Attributknoten einen URI haben dürfen. Andernfalls werden die URI von Attributknoten beim Kodieren
		 * ignoriert.
		 * 
		 * @see #setAttrUriEnabled(boolean)
		 * @return {@code true}, wenn Attributknoten einen URI haben dürfen.
		 */
		public boolean isAttrUriEnabled() {
			return this.attrUriEnabled;
		}

		public boolean isAttrParentEnabled() {
			return this.attrParentEnabled;
		}

		public boolean isChldUriEnabled() {
			return this.chldUriEnabled;
		}

		public boolean isChldParentEnabled() {
			return this.chldParentEnabled;
		}

		/**
		 * Diese Methode setzt die Aktivierung der URI an Attributknoten und gibt {@code this} zurück.
		 * 
		 * @see #isAttrUriEnabled()
		 * @param value {@code true}, wenn Attributknoten einen URI haben dürfen.
		 * @return {@code this}.
		 */
		public BEXFileEncoder setAttrUriEnabled(final boolean value) {
			this.attrUriEnabled = value;
			return this;
		}

		public BEXFileEncoder setAttrParentEnabled(final boolean value) {
			this.attrParentEnabled = value;
			if (!value) return this;
			return this.setChldParentEnabled(true);
		}

		public BEXFileEncoder setChldUriEnabled(final boolean chldUriEnabled) {
			this.chldUriEnabled = chldUriEnabled;
			return this;
		}

		public BEXFileEncoder setChldParentEnabled(final boolean value) {
			this.chldParentEnabled = value;
			if (value) return this;
			return this.setAttrParentEnabled(false);
		}

		{}

		{}

		{}


		{}

		int[] encodeText(final String source) {
			final byte[] bytes = source.getBytes(BEX.CHARSET);
			final int length = bytes.length;
			final int[] result = new int[length + 1];
			for (int i = 0; i < length; i++) {
				result[i] = bytes[i];
			}
			result[length] = 0;
			return result;
		}

		IAMListData encodeTextList(final BEXTextPool source, final ByteOrder order) {
			final IAMListEncoder result = new IAMListEncoder();
			final List<BEXTextItem> items = source.items;
			Collections.sort(items, BEXTextItem.COMPARATOR);
			for (final BEXTextItem item: items) {
				item.key = result.put(this.encodeText(item.text), false);
			}
			return new IAMListData(result.encode(order));
		}

		void update(final BEXGrpupPool source) {
			final List<BEXGroupItem> items = source.items;
			Collections.sort(items, BEXGroupItem.COMPARATOR);
			int offset = 0;
			for (final BEXGroupItem item: items) {
				item.offset = offset;
				offset += item.items.size();
			}
		}

		BEXGroupItem encodeChldList(final List<BEXNodeEncoder> source, final BEXNodeData parent) {
			final int length = source.size();
			final ArrayList<BEXItem> result = new ArrayList<BEXItem>(length * 5);
			final BEXGroupItem resultData = new BEXGroupItem(result);
			final BEXTextPool uriText = this.chldUriText, nameText = this.chldNameText, valueText = this.chldValueText;
			final boolean uriEnabled = this.chldUriEnabled, parentEnabled = this.chldParentEnabled, parentEnabled2 = parentEnabled || this.attrParentEnabled;
			for (int i = 0; i < length; i++) {
				final BEXNodeEncoder chld = source.get(i);
				final BEXNodeData chldData = parentEnabled2 ? new BEXNodeData(resultData, i) : null;
				if (BEX.type(chld.key) == BEX.BEX_ELEM_NODE) {
					result.add(uriText.get(uriEnabled ? chld.uri : null));
					result.add(nameText.get(chld.name));
					result.add(this.encodeChldList(chld.children, chldData));
					result.add(this.encodeAttrList(chld.attributes, chldData));
				} else {
					result.add(uriText.get(null));
					result.add(nameText.get(null));
					result.add(valueText.get(chld.value));
					result.add(null);
				}
				result.add(parentEnabled ? chldData : null);
			}
			return parentEnabled ? this.chldTable.get(result) : this.chldTable.put(resultData);
		}

		BEXGroupItem encodeAttrList(final List<BEXNodeEncoder> source, final BEXNodeData parent) {
			final int length = source.size();
			final ArrayList<BEXItem> result = new ArrayList<BEXItem>(length * 5);
			final BEXGroupItem resultData = new BEXGroupItem(result);
			final BEXTextPool uriText = this.attrUriText, nameText = this.attrNameText, valueText = this.attrValueText;
			final boolean uriEnabled = this.attrUriEnabled, parentEnabled = this.attrParentEnabled;
			for (int i = 0; i < length; i++) {
				final BEXNodeEncoder attr = source.get(i);
				result.add(uriText.get(uriEnabled ? attr.uri : null));
				result.add(nameText.get(attr.name));
				result.add(valueText.get(attr.value));
				result.add(null);
				result.add(parentEnabled ? parent : null);
			}
			return parentEnabled ? this.attrTable.get(result) : this.attrTable.put(resultData);
		}

		public byte[] encode(final ByteOrder order) {

			this.attrUriText = new BEXTextPool();

			this.attrNameText = new BEXTextPool();
			this.attrValueText = new BEXTextPool();
			this.chldUriText = new BEXTextPool();
			this.chldNameText = new BEXTextPool();
			this.chldValueText = new BEXTextPool();

			final BEXGroupItem rootList = this.encodeChldList(this.docuNode.children, null);

			final IAMIndexEncoder encoder = new IAMIndexEncoder();

			encoder.putList(this.encodeTextList(this.attrUriText, order));
			encoder.putList(this.encodeTextList(this.attrNameText, order));
			encoder.putList(this.encodeTextList(this.attrValueText, order));
			encoder.putList(this.encodeTextList(this.chldUriText, order));
			encoder.putList(this.encodeTextList(this.chldNameText, order));
			encoder.putList(this.encodeTextList(this.chldValueText, order));

			this.update(this.attrTable);
			this.update(this.chldTable);

			// attrUriRef, attrNameRef, attrValueRef, attrParentRef, chldUriRef, chldNameRef, chldContentRef, chldAttributesRef und chldParentRef

			// chldListRange und attrListRange

			final byte[] b = encoder.encode(order);

			return null;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNode root() {
			return this.docuNode.children().get(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXListEncoder list(final int key) {
			final int type = BEX.type(key);
			if (type == BEX.BEX_VOID_TYPE) return new BEXListEncoder(this);
			final int index = BEX.index(key);
			if ((index < 0) || (index >= this.nodeList.size())) return new BEXListEncoder(this);
			final BEXNodeEncoder node = this.nodeList.get(index);
			if (type == BEX.BEX_CHLD_LIST) return node.children();
			if (type == BEX.BEX_ATTR_LIST) return node.attributes();
			return new BEXListEncoder(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeEncoder node(final int key) {
			final int index = BEX.index(key);
			if ((index < 0) || (index >= this.nodeList.size())) return new BEXNodeEncoder(this);
			final BEXNodeEncoder node = this.nodeList.get(index);
			if (node.key == key) return node;
			return new BEXNodeEncoder(this);
		}

	}

	/**
	 * Diese Klasse implementiert die modifizierbaren {@link BEXList} eines {@link BEXFileEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXListEncoder extends BEXBaseList {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		final BEXFileEncoder owner;

		/**
		 * Dieses Feld speichert den Schlüssel.
		 * 
		 * @see BEX#key(int, int)
		 */
		final int key;

		/**
		 * Dieser Konstruktor initialisiert die undefinierte Knotenliste.
		 * 
		 * @param owner Besitzer.
		 */
		BEXListEncoder(final BEXFileEncoder owner) {
			this(owner, BEX.BEX_VOID_TYPE, 0);
		}

		/**
		 * Dieser Konstruktor initialisiert eine definierte Knotenliste.
		 * 
		 * @param owner Besitzer.
		 * @param type Typkennung ({@link BEX#BEX_CHLD_LIST}, {@link BEX#BEX_ATTR_LIST}).
		 * @param index Index des Knoten in {@link BEXFileEncoder#nodeList}.
		 */
		BEXListEncoder(final BEXFileEncoder owner, final int type, final int index) {
			this.key = BEX.key(type, index);
			this.owner = owner;
		}

		{}

		@SuppressWarnings ("javadoc")
		private List<BEXNodeEncoder> nodes() {
			switch (BEX.type(this.key)) {
				case BEX.BEX_VOID_TYPE:
					return Collections.emptyList();
				case BEX.BEX_CHLD_LIST:
					return this.owner.nodeList.get(BEX.index(this.key)).children;
				case BEX.BEX_ATTR_LIST:
					return this.owner.nodeList.get(BEX.index(this.key)).attributes;
			}
			throw new IllegalStateException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key() {
			return this.key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			switch (BEX.type(this.key)) {
				case BEX.BEX_VOID_TYPE:
					return BEXList.VOID_LIST;
				case BEX.BEX_CHLD_LIST:
					return BEXList.CHLD_LIST;
				case BEX.BEX_ATTR_LIST:
					return BEXList.ATTR_LIST;
			}
			throw new IllegalStateException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXFileEncoder owner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeEncoder get(final int index) {
			final List<BEXNodeEncoder> nodes = this.nodes();
			if ((index < 0) || (index >= nodes.size())) return new BEXNodeEncoder(this.owner);
			return nodes.get(index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int length() {
			return this.nodes().size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeEncoder parent() {
			return BEX.type(this.key) != BEX.BEX_VOID_TYPE ? this.owner.nodeList.get(BEX.index(this.key)) : new BEXNodeEncoder(this.owner);
		}

	}

	/**
	 * Diese Klasse implementiert den modifizierbaren {@link BEXNode} eines {@link BEXFileEncoder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class BEXNodeEncoder extends BEXBaseNode {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		final BEXFileEncoder owner;

		/**
		 * Dieses Feld speichert den Schlüssel.
		 * 
		 * @see BEX#key(int, int)
		 */
		final int key;

		/**
		 * Dieses Feld speichert den URI.
		 */
		String uri = "";

		/**
		 * Dieses Feld speichert den Namen.
		 */
		String name = "";

		/**
		 * Dieses Feld speichert den Wert.
		 */
		String value = "";

		/**
		 * Dieses Feld speichert das Elternelement, {@code this} (undefinierter Knoten) oder {@code null} (Dokumentknoten).
		 */
		BEXNodeEncoder parent;

		/**
		 * Dieses Feld speichert die Kondknoten.
		 */
		List<BEXNodeEncoder> children = Collections.emptyList();

		/**
		 * Dieses Feld speichert die Attributknoten.
		 */
		List<BEXNodeEncoder> attributes = Collections.emptyList();

		/**
		 * Dieser Konstruktor initialisiert den undefinierten Knoten.
		 * 
		 * @param owner Besitzer.
		 */
		BEXNodeEncoder(final BEXFileEncoder owner) {
			this.key = BEX.key(BEX.BEX_VOID_TYPE, 0);
			this.owner = owner;
			this.parent = this;
		}

		/**
		 * Dieser Konstruktor initialisiert einen definierten Knoten ohne Elternelement.
		 * 
		 * @param owner Besitzer.
		 * @param type Typkennung ({@link BEX#BEX_ELEM_NODE}, {@link BEX#BEX_TEXT_NODE}, {@link BEX#BEX_ATTR_NODE}).
		 * @param index Index des Knoten in {@link BEXFileEncoder#nodeList}.
		 */
		BEXNodeEncoder(final BEXFileEncoder owner, final int type, final int index) {
			this.key = BEX.key(type, index);
			this.owner = owner;
			this.parent = null;
		}

		{}

		@SuppressWarnings ("javadoc")
		private BEXNodeEncoder useUri(final String uri) {
			this.uri = uri != null ? uri : "";
			return this;
		}

		@SuppressWarnings ("javadoc")
		private BEXNodeEncoder useValue(final String value) {
			this.value = value != null ? value : "";
			return this;
		}

		@SuppressWarnings ("javadoc")
		private BEXNodeEncoder useName(final String name) {
			this.name = name;
			return this;
		}

		@SuppressWarnings ("javadoc")
		private BEXNodeEncoder useChld(final BEXNodeEncoder value) {
			((value.parent = this).children.isEmpty() ? this.children = new ArrayList<>(2) : this.children).add(value);
			return value;
		}

		@SuppressWarnings ("javadoc")
		private BEXNodeEncoder useAttr(final BEXNodeEncoder value) {
			((value.parent = this).attributes.isEmpty() ? this.attributes = new ArrayList<>(2) : this.attributes).add(value);
			return value;
		}

		/**
		 * Diese Methode setzt den URI und gibt {@code this} zurück. Der URI {@code null} wird als {@code ""} interpreteirt.
		 * 
		 * @see #uri()
		 * @param uri URI.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn dieser Knoten kein Element- und kein Attributknoten ist.
		 */
		public BEXNodeEncoder setUri(final String uri) throws IllegalStateException {
			final int type = BEX.type(this.key);
			if ((type != BEX.BEX_ELEM_NODE) && (type != BEX.BEX_ATTR_NODE)) throw new IllegalStateException();
			return this.useUri(uri);
		}

		/**
		 * Diese Methode setzt den Namen und gibt {@code this} zurück.
		 * 
		 * @see #name()
		 * @param name Name.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Element- und kein Attributknoten ist.
		 * @throws IllegalArgumentException Wenn der Name leer ist.
		 */
		public BEXNodeEncoder setName(final String name) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			final int type = BEX.type(this.key);
			if ((type != BEX.BEX_ELEM_NODE) && (type != BEX.BEX_ATTR_NODE)) throw new IllegalStateException();
			if (name.isEmpty()) throw new IllegalArgumentException();
			return this.useName(name);
		}

		/**
		 * Diese Methode setzt den Wert und gibt {@code this} zurück. Der Wert {@code null} wird als {@code ""} interpreteirt.
		 * 
		 * @see #value()
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn dieser Knoten kein Text- und kein Attributknoten ist.
		 */
		public BEXNodeEncoder setValue(final String value) throws IllegalStateException {
			final int type = BEX.type(this.key);
			if ((type != BEX.BEX_TEXT_NODE) && (type != BEX.BEX_ATTR_NODE)) throw new IllegalStateException();
			return this.useValue(value);
		}

		/**
		 * Diese Methode fügt einen neuen Textknoten mit dem Wert des gegebenen an und gibt diesen zurück.
		 * 
		 * @param node Textknoten.
		 * @return Textknoten.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 * @throws IllegalArgumentException Wenn {@code node} kein {@link Node#TEXT_NODE} und kein {@link Node#CDATA_SECTION_NODE} ist.
		 */
		public BEXNodeEncoder addText(final Node node) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			final int type = node.getNodeType();
			if ((type != Node.TEXT_NODE) && (type != Node.CDATA_SECTION_NODE)) throw new IllegalArgumentException();
			return this.addText(node.getNodeValue());
		}

		/**
		 * Diese Methode fügt einen neuen Textknoten mit dem gegebenen Wert an und gibt diesen zurück.<br>
		 * Der {@code value} {@code null} wird als {@code ""} interpretiert.
		 * 
		 * @param value Wert oder {@code null}.
		 * @return Textknoten.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 */
		public BEXNodeEncoder addText(final String value) throws IllegalStateException {
			if (BEX.type(this.key) != BEX.BEX_ELEM_NODE) throw new IllegalStateException();
			return this.useChld(this.owner.newNode(BEX.BEX_TEXT_NODE).useValue(value));
		}

		/**
		 * Diese Methode fügt einen neuen Attributknoten mit den Eigenschaften des gegebenen an und gibt diesen zurück.
		 * 
		 * @param node Attributknoten.
		 * @return Attributknoten.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 * @throws IllegalArgumentException Wenn {@code node} kein {@link Node#ATTRIBUTE_NODE} ist.
		 */
		public BEXNodeEncoder addAttr(final Node node) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (node.getNodeType() != Node.ATTRIBUTE_NODE) throw new IllegalArgumentException();
			final String name = node.getLocalName();
			return this.addAttr(node.getNamespaceURI(), name != null ? name : node.getNodeName(), node.getNodeValue());
		}

		/**
		 * Diese Methode fügt einen neuen Attributknoten mit den gegebenen Eigenschaften an und gibt diesen zurück.<br>
		 * Der {@code uri} bzw. {@code value} {@code null} wird als {@code ""} interpretiert.
		 * 
		 * @param uri URI oder {@code null}.
		 * @param name Name.
		 * @param value Wert oder {@code null}.
		 * @return Attributknoten.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 * @throws IllegalArgumentException Wenn {@code name} leer ist.
		 */
		public BEXNodeEncoder addAttr(final String uri, final String name, final String value) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			if (BEX.type(this.key) != BEX.BEX_ELEM_NODE) throw new IllegalStateException();
			if (name.isEmpty()) throw new IllegalArgumentException();
			return this.useAttr(this.owner.newNode(BEX.BEX_ATTR_NODE).useUri(uri).useName(name).useValue(value));
		}

		/**
		 * Diese Methode fügt einen neuen Elementknoten mit den Eigenschaften des gegebenen an und gibt diesen zurück.
		 * 
		 * @see #addAttr(Node)
		 * @see #addText(Node)
		 * @param node Elementknoten.
		 * @return Elementknoten.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 * @throws IllegalArgumentException Wenn {@code node} kein {@link Node#ENTITY_REFERENCE_NODE} ist oder nicht unterstützte Kindknoten enthält.
		 */
		public BEXNodeEncoder addElem(final Node node) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (node.getNodeType() != Node.ELEMENT_NODE) throw new IllegalArgumentException();
			final String name = node.getLocalName();
			final BEXNodeEncoder result = this.addElem(node.getNamespaceURI(), name != null ? name : node.getNodeName());
			final StringBuilder value = this.owner.textValue;
			value.setLength(0);
			final NodeList children = node.getChildNodes();
			for (int i = 0, length = children.getLength(); i < length; i++) {
				final Node node2 = children.item(i);
				switch (node2.getNodeType()) {
					case Node.TEXT_NODE:
					case Node.CDATA_SECTION_NODE:
						value.append(node2.getNodeValue());
						break;
					case Node.ELEMENT_NODE:
						if (value.length() != 0) {
							result.addText(value.toString());
						}
						result.addElem(node2);
						break;
					default:
						throw new IllegalArgumentException();
				}
			}
			if (value.length() != 0) {
				result.addText(value.toString());
			}
			value.setLength(0);
			final NamedNodeMap attributes = node.getAttributes();
			for (int i = 0, length = attributes.getLength(); i < length; i++) {
				result.addAttr(attributes.item(i));
			}
			return result;
		}

		/**
		 * Diese Methode fügt einen neuen Elementknoten mit den gegebenen Eigenschaften an und gibt diesen zurück.<br>
		 * Der {@code uri} {@code null} wird als {@code ""} interpretiert.
		 * 
		 * @param uri URI oder {@code null}.
		 * @param name Name.
		 * @return Elementknoten.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 * @throws IllegalArgumentException Wenn {@code name} leer ist.
		 */
		public BEXNodeEncoder addElem(final String uri, final String name) throws NullPointerException, IllegalStateException, IllegalArgumentException {
			if (BEX.type(this.key) != BEX.BEX_ELEM_NODE) throw new IllegalStateException();
			if (name.isEmpty()) throw new IllegalArgumentException();
			return this.useChld(this.owner.newNode(BEX.BEX_ELEM_NODE).useUri(uri).useName(name));
		}

		/**
		 * Diese Methode gibt das Ergebnis des Aufrufts {@code this.putElem(uri, name).putText(value).parent()} zurück.
		 * 
		 * @see #addText(String)
		 * @see #addElem(String, String)
		 * @param uri URI oder {@code null}.
		 * @param name Name.
		 * @param value Wert oder {@code null}.
		 * @return Elementknoten.
		 * @throws NullPointerException Wenn {@code name} {@code null} ist.
		 * @throws IllegalStateException Wenn dieser Knoten kein Elementknoten ist.
		 * @throws IllegalArgumentException Wenn {@code name} leer ist.
		 */
		public BEXNodeEncoder addElem(final String uri, final String name, final String value) throws NullPointerException, IllegalStateException,
			IllegalArgumentException {
			return this.addElem(uri, name).addText(value).parent();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int key() {
			return this.key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int type() {
			switch (BEX.type(this.key)) {
				case BEX.BEX_VOID_TYPE:
					return BEXNode.VOID_NODE;
				case BEX.BEX_ELEM_NODE:
					return BEXNode.ELEM_NODE;
				case BEX.BEX_ATTR_NODE:
					return BEXNode.ATTR_NODE;
				case BEX.BEX_TEXT_NODE:
					return BEXNode.TEXT_NODE;
			}
			throw new IllegalStateException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXFileEncoder owner() {
			return this.owner;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String uri() {
			return this.uri;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String name() {
			return this.name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String value() {
			if (BEX.type(this.key) == BEX.BEX_ELEM_NODE) return this.children().get(0).value();
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int index() {
			if (this.parent == null) return -1;
			return (BEX.type(this.key) != BEX.BEX_ATTR_NODE ? this.parent.children : this.parent.attributes).indexOf(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXNodeEncoder parent() {
			if (this.parent == null) return new BEXNodeEncoder(this.owner);
			return this.parent;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXListEncoder children() {
			return BEX.type(this.key) == BEX.BEX_ELEM_NODE ? new BEXListEncoder(this.owner, BEX.BEX_CHLD_LIST, BEX.index(this.key)) : new BEXListEncoder(this.owner);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public BEXListEncoder attributes() {
			return BEX.type(this.key) == BEX.BEX_ELEM_NODE ? new BEXListEncoder(this.owner, BEX.BEX_ATTR_LIST, BEX.index(this.key)) : new BEXListEncoder(this.owner);
		}

	}

}
