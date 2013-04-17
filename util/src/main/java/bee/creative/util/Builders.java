package bee.creative.util;

import bee.creative.util.Pointers.NullPointer;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Builder}n.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Builder} zur realisierung eines statischen Caches für Instanzen der exemplarischen Klasse {@code Helper} verwendet:
 * 
 * <pre>
 * public final class Helper {
 * 
 *   static final {@literal Builder<Helper> CACHE = Builders.synchronizedBuilder(Builders.cachedBuilder(new Builder<Helper>()} {
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
 * }
 * </pre>
 * 
 * @see Builder
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Builders {

	/**
	 * Diese Klasse implementiert einen abstrakten, delegierenden {@link Builder}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Datensatzes.
	 * @param <GValue2> Typ des Datensatzes des gegebenen {@link Builder}s.
	 */
	static abstract class AbstractDelegatingBuilder<GValue, GValue2> implements Builder<GValue> {

		/**
		 * Dieses Feld speichert den {@link Builder}.
		 */
		final Builder<? extends GValue2> builder;

		/**
		 * Dieser Konstruktor initialisiert den {@link Builder}.
		 * 
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 */
		public AbstractDelegatingBuilder(final Builder<? extends GValue2> builder) throws NullPointerException {
			if(builder == null) throw new NullPointerException("builder is null");
			this.builder = builder;
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
			final AbstractDelegatingBuilder<?, ?> data = (AbstractDelegatingBuilder<?, ?>)object;
			return Objects.equals(this.builder, data.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.builder);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder}, der einen gegebenen Datensatz bereitstellt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Datensatzes.
	 */
	public static final class ValueBuilder<GValue> implements Builder<GValue> {

		/**
		 * Dieses Feld speichert den Wert.
		 */
		GValue value;

		/**
		 * Dieser Konstruktor initialisiert den Wert.
		 * 
		 * @param value Wert.
		 */
		public ValueBuilder(final GValue value) {
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue build() throws IllegalStateException {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ValueBuilder<?>)) return false;
			final ValueBuilder<?> data = (ValueBuilder<?>)object;
			return Objects.equals(this.value, data.value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert einen gepufferten {@link Builder}, der den von einem gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer}s verwaltet.
	 * 
	 * @see Pointer
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Datensatzes.
	 */
	public static final class CachedBuilder<GValue> extends AbstractDelegatingBuilder<GValue, GValue> {

		/**
		 * Dieses Feld speichert den {@link Pointer}-Modus.
		 */
		final int mode;

		/**
		 * Dieses Feld speichert den {@link Pointer}.
		 */
		Pointer<GValue> pointer;

		/**
		 * Dieser Konstruktor initialisiert {@link Pointer}-Modus und {@link Builder}.
		 * 
		 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen {@link Pointer}-Modus ungültig ist.
		 */
		public CachedBuilder(final int mode, final Builder<? extends GValue> builder) throws NullPointerException, IllegalArgumentException {
			super(builder);
			Pointers.pointer(mode, null);
			this.mode = mode;
		}

		/**
		 * Diese Methode leer den Cache.
		 */
		public void clear() {
			this.pointer = null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue build() throws IllegalStateException {
			final Pointer<GValue> pointer = this.pointer;
			if(pointer != null){
				final GValue data = pointer.data();
				if(data != null) return data;
				if(pointer == NullPointer.INSTANCE) return null;
			}
			final GValue data = this.builder.build();
			this.pointer = Pointers.pointer(this.mode, data);
			return data;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof CachedBuilder<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder}, dessen Datensatz mit Hilfe eines gegebenen {@link Converter}s aus dem Datensatz eines gegebenen {@link Builder}s erzeugt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder}s sowie der Eingabe des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 */
	public static final class ConvertedBuilder<GInput, GOutput> extends AbstractDelegatingBuilder<GOutput, GInput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Builder} und {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConvertedBuilder(final Converter<? super GInput, ? extends GOutput> converter, final Builder<? extends GInput> builder) throws NullPointerException {
			super(builder);
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput build() throws IllegalStateException {
			return this.converter.convert(this.builder.build());
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
			return Objects.equals(this.builder, data.builder) && Objects.equals(this.builder, data.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.converter, this.builder);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder}, der einen gegebenen {@link Builder} synchronisiert. Die Synchronisation erfolgt via {@code synchronized(this)} auf dem gegebenen {@link Builder}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Datensatzes.
	 */
	public static final class SynchronizedBuilder<GValue> extends AbstractDelegatingBuilder<GValue, GValue> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Builder}.
		 * 
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 */
		public SynchronizedBuilder(final Builder<? extends GValue> builder) {
			super(builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue build() throws IllegalStateException {
			synchronized(this){
				return this.builder.build();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof SynchronizedBuilder<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Methode erzeugt einen {@link Builder}, der den gegebenen Datensatz bereitstellt, und gibt ihn zurück.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param value Datensatz.
	 * @return {@link ValueBuilder}.
	 */
	public static <GValue> ValueBuilder<GValue> valueBuilder(final GValue value) {
		return new ValueBuilder<GValue>(value);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Builder}, der den von einem gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link SoftPointer}s verwaltet, und gibt ihn zurück.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@link CachedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GValue> CachedBuilder<GValue> cachedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		return Builders.cachedBuilder(Pointers.SOFT, builder);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Builder}, der den von einem gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer}s im gegebenenen Modus verwaltet, und gibt ihn zurück.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param builder {@link Builder}.
	 * @return {@link CachedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen {@link Pointer}-Modus ungültig ist.
	 */
	public static <GValue> CachedBuilder<GValue> cachedBuilder(final int mode, final Builder<? extends GValue> builder) throws NullPointerException,
		IllegalArgumentException {
		return new CachedBuilder<GValue>(mode, builder);
	}

	/**
	 * Diese Methode erzeugt einen {@link Builder}, dessen Datensatz mit Hilfe eines gegebenen {@link Converter}s aus dem Datensatz eines gegebenen {@link Builder}s ermittelt wird, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder}s sowie der Eingabe des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param builder {@link Builder}.
	 * @return {@link ConvertedBuilder}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> ConvertedBuilder<GInput, GOutput> convertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
		final Builder<? extends GInput> builder) throws NullPointerException {
		return new ConvertedBuilder<GInput, GOutput>(converter, builder);
	}

	/**
	 * Diese Methode gibt erzeugt einen {@link SynchronizedBuilder}, der einen gegebenen {@link Builder} synchronisiert, und gibt ihn zurück. Die Synchronisation erfolgt via {@code synchronized(this)} auf dem gegebenen {@link Builder}.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@link SynchronizedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GValue> SynchronizedBuilder<GValue> synchronizedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		return new SynchronizedBuilder<GValue>(builder);
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Builders() {
	}

}
