package bee.creative.xml.adapter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.xml.view.ChildView;
import bee.creative.xml.view.ChildrenView;
import bee.creative.xml.view.TextView;

/**
 * Diese Klasse implementiert die {@link NodeList} für {@link Element#getChildNodes()}.
 * 
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class ChildrenAdapter implements NodeList {

	/**
	 * Dieses Feld speichert den {@link ChildrenView}.
	 */
	protected final ChildrenView childrenView;

	/**
	 * Dieser Konstruktor initialisiert den ChildrenView.
	 * 
	 * @param childrenView {@link ChildrenView}.
	 * @throws NullPointerException Wenn der {@link ChildrenView} {@code null} ist.
	 */
	public ChildrenAdapter(final ChildrenView childrenView) throws NullPointerException {
		if(childrenView == null) throw new NullPointerException();
		this.childrenView = childrenView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Node item(final int index) {
		final ChildView childView = this.childrenView.get(index);
		if(childView == null) return null;
		final TextView textView = childView.asText();
		if(textView != null) return new TextAdapter(textView);
		return new ElementAdapter(childView.asElement());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLength() {
		return this.childrenView.size();
	}

}