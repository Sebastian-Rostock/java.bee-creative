package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Consumer}.
 *
 * @see Consumer
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
@SuppressWarnings ("javadoc")
public class Consumers {

	/** Diese Klasse implementiert einen {@link Consumers}, der das {@link #set Setzen} ignoriert. */
	public static class EmptyConsumer extends AbstractConsumer<Object> {

		public static final Consumer3<?> INSTANCE = new EmptyConsumer();

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, der das {@link #set(Object) Schreiben} an eine gegebene {@link Method nativen statische Methode}
	 * delegiert. Das Schreiben des Werts {@code value} erfolgt über {@code this.target.invoke(null, value)}.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class NativeConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Method target;

		/** Dieser Konstruktor initialisiert Methode und Zugreifbarkeit.
		 *
		 * @param target native statische Methode.
		 * @param forceAccessible {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}.
		 * @throws NullPointerException Wenn {@code method} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die Methode nicht statisch bzw. nicht zugreifbar ist oder keine passende Parameteranzahl besitzt. */
		public NativeConsumer(final Method target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(target.getModifiers()) || (target.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		public void set(final GValue value) {
			try {
				this.target.invoke(null, value);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Consumers#toTranslated(Consumer, Getter)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedConsumer<GValue, GValue2> extends AbstractConsumer<GValue2> {

		public final Consumer<? super GValue> target;

		public final Getter<? super GValue2, ? extends GValue> trans;

		public TranslatedConsumer(final Consumer<? super GValue> target, final Getter<? super GValue2, ? extends GValue> trans) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final GValue2 value) {
			this.target.set(this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.trans, this.target);
		}

	}

	/** Diese Klasse implementiert {@link Consumers#toSynchronized(Consumer, Object)} */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Consumer<? super GValue> target;

		public final Object mutex;

		public SynchronizedConsumer(final Consumer<? super GValue> target, final Object mutex) {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public void set(final GValue value) {
			synchronized (this.mutex) {
				this.target.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
		}

	}

	/** Diese Methode liefert {@link EmptyConsumer#INSTANCE}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Consumer3<GValue> empty() {
		return (Consumer3<GValue>)EmptyConsumer.INSTANCE;
	}

	public static <GValue> Consumer3<GValue> from(final Consumer<? super GValue> target) {
		if (target == null) return Consumers.empty();
		if (target instanceof Consumer3) return (Consumer3<GValue>)target;
		return Consumers.toTranslated(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#fromNative(String, boolean) onsumers.fromNative(memberText, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Consumers.fromNative(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code fromNative(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see #fromNative(java.lang.reflect.Field, boolean)
	 * @see #fromNative(java.lang.reflect.Method, boolean)
	 * @param <GValue> Typ des Werts.
	 * @param memberText Pfad einer Methode oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code memberText} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GValue> Consumer3<GValue> fromNative(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Consumers.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Consumers.fromNative((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#fromNative(java.lang.reflect.Field, boolean) Consumers.nativeConsumer(field, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final java.lang.reflect.Field field) {
		return Consumers.fromNative(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(java.lang.reflect.Field, boolean) Properties.nativeProperty(field,
	 * forceAccessible)}. */
	public static <GValue> Consumer3<GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible) {
		return Consumers.from(Properties.fromNative(field, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#fromNative(Method, boolean) Consumers.nativeConsumer(method, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Method method) {
		return Consumers.fromNative(method, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link NativeConsumer#NativeConsumer(Method, boolean) new NativeConsumer<>(target, forceAccessible)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Method target, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new NativeConsumer<>(target, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Consumers#fromNative(Class, String, boolean) Consumers.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Consumers.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Properties#fromNative(Class, String, boolean) Consumers.from(Properties.fromNative(fieldOwner, fieldName,
	 * forceAccessible))}. */
	public static <GValue> Consumer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Consumers.from(Properties.fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode gibt einen übersetzten {@link Consumer} zurück, dessen Wert mit Hilfe des gegebenen {@link Getter} in den Wert des gegebenen
	 * {@link Consumer} überführt wird.
	 *
	 * @param target {@link Consumer}.
	 * @param trans {@link Getter}.
	 * @param <GValue> Typ des Werts des gegebenen {@link Consumer} sowie des Werts gegebenen {@link Getter}.
	 * @param <GValue2> Typ des Datensatzes des gegebenen {@link Getter} sowie des Werts des gelieferten {@link Consumer}.
	 * @return {@code translated}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code consumer} {@code null} ist. */
	public static <GValue, GValue2> Consumer3<GValue2> toTranslated(final Consumer<? super GValue> target,
		final Getter<? super GValue2, ? extends GValue> trans) {
		return new TranslatedConsumer<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toSynchronized(Consumer, Object) Consumers.toSynchronized(consumer, consumer)}. */
	public static <GValue> Consumer3<GValue> toSynchronized(final Consumer<? super GValue> consumer) {
		return Consumers.toSynchronized(consumer, consumer);
	}

	/** Diese Methode gibt einen {@link Consumer} zurück, welcher den gegebenen {@link Consumer} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Consumer} als Synchronisationsobjekt verwendet.
	 *
	 * @param target {@link Consumer}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @return {@code synchronized}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code consumer} {@code null} ist. */
	public static <GValue> Consumer3<GValue> toSynchronized(final Consumer<? super GValue> target, final Object mutex) {
		return new SynchronizedConsumer<>(target, mutex);
	}

}
