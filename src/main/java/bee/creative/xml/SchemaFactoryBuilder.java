package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.xml.SchemaBuilder.FactoryValue;
import bee.creative.xml.SchemaFactoryBuilder.FeaturesValue;
import bee.creative.xml.SchemaFactoryBuilder.HandlerValue;
import bee.creative.xml.SchemaFactoryBuilder.LanguageValue;
import bee.creative.xml.SchemaFactoryBuilder.PropertiesValue;
import bee.creative.xml.SchemaFactoryBuilder.ResolverValue;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link SchemaFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class SchemaFactoryBuilder<GOwner> extends BaseValueBuilder<SchemaFactory, GOwner> {

	/** Diese Klasse implementiert den Konfigurator einer {@link SchemaFactory}.
	 *
	 * @see SchemaFactory#newSchema(Source) */
	public static abstract class Value<GOwner> extends SchemaFactoryBuilder<GOwner> {

		FeaturesValue features = new FeaturesValue();

		PropertiesValue properties = new PropertiesValue();

		HandlerValue handler = new HandlerValue();

		ResolverValue resolver = new ResolverValue();

		LanguageValue language = new LanguageValue();

		SchemaFactory value;

		@Override
		public SchemaFactory get() {
			return this.value;
		}

		@Override
		public void set(final SchemaFactory value) {
			this.value = value;
		}

		@Override
		public FeaturesValue features() {
			return this.features;
		}

		@Override
		public PropertiesValue properties() {
			return this.properties;
		}

		@Override
		public HandlerValue handler() {
			return this.handler;
		}

		@Override
		public ResolverValue resolver() {
			return this.resolver;
		}

		@Override
		public LanguageValue language() {
			return this.language;
		}

	}

	public static abstract class Proxy<GOwner> extends SchemaFactoryBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public SchemaFactory get() {
			return value().get();
		}

		@Override
		public void set(final SchemaFactory value) {
			value().set(value);
		}

		@Override
		public FeaturesValue features() {
			return value().features();
		}

		@Override
		public PropertiesValue properties() {
			return value().properties();
		}

		@Override
		public HandlerValue handler() {
			return value().handler();
		}

		@Override
		public ResolverValue resolver() {
			return value().resolver();
		}

		@Override
		public LanguageValue language() {
			return value().language();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link SchemaFactory}.
	 *
	 * @see SchemaFactory#setFeature(String, boolean) */
	public static class FeaturesValue extends FeaturesBuilder.Value<FeaturesValue> {

		@Override
		public FeaturesValue owner() {
			return this;
		}

	}

	public class FeaturesProxy extends FeaturesBuilder.Proxy<GOwner> {

		@Override
		protected FeaturesValue value() {
			return SchemaFactoryBuilder.this.features();
		}

		@Override
		public GOwner owner() {
			return SchemaFactoryBuilder.this.owner();
		}

	}

	public static class PropertiesValue extends PropertiesBuilder<PropertiesValue> {

		Map<String, Object> value = new HashMap<>();

		@Override
		public Map<String, Object> get() {
			return this.value;
		}

		@Override
		public PropertiesValue owner() {
			return this;
		}

	}

	public class PropertiesProxy extends PropertiesBuilder<GOwner> {

		@Override
		public Map<String, Object> get() {
			return SchemaFactoryBuilder.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return SchemaFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link SchemaFactory}.
	 *
	 * @see SchemaFactory#setProperty(String, Object)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertiesBuilder<GOwner> extends BaseMapBuilder<String, Object, Map<String, Object>, GOwner> {

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler}.
	 *
	 * @see SchemaFactory#setErrorHandler(ErrorHandler) */
	public static class HandlerValue extends HandlerBuilder<HandlerValue> {

		ErrorHandler value;

		@Override
		public ErrorHandler get() {
			return this.value;
		}

		@Override
		public void set(final ErrorHandler value) {
			this.value = value;
		}

		@Override
		public HandlerValue owner() {
			return this;
		}

	}

	public class HandlerProxy extends HandlerBuilder<GOwner> {

		@Override
		public ErrorHandler get() {
			return SchemaFactoryBuilder.this.handler().get();
		}

		@Override
		public void set(final ErrorHandler value) {
			SchemaFactoryBuilder.this.handler().set(value);
		}

		@Override
		public GOwner owner() {
			return SchemaFactoryBuilder.this.owner();
		}

	}

	public static abstract class HandlerBuilder<GOwner> extends BaseValueBuilder<ErrorHandler, GOwner> {

	}

	public static class ResolverValue extends ResolverBuilder<ResolverValue> {

		LSResourceResolver value;

		@Override
		public LSResourceResolver get() {
			return this.value;
		}

		@Override
		public void set(final LSResourceResolver value) {
			this.value = value;
		}

		@Override
		public ResolverValue owner() {
			return this;
		}

	}

	public class ResolverProxy extends ResolverBuilder<GOwner> {

		@Override
		public LSResourceResolver get() {
			return SchemaFactoryBuilder.this.resolver().get();
		}

		@Override
		public void set(final LSResourceResolver value) {
			SchemaFactoryBuilder.this.resolver().set(value);
		}

		@Override
		public GOwner owner() {
			return SchemaFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link LSResourceResolver}.
	 *
	 * @see SchemaFactory#setResourceResolver(LSResourceResolver) */
	public static abstract class ResolverBuilder<GOwner> extends BaseValueBuilder<LSResourceResolver, GOwner> {

	}

	public static class LanguageValue extends LanguageBilder<LanguageValue> {

		String value;

		public LanguageValue() {
			this.useW3C_XML_SCHEMA_NS_URI();
		}

		@Override
		public String get() {
			return this.value;
		}

		@Override
		public void set(final String value) {
			this.value = value;
		}

		@Override
		public LanguageValue owner() {
			return this;
		}

	}

	public class LanguageProxy extends LanguageBilder<GOwner> {

		@Override
		public String get() {
			return SchemaFactoryBuilder.this.language().get();
		}

		@Override
		public void set(final String value) {
			SchemaFactoryBuilder.this.language().set(value);
		}

		@Override
		public GOwner owner() {
			return SchemaFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Sprache einer {@link SchemaFactory}. Initialisiert wird diese Sprache über
	 * {@link #useW3C_XML_SCHEMA_NS_URI()}.
	 *
	 * @see SchemaFactory#newInstance(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class LanguageBilder<GOwner> extends BaseValueBuilder<String, GOwner> {

		/** Diese Methode setzt den Wert auf {@link XMLConstants#RELAXNG_NS_URI} und gibt {@code this} zurück.
		 *
		 * @see #useValue(Object)
		 * @return {@code this}. */
		public GOwner useRELAXNG_NS_URI() {
			return super.useValue(XMLConstants.RELAXNG_NS_URI);
		}

		/** Diese Methode setzt den Wert auf {@link XMLConstants#W3C_XML_SCHEMA_NS_URI} und gibt {@code this} zurück.
		 *
		 * @see #useValue(Object)
		 * @return {@code this}. */
		public GOwner useW3C_XML_SCHEMA_NS_URI() {
			return super.useValue(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		}

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final SchemaFactoryBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forFeatures().use(that.features());
		this.forProperties().use(that.forProperties());
		this.forHandler().use(that.handler());
		this.forResolver().use(that.resolver());
		this.forLanguage().use(that.language());
		return this.owner();
	}
 

	/** Diese Methode gibt das {@link SchemaFactory} zurück. Wenn über {@link #useValue(Object)} noch keine {@link SchemaFactory} gesetzt wurde, wird über
	 * {@link SchemaFactory#newInstance(String)} eine neue erstellt, über {@link #useValue(Object)} gesetzt und über {@link #updateValue()} aktualisiert. Die zur
	 * Erstellung verwendete Sprache kann über {@link #language()} konfiguriert werden.
	 *
	 * @see #updateValue()
	 * @return {@link SchemaFactory}.
	 * @throws SAXException Wenn {@link #updateValue()} eine entsprechende Ausnahme auslöst. */
	public SchemaFactory putValue() throws SAXException {
		SchemaFactory src = this.getValue();
		if (src != null) return src;
		src = SchemaFactory.newInstance(this.language().get());
		this.useValue(src);
		this.updateValue();
		return src;
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link SchemaFactory} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf die über
	 * {@link #putValue()} ermittelte {@link SchemaFactory} die Einstellungen übertragen, die in {@link #handler()}, {@link #resolver()}, {@link #features()} und
	 * {@link #properties()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #putValue()}, {@link SchemaFactory#setFeature(String, boolean)} bzw. {@link SchemaFactory#setProperty(String, Object)}
	 *         eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws SAXException {
		final SchemaFactory res = this.putValue();
		for (final ErrorHandler src: this.handler()) {
			res.setErrorHandler(src);
		}
		for (final LSResourceResolver src: this.resolver()) {
			res.setResourceResolver(src);
		}
		for (final Entry<String, Boolean> src: this.features()) {
			res.setFeature(src.getKey(), src.getValue());
		}
		for (final Entry<String, Object> src: this.properties()) {
			res.setProperty(src.getKey(), src.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 *
	 * @see SchemaFactory#setFeature(String, boolean)
	 * @return Konfigurator. */
	public abstract FeaturesValue features();

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften der und gibt ihn zurück.
	 *
	 * @see SchemaFactory#setProperty(String, Object)
	 * @return Konfigurator. */
	public abstract PropertiesValue properties();

	/** Diese Methode öffnet den Konfigurator für den {@link ErrorHandler} und gibt ihn zurück.
	 *
	 * @see SchemaFactory#setErrorHandler(ErrorHandler)
	 * @return Konfigurator. */
	public abstract HandlerValue handler();

	/** Diese Methode öffnet den Konfigurator für den {@link LSResourceResolver} und gibt ihn zurück.
	 *
	 * @see SchemaFactory#setResourceResolver(LSResourceResolver)
	 * @return Konfigurator. */
	public abstract ResolverValue resolver();

	/** Diese Methode öffnet den Konfigurator für die Schemasprache und gibt ihn zurück.
	 *
	 * @see SchemaFactory#newInstance(String)
	 * @return Konfigurator. */
	public abstract LanguageValue language();

	public FeaturesProxy forFeatures() {
		return new FeaturesProxy();
	}

	public PropertiesProxy forProperties() {
		return new PropertiesProxy();
	}

	public HandlerProxy forHandler() {
		return new HandlerProxy();
	}

	public ResolverProxy forResolver() {
		return new ResolverProxy();
	}

	public LanguageProxy forLanguage() {
		return new LanguageProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.features(), this.properties(), this.handler(), this.resolver(), this.language());
	}

}