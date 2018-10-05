package bee.creative.util;

import java.lang.reflect.Modifier;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Property}-Instanzen.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Properties {

	/** Diese Klasse implementiert ein abstraktes {@link Property} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
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

	/** Diese Klasse implementiert {@link Properties#nativeProperty(java.lang.reflect.Field)}. */
	@SuppressWarnings ("javadoc")
	public static class NativeProperty<GValue> extends BaseProperty<GValue> {

		public final java.lang.reflect.Field field;

		public NativeProperty(final java.lang.reflect.Field field) {
			if (!Modifier.isStatic(field.getModifiers())) throw new IllegalArgumentException();
			try {
				field.setAccessible(true);
			} catch (final SecurityException cause) {
				throw new IllegalArgumentException(cause);
			}
			this.field = field;
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
			return Objects.toInvokeString(this, Natives.formatField(this.field));
		}

	}

	/** Diese Klasse implementiert {@link Properties#setupProperty(Property, Producer)}. */
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

	/** Diese Klasse implementiert {@link Properties#translatedProperty(Property, Translator)}. */
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

	/** Diese Methode gibt das leere {@link Property} zurück, das stets {@code null} liefert und das Schreiben ignoriert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @return {@code empty}-{@link Property}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Property<GValue> emptyProperty() {
		return (Property<GValue>)ValueProperty.EMPTY;
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #compositeProperty(Producer, Consumer)
	 * Properties.compositeProperty(Producers.valueProducer(value), Consumers.emptyConsumer())}.
	 *
	 * @see Producers#valueProducer(Object)
	 * @see Consumers#emptyConsumer() */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> valueProperty(final GValue value) {
		if (value == null) return Properties.emptyProperty();
		return new ValueProperty<>(value);
	}

	/** Diese Methode gibt ein initialisierendes {@link Property} zurück.<br>
	 * Das Schreiben wird direkt an das gegebene {@link Property} {@code property} delegiert. Beim Lesen wird der Wert zuerst über das gegebene {@link Property}
	 * ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. gemäß der gegebenen {@link Producer Initialisierung} {@code setup} ermittelt,
	 * über das {@link Property} {@code property} geschrieben und zurückgegeben. Andernfalls wird der Wertt er direkt zurückgegeben.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param property Eigenschaft zur Manipulation.
	 * @param setup Methode zur Initialisierung.
	 * @return {@code setup}-{@link Property}.
	 * @throws NullPointerException Wenn {@code setup} bzw. {@code property} {@code null} ist. */
	public static <GValue> Property<GValue> setupProperty(final Property<GValue> property, final Producer<? extends GValue> setup) throws NullPointerException {
		return new SetupProperty<>(setup, property);
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProperty(java.lang.reflect.Field) Properties.nativeProperty(Natives.parseField(fieldText))}.
	 *
	 * @see Natives#parseField(String) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> nativeProperty(final String fieldText) throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(Natives.parseField(fieldText));
	}

	/** Diese Methode ist eine Abkürzung für {@link #nativeProperty(java.lang.reflect.Field) Properties.nativeProperty(Natives.parseField(fieldOwner,
	 * fieldName))}.
	 *
	 * @see Natives#parseField(Class, String) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> nativeProperty(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(Natives.parseField(fieldOwner, fieldName));
	}

	/** Diese Methode gibt ein {@link Property} zum gegebenen {@link java.lang.reflect.Field nativen statischen Datenfeld} zurück.<br>
	 * Das Lesen des gelieferten {@link Property} erfolgt über {@code field.get(null)}. Das Schreiben eines Werts {@code value} erfolgt dazu über
	 * {@code field.set(null, value)}.
	 *
	 * @see java.lang.reflect.Field#get(Object)
	 * @see java.lang.reflect.Field#set(Object, Object)
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param field Eigenschaft.
	 * @return {@code native}-{@link Property}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 * @throws IllegalArgumentException Wenn das native Datenfeld nicht zugrifbar oder nicht statisch ist. */
	public static <GValue> Property<GValue> nativeProperty(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return new NativeProperty<>(field);
	}

	/** Diese Methode ist eine Abkürzung für
	 * {@code Properties.compositeProperty(Producers.nativeProducer(getMemberText), Consumers.nativeConsumer(setMemberText))}.
	 *
	 * @see #compositeProperty(Producer, Consumer)
	 * @see Getters#nativeGetter(String)
	 * @see Setters#nativeSetter(String) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> nativeProperty(final String getMemberText, final String setMemberText)
		throws NullPointerException, IllegalArgumentException {
		return Properties.compositeProperty(Producers.<GValue>nativeProducer(getMemberText), Consumers.<GValue>nativeConsumer(setMemberText));
	}

	/** Diese Methode ist eine Abkürzung für {@code Properties.compositeProperty(Producers.nativeProducer(getMethod), Consumers.nativeConsumer(setMethod))}.
	 *
	 * @see #compositeProperty(Producer, Consumer)
	 * @see Producers#nativeProducer(java.lang.reflect.Method)
	 * @see Consumers#nativeConsumer(java.lang.reflect.Method) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> nativeProperty(final java.lang.reflect.Method getMethod, final java.lang.reflect.Method setMethod)
		throws NullPointerException, IllegalArgumentException {
		return Properties.compositeProperty(Producers.<GValue>nativeProducer(getMethod), Consumers.<GValue>nativeConsumer(setMethod));
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link #translatedProperty(Property, Getter, Getter) Properties.translatedProperty(property,
	 * Translators.toTargetGetter(translator), Translators.toSourceGetter(translator))}.
	 *
	 * @see Translators#toTargetGetter(Translator)
	 * @see Translators#toSourceGetter(Translator) */
	@SuppressWarnings ("javadoc")
	public static <GSource, GTarget> Property<GTarget> translatedProperty(final Property<GSource> property, final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return new TranslatedProperty<>(property, translator);
	}

	/** Diese Methode gibt ein übersetztes {@link Property} zurück, welches die gegebenen {@link Getter} zum Parsen und Formatieren nutzt.
	 * <p>
	 * Das erzeugte {@link Property} liefert beim Lesen den (externen) Wert, der gemäß dem gegebenen {@link Getter Leseformat} {@code getFormat} aus dem über das
	 * gegebene {@link Property Datenfeld} ermittelten (internen) Wert berechnet wird. Beim Schreiben eines (externen) Werts wird dieser gemäß dem gegebenen
	 * {@link Getter Schreibformat} {@code setFormat} in einen (internen) Wert überfüght, welcher anschließend an das gegebene {@link Property Datenfeld}
	 * delegiert wird.
	 *
	 * @see Producers#translatedProducer(Getter, Producer)
	 * @see Consumers#translatedConsumer(Getter, Consumer)
	 * @param <GTarget> Typ des Werts der erzeugten Eigenschaft.
	 * @param <GSource> Typ des Werts der gegebenen Eigenschaft.
	 * @param property Eigenschaft.
	 * @param toTarget {@link Getter} zum Umwandeln des internen in den externen Wert zum Lesen.
	 * @param toSource {@link Getter} zum Umwandeln des externen in den internen Wert zum Schreiben.
	 * @return {@code translated}-{@link Property}.
	 * @throws NullPointerException Wenn {@code property}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GSource, GTarget> Property<GTarget> translatedProperty(final Property<GSource> property,
		final Getter<? super GSource, ? extends GTarget> toTarget, final Getter<? super GTarget, ? extends GSource> toSource) throws NullPointerException {
		return Properties.compositeProperty(Producers.translatedProducer(toTarget, property), Consumers.translatedConsumer(toSource, property));
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

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedProperty(Object, Property) Properties.synchronizedProperty(property, property)}. */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> synchronizedProperty(final Property<GValue> property) throws NullPointerException {
		return Properties.synchronizedProperty(property, property);
	}

	/** Diese Methode gibt ein {@link Property} zurück, welches das gegebene {@link Property} via {@code synchronized(mutex)} synchronisiert. Wenn das
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

}
