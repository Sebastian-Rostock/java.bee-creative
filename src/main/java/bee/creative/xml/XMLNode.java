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
	public XMLNode use(BaseNodeData<?> data) {
		return super.use(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XMLNode use(Node node) {
		return super.use(node);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected XMLNode thiz() {
		return this;
	}

}