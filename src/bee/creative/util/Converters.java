package bee.creative.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.util.Filters.FilterLink;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Converter
 * Convertern}.
 * 
 * @see Converter
 * @see Converters
 * @see Conversion
 * @see Conversions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Converters {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dass auf einen {@link Converter Converter} verweist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters}.
	 */
	static abstract class ConverterLink<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 */
		public ConverterLink(final Converter<? super GInput, ? extends GOutput> converter) {
			if(converter == null) throw new NullPointerException();
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
			final ConverterLink<?, ?> data = (ConverterLink<?, ?>)object;
			return this.converter.equals(data.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Converter Converter} mit Name.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static abstract class NamedConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den Namen.
		 */
		final String name;

		/**
		 * Dieser Konstrukteur initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name <code>null</code> ist.
		 */
		public NamedConverter(final String name) throws NullPointerException {
			if(name == null) throw new NullPointerException();
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
			final NamedConverter<?, ?> data = (NamedConverter<?, ?>)object;
			return Objects.equals(this.name, data.name);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, dessen Ausgabe durch das Lesen eines gegebenen
	 * {@link Field Fields} an der Eingabe ermittelt wird.
	 * 
	 * @see Field#get(Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class FixedFieldConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert das {@link Field Feld}.
		 */
		final Field field;

		/**
		 * Dieser Konstrukteur initialisiert das {@link Field Feld}.
		 * 
		 * @param field {@link Field Feld}.
		 * @throws NullPointerException Wenn das gegebene {@link Field Feld} <code>null</code> ist.
		 */
		public FixedFieldConverter(final Field field) throws NullPointerException {
			if(field == null) throw new NullPointerException();
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
	 * Diese Klasse implementiert einen {@link Converter Converter}, dessen Ausgabe durch das Aufrufen einer gegebenen
	 * {@link Method Methode} an der Eingabe ermittelt wird.
	 * 
	 * @see Method#invoke(Object, Object...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class FixedMethodConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Method Methode}.
		 */
		final Method method;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Method Methode}.
		 * 
		 * @param method {@link Method Methode}.
		 * @throws NullPointerException Wenn die gegebene {@link Method Methode} <code>null</code> ist.
		 */
		public FixedMethodConverter(final Method method) throws NullPointerException {
			if(method == null) throw new NullPointerException();
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
	 * Diese Klasse implementiert einen {@link Converter Converter}, dessen Ausgabe durch das Lesen eines durch einen
	 * Namen gegebenen {@link Field Fields} an der Eingabe ermittelt wird.
	 * 
	 * @see Field#get(Object)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class NamedFieldConverter<GInput, GOutput> extends NamedConverter<GInput, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name <code>null</code> ist.
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
			if(input == null) throw new NullPointerException();
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
	 * Diese Klasse implementiert einen {@link Converter Converter}, dessen Ausgabe durch das Aufrufen einer durch einen
	 * Namen gegebenen {@link Method Methode} an der Eingabe ermittelt wird.
	 * 
	 * @see Method#invoke(Object, Object...)
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class NamedMethodConverter<GInput, GOutput> extends NamedConverter<GInput, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert den Namen.
		 * 
		 * @param name Name.
		 * @throws NullPointerException Wenn der gegebene Name <code>null</code> ist.
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
			if(input == null) throw new NullPointerException();
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
	 * Diese Klasse implementiert einen {@link Converter Converter}, der für jede Eingabe immer die gleiche
	 * Standardausgabe liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class DefaultConverter<GInput, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die Standardausgabe.
		 */
		final GOutput output;

		/**
		 * Dieser Konstrukteur initialisiert die Standardausgabe.
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
	 * Diese Klasse implementiert einen {@link Converter Converter}, der über die Weiterleitug der Eingabe mit Hilfe eines
	 * einen {@link Filter Filters} entscheiden. Wenn der gegebene {@link Filter Filter} eine Eingabe akzeptiert, liefert
	 * der {@link Converter Converter} dafür die Ausgabe des gegebenen {@link Converter Accept-Converters}. Die Ausgabe
	 * des gegebenen {@link Converter Reject-Converters} liefert er dagegen für eine vom gegebenen {@link Filter Filter}
	 * abgelehnten Eingabe.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class FilteredConverter<GInput, GOutput> extends FilterLink<GInput> implements
		Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter Accept-Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> accept;

		/**
		 * Dieses Feld speichert den {@link Converter Reject-Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> reject;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter Filter} und {@link Converter Converter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param accept {@link Converter Accept-Converter}.
		 * @param reject {@link Converter Reject-Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} oder einer der gegebenen {@link Converter
		 *         Converter} <code>null</code> sind.
		 */
		public FilteredConverter(final Filter<? super GInput> filter,
			final Converter<? super GInput, ? extends GOutput> accept,
			final Converter<? super GInput, ? extends GOutput> reject) throws NullPointerException {
			super(filter);
			if((accept == null) || (reject == null)) throw new NullPointerException();
			this.accept = accept;
			this.reject = reject;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			return (this.filter.accept(input) ? this.accept.convert(input) : this.reject.convert(input));
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
			return super.equals(object) && Objects.equals(this.accept, data.accept, this.reject, data.reject);
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
	 * Diese Klasse implementiert einen gepufferten {@link Converter Converter}. Ein gepufferter {@link Converter
	 * Converter} verwaltet die vom einem gegebenen {@link Converter Converter} erzeugten Ausgaben in einer {@link Map
	 * Abbildung} von Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link Pointer Verweise} auf Eingaben und die
	 * Werte als {@link Pointer Verweise} auf die Ausgaben des gegebenen {@link Converter Converters} realisiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 */
	public static final class CachedConverter<GInput, GOutput> extends Converters.ConverterLink<GInput, GOutput>
		implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert die {@link Map Abbildung} von Schlüsseln ({@link Pointer Verweise} auf Eingaben) auf Werte
		 * ( {@link Pointer Verweise} auf die Ausgaben).
		 */
		final Map<Pointer<GInput>, Pointer<GOutput>> map;

		/**
		 * Dieses Feld speichert das Maximum für die Anzahl der Einträge in der {@link Map Abbildung}.
		 */
		final int limit;

		/**
		 * Dieses Feld speichert den Modus, in dem die {@link Pointer Verweise} auf die Eingabe-Datensätze für die Schlüssel
		 * der Abbildung erzeugt werden.
		 * 
		 * @see Pointers#pointer(int, Object)
		 */
		final int inputMode;

		/**
		 * Dieses Feld speichert den Modus, in dem die {@link Pointer Verweise} auf die Ausgabe-Datensätze für die Werte der
		 * Abbildung erzeugt werden.
		 * 
		 * @see Pointers#pointer(int, Object)
		 */
		final int outputMode;

		/**
		 * Dieser Konstrukteur initialisiert den gepuferten {@link Converter Converter}.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map Abbildung}.
		 * @param inputMode Modus, in dem die {@link Pointer Verweise} auf die Eingabe-Datensätze für die Schlüssel der
		 *        Abbildung erzeugt werden.
		 * @param outputMode Modus, in dem die {@link Pointer Verweise} auf die Ausgabe-Datensätze für die Werte der
		 *        Abbildung erzeugt werden.
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 * @throws IllegalArgumentException Wenn einer der gegebenen Modi ungültig ist.
		 */
		public CachedConverter(final int limit, final int inputMode, final int outputMode,
			final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException,
			IllegalArgumentException {
			super(converter);
			Pointers.pointer(inputMode, null);
			Pointers.pointer(outputMode, null);
			this.map = new LinkedHashMap<Pointer<GInput>, Pointer<GOutput>>(0, 0.75f, true);
			this.limit = limit;
			this.inputMode = inputMode;
			this.outputMode = outputMode;
		}

		/**
		 * Diese Methode leert die Abbildung.
		 */
		public void clear() {
			this.map.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput convert(final GInput input) {
			final Pointer<? extends GOutput> data = this.map.get(Pointers.hardPointer(input));
			if(data != null){
				if(data == Pointers.NULL_POINTER) return null;
				final GOutput output = data.data();
				if(output != null) return output;
				int valid = this.limit - 1;
				for(final Iterator<Entry<Pointer<GInput>, Pointer<GOutput>>> iterator = this.map.entrySet().iterator(); iterator
					.hasNext();){
					final Entry<Pointer<GInput>, Pointer<GOutput>> entry = iterator.next();
					final Pointer<?> key = entry.getKey(), value = entry.getValue();
					if(valid != 0){
						if(((key != Pointers.NULL_POINTER) && (key.data() == null))
							|| ((value != Pointers.NULL_POINTER) && (value.data() == null))){
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
		public boolean equals(final Object object) {
			if((object == this) || Objects.equals(this.converter, object)) return true;
			if(!(object instanceof CachedConverter<?, ?>)) return false;
			return super.equals(object);
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
	 * Diese Klasse implementiert einen verketteten {@link Converter Converter}, der seine Eingabe an einen ersten
	 * {@link Converter Converter} weiterleitet, dessen Ausgabe an einen zweiten {@link Converter Converter} übergibt und
	 * dessen Ausgabe liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter Converters}.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter Converters} sowie der Eingabe des zweiten
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter Converters}.
	 */
	public static final class ChainedConverter<GInput, GValue, GOutput> implements Converter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den ersten {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GValue> converter1;

		/**
		 * Dieses Feld speichert den zweiten {@link Converter Converter}.
		 */
		final Converter<? super GValue, ? extends GOutput> converter2;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Converter Converter}.
		 * 
		 * @param converter1 erster {@link Converter Converter}.
		 * @param converter2 zweiter {@link Converter Converter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Converter Converter} <code>null</code> ist.
		 */
		public ChainedConverter(final Converter<? super GInput, ? extends GValue> converter1,
			final Converter<? super GValue, ? extends GOutput> converter2) throws NullPointerException {
			if((converter1 == null) || (converter2 == null)) throw new NullPointerException();
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
			return this.converter1.equals(data.converter1) && this.converter2.equals(data.converter2);
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
	 * Diese Klasse implementiert einen {@link Converter Converter}, der den gegebenen {@link Converter Converter}
	 * synchronisiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class SynchronizedConverter<GInput, GOutput> extends Converters.ConverterLink<GInput, GOutput>
		implements Converter<GInput, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 */
		public SynchronizedConverter(final Converter<? super GInput, ? extends GOutput> converter)
			throws NullPointerException {
			super(converter);
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
		public boolean equals(final Object object) {
			if((object == this) || Objects.equals(object, this.converter)) return true;
			if(!(object instanceof SynchronizedConverter<?, ?>)) return false;
			return super.equals(object);
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
	 * Dieses Feld speichert den leeren {@link Converter Converter}.
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
	 * Diese Methode gibt den leeren {@link Converter Converter} zurück, dessen Ausgabe gleich seiner Eingabe ist.
	 * 
	 * @param <GInput> Typ der Eingabe sowie der Ausgabe.
	 * @return <code>Void</code>-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Converter<GInput, GInput> voidConverter() {
		return (Converter<GInput, GInput>)Converters.VOID_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, dessen Ausgabe durch das Lesen des durch seinen Namen
	 * gegebenen {@link Field Fields} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getField(String)
	 * @see Field#get(Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @return {@link NamedFieldConverter Named-Field-Converter}.
	 * @throws NullPointerException Wenn der gegebene Name <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> fieldConverter(final String name)
		throws NullPointerException {
		return new NamedFieldConverter<GInput, GOutput>(name);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, dessen Ausgabe durch das Lesen des durch einen Namen und
	 * eine {@link Class Klasse} gegebenen {@link Field Fields} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getField(String)
	 * @see Field#get(Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @param clazz {@link Class Klasse}.
	 * @return {@link FixedFieldConverter Fixed-Field-Converter}.
	 * @throws NullPointerException Wenn der gegebene Name bzw. die gegebene {@link Class Klasse} <code>null</code> ist.
	 * @throws NoSuchFieldException Wenn an der gegebenen {@link Class Klasse} kein {@link Field Feld} mit dem gegebenen
	 *         Namen existiert.
	 * @throws SecurityException Wenn auf das {@link Field Feld} nicht zugegriffen werden darf.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> fieldConverter(final String name,
		final Class<? extends GInput> clazz) throws NullPointerException, NoSuchFieldException, SecurityException {
		return Converters.fieldConverter(clazz.getField(name));
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, dessen Ausgabe durch das Lesen des gegebenen {@link Field
	 * Fields} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getField(String)
	 * @see Field#get(Object)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param field {@link Field Feld}.
	 * @return {@link FixedFieldConverter Fixed-Field-Converter}.
	 * @throws NullPointerException Wenn das gegebene {@link Field Feld} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> fieldConverter(final Field field)
		throws NullPointerException {
		return new FixedFieldConverter<GInput, GOutput>(field);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, dessen Ausgabe durch das Aufrufen der durch ihren Namen
	 * gegebenen {@link Method Methode} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @return {@link NamedMethodConverter Named-Method-Converter}.
	 * @throws NullPointerException Wenn der gegebene Name <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> methodConverter(final String name)
		throws NullPointerException {
		return new NamedMethodConverter<GInput, GOutput>(name);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, dessen Ausgabe durch das Aufrufen der durch einen Namen
	 * und eine {@link Class Klasse} gegebenen {@link Method Methode} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param name Name.
	 * @param clazz {@link Class Klasse}.
	 * @return {@link FixedMethodConverter Fixed-Method-Converter}.
	 * @throws NullPointerException Wenn der gegebene Name bzw. die gegebene {@link Class Klasse} <code>null</code> ist.
	 * @throws NoSuchMethodException Wenn an der gegebenen {@link Class Klasse} keine {@link Method Methode} mit dem
	 *         gegebenen Namen existiert.
	 * @throws SecurityException Wenn auf die {@link Method Methode} nicht zugegriffen werden darf.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> methodConverter(final String name,
		final Class<? extends GInput> clazz) throws NullPointerException, NoSuchMethodException, SecurityException {
		return Converters.methodConverter(clazz.getMethod(name));
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, dessen Ausgabe durch das Aufrufen der gegebenen
	 * {@link Method Methode} an der Eingabe ermittelt wird, und gibt ihn zurück.
	 * 
	 * @see Class#getMethod(String, Class...)
	 * @see Method#invoke(Object, Object...)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param method {@link Method Methode}.
	 * @return {@link FixedMethodConverter Fixed-Method-Converter}.
	 * @throws NullPointerException Wenn die gegebene {@link Method Methode} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> methodConverter(final Method method)
		throws NullPointerException {
		return new FixedMethodConverter<GInput, GOutput>(method);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der für jede Eingabe immer die gleiche Standardausgabe
	 * liefert, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param output Standardausgabe.
	 * @return {@link DefaultConverter Default-Converter}.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> defaultConverter(final GOutput output) {
		return new DefaultConverter<GInput, GOutput>(output);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der über die Weiterleitug der Eingabe mit Hilfe eines
	 * einen {@link Filter Filters} entscheiden, und gibt ihn zurück. Wenn der gegebene {@link Filter Filter} eine Eingabe
	 * akzeptiert, liefert der erzeugte {@link Converter Converter} dafür die Ausgabe des gegebenen {@link Converter
	 * Accept-Converters}. Die Ausgabe des gegebenen {@link Converter Reject-Converters} liefert er dagegen für eine vom
	 * gegebenen {@link Filter Filter} abgelehnten Eingabe.
	 * 
	 * @see Filter
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param filter {@link Filter Filter}.
	 * @param accept {@link Converter Accept-Converter}.
	 * @param reject {@link Converter Reject-Converter}.
	 * @return {@link FilteredConverter Filtered-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} oder einer der gegebenen {@link Converter
	 *         Converter} <code>null</code> sind.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> filteredConverter(final Filter<? super GInput> filter,
		final Converter<? super GInput, ? extends GOutput> accept, final Converter<? super GInput, ? extends GOutput> reject)
		throws NullPointerException {
		return new FilteredConverter<GInput, GOutput>(filter, accept, reject);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Converter Converter} und gibt ihn zurück. Der erzeugte
	 * {@link Converter Converter} verwaltet die vom gegebenen {@link Converter Converter} erzeugten Ausgaben in einer
	 * {@link Map Abbildung} von Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link SoftPointer weiche Verweise}
	 * auf Eingaben und die Werte als {@link SoftPointer weiche Verweise} auf die Ausgaben des gegebenen {@link Converter
	 * Converters} realisiert. Die Anzahl der Einträge in der {@link Map Abbildung} sind nicht beschränkt. Der erzeute
	 * {@link Converter Converter} realisiert damit einen speichersensitiven, assoziativen Cache.
	 * 
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 * @param converter {@link Converter Converter}.
	 * @return {@link CachedConverter Cached-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> cachedConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return Converters.cachedConverter(-1, Pointers.SOFT, Pointers.SOFT, converter);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Converter Converter} und gibt ihn zurück. Der erzeugte
	 * {@link Converter Converter} verwaltet die vom gegebenen {@link Converter Converter} erzeugten Ausgaben in einer
	 * {@link Map Abbildung} von Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link Pointer Verweise} auf
	 * Eingaben und die Werte als {@link Pointer Verweise} auf die Ausgaben des gegebenen {@link Converter Converters}
	 * realisiert.
	 * 
	 * @param <GInput> Typ der Eingabe bzw. der Datensätze in den Schlüsseln.
	 * @param <GOutput> Typ der Ausgabe bzw. der Datensätze in den Werten.
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map Abbildung}.
	 * @param inputMode Modus, in dem die {@link Pointer Verweise} auf die Eingabe-Datensätze für die Schlüssel der
	 *        {@link Map Abbildung} erzeugt werden.
	 * @param outputMode Modus, in dem die {@link Pointer Verweise} auf die Ausgabe-Datensätze für die Werte der
	 *        {@link Map Abbildung} erzeugt werden.
	 * @param converter {@link Converter Converter}.
	 * @return {@link CachedConverter Cached-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 * @throws IllegalArgumentException Wenn einer der gegebenen Modi ungültig ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> cachedConverter(final int limit, final int inputMode,
		final int outputMode, final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException,
		IllegalArgumentException {
		return new CachedConverter<GInput, GOutput>(limit, inputMode, outputMode, converter);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter Converter}, der seine Eingabe an einen ersten
	 * {@link Converter Converter} weiterleitet, dessen Ausgabe an einen zweiten {@link Converter Converter} übergibt und
	 * dessen Ausgabe liefert, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter Converters}.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter Converters} sowie der Eingabe des zweiten
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter Converters}.
	 * @param converter1 erster {@link Converter Converter}.
	 * @param converter2 zweiter {@link Converter Converter}.
	 * @return {@link ChainedConverter Chained-Converter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GValue, GOutput> Converter<GInput, GOutput> chainedConverter(
		final Converter<? super GInput, ? extends GValue> converter1,
		final Converter<? super GValue, ? extends GOutput> converter2) throws NullPointerException {
		return new ChainedConverter<GInput, GValue, GOutput>(converter1, converter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der den gegebenen {@link Converter Converter}
	 * synchronisiert, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ des Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter Converter}.
	 * @return {@link SynchronizedConverter Synchronized-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Converter<GInput, GOutput> synchronizedConverter(
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new SynchronizedConverter<GInput, GOutput>(converter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Converters() {
	}

}
