package bee.creative.xml.view;

import java.util.Iterator;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Diese Schnittstelle definiert die Sicht auf eine {@link NamedNodeMap} mit den Attributkonten eines {@link ElementView}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface AttributesView extends ListView<AttributeView>, PartView {

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
	public AttributeView get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt den {@link AttributeView} mit der URI und dem gegebenen Namen zurück.
	 * 
	 * @see Element#getAttributeNode(String)
	 * @see Element#getAttributeNodeNS(String, String)
	 * @see NamedNodeMap#getNamedItem(String)
	 * @see NamedNodeMap#getNamedItemNS(String, String)
	 * @param uri Uri ({@link Attr#getNamespaceURI()}).
	 * @param name Name ({@link Attr#getLocalName()}).
	 * @return {@link AttributeView} oder {@code null}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public AttributeView get(final String uri, final String name) throws NullPointerException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<AttributeView> iterator();

}