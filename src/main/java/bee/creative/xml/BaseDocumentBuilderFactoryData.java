package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import bee.creative.util.Objects;
import bee.creative.util.Producers.BaseBuilder;
import bee.creative.util.Producers.BaseMapData;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link DocumentBuilderFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseDocumentBuilderFactoryData<GThis> extends BaseBuilder<DocumentBuilderFactory, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#setFeature(String, boolean)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FeatureData<GOwner> extends BaseFeatureData<FeatureData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFeatureData();

		/** {@inheritDoc} */
		@Override
		protected final FeatureData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das {@link Schema} einer {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class SchemaData<GOwner> extends BaseSchemaData<SchemaData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeSchemaData();

		/** {@inheritDoc} */
		@Override
		protected final SchemaData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link DocumentBuilderFactory}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapData<String, Boolean, PropertyData<GOwner>> {

		/** Diese Methode wählt {@code "Coalescing"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setCoalescing(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forCoalescing() {
			return this.forKey("Coalescing");
		}

		/** Diese Methode wählt {@code "ExpandEntityReferences"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forExpandEntityReferences() {
			return this.forKey("ExpandEntityReferences");
		}

		/** Diese Methode wählt {@code "IgnoringComments"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forIgnoringComments() {
			return this.forKey("IgnoringComments");
		}

		/** Diese Methode wählt {@code "IgnoringElementContentWhitespace"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forIgnoringElementContentWhitespace() {
			return this.forKey("IgnoringElementContentWhitespace");
		}

		/** Diese Methode wählt {@code "NamespaceAware"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forNamespaceAware() {
			return this.forKey("NamespaceAware");
		}

		/** Diese Methode wählt {@code "Validating"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setValidating(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forValidating() {
			return this.forKey("Validating");
		}

		/** Diese Methode wählt {@code "XIncludeAware"} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forXIncludeAware() {
			return this.forKey("XIncludeAware");
		}

		/** Diese Methode gibt nur dann {@code true} zurück, wenn der Wert zum {@link #forKey(Object) gewählten Schlüssel} gleich {@link Boolean#TRUE} ist.
		 *
		 * @see #getValue()
		 * @return Wahrheitswert. */
		public final boolean getBoolean() {
			return Boolean.TRUE.equals(this.getValue());
		}

		/** Diese Methode ist eine Abkürzung für {@code this.useValue(Boolean.TRUE)} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public final PropertyData<GOwner> useTRUE() {
			return this.useValue(Boolean.TRUE);
		}

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		/** {@inheritDoc} */
		@Override
		protected final PropertyData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Attribute einer {@link DocumentBuilderFactory}.
	 *
	 * @see DocumentBuilderFactory#setAttribute(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class AttributeData<GOwner> extends BaseMapData<String, Object, AttributeData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeAttributeData();

		/** {@inheritDoc} */
		@Override
		protected final AttributeData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert die {@link DocumentBuilderFactory}. */
	DocumentBuilderFactory factory;

	/** Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}. */
	final FeatureData<GThis> featureData = new FeatureData<GThis>() {

		@Override
		public final GThis closeFeatureData() {
			return BaseDocumentBuilderFactoryData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openSchemaData()}. */
	final SchemaData<GThis> _schemaData_ = new SchemaData<GThis>() {

		@Override
		public final GThis closeSchemaData() {
			return BaseDocumentBuilderFactoryData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator {@link #openPropertyData()}. */
	final PropertyData<GThis> propertyData = new PropertyData<GThis>() {

		@Override
		public final GThis closePropertyData() {
			return BaseDocumentBuilderFactoryData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}. */
	final AttributeData<GThis> attributeData = new AttributeData<GThis>() {

		@Override
		public final GThis closeAttributeData() {
			return BaseDocumentBuilderFactoryData.this.customThis();
		}

	};

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseDocumentBuilderFactoryData<?> data) {
		if (data == null) return this.customThis();
		this.factory = data.factory;
		this.featureData.use(data.featureData);
		this.propertyData.use(data.propertyData);
		this.attributeData.use(data.attributeData);
		return this.customThis();
	}

	/** Diese Methode gibt die {@link DocumentBuilderFactory} zurück.<br>
	 * Wenn über {@link #useFactory(DocumentBuilderFactory)} noch keine {@link DocumentBuilderFactory} gesetzt wurde, wird über
	 * {@link DocumentBuilderFactory#newInstance()} eine neue erstellt, über {@link #useFactory(DocumentBuilderFactory)} gesetzt und über {@link #updateFactory()}
	 * aktualisiert.
	 *
	 * @see #useFactory(DocumentBuilderFactory)
	 * @see #updateFactory()
	 * @return {@link DocumentBuilderFactory}.
	 * @throws SAXException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link #updateFactory()} eine entsprechende Ausnahme auslöst. */
	public final DocumentBuilderFactory getFactory() throws SAXException, ParserConfigurationException {
		DocumentBuilderFactory result = this.factory;
		if (result != null) return result;
		result = DocumentBuilderFactory.newInstance();
		this.useFactory(result);
		this.updateFactory();
		return result;
	}

	/** Diese Methode setzt die {@link DocumentBuilderFactory} und gibt {@code this} zurück.
	 *
	 * @param factory {@link DocumentBuilderFactory} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useFactory(final DocumentBuilderFactory factory) {
		this.factory = factory;
		return this.customThis();
	}

	/** Diese Methode setzt die {@link DocumentBuilderFactory} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useFactory(DocumentBuilderFactory)
	 * @return {@code this}. */
	public final GThis resetFactory() {
		return this.useFactory(null);
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link DocumentBuilderFactory} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link DocumentBuilderFactory} die Einstellungen übertragen, die in
	 * {@link #openSchemaData()}, {@link #openFeatureData()}, {@link #openPropertyData()} und {@link #openAttributeData()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link SchemaData#getSchema()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst. */
	public final GThis updateFactory() throws SAXException, ParserConfigurationException {
		final DocumentBuilderFactory factory = this.getFactory();
		factory.setSchema(this._schemaData_.getSchema());
		final PropertyData<GThis> propertyData = this.propertyData;
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
		return this.customThis();
	}

	/** Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#setFeature(String, boolean)
	 * @return Konfigurator. */
	public final FeatureData<GThis> openFeatureData() {
		return this.featureData;
	}

	/** Diese Methode öffnet den Konfigurator für das {@link Schema} und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#setSchema(Schema)
	 * @return Konfigurator. */
	public final SchemaData<GThis> openSchemaData() {
		return this._schemaData_;
	}

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#setCoalescing(boolean)
	 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
	 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
	 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
	 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
	 * @see DocumentBuilderFactory#setValidating(boolean)
	 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
	 * @return Konfigurator. */
	public final PropertyData<GThis> openPropertyData() {
		return this.propertyData;
	}

	/** Diese Methode öffnet den Konfigurator für die Attribute und gibt ihn zurück.
	 *
	 * @see DocumentBuilderFactory#setAttribute(String, Object)
	 * @return Konfigurator. */
	public final AttributeData<GThis> openAttributeData() {
		return this.attributeData;
	}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getFactory() */
	@Override
	public final DocumentBuilderFactory get() throws IllegalStateException {
		try {
			return this.getFactory();
		} catch (final SAXException | ParserConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.featureData, this._schemaData_, this.propertyData, this.attributeData);
	}

}