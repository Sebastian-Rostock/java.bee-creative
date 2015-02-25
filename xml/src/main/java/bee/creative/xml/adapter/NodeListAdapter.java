package bee.creative.xml.adapter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.xml.view.NodeListView;
import bee.creative.xml.view.NodeView;

/**
 * Diese Klasse implementiert die {@link NodeList} für {@link Element#getChildNodes()}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class NodeListAdapter implements NodeList {

	/**
	 * Dieses Feld speichert den {@link NodeListView}.
	 */
	protected final NodeListView listView;

	/**
	 * Dieser Konstruktor initialisiert den NodeListView.
	 * 
	 * @param listView {@link NodeListView}.
	 * @throws NullPointerException Wenn der {@link NodeListView} {@code null} ist.
	 */
	public NodeListAdapter(final NodeListView listView) throws NullPointerException {
		if (listView == null) throw new NullPointerException();
		this.listView = listView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node item(final int index) {
		final NodeView nodeView = this.listView.get(index);
		if (nodeView == null) return null;
		if (nodeView.type() == NodeView.TYPE_ELEMENT) return new ElementAdapter(nodeView);
		return new TextAdapter(nodeView);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.listView.size();
	}

}