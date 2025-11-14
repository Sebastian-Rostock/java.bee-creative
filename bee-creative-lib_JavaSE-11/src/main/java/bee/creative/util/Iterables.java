package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Iterators.concatIterator;
import static bee.creative.util.Iterators.emptyIterator;
import static bee.creative.util.Iterators.filteredIterator;
import static bee.creative.util.Iterators.iteratorFrom;
import static bee.creative.util.Iterators.iteratorFromArray;
import static bee.creative.util.Iterators.limitedIterator;
import static bee.creative.util.Iterators.translatedIterator;
import static bee.creative.util.Iterators.uniqueIterator;
import static bee.creative.util.Iterators.unmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.lang.Array;
import bee.creative.lang.Array2;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Iterable}.
 *
 * @see Iterators
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Iterables {

	/** Diese Methode liefert den gegebenen {@link Iterable} als {@link Iterable3}. Wenn er {@code null} ist, wird {@link #emptyIterable()} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <E> Iterable3<E> iterableFrom(Iterable<? extends E> that) {
		if (that == null) return emptyIterable();
		if (that instanceof Iterable3<?>) return (Iterable3<E>)that;
		return () -> iteratorFrom(that.iterator());
	}

	/** Diese Methode ist eine Abkürzung für {@link #iterableFromItem(Object, int) iterableFromItem(item, 1)}. */
	public static <E> Iterable3<E> iterableFromItem(E item) {
		return iterableFromItem(item, 1);
	}

	/** Diese Methode liefert einen {@link Iterable3}, der das gegebene Element die gegebene Anzahl mal liefert. **/
	public static <E> Iterable3<E> iterableFromItem(E item, int count) throws IllegalArgumentException {
		return count == 0 ? emptyIterable() : iterableFromArray(index -> item, 0, count);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#iterableFromArray(Object...) iterableFromArray(items, 0, items.length)}. */
	@SafeVarargs
	public static <E> Iterable3<E> iterableFromArray(E... items) throws NullPointerException {
		return iterableFromArray(items, 0, items.length);
	}

	/** Diese Methode liefert ein {@link Iterable3}, das die Elemente des gegebenen Array im gegebenen Abschnitt liefert. */
	public static <E> Iterable3<E> iterableFromArray(E[] items, int minInclIndex, int maxExclIndex) throws NullPointerException, IllegalArgumentException {
		notNull(items);
		if ((minInclIndex < 0) || (items.length < maxExclIndex)) throw new IllegalArgumentException();
		return iterableFromArray(index -> items[index], minInclIndex, maxExclIndex);
	}

	/** Diese Methode liefert ein {@link Iterable3}, das die Elemente des gegebenen {@link Array} im gegebenen Abschnit liefert. */
	public static <E> Iterable3<E> iterableFromArray(Array<? extends E> items, int minInclIndex, int maxExclIndex)
		throws NullPointerException, IllegalArgumentException {
		notNull(items);
		if (maxExclIndex < minInclIndex) throw new IllegalArgumentException();
		return () -> iteratorFromArray(items, minInclIndex, maxExclIndex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #iterableFromRange(int, int) iterableFromRange(0, count)}. */
	public static Iterable3<Integer> iterableFromCount(int count) throws IllegalArgumentException {
		return iterableFromRange(0, count);
	}

	/** Diese Methode liefert ein {@link Iterable3}, das die {@link Integer} im gegebenen Wertebereich liefert. */
	public static Iterable3<Integer> iterableFromRange(int minInclIndex, int maxExclIndex) throws IllegalArgumentException {
		if (maxExclIndex < minInclIndex) throw new IllegalArgumentException();
		return iterableFromArray(index -> index, minInclIndex, maxExclIndex);
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#emptyIterator()}. */
	@SuppressWarnings ("unchecked")
	public static <E> Iterable3<E> emptyIterable() {
		return (Iterable3<E>)emptyIterable;
	}

	/** Diese Methode ist eine Abkürzung für {@link #concatIterable(Iterable) concatIterable(iterableFromArray(notNull(iter1), notNull(iter2)))}. */
	public static <E> Iterable3<E> concatIterable(Iterable<? extends E> iter1, Iterable<? extends E> iter2) throws NullPointerException {
		return concatIterable(iterableFromArray(notNull(iter1), notNull(iter2)));
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#concatIterator(Iterator)}. */
	public static <E> Iterable3<E> concatIterable(Iterable<? extends Iterable<? extends E>> iters) throws NullPointerException {
		return () -> concatIterator(translatedIterator(iters.iterator(), iterableIterator()));
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#limitedIterator(Iterator, int)}. */
	public static <E> Iterable3<E> limitedIterable(Iterable<? extends E> that, int limit) throws NullPointerException, IllegalArgumentException {
		notNull(that);
		if (limit < 0) throw new IllegalArgumentException();
		return () -> limitedIterator(that.iterator(), limit);
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#filteredIterator(Iterator, Filter)}. */
	public static <E> Iterable3<E> filteredIterable(Iterable<? extends E> that, Filter<? super E> filter) throws NullPointerException {
		notNull(that);
		notNull(filter);
		return () -> filteredIterator(that.iterator(), filter);
	}

	/** Diese Methode ist eine Abkürzung für {@link Iterables#concatIterable(Iterable) concatIterable(iterableFromItem(notNull(that), count))} und liefert ein
	 * {@link Iterable3}, welches die gegebene Anzahl Mal über die Elemente des gegebenen {@link Iterable} iteriert. */
	public static <E> Iterable3<E> repeatedIterable(Iterable<? extends E> that, int count) throws NullPointerException, IllegalArgumentException {
		return concatIterable(iterableFromItem(notNull(that), count));
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#uniqueIterator(Iterator)}. */
	public static <E> Iterable3<E> uniqueIterable(Iterable<? extends E> that) throws NullPointerException {
		notNull(that);
		return () -> uniqueIterator(that.iterator());
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#uniqueIterator(Iterator, Hasher)}. */
	public static <E> Iterable3<E> uniqueIterable(Iterable<? extends E> that, Hasher hasher) throws NullPointerException {
		notNull(that);
		notNull(hasher);
		return () -> uniqueIterator(that.iterator(), hasher);
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#translatedIterator(Iterator, Getter)}. */
	public static <E2, E> Iterable3<E> translatedIterable(Iterable<? extends E2> that, Getter<? super E2, ? extends E> trans) throws NullPointerException {
		notNull(that);
		notNull(trans);
		return () -> translatedIterator(that.iterator(), trans);
	}

	/** Diese Methode liefert das {@link Iterable3} zu {@link Iterators#unmodifiableIterator(Iterator)}. */
	public static <E> Iterable3<E> unmodifiableIterable(Iterable<? extends E> that) throws NullPointerException {
		notNull(that);
		return () -> unmodifiableIterator(that.iterator());
	}

	/** Diese Methode gibt die Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente zurück. */
	public static int size(Iterable<?> that) throws NullPointerException {
		if (that instanceof Collection<?>) return ((Collection<?>)that).size();
		if (that instanceof Array2<?>) return ((Array2<?>)that).size();
		if (that instanceof bee.creative.array.Array<?, ?>) return ((bee.creative.array.Array<?, ?>)that).size();
		return Iterators.size(that.iterator());
	}

	/** Diese Methode fügt alle Elemente des gegebenen {@link Iterable} in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück. */
	public static <E> boolean addAll(Collection<E> target, Iterable<? extends E> source) throws NullPointerException {
		if (source instanceof Collection<?>) return target.addAll((Collection<? extends E>)source);
		return Iterators.addAll(target, source.iterator());
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung
	 * des {@link Iterable} {@code true} zurück. */
	public static boolean retainAll(Iterable<?> target, Collection<?> filter) throws NullPointerException {
		if (target instanceof Collection<?>) return ((Collection<?>)target).retainAll(filter);
		return Iterators.retainAll(target.iterator(), Objects.notNull(filter));
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, und gibt nur bei Veränderung der
	 * {@link Collection} {@code true} zurück. */
	public static boolean retainAll(Collection<?> target, Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.retainAll((Collection<?>)filter);
		return Iterators.retainAll(target, filter.iterator());
	}

	/** Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, fügt alle Elemetne des
	 * {@link Iterable} in die {@link Collection} ein und gibt nur bei Veränderung der {@link Collection} {@code true} zurück. */
	public static <E> boolean replaceAll(Collection<E> target, Iterable<? extends E> source) throws NullPointerException {
		var buffer = Iterables.toSet(source);
		return target.retainAll(buffer) | target.addAll(buffer);
	}

	/** Diese Methode entfernt alle Elemente des gegebenen {@link Iterable} und gibt nur bei Veränderung des {@link Iterable} {@code true} zurück. */
	public static boolean removeAll(Iterable<?> target) throws NullPointerException {
		return Iterators.removeAll(target.iterator());
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
		return target.removeAll(Iterables.toSet(filter));
	}

	/** Diese Methode liefert nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterable} in der gegebenen {@link Collection} enthalten
	 * sind. */
	public static boolean containsAll(Collection<?> target, Iterable<?> filter) throws NullPointerException {
		if (filter instanceof Collection<?>) return target.containsAll((Collection<?>)filter);
		return Iterators.containsAll(target, filter.iterator());
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link Iterable#iterator()}. */
	@SuppressWarnings ("unchecked")
	public static <E> Getter3<Iterable<? extends E>, Iterator<E>> iterableIterator() {
		return (Getter3<Iterable<? extends E>, Iterator<E>>)iterableIterator;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link Set}. Wenn das Iterable ein {@link Set} ist, wird dieses geliefert.
	 * Andernfalls wird ein über {@link #addAll(Collection, Iterable)} befülltes {@link HashSet2} geliefert. */
	public static <E> Set<E> toSet(Iterable<E> source) throws NullPointerException {
		if (source instanceof Set<?>) return (Set<E>)source;
		var result = new HashSet2<E>();
		addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als {@link List}. Wenn das Iterable eine {@link List} ist, wird diese geliefert.
	 * Andernfalls wird eine über {@link #addAll(Collection, Iterable)} befüllte {@link ArrayList} geliefert. */
	public static <E> List<E> toList(Iterable<E> source) throws NullPointerException {
		if (source instanceof List<?>) return (List<E>)source;
		var result = new ArrayList<E>();
		addAll(result, source);
		return result;
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als Array. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray() geliefert} wird. */
	public static Object[] toArray(Iterable<?> source) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<?>)source : toList(source)).toArray();
	}

	/** Diese Methode liefert die Elemente des gegebenen {@link Iterable} als Array. Dazu wird das Iterable in eine {@link Collection} überführt, deren Inhalt
	 * schließlich als Array {@link Collection#toArray(Object[]) geliefert} wird. */
	public static <E> E[] toArray(Iterable<?> source, E[] array) throws NullPointerException {
		return (source instanceof Collection<?> ? (Collection<?>)source : toList(source)).toArray(array);
	}

	private static final Iterable<?> emptyIterable = () -> emptyIterator();

	private static final Getter<? extends Iterable<?>, ?> iterableIterator = Iterable::iterator;

}
