package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert gundlegende {@link Property}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class Properties {

	/** Diese Klasse implementiert ein {@link Property2}, welches das {@link #set(Object) Schreiben} ignoriert und beim {@link #get() Lesen} stets {@code null}
	 * liefert. */
	public static class EmptyProperty extends AbstractProperty<Object> {

		public static final Property2<?> INSTANCE = new EmptyProperty();

	}

	/** Diese Klasse implementiert ein {@link Property2}, dessen {@link #value Wert} {@link #get() gelesen} und {@link #set(Object) geschrieben} werden kann.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class ValueProperty<GValue> extends AbstractProperty<GValue> {

		public GValue value;

		/** Dieser Konstruktor initialisiert den Wert.
		 *
		 * @param value Wert. */
		public ValueProperty(final GValue value) {
			this.value = value;
		}

		@Override
		public GValue get() {
			return this.value;
		}

		@Override
		public void set(final GValue value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toSetup(Property, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class SetupProperty<GValue> extends AbstractProperty<GValue> {

		public final Property<GValue> target;

		public final Producer<? extends GValue> setup;

		public SetupProperty(final Property<GValue> target, final Producer<? extends GValue> setup) {
			this.target = Objects.notNull(target);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public GValue get() {
			GValue result = this.target.get();
			if (result != null) return result;
			result = this.setup.get();
			this.target.set(result);
			return result;
		}

		@Override
		public void set(final GValue value) {
			this.target.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.setup);
		}

	}

	/** Diese Klasse implementiert ein {@link Property2}, welches das {@link #get() Lesen} und {@link #set(Object) Schreiben} an ein gegebenes
	 * {@link java.lang.reflect.Field natives statisches Datenfeld} delegiert. *
	 *
	 * @param <GValue> Typ des Werts. */
	public static class NativeProperty<GValue> extends AbstractProperty<GValue> {

		public final java.lang.reflect.Field target;

		/** Dieser Konstruktor initialisiert das Datenfeld und Zugreifbarkeit.
		 * 
		 * @param target natives statisches Datenfeld.
		 * @param forceAccessible {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}.
		 * @throws NullPointerException Wenn {@code target} {@code null} ist.
		 * @throws IllegalArgumentException Wenn das native Datenfeld nicht statisch bzw. nicht zugrifbar ist. */
		public NativeProperty(final java.lang.reflect.Field target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(target.getModifiers())) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.target.get(null);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GValue value) {
			try {
				this.target.set(null, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	/** Diese Klasse implementiert ein zusammengesetztes {@link Property2}, welches das {@link #get() Lesen} an einen gegebenen {@link Producer} und das
	 * {@link #set(Object) Schreiben} an einen gegebenen {@link Consumer} delegiert.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class CompositeProperty<GValue> extends AbstractProperty<GValue> {

		public final Producer<? extends GValue> get;

		public final Consumer<? super GValue> set;

		/** Dieser Konstruktor initialisiert {@link Producer} und {@link Consumer}.
		 *
		 * @param get {@link Producer} zum Lesen.
		 * @param set {@link Consumer} zum Schreiben.
		 * @throws NullPointerException Wenn {@code get} bzw. {@code set} {@code null} ist. */
		public CompositeProperty(final Producer<? extends GValue> get, final Consumer<? super GValue> set) throws NullPointerException {
			this.get = Objects.notNull(get);
			this.set = Objects.notNull(set);
		}

		@Override
		public GValue get() {
			return this.get.get();
		}

		@Override
		public void set(final GValue value) {
			this.set.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.get, this.set);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toTranslated(Property, Translator)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedProperty<GTarget, GSource> extends AbstractProperty<GTarget> {

		public final Property<GSource> target;

		public final Translator<GSource, GTarget> translator;

		public TranslatedProperty(final Property<GSource> target, final Translator<GSource, GTarget> translator) {
			this.target = Objects.notNull(target);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public GTarget get() {
			return this.translator.toTarget(this.target.get());
		}

		@Override
		public void set(final GTarget value) {
			this.target.set(this.translator.toSource(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.translator);
		}

	}

	/** Diese Klasse implementiert {@link Properties#toSynchronized(Property, Object)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProperty<GValue> extends AbstractProperty<GValue> {

		public final Property<GValue> target;

		public final Object mutex;

		public SynchronizedProperty(final Property<GValue> target, final Object mutex) {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get() {
			synchronized (this.mutex) {
				return this.target.get();
			}
		}

		@Override
		public void set(final GValue value) {
			synchronized (this.mutex) {
				this.target.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
		}

	}

	/** Diese Methode liefert {@link EmptyProperty#INSTANCE}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Property<GValue> empty() {
		return (Property<GValue>)EmptyProperty.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(Properties.empty(), set)}. */
	public static <GValue> Property2<GValue> from(final Consumer<? super GValue> set) throws NullPointerException {
		return Properties.from(Properties.<GValue>empty(), set);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(get, Properties.empty())}. */
	public static <GValue> Property2<GValue> from(final Producer<? extends GValue> get) throws NullPointerException {
		return Properties.from(get, Properties.<GValue>empty());
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeProperty new CompositeProperty<>(get, set)}. */
	public static <GValue> Property2<GValue> from(final Producer<? extends GValue> get, final Consumer<? super GValue> set) throws NullPointerException {
		return new CompositeProperty<>(get, set);
	}

	/** Diese Methode ist eine Abkürzung für {@link ValueProperty new ValueProperty<>(value)}. */
	public static <GValue> Property<GValue> fromValue(final GValue value) {
		return new ValueProperty<>(value);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(String, boolean) Properties.fromNative(fieldPath, true)}. */
	public static <GValue> Property<GValue> fromNative(final String fieldPath) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(fieldPath, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field) Properties.fromNative(Natives.parseField(fieldPath),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseField(String) */
	public static <GValue> Property<GValue> fromNative(final String fieldPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(Natives.parseField(fieldPath), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field, boolean) Properties.fromNative(field, true)}. */
	public static <GValue> Property<GValue> fromNative(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link NativeProperty new NativeProperty<>(field, forceAccessible)}. */
	public static <GValue> Property<GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeProperty<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(Method, Method, boolean) Properties.compositeProperty(get, set, true)}. **/
	public static <GValue> Property<GValue> nativeProperty(final Method get, final Method set) throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(get, set, true);
	}

	int TODO;

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer, Consumer) Properties.compositeProperty(Producers.nativeProducer(getMethod,
	 * forceAccessible), Consumers.nativeConsumer(setMethod, forceAccessible))}.
	 *
	 * @see Producers#fromNative(Method, boolean)
	 * @see Consumers#fromNative(Method, boolean) */
	public static <GValue> Property<GValue> nativeProperty(final Method getMethod, final Method setMethod, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.from(Producers.<GValue>fromNative(getMethod, forceAccessible), Consumers.<GValue>fromNative(setMethod, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(Class, String, boolean) Properties.nativeProperty(fieldOwner, fieldName, true)}. */
	public static <GValue> Property<GValue> nativeProperty(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field) Properties.nativeProperty(Natives.parseField(fieldOwner,
	 * fieldName), forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GValue> Property<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode gibt ein initialisierendes {@link Property} zurück. Das Schreiben wird direkt an das gegebene {@link Property} delegiert. Beim Lesen wird
	 * der Wert zuerst über das gegebene {@link Property} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. mit Hilfe des gegebenen
	 * {@link Producer} ermittelt und über das {@link Property} geschrieben.
	 *
	 * @param target Eigenschaft zur Manipulation.
	 * @param setup Methode zur Initialisierung.
	 * @param <GValue> Typ des Werts.
	 * @return {@code setup}-{@link Property}.
	 * @throws NullPointerException Wenn {@code setup} bzw. {@code property} {@code null} ist. */
	public static <GValue> Property2<GValue> toSetup(final Property<GValue> target, final Producer<? extends GValue> setup) throws NullPointerException {
		return new SetupProperty<>(target, setup);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@link Properties#toTranslated(Property, Getter, Getter)
	 * Properties.translatedProperty(Translators.toTargetGetter(translator), Translators.toSourceGetter(translator), property)}.
	 *
	 * @see Translators#toTargetGetter(Translator)
	 * @see Translators#toSourceGetter(Translator) */
	public static <GSource, GTarget> Property2<GTarget> toTranslated(final Property<GSource> target, final Translator<GSource, GTarget> trans)
		throws NullPointerException {
		return new TranslatedProperty<>(target, trans);
	}

	/** Diese Methode gibt ein übersetztes {@link Property} zurück, welches die gegebenen {@link Getter} zum Parsen und Formatieren nutzt. Das erzeugte
	 * {@link Property} liefert beim Lesen den Wert, der mit Hilfe des gegebenen {@link Getter} {@code toTarget} aus dem über das gegebene {@link Property}
	 * ermittelten Wert berechnet wird. Beim Schreiben eines Werts wird dieser über den gegebenen {@link Getter} {@code toSource} in einen Wert überfüght, welcher
	 * anschließend an das gegebene {@link Property} delegiert wird.
	 *
	 * @param target Eigenschaft.
	 * @param transGet {@link Getter} zum Übersetzen des Wert beim Lesen.
	 * @param transSet {@link Getter} zum Übersetzen des Wert beim Schreiben.
	 * @see Producers#toTranslated(Producer, Getter)
	 * @see Consumers#toTranslated(Consumer, Getter)
	 * @param <GTarget> Typ des Werts der erzeugten Eigenschaft.
	 * @param <GSource> Typ des Werts der gegebenen Eigenschaft.
	 * @return {@code translated}-{@link Property}.
	 * @throws NullPointerException Wenn {@code property}, {@code toTarget} bzw. {@code toSource} {@code null} ist. */
	public static <GSource, GTarget> Property<GTarget> toTranslated(final Property<GSource> target, final Getter<? super GSource, ? extends GTarget> transGet,
		final Getter<? super GTarget, ? extends GSource> transSet) throws NullPointerException {
		return Properties.from(Producers.toTranslated(target, transGet), Consumers.toTranslated(target, transSet));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableProperty new ObservableProperty<>(property)}. */
	public static <GValue> Property2<GValue> toObservable(final Property<GValue> property) throws NullPointerException {
		return new ObservableProperty<>(property);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#toSynchronized(Property, Object) Properties.synchronizedProperty(property, property)}. */
	public static <GValue> Property2<GValue> toSynchronized(final Property<GValue> property) throws NullPointerException {
		return Properties.toSynchronized(property, property);
	}

	/** Diese Methode gibt ein {@link Property} zurück, welches das gegebene {@link Property} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird das erzeugte {@link Property} als Synchronisationsobjekt verwendet.
	 *
	 * @param property {@link Property}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @return {@code synchronized}-{@link Property}.
	 * @throws NullPointerException Wenn {@code property} {@code null} ist. */
	public static <GValue> Property2<GValue> toSynchronized(final Property<GValue> property, final Object mutex) throws NullPointerException {
		return new SynchronizedProperty<>(property, mutex);
	}

}
