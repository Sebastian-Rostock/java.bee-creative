package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Diese Klasse implementiert verkettete und rückwärts geordnete {@link List}-Sichten sowie eine verkettete {@link Collection}-Sicht.
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
	static class ReverseList<GValue> extends AbstractList<GValue> {

		/**
		 * Diese Klasse implementiert den {@link ListIterator} einer {@link ReverseList}.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ der Elemente.
		 */
		static final class ReverseIterator<GValue> implements ListIterator<GValue> {

			/**
			 * Dieses Feld speichert die {@link ReverseList}.
			 */
			final ReverseList<GValue> list;

			/**
			 * Dieses Feld speichert die Größe der {@link ReverseList}.
			 */
			int size;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link ReverseList#values}.
			 */
			final ListIterator<GValue> iterator;

			/**
			 * Dieser Konstruktor initialisiert {@link ReverseList} und Index.
			 * 
			 * @param list {@link ReverseList}.
			 * @param index Index.
			 */
			public ReverseIterator(final ReverseList<GValue> list, final int index) {
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
		List<GValue> values;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}
		 * @throws NullPointerException wenn die gegebene {@link List} {@code null} ist.
		 */
		public ReverseList(final List<GValue> list) throws NullPointerException {
			if(list == null) throw new NullPointerException();
			this.values = list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.values.subList(this.values.size() - toIndex - 2, this.values.size() - fromIndex - 2).clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GValue> subList(final int fromIndex, final int toIndex) {
			return new ReverseList<GValue>(this.values.subList(this.values.size() - toIndex - 2, this.values.size() - fromIndex - 2));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final int index) {
			return this.values.get(this.values.size() - index - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue set(final int index, final GValue element) {
			return this.values.set(this.values.size() - index - 1, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void add(final int index, final GValue element) {
			this.values.add(this.values.size() - index, element);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue remove(final int index) {
			return this.values.remove(this.values.size() - index - 1);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			this.values.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return this.values.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEmpty() {
			return this.values.isEmpty();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			final int index = this.values.lastIndexOf(o);
			return index < 0 ? index : this.values.size() - index - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			final int index = this.values.lastIndexOf(o);
			return index < 0 ? index : this.values.size() - index - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean contains(final Object o) {
			return this.values.contains(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean containsAll(final Collection<?> c) {
			return this.values.containsAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.values.removeAll(c);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.values.retainAll(c);
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
	 * Diese Klasse implementiert eine {@link ReverseList} mit {@link RandomAccess}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	static class ReverseRandomAccessList<GValue> extends ReverseList<GValue> implements RandomAccess {

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}
		 * @throws NullPointerException wenn die gegebene {@link List} {@code null} ist.
		 */
		public ReverseRandomAccessList(final List<GValue> list) throws NullPointerException {
			super(list);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GValue> subList(final int fromIndex, final int toIndex) {
			return new ReverseRandomAccessList<GValue>(this.values.subList(this.values.size() - toIndex - 2, this.values.size() - fromIndex - 2));
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als verkettete Sicht auf zwei gegebenen {@link List}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	static final class ChainedList<GValue> extends AbstractList<GValue> {

		/**
		 * Diese Klasse implementiert den {@link ListIterator} zu {@link ChainedList}.
		 * 
		 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
		 * @param <GValue> Typ der Elemente.
		 */
		static final class ChainedIterator<GValue> implements ListIterator<GValue> {

			/**
			 * Dieses Feld speichert die {@link ChainedList}.
			 */
			final ChainedList<GValue> list;

			/**
			 * Dieses Feld speichert die Größe von {@link ChainedList#values1}.
			 */
			int size;

			/**
			 * Dieses Feld speichert gewählten {@link ListIterator}.
			 */
			ListIterator<GValue> iterator;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link ChainedList#values1}.
			 */
			final ListIterator<GValue> iterator1;

			/**
			 * Dieses Feld speichert den {@link ListIterator} von {@link ChainedList#values2}.
			 */
			final ListIterator<GValue> iterator2;

			/**
			 * Dieser Konstruktor initialisiert {@link ChainedList} und Index.
			 * 
			 * @param list {@link ChainedList}.
			 * @param index Index.
			 */
			public ChainedIterator(final ChainedList<GValue> list, final int index) {
				this.size = list.values1.size();
				this.list = list;
				if(index < this.size){
					this.iterator1 = this.iterator = list.values1.listIterator(index);
					this.iterator2 = list.values2.listIterator(0);
				}else{
					this.iterator1 = list.values1.listIterator(this.size);
					this.iterator2 = this.iterator = list.values2.listIterator(index - this.size);
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
				this.size = this.list.values1.size();
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
				this.size = this.list.values1.size();
			}

		}

		/**
		 * Dieses Feld speichert die erste {@link List}.
		 */
		final List<GValue> values1;

		/**
		 * Dieses Feld speichert die zweite {@link List}.
		 */
		final List<GValue> values2;

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
		public ChainedList(final List<GValue> values1, final List<GValue> values2, final boolean extendMode) throws NullPointerException {
			if((values1 == null) || (values2 == null)) throw new NullPointerException();
			this.extendMode = extendMode;
			this.values1 = values1;
			this.values2 = values2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue get(final int index) {
			return index < this.values1.size() ? this.values1.get(index) : this.values2.get(index - this.values1.size());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GValue set(final int index, final GValue element) {
			return index < this.values1.size() ? this.values1.set(index, element) : this.values2.set(index - this.values1.size(), element);
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
		public void add(final int index, final GValue element) {
			final int size = this.values1.size();
			if((index < size) || ((index == size) && this.extendMode)){
				this.values1.add(index, element);
			}else{
				this.values2.add(index - size, element);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean addAll(final int index, final Collection<? extends GValue> c) {
			final int size = this.values1.size();
			if((index < size) || ((index == size) && this.extendMode)) return this.values1.addAll(index, c);
			return this.values2.addAll(index - size, c);
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
		public boolean retainAll(final Collection<?> c) {
			if(this.values1.retainAll(c)){
				this.values2.retainAll(c);
				return true;
			}else return this.values2.retainAll(c);
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GValue> listIterator(final int index) {
			return new ChainedIterator<GValue>(this, index);
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link Collection} als verkettete Sicht auf zwei gegebenen {@link Collection}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	static class ChainedCollection<GValue> extends AbstractCollection<GValue> {

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
		public boolean retainAll(final Collection<?> c) {
			if(!this.values1.retainAll(c)) return this.values2.retainAll(c);
			this.values2.retainAll(c);
			return true;
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
		return new ChainedList<GValue>(values1, values2, extendMode);
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

}
