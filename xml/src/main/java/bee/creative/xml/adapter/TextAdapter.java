package bee.creative.xml.adapter;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import bee.creative.util.Objects;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert ein {@link Text}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class TextAdapter extends AbstractChildNodeAdapter implements Text {

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeView}.
	 * 
	 * @param nodeView {@link NodeView}.
	 * @throws NullPointerException Wenn der {@link NodeView} {@code null} ist.
	 */
	public TextAdapter(final NodeView nodeView) throws NullPointerException {
		super(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getNodeType() {
		return Node.TEXT_NODE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNodeName() {
		return "#text";
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
	public String getData() throws DOMException {
		return this.getNodeValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final String data) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.getNodeValue().length();
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
	public String getWholeText() {
		return this.getNodeValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String substringData(final int offset, final int count) throws DOMException {
		try {
			return this.getNodeValue().substring(offset, offset + count);
		} catch (final IndexOutOfBoundsException e) {
			throw new DOMException(DOMException.INDEX_SIZE_ERR, e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text splitText(final int offset) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertData(final int offset, final String arg) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteData(final int offset, final int count) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendData(final String arg) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceData(final int offset, final int count, final String arg) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Text replaceWholeText(final String content) throws DOMException {
		throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, null);
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
	public boolean isElementContentWhitespace() {
		final String nodeValue = this.getNodeValue();
		for (int i = 0, size = nodeValue.length(); i < size; i++) {
			final char value = nodeValue.charAt(i);
			if ((value > 0x20) || (value < 0x09)) return false;
			if ((value != 0x0A) && (value != 0x0D)) return false;
		}
		return true;
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
		if (!(object instanceof TextAdapter)) return false;
		final TextAdapter data = (TextAdapter)object;
		return Objects.equals(this.nodeView, data.nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.getNodeValue();
	}

}