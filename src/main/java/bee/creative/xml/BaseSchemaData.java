package bee.creative.xml;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import bee.creative.util.Objects;
import bee.creative.util.Producers.BaseBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link Schema}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseSchemaData<GThis> extends BaseBuilder<Schema, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die Schemadaten eines {@link Schema}.
	 *
	 * @see SchemaFactory#newSchema(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class SourceData<GOwner> extends BaseSourceData<SourceData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeSourceData();

		/** {@inheritDoc} */
		@Override
		protected final SourceData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator einer {@link SchemaFactory}.
	 *
	 * @see SchemaFactory#newSchema(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FactoryData<GOwner> extends BaseSchemaFactoryData<FactoryData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFactoryData();

		/** {@inheritDoc} */
		@Override
		protected final FactoryData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert das {@link Schema}. */
	Schema result;

	/** Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}. */
	final FactoryData<GThis> factoryData = new FactoryData<GThis>() {

		@Override
		public final GThis closeFactoryData() {
			return BaseSchemaData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator {@link #openSourceData()}. */
	final SourceData<GThis> sourceData = new SourceData<GThis>() {

		@Override
		public final GThis closeSourceData() {
			return BaseSchemaData.this.customThis();
		}

	};

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseSchemaData<?> data) {
		if (data == null) return this.customThis();
		this.result = data.result;
		this.sourceData.use(data.sourceData);
		this.factoryData.use(data.factoryData);
		return this.customThis();
	}

	/** Diese Methode gibt das {@link Schema} zurück.<br>
	 * Wenn über {@link #useSchema(Schema)} noch kein {@link Schema} gesetzt wurden, werden über {@link SchemaFactory#newSchema(Source)} ein neues erstellt und
	 * über {@link #useSchema(Schema)} gesetzt. Die zur Erstellung verwendeten Quelldaten können über {@link #openSourceData()} konfiguriert werden. Wenn diese
	 * {@code null} sind, wird {@code null} geliefert.
	 *
	 * @see #useSchema(Schema)
	 * @return {@link Schema} oder {@code null}.
	 * @throws SAXException Wenn {@link SchemaFactory#newSchema(Source)} eine entsprechende Ausnahme auslöst. */
	public final Schema getSchema() throws SAXException {
		Schema result = this.result;
		if (result != null) return result;
		final Source source = this.sourceData.getSource();
		if (source == null) return null;
		result = this.factoryData.getFactory().newSchema(source);
		this.useSchema(result);
		return result;
	}

	/** Diese Methode setzt das {@link Schema} und gibt {@code this} zurück.
	 *
	 * @param schema {@link Schema} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useSchema(final Schema schema) {
		this.result = schema;
		return this.customThis();
	}

	/** Diese Methode setzt das {@link Schema} auf {@code null} und gibt {@code this} zurück. Wenn {@link #getSchema()} {@code null} liefern soll, müssen die
	 * {@link #openSourceData() Quelldaten} ebenfalls {@link SourceData#resetSource() rückgesetzt} werden.
	 *
	 * @see #useSchema(Schema)
	 * @return {@code this}. */
	public final GThis resetSchema() {
		return this.useSchema(null);
	}

	/** Diese Methode öffnet den Konfigurator für die Schemadaten (z.B. xsd-Datei) und gibt ihn zurück.
	 *
	 * @see SchemaFactory#newSchema(Source)
	 * @return Konfigurator. */
	public final SourceData<GThis> openSourceData() {
		return this.sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für die {@link SchemaFactory} und gibt ihn zurück.
	 *
	 * @see SchemaFactory#newSchema(Source)
	 * @return Konfigurator. */
	public final FactoryData<GThis> openFactoryData() {
		return this.factoryData;
	}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getSchema() */
	@Override
	public final Schema get() throws IllegalStateException {
		try {
			return this.getSchema();
		} catch (final SAXException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.factoryData);
	}

}