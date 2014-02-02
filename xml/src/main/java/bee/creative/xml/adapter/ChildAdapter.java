package bee.creative.xml.adapter;

import org.w3c.dom.Node;
import bee.creative.xml.view.ChildView;
import bee.creative.xml.view.ElementView;
import bee.creative.xml.view.ParentView;

/**
 * Diese Klasse implementiert erweitert den {@link NodeAdapter} und die Methoden {@link #getParentNode()}, {@link #getNextSibling()} und
 * {@link #getPreviousSibling()} als Basis des {@link TextAdapter}s sowie des {@link ElementAdapter}s.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public abstract class ChildAdapter extends NodeAdapter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract ChildView view();

	/**
	 * {@inheritDoc}
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getParentNode() {
		final ParentView parentView = this.view().parent();
		if(parentView == null) return null;
		final ElementView elementView = parentView.asElement();
		if(elementView != null) return new ElementAdapter(elementView);
		return new DocumentAdapter(parentView.asDocument());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getPreviousSibling() {
		final Node parent = this.getParentNode();
		if(parent == null) return null;
		return parent.getChildNodes().item(this.view().index() - 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node getNextSibling() {
		final Node parent = this.getParentNode();
		if(parent == null) return null;
		return parent.getChildNodes().item(this.view().index() + 1);
	}

}