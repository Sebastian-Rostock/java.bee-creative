package bee.creative.xml;

import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert einen Konfigurator zum {@link #unmarshal() Ausgeben} eines Objekts mit Hilfe eines {@link Unmarshaller}.
 *
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class XMLUnmarshaller {

	/** Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Unmarshaller}.
	 *
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public class SourceData extends BaseSourceData<SourceData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public XMLUnmarshaller closeSourceData() {
			return XMLUnmarshaller.this;
		}

		@Override
		protected SourceData customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für den {@link Unmarshaller}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public class UnmarshallerData extends BaseUnmarshallerData<UnmarshallerData> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public XMLUnmarshaller closeUnmarshallerData() {
			return XMLUnmarshaller.this;
		}

		@Override
		protected UnmarshallerData customThis() {
			return this;
		}

	}

	/** Diese Methode gibt einen neuen {@link XMLUnmarshaller} zurück, dessen {@link JAXBContext} die gegebenen Klassen nutzt.
	 *
	 * @param classes Klasse.
	 * @return {@link XMLUnmarshaller}. */
	public static XMLUnmarshaller from(final Class<?>... classes) {
		return new XMLUnmarshaller().openUnmarshallerData().openContextData().openClassData().putAll(Arrays.asList(classes)).closeClassesData().closeContextData()
			.closeUnmarshallerData();
	}

	/** Diese Methode ist eine Abkürzung für {@code XMLUnmarshaller.from(classes).unmarshalNode(source)}.
	 *
	 * @see #from(Class...)
	 * @see #unmarshalNode(Node) */
	public static Object unmarshalNode(final Node source, final Class<?>... classes) throws SAXException, JAXBException {
		return XMLUnmarshaller.from(classes).unmarshalNode(source);
	}

	/** Diese Methode ist eine Abkürzung für {@code XMLUnmarshaller.from(classes).unmarshalString(source)}.
	 *
	 * @see #from(Class...)
	 * @see #unmarshalString(String) */
	public static Object unmarshalString(final String source, final Class<?>... classes) throws SAXException, JAXBException {
		return XMLUnmarshaller.from(classes).unmarshalString(source);
	}

	/** Dieses Feld speichert den Konfigurator {@link #openSourceData()}. */
	final SourceData sourceData = new SourceData();

	/** Dieses Feld speichert den Konfigurator {@link #openUnmarshallerData()}. */
	final UnmarshallerData unmarshallerData = new UnmarshallerData();

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public XMLUnmarshaller use(final XMLUnmarshaller data) {
		if (data == null) return this;
		this.sourceData.use(data.sourceData);
		this.unmarshallerData.use(data.unmarshallerData);
		return this;
	}

	/** Diese Methode überführt die {@link #openSourceData() Eingabedaten} in ein Objekt und gibt dieses zurück.
	 *
	 * @see #openSourceData()
	 * @see Unmarshaller#unmarshal(Source)
	 * @return geparstes Objekt.
	 * @throws SAXException Wenn {@link UnmarshallerData#getUnmarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link Unmarshaller#unmarshal(Source)} eine entsprechende Ausnahme auslöst. */
	public Object unmarshal() throws SAXException, JAXBException {
		final Unmarshaller unmarshaller = this.unmarshallerData.getUnmarshaller();
		synchronized (unmarshaller) {
			final Source source = this.sourceData.getSource();
			return unmarshaller.unmarshal(source);
		}
	}

	/** Diese Methode überführt den gegebenen Dokumentknoten in ein Objekt und gibt dieses zurück.
	 *
	 * @see SourceData#useNode(Node)
	 * @see #openSourceData()
	 * @param source Dokumentknoten.
	 * @return geparstes Objekt.
	 * @throws SAXException Wenn {@link #unmarshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #unmarshal()} eine entsprechende Ausnahme auslöst. */
	public Object unmarshalNode(final Node source) throws SAXException, JAXBException {
		final Object result = this.openSourceData().useNode(source).closeSourceData().unmarshal();
		this.openSourceData().resetSource();
		return result;
	}

	/** Diese Methode überführt die gegebene Zeichenkette in ein Objekt und gibt dieses zurück.
	 *
	 * @see SourceData#useText(String)
	 * @see #openSourceData()
	 * @param source Zeichenkette.
	 * @return geparstes Objekt.
	 * @throws SAXException Wenn {@link #unmarshal} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link #unmarshal()} eine entsprechende Ausnahme auslöst. */
	public Object unmarshalString(final String source) throws SAXException, JAXBException {
		final Object result = this.openSourceData().useText(source).closeSourceData().unmarshal();
		this.openSourceData().resetSource();
		return result;
	}

	/** Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 *
	 * @see Unmarshaller#unmarshal(Source)
	 * @return Konfigurator. */
	public SourceData openSourceData() {
		return this.sourceData;
	}

	/** Diese Methode öffnet den Konfigurator für den {@link Unmarshaller} und gibt ihn zurück.
	 *
	 * @return Konfigurator. */
	public UnmarshallerData openUnmarshallerData() {
		return this.unmarshallerData;
	}

	@Override
	public String toString() {
		return Objects.toInvokeString(this, this.sourceData, this.unmarshallerData);
	}

}
