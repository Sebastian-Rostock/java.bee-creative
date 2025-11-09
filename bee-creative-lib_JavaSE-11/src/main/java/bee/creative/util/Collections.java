package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Entries.entryWith;
import static bee.creative.util.Entries.translatedEntry;
import static bee.creative.util.Filters.filterFrom;
import static bee.creative.util.Filters.filterFromItems;
import static bee.creative.util.Getters.getterFrom;
import static bee.creative.util.Iterators.concatIterator;
import static bee.creative.util.Iterators.emptyIterator;
import static bee.creative.util.Iterators.translatedIterator;
import static bee.creative.util.Translators.translatorFrom;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/** Diese Klasse implementiert umordnende, zusammenführende bzw. umwandelnde Sichten für {@link Set}, {@link Map}, {@link List} und {@link Collection}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Collections {

	/** Diese Methode ist eine Abkürzung für {@link UnionSet new UnionSet<>(items1, items2)}. */
	public static <E> Set2<E> unionSet(Set<? extends E> items1, Set<? extends E> items2) throws NullPointerException {
		return new UnionSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptSet new ExceptSet<>(items1, items2)}. */
	public static <E> Set2<E> exceptSet(Set<? extends E> items1, Set<? extends E> items2) throws NullPointerException {
		return new ExceptSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link IntersectSet new IntersectSet<>(items1, items2)}. */
	public static <E> Set2<E> intersectSet(Set<? extends E> items1, Set<? extends E> items2) throws NullPointerException {
		return new IntersectSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatList new ConcatList<>(items1, items2, extendMode)}. */
	public static <K, V> Set2<Entry<K, V>> cartesianSet(Set<? extends K> keys, Set<? extends V> values) throws NullPointerException {
		return new CartesianSet<>(keys, values);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSet new TranslatedSet<>(that, trans)}. */
	public static <E2, E> Set2<E> translatedSet(Set<E2> that, Translator<E2, E> trans) throws NullPointerException {
		return new TranslatedSet<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedMap new TranslatedMap<>(that, keyTrans, valueTrans)}. */
	public static <K2, V2, K, V> Map3<K, V> translatedMap(Map<K2, V2> that, Translator<K2, K> keyTrans,
		Translator<V2, V> valueTrans) throws NullPointerException {
		return new TranslatedMap<>(that, keyTrans, valueTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatList new ConcatList<>(items1, items2, extendMode)}. */
	public static <E> List2<E> concatList(List<E> items1, List<E> items2, boolean extendMode) throws NullPointerException {
		return new ConcatList<>(items1, items2, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReversedList new ReverseList<>(items)}. */
	public static <E> List2<E> reversedList(List<E> items) throws NullPointerException {
		return new ReversedList<>(items);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedList new TranslatedList<>(that, trans)}. */
	public static <E2, E> List2<E> translatedList(List<E2> that, Translator<E2, E> trans) throws NullPointerException {
		return new TranslatedList<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatCollection new ConcatCollection<>(items1, items2, extendMode)}. */
	public static <E> Collection2<E> concatCollection(Collection<E> items1, Collection<E> items2, boolean extendMode)
		throws NullPointerException {
		return new ConcatCollection<>(items1, items2, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedCollection new TranslatedCollection<>(that, trans)}. */
	public static <E2, E> Collection2<E> translatedCollection(Collection<E2> items, Translator<E2, E> trans) throws NullPointerException {
		return new TranslatedCollection<>(items, trans);
	}

	/** Diese Klasse implementiert ein unveränderliches {@link Set} als Sicht auf die Vereinigungsmenge aller Elemente zweier gegebener {@link Set}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class UnionSet<E> extends AbstractSet2<E> {

		public UnionSet(Set<? extends E> items1, Set<? extends E> items2) throws NullPointerException {
			this.items1 = notNull(items1);
			this.items2 = notNull(items2);
		}

		@Override
		public int size() {
			return Iterators.size(this.iterator());
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() && this.items2.isEmpty();
		}

		@Override
		public Iterator2<E> iterator() {
			return Iterators.<E>filteredIterator(this.items1.iterator(), filterFromItems(this.items2).negate()).concat(this.items2.iterator()).unmodifiable();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

		private final Set<? extends E> items1;

		private final Set<? extends E> items2;

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Menge aller Elemente eines ersten ohne die eines zweiten gegebenen
	 * {@link Set}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ExceptSet<E> extends AbstractSet2<E> {

		public ExceptSet(Set<? extends E> items1, Set<? extends E> items2) throws NullPointerException {
			this.items1 = notNull(items1);
			this.items2 = notNull(items2);
		}

		@Override
		public int size() {
			return Iterators.size(this.iterator());
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() || !this.iterator().hasNext();
		}

		@Override
		public Iterator2<E> iterator() {
			return Iterators.<E>filteredIterator(this.items1.iterator(), filterFromItems(this.items2).negate()).unmodifiable();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) && !this.items2.contains(item);
		}

		private final Set<? extends E> items1;

		private final Set<? extends E> items2;

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Schnittmenge zweier gegebener {@link Set}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class IntersectSet<E> extends AbstractSet2<E> {

		public IntersectSet(Set<? extends E> items1, Set<? extends E> items2) throws NullPointerException {
			this.items1 = notNull(items1);
			this.items2 = notNull(items2);
		}

		@Override
		public int size() {
			return Iterators.size(this.iterator());
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() || this.items2.isEmpty() || !this.iterator().hasNext();
		}

		@Override
		public Iterator2<E> iterator() {
			return Iterators.<E>filteredIterator(this.items1.iterator(), filterFromItems(this.items2)).unmodifiable();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) && this.items2.contains(item);
		}

		private final Set<? extends E> items1;

		private final Set<? extends E> items2;

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf das kartesische Produkt einer Schlüsselmenge und einer Wertmenge.
	 *
	 * @param <K> Typ der Elemente der Schlüsselmenge.
	 * @param <V> Typ der Elemente der Wertmenge. */
	public static class CartesianSet<K, V> extends AbstractSet2<Entry<K, V>> {

		public CartesianSet(Set<? extends K> keys, Set<? extends V> values) throws NullPointerException {
			this.keys = notNull(keys);
			this.values = notNull(values);
		}

		@Override
		public int size() {
			return this.keys.size() * this.values.size();
		}

		@Override
		public boolean isEmpty() {
			return this.keys.isEmpty() || this.values.isEmpty();
		}

		@Override
		public Iterator2<Entry<K, V>> iterator() {
			return new Iter();
		}

		@Override
		public boolean contains(Object item) {
			if (!(item instanceof Entry<?, ?>)) return false;
			var entry = (Entry<?, ?>)item;
			return this.keys.contains(entry.getKey()) && this.values.contains(entry.getValue());
		}

		private final Set<? extends K> keys;

		private final Set<? extends V> values;

		private class Iter extends AbstractIterator<Entry<K, V>> {

			@Override
			public boolean hasNext() {
				if (this.valueIter.hasNext()) return true;
				if (!this.keyIter.hasNext()) return false;
				this.nextKey = this.keyIter.next();
				this.valueIter = CartesianSet.this.values.iterator();
				return this.valueIter.hasNext();
			}

			@Override
			public Entry<K, V> next() {
				var nextValue = this.valueIter.next();
				return entryWith(this.nextKey, nextValue);
			}

			K nextKey;

			Iterator<? extends K> keyIter = CartesianSet.this.keys.iterator();

			Iterator<? extends V> valueIter = emptyIterator();

		}

	}

	/** Diese Klasse implementiert ein {@link Set} als {@link Translator übersetzte} Sicht auf ein gegebenes {@link Set}.
	 *
	 * @param <E> Typ der Elemente.
	 * @param <E2> Typ der Elemente des gegebenen {@link Set}. */
	public static class TranslatedSet<E, E2> extends AbstractSet2<E> {
	
		public TranslatedSet(Set<E2> that, Translator<E2, E> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = translatorFrom(trans);
			this.trans2 = this.trans.reverse();
		}
	
		@Override
		public boolean add(E item2) {
			return this.that.add(this.trans.toSource(item2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(Collection<? extends E> items2) {
			return this.that.addAll(translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		public boolean remove(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(Collection<?> items2) {
			return this.that.removeAll(translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(Collection<?> items2) {
			return this.that.retainAll(translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		public int size() {
			return this.that.size();
		}
	
		@Override
		public void clear() {
			this.that.clear();
		}
	
		@Override
		public boolean isEmpty() {
			return this.that.isEmpty();
		}
	
		@Override
		public boolean contains(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.contains(this.trans.toSource(item2));
		}
	
		@Override
		public Iterator2<E> iterator() {
			return translatedIterator(this.that.iterator(), this.trans.asTargetGetter());
		}
	
		private final Set<E2> that;
	
		private final Translator3<E2, E> trans;
	
		private final Translator3<E, E2> trans2;
	
	}

	/** Diese Klasse implementiert eine {@link Map} als {@link Translator übersetzte} Sicht auf eine gegebene {@link Map}.
	 *
	 * @param <K> Typ der Schlüssel.
	 * @param <K2> Typ der Schlüssel der gegebenen {@link Map}.
	 * @param <V> Typ der Werte.
	 * @param <V2> Typ der Werte der gegebenen {@link Map}. */
	public static class TranslatedMap<K, V, K2, V2> extends AbstractMap<K, V> implements Map3<K, V> {
	
		public TranslatedMap(Map<K2, V2> that, Translator<K2, K> keyTrans, Translator<V2, V> valueTrans) throws NullPointerException {
			this.that = notNull(that);
			this.keyTrans = translatorFrom(keyTrans);
			this.valueTrans = translatorFrom(valueTrans);
			this.keyTrans2 = this.keyTrans.reverse();
			this.valueTrans2 = this.valueTrans.reverse();
			this.entryTrans = translatorFrom(filterFrom(object -> {
				if (!(object instanceof Entry)) return false;
				var entry = (Entry<?, ?>)object;
				return this.keyTrans.isTarget(entry.getKey()) && this.valueTrans.isTarget(entry.getValue());
			}), filterFrom(object -> {
				if (!(object instanceof Entry)) return false;
				var entry = (Entry<?, ?>)object;
				return this.keyTrans.isSource(entry.getKey()) && this.valueTrans.isSource(entry.getValue());
			}), getterFrom(object -> {
				@SuppressWarnings ("unchecked")
				var entry = (Entry<K2, V2>)object;
				return translatedEntry(entry, this.keyTrans, this.valueTrans);
			}), getterFrom(object -> {
				@SuppressWarnings ("unchecked")
				var entry = (Entry<K, V>)object;
				return translatedEntry(entry, this.keyTrans2, this.valueTrans2);
			}));
		}
	
		@Override
		public void clear() {
			this.that.clear();
		}
	
		@Override
		public boolean containsKey(Object key2) {
			if (!this.keyTrans.isTarget(key2)) return false;
			return this.that.containsKey(this.keyTrans.toSource(key2));
		}
	
		@Override
		public boolean containsValue(Object value2) {
			if (!this.valueTrans.isTarget(value2)) return false;
			return this.that.containsValue(this.valueTrans.toSource(value2));
		}
	
		@Override
		public V get(Object key2) {
			if (!this.keyTrans.isTarget(key2)) return null;
			return this.valueTrans.toTarget(this.that.get(this.keyTrans.toSource(key2)));
		}
	
		@Override
		public boolean isEmpty() {
			return this.that.isEmpty();
		}
	
		@Override
		public Set2<K> keySet() {
			return translatedSet(this.that.keySet(), this.keyTrans);
		}
	
		@Override
		public V put(K key2, V value2) {
			return this.valueTrans.toTarget(this.that.put(this.keyTrans.toSource(key2), this.valueTrans.toSource(value2)));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public void putAll(Map<? extends K, ? extends V> entries2) {
			this.that.putAll(translatedMap((Map<K, V>)entries2, this.keyTrans2, this.valueTrans2));
		}
	
		@Override
		public V remove(final Object key2) {
			if (!this.keyTrans.isTarget(key2)) return null;
			return this.valueTrans.toTarget(this.that.remove(this.keyTrans.toSource(key2)));
		}
	
		@Override
		public int size() {
			return this.that.size();
		}
	
		@Override
		public Collection2<V> values() {
			return translatedCollection(this.that.values(), this.valueTrans);
		}
	
		@Override
		public Set2<Entry<K, V>> entrySet() {
			return translatedSet(this.that.entrySet(), this.entryTrans);
		}
	
		private final Map<K2, V2> that;
	
		private final Translator3<K2, K> keyTrans;
	
		private final Translator3<K, K2> keyTrans2;
	
		private final Translator3<V2, V> valueTrans;
	
		private final Translator3<V, V2> valueTrans2;
	
		private final Translator3<Entry<K2, V2>, Entry<K, V>> entryTrans;
	
	}

	/** Diese Klasse implementiert eine verkettete {@link List} als Sicht auf zwei gegebene {@link List}. Wenn Elemente dazwischen eingefügt werden sollen,
	 * entscheidet der {@link #extendMode Erweiterungsmodus}, in welche {@link List} diese Elemente eingefügt werden. Wenn der Erweiterungsmodus {@code true} ist,
	 * wird in die {@link #items1 erste} und andernfalls in die {@link #items2 zweite} eingefügt.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ConcatList<E> extends AbstractList2<E> {

		public ConcatList(List<E> items1, List<E> items2, boolean extendMode) {
			this.items1 = notNull(items1);
			this.items2 = notNull(items2);
			this.extendMode = extendMode;
		}

		@Override
		public E get(int index) {
			var size = this.items1.size();
			return index < size ? this.items1.get(index) : this.items2.get(index - size);
		}

		@Override
		public E set(int index, E item) {
			var size = this.items1.size();
			return index < size ? this.items1.set(index, item) : this.items2.set(index - size, item);
		}

		@Override
		public void add(int index, E item) {
			var size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) {
				this.items1.add(index, item);
			} else {
				this.items2.add(index - size, item);
			}
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> items) {
			var size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) return this.items1.addAll(index, items);
			return this.items2.addAll(index - size, items);
		}

		@Override
		public boolean retainAll(Collection<?> items) {
			if (!this.items1.retainAll(items)) return this.items2.retainAll(items);
			this.items2.retainAll(items);
			return true;
		}

		@Override
		public E remove(int index) {
			var size = this.items1.size();
			return index < size ? this.items1.remove(index) : this.items2.remove(index - size);
		}

		@Override
		public boolean remove(Object item) {
			return this.items1.remove(item) || this.items2.remove(item);
		}

		@Override
		public boolean removeAll(Collection<?> item) {
			if (!this.items1.removeAll(item)) return this.items2.removeAll(item);
			this.items2.removeAll(item);
			return true;
		}

		@Override
		public int size() {
			return this.items1.size() + this.items2.size();
		}

		@Override
		public void clear() {
			this.items1.clear();
			this.items2.clear();
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() && this.items2.isEmpty();
		}

		@Override
		public int indexOf(Object item) {
			var index = this.items1.indexOf(item);
			if (index >= 0) return index;
			index = this.items2.indexOf(item);
			return index < 0 ? -1 : index + this.items1.size();
		}

		@Override
		public int lastIndexOf(Object item) {
			var index = this.items2.lastIndexOf(item);
			if (index >= 0) return index + this.items1.size();
			return this.items1.lastIndexOf(item);
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

		private final List<E> items1;

		private final List<E> items2;

		private final boolean extendMode;

	}

	/** Diese Klasse implementiert eine rückwärts geordnete Sicht auf die gegebene {@link List}.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ReversedList<E> extends AbstractList2<E> {
	
		public ReversedList(List<E> items) {
			this.items = notNull(items);
		}
	
		@Override
		public E get(int index) {
			return this.items.get(this.items.size() - index - 1);
		}
	
		@Override
		public E set(int index, E item2) {
			return this.items.set(this.items.size() - index - 1, item2);
		}
	
		@Override
		public void add(int index, E item2) {
			this.items.add(this.items.size() - index, item2);
		}
	
		@Override
		public boolean retainAll(Collection<?> items2) {
			return this.items.retainAll(items2);
		}
	
		@Override
		public E remove(int index) {
			return this.items.remove(this.items.size() - index - 1);
		}
	
		@Override
		public boolean removeAll(Collection<?> items2) {
			return this.items.removeAll(items2);
		}
	
		@Override
		public int size() {
			return this.items.size();
		}
	
		@Override
		public void clear() {
			this.items.clear();
		}
	
		@Override
		public boolean isEmpty() {
			return this.items.isEmpty();
		}
	
		@Override
		public int indexOf(Object item2) {
			var index = this.items.lastIndexOf(item2);
			return index < 0 ? -1 : this.items.size() - index - 1;
		}
	
		@Override
		public int lastIndexOf(Object item2) {
			var index = this.items.indexOf(item2);
			return index < 0 ? -1 : this.items.size() - index - 1;
		}
	
		@Override
		public boolean contains(Object item2) {
			return this.items.contains(item2);
		}
	
		@Override
		public boolean containsAll(Collection<?> items2) {
			return this.items.containsAll(items2);
		}
	
		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			var size = this.items.size();
			this.items.subList(size - toIndex, size - fromIndex).clear();
		}
	
		private final List<E> items;
	
	}

	/** Diese Klasse implementiert eine {@link List} als {@link Translator übersetzte} Sicht auf eine gegebene {@link List}.
	 *
	 * @param <E> Typ der Elemente.
	 * @param <E2> Typ der Elemente der gegebenen {@link List}. */
	public static class TranslatedList<E, E2> extends AbstractList2<E> {
	
		public TranslatedList(List<E2> that, Translator<E2, E> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = translatorFrom(trans);
			this.trans2 = this.trans.reverse();
		}
	
		@Override
		public E get(int index) {
			return this.trans.toTarget(this.that.get(index));
		}
	
		@Override
		public E set(int index, E item2) {
			return this.trans.toTarget(this.that.set(index, this.trans.toSource(item2)));
		}
	
		@Override
		public boolean add(E item2) {
			return this.that.add(this.trans.toSource(item2));
		}
	
		@Override
		public void add(int index, E item2) {
			this.that.add(index, this.trans.toSource(item2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(Collection<? extends E> items2) {
			return this.that.addAll(translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(int index, Collection<? extends E> items2) {
			return this.that.addAll(index, translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		public E remove(int index) {
			return this.trans.toTarget(this.that.remove(index));
		}
	
		@Override
		public boolean remove(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(Collection<?> items2) {
			return this.that.removeAll(translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(Collection<?> items2) {
			return this.that.retainAll(translatedCollection((Collection<E>)items2, this.trans2));
		}
	
		@Override
		public int size() {
			return this.that.size();
		}
	
		@Override
		public void clear() {
			this.that.clear();
		}
	
		@Override
		public boolean isEmpty() {
			return this.that.isEmpty();
		}
	
		@Override
		public boolean contains(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.contains(this.trans.toSource(item2));
		}
	
		@Override
		public Iterator2<E> iterator() {
			return translatedIterator(this.that.iterator(), this.trans.asTargetGetter());
		}
	
		@Override
		public int indexOf(Object item2) {
			if (!this.trans.isTarget(item2)) return -1;
			return this.that.indexOf(this.trans.toSource(item2));
		}
	
		@Override
		public int lastIndexOf(Object item2) {
			if (!this.trans.isTarget(item2)) return -1;
			return this.that.lastIndexOf(this.trans.toSource(item2));
		}
	
		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			this.that.subList(fromIndex, toIndex).clear();
		}
	
		private final List<E2> that;
	
		private final Translator3<E2, E> trans;
	
		private final Translator3<E, E2> trans2;
	
	}

	/** Diese Klasse implementiert eine verkettete {@link Collection} als Sicht auf zwei gegebene {@link Collection}. Wenn Elemente angefügt werden sollen,
	 * entscheidet der {@link #extendMode Erweiterungsmodus}, in welche {@link Collection} diese Elemente angefügt werden. Wenn der Erweiterungsmodus {@code true}
	 * ist, wird an die {@link #items1 erste} und andernfalls an die {@link #items2 zweite} angefügt.
	 *
	 * @param <E> Typ der Elemente. */
	public static class ConcatCollection<E> extends AbstractCollection2<E> {

		public ConcatCollection(Collection<E> items1, Collection<E> items2, boolean extendMode) {
			this.items1 = notNull(items1);
			this.items2 = notNull(items2);
			this.extendMode = extendMode;
		}

		@Override
		public boolean add(E item) {
			return (this.extendMode ? this.items1 : this.items2).add(item);
		}

		@Override
		public boolean addAll(Collection<? extends E> items) {
			return (this.extendMode ? this.items1 : this.items2).addAll(items);
		}

		@Override
		public boolean retainAll(Collection<?> items) {
			if (!this.items1.retainAll(items)) return this.items2.retainAll(items);
			this.items2.retainAll(items);
			return true;
		}

		@Override
		public boolean remove(Object item) {
			return this.items1.remove(item) || this.items2.remove(item);
		}

		@Override
		public boolean removeAll(Collection<?> items) {
			if (!this.items1.removeAll(items)) return this.items2.removeAll(items);
			this.items2.removeAll(items);
			return true;
		}

		@Override
		public int size() {
			return this.items1.size() + this.items2.size();
		}

		@Override
		public void clear() {
			this.items1.clear();
			this.items2.clear();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

		@Override
		public Iterator2<E> iterator() {
			return concatIterator(this.items1.iterator(), this.items2.iterator());
		}

		private final Collection<E> items1;

		private final Collection<E> items2;

		private final boolean extendMode;

	}

	/** Diese Klasse implementiert eine {@link Collection} als {@link Translator übersetzte} Sicht auf eine gegebene {@link Collection}.
	 *
	 * @param <E> Typ der Elemente.
	 * @param <E2> Typ der Elemente der gegebenen {@link Collection}. */
	public static class TranslatedCollection<E, E2> extends AbstractCollection2<E> {

		public TranslatedCollection(Collection<E2> that, Translator<E2, E> trans) throws NullPointerException {
			this.that = notNull(that);
			this.trans = translatorFrom(trans);
			this.trans2 = this.trans.reverse();
		}

		@Override
		public boolean add(E item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(Collection<? extends E> items2) {
			return this.that.addAll(translatedCollection((Collection<E>)items2, this.trans2));
		}

		@Override
		public boolean remove(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(Collection<?> items2) {
			return this.that.removeAll(translatedCollection((Collection<E>)items2, this.trans2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(Collection<?> items2) {
			return this.that.retainAll(translatedCollection((Collection<E>)items2, this.trans2));
		}

		@Override
		public int size() {
			return this.that.size();
		}

		@Override
		public void clear() {
			this.that.clear();
		}

		@Override
		public boolean isEmpty() {
			return this.that.isEmpty();
		}

		@Override
		public boolean contains(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.contains(this.trans.toSource(item2));
		}

		@Override
		public Iterator2<E> iterator() {
			return translatedIterator(this.that.iterator(), this.trans.asTargetGetter());
		}

		private final Collection<E2> that;

		private final Translator3<E2, E> trans;

		private final Translator3<E, E2> trans2;

	}

}
