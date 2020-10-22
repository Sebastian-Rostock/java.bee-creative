package bee.creative.xml;

import javax.xml.transform.TransformerFactory;

/** Diese Klasse implementiert den Konfigurator für einn {@link TransformerFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLTransformerFactory extends BaseTransformerFactoryData<XMLTransformerFactory> {

	@Override
	protected XMLTransformerFactory customThis() {
		return this;
	}

}
