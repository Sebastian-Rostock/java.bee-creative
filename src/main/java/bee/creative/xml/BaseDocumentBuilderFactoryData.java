package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
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
		protected FeatureData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für das Schema einer {@link DocumentBuilderFactory}.
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
		protected SchemaData<GOwner> thiz() {
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
		public PropertyData<GOwner> forCoalescing() {
			return this.forKey("Coalescing");
		}

		/**
		 * Diese Methode wählt {@code "ExpandEntityReferences"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
		 * @return {@code this}.
		 */
		public PropertyData<GOwner> forExpandEntityReferences() {
			return this.forKey("ExpandEntityReferences");
		}

		/**
		 * Diese Methode wählt {@code "IgnoringComments"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
		 * @return {@code this}.
		 */
		public PropertyData<GOwner> forIgnoringComments() {
			return this.forKey("IgnoringComments");
		}

		/**
		 * Diese Methode wählt {@code "IgnoringElementContentWhitespace"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
		 * @return {@code this}.
		 */
		public PropertyData<GOwner> forIgnoringElementContentWhitespace() {
			return this.forKey("IgnoringElementContentWhitespace");
		}

		/**
		 * Diese Methode wählt {@code "NamespaceAware"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
		 * @return {@code this}.
		 */
		public PropertyData<GOwner> forNamespaceAware() {
			return this.forKey("NamespaceAware");
		}

		/**
		 * Diese Methode wählt {@code "Validating"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setValidating(boolean)
		 * @return {@code this}.
		 */
		public PropertyData<GOwner> forValidating() {
			return this.forKey("Validating");
		}

		/**
		 * Diese Methode wählt {@code "XIncludeAware"} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
		 * @return {@code this}.
		 */
		public PropertyData<GOwner> forXIncludeAware() {
			return this.forKey("XIncludeAware");
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn der Wert zum {@link #forKey(Object) gewählten Schlüssel} gleich {@link Boolean#TRUE} ist.
		 * 
		 * @see #getValue()
		 * @return Wahrheitswert.
		 */
		public boolean getBoolean() {
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
		protected PropertyData<GOwner> thiz() {
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
		protected AttributeData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link DocumentBuilderFactory}.
	 */
	DocumentBuilderFactory factory;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
	 */
	final FeatureData<GThiz> featureData = new FeatureData<GThiz>() {

		@Override
		public GThiz closeFeatureData() {
			return BaseDocumentBuilderFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openSchemaData()}.
	 */
	final SchemaData<GThiz> schemaData = new SchemaData<GThiz>() {

		@Override
		public GThiz closeSchemaData() {
			return BaseDocumentBuilderFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openPropertyData()}.
	 */
	final PropertyData<GThiz> propertyData = new PropertyData<GThiz>() {

		@Override
		public GThiz closePropertyData() {
			return BaseDocumentBuilderFactoryData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}.
	 */
	final AttributeData<GThiz> attributeData = new AttributeData<GThiz>() {

		@Override
		public GThiz closeAttributeData() {
			return BaseDocumentBuilderFactoryData.this.thiz();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseDocumentBuilderFactoryData<?> data) {
		if (data == null) return this.thiz();
		this.factory = data.factory;
		this.featureData.use(data.featureData);
		this.propertyData.use(data.propertyData);
		this.attributeData.use(data.attributeData);
		return this.thiz();
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
	 * @throws ParserConfigurationException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 */
	public DocumentBuilderFactory getFactory() throws ParserConfigurationException {
		DocumentBuilderFactory result = this.factory;
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
	public GThiz useFactory(final DocumentBuilderFactory factory) {
		this.factory = factory;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt die {@link DocumentBuilderFactory} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useFactory(DocumentBuilderFactory)
	 * @return {@code this}.
	 */
	public GThiz resetFactory() {
		return this.useFactory(null);
	}

	/**
	 * Diese Methode aktualisiert die Einstellungen der {@link DocumentBuilderFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link DocumentBuilderFactory} die Einstellungen übertragen, die in
	 * {@link #openSchemaData()}, {@link #openFeatureData()}, {@link #openPropertyData()} und {@link #openAttributeData()} konfiguriert sind.
	 * 
	 * @return {@code this}.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
	 */
	public GThiz updateFactory() throws ParserConfigurationException {
		final DocumentBuilderFactory factory = this.getFactory();
		factory.setSchema(this.schemaData.build());
		final PropertyData<GThiz> propertyData = this.propertyData;
		factory.setCoalescing(propertyData.forCoalescing().getBoolean());
		factory.setExpandEntityReferences(propertyData.forExpandEntityReferences().getBoolean());
		factory.setIgnoringComments(propertyData.forIgnoringComments().getBoolean());
		factory.setIgnoringElementContentWhitespace(propertyData.forIgnoringElementContentWhitespace().getBoolean());
		factory.setNamespaceAware(propertyData.forNamespaceAware().getBoolean());
		factory.setValidating(propertyData.forValidating().getBoolean());
		factory.setXIncludeAware(propertyData.forXIncludeAware().getBoolean());
		for (final Entry<String, Boolean> entry: this.featureData) {
			factory.setFeature(entry.getKey(), entry.getValue().booleanValue());
		}
		for (final Entry<String, Object> entry: this.attributeData) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.thiz();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setFeature(String, boolean)
	 * @return Konfigurator.
	 */
	public FeatureData<GThiz> openFeatureData() {
		return this.featureData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für das {@link Schema} und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @return Konfigurator.
	 */
	public SchemaData<GThiz> openSchemaData() {
		return this.schemaData;
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
	public PropertyData<GThiz> openPropertyData() {
		return this.propertyData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Attribute und gibt ihn zurück.
	 * 
	 * @see DocumentBuilderFactory#setAttribute(String, Object)
	 * @return Konfigurator.
	 */
	public AttributeData<GThiz> openAttributeData() {
		return this.attributeData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz thiz();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getFactory()
	 */
	@Override
	public DocumentBuilderFactory build() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final ParserConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.featureData, this.schemaData, this.propertyData, this.attributeData);
	}

}