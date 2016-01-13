package bee.creative.xml;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen Konfigurator zum {@link #parse() Parsen} sowie {@link #create() Erstellen} eines {@link Document}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLParser {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link DocumentBuilder}.
	 * 
	 * @see DocumentBuilder#parse(InputSource)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class SourceData extends BaseSourceData<SourceData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public final XMLParser closeSourceData() {
			return XMLParser.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final SourceData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link DocumentBuilder} zur Erzeugung eines {@link Document}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class BuilderData extends BaseDocumentBuilderData<BuilderData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public final XMLParser closeBuilderData() {
			return XMLParser.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final BuilderData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
	 */
	final SourceData __sourceData = new SourceData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openBuilderData()}.
	 */
	final BuilderData __builderData = new BuilderData();

	{}

	/**
	 * Diese Methode parst die Eingabedaten in ein {@link Document} und gibt dieses zurück.
	 * 
	 * @return {@link Document}.
	 * @throws IOException Wenn {@link DocumentBuilder#parse(InputSource)} eine entsprechende Ausnahme auslöst.
	 * @throws SAXException Wenn {@link DocumentBuilder#parse(InputSource)} bzw. {@link BuilderData#getBuilder()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link BuilderData#getBuilder()} eine entsprechende Ausnahme auslöst.
	 */
	public final Document parse() throws IOException, SAXException, ParserConfigurationException {
		final InputSource source = this.__sourceData.getInputSource();
		final DocumentBuilder builder = this.__builderData.getBuilder();
		final Document result = builder.parse(source);
		return result;
	}

	/**
	 * Diese Methode erzeugt ein neues {@link Document} und gibt dieses zurück.
	 * 
	 * @return {@link Document}.
	 * @throws SAXException Wenn {@link BuilderData#getBuilder()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilder#newDocument()} eine entsprechende Ausnahme auslöst.
	 */
	public final Document create() throws SAXException, ParserConfigurationException {
		final DocumentBuilder builder = this.__builderData.getBuilder();
		final Document result = builder.newDocument();
		return result;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Eingabedaten (z.B. xml-Datei) und gibt ihn zurück.
	 * 
	 * @see DocumentBuilder#parse(InputSource)
	 * @return Konfigurator.
	 */
	public final SourceData openSourceData() {
		return this.__sourceData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link DocumentBuilder} zurück.
	 * 
	 * @return Konfigurator.
	 */
	public final BuilderData openBuilderData() {
		return this.__builderData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__sourceData, this.__builderData);
	}

}