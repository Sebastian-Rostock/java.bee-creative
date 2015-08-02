package bee.creative.xml;

import java.io.StringWriter;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen Konfigurator zum {@link #transform() Transformieren} sowie {@link #transformToString() Formatieren} eines {@link Document}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLFormatter {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Transformer}.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class SourceData extends BaseSourceData<SourceData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public XMLFormatter closeSourceData() {
			return XMLFormatter.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected SourceData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für die Ausgabedaten eines {@link Transformer}.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class ResultData extends BaseResultData<ResultData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public XMLFormatter closeResultData() {
			return XMLFormatter.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ResultData thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link Transformer}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class TransformerData extends BaseTransformerData<TransformerData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public XMLFormatter closeTemplatesData() {
			return XMLFormatter.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected TransformerData thiz() {
			return this;
		}

	}

	{}

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
	 */
	final SourceData sourceData = new SourceData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openResultData()}.
	 */
	final ResultData resultData = new ResultData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openTransformerData()}.
	 */
	final TransformerData transformerData = new TransformerData();

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public XMLFormatter use(final XMLFormatter data) {
		if (data == null) return this;
		this.sourceData.use(data.sourceData);
		this.resultData.use(data.resultData);
		this.transformerData.use(data.transformerData);
		return this;
	}

	/**
	 * Diese Methode führt die Transformation aus und gibt {@code this} zurück.
	 * 
	 * @see #openSourceData()
	 * @see #openResultData()
	 * @see Transformer#transform(Source, Result)
	 * @return {@code this}.
	 * @throws TransformerException Wenn {@link Transformer#transform(Source, Result)} eine entsprechende Ausnahme auslöst.
	 */
	public XMLFormatter transform() throws TransformerException {
		final Transformer transformer = this.transformerData.getTransformer();
		synchronized (transformer) {
			final Source source = this.sourceData.build();
			final Result result = this.resultData.build();
			transformer.transform(source, result);
		}
		return this;
	}

	/**
	 * Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück.<br>
	 * Dazu wird als {@link #openResultData() Ausgabedaten} ein temporäres {@link DOMResult} eingesetzt.
	 * 
	 * @see ResultData#useNode()
	 * @see #openResultData()
	 * @return Dokumentknoten.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst.
	 */
	public Node transformToNode() throws TransformerException {
		final DOMResult result = new DOMResult();
		this.openResultData().useResult(result).closeResultData().transform();
		return result.getNode();
	}

	/**
	 * Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in eine Zeichenkette und gibt diese zurück.<br>
	 * Dazu wird als {@link #openResultData() Ausgabedaten} ein temporärer {@link StringWriter} eingesetzt.
	 * 
	 * @see StringWriter
	 * @see ResultData#useWriter(Writer)
	 * @see #openResultData()
	 * @return Zeichenkette.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst.
	 */
	public String transformToString() throws TransformerException {
		final StringWriter result = new StringWriter();
		this.openResultData().useWriter(result).closeResultData().transform().openResultData().resetResult();
		return result.toString();
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator.
	 */
	public SourceData openSourceData() {
		return this.sourceData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator.
	 */
	public ResultData openResultData() {
		return this.resultData;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link Transformer} und gibt ihn zurück.
	 * 
	 * @return Konfigurator.
	 */
	public TransformerData openTransformerData() {
		return this.transformerData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.resultData, this.transformerData);
	}

}