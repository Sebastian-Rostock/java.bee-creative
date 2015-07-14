package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link SchemaFactory}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseSchemaFactoryData<GThiz> extends BaseBuilder<SchemaFactory, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#setFeature(String, boolean)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class FeatureData extends BaseFeatureData<FeatureData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeFeatureData() {
			return BaseSchemaFactoryData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FeatureData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#setProperty(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class PropertyData extends BaseMapBuilder<String, Object, PropertyData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closePropertyData() {
			return BaseSchemaFactoryData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected PropertyData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler} einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#setErrorHandler(ErrorHandler)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class HandlerData extends BaseValueBuilder<ErrorHandler, HandlerData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeListenerData() {
			return BaseSchemaFactoryData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected HandlerData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link LSResourceResolver} einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#setResourceResolver(LSResourceResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class ResolverData extends BaseValueBuilder<LSResourceResolver, ResolverData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeResolverData() {
			return BaseSchemaFactoryData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ResolverData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Sprache einer {@link SchemaFactory}.<br>
	 * Initialisiert wird diese Sprache via {@link #useW3C_XML_SCHEMA_NS_URI()}.
	 * 
	 * @see SchemaFactory#newInstance(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class LanguageData extends BaseValueBuilder<String, LanguageData> {

		/**
		 * Dieser Konstruktor initialisiert den Wert via {@link #useW3C_XML_SCHEMA_NS_URI()}.
		 */
		public LanguageData() {
			this.useW3C_XML_SCHEMA_NS_URI();
		}

		{}

		/**
		 * Diese Methode setzt den Wert auf {@link XMLConstants#RELAXNG_NS_URI} und gibt {@code this} zurück.
		 * 
		 * @see #use(BaseValueBuilder)
		 * @return {@code this}.
		 */
		public LanguageData useRELAXNG_NS_URI() {
			return super.use(XMLConstants.RELAXNG_NS_URI);
		}

		/**
		 * Diese Methode setzt den Wert auf {@link XMLConstants#W3C_XML_SCHEMA_NS_URI} und gibt {@code this} zurück.
		 * 
		 * @see #use(BaseValueBuilder)
		 * @return {@code this}.
		 */
		public LanguageData useW3C_XML_SCHEMA_NS_URI() {
			return super.use(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		}

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeLanguageData() {
			return BaseSchemaFactoryData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected LanguageData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link SchemaFactory}.
	 */
	SchemaFactory factory;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
	 */
	final FeatureData featureData = //
		new FeatureData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}.
	 */
	final PropertyData propertyData = //
		new PropertyData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openHandlerData()}.
	 */
	final HandlerData handlerData = //
		new HandlerData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
	 */
	final ResolverData resolverData = //
		new ResolverData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openLanguageData()}.
	 */
	final LanguageData languageData = //
		new LanguageData();

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseSchemaData<?>.SchemaFactoryData data) {
		if (data == null) return this.thiz();
		this.factory = data.factory;
		this.featureData.use(data.featureData);
		this.propertyData.use(data.propertyData);
		this.handlerData.use(data.handlerData);
		this.resolverData.use(data.resolverData);
		this.languageData.use(data.languageData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt das {@link SchemaFactory} zurück.<br>
	 * Wenn über {@link #useFactory(SchemaFactory)} noch keine {@link SchemaFactory} gesetzt wurde, wird über {@link SchemaFactory#newInstance(String)} eine neue
	 * erstellt, über {@link #useFactory(SchemaFactory)} gesetzt und über {@link #updateFactory()} aktualisiert. Die zur Erstellung verwendete Sprache kann in
	 * über {@link #openLanguageData()} konfiguriert werden.
	 * 
	 * @see #useFactory(SchemaFactory)
	 * @return {@link SchemaFactory}.
	 * @throws SAXException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 */
	public SchemaFactory getFactory() throws SAXException {
		SchemaFactory result = this.factory;
		if (result != null) return result;
		result = SchemaFactory.newInstance(this.languageData.build());
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/**
	 * Diese Methode setzt die {@link SchemaFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(SchemaFactory)
	 * @return {@code this}.
	 */
	public GThiz resetFactory() {
		return this.useFactory(null);
	}

	/**
	 * Diese Methode setzt die {@link SchemaFactory} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link SchemaFactory} oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useFactory(final SchemaFactory factory) {
		this.factory = factory;
		return this.thiz();
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen der {@link SchemaFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link SchemaFactory} die Einstellungen übertragen, die in
	 * {@link #openHandlerData()}, {@link #openResolverData()}, {@link #openFeatureData()} und {@link #openPropertyData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link SchemaFactory#setFeature(String, boolean)} bzw. {@link SchemaFactory#setProperty(String, Object)} eine entsprechende
	 *         Ausnahme auslöst.
	 */
	public GThiz updateFactory() throws SAXException {
		final SchemaFactory factory = this.getFactory();
		for (final ErrorHandler value: this.handlerData) {
			factory.setErrorHandler(value);
		}
		for (final LSResourceResolver value: this.resolverData) {
			factory.setResourceResolver(value);
		}
		for (final Entry<String, Boolean> entry: this.featureData) {
			factory.setFeature(entry.getKey(), entry.getValue());
		}
		for (final Entry<String, Object> entry: this.propertyData) {
			factory.setProperty(entry.getKey(), entry.getValue());
		}
		return this.thiz();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getFeature(String) Fähigkeiten} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setFeature(String, boolean)
	 * @return Konfigurator.
	 */
	public FeatureData openFeatureData() {
		return this.featureData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getAttribute(String) Attribute} der und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setAttribute(String, Object)
	 * @return Konfigurator.
	 */
	public PropertyData openPropertyData() {
		return this.propertyData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getErrorListener() Fehlerbehandlung} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setErrorListener(ErrorListener)
	 * @return Konfigurator.
	 */
	public HandlerData openHandlerData() {
		return this.handlerData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getURIResolver() URL-Auflöser} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setURIResolver(URIResolver)
	 * @return Konfigurator.
	 */
	public ResolverData openResolverData() {
		return this.resolverData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link SchemaFactory#newInstance(String) Schemasprache} und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#newInstance(String)
	 * @return Konfigurator.
	 */
	public LanguageData openLanguageData() {
		return this.languageData;
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
	public SchemaFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.featureData, this.propertyData, this.handlerData, this.resolverData, this.languageData);
	}

}