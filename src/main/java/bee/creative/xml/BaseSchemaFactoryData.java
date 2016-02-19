package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link SchemaFactory}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseSchemaFactoryData<GThis> extends BaseBuilder<SchemaFactory, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#setFeature(String, boolean)
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
		protected final FeatureData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#setProperty(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder<String, Object, PropertyData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final PropertyData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler}.
	 * 
	 * @see SchemaFactory#setErrorHandler(ErrorHandler)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class HandlerData<GOwner> extends BaseValueBuilder<ErrorHandler, HandlerData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeHandlerData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final HandlerData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link LSResourceResolver}.
	 * 
	 * @see SchemaFactory#setResourceResolver(LSResourceResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ResolverData<GOwner> extends BaseValueBuilder<LSResourceResolver, ResolverData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeResolverData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final ResolverData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Sprache einer {@link SchemaFactory}.<br>
	 * Initialisiert wird diese Sprache via {@link #useW3C_XML_SCHEMA_NS_URI()}.
	 * 
	 * @see SchemaFactory#newInstance(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class LanguageData<GOwner> extends BaseValueBuilder<String, LanguageData<GOwner>> {

		/** Dieser Konstruktor initialisiert den Wert via {@link #useW3C_XML_SCHEMA_NS_URI()}. */
		public LanguageData() {
			this.useW3C_XML_SCHEMA_NS_URI();
		}

		{}

		/** Diese Methode setzt den Wert auf {@link XMLConstants#RELAXNG_NS_URI} und gibt {@code this} zurück.
		 * 
		 * @see #use(BaseValueBuilder)
		 * @return {@code this}. */
		public final LanguageData<GOwner> useRELAXNG_NS_URI() {
			return super.use(XMLConstants.RELAXNG_NS_URI);
		}

		/** Diese Methode setzt den Wert auf {@link XMLConstants#W3C_XML_SCHEMA_NS_URI} und gibt {@code this} zurück.
		 * 
		 * @see #use(BaseValueBuilder)
		 * @return {@code this}. */
		public final LanguageData<GOwner> useW3C_XML_SCHEMA_NS_URI() {
			return super.use(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		}

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeLanguageData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final LanguageData<GOwner> _this_() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert die {@link SchemaFactory}. */
	SchemaFactory _factory_;

	/** Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}. */
	final FeatureData<GThis> _featureData_ = new FeatureData<GThis>() {

		@Override
		public final GThis closeFeatureData() {
			return BaseSchemaFactoryData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}. */
	final PropertyData<GThis> _propertyData_ = new PropertyData<GThis>() {

		@Override
		public final GThis closePropertyData() {
			return BaseSchemaFactoryData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openHandlerData()}. */
	final HandlerData<GThis> _handlerData_ = new HandlerData<GThis>() {

		@Override
		public final GThis closeHandlerData() {
			return BaseSchemaFactoryData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openResolverData()}. */
	final ResolverData<GThis> _resolverData_ = new ResolverData<GThis>() {

		@Override
		public final GThis closeResolverData() {
			return BaseSchemaFactoryData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openLanguageData()}. */
	final LanguageData<GThis> _languageData_ = new LanguageData<GThis>() {

		@Override
		public final GThis closeLanguageData() {
			return BaseSchemaFactoryData.this._this_();
		}

	};

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseSchemaFactoryData<?> data) {
		if (data == null) return this._this_();
		this._factory_ = data._factory_;
		this._featureData_.use(data._featureData_);
		this._propertyData_.use(data._propertyData_);
		this._handlerData_.use(data._handlerData_);
		this._resolverData_.use(data._resolverData_);
		this._languageData_.use(data._languageData_);
		return this._this_();
	}

	/** Diese Methode gibt das {@link SchemaFactory} zurück.<br>
	 * Wenn über {@link #useFactory(SchemaFactory)} noch keine {@link SchemaFactory} gesetzt wurde, wird über {@link SchemaFactory#newInstance(String)} eine neue
	 * erstellt, über {@link #useFactory(SchemaFactory)} gesetzt und über {@link #updateFactory()} aktualisiert. Die zur Erstellung verwendete Sprache kann über
	 * {@link #openLanguageData()} konfiguriert werden.
	 * 
	 * @see #useFactory(SchemaFactory)
	 * @see #updateFactory()
	 * @return {@link SchemaFactory}.
	 * @throws SAXException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst. */
	public final SchemaFactory getFactory() throws SAXException {
		SchemaFactory result = this._factory_;
		if (result != null) return result;
		result = SchemaFactory.newInstance(this._languageData_.build());
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/** Diese Methode setzt die {@link SchemaFactory} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link SchemaFactory} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useFactory(final SchemaFactory factory) {
		this._factory_ = factory;
		return this._this_();
	}

	/** Diese Methode setzt die {@link SchemaFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(SchemaFactory)
	 * @return {@code this}. */
	public final GThis resetFactory() {
		return this.useFactory(null);
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link SchemaFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link SchemaFactory} die Einstellungen übertragen, die in
	 * {@link #openHandlerData()}, {@link #openResolverData()}, {@link #openFeatureData()} und {@link #openPropertyData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #getFactory()}, {@link SchemaFactory#setFeature(String, boolean)} bzw. {@link SchemaFactory#setProperty(String, Object)}
	 *         eine entsprechende Ausnahme auslöst. */
	public final GThis updateFactory() throws SAXException {
		final SchemaFactory factory = this.getFactory();
		for (final ErrorHandler value: this._handlerData_) {
			factory.setErrorHandler(value);
		}
		for (final LSResourceResolver value: this._resolverData_) {
			factory.setResourceResolver(value);
		}
		for (final Entry<String, Boolean> entry: this._featureData_) {
			factory.setFeature(entry.getKey(), entry.getValue());
		}
		for (final Entry<String, Object> entry: this._propertyData_) {
			factory.setProperty(entry.getKey(), entry.getValue());
		}
		return this._this_();
	}

	/** Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#setFeature(String, boolean)
	 * @return Konfigurator. */
	public final FeatureData<GThis> openFeatureData() {
		return this._featureData_;
	}

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften der und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#setProperty(String, Object)
	 * @return Konfigurator. */
	public final PropertyData<GThis> openPropertyData() {
		return this._propertyData_;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link ErrorHandler} und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#setErrorHandler(ErrorHandler)
	 * @return Konfigurator. */
	public final HandlerData<GThis> openHandlerData() {
		return this._handlerData_;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link LSResourceResolver} und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#setResourceResolver(LSResourceResolver)
	 * @return Konfigurator. */
	public final ResolverData<GThis> openResolverData() {
		return this._resolverData_;
	}

	/** Diese Methode öffnet den Konfigurator für die Schemasprache und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#newInstance(String)
	 * @return Konfigurator. */
	public final LanguageData<GThis> openLanguageData() {
		return this._languageData_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis _this_();

	/** {@inheritDoc} */
	@Override
	public final SchemaFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._featureData_, this._propertyData_, this._handlerData_, this._resolverData_, this._languageData_);
	}

}