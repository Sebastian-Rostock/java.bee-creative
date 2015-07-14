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

	public GThiz useFile(final File file) {
		return this.useResult(new StreamResult(file));
	}

	public GThiz useNode(final Node node) {
		return this.useResult(new DOMResult(node));
	}

	public GThiz useWriter(final Writer writer) {
		return this.useResult(new StreamResult(writer));
	}

	public GThiz useStream(final OutputStream stream) {
		return this.useResult(new StreamResult(stream));
	}

	public GThiz useSystemID(final String systemID) {
		this.systemID = systemID;
		if (this.result == null) return this.thiz();
		this.result.setSystemId(systemID);
		return this.thiz();
	}

	public GThiz useResult(final Result result) {
		this.result = result;
		if (result == null) return this.thiz();
		return this.useSystemID(this.systemID != null ? this.systemID : result.getSystemId());
	}

	public Result getResult() {
		return this.result;
	}

	public GThiz resetResult() {
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