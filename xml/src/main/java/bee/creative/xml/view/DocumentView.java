package bee.creative.xml.view;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Diese Schnittstelle definiert die Sicht auf ein {@link Document} als spezialisierung eines {@link ParentView}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface DocumentView extends ParentView {

	/**
	 * Diese Methode gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 */
	@Override
	public DocumentView document();

	/**
	 * Diese Methode gibt den {@link ElementView} zur gegebenen {@code ID} zurück.
	 * 
	 * @see Document#getElementById(String)
	 * @param id {@code ID}.
	 * @return {@link ElementView} oder {@code null}.
	 */
	public ElementView element(String id);

	/**
	 * {@inheritDoc}
	 * 
	 * @see Document#getTextContent()
	 */
	@Override
	public ChildrenView children();

	/**
	 * Diese Methode gibt {@code null} zurück.
	 * 
	 * @return {@code null}.
	 */
	@Override
	public ElementView asElement();

	/**
	 * Diese Methode gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 */
	@Override
	public DocumentView asDocument();

	/**
	 * Diese Methode gibt die Uri zum gegebenen Prefix zurück.
	 * 
	 * @see Node#lookupNamespaceURI(String)
	 * @param prefix Prefix oder {@code null}.
	 * @return Uri oder {@code null}.
	 */
	public String lookupURI(final String prefix);

	/**
	 * Diese Methode gibt das Prefix zur gegebenen Uri zurück.
	 * 
	 * @see Node#lookupPrefix(String)
	 * @param uri Uri.
	 * @return Prefix oder {@code null}.
	 */
	public String lookupPrefix(final String uri);

}