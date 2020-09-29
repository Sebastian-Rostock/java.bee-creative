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
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseBuilder;

/** Diese Klasse implementiert einen abstrakten Konfigurator eines {@link Result}, das für die Ausgabedaten eines {@link Transformer} oder {@link Validator}
 * genutzt wird.
 *
 * @see Validator#validate(Source, Result)
 * @see Transformer#transform(Source, Result)
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseResultData<GThis> extends BaseBuilder<Result, GThis> {

	/** Dieses Feld speichert das {@link Result}. */
	Result result;

	/** Dieses Feld speichert den System-Identifikator. */
	String systemId;

	/** Diese Methode delegiert das gegebene Objekt abhängig von seinem Datentyp an eine der spezifischen Methoden und gibt {@code this} zurück. Unbekannte
	 * Datentypen werden ignoriert.
	 * 
	 * @param object Quelldaten als { {@link File}, {@link Node}, {@link Writer}, {@link OutputStream}, {@link Result} oder {@link BaseResultData}.
	 * @return {@code this}. */
	public final GThis use(final Object object) {
		if (object instanceof File) return useFile((File)object);
		if (object instanceof Node) return useNode((Node)object);
		if (object instanceof Writer) return useWriter((Writer)object);
		if (object instanceof OutputStream) return useStream((OutputStream)object);
		if (object instanceof Result) return useResult((Result)object);
		if (object instanceof BaseResultData<?>) return use((BaseResultData<?>)object);
		return this.customThis();
	}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseResultData<?> data) {
		if (data == null) return this.customThis();
		this.result = data.result;
		this.systemId = data.systemId;
		return this.customThis();
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link File} und gibt {@code this} zurück.
	 *
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(File)
	 * @param file {@link File}.
	 * @return {@code this}. */
	public final GThis useFile(final File file) {
		return this.useResult(new StreamResult(file));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit der gegebenen Datei und gibt {@code this} zurück.
	 *
	 * @see #useFile(File)
	 * @see File#File(String)
	 * @param file Datei.
	 * @return {@code this}. */
	public final GThis useFile(final String file) {
		return this.useFile(new File(file));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} und gibt {@code this} zurück.
	 *
	 * @see #useResult(Result)
	 * @see DOMResult#DOMResult()
	 * @return {@code this}. */
	public final GThis useNode() {
		return this.useResult(new DOMResult());
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link DOMResult} mit dem gegebenen {@link Node} und gibt {@code this} zurück.
	 *
	 * @see #useResult(Result)
	 * @see DOMResult#DOMResult(Node)
	 * @param node {@link Node}.
	 * @return {@code this}. */
	public final GThis useNode(final Node node) {
		return this.useResult(new DOMResult(node));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link Writer} und gibt {@code this} zurück.
	 *
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(Writer)
	 * @param writer {@link Writer}.
	 * @return {@code this}. */
	public final GThis useWriter(final Writer writer) {
		return this.useResult(new StreamResult(writer));
	}

	/** Diese Methode setzt die Ergebnisdaten auf ein {@link StreamResult} mit dem gegebenen {@link OutputStream} und gibt {@code this} zurück.
	 *
	 * @see #useResult(Result)
	 * @see StreamResult#StreamResult(OutputStream)
	 * @param stream {@link OutputStream}.
	 * @return {@code this}. */
	public final GThis useStream(final OutputStream stream) {
		return this.useResult(new StreamResult(stream));
	}

	/** Diese Methode setzt den System-Identifikator und gibt {@code this} zurück.
	 *
	 * @see Result#setSystemId(String)
	 * @param systemId System-Identifikator oder {@code null}.
	 * @return {@code this}. */
	public final GThis useSystemId(final String systemId) {
		this.systemId = systemId;
		if (this.result == null) return this.customThis();
		this.result.setSystemId(systemId);
		return this.customThis();
	}

	/** Diese Methode setzt die Ergebnisdaten und gibt {@code this} zurück. Der aktuelle System-Identifikator wird beibehalten, sofern er nicht {@code null} ist.
	 *
	 * @see #getResult()
	 * @see #useSystemId(String)
	 * @param result Ergebnisdaten oder {@code null}.
	 * @return {@code this}. */
	public final GThis useResult(final Result result) {
		this.result = result;
		if (result == null) return this.customThis();
		return this.useSystemId(this.systemId != null ? this.systemId : result.getSystemId());
	}

	/** Diese Methode gibt die aktuell konfigurierten Ergebnisdaten zurück.
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
	 * @return Quelldaten oder {@code null}. */
	public final Result getResult() {
		return this.result;
	}

	/** Diese Methode setzt die Ergebnisdaten sowie den System-Identifikator auf {@code null} und gibt {@code this} zurück.
	 *
	 * @see #useResult(Result)
	 * @see #useSystemId(String)
	 * @return {@code this}. */
	public final GThis resetResult() {
		this.useSystemId(null);
		return this.useResult(null);
	}

	@Override
	public final Result get() throws IllegalStateException {
		return this.getResult();
	}

	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.result, this.systemId);
	}

}