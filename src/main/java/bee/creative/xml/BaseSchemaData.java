package bee.creative.xml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
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
	 * Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Transformer}.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class SourceData extends BaseSourceData<SourceData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeSourceData() {
			return BaseSchemaData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SourceData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator einer {@link SchemaFactory}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class SchemaFactoryData extends BaseSchemaFactoryData<SchemaFactoryData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeFactoryData() {
			return BaseSchemaData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SchemaFactoryData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert das {@link Schema}.
	 */
	Schema schema;

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
	 */
	final SchemaFactoryData factoryData = //
		new SchemaFactoryData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
	 */
	final SourceData sourceData = //
		new SourceData();

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseSchemaData<?> data) {
		if (data == null) return this.thiz();
		this.schema = data.schema;
		this.sourceData.use(data.sourceData);
		this.factoryData.use(data.factoryData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt das {@link Schema} zurück.<br>
	 * Wenn über {@link #useSchema(Schema)} noch kein {@link Schema} gesetzt wurden, werden über {@link SchemaFactory#newSchema(Source)} ein neues erstellt und
	 * über {@link #useSchema(Schema)} gesetzt. Wenn über {@link #openSourceData()} keine Quelldaten konfiguriert sind, wird {@code null} geliefert.
	 * 
	 * @return {@link Schema} oder {@code null}.
	 * @throws SAXException Wenn {@link SchemaFactory#newSchema(Source)} eine entsprechende Ausnahme auslöst.
	 */
	public Schema getSchema() throws SAXException {
		Schema result = this.schema;
		if (result != null) return result;
		final Source source = this.sourceData.build();
		if (source == null) return null;
		result = this.factoryData.build().newSchema(source);
		this.useSchema(result);
		return result;
	}

	/**
	 * Diese Methode setzt das {@link Schema} und gibt {@code this} zurück.
	 * 
	 * @param schema {@link Schema} oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useSchema(final Schema schema) {
		this.schema = schema;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt das {@link Schema} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useSchema(Schema)
	 * @return {@code this}.
	 */
	public GThiz resetSchema() {
		return this.useSchema(null);
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Schemadaten (z.B. xsd-Datei) und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator.
	 */
	public SourceData openSourceData() {
		return this.sourceData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link SchemaFactory} und gibt ihn zurück.
	 * 
	 * @return Konfigurator.
	 */
	public SchemaFactoryData openFactoryData() {
		return this.factoryData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected abstract GThiz thiz();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Schema build() throws IllegalStateException {
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
	public String toString() {
		return Objects.toStringCall(this, this.sourceData, this.factoryData);
	}

}