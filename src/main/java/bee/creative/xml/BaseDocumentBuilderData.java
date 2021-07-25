package bee.creative.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert einen Konfigurator für einen {@link DocumentBuilder}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseDocumentBuilderData<GOwner> extends BaseValueBuilder<DocumentBuilder, GOwner> {

	/** Diese Klasse implementiert den Konfigurator für die {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#newDocumentBuilder() */
	public static class FactoryData extends BaseDocumentBuilderFactoryData<FactoryData> {

		DocumentBuilderFactory value;

		SchemaData schema = new SchemaData();

		FeaturesValue features = new FeaturesValue();

		PropertiesData properties = new PropertiesData();

		AttributesData attributes = new AttributesData();

		@Override
		public DocumentBuilderFactory get() {
			return null;
		}

		@Override
		public void set(final DocumentBuilderFactory value) {
		}

		@Override
		public SchemaData schema() {
			return this.schema;
		}

		@Override
		public FeaturesValue features() {
			return this.features;
		}

		@Override
		public PropertiesData properties() {
			return this.properties;
		}

		@Override
		public AttributesData attributes() {
			return this.attributes;
		}

		@Override
		public FactoryData owner() {
			return this;
		}

	}

	public class FactoryData2 extends BaseDocumentBuilderFactoryData<GOwner> {

		@Override
		public DocumentBuilderFactory get() {
			return BaseDocumentBuilderData.this.factory().get();
		}

		@Override
		public void set(final DocumentBuilderFactory value) {
			BaseDocumentBuilderData.this.factory().set(value);
		}

		@Override
		public SchemaData schema() {
			return BaseDocumentBuilderData.this.factory().schema();
		}

		@Override
		public FeaturesValue features() {
			return BaseDocumentBuilderData.this.factory().features();
		}

		@Override
		public PropertiesData properties() {
			return BaseDocumentBuilderData.this.factory().properties();
		}

		@Override
		public AttributesData attributes() {
			return BaseDocumentBuilderData.this.factory().attributes();
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderData.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler}.
	 *
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseHandlerData<GOwner> extends BaseValueBuilder<ErrorHandler, GOwner> {

	}

	public static class HandlerData extends BaseHandlerData<HandlerData> {

		private ErrorHandler value;

		@Override
		public ErrorHandler get() {
			return this.value;
		}

		@Override
		public void set(final ErrorHandler value) {
			this.value = value;
		}

		@Override
		public HandlerData owner() {
			return this;
		}

	}

	public class HandlerData2 extends BaseHandlerData<GOwner> {

		@Override
		public ErrorHandler get() {
			return BaseDocumentBuilderData.this.handler().get();
		}

		@Override
		public void set(final ErrorHandler value) {
			BaseDocumentBuilderData.this.handler().set(value);
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderData.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link EntityResolver}.
	 *
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseResolverData<GOwner> extends BaseValueBuilder<EntityResolver, GOwner> {

	}

	public static class ResolverData extends BaseResolverData<ResolverData> {

		EntityResolver value;

		@Override
		public EntityResolver get() {
			return this.value;
		}

		@Override
		public void set(final EntityResolver value) {
			this.value = value;
		}

		@Override
		public ResolverData owner() {
			return this;
		}

	}

	public class ResolverData2 extends BaseResolverData<GOwner> {

		@Override
		public EntityResolver get() {
			return BaseDocumentBuilderData.this.resolver().get();
		}

		@Override
		public void set(final EntityResolver value) {
			BaseDocumentBuilderData.this.resolver().set(value);
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderData.this.owner();
		}

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final BaseDocumentBuilderData<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forFactory().use(that.factory());
		this.forHandler().use(that.handler());
		this.forResolver().use(that.resolver());
		return this.owner();
	}

	/** Diese Methode gibt den {@link DocumentBuilder} zurück. Wenn über {@link #useBuilder(DocumentBuilder)} noch kein {@link DocumentBuilder} gesetzt wurde,
	 * wird über {@link DocumentBuilderFactory#newDocumentBuilder()} ein neuer erstellt, über {@link #useBuilder(DocumentBuilder)} gesetzt und über
	 * {@link #updateValue()} aktualisiert. Für die Erstellung wird die {@link DocumentBuilderFactory} genutzt, die in {@link #factory()} konfiguriert ist.
	 *
	 * @see #useBuilder(DocumentBuilder)
	 * @see #updateValue()
	 * @return {@link DocumentBuilder}.
	 * @throws SAXException Wenn {@link FactoryData#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#newDocumentBuilder()} eine entsprechende Ausnahme auslöst. */
	public DocumentBuilder putValue() throws SAXException, ParserConfigurationException {
		DocumentBuilder result = this.getValue();
		if (result != null) return result;
		result = this.forFactory().putValue().newDocumentBuilder();
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link DocumentBuilder} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #putValue()} ermittelten {@link DocumentBuilder} die Einstellungen übertragen, die in {@link #openHandlerData()} und {@link #openResolverData()}
	 * konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link #putValue()} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws SAXException, ParserConfigurationException {
		final DocumentBuilder res = this.putValue();
		for (final ErrorHandler src: this.handler()) {
			res.setErrorHandler(src);
		}
		for (final EntityResolver src: this.resolver()) {
			res.setEntityResolver(src);
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link DocumentBuilderFactory} und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#newDocumentBuilder()
	 * @return Konfigurator. */
	public abstract FactoryData factory();

	public FactoryData2 forFactory() {
		return new FactoryData2();
	}

	/** Diese Methode öffnet den Konfigurator für den {@link ErrorHandler} und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @return Konfigurator. */
	public abstract HandlerData handler();

	public HandlerData2 forHandler() {
		return new HandlerData2();
	}

	/** Diese Methode öffnet den Konfigurator für den {@link EntityResolver} und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @return Konfigurator. */
	public abstract ResolverData resolver();

	public ResolverData2 forResolver() {
		return new ResolverData2();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.factory(), this.handler(), this.resolver());
	}

}