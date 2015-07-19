package bee.creative.xml;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import bee.creative.util.Unique.UniqueMap;

public class XMLEvaluator {

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
			final XPath xxath = XMLEvaluator.this.xpathData.build();
			try {
				return xxath.compile(input);
			} catch (final XPathExpressionException cause) {
				throw new IllegalStateException(cause);
			}
		}

	}

	{}

	Node base;

	XPathExpression expression;

	final XPathData xpathData = new XPathData();

	final CacheData cacheData = new CacheData();

	public Node getBase() {
		return this.base;
	}

	public XMLEvaluator useBase(final Node base) {
		this.base = base;
		return this;
	}

	public XPathExpression getExpression() {
		return this.expression;
	}

	public XMLEvaluator useExpression(final String expression) {
		return this.useExpression(this.compile(expression));
	}

	public XMLEvaluator useExpression(final XPathExpression expression) {
		this.expression = expression;
		return this;
	}

	public XMLEvaluator resetCache() {
		cacheData.entryMap().clear();
		return this;
	}

	public XMLEvaluator resetExpression() {
		return this.useExpression((XPathExpression)null);
	}

	public XPathExpression compile(final String expression) throws IllegalStateException {
		final XPathExpression result = this.cacheData.get(expression);
		return result;
	}

	public Object evaluate(final QName resultType) throws XPathExpressionException {
		final Node b = this.getBase();
		final XPathExpression ex = this.getExpression();
		final Object res = ex.evaluate(b, resultType);
		return res;
	}

	public Node evaluateNode() throws XPathExpressionException {
		final Object res = this.evaluate(XPathConstants.NODE);
		return (Node)res;
	}

	public String evaluateString() throws XPathExpressionException {
		final Object res = this.evaluate(XPathConstants.STRING);
		return (String)res;
	}

	public Boolean evaluateBoolean() throws XPathExpressionException {
		final Object res = this.evaluate(XPathConstants.BOOLEAN);
		return (Boolean)res;
	}

	public Number evaluateNumber() throws XPathExpressionException {
		final Object res = this.evaluate(XPathConstants.NUMBER);
		return (Number)res;
	}

	public NodeList evaluateNodeList() throws XPathExpressionException {
		final Object res = this.evaluate(XPathConstants.NODESET);
		return (NodeList)res;
	}

}
