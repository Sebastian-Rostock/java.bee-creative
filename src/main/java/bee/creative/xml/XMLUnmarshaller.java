package bee.creative.xml;

import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import org.xml.sax.SAXException;
import bee.creative.util.Objects;

/**
 * Diese Klasse implementiert einen Konfigurator zum {@link #unmarshal() Ausgeben/Formatieren} eines Objekts mit Hilfe eines {@link Unmarshaller}.
 * 
 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class XMLUnmarshaller {

	/**
	 * Diese Klasse implementiert den Konfigurator für die Eingabedaten eines {@link Unmarshaller}.
	 * 
	 * @see Transformer#transform(Source, Result)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class SourceData extends BaseSourceData<SourceData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public final XMLUnmarshaller closeSourceData() {
			return XMLUnmarshaller.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final SourceData _this_() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator für den {@link Unmarshaller}.
	 * 
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public final class UnmarshallerData extends BaseUnmarshallerData<UnmarshallerData> {

		/**
		 * Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer.
		 */
		public final XMLUnmarshaller closeUnmarshallerData() {
			return XMLUnmarshaller.this;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected final UnmarshallerData _this_() {
			return this;
		}

	}

	{}

	/**
	 * Diese Methode gibt einen neuen {@link XMLUnmarshaller} zurück, dessen {@link JAXBContext} die gegebenen Klassen nutzt.
	 * 
	 * @param classes Klasse.
	 * @return {@link XMLUnmarshaller}.
	 */
	public static final XMLUnmarshaller from(final Class<?>... classes) {
		return new XMLUnmarshaller() //
			.openUnmarshallerData().openContextData().openClassData() //
			.useItems(Arrays.asList(classes)) //
			.closeClassesData().closeContextData().closeUnmarshallerData();
	}

	{}

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openSourceData()}.
	 */
	final SourceData _sourceData_ = new SourceData();

	/**
	 * Dieses Feld speichert den Konfigurator {@link #openUnmarshallerData()}.
	 */
	final UnmarshallerData _unmarshallerData_ = new UnmarshallerData();

	{}

	/**
	 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}.
	 */
	public final XMLUnmarshaller use(final XMLUnmarshaller data) {
		if (data == null) return this;
		this._sourceData_.use(data._sourceData_);
		this._unmarshallerData_.use(data._unmarshallerData_);
		return this;
	}

	/**
	 * Diese Methode parst die {@link #openSourceData() Eingabedaten} in ein Objekt und gibt dieses zurück.
	 * 
	 * @see #openSourceData()
	 * @see Unmarshaller#unmarshal(Source)
	 * @return geparstes Objekt.
	 * @throws SAXException Wenn {@link UnmarshallerData#getUnmarshaller()} eine entsprechende Ausnahme auslöst.
	 * @throws JAXBException Wenn {@link Unmarshaller#unmarshal(Source)} eine entsprechende Ausnahme auslöst.
	 */
	public final Object unmarshal() throws SAXException, JAXBException {
		final Unmarshaller unmarshaller = this._unmarshallerData_.getUnmarshaller();
		synchronized (unmarshaller) {
			final Source source = this._sourceData_.getSource();
			return unmarshaller.unmarshal(source);
		}
	}

	/**
	 * Diese Methode öffnet den Konfigurator für die Eingabedaten und gibt ihn zurück.
	 * 
	 * @see Unmarshaller#unmarshal(Source)
	 * @return Konfigurator.
	 */
	public final SourceData openSourceData() {
		return this._sourceData_;
	}

	/**
	 * Diese Methode öffnet den Konfigurator für den {@link Unmarshaller} und gibt ihn zurück.
	 * 
	 * @return Konfigurator.
	 */
	public final UnmarshallerData openUnmarshallerData() {
		return this._unmarshallerData_;
	}

	{}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._sourceData_, this._unmarshallerData_);
	}

}