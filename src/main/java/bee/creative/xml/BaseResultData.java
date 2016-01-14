package bee.creative.xml;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Validator;
import org.w3c.dom.Node;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen abstrakten Konfigurator eines {@link Result}, das für die Ausgabedaten eines {@link Transformer} oder {@link Validator}
 * genutzt wird.
 * 
 * @see Validator#validate(Source, Result)
 * @see Transformer#transform(Source, Result)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseResultData<GThiz> extends BaseBuilder<Result, GThiz> {

	/**
	 * Dieses Feld speichert das {@link Result}.
	 */
	Result __result;

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
	public final GThiz use(final BaseResultData<?> data) {
		if (data == null) return this.__this();
		this.__result = data.__result;
		this.__systemID = data.__systemID;
		return this.__this();
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(File)
	 * @param file {@link File}.
	 * @return {@code this}.
	 */
	public final GThiz useFile(final File file) {
		return this.useResult(new StreamResult(file));
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see DOMResult#DOMResult()
	 * @return {@code this}.
	 */
	public final GThiz useNode() {
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
	public final GThiz useNode(final Node node) {
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
	public final GThiz useWriter(final Writer writer) {
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
	public final GThiz useStream(final OutputStream stream) {
		return this.useResult(new StreamResult(stream));
	}

	/**
	 * Diese Methode setzt den System-Identifikator und gibt {@code this} zurück.
	 * 
	 * @see Result#setSystemId(String)
	 * @param systemID System-Identifikator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useSystemID(final String systemID) {
		this.__systemID = systemID;
		if (this.__result == null) return this.__this();
		this.__result.setSystemId(systemID);
		return this.__this();
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten und gibt {@code this} zurück. Der aktuelle System-Identifikator wird beibehalten, sofern er nicht {@code null} ist.
	 * 
	 * @see #getResult()
	 * @see #useSystemID(String)
	 * @param result Ergebnisdaten oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThiz useResult(final Result result) {
		this.__result = result;
		if (result == null) return this.__this();
		return this.useSystemID(this.__systemID != null ? this.__systemID : result.getSystemId());
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
	public final Result getResult() {
		return this.__result;
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten sowie den System-Identifikator auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see #useSystemID(String)
	 * @return {@code this}.
	 */
	public final GThiz resetResult() {
		this.useSystemID(null);
		return this.useResult(null);
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Result build() throws IllegalStateException {
		return this.getResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.__result, this.__systemID);
	}

}