package bee.creative.xml;

import javax.xml.validation.Schema;

/** Diese Klasse implementiert den Konfigurator für ein {@link Schema}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLSchema extends SchemaBuilder.Value<XMLSchema> {

	@Override
	public XMLSchema owner() {
		return this;
	}

}