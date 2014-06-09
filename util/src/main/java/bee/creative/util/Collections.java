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
		final List<GEntry2> list;

		/**
		 * Dieses Feld speichert den {@link Filter} zur Erkennung von Elementen in {@link #remove(Object)}, {@link #contains(Object)}, {@link #indexOf(Object)} und
		 * {@link #lastIndexOf(Object)}.
		 */
		final Filter<? super Object> checker;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 */
		final Converter<? super GEntry, ? extends GEntry2> parser;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 */
		final Converter<? super GEntry2, ? extends GEntry> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param list {@link List} mit den internen Elementen.
		 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link #remove(Object)}, {@link #contains(Object)}, {@link #indexOf(Object)} und
		 *        {@link #lastIndexOf(Object)}. Zulässige Elemente werden in interne Elemente umgewandelt und an die interne {@link List} delegiert.
		 * @param parser {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 * @param formatter {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public AbstractTranscodedList(final List<GEntry2> list, final Filter<? super Object> checker, final Converter<? super GEntry, ? extends GEntry2> parser,
			final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			if((list == null) || (checker == null) || (parser == null) || (formatter == null)) throw new NullPointerException();
			this.list = list;
			this.checker = checker;
			this.parser = parser;
			this.formatter = formatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.list.subList(fromIndex, toIndex).clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry get(final int index) {
			return this.formatter.convert(this.list.get(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry set(final int index, final GEntry element) {
			return this.formatter.convert(this.list.set(index, this.parser.convert(element)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GEntry element) {
			this.list.add(index, this.parser.convert(element));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry remove(final int index) {
			return this.formatter.convert(this.list.remove(index));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if(!this.checker.accept(o)) return false;
			return this.list.remove(((Converter)this.parser).convert(o));
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
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean contains(final Object o) {
			if(!this.checker.accept(o)) return false;
			return this.list.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public int indexOf(final Object o) {
			if(!this.checker.accept(o)) return -1;
			return this.list.indexOf(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public int lastIndexOf(final Object o) {
			if(!this.checker.accept(o)) return -1;
			return this.list.lastIndexOf(((Converter)this.parser).convert(o));
		}

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
		 * Diese Klasse implementiert einrn abstrakten Eintrag einer {@link TranscodedMap}.
		 * 
		 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GKey> Typ des Schlüssels.
		 * @param <GValue> Typ des Werts.
		 */
		static abstract class TranscodedEntry<GKey, GValue> implements Entry<GKey, GValue> {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public int hashCode() {
				return Objects.hash(this.getKey()) ^ Objects.hash(this.getValue());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public boolean equals(final Object object) {
				if(!(object instanceof Entry<?, ?>)) return false;
				final Entry<?, ?> data = (Entry<?, ?>)object;
				return Objects.equals(this.getKey(), data.getKey()) && Objects.equals(this.getValue(), data.getValue());
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public String toString() {
				return this.getKey() + "=" + this.getValue();
			}

		}

		/**
		 * Dieses Feld speichert die {@link Map} mit den internen Einträgen.
		 */
		final Map<GKey2, GValue2> map;

		/**
		 * Dieses Feld speichert den {@link Filter} zur Erkennung von Elementen in {@link #get(Object)}, {@link #remove(Object)} und {@link #containsKey(Object)}.
		 */
		final Filter<? super Object> keyChecker;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines Schlüssels in einen internen Schlüssel.
		 */
		final Converter<? super GKey, ? extends GKey2> keyParser;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines internen Schlüssels in einen Schlüssel.
		 */
		final Converter<? super GKey2, ? extends GKey> keyFormatter;

		/**
		 * Dieses Feld speichert den {@link Filter} zur Erkennung von Werten in {@link #containsValue(Object)}.
		 */
		final Filter<? super Object> valueChecker;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines Werts in einen internen Wert.
		 */
		final Converter<? super GValue, ? extends GValue2> valueParser;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines internen Werts in einen Wert.
		 */
		final Converter<? super GValue2, ? extends GValue> valueFormatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Map}.
		 * 
		 * @param map {@link Map} mit den internen Einträgen.
		 * @param keyChecker {@link Filter} zur Erkennung von Schlüsseln in {@link #get(Object)}, {@link #remove(Object)} und {@link #containsKey(Object)}.
		 *        Zulässige Schlüssel werden in Schlüssel der internen {@link Map} umgewandelt und an diese delegiert.
		 * @param keyParser {@link Converter} zur Umwandlung eines Schlüssels in einen internen Schlüssel.
		 * @param keyFormatter {@link Converter} zur Umwandlung eines internen Schlüssels in einen Schlüssel.
		 * @param valueChecker {@link Filter} zur Erkennung von Werten in {@link #containsValue(Object)}. Zulässige Werte werden in Werte der internen {@link Map}
		 *        umgewandelt und an diese delegiert.
		 * @param valueParser {@link Converter} zur Umwandlung eines Werts in einen internen Wert.
		 * @param valueFormatter {@link Converter} zur Umwandlung eines internen Werts in einen Wert.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedMap(final Map<GKey2, GValue2> map, final Filter<? super Object> keyChecker, final Converter<? super GKey, ? extends GKey2> keyParser,
			final Converter<? super GKey2, ? extends GKey> keyFormatter, final Filter<? super Object> valueChecker,
			final Converter<? super GValue, ? extends GValue2> valueParser, final Converter<? super GValue2, ? extends GValue> valueFormatter)
			throws NullPointerException {
			if((map == null) || (keyChecker == null) || (keyParser == null) || (keyFormatter == null) || (valueChecker == null) || (valueParser == null)
				|| (valueFormatter == null)) throw new NullPointerException();
			this.map = map;
			this.keyChecker = keyChecker;
			this.keyParser = keyParser;
			this.keyFormatter = keyFormatter;
			this.valueChecker = valueChecker;
			this.valueParser = valueParser;
			this.valueFormatter = valueFormatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.map.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public boolean containsKey(final Object key) {
			if(!this.keyChecker.accept(key)) return false;
			return this.map.containsKey(((Converter)this.keyParser).convert(key));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public boolean containsValue(final Object value) {
			if(!this.valueChecker.accept(value)) return false;
			return this.map.containsValue(((Converter)this.valueParser).convert(value));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public GValue get(final Object key) {
			if(!this.keyChecker.accept(key)) return null;
			return this.valueFormatter.convert(this.map.get(((Converter)this.keyParser).convert(key)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.map.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<GKey> keySet() {
			return new TranscodedSet<GKey, GKey2>(this.map.keySet(), this.keyChecker, this.keyParser, this.keyFormatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue put(final GKey key, final GValue value) {
			return this.valueFormatter.convert(this.map.put(this.keyParser.convert(key), this.valueParser.convert(value)));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"rawtypes", "unchecked"})
		@Override
		public GValue remove(final Object key) {
			if(!this.keyChecker.accept(key)) return null;
			return this.valueFormatter.convert(this.map.remove(((Converter)this.keyParser).convert(key)));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.map.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Collection<GValue> values() {
			return new TranscodedCollection<GValue, GValue2>(this.map.values(), this.valueChecker, this.valueParser, this.valueFormatter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Set<Entry<GKey, GValue>> entrySet() {
			return new TranscodedSet<Entry<GKey, GValue>, Entry<GKey2, GValue2>>(this.map.entrySet(), new Filter<Object>() {

				@Override
				public boolean accept(final Object input) {
					if(!(input instanceof Entry)) return false;
					final Entry<?, ?> entry = (Entry<?, ?>)input;
					return TranscodedMap.this.keyChecker.accept(entry.getKey()) && TranscodedMap.this.valueChecker.accept(entry.getValue());
				}

			}, new Converter<Entry<GKey, GValue>, Entry<GKey2, GValue2>>() {

				@Override
				public Entry<GKey2, GValue2> convert(final Entry<GKey, GValue> input) {
					return new TranscodedEntry<GKey2, GValue2>() {

						@Override
						public GKey2 getKey() {
							return TranscodedMap.this.keyParser.convert(input.getKey());
						}

						@Override
						public GValue2 getValue() {
							return TranscodedMap.this.valueParser.convert(input.getValue());
						}

						@Override
						public GValue2 setValue(final GValue2 value) {
							return TranscodedMap.this.valueParser.convert(input.setValue(TranscodedMap.this.valueFormatter.convert(value)));
						}

					};
				}

			}, new Converter<Entry<GKey2, GValue2>, Entry<GKey, GValue>>() {

				@Override
				public Entry<GKey, GValue> convert(final Entry<GKey2, GValue2> input) {
					return new TranscodedEntry<GKey, GValue>() {

						@Override
						public GKey getKey() {
							return TranscodedMap.this.keyFormatter.convert(input.getKey());
						}

						@Override
						public GValue getValue() {
							return TranscodedMap.this.valueFormatter.convert(input.getValue());
						}

						@Override
						public GValue setValue(final GValue value) {
							return TranscodedMap.this.valueFormatter.convert(input.setValue(TranscodedMap.this.valueParser.convert(value)));
						}

					};
				}

			});
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
		 * Dieses Feld speichert das {@link Set} mit den internen Elementen.
		 */
		final Set<GEntry2> set;

		/**
		 * Dieses Feld speichert den {@link Filter} zur Erkennung von Elementen in {@link #remove(Object)} und {@link #contains(Object)}.
		 */
		final Filter<? super Object> checker;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 */
		final Converter<? super GEntry, ? extends GEntry2> parser;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 */
		final Converter<? super GEntry2, ? extends GEntry> formatter;

		/**
		 * Dieser Konstruktor initialisiert das Konvertierte {@link Set}.
		 * 
		 * @param set {@link Set} mit den internen Elementen.
		 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link #remove(Object)} und {@link #contains(Object)}. Zulässige Elemente werden in
		 *        interne Elemente umgewandelt und an das interne {@link Set} delegiert.
		 * @param parser {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 * @param formatter {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedSet(final Set<GEntry2> set, final Filter<? super Object> checker, final Converter<? super GEntry, ? extends GEntry2> parser,
			final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			if((set == null) || (checker == null) || (parser == null) || (formatter == null)) throw new NullPointerException();
			this.set = set;
			this.checker = checker;
			this.parser = parser;
			this.formatter = formatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GEntry e) {
			return this.set.add(this.parser.convert(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if(!this.checker.accept(o)) return false;
			return this.set.remove(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.set.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.set.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.set.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean contains(final Object o) {
			if(!this.checker.accept(o)) return false;
			return this.set.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.convertedIterator(this.formatter, this.set.iterator());
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieser {@link List}.
	 * @param <GEntry2> Typ der Elemente der internen {@link List}.
	 */
	public static final class TranscodedList<GEntry, GEntry2> extends AbstractTranscodedList<GEntry, GEntry2> {

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param list {@link List} mit den internen Elementen.
		 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link #remove(Object)}, {@link #contains(Object)}, {@link #indexOf(Object)} und
		 *        {@link #lastIndexOf(Object)}. Zulässige Elemente werden in interne Elemente umgewandelt und an die interne {@link List} delegiert.
		 * @param parser {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 * @param formatter {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedList(final List<GEntry2> list, final Filter<? super Object> checker, final Converter<? super GEntry, ? extends GEntry2> parser,
			final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			super(list, checker, parser, formatter);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als umkodierende Sicht auf eine gegebenen {@link List} mit {@link RandomAccess}.
	 * 
	 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente dieser {@link List}.
	 * @param <GEntry2> Typ der Elemente der internen {@link List}.
	 */
	public static final class TranscodedRandomAccessList<GEntry, GEntry2> extends AbstractTranscodedList<GEntry, GEntry2> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link List}.
		 * 
		 * @param list {@link List} mit den internen Elementen.
		 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link #remove(Object)}, {@link #contains(Object)}, {@link #indexOf(Object)} und
		 *        {@link #lastIndexOf(Object)}. Zulässige Elemente werden in interne Elemente umgewandelt und an die interne {@link List} delegiert.
		 * @param parser {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 * @param formatter {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedRandomAccessList(final List<GEntry2> list, final Filter<? super Object> checker,
			final Converter<? super GEntry, ? extends GEntry2> parser, final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			super(list, checker, parser, formatter);
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
		final Collection<GEntry2> collection;

		/**
		 * Dieses Feld speichert den {@link Filter} zur Erkennung von Elementen in {@link #remove(Object)} und {@link #contains(Object)}.
		 */
		final Filter<? super Object> checker;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 */
		final Converter<? super GEntry, ? extends GEntry2> parser;

		/**
		 * Dieses Feld speichert den {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 */
		final Converter<? super GEntry2, ? extends GEntry> formatter;

		/**
		 * Dieser Konstruktor initialisiert die Konvertierte {@link Collection}.
		 * 
		 * @param collection {@link Collection} mit den internen Elementen.
		 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link #remove(Object)} und {@link #contains(Object)}. Zulässige Elemente werden in
		 *        interne Elemente umgewandelt und an die interne {@link Collection} delegiert.
		 * @param parser {@link Converter} zur Umwandlung eines Elements in ein internes Elemente.
		 * @param formatter {@link Converter} zur Umwandlung eines internen Elements in ein Element.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public TranscodedCollection(final Collection<GEntry2> collection, final Filter<? super Object> checker,
			final Converter<? super GEntry, ? extends GEntry2> parser, final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
			if((collection == null) || (checker == null) || (parser == null) || (formatter == null)) throw new NullPointerException();
			this.collection = collection;
			this.checker = checker;
			this.parser = parser;
			this.formatter = formatter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean add(final GEntry e) {
			return this.collection.add(this.parser.convert(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean remove(final Object o) {
			if(!this.checker.accept(o)) return false;
			return this.collection.remove(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.collection.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.collection.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.collection.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public boolean contains(final Object o) {
			if(!this.checker.accept(o)) return false;
			return this.collection.contains(((Converter)this.parser).convert(o));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.convertedIterator(this.formatter, this.collection.iterator());
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
	 * @param keyChecker {@link Filter} zur Erkennung von Schlüsseln in {@link Map#get(Object)}, {@link Map#remove(Object)} und {@link Map#containsKey(Object)}
	 *        der erzeugten {@link Map}. Zulässige Schlüssel werden in Schlüssel der gegebenen {@link Map} umgewandelt und an die gegebene {@link Map} delegiert.
	 * @param keyParser {@link Converter} zur Umwandlung eines Schlüssels in einen internen Schlüssel.
	 * @param keyFormatter {@link Converter} zur Umwandlung eines internen Schlüssels in einen Schlüssel.
	 * @param valueChecker {@link Filter} zur Erkennung von Werten in {@link Map#containsValue(Object)}. Zulässige Werte werden in Werte der gegebenen {@link Map}
	 *        umgewandelt und an die gegebene {@link Map} delegiert.
	 * @param valueParser {@link Converter} zur Umwandlung eines Werts in einen internen Wert.
	 * @param valueFormatter {@link Converter} zur Umwandlung eines internen Werts in einen Wert.
	 * @return umkodierende {@link Map}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GKey, GValue, GKey2, GValue2> Map<GKey, GValue> transcodedMap(final Map<GKey2, GValue2> map, final Filter<? super Object> keyChecker,
		final Converter<? super GKey, ? extends GKey2> keyParser, final Converter<? super GKey2, ? extends GKey> keyFormatter,
		final Filter<? super Object> valueChecker, final Converter<? super GValue, ? extends GValue2> valueParser,
		final Converter<? super GValue2, ? extends GValue> valueFormatter) throws NullPointerException {
		return new TranscodedMap<GKey, GValue, GKey2, GValue2>(map, keyChecker, keyParser, keyFormatter, valueChecker, valueParser, valueFormatter);
	}

	/**
	 * Diese Methode erzeugt eine {@link List} als umkodierende Sicht auf die gegebene {@link List} und gibt diese zurück.
	 * 
	 * @param <GEntry> Typ der Elemente der erzeugten {@link List}.
	 * @param <GEntry2> Typ der Elemente der gegebenen {@link List}.
	 * @param list gegebene {@link List}.
	 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link List#remove(Object)}, {@link List#contains(Object)},
	 *        {@link List#indexOf(Object)} und {@link List#lastIndexOf(Object)} der erzeugten {@link List}. Zulässige Elemente werden in Elemente der gegebenen
	 *        {@link List} umgewandelt und an die gegebene {@link List} delegiert.
	 * @param parser {@link Converter} zur Umwandlung eines Elements der erzeugeten {@link List} in ein Element der gegebenen {@link List}.
	 * @param formatter {@link Converter} zur Umwandlung eines Elements der gegebenen {@link List} in ein Element der erzeugten {@link List}.
	 * @return umkodierende {@link List}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry, GEntry2> List<GEntry> transcodedList(final List<GEntry2> list, final Filter<? super Object> checker,
		final Converter<? super GEntry, ? extends GEntry2> parser, final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
		return list instanceof RandomAccess ? new TranscodedRandomAccessList<GEntry, GEntry2>(list, checker, parser, formatter)
			: new TranscodedList<GEntry, GEntry2>(list, checker, parser, formatter);
	}

	/**
	 * Diese Methode erzeugt ein {@link Set} als umkodierende Sicht auf das gegebene {@link Set} und gibt dieses zurück.
	 * 
	 * @param <GEntry> Typ der Elemente des erzeugten {@link Set}s.
	 * @param <GEntry2> Typ der Elemente des gegebenen {@link Set}s.
	 * @param set gegebenes {@link Set}.
	 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link Set#remove(Object)} und {@link Set#contains(Object)} des erzeugten {@link Set}s.
	 *        Zulässige Elemente werden in Elemente des gegebenen {@link Set} umgewandelt und an das gegebene {@link Set} delegiert.
	 * @param parser {@link Converter} zur Umwandlung eines Elements des erzeugeten {@link Set}s in ein Element des gegebenen {@link Set}s.
	 * @param formatter {@link Converter} zur Umwandlung eines Elements des gegebenen {@link Set}s in ein Element des erzeugten {@link Set}s.
	 * @return umkodierende {@link Set}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry, GEntry2> Set<GEntry> transcodedSet(final Set<GEntry2> set, final Filter<? super Object> checker,
		final Converter<? super GEntry, ? extends GEntry2> parser, final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
		return new TranscodedSet<GEntry, GEntry2>(set, checker, parser, formatter);
	}

	/**
	 * Diese Methode erzeugt eine {@link Collection} als umkodierende Sicht auf die gegebene {@link Collection} und gibt diese zurück.
	 * 
	 * @param <GEntry> Typ der Elemente der erzeugten {@link Collection}.
	 * @param <GEntry2> Typ der Elemente der gegebenen {@link Collection}.
	 * @param collection gegebene {@link Collection}.
	 * @param checker {@link Filter} zur Erkennung zulässiger Elementen in {@link Collection#remove(Object)} und {@link Collection#contains(Object)} der erzeugten
	 *        {@link Collection}. Zulässige Elemente werden in Elemente der gegebenen {@link Collection} umgewandelt und an die gegebene {@link Collection}
	 *        delegiert.
	 * @param parser {@link Converter} zur Umwandlung eines Elements der erzeugeten {@link Collection} in ein Element der gegebenen {@link Collection}.
	 * @param formatter {@link Converter} zur Umwandlung eines Elements der gegebenen {@link Collection} in ein Element der erzeugten {@link Collection}.
	 * @return umkodierende {@link Collection}-Sicht.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry, GEntry2> Collection<GEntry> transcodedCollection(final Collection<GEntry2> collection, final Filter<? super Object> checker,
		final Converter<? super GEntry, ? extends GEntry2> parser, final Converter<? super GEntry2, ? extends GEntry> formatter) throws NullPointerException {
		return new TranscodedCollection<GEntry, GEntry2>(collection, checker, parser, formatter);
	}

}
