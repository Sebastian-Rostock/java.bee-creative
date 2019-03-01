package bee.creative.util;

import java.lang.reflect.AccessibleObject;
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
	public static abstract class BaseGetter<GItem, GValue> extends BaseObject implements Getter<GItem, GValue> {
	}

	/** Diese Klasse implementiert {@link Getters#neutralGetter()}. */
	@SuppressWarnings ("javadoc")
	public static class NeutralGetter extends BaseGetter<Object, Object> {

		public static final Getter<?, ?> INSTANCE = new NeutralGetter();

		@Override
		public Object get(final Object item) {
			return item;
		}

	}

	/** Diese Klasse implementiert {@link Getters#nativeGetter(Method)}. */
	@SuppressWarnings ("javadoc")
	public static class MethodGetter<GItem, GOutput> implements Getter<GItem, GOutput> {

		public final Method method;

		public MethodGetter(final Method method, final boolean forceAccessible) {
			if (method.getParameterTypes().length != (Modifier.isStatic(method.getModifiers()) ? 1 : 0)) throw new IllegalArgumentException();
			this.method = forceAccessible ? Natives.forceAccessible(method) : Objects.notNull(method);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GOutput get(final GItem item) {
			try {
				final GOutput result;
				if (Modifier.isStatic(this.method.getModifiers())) {
					result = (GOutput)this.method.invoke(null, item);
				} else {
					result = (GOutput)this.method.invoke(item);
				}
				return result;
			} catch (final IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.method, this.method.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Getters#nativeGetter(Constructor)}. */
	@SuppressWarnings ("javadoc")
	public static class ConstructorGetter<GItem, GOutput> implements Getter<GItem, GOutput> {

		public final Constructor<?> constructor;

		public ConstructorGetter(final Constructor<?> constructor, final boolean forceAccessible) {
			if (!Modifier.isStatic(constructor.getModifiers()) || (constructor.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.constructor =  forceAccessible ? Natives.forceAccessible(constructor) : Objects.notNull(constructor);
		}

		@Override
		public GOutput get(final GItem item) {
			try {
				@SuppressWarnings ("unchecked")
				final GOutput result = (GOutput)this.constructor.newInstance(item);
				return result;
			} catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.constructor, this.constructor.isAccessible());
		}

	}

	/** Diese Klasse implementiert {@link Getters#defaultGetter(Object, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class DefaultGetter<GItem, GValue> implements Getter<GItem, GValue> {

		public final Getter<? super GItem, GValue> getter;

		public final GValue value;

		public DefaultGetter(final Getter<? super GItem, GValue> getter, final GValue value) {
			this.getter = Objects.notNull(getter);
			this.value = value;
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.value;
			return this.getter.get(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Getters#bufferedGetter(int, int, int, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class BufferedGetter<GItem, GValue> implements Getter<GItem, GValue> {

		public final int limit;

		public final byte inputMode;

		public final byte outputMode;

		public final Getter<? super GItem, ? extends GValue> getter;

		Map<Pointer<GItem>, Pointer<GValue>> map = new LinkedHashMap<>(0, 0.75f, true);

		int capacity = 0;

		public BufferedGetter(final int limit, final int inputMode, final int outputMode, final Getter<? super GItem, ? extends GValue> getter) {
			Pointers.pointer(inputMode, null);
			Pointers.pointer(outputMode, null);
			this.limit = limit;
			this.inputMode = (byte)inputMode;
			this.outputMode = (byte)outputMode;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get(final GItem item) {
			final Pointer<GValue> pointer = this.map.get(Pointers.hardPointer(item));
			if (pointer != null) {
				final GValue output = pointer.get();
				if (output != null) return output;
				if (Pointers.isValid(pointer)) return null;
				int valid = this.limit - 1;
				for (final Iterator<Entry<Pointer<GItem>, Pointer<GValue>>> iterator = this.map.entrySet().iterator(); iterator.hasNext();) {
					final Entry<Pointer<GItem>, Pointer<GValue>> entry = iterator.next();
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
			final GValue output = this.getter.get(item);
			this.map.put(Pointers.pointer(this.inputMode, item), Pointers.pointer(this.outputMode, output));
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
	public static class TranslatedGetter<GItem, GSource, GTarget> implements Getter<GItem, GTarget> {

		public final Getter<? super GItem, ? extends GSource> getter;

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public TranslatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget, final Getter<? super GItem, ? extends GSource> getter) {
			this.getter = Objects.notNull(getter);
			this.toTarget = Objects.notNull(toTarget);
		}

		@Override
		public GTarget get(final GItem item) {
			return this.toTarget.get(this.getter.get(item));
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
		public GTarget get(final Iterable<? extends GItem> item) {
			if (item == null) return this.emptyTarget;
			final Iterator<? extends GItem> iterator = item.iterator();
			if (!iterator.hasNext()) return this.emptyTarget;
			final GItem entry = iterator.next();
			final GSource value = this.getter.get(entry);
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
	public static class ConditionalGetter<GItem, GOutput> implements Getter<GItem, GOutput> {

		public final Filter<? super GItem> condition;

		public final Getter<? super GItem, ? extends GOutput> acceptGetter;

		public final Getter<? super GItem, ? extends GOutput> rejectGetter;

		public ConditionalGetter(final Filter<? super GItem> condition, final Getter<? super GItem, ? extends GOutput> acceptGetter,
			final Getter<? super GItem, ? extends GOutput> rejectGetter) {
			this.acceptGetter = acceptGetter;
			this.rejectGetter = rejectGetter;
			this.condition = condition;
		}

		@Override
		public GOutput get(final GItem item) {
			if (this.condition.accept(item)) return this.acceptGetter.get(item);
			return this.rejectGetter.get(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.condition, this.acceptGetter, this.rejectGetter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#synchronizedGetter(Object, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedGetter<GItem, GValue> implements Getter<GItem, GValue> {

		public final Object mutex;

		public final Getter<? super GItem, ? extends GValue> getter;

		public SynchronizedGetter(final Object mutex, final Getter<? super GItem, ? extends GValue> getter) {
			this.mutex = Objects.notNull(mutex, this);
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.getter.get(item);
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
		public boolean accept(final GItem entry) {
			final Boolean result = this.getter.get(entry);
			return (result != null) && result.booleanValue();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#toProducer(Object, Getter)}. */
	@SuppressWarnings ("javadoc")
	static class GetterProducer<GItem, GValue> implements Producer<GValue> {

		public final GItem item;

		public final Getter<? super GItem, ? extends GValue> getter;

		public GetterProducer(final GItem item, final Getter<? super GItem, ? extends GValue> getter) {
			this.item = item;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get() {
			return this.getter.get(this.item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.getter);
		}

	}

	/** Diese Klasse implementiert {@link Getters#toComparable(Getter)}. */
	@SuppressWarnings ("javadoc")
	static class GetterComparable<GItem> implements Comparable<GItem> {

		public final Getter<? super GItem, ? extends Number> getter;

		public GetterComparable(final Getter<? super GItem, ? extends Number> getter) {
			this.getter = Objects.notNull(getter);
		}

		@Override
		public int compareTo(final GItem item) {
			final Number result = this.getter.get(item);
			return result != null ? result.intValue() : 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#valueGetter(Object) Getters.valueGetter(null)}. */
	public static <GValue> Getter<Object, GValue> emptyGetter() {
		return Getters.valueGetter(null);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#valueField(Object) Fields.valueField(value)}. */
	public static <GValue> Getter<Object, GValue> valueGetter(final GValue value) {
		return Fields.valueField(value);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#nativeGetter(String, boolean) Getters.nativeGetter(field, true)}. */
	public static <GItem, GOutput> Getter<GItem, GOutput> nativeGetter(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Getters.nativeGetter(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Fields.nativeField(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see Getters#nativeGetter(java.lang.reflect.Field, boolean)
	 * @see Getters#nativeGetter(Method, boolean)
	 * @see Getters#nativeGetter(Constructor, boolean)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GOutput> Typ des Werts.
	 * @param memberText Pfad einer Methode, eines Konstruktors oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GItem, GOutput> Getter<GItem, GOutput> nativeGetter(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Getters.nativeGetter((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Getters.nativeGetter((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Getters.nativeGetter((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#nativeGetter(java.lang.reflect.Field, boolean) Getters.nativeGetter(field, true)}. */
	public static <GItem, GValue> Getter<GItem, GValue> nativeGetter(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Getters.nativeGetter(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(java.lang.reflect.Field, boolean) Fields.nativeField(field, forceAccessible)}. */
	public static <GItem, GValue> Getter<GItem, GValue> nativeGetter(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(field, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#nativeGetter(Method, boolean) Getters.nativeGetter(method, true)}. */
	public static <GItem, GOutput> Getter<GItem, GOutput> nativeGetter(final Method method) throws NullPointerException, IllegalArgumentException {
		return Getters.nativeGetter(method, true);
	}

	/** Diese Methode gibt einen {@link Getter} zur gegebenen {@link Method nativen Methode} zurück. Bei einer Klassenmethode liefert der erzeugte {@link Getter}
	 * für einen Datensatz {@code item} {@code method.invoke(null, item)}, bei einer Objektmethode liefert er dagegen {@code method.invoke(item)}.
	 *
	 * @see Method#invoke(Object, Object...)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param method Native Methode.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl der nativen Methode ungültig oder die Methode nicht zugreifbar ist. */
	public static <GItem, GValue> Getter<GItem, GValue> nativeGetter(final Method method, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodGetter<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#nativeGetter(Constructor, boolean) Getters.nativeGetter(constructor, true)}. */
	public static <GItem, GOutput> Getter<GItem, GOutput> nativeGetter(final Constructor<?> constructor) throws NullPointerException, IllegalArgumentException {
		return Getters.nativeGetter(constructor, true);
	}

	/** Diese Methode gibt einen {@link Getter} zum gegebenen {@link Constructor nativen Kontruktor} zurück. Der erzeugte {@link Getter} liefert für einen
	 * Datensatz {@code item} {@code constructor.newInstance(item)}.
	 *
	 * @see Constructor#newInstance(Object...)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param constructor Nativer Kontruktor.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl des nativen Konstruktor ungültig, er nicht zugreifbar oder er nicht statisch ist. */
	public static <GItem, GValue> Getter<GItem, GValue> nativeGetter(final Constructor<?> constructor, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorGetter<>(constructor, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#nativeGetter(Class, String, boolean) Getters.nativeGetter(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Getter<GItem, GValue> nativeGetter(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Getters.nativeGetter(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#nativeField(Class, String, boolean) Fields.nativeField(fieldOwner, fieldName, forceAccessible)}. */
	public static <GItem, GValue> Getter<GItem, GValue> nativeGetter(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return Fields.nativeField(fieldOwner, fieldName, forceAccessible);
	}

	/** Diese Methode gibt den neutralen {@link Getter} zurück, der den gegebenen Datensatz als Wert liefert.
	 *
	 * @param <GItem> Typ des Datensatzes bzw. Werts.
	 * @return {@code neutral}-{@link Getter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Getter<GItem, GItem> neutralGetter() {
		return (Getter<GItem, GItem>)NeutralGetter.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#defaultGetter(Object, Getter) Getters.defaultGetter(null, getter)}. **/
	public static <GItem, GValue> Getter<GItem, GValue> defaultGetter(final Getter<? super GItem, GValue> getter) throws NullPointerException {
		return Getters.defaultGetter(null, getter);
	}

	/** Diese Methode einen {@link Getter} zurück, der einen Datensatz zum Lesen des Werts ihrer Eigenschaft nur dann dann an den gegebenen {@link Getter}
	 * delegiert, wenn der Datensatz nicht {@code null} ist. Andernfalls wird der gegebene Rückfallwert geliefert. Sie ist damit eine effiziente Alternative zu
	 * {@link #conditionalGetter(Filter, Getter, Getter) Getters.conditionalGetter(Filters.nullFilter(), getter, Getters.valueGetter(value))}.
	 *
	 * @see Filters#nullFilter()
	 * @see Getters#valueGetter(Object)
	 * @param value Rückfallwert, wenn das Datensatz {@code null} ist.
	 * @param getter {@link Getter} zum Lesen des Werts der Eigenschaft.
	 * @return {@code default}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem, GValue> Getter<GItem, GValue> defaultGetter(final GValue value, final Getter<? super GItem, GValue> getter)
		throws NullPointerException {
		return new DefaultGetter<>(getter, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #bufferedGetter(int, int, int, Getter) Getters.bufferedGetter(-1, Pointers.SOFT, Pointers.SOFT, getter)}. */
	public static <GItem, GValue> Getter<GItem, GValue> bufferedGetter(final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException {
		return Getters.bufferedGetter(-1, Pointers.SOFT, Pointers.SOFT, getter);
	}

	/** Diese Methode gibt einen gepufferten {@link Getter} zurück, der die zu seinen Eingaben über die gegebene {@link Getter Eigenschaft} ermittelten Werte
	 * intern in einer {@link LinkedHashMap} zur Wiederverwendung vorhält. Die Schlüssel der {@link LinkedHashMap} werden dabei als {@link Pointer} auf Eingaben
	 * und die Werte als {@link Pointer} auf die Werte bestückt.
	 *
	 * @see Pointers#pointer(int, Object)
	 * @param <GItem> Typ des Datensatzes sowie der Datensätze in den Schlüsseln der internen {@link LinkedHashMap}.
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
	public static <GItem, GValue> Getter<GItem, GValue> bufferedGetter(final int limit, final int inputMode, final int outputMode,
		final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException, IllegalArgumentException {
		return new BufferedGetter<>(limit, inputMode, outputMode, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#mappingField(Map) Fields.mappingField(mapping)}. */
	@SuppressWarnings ("unchecked")
	public static <GValue> Getter<Object, GValue> mappingGetter(final Map<?, ? extends GValue> mapping) {
		return (Getter<Object, GValue>)Fields.mappingField(mapping);
	}

	/** Diese Methode gibt einen übersetzten {@link Getter} zurück. Der erzeugte {@link Getter} liefert für einen Datensatz {@code item} den Wert
	 * {@code getter.get(toSource.get(item))}.
	 *
	 * @param <GSource> Typ des zu übersetzenden Datensatzes.
	 * @param <GTarget> Typ des übersetzten Datensatzes.
	 * @param <GValue> Typ des Werts der Eigenschaft.
	 * @param toSource {@link Getter} zur Übersetzung des Datensatzes.
	 * @param getter {@link Getter} zur Manipulation.
	 * @return {@code navigated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code getter} {@code null} ist. */
	public static <GSource, GTarget, GValue> Getter<GTarget, GValue> navigatedGetter(final Getter<? super GTarget, ? extends GSource> toSource,
		final Getter<? super GSource, ? extends GValue> getter) throws NullPointerException {
		return Getters.translatedGetter(getter, toSource);
	}

	/** Diese Methode gibt einen navigierten {@link Getter} zurück. Der erzeugte {@link Getter} liefert für einen Datensatz {@code item} den Wert
	 * {@code toTarget.get(getter.get(item))}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GSource> Typ des zu übersetzenden Werts.
	 * @param <GTarget> Typ des übersetzten Werts.
	 * @param toTarget {@link Getter} zur Übersetzung des Werts.
	 * @param getter {@link Getter} zum Lesen.
	 * @return {@code translated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code toTarget} bzw. {@code getter} {@code null} ist. */
	public static <GItem, GSource, GTarget> Getter<GItem, GTarget> translatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GItem, ? extends GSource> getter) throws NullPointerException {
		return new TranslatedGetter<>(toTarget, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Object, Object, Getter) Getters.aggregatedGetter(null, null, getter)}. */
	public static <GItem, GValue> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final Getter<? super GItem, GValue> getter)
		throws NullPointerException {
		return Getters.aggregatedGetter(null, null, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Getter, Object, Object, Getter) Getters.aggregatedGetter(Getters.neutralGetter(),
	 * emptyTarget, mixedTarget, getter)}.
	 *
	 * @see #neutralGetter() */
	public static <GItem, GValue> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final GValue emptyTarget, final GValue mixedTarget,
		final Getter<? super GItem, GValue> getter) throws NullPointerException {
		return Getters.aggregatedGetter(Getters.<GValue>neutralGetter(), emptyTarget, mixedTarget, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregatedGetter(Getter, Object, Object, Getter) Getters.aggregatedGetter(toTarget, null, null,
	 * getter)}. */
	public static <GItem, GTarget, GSource> Getter<Iterable<? extends GItem>, GTarget> aggregatedGetter(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Getter<? super GItem, GSource> getter) throws NullPointerException {
		return Getters.aggregatedGetter(toTarget, null, null, getter);
	}

	/** Diese Methode gibt einen aggregierten {@link Getter} zurück, welcher den formatierten Wert einer {@link Getter Eigenschaft} der Elemente seiner
	 * iterierbaren Eingabe oder einen der gegebenen Standardwerte liefert. Wenn die iterierbare Eingabe des erzeugten {@link Getter} {@code null} oder leer ist,
	 * liefert dieser den Leerwert {@code emptyTarget}. Wenn die über die gegebene {@link Getter Eigenschaft} {@code getter} ermittelten Werte nicht unter allen
	 * Elementen der iterierbaren Eingabe {@link Objects#equals(Object) äquivalent} sind, liefert der zeugte {@link Getter} den Mischwert {@code mixedTarget}.
	 * Andernfalls liefert er diesen äquivalenten, gemäß dem gegebenen {@link Getter Leseformat} {@code format} umgewandelten, Wert.
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

	/** Diese Methode gibt einen {@link Getter} zurück, der über die Weiterleitug eines Datensatzes an einen der gegebenen {@link Getter} mit Hilfe des gegebenen
	 * {@link Filter} entscheiden. Wenn der {@link Filter} einen Datensatz akzeptiert, liefert der erzeugte {@link Getter} den Wert der Eigenschaft dieses
	 * Datensatzes über {@code acceptGetter}. Andernfalls liefert er ihn über {@code rejectGetter}. Der erzeugte {@link Getter} liefert für einen Datensatz
	 * {@code item} damit {@code (condition.accept(item) ? acceptGetter : rejectGetter).get(item)}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param condition Bedingung.
	 * @param acceptGetter Eigenschaft zum Lesen des Werts akzeptierter Datensätze.
	 * @param rejectGetter Eigenschaft zum lesen des Werts abgelehnter Datensätze.
	 * @return {@code conditional}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code condition}, {@code acceptGetter} bzw. {@code rejectGetter} {@code null} ist. */
	public static <GItem, GValue> Getter<GItem, GValue> conditionalGetter(final Filter<? super GItem> condition,
		final Getter<? super GItem, ? extends GValue> acceptGetter, final Getter<? super GItem, ? extends GValue> rejectGetter) throws NullPointerException {
		return new ConditionalGetter<>(condition, acceptGetter, rejectGetter);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronizedGetter(Object, Getter) Getters.synchronizedGetter(getter, getter)}. */
	public static <GItem, GValue> Getter<GItem, GValue> synchronizedGetter(final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException {
		return Getters.synchronizedGetter(getter, getter);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der den gegebenen {@link Getter} über {@code synchronized(mutex)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Getter} als Synchronisationsobjekt verwendet.
	 *
	 * @param <GItem> Typ des Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param getter {@link Getter}.
	 * @return {@code synchronized}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code getter} bzw. {@code mutex} {@code null} ist. */
	public static <GItem, GValue> Getter<GItem, GValue> synchronizedGetter(final Object mutex, final Getter<? super GItem, ? extends GValue> getter)
		throws NullPointerException {
		return new SynchronizedGetter<>(mutex, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#compositeField(Getter, Setter) Fields.compositeField(getter, Setters.emptySetter())}.
	 *
	 * @see Setters#emptySetter() */
	public static <GItem, GValue> Field<GItem, GValue> toField(final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException {
		return Fields.compositeField(getter, Setters.emptySetter());
	}

	/** Diese Methode gibt einen {@link Filter} als Adapter zu einem {@link Boolean}-{@link Getter} zurück. Die Akzeptanz einer Eingabe {@code item} entspricht
	 * {@code Boolean.TRUE.equals(getter.get(item))}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param getter Eigenschaft mit {@link Boolean}-Wert.
	 * @return {@link Filter} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem> Filter<GItem> toFilter(final Getter<? super GItem, Boolean> getter) throws NullPointerException {
		return new GetterFilter<>(getter);
	}

	/** Diese Methode gibt ein {@link Comparable} als Adapter zu einem {@link Number}-{@link Getter} zurück. Der Vergleichswert eines Datensatzes {@code item}
	 * entspricht {@code getter.get(item).intValue()}, wenn diese nicht {@code null} ist. Andernfalls ist der Vergleichswert {@code 0}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param getter Eigenschaft mit {@link Number}-Wert.
	 * @return {@link Comparable} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem> Comparable<GItem> toComparable(final Getter<? super GItem, ? extends Number> getter) throws NullPointerException {
		return new GetterComparable<>(getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#toProducer(Object, Getter) Getters.toProducer(null, getter)}. */
	public static <GItem, GValue> Producer<GValue> toProducer(final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException {
		return Getters.toProducer(null, getter);
	}

	/** Diese Methode gibt einen {@link Producer} zurück, der mit dem gegebenen Datensatz an den gegebenen {@link Getter} delegiert.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GValue> Typ des Werts.
	 * @param item Datensatz.
	 * @param getter {@link Getter}.
	 * @return {@link Getter}-{@link Producer}.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem, GValue> Producer<GValue> toProducer(final GItem item, final Getter<? super GItem, ? extends GValue> getter)
		throws NullPointerException {
		return new GetterProducer<>(item, getter);
	}

}
