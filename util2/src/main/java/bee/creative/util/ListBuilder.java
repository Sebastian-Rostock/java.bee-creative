package bee.creative.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Diese Klasse implementiert Methoden zur Bereitstellung eines {@link List}-{@link Builder}s.
 * 
 * @see ListBuilder1
 * @see ListBuilder2
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class ListBuilder {

	/**
	 * Diese Schnittstelle definiert einen {@link List}-{@link Builder}, der eine konfigurierte {@link List} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link List} umwandeln sowie durch das Hinzufügen von Werten modifizieren kann.
	 * 
	 * @see ListBuilder#reverseList(List)
	 * @see Collections#checkedList(List, Class)
	 * @see Collections#synchronizedList(List)
	 * @see Collections#unmodifiableList(List)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 */
	public static interface ListBuilder1<GValue, GList extends List<GValue>> extends ValuesBuilder<GValue>, ListBuilder2<GValue, GList> {

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> add(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> remove(GValue value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(GValue... value);

		/**
		 * {@inheritDoc}
		 * 
		 * @return {@link List}-{@link Builder}.
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(Iterable<? extends GValue> value);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asReverseList();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asCheckedList(Class<GValue> type);

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asSynchronizedList();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder2<GValue, List<GValue>> asUnmodifiableList();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GList build();

	}

	/**
	 * Diese Schnittstelle definiert einen {@link List}-{@link Builder}, der eine konfigurierte {@link List} in eine {@code reverse}-, {@code checked}-, {@code synchronized}- oder {@code unmodifiable}-{@link List} umwandeln kann.
	 * 
	 * @see ListBuilder#reverseList(List)
	 * @see Collections#checkedList(List, Class)
	 * @see Collections#synchronizedList(List)
	 * @see Collections#unmodifiableList(List)
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 */
	public static interface ListBuilder2<GValue, GList extends List<GValue>> extends Builder<GList> {

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link ListBuilder#reverseList(List)}.
		 * 
		 * @see ListBuilder#reverseList(List)
		 * @return {@link List}-{@link Builder}.
		 */
		public ListBuilder2<GValue, List<GValue>> asReverseList();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link Collections#checkedList(List, Class)}.
		 * 
		 * @see Collections#checkedList(List, Class)
		 * @param type {@link Class} der Werte.
		 * @return {@link List}-{@link Builder}.
		 */
		public ListBuilder2<GValue, List<GValue>> asCheckedList(Class<GValue> type);

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link Collections#synchronizedList(List)}.
		 * 
		 * @see Collections#synchronizedList(List)
		 * @return {@link List}-{@link Builder}.
		 */
		public ListBuilder2<GValue, List<GValue>> asSynchronizedList();

		/**
		 * Diese Methode konvertiert die konfigurierte {@link List} via {@link Collections#unmodifiableList(List)}.
		 * 
		 * @see Collections#unmodifiableList(List)
		 * @return {@link ListBuilder2}.
		 */
		public ListBuilder2<GValue, List<GValue>> asUnmodifiableList();

		/**
		 * Diese Methode gibt die konfigurierte {@link List} zurück.
		 * 
		 * @return {@link List}.
		 */
		@Override
		public GList build() throws IllegalStateException;

	}

	/**
	 * Diese Klasse implementiert den {@link ListBuilder1}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 */
	static final class ListBuilderImpl<GValue, GList extends List<GValue>> implements ListBuilder1<GValue, GList> {

		/**
		 * Dieses Feld speichert die {@link List}.
		 */
		final GList list;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}.
		 */
		public ListBuilderImpl(final GList list) {
			this.list = list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> add(final GValue value) {
			this.list.add(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(final GValue... value) {
			this.list.addAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> addAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.list, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> remove(final GValue value) {
			this.list.remove(value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(final GValue... value) {
			this.list.removeAll(Arrays.asList(value));
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, GList> removeAll(final Iterable<? extends GValue> value) {
			Iterables.appendAll(this.list, value);
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asReverseList() {
			return new ListBuilderImpl<GValue, List<GValue>>(ListBuilder.reverseList(this.list));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asCheckedList(final Class<GValue> type) {
			return new ListBuilderImpl<GValue, List<GValue>>(Collections.checkedList(this.list, type));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asSynchronizedList() {
			return new ListBuilderImpl<GValue, List<GValue>>(Collections.synchronizedList(this.list));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListBuilder1<GValue, List<GValue>> asUnmodifiableList() {
			return new ListBuilderImpl<GValue, List<GValue>>(Collections.unmodifiableList(this.list));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GList build() {
			return this.list;
		}

	}

	/**
	 * Diese Klasse implementiert eine {@link List} als rückwärts geordnete Sicht auf eine gegebene {@link List}.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GValue> Typ der Elemente.
	 */
	static class ReverseList<GValue> extends AbstractList<GValue> {

		/**
		 * Dieses Feld speichert die {@link List}.
		 */
		List<GValue> list;

		/**
		 * Dieser Konstruktor initialisiert die {@link List}.
		 * 
		 * @param list {@link List}
		 * @throws NullPointerException wenn die gegebene {@link List} {@code null} ist.
		 */
		public ReverseList(final List<GValue> list) throws NullPointerException {
			if(list == null) throw new NullPointerException();
			this.list = list;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void removeRange(final int fromIndex, final int toIndex) {
			this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2).clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<GValue> subList(final int fromIndex, final int toIndex) {
			return new ReverseList<GValue>(this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2));
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
		public GValue remove(final int index) {
			return this.list.remove(this.list.size() - index - 1);
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
		public int size() {
			return this.list.size();
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
			return index < 0 ? index : this.list.size() - index - 1;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			final int index = this.list.lastIndexOf(o);
			return index < 0 ? index : this.list.size() - index - 1;
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
		public boolean removeAll(final Collection<?> c) {
			return this.list.removeAll(c);
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
		public Iterator<GValue> iterator() {
			return super.listIterator(0);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<GValue> listIterator(final int index) {
			return new ListIterator<GValue>() {

				final ListIterator<GValue> iterator = ReverseList.this.list.listIterator(ReverseList.this.list.size() - index);

				@Override
				public boolean hasNext() {
					return this.iterator.hasPrevious();
				}

				@Override
				public GValue next() {
					return this.iterator.previous();
				}

				@Override
				public boolean hasPrevious() {
					return this.iterator.hasNext();
				}

				@Override
				public GValue previous() {
					return this.iterator.next();
				}

				@Override
				public int nextIndex() {
					return ReverseList.this.list.size() - this.iterator.previousIndex() - 1;
				}

				@Override
				public int previousIndex() {
					return ReverseList.this.list.size() - this.iterator.nextIndex() - 1;
				}

				@Override
				public void remove() {
					this.iterator.remove();
				}

				@Override
				public void set(final GValue e) {
					this.iterator.set(e);
				}

				@Override
				public void add(final GValue e) {
					this.iterator.add(e);
					this.iterator.hasPrevious();
					this.iterator.previous();
				}

			};

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
			return new ReverseRandomAccessList<GValue>(this.list.subList(this.list.size() - toIndex - 2, this.list.size() - fromIndex - 2));
		}

	}

	/**
	 * Diese Methode gibt einen neuen {@link ListBuilder} zurück.
	 * 
	 * @return {@link ListBuilder}.
	 */
	public static ListBuilder use() {
		return new ListBuilder();
	}

	/**
	 * Diese Methode gibt eine rückwärts geordnete Sicht auf die gegebene {@link List} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param list {@link List}
	 * @return rückwärts geordnete {@link List}-Sicht.
	 * @throws NullPointerException Wenn die gegebene {@link List} {@code null} ist.
	 */
	public static <GValue> List<GValue> reverseList(final List<GValue> list) throws NullPointerException {
		return (list instanceof RandomAccess ? new ReverseRandomAccessList<GValue>(list) : new ReverseList<GValue>(list));
	}

	/**
	 * Diese Methode gibt einen {@link List}-{@link Builder} für die gegebene {@link List} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param <GList> Typ der {@link List}.
	 * @param list {@link List}.
	 * @return {@link List}-{@link Builder}.
	 * @throws NullPointerException Wenn die gegebene {@link List} {@code null} ist.
	 */
	public <GValue, GList extends List<GValue>> ListBuilder1<GValue, GList> list(final GList list) throws NullPointerException {
		if(list == null) throw new NullPointerException();
		return new ListBuilderImpl<GValue, GList>(list);
	}

	/**
	 * Diese Methode einen neuen {@link ArrayList}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link ArrayList}-{@link Builder}.
	 */
	public <GValue> ListBuilder1<GValue, ArrayList<GValue>> newArrayList() {
		return this.newArrayList(10);
	}

	/**
	 * Diese Methode einen neuen {@link ArrayList}-{@link Builder} mit der gegebenen Kapazität zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @param capacity Kapazität.
	 * @return {@link ArrayList}-{@link Builder}.
	 * @throws IllegalArgumentException Wenn die gegebene Kapazität negativ ist.
	 */
	public <GValue> ListBuilder1<GValue, ArrayList<GValue>> newArrayList(final int capacity) throws IllegalArgumentException {
		return this.list(new ArrayList<GValue>(capacity));
	}

	/**
	 * Diese Methode einen neuen {@link LinkedList}-{@link Builder} zurück.
	 * 
	 * @param <GValue> Typ der Werte.
	 * @return {@link LinkedList}-{@link Builder}.
	 */
	public <GValue> ListBuilder1<GValue, LinkedList<GValue>> newLinkedList() {
		return this.list(new LinkedList<GValue>());
	}

}