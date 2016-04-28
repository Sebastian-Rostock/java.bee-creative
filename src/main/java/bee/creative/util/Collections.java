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
import java.util.Set;

/** Diese Klasse implementiert umordnende, zusammenführende bzw. umwandelnde Sichten für {@link Set}, {@link Map}, {@link List} und {@link Collection}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Collections {

	/** Diese Methode gibt ein {@link Set} als unveränderliche Sicht auf die Vereinigungsmenge der gegebenen {@link Set} zurück.
	 * 
	 * @param <GItem> Typ der Elemente der Vereinigungsmenge.
	 * @param items1 Erstes {@link Set}.
	 * @param items2 Zweites {@link Set}.
	 * @return {@code union}-{@link Set}.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Set<GItem> unionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		if (items1 == null) throw new NullPointerException("items1 = null");
		if (items2 == null) throw new NullPointerException("items2 = null");
		return new AbstractSet<GItem>() {

			@Override
			public int size() {
				return Iterables.size(this);
			}

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.unmodifiableIterator(items1.size() < items2.size() //
					? Iterators.chainedIterator(Iterators.filteredIterator(Filters.negationFilter(Filters.containsFilter(items2)), items1.iterator()), items2.iterator()) //
					: Iterators.chainedIterator(Iterators.filteredIterator(Filters.negationFilter(Filters.containsFilter(items1)), items2.iterator()), items1.iterator()) //
					);
			}

			@Override
			public boolean contains(final Object item) {
				return items1.contains(item) || items2.contains(item);
			}

		};
	}

	/** Diese Methode gibt ein {@link Set} als unveränderliche Sicht auf die Schnittmenge der gegebenen {@link Set} zurück.
	 * 
	 * @param <GItem> Typ der Elemente der Schnittmenge.
	 * @param items1 Erstes {@link Set}.
	 * @param items2 Zweites {@link Set}.
	 * @return {@code intersection}-{@link Set}.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Set<GItem> intersectionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
		if (items1 == null) throw new NullPointerException("items1 = null");
		if (items2 == null) throw new NullPointerException("items2 = null");
		return new AbstractSet<GItem>() {

			@Override
			public int size() {
				return Iterables.size(this);
			}

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.unmodifiableIterator(items1.size() < items2.size() //
					? Iterators.filteredIterator(Filters.containsFilter(items2), items1.iterator()) //
					: Iterators.filteredIterator(Filters.containsFilter(items1), items2.iterator()) //
					);
			}

			@Override
			public boolean contains(final Object item) {
				return items1.contains(item) && items2.contains(item);
			}

		};
	}

	/** Diese Methode gibt eine rückwärts geordnete Sicht auf die gegebene {@link List} zurück.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items {@link List}
	 * @return {@code reverse}-{@link List}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static <GItem> List<GItem> reverseList(final List<GItem> items) throws NullPointerException {
		if (items == null) throw new NullPointerException("items = null");
		return new AbstractList<GItem>() {

			@Override
			protected void removeRange(final int fromIndex, final int toIndex) {
				final int size = items.size();
				items.subList(size - toIndex, size - fromIndex).clear();
			}

			@Override
			public GItem get(final int index) {
				return items.get(items.size() - index - 1);
			}

			@Override
			public GItem set(final int index, final GItem item2) {
				return items.set(items.size() - index - 1, item2);
			}

			@Override
			public void add(final int index, final GItem item2) {
				items.add(items.size() - index, item2);
			}

			@Override
			public boolean retainAll(final Collection<?> items2) {
				return items.retainAll(items2);
			}

			@Override
			public GItem remove(final int index) {
				return items.remove(items.size() - index - 1);
			}

			@Override
			public boolean removeAll(final Collection<?> items2) {
				return items.removeAll(items2);
			}

			@Override
			public int size() {
				return items.size();
			}

			@Override
			public void clear() {
				items.clear();
			}

			@Override
			public boolean isEmpty() {
				return items.isEmpty();
			}

			@Override
			public int indexOf(final Object item2) {
				final int index = items.lastIndexOf(item2);
				return index < 0 ? -1 : items.size() - index - 1;
			}

			@Override
			public int lastIndexOf(final Object item2) {
				final int index = items.indexOf(item2);
				return index < 0 ? -1 : items.size() - index - 1;
			}

			@Override
			public boolean contains(final Object item2) {
				return items.contains(item2);
			}

			@Override
			public boolean containsAll(final Collection<?> items2) {
				return items.containsAll(items2);
			}

			@Override
			public Iterator<GItem> iterator() {
				return this.listIterator(0);
			}

			@Override
			public ListIterator<GItem> listIterator(int index) {
				final ListIterator<GItem> iterator = items.listIterator(items.size() - index);
				index = 0;
				return new ListIterator<GItem>() {

					@Override
					public boolean hasNext() {
						return iterator.hasPrevious();
					}

					@Override
					public boolean hasPrevious() {
						return iterator.hasNext();
					}

					@Override
					public void set(final GItem item2) {
						iterator.set(item2);
					}

					@Override
					public void add(final GItem item2) {
						iterator.add(item2);
						iterator.hasPrevious();
						iterator.previous();
					}

					@Override
					public GItem next() {
						return iterator.previous();
					}

					@Override
					public int nextIndex() {
						return items.size() - iterator.previousIndex() - 1;
					}

					@Override
					public GItem previous() {
						return iterator.next();
					}

					@Override
					public int previousIndex() {
						return items.size() - iterator.nextIndex() - 1;
					}

					@Override
					public void remove() {
						iterator.remove();
					}

				};
			}

			@Override
			public List<GItem> subList(final int fromIndex, final int toIndex) {
				return Collections.reverseList(items.subList(items.size() - toIndex - 2, items.size() - fromIndex - 2));
			}

		};
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
		if (items1 == null) throw new NullPointerException("items1 = null");
		if (items2 == null) throw new NullPointerException("items2 = null");
		return new AbstractList<GItem>() {

			@Override
			public GItem get(final int index) {
				final int size = items1.size();
				return index < size ? items1.get(index) : items2.get(index - size);
			}

			@Override
			public GItem set(final int index, final GItem item) {
				final int size = items1.size();
				return index < size ? items1.set(index, item) : items2.set(index - size, item);
			}

			@Override
			public void add(final int index, final GItem item) {
				final int size = items1.size();
				if ((index < size) || ((index == size) && extendMode)) {
					items1.add(index, item);
				} else {
					items2.add(index - size, item);
				}
			}

			@Override
			public boolean addAll(final int index, final Collection<? extends GItem> items) {
				final int size = items1.size();
				if ((index < size) || ((index == size) && extendMode)) return items1.addAll(index, items);
				return items2.addAll(index - size, items);
			}

			@Override
			public boolean retainAll(final Collection<?> items) {
				if (!items1.retainAll(items)) return items2.retainAll(items);
				items2.retainAll(items);
				return true;
			}

			@Override
			public GItem remove(final int index) {
				final int size = items1.size();
				return index < size ? items1.remove(index) : items2.remove(index - size);
			}

			@Override
			public boolean remove(final Object item) {
				return items1.remove(item) || items2.remove(item);
			}

			@Override
			public boolean removeAll(final Collection<?> item) {
				if (!items1.removeAll(item)) return items2.removeAll(item);
				items2.removeAll(item);
				return true;
			}

			@Override
			public int size() {
				return items1.size() + items2.size();
			}

			@Override
			public void clear() {
				items1.clear();
				items2.clear();
			}

			@Override
			public boolean isEmpty() {
				return items1.isEmpty() && items2.isEmpty();
			}

			@Override
			public int indexOf(final Object item) {
				int index = items1.indexOf(item);
				if (index >= 0) return index;
				index = items2.indexOf(item);
				return index < 0 ? -1 : index + items1.size();
			}

			@Override
			public int lastIndexOf(final Object item) {
				final int index = items2.lastIndexOf(item);
				if (index >= 0) return index + items1.size();
				return items1.lastIndexOf(item);
			}

			@Override
			public boolean contains(final Object item) {
				return items1.contains(item) || items2.contains(item);
			}

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.chainedIterator(items1.iterator(), items2.iterator());
			}

			@Override
			public ListIterator<GItem> listIterator(final int index) {
				final ListIterator<GItem> iterator1, iterator2;
				int size = items1.size();
				if (index < size) {
					iterator1 = items1.listIterator(index);
					iterator2 = items2.listIterator(0);
				} else {
					iterator1 = items1.listIterator(size);
					iterator2 = items2.listIterator(index - size);
				}
				size = 0;
				return new ListIterator<GItem>() {

					int size = items1.size();

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
						} else if (extendMode) {
							iterator1.add(item);
						} else {
							iterator2.add(item);
						}
						this.size = items1.size();
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
						this.size = items1.size();
					}

				};
			}

		};
	}

	/** Diese Methode gibt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection} zurück.<br>
	 * Wenn Elemente angefügt werden sollen, wird die erste {@link Collection} erweitert. Der Rückgabewert entspricht
	 * {@code Collections.chainedCollection(items1, items2, true)}.
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

	/** Diese Methode gibt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection} zurück.<br>
	 * Wenn Elemente angefügt werden sollen, entscheidet der Erweiterungsmodus, in welche {@link Collection} diese Elemente angefügt werden. Ist der
	 * Erweiterungsmodus {@code true}, wird die erste {@link Collection} erweitert, bei {@code false} wird die zweite {@link Collection} erweitert.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link Collection} der ersten Elemente.
	 * @param items2 {@link Collection} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist. */
	public static <GItem> Collection<GItem> chainedCollection(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode)
		throws NullPointerException {
		if (items1 == null) throw new NullPointerException("items1 = null");
		if (items2 == null) throw new NullPointerException("items2 = null");
		return new AbstractCollection<GItem>() {

			@Override
			public boolean add(final GItem item) {
				return (extendMode ? items1 : items2).add(item);
			}

			@Override
			public boolean addAll(final Collection<? extends GItem> items) {
				return (extendMode ? items1 : items2).addAll(items);
			}

			@Override
			public boolean retainAll(final Collection<?> items) {
				if (!items1.retainAll(items)) return items2.retainAll(items);
				items2.retainAll(items);
				return true;
			}

			@Override
			public boolean remove(final Object item) {
				return items1.remove(item) || items2.remove(item);
			}

			@Override
			public boolean removeAll(final Collection<?> items) {
				if (!items1.removeAll(items)) return items2.removeAll(items);
				items2.removeAll(items);
				return true;
			}

			@Override
			public int size() {
				return items1.size() + items2.size();
			}

			@Override
			public void clear() {
				items1.clear();
				items2.clear();
			}

			@Override
			public boolean contains(final Object item) {
				return items1.contains(item) || items2.contains(item);
			}

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.chainedIterator(items1.iterator(), items2.iterator());
			}

		};
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
	public static <GSourceKey, GSourceValue, GTargetKey, GTargetValue> Map<GTargetKey, GTargetValue> translatedMap(
		final Map<GSourceKey, GSourceValue> entries, final Translator<GSourceKey, GTargetKey> keyTranslator,
		final Translator<GSourceValue, GTargetValue> valueTranslator) throws NullPointerException {
		if (entries == null) throw new NullPointerException("entries = null");
		if (keyTranslator == null) throw new NullPointerException("keyTranslator = null");
		if (valueTranslator == null) throw new NullPointerException("valueTranslator = null");
		return new AbstractMap<GTargetKey, GTargetValue>() {

			@Override
			public void clear() {
				entries.clear();
			}

			@Override
			public boolean containsKey(final Object key2) {
				if (!keyTranslator.isTarget(key2)) return false;
				return entries.containsKey(keyTranslator.toSource(key2));
			}

			@Override
			public boolean containsValue(final Object value2) {
				if (!valueTranslator.isTarget(value2)) return false;
				return entries.containsKey(valueTranslator.toSource(value2));
			}

			@Override
			public GTargetValue get(final Object key2) {
				if (!keyTranslator.isTarget(key2)) return null;
				return valueTranslator.toTarget(entries.get(keyTranslator.toSource(key2)));
			}

			@Override
			public boolean isEmpty() {
				return entries.isEmpty();
			}

			@Override
			public Set<GTargetKey> keySet() {
				return Collections.translatedSet(entries.keySet(), keyTranslator);
			}

			@Override
			public GTargetValue put(final GTargetKey key2, final GTargetValue value2) {
				return valueTranslator.toTarget(entries.put(keyTranslator.toSource(key2), valueTranslator.toSource(value2)));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public void putAll(final Map<? extends GTargetKey, ? extends GTargetValue> entries2) {
				entries.putAll(Collections.translatedMap((Map<GTargetKey, GTargetValue>)entries2, Translators.reverseTranslator(keyTranslator),
					Translators.reverseTranslator(valueTranslator)));
			}

			@Override
			public GTargetValue remove(final Object key2) {
				if (!keyTranslator.isTarget(key2)) return null;
				return valueTranslator.toTarget(entries.remove(keyTranslator.toSource(key2)));
			}

			@Override
			public int size() {
				return entries.size();
			}

			@Override
			public Collection<GTargetValue> values() {
				return Collections.translatedCollection(entries.values(), valueTranslator);
			}

			@Override
			public Set<Entry<GTargetKey, GTargetValue>> entrySet() {
				return Collections.translatedSet(entries.entrySet(), new Translator<Entry<GSourceKey, GSourceValue>, Entry<GTargetKey, GTargetValue>>() {

					@Override
					public boolean isTarget(final Object object) {
						if (!(object instanceof Entry)) return false;
						final Entry<?, ?> entry = (Entry<?, ?>)object;
						return keyTranslator.isTarget(entry.getKey()) && valueTranslator.isTarget(entry.getValue());
					}

					@Override
					public boolean isSource(final Object object) {
						if (!(object instanceof Entry)) return false;
						final Entry<?, ?> entry = (Entry<?, ?>)object;
						return keyTranslator.isSource(entry.getKey()) && valueTranslator.isSource(entry.getValue());
					}

					@Override
					@SuppressWarnings ("unchecked")
					public Entry<GTargetKey, GTargetValue> toTarget(Object object) throws ClassCastException, IllegalArgumentException {
						final Entry<?, GSourceValue> entry = (Entry<?, GSourceValue>)object;
						object = null;
						return new SimpleEntry<GTargetKey, GTargetValue>(keyTranslator.toTarget(entry.getKey()), valueTranslator.toTarget(entry.getValue())) {

							private static final long serialVersionUID = 9189869604170030443L;

							@Override
							public GTargetValue setValue(final GTargetValue value2) {
								super.setValue(value2);
								return valueTranslator.toTarget(entry.setValue(valueTranslator.toSource(value2)));
							}

						};
					}

					@Override
					@SuppressWarnings ("unchecked")
					public Entry<GSourceKey, GSourceValue> toSource(Object object) throws ClassCastException, IllegalArgumentException {
						final Entry<?, GSourceValue> entry = (Entry<?, GSourceValue>)object;
						object = null;
						return new SimpleEntry<GSourceKey, GSourceValue>(keyTranslator.toSource(entry.getKey()), valueTranslator.toSource(entry.getValue())) {

							private static final long serialVersionUID = 6699416526369328676L;

							@Override
							public GSourceValue setValue(final GSourceValue value2) {
								super.setValue(value2);
								return valueTranslator.toSource(entry.setValue(valueTranslator.toSource(value2)));
							}

						};
					}

				});
			}

		};
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
		if (items == null) throw new NullPointerException("items = null");
		if (translator == null) throw new NullPointerException("translator = null");
		return new AbstractList<GTarget>() {

			@Override
			protected void removeRange(final int fromIndex, final int toIndex) {
				items.subList(fromIndex, toIndex).clear();
			}

			@Override
			public GTarget get(final int index) {
				return translator.toTarget(items.get(index));
			}

			@Override
			public GTarget set(final int index, final GTarget item2) {
				return translator.toTarget(items.set(index, translator.toSource(item2)));
			}

			@Override
			public boolean add(final GTarget item2) {
				return items.add(translator.toSource(item2));
			}

			@Override
			public void add(final int index, final GTarget item2) {
				items.add(index, translator.toSource(item2));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean addAll(final Collection<? extends GTarget> items2) {
				return items.addAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean addAll(final int index, final Collection<? extends GTarget> items2) {
				return items.addAll(index, Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			public GTarget remove(final int index) {
				return translator.toTarget(items.remove(index));
			}

			@Override
			public boolean remove(final Object item2) {
				if (!translator.isTarget(item2)) return false;
				return items.remove(translator.toSource(item2));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean removeAll(final Collection<?> items2) {
				return items.removeAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean retainAll(final Collection<?> items2) {
				return items.retainAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			public int size() {
				return items.size();
			}

			@Override
			public void clear() {
				items.clear();
			}

			@Override
			public boolean isEmpty() {
				return items.isEmpty();
			}

			@Override
			public boolean contains(final Object item2) {
				if (!translator.isTarget(item2)) return false;
				return items.contains(translator.toSource(item2));
			}

			@Override
			public Iterator<GTarget> iterator() {
				return Iterators.convertedIterator(Translators.toTarget(translator), items.iterator());
			}

			@Override
			public int indexOf(final Object item2) {
				if (!translator.isTarget(item2)) return -1;
				return items.indexOf(translator.toSource(item2));
			}

			@Override
			public int lastIndexOf(final Object item2) {
				if (!translator.isTarget(item2)) return -1;
				return items.lastIndexOf(translator.toSource(item2));
			}

		};
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
		if (items == null) throw new NullPointerException("items = null");
		if (translator == null) throw new NullPointerException("translator = null");
		return new AbstractSet<GTarget>() {

			@Override
			public boolean add(final GTarget item2) {
				return items.add(translator.toSource(item2));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean addAll(final Collection<? extends GTarget> items2) {
				return items.addAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			public boolean remove(final Object item2) {
				if (!translator.isTarget(item2)) return false;
				return items.remove(translator.toSource(item2));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean removeAll(final Collection<?> items2) {
				return items.removeAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean retainAll(final Collection<?> items2) {
				return items.retainAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			public int size() {
				return items.size();
			}

			@Override
			public void clear() {
				items.clear();
			}

			@Override
			public boolean isEmpty() {
				return items.isEmpty();
			}

			@Override
			public boolean contains(final Object item2) {
				if (!translator.isTarget(item2)) return false;
				return items.contains(translator.toSource(item2));
			}

			@Override
			public Iterator<GTarget> iterator() {
				return Iterators.convertedIterator(Translators.toTarget(translator), items.iterator());
			}

		};
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
		if (items == null) throw new NullPointerException("items = null");
		if (translator == null) throw new NullPointerException("translator = null");
		return new AbstractCollection<GTarget>() {

			@Override
			public boolean add(final GTarget item2) {
				return items.add(translator.toSource(item2));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean addAll(final Collection<? extends GTarget> items2) {
				return items.addAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			public boolean remove(final Object item2) {
				if (!translator.isTarget(item2)) return false;
				return items.remove(translator.toSource(item2));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean removeAll(final Collection<?> items2) {
				return items.removeAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			@SuppressWarnings ("unchecked")
			public boolean retainAll(final Collection<?> items2) {
				return items.retainAll(Collections.translatedCollection((Collection<GTarget>)items2, Translators.reverseTranslator(translator)));
			}

			@Override
			public int size() {
				return items.size();
			}

			@Override
			public void clear() {
				items.clear();
			}

			@Override
			public boolean isEmpty() {
				return items.isEmpty();
			}

			@Override
			public boolean contains(final Object item2) {
				if (!translator.isTarget(item2)) return false;
				return items.contains(translator.toSource(item2));
			}

			@Override
			public Iterator<GTarget> iterator() {
				return Iterators.convertedIterator(Translators.toTarget(translator), items.iterator());
			}

		};
	}

}
