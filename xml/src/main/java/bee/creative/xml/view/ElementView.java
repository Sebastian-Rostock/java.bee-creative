package bee.creative.xml.view;

import org.w3c.dom.Element;

/**
 * Diese Schnittstelle definiert die Sicht auf einen {@link Element}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ElementView extends ChildView, ParentView {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentView document();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ParentView parent();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int index();

	/**
	 * Diese Methode gibt die URI zurück.
	 * 
	 * @see Element#getNamespaceURI()
	 * @return Uri.
	 */
	public String uri();

	/**
	 * Diese Methode gibt den Namen zurück.
	 * 
	 * @see Element#getLocalName()
	 * @return Name.
	 */
	public String name();

	/**
	 * {@inheritDoc}
	 * 
	 * @see Element#getChildNodes()
	 */
	@Override
	public ChildrenView children();

	/**
	 * Diese Methode gibt die {@link AttributeView}s zurück.
	 * 
	 * @see Element#getAttributes()
	 * @return {@link AttributeView}s.
	 */
	public AttributesView attributes();

	/**
	 * Diese Methode gibt {@code null} zurück.
	 * 
	 * @return {@code null}.
	 */
	@Override
	public TextView asText();

	/**
	 * Diese Methode gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 */
	@Override
	public ElementView asElement();

	/**
	 * Diese Methode gibt {@code null} zurück.
	 * 
	 * @return {@code null}.
	 */
	@Override
	public DocumentView asDocument();

}