package bee.creative.bind;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import bee.creative.bind.Fields.EmptyField;
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Setter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	public static class EmptySetter extends AbstractSetter<Object, Object> {

		public static final Setter3<?, ?> INSTANCE = new EmptySetter();

	}

	/** Diese Klasse implementiert einen verketteten {@link Setter3}, der den Datensatz beim {@link #set(Object, Object) Schreiben} über einen gegebenen
	 * {@link Getter} in den Datensatz eines gegebenen {@link Setter} überführt und das Schreiben dann an letzteren delegiert. den gegebenen Datensatz über einen
	 * gegebenen {@link Getter} in einen und dazu einen Datensatz
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GItem2> Typ des Datensatzes des gegebenen {@link Setter}.
	 * @param <GValue> Typ des Werts der Eigenschaft. */
	public static class ConcatSetter<GItem, GItem2, GValue> extends AbstractSetter<GItem, GValue> {

		public final Getter<? super GItem, ? extends GItem2> source;

		public final Setter<? super GItem2, ? super GValue> target;

		public ConcatSetter(final Getter<? super GItem, ? extends GItem2> source, final Setter<? super GItem2, ? super GValue> target) {
			this.source = Objects.notNull(source);
			this.target = Objects.notNull(target);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			this.target.set(this.source.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.source, this.target);
		}

	}

	public static class DefaultSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Setter<? super GItem, ? super GValue> setter;

		public DefaultSetter(final Setter<? super GItem, ? super GValue> setter) {
			this.setter = Objects.notNull(setter);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (item == null) return;
			this.setter.set(item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setter);
		}

	}

	public static class MethodSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Method method;

		public MethodSetter(final Method method, final boolean forceAccessible) {
			if (method.getParameterTypes().length != (Modifier.isStatic(method.getModifiers()) ? 2 : 1)) throw new IllegalArgumentException();
			this.method = forceAccessible ? Natives.forceAccessible(method) : Objects.notNull(method);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			try {
				if (Modifier.isStatic(this.method.getModifiers())) {
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
			return Objects.toInvokeString(this, this.method, this.method.isAccessible());
		}

	}

	public static class TranslatedSetter<GItem, GSource, GTarget> extends AbstractSetter<GItem, GTarget> {

		public final Setter<? super GItem, ? super GSource> setter;

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public TranslatedSetter(final Getter<? super GTarget, ? extends GSource> toSource, final Setter<? super GItem, ? super GSource> setter) {
			this.toSource = Objects.notNull(toSource);
			this.setter = Objects.notNull(setter);
		}

		@Override
		public void set(final GItem item, final GTarget value) {
			this.setter.set(item, this.toSource.get(value));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.setter);
		}

	}

	public static class AggregatedSetter<GItem, GSource, GTarget> extends AbstractSetter<Iterable<? extends GItem>, GTarget> {

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public final Setter<? super GItem, GSource> setter;

		public AggregatedSetter(final Getter<? super GTarget, ? extends GSource> toSource, final Setter<? super GItem, GSource> setter) {
			this.toSource = Objects.notNull(toSource);
			this.setter = Objects.notNull(setter);
		}

		@Override
		public void set(final Iterable<? extends GItem> item, final GTarget value) {
			if (item == null) return;
			final Iterator<? extends GItem> iterator = item.iterator();
			if (!iterator.hasNext()) return;
			final GSource value2 = this.toSource.get(value);
			do {
				this.setter.set(iterator.next(), value2);
			} while (iterator.hasNext());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.setter);
		}

	}

	public static class SynchronizedSetter<GItem, GValue> extends AbstractSetter<GItem, GValue> {

		public final Object mutex;

		public final Setter<? super GItem, ? super GValue> setter;

		public SynchronizedSetter(final Object mutex, final Setter<? super GItem, ? super GValue> setter) {
			this.mutex = Objects.notNull(mutex, this);
			this.setter = Objects.notNull(setter);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			synchronized (this.mutex) {
				this.setter.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setter);
		}

	}

	static class ConsumerSetter<GValue> extends AbstractSetter<Object, GValue> {

		public final Consumer<? super GValue> target;

		public ConsumerSetter(final Consumer<? super GValue> target) {
			this.target = Objects.notNull(target);
		}

		@Override
		public void set(final Object input, final GValue value) {
			this.target.set(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	public static <GItem, GValue> Setter3<GItem, GValue> empty() {
		return (Setter3<GItem, GValue>)EmptySetter.INSTANCE;
	}

	/** Diese Methode gibt den gegebenen {@link Consumer} als {@link Consumer3} zurück. Wenn er {@code null} ist, wird {@link #empty()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Setter3<GItem, GValue> from(final Setter<? super GItem, ? super GValue> target) {
		if (target == null) return empty();
		if (target instanceof Setter3) return (Setter3<GItem, GValue>)target;
		return concat(Getters.<GItem>neutral(), target);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, der seinen Datensatz ignoriert und den Wert des gegebenen {@link Consumer} setzt.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param consumer {@link Consumer}.
	 * @return {@link Consumer}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code consumer} {@code null} ist. */
	public static <GValue> Setter3<Object, GValue> from(final Consumer<? super GValue> consumer) {
		return new Setters.ConsumerSetter<>(consumer);
	}

	public static <GTarget, GSource, GValue> Setter3<GTarget, GValue> concat(final Getter<? super GTarget, ? extends GSource> source,
		final Setter<? super GSource, ? super GValue> target) throws NullPointerException {
		return new ConcatSetter<>(source, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#fromNative(String, boolean) Setters.nativeSetter(memberPath, true)}. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Fields.nativeSetter(Natives.parse(methodPath))}, wobei eine {@link Class} bzw. ein
	 * {@link Constructor} zu einer Ausnahme führt.
	 *
	 * @see Natives#parseMethod(String)
	 * @see Setters#fromNative(java.lang.reflect.Field, boolean)
	 * @see Setters#fromNative(Method, boolean)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param memberPath Pfad einer Methode oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof java.lang.reflect.Field) return Setters.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Setters.fromNative((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(java.lang.reflect.Field) Fields.nativeField(field)}. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(java.lang.reflect.Field, boolean) Fields.nativeField(field, forceAccessible)}. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return from(Fields.fromNative(field, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#fromNative(Method, boolean) Setters.nativeSetter(method, true)}. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final Method method) throws NullPointerException, IllegalArgumentException {
		return Setters.fromNative(method, true);
	}

	/** Diese Methode gibt einen {@link Setter} zur gegebenen {@link Method nativen Methode} zurück. Bei einer Klassenmethode erfolgt das Schreiben des Werts
	 * {@code value} der Eigenschaft eines Datensatzes {@code item} über {@code method.invoke(null, item, value)}, bei einer Objektmethode hingegen über
	 * {@code method.invoke(item, value)}.
	 *
	 * @see Method#invoke(Object, Object...)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param method Methode zum Schreiben des Werts der Eigenschaft.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Methode keine passende Parameteranzahl besitzen. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final Method method, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodSetter<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Class, String) Fields.nativeField(fieldOwner, fieldName)}. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(fieldOwner, fieldName);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Class, String, boolean) Fields.nativeField(fieldOwner, fieldName, forceAccessible)}. */
	public static <GItem, GValue> Setter<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.fromNative(fieldOwner, fieldName, forceAccessible);
	}

	/** Diese Methode einen {@link Setter} zurück, der Datensatz und Wert nur dann dann an den gegebenen {@link Setter} delegiert, wenn der Datensatz nicht
	 * {@code null} ist.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param setter {@link Setter} zum Schreiben der Eigenschaft.
	 * @return {@code default}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code setter} {@code null} ist. */
	public static <GItem, GValue> Setter3<GItem, GValue> toDefault(final Setter<? super GItem, GValue> setter) throws NullPointerException {
		return new DefaultSetter<>(setter);
	}

	/** Diese Methode gibt einen übersetzten {@link Setter} zurück. Der erzeugte {@link Setter} setzt den Wert {@code value} der Eigenschaft eines Datensatzes
	 * {@code item} über {@code setter.set(item, toSource.get(value))}.
	 * 
	 * @param target {@link Setter} zur Manipulation.
	 * @param trans zur Übersetzung des Werts.
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GSource> Typ des Datensatzes des Übersetzers sowie des Werts der Eigenschaft.
	 * @param <GTarget> Typ des Werts der Eigenschaft.
	 * @return {@code translated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GSource, GTarget> AbstractSetter<GItem, GTarget> toTranslated(final Setter<? super GItem, ? super GSource> target,
		final Getter<? super GTarget, ? extends GSource> trans) throws NullPointerException {
		return new TranslatedSetter<>(trans, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toAggregated(Setter, Getter) Setters.aggregatedSetter(Getters.neutralGetter(), setter)}. **/
	public static <GItem, GValue> AbstractSetter<Iterable<? extends GItem>, GValue> toAggregated(final Setter<? super GItem, GValue> setter)
		throws NullPointerException {
		return Setters.toAggregated(setter, Getters.<GValue>neutral());
	}

	/** Diese Methode gibt einen aggregierten {@link Setter} zurück, welcher den formatierten Wert der Eigenschaften der Elemente des iterierbaren Datensatzes
	 * setzt. Wenn der iterierbare Datensatz des erzeugten {@link Setter} {@code null} oder leer ist, wird das Setzen ignoriert. Andernfalls wird der gemäß dem
	 * gegebenen {@link Getter} {@code toSource} umgewandelte Wert über die gegebene {@link Setter} an jedem Element des iterierbaren Datensatzes gesetzt.
	 * @param target Eigenschaft der Elemente des iterierbaren Datensatzes.
	 * @param trans {@link Getter} zur Umwandlung des Werts der Eigenschaft der Elemente in den Wert des gelieferten {@link Setter}.
	 *
	 * @param <GItem> Typ der Elemente des iterierbaren Datensatzes.
	 * @param <GSource> Typ des Werts der Eigenschaft der Elemente.
	 * @param <GTarget> Typ des Werts des gelieferten {@link Setter}.
	 * @return {@code aggregated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GSource, GTarget> AbstractSetter<Iterable<? extends GItem>, GTarget> toAggregated(
		final Setter<? super GItem, GSource> target, final Getter<? super GTarget, ? extends GSource> trans) throws NullPointerException {
		return new AggregatedSetter<>(trans, target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toSynchronized(Setter, Object) Setters.synchronizedSetter(setter, setter)}. */
	public static <GItem, GValue> AbstractSetter<GItem, GValue> toSynchronized(final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return Setters.toSynchronized(setter, setter);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, welcher den gegebenen {@link Setter} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Setter} als Synchronisationsobjekt verwendet.
	 * @param target {@link Setter}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @return {@code synchronized}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code setter} {@code null} ist. */
	public static <GItem, GValue> AbstractSetter<GItem, GValue> toSynchronized(final Setter<? super GItem, ? super GValue> target, final Object mutex)
		throws NullPointerException {
		return new SynchronizedSetter<>(mutex, target);
	}

}
