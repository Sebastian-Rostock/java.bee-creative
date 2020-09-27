package bee.creative.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator einer {@link Source} oder {@link InputSource}, die für die Eingabedaten eines {@link Schema},
 * {@link Validator}, {@link DocumentBuilder} bzw. {@link Transformer} genutzt wird.
 *
 * @see DocumentBuilder#parse(InputSource)
 * @see SchemaFactory#newSchema(Source)
 * @see Transformer#transform(Source, Result)
 * @see TransformerFactory#newTemplates(Source)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseSourceData<GThis> extends BaseBuilder<Source, GThis> {

	/** Dieses Feld speichert die Quelldaten. */
	Source source;

	/** Dieses Feld speichert den System-Identifikator. */
	String systemId;

	public final GThis use(final Object object) throws MalformedURLException  {
		if(object instanceof URI) return useUri((URI)object);
		if(object instanceof URL) return useUrl((URL)object);
		if(object instanceof File) return useFile((File)object);
		if(object instanceof String) return useText((String)object);
		if(object instanceof Node) return useNode((Node)object);
		if(object instanceof Reader) return useReader((Reader)object);
		if(object instanceof InputStream) return useStream((InputStream)object);
		if(object instanceof Source) return useSource((Source)object);
		if(object instanceof BaseSourceData<?>) return use((BaseSourceData<?>)object);
		return this.customThis();
	}
	
	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseSourceData<?> data) {
		if (data == null) return this.customThis();
		this.source = data.source;
		this.systemId = data.systemId;
		return this.customThis();
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link URI} und gibt {@code this} zurück.
	 *
	 * @see #useUrl(URL)
	 * @see URI#toURL()
	 * @param uri {@link URI}.
	 * @return {@code this}.
	 * @throws MalformedURLException Wenn {@link URI#toURL()} eine entsprechende Ausnahme auslöst. */
	public final GThis useUri(final URI uri) throws MalformedURLException {
		return this.useUrl(uri.toURL());
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen URI und gibt {@code this} zurück.
	 *
	 * @see #useUri(URI)
	 * @see URI#URI(String)
	 * @param uri URI.
	 * @return {@code this}.
	 * @throws URISyntaxException Wenn {@link URI#URI(String)} eine entsprechende Ausnahme auslöst.
	 * @throws MalformedURLException Wenn {@link #useUri(URI)} eine entsprechende Ausnahme auslöst. */
	public final GThis useUri(final String uri) throws URISyntaxException, MalformedURLException {
		return this.useUri(new URI(uri));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link URL} und gibt {@code this} zurück.
	 *
	 * @see #useSource(Source)
	 * @see URL#toExternalForm()
	 * @see StreamSource#StreamSource(String)
	 * @param url {@link URL}.
	 * @return {@code this}. */
	public final GThis useUrl(final URL url) {
		return this.useSource(new StreamSource(url.toExternalForm()));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen URL und gibt {@code this} zurück.
	 *
	 * @see #useUrl(URL)
	 * @see URL#URL(String)
	 * @param url URL.
	 * @return {@code this}.
	 * @throws MalformedURLException Wenn {@link URL#URL(String)} eine entsprechende Ausnahme auslöst. */
	public final GThis useUrl(final String url) throws MalformedURLException {
		return this.useUrl(new URL(url));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 *
	 * @see #useSource(Source)
	 * @see StreamSource#StreamSource(File)
	 * @param file {@link File}.
	 * @return {@code this}. */
	public final GThis useFile(final File file) {
		return this.useSource(new StreamSource(file));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit der gegebenen Datei und gibt {@code this} zurück.
	 *
	 * @see #useFile(File)
	 * @see File#File(String)
	 * @param file Datei.
	 * @return {@code this}. */
	public final GThis useFile(final String file) {
		return this.useFile(new File(file));
	}

	/** Diese Methode setzt die Quelldaten über {@link StringReader} mit dem gegebenen Text auf eine {@link StreamSource} und gibt {@code this} zurück.
	 *
	 * @see #useReader(Reader)
	 * @see StringReader#StringReader(String)
	 * @param text Text.
	 * @return {@code this}. */
	public final GThis useText(final String text) {
		return this.useReader(new StringReader(text));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link DOMSource} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
	 *
	 * @see #useSource(Source)
	 * @see DOMSource#DOMSource(Node)
	 * @param node {@link Node}.
	 * @return {@code this}. */
	public final GThis useNode(final Node node) {
		return this.useSource(new DOMSource(node));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link Reader} und gibt {@code this} zurück.
	 *
	 * @see #useSource(Source)
	 * @see StreamSource#StreamSource(Reader)
	 * @param reader {@link Reader}.
	 * @return {@code this}. */
	public final GThis useReader(final Reader reader) {
		return this.useSource(new StreamSource(reader));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt {@code this} zurück.
	 *
	 * @see #useSource(Source)
	 * @see StreamSource#StreamSource(InputStream)
	 * @param stream {@link InputStream}.
	 * @return {@code this}. */
	public final GThis useStream(final InputStream stream) {
		return this.useSource(new StreamSource(stream));
	}

	/** Diese Methode setzt den System-Identifikator und gibt {@code this} zurück.
	 *
	 * @see Source#setSystemId(String)
	 * @param systemID System-Identifikator oder {@code null}.
	 * @return {@code this}. */
	public final GThis useSystemID(final String systemID) {
		this.systemId = systemID;
		if (this.source == null) return this.customThis();
		this.source.setSystemId(systemID);
		return this.customThis();
	}

	/** Diese Methode setzt die Quelldaten und gibt {@code this} zurück. Der aktuelle System-Identifikator wird beibehalten, sofern er nicht {@code null} ist.
	 *
	 * @see #getSource()
	 * @see #useSystemID(String)
	 * @param source Quelldaten oder {@code null}.
	 * @return {@code this}. */
	public final GThis useSource(final Source source) {
		this.source = source;
		if (source == null) return this.customThis();
		return this.useSystemID(this.systemId != null ? this.systemId : source.getSystemId());
	}

	/** Diese Methode gibt die aktuell konfigurierten Quelldaten zurück.
	 *
	 * @see #useFile(File)
	 * @see #useNode(Node)
	 * @see #useReader(Reader)
	 * @see #useSource(Source)
	 * @see #useStream(InputStream)
	 * @see #useSystemID(String)
	 * @see DOMSource
	 * @see StreamSource
	 * @return Quelldaten oder {@code null}. */
	public final Source getSource() {
		return this.source;
	}

	/** Diese Methode gibt die aktuell konfigurierten Quelldaten als {@link InputSource} zurück. Der Rückgabewert ist {@code null}, wenn die Quelldaten keine
	 * {@link StreamSource} sind.
	 *
	 * @return Quelldaten oder {@code null}. */
	public final InputSource getInputSource() {
		final Source source = this.getSource();
		if (!(source instanceof StreamSource)) return null;
		final StreamSource stream = (StreamSource)source;
		final InputSource result = new InputSource();
		result.setSystemId(source.getSystemId());
		result.setCharacterStream(stream.getReader());
		result.setByteStream(stream.getInputStream());
		return result;
	}

	/** Diese Methode setzt die Quelldaten sowie den System-Identifikator auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useSource(Source)
	 * @see #useSystemID(String)
	 * @return {@code this}. */
	public final GThis resetSource() {
		this.useSystemID(null);
		return this.useSource(null);
	}

	/** {@inheritDoc}
	 *
	 * @see #getSource() */
	@Override
	public final Source get() throws IllegalStateException {
		return this.getSource();
	}

	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.source, this.systemId);
	}

}