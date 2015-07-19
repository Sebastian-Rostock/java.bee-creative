package bee.creative.xml;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator eines {@link Result}, das für die Ausgabedaten eines {@link Transformer} genutzt wird.
 * 
 * @see Transformer#transform(Source, Result)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseResultData<GThiz> extends BaseBuilder<Result, GThiz> {

	/**
	 * Dieses Feld speichert das {@link Result}.
	 */
	Result result;

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
	public GThiz use(final BaseResultData<?> data) {
		if (data == null) return this.thiz();
		this.result = data.result;
		this.systemID = data.systemID;
		return this.thiz();
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(File)
	 * @param file {@link File}.
	 * @return {@code this}.
	 */
	public GThiz useFile(final File file) {
		return this.useResult(new StreamResult(file));
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see DOMResult#DOMResult()
	 * @return {@code this}.
	 */
	public GThiz useNode() {
		return this.useResult(new DOMResult());
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see DOMResult#DOMResult(Node)
	 * @param node {@link Node}.
	 * @return {@code this}.
	 */
	public GThiz useNode(final Node node) {
		return this.useResult(new DOMResult(node));
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link Writer} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(Writer)
	 * @param writer {@link Writer}.
	 * @return {@code this}.
	 */
	public GThiz useWriter(final Writer writer) {
		return this.useResult(new StreamResult(writer));
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link OutputStream} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(OutputStream)
	 * @param stream {@link OutputStream}.
	 * @return {@code this}.
	 */
	public GThiz useStream(final OutputStream stream) {
		return this.useResult(new StreamResult(stream));
	}

	/**
	 * Diese Methode setzt den System-Identifikator und gibt {@code this} zurück.
	 * 
	 * @see Result#setSystemId(String)
	 * @param systemID System-Identifikator oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useSystemID(final String systemID) {
		this.systemID = systemID;
		if (this.result == null) return this.thiz();
		this.result.setSystemId(systemID);
		return this.thiz();
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten und gibt {@code this} zurück. Der aktuelle System-Identifikator wird beibehalten, sofern er nicht {@code null} ist.
	 * 
	 * @see #getResult()
	 * @see #useSystemID(String)
	 * @param result Ergebnisdaten oder {@code null}.
	 * @return {@code this}.
	 */
	public GThiz useResult(final Result result) {
		this.result = result;
		if (result == null) return this.thiz();
		return this.useSystemID(this.systemID != null ? this.systemID : result.getSystemId());
	}

	/**
	 * Diese Methode gibt die aktuell konfigurierten Ergebnisdaten zurück.
	 * 
	 * @see #useFile(File)
	 * @see #useNode()
	 * @see #useNode(Node)
	 * @see #useWriter(Writer)
	 * @see #useResult(Result)
	 * @see #useStream(OutputStream)
	 * @see #useSystemID(String)
	 * @see DOMResult
	 * @see StreamResult
	 * @return Quelldaten oder {@code null}.
	 */
	public Result getResult() {
		return this.result;
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten sowie den System-Identifikator auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see #useSystemID(String)
	 * @return {@code this}.
	 */
	public GThiz resetResult() {
		this.useSystemID(null);
		return this.useResult(null);
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