package bee.creative.xml;

import java.util.Map.Entry;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link XPathFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class XPathFactoryBuilder<GOwner> extends BaseValueBuilder<XPathFactory, GOwner> {

	public static abstract class Value<GOwner> extends XPathFactoryBuilder<GOwner> {

		XPathFactory value;

		ModelValue model = new ModelValue();

		FeaturesValue features = new FeaturesValue();

		VariableValue variable = new VariableValue();

		FunctionValue function = new FunctionValue();

		@Override
		public XPathFactory get() {
			return this.value;
		}

		@Override
		public void set(final XPathFactory value) {
			this.value = value;
		}

		@Override
		public ModelValue model() {
			return this.model;
		}

		@Override
		public FeaturesValue features() {
			return this.features;
		}

		@Override
		public VariableValue variable() {
			return this.variable;
		}

		@Override
		public FunctionValue function() {
			return this.function;
		}

	}

	public static abstract class Proxy<GOwner> extends XPathFactoryBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public XPathFactory get() {
			return this.value().get();
		}

		@Override
		public void set(final XPathFactory value) {
			this.value().set(value);
		}

		@Override
		public ModelValue model() {
			return this.value().model();
		}

		@Override
		public FeaturesValue features() {
			return this.value().features();
		}

		@Override
		public VariableValue variable() {
			return this.value().variable();
		}

		@Override
		public FunctionValue function() {
			return this.value().function();
		}

	}

	public static class ModelValue extends ModelBuilder<ModelValue> {

		ModelValue() {
			this.useDEFAULT_OBJECT_MODEL_URI();
		}

		String value;

		@Override
		public String get() {
			return this.value;
		}

		@Override
		public void set(final String value) {
			this.value = value;
		}

		@Override
		public ModelValue owner() {
			return this;
		}

	}

	public class ModelProxy extends ModelBuilder<GOwner> {

		@Override
		public String get() {
			return null;
		}

		@Override
		public void set(final String value) {
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für das Objektmodel einer {@link XPathFactory}. Initialisiert wird dieses über
	 * {@link #useDEFAULT_OBJECT_MODEL_URI()}.
	 *
	 * @see XPathFactory#newInstance(String)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ModelBuilder<GOwner> extends BaseValueBuilder<String, GOwner> {

		/** Diese Methode setzt den Wert auf {@link XPathFactory#DEFAULT_OBJECT_MODEL_URI} und gibt {@code this} zurück. */
		public GOwner useDEFAULT_OBJECT_MODEL_URI() {
			return super.useValue(XPathFactory.DEFAULT_OBJECT_MODEL_URI);
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link XPathFactory}.
	 *
	 * @see XPathFactory#setFeature(String, boolean) */
	public static class FeaturesValue extends FeaturesBuilder.Value<FeaturesValue> {

		@Override
		public FeaturesValue owner() {
			return this;
		}

	}

	public class FeaturesProxy extends FeaturesBuilder.Proxy<GOwner> {

		@Override
		protected FeaturesValue value() {
			return XPathFactoryBuilder.this.features();
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	public static class VariableValue extends VariableBuilder<VariableValue> {

		@Override
		public XPathVariableResolver get() {
			return null;
		}

		@Override
		public void set(final XPathVariableResolver value) {
		}

		@Override
		public VariableValue owner() {
			return null;
		}

	}

	public class VariableProxy extends VariableBuilder<GOwner> {

		@Override
		public XPathVariableResolver get() {
			return null;
		}

		@Override
		public void set(final XPathVariableResolver value) {
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver}.
	 *
	 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class VariableBuilder<GOwner> extends BaseValueBuilder<XPathVariableResolver, GOwner> {

	}

	public static class FunctionValue extends FunctionBuilder<FunctionValue> {

		@Override
		public XPathFunctionResolver get() {
			return null;
		}

		@Override
		public void set(final XPathFunctionResolver value) {
		}

		@Override
		public FunctionValue owner() {
			return null;
		}

	}

	public class FunctionProxy extends FunctionBuilder<GOwner> {

		@Override
		public XPathFunctionResolver get() {
			return null;
		}

		@Override
		public void set(final XPathFunctionResolver value) {
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver}.
	 *
	 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FunctionBuilder<GOwner> extends BaseValueBuilder<XPathFunctionResolver, GOwner> {

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final XPathFactoryBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forModel().use(that.model());
		this.forFeatures().use(that.features());
		this.forVariable().use(that.variable());
		this.forFunction().use(that.function());
		return this.owner();
	}

	/** Diese Methode gibt das {@link XPathFactory} zurück. Wenn über {@link #useValue(Object)} noch keine {@link XPathFactory} gesetzt wurde, wird über
	 * {@link XPathFactory#newInstance(String)} eine neue erstellt, über {@link #useValue(Object)} gesetzt und über {@link #updateValue()} aktualisiert. Das zur
	 * Erstellung verwendete Objektmodell kann über {@link #model()} konfiguriert werden.
	 *
	 * @return {@link XPathFactory}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactory#newInstance(String)} bzw. {@link #updateValue()} eine entsprechende Ausnahme
	 *         auslöst. */
	public XPathFactory putValue() throws XPathFactoryConfigurationException {
		XPathFactory result = this.getValue();
		if (result != null) return result;
		result = XPathFactory.newInstance(this.model().get());
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link XPathFactory} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf die über
	 * {@link #putValue()} ermittelte {@link XPathFactory} die Einstellungen übertragen, die in {@link #variable()}, {@link #function()} und {@link #features()}
	 * konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactory#setFeature(String, boolean)} bzw. {@link #putValue()} eine entsprechende Ausnahme
	 *         auslöst. */
	public GOwner updateValue() throws XPathFactoryConfigurationException {
		final XPathFactory factory = this.putValue();
		for (final XPathVariableResolver value: this.variable()) {
			factory.setXPathVariableResolver(value);
		}
		for (final XPathFunctionResolver value: this.function()) {
			factory.setXPathFunctionResolver(value);
		}
		for (final Entry<String, Boolean> entry: this.features()) {
			factory.setFeature(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für das Objektmodel und gibt ihn zurück.
	 *
	 * @see XPathFactory#newInstance(String)
	 * @return Konfigurator. */
	public abstract ModelValue model();

	/** Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 *
	 * @see XPathFactory#setFeature(String, boolean)
	 * @return Konfigurator. */
	public abstract FeaturesValue features();

	/** Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 *
	 * @see XPathFactory#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator. */
	public abstract VariableValue variable();

	/** Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 *
	 * @see XPathFactory#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator. */
	public abstract FunctionValue function();

	public ModelProxy forModel() {
		return new ModelProxy();
	}

	public FeaturesProxy forFeatures() {
		return new FeaturesProxy();
	}

	public VariableProxy forVariable() {
		return new VariableProxy();
	}

	public FunctionProxy forFunction() {
		return new FunctionProxy();
	}

	@Override
	public abstract GOwner owner();

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.model(), this.features(), this.variable(), this.function());
	}

}