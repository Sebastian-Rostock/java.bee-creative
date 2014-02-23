package bee.creative.xml.adapter;

import org.w3c.dom.Node;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert erweitert den {@link AbstractNodeAdapter} und die Methoden {@link #getParentNode()}, {@link #getNextSibling()} und
 * {@link #getPreviousSibling()} als Basis des {@link TextAdapter}s sowie des {@link ElementAdapter}s.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class AbstractChildNodeAdapter extends AbstractNodeAdapter {

	/**
	 * Dieser Konstruktor initialisiert den {@link NodeView}.
	 * 
	 * @param nodeView {@link NodeView}.
	 * @throws NullPointerException Wenn der {@link NodeView} {@code null} ist.
	 */
	public AbstractChildNodeAdapter(NodeView nodeView) throws NullPointerException {
		super(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getParentNode() {
		final NodeView nodeView = this.nodeView.parent();
		if(nodeView == null) return null;
		if(nodeView.type() == NodeView.TYPE_ELEMENT) return new ElementAdapter(nodeView);
		return new DocumentAdapter(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getPreviousSibling() {
		final Node parent = this.getParentNode();
		if(parent == null) return null;
		return parent.getChildNodes().item(this.nodeView.index() - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getNextSibling() {
		final Node parent = this.getParentNode();
		if(parent == null) return null;
		return parent.getChildNodes().item(this.nodeView.index() + 1);
	}

}