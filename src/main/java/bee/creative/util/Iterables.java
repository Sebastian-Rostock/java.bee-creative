package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert grundlegende {@link Iterable}.
 * 
 * @see Iterator
 * @see Iterators
 * @see Iterable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Iterables {

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Iterable}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GItem> Typ der Elemente.
	 */
	public static abstract class BaseIterable<GItem> implements Iterable<GItem>, UseToString {

	}

	{}

	/**
	 * Dieses Feld speichert das leere {@link Iterable}.
	 */
	public static final Iterable<?> EMPTY_ITERABLE = new Iterable<Object>() {

		@Override
		public Iterator<Object> iterator() {
			return Iterators.emptyIterator();
		}

		@Override
		public String toString() {
			return "EMPTY_ITERABLE";
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der den {@link Iterator} eines {@link Iterable} ermittelt.
	 */
	public static final Converter<?, ?> ITERABLE_ITERATOR = new Converter<Iterable<?>, Iterator<?>>() {

		@Override
		public Iterator<?> convert(final Iterable<?> input) {
			return input.iterator();
		}

		@Override
		public String toString() {
			return "ITERABLE_ITERATOR";
		}

	};

	{}

	/**
	 * Diese Methode gibt die Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente zurück.
	 * 
	 * @param iterable {@link Iterable}.
	 * @return Anzahl der gelieferten Elemente.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 */
	public static int size(final Iterable<?> iterable) throws NullPointerException {
		if (iterable == null) throw new NullPointerException("iterable = null");
		return -Iterators.skip(iterable.iterator(), -1) - 1;
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}s, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung
	 * des {@link Iterable}s {@code true} zurück.
	 * 
	 * @see Iterators#retainAll(Iterator, Collection)
	 * @param iterable {@link Iterable}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean retainAll(final Iterable<?> iterable, final Collection<?> collection) throws NullPointerException {
		if (iterable == null) throw new NullPointerException("iterable = null");
		if (collection == null) throw new NullPointerException("collection = null");
		return Iterators.retainAll(iterable.iterator(), collection);
	}

	/**
	 * Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable} vorkommen, und gibt nur bei Veränderung der
	 * {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#retainAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean retainAll(final Collection<?> collection, final Iterable<?> iterable) throws NullPointerException {
		if (collection == null) throw new NullPointerException("collection = null");
		if (iterable == null) throw new NullPointerException("iterable = null");
		return Iterators.retainAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link Iterable}s in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#appendAll(Collection, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GItem> boolean appendAll(final Collection<GItem> collection, final Iterable<? extends GItem> iterable) throws NullPointerException {
		if (collection == null) throw new NullPointerException("collection = null");
		if (iterable == null) throw new NullPointerException("iterable = null");
		return Iterators.appendAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}s und gibt nur bei Veränderung des {@link Iterable}s {@code true} zurück.
	 * 
	 * @see Iterators#removeAll(Iterator)
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
	 */
	public static boolean removeAll(final Iterable<?> iterable) throws NullPointerException {
		if (iterable == null) throw new NullPointerException("iterable = null");
		return Iterators.removeAll(iterable.iterator());
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}s, die in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des
	 * {@link Iterable}s {@code true} zurück.
	 * 
	 * @see Iterators#removeAll(Iterator, Collection)
	 * @param iterable {@link Iterable}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean removeAll(final Iterable<?> iterable, final Collection<?> collection) throws NullPointerException {
		if (iterable == null) throw new NullPointerException("iterable = null");
		if (collection == null) throw new NullPointerException("collection = null");
		return Iterators.removeAll(iterable.iterator(), collection);
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}s aus der gegebenen {@link Collection} und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#removeAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean removeAll(final Collection<?> collection, final Iterable<?> iterable) throws NullPointerException {
		if (collection == null) throw new NullPointerException("collection = null");
		if (iterable == null) throw new NullPointerException("iterable = null");
		return Iterators.removeAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterable}s in der gegebenen {@link Collection} enthalten sind.
	 * 
	 * @see Iterators#containsAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei vollständiger Inklusion.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean containsAll(final Collection<?> collection, final Iterable<?> iterable) throws NullPointerException {
		if (collection == null) throw new NullPointerException("collection = null");
		if (iterable == null) throw new NullPointerException("iterable = null");
		return Iterators.containsAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Iterable} oder {@link #EMPTY_ITERABLE} zurück. Wenn {@code iterable} {@code null} ist, wird {@link #EMPTY_ITERABLE}
	 * geliefert.
	 * 
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable} oder {@code null}.
	 * @return {@link Iterable} oder {@link #EMPTY_ITERABLE}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable<GItem> iterable(final Iterable<? extends GItem> iterable) {
		if (iterable == null) return Iterables.emptyIterable();
		return (Iterable<GItem>)iterable;
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} zurück, das einmalig das gegebenen Element liefert.
	 * 
	 * @see #itemIterable(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @return {@code item}-{@link Iterable}.
	 */
	public static <GItem> Iterable<GItem> itemIterator(final GItem item) {
		return Iterables.itemIterable(item, 1);
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} zurück, das das gegebenen Element die gegebene Anzahl mal liefert.
	 * 
	 * @see Iterators#itemIterator(Object, int)
	 * @param <GItem> Typ des Elements.
	 * @param item Element.
	 * @param count Anzahl der iterierbaren Elemente.
	 * @return {@code item}-{@link Iterable}
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist.
	 */
	public static <GItem> Iterable<GItem> itemIterable(final GItem item, final int count) throws IllegalArgumentException {
		if (count == 0) return Iterables.emptyIterable();
		if (count < 0) throw new IllegalArgumentException("count < 0");
		return new BaseIterable<GItem>() {

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.itemIterator(item, count);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("itemIterable", item, count);
			}

		};
	}

	/**
	 * Diese Methode gibt das leere {@link Iterable} zurück.
	 * 
	 * @see Iterators#emptyIterator()
	 * @param <GItem> Typ der Elemente.
	 * @return {@link #EMPTY_ITERABLE}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable<GItem> emptyIterable() {
		return (Iterable<GItem>)Iterables.EMPTY_ITERABLE;
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} zurück, das die gegebene Anzahl an {@link Integer} ab dem Wert {@code 0} liefert, d.h {@code 0}, {@code 1}, ...,
	 * {@code count-1}.
	 * 
	 * @see Iterators#integerIterator(int)
	 * @param count Anzahl.
	 * @return {@link Integer}-{@link Iterable}.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist.
	 */
	public static Iterable<Integer> integerIterable(final int count) throws IllegalArgumentException {
		if (count < 0) throw new IllegalArgumentException("count < 0");
		return new BaseIterable<Integer>() {

			@Override
			public Iterator<Integer> iterator() {
				return Iterators.integerIterator(count);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("integerIterable", count);
			}

		};
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der den {@link Iterator} eines {@link Iterable} ermittelt.
	 * 
	 * @see Iterable#iterator()
	 * @param <GItem> Typ der Elemente.
	 * @return {@link #ITERABLE_ITERATOR}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GItem> Converter<Iterable<? extends GItem>, Iterator<GItem>> iterableIterator() {
		return (Converter<Iterable<? extends GItem>, Iterator<GItem>>)Iterables.ITERABLE_ITERATOR;
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} zurück, das die gegebene maximale Anzahl an Elementen des gegebenen {@link Iterable} liefert.
	 * 
	 * @see Iterators#limitedIterator(int, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param count Maximale Anzahl der vom gegebenen {@link Iterable} gelieferten Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code limited}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@code count < 0} ist.
	 */
	public static <GItem> Iterable<GItem> limitedIterable(final int count, final Iterable<? extends GItem> iterable) throws NullPointerException,
		IllegalArgumentException {
		if (count < 0) throw new IllegalArgumentException("count < 0");
		if (iterable == null) throw new NullPointerException("iterable = null");
		if (count == 0) return Iterables.emptyIterable();
		return new BaseIterable<GItem>() {

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.limitedIterator(count, iterable.iterator());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("limitedIterable", count, iterable);
			}

		};
	}

	/**
	 * Diese Methode gibt ein filterndes {@link Iterable} zurück, das nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterable}
	 * liefert.
	 * 
	 * @see Iterators#filteredIterator(Filter, Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @param iterable {@link Iterable}.
	 * @return {@code filtered}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code filter} bzw. {@code iterable} {@code null} ist.
	 */
	public static <GItem> Iterable<GItem> filteredIterable(final Filter<? super GItem> filter, final Iterable<? extends GItem> iterable)
		throws NullPointerException {
		if (filter == null) throw new NullPointerException("filter = null");
		if (iterable == null) throw new NullPointerException("iterable = null");
		return new BaseIterable<GItem>() {

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.filteredIterator(filter, iterable.iterator());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("filteredIterable", filter, iterable);
			}

		};
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} zurück, das kein Element des gegebenen {@link Iterable} mehrfach liefert.
	 * 
	 * @see Iterators#uniqueIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code unique}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 */
	public static <GItem> Iterable<GItem> uniqueIterable(final Iterable<? extends GItem> iterable) throws NullPointerException {
		if (iterable == null) throw new NullPointerException("iterable = null");
		return new BaseIterable<GItem>() {

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.uniqueIterator(iterable.iterator());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("uniqueIterable", iterable);
			}

		};
	}

	/**
	 * Diese Methode gibt ein verkettetes {@link Iterable} zurück, das alle Elemente der gegebenen {@link Iterable} in der gegebenen Reihenfolge liefert.
	 * 
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterables {@link Iterable}, dessen Elemente ({@link Iterator}) verkettet werden.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterables} {@code null} ist.
	 */
	public static <GItem> Iterable<GItem> chainedIterable(final Iterable<? extends Iterable<? extends GItem>> iterables) throws NullPointerException {
		if (iterables == null) throw new NullPointerException("iterables = null");
		return new BaseIterable<GItem>() {

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.chainedIterator(Iterators.convertedIterator(Iterables.<GItem>iterableIterator(), iterables.iterator()));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("chainedIterable", iterables);
			}

		};
	}

	/**
	 * Diese Methode gibt ein umgewandeltes {@link Iterable} zurück, das die vom gegebenen {@link Converter} konvertierten Elemente der gegebenen {@link Iterable}
	 * in der gegebenen Reihenfolge liefert.
	 * 
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterables Array, dessen Elemente ({@link Iterator}) verkettet werden.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterables} {@code null} ist.
	 */
	@SuppressWarnings ("unchecked")
	public static <GItem> Iterable<GItem> chainedIterable(final Iterable<? extends GItem>... iterables) throws NullPointerException {
		if (iterables == null) throw new NullPointerException("iterables = null");
		return Iterables.chainedIterable(Arrays.asList(iterables));
	}

	/**
	 * Diese Methode gibt ein umgewandeltes {@link Iterable} zurück, das die vom gegebenen {@link Converter} konvertierten Elemente der gegebenen {@link Iterable}
	 * in der gegebenen Reihenfolge liefert.
	 * 
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable1 erstes {@link Iterable}.
	 * @param iterable2 zweites {@link Iterable}.
	 * @return {@code chained}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable1} bzw. {@code iterable2} {@code null} ist.
	 */
	public static <GItem> Iterable<GItem> chainedIterable(final Iterable<? extends GItem> iterable1, final Iterable<? extends GItem> iterable2)
		throws NullPointerException {
		if (iterable1 == null) throw new NullPointerException("iterable1 = null");
		if (iterable2 == null) throw new NullPointerException("iterable2 = null");
		return Iterables.chainedIterable(Arrays.asList(iterable1, iterable2));
	}

	/**
	 * Diese Methode gibt ein umgewandeltes {@link Iterable} zurück, das die vom gegebenen {@link Converter} konvertierten Elemente der gegebenen {@link Iterable}
	 * in der gegebenen Reihenfolge liefert.
	 * 
	 * @see Iterators#convertedIterator(Converter, Iterator)
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter} sowie der Elemente des gegebenen {@link Iterable}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter} sowie der Elemente des erzeugten {@link Iterable}.
	 * @param converter {@link Converter}.
	 * @param iterable {@link Iterable}.
	 * @return {@code converted}-{@link Iterable}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> Iterable<GOutput> convertedIterable(final Converter<? super GInput, ? extends GOutput> converter,
		final Iterable<? extends GInput> iterable) throws NullPointerException {
		return new BaseIterable<GOutput>() {

			@Override
			public Iterator<GOutput> iterator() {
				return Iterators.convertedIterator(converter, iterable.iterator());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("convertedIterable", converter, iterable);
			}

		};
	}

	/**
	 * Diese Methode gibt eun unveränderliches {@link Iterable} zurück, das die Elemente des gegebenen {@link Iterable} liefert.
	 * 
	 * @see Iterators#unmodifiableIterator(Iterator)
	 * @param <GItem> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@code unmodifiable}-{@link Iterable}.
	 * @throws NullPointerException Wenn {@code iterable} {@code null} ist.
	 */
	public static <GItem> Iterable<GItem> unmodifiableIterable(final Iterable<? extends GItem> iterable) throws NullPointerException {
		if (iterable == null) throw new NullPointerException("iterable = null");
		return new BaseIterable<GItem>() {

			@Override
			public Iterator<GItem> iterator() {
				return Iterators.unmodifiableIterator(iterable.iterator());
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("unmodifiableIterator", iterable);
			}

		};
	}

}
