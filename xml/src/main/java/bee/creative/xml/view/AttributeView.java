package bee.creative.xml.view;

import org.w3c.dom.Attr;

/**
 * Diese Schnittstelle definiert die Sicht auf einen {@link Attr}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface AttributeView extends ItemView {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentView document();

	/**
	 * Diese Methode gibt den übergeordneten {@link ElementView} zurück.
	 */
	@Override
	public ElementView parent();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int index();

	/**
	 * Diese Methode gibt die URI zurück.
	 * 
	 * @see Attr#getNamespaceURI()
	 * @return Uri.
	 */
	public String uri();

	/**
	 * Diese Methode gibt den Namen zurück.
	 * 
	 * @see Attr#getLocalName()
	 * @return Name.
	 */
	public String name();

	/**
	 * Diese Methode gibt den Wert zurück.
	 * 
	 * @see Attr#getValue()
	 * @return Wert.
	 */
	public String value();

}