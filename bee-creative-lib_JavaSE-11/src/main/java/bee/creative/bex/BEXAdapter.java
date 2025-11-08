package bee.creative.bex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import bee.creative.lang.Objects;

/** Diese Klasse implementiert die Adapter zur Überführung von {@link BEXFile}, {@link BEXNode} und {@link BEXList} in {@link Document}, {@link Text},
 * {@link Attr}, {@link Element}, {@link NodeList} und {@link NamedNodeMap}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class BEXAdapter {

	/** Diese Methode gibt das gegebene {@link BEXFile} als {@link Document} zurück.
	 *
	 * @param file {@link BEXFile}.
	 * @return {@link Document}.
	 * @throws NullPointerException Wenn {@code file} {@code null} ist. */
	public static Document wrap(BEXFile file) throws NullPointerException {
		return new BEXDocuAdapter(file);
	}

	/** Diese Klasse implementiert ein {@link Attr} als {@link BEXNodeAdapter}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXAttrAdapter extends BEXNodeAdapter implements Attr {

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXAttrAdapter(BEXNode node, Element parent) throws NullPointerException {
			super(node);
			this.parent = Objects.notNull(parent);
		}

		@Override
		public String getNamespaceURI() {
			return this.node.uri();
		}

		@Override
		public short getNodeType() {
			return Node.ATTRIBUTE_NODE;
		}

		@Override
		public String getNodeName() {
			return this.node.name();
		}

		@Override
		public String getNodeValue() throws DOMException {
			return this.node.value();
		}

		@Override
		public String getLocalName() {
			return this.node.name();
		}

		@Override
		public Node getParentNode() {
			return this.getOwnerElement();
		}

		@Override
		public String getName() {
			return this.getNodeName();
		}

		@Override
		public String getValue() {
			return this.getNodeValue();
		}

		@Override
		public void setValue(String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public boolean getSpecified() {
			return true;
		}

		@Override
		public String getTextContent() throws DOMException {
			return this.getNodeValue();
		}

		@Override
		public Element getOwnerElement() {
			return this.parent;
		}

		@Override
		public TypeInfo getSchemaTypeInfo() {
			return BEXDocuAdapter.VOID_TYPE_INFO;
		}

		@Override
		public boolean isId() {
			return false;
		}

		@Override
		public boolean isSameNode(Node object) {
			return this.equals(object) && this.getParentNode().isSameNode(object.getParentNode());
		}

		@Override
		public boolean isEqualNode(Node object) {
			return this.equals(object);
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXAttrAdapter)) return false;
			var data = (BEXAttrAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return this.getNodeName() + "=" + Objects.toString(this.getNodeValue());
		}

		/** Dieses Feld speichert den Elternknoten. */
		protected final Element parent;

	}

	/** Diese Klasse implementiert ein {@link Text}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXTextAdapter extends BEXChldAdapter implements Text {

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXTextAdapter(BEXNode node, Node parent) throws NullPointerException {
			super(node, parent);
		}

		@Override
		public short getNodeType() {
			return Node.TEXT_NODE;
		}

		@Override
		public String getNodeName() {
			return "#text";
		}

		@Override
		public String getNodeValue() throws DOMException {
			return this.node.value();
		}

		@Override
		public String getData() throws DOMException {
			return this.getNodeValue();
		}

		@Override
		public void setData(String data) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public int getLength() {
			return this.getNodeValue().length();
		}

		@Override
		public String getTextContent() throws DOMException {
			return this.getNodeValue();
		}

		@Override
		public String getWholeText() {
			return this.getNodeValue();
		}

		@Override
		public String substringData(int offset, int count) throws DOMException {
			try {
				return this.getNodeValue().substring(offset, offset + count);
			} catch (IndexOutOfBoundsException cause) {
				throw new DOMException(DOMException.INDEX_SIZE_ERR, cause.getMessage());
			}
		}

		@Override
		public Text splitText(int offset) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void insertData(int offset, String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void deleteData(int offset, int count) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void appendData(String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void replaceData(int offset, int count, String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Text replaceWholeText(String content) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public boolean isSameNode(Node object) {
			return this.equals(object) && this.getParentNode().isSameNode(object.getParentNode());
		}

		@Override
		public boolean isEqualNode(Node object) {
			return this.equals(object);
		}

		@Override
		public boolean isElementContentWhitespace() {
			var nodeValue = this.getNodeValue();
			for (int i = 0, size = nodeValue.length(); i < size; i++) {
				var value = nodeValue.charAt(i);
				if ((value > 0x20) || (value < 0x09) || ((value != 0x0A) && (value != 0x0D))) return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXTextAdapter)) return false;
			var data = (BEXTextAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return this.getNodeValue();
		}

	}

	/** Diese Klasse implementiert ein {@link Element}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXElemAdapter extends BEXChldAdapter implements Element {

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXElemAdapter(BEXNode node, Node parent) throws NullPointerException {
			super(node, parent);
		}

		@Override
		public String getNamespaceURI() {
			return this.node.uri();
		}

		@Override
		public short getNodeType() {
			return Node.ELEMENT_NODE;
		}

		@Override
		public String getNodeName() {
			return this.node.name();
		}

		@Override
		public String getNodeValue() throws DOMException {
			return null;
		}

		@Override
		public String getTagName() {
			return this.getNodeName();
		}

		@Override
		public String getLocalName() {
			return this.node.name();
		}

		@Override
		public Node getFirstChild() {
			var c = this.getChildNodes();
			return c.item(0);
		}

		@Override
		public Node getLastChild() {
			var c = this.getChildNodes();
			return c.item(c.getLength() - 1);
		}

		@Override
		public boolean hasChildNodes() {
			return this.node.children().size() != 0;
		}

		@Override
		public NodeList getChildNodes() {
			return new BEXChldListAdapter(this.node.children(), this);
		}

		@Override
		public NamedNodeMap getAttributes() {
			return new BEXAttrListAdapter(this.node.attributes(), this);
		}

		@Override
		public boolean hasAttributes() {
			return this.node.attributes().size() != 0;
		}

		@Override
		public String getAttribute(String name) {
			return this.getAttributeNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public boolean hasAttribute(String name) {
			return this.hasAttributeNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public void setAttribute(String name, String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String getAttributeNS(String uri, String name) throws DOMException {
			var attributes = this.node.attributes();
			var index = attributes.find(uri, name, 0);
			if (index < 0) return "";
			return attributes.get(index).value();
		}

		@Override
		public boolean hasAttributeNS(String uri, String name) throws DOMException {
			var attributes = this.node.attributes();
			var index = attributes.find(uri, name, 0);
			return index >= 0;
		}

		@Override
		public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Attr getAttributeNode(String name) {
			return this.getAttributeNodeNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public Attr setAttributeNode(Attr newAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Attr getAttributeNodeNS(String uri, String name) throws DOMException {
			var attributes = this.node.attributes();
			var index = attributes.find(uri, name, 0);
			if (index < 0) return null;
			return new BEXAttrAdapter(attributes.get(index), this);
		}

		@Override
		public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setIdAttribute(String name, boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String getTextContent() throws DOMException {
			var content = new StringBuilder();
			BEXElemAdapter.collectContent(content, this.node.children());
			return content.toString();
		}

		@Override
		public TypeInfo getSchemaTypeInfo() {
			return BEXDocuAdapter.VOID_TYPE_INFO;
		}

		@Override
		public void removeAttribute(String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public NodeList getElementsByTagName(String name) {
			return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public NodeList getElementsByTagNameNS(String uri, String name) throws DOMException {
			return new BEXElemCollector(this, uri, name, false);
		}

		@Override
		public boolean isSameNode(Node object) {
			return this.isEqualNode(object) && this.getParentNode().isSameNode(object.getParentNode());
		}

		@Override
		public boolean isEqualNode(Node object) {
			return this.equals(object);
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXElemAdapter)) return false;
			var data = (BEXElemAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return "<" + this.getNodeName() + this.getAttributes() + ">";
		}

		/** Diese Methode implementeirt {@link #getTextContent()}.
		 *
		 * @param result Puffer mit dem bisher gesammelten Texten.
		 * @param children {@link BEXList} der rekursiv analysierten Kindknoten.
		 * @throws NullPointerException Wenn eine der Eingabe {@code null} ist. */
		static void collectContent(StringBuilder result, BEXList children) throws NullPointerException {
			for (BEXNode child: children) {
				if (child.type() == BEXNode.ELEM_NODE) {
					BEXElemAdapter.collectContent(result, child.children());
				} else {
					result.append(child.value());
				}
			}
		}

	}

	/** Diese Klasse implementiert ein {@link Document}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXDocuAdapter extends BEXNodeAdapter implements Document {

		/** Dieses Feld speichert die leere {@link TypeInfo}. */
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
			public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
				return false;
			}

		};

		/** Dieses Feld speichert die leere {@link NodeList}. */
		public static final NodeList VOID_NODE_LIST = new NodeList() {

			@Override
			public Node item(int index) {
				return null;
			}

			@Override
			public int getLength() {
				return 0;
			}

		};

		/** Dieses Feld speichert die leere {@link DOMConfiguration}. */
		public static final DOMConfiguration VOID_DOM_CONFIGURATION = new DOMConfiguration() {

			@Override
			public void setParameter(String name, Object value) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

			@Override
			public DOMStringList getParameterNames() {
				return BEXDocuAdapter.defaultParameterList;
			}

			@Override
			public Object getParameter(String name) throws DOMException {
				if (BEXDocuAdapter.defaultParameterListTrue.contains(name)) return Boolean.TRUE;
				if (BEXDocuAdapter.defaultParameterListFalse.contains(name)) return Boolean.FALSE;
				throw new DOMException(DOMException.NOT_FOUND_ERR, null);
			}

			@Override
			public boolean canSetParameter(String name, Object value) {
				return false;
			}

		};

		/** Dieses Feld speichert die leere {@link DOMImplementation}. */
		public static final DOMImplementation VOID_DOM_IMPLEMENTATION = new DOMImplementation() {

			@Override
			public boolean hasFeature(String feature, String version) {
				return false;
			}

			@Override
			public Object getFeature(String feature, String version) {
				return null;
			}

			@Override
			public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

			@Override
			public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

		};

		/** Dieser Konstruktor initialisiert das {@link BEXFile}.
		 *
		 * @param file {@link BEXFile}.
		 * @throws NullPointerException Wenn {@code file} {@code null} ist. */
		public BEXDocuAdapter(BEXFile file) throws NullPointerException {
			super(file.root());
		}

		@Override
		public short getNodeType() {
			return Node.DOCUMENT_NODE;
		}

		@Override
		public String getNodeName() {
			return "#document";
		}

		@Override
		public String getNodeValue() throws DOMException {
			return null;
		}

		@Override
		public Node getParentNode() {
			return null;
		}

		@Override
		public Node getFirstChild() {
			return this.getDocumentElement();
		}

		@Override
		public Node getLastChild() {
			return this.getDocumentElement();
		}

		@Override
		public NodeList getChildNodes() {
			return new NodeList() {

				@Override
				public Node item(int index) {
					if (index != 0) return null;
					return BEXDocuAdapter.this.getDocumentElement();
				}

				@Override
				public int getLength() {
					return 1;
				}

			};
		}

		@Override
		public boolean hasChildNodes() {
			return true;
		}

		@Override
		public NamedNodeMap getAttributes() {
			return null;
		}

		@Override
		public boolean hasAttributes() {
			return false;
		}

		@Override
		public String getTextContent() throws DOMException {
			return this.getDocumentElement().getTextContent();
		}

		@Override
		public DocumentType getDoctype() {
			return null;
		}

		@Override
		public DOMImplementation getImplementation() {
			return BEXDocuAdapter.VOID_DOM_IMPLEMENTATION;
		}

		@Override
		public String lookupPrefix(String namespaceURI) {
			return null;
		}

		@Override
		public String lookupNamespaceURI(String prefix) {
			return null;
		}

		@Override
		public boolean isDefaultNamespace(String uri) {
			return false;
		}

		@Override
		public Element getElementById(String elementId) {
			return null;
		}

		@Override
		public NodeList getElementsByTagName(String name) {
			return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public NodeList getElementsByTagNameNS(String uri, String name) {
			return new BEXElemCollector(this, uri, name, true);
		}

		@Override
		public Element getDocumentElement() {
			return new BEXElemAdapter(this.node, this);
		}

		@Override
		public Element createElement(String tagName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public DocumentFragment createDocumentFragment() {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Text createTextNode(String data) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Comment createComment(String data) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public CDATASection createCDATASection(String data) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Attr createAttribute(String name) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public EntityReference createEntityReference(String name) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Node importNode(Node importedNode, boolean deep) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public String getInputEncoding() {
			return null;
		}

		@Override
		public String getXmlEncoding() {
			return "UTF-8";
		}

		@Override
		public boolean getXmlStandalone() {
			return true;
		}

		@Override
		public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public String getXmlVersion() {
			return "1.0";
		}

		@Override
		public void setXmlVersion(String xmlVersion) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public boolean getStrictErrorChecking() {
			return true;
		}

		@Override
		public void setStrictErrorChecking(boolean strictErrorChecking) {
		}

		@Override
		public String getDocumentURI() {
			return null;
		}

		@Override
		public void setDocumentURI(String documentURI) {
		}

		@Override
		public Node adoptNode(Node source) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public DOMConfiguration getDomConfig() {
			return BEXDocuAdapter.VOID_DOM_CONFIGURATION;
		}

		@Override
		public void normalizeDocument() {
		}

		@Override
		public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public boolean isSameNode(Node object) {
			return this.equals(object);
		}

		@Override
		public boolean isEqualNode(Node object) {
			return this.equals(object);
		}

		@Override
		public Document getOwnerDocument() {
			return null;
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXDocuAdapter)) return false;
			var data = (BEXDocuAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return this.getDocumentElement().toString();
		}

		/** Dieses Feld speichert die {@link DOMStringList} der leeren {@link DOMConfiguration}.
		 *
		 * @see DOMConfiguration#getParameterNames() */
		static final DOMStringList defaultParameterList = new DOMStringList() {

			@Override
			public String item(int index) {
				var offset = BEXDocuAdapter.defaultParameterListTrue.size();
				if (index < offset) return BEXDocuAdapter.defaultParameterListTrue.get(index);
				return BEXDocuAdapter.defaultParameterListFalse.get(index - offset);
			}

			@Override
			public int getLength() {
				return BEXDocuAdapter.defaultParameterListTrue.size() + BEXDocuAdapter.defaultParameterListFalse.size();
			}

			@Override
			public boolean contains(String str) {
				return BEXDocuAdapter.defaultParameterListTrue.contains(str) || BEXDocuAdapter.defaultParameterListFalse.contains(str);
			}

		};

		/** Dieses Feld speichert die {@code true-Parameter} der leeren {@link DOMConfiguration}.
		 *
		 * @see DOMConfiguration#getParameter(String) */
		static final List<String> defaultParameterListTrue = Collections.unmodifiableList(
			Arrays.asList("comments", "datatype-normalization", "well-formed", "namespaces", "namespace-declarations", "element-content-whitespace"));

		/** Dieses Feld speichert die {@code false-Parameter} der leeren {@link DOMConfiguration}.
		 *
		 * @see DOMConfiguration#getParameter(String) */
		static final List<String> defaultParameterListFalse = Collections.unmodifiableList(Arrays.asList("cdata-sections", "entities", "split-cdata-sections",
			"validate", "infoset", "normalize-characters", "canonical-form", "validate-if-schema", "check-character-normalization"));

	}

	/** Diese Klasse implementiert einen abstrakten {@link Node}, dessen Methoden keine Modifikation zulassen.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BEXNodeAdapter implements Node {

		/** Dieser Konstruktor initialisiert den {@link BEXNode}.
		 *
		 * @param node {@link BEXNode}.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist. */
		public BEXNodeAdapter(BEXNode node) throws NullPointerException {
			this.node = Objects.notNull(node);
		}

		@Override
		public String getPrefix() {
			return null;
		}

		@Override
		public void setPrefix(String prefix) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String getBaseURI() {
			return null;
		}

		@Override
		public String getNamespaceURI() {
			return null;
		}

		@Override
		public Node getFirstChild() {
			return null;
		}

		@Override
		public Node getLastChild() {
			return null;
		}

		@Override
		public Node getNextSibling() {
			return null;
		}

		@Override
		public Node getPreviousSibling() {
			return null;
		}

		@Override
		public boolean hasAttributes() {
			return false;
		}

		@Override
		public NamedNodeMap getAttributes() {
			return null;
		}

		@Override
		public boolean hasChildNodes() {
			return false;
		}

		@Override
		public NodeList getChildNodes() {
			return BEXDocuAdapter.VOID_NODE_LIST;
		}

		@Override
		public String getLocalName() {
			return null;
		}

		@Override
		public void setNodeValue(String nodeValue) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setTextContent(String textContent) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String lookupPrefix(String uri) {
			return null;
		}

		@Override
		public String lookupNamespaceURI(String prefix) {
			return null;
		}

		@Override
		public boolean isDefaultNamespace(String uri) {
			return false;
		}

		@Override
		public Object getFeature(String feature, String version) {
			return null;
		}

		@Override
		public boolean isSupported(String feature, String version) {
			return false;
		}

		@Override
		public Object getUserData(String key) {
			return null;
		}

		@Override
		public Object setUserData(String key, Object data, UserDataHandler handler) {
			return null;
		}

		@Override
		public Document getOwnerDocument() {
			return new BEXDocuAdapter(this.node.owner());
		}

		@Override
		public Node insertBefore(Node newChild, Node refChild) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node cloneNode(boolean deep) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node removeChild(Node node) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node appendChild(Node node) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void normalize() {
		}

		@Override
		public short compareDocumentPosition(Node node) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		/** Dieses Feld speichert den {@link BEXNode}. */
		protected final BEXNode node;

	}

	/** Diese Klasse erweitert den {@link BEXNodeAdapter} und die Methoden {@link #getParentNode()}, {@link #getNextSibling()} und {@link #getPreviousSibling()}
	 * als Basis des {@link BEXTextAdapter} sowie des {@link BEXElemAdapter}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BEXChldAdapter extends BEXNodeAdapter {

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXChldAdapter(BEXNode node, Node parent) throws NullPointerException {
			super(node);
			this.parent = parent;
		}

		@Override
		public Node getParentNode() {
			return this.parent;
		}

		@Override
		public Node getPreviousSibling() {
			var parent = this.getParentNode();
			if (parent == null) return null;
			var index = this.node.index();
			if (index < 0) return null;
			return parent.getChildNodes().item(index - 1);
		}

		@Override
		public Node getNextSibling() {
			var parent = this.getParentNode();
			if (parent == null) return null;
			var index = this.node.index();
			if (index < 0) return null;
			return parent.getChildNodes().item(index + 1);
		}

		/** Dieses Feld speichert den Elternknoten. */
		protected final Node parent;

	}

	/** Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getAttributes()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXAttrListAdapter implements NamedNodeMap {

		/** Dieser Konstruktor initialisiert {@link BEXList} und Elternknoten.
		 *
		 * @param list {@link BEXList}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXAttrListAdapter(BEXList list, Element parent) throws NullPointerException {
			this.list = Objects.notNull(list);
			this.parent = Objects.notNull(parent);
		}

		@Override
		public Node item(int index) {
			var node = this.list.get(index);
			if (node.type() == BEXNode.VOID_NODE) return null;
			return new BEXAttrAdapter(node, this.parent);
		}

		@Override
		public int getLength() {
			return this.list.size();
		}

		@Override
		public Node getNamedItem(String name) {
			return this.getNamedItemNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public Node setNamedItem(Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
			var index = this.list.find(namespaceURI, localName, 0);
			if (index < 0) return null;
			return new BEXAttrAdapter(this.list.get(index), this.parent);
		}

		@Override
		public Node setNamedItemNS(Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node removeNamedItem(String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String toString() {
			var builder = new StringBuilder();
			for (int i = 0, size = this.getLength(); i < size; i++) {
				builder.append(' ').append(this.item(i));
			}
			return builder.toString();
		}

		/** Dieses Feld speichert den {@link BEXList}. */
		protected final BEXList list;

		/** Dieses Feld speichert den Elternknoten. */
		protected final Element parent;

	}

	/** Diese Klasse implementiert die {@link NodeList} für {@link Element#getChildNodes()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXChldListAdapter implements NodeList {

		/** Dieser Konstruktor initialisiert {@link BEXList} und Elternknoten.
		 *
		 * @param list {@link BEXList}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXChldListAdapter(BEXList list, Node parent) throws NullPointerException {
			this.list = Objects.notNull(list);
			this.parent = Objects.notNull(parent);
		}

		@Override
		public Node item(int index) {
			var node = this.list.get(index);
			if (node.type() == BEXNode.VOID_NODE) return null;
			if (node.type() == BEXNode.ELEM_NODE) return new BEXElemAdapter(node, this.parent);
			return new BEXTextAdapter(node, this.parent);
		}

		@Override
		public int getLength() {
			return this.list.size();
		}

		@Override
		public String toString() {
			var builder = new StringBuilder();
			for (int i = 0, size = this.getLength(); i < size; i++) {
				builder.append(this.item(i));
			}
			return builder.toString();
		}

		/** Dieses Feld speichert den {@link BEXList}. */
		protected final BEXList list;

		/** Dieses Feld speichert den Elternknoten. */
		protected final Node parent;

	}

	/** Diese Klasse implementiert die {@link NodeList} für {@link Element#getElementsByTagName(String)}, {@link Element#getElementsByTagNameNS(String, String)},
	 * {@link Document#getElementsByTagName(String)} und {@link Document#getElementsByTagNameNS(String, String)} mit der entsprechenden Sematik beim
	 * Zusammenstellen der Elementknoten.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXElemCollector implements NodeList {

		/** Dieser Konstruktor initialisiert die Parameter zur Zusammenstellung der {@link BEXNodeAdapter}.
		 *
		 * @param node Elementknoten, dessen Kindknoten rekursiv analysiert werden.
		 * @param uri Uri oder {@code "*"}.
		 * @param name Name oder {@code "*"}.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst auch analysiert werden soll.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
		public BEXElemCollector(BEXNodeAdapter node, String uri, String name, boolean self) throws NullPointerException {
			this.uri = Objects.notNull(uri);
			this.name = Objects.notNull(name);
			this.list = new ArrayList<>();
			if ("*".equals(uri)) {
				if ("*".equals(name)) {
					this.collectElements(node, self);
				} else {
					this.collectElementsByName(node, self);
				}
			} else {
				if ("*".equals(name)) {
					this.collectElementsByUri(node, self);
				} else {
					this.collectElementsByLabel(node, self);
				}
			}
			this.size = this.list.size();
		}

		@Override
		public Node item(int index) {
			if (index < this.size) return this.list.get(index);
			return null;
		}

		@Override
		public int getLength() {
			return this.size;
		}

		/** Dieses Feld speichert die gesuchte {@link BEXNode#uri()} oder {@code "*"}. */
		protected final String uri;

		/** Dieses Feld speichert den gesuchten {@link BEXNode#name()} oder {@code "*"}. */
		protected final String name;

		/** Dieses Feld speichert die gesammelten Elementknoten. */
		protected final List<Node> list;

		/** Dieses Feld speichert die Anzahl der gesammelten Elementknoten. */
		protected final int size;

		/** Diese Methode sammelt alle Elementknoten.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElements(BEXNodeAdapter node, boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self) {
					this.list.add(node);
				}
				var children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElements((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		/** Diese Methode sammelt alle Elementknoten mit passendem {@link #uri}.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElementsByUri(BEXNodeAdapter node, boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self && this.uri.equals(node.node.uri())) {
					this.list.add(node);
				}
				var children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElementsByUri((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		/** Diese Methode sammelt alle Elementknoten mit passendem {@link #name}.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElementsByName(BEXNodeAdapter node, boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self && this.name.equals(node.node.name())) {
					this.list.add(node);
				}
				var children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElementsByName((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		/** Diese Methode sammelt alle Elementknoten mit passendem {@link #uri} und {@link #name}.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElementsByLabel(BEXNodeAdapter node, boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self && this.name.equals(node.node.name()) && this.uri.equals(node.node.uri())) {
					this.list.add(node);
				}
				var children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElementsByLabel((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

	}

}
