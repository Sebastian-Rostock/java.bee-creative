package bee.creative.util;

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import bee.creative.util.Compact.CompactSet;

public class Pairs {

	static class absPair<T> {

		T a;

		T b;

	}

	static abstract class absPairColle<E, T extends Collection<E>> extends absPair<T> implements Collection<E> {

		@Override
		public int size() {
			return this.a.size() + this.b.size();
		}

		@Override
		public boolean isEmpty() {
			return this.a.isEmpty() && this.b.isEmpty();
		}

		@Override
		public boolean contains(final Object o) {
			return this.a.contains(o) || this.b.contains(o);
		}

		@Override
		public Iterator<E> iterator() {
			if(this.a.isEmpty()) return this.b.iterator();
			if(this.b.isEmpty()) return this.a.iterator();
			return Iterators.chainedIterator(this.a.iterator(), this.b.iterator());
		}

		@Override
		public Object[] toArray() {
			if(this.a.isEmpty()) return this.b.toArray();
			if(this.b.isEmpty()) return this.a.toArray();
			final Object[] o1 = this.a.toArray();
			final Object[] o2 = this.a.toArray();
			final Object[] o12 = new Object[o1.length + o2.length];
			System.arraycopy(o1, 0, o12, 0, o1.length);
			System.arraycopy(o2, 0, o12, o1.length, o2.length);
			return o12;
		}

		@Override
		public <T> T[] toArray(final T[] x) {
			if(this.a.isEmpty()) return this.b.toArray(x);
			if(this.b.isEmpty()) return this.a.toArray(x);
			final T[] o1 = this.a.toArray(x);
			final T[] o2 = this.a.toArray(x);
			final T[] o12 = Arrays.copyOf(o1, o1.length + o2.length);
			System.arraycopy(o2, 0, o12, o1.length, o2.length);
			return o12;
		}

		@Override
		public boolean containsAll(final Collection<?> c) {
			return CompactSet.containsAll(this, c);
		}

		@Override
		public boolean addAll(final Collection<? extends E> c) {
			return CompactSet.addAll(this, c);
		}

		@Override
		public boolean removeAll(final Collection<?> c) {
			return CompactSet.removeAll(this, c);
		}

		@Override
		public boolean retainAll(final Collection<?> c) {
			return CompactSet.retainAll(this, c);
		}

		@Override
		public void clear() {
			this.a.clear();
			this.b.clear();
		}

	}

	public static final class PairSet<E, T extends Set<E>> extends absPairColle<E, Set<E>> implements Set<E> {

		Set<E> set() {
			return new AbstractSet<E>() {

				@Override
				public Iterator<E> iterator() {
					return PairSet.this.iterator();
				}

				@Override
				public int size() {
					return PairSet.this.size();
				}

			};
		}

		@Override
		public boolean add(final E e) {
			if(this.contains(e)) return false;
			if(this.a.size() < this.b.size()) return this.a.add(e);
			return this.b.add(e);
		}

		@Override
		public boolean remove(final Object o) {
			return this.a.remove(o) || this.b.remove(o);
		}

		@Override
		public int hashCode() {
			return set().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return set().equals(obj);
		}

		@Override
		public String toString() {
			return set().toString();
		}

	}

	public static final class PairList<E> extends absPairColle<E, List<E>> implements List<E> {

		List<E> set() {
			return new AbstractList<E>() {

				@Override
				public int size() {
					return PairList.this.size();
				}

				@Override
				public E get(int index) {
					return PairList.this.get(index);
				}

			};
		}

		@Override
		public E get(int index) {
			return null;
		}

		@Override
		public E set(int index, E element) {
			return null;
		}

		@Override
		public boolean add(E e) {
			return false;
		}

		@Override
		public void add(int index, E element) {
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			return false;
		}

		@Override
		public E remove(int index) {
			return null;
		}

		@Override
		public boolean remove(Object o) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(Object o) {
			int index = a.indexOf(o);
			if(index < 0) return b.indexOf(o);
			return index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(Object o) {
			int index = b.lastIndexOf(o);
			if(index < 0) return a.lastIndexOf(o);
			return index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<E> listIterator() {
			return set().listIterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<E> listIterator(int index) {
			return set().listIterator(index);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			return null;
		}

		@Override
		public int hashCode() {
			return set().hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return set().equals(obj);
		}

		@Override
		public String toString() {
			return set().toString();
		}

	}

}
