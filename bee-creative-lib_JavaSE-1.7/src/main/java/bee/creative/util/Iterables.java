package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.lang.Array;
import bee.creative.lang.Array2;
import bee.creative.lang.Objects;
import bee.creative.util.Iterators.UnmodifiableIterator;

/** Diese Klasse implementiert grundlegende {@link Iterable}.
 *
 * @see Iterators
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterables {

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#empty()}. */
	@SuppressWarnings ("javadoc")
	public static class EmptyIterable extends AbstractIterable<Object> {

		public static final Iterable<?> INSTANCE = new EmptyIterable();

	}

	/** Diese Klasse implementiert einen {@link Iterable2}, der die Elemente eines Abschnitts eines gegebenen {@link Array} liefert.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ArrayIterable<GItem> extends AbstractIterable<GItem> {

		public final Array<? extends GItem> items;

		public final int fromIndex;

		public final int toIndex;

		public ArrayIterable(final Array<? extends GItem> items, final int fromIndex, final int toIndex) throws NullPointerException, IllegalArgumentException {
			Comparables.check(fromIndex, toIndex);
			this.items = Objects.notNull(items);
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.fromArray(this.items, this.fromIndex, this.toIndex);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.items, this.fromIndex, this.toIndex);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#fromCount(int)}. */
	@SuppressWarnings ("javadoc")
	public static class CountIterable extends AbstractIterable<Integer> {

		public final int count;

		public CountIterable(final int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		@Override
		public Iterator2<Integer> iterator() {
			return Iterators.fromCount(this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#concatAll(Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ConcatIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends Iterable<? extends GItem>> that;

		public ConcatIterable(final Iterable<? extends Iterable<? extends GItem>> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.concatAll(Iterators.translate(this.that.iterator(), Iterables.<GItem>iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#fromItem(Object, int)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UniformIterable<GItem> extends AbstractIterable<GItem> {

		public final GItem item;

		public final int count;

		public UniformIterable(final GItem item, final int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.item = item;
			this.count = count;
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.fromItem(this.item, this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.count);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#unmodifiable(Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class LimitedIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem> that;

		public final int count;

		public LimitedIterable(final Iterable<? extends GItem> that, final int count) throws NullPointerException, IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.that = Objects.notNull(that);
			this.count = count;
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.limit(this.that.iterator(), this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.count);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#filter(Iterator, Filter)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class FilteredIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem> that;

		public final Filter<? super GItem> filter;

		public FilteredIterable(final Iterable<? extends GItem> that, final Filter<? super GItem> filter) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.filter = Objects.notNull(filter);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.filter(this.that.iterator(), this.filter);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.filter);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#unique(Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UniqueIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem> that;

		public UniqueIterable(final Iterable<? extends GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.unique(this.that.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#translate(Iterator, Getter)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedIterable<GItem, GItem2> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem2> that;

		public final Getter<? super GItem2, ? extends GItem> trans;

		public TranslatedIterable(final Iterable<? extends GItem2> that, final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.translate(this.that.iterator(), this.trans);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link UnmodifiableIterator}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UnmodifiableIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem> that;

		public UnmodifiableIterable(final Iterable<? extends GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.unmodifiable(this.that.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#unionAll(Comparator, Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UnionIterable<GItem> extends AbstractIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends Iterable<? extends GItem>> iters;

		public UnionIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iters) throws NullPointerException {
			this.order = Objects.notNull(order);
			this.iters = Objects.notNull(iters);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.unionAll(this.order, Iterators.translate(this.iters.iterator(), Iterables.<GItem>iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iters);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#except(Comparator, Iterator, Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class ExceptIterable<GItem> extends AbstractIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends GItem> iter1;

		public final Iterable<? extends GItem> iter2;

		public ExceptIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iter1, final Iterable<? extends GItem> iter2)
			throws NullPointerException {
			this.order = Objects.notNull(order);
			this.iter1 = Objects.notNull(iter1);
			this.iter2 = Objects.notNull(iter2);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.except(this.order, this.iter1.iterator(), this.iter2.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iter1, this.iter2);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#intersectAll(Comparator, Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class IntersectIterable<GItem> extends AbstractIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends Iterable<? extends GItem>> iters;

		public IntersectIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iters) throws NullPointerException {
			this.order = Objects.notNull(order);
			this.iters = Objects.notNull(iters);
		}

		@Override
		public Iterator2<GItem> iterator() {
			return Iterators.intersectAll(this.order, Iterators.translate(this.iters.iterator(), Iterables.<GItem>iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iters);
		}

	}

	static class IteratorGetter extends AbstractGetter<Iterable<?>, Iterator<?>> {

		public static final Getter<?, ?> INSTANCE = new IteratorGetter();

		@Override
		public Iterator<?> get(final Iterable<?> input) {
			return input.iterator();
		}

	}

	/** Diese Methode liefert {@link EmptyIterable EmptyIterable.INSTANCE}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable2<GItem> empty() {
		return (Iterable2<GItem>)EmptyIterable.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Iterable} als {@link Iterable2}. Wenn er {@code null} ist, wird {@link #empty() Iterables.empty()}
	 * geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable2<GItem> from(final Iterable<? extends GItem> that) {
		if (that == null) return Iterables.empty();
		if (that instanceof Iterable2<?>) return (Iterable2<GItem>)that;
		return Iterables.translate(that, Getters.<GItem>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromItem(Object, int) Iterables.fromItem(item, 1)}. */
	public static <GItem> Iterable2<GItem> fromItem(final GItem item) {
		return Iterables.fromItem(item, 1);
	}

	/** Diese Methode ist eine Abkürzung für {@link UniformIterable new UniformIterable<>(item, count)}. */
	public static <GItem> Iterable2<GItem> fromItem(final GItem item, final int count) throws IllegalArgumentException {
		return new UniformIterable<>(item, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Iterable) Iterables.from(Arrays.asList(items))}.
	 *
	 * @see Arrays#asList(Object...) */
	@SafeVarargs
	public static <GItem> Iterable2<GItem> fromArray(final GItem... items) throws NullPointerException {
		return Iterables.from(Arrays.asList(items));
	}

	/** Diese Methode ist eine Abkürzung für {@link #from(Iterable) Iterables.from(Arrays.asList(items).subList(fromIndex, toIndex))}.
	 *
	 * @see List#subList(int, int)
	 * @see Arrays#asList(Object...) */
	public static <GItem> Iterable2<GItem> fromArray(final GItem[] items, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		return Iterables.from(Arrays.asList(items).subList(fromIndex, toIndex));
	}

	/** Diese Methode ist eine Abkürzung für {@link ArrayIterable new ArrayIterable<>(items, fromIndex, toIndex)}. */
	public static <GItem> Iterable2<GItem> fromArray(final Array<? extends GItem> items, final int fromIndex, final int toIndex)
		throws NullPointerException, IllegalArgumentException {
		return new ArrayIterable<>(items, fromIndex, toIndex);
	}

	/** Diese Methode ist eine Abkürzung für {@link CountIterable new CountIterable<>(count)}. */
	public static Iterable2<Integer> fromCount(final int count) throws IllegalArgumentException {
		return new CountIterable(count);
	}

	/** Diese Methode gibt die Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente zurück. */
	public static int size(final Iterable<?> that) throws NullPointerException {
		if (that instanceof Collection<?>) return ((Collection<?>)that).size();
		if (that instanceof Array2<?>) return ((Array2<?>)that).size();
		if (that instanceof bee.creative.array.Array<?, ?>) return ((bee.creative.array.Array<?, ?>)that).size();
		return Iterators.size(that.iterator());
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterable} in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück. */
	public static <GItem> boolean addAll(final Collection<GItem> target, final Iterable<? extends GItem> source) throws NullPointerException {
		if (source instanceof Collection<?>) return target.addAll((Collection<? extends GItem>)source);
		return Iterators.addAll(target, source.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung
	 * des {@link Iterable} {@code true} zurück. */
	public static boolean retainAll(final Iterable<?> target, final Collection<?> filter) throws NullPointerException {
		if (target instanceof Collection<?>) return ((Collection<?>)target).retainAll(filter);
		return Iterators.retainAll(target.iterator(), Objects.notNull(filter));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, und gibt nur bei Veränderung der
	 * {@link Collection} {@code true} zurück. */
	public static boolean retainAll(final Collection<?> target, final Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.retainAll((Collection<?>)filter);
		return Iterators.retainAll(target, filter.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} und gibt nur bei Veränderung des {@link Iterable} {@code true} zurück. */
	public static boolean removeAll(final Iterable<?> target) throws NullPointerException {
		return Iterators.removeAll(target.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des
	 * {@link Iterable} {@code true} zurück. */
	public static boolean removeAll(final Iterable<?> target, final Collection<?> filter) throws NullPointerException {
		if (target instanceof Collection<?>) return ((Collection<?>)target).removeAll(filter);
		return Iterators.removeAll(target.iterator(), filter);
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} aus der gegebenen {@link Collection} und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück. */
	public static boolean removeAll(final Collection<?> target, final Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.removeAll((Collection<?>)filter);
		return target.removeAll(Iterables.toSet(filter));
	}

	/** Diese Methode liefert nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterable} in der gegebenen {@link Collection} enthalten
	 * sind. */
	public static boolean containsAll(final Collection<?> target, final Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.containsAll((Collection<?>)filter);
		return Iterators.containsAll(target, filter.iterator());
	}

	/** Diese Methode übergibt alle Elemente des gegebene {@link Iterable} an den gegebenen {@link Consumer}. */
	public static <GItem> void collectAll(final Iterable<? extends GItem> source, final Consumer<? super GItem> target) throws NullPointerException {
		Iterators.collectAll(source.iterator(), target);
	}

	/** Diese Methode ist eine Abkürzung für {@link #concatAll(Iterable) Iterables.concatAll(Arrays.asList(iter1, iter2))}. */
	public static <GItem> Iterable2<GItem> concat(final Iterable<? extends GItem> iter1, final Iterable<? extends GItem> iter2) throws NullPointerException {
		return Iterables.concatAll(Arrays.asList(Objects.notNull(iter1), Objects.notNull(iter2)));
	}

	/** Diese Methode ist eine Abkürzung für {@link ConcatIterable new ConcatIterable<>(iters)}. */
	public static <GItem> Iterable2<GItem> concatAll(final Iterable<? extends Iterable<? extends GItem>> iters) throws NullPointerException {
		return new ConcatIterable<>(iters);
	}

	/** Diese Methode ist eine Abkürzung für {@link #unionAll(Comparator, Iterable) Iterables.unionAll(order, Arrays.asList(iter1, iter2))}. */
	public static <GItem> Iterable2<GItem> union(final Comparator<? super GItem> order, final Iterable<? extends GItem> iter1,
		final Iterable<? extends GItem> iter2) throws NullPointerException {
		return Iterables.unionAll(order, Arrays.asList(Objects.notNull(iter1), Objects.notNull(iter2)));
	}

	/** Diese Methode ist eine Abkürzung für {@link UnionIterable new UnionIterable<>(order, iters)}. */
	public static <GItem> Iterable2<GItem> unionAll(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iters)
		throws NullPointerException {
		return new UnionIterable<>(order, iters);
	}

	/** Diese Methode ist eine Abkürzung für {@link ExceptIterable new ExceptIterable<>(order, iter1, iter2)}. */
	public static <GItem> Iterable2<GItem> except(final Comparator<? super GItem> order, final Iterable<? extends GItem> iter1,
		final Iterable<? extends GItem> iter2) throws NullPointerException {
		return new ExceptIterable<>(order, iter1, iter2);
	}

	/** Diese Methode ist eine Abkürzung für {@link #intersectAll(Comparator, Iterable) Iterables.intersectAll(order, Arrays.asList(iter1, iter2))}. */
	public static <GItem> Iterable2<GItem> intersect(final Comparator<? super GItem> order, final Iterable<? extends GItem> iter1,
		final Iterable<? extends GItem> iter2) throws NullPointerException {
		return Iterables.intersectAll(order, Arrays.asList(Objects.notNull(iter1), Objects.notNull(iter2)));
	}

	/** Diese Methode ist eine Abkürzung für {@link IntersectIterable new IntersectIterable<>(order, iters)}. */
	public static <GItem> Iterable2<GItem> intersectAll(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iters)
		throws NullPointerException {
		return new IntersectIterable<>(order, iters);
	}

	/** Diese Methode ist eine Abkürzung für {@link FilteredIterable new FilteredIterable<>(that, filter)}. */
	public static <GItem> Iterable2<GItem> limit(final Iterable<? extends GItem> that, final int count) throws NullPointerException, IllegalArgumentException {
		return new LimitedIterable<>(that, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link FilteredIterable new FilteredIterable<>(that, filter)}. */
	public static <GItem> Iterable2<GItem> filter(final Iterable<? extends GItem> that, final Filter<? super GItem> filter) throws NullPointerException {
		return new FilteredIterable<>(that, filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concatAll(Iterable) Iterables.concatAll(Iterables.fromItem(that, count))} und liefert ein
	 * {@link Iterable2}, welches die gegebene Anzahl Mal über die Elemente des gegebenen {@link Iterable} iteriert. */
	public static <GItem> Iterable2<GItem> repeat(final Iterable<? extends GItem> that, final int count) throws NullPointerException, IllegalArgumentException {
		return Iterables.concatAll(Iterables.fromItem(Objects.notNull(that), count));
	}

	/** Diese Methode ist eine Abkürzung für {@link UniqueIterable new UniqueIterable<>(that)}. */
	public static <GItem> Iterable2<GItem> unique(final Iterable<? extends GItem> that) throws NullPointerException {
		return new UniqueIterable<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedIterable new TranslatedIterable<>(that, trans)}. */
	public static <GSource, GTarget> Iterable2<GTarget> translate(final Iterable<? extends GSource> that, final Getter<? super GSource, ? extends GTarget> trans)
		throws NullPointerException {
		return new TranslatedIterable<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link UnmodifiableIterable new UnmodifiableIterable<>(that)}. */
	public static <GItem> Iterable2<GItem> unmodifiable(final Iterable<? extends GItem> that) throws NullPointerException {
		return new UnmodifiableIterable<>(that);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Iterable#iterator()}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Getter3<Iterable<? extends GItem>, Iterator<GItem>> iterator() {
		return (Getter3<Iterable<? extends GItem>, Iterator<GItem>>)IteratorGetter.INSTANCE;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link Set}. Wenn das Iterable ein {@link Set} ist, wird dieses geliefert.
	 * Andernfalls wird ein über {@link #addAll(Collection, Iterable)} befülltes {@link HashSet2} geliefert. */
	public static <GItem> Set<GItem> toSet(final Iterable<GItem> source) throws NullPointerException {
		if (source instanceof Set<?>) return (Set<GItem>)source;
		final HashSet2<GItem> result = new HashSet2<>();
		Iterables.addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link List}. Wenn das Iterable eine {@link List} ist, wird diese geliefert.
	 * Andernfalls wird eine über {@link #addAll(Collection, Iterable)} befüllte {@link ArrayList} geliefert. */
	public static <GItem> List<GItem> toList(final Iterable<GItem> source) throws NullPointerException {
		if (source instanceof List<?>) return (List<GItem>)source;
		final ArrayList<GItem> result = new ArrayList<>();
		Iterables.addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als Array. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray() geliefert} wird. */
	public static Object[] toArray(final Iterable<?> source) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<?>)source : Iterables.toList(source)).toArray();
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als Array. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray(Object[]) geliefert} wird. */
	public static <GItem> GItem[] toArray(final Iterable<? extends GItem> source, final GItem[] array) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<? extends GItem>)source : Iterables.toList(source)).toArray(array);
	}

}
