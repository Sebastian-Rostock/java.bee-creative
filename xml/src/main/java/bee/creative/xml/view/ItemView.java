package bee.creative.xml.view;

/**
 * Diese Schnittstelle definiert einen allgemeinen {@link PartView} mit Positionsangabe bezogen auf seinen {@link ParentView}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ItemView extends PartView {

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
	 * Diese Methode gibt die Position des {@link ItemView} in seinem übergeotdneten {@link ParentView} zurück.
	 * 
	 * @return Position im {@link ParentView}.
	 */
	public int index();

}