package bee.creative.util;

import static bee.creative.lang.Objects.notNull;

/** Diese Klasse implementiert grundlegende {@link Setter}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	/** Diese Methode liefert den gegebenen {@link Setter} als {@link Setter3}. */
	@SuppressWarnings ("unchecked")
	public static <T, V> Setter3<T, V> setterFrom(Setter<? super T, ? super V> that) throws NullPointerException {
		notNull(that);
		if (that instanceof Setter3) return (Setter3<T, V>)that;
		return (item, value) -> that.set(item, value);
	}

	/** Diese Methode liefert den gegebenen {@link Setter3} zu {@link Consumer#set(Object)}. */
	public static <V> Setter3<Object, V> setterFromConsumer(Consumer<? super V> that) throws NullPointerException {
		notNull(that);
		return (item, value) -> that.set(value);
	}

	/** Diese Methode liefert einen {@link Setter3}, der das Schreiben ignoriert. */
	@SuppressWarnings ("unchecked")
	public static <T, V> Setter3<T, V> emptySetter() {
		return (Setter3<T, V>)emptySetter;
	}

	/** Diese Methode liefert einen übersetzten {@link Setter3}, der das Schreiben mit dem über den gegebenen {@link Getter} übersetzten Datensatz an den
	 * gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} erfolgt über
	 * {@code that.set(trans.get(item), value)}. */
	public static <T, T2, V> Setter3<T, V> concatSetter(Getter<? super T, ? extends T2> trans, Setter<? super T2, ? super V> that) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return (item, value) -> that.set(trans.get(item), value);
	}

	/** Diese Methode liefert einen übersetzten {@link Setter3}, der das Schreiben mit dem über den gegebenen {@link Getter} übersetzten Wert an den gegebenen
	 * {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} erfolgt über
	 * {@code that.set(item, trans.get(value))}. */
	public static <T, V, V2> Setter3<T, V> translatedSetter(Setter<? super T, ? super V2> that, Getter<? super V, ? extends V2> trans)
		throws NullPointerException {
		notNull(that);
		notNull(trans);
		return (item, value) -> that.set(item, trans.get(value));
	}

	/** Diese Methode liefert einen aggregierten {@link Setter3}, der das Schreiben mit dem über den gegebenen {@link Getter} übersetzten Wert für jedes Element
	 * des iterierbaren Datensatzes an den gegebenen {@link Setter} delegeirt. Wenn der iterierbare Datensatz {@code null} oder leer ist, wird das Setzen
	 * ignoriert. */
	public static <T, V, V2> Setter3<Iterable<? extends T>, V> aggregatedSetter(Setter<? super T, ? super V2> that, Getter<? super V, ? extends V2> trans)
		throws NullPointerException {
		notNull(that);
		notNull(trans);
		return (item, value) -> {
			if (item == null) return;
			var iter = item.iterator();
			if (!iter.hasNext()) return;
			var value2 = trans.get(value);
			do {
				that.set(iter.next(), value2);
			} while (iter.hasNext());
		};
	}

	/** Diese Methode liefert einen {@link Setter3}, der das Schreiben nur dann an den gegebenen {@link Setter} delegiert, wenn die Eingabe nicht {@code null}
	 * ist. */
	public static <T, V> Setter3<T, V> optionalizedSetter(Setter<? super T, V> that) throws NullPointerException {
		notNull(that);
		return (item, value) -> {
			if (item == null) return;
			that.set(item, value);
		};
	}

	/** Diese Methode liefert einen {@link Setter3}, der einen gegebenen {@link Setter} über {@code synchronized(mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird der gelieferte {@link Setter} verwendet. */
	public static <T, V> Setter3<T, V> synchronizedSetter(Setter<? super T, ? super V> that, Object mutex) throws NullPointerException {
		notNull(that);
		return new Setter3<T, V>() {

			@Override
			public void set(T T, V V) {
				synchronized (notNull(mutex, this)) {
					that.set(T, V);
				}
			}

		};
	}

	private static final Setter3<?, ?> emptySetter = (item, value) -> {};

}
