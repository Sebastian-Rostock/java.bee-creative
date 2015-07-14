package bee.creative.xml;

import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import bee.creative.util.Objects;

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
	final SourceData sourceData = //
		new SourceData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openResultData()}.
	 */
	final ResultData resultData = //
		new ResultData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openTransformerData()}.
	 */
	final TransformerData transformerData = //
		new TransformerData();

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

	public XMLFormatter transform() throws TransformerException {
		final Transformer transformer = this.transformerData.build();
		synchronized (transformer) {
			final Source source = this.sourceData.build();
			final Result result = this.resultData.build();
			transformer.transform(source, result);
		}
		return this;
	}

	/**
	 * Diese Methode gibt das zurück.
	 * 
	 * @return
	 * @throws TransformerException
	 */
	public String transformToText() throws TransformerException {
		final StringWriter result = new StringWriter();
		this.openResultData().useWriter(result).closeResultData().transform();
		return result.toString();
	}

	public Node transformToNode() throws TransformerException {
		final DOMResult result = new DOMResult();
		this.openResultData().useResult(result).closeResultData().transform();
		return result.getNode();
	}

	public SourceData openSourceData() {
		return this.sourceData;
	}

	public ResultData openResultData() {
		return this.resultData;
	}

	public TransformerData openTransformerData() {
		return this.transformerData;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return Objects.toStringCall(this, this.sourceData, this.resultData, this.transformerData);
	}

}