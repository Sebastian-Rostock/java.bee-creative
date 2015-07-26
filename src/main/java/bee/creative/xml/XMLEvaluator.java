package bee.creative.xml;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.util.Objects;
import bee.creative.util.Unique.UniqueMap;

/**
 * Diese Klasse implementiert einen Konfigurator zum {@link #compile(String) Kompilieren} sowie {@link #evaluate(QName) Auswerten} von {@link XPathExpression}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLEvaluator {

	/**
	 * Diese Klasse implementiert den Konfigurator für einen {@link XPath}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class XPathData extends BaseXPathData<XPathData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public XMLEvaluator closeXPathData() {
			return XMLEvaluator.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XPathData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Puffer für die erzeugten {@link XPathExpression}.
	 * 
	 * @see XMLEvaluator#compile(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class CacheData extends UniqueMap<String, XPathExpression> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XPathExpression compile(final String input) {
			try {
				final XPath xxath = XMLEvaluator.this.xpathData.getXPath();
				return xxath.compile(input);
			} catch (final XPathExpressionException | RuntimeException | XPathFactoryConfigurationException cause) {
				throw new IllegalStateException(cause);
			}
		}

	}

	{}

	/**
	 * Dieses Feld speichert des Basisknoten.
	 */
	Node base;

	/**
	 * Dieses Feld speichert den aktuellen Ausdruck.
	 */
	XPathExpression expression;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openXpathData()}.
	 */
	final XPathData xpathData = new XPathData();

	/**
	 * Dieses Feld speichert den Cache für {@link #compile(String)}.
	 */
	final CacheData cacheData = new CacheData();

	{}

	/**
	 * Diese Methode gibt den Basisknoten zurück, auf den sich die Pfadangaben beziehen.
	 * 
	 * @see #useBase(Node)
	 * @return Basisknoten.
	 */
	public Node getBase() {
		return this.base;
	}

	/**
	 * Diese Methode setzt den Basisknoten und gibt {@code this} zurück.
	 * 
	 * @see #getBase()
	 * @param base Basisknoten, auf den sich die Pfadangaben beziehen.
	 * @return {@code this}.
	 */
	public XMLEvaluator useBase(final Node base) {
		this.base = base;
		return this;
	}

	/**
	 * Diese Methode gibt den aktuellen Ausdruck zurück.
	 * 
	 * @return Ausdruck oder {@code null}.
	 */
	public XPathExpression getExpression() {
		return this.expression;
	}

	/**
	 * Diese Methode setzt den {@link #getExpression() aktuellen Ausdruck} und gibt {@code this} zurück.
	 * 
	 * @see #compile(String)
	 * @see #useExpression(XPathExpression)
	 * @param expression Ausdruck oder {@code null}.
	 * @return {@code this}.
	 * @throws IllegalStateException
	 */
	public XMLEvaluator useExpression(final String expression) throws IllegalStateException {
		return this.useExpression(this.compile(expression));
	}

	/**
	 * Diese Methode setzt den {@link #getExpression() aktuellen Ausdruck} und gibt {@code this} zurück.
	 * 
	 * @param expression Ausdruck oder {@code null}.
	 * @return {@code this}.
	 */
	public XMLEvaluator useExpression(final XPathExpression expression) {
		this.expression = expression;
		return this;
	}

	public XMLEvaluator resetCache() {
		this.cacheData.entryMap().clear();
		return this;
	}

	/**
	 * Diese Methode setzt den {@link #getExpression() aktuellen Ausdruck} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useExpression(XPathExpression)
	 * @return {@code this}.
	 */
	public XMLEvaluator resetExpression() {
		return this.useExpression((XPathExpression)null);
	}

	public XPathExpression compile(final String expression) throws IllegalStateException {
		if (expression == null) return null;
		final XPathExpression result = this.cacheData.get(expression);
		return result;
	}

	public Object evaluate(final QName resultType) throws XPathExpressionException {
		final Object result = this.expression.evaluate(this.base, resultType);
		return result;
	}

	public Node evaluateNode() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.NODE);
		return (Node)result;
	}

	public String evaluateString() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.STRING);
		return (String)result;
	}

	public Boolean evaluateBoolean() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.BOOLEAN);
		return (Boolean)result;
	}

	public Number evaluateNumber() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.NUMBER);
		return (Number)result;
	}

	public NodeList evaluateNodeList() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.NODESET);
		return (NodeList)result;
	}

	public XPathData openXpathData() {
		return xpathData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.base, this.expression, this.xpathData, this.cacheData);
	}

}
