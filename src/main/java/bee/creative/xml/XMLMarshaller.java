package bee.creative.xml;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.util.Objects;

/** Diese Klasse implementiert einen Konfigurator zum {@link #marshal() Ausgeben/Formatieren} eines Objekts mit Hilfe eines {@link Marshaller}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class XMLMarshaller {

	/** Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Marshaller}.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public final class SourceData extends BaseValueBuilder<Object, SourceData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public final XMLMarshaller closeSourceData() {
			return XMLMarshaller.this;
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SourceData _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Ausgabedaten eines {@link Marshaller}.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public final class ResultData extends BaseResultData<ResultData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public final XMLMarshaller closeResultData() {
			return XMLMarshaller.this;
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final ResultData _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Marshaller}.
	 * 
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public final class MarshallerData extends BaseMarshallerData<MarshallerData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public final XMLMarshaller closeMarshallerData() {
			return XMLMarshaller.this;
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final MarshallerData _this_() {
			return this;
		}

	}

	{}

	/** Diese Methode gibt einen neuen {@link XMLMarshaller} zurück, dessen {@link JAXBContext} die gegebenen Klassen nutzt.
	 * 
	 * @param classes Klasse.
	 * @return {@link XMLMarshaller}. */
	public static XMLMarshaller from(final Class<?>... classes) {
		return new XMLMarshaller() //
			.openMarshallerData().openContextData().openClassData() //
			.useItems(Arrays.asList(classes)) //
			.closeClassesData().closeContextData().closeMarshallerData();
	}

	{}

	/** Dieses Feld speichert den Konfigurator {@link #openSourceData()}. */
	final SourceData _sourceData_ = new SourceData();

	/** Dieses Feld speichert den Konfigurator {@link #openResultData()}. */
	final ResultData _resultData_ = new ResultData();

	/** Dieses Feld speichert den Konfigurator {@link #openMarshallerData()}. */
	final MarshallerData _marshallerData_ = new MarshallerData();

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final XMLMarshaller use(final XMLMarshaller data) {
		if (data == null) return this;
		this._sourceData_.use(data._sourceData_);
		this._resultData_.use(data._resultData_);
		this._marshallerData_.use(data._marshallerData_);
		return this;
	}

	/** Diese Methode führt die Formatierung aus und gibt {@code this} zurück.
	 * 
	 * @see #openSourceData()
	 * @see #openResultData()
	 * @see Marshaller#marshal(Object, Result)
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link MarshallerData#getMarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link Marshaller#marshal(Object, Result)} eine entsprechende Ausnahme auslöst. */
	public final XMLMarshaller marshal() throws SAXException, JAXBException {
		final Marshaller marshaller = this._marshallerData_.getMarshaller();
		synchronized (marshaller) {
			final Object source = this._sourceData_.get();
			final Result result = this._resultData_.getResult();
			marshaller.marshal(source, result);
		}
		return this;
	}

	/** Diese Methode formatiert die {@link #openSourceData() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück.<br>
	 * Dazu wird als {@link #openResultData() Ausgabedaten} ein neues {@link DOMResult} eingesetzt.
	 * 
	 * @see ResultData#useNode()
	 * @see #openResultData()
	 * @return Dokumentknoten.
	 * @throws SAXException Wenn {@link #marshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #marshal()} eine entsprechende Ausnahme auslöst. */
	public final Node marshalToNode() throws SAXException, JAXBException {
		final DOMResult result = new DOMResult();
		this.openResultData().useResult(result).closeResultData().marshal().openResultData().resetResult();
		return result.getNode();
	}

	/** Diese Methode formatiert die {@link #openSourceData() Eingabedaten} in eine Zeichenkette und gibt diese zurück.<br>
	 * Dazu wird als {@link #openResultData() Ausgabedaten} ein neuer {@link StringWriter} eingesetzt.
	 * 
	 * @see StringWriter
	 * @see ResultData#useWriter(Writer)
	 * @see #openResultData()
	 * @return Zeichenkette.
	 * @throws SAXException Wenn {@link #marshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #marshal()} eine entsprechende Ausnahme auslöst. */
	public final String marshalToString() throws SAXException, JAXBException {
		final StringWriter result = new StringWriter();
		this.openResultData().useWriter(result).closeResultData().marshal().openResultData().resetResult();
		return result.toString();
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 * 
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public final SourceData openSourceData() {
		return this._sourceData_;
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 * 
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public final ResultData openResultData() {
		return this._resultData_;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Marshaller} und gibt ihn zurück.
	 * 
	 * @return Konfigurator. */
	public final MarshallerData openMarshallerData() {
		return this._marshallerData_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._sourceData_, this._resultData_, this._marshallerData_);
	}

}
