package bee.creative.xml.view;

import java.util.Iterator;
import org.w3c.dom.NodeList;

/**
 * Diese Schnittstelle definiert die Sicht auf eine {@link NodeList} mit den Kindknoten eines {@link ParentView}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ChildrenView extends ListView<ChildView>, PartView {

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
	public ChildView get(int index) throws IndexOutOfBoundsException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<ChildView> iterator();

}