package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseSetBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.HashMap;
import bee.creative.util.HashSet;

/** Diese Klasse implementiert den Konfigurator für einen {@link Unmarshaller}.
 *
 * @see JAXBContext#createUnmarshaller()
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class UnmarshallerBuilder<GOwner> extends BaseValueBuilder<Unmarshaller, GOwner> {

	/** Diese Klasse implementiert den Konfigurator für den {@link Unmarshaller}. */
	public static abstract class Value<GOwner> extends UnmarshallerBuilder<GOwner> {

		Unmarshaller value;

		ShemaValue shema = new ShemaValue();

		AdaptersValue adapters = new AdaptersValue();

		ListenerValue listener = new ListenerValue();

		PropertiesValue properties = new PropertiesValue();

		ValidationValue validation = new ValidationValue();

		ContextValue context = new ContextValue();

		@Override
		public Unmarshaller get() {
			return this.value;
		}

		@Override
		public void set(final Unmarshaller value) {
			this.value = value;
		}

		@Override
		public ShemaValue shema() {
			return this.shema;
		}

		@Override
		public AdaptersValue adapters() {
			return this.adapters;
		}

		@Override
		public ListenerValue listener() {
			return this.listener;
		}

		@Override
		public PropertiesValue properties() {
			return this.properties;
		}

		@Override
		public ValidationValue validation() {
			return this.validation;
		}

		@Override
		public ContextValue context() {
			return this.context;
		}

	}

	public static abstract class Proxy<GOwner> extends UnmarshallerBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Unmarshaller get() {
			return this.value().get();
		}

		@Override
		public void set(final Unmarshaller value) {
			this.value().set(value);
		}

		@Override
		public ShemaValue shema() {
			return this.value().shema();
		}

		@Override
		public AdaptersValue adapters() {
			return this.value().adapters();
		}

		@Override
		public ListenerValue listener() {
			return this.value().listener();
		}

		@Override
		public PropertiesValue properties() {
			return this.value().properties();
		}

		@Override
		public ValidationValue validation() {
			return this.value().validation();
		}

		@Override
		public ContextValue context() {
			return this.value().context();
		}

	}

	public static class ContextValue extends ContextBuilder.Value<ContextValue> {

		@Override
		public ContextValue owner() {
			return this;
		}

	}

	public class ContextProxy extends ContextBuilder.Proxy<GOwner> {

		@Override
		protected ContextValue value() {
			return UnmarshallerBuilder.this.context();
		}

		@Override
		public GOwner owner() {
			return UnmarshallerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das {@link Schema}.
	 *
	 * @see Unmarshaller#setSchema(Schema) */
	public static class ShemaValue extends SchemaBuilder.Value<ShemaValue> {

		@Override
		public ShemaValue owner() {
			return this;
		}

	}

	public class ShemaProxy extends SchemaBuilder.Proxy<GOwner> {

		@Override
		protected bee.creative.xml.SchemaBuilder.Value<?> value() {
			return null;
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	public static class AdaptersValue extends AdaptersBuilder<AdaptersValue> {

		Set<XmlAdapter<?, ?>> value = new HashSet<>();

		@Override
		public Set<XmlAdapter<?, ?>> get() {
			return this.value;
		}

		@Override
		public AdaptersValue owner() {
			return this;
		}

	}

	public class AdaptersProxy extends AdaptersBuilder<GOwner> {

		@Override
		public Set<XmlAdapter<?, ?>> get() {
			return UnmarshallerBuilder.this.adapters().get();
		}

		@Override
		public GOwner owner() {
			return UnmarshallerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link XmlAdapter}.
	 *
	 * @see Unmarshaller#setAdapter(XmlAdapter)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class AdaptersBuilder<GOwner> extends BaseSetBuilder<XmlAdapter<?, ?>, Set<XmlAdapter<?, ?>>, GOwner> {

	}

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
			return UnmarshallerBuilder.this.listener().get();
		}

		@Override
		public void set(final Listener value) {
			UnmarshallerBuilder.this.listener().set(value);
		}

		@Override
		public GOwner owner() {
			return UnmarshallerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Listener}.
	 *
	 * @see Unmarshaller#setListener(Listener)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ListenerBuilder<GOwner> extends BaseValueBuilder<Listener, GOwner> {

	}

	public static class PropertiesValue extends PropertiesBuilder<PropertiesValue> {

		Map<String, Object> value = new HashMap<>();

		@Override
		public Map<String, Object> get() {
			return this.value;
		}

		@Override
		public PropertiesValue owner() {
			return this;
		}

	}

	public class PropertiesProxy extends PropertiesBuilder<GOwner> {

		@Override
		public Map<String, Object> get() {
			return UnmarshallerBuilder.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return UnmarshallerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften.
	 *
	 * @see Unmarshaller#setProperty(String, Object)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertiesBuilder<GOwner> extends BaseMapBuilder<String, Object, Map<String, Object>, GOwner> {

	}

	public static class ValidationValue extends ValidationBuilder<ValidationValue> {

		ValidationEventHandler value;

		@Override
		public ValidationEventHandler get() {
			return this.value;
		}

		@Override
		public void set(final ValidationEventHandler value) {
			this.value = value;
		}

		@Override
		public ValidationValue owner() {
			return this;
		}

	}

	public class ValidationProxy extends ValidationBuilder<GOwner> {

		@Override
		public ValidationEventHandler get() {
			return UnmarshallerBuilder.this.validation().get();
		}

		@Override
		public void set(final ValidationEventHandler value) {
			UnmarshallerBuilder.this.validation().set(value);
		}

		@Override
		public GOwner owner() {
			return UnmarshallerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ValidationEventHandler}.
	 *
	 * @see Unmarshaller#setEventHandler(ValidationEventHandler)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ValidationBuilder<GOwner> extends BaseValueBuilder<ValidationEventHandler, GOwner> {

	}
	
	/** {@inheritDoc}
	 *
	 * @see #putValue() */
	@Override
	public Unmarshaller get() throws IllegalStateException {
		try {
			return this.putValue();
		} catch (Exception cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final UnmarshallerBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forShema().use(that.shema());
		this.forAdapter().use(that.adapters());
		this.forListener().use(that.listener());
		this.forProperties().use(that.properties());
		this.forValidation().use(that.validation());
		return this.owner();
	}

	/** Diese Methode gibt den {@link Unmarshaller} zurück. Wenn über {@link #useValue(Object)} noch kein {@link Unmarshaller} gesetzt wurden, werden über
	 * {@link JAXBContext#createUnmarshaller()} ein neuer erstellt und über {@link #useValue(Object)} gesetzt und über {@link #updateValue()} aktualisiert. Der
	 * zur Erstellung verwendeten Kontext kann über {@link #context()} konfiguriert werden.
	 *
	 * @see #updateValue()
	 * @return {@link Unmarshaller}.
	 * @throws SAXException Wenn {@link #updateValue()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link JAXBContext#createUnmarshaller()} eine entsprechende Ausnahme auslöst. */
	public Unmarshaller putValue() throws SAXException, JAXBException {
		Unmarshaller result = this.getValue();
		if (result != null) return result;
		final JAXBContext context = this.context().putValue();
		result = context.createUnmarshaller();
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link Unmarshaller} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #getValue()} ermittelten {@link Unmarshaller} die Einstellungen übertragen, die in {@link #shema()}, {@link #adapters()}, {@link #listener()},
	 * {@link #properties()} und {@link #validation()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link #putValue()} oder {@link SchemaBuilder#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #putValue()}, {@link Unmarshaller#setProperty(String, Object)} oder
	 *         {@link Unmarshaller#setEventHandler(ValidationEventHandler)} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws SAXException, JAXBException {
		final Unmarshaller result = this.putValue();
		result.setSchema(this.shema().putValue());
		result.setListener(this.listener().get());
		result.setEventHandler(this.validation().get());
		for (final XmlAdapter<?, ?> entry: this.adapters()) {
			result.setAdapter(entry);
		}
		for (final Entry<String, Object> entry: this.properties()) {
			result.setProperty(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für das Schema und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setSchema(Schema)
	 * @return Konfigurator. */
	public abstract ShemaValue shema();

	/** Diese Methode öffnet den Konfigurator für die Adapter und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setAdapter(XmlAdapter)
	 * @return Konfigurator. */
	public abstract AdaptersValue adapters();

	/** Diese Methode öffnet den Konfigurator für die Ereignisüberwachung und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setListener(Listener)
	 * @return Konfigurator. */
	public abstract ListenerValue listener();

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setProperty(String, Object)
	 * @return Konfigurator. */
	public abstract PropertiesValue properties();

	/** Diese Methode öffnet den Konfigurator für die Validationsüberwachung und gibt ihn zurück.
	 *
	 * @see Unmarshaller#setEventHandler(ValidationEventHandler)
	 * @return Konfigurator. */
	public abstract ValidationValue validation();

	/** Diese Methode öffnet den Konfigurator für den Kontext und gibt ihn zurück.
	 *
	 * @see JAXBContext#createUnmarshaller()
	 * @return Konfigurator. */
	public abstract ContextValue context();

	public ShemaProxy forShema() {
		return new ShemaProxy();
	}

	public AdaptersProxy forAdapter() {
		return new AdaptersProxy();
	}

	public ListenerProxy forListener() {
		return new ListenerProxy();
	}

	public PropertiesProxy forProperties() {
		return new PropertiesProxy();
	}

	public ValidationProxy forValidation() {
		return new ValidationProxy();
	}

	public ContextProxy forContext() {
		return new ContextProxy();
	}

	@Override
	public abstract GOwner owner();

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.shema(), this.adapters(), this.listener(), this.properties(), this.validation());
	}

}