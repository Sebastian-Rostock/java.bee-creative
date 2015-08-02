package bee.creative.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen Konfigurator für einen {@link DocumentBuilder}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseDocumentBuilderData<GThiz> extends BaseBuilder<DocumentBuilder, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link DocumentBuilderFactory}.
	 * 
	 * @see DocumentBuilderFactory#newDocumentBuilder()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class FactoryData<GOwner> extends BaseDocumentBuilderFactoryData<FactoryData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeFactoryData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FactoryData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler}.
	 * 
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class HandlerData<GOwner> extends BaseValueBuilder<ErrorHandler, HandlerData<GOwner>> {

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
		protected HandlerData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link EntityResolver}.
	 * 
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ResolverData<GOwner> extends BaseValueBuilder<EntityResolver, ResolverData<GOwner>> {

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
		protected ResolverData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@link DocumentBuilder}.
	 */
	DocumentBuilder builder;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
	 */
	final FactoryData<GThiz> factoryData = new FactoryData<GThiz>() {

		@Override
		public GThiz closeFactoryData() {
			return BaseDocumentBuilderData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openHandlerData()}.
	 */
	final HandlerData<GThiz> handlerData = new HandlerData<GThiz>() {

		@Override
		public GThiz closeListenerData() {
			return BaseDocumentBuilderData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
	 */
	final ResolverData<GThiz> resolverData = new ResolverData<GThiz>() {

		@Override
		public GThiz closeResolverData() {
			return BaseDocumentBuilderData.this.thiz();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseDocumentBuilderData<?> data) {
		if (data == null) return this.thiz();
		this.builder = data.builder;
		this.factoryData.use(data.factoryData);
		this.handlerData.use(data.handlerData);
		this.resolverData.use(data.resolverData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt den {@link DocumentBuilder} zurück.<br>
	 * Wenn über {@link #useBuilder(DocumentBuilder)} noch kein {@link DocumentBuilder} gesetzt wurde, wird über
	 * {@link DocumentBuilderFactory#newDocumentBuilder()} ein neuer erstellt, über {@link #useBuilder(DocumentBuilder)} gesetzt und über {@link #updateBuilder()}
	 * aktualisiert. Für die Erstellung wird die {@link DocumentBuilderFactory} genutzt, die in {@link #openFactoryData()} konfiguriert ist.
	 * 
	 * @see #useBuilder(DocumentBuilder)
	 * @see #updateBuilder()
	 * @return {@link DocumentBuilder}.
	 * @throws SAXException Wenn {@link FactoryData#getFactory()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#newDocumentBuilder()} eine entsprechende Ausnahme auslöst.
	 */
	public DocumentBuilder getBuilder() throws SAXException, ParserConfigurationException {
		DocumentBuilder result = this.builder;
		if (result != null) return result;
		result = this.factoryData.getFactory().newDocumentBuilder();
		this.useBuilder(result);
		this.updateBuilder();
		return result;
	}

	/**
	 * Diese Methode setzt den {@link DocumentBuilder} und gibt {@code this} zurück.
	 * 
	 * @param builder {@link DocumentBuilder} oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useBuilder(final DocumentBuilder builder) {
		this.builder = builder;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt den {@link DocumentBuilder} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useBuilder(DocumentBuilder)
	 * @return {@code this}.
	 */
	public GThiz resetBuilder() {
		return this.useBuilder(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen des {@link DocumentBuilder} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf den über {@link #getBuilder()} ermittelten {@link DocumentBuilder} die Einstellungen übertragen, die in
	 * {@link #openHandlerData()} und {@link #openResolverData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #getBuilder()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link #getBuilder()} eine entsprechende Ausnahme auslöst.
	 */
	public GThiz updateBuilder() throws SAXException, ParserConfigurationException {
		final DocumentBuilder builder = this.getBuilder();
		for (final ErrorHandler value: this.handlerData) {
			builder.setErrorHandler(value);
		}
		for (final EntityResolver value: this.resolverData) {
			builder.setEntityResolver(value);
		}
		return this.thiz();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilderFactory} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#newDocumentBuilder()
	 * @return Konfigurator.
	 */
	public FactoryData<GThiz> openFactoryData() {
		return this.factoryData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link ErrorHandler} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @return Konfigurator.
	 */
	public HandlerData<GThiz> openHandlerData() {
		return this.handlerData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link EntityResolver} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @return Konfigurator.
	 */
	public ResolverData<GThiz> openResolverData() {
		return this.resolverData;
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
	 * @see #getBuilder()
	 */
	@Override
	public DocumentBuilder build() throws IllegalStateException {
		try {
			return this.getBuilder();
		} catch (final SAXException | ParserConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.factoryData, this.handlerData, this.resolverData);
	}

}