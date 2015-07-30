package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Converter}n.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Converter} zur realisierung eines statischen Caches für Instanzen der exemplarischen Klasse
 * {@code Helper} verwendet, wobei maximal eine Instanz pro {@link Thread Thread} erzeugt wird:
 * 
 * <pre>
 * public final class Helper {
 * 
 *   static final {@literal Converter<Thread, Helper> CACHE = Converters.synchronizedConverter(Converters.cachedConverter(new Converter<Thread, Helper>()} {
 *   
 *     public Helper convert(Thread value) {
 *       return new Helper(value);
 *     }
 *     
 *   }));
 *   
 *   public static Helper get() {
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
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Converters {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Converter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class AbstractConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Converter} mit Name.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class AbstractNamedConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den Namen.
		 */
		final String name;

		/**
		 * Dieser Konstruktor initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
		 */
		public AbstractNamedConverter(final String name) throws NullPointerException {
			if (name == null) throw new NullPointerException();
			this.name = name;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof AbstractNamedConverter<?, ?>)) return false;
			final AbstractNamedConverter<?, ?> data = (AbstractNamedConverter<?, ?>)object;
			return Objects.equals(this.name, data.name);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.name);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten, delegierenden {@link Converter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param <GInput2> Typ des Eingabe des gegebenen {@link Converter}s.
	 * @param <GOutput2> Typ der Ausgabe des gegebenen {@link Converter}s.
	 */
	static abstract class AbstractDelegatingConverter<GInput, GOutput, GInput2, GOutput2> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput2, ? extends GOutput2> converter;

		/**
		 * Dieser Konstruktor initialisiert den {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public AbstractDelegatingConverter(final Converter<? super GInput2, ? extends GOutput2> converter) throws NullPointerException {
			if (converter == null) throw new NullPointerException();
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof AbstractDelegatingConverter<?, ?, ?, ?>)) return false;
			final AbstractDelegatingConverter<?, ?, ?, ?> data = (AbstractDelegatingConverter<?, ?, ?, ?>)object;
			return Objects.equals(this.converter, data.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, dessen Ausgabe durch das Aufrufen einer gegebenen {@link Method} an der Eingabe ermittelt wird.
	 * 
	 * @see Method#invoke(Object, Object...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class FixedMethodConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Method}.
		 */
		final Method method;

		/**
		 * Dieser Konstruktor initialisiert die {@link Method}.
		 * 
		 * @param method {@link Method}.
		 * @throws NullPointerException Wenn die gegebene {@link Method} {@code null} ist.
		 */
		public FixedMethodConverter(final Method method) throws NullPointerException {
			if (method == null) throw new NullPointerException();
			this.method = method;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput convert(final GInput input) {
			try {
				return (GOutput)this.method.invoke(input);
			} catch (final InvocationTargetException e) {
				throw new IllegalArgumentException(e);
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.method);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof FixedMethodConverter<?, ?>)) return false;
			final FixedMethodConverter<?, ?> data = (FixedMethodConverter<?, ?>)object;
			return Objects.equals(this.method, data.method);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.method);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, dessen Ausgabe durch das Aufrufen einer durch einen Namen gegebenen {@link Method} an der Eingabe
	 * ermittelt wird.
	 * 
	 * @see Method#invoke(Object, Object...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class NamedMethodConverter<GInput, GOutput> extends AbstractNamedConverter<GInput, GOutput> {

		/**
		 * Dieser Konstruktor initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
		 */
		public NamedMethodConverter(final String name) throws NullPointerException {
			super(name);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput convert(final GInput input) {
			if (input == null) throw new NullPointerException();
			try {
				return (GOutput)input.getClass().getMethod(this.name).invoke(input);
			} catch (final InvocationTargetException e) {
				throw new IllegalArgumentException(e);
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			} catch (final NoSuchMethodException e) {
				throw new IllegalArgumentException(e);
			} catch (final SecurityException e) {
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof NamedMethodConverter<?, ?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen gepufferten {@link Converter}. Ein gepufferter {@link Converter} verwaltet die vom einem gegebenen {@link Converter}
	 * erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link Pointer} auf Eingaben und die Werte als
	 * {@link Pointer} auf die Ausgaben des gegebenen {@link Converter}s realisiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 */
	public static final class CachedConverter<GInput, GOutput> extends AbstractDelegatingConverter<GInput, GOutput, GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Map} von Schlüsseln ({@link Pointer} auf Eingaben) auf Werte ({@link Pointer} auf die Ausgaben).
		 */
		Map<Pointer<GInput>, Pointer<GOutput>> map;

		/**
		 * Dieses Feld speichert das Maximum für die Anzahl der Einträge in der {@link Map}.
		 */
		final int limit;

		/**
		 * Dieses Feld speichert den Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der Abbildung erzeugt werden.
		 * 
		 * @see Pointers#pointer(int, Object)
		 */
		final int inputMode;

		/**
		 * Dieses Feld speichert den Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der Abbildung erzeugt werden.
		 * 
		 * @see Pointers#pointer(int, Object)
		 */
		final int outputMode;

		/**
		 * Dieses Feld speichert die maximale Anzahl an Einträgen in der {@link Map}. Wenn die aktuelle Anzahl 25% der maximalen Anzahl unterschreitet, wird die
		 * Größe der {@link Map} angepasst.
		 */
		int capacity = 0;

		/**
		 * Dieser Konstruktor initialisiert den gepuferten {@link Converter}.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
		 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der Abbildung erzeugt werden.
		 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der Abbildung erzeugt werden.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 * @throws IllegalArgumentException Wenn einer der gegebenen Modi ungültig ist.
		 */
		public CachedConverter(final int limit, final int inputMode, final int outputMode, final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException, IllegalArgumentException {
			super(converter);
			Pointers.pointer(inputMode, null);
			Pointers.pointer(outputMode, null);
			this.map = new LinkedHashMap<Pointer<GInput>, Pointer<GOutput>>(0, 0.75f, true);
			this.limit = limit;
			this.inputMode = inputMode;
			this.outputMode = outputMode;
		}

		/**
		 * Diese Methode leert den Cache.
		 */
		public void clear() {
			if (this.capacity == 0) return;
			this.map = new LinkedHashMap<Pointer<GInput>, Pointer<GOutput>>(0, 0.75f, true);
			this.capacity = 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			final Map<Pointer<GInput>, Pointer<GOutput>> map = this.map;
			final Pointer<GOutput> pointer = map.get(Pointers.hardPointer(input));
			if (pointer != null) {
				final GOutput output = pointer.data();
				if (output != null) return output;
				if (Pointers.isValid(pointer)) return null;
				int valid = this.limit - 1;
				for (final Iterator<Entry<Pointer<GInput>, Pointer<GOutput>>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
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
			final GOutput output = this.converter.convert(input);
			map.put(Pointers.pointer(this.inputMode, input), Pointers.pointer(this.outputMode, output));
			final int size = map.size(), capacity = this.capacity;
			if (size >= capacity) {
				this.capacity = size;
			} else if ((size << 2) <= capacity) {
				(this.map = new LinkedHashMap<Pointer<GInput>, Pointer<GOutput>>(0, 0.75f, true)).putAll(map);
				this.capacity = size;
			}
			return output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof CachedConverter<?, ?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.limit, this.inputMode, this.outputMode, this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Converter}, der seine Eingabe an einen ersten {@link Converter} weiterleitet, dessen Ausgabe an einen
	 * zweiten {@link Converter} übergibt und dessen Ausgabe liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter}s.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter}s sowie der Eingabe des zweiten {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter}s.
	 */
	public static final class ChainedConverter<GInput, GValue, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den primären {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GValue> converter1;

		/**
		 * Dieses Feld speichert den sekundären {@link Converter}.
		 */
		final Converter<? super GValue, ? extends GOutput> converter2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Converter}.
		 * 
		 * @param converter1 primärer {@link Converter}.
		 * @param converter2 sekundärer {@link Converter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ChainedConverter(final Converter<? super GInput, ? extends GValue> converter1, final Converter<? super GValue, ? extends GOutput> converter2)
			throws NullPointerException {
			if ((converter1 == null) || (converter2 == null)) throw new NullPointerException();
			this.converter1 = converter1;
			this.converter2 = converter2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			return this.converter2.convert(this.converter1.convert(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.converter1.hashCode() + (31 * this.converter2.hashCode());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ChainedConverter<?, ?, ?>)) return false;
			final ChainedConverter<?, ?, ?> data = (ChainedConverter<?, ?, ?>)object;
			return Objects.equals(this.converter1, data.converter1) && Objects.equals(this.converter2, data.converter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.converter1, this.converter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen bedingten {@link Converter}, der über die Weiterleitug der Eingabe mit Hilfe eines {@link Filter}s entscheiden. Wenn der
	 * gegebene {@link Filter} eine Eingabe akzeptiert, liefert der {@link ConditionalConverter} dafür die Ausgabe des gegebenen {@code Accept}- {@link Converter}
	 * s. Andernfalls wird die Ausgabe des gegebenen {@code Reject}-{@link Converter}s geliefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class ConditionalConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GInput> condition;

		/**
		 * Dieses Feld speichert den {@code Accept}-{@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> accept;

		/**
		 * Dieses Feld speichert den {@code Reject}-{@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> reject;

		/**
		 * Dieser Konstruktor initialisiert {@link Filter} und {@link Converter}.
		 * 
		 * @param condition {@link Filter}.
		 * @param accept {@code Accept}-{@link Converter}.
		 * @param reject {@code Reject}-{@link Converter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConditionalConverter(final Filter<? super GInput> condition, final Converter<? super GInput, ? extends GOutput> accept,
			final Converter<? super GInput, ? extends GOutput> reject) throws NullPointerException {
			if ((condition == null) || (accept == null) || (reject == null)) throw new NullPointerException();
			this.condition = condition;
			this.accept = accept;
			this.reject = reject;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			if (this.condition.accept(input)) return this.accept.convert(input);
			return this.reject.convert(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.condition, this.accept, this.reject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ConditionalConverter<?, ?>)) return false;
			final ConditionalConverter<?, ?> data = (ConditionalConverter<?, ?>)object;
			return Objects.equals(this.condition, data.condition) && Objects.equals(this.accept, data.accept) && Objects.equals(this.reject, data.reject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.condition, this.accept, this.reject);
		}

	}

	{}

	/**
	 * Dieses Feld speichert den neutralen {@link Converter}, dessen Ausgabe gleich seiner Eingabe ist.
	 */
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

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Aufrufen der durch ihren Namen gegebenen {@link Method} an der Eingabe ermittelt
	 * wird, und gibt ihn zurück.
	 * 
	 * @see NamedMethodConverter
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param methodName Name.
	 * @return {@link NamedMethodConverter}.
	 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> javaMethod(final String methodName) throws NullPointerException {
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
				return Objects.toStringCall("javaMethod", methodName);
			}

		};
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Aufrufen der durch einen Namen und eine {@link Class} gegebenen {@link Method} an
	 * der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see #methodConverter(Method)
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @param clazz {@link Class}.
	 * @return {@link FixedMethodConverter}.
	 * @throws NullPointerException Wenn der gegebene Name bzw. die gegebene {@link Class} {@code null} ist.
	 * @throws NoSuchMethodException Wenn an der gegebenen {@link Class} keine {@link Method} mit dem gegebenen Namen existiert.
	 * @throws SecurityException Wenn auf die {@link Method} nicht zugegriffen werden darf.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> javaMethod(final Class<GInput> inputType, final String methodName) throws NullPointerException,
		NoSuchMethodException, SecurityException {
		if (inputType == null) throw new NullPointerException("inputType = null");
		if (methodName == null) throw new NullPointerException("methodName = null");
		return Converters.javaMethod(inputType.getMethod(methodName));
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Aufrufen der gegebenen {@link Method Methode} an der Eingabe ermittelt wird, und
	 * gibt ihn zurück.
	 * 
	 * @see FixedMethodConverter
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method {@link Method}.
	 * @return method {@link FixedMethodConverter}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> javaMethod(final java.lang.reflect.Method method) throws NullPointerException {
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
				return Objects.toStringCall("javaMethod", method);
			}

		};
	}

	/**
	 * Diese Methode gibt den neutralen {@link Converter} zurück, dessen Ausgabe gleich seiner Eingabe ist.
	 * 
	 * @param <GInput> Typ der Ein-/Ausgabe.
	 * @return {@link #NEUTRAL_CONVERTER}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Converter<Object, GInput> neutralConverter() {
		return (Converter<Object, GInput>)Converters.NEUTRAL_CONVERTER;
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, welcher dessen Ausgabe dem Wert einer über das gegebene {@link Field} beschriebenen Eigenschaft der
	 * Eingabe entspricht. Für eine Eingabe {@code input} liefert der {@link Converter} damit {@code field.get(input)}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ des Werts.
	 * @param field {@link Field}.
	 * @return {@code field}-{@link Converter}.
	 * @throws NullPointerException Wenn {@code field} {@code null} ist.
	 */
	public static <GInput, GValue> Converter<GInput, GValue> fieldConverter(final Field<? super GInput, ? extends GValue> field) throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		return new Converter<GInput, GValue>() {

			@Override
			public GValue convert(final GInput input) {
				return field.get(input);
			}

			@Override
			public String toString() {
				return Objects.toStringCall("fieldConverter", field);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, welcher stats die gegebene Ausgabe liefert.
	 * 
	 * @param <GValue> Typ der Ausgabe.
	 * @param value Ausgabe.
	 * @return {@code value}-{@link Converter}.
	 */
	public static <GValue> Converter<Object, GValue> valueConverter(final GValue value) {
		return new Converter<Object, GValue>() {

			@Override
			public GValue convert(final Object input) {
				return value;
			}

			@Override
			public String toString() {
				return Objects.toStringCall("valueConverter", value);
			}

		};
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Converter} und gibt ihn zurück. Der erzeugte {@link Converter} verwaltet die vom gegebenen {@link Converter}
	 * erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link SoftPointer} auf Eingaben und die Werte als
	 * {@link SoftPointer} auf die Ausgaben des gegebenen {@link Converter}s realisiert. Die Anzahl der Einträge in der {@link Map Abbildung} sind nicht
	 * beschränkt. Der erzeute {@link Converter} realisiert damit einen speichersensitiven, assoziativen Cache.
	 * 
	 * @see #cachedConverter(int, int, int, Converter)
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 * @param converter {@link Converter}.
	 * @return {@link CachedConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> CachedConverter<GInput, GOutput> cachedConverter(final Converter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		return Converters.cachedConverter(-1, Pointers.SOFT, Pointers.SOFT, converter);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Converter} und gibt ihn zurück. Der erzeugte {@link Converter} verwaltet die vom gegebenen {@link Converter}
	 * erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link Pointer} auf Eingaben und die Werte als
	 * {@link Pointer} auf die Ausgaben des gegebenen {@link Converter}s realisiert.
	 * 
	 * @see CachedConverter
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map} erzeugt werden.
	 * @param converter {@link Converter}.
	 * @return {@link CachedConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn einer der gegebenen Modi ungültig ist.
	 */
	public static <GInput, GOutput> CachedConverter<GInput, GOutput> cachedConverter(final int limit, final int inputMode, final int outputMode,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException, IllegalArgumentException {
		return new CachedConverter<GInput, GOutput>(limit, inputMode, outputMode, converter);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter}, der seine Eingabe an einen ersten {@link Converter} weiterleitet, dessen Ausgabe an einen
	 * zweiten {@link Converter} übergibt und dessen Ausgabe liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedConverter
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter}s.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter}s sowie der Eingabe des zweiten {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter}s.
	 * @param converter1 erster {@link Converter}.
	 * @param converter2 zweiter {@link Converter}.
	 * @return {@link ChainedConverter}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GValue, GOutput> ChainedConverter<GInput, GValue, GOutput> chainedConverter(
		final Converter<? super GInput, ? extends GValue> converter1, final Converter<? super GValue, ? extends GOutput> converter2) throws NullPointerException {
		return new ChainedConverter<GInput, GValue, GOutput>(converter1, converter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der über die Weiterleitug der Eingabe mit Hilfe eines {@link Filter}s entscheiden, und gibt ihn zurück. Wenn
	 * der gegebene {@link Filter} eine Eingabe akzeptiert, liefert der erzeugte {@link Converter} dafür die Ausgabe des gegebenen {@code Accept}-
	 * {@link Converter}s. Die Ausgabe des gegebenen {@code Reject}-{@link Converter}s liefert er dagegen für eine vom gegebenen {@link Filter} abgelehnten
	 * Eingabe.
	 * 
	 * @see Filter
	 * @see ConditionalConverter
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param filter {@link Filter}.
	 * @param accept {@code Accept}-{@link Converter}.
	 * @param reject {@code Reject}-{@link Converter}.
	 * @return {@link ConditionalConverter}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> ConditionalConverter<GInput, GOutput> conditionalConverter(final Filter<? super GInput> filter,
		final Converter<? super GInput, ? extends GOutput> accept, final Converter<? super GInput, ? extends GOutput> reject) throws NullPointerException {
		return new ConditionalConverter<GInput, GOutput>(filter, accept, reject);
	}

	/**
	 * Diese Methode gibt einen einen {@link Converter} zurück, der den gegebenen {@link Converter} via {@code synchronized(this)} synchronisiert.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@code synchronized}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> synchronizedConverter(final Converter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Converter<GInput, GOutput>() {

			@Override
			public GOutput convert(final GInput input) {
				synchronized (this) {
					return converter.convert(input);
				}
			}

		};
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Converters() {
	}

}
