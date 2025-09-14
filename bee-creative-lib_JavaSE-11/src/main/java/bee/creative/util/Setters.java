package bee.creative.util;

import static bee.creative.lang.Natives.forceAccessible;
import static bee.creative.lang.Natives.parseNative;
import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Fields.fieldFromNative;
import static bee.creative.util.Getters.neutralGetter;
import static java.lang.reflect.Modifier.isStatic;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	/** Diese Methode liefert einen {@link Setter3}, der beim {@link Setter#set(Object, Object) Schreiben} den Datensatz ignoriert und den Wert an den gegebenen
	 * {@link Consumer} delegiert. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFrom(Consumer<? super VALUE> that) throws NullPointerException {
		return new ConsumerSetter<>(that);
	}

	/** Diese Methode liefert den gegebenen {@link Setter} als {@link Setter}. Wenn er {@code null} ist, wird der {@link EmptySetter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFrom(Setter<? super ITEM, ? super VALUE> that) {
		if (that == null) return Setters.emptySetter();
		if (that instanceof Setter3) return (Setter3<ITEM, VALUE>)that;
		return concatSetter(neutralGetter(), that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFromNative(String, boolean) setterFromNative(memberPath, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(String memberPath) throws NullPointerException, IllegalArgumentException {
		return setterFromNative(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code setterFromNative(parseNative(memberPath), forceAccessible)}.
	 *
	 * @see Natives#parseNative(String)
	 * @see #setterFromNative(java.lang.reflect.Field, boolean)
	 * @see #setterFromNative(Method, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(String memberPath, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		var object = parseNative(memberPath);
		if (object instanceof java.lang.reflect.Field) return setterFromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return setterFromNative((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFromNative(java.lang.reflect.Field) setterFromNative(that, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(java.lang.reflect.Field that) throws NullPointerException, IllegalArgumentException {
		return setterFromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFrom(Setter) setterFrom(fieldFromNative(that, forceAccessible))}.
	 *
	 * @see Fields#fieldFromNative(java.lang.reflect.Field, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(java.lang.reflect.Field that, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return setterFrom(fieldFromNative(that, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFromNative(Method, boolean) setterFromNative(that, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(Method that) throws NullPointerException, IllegalArgumentException {
		return setterFromNative(that, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link MethodSetter new MethodSetter<>(that, forceAccessible)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(Method that, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodSetter<>(that, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFromNative(Class, String, boolean) setterFromNative(fieldOwner, fieldName, true)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(Class<? extends ITEM> fieldOwner, String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return setterFromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link #setterFrom(Setter) setterFromNative(fieldFromNative(fieldOwner, fieldName, forceAccessible))}.
	 *
	 * @see Fields#fieldFromNative(Class, String, boolean) */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> setterFromNative(Class<? extends ITEM> fieldOwner, String fieldName, boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Setters.setterFrom(fieldFromNative(fieldOwner, fieldName, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatSetter new ConcatSetter<>(that, trans)}. */
	public static <ITEM, ITEM2, VALUE> Setter3<ITEM, VALUE> concatSetter(Getter<? super ITEM, ? extends ITEM2> trans, Setter<? super ITEM2, ? super VALUE> that)
		throws NullPointerException {
		return new ConcatSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSetter new TranslatedSetter<>(that, trans)}. */
	public static <ITEM, VALUE, VALUE2> Setter3<ITEM, VALUE> translateSetter(Setter<? super ITEM, ? super VALUE2> that,
		Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
		return new TranslatedSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregateSetter(Setter, Getter) aggregateSetter(that, Getters.neutral())}. */
	public static <ITEM, VALUE> Setter3<Iterable<? extends ITEM>, VALUE> aggregateSetter(Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
		return aggregateSetter(that, neutralGetter());
	}

	/** Diese Methode ist eine Abkürzung für {@link AggregatedSetter new AggregatedSetter<>(that, trans)}. */
	public static <ITEM, VALUE, VALUE2> Setter3<Iterable<? extends ITEM>, VALUE> aggregateSetter(Setter<? super ITEM, ? super VALUE2> that,
		Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
		return new AggregatedSetter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link OptionalizedSetter new OptionalizedSetter<>(that)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> optionalizeSetter(Setter<? super ITEM, VALUE> that) throws NullPointerException {
		return new OptionalizedSetter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizeSetter(Setter, Object) Setters.synchronize(that, that)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> synchronizeSetter(Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
		return Setters.synchronizeSetter(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedSetter new SynchronizedSetter<>(that, mutex)}. */
	public static <ITEM, VALUE> Setter3<ITEM, VALUE> synchronizeSetter(Setter<? super ITEM, ? super VALUE> that, Object mutex) throws NullPointerException {
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

		public MethodSetter(Method method, boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
			this.forceAccessible = forceAccessible;
			if (method.getParameterTypes().length != (isStatic(method.getModifiers()) ? 2 : 1)) throw new IllegalArgumentException();
			this.method = forceAccessible ? forceAccessible(method) : notNull(method);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			try {
				if (isStatic(this.method.getModifiers())) {
					this.method.invoke(null, item, value);
				} else {
					this.method.invoke(item, value);
				}
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.method, this.forceAccessible);
		}

		private final Method method;

		private final boolean forceAccessible;

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, der das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen {@link Getter}
	 * übersetzten Datensatz an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item}
	 * erfolgt über {@code this.that.set(this.trans.get(item), value)}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <ITEM2> Typ des Datensatzes des gegebenen {@link Setter}.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class ConcatSetter<ITEM, ITEM2, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public ConcatSetter(Setter<? super ITEM2, ? super VALUE> that, Getter<? super ITEM, ? extends ITEM2> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			this.that.set(this.trans.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Setter<? super ITEM2, ? super VALUE> that;

		private final Getter<? super ITEM, ? extends ITEM2> trans;

	}

	/** Diese Klasse implementiert einen übersetzten {@link Setter3}, der das {@link #set(Object, Object) Schreiben} mit dem über einen gegebenen {@link Getter}
	 * übersetzten Wert an einen gegebenen {@link Setter} delegeirt. Das Schreiben des Werts {@code value} der Eigenschaft eines Datensatzes {@code item} erfolgt
	 * über {@code this.that.set(item, this.trans.get(value))}.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft.
	 * @param <VALUE2> Typ des Datensatzes des Übersetzers sowie des Werts der Eigenschaft. */
	public static class TranslatedSetter<ITEM, VALUE, VALUE2> extends AbstractSetter<ITEM, VALUE> {

		public TranslatedSetter(Setter<? super ITEM, ? super VALUE2> that, Getter<? super VALUE, ? extends VALUE2> trans) {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			this.that.set(item, this.trans.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Setter<? super ITEM, ? super VALUE2> that;

		private final Getter<? super VALUE, ? extends VALUE2> trans;

	}

	/** Diese Klasse implementiert einen aggregierten {@link Setter3}, der das {@link #set(Iterable, Object) Schreiben} mit dem über einen gegebenen
	 * {@link Getter} übersetzten Wert für jedes Element des iterierbaren Datensatzes an einen gegebenen {@link Setter} delegeirt. Wenn der iterierbare Datensatz
	 * {@code null} oder leer ist, wird das Setzen ignoriert.
	 *
	 * @param <ITEM> Typ der Elemente des iterierbaren Datensatzes.
	 * @param <VALUE> Typ des Werts.
	 * @param <VALUE2> Typ des Werts der Eigenschaft der Elemente. */
	public static class AggregatedSetter<ITEM, VALUE, VALUE2> extends AbstractSetter<Iterable<? extends ITEM>, VALUE> {

		public AggregatedSetter(Setter<? super ITEM, VALUE2> that, Getter<? super VALUE, ? extends VALUE2> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = notNull(trans);
		}

		@Override
		public void set(Iterable<? extends ITEM> item, VALUE value) {
			if (item == null) return;
			var iterator = item.iterator();
			if (!iterator.hasNext()) return;
			var value2 = this.trans.get(value);
			do {
				this.that.set(iterator.next(), value2);
			} while (iterator.hasNext());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

		private final Setter<? super ITEM, VALUE2> that;

		private final Getter<? super VALUE, ? extends VALUE2> trans;

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der das {@link #set(Object, Object) Schreiben} nur dann an einen gegebenen {@link Setter} delegiert,
	 * wenn die Eingabe nicht {@code null} ist.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class OptionalizedSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public OptionalizedSetter(Setter<? super ITEM, ? super VALUE> that) throws NullPointerException {
			this.that = notNull(that);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			if (item == null) return;
			this.that.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

		private final Setter<? super ITEM, ? super VALUE> that;

	}

	/** Diese Klasse implementiert einen {@link Setter3}, der einen gegebenen {@link Setter} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <ITEM> Typ des Datensatzes.
	 * @param <VALUE> Typ des Werts der Eigenschaft. */
	public static class SynchronizedSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public SynchronizedSetter(Setter<? super ITEM, ? super VALUE> that, Object mutex) throws NullPointerException {
			this.that = notNull(that);
			this.mutex = notNull(mutex, this);
		}

		@Override
		public void set(ITEM item, VALUE value) {
			synchronized (this.mutex) {
				this.that.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.mutex == this ? null : this.mutex);
		}

		private final Setter<? super ITEM, ? super VALUE> that;

		private final Object mutex;

	}

	public static class ConsumerSetter<ITEM, VALUE> extends AbstractSetter<ITEM, VALUE> {

		public ConsumerSetter(Consumer<? super VALUE> that) {
			this.that = notNull(that);
		}

		@Override
		public void set(ITEM input, VALUE value) {
			this.that.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

		private final Consumer<? super VALUE> that;

	}

}
