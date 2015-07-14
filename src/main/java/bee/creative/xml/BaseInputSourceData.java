package bee.creative.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator einer {@link InputSource}, die für die Quelldaten ({@code *.xml}) eines {@link Document} genutzt
 * wird.
 * 
 * @see DocumentBuilder#parse(InputSource)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseInputSourceData<GThiz> extends BaseBuilder<InputSource, GThiz> {

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
	 * Diese Methode setzt die Quelldaten via {@link #useSource(Source)} auf eine {@link StreamSource} mit dem gegebenen {@link InputStream} und gibt {@code this}
	 * zurück.
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

	public GThiz resetSource() {
		return this.useSource(null);
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