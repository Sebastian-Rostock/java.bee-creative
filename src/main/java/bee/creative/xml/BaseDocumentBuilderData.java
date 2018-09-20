package bee.creative.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import bee.creative.util.Objects;
import bee.creative.util.Producers.BaseBuilder;
import bee.creative.util.Producers.BaseValueBuilder;

/** Diese Klasse implementiert einen Konfigurator für einen {@link DocumentBuilder}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseDocumentBuilderData<GThis> extends BaseBuilder<DocumentBuilder, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#newDocumentBuilder()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FactoryData<GOwner> extends BaseDocumentBuilderFactoryData<FactoryData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFactoryData();

		/** {@inheritDoc} */
		@Override
		protected final FactoryData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler}.
	 *
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class HandlerData<GOwner> extends BaseValueBuilder<ErrorHandler, HandlerData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeHandlerData();

		/** {@inheritDoc} */
		@Override
		protected final HandlerData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link EntityResolver}.
	 *
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ResolverData<GOwner> extends BaseValueBuilder<EntityResolver, ResolverData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeResolverData();

		/** {@inheritDoc} */
		@Override
		protected final ResolverData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert den {@link DocumentBuilder}. */
	DocumentBuilder builder;

	/** Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}. */
	final FactoryData<GThis> factoryData = new FactoryData<GThis>() {

		@Override
		public final GThis closeFactoryData() {
			return BaseDocumentBuilderData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openHandlerData()}. */
	final HandlerData<GThis> handlerData = new HandlerData<GThis>() {

		@Override
		public final GThis closeHandlerData() {
			return BaseDocumentBuilderData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openResolverData()}. */
	final ResolverData<GThis> resolverData = new ResolverData<GThis>() {

		@Override
		public final GThis closeResolverData() {
			return BaseDocumentBuilderData.this.customThis();
		}

	};

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseDocumentBuilderData<?> data) {
		if (data == null) return this.customThis();
		this.builder = data.builder;
		this.factoryData.use(data.factoryData);
		this.handlerData.use(data.handlerData);
		this.resolverData.use(data.resolverData);
		return this.customThis();
	}

	/** Diese Methode gibt den {@link DocumentBuilder} zurück.<br>
	 * Wenn über {@link #useBuilder(DocumentBuilder)} noch kein {@link DocumentBuilder} gesetzt wurde, wird über
	 * {@link DocumentBuilderFactory#newDocumentBuilder()} ein neuer erstellt, über {@link #useBuilder(DocumentBuilder)} gesetzt und über {@link #updateBuilder()}
	 * aktualisiert. Für die Erstellung wird die {@link DocumentBuilderFactory} genutzt, die in {@link #openFactoryData()} konfiguriert ist.
	 *
	 * @see #useBuilder(DocumentBuilder)
	 * @see #updateBuilder()
	 * @return {@link DocumentBuilder}.
	 * @throws SAXException Wenn {@link FactoryData#getFactory()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#newDocumentBuilder()} eine entsprechende Ausnahme auslöst. */
	public final DocumentBuilder getBuilder() throws SAXException, ParserConfigurationException {
		DocumentBuilder result = this.builder;
		if (result != null) return result;
		result = this.factoryData.getFactory().newDocumentBuilder();
		this.useBuilder(result);
		this.updateBuilder();
		return result;
	}

	/** Diese Methode setzt den {@link DocumentBuilder} und gibt {@code this} zurück.
	 *
	 * @param builder {@link DocumentBuilder} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useBuilder(final DocumentBuilder builder) {
		this.builder = builder;
		return this.customThis();
	}

	/** Diese Methode setzt den {@link DocumentBuilder} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useBuilder(DocumentBuilder)
	 * @return {@code this}. */
	public final GThis resetBuilder() {
		return this.useBuilder(null);
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link DocumentBuilder} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf den über {@link #getBuilder()} ermittelten {@link DocumentBuilder} die Einstellungen übertragen, die in
	 * {@link #openHandlerData()} und {@link #openResolverData()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #getBuilder()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link #getBuilder()} eine entsprechende Ausnahme auslöst. */
	public final GThis updateBuilder() throws SAXException, ParserConfigurationException {
		final DocumentBuilder builder = this.getBuilder();
		for (final ErrorHandler value: this.handlerData) {
			builder.setErrorHandler(value);
		}
		for (final EntityResolver value: this.resolverData) {
			builder.setEntityResolver(value);
		}
		return this.customThis();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link DocumentBuilderFactory} und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#newDocumentBuilder()
	 * @return Konfigurator. */
	public final FactoryData<GThis> openFactoryData() {
		return this.factoryData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link ErrorHandler} und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @return Konfigurator. */
	public final HandlerData<GThis> openHandlerData() {
		return this.handlerData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link EntityResolver} und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @return Konfigurator. */
	public final ResolverData<GThis> openResolverData() {
		return this.resolverData;
	}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getBuilder() */
	@Override
	public final DocumentBuilder get() throws IllegalStateException {
		try {
			return this.getBuilder();
		} catch (final SAXException | ParserConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.factoryData, this.handlerData, this.resolverData);
	}

}