package bee.creative.xml;

import java.io.StringWriter;
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

/** Diese Klasse implementiert einen Konfigurator zum {@link #marshal() Ausgeben} eines Objekts mit Hilfe eines {@link Marshaller}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLMarshaller {

	public static class ResultValue extends ResultBuilder.Value<ResultValue> {

		@Override
		public ResultValue owner() {
			return this;
		}

	}

	public class ResultProxy extends ResultBuilder.Proxy<XMLMarshaller> {

		@Override
		protected ResultValue value() {
			return XMLMarshaller.this.result();
		}

		@Override
		public XMLMarshaller owner() {
			return XMLMarshaller.this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Marshaller}.
	 *
	 * @see Transformer#transform(Source, Result) */
	public static class SourceValue extends BaseValueBuilder<Object, SourceValue> {

		Object value;

		@Override
		public Object get() {
			return this.value;
		}

		@Override
		public void set(final Object value) {
			this.value = value;
		}

		@Override
		public SourceValue owner() {
			return this;
		}

	}

	public class SourceProxy extends BaseValueBuilder<Object, XMLMarshaller> {

		@Override
		public Object get() {
			return XMLMarshaller.this.source().get();
		}

		@Override
		public void set(final Object value) {
			XMLMarshaller.this.source().set(value);
		}

		@Override
		public XMLMarshaller owner() {
			return XMLMarshaller.this;
		}

	}

	public static class MarshallerValue extends MarshallerBuilder.Value<MarshallerValue> {

		@Override
		public MarshallerValue owner() {
			return this;
		}

	}

	public class MarshallerProxy extends MarshallerBuilder.Proxy<XMLMarshaller> {

		@Override
		protected MarshallerValue value() {
			return XMLMarshaller.this.marshaller();
		}

		@Override
		public XMLMarshaller owner() {
			return XMLMarshaller.this;
		}

	}

	/** Diese Methode gibt einen neuen {@link XMLMarshaller} zurück, dessen {@link JAXBContext} die gegebenen Klassen nutzt.
	 *
	 * @param classes Klasse.
	 * @return {@link XMLMarshaller}. */
	public static XMLMarshaller from(final Class<?>... classes) {
		return new XMLMarshaller().forMarshaller().forContext().forClasses().putAll(classes);
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

	final ResultValue result = new ResultValue();

	final SourceValue source = new SourceValue();

	final MarshallerValue marshaller = new MarshallerValue();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLMarshaller use(final XMLMarshaller data) {
		if (data == null) return this;
		this.source.use(data.source);
		this.result.use(data.result);
		this.marshaller.use(data.marshaller);
		return this;
	}

	/** Diese Methode führt die Formatierung aus und gibt {@code this} zurück.
	 *
	 * @see #forSource()
	 * @see #forResult()
	 * @see Marshaller#marshal(Object, Result)
	 * @return {@code this}.
	 * @throws SAXException Wenn {@link MarshallerBuilder#putValue()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link Marshaller#marshal(Object, Result)} eine entsprechende Ausnahme auslöst. */
	public XMLMarshaller marshal() throws SAXException, JAXBException {
		final Marshaller marshaller = this.marshaller().putValue();
		final Object source = this.source().getValue();
		final Result result = this.result().getValue();
		marshaller.marshal(source, result);
		return this;
	}

	/** Diese Methode überführt die {@link #forSource() Eingabedaten} in einen Dokumentknoten und gibt diesen zurück.
	 *
	 * @see ResultValue#useNode()
	 * @see #forResult()
	 * @return Dokumentknoten.
	 * @throws SAXException Wenn {@link #marshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #marshal()} eine entsprechende Ausnahme auslöst. */
	public Node marshalNode() throws SAXException, JAXBException {
		final DOMResult res = new DOMResult();
		this.forResult().useValue(res).marshal();
		return res.getNode();
	}

	/** Diese Methode überführt die {@link #forSource() Eingabedaten} in eine Zeichenkette und gibt diese zurück.
	 *
	 * @see StringWriter
	 * @see ResultValue #useWriter(Writer)
	 * @see #forResult()
	 * @return Zeichenkette.
	 * @throws SAXException Wenn {@link #marshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #marshal()} eine entsprechende Ausnahme auslöst. */
	public String marshalString() throws SAXException, JAXBException {
		final StringWriter res = new StringWriter();
		this.forResult().useWriter(res).marshal();
		return res.toString();
	}

	public ResultValue result() {
		return this.result;
	}

	public SourceValue source() {
		return this.source;
	}

	/** Diese Methode öffnet den Konfigurator für die Ausgabedaten und gibt ihn zurück.
	 *
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public ResultProxy forResult() {
		return new ResultProxy();
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Marshaller#marshal(Object, Result)
	 * @return Konfigurator. */
	public SourceProxy forSource() {
		return new SourceProxy();
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Marshaller} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public MarshallerValue marshaller() {
		return this.marshaller;
	}

	public MarshallerProxy forMarshaller() {
		return new MarshallerProxy();
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.source(), this.result(), this.marshaller());
	}

}
