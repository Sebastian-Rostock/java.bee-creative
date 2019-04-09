package bee.creative.xml;

import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import bee.creative.lang.Objects;
import bee.creative.util.Builders.BaseBuilder;
import bee.creative.util.Builders.BaseMapBuilder2;
import bee.creative.util.Builders.BaseSetBuilder2;

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
	public static abstract class ClassData<GOwner> extends BaseSetBuilder2<Class<?>, ClassData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closeClassesData();

		/** {@inheritDoc} */
		@Override
		protected ClassData<GOwner> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator für die Eigenschaften zur Erzeugung des {@link JAXBContext}.
	 *
	 * @see JAXBContext#newInstance(Class[], java.util.Map)
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class PropertyData<GOwner> extends BaseMapBuilder2<String, Object, PropertyData<GOwner>> {

		/** Diese Methode schließt die Konfiguration ab und gibt den Besitzer zurück.
		 *
		 * @return Besitzer. */
		public abstract GOwner closePropertyData();

		/** {@inheritDoc} */
		@Override
		protected PropertyData<GOwner> customThis() {
			return this;
		}

	}

	/** Dieses Feld speichert den {@link JAXBContext}. */
	JAXBContext context;

	/** Dieses Feld speichert den Konfigurator für {@link #openClassData()}. */
	final ClassData<GThis> classData = new ClassData<GThis>() {

		@Override
		public GThis closeClassesData() {
			return BaseContextData.this.customThis();
		}

	};

	/** Dieses Feld speichert den Konfigurator für {@link #openPropertyData()}. */
	final PropertyData<GThis> propertyData = new PropertyData<GThis>() {

		@Override
		public GThis closePropertyData() {
			return BaseContextData.this.customThis();
		}

	};

	/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
	 *
	 * @param data Konfigurator oder {@code null}.
	 * @return {@code this}. */
	public final GThis use(final BaseContextData<?> data) {
		if (data == null) return this.customThis();
		this.context = data.context;
		this.classData.use(data.classData);
		this.propertyData.use(data.propertyData);
		return this.customThis();
	}

	/** Diese Methode gibt den {@link JAXBContext} zurück. Wenn über {@link #useContext(JAXBContext)} noch kein {@link JAXBContext} gesetzt wurden, werden über
	 * {@link JAXBContext#newInstance(Class[], Map)} ein neuer erstellt und dieser über {@link #useContext(JAXBContext)} gesetzt. Die zur Erstellung verwendeten
	 * Klassen und Eigenschaften können über {@link #openClassData()} bzw. {@link #openPropertyData()} konfiguriert werden.
	 *
	 * @see #useContext(JAXBContext)
	 * @return {@link JAXBContext}.
	 * @throws JAXBException Wenn {@link JAXBContext#newInstance(Class[], Map)} eine entsprechende Ausnahme auslöst. */
	public final JAXBContext getContext() throws JAXBException {
		JAXBContext result = this.context;
		if (result != null) return result;
		final Set<Class<?>> classes = this.classData.get();
		result = JAXBContext.newInstance(classes.toArray(new Class<?>[classes.size()]), this.propertyData.get());
		this.useContext(result);
		return result;
	}

	/** Diese Methode setzt den {@link JAXBContext} und gibt {@code this} zurück.
	 *
	 * @param context {@link JAXBContext} oder {@code null}.
	 * @return {@code this}. */
	public final GThis useContext(final JAXBContext context) {
		this.context = context;
		return this.customThis();
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
		return this.classData;
	}

	/** Diese Methode öffnet den Konfigurator für die Eigenschaften zur Erzeugung des {@link JAXBContext} und gibt ihn zurück.
	 *
	 * @see JAXBContext#newInstance(Class[], Map)
	 * @return Konfigurator. */
	public final PropertyData<GThis> openPropertyData() {
		return this.propertyData;
	}

	/** {@inheritDoc} */
	@Override
	protected abstract GThis customThis();

	/** {@inheritDoc}
	 *
	 * @see #getContext() */
	@Override
	public final JAXBContext get() throws IllegalStateException {
		try {
			return this.getContext();
		} catch (final JAXBException cause) {
			throw new IllegalStateException(cause);
		}
	}

	/** {@inheritDoc} */
	@Override
	public final String toString() {
		return Objects.toInvokeString(this, this.classData, this.propertyData);
	}

}
