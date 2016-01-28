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
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse.
 */
public abstract class BaseResultData<GThis> extends BaseBuilder<Result, GThis> {

	/**
	 * Dieses Feld speichert das {@link Result}.
	 */
	Result _result_;

	/**
	 * Dieses Feld speichert den System-Identifikator.
	 */
	String _systemId_;

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThis use(final BaseResultData<?> data) {
		if (data == null) return this._this_();
		this._result_ = data._result_;
		this._systemId_ = data._systemId_;
		return this._this_();
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(File)
	 * @param file {@link File}.
	 * @return {@code this}.
	 */
	public final GThis useFile(final File file) {
		return this.useResult(new StreamResult(file));
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see DOMResult#DOMResult()
	 * @return {@code this}.
	 */
	public final GThis useNode() {
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
	public final GThis useNode(final Node node) {
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
	public final GThis useWriter(final Writer writer) {
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
	public final GThis useStream(final OutputStream stream) {
		return this.useResult(new StreamResult(stream));
	}

	/**
	 * Diese Methode setzt den System-Identifikator und gibt {@code this} zurück.
	 * 
	 * @see Result#setSystemId(String)
	 * @param systemId System-Identifikator oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThis useSystemId(final String systemId) {
		this._systemId_ = systemId;
		if (this._result_ == null) return this._this_();
		this._result_.setSystemId(systemId);
		return this._this_();
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten und gibt {@code this} zurück. Der aktuelle System-Identifikator wird beibehalten, sofern er nicht {@code null} ist.
	 * 
	 * @see #getResult()
	 * @see #useSystemId(String)
	 * @param result Ergebnisdaten oder {@code null}.
	 * @return {@code this}.
	 */
	public final GThis useResult(final Result result) {
		this._result_ = result;
		if (result == null) return this._this_();
		return this.useSystemId(this._systemId_ != null ? this._systemId_ : result.getSystemId());
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
	 * @see #useSystemId(String)
	 * @see DOMResult
	 * @see StreamResult
	 * @return Quelldaten oder {@code null}.
	 */
	public final Result getResult() {
		return this._result_;
	}

	/**
	 * Diese Methode setzt die Ergebnisdaten sowie den System-Identifikator auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useResult(Result)
	 * @see #useSystemId(String)
	 * @return {@code this}.
	 */
	public final GThis resetResult() {
		this.useSystemId(null);
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
		return Objects.toInvokeString(this, this._result_, this._systemId_);
	}

}