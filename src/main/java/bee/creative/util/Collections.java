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
import java.util.RandomAccess;
import java.util.Set;

/**
 * Diese Klasse implementiert umordnende, zusammenführende bzw. umwandelnde Sichten für {@link Set}, {@link Map}, {@link List} und {@link Collection}.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Collections {

	/**
	 * Diese Klasse implementiert eine abstrakte {@link List} als rückwärts geordnete Sicht auf eine gegebene {@link List}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class BaseReverseList<GItem> extends AbstractList<GItem> {

		/**
		 * Diese Klasse implementiert den {@link ListIterator} einer {@link BaseReverseList}.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		public static final class ReverseIterator<GItem> implements ListIterator<GItem> {

			/**
			 * Dieses Feld speichert die {@link BaseReverseList}.
			 */
			final BaseReverseList<GItem> list;

			/**
			 * Dieses Feld speichert die Größe der {@link BaseReverseList}.
			 */
			int size;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link BaseReverseList#list}.
			 */
			final ListIterator<GItem> iterator;

			/**
			 * Dieser Konstruktor initialisiert {@link BaseReverseList} und Index.
			 * 
			 * @param list {@link BaseReverseList}.
			 * @param index Index.
			 * @throws NullPointerException Wenn {@code list} {@code null} ist.
			 */
			public ReverseIterator(final BaseReverseList<GItem> list, final int index) throws NullPointerException {
				if (list == null) throw new NullPointerException("list = null");
				this.size = list.size();
				this.list = list;
				this.iterator = list.list.listIterator(this.size - index);
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return this.iterator.hasPrevious();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasPrevious() {
				return this.iterator.hasNext();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void set(final GItem e) {
				this.iterator.set(e);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void add(final GItem e) {
				this.iterator.add(e);
				this.iterator.hasPrevious();
				this.iterator.previous();
				this.size = this.list.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem next() {
				return this.iterator.previous();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int nextIndex() {
				return this.size - this.iterator.previousIndex() - 1;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem previous() {
				return this.iterator.next();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int previousIndex() {
				return this.size - this.iterator.nextIndex() - 1;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				this.iterator.remove();
				this.size = this.list.size();
			}

		}

		{}

		/**
		 * Dieses Feld speichert die {@link List}.
		 */
		final List<GItem> list;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param items {@link List}
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public BaseReverseList(final List<GItem> items) throws NullPointerException {
			if (items == null) throw new NullPointerException("items = null");
			this.list = items;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			final int size = this.list.size();
			this.list.subList(size - toIndex, size - fromIndex).clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem get(final int index) {
			return this.list.get(this.list.size() - index - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem set(final int index, final GItem element) {
			return this.list.set(this.list.size() - index - 1, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GItem element) {
			this.list.add(this.list.size() - index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.list.retainAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem remove(final int index) {
			return this.list.remove(this.list.size() - index - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.list.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.list.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.list.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.list.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			final int index = this.list.lastIndexOf(o);
			return index < 0 ? -1 : this.list.size() - index - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			final int index = this.list.indexOf(o);
			return index < 0 ? -1 : this.list.size() - index - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.list.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> c) {
			return this.list.containsAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return this.listIterator(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GItem> listIterator(final int index) {
			return new ReverseIterator<GItem>(this, index);

		}
	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link List} als verkettete Sicht auf zwei gegebenen {@link List}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class BaseChainedList<GItem> extends AbstractList<GItem> {

		/**
		 * Diese Klasse implementiert den {@link ListIterator} zu {@link BaseChainedList}.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GItem> Typ der Elemente.
		 */
		public static final class ChainedIterator<GItem> implements ListIterator<GItem> {

			/**
			 * Dieses Feld speichert die {@link BaseChainedList}.
			 */
			final BaseChainedList<GItem> list;

			/**
			 * Dieses Feld speichert die Größe von {@link BaseChainedList#items1}.
			 */
			int size;

			/**
			 * Dieses Feld speichert gewählten {@link ListIterator}.
			 */
			ListIterator<GItem> iterator;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link BaseChainedList#items1}.
			 */
			final ListIterator<GItem> iterator1;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link BaseChainedList#items2}.
			 */
			final ListIterator<GItem> iterator2;

			/**
			 * Dieser Konstruktor initialisiert {@link BaseChainedList} und Index.
			 * 
			 * @param list {@link BaseChainedList}.
			 * @param index Index.
			 * @throws NullPointerException Wenn {@code list} {@code null} ist.
			 */
			public ChainedIterator(final BaseChainedList<GItem> list, final int index) throws NullPointerException {
				if (list == null) throw new NullPointerException("list = null");
				this.size = list.items1.size();
				this.list = list;
				if (index < this.size) {
					this.iterator1 = this.iterator = list.items1.listIterator(index);
					this.iterator2 = list.items2.listIterator(0);
				} else {
					this.iterator1 = list.items1.listIterator(this.size);
					this.iterator2 = this.iterator = list.items2.listIterator(index - this.size);
				}
			}

			{}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasNext() {
				return this.iterator.hasNext() || (this.iterator = this.iterator2).hasNext();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean hasPrevious() {
				return this.iterator.hasPrevious() || (this.iterator = this.iterator1).hasPrevious();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void set(final GItem e) {
				this.iterator.set(e);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void add(final GItem e) {
				if ((this.iterator2.nextIndex() != 0) || (this.iterator1.nextIndex() != this.size)) {
					this.iterator.add(e);
				} else if (this.list.extendMode) {
					this.iterator1.add(e);
				} else {
					this.iterator2.add(e);
				}
				this.size = this.list.items1.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem next() {
				return this.iterator.next();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int nextIndex() {
				return this.iterator == this.iterator1 ? this.iterator1.nextIndex() : this.iterator2.nextIndex() + this.size;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GItem previous() {
				return this.iterator.previous();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int previousIndex() {
				return this.iterator == this.iterator1 ? this.iterator1.previousIndex() : this.iterator2.previousIndex() + this.size;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void remove() {
				this.iterator.remove();
				if (this.iterator == this.iterator2) return;
				this.size = this.list.items1.size();
			}

		}

		{}

		/**
		 * Dieses Feld speichert die erste {@link List}.
		 */
		final List<GItem> items1;

		/**
		 * Dieses Feld speichert die zweite {@link List}.
		 */
		final List<GItem> items2;

		/**
		 * Dieses Feld speichert den Erweiterungsmodus.
		 */
		final boolean extendMode;

		/**
		 * Dieser Konstruktor initialisiert die {@link List} und den Erweiterungsmodus. Wenn ein Elemente zwischen beiden {@link List} eingefügt werden sollen,
		 * entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
		 * {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
		 * 
		 * @param items1 {@link List} der ersten Elemente.
		 * @param items2 {@link List} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
		 */
		public BaseChainedList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) throws NullPointerException {
			if (items1 == null) throw new NullPointerException("items1 = null");
			if (items2 == null) throw new NullPointerException("items2 = null");
			this.items1 = items1;
			this.items2 = items2;
			this.extendMode = extendMode;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem get(final int index) {
			final int size = this.items1.size();
			return index < size ? this.items1.get(index) : this.items2.get(index - size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem set(final int index, final GItem element) {
			final int size = this.items1.size();
			return index < size ? this.items1.set(index, element) : this.items2.set(index - size, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GItem element) {
			final int size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) {
				this.items1.add(index, element);
			} else {
				this.items2.add(index - size, element);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final int index, final Collection<? extends GItem> c) {
			final int size = this.items1.size();
			if ((index < size) || ((index == size) && this.extendMode)) return this.items1.addAll(index, c);
			return this.items2.addAll(index - size, c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> c) {
			if (!this.items1.retainAll(c)) return this.items2.retainAll(c);
			this.items2.retainAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem remove(final int index) {
			final int size = this.items1.size();
			return index < size ? this.items1.remove(index) : this.items2.remove(index - size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object o) {
			return this.items1.remove(o) || this.items2.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			if (!this.items1.removeAll(c)) return this.items2.removeAll(c);
			this.items2.removeAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.items1.size() + this.items2.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.items1.clear();
			this.items2.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.items1.isEmpty() && this.items2.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			int index = this.items1.indexOf(o);
			if (index >= 0) return index;
			index = this.items2.indexOf(o);
			return index < 0 ? -1 : index + this.items1.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			final int index = this.items2.lastIndexOf(o);
			if (index >= 0) return index + this.items1.size();
			return this.items1.lastIndexOf(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.items1.contains(o) || this.items2.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return Iterators.chainedIterator(this.items1.iterator(), this.items2.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GItem> listIterator(final int index) {
			return new ChainedIterator<GItem>(this, index);
		}

	}

	/**
	 * Diese Klasse implementiert eine abstrakte {@link List} als umkodierende Sicht auf eine gegebenen {@link List}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente dieser {@link List}.
	 * @param <GItem2> Typ der Elemente der internen {@link List}.
	 */
	public static abstract class BaseConvertedList<GItem, GItem2> extends AbstractList<GItem> {

		/**
		 * Dieses Feld speichert die {@link List} mit den internen Elementen.
		 */
		final List<GItem2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@link #data} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GItem, ? extends GItem2> parser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements von {@link #data} in ein Element dieser {@link List} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GItem2, ? extends GItem> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param data {@link List} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link List} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
		 */
		public BaseConvertedList(final List<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
			final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			if (parser == null) throw new NullPointerException("parser = null");
			if (formatter == null) throw new NullPointerException("formatter = null");
			this.data = data;
			this.parser = parser;
			this.formatter = formatter;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.data.subList(fromIndex, toIndex).clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem get(final int index) {
			final GItem2 entry2 = this.data.get(index);
			return this.formatter.accept(entry2) ? this.formatter.convert(entry2) : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem set(final int index, final GItem element) {
			final GItem2 entry2 = this.parser.accept(element) ? this.data.set(index, this.parser.convert(element)) : this.data.get(index);
			return this.formatter.accept(entry2) ? this.formatter.convert(entry2) : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem entry) {
			if (!this.parser.accept(entry)) throw new IllegalArgumentException();
			return this.data.add(this.parser.convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GItem entry) {
			if (!this.parser.accept(entry)) throw new IllegalArgumentException();
			this.data.add(index, this.parser.convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final Collection<? extends GItem> c) {
			return this.data.addAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final int index, final Collection<? extends GItem> c) {
			return this.data.addAll(index, new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GItem remove(final int index) {
			final GItem2 entry2 = this.data.remove(index);
			if (!this.formatter.accept(entry2)) throw new IllegalArgumentException();
			return this.formatter.convert(entry2);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object entry) {
			if (!this.parser.accept(entry)) return false;
			return this.data.remove(((Converter)this.parser).convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.data.removeAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.data.retainAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.data.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean contains(final Object enrty) {
			if (!this.parser.accept(enrty)) return false;
			return this.data.contains(((Converter)this.parser).convert(enrty));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return Iterators.convertedIterator(this.formatter, Iterators.filteredIterator(this.formatter, this.data.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public int indexOf(final Object entry) {
			if (!this.parser.accept(entry)) return -1;
			return this.data.indexOf(((Converter)this.parser).convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public int lastIndexOf(final Object entry) {
			if (!this.parser.accept(entry)) return -1;
			return this.data.lastIndexOf(((Converter)this.parser).convert(entry));
		}

	}

	/**
	 * Diese Schnittstelle definiert eine Kombination aus einem {@link Filter} und einem {@link Converter}, bei welcher über {@link #accept(Object)} eine gültige
	 * Eingabe für {@link #convert(Object)} erkannt werden kann.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static interface FilterConverter<GInput, GOutput> extends Filter<Object>, Converter<GInput, GOutput> {

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als rückwärts geordnete Sicht auf eine gegebene {@link List}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class ReverseList<GItem> extends BaseReverseList<GItem> {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param items {@link List}
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public ReverseList(final List<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GItem> subList(final int fromIndex, final int toIndex) {
			return new ReverseList<GItem>(this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als rückwärts geordnete Sicht auf eine gegebene {@link List} mit {@link RandomAccess}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class ReverseRandomAccessList<GItem> extends BaseReverseList<GItem> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param items {@link List}
		 * @throws NullPointerException Wenn {@code items} {@code null} ist.
		 */
		public ReverseRandomAccessList(final List<GItem> items) throws NullPointerException {
			super(items);
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GItem> subList(final int fromIndex, final int toIndex) {
			return new ReverseRandomAccessList<GItem>(this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als verkettete Sicht auf zwei gegebenen {@link List}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class ChainedList<GItem> extends BaseChainedList<GItem> {

		/**
		 * Dieser Konstruktor initialisiert die {@link List} und den Erweiterungsmodus. Wenn ein Elemente zwischen beiden {@link List} eingefügt werden sollen,
		 * entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
		 * {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
		 * 
		 * @param items1 {@link List} der ersten Elemente.
		 * @param items2 {@link List} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
		 */
		public ChainedList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) throws NullPointerException {
			super(items1, items2, extendMode);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als verkettete Sicht auf zwei gegebenen {@link List} mit {@link RandomAccess}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class ChainedRandomAccessList<GItem> extends BaseChainedList<GItem> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die {@link List} und den Erweiterungsmodus. Wenn ein Elemente zwischen beiden {@link List} eingefügt werden sollen,
		 * entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
		 * {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
		 * 
		 * @param items1 {@link List} der ersten Elemente.
		 * @param items2 {@link List} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
		 */
		public ChainedRandomAccessList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) throws NullPointerException {
			super(items1, items2, extendMode);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Collection} als verkettete Sicht auf zwei gegebenen {@link Collection}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class ChainedCollection<GItem> extends AbstractCollection<GItem> {

		/**
		 * Dieses Feld speichert die erste {@link Collection}.
		 */
		final Collection<GItem> items1;

		/**
		 * Dieses Feld speichert die zweite {@link Collection}.
		 */
		final Collection<GItem> items2;

		/**
		 * Dieses Feld speichert den Erweiterungsmodus.
		 */
		final boolean extendMode;

		/**
		 * Dieser Konstruktor initialisiert die {@link Collection} und den Erweiterungsmodus. Wenn Elemente eingefügt werden sollen, entscheidet der
		 * Erweiterungsmodus, in welche {@link Collection} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste {@link Collection}
		 * erweitert, bei {@code false} wird die zweite {@link Collection} erweitert.
		 * 
		 * @param items1 {@link Collection} der ersten Elemente.
		 * @param items2 {@link Collection} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
		 */
		public ChainedCollection(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode) throws NullPointerException {
			if (items1 == null) throw new NullPointerException("items1 = null");
			if (items2 == null) throw new NullPointerException("items2 = null");
			this.items1 = items1;
			this.items2 = items2;
			this.extendMode = extendMode;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem e) {
			return (this.extendMode ? this.items1 : this.items2).add(e);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GItem> c) {
			return (this.extendMode ? this.items1 : this.items2).addAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> c) {
			if (!this.items1.retainAll(c)) return this.items2.retainAll(c);
			this.items2.retainAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object o) {
			return this.items1.remove(o) || this.items2.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			if (!this.items1.removeAll(c)) return this.items2.removeAll(c);
			this.items2.removeAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.items1.size() + this.items2.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.items1.clear();
			this.items2.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.items1.contains(o) || this.items2.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return Iterators.chainedIterator(this.items1.iterator(), this.items2.iterator());
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Map} als umkodierende Sicht auf eine gegebene {@link Map}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GKey> Typ der Schlüssel dieser {@link Map}.
	 * @param <GValue> Typ der Werte dieser {@link Map}.
	 * @param <GKey2> Typ der Schlüssel der internen {@link Map}.
	 * @param <GValue2> Typ der Werte der internen {@link Map}.
	 */
	public static final class ConvertedMap<GKey, GValue, GKey2, GValue2> extends AbstractMap<GKey, GValue> {

		/**
		 * Diese Methode erzeugt einen {@link FilterConverter} für das {@link Map#entrySet()} einer {@link ConvertedMap} und gibt diesen zurück.
		 * 
		 * @param <GKey> Typ der Schlüssel der Eingabe.
		 * @param <GItem> Typ der Werte der Eingabe.
		 * @param <GKey2> Typ der Schlüssel der Ausgabe.
		 * @param <GItem2> Typ der Werte der Ausgabe.
		 * @param keyParser {@link FilterConverter} zum Lesen der Schlüssel.
		 * @param valueParser {@link FilterConverter} zum Lesen der Werte.
		 * @param valueFormatter {@link FilterConverter} zum schreiben der Werte.
		 * @return {@link FilterConverter} für ein {@link Map#entrySet()}.
		 */
		private static final <GKey, GItem, GKey2, GItem2> FilterConverter<Entry<GKey, GItem>, Entry<GKey2, GItem2>> newEntryConverter(
			final FilterConverter<? super GKey, ? extends GKey2> keyParser, final FilterConverter<? super GItem, ? extends GItem2> valueParser,
			final FilterConverter<? super GItem2, ? extends GItem> valueFormatter) {
			return new FilterConverter<Entry<GKey, GItem>, Entry<GKey2, GItem2>>() {

				@Override
				public boolean accept(final Object input) {
					if (!(input instanceof Entry)) return false;
					final Entry<?, ?> entry = (Entry<?, ?>)input;
					return keyParser.accept(entry.getKey()) && valueParser.accept(entry.getValue());
				}

				@Override
				public Entry<GKey2, GItem2> convert(final Entry<GKey, GItem> input) {
					return new Entry<GKey2, GItem2>() {

						@Override
						public GKey2 getKey() {
							final GKey key = input.getKey();
							if (!keyParser.accept(key)) throw new IllegalArgumentException();
							return keyParser.convert(key);
						}

						@Override
						public GItem2 getValue() {
							final GItem value = input.getValue();
							if (!valueParser.accept(value)) throw new IllegalArgumentException();
							return valueParser.convert(value);
						}

						@Override
						public GItem2 setValue(final GItem2 value2) {
							if (!valueFormatter.accept(value2)) throw new IllegalArgumentException();
							final GItem value = input.setValue(valueFormatter.convert(value2));
							if (!valueParser.accept(value)) throw new IllegalArgumentException();
							return valueParser.convert(value);
						}

						@Override
						public int hashCode() {
							return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
						}

						@Override
						public boolean equals(final Object object) {
							if (!(object instanceof Entry<?, ?>)) return false;
							final Entry<?, ?> data = (Entry<?, ?>)object;
							return Objects.equals(this.getKey(), data.getKey()) && Objects.equals(this.getValue(), data.getValue());
						}

						@Override
						public String toString() {
							return this.getKey() + "=" + this.getValue();
						}

					};
				}

			};
		}

		{}

		/**
		 * Dieses Feld speichert die {@link Map} mit den internen Einträgen.
		 */
		final Map<GKey2, GValue2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Schlüssels dieser {@link Map} in einen Schlüssel von {@link #data} sowie zur
		 * Erkennung einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GKey, ? extends GKey2> keyParser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Schlüssels von {@link #data} in einen Schlüssel dieser {@link Map} sowie zur
		 * Erkennung einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GKey2, ? extends GKey> keyFormatter;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Werts dieser {@link Map} in einen Wert von {@link #data} sowie zur Erkennung einer
		 * gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GValue, ? extends GValue2> valueParser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Werts von {@link #data} in einen Wert dieser {@link Map} sowie zur Erkennung einer
		 * gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GValue2, ? extends GValue> valueFormatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Map}.
		 * 
		 * @param data {@link Map} mit den internen Einträgen.
		 * @param keyParser {@link FilterConverter} zur Umwandlung eines Schlüssels dieser {@link Map} in einen Schlüssel von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param keyFormatter {@link FilterConverter} zur Umwandlung eines Schlüssels von {@code data} in einen Schlüssel dieser {@link Map} sowie zur Erkennung
		 *        einer gültigen Eingabe für die Umwandlung.
		 * @param valueParser {@link FilterConverter} zur Umwandlung eines Werts dieser {@link Map} in einen Wert von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param valueFormatter {@link FilterConverter} zur Umwandlung eines Werts von {@code data} in einen Wert dieser {@link Map} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn {@code data}, {@code keyParser}, {@code keyFormatter}, {@code valueParser} bzw. {@code valueFormatter} {@code null}
		 *         ist.
		 */
		public ConvertedMap(final Map<GKey2, GValue2> data, final FilterConverter<? super GKey, ? extends GKey2> keyParser,
			final FilterConverter<? super GKey2, ? extends GKey> keyFormatter, final FilterConverter<? super GValue, ? extends GValue2> valueParser,
			final FilterConverter<? super GValue2, ? extends GValue> valueFormatter) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			if (keyParser == null) throw new NullPointerException("keyParser = null");
			if (keyFormatter == null) throw new NullPointerException("keyFormatter = null");
			if (valueParser == null) throw new NullPointerException("valueParser = null");
			if (valueFormatter == null) throw new NullPointerException("valueFormatter = null");
			this.data = data;
			this.keyParser = keyParser;
			this.keyFormatter = keyFormatter;
			this.valueParser = valueParser;
			this.valueFormatter = valueFormatter;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public boolean containsKey(final Object key) {
			if (!this.keyParser.accept(key)) return false;
			return this.data.containsKey(((Converter)this.keyParser).convert(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public boolean containsValue(final Object value) {
			if (!this.valueParser.accept(value)) return false;
			return this.data.containsValue(((Converter)this.valueParser).convert(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public GValue get(final Object key) {
			if (!this.keyParser.accept(key)) return null;
			final GValue2 value2 = this.data.get(((Converter)this.keyParser).convert(key));
			if (!this.valueFormatter.accept(value2)) throw new IllegalArgumentException();
			return this.valueFormatter.convert(value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.data.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<GKey> keySet() {
			return new ConvertedSet<GKey, GKey2>(this.data.keySet(), this.keyParser, this.keyFormatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
			if (!this.keyParser.accept(key) || !this.valueParser.accept(value)) throw new IllegalArgumentException();
			final GValue2 value2 = this.data.put(this.keyParser.convert(key), this.valueParser.convert(value));
			if (!this.valueFormatter.accept(value2)) throw new IllegalArgumentException();
			return this.valueFormatter.convert(value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public void putAll(final Map<? extends GKey, ? extends GValue> map) {
			this.data.putAll(new ConvertedMap(map, this.keyFormatter, this.keyParser, this.valueFormatter, this.valueParser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public GValue remove(final Object key) {
			if (!this.keyParser.accept(key)) return null;
			final GValue2 value2 = this.data.remove(((Converter)this.keyParser).convert(key));
			if (!this.valueFormatter.accept(value2)) throw new IllegalArgumentException();
			return this.valueFormatter.convert(value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GValue> values() {
			return new ConvertedCollection<GValue, GValue2>(this.data.values(), this.valueParser, this.valueFormatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return new ConvertedSet<Entry<GKey, GValue>, Entry<GKey2, GValue2>>(this.data.entrySet(), ConvertedMap.newEntryConverter(this.keyParser,
				this.valueParser, this.valueFormatter), ConvertedMap.newEntryConverter(this.keyFormatter, this.valueFormatter, this.valueParser));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Set} als umkodierende Sicht auf ein gegebenes {@link Set}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente dieses {@link Set}.
	 * @param <GItem2> Typ der Elemente des internen {@link Set}.
	 */
	public static final class ConvertedSet<GItem, GItem2> extends AbstractSet<GItem> {

		/**
		 * Dieses Feld speichert die {@link Set} mit den internen Elementen.
		 */
		final Set<GItem2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements dieser {@link Set} in ein Element von {@link #data} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GItem, ? extends GItem2> parser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements von {@link #data} in ein Element dieser {@link Set} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GItem2, ? extends GItem> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Set}.
		 * 
		 * @param data {@link Set} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieses {@link Set} in ein Element von {@code data} sowie zur Erkennung einer gültigen
		 *        Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieses {@link Set} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
		 */
		public ConvertedSet(final Set<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
			final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			if (parser == null) throw new NullPointerException("parser = null");
			if (formatter == null) throw new NullPointerException("formatter = null");
			this.data = data;
			this.parser = parser;
			this.formatter = formatter;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem e) {
			if (!this.parser.accept(e)) throw new IllegalArgumentException();
			return this.data.add(this.parser.convert(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final Collection<? extends GItem> c) {
			return this.data.addAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if (!this.parser.accept(o)) return false;
			return this.data.remove(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.data.removeAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.data.retainAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.data.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean contains(final Object o) {
			if (!this.parser.accept(o)) return false;
			return this.data.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return Iterators.convertedIterator(this.formatter, Iterators.filteredIterator(this.formatter, this.data.iterator()));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List}. Die Methoden {@link ListIterator#next()} und
	 * {@link ListIterator#previous()} können eine {@link IllegalArgumentException} auslösen.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente dieser {@link List}.
	 * @param <GItem2> Typ der Elemente der internen {@link List}.
	 */
	public static final class ConvertedList<GItem, GItem2> extends BaseConvertedList<GItem, GItem2> {

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param data {@link List} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link List} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
		 */
		public ConvertedList(final List<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
			final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
			super(data, parser, formatter);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List} mit {@link RandomAccess}. Die Methoden
	 * {@link ListIterator#next()} und {@link ListIterator#previous()} können eine {@link IllegalArgumentException} auslösen.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente dieser {@link List}.
	 * @param <GItem2> Typ der Elemente der internen {@link List}.
	 */
	public static final class ConvertedRandomAccessList<GItem, GItem2> extends BaseConvertedList<GItem, GItem2> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param data {@link List} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link List} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
		 */
		public ConvertedRandomAccessList(final List<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
			final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
			super(data, parser, formatter);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Collection} als umkodierende Sicht auf eine gegebene {@link Collection}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente dieser {@link Collection}.
	 * @param <GItem2> Typ der Elemente der internen {@link Collection}.
	 */
	public static final class ConvertedCollection<GItem, GItem2> extends AbstractCollection<GItem> {

		/**
		 * Dieses Feld speichert die {@link Collection} mit den internen Elementen.
		 */
		final Collection<GItem2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements dieser {@link Collection} in ein Element von {@link #data} sowie zur
		 * Erkennung einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GItem, ? extends GItem2> parser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements von {@link #data} in ein Element dieser {@link Collection} sowie zur
		 * Erkennung einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GItem2, ? extends GItem> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Collection}.
		 * 
		 * @param data {@link Collection} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link Collection} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link Collection} sowie zur Erkennung
		 *        einer gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
		 */
		public ConvertedCollection(final Collection<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
			final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
			if (data == null) throw new NullPointerException("data = null");
			if (parser == null) throw new NullPointerException("parser = null");
			if (formatter == null) throw new NullPointerException("formatter = null");
			this.data = data;
			this.parser = parser;
			this.formatter = formatter;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GItem e) {
			if (!this.parser.accept(e)) throw new IllegalArgumentException();
			return this.data.add(this.parser.convert(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final Collection<? extends GItem> c) {
			return this.data.addAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if (!this.parser.accept(o)) return false;
			return this.data.remove(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.data.removeAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.data.retainAll(new ConvertedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.data.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.data.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.data.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean contains(final Object o) {
			if (!this.parser.accept(o)) return false;
			return this.data.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			return Iterators.convertedIterator(this.formatter, Iterators.filteredIterator(this.formatter, this.data.iterator()));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Vereinigungsmenge zweier {@link Set}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class UnionSet<GItem> extends AbstractSet<GItem> {

		/**
		 * Dieses Feld speichert das erste {@link Set}.
		 */
		final Set<? extends GItem> items1;

		/**
		 * Dieses Feld speichert das zweite {@link Set}.
		 */
		final Set<? extends GItem> items2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Set} der Vereinigungsmenge.
		 * 
		 * @param items1 erstes {@link Set}.
		 * @param items2 zweites {@link Set}.
		 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
		 */
		public UnionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
			if (items1 == null) throw new NullPointerException("items1 = null");
			if (items2 == null) throw new NullPointerException("items2 = null");
			this.items1 = items1;
			this.items2 = items2;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return Iterables.size(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			if (this.items1.size() < this.items2.size()) return Iterators.unmodifiableIterator(Iterators.chainedIterator(Iterators.filteredIterator( //
				Filters.negationFilter(Filters.containsFilter(this.items2)), this.items1.iterator()), this.items2.iterator()));
			return Iterators.unmodifiableIterator(Iterators.chainedIterator(Iterators.filteredIterator( //
				Filters.negationFilter(Filters.containsFilter(this.items1)), this.items2.iterator()), this.items1.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.items1.contains(o) || this.items2.contains(o);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Set} als unveränderliche Sicht auf die Schnittmenge zweier {@link Set}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static final class IntersectionSet<GItem> extends AbstractSet<GItem> {

		/**
		 * Dieses Feld speichert das erste {@link Set}.
		 */
		final Set<? extends GItem> items1;

		/**
		 * Dieses Feld speichert das zweite {@link Set}.
		 */
		final Set<? extends GItem> items2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Set} der Schnittmenge.
		 * 
		 * @param items1 erstes {@link Set}.
		 * @param items2 zweites {@link Set}.
		 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
		 */
		public IntersectionSet(final Set<? extends GItem> items1, final Set<? extends GItem> items2) throws NullPointerException {
			if (items1 == null) throw new NullPointerException("items1 = null");
			if (items2 == null) throw new NullPointerException("items2 = null");
			this.items1 = items1;
			this.items2 = items2;
		}

		{}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return Iterables.size(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GItem> iterator() {
			if (this.items1.size() < this.items2.size()) return Iterators.unmodifiableIterator(Iterators.filteredIterator( //
				Filters.containsFilter(this.items2), this.items1.iterator()));
			return Iterators.unmodifiableIterator(Iterators.filteredIterator( //
				Filters.containsFilter(this.items1), this.items2.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.items1.contains(o) && this.items2.contains(o);
		}

	}

	{}

	/**
	 * Diese Methode gibt eine rückwärts geordnete Sicht auf die gegebene {@link List} zurück.
	 * 
	 * @param <GItem> Typ der Werte.
	 * @param items {@link List}
	 * @return rückwärts geordnete {@link List}-Sicht.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist.
	 */
	public static <GItem> List<GItem> reverseList(final List<GItem> items) throws NullPointerException {
		if (items instanceof RandomAccess) return new ReverseRandomAccessList<>(items);
		return new ReverseList<>(items);
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als verkettete Sicht auf die gegebenen {@link List} und gibt diese zurück. Wenn ein Elemente zwischen beiden
	 * {@link List} eingefügt werden sollen, wird die erste {@link List} erweitert. Der Rückgabewert entspricht
	 * {@code Collections.chainedList(items1, items2, true)}.
	 * 
	 * @see #chainedCollection(Collection, Collection, boolean)
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link List} der ersten Elemente.
	 * @param items2 {@link List} der letzten Elemente.
	 * @return verkettete {@link List}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
	 */
	public static <GItem> List<GItem> chainedList(final List<GItem> items1, final List<GItem> items2) throws NullPointerException {
		return Collections.chainedList(items1, items2, true);
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als verkettete Sicht auf die gegebenen {@link List} und gibt diese zurück. Wenn ein Elemente zwischen beiden
	 * {@link List} eingefügt werden sollen, entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus
	 * {@code true}, wird die erste {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link List} der ersten Elemente.
	 * @param items2 {@link List} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link List}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
	 */
	public static <GItem> List<GItem> chainedList(final List<GItem> items1, final List<GItem> items2, final boolean extendMode) throws NullPointerException {
		if ((items1 instanceof RandomAccess) && (items2 instanceof RandomAccess)) return new ChainedRandomAccessList<>(items1, items2, extendMode);
		return new ChainedList<>(items1, items2, extendMode);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection} und gibt diese zurück. Wenn Elemente eingefügt
	 * werden sollen, wird die erste {@link Collection} erweitert. Der Rückgabewert entspricht {@code Collections.chainedCollection(items1, items2, true)}.
	 * 
	 * @see #chainedCollection(Collection, Collection, boolean)
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link Collection} der ersten Elemente.
	 * @param items2 {@link Collection} der letzten Elemente.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
	 */
	public static <GItem> Collection<GItem> chainedCollection(final Collection<GItem> items1, final Collection<GItem> items2) throws NullPointerException {
		return Collections.chainedCollection(items1, items2, true);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection} und gibt diese zurück. Wenn Elemente eingefügt
	 * werden sollen, entscheidet der Erweiterungsmodus, in welche {@link Collection} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird
	 * die erste {@link Collection} erweitert, bei {@code false} wird die zweite {@link Collection} erweitert.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param items1 {@link Collection} der ersten Elemente.
	 * @param items2 {@link Collection} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn {@code items1} bzw. {@code items2} {@code null} ist.
	 */
	public static <GItem> Collection<GItem> chainedCollection(final Collection<GItem> items1, final Collection<GItem> items2, final boolean extendMode)
		throws NullPointerException {
		return new ChainedCollection<>(items1, items2, extendMode);
	}

	/**
	 * Diese Methode erzeugt eine {@link Map} als umkodierende Sicht auf die gegebene {@link Map} und gibt diese zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel der erzeugten {@link Map}.
	 * @param <GValue> Typ der Werte der erzeugten {@link Map}.
	 * @param <GKey2> Typ der Schlüssel der gegebenen {@link Map}.
	 * @param <GValue2> Typ der Werte der gegebenen {@link Map}.
	 * @param data {@link Map} mit den internen Einträgen.
	 * @param keyParser {@link FilterConverter} zur Umwandlung eines Schlüssels der erzeugten {@link Map} in einen Schlüssel der gegebenen {@link Map} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param keyFormatter {@link FilterConverter} zur Umwandlung eines Schlüssels der gegebenen {@link Map} in einen Schlüssel der erzeugten {@link Map} sowie
	 *        zur Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param valueParser {@link FilterConverter} zur Umwandlung eines Werts der erzeugten {@link Map} in einen Wert der gegebenen {@link Map} sowie zur Erkennung
	 *        einer gültigen Eingabe für die Umwandlung.
	 * @param valueFormatter {@link FilterConverter} zur Umwandlung eines Werts der gegebenen {@link Map} in einen Wert der erzeugten {@link Map} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link Map}-Sicht.
	 * @throws NullPointerException Wenn {@code data}, {@code keyParser}, {@code keyFormatter}, {@code valueParser} bzw. {@code valueFormatter} {@code null} ist.
	 */
	public static <GKey, GValue, GKey2, GValue2> Map<GKey, GValue> convertedMap(final Map<GKey2, GValue2> data,
		final FilterConverter<? super GKey, ? extends GKey2> keyParser, final FilterConverter<? super GKey2, ? extends GKey> keyFormatter,
		final FilterConverter<? super GValue, ? extends GValue2> valueParser, final FilterConverter<? super GValue2, ? extends GValue> valueFormatter)
		throws NullPointerException {
		return new ConvertedMap<>(data, keyParser, keyFormatter, valueParser, valueFormatter);
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als umkodierende Sicht auf die gegebene {@link List} und gibt diese zurück. Die Methoden
	 * {@link ListIterator#next()} und {@link ListIterator#previous()} können eine {@link IllegalArgumentException} auslösen.
	 * 
	 * @param <GItem> Typ der Elemente der erzeugten {@link List}.
	 * @param <GItem2> Typ der Elemente der gegebenen {@link List}.
	 * @param data gegebene {@link List}.
	 * @param parser {@link FilterConverter} zur Umwandlung eines Elements der erzeugeten {@link List} in ein Element der gegebenen {@link List} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements der gegebenen {@link List} in ein Element der erzeugten {@link List} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link List}-Sicht.
	 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
	 */
	public static <GItem, GItem2> List<GItem> convertedList(final List<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
		final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
		if (data instanceof RandomAccess) return new ConvertedRandomAccessList<>(data, parser, formatter);
		return new ConvertedList<>(data, parser, formatter);
	}

	/**
	 * Diese Methode erzeugt ein {@link Set} als umkodierende Sicht auf das gegebene {@link Set} und gibt dieses zurück.
	 * 
	 * @param <GItem> Typ der Elemente des erzeugten {@link Set}.
	 * @param <GItem2> Typ der Elemente des gegebenen {@link Set}.
	 * @param data gegebenes {@link Set}.
	 * @param parser {@link FilterConverter} zur Umwandlung eines Elements des erzeugeten {@link Set} in ein Element des gegebenen {@link Set} sowie zur Erkennung
	 *        einer gültigen Eingabe für die Umwandlung.
	 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements des gegebenen {@link Set} in ein Element des erzeugten {@link Set} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link Set}-Sicht.
	 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
	 */
	public static <GItem, GItem2> Set<GItem> convertedSet(final Set<GItem2> data, final FilterConverter<? super GItem, ? extends GItem2> parser,
		final FilterConverter<? super GItem2, ? extends GItem> formatter) throws NullPointerException {
		return new ConvertedSet<>(data, parser, formatter);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als umkodierende Sicht auf die gegebene {@link Collection} und gibt diese zurück.
	 * 
	 * @param <GItem> Typ der Elemente der erzeugten {@link Collection}.
	 * @param <GItem2> Typ der Elemente der gegebenen {@link Collection}.
	 * @param data gegebene {@link Collection}.
	 * @param parser {@link FilterConverter} zur Umwandlung eines Elements der erzeugeten {@link Collection} in ein Element der gegebenen {@link Collection} sowie
	 *        zur Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements der gegebenen {@link Collection} in ein Element der erzeugten {@link Collection}
	 *        sowie zur Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn {@code data}, {@code parser} bzw. {@code formatter} {@code null} ist.
	 */
	public static <GItem, GItem2> Collection<GItem> convertedCollection(final Collection<GItem2> data,
		final FilterConverter<? super GItem, ? extends GItem2> parser, final FilterConverter<? super GItem2, ? extends GItem> formatter)
		throws NullPointerException {
		return new ConvertedCollection<>(data, parser, formatter);
	}

}
