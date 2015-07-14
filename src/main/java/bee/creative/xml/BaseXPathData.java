package bee.creative.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link XPath}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseXPathData<GThiz> extends BaseBuilder<XPath, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Sprache einer {@link XPath}.
	 * 
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class ContextData extends BaseValueBuilder<NamespaceContext, ContextData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeLanguageData() {
			return BaseXPathData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ContextData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver} einer {@link XPath}.
	 * 
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class VariableData extends BaseValueBuilder<XPathVariableResolver, VariableData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeListenerData() {
			return BaseXPathData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected VariableData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver} einer {@link XPath}.
	 * 
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class FunctionData extends BaseValueBuilder<XPathFunctionResolver, FunctionData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeResolverData() {
			return BaseXPathData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FunctionData thiz() {
			return this;
		}

	}

	public final class FacroryData extends BaseXPathFactoryData<FacroryData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeFacroryData() {
			return BaseXPathData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FacroryData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link XPath}.
	 */
	XPath xpath;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFacroryData()}.
	 */
	final FacroryData facroryData = //
		new FacroryData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openContextData()}.
	 */
	final ContextData contextData = //
		new ContextData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openVariableData()}.
	 */
	final VariableData variableData = //
		new VariableData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFunctionData()}.
	 */
	final FunctionData functionData = //
		new FunctionData();

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseXPathData<?> data) {
		if (data == null) return this.thiz();
		this.xpath = data.xpath;
		this.facroryData.use(data.facroryData);
		this.contextData.use(data.contextData);
		this.variableData.use(data.variableData);
		this.functionData.use(data.functionData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt das {@link XPath} zurück.<br>
	 * Wenn über {@link #useXPath(XPath)} noch keine {@link XPath} gesetzt wurde, wird über {@link XPath#newInstance(String)} eine neue erstellt, über
	 * {@link #useXPath(XPath)} gesetzt und über {@link #updateXPath()} aktualisiert. Das zur Erstellung verwendete Objektmodell kann über
	 * {@link #openContextData()} konfiguriert werden.
	 * 
	 * @see #useXPath(XPath)
	 * @return {@link XPath}.
	 */
	public XPath getXPath() {
		XPath result = this.xpath;
		if (result != null) return result;
		final XPathFactory x = this.facroryData.build();
		result = x.newXPath();
		this.useXPath(result);
		this.updateXPath();
		return result;
	}

	/**
	 * Diese Methode setzt die {@link XPath} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link XPath} oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useXPath(final XPath factory) {
		this.xpath = factory;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt den {@link XPath} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useXPath(XPath)
	 * @return {@code this}.
	 */
	public GThiz resetXPath() {
		return this.useXPath(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen der {@link XPath} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getXPath()} ermittelte {@link XPath} die Einstellungen übertragen, die in {@link #openVariableData()}
	 * , {@link #openFunctionData()} und {@link #openFeatureData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws XPathConfigurationException Wenn {@link XPath#setFeature(String, boolean)} bzw. {@link #getXPath()} eine entsprechende Ausnahme auslöst.
	 */
	public GThiz updateXPath() {
		final XPath factory = this.getXPath();
		for (final NamespaceContext value: this.contextData) {
			factory.setNamespaceContext(value);
		}
		for (final XPathVariableResolver value: this.variableData) {
			factory.setXPathVariableResolver(value);
		}
		for (final XPathFunctionResolver value: this.functionData) {
			factory.setXPathFunctionResolver(value);
		}
		return this.thiz();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link XPathFactory} und gibt ihn zurück.
	 * 
	 * @return Konfigurator.
	 */
	public FacroryData openFacroryData() {
		return this.facroryData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link NamespaceContext} und gibt ihn zurück.
	 * 
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @return Konfigurator.
	 */
	public ContextData openContextData() {
		return this.contextData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 * 
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator.
	 */
	public VariableData openVariableData() {
		return this.variableData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 * 
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator.
	 */
	public FunctionData openFunctionData() {
		return this.functionData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz thiz();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public XPath build() throws IllegalStateException {
		return this.getXPath();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.contextData, this.variableData, this.functionData, this.facroryData);
	}

}