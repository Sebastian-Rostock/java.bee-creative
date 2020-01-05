package bee.creative.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseItemBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator für einen {@link XPath}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseXPathData<GThis> extends BaseBuilder<XPath, GThis> {

	/** Diese Klasse implementiert den Konfigurator einer {@link XPathFactory}.
	 *
	 * @see XPathFactory#newXPath()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FactoryData<GOwner> extends BaseXPathFactoryData<FactoryData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFacroryData();

		@Override
		protected final FactoryData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link NamespaceContext}.
	 *
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ContextData<GOwner> extends BaseItemBuilder<NamespaceContext, ContextData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeContextData();

		@Override
		protected final ContextData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver}.
	 *
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class VariableData<GOwner> extends BaseItemBuilder<XPathVariableResolver, VariableData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeVariableData();

		@Override
		protected final VariableData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver}.
	 *
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FunctionData<GOwner> extends BaseItemBuilder<XPathFunctionResolver, FunctionData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFunctionData();

		@Override
		protected final FunctionData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert die {@link XPath}. */
	XPath xpath;

	/** Dieses Feld speichert den Konfigurator für {@link #openFacroryData()}. */
	final FactoryData<GThis> facroryData = new FactoryData<GThis>() {

		@Override
		public final GThis closeFacroryData() {
			return BaseXPathData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openContextData()}. */
	final ContextData<GThis> contextData = new ContextData<GThis>() {

		@Override
		public final GThis closeContextData() {
			return BaseXPathData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openVariableData()}. */
	final VariableData<GThis> variableData = new VariableData<GThis>() {

		@Override
		public final GThis closeVariableData() {
			return BaseXPathData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openFunctionData()}. */
	final FunctionData<GThis> functionData = new FunctionData<GThis>() {

		@Override
		public final GThis closeFunctionData() {
			return BaseXPathData.this.customThis();
		}

	};

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseXPathData<?> data) {
		if (data == null) return this.customThis();
		this.xpath = data.xpath;
		this.facroryData.use(data.facroryData);
		this.contextData.use(data.contextData);
		this.variableData.use(data.variableData);
		this.functionData.use(data.functionData);
		return this.customThis();
	}

	/** Diese Methode gibt das {@link XPath} zurück. Wenn über {@link #useXPath(XPath)} noch keine {@link XPath} gesetzt wurde, wird über
	 * {@link XPathFactory#newXPath()} eine neue erstellt, über {@link #useXPath(XPath)} gesetzt und über {@link #updateXPath()} aktualisiert. Die zur Erstellung
	 * verwendete {@link XPathFactory} kann über {@link #openFacroryData()} konfiguriert werden.
	 *
	 * @see #useXPath(XPath)
	 * @see #updateXPath()
	 * @return {@link XPath}.
	 * @throws XPathFactoryConfigurationException Wenn {@link FactoryData#getFactory()} bzw. {@link XPathFactory#newXPath()} eine entsprechende Ausnahme
	 *         auslöst. */
	public final XPath getXPath() throws XPathFactoryConfigurationException {
		XPath result = this.xpath;
		if (result != null) return result;
		result = this.facroryData.getFactory().newXPath();
		this.useXPath(result);
		this.updateXPath();
		return result;
	}

	/** Diese Methode setzt die {@link XPath} und gibt {@code this} zurück.
	 *
	 * @param factory {@link XPath} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useXPath(final XPath factory) {
		this.xpath = factory;
		return this.customThis();
	}

	/** Diese Methode setzt den {@link XPath} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useXPath(XPath)
	 * @return {@code this}. */
	public final GThis resetXPath() {
		return this.useXPath(null);
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link XPath} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #getXPath()} ermittelten {@link XPath} die Einstellungen übertragen, die in {@link #openContextData()}, {@link #openVariableData()} und
	 * {@link #openFunctionData()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws XPathFactoryConfigurationException Wenn {@link #getXPath()} eine entsprechende Ausnahme auslöst. */
	public final GThis updateXPath() throws XPathFactoryConfigurationException {
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
		return this.customThis();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link XPathFactory} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public final FactoryData<GThis> openFacroryData() {
		return this.facroryData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link NamespaceContext} und gibt ihn zurück.
	 *
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @return Konfigurator. */
	public final ContextData<GThis> openContextData() {
		return this.contextData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 *
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator. */
	public final VariableData<GThis> openVariableData() {
		return this.variableData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 *
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator. */
	public final FunctionData<GThis> openFunctionData() {
		return this.functionData;
	}

	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getXPath() */
	@Override
	public final XPath get() throws IllegalStateException {
		try {
			return this.getXPath();
		} catch (final XPathFactoryConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.facroryData, this.contextData, this.variableData, this.functionData);
	}

}