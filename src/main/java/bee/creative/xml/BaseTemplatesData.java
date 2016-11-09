package bee.creative.xml;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/** Diese Klasse implementiert den Konfigurator für die {@link Templates} zur Erzeugung eines {@link Transformer}.
 *
 * @see TransformerFactory#newTemplates(Source)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseTemplatesData<GThis> extends BaseBuilder<Templates, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die Transformationsdaten eines {@link Templates}.
	 *
	 * @see TransformerFactory#newTemplates(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class SourceData<GOwner> extends BaseSourceData<SourceData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeSourceData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final SourceData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link TransformerFactory}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FactoryData<GOwner> extends BaseTransformerFactoryData<FactoryData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeFactoryData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final FactoryData<GOwner> _this_() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert die {@link Templates}. */
	Templates _templates_;

	/** Dieses Feld speichert den Konfigurator für {@link #openScriptData()}. */
	final SourceData<GThis> _scriptData_ = new SourceData<GThis>() {

		@Override
		public final GThis closeSourceData() {
			return BaseTemplatesData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}. */
	final FactoryData<GThis> _factoryData_ = new FactoryData<GThis>() {

		@Override
		public final GThis closeFactoryData() {
			return BaseTemplatesData.this._this_();
		}

	};

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseTemplatesData<?> data) {
		if (data == null) return this._this_();
		this._templates_ = data._templates_;
		this._scriptData_.use(data._scriptData_);
		this._factoryData_.use(data._factoryData_);
		return this._this_();
	}

	/** Diese Methode gibt die {@link Templates} zurück.<br>
	 * Wenn über {@link #useTemplates(Templates)} noch keine {@link Templates} gesetzt wurden, werden über {@link TransformerFactory#newTemplates(Source)} neue
	 * erstellt und über {@link #useTemplates(Templates)} gesetzt. Die zur Erstellung verwendeten Quelldaten können über {@link #openScriptData()} konfiguriert
	 * werden. Wenn diese {@code null} sind, wird {@code null} geliefert.
	 *
	 * @see #useTemplates(Templates)
	 * @return {@link Templates} oder {@code null}.
	 * @throws TransformerConfigurationException Wenn {@link FactoryData#getFactory()} bzw. {@link TransformerFactory#newTemplates(Source)} eine entsprechende
	 *         Ausnahme auslöst. */
	public final Templates getTemplates() throws TransformerConfigurationException {
		Templates result = this._templates_;
		if (result != null) return result;
		final Source source = this._scriptData_.getSource();
		if (source == null) return null;
		result = this._factoryData_.getFactory().newTemplates(source);
		this.useTemplates(result);
		return result;
	}

	/** Diese Methode setzt die {@link Templates} und gibt {@code this} zurück.
	 *
	 * @param templates {@link Templates} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useTemplates(final Templates templates) {
		this._templates_ = templates;
		return this._this_();
	}

	/** Diese Methode setzt die {@link Templates} auf {@code null} und gibt {@code this} zurück. Wenn {@link #getTemplates()} {@code null} liefern soll, müssen
	 * die {@link #openScriptData() Transformationsdaten} ebenfalls {@link SourceData#resetSource() rückgesetzt} werden.
	 *
	 * @return {@code this}. */
	public final GThis resetTemplates() {
		return this.useTemplates(null);
	}

	/** Diese Methode öffnet den Konfigurator für die Transformationsdaten (z.B. xsl-Datei) und gibt ihn zurück.
	 *
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator. */
	public final SourceData<GThis> openScriptData() {
		return this._scriptData_;
	}

	/** Diese Methode öffnet den Konfigurator für die {@link TransformerFactory} und gibt ihn zurück.
	 *
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator. */
	public final FactoryData<GThis> openFactoryData() {
		return this._factoryData_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis _this_();

	/** {@inheritDoc}
	 *
	 * @see #getTemplates() */
	@Override
	public final Templates build() throws IllegalStateException {
		try {
			return this.getTemplates();
		} catch (final TransformerConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._scriptData_, this._factoryData_);
	}

}