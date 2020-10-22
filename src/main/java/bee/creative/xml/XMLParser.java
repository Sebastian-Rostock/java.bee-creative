package bee.creative.xml;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Konfigurator zum {@link #parse() Parsen} sowie {@link #create() Erstellen} eines {@link Document}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLParser {

	/** Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link DocumentBuilder}.
	 *
	 * @see DocumentBuilder#parse(InputSource)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public class SourceData extends BaseSourceData<SourceData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public XMLParser closeSourceData() {
			return XMLParser.this;
		}

		@Override
		protected SourceData customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link DocumentBuilder} zur Erzeugung eines {@link Document}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public class BuilderData extends BaseDocumentBuilderData<BuilderData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public XMLParser closeBuilderData() {
			return XMLParser.this;
		}

		@Override
		protected BuilderData customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert den Konfigurator {@link #openSourceData()}. */
	final SourceData sourceData = new SourceData();

	/** Dieses Feld speichert den Konfigurator {@link #openBuilderData()}. */
	final BuilderData builderData = new BuilderData();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLParser use(final XMLParser data) {
		if (data == null) return this;
		this.sourceData.use(data.sourceData);
		this.builderData.use(data.builderData);
		return this;
	}

	/** Diese Methode parst die Eingabedaten in ein {@link Document} und gibt dieses zurück.
	 *
	 * @return {@link Document}.
	 * @throws IOException Wenn {@link DocumentBuilder#parse(InputSource)} eine entsprechende Ausnahme auslöst.
	 * @throws SAXException Wenn {@link DocumentBuilder#parse(InputSource)} bzw. {@link BuilderData#getBuilder()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link BuilderData#getBuilder()} eine entsprechende Ausnahme auslöst. */
	public Document parse() throws IOException, SAXException, ParserConfigurationException {
		final InputSource source = this.sourceData.getInputSource();
		final DocumentBuilder builder = this.builderData.getBuilder();
		final Document result = builder.parse(source);
		return result;
	}

	/** Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().parse()}.
	 *
	 * @see #parse()
	 * @see BaseSourceData#use(Object) */
	public Document parse(final Object source) throws IOException, SAXException, ParserConfigurationException {
		return this.openSourceData().use(source).closeSourceData().parse();
	}

	/** Diese Methode erzeugt ein neues {@link Document} und gibt dieses zurück.
	 *
	 * @return {@link Document}.
	 * @throws SAXException Wenn {@link BuilderData#getBuilder()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilder#newDocument()} eine entsprechende Ausnahme auslöst. */
	public Document create() throws SAXException, ParserConfigurationException {
		final DocumentBuilder builder = this.builderData.getBuilder();
		final Document result = builder.newDocument();
		return result;
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten (z.B. xml-Datei) und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#parse(InputSource)
	 * @return Konfigurator. */
	public SourceData openSourceData() {
		return this.sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link DocumentBuilder} zurück.
	 *
	 * @return Konfigurator. */
	public BuilderData openBuilderData() {
		return this.builderData;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.builderData);
	}

}