package bee.creative.util;

import static bee.creative.util.Getters.neutralGetter;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert gundlegende {@link Property}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Properties {

	/** Diese Methode liefert das gegebene {@link Property} als {@link Property2}. Wenn es {@code null} ist, wird das {@link EmptyProperty} geliefert. */
	public static <VALUE> Property2<VALUE> propertyFrom(Property<VALUE> that) {
		if (that == null) return Properties.emptyProperty();
		if (that instanceof Property2<?>) return (Property2<VALUE>)that;
		return translatedProperty(that, neutralGetter(), neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link CompositeProperty new CompositeProperty<>(get, set)}. */
	public static <VALUE> Property2<VALUE> propertyFrom(Producer<? extends VALUE> get, Consumer<? super VALUE> set) throws NullPointerException {
		return new CompositeProperty<>(get, set);
	}

	public static <VALUE> Property2<VALUE> propertyFrom(Field<?, VALUE> that) {
		return propertyFrom(Producers.producerFromGetter(that), Consumers.consumerFromSetter(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link #propertyFrom(Producer, Consumer) Properties.from(get, Properties.empty())}. */
	public static <VALUE> Property2<VALUE> propertyFrom(Producer<? extends VALUE> get) throws NullPointerException {
		return propertyFrom(get, emptyProperty());
	}

	/** Diese Methode ist eine Abkürzung für {@link #propertyFrom(Producer, Consumer) Properties.from(Properties.empty(), set)}. */
	public static <VALUE> Property2<VALUE> propertyFrom(Consumer<? super VALUE> set) throws NullPointerException {
		return propertyFrom(emptyProperty(), set);
	}

	/** Diese Methode ist eine Abkürzung für {@link ValueProperty new ValueProperty<>(value)}. */
	public static <VALUE> Property2<VALUE> propertyFromValue(VALUE value) {
		return new ValueProperty<>(value);
	}

	/** Diese Methode liefert das {@link EmptyProperty}. */
	@SuppressWarnings ("unchecked")
	public static <VALUE> Property2<VALUE> emptyProperty() {
		return (Property2<VALUE>)EmptyProperty.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link SetupProperty new SetupProperty<>(that, setup)}. */
	public static <VALUE> Property2<VALUE> setupProperty(Property<VALUE> that, Producer<? extends VALUE> setup) throws NullPointerException {
		return new SetupProperty<>(that, setup);
	}

	/** Diese Methode ist eine Abkürzung für {@link #translatedProperty(Property, Getter, Getter) Properties.translate(that, Getters.fromTarget(trans),
	 * Getters.fromSource(trans))}.
	 *
	 * @see Translator#toTarget(Object)
	 * @see Translator#toSource(Object) */
	public static <VALUE, VALUE2> Property2<VALUE> translatedProperty(Property<VALUE2> that, Translator<VALUE2, VALUE> trans) throws NullPointerException {
		return Properties.translatedProperty(that, trans::toTarget, trans::toSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link #propertyFrom(Producer, Consumer) Properties.from(Producers.translate(that, transGet),
	 * Consumers.translate(that, transSet))}.
	 *
	 * @see Producers#translatedProducer(Producer, Getter)
	 * @see Consumers#translatedConsumer(Consumer, Getter) */
	public static <GSource, GTarget> Property2<GTarget> translatedProperty(Property<GSource> that, Getter<? super GSource, ? extends GTarget> transGet,
		Getter<? super GTarget, ? extends GSource> transSet) throws NullPointerException {
		return Properties.propertyFrom(Producers.translatedProducer(that, transGet), Consumers.translatedConsumer(that, transSet));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableProperty new ObservableProperty<>(property)}. */
	public static <VALUE> ObservableProperty<VALUE> observableProperty(Property<VALUE> that) throws NullPointerException {
		return new ObservableProperty<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedProperty(Property, Object) Properties.synchronize(that, that)}. */
	public static <VALUE> Property2<VALUE> synchronizedProperty(Property<VALUE> that) throws NullPointerException {
		return synchronizedProperty(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedProperty new SynchronizedProperty<>(that, mutex)}. */
	public static <VALUE> Property2<VALUE> synchronizedProperty(Property<VALUE> that, final Object mutex) throws NullPointerException {
		return new SynchronizedProperty<>(that, mutex);
	}

	/** Diese Klasse implementiert ein {@link Property2}, das das {@link #set(Object) Schreiben} ignoriert und beim {@link #get() Lesen} stets {@code null}
	 * liefert. */
	public static class EmptyProperty extends AbstractProperty<Object> {

		public static final Property2<?> INSTANCE = new EmptyProperty();

	}

	/** Diese Klasse implementiert ein {@link Property2}, das einen {@link #value Wert} verwaltet, der {@link #get() gelesen} und {@link #set(Object) geschrieben}
	 * werden kann.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class ValueProperty<VALUE> extends AbstractProperty<VALUE> {

		public VALUE value;

		public ValueProperty(VALUE value) {
			this.value = value;
		}

		@Override
		public VALUE get() {
			return this.value;
		}

		@Override
		public void set(VALUE value) {
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
	 * @param <VALUE> Typ des Werts. */
	public static class SetupProperty<VALUE> extends AbstractProperty<VALUE> {

		public final Property<VALUE> that;

		public final Producer<? extends VALUE> setup;

		public SetupProperty(Property<VALUE> that, Producer<? extends VALUE> setup) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.setup = Objects.notNull(setup);
		}

		@Override
		public VALUE get() {
			var result = this.that.get();
			if (result != null) return result;
			result = this.setup.get();
			this.that.set(result);
			return result;
		}

		@Override
		public void set(VALUE value) {
			this.that.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.setup);
		}

	}

	/** Diese Klasse implementiert ein zusammengesetztes {@link Property2}, das das {@link #get() Lesen} an einen gegebenen {@link Producer} und das
	 * {@link #set(Object) Schreiben} an einen gegebenen {@link Consumer} delegiert.
	 *
	 * @param <VALUE> Typ des Werts. */
	public static class CompositeProperty<VALUE> extends AbstractProperty<VALUE> {

		public final Producer<? extends VALUE> get;

		public final Consumer<? super VALUE> set;

		public CompositeProperty(Producer<? extends VALUE> get, Consumer<? super VALUE> set) throws NullPointerException {
			this.get = Objects.notNull(get);
			this.set = Objects.notNull(set);
		}

		@Override
		public VALUE get() {
			return this.get.get();
		}

		@Override
		public void set(VALUE value) {
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
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class ObservableProperty<VALUE> extends AbstractProperty<VALUE> implements Observable<UpdatePropertyEvent, UpdatePropertyListener> {

		/** Dieses Feld speichert die Eigenschaft, an die in {@link #get()} und {@link #set(Object)} delegiert wird. */
		public final Property<VALUE> that;

		/** Dieser Konstruktor initialisiert die überwachte Eigenschaft. */
		public ObservableProperty(Property<VALUE> that) {
			this.that = Objects.notNull(that);
		}

		/** Diese Methode gibt eine Kopie des gegebenen Werts oder diesen unverändert zurück. Vor dem Schreiben des neuen Werts wird vom alten Wert über diese
		 * Methode eine Kopie erzeugt, welche nach dem Schreiben beim auslösen des Ereignisses zur Aktualisierung eingesetzt wird. Eine Kopie ist hierbei nur dann
		 * nötig, wenn der alte Wert sich durch das Schreiben des neuen ändert.
		 *
		 * @param value alter Wert.
		 * @return gegebener oder kopierter Wert. */
		protected VALUE customClone(VALUE value) {
			return value;
		}

		/** Diese Methode gibt die {@link Object#equals(Object) Äquivalenz} der gegebenen Werte zurück. Sie wird beim Setzen des Werts zur Erkennung einer
		 * Wertänderung eingesetzt.
		 *
		 * @param value1 alter Wert.
		 * @param value2 neuer Wert.
		 * @return {@link Object#equals(Object) Äquivalenz} der gegebenen Objekte. */
		protected boolean customEquals(VALUE value1, VALUE value2) {
			return Objects.deepEquals(value1, value2);
		}

		@Override
		public VALUE get() {
			return this.that.get();
		}

		@Override
		public void set(VALUE newValue) {
			var oldValue = this.that.get();
			if (this.customEquals(oldValue, newValue)) return;
			oldValue = this.customClone(oldValue);
			this.that.set(newValue);
			this.fire(new UpdatePropertyEvent(this, oldValue, newValue));
		}

		@Override
		public UpdatePropertyListener put(UpdatePropertyListener listener) throws IllegalArgumentException {
			return UpdatePropertyObservables.INSTANCE.put(this, listener);
		}

		@Override
		public UpdatePropertyListener putWeak(UpdatePropertyListener listener) throws IllegalArgumentException {
			return UpdatePropertyObservables.INSTANCE.putWeak(this, listener);
		}

		@Override
		public void pop(UpdatePropertyListener listener) throws IllegalArgumentException {
			UpdatePropertyObservables.INSTANCE.pop(this, listener);
		}

		@Override
		public UpdatePropertyEvent fire(UpdatePropertyEvent event) throws NullPointerException {
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
	 * @param <VALUE> Typ des Werts. */
	public static class SynchronizedProperty<VALUE> extends AbstractProperty<VALUE> {

		public final Property<VALUE> that;

		public final Object mutex;

		public SynchronizedProperty(Property<VALUE> that, Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public VALUE get() {
			synchronized (this.mutex) {
				return this.that.get();
			}
		}

		@Override
		public void set(VALUE value) {
			synchronized (this.mutex) {
				this.that.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

}
