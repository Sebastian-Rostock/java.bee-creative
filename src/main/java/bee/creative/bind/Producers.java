package bee.creative.bind;

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

	/** Diese Klasse implementiert {@link Producers#empty()}. */
	@SuppressWarnings ("javadoc")
	public static class EmptyProducer extends AbstractProducer<Object> {

		public static final Producer3<?> INSTANCE = new EmptyProducer();

	}

	/** Diese Klasse implementiert {@link Producers#fromValue(Object)}. */
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

	/** Diese Klasse implementiert {@link Producers#fromNative(Method)}. */
	@SuppressWarnings ("javadoc")
	public static class MethodProducer<GValue> extends AbstractProducer<GValue> {

		public final Method target;

		public MethodProducer(final Method method, final boolean forceAccessible) {
			if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(method) : Objects.notNull(method);
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

	/** Diese Klasse implementiert {@link Producers#fromNative(Constructor)}. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorProducer<GValue> extends AbstractProducer<GValue> {

		public final Constructor<?> target;

		public ConstructorProducer(final Constructor<?> constructor, final boolean forceAccessible) {
			if (!Modifier.isStatic(constructor.getModifiers()) || (constructor.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(constructor) : Objects.notNull(constructor);
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

	/** Diese Klasse implementiert {@link Producers#toBuffered(Producer, int)}. */
	@SuppressWarnings ("javadoc")
	public static class BufferedProducer<GValue> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue> target;

		protected final int mode;

		protected Pointer<GValue> buffer;

		public BufferedProducer(final Producer<? extends GValue> target, final int mode) {
			Pointers.pointer(mode, null);
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
			this.buffer = Pointers.pointer(this.mode, data);
			return data;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mode);
		}

	}

	/** Diese Klasse implementiert {@link Producers#toTranslated(Producer, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedProducer<GValue, GValue2> extends AbstractProducer<GValue> {

		public final Producer<? extends GValue2> target;

		public final Getter<? super GValue2, ? extends GValue> trans;

		public TranslatedProducer(final Producer<? extends GValue2> target, final Getter<? super GValue2, ? extends GValue> trans) {
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

	/** Diese Klasse implementiert {@link Producers#toSynchronized(Producer, Object)}. */
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

	/** Diese Klasse implementiert {@link Producers#from(Getter, Object)}. */
	static class GetterProducer<GItem, GValue> extends AbstractProducer<GValue> {

		public final GItem item;

		public final Getter<? super GItem, ? extends GValue> getter;

		public GetterProducer(final GItem item, final Getter<? super GItem, ? extends GValue> getter) {
			this.item = item;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get() {
			return this.getter.get(this.item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.getter);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromValue(Object) Producers.valueProducer(null)}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Producer3<GValue> empty() {
		return (Producer3<GValue>)EmptyProducer.INSTANCE;
	}

	public static <GValue> Producer3<GValue> from(final Producer<? extends GValue> target) {
		if (target == null) return Producers.empty();
		if (target instanceof Producer3) return (Producer3<GValue>)target;
		return Producers.toTranslated(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#from(Getter, Object) Getters.toProducer(null, getter)}. */
	public static <GItem, GValue> Producer2<GValue> from(final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException {
		return Producers.from(getter, null);
	}

	/** Diese Methode gibt einen {@link Producer} zurück, der mit dem gegebenen Datensatz an den gegebenen {@link Getter} delegiert.
	 *
	 * @param getter {@link Getter}.
	 * @param item Datensatz.
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @return {@link Getter}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem, GValue> Producer2<GValue> from(final Getter<? super GItem, ? extends GValue> getter, final GItem item) throws NullPointerException {
		return new GetterProducer<>(item, getter);
	}

	public static <GValue> Producer3<GValue> fromValue(final GValue value) {
		if (value == null) return Producers.empty();
		return new ValueProducer<>(value);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(String, boolean) Producers.nativeProducer(memberPath, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Producers.nativeProducer(Natives.parse(memberPath), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see #fromNative(Class, boolean)
	 * @see #fromNative(java.lang.reflect.Field, boolean)
	 * @see #fromNative(Method, boolean)
	 * @see #fromNative(Constructor, boolean)
	 * @param <GValue> Typ des Werts.
	 * @param memberPath Pfad einer Klasse, einer Methode, eines Konstruktors oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GValue> Producer3<GValue> fromNative(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof Class<?>) return Producers.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof java.lang.reflect.Field) return Producers.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Producers.fromNative((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Producers.fromNative((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(java.lang.reflect.Field, boolean) Producers.nativeProducer(field, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final java.lang.reflect.Field field) throws NullPointerException {
		return Producers.fromNative(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field, boolean) Properties.nativeProperty(field,
	 * forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible) throws NullPointerException {
		return Producers.from(Properties.<GValue>fromNative(field, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(Method, boolean) Producers.nativeProducer(method, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Method method) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(method, true);
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
	public static <GValue> Producer3<GValue> fromNative(final Method method, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(Class, boolean) Producers.nativeProducer(valueClass, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<? extends GValue> valueClass) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(valueClass, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(Constructor, boolean) Producers.nativeProducer(Natives.parseConstructor(valueClass),
	 * forceAccessible)}.
	 *
	 * @see Natives#parseConstructor(Class, Class...) */
	public static <GValue> Producer3<GValue> fromNative(final Class<? extends GValue> valueClass, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(Natives.parseConstructor(valueClass), forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(Constructor, boolean) Producers.nativeProducer(constructor, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Constructor<?> constructor) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(constructor, true);
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
	public static <GValue> Producer3<GValue> fromNative(final Constructor<?> constructor, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorProducer<>(constructor, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#fromNative(Class, String, boolean) Producers.nativeProducer(fieldOwner, fieldName, true)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Producers.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(Class, String, boolean) Properties.nativeProperty(fieldOwner, fieldName,
	 * forceAccessible)}. */
	public static <GValue> Producer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Producers.from(Properties.<GValue>fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Producers#toBuffered(Producer, int) Producers.bufferedProducer(Pointers.SOFT, producer)}. */
	public static <GValue> Producer3<GValue> toBuffered(final Producer<? extends GValue> target) throws NullPointerException {
		return Producers.toBuffered(target, Pointers.SOFT);
	}

	/** Diese Methode gibt einen gepufferten {@link Producer} zurück, der den vom gegebenen {@link Producer} erzeugten Wert mit Hilfe eines {@link Pointer} im
	 * gegebenenen Modus verwaltet.
	 *
	 * @param target {@link Producer}.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param <GValue> Typ des Datensatzes.
	 * @return {@code buffered}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code mode} ungültig ist. */
	public static <GValue> Producer3<GValue> toBuffered(final Producer<? extends GValue> target, final int mode)
		throws NullPointerException, IllegalArgumentException {
		return new BufferedProducer<>(target, mode);
	}

	/** Diese Methode gibt einen übersetzten {@link Producer} zurück, dessen Wert mit Hilfe des gegebenen {@link Getter} aus dem Wert des gegebenen
	 * {@link Producer} erzeugt wird.
	 *
	 * @param target {@link Producer}.
	 * @param trans {@link Getter} zum Übersetzen des Wert.
	 * @param <GValue> Typ des Werts des gegebenen {@link Producer} sowie des Datensatzs des gegebenen {@link Getter}.
	 * @param <GValue2> Typ des Werts des gegebenen {@link Getter} sowie des erzeugten {@link Producer}.
	 * @return {@code translated}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code toTarget} bzw. {@code producer} {@code null} ist. */
	public static <GValue, GValue2> Producer3<GValue2> toTranslated(final Producer<? extends GValue> target,
		final Getter<? super GValue, ? extends GValue2> trans) throws NullPointerException {
		return new TranslatedProducer<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toSynchronized(Producer, Object) Producers.synchronizedProducer(producer, producer)}. */
	public static <GValue> Producer3<GValue> toSynchronized(final Producer<? extends GValue> target) throws NullPointerException {
		return Producers.toSynchronized(target, target);
	}

	/** Diese Methode gibt einen synchronisierten {@link Producer} zurück, der den gegebenen {@link Producer} über {@code synchronized(mutex)} synchronisiert.
	 * Wenn das Synchronisationsobjekt {@code null} ist, wird das erzeugte {@link Property} als Synchronisationsobjekt verwendet.
	 *
	 * @param target {@link Producer}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param <GValue> Typ des Werts.
	 * @return {@code synchronized}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist. */
	public static <GValue> Producer3<GValue> toSynchronized(final Producer<? extends GValue> target, final Object mutex) throws NullPointerException {
		return new SynchronizedProducer<>(target, mutex);
	}

}
