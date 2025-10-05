package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Producers.producerFromValue;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Getter}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Getters {

	/** Diese Methode liefert den gegebenen {@link Getter} als {@link Getter3}. Wenn er {@code null} ist, wird dern {@link EmptyGetter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Getter3<ITEM, VALUE> getterFrom(Getter<? super ITEM, ? extends VALUE> that) {
		if (that == null) return emptyGetter();
		if (that instanceof Getter3) return (Getter3<ITEM, VALUE>)that;
		return concatGetter(that, neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#producerFromValue(Producer) getterFromProducer(producerFromValue(that))}. */
	public static <VALUE> Getter3<Object, VALUE> getterFromValue(VALUE that) {
		return Getters.getterFromProducer(producerFromValue(that));
	}

	/** Diese Methode liefert den {@link EmptyGetter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Getter3<ITEM, VALUE> emptyGetter() {
		return (Getter3<ITEM, VALUE>)EmptyGetter.INSTANCE;
	}

	/** Diese Methode liefert den {@link NeutralGetter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Getter3<ITEM, ITEM> neutralGetter() {
		return (Getter3<ITEM, ITEM>)NeutralGetter.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatGetter new ConcatGetter<>(that, trans)}. */
	public static <ITEM, VALUE2, VALUE> Getter3<ITEM, VALUE> concatGetter(Getter<? super ITEM, ? extends VALUE2> that,
		Getter<? super VALUE2, ? extends VALUE> trans) throws NullPointerException {
		return new ConcatGetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link BufferedGetter new BufferedGetter<>(that, mode, hasher)}. */
	public static <ITEM, VALUE> Getter3<ITEM, VALUE> bufferedGetter(Getter<? super ITEM, ? extends VALUE> that, int mode, Hasher hasher)
		throws NullPointerException, IllegalArgumentException {
		return new BufferedGetter<>(that, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedGetter(Getter, Getter, Getter, Getter) aggregate(that, trans, empty(), empty())}. */
	public static <ITEM, VALUE, VALUE2> Getter3<Iterable<? extends ITEM>, VALUE> aggregatedGetter(final Getter<? super ITEM, ? extends VALUE2> that,
		final Getter<? super VALUE2, ? extends VALUE> trans) throws NullPointerException {
		return aggregatedGetter(that, trans, emptyGetter(), emptyGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link AggregatedGetter new AggregatedGetter<>(that, trans, empty, mixed)}. */
	public static <ITEM extends Iterable<? extends ITEM2>, VALUE, ITEM2, VALUE2> Getter3<ITEM, VALUE> aggregatedGetter(
		final Getter<? super ITEM2, ? extends VALUE2> that, final Getter<? super VALUE2, ? extends VALUE> trans, final Getter<? super ITEM, ? extends VALUE> empty,
		final Getter<? super ITEM, ? extends VALUE> mixed) throws NullPointerException {
		return new AggregatedGetter<>(that, trans, empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedGetter new OptionalizedGetter<>(that, value)}. */
	public static <ITEM, VALUE> Getter3<ITEM, VALUE> optionalizedGetter(Getter<? super ITEM, ? extends VALUE> that, final VALUE value)
		throws NullPointerException {
		return new OptionalizedGetter<>(that, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedGetter new SynchronizedGetter<>(that, mutex)}. */
	public static <ITEM, VALUE> Getter3<ITEM, VALUE> synchronizedGetter(final Getter<? super ITEM, ? extends VALUE> that, final Object mutex)
		throws NullPointerException {
		return new SynchronizedGetter<>(that, mutex);
	}

	/** Diese Methode liefert einen {@link Getter3} zu {@link Producer#get()} des gegebenen {@link Producer}. */
	public static <VALUE> Getter3<Object, VALUE> getterFromProducer(Producer<? extends VALUE> that) throws NullPointerException {
		return notNull(that) == Producers.emptyProducer() ? emptyGetter() : item -> that.get();
	}

	/** Diese Klasse implementiert den leeren {@link Getter3}, der beim {@link #get(Object) Lesen} stets {@code null} liefert. */
	public static class EmptyGetter extends AbstractGetter<Object, Object> {

		public static final Getter3<?, ?> INSTANCE = new EmptyGetter();

	}

	/** Diese Klasse implementiert den neutralen {@link Getter3}, der beim {@link #get(Object) Lesen} stets den gegebenen Datensatz als Wert liefert. */
	public static class NeutralGetter extends AbstractGetter<Object, Object> {

		public static final Getter3<?, ?> INSTANCE = new NeutralGetter();

		@Override
		public Object get(Object item) {
			return item;
		}

	}

	/** Diese Klasse implementiert einen verkettenden {@link Getter3}, der beim {@link #get(Object) Lesen} des Werts eines Datensatzes {@code item}
	 * {@code this.trans.get(this.that.get(item))} liefert.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft.
	 * @param <VALUE2> Typ des zu übersetzenden Werts. */
	public static class ConcatGetter<ITEM, VALUE, VALUE2> extends AbstractGetter<ITEM, VALUE> {

		public ConcatGetter(Getter<? super ITEM, ? extends VALUE2> that, Getter<? super VALUE2, ? extends VALUE> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public VALUE get(ITEM item) {
			return this.trans.get(this.that.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Getter<? super ITEM, ? extends VALUE2> that;

		private final Getter<? super VALUE2, ? extends VALUE> trans;

	}

	/** Diese Klasse implementiert einen gepufferten {@link Getter3}, der das {@link #get(Object) Lesen} an einen {@link #that gegebenen} {@link Getter} delegiert
	 * und den ermittelten Wert zur Wiederverwendung in einem {@link #buffer Puffer} ablegt, sofern der Puffer den Wert zum Datensatz noch nicht enthält, und
	 * sonst den im Puffer abgelegte Wert liefert. Der Abgleich der Datensätze erfolgt über einen gegebenen {@link Hasher}. Die Ablage der Werte zu den
	 * Datensätzen erfolgt über {@link Reference Verweise} der {@lonk #mode gegebenen} Stärke ({@link BufferedGetter#HARD_REF_MODE},
	 * {@link BufferedGetter#SOFT_REF_MODE}, {@link BufferedGetter#WEAK_REF_MODE}).
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class BufferedGetter<ITEM, VALUE> extends AbstractGetter<ITEM, VALUE> {

		/** Dieses Feld identifiziert die direkte Referenz. */
		public static final int HARD_REF_MODE = 0;

		/** Dieses Feld identifiziert die {@link WeakReference}. */
		public static final int WEAK_REF_MODE = 1;

		/** Dieses Feld identifiziert die {@link SoftReference}. */
		public static final int SOFT_REF_MODE = 2;

		public BufferedGetter(final Getter<? super ITEM, ? extends VALUE> that, final int mode, final Hasher hasher)
			throws NullPointerException, IllegalArgumentException {
			this.that = notNull(that);
			this.mode = mode;
			this.hasher = notNull(hasher);
			if (mode == BufferedGetter.HARD_REF_MODE) {
				this.queue = null;
				this.buffer = HashMap.from(hasher, new Field<ITEM, VALUE>() {

					@Override
					public VALUE get(final ITEM item) {
						return BufferedGetter.this.value = that.get(item);
					}

					@Override
					public void set(final ITEM item, final VALUE value) {
						BufferedGetter.this.value = value;
					}

				});
			} else if (mode == BufferedGetter.SOFT_REF_MODE) {
				this.queue = new ReferenceQueue<>();
				this.buffer = HashMap.from(hasher, new Field<ITEM, SoftRef>() {

					@Override
					public SoftRef get(final ITEM item) {
						return new SoftRef(item, BufferedGetter.this.value = that.get(item));
					}

					@Override
					@SuppressWarnings ("unchecked")
					public void set(final ITEM item, final SoftRef value) {
						if ((BufferedGetter.this.value = (VALUE)value.get()) != null) return;
						((HashMap<ITEM, SoftRef>)BufferedGetter.this.buffer).put(item, this.get(item));
					}

				});
			} else if (mode == BufferedGetter.WEAK_REF_MODE) {
				this.queue = new ReferenceQueue<>();
				this.buffer = HashMap.from(hasher, new Field<ITEM, WeakRef>() {

					@Override
					public WeakRef get(final ITEM item) {
						return new WeakRef(item, BufferedGetter.this.value = that.get(item));
					}

					@Override
					@SuppressWarnings ("unchecked")
					public void set(final ITEM item, final WeakRef value) {
						if ((BufferedGetter.this.value = (VALUE)value.get()) != null) return;
						((HashMap<ITEM, WeakRef>)BufferedGetter.this.buffer).put(item, this.get(item));
					}

				});
			} else throw new IllegalArgumentException();
		}

		@Override
		public VALUE get(final ITEM item) {
			try {
				this.buffer.install(item);
				final var result = this.value;
				if (this.queue == null) return result;
				while (true) {
					final var ref = (Ref)this.queue.poll();
					if (ref == null) return result;
					this.buffer.remove(ref.item());
				}
			} finally {
				this.value = null;
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mode, this.hasher);
		}

		private static interface Ref extends Producer<Object> {

			public Object item();

		}

		private static final class SoftRef extends SoftReference<Object> implements Ref {

			final Object item;

			public SoftRef(final Object item, final Object value) {
				super(value);
				this.item = item;
			}

			@Override
			public Object item() {
				return this.item;
			}

		}

		private static final class WeakRef extends WeakReference<Object> implements Ref {

			final Object item;

			public WeakRef(final Object item, final Object value) {
				super(value);
				this.item = item;
			}

			@Override
			public Object item() {
				return this.item;
			}

		}

		public final Getter<? super ITEM, ? extends VALUE> that;

		public final int mode;

		public final Hasher hasher;

		public final HashMap<ITEM, ?> buffer;

		private final ReferenceQueue<Object> queue;

		VALUE value;

	}

	/** Diese Klasse implementiert einen aggregierenden {@link Getter3}, der beim {@link #get(Iterable) Lesen} über einen {@link #that gegebenen} {@link Getter}
	 * zunächst zu jedem Elemente des iterierbaren Datensatzes einen Wert ermittelt. Wenn der iterierbare Datensatz {@code null} oder leer ist, wird das Lesen an
	 * einen {@link #empty Leerwert} {@link Getter} delegiert. Wenn sich die ermittelten Werte voneinander {@link Objects#equals(Object) unterscheiden}, wird das
	 * Lesen an einen {@link #mixed Mischwert} {@link Getter} delegiert. Andernfalls wird der erste Wert über einen {@link #trans übersetzenden} {@link Getter} in
	 * den Ergebniswert überführt.
	 *
	 * @param <ITEM> Typ des iterierbaren Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft.
	 * @param <ITEM2> Typ der Elemente des iterierbaren Datensatzes sowie des Datensatzes des {@link #that lesenden} {@link Getter}.
	 * @param <VALUE2> Typ des Werts des {@link #that lesenden} {@link Getter}. */
	public static class AggregatedGetter<ITEM extends Iterable<? extends ITEM2>, VALUE, ITEM2, VALUE2> extends AbstractGetter<ITEM, VALUE> {

		public AggregatedGetter(Getter<? super ITEM2, VALUE2> that, Getter<? super VALUE2, ? extends VALUE> trans, Getter<? super ITEM, ? extends VALUE> empty,
			Getter<? super ITEM, ? extends VALUE> mixed) throws NullPointerException {
			this.that = notNull(that);
			this.trans = notNull(trans);
			this.empty = notNull(empty);
			this.mixed = notNull(mixed);
		}

		@Override
		public VALUE get(ITEM item) {
			if (item == null) return this.empty.get(item);
			var iterator = item.iterator();
			if (!iterator.hasNext()) return this.empty.get(item);
			var value = this.that.get((ITEM2)iterator.next());
			while (iterator.hasNext()) {
				if (!Objects.equals(value, this.that.get((ITEM2)iterator.next()))) return this.mixed.get(item);
			}
			return this.trans.get(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans, this.empty, this.mixed);
		}

		/** Dieses Feld speichert den {@link Getter} zum Lesen des Werts eines Elements des interierbaren Datensatzes. */
		private final Getter<? super ITEM2, VALUE2> that;

		/** Dieses Feld speichert den {@link Getter} zur Übersetzung des Werts eines Elements des interierbaren Datensatzes. */
		private final Getter<? super VALUE2, ? extends VALUE> trans;

		/** Dieses Feld speichert den {@link Getter} zur Ermittlung des Leerwerts. */
		private final Getter<? super ITEM, ? extends VALUE> empty;

		/** Dieses Feld speichert den {@link Getter} zur Ermittlung des Mischwerts. */
		private final Getter<? super ITEM, ? extends VALUE> mixed;

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der das {@link #get(Object) Lesen} nur dann an einen gegebenen {@link Getter} delegiert, wenn der dazu
	 * verwendete Datensatz nicht {@code null} ist, und sonst einen gegebene {@link #value Rückfallwert} liefert.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class OptionalizedGetter<ITEM, VALUE> extends AbstractGetter<ITEM, VALUE> {

		public OptionalizedGetter(Getter<? super ITEM, ? extends VALUE> that, VALUE value) throws NullPointerException {
			this.that = notNull(that);
			this.value = value;
		}

		@Override
		public VALUE get(ITEM item) {
			return item == null ? this.value : this.that.get(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.value);
		}

		private final Getter<? super ITEM, ? extends VALUE> that;

		private final VALUE value;

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der einen gegebenen {@link Getter} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class SynchronizedGetter<ITEM, VALUE> extends AbstractGetter<ITEM, VALUE> {

		public SynchronizedGetter(Getter<? super ITEM, ? extends VALUE> that, Object mutex) throws NullPointerException {
			this.that = notNull(that);
			this.mutex = notNull(mutex, this);
		}

		@Override
		public VALUE get(ITEM item) {
			synchronized (this.mutex) {
				return this.that.get(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

		private final Getter<? super ITEM, ? extends VALUE> that;

		private final Object mutex;

	}

}
