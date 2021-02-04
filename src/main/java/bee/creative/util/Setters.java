package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Setter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	/** Diese Klasse implementiert einen {@link Setter3}, welcher das {@link #set(Object, Object) Schreiben} ignoriert. */
	@SuppressWarnings ("javadoc")
	public static class EmptySetter extends AbstractSetter<Object, Object> {

		public static final Setter3<?, ?> INSTANCE = new EmptySetter();

	}

	/** Diese Klasse implementiert einen {@link Setter3}, welcher das {@link #set(Object, Object) Schreiben} nur dann an einen gegebenen {@link Setter} delegiert,
	 * wenn die Eingabe nicht {@code null} ist.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class DefaultSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Setter<? super GItem, ? super GValue> target;

		public DefaultSetter(final Setter<? super GItem, ? super GValue> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (item == null) return;
			this.target.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	/** Diese Klasse implementiert einen {@link Setter3}, welcher das {@link #set(Object, Object) Schreiben} an eine gegebene {@link Method nativen Methode}
	 * delegiert. Bei einer Klassenmethode erfolgt das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} über
	 * {@link Method#invoke(Object, Object...) method.invoke(null, item, value)}, bei einer Objektmethode hingegen über {@link Method#invoke(Object, Object...)
	 * method.invoke(item, value)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class MethodSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Method target;

		public MethodSetter(final Method method, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			if (method.getParameterTypes().length != (Modifier.isStatic(method.getModifiers()) ? 2 : 1)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(method) : Objects.notNull(method);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				if (Modifier.isStatic(this.target.getModifiers())) {
					this.target.invoke(null, item, value);
				} else {
					this.target.invoke(item, value);
				}
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, welcher das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen
	 * {@link Getter} übersetzten Datensatz an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes
	 * {@code item} erfolgt über {@code this.target.set(this.trans.get(item), value)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GItem2> Typ des Datensatzes des gegebenen {@link Setter}.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedSetter1<GItem, GItem2, GValue> extends AbstractSetter<GItem, GValue> {

		public final Setter<? super GItem2, ? super GValue> target;

		public final Getter<? super GItem, ? extends GItem2> trans;

		public TranslatedSetter1(final Setter<? super GItem2, ? super GValue> target, final Getter<? super GItem, ? extends GItem2> trans)
			throws NullPointerException {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.target.set(this.trans.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, welcher das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen
	 * {@link Getter} übersetzten Wert an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes
	 * {@code item} erfolgt über {@code this.target.set(item, this.trans.get(value))}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param <GValue2> Typ des Datensatzes des Übersetzers sowie des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedSetter2<GItem, GValue, GValue2> extends AbstractSetter<GItem, GValue> {

		public final Setter<? super GItem, ? super GValue2> target;

		public final Getter<? super GValue, ? extends GValue2> trans;

		public TranslatedSetter2(final Setter<? super GItem, ? super GValue2> target, final Getter<? super GValue, ? extends GValue2> trans) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.target.set(item, this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans);
		}

	}

	/** Diese Klasse implementiert einen aggregierten {@link Setter3}, welcher das {@link #set(Iterable, Object) Schreiben} mit dem über einen gegebenen
	 * {@link Getter} übersetzten Wert für jedes Element des iterierbaren Datensatzes an einen gegebenen {@link Setter} delegeirt. Wenn der iterierbare Datensatz
	 * {@code null} oder leer ist, wird das Setzen ignoriert.
	 *
	 * @param <GItem> Typ der Elemente des iterierbaren Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param <GValue2> Typ des Werts der Eigenschaft der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class AggregatedSetter<GItem, GValue, GValue2> extends AbstractSetter<Iterable<? extends GItem>, GValue> {

		public final Setter<? super GItem, GValue2> target;

		public final Getter<? super GValue, ? extends GValue2> trans;

		public AggregatedSetter(final Setter<? super GItem, GValue2> target, final Getter<? super GValue, ? extends GValue2> trans) throws NullPointerException {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final Iterable<? extends GItem> item, final GValue value) {
			if (item == null) return;
			final Iterator<? extends GItem> iterator = item.iterator();
			if (!iterator.hasNext()) return;
			final GValue2 value2 = this.trans.get(value);
			do {
				this.target.set(iterator.next(), value2);
			} while (iterator.hasNext());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans);
		}

	}

	/** Diese Klasse implementiert einen {@link Consumer3}, welcher einen gegebenen {@link Consumer} über {@code synchronized(this.mutex)} synchronisiert. Wenn
	 * dieses Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Setter<? super GItem, ? super GValue> target;

		public final Object mutex;

		public SynchronizedSetter(final Setter<? super GItem, ? super GValue> target, final Object mutex) throws NullPointerException {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			synchronized (this.mutex) {
				this.target.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
		}

	}

	static class ConsumerSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Consumer<? super GValue> target;

		public ConsumerSetter(final Consumer<? super GValue> target) {
			this.target = Objects.notNull(target);
		}

		@Override
		public void set(final GItem input, final GValue value) {
			this.target.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	/** Diese Methode liefert den {@link EmptySetter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Setter3<GItem, GValue> empty() {
		return (Setter3<GItem, GValue>)EmptySetter.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Consumer} als {@link Consumer3}. Wenn er {@code null} ist, wird der {@link EmptySetter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Setter3<GItem, GValue> from(final Setter<? super GItem, ? super GValue> target) {
		if (target == null) return Setters.empty();
		if (target instanceof Setter3) return (Setter3<GItem, GValue>)target;
		return Setters.translate(Getters.<GItem>neutral(), target);
	}

	/** Diese Methode liefert einen {@link Setter3}, welcher beim {@link Setter#set(Object, Object)} den Datensatz ignoriert und den Wert an den gegebenen
	 * {@link Consumer} delegiert. */
	public static <GItem, GValue> Setter3<GItem, GValue> from(final Consumer<? super GValue> target) throws NullPointerException {
		return new ConsumerSetter<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(String, boolean) Setters.fromNative(memberPath, true)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Setters.fromNative(Natives.parse(memberPath), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see Setters#fromNative(java.lang.reflect.Field, boolean)
	 * @see Setters#fromNative(Method, boolean) */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof java.lang.reflect.Field) return Setters.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Setters.fromNative((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field) fromNative.nativeField(target, true)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final java.lang.reflect.Field target) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Setter) Setters.from(Fields.fromNative(target, forceAccessible))}.
	 *
	 * @see Fields#fromNative(java.lang.reflect.Field, boolean) */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final java.lang.reflect.Field target, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Setters.from(Fields.fromNative(target, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Setters.fromNative(target, true)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final Method target) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(target, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodSetter new MethodSetter<>(target, forceAccessible)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final Method target, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodSetter<>(target, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Setters.fromNative(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Setter) Setters.from(Fields.fromNative(fieldOwner, fieldName, forceAccessible))}.
	 *
	 * @see Fields#fromNative(Class, String, boolean) */
	public static <GItem, GValue> Setter3<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return Setters.from(Fields.fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link DefaultSetter new DefaultSetter<>(target)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> toDefault(final Setter<? super GItem, GValue> target) throws NullPointerException {
		return new DefaultSetter<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSetter1 new TranslatedSetter1<>(target, trans)}. */
	public static <GTarget, GSource, GValue> Setter3<GTarget, GValue> translate(final Getter<? super GTarget, ? extends GSource> trans,
		final Setter<? super GSource, ? super GValue> target) throws NullPointerException {
		return new TranslatedSetter1<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSetter2 new TranslatedSetter2<>(target, trans)}. */
	public static <GItem, GValue, GValue2> Setter3<GItem, GValue> translate(final Setter<? super GItem, ? super GValue2> target,
		final Getter<? super GValue, ? extends GValue2> trans) throws NullPointerException {
		return new TranslatedSetter2<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toAggregated(Setter, Getter) Setters.toAggregated(target, Getters.neutral())}. */
	public static <GItem, GValue> Setter3<Iterable<? extends GItem>, GValue> toAggregated(final Setter<? super GItem, ? super GValue> target)
		throws NullPointerException {
		return Setters.toAggregated(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link AggregatedSetter new AggregatedSetter<>(target, trans)}. */
	public static <GItem, GValue, GValue2> Setter3<Iterable<? extends GItem>, GValue> toAggregated(final Setter<? super GItem, ? super GValue2> target,
		final Getter<? super GValue, ? extends GValue2> trans) throws NullPointerException {
		return new AggregatedSetter<>(target, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Setter, Object) Setters.synchronize(target, target)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> synchronize(final Setter<? super GItem, ? super GValue> target) throws NullPointerException {
		return Setters.synchronize(target, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedSetter new SynchronizedSetter<>(target, mutex)}. */
	public static <GItem, GValue> Setter3<GItem, GValue> synchronize(final Setter<? super GItem, ? super GValue> target, final Object mutex)
		throws NullPointerException {
		return new SynchronizedSetter<>(target, mutex);
	}

}
