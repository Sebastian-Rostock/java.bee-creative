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
 *   static final {@literal Builder<Helper>} CACHE = SynchronizedBuilder.of(CachedBuilder.of(new Builder<Helper>() {
 *   
 *     public Helper create() {
 *       return new Helper();
 *     }
 *     
 *   }));
 *   
 *   public static Helper get() {
 *     return Helper.CACHE.create();
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
	 * Diese Klasse implementiert einen abstrakten delegierenden {@link Builder}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ der Datensatzes.
	 * @param <GData2> Typ des Datensatzes des gegebenen {@link Builder}s.
	 */
	static abstract class AbstractBuilder<GData, GData2> implements Builder<GData> {

		/**
		 * Dieses Feld speichert den {@link Builder}.
		 */
		final Builder<? extends GData2> builder;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Builder}.
		 * 
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 */
		public AbstractBuilder(final Builder<? extends GData2> builder) throws NullPointerException {
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
			final AbstractBuilder<?, ?> data = (AbstractBuilder<?, ?>)object;
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
	 * Diese Klasse implementiert einen {@link Builder}, der immer {@code null} liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullBuilder implements Builder<Object> {

		/**
		 * Dieses Feld speichert den {@link Builder}, der immer {@code null} liefert.
		 */
		public static final Builder<?> INSTANCE = new NullBuilder();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object create() {
			return null;
		}

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
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NullBuilder);
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
	 * Diese Klasse implementiert einen gepufferten {@link Builder}. Ein gepufferter {@link Builder} verwaltet den vom einem gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer}s.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class CachedBuilder<GData> extends AbstractBuilder<GData, GData> {

		/**
		 * Dieses Feld speichert den {@link Pointer}-Modus.
		 */
		final int mode;

		/**
		 * Dieses Feld speichert den {@link Pointer}.
		 */
		Pointer<GData> pointer;

		/**
		 * Dieser Konstrukteur initialisiert {@link Pointer}-Modus und {@link Builder}.
		 * 
		 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 * @throws IllegalArgumentException Wenn der gegebenen {@link Pointer}-Modus ungültig ist.
		 */
		public CachedBuilder(final int mode, final Builder<? extends GData> builder) throws NullPointerException, IllegalArgumentException {
			super(builder);
			Pointers.pointerConverter(mode);
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
		public GData create() {
			final Pointer<GData> pointer = this.pointer;
			if(pointer != null){
				final GData data = pointer.data();
				if(data != null) return data;
				if(pointer == NullPointer.INSTANCE) return null;
			}
			final GData data = this.builder.create();
			this.pointer = Pointers.pointerOf(this.mode, data);
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

	}

	/**
	 * Diese Klasse implementiert einen {@link Builder}, dessen Datensatz mit Hilfe eines gegebenen {@link Converter}s aus dem Datensatz eines gegebenen {@link Builder}s ermittelt wird.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder}s sowie der Eingabe des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 */
	public static final class ConvertedBuilder<GInput, GOutput> extends AbstractBuilder<GOutput, GInput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Builder} und {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Builder} {@code null} ist.
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
		public GOutput create() {
			return this.converter.convert(this.builder.create());
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
	 * Diese Klasse implementiert einen {@link Builder}, der einen gegebenen {@link Builder} synchronisiert. Die Synchronisation erfolgt via {@code synchronized(builder)} auf dem gegebenen {@link Builder}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GData> Typ des Datensatzes.
	 */
	public static final class SynchronizedBuilder<GData> extends AbstractBuilder<GData, GData> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Builder}.
		 * 
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 */
		public SynchronizedBuilder(final Builder<? extends GData> builder) {
			super(builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GData create() {
			synchronized(this.builder){
				return this.builder.create();
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
	 * Diese Methode gibt den gegebenen {@link Builder} oder {@link NullBuilder#INSTANCE} zurück. Wenn die Eingabe {@code null} ist, wird {@link NullBuilder#INSTANCE} zurück gegeben.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param builder {@link Builder} oder {@code null}.
	 * @return {@link Builder}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GData> Builder<GData> builder(final Builder<GData> builder) {
		return builder != null ? null : (Builder<GData>)NullBuilder.INSTANCE;
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Builder} und gibt ihn zurück. Der erzeugte {@link Builder} verwaltet den vom gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link SoftPointer}s.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@link CachedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GData> CachedBuilder<GData> cachedBuilder(final Builder<? extends GData> builder) throws NullPointerException {
		return Builders.cachedBuilder(Pointers.SOFT, builder);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Builder} und gibt ihn zurück. Der erzeugte {@link Builder} verwaltet den vom gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer}s.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param builder {@link Builder}.
	 * @return {@link CachedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 * @throws IllegalArgumentException Wenn der gegebenen {@link Pointer}-Modus ungültig ist.
	 */
	public static <GData> CachedBuilder<GData> cachedBuilder(final int mode, final Builder<? extends GData> builder) throws NullPointerException,
		IllegalArgumentException {
		return new CachedBuilder<GData>(mode, builder);
	}

	/**
	 * Diese Methode erzeugt einen {@link Builder}, dessen Datensatz mit Hilfe eines gegebenen {@link Converter}s aus dem Datensatz eines gegebenen {@link Builder}s ermittelt wird, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder}s sowie der Eingabe des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param builder {@link Builder}.
	 * @return {@link ConvertedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GInput, GOutput> ConvertedBuilder<GInput, GOutput> convertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
		final Builder<? extends GInput> builder) throws NullPointerException {
		return new ConvertedBuilder<GInput, GOutput>(converter, builder);
	}

	/**
	 * Diese Methode gibt erzeugt einen {@link SynchronizedBuilder}, der einen gegebenen {@link Builder} synchronisiert, und gibt ihn zurück. Die Synchronisation erfolgt via {@code synchronized(builder)} auf dem gegebenen {@link Builder}.
	 * 
	 * @param <GData> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@link SynchronizedBuilder}.
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GData> SynchronizedBuilder<GData> synchronizedBuilder(final Builder<? extends GData> builder) throws NullPointerException {
		return new SynchronizedBuilder<GData>(builder);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Builders() {
	}

}
