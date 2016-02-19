package bee.creative.xml;

import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder;
import bee.creative.util.Builders.BaseSetBuilder;
import bee.creative.util.Objects;

/** Diese Klasse implementiert den Konfigurator für einen {@link JAXBContext}.
 * 
 * @see JAXBContext#newInstance(Class[], java.util.Map)
 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
public abstract class BaseContextData<GThis> extends BaseBuilder<JAXBContext, GThis> {

	/** Diese Klasse implementiert den Konfigurator für die Klassen zur Erzeugung des {@link JAXBContext}.
	 * 
	 * @see JAXBContext#newInstance(Class[], java.util.Map)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ClassData<GOwner> extends BaseSetBuilder<Class<?>, ClassData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closeClassesData();

		{}

		/** {@inheritDoc} */
		@Override
		protected ClassData<GOwner> _this_() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften zur Erzeugung des {@link JAXBContext}.
	 * 
	 * @see JAXBContext#newInstance(Class[], java.util.Map)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder<String, Object, PropertyData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 * 
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		{}

		/** {@inheritDoc} */
		@Override
		protected PropertyData<GOwner> _this_() {
			return this;
		}

	}

	{}

	/** Dieses Feld speichert den {@link JAXBContext}. */
	JAXBContext _context_;

	/** Dieses Feld speichert den Konfigurator für {@link #openClassData()}. */
	final ClassData<GThis> _classData_ = new ClassData<GThis>() {

		@Override
		public GThis closeClassesData() {
			return BaseContextData.this._this_();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}. */
	final PropertyData<GThis> _propertyData_ = new PropertyData<GThis>() {

		@Override
		public GThis closePropertyData() {
			return BaseContextData.this._this_();
		}

	};

	{}

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 * 
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseContextData<?> data) {
		if (data == null) return this._this_();
		this._context_ = data._context_;
		this._classData_.use(data._classData_);
		this._propertyData_.use(data._propertyData_);
		return this._this_();
	}

	/** Diese Methode gibt den {@link JAXBContext} zurück.<br>
	 * Wenn über {@link #useContext(JAXBContext)} noch kein {@link JAXBContext} gesetzt wurden, werden über {@link JAXBContext#newInstance(Class[], Map)} ein
	 * neuer erstellt und dieser über {@link #useContext(JAXBContext)} gesetzt. Die zur Erstellung verwendeten Klassen und Eigenschaften können über
	 * {@link #openClassData()} bzw. {@link #openPropertyData()} konfiguriert werden.
	 * 
	 * @see #useContext(JAXBContext)
	 * @return {@link JAXBContext}.
	 * @throws JAXBException Wenn {@link JAXBContext#newInstance(Class[], Map)} eine entsprechende Ausnahme auslöst. */
	public final JAXBContext getContext() throws JAXBException {
		JAXBContext result = this._context_;
		if (result != null) return result;
		final Set<Class<?>> classes = this._classData_.getItems();
		result = JAXBContext.newInstance(classes.toArray(new Class<?>[classes.size()]), this._propertyData_.getEntries());
		this.useContext(result);
		return result;
	}

	/** Diese Methode setzt den {@link JAXBContext} und gibt {@code this} zurück.
	 * 
	 * @param context {@link JAXBContext} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useContext(final JAXBContext context) {
		this._context_ = context;
		return this._this_();
	}

	/** Diese Methode setzt den {@link JAXBContext} auf {@code null} und gibt {@code this} zurück.
	 * 
	 * @see #useContext(JAXBContext)
	 * @return {@code this}. */
	public final GThis resetContext() {
		return this.useContext(null);
	}

	/** Diese Methode öffnet den Konfigurator für die Klassen zur Erzeugung des {@link JAXBContext} und gibt ihn zurück.
	 * 
	 * @see JAXBContext#newInstance(Class[], Map)
	 * @return Konfigurator. */
	public final ClassData<GThis> openClassData() {
		return this._classData_;
	}

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften zur Erzeugung des {@link JAXBContext} und gibt ihn zurück.
	 * 
	 * @see JAXBContext#newInstance(Class[], Map)
	 * @return Konfigurator. */
	public final PropertyData<GThis> openPropertyData() {
		return this._propertyData_;
	}

	{}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis _this_();

	/** {@inheritDoc}
	 * 
	 * @see #getContext() */
	@Override
	public final JAXBContext build() throws IllegalStateException {
		try {
			return this.getContext();
		} catch (final JAXBException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this._classData_, this._propertyData_);
	}

}
