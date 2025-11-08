package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Producers.emptyProducer;

/** Diese Klasse implementiert grundlegende {@link Consumer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Consumers {

	/** Diese Methode liefert den gegebenen {@link Consumer} als {@link Consumer3}. */
	@SuppressWarnings ("unchecked")
	public static <V> Consumer3<V> consumerFrom(Consumer<? super V> that) {
		notNull(that);
		if (that instanceof Consumer3<?>) return (Consumer3<V>)that;
		return value -> that.set(value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #consumerFromSetter(Setter, Producer) consumerFromSetter(that, emptyProducer())}. */
	public static <T, V> Consumer3<V> consumerFromSetter(Setter<? super T, ? super V> that) throws NullPointerException {
		return consumerFromSetter(that, emptyProducer());
	}

	/** Diese Methode ist liefert einen verketteten {@link Consumer3}, der beim Schreiben den gegebenen Wert an den gegebenen {@link Setter} delegiert und dazu
	 * den vom gegebenen {@link Producer} bereitgestellten Datensatz verwendet. Das Schreiben des Werts {@code value} erfolgt über
	 * {@code that.set(item.get(), value)}. */
	public static <T, V> Consumer3<V> consumerFromSetter(Setter<? super T, ? super V> that, Producer<? extends T> item) throws NullPointerException {
		notNull(that);
		notNull(item);
		return value -> that.set(item.get(), value);
	}

	/** Diese Methode liefert einen {@link Consumer3}, der das Schreiben ignoriert. */
	@SuppressWarnings ("unchecked")
	public static <V> Consumer3<V> emptyConsumer() {
		return (Consumer3<V>)emptyConsumer;
	}

	/** Diese Methode liefert einen übersetzten {@link Consumer3}, der den Wert beim Schreiben über den gegebenen {@link Getter} in den Wert eines gegebenen
	 * {@link Consumer} überführt. Das Schreiben des Werts {@code value} erfolgt über {@code that.set(trans.get(value))}. */
	public static <V, V2> Consumer3<V> translatedConsumer(Consumer<? super V2> that, Getter<? super V, ? extends V2> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return value -> that.set(trans.get(value));
	}

	/** Diese Methode liefert einen {@link Consumer3}, der einen gegebenen {@link Consumer} über {@code synchronized(mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird der gelieferte {@link Consumer} verwendet. */
	public static <V> Consumer3<V> synchronizedConsumer(Consumer<? super V> that, Object mutex) {
		notNull(that);
		return new Consumer3<>() {

			public void set(V value) {
				synchronized (notNull(mutex, this)) {
					that.set(value);
				}
			}

		};
	}

	private static final Consumer3<?> emptyConsumer = value -> {};

}
