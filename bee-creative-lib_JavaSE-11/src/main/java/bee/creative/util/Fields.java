package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.concatGetter;
import static bee.creative.util.Getters.emptyGetter;
import static bee.creative.util.Getters.getterFromProducer;
import static bee.creative.util.Getters.optionalizedGetter;
import static bee.creative.util.Setters.aggregatedSetter;
import static bee.creative.util.Setters.concatSetter;
import static bee.creative.util.Setters.emptySetter;
import static bee.creative.util.Setters.optionalizedSetter;
import static bee.creative.util.Setters.setterFromConsumer;
import java.util.Map;

/** Diese Klasse implementiert grundlegende {@link Field}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public final class Fields {

	/** Diese Methode liefert das gegebene {@link Field} als {@link Field3}. Wenn es {@code null} ist, wird {@link #emptyField()} geliefert. */
	public static <T, V> Field3<T, V> fieldFrom(Field<T, V> that) {
		if (that == null) return emptyField();
		if (that instanceof Field3<?, ?>) return (Field3<T, V>)that;
		return fieldFrom(that, that);
	}

	/** Diese Methode liefert ein zusammengesetztes {@link Field3} mit den gegebenen Methoden. */
	public static <T, V> Field3<T, V> fieldFrom(Getter<? super T, ? extends V> get, Setter<? super T, ? super V> set) throws NullPointerException {
		notNull(get);
		notNull(set);
		return new Field3<>() {

			@Override
			public V get(T item) {
				return get.get(item);
			}

			@Override
			public void set(T item, V value) {
				set.set(item, value);
			}

		};
	}

	/** Diese Methode liefert ein {@link Field3} zu {@link Map#get(Object)} und {@link Map#put(Object, Object)} und ist eine Abkürzung für
	 * {@link #fieldFrom(Getter, Setter) fieldFrom(that::get, that::put)}. */
	public static <T, V> Field3<T, V> fieldFromMap(Map<T, V> that) throws NullPointerException {
		return fieldFrom(that::get, that::put);
	}

	/** Diese Methode liefert ein {@link Field3} zu {@link Property#get()} und {@link Property#set(Object)} und ist eine Abkürzung für
	 * {@link #fieldFrom(Getter, Setter) fieldFrom(getterFromProducer(that), setterFromConsumer(that))}. */
	public static <V> Field3<Object, V> fieldFromProperty(Property<V> that) {
		return fieldFrom(getterFromProducer(that), setterFromConsumer(that));
	}

	/** Diese Methode liefert ein {@link Field3}, das beim Lesen stets {@code null} liefert und das Schreiben ignoriert. */
	@SuppressWarnings ("unchecked")
	public static <T, V> Field3<T, V> emptyField() {
		return (Field3<T, V>)emptyField;
	}

	/** Diese Methode liefert ein initialisierendes {@link Field3}, das das Schreiben an das gegebene {@link Field} delegiert und beim Lesen den Wert des
	 * gegebenen {@link Field} nur dann liefert, wenn dieser nicht {@code null} ist. Andernfalls wird der über den gegebenen {@link Getter} ermittelte Wert
	 * geliefert und zuvor über das gegebene {@link Field} geschrieben. */
	public static <T, V> Field3<T, V> setupField(final Field<? super T, V> that, final Getter<? super T, ? extends V> setup) throws NullPointerException {
		notNull(that);
		notNull(setup);
		return fieldFrom(item -> {
			var result = that.get(item);
			if (result != null) return result;
			result = setup.get(item);
			that.set(item, result);
			return result;
		}, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) fieldFrom(concatGetter(trans, that), concatSetter(trans, that))}. */
	public static <T, T2, V> Field3<T, V> concatField(Getter<? super T, ? extends T2> trans, Field<? super T2, V> that) throws NullPointerException {
		return fieldFrom(concatGetter(trans, that), concatSetter(trans, that));
	}

	/** Diese Methode ist eine Abkürzung für {@link ObservableField new ObservableField<>(target)}. */
	public static <T, V> ObservableField<T, V> observableField(Field<? super T, V> that) throws NullPointerException {
		return new ObservableField<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) Fields.from(Getters.concat(that, getTrans), Setters.translate(that, setTrans))}.
	 *
	 * @see Getters#concatGetter(Getter, Getter)
	 * @see Setters#translatedSetter(Setter, Getter) */
	public static <T, GSource, GTarget> Field3<T, GTarget> translateField(final Field<? super T, GSource> that,
		final Getter<? super GSource, ? extends GTarget> getTrans, final Getter<? super GTarget, ? extends GSource> setTrans) throws NullPointerException {
		return Fields.fieldFrom(concatGetter(that, getTrans), Setters.translatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #translateField(Field, Getter, Getter) translateField(that, trans::toTarget, trans::toSource)}.
	 *
	 * @see Translator#toTarget(Object)
	 * @see Translator#toSource(Object) */
	public static <T, GSource, GTarget> Field3<T, GTarget> translateField(final Field<? super T, GSource> that, final Translator<GSource, GTarget> trans)
		throws NullPointerException {
		return translateField(that, trans::toTarget, trans::toSource);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) fieldFrom(aggregatedGetter(that, getTrans, empty, mixed), aggregatedSetter(that,
	 * setTrans))}. */
	public static <T extends Iterable<? extends T2>, V, T2, V2> Field3<T, V> aggregatedField(Field<? super T2, V2> that, Getter<? super V2, ? extends V> getTrans,
		Getter<? super V, ? extends V2> setTrans, Getter<? super T, ? extends V> empty, Getter<? super T, ? extends V> mixed) throws NullPointerException {
		return fieldFrom(Getters.<T, V, T2, V2>aggregatedGetter(that, getTrans, empty, mixed), aggregatedSetter(that, setTrans));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fieldFrom(Getter, Setter) fieldFrom(optionalizedGetter(that, value), optionalizedSetter(that))}. */
	public static <T, V> Field3<T, V> optionalizedField(Field<? super T, V> that, V value) throws NullPointerException {
		return fieldFrom(optionalizedGetter(that, value), optionalizedSetter(that));
	}

	/** Diese Methode liefert ein {@link Field3}, das das gegebenen {@link Field} über {@code synchronized(mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird das gelieferte {@link Field} verwendet. */
	public static <T, V> Field3<T, V> synchronizedField(Field<? super T, V> that, Object mutex) throws NullPointerException {
		notNull(that);
		return new Field3<>() {

			@Override
			public V get(final T item) {
				synchronized (notNull(mutex, this)) {
					return that.get(item);
				}
			}

			@Override
			public void set(final T item, final V value) {
				synchronized (notNull(mutex, this)) {
					that.set(item, value);
				}
			}

		};
	}

	private static final Field3<?, ?> emptyField = fieldFrom(emptyGetter(), emptySetter());

}