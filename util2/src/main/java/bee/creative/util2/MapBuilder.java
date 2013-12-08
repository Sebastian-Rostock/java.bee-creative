package bee.creative.util2;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import bee.creative.util.Builder;
import bee.creative.util.Iterables;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link Map}-{@link Builder}s.
 * 
 * @see MapBuilder1
 * @see MapBuilder2
 * @see MapBuilder3
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class MapBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link Map}-{@link Builder}, der eine konfigurierte {@link Map} in ein {@code checked}-, {@code synchronized}- oder
	 * {@code unmodifiable}-{@link Map} umwandeln sowie durch das Hinzufügen von Schlüssel-Wert-Paaren modifizieren kann.
	 * 
	 * @see Collections#checkedMap(Map, Class, Class)
	 * @see Collections#synchronizedMap(Map)
	 * @see Collections#unmodifiableMap(Map)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link Map}.
	 */
	public static interface MapBuilder1<GKey, GValue, GMap extends Map<GKey, GValue>> extends EntriesBuilder<GKey, GValue>,
		MapBuilder3<GKey, GValue, Map<GKey, GValue>> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> add(final GKey key, final GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> add(Entry<? extends GKey, ? extends GValue> entry);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> addAll(final Entry<? extends GKey, ? extends GValue>... entries);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> addAll(Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> addAll(Map<? extends GKey, ? extends GValue> entries);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> remove(GKey key);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> removeAll(GKey... keys);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> removeAll(Iterable<? extends GKey> keys);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Map} via {@link Collections#checkedMap(Map, Class, Class)}.
		 * 
		 * @see Collections#checkedMap(Map, Class, Class)
		 * @param keyType {@link Class} der Schlüssel.
		 * @param valueType {@link Class} der Werte.
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder1<GKey, GValue, Map<GKey, GValue>> asCheckedMap(Class<GKey> keyType, Class<GValue> valueType);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, Map<GKey, GValue>> asSynchronizedMap();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder3<GKey, GValue, Map<GKey, GValue>> asUnmodifiableMap();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GMap build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link SortedMap}-{@link Builder}, der eine konfigurierte {@link SortedMap} in ein {@code checked}-,
	 * {@code synchronized}- oder {@code unmodifiable}-{@link Map} umwandeln sowie durch das Hinzufügen von Schlüssel-Wert-Paaren modifizieren kann.
	 * 
	 * @see Collections#checkedSortedMap(SortedMap, Class, Class)
	 * @see Collections#synchronizedSortedMap(SortedMap)
	 * @see Collections#unmodifiableSortedMap(SortedMap)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link Map}.
	 */
	public static interface MapBuilder2<GKey, GValue, GMap extends SortedMap<GKey, GValue>> extends EntriesBuilder<GKey, GValue>,
		MapBuilder3<GKey, GValue, SortedMap<GKey, GValue>> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> add(final GKey key, final GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> add(Entry<? extends GKey, ? extends GValue> entry);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> addAll(final Entry<? extends GKey, ? extends GValue>... entries);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> addAll(Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> addAll(Map<? extends GKey, ? extends GValue> entries);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> remove(GKey key);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> removeAll(GKey... keys);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> removeAll(Iterable<? extends GKey> keys);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Map} via {@link Collections#checkedSortedMap(SortedMap, Class, Class)}.
		 * 
		 * @see Collections#checkedSortedMap(SortedMap, Class, Class)
		 * @param keyType {@link Class} der Schlüssel.
		 * @param valueType {@link Class} der Werte.
		 * @return {@link Map}-{@link Builder}.
		 */
		@Override
		public MapBuilder3<GKey, GValue, SortedMap<GKey, GValue>> asCheckedMap(Class<GKey> keyType, Class<GValue> valueType);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, SortedMap<GKey, GValue>> asSynchronizedMap();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, SortedMap<GKey, GValue>> asUnmodifiableMap();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GMap build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link Map}-{@link Builder}, der eine konfigurierte {@link Map} in eine {@code checked}-, {@code synchronized}- oder
	 * {@code unmodifiable}-{@link Map} umwandeln kann.
	 * 
	 * @see Collections#checkedMap(Map, Class, Class)
	 * @see Collections#synchronizedMap(Map)
	 * @see Collections#unmodifiableMap(Map)
	 * @see Collections#checkedSortedMap(SortedMap, Class, Class)
	 * @see Collections#synchronizedSortedMap(SortedMap)
	 * @see Collections#unmodifiableSortedMap(SortedMap)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link Map}.
	 */
	public static interface MapBuilder3<GKey, GValue, GMap extends Map<GKey, GValue>> extends Builder<GMap> {

		/**
		 * Diese Methode konvertiert das konfigurierte {@link Map} via {@link Collections#checkedMap(Map, Class, Class)} bzw.
		 * {@link Collections#checkedSortedMap(SortedMap, Class, Class)}.
		 * 
		 * @see Collections#checkedMap(Map, Class, Class)
		 * @see Collections#checkedSortedMap(SortedMap, Class, Class)
		 * @param keyType {@link Class} der Schlüssel.
		 * @param valueType {@link Class} der Werte.
		 * @return {@link Map}-{@link Builder}.
		 */
		public MapBuilder3<GKey, GValue, GMap> asCheckedMap(Class<GKey> keyType, Class<GValue> valueType);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Map} via {@link Collections#synchronizedMap(Map)} bzw.
		 * {@link Collections#synchronizedSortedMap(SortedMap)}.
		 * 
		 * @see Collections#synchronizedMap(Map)
		 * @see Collections#synchronizedSortedMap(SortedMap)
		 * @return {@link Map}-{@link Builder}.
		 */
		public MapBuilder3<GKey, GValue, GMap> asSynchronizedMap();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link Map} via {@link Collections#unmodifiableMap(Map)} bzw.
		 * {@link Collections#unmodifiableSortedMap(SortedMap)}.
		 * 
		 * @see Collections#unmodifiableMap(Map)
		 * @see Collections#unmodifiableSortedMap(SortedMap)
		 * @return {@link Map}-{@link Builder}.
		 */
		public MapBuilder3<GKey, GValue, GMap> asUnmodifiableMap();

		/**
		 * Diese Methode gibt die konfigurierte {@link Map} zurück.
		 * 
		 * @return {@link Map}.
		 */
		@Override
		public GMap build() throws IllegalStateException;

	}

	/**
	 * Diese Klasse implementiert einen {@link MapBuilder1}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link Map}.
	 */
	static final class MapBuilderAImpl<GKey, GValue, GMap extends Map<GKey, GValue>> implements MapBuilder1<GKey, GValue, GMap> {

		/**
		 * Dieses Feld speichert die {@link Map}.
		 */
		final GMap map;

		/**
		 * Dieser Konstruktor initialisiert die {@link Map}.
		 * 
		 * @param map {@link Map}.
		 */
		public MapBuilderAImpl(final GMap map) {
			this.map = map;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> add(final GKey key, final GValue value) {
			this.map.put(key, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> add(final Entry<? extends GKey, ? extends GValue> entry) {
			return this.add(entry.getKey(), entry.getValue());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> addAll(final Entry<? extends GKey, ? extends GValue>... entries) {
			return this.addAll(Arrays.asList(entries));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> addAll(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			for(final Entry<? extends GKey, ? extends GValue> entry: entries){
				this.add(entry);
			}
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> addAll(final Map<? extends GKey, ? extends GValue> entries) {
			return this.addAll(entries.entrySet());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> remove(final GKey key) {
			this.map.remove(key);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> removeAll(final GKey... keys) {
			this.map.keySet().removeAll(Arrays.asList(keys));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, GMap> removeAll(final Iterable<? extends GKey> keys) {
			Iterables.removeAll(this.map.keySet(), keys);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder1<GKey, GValue, Map<GKey, GValue>> asCheckedMap(final Class<GKey> keyType, final Class<GValue> valueType) {
			return new MapBuilderAImpl<GKey, GValue, Map<GKey, GValue>>(Collections.checkedMap(this.map, keyType, valueType));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilderAImpl<GKey, GValue, Map<GKey, GValue>> asSynchronizedMap() {
			return new MapBuilderAImpl<GKey, GValue, Map<GKey, GValue>>(Collections.synchronizedMap(this.map));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilderAImpl<GKey, GValue, Map<GKey, GValue>> asUnmodifiableMap() {
			return new MapBuilderAImpl<GKey, GValue, Map<GKey, GValue>>(Collections.unmodifiableMap(this.map));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GMap build() {
			return this.map;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link MapBuilder2}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link Map}.
	 */
	static final class MapBuilderBImpl<GKey, GValue, GMap extends SortedMap<GKey, GValue>> implements MapBuilder2<GKey, GValue, GMap> {

		/**
		 * Dieses Feld speichert die {@link Map}.
		 */
		final GMap map;

		/**
		 * Dieser Konstruktor initialisiert die {@link Map}.
		 * 
		 * @param map {@link Map}.
		 */
		public MapBuilderBImpl(final GMap map) {
			this.map = map;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> add(final GKey key, final GValue value) {
			this.map.put(key, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> add(final Entry<? extends GKey, ? extends GValue> entry) {
			return this.add(entry.getKey(), entry.getValue());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> addAll(final Entry<? extends GKey, ? extends GValue>... entries) {
			return this.addAll(Arrays.asList(entries));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> addAll(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			for(final Entry<? extends GKey, ? extends GValue> entry: entries){
				this.add(entry);
			}
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> addAll(final Map<? extends GKey, ? extends GValue> entries) {
			return this.addAll(entries.entrySet());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> remove(final GKey key) {
			this.map.remove(key);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> removeAll(final GKey... keys) {
			this.map.keySet().removeAll(Arrays.asList(keys));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, GMap> removeAll(final Iterable<? extends GKey> keys) {
			Iterables.removeAll(this.map.keySet(), keys);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilder2<GKey, GValue, SortedMap<GKey, GValue>> asCheckedMap(final Class<GKey> keyType, final Class<GValue> valueType) {
			return new MapBuilderBImpl<GKey, GValue, SortedMap<GKey, GValue>>(Collections.checkedSortedMap(this.map, keyType, valueType));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilderBImpl<GKey, GValue, SortedMap<GKey, GValue>> asSynchronizedMap() {
			return new MapBuilderBImpl<GKey, GValue, SortedMap<GKey, GValue>>(Collections.synchronizedSortedMap(this.map));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MapBuilderBImpl<GKey, GValue, SortedMap<GKey, GValue>> asUnmodifiableMap() {
			return new MapBuilderBImpl<GKey, GValue, SortedMap<GKey, GValue>>(Collections.unmodifiableSortedMap(this.map));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GMap build() {
			return this.map;
		}

	}

	/**
	 * Diese Methode gibt einen neuen {@link MapBuilder} zurück.
	 * 
	 * @return {@link MapBuilder}.
	 */
	public static MapBuilder use() {
		return new MapBuilder();
	}

	/**
	 * Diese Methode gibt einen {@link Map}-{@link Builder} für das gegebene {@link Map} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link Map}.
	 * @param set {@link Map}.
	 * @return {@link Map}-{@link Builder}.
	 * @throws NullPointerException Wenn das gegebene {@link Map} {@code null} ist.
	 */
	public <GKey, GValue, GMap extends Map<GKey, GValue>> MapBuilder1<GKey, GValue, GMap> set(final GMap set) throws NullPointerException {
		if(set == null) throw new NullPointerException();
		return new MapBuilderAImpl<GKey, GValue, GMap>(set);
	}

	/**
	 * Diese Methode gibt einen {@link Map}-{@link Builder} für das gegebene {@link SortedMap} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GMap> Typ des {@link SortedMap}.
	 * @param set {@link SortedMap}.
	 * @return {@link SortedMap}-{@link Builder}.
	 * @throws NullPointerException Wenn das gegebene {@link SortedMap} {@code null} ist.
	 */
	public <GKey, GValue, GMap extends SortedMap<GKey, GValue>> MapBuilder2<GKey, GValue, GMap> sortedMap(final GMap set) throws NullPointerException {
		if(set == null) throw new NullPointerException();
		return new MapBuilderBImpl<GKey, GValue, GMap>(set);
	}

	/**
	 * Diese Methode einen neuen {@link TreeMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @return {@link TreeMap}-{@link Builder}.
	 */
	public <GKey, GValue extends Comparable<?>> MapBuilder2<GKey, GValue, TreeMap<GKey, GValue>> treeMap() {
		return this.sortedMap(new TreeMap<GKey, GValue>());
	}

	/**
	 * Diese Methode einen neuen {@link TreeMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param comparator {@link Comparator}.
	 * @return {@link TreeMap}-{@link Builder}.
	 */
	public <GKey, GValue> MapBuilder2<GKey, GValue, TreeMap<GKey, GValue>> treeMap(final Comparator<? super GKey> comparator) {
		return this.sortedMap(new TreeMap<GKey, GValue>(comparator));
	}

	/**
	 * Diese Methode einen neuen {@link HashMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @return {@link HashMap}-{@link Builder}.
	 */
	public <GKey, GValue> MapBuilder1<GKey, GValue, HashMap<GKey, GValue>> hashMap() {
		return this.set(new HashMap<GKey, GValue>());
	}

	/**
	 * Diese Methode einen neuen {@link HashMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @return {@link HashMap}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität negativ ist.
	 */
	public <GKey, GValue> MapBuilder1<GKey, GValue, HashMap<GKey, GValue>> hashMap(final int capacity) throws IllegalArgumentException {
		return this.set(new HashMap<GKey, GValue>(capacity));
	}

	/**
	 * Diese Methode einen neuen {@link HashMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @param factor Ladefaktor.
	 * @return {@link HashMap}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn eine der Eingaben negativ ist.
	 */
	public <GKey, GValue> MapBuilder1<GKey, GValue, HashMap<GKey, GValue>> hashMap(final int capacity, final float factor) throws IllegalArgumentException {
		return this.set(new HashMap<GKey, GValue>(capacity, factor));
	}

	/**
	 * Diese Methode einen neuen {@link LinkedHashMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @return {@link LinkedHashMap}-{@link Builder}.
	 */
	public <GKey, GValue> MapBuilder1<GKey, GValue, LinkedHashMap<GKey, GValue>> linkedHashMap() {
		return this.set(new LinkedHashMap<GKey, GValue>());
	}

	/**
	 * Diese Methode einen neuen {@link LinkedHashMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @return {@link LinkedHashMap}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität negativ ist.
	 */
	public <GKey, GValue> MapBuilder1<GKey, GValue, LinkedHashMap<GKey, GValue>> linkedHashMap(final int capacity) throws IllegalArgumentException {
		return this.set(new LinkedHashMap<GKey, GValue>(capacity));
	}

	/**
	 * Diese Methode einen neuen {@link LinkedHashMap}-{@link Builder} zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @param factor Ladefaktor.
	 * @return {@link LinkedHashMap}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn eine der Eingaben negativ ist.
	 */
	public <GKey, GValue> MapBuilder1<GKey, GValue, LinkedHashMap<GKey, GValue>> linkedHashMap(final int capacity, final float factor)
		throws IllegalArgumentException {
		return this.set(new LinkedHashMap<GKey, GValue>(capacity, factor));
	}

}