package bee.creative.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Konfigurator zum {@link #transform() Transformieren} sowie {@link #transformToString() Formatieren} eines {@link Document}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLFormatter {

	public static class SourceValue extends SourceBuilder.Value<SourceValue> {

		@Override
		public SourceValue owner() {
			return this;
		}

	}

	public class SourceProxy extends SourceBuilder.Proxy<XMLFormatter> {

		@Override
		protected SourceValue value() {
			return XMLFormatter.this.source();
		}

		@Override
		public XMLFormatter owner() {
			return XMLFormatter.this;
		}

	}

	public static class ResultValue extends ResultBuilder.Value<ResultValue> {

		@Override
		public ResultValue owner() {
			return this;
		}

	}

	public class ResultProxy extends ResultBuilder.Proxy<XMLFormatter> {

		@Override
		protected ResultValue value() {
			return XMLFormatter.this.result();
		}

		@Override
		public XMLFormatter owner() {
			return XMLFormatter.this;
		}

	}

	public static class TransformerValue extends TransformerBuilder.Value<TransformerValue> {

		@Override
		public TransformerValue owner() {
			return this;
		}

	}

	public class TransformerProxy extends TransformerBuilder.Proxy<XMLFormatter> {

		@Override
		protected TransformerValue value() {
			return XMLFormatter.this.transformer();
		}

		@Override
		public XMLFormatter owner() {
			return XMLFormatter.this;
		}

	}

	SourceValue source = new SourceValue();

	ResultValue result = new ResultValue();

	TransformerValue transformer = new TransformerValue();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param that Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLFormatter use(final XMLFormatter that) {
		if (that == null) return this;
		return this.forSource().use(that.source()).forResult().use(that.result()).forTransformer().use(that.transformer());
	}

	/** Diese Methode führt die Transformation aus und gibt {@code this} zurück.
	 *
	 * @see #openSourceData()
	 * @see #openResultData()
	 * @see Transformer#transform(Source, Result)
	 * @return {@code this}.
	 * @throws TransformerException Wenn {@link Transformer#transform(Source, Result)} eine entsprechende Ausnahme auslöst. */
	public XMLFormatter transform() throws TransformerException {
		final Transformer transformer = this.transformer().putValue();
			final Source source = this.source().getValue();
			final Result result = this.result().getValue();
			transformer.transform(source, result);
		return this;
	}

	/** Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück. Dazu wird als
	 * {@link #openResultData() Ausgabedaten} ein neues {@link DOMResult} eingesetzt.
	 *
	 * @see ResultBuilder#useNode()
	 * @see #openResultData()
	 * @return Dokumentknoten.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst. */
	public Node transformToNode() throws TransformerException {
		final DOMResult result = new DOMResult();
		this.forResult().useValue(result).transform().result().clear();
		return result.getNode();
	}

	/** Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().transformToNode()}.
	 *
	 * @see #transformToNode()
	 * @see SourceBuilder#use(Object) */
	public Node transformToNode(final Object source) throws TransformerException, MalformedURLException {
		return this.forSource().use(source).transformToNode();
	}

	/** Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in eine Zeichenkette und gibt diese zurück. Dazu wird als {@link #openResultData()
	 * Ausgabedaten} ein neuer {@link StringWriter} eingesetzt.
	 *
	 * @see StringWriter
	 * @see ResultBuilder#useWriter(Writer)
	 * @see #openResultData()
	 * @return Zeichenkette.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst. */
	public String transformToString() throws TransformerException {
		final StringWriter result = new StringWriter();
		this.forResult().useWriter(result).transform().result().clear();
		return result.toString();
	}

	/** Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().transformToString()}.
	 *
	 * @see #transformToString()
	 * @see SourceBuilder#use(Object) */
	public String transformToString(final Object source) throws TransformerException, MalformedURLException {
		return this.forSource().use(source).transformToString();
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator. */
	public SourceValue source() {
		return this.source;
	}

	public SourceProxy forSource() {
		return new SourceProxy();
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator. */
	public ResultValue result() {
		return this.result;
	}

	public ResultProxy forResult() {
		return new ResultProxy();
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Transformer} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public TransformerValue transformer() {
		return this.transformer;
	}

	public TransformerProxy forTransformer() {
		return new TransformerProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.source(), this.result(), this.transformer());
	}

}