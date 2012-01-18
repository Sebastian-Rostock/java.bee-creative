package bee.creative.util;

import bee.creative.util.Converters.ConverterLink;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Builder
 * Buildern}.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Builder Builder} zur realisierung eines statischen Caches für
 * Instanzen der exemplarischen Klasse {@code Helper} verwendet:
 * 
 * <pre>
 * public final class Helper {
 * 
 *   static final {@literal Builder<Helper>} CACHE = Builders.synchronizedBuilder(Builders.cachedBuilder(new Builder<Helper>() {
 *   
 *     public Helper build() {
 *       return new Helper();
 *     }
 *     
 *   }));
 *   
 *   public static Helper get() {
 *     return Helper.CACHE.build();
 *   }
 *   
 *   protected Helper() {
 *     ...
 *   }
 *   
 *   ...
 *   
 * }</pre>
 * 
 * @see Builder
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Builders {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dass auf einen {@link Builder Builder} verweist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	static abstract class BuilderLink<GData> {

		/**
		 * Dieses Feld speichert den {@link Builder Builder}.
		 */
		final Builder<? extends GData> builder;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Builder Builder}.
		 * 
		 * @param builder {@link Builder Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
		 */
		public BuilderLink(final Builder<? extends GData> builder) throws NullPointerException {
			if(builder == null) throw new NullPointerException();
			this.builder = builder;
		}

		/**
		 * Diese Methode gibt den {@link Builder Builder} zurück.
		 * 
		 * @return {@link Builder Builder}.
		 */
		public Builder<? extends GData> builder() {
			return this.builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BuilderLink<?> data = (BuilderLink<?>)object;
			return Objects.equals(this.builder, data.builder);
		}

	}

	/**
	 * Diese Klasse implementiert einen gepufferten {@link Builder Builder}. Ein gepufferter {@link Builder Builder}
	 * verwaltet den vom einem gegebenen {@link Builder Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer
	 * Pointers}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class CachedBuilder<GData> extends BuilderLink<GData> implements Builder<GData> {

		/**
		 * Dieses Feld speichert den Modus.
		 */
		final int mode;

		/**
		 * Dieses Feld speichert das {@link Pointer Pointer}.
		 */
		Pointer<GData> pointer;

		/**
		 * Dieser Konstrukteur initialisiert Modus und {@link Builder Builder}.
		 * 
		 * @param mode Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
		 * @param builder {@link Builder Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist.
		 */
		public CachedBuilder(final int mode, final Builder<? extends GData> builder) throws NullPointerException,
			IllegalArgumentException {
			super(builder);
			Pointers.pointer(mode, null);
			this.mode = mode;
		}

		/**
		 * Diese Methode gibt den {@link Pointer Pointer}-Modus zurück.
		 * 
		 * @see Pointers#pointer(int, Object)
		 * @return {@link Pointer Pointer}-Modus.
		 */
		public int mode() {
			return this.mode;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData build() {
			if(this.pointer != null){
				if(this.pointer == Pointers.NULL_POINTER) return null;
				final GData data = this.pointer.data();
				if(data != null) return data;
			}
			final GData data = this.builder.build();
			this.pointer = Pointers.pointer(this.mode, data);
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if((object == this) || this.builder.equals(object)) return true;
			if(!(object instanceof CachedBuilder<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("cachedBuilder", this.builder);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder Builder}, dessen Datensatz mit Hilfe eines gegebenen
	 * {@link Converter Converters} aus dem Datensatz eines gegebenen {@link Builder Builders} ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder Builders} sowie der Eingabe des gegebenen
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie des Datensatzes.
	 */
	public static final class ConvertedBuilder<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Builder<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Builder Builder}.
		 */
		final Builder<? extends GInput> builder;

		/**
		 * Dieser Konstrukteur initialisiert {@link Builder Builder} und {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @param builder {@link Builder Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} bzw. der gegebene {@link Builder
		 *         Builder} {@code null} ist.
		 */
		public ConvertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
			final Builder<? extends GInput> builder) throws NullPointerException {
			super(converter);
			if(builder == null) throw new NullPointerException();
			this.builder = builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput build() {
			return this.converter.convert(this.builder.build());
		}

		/**
		 * Diese Methode gibt den {@link Builder Builder} zurück.
		 * 
		 * @return {@link Builder Builder}.
		 */
		public Builder<? extends GInput> builder() {
			return this.builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.builder, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConvertedBuilder<?, ?>)) return false;
			final ConvertedBuilder<?, ?> data = (ConvertedBuilder<?, ?>)object;
			return super.equals(object) && Objects.equals(this.builder, data.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("ConvertedBuilder", this.converter, this.builder);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder Builder}, der einen gegebenen {@link Builder Builder}
	 * synchronisiert. Die Synchronisation erfolgt via {@code synchronized(builder)} auf dem gegebenen {@link Builder
	 * Builder}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class SynchronizedBuilder<GData> extends BuilderLink<GData> implements Builder<GData> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Builder Builder}.
		 * 
		 * @param builder {@link Builder Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
		 */
		public SynchronizedBuilder(final Builder<? extends GData> builder) {
			super(builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData build() {
			synchronized(this.builder){
				return this.builder.build();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if((object == this) || this.builder.equals(object)) return true;
			if(!(object instanceof SynchronizedBuilder<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("synchronizedBuilder", this.builder);
		}

	}

	/**
	 * Dieses Feld speichert den {@link Builder Builder} zurück, der immer {@code null} liefert.
	 */
	static final Builder<?> NULL_BUILDER = new Builder<Object>() {

		@Override
		public Object build() {
			return null;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("nullBuilder");
		}

	};

	/**
	 * Diese Methode gibt den {@link Builder Builder} zurück, der immer {@code null} liefert.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @return {@code null}-{@link Builder Builder}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Builder<GData> nullBuilder() {
		return (Builder<GData>)Builders.NULL_BUILDER;
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Builder Builder} und gibt ihn zurück. Der erzeugte {@link Builder
	 * Builder} verwaltet den vom gegebenen {@link Builder Builder} erzeugten Datensatz mit Hilfe eines
	 * {@link SoftPointer Soft-Pointers}.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param builder {@link Builder Builder}.
	 * @return {@link CachedBuilder Cached-Builder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
	 */
	public static <GData> Builder<GData> cachedBuilder(final Builder<? extends GData> builder)
		throws NullPointerException {
		return Builders.cachedBuilder(Pointers.SOFT, builder);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Builder Builder} und gibt ihn zurück. Der erzeugte {@link Builder
	 * Builder} verwaltet den vom gegebenen {@link Builder Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer
	 * Pointers}.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param mode Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param builder {@link Builder Builder}.
	 * @return {@link CachedBuilder Cached-Builder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen Modus ungültig ist.
	 */
	public static <GData> Builder<GData> cachedBuilder(final int mode, final Builder<? extends GData> builder)
		throws NullPointerException, IllegalArgumentException {
		return new CachedBuilder<GData>(mode, builder);
	}

	/**
	 * Diese Methode erzeugt einen {@link Builder Builder}, dessen Datensatz mit Hilfe eines gegebenen {@link Converter
	 * Converters} aus dem Datensatz eines gegebenen {@link Builder Builders} ermittelt wird, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder Builders} sowie der Eingabe des gegebenen
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie des Datensatzes.
	 * @param converter {@link Converter Converter}.
	 * @param builder {@link Builder Builder}.
	 * @return {@link ConvertedBuilder Converted-Builder}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} bzw. der gegebene {@link Builder
	 *         Builder} {@code null} ist.
	 */
	public static <GInput, GOutput> Builder<GOutput> convertedBuilder(
		final Converter<? super GInput, ? extends GOutput> converter, final Builder<? extends GInput> builder)
		throws NullPointerException {
		return new ConvertedBuilder<GInput, GOutput>(converter, builder);
	}

	/**
	 * Diese Methode gibt erzeugt einen {@link Builder Builder}, der einen gegebenen {@link Builder Builder}
	 * synchronisiert, und gibt ihn zurück. Die Synchronisation erfolgt via {@code synchronized(builder)} auf dem
	 * gegebenen {@link Builder Builder}.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param builder {@link Builder Builder}.
	 * @return {@link SynchronizedBuilder Synchronized-Builder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder Builder} {@code null} ist.
	 */
	public static <GData> Builder<GData> synchronizedBuilder(final Builder<? extends GData> builder)
		throws NullPointerException {
		return new SynchronizedBuilder<GData>(builder);
	}

}
