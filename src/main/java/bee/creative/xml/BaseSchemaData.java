package bee.creative.xml;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link Schema}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseSchemaData<GThiz> extends BaseBuilder<Schema, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Schemadaten eines {@link Schema}.
	 * 
	 * @see SchemaFactory#newSchema(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class SourceData<GOwner> extends BaseSourceData<SourceData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeSourceData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final SourceData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator einer {@link SchemaFactory}.
	 * 
	 * @see SchemaFactory#newSchema(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class FactoryData<GOwner> extends BaseSchemaFactoryData<FactoryData<GOwner>> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public abstract GOwner closeFactoryData();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final FactoryData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert das {@link Schema}.
	 */
	Schema __schema;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
	 */
	final FactoryData<GThiz> __factoryData = new FactoryData<GThiz>() {

		@Override
		public final GThiz closeFactoryData() {
			return BaseSchemaData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
	 */
	final SourceData<GThiz> __sourceData = new SourceData<GThiz>() {

		@Override
		public final GThiz closeSourceData() {
			return BaseSchemaData.this.thiz();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz use(final BaseSchemaData<?> data) {
		if (data == null) return this.thiz();
		this.__schema = data.__schema;
		this.__sourceData.use(data.__sourceData);
		this.__factoryData.use(data.__factoryData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt das {@link Schema} zurück.<br>
	 * Wenn über {@link #useSchema(Schema)} noch kein {@link Schema} gesetzt wurden, werden über {@link SchemaFactory#newSchema(Source)} ein neues erstellt und
	 * über {@link #useSchema(Schema)} gesetzt. Die zur erstellung verwendeten Quelldaten können über {@link #openSourceData()} konfiguriert werden. Wenn diese
	 * {@code null} sind, wird {@code null} geliefert.
	 * 
	 * @see #useSchema(Schema)
	 * @return {@link Schema} oder {@code null}.
	 * @throws SAXException Wenn {@link SchemaFactory#newSchema(Source)} eine entsprechende Ausnahme auslöst.
	 */
	public final Schema getSchema() throws SAXException {
		Schema result = this.__schema;
		if (result != null) return result;
		final Source source = this.__sourceData.getSource();
		if (source == null) return null;
		result = this.__factoryData.getFactory().newSchema(source);
		this.useSchema(result);
		return result;
	}

	/**
	 * Diese Methode setzt das {@link Schema} und gibt {@code this} zurück.
	 * 
	 * @param schema {@link Schema} oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useSchema(final Schema schema) {
		this.__schema = schema;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt das {@link Schema} auf {@code null} und gibt {@code this} zurück. Wenn {@link #getSchema()} {@code null} liefern soll, müssen die
	 * {@link #openSourceData() Quelldaten} ebenfalls {@link SourceData#resetSource() rückgesetzt} werden.
	 * 
	 * @see #useSchema(Schema)
	 * @return {@code this}.
	 */
	public final GThiz resetSchema() {
		return this.useSchema(null);
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Schemadaten (z.B. xsd-Datei) und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#newSchema(Source)
	 * @return Konfigurator.
	 */
	public final SourceData<GThiz> openSourceData() {
		return this.__sourceData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link SchemaFactory} und gibt ihn zurück.
	 * 
	 * @see SchemaFactory#newSchema(Source)
	 * @return Konfigurator.
	 */
	public final FactoryData<GThiz> openFactoryData() {
		return this.__factoryData;
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
	 * @see #getSchema()
	 */
	@Override
	public final Schema build() throws IllegalStateException {
		try {
			return this.getSchema();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__sourceData, this.__factoryData);
	}

}