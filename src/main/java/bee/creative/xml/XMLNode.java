package bee.creative.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Diese Klasse implementiert den Konfigurator für {@link Document}-, {@link Attr}-, {@link Text}- und {@link Element}-Knoten.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLNode extends BaseNodeData<XMLNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final XMLNode use(final BaseNodeData<?> data) {
		return super.use(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final XMLNode useNode(final Node node) {
		return super.useNode(node);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final XMLNode _this_() {
		return this;
	}

}