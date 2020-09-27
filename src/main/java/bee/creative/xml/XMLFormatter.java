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
public  class XMLFormatter {

	/** Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Transformer}.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public  class SourceData extends BaseSourceData<SourceData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public  XMLFormatter closeSourceData() {
			return XMLFormatter.this;
		}

		@Override
		protected  SourceData customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Ausgabedaten eines {@link Transformer}.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public  class ResultData extends BaseResultData<ResultData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public  XMLFormatter closeResultData() {
			return XMLFormatter.this;
		}

		@Override
		protected  ResultData customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Transformer}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public  class TransformerData extends BaseTransformerData<TransformerData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public  XMLFormatter closeTransformerData() {
			return XMLFormatter.this;
		}

		@Override
		protected  TransformerData customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert den Konfigurator {@link #openSourceData()}. */
	final	 SourceData sourceData = new SourceData();

	/** Dieses Feld speichert den Konfigurator {@link #openResultData()}. */
	final ResultData resultData = new ResultData();

	/** Dieses Feld speichert den Konfigurator {@link #openTransformerData()}. */
	final TransformerData transformerData = new TransformerData();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public  XMLFormatter use( XMLFormatter data) {
		if (data == null) return this;
		this.sourceData.use(data.sourceData);
		this.resultData.use(data.resultData);
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
	public  XMLFormatter transform() throws TransformerException {
		 Transformer transformer = this.transformerData.getTransformer();
		synchronized (transformer) {
			 Source source = this.sourceData.getSource();
			 Result result = this.resultData.getResult();
			transformer.transform(source, result);
		}
		return this;
	}

	/** Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück. Dazu wird als
	 * {@link #openResultData() Ausgabedaten} ein neues {@link DOMResult} eingesetzt.
	 *
	 * @see ResultData#useNode()
	 * @see #openResultData()
	 * @return Dokumentknoten.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst. */
	public  Node transformToNode() throws TransformerException {
		 DOMResult result = new DOMResult();
		this.openResultData().useResult(result).closeResultData().transform().openResultData().resetResult();
		return result.getNode();
	}
	
	/**
	 * Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().transformToNode()}.
	 * @see #transformToNode()
	 * @see BaseSourceData#use(Object)
	 */
	public  Node transformToNode(Object source) throws TransformerException, MalformedURLException {
		return openSourceData().use(source).closeSourceData().transformToNode();
	}

	/** Diese Methode transformiert die {@link #openSourceData() Eingabedaten} in eine Zeichenkette und gibt diese zurück. Dazu wird als {@link #openResultData()
	 * Ausgabedaten} ein neuer {@link StringWriter} eingesetzt.
	 *
	 * @see StringWriter
	 * @see ResultData#useWriter(Writer)
	 * @see #openResultData()
	 * @return Zeichenkette.
	 * @throws TransformerException Wenn {@link #transform()} eine entsprechende Ausnahme auslöst. */
	public  String transformToString() throws TransformerException {
		 StringWriter result = new StringWriter();
		this.openResultData().useWriter(result).closeResultData().transform().openResultData().resetResult();
		return result.toString();
	}

	/**
	 * Diese Methode ist eine Abkürzung für {@code this.openSourceData().use(source).closeSourceData().transformToString()}.
	 * @see #transformToString()
	 * @see BaseSourceData#use(Object)
	 */
	public  String transformToString(Object source) throws TransformerException, MalformedURLException {
		return openSourceData().use(source).closeSourceData().transformToString();
	}

	
	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator. */
	public  SourceData openSourceData() {
		return this.sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @return Konfigurator. */
	public  ResultData openResultData() {
		return this.resultData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Transformer} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public  TransformerData openTransformerData() {
		return this.transformerData;
	}

	@Override
	public  String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.resultData, this.transformerData);
	}

}