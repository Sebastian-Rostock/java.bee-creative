package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Iterators.emptyIterator;
import static bee.creative.util.Iterators.iteratorFrom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.lang.Array2;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Iterable}.
 *
 * @see Iterators
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterables {

	/** Diese Methode liefert den gegebenen {@link Iterable} als {@link Iterable3}. Wenn er {@code null} ist, wird {@link #emptyIterable()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <T> Iterable3<T> iterableFrom(Iterable<? extends T> that) {
		if (that == null) return emptyIterable();
		if (that instanceof Iterable3<?>) return (Iterable3<T>)that;
		return () -> iteratorFrom(that.iterator());
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromItem(Object, int) Iterables.fromItem(item, 1)}. */
	public static <T> Iterable3<T> fromItem(T item) {
		return Iterables.fromItem(item, 1);
	}

	/** Diese Methode ist eine Abkürzung für {@link UniformIterable new UniformIterable<>(item, count)}. */
	public static <T> Iterable3<T> fromItem(T item, int count) throws IllegalArgumentException {
		return new UniformIterable<>(item, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link #iterableFromArray(Object[], int, int) iterableFromArray(items, 0, items.length)}. */
	@SafeVarargs
	public static <T> Iterable3<T> iterableFromArray(T... items) throws NullPointerException {
		return iterableFromArray(items, 0, items.length);
	}

	/** Diese Methode ist eine Abkürzung für {@link ArrayIterable new ArrayIterable<>(items, fromIndex, toIndex)}. */
	public static <T> Iterable3<T> iterableFromArray(T[] items, int fromIndex, int toIndex) throws NullPointerException, IllegalArgumentException {
		return new ArrayIterable<>(items, fromIndex, toIndex);
	}

	/** Diese Methode ist eine Abkürzung für {@link CountIterable new CountIterable<>(count)}. */
	public static Iterable3<Integer> iterableFromCount(int count) throws IllegalArgumentException {
		return new CountIterable(count);
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#emptyIterator()}. */
	@SuppressWarnings ("unchecked")
	public static <T> Iterable3<T> emptyIterable() {
		return (Iterable3<T>)emptyIterable;
	}

	private static final Iterable<?> emptyIterable = () -> emptyIterator();

	/** Diese Methode gibt die Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente zurück. */
	public static int size(Iterable<?> that) throws NullPointerException {
		if (that instanceof Collection<?>) return ((Collection<?>)that).size();
		if (that instanceof Array2<?>) return ((Array2<?>)that).size();
		if (that instanceof bee.creative.array.Array<?, ?>) return ((bee.creative.array.Array<?, ?>)that).size();
		return Iterators.size(that.iterator());
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterable} in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück. */
	public static <T> boolean addAll(Collection<T> target, Iterable<? extends T> source) throws NullPointerException {
		if (source instanceof Collection<?>) return target.addAll((Collection<? extends T>)source);
		return Iterators.addAll(target, source.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung
	 * des {@link Iterable} {@code true} zurück. */
	public static boolean retainAll(Iterable<?> target, Collection<?> filter) throws NullPointerException {
		if (target instanceof Collection<?>) return ((Collection<?>)target).retainAll(filter);
		return Iterators.iteratorRetainAll(target.iterator(), Objects.notNull(filter));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, und gibt nur bei Veränderung der
	 * {@link Collection} {@code true} zurück. */
	public static boolean retainAll(Collection<?> target, Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.retainAll((Collection<?>)filter);
		return Iterators.retainAll(target, filter.iterator());
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, fügt alle Elemetne des
	 * {@link Iterable} in die {@link Collection} ein und gibt nur bei Veränderung der {@link Collection} {@code true} zurück. */
	public static <T> boolean replaceAll(Collection<T> target, Iterable<? extends T> source) throws NullPointerException {
		var buffer = Iterables.iterableToSet(source);
		return target.retainAll(buffer) | target.addAll(buffer);
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} und gibt nur bei Veränderung des {@link Iterable} {@code true} zurück. */
	public static boolean removeAll(Iterable<?> target) throws NullPointerException {
		return Iterators.iteratorRemoveAll(target.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des
	 * {@link Iterable} {@code true} zurück. */
	public static boolean removeAll(Iterable<?> target, Collection<?> filter) throws NullPointerException {
		if (target instanceof Collection<?>) return ((Collection<?>)target).removeAll(filter);
		return Iterators.removeAll(target.iterator(), filter);
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} aus der gegebenen {@link Collection} und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück. */
	public static boolean removeAll(Collection<?> target, Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.removeAll((Collection<?>)filter);
		return target.removeAll(Iterables.iterableToSet(filter));
	}

	/** Diese Methode liefert nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterable} in der gegebenen {@link Collection} enthalten
	 * sind. */
	public static boolean containsAll(Collection<?> target, Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.containsAll((Collection<?>)filter);
		return Iterators.containsAll(target, filter.iterator());
	}

	/** Diese Methode übergibt alle Elemente des gegebene {@link Iterable} an den gegebenen {@link Consumer}. */
	public static <T> void collectAll(Iterable<? extends T> source, Consumer<? super T> target) throws NullPointerException {
		Iterators.forEachRemaining(source.iterator(), target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #concatAll(Iterable) Iterables.concatAll(Arrays.asList(iter1, iter2))}. */
	public static <T> Iterable3<T> concat(Iterable<? extends T> iter1, Iterable<? extends T> iter2) throws NullPointerException {
		return Iterables.concatAll(Arrays.asList(Objects.notNull(iter1), Objects.notNull(iter2)));
	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#concatAllIterator(Iterator)}.
	 *
	 * @param <T> Typ der Elemente. */
	public static class ConcatIterable<T> extends AbstractIterable<T> {

		public final Iterable<? extends Iterable<? extends T>> that;

		public ConcatIterable(Iterable<? extends Iterable<? extends T>> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.concatAllIterator(Iterators.translatedIterator(this.that.iterator(), Iterables.<T>iterableIterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatIterable new ConcatIterable<>(iters)}. */
	public static <T> Iterable3<T> concatAll(Iterable<? extends Iterable<? extends T>> iters) throws NullPointerException {
		return new ConcatIterable<>(iters);
	}

	/** Diese Methode ist eine Abkürzung für {@link #unionAll(Comparator, Iterable) Iterables.unionAll(order, Arrays.asList(iter1, iter2))}. */
	public static <T> Iterable3<T> union(Comparator<? super T> order, Iterable<? extends T> iter1, Iterable<? extends T> iter2) throws NullPointerException {
		return Iterables.unionAll(order, Arrays.asList(Objects.notNull(iter1), Objects.notNull(iter2)));
	}

	/** Diese Methode ist eine Abkürzung für {@link UnionIterable new UnionIterable<>(order, iters)}. */
	public static <T> Iterable3<T> unionAll(Comparator<? super T> order, Iterable<? extends Iterable<? extends T>> iters) throws NullPointerException {
		return new UnionIterable<>(order, iters);
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptIterable new ExceptIterable<>(order, iter1, iter2)}. */
	public static <T> Iterable3<T> except(Comparator<? super T> order, Iterable<? extends T> iter1, Iterable<? extends T> iter2) throws NullPointerException {
		return new ExceptIterable<>(order, iter1, iter2);
	}

	/** Diese Methode ist eine Abkürzung für {@link #intersectAll(Comparator, Iterable) Iterables.intersectAll(order, Arrays.asList(iter1, iter2))}. */
	public static <T> Iterable3<T> intersect(Comparator<? super T> order, Iterable<? extends T> iter1, Iterable<? extends T> iter2) throws NullPointerException {
		return Iterables.intersectAll(order, Arrays.asList(Objects.notNull(iter1), Objects.notNull(iter2)));
	}

	/** Diese Methode ist eine Abkürzung für {@link IntersectIterable new IntersectIterable<>(order, iters)}. */
	public static <T> Iterable3<T> intersectAll(Comparator<? super T> order, Iterable<? extends Iterable<? extends T>> iters) throws NullPointerException {
		return new IntersectIterable<>(order, iters);
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#limitedIterator(Iterator, int)}. */
	public static <T> Iterable3<T> limit(Iterable<? extends T> that, int count) throws NullPointerException, IllegalArgumentException {
		notNull(that);
		if (count < 0) throw new IllegalArgumentException();
		return () -> Iterators.limitedIterator(that.iterator(), count);
	}

	/** Diese Methode ist eine Abkürzung für {@link FilteredIterable new FilteredIterable<>(that, filter)}. */
	public static <T> Iterable3<T> filter(Iterable<? extends T> that, Filter<? super T> filter) throws NullPointerException {
		return new FilteredIterable<>(that, filter);
	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#filteredIterator(Iterator, Filter)}.
	 *
	 * @param <T> Typ der Elemente. */
	public static class FilteredIterable<T> extends AbstractIterable<T> {

		public final Iterable<? extends T> that;

		public final Filter<? super T> filter;

		public FilteredIterable(Iterable<? extends T> that, Filter<? super T> filter) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.filter = Objects.notNull(filter);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.filteredIterator(this.that.iterator(), this.filter);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.filter);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concatAll(Iterable) Iterables.concatAll(Iterables.fromItem(that, count))} und liefert ein
	 * {@link Iterable3}, welches die gegebene Anzahl Mal über die Elemente des gegebenen {@link Iterable} iteriert. */
	public static <T> Iterable3<T> repeat(Iterable<? extends T> that, int count) throws NullPointerException, IllegalArgumentException {
		return Iterables.concatAll(Iterables.fromItem(Objects.notNull(that), count));
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#uniqueIterator(Iterator)}. */
	public static <T> Iterable3<T> unique(Iterable<? extends T> that) throws NullPointerException {
		notNull(that);
		return () -> Iterators.uniqueIterator(that.iterator());
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedIterable new TranslatedIterable<>(that, trans)}. */
	public static <GSource, GTarget> Iterable3<GTarget> translatedIterable(Iterable<? extends GSource> that, Getter<? super GSource, ? extends GTarget> trans)
		throws NullPointerException {
		return new TranslatedIterable<>(that, trans);
	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#translatedIterator(Iterator, Getter)}. */
	public static class TranslatedIterable<T, T2> extends AbstractIterable<T> {

		public final Iterable<? extends T2> that;

		public final Getter<? super T2, ? extends T> trans;

		public TranslatedIterable(Iterable<? extends T2> that, Getter<? super T2, ? extends T> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.translatedIterator(this.that.iterator(), this.trans);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Methode ist eine Abkürzung für {@link UnmodifiableIterable new UnmodifiableIterable<>(that)}. */
	public static <T> Iterable3<T> unmodifiable(Iterable<? extends T> that) throws NullPointerException {
		return new UnmodifiableIterable<>(that);
	}

	public static class UnmodifiableIterable<T> extends AbstractIterable<T> {

		public final Iterable<? extends T> that;

		public UnmodifiableIterable(Iterable<? extends T> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.unmodifiableIterator(this.that.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Iterable#iterator()}. */
	@SuppressWarnings ("unchecked")
	public static <T> Getter3<Iterable<? extends T>, Iterator<T>> iterableIterator() {
		return (Getter3<Iterable<? extends T>, Iterator<T>>)iterableIterator;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link Set}. Wenn das Iterable ein {@link Set} ist, wird dieses geliefert.
	 * Andernfalls wird ein über {@link #addAll(Collection, Iterable)} befülltes {@link HashSet2} geliefert. */
	public static <T> Set<T> iterableToSet(Iterable<T> source) throws NullPointerException {
		if (source instanceof Set<?>) return (Set<T>)source;
		var result = new HashSet2<T>();
		Iterables.addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link List}. Wenn das Iterable eine {@link List} ist, wird diese geliefert.
	 * Andernfalls wird eine über {@link #addAll(Collection, Iterable)} befüllte {@link ArrayList} geliefert. */
	public static <T> List<T> toList(Iterable<T> source) throws NullPointerException {
		if (source instanceof List<?>) return (List<T>)source;
		var result = new ArrayList<T>();
		Iterables.addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als Array. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray() geliefert} wird. */
	public static Object[] toArray(Iterable<?> source) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<?>)source : Iterables.toList(source)).toArray();
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als Array. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray(Object[]) geliefert} wird. */
	public static <T> T[] toArray(Iterable<? extends T> source, T[] array) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<? extends T>)source : Iterables.toList(source)).toArray(array);
	}

	/** Diese Klasse implementiert einen {@link Iterable3}, der die Elemente eines Abschnitts eines gegebenen Array liefert.
	 *
	 * @param <T> Typ der Elemente. */
	public static class ArrayIterable<T> extends AbstractIterable<T> {

		public ArrayIterable(T[] items, int fromIndex, int toIndex) throws NullPointerException, IllegalArgumentException {
			if (fromIndex > toIndex) throw new IllegalArgumentException("fromIndex > toIndex");
			this.items = Objects.notNull(items);
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.iteratorFromArray(this.items, this.fromIndex, this.toIndex);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.items, this.fromIndex, this.toIndex);
		}

		private final T[] items;

		private final int fromIndex;

		private final int toIndex;

	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#iteratorFromCount(int)}. */
	public static class CountIterable extends AbstractIterable<Integer> {

		public final int count;

		public CountIterable(int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		@Override
		public Iterator3<Integer> iterator() {
			return Iterators.iteratorFromCount(this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#iteratorFromItem(Object, int)}.
	 *
	 * @param <T> Typ der Elemente. */
	public static class UniformIterable<T> extends AbstractIterable<T> {

		public final T item;

		public final int count;

		public UniformIterable(T item, int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.item = item;
			this.count = count;
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.iteratorFromItem(this.item, this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.count);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#unionAll(Comparator, Iterator)}.
	 *
	 * @param <T> Typ der Elemente. */
	public static class UnionIterable<T> extends AbstractIterable<T> {

		public final Comparator<? super T> order;

		public final Iterable<? extends Iterable<? extends T>> iters;

		public UnionIterable(Comparator<? super T> order, Iterable<? extends Iterable<? extends T>> iters) throws NullPointerException {
			this.order = Objects.notNull(order);
			this.iters = Objects.notNull(iters);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.unionAll(this.order, Iterators.translatedIterator(this.iters.iterator(), Iterables.<T>iterableIterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iters);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#except(Comparator, Iterator, Iterator)}.
	 *
	 * @param <T> Typ der Elemente. */
	public static class ExceptIterable<T> extends AbstractIterable<T> {

		public final Comparator<? super T> order;

		public final Iterable<? extends T> iter1;

		public final Iterable<? extends T> iter2;

		public ExceptIterable(Comparator<? super T> order, Iterable<? extends T> iter1, Iterable<? extends T> iter2) throws NullPointerException {
			this.order = Objects.notNull(order);
			this.iter1 = Objects.notNull(iter1);
			this.iter2 = Objects.notNull(iter2);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.except(this.order, this.iter1.iterator(), this.iter2.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable3} zu {@link Iterators#intersectAll(Comparator, Iterator)}.
	 *
	 * @param <T> Typ der Elemente. */
	public static class IntersectIterable<T> extends AbstractIterable<T> {

		public final Comparator<? super T> order;

		public final Iterable<? extends Iterable<? extends T>> iters;

		public IntersectIterable(Comparator<? super T> order, Iterable<? extends Iterable<? extends T>> iters) throws NullPointerException {
			this.order = Objects.notNull(order);
			this.iters = Objects.notNull(iters);
		}

		@Override
		public Iterator3<T> iterator() {
			return Iterators.intersectAll(this.order, Iterators.translatedIterator(this.iters.iterator(), Iterables.<T>iterableIterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iters);
		}

	}

	private static final Getter<? extends Iterable<?>, ?> iterableIterator = Iterable::iterator;

}
