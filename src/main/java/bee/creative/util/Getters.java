package bee.creative.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;
import bee.creative.ref.References;

/** Diese Klasse implementiert grundlegende {@link Getter}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Getters {

	/** Diese Klasse implementiert den leeren {@link Getter3}, der beim {@link #get(Object) Lesen} stets {@code null} liefert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyGetter extends AbstractGetter<Object, Object> {

		public static final Getter3<?, ?> INSTANCE = new EmptyGetter();

	}

	/** Diese Klasse implementiert den neutralen {@link Getter3}, der beim {@link #get(Object) Lesen} stets den gegebenen Datensatz als Wert liefert. */
	@SuppressWarnings ("javadoc")
	public static class NeutralGetter extends AbstractGetter<Object, Object> {

		public static final Getter3<?, ?> INSTANCE = new NeutralGetter();

		@Override
		public Object get(final Object item) {
			return item;
		}

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der das {@link #get(Object) Lesen} an eine gegebene {@link Method nativen Methode} delegiert. Bei einer
	 * Klassenmethode liefert er für einen Datensatz {@code item} {@link Method#invoke(Object, Object...) this.that.invoke(null, item)}, bei einer Objektmethode
	 * dagegen {@link Method#invoke(Object, Object...) this.that.invoke(item)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class MethodGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Method that;

		public MethodGetter(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (that.getParameterTypes().length != (Modifier.isStatic(that.getModifiers()) ? 1 : 0)) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				final GValue result;
				if (Modifier.isStatic(this.that.getModifiers())) {
					result = (GValue)this.that.invoke(null, item);
				} else {
					result = (GValue)this.that.invoke(item);
				}
				return result;
			} catch (final IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.that.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der das {@link #get(Object) Lesen} an einen gegebenen {@link Constructor nativen Kontruktor} delegiert.
	 * Für einen Datensatz {@code item} liefert er {@link Constructor#newInstance(Object...) this.that.newInstance(item)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Constructor<?> that;

		public ConstructorGetter(final Constructor<?> that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		public GValue get(final GItem item) {
			try {
				@SuppressWarnings ("unchecked")
				final GValue result = (GValue)this.that.newInstance(item);
				return result;
			} catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.that.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen verkettenden {@link Getter3}, der beim {@link #get(Object) Lesen} des Werts eines Datensatzes {@code item}
	 * {@code this.trans.get(this.that.get(item))} liefert.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param <GValue2> Typ des zu übersetzenden Werts. */
	@SuppressWarnings ("javadoc")
	public static class ConcatGetter<GItem, GValue, GValue2> extends AbstractGetter<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue2> that;

		public final Getter<? super GValue2, ? extends GValue> trans;

		public ConcatGetter(final Getter<? super GItem, ? extends GValue2> that, final Getter<? super GValue2, ? extends GValue> trans)
			throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public GValue get(final GItem item) {
			return this.trans.get(this.that.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen gepufferten {@link Getter3}, der das {@link #get(Object) Lesen} an einen {@link #that gegebenen} {@link Getter} delegiert
	 * und den ermittelten Wert zur Wiederverwendung in einem {@link #buffer Puffer} ablegt, sofern der Puffer den Wert zum Datensatz noch nicht enthält, und
	 * sonst den im Puffer abgelegte Wert liefert. Der Abgleich der Datensätze erfolgt über einen gegebenen {@link Hasher}. Die Ablage der Werte zu den
	 * Datensätzen erfolgt über {@link Reference Verweise} der {@lonk #mode gegebenen} Stärke ({@link References#HARD}, {@link References#SOFT},
	 * {@link References#WEAK}).
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class BufferedGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

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

		public final Getter<? super GItem, ? extends GValue> that;

		public final int mode;

		public final Hasher hasher;

		public final HashMap<GItem, ?> buffer;

		private final ReferenceQueue<Object> queue;

		GValue value;

		public BufferedGetter(final Getter<? super GItem, ? extends GValue> that, final int mode, final Hasher hasher)
			throws NullPointerException, IllegalArgumentException {
			this.that = Objects.notNull(that);
			this.mode = mode;
			this.hasher = Objects.notNull(hasher);
			if (mode == References.HARD) {
				this.queue = null;
				this.buffer = HashMap.from(hasher, new Field<GItem, GValue>() {

					@Override
					public GValue get(final GItem item) {
						return BufferedGetter.this.value = that.get(item);
					}

					@Override
					public void set(final GItem item, final GValue value) {
						BufferedGetter.this.value = value;
					}

				});
			} else if (mode == References.SOFT) {
				this.queue = new ReferenceQueue<>();
				this.buffer = HashMap.from(hasher, new Field<GItem, SoftRef>() {

					@Override
					public SoftRef get(final GItem item) {
						return new SoftRef(item, BufferedGetter.this.value = that.get(item));
					}

					@Override
					@SuppressWarnings ("unchecked")
					public void set(final GItem item, final SoftRef value) {
						if ((BufferedGetter.this.value = (GValue)value.get()) != null) return;
						((HashMap<GItem, SoftRef>)BufferedGetter.this.buffer).put(item, this.get(item));
					}

				});
			} else if (mode == References.WEAK) {
				this.queue = new ReferenceQueue<>();
				this.buffer = HashMap.from(hasher, new Field<GItem, WeakRef>() {

					@Override
					public WeakRef get(final GItem item) {
						return new WeakRef(item, BufferedGetter.this.value = that.get(item));
					}

					@Override
					@SuppressWarnings ("unchecked")
					public void set(final GItem item, final WeakRef value) {
						if ((BufferedGetter.this.value = (GValue)value.get()) != null) return;
						((HashMap<GItem, WeakRef>)BufferedGetter.this.buffer).put(item, this.get(item));
					}

				});
			} else throw new IllegalArgumentException();
		}

		@Override
		public GValue get(final GItem item) {
			try {
				this.buffer.install(item);
				final GValue result = this.value;
				if (this.queue == null) return result;
				while (true) {
					final Ref ref = (Ref)this.queue.poll();
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

	}

	/** Diese Klasse implementiert einen aggregierenden {@link Getter3}, der beim {@link #get(Iterable) Lesen} über einen {@link #that gegebenen} {@link Getter}
	 * zunächst zu jedem Elemente des iterierbaren Datensatzes einen Wert ermittelt. Wenn der iterierbare Datensatz {@code null} oder leer ist, wird das Lesen an
	 * einen {@link #empty Leerwert} {@link Getter} delegiert. Wenn sich die ermittelten Werte voneinander {@link Objects#equals(Object) unterscheiden}, wird das
	 * Lesen an einen {@link #mixed Mischwert} {@link Getter} delegiert. Andernfalls wird der erste Wert über einen {@link #trans übersetzenden} {@link Getter} in
	 * den Ergebniswert überführt.
	 *
	 * @param <GItem> Typ des iterierbaren Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param <GItem2> Typ der Elemente des iterierbaren Datensatzes sowie des Datensatzes des {@link #that lesenden} {@link Getter}.
	 * @param <GValue2> Typ des Werts des {@link #that lesenden} {@link Getter}. */
	@SuppressWarnings ("javadoc")
	public static class AggregatedGetter<GItem extends Iterable<? extends GItem2>, GValue, GItem2, GValue2> extends AbstractGetter<GItem, GValue> {

		/** Dieses Feld speichert den {@link Getter} zum Lesen des Werts eines Elements des interierbaren Datensatzes. */
		public final Getter<? super GItem2, GValue2> that;

		/** Dieses Feld speichert den {@link Getter} zur Übersetzung des Werts eines Elements des interierbaren Datensatzes. */
		public final Getter<? super GValue2, ? extends GValue> trans;

		/** Dieses Feld speichert den {@link Getter} zur Ermittlung des Leerwerts. */
		public final Getter<? super GItem, ? extends GValue> empty;

		/** Dieses Feld speichert den {@link Getter} zur Ermittlung des Mischwerts. */
		public final Getter<? super GItem, ? extends GValue> mixed;

		public AggregatedGetter(final Getter<? super GItem2, GValue2> that, final Getter<? super GValue2, ? extends GValue> trans,
			final Getter<? super GItem, ? extends GValue> empty, final Getter<? super GItem, ? extends GValue> mixed) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
			this.empty = Objects.notNull(empty);
			this.mixed = Objects.notNull(mixed);
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.empty.get(item);
			final Iterator<? extends GItem2> iterator = item.iterator();
			if (!iterator.hasNext()) return this.empty.get(item);
			final GItem2 entry = iterator.next();
			final GValue2 value = this.that.get(entry);
			while (iterator.hasNext()) {
				final GItem2 item2 = iterator.next();
				final GValue2 value2 = this.that.get(item2);
				if (!Objects.equals(value, value2)) return this.mixed.get(item);
			}
			return this.trans.get(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans, this.empty, this.mixed);
		}

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der das {@link #get(Object) Lesen} nur dann an einen gegebenen {@link Getter} delegiert, wenn der dazu
	 * verwendete Datensatz nicht {@code null} ist, und sonst einen gegebene {@link #value Rückfallwert} liefert.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class OptionalizedGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Getter<? super GItem, GValue> that;

		public final GValue value;

		public OptionalizedGetter(final Getter<? super GItem, GValue> that, final GValue value) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.value = value;
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.value;
			return this.that.get(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.value);
		}

	}

	/** Diese Klasse implementiert einen {@link Getter3}, der einen gegebenen {@link Getter} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> that;

		public final Object mutex;

		public SynchronizedGetter(final Getter<? super GItem, ? extends GValue> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.that.get(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	static class FilterGetter<GItem> extends AbstractGetter<GItem, Boolean> {

		public final Filter<? super GItem> that;

		public FilterGetter(final Filter<? super GItem> that) throws NullPointerException {
			this.that = that;
		}

		@Override
		public Boolean get(final GItem item) {
			return Boolean.valueOf(this.that.accept(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class SourceGetter<GValue, GItem> extends AbstractGetter<GItem, GValue> {

		public final Translator<? extends GValue, ? super GItem> that;

		public SourceGetter(final Translator<? extends GValue, ? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public GValue get(final Object item) {
			return this.that.toSource(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class TargetGetter<GValue, GItem> extends AbstractGetter<GItem, GValue> {

		public final Translator<? super GItem, ? extends GValue> that;

		public TargetGetter(final Translator<? super GItem, ? extends GValue> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public GValue get(final Object item) {
			return this.that.toTarget(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class ProducerGetter<GValue> extends AbstractGetter<Object, GValue> {

		public final Producer<? extends GValue> that;

		public ProducerGetter(final Producer<? extends GValue> target) throws NullPointerException {
			this.that = Objects.notNull(target);
		}

		@Override
		public GValue get(final Object item) {
			return this.that.get();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode liefert den {@link EmptyGetter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Getter3<GItem, GValue> empty() {
		return (Getter3<GItem, GValue>)EmptyGetter.INSTANCE;
	}

	/** Diese Methode liefert den {@link NeutralGetter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Getter3<GItem, GItem> neutral() {
		return (Getter3<GItem, GItem>)NeutralGetter.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Getter} als {@link Getter3}. Wenn er {@code null} ist, wird dern {@link EmptyGetter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Getter3<GItem, GValue> from(final Getter<? super GItem, ? extends GValue> that) {
		if (that == null) return Getters.empty();
		if (that instanceof Getter3) return (Getter3<GItem, GValue>)that;
		return Getters.concat(that, Getters.<GValue>neutral());
	}

	/** Diese Methode liefert einen {@link Getter3} zu {@link Producer#get()} des gegebenen {@link Producer}. */
	public static <GValue> Getter3<Object, GValue> from(final Producer<? extends GValue> that) throws NullPointerException {
		return that == Producers.empty() ? Getters.<Object, GValue>empty() : new ProducerGetter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(String, boolean) Getters.fromNative(memberText, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Getters.fromNative(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see Getters#fromNative(java.lang.reflect.Field, boolean)
	 * @see Getters#fromNative(Method, boolean)
	 * @see Getters#fromNative(Constructor, boolean) */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Getters.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Getters.fromNative((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Getters.fromNative((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Getters.fromNative(field, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter) Getters.from(Fields.fromNative(that, forceAccessible))}.
	 *
	 * @see Fields#fromNative(java.lang.reflect.Field, boolean) */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final java.lang.reflect.Field that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Getters.from(Fields.<GItem, GValue>fromNative(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Getters.fromNative(that, true)}. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> fromNative(final Method that) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodGetter new MethodGetter<>(that, forceAccessible)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Method that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodGetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Constructor, boolean) Getters.fromNative(that, true)}. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> fromNative(final Constructor<?> that) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConstructorGetter new ConstructorGetter<>(that, forceAccessible)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Constructor<?> that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorGetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Getters.fromNative(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter) Getters.from(Fields.fromNative(fieldOwner, fieldName, forceAccessible))}.
	 *
	 * @see Fields#fromNative(Class, String, boolean) */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return Getters.from(Fields.<GItem, GValue>fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter) Getters.from(Producers.fromValue(that).toGetter())}.
	 *
	 * @see Producers#fromValue(Object) */
	public static <GValue> Getter3<Object, GValue> fromValue(final GValue that) {
		return Getters.from(Producers.fromValue(that).toGetter());
	}

	/** Diese Methode liefert einen {@link Getter3} zu {@link Filter#accept(Object)} des gegebenen {@link Filter}. Für einen Datenstz {@code item} liefert er
	 * {@code Boolean.valueOf(that.accept(item))}. */
	public static <GItem> Getter3<GItem, Boolean> fromFilter(final Filter<? super GItem> that) throws NullPointerException {
		return new FilterGetter<>(that);
	}

	/** Diese Methode gibt einen {@link Getter3} zu {@link Translator#toSource(Object)} des gegebenen {@link Translator} zurück. Für einen Datensatz {@code item}
	 * liefert er den Wert {@code that.toSource(item)}. */
	public static <GValue, GItem> Getter3<GItem, GValue> fromSource(final Translator<? extends GValue, ? super GItem> that) throws NullPointerException {
		return new SourceGetter<>(that);
	}

	/** Diese Methode gibt einen {@link Getter3} zu {@link Translator#toTarget(Object)} des gegebenen {@link Translator} zurück. Für einen Datensatz {@code item}
	 * liefert er den Wert {@code that.toTarget(item)}. */
	public static <GSource, GTarget> Getter3<GSource, GTarget> fromTarget(final Translator<? super GSource, ? extends GTarget> that) throws NullPointerException {
		return new TargetGetter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatGetter new ConcatGetter<>(that, trans)}. */
	public static <GItem, GValue2, GValue> Getter3<GItem, GValue> concat(final Getter<? super GItem, ? extends GValue2> that,
		final Getter<? super GValue2, ? extends GValue> trans) throws NullPointerException {
		return new ConcatGetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(Getter, int, Hasher) Getters.buffer(that, References.SOFT, Objects.HASHER)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> buffer(final Getter<? super GItem, ? extends GValue> that) throws NullPointerException {
		return Getters.buffer(that, References.SOFT, Objects.HASHER);
	}

	/** Diese Methode ist eine Abkürzung für {@link BufferedGetter new BufferedGetter<>(that, mode, hasher)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> buffer(final Getter<? super GItem, ? extends GValue> that, final int mode, final Hasher hasher)
		throws NullPointerException, IllegalArgumentException {
		return new BufferedGetter<>(that, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter, Getter) Getters.aggregate(that, Getters.neutral())}. */
	public static <GItem, GValue> Getter3<Iterable<? extends GItem>, GValue> aggregate(final Getter<? super GItem, ? extends GValue> that)
		throws NullPointerException {
		return Getters.aggregate(that, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Getter, Getter, Getter, Getter) Getters.aggregate(that, trans, Getters.empty(),
	 * Getters.empty())}. */
	public static <GItem, GValue, GValue2> Getter3<Iterable<? extends GItem>, GValue> aggregate(final Getter<? super GItem, ? extends GValue2> that,
		final Getter<? super GValue2, ? extends GValue> trans) throws NullPointerException {
		return Getters.aggregate(that, trans, Getters.<Object, GValue>empty(), Getters.<Object, GValue>empty());
	}

	/** Diese Methode ist eine Abkürzung für {@link AggregatedGetter new AggregatedGetter<>(that, trans, empty, mixed)}. */
	public static <GItem extends Iterable<? extends GItem2>, GValue, GItem2, GValue2> Getter3<GItem, GValue> aggregate(
		final Getter<? super GItem2, ? extends GValue2> that, final Getter<? super GValue2, ? extends GValue> trans,
		final Getter<? super GItem, ? extends GValue> empty, final Getter<? super GItem, ? extends GValue> mixed) throws NullPointerException {
		return new AggregatedGetter<>(that, trans, empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link #optionalize(Getter, Object) Getters.optionalize(that, null)}. **/
	public static <GItem, GValue> Getter3<GItem, GValue> optionalize(final Getter<? super GItem, GValue> that) throws NullPointerException {
		return Getters.optionalize(that, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedGetter new OptionalizedGetter<>(that, value)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> optionalize(final Getter<? super GItem, GValue> that, final GValue value) throws NullPointerException {
		return new OptionalizedGetter<>(that, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Getter, Object) Getters.synchronize(that, that)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> synchronize(final Getter<? super GItem, ? extends GValue> that) throws NullPointerException {
		return Getters.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedGetter new SynchronizedGetter<>(that, mutex)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> synchronize(final Getter<? super GItem, ? extends GValue> that, final Object mutex)
		throws NullPointerException {
		return new SynchronizedGetter<>(that, mutex);
	}

}
