package bee.creative.util;

import java.lang.reflect.Field;
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
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Converter} zur realisierung eines statischen Caches für
 * Instanzen der exemplarischen Klasse {@code Helper} verwendet, wobei maximal eine Instanz pro {@link Thread Thread}
 * erzeugt wird:
 * 
 * <pre>
 * public final class Helper {
 * 
 *   static final  Converter&lt;Thread, Helper&gt; CACHE = Converters.synchronizedConverter(Converters.cachedConverter(new Converter&lt;Thread, Helper&gt;() {
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
public final class Converters {

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
		 * Dieser Konstrukteur initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
		 */
		public AbstractNamedConverter(final String name) throws NullPointerException {
			if(name == null) throw new NullPointerException("name is null");
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
			final AbstractNamedConverter<?, ?> data = (AbstractNamedConverter<?, ?>)object;
			return Objects.equals(this.name, data.name);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, dessen Ausgabe durch das Lesen eines gegebenen {@link Field}s
	 * an der Eingabe ermittelt wird.
	 * 
	 * @see Field#get(Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class FixedFieldConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert das {@link Field}.
		 */
		final Field field;

		/**
		 * Dieser Konstrukteur initialisiert das {@link Field}.
		 * 
		 * @param field {@link Field}.
		 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
		 */
		public FixedFieldConverter(final Field field) throws NullPointerException {
			if(field == null) throw new NullPointerException("field is null");
			this.field = field;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput convert(final GInput input) {
			try{
				return (GOutput)this.field.get(input);
			}catch(final IllegalAccessException e){
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FixedFieldConverter<?, ?>)) return false;
			final FixedFieldConverter<?, ?> data = (FixedFieldConverter<?, ?>)object;
			return Objects.equals(this.field, data.field);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("fixedFieldConverter", this.field);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, dessen Ausgabe durch das Aufrufen einer gegebenen
	 * {@link Method} an der Eingabe ermittelt wird.
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
		 * Dieser Konstrukteur initialisiert die {@link Method}.
		 * 
		 * @param method {@link Method}.
		 * @throws NullPointerException Wenn die gegebene {@link Method} {@code null} ist.
		 */
		public FixedMethodConverter(final Method method) throws NullPointerException {
			if(method == null) throw new NullPointerException("method is null");
			this.method = method;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput convert(final GInput input) {
			try{
				return (GOutput)this.method.invoke(input);
			}catch(final InvocationTargetException e){
				throw new IllegalArgumentException(e);
			}catch(final IllegalAccessException e){
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
			if(object == this) return true;
			if(!(object instanceof FixedMethodConverter<?, ?>)) return false;
			final FixedMethodConverter<?, ?> data = (FixedMethodConverter<?, ?>)object;
			return Objects.equals(this.method, data.method);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("methodConverter", this.method);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, dessen Ausgabe durch das Lesen eines durch einen Namen
	 * gegebenen {@link Field}s an der Eingabe ermittelt wird.
	 * 
	 * @see Field#get(Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class NamedFieldConverter<GInput, GOutput> extends AbstractNamedConverter<GInput, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
		 */
		public NamedFieldConverter(final String name) throws NullPointerException {
			super(name);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public GOutput convert(final GInput input) {
			if(input == null) throw new NullPointerException("input is null");
			try{
				return (GOutput)input.getClass().getField(this.name).get(input);
			}catch(final IllegalAccessException e){
				throw new IllegalArgumentException(e);
			}catch(final NoSuchFieldException e){
				throw new IllegalArgumentException(e);
			}catch(final SecurityException e){
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof NamedFieldConverter<?, ?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("namedFieldConverter", this.name);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, dessen Ausgabe durch das Aufrufen einer durch einen Namen
	 * gegebenen {@link Method} an der Eingabe ermittelt wird.
	 * 
	 * @see Method#invoke(Object, Object...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class NamedMethodConverter<GInput, GOutput> extends AbstractNamedConverter<GInput, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert den Namen.
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
			if(input == null) throw new NullPointerException("input is null");
			try{
				return (GOutput)input.getClass().getMethod(this.name).invoke(input);
			}catch(final InvocationTargetException e){
				throw new IllegalArgumentException(e);
			}catch(final IllegalAccessException e){
				throw new IllegalArgumentException(e);
			}catch(final NoSuchMethodException e){
				throw new IllegalArgumentException(e);
			}catch(final SecurityException e){
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof NamedMethodConverter<?, ?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("namedMethodConverter", this.name);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der für jede Eingabe immer die gleiche {@code default}-Ausgabe
	 * liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class DefaultConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@code default}-Ausgabe.
		 */
		final GOutput output;

		/**
		 * Dieser Konstrukteur initialisiert die {@code default}-Ausgabe.
		 * 
		 * @param output Standardausgabe.
		 */
		public DefaultConverter(final GOutput output) {
			this.output = output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			return this.output;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.output);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DefaultConverter<?, ?>)) return false;
			final DefaultConverter<?, ?> data = (DefaultConverter<?, ?>)object;
			return Objects.equals(this.output, data.output);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("defaultConverter", this.output);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der über die Weiterleitug der Eingabe mit Hilfe eines einen
	 * {@link Filter}s entscheiden. Wenn der gegebene {@link Filter} eine Eingabe akzeptiert, liefert der
	 * {@link Converter} dafür die Ausgabe des gegebenen {@code Accept}-{@link Converter}s. Die Ausgabe des gegebenen
	 * {@code Reject}-{@link Converter}s liefert er dagegen für eine vom gegebenen {@link Filter} abgelehnten Eingabe.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class FilteredConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GInput> filter;

		/**
		 * Dieses Feld speichert den {@code Accept}-{@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> accept;

		/**
		 * Dieses Feld speichert den {@code Reject}-{@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> reject;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Converter}.
		 * 
		 * @param filter {@link Filter}.
		 * @param accept {@code Accept}-{@link Converter}.
		 * @param reject {@code Reject}-{@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} oder einer der gegebenen {@link Converter}
		 *         {@code null} sind.
		 */
		public FilteredConverter(final Filter<? super GInput> filter,
			final Converter<? super GInput, ? extends GOutput> accept,
			final Converter<? super GInput, ? extends GOutput> reject) throws NullPointerException {
			if(filter == null) throw new NullPointerException("filter is null");
			if(accept == null) throw new NullPointerException("Accept is null");
			if(reject == null) throw new NullPointerException("reject is null");
			this.filter = filter;
			this.accept = accept;
			this.reject = reject;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			if(this.filter.accept(input)) return this.accept.convert(input);
			return this.reject.convert(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.filter, this.accept, this.reject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FilteredConverter<?, ?>)) return false;
			final FilteredConverter<?, ?> data = (FilteredConverter<?, ?>)object;
			return Objects.equals(this.filter, data.filter) && Objects.equals(this.accept, data.accept)
				&& Objects.equals(this.reject, data.reject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("filterConverter", this.filter, this.accept, this.reject);
		}

	}

	/**
	 * Diese Klasse implementiert einen gepufferten {@link Converter}. Ein gepufferter {@link Converter} verwaltet die vom
	 * einem gegebenen {@link Converter} erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die Schlüssel
	 * werden dabei über {@link Pointer} auf Eingaben und die Werte als {@link Pointer} auf die Ausgaben des gegebenen
	 * {@link Converter}s realisiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 */
	public static final class CachedConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Map} von Schlüsseln ({@link Pointer} auf Eingaben) auf Werte ({@link Pointer}
		 * auf die Ausgaben).
		 */
		final Map<Pointer<GInput>, Pointer<GOutput>> map;

		/**
		 * Dieses Feld speichert das Maximum für die Anzahl der Einträge in der {@link Map}.
		 */
		final int limit;

		/**
		 * Dieses Feld speichert den Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der
		 * Abbildung erzeugt werden.
		 * 
		 * @see Pointers#pointer(int, Object)
		 */
		final int inputMode;

		/**
		 * Dieses Feld speichert den Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der
		 * Abbildung erzeugt werden.
		 * 
		 * @see Pointers#pointer(int, Object)
		 */
		final int outputMode;

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert den gepuferten {@link Converter}.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
		 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der Abbildung
		 *        erzeugt werden.
		 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der Abbildung
		 *        erzeugt werden.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 * @throws IllegalArgumentException Wenn einer der gegebenen Modi ungültig ist.
		 */
		public CachedConverter(final int limit, final int inputMode, final int outputMode,
			final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException,
			IllegalArgumentException {
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
			Pointers.pointerConverter(inputMode);
			Pointers.pointerConverter(outputMode);
			this.map = new LinkedHashMap<Pointer<GInput>, Pointer<GOutput>>(0, 0.75f, true);
			this.limit = limit;
			this.inputMode = inputMode;
			this.outputMode = outputMode;
		}

		/**
		 * Diese Methode leert den Cache.
		 */
		public void clear() {
			this.map.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			final Pointer<GOutput> pointer = this.map.get(Pointers.hardPointer(input));
			if(pointer != null){
				final GOutput output = pointer.data();
				if(output != null) return output;
				if(Pointers.isValid(pointer)) return null;
				int valid = this.limit - 1;
				for(final Iterator<Entry<Pointer<GInput>, Pointer<GOutput>>> iterator = this.map.entrySet().iterator(); iterator
					.hasNext();){
					final Entry<Pointer<GInput>, Pointer<GOutput>> entry = iterator.next();
					final Pointer<?> key = entry.getKey(), value = entry.getValue();
					if(valid != 0){
						if(!Pointers.isValid(key) || !Pointers.isValid(value)){
							iterator.remove();
						}else{
							valid--;
						}
					}else{
						iterator.remove();
					}
				}
			}
			final GOutput output = this.converter.convert(input);
			this.map.put(Pointers.pointer(this.inputMode, input), Pointers.pointer(this.outputMode, output));
			return output;
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
			if((object == this) || Objects.equals(this.converter, object)) return true;
			if(!(object instanceof CachedConverter<?, ?>)) return false;
			final CachedConverter<?, ?> data = (CachedConverter<?, ?>)object;
			return Objects.equals(this.converter, data.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("cachedConverter", this.limit, this.inputMode, this.outputMode, this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Converter}, der seine Eingabe an einen ersten {@link Converter}
	 * weiterleitet, dessen Ausgabe an einen zweiten {@link Converter} übergibt und dessen Ausgabe liefert.
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
		 * Dieser Konstrukteur initialisiert die {@link Converter}.
		 * 
		 * @param converter1 primärer {@link Converter}.
		 * @param converter2 sekundärer {@link Converter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Converter} {@code null} ist.
		 */
		public ChainedConverter(final Converter<? super GInput, ? extends GValue> converter1,
			final Converter<? super GValue, ? extends GOutput> converter2) throws NullPointerException {
			if(converter1 == null) throw new NullPointerException("converter1 is null");
			if(converter2 == null) throw new NullPointerException("converter2 is null");
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
			if(object == this) return true;
			if(!(object instanceof ChainedConverter<?, ?, ?>)) return false;
			final ChainedConverter<?, ?, ?> data = (ChainedConverter<?, ?, ?>)object;
			return Objects.equals(this.converter1, data.converter1, this.converter2, data.converter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedConverter", this.converter1, this.converter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der den gegebenen {@link Converter} synchronisiert. Die
	 * Synchronisation erfolgt via {@code synchronized(converter)} auf dem gegebenen {@link Converter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class SynchronizedConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public SynchronizedConverter(final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException {
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			synchronized(this.converter){
				return this.converter.convert(input);
			}
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
			if((object == this) || Objects.equals(object, this.converter)) return true;
			if(!(object instanceof SynchronizedConverter<?, ?>)) return false;
			final SynchronizedConverter<?, ?> data = (SynchronizedConverter<?, ?>)object;
			return Objects.equals(this.converter, data.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("synchronizedConverter", this.converter);
		}

	}

	/**
	 * Dieses Feld speichert den leeren {@link Converter}.
	 */
	static final Converter<?, ?> VOID_CONVERTER = new Converter<Object, Object>() {

		@Override
		public Object convert(final Object input) {
			return input;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("voidConverter");
		}

	};

	/**
	 * Diese Methode gibt den leeren {@link Converter} zurück, dessen Ausgabe gleich seiner Eingabe ist.
	 * 
	 * @param <GInput> Typ der Eingabe sowie der Ausgabe.
	 * @return {@code void}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Converter<GInput, GInput> voidConverter() {
		return (Converter<GInput, GInput>)Converters.VOID_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Lesen des durch seinen Namen gegebenen
	 * {@link Field}s an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getField(String)
	 * @see Field#get(Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @return {@link NamedFieldConverter}.
	 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
	 */
	public static <GInput, GOutput> NamedFieldConverter<GInput, GOutput> fieldConverter(final String name)
		throws NullPointerException {
		return new NamedFieldConverter<GInput, GOutput>(name);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Lesen des durch einen Namen und eine
	 * {@link Class} gegebenen {@link Field}s an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getField(String)
	 * @see Field#get(Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @param clazz {@link Class}.
	 * @return {@link FixedFieldConverter}.
	 * @throws NullPointerException Wenn der gegebene Name bzw. die gegebene {@link Class} {@code null} ist.
	 * @throws NoSuchFieldException Wenn an der gegebenen {@link Class} kein {@link Field} mit dem gegebenen Namen
	 *         existiert.
	 * @throws SecurityException Wenn auf das {@link Field} nicht zugegriffen werden darf.
	 */
	public static <GInput, GOutput> FixedFieldConverter<GInput, GOutput> fieldConverter(final String name,
		final Class<? extends GInput> clazz) throws NullPointerException, NoSuchFieldException, SecurityException {
		return Converters.fieldConverter(clazz.getField(name));
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Lesen des gegebenen {@link Field}s an der
	 * Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getField(String)
	 * @see Field#get(Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param field {@link Field}.
	 * @return {@link FixedFieldConverter}.
	 * @throws NullPointerException Wenn das gegebene {@link Field} {@code null} ist.
	 */
	public static <GInput, GOutput> FixedFieldConverter<GInput, GOutput> fieldConverter(final Field field)
		throws NullPointerException {
		return new FixedFieldConverter<GInput, GOutput>(field);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Aufrufen der durch ihren Namen gegebenen
	 * {@link Method} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @return {@link NamedMethodConverter}.
	 * @throws NullPointerException Wenn der gegebene Name {@code null} ist.
	 */
	public static <GInput, GOutput> NamedMethodConverter<GInput, GOutput> methodConverter(final String name)
		throws NullPointerException {
		return new NamedMethodConverter<GInput, GOutput>(name);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Aufrufen der durch einen Namen und eine
	 * {@link Class} gegebenen {@link Method} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @param clazz {@link Class}.
	 * @return {@link FixedMethodConverter}.
	 * @throws NullPointerException Wenn der gegebene Name bzw. die gegebene {@link Class} {@code null} ist.
	 * @throws NoSuchMethodException Wenn an der gegebenen {@link Class} keine {@link Method} mit dem gegebenen Namen
	 *         existiert.
	 * @throws SecurityException Wenn auf die {@link Method} nicht zugegriffen werden darf.
	 */
	public static <GInput, GOutput> FixedMethodConverter<GInput, GOutput> methodConverter(final String name,
		final Class<? extends GInput> clazz) throws NullPointerException, NoSuchMethodException, SecurityException {
		return Converters.methodConverter(clazz.getMethod(name));
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, dessen Ausgabe durch das Aufrufen der gegebenen {@link Method
	 * Methode} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method {@link Method}.
	 * @return {@link FixedMethodConverter}.
	 * @throws NullPointerException Wenn die gegebene {@link Method} {@code null} ist.
	 */
	public static <GInput, GOutput> FixedMethodConverter<GInput, GOutput> methodConverter(final Method method)
		throws NullPointerException {
		return new FixedMethodConverter<GInput, GOutput>(method);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der für jede Eingabe immer die gleiche {@code default}-Ausgabe
	 * liefert, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param output {@code default}-Ausgabe.
	 * @return {@link DefaultConverter}.
	 */
	public static <GInput, GOutput> DefaultConverter<GInput, GOutput> defaultConverter(final GOutput output) {
		return new DefaultConverter<GInput, GOutput>(output);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der über die Weiterleitug der Eingabe mit Hilfe eines einen
	 * {@link Filter}s entscheiden, und gibt ihn zurück. Wenn der gegebene {@link Filter} eine Eingabe akzeptiert, liefert
	 * der erzeugte {@link Converter} dafür die Ausgabe des gegebenen {@code Accept}-{@link Converter}s. Die Ausgabe des
	 * gegebenen {@code Reject}-{@link Converter}s liefert er dagegen für eine vom gegebenen {@link Filter} abgelehnten
	 * Eingabe.
	 * 
	 * @see Filter
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param filter {@link Filter}.
	 * @param accept {@code Accept}-{@link Converter}.
	 * @param reject {@code Reject}-{@link Converter}.
	 * @return {@link FilteredConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter} oder einer der gegebenen {@link Converter}
	 *         {@code null} sind.
	 */
	public static <GInput, GOutput> FilteredConverter<GInput, GOutput> filteredConverter(
		final Filter<? super GInput> filter, final Converter<? super GInput, ? extends GOutput> accept,
		final Converter<? super GInput, ? extends GOutput> reject) throws NullPointerException {
		return new FilteredConverter<GInput, GOutput>(filter, accept, reject);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Converter} und gibt ihn zurück. Der erzeugte {@link Converter}
	 * verwaltet die vom gegebenen {@link Converter} erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die
	 * Schlüssel werden dabei über {@link SoftPointer} auf Eingaben und die Werte als {@link SoftPointer} auf die Ausgaben
	 * des gegebenen {@link Converter}s realisiert. Die Anzahl der Einträge in der {@link Map Abbildung} sind nicht
	 * beschränkt. Der erzeute {@link Converter} realisiert damit einen speichersensitiven, assoziativen Cache.
	 * 
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 * @param converter {@link Converter}.
	 * @return {@link CachedConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> CachedConverter<GInput, GOutput> cachedConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return Converters.cachedConverter(-1, Pointers.SOFT, Pointers.SOFT, converter);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Converter} und gibt ihn zurück. Der erzeugte {@link Converter}
	 * verwaltet die vom gegebenen {@link Converter} erzeugten Ausgaben in einer {@link Map} von Schlüsseln auf Werte. Die
	 * Schlüssel werden dabei über {@link Pointer} auf Eingaben und die Werte als {@link Pointer} auf die Ausgaben des
	 * gegebenen {@link Converter}s realisiert.
	 * 
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map}.
	 * @param inputMode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map}
	 *        erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer} auf die Ausgabe-Datensätze für die Werte der {@link Map}
	 *        erzeugt werden.
	 * @param converter {@link Converter}.
	 * @return {@link CachedConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn einer der gegebenen Modi ungültig ist.
	 */
	public static <GInput, GOutput> CachedConverter<GInput, GOutput> cachedConverter(final int limit,
		final int inputMode, final int outputMode, final Converter<? super GInput, ? extends GOutput> converter)
		throws NullPointerException, IllegalArgumentException {
		return new CachedConverter<GInput, GOutput>(limit, inputMode, outputMode, converter);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter}, der seine Eingabe an einen ersten {@link Converter}
	 * weiterleitet, dessen Ausgabe an einen zweiten {@link Converter} übergibt und dessen Ausgabe liefert, und gibt ihn
	 * zurück.
	 * 
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter}s.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter}s sowie der Eingabe des zweiten {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter}s.
	 * @param converter1 erster {@link Converter}.
	 * @param converter2 zweiter {@link Converter}.
	 * @return {@link ChainedConverter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Converter} {@code null} ist.
	 */
	public static <GInput, GValue, GOutput> ChainedConverter<GInput, GValue, GOutput> chainedConverter(
		final Converter<? super GInput, ? extends GValue> converter1,
		final Converter<? super GValue, ? extends GOutput> converter2) throws NullPointerException {
		return new ChainedConverter<GInput, GValue, GOutput>(converter1, converter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der den gegebenen {@link Converter} synchronisiert, und gibt ihn
	 * zurück. Die Synchronisation erfolgt via {@code synchronized(converter)} auf dem gegebenen {@link Converter}.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link SynchronizedConverter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> SynchronizedConverter<GInput, GOutput> synchronizedConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new SynchronizedConverter<GInput, GOutput>(converter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Converters() {
	}

}
