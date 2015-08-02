package bee.creative.xml;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert den Konfigurator für die {@link Templates} zur Erzeugung eines {@link Transformer}.
 * 
 * @see TransformerFactory#newTemplates(Source)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseTemplatesData<GThiz> extends BaseBuilder<Templates, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Transformationsdaten eines {@link Templates}.
	 * 
	 * @see TransformerFactory#newTemplates(Source)
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
		protected SourceData<GOwner> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link TransformerFactory}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers.
	 */
	public static abstract class FactoryData<GOwner> extends BaseTransformerFactoryData<FactoryData<GOwner>> {

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
		protected FactoryData<GOwner> thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert die {@link Templates}.
	 */
	Templates templates;

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openScriptData()}.
	 */
	final SourceData<GThiz> scriptData = new SourceData<GThiz>() {

		@Override
		public GThiz closeSourceData() {
			return BaseTemplatesData.this.thiz();
		}

	};

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
	 */
	final FactoryData<GThiz> factoryData = new FactoryData<GThiz>() {

		@Override
		public GThiz closeFactoryData() {
			return BaseTemplatesData.this.thiz();
		}

	};

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseTemplatesData<?> data) {
		if (data == null) return this.thiz();
		this.templates = data.templates;
		this.scriptData.use(data.scriptData);
		this.factoryData.use(data.factoryData);
		return this.thiz();
	}

	/**
	 * Diese Methode gibt die {@link Templates} zurück.<br>
	 * Wenn über {@link #useTemplates(Templates)} noch keine {@link Templates} gesetzt wurden, werden über {@link TransformerFactory#newTemplates(Source)} neue
	 * erstellt und über {@link #useTemplates(Templates)} gesetzt. Die zur erstellung verwendeten Quelldaten können über {@link #openScriptData()} konfiguriert
	 * werden. Wenn diese {@code null} sind, wird {@code null} geliefert.
	 * 
	 * @see #useTemplates(Templates)
	 * @return {@link Templates} oder {@code null}.
	 * @throws TransformerConfigurationException Wenn {@link FactoryData#getFactory()} bzw. {@link TransformerFactory#newTemplates(Source)} eine entsprechende
	 *         Ausnahme auslöst.
	 */
	public Templates getTemplates() throws TransformerConfigurationException {
		Templates result = this.templates;
		if (result != null) return result;
		final Source source = this.scriptData.getSource();
		if (source == null) return null;
		result = this.factoryData.getFactory().newTemplates(source);
		this.useTemplates(result);
		return result;
	}

	/**
	 * Diese Methode setzt die {@link Templates} und gibt {@code this} zurück.
	 * 
	 * @param templates {@link Templates} oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useTemplates(final Templates templates) {
		this.templates = templates;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt die {@link Templates} auf {@code null} und gibt {@code this} zurück. Wenn {@link #getTemplates()} {@code null} liefern soll, müssen die
	 * {@link #openScriptData() Transformationsdaten} ebenfalls {@link SourceData#resetSource() rückgesetzt} werden.
	 * 
	 * @return {@code this}.
	 */
	public GThiz resetTemplates() {
		return this.useTemplates(null);
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Transformationsdaten (z.B. xsl-Datei) und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator.
	 */
	public SourceData<GThiz> openScriptData() {
		return this.scriptData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory} und gibt ihn zurück.
	 * 
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator.
	 */
	public FactoryData<GThiz> openFactoryData() {
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
	 * 
	 * @see #getTemplates()
	 */
	@Override
	public Templates build() throws IllegalStateException {
		try {
			return this.getTemplates();
		} catch (final TransformerConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.scriptData, this.factoryData);
	}

}