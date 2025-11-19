package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Iterators.emptyIterator;
import static bee.creative.util.Iterators.iteratorFrom;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import bee.creative.lang.Objects;

public class Iterators2 {

	/** Diese Methode ist eine Abkürzung für {@link UnionIterator new UnionIterator<>(order, iter1, iter2)}. */
	public static <T> Iterator3<T> union(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
		throws NullPointerException {
		return new UnionIterator<>(order, iter1, iter2);
	}

	/** Diese Methode liefert einen {@link Iterator3}, welcher die aufsteigend geordnete Vereinigung der Elemente der gegebenen Iteratoren liefert und welcher das
	 * Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @see #union(Comparator, Iterator, Iterator) */
	public static <T> Iterator3<T> unionAll(final Comparator<? super T> order, final Iterator<? extends Iterator<? extends T>> iters)
		throws NullPointerException {
		if (!iters.hasNext()) return emptyIterator();
		Iterator3<T> result = iteratorFrom(iters.next());
		while (iters.hasNext()) {
			result = union(order, result, iters.next());
		}
		return result;
	}

	/** Diese Klasse implementiert einen {@link Iterator3}, der die aufsteigend geordnete Vereinigung der Elemente zweier gegebener Iteratoren liefert und welcher
	 * das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen
	 * Ordnung liefern.
	 *
	 * @param <T> Typ der Elemente. */
	
	public static class UnionIterator<T> implements Iterator3<T> {
	
		public final Comparator<? super T> order;
	
		public final Iterator<? extends T> iter1;
	
		public final Iterator<? extends T> iter2;
	
		protected T item1;
	
		protected T item2;
	
		public UnionIterator(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2) throws NullPointerException {
			this.order = notNull(order);
			this.iter1 = iter1;
			this.iter2 = iter2;
			this.item1 = this.next1();
			this.item2 = this.next2();
		}
	
		@Override
		public boolean hasNext() {
			return (this.item1 != null) || (this.item2 != null);
		}
	
		@Override
		public T next() {
			final T item1 = this.item1, item2 = this.item2;
			if (item1 == null) {
				if (item2 == null) throw new NoSuchElementException();
				this.item2 = this.next2();
				return item2;
			}
			if (item2 == null) {
				if (item1 == null) throw new NoSuchElementException();
				this.item1 = this.next1();
				return item1;
			}
			final var order = this.order.compare(item1, item2);
			if (order < 0) {
				this.item1 = this.next1();
				return item1;
			}
			if (order > 0) {
				this.item2 = this.next2();
				return item2;
			}
			this.item1 = this.next1();
			this.item2 = this.next2();
			return item1;
		}
	
		T next1() {
			return this.iter1.hasNext() ? this.iter1.next() : null;
		}
	
		T next2() {
			return this.iter2.hasNext() ? this.iter2.next() : null;
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}
	
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptIterator new ExceptIterator<>(order, iter1, iter2)}. */
	public static <T> Iterator3<T> except(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
		throws NullPointerException {
		return new ExceptIterator<>(order, iter1, iter2);
	}

	/** Diese Klasse implementiert einen {@link Iterator3}, der aufsteigend geordnete die Elemente eines ersten gegebenen Iterators ohne denen eines zweiten
	 * gegebenen Iterators liefert und welcher das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu
	 * aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @param <T> Typ der Elemente. */
	
	public static class ExceptIterator<T> implements Iterator3<T> {
	
		public final Comparator<? super T> order;
	
		public final Iterator<? extends T> iter1;
	
		public final Iterator<? extends T> iter2;
	
		protected T item1;
	
		protected T item2;
	
		public ExceptIterator(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2) throws NullPointerException {
			this.order = notNull(order);
			this.iter1 = iter1;
			this.iter2 = iter2;
			this.item2 = this.next2();
			this.item1 = this.next1();
		}
	
		@Override
		public boolean hasNext() {
			return this.item1 != null;
		}
	
		@Override
		public T next() {
			final var item = this.item1;
			if (item == null) throw new NoSuchElementException();
			this.item1 = this.next1();
			return item;
		}
	
		T next1() {
			while (this.iter1.hasNext()) {
				final T item1 = this.iter1.next();
				if (this.item2 == null) return item1;
				final var order = this.order.compare(item1, this.item2);
				if (order < 0) return item1;
				if (order == 0) {
					this.item2 = this.next2();
				}
			}
			return null;
		}
	
		T next2() {
			return this.iter2.hasNext() ? this.iter2.next() : null;
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}
	
	}

	/** Diese Methode liefert einen {@link Iterator3}, der aufsteigend geordnete nur die Elemente liefert, die von den beiden gegebenen Iteratoren geliefert
	 * werden und der das Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu bezüglich der gegebenen Ordnung
	 * aufsteigend geordnet liefern. */
	public static <T> Iterator3<T> intersect(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
		throws NullPointerException {
		return new IntersectIterator<>(order, iter1, iter2);
	}

	/** Diese Klasse implementiert einen {@link Iterator3}, der aufsteigend geordnete nur die Elemente liefert, die von beiden gegebenen Iteratoren geliefert
	 * werden und und welcher das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend
	 * bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @param <T> Typ der Elemente. */
	
	public static class IntersectIterator<T> implements Iterator3<T> {
	
		public final Comparator<? super T> order;
	
		public final Iterator<? extends T> iter1;
	
		public final Iterator<? extends T> iter2;
	
		protected T item;
	
		public IntersectIterator(final Comparator<? super T> order, final Iterator<? extends T> iter1, final Iterator<? extends T> iter2)
			throws NullPointerException {
			this.iter1 = iter1;
			this.iter2 = notNull(iter2);
			this.order = notNull(order);
			this.item = this.next0();
		}
	
		@Override
		public boolean hasNext() {
			return this.item != null;
		}
	
		@Override
		public T next() {
			final var item = this.item;
			if (item == null) throw new NoSuchElementException();
			this.item = this.next0();
			return item;
		}
	
		T next0() {
			if (!this.iter1.hasNext()) return null;
			T item1 = this.iter1.next();
			if (!this.iter2.hasNext()) return null;
			T item2 = this.iter2.next();
			while (true) {
				final var order = this.order.compare(item1, item2);
				if (order == 0) return item1;
				if (order < 0) {
					if (!this.iter1.hasNext()) return null;
					item1 = this.iter1.next();
				} else {
					if (!this.iter2.hasNext()) return null;
					item2 = this.iter2.next();
				}
			}
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}
	
	}

	/** Diese Methode liefert einen {@link Iterator3}, welcher den aufsteigend geordneten Schnitt der Elemente der gegebenen Iteratoren liefert und welcher das
	 * Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @see #intersect(Comparator, Iterator, Iterator) */
	public static <T> Iterator3<T> intersectAll(final Comparator<? super T> order, final Iterator<? extends Iterator<? extends T>> iters)
		throws NullPointerException {
		if (!iters.hasNext()) return emptyIterator();
		Iterator3<T> result = iteratorFrom(iters.next());
		while (iters.hasNext()) {
			result = intersect(order, result, iters.next());
		}
		return result;
	}

}
