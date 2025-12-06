package bee.creative.xml;

import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.HashMap;

/** Diese Klasse implementiert einen abstrakten Konfigurator für die Fähigkeiten einer {@link XPathFactory}, {@link TransformerFactory} bzw.
 * {@link DocumentBuilderFactory}.
 *
 * @see XPathFactory#setFeature(String, boolean)
 * @see TransformerFactory#setFeature(String, boolean)
 * @see DocumentBuilderFactory#setFeature(String, boolean)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class FeaturesBuilder<GOwner> extends BaseMapBuilder<String, Boolean, Map<String, Boolean>, GOwner> {

	public static abstract class Value<GOwner> extends FeaturesBuilder<GOwner> {

		Map<String, Boolean> value = new HashMap<>();

		@Override
		public Map<String, Boolean> get() {
			return this.value;
		}

	}

	public static abstract class Proxy<GOwner> extends FeaturesBuilder<GOwner> {

		@Override
		public Map<String, Boolean> get() {
			return this.value().get();
		}

		protected abstract Value<?> value();

	}

	/** Diese Methode wählt {@link XMLConstants#FEATURE_SECURE_PROCESSING} und gibt {@code this} zurück.
	 *
	 * @see #forKey(Object)
	 * @return {@code this}. */
	public final MapValueBuilder forFEATURE_SECURE_PROCESSING() {
		return this.forKey(XMLConstants.FEATURE_SECURE_PROCESSING);
	}

}