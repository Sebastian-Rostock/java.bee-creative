package bee.creative.xml.adapter;

import javax.xml.XMLConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import bee.creative.xml.view.AttributeView;
import bee.creative.xml.view.AttributesView;

/**
 * Diese Klasse implementiert die {@link NamedNodeMap} für {@link Element#getAttributes()}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class AttributesAdapter implements NamedNodeMap {

	/**
	 * Dieses Feld speichert den {@link AttributesView}.
	 */
	protected final AttributesView attributesView;

	/**
	 * Dieser Konstruktor initialisiert den {@link AttributesView}.
	 * 
	 * @param attributesView {@link AttributesView}.
	 * @throws NullPointerException Wenn der {@link AttributesView} {@code null} ist.
	 */
	public AttributesAdapter(final AttributesView attributesView) throws NullPointerException {
		if(attributesView == null) throw new NullPointerException();
		this.attributesView = attributesView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node item(final int index) {
		final AttributeView attributeView = this.attributesView.get(index);
		if(attributeView == null) return null;
		return new AttributeAdapter(attributeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.attributesView.size();
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
		final AttributeView attributeView = this.attributesView.get(namespaceURI, localName);
		if(attributeView == null) return null;
		return new AttributeAdapter(attributeView);
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