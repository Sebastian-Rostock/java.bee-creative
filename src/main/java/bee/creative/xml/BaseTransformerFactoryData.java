package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link TransformerFactory} zur Erzeugung von {@link Templates} oder eines
 * {@link Transformer}.
 * 
 * @see TransformerFactory#newTemplates(Source)
 * @see TransformerFactory#newTransformer()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseTransformerFactoryData<GThiz> extends BaseBuilder<TransformerFactory, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link TransformerFactory}.
	 * 
	 * @see TransformerFactory#setFeature(String, boolean)
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
		protected final FeatureData<GOwner> __this() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Attribute einer {@link TransformerFactory}.
	 * 
	 * @see TransformerFactory#setAttribute(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class AttributeData<GOwner> extends BaseMapBuilder<String, String, AttributeData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeAttributeData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final AttributeData<GOwner> __this() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link ErrorListener}.
	 * 
	 * @see TransformerFactory#setErrorListener(ErrorListener)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ListenerData<GOwner> extends BaseValueBuilder<ErrorListener, ListenerData<GOwner>> {

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
		protected final ListenerData<GOwner> __this() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link URIResolver}.
	 * 
	 * @see TransformerFactory#setURIResolver(URIResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ResolverData<GOwner> extends BaseValueBuilder<URIResolver, ResolverData<GOwner>> {

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
		protected final ResolverData<GOwner> __this() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link TransformerFactory}.
	 */
	TransformerFactory __factory;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
	 */
	final FeatureData<GThiz> __featureData = new FeatureData<GThiz>() {

		@Override
		public final GThiz closeFeatureData() {
			return BaseTransformerFactoryData.this.__this();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}.
	 */
	final AttributeData<GThiz> __attributeData = new AttributeData<GThiz>() {

		@Override
		public final GThiz closeAttributeData() {
			return BaseTransformerFactoryData.this.__this();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openListenerData()}.
	 */
	final ListenerData<GThiz> __listenerData = new ListenerData<GThiz>() {

		@Override
		public final GThiz closeListenerData() {
			return BaseTransformerFactoryData.this.__this();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
	 */
	final ResolverData<GThiz> __resolverData = new ResolverData<GThiz>() {

		@Override
		public final GThiz closeResolverData() {
			return BaseTransformerFactoryData.this.__this();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz use(final BaseTransformerFactoryData<?> data) {
		if (data == null) return this.__this();
		this.__factory = data.__factory;
		this.__featureData.use(data.__featureData);
		this.__attributeData.use(data.__attributeData);
		this.__listenerData.use(data.__listenerData);
		this.__resolverData.use(data.__resolverData);
		return this.__this();
	}

	/**
	 * Diese Methode gibt die {@link TransformerFactory} zurück.<br>
	 * Wenn über {@link #useFactory(TransformerFactory)} noch keine {@link TransformerFactory} gesetzt wurde, wird über {@link TransformerFactory#newInstance()}
	 * eine neue erstellt, über {@link #useFactory(TransformerFactory)} gesetzt und über {@link #updateFactory()} aktualisiert.
	 * 
	 * @see #useFactory(TransformerFactory)
	 * @see #updateFactory()
	 * @return {@link TransformerFactory}.
	 * @throws TransformerConfigurationException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 */
	public final TransformerFactory getFactory() throws TransformerConfigurationException {
		TransformerFactory result = this.__factory;
		if (result != null) return result;
		result = TransformerFactory.newInstance();
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/**
	 * Diese Methode setzt die {@link TransformerFactory} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link TransformerFactory} oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useFactory(final TransformerFactory factory) {
		this.__factory = factory;
		return this.__this();
	}

	/**
	 * Diese Methode setzt die {@link TransformerFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(TransformerFactory)
	 * @return {@code this}.
	 */
	public final GThiz resetFactory() {
		return this.useFactory(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen der {@link TransformerFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link TransformerFactory} die Einstellungen übertragen, die in
	 * {@link #openListenerData()}, {@link #openResolverData()}, {@link #openFeatureData()} und {@link #openAttributeData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws TransformerConfigurationException Wenn {@link TransformerFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
	 */
	public final GThiz updateFactory() throws TransformerConfigurationException {
		final TransformerFactory factory = this.getFactory();
		for (final URIResolver value: this.__resolverData) {
			factory.setURIResolver(value);
		}
		for (final ErrorListener value: this.__listenerData) {
			factory.setErrorListener(value);
		}
		for (final Entry<String, Boolean> entry: this.__featureData) {
			factory.setFeature(entry.getKey(), entry.getValue().booleanValue());
		}
		for (final Entry<String, String> entry: this.__attributeData) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.__this();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setFeature(String, boolean)
	 * @return Konfigurator.
	 */
	public final FeatureData<GThiz> openFeatureData() {
		return this.__featureData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Attribute der und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setAttribute(String, Object)
	 * @return Konfigurator.
	 */
	public final AttributeData<GThiz> openAttributeData() {
		return this.__attributeData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link ErrorListener} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setErrorListener(ErrorListener)
	 * @return Konfigurator.
	 */
	public final ListenerData<GThiz> openListenerData() {
		return this.__listenerData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link URIResolver} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setURIResolver(URIResolver)
	 * @return Konfigurator.
	 */
	public final ResolverData<GThiz> openResolverData() {
		return this.__resolverData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz __this();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getFactory()
	 */
	@Override
	public final TransformerFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final TransformerConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__featureData, this.__attributeData, this.__listenerData, this.__resolverData);
	}

}