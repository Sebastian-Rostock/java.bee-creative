package bee.creative.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import bee.creative.util.Pointers.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Builder}.
 *
 * @see Builder
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Builders {

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Erzeugung eines Datensatzes.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseBuilder<GValue, GThis> implements Builder<GValue> {

		/** Diese Methode gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		protected abstract GThis customThis();

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator das auf Feld {@link #result} gespeicherte Objekt.
	 *
	 * @param <GResult> Typ des Werts.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseBuilder2<GResult, GThis> extends BaseBuilder<GResult, GThis> {

		/** Dieses Feld speichert den Wert. */
		protected GResult result;

		/** {@inheritDoc} */
		@Override
		public final GResult build() throws IllegalStateException {
			return this.result;
		}

		/** {@inheritDoc} */
		@Override
		public String toString() {
			return Objects.toString(true, this.result);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert. Der {@link #iterator()} liefert diesen, sofern er nicht {@code null} ist.
	 *
	 * @param <GResult> Typ des Werts.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseValueBuilder<GResult, GThis> extends BaseBuilder2<GResult, GThis> implements Iterable<GResult> {

		/** Diese Methode setzt den Wert und gibt {@code this} zurück.
		 *
		 * @see #build()
		 * @param value Wert.
		 * @return {@code this}. */
		public GThis use(final GResult value) {
			this.result = value;
			return this.customThis();
		}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #use(Object)
		 * @param source Konfigurator.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GThis use(final BaseValueBuilder<? extends GResult, ?> source) throws NullPointerException {
			return this.use(source.result);
		}

		/** Diese Methode setzt den Wert auf {@code null} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public GThis clear() {
			this.result = null;
			return this.customThis();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final Iterator<GResult> iterator() {
			final GResult result = this.result;
			if (result == null) return Iterators.emptyIterator();
			return Iterators.itemIterator(result);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Collection}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link Collection}.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSetBuilder<GItem, GResult extends Collection<GItem>, GThis> extends BaseBuilder2<GResult, GThis> implements Iterable<GItem> {

		/** Dieser Konstruktor initialisiert die interne Sammlung.
		 *
		 * @param result Sammlung.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected BaseSetBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		{}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #clear()
		 * @see #putAll(Iterable)
		 * @param source Konfigurator.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GThis use(final BaseSetBuilder<? extends GItem, ?, ?> source) throws NullPointerException {
			Objects.assertNotNull(source);
			this.clear();
			return this.putAll(source.result);
		}

		/** Diese Methode {@link Collection#add(Object) fügt das gegebene Element} der {@link #build() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param item Element.
		 * @return {@code this}. */
		public GThis put(final GItem item) {
			this.result.add(item);
			return this.customThis();
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #build() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		@SuppressWarnings ("unchecked")
		public GThis putAll(final GItem... items) {
			return this.putAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #build() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis putAll(final Iterator<? extends GItem> items) {
			while (items.hasNext()) {
				this.result.add(items.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #build() internen Sammlung} hinzu und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis putAll(final Iterable<? extends GItem> items) {
			return this.putAll(items.iterator());
		}

		/** Diese Methode {@link Collection#remove(Object) entfern das gegebene Element} und gibt {@code this} zurück.
		 *
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public GThis pop(final Object key) {
			this.result.remove(key);
			return this.customThis();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern die gegebenen Elemente} und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis popAll(final Object... items) {
			return this.popAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#remove(Object) entfern die gegebenen Elemente} und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis popAll(final Iterator<?> items) {
			while (items.hasNext()) {
				this.result.remove(items.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die gegebenen Elemente} und gibt {@code this} zurück.
		 *
		 * @param items Elemente.
		 * @return {@code this}. */
		public GThis popAll(final Iterable<?> items) {
			return this.popAll(items.iterator());
		}

		/** Diese Methode {@link Collection#clear() entfern alle Elemente} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public GThis clear() {
			this.result.clear();
			return this.customThis();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final Iterator<GItem> iterator() {
			return this.result.iterator();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseMapBuilder<GKey, GValue, GResult extends Map<GKey, GValue>, GThis> extends BaseBuilder2<GResult, GThis>
		implements Iterable<Entry<GKey, GValue>> {

		/** Dieses Feld speichert den über {@link #forKey(Object)} gewählten Schlüssel, der in {@link #useValue(Object)} eingesetzt wird. */
		protected GKey key;

		/** Dieser Konstruktor initialisiert die interne {@link Map}.
		 *
		 * @param result interne {@link Map}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected BaseMapBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		{}

		@SuppressWarnings ("javadoc")
		void putImpl(final Entry<? extends GKey, ? extends GValue> entry) {
			this.result.put(entry.getKey(), entry.getValue());
		}

		@SuppressWarnings ("javadoc")
		void putKeyFromImpl(final Getter<? super GValue, ? extends GKey> key, final GValue value) {
			this.result.put(key.get(value), value);
		}

		@SuppressWarnings ("javadoc")
		void putValueFromImpl(final Getter<? super GKey, ? extends GValue> value, final GKey key) {
			this.result.put(key, value.get(key));
		}

		@SuppressWarnings ("javadoc")
		void putInverseImpl(final Entry<? extends GValue, ? extends GKey> entry) {
			this.result.put(entry.getValue(), entry.getKey());
		}

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@code this} zurück.
		 *
		 * @see #clear()
		 * @see #putAll(Map)
		 * @param source Konfigurator.
		 * @return {@code this}.
		 * @throws NullPointerException Wenn {@code source} {@code null} ist. */
		public GThis use(final BaseMapBuilder<? extends GKey, ? extends GValue, ?, ?> source) throws NullPointerException {
			Objects.assertNotNull(source);
			return this.putAll(source.result);
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param entry Eintrag.
		 * @return {@code this}. */
		public GThis put(final Entry<? extends GKey, ? extends GValue> entry) {
			this.putImpl(entry);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param key Schlüssel des Eintrags.
		 * @param value Wert des Eintrags.
		 * @return {@code this}. */
		public GThis put(final GKey key, final GValue value) {
			this.result.put(key, value);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung des Schlüssels zum gegebenen Wert des Eintrags.
		 * @param value Wert des Eintrags.
		 * @return {@code this}. */
		public GThis putKeyFrom(final Getter<? super GValue, ? extends GKey> key, final GValue value) {
			this.putKeyFromImpl(key, value);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung des Werts zum gegebenen Schlüssel des Eintrags.
		 * @param key Schlüssel des Eintrags.
		 * @return {@code this}. */
		public GThis putValueFrom(final Getter<? super GKey, ? extends GValue> value, final GKey key) {
			this.putValueFromImpl(value, key);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag invertiert ein} und gibt {@code this} zurück.<br>
		 * Schlüssel und Wert des Eintrags werden dazu als Wert bzw. Schlüssel verwendet.
		 *
		 * @param entry Eintrag.
		 * @return {@code this}. */
		public GThis putInverse(final Entry<? extends GValue, ? extends GKey> entry) {
			this.putInverseImpl(entry);
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Map<? extends GKey, ? extends GValue> entries) {
			return this.putAll(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterator<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			while (entries.hasNext()) {
				this.putImpl(entries.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			return this.putAll(entries.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAll(final GKey[] keys, final GValue[] values) {
			return this.putAll(Arrays.asList(keys), Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterator<? extends GKey> keys, final Iterator<? extends GValue> values) {
			while (keys.hasNext() && values.hasNext()) {
				this.put(keys.next(), values.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAll(final Iterable<? extends GKey> keys, final Iterable<? extends GValue> values) {
			return this.putAll(keys.iterator(), values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		@SuppressWarnings ("unchecked")
		public GThis putAllKeysFrom(final Getter<? super GValue, ? extends GKey> key, final GValue... values) {
			return this.putAllKeysFrom(key, Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAllKeysFrom(final Getter<? super GValue, ? extends GKey> key, final Iterator<? extends GValue> values) {
			while (values.hasNext()) {
				this.putKeyFromImpl(key, values.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param key {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@code this}. */
		public GThis putAllKeysFrom(final Getter<? super GValue, ? extends GKey> key, final Iterable<? extends GValue> values) {
			return this.putAllKeysFrom(key, values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		@SuppressWarnings ("unchecked")
		public GThis putAllValuesFrom(final Getter<? super GKey, ? extends GValue> value, final GKey... keys) {
			return this.putAllValuesFrom(value, Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis putAllValuesFrom(final Getter<? super GKey, ? extends GValue> value, final Iterator<? extends GKey> keys) {
			while (keys.hasNext()) {
				this.putValueFromImpl(value, keys.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@code this} zurück.
		 *
		 * @param value {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis putAllValuesFrom(final Getter<? super GKey, ? extends GValue> value, final Iterable<? extends GKey> keys) {
			return this.putAllValuesFrom(value, keys.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAllInverse(final Map<? extends GValue, ? extends GKey> entries) {
			return this.putAllInverse(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAllInverse(final Iterator<? extends Entry<? extends GValue, ? extends GKey>> entries) {
			while (entries.hasNext()) {
				this.putInverseImpl(entries.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@code this} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@code this}. */
		public GThis putAllInverse(final Iterable<? extends Entry<? extends GValue, ? extends GKey>> entries) {
			return this.putAllInverse(entries.iterator());
		}

		/** Diese Methode {@link Map#remove(Object) entfern den Eintrag mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public GThis pop(final Object key) {
			this.result.remove(key);
			return this.customThis();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis popAll(final Object... keys) {
			return this.popAll(Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis popAll(final Iterator<?> keys) {
			while (keys.hasNext()) {
				this.result.remove(keys.next());
			}
			return this.customThis();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@code this} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@code this}. */
		public GThis popAll(final Iterable<?> keys) {
			return this.popAll(keys.iterator());
		}

		/** Diese Methode {@link Map#clear() entfern alle Einträge} und gibt {@code this} zurück.
		 *
		 * @return {@code this}. */
		public GThis clear() {
			this.result.clear();
			return this.customThis();
		}

		/** Diese Methode wählt den gegebenen Schlüssel und gibt {@code this} zurück. Dieser Schlüssel wird in den nachfolgenden Aufrufen von {@link #getValue()}
		 * und {@link #useValue(Object)} verwendet.
		 *
		 * @see #getValue()
		 * @see #useValue(Object)
		 * @param key Schlüssel.
		 * @return {@code this}. */
		public GThis forKey(final GKey key) {
			this.key = key;
			return this.customThis();
		}

		/** Diese Methode gibt den Wert zum {@link #forKey(Object) gewählten Schlüssel} zurück.
		 *
		 * @see #forKey(Object)
		 * @see #useValue(Object)
		 * @return Wert zum gewählten Schlüssel. */
		public GValue getValue() {
			return this.result.get(this.key);
		}

		/** Diese Methode setzt den Wert zum {@link #forKey(Object) gewählten Schlüssel} und gibt {@code this} zurück.
		 *
		 * @see #forKey(Object)
		 * @see #getValue()
		 * @param value Wert.
		 * @return {@code this}. */
		public GThis useValue(final GValue value) {
			this.put(this.key, value);
			return this.customThis();
		}

		{}

		/** {@inheritDoc} */
		@Override
		public final Iterator<Entry<GKey, GValue>> iterator() {
			return this.result.entrySet().iterator();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für ein {@link HashSet}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseSetData<GItem, GThis> extends BaseSetBuilder<GItem, HashSet<GItem>, GThis> {

		/** Dieser Konstruktor initialisiert das interne {@link HashSet}. */
		public BaseSetData() {
			super(new HashSet<GItem>());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link HashMap}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GThis> Typ des konkreten Nachfahren dieser Klasse. */
	public static abstract class BaseMapData<GKey, GValue, GThis> extends BaseMapBuilder<GKey, GValue, HashMap<GKey, GValue>, GThis> {

		/** Dieser Konstruktor initialisiert die interne {@link HashMap}. */
		public BaseMapData() {
			super(new HashMap<GKey, GValue>());
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}. */
	public static class MapBuilder<GKey, GValue, GResult extends Map<GKey, GValue>>
		extends BaseMapBuilder<GKey, GValue, GResult, MapBuilder<GKey, GValue, GResult>> {

		/** Diese Methode gibt einen {@link MapBuilder} zur gegebenen {@link Map} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param <GResult> Typ der {@link Map}.
		 * @param result {@link Map}.
		 * @return {@link MapBuilder}. */
		public static <GKey, GValue, GResult extends Map<GKey, GValue>> MapBuilder<GKey, GValue, GResult> from(final GResult result) {
			return new MapBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} mit natürlicher Ordnung zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GValue> MapBuilder<GKey, GValue, TreeMap<GKey, GValue>> forTreeMap() {
			return MapBuilder.forTreeMap(null);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param comparator Ordnung der Schlüssel.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GValue> MapBuilder<GKey, GValue, TreeMap<GKey, GValue>> forTreeMap(final Comparator<? super GKey> comparator) {
			return MapBuilder.from(new TreeMap<GKey, GValue>(comparator));
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} mit Steuwertpuffer zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GValue> MapBuilder<GKey, GValue, HashMap<GKey, GValue>> forHashMap() {
			return MapBuilder.forHashMap(true);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GValue> MapBuilder<GKey, GValue, HashMap<GKey, GValue>> forHashMap(final boolean withHashCache) {
			return MapBuilder.from(withHashCache ? new HashMap2<GKey, GValue>() : new HashMap<GKey, GValue>());
		}

		{}

		/** Dieser Konstruktor initialisiert die interne {@link Map}.
		 *
		 * @param result interne {@link Map}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public MapBuilder(final GResult result) throws NullPointerException {
			super(result);
		}

		{}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur datentypsicheren {@link #build() Abbildung} zurück.
		 *
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return neuer {@link MapBuilder} zur {@code checkedMap}. */
		public MapBuilder<GKey, GValue, Map<GKey, GValue>> toChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			return MapBuilder.from(Collections.checkedMap(this.result, keyClazz, valueClazz));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur übersetzten {@link #build() Abbildung} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedMap(Map, Translator, Translator)
		 * @param <GKey2> Typ der übersetzten Schlüssel.
		 * @param <GValue2> Typ der übersetzten Werte.
		 * @param keyTranslator {@link Translator} zur Übersetzung der Schlüssel.
		 * @param valueTranslator {@link Translator} zur Übersetzung der Werte.
		 * @return neuer {@link MapBuilder} zur {@code translatedMap}. */
		public <GKey2, GValue2> MapBuilder<GKey2, GValue2, Map<GKey2, GValue2>> toTranslated(final Translator<GKey, GKey2> keyTranslator,
			final Translator<GValue, GValue2> valueTranslator) {
			return MapBuilder.from(bee.creative.util.Collections.translatedMap(this.result, keyTranslator, valueTranslator));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur threadsicheren {@link #build() Abbildung} zurück.
		 *
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code synchronizedMap}. */
		public MapBuilder<GKey, GValue, Map<GKey, GValue>> toSynchronized() {
			return MapBuilder.from(Collections.synchronizedMap(this.result));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur unveränderliche {@link #build() Abbildung} zurück.
		 *
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code unmodifiableMap}. */
		public MapBuilder<GKey, GValue, Map<GKey, GValue>> toUnmodifiable() {
			return MapBuilder.from(Collections.unmodifiableMap(this.result));
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected MapBuilder<GKey, GValue, GResult> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ des {@link Set}. */
	public static class SetBuilder<GItem, GResult extends Set<GItem>> extends BaseSetBuilder<GItem, GResult, SetBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link SetBuilder} zum gegebenen {@link Set} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param <GResult> Typ des {@link Set}.
		 * @param result {@link Set}.
		 * @return {@link SetBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem, GResult extends Set<GItem>> SetBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new SetBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} mit natürlicher Ordnung zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link SetBuilder} eines {@link TreeSet}. */
		public static <GItem> SetBuilder<GItem, TreeSet<GItem>> forTreeSet() {
			return SetBuilder.forTreeSet(null);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param comparator Ordnung der Elemente.
		 * @return {@link SetBuilder} eines {@link TreeSet}. */
		public static <GItem> SetBuilder<GItem, TreeSet<GItem>> forTreeSet(final Comparator<? super GItem> comparator) {
			return SetBuilder.from(new TreeSet<>(comparator));
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} mit Steuwertpuffer zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <GItem> SetBuilder<GItem, HashSet<GItem>> forHashSet() {
			return SetBuilder.forHashSet(true);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <GItem> SetBuilder<GItem, HashSet<GItem>> forHashSet(final boolean withHashCache) {
			return SetBuilder.from(withHashCache ? new HashSet2<GItem>() : new HashSet<GItem>());
		}

		{}

		/** Dieser Konstruktor initialisiert das {@link Set}.
		 *
		 * @param result {@link Set}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public SetBuilder(final GResult result) throws NullPointerException {
			super(result);
		}

		{}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Vereinigungsmenge dieser {@link #build() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#unionSet(Set, Set)
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public SetBuilder<GItem, Set<GItem>> toUnion(final Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.unionSet(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Schnittmenge dieser {@link #build() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#intersectionSet(Set, Set)
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code intersectionSet}. */
		public SetBuilder<GItem, Set<GItem>> toIntersection(final Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.intersectionSet(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #build() Menge} zurück.
		 *
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public SetBuilder<GItem, Set<GItem>> toChecked(final Class<GItem> itemClazz) {
			return SetBuilder.from(Collections.checkedSet(this.result, itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #build() Menge} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedSet(Set, Translator)
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code translatedSet}. */
		public <GItem2> SetBuilder<GItem2, Set<GItem2>> toTranslated(final Translator<GItem, GItem2> itemTranslator) {
			return SetBuilder.from(bee.creative.util.Collections.translatedSet(this.result, itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die threadsichere {@link #build() Menge} zurück.
		 *
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code synchronizedSet}. */
		public SetBuilder<GItem, Set<GItem>> toSynchronized() {
			return SetBuilder.from(Collections.synchronizedSet(this.result));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die unveränderliche {@link #build() Menge} zurück.
		 *
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code unmodifiableSet}. */
		public SetBuilder<GItem, Set<GItem>> toUnmodifiable() {
			return SetBuilder.from(Collections.unmodifiableSet(this.result));
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final SetBuilder<GItem, GResult> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link List}. */
	public static class ListBuilder<GItem, GResult extends List<GItem>> extends BaseSetBuilder<GItem, GResult, ListBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link ListBuilder} zur gegebenen {@link List} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param <GResult> Typ des {@link List}.
		 * @param result {@link List}.
		 * @return {@link ListBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem, GResult extends List<GItem>> ListBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new ListBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link ArrayList} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link ArrayList}. */
		public static <GItem> ListBuilder<GItem, ArrayList<GItem>> forArrayList() {
			return ListBuilder.from(new ArrayList<GItem>());
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link LinkedList} mit zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link LinkedList}. */
		public static <GItem> ListBuilder<GItem, LinkedList<GItem>> forLinkedList() {
			return ListBuilder.from(new LinkedList<GItem>());
		}

		{}

		/** Dieser Konstruktor initialisiert das {@link List}.
		 *
		 * @param result {@link List}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public ListBuilder(final GResult result) throws NullPointerException {
			super(result);
		}

		{}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die rückwärts geordnete {@link #build() Liste} zurück.
		 *
		 * @see bee.creative.util.Collections#reverseList(List)
		 * @return neuer {@link ListBuilder} zur {@code reverseList}. */
		public ListBuilder<GItem, List<GItem>> toReverse() {
			return ListBuilder.from(bee.creative.util.Collections.reverseList(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #build() Liste} mit der gegebenen zurück.
		 *
		 * @see bee.creative.util.Collections#chainedList(List, List)
		 * @param items zweite Liste.
		 * @return neuer {@link ListBuilder} zur {@code chained}. */
		public ListBuilder<GItem, List<GItem>> toChained(final List<GItem> items) {
			return ListBuilder.from(bee.creative.util.Collections.chainedList(this.result, items));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #build() Liste} mit der gegebenen zurück.
		 *
		 * @see bee.creative.util.Collections#chainedList(List, List, boolean)
		 * @param items zweite Liste.
		 * @param extendMode Erweiterungsmodus.
		 * @return neuer {@link ListBuilder} zur {@code chained}. */
		public ListBuilder<GItem, List<GItem>> toChained(final List<GItem> items, final boolean extendMode) {
			return ListBuilder.from(bee.creative.util.Collections.chainedList(this.result, items, extendMode));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #build() Liste} zurück.
		 *
		 * @see java.util.Collections#checkedList(List, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code checkedList}. */
		public ListBuilder<GItem, List<GItem>> toChecked(final Class<GItem> itemClazz) {
			return ListBuilder.from(Collections.checkedList(this.result, itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #build() Liste} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedList(List, Translator)
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code translatedList}. */
		public <GItem2> ListBuilder<GItem2, List<GItem2>> toTranslated(final Translator<GItem, GItem2> itemTranslator) {
			return ListBuilder.from(bee.creative.util.Collections.translatedList(this.result, itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die threadsichere {@link #build() Liste} zurück.
		 *
		 * @see java.util.Collections#synchronizedList(List)
		 * @return neuer {@link ListBuilder} zur {@code synchronizedList}. */
		public ListBuilder<GItem, List<GItem>> toSynchronized() {
			return ListBuilder.from(Collections.synchronizedList(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die unveränderliche {@link #build() Liste} zurück.
		 *
		 * @see java.util.Collections#unmodifiableList(List)
		 * @return neuer {@link ListBuilder} zur {@code unmodifiableList}. */
		public ListBuilder<GItem, List<GItem>> toUnmodifiable() {
			return ListBuilder.from(Collections.unmodifiableList(this.result));
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected final ListBuilder<GItem, GResult> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für einen {@link Comparator}.
	 *
	 * @param <GItem> Typ der verglichenen Elemente.
	 * @param <GResult> Typ des {@link Comparator}. */
	public static class ComparatorBuilder<GItem, GResult extends Comparator<GItem>> extends BaseBuilder2<GResult, ComparatorBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link ComparatorBuilder} zum gegebenen {@link Comparator} zurück.
		 *
		 * @param <GItem> Typ der verglichenen Elemente.
		 * @param <GResult> Typ des {@link Comparator}.
		 * @param result {@link Comparator}.
		 * @return {@link ComparatorBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static final <GItem, GResult extends Comparator<GItem>> ComparatorBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new ComparatorBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link ComparatorBuilder} zur {@link Comparators#naturalComparator() natürlichen Ordnung} zurück.
		 *
		 * @param <GItem> Typ der {@link Comparable} Elemente.
		 * @return {@link ComparatorBuilder}. */
		public static final <GItem extends Comparable<? super GItem>> ComparatorBuilder<GItem, Comparator<GItem>> fromNatural() {
			return ComparatorBuilder.from(Comparators.<GItem>naturalComparator());
		}

		{}

		/** Dieser Konstruktor initialisiert den internen {@link Comparator}.
		 *
		 * @param result interner {@link Comparator}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public ComparatorBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		{}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum {@code null} vergleichenden {@link #build() Comparator} zurück.
		 *
		 * @see Comparators#defaultComparator(Comparator)
		 * @return neuer {@link ComparatorBuilder} zu {@code defaultComparator}. */
		public ComparatorBuilder<GItem, Comparator<GItem>> toDefault() {
			return ComparatorBuilder.from(Comparators.defaultComparator(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum umkehrenden {@link #build() Comparator} zurück.
		 *
		 * @see Comparators#reverseComparator(Comparator)
		 * @return neuer {@link ComparatorBuilder} zu {@code reverseComparator}. */
		public ComparatorBuilder<GItem, Comparator<GItem>> toReverse() {
			return ComparatorBuilder.from(Comparators.reverseComparator(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum iterierenden {@link #build() Comparator} zurück.
		 *
		 * @see Comparators#iterableComparator(Comparator)
		 * @return neuer {@link ComparatorBuilder} zu {@code iterableComparator}. */
		public ComparatorBuilder<Iterable<? extends GItem>, Comparator<Iterable<? extends GItem>>> toIterable() {
			return ComparatorBuilder.from(Comparators.iterableComparator(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum verkettenden {@link #build() Comparator} zurück.
		 *
		 * @see Comparators#chainedComparator(Comparator, Comparator)
		 * @param comparator zweiter {@link Comparator}, das nach dem {@link #build() aktuell konfigurierten} angewandt wird.
		 * @return neuer {@link ComparatorBuilder} zu {@code chainedComparator}. */
		public ComparatorBuilder<GItem, Comparator<GItem>> toChained(final Comparator<? super GItem> comparator) {
			return ComparatorBuilder.from(Comparators.chainedComparator(this.result, comparator));
		}

		/** Diese Methode gibt einen neuen {@link ComparatorBuilder} zum navigierenden {@link #build() Comparator} zurück.
		 *
		 * @see Comparators#navigatedComparator(Getter, Comparator)
		 * @param <GItem2> Typ der Eingabe des {@link Getter} sowie der Elemente des erzeugten {@link Comparator}.
		 * @param navigator {@link Getter} zur Navigation.
		 * @return neuer {@link ComparatorBuilder} zu {@code navigatedComparator}. */
		public <GItem2> ComparatorBuilder<GItem2, Comparator<GItem2>> toNavigated(final Getter<? super GItem2, ? extends GItem> navigator) {
			return ComparatorBuilder.from(Comparators.navigatedComparator(navigator, this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum gegebenen Element sowie dem {@link #build() aktuell konfigurierten Comparator} zurück.
		 *
		 * @see Comparables#itemComparable(Getter, Comparator)
		 * @param item Element, welches über den {@link #build() aktuellen Comparator} mit den Eingabe des erzeugten {@link Comparable} vergleichen wird.
		 * @return neuer {@link ComparableBuilder} zu {@code itemComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toComparable(final GItem item) {
			return ComparableBuilder.from(Comparables.itemComparable(item, this.result));
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected ComparatorBuilder<GItem, GResult> customThis() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für einen {@link Comparable}.
	 *
	 * @param <GItem> Typ der verglichenen Elemente.
	 * @param <GResult> Typ des {@link Comparable}. */
	public static class ComparableBuilder<GItem, GResult extends Comparable<GItem>> extends BaseBuilder2<GResult, ComparableBuilder<GItem, GResult>> {

		/** Diese Methode gibt einen {@link ComparableBuilder} zum gegebenen {@link Comparable} zurück.
		 *
		 * @param <GItem> Typ der verglichenen Elemente.
		 * @param <GResult> Typ des {@link Comparable}.
		 * @param result {@link Comparable}.
		 * @return {@link ComparableBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static final <GItem, GResult extends Comparable<GItem>> ComparableBuilder<GItem, GResult> from(final GResult result) throws NullPointerException {
			return new ComparableBuilder<>(result);
		}

		{}

		/** Dieser Konstruktor initialisiert das interne {@link Comparable}.
		 *
		 * @param result internes {@link Comparable}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		protected ComparableBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.assertNotNull(result);
		}

		{}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum {@code null} vergleichenden {@link #build() Comparable} zurück.
		 *
		 * @see Comparables#defaultComparable(Comparable)
		 * @return neuer {@link ComparableBuilder} zu {@code defaultComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toDefault() {
			return ComparableBuilder.from(Comparables.defaultComparable(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum umkehrenden {@link #build() Comparable} zurück.
		 *
		 * @see Comparables#reverseComparable(Comparable)
		 * @return neuer {@link ComparableBuilder} zu {@code reverseComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toReverse() {
			return ComparableBuilder.from(Comparables.reverseComparable(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum iterierenden {@link #build() Comparable} zurück.
		 *
		 * @see Comparables#iterableComparable(Comparable)
		 * @return neuer {@link ComparableBuilder} zu {@code iterableComparable}. */
		public ComparableBuilder<Iterable<? extends GItem>, Comparable<Iterable<? extends GItem>>> toIterable() {
			return ComparableBuilder.from(Comparables.iterableComparable(this.result));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum verkettenden {@link #build() Comparable} zurück.
		 *
		 * @see Comparables#chainedComparable(Comparable, Comparable)
		 * @param comparable zweites {@link Comparable}, das nach dem {@link #build() aktuell konfigurierten} angewandt wird.
		 * @return neuer {@link ComparableBuilder} zu {@code chainedComparable}. */
		public ComparableBuilder<GItem, Comparable<GItem>> toChained(final Comparable<? super GItem> comparable) {
			return ComparableBuilder.from(Comparables.chainedComparable(this.result, comparable));
		}

		/** Diese Methode gibt einen neuen {@link ComparableBuilder} zum navigierenden {@link #build() Comparable} zurück.
		 *
		 * @see Comparables#navigatedComparable(Getter, Comparable)
		 * @param <GItem2> Typ der Eingabe des {@link Getter} sowie der Elemente des erzeugten {@link Comparable}.
		 * @param navigator {@link Getter} zur Navigation.
		 * @return neuer {@link ComparableBuilder} zu {@code iterableComparable}. */
		public <GItem2> ComparableBuilder<GItem2, Comparable<GItem2>> toNavigated(final Getter<? super GItem2, ? extends GItem> navigator) {
			return ComparableBuilder.from(Comparables.navigatedComparable(navigator, this.result));
		}

		{}

		/** {@inheritDoc} */
		@Override
		protected ComparableBuilder<GItem, GResult> customThis() {
			return this;
		}

	}

	{}

	/** Diese Methode gibt einen {@link Builder} zurück, der den gegebenen Datensatz bereitstellt.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param value Datensatz.
	 * @return {@code value}-{@link Builder}. */
	public static <GValue> Builder<GValue> itemBuilder(final GValue value) {
		return new Builder<GValue>() {

			@Override
			public final GValue build() throws IllegalStateException {
				return value;
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("itemBuilder", value);
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
		if (object instanceof java.lang.reflect.Constructor<?>) return Builders.nativeBuilder((java.lang.reflect.Constructor<?>)object);
		throw new IllegalArgumentException();
	}

	/** Diese Methode gibt einen {@link Builder} zur gegebenen {@link java.lang.reflect.Method nativen statischen Methode} zurück.<br>
	 * Der vom gelieferten {@link Builder} erzeugte Datensatz entspricht {@code method.invoke(null)}.
	 *
	 * @see java.lang.reflect.Method#invoke(Object, Object...)
	 * @param <GValue> Typ des Datensatzes.
	 * @param method Native statische Methode.
	 * @return {@code native}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code method} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Methode nicht statisch oder nicht parameterlos ist. */
	public static <GValue> Builder<GValue> nativeBuilder(final java.lang.reflect.Method method) throws NullPointerException, IllegalArgumentException {
		if (!Modifier.isStatic(method.getModifiers()) || (method.getParameterTypes().length != 0)) throw new IllegalArgumentException();
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
		Objects.assertNotNull(constructor);
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
		Objects.assertNotNull(builder);
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

	/** Diese Methode gibt einen umgewandelten {@link Builder} zurück, dessen Datensatz mit Hilfe des gegebenen {@link Getter} aus dem Datensatz des gegebenen
	 * {@link Builder} ermittelt wird.
	 *
	 * @param <GInput> Typ des Datensatzes des gegebenen {@link Builder} sowie der Eingabe des gegebenen {@link Getter}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Getter} sowie des Datensatzes.
	 * @param navigator {@link Getter}.
	 * @param builder {@link Builder}.
	 * @return {@code navigated}-{@link Builder}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code builder} {@code null} ist. */
	public static <GInput, GOutput> Builder<GOutput> navigatedBuilder(final Getter<? super GInput, ? extends GOutput> navigator,
		final Builder<? extends GInput> builder) throws NullPointerException {
		Objects.assertNotNull(navigator);
		Objects.assertNotNull(builder);
		return new Builder<GOutput>() {

			@Override
			public final GOutput build() throws IllegalStateException {
				return navigator.get(builder.build());
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("navigatedBuilder", navigator, builder);
			}

		};
	}

	/** Diese Methode ist eine Abkürzung für {@code Builders.synchronizedBuilder(builder, builder)}.
	 *
	 * @see #synchronizedBuilder(Builder, Object) */
	@SuppressWarnings ("javadoc")
	public static <GValue> Builder<GValue> synchronizedBuilder(final Builder<? extends GValue> builder) throws NullPointerException {
		return Builders.synchronizedBuilder(builder, builder);
	}

	/** Diese Methode gibt einen synchronisierten {@link Builder} zurück, der den gegebenen {@link Builder} via {@code synchronized(mutex)} synchronisiert.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param builder {@link Builder}.
	 * @param mutex Synchronisationsobjekt.
	 * @return {@code synchronized}-{@link Builder}.
	 * @throws NullPointerException Wenn der {@code builder} bzw. {@code mutex} {@code null} ist. */
	public static <GValue> Builder<GValue> synchronizedBuilder(final Builder<? extends GValue> builder, final Object mutex) throws NullPointerException {
		Objects.assertNotNull(builder);
		Objects.assertNotNull(mutex);
		return new Builder<GValue>() {

			@Override
			public final GValue build() throws IllegalStateException {
				synchronized (mutex) {
					return builder.build();
				}
			}

			@Override
			public final String toString() {
				return Objects.toInvokeString("synchronizedBuilder", builder, mutex);
			}

		};
	}

}
