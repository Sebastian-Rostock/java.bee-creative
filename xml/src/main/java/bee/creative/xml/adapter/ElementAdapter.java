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
import bee.creative.xml.view.AttributeView;
import bee.creative.xml.view.ChildView;
import bee.creative.xml.view.ChildrenView;
import bee.creative.xml.view.ElementView;
import bee.creative.xml.view.TextView;

/**
 * Diese Klasse implementiert ein {@link Element}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class ElementAdapter extends ChildAdapter implements Element {

	/**
	 * Diese Methode implementeirt {@link #getTextContent()}.
	 * 
	 * @param content {@link StringBuffer} mit dem bisher gesammelten Texten.
	 * @param children {@link ChildrenView} der rekursiv analysierten Kindknoten.
	 * @throws NullPointerException Wenn eine der Eingabe {@code null} ist.
	 */
	static final void collectContent(final StringBuffer content, final ChildrenView children) throws NullPointerException {
		for(final ChildView child: children){
			final TextView text = child.asText();
			if(text != null){
				content.append(text.value());
			}else{
				ElementAdapter.collectContent(content, child.asElement().children());
			}
		}
	}

	/**
	 * Dieses Feld speichert den {@link ElementView}.
	 */
	protected final ElementView elementView;

	/**
	 * Dieser Konstruktor initialisiert den {@link ElementView}.
	 * 
	 * @param elementView {@link ElementView}.
	 * @throws NullPointerException Wenn der {@link ElementView} {@code null} ist.
	 */
	public ElementAdapter(final ElementView elementView) throws NullPointerException {
		if(elementView == null) throw new NullPointerException();
		this.elementView = elementView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ChildView view() {
		return this.elementView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrefix() {
		return this.elementView.document().lookupPrefix(this.elementView.uri());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamespaceURI() {
		return this.elementView.uri();
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
		final String xmlnsName = this.lookupPrefix(this.elementView.uri());
		final String elementName = this.elementView.name();
		if(xmlnsName == null) return elementName;
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
		return this.elementView.name();
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
		return this.elementView.children().size() != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeList getChildNodes() {
		return new ChildrenAdapter(this.elementView.children());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NamedNodeMap getAttributes() {
		return new AttributesAdapter(this.elementView.attributes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAttributes() {
		return this.elementView.attributes().size() != 0;
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
		final AttributeView attributeView = this.elementView.attributes().get(uri, name);
		if(attributeView == null) return "";
		return attributeView.value();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAttributeNS(final String uri, final String name) throws DOMException {
		return this.elementView.attributes().get(uri, name) != null;
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
		final AttributeView attributeView = this.elementView.attributes().get(uri, name);
		if(attributeView == null) return null;
		return new AttributeAdapter(attributeView);
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
		ElementAdapter.collectContent(content, this.elementView.children());
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
		return this.elementView.document().lookupPrefix(uri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String lookupNamespaceURI(final String prefix) {
		return this.elementView.document().lookupURI(prefix);
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
		return new ElementsAdapter(this.elementView.children(), uri, name);
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
		return this.elementView.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object object) {
		if(object == this) return true;
		if(!(object instanceof ElementAdapter)) return false;
		final ElementAdapter data = (ElementAdapter)object;
		return Objects.equals(this.elementView, data.elementView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "<" + this.getNodeName() + this.getAttributes() + ">";
	}

}