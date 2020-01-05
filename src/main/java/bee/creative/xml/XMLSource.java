package bee.creative.xml;

import javax.xml.transform.Source;

/** Diese Klasse implementiert den Konfigurator für eine {@link Source}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class XMLSource extends BaseSourceData<XMLSource> {

	@Override
	protected final XMLSource customThis() {
		return this;
	}

}