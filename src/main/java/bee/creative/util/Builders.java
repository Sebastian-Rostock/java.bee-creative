package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import bee.creative.util.Pointers.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Builder}.
 * <p>
 * Im nachfolgenden Beispiel wird ein gepufferter {@link Builder} zur realisierung eines statischen Caches für Instanzen der exemplarischen Klasse
 * {@code Helper} verwendet: <pre>
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
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Builders {

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Erzeugung eines Datensatzes.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Datensatzes.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseBuilder<GValue, GThis> implements Builder<GValue> {

		/** Diese Methode gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		protected abstract GThis customThis();

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ des Werts.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseValueBuilder<GValue, GThis> extends BaseBuilder<GValue, GThis> implements Iterable<GValue> {

		/** Dieses Feld speichert den Wert. */
		protected GValue value;

		/** Dieser Konstruktor initialisiert den Wert mit {@code null}. */
		public BaseValueBuilder() {
		}

		{}

		/** Diese Methode gibt den Wert zurück.
		 *
		 * @see #use(Object)
		 * @return Wert. */
		public final GValue get() {
			return this.value;
		}

		/** Diese Methode setzt den Wert und gibt {@code this} zurück.
		 *
		 * @see #get()
		 * @param value Wert.
		 * @return {@code this}. */
		public final GThis use(final GValue value) {
			this.value = value;
			return this.customThis();
		}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #use(Object)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}. */
		public final GThis use(final BaseValueBuilder<? extends GValue, ?> data) {
			if (data == null) return this.customThis();
			return this.use(data.value);
		}

		/** Diese Methode setzt den Wert auf {@code null} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public final GThis clear() {
			this.value = null;
			return this.customThis();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GValue build() throws IllegalStateException {
			return this.value;
		}

		/** {@inheritDoc} */
		@Override
		public final Iterator<GValue> iterator() {
			final GValue value = this.value;
			if (value == null) return Iterators.emptyIterator();
			return Iterators.itemIterator(value);
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.toString(true, this.value);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine Sammlung von Elementen.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GItems> Typ der Sammlung.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseItemsBuilder<GItem, GItems extends Collection<GItem>, GThis> extends BaseBuilder<GItems, GThis> implements Iterable<GItem> {

		/** Dieses Feld speichert die Sammlung. */
		protected GItems items;

		/** Dieser Konstruktor initialisiert die interne Sammlung.
		 *
		 * @param items Sammlung.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		protected BaseItemsBuilder(final GItems items) throws NullPointerException {
			if (items == null) throw new NullPointerException("items = null");
			this.items = items;
		}

		{}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #clearItems()
		 * @see #useItems(Iterable)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}. */
		public final GThis use(final BaseItemsBuilder<? extends GItem, ?, ?> data) {
			if (data == null) return this.customThis();
			this.clearItems();
			return this.useItems(data);
		}

		/** Diese Methode fügt das gegebene Element zur {@link #getItems() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param item Element.
		 * @return {@code this}. */
		public final GThis useItem(final GItem item) {
			this.items.add(item);
			return this.customThis();
		}

		/** Diese Methode fügt die gegebenen Elemente zur {@link #getItems() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente oder {@code null}.
		 * @return {@code this}. */
		public final GThis useItems(final Iterable<? extends GItem> items) {
			if (items == null) return this.customThis();
			Iterables.appendAll(this.items, items);
			return this.customThis();
		}

		/** Diese Methode gibt die interne Sammlung zurück.
		 *
		 * @return interne Sammlung. */
		public final GItems getItems() {
			return this.items;
		}

		/** Diese Methode leert die {@link #getItems() interne Sammlung} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public final GThis clearItems() {
			this.items.clear();
			return this.customThis();
		}

		/** Diese Methode macht die {@link #getItems() interne Sammlung} datentypsicher und gibt {@code this} zurück.
		 *
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @see java.util.Collections#checkedSortedSet(SortedSet, Class)
		 * @see java.util.Collections#checkedList(List, Class)
		 * @see java.util.Collections#checkedCollection(Collection, Class)
		 * @param clazz Klasse der Elemente.
		 * @return {@code this}. */
		protected abstract GThis makeChecked(Class<GItem> clazz);

		/** Diese Methode macht die {@link #getItems() interne Sammlung} threadsicher und gibt {@code this} zurück.
		 *
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @see java.util.Collections#synchronizedSortedSet(SortedSet)
		 * @see java.util.Collections#synchronizedList(List)
		 * @see java.util.Collections#synchronizedCollection(Collection)
		 * @return {@code this}. */
		protected abstract GThis makeSynchronized();

		/** Diese Methode macht die {@link #getItems() interne Sammlung} unveränderlich und gibt {@code this} zurück.
		 *
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @see java.util.Collections#unmodifiableSortedSet(SortedSet)
		 * @see java.util.Collections#unmodifiableList(List)
		 * @see java.util.Collections#unmodifiableCollection(Collection)
		 * @return {@code this}. */
		protected abstract GThis makeUnmodifiable();

		{}

		/** {@inheritDoc} */
		@Override
		public final GItems build() throws IllegalStateException {
			return this.items;
		}

		/** {@inheritDoc} */
		@Override
		public final Iterator<GItem> iterator() {
			return this.items.iterator();
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.toString(true, this.items);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GEntries> Typ der {@link Map}.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseEntriesBuilder<GKey, GValue, GEntries extends Map<GKey, GValue>, GThis> extends BaseBuilder<GEntries, GThis>
		implements Iterable<Entry<GKey, GValue>> {

		/** Dieses Feld speichert den über {@link #forKey(Object)} gewählten Schlüssel. */
		protected GKey key;

		/** Dieses Feld speichert die interne {@link Map}. */
		protected GEntries entries;

		/** Dieser Konstruktor initialisiert die interne {@link Map}.
		 *
		 * @param entries interne {@link Map}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		protected BaseEntriesBuilder(final GEntries entries) throws NullPointerException {
			if (entries == null) throw new NullPointerException("entries = null");
			this.entries = entries;
		}

		{}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #clearEntries()
		 * @see #useEntries(Iterable)
		 * @param data Konfigurator oder {@code null}.
		 * @return {@code this}. */
		public final GThis use(final BaseEntriesBuilder<? extends GKey, ? extends GValue, ?, ?> data) {
			if (data == null) return this.customThis();
			this.clearEntries();
			return this.useEntries(data.getEntries());
		}

		/** Diese Methode fügt den gegebenen Eintrag zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 *
		 * @see #useEntry(Object, Object)
		 * @param entry Eintrag.
		 * @return {@code this}. */
		public final GThis useEntry(final Entry<? extends GKey, ? extends GValue> entry) {
			if (entry == null) return this.customThis();
			return this.useEntry(entry.getKey(), entry.getValue());
		}

		/** Diese Methode fügt den gegebenen Eintrag zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 *
		 * @see Map#put(Object, Object)
		 * @param key Schlüssel.
		 * @param value Wert.
		 * @return {@code this}. */
		public final GThis useEntry(final GKey key, final GValue value) {
			this.entries.put(key, value);
			return this.customThis();
		}

		/** Diese Methode fügt die gegebenen Einträge zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 *
		 * @see #useEntries(Iterable)
		 * @param entries Einträge oder {@code null}.
		 * @return {@code this}. */
		public final GThis useEntries(final Map<? extends GKey, ? extends GValue> entries) {
			if (entries == null) return this.customThis();
			return this.useEntries(entries.entrySet());
		}

		/** Diese Methode fügt die gegebenen Einträge zur {@link #getEntries() internen Abbildung} hinzu und gibt {@code this} zurück.
		 *
		 * @see #useEntry(Entry)
		 * @param entries Einträge oder {@code null}.
		 * @return {@code this}. */
		public final GThis useEntries(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			if (entries == null) return this.customThis();
			for (final Entry<? extends GKey, ? extends GValue> entry: entries) {
				this.useEntry(entry);
			}
			return this.customThis();
		}

		/** Diese Methode gibt die interne {@link Map} zurück.
		 *
		 * @see BaseEntriesBuilder#BaseEntriesBuilder(Map)
		 * @return interne Abbildung. */
		public final GEntries getEntries() {
			return this.entries;
		}

		/** Diese Methode wählt den gegebenen Schlüssel und gibt {@code this} zurück. Dieser Schlüssel wird in den nachfolgenden Aufrufen von {@link #getValue()}
		 * und {@link #useValue(Object)} verwendet.
		 *
		 * @see #getValue()
		 * @see #useValue(Object)
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public final GThis forKey(final GKey key) {
			this.key = key;
			return this.customThis();
		}

		/** Diese Methode gibt den Wert zum {@link #forKey(Object) gewählten Schlüssel} zurück.
		 *
		 * @see #forKey(Object)
		 * @see #useValue(Object)
		 * @return Wert zum gewählten Schlüssel. */
		public final GValue getValue() {
			return this.entries.get(this.key);
		}

		/** Diese Methode setzt den Wert zum {@link #forKey(Object) gewählten Schlüssel} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see #getValue()
		 * @param value Wert.
		 * @return {@code this}. */
		public final GThis useValue(final GValue value) {
			this.useEntry(this.key, value);
			return this.customThis();
		}

		/** Diese Methode leert die {@link #getEntries() interne Abbildung} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public final GThis clearEntries() {
			this.entries.clear();
			return this.customThis();
		}

		/** Diese Methode macht die {@link #getEntries() interne Abbildung} datentypsicher und gibt {@code this} zurück.
		 *
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @see java.util.Collections#checkedSortedMap(SortedMap, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return {@code this}. */
		protected abstract GThis makeChecked(Class<GKey> keyClazz, Class<GValue> valueClazz);

		/** Diese Methode macht die {@link #getEntries() interne Abbildung} threadsicher und gibt {@code this} zurück.
		 *
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
		 * @return {@code this}. */
		protected abstract GThis makeSynchronized();

		/** Diese Methode macht die {@link #getEntries() interne Abbildung} unveränderlich und gibt {@code this} zurück.
		 *
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @see java.util.Collections#unmodifiableSortedMap(SortedMap)
		 * @return {@code this}. */
		protected abstract GThis makeUnmodifiable();

		{}

		/** {@inheritDoc}
		 *
		 * @see #getEntries() */
		@Override
		public final GEntries build() throws IllegalStateException {
			return this.getEntries();
		}

		/** {@inheritDoc} */
		@Override
		public final Iterator<Entry<GKey, GValue>> iterator() {
			return this.entries.entrySet().iterator();
		}

		/** {@inheritDoc} */
		@Override
		public final String toString() {
			return Objects.toString(true, this.entries);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link Set}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSetBuilder<GItem, GThis> extends BaseItemsBuilder<GItem, Set<GItem>, GThis> {

		/** Dieser Konstruktor initialisiert das interne {@link HashSet}. */
		public BaseSetBuilder() {
			super(new HashSet<GItem>());
		}

		/** Dieser Konstruktor initialisiert das interne {@link Set}.
		 *
		 * @param items {@link Set}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseSetBuilder(final Set<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected GThis makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedSet(this.items, clazz);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeSynchronized() {
			this.items = Collections.synchronizedSet(this.items);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeUnmodifiable() {
			this.items = Collections.unmodifiableSet(this.items);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen {@link BaseSetBuilder} mit sichtbaren Umwandlungsmethoden {@link #makeChecked(Class)}, {@link #makeSynchronized()} und
	 * {@link #makeUnmodifiable()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSetBuilder2<GItem, GThis> extends BaseSetBuilder<GItem, GThis> {

		/** Dieser Konstruktor initialisiert das interne {@link Set}.
		 *
		 * @param items {@link Set}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseSetBuilder2(final Set<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GThis makeChecked(final Class<GItem> clazz) {
			return super.makeChecked(clazz);
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeSynchronized() {
			return super.makeSynchronized();
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeUnmodifiable() {
			return super.makeUnmodifiable();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link List}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseListBuilder<GItem, GThis> extends BaseItemsBuilder<GItem, List<GItem>, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link List}.
		 *
		 * @param items {@link List}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseListBuilder(final List<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected GThis makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedList(this.items, clazz);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeSynchronized() {
			this.items = Collections.synchronizedList(this.items);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeUnmodifiable() {
			this.items = Collections.unmodifiableList(this.items);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen {@link BaseListBuilder} mit sichtbaren Umwandlungsmethoden {@link #makeChecked(Class)}, {@link #makeSynchronized()} und
	 * {@link #makeUnmodifiable()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseListBuilder2<GItem, GThis> extends BaseListBuilder<GItem, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link List}.
		 *
		 * @param items {@link List}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseListBuilder2(final List<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GThis makeChecked(final Class<GItem> clazz) {
			return super.makeChecked(clazz);
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeSynchronized() {
			this.items = Collections.synchronizedList(this.items);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeUnmodifiable() {
			this.items = Collections.unmodifiableList(this.items);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseMapBuilder<GKey, GValue, GThis> extends BaseEntriesBuilder<GKey, GValue, Map<GKey, GValue>, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link HashMap}. */
		public BaseMapBuilder() {
			super(new HashMap<GKey, GValue>());
		}

		/** Dieser Konstruktor initialisiert die interne Abbildung.
		 *
		 * @param entries interne Abbildung.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public BaseMapBuilder(final Map<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected GThis makeChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			this.entries = Collections.checkedMap(this.entries, keyClazz, valueClazz);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeSynchronized() {
			this.entries = Collections.synchronizedMap(this.entries);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeUnmodifiable() {
			this.entries = Collections.unmodifiableMap(this.entries);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen {@link BaseMapBuilder} mit sichtbaren Umwandlungsmethoden {@link #makeChecked(Class, Class)}, {@link #makeSynchronized()}
	 * und {@link #makeUnmodifiable()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseMapBuilder2<GKey, GValue, GThis> extends BaseMapBuilder<GKey, GValue, GThis> {

		/** Dieser Konstruktor initialisiert die interne Abbildung.
		 *
		 * @param entries interne Abbildung.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public BaseMapBuilder2(final Map<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GThis makeChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			return super.makeChecked(keyClazz, valueClazz);
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeSynchronized() {
			return super.makeSynchronized();
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeUnmodifiable() {
			return super.makeUnmodifiable();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Collection}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseCollectionBuilder<GItem, GThis> extends BaseItemsBuilder<GItem, Collection<GItem>, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link Collection}.
		 *
		 * @param items {@link Collection}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseCollectionBuilder(final Collection<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected GThis makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedCollection(this.items, clazz);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeSynchronized() {
			this.items = Collections.synchronizedCollection(this.items);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeUnmodifiable() {
			this.items = Collections.unmodifiableCollection(this.items);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen {@link BaseCollectionBuilder} mit sichtbaren Umwandlungsmethoden {@link #makeChecked(Class)}, {@link #makeSynchronized()}
	 * und {@link #makeUnmodifiable()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseCollectionBuilder2<GItem, GThis> extends BaseCollectionBuilder<GItem, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link Collection}.
		 *
		 * @param items {@link Collection}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseCollectionBuilder2(final Collection<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GThis makeChecked(final Class<GItem> clazz) {
			return super.makeChecked(clazz);
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeSynchronized() {
			return super.makeSynchronized();
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeUnmodifiable() {
			return super.makeUnmodifiable();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link SortedSet}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSortedSetBuilder<GItem, GThis> extends BaseItemsBuilder<GItem, SortedSet<GItem>, GThis> {

		/** Dieser Konstruktor initialisiert das interne {@link TreeSet}. */
		public BaseSortedSetBuilder() {
			super(new TreeSet<GItem>());
		}

		/** Dieser Konstruktor initialisiert das interne {@link SortedSet}.
		 *
		 * @param items {@link SortedSet}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseSortedSetBuilder(final SortedSet<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected GThis makeChecked(final Class<GItem> clazz) {
			this.items = Collections.checkedSortedSet(this.items, clazz);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeSynchronized() {
			this.items = Collections.synchronizedSortedSet(this.items);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeUnmodifiable() {
			this.items = Collections.unmodifiableSortedSet(this.items);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen {@link BaseSortedSetBuilder} mit sichtbaren Umwandlungsmethoden {@link #makeChecked(Class)}, {@link #makeSynchronized()}
	 * und {@link #makeUnmodifiable()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSortedSetBuilder2<GItem, GThis> extends BaseSortedSetBuilder<GItem, GThis> {

		/** Dieser Konstruktor initialisiert das interne {@link SortedSet}.
		 *
		 * @param items {@link SortedSet}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public BaseSortedSetBuilder2(final SortedSet<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GThis makeChecked(final Class<GItem> clazz) {
			return super.makeChecked(clazz);
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeSynchronized() {
			return super.makeSynchronized();
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeUnmodifiable() {
			return super.makeUnmodifiable();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link SortedMap}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSortedMapBuilder<GKey, GValue, GThis> extends BaseEntriesBuilder<GKey, GValue, SortedMap<GKey, GValue>, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link SortedMap}.
		 *
		 * @param entries interne {@link SortedMap}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public BaseSortedMapBuilder(final SortedMap<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected GThis makeChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			this.entries = Collections.checkedSortedMap(this.entries, keyClazz, valueClazz);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeSynchronized() {
			this.entries = Collections.synchronizedSortedMap(this.entries);
			return this.customThis();
		}

		/** {@inheritDoc} */
		@Override
		protected GThis makeUnmodifiable() {
			this.entries = Collections.unmodifiableSortedMap(this.entries);
			return this.customThis();
		}

	}

	/** Diese Klasse implementiert einen {@link BaseMapBuilder} mit sichtbaren Umwandlungsmethoden {@link #makeChecked(Class, Class)}, {@link #makeSynchronized()}
	 * und {@link #makeUnmodifiable()}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSortedMapBuilder2<GKey, GValue, GThis> extends BaseSortedMapBuilder<GKey, GValue, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link SortedMap}.
		 *
		 * @param entries interne Abbildung.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public BaseSortedMapBuilder2(final SortedMap<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final GThis makeChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			return super.makeChecked(keyClazz, valueClazz);
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeSynchronized() {
			return super.makeSynchronized();
		}

		/** {@inheritDoc} */
		@Override
		public final GThis makeUnmodifiable() {
			return super.makeUnmodifiable();
		}

	}

	/** Diese Klasse implementiert den Konfigurator eines {@link TreeSet}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class TreeSetBuilder<GItem> extends BaseSortedSetBuilder2<GItem, TreeSetBuilder<GItem>> {

		/** Dieser Konstruktor initialisiert das interne {@link TreeSet}. */
		public TreeSetBuilder() {
			super(new TreeSet<GItem>());
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final TreeSetBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator einer {@link TreeMap}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung. */
	public static final class TreeMapBuilder<GKey, GValue> extends BaseSortedMapBuilder2<GKey, GValue, TreeMapBuilder<GKey, GValue>> {

		/** Dieser Konstruktor initialisiert die interne {@link TreeMap}. */
		public TreeMapBuilder() {
			super(new TreeMap<GKey, GValue>());
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final TreeMapBuilder<GKey, GValue> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator eines {@link HashSet}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class HashSetBuilder<GItem> extends BaseSetBuilder2<GItem, HashSetBuilder<GItem>> {

		/** Dieser Konstruktor initialisiert das interne {@link HashSet}. */
		public HashSetBuilder() {
			super(new HashSet<GItem>());
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final HashSetBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator einer {@link HashMap}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung. */
	public static final class HashMapBuilder<GKey, GValue> extends BaseMapBuilder2<GKey, GValue, HashMapBuilder<GKey, GValue>> {

		/** Dieser Konstruktor initialisiert die interne {@link HashMap}. */
		public HashMapBuilder() {
			super(new HashMap<GKey, GValue>());
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final HashMapBuilder<GKey, GValue> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator einer {@link LinkedList}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class LinkedListBuilder<GItem> extends BaseListBuilder2<GItem, LinkedListBuilder<GItem>> {

		/** Dieser Konstruktor initialisiert die interne {@link LinkedList}. */
		public LinkedListBuilder() {
			super(new LinkedList<GItem>());
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final LinkedListBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert den Konfigurator einer {@link ArrayList}.
	 *
	 * @author [cc-by] 2015 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class ArrayListBuilder<GItem> extends BaseListBuilder2<GItem, ArrayListBuilder<GItem>> {

		/** Dieser Konstruktor initialisiert die interne {@link ArrayList}. */
		public ArrayListBuilder() {
			super(new ArrayList<GItem>());
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final ArrayListBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class SimpleSetBuilder<GItem> extends BaseSetBuilder2<GItem, SimpleSetBuilder<GItem>> {

		/** Diese Methode gibt einen neuen {@link SimpleSetBuilder} zum gegebenen {@link Set} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param items {@link Set}.
		 * @return {@link SimpleSetBuilder}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public static <GItem> SimpleSetBuilder<GItem> from(final Set<GItem> items) throws NullPointerException {
			return new SimpleSetBuilder<>(items);
		}

		{}

		/** Dieser Konstruktor initialisiert das {@link Set}.
		 *
		 * @param items {@link Set}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public SimpleSetBuilder(final Set<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SimpleSetBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class SimpleListBuilder<GItem> extends BaseListBuilder2<GItem, SimpleListBuilder<GItem>> {

		/** Diese Methode gibt einen neuen {@link SimpleListBuilder} zur gegebenen {@link List} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param items {@link List}.
		 * @return {@link SimpleListBuilder}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public static <GItem> SimpleListBuilder<GItem> from(final List<GItem> items) throws NullPointerException {
			return new SimpleListBuilder<>(items);
		}

		{}

		/** Dieser Konstruktor initialisiert die {@link List}.
		 *
		 * @param items {@link List}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public SimpleListBuilder(final List<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SimpleListBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Map}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung. */
	public static final class SimpleMapBuilder<GKey, GValue> extends BaseMapBuilder2<GKey, GValue, SimpleMapBuilder<GKey, GValue>> {

		/** Diese Methode gibt einen neuen {@link SimpleMapBuilder} zum gegebenen {@link Map} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel in der Abbildung.
		 * @param <GValue> Typ der Werte in der Abbildung.
		 * @param entries {@link Map}.
		 * @return {@link SimpleMapBuilder}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public static <GKey, GValue> SimpleMapBuilder<GKey, GValue> from(final Map<GKey, GValue> entries) throws NullPointerException {
			return new SimpleMapBuilder<>(entries);
		}

		{}

		/** Dieser Konstruktor initialisiert das {@link Map}.
		 *
		 * @param entries {@link Map}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public SimpleMapBuilder(final Map<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SimpleMapBuilder<GKey, GValue> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Collection}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class SimpleCollectionBuilder<GItem> extends BaseCollectionBuilder2<GItem, SimpleCollectionBuilder<GItem>> {

		/** Diese Methode gibt einen neuen {@link SimpleCollectionBuilder} zur gegebenen {@link Collection} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param items {@link Collection}.
		 * @return {@link SimpleCollectionBuilder}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public static <GItem> SimpleCollectionBuilder<GItem> from(final Collection<GItem> items) throws NullPointerException {
			return new SimpleCollectionBuilder<>(items);
		}

		{}

		/** Dieser Konstruktor initialisiert die {@link Collection}.
		 *
		 * @param items {@link Collection}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public SimpleCollectionBuilder(final Collection<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SimpleCollectionBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link SortedSet}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente. */
	public static final class SimpleSortedSetBuilder<GItem> extends BaseSortedSetBuilder2<GItem, SimpleSortedSetBuilder<GItem>> {

		/** Diese Methode gibt einen neuen {@link SimpleSortedSetBuilder} zur gegebenen {@link SortedSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param items {@link SortedSet}.
		 * @return {@link SimpleSortedSetBuilder}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public static <GItem> SimpleSortedSetBuilder<GItem> from(final SortedSet<GItem> items) throws NullPointerException {
			return new SimpleSortedSetBuilder<>(items);
		}

		{}

		/** Dieser Konstruktor initialisiert die {@link SortedSet}.
		 *
		 * @param items {@link SortedSet}.
		 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
		public SimpleSortedSetBuilder(final SortedSet<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SimpleSortedSetBuilder<GItem> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link SortedMap}.
	 *
	 * @author [cc-by] 2016 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel in der Abbildung.
	 * @param <GValue> Typ der Werte in der Abbildung. */
	public static final class SimpleSortedMapBuilder<GKey, GValue> extends BaseSortedMapBuilder2<GKey, GValue, SimpleSortedMapBuilder<GKey, GValue>> {

		/** Diese Methode gibt einen neuen {@link SimpleSortedMapBuilder} zum gegebenen {@link SortedMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel in der Abbildung.
		 * @param <GValue> Typ der Werte in der Abbildung.
		 * @param entries {@link SortedMap}.
		 * @return {@link SimpleSortedMapBuilder}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public static <GKey, GValue> SimpleSortedMapBuilder<GKey, GValue> from(final SortedMap<GKey, GValue> entries) throws NullPointerException {
			return new SimpleSortedMapBuilder<>(entries);
		}

		{}

		/** Dieser Konstruktor initialisiert das {@link SortedMap}.
		 *
		 * @param entries {@link SortedMap}.
		 * @throws NullPointerException Wenn {@code entries} {@code null} ist. */
		public SimpleSortedMapBuilder(final SortedMap<GKey, GValue> entries) throws NullPointerException {
			super(entries);
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SimpleSortedMapBuilder<GKey, GValue> customThis() {
			return this;
		}

	}

	{}

	/** Diese Methode gibt einen {@link Builder} zurück, der den gegebenen Datensatz bereitstellt.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param value Datensatz.
	 * @return {@code value}-{@link Builder}. */
	public static <GValue> Builder<GValue> valueBuilder(final GValue value) {
		return new Builder<GValue>() {

			@Override
			public final GValue build() throws IllegalStateException {
				return value;
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("valueBuilder", value);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Builders.nativeBuilder(Natives.parse(memberText))}.
	 *
	 * @see #nativeBuilder(java.lang.reflect.Method)
	 * @see #nativeBuilder(java.lang.reflect.Constructor)
	 * @see Natives#parse(String)
	 * @param <GValue> Typ des Datensatzes.
	 * @param memberText Methoden- oder Konstruktortext.
	 * @return {@code native}-{@link Builder}.
	 * @throws NullPointerException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws IllegalArgumentException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst.
	 * @throws ReflectiveOperationException Wenn {@link Natives#parse(String)} eine entsprechende Ausnahme auslöst. */
	public static <GValue> Builder<GValue> nativeBuilder(final String memberText)
		throws NullPointerException, IllegalArgumentException, ReflectiveOperationException {
		final Object object = Natives.parse(memberText);
		if (object instanceof java.lang.reflect.Method) return Builders.nativeBuilder((java.lang.reflect.Method)object);
		return Builders.nativeBuilder((java.lang.reflect.Constructor<?>)object);
	}

	/** Diese Methode gibt einen {@link Builder} zur gegebenen {@link java.lang.reflect.Method nativen statischen Methode} zurück.<br>
	 * Der vom gelieferten {@link Builder} erzeugte Datensatz entspricht {@code method.invoke(null)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GValue> Typ des Datensatzes.
	 * @param method Native statische Methode.
	 * @return {@code native}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Methode nicht statisch ist. */
	public static <GValue> Builder<GValue> nativeBuilder(final java.lang.reflect.Method method) throws NullPointerException, IllegalArgumentException {
		if (!Modifier.isStatic(method.getModifiers())) throw new IllegalArgumentException();
		return new Builder<GValue>() {

			@Override
			@SuppressWarnings ("unchecked")
			public GValue build() throws IllegalStateException {
				try {
					return (GValue)method.invoke(null);
				} catch (IllegalAccessException | InvocationTargetException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("nativeBuilder", Natives.formatMethod(method));
			}

		};
	}

	/** Diese Methode gibt einen {@link Builder} zum gegebenen {@link java.lang.reflect.Constructor nativen Kontruktor} zurück.<br>
	 * Der vom gelieferten {@link Builder} erzeugte Datensatz entspricht {@code constructor.newInstance()}.
	 *
	 * @see java.lang.reflect.Constructor#newInstance(Object...)
	 * @param <GValue> Typ des Datensatzes.
	 * @param constructor Nativer Kontruktor.
	 * @return {@code native}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code constructor} {@code null} ist. */
	public static <GValue> Builder<GValue> nativeBuilder(final java.lang.reflect.Constructor<?> constructor) throws NullPointerException {
		if (constructor == null) throw new NullPointerException("constructor = null");
		return new Builder<GValue>() {

			@Override
			@SuppressWarnings ("unchecked")
			public GValue build() throws IllegalStateException {
				try {
					return (GValue)constructor.newInstance();
				} catch (IllegalAccessException | InstantiationException | InvocationTargetException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("nativeBuilder", Natives.formatConstructor(constructor));
			}

		};
	}

	/** Diese Methode gibt einen gepufferten {@link Builder} zurück, der den vonm gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines
	 * {@link SoftPointer} verwaltet.
	 *
	 * @see #bufferedBuilder(int, Builder)
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@code buffered}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist. */
	public static <GValue> Builder<GValue> bufferedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		return Builders.bufferedBuilder(Pointers.SOFT, builder);
	}

	/** Diese Methode gibt einen gepufferten {@link Builder} zurück, der den vonm gegebenen {@link Builder} erzeugten Datensatz mit Hilfe eines {@link Pointer} im
	 * gegebenenen Modus verwaltet.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param mode {@link Pointer}-Modus ({@link Pointers#HARD}, {@link Pointers#WEAK}, {@link Pointers#SOFT}).
	 * @param builder {@link Builder}.
	 * @return {@code buffered}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code mode} ungültig ist. */
	public static <GValue> Builder<GValue> bufferedBuilder(final int mode, final Builder<? extends GValue> builder)
		throws NullPointerException, IllegalArgumentException {
		if (builder == null) throw new NullPointerException("builder = null");
		Pointers.pointer(mode, null);
		return new Builder<GValue>() {

			Pointer<GValue> pointer;

			@Override
			public final GValue build() throws IllegalStateException {
				final Pointer<GValue> pointer = this.pointer;
				if (pointer != null) {
					final GValue data = pointer.data();
					if (data != null) return data;
					if (pointer == Pointers.NULL) return null;
				}
				final GValue data = builder.build();
				this.pointer = Pointers.pointer(mode, data);
				return data;
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("bufferedBuilder", mode, builder);
			}

		};
	}

	/** Diese Methode gibt einen umgewandelten {@link Builder} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Converter} aus dem Datensatz des gegebenen
	 * {@link Builder} ermittelt wird.
	 *
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder} sowie der Eingabe des gegebenen {@link Converter}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter} sowie des Datensatzes.
	 * @param converter {@link Converter}.
	 * @param builder {@link Builder}.
	 * @return {@code converted}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code builder} {@code null} ist. */
	public static <GInput, GOutput> Builder<GOutput> convertedBuilder(final Converter<? super GInput, ? extends GOutput> converter,
		final Builder<? extends GInput> builder) throws NullPointerException {
		if (builder == null) throw new NullPointerException("builder = null");
		if (converter == null) throw new NullPointerException("converter = null");
		return new Builder<GOutput>() {

			@Override
			public final GOutput build() throws IllegalStateException {
				return converter.convert(builder.build());
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("convertedBuilder", converter, builder);
			}

		};
	}

	/** Diese Methode gibt einen synchronisierten {@link Builder} zurück, der den gegebenen {@link Builder} via {@code synchronized(builder)} synchronisiert.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @return {@code synchronized}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code builder} {@code null} ist. */
	public static <GValue> Builder<GValue> synchronizedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		if (builder == null) throw new NullPointerException("builder = null");
		return new Builder<GValue>() {

			@Override
			public final GValue build() throws IllegalStateException {
				synchronized (builder) {
					return builder.build();
				}
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("synchronizedBuilder", builder);
			}

		};
	}

}
