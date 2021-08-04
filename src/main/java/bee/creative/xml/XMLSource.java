package bee.creative.xml;

import javax.xml.transform.Source;

/** Diese Klasse implementiert den Konfigurator für eine {@link Source}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLSource extends SourceBuilder.Value<XMLSource> {

	@Override
	public XMLSource owner() {
		return this;
	}

}