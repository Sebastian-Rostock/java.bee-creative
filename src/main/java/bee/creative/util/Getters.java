package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/** Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur {@link Getter}-Konstruktion und -Verarbeitung.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Getters {

	/** Dieses Feld speichert den neutralen {@link Getter}, dessen Ausgabe gleich seiner Eingabe ist. */
	public static final Getter<?, ?> NEUTRAL_GETTER = new Getter<Object, Object>() {

		@Override
		public Object get(final Object input) {
			return input;
		}

		@Override
		public String toString() {
			return "NEUTRAL_GETTER";
		}

	};

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
		if (value == null) return Getters.emptyGetter();
		return new Getter<Object, GValue>() {

			@Override
			public GValue get(final Object input) {
				return value;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("valueGetter", value);
			}

		};
	}

	/** Diese Methode ist effektiv eine Abkürzung für {@code Fields.nativeField(Natives.parse(memberText))}, wobei eine {@link Class} zu einer Ausnahme führt.
	 *
	 * @see #nativeGetter(java.lang.reflect.Field)
	 * @see #nativeGetter(java.lang.reflect.Method)
	 * @see #nativeGetter(java.lang.reflect.Constructor)
	 * @see Natives#parse(String)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param memberText Methoden- oder Konstruktortext.
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

	/** Diese Methode gibt einen {@link Getter} zur gegebenen {@link java.lang.reflect.Method nativen Methode} zurück.<br>
	 * Bei einer Klassenmethode liefert der erzeugte {@link Getter} für eine Eingabe {@code input} {@code method.invoke(null, input)}, bei einer Objektmethode
	 * liefert er dagegen {@code method.invoke(input)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method Native Methode.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl der nativen Methode ungültig oder die Methode nicht zugreifbar ist. */
	public static <GInput, GOutput> Getter<GInput, GOutput> nativeGetter(final java.lang.reflect.Method method)
		throws NullPointerException, IllegalArgumentException {
		try {
			method.setAccessible(true);
		} catch (final SecurityException cause) {
			throw new IllegalArgumentException(cause);
		}
		if (Modifier.isStatic(method.getModifiers())) {
			if (method.getParameterTypes().length != 1) throw new IllegalArgumentException();
			return new Getter<GInput, GOutput>() {

				@Override
				public GOutput get(final GInput input) {
					try {
						@SuppressWarnings ("unchecked")
						final GOutput result = (GOutput)method.invoke(null, input);
						return result;
					} catch (final IllegalAccessException | InvocationTargetException cause) {
						throw new IllegalArgumentException(cause);
					}
				}

				@Override
				public String toString() {
					return Objects.toInvokeString("nativeGetter", Natives.formatMethod(method));
				}

			};
		} else {
			if (method.getParameterTypes().length != 0) throw new IllegalArgumentException();
			return new Getter<GInput, GOutput>() {

				@Override
				public GOutput get(final GInput input) {
					try {
						@SuppressWarnings ("unchecked")
						final GOutput result = (GOutput)method.invoke(input);
						return result;
					} catch (final IllegalAccessException | InvocationTargetException cause) {
						throw new IllegalArgumentException(cause);
					}
				}

				@Override
				public String toString() {
					return Objects.toInvokeString("nativeGetter", Natives.formatMethod(method));
				}

			};
		}
	}

	/** Diese Methode gibt einen {@link Getter} zum gegebenen {@link java.lang.reflect.Constructor nativen Kontruktor} zurück.<br>
	 * Der erzeugte {@link Getter} liefert für eine Eingabe {@code input} {@code constructor.newInstance(input)}.
	 *
	 * @see java.lang.reflect.Constructor#newInstance(Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ des Werts.
	 * @param constructor Nativer Kontruktor.
	 * @return {@code native}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die Parameteranzahl des nativen Konstruktor ungültig, er nicht zugreifbar oder er nicht statisch ist. */
	public static <GInput, GOutput> Getter<GInput, GOutput> nativeGetter(final java.lang.reflect.Constructor<?> constructor)
		throws NullPointerException, IllegalArgumentException {
		try {
			constructor.setAccessible(true);
		} catch (final SecurityException cause) {
			throw new IllegalArgumentException(cause);
		}
		if (!Modifier.isStatic(constructor.getModifiers()) || (constructor.getParameterTypes().length != 1)) throw new IllegalArgumentException();
		return new Getter<GInput, GOutput>() {

			@Override
			public GOutput get(final GInput input) {
				try {
					@SuppressWarnings ("unchecked")
					final GOutput result = (GOutput)constructor.newInstance(input);
					return result;
				} catch (final IllegalAccessException | InstantiationException | InvocationTargetException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("nativeGetter", Natives.formatConstructor(constructor));
			}

		};
	}

	/** Diese Methode gibt den neutralen {@link Getter} zurück, dessen Ausgabe gleich seiner Eingabe ist.
	 *
	 * @param <GInput> Typ der Ein-/Ausgabe.
	 * @return {@link #NEUTRAL_GETTER}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Getter<GInput, GInput> neutralGetter() {
		return (Getter<GInput, GInput>)Getters.NEUTRAL_GETTER;
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
		Objects.assertNotNull(getter);
		return new Getter<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				if (input == null) return value;
				return getter.get(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("defaultGetter", getter, value);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Fields.mappingField(mapping)}. */
	@SuppressWarnings ({"javadoc", "unchecked"})
	public static <GValue> Getter<Object, GValue> mappedGetter(final Map<?, ? extends GValue> mapping) {
		return (Getter<Object, GValue>)Fields.mappingField(mapping);
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
	 * @param <GInput> Typ der Eingabe sowie der Datensätze in den Schlüsseln der internen {@link LinkedHashMap}.
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
		Objects.assertNotNull(getter);
		Pointers.pointer(inputMode, null);
		Pointers.pointer(outputMode, null);
		return new Getter<GInput, GValue>() {

			Map<Pointer<GInput>, Pointer<GValue>> map = new LinkedHashMap<>(0, 0.75f, true);

			int capacity = 0;

			@Override
			public GValue get(final GInput input) {
				final Pointer<GValue> pointer = this.map.get(Pointers.hardPointer(input));
				if (pointer != null) {
					final GValue output = pointer.data();
					if (output != null) return output;
					if (Pointers.isValid(pointer)) return null;
					int valid = limit - 1;
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
				final GValue output = getter.get(input);
				this.map.put(Pointers.pointer(inputMode, input), Pointers.pointer(outputMode, output));
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
				return Objects.toInvokeString("bufferedGetter", limit, inputMode, outputMode, getter);
			}

		};
	}

	/** Diese Methode gibt einen navigierten bzw. verketteten {@link Getter} zurück.<br>
	 * Der erzeugte {@link Getter} liefert für eine Eingabe {@code input} den Wert {@code getter.get(navigator.get(input))}.
	 *
	 * @param <GInput> Typ der Eingabe des erzeugten sowie der Eingabe des ersten {@link Getter}.
	 * @param <GValue> Typ der Ausgabe des ersten sowie der Eingabe des zweiten {@link Getter}.
	 * @param <GOutput> Typ der Ausgabe des zweiten sowie der Ausgabe des erzeugten {@link Getter}.
	 * @param navigator {@link Getter} zur Navigation.
	 * @param getter {@link Getter} zum Lesen.
	 * @return {@code navigated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code getter} {@code null} ist. */
	public static <GInput, GValue, GOutput> Getter<GInput, GOutput> navigatedGetter(final Getter<? super GInput, ? extends GValue> navigator,
		final Getter<? super GValue, ? extends GOutput> getter) throws NullPointerException {
		Objects.assertNotNull(navigator);
		Objects.assertNotNull(getter);
		return new Getter<GInput, GOutput>() {

			@Override
			public GOutput get(final GInput input) {
				return getter.get(navigator.get(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("navigatedGetter", navigator, getter);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.aggregatedGetter(getter, null, null)}.
	 *
	 * @see #neutralGetter()
	 * @see #aggregatedGetter(Getter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final Getter<? super GItem, GValue> getter)
		throws NullPointerException {
		return Getters.aggregatedGetter(getter, null, null);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.aggregatedGetter(getter, Getters.neutralGetter(), emptyValue, mixedValue)}.
	 *
	 * @see #neutralGetter()
	 * @see #aggregatedGetter(Getter, Getter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final Getter<? super GItem, GValue> getter, final GValue emptyValue,
		final GValue mixedValue) throws NullPointerException {
		return Getters.aggregatedGetter(getter, Getters.<GValue>neutralGetter(), emptyValue, mixedValue);
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.aggregatedGetter(getter, format, null, null)}.
	 *
	 * @see #aggregatedGetter(Getter, Getter, Object, Object) */
	@SuppressWarnings ("javadoc")
	public static <GItem, GValue, GValue2> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final Getter<? super GItem, GValue2> getter,
		final Getter<? super GValue2, ? extends GValue> format) throws NullPointerException {
		return Getters.aggregatedGetter(getter, format, null, null);
	}

	/** Diese Methode gibt einen aggregierten {@link Getter} zurück, welcher den formatierten Wert einer {@link Getter Eigenschaft} der Elemente seiner
	 * iterierbaren Eingabe oder einen der gegebenen Standardwerte liefert.<br>
	 * Wenn die iterierbare Eingabe des erzeugten {@link Getter} {@code null} oder leer ist, liefert dieser den Leerwert {@code emptyValue}. Wenn die über die
	 * gegebene {@link Getter Eigenschaft} {@code getter} ermittelten Werte nicht unter allen Elementen der iterierbaren Eingabe {@link Objects#equals(Object)
	 * äquivalent} sind, liefert der zeugte {@link Getter} den Mischwert {@code mixedValue}. Andernfalls liefert er diesen äquivalenten, gemäß dem gegebenen
	 * {@link Getter Leseformat} {@code format} umgewandelten, Wert.
	 *
	 * @param <GItem> Typ der Elemente in der iterierbaren Eingabe.
	 * @param <GValue> Typ des Werts des gelieferten {@link Getter}.
	 * @param <GValue2> Typ des Werts der Eigenschaft der Elemente in der iterierbaren Eingabe.
	 * @param getter Eigenschaft der Elemente in der iterierbaren Eingabe.
	 * @param format Leseformat zur Umwandlung des Werts der Eigenschaft der Elemente in den Wert des gelieferten {@link Getter}.
	 * @param emptyValue Leerwert.
	 * @param mixedValue Mischwert.
	 * @return {@code aggregated}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code property} bzw. {@code getFormat} {@code null} ist. */
	public static <GItem, GValue, GValue2> Getter<Iterable<? extends GItem>, GValue> aggregatedGetter(final Getter<? super GItem, GValue2> getter,
		final Getter<? super GValue2, ? extends GValue> format, final GValue emptyValue, final GValue mixedValue) throws NullPointerException {
		Objects.assertNotNull(getter);
		Objects.assertNotNull(format);
		return new Getter<Iterable<? extends GItem>, GValue>() {

			@Override
			public GValue get(final Iterable<? extends GItem> input) {
				if (input == null) return emptyValue;
				final Iterator<? extends GItem> iterator = input.iterator();
				if (!iterator.hasNext()) return emptyValue;
				final GItem item = iterator.next();
				final GValue2 value = getter.get(item);
				while (iterator.hasNext()) {
					final GItem item2 = iterator.next();
					final GValue2 value2 = getter.get(item2);
					if (!Objects.equals(value, value2)) return mixedValue;
				}
				return format.get(value);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("aggregatedGetter", getter, format, emptyValue, mixedValue);
			}

		};
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der über die Weiterleitug der Eingabe an einen der gegebenen {@link Getter Eigenschaften} mit Hilfe des
	 * gegebenen {@link Filter} entscheiden.<br>
	 * Wenn der {@link Filter} eine Eingabe akzeptiert, liefert der erzeugte {@link Getter} den Wert der {@link Getter Eigenschaft} {@code acceptGetter}.
	 * Andernfalls liefert er den Wert der {@link Getter Eigenschaft} {@code rejectGetter}. Der erzeugte {@link Getter} liefert für eine Eingabe {@code input}
	 * damit {@code (condition.accept(input) ? acceptGetter : rejectGetter).get(input)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ des Werts.
	 * @param condition Bedingung.
	 * @param acceptGetter Eigenschaft zum Lesen des Werts akzeptierter Eingaben.
	 * @param rejectGetter Eigenschaft zum lesen des Werts abgelehntenr Eingaben.
	 * @return {@code conditional}-{@link Getter}.
	 * @throws NullPointerException Wenn {@code condition}, {@code acceptGetter} bzw. {@code rejectGetter} {@code null} ist. */
	public static <GInput, GOutput> Getter<GInput, GOutput> conditionalGetter(final Filter<? super GInput> condition,
		final Getter<? super GInput, ? extends GOutput> acceptGetter, final Getter<? super GInput, ? extends GOutput> rejectGetter) throws NullPointerException {
		return new Getter<GInput, GOutput>() {

			@Override
			public GOutput get(final GInput input) {
				if (condition.accept(input)) return acceptGetter.get(input);
				return rejectGetter.get(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("conditionalGetter", condition, acceptGetter, rejectGetter);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Getters.synchronizedGetter(getter, getter)}.
	 *
	 * @see #synchronizedGetter(Getter, Object) */
	@SuppressWarnings ("javadoc")
	public static <GInput, GValue> Getter<GInput, GValue> synchronizedGetter(final Getter<? super GInput, ? extends GValue> getter) throws NullPointerException {
		return Getters.synchronizedGetter(getter, getter);
	}

	/** Diese Methode gibt einen {@link Getter} zurück, der den gegebenen {@link Getter} via {@code synchronized(mutex)} synchronisiert.
	 *
	 * @param <GInput> Typ des Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param getter {@link Getter}.
	 * @param mutex Synchronisationsobjekt.
	 * @return {@code synchronized}-{@link Getter}.
	 * @throws NullPointerException Wenn der {@code getter} bzw. {@code mutex} {@code null} ist. */
	public static <GInput, GValue> Getter<GInput, GValue> synchronizedGetter(final Getter<? super GInput, ? extends GValue> getter, final Object mutex)
		throws NullPointerException {
		Objects.assertNotNull(getter);
		Objects.assertNotNull(mutex);
		return new Getter<GInput, GValue>() {

			@Override
			public GValue get(final GInput input) {
				synchronized (mutex) {
					return getter.get(input);
				}
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("synchronizedGetter", getter);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} als Adapter zu einer {@code boolean}-{@link Getter Eigenschaft} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} entspricht {@code Boolean.TRUE.equals(getter.get(input))}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param getter Eigenschaft mit {@code boolean}-Wert.
	 * @return {@link Filter} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GInput> Filter<GInput> toFilter(final Getter<? super GInput, Boolean> getter) throws NullPointerException {
		Objects.assertNotNull(getter);
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				final Boolean result = getter.get(input);
				return (result != null) && result.booleanValue();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("toFilter", getter);
			}

		};
	}

	/** Diese Methode gibt ein {@link Comparable} als Adapter zu einer {@link Number}-{@link Getter Eigenschaft} zurück.<br>
	 * Der Vergleichswert einer Eingabe {@code input} entspricht {@code getter.get(input).intValue()}, wenn diese nicht {@code null} ist. Andernfalls ist der
	 * Vergleichswert {@code 0}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param getter Eigenschaft mit {@link Number}-Wert.
	 * @return {@link Comparable} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GInput> Comparable<GInput> toComparable(final Getter<? super GInput, ? extends Number> getter) throws NullPointerException {
		Objects.assertNotNull(getter);
		return new Comparable<GInput>() {

			@Override
			public int compareTo(final GInput input) {
				final Number result = getter.get(input);
				return result != null ? result.intValue() : 0;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("toFilter", getter);
			}

		};
	}

}
