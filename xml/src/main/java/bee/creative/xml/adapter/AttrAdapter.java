package bee.creative.xml.adapter;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;
import bee.creative.util.Objects;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert ein {@link Attr} als {@link AbstractNodeAdapter}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class AttrAdapter extends AbstractNodeAdapter implements Attr {

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeView}.
	 * 
	 * @param nodeView {@link NodeView}.
	 * @throws NullPointerException Wenn der {@link NodeView} {@code null} ist.
	 */
	public AttrAdapter(final NodeView nodeView) throws NullPointerException {
		super(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPrefix() {
		return this.nodeView.lookupPrefix(this.nodeView.uri());
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
		return Node.ATTRIBUTE_NODE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNodeName() {
		final String xmlnsName = this.lookupPrefix(this.nodeView.uri());
		final String attributeName = this.nodeView.name();
		if(xmlnsName == null) return attributeName;
		return xmlnsName + ":" + attributeName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNodeValue() throws DOMException {
		return this.nodeView.value();
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
	public Node getParentNode() {
		return this.getOwnerElement();
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
		return new ElementAdapter(this.nodeView.parent());
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
	public boolean isId() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSameNode(final Node object) {
		return this.equals(object) && this.getParentNode().isSameNode(object.getParentNode());
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
		if(object == this) return true;
		if(!(object instanceof AttrAdapter)) return false;
		final AttrAdapter data = (AttrAdapter)object;
		return Objects.equals(this.nodeView, data.nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getNodeName() + "=" + Objects.toString(this.getNodeValue());
	}

}