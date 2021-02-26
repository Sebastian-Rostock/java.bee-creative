package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import bee.creative.lang.Array;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Iterator}.
 *
 * @see Iterables
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterators {

	/** Diese Klasse implementiert einen leeren {@link Iterator2}, der keine Elemente liefert. */
	@SuppressWarnings ("javadoc")
	public static class EmptyIterator extends AbstractIterator<Object> {

		public static final Iterator<?> INSTANCE = new EmptyIterator();

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der die Elemente eines Abschnitts von gegebenen {@link Array} liefert.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ItemsIterator<GItem> extends AbstractIterator<GItem> {

		public final Array<? extends GItem> that;

		public final int fromIndex;

		public final int toIndex;

		protected int index;

		public ItemsIterator(final Array<? extends GItem> that, final int fromIndex, final int toIndex) throws NullPointerException, IllegalArgumentException {
			Comparables.check(fromIndex, toIndex);
			this.that = Objects.notNull(that);
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.index = fromIndex;
		}

		@Override
		public boolean hasNext() {
			return this.index < this.toIndex;
		}

		@Override
		public GItem next() {
			if (this.index >= this.toIndex) throw new NoSuchElementException();
			return this.that.get(this.index++);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.fromIndex, this.toIndex);
		}

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der eine gegebene Anzahl an {@link Integer Zahlen} beginnend mit {@code 0} liefert, d.h {@code 0},
	 * {@code 1}, ..., {@code this.count-1}. */
	@SuppressWarnings ("javadoc")
	public static class CountIterator extends AbstractIterator<Integer> {

		public final int count;

		protected int item;

		public CountIterator(final int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		@Override
		public boolean hasNext() {
			return this.item < this.count;
		}

		@Override
		public Integer next() {
			return Integer.valueOf(this.item++);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count);
		}

	}

	/** Diese Klasse implementiert einen verkettenden {@link Iterator2}, der alle Elemente der gegebenen Iteratoren in der gegebenen Reihenfolge liefert. Diese
	 * Iteratoren dürfen dabei {@code null} sein.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ConcatIterator<GItem> extends AbstractIterator<GItem> {

		public final Iterator<? extends Iterator<? extends GItem>> that;

		protected Iterator<? extends GItem> iter;

		public ConcatIterator(final Iterator<? extends Iterator<? extends GItem>> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean hasNext() {
			while (true) {
				while (this.iter == null) {
					if (!this.that.hasNext()) return false;
					this.iter = this.that.next();
				}
				if (this.iter.hasNext()) return true;
				this.iter = null;
			}
		}

		@Override
		public GItem next() {
			if (this.iter == null) throw new NoSuchElementException();
			return this.iter.next();
		}

		@Override
		public void remove() {
			if (this.iter == null) throw new IllegalStateException();
			this.iter.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der kein Element eines gegebenen {@link Iterator} mehrfach liefert. Zur Duplikaterkennung werden die
	 * gelieferten Elemente in einer gegebenen {@link Collection} gepuffert.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UniqueIterator<GItem> extends AbstractIterator<GItem> {

		public final Iterator<? extends GItem> that;

		public final Collection<? super GItem> buffer;

		final Iterator<? extends GItem> helper;

		public UniqueIterator(final Iterator<? extends GItem> that, final Collection<? super GItem> buffer) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.buffer = Objects.notNull(buffer);
			this.helper = Iterators.filter(that, Filters.negate(Filters.fromItems(buffer)));
		}

		@Override
		public boolean hasNext() {
			return this.helper.hasNext();
		}

		@Override
		public GItem next() {
			final GItem item = this.helper.next();
			this.buffer.add(item);
			return item;
		}

		@Override
		public void remove() {
			this.helper.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.buffer);
		}

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der ein gegebenes Element eine gegebene Anzahl mal liefert.
	 *
	 * @param <GItem> Typ des Elements. */
	@SuppressWarnings ("javadoc")
	public static class UniformIterator<GItem> extends AbstractIterator<GItem> {

		public final GItem item;

		public int count;

		public UniformIterator(final GItem item, final int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.item = item;
			this.count = count;
		}

		@Override
		public boolean hasNext() {
			return this.count > 0;
		}

		@Override
		public GItem next() {
			if (!this.hasNext()) throw new NoSuchElementException();
			this.count--;
			return this.item;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count, this.item);
		}

	}

	/** Diese Klasse implementiert einen begrenzenden {@link Iterator2}, der die Elemente eines gegebenen {@link Iterator} bis zu einer gegebenen maximalen Anzahl
	 * liefert.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class LimitedIterator<GItem> extends AbstractIterator<GItem> {

		public final Iterator<? extends GItem> that;

		public int limit;

		public LimitedIterator(final Iterator<? extends GItem> that, final int limit) throws NullPointerException, IllegalArgumentException {
			this.that = Objects.notNull(that);
			this.limit = limit;
			if (limit < 0) throw new IllegalArgumentException();
		}

		@Override
		public boolean hasNext() {
			return (this.limit > 0) && this.that.hasNext();
		}

		@Override
		public GItem next() {
			if (this.limit <= 0) throw new NoSuchElementException();
			this.limit--;
			return this.that.next();
		}

		@Override
		public void remove() {
			this.that.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.limit);
		}

	}

	/** Diese Klasse implementiert einen filternden {@link Iterator2}, der nur die von einem gegebenen {@link Filter} akzeptierten Elemente des gegebenen
	 * {@link Iterator} liefert.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class FilteredIterator<GItem> extends AbstractIterator<GItem> {

		public final Iterator<? extends GItem> that;

		public final Filter<? super GItem> filter;

		protected Boolean has;

		protected GItem next;

		public FilteredIterator(final Iterator<? extends GItem> that, final Filter<? super GItem> filter) {
			this.that = Objects.notNull(that);
			this.filter = Objects.notNull(filter);
		}

		@Override
		public boolean hasNext() {
			if (this.has != null) return this.has.booleanValue();
			while (this.that.hasNext()) {
				if (this.filter.accept(this.next = this.that.next())) return this.has = Boolean.TRUE;
			}
			return this.has = Boolean.FALSE;
		}

		@Override
		public GItem next() {
			if (!this.hasNext()) throw new NoSuchElementException();
			this.has = null;
			return this.next;
		}

		@Override
		public void remove() {
			if (this.has != null) throw new IllegalStateException();
			this.that.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.filter);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Iterator2}, der die über einen gegebenen {@link Getter} umgewandelte Elemente eines gegebenen
	 * {@link Iterator} liefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param <GItem2> Typ der Elemente des gegebenen {@link Iterator}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedIterator<GItem, GItem2> extends AbstractIterator<GItem> {

		public final Iterator<? extends GItem2> that;

		public final Getter<? super GItem2, ? extends GItem> trans;

		public TranslatedIterator(final Iterator<? extends GItem2> that, final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean hasNext() {
			return this.that.hasNext();
		}

		@Override
		public GItem next() {
			return this.trans.get(this.that.next());
		}

		@Override
		public void remove() {
			this.that.remove();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen unveränderlichen {@link Iterator2}, der beim {@link #remove() Entfernen} stets eine {@link UnsupportedOperationException}
	 * auslöst.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UnmodifiableIterator<GItem> extends AbstractIterator<GItem> {

		public final Iterator<? extends GItem> that;

		public UnmodifiableIterator(final Iterator<? extends GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean hasNext() {
			return this.that.hasNext();
		}

		@Override
		public GItem next() {
			return this.that.next();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der die aufsteigend geordnete Vereinigung der Elemente zweier gegebener Iteratoren liefert und welcher
	 * das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen
	 * Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UnionIterator<GItem> extends AbstractIterator<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterator<? extends GItem> iter1;

		public final Iterator<? extends GItem> iter2;

		protected GItem item1;

		protected GItem item2;

		public UnionIterator(final Comparator<? super GItem> order, final Iterator<? extends GItem> iter1, final Iterator<? extends GItem> iter2)
			throws NullPointerException {
			this.order = Objects.notNull(order);
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
		public GItem next() {
			final GItem item1 = this.item1, item2 = this.item2;
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
			final int order = this.order.compare(item1, item2);
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

		GItem next1() {
			return this.iter1.hasNext() ? this.iter1.next() : null;
		}

		GItem next2() {
			return this.iter2.hasNext() ? this.iter2.next() : null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der aufsteigend geordnete die Elemente eines ersten gegebenen Iterators ohne denen eines zweiten
	 * gegebenen Iterators liefert und welcher das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu
	 * aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ExceptIterator<GItem> extends AbstractIterator<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterator<? extends GItem> iter1;

		public final Iterator<? extends GItem> iter2;

		protected GItem item1;

		protected GItem item2;

		public ExceptIterator(final Comparator<? super GItem> order, final Iterator<? extends GItem> iter1, final Iterator<? extends GItem> iter2)
			throws NullPointerException {
			this.order = Objects.notNull(order);
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
		public GItem next() {
			final GItem item = this.item1;
			if (item == null) throw new NoSuchElementException();
			this.item1 = this.next1();
			return item;
		}

		GItem next1() {
			while (this.iter1.hasNext()) {
				final GItem item1 = this.iter1.next();
				if (this.item2 == null) return item1;
				final int order = this.order.compare(item1, this.item2);
				if (order < 0) return item1;
				if (order == 0) {
					this.item2 = this.next2();
				}
			}
			return null;
		}

		GItem next2() {
			return this.iter2.hasNext() ? this.iter2.next() : null;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Klasse implementiert einen {@link Iterator2}, der aufsteigend geordnete nur die Elemente liefert, die von beiden gegebenen Iteratoren geliefert
	 * werden und und welcher das {@link #remove() Entfernen} von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend
	 * bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class IntersectIterator<GItem> extends AbstractIterator<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterator<? extends GItem> iter1;

		public final Iterator<? extends GItem> iter2;

		protected GItem item;

		public IntersectIterator(final Comparator<? super GItem> order, final Iterator<? extends GItem> iter1, final Iterator<? extends GItem> iter2)
			throws NullPointerException {
			this.iter1 = iter1;
			this.iter2 = Objects.notNull(iter2);
			this.order = Objects.notNull(order);
			this.item = this.next0();
		}

		@Override
		public boolean hasNext() {
			return this.item != null;
		}

		@Override
		public GItem next() {
			final GItem item = this.item;
			if (item == null) throw new NoSuchElementException();
			this.item = this.next0();
			return item;
		}

		GItem next0() {
			if (!this.iter1.hasNext()) return null;
			GItem item1 = this.iter1.next();
			if (!this.iter2.hasNext()) return null;
			GItem item2 = this.iter2.next();
			while (true) {
				final int order = this.order.compare(item1, item2);
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

	/** Diese Methode liefert den {@link EmptyIterator}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterator2<GItem> empty() {
		return (Iterator2<GItem>)EmptyIterator.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Iterator} als {@link Iterator2}. Wenn er {@code null} ist, wird der {@link EmptyIterator} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterator2<GItem> from(final Iterator<? extends GItem> that) {
		if (that == null) return Iterators.empty();
		if (that instanceof Iterator2<?>) return (Iterator2<GItem>)that;
		return Iterators.translate(that, Getters.<GItem>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromItem(Object, int) Iterators.fromItem(item, 1)}. */
	public static <GItem> Iterator2<GItem> fromItem(final GItem item) {
		return Iterators.fromItem(item, 1);
	}

	/** Diese Methode ist eine Abkürzung für {@link UniformIterator new UniformIterator<>(item, count)}. */
	public static <GItem> Iterator2<GItem> fromItem(final GItem item, final int count) throws IllegalArgumentException {
		if (count == 0) return Iterators.empty();
		return new UniformIterator<>(item, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link ItemsIterator new ItemsIterator<>(items, fromIndex, toIndex)}. */
	public static <GItem> Iterator2<GItem> fromItems(final Array<? extends GItem> items, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		return new ItemsIterator<>(items, fromIndex, toIndex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Iterator) Iterators.from(Arrays.asList(items).iterator())}.
	 *
	 * @see Arrays#asList(Object...) */
	@SafeVarargs
	public static <GItem> Iterator2<GItem> fromArray(final GItem... items) throws NullPointerException {
		return Iterators.from(Arrays.asList(items).iterator());
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Iterator) Iterators.from(Arrays.asList(items).subList(fromIndex, toIndex).iterator())}.
	 *
	 * @see List#subList(int, int)
	 * @see Arrays#asList(Object...) */
	public static <GItem> Iterator2<GItem> fromArray(final GItem[] items, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		return Iterators.from(Arrays.asList(items).subList(fromIndex, toIndex).iterator());
	}

	/** Diese Methode ist eine Abkürzung für {@link CountIterator new CountIterator(count)}. */
	public static Iterator2<Integer> fromCount(final int count) throws IllegalArgumentException {
		return new CountIterator(count);
	}

	/** Diese Methode liefert das {@code index}-te Elemente des gegebenen {@link Iterator} oder löst eine {@link NoSuchElementException} aus.
	 *
	 * @see Iterators#skip(Iterator, int) */
	public static <GItem> GItem get(final Iterator<? extends GItem> iter, final int index) throws NullPointerException, NoSuchElementException {
		if ((index < 0) || (Iterators.skip(iter, index) != 0) || !iter.hasNext()) throw new NoSuchElementException();
		return iter.next();
	}

	/** Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die Anzahl der noch zu überspringenden
	 * Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene Iterator {@link Iterator#hasNext() anzeigt}, dass er keine weiteren Elemente
	 * mehr liefern kann. Wenn die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen Iterator zurück
	 * gegeben. Damit bestimmt {@code (-Iterators.skip(iter, -1) - 1)} die Anzahl der Elemente des gegebenen Iterator. */
	public static int skip(final Iterator<?> iter, int count) throws NullPointerException {
		Objects.notNull(iter);
		while ((count != 0) && iter.hasNext()) {
			count--;
			iter.next();
		}
		return count;
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterator} in die gegebene {@link Collection} ein und gibt nur dann {@code true} zurück, wenn
	 * Elemente eingefügt wurden.
	 *
	 * @see Collection#addAll(Collection) */
	public static <GItem> boolean addAll(final Collection<GItem> target, final Iterator<? extends GItem> source) throws NullPointerException {
		Objects.notNull(target);
		boolean modified = false;
		while (source.hasNext()) {
			if (target.add(source.next())) {
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur dann
	 * {@code true} zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection) */
	public static boolean retainAll(final Iterator<?> target, final Collection<?> filter) throws NullPointerException {
		return Iterators.removeAll(Iterators.filter(target, Filters.fromItems(filter).negate()));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#retainAll(Collection) */
	public static boolean retainAll(final Collection<?> target, final Iterator<?> filter) throws NullPointerException {
		final HashSet2<Object> filter2 = new HashSet2<>();
		Iterators.addAll(filter2, filter);
		return target.retainAll(filter2);
	}

	/** Diese Methode {@link Iterator#remove() entfernt} alle Elemente des gegebenen {@link Iterator} und gibt nur dann {@code true} zurück, wenn Elemente
	 * entfernt wurden.
	 *
	 * @return {@code true} bei Veränderungen am {@link Iterator}. */
	public static boolean removeAll(final Iterator<?> target) throws NullPointerException {
		if (!target.hasNext()) return false;
		do {
			target.next();
			target.remove();
		} while (target.hasNext());
		return true;
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}, die in der gegebenen {@link Collection} vorkommen, und gibt nur dann {@code true}
	 * zurück, wenn Elemente entfernt wurden.
	 *
	 * @see Collection#removeAll(Collection) */
	public static boolean removeAll(final Iterator<?> target, final Collection<?> filter) throws NullPointerException {
		return Iterators.removeAll(Iterators.filter(target, Filters.fromItems(filter)));
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterator} aus der gegebenen {@link Collection} und gibt nur dann {@code true} zurück, wenn
	 * Elemente entfernt wurden.
	 *
	 * @see Collection#removeAll(Collection)
	 * @return {@code true} bei Veränderungen an der {@link Collection}. */
	public static boolean removeAll(final Collection<?> target, final Iterator<?> filter) throws NullPointerException {
		boolean modified = false;
		while (filter.hasNext()) {
			if (target.remove(filter.next())) {
				modified = true;
			}
		}
		return modified;
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterator} in der gegebenen {@link Collection} enthalten sind.
	 *
	 * @see Collection#containsAll(Collection)
	 * @return {@code true} bei vollständiger Inklusion. */
	public static boolean containsAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		Objects.notNull(collection);
		while (iterator.hasNext()) {
			if (!collection.contains(iterator.next())) return false;
		}
		return true;
	}

	/** Diese Methode ist eine Abkürzung für {@link #concatAll(Iterator) Iterators.concatAll(Iterators.fromArray(iter1, iter2))}.
	 *
	 * @see #fromArray(Object...) */
	public static <GItem> Iterator2<GItem> concat(final Iterator<? extends GItem> iter1, final Iterator<? extends GItem> iter2) {
		return Iterators.concatAll(Iterators.fromArray(iter1, iter2));
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatIterator new ConcatIterator(that)}. */
	public static <GItem> Iterator2<GItem> concatAll(final Iterator<? extends Iterator<? extends GItem>> that) throws NullPointerException {
		return new ConcatIterator<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link UnionIterator new UnionIterator<>(order, iter1, iter2)}. */
	public static <GItem> Iterator2<GItem> union(final Comparator<? super GItem> order, final Iterator<? extends GItem> iter1,
		final Iterator<? extends GItem> iter2) throws NullPointerException {
		return new UnionIterator<>(order, iter1, iter2);
	}

	/** Diese Methode liefert einen {@link Iterator2}, welcher die aufsteigend geordnete Vereinigung der Elemente der gegebenen Iteratoren liefert und welcher das
	 * Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @see #empty()
	 * @see #union(Comparator, Iterator, Iterator) */
	public static <GItem> Iterator2<GItem> unionAll(final Comparator<? super GItem> order, final Iterator<? extends Iterator<? extends GItem>> iters)
		throws NullPointerException {
		if (!iters.hasNext()) return Iterators.empty();
		Iterator2<GItem> result = from(iters.next());
		while (iters.hasNext()) {
			result = Iterators.union(order, result, iters.next());
		}
		return result;
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptIterator new ExceptIterator<>(order, iter1, iter2)}. */
	public static <GItem> Iterator2<GItem> except(final Comparator<? super GItem> order, final Iterator<? extends GItem> iter1,
		final Iterator<? extends GItem> iter2) throws NullPointerException {
		return new ExceptIterator<>(order, iter1, iter2);
	}

	/** Diese Methode ist eine Abkürzung für {@link IntersectIterator new IntersectIterator<>(order, iter1, iter2)}. */
	public static <GItem> Iterator2<GItem> intersect(final Comparator<? super GItem> order, final Iterator<? extends GItem> iter1,
		final Iterator<? extends GItem> iter2) throws NullPointerException {
		return new IntersectIterator<>(order, iter1, iter2);
	}

	/** Diese Methode liefert einen {@link Iterator2}, welcher den aufsteigend geordneten Schnitt der Elemente der gegebenen Iteratoren liefert und welcher das
	 * Entfernen von Elementen nicht unterstützt. Die gegebenen Iteratoren müssen ihre Elemente dazu aufsteigend bezüglich einer gegebenen Ordnung liefern.
	 *
	 * @see #empty()
	 * @see #intersect(Comparator, Iterator, Iterator) */
	public static <GItem> Iterator2<GItem> intersectAll(final Comparator<? super GItem> order, final Iterator<? extends Iterator<? extends GItem>> iters)
		throws NullPointerException {
		if (!iters.hasNext()) return Iterators.empty();
		Iterator2<GItem> result = Iterators.from(iters.next());
		while (iters.hasNext()) {
			result = Iterators.intersect(order, result, iters.next());
		}
		return result;
	}

	/** Diese Methode ist eine Abkürzung für {@link LimitedIterator new LimitedIterator<>(that, count)}. */
	public static <GItem> Iterator2<GItem> limit(final Iterator<? extends GItem> that, final int count) throws NullPointerException, IllegalArgumentException {
		return new LimitedIterator<>(that, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link FilteredIterator new FilteredIterator<>(that, filter)}. */
	public static <GItem> Iterator2<GItem> filter(final Iterator<? extends GItem> that, final Filter<? super GItem> filter) throws NullPointerException {
		return new FilteredIterator<>(that, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link #unique(Iterator, Collection) Iterators.unique(that, new HashSet2<>())}. */
	public static <GItem> Iterator2<GItem> unique(final Iterator<? extends GItem> that) throws NullPointerException {
		return Iterators.unique(that, new HashSet2<>());
	}

	/** Diese Methode ist eine Abkürzung für {@link #unique(Iterator, Collection) Iterators.unique(that, HashSet2.from(hasher))}. */
	public static <GItem> Iterator2<GItem> unique(final Iterator<? extends GItem> that, Hasher hasher) throws NullPointerException {
		return Iterators.unique(that, HashSet2.from(hasher));
	}

	/** Diese Methode ist eine Abkürzung für {@link UniqueIterator new UniqueIterator<>(that, buffer)}. */
	public static <GItem> Iterator2<GItem> unique(final Iterator<? extends GItem> that, final Collection<? super GItem> buffer) throws NullPointerException {
		return new UniqueIterator<>(that, buffer);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedIterator new TranslatedIterator<>(that, trans)}. */
	public static <GItem, GItem2> Iterator2<GItem> translate(final Iterator<? extends GItem2> that, final Getter<? super GItem2, ? extends GItem> trans)
		throws NullPointerException {
		return new TranslatedIterator<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link UnmodifiableIterator new UnmodifiableIterator<>(that)}. */
	public static <GItem> Iterator2<GItem> unmodifiable(final Iterator<? extends GItem> that) throws NullPointerException {
		return new UnmodifiableIterator<>(that);
	}

}
