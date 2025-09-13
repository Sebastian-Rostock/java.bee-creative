package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Setter}.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	/** Diese Methode liefert den {@link EmptySetter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> emptySetter() {
		return (Setter3<ITEM, VALUE>)EmptySetter.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Setter} als {@link Setter}. Wenn er {@code null} ist, wird der {@link EmptySetter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFrom(Setter<? super ITEM, ? super VALUE> that) {
		if (that == null) return Setters.emptySetter();
		if (that instanceof Setter3) return (Setter3<ITEM, VALUE>)that;
		return translate(Getters.neutralGetter(), that);
	}

	/** Diese Methode liefert einen {@link Setter3}, der beim {@link Setter#set(Object, Object) Schreiben} den Datensatz ignoriert und den Wert an den gegebenen
	 * {@link Consumer} delegiert. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFrom(Consumer<? super VALUE> that) throws NullPointerException {
		return new ConsumerSetter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(String, boolean) Setters.fromNative(memberPath, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Setters.fromNative(Natives.parse(memberPath), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see Setters#fromNative(java.lang.reflect.Field, boolean)
	 * @see Setters#fromNative(Method, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final var object = Natives.parse(memberPath);
		if (object instanceof java.lang.reflect.Field) return Setters.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Setters.fromNative((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(java.lang.reflect.Field) fromNative.nativeField(that, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFrom(Setter) Setters.from(Fields.fromNative(that, forceAccessible))}.
	 *
	 * @see Fields#fromNative(java.lang.reflect.Field, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final java.lang.reflect.Field that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Setters.setterFrom(Fields.fromNative(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Method, boolean) Setters.fromNative(that, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final Method that) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodSetter new MethodSetter<>(that, forceAccessible)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final Method that, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodSetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromNative(Class, String, boolean) Setters.fromNative(fieldOwner, fieldName, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final Class<? extends ITEM> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFrom(Setter) Setters.from(Fields.fromNative(fieldOwner, fieldName, forceAccessible))}.
	 *
	 * @see Fields#fromNative(Class, String, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> fromNative(final Class<? extends ITEM> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Setters.setterFrom(Fields.fromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSetter1 new TranslatedSetter1<>(that, trans)}. */
	public static <GTarget, GSource, VALUE> Setter3<GTarget, VALUE> translate(final Getter<? super GTarget, ? extends GSource> trans,
		final Setter<? super GSource, ? super VALUE> that) throws NullPointerException {
		return new TranslatedSetter1<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSetter2 new TranslatedSetter2<>(that, trans)}. */
	public static <ITEM, VALUE, VALUE2> Setter3<ITEM, VALUE> translate(final Setter<? super ITEM, ? super VALUE2> that,
		final Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
		return new TranslatedSetter2<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregate(Setter, Getter) Setters.aggregate(that, Getters.neutral())}. */
	public static <ITEM, VALUE> Setter3<Iterable<? extends ITEM>, VALUE> aggregate(final Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
		return Setters.aggregate(that, Getters.<VALUE>neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link AggregatedSetter new AggregatedSetter<>(that, trans)}. */
	public static <ITEM, VALUE, VALUE2> Setter3<Iterable<? extends ITEM>, VALUE> aggregate(final Setter<? super ITEM, ? super VALUE2> that,
		final Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
		return new AggregatedSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedSetter new OptionalizedSetter<>(that)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> optionalize(final Setter<? super ITEM, VALUE> that) throws NullPointerException {
		return new OptionalizedSetter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Setter, Object) Setters.synchronize(that, that)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> synchronize(final Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
		return Setters.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedSetter new SynchronizedSetter<>(that, mutex)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> synchronize(final Setter<? super ITEM, ? super VALUE> that, final Object mutex) throws NullPointerException {
		return new SynchronizedSetter<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} ignoriert. */
	public static class EmptySetter extends AbstractSetter<Object, Object> {

		public static final Setter3<?, ?> INSTANCE = new EmptySetter();

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} an eine gegebene {@link Method nativen Methode}
	 * delegiert. Bei einer Klassenmethode erfolgt das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} über
	 * {@link Method#invoke(Object, Object...) this.that.invoke(null, item, value)}, bei einer Objektmethode hingegen über {@link Method#invoke(Object, Object...)
	 * this.that.invoke(item, value)}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class MethodSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public final Method that;

		public final boolean forceAccessible;

		public MethodSetter(final Method method, final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (method.getParameterTypes().length != (Modifier.isStatic(method.getModifiers()) ? 2 : 1)) throw new IllegalArgumentException();
			this.that = forceAccessible ? Natives.forceAccessible(method) : Objects.notNull(method);
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			try {
				if (Modifier.isStatic(this.that.getModifiers())) {
					this.that.invoke(null, item, value);
				} else {
					this.that.invoke(item, value);
				}
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.forceAccessible);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, der das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen {@link Getter}
	 * übersetzten Datensatz an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item}
	 * erfolgt über {@code this.that.set(this.trans.get(item), value)}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <ITEM2> Typ des Datensatzes des gegebenen {@link Setter}.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class TranslatedSetter1<ITEM, ITEM2, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public final Setter<? super ITEM2, ? super VALUE> that;

		public final Getter<? super ITEM, ? extends ITEM2> trans;

		public TranslatedSetter1(final Setter<? super ITEM2, ? super VALUE> that, final Getter<? super ITEM, ? extends ITEM2> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			this.that.set(this.trans.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, der das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen {@link Getter}
	 * übersetzten Wert an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} erfolgt
	 * über {@code this.that.set(item, this.trans.get(value))}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft.
	 * @param <VALUE2> Typ des Datensatzes des Übersetzers sowie des Werts der Eigenschaft. */
	public static class TranslatedSetter2<ITEM, VALUE, VALUE2> extends AbstractSetter<ITEM, VALUE> {

		public final Setter<? super ITEM, ? super VALUE2> that;

		public final Getter<? super VALUE, ? extends VALUE2> trans;

		public TranslatedSetter2(final Setter<? super ITEM, ? super VALUE2> that, final Getter<? super VALUE, ? extends VALUE2> trans) {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			this.that.set(item, this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen aggregierten {@link Setter3}, der das {@link #set(Iterable, Object) Schreiben} mit dem über einen gegebenen
	 * {@link Getter} übersetzten Wert für jedes Element des iterierbaren Datensatzes an einen gegebenen {@link Setter} delegeirt. Wenn der iterierbare Datensatz
	 * {@code null} oder leer ist, wird das Setzen ignoriert.
	 *
	 * @param <ITEM> Typ der Elemente des iterierbaren Datensatzes.
	 * @param <VALUE> Typ des Werts.
	 * @param <VALUE2> Typ des Werts der Eigenschaft der Elemente. */
	public static class AggregatedSetter<ITEM, VALUE, VALUE2> extends AbstractSetter<Iterable<? extends ITEM>, VALUE> {

		public final Setter<? super ITEM, VALUE2> that;

		public final Getter<? super VALUE, ? extends VALUE2> trans;

		public AggregatedSetter(final Setter<? super ITEM, VALUE2> that, final Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public void set(final Iterable<? extends ITEM> item, final VALUE value) {
			if (item == null) return;
			final Iterator<? extends ITEM> iterator = item.iterator();
			if (!iterator.hasNext()) return;
			final VALUE2 value2 = this.trans.get(value);
			do {
				this.that.set(iterator.next(), value2);
			} while (iterator.hasNext());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} nur dann an einen gegebenen {@link Setter} delegiert,
	 * wenn die Eingabe nicht {@code null} ist.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class OptionalizedSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public final Setter<? super ITEM, ? super VALUE> that;

		public OptionalizedSetter(final Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			if (item == null) return;
			this.that.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der einen gegebenen {@link Setter} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class SynchronizedSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public final Setter<? super ITEM, ? super VALUE> that;

		public final Object mutex;

		public SynchronizedSetter(final Setter<? super ITEM, ? super VALUE> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public void set(final ITEM item, final VALUE value) {
			synchronized (this.mutex) {
				this.that.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

	}

	static class ConsumerSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public final Consumer<? super VALUE> that;

		public ConsumerSetter(final Consumer<? super VALUE> that) {
			this.that = Objects.notNull(that);
		}

		@Override
		public void set(final ITEM input, final VALUE value) {
			this.that.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

}
