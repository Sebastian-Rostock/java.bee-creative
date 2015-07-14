package bee.creative.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen Konfigurator für einen {@link DocumentBuilder} zur Erzeugung eines {@link Document}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseDocumentBuilderData<GThiz> extends BaseBuilder<DocumentBuilder, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link DocumentBuilderFactory} zur Erzeugung eines {@link DocumentBuilder}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class FactoryData extends BaseDocumentBuilderFactoryData<FactoryData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeFactoryData() {
			return BaseDocumentBuilderData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FactoryData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler} einer {@link DocumentBuilder}.
	 * 
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class HandlerData extends BaseValueBuilder<ErrorHandler, HandlerData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeListenerData() {
			return BaseDocumentBuilderData.this.thiz();
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
	 * Diese Klasse implementiert den Konfigurator für den {@link EntityResolver} einer {@link DocumentBuilder}.
	 * 
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class ResolverData extends BaseValueBuilder<EntityResolver, ResolverData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeResolverData() {
			return BaseDocumentBuilderData.this.thiz();
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

	{}

	/**
	 * Dieses Feld speichert die {@link DocumentBuilderFactory}.
	 */
	DocumentBuilder builder;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
	 */
	final FactoryData factoryData = //
		new FactoryData();

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
	 * Diese Methode gibt die {@link DocumentBuilder} zurück.<br>
	 * Wenn über {@link #useBuilder(DocumentBuilder)} noch keine {@link DocumentBuilder} gesetzt wurde, wird über
	 * {@link DocumentBuilderFactory#newDocumentBuilder()} eine neue erstellt, über {@link #useBuilder(DocumentBuilder)} gesetzt und über {@link #updateBuilder()}
	 * aktualisiert.
	 * 
	 * @see #useBuilder(DocumentBuilder)
	 * @see #updateBuilder()
	 * @return {@link DocumentBuilder}.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#newDocumentBuilder()} eine entsprechende Ausnahme auslöst.
	 */
	public DocumentBuilder getBuilder() throws ParserConfigurationException {
		DocumentBuilder result = this.builder;
		if (result != null) return result;
		result = this.factoryData.build().newDocumentBuilder();
		this.useBuilder(result);
		this.updateBuilder();
		return result;
	}

	/**
	 * Diese Methode setzt die {@link DocumentBuilder} und gibt {@code this} zurück.
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
	 * Bei dieser Aktualisierung werden auf den über {@link #getBuilder()} ermittelte {@link DocumentBuilder} die Einstellungen übertragen, die in
	 * {@link #openHandlerData()} und {@link #openResolverData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws ParserConfigurationException Wenn {@link #getBuilder()} eine entsprechende Ausnahme auslöst.
	 */
	public GThiz updateBuilder() throws ParserConfigurationException {
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
	 * @return Konfigurator.
	 */
	public FactoryData openFactoryData() {
		return this.factoryData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilder#setErrorHandler(ErrorHandler) Fehlerbehandlung} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
	 * @return Konfigurator.
	 */
	public HandlerData openHandlerData() {
		return this.handlerData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilder#setEntityResolver(EntityResolver) URL-Auflöser} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilder#setEntityResolver(EntityResolver)
	 * @return Konfigurator.
	 */
	public ResolverData openResolverData() {
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
	 */
	@Override
	public DocumentBuilder build() throws IllegalStateException {
		try {
			return this.getBuilder();
		} catch (final ParserConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.factoryData, this.handlerData, this.resolverData);
	}

}