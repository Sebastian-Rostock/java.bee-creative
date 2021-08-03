package bee.creative.xml;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.xml.SourceBuilder.Value;

/** Diese Klasse implementiert den Konfigurator für die {@link Templates} zur Erzeugung eines {@link Transformer}.
 *
 * @see TransformerFactory#newTemplates(Source)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class TemplatesBuilder<GOwner> extends BaseValueBuilder<Templates, GOwner> {

	public static abstract class Value<GOwner> extends TemplatesBuilder<GOwner> {

		Templates value;

		ScriptValue script = new ScriptValue();

		FactoryValue factory = new FactoryValue();

		@Override
		public Templates get() {
			return value;
		}

		@Override
		public void set(Templates value) {
			this.value = value;
		}

		@Override
		public ScriptValue script() {
			return script;
		}

		@Override
		public FactoryValue factory() {
			return factory;
		}

	}

	public static abstract class Proxy<GOwner> extends TemplatesBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Templates get() {
			return value().get();
		}

		@Override
		public void set(Templates value) {
			this.value().set(value);
		}

		@Override
		public ScriptValue script() {
			return value().script();
		}

		@Override
		public FactoryValue factory() {
			return value().factory();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Transformationsdaten eines {@link Templates}.
	 *
	 * @see TransformerFactory#newTemplates(Source) */
	public static class ScriptValue extends SourceBuilder.Value<ScriptValue> {

		@Override
		public ScriptValue owner() {
			return this;
		}

	}

	public class ScriptProxy extends SourceBuilder.Proxy<GOwner> {

		@Override
		protected ScriptValue value() {
			return script();
		}

		@Override
		public GOwner owner() {
			return TemplatesBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die {@link TransformerFactory}. */
	public static class FactoryValue extends TransformerFactoryBuilder.Value<FactoryValue> {

		@Override
		public FactoryValue owner() {
			return this;
		}

	}

	public class FactoryProxy extends TransformerFactoryBuilder.Proxy<GOwner> {

		@Override
		protected FactoryValue value() {
			return TemplatesBuilder.this.factory();
		}

		@Override
		public GOwner owner() {
			return TemplatesBuilder.this.owner();
		}

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final TemplatesBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forScript().use(that.script());
		this.forFactory().use(that.factory());
		return this.owner();
	}

	/** Diese Methode gibt die {@link Templates} zurück. Wenn über {@link #useValue(Object)} noch keine {@link Templates} gesetzt wurden, werden über
	 * {@link TransformerFactory#newTemplates(Source)} neue erstellt und über {@link #useValue(Object)} gesetzt. Die zur Erstellung verwendeten Quelldaten können
	 * über {@link #script()} konfiguriert werden. Wenn diese {@code null} sind, wird {@code null} geliefert.
	 *
	 * @return {@link Templates} oder {@code null}.
	 * @throws TransformerConfigurationException Wenn {@link FactoryValue#putValue()} bzw. {@link TransformerFactory#newTemplates(Source)} eine entsprechende
	 *         Ausnahme auslöst. */
	public Templates putValue() throws TransformerConfigurationException {
		Templates result = this.getValue();
		if (result != null) return result;
		final Source source = this.script().getValue();
		if (source == null) return null;
		result = this.factory().putValue().newTemplates(source);
		this.useValue(result);
		return result;
	}

	/** Diese Methode öffnet den Konfigurator für die Transformationsdaten (z.B. xsl-Datei) und gibt ihn zurück.
	 *
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator. */
	public abstract ScriptValue script();

	public ScriptProxy forScript() {
		return new ScriptProxy();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link TransformerFactory} und gibt ihn zurück.
	 *
	 * @see TransformerFactory#newTemplates(Source)
	 * @return Konfigurator. */
	public abstract FactoryValue factory();

	public FactoryProxy forFactory() {
		return new FactoryProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.script(), this.factory());
	}

}