package bee.creative.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/** Diese Klasse implementiert einen Konfigurator für eine {@link DocumentBuilderFactory} zur Erzeugung eines {@link DocumentBuilder}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class XMLDocumentBuilderFactory extends BaseDocumentBuilderFactoryData<XMLDocumentBuilderFactory> {

	/** {@inheritDoc} */
	@Override
	protected final XMLDocumentBuilderFactory customThis() {
		return this;
	}

}
