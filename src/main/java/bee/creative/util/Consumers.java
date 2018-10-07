package bee.creative.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.util.Objects.BaseObject;
import bee.creative.util.Setters.BaseSetter;

/** Diese Klasse implementiert grundlegende {@link Consumer}.
 *
 * @see Consumer
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Consumers {

	/** Diese Klasse implementiert einen abstrakten {@link Consumer} als {@link BaseObject}. */
	public static abstract class BaseConsumer<GValue> extends BaseObject implements Consumer<GValue> {
	}

	/** Diese Klasse implementiert {@link Consumers#nativeConsumer(Method, boolean)} */
	@SuppressWarnings ("javadoc")
	public static class NativeConsumer<GValue> extends BaseConsumer<GValue> {

		public final Method method;

		public NativeConsumer(final Method method, final boolean forceAccessible) {
			if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			try {
				method.setAccessible(forceAccessible);
			} catch (final SecurityException cause) {
				throw new IllegalArgumentException(cause);
			}
			this.method = method;
		}

		@Override
		public void set(final GValue value) {
			try {
				this.method.invoke(null, value);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.method, this.method.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Consumers#translatedConsumer(Getter, Consumer)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedConsumer<GSource, GTarget> extends BaseConsumer<GTarget> {

		public final Consumer<? super GSource> consumer;

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public TranslatedConsumer(final Getter<? super GTarget, ? extends GSource> toSource, final Consumer<? super GSource> consumer) {
			this.toSource = Objects.notNull(toSource);
			this.consumer = Objects.notNull(consumer);
		}

		@Override
		public void set(final GTarget value) {
			this.consumer.set(this.toSource.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.consumer);
		}

	}

	/** Diese Klasse implementiert {@link Consumers#synchronizedConsumer(Object, Consumer)} */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedConsumer<GValue> extends BaseConsumer<GValue> {

		public final Object mutex;

		public final Consumer<? super GValue> consumer;

		public SynchronizedConsumer(final Object mutex, final Consumer<? super GValue> consumer) {
			this.mutex = Objects.notNull(mutex, this);
			this.consumer = Objects.notNull(consumer);
		}

		@Override
		public void set(final GValue value) {
			synchronized (this.mutex) {
				this.consumer.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.consumer);
		}

	}

	/** Diese Klasse implementiert {@link Consumers#toSetter(Consumer)} */
	@SuppressWarnings ("javadoc")
	static class ConsumerSetter<GValue> extends BaseSetter<Object, GValue> {

		public final Consumer<? super GValue> consumer;

		public ConsumerSetter(final Consumer<? super GValue> consumer) {
			this.consumer = Objects.notNull(consumer);
		}

		@Override
		public void set(final Object input, final GValue value) {
			this.consumer.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.consumer);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#emptyProperty() Properties.emptyProperty()}. */
	public static <GValue> Consumer<GValue> emptyConsumer() {
		return Properties.emptyProperty();
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#nativeConsumer(String, boolean) Consumers.nativeConsumer(memberText, true)}. */
	public static <GValue> Consumer<GValue> nativeConsumer(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Consumers.nativeConsumer(memberText, true);
	}

	/** Diese Methode ist eine Abkürzung für {@code nativeProducer(Natives.parse(memberText))}.
	 *
	 * @see Natives#parse(String)
	 * @see #nativeConsumer(java.lang.reflect.Field, boolean)
	 * @see #nativeConsumer(Method, boolean)
	 * @param <GValue> Typ des Werts.
	 * @param memberText Pfad einer Methode oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code memberText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GValue> Consumer<GValue> nativeConsumer(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Consumers.nativeConsumer((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Consumers.nativeConsumer((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#nativeConsumer(java.lang.reflect.Field, boolean) Consumers.nativeConsumer(field, true)}. */
	public static <GValue> Consumer<GValue> nativeConsumer(final java.lang.reflect.Field field) {
		return Consumers.nativeConsumer(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(java.lang.reflect.Field, boolean) Properties.nativeProperty(field,
	 * forceAccessible)}. */
	public static <GValue> Consumer<GValue> nativeConsumer(final java.lang.reflect.Field field, final boolean forceAccessible) {
		return Properties.nativeProperty(field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#nativeConsumer(Method, boolean) Consumers.nativeConsumer(method, true)}. */
	public static <GValue> Consumer<GValue> nativeConsumer(final Method method) {
		return Consumers.nativeConsumer(method, true);
	}

	/** Diese Methode gibt einen {@link Consumer} zur gegebenen {@link Method nativen statischen Methode} zurück. Das Schreiben des Werts {@code value} erfolgt
	 * über {@code method.invoke(null, value)}.
	 *
	 * @see Method#invoke(Object, Object...)
	 * @param <GValue> Typ des Werts.
	 * @param method Methode zum Schreiben.
	 * @return {@code native}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Methode nicht zugreifbar ist oder keine passende Parameteranzahl besitzt. */
	public static <GValue> Consumer<GValue> nativeConsumer(final Method method, final boolean forceAccessible) {
		return new NativeConsumer<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#nativeConsumer(Class, String, boolean) Consumers.NativeConsumer(fieldOwner, fieldName, true)}. */
	public static <GValue> Consumer<GValue> nativeConsumer(final Class<?> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Consumers.nativeConsumer(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#nativeProperty(Class, String, boolean) Properties.nativeProperty(fieldOwner, fieldName,
	 * forceAccessible)}. */
	public static <GValue> Consumer<GValue> nativeConsumer(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Properties.nativeProperty(fieldOwner, fieldName, forceAccessible);
	}

	/** Diese Methode gibt einen übersetzten {@link Consumer} zurück, dessen Wert mit Hilfe des gegebenen {@link Getter} in den Wert des gegebenen
	 * {@link Consumer} überführt wird.
	 *
	 * @param <GSource> Typ des Werts des gegebenen {@link Consumer} sowie des Werts gegebenen {@link Getter}.
	 * @param <GTarget> Typ des Datensatzes des gegebenen {@link Getter} sowie des Werts des gelieferten {@link Consumer}.
	 * @param toSource {@link Getter}.
	 * @param consumer {@link Consumer}.
	 * @return {@code translated}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code consumer} {@code null} ist. */
	public static <GTarget, GSource> Consumer<GTarget> translatedConsumer(final Getter<? super GTarget, ? extends GSource> toSource,
		final Consumer<? super GSource> consumer) {
		return new TranslatedConsumer<>(toSource, consumer);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedConsumer(Object, Consumer) Consumers.synchronizedConsumer(consumer, consumer)}. */
	public static <GValue> Consumer<GValue> synchronizedConsumer(final Consumer<? super GValue> consumer) {
		return Consumers.synchronizedConsumer(consumer, consumer);
	}

	/** Diese Methode gibt einen {@link Consumer} zurück, welcher den gegebenen {@link Consumer} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Consumer} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param consumer {@link Consumer}.
	 * @return {@code synchronized}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code consumer} {@code null} ist. */
	public static <GValue> Consumer<GValue> synchronizedConsumer(final Object mutex, final Consumer<? super GValue> consumer) {
		return new SynchronizedConsumer<>(mutex, consumer);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, der seinen Datensatz ignoriert und den Wert des gegebenen {@link Consumer} setzt.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param consumer {@link Consumer}.
	 * @return {@link Consumer}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code consumer} {@code null} ist. */
	public static <GValue> Setter<Object, GValue> toSetter(final Consumer<? super GValue> consumer) {
		return new ConsumerSetter<>(consumer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#compositeProperty(Producer, Consumer) Properties.compositeProperty(Producers.emptyProducer(),
	 * consumer)}.
	 *
	 * @see Producers#emptyProducer() */
	public static <GValue> Property<GValue> toProperty(final Consumer<? super GValue> consumer) {
		return Properties.compositeProperty(Producers.<GValue>emptyProducer(), consumer);
	}

}
