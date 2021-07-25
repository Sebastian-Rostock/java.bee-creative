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
public abstract class BaseDocumentBuilderFactoryData<GOwner> extends BaseValueBuilder<DocumentBuilderFactory, GOwner> {

	public static class FeaturesValue extends BaseFeaturesData.Value<FeaturesValue> {

		@Override
		public FeaturesValue owner() {
			return this;
		}

	}

	public class FeaturesProxy extends BaseFeaturesData.Proxy<GOwner> {

		protected FeaturesValue value() {
			return BaseDocumentBuilderFactoryData.this.features();
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderFactoryData.this.owner();
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

		protected SchemaValue value() {
			return BaseDocumentBuilderFactoryData.this.schema();
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderFactoryData.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link DocumentBuilderFactory}. */
	public static abstract class BasePropertiesData<GOwner> extends BaseMapBuilder<String, Boolean, Map<String, Boolean>, GOwner> {

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

	public static class PropertiesData extends BasePropertiesData<PropertiesData> {

		@Override
		public Map<String, Boolean> get() {
			return null;
		}

		@Override
		public PropertiesData owner() {
			return this;
		}

	}

	public class PropertiesData2 extends BasePropertiesData<GOwner> {

		@Override
		public Map<String, Boolean> get() {
			return BaseDocumentBuilderFactoryData.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderFactoryData.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Attribute einer {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#setAttribute(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public static abstract class BaseAttributesData<GOwner> extends BaseMapBuilder<String, Object, Map<String, Object>, GOwner> {

	}

	public static class AttributesData extends BaseAttributesData<AttributesData> {

		Map<String, Object> value = new HashMap<>();

		@Override
		public Map<String, Object> get() {
			return this.value;
		}

		@Override
		public AttributesData owner() {
			return this;
		}

	}

	public class AttributesData2 extends BaseAttributesData<GOwner> {

		@Override
		public Map<String, Object> get() {
			return BaseDocumentBuilderFactoryData.this.attributes().get();
		}

		@Override
		public GOwner owner() {
			return BaseDocumentBuilderFactoryData.this.owner();
		}

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final BaseDocumentBuilderFactoryData<?> data) {
		if (data == null) return this.owner();
		this.factory = data.factory;
		this.featureData.use(data.featureData);
		this.propertyData.use(data.propertyData);
		this.attributes().use(data.attributes());
		return this.owner();
	}

	/** Diese Methode gibt die {@link DocumentBuilderFactory} zurück. Wenn über {@link #useFactory(DocumentBuilderFactory)} noch keine
	 * {@link DocumentBuilderFactory} gesetzt wurde, wird über {@link DocumentBuilderFactory#newInstance()} eine neue erstellt, über
	 * {@link #useFactory(DocumentBuilderFactory)} gesetzt und über {@link #updateValue()} aktualisiert.
	 *
	 * @see #useFactory(DocumentBuilderFactory)
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
	 * über {@link #putValue()} ermittelte {@link DocumentBuilderFactory} die Einstellungen übertragen, die in {@link #openSchemaData()}, {@link #forFeatures()},
	 * {@link #forProperties()} und {@link #attributes()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link SchemaValue#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws SAXException, ParserConfigurationException {
		final DocumentBuilderFactory factory = this.putValue();
		factory.setSchema(this._schemaData_.putValue());
		final PropertiesData propertyData = this.propertyData;
		factory.setCoalescing(propertyData.forCoalescing().getBoolean());
		factory.setExpandEntityReferences(propertyData.forExpandEntityReferences().getBoolean());
		factory.setIgnoringComments(propertyData.forIgnoringComments().getBoolean());
		factory.setIgnoringElementContentWhitespace(propertyData.forIgnoringElementContentWhitespace().getBoolean());
		factory.setNamespaceAware(propertyData.forNamespaceAware().getBoolean());
		factory.setValidating(propertyData.forValidating().getBoolean());
		factory.setXIncludeAware(propertyData.forXIncludeAware().getBoolean());
		for (final Entry<String, Boolean> entry: this.featureData) {
			factory.setFeature(entry.getKey(), Boolean.TRUE.equals(entry.getValue()));
		}
		for (final Entry<String, Object> entry: this.attributeData) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode liefert den Konfigurator für die Fähigkeiten.
	 *
	 * @see DocumentBuilderFactory#setFeature(String, boolean) */
	public abstract FeaturesValue features();

	public FeaturesProxy forFeatures() {
		return new FeaturesProxy();
	}

	/** Diese Methode öffnet den Konfigurator für das {@link Schema} und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @return Konfigurator. */
	public abstract SchemaValue schema();

	public SchemaProxy forSchema() {
		return new SchemaProxy();
	}

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
	public abstract PropertiesData properties();

	public PropertiesData2 forProperties() {
		return new PropertiesData2();
	}

	/** Diese Methode liefert den Konfigurator für die Attribute.
	 *
	 * @see DocumentBuilderFactory#setAttribute(String, Object) */
	public abstract AttributesData attributes();

	public AttributesData2 forAttributes() {
		return new AttributesData2();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.features(), this.schema(), this.properties(), this.attributes());
	}

}