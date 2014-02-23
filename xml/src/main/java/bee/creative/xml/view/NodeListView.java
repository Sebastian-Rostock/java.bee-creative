package bee.creative.xml.view;

import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import bee.creative.util.Comparables.Get;
import bee.creative.util.Iterators.GetIterator;

/**
 * Diese Schnittstelle definiert eine iterierbare Auflistung von Kind- bzw. Attributknoten.
 * 
 * @see NodeView
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface NodeListView extends Get<NodeView>, Iterable<NodeView> {

	/**
	 * Diese Methode gibt das index-ten Kind- bzw. Attributknoten zurück. Wenn der gegebene Index größer als oder gleich der {@link #size() Anzahl der Knoten}
	 * ist, wird {@code null} zurück gegeben.
	 */
	@Override
	public NodeView get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt den Element- bzw. Attributknoten mit dem gegebenen URI und dem gegebenen Namen zurück. Elementknoten werden linear ab der gegebenen
	 * Postion gesucht. Attributknoten werden linear oder binär ab der gegebenen Postion gesucht.
	 * 
	 * @see Element#getAttributeNode(String)
	 * @see Element#getAttributeNodeNS(String, String)
	 * @see NamedNodeMap#getNamedItem(String)
	 * @see NamedNodeMap#getNamedItemNS(String, String)
	 * @param uri URI oder {@code null}.
	 * @param name Name.
	 * @param index Startposition der Linearsuche.
	 * @return Element- bzw. Attributknoten oder {@code null}.
	 * @throws NullPointerException Wenn der Name {@code null} ist.
	 */
	public NodeView get(final String uri, final String name, int index) throws NullPointerException;

	/**
	 * Diese Methode gibt die Anzahl der Kind- bzw. Attributknoten zurück.
	 * 
	 * @return Anzahl der Kind- bzw. Attributknoten.
	 */
	public int size();

	/**
	 * Diese Methode gibt den diese Knotenliste besitzenden Dokument- bzw. Elementknoten zurück.
	 * 
	 * @return Dokument- bzw. Elementknoten.
	 */
	public NodeView owner();

	/**
	 * {@inheritDoc}
	 * 
	 * @see GetIterator
	 */
	@Override
	public Iterator<NodeView> iterator();

}