package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.array.Array;
import bee.creative.bind.AbstractGetter;
import bee.creative.bind.Getter;
import bee.creative.lang.Objects;
import bee.creative.lang.Objects.BaseObject;
import bee.creative.lang.Objects.UseToString;

/** Diese Klasse implementiert grundlegende {@link Iterable}.
 *
 * @see Iterator
 * @see Iterators
 * @see Iterable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterables {

	/** Diese Klasse implementiert ein abstraktes {@link Iterable} mit {@link UseToString}-Merkierung. */
	public static abstract class BaseIterable<GItem> extends BaseObject implements Iterable<GItem>, UseToString {
	}

	/** Diese Klasse implementiert {@link Iterables#emptyIterable()}. */
	public static class EmptyIterable extends BaseIterable<Object> {

		static final Iterable<?> INSTANCE = new EmptyIterable();

		@Override
		public Iterator<Object> iterator() {
			return Iterators.emptyIterator();
		}

	}

	/** Diese Klasse implementiert {@link Iterables#itemIterable(Object, int)}. */
	@SuppressWarnings ("javadoc")
	public static class ItemIterable<GItem> extends BaseIterable<GItem> {

		public final GItem item;

		public final int count;

		public ItemIterable(final GItem item, final int count) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
			this.item = item;
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.itemIterator(this.item, this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.count);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#integerIterable(int)}. */
	@SuppressWarnings ("javadoc")
	public static class IntegerIterable extends BaseIterable<Integer> {

		public final int count;

		public IntegerIterable(final int count) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		@Override
		public Iterator<Integer> iterator() {
			return Iterators.integerIterator(this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#limitedIterable(int, Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class LimitedIterable<GItem> extends BaseIterable<GItem> {

		public final int count;

		public final Iterable<? extends GItem> iterable;

		public LimitedIterable(final int count, final Iterable<? extends GItem> iterable) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.limitedIterator(this.count, this.iterable.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count, this.iterable);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#filteredIterable(Filter, Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class FilteredIterable<GItem> extends BaseIterable<GItem> {

		public final Filter<? super GItem> filter;

		public final Iterable<? extends GItem> iterable;

		public FilteredIterable(final Filter<? super GItem> filter, final Iterable<? extends GItem> iterable) {
			this.filter = Objects.notNull(filter);
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.filteredIterator(this.filter, this.iterable.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter, this.iterable);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#uniqueIterable(Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class UniqueIterable<GItem> extends BaseIterable<GItem> {

		public final Iterable<? extends GItem> iterable;

		public UniqueIterable(final Iterable<? extends GItem> iterable) {
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.uniqueIterator(this.iterable.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterable);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#chainedIterable(Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class ChainedIterable<GItem> extends BaseIterable<GItem> {

		public final Iterable<? extends Iterable<? extends GItem>> iterables;

		public ChainedIterable(final Iterable<? extends Iterable<? extends GItem>> iterables) {
			this.iterables = Objects.notNull(iterables);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.chainedIterator(Iterators.translatedIterator(Iterables.<GItem>toIteratorGetter(), this.iterables.iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterables);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#translatedIterable(Getter, Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedIterable<GSource, GTarget> extends BaseIterable<GTarget> {

		public final Iterable<? extends GSource> iterable;

		public final Getter<? super GSource, ? extends GTarget> toTarget;

		public TranslatedIterable(final Getter<? super GSource, ? extends GTarget> navigator, final Iterable<? extends GSource> iterable) {
			this.iterable = Objects.notNull(iterable);
			this.toTarget = Objects.notNull(navigator);
		}

		@Override
		public Iterator<GTarget> iterator() {
			return Iterators.translatedIterator(this.toTarget, this.iterable.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toTarget, this.iterable);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#unmodifiableIterable(Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class UnmodifiableIterable<GItem> extends BaseIterable<GItem> {

		public final Iterable<? extends GItem> iterable;

		public UnmodifiableIterable(final Iterable<? extends GItem> iterable) {
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.unmodifiableIterator(this.iterable.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterable);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#toIteratorGetter()}. */
	static class IteratorGetter extends AbstractGetter<Iterable<?>, Iterator<?>> {

		public static final Getter<?, ?> INSTANCE = new IteratorGetter();

		@Override
		public Iterator<?> get(final Iterable<?> input) {
			return input.iterator();
		}

	}

	/** Diese Klasse implementiert {@link Iterables#unionIterable(Comparator, Iterable, Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class UnionIterable<GItem> extends BaseIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends Iterable<? extends GItem>> iterable;

		public UnionIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iterable) {
			this.order = Objects.notNull(order);
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.unionIterator(this.order, Iterators.translatedIterator(Iterables.<GItem>toIteratorGetter(), this.iterable.iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iterable);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#exceptIterable(Comparator, Iterable, Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class ExceptIterable<GItem> extends BaseIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends GItem> iterable1;

		public final Iterable<? extends GItem> iterable2;

		public ExceptIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1, final Iterable<? extends GItem> iterable2) {
			this.order = Objects.notNull(order);
			this.iterable1 = Objects.notNull(iterable1);
			this.iterable2 = Objects.notNull(iterable2);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.exceptIterator(this.order, this.iterable1.iterator(), this.iterable2.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iterable1, this.iterable2);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#intersectIterable(Comparator, Iterable, Iterable)}. */
	@SuppressWarnings ("javadoc")
	public static class IntersectIterable<GItem> extends BaseIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends Iterable<? extends GItem>> iterable;

		public IntersectIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iterable) {
			this.order = Objects.notNull(order);
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.intersectIterator(this.order, Iterators.translatedIterator(Iterables.<GItem>toIteratorGetter(), this.iterable.iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iterable);
		}

	}

	/** Diese Methode gibt die Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente zurück.
	 *
	 * @param iterable {@link Iterable}.
	 * @return Anzahl der gelieferten Elemente.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static int size(final Iterable<?> iterable) throws NullPointerException {
		if (iterable instanceof Collection<?>) return ((Collection<?>)iterable).size();
		if (iterable instanceof Array<?, ?>) return ((Array<?, ?>)iterable).size();
		return -Iterators.skip(iterable.iterator(), -1) - 1;
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterable} in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
	 *
	 * @see Iterators#addAll(Collection, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
	public static <GItem> boolean addAll(final Collection<GItem> collection, final Iterable<? extends GItem> iterable) throws NullPointerException {
		return Iterators.addAll(collection, iterable.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung
	 * des {@link Iterable} {@code true} zurück.
	 *
	 * @see Iterators#retainAll(Iterator, Collection)
	 * @param iterable {@link Iterable}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist. */
	public static boolean retainAll(final Iterable<?> iterable, final Collection<?> collection) throws NullPointerException {
		return Iterators.retainAll(iterable.iterator(), Objects.notNull(collection));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, und gibt nur bei Veränderung der
	 * {@link Collection} {@code true} zurück.
	 *
	 * @see Iterators#retainAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist. */
	public static boolean retainAll(final Collection<?> collection, final Iterable<?> iterable) throws NullPointerException {
		return Iterators.retainAll(collection, iterable.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} und gibt nur bei Veränderung des {@link Iterable} {@code true} zurück.
	 *
	 * @see Iterators#removeAll(Iterator)
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist. */
	public static boolean removeAll(final Iterable<?> iterable) throws NullPointerException {
		return Iterators.removeAll(iterable.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des
	 * {@link Iterable} {@code true} zurück.
	 *
	 * @see Iterators#removeAll(Iterator, Collection)
	 * @param iterable {@link Iterable}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist. */
	public static boolean removeAll(final Iterable<?> iterable, final Collection<?> collection) throws NullPointerException {
		return Iterators.removeAll(iterable.iterator(), collection);
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} aus der gegebenen {@link Collection} und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
	 *
	 * @see Iterators#removeAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist. */
	public static boolean removeAll(final Collection<?> collection, final Iterable<?> iterable) throws NullPointerException {
		return Iterators.removeAll(collection, iterable.iterator());
	}

	/** Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterable} in der gegebenen {@link Collection} enthalten sind.
	 *
	 * @see Iterators#containsAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei vollständiger Inklusion.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist. */
	public static boolean containsAll(final Collection<?> collection, final Iterable<?> iterable) throws NullPointerException {
		return Iterators.containsAll(collection, iterable.iterator());
	}

	/** Diese Methode gibt den gegebenen {@link Iterable} zurück, sofern dieser nicht {@code null} ist. Andernfalls wird {@link #emptyIterable()} geliefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable} oder {@code null}.
	 * @return {@link Iterable} oder {@link #emptyIterable()}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable<GItem> iterable(final Iterable<? extends GItem> iterable) {
		if (iterable == null) return Iterables.emptyIterable();
		return (Iterable<GItem>)iterable;
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das einmalig das gegebenen Element liefert.
	 *
	 * @see #itemIterable(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @return {@code item}-{@link Iterable}. */
	public static <GItem> Iterable<GItem> itemIterable(final GItem item) {
		return Iterables.itemIterable(item, 1);
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das das gegebenen Element die gegebene Anzahl mal liefert.
	 *
	 * @see Iterators#itemIterator(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @param count Anzahl der iterierbaren Elemente.
	 * @return {@code item}-{@link Iterable}
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterable<GItem> itemIterable(final GItem item, final int count) throws IllegalArgumentException {
		if (count == 0) return Iterables.emptyIterable();
		return new ItemIterable<>(item, count);
	}

	/** Diese Methode gibt das leere {@link Iterable} zurück.
	 *
	 * @see Iterators#emptyIterator()
	 * @param <GItem> Typ der Elemente.
	 * @return {@link EmptyIterable}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable<GItem> emptyIterable() {
		return (Iterable<GItem>)EmptyIterable.INSTANCE;
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das die gegebene Anzahl an {@link Integer} ab dem Wert {@code 0} liefert, d.h {@code 0}, {@code 1}, ...,
	 * {@code count-1}.
	 *
	 * @see Iterators#integerIterator(int)
	 * @param count Anzahl.
	 * @return {@link Integer}-{@link Iterable}.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static Iterable<Integer> integerIterable(final int count) throws IllegalArgumentException {
		return new IntegerIterable(count);
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das die gegebene maximale Anzahl an Elementen des gegebenen {@link Iterable} liefert.
	 *
	 * @see Iterators#limitedIterator(int, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param count Maximale Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code limited}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterable<GItem> limitedIterable(final int count, final Iterable<? extends GItem> iterable)
		throws NullPointerException, IllegalArgumentException {
		if (count == 0) return Iterables.emptyIterable();
		return new LimitedIterable<>(count, iterable);
	}

	/** Diese Methode gibt ein filterndes {@link Iterable} zurück, das nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterable}
	 * liefert.
	 *
	 * @see Iterators#filteredIterator(Filter, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @param iterable {@link Iterable}.
	 * @return {@code filtered}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code filter} bzw. {@code iterable} {@code null} ist. */
	public static <GItem> Iterable<GItem> filteredIterable(final Filter<? super GItem> filter, final Iterable<? extends GItem> iterable)
		throws NullPointerException {
		return new FilteredIterable<>(filter, iterable);
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das kein Element des gegebenen {@link Iterable} mehrfach liefert.
	 *
	 * @see Iterators#uniqueIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code unique}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> Iterable<GItem> uniqueIterable(final Iterable<? extends GItem> iterable) throws NullPointerException {
		return new UniqueIterable<>(iterable);
	}

	/** Diese Methode gibt ein verkettetes {@link Iterable} zurück, das alle Elemente der gegebenen {@link Iterable} in der gegebenen Reihenfolge liefert.
	 *
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterables {@link Iterable}, dessen Elemente ({@link Iterator}) verkettet werden.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterables} {@code null} ist. */
	public static <GItem> Iterable<GItem> chainedIterable(final Iterable<? extends Iterable<? extends GItem>> iterables) throws NullPointerException {
		return new ChainedIterable<>(iterables);
	}

	/** Diese Methode gibt ein verkettetes {@link Iterable} zurück, das alle Elemente der gegebenen {@link Iterable} in der gegebenen Reihenfolge liefert.
	 *
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterables Array, dessen Elemente ({@link Iterator}) verkettet werden.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterables} {@code null} ist. */
	@SafeVarargs
	public static <GItem> Iterable<GItem> chainedIterable(final Iterable<? extends GItem>... iterables) throws NullPointerException {
		return Iterables.chainedIterable(Arrays.asList(iterables));
	}

	/** Diese Methode gibt ein verkettetes {@link Iterable} zurück, das alle Elemente der gegebenen {@link Iterable} in der gegebenen Reihenfolge liefert.
	 *
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable1 erstes {@link Iterable} oder {@code null}.
	 * @param iterable2 zweites {@link Iterable} oder {@code null}.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable1} bzw. {@code iterable2} {@code null} ist. */
	public static <GItem> Iterable<GItem> chainedIterable(final Iterable<? extends GItem> iterable1, final Iterable<? extends GItem> iterable2)
		throws NullPointerException {
		return Iterables.chainedIterable(Arrays.asList(iterable1, iterable2));
	}

	/** Diese Methode ist eine Abkürzung für {@code Iterables.chainedIterable(Iterables.itemIterable(iterable, count))} und liefert ein {@link Iterable}, welches
	 * die gegebene Anzahl Mal über die Elemente des gegebenen {@link Iterable} iteriert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param count Anzahl der Wiederholungen.
	 * @param iterable {@link Iterable}.
	 * @return {@code repeated}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterable<GItem> repeatedIterable(final int count, final Iterable<? extends GItem> iterable)
		throws NullPointerException, IllegalArgumentException {
		return Iterables.chainedIterable(Iterables.itemIterable(Objects.notNull(iterable), count));
	}

	/** Diese Methode gibt ein umgewandeltes {@link Iterable} zurück, das die vom gegebenen {@link Getter} konvertierten Elemente der gegebenen {@link Iterable}
	 * liefert.
	 *
	 * @see Iterators#translatedIterator(Getter, Iterator)
	 * @param <GSource> Typ der Eingabe des gegebenen {@link Getter} sowie der Elemente des gegebenen {@link Iterable}.
	 * @param <GTarget> Typ der Ausgabe des gegebenen {@link Getter} sowie der Elemente des erzeugten {@link Iterable}.
	 * @param toTarget {@link Getter} zur Navigation.
	 * @param iterable {@link Iterable}.
	 * @return {@code translated}-{@link Iterable}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
	public static <GSource, GTarget> Iterable<GTarget> translatedIterable(final Getter<? super GSource, ? extends GTarget> toTarget,
		final Iterable<? extends GSource> iterable) throws NullPointerException {
		return new TranslatedIterable<>(toTarget, iterable);
	}

	/** Diese Methode gibt ein unveränderliches {@link Iterable} zurück, das die Elemente des gegebenen {@link Iterable} liefert.
	 *
	 * @see Iterators#unmodifiableIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code unmodifiable}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> Iterable<GItem> unmodifiableIterable(final Iterable<? extends GItem> iterable) throws NullPointerException {
		return new UnmodifiableIterable<>(iterable);
	}

	/** Diese Methode gibt ein vereinigendes {@link Iterable} zurück, das die aufsteigend geordnete Vereinigung der gegebenen {@link Iterable} liefert. Die
	 * gegebenen Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterables {@link Iterable}, deren Elemente ({@link Iterable}) vereinigt werden.
	 * @return {@code union}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order} bzw. {@code iterables} {@code null} ist. */
	@SafeVarargs
	public static <GItem> Iterable<GItem> unionIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem>... iterables) {
		return new UnionIterable<>(order, Arrays.asList(iterables));
	}

	/** Diese Methode gibt ein vereinigendes {@link Iterable} zurück, das die aufsteigend geordnete Vereinigung der gegebenen {@link Iterable} liefert. Die
	 * gegebenen Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterables {@link Iterable}, deren Elemente ({@link Iterable}) vereinigt werden.
	 * @return {@code union}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order} bzw. {@code iterables} {@code null} ist. */
	public static <GItem> Iterable<GItem> unionIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iterables) {
		return new UnionIterable<>(order, iterables);
	}

	/** Diese Methode gibt ein vereinigendes {@link Iterable} zurück, das die aufsteigend geordnete Vereinigung der gegebenen {@link Iterable} liefert. Die
	 * gegebenen Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterable1 erstes {@link Iterable}.
	 * @param iterable2 zweites {@link Iterable}.
	 * @return {@code union}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order}, {@code iterable1} bzw. {@code iterable2} {@code null} ist. */
	public static <GItem> Iterable<GItem> unionIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1,
		final Iterable<? extends GItem> iterable2) {
		return Iterables.unionIterable(order, Arrays.asList(Objects.notNull(iterable1), Objects.notNull(iterable2)));
	}

	/** Diese Methode gibt ein ausschließendes {@link Iterable} zurück, das aufsteigend geordnete die Elemente des ersten gegebenen {@link Iterable} ohne die des
	 * zweiten liefert. Die gegebenen Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterable1 erstes {@link Iterable}.
	 * @param iterable2 zweites {@link Iterable}.
	 * @return {@code except}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order}, {@code iterable1} bzw. {@code iterable2} {@code null} ist. */
	public static <GItem> Iterable<GItem> exceptIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1,
		final Iterable<? extends GItem> iterable2) {
		return new ExceptIterable<>(order, iterable1, iterable2);
	}

	/** Diese Methode gibt ein schneidendes {@link Iterable} zurück, das den aufsteigend geordneten Schnitt der gegebenen {@link Iterable} liefert. Die gegebenen
	 * Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterables {@link Iterable}, deren Elemente ({@link Iterable}) geschnitten werden.
	 * @return {@code intersect}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order} bzw. {@code iterables} {@code null} ist. */
	public static <GItem> Iterable<GItem> intersectIterable(final Comparator<? super GItem> order,
		final Iterable<? extends Iterable<? extends GItem>> iterables) {
		return new IntersectIterable<>(order, iterables);
	}

	/** Diese Methode gibt ein schneidendes {@link Iterable} zurück, das den aufsteigend geordneten Schnitt der gegebenen {@link Iterable} liefert. Die gegebenen
	 * Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterables {@link Iterable}, deren Elemente ({@link Iterable}) geschnitten werden.
	 * @return {@code intersect}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order} bzw. {@code iterables} {@code null} ist. */
	@SafeVarargs
	public static <GItem> Iterable<GItem> intersectIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem>... iterables) {
		return Iterables.intersectIterable(order, Arrays.asList(iterables));
	}

	/** Diese Methode gibt ein schneidendes {@link Iterable} zurück, das den aufsteigend geordneten Schnitt der gegebenen {@link Iterable} liefert. Die gegebenen
	 * Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterable1 erstes {@link Iterable}.
	 * @param iterable2 zweites {@link Iterable}.
	 * @return {@code intersect}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order}, {@code iterable1} bzw. {@code iterable2} {@code null} ist. */
	public static <GItem> Iterable<GItem> intersectIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1,
		final Iterable<? extends GItem> iterable2) {
		return Iterables.intersectIterable(order, Arrays.asList(Objects.notNull(iterable1), Objects.notNull(iterable2)));
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als {@link Set} zurück. Wenn das gegebene {@link Iterable} ein {@link Set} ist, wird dieses
	 * geliefert. Andernfalls wird ein über {@link #addAll(Collection, Iterable)} befülltes {@link HashSet2} geliefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@link Set} der Elemente.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> Set<GItem> toSet(final Iterable<GItem> iterable) throws NullPointerException {
		if (iterable instanceof Set<?>) return (Set<GItem>)iterable;
		final HashSet2<GItem> result = new HashSet2<>();
		Iterables.addAll(result, iterable);
		return result;
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als {@link List} zurück. Wenn das gegebene {@link Iterable} eine {@link List} ist, wird
	 * diese geliefert. Andernfalls wird eine über {@link #addAll(Collection, Iterable)} befüllte {@link ArrayList} geliefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@link List} der Elemente.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> List<GItem> toList(final Iterable<GItem> iterable) throws NullPointerException {
		if (iterable instanceof List<?>) return (List<GItem>)iterable;
		final ArrayList<GItem> result = new ArrayList<>();
		Iterables.addAll(result, iterable);
		return result;
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als Array zurück. Dazu wird das gegebene {@link Iterable} in eine {@link Collection}
	 * überführt, deren Inhalt schließlich aks Array {@link Collection#toArray() geliefert} wird.
	 *
	 * @param iterable {@link Iterable}.
	 * @return Array der Elemente.
	 * @throws NullPointerException Wenn {@code result} bzw. {@code iterable} {@code null} ist. */
	public static Object[] toArray(final Iterable<?> iterable) throws NullPointerException {
		return (iterable instanceof Collection<?> ? (Collection<?>)iterable : Iterables.toList(iterable)).toArray();
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als Array zurück. Dazu wird das gegebene {@link Iterable} in eine {@link Collection}
	 * überführt, deren Inhalt schließlich aks Array {@link Collection#toArray(Object[]) geliefert} wird.
	 *
	 * @param result Ergebnis für {@link Collection#toArray(Object[])}.
	 * @param iterable {@link Iterable}.
	 * @param <GItem> Typ der Elemente.
	 * @return Array der Elemente.
	 * @throws NullPointerException Wenn {@code result} bzw. {@code iterable} {@code null} ist. */
	public static <GItem> GItem[] toArray(final GItem[] result, final Iterable<? extends GItem> iterable) throws NullPointerException {
		return (iterable instanceof Collection<?> ? (Collection<? extends GItem>)iterable : Iterables.toList(iterable)).toArray(result);
	}

	/** Diese Methode gibt den {@link Getter} zurück, der den {@link Iterator} eines {@link Iterable} ermittelt.
	 *
	 * @see Iterable#iterator()
	 * @param <GItem> Typ der Elemente.
	 * @return {@code iterator}-{@link Getter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Getter<Iterable<? extends GItem>, Iterator<GItem>> toIteratorGetter() {
		return (Getter<Iterable<? extends GItem>, Iterator<GItem>>)IteratorGetter.INSTANCE;
	}

}
