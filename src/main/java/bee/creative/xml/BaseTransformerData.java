package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Objects;

public abstract class BaseTransformerData<GThiz> extends BaseBuilder<Transformer, GThiz> {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Ausgabeeigenschaften eines {@link Transformer}.
	 * 
	 * @see Transformer#setOutputProperty(String, String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class PropertyData extends BaseMapBuilder<String, String, PropertyData> {

		/**
		 * Diese Methode wählt {@link OutputKeys#INDENT} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forINDENT() {
			return this.forKey(OutputKeys.INDENT);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#VERSION} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forVERSION() {
			return this.forKey(OutputKeys.VERSION);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#METHOD} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forMETHOD() {
			return this.forKey(OutputKeys.METHOD);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#ENCODING} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forENCODING() {
			return this.forKey(OutputKeys.ENCODING);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#MEDIA_TYPE} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forMEDIA_TYPE() {
			return this.forKey(OutputKeys.MEDIA_TYPE);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#STANDALONE} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forSTANDALONE() {
			return this.forKey(OutputKeys.STANDALONE);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#OMIT_XML_DECLARATION} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forOMIT_XML_DECLARATION() {
			return this.forKey(OutputKeys.OMIT_XML_DECLARATION);
		}

		/**
		 * Diese Methode wählt {@link OutputKeys#CDATA_SECTION_ELEMENTS} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public PropertyData forCDATA_SECTION_ELEMENTS() {
			return this.forKey(OutputKeys.CDATA_SECTION_ELEMENTS);
		}

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GThiz closePropertyData() {
			return BaseTransformerData.this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected PropertyData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Parameter eines {@link Transformer}.
	 * 
	 * @see Transformer#setParameter(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class ParameterData extends BaseMapBuilder<String, Object, ParameterData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public BaseTransformerData closeParameterData() {
			return BaseTransformerData.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ParameterData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die {@link Templates} zur Erzeugung eines {@link Transformer}.
	 * 
	 * @see TransformerFactory#newTemplates(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class TemplatesData extends BaseTemplatesData<TemplatesData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public BaseTransformerData closeTemplatesData() {
			return BaseTransformerData.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected TemplatesData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den {@link Transformer}.
	 */
	Transformer transformer;

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openPropertyData()}.
	 */
	final PropertyData propertyData = //
		new PropertyData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openParameterData()}.
	 */
	final ParameterData parameterData = //
		new ParameterData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openTemplatesData()}.
	 */
	final TemplatesData templatesData = //
		new TemplatesData();

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz use(final BaseTransformerData<?> data) {
		if (data == null) return this.thiz();
		this.transformer = data.transformer;
		this.propertyData.use(data.propertyData);
		this.parameterData.use(data.parameterData);
		this.templatesData.use(data.templatesData);
		return this.thiz();
	}

	public Transformer getTransformer() throws TransformerConfigurationException {
		Transformer result = this.transformer;
		if (result != null) return result;
		final Templates templates = this.templatesData.getTemplates();
		final TransformerFactory factory = this.templatesData.factoryData.getFactory();
		result = templates != null ? templates.newTransformer() : factory.newTransformer();
		this.useTransformer(this.transformer);
		this.updateTransformer();
		return result;
	}

	public GThiz useTransformer(final Transformer transformer) {
		this.transformer = transformer;
		return this.thiz();
	}

	public GThiz updateTransformer() throws TransformerConfigurationException {
		final Transformer result = this.getTransformer();
		for (final Entry<String, Object> entry: this.parameterData) {
			result.setParameter(entry.getKey(), entry.getValue());
		}
		for (final Entry<String, String> entry: this.propertyData) {
			result.setOutputProperty(entry.getKey(), entry.getValue());
		}
		return this.thiz();
	}

	public PropertyData openPropertyData() {
		return this.propertyData;
	}

	public ParameterData openParameterData() {
		return this.parameterData;
	}

	public TemplatesData openTemplatesData() {
		return this.templatesData;
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
	public Transformer build() throws IllegalStateException {
		try {
			return this.getTransformer();
		} catch (final TransformerConfigurationException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.propertyData, this.parameterData, this.templatesData);
	}

}