package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert einen abstrakten Konfigurator für einen {@link Transformer}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class TransformerBuilder<GOwner> extends BaseValueBuilder<Transformer, GOwner> {

	public static abstract class Value<GOwner> extends TransformerBuilder<GOwner> {

		Transformer value;

		PropertiesValue properties = new PropertiesValue();

		ParametersValue parameters = new ParametersValue();

		TemplatesValue templates = new TemplatesValue();

		@Override
		public Transformer get() {
			return this.value;
		}

		@Override
		public void set(final Transformer value) {
			this.value = value;
		}

		@Override
		public PropertiesValue properties() {
			return this.properties;
		}

		@Override
		public ParametersValue parameters() {
			return this.parameters;
		}

		@Override
		public TemplatesValue templates() {
			return this.templates;
		}

	}

	public static abstract class Proxy<GOwner> extends TransformerBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Transformer get() {
			return this.value().get();
		}

		@Override
		public void set(final Transformer value) {
			this.value().set(value);
		}

		@Override
		public PropertiesValue properties() {
			return this.value().properties();
		}

		@Override
		public ParametersValue parameters() {
			return this.value().parameters();
		}

		@Override
		public TemplatesValue templates() {
			return this.value().templates();
		}

	}

	public static class PropertiesValue extends PropertiesBuilder<PropertiesValue> {

		Map<String, String> value = new HashMap<>();

		@Override
		public Map<String, String> get() {
			return this.value;
		}

		@Override
		public PropertiesValue owner() {
			return this;
		}

	}

	public class PropertiesProxy extends PropertiesBuilder<GOwner> {

		@Override
		public Map<String, String> get() {
			return TransformerBuilder.this.properties().get();
		}

		@Override
		public GOwner owner() {
			return TransformerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Ausgabeeigenschaften eines {@link Transformer}.
	 *
	 * @see Transformer#setOutputProperty(String, String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertiesBuilder<GOwner> extends BaseMapBuilder<String, String, Map<String, String>, GOwner> {

		/** Diese Methode wählt {@link OutputKeys#INDENT} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forINDENT() {
			return this.forKey(OutputKeys.INDENT);
		}

		/** Diese Methode wählt {@link OutputKeys#VERSION} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forVERSION() {
			return this.forKey(OutputKeys.VERSION);
		}

		/** Diese Methode wählt {@link OutputKeys#METHOD} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forMETHOD() {
			return this.forKey(OutputKeys.METHOD);
		}

		/** Diese Methode wählt {@link OutputKeys#ENCODING} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forENCODING() {
			return this.forKey(OutputKeys.ENCODING);
		}

		/** Diese Methode wählt {@link OutputKeys#MEDIA_TYPE} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forMEDIA_TYPE() {
			return this.forKey(OutputKeys.MEDIA_TYPE);
		}

		/** Diese Methode wählt {@link OutputKeys#STANDALONE} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forSTANDALONE() {
			return this.forKey(OutputKeys.STANDALONE);
		}

		/** Diese Methode wählt {@link OutputKeys#OMIT_XML_DECLARATION} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forOMIT_XML_DECLARATION() {
			return this.forKey(OutputKeys.OMIT_XML_DECLARATION);
		}

		/** Diese Methode wählt {@link OutputKeys#CDATA_SECTION_ELEMENTS} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @return {@code this}. */
		public ValueProxy forCDATA_SECTION_ELEMENTS() {
			return this.forKey(OutputKeys.CDATA_SECTION_ELEMENTS);
		}

	}

	public static class ParametersValue extends ParametersBuilder<ParametersValue> {

		Map<String, Object> value = new HashMap<>();

		@Override
		public Map<String, Object> get() {
			return this.value;
		}

		@Override
		public ParametersValue owner() {
			return this;
		}

	}

	public class ParametersProxy extends ParametersBuilder<GOwner> {

		@Override
		public Map<String, Object> get() {
			return TransformerBuilder.this.parameters().get();
		}

		@Override
		public GOwner owner() {
			return TransformerBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Parameter eines {@link Transformer}.
	 *
	 * @see Transformer#setParameter(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ParametersBuilder<GOwner> extends BaseMapBuilder<String, Object, Map<String, Object>, GOwner> {

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link Templates}.
	 *
	 * @see Templates#newTransformer() */
	public static class TemplatesValue extends TemplatesBuilder.Value<TemplatesValue> {

		@Override
		public TemplatesValue owner() {
			return this;
		}

	}

	public class TemplatesProxy extends TemplatesBuilder.Proxy<GOwner> {

		@Override
		protected TemplatesValue value() {
			return TransformerBuilder.this.templates();
		}

		@Override
		public GOwner owner() {
			return TransformerBuilder.this.owner();
		}

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final TransformerBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forProperties().use(that.properties());
		this.forParameters().use(that.parameters());
		this.forTemplates().use(that.templates());
		return this.owner();
	}

	/** Diese Methode gibt den {@link Transformer} zurück. Wenn über {@link #useValue(Object)} noch kein {@link Transformer} gesetzt wurde, wird über
	 * {@link Templates#newTransformer()} bzw. {@link TransformerFactory#newTransformer()} ein neuer erstellt, über {@link #useValue(Object)} gesetzt und über
	 * {@link #updateValue()} aktualisiert. Für die Erstellung werden entweder die {@link Templates} oder die {@link TransformerFactoryConfigurationError}
	 * genutzt, die in {@link #templates()} konfiguriert sind. Die {@link TransformerFactory} wird nur dann verwendet, wenn die {@link Templates} {@code null}
	 * sind.
	 *
	 * @see #updateValue()
	 * @return {@link Transformer}.
	 * @throws TransformerConfigurationException Wenn {@link TransformerFactoryBuilder#putValue()}, {@link TemplatesBuilder#putValue()},
	 *         {@link Templates#newTransformer()} bzw. {@link TransformerFactory#newTransformer()} eine entsprechende Ausnahme auslöst. */
	public Transformer putValue() throws TransformerConfigurationException {
		Transformer result = this.getValue();
		if (result != null) return result;
		final Templates templates = this.templates().putValue();
		result = templates != null ? templates.newTransformer() : this.templates().factory().putValue().newTransformer();
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link Transformer} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #putValue()} ermittelten {@link Transformer} die Einstellungen übertragen, die in {@link #properties()} und {@link #parameters()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws TransformerConfigurationException Wenn {@link #putValue()} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws TransformerConfigurationException {
		final Transformer result = this.putValue();
		for (final Entry<String, String> entry: this.properties()) {
			result.setOutputProperty(entry.getKey(), entry.getValue());
		}
		for (final Entry<String, Object> entry: this.parameters()) {
			result.setParameter(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabeeigenschaften und gibt ihn zurück.
	 *
	 * @see Transformer#setOutputProperty(String, String) */
	public abstract PropertiesValue properties();

	public PropertiesProxy forProperties() {
		return new PropertiesProxy();
	}

	/** Diese Methode öffnet den Konfigurator für die Parameter und gibt ihn zurück.
	 *
	 * @see Transformer#setParameter(String, Object)
	 * @return Konfigurator. */
	public abstract ParametersValue parameters();

	public ParametersProxy forParameters() {
		return new ParametersProxy();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link Templates} und gibt ihn zurück.
	 *
	 * @see Templates#newTransformer()
	 * @see TransformerFactory#newTransformer()
	 * @return Konfigurator. */
	public abstract TemplatesValue templates();

	public TemplatesProxy forTemplates() {
		return new TemplatesProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.properties(), this.parameters(), this.templates());
	}

}