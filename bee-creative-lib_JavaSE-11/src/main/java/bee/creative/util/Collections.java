package bee.creative.util;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert umordnende, zusammenführende bzw. umwandelnde Sichten für {@link Set}, {@link Map}, {@link List} und {@link Collection}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Collections {

	/** Diese Klasse implementiert ein unveränderliches {@link Set} als Sicht auf die Menge aller Elemente von zwei gegebenen {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class UnionSet<GItem> extends AbstractSet2<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public UnionSet(Set<? extends GItem> items1, Set<? extends GItem> items2) throws NullPointerException {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
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
		public Iterator2<GItem> iterator() {
			return Iterators.<GItem>filter(this.items1.iterator(), Filters.fromItems(this.items2).negate()).concat(this.items2.iterator()).unmodifiable();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Menge aller Elemente eines {@link #items1 ersten} ohne die eines
	 * {@link #items2 zweiten} gegebenen {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ExceptSet<GItem> extends AbstractSet2<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public ExceptSet(Set<? extends GItem> items1, Set<? extends GItem> items2) throws NullPointerException {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
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
		public Iterator2<GItem> iterator() {
			return Iterators.<GItem>filter(this.items1.iterator(), Filters.fromItems(this.items2).negate()).unmodifiable();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) && !this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Schnittmenge eines {@link #items1 ersten} und eines {@link #items2 zweiten}
	 * gegebenen {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class IntersectSet<GItem> extends AbstractSet2<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public IntersectSet(Set<? extends GItem> items1, Set<? extends GItem> items2) throws NullPointerException {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
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
		public Iterator2<GItem> iterator() {
			return Iterators.<GItem>filter(this.items1.iterator(), Filters.fromItems(this.items2)).unmodifiable();
		}

		@Override
		public boolean contains(Object item) {
			return this.items1.contains(item) && this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf das kartesische Produkt einer {@link #keys Schlüsselmenge} und einer
	 * {@link #values Wertmenge}.
	 *
	 * @param <GKey> Typ der Elemente der Schlüsselmenge.
	 * @param <GValue> Typ der Elemente der Wertmenge. */
	public static class CartesianSet<GKey, GValue> extends AbstractSet2<Entry<GKey, GValue>> {

		class Iter extends AbstractIterator<Entry<GKey, GValue>> {

			GKey nextKey;

			Iterator<? extends GKey> keyIter = CartesianSet.this.keys.iterator();

			Iterator<? extends GValue> valueIter = Iterators.empty();

			@Override
			public boolean hasNext() {
				if (this.valueIter.hasNext()) return true;
				if (!this.keyIter.hasNext()) return false;
				this.nextKey = this.keyIter.next();
				this.valueIter = CartesianSet.this.values.iterator();
				return this.valueIter.hasNext();
			}

			@Override
			public Entry<GKey, GValue> next() {
				var nextValue = this.valueIter.next();
				return Entries.from(this.nextKey, nextValue);
			}

		}

		public final Set<? extends GKey> keys;

		public final Set<? extends GValue> values;

		public CartesianSet(Set<? extends GKey> keys, Set<? extends GValue> values) throws NullPointerException {
			this.keys = Objects.notNull(keys);
			this.values = Objects.notNull(values);
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
		public Iterator2<Entry<GKey, GValue>> iterator() {
			return new Iter();
		}

		@Override
		public boolean contains(Object item) {
			if (!(item instanceof Entry<?, ?>)) return false;
			var entry = (Entry<?, ?>)item;
			return this.keys.contains(entry.getKey()) && this.values.contains(entry.getValue());
		}

	}

	/** Diese Klasse implementiert eine rückwärts geordnete Sicht auf die gegebene {@link List}.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ReverseList<GItem> extends AbstractList2<GItem> {

		public final List<GItem> items;

		public ReverseList(List<GItem> items) {
			this.items = Objects.notNull(items);
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			var size = this.items.size();
			this.items.subList(size - toIndex, size - fromIndex).clear();
		}

		@Override
		public GItem get(int index) {
			return this.items.get(this.items.size() - index - 1);
		}

		@Override
		public GItem set(int index, GItem item2) {
			return this.items.set(this.items.size() - index - 1, item2);
		}

		@Override
		public void add(int index, GItem item2) {
			this.items.add(this.items.size() - index, item2);
		}

		@Override
		public boolean retainAll(Collection<?> items2) {
			return this.items.retainAll(items2);
		}

		@Override
		public GItem remove(int index) {
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

	}

	/** Diese Klasse implementiert eine verkettete {@link List} als Sicht auf zwei gegebene {@link List}. Wenn Elemente dazwischen eingefügt werden sollen,
	 * entscheidet der {@link #extendMode Erweiterungsmodus}, in welche {@link List} diese Elemente eingefügt werden. Wenn der Erweiterungsmodus {@code true} ist,
	 * wird in die {@link #items1 erste} und andernfalls in die {@link #items2 zweite} eingefügt.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ConcatList<GItem> extends AbstractList2<GItem> {

		public final List<GItem> items1;

		public final List<GItem> items2;

		public final boolean extendMode;

		public ConcatList(List<GItem> items1, List<GItem> items2, boolean extendMode) {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
			this.extendMode = extendMode;
		}

		@Override
		public GItem get(int index) {
			var size = this.items1.size();
			return index < size ? this.items1.get(index) : this.items2.get(index - size);
		}

		@Override
		public GItem set(int index, GItem item) {
			var size = this.items1.size();
			return index < size ? this.items1.set(index, item) : this.items2.set(index - size, item);
		}

		@Override
		public void add(int index, GItem item) {
			var size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) {
				this.items1.add(index, item);
			} else {
				this.items2.add(index - size, item);
			}
		}

		@Override
		public boolean addAll(int index, Collection<? extends GItem> items) {
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
		public GItem remove(int index) {
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

	}

	/** Diese Klasse implementiert eine verkettete {@link Collection} als Sicht auf zwei gegebene {@link Collection}. Wenn Elemente angefügt werden sollen,
	 * entscheidet der {@link #extendMode Erweiterungsmodus}, in welche {@link Collection} diese Elemente angefügt werden. Wenn der Erweiterungsmodus {@code true}
	 * ist, wird an die {@link #items1 erste} und andernfalls an die {@link #items2 zweite} angefügt.
	 *
	 * @param <GItem> Typ der Elemente. */
	public static class ConcatCollection<GItem> extends AbstractCollection2<GItem> {

		public final Collection<GItem> items1;

		public final Collection<GItem> items2;

		public final boolean extendMode;

		public ConcatCollection(Collection<GItem> items1, Collection<GItem> items2, boolean extendMode) {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
			this.extendMode = extendMode;
		}

		@Override
		public boolean add(GItem item) {
			return (this.extendMode ? this.items1 : this.items2).add(item);
		}

		@Override
		public boolean addAll(Collection<? extends GItem> items) {
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
		public Iterator2<GItem> iterator() {
			return Iterators.concat(this.items1.iterator(), this.items2.iterator());
		}

	}

	/** Diese Klasse implementiert eine {@link Map} als {@link Translator übersetzte} Sicht auf eine gegebene {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GKey2> Typ der Schlüssel der gegebenen {@link Map}.
	 * @param <GValue> Typ der Werte.
	 * @param <GValue2> Typ der Werte der gegebenen {@link Map}. */
	public static class TranslatedMap<GKey, GValue, GKey2, GValue2> extends AbstractMap<GKey, GValue> implements Map3<GKey, GValue> {

		class SourceEntry extends AbstractEntry<GKey2, GValue2> {

			public final Entry<GKey, GValue> that;

			SourceEntry(Entry<GKey, GValue> that) {
				this.that = that;
			}

			@Override
			public GKey2 getKey() {
				return TranslatedMap.this.keyTrans.toSource(this.that.getKey());
			}

			@Override
			public GValue2 getValue() {
				return TranslatedMap.this.valueTrans.toSource(this.that.getValue());
			}

			@Override
			public Entry2<GKey2, GValue2> useValue(GValue2 value) throws UnsupportedOperationException {
				this.that.setValue(TranslatedMap.this.valueTrans.toTarget(value));
				return this;
			}

		}

		class TargetEntry extends AbstractEntry<GKey, GValue> {

			final Entry<GKey2, GValue2> that;

			TargetEntry(Entry<GKey2, GValue2> that) {
				this.that = that;
			}

			@Override
			public GKey getKey() {
				return TranslatedMap.this.keyTrans.toTarget(this.that.getKey());
			}

			@Override
			public GValue getValue() {
				return TranslatedMap.this.valueTrans.toTarget(this.that.getValue());
			}

			@Override
			public Entry2<GKey, GValue> useValue(GValue value) throws UnsupportedOperationException {
				this.that.setValue(TranslatedMap.this.valueTrans.toSource(value));
				return this;
			}

		}

		class EntryTranslator implements Translator<Entry<GKey2, GValue2>, Entry<GKey, GValue>> {

			@Override
			public boolean isSource(Object object) {
				if (!(object instanceof Entry)) return false;
				var entry = (Entry<?, ?>)object;
				return TranslatedMap.this.keyTrans.isSource(entry.getKey()) && TranslatedMap.this.valueTrans.isSource(entry.getValue());
			}

			@Override
			public boolean isTarget(Object object) {
				if (!(object instanceof Entry)) return false;
				var entry = (Entry<?, ?>)object;
				return TranslatedMap.this.keyTrans.isTarget(entry.getKey()) && TranslatedMap.this.valueTrans.isTarget(entry.getValue());
			}

			@Override
			@SuppressWarnings ("unchecked")
			public Entry<GKey2, GValue2> toSource(Object object) throws ClassCastException, IllegalArgumentException {
				return new SourceEntry((Entry<GKey, GValue>)object);
			}

			@Override
			@SuppressWarnings ("unchecked")
			public Entry<GKey, GValue> toTarget(Object object) throws ClassCastException, IllegalArgumentException {
				return new TargetEntry((Entry<GKey2, GValue2>)object);
			}

		}

		public final Map<GKey2, GValue2> that;

		public final Translator<GKey2, GKey> keyTrans;

		public final Translator<GValue2, GValue> valueTrans;

		public TranslatedMap(Map<GKey2, GValue2> that, Translator<GKey2, GKey> keyTrans, Translator<GValue2, GValue> valueTrans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.keyTrans = Objects.notNull(keyTrans);
			this.valueTrans = Objects.notNull(valueTrans);
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
		public GValue get(Object key2) {
			if (!this.keyTrans.isTarget(key2)) return null;
			return this.valueTrans.toTarget(this.that.get(this.keyTrans.toSource(key2)));
		}

		@Override
		public boolean isEmpty() {
			return this.that.isEmpty();
		}

		@Override
		public Set2<GKey> keySet() {
			return Collections.translate(this.that.keySet(), this.keyTrans);
		}

		@Override
		public GValue put(GKey key2, GValue value2) {
			return this.valueTrans.toTarget(this.that.put(this.keyTrans.toSource(key2), this.valueTrans.toSource(value2)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public void putAll(Map<? extends GKey, ? extends GValue> entries2) {
			this.that.putAll(Collections.translate((Map<GKey, GValue>)entries2, Translators.reverseTranslator(this.keyTrans), Translators.reverseTranslator(this.valueTrans)));
		}

		@Override
		public GValue remove(final Object key2) {
			if (!this.keyTrans.isTarget(key2)) return null;
			return this.valueTrans.toTarget(this.that.remove(this.keyTrans.toSource(key2)));
		}

		@Override
		public int size() {
			return this.that.size();
		}

		@Override
		public Collection2<GValue> values() {
			return Collections.translate(this.that.values(), this.valueTrans);
		}

		@Override
		public Set2<Entry<GKey, GValue>> entrySet() {
			return Collections.translate(this.that.entrySet(), new EntryTranslator());
		}

	}

	/** Diese Klasse implementiert eine {@link List} als {@link Translator übersetzte} Sicht auf eine gegebene {@link List}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente der gegebenen {@link List}. */
	public static class TranslatedList<GItem, GItem2> extends AbstractList2<GItem> {

		public final List<GItem2> that;

		public final Translator<GItem2, GItem> trans;

		public TranslatedList(List<GItem2> that, Translator<GItem2, GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			this.that.subList(fromIndex, toIndex).clear();
		}

		@Override
		public GItem get(int index) {
			return this.trans.toTarget(this.that.get(index));
		}

		@Override
		public GItem set(int index, GItem item2) {
			return this.trans.toTarget(this.that.set(index, this.trans.toSource(item2)));
		}

		@Override
		public boolean add(GItem item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		public void add(int index, GItem item2) {
			this.that.add(index, this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(Collection<? extends GItem> items2) {
			return this.that.addAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(int index, Collection<? extends GItem> items2) {
			return this.that.addAll(index, Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		public GItem remove(int index) {
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
			return this.that.removeAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(Collection<?> items2) {
			return this.that.retainAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
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
		public Iterator2<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), Getters.fromTarget(this.trans));
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
	}

	/** Diese Klasse implementiert ein {@link Set} als {@link Translator übersetzte} Sicht auf ein gegebenes {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente des gegebenen {@link Set}. */
	public static class TranslatedSet<GItem, GItem2> extends AbstractSet2<GItem> {

		public final Set<GItem2> that;

		public final Translator<GItem2, GItem> trans;

		public TranslatedSet(Set<GItem2> that, Translator<GItem2, GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean add(GItem item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(Collection<? extends GItem> items2) {
			return this.that.addAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		public boolean remove(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(Collection<?> items2) {
			return this.that.removeAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(Collection<?> items2) {
			return this.that.retainAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
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
		public Iterator2<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), Getters.fromTarget(this.trans));
		}

	}

	/** Diese Klasse implementiert eine {@link Collection} als {@link Translator übersetzte} Sicht auf eine gegebene {@link Collection}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente der gegebenen {@link Collection}. */
	public static class TranslatedCollection<GItem, GItem2> extends AbstractCollection2<GItem> {

		public final Collection<GItem2> that;

		public final Translator<GItem2, GItem> trans;

		public TranslatedCollection(Collection<GItem2> that, Translator<GItem2, GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean add(GItem item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(Collection<? extends GItem> items2) {
			return this.that.addAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		public boolean remove(Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(Collection<?> items2) {
			return this.that.removeAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(Collection<?> items2) {
			return this.that.retainAll(Collections.translate((Collection<GItem>)items2, Translators.reverseTranslator(this.trans)));
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
		public Iterator2<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), Getters.fromTarget(this.trans));
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link UnionSet new UnionSet<>(items1, items2)}. */
	public static <GItem> Set2<GItem> union(Set<? extends GItem> items1, Set<? extends GItem> items2) throws NullPointerException {
		return new UnionSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptSet new ExceptSet<>(items1, items2)}. */
	public static <GItem> Set2<GItem> except(Set<? extends GItem> items1, Set<? extends GItem> items2) throws NullPointerException {
		return new ExceptSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link IntersectSet new IntersectSet<>(items1, items2)}. */
	public static <GItem> Set2<GItem> intersect(Set<? extends GItem> items1, Set<? extends GItem> items2) throws NullPointerException {
		return new IntersectSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatList new ConcatList<>(items1, items2, extendMode)}. */
	public static <GKey, GValue> Set2<Entry<GKey, GValue>> cartesian(Set<? extends GKey> keys, Set<? extends GValue> values) throws NullPointerException {
		return new CartesianSet<>(keys, values);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseList new ReverseList<>(items)}. */
	public static <GItem> List2<GItem> reverse(List<GItem> items) throws NullPointerException {
		return new ReverseList<>(items);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(List, List, boolean) Collections.concat(items1, items2, true)}. */
	public static <GTarget> List2<GTarget> concat(List<GTarget> items1, List<GTarget> items2) throws NullPointerException {
		return Collections.concat(items1, items2, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatList new ConcatList<>(items1, items2, extendMode)}. */
	public static <GItem> List2<GItem> concat(List<GItem> items1, List<GItem> items2, boolean extendMode) throws NullPointerException {
		return new ConcatList<>(items1, items2, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(Collection, Collection, boolean) Collections.concat(items1, items2, true)}. */
	public static <GItem> Collection2<GItem> concat(Collection<GItem> items1, Collection<GItem> items2) throws NullPointerException {
		return Collections.concat(items1, items2, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatCollection new ConcatCollection<>(items1, items2, extendMode)}. */
	public static <GItem> Collection2<GItem> concat(Collection<GItem> items1, Collection<GItem> items2, boolean extendMode) throws NullPointerException {
		return new ConcatCollection<>(items1, items2, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedMap new TranslatedMap<>(that, keyTrans, valueTrans)}. */
	public static <GKey2, GValue2, GKey, GValue> Map3<GKey, GValue> translate(Map<GKey2, GValue2> that, Translator<GKey2, GKey> keyTrans,
		Translator<GValue2, GValue> valueTrans) throws NullPointerException {
		return new TranslatedMap<>(that, keyTrans, valueTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedList new TranslatedList<>(that, trans)}. */
	public static <GItem2, GItem> List2<GItem> translate(List<GItem2> that, Translator<GItem2, GItem> trans) throws NullPointerException {
		return new TranslatedList<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSet new TranslatedSet<>(that, trans)}. */
	public static <GItem2, GItem> Set2<GItem> translate(Set<GItem2> that, Translator<GItem2, GItem> trans) throws NullPointerException {
		return new TranslatedSet<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedCollection new TranslatedCollection<>(that, trans)}. */
	public static <GItem2, GItem> Collection2<GItem> translate(Collection<GItem2> items, Translator<GItem2, GItem> trans) throws NullPointerException {
		return new TranslatedCollection<>(items, trans);
	}

}
