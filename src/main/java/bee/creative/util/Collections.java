package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
	@SuppressWarnings ("javadoc")
	public static class UnionSet<GItem> extends AbstractSet<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public UnionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
		}

		@Override
		public int size() {
			return Iterables.size(this);
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() && this.items2.isEmpty();
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.filter(this.items1.iterator(), Filters.fromItems(this.items2).negate()).concat(this.items2.iterator()).unmodifiable();
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Menge aller Elemente eines {@link #items1 ersten} ohne die eines
	 * {@link #items2 zweiten} gegebenen {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ExceptSet<GItem> extends AbstractSet<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public ExceptSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
		}

		@Override
		public int size() {
			return Iterables.size(this);
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() || !this.iterator().hasNext();
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.filter(this.items1.iterator(), Filters.fromItems(this.items2).negate()).unmodifiable();
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) && !this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Schnittmenge eines {@link #items1 ersten} und eines {@link #items2 zweiten}
	 * gegebenen {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class IntersectSet<GItem> extends AbstractSet<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public IntersectSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
		}

		@Override
		public int size() {
			return Iterables.size(this);
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() || this.items2.isEmpty() || !this.iterator().hasNext();
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.filter(this.items1.iterator(), Filters.fromItems(this.items2)).unmodifiable();
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) && this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf das kartesische Produkt einer {@link #keys Schlüsselmenge} und einer
	 * {@link #values Wertmenge}.
	 *
	 * @param <GKey> Typ der Elemente der Schlüsselmenge.
	 * @param <GValue> Typ der Elemente der Wertmenge. */
	@SuppressWarnings ("javadoc")
	public static class CartesianSet<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

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
				final GValue nextValue = this.valueIter.next();
				return Entries.from(this.nextKey, nextValue);
			}

		}

		public final Set<? extends GKey> keys;

		public final Set<? extends GValue> values;

		public CartesianSet(final Set<? extends GKey> keys, final Set<? extends GValue> values) throws NullPointerException {
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
		public Iterator<Entry<GKey, GValue>> iterator() {
			return new Iter();
		}

		@Override
		public boolean contains(final Object item) {
			if (!(item instanceof Entry<?, ?>)) return false;
			final Entry<?, ?> entry = (Entry<?, ?>)item;
			return this.keys.contains(entry.getKey()) && this.values.contains(entry.getValue());
		}

	}

	/** Diese Klasse implementiert eine rückwärts geordnete Sicht auf die gegebene {@link List}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ReverseList<GItem> extends AbstractList<GItem> {

		class Iter implements ListIterator<GItem> {

			final ListIterator<GItem> that;

			Iter(final int index) {
				final List<GItem> items = ReverseList.this.items;
				this.that = items.listIterator(items.size() - index);
			}

			@Override
			public boolean hasNext() {
				return this.that.hasPrevious();
			}

			@Override
			public boolean hasPrevious() {
				return this.that.hasNext();
			}

			@Override
			public void set(final GItem item2) {
				this.that.set(item2);
			}

			@Override
			public void add(final GItem item2) {
				this.that.add(item2);
				this.that.hasPrevious();
				this.that.previous();
			}

			@Override
			public GItem next() {
				return this.that.previous();
			}

			@Override
			public int nextIndex() {
				return ReverseList.this.items.size() - this.that.previousIndex() - 1;
			}

			@Override
			public GItem previous() {
				return this.that.next();
			}

			@Override
			public int previousIndex() {
				return ReverseList.this.items.size() - this.that.nextIndex() - 1;
			}

			@Override
			public void remove() {
				this.that.remove();
			}

		}

		public final List<GItem> items;

		public ReverseList(final List<GItem> items) {
			this.items = Objects.notNull(items);
		}

		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			final int size = this.items.size();
			this.items.subList(size - toIndex, size - fromIndex).clear();
		}

		@Override
		public GItem get(final int index) {
			return this.items.get(this.items.size() - index - 1);
		}

		@Override
		public GItem set(final int index, final GItem item2) {
			return this.items.set(this.items.size() - index - 1, item2);
		}

		@Override
		public void add(final int index, final GItem item2) {
			this.items.add(this.items.size() - index, item2);
		}

		@Override
		public boolean retainAll(final Collection<?> items2) {
			return this.items.retainAll(items2);
		}

		@Override
		public GItem remove(final int index) {
			return this.items.remove(this.items.size() - index - 1);
		}

		@Override
		public boolean removeAll(final Collection<?> items2) {
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
		public int indexOf(final Object item2) {
			final int index = this.items.lastIndexOf(item2);
			return index < 0 ? -1 : this.items.size() - index - 1;
		}

		@Override
		public int lastIndexOf(final Object item2) {
			final int index = this.items.indexOf(item2);
			return index < 0 ? -1 : this.items.size() - index - 1;
		}

		@Override
		public boolean contains(final Object item2) {
			return this.items.contains(item2);
		}

		@Override
		public boolean containsAll(final Collection<?> items2) {
			return this.items.containsAll(items2);
		}

		@Override
		public Iterator<GItem> iterator() {
			return this.listIterator(0);
		}

		@Override
		public ListIterator<GItem> listIterator(final int index) {
			return new Iter(index);
		}

	}

	/** Diese Klasse implementiert eine verkettete {@link List} als Sicht auf zwei gegebene {@link List}. Wenn Elemente dazwischen eingefügt werden sollen,
	 * entscheidet der {@link #extendMode Erweiterungsmodus}, in welche {@link List} diese Elemente eingefügt werden. Wenn der Erweiterungsmodus {@code true} ist,
	 * wird in die {@link #items1 erste} und andernfalls in die {@link #items2 zweite} eingefügt.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ConcatList<GItem> extends AbstractList<GItem> {

		public final List<GItem> items1;

		public final List<GItem> items2;

		public final boolean extendMode;

		public ConcatList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
			this.extendMode = extendMode;
		}

		@Override
		public GItem get(final int index) {
			final int size = this.items1.size();
			return index < size ? this.items1.get(index) : this.items2.get(index - size);
		}

		@Override
		public GItem set(final int index, final GItem item) {
			final int size = this.items1.size();
			return index < size ? this.items1.set(index, item) : this.items2.set(index - size, item);
		}

		@Override
		public void add(final int index, final GItem item) {
			final int size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) {
				this.items1.add(index, item);
			} else {
				this.items2.add(index - size, item);
			}
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends GItem> items) {
			final int size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) return this.items1.addAll(index, items);
			return this.items2.addAll(index - size, items);
		}

		@Override
		public boolean retainAll(final Collection<?> items) {
			if (!this.items1.retainAll(items)) return this.items2.retainAll(items);
			this.items2.retainAll(items);
			return true;
		}

		@Override
		public GItem remove(final int index) {
			final int size = this.items1.size();
			return index < size ? this.items1.remove(index) : this.items2.remove(index - size);
		}

		@Override
		public boolean remove(final Object item) {
			return this.items1.remove(item) || this.items2.remove(item);
		}

		@Override
		public boolean removeAll(final Collection<?> item) {
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
		public int indexOf(final Object item) {
			int index = this.items1.indexOf(item);
			if (index >= 0) return index;
			index = this.items2.indexOf(item);
			return index < 0 ? -1 : index + this.items1.size();
		}

		@Override
		public int lastIndexOf(final Object item) {
			final int index = this.items2.lastIndexOf(item);
			if (index >= 0) return index + this.items1.size();
			return this.items1.lastIndexOf(item);
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.concat(this.items1.iterator(), this.items2.iterator());
		}

		@Override
		public ListIterator<GItem> listIterator(final int index) {
			final ListIterator<GItem> iterator1, iterator2;
			int size = this.items1.size();
			if (index < size) {
				iterator1 = this.items1.listIterator(index);
				iterator2 = this.items2.listIterator(0);
			} else {
				iterator1 = this.items1.listIterator(size);
				iterator2 = this.items2.listIterator(index - size);
			}
			size = 0;
			return new ListIterator<GItem>() {

				int size = ConcatList.this.items1.size();

				ListIterator<GItem> iterator = index < this.size ? iterator1 : iterator2;

				@Override
				public boolean hasNext() {
					return this.iterator.hasNext() || (this.iterator = iterator2).hasNext();
				}

				@Override
				public boolean hasPrevious() {
					return this.iterator.hasPrevious() || (this.iterator = iterator1).hasPrevious();
				}

				@Override
				public void set(final GItem item) {
					this.iterator.set(item);
				}

				@Override
				public void add(final GItem item) {
					if ((iterator2.nextIndex() != 0) || (iterator1.nextIndex() != this.size)) {
						this.iterator.add(item);
					} else if (ConcatList.this.extendMode) {
						iterator1.add(item);
					} else {
						iterator2.add(item);
					}
					this.size = ConcatList.this.items1.size();
				}

				@Override
				public GItem next() {
					return this.iterator.next();
				}

				@Override
				public int nextIndex() {
					return this.iterator == iterator2 ? iterator2.nextIndex() + this.size : iterator1.nextIndex();
				}

				@Override
				public GItem previous() {
					return this.iterator.previous();
				}

				@Override
				public int previousIndex() {
					return this.iterator == iterator2 ? iterator2.previousIndex() + this.size : iterator1.previousIndex();
				}

				@Override
				public void remove() {
					this.iterator.remove();
					if (this.iterator == iterator2) return;
					this.size = ConcatList.this.items1.size();
				}

			};
		}
	}

	/** Diese Klasse implementiert eine verkettete {@link Collection} als Sicht auf zwei gegebene {@link Collection}. Wenn Elemente angefügt werden sollen,
	 * entscheidet der {@link #extendMode Erweiterungsmodus}, in welche {@link Collection} diese Elemente angefügt werden. Wenn der Erweiterungsmodus {@code true}
	 * ist, wird an die {@link #items1 erste} und andernfalls an die {@link #items2 zweite} angefügt.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ConcatCollection<GItem> extends AbstractCollection<GItem> {

		public final Collection<GItem> items1;

		public final Collection<GItem> items2;

		public final boolean extendMode;

		public ConcatCollection(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode) {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
			this.extendMode = extendMode;
		}

		@Override
		public boolean add(final GItem item) {
			return (this.extendMode ? this.items1 : this.items2).add(item);
		}

		@Override
		public boolean addAll(final Collection<? extends GItem> items) {
			return (this.extendMode ? this.items1 : this.items2).addAll(items);
		}

		@Override
		public boolean retainAll(final Collection<?> items) {
			if (!this.items1.retainAll(items)) return this.items2.retainAll(items);
			this.items2.retainAll(items);
			return true;
		}

		@Override
		public boolean remove(final Object item) {
			return this.items1.remove(item) || this.items2.remove(item);
		}

		@Override
		public boolean removeAll(final Collection<?> items) {
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
		public boolean contains(final Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.concat(this.items1.iterator(), this.items2.iterator());
		}

	}

	/** Diese Klasse implementiert eine {@link Map} als {@link Translator übersetzte} Sicht auf eine gegebene {@link Map}.
	 *
	 * @param <GKey> Typ der Schlüssel.
	 * @param <GKey2> Typ der Schlüssel der gegebenen {@link Map}.
	 * @param <GValue> Typ der Werte.
	 * @param <GValue2> Typ der Werte der gegebenen {@link Map}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedMap<GKey, GValue, GKey2, GValue2> extends AbstractMap<GKey, GValue> {

		class SourceEntry extends AbstractEntry<GKey2, GValue2> {

			public final Entry<GKey, GValue> that;

			SourceEntry(final Entry<GKey, GValue> that) {
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
			public Entry2<GKey2, GValue2> useValue(final GValue2 value) throws UnsupportedOperationException {
				this.that.setValue(TranslatedMap.this.valueTrans.toTarget(value));
				return this;
			}

		}

		class TargetEntry extends AbstractEntry<GKey, GValue> {

			final Entry<GKey2, GValue2> that;

			TargetEntry(final Entry<GKey2, GValue2> that) {
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
			public Entry2<GKey, GValue> useValue(final GValue value) throws UnsupportedOperationException {
				this.that.setValue(TranslatedMap.this.valueTrans.toSource(value));
				return this;
			}

		}

		class EntryTranslator implements Translator<Entry<GKey2, GValue2>, Entry<GKey, GValue>> {

			@Override
			public boolean isSource(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				return TranslatedMap.this.keyTrans.isSource(entry.getKey()) && TranslatedMap.this.valueTrans.isSource(entry.getValue());
			}

			@Override
			public boolean isTarget(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				return TranslatedMap.this.keyTrans.isTarget(entry.getKey()) && TranslatedMap.this.valueTrans.isTarget(entry.getValue());
			}

			@Override
			@SuppressWarnings ("unchecked")
			public Entry<GKey2, GValue2> toSource(final Object object) throws ClassCastException, IllegalArgumentException {
				return new SourceEntry((Entry<GKey, GValue>)object);
			}

			@Override
			@SuppressWarnings ("unchecked")
			public Entry<GKey, GValue> toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
				return new TargetEntry((Entry<GKey2, GValue2>)object);
			}

		}

		public final Map<GKey2, GValue2> that;

		public final Translator<GKey2, GKey> keyTrans;

		public final Translator<GValue2, GValue> valueTrans;

		public TranslatedMap(final Map<GKey2, GValue2> that, final Translator<GKey2, GKey> keyTrans, final Translator<GValue2, GValue> valueTrans)
			throws NullPointerException {
			this.that = Objects.notNull(that);
			this.keyTrans = Objects.notNull(keyTrans);
			this.valueTrans = Objects.notNull(valueTrans);
		}

		@Override
		public void clear() {
			this.that.clear();
		}

		@Override
		public boolean containsKey(final Object key2) {
			if (!this.keyTrans.isTarget(key2)) return false;
			return this.that.containsKey(this.keyTrans.toSource(key2));
		}

		@Override
		public boolean containsValue(final Object value2) {
			if (!this.valueTrans.isTarget(value2)) return false;
			return this.that.containsKey(this.valueTrans.toSource(value2));
		}

		@Override
		public GValue get(final Object key2) {
			if (!this.keyTrans.isTarget(key2)) return null;
			return this.valueTrans.toTarget(this.that.get(this.keyTrans.toSource(key2)));
		}

		@Override
		public boolean isEmpty() {
			return this.that.isEmpty();
		}

		@Override
		public Set<GKey> keySet() {
			return Collections.translate(this.that.keySet(), this.keyTrans);
		}

		@Override
		public GValue put(final GKey key2, final GValue value2) {
			return this.valueTrans.toTarget(this.that.put(this.keyTrans.toSource(key2), this.valueTrans.toSource(value2)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public void putAll(final Map<? extends GKey, ? extends GValue> entries2) {
			this.that.putAll(Collections.translate((Map<GKey, GValue>)entries2, Translators.reverse(this.keyTrans), Translators.reverse(this.valueTrans)));
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
		public Collection<GValue> values() {
			return Collections.translate(this.that.values(), this.valueTrans);
		}

		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return Collections.translate(this.that.entrySet(), new EntryTranslator());
		}

	}

	/** Diese Klasse implementiert eine {@link List} als {@link Translator übersetzte} Sicht auf eine gegebene {@link List}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente der gegebenen {@link List}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedList<GItem, GItem2> extends AbstractList<GItem> {

		public final List<GItem2> that;

		public final Translator<GItem2, GItem> trans;

		public TranslatedList(final List<GItem2> that, final Translator<GItem2, GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.that.subList(fromIndex, toIndex).clear();
		}

		@Override
		public GItem get(final int index) {
			return this.trans.toTarget(this.that.get(index));
		}

		@Override
		public GItem set(final int index, final GItem item2) {
			return this.trans.toTarget(this.that.set(index, this.trans.toSource(item2)));
		}

		@Override
		public boolean add(final GItem item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		public void add(final int index, final GItem item2) {
			this.that.add(index, this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final Collection<? extends GItem> items2) {
			return this.that.addAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final int index, final Collection<? extends GItem> items2) {
			return this.that.addAll(index, Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		public GItem remove(final int index) {
			return this.trans.toTarget(this.that.remove(index));
		}

		@Override
		public boolean remove(final Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(final Collection<?> items2) {
			return this.that.removeAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(final Collection<?> items2) {
			return this.that.retainAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
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
		public boolean contains(final Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.contains(this.trans.toSource(item2));
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), Getters.fromTarget(this.trans));
		}

		@Override
		public int indexOf(final Object item2) {
			if (!this.trans.isTarget(item2)) return -1;
			return this.that.indexOf(this.trans.toSource(item2));
		}

		@Override
		public int lastIndexOf(final Object item2) {
			if (!this.trans.isTarget(item2)) return -1;
			return this.that.lastIndexOf(this.trans.toSource(item2));
		}
	}

	/** Diese Klasse implementiert ein {@link Set} als {@link Translator übersetzte} Sicht auf ein gegebenes {@link Set}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente des gegebenen {@link Set}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedSet<GItem, GItem2> extends AbstractSet<GItem> {

		public final Set<GItem2> that;

		public final Translator<GItem2, GItem> trans;

		public TranslatedSet(final Set<GItem2> that, final Translator<GItem2, GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean add(final GItem item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final Collection<? extends GItem> items2) {
			return this.that.addAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		public boolean remove(final Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(final Collection<?> items2) {
			return this.that.removeAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(final Collection<?> items2) {
			return this.that.retainAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
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
		public boolean contains(final Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.contains(this.trans.toSource(item2));
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), Getters.fromTarget(this.trans));
		}

	}

	/** Diese Klasse implementiert eine {@link Collection} als {@link Translator übersetzte} Sicht auf eine gegebene {@link Collection}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente der gegebenen {@link Collection}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedCollection<GItem, GItem2> extends AbstractCollection<GItem> {

		public final Collection<GItem2> that;

		public final Translator<GItem2, GItem> trans;

		public TranslatedCollection(final Collection<GItem2> that, final Translator<GItem2, GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean add(final GItem item2) {
			return this.that.add(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final Collection<? extends GItem> items2) {
			return this.that.addAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		public boolean remove(final Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.remove(this.trans.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(final Collection<?> items2) {
			return this.that.removeAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(final Collection<?> items2) {
			return this.that.retainAll(Collections.translate((Collection<GItem>)items2, Translators.reverse(this.trans)));
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
		public boolean contains(final Object item2) {
			if (!this.trans.isTarget(item2)) return false;
			return this.that.contains(this.trans.toSource(item2));
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), Getters.fromTarget(this.trans));
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link UnionSet new UnionSet<>(items1, items2)}. */
	public static <GItem> Set<GItem> union(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		return new UnionSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptSet new ExceptSet<>(items1, items2)}. */
	public static <GItem> Set<GItem> except(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		return new ExceptSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link IntersectSet new IntersectSet<>(items1, items2)}. */
	public static <GItem> Set<GItem> intersect(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		return new IntersectSet<>(items1, items2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatList new ConcatList<>(items1, items2, extendMode)}. */
	public static <GKey, GValue> Set<Entry<GKey, GValue>> cartesian(final Set<? extends GKey> keys, final Set<? extends GValue> values)
		throws NullPointerException {
		return new CartesianSet<>(keys, values);
	}

	/** Diese Methode ist eine Abkürzung für {@link ReverseList new ReverseList<>(items)}. */
	public static <GItem> List<GItem> reverse(final List<GItem> items) throws NullPointerException {
		return new ReverseList<>(items);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(List, List, boolean) Collections.concat(items1, items2, true)}. */
	public static <GTarget> List<GTarget> concat(final List<GTarget> items1, final List<GTarget> items2) throws NullPointerException {
		return Collections.concat(items1, items2, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatList new ConcatList<>(items1, items2, extendMode)}. */
	public static <GItem> List<GItem> concat(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) throws NullPointerException {
		return new ConcatList<>(items1, items2, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link Collections#concat(Collection, Collection, boolean) Collections.concat(items1, items2, true)}. */
	public static <GItem> Collection<GItem> concat(final Collection<GItem> items1, final Collection<GItem> items2) throws NullPointerException {
		return Collections.concat(items1, items2, true);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatCollection new ConcatCollection<>(items1, items2, extendMode)}. */
	public static <GItem> Collection<GItem> concat(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode)
		throws NullPointerException {
		return new ConcatCollection<>(items1, items2, extendMode);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedMap new TranslatedMap<>(that, keyTrans, valueTrans)}. */
	public static <GKey2, GValue2, GKey, GValue> Map<GKey, GValue> translate(final Map<GKey2, GValue2> that, final Translator<GKey2, GKey> keyTrans,
		final Translator<GValue2, GValue> valueTrans) throws NullPointerException {
		return new TranslatedMap<>(that, keyTrans, valueTrans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedList new TranslatedList<>(that, trans)}. */
	public static <GItem2, GItem> List<GItem> translate(final List<GItem2> that, final Translator<GItem2, GItem> trans) throws NullPointerException {
		return new TranslatedList<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedSet new TranslatedSet<>(that, trans)}. */
	public static <GItem2, GItem> Set<GItem> translate(final Set<GItem2> that, final Translator<GItem2, GItem> trans) throws NullPointerException {
		return new TranslatedSet<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedCollection new TranslatedCollection<>(that, trans)}. */
	public static <GItem2, GItem> Collection<GItem> translate(final Collection<GItem2> items, final Translator<GItem2, GItem> trans) throws NullPointerException {
		return new TranslatedCollection<>(items, trans);
	}

}
