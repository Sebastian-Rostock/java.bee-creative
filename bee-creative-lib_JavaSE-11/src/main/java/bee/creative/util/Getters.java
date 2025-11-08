package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.HashMap2.hashMapFrom;
import static bee.creative.util.Producers.emptyProducer;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Getter}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Getters {

	/** Diese Methode liefert den gegebenen {@link Getter} als {@link Getter3}. */
	@SuppressWarnings ("unchecked")
	public static <T, V> Getter3<T, V> getterFrom(Getter<? super T, ? extends V> that) {
		notNull(that);
		if (that instanceof Getter3) return (Getter3<T, V>)that;
		return item -> that.get(item);
	}

	/** Diese Methode liefert einen {@link Getter3}, der beim Lesen stets den gegebenen Wert liefert. */
	public static <V> Getter3<Object, V> getterFromValue(V value) {
		return value == null ? emptyGetter() : item -> value;
	}

	/** Diese Methode liefert einen {@link Getter3} zu {@link Producer#get()} des gegebenen {@link Producer}. */
	public static <V> Getter3<Object, V> getterFromProducer(Producer<? extends V> that) throws NullPointerException {
		return notNull(that) == emptyProducer() ? emptyGetter() : item -> that.get();
	}

	/** Diese Methode liefert den leeren {@link Getter3}, der beim Lesen stets {@code null} liefert. */
	@SuppressWarnings ("unchecked")
	public static <V> Getter3<Object, V> emptyGetter() {
		return (Getter3<Object, V>)emptyGetter;
	}

	/** Diese Methode liefert den neutralen {@link Getter3}, der beim Lesen stets den gegebenen Datensatz als Wert liefert. */
	@SuppressWarnings ("unchecked")
	public static <T> Getter3<T, T> neutralGetter() {
		return (Getter3<T, T>)neutralGetter;
	}

	/** Diese Methode liefert einen verkettenden {@link Getter3}, der beim Lesen des Werts eines Datensatzes {@code item} {@code trans.get(that.get(item))}
	 * liefert. */
	public static <T, V2, V> Getter3<T, V> concatGetter(Getter<? super T, ? extends V2> that, Getter<? super V2, ? extends V> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return item -> trans.get(that.get(item));
	}

	/** Diese Methode liefert einen gepufferten {@link Getter3}, der das Lesen an den gegebenen {@link Getter} {@code that} delegiert und den ermittelten Wert zur
	 * Wiederverwendung in einem {@link HashMap2 Puffer} ablegt, sofern der Puffer den Wert zum Datensatz noch nicht enthält, und sonst den im Puffer abgelegte
	 * Wert liefert. Der Abgleich der Datensätze erfolgt über einen gegebenen {@link Hasher}. Die Ablage der Werte zu den Datensätzen erfolgt über Verweise der
	 * gegebenen Stärke ({@link RefMode#HARD_REF}, {@link RefMode#SOFT_REF}, {@link RefMode#WEAK_REF}). */
	public static <T, V> Getter3<T, V> bufferedGetter(Getter<? super T, ? extends V> that, RefMode mode, Hasher hasher)
		throws NullPointerException, IllegalArgumentException {
		notNull(that);
		notNull(mode);
		notNull(hasher);
		if (mode.equals(RefMode.HARD_REF)) {
			var buffer = hashMapFrom(hasher, that);
			return item -> buffer.install(item);
		}
		return new Getter3<>() {

			@Override
			public V get(T item) {
				try {
					this.buffer.install(item);
					var result = this.value;
					while (true) {
						var ref = (Ref)this.queue.poll();
						if (ref == null) return result;
						this.buffer.remove(ref.item());
					}
				} finally {
					this.value = null;
				}
			}

			V value;

			ReferenceQueue<V> queue = new ReferenceQueue<>();

			@SuppressWarnings ("unchecked")
			HashMap<T, Ref> buffer = mode.equals(RefMode.SOFT_REF) ? hashMapFrom(hasher, item -> {
				return new SoftRef(item, this.value = that.get(item));
			}, (item, value) -> {
				if ((this.value = (V)value.get()) != null) return;
				this.buffer.put(item, new SoftRef(item, this.value = that.get(item)));
			}) : hashMapFrom(hasher, (T item) -> {
				return new WeakRef(item, this.value = that.get(item));
			}, (T item, Ref value) -> {
				if ((this.value = (V)value.get()) != null) return;
				(this.buffer).put(item, new WeakRef(item, this.value = that.get(item)));
			});

		};
	}

	/** Diese Methode liefert einen aggregierenden {@link Getter3}, der beim Lesen über den gegebenen {@link Getter} {@code that} zunächst zu jedem Elemente des
	 * iterierbaren Datensatzes einen Wert ermittelt. Wenn der iterierbare Datensatz {@code null} oder leer ist, wird das Lesen an den {@link Getter}
	 * {@code empty} delegiert. Wenn sich die ermittelten Werte voneinander {@link Objects#equals(Object) unterscheiden}, wird das Lesen an den {@link Getter}
	 * {@code mixed} delegiert. Andernfalls wird der erste Wert über den übersetzenden {@link Getter} {@code trans} in den Ergebniswert überführt. */
	public static <T extends Iterable<? extends T2>, V, T2, V2> Getter3<T, V> aggregatedGetter(Getter<? super T2, ? extends V2> that,
		Getter<? super V2, ? extends V> trans, Getter<? super T, ? extends V> empty, Getter<? super T, ? extends V> mixed) throws NullPointerException {
		notNull(that);
		notNull(trans);
		notNull(empty);
		notNull(mixed);
		return item -> {
			if (item == null) return empty.get(item);
			var iter = item.iterator();
			if (!iter.hasNext()) return empty.get(item);
			var value = that.get(iter.next());
			while (iter.hasNext()) {
				if (!Objects.equals(value, that.get(iter.next()))) return mixed.get(item);
			}
			return trans.get(value);
		};
	}

	/** Diese Methode liefert einen {@link Getter3}, der das Lesen nur dann an den gegebenen {@link Getter} delegiert, wenn der dazu verwendete Datensatz nicht
	 * {@code null} ist, und sonst den gegebene Rückfallwert liefert. */
	public static <T, V> Getter3<T, V> optionalizedGetter(Getter<? super T, ? extends V> that, V value) throws NullPointerException {
		notNull(that);
		return item -> item == null ? value : that.get(item);
	}

	/** Diese Methode liefert einen {@link Getter3}, der einen gegebenen {@link Getter} über {@code synchronized(mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird der gelieferte {@link Getter} verwendet. */
	public static <T, V> Getter3<T, V> synchronizedGetter(Getter<? super T, ? extends V> that, Object mutex) throws NullPointerException {
		notNull(that);
		return new Getter3<>() {

			@Override
			public V get(T item) {
				synchronized (notNull(mutex, this)) {
					return that.get(item);
				}
			}

		};
	}

	/** Diese Klasse implementiert die Referenzstärke für {@link Getters#bufferedGetter(Getter, RefMode, Hasher)}.
	 *
	 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
	public enum RefMode {

		/** Dieses Feld identifiziert die direkte Referenz. */
		HARD_REF,

		/** Dieses Feld identifiziert die {@link WeakReference}. */
		WEAK_REF,

		/** Dieses Feld identifiziert die {@link SoftReference}. */
		SOFT_REF;

	}

	private static final Getter3<?, ?> emptyGetter = null;

	private static final Getter3<?, ?> neutralGetter = item -> item;

	private interface Ref extends Producer<Object> {

		public Object item();

	}

	private static final class SoftRef extends SoftReference<Object> implements Ref {

		public final Object item;

		public SoftRef(Object item, Object value) {
			super(value);
			this.item = item;
		}

		@Override
		public Object item() {
			return this.item;
		}

	}

	private static final class WeakRef extends WeakReference<Object> implements Ref {

		public final Object item;

		public WeakRef(Object item, Object value) {
			super(value);
			this.item = item;
		}

		@Override
		public Object item() {
			return this.item;
		}

	}

}
