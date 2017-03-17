package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Objects;
import bee.creative.xml.BaseTemplatesData.FactoryData;

/** Diese Klasse implementiert einen abstrakten Konfigurator für einen {@link Transformer}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseTransformerData<GThis> extends BaseBuilder<Transformer, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die Ausgabeeigenschaften eines {@link Transformer}.
	 *
	 * @see Transformer#setOutputProperty(String, String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder<String, String, PropertyData<GOwner>> {

		/** Diese Methode wählt {@link OutputKeys#INDENT} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forINDENT() {
			return this.forKey(OutputKeys.INDENT);
		}

		/** Diese Methode wählt {@link OutputKeys#VERSION} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forVERSION() {
			return this.forKey(OutputKeys.VERSION);
		}

		/** Diese Methode wählt {@link OutputKeys#METHOD} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forMETHOD() {
			return this.forKey(OutputKeys.METHOD);
		}

		/** Diese Methode wählt {@link OutputKeys#ENCODING} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forENCODING() {
			return this.forKey(OutputKeys.ENCODING);
		}

		/** Diese Methode wählt {@link OutputKeys#MEDIA_TYPE} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forMEDIA_TYPE() {
			return this.forKey(OutputKeys.MEDIA_TYPE);
		}

		/** Diese Methode wählt {@link OutputKeys#STANDALONE} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forSTANDALONE() {
			return this.forKey(OutputKeys.STANDALONE);
		}

		/** Diese Methode wählt {@link OutputKeys#OMIT_XML_DECLARATION} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forOMIT_XML_DECLARATION() {
			return this.forKey(OutputKeys.OMIT_XML_DECLARATION);
		}

		/** Diese Methode wählt {@link OutputKeys#CDATA_SECTION_ELEMENTS} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public final PropertyData<GOwner> forCDATA_SECTION_ELEMENTS() {
			return this.forKey(OutputKeys.CDATA_SECTION_ELEMENTS);
		}

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final PropertyData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Parameter eines {@link Transformer}.
	 *
	 * @see Transformer#setParameter(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ParameterData<GOwner> extends BaseMapBuilder<String, Object, ParameterData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeParameterData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final ParameterData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link Templates}.
	 *
	 * @see Templates#newTransformer()
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class TemplatesData<GOwner> extends BaseTemplatesData<TemplatesData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeTemplatesData();

		{}

		/** {@inheritDoc} */
		@Override
		protected final TemplatesData<GOwner> customThis() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert den {@link Transformer}. */
	Transformer transformer;

	/** Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}. */
	final PropertyData<GThis> propertyData = new PropertyData<GThis>() {

		@Override
		public final GThis closePropertyData() {
			return BaseTransformerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openParameterData()}. */
	final ParameterData<GThis> parameterData = new ParameterData<GThis>() {

		@Override
		public final GThis closeParameterData() {
			return BaseTransformerData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openTemplatesData()}. */
	final TemplatesData<GThis> templatesData = new TemplatesData<GThis>() {

		@Override
		public final GThis closeTemplatesData() {
			return BaseTransformerData.this.customThis();
		}

	};

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseTransformerData<?> data) {
		if (data == null) return this.customThis();
		this.transformer = data.transformer;
		this.propertyData.use(data.propertyData);
		this.parameterData.use(data.parameterData);
		this.templatesData.use(data.templatesData);
		return this.customThis();
	}

	/** Diese Methode gibt den {@link Transformer} zurück.<br>
	 * Wenn über {@link #useTransformer(Transformer)} noch kein {@link Transformer} gesetzt wurde, wird über {@link Templates#newTransformer()} bzw.
	 * {@link TransformerFactory#newTransformer()} ein neuer erstellt, über {@link #useTransformer(Transformer)} gesetzt und über {@link #updateTransformer()}
	 * aktualisiert. Für die Erstellung werden entweder die {@link Templates} oder die {@link TransformerFactoryConfigurationError} genutzt, die in
	 * {@link #openTemplatesData()} konfiguriert sind. Die {@link TransformerFactory} wird nur dann verwendet, wenn die {@link Templates} {@code null} sind.
	 *
	 * @see #useTransformer(Transformer)
	 * @see #updateTransformer()
	 * @return {@link Transformer}.
	 * @throws TransformerConfigurationException Wenn {@link FactoryData#getFactory()}, {@link TemplatesData#getTemplates()}, {@link Templates#newTransformer()}
	 *         bzw. {@link TransformerFactory#newTransformer()} eine entsprechende Ausnahme auslöst. */
	public final Transformer getTransformer() throws TransformerConfigurationException {
		Transformer result = this.transformer;
		if (result != null) return result;
		final Templates templates = this.templatesData.getTemplates();
		final TransformerFactory factory = this.templatesData.openFactoryData().getFactory();
		result = templates != null ? templates.newTransformer() : factory.newTransformer();
		this.useTransformer(result);
		this.updateTransformer();
		return result;
	}

	/** Diese Methode setzt den {@link Transformer} und gibt {@code this} zurück.
	 *
	 * @param transformer {@link Transformer} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useTransformer(final Transformer transformer) {
		this.transformer = transformer;
		return this.customThis();
	}

	/** Diese Methode setzt den {@link Transformer} auf {@code null} und gibt {@code this} zurück.
	 *
	 * @return {@code this}. */
	public final GThis resetTransformer() {
		return this.useTransformer(null);
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link Transformer} und gibt {@code this} zurück.<br>
	 * Bei dieser Aktualisierung werden auf den über {@link #getTransformer()} ermittelten {@link Transformer} die Einstellungen übertragen, die in
	 * {@link #openPropertyData()} und {@link #openParameterData()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws TransformerConfigurationException Wenn {@link #getTransformer()} eine entsprechende Ausnahme auslöst. */
	public final GThis updateTransformer() throws TransformerConfigurationException {
		final Transformer result = this.getTransformer();
		for (final Entry<String, String> entry: this.propertyData) {
			result.setOutputProperty(entry.getKey(), entry.getValue());
		}
		for (final Entry<String, Object> entry: this.parameterData) {
			result.setParameter(entry.getKey(), entry.getValue());
		}
		return this.customThis();
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabeeigenschaften und gibt ihn zurück.
	 *
	 * @see Transformer#setOutputProperty(String, String)
	 * @return Konfigurator. */
	public final PropertyData<GThis> openPropertyData() {
		return this.propertyData;
	}

	/** Diese Methode öffnet den Konfigurator für die Parameter und gibt ihn zurück.
	 *
	 * @see Transformer#setParameter(String, Object)
	 * @return Konfigurator. */
	public final ParameterData<GThis> openParameterData() {
		return this.parameterData;
	}

	/** Diese Methode öffnet den Konfigurator für die {@link Templates} und gibt ihn zurück.
	 *
	 * @see Templates#newTransformer()
	 * @see TransformerFactory#newTransformer()
	 * @return Konfigurator. */
	public final TemplatesData<GThis> openTemplatesData() {
		return this.templatesData;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getTransformer() */
	@Override
	public final Transformer build() throws IllegalStateException {
		try {
			return this.getTransformer();
		} catch (final TransformerConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.propertyData, this.parameterData, this.templatesData);
	}

}