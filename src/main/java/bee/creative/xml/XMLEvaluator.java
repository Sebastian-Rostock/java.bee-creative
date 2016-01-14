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
		public final XMLEvaluator closeXPathData() {
			return XMLEvaluator.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final XPathData __this() {
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
		protected final XPathExpression compile(final String input) {
			try {
				final XPath xxath = XMLEvaluator.this.__xpathData.getXPath();
				return xxath.compile(input);
			} catch (final XPathExpressionException | XPathFactoryConfigurationException cause) {
				throw new IllegalStateException(cause);
			}
		}

	}

	{}

	/**
	 * Dieses Feld speichert des Basisknoten.
	 */
	Node __base;

	/**
	 * Dieses Feld speichert den aktuellen Ausdruck.
	 */
	XPathExpression __expression;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openXpathData()}.
	 */
	final XPathData __xpathData = new XPathData();

	/**
	 * Dieses Feld speichert den Cache für {@link #compile(String)}.
	 */
	final CacheData __cacheData = new CacheData();

	{}

	/**
	 * Diese Methode gibt den Basisknoten zurück, auf den sich die Pfadangaben beziehen.
	 * 
	 * @see #useBase(Node)
	 * @return Basisknoten.
	 */
	public final Node getBase() {
		return this.__base;
	}

	/**
	 * Diese Methode setzt den Basisknoten und gibt {@code this} zurück.
	 * 
	 * @see #getBase()
	 * @param base Basisknoten, auf den sich die Pfadangaben beziehen.
	 * @return {@code this}.
	 */
	public final XMLEvaluator useBase(final Node base) {
		this.__base = base;
		return this;
	}

	/**
	 * Diese Methode gibt den Puffer zurück, in dem die {@link #compile(String) kompilierten} Ausdrücke zur Wiederverwendung vorgehalten werden.
	 * 
	 * @return Puffer der Ausdrücke.
	 */
	public final CacheData getCache() {
		return this.__cacheData;
	}

	/**
	 * Diese Methode gibt den aktuellen Ausdruck zurück.
	 * 
	 * @return Ausdruck oder {@code null}.
	 */
	public final XPathExpression getExpression() {
		return this.__expression;
	}

	/**
	 * Diese Methode setzt den {@link #getExpression() aktuellen Ausdruck} und gibt {@code this} zurück.
	 * 
	 * @see #compile(String)
	 * @see #useExpression(XPathExpression)
	 * @param expression Ausdruck oder {@code null}.
	 * @return {@code this}.
	 * @throws XPathExpressionException Wenn {@link #compile(String)} eine entsprechende Ausnahme auslöst.
	 * @throws XPathFactoryConfigurationException Wenn {@link #compile(String)} eine entsprechende Ausnahme auslöst.
	 */
	public final XMLEvaluator useExpression(final String expression) throws XPathExpressionException, XPathFactoryConfigurationException {
		return this.useExpression(this.compile(expression));
	}

	/**
	 * Diese Methode setzt den {@link #getExpression() aktuellen Ausdruck} und gibt {@code this} zurück.
	 * 
	 * @param expression Ausdruck oder {@code null}.
	 * @return {@code this}.
	 */
	public final XMLEvaluator useExpression(final XPathExpression expression) {
		this.__expression = expression;
		return this;
	}

	/**
	 * Diese Methode leert den Puffer der {@link #compile(String) kompilierten} Ausdrücke und gibt {@code this} zurück.
	 * 
	 * @return {@code this}.
	 */
	public final XMLEvaluator resetCache() {
		this.__cacheData.entryMap().clear();
		return this;
	}

	/**
	 * Diese Methode setzt den {@link #getExpression() aktuellen Ausdruck} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useExpression(XPathExpression)
	 * @return {@code this}.
	 */
	public final XMLEvaluator resetExpression() {
		return this.useExpression((XPathExpression)null);
	}

	/**
	 * Diese Methode kompiliert den gegebenen Ausdruck und gibt ihn als {@link XPathExpression} zurück.<br>
	 * Wenn der Ausdruck {@code null} ist, wird {@code null} geliefert. Die kompilierten Ausdrücke werden {@link #getCache() gepuffert}.
	 * 
	 * @param expression Ausdruck oder {@code null}.
	 * @return {@link XPathExpression} oder {@code null}.
	 * @throws XPathExpressionException Wenn {@link XPath#compile(String)} eine entsprechende Ausnahme auslöst.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathData#getXPath()} eine entsprechende Ausnahme auslöst.
	 */
	public final XPathExpression compile(final String expression) throws XPathExpressionException, XPathFactoryConfigurationException {
		if (expression == null) return null;
		try {
			final XPathExpression result = this.__cacheData.get(expression);
			return result;
		} catch (final RuntimeException exception) {
			final Throwable cause = exception.getCause();
			if (cause instanceof XPathExpressionException) throw (XPathExpressionException)cause;
			if (cause instanceof XPathFactoryConfigurationException) throw (XPathFactoryConfigurationException)cause;
			throw exception;
		}
	}

	/**
	 * Diese Methode evaluiert den {@link #getExpression() aktuellen Ausdruck} in den gegebenen Ergebnistyp und gibt das Ergebnis zurück.<br>
	 * Wenn der Ausdruck {@code null} ist, wird {@code null} geliefert.
	 * 
	 * @see XPathConstants
	 * @param resultType Ergebnistyp.
	 * @return Ergebnis oser {@code null}.
	 * @throws XPathExpressionException Wenn {@link XPathExpression#evaluate(Object, QName)} eine entsprechende Ausnahme auslöst.
	 */
	public final Object evaluate(final QName resultType) throws XPathExpressionException {
		if (this.__expression == null) return null;
		final Object result = this.__expression.evaluate(this.__base, resultType);
		return result;
	}

	/**
	 * Diese Methode evaluiert den {@link #getExpression() aktuellen Ausdruck} in einen Knoten und diesen zurück.
	 * 
	 * @see #evaluate(QName)
	 * @see XPathConstants#NODE
	 * @return Knoten oder {@code null}.
	 * @throws XPathExpressionException Wenn {@link #evaluate(QName)} eine entsprechende Ausnahme auslöst.
	 */
	public final Node evaluateNode() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.NODE);
		return (Node)result;
	}

	/**
	 * Diese Methode evaluiert den {@link #getExpression() aktuellen Ausdruck} in eine Zeichenkette und diese zurück.
	 * 
	 * @see #evaluate(QName)
	 * @see XPathConstants#STRING
	 * @return Zeichenkette oder {@code null}.
	 * @throws XPathExpressionException Wenn {@link #evaluate(QName)} eine entsprechende Ausnahme auslöst.
	 */
	public final String evaluateString() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.STRING);
		return (String)result;
	}

	/**
	 * Diese Methode evaluiert den {@link #getExpression() aktuellen Ausdruck} in einen Wahrheitswert und diesen zurück.
	 * 
	 * @see #evaluate(QName)
	 * @see XPathConstants#BOOLEAN
	 * @return Wahrheitswert oder {@code null}.
	 * @throws XPathExpressionException Wenn {@link #evaluate(QName)} eine entsprechende Ausnahme auslöst.
	 */
	public final Boolean evaluateBoolean() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.BOOLEAN);
		return (Boolean)result;
	}

	/**
	 * Diese Methode evaluiert den {@link #getExpression() aktuellen Ausdruck} in einen Zahlenwert und diesen zurück.
	 * 
	 * @see #evaluate(QName)
	 * @see XPathConstants#NUMBER
	 * @return Zahlenwert oder {@code null}.
	 * @throws XPathExpressionException Wenn {@link #evaluate(QName)} eine entsprechende Ausnahme auslöst.
	 */
	public final Number evaluateNumber() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.NUMBER);
		return (Number)result;
	}

	/**
	 * Diese Methode evaluiert den {@link #getExpression() aktuellen Ausdruck} in eine Knotenliste und diesen zurück.
	 * 
	 * @see #evaluate(QName)
	 * @see XPathConstants#NODESET
	 * @return Knotenliste oder {@code null}.
	 * @throws XPathExpressionException Wenn {@link #evaluate(QName)} eine entsprechende Ausnahme auslöst.
	 */
	public final NodeList evaluateNodeList() throws XPathExpressionException {
		final Object result = this.evaluate(XPathConstants.NODESET);
		return (NodeList)result;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link XPath} und gibt ihn zurück.
	 * 
	 * @return Konfigurator.
	 */
	public final XPathData openXpathData() {
		return this.__xpathData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__base, this.__expression, this.__xpathData, this.__cacheData);
	}

}
