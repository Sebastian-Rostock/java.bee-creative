package bee.creative.xml;

import java.util.Map;
import java.util.Map.Entry;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link TransformerFactory} zur Erzeugung von {@link Templates} oder eines
 * {@link Transformer}.
 *
 * @see TransformerFactory#newTemplates(Source)
 * @see TransformerFactory#newTransformer()
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class TransformerFactoryBuilder<GOwner> extends BaseValueBuilder<TransformerFactory, GOwner> {

	public static abstract class Value<GOwner> extends TransformerFactoryBuilder<GOwner> {

		TransformerFactory value;

		FeaturesValue features = new FeaturesValue();

		AttributesValue attributes = new AttributesValue();

		ListenerValue listener = new ListenerValue();

		ResolverValue resolver = new ResolverValue();

		@Override
		public TransformerFactory get() {
			return this.value;
		}

		@Override
		public void set(final TransformerFactory value) {
			this.value = value;
		}

		@Override
		public FeaturesValue features() {
			return this.features;
		}

		@Override
		public AttributesValue attributes() {
			return this.attributes;
		}

		@Override
		public ListenerValue listener() {
			return this.listener;
		}

		@Override
		public ResolverValue resolver() {
			return this.resolver;
		}

	}

	public static abstract class Proxy<GOwner> extends TransformerFactoryBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public TransformerFactory get() {
			return this.value().get();
		}

		@Override
		public void set(final TransformerFactory value) {
			this.value().set(value);
		}

		@Override
		public FeaturesValue features() {
			return this.value().features();
		}

		@Override
		public AttributesValue attributes() {
			return this.value().attributes();
		}

		@Override
		public ListenerValue listener() {
			return this.value().listener();
		}

		@Override
		public ResolverValue resolver() {
			return this.value().resolver();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link TransformerFactory}.
	 *
	 * @see TransformerFactory#setFeature(String, boolean) */
	public static class FeaturesValue extends FeaturesBuilder.Value<FeaturesValue> {

		@Override
		public FeaturesValue owner() {
			return this;
		}

	}

	public class FeaturesProxy extends FeaturesBuilder.Proxy<GOwner> {

		@Override
		protected FeaturesValue value() {
			return TransformerFactoryBuilder.this.features();
		}

		@Override
		public GOwner owner() {
			return TransformerFactoryBuilder.this.owner();
		}

	}

	public static class AttributesValue extends AttributesBuilder<AttributesValue> {

		Map<String, String> value = new HashMap<>();

		@Override
		public Map<String, String> get() {
			return this.value;
		}

		@Override
		public AttributesValue owner() {
			return this;
		}

	}

	public class AttributesProxy extends AttributesBuilder<GOwner> {

		@Override
		public Map<String, String> get() {
			return TransformerFactoryBuilder.this.attributes().get();
		}

		@Override
		public GOwner owner() {
			return TransformerFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Attribute einer {@link TransformerFactory}.
	 *
	 * @see TransformerFactory#setAttribute(String, Object)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class AttributesBuilder<GOwner> extends BaseMapBuilder<String, String, Map<String, String>, GOwner> {

	}

	public static class ListenerValue extends ListenerBuilder<ListenerValue> {

		ErrorListener value;

		@Override
		public ErrorListener get() {
			return this.value;
		}

		@Override
		public void set(final ErrorListener value) {
			this.value = value;
		}

		@Override
		public ListenerValue owner() {
			return this;
		}

	}

	public class ListenerProxy extends ListenerBuilder<GOwner> {

		@Override
		public ErrorListener get() {
			return TransformerFactoryBuilder.this.listener().get();
		}

		@Override
		public void set(final ErrorListener value) {
			TransformerFactoryBuilder.this.listener().set(value);
		}

		@Override
		public GOwner owner() {
			return TransformerFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link ErrorListener}.
	 *
	 * @see TransformerFactory#setErrorListener(ErrorListener)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ListenerBuilder<GOwner> extends BaseValueBuilder<ErrorListener, GOwner> {

	}

	public static class ResolverValue extends ResolverBuilder<ResolverValue> {

		URIResolver value;

		@Override
		public URIResolver get() {
			return this.value;
		}

		@Override
		public void set(final URIResolver value) {
			this.value = value;
		}

		@Override
		public ResolverValue owner() {
			return this;
		}

	}

	public class ResolverProxy extends ResolverBuilder<GOwner> {

		@Override
		public URIResolver get() {
			return TransformerFactoryBuilder.this.resolver().get();
		}

		@Override
		public void set(final URIResolver value) {
			TransformerFactoryBuilder.this.resolver().set(value);
		}

		@Override
		public GOwner owner() {
			return TransformerFactoryBuilder.this.owner();
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link URIResolver}.
	 *
	 * @see TransformerFactory#setURIResolver(URIResolver)
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ResolverBuilder<GOwner> extends BaseValueBuilder<URIResolver, GOwner> {

	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public GOwner use(final TransformerFactoryBuilder<?> that) {
		if (that == null) return this.owner();
		this.useValue(that.getValue());
		this.forFeatures().use(that.features());
		this.forAttributes().use(that.attributes());
		this.forListener().use(that.listener());
		this.forResolver().use(that.resolver());
		return this.owner();
	}

	/** Diese Methode gibt die {@link TransformerFactory} zurück. Wenn über {@link #useValue(Object)} noch keine {@link TransformerFactory} gesetzt wurde, wird
	 * über {@link TransformerFactory#newInstance()} eine neue erstellt, über {@link #useValue(Object)} gesetzt und über {@link #updateValue()} aktualisiert.
	 *
	 * @see #updateValue()
	 * @return {@link TransformerFactory}.
	 * @throws TransformerConfigurationException Wenn {@link #updateValue()} eine entsprechende Ausnahme auslöst. */
	public TransformerFactory putValue() throws TransformerConfigurationException {
		TransformerFactory result = this.getValue();
		if (result != null) return result;
		result = TransformerFactory.newInstance();
		this.useValue(result);
		this.updateValue();
		return result;
	}

	/** Diese Methode aktualisiert die Einstellungen der {@link TransformerFactory} und gibt {@code this} zurück. Bei dieser Aktualisierung werden auf die über
	 * {@link #getValue()} ermittelte {@link TransformerFactory} die Einstellungen übertragen, die in {@link #listener()}, {@link #resolver()},
	 * {@link #features()} und {@link #attributes()} konfiguriert sind.
	 *
	 * @return {@code this}.
	 * @throws TransformerConfigurationException Wenn {@link TransformerFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst. */
	public GOwner updateValue() throws TransformerConfigurationException {
		final TransformerFactory factory = this.putValue();
		for (final URIResolver value: this.resolver()) {
			factory.setURIResolver(value);
		}
		for (final ErrorListener value: this.listener()) {
			factory.setErrorListener(value);
		}
		for (final Entry<String, Boolean> entry: this.features()) {
			factory.setFeature(entry.getKey(), entry.getValue().booleanValue());
		}
		for (final Entry<String, String> entry: this.attributes()) {
			factory.setAttribute(entry.getKey(), entry.getValue());
		}
		return this.owner();
	}

	/** Diese Methode öffnet den Konfigurator für die Fähigkeiten und gibt ihn zurück.
	 *
	 * @see TransformerFactory#setFeature(String, boolean)
	 * @return Konfigurator. */
	public abstract FeaturesValue features();

	public FeaturesProxy forFeatures() {
		return new FeaturesProxy();
	}

	public AttributesProxy forAttributes() {
		return new AttributesProxy();
	}

	/** Diese Methode öffnet den Konfigurator für die Attribute der und gibt ihn zurück.
	 *
	 * @see TransformerFactory#setAttribute(String, Object)
	 * @return Konfigurator. */
	public abstract AttributesValue attributes();

	/** Diese Methode öffnet den Konfigurator für den {@link ErrorListener} und gibt ihn zurück.
	 *
	 * @see TransformerFactory#setErrorListener(ErrorListener)
	 * @return Konfigurator. */
	public abstract ListenerValue listener();

	public ListenerProxy forListener() {
		return new ListenerProxy();
	}

	/** Diese Methode öffnet den Konfigurator für den {@link URIResolver} und gibt ihn zurück.
	 *
	 * @see TransformerFactory#setURIResolver(URIResolver)
	 * @return Konfigurator. */
	public abstract ResolverValue resolver();

	public ResolverProxy forResolver() {
		return new ResolverProxy();
	}

	@Override
	public abstract GOwner owner();

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.features(), this.attributes(), this.listener(), this.resolver());
	}

}