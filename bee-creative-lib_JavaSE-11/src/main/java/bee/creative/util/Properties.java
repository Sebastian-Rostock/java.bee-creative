package bee.creative.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert gundlegende {@link Property}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Properties {

	/** Diese Klasse implementiert ein {@link Property2}, das das {@link #set(Object) Schreiben} ignoriert und beim {@link #get() Lesen} stets {@code null}
	 * liefert. */
	public static class EmptyProperty extends AbstractProperty<Object> {

		public static final Property2<?> INSTANCE = new EmptyProperty();

	}

	/** Diese Klasse implementiert ein {@link Property2}, das einen {@link #value Wert} verwaltet, der {@link #get() gelesen} und {@link #set(Object) geschrieben}
	 * werden kann.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class ValueProperty<GValue> extends AbstractProperty<GValue> {

		public GValue value;

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

	/** Diese Klasse implementiert ein initialisierendes {@link Property2}, das das {@link #get() Lesen} und {@link #set(Object) Schreiben} an ein gegebenes
	 * {@link Property} delegiert. Wenn der gelesene Wert {@code null} ist, wird er initialisiert, d.h. mit Hilfe eines gegebenen {@link Producer} ermittelt und
	 * über das gegebene {@link Property} geschrieben.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class SetupProperty<GValue> extends AbstractProperty<GValue> {

		public final Property<GValue> that;

		public final Producer<? extends GValue> setup;

		public SetupProperty(final Property<GValue> that, final Producer<? extends GValue> setup) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public GValue get() {
			GValue result = this.that.get();
			if (result != null) return result;
			result = this.setup.get();
			this.that.set(result);
			return result;
		}

		@Override
		public void set(final GValue value) {
			this.that.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.setup);
		}

	}

	/** Diese Klasse implementiert ein {@link Property2}, das das {@link #get() Lesen} und {@link #set(Object) Schreiben} an ein gegebenes
	 * {@link java.lang.reflect.Field natives statisches Datenfeld} delegiert. *
	 *
	 * @param <GValue> Typ des Werts. */
	public static class NativeProperty<GValue> extends AbstractProperty<GValue> {

		public final java.lang.reflect.Field that;

		public final boolean forceAccessible;

		/** Dieser Konstruktor initialisiert das Datenfeld und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public NativeProperty(final java.lang.reflect.Field that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (!Modifier.isStatic(that.getModifiers())) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.that.get(null);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public void set(final GValue value) {
			try {
				this.that.set(null, value);
			} catch (final IllegalAccessException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert ein zusammengesetztes {@link Property2}, das das {@link #get() Lesen} an einen gegebenen {@link Producer} und das
	 * {@link #set(Object) Schreiben} an einen gegebenen {@link Consumer} delegiert.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class CompositeProperty<GValue> extends AbstractProperty<GValue> {

		public final Producer<? extends GValue> get;

		public final Consumer<? super GValue> set;

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

	/** Diese Klasse implementiert eine {@link Observable} {@link Property2}, das das {@link #get() Lesen} und {@link #set(Object) Schreiben} an ein gegebenes
	 * {@link Property} delegiert und beim ändernden Schreiben ein {@link UpdatePropertyListener Änderungsereignis} auslöst.
	 *
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class ObservableProperty<GValue> extends AbstractProperty<GValue> implements Observable<UpdatePropertyEvent, UpdatePropertyListener> {

		/** Dieses Feld speichert die Eigenschaft, an die in {@link #get()} und {@link #set(Object)} delegiert wird. */
		public final Property<GValue> that;

		/** Dieser Konstruktor initialisiert die überwachte Eigenschaft. */
		public ObservableProperty(final Property<GValue> that) {
			this.that = Objects.notNull(that);
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
			return this.that.get();
		}

		@Override
		public void set(final GValue newValue) {
			GValue oldValue = this.that.get();
			if (this.customEquals(oldValue, newValue)) return;
			oldValue = this.customClone(oldValue);
			this.that.set(newValue);
			this.fire(new UpdatePropertyEvent(this, oldValue, newValue));
		}

		@Override
		public UpdatePropertyListener put(final UpdatePropertyListener listener) throws IllegalArgumentException {
			return UpdatePropertyObservables.INSTANCE.put(this, listener);
		}

		@Override
		public UpdatePropertyListener putWeak(final UpdatePropertyListener listener) throws IllegalArgumentException {
			return UpdatePropertyObservables.INSTANCE.putWeak(this, listener);
		}

		@Override
		public void pop(final UpdatePropertyListener listener) throws IllegalArgumentException {
			UpdatePropertyObservables.INSTANCE.pop(this, listener);
		}

		@Override
		public UpdatePropertyEvent fire(final UpdatePropertyEvent event) throws NullPointerException {
			return UpdatePropertyObservables.INSTANCE.fire(this, event);
		}

		@Override
		public String toString() {
			return this.that.toString();
		}

	}

	/** Diese Klasse implementiert ein {@link Property2}, das ein gegebenes {@link Property} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class SynchronizedProperty<GValue> extends AbstractProperty<GValue> {

		public final Property<GValue> that;

		public final Object mutex;

		public SynchronizedProperty(final Property<GValue> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get() {
			synchronized (this.mutex) {
				return this.that.get();
			}
		}

		@Override
		public void set(final GValue value) {
			synchronized (this.mutex) {
				this.that.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	/** Diese Methode liefert das {@link EmptyProperty}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Property2<GValue> empty() {
		return (Property2<GValue>)EmptyProperty.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(Producers.translate(item, that), Consumers.from(item, that))}.
	 *
	 * @see Producers#translate(Producer, Getter)
	 * @see Consumers#from(Producer, Setter) */
	public static <GItem, GValue> Property2<GValue> from(final Producer<? extends GItem> item, final Field<? super GItem, GValue> that)
		throws NullPointerException {
		return Properties.from(Producers.translate(item, that), Consumers.from(item, that));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Field, Object) Fields.from(that, null)}. */
	public static <GItem, GValue> Property2<GValue> from(final Field<? super GItem, GValue> that) throws NullPointerException {
		return Properties.from(that, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Field) Properties.from(Producers.fromValue(item), that)}.
	 *
	 * @see Producers#fromValue(Object) */
	public static <GItem, GValue> Property2<GValue> from(final Field<? super GItem, GValue> that, final GItem item) throws NullPointerException {
		return Properties.from(Producers.fromValue(item), that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(Properties.empty(), set)}.
	 *
	 * @see #empty() */
	public static <GValue> Property2<GValue> from(final Consumer<? super GValue> set) throws NullPointerException {
		return Properties.from(Properties.<GValue>empty(), set);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(get, Properties.empty())}.
	 *
	 * @see #empty() */
	public static <GValue> Property2<GValue> from(final Producer<? extends GValue> get) throws NullPointerException {
		return Properties.from(get, Properties.<GValue>empty());
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeProperty new CompositeProperty<>(get, set)}. */
	public static <GValue> Property2<GValue> from(final Producer<? extends GValue> get, final Consumer<? super GValue> set) throws NullPointerException {
		return new CompositeProperty<>(get, set);
	}

	/** Diese Methode liefert das gegebene {@link Property} als {@link Property2}. Wenn es {@code null} ist, wird das {@link EmptyProperty} geliefert. */
	public static <GValue> Property2<GValue> from(final Property<GValue> that) {
		if (that == null) return Properties.empty();
		if (that instanceof Property2<?>) return (Property2<GValue>)that;
		return Properties.translate(that, Getters.<GValue>neutralGetter(), Getters.<GValue>neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link ValueProperty new ValueProperty<>(value)}. */
	public static <GValue> Property2<GValue> propertyFromValue(final GValue value) {
		return new ValueProperty<>(value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(String, boolean) Properties.fromNative(fieldPath, true)}. */
	public static <GValue> Property2<GValue> fromNative(final String fieldPath) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(fieldPath, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field) Properties.fromNative(Natives.parseField(fieldPath), forceAccessible)}.
	 *
	 * @see Natives#parseField(String) */
	public static <GValue> Property2<GValue> fromNative(final String fieldPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(Natives.parseField(fieldPath), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Properties.fromNative(field, true)}. */
	public static <GValue> Property2<GValue> fromNative(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link NativeProperty new NativeProperty<>(field, forceAccessible)}. */
	public static <GValue> Property2<GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeProperty<>(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, Method, boolean) Properties.fromNative(get, set, true)}. **/
	public static <GValue> Property2<GValue> fromNative(final Method get, final Method set) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(get, set, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(Producers.fromNative(get, forceAccessible),
	 * Consumers.fromNative(set, forceAccessible))}.
	 *
	 * @see Producers#fromNative(Method, boolean)
	 * @see Consumers#fromNative(Method, boolean) */
	public static <GValue> Property2<GValue> fromNative(final Method get, final Method set, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.from(Producers.<GValue>fromNative(get, forceAccessible), Consumers.<GValue>fromNative(set, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Properties.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Property2<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Properties.fromNative(Natives.parseField(fieldOwner, fieldName),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseField(Class, String) */
	public static <GValue> Property2<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.fromNative(Natives.parseField(fieldOwner, fieldName), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link SetupProperty new SetupProperty<>(that, setup)}. */
	public static <GValue> Property2<GValue> setup(final Property<GValue> that, final Producer<? extends GValue> setup) throws NullPointerException {
		return new SetupProperty<>(that, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link #translate(Property, Getter, Getter) Properties.translate(that, Getters.fromTarget(trans),
	 * Getters.fromSource(trans))}.
	 *
	 * @see Getters#fromTarget(Translator)
	 * @see Getters#fromSource(Translator) */
	public static <GValue, GValue2> Property2<GValue> translate(final Property<GValue2> that, final Translator<GValue2, GValue> trans)
		throws NullPointerException {
		return Properties.translate(that, Getters.fromTarget(trans), Getters.fromSource(trans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Consumer) Properties.from(Producers.translate(that, transGet), Consumers.translate(that,
	 * transSet))}.
	 *
	 * @see Producers#translate(Producer, Getter)
	 * @see Consumers#translate(Consumer, Getter) */
	public static <GSource, GTarget> Property2<GTarget> translate(final Property<GSource> that, final Getter<? super GSource, ? extends GTarget> transGet,
		final Getter<? super GTarget, ? extends GSource> transSet) throws NullPointerException {
		return Properties.from(Producers.translate(that, transGet), Consumers.translate(that, transSet));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableProperty new ObservableProperty<>(property)}. */
	public static <GValue> ObservableProperty<GValue> observe(final Property<GValue> property) throws NullPointerException {
		return new ObservableProperty<>(property);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Property, Object) Properties.synchronize(that, that)}. */
	public static <GValue> Property2<GValue> synchronize(final Property<GValue> that) throws NullPointerException {
		return Properties.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedProperty new SynchronizedProperty<>(that, mutex)}. */
	public static <GValue> Property2<GValue> synchronize(final Property<GValue> that, final Object mutex) throws NullPointerException {
		return new SynchronizedProperty<>(that, mutex);
	}

}
