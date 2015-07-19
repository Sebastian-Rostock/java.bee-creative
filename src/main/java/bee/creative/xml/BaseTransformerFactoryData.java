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
		protected FeatureData<GOwner> thiz() {
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
		protected AttributeData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link ErrorListener} einer {@link TransformerFactory}.
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
		protected ListenerData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link URIResolver} einer {@link TransformerFactory}.
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
		protected ResolverData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link TransformerFactory}.
	 */
	TransformerFactory factory;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
	 */
	final FeatureData<GThiz> featureData = new FeatureData<GThiz>() {

		@Override
		public GThiz closeFeatureData() {
			return BaseTransformerFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}.
	 */
	final AttributeData<GThiz> attributeData = new AttributeData<GThiz>() {

		@Override
		public GThiz closeAttributeData() {
			return BaseTransformerFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openListenerData()}.
	 */
	final ListenerData<GThiz> listenerData = new ListenerData<GThiz>() {

		@Override
		public GThiz closeListenerData() {
			return BaseTransformerFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
	 */
	final ResolverData<GThiz> resolverData = new ResolverData<GThiz>() {

		@Override
		public GThiz closeResolverData() {
			return BaseTransformerFactoryData.this.thiz();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseTransformerFactoryData<?> data) {
		if (data == null) return this.thiz();
		this.factory = data.factory;
		this.featureData.use(data.featureData);
		this.attributeData.use(data.attributeData);
		this.listenerData.use(data.listenerData);
		this.resolverData.use(data.resolverData);
		return this.thiz();
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
	public TransformerFactory getFactory() throws TransformerConfigurationException {
		TransformerFactory result = this.factory;
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
	public GThiz useFactory(final TransformerFactory factory) {
		this.factory = factory;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt die {@link TransformerFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(TransformerFactory)
	 * @return {@code this}.
	 */
	public GThiz resetFactory() {
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
	public GThiz updateFactory() throws TransformerConfigurationException {
		final TransformerFactory factory = this.getFactory();
		for (final URIResolver value: this.resolverData) {
			factory.setURIResolver(value);
		}
		for (final ErrorListener value: this.listenerData) {
			factory.setErrorListener(value);
		}
		for (final Entry<String, Boolean> entry: this.featureData) {
			factory.setFeature(entry.getKey(), entry.getValue().booleanValue());
		}
		for (final Entry<String, String> entry: this.attributeData) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.thiz();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setFeature(String, boolean)
	 * @return Konfigurator.
	 */
	public FeatureData<GThiz> openFeatureData() {
		return this.featureData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Attribute der und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setAttribute(String, Object)
	 * @return Konfigurator.
	 */
	public AttributeData<GThiz> openAttributeData() {
		return this.attributeData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link ErrorListener} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setErrorListener(ErrorListener)
	 * @return Konfigurator.
	 */
	public ListenerData<GThiz> openListenerData() {
		return this.listenerData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link URIResolver} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#setURIResolver(URIResolver)
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
	 * @see #getFactory()
	 */
	@Override
	public TransformerFactory build() throws IllegalStateException {
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
	public String toString() {
		return Objects.toStringCall(this, this.featureData, this.attributeData, this.listenerData, this.resolverData);
	}

}