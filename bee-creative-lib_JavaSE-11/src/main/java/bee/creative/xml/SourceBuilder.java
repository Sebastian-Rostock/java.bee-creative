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
import bee.creative.util.Builders.BaseValueBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator einer {@link Source} oder {@link InputSource}, die für die Eingabedaten eines {@link Schema},
 * {@link Validator}, {@link DocumentBuilder} bzw. {@link Transformer} genutzt wird.
 *
 * @see DocumentBuilder#parse(InputSource)
 * @see SchemaFactory#newSchema(Source)
 * @see Transformer#transform(Source, Result)
 * @see TransformerFactory#newTemplates(Source)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GOwner> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class SourceBuilder<GOwner> extends BaseValueBuilder<Source, GOwner> {

	public static abstract class Value<GOwner> extends SourceBuilder<GOwner> {

		Source value;

		@Override
		public Source get() {
			return this.value;
		}

		@Override
		public void set(final Source value) {
			this.value = value;
		}

	}

	public static abstract class Proxy<GOwner> extends SourceBuilder<GOwner> {

		protected abstract Value<?> value();

		@Override
		public Source get() {
			return this.value().get();
		}

		@Override
		public void set(final Source value) {
			this.value().set(value);
		}

	}

	/** Diese Methode delegiert das gegebene Objekt abhängig von seinem Datentyp an eine der spezifischen Methoden und gibt {@code this} zurück. Unbekannte
	 * Datentypen werden ignoriert.
	 *
	 * @param object Quelldaten als {@link URL}, {@link URI}, {@link File}, {@link String}, {@link Node}, {@link Reader}, {@link InputStream}, {@link Source} oder
	 *        {@link SourceBuilder}.
	 * @return {@code this}. */
	public GOwner use(final Object object) throws MalformedURLException {
		if (object instanceof URI) return this.useUri((URI)object);
		if (object instanceof URL) return this.useUrl((URL)object);
		if (object instanceof File) return this.useFile((File)object);
		if (object instanceof String) return this.useText((String)object);
		if (object instanceof Node) return this.useNode((Node)object);
		if (object instanceof Reader) return this.useReader((Reader)object);
		if (object instanceof InputStream) return this.useStream((InputStream)object);
		if (object instanceof Source) return this.useValue((Source)object);
		if (object instanceof SourceBuilder<?>) return this.use((SourceBuilder<?>)object);
		return this.owner();
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link URI} und gibt {@code this} zurück.
	 *
	 * @see #useUrl(URL)
	 * @see URI#toURL()
	 * @param uri {@link URI}.
	 * @return {@code this}.
	 * @throws MalformedURLException Wenn {@link URI#toURL()} eine entsprechende Ausnahme auslöst. */
	public GOwner useUri(final URI uri) throws MalformedURLException {
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
	public GOwner useUri(final String uri) throws URISyntaxException, MalformedURLException {
		return this.useUri(new URI(uri));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link URL} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see URL#toExternalForm()
	 * @see StreamSource#StreamSource(String)
	 * @param url {@link URL}.
	 * @return {@code this}. */
	public GOwner useUrl(final URL url) {
		return this.useValue(new StreamSource(url.toExternalForm()));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen URL und gibt {@code this} zurück.
	 *
	 * @see #useUrl(URL)
	 * @see URL#URL(String)
	 * @param url URL.
	 * @return {@code this}.
	 * @throws MalformedURLException Wenn {@link URL#URL(String)} eine entsprechende Ausnahme auslöst. */
	public GOwner useUrl(final String url) throws MalformedURLException {
		return this.useUrl(new URL(url));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see StreamSource#StreamSource(File)
	 * @param file {@link File}.
	 * @return {@code this}. */
	public GOwner useFile(final File file) {
		return this.useValue(new StreamSource(file));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit der gegebenen Datei und gibt {@code this} zurück.
	 *
	 * @see #useFile(File)
	 * @see File#File(String)
	 * @param file Datei.
	 * @return {@code this}. */
	public GOwner useFile(final String file) {
		return this.useFile(new File(file));
	}

	/** Diese Methode setzt die Quelldaten über {@link StringReader} mit dem gegebenen Text auf eine {@link StreamSource} und gibt {@code this} zurück.
	 *
	 * @see #useReader(Reader)
	 * @see StringReader#StringReader(String)
	 * @param text Text.
	 * @return {@code this}. */
	public GOwner useText(final String text) {
		return this.useReader(new StringReader(text));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link DOMSource} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see DOMSource#DOMSource(Node)
	 * @param node {@link Node}.
	 * @return {@code this}. */
	public GOwner useNode(final Node node) {
		return this.useValue(new DOMSource(node));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link Reader} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see StreamSource#StreamSource(Reader)
	 * @param reader {@link Reader}.
	 * @return {@code this}. */
	public GOwner useReader(final Reader reader) {
		return this.useValue(new StreamSource(reader));
	}

	/** Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt {@code this} zurück.
	 *
	 * @see #useValue(Object)
	 * @see StreamSource#StreamSource(InputStream)
	 * @param stream {@link InputStream}.
	 * @return {@code this}. */
	public GOwner useStream(final InputStream stream) {
		return this.useValue(new StreamSource(stream));
	}

	/** Diese Methode gibt die aktuell konfigurierten Quelldaten als {@link InputSource} zurück. Der Rückgabewert ist {@code null}, wenn die Quelldaten keine
	 * {@link StreamSource} sind.
	 *
	 * @return Quelldaten oder {@code null}. */
	public InputSource getInputSource() {
		final Source source = this.getValue();
		if (!(source instanceof StreamSource)) return null;
		final StreamSource stream = (StreamSource)source;
		final InputSource result = new InputSource();
		result.setSystemId(source.getSystemId());
		result.setCharacterStream(stream.getReader());
		result.setByteStream(stream.getInputStream());
		return result;
	}

}