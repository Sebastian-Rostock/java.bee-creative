package bee.creative.xml.adapter;

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
import bee.creative.util.Objects;
import bee.creative.xml.view.NodeListView;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert ein {@link Document}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class DocumentAdapter extends AbstractNodeAdapter implements Document {

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
	 * Dieses Feld speichert die leere {@link NodeList}.
	 */
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
			return DocumentAdapter.VOID_DOM_PARAMETER_LIST;
		}

		@Override
		public Object getParameter(final String name) throws DOMException {
			if(DocumentAdapter.VOID_DOM_PARAMETER_LIST_TRUE.contains(name)) return Boolean.TRUE;
			if(DocumentAdapter.VOID_DOM_PARAMETER_LIST_FALSE.contains(name)) return Boolean.FALSE;
			throw new DOMException(DOMException.NOT_FOUND_ERR, null);
		}

		@Override
		public boolean canSetParameter(final String name, final Object value) {
			return false;
		}

	};

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
		public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

		@Override
		public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException {
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, null);
		}

	};

	/**
	 * Dieses Feld speichert die {@link DOMStringList} der leeren {@link DOMConfiguration}.
	 * 
	 * @see DOMConfiguration#getParameterNames()
	 */
	static final DOMStringList VOID_DOM_PARAMETER_LIST = new DOMStringList() {

		@Override
		public String item(final int index) {
			final int offset = DocumentAdapter.VOID_DOM_PARAMETER_LIST_TRUE.size();
			if(index < offset) return DocumentAdapter.VOID_DOM_PARAMETER_LIST_TRUE.get(index);
			return DocumentAdapter.VOID_DOM_PARAMETER_LIST_FALSE.get(index - offset);
		}

		@Override
		public int getLength() {
			return DocumentAdapter.VOID_DOM_PARAMETER_LIST_TRUE.size() + DocumentAdapter.VOID_DOM_PARAMETER_LIST_FALSE.size();
		}

		@Override
		public boolean contains(final String str) {
			return DocumentAdapter.VOID_DOM_PARAMETER_LIST_TRUE.contains(str) || DocumentAdapter.VOID_DOM_PARAMETER_LIST_FALSE.contains(str);
		}

	};

	/**
	 * Dieses Feld speichert die {@code true-Parameter} der leeren {@link DOMConfiguration}.
	 * 
	 * @see DOMConfiguration#getParameter(String)
	 */
	static final List<String> VOID_DOM_PARAMETER_LIST_TRUE = Collections.unmodifiableList(Arrays.asList("comments", "datatype-normalization", "well-formed",
		"namespaces", "namespace-declarations", "element-content-whitespace"));

	/**
	 * Dieses Feld speichert die {@code false-Parameter} der leeren {@link DOMConfiguration}.
	 * 
	 * @see DOMConfiguration#getParameter(String)
	 */
	static final List<String> VOID_DOM_PARAMETER_LIST_FALSE = Collections.unmodifiableList(Arrays.asList("cdata-sections", "entities", "split-cdata-sections",
		"validate", "infoset", "normalize-characters", "canonical-form", "validate-if-schema", "check-character-normalization"));

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeView}.
	 * 
	 * @param nodeView {@link NodeView}.
	 * @throws NullPointerException Wenn der {@link NodeView} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der {@link NodeView} keinen Elementknoten als einziges Kindelement besitzt.
	 */
	public DocumentAdapter(final NodeView nodeView) throws NullPointerException, IllegalArgumentException {
		super(nodeView);
		final NodeListView children = nodeView.children();
		if((children.size() != 1) || (children.get(0).type() != NodeView.TYPE_ELEMENT)) throw new IllegalArgumentException();
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
	public Node getFirstChild() {
		return this.getDocumentElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getLastChild() {
		return this.getDocumentElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeList getChildNodes() {
		return new NodeListAdapter(this.nodeView.children());
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
		return this.getDocumentElement().getTextContent();
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
		return DocumentAdapter.VOID_DOM_IMPLEMENTATION;
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
		final NodeView nodeView = this.nodeView.element(elementId);
		if(nodeView == null) return null;
		return new ElementAdapter(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeList getElementsByTagName(final String name) {
		return this.getElementsByTagNameNS(XMLConstants.NULL_NS_URI, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeList getElementsByTagNameNS(final String uri, final String name) {
		return new ElementCollector(this.nodeView.children(), uri, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Element getDocumentElement() {
		return new ElementAdapter(this.nodeView.children().get(0));
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
	public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
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
		return DocumentAdapter.VOID_DOM_CONFIGURATION;
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
		return this.nodeView.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof DocumentAdapter)) return false;
		final DocumentAdapter data = (DocumentAdapter)object;
		return Objects.equals(this.nodeView, data.nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getDocumentElement().toString();
	}

}