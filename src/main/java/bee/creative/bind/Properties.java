package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert gundlegende {@link Property}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
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

	/** Diese Klasse implementiert ein initialisierendes {@link Property2}, welches das {@link #set(Object) Schreiben} an ein gegebenes {@link Property} delegiert
	 * und den Wert beim {@link #get() Lesen} über das gegebene {@link Property} ermittelt. Wenn dieser Wert {@code null} ist, wird er initialisiert, d.h. mit
	 * Hilfe eines gegebenen {@link Producer} ermittelt und über das gegebene {@link Property} geschrieben.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class SetupProperty<GValue> extends AbstractProperty<GValue> {

		public final Property<GValue> target;

		public final Producer<? extends GValue> setup;

		/** Dieser Konstruktor initialisiert Eigenschaft und Initialisierung.
		 *
		 * @param target Eigenschaft.
		 * @param setup Initialisierung.
		 * @throws NullPointerException Wenn {@code target} bzw. {@code setup} {@code null} ist. */
		public SetupProperty(final Property<GValue> target, final Producer<? extends GValue> setup) throws NullPointerException {
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

		/** Dieser Konstruktor initialisiert Lesen- und Schreibmethode.
		 *
		 * @param get Lesenmethode.
		 * @param set Schreibmethode.
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

	/** Diese Klasse implementiert ein übersetztes {@link Property2}, welches einen gegebnenen {@link Translator} zur Übersetzung des Werts beim {@link #get()
	 * Lesen} bzw. {@link #set(Object) Schreiben} nutzt. Beim Lesen wird {@code this.trans.toSource(this.target.get())} geliefert. Das Schreiben erfolgt über
	 * {@code this.target.set(this.trans.toTarget(value))}.
	 *
	 * @param <GValue> Typ des Werts dieser Eigenschaft.
	 * @param <GValue2> Typ des Werts der gegebenen Eigenschaft. */
	public static class TranslatedProperty<GValue, GValue2> extends AbstractProperty<GValue> {

		public final Property<GValue2> target;

		public final Translator<GValue, GValue2> trans;

		public TranslatedProperty(final Property<GValue2> target, final Translator<GValue, GValue2> trans) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public GValue get() {
			return this.trans.toSource(this.target.get());
		}

		@Override
		public void set(final GValue value) {
			this.target.set(this.trans.toTarget(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans);
		}

	}

	/** Diese Klasse implementiert eine {@link Observable} {@link Property2}.
	 *
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class ObservableProperty<GValue> extends AbstractProperty<GValue> implements Observable<UpdatePropertyMessage, UpdatePropertyObserver> {

		/** Dieses Feld speichert die Eigenschaft, an die in {@link #get()} und {@link #set(Object)} delegiert wird. */
		public final Property<GValue> target;

		/** Dieser Konstruktor initialisiert die überwachte Eigenschaft.
		 *
		 * @param target überwachte Eigenschaft. */
		public ObservableProperty(final Property<GValue> target) {
			this.target = Objects.notNull(target);
		}

		/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese
		 * Methode eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann
		 * nötig, wenn der alte Wert sich durch das Schreiben des neuen ändert.
		 *
		 * @param value alter Wert.
		 * @return gegebener oder kopierter Wert. */
		protected GValue customClone(final GValue value) {
			return value;
		}

		/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
		 * Wertänderung eingesetzt.
		 *
		 * @param value1 alter Wert.
		 * @param value2 neuer Wert.
		 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
		protected boolean customEquals(final GValue value1, final GValue value2) {
			return Objects.deepEquals(value1, value2);
		}

		@Override
		public GValue get() {
			return this.target.get();
		}

		@Override
		public void set(final GValue newValue) {
			GValue oldValue = this.target.get();
			if (this.customEquals(oldValue, newValue)) return;
			oldValue = this.customClone(oldValue);
			this.target.set(newValue);
			this.fire(new UpdatePropertyMessage(this, oldValue, newValue));
		}

		@Override
		public UpdatePropertyObserver put(final UpdatePropertyObserver listener) throws IllegalArgumentException {
			return UpdatePropertyEvent.INSTANCE.put(this, listener);
		}

		@Override
		public UpdatePropertyObserver putWeak(final UpdatePropertyObserver listener) throws IllegalArgumentException {
			return UpdatePropertyEvent.INSTANCE.putWeak(this, listener);
		}

		@Override
		public void pop(final UpdatePropertyObserver listener) throws IllegalArgumentException {
			UpdatePropertyEvent.INSTANCE.pop(this, listener);
		}

		@Override
		public UpdatePropertyMessage fire(final UpdatePropertyMessage event) throws NullPointerException {
			return UpdatePropertyEvent.INSTANCE.fire(this, event);
		}

		@Override
		public String toString() {
			return this.target.toString();
		}

	}

	/** Diese Klasse implementiert ein {@link Property2}, welches ein gegebenes {@link Property} über {@code synchronized(this.mutex)} synchronisiert.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class SynchronizedProperty<GValue> extends AbstractProperty<GValue> {

		public final Property<GValue> target;

		public final Object mutex;

		/** Dieser Konstruktor initialisiert Eigenschaft und Synchronisationsobjekt. Wenn das Synchronisationsobjekt {@code null} ist, wird {@code this} als
		 * Synchronisationsobjekt verwendet.
		 *
		 * @param target Eigenschaft.
		 * @param mutex Synchronisationsobjekt oder {@code null}.
		 * @throws NullPointerException Wenn {@code target} {@code null} ist. */
		public SynchronizedProperty(final Property<GValue> target, final Object mutex) throws NullPointerException {
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
	public static <GValue> Property2<GValue> empty() {
		return (Property2<GValue>)EmptyProperty.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Field, Object) Fields.toProperty(null, field)}. */
	public static <GItem, GValue> Property2<GValue> from(final Field<? super GItem, GValue> target) throws NullPointerException {
		return Properties.from(target, null);
	}

	public static <GItem, GValue> Property2<GValue> from(final Field<? super GItem, GValue> target, final GItem item) throws NullPointerException {
		return Properties.concat(Producers.fromValue(item), target);
	}

	/** Diese Methode gibt das gegebene {@link Property} als {@link Property2} zurück. Wenn es {@code null} ist, wird {@link #empty()} geliefert. */
	public static <GValue> Property2<GValue> from(final Property<GValue> target) {
		if (target == null) return Properties.empty();
		if (target instanceof Consumer3) return (Property2<GValue>)target;
		return Properties.toTranslated(target, Getters.<GValue>neutral(), Getters.<GValue>neutral());
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

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(Method, Method, boolean) Properties.fromNative(get, set, true)}. **/
	public static <GValue> Property<GValue> fromNative(final Method get, final Method set) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(get, set, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer, Consumer) Properties.from(Producers.fromNative(get, forceAccessible),
	 * Consumers.fromNative(set, forceAccessible))}.
	 *
	 * @see Producers#fromNative(Method, boolean)
	 * @see Consumers#fromNative(Method, boolean) */
	public static <GValue> Property<GValue> fromNative(final Method get, final Method set, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.from(Producers.<GValue>fromNative(get, forceAccessible), Consumers.<GValue>fromNative(set, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(Class, String, boolean) Properties.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Property<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field) Properties.fromNative(Natives.parseField(fieldOwner,
	 * fieldName), forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GValue> Property<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	public static <GItem, GValue> Property2<GValue> concat(final Producer<? extends GItem> source, final Field<? super GItem, GValue> target)
		throws NullPointerException {
		return Properties.from(Producers.concat(source, target), Consumers.concat(source, target));
	}

	/** Diese Methode ist eine Abkürzung für {@link SetupProperty new SetupProperty<>(target, setup)}. */
	public static <GValue> Property2<GValue> toSetup(final Property<GValue> target, final Producer<? extends GValue> setup) throws NullPointerException {
		return new SetupProperty<>(target, setup);
	}

	public static <GValue, GValue2> Property2<GValue> toTranslated(final Property<GValue2> target, final Translator<GValue2, GValue> trans)
		throws NullPointerException {
		return toTranslated(target, Getters.fromTarget(trans), Getters.fromSource(trans));
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#from(Producer, Consumer) Properties.from(Producers.toTranslated(target, transGet),
	 * Consumers.toTranslated(target, transSet))}.
	 *
	 * @see Producers#concat(Producer, Getter)
	 * @see Consumers#toTranslated(Consumer, Getter) */
	public static <GSource, GTarget> Property2<GTarget> toTranslated(final Property<GSource> target, final Getter<? super GSource, ? extends GTarget> transGet,
		final Getter<? super GTarget, ? extends GSource> transSet) throws NullPointerException {
		return Properties.from(Producers.concat(target, transGet), Consumers.toTranslated(target, transSet));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableProperty new ObservableProperty<>(property)}. */
	public static <GValue> ObservableProperty<GValue> toObservable(final Property<GValue> property) throws NullPointerException {
		return new ObservableProperty<>(property);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#toSynchronized(Property, Object) Properties.toSynchronized(target, target)}. */
	public static <GValue> Property2<GValue> toSynchronized(final Property<GValue> target) throws NullPointerException {
		return Properties.toSynchronized(target, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedProperty new SynchronizedProperty<>(target, mutex)}. */
	public static <GValue> Property2<GValue> toSynchronized(final Property<GValue> target, final Object mutex) throws NullPointerException {
		return new SynchronizedProperty<>(target, mutex);
	}

}
