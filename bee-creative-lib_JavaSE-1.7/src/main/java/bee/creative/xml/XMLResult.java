package bee.creative.xml;

import javax.xml.transform.Result;

/** Diese Klasse implementiert den Konfigurator eines {@link Result}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLResult extends ResultBuilder.Value<XMLResult> {

	@Override
	public XMLResult owner() {
		return this;
	}

}