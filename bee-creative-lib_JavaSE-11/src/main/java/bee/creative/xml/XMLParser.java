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
	public static class SourceValue extends SourceBuilder.Value<SourceValue> {

		@Override
		public SourceValue owner() {
			return this;
		}

	}

	public class SourceProxy extends SourceBuilder.Proxy<XMLParser> {

		@Override
		protected SourceValue value() {
			return XMLParser.this.source();
		}

		@Override
		public XMLParser owner() {
			return XMLParser.this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link DocumentBuilder} zur Erzeugung eines {@link Document}. */
	public static class BuilderValue extends DocumentBuilderBuilder.Value<BuilderValue> {

		@Override
		public BuilderValue owner() {
			return this;
		}

	}

	public class BuilderProxy extends DocumentBuilderBuilder.Proxy<XMLParser> {

		@Override
		protected BuilderValue value() {
			return XMLParser.this.builder();
		}

		@Override
		public XMLParser owner() {
			return XMLParser.this;
		}

	}

	/** Dieses Feld speichert den Konfigurator {@link #source()}. */
	final SourceValue source = new SourceValue();

	/** Dieses Feld speichert den Konfigurator {@link #builder()}. */
	final BuilderValue builder = new BuilderValue();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLParser use(final XMLParser that) {
		if (that == null) return this;
		this.forSource().use(that.source());
		this.forBuilder().use(that.builder());
		return this;
	}

	/** Diese Methode parst die Eingabedaten in ein {@link Document} und gibt dieses zurück.
	 *
	 * @return {@link Document}.
	 * @throws IOException Wenn {@link DocumentBuilder#parse(InputSource)} eine entsprechende Ausnahme auslöst.
	 * @throws SAXException Wenn {@link DocumentBuilder#parse(InputSource)} bzw. {@link BuilderValue#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link BuilderValue#putValue()} eine entsprechende Ausnahme auslöst. */
	public Document parse() throws IOException, SAXException, ParserConfigurationException {
		return this.builder().putValue().parse(this.source().getInputSource());
	}

	/** Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().parse()}.
	 *
	 * @see #parse()
	 * @see SourceBuilder#use(Object) */
	public Document parse(final Object source) throws IOException, SAXException, ParserConfigurationException {
		return this.forSource().use(source).parse();
	}

	/** Diese Methode erzeugt ein neues {@link Document} und gibt dieses zurück.
	 *
	 * @return {@link Document}.
	 * @throws SAXException Wenn {@link BuilderValue#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws ParserConfigurationException Wenn {@link DocumentBuilder#newDocument()} eine entsprechende Ausnahme auslöst. */
	public Document create() throws SAXException, ParserConfigurationException {
		return this.builder().putValue().newDocument();
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten (z.B. xml-Datei) und gibt ihn zurück.
	 *
	 * @see DocumentBuilder#parse(InputSource)
	 * @return Konfigurator. */
	public SourceValue source() {
		return this.source;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link DocumentBuilder} zurück.
	 *
	 * @return Konfigurator. */
	public BuilderValue builder() {
		return this.builder;
	}

	public SourceProxy forSource() {
		return new SourceProxy();
	}

	public BuilderProxy forBuilder() {
		return new BuilderProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.source(), this.builder());
	}

}