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
 * Diese Klasse implementiert umordnende, zusammenführende bzw. umwandelnde Sichten für {@link Set}s, {@link Map}s, {@link List}s und {@link Collection}s.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Collections {

	/**
	 * Diese Klasse implementiert eine {@link List} als rückwärts geordnete Sicht auf eine gegebene {@link List}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	static abstract class AbstractReverseList<GValue> extends AbstractList<GValue> {

		/**
		 * Diese Klasse implementiert den {@link ListIterator} einer {@link AbstractReverseList}.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ der Elemente.
		 */
		static final class ReverseIterator<GValue> implements ListIterator<GValue> {

			/**
			 * Dieses Feld speichert die {@link AbstractReverseList}.
			 */
			final AbstractReverseList<GValue> list;

			/**
			 * Dieses Feld speichert die Größe der {@link AbstractReverseList}.
			 */
			int size;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link AbstractReverseList#list}.
			 */
			final ListIterator<GValue> iterator;

			/**
			 * Dieser Konstruktor initialisiert {@link AbstractReverseList} und Index.
			 * 
			 * @param list {@link AbstractReverseList}.
			 * @param index Index.
			 */
			public ReverseIterator(final AbstractReverseList<GValue> list, final int index) {
				this.size = list.size();
				this.list = list;
				this.iterator = list.listIterator(this.size - index);
			}

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
			public void set(final GValue e) {
				this.iterator.set(e);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void add(final GValue e) {
				this.iterator.add(e);
				this.iterator.hasPrevious();
				this.iterator.previous();
				this.size = this.list.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GValue next() {
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
			public GValue previous() {
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

		/**
		 * Dieses Feld speichert die {@link List}.
		 */
		final List<GValue> list;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public AbstractReverseList(final List<GValue> list) throws NullPointerException {
			if(list == null) throw new NullPointerException();
			this.list = list;
		}

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
		public GValue get(final int index) {
			return this.list.get(this.list.size() - index - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue set(final int index, final GValue element) {
			return this.list.set(this.list.size() - index - 1, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GValue element) {
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
		public GValue remove(final int index) {
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
		public Iterator<GValue> iterator() {
			return this.listIterator(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GValue> listIterator(final int index) {
			return new ReverseIterator<GValue>(this, index);

		}
	}

	/**
	 * Diese Klasse implementiert eine {@link List} als verkettete Sicht auf zwei gegebenen {@link List}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	static abstract class AbstractChainedList<GValue> extends AbstractList<GValue> {

		/**
		 * Diese Klasse implementiert den {@link ListIterator} zu {@link AbstractChainedList}.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ der Elemente.
		 */
		static final class ChainedIterator<GValue> implements ListIterator<GValue> {

			/**
			 * Dieses Feld speichert die {@link AbstractChainedList}.
			 */
			final AbstractChainedList<GValue> list;

			/**
			 * Dieses Feld speichert die Größe von {@link AbstractChainedList#list1}.
			 */
			int size;

			/**
			 * Dieses Feld speichert gewählten {@link ListIterator}.
			 */
			ListIterator<GValue> iterator;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link AbstractChainedList#list1}.
			 */
			final ListIterator<GValue> iterator1;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link AbstractChainedList#list2}.
			 */
			final ListIterator<GValue> iterator2;

			/**
			 * Dieser Konstruktor initialisiert {@link AbstractChainedList} und Index.
			 * 
			 * @param list {@link AbstractChainedList}.
			 * @param index Index.
			 */
			public ChainedIterator(final AbstractChainedList<GValue> list, final int index) {
				this.size = list.list1.size();
				this.list = list;
				if(index < this.size){
					this.iterator1 = this.iterator = list.list1.listIterator(index);
					this.iterator2 = list.list2.listIterator(0);
				}else{
					this.iterator1 = list.list1.listIterator(this.size);
					this.iterator2 = this.iterator = list.list2.listIterator(index - this.size);
				}
			}

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
			public void set(final GValue e) {
				this.iterator.set(e);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void add(final GValue e) {
				if((this.iterator2.nextIndex() != 0) || (this.iterator1.nextIndex() != this.size)){
					this.iterator.add(e);
				}else if(this.list.extendMode){
					this.iterator1.add(e);
				}else{
					this.iterator2.add(e);
				}
				this.size = this.list.list1.size();
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public GValue next() {
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
			public GValue previous() {
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
				if(this.iterator == this.iterator2) return;
				this.size = this.list.list1.size();
			}

		}

		/**
		 * Dieses Feld speichert die erste {@link List}.
		 */
		final List<GValue> list1;

		/**
		 * Dieses Feld speichert die zweite {@link List}.
		 */
		final List<GValue> list2;

		/**
		 * Dieses Feld speichert den Erweiterungsmodus.
		 */
		boolean extendMode;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}s und den Erweiterungsmodus. Wenn ein Elemente zwischen beiden {@link List}s eingefügt werden sollen,
		 * entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
		 * {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
		 * 
		 * @param values1 {@link List} der ersten Elemente.
		 * @param values2 {@link List} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public AbstractChainedList(final List<GValue> values1, final List<GValue> values2, final boolean extendMode) throws NullPointerException {
			if((values1 == null) || (values2 == null)) throw new NullPointerException();
			this.extendMode = extendMode;
			this.list1 = values1;
			this.list2 = values2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final int index) {
			final int size = this.list1.size();
			return index < size ? this.list1.get(index) : this.list2.get(index - size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue set(final int index, final GValue element) {
			final int size = this.list1.size();
			return index < size ? this.list1.set(index, element) : this.list2.set(index - size, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GValue element) {
			final int size = this.list1.size();
			if((index < size) || ((index == size) && this.extendMode)){
				this.list1.add(index, element);
			}else{
				this.list2.add(index - size, element);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final int index, final Collection<? extends GValue> c) {
			final int size = this.list1.size();
			if((index < size) || ((index == size) && this.extendMode)) return this.list1.addAll(index, c);
			return this.list2.addAll(index - size, c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> c) {
			if(!this.list1.retainAll(c)) return this.list2.retainAll(c);
			this.list2.retainAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue remove(final int index) {
			final int size = this.list1.size();
			return index < size ? this.list1.remove(index) : this.list2.remove(index - size);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object o) {
			return this.list1.remove(o) || this.list2.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			if(!this.list1.removeAll(c)) return this.list2.removeAll(c);
			this.list2.removeAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.list1.size() + this.list2.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.list1.clear();
			this.list2.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.list1.isEmpty() && this.list2.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			int index = this.list1.indexOf(o);
			if(index >= 0) return index;
			index = this.list2.indexOf(o);
			return index < 0 ? -1 : index + this.list1.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			final int index = this.list2.lastIndexOf(o);
			if(index >= 0) return index + this.list1.size();
			return this.list1.lastIndexOf(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.list1.contains(o) || this.list2.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GValue> iterator() {
			return Iterators.chainedIterator(this.list1.iterator(), this.list2.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GValue> listIterator(final int index) {
			return new ChainedIterator<GValue>(this, index);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieser {@link List}.
	 * @param <GEntry2> Typ der Elemente der internen {@link List}.
	 */
	static abstract class AbstractTranscodedList<GEntry, GEntry2> extends AbstractList<GEntry> {

		/**
		 * Dieses Feld speichert die {@link List} mit den internen Elementen.
		 */
		final List<GEntry2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@link #data} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GEntry, ? extends GEntry2> parser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements von {@link #data} in ein Element dieser {@link List} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GEntry2, ? extends GEntry> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param data {@link List} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link List} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public AbstractTranscodedList(final List<GEntry2> data, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
			final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			if((data == null) || (parser == null) || (formatter == null)) throw new NullPointerException();
			this.data = data;
			this.parser = parser;
			this.formatter = formatter;
		}

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
		public GEntry get(final int index) {
			final GEntry2 entry2 = this.data.get(index);
			return this.formatter.accept(entry2) ? this.formatter.convert(entry2) : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry set(final int index, final GEntry element) {
			final GEntry2 entry2 = this.parser.accept(element) ? this.data.set(index, this.parser.convert(element)) : this.data.get(index);
			return this.formatter.accept(entry2) ? this.formatter.convert(entry2) : null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GEntry entry) {
			if(!this.parser.accept(entry)) throw new IllegalArgumentException();
			return this.data.add(this.parser.convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GEntry entry) {
			if(!this.parser.accept(entry)) throw new IllegalArgumentException();
			this.data.add(index, this.parser.convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final Collection<? extends GEntry> c) {
			return this.data.addAll(new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final int index, final Collection<? extends GEntry> c) {
			return this.data.addAll(index, new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry remove(final int index) {
			final GEntry2 entry2 = this.data.remove(index);
			if(!this.formatter.accept(entry2)) throw new IllegalArgumentException();
			return this.formatter.convert(entry2);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object entry) {
			if(!this.parser.accept(entry)) return false;
			return this.data.remove(((Converter)this.parser).convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.data.removeAll(new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.data.retainAll(new TranscodedCollection(c, this.formatter, this.parser));
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
			if(!this.parser.accept(enrty)) return false;
			return this.data.contains(((Converter)this.parser).convert(enrty));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.convertedIterator(this.formatter, Iterators.filteredIterator(this.formatter, this.data.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public int indexOf(final Object entry) {
			if(!this.parser.accept(entry)) return -1;
			return this.data.indexOf(((Converter)this.parser).convert(entry));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public int lastIndexOf(final Object entry) {
			if(!this.parser.accept(entry)) return -1;
			return this.data.lastIndexOf(((Converter)this.parser).convert(entry));
		}

	}

	/**
	 * Diese Schnittstelle definiert eine Kombination aus einem {@link Filter} und einem {@link Converter}, bei welcher über {@link #accept(Object)} eine gültige
	 * Eingabe Eingabe für {@link #convert(Object)} erkannt werden kann.
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
	 * @param <GValue> Typ der Elemente.
	 */
	public static final class ReverseList<GValue> extends AbstractReverseList<GValue> {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ReverseList(final List<GValue> list) throws NullPointerException {
			super(list);

		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GValue> subList(final int fromIndex, final int toIndex) {
			return new ReverseList<GValue>(this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als rückwärts geordnete Sicht auf eine gegebene {@link List} mit {@link RandomAccess}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	public static final class ReverseRandomAccessList<GValue> extends AbstractReverseList<GValue> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ReverseRandomAccessList(final List<GValue> list) throws NullPointerException {
			super(list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GValue> subList(final int fromIndex, final int toIndex) {
			return new ReverseRandomAccessList<GValue>(this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als verkettete Sicht auf zwei gegebenen {@link List}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	public static final class ChainedList<GValue> extends AbstractChainedList<GValue> {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}s und den Erweiterungsmodus. Wenn ein Elemente zwischen beiden {@link List}s eingefügt werden sollen,
		 * entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
		 * {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
		 * 
		 * @param list1 {@link List} der ersten Elemente.
		 * @param list2 {@link List} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ChainedList(final List<GValue> list1, final List<GValue> list2, final boolean extendMode) throws NullPointerException {
			super(list1, list2, extendMode);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als verkettete Sicht auf zwei gegebenen {@link List}s mit {@link RandomAccess}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	public static final class ChainedRandomAccessList<GValue> extends AbstractChainedList<GValue> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}s und den Erweiterungsmodus. Wenn ein Elemente zwischen beiden {@link List}s eingefügt werden sollen,
		 * entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste
		 * {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
		 * 
		 * @param list1 {@link List} der ersten Elemente.
		 * @param list2 {@link List} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ChainedRandomAccessList(final List<GValue> list1, final List<GValue> list2, final boolean extendMode) throws NullPointerException {
			super(list1, list2, extendMode);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Collection} als verkettete Sicht auf zwei gegebenen {@link Collection}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	public static final class ChainedCollection<GValue> extends AbstractCollection<GValue> {

		/**
		 * Dieses Feld speichert die erste {@link Collection}.
		 */
		final Collection<GValue> values1;

		/**
		 * Dieses Feld speichert die zweite {@link Collection}.
		 */
		final Collection<GValue> values2;

		/**
		 * Dieses Feld speichert den Erweiterungsmodus.
		 */
		boolean extendMode;

		/**
		 * Dieser Konstruktor initialisiert die {@link Collection}s und den Erweiterungsmodus. Wenn Elemente eingefügt werden sollen, entscheidet der
		 * Erweiterungsmodus, in welche {@link Collection} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird die erste {@link Collection}
		 * erweitert, bei {@code false} wird die zweite {@link Collection} erweitert.
		 * 
		 * @param values1 {@link Collection} der ersten Elemente.
		 * @param values2 {@link Collection} der letzten Elemente.
		 * @param extendMode Erweiterungsmodus.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ChainedCollection(final Collection<GValue> values1, final Collection<GValue> values2, final boolean extendMode) throws NullPointerException {
			if((values1 == null) || (values2 == null)) throw new NullPointerException();
			this.extendMode = extendMode;
			this.values1 = values1;
			this.values2 = values2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GValue e) {
			return (this.extendMode ? this.values1 : this.values2).add(e);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final Collection<? extends GValue> c) {
			return (this.extendMode ? this.values1 : this.values2).addAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> c) {
			if(!this.values1.retainAll(c)) return this.values2.retainAll(c);
			this.values2.retainAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(final Object o) {
			return this.values1.remove(o) || this.values2.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			if(!this.values1.removeAll(c)) return this.values2.removeAll(c);
			this.values2.removeAll(c);
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.values1.size() + this.values2.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.values1.clear();
			this.values2.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.values1.contains(o) || this.values2.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GValue> iterator() {
			return Iterators.chainedIterator(this.values1.iterator(), this.values2.iterator());
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
	public static final class TranscodedMap<GKey, GValue, GKey2, GValue2> extends AbstractMap<GKey, GValue> {

		/**
		 * Diese Methode erzeugt einen {@link FilterConverter} für das {@link Map#entrySet()} einer {@link TranscodedMap} und gibt diesen zurück.
		 * 
		 * @param <GKey> Typ der Schlüssel der Eingabe.
		 * @param <GValue> Typ der Werte der Eingabe.
		 * @param <GKey2> Typ der Schlüssel der Ausgabe.
		 * @param <GValue2> Typ der Werte der Ausgabe.
		 * @param keyParser {@link FilterConverter} zum Lesen der Schlüssel.
		 * @param valueParser {@link FilterConverter} zum Lesen der Werte.
		 * @param valueFormatter {@link FilterConverter} zum schreiben der Werte.
		 * @return {@link FilterConverter} für ein {@link Map#entrySet()}.
		 */
		private static final <GKey, GValue, GKey2, GValue2> FilterConverter<Entry<GKey, GValue>, Entry<GKey2, GValue2>> newEntryConverter(
			final FilterConverter<? super GKey, ? extends GKey2> keyParser, final FilterConverter<? super GValue, ? extends GValue2> valueParser,
			final FilterConverter<? super GValue2, ? extends GValue> valueFormatter) {
			return new FilterConverter<Entry<GKey, GValue>, Entry<GKey2, GValue2>>() {

				@Override
				public boolean accept(final Object input) {
					if(!(input instanceof Entry)) return false;
					final Entry<?, ?> entry = (Entry<?, ?>)input;
					return keyParser.accept(entry.getKey()) && valueParser.accept(entry.getValue());
				}

				@Override
				public Entry<GKey2, GValue2> convert(final Entry<GKey, GValue> input) {
					return new Entry<GKey2, GValue2>() {

						@Override
						public GKey2 getKey() {
							final GKey key = input.getKey();
							if(!keyParser.accept(key)) throw new IllegalArgumentException();
							return keyParser.convert(key);
						}

						@Override
						public GValue2 getValue() {
							final GValue value = input.getValue();
							if(!valueParser.accept(value)) throw new IllegalArgumentException();
							return valueParser.convert(value);
						}

						@Override
						public GValue2 setValue(final GValue2 value2) {
							if(!valueFormatter.accept(value2)) throw new IllegalArgumentException();
							final GValue value = input.setValue(valueFormatter.convert(value2));
							if(!valueParser.accept(value)) throw new IllegalArgumentException();
							return valueParser.convert(value);
						}

						@Override
						public int hashCode() {
							return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
						}

						@Override
						public boolean equals(final Object object) {
							if(!(object instanceof Entry<?, ?>)) return false;
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
		 * @param map {@link Map} mit den internen Einträgen.
		 * @param keyParser {@link FilterConverter} zur Umwandlung eines Schlüssels dieser {@link Map} in einen Schlüssel von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param keyFormatter {@link FilterConverter} zur Umwandlung eines Schlüssels von {@code data} in einen Schlüssel dieser {@link Map} sowie zur Erkennung
		 *        einer gültigen Eingabe für die Umwandlung.
		 * @param valueParser {@link FilterConverter} zur Umwandlung eines Werts dieser {@link Map} in einen Wert von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param valueFormatter {@link FilterConverter} zur Umwandlung eines Werts von {@code data} in einen Wert dieser {@link Map} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedMap(final Map<GKey2, GValue2> map, final FilterConverter<? super GKey, ? extends GKey2> keyParser,
			final FilterConverter<? super GKey2, ? extends GKey> keyFormatter, final FilterConverter<? super GValue, ? extends GValue2> valueParser,
			final FilterConverter<? super GValue2, ? extends GValue> valueFormatter) throws NullPointerException {
			if((map == null) || (keyParser == null) || (keyFormatter == null) || (valueParser == null) || (valueFormatter == null)) throw new NullPointerException();
			this.data = map;
			this.keyParser = keyParser;
			this.keyFormatter = keyFormatter;
			this.valueParser = valueParser;
			this.valueFormatter = valueFormatter;
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
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public boolean containsKey(final Object key) {
			if(!this.keyParser.accept(key)) return false;
			return this.data.containsKey(((Converter)this.keyParser).convert(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public boolean containsValue(final Object value) {
			if(!this.valueParser.accept(value)) return false;
			return this.data.containsValue(((Converter)this.valueParser).convert(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public GValue get(final Object key) {
			if(!this.keyParser.accept(key)) return null;
			final GValue2 value2 = this.data.get(((Converter)this.keyParser).convert(key));
			if(!this.valueFormatter.accept(value2)) throw new IllegalArgumentException();
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
			return new TranscodedSet<GKey, GKey2>(this.data.keySet(), this.keyParser, this.keyFormatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
			if(!this.keyParser.accept(key) || !this.valueParser.accept(value)) throw new IllegalArgumentException();
			final GValue2 value2 = this.data.put(this.keyParser.convert(key), this.valueParser.convert(value));
			if(!this.valueFormatter.accept(value2)) throw new IllegalArgumentException();
			return this.valueFormatter.convert(value2);
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public void putAll(final Map<? extends GKey, ? extends GValue> map) {
			this.data.putAll(new TranscodedMap(map, this.keyFormatter, this.keyParser, this.valueFormatter, this.valueParser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public GValue remove(final Object key) {
			if(!this.keyParser.accept(key)) return null;
			final GValue2 value2 = this.data.remove(((Converter)this.keyParser).convert(key));
			if(!this.valueFormatter.accept(value2)) throw new IllegalArgumentException();
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
			return new TranscodedCollection<GValue, GValue2>(this.data.values(), this.valueParser, this.valueFormatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return new TranscodedSet<Entry<GKey, GValue>, Entry<GKey2, GValue2>>(this.data.entrySet(), TranscodedMap.newEntryConverter(this.keyParser,
				this.valueParser, this.valueFormatter), TranscodedMap.newEntryConverter(this.keyFormatter, this.valueFormatter, this.valueParser));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Set} als umkodierende Sicht auf ein gegebenes {@link Set}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieses {@link Set}s.
	 * @param <GEntry2> Typ der Elemente des internen {@link Set}s.
	 */
	public static final class TranscodedSet<GEntry, GEntry2> extends AbstractSet<GEntry> {

		/**
		 * Dieses Feld speichert die {@link Set} mit den internen Elementen.
		 */
		final Set<GEntry2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements dieser {@link Set} in ein Element von {@link #data} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GEntry, ? extends GEntry2> parser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements von {@link #data} in ein Element dieser {@link Set} sowie zur Erkennung
		 * einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GEntry2, ? extends GEntry> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Set}.
		 * 
		 * @param data {@link Set} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieses {@link Set}s in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieses {@link Set}s sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedSet(final Set<GEntry2> data, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
			final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			if((data == null) || (parser == null) || (formatter == null)) throw new NullPointerException();
			this.data = data;
			this.parser = parser;
			this.formatter = formatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GEntry e) {
			if(!this.parser.accept(e)) throw new IllegalArgumentException();
			return this.data.add(this.parser.convert(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final Collection<? extends GEntry> c) {
			return this.data.addAll(new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if(!this.parser.accept(o)) return false;
			return this.data.remove(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.data.removeAll(new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.data.retainAll(new TranscodedCollection(c, this.formatter, this.parser));
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
			if(!this.parser.accept(o)) return false;
			return this.data.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.convertedIterator(this.formatter, Iterators.filteredIterator(this.formatter, this.data.iterator()));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List}. Die Methoden {@link ListIterator#next()} und
	 * {@link ListIterator#previous()} können eine {@link IllegalArgumentException} auslösen.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieser {@link List}.
	 * @param <GEntry2> Typ der Elemente der internen {@link List}.
	 */
	public static final class TranscodedList<GEntry, GEntry2> extends AbstractTranscodedList<GEntry, GEntry2> {

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param data {@link List} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link List} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedList(final List<GEntry2> data, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
			final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			super(data, parser, formatter);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List} mit {@link RandomAccess}. Die Methoden
	 * {@link ListIterator#next()} und {@link ListIterator#previous()} können eine {@link IllegalArgumentException} auslösen.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieser {@link List}.
	 * @param <GEntry2> Typ der Elemente der internen {@link List}.
	 */
	public static final class TranscodedRandomAccessList<GEntry, GEntry2> extends AbstractTranscodedList<GEntry, GEntry2> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param data {@link List} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link List} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link List} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedRandomAccessList(final List<GEntry2> data, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
			final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			super(data, parser, formatter);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Collection} als umkodierende Sicht auf eine gegebene {@link Collection}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieser {@link Collection}.
	 * @param <GEntry2> Typ der Elemente der internen {@link Collection}.
	 */
	public static final class TranscodedCollection<GEntry, GEntry2> extends AbstractCollection<GEntry> {

		/**
		 * Dieses Feld speichert die {@link Collection} mit den internen Elementen.
		 */
		final Collection<GEntry2> data;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements dieser {@link Collection} in ein Element von {@link #data} sowie zur
		 * Erkennung einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GEntry, ? extends GEntry2> parser;

		/**
		 * Dieses Feld speichert den {@link FilterConverter} zur Umwandlung eines Elements von {@link #data} in ein Element dieser {@link Collection} sowie zur
		 * Erkennung einer gültigen Eingabe für die Umwandlung.
		 */
		final FilterConverter<? super GEntry2, ? extends GEntry> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Collection}.
		 * 
		 * @param data {@link Collection} mit den internen Elementen.
		 * @param parser {@link FilterConverter} zur Umwandlung eines Elements dieser {@link Collection} in ein Element von {@code data} sowie zur Erkennung einer
		 *        gültigen Eingabe für die Umwandlung.
		 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements von {@code data} in ein Element dieser {@link Collection} sowie zur Erkennung
		 *        einer gültigen Eingabe für die Umwandlung.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedCollection(final Collection<GEntry2> data, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
			final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			if((data == null) || (parser == null) || (formatter == null)) throw new NullPointerException();
			this.data = data;
			this.parser = parser;
			this.formatter = formatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GEntry e) {
			if(!this.parser.accept(e)) throw new IllegalArgumentException();
			return this.data.add(this.parser.convert(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean addAll(final Collection<? extends GEntry> c) {
			return this.data.addAll(new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if(!this.parser.accept(o)) return false;
			return this.data.remove(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.data.removeAll(new TranscodedCollection(c, this.formatter, this.parser));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.data.retainAll(new TranscodedCollection(c, this.formatter, this.parser));
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
			if(!this.parser.accept(o)) return false;
			return this.data.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.convertedIterator(this.formatter, Iterators.filteredIterator(this.formatter, this.data.iterator()));
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Set} als unmodifizierbare Sicht auf die Vereinigungsmenge zweier {@link Set}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class UnionSet<GEntry> extends AbstractSet<GEntry> {

		/**
		 * Dieses Feld speichert das erste {@link Set}.
		 */
		final Set<? extends GEntry> set1;

		/**
		 * Dieses Feld speichert das zweite {@link Set}.
		 */
		final Set<? extends GEntry> set2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Set}s der Vereinigungsmenge.
		 * 
		 * @param set1 erstes {@link Set}.
		 * @param set2 zweites {@link Set}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public UnionSet(final Set<? extends GEntry> set1, final Set<? extends GEntry> set2) throws NullPointerException {
			if((set1 == null) || (set2 == null)) throw new NullPointerException();
			this.set1 = set1;
			this.set2 = set2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return -Iterators.skip(this.iterator(), -1) - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			if(this.set1.size() < this.set2.size()) return Iterators.unmodifiableIterator(Iterators.chainedIterator(Iterators.filteredIterator( //
				Filters.negationFilter(Filters.containsFilter(this.set2)), this.set1.iterator()), this.set2.iterator()));
			return Iterators.unmodifiableIterator(Iterators.chainedIterator(Iterators.filteredIterator( //
				Filters.negationFilter(Filters.containsFilter(this.set1)), this.set2.iterator()), this.set1.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.set1.contains(o) || this.set2.contains(o);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Set} als unmodifizierbare Sicht auf die Schnittmenge zweier {@link Set}s.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class IntersectionSet<GEntry> extends AbstractSet<GEntry> {

		/**
		 * Dieses Feld speichert das erste {@link Set}.
		 */
		final Set<? extends GEntry> set1;

		/**
		 * Dieses Feld speichert das zweite {@link Set}.
		 */
		final Set<? extends GEntry> set2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Set}s der Schnittmenge.
		 * 
		 * @param set1 erstes {@link Set}.
		 * @param set2 zweites {@link Set}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public IntersectionSet(final Set<? extends GEntry> set1, final Set<? extends GEntry> set2) throws NullPointerException {
			if((set1 == null) || (set2 == null)) throw new NullPointerException();
			this.set1 = set1;
			this.set2 = set2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return -Iterators.skip(this.iterator(), -1) - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			if(this.set1.size() < this.set2.size()) return Iterators.unmodifiableIterator(Iterators.filteredIterator( //
				Filters.containsFilter(this.set2), this.set1.iterator()));
			return Iterators.unmodifiableIterator(Iterators.filteredIterator( //
				Filters.containsFilter(this.set1), this.set2.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.set1.contains(o) && this.set2.contains(o);
		}

	}

	/**
	 * Diese Methode gibt eine rückwärts geordnete Sicht auf die gegebene {@link List} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param values {@link List}
	 * @return rückwärts geordnete {@link List}-Sicht.
	 * @throws NullPointerException Wenn die gegebene {@link List} {@code null} ist.
	 */
	public static <GValue> List<GValue> reverseList(final List<GValue> values) throws NullPointerException {
		return (values instanceof RandomAccess ? new ReverseRandomAccessList<GValue>(values) : new ReverseList<GValue>(values));
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als verkettete Sicht auf die gegebenen {@link List}s und gibt diese zurück. Wenn ein Elemente zwischen beiden
	 * {@link List}s eingefügt werden sollen, wird die erste {@link List} erweitert. Der Rückgabewert entspricht
	 * 
	 * <pre>chainedList(values1, values2, true)</pre>
	 * 
	 * @see #chainedCollection(Collection, Collection, boolean)
	 * @param <GValue> Typ der Elemente.
	 * @param values1 {@link List} der ersten Elemente.
	 * @param values2 {@link List} der letzten Elemente.
	 * @return verkettete {@link List}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> List<GValue> chainedList(final List<GValue> values1, final List<GValue> values2) throws NullPointerException {
		return Collections.chainedList(values1, values2, true);
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als verkettete Sicht auf die gegebenen {@link List}s und gibt diese zurück. Wenn ein Elemente zwischen beiden
	 * {@link List}s eingefügt werden sollen, entscheidet der Erweiterungsmodus, an welcher {@link List} diese Elemente angefügt werden. Ist der Erweiterungsmodus
	 * {@code true}, wird die erste {@link List} erweitert, bei {@code false} wird die zweite {@link List} erweitert.
	 * 
	 * @param <GValue> Typ der Elemente.
	 * @param values1 {@link List} der ersten Elemente.
	 * @param values2 {@link List} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link List}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> List<GValue> chainedList(final List<GValue> values1, final List<GValue> values2, final boolean extendMode) throws NullPointerException {
		return ((values1 instanceof RandomAccess) && (values2 instanceof RandomAccess)) ? new ChainedRandomAccessList<GValue>(values1, values2, extendMode)
			: new ChainedList<GValue>(values1, values2, extendMode);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection}s und gibt diese zurück. Wenn Elemente eingefügt
	 * werden sollen, wird die erste {@link Collection} erweitert. Der Rückgabewert entspricht
	 * 
	 * <pre>chainedCollection(values1, values2, true)</pre>
	 * 
	 * @see #chainedCollection(Collection, Collection, boolean)
	 * @param <GValue> Typ der Elemente.
	 * @param values1 {@link Collection} der ersten Elemente.
	 * @param values2 {@link Collection} der letzten Elemente.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> Collection<GValue> chainedCollection(final Collection<GValue> values1, final Collection<GValue> values2) throws NullPointerException {
		return Collections.chainedCollection(values1, values2, true);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als verkettete Sicht auf die gegebenen {@link Collection}s und gibt diese zurück. Wenn Elemente eingefügt
	 * werden sollen, entscheidet der Erweiterungsmodus, in welche {@link Collection} diese Elemente angefügt werden. Ist der Erweiterungsmodus {@code true}, wird
	 * die erste {@link Collection} erweitert, bei {@code false} wird die zweite {@link Collection} erweitert.
	 * 
	 * @param <GValue> Typ der Elemente.
	 * @param values1 {@link Collection} der ersten Elemente.
	 * @param values2 {@link Collection} der letzten Elemente.
	 * @param extendMode Erweiterungsmodus.
	 * @return verkettete {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GValue> Collection<GValue> chainedCollection(final Collection<GValue> values1, final Collection<GValue> values2, final boolean extendMode)
		throws NullPointerException {
		return new ChainedCollection<GValue>(values1, values2, extendMode);
	}

	/**
	 * Diese Methode erzeugt eine {@link Map} als umkodierende Sicht auf die gegebene {@link Map} und gibt diese zurück.
	 * 
	 * @param <GKey> Typ der Schlüssel der erzeugten {@link Map}.
	 * @param <GValue> Typ der Werte der erzeugten {@link Map}.
	 * @param <GKey2> Typ der Schlüssel der gegebenen {@link Map}.
	 * @param <GValue2> Typ der Werte der gegebenen {@link Map}.
	 * @param map {@link Map} mit den internen Einträgen.
	 * @param keyParser {@link FilterConverter} zur Umwandlung eines Schlüssels der erzeugten {@link Map} in einen Schlüssel der gegebenen {@link Map} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param keyFormatter {@link FilterConverter} zur Umwandlung eines Schlüssels der gegebenen {@link Map} in einen Schlüssel der erzeugten {@link Map} sowie
	 *        zur Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param valueParser {@link FilterConverter} zur Umwandlung eines Werts der erzeugten {@link Map} in einen Wert der gegebenen {@link Map} sowie zur Erkennung
	 *        einer gültigen Eingabe für die Umwandlung.
	 * @param valueFormatter {@link FilterConverter} zur Umwandlung eines Werts der gegebenen {@link Map} in einen Wert der erzeugten {@link Map} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link Map}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GKey, GValue, GKey2, GValue2> Map<GKey, GValue> transcodedMap(final Map<GKey2, GValue2> map,
		final FilterConverter<? super GKey, ? extends GKey2> keyParser, final FilterConverter<? super GKey2, ? extends GKey> keyFormatter,
		final FilterConverter<? super GValue, ? extends GValue2> valueParser, final FilterConverter<? super GValue2, ? extends GValue> valueFormatter)
		throws NullPointerException {
		return new TranscodedMap<GKey, GValue, GKey2, GValue2>(map, keyParser, keyFormatter, valueParser, valueFormatter);
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als umkodierende Sicht auf die gegebene {@link List} und gibt diese zurück. Die Methoden
	 * {@link ListIterator#next()} und {@link ListIterator#previous()} können eine {@link IllegalArgumentException} auslösen.
	 * 
	 * @param <GEntry> Typ der Elemente der erzeugten {@link List}.
	 * @param <GEntry2> Typ der Elemente der gegebenen {@link List}.
	 * @param data gegebene {@link List}.
	 * @param parser {@link FilterConverter} zur Umwandlung eines Elements der erzeugeten {@link List} in ein Element der gegebenen {@link List} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements der gegebenen {@link List} in ein Element der erzeugten {@link List} sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link List}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry, GEntry2> List<GEntry> transcodedList(final List<GEntry2> data, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
		final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
		return data instanceof RandomAccess ? new TranscodedRandomAccessList<GEntry, GEntry2>(data, parser, formatter) : new TranscodedList<GEntry, GEntry2>(data,
			parser, formatter);
	}

	/**
	 * Diese Methode erzeugt ein {@link Set} als umkodierende Sicht auf das gegebene {@link Set} und gibt dieses zurück.
	 * 
	 * @param <GEntry> Typ der Elemente des erzeugten {@link Set}s.
	 * @param <GEntry2> Typ der Elemente des gegebenen {@link Set}s.
	 * @param set gegebenes {@link Set}.
	 * @param parser {@link FilterConverter} zur Umwandlung eines Elements des erzeugeten {@link Set}s in ein Element des gegebenen {@link Set}s sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements des gegebenen {@link Set}s in ein Element des erzeugten {@link Set}s sowie zur
	 *        Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link Set}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry, GEntry2> Set<GEntry> transcodedSet(final Set<GEntry2> set, final FilterConverter<? super GEntry, ? extends GEntry2> parser,
		final FilterConverter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
		return new TranscodedSet<GEntry, GEntry2>(set, parser, formatter);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als umkodierende Sicht auf die gegebene {@link Collection} und gibt diese zurück.
	 * 
	 * @param <GEntry> Typ der Elemente der erzeugten {@link Collection}.
	 * @param <GEntry2> Typ der Elemente der gegebenen {@link Collection}.
	 * @param data gegebene {@link Collection}.
	 * @param parser {@link FilterConverter} zur Umwandlung eines Elements der erzeugeten {@link Collection} in ein Element der gegebenen {@link Collection} sowie
	 *        zur Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @param formatter {@link FilterConverter} zur Umwandlung eines Elements der gegebenen {@link Collection} in ein Element der erzeugten {@link Collection}
	 *        sowie zur Erkennung einer gültigen Eingabe für die Umwandlung.
	 * @return umkodierende {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry, GEntry2> Collection<GEntry> transcodedCollection(final Collection<GEntry2> data,
		final FilterConverter<? super GEntry, ? extends GEntry2> parser, final FilterConverter<? super GEntry2, ? extends GEntry> formatter)
		throws NullPointerException {
		return new TranscodedCollection<GEntry, GEntry2>(data, parser, formatter);
	}

}
