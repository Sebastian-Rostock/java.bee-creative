package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;

/** Diese Klasse implementiert grundlegende {@link Producer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Klasse implementiert einen abstrakten {@link Producer} als {@link BaseObject}. */
	public static abstract class BaseProducer<GValue> extends BaseObject implements Producer<GValue> {
	}

	/** Diese Klasse implementiert {@link Producers#nativeProducer(Method)}. */
	@SuppressWarnings ("javadoc")
	public static class MethodProducer<GValue> implements Producer<GValue> {

		public final Method method;

		public MethodProducer(final Method method, final boolean forceAccessible) {
			if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.method = forceAccessible ? Natives.forceAccessible(method) : Objects.notNull(method);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.method.invoke(null);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.method, this.method.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Producers#nativeProducer(Constructor)}. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorProducer<GValue> implements Producer<GValue> {

		public final Constructor<?> constructor;

		public ConstructorProducer(final Constructor<?> constructor, final boolean forceAccessible) {
			if (!Modifier.isStatic(constructor.getModifiers()) || (constructor.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.constructor = forceAccessible ? Natives.forceAccessible(constructor) : Objects.notNull(constructor);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get() {
			try {
				return (GValue)this.constructor.newInstance();
			} catch (IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.constructor, this.constructor.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Producers#bufferedProducer(int, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class BufferedProducer<GValue> implements Producer<GValue> {

		public final Producer<? extends GValue> producer;

		public final int mode;

		protected Pointer<GValue> pointer;

		public BufferedProducer(final int mode, final Producer<? extends GValue> producer) {
			Pointers.pointer(mode, null);
			this.mode = mode;
			this.producer = Objects.notNull(producer);
		}

		@Override
		public GValue get() {
			final Pointer<GValue> pointer = this.pointer;
			if (pointer != null) {
				final GValue data = pointer.get();
				if (data != null) return data;
				if (pointer == Pointers.NULL) return null;
			}
			final GValue data = this.producer.get();
			this.pointer = Pointers.pointer(this.mode, data);
			return data;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.mode, this.producer);
		}

	}

	/** Diese Klasse implementiert {@link Producers#translatedProducer(Getter, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedProducer<GSource, GTarget> implements Producer<GTarget> {

		public final Producer<? extends GSource> producer;

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public TranslatedProducer(final Getter<? super GSource, ? extends GTarget> toTarget, final Producer<? extends GSource> producer) {
			this.producer = Objects.notNull(producer);
			this.toTarget = Objects.notNull(toTarget);
		}

		@Override
		public GTarget get() {
			return this.toTarget.get(this.producer.get());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toTarget, this.producer);
		}

	}

	/** Diese Klasse implementiert {@link Producers#synchronizedProducer(Object, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProducer<GValue> implements Producer<GValue> {

		public final Object mutex;

		public final Producer<? extends GValue> producer;

		public SynchronizedProducer(final Object mutex, final Producer<? extends GValue> producer) throws NullPointerException {
			this.mutex = Objects.notNull(mutex, this);
			this.producer = Objects.notNull(producer);
		}

		@Override
		public GValue get() {
			synchronized (this.mutex) {
				return this.producer.get();
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.producer);
		}

	}

	/** Diese Klasse implementiert {@link Producers#toGetter(Producer)}. */
	@SuppressWarnings ("javadoc")
	static class ProducerGetter<GValue> implements Getter<Object, GValue> {

		public final Producer<? extends GValue> producer;

		public ProducerGetter(final Producer<? extends GValue> producer) {
			this.producer = Objects.notNull(producer);
		}

		@Override
		public GValue get(final Object input) {
			return this.producer.get();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.producer);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#valueProducer(Object) Producers.valueProducer(null)}. */
	public static <GValue> Producer<GValue> emptyProducer() {
		return Producers.valueProducer(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#valueProperty(Object) Properties.valueProperty(value)}. */
	public static <GValue> Producer<GValue> valueProducer(final GValue value) {
		return Properties.valueProperty(value);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(String, boolean) Producers.nativeProducer(memberPath, true)}. */
	public static <GValue> Producer<GValue> nativeProducer(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Producers.nativeProducer(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Producers.nativeProducer(Natives.parse(memberPath), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see #nativeProducer(Class, boolean)
	 * @see #nativeProducer(java.lang.reflect.Field, boolean)
	 * @see #nativeProducer(Method, boolean)
	 * @see #nativeProducer(Constructor, boolean)
	 * @param <GValue> Typ des Werts.
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktors oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GValue> Producer<GValue> nativeProducer(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof Class<?>) return Producers.nativeProducer((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof java.lang.reflect.Field) return Producers.nativeProducer((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Producers.nativeProducer((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Producers.nativeProducer((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(java.lang.reflect.Field, boolean) Producers.nativeProducer(field, true)}. */
	public static <GValue> Producer<GValue> nativeProducer(final java.lang.reflect.Field field) throws NullPointerException {
		return Producers.nativeProducer(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(java.lang.reflect.Field, boolean) Properties.nativeProperty(field,
	 * forceAccessible)}. */
	public static <GValue> Producer<GValue> nativeProducer(final java.lang.reflect.Field field, final boolean forceAccessible) throws NullPointerException {
		return Properties.nativeProperty(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(Method, boolean) Producers.nativeProducer(method, true)}. */
	public static <GValue> Producer<GValue> nativeProducer(final Method method) throws NullPointerException, IllegalArgumentException {
		return Producers.nativeProducer(method, true);
	}

	/** Diese Methode gibt einen {@link Producer} zur gegebenen {@link Method nativen statischen Methode} zurück. Der vom gelieferten {@link Producer} erzeugte
	 * Wert entspricht {@code method.invoke(null)}.
	 *
	 * @see Method#invoke(Object, Object...)
	 * @param <GValue> Typ des Datensatzes.
	 * @param method Native statische Methode.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Methode nicht statisch, nicht parameterlos oder nicht zugreifbar ist. */
	public static <GValue> Producer<GValue> nativeProducer(final Method method, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(Class, boolean) Producers.nativeProducer(valueClass, true)}. */
	public static <GValue> Producer<GValue> nativeProducer(final Class<? extends GValue> valueClass) throws NullPointerException, IllegalArgumentException {
		return Producers.nativeProducer(valueClass, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(Constructor, boolean) Producers.nativeProducer(Natives.parseConstructor(valueClass),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseConstructor(Class, Class...) */
	public static <GValue> Producer<GValue> nativeProducer(final Class<? extends GValue> valueClass, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.nativeProducer(Natives.parseConstructor(valueClass), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(Constructor, boolean) Producers.nativeProducer(constructor, true)}. */
	public static <GValue> Producer<GValue> nativeProducer(final Constructor<?> constructor) throws NullPointerException, IllegalArgumentException {
		return Producers.nativeProducer(constructor, true);
	}

	/** Diese Methode gibt einen {@link Producer} zum gegebenen {@link Constructor nativen statischen parameterlosen Kontruktor} zurück. Der vom gelieferten
	 * {@link Producer} erzeugte Datensatz entspricht {@link Constructor#newInstance(Object...) constructor.newInstance()}.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param constructor Nativer Kontruktor.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Kontruktor nicht statisch, nicht parameterlos oder nicht zugreifbar ist. */
	public static <GValue> Producer<GValue> nativeProducer(final Constructor<?> constructor, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorProducer<>(constructor, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#nativeProducer(Class, String, boolean) Producers.nativeProducer(fieldOwner, fieldName, true)}. */
	public static <GValue> Producer<GValue> nativeProducer(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Producers.nativeProducer(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(Class, String, boolean) Properties.nativeProperty(fieldOwner, fieldName,
	 * forceAccessible)}. */
	public static <GValue> Producer<GValue> nativeProducer(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(fieldOwner, fieldName, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#bufferedProducer(int, Producer) Producers.bufferedProducer(Pointers.SOFT, producer)}. */
	public static <GValue> Producer<GValue> bufferedProducer(final Producer<? extends GValue> producer) throws NullPointerException {
		return Producers.bufferedProducer(Pointers.SOFT, producer);
	}

	/** Diese Methode gibt einen gepufferten {@link Producer} zurück, der den vom gegebenen {@link Producer} erzeugten Wert mit Hilfe eines {@link Pointer} im
	 * gegebenenen Modus verwaltet.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param producer {@link Producer}.
	 * @return {@code buffered}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code mode} ungültig ist. */
	public static <GValue> Producer<GValue> bufferedProducer(final int mode, final Producer<? extends GValue> producer)
		throws NullPointerException, IllegalArgumentException {
		return new BufferedProducer<>(mode, producer);
	}

	/** Diese Methode gibt einen übersetzten {@link Producer} zurück, dessen Wert mit Hilfe des gegebenen {@link Getter} aus dem Wert des gegebenen
	 * {@link Producer} erzeugt wird.
	 *
	 * @param <GSource> Typ des Werts des gegebenen {@link Producer} sowie des Datensatzs des gegebenen {@link Getter}.
	 * @param <GTarget> Typ des Werts des gegebenen {@link Getter} sowie des erzeugten {@link Producer}.
	 * @param toTarget {@link Getter} zum Übersetzen des Wert.
	 * @param producer {@link Producer}.
	 * @return {@code translated}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code toTarget} bzw. {@code producer} {@code null} ist. */
	public static <GSource, GTarget> Producer<GTarget> translatedProducer(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Producer<? extends GSource> producer) throws NullPointerException {
		return new TranslatedProducer<>(toTarget, producer);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedProducer(Object, Producer) Producers.synchronizedProducer(producer, producer)}. */
	public static <GValue> Producer<GValue> synchronizedProducer(final Producer<? extends GValue> producer) throws NullPointerException {
		return Producers.synchronizedProducer(producer, producer);
	}

	/** Diese Methode gibt einen synchronisierten {@link Producer} zurück, der den gegebenen {@link Producer} über {@code synchronized(mutex)} synchronisiert.
	 * Wenn das Synchronisationsobjekt {@code null} ist, wird das erzeugte {@link Property} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param producer {@link Producer}.
	 * @return {@code synchronized}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist. */
	public static <GValue> Producer<GValue> synchronizedProducer(final Object mutex, final Producer<? extends GValue> producer) throws NullPointerException {
		return new SynchronizedProducer<>(mutex, producer);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der seinen Datensatz ignoriert und den Wert des gegebenen {@link Producer} liefert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param producer {@link Producer}.
	 * @return {@link Producer}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist. */
	public static <GValue> Getter<Object, GValue> toGetter(final Producer<? extends GValue> producer) throws NullPointerException {
		return new ProducerGetter<>(producer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#compositeProperty(Producer, Consumer) Properties.compositeProperty(producer,
	 * Consumers.emptyConsumer())}.
	 *
	 * @see Consumers#emptyConsumer() */
	public static <GValue> Property<GValue> toProperty(final Producer<? extends GValue> producer) {
		return Properties.compositeProperty(producer, Consumers.<GValue>emptyConsumer());
	}

}
