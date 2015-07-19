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
		public XMLParser closeSourceData() {
			return XMLParser.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SourceData thiz() {
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
		public XMLParser closeFactoryData() {
			return XMLParser.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected BuilderData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
	 */
	final SourceData sourceData = new SourceData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openBuilderData()}.
	 */
	final BuilderData builderData = new BuilderData();

	{}

	/**
	 * Diese Methode parst die Eingabedaten in ein {@link Document} und gibt dieses zurück.
	 * 
	 * @return {@link Document}.
	 * @throws SAXException Wenn {@link DocumentBuilder#parse(InputSource)} eine entsprechende Ausnahme auslöst.
	 * @throws IOException Wenn {@link DocumentBuilder#parse(InputSource)} eine entsprechende Ausnahme auslöst.
	 */
	public Document parse() throws SAXException, IOException {
		final InputSource source = this.sourceData.getInputSource();
		final DocumentBuilder builder = this.builderData.build();
		final Document result = builder.parse(source);
		return result;
	}

	/**
	 * Diese Methode erzeugt ein neues {@link Document} und gibt dieses zurück.
	 * 
	 * @return {@link Document}.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilder#newDocument()} eine entsprechende Ausnahme auslöst.
	 */
	public Document create() throws ParserConfigurationException {
		final DocumentBuilder builder = this.builderData.build();
		final Document result = builder.newDocument();
		return result;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Eingabedaten (z.B. xml-Datei) und gibt ihn zurück.
	 * 
	 * @see DocumentBuilder#parse(InputSource)
	 * @return Konfigurator.
	 */
	public SourceData openSourceData() {
		return this.sourceData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link DocumentBuilder} zurück.
	 * 
	 * @return Konfigurator.
	 */
	public BuilderData openBuilderData() {
		return this.builderData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.sourceData, this.builderData);
	}

}