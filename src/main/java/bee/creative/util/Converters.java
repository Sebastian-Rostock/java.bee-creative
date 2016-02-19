package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.util.Pointers.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Converter}.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Converter} zur realisierung eines statischen Caches für Instanzen der exemplarischen Klasse
 * {@code Helper} verwendet, wobei maximal eine Instanz pro {@link Thread} erzeugt wird:
 * 
 * <pre>
 * public final class Helper {
 * 
 *   static final {@literal Converter<Thread, Helper> CACHE = Converters.synchronizedConverter(Converters.bufferedConverter(new Converter<Thread, Helper>()} {
 *   
 *     public Helper convert(Thread value) {
 *       return new Helper(value);
 *     }
 *     
 *   }));
 *   
 *   public static final Helper get() {
 *     return Helper.CACHE.convert(Thread.currentThread());
 *   }
 *   
 *   protected Helper(Thread value) {
 *     ...
 *   }
 *   
 *   ...
 *   
 * }
 * </pre>
 * 
 * @see Converter
 * @see Conversion
 * @see Conversions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Converters {

	/** Dieses Feld speichert den neutralen {@link Converter}, dessen Ausgabe gleich seiner Eingabe ist. */
	public static final Converter<?, ?> NEUTRAL_CONVERTER = new Converter<Object, Object>() {

		@Override
		public Object convert(final Object input) {
			return input;
		}

		@Override
		public String toString() {
			return "NEUTRAL_CONVERTER";
		}

	};

	{}

	/** Diese Methode gibt einen {@link Converter} als Adapter zu einem {@link Field} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code field.get(input)}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field}.
	 * @return {@link Field}-Adapter.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist. */
	public static final <GInput, GValue> Converter<GInput, GValue> fieldAdapter(final Field<? super GInput, ? extends GValue> field) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new Converter<GInput, GValue>() {

			@Override
			public GValue convert(final GInput input) {
				return field.get(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("fieldAdapter", field);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} als Adapter zu einem {@link Filter} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code Boolean.valueOf(filter.accept(input))}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@link Filter}-Adapter.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static final <GInput> Converter<GInput, Boolean> filterAdapter(final Filter<? super GInput> filter) throws NullPointerException {
		if (filter == null) throw new NullPointerException("filter = null");
		return new Converter<GInput, Boolean>() {

			@Override
			public Boolean convert(final GInput input) {
				return Boolean.valueOf(filter.accept(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("filterAdapter", filter);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der die über die native {@link java.lang.reflect.Method Methode} gegebene Umwandlung durchführt.
	 * 
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method Native statischen Methode, die mit der Eingabe als Parameter aufgerufen wird, d.h. {@code method(input)}.
	 * @return {@code native}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> nativeStaticMethod(final java.lang.reflect.Method method) throws NullPointerException {
		if (method == null) throw new NullPointerException("method = null");
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
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
				return Objects.toInvokeString("nativeStaticMethod", method);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der die über eine native {@link java.lang.reflect.Method Methode} gegebene Umwandlung durchführt.
	 * 
	 * @see #nativeStaticMethod(java.lang.reflect.Method)
	 * @see Class#getMethod(String, Class...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param staticType {@link Class}, an der die {@code static}-Methode definiert ist.
	 * @param inputType {@link Class} der Eingabe.
	 * @param methodName Name der nativen statischen Methode, die parameterlos an der Eingabe aufgerufen wird, d.h. {@code staticType.method(input)}.
	 * @return {@code native}-{@link Converter}.
	 * @throws SecurityException Wenn {@link Class#getMethod(String, Class...)} eine entsprechende Ausnahme auslöst.
	 * @throws NoSuchMethodException Wenn {@link Class#getMethod(String, Class...)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code staticType}, {@code inputType} bzw. {@code methodName} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> nativeStaticMethod(final Class<?> staticType, final Class<? super GInput> inputType,
		final String methodName) throws SecurityException, NoSuchMethodException, NullPointerException {
		if (staticType == null) throw new NullPointerException("staticType = null");
		if (inputType == null) throw new NullPointerException("inputType = null");
		if (methodName == null) throw new NullPointerException("methodName = null");
		return Converters.nativeStaticMethod(staticType.getMethod(methodName, inputType));
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der die über eine native {@link java.lang.reflect.Method Methode} gegebene Eigenschaft der Eingabe
	 * ließt. Die native Methode wird bei jedem Zugriff dynamisch über die {@link Class} der Eingabe ermittelt.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param methodName Name der nativen Methode, die parameterlos an der Eingabe aufgerufen wird, d.h. {@code input.method()}.
	 * @return {@code native}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code methodName} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> nativeObjectMethod(final String methodName) throws NullPointerException {
		if (methodName == null) throw new NullPointerException("methodName = null");
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
				if (input == null) throw new NullPointerException("input = null");
				try {
					final java.lang.reflect.Method method = input.getClass().getMethod(methodName);
					try {
						@SuppressWarnings ("unchecked")
						final GOutput result = (GOutput)method.invoke(input);
						return result;
					} catch (final IllegalAccessException | InvocationTargetException e) {
						throw new IllegalArgumentException(e);
					}
				} catch (final SecurityException | NoSuchMethodException e) {
					throw new IllegalArgumentException(e);
				}
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("nativeObjectMethod", methodName);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der die über die native {@link java.lang.reflect.Method Methode} gegebene Eigenschaft der Eingabe ließt.
	 * 
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method Nativen Methode, die parameterlos an der Eingabe aufgerufen wird, d.h. {@code input.method()}.
	 * @return {@code native}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> nativeObjectMethod(final java.lang.reflect.Method method) throws NullPointerException {
		if (method == null) throw new NullPointerException("method = null");
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
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
				return Objects.toInvokeString("nativeObjectMethod", method);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der die über eine native {@link java.lang.reflect.Method Methode} gegebene Eigenschaft der Eingabe
	 * ließt.
	 * 
	 * @see #nativeObjectMethod(java.lang.reflect.Method)
	 * @see Class#getMethod(String, Class...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param inputType {@link Class} der Eingabe.
	 * @param methodName Name der nativen Methode, die parameterlos an der Eingabe aufgerufen wird, d.h. {@code input.method()}.
	 * @return {@code native}-{@link Converter}.
	 * @throws SecurityException Wenn {@link Class#getMethod(String, Class...)} eine entsprechende Ausnahme auslöst.
	 * @throws NoSuchMethodException Wenn {@link Class#getMethod(String, Class...)} eine entsprechende Ausnahme auslöst.
	 * @throws NullPointerException Wenn {@code inputType} bzw. {@code methodName} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> nativeObjectMethod(final Class<? super GInput> inputType, final String methodName)
		throws SecurityException, NoSuchMethodException, NullPointerException {
		return Converters.nativeObjectMethod(inputType.getMethod(methodName));
	}

	/** Diese Methode gibt einen {@link Converter} zurück, welcher stats die gegebene Ausgabe liefert.
	 * 
	 * @param <GValue> Typ der Ausgabe.
	 * @param value Ausgabe.
	 * @return {@code value}-{@link Converter}. */
	public static final <GValue> Converter<Object, GValue> valueConverter(final GValue value) {
		return new Converter<Object, GValue>() {

			@Override
			public GValue convert(final Object input) {
				return value;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("valueConverter", value);
			}

		};
	}

	/** Diese Methode gibt den neutralen {@link Converter} zurück, dessen Ausgabe gleich seiner Eingabe ist.
	 * 
	 * @param <GInput> Typ der Ein-/Ausgabe.
	 * @return {@link #NEUTRAL_CONVERTER}. */
	@SuppressWarnings ("unchecked")
	public static final <GInput> Converter<GInput, GInput> neutralConverter() {
		return (Converter<GInput, GInput>)Converters.NEUTRAL_CONVERTER;
	}

	/** Diese Methode gibt einen verketteten {@link Converter} zurück, der seine Eingabe durch den ersten und zweiten {@link Converter} umgewandelt wird.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code converter2.convert(converter1.convert(input))}.
	 * 
	 * @param <GInput> Typ der Eingabe des erzeugten sowie der Eingabe des ersten {@link Converter}.
	 * @param <GValue> Typ der Ausgabe des ersten sowie der Eingabe des zweiten {@link Converter}.
	 * @param <GOutput> Typ der Ausgabe des zweiten sowie der Ausgabe des erzeugten {@link Converter}.
	 * @param converter1 erster {@link Converter}.
	 * @param converter2 zweiter {@link Converter}.
	 * @return {@code chained}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code converter1} bzw. {@code converter2} {@code null} ist. */
	public static final <GInput, GValue, GOutput> Converter<GInput, GOutput> chainedConverter(final Converter<? super GInput, ? extends GValue> converter1,
		final Converter<? super GValue, ? extends GOutput> converter2) throws NullPointerException {
		if (converter1 == null) throw new NullPointerException("converter1 = null");
		if (converter2 == null) throw new NullPointerException("converter2 = null");
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
				return converter2.convert(converter1.convert(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("chainedConverter", converter1, converter2);
			}

		};
	}

	/** Diese Methode gibt einen gepufferten {@link Converter} zurück, der die zu seinen Eingaben über den gegebenen {@link Converter} ermittelten Ausgaben intern
	 * in einer {@link Map} (genauer {@link LinkedHashMap}) zur Wiederverwendung vorhält. Die Schlüssel der {@link Map} werden dabei als {@link SoftPointer} auf
	 * Eingaben und die Werte als {@link SoftPointer} auf die Ausgaben bestückt.
	 * 
	 * @see #bufferedConverter(int, int, int, Converter)
	 * @param <GInput> Typ der Eingabe sowie der Datensätze in den Schlüsseln der internen {@link Map}.
	 * @param <GOutput> Typ der Ausgabe sowie der Datensätze in den Werten der internen {@link Map}.
	 * @param converter {@link Converter}.
	 * @return {@code buffered}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> bufferedConverter(final Converter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		return Converters.bufferedConverter(-1, Pointers.SOFT, Pointers.SOFT, converter);
	}

	/** Diese Methode gibt einen gepufferten {@link Converter} zurück, der die zu seinen Eingaben über den gegebenen {@link Converter} ermittelten Ausgaben intern
	 * in einer {@link Map} (genauer {@link LinkedHashMap}) zur Wiederverwendung vorhält. Die Schlüssel der {@link Map} werden dabei als {@link Pointer} auf
	 * Eingaben und die Werte als {@link Pointer} auf die Ausgaben bestückt.
	 * 
	 * @see Pointers#pointer(int, Object)
	 * @param <GInput> Typ der Eingabe sowie der Datensätze in den Schlüsseln der internen {@link Map}.
	 * @param <GOutput> Typ der Ausgabe sowie der Datensätze in den Werten der internen {@link Map}.
	 * @param limit Maximum für die Anzahl der Einträge in der internen {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden ({@link Pointers#HARD},
	 *        {@link Pointers#SOFT}, {@link Pointers#WEAK}).
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map} erzeugt werden ({@link Pointers#HARD},
	 *        {@link Pointers#SOFT}, {@link Pointers#WEAK}).
	 * @param converter {@link Converter}.
	 * @return {@code buffered}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Pointers#pointer(int, Object)} eine entsprechende Ausnahme auslöst. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> bufferedConverter(final int limit, final int inputMode, final int outputMode,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException, IllegalArgumentException {
		if (converter == null) throw new NullPointerException("converter = null");
		Pointers.pointer(inputMode, null);
		Pointers.pointer(outputMode, null);
		return new Converter<GInput, GOutput>() {

			Map<Pointer<GInput>, Pointer<GOutput>> map = new LinkedHashMap<>(0, 0.75f, true);

			int capacity = 0;

			@Override
			public GOutput convert(final GInput input) {
				final Pointer<GOutput> pointer = this.map.get(Pointers.hardPointer(input));
				if (pointer != null) {
					final GOutput output = pointer.data();
					if (output != null) return output;
					if (Pointers.isValid(pointer)) return null;
					int valid = limit - 1;
					for (final Iterator<Entry<Pointer<GInput>, Pointer<GOutput>>> iterator = this.map.entrySet().iterator(); iterator.hasNext();) {
						final Entry<Pointer<GInput>, Pointer<GOutput>> entry = iterator.next();
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
				final GOutput output = converter.convert(input);
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

			/** {@inheritDoc} */
			@Override
			public String toString() {
				return Objects.toInvokeString("bufferedConverter", limit, inputMode, outputMode, converter);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zurüch, der über die Weiterleitug der Eingabe an einen der gegebenen {@link Converter} mit Hilfe des gegebenen
	 * eines {@link Filter}s entscheiden. Wenn der {@link Filter} eine Eingabe akzeptiert, liefert der erzeugte {@link Converter} dafür die Ausgabe des
	 * {@code acceptConverter}. Die Ausgabe des gegebenen {@code rejectConverter} liefert er dagegen für die vom {@link Filter} abgelehnten Eingaben.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code (condition.accept(input) ? acceptConverter : rejectConverter).convert(input)}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param condition {@link Filter} als Bedingung.
	 * @param acceptConverter {@link Converter} für die vom {@link Filter} akzeptierten Eingaben.
	 * @param rejectConverter {@link Converter} für die vom {@link Filter}abgelehnten Eingaben.
	 * @return {@code conditional}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code condition}, {@code acceptConverter} bzw. {@code rejectConverter} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> conditionalConverter(final Filter<? super GInput> condition,
		final Converter<? super GInput, ? extends GOutput> acceptConverter, final Converter<? super GInput, ? extends GOutput> rejectConverter)
		throws NullPointerException {
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
				if (condition.accept(input)) return acceptConverter.convert(input);
				return rejectConverter.convert(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("conditionalConverter", condition, acceptConverter, rejectConverter);
			}

		};
	}

	/** Diese Methode gibt einen {@link Converter} zurück, der den gegebenen {@link Converter} via {@code synchronized(this)} synchronisiert.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@code synchronized}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist. */
	public static final <GInput, GOutput> Converter<GInput, GOutput> synchronizedConverter(final Converter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
				synchronized (this) {
					return converter.convert(input);
				}
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("synchronizedConverter", converter);
			}

		};
	}

}
