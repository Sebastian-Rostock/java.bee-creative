package bee.creative.xml;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.HashMap;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseSetBuilder2;
import bee.creative.xml.DocumentBuilderFactoryBuilder.SchemaData;

/** Diese Klasse implementiert den Konfigurator für einen {@link Marshaller}.
 *
 * @see JAXBContext#createMarshaller()
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseMarshallerData<GOwner> extends BaseBuilder<Marshaller, GOwner> {

	/** Diese Klasse implementiert den Konfigurator für das {@link Schema}.
	 *
	 * @see Marshaller#setSchema(Schema)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ShemaData<GOwner> extends SchemaBuilder<ShemaData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeShemaData();

		@Override
		public ShemaData<GOwner> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link XmlAdapter}.
	 *
	 * @see Marshaller#setAdapter(XmlAdapter)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class AdapterData<GOwner> extends BaseSetBuilder<XmlAdapter<?, ?>, AdapterData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeAdapterData();

		@Override
		public AdapterData<GOwner> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Listener}.
	 *
	 * @see Marshaller#setListener(Listener)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ListenerData<GOwner> extends BaseValueBuilder<Listener, ListenerData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeListenerData();

		@Override
		public ListenerData<GOwner> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften.
	 *
	 * @see Marshaller#setProperty(String, Object)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder<String, Object, PropertyData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		@Override
		public PropertyData<GOwner> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ValidationEventHandler}.
	 *
	 * @see Marshaller#setEventHandler(ValidationEventHandler)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ValidationData<GOwner> extends BaseValueBuilder<ValidationEventHandler, ValidationData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeValidationData();

		@Override
		public ValidationData<GOwner> owner() {
			return this;
		}

	}

 

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GOwner use(final BaseMarshallerData<?> data) {
		if (data == null) return this.owner();
		this.marshaller = data.marshaller;
		this.shemaData.use(data.shemaData);
		this.adapterData.use(data.adapterData);
		this.listenerData.use(data.listenerData);
		this.propertyData.useString(data.propertyData);
		this.validationData.use(data.validationData);
		return this.owner();
	}

	/** Diese Methode gibt den {@link Marshaller} zurück. Wenn über {@link #useMarshaller(Marshaller)} noch kein {@link Marshaller} gesetzt wurden, werden über
	 * {@link JAXBContext#createMarshaller()} ein neuer erstellt und über {@link #useMarshaller(Marshaller)} gesetzt und über {@link #updateMarshaller()}
	 * aktualisiert. Der zur Erstellung verwendeten Kontext kann über {@link #openContextData()} konfiguriert werden.
	 *
	 * @see #useMarshaller(Marshaller)
	 * @see #updateMarshaller()
	 * @return {@link Marshaller}.
	 * @throws SAXException Wenn {@link #updateMarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link JAXBContext#createMarshaller()} eine entsprechende Ausnahme auslöst. */
	public final Marshaller getMarshaller() throws SAXException, JAXBException {
		Marshaller result = this.marshaller;
		if (result != null) return result;
		final JAXBContext context = this._contextData_.putContext();
		result = context.createMarshaller();
		this.useMarshaller(result);
		this.updateMarshaller();
		return result;
	}

	/** Diese Methode setzt den {@link Marshaller} und gibt {@code this} zurück.
	 *
	 * @param marshaller {@link Marshaller} oder {@code null}.
	 * @return {@code this}. */
	public final GOwner useMarshaller(final Marshaller marshaller) {
		this.marshaller = marshaller;
		return this.owner();
	}

	/** Diese Methode setzt den {@link Marshaller} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useMarshaller(Marshaller)
	 * @return {@code this}. */
	public final GOwner resetMarshaller() {
		return this.useMarshaller(null);
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link Marshaller} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #getMarshaller()} ermittelten {@link Marshaller} die Einstellungen übertragen, die in {@link #openShemaData()}, {@link #openAdapterData()},
	 * {@link #openListenerData()}, {@link #openPropertyData()} und {@link #openValidationData()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #getMarshaller()} oder {@link SchemaData#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #getMarshaller()}, {@link Marshaller#setProperty(String, Object)} oder
	 *         {@link Marshaller#setEventHandler(ValidationEventHandler)} eine entsprechende Ausnahme auslöst. */
	public final GOwner updateMarshaller() throws SAXException, JAXBException {
		final Marshaller result = this.getMarshaller();
		result.setSchema(this.shemaData.putValue());
		result.setListener(this.listenerData.get());
		result.setEventHandler(this.validationData.get());
		for (final XmlAdapter<?, ?> entry: this.adapterData) {
			result.setAdapter(entry);
		}
		for (final Entry<String, Object> entry: this.propertyData) {
			result.setProperty(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für das Schema und gibt ihn zurück.
	 *
	 * @see Marshaller#setSchema(Schema)
	 * @return Konfigurator. */
	public final ShemaData<GOwner> openShemaData() {
		return this.shemaData;
	}

	/** Diese Methode öffnet den Konfigurator für die Adapter und gibt ihn zurück.
	 *
	 * @see Marshaller#setAdapter(XmlAdapter)
	 * @return Konfigurator. */
	public final AdapterData<GOwner> openAdapterData() {
		return this.adapterData;
	}

	/** Diese Methode öffnet den Konfigurator für die Ereignisüberwachung und gibt ihn zurück.
	 *
	 * @see Marshaller#setListener(Listener)
	 * @return Konfigurator. */
	public final ListenerData<GOwner> openListenerData() {
		return this.listenerData;
	}

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 *
	 * @see Marshaller#setProperty(String, Object)
	 * @return Konfigurator. */
	public final PropertyData<GOwner> openPropertyData() {
		return this.propertyData;
	}

	/** Diese Methode öffnet den Konfigurator für die Validationsüberwachung und gibt ihn zurück.
	 *
	 * @see Marshaller#setEventHandler(ValidationEventHandler)
	 * @return Konfigurator. */
	public final ValidationData<GOwner> openValidationData() {
		return this.validationData;
	}

	/** Diese Methode öffnet den Konfigurator für den Kontext und gibt ihn zurück.
	 *
	 * @see JAXBContext#createMarshaller()
	 * @return Konfigurator. */
	public final ContextBuilder<GOwner> openContextData() {
		return new ContextBuilder<GOwner>() {

			@Override
			public JAXBContext get() {
				return null;
			}

			@Override
			public void set(JAXBContext value) {
			}

			@Override
			public ClassesValue<GOwner> classes() {
				return null;
			}

			@Override
			public PropertiesValue<GOwner> properties() {
				return null;
			}

			@Override
			public GOwner owner() {
				return null;
			}

		};
	}

	@Override
	public abstract GOwner owner();

	/** {@inheritDoc}
	 *
	 * @see #getMarshaller() */
	@Override
	public final Marshaller get() throws IllegalStateException {
		try {
			return this.getMarshaller();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		} catch (final JAXBException cause) {
			throw new IllegalStateException(cause);
		}
	}

	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.shemaData, this.adapterData, this.listenerData, this.propertyData, this.validationData);
	}

}