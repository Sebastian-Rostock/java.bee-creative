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
public class Consumers {

	/** Diese Klasse implementiert einen {@link Consumer3}, welcher das {@link #set(Object) Schreiben} ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyConsumer extends AbstractConsumer<Object> {

		public static final Consumer3<?> INSTANCE = new EmptyConsumer();

	}

	/** Diese Klasse implementiert einen verketteten {@link Consumer3}, welcher beim {@link #set(Object) Schreiben} den gegebenen Wert an einen gegebenen
	 * {@link Setter} delegiert und dazu den von einem gegebenen {@link Producer} bereitgestellten Datensatz verwendet. Das Schreiben des Werts {@code value}
	 * erfolgt über {@code this.target.set(this.source.get(), value)}.
	 * 
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class ConcatConsumer<GItem, GValue> extends AbstractConsumer<GValue> {

		public final Producer<? extends GItem> source;

		public final Setter<? super GItem, ? super GValue> target;

		public ConcatConsumer(final Producer<? extends GItem> source, final Setter<? super GItem, ? super GValue> target) {
			this.source = Objects.notNull(source);
			this.target = Objects.notNull(target);
		}

		@Override
		public void set(final GValue value) {
			this.target.set(this.source.get(), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.source, this.target);
		}

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, welcher das {@link #set(Object) Schreiben} an eine gegebene {@link Method nativen statische Methode}
	 * delegiert. Das Schreiben des Werts {@code value} erfolgt über {@code this.target.invoke(null, value)}.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class MethodConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Method target;

		/** Dieser Konstruktor initialisiert Methode und {@link Natives#forceAccessible(AccessibleObject) Zugreifbarkeit}. */
		public MethodConsumer(final Method target, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
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

	/** Diese Klasse implementiert übersetzten {@link Consumer3}, welcher den Wert beim {@link #set(Object) Schreiben} über einen gegebenen {@link Getter} in den
	 * Wert eines gegebenen {@link Consumer} überführt. Das Schreiben des Werts {@code value} erfolgt über {@code this.target.set(this.trans.get(value))}.
	 *
	 * @param <GValue> Typ des Werts dieses {@link Consumer3}.
	 * @param <GValue2> Typ des Werts des gegebenen {@link Consumer}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedConsumer<GValue, GValue2> extends AbstractConsumer<GValue> {

		public final Consumer<? super GValue2> target;

		public final Getter<? super GValue, ? extends GValue2> trans;

		public TranslatedConsumer(final Consumer<? super GValue2> target, final Getter<? super GValue, ? extends GValue2> trans) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final GValue value) {
			this.target.set(this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.trans, this.target);
		}

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, welcher einen gegebenen {@link Consumer} über {@code synchronized(this.mutex)} synchronisiert. Wenn
	 * dieses Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GValue> Typ des Werts. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedConsumer<GValue> extends AbstractConsumer<GValue> {

		public final Consumer<? super GValue> target;

		public final Object mutex;

		public SynchronizedConsumer(final Consumer<? super GValue> target, final Object mutex) throws NullPointerException {
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

	/** Diese Methode liefert einen {@link EmptyConsumer}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Consumer3<GValue> empty() {
		return (Consumer3<GValue>)EmptyConsumer.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatConsumer new ConcatConsumer<>(source, target)}. */
	public static <GItem, GValue> Consumer3<GValue> concat(final Producer<? extends GItem> source, final Setter<? super GItem, ? super GValue> target)
		throws NullPointerException {
		return new ConcatConsumer<>(source, target);
	}

	/** Diese Methode liefert den gegebenen {@link Consumer} als {@link Consumer3}. Wenn er {@code null} ist, wird {@link #empty() Consumers.empty()}
	 * geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Consumer3<GValue> from(final Consumer<? super GValue> target) {
		if (target == null) return Consumers.empty();
		if (target instanceof Consumer3<?>) return (Consumer3<GValue>)target;
		return Consumers.toTranslated(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Setter, Object) Consumers.from(target, null)}. */
	public static <GItem, GValue> Consumer3<GValue> from(final Setter<? super GItem, ? super GValue> target) throws NullPointerException {
		return Consumers.from(target, null);
	}

	/** Diese Methode ist eine Abkürzung für {@link #concat(Producer, Setter) Consumers.concat(Producers.fromValue(item), target)}.
	 * 
	 * @see Producers#fromValue(Object) */
	public static <GItem, GValue> Consumer3<GValue> from(final Setter<? super GItem, ? super GValue> target, final GItem item) throws NullPointerException {
		return Consumers.concat(Producers.fromValue(item), target);
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

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field, boolean) Consumers.fromNative(target, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final java.lang.reflect.Field target) {
		return Consumers.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Consumer) Consumers.from(Properties.fromNative(target, forceAccessible))}.
	 * 
	 * @see Properties#fromNative(java.lang.reflect.Field, boolean) */
	public static <GValue> Consumer3<GValue> fromNative(final java.lang.reflect.Field target, final boolean forceAccessible) {
		return Consumers.from(Properties.fromNative(target, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Consumers.fromNative(target, true)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Method target) {
		return Consumers.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodConsumer new MethodConsumer<>(target, forceAccessible)}. */
	public static <GValue> Consumer3<GValue> fromNative(final Method target, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodConsumer<>(target, forceAccessible);
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

	/** Diese Methode ist eine Abkürzung für {@link TranslatedConsumer new TranslatedConsumer<>(target, trans)}. */
	public static <GValue, GValue2> Consumer3<GValue2> toTranslated(final Consumer<? super GValue> target, final Getter<? super GValue2, ? extends GValue> trans)
		throws NullPointerException {
		return new TranslatedConsumer<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toSynchronized(Consumer, Object) Consumers.toSynchronized(target, target)}. */
	public static <GValue> Consumer3<GValue> toSynchronized(final Consumer<? super GValue> target) {
		return Consumers.toSynchronized(target, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedConsumer new SynchronizedConsumer<>(target, mutex)}. */
	public static <GValue> Consumer3<GValue> toSynchronized(final Consumer<? super GValue> target, final Object mutex) {
		return new SynchronizedConsumer<>(target, mutex);
	}

}
