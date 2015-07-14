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
	 */
	public final class ScriptData extends BaseSourceData<ScriptData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeSourceData() {
			return BaseTemplatesData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ScriptData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link TransformerFactory}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class FactoryData extends BaseTransformerFactoryData<FactoryData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closeFactoryData() {
			return BaseTemplatesData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected FactoryData thiz() {
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
	final ScriptData scriptData = //
		new ScriptData();

	/**
	 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
	 */
	final FactoryData factoryData = //
		new FactoryData();

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
	 * erstellt und über {@link #useTemplates(Templates)} gesetzt. Wenn über {@link #openScriptData()} keine Quelldaten konfiguriert sind oder über
	 * {@link #resetTemplates()} die {@link Templates} auf {@code null} gesetzt wurden, wird {@code null} geliefert.
	 * 
	 * @return {@link Templates} oder {@code null}.
	 * @throws TransformerConfigurationException Wenn {@link TransformerFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
	 */
	public Templates getTemplates() throws TransformerConfigurationException {
		Templates result = this.templates;
		if (result != null) return result;
		final Source source = this.scriptData.build();
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
	 * Diese Methode leert die {@link Templates} und gibt {@code this} zurück.<br>
	 * Wenn {@link #getTemplates()} {@code null} liefern sollen, müssen die {@link #openScriptData() Transformationsdaten} ebenfalls geleert werden.
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
	public ScriptData openScriptData() {
		return this.scriptData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory} und gibt ihn zurück.
	 * 
	 * @return Konfigurator.
	 */
	public FactoryData openFactoryData() {
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
		return Objects.toStringCall(this, this.scriptData, this.factoryData);
	}

}