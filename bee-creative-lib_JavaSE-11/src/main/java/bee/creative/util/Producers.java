package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.emptyGetter;

/** Diese Klasse implementiert grundlegende {@link Producer3}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Methode liefert den gegebenen {@link Producer} als {@link Producer3}. */
	@SuppressWarnings ("unchecked")
	public static <V> Producer3<V> producerFrom(Producer<? extends V> that) throws NullPointerException {
		notNull(that);
		if (that instanceof Producer3<?>) return (Producer3<V>)that;
		return () -> that.get();
	}

	/** Diese Methode liefert einen {@link Producer3}, der beim Lesen den gegebenen {@code value} liefert. */
	public static <V> Producer3<V> producerFromValue(V value) {
		return value == null ? emptyProducer() : () -> value;
	}

	/** Diese Methode liefert einen {@link Producer3}, der beim Lesen den das Ergebnis von {@code that.get(null)} liefert. */
	public static <V> Producer3<V> producerFromGetter(Getter<?, ? extends V> that) {
		return notNull(that) == emptyGetter() ? emptyProducer() : () -> that.get(null);
	}

	/** Diese Methode liefert einen {@link Producer3}, der beim Lesen stets {@code null} liefert. */
	@SuppressWarnings ("unchecked")
	public static <V> Producer3<V> emptyProducer() {
		return (Producer3<V>)emptyProducer;
	}

	public static <T, V> Producer3<V> concatProducer(Producer<? extends T> item, Getter<? super T, ? extends V> that) throws NullPointerException {
		return translatedProducer(item, that);
	}

	/** Diese Methode liefert einen übersetzten {@link Producer3}, der beim Lesen einen Wert liefert, der über den gegebenen {@link Getter} {@code trans} aus dem
	 * Wert des gegebenen {@link Producer} {@code that} ermittelt wird. Das Lesen erfolgt über {@code trans.get(that.get())}. */
	public static <V, V2> Producer3<V> translatedProducer(Producer<? extends V2> that, Getter<? super V2, ? extends V> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return () -> trans.get(that.get());
	}

	/** Diese Methode liefert einen {@link Producer3}, der den gegebenen {@link Producer} {@code that} über {@code synchronized(mutex)} synchronisiert. Wenn
	 * dieses Synchronisationsobjekt {@code null} ist, wird der gelieferte {@link Producer} verwendet. */
	public static <V> Producer3<V> synchronizedProducer(Producer<? extends V> that, Object mutex) throws NullPointerException {
		notNull(that);
		return new Producer3<>() {

			@Override
			public V get() {
				synchronized (notNull(mutex, this)) {
					return that.get();
				}
			}

		};
	}

	private static final Producer3<?> emptyProducer = () -> null;

}
