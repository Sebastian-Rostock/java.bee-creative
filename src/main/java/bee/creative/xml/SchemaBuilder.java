package bee.creative.xml;

import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link Schema}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class SchemaBuilder<GOwner> extends BaseValueBuilder<Schema, GOwner> {

	public static abstract class Value<GOwner> extends SchemaBuilder<GOwner> {

		Schema value;

		FactoryValue factory = new FactoryValue();

		SourceValue source = new SourceValue();

		@Override
		public Schema get() {
			return this.value;
		}

		@Override
		public void set(final Schema value) {
			this.value = value;
		}

		@Override
		public SourceValue source() {
			return this.source;
		}

		@Override
		public FactoryValue factory() {
			return this.factory;
		}

	}

	public static abstract class Proxy<GOwner> extends SchemaBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Schema get() {
			return this.value().get();
		}

		@Override
		public void set(final Schema value) {
			this.value().set(value);
		}

		@Override
		public SourceValue source() {
			return this.value().source();
		}

		@Override
		public FactoryValue factory() {
			return this.value().factory();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Schemadaten eines {@link Schema}.
	 *
	 * @see SchemaFactory#newSchema(Source) */
	public static class SourceValue extends SourceBuilder.Value<SourceValue> {

		@Override
		public SourceValue owner() {
			return this;
		}

	}

	public class SourceProxy extends SourceBuilder.Proxy<GOwner> {

		@Override
		protected SourceValue value() {
			return source();
		}

		@Override
		public GOwner owner() {
			return SchemaBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator einer {@link SchemaFactory}.
	 *
	 * @see SchemaFactory#newSchema(Source) */
	public static class FactoryValue extends SchemaFactoryBuilder.Value<FactoryValue> {

		@Override
		public FactoryValue owner() {
			return this;
		}

	}

	public class FactoryProxy extends SchemaFactoryBuilder.Proxy<GOwner> {

		@Override
		protected FactoryValue value() {
			return SchemaBuilder.this.factory();
		}

		@Override
		public GOwner owner() {
			return SchemaBuilder.this.owner();
		}

	}
 
	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final SchemaBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forSource().use(that.source());
		this.forFactory().use(that.factory());
		return this.owner();
	}

	/** Diese Methode gibt das {@link Schema} zurück. Wenn über {@link #useValue(Object)} noch kein {@link Schema} gesetzt wurden, werden über
	 * {@link SchemaFactory#newSchema(Source)} ein neues erstellt und über {@link #useValue(Object)} gesetzt. Die zur Erstellung verwendeten Quelldaten können
	 * über {@link #source()} konfiguriert werden. Wenn diese {@code null} sind, wird {@code null} geliefert.
	 *
	 * @return {@link Schema} oder {@code null}.
	 * @throws SAXException Wenn {@link SchemaFactory#newSchema(Source)} eine entsprechende Ausnahme auslöst. */
	public Schema putValue() throws SAXException {
		Schema result = this.getValue();
		if (result != null) return result;
		final Source source = this.source().getValue();
		if (source == null) return null;
		result = this.factory().putValue().newSchema(source);
		this.useValue(result);
		return result;
	}

	/** Diese Methode öffnet den Konfigurator für die Schemadaten (z.B. xsd-Datei) und gibt ihn zurück.
	 *
	 * @see SchemaFactory#newSchema(Source)
	 * @return Konfigurator. */
	public abstract SourceValue source();

	/** Diese Methode öffnet den Konfigurator für die {@link SchemaFactory} und gibt ihn zurück.
	 *
	 * @see SchemaFactory#newSchema(Source)
	 * @return Konfigurator. */
	public abstract FactoryValue factory();

	public SourceProxy forSource() {
		return new SourceProxy();
	}

	public FactoryProxy forFactory() {
		return new FactoryProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.source(), this.factory());
	}

}