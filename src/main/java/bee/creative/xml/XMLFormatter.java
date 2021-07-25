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

	Source source;

	Result result;

	Transformer transformer;

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLFormatter use(final XMLFormatter data) {
		if (data == null) return this;
		this.forSource().useValue(data.source).forResult().useValue(data.result);
		this.transformerData.use(data.transformerData);
		return this;
	}

	/** Diese Methode führt die Transformation aus und gibt {@code this} zurück.
	 *
	 * @see #openSourceData()
	 * @see #openResultData()
	 * @see Transformer#transform(Source, Result)
	 * @return {@code this}.
	 * @throws TransformerException Wenn {@link Transformer#transform(Source, Result)} eine entsprechende Ausnahme auslöst. */
	public XMLFormatter transform() throws TransformerException {
		final Transformer transformer = this.transformerData.getTransformer();
		synchronized (transformer) {
			final Source source = this.sourceData.getSource();
			final Result result = this.resultData.getResult();
			transformer.transform(source, result);
		}
		return this;
	}

	/** Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück. Dazu wird als
	 * {@link #openResultData() Ausgabedaten} ein neues {@link DOMResult} eingesetzt.
	 *
	 * @see BaseResultData#useNode()
	 * @see #openResultData()
	 * @return Dokumentknoten.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst. */
	public Node transformToNode() throws TransformerException {
		final DOMResult result = new DOMResult();
		this.openResultData().useResult(result).closeResultData().transform().openResultData().resetResult();
		return result.getNode();
	}

	/** Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().transformToNode()}.
	 *
	 * @see #transformToNode()
	 * @see BaseSourceData#use(Object) */
	public Node transformToNode(final Object source) throws TransformerException, MalformedURLException {
		return this.openSourceData().use(source).closeSourceData().transformToNode();
	}

	/** Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in eine Zeichenkette und gibt diese zurück. Dazu wird als {@link #openResultData()
	 * Ausgabedaten} ein neuer {@link StringWriter} eingesetzt.
	 *
	 * @see StringWriter
	 * @see BaseResultData#useWriter(Writer)
	 * @see #openResultData()
	 * @return Zeichenkette.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst. */
	public String transformToString() throws TransformerException {
		final StringWriter result = new StringWriter();
		this.openResultData().useWriter(result).closeResultData().transform().openResultData().resetResult();
		return result.toString();
	}

	/** Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().transformToString()}.
	 *
	 * @see #transformToString()
	 * @see BaseSourceData#use(Object) */
	public String transformToString(final Object source) throws TransformerException, MalformedURLException {
		return this.forSource().use(source).transformToString();
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator. */
	public BaseSourceData<XMLFormatter> forSource() {
		return new BaseSourceData<XMLFormatter>() {

			@Override
			public Source get() {
				return XMLFormatter.this.source;
			}

			@Override
			public void set(final Source value) {
				XMLFormatter.this.source = value;
			}

			@Override
			public XMLFormatter owner() {
				return null;
			}

		};
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator. */
	public BaseResultData<XMLFormatter> forResult() {
		return new BaseResultData<XMLFormatter>() {

			@Override
			public void set(final Result value) {
				XMLFormatter.this.result = value;
			}

			@Override
			public Result get() {
				return XMLFormatter.this.result;
			}

			@Override
			public XMLFormatter owner() {
				return XMLFormatter.this;
			}

		};
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Transformer} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public BaseTransformerData<XMLFormatter> openTransformerData() {
		return new BaseTransformerData<XMLFormatter>() {

		};
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.source, this.result, this.transformerData);
	}

}