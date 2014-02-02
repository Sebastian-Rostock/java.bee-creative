package bee.creative.xml.view;

import org.w3c.dom.Node;

/**
 * Diese Schnittstelle definiert die Sicht auf einen allgemeinen {@link Node} mit {@link #children() Kindknoten}, welcher die Basis des {@link ElementView}
 * sowie des {@link DocumentView} beschreibt.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ParentView extends NodeView {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentView document();

	/**
	 * Diese Methode gibt die {@link ChildrenView Sicht auf die Kindknoten} zurück.
	 * 
	 * @return {@link ChildrenView}.
	 */
	public ChildrenView children();

	/**
	 * Diese Methode gibt nur dann einen {@link ElementView} zurück, wenn dieses Objekt einen solchen vertritt.
	 * 
	 * @return {@link ElementView} oder {@code null}.
	 */
	public ElementView asElement();

	/**
	 * Diese Methode gibt nur dann einen {@link DocumentView} zurück, wenn dieses Objekt einen solchen vertritt.
	 * 
	 * @return {@link DocumentView} oder {@code null}.
	 */
	public DocumentView asDocument();

}
