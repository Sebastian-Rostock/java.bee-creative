package bee.creative.xml;

import javax.xml.transform.Transformer;

/** Diese Klasse implementiert den Konfigurator für einen {@link Transformer}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLTransformer extends BaseTransformerData<XMLTransformer> {

	@Override
	protected XMLTransformer customThis() {
		return this;
	}

}
