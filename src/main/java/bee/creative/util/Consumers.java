package bee.creative.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Consumer}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Consumers {

	/** Diese Klasse implementiert einen {@link Consumer3}, der das {@link #set(Object) Schreiben} ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyConsumer extends AbstractConsumer<Object> {

		public static final Consumer3<?> INSTANCE = new EmptyConsumer();

	}

	/** Diese Klasse implementiert einen verketteten {@link Consumer3}, der beim {@link #set(Object) Schreiben} den gegebenen Wert an einen gegebenen
	 * {@link Setter} delegiert und dazu den von einem gegebenen {@link Producer} bereitgestellten Datensatz verwendet. Das Schreiben des Werts {@code value}
	 * erfolgt über {@code this.that.set(this.item.get(), value)}.
	 * 
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class SetterConsumer<GItem, GValue> extends AbstractConsumer<GValue> {

		public final Producer<? extends GItem> item;

		public final Setter<? super GItem, ? super GValue> that;

		public SetterConsumer(final Producer<? extends GItem> item, final Setter<? super GItem, ? super GValue> that) {
			this.item = Objects.notNull(item);
			this.that = Objects.notNull(that);
		}

		@Override
		public void set(final GValue value) {
			this.that.set(this.item.get(), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, der das {@link #set(Object) Schreiben} an eine gegebene {@link Method nativen statische Methode}
	 * delegiert. Das Schreiben des Werts {@code value} erfolgt über {@code this.that.invoke(null, value)}.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class MethodConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Method that;

		/** Dieser Konstruktor initialisiert Methode und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public MethodConsumer(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (!Modifier.isStatic(that.getModifiers()) || (that.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(that) : Objects.notNull(that);
		}

		@Override
		public void set(final GValue value) {
			try {
				this.that.invoke(null, value);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.that.isAccessible());
		}

	}

	/** Diese Klasse implementiert übersetzten {@link Consumer3}, der den Wert beim {@link #set(Object) Schreiben} über einen gegebenen {@link Getter} in den Wert
	 * eines gegebenen {@link Consumer} überführt. Das Schreiben des Werts {@code value} erfolgt über {@code this.that.set(this.trans.get(value))}.
	 *
	 * @param <GValue> Typ des Werts dieses {@link Consumer3}.
	 * @param <GValue2> Typ des Werts des gegebenen {@link Consumer}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedConsumer<GValue, GValue2> extends AbstractConsumer<GValue> {

		public final Consumer<? super GValue2> that;

		public final Getter<? super GValue, ? extends GValue2> trans;

		public TranslatedConsumer(final Consumer<? super GValue2> that, final Getter<? super GValue, ? extends GValue2> trans) {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final GValue value) {
			this.that.set(this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, der einen gegebenen {@link Consumer} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Consumer<? super GValue> that;

		public final Object mutex;

		public SynchronizedConsumer(final Consumer<? super GValue> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public void set(final GValue value) {
			synchronized (this.mutex) {
				this.that.set(value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	/** Diese Methode liefert den {@link EmptyConsumer}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Consumer3<GValue> empty() {
		return (Consumer3<GValue>)EmptyConsumer.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Consumer} als {@link Consumer3}. Wenn er {@code null} ist, wird der {@link EmptyConsumer} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Consumer3<GValue> from(final Consumer<? super GValue> target) {
		if (target == null) return Consumers.empty();
		if (target instanceof Consumer3<?>) return (Consumer3<GValue>)target;
		return Consumers.translate(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Setter, Object) Consumers.from(that, null)}. */
	public static <GItem, GValue> Consumer3<GValue> from(final Setter<? super GItem, ? super GValue> that) throws NullPointerException {
		return Consumers.from(that, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Producer, Setter) Consumers.from(Producers.fromValue(item), that)}.
	 * 
	 * @see Producers#fromValue(Object) */
	public static <GItem, GValue> Consumer3<GValue> from(final Setter<? super GItem, ? super GValue> that, final GItem item) throws NullPointerException {
		return Consumers.from(Producers.fromValue(item), that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SetterConsumer new SetterConsumer<>(item, that)}. */
	public static <GItem, GValue> Consumer3<GValue> from(final Producer<? extends GItem> item, final Setter<? super GItem, ? super GValue> that)
		throws NullPointerException {
		return new SetterConsumer<>(item, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(String, boolean) Consumers.fromNative(memberText, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Consumers.fromNative(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Consumers.fromNative(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see #fromNative(java.lang.reflect.Field, boolean)
	 * @see #fromNative(java.lang.reflect.Method, boolean) */
	public static <GValue> Consumer3<GValue> fromNative(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Consumers.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Consumers.fromNative((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Consumers.fromNative(that, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final java.lang.reflect.Field that) {
		return Consumers.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Consumer) Consumers.from(Properties.fromNative(that, forceAccessible))}.
	 * 
	 * @see Properties#fromNative(java.lang.reflect.Field, boolean) */
	public static <GValue> Consumer3<GValue> fromNative(final java.lang.reflect.Field that, final boolean forceAccessible) {
		return Consumers.from(Properties.fromNative(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Consumers.fromNative(that, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Method that) {
		return Consumers.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodConsumer new MethodConsumer<>(that, forceAccessible)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Method that, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return new MethodConsumer<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Consumers.fromNative(fieldOwner, fieldName, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName) throws NullPointerException, IllegalArgumentException {
		return Consumers.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Consumer) Consumers.from(Properties.fromNative(fieldOwner, fieldName, forceAccessible))}.
	 * 
	 * @see Properties#fromNative(Class, String, boolean) */
	public static <GValue> Consumer3<GValue> fromNative(final Class<?> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Consumers.from(Properties.fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedConsumer new TranslatedConsumer<>(that, trans)}. */
	public static <GValue, GValue2> Consumer3<GValue2> translate(final Consumer<? super GValue> that, final Getter<? super GValue2, ? extends GValue> trans)
		throws NullPointerException {
		return new TranslatedConsumer<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Consumer, Object) Consumers.synchronize(that, that)}. */
	public static <GValue> Consumer3<GValue> synchronize(final Consumer<? super GValue> that) {
		return Consumers.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedConsumer new SynchronizedConsumer<>(that, mutex)}. */
	public static <GValue> Consumer3<GValue> synchronize(final Consumer<? super GValue> that, final Object mutex) {
		return new SynchronizedConsumer<>(that, mutex);
	}

}
