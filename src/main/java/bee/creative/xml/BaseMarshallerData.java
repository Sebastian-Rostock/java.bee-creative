package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseSetBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;
import bee.creative.xml.BaseDocumentBuilderFactoryData.SchemaData;

/**
 * Diese Klasse implementiert den Konfigurator für einen {@link Marshaller}.
 * 
 * @see JAXBContext#createMarshaller()
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseMarshallerData<GThis> extends BaseBuilder<Marshaller, GThis> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link JAXBContext}.
	 * 
	 * @see JAXBContext#createMarshaller()
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ContextData<GOwner> extends BaseContextData<ContextData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeContextData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ContextData<GOwner> _this_() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für das {@link Schema}.
	 * 
	 * @see Marshaller#setSchema(Schema)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ShemaData<GOwner> extends BaseSchemaData<ShemaData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeShemaData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ShemaData<GOwner> _this_() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link XmlAdapter}.
	 * 
	 * @see Marshaller#setAdapter(XmlAdapter)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class AdapterData<GOwner> extends BaseSetBuilder<XmlAdapter<?, ?>, AdapterData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeAdapterData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected AdapterData<GOwner> _this_() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link Listener}.
	 * 
	 * @see Marshaller#setListener(Listener)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ListenerData<GOwner> extends BaseValueBuilder<Listener, ListenerData<GOwner>> {

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
		protected ListenerData<GOwner> _this_() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Eigenschaften.
	 * 
	 * @see Marshaller#setProperty(String, Object)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder<String, Object, PropertyData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closePropertyData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected PropertyData<GOwner> _this_() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link ValidationEventHandler}.
	 * 
	 * @see Marshaller#setEventHandler(ValidationEventHandler)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class ValidationData<GOwner> extends BaseValueBuilder<ValidationEventHandler, ValidationData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeValidationData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ValidationData<GOwner> _this_() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@link Marshaller}.
	 */
	Marshaller _marshaller_;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openContextData()}.
	 */
	final ContextData<GThis> _contextData_ = new ContextData<GThis>() {

		@Override
		public GThis closeContextData() {
			return BaseMarshallerData.this._this_();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openShemaData()}.
	 */
	final ShemaData<GThis> _shemaData_ = new ShemaData<GThis>() {

		@Override
		public GThis closeShemaData() {
			return BaseMarshallerData.this._this_();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openAdapterData()}.
	 */
	final AdapterData<GThis> _adapterData_ = new AdapterData<GThis>() {

		@Override
		public GThis closeAdapterData() {
			return BaseMarshallerData.this._this_();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openListenerData()}.
	 */
	final ListenerData<GThis> _listenerData_ = new ListenerData<GThis>() {

		@Override
		public GThis closeListenerData() {
			return BaseMarshallerData.this._this_();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}.
	 */
	final PropertyData<GThis> _propertyData_ = new PropertyData<GThis>() {

		@Override
		public GThis closePropertyData() {
			return BaseMarshallerData.this._this_();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openValidationData()}.
	 */
	final ValidationData<GThis> _validationData_ = new ValidationData<GThis>() {

		@Override
		public GThis closeValidationData() {
			return BaseMarshallerData.this._this_();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThis use(final BaseMarshallerData<?> data) {
		if (data == null) return this._this_();
		this._marshaller_ = data._marshaller_;
		this._shemaData_.use(data._shemaData_);
		this._adapterData_.use(data._adapterData_);
		this._listenerData_.use(data._listenerData_);
		this._propertyData_.use(data._propertyData_);
		this._validationData_.use(data._validationData_);
		return this._this_();
	}

	/**
	 * Diese Methode gibt den {@link Marshaller} zurück.<br>
	 * Wenn über {@link #useMarshaller(Marshaller)} noch kein {@link Marshaller} gesetzt wurden, werden über {@link JAXBContext#createMarshaller()} ein neuer
	 * erstellt und über {@link #useMarshaller(Marshaller)} gesetzt und über {@link #updateMarshaller()} aktualisiert. Der zur Erstellung verwendeten Kontext kann
	 * über {@link #openContextData()} konfiguriert werden.
	 * 
	 * @see #useMarshaller(Marshaller)
	 * @see #updateMarshaller()
	 * @return {@link Marshaller}.
	 * @throws SAXException Wenn {@link #updateMarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link JAXBContext#createMarshaller()} eine entsprechende Ausnahme auslöst.
	 */
	public final Marshaller getMarshaller() throws SAXException, JAXBException {
		Marshaller result = this._marshaller_;
		if (result != null) return result;
		final JAXBContext context = this._contextData_.getContext();
		result = context.createMarshaller();
		this.useMarshaller(result);
		this.updateMarshaller();
		return result;
	}

	/**
	 * Diese Methode setzt den {@link Marshaller} und gibt {@code this} zurück.
	 * 
	 * @param marshaller {@link Marshaller} oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThis useMarshaller(final Marshaller marshaller) {
		this._marshaller_ = marshaller;
		return this._this_();
	}

	/**
	 * Diese Methode setzt den {@link Marshaller} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useMarshaller(Marshaller)
	 * @return {@code this}.
	 */
	public final GThis resetMarshaller() {
		return this.useMarshaller(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen des {@link Marshaller} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf den über {@link #getMarshaller()} ermittelten {@link Marshaller} die Einstellungen übertragen, die in
	 * {@link #openShemaData()}, {@link #openAdapterData()}, {@link #openListenerData()}, {@link #openPropertyData()} und {@link #openValidationData()}
	 * konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #getMarshaller()} oder {@link SchemaData#getSchema()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #getMarshaller()}, {@link Marshaller#setProperty(String, Object)} oder
	 *         {@link Marshaller#setEventHandler(ValidationEventHandler)} eine entsprechende Ausnahme auslöst.
	 */
	public final GThis updateMarshaller() throws SAXException, JAXBException {
		final Marshaller result = this.getMarshaller();
		result.setSchema(this._shemaData_.getSchema());
		result.setListener(this._listenerData_.get());
		result.setEventHandler(this._validationData_.get());
		for (final XmlAdapter<?, ?> entry: this._adapterData_) {
			result.setAdapter(entry);
		}
		for (final Entry<String, Object> entry: this._propertyData_) {
			result.setProperty(entry.getKey(), entry.getValue());
		}
		return this._this_();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für das Schema und gibt ihn zurück.
	 * 
	 * @see Marshaller#setSchema(Schema)
	 * @return Konfigurator.
	 */
	public final ShemaData<GThis> openShemaData() {
		return this._shemaData_;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Adapter und gibt ihn zurück.
	 * 
	 * @see Marshaller#setAdapter(XmlAdapter)
	 * @return Konfigurator.
	 */
	public final AdapterData<GThis> openAdapterData() {
		return this._adapterData_;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Ereignisüberwachung und gibt ihn zurück.
	 * 
	 * @see Marshaller#setListener(Listener)
	 * @return Konfigurator.
	 */
	public final ListenerData<GThis> openListenerData() {
		return this._listenerData_;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 * 
	 * @see Marshaller#setProperty(String, Object)
	 * @return Konfigurator.
	 */
	public final PropertyData<GThis> openPropertyData() {
		return this._propertyData_;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Validationsüberwachung und gibt ihn zurück.
	 * 
	 * @see Marshaller#setEventHandler(ValidationEventHandler)
	 * @return Konfigurator.
	 */
	public final ValidationData<GThis> openValidationData() {
		return this._validationData_;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den Kontext und gibt ihn zurück.
	 * 
	 * @see JAXBContext#createMarshaller()
	 * @return Konfigurator.
	 */
	public final ContextData<GThis> openContextData() {
		return this._contextData_;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThis _this_();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getMarshaller()
	 */
	@Override
	public final Marshaller build() throws IllegalStateException {
		try {
			return this.getMarshaller();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		} catch (final JAXBException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._shemaData_, this._adapterData_, this._listenerData_, this._propertyData_, this._validationData_);
	}

}