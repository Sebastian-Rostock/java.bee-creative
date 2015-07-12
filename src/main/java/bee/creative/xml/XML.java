package bee.creative.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import bee.creative.util.Builder;
import bee.creative.util.Builders;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Converter;
import bee.creative.util.Converters;
import bee.creative.util.Converters.CachedConverter;
import bee.creative.util.Objects;
import bee.creative.util.Pointer;
import bee.creative.util.Pointers;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden zur zur Erzeugung und Formatierung von {@link Document DOM-Dokumenten}, zur Verarbeitung von {@link XPath
 * XPath-Auswertungsumgebungen}, {@link Node DOM-Knoten}, {@link Templates XSL-Templates} und {@link Transformer XSL-Transformern}.
 * 
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class XML {

	/**
	 * Diese Klasse implementiert den abstrakten Konfigurator einer {@link Source}, die für die Quelldaten ({@code *.xml}) bzw. Transformationsdaten ({@code *.xsl}
	 * ) eines {@link Transformer} genutzt wird.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @see TransformerFactory#newTemplates(Source)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseSourceData<GThiz> extends BaseBuilder<Source, GThiz> {

		/**
		 * Dieses Feld speichert die Quelldaten.
		 */
		Source source;

		/**
		 * Dieses Feld speichert den System-Identifikator.
		 */
		String systemID;

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseSourceData<?> data) {
			if (data == null) return this.thiz();
			this.source = data.source;
			this.systemID = data.systemID;
			return this.thiz();
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link URL} und gibt {@code this}
		 * zurück.
		 * 
		 * @see #useSource(Source)
		 * @see URL#toExternalForm()
		 * @see StreamSource#StreamSource(String)
		 * @param url {@link URL}.
		 * @return {@code this}.
		 */
		public GThiz useUrl(final URL url) {
			return this.useSource(new StreamSource(url.toExternalForm()));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link File} und gibt {@code this}
		 * zurück.
		 * 
		 * @see #useSource(Source)
		 * @see StreamSource#StreamSource(File)
		 * @param file {@link File}.
		 * @return {@code this}.
		 */
		public GThiz useFile(final File file) {
			return this.useSource(new StreamSource(file));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useReader(Reader)} über eine {@link StringReader} mit dem gegebenen Text und gibt {@code this} zurück.
		 * 
		 * @see #useReader(Reader)
		 * @see StringReader#StringReader(String)
		 * @param text Text.
		 * @return {@code this}.
		 */
		public GThiz useText(final String text) {
			return this.useReader(new StringReader(text));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link DOMSource} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
		 * 
		 * @see #useSource(Source)
		 * @see DOMSource#DOMSource(Node)
		 * @param node {@link Node}.
		 * @return {@code this}.
		 */
		public GThiz useNode(final Node node) {
			return this.useSource(new DOMSource(node));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link Reader} und gibt {@code this}
		 * zurück.
		 * 
		 * @see #useSource(Source)
		 * @see StreamSource#StreamSource(Reader)
		 * @param reader {@link Reader}.
		 * @return {@code this}.
		 */
		public GThiz useReader(final Reader reader) {
			return this.useSource(new StreamSource(reader));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt
		 * {@code this} zurück.
		 * 
		 * @see #useSource(Source)
		 * @see StreamSource#StreamSource(InputStream)
		 * @param stream {@link InputStream}.
		 * @return {@code this}.
		 */
		public GThiz useStream(final InputStream stream) {
			return this.useSource(new StreamSource(stream));
		}

		/**
		 * Diese Methode setzt den {@link Source#getSystemId() System-Identifikator} und gibt {@code this} zurück.
		 * 
		 * @see Source#setSystemId(String)
		 * @param systemID System-Identifikator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useSystemID(final String systemID) {
			this.systemID = systemID;
			if (this.source == null) return this.thiz();
			this.source.setSystemId(systemID);
			return this.thiz();
		}

		/**
		 * Diese Methode setzt die Quelldaten und gibt {@code this} zurück.<br>
		 * Der aktuelle {@link Source#getSystemId() System-Identifikator} wird beibehalten, sofern er nicht {@code null} ist.
		 * 
		 * @see #getSource()
		 * @see #useSystemID(String)
		 * @param source Quelldaten oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useSource(final Source source) {
			this.source = source;
			if (source == null) return this.thiz();
			return this.useSystemID(this.systemID != null ? this.systemID : source.getSystemId());
		}

		/**
		 * Diese Methode gibt die aktuell konfigurierten Quelldaten zurück.
		 * 
		 * @see #useFile(File)
		 * @see #useNode(Node)
		 * @see #useReader(Reader)
		 * @see #useSource(Source)
		 * @see #useStream(InputStream)
		 * @see #useSystemID(String)
		 * @return Quelldaten.
		 */
		public Source getSource() {
			return this.source;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Source build() throws IllegalStateException {
			return this.getSource();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.source, this.systemID);
		}

	}

	/**
	 * Diese Klasse implementiert den abstrakten Konfigurator einer {@link InputSource}, die für die Quelldaten ({@code *.xml}) eines {@link Document} genutzt
	 * wird.
	 * 
	 * @see DocumentBuilder#parse(InputSource)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseInputSourceData<GThiz> extends BaseBuilder<InputSource, GThiz> {

		/**
		 * Dieses Feld speichert die Quelldaten.
		 */
		InputSource source;

		/**
		 * Dieses Feld speichert den System-Identifikator.
		 */
		String systemID;

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseInputSourceData<?> data) {
			if (data == null) return this.thiz();
			this.source = data.source;
			this.systemID = data.systemID;
			return this.thiz();
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useReader(Reader)} über eine {@link StringReader} mit dem gegebenen Text und gibt {@code this} zurück.
		 * 
		 * @see #useReader(Reader)
		 * @see StringReader#StringReader(String)
		 * @param text Text.
		 * @return {@code this}.
		 */
		public GThiz useText(final String text) {
			return this.useReader(new StringReader(text));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(InputSource)} auf eine {@link FileReader} mit dem gegebenen {@link File} und gibt {@code this}
		 * zurück.
		 * 
		 * @see #useSource(InputSource)
		 * @see FileReader#FileReader(File)
		 * @param file {@link File}.
		 * @return {@code this}.
		 * @throws FileNotFoundException
		 */
		public GThiz useReader(final File file) throws FileNotFoundException {
			return this.useReader(new FileReader(file));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link Reader} und gibt {@code this}
		 * zurück.
		 * 
		 * @see #useSource(Source)
		 * @see StreamSource#StreamSource(Reader)
		 * @param reader {@link Reader}.
		 * @return {@code this}.
		 */
		public GThiz useReader(final Reader reader) {
			return this.useSource(new InputSource(reader));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(InputSource)} auf eine {@link FileReader} mit dem gegebenen {@link File} und gibt {@code this}
		 * zurück.
		 * 
		 * @see #useSource(InputSource)
		 * @see FileReader#FileReader(File)
		 * @param file {@link File}.
		 * @return {@code this}.
		 * @throws FileNotFoundException
		 */
		public GThiz useStream(final File file) throws FileNotFoundException {
			return this.useStream(new FileInputStream(file));
		}

		/**
		 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt
		 * {@code this} zurück.
		 * 
		 * @see #useSource(Source)
		 * @see StreamSource#StreamSource(InputStream)
		 * @param stream {@link InputStream}.
		 * @return {@code this}.
		 */
		public GThiz useStream(final InputStream stream) {
			return this.useSource(new InputSource(stream));
		}

		/**
		 * Diese Methode setzt den {@link Source#getSystemId() System-Identifikator} und gibt {@code this} zurück.
		 * 
		 * @see Source#setSystemId(String)
		 * @param systemID System-Identifikator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useSystemID(final String systemID) {
			this.systemID = systemID;
			if (this.source == null) return this.thiz();
			this.source.setSystemId(systemID);
			return this.thiz();
		}

		/**
		 * Diese Methode setzt die Quelldaten und gibt {@code this} zurück.<br>
		 * Der aktuelle {@link InputSource#getSystemId() System-Identifikator} wird beibehalten, sofern er nicht {@code null} ist.
		 * 
		 * @see #getSource()
		 * @see #useSystemID(String)
		 * @param source Quelldaten oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useSource(final InputSource source) {
			this.source = source;
			if (source == null) return this.thiz();
			return this.useSystemID(this.systemID != null ? this.systemID : source.getSystemId());
		}

		/**
		 * Diese Methode gibt die aktuell konfigurierten Quelldaten zurück.
		 * 
		 * @see #useReader(File)
		 * @see #useReader(Reader)
		 * @see #useStream(File)
		 * @see #useStream(InputStream)
		 * @see #useSource(InputSource)
		 * @see #useSystemID(String)
		 * @return Quelldaten.
		 */
		public InputSource getSource() {
			return this.source;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public InputSource build() throws IllegalStateException {
			return this.getSource();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.source, this.systemID);
		}

	}

	/**
	 * Diese Klasse implementiert den abstrakten Konfigurator für die Fähigkeiten einer {@link XPathFactory}, {@link TransformerFactory} bzw.
	 * {@link DocumentBuilderFactory}.
	 * 
	 * @see XPathFactory#setFeature(String, boolean)
	 * @see TransformerFactory#setFeature(String, boolean)
	 * @see DocumentBuilderFactory#setFeature(String, boolean)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseFeatureData<GThiz> extends BaseMapBuilder<String, Boolean, GThiz> {

		/**
		 * Diese Methode wählt {@link XMLConstants#FEATURE_SECURE_PROCESSING} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @return {@code this}.
		 */
		public GThiz forFEATURE_SECURE_PROCESSING() {
			return this.forKey(XMLConstants.FEATURE_SECURE_PROCESSING);
		}

	}

	/**
	 * Diese Klasse implementiert den abstrakten Konfigurator für ein {@link Schema}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseSchemaData<GThiz> extends BaseBuilder<Schema, GThiz> {

		/**
		 * Diese Klasse implementiert den Konfigurator für die Quelldaten eines {@link Transformer}.
		 * 
		 * @see Transformer#transform(Source, Result)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class SourceData extends BaseSourceData<SourceData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public GThiz closeSourceData() {
				return BaseSchemaData.this.thiz();
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
		 * Diese Klasse implementiert den Konfigurator einer {@link SchemaFactory}.
		 * 
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class SchemaFactoryData extends BaseSchemaFactoryData<SchemaFactoryData> {

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected SchemaFactoryData thiz() {
				return this;
			}

		}

		{}

		/**
		 * Dieses Feld speichert das {@link Schema}.
		 */
		Schema schema;

		/**
		 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
		 */
		final SchemaFactoryData factoryData = //
			new SchemaFactoryData();

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
		 */
		final SourceData sourceData = //
			new SourceData();

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseSchemaData<?> data) {
			if (data == null) return this.thiz();
			this.schema = data.schema;
			this.sourceData.use(data.sourceData);
			this.factoryData.use(data.factoryData);
			return this.thiz();
		}

		/**
		 * Diese Methode gibt das {@link Schema} zurück.<br>
		 * Wenn über {@link #useSchema(Schema)} noch kein {@link Schema} gesetzt wurden, werden über {@link SchemaFactory#newSchema(Source)} ein neues erstellt und
		 * über {@link #useSchema(Schema)} gesetzt. Wenn über {@link #openSourceData()} keine Quelldaten konfiguriert sind oder über {@link #clearSchema()} das
		 * {@link Schema} auf {@code null} gesetzt wurden, wird {@code null} geliefert.
		 * 
		 * @return {@link Schema} oder {@code null}.
		 * @throws SAXException Wenn {@link SchemaFactory#newSchema(Source)} eine entsprechende Ausnahme auslöst.
		 */
		public Schema getSchema() throws SAXException {
			Schema result = this.schema;
			if (result != null) return result;
			final Source source = this.sourceData.build();
			if (source == null) return null;
			result = this.factoryData.build().newSchema(source);
			this.useSchema(result);
			return result;
		}

		/**
		 * Diese Methode setzt das {@link Schema} und gibt {@code this} zurück.
		 * 
		 * @param schema {@link Schema} oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useSchema(final Schema schema) {
			this.schema = schema;
			return this.thiz();
		}

		/**
		 * Diese Methode leert das {@link Schema} sowie dessen Quelldaten und gibt {@code this} zurück.<br>
		 * Damit liefern {@link #getSchema()} immer {@code null}.
		 * 
		 * @return {@code this}.
		 */
		public GThiz clearSchema() {
			this.openSourceData().useSource(null).closeSourceData();
			return this.useSchema(null);
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die Schemadaten (z.B. xsd-Datei) und gibt ihn zurück.
		 * 
		 * @see TransformerFactory#newTemplates(Source)
		 * @return Konfigurator.
		 */
		public SourceData openSourceData() {
			return this.sourceData;
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die {@link SchemaFactory} und gibt ihn zurück.
		 * 
		 * @return Konfigurator.
		 */
		public SchemaFactoryData openFactoryData() {
			return this.factoryData;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected abstract GThiz thiz();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Schema build() throws IllegalStateException {
			try {
				return this.getSchema();
			} catch (final SAXException cause) {
				throw new IllegalStateException(cause);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.sourceData, this.factoryData);
		}

	}

	/**
	 * Diese Klasse implementiert den abstrakten Konfigurator für eine {@link SchemaFactory}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseSchemaFactoryData<GThiz> extends BaseBuilder<SchemaFactory, GThiz> {

		/**
		 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link SchemaFactory}.
		 * 
		 * @see SchemaFactory#setFeature(String, boolean)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class FeatureData extends BaseFeatureData<FeatureData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public GThiz closeFeatureData() {
				return BaseSchemaFactoryData.this.thiz();
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected FeatureData thiz() {
				return this;
			}

		}

		/**
		 * Diese Klasse implementiert den Konfigurator für die Eigenschaften einer {@link SchemaFactory}.
		 * 
		 * @see SchemaFactory#setProperty(String, Object)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class PropertyData extends BaseMapBuilder<String, Object, PropertyData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public GThiz closePropertyData() {
				return BaseSchemaFactoryData.this.thiz();
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected PropertyData thiz() {
				return this;
			}

		}

		/**
		 * Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler} einer {@link SchemaFactory}.
		 * 
		 * @see SchemaFactory#setErrorHandler(ErrorHandler)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class HandlerData extends BaseValueBuilder<ErrorHandler, HandlerData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public GThiz closeListenerData() {
				return BaseSchemaFactoryData.this.thiz();
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected HandlerData thiz() {
				return this;
			}

		}

		/**
		 * Diese Klasse implementiert den Konfigurator für den {@link LSResourceResolver} einer {@link SchemaFactory}.
		 * 
		 * @see SchemaFactory#setResourceResolver(LSResourceResolver)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class ResolverData extends BaseValueBuilder<LSResourceResolver, ResolverData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public GThiz closeResolverData() {
				return BaseSchemaFactoryData.this.thiz();
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected ResolverData thiz() {
				return this;
			}

		}

		/**
		 * Diese Klasse implementiert den Konfigurator für die Sprache einer {@link SchemaFactory}.<br>
		 * Initialisiert wird diese Sprache via {@link #useW3C_XML_SCHEMA_NS_URI()}.
		 * 
		 * @see SchemaFactory#newInstance(String)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class LanguageData extends BaseValueBuilder<String, LanguageData> {

			/**
			 * Dieser Konstruktor initialisiert den Wert via {@link #useW3C_XML_SCHEMA_NS_URI()}.
			 */
			LanguageData() {
				this.useW3C_XML_SCHEMA_NS_URI();
			}

			{}

			/**
			 * Diese Methode setzt den Wert auf {@link XMLConstants#W3C_XML_SCHEMA_NS_URI} und gibt {@code this} zurück.
			 * 
			 * @see #use(BaseValueBuilder)
			 * @return {@code this}.
			 */
			public LanguageData useW3C_XML_SCHEMA_NS_URI() {
				return super.use(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			}

			/**
			 * Diese Methode setzt den Wert auf {@link XMLConstants#RELAXNG_NS_URI} und gibt {@code this} zurück.
			 * 
			 * @see #use(BaseValueBuilder)
			 * @return {@code this}.
			 */
			public LanguageData useRELAXNG_NS_URI() {
				return super.use(XMLConstants.RELAXNG_NS_URI);
			}

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public GThiz closeLanguageData() {
				return BaseSchemaFactoryData.this.thiz();
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected LanguageData thiz() {
				return this;
			}

		}

		{}

		/**
		 * Dieses Feld speichert die {@link SchemaFactory}.
		 */
		SchemaFactory factory;

		/**
		 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
		 */
		final FeatureData featureData = //
			new FeatureData();

		/**
		 * Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}.
		 */
		final PropertyData propertyData = //
			new PropertyData();

		/**
		 * Dieses Feld speichert den Konfigurator für {@link #openHandlerData()}.
		 */
		final HandlerData handlerData = //
			new HandlerData();

		/**
		 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
		 */
		final ResolverData resolverData = //
			new ResolverData();

		/**
		 * Dieses Feld speichert den Konfigurator für {@link #openLanguageData()}.
		 */
		final LanguageData languageData = //
			new LanguageData();

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseSchemaData<?>.SchemaFactoryData data) {
			if (data == null) return this.thiz();
			this.factory = data.factory;
			this.featureData.use(data.featureData);
			this.propertyData.use(data.propertyData);
			this.handlerData.use(data.handlerData);
			this.resolverData.use(data.resolverData);
			this.languageData.use(data.languageData);
			return this.thiz();
		}

		/**
		 * Diese Methode gibt das {@link SchemaFactory} zurück.<br>
		 * Wenn über {@link #useFactory(SchemaFactory)} noch keine {@link SchemaFactory} gesetzt wurde, wird über {@link SchemaFactory#newInstance(String)} eine
		 * neue erstellt und über {@link #useFactory(SchemaFactory)} gesetzt. Die zur Erstellung verwendete Sprache kann in über {@link #openLanguageData()}
		 * konfiguriert werden.
		 * 
		 * @see #useFactory(SchemaFactory)
		 * @return {@link SchemaFactory}.
		 */
		public SchemaFactory getFactory() {
			SchemaFactory result = this.factory;
			if (result != null) return result;
			result = SchemaFactory.newInstance(this.languageData.get());
			this.useFactory(result);
			return result;
		}

		/**
		 * Diese Methode setzt die {@link SchemaFactory} und gibt {@code this} zurück.
		 * 
		 * @param factory {@link SchemaFactory} oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useFactory(final SchemaFactory factory) {
			this.factory = factory;
			return this.thiz();
		}

		/**
		 * Diese Methode aktualisiert die Einstellungen der {@link SchemaFactory} und gibt {@code this} zurück.<br>
		 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link SchemaFactory} die Einstellungen übertragen, die in
		 * {@link #openHandlerData()}, {@link #openResolverData()}, {@link #openFeatureData()} und {@link #openPropertyData()} konfiguriert sind.
		 * 
		 * @return {@code this}.
		 * @throws SAXException Wenn {@link SchemaFactory#setFeature(String, boolean)} bzw. {@link SchemaFactory#setProperty(String, Object)} eine entsprechende
		 *         Ausnahme auslöst.
		 */
		public GThiz updateFactory() throws SAXException {
			final SchemaFactory factory = this.getFactory();
			if (this.handlerData.get() != null) {
				factory.setErrorHandler(this.handlerData.get());
			}
			if (this.resolverData.get() != null) {
				factory.setResourceResolver(this.resolverData.get());
			}
			for (final Entry<String, Boolean> entry: this.featureData) {
				factory.setFeature(entry.getKey(), entry.getValue());
			}
			for (final Entry<String, Object> entry: this.propertyData) {
				factory.setProperty(entry.getKey(), entry.getValue());
			}
			return this.thiz();
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getFeature(String) Fähigkeiten} und gibt ihn zurück.
		 * 
		 * @see TransformerFactory#setFeature(String, boolean)
		 * @return Konfigurator.
		 */
		public FeatureData openFeatureData() {
			return this.featureData;
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getAttribute(String) Attribute} der und gibt ihn zurück.
		 * 
		 * @see TransformerFactory#setAttribute(String, Object)
		 * @return Konfigurator.
		 */
		public PropertyData openPropertyData() {
			return this.propertyData;
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getErrorListener() Fehlerbehandlung} und gibt ihn zurück.
		 * 
		 * @see TransformerFactory#setErrorListener(ErrorListener)
		 * @return Konfigurator.
		 */
		public HandlerData openHandlerData() {
			return this.handlerData;
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getURIResolver() URL-Auflöser} und gibt ihn zurück.
		 * 
		 * @see TransformerFactory#setURIResolver(URIResolver)
		 * @return Konfigurator.
		 */
		public ResolverData openResolverData() {
			return this.resolverData;
		}

		/**
		 * Diese Methode öffnet den Konfigurator für die {@link SchemaFactory#newInstance(String) Schemasprache} und gibt ihn zurück.
		 * 
		 * @see SchemaFactory#newInstance(String)
		 * @return Konfigurator.
		 */
		public LanguageData openLanguageData() {
			return this.languageData;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected abstract GThiz thiz();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SchemaFactory build() throws IllegalStateException {
			try {
				this.updateFactory();
				return this.getFactory();
			} catch (final SAXException cause) {
				throw new IllegalStateException(cause);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.featureData, this.propertyData, this.handlerData, this.resolverData, this.languageData);
		}

	}

	public static final class XMLResultData<GOwner> implements Builder<Result> {

		/**
		 * Dieses Feld speichert den Besitzer.
		 */
		final GOwner owner;

		Result result;

		/**
		 * Dieses Feld speichert den System-Identifikator.
		 */
		String systemID;

		/**
		 * Dieser Konstruktor initialisiert den Besitzer.
		 * 
		 * @param owner Besitzer.
		 */
		public XMLResultData(final GOwner owner) {
			this.owner = owner;
		}

		{}

		public XMLResultData<GOwner> use(final XMLResultData<?> data) {
			if (data == null) return this;
			this.result = data.result;
			this.systemID = data.systemID;
			return this;
		}

		public XMLResultData<GOwner> useFile(final File file) {
			return this.useResult(new StreamResult(file));
		}

		public XMLResultData<GOwner> useNode(final Node node) {
			return this.useResult(new DOMResult(node));
		}

		public XMLResultData<GOwner> useWriter(final Writer writer) {
			return this.useResult(new StreamResult(writer));
		}

		public XMLResultData<GOwner> useStream(final OutputStream stream) {
			return this.useResult(new StreamResult(stream));
		}

		public XMLResultData<GOwner> useSystemID(final String systemID) {
			this.systemID = systemID;
			if (this.result == null) return this;
			this.result.setSystemId(systemID);
			return this;
		}

		public XMLResultData<GOwner> useResult(final Result result) {
			this.result = result;
			if (result == null) return this;
			return this.useSystemID(this.systemID != null ? this.systemID : result.getSystemId());
		}

		public Result getResult() {
			return this.result;
		}

		/**
		 * Diese Methode gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public GOwner closeResultData() {
			return this.owner;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Result build() throws IllegalStateException {
			return this.getResult();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.result, this.systemID);
		}

	}

	{}

	public static final class XMLSource extends BaseSourceData<XMLSource> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XMLSource thiz() {
			return this;
		}

	}

	public static final class XMLInputSource extends BaseInputSourceData<XMLInputSource> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XMLInputSource thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für ein {@link Schema}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class XMLSchema extends BaseSchemaData<XMLSchema> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XMLSchema thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für eine {@link SchemaFactory}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class XMLSchemaFactory extends BaseSchemaFactoryData<XMLSchemaFactory> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected XMLSchemaFactory thiz() {
			return this;
		}

	}

	{}

	public static XMLParser newParser() {
		return new XMLParser();
	}

	public static XMLSource newSource() {
		return new XMLSource();
	}

	public static XMLSchema newSchema() {
		return new XMLSchema();
	}

	{}

	public static final class XMLParser {

		public final class SourceData extends BaseInputSourceData<SourceData> {

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
		public final class BuilderData implements Builder<DocumentBuilder> {

			/**
			 * Diese Klasse implementiert den Konfigurator für die {@link DocumentBuilderFactory} zur Erzeugung eines {@link DocumentBuilder}.
			 * 
			 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
			 */
			public final class FactoryData implements Builder<DocumentBuilderFactory> {

				/**
				 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link DocumentBuilderFactory}.
				 * 
				 * @see DocumentBuilderFactory#setFeature(String, boolean)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class FeatureData extends BaseFeatureData<FeatureData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public FactoryData closeFeatureData() {
						return FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected FeatureData thiz() {
						return this;
					}

				}

				/**
				 * Diese Klasse implementiert den Konfigurator für das Schema einer {@link DocumentBuilderFactory}.
				 * 
				 * @see DocumentBuilderFactory#setSchema(Schema)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class SchemaData extends BaseSchemaData<SchemaData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public BuilderData.FactoryData closeSchemaData() {
						return BuilderData.FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected SchemaData thiz() {
						return this;
					}

				}

				/**
				 * Diese Klasse implementiert den Konfigurator für die Eigenschaften eines {@link DocumentBuilderFactory}.
				 * 
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class PropertyData extends BaseMapBuilder<String, Boolean, PropertyData> {

					/**
					 * Diese Methode wählt {@code "Coalescing"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setCoalescing(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forCoalescing() {
						return this.forKey("Coalescing");
					}

					/**
					 * Diese Methode wählt {@code "ExpandEntityReferences"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forExpandEntityReferences() {
						return this.forKey("ExpandEntityReferences");
					}

					/**
					 * Diese Methode wählt {@code "IgnoringComments"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forIgnoringComments() {
						return this.forKey("IgnoringComments");
					}

					/**
					 * Diese Methode wählt {@code "IgnoringElementContentWhitespace"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forIgnoringElementContentWhitespace() {
						return this.forKey("IgnoringElementContentWhitespace");
					}

					/**
					 * Diese Methode wählt {@code "NamespaceAware"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forNamespaceAware() {
						return this.forKey("NamespaceAware");
					}

					/**
					 * Diese Methode wählt {@code "Validating"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setValidating(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forValidating() {
						return this.forKey("Validating");
					}

					/**
					 * Diese Methode wählt {@code "XIncludeAware"} und gibt {@code this} zurück.
					 * 
					 * @see #forKey(String)
					 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
					 * @return {@code this}.
					 */
					public PropertyData forXIncludeAware() {
						return this.forKey("XIncludeAware");
					}

					/**
					 * Diese Methode gibt nur dann {@code true} zurück, wenn der Wert zum {@link #forKey(String) gewählten Schlüssel} gleich {@link Boolean#TRUE} ist.
					 * 
					 * @see #getValue()
					 * @return Wahrheitswert.
					 */
					public boolean getBoolean() {
						return Boolean.TRUE.equals(this.getValue());
					}

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public XMLParser closePropertyData() {
						return XMLParser.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected PropertyData thiz() {
						return this;
					}

				}

				/**
				 * Diese Klasse implementiert den Konfigurator für die Attribute einer {@link DocumentBuilderFactory}.
				 * 
				 * @see DocumentBuilderFactory#setAttribute(String, Object)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class AttributeData extends BaseMapBuilder<String, Object, AttributeData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public FactoryData closeAttributeData() {
						return FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected AttributeData thiz() {
						return this;
					}

				}

				{}

				/**
				 * Dieses Feld speichert die {@link DocumentBuilderFactory}.
				 */
				DocumentBuilderFactory factory;

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
				 */
				@SuppressWarnings ("hiding")
				final FeatureData featureData = //
					new FeatureData();

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openSchemaData()}.
				 */
				final SchemaData schemaData = //
					new SchemaData();

				/**
				 * Dieses Feld speichert den Konfigurator {@link #openPropertyData()}.
				 */
				final PropertyData propertyData = //
					new PropertyData();

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}.
				 */
				final AttributeData attributeData = //
					new AttributeData();

				{}

				/**
				 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
				 * 
				 * @param data Konfigurator oder {@code null}.
				 * @return {@code this}.
				 */
				public FactoryData use(final FactoryData data) {
					if (data == null) return this;
					this.factory = data.factory;
					this.featureData.use(data.featureData);
					this.propertyData.use(data.propertyData);
					this.attributeData.use(data.attributeData);
					return this;
				}

				/**
				 * Diese Methode gibt die {@link DocumentBuilderFactory} zurück.<br>
				 * Wenn über {@link #useFactory(DocumentBuilderFactory)} noch keine {@link DocumentBuilderFactory} gesetzt wurde, wird über
				 * {@link DocumentBuilderFactory#newInstance()} eine neue erstellt, über {@link #useFactory(DocumentBuilderFactory)} gesetzt und über
				 * {@link #updateFactory()} aktualisiert.
				 * 
				 * @see #useFactory(DocumentBuilderFactory)
				 * @see #updateFactory()
				 * @return {@link DocumentBuilderFactory}.
				 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
				 */
				public DocumentBuilderFactory getFactory() throws ParserConfigurationException {
					DocumentBuilderFactory result = this.factory;
					if (result != null) return result;
					result = DocumentBuilderFactory.newInstance();
					this.useFactory(result).updateFactory();
					return result;
				}

				/**
				 * Diese Methode setzt die {@link DocumentBuilderFactory} und gibt {@code this} zurück.
				 * 
				 * @param factory {@link DocumentBuilderFactory} oder {@code null}.
				 * @return {@code this}.
				 */
				public FactoryData useFactory(final DocumentBuilderFactory factory) {
					this.factory = factory;
					return this;
				}

				/**
				 * Diese Methode aktualisiert die Einstellungen der {@link DocumentBuilderFactory} und gibt {@code this} zurück.<br>
				 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link DocumentBuilderFactory} die Einstellungen übertragen, die in
				 * {@link #openSchemaData()}, {@link #openFeatureData()}, {@link #openPropertyData()} und {@link #openAttributeData()} konfiguriert sind.
				 * 
				 * @return {@code this}.
				 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
				 */
				public FactoryData updateFactory() throws ParserConfigurationException {
					final DocumentBuilderFactory factory = this.getFactory();
					final PropertyData propertyData = this.propertyData;
					factory.setCoalescing(propertyData.forCoalescing().getBoolean());
					factory.setExpandEntityReferences(propertyData.forExpandEntityReferences().getBoolean());
					factory.setIgnoringComments(propertyData.forIgnoringComments().getBoolean());
					factory.setIgnoringElementContentWhitespace(propertyData.forIgnoringElementContentWhitespace().getBoolean());
					factory.setNamespaceAware(propertyData.forNamespaceAware().getBoolean());
					factory.setValidating(propertyData.forValidating().getBoolean());
					factory.setXIncludeAware(propertyData.forXIncludeAware().getBoolean());
					for (final Entry<String, Boolean> entry: this.featureData) {
						factory.setFeature(entry.getKey(), entry.getValue().booleanValue());
					}
					for (final Entry<String, Object> entry: this.attributeData) {
						factory.setAttribute(entry.getKey(), entry.getValue());
					}
					return this;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilderFactory#getFeature(String) Fähigkeiten} und gibt ihn zurück.
				 * 
				 * @see DocumentBuilderFactory#setFeature(String, boolean)
				 * @return Konfigurator.
				 */
				public FeatureData openFeatureData() {
					return this.featureData;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für das {@link DocumentBuilderFactory#getSchema() Schema} und gibt ihn zurück.
				 * 
				 * @see DocumentBuilderFactory#setSchema(Schema)
				 * @return Konfigurator.
				 */
				public SchemaData openSchemaData() {
					return this.schemaData;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die Eigenschaften und gibt ihn zurück.
				 * 
				 * @see DocumentBuilderFactory#setCoalescing(boolean)
				 * @see DocumentBuilderFactory#setExpandEntityReferences(boolean)
				 * @see DocumentBuilderFactory#setIgnoringComments(boolean)
				 * @see DocumentBuilderFactory#setIgnoringElementContentWhitespace(boolean)
				 * @see DocumentBuilderFactory#setNamespaceAware(boolean)
				 * @see DocumentBuilderFactory#setValidating(boolean)
				 * @see DocumentBuilderFactory#setXIncludeAware(boolean)
				 * @return Konfigurator.
				 */
				public PropertyData openPropertyData() {
					return this.propertyData;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilderFactory#getAttribute(String) Attribute} und gibt ihn zurück.
				 * 
				 * @see DocumentBuilderFactory#setAttribute(String, Object)
				 * @return Konfigurator.
				 */
				public AttributeData openAttributeData() {
					return this.attributeData;
				}

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
				public DocumentBuilderFactory build() throws IllegalStateException {
					try {
						return this.getFactory();
					} catch (final ParserConfigurationException cause) {
						throw new IllegalStateException(cause);
					}
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public String toString() {
					return Objects.toStringCall(this, this.featureData, this.schemaData, this.propertyData, this.attributeData);
				}

			}

			/**
			 * Diese Klasse implementiert den Konfigurator für den {@link ErrorHandler} einer {@link DocumentBuilder}.
			 * 
			 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
			 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
			 */
			public final class HandlerData extends BaseValueBuilder<ErrorHandler, HandlerData> {

				/**
				 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
				 * 
				 * @return Besitzer.
				 */
				public BuilderData closeListenerData() {
					return BuilderData.this;
				}

				{}

				/**
				 * {@inheritDoc}
				 */
				@Override
				protected HandlerData thiz() {
					return this;
				}

			}

			/**
			 * Diese Klasse implementiert den Konfigurator für den {@link EntityResolver} einer {@link DocumentBuilder}.
			 * 
			 * @see DocumentBuilder#setEntityResolver(EntityResolver)
			 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
			 */
			public final class ResolverData extends BaseValueBuilder<EntityResolver, ResolverData> {

				/**
				 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
				 * 
				 * @return Besitzer.
				 */
				public BuilderData closeResolverData() {
					return BuilderData.this;
				}

				{}

				/**
				 * {@inheritDoc}
				 */
				@Override
				protected ResolverData thiz() {
					return this;
				}

			}

			{}

			/**
			 * Dieses Feld speichert die {@link DocumentBuilderFactory}.
			 */
			DocumentBuilder builder;

			/**
			 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
			 */
			final FactoryData factoryData = //
				new FactoryData();

			/**
			 * Dieses Feld speichert den Konfigurator für {@link #openHandlerData()}.
			 */
			final HandlerData handlerData = //
				new HandlerData();

			/**
			 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
			 */
			final ResolverData resolverData = //
				new ResolverData();

			{}

			/**
			 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
			 * 
			 * @param data Konfigurator oder {@code null}.
			 * @return {@code this}.
			 */
			public BuilderData use(final BuilderData data) {
				if (data == null) return this;
				this.builder = data.builder;
				this.factoryData.use(data.factoryData);
				this.handlerData.use(data.handlerData);
				this.resolverData.use(data.resolverData);
				return this;
			}

			/**
			 * Diese Methode gibt die {@link DocumentBuilder} zurück.<br>
			 * Wenn über {@link #useBuilder(DocumentBuilder)} noch keine {@link DocumentBuilder} gesetzt wurde, wird über
			 * {@link DocumentBuilderFactory#newDocumentBuilder()} eine neue erstellt und über {@link #useBuilder(DocumentBuilder)} gesetzt.
			 * 
			 * @see #useBuilder(DocumentBuilder)
			 * @see #updateBuilder()
			 * @return {@link DocumentBuilder}.
			 * @throws ParserConfigurationException Wenn {@link DocumentBuilderFactory#newDocumentBuilder()} eine entsprechende Ausnahme auslöst.
			 */
			public DocumentBuilder getBuilder() throws ParserConfigurationException {
				DocumentBuilder result = this.builder;
				if (result != null) return result;
				result = this.factoryData.build().newDocumentBuilder();
				this.useBuilder(result);
				return result;
			}

			/**
			 * Diese Methode setzt die {@link DocumentBuilder} und gibt {@code this} zurück.
			 * 
			 * @param builder {@link DocumentBuilder} oder {@code null}.
			 * @return {@code this}.
			 */
			public BuilderData useBuilder(final DocumentBuilder builder) {
				this.builder = builder;
				return this;
			}

			/**
			 * Diese Methode aktualisiert die Einstellungen des {@link DocumentBuilder} und gibt {@code this} zurück.<br>
			 * Bei dieser Aktualisierung werden auf den über {@link #getBuilder()} ermittelte {@link DocumentBuilder} die Einstellungen übertragen, die in
			 * {@link #openHandlerData()} und {@link #openResolverData()} konfiguriert sind.
			 * 
			 * @return {@code this}.
			 * @throws ParserConfigurationException Wenn {@link #getBuilder()} eine entsprechende Ausnahme auslöst.
			 */
			public BuilderData updateBuilder() throws ParserConfigurationException {
				final DocumentBuilder builder = this.getBuilder();
				if (this.handlerData.get() != null) {
					builder.setErrorHandler(this.handlerData.get());
				}
				if (this.resolverData.get() != null) {
					builder.setEntityResolver(this.resolverData.get());
				}
				return this;
			}

			/**
			 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilderFactory} und gibt ihn zurück.
			 * 
			 * @return Konfigurator.
			 */
			public FactoryData openFactoryData() {
				return this.factoryData;
			}

			/**
			 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilder#setErrorHandler(ErrorHandler) Fehlerbehandlung} und gibt ihn zurück.
			 * 
			 * @see DocumentBuilder#setErrorHandler(ErrorHandler)
			 * @return Konfigurator.
			 */
			public HandlerData openHandlerData() {
				return this.handlerData;
			}

			/**
			 * Diese Methode öffnet den Konfigurator für die {@link DocumentBuilder#setEntityResolver(EntityResolver) URL-Auflöser} und gibt ihn zurück.
			 * 
			 * @see DocumentBuilder#setEntityResolver(EntityResolver)
			 * @return Konfigurator.
			 */
			public ResolverData openResolverData() {
				return this.resolverData;
			}

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
			public DocumentBuilder build() throws IllegalStateException {
				try {
					return this.updateBuilder().getBuilder();
				} catch (final ParserConfigurationException cause) {
					throw new IllegalStateException(cause);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(this, this.factoryData, this.handlerData, this.resolverData);
			}

		}

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
		 */
		final SourceData sourceData = //
			new SourceData();

		final BuilderData builderData = //
			new BuilderData();

		{}

		public SourceData openSourceData() {
			return this.sourceData;
		}

		public void parse() {
			final DocumentBuilder builder = this.builderData.build();
			// builder.parse(null);
		}
	}

	public static final class XMLFormatter implements Builder<Transformer> {

		/**
		 * Diese Klasse implementiert den Konfigurator für die Quelldaten eines {@link Transformer}.
		 * 
		 * @see Transformer#transform(Source, Result)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class SourceData extends BaseSourceData<SourceData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public XMLFormatter closeSourceData() {
				return XMLFormatter.this;
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
		 * Diese Klasse implementiert den Konfigurator für die Ausgabeeigenschaften eines {@link Transformer}.
		 * 
		 * @see Transformer#setOutputProperty(String, String)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class PropertyData extends BaseMapBuilder<String, String, PropertyData> {

			/**
			 * Diese Methode wählt {@link OutputKeys#INDENT} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forINDENT() {
				return this.forKey(OutputKeys.INDENT);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#VERSION} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forVERSION() {
				return this.forKey(OutputKeys.VERSION);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#METHOD} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forMETHOD() {
				return this.forKey(OutputKeys.METHOD);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#ENCODING} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forENCODING() {
				return this.forKey(OutputKeys.ENCODING);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#MEDIA_TYPE} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forMEDIA_TYPE() {
				return this.forKey(OutputKeys.MEDIA_TYPE);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#STANDALONE} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forSTANDALONE() {
				return this.forKey(OutputKeys.STANDALONE);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#OMIT_XML_DECLARATION} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forOMIT_XML_DECLARATION() {
				return this.forKey(OutputKeys.OMIT_XML_DECLARATION);
			}

			/**
			 * Diese Methode wählt {@link OutputKeys#CDATA_SECTION_ELEMENTS} und gibt {@code this} zurück.
			 * 
			 * @see #forKey(String)
			 * @return {@code this}.
			 */
			public PropertyData forCDATA_SECTION_ELEMENTS() {
				return this.forKey(OutputKeys.CDATA_SECTION_ELEMENTS);
			}

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public XMLFormatter closePropertyData() {
				return XMLFormatter.this;
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected PropertyData thiz() {
				return this;
			}

		}

		/**
		 * Diese Klasse implementiert den Konfigurator für die Parameter eines {@link Transformer}.
		 * 
		 * @see Transformer#setParameter(String, Object)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class ParameterData extends BaseMapBuilder<String, Object, ParameterData> {

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public XMLFormatter closeParameterData() {
				return XMLFormatter.this;
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected ParameterData thiz() {
				return this;
			}

		}

		/**
		 * Diese Klasse implementiert den Konfigurator für die {@link Templates} zur Erzeugung eines {@link Transformer}.
		 * 
		 * @see TransformerFactory#newTemplates(Source)
		 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 */
		public final class TemplatesData implements Builder<Templates> {

			/**
			 * Diese Klasse implementiert den Konfigurator für die Quelldaten eines {@link Templates}.
			 * 
			 * @see TransformerFactory#newTemplates(Source)
			 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
			 */
			public final class ScriptData extends BaseSourceData<ScriptData> {

				/**
				 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
				 * 
				 * @return Besitzer.
				 */
				public TemplatesData closeSourceData() {
					return TemplatesData.this;
				}

				{}

				/**
				 * {@inheritDoc}
				 */
				@Override
				protected ScriptData thiz() {
					return this;
				}

			}

			/**
			 * Diese Klasse implementiert den Konfigurator für die {@link TransformerFactory} zur Erzeugung eines {@link Templates} oder {@link Transformer}.
			 * 
			 * @see TransformerFactory#newTemplates(Source)
			 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
			 */
			public final class FactoryData implements Builder<TransformerFactory> {

				/**
				 * Diese Klasse implementiert den Konfigurator für die Fähigkeiten einer {@link TransformerFactory}.
				 * 
				 * @see TransformerFactory#setFeature(String, boolean)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class FeatureData extends BaseFeatureData<FeatureData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public FactoryData closeFeatureData() {
						return FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected FeatureData thiz() {
						return this;
					}

				}

				/**
				 * Diese Klasse implementiert den Konfigurator für die Attribute einer {@link TransformerFactory}.
				 * 
				 * @see TransformerFactory#setAttribute(String, Object)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class AttributeData extends BaseMapBuilder<String, String, AttributeData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public FactoryData closeAttributeData() {
						return FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected AttributeData thiz() {
						return this;
					}

				}

				/**
				 * Diese Klasse implementiert den Konfigurator für den {@link ErrorListener} einer {@link TransformerFactory}.
				 * 
				 * @see TransformerFactory#setErrorListener(ErrorListener)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class ListenerData extends BaseValueBuilder<ErrorListener, ListenerData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public FactoryData closeListenerData() {
						return FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected ListenerData thiz() {
						return this;
					}

				}

				/**
				 * Diese Klasse implementiert den Konfigurator für den {@link URIResolver} einer {@link TransformerFactory}.
				 * 
				 * @see TransformerFactory#setURIResolver(URIResolver)
				 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
				 */
				public final class ResolverData extends BaseValueBuilder<URIResolver, ResolverData> {

					/**
					 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
					 * 
					 * @return Besitzer.
					 */
					public FactoryData closeResolverData() {
						return FactoryData.this;
					}

					{}

					/**
					 * {@inheritDoc}
					 */
					@Override
					protected ResolverData thiz() {
						return this;
					}

				}

				{}

				/**
				 * Dieses Feld speichert die {@link TransformerFactory}.
				 */
				TransformerFactory factory;

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openFeatureData()}.
				 */
				final FeatureData featureData = //
					new FeatureData();

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openAttributeData()}.
				 */
				final AttributeData attributeData = //
					new AttributeData();

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openListenerData()}.
				 */
				final ListenerData listenerData = //
					new ListenerData();

				/**
				 * Dieses Feld speichert den Konfigurator für {@link #openResolverData()}.
				 */
				final ResolverData resolverData = //
					new ResolverData();

				{}

				/**
				 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
				 * 
				 * @param data Konfigurator oder {@code null}.
				 * @return {@code this}.
				 */
				public FactoryData use(final FactoryData data) {
					if (data == null) return this;
					this.factory = data.factory;
					this.featureData.use(data.featureData);
					this.attributeData.use(data.attributeData);
					this.listenerData.use(data.listenerData);
					this.resolverData.use(data.resolverData);
					return this;
				}

				/**
				 * Diese Methode gibt die {@link TransformerFactory} zurück.<br>
				 * Wenn über {@link #useFactory(TransformerFactory)} noch keine {@link TransformerFactory} gesetzt wurde, wird über
				 * {@link TransformerFactory#newInstance()} eine neue erstellt und über {@link #useFactory(TransformerFactory)} gesetzt.
				 * 
				 * @see #useFactory(TransformerFactory)
				 * @see #updateFactory()
				 * @return {@link TransformerFactory}.
				 */
				public TransformerFactory getFactory() {
					TransformerFactory result = this.factory;
					if (result != null) return result;
					result = TransformerFactory.newInstance();
					this.useFactory(result);
					return result;
				}

				/**
				 * Diese Methode setzt die {@link TransformerFactory} und gibt {@code this} zurück.
				 * 
				 * @param factory {@link TransformerFactory} oder {@code null}.
				 * @return {@code this}.
				 */
				public FactoryData useFactory(final TransformerFactory factory) {
					this.factory = factory;
					return this;
				}

				/**
				 * Diese Methode aktualisiert die Einstellungen der {@link TransformerFactory} und gibt {@code this} zurück.<br>
				 * Bei dieser Aktualisierung werden auf die über {@link #getFactory()} ermittelte {@link TransformerFactory} die Einstellungen übertragen, die in
				 * {@link #openListenerData()}, {@link #openResolverData()}, {@link #openFeatureData()} und {@link #openAttributeData()} konfiguriert sind.
				 * 
				 * @return {@code this}.
				 * @throws TransformerConfigurationException Wenn {@link TransformerFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
				 */
				public FactoryData updateFactory() throws TransformerConfigurationException {
					final TransformerFactory factory = this.getFactory();
					if (this.resolverData.get() != null) {
						factory.setURIResolver(this.resolverData.get());
					}
					if (this.listenerData.get() != null) {
						factory.setErrorListener(this.listenerData.get());
					}
					for (final Entry<String, Boolean> entry: this.featureData) {
						factory.setFeature(entry.getKey(), entry.getValue().booleanValue());
					}
					for (final Entry<String, String> entry: this.attributeData) {
						factory.setAttribute(entry.getKey(), entry.getValue());
					}
					TemplatesData.this.templates = null;
					return this;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getFeature(String) Fähigkeiten} und gibt ihn zurück.
				 * 
				 * @see TransformerFactory#setFeature(String, boolean)
				 * @return Konfigurator.
				 */
				public FeatureData openFeatureData() {
					return this.featureData;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getAttribute(String) Attribute} der und gibt ihn zurück.
				 * 
				 * @see TransformerFactory#setAttribute(String, Object)
				 * @return Konfigurator.
				 */
				public AttributeData openAttributeData() {
					return this.attributeData;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getErrorListener() Fehlerbehandlung} und gibt ihn zurück.
				 * 
				 * @see TransformerFactory#setErrorListener(ErrorListener)
				 * @return Konfigurator.
				 */
				public ListenerData openListenerData() {
					return this.listenerData;
				}

				/**
				 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory#getURIResolver() URL-Auflöser} und gibt ihn zurück.
				 * 
				 * @see TransformerFactory#setURIResolver(URIResolver)
				 * @return Konfigurator.
				 */
				public ResolverData openResolverData() {
					return this.resolverData;
				}

				/**
				 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
				 * 
				 * @return Besitzer.
				 */
				public TemplatesData closeFactoryData() {
					return TemplatesData.this;
				}

				{}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public TransformerFactory build() throws IllegalStateException {
					try {
						return this.updateFactory().getFactory();
					} catch (final TransformerConfigurationException cause) {
						throw new IllegalStateException(cause);
					}
				}

				/**
				 * {@inheritDoc}
				 */
				@Override
				public String toString() {
					return Objects.toStringCall(this, this.featureData, this.attributeData, this.listenerData, this.resolverData);
				}

			}

			{}

			/**
			 * Dieses Feld speichert die {@link Templates}.
			 */
			Templates templates;

			/**
			 * Dieses Feld speichert den Konfigurator {@link #openScriptData()}.
			 */
			final ScriptData scriptData = //
				new ScriptData();

			/**
			 * Dieses Feld speichert den Konfigurator für {@link #openFactoryData()}.
			 */
			final FactoryData factoryData = //
				new FactoryData();

			{}

			/**
			 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
			 * 
			 * @param data Konfigurator oder {@code null}.
			 * @return {@code this}.
			 */
			public TemplatesData use(final TemplatesData data) {
				if (data == null) return this;
				this.templates = data.templates;
				this.scriptData.use(data.scriptData);
				this.factoryData.use(data.factoryData);
				return this;
			}

			/**
			 * Diese Methode gibt die {@link Templates} zurück.<br>
			 * Wenn über {@link #useTemplates(Templates)} noch keine {@link Templates} gesetzt wurden, werden über {@link TransformerFactory#newTemplates(Source)}
			 * neue erstellt und über {@link #useTemplates(Templates)} gesetzt. Wenn über {@link #openScriptData()} keine Quelldaten konfiguriert sind oder über
			 * {@link #clearTemplates()} die {@link Templates} auf {@code null} gesetzt wurden, wird {@code null} geliefert.
			 * 
			 * @return {@link Templates} oder {@code null}.
			 * @throws TransformerConfigurationException Wenn {@link TransformerFactory#setFeature(String, boolean)} eine entsprechende Ausnahme auslöst.
			 */
			public Templates getTemplates() throws TransformerConfigurationException {
				Templates result = this.templates;
				if (result != null) return result;
				final Source source = this.scriptData.build();
				if (source == null) return null;
				result = this.factoryData.getFactory().newTemplates(source);
				this.useTemplates(result);
				return result;
			}

			/**
			 * Diese Methode setzt die {@link Templates} und gibt {@code this} zurück.
			 * 
			 * @param templates {@link Templates} oder {@code null}.
			 * @return {@code this}.
			 */
			public TemplatesData useTemplates(final Templates templates) {
				this.templates = templates;
				return this;
			}

			/**
			 * Diese Methode leert die {@link Templates} sowie deren Quelldaten und gibt {@code this} zurück.<br>
			 * Damit liefern {@link #getTemplates()} immer {@code null} und {@link #getTransformer()} immer den kopierenden {@link Transformer}.
			 * 
			 * @return {@code this}.
			 */
			public TemplatesData clearTemplates() {
				return this.openScriptData().useSource(null).closeSourceData().useTemplates(null);
			}

			/**
			 * Diese Methode öffnet den Konfigurator für die Transformationsdaten (z.B. xsl-Datei) und gibt ihn zurück.
			 * 
			 * @see TransformerFactory#newTemplates(Source)
			 * @return Konfigurator.
			 */
			public ScriptData openScriptData() {
				return this.scriptData;
			}

			/**
			 * Diese Methode öffnet den Konfigurator für die {@link TransformerFactory} und gibt ihn zurück.
			 * 
			 * @return Konfigurator.
			 */
			public FactoryData openFactoryData() {
				return this.factoryData;
			}

			/**
			 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
			 * 
			 * @return Besitzer.
			 */
			public XMLFormatter closeTemplatesData() {
				return XMLFormatter.this;
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public Templates build() throws IllegalStateException {
				try {
					return this.getTemplates();
				} catch (final TransformerConfigurationException cause) {
					throw new IllegalStateException(cause);
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return Objects.toStringCall(this, this.scriptData, this.factoryData);
			}

		}

		{}

		/**
		 * Dieses Feld speichert den {@link Transformer}.
		 */
		Transformer transformer;

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
		 */
		final SourceData sourceData = //
			new SourceData();

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openResultData()}.
		 */
		final XMLResultData<XMLFormatter> resultData = //
			new XMLResultData<>(this);

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openPropertyData()}.
		 */
		final PropertyData propertyData = //
			new PropertyData();

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openParameterData()}.
		 */
		final ParameterData parameterData = //
			new ParameterData();

		/**
		 * Dieses Feld speichert den Konfigurator {@link #openTemplatesData()}.
		 */
		final TemplatesData templatesData = //
			new TemplatesData();

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public XMLFormatter use(final XMLFormatter data) {
			if (data == null) return this;
			this.sourceData.use(data.sourceData);
			this.resultData.use(data.resultData);
			this.templatesData.use(data.templatesData);
			this.propertyData.use(data.propertyData);
			this.parameterData.use(data.parameterData);
			return this;
		}

		public Transformer getTransformer() throws TransformerConfigurationException {
			Transformer result = this.transformer;
			if (result != null) return result;
			final Templates templates = this.templatesData.getTemplates();
			final TransformerFactory factory = this.templatesData.factoryData.getFactory();
			result = templates != null ? templates.newTransformer() : factory.newTransformer();
			this.useTransformer(this.transformer);
			return result;
		}

		public XMLFormatter useTransformer(final Transformer transformer) {
			this.transformer = transformer;
			return this;
		}

		public XMLFormatter updateTransformer() throws TransformerConfigurationException {
			final Transformer result = this.getTransformer();
			for (final Entry<String, Object> entry: this.parameterData) {
				result.setParameter(entry.getKey(), entry.getValue());
			}
			for (final Entry<String, String> entry: this.propertyData) {
				result.setOutputProperty(entry.getKey(), entry.getValue());
			}
			return this;
		}

		public SourceData openSourceData() {
			return this.sourceData;
		}

		public XMLResultData<XMLFormatter> openResultData() {
			return this.resultData;
		}

		public PropertyData openPropertyData() {
			return this.propertyData;
		}

		public ParameterData openParameterData() {
			return this.parameterData;
		}

		public TemplatesData openTemplatesData() {
			return this.templatesData;
		}

		public XMLFormatter transform() throws TransformerException {
			final Transformer transformer = this.getTransformer();
			synchronized (transformer) {
				transformer.transform(this.sourceData.build(), this.resultData.build());
			}
			return this;
		}

		public String transformToText() throws TransformerException {
			final StringWriter result = new StringWriter();
			this.openResultData().useWriter(result).closeResultData().transform();
			return result.toString();
		}

		public Node transformToNode() throws TransformerException {
			final DOMResult result = new DOMResult();
			this.openResultData().useResult(result).closeResultData().transform();
			return result.getNode();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Transformer build() throws IllegalStateException {
			try {
				return this.updateTransformer().getTransformer();
			} catch (final TransformerConfigurationException cause) {
				throw new IllegalStateException(cause);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.sourceData, this.resultData, this.propertyData, this.parameterData, this.templatesData);
		}

	}

	{}
	{}
	{}
	{}
	{}

	/**
	 * Diese Klasse implementiert eine gepufferte {@link XPath XPath-Auswertungsumgebung}, die die von einer gegebenen {@link XPath XPath-Auswertungsumgebung}
	 * erzeugten {@link XPathExpression XPath-Ausdrücke} in einer {@link Map Abbildung} von Schlüsseln auf Werte verwaltet. Die Schlüssel werden dabei über
	 * {@link Pointer} auf Zeichenketten und die Werte als {@link Pointer} auf die {@link XPathExpression XPath-Ausdrücke} des gegebenen {@link XPath
	 * XPath-Auswertungsumgebung} realisiert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Converters#cachedConverter(int, int, int, Converter)
	 */
	public static final class CachedXPath implements XPath, Converter<String, XPathExpression> {

		/**
		 * Dieses Feld speichert die {@link XPath XPath-Auswertungsumgebung}.
		 */
		final XPath xpath;

		/**
		 * Dieses Feld speichert den {@link CachedConverter Cached-Converter}.
		 */
		final CachedConverter<String, XPathExpression> converter;

		/**
		 * Dieser Konstruktor initialisiert die {@link XPath XPath-Auswertungsumgebung}.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
		 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der Abbildung erzeugt werden.
		 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der Abbildung erzeugt werden.
		 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
		 */
		public CachedXPath(final int limit, final int inputMode, final int outputMode, final XPath xpath) {
			this.xpath = xpath;
			this.converter = new CachedConverter<String, XPathExpression>(limit, inputMode, outputMode, this);
		}

		/**
		 * Diese Methode leert die Abbildung.
		 * 
		 * @see CachedConverter#clear()
		 */
		public void clear() {
			this.converter.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void reset() {
			this.converter.clear();
			this.xpath.reset();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathExpression convert(final String input) {
			try {
				return this.xpath.compile(input);
			} catch (final XPathExpressionException e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathExpression compile(final String expression) throws XPathExpressionException {
			try {
				return this.converter.convert(expression);
			} catch (final RuntimeException e) {
				final Throwable cause = e.getCause();
				if (cause instanceof XPathExpressionException) throw (XPathExpressionException)cause;
				throw e;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object evaluate(final String expression, final Object item, final QName returnType) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			if (returnType == null) throw new NullPointerException("returnType is null");
			return this.compile(expression).evaluate(item, returnType);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String evaluate(final String expression, final Object item) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			return this.compile(expression).evaluate(item);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object evaluate(final String expression, final InputSource source, final QName returnType) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			if (source == null) throw new NullPointerException("source is null");
			if (returnType == null) throw new NullPointerException("returnType is null");
			return this.compile(expression).evaluate(source, returnType);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String evaluate(final String expression, final InputSource source) throws XPathExpressionException {
			if (expression == null) throw new NullPointerException("expression is null");
			if (source == null) throw new NullPointerException("source is null");
			return this.compile(expression).evaluate(source);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathVariableResolver getXPathVariableResolver() {
			return this.xpath.getXPathVariableResolver();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXPathVariableResolver(final XPathVariableResolver resolver) {
			this.converter.clear();
			this.xpath.setXPathVariableResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public XPathFunctionResolver getXPathFunctionResolver() {
			return this.xpath.getXPathFunctionResolver();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setXPathFunctionResolver(final XPathFunctionResolver resolver) {
			this.converter.clear();
			this.xpath.setXPathFunctionResolver(resolver);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public NamespaceContext getNamespaceContext() {
			return this.xpath.getNamespaceContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setNamespaceContext(final NamespaceContext nsContext) {
			this.converter.clear();
			this.xpath.setNamespaceContext(nsContext);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.xpath);
		}

	}

	/**
	 * Diese Klasse implementiert ein Objekt zur vereinfachten Befüllung eines {@link Document}s mit {@link Attr}-, {@link Text}-, {@link Comment} und
	 * {@link Element}-Knoten. Die Methoden zur Erzeugung von Knoten geben diesen oder einen neuen {@link NodeBuilder} zurück, sodass mehrere Befehle analog zu
	 * einem {@link StringBuilder} hintereinander geschrieben werden können.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NodeBuilder {

		/**
		 * Dieses Feld speichert den aktuelle Knoten.
		 */
		final Node node;

		/**
		 * Dieses Feld speichert den aufrufenden {@link NodeBuilder}.
		 */
		final NodeBuilder owner;

		/**
		 * Dieses Feld speichert die aktuelle Position.
		 */
		int index;

		/**
		 * Dieser Konstruktor initialisiert den aktuellen Knoten und den aufrufenden {@link NodeBuilder}.
		 * 
		 * @param node aktuellen Knoten.
		 * @param owner aufrufender {@link NodeBuilder}
		 */
		private NodeBuilder(final Node node, final NodeBuilder owner) {
			this.node = node;
			this.owner = owner;
		}

		/**
		 * Dieser Konstruktor initialisiert den {@link Node}, das über diesen {@link NodeBuilder} befüllt werden soll.
		 * 
		 * @param node {@link Node}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public NodeBuilder(final Node node) throws NullPointerException {
			if (node == null) throw new NullPointerException();
			this.node = node;
			this.owner = null;
		}

		/**
		 * Diese Methode gibt den aktuellen Knoten zurück, in dennen Kontext Kind- und Attributknoten erzeugt werden. Dieser kann nur ein {@link Element} oder ein
		 * {@link Document} sein.
		 * 
		 * @see Element
		 * @see Document
		 * @return aktueller Knoten.
		 */
		public Node node() {
			return this.node;
		}

		/**
		 * Diese Methode gibt das {@link Document} des {@link #node() aktuellen Knoten} zurück.
		 * 
		 * @see Node#getOwnerDocument()
		 * @return {@link Document} des {@link #node() aktuellen Knoten}.
		 */
		public Document document() {
			final Node node = this.node.getOwnerDocument();
			if (node.getNodeType() == Node.DOCUMENT_NODE) return (Document)node;
			return node.getOwnerDocument();
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code index() - 1} aus und gibt {@code this} das zurück.
		 * 
		 * @see #hasPrev()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn es keine vorherige Position gibt.
		 */
		public NodeBuilder prev() throws IllegalStateException {
			if (!this.hasPrev()) throw new IllegalStateException();
			this.index--;
			return this;
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position} auf {@code index() + 1} aus und gibt {@code this} das zurück.
		 * 
		 * @see #hasNext()
		 * @return {@code this}.
		 * @throws IllegalStateException Wenn es keine nächste Position gibt.
		 */
		public NodeBuilder next() throws IllegalStateException {
			if (!this.hasNext()) throw new IllegalStateException();
			this.index++;
			return this;
		}

		/**
		 * Diese Methode setzt die {@link #index() aktuelle Position}, an welcher neue Kindelemente eingefügt werden. Negative Positionen zählen vom Ende der
		 * Kindknotenliste, d.h. {@code -1 => length()}, {@code -2 => (length() - 1)} usw.
		 * 
		 * @see #index()
		 * @see #length()
		 * @param index Positioh.
		 * @return {@code this}.
		 * @throws IllegalArgumentException Wenn {@code index > length()} oder {@code (-index - 1) > length()}.
		 */
		public NodeBuilder seek(final int index) throws IllegalArgumentException {
			final int length = this.length(), offset = index >= 0 ? index : length + index + 1;
			if ((offset < 0) || (offset > length)) throw new IllegalArgumentException();
			this.index = offset;
			return this;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} größer als {@code 0} ist.
		 * 
		 * @see #index()
		 * @see #length()
		 * @return {@code true}, wenn {@code index() > 0}.
		 */
		public boolean hasPrev() {
			return this.index > 0;
		}

		/**
		 * Diese Methode gibt nur dann {@code true} zurück, wenn die {@link #index() aktuelle Position} kleiner als {@link #length()} ist.
		 * 
		 * @see #index()
		 * @see #length()
		 * @return {@code true}, wenn {@code index() < length()}.
		 */
		public boolean hasNext() {
			return this.index < this.length();
		}

		/**
		 * Diese Methode gibt die aktuelle Position zurück, an welcher neue Kindelemente eingefügt werden. für eine gültige Position gilt
		 * {@code 0 <= index() <= length()}.
		 * 
		 * @see #seek(int)
		 * @see #length()
		 * @return aktuelle Position.
		 */
		public int index() {
			return this.index;
		}

		/**
		 * Diese Methode gibt die Anzahl der Kondknoten zurück.
		 * 
		 * @see Node#getChildNodes()
		 * @see NodeList#getLength()
		 * @return Anzahl der Kondknoten.
		 */
		public int length() {
			return this.node().getChildNodes().getLength();
		}

		/**
		 * Diese Methode gibt einen neuen {@link NodeBuilder} zu dem {@link Element} zurück, das sich in der Kondknotenliste an der {@link #index() aktuellen
		 * Position} befindet.
		 * 
		 * @see #index()
		 * @return {@link NodeBuilder} zum {@link Element} an der {@link #index() aktuellen Position}.
		 * @throws IllegalStateException Wenn die Kondknotenliste an der {@link #index() aktuellen Position} kein {@link Element} enthält.
		 */
		public NodeBuilder open() throws IllegalStateException {
			final Node node = NodeBuilder.this.node().getChildNodes().item(NodeBuilder.this.index);
			if (!(node instanceof Element)) throw new IllegalStateException();
			return new NodeBuilder(node, this);
		}

		/**
		 * Diese Methode schließt die Bearbeitung des {@link #node() aktuelle Knoten} ab und gibt den {@link NodeBuilder} zurück, von dem aus dieser erzeugt wurde.
		 * 
		 * @see #open()
		 * @see #element(String)
		 * @see #element(String, String)
		 * @return erzeugenden {@link NodeBuilder}.
		 * @throws IllegalStateException Wenn es keinen erzeugenden {@link NodeBuilder} gibt.
		 */
		public NodeBuilder close() throws IllegalStateException {
			if (this.owner == null) throw new IllegalStateException();
			return this.owner;
		}

		/**
		 * Diese Methode fügt den gegebenen {@link Node} an der {@link #index() aktuellen Position} ein, bewegt die {@link #index() aktuelle Position} hinter den
		 * eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #index()
		 * @see Node#insertBefore(Node, Node)
		 * @param node {@link Node}.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder insert(final Node node) throws DOMException {
			final Node root = this.node(), next = root.getChildNodes().item(this.index);
			root.insertBefore(node, next);
			this.index++;
			return this;
		}

		/**
		 * Diese Methode entfernt den Kindknoten an der {@link #index() aktuellen Position} und gibt {@code this} zurück.
		 * 
		 * @see #index()
		 * @see Node#removeChild(Node)
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn die Kondknotenliste an der {@link #index() aktuellen Position} kein Knoten existiert.
		 */
		public NodeBuilder delete() throws DOMException, IllegalStateException {
			final Node root = this.node(), next = root.getChildNodes().item(this.index);
			if (next == null) throw new IllegalStateException();
			root.removeChild(next);
			return this;
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Text} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #insert(Node)
		 * @see Document#createTextNode(String)
		 * @param text Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder text(final String text) throws DOMException {
			return this.insert(this.document().createTextNode(text));
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} einen neuen {@link Comment} mit dem gegebenen Wert ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt {@code this} zurück.
		 * 
		 * @see #insert(Node)
		 * @see Document#createComment(String)
		 * @param text Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder comment(final String text) throws DOMException {
			return this.insert(this.document().createComment(text));
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit dem gegebenen Namen ein, bewegt die {@link #index() aktuelle
		 * Position} hinter den eingefügten Knoten und gibt einen neuen {@link NodeBuilder} zum eingefügten {@link Element} zurück.
		 * 
		 * @see Document#createElement(String)
		 * @param name Name.
		 * @return {@link NodeBuilder} zum erzeugten Knoten.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder element(final String name) throws DOMException {
			return this.insert(this.document().createElement(name)).prev().open();
		}

		/**
		 * Diese Methode fügt an der {@link #index() aktuellen Position} ein neues {@link Element} mit den gegebenen Eigenschaften ein, bewegt die {@link #index()
		 * aktuelle Position} hinter den eingefügten Knoten und gibt einen neuen {@link NodeBuilder} zum eingefügten {@link Element} zurück.
		 * 
		 * @see Document#createElementNS(String, String)
		 * @param uri Uri.
		 * @param name Name.
		 * @return {@link NodeBuilder} zum erzeugten Knoten.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 */
		public NodeBuilder element(final String uri, final String name) throws DOMException {
			return this.insert(this.document().createElementNS(uri, name)).prev().open();
		}

		/**
		 * Diese Methode modifiziert das {@link Attr} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Wenn der gegebene Wert {@code null} ist, wird
		 * das {@link Attr} entfernt. Andernfalls wird es erzeugt bzw. geändert.
		 * 
		 * @see Element#setAttribute(String, String)
		 * @see Element#removeAttribute(String)
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn das {@link #node() aktuelle Knoten} kein {@link Element} ist.
		 */
		public NodeBuilder attribute(final String name, final String value) throws DOMException, IllegalStateException {
			if (this.owner == null) throw new IllegalStateException();
			if (value == null) {
				((Element)this.node).removeAttribute(name);
			} else {
				((Element)this.node).setAttribute(name, value);
			}
			return this;
		}

		/**
		 * Diese Methode modifiziert das {@link Attr} mit den gegebenen Eigenschaften und gibt {@code this} zurück. Wenn der gegebene Wert {@code null} ist, wird
		 * das {@link Attr} entfernt. Andernfalls wird es erzeugt bzw. geändert.
		 * 
		 * @see Element#setAttributeNS(String, String, String)
		 * @see Element#removeAttributeNS(String, String)
		 * @param uri Uri.
		 * @param name Name.
		 * @param value Wert.
		 * @return {@code this}.
		 * @throws DOMException Wenn Eingaben oder Modifikation ungültig sind.
		 * @throws IllegalStateException Wenn das {@link #node() aktuelle Knoten} kein {@link Element} ist.
		 */
		public NodeBuilder attribute(final String uri, final String name, final String value) throws DOMException, IllegalStateException {
			if (this.owner == null) throw new IllegalStateException();
			if (value == null) {
				((Element)this.node).removeAttributeNS(uri, name);
			} else {
				((Element)this.node).setAttributeNS(uri, name, value);
			}
			return this;
		}

	}

	/**
	 * Dieses Feld speichert den {@link CachedBuilder Cached-Builder} zur Erzeugung bzw. Bereitstellung einer {@link CachedXPath Cached-XPath-Auswertungsumgebung}
	 * .
	 */
	static final Builder<XPath> CACHED_XPATH_BUILDER = Builders.synchronizedBuilder(Builders.cachedBuilder(new Builder<XPath>() {

		@Override
		public XPath build() {
			try {
				return XML.cachedXPath();
			} catch (final XPathFactoryConfigurationException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public String toString() {
			return Objects.toStringCall("XpathBuilder");
		}

	}));

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des gegebenen {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten}
	 * {@code null} ist, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see Node#getTextContent()
	 * @param node {@link Node DOM-Knoten}.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert}.
	 */
	public static String value(final Node node, final String defaultValue) {
		return ((node == null) ? defaultValue : node.getTextContent());
	}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des über seinen Namen gegebenen {@link Attr DOM-Attributs} des gegebenen {@link Node
	 * DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist oder kein {@link Attr DOM-Attribut} mit dem gegebenen Namen gefunden werden
	 * kann, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see XML#value(Node, String)
	 * @see Element#getAttributeNode(String)
	 * @param node {@link Node DOM-Knoten}.
	 * @param name Attribut-Name.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert} oder Standardwert.
	 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
	 */
	public static String value(final Node node, final String name, final String defaultValue) throws NullPointerException {
		if (name == null) throw new NullPointerException("name is null");
		if ((node == null) || (node.getNodeType() != Node.ELEMENT_NODE)) return defaultValue;
		return XML.value(((Element)node).getAttributeNode(name), defaultValue);
	}

	/**
	 * Diese Methode gibt den {@link Node#getTextContent() Knotenwert} des über seinen Namen und Namensraum gegebenen {@link Attr DOM-Attributs} des gegebenen
	 * {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist oder kein {@link Attr DOM-Attribut} mit dem gegebenen Namen und
	 * Namensraum gefunden werden kann, wird der gegebenen Standardwert zurück gegeben.
	 * 
	 * @see XML#value(Node, String)
	 * @see Element#getAttributeNodeNS(String, String)
	 * @param node {@link Node DOM-Knoten}.
	 * @param localName Attribut-Name.
	 * @param namespaceURI Namensraum.
	 * @param defaultValue Standardwert.
	 * @return {@link Node#getTextContent() Knotenwert} oder Standardwert.
	 * @throws NullPointerException Wenn der gegebene Attribut-Name {@code null} ist.
	 */
	public static String value(final Node node, final String namespaceURI, final String localName, final String defaultValue) throws NullPointerException {
		if (namespaceURI == null) throw new NullPointerException("namespaceURI is null");
		if (localName == null) throw new NullPointerException("localName is null");
		if ((node == null) || (node.getNodeType() != Node.ELEMENT_NODE)) return defaultValue;
		return XML.value(((Element)node).getAttributeNodeNS(namespaceURI, localName), defaultValue);
	}

	/**
	 * Diese Methode erzeugt neue {@link XMLXPathOptions} und gibt diese zurück.
	 * 
	 * @see XMLXPathOptions#XPathOptions()
	 * @return {@link XMLXPathOptions}.
	 */
	public static XMLXPathOptions xpathOptions() {
		return new XMLXPathOptions();
	}

	/**
	 * Diese Methode erzeugt neue {@link XMLParseOptions} und gibt diese zurück.
	 * 
	 * @see XMLParseOptions#ParseOptions()
	 * @return {@link XMLParseOptions}.
	 */
	public static XMLParseOptions parseOptions() {
		return new XMLParseOptions();
	}

	/**
	 * Diese Methode erzeugt einen {@link NodeBuilder} für das gegebene {@link Document} und gibt ihn zurück.
	 * 
	 * @param document {@link Document}.
	 * @return {@link NodeBuilder}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static NodeBuilder builder(final Document document) throws NullPointerException {
		return new NodeBuilder(document);
	}

	/**
	 * Diese Methode erzeugt eine {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XMLXPathOptions#DEFAULT
	 * @return {@link XPath XPath-Auswertungsumgebung}.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath createXPath() throws XPathFactoryConfigurationException {
		return XML.createXPath(XMLXPathOptions.DEFAULT);
	}

	/**
	 * Diese Methode erzeugt eine {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @param options {@link XMLXPathOptions XPath-Kontext-Optionen}.
	 * @return {@link XPath XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebenen {@link XMLXPathOptions XPath-Kontext-Optionen} {@code null} sind.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath createXPath(final XMLXPathOptions options) throws NullPointerException, XPathFactoryConfigurationException {
		if (options == null) throw new NullPointerException("options is null");
		return options.applyTo(options.applyTo(XPathFactory.newInstance()).newXPath());
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XMLXPathOptions#DEFAULT
	 * @see XML#cachedXPath(XMLXPathOptions)
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath cachedXPath() throws XPathFactoryConfigurationException {
		return XML.cachedXPath(XMLXPathOptions.DEFAULT);
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück. Die erzeugte {@link XPath XPath-Auswertungsumgebung}
	 * verwaltet die von der gegebenen {@link XPath XPath-Auswertungsumgebung} erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die Schlüssel
	 * werden dabei über {@link SoftPointer} auf Zeichenketten und die Werte als {@link SoftPointer} auf die {@link XPathExpression XPath-Ausdrücke} der gegebenen
	 * {@link XPath XPath-Auswertungsumgebung} realisiert. Die Anzahl der Einträge in der {@link Map} sind nicht beschränkt.
	 * 
	 * @see XML#cachedXPath(int, int, int, XPath)
	 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 */
	public static XPath cachedXPath(final XPath xpath) throws NullPointerException {
		if (xpath == null) throw new NullPointerException("xpath is null");
		return XML.cachedXPath(-1, Pointers.SOFT, Pointers.SOFT, xpath);
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XML#createXPath(XMLXPathOptions)
	 * @see XML#cachedXPath(int, int, int, XPath)
	 * @param options {@link XMLXPathOptions XPath-Kontext-Optionen}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebenen {@link XMLXPathOptions XPath-Kontext-Optionen} {@code null} sind.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath cachedXPath(final XMLXPathOptions options) throws NullPointerException, XPathFactoryConfigurationException {
		if (options == null) throw new NullPointerException("options is null");
		return XML.cachedXPath(XML.createXPath(options));
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück. Die erzeugte {@link XPath XPath-Auswertungsumgebung}
	 * verwaltet die von der gegebenen {@link XPath XPath-Auswertungsumgebung} erzeugten {@link XPathExpression XPath-Ausdrücke} in einer {@link Map} von
	 * Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link Pointer} auf Zeichenketten und die Werte als {@link Pointer} auf die {@link XPathExpression
	 * XPath-Ausdrücke} des gegebenen {@link XPath XPath-Auswertungsumgebung} realisiert.
	 * 
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map} erzeugt werden.
	 * @param xpath {@link XPath XPath-Auswertungsumgebung}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 */
	public static XPath cachedXPath(final int limit, final int inputMode, final int outputMode, final XPath xpath) throws NullPointerException {
		if (xpath == null) throw new NullPointerException("xpath is null");
		return new CachedXPath(limit, inputMode, outputMode, xpath);
	}

	/**
	 * Diese Methode erzeugt eine gepufferte {@link XPath XPath-Auswertungsumgebung} und gibt diese zurück.
	 * 
	 * @see XML#createXPath(XMLXPathOptions)
	 * @see XML#cachedXPath(int, int, int, XPath)
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map} erzeugt werden.
	 * @param options {@link XMLXPathOptions XPath-Kontext-Optionen}.
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}.
	 * @throws NullPointerException Wenn die gegebenen {@link XMLXPathOptions XPath-Kontext-Optionen} {@code null} sind.
	 * @throws XPathFactoryConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static XPath cachedXPath(final int limit, final int inputMode, final int outputMode, final XMLXPathOptions options) throws NullPointerException,
		XPathFactoryConfigurationException {
		if (options == null) throw new NullPointerException("options is null");
		return XML.cachedXPath(limit, inputMode, outputMode, XML.createXPath(options));
	}

	/**
	 * Diese Methode gibt den {@link SynchronizedBuilder Synchronized-}-{@link CachedBuilder Cached}-{@link Builder Builder} zur Erzeugung bzw. Bereitstellung
	 * einer {@link CachedXPath Cached-XPath-Auswertungsumgebung} zurück.
	 * 
	 * @return {@link CachedXPath Cached-XPath-Auswertungsumgebung}-{@link Builder}.
	 */
	public static Builder<XPath> cachedXPathBuilder() {
		return XML.CACHED_XPATH_BUILDER;
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateNode(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Node DOM-Knoten}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Node evaluateNode(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (script == null) throw new NullPointerException("script is null");
		return XML.evaluateNode(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#NODE
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Node DOM-Knoten}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Node evaluateNode(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		synchronized (source) {
			return (Node)source.evaluate(input, XPathConstants.NODE);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Node DOM-Knoten} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateNode(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Node DOM-Knoten}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Node evaluateNode(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		if (script == null) throw new NullPointerException("script is null");
		synchronized (source) {
			return XML.evaluateNode(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link NodeList DOM-Knotenliste} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateNodeSet(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link NodeList DOM-Knotenliste}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static NodeList evaluateNodeSet(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (script == null) throw new NullPointerException("script is null");
		return XML.evaluateNodeSet(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link NodeList DOM-Knotenliste} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XPathConstants#NODESET
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link NodeList DOM-Knotenliste}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static NodeList evaluateNodeSet(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		synchronized (source) {
			return (NodeList)source.evaluate(input, XPathConstants.NODESET);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link NodeList DOM-Knotenliste} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateNodeSet(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link NodeList DOM-Knotenliste}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static NodeList evaluateNodeSet(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		if (script == null) throw new NullPointerException("script is null");
		synchronized (source) {
			return XML.evaluateNodeSet(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link String Textwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateString(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link String Textwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static String evaluateString(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (script == null) throw new NullPointerException("script is null");
		return XML.evaluateString(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link String Textwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#NODE
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link String Textwert}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static String evaluateString(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		synchronized (source) {
			return (String)source.evaluate(input, XPathConstants.STRING);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link String Textwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateString(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link String Textwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static String evaluateString(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		if (script == null) throw new NullPointerException("script is null");
		synchronized (source) {
			return XML.evaluateString(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Boolean Wahrheitswert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateBoolean(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Boolean Wahrheitswert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Boolean evaluateBoolean(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (script == null) throw new NullPointerException("script is null");
		return XML.evaluateBoolean(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link Boolean Wahrheitswert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#BOOLEAN
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Boolean Wahrheitswert}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Boolean evaluateBoolean(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		synchronized (source) {
			return (Boolean)source.evaluate(input, XPathConstants.BOOLEAN);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Boolean Wahrheitswert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateBoolean(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Boolean Wahrheitswert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Boolean evaluateBoolean(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		if (script == null) throw new NullPointerException("script is null");
		synchronized (source) {
			return XML.evaluateBoolean(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Number Zahlenwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}.
	 * 
	 * @see XML#cachedXPathBuilder()
	 * @see XML#evaluateNumber(XPath, String, Node)
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Number Zahlenwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Number evaluateNumber(final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (script == null) throw new NullPointerException("script is null");
		return XML.evaluateNumber(XML.CACHED_XPATH_BUILDER.build(), script, input);
	}

	/**
	 * Diese Methode wertet den gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das Ergebnis als
	 * {@link Number Zahlenwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren {@link Document
	 * DOM-Dokument}.
	 * 
	 * @see XPathConstants#NUMBER
	 * @see XPathExpression#evaluate(Object, QName)
	 * @param source {@link XPathExpression XPath-Ausdruck}.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Number Zahlenwert}.
	 * @throws NullPointerException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Number evaluateNumber(final XPathExpression source, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		synchronized (source) {
			return (Number)source.evaluate(input, XPathConstants.NUMBER);
		}
	}

	/**
	 * Diese Methode wertet den als Zeichenkette gegebenen {@link XPathExpression XPath-Ausdruck} auf dem gegebenen {@link Node DOM-Knoten} aus und gibt das
	 * Ergebnis als {@link Number Zahlenwert} zurück. Wenn der gegebene {@link Node DOM-Knoten} {@code null} ist, erfolgt die Auswertung auf einem leeren
	 * {@link Document DOM-Dokument}. Der {@link XPathExpression XPath-Ausdruck} wird über die gegebene {@link XPath XPath-Auswertungsumgebung} erzeugt.
	 * 
	 * @see XPath#compile(String)
	 * @see XML#evaluateNumber(XPathExpression, Node)
	 * @param source {@link XPath XPath-Auswertungsumgebung}.
	 * @param script {@link XPathExpression XPath-Ausdruck} als Zeichenkette.
	 * @param input {@link Node DOM-Knoten} oder {@code null}.
	 * @return {@link Number Zahlenwert}.
	 * @throws NullPointerException Wenn die gegebene Zeichenkette bzw. die gegebene {@link XPath XPath-Auswertungsumgebung} {@code null} ist.
	 * @throws XPathExpressionException Wenn der gegebenen {@link XPathExpression XPath-Ausdruck} nicht ausgewertet werden kann.
	 */
	public static Number evaluateNumber(final XPath source, final String script, final Node input) throws NullPointerException, XPathExpressionException {
		if (source == null) throw new NullPointerException("source is null");
		if (script == null) throw new NullPointerException("script is null");
		synchronized (source) {
			return XML.evaluateNumber(source.compile(script), input);
		}
	}

	/**
	 * Diese Methode erzeugt ein neues {@link Document DOM-Dokument} und gibt es zurück.
	 * 
	 * @see XMLParseOptions#DEFAULT
	 * @see DocumentBuilderFactory#newInstance()
	 * @see DocumentBuilder#newDocument()
	 * @return {@link Document DOM-Dokument}.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument() throws ParserConfigurationException {
		return XML.createDocument(XMLParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode erzeugt ein neues {@link Document Document} und gibt es zurück.
	 * 
	 * @param options {@link XMLParseOptions ParseOptions}.
	 * @see DocumentBuilderFactory#newInstance()
	 * @see DocumentBuilder#newDocument()
	 * @return {@link Document Document}.
	 * @throws NullPointerException Wenn die gegebenen {@link XMLParseOptions Parse-Optionen} {@code null} sind.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final XMLParseOptions options) throws NullPointerException, ParserConfigurationException {
		if (options == null) throw new NullPointerException("options is null");
		return options.applyTo(options.applyTo(DocumentBuilderFactory.newInstance()).newDocumentBuilder()).newDocument();
	}

	/**
	 * Diese Methode konvertiert das als {@link String Zeichenkette} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XMLParseOptions#DEFAULT
	 * @see XML#createDocument(String, XMLParseOptions)
	 * @param source Quelldokument als {@link String Zeichenkette}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt.
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final String source) throws NullPointerException, SAXException, IOException, ParserConfigurationException {
		if (source == null) throw new NullPointerException("source is null");
		return XML.createDocument(source, XMLParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link String Zeichenkette} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XML#createDocument(Reader, XMLParseOptions)
	 * @param source Quelldokument als {@link String Zeichenkette}.
	 * @param options {@link XMLParseOptions ParseOptions}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die gegebenen {@link XMLParseOptions Parse-Optionen} {@code null} sind.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final String source, final XMLParseOptions options) throws NullPointerException, SAXException, IOException,
		ParserConfigurationException {
		if (source == null) throw new NullPointerException("source is null");
		if (options == null) throw new NullPointerException("options is null");
		return XML.createDocument(new StringReader(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link InputSource InputSource} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XMLParseOptions#DEFAULT
	 * @see XML#createDocument(Reader, XMLParseOptions)
	 * @param source Quelldokument als {@link InputSource InputSource}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt.
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final Reader source) throws NullPointerException, SAXException, IOException, ParserConfigurationException {
		if (source == null) throw new NullPointerException("source is null");
		return XML.createDocument(source, XMLParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link Reader Reader} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XML#createDocument(InputSource, XMLParseOptions)
	 * @param source Quelldokument als {@link Reader Reader}.
	 * @param options {@link XMLParseOptions ParseOptions}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die gegebenen {@link XMLParseOptions Parse-Optionen} {@code null} sind.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final Reader source, final XMLParseOptions options) throws NullPointerException, SAXException, IOException,
		ParserConfigurationException {
		if (source == null) throw new NullPointerException("source is null");
		if (options == null) throw new NullPointerException("options is null");
		return XML.createDocument(new InputSource(source), options);
	}

	/**
	 * Diese Methode konvertiert das als {@link InputSource InputSource} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @see XMLParseOptions#DEFAULT
	 * @see XML#createDocument(InputSource, XMLParseOptions)
	 * @param source Quelldokument als {@link InputSource InputSource}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument {@code null} ist.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt.
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final InputSource source) throws NullPointerException, SAXException, IOException, ParserConfigurationException {
		if (source == null) throw new NullPointerException("source is null");
		return XML.createDocument(source, XMLParseOptions.DEFAULT);
	}

	/**
	 * Diese Methode konvertiert das als {@link InputSource InputSource} gegebene Quelldokument zu einem {@link Document DOM-Dokument} und gibt dieses zurück.
	 * 
	 * @param source Quelldokument als {@link InputSource InputSource}.
	 * @param options {@link XMLParseOptions ParseOptions}.
	 * @return {@link Document DOM-Dokument}.
	 * @throws NullPointerException Wenn das gegebene Quelldokument oder die gegebenen {@link XMLParseOptions Parse-Optionen} {@code null} sind.
	 * @throws SAXException Wenn die Konvertierung des gegebenen Quelldokuments fehlschlägt
	 * @throws IOException Wenn das Lesen des gegebenen Quelldokuments fehlschlägt.
	 * @throws ParserConfigurationException Wenn eines der Feature nicht unterstützt wird.
	 */
	public static Document createDocument(final InputSource source, final XMLParseOptions options) throws NullPointerException, SAXException, IOException,
		ParserConfigurationException {
		if (source == null) throw new NullPointerException("source is null");
		if (options == null) throw new NullPointerException("options is null");
		return options.applyTo(options.applyTo(DocumentBuilderFactory.newInstance()).newDocumentBuilder()).parse(source);
	}

}
