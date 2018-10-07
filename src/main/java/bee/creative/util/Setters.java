package bee.creative.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import bee.creative.util.Consumers.BaseConsumer;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Setter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	/** Diese Klasse implementiert einen abstrakten {@link Setter} als {@link BaseObject}. */
	public static abstract class BaseSetter<GItem, GValue> extends BaseObject implements Setter<GItem, GValue> {
	}

	/** Diese Klasse implementiert {@link Setters#nativeSetter(Method)} */
	@SuppressWarnings ("javadoc")
	public static class MethodSetter<GItem, GValue> extends BaseSetter<GItem, GValue> {

		public final Method method;

		public MethodSetter(final Method method, final boolean forceAccessible) {
			if (method.getParameterTypes().length != (Modifier.isStatic(method.getModifiers()) ? 2 : 1)) throw new IllegalArgumentException();
			try {
				method.setAccessible(forceAccessible);
			} catch (final SecurityException cause) {
				throw new IllegalArgumentException(cause);
			}
			this.method = method;
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

	/** Diese Klasse implementiert {@link Setters#defaultSetter(Setter)} */
	@SuppressWarnings ("javadoc")
	public static class DefaultSetter<GItem, GValue> extends BaseSetter<GItem, GValue> {

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

	/** Diese Klasse implementiert {@link Setters#navigatedSetter(Getter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class NavigatedSetter<GTarget, GSource, GValue> extends BaseSetter<GTarget, GValue> {

		public final Setter<? super GSource, ? super GValue> setter;

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public NavigatedSetter(final Getter<? super GTarget, ? extends GSource> toSource, final Setter<? super GSource, ? super GValue> setter) {
			this.setter = Objects.notNull(setter);
			this.toSource = Objects.notNull(toSource);
		}

		@Override
		public void set(final GTarget item, final GValue value) {
			this.setter.set(this.toSource.get(item), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.setter);
		}

	}

	/** Diese Klasse implementiert {@link Setters#translatedSetter(Getter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedSetter<GItem, GSource, GTarget> extends BaseSetter<GItem, GTarget> {

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

	/** Diese Klasse implementiert {@link Setters#conditionalSetter(Filter, Setter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class ConditionalSetter<GItem, GValue> extends BaseSetter<GItem, GValue> {

		public final Filter<? super GItem> condition;

		public final Setter<? super GItem, ? super GValue> accept;

		public final Setter<? super GItem, ? super GValue> reject;

		public ConditionalSetter(final Filter<? super GItem> condition, final Setter<? super GItem, ? super GValue> acceptSetter,
			final Setter<? super GItem, ? super GValue> rejectSetter) {
			this.condition = Objects.notNull(condition);
			this.accept = Objects.notNull(acceptSetter);
			this.reject = Objects.notNull(rejectSetter);
		}

		@Override
		public void set(final GItem item, final GValue value) {
			if (this.condition.accept(item)) {
				this.accept.set(item, value);
			} else {
				this.reject.set(item, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.condition, this.accept, this.reject);
		}

	}

	/** Diese Klasse implementiert {@link Setters#aggregatedSetter(Getter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class AggregatedSetter<GItem, GSource, GTarget> extends BaseSetter<Iterable<? extends GItem>, GTarget> {

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

	/** Diese Klasse implementiert {@link Setters#synchronizedSetter(Object, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedSetter<GItem, GValue> extends BaseSetter<GItem, GValue> {

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

	/** Diese Klasse implementiert {@link Setters#toConsumer(Object, Setter)} */
	@SuppressWarnings ("javadoc")
	static class SetterConsumer<GValue, GItem> extends BaseConsumer<GValue> {

		public final GItem item;

		public final Setter<? super GItem, ? super GValue> setter;

		public SetterConsumer(final GItem item, final Setter<? super GItem, ? super GValue> setter) {
			this.item = item;
			this.setter = Objects.notNull(setter);
		}

		@Override
		public void set(final GValue value) {
			this.setter.set(this.item, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.setter);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#emptyField() Fields.emptyField()}. */
	public static Setter<Object, Object> emptySetter() {
		return Fields.emptyField();
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#nativeSetter(String, boolean) Setters.nativeSetter(memberPath, true)}. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final String memberPath) throws NullPointerException, IllegalArgumentException {
		return Setters.nativeSetter(memberPath, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Fields.nativeSetter(Natives.parse(methodPath))}, wobei eine {@link Class} bzw. ein
	 * {@link Constructor} zu einer Ausnahme führt.
	 *
	 * @see Natives#parseMethod(String)
	 * @see Setters#nativeSetter(java.lang.reflect.Field, boolean)
	 * @see Setters#nativeSetter(Method, boolean)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param memberPath Pfad einer Methode oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final String memberPath, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberPath);
		if (object instanceof java.lang.reflect.Field) return Setters.nativeSetter((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Setters.nativeSetter((Method)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(java.lang.reflect.Field) Fields.nativeField(field)}. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(field);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(java.lang.reflect.Field, boolean) Fields.nativeField(field, forceAccessible)}. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Setters#nativeSetter(Method, boolean) Setters.nativeSetter(method, true)}. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final Method method) throws NullPointerException, IllegalArgumentException {
		return Setters.nativeSetter(method, true);
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
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final Method method, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodSetter<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(Class, String) Fields.nativeField(fieldOwner, fieldName)}. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(fieldOwner, fieldName);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(Class, String, boolean) Fields.nativeField(fieldOwner, fieldName, forceAccessible)}. */
	public static <GItem, GValue> Setter<GItem, GValue> nativeSetter(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(fieldOwner, fieldName, forceAccessible);
	}

	/** Diese Methode einen {@link Setter} zurück, der Datensatz und Wert nur dann dann an den gegebenen {@link Setter} delegiert, wenn der Datensatz nicht
	 * {@code null} ist.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param setter {@link Setter} zum Schreiben der Eigenschaft.
	 * @return {@code default}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code setter} {@code null} ist. */
	public static <GItem, GValue> Setter<GItem, GValue> defaultSetter(final Setter<? super GItem, GValue> setter) throws NullPointerException {
		return new DefaultSetter<>(setter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#mappingField(Map) Fields.mappingField(mapping)}. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Setter<GItem, GValue> mappingSetter(final Map<? super GItem, ? super GValue> mapping) {
		return (Setter<GItem, GValue>)Fields.mappingField(mapping);
	}

	/** Diese Methode gibt einen navigierten {@link Setter} zurück. Der erzeugte {@link Setter} setzt den Wert {@code value} der Eigenschaft eines Datensatzes
	 * {@code item} über {@code setter.set(toSource.get(item), value)}.
	 *
	 * @param <GTarget> Typ des Datensatzes.
	 * @param <GSource> Typ des Datensatzes des gegebenen {@link Setter}.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param toSource {@link Getter} zur Übersetzung des Datensatzes.
	 * @param setter {@link Setter} zur Manipulation.
	 * @return {@code navigated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code setter} {@code null} ist. */
	public static <GTarget, GSource, GValue> Setter<GTarget, GValue> navigatedSetter(final Getter<? super GTarget, ? extends GSource> toSource,
		final Setter<? super GSource, ? super GValue> setter) throws NullPointerException {
		return new NavigatedSetter<>(toSource, setter);
	}

	/** Diese Methode gibt einen übersetzten {@link Setter} zurück. Der erzeugte {@link Setter} setzt den Wert {@code value} der Eigenschaft eines Datensatzes
	 * {@code item} über {@code setter.set(item, toSource.get(value))}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GSource> Typ des Datensatzes des Übersetzers sowie des Werts der Eigenschaft.
	 * @param <GTarget> Typ des Werts der Eigenschaft.
	 * @param toSource zur Übersetzung des Werts.
	 * @param setter {@link Setter} zur Manipulation.
	 * @return {@code translated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GSource, GTarget> Setter<GItem, GTarget> translatedSetter(final Getter<? super GTarget, ? extends GSource> toSource,
		final Setter<? super GItem, ? super GSource> setter) throws NullPointerException {
		return new TranslatedSetter<>(toSource, setter);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, der über die Weiterleitug eines Datensatzes an einen der gegebenen {@link Setter} mit Hilfe des gegebenen
	 * {@link Filter} entscheiden. Wenn der {@link Filter} einen Datensatz akzeptiert, setzt der erzeugte {@link Setter} den Wert der Eigenschaft dieses
	 * Datensatzes über {@code acceptSetter}. Andernfalls setzt er ihn über {@code rejectSetter}. Der erzeugte {@link Setter} setzt den Wert {@code value} für
	 * einen Datensatz {@code item} damit über {@code (condition.accept(item) ? acceptSetter : rejectSetter).set(item, value)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param condition Bedingung.
	 * @param acceptSetter Eigenschaft zum Setzen des Werts akzeptierter Datensätze.
	 * @param rejectSetter Eigenschaft zum Setzen des Werts abgelehnter Datensätze.
	 * @return {@code conditional}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code condition}, {@code acceptSetter} bzw. {@code rejectSetter} {@code null} ist. */
	public static <GItem, GValue> Setter<GItem, GValue> conditionalSetter(final Filter<? super GItem> condition,
		final Setter<? super GItem, ? super GValue> acceptSetter, final Setter<? super GItem, ? super GValue> rejectSetter) throws NullPointerException {
		return new ConditionalSetter<>(condition, acceptSetter, rejectSetter);
	}

	/** Diese Methode ist eine Abkürzung für {@link #aggregatedSetter(Getter, Setter) Setters.aggregatedSetter(Getters.neutralGetter(), setter)}. **/
	public static <GItem, GValue> Setter<Iterable<? extends GItem>, GValue> aggregatedSetter(final Setter<? super GItem, GValue> setter)
		throws NullPointerException {
		return Setters.aggregatedSetter(Getters.<GValue>neutralGetter(), setter);
	}

	/** Diese Methode gibt einen aggregierten {@link Setter} zurück, welcher den formatierten Wert der Eigenschaften der Elemente des iterierbaren Datensatzes
	 * setzt. Wenn der iterierbare Datensatz des erzeugten {@link Setter} {@code null} oder leer ist, wird das Setzen ignoriert. Andernfalls wird der gemäß dem
	 * gegebenen {@link Getter} {@code toSource} umgewandelte Wert über die gegebene {@link Setter} an jedem Element des iterierbaren Datensatzes gesetzt.
	 *
	 * @param <GItem> Typ der Elemente des iterierbaren Datensatzes.
	 * @param <GSource> Typ des Werts der Eigenschaft der Elemente.
	 * @param <GTarget> Typ des Werts des gelieferten {@link Setter}.
	 * @param toSource {@link Getter} zur Umwandlung des Werts der Eigenschaft der Elemente in den Wert des gelieferten {@link Setter}.
	 * @param setter Eigenschaft der Elemente des iterierbaren Datensatzes.
	 * @return {@code aggregated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code setter} {@code null} ist. */
	public static <GItem, GSource, GTarget> Setter<Iterable<? extends GItem>, GTarget> aggregatedSetter(final Getter<? super GTarget, ? extends GSource> toSource,
		final Setter<? super GItem, GSource> setter) throws NullPointerException {
		return new AggregatedSetter<>(toSource, setter);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedSetter(Object, Setter) Setters.synchronizedSetter(setter, setter)}. */
	public static <GItem, GValue> Setter<GItem, GValue> synchronizedSetter(final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return Setters.synchronizedSetter(setter, setter);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, welcher den gegebenen {@link Setter} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Setter} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param setter {@link Setter}.
	 * @return {@code synchronized}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code setter} {@code null} ist. */
	public static <GItem, GValue> Setter<GItem, GValue> synchronizedSetter(final Object mutex, final Setter<? super GItem, ? super GValue> setter)
		throws NullPointerException {
		return new SynchronizedSetter<>(mutex, setter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#compositeField(Getter, Setter) Fields.compositeField(Getters.emptyGetter(), setter)}. */
	public static <GItem, GValue> Field<GItem, GValue> toField(final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return Fields.compositeField(Getters.<GValue>emptyGetter(), setter);
	}

	/** Diese Methode ist eine Abkürzung für {@link #toConsumer(Object, Setter) Setters.toConsumer(null, setter)}. */
	public static <GItem, GValue> Consumer<GValue> toConsumer(final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return Setters.toConsumer(null, setter);
	}

	/** Diese Methode gibt einen {@link Consumer} zurück, der mit dem gegebenen Datensatz an den gegebenen {@link Setter} delegiert.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param item Datensatz.
	 * @param setter {@link Setter}.
	 * @return {@link Setter}-{@link Consumer}.
	 * @throws NullPointerException Wenn {@code setter} {@code null} ist. */
	public static <GItem, GValue> Consumer<GValue> toConsumer(final GItem item, final Setter<? super GItem, ? super GValue> setter) throws NullPointerException {
		return new SetterConsumer<>(item, setter);
	}

}
