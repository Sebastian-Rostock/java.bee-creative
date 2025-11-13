package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Consumers.consumerFromSetter;
import static bee.creative.util.Consumers.emptyConsumer;
import static bee.creative.util.Consumers.translatedConsumer;
import static bee.creative.util.Producers.emptyProducer;
import static bee.creative.util.Producers.producerFromGetter;
import static bee.creative.util.Producers.translatedProducer;

/** Diese Klasse implementiert gundlegende {@link Property}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Properties {

	/** Diese Methode liefert das gegebene {@link Property3}. Wenn es {@code null} ist, wird {@link #emptyProperty()} geliefert. */
	public static <V> Property3<V> propertyFrom(Property3<V> that) {
		if (that == null) return emptyProperty();
		return (Property3<V>)that;
	}

	/** Diese Methode liefert das gegebene {@link Property} als {@link Property3}. Wenn es {@code null} ist, wird {@link #emptyProperty()} geliefert. */
	public static <V> Property3<V> propertyFrom(Property<V> that) {
		if (that == null) return emptyProperty();
		if (that instanceof Property3<?>) return (Property3<V>)that;
		return propertyFrom(that, that);
	}

	/** Diese Methode liefert ein zusammengesetztes {@link Property3}mit den gegebenen Methoden. */
	public static <V> Property3<V> propertyFrom(Producer<? extends V> get, Consumer<? super V> set) throws NullPointerException {
		notNull(get);
		notNull(set);
		return new Property3<>() {

			@Override
			public V get() {
				return get.get();
			}

			@Override
			public void set(V value) {
				set.set(value);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@link #propertyFrom(Producer, Consumer) propertyFrom(producerFromGetter(that), consumerFromSetter(that))}. */
	public static <V> Property3<V> propertyFromField(Field<?, V> that) {
		return propertyFrom(producerFromGetter(that), consumerFromSetter(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link #propertyFrom(Producer, Consumer) propertyFrom(producerFromGetter(that, item), consumerFromSetter(that,
	 * item))}. */
	public static <T, V> Property3<V> propertyFromField(Field<? super T, V> that, Producer<? extends T> item) {
		return propertyFrom(producerFromGetter(that, item), consumerFromSetter(that, item));
	}

	/** Diese Methode liefert ein {@link Property3}, das einen Wert verwaltet, der gelesen und geschrieben werden kann. Der Wert ist initial {@code null}. */
	public static <V> Property3<V> propertyWithValue() {
		return propertyWithValue(null);
	}

	/** Diese Methode liefert ein {@link Property3}, das einen Wert verwaltet, der gelesen und geschrieben werden kann. Der Wert ist initial {@code value}. */
	public static <V> Property3<V> propertyWithValue(V value) {
		var result = new Property3<V>() {

			@Override
			public V get() {
				return this.value;
			}

			@Override
			public void set(V value) {
				this.value = value;
			}

			V value;

		};
		result.set(value);
		return result;
	}

	/** Diese Methode liefert ein {@link Property3}, das das Schreiben ignoriert und beim Lesen stets {@code null} liefert. */
	@SuppressWarnings ("unchecked")
	public static <V> Property3<V> emptyProperty() {
		return (Property3<V>)emptyProperty;
	}

	/** Diese Methode liefert ein initialisierendes {@link Property3}, das das Lesen und Schreiben an das gegebene {@link Property} delegiert. Wenn der gelesene
	 * Wert {@code null} ist, wird er initialisiert, d.h. mit Hilfe eines gegebenen {@link Producer} ermittelt und über das gegebene {@link Property}
	 * geschrieben. */
	public static <V> Property3<V> setupProperty(Property<V> that, Producer<? extends V> setup) throws NullPointerException {
		notNull(that);
		notNull(setup);
		return propertyFrom(() -> {
			var result = that.get();
			if (result != null) return result;
			result = setup.get();
			that.set(result);
			return result;
		}, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #translatedProperty(Property, Getter, Getter) translatedProperty(that, trans::toTarget, trans::toSource)}. */
	public static <V2, V> Property3<V> translatedProperty(Property<V2> that, Translator<V2, V> trans) throws NullPointerException {
		return translatedProperty(that, trans::toTarget, trans::toSource);
	}

	/** Diese Methode liefert ein übersetztes {@link Property3} und ist eine Abkürzung für {@link #propertyFrom(Producer, Consumer)
	 * propertyFrom(translatedProducer(that, transGet), translatedConsumer(that, transSet))}. */
	public static <V2, V> Property3<V> translatedProperty(Property<V2> that, Getter<? super V2, ? extends V> transGet, Getter<? super V, ? extends V2> transSet)
		throws NullPointerException {
		return propertyFrom(translatedProducer(that, transGet), translatedConsumer(that, transSet));
	}

	/** Diese Methode liefert ein {@link Property3}, das das gegebenen {@link Property} über {@code synchronized(mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird das gelieferte {@link Property} verwendet. */
	public static <V> Property3<V> synchronizedProperty(Property<V> that, final Object mutex) throws NullPointerException {
		notNull(that);
		return new Property3<>() {

			@Override
			public V get() {
				synchronized (notNull(mutex, this)) {
					return that.get();
				}
			}

			@Override
			public void set(V value) {
				synchronized (notNull(mutex, this)) {
					that.set(value);
				}
			}

		};
	}

	private static final Property3<?> emptyProperty = propertyFrom(emptyProducer(), emptyConsumer());

}
