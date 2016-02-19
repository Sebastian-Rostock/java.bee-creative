package bee.creative.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

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

		{}

		/** {@inheritDoc} */
		@Override
		protected final FactoryData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link NamespaceContext}.
	 * 
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ContextData<GOwner> extends BaseValueBuilder<NamespaceContext, ContextData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeContextData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final ContextData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver}.
	 * 
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class VariableData<GOwner> extends BaseValueBuilder<XPathVariableResolver, VariableData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeVariableData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final VariableData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver}.
	 * 
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FunctionData<GOwner> extends BaseValueBuilder<XPathFunctionResolver, FunctionData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeFunctionData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final FunctionData<GOwner> _this_() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert die {@link XPath}. */
	XPath _xpath_;

	/** Dieses Feld speichert den Konfigurator für {@link #openFacroryData()}. */
	final FactoryData<GThis> _facroryData_ = new FactoryData<GThis>() {

		@Override
		public final GThis closeFacroryData() {
			return BaseXPathData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openContextData()}. */
	final ContextData<GThis> _contextData_ = new ContextData<GThis>() {

		@Override
		public final GThis closeContextData() {
			return BaseXPathData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openVariableData()}. */
	final VariableData<GThis> _variableData_ = new VariableData<GThis>() {

		@Override
		public final GThis closeVariableData() {
			return BaseXPathData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openFunctionData()}. */
	final FunctionData<GThis> _functionData_ = new FunctionData<GThis>() {

		@Override
		public final GThis closeFunctionData() {
			return BaseXPathData.this._this_();
		}

	};

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseXPathData<?> data) {
		if (data == null) return this._this_();
		this._xpath_ = data._xpath_;
		this._facroryData_.use(data._facroryData_);
		this._contextData_.use(data._contextData_);
		this._variableData_.use(data._variableData_);
		this._functionData_.use(data._functionData_);
		return this._this_();
	}

	/** Diese Methode gibt das {@link XPath} zurück.<br>
	 * Wenn über {@link #useXPath(XPath)} noch keine {@link XPath} gesetzt wurde, wird über {@link XPathFactory#newXPath()} eine neue erstellt, über
	 * {@link #useXPath(XPath)} gesetzt und über {@link #updateXPath()} aktualisiert. Die zur Erstellung verwendete {@link XPathFactory} kann über
	 * {@link #openFacroryData()} konfiguriert werden.
	 * 
	 * @see #useXPath(XPath)
	 * @see #updateXPath()
	 * @return {@link XPath}.
	 * @throws XPathFactoryConfigurationException Wenn {@link FactoryData#getFactory()} bzw. {@link XPathFactory#newXPath()} eine entsprechende Ausnahme auslöst. */
	public final XPath getXPath() throws XPathFactoryConfigurationException {
		XPath result = this._xpath_;
		if (result != null) return result;
		result = this._facroryData_.getFactory().newXPath();
		this.useXPath(result);
		this.updateXPath();
		return result;
	}

	/** Diese Methode setzt die {@link XPath} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link XPath} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useXPath(final XPath factory) {
		this._xpath_ = factory;
		return this._this_();
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
		for (final NamespaceContext value: this._contextData_) {
			factory.setNamespaceContext(value);
		}
		for (final XPathVariableResolver value: this._variableData_) {
			factory.setXPathVariableResolver(value);
		}
		for (final XPathFunctionResolver value: this._functionData_) {
			factory.setXPathFunctionResolver(value);
		}
		return this._this_();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link XPathFactory} und gibt ihn zurück.
	 * 
	 * @return Konfigurator. */
	public final FactoryData<GThis> openFacroryData() {
		return this._facroryData_;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link NamespaceContext} und gibt ihn zurück.
	 * 
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @return Konfigurator. */
	public final ContextData<GThis> openContextData() {
		return this._contextData_;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 * 
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator. */
	public final VariableData<GThis> openVariableData() {
		return this._variableData_;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 * 
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator. */
	public final FunctionData<GThis> openFunctionData() {
		return this._functionData_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis _this_();

	/** {@inheritDoc}
	 * 
	 * @see #getXPath() */
	@Override
	public final XPath build() throws IllegalStateException {
		try {
			return this.getXPath();
		} catch (final XPathFactoryConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._facroryData_, this._contextData_, this._variableData_, this._functionData_);
	}

}