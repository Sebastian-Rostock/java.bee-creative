package bee.creative.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;
import bee.creative.ref.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Producer}.
 *
 * @see Producer
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Producers {

	/** Diese Klasse implementiert {@link Producers#valueProducer(Object)}. */
	@SuppressWarnings ("javadoc")
	public static class ValueProducer<GValue> implements Producer<GValue> {

		public final GValue item;

		public ValueProducer(final GValue item) {
			this.item = item;
		}

		@Override
		public GValue get() {
			return this.item;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item);
		}

	}

	/** Diese Klasse implementiert {@link Producers#nativeProducer(Method)}. */
	@SuppressWarnings ("javadoc")
	public static class MethodProducer<GValue> implements Producer<GValue> {

		public final Method method;

		public MethodProducer(final Method method) {
			if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 0)) throw new IllegalArgumentException();
			this.method = method;
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
			return Objects.toInvokeString(this, Natives.formatMethod(this.method));
		}

	}

	/** Diese Klasse implementiert {@link Producers#nativeProducer(Constructor)}. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorProducer<GValue> implements Producer<GValue> {

		public final Constructor<?> constructor;

		public ConstructorProducer(final Constructor<?> constructor) {
			this.constructor = Objects.assertNotNull(constructor);
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
			return Objects.toInvokeString(this, Natives.formatConstructor(this.constructor));
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
			this.producer = Objects.assertNotNull(producer);
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

	/** Diese Klasse implementiert {@link Producers#navigatedBuilder(Getter, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class NavigatedProducer<GInput, GOutput> implements Producer<GOutput> {

		public final Getter<? super GInput, ? extends GOutput> navigator;

		public final Producer<? extends GInput> producer;

		public NavigatedProducer(final Getter<? super GInput, ? extends GOutput> navigator, final Producer<? extends GInput> producer) {
			this.navigator = Objects.assertNotNull(navigator);
			this.producer = Objects.assertNotNull(producer);
		}

		@Override
		public GOutput get() {
			return this.navigator.get(this.producer.get());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.navigator, this.producer);
		}

	}

	/** Diese Klasse implementiert {@link Producers#synchronizedProducer(Object, Producer)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedProducer<GValue> implements Producer<GValue> {

		public final Object mutex;

		public final Producer<? extends GValue> producer;

		public SynchronizedProducer(final Object mutex, final Producer<? extends GValue> producer) throws NullPointerException {
			this.mutex = mutex != null ? mutex : this;
			this.producer = Objects.assertNotNull(producer);
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

	/** Diese Methode gibt einen {@link Producer} zurück, der den gegebenen Datensatz bereitstellt.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param item Datensatz.
	 * @return {@code value}-{@link Producer}. */
	public static <GValue> Producer<GValue> valueProducer(final GValue item) {
		return new ValueProducer<>(item);
	}

	/** Diese Methode ist eine Abkürzung für {@code nativeProducer(Natives.parse(memberText))}.
	 *
	 * @see Natives#parse(String)
	 * @see #nativeProducer(java.lang.reflect.Method)
	 * @see #nativeProducer(java.lang.reflect.Constructor)
	 * @param <GValue> Typ des Datensatzes.
	 * @param memberText Methoden- oder Konstruktortext.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws ReflectiveOperationException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst. */
	public static <GValue> Producer<GValue> nativeProducer(final String memberText)
		throws NullPointerException, IllegalArgumentException, ReflectiveOperationException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Method) return Producers.nativeProducer((java.lang.reflect.Method)object);
		if (object instanceof java.lang.reflect.Constructor<?>) return Producers.nativeProducer((java.lang.reflect.Constructor<?>)object);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt einen {@link Producer} zur gegebenen {@link java.lang.reflect.Method nativen statischen Methode} zurück.<br>
	 * Der vom gelieferten {@link Producer} erzeugte Datensatz entspricht {@code method.invoke(null)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GValue> Typ des Datensatzes.
	 * @param method Native statische Methode.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Methode nicht statisch oder nicht parameterlos ist. */
	public static <GValue> Producer<GValue> nativeProducer(final java.lang.reflect.Method method) throws NullPointerException, IllegalArgumentException {
		return new MethodProducer<>(method);
	}

	/** Diese Methode gibt einen {@link Producer} zum gegebenen {@link java.lang.reflect.Constructor nativen Kontruktor} zurück.<br>
	 * Der vom gelieferten {@link Producer} erzeugte Datensatz entspricht {@code constructor.newInstance()}.
	 *
	 * @see java.lang.reflect.Constructor#newInstance(Object...)
	 * @param <GValue> Typ des Datensatzes.
	 * @param constructor Nativer Kontruktor.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static <GValue> Producer<GValue> nativeProducer(final java.lang.reflect.Constructor<?> constructor) throws NullPointerException {
		return new ConstructorProducer<>(constructor);
	}

	/** Diese Methode gibt einen gepufferten {@link Producer} zurück, der den vonm gegebenen {@link Producer} erzeugten Datensatz mit Hilfe eines
	 * {@link SoftPointer} verwaltet.
	 *
	 * @see #bufferedProducer(int, Producer)
	 * @param <GValue> Typ des Datensatzes.
	 * @param producer {@link Producer}.
	 * @return {@code buffered}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist. */
	public static <GValue> Producer<GValue> bufferedProducer(final Producer<? extends GValue> producer) throws NullPointerException {
		return Producers.bufferedProducer(Pointers.SOFT, producer);
	}

	/** Diese Methode gibt einen gepufferten {@link Producer} zurück, der den vonm gegebenen {@link Producer} erzeugten Datensatz mit Hilfe eines {@link Pointer}
	 * im gegebenenen Modus verwaltet.
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

	/** Diese Methode gibt einen umgewandelten {@link Producer} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Getter} aus dem Datensatz des gegebenen
	 * {@link Producer} ermittelt wird.
	 *
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Producer} sowie der Eingabe des gegebenen {@link Getter}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Getter} sowie des Datensatzes.
	 * @param navigator {@link Getter}.
	 * @param producer {@link Producer}.
	 * @return {@code navigated}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code producer} {@code null} ist. */
	public static <GInput, GOutput> Producer<GOutput> navigatedBuilder(final Getter<? super GInput, ? extends GOutput> navigator,
		final Producer<? extends GInput> producer) throws NullPointerException {
		return new NavigatedProducer<>(navigator, producer);
	}

	/** Diese Methode ist eine Abkürzung für {@code synchronizedProducer(producer, producer)}.
	 *
	 * @see #synchronizedProducer(Producer, Object) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Producer<GValue> synchronizedProducer(final Producer<? extends GValue> producer) throws NullPointerException {
		return Producers.synchronizedProducer(producer, producer);
	}

	/** Diese Methode gibt einen synchronisierten {@link Producer} zurück, der den gegebenen {@link Producer} via {@code synchronized(mutex)} synchronisiert.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param producer {@link Producer}.
	 * @param mutex Synchronisationsobjekt.
	 * @return {@code synchronized}-{@link Producer}.
	 * @throws NullPointerException Wenn der {@code producer} bzw. {@code mutex} {@code null} ist. */
	public static <GValue> Producer<GValue> synchronizedProducer( final Object mutex,final Producer<? extends GValue> producer) throws NullPointerException {
		return new SynchronizedProducer<>(mutex, producer);
	}

	public static <GValue> Getter<Object, GValue> toGetter(final Producer<? extends GValue> producer) throws NullPointerException {
		return null;
	}

}
