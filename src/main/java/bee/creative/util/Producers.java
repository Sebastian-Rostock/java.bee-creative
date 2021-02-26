package bee.creative.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Producer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Klasse implementiert einen {@link Producer3}, der beim {@link #get() Lesen} stets {@code null} liefert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyProducer extends AbstractProducer<Object> {

		public static final Producer3<?> INSTANCE = new EmptyProducer();

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der beim {@link #get() Lesen} stets einen gegebenen Wert liefert.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class ValueProducer<GValue> extends AbstractProducer<GValue> {

		public final GValue that;

		public ValueProducer(final GValue that) {
			this.that = that;
		}

		@Override
		public GValue get() {
			return this.that;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der das {@link #get() Lesen} an eine gegebene {@link Method nativen statische Methode} delegiert. Das
	 * Lesen erfolgt über {@code this.that.invoke(null)}.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class MethodProducer<GValue> extends AbstractProducer<GValue> {

		public final Method that;

		/** Dieser Konstruktor initialisiert Methode und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public MethodProducer(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.that.invoke(null);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.that.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der das {@link #get() Lesen} an einen gegebenen {@link Method nativen statischen Konstruktor}
	 * delegiert. Das Lesen erfolgt über {@code this.that.newInstance()}.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorProducer<GValue> extends AbstractProducer<GValue> {

		public final Constructor<?> that;

		/** Dieser Konstruktor initialisiert Konstruktor und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public ConstructorProducer(final Constructor<?> that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.that.newInstance();
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.that.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Producer3}, der beim {@link #get() Lesen} einen Wert liefert, der über einen gegebenen {@link Getter}
	 * aus dem Wert eines gegebenen {@link Producer} ermittelt wird. Das Lesen erfolgt über {@code this.trans.get(this.that.get())}.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param <GValue2> Typ des Werts des gegebenen {@link Producer}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedProducer<GValue, GValue2> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue2> that;

		public final Getter<? super GValue2, ? extends GValue> trans;

		public TranslatedProducer(final Producer<? extends GValue2> that, final Getter<? super GValue2, ? extends GValue> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public GValue get() {
			return this.trans.get(this.that.get());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen {@link Producer3}, der einen gegebenen {@link Producer} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProducer<GValue> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue> that;

		public final Object mutex;

		public SynchronizedProducer(final Producer<? extends GValue> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get() {
			synchronized (this.mutex) {
				return this.that.get();
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	/** Diese Methode liefert den {@link EmptyProducer}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Producer3<GValue> empty() {
		return (Producer3<GValue>)EmptyProducer.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Producer} als {@link Producer3}. Wenn er {@code null} ist, wird {@link #empty() Producers.empty()}
	 * geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Producer3<GValue> from(final Producer<? extends GValue> that) {
		if (that == null) return Producers.empty();
		if (that instanceof Producer3<?>) return (Producer3<GValue>)that;
		return Producers.translate(that, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Getter, Object) Producers.from(that, null)}. */
	public static <GItem, GValue> Producer3<GValue> from(final Getter<? super GItem, ? extends GValue> that) throws NullPointerException {
		return Producers.from(that, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #translate(Producer, Getter) Producers.translate(Producers.fromValue(item), that)}. */
	public static <GItem, GValue> Producer3<GValue> from(final Getter<? super GItem, ? extends GValue> that, final GItem item) throws NullPointerException {
		return Producers.translate(Producers.fromValue(item), that);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@link ValueProducer new ValueProducer<>(that)}. Wenn {@code that} {@code null} ist, wird der
	 * {@link EmptyProducer} geliefert. */
	public static <GValue> Producer3<GValue> fromValue(final GValue that) {
		if (that == null) return Producers.empty();
		return new ValueProducer<>(that);
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

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Producers.fromNative(that, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final java.lang.reflect.Field that) throws NullPointerException {
		return Producers.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field, boolean) Producers.from(Properties.fromNative(that,
	 * forceAccessible))}. */
	public static <GValue> Producer3<GValue> fromNative(final java.lang.reflect.Field that, final boolean forceAccessible) throws NullPointerException {
		return Producers.from(Properties.<GValue>fromNative(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Producers.fromNative(that, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Method that) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodProducer new MethodProducer<>(that, forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(that, forceAccessible);
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

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Constructor, boolean) Producers.fromNative(that, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Constructor<?> that) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConstructorProducer new ConstructorProducer<>(that, forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final Constructor<?> that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorProducer<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Producers.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(fieldOwner, fieldName, true);
	}

	/** MAMA Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Producers.from(Properties.fromNative(fieldOwner, fieldName,
	 * forceAccessible))}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.from(Properties.<GValue>fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#buffer(Getter) Getters.from(that).buffer().toProducer()}.
	 *
	 * @see Getters#from(Producer) */
	public static <GValue> Producer3<GValue> buffer(final Producer<? extends GValue> that) throws NullPointerException {
		return Getters.from(that).buffer().toProducer();
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#buffer(Getter, int, Hasher) Getters.from(that).buffer(mode, hasher).toProducer()}.
	 *
	 * @see Getters#from(Producer) */
	public static <GValue> Producer3<GValue> buffer(final Producer<? extends GValue> that, final int mode, final Hasher hasher)
		throws NullPointerException, IllegalArgumentException {
		return Getters.from(that).buffer(mode, hasher).toProducer();
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedProducer new TranslatedProducer<>(that, trans)}. */
	public static <GValue, GValue2> Producer3<GValue2> translate(final Producer<? extends GValue> that, final Getter<? super GValue, ? extends GValue2> trans)
		throws NullPointerException {
		return new TranslatedProducer<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Producer, Object) Producers.synchronize(that, that)}. */
	public static <GValue> Producer3<GValue> synchronize(final Producer<? extends GValue> that) throws NullPointerException {
		return Producers.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedProducer new SynchronizedProducer<>(that, mutex)}. */
	public static <GValue> Producer3<GValue> synchronize(final Producer<? extends GValue> that, final Object mutex) throws NullPointerException {
		return new SynchronizedProducer<>(that, mutex);
	}

}
