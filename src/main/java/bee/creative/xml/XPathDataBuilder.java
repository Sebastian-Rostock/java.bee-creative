package bee.creative.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator für einen {@link XPath}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class XPathDataBuilder<GOwner> extends BaseValueBuilder<XPath, GOwner> {

	/** Diese Klasse implementiert den Konfigurator einer {@link XPathFactory}.
	 *
	 * @see XPathFactory#newXPath() */
	public static class FactoryValue extends XPathFactoryBuilder.Value<FactoryValue> {

		@Override
		public FactoryValue owner() {
			return this;
		}

	}

	public class FactoryProxy extends XPathFactoryBuilder.Proxy<GOwner> {

		@Override
		protected bee.creative.xml.XPathFactoryBuilder.Value<?> value() {
			return null;
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	public static class NamespaceValue extends NamespaceBuilder<NamespaceValue> {

		@Override
		public NamespaceContext get() {
			return null;
		}

		@Override
		public void set(NamespaceContext value) {
		}

		@Override
		public NamespaceValue owner() {
			return null;
		}

	}

	public class NamespaceProxy extends NamespaceBuilder<GOwner> {

		@Override
		public NamespaceContext get() {
			return null;
		}

		@Override
		public void set(NamespaceContext value) {
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link NamespaceContext}.
	 *
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class NamespaceBuilder<GOwner> extends BaseValueBuilder<NamespaceContext, GOwner> {

	}

	public static class VariableValue extends VariableBuilder<VariableValue> {

		@Override
		public XPathVariableResolver get() {
			return null;
		}

		@Override
		public void set(XPathVariableResolver value) {
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
		public void set(XPathVariableResolver value) {
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathVariableResolver}.
	 *
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class VariableBuilder<GOwner> extends BaseValueBuilder<XPathVariableResolver, GOwner> {

	}

	public static class FunctionValue extends FunctionBuilder<FunctionValue> {

		@Override
		public XPathFunctionResolver get() {
			return null;
		}

		@Override
		public void set(XPathFunctionResolver value) {
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
		public void set(XPathFunctionResolver value) {
		}

		@Override
		public GOwner owner() {
			return null;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link XPathFunctionResolver}.
	 *
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class FunctionBuilder<GOwner> extends BaseValueBuilder<XPathFunctionResolver, GOwner> {

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(XPathDataBuilder<?> data) {
		if (data == null) return this.owner();
		this.useValue(data.getValue());
		this.forFacrory().use(data.facrory());
		this.forVariable().use(data.variable());
		this.forFunction().use(data.function());
		this.forNamespace().use(data.namespace());
		return this.owner();
	}

	/** Diese Methode gibt das {@link XPath} zurück. Wenn über {@link #useValue(Object)} noch keine {@link XPath} gesetzt wurde, wird über
	 * {@link XPathFactory#newXPath()} eine neue erstellt, über {@link #useValue(Object)} gesetzt und über {@link #updateValue()} aktualisiert. Die zur Erstellung
	 * verwendete {@link XPathFactory} kann über {@link #facrory()} konfiguriert werden.
	 *
	 * @see #updateValue()
	 * @return {@link XPath}.
	 * @throws XPathFactoryConfigurationException Wenn {@link XPathFactoryBuilder#putValue()} bzw. {@link XPathFactory#newXPath()} eine entsprechende Ausnahme auslöst. */
	public XPath getXPath() throws XPathFactoryConfigurationException {
		XPath result = this.getValue();
		if (result != null) return result;
		result = this.facrory().putValue().newXPath();
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen des {@link XPath} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf den über
	 * {@link #getXPath()} ermittelten {@link XPath} die Einstellungen übertragen, die in {@link #namespace()}, {@link #variable()} und {@link #function()}
	 * konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws XPathFactoryConfigurationException Wenn {@link #getXPath()} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws XPathFactoryConfigurationException {
		XPath factory = this.getXPath();
		for (NamespaceContext value: this.namespace()) {
			factory.setNamespaceContext(value);
		}
		for (XPathVariableResolver value: this.variable()) {
			factory.setXPathVariableResolver(value);
		}
		for (XPathFunctionResolver value: this.function()) {
			factory.setXPathFunctionResolver(value);
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für die {@link XPathFactory} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public abstract FactoryValue facrory();

	/** Diese Methode öffnet den Konfigurator für den {@link XPathVariableResolver} und gibt ihn zurück.
	 *
	 * @see XPath#setXPathVariableResolver(XPathVariableResolver)
	 * @return Konfigurator. */
	public abstract VariableValue variable();

	/** Diese Methode öffnet den Konfigurator für den {@link XPathFunctionResolver} und gibt ihn zurück.
	 *
	 * @see XPath#setXPathFunctionResolver(XPathFunctionResolver)
	 * @return Konfigurator. */
	public abstract FunctionValue function();

	/** Diese Methode öffnet den Konfigurator für den {@link NamespaceContext} und gibt ihn zurück.
	 *
	 * @see XPath#setNamespaceContext(NamespaceContext)
	 * @return Konfigurator. */
	public abstract NamespaceValue namespace();

	public FactoryProxy forFacrory() {
		return new FactoryProxy();
	}

	public VariableProxy forVariable() {
		return new VariableProxy();
	}

	public FunctionProxy forFunction() {
		return new FunctionProxy();
	}

	public NamespaceProxy forNamespace() {
		return new NamespaceProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.facrory(), this.namespace(), this.variable(), this.function());
	}

}