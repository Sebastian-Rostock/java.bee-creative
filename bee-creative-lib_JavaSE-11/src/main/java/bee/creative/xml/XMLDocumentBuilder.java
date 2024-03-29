package bee.creative.xml;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

/** Diese Klasse implementiert einen Konfigurator für den {@link DocumentBuilder} zur Erzeugung eines {@link Document}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class XMLDocumentBuilder extends DocumentBuilderBuilder.Value<XMLDocumentBuilder> {

	@Override
	public final XMLDocumentBuilder owner() {
		return this;
	}

}