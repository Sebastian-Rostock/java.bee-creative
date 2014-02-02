package bee.creative.xml.view;

import org.w3c.dom.Text;

/**
 * Diese Schnittstelle definiert die Sicht auf einen {@link Text}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface TextView extends ChildView {

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
	 * Diese Methode gibt den Textwert zurück.
	 * 
	 * @see Text#getData()
	 * @return Textwert.
	 */
	public String value();

	/**
	 * Diese Methode gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 */
	@Override
	public TextView asText();

	/**
	 * Diese Methode gibt {@code null} zurück.
	 * 
	 * @return {@code null}.
	 */
	@Override
	public ElementView asElement();

}