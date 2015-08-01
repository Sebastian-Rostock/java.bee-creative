package bee.creative.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert grundlegende {@link Builder}.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Builder} zur realisierung eines statischen Caches für Instanzen der exemplarischen Klasse
 * {@code Helper} verwendet:
 * 
 * <pre>
 * public final class Helper {
 * 
 *   static final {@literal Builder<Helper> CACHE = Builders.synchronizedBuilder(Builders.bufferedBuilder(new Builder<Helper>()} {
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
			if (value == null) return Iterators.emptyIterator();
			return Iterators.itemIterator(value);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.value);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine Sammlung von Elementen.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GItems> Typ der Sammlung.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseItemsBuilder<GItem, GItems extends Collection<GItem>, GThiz> extends BaseBuilder<GItems, GThiz> implements Iterable<GItem> {

		/**
		 * Dieses Feld speichert die Sammlung.
		 */
		protected GItems items;

		/**
		 * Dieser Konstruktor initialisiert die interne Sammlung.
		 * 
		 * @param items Sammlung.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		protected BaseItemsBuilder(final GItems items) throws NullPointerException {
			if (items == null) throw new NullPointerException("items = null");
			this.items = items;
		}

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @see #clearItems()
		 * @see #useItems(Iterable)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseItemsBuilder<? extends GItem, ?, ?> data) {
			if (data == null) return this.thiz();
			this.clearItems();
			return this.useItems(data);
		}

		/**
		 * Diese Methode fügt das gegebene Element zur {@link #getItems() internen Sammlung} hinzu und gibt {@code this} zurück.
		 * 
		 * @param item Element.
		 * @return {@code this}.
		 */
		public GThiz useItem(final GItem item) {
			this.items.add(item);
			return this.thiz();
		}

		/**
		 * Diese Methode fügt die gegebenen Elemente zur {@link #getItems() internen Sammlung} hinzu und gibt {@code this} zurück.
		 * 
		 * @param items Elemente oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useItems(final Iterable<? extends GItem> items) {
			if (items == null) return this.thiz();
			Iterables.appendAll(this.items, items);
			return this.thiz();
		}

		/**
		 * Diese Methode gibt die interne Sammlung zurück.
		 * 
		 * @return interne Sammlung.
		 */
		public GItems getItems() {
			return this.items;
		}

		/**
		 * Diese Methode leert die {@link #getItems() interne Sammlung} und gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		public GThiz clearItems() {
			this.items.clear();
			return this.thiz();
		}

		/**
		 * Diese Methode macht die {@link #getItems() interne Sammlung} datentypsicher und gibt {@code this} zurück.
		 * 
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @see java.util.Collections#checkedSortedSet(SortedSet, Class)
		 * @see java.util.Collections#checkedList(List, Class)
		 * @see java.util.Collections#checkedCollection(Collection, Class)
		 * @param clazz Klasse der Elemente.
		 * @return {@code this}.
		 */
		public abstract GThiz makeChecked(Class<GItem> clazz);

		/**
		 * Diese Methode macht die {@link #getItems() interne Sammlung} threadsicher und gibt {@code this} zurück.
		 * 
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @see java.util.Collections#synchronizedSortedSet(SortedSet)
		 * @see java.util.Collections#synchronizedList(List)
		 * @see java.util.Collections#synchronizedCollection(Collection)
		 * @return {@code this}.
		 */
		public abstract GThiz makeSynchronized();

		/**
		 * Diese Methode macht die {@link #getItems() interne Sammlung} unveränderlich und gibt {@code this} zurück.
		 * 
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @see java.util.Collections#unmodifiableSortedSet(SortedSet)
		 * @see java.util.Collections#unmodifiableList(List)
		 * @see java.util.Collections#unmodifiableCollection(Collection)
		 * @return {@code this}.
		 */
		public abstract GThiz makeUnmodifiable();

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItems build() throws IllegalStateException {
			return this.items;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return this.items.iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.items);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GEntries> Typ der {@link Map}.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseEntriesBuilder<GKey, GValue, GEntries extends Map<GKey, GValue>, GThiz> extends BaseBuilder<GEntries, GThiz> implements
		Iterable<Entry<GKey, GValue>> {

		/**
		 * Dieses Feld speichert den über {@link #forKey(Object)} gewählten Schlüssel.
		 */
		protected GKey key;

		/**
		 * Dieses Feld speichert die interne Abbildung.
		 */
		protected GEntries entries;

		/**
		 * Dieser Konstruktor initialisiert die interne {@link Map}.
		 * 
		 * @param entries interne {@link Map}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist.
		 */
		protected BaseEntriesBuilder(final GEntries entries) throws NullPointerException {
			if (entries == null) throw new NullPointerException("entries = null");
			this.entries = entries;
		}

		{}

		/**
		 * Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 * 
		 * @see #clearEntries()
		 * @see #useEntries(Iterable)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz use(final BaseEntriesBuilder<? extends GKey, ? extends GValue, ?, ?> data) {
			if (data == null) return this.thiz();
			this.clearEntries();
			return this.useEntries(data.getEntries());
		}

		/**
		 * Diese Methode fügt den gegebenen Eintrag zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 * 
		 * @see #useEntry(Object, Object)
		 * @param entry Eintrag.
		 * @return {@code this}.
		 */
		public GThiz useEntry(final Entry<? extends GKey, ? extends GValue> entry) {
			if (entry == null) return this.thiz();
			return this.useEntry(entry.getKey(), entry.getValue());
		}

		/**
		 * Diese Methode fügt den gegebenen Eintrag zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 * 
		 * @see Map#put(Object, Object)
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @return {@code this}.
		 */
		public GThiz useEntry(final GKey key, final GValue value) {
			this.entries.put(key, value);
			return this.thiz();
		}

		/**
		 * Diese Methode fügt die gegebenen Einträge zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 * 
		 * @see #useEntries(Iterable)
		 * @param entries Einträge oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useEntries(final Map<? extends GKey, ? extends GValue> entries) {
			if (entries == null) return this.thiz();
			return this.useEntries(entries.entrySet());
		}

		/**
		 * Diese Methode fügt die gegebenen Einträge zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 * 
		 * @see #useEntry(Entry)
		 * @param entries Einträge oder {@code null}.
		 * @return {@code this}.
		 */
		public GThiz useEntries(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			if (entries == null) return this.thiz();
			for (final Entry<? extends GKey, ? extends GValue> entry: entries) {
				this.useEntry(entry);
			}
			return this.thiz();
		}

		/**
		 * Diese Methode gibt die interne {@link Map} zurück.
		 * 
		 * @see BaseEntriesBuilder#BaseEntriesBuilder(Map)
		 * @return interne Abbildung.
		 */
		public GEntries getEntries() {
			return this.entries;
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
			return this.entries.get(this.key);
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
			this.useEntry(this.key, value);
			return this.thiz();
		}

		/**
		 * Diese Methode leert die {@link #getEntries() interne Abbildung} und gibt {@code this} zurück.
		 * 
		 * @return {@code this}.
		 */
		public GThiz clearEntries() {
			this.entries.clear();
			return this.thiz();
		}

		/**
		 * Diese Methode macht die {@link #getEntries() interne Abbildung} datentypsicher und gibt {@code this} zurück.
		 * 
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @see java.util.Collections#checkedSortedMap(SortedMap, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return {@code this}.
		 */
		public abstract GThiz makeChecked(Class<GKey> keyClazz, Class<GValue> valueClazz);

		/**
		 * Diese Methode macht die {@link #getEntries() interne Abbildung} threadsicher und gibt {@code this} zurück.
		 * 
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
		 * @return {@code this}.
		 */
		public abstract GThiz makeSynchronized();

		/**
		 * Diese Methode macht die {@link #getEntries() interne Abbildung} unveränderlich und gibt {@code this} zurück.
		 * 
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @see java.util.Collections#unmodifiableSortedMap(SortedMap)
		 * @return {@code this}.
		 */
		public abstract GThiz makeUnmodifiable();

		{}

		/**
		 * {@inheritDoc}
		 * 
		 * @see #getEntries()
		 */
		@Override
		public GEntries build() throws IllegalStateException {
			return this.getEntries();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Entry<GKey, GValue>> iterator() {
			return this.entries.entrySet().iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toString(true, this.entries);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link Set}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseSetBuilder<GItem, GThiz> extends BaseItemsBuilder<GItem, Set<GItem>, GThiz> {

		/**
		 * Dieser Konstruktor initialisiert das interne {@link HashSet}.
		 */
		public BaseSetBuilder() {
			super(new HashSet<GItem>());
		}

		/**
		 * Dieser Konstruktor initialisiert das interne {@link Set}.
		 * 
		 * @param items {@link Set}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public BaseSetBuilder(final Set<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedSet(this.items, clazz);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeSynchronized() {
			this.items = Collections.synchronizedSet(this.items);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeUnmodifiable() {
			this.items = Collections.unmodifiableSet(this.items);
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link List}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseListBuilder<GItem, GThiz> extends BaseItemsBuilder<GItem, List<GItem>, GThiz> {

		/**
		 * Dieser Konstruktor initialisiert die interne {@link ArrayList}.
		 */
		public BaseListBuilder() {
			super(new ArrayList<GItem>());
		}

		/**
		 * Dieser Konstruktor initialisiert die interne {@link List}.
		 * 
		 * @param items {@link List}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public BaseListBuilder(final List<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedList(this.items, clazz);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeSynchronized() {
			this.items = Collections.synchronizedList(this.items);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeUnmodifiable() {
			this.items = Collections.unmodifiableList(this.items);
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseMapBuilder<GKey, GValue, GThiz> extends BaseEntriesBuilder<GKey, GValue, Map<GKey, GValue>, GThiz> {

		/**
		 * Dieser Konstruktor initialisiert die interne Abbildung mit einer neuen {@link HashMap}.
		 */
		public BaseMapBuilder() {
			super(new HashMap<GKey, GValue>());
		}

		/**
		 * Dieser Konstruktor initialisiert die interne Abbildung.
		 * 
		 * @param entries interne Abbildung.
		 * @throws NullPointerException Wenn {@code entryMap} {@code null} ist.
		 */
		public BaseMapBuilder(final Map<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			this.entries = Collections.checkedMap(this.entries, keyClazz, valueClazz);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeSynchronized() {
			this.entries = Collections.synchronizedMap(this.entries);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeUnmodifiable() {
			this.entries = Collections.unmodifiableMap(this.entries);
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Collection}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseCollectionBuilder<GItem, GThiz> extends BaseItemsBuilder<GItem, Collection<GItem>, GThiz> {

		/**
		 * Dieser Konstruktor initialisiert die interne {@link Collection}.
		 * 
		 * @param items {@link Collection}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public BaseCollectionBuilder(final Collection<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedCollection(this.items, clazz);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeSynchronized() {
			this.items = Collections.synchronizedCollection(this.items);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeUnmodifiable() {
			this.items = Collections.unmodifiableCollection(this.items);
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link SortedSet}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseSortedSetBuilder<GItem, GThiz> extends BaseItemsBuilder<GItem, SortedSet<GItem>, GThiz> {

		/**
		 * Dieser Konstruktor initialisiert das interne {@link TreeSet}.
		 */
		public BaseSortedSetBuilder() {
			super(new TreeSet<GItem>());
		}

		/**
		 * Dieser Konstruktor initialisiert das interne {@link SortedSet}.
		 * 
		 * @param items {@link SortedSet}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public BaseSortedSetBuilder(final SortedSet<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedSortedSet(this.items, clazz);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeSynchronized() {
			this.items = Collections.synchronizedSortedSet(this.items);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeUnmodifiable() {
			this.items = Collections.unmodifiableSortedSet(this.items);
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link SortedMap}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThiz> Typ des konkreten Nachfahren dieser Klasse.
	 */
	public static abstract class BaseSortedMapBuilder<GKey, GValue, GThiz> extends BaseEntriesBuilder<GKey, GValue, SortedMap<GKey, GValue>, GThiz> {

		/**
		 * Dieser Konstruktor initialisiert die interne Abbildung mit einer neuen {@link TreeMap}.
		 */
		public BaseSortedMapBuilder() {
			super(new TreeMap<GKey, GValue>());
		}

		/**
		 * Dieser Konstruktor initialisiert die interne Abbildung.
		 * 
		 * @param entries interne Abbildung.
		 * @throws NullPointerException Wenn {@code entrySortedMap} {@code null} ist.
		 */
		public BaseSortedMapBuilder(final SortedMap<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			this.entries = Collections.checkedSortedMap(this.entries, keyClazz, valueClazz);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeSynchronized() {
			this.entries = Collections.synchronizedSortedMap(this.entries);
			return this.thiz();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GThiz makeUnmodifiable() {
			this.entries = Collections.unmodifiableSortedMap(this.entries);
			return this.thiz();
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator eines {@link TreeSet}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class TreeSetBuilder<GItem> extends BaseSortedSetBuilder<GItem, TreeSetBuilder<GItem>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected TreeSetBuilder<GItem> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator einer {@link TreeMap}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 */
	public static final class TreeMapBuilder<GKey, GValue> extends BaseSortedMapBuilder<GKey, GValue, TreeMapBuilder<GKey, GValue>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected TreeMapBuilder<GKey, GValue> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator eines {@link HashSet}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class HashSetBuilder<GItem> extends BaseSetBuilder<GItem, HashSetBuilder<GItem>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected HashSetBuilder<GItem> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator einer {@link HashMap}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 */
	public static final class HashMapBuilder<GKey, GValue> extends BaseMapBuilder<GKey, GValue, HashMapBuilder<GKey, GValue>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected HashMapBuilder<GKey, GValue> thiz() {
			return this;
		}

	}

	/**
	 * Diese Klasse implementiert den Konfigurator einer {@link ArrayList}.
	 * 
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class ArrayListBuilder<GItem> extends BaseListBuilder<GItem, ArrayListBuilder<GItem>> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected ArrayListBuilder<GItem> thiz() {
			return this;
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
	 * @see #bufferedBuilder(int, Builder)
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@code buffered}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist.
	 */
	public static <GValue> Builder<GValue> bufferedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		return Builders.bufferedBuilder(Pointers.SOFT, builder);
	}

	/**
	 * Diese Methode gibt einen gepufferten {@link Builder} zurück, der den vonm gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer} im
	 * gegebenenen Modus verwaltet.
	 * 
	 * @param <GValue> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param builder {@link Builder}.
	 * @return {@code buffered}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code mode} ungültig ist.
	 */
	public static <GValue> Builder<GValue> bufferedBuilder(final int mode, final Builder<? extends GValue> builder) throws NullPointerException,
		IllegalArgumentException {
		if (builder == null) throw new NullPointerException("builder = null");
		Pointers.pointer(mode, null);
		return new Builder<GValue>() {

			Pointer<GValue> pointer;

			@Override
			public GValue build() throws IllegalStateException {
				final Pointer<GValue> pointer = this.pointer;
				if (pointer != null) {
					final GValue data = pointer.data();
					if (data != null) return data;
					if (pointer == Pointers.NULL_POINTER) return null;
				}
				final GValue data = builder.build();
				this.pointer = Pointers.pointer(mode, data);
				return data;
			}

			@Override
			public String toString() {
				return Objects.toStringCall("bufferedBuilder", mode, builder);
			}

		};
	}

	/**
	 * Diese Methode gibt einen umgewandelten {@link Builder} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Converter} aus dem Datensatz des gegebenen
	 * {@link Builder} ermittelt wird.
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
		if (converter == null) throw new NullPointerException("converter = null");
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
	 * Diese Methode gibt einen synchronisierten {@link Builder} zurück, der den gegebenen {@link Builder} via {@code synchronized(this)} synchronisiert.
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
