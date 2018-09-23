package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import bee.creative.util.Consumers.BaseConsumer;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Setter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Setters {

	/** Diese Klasse implementiert einen {@link Setter} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
	public static abstract class BaseSetter<GInput, GValue> extends BaseObject implements Setter<GInput, GValue> {
	}

	/** Diese Klasse implementiert {@link Setters#nativeSetter(Method)} */
	@SuppressWarnings ("javadoc")
	public static class NativeSetter<GInput, GValue> implements Setter<GInput, GValue> {

		public final Method method;

		public NativeSetter(final Method method) {
			if (method.getParameterTypes().length != 1) throw new IllegalArgumentException();
			method.setAccessible(true);
			this.method = method;
		}

		@Override
		public void set(final GInput input, final GValue value) {
			try {
				this.method.invoke(input, value);
			} catch (IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, Natives.formatMethod(this.method));
		}

	}

	/** Diese Klasse implementiert {@link Setters#defaultSetter(Setter)} */
	@SuppressWarnings ("javadoc")
	public static class DefaultSetter<GInput, GValue> implements Setter<GInput, GValue> {

		public final Setter<? super GInput, GValue> setter;

		public DefaultSetter(final Setter<? super GInput, GValue> setter) {
			this.setter = Objects.assertNotNull(setter);
		}

		@Override
		public void set(final GInput input, final GValue value) {
			if (input == null) return;
			this.setter.set(input, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setter);
		}

	}

	/** Diese Klasse implementiert {@link Setters#navigatedSetter(Getter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class NavigatedSetter<GInput, GInput2, GValue> implements Setter<GInput, GValue> {

		public final Getter<? super GInput, ? extends GInput2> navigator;

		public final Setter<? super GInput2, GValue> setter;

		public NavigatedSetter(final Getter<? super GInput, ? extends GInput2> navigator, final Setter<? super GInput2, GValue> setter) {
			this.navigator = Objects.assertNotNull(navigator);
			this.setter = Objects.assertNotNull(setter);
		}

		@Override
		public void set(final GInput input, final GValue value) {
			this.setter.set(this.navigator.get(input), value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.navigator, this.setter);
		}

	}

	/** Diese Klasse implementiert {@link Setters#conditionalSetter(Filter, Setter, Setter)} */
	@SuppressWarnings ("javadoc")
	public static class ConditionalSetter<GInput, GValue> implements Setter<GInput, GValue> {

		public final Filter<? super GInput> condition;

		public final Setter<? super GInput, ? super GValue> accept;

		public final Setter<? super GInput, ? super GValue> reject;

		public ConditionalSetter(final Filter<? super GInput> condition, final Setter<? super GInput, ? super GValue> acceptSetter,
			final Setter<? super GInput, ? super GValue> rejectSetter) {
			this.condition = Objects.assertNotNull(condition);
			this.accept = Objects.assertNotNull(acceptSetter);
			this.reject = Objects.assertNotNull(rejectSetter);
		}

		@Override
		public void set(final GInput input, final GValue value) {
			if (this.condition.accept(input)) {
				this.accept.set(input, value);
			} else {
				this.reject.set(input, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.condition, this.accept, this.reject);
		}

	}

	/** Diese Klasse implementiert {@link Setters#aggregatedSetter(Setter, Getter)} */
	@SuppressWarnings ("javadoc")
	public static class AggregatedSetter<GItem, GValue, GValue2> implements Setter<Iterable<? extends GItem>, GValue> {

		public final Setter<? super GItem, GValue2> setter;

		public final Getter<? super GValue, ? extends GValue2> format;

		public AggregatedSetter(final Setter<? super GItem, GValue2> setter, final Getter<? super GValue, ? extends GValue2> format) {
			Objects.assertNotNull(setter);
			Objects.assertNotNull(format);
			this.setter = setter;
			this.format = format;
		}

		@Override
		public void set(final Iterable<? extends GItem> input, final GValue value) {
			if (input == null) return;
			final Iterator<? extends GItem> iterator = input.iterator();
			if (!iterator.hasNext()) return;
			final GValue2 value2 = this.format.get(value);
			do {
				this.setter.set(iterator.next(), value2);
			} while (iterator.hasNext());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setter, this.format);
		}

	}

	/** Diese Klasse implementiert {@link Setters#synchronizedSetter(Setter, Object)} */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedSetter<GInput, GValue> implements Setter<GInput, GValue> {

		public final Object mutex;

		public final Setter<? super GInput, ? super GValue> setter;

		public SynchronizedSetter(final Object mutex, final Setter<? super GInput, ? super GValue> setter) {
			this.mutex = Objects.assertNotNull(mutex);
			this.setter = Objects.assertNotNull(setter);
		}

		@Override
		public void set(final GInput input, final GValue value) {
			synchronized (this.mutex) {
				this.setter.set(input, value);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.setter);
		}

	}

	public static class SetterConsumer<GValue, GInput> extends BaseConsumer<GValue> {

		public final Setter<? super GInput, ? super GValue> setter;

		public final GInput input;

		public SetterConsumer(final GInput input, final Setter<? super GInput, ? super GValue> setter) {
			this.input = input;
			this.setter = Objects.assertNotNull(setter);
		}

		@Override
		public void set(final GValue value) {
			this.setter.set(this.input, value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.input, this.setter);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.emptyField()}. */
	@SuppressWarnings ("javadoc")
	public static Setter<Object, Object> emptySetter() {
		return Fields.emptyField();
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeSetter(Natives.parseMethod(methodText))}.
	 *
	 * @see #nativeSetter(java.lang.reflect.Method)
	 * @see Natives#parseMethod(String)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param methodText Methodentext der Methode zum Schreiben der Eigenschaft.
	 * @return {@code native}-{@link Setter}.
	 * @throws NullPointerException Wenn {@link Natives#parseMethod(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parseMethod(String)} eine entsprechende Ausnahme auslöst. */
	public static <GInput, GValue> Setter<GInput, GValue> nativeSetter(final String methodText) throws NullPointerException, IllegalArgumentException {
		return Setters.nativeSetter(Natives.parseMethod(methodText));
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeField(field)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Setter<GInput, GValue> nativeSetter(final java.lang.reflect.Field field)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(field);
	}

	// TODO satic mit (null, inp, val)
	/** Diese Methode gibt einen {@link Setter} zur gegebenen {@link java.lang.reflect.Method nativen Methode} zurück.<br>
	 * Für eine Eingabe {@code input} erfolgt das Schreiben des Werts {@code value} über {@code method.invoke(input, value)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param method Methode zum Schreiben der Eigenschaft.
	 * @return {@code native}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Methode keine passende Parameteranzahl besitzen. */
	public static <GInput, GValue> Setter<GInput, GValue> nativeSetter(final java.lang.reflect.Method method)
		throws NullPointerException, IllegalArgumentException {
		return new NativeSetter<>(method);
	}

	/** Diese Methode einen {@link Setter} zurück, der seine Eingabe nur dann an den gegebenen {@link Setter} delegiert, wenn diese nicht {@code null} ist.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param setter {@link Setter} zum Schreiben der Eigenschaft.
	 * @return {@code default}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code setter} {@code null} ist. */
	public static <GInput, GValue> Setter<GInput, GValue> defaultSetter(final Setter<? super GInput, GValue> setter) throws NullPointerException {
		return new DefaultSetter<>(setter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.mappingField(mapping)}. */
	@SuppressWarnings ({"javadoc", "unchecked"})
	public static <GInput, GValue> Setter<GInput, GValue> mappingSetter(final Map<? super GInput, ? super GValue> mapping) {
		return (Setter<GInput, GValue>)Fields.mappingField(mapping);
	}

	/** Diese Methode gibt einen navigierten {@link Setter} zurück.<br>
	 * Der erzeugte {@link Setter} setzt den Wert {@code value} einer Eingabe {@code input} über {@code setter.set(navigator.get(input), value)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GInput2> Typ des Werts des Navigators sowie der Eingabe der Eigenschaft.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param navigator {@link Getter} zur Navigation.
	 * @param setter {@link Setter} zur Manipulation.
	 * @return {@code navigated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code setter} {@code null} ist. */
	public static <GInput, GInput2, GValue> Setter<GInput, GValue> navigatedSetter(final Getter<? super GInput, ? extends GInput2> navigator,
		final Setter<? super GInput2, GValue> setter) throws NullPointerException {
		return new NavigatedSetter<>(navigator, setter);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, der über die Weiterleitug der Eingabe an einen der gegebenen {@link Setter Eigenschaften} mit Hilfe des
	 * gegebenen {@link Filter} entscheiden.<br>
	 * Wenn der {@link Filter} eine Eingabe akzeptiert, setzt der erzeugte {@link Setter} den Wert der {@link Setter Eigenschaft} {@code acceptSetter}.
	 * Andernfalls setzt er den Wert der {@link Setter Eigenschaft} {@code rejectSetter}. Der erzeugte {@link Setter} setzt den Wert {@code value} für eine
	 * Eingabe {@code input} damit über {@code (condition.accept(input) ? acceptSetter : rejectSetter).set(input, value)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param condition Bedingung.
	 * @param acceptSetter Eigenschaft zum Setzen des Werts akzeptierter Eingaben.
	 * @param rejectSetter Eigenschaft zum Setzen des Werts abgelehntenr Eingaben.
	 * @return {@code conditional}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code condition}, {@code acceptSetter} bzw. {@code rejectSetter} {@code null} ist. */
	public static <GInput, GValue> Setter<GInput, GValue> conditionalSetter(final Filter<? super GInput> condition,
		final Setter<? super GInput, ? super GValue> acceptSetter, final Setter<? super GInput, ? super GValue> rejectSetter) throws NullPointerException {
		return new ConditionalSetter<>(condition, acceptSetter, rejectSetter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Setters.aggregatedSetter(setter, Getters.neutralGetter())}.
	 *
	 * @see #emptySetter()
	 * @see #aggregatedSetter(Setter, Setter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Setter<Iterable<? extends GItem>, GValue> aggregatedSetter(final Setter<? super GItem, GValue> setter)
		throws NullPointerException {
		return Setters.aggregatedSetter(setter, Getters.<GValue>neutralGetter());
	}

	/** Diese Methode gibt einen aggregierten {@link Setter} zurück, welcher den formatierten Wert der {@link Setter Eigenschaften} der Elemente seiner
	 * iterierbaren Eingabe setzt.<br>
	 * Wenn die iterierbare Eingabe des erzeugten {@link Setter} {@code null} oder leer ist, wird das Setzen ignoriert. Andernfalls wird der gemäß dem gegebenen
	 * {@link Getter Schreibformat} {@code format} umgewandelte Wert über die gegebene {@link Setter Eigenschaft} an jedem Element der iterierbaren Eingabe
	 * gesetzt.
	 *
	 * @param <GItem> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts des gelieferten {@link Setter}.
	 * @param <GValue2> Typ des Werts der Eigenschaft der Elemente in der iterierbaren Eingabe.
	 * @param setter Eigenschaft der Elemente in der iterierbaren Eingabe.
	 * @param format Leseformat zur Umwandlung des Werts der Eigenschaft der Elemente in den Wert des gelieferten {@link Setter}.
	 * @return {@code aggregated}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code property} bzw. {@code getFormat} {@code null} ist. */
	public static <GItem, GValue, GValue2> Setter<Iterable<? extends GItem>, GValue> aggregatedSetter(final Setter<? super GItem, GValue2> setter,
		final Getter<? super GValue, ? extends GValue2> format) throws NullPointerException {
		return new AggregatedSetter<>(setter, format);
	}

	/** Diese Methode ist eine Abkürzung für {@code synchronizedSetter(setter, setter)}.
	 *
	 * @see #synchronizedSetter(Setter, Object) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Setter<GInput, GValue> synchronizedSetter(final Setter<? super GInput, ? super GValue> setter) throws NullPointerException {
		return Setters.synchronizedSetter(setter, setter);
	}

	/** Diese Methode gibt einen {@link Setter} zurück, welcher den gegebenen {@link Setter} via {@code synchronized(mutex)} synchronisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param setter {@link Setter}.
	 * @param mutex Synchronisationsobjekt.
	 * @return {@code synchronized}-{@link Setter}.
	 * @throws NullPointerException Wenn {@code setter} bzw. {@code mutex} {@code null} ist. */
	public static <GInput, GValue> Setter<GInput, GValue> synchronizedSetter(final Setter<? super GInput, ? super GValue> setter, final Object mutex)
		throws NullPointerException {
		return new SynchronizedSetter<>(mutex, setter);
	}

	public static <GInput, GValue> Consumer<GValue> toConsumer(final Setter<? super GInput, ? super GValue> setter) {
		return Setters.toConsumer(setter, null);
	}

	public static <GInput, GValue> Consumer<GValue> toConsumer(final Setter<? super GInput, ? super GValue> setter, final GInput input) {
		return new SetterConsumer<>(input, setter);
	}

}
