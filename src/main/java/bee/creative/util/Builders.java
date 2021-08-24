package bee.creative.util;

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
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Producer Konfiguratoren}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Builders {

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Erzeugung eines Datensatzes.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseBuilder<GValue, GOwner> implements Producer<GValue> {

		/** Diese Methode gibt den Besitzer dieses Konfigurators zurück. Bei selbsständigen Konfiguratoren ist dieser {@code this}.
		 *
		 * @return Besitzer. */
		public abstract GOwner owner();

		@Override
		public String toString() {
			return Objects.toString(true, this.get());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Anpassung eines Datensatzes.
	 *
	 * @param <GValue> Typ des Datensatzes.
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseBuilder2<GValue, GOwner> extends BaseBuilder<GValue, GOwner> implements Property<GValue> {

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@link #owner()} zurück.
		 *
		 * @param source Konfigurator.
		 * @return {@link #owner()}. */
		public GOwner use(final BaseBuilder<? extends GValue, ?> source) throws NullPointerException {
			this.set(source.get());
			return this.owner();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Collection}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link Collection}.
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseSetBuilder<GItem, GResult extends Collection<GItem>, GOwner> extends BaseBuilder2<GResult, GOwner>
		implements Iterable<GItem> {

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt {@link #owner()} zurück.
		 *
		 * @see #clear()
		 * @see #putAll(Iterable) */
		@Override
		public void set(final GResult value) throws NullPointerException {
			this.clear();
			this.putAll(value);
		}

		/** Diese Methode {@link Collection#add(Object) fügt das gegebene Element} der {@link #get() internen Sammlung} hinzu und gibt {@link #owner()} zurück.
		 *
		 * @param item Element.
		 * @return {@link #owner()}. */
		public GOwner put(final GItem item) {
			this.get().add(item);
			return this.owner();
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #get() internen Sammlung} hinzu und gibt {@link #owner()} zurück.
		 *
		 * @param items Elemente.
		 * @return {@link #owner()}. */
		@SafeVarargs
		public final GOwner putAll(final GItem... items) {
			return this.putAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #get() internen Sammlung} hinzu und gibt {@link #owner()} zurück.
		 *
		 * @param items Elemente.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Iterator<? extends GItem> items) {
			while (items.hasNext()) {
				this.get().add(items.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Collection#add(Object) fügt die gegebenen Element} der {@link #get() internen Sammlung} hinzu und gibt {@link #owner()} zurück.
		 *
		 * @param items Elemente.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Iterable<? extends GItem> items) {
			return this.putAll(items.iterator());
		}

		/** Diese Methode {@link Collection#remove(Object) entfern das gegebene Element} und gibt {@link #owner()} zurück.
		 *
		 * @param key Schlüssel.
		 * @return {@link #owner()}. */
		public GOwner pop(final Object key) {
			this.get().remove(key);
			return this.owner();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern die gegebenen Elemente} und gibt {@link #owner()} zurück.
		 *
		 * @param items Elemente.
		 * @return {@link #owner()}. */
		@SafeVarargs
		public final GOwner popAll(final Object... items) {
			return this.popAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#remove(Object) entfern die gegebenen Elemente} und gibt {@link #owner()} zurück.
		 *
		 * @param items Elemente.
		 * @return {@link #owner()}. */
		public GOwner popAll(final Iterator<?> items) {
			Iterators.removeAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die gegebenen Elemente} und gibt {@link #owner()} zurück.
		 *
		 * @param items Elemente.
		 * @return {@link #owner()}. */
		public GOwner popAll(final Iterable<?> items) {
			return this.popAll(items.iterator());
		}

		/** Diese Methode {@link Collection#clear() entfern alle Elemente} und gibt {@link #owner()} zurück.
		 *
		 * @return {@link #owner()}. */
		public GOwner clear() {
			this.get().clear();
			return this.owner();
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.from(this.get().iterator());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}.
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseMapBuilder<GKey, GValue, GResult extends Map<GKey, GValue>, GOwner> extends BaseBuilder2<GResult, GOwner>
		implements Iterable<Entry<GKey, GValue>> {

		public abstract class ValueProxy extends BaseValueBuilder<GValue, GOwner> {

			protected abstract GKey getKey();

			@Override
			public GValue get() {
				return BaseMapBuilder.this.get().get(this.getKey());
			}

			public boolean getBoolean() {
				return Boolean.TRUE.equals(get());
			}

			@Override
			public void set(final GValue value) {
				BaseMapBuilder.this.put(this.getKey(), value);
			}

			@Override
			public GOwner clear() {
				return BaseMapBuilder.this.pop(this.getKey());
			}

			@Override
			public GOwner owner() {
				return BaseMapBuilder.this.owner();
			}

		}

		@SuppressWarnings ("javadoc")
		protected void putImpl(final Entry<? extends GKey, ? extends GValue> entry) {
			this.get().put(entry.getKey(), entry.getValue());
		}

		@SuppressWarnings ("javadoc")
		protected <GKey2 extends GKey> void putKeyImpl(final Getter<? super GKey2, ? extends GValue> toValue, final GKey2 key) {
			this.get().put(key, toValue.get(key));
		}

		@SuppressWarnings ("javadoc")
		protected <GValue2 extends GValue> void putValueImpl(final Getter<? super GValue2, ? extends GKey> toKey, final GValue2 value) {
			this.get().put(toKey.get(value), value);
		}

		@SuppressWarnings ("javadoc")
		protected void putInverseImpl(final Entry<? extends GValue, ? extends GKey> entry) {
			this.get().put(entry.getValue(), entry.getKey());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entry Eintrag.
		 * @return {@link #owner()}. */
		public GOwner put(final Entry<? extends GKey, ? extends GValue> entry) {
			this.putImpl(entry);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@link #owner()} zurück.
		 *
		 * @param key Schlüssel des Eintrags.
		 * @param value Wert des Eintrags.
		 * @return {@link #owner()}. */
		public GOwner put(final GKey key, final GValue value) {
			this.get().put(key, value);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toValue {@link Getter} zur Ermittlung des Werts zum gegebenen Schlüssel des Eintrags.
		 * @param key Schlüssel des Eintrags.
		 * @return {@link #owner()}. */
		public <GKey2 extends GKey> GOwner putKey(final Getter<? super GKey2, ? extends GValue> toValue, final GKey2 key) {
			this.putKeyImpl(toValue, key);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toKey {@link Getter} zur Ermittlung des Schlüssels zum gegebenen Wert des Eintrags.
		 * @param value Wert des Eintrags.
		 * @return {@link #owner()}. */
		public <GValue2 extends GValue> GOwner putValue(final Getter<? super GValue2, ? extends GKey> toKey, final GValue2 value) {
			this.putValueImpl(toKey, value);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt den gegebenen Eintrag invertiert ein} und gibt {@link #owner()} zurück. Schlüssel und Wert des
		 * Eintrags werden dazu als Wert bzw. Schlüssel verwendet.
		 *
		 * @param entry Eintrag.
		 * @return {@link #owner()}. */
		public GOwner putInverse(final Entry<? extends GValue, ? extends GKey> entry) {
			this.putInverseImpl(entry);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Map<? extends GKey, ? extends GValue> entries) {
			return this.putAll(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Iterator<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			while (entries.hasNext()) {
				this.putImpl(entries.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			return this.putAll(entries.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAll(final GKey[] keys, final GValue[] values) {
			return this.putAll(Arrays.asList(keys), Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Iterator<? extends GKey> keys, final Iterator<? extends GValue> values) {
			while (keys.hasNext() && values.hasNext()) {
				this.put(keys.next(), values.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAll(final Iterable<? extends GKey> keys, final Iterable<? extends GValue> values) {
			return this.putAll(keys.iterator(), values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toValue {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@link #owner()}. */
		@SafeVarargs
		public final <GKey2 extends GKey> GOwner putAllKeys(final Getter<? super GKey2, ? extends GValue> toValue, final GKey2... keys) {
			return this.putAllKeys(toValue, Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toValue {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@link #owner()}. */
		public <GKey2 extends GKey> GOwner putAllKeys(final Getter<? super GKey2, ? extends GValue> toValue, final Iterator<? extends GKey2> keys) {
			while (keys.hasNext()) {
				this.putKeyImpl(toValue, keys.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toValue {@link Getter} zur Ermittlung der Werte zu den gegebenen Schlüsseln der Einträge.
		 * @param keys Schlüssel der Einträge.
		 * @return {@link #owner()}. */
		public <GKey2 extends GKey> GOwner putAllKeys(final Getter<? super GKey2, ? extends GValue> toValue, final Iterable<? extends GKey2> keys) {
			return this.putAllKeys(toValue, keys.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toKey {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@link #owner()}. */
		@SafeVarargs
		public final <GValue2 extends GValue> GOwner putAllValues(final Getter<? super GValue2, ? extends GKey> toKey, final GValue2... values) {
			return this.putAllValues(toKey, Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toKey {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@link #owner()}. */
		public <GValue2 extends GValue> GOwner putAllValues(final Getter<? super GValue2, ? extends GKey> toKey, final Iterator<? extends GValue2> values) {
			while (values.hasNext()) {
				this.putValueImpl(toKey, values.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge ein} und gibt {@link #owner()} zurück.
		 *
		 * @param toKey {@link Getter} zur Ermittlung der Schlüssel zu den gegebenen Werten der Einträge.
		 * @param values Werte der Einträge.
		 * @return {@link #owner()}. */
		public <GValue2 extends GValue> GOwner putAllValues(final Getter<? super GValue2, ? extends GKey> toKey, final Iterable<? extends GValue2> values) {
			return this.putAllValues(toKey, values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAllInverse(final Map<? extends GValue, ? extends GKey> entries) {
			return this.putAllInverse(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAllInverse(final Iterator<? extends Entry<? extends GValue, ? extends GKey>> entries) {
			while (entries.hasNext()) {
				this.putInverseImpl(entries.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt die gegebenen Einträge invertiert ein} und gibt {@link #owner()} zurück.
		 *
		 * @param entries Einträge.
		 * @return {@link #owner()}. */
		public GOwner putAllInverse(final Iterable<? extends Entry<? extends GValue, ? extends GKey>> entries) {
			return this.putAllInverse(entries.iterator());
		}

		/** Diese Methode {@link Map#remove(Object) entfern den Eintrag mit dem gegebenen Schlüssel} und gibt {@link #owner()} zurück.
		 *
		 * @param key Schlüssel.
		 * @return {@link #owner()}. */
		public GOwner pop(final Object key) {
			this.get().remove(key);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@link #owner()} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@link #owner()}. */
		@SafeVarargs
		public final GOwner popAll(final Object... keys) {
			return this.popAll(Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@link #owner()} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@link #owner()}. */
		public GOwner popAll(final Iterator<?> keys) {
			while (keys.hasNext()) {
				this.get().remove(keys.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern die Einträge mit dem gegebenen Schlüssel} und gibt {@link #owner()} zurück.
		 *
		 * @param keys Schlüssel der Einträge.
		 * @return {@link #owner()}. */
		public GOwner popAll(final Iterable<?> keys) {
			return this.popAll(keys.iterator());
		}

		@Override
		public void set(final GResult value) {
			this.clear();
			this.putAll(value);
		}

		/** Diese Methode {@link Map#clear() entfern alle Einträge} und gibt {@link #owner()} zurück.
		 *
		 * @return {@link #owner()}. */
		public GOwner clear() {
			this.get().clear();
			return this.owner();
		}

		/** Diese Methode liefert den Konfigurator für den Wert zum gegebenen Schlüssel.
		 *
		 * @param key Schlüssel.
		 * @return Konfigurator. */
		public ValueProxy forKey(final GKey key) {
			return new ValueProxy() {

				@Override
				protected GKey getKey() {
					return key;
				}

			};
		}

		@Override
		public Iterator2<Entry<GKey, GValue>> iterator() {
			return Iterators.from(this.get().entrySet().iterator());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert. Der {@link #iterator()} liefert diesen, sofern er nicht {@code null} ist.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class BaseValueBuilder<GValue, GOwner> extends BaseBuilder2<GValue, GOwner> implements Iterable<GValue> {

		/** Diese Methode liefert den {@link #get() Wert}. */
		public GValue getValue() {
			return this.get();
		}

		/** Diese Methode {@link #set(Object) setzt} den Wert und gibt {@link #owner()} zurück. */
		public GOwner useValue(final GValue value) {
			this.set(value);
			return this.owner();
		}

		/** Diese Methode {@link #useValue(Object) setzt} den Wert auf {@code null} und gibt {@link #owner()} zurück. */
		public GOwner clear() {
			return this.useValue(null);
		}

		@Override
		public Iterator2<GValue> iterator() {
			final GValue result = this.get();
			if (result == null) return Iterators.empty();
			return Iterators.fromItem(result);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ des {@link Set}. */
	public static class SetBuilder<GItem, GResult extends Set<GItem>> extends BaseSetBuilder<GItem, GResult, SetBuilder<GItem, GResult>> {

		private final GResult result;

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

		/** Dieser Konstruktor initialisiert das {@link Set}.
		 *
		 * @param result {@link Set}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public SetBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.notNull(result);
		}

		@Override
		public GResult get() {
			return this.result;
		}

		@Override
		public SetBuilder<GItem, GResult> owner() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Vereinigungsmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#union(Set, Set)
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public SetBuilder<GItem, Set<GItem>> toUnion(final Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.union(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für das Kartesische Produkt dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#cartesian(Set, Set)
		 * @param <GItem2> Typ der Elemente in der zweiten Menge.
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code cartesianSet}. */
		public <GItem2> SetBuilder<Entry<GItem, GItem2>, Set<Entry<GItem, GItem2>>> toCartesian(final Set<? extends GItem2> items) {
			return SetBuilder.from(bee.creative.util.Collections.cartesian(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Schnittmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @see bee.creative.util.Collections#intersect(Set, Set)
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@code intersectionSet}. */
		public SetBuilder<GItem, Set<GItem>> toIntersection(final Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.intersect(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public SetBuilder<GItem, Set<GItem>> toChecked(final Class<GItem> itemClazz) {
			return SetBuilder.from(Collections.checkedSet(this.get(), itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @see bee.creative.util.Collections#translate(Set, Translator)
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code translatedSet}. */
		public <GItem2> SetBuilder<GItem2, Set<GItem2>> toTranslated(final Translator<GItem, GItem2> itemTranslator) {
			return SetBuilder.from(bee.creative.util.Collections.translate(this.get(), itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die threadsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code synchronizedSet}. */
		public SetBuilder<GItem, Set<GItem>> toSynchronized() {
			return SetBuilder.from(Collections.synchronizedSet(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die unveränderliche {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code unmodifiableSet}. */
		public SetBuilder<GItem, Set<GItem>> toUnmodifiable() {
			return SetBuilder.from(Collections.unmodifiableSet(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link List}. */
	public static class ListBuilder<GItem, GResult extends List<GItem>> extends BaseSetBuilder<GItem, GResult, ListBuilder<GItem, GResult>> {

		private final GResult result;

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

		/** Dieser Konstruktor initialisiert das {@link List}.
		 *
		 * @param result {@link List}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public ListBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.notNull(result);
		}

		@Override
		public GResult get() {
			return this.result;
		}

		@Override
		public ListBuilder<GItem, GResult> owner() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die rückwärts geordnete {@link #get() Liste} zurück.
		 *
		 * @see bee.creative.util.Collections#reverse(List)
		 * @return neuer {@link ListBuilder} zur {@code reverseList}. */
		public ListBuilder<GItem, List<GItem>> reverse() {
			return ListBuilder.from(bee.creative.util.Collections.reverse(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @see bee.creative.util.Collections#concat(List, List)
		 * @param items zweite Liste.
		 * @return neuer {@link ListBuilder} zur {@code chained}. */
		public ListBuilder<GItem, List<GItem>> toChained(final List<GItem> items) {
			return ListBuilder.from(bee.creative.util.Collections.concat(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @see bee.creative.util.Collections#concat(List, List, boolean)
		 * @param items zweite Liste.
		 * @param extendMode Erweiterungsmodus.
		 * @return neuer {@link ListBuilder} zur {@code chained}. */
		public ListBuilder<GItem, List<GItem>> toChained(final List<GItem> items, final boolean extendMode) {
			return ListBuilder.from(bee.creative.util.Collections.concat(this.get(), items, extendMode));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#checkedList(List, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code checkedList}. */
		public ListBuilder<GItem, List<GItem>> toChecked(final Class<GItem> itemClazz) {
			return ListBuilder.from(java.util.Collections.checkedList(this.get(), itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @see bee.creative.util.Collections#translate(List, Translator)
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code translatedList}. */
		public <GItem2> ListBuilder<GItem2, List<GItem2>> toTranslated(final Translator<GItem, GItem2> itemTranslator) {
			return ListBuilder.from(bee.creative.util.Collections.translate(this.get(), itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die threadsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#synchronizedList(List)
		 * @return neuer {@link ListBuilder} zur {@code synchronizedList}. */
		public ListBuilder<GItem, List<GItem>> toSynchronized() {
			return ListBuilder.from(java.util.Collections.synchronizedList(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die unveränderliche {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#unmodifiableList(List)
		 * @return neuer {@link ListBuilder} zur {@code unmodifiableList}. */
		public ListBuilder<GItem, List<GItem>> toUnmodifiable() {
			return ListBuilder.from(java.util.Collections.unmodifiableList(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}. */
	public static class MapBuilder<GKey, GValue, GResult extends Map<GKey, GValue>>
		extends BaseMapBuilder<GKey, GValue, GResult, MapBuilder<GKey, GValue, GResult>> {

		private final GResult result;

		/** Diese Methode gibt einen {@link MapBuilder} zur gegebenen {@link Map} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param <GResult> Typ der {@link Map}.
		 * @param result {@link Map}.
		 * @return {@link MapBuilder}. */
		public static <GKey, GItem, GResult extends Map<GKey, GItem>> MapBuilder<GKey, GItem, GResult> from(final GResult result) {
			return new MapBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} mit natürlicher Ordnung zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, TreeMap<GKey, GItem>> forTreeMap() {
			return MapBuilder.forTreeMap(null);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param comparator Ordnung der Schlüssel.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, TreeMap<GKey, GItem>> forTreeMap(final Comparator<? super GKey> comparator) {
			return MapBuilder.from(new TreeMap<GKey, GItem>(comparator));
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} mit Steuwertpuffer zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, HashMap<GKey, GItem>> forHashMap() {
			return MapBuilder.forHashMap(true);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GItem> MapBuilder<GKey, GItem, HashMap<GKey, GItem>> forHashMap(final boolean withHashCache) {
			return MapBuilder.from(withHashCache ? new HashMap2<GKey, GItem>() : new HashMap<GKey, GItem>());
		}

		/** Dieser Konstruktor initialisiert die interne {@link Map}.
		 *
		 * @param result interne {@link Map}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public MapBuilder(final GResult result) throws NullPointerException {
			this.result = Objects.notNull(result);
		}

		@Override
		public GResult get() {
			return this.result;
		}

		@Override
		public MapBuilder<GKey, GValue, GResult> owner() {
			return this;
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur datentypsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return neuer {@link MapBuilder} zur {@code checkedMap}. */
		public MapBuilder<GKey, GValue, Map<GKey, GValue>> toChecked(final Class<GKey> keyClazz, final Class<GValue> valueClazz) {
			return MapBuilder.from(Collections.checkedMap(this.get(), keyClazz, valueClazz));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur übersetzten {@link #get() Abbildung} zurück.
		 *
		 * @see bee.creative.util.Collections#translate(Map, Translator, Translator)
		 * @param <GKey2> Typ der übersetzten Schlüssel.
		 * @param <GValue2> Typ der übersetzten Werte.
		 * @param keyTranslator {@link Translator} zur Übersetzung der Schlüssel.
		 * @param valueTranslator {@link Translator} zur Übersetzung der Werte.
		 * @return neuer {@link MapBuilder} zur {@code translatedMap}. */
		public <GKey2, GValue2> MapBuilder<GKey2, GValue2, Map<GKey2, GValue2>> toTranslated(final Translator<GKey, GKey2> keyTranslator,
			final Translator<GValue, GValue2> valueTranslator) {
			return MapBuilder.from(bee.creative.util.Collections.translate(this.get(), keyTranslator, valueTranslator));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur threadsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code synchronizedMap}. */
		public MapBuilder<GKey, GValue, Map<GKey, GValue>> toSynchronized() {
			return MapBuilder.from(Collections.synchronizedMap(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur unveränderliche {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code unmodifiableMap}. */
		public MapBuilder<GKey, GValue, Map<GKey, GValue>> toUnmodifiable() {
			return MapBuilder.from(Collections.unmodifiableMap(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für einen Wert.
	 *
	 * @param <GValue> Typ des Werts. */
	public static abstract class ValueBuilder<GValue> extends BaseValueBuilder<GValue, ValueBuilder<GValue>> {

		public static <GValue> ValueBuilder<GValue> from(final Property<GValue> result) {
			return new ValueBuilder<GValue>() {

				@Override
				public GValue get() {
					return result.get();
				}

				@Override
				public void set(final GValue value) {
					result.set(value);
				}

			};
		}

		@Override
		public ValueBuilder<GValue> owner() {
			return this;
		}

	}

}
