package bee.creative.util;

import static bee.creative.util.Iterators.emptyIterator;
import static bee.creative.util.Iterators.iteratorFrom;
import static bee.creative.util.Iterators.iteratorFromItem;
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
import bee.creative.util.Collections.CartesianSet;
import bee.creative.util.Collections.ConcatList;
import bee.creative.util.Collections.IntersectSet;
import bee.creative.util.Collections.ReversedList;
import bee.creative.util.Collections.TranslatedList;
import bee.creative.util.Collections.TranslatedSet;
import bee.creative.util.Collections.UnionSet;

/** Diese Klasse implementiert grundlegende {@link Producer Konfiguratoren}.
 *
 * @author [cc-by] 2018 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Builders {

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Erzeugung eines Datensatzes.
	 *
	 * @param <V> Typ des Datensatzes.
	 * @param <O> Typ des Besitzers. */
	public static abstract class BaseBuilder<V, O> implements Producer<V> {

		/** Diese Methode gibt den Besitzer dieses Konfigurators zurück. Bei selbsständigen Konfiguratoren ist dieser {@code this}.
		 *
		 * @return Besitzer. */
		public abstract O owner();

		@Override
		public String toString() {
			return Objects.toString(true, this.get());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator zur Anpassung eines Datensatzes.
	 *
	 * @param <V> Typ des Datensatzes.
	 * @param <O> Typ des Besitzers. */
	public static abstract class BaseBuilder2<V, O> extends BaseBuilder<V, O> implements Property<V> {

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt den {@link #owner() Besitzer} zurück. */
		public O use(BaseBuilder<? extends V, ?> source) throws NullPointerException {
			this.set(source.get());
			return this.owner();
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Collection}.
	 *
	 * @param <E> Typ der Elemente.
	 * @param <R> Typ der {@link Collection}.
	 * @param <O> Typ des Besitzers. */
	public static abstract class BaseSetBuilder<E, R extends Collection<E>, O> extends BaseBuilder2<R, O> implements Iterable<E> {

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @see #clear()
		 * @see #addAll(Iterable) */
		@Override
		public void set(R value) throws NullPointerException {
			this.clear();
			this.addAll(value);
		}

		/** Diese Methode {@link Collection#add(Object) fügt} das gegebene Element hinzu und gibt den {@link #owner() Besitzer} zurück. */
		public O add(E item) {
			this.get().add(item);
			return this.owner();
		}

		/** Diese Methode {@link Collection#add(Object) fügt} die gegebenen Elemente hinzu und gibt den {@link #owner() Besitzer} zurück. */
		@SafeVarargs
		public final O addAll(E... items) {
			return this.addAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#add(Object) fügt} die gegebenen Elemente hinzu und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @param items Elemente.
		 * @return den {@link #owner() Besitzer}. */
		public O addAll(Iterator<? extends E> items) {
			Iterators.addAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Collection#add(Object) fügt} die gegebenen Element hinzu und gibt den {@link #owner() Besitzer} zurück. */
		public O addAll(Iterable<? extends E> items) {
			Iterables.addAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern} das gegebene Element und gibt den {@link #owner() Besitzer} zurück. */
		public O remove(Object key) {
			this.get().remove(key);
			return this.owner();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern} die gegebenen Elemente und gibt den {@link #owner() Besitzer} zurück. */
		@SafeVarargs
		public final O removeAll(Object... items) {
			return this.removeAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#remove(Object) entfern} die gegebenen Elemente und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @param items Elemente.
		 * @return den {@link #owner() Besitzer}. */
		public O removeAll(Iterator<?> items) {
			Iterators.removeAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern} die gegebenen Elemente und gibt den {@link #owner() Besitzer} zurück. */
		public O removeAll(Iterable<?> items) {
			Iterables.removeAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Collection#clear() entfern} alle Elemente und gibt den {@link #owner() Besitzer} zurück. */
		public O clear() {
			this.get().clear();
			return this.owner();
		}

		@Override
		public Iterator3<E> iterator() {
			return iteratorFrom(this.get().iterator());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für eine {@link Map}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 * @param <R> Typ der {@link Map}.
	 * @param <O> Typ des Besitzers. */
	public static abstract class BaseMapBuilder<K, V, R extends Map<K, V>, O> extends BaseBuilder2<R, O> implements Iterable<Entry<K, V>> {

		/** Diese Methode {@link Map#put(Object, Object) fügt} den gegebenen Eintrag ein und gibt den {@link #owner() Besitzer} zurück. */
		public O put(Entry<? extends K, ? extends V> entry) {
			this.putImpl(entry);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den über Schlüssel und Wert gegebenen Eintrag ein und gibt den {@link #owner() Besitzer} zurück. */
		public O put(K key, V value) {
			this.get().put(key, value);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den über Schlüssel und {@link Getter} gegebenen Eintrag ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung des Werts zum gegebenen Schlüssel. */
		public <K2 extends K> O putKey(Getter<? super K2, ? extends V> toValue, K2 key) {
			this.putKeyImpl(toValue, key);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den über {@link Getter} und Wert gegebenen Eintrag ein und gibt den {@link #owner() Besitzer} zurück.
		 * Der {@link Getter} dient der Ermittlung des Schlüssels zum gegebenen Wert. */
		public <V2 extends V> O putValue(Getter<? super V2, ? extends K> toKey, V2 value) {
			this.putValueImpl(toKey, value);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den gegebenen Eintrag invertiert ein und gibt den {@link #owner() Besitzer} zurück. Schlüssel und
		 * Wert des Eintrags werden dazu als Wert bzw. Schlüssel verwendet. */
		public O putInverse(Entry<? extends V, ? extends K> entry) {
			this.putInverseImpl(entry);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAll(Map<? extends K, ? extends V> entries) {
			return this.putAll(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAll(Iterator<? extends Entry<? extends K, ? extends V>> entries) {
			while (entries.hasNext()) {
				this.putImpl(entries.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAll(Iterable<? extends Entry<? extends K, ? extends V>> entries) {
			return this.putAll(entries.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAll(K[] keys, V[] values) {
			return this.putAll(Arrays.asList(keys), Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAll(Iterator<? extends K> keys, Iterator<? extends V> values) {
			while (keys.hasNext() && values.hasNext()) {
				this.put(keys.next(), values.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAll(Iterable<? extends K> keys, Iterable<? extends V> values) {
			return this.putAll(keys.iterator(), values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und {@link Getter} gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Werte zu den gegebenen Schlüsseln. */
		@SafeVarargs
		public final <K2 extends K> O putAllKeys(Getter<? super K2, ? extends V> toValue, K2... keys) {
			return this.putAllKeys(toValue, Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und {@link Getter} gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Werte zu den gegebenen Schlüsseln. */
		public <K2 extends K> O putAllKeys(Getter<? super K2, ? extends V> toValue, Iterator<? extends K2> keys) {
			while (keys.hasNext()) {
				this.putKeyImpl(toValue, keys.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und {@link Getter} gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Werte zu den gegebenen Schlüsseln. */
		public <K2 extends K> O putAllKeys(Getter<? super K2, ? extends V> toValue, Iterable<? extends K2> keys) {
			return this.putAllKeys(toValue, keys.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über {@link Getter} und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Schlüssel zu den gegebenen Werten. */
		@SafeVarargs
		public final <V2 extends V> O putAllValues(Getter<? super V2, ? extends K> toKey, V2... values) {
			return this.putAllValues(toKey, Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über {@link Getter} und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Schlüssel zu den gegebenen Werten. */
		public <V2 extends V> O putAllValues(Getter<? super V2, ? extends K> toKey, Iterator<? extends V2> values) {
			while (values.hasNext()) {
				this.putValueImpl(toKey, values.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über {@link Getter} und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Schlüssel zu den gegebenen Werten. */
		public <V2 extends V> O putAllValues(Getter<? super V2, ? extends K> toKey, Iterable<? extends V2> values) {
			return this.putAllValues(toKey, values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge invertiert ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAllInverse(Map<? extends V, ? extends K> entries) {
			return this.putAllInverse(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge invertiert ein und gibt den {@link #owner() Besitzer} zurück. **/
		public O putAllInverse(Iterator<? extends Entry<? extends V, ? extends K>> entries) {
			while (entries.hasNext()) {
				this.putInverseImpl(entries.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge invertiert ein und gibt den {@link #owner() Besitzer} zurück. */
		public O putAllInverse(Iterable<? extends Entry<? extends V, ? extends K>> entries) {
			return this.putAllInverse(entries.iterator());
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		public O pop(Object key) {
			this.get().remove(key);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		@SafeVarargs
		public final O popAll(Object... keys) {
			return this.popAll(Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		public O popAll(Iterator<?> keys) {
			Iterators.removeAll(this.get().keySet(), keys);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		public O popAll(Iterable<?> keys) {
			Iterables.removeAll(this.get().keySet(), keys);
			return this.owner();
		}

		@Override
		public void set(R value) {
			this.clear();
			this.putAll(value);
		}

		/** Diese Methode {@link Map#clear() entfern alle Einträge} und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @return den {@link #owner() Besitzer}. */
		public O clear() {
			this.get().clear();
			return this.owner();
		}

		/** Diese Methode liefert den Konfigurator für den Wert zum gegebenen Schlüssel.
		 *
		 * @param key Schlüssel.
		 * @return Konfigurator. */
		public MapValueBuilder forKey(K key) {
			return new MapValueBuilder() {

				@Override
				protected K getKey() {
					return key;
				}

			};
		}

		@Override
		public Iterator3<Entry<K, V>> iterator() {
			return iteratorFrom(this.get().entrySet().iterator());
		}

		/** Diese Klasse implementiert den Konfigurator für den {@link Entry#getValue() Wert} eines Eintrags der {@link BaseMapBuilder#get() Abbildung}. */
		public abstract class MapValueBuilder extends BaseValueBuilder<V, O> {

			@Override
			public V get() {
				return BaseMapBuilder.this.get().get(this.getKey());
			}

			/** Diese Methode liefert nur dann {@code true}, wenn der Wert {@link Boolean#TRUE} ist. */
			public boolean getBoolean() {
				return Boolean.TRUE.equals(this.get());
			}

			@Override
			public void set(V value) {
				BaseMapBuilder.this.put(this.getKey(), value);
			}

			@Override
			public O clear() {
				return BaseMapBuilder.this.pop(this.getKey());
			}

			@Override
			public O owner() {
				return BaseMapBuilder.this.owner();
			}

			/** Diese Methode liefert den {@link Entry#getKey() Schlüssel} des Eintrags. */
			protected abstract K getKey();

		}

		void putImpl(Entry<? extends K, ? extends V> entry) {
			this.get().put(entry.getKey(), entry.getValue());
		}

		<K2 extends K> void putKeyImpl(Getter<? super K2, ? extends V> toValue, K2 key) {
			this.get().put(key, toValue.get(key));
		}

		<V2 extends V> void putValueImpl(Getter<? super V2, ? extends K> toKey, V2 value) {
			this.get().put(toKey.get(value), value);
		}

		void putInverseImpl(Entry<? extends V, ? extends K> entry) {
			this.get().put(entry.getValue(), entry.getKey());
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert. Der {@link #iterator()} liefert diesen, sofern er nicht {@code null} ist.
	 *
	 * @param <V> Typ des Werts.
	 * @param <O> Typ des Besitzers. */
	public static abstract class BaseValueBuilder<V, O> extends BaseBuilder2<V, O> implements Iterable<V> {

		/** Diese Methode liefert den {@link #get() Wert}. */
		public V getValue() {
			return this.get();
		}

		/** Diese Methode {@link #set(Object) setzt} den Wert und gibt den {@link #owner() Besitzer} zurück. */
		public O useValue(V value) {
			this.set(value);
			return this.owner();
		}

		/** Diese Methode {@link #useValue(Object) setzt} den Wert auf {@code null} und gibt den {@link #owner() Besitzer} zurück. */
		public O clear() {
			return this.useValue(null);
		}

		@Override
		public Iterator3<V> iterator() {
			var item = this.get();
			return item == null ? emptyIterator() : iteratorFromItem(item);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <E> Typ der Elemente.
	 * @param <R> Typ des {@link Set}. */
	public static abstract class SetBuilder<E, R extends Set<E>, O> extends BaseSetBuilder<E, R, O> {

		/** Diese Methode gibt einen {@link SetBuilder} zum gegebenen {@link Set} zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @param result {@link Set}.
		 * @return {@link SetBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <E> ItemSetBuilder<E> from(Set<E> result) throws NullPointerException {
			return new ItemSetBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} mit natürlicher Ordnung zurück. */
		public static <E> TreeSetBuilder<E> forTreeSet() {
			return SetBuilder.forTreeSet(null);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @param comparator Ordnung der Elemente.
		 * @return {@link SetBuilder} eines {@link TreeSet}. */
		public static <E> TreeSetBuilder<E> forTreeSet(Comparator<? super E> comparator) {
			return new TreeSetBuilder<>(new TreeSet<>(comparator));
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} mit Steuwertpuffer zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <E> HashSetBuilder<E> forHashSet() {
			return SetBuilder.forHashSet(true);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <E> HashSetBuilder<E> forHashSet(boolean withHashCache) {
			return new HashSetBuilder<>(withHashCache ? new HashSet2<E>() : new HashSet<E>());
		}

		/** Dieser Konstruktor initialisiert das {@link Set}. */
		public SetBuilder(R value) throws NullPointerException {
			this.value = Objects.notNull(value);
		}

		@Override
		public R get() {
			return this.value;
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Vereinigungsmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@link UnionSet}. */
		public ItemSetBuilder<E> toUnited(Set<? extends E> items) {
			return SetBuilder.from(bee.creative.util.Collections.<E>unionSet(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für das Kartesische Produkt dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @param <E2> Typ der Elemente in der zweiten Menge.
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@link CartesianSet}. */
		public <E2> ItemSetBuilder<Entry<E, E2>> toCrossed(Set<? extends E2> items) {
			return SetBuilder.from(bee.creative.util.Collections.cartesianSet(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Schnittmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@link IntersectSet}. */
		public ItemSetBuilder<E> toIntersected(Set<? extends E> items) {
			return SetBuilder.from(bee.creative.util.Collections.intersectSet(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public ItemSetBuilder<E> toChecked(Class<E> itemClazz) {
			return SetBuilder.from(Collections.checkedSet(this.get(), itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @param <E2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link SetBuilder} zum {@link TranslatedSet}. */
		public <E2> ItemSetBuilder<E2> toTranslated(Translator<E, E2> itemTranslator) {
			return SetBuilder.from(bee.creative.util.Collections.translatedSet(this.get(), itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die threadsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code synchronizedSet}. */
		public ItemSetBuilder<E> toSynchronized() {
			return SetBuilder.from(Collections.synchronizedSet(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die unveränderliche {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code unmodifiableSet}. */
		public ItemSetBuilder<E> toUnmodifiable() {
			return SetBuilder.from(Collections.unmodifiableSet(this.get()));
		}

		final R value;

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ItemSetBuilder<E> extends SetBuilder<E, Set<E>, ItemSetBuilder<E>> {

		@Override
		public ItemSetBuilder<E> owner() {
			return this;
		}

		ItemSetBuilder(Set<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link TreeSet}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class TreeSetBuilder<E> extends SetBuilder<E, TreeSet<E>, TreeSetBuilder<E>> {

		@Override
		public TreeSetBuilder<E> owner() {
			return this;
		}

		TreeSetBuilder(TreeSet<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link HashSet}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class HashSetBuilder<E> extends SetBuilder<E, HashSet<E>, HashSetBuilder<E>> {

		@Override
		public HashSetBuilder<E> owner() {
			return this;
		}

		HashSetBuilder(HashSet<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link ProxySet}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ProxySetBuilder<E> extends SetBuilder<E, ProxySet<E>, ProxySetBuilder<E>> {

		@Override
		public ProxySetBuilder<E> owner() {
			return this;
		}

		ProxySetBuilder(ProxySet<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <E> Typ der Elemente.
	 * @param <R> Typ der {@link List}. */
	public static abstract class ListBuilder<E, R extends List<E>, O> extends BaseSetBuilder<E, R, O> {

		/** Diese Methode gibt einen {@link ListBuilder} zur gegebenen {@link List} zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @param result {@link List}.
		 * @return {@link ListBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <E> ItemListBuilder<E> from(List<E> result) throws NullPointerException {
			return new ItemListBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link ArrayList} zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link ArrayList}. */
		public static <E> ArrayListBuilder<E> forArrayList() {
			return new ArrayListBuilder<>(new ArrayList<E>());
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link LinkedList} mit zurück.
		 *
		 * @param <E> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link LinkedList}. */
		public static <E> LinkedListBuilder<E> forLinkedList() {
			return new LinkedListBuilder<>(new LinkedList<E>());
		}

		/** Dieser Konstruktor initialisiert die {@link List}. */
		public ListBuilder(R value) throws NullPointerException {
			this.value = Objects.notNull(value);
		}

		@Override
		public R get() {
			return this.value;
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die rückwärts geordnete {@link #get() Liste} zurück.
		 *
		 * @return neuer {@link ListBuilder} zur {@link ReversedList}. */
		public ItemListBuilder<E> toReverse() {
			return ListBuilder.from(bee.creative.util.Collections.reversedList(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @param items zweite Liste.
		 * @return neuer {@link ListBuilder} zur {@link ConcatList}. */
		public ItemListBuilder<E> toChained(List<E> items) {
			return ListBuilder.from(bee.creative.util.Collections.concatList(this.get(), items, true));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @param items zweite Liste.
		 * @param extendMode Erweiterungsmodus.
		 * @return neuer {@link ListBuilder} zur {@link ConcatList}. */
		public ItemListBuilder<E> toChained(List<E> items, boolean extendMode) {
			return ListBuilder.from(bee.creative.util.Collections.concatList(this.get(), items, extendMode));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#checkedList(List, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code checkedList}. */
		public ItemListBuilder<E> toChecked(Class<E> itemClazz) {
			return ListBuilder.from(java.util.Collections.checkedList(this.get(), itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @param <E2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link ListBuilder} zur {@link TranslatedList}. */
		public <E2> ItemListBuilder<E2> toTranslated(Translator<E, E2> itemTranslator) {
			return ListBuilder.from(bee.creative.util.Collections.translatedList(this.get(), itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die threadsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#synchronizedList(List)
		 * @return neuer {@link ListBuilder} zur {@code synchronizedList}. */
		public ItemListBuilder<E> toSynchronized() {
			return ListBuilder.from(java.util.Collections.synchronizedList(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die unveränderliche {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#unmodifiableList(List)
		 * @return neuer {@link ListBuilder} zur {@code unmodifiableList}. */
		public ItemListBuilder<E> toUnmodifiable() {
			return ListBuilder.from(java.util.Collections.unmodifiableList(this.get()));
		}

		final R value;

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ItemListBuilder<E> extends ListBuilder<E, List<E>, ItemListBuilder<E>> {

		@Override
		public ItemListBuilder<E> owner() {
			return this;
		}

		ItemListBuilder(List<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link ArrayList}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ArrayListBuilder<E> extends ListBuilder<E, ArrayList<E>, ArrayListBuilder<E>> {

		@Override
		public ArrayListBuilder<E> owner() {
			return this;
		}

		ArrayListBuilder(ArrayList<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link LinkedList}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class LinkedListBuilder<E> extends ListBuilder<E, LinkedList<E>, LinkedListBuilder<E>> {

		@Override
		public LinkedListBuilder<E> owner() {
			return this;
		}

		LinkedListBuilder(LinkedList<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link ProxyList}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ProxyListBuilder<E> extends ListBuilder<E, ProxyList<E>, ProxyListBuilder<E>> {

		@Override
		public ProxyListBuilder<E> owner() {
			return this;
		}

		ProxyListBuilder(ProxyList<E> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte.
	 * @param <R> Typ der {@link Map}. */
	public static abstract class MapBuilder<K, V, R extends Map<K, V>, O> extends BaseMapBuilder<K, V, R, O> {

		/** Diese Methode gibt einen {@link ItemMapBuilder} zur gegebenen {@link Map} zurück.
		 *
		 * @param <K> Typ der Schlüssel.
		 * @param <V> Typ der Werte.
		 * @param result {@link Map}.
		 * @return {@link MapBuilder}. */
		public static <K, V> ItemMapBuilder<K, V> from(Map<K, V> result) {
			return new ItemMapBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} mit natürlicher Ordnung zurück.
		 *
		 * @param <K> Typ der Schlüssel.
		 * @param <E> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <K, E> TreeMapBuilder<K, E> forTreeMap() {
			return MapBuilder.forTreeMap(null);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} zurück.
		 *
		 * @param <K> Typ der Schlüssel.
		 * @param <E> Typ der Werte.
		 * @param comparator Ordnung der Schlüssel.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <K, E> TreeMapBuilder<K, E> forTreeMap(Comparator<? super K> comparator) {
			return new TreeMapBuilder<>(new TreeMap<K, E>(comparator));
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} mit Steuwertpuffer zurück.
		 *
		 * @param <K> Typ der Schlüssel.
		 * @param <E> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <K, E> HashMapBuilder<K, E> forHashMap() {
			return MapBuilder.forHashMap(true);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} zurück.
		 *
		 * @param <K> Typ der Schlüssel.
		 * @param <E> Typ der Werte.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <K, E> HashMapBuilder<K, E> forHashMap(boolean withHashCache) {
			return new HashMapBuilder<>(withHashCache ? new HashMap2<K, E>() : new HashMap<K, E>());
		}

		/** Dieser Konstruktor initialisiert die {@link Map}. */
		public MapBuilder(R value) throws NullPointerException {
			this.value = Objects.notNull(value);
		}

		@Override
		public R get() {
			return this.value;
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur datentypsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return neuer {@link MapBuilder} zur {@code checkedMap}. */
		public ItemMapBuilder<K, V> toChecked(Class<K> keyClazz, Class<V> valueClazz) {
			return MapBuilder.from(Collections.checkedMap(this.get(), keyClazz, valueClazz));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur übersetzten {@link #get() Abbildung} zurück.
		 *
		 * @see bee.creative.util.Collections#translatedMap(Map, Translator, Translator)
		 * @param <K2> Typ der übersetzten Schlüssel.
		 * @param <V2> Typ der übersetzten Werte.
		 * @param keyTranslator {@link Translator} zur Übersetzung der Schlüssel.
		 * @param valueTranslator {@link Translator} zur Übersetzung der Werte.
		 * @return neuer {@link MapBuilder} zur {@code translatedMap}. */
		public <K2, V2> ItemMapBuilder<K2, V2> toTranslated(Translator<K, K2> keyTranslator, Translator<V, V2> valueTranslator) {
			return MapBuilder.from(bee.creative.util.Collections.translatedMap(this.get(), keyTranslator, valueTranslator));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur threadsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code synchronizedMap}. */
		public ItemMapBuilder<K, V> toSynchronized() {
			return MapBuilder.from(Collections.synchronizedMap(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur unveränderliche {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code unmodifiableMap}. */
		public ItemMapBuilder<K, V> toUnmodifiable() {
			return MapBuilder.from(Collections.unmodifiableMap(this.get()));
		}

		final R value;

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte. */
	public static class ItemMapBuilder<K, V> extends MapBuilder<K, V, Map<K, V>, ItemMapBuilder<K, V>> {

		@Override
		public ItemMapBuilder<K, V> owner() {
			return this;
		}

		ItemMapBuilder(Map<K, V> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link TreeMap}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte. */
	public static class TreeMapBuilder<K, V> extends MapBuilder<K, V, TreeMap<K, V>, TreeMapBuilder<K, V>> {

		@Override
		public TreeMapBuilder<K, V> owner() {
			return this;
		}

		TreeMapBuilder(TreeMap<K, V> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link HashMap}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte. */
	public static class HashMapBuilder<K, V> extends MapBuilder<K, V, HashMap<K, V>, HashMapBuilder<K, V>> {

		@Override
		public HashMapBuilder<K, V> owner() {
			return this;
		}

		HashMapBuilder(HashMap<K, V> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link ProxyMap}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <V> Typ der Werte. */
	public static class ProxyMapBuilder<K, V> extends MapBuilder<K, V, ProxyMap<K, V>, ProxyMapBuilder<K, V>> {

		@Override
		public ProxyMapBuilder<K, V> owner() {
			return this;
		}

		ProxyMapBuilder(ProxyMap<K, V> value) throws NullPointerException {
			super(value);
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert.
	 *
	 * @param <V> Typ des Werts.
	 * @param <O> Typ des Besitzers. */
	public static abstract class ValueBuilder<V, O> extends BaseValueBuilder<V, O> {

		/** Diese Methode gibt einen {@link ValueBuilder} zum Wert des gegebenen {@link Property} zurück.
		 *
		 * @param <V> Typ des Werts.
		 * @return {@link ValueBuilder} eines {@link Property}. */
		public static <V> ProxyValueBuilder<V> fromProxy(Property<V> that) {
			return new ProxyValueBuilder<>(that);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für den Wert eines {@link Property}.
	 *
	 * @param <V> Typ des Werts. */
	public static class ProxyValueBuilder<V> extends ValueBuilder<V, ProxyValueBuilder<V>> {

		@Override
		public V get() {
			return this.that.get();
		}

		@Override
		public void set(V value) {
			this.that.set(value);
		}

		@Override
		public ProxyValueBuilder<V> owner() {
			return this;
		}

		final Property<V> that;

		ProxyValueBuilder(Property<V> that) {
			this.that = Objects.notNull(that);
		}

	}

}
