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
import bee.creative.util.Collections.CartesianSet;
import bee.creative.util.Collections.ConcatList;
import bee.creative.util.Collections.IntersectSet;
import bee.creative.util.Collections.ReverseList;
import bee.creative.util.Collections.TranslatedList;
import bee.creative.util.Collections.TranslatedSet;
import bee.creative.util.Collections.UnionSet;

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

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner use(BaseBuilder<? extends GValue, ?> source) throws NullPointerException {
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

		/** Diese Methode übernimmt die Einstellungen des gegebenen Konfigurators und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @see #clear()
		 * @see #putAll(Iterable) */
		@Override
		public void set(GResult value) throws NullPointerException {
			this.clear();
			this.putAll(value);
		}

		/** Diese Methode {@link Collection#add(Object) fügt} das gegebene Element hinzu und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner put(GItem item) {
			this.get().add(item);
			return this.owner();
		}

		/** Diese Methode {@link Collection#add(Object) fügt} die gegebenen Elemente hinzu und gibt den {@link #owner() Besitzer} zurück. */
		@SafeVarargs
		public final GOwner putAll(GItem... items) {
			return this.putAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#add(Object) fügt} die gegebenen Elemente hinzu und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @param items Elemente.
		 * @return den {@link #owner() Besitzer}. */
		public GOwner putAll(Iterator<? extends GItem> items) {
			Iterators.addAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Collection#add(Object) fügt} die gegebenen Element hinzu und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(Iterable<? extends GItem> items) {
			Iterables.addAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern} das gegebene Element und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner pop(Object key) {
			this.get().remove(key);
			return this.owner();
		}

		/** Diese Methode {@link Collection#remove(Object) entfern} die gegebenen Elemente und gibt den {@link #owner() Besitzer} zurück. */
		@SafeVarargs
		public final GOwner popAll(Object... items) {
			return this.popAll(Arrays.asList(items));
		}

		/** Diese Methode {@link Collection#remove(Object) entfern} die gegebenen Elemente und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @param items Elemente.
		 * @return den {@link #owner() Besitzer}. */
		public GOwner popAll(Iterator<?> items) {
			Iterators.removeAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern} die gegebenen Elemente und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner popAll(Iterable<?> items) {
			Iterables.removeAll(this.get(), items);
			return this.owner();
		}

		/** Diese Methode {@link Collection#clear() entfern} alle Elemente und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner clear() {
			this.get().clear();
			return this.owner();
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.iteratorFrom(this.get().iterator());
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

		/** Diese Klasse implementiert den Konfigurator für den {@link Entry#getValue() Wert} eines Eintrags der {@link BaseMapBuilder#get() Abbildung}. */
		public abstract class ValueProxy extends BaseValueBuilder<GValue, GOwner> {

			/** Diese Methode liefert den {@link Entry#getKey() Schlüssel} des Eintrags. */
			protected abstract GKey getKey();

			@Override
			public GValue get() {
				return BaseMapBuilder.this.get().get(this.getKey());
			}

			/** Diese Methode liefert nur dann {@code true}, wenn der Wert {@link Boolean#TRUE} ist. */
			public boolean getBoolean() {
				return Boolean.TRUE.equals(this.get());
			}

			@Override
			public void set(GValue value) {
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

		void putImpl(Entry<? extends GKey, ? extends GValue> entry) {
			this.get().put(entry.getKey(), entry.getValue());
		}

		<GKey2 extends GKey> void putKeyImpl(Getter<? super GKey2, ? extends GValue> toValue, GKey2 key) {
			this.get().put(key, toValue.get(key));
		}

		<GValue2 extends GValue> void putValueImpl(Getter<? super GValue2, ? extends GKey> toKey, GValue2 value) {
			this.get().put(toKey.get(value), value);
		}

		void putInverseImpl(Entry<? extends GValue, ? extends GKey> entry) {
			this.get().put(entry.getValue(), entry.getKey());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den gegebenen Eintrag ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner put(Entry<? extends GKey, ? extends GValue> entry) {
			this.putImpl(entry);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den über Schlüssel und Wert gegebenen Eintrag ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner put(GKey key, GValue value) {
			this.get().put(key, value);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den über Schlüssel und {@link Getter} gegebenen Eintrag ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung des Werts zum gegebenen Schlüssel. */
		public <GKey2 extends GKey> GOwner putKey(Getter<? super GKey2, ? extends GValue> toValue, GKey2 key) {
			this.putKeyImpl(toValue, key);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den über {@link Getter} und Wert gegebenen Eintrag ein und gibt den {@link #owner() Besitzer} zurück.
		 * Der {@link Getter} dient der Ermittlung des Schlüssels zum gegebenen Wert. */
		public <GValue2 extends GValue> GOwner putValue(Getter<? super GValue2, ? extends GKey> toKey, GValue2 value) {
			this.putValueImpl(toKey, value);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} den gegebenen Eintrag invertiert ein und gibt den {@link #owner() Besitzer} zurück. Schlüssel und
		 * Wert des Eintrags werden dazu als Wert bzw. Schlüssel verwendet. */
		public GOwner putInverse(Entry<? extends GValue, ? extends GKey> entry) {
			this.putInverseImpl(entry);
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(Map<? extends GKey, ? extends GValue> entries) {
			return this.putAll(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(Iterator<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			while (entries.hasNext()) {
				this.putImpl(entries.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(Iterable<? extends Entry<? extends GKey, ? extends GValue>> entries) {
			return this.putAll(entries.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(GKey[] keys, GValue[] values) {
			return this.putAll(Arrays.asList(keys), Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(Iterator<? extends GKey> keys, Iterator<? extends GValue> values) {
			while (keys.hasNext() && values.hasNext()) {
				this.put(keys.next(), values.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAll(Iterable<? extends GKey> keys, Iterable<? extends GValue> values) {
			return this.putAll(keys.iterator(), values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und {@link Getter} gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Werte zu den gegebenen Schlüsseln. */
		@SafeVarargs
		public final <GKey2 extends GKey> GOwner putAllKeys(Getter<? super GKey2, ? extends GValue> toValue, GKey2... keys) {
			return this.putAllKeys(toValue, Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und {@link Getter} gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Werte zu den gegebenen Schlüsseln. */
		public <GKey2 extends GKey> GOwner putAllKeys(Getter<? super GKey2, ? extends GValue> toValue, Iterator<? extends GKey2> keys) {
			while (keys.hasNext()) {
				this.putKeyImpl(toValue, keys.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über Schlüssel und {@link Getter} gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Werte zu den gegebenen Schlüsseln. */
		public <GKey2 extends GKey> GOwner putAllKeys(Getter<? super GKey2, ? extends GValue> toValue, Iterable<? extends GKey2> keys) {
			return this.putAllKeys(toValue, keys.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über {@link Getter} und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Schlüssel zu den gegebenen Werten. */
		@SafeVarargs
		public final <GValue2 extends GValue> GOwner putAllValues(Getter<? super GValue2, ? extends GKey> toKey, GValue2... values) {
			return this.putAllValues(toKey, Arrays.asList(values));
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über {@link Getter} und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Schlüssel zu den gegebenen Werten. */
		public <GValue2 extends GValue> GOwner putAllValues(Getter<? super GValue2, ? extends GKey> toKey, Iterator<? extends GValue2> values) {
			while (values.hasNext()) {
				this.putValueImpl(toKey, values.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die über {@link Getter} und Werte gegebenen Einträge ein und gibt den {@link #owner() Besitzer}
		 * zurück. Der {@link Getter} dient der Ermittlung der Schlüssel zu den gegebenen Werten. */
		public <GValue2 extends GValue> GOwner putAllValues(Getter<? super GValue2, ? extends GKey> toKey, Iterable<? extends GValue2> values) {
			return this.putAllValues(toKey, values.iterator());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge invertiert ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAllInverse(Map<? extends GValue, ? extends GKey> entries) {
			return this.putAllInverse(entries.entrySet());
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge invertiert ein und gibt den {@link #owner() Besitzer} zurück. **/
		public GOwner putAllInverse(Iterator<? extends Entry<? extends GValue, ? extends GKey>> entries) {
			while (entries.hasNext()) {
				this.putInverseImpl(entries.next());
			}
			return this.owner();
		}

		/** Diese Methode {@link Map#put(Object, Object) fügt} die gegebenen Einträge invertiert ein und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner putAllInverse(Iterable<? extends Entry<? extends GValue, ? extends GKey>> entries) {
			return this.putAllInverse(entries.iterator());
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner pop(Object key) {
			this.get().remove(key);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		@SafeVarargs
		public final GOwner popAll(Object... keys) {
			return this.popAll(Arrays.asList(keys));
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner popAll(Iterator<?> keys) {
			Iterators.removeAll(this.get().keySet(), keys);
			return this.owner();
		}

		/** Diese Methode {@link Map#remove(Object) entfern} den Eintrag mit dem gegebenen Schlüssel und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner popAll(Iterable<?> keys) {
			Iterables.removeAll(this.get().keySet(), keys);
			return this.owner();
		}

		@Override
		public void set(GResult value) {
			this.clear();
			this.putAll(value);
		}

		/** Diese Methode {@link Map#clear() entfern alle Einträge} und gibt den {@link #owner() Besitzer} zurück.
		 *
		 * @return den {@link #owner() Besitzer}. */
		public GOwner clear() {
			this.get().clear();
			return this.owner();
		}

		/** Diese Methode liefert den Konfigurator für den Wert zum gegebenen Schlüssel.
		 *
		 * @param key Schlüssel.
		 * @return Konfigurator. */
		public ValueProxy forKey(GKey key) {
			return new ValueProxy() {

				@Override
				protected GKey getKey() {
					return key;
				}

			};
		}

		@Override
		public Iterator2<Entry<GKey, GValue>> iterator() {
			return Iterators.iteratorFrom(this.get().entrySet().iterator());
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

		/** Diese Methode {@link #set(Object) setzt} den Wert und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner useValue(GValue value) {
			this.set(value);
			return this.owner();
		}

		/** Diese Methode {@link #useValue(Object) setzt} den Wert auf {@code null} und gibt den {@link #owner() Besitzer} zurück. */
		public GOwner clear() {
			return this.useValue(null);
		}

		@Override
		public Iterator2<GValue> iterator() {
			var result = this.get();
			if (result == null) return Iterators.empty();
			return Iterators.fromItem(result);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ des {@link Set}. */
	public static abstract class SetBuilder<GItem, GResult extends Set<GItem>, GOwner> extends BaseSetBuilder<GItem, GResult, GOwner> {

		/** Diese Methode gibt einen {@link SetBuilder} zum gegebenen {@link Set} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param result {@link Set}.
		 * @return {@link SetBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem> ItemSetBuilder<GItem> from(Set<GItem> result) throws NullPointerException {
			return new ItemSetBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} mit natürlicher Ordnung zurück. */
		public static <GItem> TreeSetBuilder<GItem> forTreeSet() {
			return SetBuilder.forTreeSet(null);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link TreeSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param comparator Ordnung der Elemente.
		 * @return {@link SetBuilder} eines {@link TreeSet}. */
		public static <GItem> TreeSetBuilder<GItem> forTreeSet(Comparator<? super GItem> comparator) {
			return new TreeSetBuilder<>(new TreeSet<>(comparator));
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} mit Steuwertpuffer zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <GItem> HashSetBuilder<GItem> forHashSet() {
			return SetBuilder.forHashSet(true);
		}

		/** Diese Methode gibt einen {@link SetBuilder} zu einem neuen {@link HashSet} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link SetBuilder} eines {@link HashSet}. */
		public static <GItem> HashSetBuilder<GItem> forHashSet(boolean withHashCache) {
			return new HashSetBuilder<>(withHashCache ? new HashSet2<GItem>() : new HashSet<GItem>());
		}

		final GResult value;

		/** Dieser Konstruktor initialisiert das {@link Set}. */
		public SetBuilder(GResult value) throws NullPointerException {
			this.value = Objects.notNull(value);
		}

		@Override
		public GResult get() {
			return this.value;
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Vereinigungsmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@link UnionSet}. */
		public ItemSetBuilder<GItem> toUnited(Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.<GItem>union(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für das Kartesische Produkt dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @param <GItem2> Typ der Elemente in der zweiten Menge.
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@link CartesianSet}. */
		public <GItem2> ItemSetBuilder<Entry<GItem, GItem2>> toCrossed(Set<? extends GItem2> items) {
			return SetBuilder.from(bee.creative.util.Collections.cartesian(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die Schnittmenge dieser {@link #get() Menge} und der gegebenen Menge zurück.
		 *
		 * @param items zweite Menge.
		 * @return neuer {@link SetBuilder} zum {@link IntersectSet}. */
		public ItemSetBuilder<GItem> toIntersected(Set<? extends GItem> items) {
			return SetBuilder.from(bee.creative.util.Collections.intersect(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#checkedSet(Set, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link SetBuilder} zum {@code checkedSet}. */
		public ItemSetBuilder<GItem> toChecked(Class<GItem> itemClazz) {
			return SetBuilder.from(Collections.checkedSet(this.get(), itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die datentypsichere {@link #get() Menge} zurück.
		 *
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link SetBuilder} zum {@link TranslatedSet}. */
		public <GItem2> ItemSetBuilder<GItem2> toTranslated(Translator<GItem, GItem2> itemTranslator) {
			return SetBuilder.from(bee.creative.util.Collections.translate(this.get(), itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die threadsichere {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#synchronizedSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code synchronizedSet}. */
		public ItemSetBuilder<GItem> toSynchronized() {
			return SetBuilder.from(Collections.synchronizedSet(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link SetBuilder} für die unveränderliche {@link #get() Menge} zurück.
		 *
		 * @see java.util.Collections#unmodifiableSet(Set)
		 * @return neuer {@link SetBuilder} zum {@code unmodifiableSet}. */
		public ItemSetBuilder<GItem> toUnmodifiable() {
			return SetBuilder.from(Collections.unmodifiableSet(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ItemSetBuilder<GItem> extends SetBuilder<GItem, Set<GItem>, ItemSetBuilder<GItem>> {

		ItemSetBuilder(Set<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ItemSetBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link TreeSet}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class TreeSetBuilder<GItem> extends SetBuilder<GItem, TreeSet<GItem>, TreeSetBuilder<GItem>> {

		TreeSetBuilder(TreeSet<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public TreeSetBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link HashSet}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class HashSetBuilder<GItem> extends SetBuilder<GItem, HashSet<GItem>, HashSetBuilder<GItem>> {

		HashSetBuilder(HashSet<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public HashSetBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für ein {@link ProxySet}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ProxySetBuilder<GItem> extends SetBuilder<GItem, ProxySet<GItem>, ProxySetBuilder<GItem>> {

		ProxySetBuilder(ProxySet<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ProxySetBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GResult> Typ der {@link List}. */
	public static abstract class ListBuilder<GItem, GResult extends List<GItem>, GOwner> extends BaseSetBuilder<GItem, GResult, GOwner> {

		/** Diese Methode gibt einen {@link ListBuilder} zur gegebenen {@link List} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @param result {@link List}.
		 * @return {@link ListBuilder}.
		 * @throws NullPointerException Wenn {@code result} {@code null} ist. */
		public static <GItem> ItemListBuilder<GItem> from(List<GItem> result) throws NullPointerException {
			return new ItemListBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link ArrayList} zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link ArrayList}. */
		public static <GItem> ArrayListBuilder<GItem> forArrayList() {
			return new ArrayListBuilder<>(new ArrayList<GItem>());
		}

		/** Diese Methode gibt einen {@link ListBuilder} zu einer neuen {@link LinkedList} mit zurück.
		 *
		 * @param <GItem> Typ der Elemente.
		 * @return {@link ListBuilder} einer {@link LinkedList}. */
		public static <GItem> LinkedListBuilder<GItem> forLinkedList() {
			return new LinkedListBuilder<>(new LinkedList<GItem>());
		}

		final GResult value;

		/** Dieser Konstruktor initialisiert die {@link List}. */
		public ListBuilder(GResult value) throws NullPointerException {
			this.value = Objects.notNull(value);
		}

		@Override
		public GResult get() {
			return this.value;
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die rückwärts geordnete {@link #get() Liste} zurück.
		 *
		 * @return neuer {@link ListBuilder} zur {@link ReverseList}. */
		public ItemListBuilder<GItem> toReverse() {
			return ListBuilder.from(bee.creative.util.Collections.reverse(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @param items zweite Liste.
		 * @return neuer {@link ListBuilder} zur {@link ConcatList}. */
		public ItemListBuilder<GItem> toChained(List<GItem> items) {
			return ListBuilder.from(bee.creative.util.Collections.concat(this.get(), items));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die Verkettung dieser {@link #get() Liste} mit der gegebenen zurück.
		 *
		 * @param items zweite Liste.
		 * @param extendMode Erweiterungsmodus.
		 * @return neuer {@link ListBuilder} zur {@link ConcatList}. */
		public ItemListBuilder<GItem> toChained(List<GItem> items, boolean extendMode) {
			return ListBuilder.from(bee.creative.util.Collections.concat(this.get(), items, extendMode));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#checkedList(List, Class)
		 * @param itemClazz Klasse der Elemente.
		 * @return neuer {@link ListBuilder} zur {@code checkedList}. */
		public ItemListBuilder<GItem> toChecked(Class<GItem> itemClazz) {
			return ListBuilder.from(java.util.Collections.checkedList(this.get(), itemClazz));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die datentypsichere {@link #get() Liste} zurück.
		 *
		 * @param <GItem2> Typ der übersetzten Elemente.
		 * @param itemTranslator {@link Translator} zur Übersetzung der Elemente.
		 * @return neuer {@link ListBuilder} zur {@link TranslatedList}. */
		public <GItem2> ItemListBuilder<GItem2> toTranslated(Translator<GItem, GItem2> itemTranslator) {
			return ListBuilder.from(bee.creative.util.Collections.translate(this.get(), itemTranslator));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die threadsichere {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#synchronizedList(List)
		 * @return neuer {@link ListBuilder} zur {@code synchronizedList}. */
		public ItemListBuilder<GItem> toSynchronized() {
			return ListBuilder.from(java.util.Collections.synchronizedList(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link ListBuilder} für die unveränderliche {@link #get() Liste} zurück.
		 *
		 * @see java.util.Collections#unmodifiableList(List)
		 * @return neuer {@link ListBuilder} zur {@code unmodifiableList}. */
		public ItemListBuilder<GItem> toUnmodifiable() {
			return ListBuilder.from(java.util.Collections.unmodifiableList(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link List}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ItemListBuilder<GItem> extends ListBuilder<GItem, List<GItem>, ItemListBuilder<GItem>> {

		ItemListBuilder(List<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ItemListBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link ArrayList}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ArrayListBuilder<GItem> extends ListBuilder<GItem, ArrayList<GItem>, ArrayListBuilder<GItem>> {

		ArrayListBuilder(ArrayList<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ArrayListBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link LinkedList}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class LinkedListBuilder<GItem> extends ListBuilder<GItem, LinkedList<GItem>, LinkedListBuilder<GItem>> {

		LinkedListBuilder(LinkedList<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public LinkedListBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link ProxyList}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ProxyListBuilder<GItem> extends ListBuilder<GItem, ProxyList<GItem>, ProxyListBuilder<GItem>> {

		ProxyListBuilder(ProxyList<GItem> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ProxyListBuilder<GItem> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte.
	 * @param <GResult> Typ der {@link Map}. */
	public static abstract class MapBuilder<GKey, GValue, GResult extends Map<GKey, GValue>, GOwner> extends BaseMapBuilder<GKey, GValue, GResult, GOwner> {

		/** Diese Methode gibt einen {@link ItemMapBuilder} zur gegebenen {@link Map} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GValue> Typ der Werte.
		 * @param result {@link Map}.
		 * @return {@link MapBuilder}. */
		public static <GKey, GValue> ItemMapBuilder<GKey, GValue> from(Map<GKey, GValue> result) {
			return new ItemMapBuilder<>(result);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} mit natürlicher Ordnung zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GItem> TreeMapBuilder<GKey, GItem> forTreeMap() {
			return MapBuilder.forTreeMap(null);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link TreeMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param comparator Ordnung der Schlüssel.
		 * @return {@link MapBuilder} einer {@link TreeMap}. */
		public static <GKey, GItem> TreeMapBuilder<GKey, GItem> forTreeMap(Comparator<? super GKey> comparator) {
			return new TreeMapBuilder<>(new TreeMap<GKey, GItem>(comparator));
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} mit Steuwertpuffer zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GItem> HashMapBuilder<GKey, GItem> forHashMap() {
			return MapBuilder.forHashMap(true);
		}

		/** Diese Methode gibt einen {@link MapBuilder} zu einer neuen {@link HashMap} zurück.
		 *
		 * @param <GKey> Typ der Schlüssel.
		 * @param <GItem> Typ der Werte.
		 * @param withHashCache Aktivierung des Streuwertpuffers.
		 * @return {@link MapBuilder} einer {@link HashMap}. */
		public static <GKey, GItem> HashMapBuilder<GKey, GItem> forHashMap(boolean withHashCache) {
			return new HashMapBuilder<>(withHashCache ? new HashMap2<GKey, GItem>() : new HashMap<GKey, GItem>());
		}

		final GResult value;

		/** Dieser Konstruktor initialisiert die {@link Map}. */
		public MapBuilder(GResult value) throws NullPointerException {
			this.value = Objects.notNull(value);
		}

		@Override
		public GResult get() {
			return this.value;
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur datentypsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#checkedMap(Map, Class, Class)
		 * @param keyClazz Klasse der Schlüssel.
		 * @param valueClazz Klasse der Werte.
		 * @return neuer {@link MapBuilder} zur {@code checkedMap}. */
		public ItemMapBuilder<GKey, GValue> toChecked(Class<GKey> keyClazz, Class<GValue> valueClazz) {
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
		public <GKey2, GValue2> ItemMapBuilder<GKey2, GValue2> toTranslated(Translator<GKey, GKey2> keyTranslator, Translator<GValue, GValue2> valueTranslator) {
			return MapBuilder.from(bee.creative.util.Collections.translate(this.get(), keyTranslator, valueTranslator));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur threadsicheren {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#synchronizedMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code synchronizedMap}. */
		public ItemMapBuilder<GKey, GValue> toSynchronized() {
			return MapBuilder.from(Collections.synchronizedMap(this.get()));
		}

		/** Diese Methode gibt einen neuen {@link MapBuilder} zur unveränderliche {@link #get() Abbildung} zurück.
		 *
		 * @see java.util.Collections#unmodifiableMap(Map)
		 * @return neuer {@link MapBuilder} zur {@code unmodifiableMap}. */
		public ItemMapBuilder<GKey, GValue> toUnmodifiable() {
			return MapBuilder.from(Collections.unmodifiableMap(this.get()));
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static class ItemMapBuilder<GKey, GValue> extends MapBuilder<GKey, GValue, Map<GKey, GValue>, ItemMapBuilder<GKey, GValue>> {

		ItemMapBuilder(Map<GKey, GValue> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ItemMapBuilder<GKey, GValue> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link TreeMap}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static class TreeMapBuilder<GKey, GValue> extends MapBuilder<GKey, GValue, TreeMap<GKey, GValue>, TreeMapBuilder<GKey, GValue>> {

		TreeMapBuilder(TreeMap<GKey, GValue> value) throws NullPointerException {
			super(value);
		}

		@Override
		public TreeMapBuilder<GKey, GValue> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link HashMap}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static class HashMapBuilder<GKey, GValue> extends MapBuilder<GKey, GValue, HashMap<GKey, GValue>, HashMapBuilder<GKey, GValue>> {

		HashMapBuilder(HashMap<GKey, GValue> value) throws NullPointerException {
			super(value);
		}

		@Override
		public HashMapBuilder<GKey, GValue> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für eine {@link ProxyMap}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GValue> Typ der Werte. */
	public static class ProxyMapBuilder<GKey, GValue> extends MapBuilder<GKey, GValue, ProxyMap<GKey, GValue>, ProxyMapBuilder<GKey, GValue>> {

		ProxyMapBuilder(ProxyMap<GKey, GValue> value) throws NullPointerException {
			super(value);
		}

		@Override
		public ProxyMapBuilder<GKey, GValue> owner() {
			return this;
		}

	}

	/** Diese Klasse implementiert einen abstrakten Konfigurator für einen Wert.
	 *
	 * @param <GValue> Typ des Werts.
	 * @param <GOwner> Typ des Besitzers. */
	public static abstract class ValueBuilder<GValue, GOwner> extends BaseValueBuilder<GValue, GOwner> {

		/** Diese Methode gibt einen {@link ValueBuilder} zum Wert des gegebenen {@link Property} zurück.
		 *
		 * @param <GValue> Typ des Werts.
		 * @return {@link ValueBuilder} eines {@link Property}. */
		public static <GValue> ProxyValueBuilder<GValue> fromProxy(Property<GValue> that) {
			return new ProxyValueBuilder<>(that);
		}

	}

	/** Diese Klasse implementiert einen Konfigurator für den Wert eines {@link Property}.
	 *
	 * @param <GValue> Typ des Werts. */
	public static class ProxyValueBuilder<GValue> extends ValueBuilder<GValue, ProxyValueBuilder<GValue>> {

		final Property<GValue> that;

		ProxyValueBuilder(Property<GValue> that) {
			this.that = Objects.notNull(that);
		}

		@Override
		public GValue get() {
			return this.that.get();
		}

		@Override
		public void set(GValue value) {
			this.that.set(value);
		}

		@Override
		public ProxyValueBuilder<GValue> owner() {
			return this;
		}

	}

}
