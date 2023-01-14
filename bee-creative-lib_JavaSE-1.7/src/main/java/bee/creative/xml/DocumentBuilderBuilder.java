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
public abstract class DocumentBuilderBuilder<GOwner> extends BaseValueBuilder<DocumentBuilder, GOwner> {

	public static abstract class Value<GOwner> extends DocumentBuilderBuilder<GOwner> {

		DocumentBuilder value;

		FactoryValue factory = new FactoryValue();

		HandlerValue handler = new HandlerValue();

		ResolverValue resolver = new ResolverValue();

		@Override
		public DocumentBuilder get() {
			return this.value;
		}

		@Override
		public void set(final DocumentBuilder value) {
			this.value = value;
		}

		@Override
		public FactoryValue factory() {
			return this.factory;
		}

		@Override
		public HandlerValue handler() {
			return this.handler;
		}

		@Override
		public ResolverValue resolver() {
			return this.resolver;
		}

	}

	public static abstract class Proxy<GOwner> extends DocumentBuilderBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public DocumentBuilder get() {
			return this.value().get();
		}

		@Override
		public void set(final DocumentBuilder value) {
			this.value().set(value);
		}

		@Override
		public FactoryValue factory() {
			return this.value().factory();
		}

		@Override
		public HandlerValue handler() {
			return this.value().handler();
		}

		@Override
		public ResolverValue resolver() {
			return this.value().resolver();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#newDocumentBuilder() */
	public static class FactoryValue extends DocumentBuilderFactoryBuilder.Value<FactoryValue> {

		@Override
		public FactoryValue owner() {
			return this;
		}

	}

	public class FactoryProxy extends DocumentBuilderFactoryBuilder.Proxy<GOwner> {

		@Override
		protected FactoryValue value() {
			return DocumentBuilderBuilder.this.factory();
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderBuilder.this.owner();
		}

	}

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
			return DocumentBuilderBuilder.this.handler().get();
		}

		@Override
		public void set(final ErrorHandler value) {
			DocumentBuilderBuilder.this.handler().set(value);
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler}.
	 *
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class HandlerBuilder<GOwner> extends BaseValueBuilder<ErrorHandler, GOwner> {

	}

	public static class ResolverValue extends ResolverBuilder<ResolverValue> {

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
		public ResolverValue owner() {
			return this;
		}

	}

	public class ResolverProxy extends ResolverBuilder<GOwner> {

		@Override
		public EntityResolver get() {
			return DocumentBuilderBuilder.this.resolver().get();
		}

		@Override
		public void set(final EntityResolver value) {
			DocumentBuilderBuilder.this.resolver().set(value);
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link EntityResolver}.
	 *
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ResolverBuilder<GOwner> extends BaseValueBuilder<EntityResolver, GOwner> {

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final DocumentBuilderBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forFactory().use(that.factory());
		this.forHandler().use(that.handler());
		this.forResolver().use(that.resolver());
		return this.owner();
	}

	/** Diese Methode gibt den {@link DocumentBuilder} zurück. Wenn über {@link #useValue(Object)} noch kein {@link DocumentBuilder} gesetzt wurde, wird über
	 * {@link DocumentBuilderFactory#newDocumentBuilder()} ein neuer erstellt, über {@link #useValue(Object)} gesetzt und über {@link #updateValue()}
	 * aktualisiert. Für die Erstellung wird die {@link DocumentBuilderFactory} genutzt, die in {@link #factory()} konfiguriert ist.
	 *
	 * @see #updateValue()
	 * @return {@link DocumentBuilder}.
	 * @throws SAXException Wenn {@link FactoryValue#putValue()} eine entsprechende Ausnahme auslöst.
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
	 * {@link #putValue()} ermittelten {@link DocumentBuilder} die Einstellungen übertragen, die in {@link #handler()} und {@link #resolver()} konfiguriert sind.
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
	public abstract FactoryValue factory();

	/** Diese Methode öffnet den Konfigurator für den {@link ErrorHandler} und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @return Konfigurator. */
	public abstract HandlerValue handler();

	/** Diese Methode öffnet den Konfigurator für den {@link EntityResolver} und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @return Konfigurator. */
	public abstract ResolverValue resolver();

	public FactoryProxy forFactory() {
		return new FactoryProxy();
	}

	public HandlerProxy forHandler() {
		return new HandlerProxy();
	}

	public ResolverProxy forResolver() {
		return new ResolverProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.factory(), this.handler(), this.resolver());
	}

}