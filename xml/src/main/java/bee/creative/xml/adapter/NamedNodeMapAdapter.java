package bee.creative.xml.adapter;

import javax.xml.XMLConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import bee.creative.xml.view.NodeListView;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getAttributes()}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class NamedNodeMapAdapter implements NamedNodeMap {

	/**
	 * Dieses Feld speichert den {@link NodeListView}.
	 */
	protected final NodeListView listView;

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeListView}.
	 * 
	 * @param attributesView {@link NodeListView}.
	 * @throws NullPointerException Wenn der {@link NodeListView} {@code null} ist.
	 */
	public NamedNodeMapAdapter(final NodeListView attributesView) throws NullPointerException {
		if(attributesView == null) throw new NullPointerException();
		this.listView = attributesView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node item(final int index) {
		final NodeView attributeView = this.listView.get(index);
		if(attributeView == null) return null;
		return new AttrAdapter(attributeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.listView.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getNamedItem(final String name) {
		return this.getNamedItemNS(XMLConstants.NULL_NS_URI, name);
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
	public Node getNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
		final NodeView nodeView = this.listView.get(namespaceURI, localName, 0);
		if(nodeView == null) return null;
		return new AttrAdapter(nodeView);
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
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		for(int i = 0, size = this.getLength(); i < size; i++){
			builder.append(' ').append(this.item(i));
		}
		return builder.toString();
	}

}