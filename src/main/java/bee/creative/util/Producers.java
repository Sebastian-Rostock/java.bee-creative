package bee.creative.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;

/** Diese Klasse implementiert grundlegende {@link Producer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Klasse implementiert einen {@link Producer3}, welcher beim {@link #get() Lesen} stets {@code null} liefert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyProducer extends AbstractProducer<Object> {

		public static final Producer3<?> INSTANCE = new EmptyProducer();

	}

	/** Diese Klasse implementiert einen {@link Producer3}, welcher beim {@link #get() Lesen} stets einen gegebenen Wert liefert.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class ValueProducer<GValue> extends AbstractProducer<GValue> {

		public final GValue target;

		public ValueProducer(final GValue target) {
			this.target = target;
		}

		@Override
		public GValue get() {
			return this.target;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, welcher das {@link #get() Lesen} an eine gegebene {@link Method nativen statische Methode} delegiert.
	 * Das Lesen erfolgt über {@code this.target.invoke(null)}.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class MethodProducer<GValue> extends AbstractProducer<GValue> {

		public final Method target;

		/** Dieser Konstruktor initialisiert Methode und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public MethodProducer(final Method target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(target.getModifiers()) || (target.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.target.invoke(null);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, welcher das {@link #get() Lesen} an einen gegebenen {@link Method nativen statischen Konstruktor}
	 * delegiert. Das Lesen erfolgt über {@code this.target.newInstance()}.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorProducer<GValue> extends AbstractProducer<GValue> {

		public final Constructor<?> target;

		/** Dieser Konstruktor initialisiert Konstruktor und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public ConstructorProducer(final Constructor<?> target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(target.getModifiers()) || (target.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.target.newInstance();
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen puffernden {@link Producer3}, welcher den beim {@link #get() Lesen} einen Wert liefert, der von einem gegebenen
	 * {@link Producer} gelieferten und mit einem {@link Pointer} im gegebenenen Modus zwischenspeichert wird.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class BufferedProducer<GValue> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue> target;

		protected final int mode;

		protected Pointer<GValue> buffer;

		/** Dieser Konstruktor initialisiert das {@link Producer} und Zeigerstärke ({@link Pointers#HARD}, {@link Pointers#WEAK} oder {@link Pointers#SOFT}). */
		public BufferedProducer(final Producer<? extends GValue> target, final int mode) throws NullPointerException, IllegalArgumentException {
			Pointers.from(mode, null);
			this.mode = mode;
			this.target = Objects.notNull(target);
		}

		@Override
		public GValue get() {
			final Pointer<GValue> pointer = this.buffer;
			if (pointer != null) {
				final GValue data = pointer.get();
				if (data != null) return data;
				if (pointer == Pointers.NULL) return null;
			}
			final GValue data = this.target.get();
			this.buffer = Pointers.from(this.mode, data);
			return data;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mode);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Producer3}, welcher den beim {@link #get() Lesen} einen Wert liefert, der über einen gegebenen
	 * {@link Getter} aus dem Wert eines gegebenen {@link Producer} ermittelt wird. Das Lesen erfolgt über {@code this.trans.get(this.target.get())}.
	 *
	 * @param <GValue> Typ des Werts dieses {@link Producer3}.
	 * @param <GValue2> Typ des Werts des gegebenen {@link Producer}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedProducer<GValue, GValue2> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue2> target;

		public final Getter<? super GValue2, ? extends GValue> trans;

		public TranslatedProducer(final Producer<? extends GValue2> target, final Getter<? super GValue2, ? extends GValue> trans) throws NullPointerException {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public GValue get() {
			return this.trans.get(this.target.get());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans);
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, welcher einen gegebenen {@link Producer} über {@code synchronized(this.mutex)} synchronisiert. Wenn
	 * dieses Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProducer<GValue> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue> target;

		public final Object mutex;

		public SynchronizedProducer(final Producer<? extends GValue> target, final Object mutex) throws NullPointerException {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get() {
			synchronized (this.mutex) {
				return this.target.get();
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
		}

	}



	/** Diese Methode liefert {@link EmptyProducer EmptyProducer.INSTANCE}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Producer3<GValue> empty() {
		return (Producer3<GValue>)EmptyProducer.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Producer} als {@link Producer3}. Wenn er {@code null} ist, wird {@link #empty() Producers.empty()}
	 * geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Producer3<GValue> from(final Producer<? extends GValue> target) {
		if (target == null) return Producers.empty();
		if (target instanceof Producer3<?>) return (Producer3<GValue>)target;
		return Producers.toTranslated(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Object) Producers.from(target, null)}. */
	public static <GItem, GValue> Producer3<GValue> from(final Getter<? super GItem, ? extends GValue> target) throws NullPointerException {
		return Producers.from(target, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toTranslated(Producer, Getter) Producers.concat(Producers.fromValue(item), target)}. */
	public static <GItem, GValue> Producer3<GValue> from(final Getter<? super GItem, ? extends GValue> target, final GItem item) throws NullPointerException {
		return Producers.toTranslated(Producers.fromValue(item), target);
	}

	/** Diese Methode ist eine Abkürzung für {@link ValueProducer new ValueProducer<>(target)}. Wenn {@code target} {@code null} ist, wird
	 * {@link Producers#empty() Producers.empty()} geliefert. */
	public static <GValue> Producer3<GValue> fromValue(final GValue target) {
		if (target == null) return Producers.empty();
		return new ValueProducer<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(String, boolean) Producers.fromNative(memberPath, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Producers.fromNative(Natives.parse(memberPath), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see #fromNative(Class, boolean)
	 * @see #fromNative(java.lang.reflect.Field, boolean)
	 * @see #fromNative(Method, boolean)
	 * @see #fromNative(Constructor, boolean) */
	public static <GValue> Producer3<GValue> fromNative(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof Class<?>) return Producers.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof java.lang.reflect.Field) return Producers.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Producers.fromNative((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Producers.fromNative((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Producers.fromNative(target, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final java.lang.reflect.Field target) throws NullPointerException {
		return Producers.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field, boolean) Producers.from(Properties.fromNative(target,
	 * forceAccessible))}. */
	public static <GValue> Producer3<GValue> fromNative(final java.lang.reflect.Field target, final boolean forceAccessible) throws NullPointerException {
		return Producers.from(Properties.<GValue>fromNative(target, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Producers.fromNative(target, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Method target) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodProducer new MethodProducer<>(target, forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final Method target, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(target, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, boolean) Producers.fromNative(valueClass, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<? extends GValue> valueClass) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(valueClass, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Constructor, boolean) Producers.fromNative(Natives.parseConstructor(valueClass),
	 * forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<? extends GValue> valueClass, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(Natives.parseConstructor(valueClass), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Constructor, boolean) Producers.fromNative(target, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Constructor<?> target) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConstructorProducer new ConstructorProducer<>(target, forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final Constructor<?> target, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorProducer<>(target, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Producers.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Producers.from(Properties.fromNative(fieldOwner, fieldName,
	 * forceAccessible))}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.from(Properties.<GValue>fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #toBuffered(Producer, int) Producers.toBuffered(target, Pointers.SOFT)}. */
	public static <GValue> Producer3<GValue> toBuffered(final Producer<? extends GValue> target) throws NullPointerException {
		return Producers.toBuffered(target, Pointers.SOFT);
	}

	/** Diese Methode ist eine Abkürzung für {@link BufferedProducer new BufferedProducer<>(target, mode)}. */
	public static <GValue> Producer3<GValue> toBuffered(final Producer<? extends GValue> target, final int mode)
		throws NullPointerException, IllegalArgumentException {
		return new BufferedProducer<>(target, mode);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedProducer new TranslatedProducer<>(target, trans)}. */
	public static <GValue, GValue2> Producer3<GValue2> toTranslated(final Producer<? extends GValue> target,
		final Getter<? super GValue, ? extends GValue2> trans) throws NullPointerException {
		return new TranslatedProducer<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toSynchronized(Producer, Object) Producers.toSynchronized(target, target)}. */
	public static <GValue> Producer3<GValue> toSynchronized(final Producer<? extends GValue> target) throws NullPointerException {
		return Producers.toSynchronized(target, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedProducer new SynchronizedProducer<>(target, mutex)}. */
	public static <GValue> Producer3<GValue> toSynchronized(final Producer<? extends GValue> target, final Object mutex) throws NullPointerException {
		return new SynchronizedProducer<>(target, mutex);
	}

}
