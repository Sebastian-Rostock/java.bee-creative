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
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseValueBuilder;
import bee.creative.xml.XMLMarshaller.SourceData2;

/** Diese Klasse implementiert einen Konfigurator zum {@link #marshal() Ausgeben} eines Objekts mit Hilfe eines {@link Marshaller}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLMarshaller {

	public static class ResultData extends BaseResultData<ResultData> {

		Result value;

		@Override
		public Result get() {
			return value;
		}

		@Override
		public void set(Result value) {
			this.value = value;
		}

		@Override
		public ResultData owner() {
			return this;
		}

	}

	public class ResultData2 extends BaseResultData<XMLMarshaller> {

		ResultData resultData = new ResultData();

		@Override
		public Result get() {
			return resultData.get();
		}

		@Override
		public void set(Result value) {
			this.resultData.set(value);
		}

		@Override
		public XMLMarshaller owner() {
			return XMLMarshaller.this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Marshaller}.
	 *
	 * @see Transformer#transform(Source, Result) */
	public static class SourceData extends BaseValueBuilder<Object, SourceData> {

		Object value;

		@Override
		public Object get() {
			return value;
		}

		@Override
		public void set(Object value) {
			this.value = value;
		}

		@Override
		public SourceData owner() {
			return this;
		}

	}

	public class SourceData2 extends BaseValueBuilder<Object, XMLMarshaller> {

		SourceData sourceData = new SourceData();

		@Override
		public Object get() {
			return sourceData.get();
		}

		@Override
		public void set(Object value) {
			sourceData.set(value);
		}

		@Override
		public XMLMarshaller owner() {
			return XMLMarshaller.this;
		}

	}

	public static class MarshallerData extends BaseMarshallerData<MarshallerData> {

		@Override
		public MarshallerData owner() {
			return this;
		}

	}

	public class MarshallerData2 extends BaseMarshallerData<XMLMarshaller> {

		
		MarshallerData marshallerData = new MarshallerData();
		
		
		

	}

	/** Diese Methode gibt einen neuen {@link XMLMarshaller} zurück, dessen {@link JAXBContext} die gegebenen Klassen nutzt.
	 *
	 * @param classes Klasse.
	 * @return {@link XMLMarshaller}. */
	public static XMLMarshaller from(final Class<?>... classes) {
		return new XMLMarshaller().forMarshaller().context().ClassData().putAll(classes).closeClassesData().closeContextData().closeMarshallerData();
	}

	/** Diese Methode ist eine Abkürzung für {@code XMLMarshaller.from(object.getClass()).openSourceData().use(object).closeSourceData().marshalNode()}.
	 *
	 * @see #from(Class...)
	 * @see #marshalString() */
	public static Node marshalNode(final Object object) throws SAXException, JAXBException {
		return XMLMarshaller.from(object.getClass()).forSource().useValue(object).marshalNode();
	}

	/** Diese Methode ist eine Abkürzung für {@code XMLMarshaller.from(object.getClass()).openSourceData().use(object).closeSourceData().marshalString()}.
	 *
	 * @see #from(Class...)
	 * @see #marshalString() */
	public static String marshalString(final Object object) throws SAXException, JAXBException {
		return XMLMarshaller.from(object.getClass()).forSource().useValue(object).marshalString();
	}

	final ResultData2 resultData = new ResultData2();

	final SourceData2 sourceData = new SourceData2();

	/** Dieses Feld speichert den Konfigurator {@link #forMarshaller()}. */
	final BaseMarshallerData<XMLMarshaller> marshallerData = new BaseMarshallerData<XMLMarshaller>();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLMarshaller use(final XMLMarshaller data) {
		if (data == null) return this;
		this.sourceData.use(data.sourceData);
		this.resultData.use(data.resultData);
		this.marshallerData.use(data.marshallerData);
		return this;
	}

	/** Diese Methode führt die Formatierung aus und gibt {@code this} zurück.
	 *
	 * @see #forSource()
	 * @see #forResult()
	 * @see Marshaller#marshal(Object, Result)
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link BaseMarshallerData#getMarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link Marshaller#marshal(Object, Result)} eine entsprechende Ausnahme auslöst. */
	public XMLMarshaller marshal() throws SAXException, JAXBException {
		final Marshaller marshaller = this.marshallerData.getMarshaller();
		synchronized (marshaller) {
			final Object source = this.sourceData;
			final Result result = this.resultData.getResult();
			marshaller.marshal(source, result);
		}
		return this;
	}

	/** Diese Methode überführt die {@link #forSource() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück.
	 *
	 * @see ResultData#useNode()
	 * @see #forResult()
	 * @return Dokumentknoten.
	 * @throws SAXException Wenn {@link #marshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #marshal()} eine entsprechende Ausnahme auslöst. */
	public Node marshalNode() throws SAXException, JAXBException {
		final DOMResult result = new DOMResult();
		this.forResult().useValue(result).marshal().forResult().clear();
		return result.getNode();
	}

	/** Diese Methode überführt die {@link #forSource() Eingabedaten} in eine Zeichenkette und gibt diese zurück.
	 *
	 * @see StringWriter
	 * @see ResultData #useWriter(Writer)
	 * @see #forResult()
	 * @return Zeichenkette.
	 * @throws SAXException Wenn {@link #marshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #marshal()} eine entsprechende Ausnahme auslöst. */
	public String marshalString() throws SAXException, JAXBException {
		final StringWriter result = new StringWriter();
		this.forResult().useWriter(result).marshal().forResult().clear();
		return result.toString();
	}

	public ResultData result() {
		return resultData.resultData;
	}

	public SourceData source() {
		return sourceData.sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 *
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public ResultData2 forResult() {
		return resultData;
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public SourceData2 forSource() {
		return sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Marshaller} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public BaseMarshallerData<XMLMarshaller> forMarshaller() {
		return this.marshallerData;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.resultData, this.marshallerData);
	}

}
