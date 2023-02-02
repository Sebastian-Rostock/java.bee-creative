package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.xml.ContextBuilder.PropertiesBuilder;
import bee.creative.xml.ContextBuilder.PropertiesValue;
import bee.creative.xml.UnmarshallerBuilder.AdaptersBuilder;
import bee.creative.xml.UnmarshallerBuilder.AdaptersValue;
import bee.creative.xml.UnmarshallerBuilder.ContextValue;
import bee.creative.xml.UnmarshallerBuilder.SchemaValue;
import bee.creative.xml.UnmarshallerBuilder.ValidationBuilder;
import bee.creative.xml.UnmarshallerBuilder.ValidationValue;

/** Diese Klasse implementiert den Konfigurator für einen {@link Marshaller}.
 *
 * @see JAXBContext#createMarshaller()
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class MarshallerBuilder<GOwner> extends BaseValueBuilder<Marshaller, GOwner> {

	public static abstract class Value<GOwner> extends MarshallerBuilder<GOwner> {

		Marshaller value;

		SchemaValue schema = new SchemaValue();

		ContextValue context = new ContextValue();

		ListenerValue listener = new ListenerValue();

		ValidationValue validation = new ValidationValue();

		AdaptersValue adapters = new AdaptersValue();

		PropertiesValue properties = new PropertiesValue();

		@Override
		public Marshaller get() {
			return this.value;
		}

		@Override
		public void set(final Marshaller value) {
			this.value = value;
		}

		@Override
		public SchemaValue schema() {
			return this.schema;
		}

		@Override
		public ContextValue context() {
			return this.context;
		}

		@Override
		public ListenerValue listener() {
			return this.listener;
		}

		@Override
		public ValidationValue validation() {
			return this.validation;
		}

		@Override
		public AdaptersValue adapters() {
			return this.adapters;
		}

		@Override
		public PropertiesValue properties() {
			return this.properties;
		}

	}

	public static abstract class Proxy<GOwner> extends MarshallerBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Marshaller get() {
			return this.value().get();
		}

		@Override
		public void set(final Marshaller value) {
			this.value().set(value);
		}

		@Override
		public SchemaValue schema() {
			return this.value().schema();
		}

		@Override
		public ContextValue context() {
			return this.value().context();
		}

		@Override
		public ListenerValue listener() {
			return this.value().listener();
		}

		@Override
		public ValidationValue validation() {
			return this.value().validation();
		}

		@Override
		public AdaptersValue adapters() {
			return this.value().adapters();
		}

		@Override
		public PropertiesValue properties() {
			return this.value().properties();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das {@link Schema}. */
	public class SchemaProxy extends SchemaBuilder.Proxy<GOwner> {

		@Override
		protected SchemaValue value() {
			return MarshallerBuilder.this.schema();
		}

		@Override
		public GOwner owner() {
			return MarshallerBuilder.this.owner();
		}

	}

	public class ContextProxy extends ContextBuilder.Proxy<GOwner> {

		@Override
		protected ContextValue value() {
			return MarshallerBuilder.this.context();
		}

		@Override
		public GOwner owner() {
			return MarshallerBuilder.this.owner();
		}

	}

	public class AdaptersProxy extends AdaptersBuilder<GOwner> {

		@Override
		public Set<XmlAdapter<?, ?>> get() {
			return MarshallerBuilder.this.adapters().get();
		}

		@Override
		public GOwner owner() {
			return MarshallerBuilder.this.owner();
		}

	}

	// TODO
	public static class ListenerValue extends ListenerBuilder<ListenerValue> {

		Listener value;

		@Override
		public Listener get() {
			return this.value;
		}

		@Override
		public void set(final Listener value) {
			this.value = value;
		}

		@Override
		public ListenerValue owner() {
			return this;
		}

	}

	public class ListenerProxy extends ListenerBuilder<GOwner> {

		@Override
		public Listener get() {
			return MarshallerBuilder.this.listener().get();
		}

		@Override
		public void set(final Listener value) {
			MarshallerBuilder.this.listener().set(value);
		}

		@Override
		public GOwner owner() {
			return MarshallerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Listener}.
	 *
	 * @see Marshaller#setListener(Listener)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ListenerBuilder<GOwner> extends BaseValueBuilder<Listener, GOwner> {

	}

	public class PropertiesProxy extends PropertiesBuilder<GOwner> {

		@Override
		public Map<String, Object> get() {
			return MarshallerBuilder.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return MarshallerBuilder.this.owner();
		}

	}

	public class ValidationProxy extends ValidationBuilder<GOwner> {

		@Override
		public ValidationEventHandler get() {
			return MarshallerBuilder.this.validation().get();
		}

		@Override
		public void set(final ValidationEventHandler value) {
			MarshallerBuilder.this.validation().set(value);
		}

		@Override
		public GOwner owner() {
			return MarshallerBuilder.this.owner();
		}

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final MarshallerBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forSchema().use(that.schema());
		this.forContext().use(that.context());
		this.forListener().use(that.listener());
		this.forValidation().use(that.validation());
		this.forAdapters().use(that.adapters());
		this.forProperties().use(that.properties());
		return this.owner();
	}

	/** Diese Methode gibt den {@link Marshaller} zurück. Wenn über {@link #useValue(Object)} noch kein {@link Marshaller} gesetzt wurden, werden über
	 * {@link JAXBContext#createMarshaller()} ein neuer erstellt und über {@link #useValue(Object)} gesetzt und über {@link #updateValue()} aktualisiert. Der zur
	 * Erstellung verwendeten Kontext kann über {@link #context()} konfiguriert werden.
	 *
	 * @see #updateValue()
	 * @return {@link Marshaller}.
	 * @throws SAXException Wenn {@link #updateValue()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link JAXBContext#createMarshaller()} eine entsprechende Ausnahme auslöst. */
	public Marshaller putValue() throws SAXException, JAXBException {
		Marshaller res = this.getValue();
		if (res != null) return res;
		res = this.context().putValue().createMarshaller();
		this.useValue(res);
		this.updateValue();
		return res;
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link Marshaller} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #putValue()} ermittelten {@link Marshaller} die Einstellungen übertragen, die in {@link #schema()}, {@link #adapters()}, {@link #listener()},
	 * {@link #properties()} und {@link #validation()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #putValue()} oder {@link SchemaBuilder#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #putValue()}, {@link Marshaller#setProperty(String, Object)} oder
	 *         {@link Marshaller#setEventHandler(ValidationEventHandler)} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws SAXException, JAXBException {
		final Marshaller res = this.putValue();
		res.setSchema(this.schema().putValue());
		res.setListener(this.listener().get());
		res.setEventHandler(this.validation().get());
		for (final XmlAdapter<?, ?> entry: this.adapters()) {
			res.setAdapter(entry);
		}
		for (final Entry<String, Object> entry: this.properties()) {
			res.setProperty(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für das Schema und gibt ihn zurück.
	 *
	 * @see Marshaller#setSchema(Schema)
	 * @return Konfigurator. */
	public abstract SchemaValue schema();

	/** Diese Methode öffnet den Konfigurator für den Kontext und gibt ihn zurück.
	 *
	 * @see JAXBContext#createMarshaller()
	 * @return Konfigurator. */
	public abstract ContextValue context();

	/** Diese Methode öffnet den Konfigurator für die Ereignisüberwachung und gibt ihn zurück.
	 *
	 * @see Marshaller#setListener(Listener)
	 * @return Konfigurator. */
	public abstract ListenerValue listener();

	/** Diese Methode öffnet den Konfigurator für die Validationsüberwachung und gibt ihn zurück.
	 *
	 * @see Marshaller#setEventHandler(ValidationEventHandler)
	 * @return Konfigurator. */
	public abstract ValidationValue validation();

	/** Diese Methode öffnet den Konfigurator für die Adapter und gibt ihn zurück.
	 *
	 * @see Marshaller#setAdapter(XmlAdapter)
	 * @return Konfigurator. */
	public abstract AdaptersValue adapters();

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 *
	 * @see Marshaller#setProperty(String, Object)
	 * @return Konfigurator. */
	public abstract PropertiesValue properties();

	public SchemaProxy forSchema() {
		return new SchemaProxy();
	}

	public ContextProxy forContext() {
		return new ContextProxy();
	}

	public ListenerProxy forListener() {
		return new ListenerProxy();
	}

	public ValidationProxy forValidation() {
		return new ValidationProxy();
	}

	public AdaptersProxy forAdapters() {
		return new AdaptersProxy();
	}

	public PropertiesProxy forProperties() {
		return new PropertiesProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.schema(), this.context(), this.listener(), this.validation(), this.adapters(), this.properties());
	}

}