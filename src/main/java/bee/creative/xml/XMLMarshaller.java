package bee.creative.xml;

import java.io.StringWriter;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import bee.creative.util.Objects;
import bee.creative.util.Producers.BaseValueBuilder;

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

		/** {@inheritDoc} */
		@Override
		protected final SourceData customThis() {
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

		/** {@inheritDoc} */
		@Override
		protected final ResultData customThis() {
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

		/** {@inheritDoc} */
		@Override
		protected final MarshallerData customThis() {
			return this;
		}

	}

	/** Diese Methode gibt einen neuen {@link XMLMarshaller} zurück, dessen {@link JAXBContext} die gegebenen Klassen nutzt.
	 *
	 * @param classes Klasse.
	 * @return {@link XMLMarshaller}. */
	public static XMLMarshaller from(final Class<?>... classes) {
		return new XMLMarshaller().openMarshallerData().openContextData().openClassData().putAll(classes).closeClassesData().closeContextData()
			.closeMarshallerData();
	}

	/** Diese Methode ist eine Abkürtung für {@code XMLMarshaller.from(object.getClass()).openSourceData().use(object).closeSourceData().marshalNode()}.
	 *
	 * @see #from(Class...)
	 * @see #marshalString() */
	@SuppressWarnings ("javadoc")
	public static Node marshalNode(final Object object) throws SAXException, JAXBException {
		return XMLMarshaller.from(object.getClass()).openSourceData().use(object).closeSourceData().marshalNode();
	}

	/** Diese Methode ist eine Abkürtung für {@code XMLMarshaller.from(object.getClass()).openSourceData().use(object).closeSourceData().marshalString()}.
	 *
	 * @see #from(Class...)
	 * @see #marshalString() */
	@SuppressWarnings ("javadoc")
	public static String marshalString(final Object object) throws SAXException, JAXBException {
		return XMLMarshaller.from(object.getClass()).openSourceData().use(object).closeSourceData().marshalString();
	}

	/** Dieses Feld speichert den Konfigurator {@link #openSourceData()}. */
	final SourceData sourceData = new SourceData();

	/** Dieses Feld speichert den Konfigurator {@link #openResultData()}. */
	final ResultData resultData = new ResultData();

	/** Dieses Feld speichert den Konfigurator {@link #openMarshallerData()}. */
	final MarshallerData marshallerData = new MarshallerData();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final XMLMarshaller use(final XMLMarshaller data) {
		if (data == null) return this;
		this.sourceData.use(data.sourceData);
		this.resultData.use(data.resultData);
		this.marshallerData.use(data.marshallerData);
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
		final Marshaller marshaller = this.marshallerData.getMarshaller();
		synchronized (marshaller) {
			final Object source = this.sourceData.get();
			final Result result = this.resultData.getResult();
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
	public final Node marshalNode() throws SAXException, JAXBException {
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
	public final String marshalString() throws SAXException, JAXBException {
		final StringWriter result = new StringWriter();
		this.openResultData().useWriter(result).closeResultData().marshal().openResultData().resetResult();
		return result.toString();
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public final SourceData openSourceData() {
		return this.sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 *
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public final ResultData openResultData() {
		return this.resultData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Marshaller} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public final MarshallerData openMarshallerData() {
		return this.marshallerData;
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.resultData, this.marshallerData);
	}

}
