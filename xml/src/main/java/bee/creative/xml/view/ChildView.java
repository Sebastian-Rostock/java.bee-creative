package bee.creative.xml.view;

/**
 * Diese Schnittstelle definiert das allgemeinene Element eines {@link ChildrenView}, welches ein {@link TextView} oder {@link ElementView} sein kann.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ChildView extends ItemView {

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
	 * Diese Methode gibt nur dann einen {@link TextView} zurück, wenn dieses Objekt einen solchen vertritt.
	 * 
	 * @return {@link TextView} oder {@code null}.
	 */
	public TextView asText();

	/**
	 * Diese Methode gibt nur dann einen {@link ElementView} zurück, wenn dieses Objekt einen solchen vertritt.
	 * 
	 * @return {@link ElementView} oder {@code null}.
	 */
	public ElementView asElement();

}