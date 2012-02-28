package bee.creative.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class Pairs {

	static class absPair<T> {

		T a;

		T b;

	}

	static abstract class absPairColle<E, T extends Collection<E>> extends AbstractCollection<E> {

		T a;

		T b;

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
			return this.set().hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return this.set().equals(obj);
		}

		@Override
		public String toString() {
			return this.set().toString();
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
				public E get(final int index) {
					return PairList.this.get(index);
				}

			};
		}

		@Override
		public E get(final int index) {
			return null;
		}

		@Override
		public E set(final int index, final E element) {
			return null;
		}

		@Override
		public boolean add(final E e) {
			return false;
		}

		@Override
		public void add(final int index, final E element) {
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends E> c) {
			return false;
		}

		@Override
		public E remove(final int index) {
			return null;
		}

		@Override
		public boolean remove(final Object o) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int indexOf(final Object o) {
			final int index = this.a.indexOf(o);
			if(index < 0) return this.b.indexOf(o);
			return index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int lastIndexOf(final Object o) {
			final int index = this.b.lastIndexOf(o);
			if(index < 0) return this.a.lastIndexOf(o);
			return index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<E> listIterator() {
			return this.set().listIterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public ListIterator<E> listIterator(final int index) {
			return this.set().listIterator(index);
		}

		@Override
		public List<E> subList(final int fromIndex, final int toIndex) {
			return null;
		}

		@Override
		public int hashCode() {
			return this.set().hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			return this.set().equals(obj);
		}

		@Override
		public String toString() {
			return this.set().toString();
		}

	}

}
