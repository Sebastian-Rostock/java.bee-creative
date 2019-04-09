package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.util.AbstractProxyCollection;
import bee.creative.util.AbstractProxyList;
import bee.creative.util.AbstractProxyMap;
import bee.creative.util.AbstractProxySet;

/** Diese Klasse implementiert gundlegende {@link Property}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Properties {

	/** Diese Klasse implementiert ein abstraktes {@link Property} als {@link BaseObject}. */
	public static abstract class BaseProperty<GValue> extends BaseObject implements Property<GValue> {
	}

	/** Diese Klasse implementiert {@link Properties#valueProperty(Object)}. */
	@SuppressWarnings ("javadoc")
	public static class ValueProperty<GValue> extends BaseProperty<GValue> {

		public static final Property<?> EMPTY = new ValueProperty<>(null);

		public final GValue value;

		public ValueProperty(final GValue value) {
			this.value = value;
		}

		@Override
		public GValue get() {
			return this.value;
		}

		@Override
		public void set(final GValue value) {
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Properties#setupProperty(Producer, Property)}. */
	@SuppressWarnings ("javadoc")
	public static class SetupProperty<GValue> extends BaseProperty<GValue> {

		public final Producer<? extends GValue> setup;

		public final Property<GValue> property;

		public SetupProperty(final Producer<? extends GValue> setup, final Property<GValue> property) {
			this.setup = Objects.notNull(setup);
			this.property = Objects.notNull(property);
		}

		@Override
		public GValue get() {
			GValue result = this.property.get();
			if (result != null) return result;
			result = this.setup.get();
			this.property.set(result);
			return result;
		}

		@Override
		public void set(final GValue value) {
			this.property.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setup, this.property);
		}

	}

	/** Diese Klasse implementiert {@link Properties#nativeProperty(java.lang.reflect.Field, boolean)}. */
	@SuppressWarnings ("javadoc")
	public static class NativeProperty<GValue> extends BaseProperty<GValue> {

		public final java.lang.reflect.Field field;

		public NativeProperty(final java.lang.reflect.Field field, final boolean forceAccessible) {
			if (!Modifier.isStatic(field.getModifiers())) throw new IllegalArgumentException();
			this.field = forceAccessible ? Natives.forceAccessible(field) : Objects.notNull(field);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.field.get(null);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GValue value) {
			try {
				this.field.set(null, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.field, this.field.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Properties#compositeProperty(Producer, Consumer)}. */
	@SuppressWarnings ("javadoc")
	public static class CompositeProperty<GValue> extends BaseProperty<GValue> {

		public final Producer<? extends GValue> producer;

		public final Consumer<? super GValue> consumer;

		public CompositeProperty(final Producer<? extends GValue> producer, final Consumer<? super GValue> consumer) {
			this.producer = Objects.notNull(producer);
			this.consumer = Objects.notNull(consumer);
		}

		@Override
		public GValue get() {
			return this.producer.get();
		}

		@Override
		public void set(final GValue value) {
			this.consumer.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.producer, this.consumer);
		}

	}

	/** Diese Klasse implementiert {@link Properties#translatedProperty(Translator, Property)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedProperty<GTarget, GSource> extends BaseProperty<GTarget> {

		public final Property<GSource> property;

		public final Translator<GSource, GTarget> translator;

		public TranslatedProperty(final Property<GSource> property, final Translator<GSource, GTarget> translator) {
			this.property = Objects.notNull(property);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public GTarget get() {
			return this.translator.toTarget(this.property.get());
		}

		@Override
		public void set(final GTarget value) {
			this.property.set(this.translator.toSource(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.property, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Properties#synchronizedProperty(Object, Property)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProperty<GValue> extends BaseProperty<GValue> {

		public final Object mutex;

		public final Property<GValue> property;

		public SynchronizedProperty(final Object mutex, final Property<GValue> property) {
			this.mutex = Objects.notNull(mutex, this);
			this.property = Objects.notNull(property);
		}

		@Override
		public GValue get() {
			synchronized (this.mutex) {
				return this.property.get();
			}
		}

		@Override
		public void set(final GValue value) {
			synchronized (this.mutex) {
				this.property.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.property);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toField(Property)}. */
	@SuppressWarnings ("javadoc")
	static class PropertyField<GValue> implements Field<Object, GValue> {

		public final Property<GValue> property;

		public PropertyField(final Property<GValue> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		public GValue get(final Object input) {
			return this.property.get();
		}

		@Override
		public void set(final Object input, final GValue value) {
			this.property.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.property);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toSet(Property)}. */
	@SuppressWarnings ("javadoc")
	static class PropertySet<GItem> extends AbstractProxySet<GItem, Set<GItem>> {

		public final Property<Set<GItem>> property;

		public PropertySet(final Property<Set<GItem>> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		protected Set<GItem> getData(final boolean readonly) {
			return this.property.get();
		}

		@Override
		protected void setData(final Set<GItem> items) {
			this.property.set(items);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toList(Property)}. */
	@SuppressWarnings ("javadoc")
	static class PropertyList<GItem> extends AbstractProxyList<GItem, List<GItem>> {

		public final Property<List<GItem>> property;

		public PropertyList(final Property<List<GItem>> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		protected List<GItem> getData(final boolean readonly) {
			return this.property.get();
		}

		@Override
		protected void setData(final List<GItem> items) {
			this.property.set(items);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toMap(Property)}. */
	@SuppressWarnings ("javadoc")
	static class PropertyMap<GKey, GValue> extends AbstractProxyMap<GKey, GValue, Map<GKey, GValue>> {

		public final Property<Map<GKey, GValue>> property;

		public PropertyMap(final Property<Map<GKey, GValue>> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		protected Map<GKey, GValue> getData(final boolean readonly) {
			return this.property.get();
		}

		@Override
		protected void setData(final Map<GKey, GValue> items) {
			this.property.set(items);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toCollection(Property)}. */
	@SuppressWarnings ("javadoc")
	static class PropertyCollection<GItem> extends AbstractProxyCollection<GItem, Collection<GItem>> {

		public final Property<Collection<GItem>> property;

		public PropertyCollection(final Property<Collection<GItem>> property) {
			this.property = Objects.notNull(property);
		}

		@Override
		protected Collection<GItem> getData(final boolean readonly) {
			return this.property.get();
		}

		@Override
		protected void setData(final Collection<GItem> items) {
			this.property.set(items);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#valueProperty(Object) Properties.valueProperty(null)}. */
	public static <GValue> Property<GValue> emptyProperty() {
		return Properties.valueProperty(null);
	}

	/** Diese Methode gibt ein {@link Property} zurück, das stets den gegebenen Wert liefert und das Schreiben ignoriert.
	 *
	 * @param value Wert.
	 * @return {@code value}-{@link Property}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Property<GValue> valueProperty(final GValue value) {
		if (value == null) return (Property<GValue>)ValueProperty.EMPTY;
		return new ValueProperty<>(value);
	}

	/** Diese Methode gibt ein initialisierendes {@link Property} zurück. Das Schreiben wird direkt an das gegebene {@link Property} delegiert. Beim Lesen wird
	 * der Wert zuerst über das gegebene {@link Property} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. mit Hilfe des gegebenen
	 * {@link Producer} ermittelt und über das {@link Property} geschrieben.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param setup Methode zur Initialisierung.
	 * @param property Eigenschaft zur Manipulation.
	 * @return {@code setup}-{@link Property}.
	 * @throws NullPointerException Wenn {@code setup} bzw. {@code property} {@code null} ist. */
	public static <GValue> Property<GValue> setupProperty(final Producer<? extends GValue> setup, final Property<GValue> property) throws NullPointerException {
		return new SetupProperty<>(setup, property);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(String, boolean) Properties.nativeProperty(fieldPath, true)}. */
	public static <GValue> Property<GValue> nativeProperty(final String fieldPath) throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(fieldPath, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(java.lang.reflect.Field) Properties.nativeProperty(Natives.parseField(fieldPath),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseField(String) */
	public static <GValue> Property<GValue> nativeProperty(final String fieldPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(Natives.parseField(fieldPath), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(java.lang.reflect.Field, boolean) Properties.nativeProperty(field, true)}. */
	public static <GValue> Property<GValue> nativeProperty(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(field, true);
	}

	/** Diese Methode gibt ein {@link Property} zum gegebenen {@link java.lang.reflect.Field nativen statischen Datenfeld} zurück. Das Lesen des gelieferten
	 * {@link Property} erfolgt über {@code field.get(null)}. Das Schreiben eines Werts {@code value} erfolgt dazu über {@code field.set(null, value)}.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field Datenfeld.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Property}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das native Datenfeld nicht zugrifbar oder nicht statisch ist. */
	public static <GValue> Property<GValue> nativeProperty(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeProperty<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(Method, Method, boolean) Properties.compositeProperty(getMethod, setMethod,
	 * true)}. **/
	public static <GValue> Property<GValue> nativeProperty(final Method getMethod, final Method setMethod) throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(getMethod, setMethod, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#compositeProperty(Producer, Consumer)
	 * Properties.compositeProperty(Producers.nativeProducer(getMethod, forceAccessible), Consumers.nativeConsumer(setMethod, forceAccessible))}.
	 *
	 * @see Producers#nativeProducer(Method, boolean)
	 * @see Consumers#nativeConsumer(Method, boolean) */
	public static <GValue> Property<GValue> nativeProperty(final Method getMethod, final Method setMethod, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.compositeProperty(Producers.<GValue>nativeProducer(getMethod, forceAccessible),
			Consumers.<GValue>nativeConsumer(setMethod, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(Class, String, boolean) Properties.nativeProperty(fieldOwner, fieldName, true)}. */
	public static <GValue> Property<GValue> nativeProperty(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(java.lang.reflect.Field) Properties.nativeProperty(Natives.parseField(fieldOwner,
	 * fieldName), forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GValue> Property<GValue> nativeProperty(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link Properties#translatedProperty(Getter, Getter, Property)
	 * Properties.translatedProperty(Translators.toTargetGetter(translator), Translators.toSourceGetter(translator), property)}.
	 *
	 * @see Translators#toTargetGetter(Translator)
	 * @see Translators#toSourceGetter(Translator) */
	public static <GSource, GTarget> Property<GTarget> translatedProperty(final Translator<GSource, GTarget> translator, final Property<GSource> property)
		throws NullPointerException {
		return new TranslatedProperty<>(property, translator);
	}

	/** Diese Methode gibt ein übersetztes {@link Property} zurück, welches die gegebenen {@link Getter} zum Parsen und Formatieren nutzt. Das erzeugte
	 * {@link Property} liefert beim Lesen den Wert, der mit Hilfe des gegebenen {@link Getter} {@code toTarget} aus dem über das gegebene {@link Property}
	 * ermittelten Wert berechnet wird. Beim Schreiben eines Werts wird dieser über den gegebenen {@link Getter} {@code toSource} in einen Wert überfüght, welcher
	 * anschließend an das gegebene {@link Property} delegiert wird.
	 *
	 * @see Producers#translatedProducer(Getter, Producer)
	 * @see Consumers#translatedConsumer(Getter, Consumer)
	 * @param <GTarget> Typ des Werts der erzeugten Eigenschaft.
	 * @param <GSource> Typ des Werts der gegebenen Eigenschaft.
	 * @param toTarget {@link Getter} zum Übersetzen des Wert beim Lesen.
	 * @param toSource {@link Getter} zum Übersetzen des Wert beim Schreiben.
	 * @param property Eigenschaft.
	 * @return {@code translated}-{@link Property}.
	 * @throws NullPointerException Wenn {@code property}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GSource, GTarget> Property<GTarget> translatedProperty(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GTarget, ? extends GSource> toSource, final Property<GSource> property) throws NullPointerException {
		return Properties.compositeProperty(Producers.translatedProducer(toTarget, property), Consumers.translatedConsumer(toSource, property));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableProperty new ObservableProperty<>(property)}. */
	public static <GValue> Property<GValue> observableProperty(final Property<GValue> property) throws NullPointerException {
		return new ObservableProperty<>(property);
	}

	/** Diese Methode ist eine Abkürzung für {@link #observableProperty(Property) Properties.observableProperty(Properties.compositeProperty(producer,
	 * consumer))}. */
	public static <GValue> Property<GValue> observableProperty(final Producer<? extends GValue> producer, final Consumer<? super GValue> consumer)
		throws NullPointerException {
		return Properties.observableProperty(Properties.compositeProperty(producer, consumer));
	}

	/** Diese Methode gibt ein zusammengesetztes {@link Property} zurück, dessen Methoden an die des gegebenen {@link Producer} und {@link Consumer} delegieren.
	 *
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param producer {@link Producer} für {@link Property#get()}.
	 * @param consumer {@link Consumer} für {@link Property#set( Object)}.
	 * @return {@code composite}-{@link Property}.
	 * @throws NullPointerException Wenn {@code property} bzw. {@code consumer} {@code null} ist. */
	public static <GValue> Property<GValue> compositeProperty(final Producer<? extends GValue> producer, final Consumer<? super GValue> consumer)
		throws NullPointerException {
		return new CompositeProperty<>(producer, consumer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#synchronizedProperty(Object, Property) Properties.synchronizedProperty(property, property)}. */
	public static <GValue> Property<GValue> synchronizedProperty(final Property<GValue> property) throws NullPointerException {
		return Properties.synchronizedProperty(property, property);
	}

	/** Diese Methode gibt ein {@link Property} zurück, welches das gegebene {@link Property} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird das erzeugte {@link Property} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param property {@link Property}.
	 * @return {@code synchronized}-{@link Property}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GValue> Property<GValue> synchronizedProperty(final Object mutex, final Property<GValue> property) throws NullPointerException {
		return new SynchronizedProperty<>(mutex, property);
	}

	/** Diese Methode gibt ein {@link Field} zurück, das seinen Datensatz ignoriert und den Wert des gegebenen {@link Property} manipuliert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param property {@link Property}.
	 * @return {@link Property}-{@link Field}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GValue> Field<Object, GValue> toField(final Property<GValue> property) {
		return new PropertyField<>(property);
	}

	/** Diese Methode gibt ein {@link Set} zurück, dessen Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxySet
	 * @param property {@link Property}.
	 * @return {@link Set}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GItem> Set<GItem> toSet(final Property<Set<GItem>> property) throws NullPointerException {
		return new PropertySet<>(property);
	}

	/** Diese Methode gibt eine {@link List} zurück, deren Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxyList
	 * @param property {@link Property}.
	 * @return {@link List}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GItem> List<GItem> toList(final Property<List<GItem>> property) throws NullPointerException {
		return new PropertyList<>(property);
	}

	/** Diese Methode gibt eine {@link Map} zurück, deren Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxyMap
	 * @param property {@link Property}.
	 * @return {@link Map}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GKey, GValue> Map<GKey, GValue> toMap(final Property<Map<GKey, GValue>> property) throws NullPointerException {
		return new PropertyMap<>(property);
	}

	/** Diese Methode gibt eine {@link Collection} zurück, deren Inhalt über das gegebene {@link Property} gelesen und geschrieben wird.
	 *
	 * @see AbstractProxyCollection
	 * @param property {@link Property}.
	 * @return {@link Collection}-{@code Proxy}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GItem> Collection<GItem> toCollection(final Property<Collection<GItem>> property) {
		return new PropertyCollection<>(property);
	}

}
