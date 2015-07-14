package bee.creative.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Node;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator einer {@link Source}, die für die Eringabedaten eines {@link Schema} bzw. {@link Transformer}
 * genutzt wird.
 * 
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
	 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link URL} und gibt {@code this} zurück.
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
	 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt {@code this}
	 * zurück.
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

	/**
	 * Diese Methode setzt die Quelldaten auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @se {@link #useSource(Source)}
	 * @return {@code this}.
	 */
	public GThiz resetSource() {
		return this.useSource(null);
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