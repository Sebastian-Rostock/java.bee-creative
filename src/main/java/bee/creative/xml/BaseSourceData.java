package bee.creative.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
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
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator einer {@link Source} oder {@link InputSource}, die für die Eingabedaten eines {@link Schema},
 * {@link Validator}, {@link DocumentBuilder} bzw. {@link Transformer} genutzt wird.
 * 
 * @see DocumentBuilder#parse(InputSource)
 * @see SchemaFactory#newSchema(Source)
 * @see Transformer#transform(Source, Result)
 * @see TransformerFactory#newTemplates(Source)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseSourceData<GThiz> extends BaseBuilder<Source, GThiz> {

	/**
	 * Dieses Feld speichert die Quelldaten.
	 */
	Source __source;

	/**
	 * Dieses Feld speichert den System-Identifikator.
	 */
	String __systemID;

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz use(final BaseSourceData<?> data) {
		if (data == null) return this.__this();
		this.__source = data.__source;
		this.__systemID = data.__systemID;
		return this.__this();
	}

	/**
	 * Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link URL} und gibt {@code this} zurück.
	 * 
	 * @see #useSource(Source)
	 * @see URL#toExternalForm()
	 * @see StreamSource#StreamSource(String)
	 * @param url {@link URL}.
	 * @return {@code this}.
	 */
	public final GThiz useUrl(final URL url) {
		return this.useSource(new StreamSource(url.toExternalForm()));
	}

	/**
	 * Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 * 
	 * @see #useSource(Source)
	 * @see StreamSource#StreamSource(File)
	 * @param file {@link File}.
	 * @return {@code this}.
	 */
	public final GThiz useFile(final File file) {
		return this.useSource(new StreamSource(file));
	}

	/**
	 * Diese Methode setzt die Quelldaten über {@link StringReader} mit dem gegebenen Text auf eine {@link StreamSource} und gibt {@code this} zurück.
	 * 
	 * @see #useReader(Reader)
	 * @see StringReader#StringReader(String)
	 * @param text Text.
	 * @return {@code this}.
	 */
	public final GThiz useText(final String text) {
		return this.useReader(new StringReader(text));
	}

	/**
	 * Diese Methode setzt die Quelldaten auf eine {@link DOMSource} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
	 * 
	 * @see #useSource(Source)
	 * @see DOMSource#DOMSource(Node)
	 * @param node {@link Node}.
	 * @return {@code this}.
	 */
	public final GThiz useNode(final Node node) {
		return this.useSource(new DOMSource(node));
	}

	/**
	 * Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link Reader} und gibt {@code this} zurück.
	 * 
	 * @see #useSource(Source)
	 * @see StreamSource#StreamSource(Reader)
	 * @param reader {@link Reader}.
	 * @return {@code this}.
	 */
	public final GThiz useReader(final Reader reader) {
		return this.useSource(new StreamSource(reader));
	}

	/**
	 * Diese Methode setzt die Quelldaten auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt {@code this} zurück.
	 * 
	 * @see #useSource(Source)
	 * @see StreamSource#StreamSource(InputStream)
	 * @param stream {@link InputStream}.
	 * @return {@code this}.
	 */
	public final GThiz useStream(final InputStream stream) {
		return this.useSource(new StreamSource(stream));
	}

	/**
	 * Diese Methode setzt den System-Identifikator und gibt {@code this} zurück.
	 * 
	 * @see Source#setSystemId(String)
	 * @param systemID System-Identifikator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useSystemID(final String systemID) {
		this.__systemID = systemID;
		if (this.__source == null) return this.__this();
		this.__source.setSystemId(systemID);
		return this.__this();
	}

	/**
	 * Diese Methode setzt die Quelldaten und gibt {@code this} zurück. Der aktuelle System-Identifikator wird beibehalten, sofern er nicht {@code null} ist.
	 * 
	 * @see #getSource()
	 * @see #useSystemID(String)
	 * @param source Quelldaten oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useSource(final Source source) {
		this.__source = source;
		if (source == null) return this.__this();
		return this.useSystemID(this.__systemID != null ? this.__systemID : source.getSystemId());
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
	 * @see DOMSource
	 * @see StreamSource
	 * @return Quelldaten oder {@code null}.
	 */
	public final Source getSource() {
		return this.__source;
	}

	/**
	 * Diese Methode gibt die aktuell konfigurierten Quelldaten als {@link InputSource} zurück.<br>
	 * Der Rückgabewert ist {@code null}, wenn die Quelldaten keine {@link StreamSource} sind.
	 * 
	 * @return Quelldaten oder {@code null}.
	 */
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

	/**
	 * Diese Methode setzt die Quelldaten sowie den System-Identifikator auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useSource(Source)
	 * @see #useSystemID(String)
	 * @return {@code this}.
	 */
	public final GThiz resetSource() {
		this.useSystemID(null);
		return this.useSource(null);
	}

	{}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #getSource()
	 */
	@Override
	public final Source build() throws IllegalStateException {
		return this.getSource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__source, this.__systemID);
	}

}