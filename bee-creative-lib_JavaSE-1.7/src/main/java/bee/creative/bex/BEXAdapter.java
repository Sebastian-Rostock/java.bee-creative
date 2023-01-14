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

	/** Diese Klasse implementiert ein {@link Attr} als {@link BEXNodeAdapter}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXAttrAdapter extends BEXNodeAdapter implements Attr {

		/** Dieses Feld speichert den Elternknoten. */
		protected final Element parent;

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXAttrAdapter(final BEXNode node, final Element parent) throws NullPointerException {
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
		public void setValue(final String value) throws DOMException {
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
		public boolean isSameNode(final Node object) {
			return this.equals(object) && this.getParentNode().isSameNode(object.getParentNode());
		}

		@Override
		public boolean isEqualNode(final Node object) {
			return this.equals(object);
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXAttrAdapter)) return false;
			final BEXAttrAdapter data = (BEXAttrAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return this.getNodeName() + "=" + Objects.toString(this.getNodeValue());
		}

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
		public BEXTextAdapter(final BEXNode node, final Node parent) throws NullPointerException {
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
		public void setData(final String data) throws DOMException {
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
		public String substringData(final int offset, final int count) throws DOMException {
			try {
				return this.getNodeValue().substring(offset, offset + count);
			} catch (final IndexOutOfBoundsException cause) {
				throw new DOMException(DOMException.INDEX_SIZE_ERR, cause.getMessage());
			}
		}

		@Override
		public Text splitText(final int offset) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void insertData(final int offset, final String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void deleteData(final int offset, final int count) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void appendData(final String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void replaceData(final int offset, final int count, final String arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Text replaceWholeText(final String content) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public boolean isSameNode(final Node object) {
			return this.equals(object) && this.getParentNode().isSameNode(object.getParentNode());
		}

		@Override
		public boolean isEqualNode(final Node object) {
			return this.equals(object);
		}

		@Override
		public boolean isElementContentWhitespace() {
			final String nodeValue = this.getNodeValue();
			for (int i = 0, size = nodeValue.length(); i < size; i++) {
				final char value = nodeValue.charAt(i);
				if ((value > 0x20) || (value < 0x09)) return false;
				if ((value != 0x0A) && (value != 0x0D)) return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXTextAdapter)) return false;
			final BEXTextAdapter data = (BEXTextAdapter)object;
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

		/** Diese Methode implementeirt {@link #getTextContent()}.
		 *
		 * @param result Puffer mit dem bisher gesammelten Texten.
		 * @param children {@link BEXList} der rekursiv analysierten Kindknoten.
		 * @throws NullPointerException Wenn eine der Eingabe {@code null} ist. */
		static void collectContent(final StringBuilder result, final BEXList children) throws NullPointerException {
			for (final BEXNode child: children) {
				if (child.type() == BEXNode.ELEM_NODE) {
					BEXElemAdapter.collectContent(result, child.children());
				} else {
					result.append(child.value());
				}
			}
		}

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXElemAdapter(final BEXNode node, final Node parent) throws NullPointerException {
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
			final NodeList c = this.getChildNodes();
			return c.item(0);
		}

		@Override
		public Node getLastChild() {
			final NodeList c = this.getChildNodes();
			return c.item(c.getLength() - 1);
		}

		@Override
		public boolean hasChildNodes() {
			return this.node.children().length() != 0;
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
			return this.node.attributes().length() != 0;
		}

		@Override
		public String getAttribute(final String name) {
			return this.getAttributeNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public boolean hasAttribute(final String name) {
			return this.hasAttributeNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public void setAttribute(final String name, final String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String getAttributeNS(final String uri, final String name) throws DOMException {
			final BEXList attributes = this.node.attributes();
			final int index = attributes.find(uri, name, 0);
			if (index < 0) return "";
			return attributes.get(index).value();
		}

		@Override
		public boolean hasAttributeNS(final String uri, final String name) throws DOMException {
			final BEXList attributes = this.node.attributes();
			final int index = attributes.find(uri, name, 0);
			return index >= 0;
		}

		@Override
		public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Attr getAttributeNode(final String name) {
			return this.getAttributeNodeNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public Attr setAttributeNode(final Attr newAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Attr getAttributeNodeNS(final String uri, final String name) throws DOMException {
			final BEXList attributes = this.node.attributes();
			final int index = attributes.find(uri, name, 0);
			if (index < 0) return null;
			return new BEXAttrAdapter(attributes.get(index), this);
		}

		@Override
		public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setIdAttribute(final String name, final boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setIdAttributeNode(final Attr idAttr, final boolean isId) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String getTextContent() throws DOMException {
			final StringBuilder content = new StringBuilder();
			BEXElemAdapter.collectContent(content, this.node.children());
			return content.toString();
		}

		@Override
		public TypeInfo getSchemaTypeInfo() {
			return BEXDocuAdapter.VOID_TYPE_INFO;
		}

		@Override
		public void removeAttribute(final String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void removeAttributeNS(final String namespaceURI, final String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public NodeList getElementsByTagName(final String name) {
			return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public NodeList getElementsByTagNameNS(final String uri, final String name) throws DOMException {
			return new BEXElemCollector(this, uri, name, false);
		}

		@Override
		public boolean isSameNode(final Node object) {
			return this.isEqualNode(object) && this.getParentNode().isSameNode(object.getParentNode());
		}

		@Override
		public boolean isEqualNode(final Node object) {
			return this.equals(object);
		}

		@Override
		public int hashCode() {
			return this.node.hashCode();
		}

		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXElemAdapter)) return false;
			final BEXElemAdapter data = (BEXElemAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return "<" + this.getNodeName() + this.getAttributes() + ">";
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
			public boolean isDerivedFrom(final String typeNamespaceArg, final String typeNameArg, final int derivationMethod) {
				return false;
			}

		};

		/** Dieses Feld speichert die leere {@link NodeList}. */
		public static final NodeList VOID_NODE_LIST = new NodeList() {

			@Override
			public Node item(final int index) {
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
			public void setParameter(final String name, final Object value) throws DOMException {
				throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
			}

			@Override
			public DOMStringList getParameterNames() {
				return BEXDocuAdapter.defaultParameterList;
			}

			@Override
			public Object getParameter(final String name) throws DOMException {
				if (BEXDocuAdapter.defaultParameterListTrue.contains(name)) return Boolean.TRUE;
				if (BEXDocuAdapter.defaultParameterListFalse.contains(name)) return Boolean.FALSE;
				throw new DOMException(DOMException.NOT_FOUND_ERR, null);
			}

			@Override
			public boolean canSetParameter(final String name, final Object value) {
				return false;
			}

		};

		/** Dieses Feld speichert die leere {@link DOMImplementation}. */
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

		/** Dieses Feld speichert die {@link DOMStringList} der leeren {@link DOMConfiguration}.
		 *
		 * @see DOMConfiguration#getParameterNames() */
		static final DOMStringList defaultParameterList = new DOMStringList() {

			@Override
			public String item(final int index) {
				final int offset = BEXDocuAdapter.defaultParameterListTrue.size();
				if (index < offset) return BEXDocuAdapter.defaultParameterListTrue.get(index);
				return BEXDocuAdapter.defaultParameterListFalse.get(index - offset);
			}

			@Override
			public int getLength() {
				return BEXDocuAdapter.defaultParameterListTrue.size() + BEXDocuAdapter.defaultParameterListFalse.size();
			}

			@Override
			public boolean contains(final String str) {
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

		/** Dieser Konstruktor initialisiert das {@link BEXFile}.
		 *
		 * @param file {@link BEXFile}.
		 * @throws NullPointerException Wenn {@code file} {@code null} ist. */
		public BEXDocuAdapter(final BEXFile file) throws NullPointerException {
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
				public Node item(final int index) {
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
		public String lookupPrefix(final String namespaceURI) {
			return null;
		}

		@Override
		public String lookupNamespaceURI(final String prefix) {
			return null;
		}

		@Override
		public boolean isDefaultNamespace(final String uri) {
			return false;
		}

		@Override
		public Element getElementById(final String elementId) {
			return null;
		}

		@Override
		public NodeList getElementsByTagName(final String name) {
			return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public NodeList getElementsByTagNameNS(final String uri, final String name) {
			return new BEXElemCollector(this, uri, name, true);
		}

		@Override
		public Element getDocumentElement() {
			return new BEXElemAdapter(this.node, this);
		}

		@Override
		public Element createElement(final String tagName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public DocumentFragment createDocumentFragment() {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Text createTextNode(final String data) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Comment createComment(final String data) {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public CDATASection createCDATASection(final String data) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Attr createAttribute(final String name) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public EntityReference createEntityReference(final String name) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Node importNode(final Node importedNode, final boolean deep) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
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
		public void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public String getXmlVersion() {
			return "1.0";
		}

		@Override
		public void setXmlVersion(final String xmlVersion) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public boolean getStrictErrorChecking() {
			return true;
		}

		@Override
		public void setStrictErrorChecking(final boolean strictErrorChecking) {
		}

		@Override
		public String getDocumentURI() {
			return null;
		}

		@Override
		public void setDocumentURI(final String documentURI) {
		}

		@Override
		public Node adoptNode(final Node source) throws DOMException {
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
		public Node renameNode(final Node n, final String namespaceURI, final String qualifiedName) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public boolean isSameNode(final Node object) {
			return this.equals(object);
		}

		@Override
		public boolean isEqualNode(final Node object) {
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
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof BEXDocuAdapter)) return false;
			final BEXDocuAdapter data = (BEXDocuAdapter)object;
			return Objects.equals(this.node, data.node);
		}

		@Override
		public String toString() {
			return this.getDocumentElement().toString();
		}

	}

	/** Diese Klasse implementiert einen abstrakten {@link Node}, dessen Methoden keine Modifikation zulassen.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BEXNodeAdapter implements Node {

		/** Dieses Feld speichert den {@link BEXNode}. */
		protected final BEXNode node;

		/** Dieser Konstruktor initialisiert den {@link BEXNode}.
		 *
		 * @param node {@link BEXNode}.
		 * @throws NullPointerException Wenn {@code node} {@code null} ist. */
		public BEXNodeAdapter(final BEXNode node) throws NullPointerException {
			this.node = Objects.notNull(node);
		}

		@Override
		public String getPrefix() {
			return null;
		}

		@Override
		public void setPrefix(final String prefix) throws DOMException {
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
		public void setNodeValue(final String nodeValue) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void setTextContent(final String textContent) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String lookupPrefix(final String uri) {
			return null;
		}

		@Override
		public String lookupNamespaceURI(final String prefix) {
			return null;
		}

		@Override
		public boolean isDefaultNamespace(final String uri) {
			return false;
		}

		@Override
		public Object getFeature(final String feature, final String version) {
			return null;
		}

		@Override
		public boolean isSupported(final String feature, final String version) {
			return false;
		}

		@Override
		public Object getUserData(final String key) {
			return null;
		}

		@Override
		public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
			return null;
		}

		@Override
		public Document getOwnerDocument() {
			return new BEXDocuAdapter(this.node.owner());
		}

		@Override
		public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node cloneNode(final boolean deep) {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node removeChild(final Node node) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node appendChild(final Node node) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public void normalize() {
		}

		@Override
		public short compareDocumentPosition(final Node node) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

	}

	/** Diese Klasse implementiert erweitert den {@link BEXNodeAdapter} und die Methoden {@link #getParentNode()}, {@link #getNextSibling()} und
	 * {@link #getPreviousSibling()} als Basis des {@link BEXTextAdapter} sowie des {@link BEXElemAdapter}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BEXChldAdapter extends BEXNodeAdapter {

		/** Dieses Feld speichert den Elternknoten. */
		protected final Node parent;

		/** Dieser Konstruktor initialisiert {@link BEXNode} und Elternknoten.
		 *
		 * @param node {@link BEXNode}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXChldAdapter(final BEXNode node, final Node parent) throws NullPointerException {
			super(node);
			this.parent = parent;
		}

		@Override
		public Node getParentNode() {
			return this.parent;
		}

		@Override
		public Node getPreviousSibling() {
			final Node parent = this.getParentNode();
			if (parent == null) return null;
			final int index = this.node.index();
			if (index < 0) return null;
			return parent.getChildNodes().item(index - 1);
		}

		@Override
		public Node getNextSibling() {
			final Node parent = this.getParentNode();
			if (parent == null) return null;
			final int index = this.node.index();
			if (index < 0) return null;
			return parent.getChildNodes().item(index + 1);
		}

	}

	/** Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getAttributes()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXAttrListAdapter implements NamedNodeMap {

		/** Dieses Feld speichert den {@link BEXList}. */
		protected final BEXList list;

		/** Dieses Feld speichert den Elternknoten. */
		protected final Element parent;

		/** Dieser Konstruktor initialisiert {@link BEXList} und Elternknoten.
		 *
		 * @param list {@link BEXList}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXAttrListAdapter(final BEXList list, final Element parent) throws NullPointerException {
			this.list = Objects.notNull(list);
			this.parent = Objects.notNull(parent);
		}

		@Override
		public Node item(final int index) {
			final BEXNode node = this.list.get(index);
			if (node.type() == BEXNode.VOID_NODE) return null;
			return new BEXAttrAdapter(node, this.parent);
		}

		@Override
		public int getLength() {
			return this.list.length();
		}

		@Override
		public Node getNamedItem(final String name) {
			return this.getNamedItemNS(XMLConstants.NULL_NS_URI, name);
		}

		@Override
		public Node setNamedItem(final Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node getNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
			final int index = this.list.find(namespaceURI, localName, 0);
			if (index < 0) return null;
			return new BEXAttrAdapter(this.list.get(index), this.parent);
		}

		@Override
		public Node setNamedItemNS(final Node arg) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node removeNamedItem(final String name) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public Node removeNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			for (int i = 0, size = this.getLength(); i < size; i++) {
				builder.append(' ').append(this.item(i));
			}
			return builder.toString();
		}

	}

	/** Diese Klasse implementiert die {@link NodeList} für {@link Element#getChildNodes()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXChldListAdapter implements NodeList {

		/** Dieses Feld speichert den {@link BEXList}. */
		protected final BEXList list;

		/** Dieses Feld speichert den Elternknoten. */
		protected final Node parent;

		/** Dieser Konstruktor initialisiert {@link BEXList} und Elternknoten.
		 *
		 * @param list {@link BEXList}.
		 * @param parent Elternknoten.
		 * @throws NullPointerException Wenn {@code node} bzw. {@code parent} {@code null} ist. */
		public BEXChldListAdapter(final BEXList list, final Node parent) throws NullPointerException {
			this.list = Objects.notNull(list);
			this.parent = Objects.notNull(parent);
		}

		@Override
		public Node item(final int index) {
			final BEXNode node = this.list.get(index);
			if (node.type() == BEXNode.VOID_NODE) return null;
			if (node.type() == BEXNode.ELEM_NODE) return new BEXElemAdapter(node, this.parent);
			return new BEXTextAdapter(node, this.parent);
		}

		@Override
		public int getLength() {
			return this.list.length();
		}

		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			for (int i = 0, size = this.getLength(); i < size; i++) {
				builder.append(this.item(i));
			}
			return builder.toString();
		}

	}

	/** Diese Klasse implementiert die {@link NodeList} für {@link Element#getElementsByTagName(String)}, {@link Element#getElementsByTagNameNS(String, String)},
	 * {@link Document#getElementsByTagName(String)} und {@link Document#getElementsByTagNameNS(String, String)} mit der entsprechenden Sematik beim
	 * Zusammenstellen der Elementknoten.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static class BEXElemCollector implements NodeList {

		/** Dieses Feld speichert die gesuchte {@link BEXNode#uri()} oder {@code "*"}. */
		protected final String uri;

		/** Dieses Feld speichert den gesuchten {@link BEXNode#name()} oder {@code "*"}. */
		protected final String name;

		/** Dieses Feld speichert die gesammelten Elementknoten. */
		protected final List<Node> list;

		/** Dieses Feld speichert die Anzahl der gesammelten Elementknoten. */
		protected final int size;

		/** Dieser Konstruktor initialisiert die Parameter zur Zusammenstellung der {@link BEXNodeAdapter}.
		 *
		 * @param node Elementknoten, dessen Kindknoten rekursiv analysiert werden.
		 * @param uri Uri oder {@code "*"}.
		 * @param name Name oder {@code "*"}.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst auch analysiert werden soll.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
		public BEXElemCollector(final BEXNodeAdapter node, final String uri, final String name, final boolean self) throws NullPointerException {
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

		/** Diese Methode sammelt alle Elementknoten.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElements(final BEXNodeAdapter node, final boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self) {
					this.list.add(node);
				}
				final NodeList children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElements((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		/** Diese Methode sammelt alle Elementknoten mit passendem {@link #uri}.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElementsByUri(final BEXNodeAdapter node, final boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self && this.uri.equals(node.node.uri())) {
					this.list.add(node);
				}
				final NodeList children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElementsByUri((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		/** Diese Methode sammelt alle Elementknoten mit passendem {@link #name}.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElementsByName(final BEXNodeAdapter node, final boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self && this.name.equals(node.node.name())) {
					this.list.add(node);
				}
				final NodeList children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElementsByName((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		/** Diese Methode sammelt alle Elementknoten mit passendem {@link #uri} und {@link #name}.
		 *
		 * @param node Elementknoten.
		 * @param self {@code true}, wenn der gegebene Elementknoten selbst analysiert werden soll. */
		void collectElementsByLabel(final BEXNodeAdapter node, final boolean self) {
			if (node.node.type() == BEXNode.ELEM_NODE) {
				if (self && this.name.equals(node.node.name()) && this.uri.equals(node.node.uri())) {
					this.list.add(node);
				}
				final NodeList children = node.getChildNodes();
				for (int i = 0, length = children.getLength(); i < length; i++) {
					this.collectElementsByLabel((BEXNodeAdapter)children.item(i), true);
				}
			}
		}

		@Override
		public Node item(final int index) {
			if (index < this.size) return this.list.get(index);
			return null;
		}

		@Override
		public int getLength() {
			return this.size;
		}

	}

	/** Diese Methode gibt das gegebene {@link BEXFile} als {@link Document} zurück.
	 *
	 * @param file {@link BEXFile}.
	 * @return {@link Document}.
	 * @throws NullPointerException Wenn {@code file} {@code null} ist. */
	public static Document wrap(final BEXFile file) throws NullPointerException {
		return new BEXDocuAdapter(file);
	}

}
