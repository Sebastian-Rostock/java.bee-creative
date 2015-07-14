package bee.creative.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import bee.creative.util.Pointers.NullPointer;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Builder}n.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Builder} zur realisierung eines statischen Caches für Instanzen der exemplarischen Klasse
 * {@code Helper} verwendet:
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
	 * Diese Klasse implementiert einen abstrakten Konfigurator zur Erzeugung eines Datensatzes.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Datensatzes.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseBuilder<GValue, GThiz> implements Builder<GValue> {

		/**
		 * Diese Methode gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		protected abstract GThiz thiz();

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseMapBuilder<GKey, GValue, GThiz> extends BaseBuilder<Map<GKey, GValue>, GThiz> implements Iterable<Entry<GKey, GValue>> {

		/**
		 * Dieses Feld speichert den über {@link #forKey(Object)} gewählten Schlüssel.
		 */
		protected GKey key;

		/**
		 * Dieses Feld speichert die interne Abbildung.
		 */
		protected final Map<GKey, GValue> map;

		/**
		 * Dieser Konstruktor initialisiert die interne Abbildung mit einer neuen {@link HashMap}.
		 */
		public BaseMapBuilder() {
			this.map = new HashMap<>();
		}

		/**
		 * Dieser Konstruktor initialisiert die interne Abbildung.
		 * 
		 * @param entryMap interne Abbildung.
		 * @throws NullPointerException Wenn {@code entryMap} {@code null} ist.
		 */
		public BaseMapBuilder(final Map<GKey, GValue> entryMap) throws NullPointerException {
			if (entryMap == null) throw new NullPointerException("entryMap = null");
			this.map = entryMap;
		}

		{}

		/**
		 * Diese Methode kopiert die gegebenen Einträge in die {@link #map() interne Abbildung} und gibt {@code this} zurück.
		 * 
		 * @see #use(BaseMapBuilder)
		 * @see Map#putAll(Map)
		 * @param map Einträge oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final Map<? extends GKey, ? extends GValue> map) {
			if (map == null) return this.thiz();
			this.map.putAll(map);
			return this.thiz();
		}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @see #use(Map)
		 * @see Map#putAll(Map)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseMapBuilder<? extends GKey, ? extends GValue, ?> data) {
			if (data == null) return this.thiz();
			this.clear();
			return this.use(data.map());
		}

		/**
		 * Diese Methode gibt die interne Abbildung zurück.
		 * 
		 * @see BaseMapBuilder#BaseMapBuilder(Map)
		 * @return interne Abbildung.
		 */
		public Map<GKey, GValue> map() {
			return this.map;
		}

		/**
		 * Diese Methode leert die {@link #map() interne Abbildung} und gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		public GThiz clear() {
			this.map.clear();
			return this.thiz();
		}

		/**
		 * Diese Methode wählt den gegebenen Schlüssel und gibt {@code this} zurück. Dieser Schlüssel wird in den nachfolgenden Aufrufen von {@link #getValue()} und
		 * {@link #useValue(Object)} verwendet.
		 * 
		 * @see #getValue()
		 * @see #useValue(Object)
		 * @param key Schlüssel.
		 * @return {@code this}.
		 */
		public GThiz forKey(final GKey key) {
			this.key = key;
			return this.thiz();
		}

		/**
		 * Diese Methode gibt den Wert zum {@link #forKey(Object) gewählten Schlüssel} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see #useValue(Object)
		 * @return Wert zum gewählten Schlüssel.
		 */
		public GValue getValue() {
			return this.map.get(this.key);
		}

		/**
		 * Diese Methode setzt den Wert zum {@link #forKey(Object) gewählten Schlüssel} und gibt {@code this} zurück.
		 * 
		 * @see #forKey(Object)
		 * @see #getValue()
		 * @param value Wert.
		 * @return {@code this}.
		 */
		public GThiz useValue(final GValue value) {
			this.map.put(this.key, value);
			return this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Map<GKey, GValue> build() throws IllegalStateException {
			return this.map();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<GKey, GValue>> iterator() {
			return this.map.entrySet().iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.map);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Werts.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseValueBuilder<GValue, GThiz> extends BaseBuilder<GValue, GThiz> implements Iterable<GValue> {

		/**
		 * Dieses Feld speichert den Wert.
		 */
		protected GValue value;

		/**
		 * Dieser Konstruktor initialisiert den Wert mit {@code null}.
		 */
		public BaseValueBuilder() {
		}

		{}

		/**
		 * Diese Methode gibt den Wert zurück.
		 * 
		 * @see #use(Object)
		 * @return Wert.
		 */
		public GValue get() {
			return this.value;
		}

		/**
		 * Diese Methode setzt den Wert und gibt {@code this} zurück.
		 * 
		 * @see #get()
		 * @param value Wert.
		 * @return {@code this}.
		 */
		public GThiz use(final GValue value) {
			this.value = value;
			return this.thiz();
		}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @see #use(Object)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseValueBuilder<? extends GValue, ?> data) {
			if (data == null) return this.thiz();
			return this.use(data.get());
		}

		/**
		 * Diese Methode setzt den Wert auf {@code null} und gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		public GThiz clear() {
			this.value = null;
			return this.thiz();
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue build() throws IllegalStateException {
			return this.get();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GValue> iterator() {
			final GValue value = this.get();
			if (value == null) return Iterators.voidIterator();
			return Iterators.entryIterator(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.value);
		}

	}

	{}

	/**
	 * Diese Methode gibt einen {@link Builder} zurück, der den gegebenen Datensatz bereitstellt.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param value Datensatz.
	 * @return {@code value}-{@link Builder}.
	 */
	public static <GValue> Builder<GValue> valueBuilder(final GValue value) {
		return new Builder<GValue>() {

			@Override
			public GValue build() throws IllegalStateException {
				return value;
			}

			@Override
			public String toString() {
				return Objects.toStringCall("valueBuilder", value);
			}

		};
	}

	/**
	 * Diese Methode gibt einen gepufferten {@link Builder} zurück, der den vonm gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link SoftPointer}
	 * verwaltet.
	 * 
	 * @see #cachedBuilder(int, Builder)
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@code cached}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist.
	 */
	public static <GValue> Builder<GValue> cachedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		return Builders.cachedBuilder(Pointers.SOFT, builder);
	}

	/**
	 * Diese Methode gibt einen gepufferten {@link Builder} zurück, der den vonm gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer} im
	 * gegebenenen Modus verwaltet.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param builder {@link Builder}.
	 * @return {@code cached}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code mode} ungültig ist.
	 */
	public static <GValue> Builder<GValue> cachedBuilder(final int mode, final Builder<? extends GValue> builder) throws NullPointerException,
		IllegalArgumentException {
		if (builder == null) throw new NullPointerException();
		Pointers.pointer(mode, null);
		return new Builder<GValue>() {

			Pointer<GValue> pointer;

			@Override
			public GValue build() throws IllegalStateException {
				final Pointer<GValue> pointer = this.pointer;
				if (pointer != null) {
					final GValue data = pointer.data();
					if (data != null) return data;
					if (pointer == NullPointer.INSTANCE) return null;
				}
				final GValue data = builder.build();
				this.pointer = Pointers.pointer(mode, data);
				return data;
			}

			@Override
			public String toString() {
				return Objects.toStringCall("cachedBuilder", mode, builder);
			}

		};
	}

	/**
	 * Diese Methode gibt einen {@link Builder} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Converter} aus dem Datensatz des gegebenen {@link Builder}
	 * ermittelt wird.
	 * 
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder}s sowie der Eingabe des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param builder {@link Builder}.
	 * @return {@code converted}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code builder} {@code null} ist.
	 */
	public static <GInput, GOutput> Builder<GOutput> convertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
		final Builder<? extends GInput> builder) throws NullPointerException {
		if (builder == null) throw new NullPointerException("builder = null");
		if (converter == null) throw new NullPointerException();
		return new Builder<GOutput>() {

			@Override
			public GOutput build() throws IllegalStateException {
				return converter.convert(builder.build());
			}

			@Override
			public String toString() {
				return Objects.toStringCall("convertedBuilder", converter, builder);
			}

		};
	}

	/**
	 * Diese Methode gibt einen synchronisierten {@link Builder} zurück. Die Synchronisation erfolgt via {@code synchronized(this)}.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@code synchronized}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist.
	 */
	public static <GValue> Builder<GValue> synchronizedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		if (builder == null) throw new NullPointerException("builder = null");
		return new Builder<GValue>() {

			@Override
			public GValue build() throws IllegalStateException {
				synchronized (this) {
					return builder.build();
				}
			}

			@Override
			public String toString() {
				return Objects.toStringCall("synchronizedBuilder", builder);
			}

		};
	}

}
