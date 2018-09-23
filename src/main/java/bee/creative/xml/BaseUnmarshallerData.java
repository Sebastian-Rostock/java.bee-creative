package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapData;
import bee.creative.util.Builders.BaseSetData;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;
import bee.creative.xml.BaseDocumentBuilderFactoryData.SchemaData;

/** Diese Klasse implementiert den Konfigurator für einen {@link Unmarshaller}.
 *
 * @see JAXBContext#createUnmarshaller()
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseUnmarshallerData<GThis> extends BaseBuilder<Unmarshaller, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die {@link JAXBContext}.
	 *
	 * @see JAXBContext#createUnmarshaller()
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ContextData<GOwner> extends BaseContextData<ContextData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeContextData();

		/** {@inheritDoc} */
		@Override
		protected ContextData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das {@link Schema}.
	 *
	 * @see Unmarshaller#setSchema(Schema)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ShemaData<GOwner> extends BaseSchemaData<ShemaData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeShemaData();

		/** {@inheritDoc} */
		@Override
		protected ShemaData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link XmlAdapter}.
	 *
	 * @see Unmarshaller#setAdapter(XmlAdapter)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class AdapterData<GOwner> extends BaseSetData<XmlAdapter<?, ?>, AdapterData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeAdapterData();

		/** {@inheritDoc} */
		@Override
		protected AdapterData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Listener}.
	 *
	 * @see Unmarshaller#setListener(Listener)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ListenerData<GOwner> extends BaseValueBuilder<Listener, ListenerData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeListenerData();

		/** {@inheritDoc} */
		@Override
		protected ListenerData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften.
	 *
	 * @see Unmarshaller#setProperty(String, Object)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapData<String, Object, PropertyData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		/** {@inheritDoc} */
		@Override
		protected PropertyData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ValidationEventHandler}.
	 *
	 * @see Unmarshaller#setEventHandler(ValidationEventHandler)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ValidationData<GOwner> extends BaseValueBuilder<ValidationEventHandler, ValidationData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeValidationData();

		/** {@inheritDoc} */
		@Override
		protected ValidationData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert den {@link Unmarshaller}. */
	Unmarshaller marshaller;

	/** Dieses Feld speichert den Konfigurator für {@link #openContextData()}. */
	final ContextData<GThis> contextData = new ContextData<GThis>() {

		@Override
		public GThis closeContextData() {
			return BaseUnmarshallerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openShemaData()}. */
	final ShemaData<GThis> shemaData = new ShemaData<GThis>() {

		@Override
		public GThis closeShemaData() {
			return BaseUnmarshallerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openAdapterData()}. */
	final AdapterData<GThis> adapterData = new AdapterData<GThis>() {

		@Override
		public GThis closeAdapterData() {
			return BaseUnmarshallerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openListenerData()}. */
	final ListenerData<GThis> listenerData = new ListenerData<GThis>() {

		@Override
		public GThis closeListenerData() {
			return BaseUnmarshallerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}. */
	final PropertyData<GThis> propertyData = new PropertyData<GThis>() {

		@Override
		public GThis closePropertyData() {
			return BaseUnmarshallerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openValidationData()}. */
	final ValidationData<GThis> validationData = new ValidationData<GThis>() {

		@Override
		public GThis closeValidationData() {
			return BaseUnmarshallerData.this.customThis();
		}

	};

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseUnmarshallerData<?> data) {
		if (data == null) return this.customThis();
		this.marshaller = data.marshaller;
		this.shemaData.use(data.shemaData);
		this.adapterData.use(data.adapterData);
		this.listenerData.use(data.listenerData);
		this.propertyData.use(data.propertyData);
		this.validationData.use(data.validationData);
		return this.customThis();
	}

	/** Diese Methode gibt den {@link Unmarshaller} zurück.<br>
	 * Wenn über {@link #useUnmarshaller(Unmarshaller)} noch kein {@link Unmarshaller} gesetzt wurden, werden über {@link JAXBContext#createUnmarshaller()} ein
	 * neuer erstellt und über {@link #useUnmarshaller(Unmarshaller)} gesetzt und über {@link #updateUnmarshaller()} aktualisiert. Der zur Erstellung verwendeten
	 * Kontext kann über {@link #openContextData()} konfiguriert werden.
	 *
	 * @see #useUnmarshaller(Unmarshaller)
	 * @see #updateUnmarshaller()
	 * @return {@link Unmarshaller}.
	 * @throws SAXException Wenn {@link #updateUnmarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link JAXBContext#createUnmarshaller()} eine entsprechende Ausnahme auslöst. */
	public final Unmarshaller getUnmarshaller() throws SAXException, JAXBException {
		Unmarshaller result = this.marshaller;
		if (result != null) return result;
		final JAXBContext context = this.contextData.getContext();
		result = context.createUnmarshaller();
		this.useUnmarshaller(result);
		this.updateUnmarshaller();
		return result;
	}

	/** Diese Methode setzt den {@link Unmarshaller} und gibt {@code this} zurück.
	 *
	 * @param marshaller {@link Unmarshaller} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useUnmarshaller(final Unmarshaller marshaller) {
		this.marshaller = marshaller;
		return this.customThis();
	}

	/** Diese Methode setzt den {@link Unmarshaller} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useUnmarshaller(Unmarshaller)
	 * @return {@code this}. */
	public final GThis resetUnmarshaller() {
		return this.useUnmarshaller(null);
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link Unmarshaller} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf den über {@link #getUnmarshaller()} ermittelten {@link Unmarshaller} die Einstellungen übertragen, die in
	 * {@link #openShemaData()}, {@link #openAdapterData()}, {@link #openListenerData()}, {@link #openPropertyData()} und {@link #openValidationData()}
	 * konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #getUnmarshaller()} oder {@link SchemaData#getSchema()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #getUnmarshaller()}, {@link Unmarshaller#setProperty(String, Object)} oder
	 *         {@link Unmarshaller#setEventHandler(ValidationEventHandler)} eine entsprechende Ausnahme auslöst. */
	public final GThis updateUnmarshaller() throws SAXException, JAXBException {
		final Unmarshaller result = this.getUnmarshaller();
		result.setSchema(this.shemaData.getSchema());
		result.setListener(this.listenerData.get());
		result.setEventHandler(this.validationData.get());
		for (final XmlAdapter<?, ?> entry: this.adapterData) {
			result.setAdapter(entry);
		}
		for (final Entry<String, Object> entry: this.propertyData) {
			result.setProperty(entry.getKey(), entry.getValue());
		}
		return this.customThis();
	}

	/** Diese Methode öffnet den Konfigurator für das Schema und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setSchema(Schema)
	 * @return Konfigurator. */
	public final ShemaData<GThis> openShemaData() {
		return this.shemaData;
	}

	/** Diese Methode öffnet den Konfigurator für die Adapter und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setAdapter(XmlAdapter)
	 * @return Konfigurator. */
	public final AdapterData<GThis> openAdapterData() {
		return this.adapterData;
	}

	/** Diese Methode öffnet den Konfigurator für die Ereignisüberwachung und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setListener(Listener)
	 * @return Konfigurator. */
	public final ListenerData<GThis> openListenerData() {
		return this.listenerData;
	}

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setProperty(String, Object)
	 * @return Konfigurator. */
	public final PropertyData<GThis> openPropertyData() {
		return this.propertyData;
	}

	/** Diese Methode öffnet den Konfigurator für die Validationsüberwachung und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setEventHandler(ValidationEventHandler)
	 * @return Konfigurator. */
	public final ValidationData<GThis> openValidationData() {
		return this.validationData;
	}

	/** Diese Methode öffnet den Konfigurator für den Kontext und gibt ihn zurück.
	 *
	 * @see JAXBContext#createUnmarshaller()
	 * @return Konfigurator. */
	public final ContextData<GThis> openContextData() {
		return this.contextData;
	}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getUnmarshaller() */
	@Override
	public final Unmarshaller get() throws IllegalStateException {
		try {
			return this.getUnmarshaller();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		} catch (final JAXBException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.shemaData, this.adapterData, this.listenerData, this.propertyData, this.validationData);
	}

}