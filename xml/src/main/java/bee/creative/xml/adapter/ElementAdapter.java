package bee.creative.xml.adapter;

import javax.xml.XMLConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import bee.creative.util.Objects;
import bee.creative.xml.view.NodeListView;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert ein {@link Element}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class ElementAdapter extends AbstractChildNodeAdapter implements Element {

	/**
	 * Diese Methode implementeirt {@link #getTextContent()}.
	 * 
	 * @param content {@link StringBuffer} mit dem bisher gesammelten Texten.
	 * @param children {@link NodeListView} der rekursiv analysierten Kindknoten.
	 * @throws NullPointerException Wenn eine der Eingabe {@code null} ist.
	 */
	static final void collectContent(final StringBuffer content, final NodeListView children) throws NullPointerException {
		for (final NodeView child: children) {
			if (child.type() == NodeView.TYPE_ELEMENT) {
				ElementAdapter.collectContent(content, child.children());
			} else {
				content.append(child.value());
			}
		}
	}

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeView}.
	 * 
	 * @param nodeView {@link NodeView}.
	 * @throws NullPointerException Wenn der {@link NodeView} {@code null} ist.
	 */
	public ElementAdapter(final NodeView nodeView) throws NullPointerException {
		super(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrefix() {
		return this.nodeView.document().lookupPrefix(this.nodeView.uri());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamespaceURI() {
		return this.nodeView.uri();
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
		final String xmlnsName = this.lookupPrefix(this.nodeView.uri());
		final String elementName = this.nodeView.name();
		if (xmlnsName == null) return elementName;
		return xmlnsName + ":" + elementName;
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
		return this.getNodeName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalName() {
		return this.nodeView.name();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getFirstChild() {
		final NodeList c = this.getChildNodes();
		return c.item(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getLastChild() {
		final NodeList c = this.getChildNodes();
		return c.item(c.getLength() - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildNodes() {
		return this.nodeView.children().size() != 0;
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
	public NamedNodeMap getAttributes() {
		return new NamedNodeMapAdapter(this.nodeView.attributes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAttributes() {
		return this.nodeView.attributes().size() != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAttribute(final String name) {
		return this.getAttributeNS(XMLConstants.NULL_NS_URI, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAttribute(final String name) {
		return this.hasAttributeNS(XMLConstants.NULL_NS_URI, name);
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
		final NodeView nodeView = this.nodeView.attributes().get(uri, name, 0);
		if (nodeView == null) return "";
		return nodeView.value();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAttributeNS(final String uri, final String name) throws DOMException {
		return this.nodeView.attributes().get(uri, name, 0) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Attr getAttributeNode(final String name) {
		return this.getAttributeNodeNS(XMLConstants.NULL_NS_URI, name);
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
		final NodeView nodeView = this.nodeView.attributes().get(uri, name, 0);
		if (nodeView == null) return null;
		return new AttrAdapter(nodeView);
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
	public void setIdAttributeNS(final String namespaceURI, final String localName, final boolean isId) throws DOMException {
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
		final StringBuffer content = new StringBuffer();
		ElementAdapter.collectContent(content, this.nodeView.children());
		return content.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeInfo getSchemaTypeInfo() {
		return DocumentAdapter.VOID_TYPE_INFO;
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
		return this.nodeView.document().lookupPrefix(uri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String lookupNamespaceURI(final String prefix) {
		return this.nodeView.document().lookupURI(prefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDefaultNamespace(final String uri) {
		return uri.equals(this.lookupNamespaceURI(XMLConstants.DEFAULT_NS_PREFIX));
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
	public NodeList getElementsByTagNameNS(final String uri, final String name) throws DOMException {
		return new ElementCollector(this.nodeView.children(), uri, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSameNode(final Node object) {
		return this.isEqualNode(object) && this.getParentNode().isSameNode(object.getParentNode());
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
	public int hashCode() {
		return this.nodeView.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) return true;
		if (!(object instanceof ElementAdapter)) return false;
		final ElementAdapter data = (ElementAdapter)object;
		return Objects.equals(this.nodeView, data.nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "<" + this.getNodeName() + this.getAttributes() + ">";
	}

}