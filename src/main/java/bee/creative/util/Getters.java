package bee.creative.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Getter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Getters {

	/** Diese Klasse implementiert einen abstrakten {@link Getter} als {@link BaseObject}. */
	@SuppressWarnings ("javadoc")
	public static abstract class BaseGetter<GInput, GValue> extends BaseObject implements Getter<GInput, GValue> {
	}

	/** Diese Klasse implementiert {@link Getters#neutralGetter()}. */
	@SuppressWarnings ("javadoc")
	public static class NeutralGetter extends BaseGetter<Object, Object> {

		public static final Getter<?, ?> INSTANCE = new NeutralGetter();

		@Override
		public Object get(final Object input) {
			return input;
		}

	}

	/** Diese Klasse implementiert {@link Getters#nativeGetter(Method)}. */
	@SuppressWarnings ("javadoc")
	public static class MethodGetter<GInput, GOutput> implements Getter<GInput, GOutput> {

		public final Method method;

		public MethodGetter(final Method method) {
			try {
				method.setAccessible(true);
			} catch (final SecurityException cause) {
				throw new IllegalArgumentException(cause);
			}
			if (Modifier.isStatic(method.getModifiers())) {
				if (method.getParameterTypes().length != 1) throw new IllegalArgumentException();
			} else {
				if (method.getParameterTypes().length != 0) throw new IllegalArgumentException();
			}
			this.method = method;
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GOutput get(final GInput input) {
			try {
				final GOutput result;
				if (Modifier.isStatic(this.method.getModifiers())) {
					result = (GOutput)this.method.invoke(null, input);
				} else {
					result = (GOutput)this.method.invoke(input);
				}
				return result;
			} catch (final IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, Natives.formatMethod(this.method));
		}

	}

	/** Diese Klasse implementiert {@link Getters#nativeGetter(Constructor)}. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorGetter<GInput, GOutput> implements Getter<GInput, GOutput> {

		public final Constructor<?> constructor;

		public ConstructorGetter(final Constructor<?> constructor) {
			try {
				constructor.setAccessible(true);
			} catch (final SecurityException cause) {
				throw new IllegalArgumentException(cause);
			}
			if (!Modifier.isStatic(constructor.getModifiers()) || (constructor.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.constructor = constructor;
		}

		@Override
		public GOutput get(final GInput input) {
			try {
				@SuppressWarnings ("unchecked")
				final GOutput result = (GOutput)this.constructor.newInstance(input);
				return result;
			} catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, Natives.formatConstructor(this.constructor));
		}

	}

	/** Diese Klasse implementiert {@link Getters#defaultGetter(Getter, Object)}. */
	@SuppressWarnings ("javadoc")
	public static class DefaultGetter<GInput, GValue> implements Getter<GInput, GValue> {

		public final Getter<? super GInput, GValue> getter;

		public final GValue value;

		public DefaultGetter(final Getter<? super GInput, GValue> getter, final GValue value) {
			this.getter = Objects.notNull(getter);
			this.value = value;
		}

		@Override
		public GValue get(final GInput input) {
			if (input == null) return this.value;
			return this.getter.get(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Getters#bufferedGetter(int, int, int, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class BufferedGetter<GInput, GValue> implements Getter<GInput, GValue> {

		public final int limit;

		public final byte inputMode;

		public final byte outputMode;

		public final Getter<? super GInput, ? extends GValue> getter;

		Map<Pointer<GInput>, Pointer<GValue>> map = new LinkedHashMap<>(0, 0.75f, true);

		int capacity = 0;

		public BufferedGetter(final int limit, final int inputMode, final int outputMode, final Getter<? super GInput, ? extends GValue> getter) {
			Pointers.pointer(inputMode, null);
			Pointers.pointer(outputMode, null);
			this.limit = limit;
			this.inputMode = (byte)inputMode;
			this.outputMode = (byte)outputMode;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get(final GInput input) {
			final Pointer<GValue> pointer = this.map.get(Pointers.hardPointer(input));
			if (pointer != null) {
				final GValue output = pointer.get();
				if (output != null) return output;
				if (Pointers.isValid(pointer)) return null;
				int valid = this.limit - 1;
				for (final Iterator<Entry<Pointer<GInput>, Pointer<GValue>>> iterator = this.map.entrySet().iterator(); iterator.hasNext();) {
					final Entry<Pointer<GInput>, Pointer<GValue>> entry = iterator.next();
					final Pointer<?> key = entry.getKey(), value = entry.getValue();
					if (valid != 0) {
						if (!Pointers.isValid(key) || !Pointers.isValid(value)) {
							iterator.remove();
						} else {
							valid--;
						}
					} else {
						iterator.remove();
					}
				}
			}
			final GValue output = this.getter.get(input);
			this.map.put(Pointers.pointer(this.inputMode, input), Pointers.pointer(this.outputMode, output));
			final int size = this.map.size(), capacity = this.capacity;
			if (size >= capacity) {
				this.capacity = size;
			} else if ((size << 2) <= capacity) {
				(this.map = new LinkedHashMap<>(0, 0.75f, true)).putAll(this.map);
				this.capacity = size;
			}
			return output;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.limit, this.inputMode, this.outputMode, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#translatedGetter(Getter, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedGetter<GInput, GSource, GTarget> implements Getter<GInput, GTarget> {

		public final Getter<? super GInput, ? extends GSource> getter;

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public TranslatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget, final Getter<? super GInput, ? extends GSource> getter) {
			this.getter = Objects.notNull(getter);
			this.toTarget = Objects.notNull(toTarget);
		}

		@Override
		public GTarget get(final GInput input) {
			return this.toTarget.get(this.getter.get(input));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter, this.toTarget);
		}

	}

	/** Diese Klasse implementiert {@link Getters#aggregatedGetter(Getter, Object, Object, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class AggregatedGetter<GItem, GSource, GTarget> implements Getter<Iterable<? extends GItem>, GTarget> {

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public final GTarget emptyTarget;

		public final GTarget mixedTarget;

		public final Getter<? super GItem, GSource> getter;

		public AggregatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget, final GTarget emptyTarget, final GTarget mixedTarget,
			final Getter<? super GItem, GSource> getter) {
			this.toTarget = Objects.notNull(toTarget);
			this.emptyTarget = emptyTarget;
			this.mixedTarget = mixedTarget;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GTarget get(final Iterable<? extends GItem> input) {
			if (input == null) return this.emptyTarget;
			final Iterator<? extends GItem> iterator = input.iterator();
			if (!iterator.hasNext()) return this.emptyTarget;
			final GItem item = iterator.next();
			final GSource value = this.getter.get(item);
			while (iterator.hasNext()) {
				final GItem item2 = iterator.next();
				final GSource value2 = this.getter.get(item2);
				if (!Objects.equals(value, value2)) return this.mixedTarget;
			}
			return this.toTarget.get(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toTarget, this.emptyTarget, this.mixedTarget, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#conditionalGetter(Filter, Getter, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class ConditionalGetter<GInput, GOutput> implements Getter<GInput, GOutput> {

		public final Filter<? super GInput> condition;

		public final Getter<? super GInput, ? extends GOutput> acceptGetter;

		public final Getter<? super GInput, ? extends GOutput> rejectGetter;

		public ConditionalGetter(final Filter<? super GInput> condition, final Getter<? super GInput, ? extends GOutput> acceptGetter,
			final Getter<? super GInput, ? extends GOutput> rejectGetter) {
			this.acceptGetter = acceptGetter;
			this.rejectGetter = rejectGetter;
			this.condition = condition;
		}

		@Override
		public GOutput get(final GInput input) {
			if (this.condition.accept(input)) return this.acceptGetter.get(input);
			return this.rejectGetter.get(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.condition, this.acceptGetter, this.rejectGetter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#synchronizedGetter(Object, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedGetter<GInput, GValue> implements Getter<GInput, GValue> {

		public final Object mutex;

		public final Getter<? super GInput, ? extends GValue> getter;

		public SynchronizedGetter(final Getter<? super GInput, ? extends GValue> getter, final Object mutex) {
			this.mutex = Objects.notNull(mutex, this);
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get(final GInput input) {
			synchronized (this.mutex) {
				return this.getter.get(input);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#toFilter(Getter)}. */
	@SuppressWarnings ("javadoc")
	static class GetterFilter<GItem> implements Filter<GItem> {

		public final Getter<? super GItem, Boolean> getter;

		public GetterFilter(final Getter<? super GItem, Boolean> getter) {
			this.getter = Objects.notNull(getter);
		}

		@Override
		public boolean accept(final GItem item) {
			final Boolean result = this.getter.get(item);
			return (result != null) && result.booleanValue();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#toProducer(Getter, Object)}. */
	@SuppressWarnings ("javadoc")
	static class GetterProducer<GInput, GValue> implements Producer<GValue> {

		public final GInput input;

		public final Getter<? super GInput, ? extends GValue> getter;

		public GetterProducer(final Getter<? super GInput, ? extends GValue> getter, final GInput input) {
			this.input = input;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get() {
			return this.getter.get(this.input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#toComparable(Getter)}. */
	@SuppressWarnings ("javadoc")
	static class GetterComparable<GInput> implements Comparable<GInput> {

		public final Getter<? super GInput, ? extends Number> getter;

		public GetterComparable(final Getter<? super GInput, ? extends Number> getter) {
			this.getter = Objects.notNull(getter);
		}

		@Override
		public int compareTo(final GInput input) {
			final Number result = this.getter.get(input);
			return result != null ? result.intValue() : 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.emptyField()}. */
	@SuppressWarnings ("javadoc")
	public static <GValue> Getter<Object, GValue> emptyGetter() {
		return Fields.emptyField();
	}

	/** Diese Methode gibt einen {@link Getter} zurück, welcher stets die gegebene Ausgabe liefert.
	 *
	 * @param <GValue> Typ der Ausgabe.
	 * @param value Ausgabe.
	 * @return {@code value}-{@link Getter}. */
	public static <GValue> Getter<Object, GValue> valueGetter(final GValue value) {
		return Fields.valueField(value);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Fields.nativeField(Natives.parse(memberText))}, wobei eine {@link Class} zu einer Ausnahme führt.
	 *
	 * @see #nativeGetter(java.lang.reflect.Field)
	 * @see #nativeGetter(java.lang.reflect.Method)
	 * @see #nativeGetter(java.lang.reflect.Constructor)
	 * @see Natives#parse(String)
	 * @param <GInput> Typ des Datensatzes.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param memberText Feld-, Methoden- oder Konstruktortext.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst oder eine {@link Class} bzw. ein nicht zugrifbares
	 *         Objekt liefert. */
	public static <GInput, GOutput> Getter<GInput, GOutput> nativeGetter(final String memberText) throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Getters.nativeGetter((java.lang.reflect.Field)object);
		if (object instanceof java.lang.reflect.Method) return Getters.nativeGetter((java.lang.reflect.Method)object);
		if (object instanceof java.lang.reflect.Constructor<?>) return Getters.nativeGetter((java.lang.reflect.Constructor<?>)object);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeField(field)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> nativeGetter(final java.lang.reflect.Field field) throws NullPointerException {
		return Fields.nativeField(field);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.nativeField(fieldOwner, fieldName)}. */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> nativeGetter(final Class<? extends GInput> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(fieldOwner, fieldName);
	}

	// TODO doku
	/** Diese Methode gibt einen {@link Getter} zur gegebenen {@link java.lang.reflect.Method nativen Methode} zurück.<br>
	 * Bei einer Klassenmethode liefert der erzeugte {@link Getter} für eine Eingabe {@code input} {@code method.invoke(null, input)}, bei einer Objektmethode
	 * liefert er dagegen {@code method.invoke(input)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ des Datensatzes.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method Native Methode.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl der nativen Methode ungültig oder die Methode nicht zugreifbar ist. */
	public static <GInput, GOutput> Getter<GInput, GOutput> nativeGetter(final java.lang.reflect.Method method)
		throws NullPointerException, IllegalArgumentException {
		return new MethodGetter<>(method);
	}

	/** Diese Methode gibt einen {@link Getter} zum gegebenen {@link java.lang.reflect.Constructor nativen Kontruktor} zurück.<br>
	 * Der erzeugte {@link Getter} liefert für eine Eingabe {@code input} {@code constructor.newInstance(input)}.
	 *
	 * @see java.lang.reflect.Constructor#newInstance(Object...)
	 * @param <GInput> Typ des Datensatzes.
	 * @param <GOutput> Typ des Werts.
	 * @param constructor Nativer Kontruktor.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl des nativen Konstruktor ungültig, er nicht zugreifbar oder er nicht statisch ist. */
	public static <GInput, GOutput> Getter<GInput, GOutput> nativeGetter(final java.lang.reflect.Constructor<?> constructor)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorGetter<>(constructor);
	}

	/** Diese Methode gibt den neutralen {@link Getter} zurück, dessen Ausgabe gleich seiner Eingabe ist.
	 *
	 * @param <GInput> Typ der Ein-/Ausgabe.
	 * @return {@code neutral}-{@link Getter}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Getter<GInput, GInput> neutralGetter() {
		return (Getter<GInput, GInput>)NeutralGetter.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.defaultGetter(getter, null)}.
	 *
	 * @see #defaultGetter(Getter, Object) **/
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> defaultGetter(final Getter<? super GInput, GValue> getter) throws NullPointerException {
		return Getters.defaultGetter(getter, null);
	}

	/** Diese Methode ist eine effiziente Alternative zu {@code Getters.conditionalGetter(Filters.nullFilter(), getter, Getters.valueGetter(value))}.
	 *
	 * @see Filters#nullFilter()
	 * @see #valueGetter(Object)
	 * @see #conditionalGetter(Filter, Getter, Getter) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> defaultGetter(final Getter<? super GInput, GValue> getter, final GValue value)
		throws NullPointerException {
		return new DefaultGetter<>(getter, value);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.bufferedGetter(-1, Pointers.SOFT, Pointers.SOFT, getter)}.
	 *
	 * @see #bufferedGetter(int, int, int, Getter) **/
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> bufferedGetter(final Getter<? super GInput, ? extends GValue> getter) throws NullPointerException {
		return Getters.bufferedGetter(-1, Pointers.SOFT, Pointers.SOFT, getter);
	}

	/** Diese Methode gibt einen gepufferten {@link Getter} zurück, der die zu seinen Eingaben über die gegebene {@link Getter Eigenschaft} ermittelten Werte
	 * intern in einer {@link LinkedHashMap} zur Wiederverwendung vorhält. Die Schlüssel der {@link LinkedHashMap} werden dabei als {@link Pointer} auf Eingaben
	 * und die Werte als {@link Pointer} auf die Werte bestückt.
	 *
	 * @see Pointers#pointer(int, Object)
	 * @param <GInput> Typ des Datensatzes sowie der Datensätze in den Schlüsseln der internen {@link LinkedHashMap}.
	 * @param <GValue> Typ der Werte sowie der Datensätze in den Werten der internen {@link LinkedHashMap}.
	 * @param limit Maximum für die Anzahl der Einträge in der internen {@link LinkedHashMap}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link LinkedHashMap} erzeugt werden
	 *        ({@link Pointers#HARD}, {@link Pointers#SOFT}, {@link Pointers#WEAK}).
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Wert-Datensätze für die Werte der {@link LinkedHashMap} erzeugt werden ({@link Pointers#HARD},
	 *        {@link Pointers#SOFT}, {@link Pointers#WEAK}).
	 * @param getter Eigenschaft, die gepuffert werden soll.
	 * @return {@code buffered}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Pointers#pointer(int, Object)} eine entsprechende Ausnahme auslöst. */
	public static <GInput, GValue> Getter<GInput, GValue> bufferedGetter(final int limit, final int inputMode, final int outputMode,
		final Getter<? super GInput, ? extends GValue> getter) throws NullPointerException, IllegalArgumentException {
		return new BufferedGetter<>(limit, inputMode, outputMode, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.mappingField(mapping)}. */
	@SuppressWarnings ({"javadoc", "unchecked"})
	public static <GValue> Getter<Object, GValue> mappingGetter(final Map<?, ? extends GValue> mapping) {
		return (Getter<Object, GValue>)Fields.mappingField(mapping);
	}

	public static <GSource, GTarget, GValue> Getter<GTarget, GValue> navigatedGetter(final Getter<? super GTarget, ? extends GSource> toSource,
		final Getter<? super GSource, ? extends GValue> getter) throws NullPointerException {
		return Getters.translatedGetter(getter, toSource);
	}

	/** Diese Methode gibt einen navigierten bzw. verketteten {@link Getter} zurück.<br>
	 * Der erzeugte {@link Getter} liefert für eine Eingabe {@code input} den Wert {@code getter.get(navigator.get(input))}.
	 *
	 * @param <GInput> Typ des Datensatzes des erzeugten sowie der Eingabe des ersten {@link Getter}.
	 * @param <GSource> Typ der Ausgabe des ersten sowie der Eingabe des zweiten {@link Getter}.
	 * @param <GTarget> Typ der Ausgabe des zweiten sowie der Ausgabe des erzeugten {@link Getter}.
	 * @param toTarget {@link Getter} zur Navigation.
	 * @param getter {@link Getter} zum Lesen.
	 * @return {@code translated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code getter} {@code null} ist. */
	public static <GInput, GSource, GTarget> Getter<GInput, GTarget> translatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GInput, ? extends GSource> getter) throws NullPointerException {
		return new TranslatedGetter<>(toTarget, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.aggregatedGetter(null, null, getter)}.
	 *
	 * @see #neutralGetter()
	 * @see #aggregatedGetter(Object, Object, Getter) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final Getter<? super GItem, GValue> getter)
		throws NullPointerException {
		return Getters.aggregatedGetter(null, null, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.aggregatedGetter(Getters.neutralGetter(), emptyTarget, mixedTarget, getter)}.
	 *
	 * @see #neutralGetter()
	 * @see #aggregatedGetter(Getter, Object, Object, Getter) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final GValue emptyTarget, final GValue mixedTarget,
		final Getter<? super GItem, GValue> getter) throws NullPointerException {
		return Getters.aggregatedGetter(Getters.<GValue>neutralGetter(), emptyTarget, mixedTarget, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.aggregatedGetter(toTarget, null, null, getter)}.
	 *
	 * @see #aggregatedGetter(Getter, Object, Object, Getter) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GTarget, GSource> Getter<Iterable<? extends GItem>, GTarget> aggregatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GItem, GSource> getter) throws NullPointerException {
		return Getters.aggregatedGetter(toTarget, null, null, getter);
	}

	/** Diese Methode gibt einen aggregierten {@link Getter} zurück, welcher den formatierten Wert einer {@link Getter Eigenschaft} der Elemente seiner
	 * iterierbaren Eingabe oder einen der gegebenen Standardwerte liefert.<br>
	 * Wenn die iterierbare Eingabe des erzeugten {@link Getter} {@code null} oder leer ist, liefert dieser den Leerwert {@code emptyTarget}. Wenn die über die
	 * gegebene {@link Getter Eigenschaft} {@code getter} ermittelten Werte nicht unter allen Elementen der iterierbaren Eingabe {@link Objects#equals(Object)
	 * äquivalent} sind, liefert der zeugte {@link Getter} den Mischwert {@code mixedTarget}. Andernfalls liefert er diesen äquivalenten, gemäß dem gegebenen
	 * {@link Getter Leseformat} {@code format} umgewandelten, Wert.
	 *
	 * @param <GItem> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GSource> Typ des Werts der Eigenschaft der Elemente in der iterierbaren Eingabe.
	 * @param <GTarget> Typ des Werts des gelieferten {@link Getter}.
	 * @param toTarget Leseformat zur Umwandlung des Werts der Eigenschaft der Elemente in den Wert des gelieferten {@link Getter}.
	 * @param emptyTarget Leerwert.
	 * @param mixedTarget Mischwert.
	 * @param getter Eigenschaft der Elemente in der iterierbaren Eingabe.
	 * @return {@code aggregated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code property} bzw. {@code getFormat} {@code null} ist. */
	public static <GItem, GSource, GTarget> Getter<Iterable<? extends GItem>, GTarget> aggregatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget,
		final GTarget emptyTarget, final GTarget mixedTarget, final Getter<? super GItem, GSource> getter) throws NullPointerException {
		return new AggregatedGetter<>(toTarget, emptyTarget, mixedTarget, getter);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der über die Weiterleitug der Eingabe an einen der gegebenen {@link Getter Eigenschaften} mit Hilfe des
	 * gegebenen {@link Filter} entscheiden.<br>
	 * Wenn der {@link Filter} eine Eingabe akzeptiert, liefert der erzeugte {@link Getter} den Wert der {@link Getter Eigenschaft} {@code acceptGetter}.
	 * Andernfalls liefert er den Wert der {@link Getter Eigenschaft} {@code rejectGetter}. Der erzeugte {@link Getter} liefert für eine Eingabe {@code input}
	 * damit {@code (condition.accept(input) ? acceptGetter : rejectGetter).get(input)}.
	 *
	 * @param <GInput> Typ des Datensatzes.
	 * @param <GOutput> Typ des Werts.
	 * @param condition Bedingung.
	 * @param acceptGetter Eigenschaft zum Lesen des Werts akzeptierter Eingaben.
	 * @param rejectGetter Eigenschaft zum lesen des Werts abgelehntenr Eingaben.
	 * @return {@code conditional}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code condition}, {@code acceptGetter} bzw. {@code rejectGetter} {@code null} ist. */
	public static <GInput, GOutput> Getter<GInput, GOutput> conditionalGetter(final Filter<? super GInput> condition,
		final Getter<? super GInput, ? extends GOutput> acceptGetter, final Getter<? super GInput, ? extends GOutput> rejectGetter) throws NullPointerException {
		return new ConditionalGetter<>(condition, acceptGetter, rejectGetter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.synchronizedGetter(getter, getter)}.
	 *
	 * @see #synchronizedGetter(Object, Getter) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> synchronizedGetter(final Getter<? super GInput, ? extends GValue> getter) throws NullPointerException {
		return Getters.synchronizedGetter(getter, getter);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der den gegebenen {@link Getter} via {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Getter} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param getter {@link Getter}.
	 * @return {@code synchronized}-{@link Getter}.
	 * @throws NullPointerException Wenn der {@code getter} bzw. {@code mutex} {@code null} ist. */
	public static <GInput, GValue> Getter<GInput, GValue> synchronizedGetter(final Object mutex, final Getter<? super GInput, ? extends GValue> getter)
		throws NullPointerException {
		return new SynchronizedGetter<>(getter, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.compositeField(getter, Fields.emptyField())}.
	 *
	 * @see Fields#compositeField(Getter, Setter) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Field<GInput, GValue> toField(final Getter<? super GInput, ? extends GValue> getter) throws NullPointerException {
		return Fields.compositeField(getter, Fields.emptyField());
	}

	/** Diese Methode gibt einen {@link Filter} als Adapter zu einer {@code boolean}-{@link Getter Eigenschaft} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} entspricht {@code Boolean.TRUE.equals(getter.get(input))}.
	 *
	 * @param <GInput> Typ des Datensatzes.
	 * @param getter Eigenschaft mit {@code boolean}-Wert.
	 * @return {@link Filter} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GInput> Filter<GInput> toFilter(final Getter<? super GInput, Boolean> getter) throws NullPointerException {
		return new GetterFilter<>(getter);
	}

	/** Diese Methode gibt ein {@link Comparable} als Adapter zu einer {@link Number}-{@link Getter Eigenschaft} zurück.<br>
	 * Der Vergleichswert einer Eingabe {@code input} entspricht {@code getter.get(input).intValue()}, wenn diese nicht {@code null} ist. Andernfalls ist der
	 * Vergleichswert {@code 0}.
	 *
	 * @param <GInput> Typ des Datensatzes.
	 * @param getter Eigenschaft mit {@link Number}-Wert.
	 * @return {@link Comparable} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GInput> Comparable<GInput> toComparable(final Getter<? super GInput, ? extends Number> getter) throws NullPointerException {
		return new GetterComparable<>(getter);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.toProducer(getter, null)}.
	 *
	 * @see #toProducer(Getter, Object) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Producer<GValue> toProducer(final Getter<Object, ? extends GValue> getter) throws NullPointerException {
		return Getters.toProducer(getter, null);
	}

	/** Diese Methode gibt einen {@link Producer} zurück, der mit der gegebenen Eingabe an den gegebenen {@link Getter} delegiert.
	 *
	 * @param <GInput> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param getter {@link Getter}.
	 * @param input Eingabe.
	 * @return {@link Getter}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GInput, GValue> Producer<GValue> toProducer(final Getter<? super GInput, ? extends GValue> getter, final GInput input)
		throws NullPointerException {
		return new GetterProducer<>(getter, input);
	}

}
