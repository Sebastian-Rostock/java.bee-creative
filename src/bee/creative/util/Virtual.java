package bee.creative.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class Virtual {

	static class AbstractVirtualSet<E> extends AbstractSet<E> implements Set<E> {

		protected static final class InputIterator<E> implements Iterator<E>, Filter<E> {

			Set<?> set;

			E value;

			Iterator<E> iterator;

			public InputIterator(final Set<E> input, Set<?> remove) {
				this.set = remove;
				this.iterator = Iterators.filteredIterator(this, input.iterator());
			}

			@Override
			public boolean accept(final E input) {
				return !this.set.contains(input);
			}

			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}

			@Override
			public E next() {
				return this.value = this.iterator.next();
			}

			@Override
			public void remove() {
				this.set.remove(this.value);
			}

		}

		protected final Set<E> input;

		protected final Set<E> append;

		protected final Set<Object> remove;

		public AbstractVirtualSet(final Set<E> input, final Set<E> append, final Set<Object> remove) {
			super();
			this.input = input;
			this.append = append;
			this.remove = remove;
		}

		@Override
		public int size() {
			return (this.input.size() + this.append.size()) - this.remove.size();
		}

		@Override
		public boolean contains(final Object o) {
			if(this.append.contains(o)) return true;
			if(this.remove.contains(o)) return false;
			return this.input.contains(o);
		}

		@Override
		public Iterator<E> iterator() {
			return Iterators.chainedIterator(this.append.iterator(), new InputIterator<E>(input, remove));
		}

		@Override
		public boolean add(final E e) {
			if(this.remove.contains(e)) return this.remove.remove(e);
			if(this.input.contains(e)) return false;
			return this.append.add(e);
		}

		@Override
		public boolean remove(final Object o) {
			if(this.append.contains(o)) return this.append.remove(o);
			if(!this.input.contains(o)) return false;
			return this.remove.add(o);
		}

		@Override
		public void clear() {
			this.append.clear();
			this.remove.clear();
			this.remove.addAll(this.input);
		}

	}

}
