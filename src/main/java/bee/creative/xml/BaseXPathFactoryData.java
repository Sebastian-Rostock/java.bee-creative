package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link XPathFactory}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseXPathFactoryData<GThiz> extends BaseBuilder<XPathFactory, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für das Objektmodel einer {@link XPathFactory}.<br>
	 * Initialisiert wird dieses via {@link #useDEFAULT_OBJECT_MODEL_URI()}.
	 * 
	 * @see XPathFactory#newInstance(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ModelData<GOwner> extends BaseValueBuilder<String, ModelData<GOwner>> {

		/**
		 * Dieser Konstruktor initialisiert den Wert via {@link #useDEFAULT_OBJECT_MODEL_URI()}.
		 */
		public ModelData() {
			this.useDEFAULT_OBJECT_MODEL_URI();
		}

		{}

		/**
		 * Diese Methode setzt den Wert auf {@link XPathFactory#DEFAULT_OBJECT_MODEL_URI} und gibt {@code this} zurück.
		 * 
		 * @see #use(BaseValueBuilder)
		 * @return {@code this}.
		 */
		public final ModelData<GOwner> useDEFAULT_OBJECT_MODEL_URI() {
			return super.use(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
		}

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeLanguageData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final ModelData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link XPathFactory}.
	 * 
	 * @see XPathFactory#setFeature(String, boolean)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class FeatureData<GOwner> extends BaseFeatureData<FeatureData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeFeatureData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final FeatureData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver}.
	 * 
	 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class VariableData<GOwner> extends BaseValueBuilder<XPathVariableResolver, VariableData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeListenerData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final VariableData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver}.
	 * 
	 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class FunctionData<GOwner> extends BaseValueBuilder<XPathFunctionResolver, FunctionData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeResolverData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final FunctionData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link XPathFactory}.
	 */
	XPathFactory __factory;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openModelData()}.
	 */
	final ModelData<GThiz> __modelData = new ModelData<GThiz>() {

		@Override
		public final GThiz closeLanguageData() {
			return BaseXPathFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
	 */
	final FeatureData<GThiz> __featureData = new FeatureData<GThiz>() {

		@Override
		public final GThiz closeFeatureData() {
			return BaseXPathFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openVariableData()}.
	 */
	final VariableData<GThiz> __variableData = new VariableData<GThiz>() {

		@Override
		public final GThiz closeListenerData() {
			return BaseXPathFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFunctionData()}.
	 */
	final FunctionData<GThiz> __functionData = new FunctionData<GThiz>() {

		@Override
		public final GThiz closeResolverData() {
			return BaseXPathFactoryData.this.thiz();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz use(final BaseXPathFactoryData<?> data) {
		if (data == null) return this.thiz();
		this.__factory = data.__factory;
		this.__modelData.use(data.__modelData);
		this.__featureData.use(data.__featureData);
		this.__variableData.use(data.__variableData);
		this.__functionData.use(data.__functionData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt das {@link XPathFactory} zurück.<br>
	 * Wenn über {@link #useFactory(XPathFactory)} noch keine {@link XPathFactory} gesetzt wurde, wird über {@link XPathFactory#newInstance(String)} eine neue
	 * erstellt, über {@link #useFactory(XPathFactory)} gesetzt und über {@link #updateFactory()} aktualisiert. Das zur Erstellung verwendete Objektmodell kann
	 * über {@link #openModelData()} konfiguriert werden.
	 * 
	 * @see #useFactory(XPathFactory)
	 * @return {@link XPathFactory}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactory#newInstance(String)} bzw. {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 */
	public final XPathFactory getFactory() throws XPathFactoryConfigurationException {
		XPathFactory result = this.__factory;
		if (result != null) return result;
		result = XPathFactory.newInstance(this.__modelData.build());
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/**
	 * Diese Methode setzt die {@link XPathFactory} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link XPathFactory} oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useFactory(final XPathFactory factory) {
		this.__factory = factory;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt die {@link XPathFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(XPathFactory)
	 * @return {@code this}.
	 */
	public final GThiz resetFactory() {
		return this.useFactory(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen der {@link XPathFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link XPathFactory} die Einstellungen übertragen, die in
	 * {@link #openVariableData()}, {@link #openFunctionData()} und {@link #openFeatureData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactory#setFeature(String, boolean)} bzw. {@link #getFactory()} eine entsprechende Ausnahme
	 *         auslöst.
	 */
	public final GThiz updateFactory() throws XPathFactoryConfigurationException {
		final XPathFactory factory = this.getFactory();
		for (final XPathVariableResolver value: this.__variableData) {
			factory.setXPathVariableResolver(value);
		}
		for (final XPathFunctionResolver value: this.__functionData) {
			factory.setXPathFunctionResolver(value);
		}
		for (final Entry<String, Boolean> entry: this.__featureData) {
			factory.setFeature(entry.getKey(), entry.getValue());
		}
		return this.thiz();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für das Objektmodel und gibt ihn zurück.
	 * 
	 * @see XPathFactory#newInstance(String)
	 * @return Konfigurator.
	 */
	public final ModelData<GThiz> openModelData() {
		return this.__modelData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 * 
	 * @see XPathFactory#setFeature(String, boolean)
	 * @return Konfigurator.
	 */
	public final FeatureData<GThiz> openFeatureData() {
		return this.__featureData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 * 
	 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator.
	 */
	public final VariableData<GThiz> openVariableData() {
		return this.__variableData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 * 
	 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator.
	 */
	public final FunctionData<GThiz> openFunctionData() {
		return this.__functionData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz thiz();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getFactory()
	 */
	@Override
	public final XPathFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final XPathFactoryConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__modelData, this.__featureData, this.__variableData, this.__functionData);
	}

}