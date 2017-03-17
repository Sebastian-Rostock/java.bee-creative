package bee.creative.xml;

import javax.xml.transform.TransformerFactory;

/** Diese Klasse implementiert den Konfigurator für einn {@link TransformerFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class XMLTransformerFactory extends BaseTransformerFactoryData<XMLTransformerFactory> {

	/** {@inheritDoc} */
	@Override
	protected final XMLTransformerFactory customThis() {
		return this;
	}

}
