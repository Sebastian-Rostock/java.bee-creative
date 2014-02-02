package bee.creative.xml.view;

import org.w3c.dom.Node;

/**
 * Diese Schnittstelle definiert die Sicht auf einen allgemeinen {@link Node} mit besitzendem {@link DocumentView}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface NodeView {

	/**
	 * Diese Methode gibt den besitzenden {@link DocumentView} zurück.
	 * 
	 * @see Node#getOwnerDocument()
	 * @return {@link DocumentView}.
	 */
	public DocumentView document();

}