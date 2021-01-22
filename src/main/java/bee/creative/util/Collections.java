package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import bee.creative.bind.Getters;
import bee.creative.bind.Translator;
import bee.creative.bind.Translators;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert umordnende, zusammenführende bzw. umwandelnde Sichten für {@link Set}, {@link Map}, {@link List} und {@link Collection}.
 *
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Collections {

	/** Diese Klasse implementiert {@link Collections#unionSet(Set, Set)} */
	@SuppressWarnings ("javadoc")
	public static class UnionSet<GItem> extends AbstractSet<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public UnionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
		}

		@Override
		public int size() {
			final int size1 = this.items1.size(), size2 = this.items2.size();
			int result = size1 + size2;
			if (size1 < size2) {
				for (final Object item: this.items1)
					if (this.items2.contains(item)) {
						result--;
					}
			} else {
				for (final Object item: this.items2)
					if (this.items1.contains(item)) {
						result--;
					}
			}
			return result;
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() && this.items2.isEmpty();
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.unmodifiableIterator(this.items1.size() < this.items2.size()
				? Iterators.chainedIterator(Iterators.filteredIterator(Filters.negationFilter(Collections.toContainsFilter(this.items2)), this.items1.iterator()),
					this.items2.iterator())
				: Iterators.chainedIterator(Iterators.filteredIterator(Filters.negationFilter(Collections.toContainsFilter(this.items1)), this.items2.iterator()),
					this.items1.iterator()));
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) || this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert {@link Collections#cartesianSet(Set, Set)} */
	@SuppressWarnings ("javadoc")
	public static class CartesianSet<GKey, GValue> extends AbstractSet<Entry<GKey, GValue>> {

		class EntryIterator implements Iterator<Entry<GKey, GValue>> {

			GKey nextKey;

			Iterator<? extends GKey> keyIter = CartesianSet.this.keys.iterator();

			Iterator<? extends GValue> valueIter = Iterators.emptyIterator();

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
				return new AbstractMap.SimpleImmutableEntry<>(this.nextKey, nextValue);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		}

		public final Set<? extends GKey> keys;

		public final Set<? extends GValue> values;

		public CartesianSet(final Set<? extends GKey> keys, final Set<? extends GValue> values) {
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
			if (this.keys.isEmpty() || this.values.isEmpty()) return Iterators.emptyIterator();
			return new EntryIterator();
		}

		@Override
		public boolean contains(final Object item) {
			if (!(item instanceof Entry<?, ?>)) return false;
			final Entry<?, ?> entry = (Entry<?, ?>)item;
			return this.keys.contains(entry.getKey()) && this.values.contains(entry.getValue());
		}

	}

	/** Diese Klasse implementiert {@link Collections#intersectionSet(Set, Set)} */
	@SuppressWarnings ("javadoc")
	public static class IntersectionSet<GItem> extends AbstractSet<GItem> {

		public final Set<? extends GItem> items1;

		public final Set<? extends GItem> items2;

		public IntersectionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) {
			this.items1 = Objects.notNull(items1);
			this.items2 = Objects.notNull(items2);
		}

		@Override
		public int size() {
			final int size1 = this.items1.size(), size2 = this.items2.size();
			int result = 0;
			if (size1 < size2) {
				for (final Object item: this.items1)
					if (this.items2.contains(item)) {
						result++;
					}
			} else {
				for (final Object item: this.items2)
					if (this.items1.contains(item)) {
						result++;
					}
			}
			return result;
		}

		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() || this.items2.isEmpty() || !this.iterator().hasNext();
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.unmodifiableIterator(this.items1.size() < this.items2.size() //
				? Iterators.filteredIterator(Collections.toContainsFilter(this.items2), this.items1.iterator()) //
				: Iterators.filteredIterator(Collections.toContainsFilter(this.items1), this.items2.iterator()) //
			);
		}

		@Override
		public boolean contains(final Object item) {
			return this.items1.contains(item) && this.items2.contains(item);
		}

	}

	/** Diese Klasse implementiert {@link Collections#reverseList(List)} */
	@SuppressWarnings ("javadoc")
	public static class ReverseList<GItem> extends AbstractList<GItem> {

		class ReverseIterator implements ListIterator<GItem> {

			final ListIterator<GItem> iter;

			ReverseIterator(final int index) {
				final List<GItem> items = ReverseList.this.items;
				this.iter = items.listIterator(items.size() - index);
			}

			@Override
			public boolean hasNext() {
				return this.iter.hasPrevious();
			}

			@Override
			public boolean hasPrevious() {
				return this.iter.hasNext();
			}

			@Override
			public void set(final GItem item2) {
				this.iter.set(item2);
			}

			@Override
			public void add(final GItem item2) {
				this.iter.add(item2);
				this.iter.hasPrevious();
				this.iter.previous();
			}

			@Override
			public GItem next() {
				return this.iter.previous();
			}

			@Override
			public int nextIndex() {
				return ReverseList.this.items.size() - this.iter.previousIndex() - 1;
			}

			@Override
			public GItem previous() {
				return this.iter.next();
			}

			@Override
			public int previousIndex() {
				return ReverseList.this.items.size() - this.iter.nextIndex() - 1;
			}

			@Override
			public void remove() {
				this.iter.remove();
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
			return new ReverseIterator(index);
		}

	}

	/** Diese Klasse implementiert {@link Collections#chainedList(List, List, boolean)} */
	@SuppressWarnings ("javadoc")
	public static class ChainedList<GItem> extends AbstractList<GItem> {

		public final List<GItem> items1;

		public final List<GItem> items2;

		public final boolean extendMode;

		public ChainedList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) {
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
			return Iterators.chainedIterator(this.items1.iterator(), this.items2.iterator());
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

				int size = ChainedList.this.items1.size();

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
					} else if (ChainedList.this.extendMode) {
						iterator1.add(item);
					} else {
						iterator2.add(item);
					}
					this.size = ChainedList.this.items1.size();
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
					this.size = ChainedList.this.items1.size();
				}

			};
		}
	}

	/** Diese Klasse implementiert {@link Collections#chainedCollection(Collection, Collection, boolean)} */
	@SuppressWarnings ("javadoc")
	public static class ChainedCollection<GItem> extends AbstractCollection<GItem> {

		public final Collection<GItem> items1;

		public final Collection<GItem> items2;

		public final boolean extendMode;

		public ChainedCollection(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode) {
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
			return Iterators.chainedIterator(this.items1.iterator(), this.items2.iterator());
		}

	}

	/** Diese Klasse implementiert {@link Collections#translatedMap(Map, Translator, Translator)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedMap<GTargetKey, GTargetValue, GSourceKey, GSourceValue> extends AbstractMap<GTargetKey, GTargetValue> {

		class SourceEntry extends SimpleEntry<GSourceKey, GSourceValue> {

			private static final long serialVersionUID = -1304243507468561381L;

			final Entry<GTargetKey, GTargetValue> entry;

			SourceEntry(final Entry<GTargetKey, GTargetValue> entry) {
				super(TranslatedMap.this.keyTranslator.toSource(entry.getKey()), TranslatedMap.this.valueTranslator.toSource(entry.getValue()));
				this.entry = entry;
			}

			@Override
			public GSourceValue setValue(final GSourceValue value2) {
				super.setValue(value2);
				return TranslatedMap.this.valueTranslator.toSource(this.entry.setValue(TranslatedMap.this.valueTranslator.toTarget(value2)));
			}

		}

		class TargetEntry extends SimpleEntry<GTargetKey, GTargetValue> {

			private static final long serialVersionUID = 3378767550974847519L;

			final Entry<GSourceKey, GSourceValue> entry;

			TargetEntry(final Entry<GSourceKey, GSourceValue> entry) {
				super(TranslatedMap.this.keyTranslator.toTarget(entry.getKey()), TranslatedMap.this.valueTranslator.toTarget(entry.getValue()));
				this.entry = entry;
			}

			@Override
			public GTargetValue setValue(final GTargetValue value) {
				super.setValue(value);
				return TranslatedMap.this.valueTranslator.toTarget(this.entry.setValue(TranslatedMap.this.valueTranslator.toSource(value)));
			}

		}

		class EntryTranslator implements Translator<Entry<GSourceKey, GSourceValue>, Entry<GTargetKey, GTargetValue>> {

			@Override
			public boolean isSource(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				return TranslatedMap.this.keyTranslator.isSource(entry.getKey()) && TranslatedMap.this.valueTranslator.isSource(entry.getValue());
			}

			@Override
			public boolean isTarget(final Object object) {
				if (!(object instanceof Entry)) return false;
				final Entry<?, ?> entry = (Entry<?, ?>)object;
				return TranslatedMap.this.keyTranslator.isTarget(entry.getKey()) && TranslatedMap.this.valueTranslator.isTarget(entry.getValue());
			}

			@Override
			@SuppressWarnings ("unchecked")
			public Entry<GSourceKey, GSourceValue> toSource(final Object object) throws ClassCastException, IllegalArgumentException {
				return new SourceEntry((Entry<GTargetKey, GTargetValue>)object);
			}

			@Override
			@SuppressWarnings ("unchecked")
			public Entry<GTargetKey, GTargetValue> toTarget(final Object object) throws ClassCastException, IllegalArgumentException {
				return new TargetEntry((Entry<GSourceKey, GSourceValue>)object);
			}

		}

		public final Map<GSourceKey, GSourceValue> entries;

		public final Translator<GSourceKey, GTargetKey> keyTranslator;

		public final Translator<GSourceValue, GTargetValue> valueTranslator;

		public TranslatedMap(final Map<GSourceKey, GSourceValue> entries, final Translator<GSourceKey, GTargetKey> keyTranslator,
			final Translator<GSourceValue, GTargetValue> valueTranslator) {
			this.entries = Objects.notNull(entries);
			this.keyTranslator = Objects.notNull(keyTranslator);
			this.valueTranslator = Objects.notNull(valueTranslator);
		}

		@Override
		public void clear() {
			this.entries.clear();
		}

		@Override
		public boolean containsKey(final Object key2) {
			if (!this.keyTranslator.isTarget(key2)) return false;
			return this.entries.containsKey(this.keyTranslator.toSource(key2));
		}

		@Override
		public boolean containsValue(final Object value2) {
			if (!this.valueTranslator.isTarget(value2)) return false;
			return this.entries.containsKey(this.valueTranslator.toSource(value2));
		}

		@Override
		public GTargetValue get(final Object key2) {
			if (!this.keyTranslator.isTarget(key2)) return null;
			return this.valueTranslator.toTarget(this.entries.get(this.keyTranslator.toSource(key2)));
		}

		@Override
		public boolean isEmpty() {
			return this.entries.isEmpty();
		}

		@Override
		public Set<GTargetKey> keySet() {
			return Collections.translatedSet(this.entries.keySet(), this.keyTranslator);
		}

		@Override
		public GTargetValue put(final GTargetKey key2, final GTargetValue value2) {
			return this.valueTranslator.toTarget(this.entries.put(this.keyTranslator.toSource(key2), this.valueTranslator.toSource(value2)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public void putAll(final Map<? extends GTargetKey, ? extends GTargetValue> entries2) {
			this.entries.putAll(Collections.translatedMap((Map<GTargetKey, GTargetValue>)entries2, Translators.reverseTranslator(this.keyTranslator),
				Translators.reverseTranslator(this.valueTranslator)));
		}

		@Override
		public GTargetValue remove(final Object key2) {
			if (!this.keyTranslator.isTarget(key2)) return null;
			return this.valueTranslator.toTarget(this.entries.remove(this.keyTranslator.toSource(key2)));
		}

		@Override
		public int size() {
			return this.entries.size();
		}

		@Override
		public Collection<GTargetValue> values() {
			return Collections.translatedCollection(this.entries.values(), this.valueTranslator);
		}

		@Override
		public Set<Entry<GTargetKey, GTargetValue>> entrySet() {
			return Collections.translatedSet(this.entries.entrySet(), new EntryTranslator());
		}

	}

	/** Diese Klasse implementiert {@link Collections#translatedList(List, Translator)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedList<GSource, GTarget> extends AbstractList<GTarget> {

		public final List<GSource> items;

		public final Translator<GSource, GTarget> translator;

		public TranslatedList(final List<GSource> items, final Translator<GSource, GTarget> translator) {
			this.items = Objects.notNull(items);
			this.translator = Objects.notNull(translator);
		}

		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.items.subList(fromIndex, toIndex).clear();
		}

		@Override
		public GTarget get(final int index) {
			return this.translator.toTarget(this.items.get(index));
		}

		@Override
		public GTarget set(final int index, final GTarget item2) {
			return this.translator.toTarget(this.items.set(index, this.translator.toSource(item2)));
		}

		@Override
		public boolean add(final GTarget item2) {
			return this.items.add(this.translator.toSource(item2));
		}

		@Override
		public void add(final int index, final GTarget item2) {
			this.items.add(index, this.translator.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final Collection<? extends GTarget> items2) {
			return this.items.addAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final int index, final Collection<? extends GTarget> items2) {
			return this.items.addAll(index, Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		public GTarget remove(final int index) {
			return this.translator.toTarget(this.items.remove(index));
		}

		@Override
		public boolean remove(final Object item2) {
			if (!this.translator.isTarget(item2)) return false;
			return this.items.remove(this.translator.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(final Collection<?> items2) {
			return this.items.removeAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(final Collection<?> items2) {
			return this.items.retainAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
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
		public boolean contains(final Object item2) {
			if (!this.translator.isTarget(item2)) return false;
			return this.items.contains(this.translator.toSource(item2));
		}

		@Override
		public Iterator<GTarget> iterator() {
			return Iterators.translatedIterator(Getters.fromTarget(this.translator), this.items.iterator());
		}

		@Override
		public int indexOf(final Object item2) {
			if (!this.translator.isTarget(item2)) return -1;
			return this.items.indexOf(this.translator.toSource(item2));
		}

		@Override
		public int lastIndexOf(final Object item2) {
			if (!this.translator.isTarget(item2)) return -1;
			return this.items.lastIndexOf(this.translator.toSource(item2));
		}
	}

	/** Diese Klasse implementiert {@link Collections#translatedSet(Set, Translator)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedSet<GSource, GTarget> extends AbstractSet<GTarget> {

		public final Translator<GSource, GTarget> translator;

		public final Set<GSource> items;

		public TranslatedSet(final Translator<GSource, GTarget> translator, final Set<GSource> items) {
			this.items = Objects.notNull(items);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public boolean add(final GTarget item2) {
			return this.items.add(this.translator.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final Collection<? extends GTarget> items2) {
			return this.items.addAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		public boolean remove(final Object item2) {
			if (!this.translator.isTarget(item2)) return false;
			return this.items.remove(this.translator.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(final Collection<?> items2) {
			return this.items.removeAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(final Collection<?> items2) {
			return this.items.retainAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
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
		public boolean contains(final Object item2) {
			if (!this.translator.isTarget(item2)) return false;
			return this.items.contains(this.translator.toSource(item2));
		}

		@Override
		public Iterator<GTarget> iterator() {
			return Iterators.translatedIterator(Getters.fromTarget(this.translator), this.items.iterator());
		}

	}

	/** Diese Klasse implementiert {@link Collections#translatedCollection(Collection, Translator)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedCollection<GSource, GTarget> extends AbstractCollection<GTarget> {

		public final Collection<GSource> items;

		public final Translator<GSource, GTarget> translator;

		public TranslatedCollection(final Collection<GSource> items, final Translator<GSource, GTarget> translator) {
			this.items = Objects.notNull(items);
			this.translator = Objects.notNull(translator);
		}

		@Override
		public boolean add(final GTarget item2) {
			return this.items.add(this.translator.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean addAll(final Collection<? extends GTarget> items2) {
			return this.items.addAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		public boolean remove(final Object item2) {
			if (!this.translator.isTarget(item2)) return false;
			return this.items.remove(this.translator.toSource(item2));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean removeAll(final Collection<?> items2) {
			return this.items.removeAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
		}

		@Override
		@SuppressWarnings ("unchecked")
		public boolean retainAll(final Collection<?> items2) {
			return this.items.retainAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(this.translator)));
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
		public boolean contains(final Object item2) {
			if (!this.translator.isTarget(item2)) return false;
			return this.items.contains(this.translator.toSource(item2));
		}

		@Override
		public Iterator<GTarget> iterator() {
			return Iterators.translatedIterator(Getters.fromTarget(this.translator), this.items.iterator());
		}

	}

	/** Diese Klasse implementiert {@link Collections#toContainsFilter(Collection)} */
	static class ContainsFilter implements Filter<Object> {

		public final Collection<?> collection;

		public ContainsFilter(final Collection<?> collection) {
			this.collection = Objects.notNull(collection);
		}

		@Override
		public boolean accept(final Object item) {
			return this.collection.contains(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.collection);
		}

	}

	/** Diese Methode gibt ein {@link Set} als unveränderliche Sicht auf die Vereinigungsmenge der gegebenen {@link Set} zurück.
	 *
	 * @param <GItem> Typ der Elemente der Vereinigungsmenge.
	 * @param items1 Erstes {@link Set}.
	 * @param items2 Zweites {@link Set}.
	 * @return {@code union}-{@link Set}.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Set<GItem> unionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		return new UnionSet<>(items1, items2);
	}

	/** Diese Methode gibt ein {@link Set} als unveränderliche Sicht auf das Kartesische Produkt der gegebenen {@link Set} zurück.
	 *
	 * @param <GKey> Typ der Elemente der Schlüsselmenge.
	 * @param <GValue> Typ der Elemente der Wertmenge.
	 * @param keys Schlüsselmenge.
	 * @param values Wertmenge.
	 * @return {@code cartesian}-{@link Set}.
	 * @throws NullPointerException Wenn {@code keys} bzw. {@code values} {@code null} ist. */
	public static <GKey, GValue> Set<Entry<GKey, GValue>> cartesianSet(final Set<? extends GKey> keys, final Set<? extends GValue> values)
		throws NullPointerException {
		return new CartesianSet<>(keys, values);
	}

	/** Diese Methode gibt ein {@link Set} als unveränderliche Sicht auf die Schnittmenge der gegebenen {@link Set} zurück.
	 *
	 * @param <GItem> Typ der Elemente der Schnittmenge.
	 * @param items1 Erstes {@link Set}.
	 * @param items2 Zweites {@link Set}.
	 * @return {@code intersection}-{@link Set}.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Set<GItem> intersectionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		return new IntersectionSet<>(items1, items2);
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf die gegebene {@link List} zurück.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List}
	 * @return {@code reverse}-{@link List}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static <GItem> List<GItem> reverseList(final List<GItem> items) throws NullPointerException {
		return new ReverseList<>(items);
	}

	/** Diese Methode erzeugt eine {@link List} als verkettete Sicht auf die gegebenen {@link List} und gibt diese zurück. Wenn ein Elemente zwischen beiden
	 * {@link List} eingefügt werden sollen, wird die erste {@link List} erweitert. Der Rückgabewert entspricht
	 * {@code Collections.chainedList(items1, items2, true)}.
	 *
	 * @see #chainedCollection(Collection, Collection, boolean)
	 * @param <GTarget> Typ der Elemente.
	 * @param items1 {@link List} der ersten Elemente.
	 * @param items2 {@link List} der letzten Elemente.
	 * @return verkettete {@link List}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GTarget> List<GTarget> chainedList(final List<GTarget> items1, final List<GTarget> items2) throws NullPointerException {
		return Collections.chainedList(items1, items2, true);
	}

	/** Diese Methode erzeugt eine {@link List} als verkettete Sicht auf die gegebenen {@link List} und gibt diese zurück. Wenn ein Elemente zwischen beiden
	 * {@link List} eingefügt werden sollen, entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus
	 * {@code true}, wird die erste {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link List} der ersten Elemente.
	 * @param items2 {@link List} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link List}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> List<GItem> chainedList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) throws NullPointerException {
		return new ChainedList<>(items1, items2, extendMode);
	}

	/** Diese Methode gibt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection} zurück. Wenn Elemente angefügt werden sollen, wird
	 * die erste {@link Collection} erweitert. Der Rückgabewert entspricht {@code Collections.chainedCollection(items1, items2, true)}.
	 *
	 * @see #chainedCollection(Collection, Collection, boolean)
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link Collection} der ersten Elemente.
	 * @param items2 {@link Collection} der letzten Elemente.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Collection<GItem> chainedCollection(final Collection<GItem> items1, final Collection<GItem> items2) throws NullPointerException {
		return Collections.chainedCollection(items1, items2, true);
	}

	/** Diese Methode gibt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection} zurück. Wenn Elemente angefügt werden sollen,
	 * entscheidet der Erweiterungsmodus, in welche {@link Collection} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
	 * {@link Collection} erweitert, bei {@code false} wird die zweite {@link Collection} erweitert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link Collection} der ersten Elemente.
	 * @param items2 {@link Collection} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Collection<GItem> chainedCollection(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode)
		throws NullPointerException {
		return new ChainedCollection<>(items1, items2, extendMode);
	}

	/** Diese Methode gibt eine {@link Map} als übersetzte Sicht auf die gegebene {@link Map} zurück.
	 *
	 * @param <GSourceKey> Typ der Schlüssel der gegebenen {@link Map}.
	 * @param <GSourceValue> Typ der Werte der gegebenen {@link Map}.
	 * @param <GTargetKey> Typ der Schlüssel der erzeugten {@link Map}.
	 * @param <GTargetValue> Typ der Werte der erzeugten {@link Map}.
	 * @param entries Gegebene {@link Map}.
	 * @param keyTranslator {@link Translator} zur Übersetzung der Schlüssel.
	 * @param valueTranslator {@link Translator} zur Übersetzung der Werte.
	 * @return {@code translated}-{@link Map}.
	 * @throws NullPointerException Wenn {@code entries}, {@code keyTranslator} bzw. {@code valueTranslator} {@code null} ist. */
	public static <GSourceKey, GSourceValue, GTargetKey, GTargetValue> Map<GTargetKey, GTargetValue> translatedMap(final Map<GSourceKey, GSourceValue> entries,
		final Translator<GSourceKey, GTargetKey> keyTranslator, final Translator<GSourceValue, GTargetValue> valueTranslator) throws NullPointerException {
		return new TranslatedMap<>(entries, keyTranslator, valueTranslator);
	}

	/** Diese Methode gibt eine {@link List} als übersetzte Sicht auf die gegebene {@link List} zurück.
	 *
	 * @param <GSource> Typ der Elemente der gegebenen {@link List}.
	 * @param <GTarget> Typ der Elemente der erzeugten {@link List}.
	 * @param items Gegebene {@link List}.
	 * @param translator {@link Translator} zur Übersetzung der Elemente.
	 * @return {@code translated}-{@link List}.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code translator} {@code null} ist. */
	public static <GSource, GTarget> List<GTarget> translatedList(final List<GSource> items, final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return new TranslatedList<>(items, translator);
	}

	/** Diese Methode gibt ein {@link Set} als übersetzte Sicht auf das gegebene {@link Set} zurück.
	 *
	 * @param <GSource> Typ der Elemente des gegebenen {@link Set}.
	 * @param <GTarget> Typ der Elemente des erzeugten {@link Set}.
	 * @param items Gegebenes {@link Set}.
	 * @param translator {@link Translator} zur Übersetzung der Elemente.
	 * @return {@code translated}-{@link Set}.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Set<GTarget> translatedSet(final Set<GSource> items, final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return new TranslatedSet<>(translator, items);
	}

	/** Diese Methode gibt eine {@link Collection} als übersetzte Sicht auf die gegebene {@link Collection} zurück.
	 *
	 * @param <GSource> Typ der Elemente der gegebenen {@link Collection}.
	 * @param <GTarget> Typ der Elemente der erzeugten {@link Collection}.
	 * @param items Gegebene {@link Collection}.
	 * @param translator {@link Translator} zur Übersetzung der Elemente.
	 * @return {@code translated}-{@link Collection}.
	 * @throws NullPointerException Wenn {@code items} bzw. {@code translator} {@code null} ist. */
	public static <GSource, GTarget> Collection<GTarget> translatedCollection(final Collection<GSource> items, final Translator<GSource, GTarget> translator)
		throws NullPointerException {
		return new TranslatedCollection<>(items, translator);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die gegebenen Eingaben akzeptiert.
	 *
	 * @see #toContainsFilter(Collection)
	 * @param items akzeptierte Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static Filter<Object> toContainsFilter(final Object... items) throws NullPointerException {
		if (items.length == 0) return Filters.valueFilter(false);
		if (items.length == 1) return Collections.toContainsFilter(java.util.Collections.singleton(items[0]));
		return Collections.toContainsFilter(new HashSet2<>(Arrays.asList(items)));
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die in der gegebenen {@link Collection} enthalten sind. Die Akzeptanz
	 * einer Eingabe {@code input} ist {@code collection.contains(input)}.
	 *
	 * @param collection {@link Collection} der akzeptierten Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code collection} {@code null} ist. */
	public static Filter<Object> toContainsFilter(final Collection<?> collection) throws NullPointerException {
		return new ContainsFilter(collection);
	}

}
