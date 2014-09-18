package bee.creative.xml.view;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Diese Schnittstelle definiert eine vereinfachte Sicht auf die Knoten eines XML Dokuments.
 * 
 * @see Node
 * @see Attr
 * @see Element
 * @see Document
 * @see NodeListView
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface NodeView {

	/**
	 * Dieses Feld speichert den Typ eines Textknoten.
	 */
	public static final int TYPE_TEXT = 4;

	/**
	 * Dieses Feld speichert den Typ eines Elementknoten.
	 */
	public static final int TYPE_ELEMENT = 2;

	/**
	 * Dieses Feld speichert den Typ eines Dokumentknoten.
	 */
	public static final int TYPE_DOCUMENT = 1;

	/**
	 * Dieses Feld speichert den Typ eines Attributknoten.
	 */
	public static final int TYPE_ATTRIBUTE = 8;

	/**
	 * Diese Methode gibt den Typ des Knoten zurück.
	 * 
	 * @see #TYPE_TEXT
	 * @see #TYPE_ELEMENT
	 * @see #TYPE_DOCUMENT
	 * @see #TYPE_ATTRIBUTE
	 * @return Typ des Knoten.
	 */
	public int type();

	/**
	 * Diese Methode gibt den URI zurück. Bei einem Text- oder Dokumentknoten ist der Rückgabewert {@code null}.
	 * 
	 * @see Attr#getNamespaceURI()
	 * @see Element#getNamespaceURI()
	 * @return Uri oder {@code null}.
	 */
	public String uri();

	/**
	 * Diese Methode gibt den Namen zurück. Bei einem Text- oder Dokumentknoten ist der Rückgabewert {@code null}.
	 * 
	 * @see Attr#getLocalName()
	 * @see Element#getLocalName()
	 * @return Name oder {@code null}.
	 */
	public String name();

	/**
	 * Diese Methode gibt den Wert zurück. Bei einem Elementknoten ohne Kindelementknoten entspricht dieser Wert dem Textinhalt. Bei einem Elementknoten mit
	 * Kindelementknoten oder einem Dokumentknoten ist der Rückgabewert {@code null}.
	 * 
	 * @see Attr#getValue()
	 * @see Text#getData()
	 * @see Element#getTextContent()
	 * @return Wert oder {@code null}.
	 */
	public String value();

	/**
	 * Diese Methode gibt die Position dieses Kind- bzw. Attributknoten in seinem übergeotdneten Dokument- bzw. Elementknoten zurück. Bei einem Dokumentknoten ist
	 * der Rückgabewert {@code -1}.
	 * 
	 * @return Position dieses Knoten oder {@code -1}.
	 */
	public int index();

	/**
	 * Diese Methode gibt den übergeotdneten Dokument- bzw. Elementknoten zurück. Bei einem Dokumentknoten ist der Rückgabewert {@code null}.
	 * 
	 * @see Attr#getOwnerElement()
	 * @see Element#getParentNode()
	 * @return Dokument- bzw. Elementknoten.
	 */
	public NodeView parent();

	/**
	 * Diese Methode gibt den Elementknoten zur gegebenen {@code ID} zurück. Wenn diese Methode nicht unterstützt wird, ist der Rückgabewert {@code null}.
	 * 
	 * @see Document#getElementById(String)
	 * @param id {@code ID}.
	 * @return Elementknoten oder {@code null}.
	 */
	public NodeView element(String id);

	/**
	 * Diese Methode gibt die Kindknoten zurück. Bei einem Text- oder Attributknoten ist der Rückgabewert {@code null}.
	 * 
	 * @see Element#getChildNodes()
	 * @see Document#getChildNodes()
	 * @return Kindknoten.
	 */
	public NodeListView children();

	/**
	 * Diese Methode gibt die Attributknoten zurück. Bei einem Text-, Attribut- oder Dokumentknoten ist der Rückgabewert {@code null}.
	 * 
	 * @see Element#getAttributes()
	 * @return Attributknoten.
	 */
	public NodeListView attributes();

	/**
	 * Diese Methode gibt den besitzenden Dokumentknoten zurück.
	 * 
	 * @see Node#getOwnerDocument()
	 * @return Dokumentknoten.
	 */
	public NodeView document();

	/**
	 * Diese Methode gibt die Uri zum gegebenen Prefix zurück. Wenn diese Methode nicht unterstützt wird, ist der Rückgabewert {@code null}.
	 * 
	 * @see Node#lookupNamespaceURI(String)
	 * @param prefix Prefix oder {@code null}.
	 * @return Uri oder {@code null}.
	 */
	public String lookupURI(final String prefix);

	/**
	 * Diese Methode gibt das Prefix zur gegebenen Uri zurück. Wenn diese Methode nicht unterstützt wird, ist der Rückgabewert {@code null}.
	 * 
	 * @see Node#lookupPrefix(String)
	 * @param uri Uri.
	 * @return Prefix oder {@code null}.
	 */
	public String lookupPrefix(final String uri);

}