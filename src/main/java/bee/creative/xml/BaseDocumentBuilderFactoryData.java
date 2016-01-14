package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link DocumentBuilderFactory}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseDocumentBuilderFactoryData<GThiz> extends BaseBuilder<DocumentBuilderFactory, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link DocumentBuilderFactory}.
	 * 
	 * @see DocumentBuilderFactory#setFeature(String, boolean)
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
		protected final FeatureData<GOwner> __this() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für das {@link Schema} einer {@link DocumentBuilderFactory}.
	 * 
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class SchemaData<GOwner> extends BaseSchemaData<SchemaData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeSchemaData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final SchemaData<GOwner> __this() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link DocumentBuilderFactory}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder<String, Boolean, PropertyData<GOwner>> {

		/**
		 * Diese Methode wählt {@code "Coalescing"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setCoalescing(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forCoalescing() {
			return this.forKey("Coalescing");
		}

		/**
		 * Diese Methode wählt {@code "ExpandEntityReferences"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forExpandEntityReferences() {
			return this.forKey("ExpandEntityReferences");
		}

		/**
		 * Diese Methode wählt {@code "IgnoringComments"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forIgnoringComments() {
			return this.forKey("IgnoringComments");
		}

		/**
		 * Diese Methode wählt {@code "IgnoringElementContentWhitespace"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forIgnoringElementContentWhitespace() {
			return this.forKey("IgnoringElementContentWhitespace");
		}

		/**
		 * Diese Methode wählt {@code "NamespaceAware"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forNamespaceAware() {
			return this.forKey("NamespaceAware");
		}

		/**
		 * Diese Methode wählt {@code "Validating"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setValidating(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forValidating() {
			return this.forKey("Validating");
		}

		/**
		 * Diese Methode wählt {@code "XIncludeAware"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
		 * @return {@code this}.
		 */
		public final PropertyData<GOwner> forXIncludeAware() {
			return this.forKey("XIncludeAware");
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der Wert zum {@link #forKey(Object) gewählten Schlüssel} gleich {@link Boolean#TRUE} ist.
		 * 
		 * @see #getValue()
		 * @return Wahrheitswert.
		 */
		public final boolean getBoolean() {
			return Boolean.TRUE.equals(this.getValue());
		}

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
		protected final PropertyData<GOwner> __this() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Attribute einer {@link DocumentBuilderFactory}.
	 * 
	 * @see DocumentBuilderFactory#setAttribute(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class AttributeData<GOwner> extends BaseMapBuilder<String, Object, AttributeData<GOwner>> {

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
		protected final AttributeData<GOwner> __this() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link DocumentBuilderFactory}.
	 */
	DocumentBuilderFactory __factory;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
	 */
	final FeatureData<GThiz> __featureData = new FeatureData<GThiz>() {

		@Override
		public final GThiz closeFeatureData() {
			return BaseDocumentBuilderFactoryData.this.__this();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openSchemaData()}.
	 */
	final SchemaData<GThiz> __schemaData = new SchemaData<GThiz>() {

		@Override
		public final GThiz closeSchemaData() {
			return BaseDocumentBuilderFactoryData.this.__this();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openPropertyData()}.
	 */
	final PropertyData<GThiz> __propertyData = new PropertyData<GThiz>() {

		@Override
		public final GThiz closePropertyData() {
			return BaseDocumentBuilderFactoryData.this.__this();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}.
	 */
	final AttributeData<GThiz> __attributeData = new AttributeData<GThiz>() {

		@Override
		public final GThiz closeAttributeData() {
			return BaseDocumentBuilderFactoryData.this.__this();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz use(final BaseDocumentBuilderFactoryData<?> data) {
		if (data == null) return this.__this();
		this.__factory = data.__factory;
		this.__featureData.use(data.__featureData);
		this.__propertyData.use(data.__propertyData);
		this.__attributeData.use(data.__attributeData);
		return this.__this();
	}

	/**
	 * Diese Methode gibt die {@link DocumentBuilderFactory} zurück.<br>
	 * Wenn über {@link #useFactory(DocumentBuilderFactory)} noch keine {@link DocumentBuilderFactory} gesetzt wurde, wird über
	 * {@link DocumentBuilderFactory#newInstance()} eine neue erstellt, über {@link #useFactory(DocumentBuilderFactory)} gesetzt und über {@link #updateFactory()}
	 * aktualisiert.
	 * 
	 * @see #useFactory(DocumentBuilderFactory)
	 * @see #updateFactory()
	 * @return {@link DocumentBuilderFactory}.
	 * @throws SAXException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 */
	public final DocumentBuilderFactory getFactory() throws SAXException, ParserConfigurationException {
		DocumentBuilderFactory result = this.__factory;
		if (result != null) return result;
		result = DocumentBuilderFactory.newInstance();
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/**
	 * Diese Methode setzt die {@link DocumentBuilderFactory} und gibt {@code this} zurück.
	 * 
	 * @param factory {@link DocumentBuilderFactory} oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useFactory(final DocumentBuilderFactory factory) {
		this.__factory = factory;
		return this.__this();
	}

	/**
	 * Diese Methode setzt die {@link DocumentBuilderFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(DocumentBuilderFactory)
	 * @return {@code this}.
	 */
	public final GThiz resetFactory() {
		return this.useFactory(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen der {@link DocumentBuilderFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link DocumentBuilderFactory} die Einstellungen übertragen, die in
	 * {@link #openSchemaData()}, {@link #openFeatureData()}, {@link #openPropertyData()} und {@link #openAttributeData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link SchemaData#getSchema()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
	 */
	public final GThiz updateFactory() throws SAXException, ParserConfigurationException {
		final DocumentBuilderFactory factory = this.getFactory();
		factory.setSchema(this.__schemaData.getSchema());
		final PropertyData<GThiz> propertyData = this.__propertyData;
		factory.setCoalescing(propertyData.forCoalescing().getBoolean());
		factory.setExpandEntityReferences(propertyData.forExpandEntityReferences().getBoolean());
		factory.setIgnoringComments(propertyData.forIgnoringComments().getBoolean());
		factory.setIgnoringElementContentWhitespace(propertyData.forIgnoringElementContentWhitespace().getBoolean());
		factory.setNamespaceAware(propertyData.forNamespaceAware().getBoolean());
		factory.setValidating(propertyData.forValidating().getBoolean());
		factory.setXIncludeAware(propertyData.forXIncludeAware().getBoolean());
		for (final Entry<String, Boolean> entry: this.__featureData) {
			factory.setFeature(entry.getKey(), Boolean.TRUE.equals(entry.getValue()));
		}
		for (final Entry<String, Object> entry: this.__attributeData) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.__this();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setFeature(String, boolean)
	 * @return Konfigurator.
	 */
	public final FeatureData<GThiz> openFeatureData() {
		return this.__featureData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für das {@link Schema} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @return Konfigurator.
	 */
	public final SchemaData<GThiz> openSchemaData() {
		return this.__schemaData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setCoalescing(boolean)
	 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
	 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
	 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
	 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
	 * @see DocumentBuilderFactory#setValidating(boolean)
	 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
	 * @return Konfigurator.
	 */
	public final PropertyData<GThiz> openPropertyData() {
		return this.__propertyData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Attribute und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setAttribute(String, Object)
	 * @return Konfigurator.
	 */
	public final AttributeData<GThiz> openAttributeData() {
		return this.__attributeData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz __this();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getFactory()
	 */
	@Override
	public final DocumentBuilderFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final SAXException | ParserConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__featureData, this.__schemaData, this.__propertyData, this.__attributeData);
	}

}