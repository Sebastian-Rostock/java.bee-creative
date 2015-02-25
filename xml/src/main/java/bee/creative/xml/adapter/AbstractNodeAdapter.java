package bee.creative.xml.adapter;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import bee.creative.util.Objects;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert einen abstrakten {@link Node}, dessen Methoden keine Modifikation zulassen.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class AbstractNodeAdapter implements Node {

	/**
	 * Dieses Feld speichert den {@link NodeView}.
	 */
	protected final NodeView nodeView;

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeView}.
	 * 
	 * @param nodeView {@link NodeView}.
	 * @throws NullPointerException Wenn der {@link NodeView} {@code null} ist.
	 */
	public AbstractNodeAdapter(final NodeView nodeView) throws NullPointerException {
		if (nodeView == null) throw new NullPointerException();
		this.nodeView = nodeView;
	}

	/**
	 * Diese Methode gibt den {@link NodeView} zurück.
	 * 
	 * @return {@link NodeView}.
	 */
	public NodeView nodeView() {
		return this.nodeView;
	}

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
	public void setPrefix(final String prefix) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
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
	public String getNamespaceURI() {
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
		return DocumentAdapter.VOID_NODE_LIST;
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
		return this.nodeView.lookupPrefix(uri);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String lookupNamespaceURI(final String prefix) {
		return this.nodeView.lookupURI(prefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDefaultNamespace(final String uri) {
		return (uri != null) && Objects.equals(uri, this.nodeView.lookupURI(null));
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
		return new DocumentAdapter(this.nodeView.document());
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