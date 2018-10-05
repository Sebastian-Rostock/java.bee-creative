package bee.creative.util;

import java.lang.reflect.Constructor;
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
	@SuppressWarnings ("javadoc")
	public static abstract class BaseConsumer<GValue> extends BaseObject implements Consumer<GValue> {
	}

	public static class NativeConsumer<GValue> extends BaseConsumer<GValue> {

		public final Method method;

		public NativeConsumer(final java.lang.reflect.Method method) {
			if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			try {
				method.setAccessible(true);
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
			return Objects.toInvokeString(this, this.method);
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

	/** Diese Methode ist eine Abkürzung für {@code Properties.emptyProperty()}. */
	@SuppressWarnings ("javadoc")
	public static <GValue> Consumer<GValue> emptyConsumer() {
		return Properties.emptyProperty();
	}

	/** Diese Methode ist eine Abkürzung für {@code nativeProducer(Natives.parse(memberText))}, wobei eine {@link Class} bzw. ein {@link Constructor} zu einer
	 * Ausnahme führt.
	 *
	 * @see Natives#parse(String)
	 * @see #nativeConsumer(java.lang.reflect.Field)
	 * @see #nativeConsumer(java.lang.reflect.Method)
	 * @param <GValue> Typ des Datensatzes.
	 * @param memberText Methoden- oder Konstruktortext.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst. */
	public static <GValue> Consumer<GValue> nativeConsumer(final String memberText) throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Consumers.nativeConsumer((java.lang.reflect.Field)object);
		if (object instanceof java.lang.reflect.Method) return Consumers.nativeConsumer((java.lang.reflect.Method)object);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@code Properties.nativeProperty(field)}. */
	@SuppressWarnings ("javadoc")
	public static <GValue> Consumer<GValue> nativeConsumer(final java.lang.reflect.Field field) {
		return Properties.nativeProperty(field);
	}

	/** Diese Methode gibt einen {@link Consumer} zur gegebenen {@link java.lang.reflect.Method nativen statischen Methode} zurück.<br>
	 * Das Schreiben des Werts {@code value} erfolgt über {@code method.invoke(null, value)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param method Methode zum Schreiben der Eigenschaft.
	 * @return {@code native}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Methode keine passende Parameteranzahl besitzen. */
	public static <GValue> Consumer<GValue> nativeConsumer(final java.lang.reflect.Method method) {
		return new NativeConsumer<>(method);
	}
	
	
	
	/** Diese Methode gibt einen umgewandelten {@link Consumer} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Getter} in dem Datensatz des gegebenen
	 * {@link Consumer} überführt wird.
	 *
	 * @param <GSource> Typ des Datensatzes des gegebenen {@link Producer} sowie der Eingabe des gegebenen {@link Getter}.
	 * @param <GTarget> Typ der Ausgabe des gegebenen {@link Getter} sowie des Datensatzes.
	 * @param toSource {@link Getter}.
	 * @param consumer {@link Consumer}.
	 * @return {@code translated}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code producer} {@code null} ist. */


	public static <GTarget, GSource> Consumer<GTarget> translatedConsumer(final Getter<? super GTarget, ? extends GSource> toSource,
		final Consumer<? super GSource> consumer) {
		return new TranslatedConsumer<>(toSource, consumer);
	}

	/** Diese Methode ist eine Abkürzung für {@code Consumers.synchronizedConsumer(consumer, consumer)}.
	 *
	 * @see #synchronizedConsumer(Object, Consumer) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Consumer<GValue> synchronizedConsumer(final Consumer<? super GValue> consumer) {
		return Consumers.synchronizedConsumer(consumer, consumer);
	}

	/** Diese Methode gibt einen {@link Consumer} zurück, welcher den gegebenen {@link Consumer} via {@code synchronized(mutex)} synchronisiert. Wenn das
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

	/** Diese Methode gibt einen {@link Setter} zurück, der seine Eingabe ignoriert und den Wert des gegebenen {@link Consumer} setzt.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param consumer {@link Consumer}.
	 * @return {@link Consumer}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code consumer} {@code null} ist. */
	public static <GValue> Setter<Object, GValue> toSetter(final Consumer<? super GValue> consumer) {
		return new ConsumerSetter<>(consumer);
	}

	/** Diese Methode ist eine Abkürzung für {@code Properties.compositeProperty(Properties.emptyProperty(), consumer)}.
	 *
	 * @see Properties#compositeProperty(Producer, Consumer) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Property<GValue> toProperty(final Consumer<? super GValue> consumer) {
		return Properties.compositeProperty(Properties.<GValue>emptyProperty(), consumer);
	}

}
