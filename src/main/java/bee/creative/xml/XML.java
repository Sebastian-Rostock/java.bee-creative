package bee.creative.xml;

import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import bee.creative.util.Builder;
import bee.creative.util.Builders;
import bee.creative.util.Converter;
import bee.creative.util.Converters;
import bee.creative.util.Converters.CachedConverter;
import bee.creative.util.Objects;
import bee.creative.util.Pointer;
import bee.creative.util.Pointers;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden zur zur Erzeugung und Formatierung von {@link Document DOM-Dokumenten}, zur Verarbeitung von {@link XPath
 * XPath-Auswertungsumgebungen}, {@link Node DOM-Knoten}, {@link Templates XSL-Templates} und {@link Transformer XSL-Transformern}.
 * 
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class XML {

	public static XMLParser newParser() {
		return new XMLParser();
	}

	public static XMLResult newResult() {
		return new XMLResult();
	}

	public static XMLSource newSource() {
		return new XMLSource();
	}

	public static XMLSchema newSchema() {
		return new XMLSchema();
	}

	public static XMLEvaluator newEvaluator() {
		return new XMLEvaluator();
	}

	public static XMLFormatter newFormatter() {
		return new XMLFormatter();
	}

	{}

	{}

	{}

	{}
	{}
	{}
	{}
	{}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des gegebenen {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten}
	 * {@code null} ist, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see Node#getTextContent()
	 * @param node {@link Node DOM-Knoten}.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert}.
	 */
	public static String value(final Node node, final String defaultValue) {
		return ((node == null) ? defaultValue : node.getTextContent());
	}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des über seinen Namen gegebenen {@link Attr DOM-Attributs} des gegebenen {@link Node
	 * DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist oder kein {@link Attr DOM-Attribut} mit dem gegebenen Namen gefunden werden
	 * kann, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see XML#value(Node, String)
	 * @see Element#getAttributeNode(String)
	 * @param node {@link Node DOM-Knoten}.
	 * @param name Attribut-Name.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert} oder Standardwert.
	 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
	 */
	public static String value(final Node node, final String name, final String defaultValue) throws NullPointerException {
		if (name == null) throw new NullPointerException("name is null");
		if ((node == null) || (node.getNodeType() != Node.ELEMENT_NODE)) return defaultValue;
		return XML.value(((Element)node).getAttributeNode(name), defaultValue);
	}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des über seinen Namen und Namensraum gegebenen {@link Attr DOM-Attributs} des gegebenen
	 * {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist oder kein {@link Attr DOM-Attribut} mit dem gegebenen Namen und
	 * Namensraum gefunden werden kann, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see XML#value(Node, String)
	 * @see Element#getAttributeNodeNS(String, String)
	 * @param node {@link Node DOM-Knoten}.
	 * @param localName Attribut-Name.
	 * @param namespaceURI Namensraum.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert} oder Standardwert.
	 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
	 */
	public static String value(final Node node, final String namespaceURI, final String localName, final String defaultValue) throws NullPointerException {
		if (namespaceURI == null) throw new NullPointerException("namespaceURI is null");
		if (localName == null) throw new NullPointerException("localName is null");
		if ((node == null) || (node.getNodeType() != Node.ELEMENT_NODE)) return defaultValue;
		return XML.value(((Element)node).getAttributeNodeNS(namespaceURI, localName), defaultValue);
	}

	{}

}
