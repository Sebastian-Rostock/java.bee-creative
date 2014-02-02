package bee.creative.xml.view;

/**
 * Diese Schnittstelle definiert einen allgemeinen {@link NodeView} mit übergeordnetem {@link ParentView}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface PartView extends NodeView {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DocumentView document();

	/**
	 * Diese Methode gibt den übergeordneten {@link ParentView} zurück.
	 * 
	 * @return {@link ParentView}.
	 */
	public ParentView parent();

}