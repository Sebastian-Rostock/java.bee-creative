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
import bee.creative.lang.Natives;
import bee.creative.lang.Objects;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Getter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Getters {

	/** Diese Klasse implementiert den leeren {@link Getter3}, welcher beim {@link #get(Object) Lesen} stets {@code null} liefert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyGetter extends AbstractGetter<Object, Object> {

		public static final Getter3<?, ?> INSTANCE = new EmptyGetter();

	}

	/** Diese Klasse implementiert den neutralen {@link Getter3}, welcher beim {@link #get(Object) Lesen} stets die gegebene Eingabe als Wert liefert. */
	@SuppressWarnings ("javadoc")
	public static class NeutralGetter extends AbstractGetter<Object, Object> {

		public static final Getter3<?, ?> INSTANCE = new NeutralGetter();

		@Override
		public Object get(final Object item) {
			return item;
		}

	}

	public static class MethodGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Method target;

		public MethodGetter(final Method target, final boolean forceAccessible) {
			if (target.getParameterTypes().length != (Modifier.isStatic(target.getModifiers()) ? 1 : 0)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		@SuppressWarnings ("unchecked")
		public GValue get(final GItem item) {
			try {
				final GValue result;
				if (Modifier.isStatic(this.target.getModifiers())) {
					result = (GValue)this.target.invoke(null, item);
				} else {
					result = (GValue)this.target.invoke(item);
				}
				return result;
			} catch (final IllegalAccessException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	public static class ConstructorGetter<GItem, GOutput> extends AbstractGetter<GItem, GOutput> {

		public final Constructor<?> target;

		public ConstructorGetter(final Constructor<?> target, final boolean forceAccessible) {
			if (!Modifier.isStatic(target.getModifiers()) || (target.getParameterTypes().length != 1)) throw new IllegalArgumentException();
			this.target = forceAccessible ? Natives.forceAccessible(target) : Objects.notNull(target);
		}

		@Override
		public GOutput get(final GItem item) {
			try {
				@SuppressWarnings ("unchecked")
				final GOutput result = (GOutput)this.target.newInstance(item);
				return result;
			} catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
				throw new IllegalArgumentException(cause);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.target.isAccessible());
		}

	}

	public static class DefaultGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Getter<? super GItem, GValue> target;

		public final GValue value;

		public DefaultGetter(final Getter<? super GItem, GValue> target, final GValue value) {
			this.target = Objects.notNull(target);
			this.value = value;
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.value;
			return this.target.get(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.value);
		}

	}

	public static class BufferedGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final int limit;

		public final byte inputMode;

		public final byte outputMode;

		public final Getter<? super GItem, ? extends GValue> getter;

		Map<Pointer<GItem>, Pointer<GValue>> buffer = new LinkedHashMap<>(0, 0.75f, true);

		int capacity = 0;

		public BufferedGetter(final int limit, final int inputMode, final int outputMode, final Getter<? super GItem, ? extends GValue> getter) {
			Pointers.from(inputMode, null);
			Pointers.from(outputMode, null);
			this.limit = limit;
			this.inputMode = (byte)inputMode;
			this.outputMode = (byte)outputMode;
			this.getter = Objects.notNull(getter);
		}

		@Override
		public GValue get(final GItem item) {
			final Pointer<GValue> pointer = this.buffer.get(Pointers.fromHard(item));
			if (pointer != null) {
				final GValue output = pointer.get();
				if (output != null) return output;
				if (Pointers.isValid(pointer)) return null;
				int valid = this.limit - 1;
				for (final Iterator<Entry<Pointer<GItem>, Pointer<GValue>>> iterator = this.buffer.entrySet().iterator(); iterator.hasNext();) {
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
			this.buffer.put(Pointers.from(this.inputMode, item), Pointers.from(this.outputMode, output));
			final int size = this.buffer.size(), capacity = this.capacity;
			if (size >= capacity) {
				this.capacity = size;
			} else if ((size << 2) <= capacity) {
				(this.buffer = new LinkedHashMap<>(0, 0.75f, true)).putAll(this.buffer);
				this.capacity = size;
			}
			return output;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.limit, this.inputMode, this.outputMode, this.getter);
		}

	}

	public static class ConcatGetter<GItem, GSource, GTarget> extends AbstractGetter<GItem, GTarget> {

		public final Getter<? super GItem, ? extends GSource> target;

		public final Getter<? super GSource, ? extends GTarget> trans;

		public ConcatGetter(final Getter<? super GItem, ? extends GSource> target, final Getter<? super GSource, ? extends GTarget> trans) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public GTarget get(final GItem item) {
			return this.trans.get(this.target.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans);
		}

	}

	/** Diese Klasse implementiert . /** Diese Methode gibt einen aggregierten {@link Getter} zurück, welcher den formatierten Wert einer {@link Getter
	 * Eigenschaft} der Elemente seiner iterierbaren Eingabe oder einen der gegebenen Standardwerte liefert. Wenn die iterierbare Eingabe des erzeugten
	 * {@link Getter} {@code null} oder leer ist, liefert dieser den Leerwert {@code emptyTarget}. Wenn die über die gegebene {@link Getter Eigenschaft}
	 * {@code getter} ermittelten Werte nicht unter allen Elementen der iterierbaren Eingabe {@link Objects#equals(Object) äquivalent} sind, liefert der zeugte
	 * {@link Getter} den Mischwert {@code mixedTarget}. Andernfalls liefert er diesen äquivalenten, gemäß dem gegebenen {@link Getter Leseformat} {@code format}
	 * umgewandelten, Wert. */
	public static class AggregatedGetter<GItem extends Iterable<? extends GItem2>, GValue, GItem2, GValue2> extends AbstractGetter<GItem, GValue> {

		public final Getter<? super GItem2, GValue2> target;

		public final Getter<? super GValue2, ? extends GValue> trans;

		public final Getter<? super GItem, ? extends GValue> empty;

		public final Getter<? super GItem, ? extends GValue> mixed;

		public AggregatedGetter(final Getter<? super GItem2, GValue2> target, final Getter<? super GValue2, ? extends GValue> trans,
			final Getter<? super GItem, ? extends GValue> empty, final Getter<? super GItem, ? extends GValue> mixed) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
			this.empty = Objects.notNull(empty);
			this.mixed = Objects.notNull(mixed);
		}

		@Override
		public GValue get(final GItem item) {
			if (item == null) return this.empty.get(item);
			final Iterator<? extends GItem2> iterator = item.iterator();
			if (!iterator.hasNext()) return this.empty.get(item);
			final GItem2 entry = iterator.next();
			final GValue2 value = this.target.get(entry);
			while (iterator.hasNext()) {
				final GItem2 item2 = iterator.next();
				final GValue2 value2 = this.target.get(item2);
				if (!Objects.equals(value, value2)) return this.mixed.get(item);
			}
			return this.trans.get(value);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.trans, this.empty, this.mixed);
		}

	}

	public static class SynchronizedGetter<GItem, GValue> extends AbstractGetter<GItem, GValue> {

		public final Getter<? super GItem, ? extends GValue> target;

		public final Object mutex;

		public SynchronizedGetter(final Getter<? super GItem, ? extends GValue> getter, final Object mutex) {
			this.target = Objects.notNull(getter);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public GValue get(final GItem item) {
			synchronized (this.mutex) {
				return this.target.get(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target, this.mutex == this ? null : this.mutex);
		}

	}

	static class SourceGetter<GValue, GItem> extends AbstractGetter<GItem, GValue> {

		public final Translator<? extends GValue, ? super GItem> target;

		public SourceGetter(final Translator<? extends GValue, ? super GItem> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public GValue get(final Object item) {
			return this.target.toSource(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class TargetGetter<GValue, GItem> extends AbstractGetter<GItem, GValue> {

		public final Translator<? super GItem, ? extends GValue> target;

		public TargetGetter(final Translator<? super GItem, ? extends GValue> target) {
			this.target = Objects.notNull(target);
		}

		@Override
		public GValue get(final Object item) {
			return this.target.toTarget(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class ProducerGetter<GValue> extends AbstractGetter<Object, GValue> {

		public final Producer<? extends GValue> target;

		public ProducerGetter(final Producer<? extends GValue> target) {
			this.target = Objects.notNull(target);
		}

		@Override
		public GValue get(final Object item) {
			return this.target.get();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	/** Diese Klasse implementiert {@link Getters#from(Filter)} */
	static class FilterGetter<GItem> extends AbstractGetter<GItem, Boolean> {
	
		public final Filter<? super GItem> filter;
	
		public FilterGetter(final Filter<? super GItem> filter) {
			this.filter = filter;
		}
	
		@Override
		public Boolean get(final GItem item) {
			return Boolean.valueOf(this.filter.accept(item));
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter);
		}
	
	}

	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Getter3<GItem, GValue> empty() {
		return (Getter3<GItem, GValue>)EmptyGetter.INSTANCE;
	}

	@SuppressWarnings ("unchecked")
	public static <GItem> Getter3<GItem, GItem> neutral() {
		return (Getter3<GItem, GItem>)NeutralGetter.INSTANCE;
	}

	/** Diese Methode gibt einen navigierten {@link Getter} zurück. Der erzeugte {@link Getter} liefert für einen Datensatz {@code item} den Wert
	 * {@code toTarget.get(getter.get(item))}.
	 *
	 * @param target {@link Getter} zum Lesen.
	 * @param trans {@link Getter} zur Übersetzung des Werts.
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GSource> Typ des zu übersetzenden Werts.
	 * @param <GTarget> Typ des übersetzten Werts.
	 * @return {@code translated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code toTarget} bzw. {@code getter} {@code null} ist. */
	public static <GItem, GSource, GTarget> Getter3<GItem, GTarget> concat(final Getter<? super GItem, ? extends GSource> target,
		final Getter<? super GSource, ? extends GTarget> trans) throws NullPointerException {
		return new ConcatGetter<>(target, trans);
	}

	/** Diese Methode gibt den gegebenen {@link Consumer} als {@link Consumer3} zurück. Wenn er {@code null} ist, wird {@link Consumers#empty() Consumers.empty()}
	 * geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem, GValue> Getter3<GItem, GValue> from(final Getter<? super GItem, ? extends GValue> target) {
		if (target == null) return empty();
		if (target instanceof Getter3) return (Getter3<GItem, GValue>)target;
		return concat(target, Getters.<GValue>neutral());
	}

	/** Diese Methode gibt einen {@link Getter} als Adapter zu einem {@link Filter} zurück. Für eine Eingabe {@code item} liefert er die Ausgabe
	 * {@code Boolean.valueOf(filter.accept(item))}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@link Filter}-Adapter.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GItem> Getter3<GItem, Boolean> from(final Filter<? super GItem> filter) throws NullPointerException {
		return new FilterGetter<>(filter);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der seinen Datensatz ignoriert und den Wert des gegebenen {@link Producer} liefert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param producer {@link Producer}.
	 * @return {@link Producer}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code producer} {@code null} ist. */
	public static <GValue> Getter3<Object, GValue> from(final Producer<? extends GValue> producer) throws NullPointerException {
		return new ProducerGetter<>(producer);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromNative(String, boolean) Getters.nativeGetter(field, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final String memberText) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(memberText, true);
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Fields.nativeField(Natives.parse(memberText), forceAccessible)}.
	 *
	 * @see Natives#parse(String)
	 * @see Getters#fromNative(java.lang.reflect.Field, boolean)
	 * @see Getters#fromNative(Method, boolean)
	 * @see Getters#fromNative(Constructor, boolean)
	 * @param <GItem> Typ des Datensatzes.
	 * @param <GOutput> Typ des Werts.
	 * @param memberText Pfad einer Methode, eines Konstruktors oder eines Datenfelds.
	 * @param forceAccessible Parameter für die {@link AccessibleObject#setAccessible(boolean) erzwungene Zugreifbarkeit}.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code memberPath} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der Pfad ungültig bzw. sein Ziel nicht zugreifbar ist. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> fromNative(final String memberText, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Field) return Getters.fromNative((java.lang.reflect.Field)object, forceAccessible);
		if (object instanceof Method) return Getters.fromNative((Method)object, forceAccessible);
		if (object instanceof Constructor<?>) return Getters.fromNative((Constructor<?>)object, forceAccessible);
		throw new IllegalArgumentException();
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromNative(java.lang.reflect.Field, boolean) Getters.nativeGetter(field, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final java.lang.reflect.Field field) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(field, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(java.lang.reflect.Field, boolean) Fields.nativeField(field, forceAccessible)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final java.lang.reflect.Field field, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return from(Fields.<GItem, GValue>fromNative(field, forceAccessible));
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromNative(Method, boolean) Getters.nativeGetter(method, true)}. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> fromNative(final Method target) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(target, true);
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
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Method method, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new MethodGetter<>(method, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromNative(Constructor, boolean) Getters.nativeGetter(constructor, true)}. */
	public static <GItem, GOutput> Getter3<GItem, GOutput> fromNative(final Constructor<?> constructor) throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(constructor, true);
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
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Constructor<?> constructor, final boolean forceAccessible)
		throws NullPointerException, IllegalArgumentException {
		return new ConstructorGetter<>(constructor, forceAccessible);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromNative(Class, String, boolean) Getters.nativeGetter(fieldOwner, fieldName, true)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName)
		throws NullPointerException, IllegalArgumentException {
		return Getters.fromNative(fieldOwner, fieldName, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link Fields#fromNative(Class, String, boolean) Fields.nativeField(fieldOwner, fieldName, forceAccessible)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> fromNative(final Class<? extends GItem> fieldOwner, final String fieldName,
		final boolean forceAccessible) throws NullPointerException, IllegalArgumentException {
		return from(Fields.<GItem, GValue>fromNative(fieldOwner, fieldName, forceAccessible));
	}

	public static <GValue> Getter3<Object, GValue> fromValue(final GValue target) throws NullPointerException {
		if (target == null) return Getters.empty();
		return Getters.from(Producers.fromValue(target));
	}

	/** Diese Methode gibt einen {@link Getter} zu {@link Translator#toSource(Object)} des gegebenen {@link Translator} zurück. Für einen Datensatz {@code item}
	 * liefert er den Wert {@code translator.toSource(item)}.
	 *
	 * @param <GSource> Typ der Quellobjekte des {@link Translator} sowie des Werts des erzeugten {@link Getter}.
	 * @param <GTarget> Typ der Zielobjekte des {@link Translator} sowie des Datensatzes des erzeugten {@link Getter}.
	 * @param translator {@link Translator}.
	 * @return {@link Getter}, der Zielobjekte in Quellobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Getter3<GTarget, GSource> fromSource(final Translator<? extends GSource, ? super GTarget> translator)
		throws NullPointerException {
		return new SourceGetter<>(translator);
	}

	/** Diese Methode gibt einen {@link Getter} zu {@link Translator#toTarget(Object)} des gegebenen {@link Translator} zurück. Für einen Datensatz {@code item}
	 * liefert er den Wert {@code translator.toTarget(item)}.
	 *
	 * @param <GSource> Typ der Quellobjekte des {@link Translator} sowie des Datensatzes des erzeugten {@link Getter}.
	 * @param <GTarget> Typ der Zielobjekte des {@link Translator} sowie des Werts des erzeugten {@link Getter}.
	 * @param translator {@link Translator}.
	 * @return {@link Getter}, der Quellobjekte in Zielobjekte des {@code translator} umwandelt.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Getter3<GSource, GTarget> fromTarget(final Translator<? super GSource, ? extends GTarget> translator)
		throws NullPointerException {
		return new TargetGetter<>(translator);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#optionalize(Getter, Object) Getters.optionalize(target, null)}. **/
	public static <GItem, GValue> Getter3<GItem, GValue> optionalize(final Getter<? super GItem, GValue> target) throws NullPointerException {
		return Getters.optionalize(target, null);
	}

	/** Diese Methode einen {@link Getter} zurück, der einen Datensatz zum Lesen des Werts ihrer Eigenschaft nur dann dann an den gegebenen {@link Getter}
	 * delegiert, wenn der Datensatz nicht {@code null} ist. Andernfalls wird der gegebene Rückfallwert geliefert. Sie ist damit eine effiziente Alternative zu
	 * {@code Getters.conditionalGetter(Filters.nullFilter(), getter, Getters.valueGetter(value))}.
	 *
	 * @param target {@link Getter} zum Lesen des Werts der Eigenschaft.
	 * @param value Rückfallwert, wenn das Datensatz {@code null} ist.
	 * @see Filters#empty()
	 * @return {@code default}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem, GValue> Getter3<GItem, GValue> optionalize(final Getter<? super GItem, GValue> target, final GValue value) throws NullPointerException {
		return new DefaultGetter<>(target, value);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, int, int, Getter) Getters.bufferedGetter(-1, Pointers.SOFT, Pointers.SOFT, getter)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> buffer(final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException {
		return Getters.buffer(-1, Pointers.SOFT, Pointers.SOFT, getter);
	}

	/** Diese Methode gibt einen gepufferten {@link Getter} zurück, der die zu seinen Eingaben über die gegebene {@link Getter Eigenschaft} ermittelten Werte
	 * intern in einer {@link LinkedHashMap} zur Wiederverwendung vorhält. Die Schlüssel der {@link LinkedHashMap} werden dabei als {@link Pointer} auf Eingaben
	 * und die Werte als {@link Pointer} auf die Werte bestückt.
	 *
	 * @see Pointers#from(int, Object)
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
	 * @throws IllegalArgumentException Wenn {@link Pointers#from(int, Object)} eine entsprechende Ausnahme auslöst. */
	public static <GItem, GValue> Getter3<GItem, GValue> buffer(final int limit, final int inputMode, final int outputMode,
		final Getter<? super GItem, ? extends GValue> getter) throws NullPointerException, IllegalArgumentException {
		return new BufferedGetter<>(limit, inputMode, outputMode, getter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregate(Getter, Getter) Getters.aggregate(target, Getters.neutral())}. */
	public static <GItem, GValue> Getter3<Iterable<? extends GItem>, GValue> aggregate(final Getter<? super GItem, ? extends GValue> target)
		throws NullPointerException {
		return Getters.aggregate(target, Getters.<GValue>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#aggregate(Getter, Getter, Getter, Getter) Getters.aggregate(target, trans, Getters.empty(),
	 * Getters.empty())}. */
	public static <GItem, GValue, GValue2> Getter3<Iterable<? extends GItem>, GValue> aggregate(final Getter<? super GItem, ? extends GValue2> target,
		final Getter<? super GValue2, ? extends GValue> trans) throws NullPointerException {
		return Getters.aggregate(target, trans, Getters.<Object, GValue>empty(), Getters.<Object, GValue>empty());
	}

	public static <GItem extends Iterable<? extends GItem2>, GValue, GItem2, GValue2> Getter3<GItem, GValue> aggregate(
		final Getter<? super GItem2, ? extends GValue2> target, final Getter<? super GValue2, ? extends GValue> trans,
		final Getter<? super GItem, ? extends GValue> empty, final Getter<? super GItem, ? extends GValue> mixed) throws NullPointerException {
		return new AggregatedGetter<>(target, trans, empty, mixed);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Getter, Object) Getters.synchronize(target, target)}. */
	public static <GItem, GValue> Getter3<GItem, GValue> synchronize(final Getter<? super GItem, ? extends GValue> target) throws NullPointerException {
		return Getters.synchronize(target, target);
	}

	public static <GItem, GValue> Getter3<GItem, GValue> synchronize(final Getter<? super GItem, ? extends GValue> target, final Object mutex)
		throws NullPointerException {
		return new SynchronizedGetter<>(target, mutex);
	}

}
