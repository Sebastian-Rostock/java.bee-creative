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
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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

	{}

	/**
	 * Diese Klasse implementiert den Konfigurator für {@link Document}-, {@link Attr}-, {@link Text}-, {@link Comment}- und {@link Element}-Knoten.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class XMLNode {

		// TODO
		/**
		 * Dieses Feld speichert den aktuelle Knoten.
		 */
		final Node node;

		/**
		 * Dieses Feld speichert den aufrufenden {@link XMLNode}.
		 */
		final XMLNode owner;

		/**
		 * Dieses Feld speichert die aktuelle Position.
		 */
		int index;

		/**
		 * Dieser Konstruktor initialisiert den aktuellen Knoten und den aufrufenden {@link XMLNode}.
		 * 
		 * @param node aktuellen Knoten.
		 * @param owner aufrufender {@link XMLNode}
		 */
		private XMLNode(final Node node, final XMLNode owner) {
			this.node = node;
			this.owner = owner;
		}

		/**
		 * Dieser Konstruktor initialisiert den {@link Node}, der über diesen {@link XMLNode} befüllt werden soll.
		 * 
		 * @param node {@link Node}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public XMLNode(final Node node) throws NullPointerException {
			if (node == null) throw new NullPointerException();
			this.node = node;
			this.owner = null;
		}

		/**
		 * Diese Methode gibt den aktuellen Knoten zurück, in dennen Kontext Kind- und Attributknoten erzeugt werden. Dieser kann nur ein {@link Element} oder ein
		 * {@link Document} sein.
		 * 
		 * @see Element
		 * @see Document
		 * @return aktueller Knoten.
		 */
		public Node node() {
			return this.node;
		}

		/**
		 * Diese Methode gibt das {@link Document} des {@link #node() aktuellen Knoten} zurück.
		 * 
		 * @see Node#getOwnerDocument()
		 * @return {@link Document} des {@link #node() aktuellen Knoten}.
		 */
		public Document document() {
			final Node node = this.node.getOwnerDocument();
			if (node.getNodeType() == Node.DOCUMENT_NODE) return (Document)node;
			return node.getOwnerDocument();
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code index() - 1} aus und gibt {@code this} das zurück.
		 * 
		 * @see #hasPrev()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn es keine vorherige Position gibt.
		 */
		public XMLNode prev() throws IllegalStateException {
			if (!this.hasPrev()) throw new IllegalStateException();
			this.index--;
			return this;
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code index() + 1} aus und gibt {@code this} das zurück.
		 * 
		 * @see #hasNext()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn es keine nächste Position gibt.
		 */
		public XMLNode next() throws IllegalStateException {
			if (!this.hasNext()) throw new IllegalStateException();
			this.index++;
			return this;
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position}, an welcher neue Kindelemente eingefügt werden. Negative Positionen zählen vom Ende der
		 * Kindknotenliste, d.h. {@code -1 => length()}, {@code -2 => (length() - 1)} usw.
		 * 
		 * @see #index()
		 * @see #length()
		 * @param index Positioh.
		 * @return {@code this}.
		 * @throws IllegalArgumentException Wenn {@code index > length()} oder {@code (-index - 1) > length()}.
		 */
		public XMLNode seek(final int index) throws IllegalArgumentException {
			final int length = this.length(), offset = index >= 0 ? index : length + index + 1;
			if ((offset < 0) || (offset > length)) throw new IllegalArgumentException();
			this.index = offset;
			return this;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} größer als {@code 0} ist.
		 * 
		 * @see #index()
		 * @see #length()
		 * @return {@code true}, wenn {@code index() > 0}.
		 */
		public boolean hasPrev() {
			return this.index > 0;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} kleiner als {@link #length()} ist.
		 * 
		 * @see #index()
		 * @see #length()
		 * @return {@code true}, wenn {@code index() < length()}.
		 */
		public boolean hasNext() {
			return this.index < this.length();
		}

		/**
		 * Diese Methode gibt die aktuelle Position zurück, an welcher neue Kindelemente eingefügt werden. für eine gültige Position gilt
		 * {@code 0 <= index() <= length()}.
		 * 
		 * @see #seek(int)
		 * @see #length()
		 * @return aktuelle Position.
		 */
		public int index() {
			return this.index;
		}

		/**
		 * Diese Methode gibt die Anzahl der Kondknoten zurück.
		 * 
		 * @see Node#getChildNodes()
		 * @see NodeList#getLength()
		 * @return Anzahl der Kondknoten.
		 */
		public int length() {
			return this.node().getChildNodes().getLength();
		}

		/**
		 * Diese Methode gibt einen neuen {@link XMLNode} zu dem {@link Element} zurück, das sich in der Kondknotenliste an der {@link #index() aktuellen Position}
		 * befindet.
		 * 
		 * @see #index()
		 * @return {@link XMLNode} zum {@link Element} an der {@link #index() aktuellen Position}.
		 * @throws IllegalStateException Wenn die Kondknotenliste an der {@link #index() aktuellen Position} kein {@link Element} enthält.
		 */
		public XMLNode open() throws IllegalStateException {
			final Node node = XMLNode.this.node().getChildNodes().item(XMLNode.this.index);
			if (!(node instanceof Element)) throw new IllegalStateException();
			return new XMLNode(node, this);
		}

		/**
		 * Diese Methode schließt die Bearbeitung des {@link #node() aktuelle Knoten} ab und gibt den {@link XMLNode} zurück, von dem aus dieser erzeugt wurde.
		 * 
		 * @see #open()
		 * @see #element(String)
		 * @see #element(String, String)
		 * @return erzeugenden {@link XMLNode}.
		 * @throws IllegalStateException Wenn es keinen erzeugenden {@link XMLNode} gibt.
		 */
		public XMLNode close() throws IllegalStateException {
			if (this.owner == null) throw new IllegalStateException();
			return this.owner;
		}

		/**
		 * Diese Methode fügt den gegebenen {@link Node} an der {@link #index() aktuellen Position} ein, bewegt die {@link #index() aktuelle Position} hinter den
		 * eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #index()
		 * @see Node#insertBefore(Node, Node)
		 * @param node {@link Node}.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public XMLNode insert(final Node node) throws DOMException {
			final Node root = this.node(), next = root.getChildNodes().item(this.index);
			root.insertBefore(node, next);
			this.index++;
			return this;
		}

		/**
		 * Diese Methode entfernt den Kindknoten an der {@link #index() aktuellen Position} und gibt {@code this} zurück.
		 * 
		 * @see #index()
		 * @see Node#removeChild(Node)
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn die Kondknotenliste an der {@link #index() aktuellen Position} kein Knoten existiert.
		 */
		public XMLNode delete() throws DOMException, IllegalStateException {
			final Node root = this.node(), next = root.getChildNodes().item(this.index);
			if (next == null) throw new IllegalStateException();
			root.removeChild(next);
			return this;
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Text} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #insert(Node)
		 * @see Document#createTextNode(String)
		 * @param text Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public XMLNode text(final String text) throws DOMException {
			return this.insert(this.document().createTextNode(text));
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Comment} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #insert(Node)
		 * @see Document#createComment(String)
		 * @param text Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public XMLNode comment(final String text) throws DOMException {
			return this.insert(this.document().createComment(text));
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit dem gegebenen Namen ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt einen neuen {@link XMLNode} zum eingefügten {@link Element} zurück.
		 * 
		 * @see Document#createElement(String)
		 * @param name Name.
		 * @return {@link XMLNode} zum erzeugten Knoten.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public XMLNode element(final String name) throws DOMException {
			return this.insert(this.document().createElement(name)).prev().open();
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit den gegebenen Eigenschaften ein, bewegt die {@link #index()
		 * aktuelle Position} hinter den eingefügten Knoten und gibt einen neuen {@link XMLNode} zum eingefügten {@link Element} zurück.
		 * 
		 * @see Document#createElementNS(String, String)
		 * @param uri Uri.
		 * @param name Name.
		 * @return {@link XMLNode} zum erzeugten Knoten.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public XMLNode element(final String uri, final String name) throws DOMException {
			return this.insert(this.document().createElementNS(uri, name)).prev().open();
		}

		/**
		 * Diese Methode modifiziert das {@link Attr} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Wenn der gegebene Wert {@code null} ist, wird
		 * das {@link Attr} entfernt. Andernfalls wird es erzeugt bzw. geändert.
		 * 
		 * @see Element#setAttribute(String, String)
		 * @see Element#removeAttribute(String)
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn das {@link #node() aktuelle Knoten} kein {@link Element} ist.
		 */
		public XMLNode attribute(final String name, final String value) throws DOMException, IllegalStateException {
			if (this.owner == null) throw new IllegalStateException();
			if (value == null) {
				((Element)this.node).removeAttribute(name);
			} else {
				((Element)this.node).setAttribute(name, value);
			}
			return this;
		}

		/**
		 * Diese Methode modifiziert das {@link Attr} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Wenn der gegebene Wert {@code null} ist, wird
		 * das {@link Attr} entfernt. Andernfalls wird es erzeugt bzw. geändert.
		 * 
		 * @see Element#setAttributeNS(String, String, String)
		 * @see Element#removeAttributeNS(String, String)
		 * @param uri Uri.
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn das {@link #node() aktuelle Knoten} kein {@link Element} ist.
		 */
		public XMLNode attribute(final String uri, final String name, final String value) throws DOMException, IllegalStateException {
			if (this.owner == null) throw new IllegalStateException();
			if (value == null) {
				((Element)this.node).removeAttributeNS(uri, name);
			} else {
				((Element)this.node).setAttributeNS(uri, name, value);
			}
			return this;
		}

	}

	{}

	public static XMLParser newParser() {
		return new XMLParser();
	}

	public static XMLSource newSource() {
		return new XMLSource();
	}

	public static XMLInputSource newInputSource() {
		return new XMLInputSource();
	}

	public static XMLSchema newSchema() {
		return new XMLSchema();
	}

	public static XMLSchemaFactory newSchemaFactory() {
		return new XMLSchemaFactory();
	}

	{}

	{}
	{}
	{}
	{}
	{}

	/**
	 * Diese Klasse implementiert eine gepufferte {@link XPath XPath-Auswertungsumgebung}, die die von einer gegebenen {@link XPath XPath-Auswertungsumgebung}
	 * erzeugten {@link XPathExpression XPath-Ausdrücke} in einer {@link Map Abbildung} von Schlüsseln auf Werte verwaltet. Die Schlüssel werden dabei über
	 * {@link Pointer} auf Zeichenketten und die Werte als {@link Pointer} auf die {@link XPathExpression XPath-Ausdrücke} des gegebenen {@link XPath
	 * XPath-Auswertungsumgebung} realisiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Converters#cachedConverter(int, int, int, Converter)
	 */
	public static final class CachedXPath implements XPath, Converter<String, XPathExpression> {

		/**
		 * Dieses Feld speichert die {@link XPath XPath-Auswertungsumgebung}.
		 */
		final XPath xpath;

		/**
		 * Dieses Feld speichert den {@link CachedConverter Cached-Converter}.
		 */
		final CachedConverter<String, XPathExpression> converter;

		/**
		 * Dieser Konstruktor initialisiert die {@link XPath XPath-Auswertungsumgebung}.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
		 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der Abbildung erzeugt werden.
		 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der Abbildung erzeugt werden.
		 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
		 */
		public CachedXPath(final int limit, final int inputMode, final int outputMode, final XPath xpath) {
			this.xpath = xpath;
			this.converter = new CachedConverter<String, XPathExpression>(limit, inputMode, outputMode, this);
		}

		/**
		 * Diese Methode leert die Abbildung.
		 * 
		 * @see CachedConverter#clear()
		 */
		public void clear() {
			this.converter.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void reset() {
			this.converter.clear();
			this.xpath.reset();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathExpression convert(final String input) {
			try {
				return this.xpath.compile(input);
			} catch (final XPathExpressionException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathExpression compile(final String expression) throws XPathExpressionException {
			try {
				return this.converter.convert(expression);
			} catch (final RuntimeException e) {
				final Throwable cause = e.getCause();
				if (cause instanceof XPathExpressionException) throw (XPathExpressionException)cause;
				throw e;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object evaluate(final String expression, final Object item, final QName returnType) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			if (returnType == null) throw new NullPointerException("returnType is null");
			return this.compile(expression).evaluate(item, returnType);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String evaluate(final String expression, final Object item) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			return this.compile(expression).evaluate(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object evaluate(final String expression, final InputSource source, final QName returnType) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			if (source == null) throw new NullPointerException("source is null");
			if (returnType == null) throw new NullPointerException("returnType is null");
			return this.compile(expression).evaluate(source, returnType);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String evaluate(final String expression, final InputSource source) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			if (source == null) throw new NullPointerException("source is null");
			return this.compile(expression).evaluate(source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathVariableResolver getXPathVariableResolver() {
			return this.xpath.getXPathVariableResolver();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXPathVariableResolver(final XPathVariableResolver resolver) {
			this.converter.clear();
			this.xpath.setXPathVariableResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathFunctionResolver getXPathFunctionResolver() {
			return this.xpath.getXPathFunctionResolver();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {
			this.converter.clear();
			this.xpath.setXPathFunctionResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NamespaceContext getNamespaceContext() {
			return this.xpath.getNamespaceContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setNamespaceContext(final NamespaceContext nsContext) {
			this.converter.clear();
			this.xpath.setNamespaceContext(nsContext);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.xpath);
		}

	}

	
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

	/**
	 * Diese Methode erzeugt einen {@link XMLNode} für das gegebene {@link Document} und gibt ihn zurück.
	 * 
	 * @param document {@link Document}.
	 * @return {@link XMLNode}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static XMLNode builder(final Document document) throws NullPointerException {
		return new XMLNode(document);
	}

	{}

}
