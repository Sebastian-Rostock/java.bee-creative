package bee.creative.xml;

import javax.xml.validation.SchemaFactory;

/** Diese Klasse implementiert den Konfigurator für eine {@link SchemaFactory}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLSchemaFactory extends BaseSchemaFactoryData<XMLSchemaFactory> {

	@Override
	protected XMLSchemaFactory customThis() {
		return this;
	}

}