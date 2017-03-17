package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link XPathFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseXPathFactoryData<GThis> extends BaseBuilder<XPathFactory, GThis> {

	/** Diese Klasse implementiert den Konfigurator für das Objektmodel einer {@link XPathFactory}.<br>
	 * Initialisiert wird dieses via {@link #useDEFAULT_OBJECT_MODEL_URI()}.
	 *
	 * @see XPathFactory#newInstance(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ModelData<GOwner> extends BaseValueBuilder<String, ModelData<GOwner>> {

		/** Dieser Konstruktor initialisiert den Wert via {@link #useDEFAULT_OBJECT_MODEL_URI()}. */
		public ModelData() {
			this.useDEFAULT_OBJECT_MODEL_URI();
		}

		{}

		/** Diese Methode setzt den Wert auf {@link XPathFactory#DEFAULT_OBJECT_MODEL_URI} und gibt {@code this} zurück.
		 *
		 * @see #use(BaseValueBuilder)
		 * @return {@code this}. */
		public final ModelData<GOwner> useDEFAULT_OBJECT_MODEL_URI() {
			return super.use(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
		}

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeModelData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final ModelData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link XPathFactory}.
	 *
	 * @see XPathFactory#setFeature(String, boolean)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FeatureData<GOwner> extends BaseFeatureData<FeatureData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFeatureData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final FeatureData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver}.
	 *
	 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
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
		protected final VariableData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver}.
	 *
	 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
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
		protected final FunctionData<GOwner> customThis() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert die {@link XPathFactory}. */
	XPathFactory factory;

	/** Dieses Feld speichert den Konfigurator für {@link #openModelData()}. */
	final ModelData<GThis> modelData = new ModelData<GThis>() {

		@Override
		public final GThis closeModelData() {
			return BaseXPathFactoryData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}. */
	final FeatureData<GThis> featureData = new FeatureData<GThis>() {

		@Override
		public final GThis closeFeatureData() {
			return BaseXPathFactoryData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openVariableData()}. */
	final VariableData<GThis> variableData = new VariableData<GThis>() {

		@Override
		public final GThis closeVariableData() {
			return BaseXPathFactoryData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openFunctionData()}. */
	final FunctionData<GThis> functionData = new FunctionData<GThis>() {

		@Override
		public final GThis closeFunctionData() {
			return BaseXPathFactoryData.this.customThis();
		}

	};

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseXPathFactoryData<?> data) {
		if (data == null) return this.customThis();
		this.factory = data.factory;
		this.modelData.use(data.modelData);
		this.featureData.use(data.featureData);
		this.variableData.use(data.variableData);
		this.functionData.use(data.functionData);
		return this.customThis();
	}

	/** Diese Methode gibt das {@link XPathFactory} zurück.<br>
	 * Wenn über {@link #useFactory(XPathFactory)} noch keine {@link XPathFactory} gesetzt wurde, wird über {@link XPathFactory#newInstance(String)} eine neue
	 * erstellt, über {@link #useFactory(XPathFactory)} gesetzt und über {@link #updateFactory()} aktualisiert. Das zur Erstellung verwendete Objektmodell kann
	 * über {@link #openModelData()} konfiguriert werden.
	 *
	 * @see #useFactory(XPathFactory)
	 * @return {@link XPathFactory}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactory#newInstance(String)} bzw. {@link #updateFactory()} eine entsprechende Ausnahme
	 *         auslöst. */
	public final XPathFactory getFactory() throws XPathFactoryConfigurationException {
		XPathFactory result = this.factory;
		if (result != null) return result;
		result = XPathFactory.newInstance(this.modelData.build());
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/** Diese Methode setzt die {@link XPathFactory} und gibt {@code this} zurück.
	 *
	 * @param factory {@link XPathFactory} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useFactory(final XPathFactory factory) {
		this.factory = factory;
		return this.customThis();
	}

	/** Diese Methode setzt die {@link XPathFactory} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useFactory(XPathFactory)
	 * @return {@code this}. */
	public final GThis resetFactory() {
		return this.useFactory(null);
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link XPathFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link XPathFactory} die Einstellungen übertragen, die in
	 * {@link #openVariableData()}, {@link #openFunctionData()} und {@link #openFeatureData()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactory#setFeature(String, boolean)} bzw. {@link #getFactory()} eine entsprechende Ausnahme
	 *         auslöst. */
	public final GThis updateFactory() throws XPathFactoryConfigurationException {
		final XPathFactory factory = this.getFactory();
		for (final XPathVariableResolver value: this.variableData) {
			factory.setXPathVariableResolver(value);
		}
		for (final XPathFunctionResolver value: this.functionData) {
			factory.setXPathFunctionResolver(value);
		}
		for (final Entry<String, Boolean> entry: this.featureData) {
			factory.setFeature(entry.getKey(), entry.getValue());
		}
		return this.customThis();
	}

	/** Diese Methode öffnet den Konfigurator für das Objektmodel und gibt ihn zurück.
	 *
	 * @see XPathFactory#newInstance(String)
	 * @return Konfigurator. */
	public final ModelData<GThis> openModelData() {
		return this.modelData;
	}

	/** Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 *
	 * @see XPathFactory#setFeature(String, boolean)
	 * @return Konfigurator. */
	public final FeatureData<GThis> openFeatureData() {
		return this.featureData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 *
	 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator. */
	public final VariableData<GThis> openVariableData() {
		return this.variableData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 *
	 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator. */
	public final FunctionData<GThis> openFunctionData() {
		return this.functionData;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getFactory() */
	@Override
	public final XPathFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final XPathFactoryConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.modelData, this.featureData, this.variableData, this.functionData);
	}

}