package bee.creative.xml;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;
import bee.creative.util.Builders.BaseMapBuilder;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator für die Fähigkeiten einer {@link XPathFactory}, {@link TransformerFactory} bzw.
 * {@link DocumentBuilderFactory}.
 * 
 * @see XPathFactory#setFeature(String, boolean)
 * @see TransformerFactory#setFeature(String, boolean)
 * @see DocumentBuilderFactory#setFeature(String, boolean)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseFeatureData<GThiz> extends BaseMapBuilder<String, Boolean, GThiz> {

	/**
	 * Diese Methode wählt {@link XMLConstants#FEATURE_SECURE_PROCESSING} und gibt {@code this} zurück.
	 * 
	 * @see #forKey(Object)
	 * @return {@code this}.
	 */
	public GThiz forFEATURE_SECURE_PROCESSING() {
		return this.forKey(XMLConstants.FEATURE_SECURE_PROCESSING);
	}

}