package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link DocumentBuilderFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class DocumentBuilderFactoryBuilder<GOwner> extends BaseValueBuilder<DocumentBuilderFactory, GOwner> {

	public static abstract class Value<GOwner> extends DocumentBuilderFactoryBuilder<GOwner> {

		DocumentBuilderFactory value;

		SchemaValue schema = new SchemaValue();

		FeaturesValue features = new FeaturesValue();

		PropertiesValue properties = new PropertiesValue();

		AttributesValue attributes = new AttributesValue();

		@Override
		public DocumentBuilderFactory get() {
			return this.value;
		}

		@Override
		public void set(final DocumentBuilderFactory value) {
			this.value = value;
		}

		@Override
		public SchemaValue schema() {
			return this.schema;
		}

		@Override
		public FeaturesValue features() {
			return this.features;
		}

		@Override
		public PropertiesValue properties() {
			return this.properties;
		}

		@Override
		public AttributesValue attributes() {
			return this.attributes;
		}

	}

	public static abstract class Proxy<GOwner> extends DocumentBuilderFactoryBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public DocumentBuilderFactory get() {
			return this.value().get();
		}

		@Override
		public void set(final DocumentBuilderFactory value) {
			this.value().set(value);
		}

		@Override
		public SchemaValue schema() {
			return this.value().schema();
		}

		@Override
		public FeaturesValue features() {
			return this.value().features();
		}

		@Override
		public PropertiesValue properties() {
			return this.value().properties();
		}

		@Override
		public AttributesValue attributes() {
			return this.value().attributes();
		}

	}

	public static class FeaturesValue extends BaseFeaturesData.Value<FeaturesValue> {

		@Override
		public FeaturesValue owner() {
			return this;
		}

	}

	public class FeaturesProxy extends BaseFeaturesData.Proxy<GOwner> {

		@Override
		protected FeaturesValue value() {
			return DocumentBuilderFactoryBuilder.this.features();
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das {@link Schema} einer {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#setSchema(Schema) */
	public static class SchemaValue extends SchemaBuilder.Value<SchemaValue> {

		@Override
		public SchemaValue owner() {
			return this;
		}

	}

	public class SchemaProxy extends SchemaBuilder.Proxy<GOwner> {

		@Override
		protected SchemaValue value() {
			return DocumentBuilderFactoryBuilder.this.schema();
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderFactoryBuilder.this.owner();
		}

	}

	public static class PropertiesValue extends PropertiesBuilder<PropertiesValue> {

		Map<String, Boolean> value = new HashMap<>();

		@Override
		public Map<String, Boolean> get() {
			return this.value;
		}

		@Override
		public PropertiesValue owner() {
			return this;
		}

	}

	public class PropertiesProxy extends PropertiesBuilder<GOwner> {

		@Override
		public Map<String, Boolean> get() {
			return DocumentBuilderFactoryBuilder.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link DocumentBuilderFactory}. */
	public static abstract class PropertiesBuilder<GOwner> extends BaseMapBuilder<String, Boolean, Map<String, Boolean>, GOwner> {

		/** Diese Methode liefert den den Konfigurator für {@code "Coalescing"}.
		 *
		 * @see DocumentBuilderFactory#setCoalescing(boolean) */
		public ValueData forCoalescing() {
			return this.forKey("Coalescing");
		}

		/** Diese Methode wählt {@code "ExpandEntityReferences"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
		 * @return {@code this}. */
		public ValueData forExpandEntityReferences() {
			return this.forKey("ExpandEntityReferences");
		}

		/** Diese Methode wählt {@code "IgnoringComments"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
		 * @return {@code this}. */
		public ValueData forIgnoringComments() {
			return this.forKey("IgnoringComments");
		}

		/** Diese Methode wählt {@code "IgnoringElementContentWhitespace"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
		 * @return {@code this}. */
		public ValueData forIgnoringElementContentWhitespace() {
			return this.forKey("IgnoringElementContentWhitespace");
		}

		/** Diese Methode wählt {@code "NamespaceAware"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
		 * @return {@code this}. */
		public ValueData forNamespaceAware() {
			return this.forKey("NamespaceAware");
		}

		/** Diese Methode wählt {@code "Validating"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setValidating(boolean)
		 * @return {@code this}. */
		public ValueData forValidating() {
			return this.forKey("Validating");
		}

		/** Diese Methode wählt {@code "XIncludeAware"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
		 * @return {@code this}. */
		public ValueData forXIncludeAware() {
			return this.forKey("XIncludeAware");
		}

	}

	public static class AttributesValue extends BaseAttributesBuilder<AttributesValue> {

		Map<String, Object> value = new HashMap<>();

		@Override
		public Map<String, Object> get() {
			return this.value;
		}

		@Override
		public AttributesValue owner() {
			return this;
		}

	}

	public class AttributesProxy extends BaseAttributesBuilder<GOwner> {

		@Override
		public Map<String, Object> get() {
			return DocumentBuilderFactoryBuilder.this.attributes().get();
		}

		@Override
		public GOwner owner() {
			return DocumentBuilderFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Attribute einer {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#setAttribute(String, Object) */
	public static abstract class BaseAttributesBuilder<GOwner> extends BaseMapBuilder<String, Object, Map<String, Object>, GOwner> {

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final DocumentBuilderFactoryBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forSchema().use(that.schema());
		this.forFeatures().use(that.features());
		this.forProperties().use(that.properties());
		this.forAttributes().use(that.attributes());
		return this.owner();
	}

	/** Diese Methode gibt die {@link DocumentBuilderFactory} zurück. Wenn über {@link #useValue(Object)} noch keine {@link DocumentBuilderFactory} gesetzt wurde,
	 * wird über {@link DocumentBuilderFactory#newInstance()} eine neue erstellt, über {@link #useValue(Object)} gesetzt und über {@link #updateValue()}
	 * aktualisiert.
	 *
	 * @see #updateValue()
	 * @return {@link DocumentBuilderFactory}.
	 * @throws SAXException Wenn {@link #updateValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link #updateValue()} eine entsprechende Ausnahme auslöst. */
	public DocumentBuilderFactory putValue() throws SAXException, ParserConfigurationException {
		DocumentBuilderFactory result = this.getValue();
		if (result != null) return result;
		result = DocumentBuilderFactory.newInstance();
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link DocumentBuilderFactory} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf die
	 * über {@link #putValue()} ermittelte {@link DocumentBuilderFactory} die Einstellungen übertragen, die in {@link #schema()}, {@link #features()},
	 * {@link #properties()} und {@link #attributes()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link SchemaValue#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws SAXException, ParserConfigurationException {
		final DocumentBuilderFactory factory = this.putValue();
		factory.setSchema(this.schema().putValue());
		final PropertiesValue propertyData = this.properties();
		factory.setCoalescing(propertyData.forCoalescing().getBoolean());
		factory.setExpandEntityReferences(propertyData.forExpandEntityReferences().getBoolean());
		factory.setIgnoringComments(propertyData.forIgnoringComments().getBoolean());
		factory.setIgnoringElementContentWhitespace(propertyData.forIgnoringElementContentWhitespace().getBoolean());
		factory.setNamespaceAware(propertyData.forNamespaceAware().getBoolean());
		factory.setValidating(propertyData.forValidating().getBoolean());
		factory.setXIncludeAware(propertyData.forXIncludeAware().getBoolean());
		for (final Entry<String, Boolean> entry: this.features()) {
			factory.setFeature(entry.getKey(), Boolean.TRUE.equals(entry.getValue()));
		}
		for (final Entry<String, Object> entry: this.attributes()) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für das {@link Schema} und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @return Konfigurator. */
	public abstract SchemaValue schema();

	/** Diese Methode liefert den Konfigurator für die Fähigkeiten.
	 *
	 * @see DocumentBuilderFactory#setFeature(String, boolean) */
	public abstract FeaturesValue features();

	/** Diese Methode liefert den Konfigurator für die Eigenschaften.
	 *
	 * @see DocumentBuilderFactory#setCoalescing(boolean)
	 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
	 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
	 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
	 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
	 * @see DocumentBuilderFactory#setValidating(boolean)
	 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
	 * @return Konfigurator. */
	public abstract PropertiesValue properties();

	/** Diese Methode liefert den Konfigurator für die Attribute.
	 *
	 * @see DocumentBuilderFactory#setAttribute(String, Object) */
	public abstract AttributesValue attributes();

	public SchemaProxy forSchema() {
		return new SchemaProxy();
	}

	public FeaturesProxy forFeatures() {
		return new FeaturesProxy();
	}

	public PropertiesProxy forProperties() {
		return new PropertiesProxy();
	}

	public AttributesProxy forAttributes() {
		return new AttributesProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.features(), this.schema(), this.properties(), this.attributes());
	}

}