package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.array.Array;
import bee.creative.lang.Objects;
import bee.creative.util.Iterators.TranslatedIterator;

/** Diese Klasse implementiert grundlegende {@link Iterable}.
 *
 * @see Iterator
 * @see Iterators
 * @see Iterable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterables {

	public static class EmptyIterable extends AbstractIterable<Object> {

		static final Iterable<?> INSTANCE = new EmptyIterable();

	}

	public static class UniformIterable<GItem> extends AbstractIterable<GItem> {

		public final GItem item;

		public final int count;

		public UniformIterable(final GItem item, final int count) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
			this.item = item;
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.fromItem(this.item, this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.item, this.count);
		}

	}

	public static class IntegerIterable extends AbstractIterable<Integer> {

		public final int count;

		public IntegerIterable(final int count) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		@Override
		public Iterator<Integer> iterator() {
			return Iterators.fromCount(this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count);
		}

	}

	public static class LimitedIterable<GItem> extends AbstractIterable<GItem> {

		public final int count;

		public final Iterable<? extends GItem> iterable;

		public LimitedIterable(final int count, final Iterable<? extends GItem> iterable) {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.toLimited(this.iterable.iterator(), this.count);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.count, this.iterable);
		}

	}

	public static class FilteredIterable<GItem> extends AbstractIterable<GItem> {

		public final Filter<? super GItem> filter;

		public final Iterable<? extends GItem> iterable;

		public FilteredIterable(final Iterable<? extends GItem> target, final Filter<? super GItem> filter) {
			this.filter = Objects.notNull(filter);
			this.iterable = Objects.notNull(target);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.toFiltered(this.iterable.iterator(), this.filter);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter, this.iterable);
		}

	}

	public static class UniqueIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem> iterable;

		public UniqueIterable(final Iterable<? extends GItem> iterable) {
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.toUnique(this.iterable.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterable);
		}

	}

	public static class ChainedIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends Iterable<? extends GItem>> iterables;

		public ChainedIterable(final Iterable<? extends Iterable<? extends GItem>> iterables) {
			this.iterables = Objects.notNull(iterables);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.concatAll(Iterators.translate(this.iterables.iterator(), Iterables.<GItem>iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.iterables);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link TranslatedIterator}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedIterable<GItem, GItem2> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem2> iterable;

		public final Getter<? super GItem2, ? extends GItem> toTarget;

		public TranslatedIterable(final Iterable<? extends GItem2> target, final Getter<? super GItem2, ? extends GItem> trans) {
			this.iterable = Objects.notNull(target);
			this.toTarget = Objects.notNull(trans);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.translate(this.iterable.iterator(), this.toTarget);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toTarget, this.iterable);
		}

	}

	/** Diese Klasse implementiert das {@link Iterable2} zu {@link Iterators#toUnmodifiable(Iterator)}.
	 *
	 * @param <GItem> Typ der Elemente. */
	@SuppressWarnings ("javadoc")
	public static class UnmodifiableIterable<GItem> extends AbstractIterable<GItem> {

		public final Iterable<? extends GItem> source;

		public UnmodifiableIterable(final Iterable<? extends GItem> source) throws NullPointerException {
			this.source = Objects.notNull(source);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.toUnmodifiable(this.source.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.source);
		}

	}

	/** Diese Klasse implementiert {@link Iterables#iterator()}. */
	static class IteratorGetter extends AbstractGetter<Iterable<?>, Iterator<?>> {

		public static final Getter<?, ?> INSTANCE = new IteratorGetter();

		@Override
		public Iterator<?> get(final Iterable<?> input) {
			return input.iterator();
		}

	}

	public static class UnionIterable<GItem> extends AbstractIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends Iterable<? extends GItem>> iterable;

		public UnionIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iterable) {
			this.order = Objects.notNull(order);
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.unionAll(this.order, Iterators.translate(this.iterable.iterator(), Iterables.<GItem>iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iterable);
		}

	}

	public static class ExceptIterable<GItem> extends AbstractIterable<GItem> {

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
			return Iterators.except(this.order, this.iterable1.iterator(), this.iterable2.iterator());
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iterable1, this.iterable2);
		}

	}

	public static class IntersectIterable<GItem> extends AbstractIterable<GItem> {

		public final Comparator<? super GItem> order;

		public final Iterable<? extends Iterable<? extends GItem>> iterable;

		public IntersectIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iterable) {
			this.order = Objects.notNull(order);
			this.iterable = Objects.notNull(iterable);
		}

		@Override
		public Iterator<GItem> iterator() {
			return Iterators.intersectAll(this.order, Iterators.translate(this.iterable.iterator(), Iterables.<GItem>iterator()));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.order, this.iterable);
		}

	}

	/** Diese Methode gibt das leere {@link Iterable} zurück.
	 *
	 * @see Iterators#empty()
	 * @param <GItem> Typ der Elemente.
	 * @return {@link EmptyIterable}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable2<GItem> empty() {
		return (Iterable2<GItem>)EmptyIterable.INSTANCE;
	}

	/** Diese Methode gibt den gegebenen {@link Iterable} zurück, sofern dieser nicht {@code null} ist. Andernfalls wird {@link #empty()} geliefert.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable} oder {@code null}.
	 * @return {@link Iterable} oder {@link #empty()}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable<GItem> from(final Iterable<? extends GItem> iterable) {
		if (iterable == null) return Iterables.empty();
		return (Iterable<GItem>)iterable;
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das einmalig das gegebenen Element liefert.
	 *
	 * @see #fromItem(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @return {@code item}-{@link Iterable}. */
	public static <GItem> Iterable<GItem> fromItem(final GItem item) {
		return Iterables.fromItem(item, 1);
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das das gegebenen Element die gegebene Anzahl mal liefert.
	 *
	 * @see Iterators#fromItem(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @param count Anzahl der iterierbaren Elemente.
	 * @return {@code item}-{@link Iterable}
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterable<GItem> fromItem(final GItem item, final int count) throws IllegalArgumentException {
		if (count == 0) return Iterables.empty();
		return new UniformIterable<>(item, count);
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das die gegebene Anzahl an {@link Integer} ab dem Wert {@code 0} liefert, d.h {@code 0}, {@code 1}, ...,
	 * {@code count-1}.
	 *
	 * @see Iterators#fromCount(int)
	 * @param count Anzahl.
	 * @return {@link Integer}-{@link Iterable}.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static Iterable<Integer> fromCount(final int count) throws IllegalArgumentException {
		return new IntegerIterable(count);
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

	/** Diese Methode gibt ein verkettetes {@link Iterable} zurück, das alle Elemente der gegebenen {@link Iterable} in der gegebenen Reihenfolge liefert.
	 *
	 * @see Iterators#concatAll(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable1 erstes {@link Iterable} oder {@code null}.
	 * @param iterable2 zweites {@link Iterable} oder {@code null}.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable1} bzw. {@code iterable2} {@code null} ist. */
	public static <GItem> Iterable2<GItem> concat(final Iterable<? extends GItem> iterable1, final Iterable<? extends GItem> iterable2)
		throws NullPointerException {
		return Iterables.concatAll(Arrays.asList(iterable1, iterable2));
	}

	/** Diese Methode gibt ein verkettetes {@link Iterable} zurück, das alle Elemente der gegebenen {@link Iterable} in der gegebenen Reihenfolge liefert.
	 *
	 * @see Iterators#concatAll(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterables {@link Iterable}, dessen Elemente ({@link Iterator}) verkettet werden.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterables} {@code null} ist. */
	public static <GItem> Iterable2<GItem> concatAll(final Iterable<? extends Iterable<? extends GItem>> iterables) throws NullPointerException {
		return new ChainedIterable<>(iterables);
	}

	/** Diese Methode gibt ein vereinigendes {@link Iterable} zurück, das die aufsteigend geordnete Vereinigung der gegebenen {@link Iterable} liefert. Die
	 * gegebenen Iterable müssen ihre Elemente dazu aufsteigend in der gegebenen Ordnung liefern.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param order Ordnung der Elemente.
	 * @param iterables {@link Iterable}, deren Elemente ({@link Iterable}) vereinigt werden.
	 * @return {@code union}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code order} bzw. {@code iterables} {@code null} ist. */
	public static <GItem> Iterable2<GItem> unionIterable(final Comparator<? super GItem> order, final Iterable<? extends Iterable<? extends GItem>> iterables) {
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
	public static <GItem> Iterable2<GItem> unionIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1,
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
	public static <GItem> Iterable2<GItem> exceptIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1,
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
	public static <GItem> Iterable2<GItem> intersectIterable(final Comparator<? super GItem> order,
		final Iterable<? extends Iterable<? extends GItem>> iterables) {
		return new IntersectIterable<>(order, iterables);
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
	public static <GItem> Iterable2<GItem> intersectIterable(final Comparator<? super GItem> order, final Iterable<? extends GItem> iterable1,
		final Iterable<? extends GItem> iterable2) {
		return Iterables.intersectIterable(order, Arrays.asList(Objects.notNull(iterable1), Objects.notNull(iterable2)));
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das die gegebene maximale Anzahl an Elementen des gegebenen {@link Iterable} liefert.
	 * 
	 * @param target {@link Iterable}.
	 * @param count Maximale Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente.
	 * @see Iterators#toLimited(Iterator, int)
	 * @param <GItem> Typ der Elemente.
	 * @return {@code limited}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterable2<GItem> toLimited(final Iterable<? extends GItem> target, final int count)
		throws NullPointerException, IllegalArgumentException {
		if (count == 0) return Iterables.empty();
		return new LimitedIterable<>(count, target);
	}

	/** Diese Methode gibt ein filterndes {@link Iterable} zurück, das nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterable}
	 * liefert.
	 * 
	 * @param target {@link Iterable}.
	 * @param filter {@link Filter}.
	 * @see Iterators#toFiltered(Iterator, Filter)
	 * @param <GItem> Typ der Elemente.
	 * @return {@code filtered}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code filter} bzw. {@code iterable} {@code null} ist. */
	public static <GItem> Iterable2<GItem> toFiltered(final Iterable<? extends GItem> target, final Filter<? super GItem> filter) throws NullPointerException {
		return new FilteredIterable<>(target, filter);
	}

	/** Diese Methode gibt ein {@link Iterable} zurück, das kein Element des gegebenen {@link Iterable} mehrfach liefert.
	 *
	 * @see Iterators#toUnique(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param target {@link Iterable}.
	 * @return {@code unique}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> Iterable2<GItem> toUnique(final Iterable<? extends GItem> target) throws NullPointerException {
		return new UniqueIterable<>(target);
	}

	/** Diese Methode ist eine Abkürzung für {@code Iterables.chainedIterable(Iterables.itemIterable(iterable, count))} und liefert ein {@link Iterable}, welches
	 * die gegebene Anzahl Mal über die Elemente des gegebenen {@link Iterable} iteriert.
	 * 
	 * @param target {@link Iterable}.
	 * @param count Anzahl der Wiederholungen.
	 * @param <GItem> Typ der Elemente.
	 * @return {@code repeated}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist. */
	public static <GItem> Iterable2<GItem> toRepeated(final Iterable<? extends GItem> target, final int count)
		throws NullPointerException, IllegalArgumentException {
		return Iterables.concatAll(Iterables.fromItem(Objects.notNull(target), count));
	}

	/** Diese Methode gibt ein umgewandeltes {@link Iterable} zurück, das die vom gegebenen {@link Getter} konvertierten Elemente der gegebenen {@link Iterable}
	 * liefert.
	 * 
	 * @param target {@link Iterable}.
	 * @param trans {@link Getter} zur Navigation.
	 * @see Iterators#translate(Iterator, Getter)
	 * @param <GSource> Typ der Eingabe des gegebenen {@link Getter} sowie der Elemente des gegebenen {@link Iterable}.
	 * @param <GTarget> Typ der Ausgabe des gegebenen {@link Getter} sowie der Elemente des erzeugten {@link Iterable}.
	 * @return {@code translated}-{@link Iterable}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist. */
	public static <GSource, GTarget> Iterable2<GTarget> translate(final Iterable<? extends GSource> target,
		final Getter<? super GSource, ? extends GTarget> trans) throws NullPointerException {
		return new TranslatedIterable<>(target, trans);
	}

	/** Diese Methode gibt ein unveränderliches {@link Iterable} zurück, das die Elemente des gegebenen {@link Iterable} liefert.
	 *
	 * @see Iterators#toUnmodifiable(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code unmodifiable}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist. */
	public static <GItem> Iterable2<GItem> toUnmodifiable(final Iterable<? extends GItem> iterable) throws NullPointerException {
		return new UnmodifiableIterable<>(iterable);
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link Set}. Wenn das Iterable ein {@link Set} ist, wird dieses geliefert.
	 * Andernfalls wird ein über {@link #addAll(Collection, Iterable)} befülltes {@link HashSet2} geliefert. */
	public static <GItem> Set<GItem> toSet(final Iterable<GItem> source) throws NullPointerException {
		if (source instanceof Set<?>) return (Set<GItem>)source;
		final HashSet2<GItem> result = new HashSet2<>();
		Iterables.addAll(result, source);
		return result;
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als {@link List} zurück. Wenn das Iterable eine {@link List} ist, wird diese geliefert.
	 * Andernfalls wird eine über {@link #addAll(Collection, Iterable)} befüllte {@link ArrayList} geliefert. */
	public static <GItem> List<GItem> toList(final Iterable<GItem> source) throws NullPointerException {
		if (source instanceof List<?>) return (List<GItem>)source;
		final ArrayList<GItem> result = new ArrayList<>();
		Iterables.addAll(result, source);
		return result;
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als Array zurück. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray() geliefert} wird. */
	public static Object[] toArray(final Iterable<?> source) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<?>)source : Iterables.toList(source)).toArray();
	}

	/** Diese Methode gibt die Elemente des gegebenen {@link Iterable} als Array zurück. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray(Object[]) geliefert} wird. */
	public static <GItem> GItem[] toArray(final Iterable<? extends GItem> source, final GItem[] array) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<? extends GItem>)source : Iterables.toList(source)).toArray(array);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Iterable#iterator()}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Getter<Iterable<? extends GItem>, Iterator<GItem>> iterator() {
		return (Getter<Iterable<? extends GItem>, Iterator<GItem>>)IteratorGetter.INSTANCE;
	}

}
