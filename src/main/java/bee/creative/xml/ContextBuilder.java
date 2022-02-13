package bee.creative.xml;

import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseSetBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.HashMap;
import bee.creative.util.HashSet;

/** Diese Klasse implementiert den Konfigurator für einen {@link JAXBContext}.
 *
 * @see JAXBContext#newInstance(Class[], java.util.Map)
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class ContextBuilder<GOwner> extends BaseValueBuilder<JAXBContext, GOwner> {

	public static abstract class Value<GOwner> extends ContextBuilder<GOwner> {

		JAXBContext value;

		ClassesValue classes = new ClassesValue();

		PropertiesValue properties = new PropertiesValue();

		@Override
		public JAXBContext get() {
			return this.value;
		}

		@Override
		public void set(final JAXBContext value) {
			this.value = value;
		}

		@Override
		public ClassesValue classes() {
			return this.classes;
		}

		@Override
		public PropertiesValue properties() {
			return this.properties;
		}

	}

	public static abstract class Proxy<GOwner> extends ContextBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public JAXBContext get() {
			return this.value().get();
		}

		@Override
		public void set(final JAXBContext value) {
			this.value().set(value);
		}

		@Override
		public ClassesValue classes() {
			return this.value().classes();
		}

		@Override
		public PropertiesValue properties() {
			return this.value().properties();
		}

	}

	public static class ClassesValue extends ClassesBuilder<ClassesValue> {

		Set<Class<?>> value = new HashSet<>();

		@Override
		public Set<Class<?>> get() {
			return this.value;
		}

		@Override
		public ClassesValue owner() {
			return this;
		}

	}

	public class ClassesProxy extends ClassesBuilder<GOwner> {

		@Override
		public Set<Class<?>> get() {
			return ContextBuilder.this.classes().get();
		}

		@Override
		public GOwner owner() {
			return ContextBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Klassen zur Erzeugung des {@link JAXBContext}.
	 *
	 * @see JAXBContext#newInstance(Class[], java.util.Map)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ClassesBuilder<GOwner> extends BaseSetBuilder<Class<?>, Set<Class<?>>, GOwner> {

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
			return ContextBuilder.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return ContextBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften zur Erzeugung des {@link JAXBContext}.
	 *
	 * @see JAXBContext#newInstance(Class[], java.util.Map)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertiesBuilder<GOwner> extends BaseMapBuilder<String, Object, Map<String, Object>, GOwner> {

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final ContextBuilder<?> that) {
		if (that == null) return this.owner();
		this.forClasses().use(that.classes());
		this.forProperties().use(that.properties());
		return this.useValue(that.getValue());
	}

	/** Diese Methode gibt den {@link JAXBContext} zurück. Wenn über {@link #useValue(Object)} noch kein {@link JAXBContext} gesetzt wurden, werden über
	 * {@link JAXBContext#newInstance(Class[], Map)} ein neuer erstellt und dieser über {@link #useValue(Object)} gesetzt. Die zur Erstellung verwendeten Klassen
	 * und Eigenschaften können über {@link #classes()} bzw. {@link #properties()} konfiguriert werden.
	 *
	 * @see #useValue(Object)
	 * @return {@link JAXBContext}.
	 * @throws JAXBException Wenn {@link JAXBContext#newInstance(Class[], Map)} eine entsprechende Ausnahme auslöst. */
	public JAXBContext putValue() throws JAXBException {
		JAXBContext res = this.getValue();
		if (res != null) return res;
		res = JAXBContext.newInstance(this.classes().get().toArray(new Class<?>[0]), this.properties().get());
		this.useValue(res);
		return res;
	}

	/** Diese Methode öffnet den Konfigurator für die Klassen zur Erzeugung des {@link JAXBContext} und gibt ihn zurück.
	 *
	 * @see JAXBContext#newInstance(Class[], Map) */
	public abstract ClassesValue classes();

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften zur Erzeugung des {@link JAXBContext} und gibt ihn zurück.
	 *
	 * @see JAXBContext#newInstance(Class[], Map)
	 * @return Konfigurator. */
	public abstract PropertiesValue properties();

	/** Diese Methode öffnet den Konfigurator für die Klassen zur Erzeugung des {@link JAXBContext} und gibt ihn zurück.
	 *
	 * @see JAXBContext#newInstance(Class[], Map) */
	public ClassesProxy forClasses() {
		return new ClassesProxy();
	}

	public PropertiesProxy forProperties() {
		return new PropertiesProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.classes(), this.properties());
	}

}
