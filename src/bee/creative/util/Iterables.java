package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Converters.ConverterLink;
import bee.creative.util.Filters.FilterLink;
import bee.creative.util.Iterators.EntryLink;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Iterable}s.
 * 
 * @see Iterator
 * @see Iterators
 * @see Iterable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Iterables {

	/**
	 * Diese Klasse implementiert den leeren {@link Iterable}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class VoidIterable implements Iterable<Object>, UseToString {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Object> iterator() {
			return Iterators.VOID_ITERATOR;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("voidIterable");
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten filternden {@link Iterable}, der nur die vom gegebenen {@link Filter}
	 * akzeptierten Elemente des gegebenen {@link Iterable}s liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class BaseIterable<GEntry> extends FilterLink<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * Dieses Feld speichert den {@link Iterable}.
		 */
		final Iterable<? extends GEntry> iterable;

		/**
		 * Dieser Konstrukteur initialisiert das {@link Filter} und {@link Iterable}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebene {@link Iterable} {@code null}
		 *         ist.
		 */
		public BaseIterable(final Filter<? super GEntry> filter, final Iterable<? extends GEntry> iterable)
			throws NullPointerException {
			super(filter);
			if(iterable == null) throw new NullPointerException("iterable is null");
			this.iterable = iterable;
		}

		/**
		 * Diese Methode gibt den {@link Iterable} zurück.
		 * 
		 * @return {@link Iterable}.
		 */
		public Iterable<? extends GEntry> iterable() {
			return this.iterable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.filter, this.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final BaseIterable<?> data = (BaseIterable<?>)object;
			return super.equals(object) && Objects.equals(this.iterable, data.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#limitedIterable(Filter, Iterable)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static final class LimitedIterableConverter<GEntry> extends FilterLink<GEntry> implements
		Converter<Iterable<? extends GEntry>, Iterable<GEntry>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
		 */
		public LimitedIterableConverter(final Filter<? super GEntry> filter) throws NullPointerException {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterable<GEntry> convert(final Iterable<? extends GEntry> input) {
			return Iterables.limitedIterable(this.filter, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof LimitedIterableConverter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("limitedIterableConverter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#filteredIterable(Filter, Iterable)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static final class FilteredIterableConverter<GEntry> extends FilterLink<GEntry> implements
		Converter<Iterable<? extends GEntry>, Iterable<GEntry>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
		 */
		public FilteredIterableConverter(final Filter<? super GEntry> filter) throws NullPointerException {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterable<GEntry> convert(final Iterable<? extends GEntry> input) {
			return Iterables.filteredIterable(this.filter, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FilteredIterableConverter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("filteredIterableConverter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#convertedIterable(Converter, Iterable)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class ConvertedIterableConverter<GInput extends Iterable<? extends GValue>, GValue, GOutput> extends
		ConverterLink<GValue, GOutput> implements Converter<GInput, Iterable<GOutput>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
		 */
		public ConvertedIterableConverter(final Converter<? super GValue, ? extends GOutput> converter) {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterable<GOutput> convert(final GInput input) {
			return Iterables.convertedIterable(this.converter, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConvertedIterableConverter<?, ?, ?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedIterableConverter", this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterable} über ein gegebenes Element.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elements.
	 */
	public static final class EntryIterable<GEntry> extends EntryLink<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * Dieser Konstrukteur initialisiert das Element.
		 * 
		 * @param entry Element.
		 */
		public EntryIterable(final GEntry entry) {
			super(entry);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.entryIterator(this.entry);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof EntryIterable<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("entryIterable", this.entry);
		}

	}

	/**
	 * Diese Klasse implementiert einen filternden {@link Iterable}, der nur die vom gegebenen {@link Filter} akzeptierten
	 * Elemente des gegebenen {@link Iterable}s liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterable<GEntry> extends BaseIterable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Filter} und {@link Iterable}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebene {@link Iterable} {@code null}
		 *         ist.
		 */
		public LimitedIterable(final Filter<? super GEntry> filter, final Iterable<? extends GEntry> iterable)
			throws NullPointerException {
			super(filter, iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.limitedIterator(this.filter, this.iterable.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof LimitedIterable<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("limitedIterable", this.filter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert einen begrenzten {@link Iterable}, der nur die ersten vom gegebenen {@link Filter}
	 * akzeptierten Elemente des eingegebenen {@link Iterable}s liefert und die Iteration beim ersten abgelehnten Element
	 * abbricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterable<GEntry> extends BaseIterable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Filter} und {@link Iterable}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebene {@link Iterable} {@code null}
		 *         ist.
		 */
		public FilteredIterable(final Filter<? super GEntry> filter, final Iterable<? extends GEntry> iterable)
			throws NullPointerException {
			super(filter, iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return Iterators.filteredIterator(this.filter, this.iterable.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FilteredIterable<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("filteredIterable", this.filter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Iterable}, der alle Elemente der gegebenen {@link Iterable}s in
	 * der gegebenen Reihenfolge liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ChainedIterable<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * Dieses Feld speichert den {@link Iterable} über die verketteten {@link Iterable}.
		 */
		final Iterable<? extends Iterable<? extends GEntry>> iterables;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Iterable}.
		 * 
		 * @param iterables {@link Iterable}-{@link Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
		 */
		public ChainedIterable(final Iterable<? extends Iterable<? extends GEntry>> iterables) throws NullPointerException {
			if(iterables == null) throw new NullPointerException("iterables is null");
			this.iterables = iterables;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final Iterator<GEntry> iterator() {
			return Iterators.chainedIterator(Iterators.convertedIterator(Iterables.<GEntry>iterableIteratorConverter(),
				this.iterables.iterator()));
		}

		/**
		 * Diese Methode gibt den {@link Iterable} über die verketteten {@link Iterable}. zurück.
		 * 
		 * @return {@link Iterable} über die verketteten {@link Iterable}.
		 */
		public Iterable<? extends Iterable<? extends GEntry>> iterables() {
			return this.iterables;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.iterables);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ChainedIterable<?>)) return false;
			final ChainedIterable<?> data = (ChainedIterable<?>)object;
			return Objects.equals(this.iterables, data.iterables);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedIterable", this.iterables);
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Iterable}, der die vom gegebenen {@link Converter}
	 * konvertierten Elemente des gegebenen {@link Iterable}s liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterable}
	 *        s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente.
	 */
	public static final class ConvertedIterable<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Iterable<GOutput>, UseToString {

		/**
		 * Dieses Feld speichert den {@link Iterable}.
		 */
		final Iterable<? extends GInput> iterable;

		/**
		 * Dieser Konstrukteur initialisiert {@link Converter} und {@link Iterable}.
		 * 
		 * @param converter {@link Converter}.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterable} {@code null}
		 *         ist.
		 */
		public ConvertedIterable(final Converter<? super GInput, ? extends GOutput> converter,
			final Iterable<? extends GInput> iterable) throws NullPointerException {
			super(converter);
			if(iterable == null) throw new NullPointerException("iterable is null");
			this.iterable = iterable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GOutput> iterator() {
			return Iterators.convertedIterator(this.converter, this.iterable.iterator());
		}

		/**
		 * Diese Methode gibt den {@link Iterable} zurück.
		 * 
		 * @return {@link Iterable}.
		 */
		public Iterable<? extends GInput> iterable() {
			return this.iterable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.converter, this.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof EntryIterable<?>)) return false;
			final ConvertedIterable<?, ?> data = (ConvertedIterable<?, ?>)object;
			return super.equals(object) && Objects.equals(this.iterable, data.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedIterable", this.converter, this.iterable);
		}

	}

	/**
	 * Dieses Feld speichert den leeren {@link Iterable}.
	 */
	static final Iterable<Object> VOID_ITERABLE = new VoidIterable();

	/**
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#iterable(Iterable)} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> ITERABLE_CONVERTER = new Converter<Iterable<?>, Iterable<?>>() {

		@Override
		public Iterable<?> convert(final Iterable<?> input) {
			return Iterables.iterable(input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("iterableConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der ein {@link Iterable} in einen {@link Iterator} umwandelt.
	 */
	static final Converter<?, ?> ITERABLE_ITERATOR_CONVERTER = new Converter<Iterable<?>, Iterator<?>>() {

		@Override
		public Iterator<?> convert(final Iterable<?> input) {
			return input.iterator();
		}

		@Override
		public String toString() {
			return Objects.toStringCall("iterableIteratorConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#entryIterator(Object)} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> ENTRY_ITERABLE_CONVERTER = new Converter<Object, Iterable<?>>() {

		@Override
		public Iterable<?> convert(final Object input) {
			return Iterables.entryIterable(input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("entryIterableConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#chainedIterable(Iterable)} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> CHAINED_ITERABLE_CONVERTER = new Converter<Iterable<Iterable<?>>, Iterable<?>>() {

		@Override
		public Iterable<?> convert(final Iterable<Iterable<?>> input) {
			return Iterables.chainedIterable(input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("chainedIterableConverter");
		}

	};

	/**
	 * Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterable}
	 * vorkommen, und gibt nur bei Veränderung der {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#retainAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean retainAll(final Collection<?> collection, final Iterable<?> iterable)
		throws NullPointerException {
		if(iterable == null) throw new NullPointerException("iterable is null");
		if(collection == null) throw new NullPointerException("collection is null");
		return Iterators.retainAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}s, die nicht in der gegebenen {@link Collection}
	 * vorkommen, und gibt nur bei Veränderung des {@link Iterable}s {@code true} zurück.
	 * 
	 * @see Iterators#retainAll(Iterator, Collection)
	 * @param iterable {@link Iterable}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean retainAll(final Iterable<?> iterable, final Collection<?> collection)
		throws NullPointerException {
		if(iterable == null) throw new NullPointerException("iterable is null");
		if(collection == null) throw new NullPointerException("collection is null");
		return Iterators.retainAll(iterable.iterator(), collection);
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link Iterable}s in die gegebene {@link Collection} ein und gibt
	 * nur bei Veränderungen an der {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#appendAll(Collection, Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection } {@code null}
	 *         ist.
	 */
	public static <GEntry> boolean appendAll(final Collection<GEntry> collection,
		final Iterable<? extends GEntry> iterable) throws NullPointerException {
		if(iterable == null) throw new NullPointerException("iterable is null");
		if(collection == null) throw new NullPointerException("collection is null");
		return Iterators.appendAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterable}s aus der gegebenen {@link Collection} und gibt
	 * nur bei Veränderungen an der {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#removeAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean removeAll(final Iterable<?> iterable, final Collection<?> collection)
		throws NullPointerException {
		if(iterable == null) throw new NullPointerException("iterable is null");
		if(collection == null) throw new NullPointerException("collection is null");
		return Iterators.removeAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterable}s in der
	 * gegebenen {@link Collection} enthalten sind.
	 * 
	 * @see Iterators#containsAll(Collection, Iterator)
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei vollständiger Inklusion.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean containsAll(final Collection<?> collection, final Iterable<?> iterable)
		throws NullPointerException {
		if(iterable == null) throw new NullPointerException("iterable is null");
		if(collection == null) throw new NullPointerException("collection is null");
		return Iterators.containsAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Iterable} oder den leeren {@link Iterable} zurück.
	 * 
	 * @see Iterables#voidIterable()
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@link Iterable} oder {@code void}-{@link Iterable}.
	 */
	public static <GEntry> Iterable<GEntry> iterable(final Iterable<GEntry> iterable) {
		return ((iterable == null) ? Iterables.<GEntry>voidIterable() : iterable);
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#iterable(Iterable)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Iterables#iterable(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterables#iterable(Iterable)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterable<GEntry>, Iterable<GEntry>> iterableConverter() {
		return (Converter<Iterable<GEntry>, Iterable<GEntry>>)Iterables.ITERABLE_CONVERTER;
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der ein {@link Iterable} in einen {@link Iterator} umwandelt.
	 * 
	 * @see Converter
	 * @see Iterable#iterator()
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterable#iterator()}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterable<? extends GEntry>, Iterator<GEntry>> iterableIteratorConverter() {
		return (Converter<Iterable<? extends GEntry>, Iterator<GEntry>>)Iterables.ITERABLE_ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode gibt den leeren {@link Iterable} zurück.
	 * 
	 * @see Iterators#voidIterator()
	 * @param <GEntry> Typ der Elemente.
	 * @return {@code void}-{@link Iterable}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterable<GEntry> voidIterable() {
		return (Iterable<GEntry>)Iterables.VOID_ITERABLE;
	}

	/**
	 * Diese Methode gibt den {@link Iterable} über das gegebene Element zurück.
	 * 
	 * @see Iterators#entryIterator(Object)
	 * @param <GEntry> Typ des Elements.
	 * @param entry Element.
	 * @return {@link EntryIterable}
	 */
	public static <GEntry> EntryIterable<GEntry> entryIterable(final GEntry entry) {
		return new EntryIterable<GEntry>(entry);
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#entryIterable(Object)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Iterables#entryIterable(Object)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterables#entryIterable(Object)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<GEntry, Iterable<GEntry>> entryIteratorConverter() {
		return (Converter<GEntry, Iterable<GEntry>>)Iterables.ENTRY_ITERABLE_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterable}, der nur die vom gegebenen {@link Filter} akzeptierten
	 * Elemente des gegebenen {@link Iterable}s liefert, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @param iterable {@link Iterable}.
	 * @return {@link LimitedIterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebene {@link Iterable} {@code null} ist.
	 */
	public static <GEntry> LimitedIterable<GEntry> limitedIterable(final Filter<? super GEntry> filter,
		final Iterable<? extends GEntry> iterable) throws NullPointerException {
		return new LimitedIterable<GEntry>(filter, iterable);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#limitedIterable(Filter, Iterable)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @see Converter
	 * @see Iterables#limitedIterable(Filter, Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@link Iterables#limitedIterable(Filter, Iterable)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GEntry> Converter<Iterable<? extends GEntry>, Iterable<GEntry>> limitedIterableConverter(
		final Filter<? super GEntry> filter) throws NullPointerException {
		return new LimitedIterableConverter<GEntry>(filter);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterable}, der nur die vom gegebenen {@link Filter} akzeptierten
	 * Elemente des gegebenen {@link Iterable}s liefert, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @see Iterators#filteredIterator(Filter, Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @param iterable {@link Iterable}.
	 * @return {@link FilteredIterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebene {@link Iterable} {@code null} ist.
	 */
	public static <GEntry> FilteredIterable<GEntry> filteredIterable(final Filter<? super GEntry> filter,
		final Iterable<? extends GEntry> iterable) throws NullPointerException {
		return new FilteredIterable<GEntry>(filter, iterable);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#filteredIterable(Filter, Iterable)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @see Converter
	 * @see Iterables#filteredIterable(Filter, Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@link Iterables#filteredIterable(Filter, Iterable)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GEntry> Converter<Iterable<? extends GEntry>, Iterable<GEntry>> filteredIterableConverter(
		final Filter<? super GEntry> filter) throws NullPointerException {
		return new FilteredIterableConverter<GEntry>(filter);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterable}, der alle Elemente der gegebenen {@link Iterable}s in der
	 * gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterables {@link Iterable}-{@link Iterable}.
	 * @return {@link ChainedIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable}-{@link Iterable} {@code null} ist.
	 */
	public static <GEntry> ChainedIterable<GEntry> chainedIterable(
		final Iterable<? extends Iterable<? extends GEntry>> iterables) throws NullPointerException {
		return new ChainedIterable<GEntry>(iterables);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterable}, der alle Elemente der gegebenen {@link Iterable}s in der
	 * gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see Iterables#chainedIterable(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterables {@link Iterable}-{@link Array}.
	 * @return {@link ChainedIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable}-{@link Array} {@code null} ist.
	 */
	public static <GEntry> ChainedIterable<GEntry> chainedIterable(final Iterable<? extends GEntry>... iterables)
		throws NullPointerException {
		if(iterables == null) throw new NullPointerException("iterables is null");
		return Iterables.chainedIterable(Arrays.asList(iterables));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterable}, der alle Elemente der gegebenen {@link Iterable}s in der
	 * gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see Iterables#chainedIterable(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable1 {@link Iterable} 1.
	 * @param iterable2 {@link Iterable} 2.
	 * @return {@link ChainedIterable}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> ChainedIterable<GEntry> chainedIterable(final Iterable<? extends GEntry> iterable1,
		final Iterable<? extends GEntry> iterable2) {
		return Iterables.chainedIterable(Arrays.asList(iterable1, iterable2));
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#chainedIterable(Iterable)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Iterables#chainedIterable(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterables#chainedIterable(Iterable)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterable<? extends Iterable<? extends GEntry>>, Iterable<GEntry>> chainedIterableConverter() {
		return (Converter<Iterable<? extends Iterable<? extends GEntry>>, Iterable<GEntry>>)Iterables.CHAINED_ITERABLE_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Iterable}, der die vom gegebenen {@link Converter Converter}
	 * konvertierten Elemente des gegebenen {@link Iterable}s liefert, und gibt ihn zurück.
	 * 
	 * @see Iterators#convertedIterator(Converter, Iterator)
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterable
	 *        Iterables}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente des erzeugten {@link Iterable
	 *        Iterables}.
	 * @param converter {@link Converter}.
	 * @param iterable {@link Iterable}.
	 * @return {@link ConvertedIterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterable} {@code null}
	 *         ist.
	 */
	public static <GInput, GOutput> ConvertedIterable<GInput, GOutput> convertedIterable(
		final Converter<? super GInput, ? extends GOutput> converter, final Iterable<? extends GInput> iterable)
		throws NullPointerException {
		return new ConvertedIterable<GInput, GOutput>(converter, iterable);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterables#convertedIterable(Converter, Iterable)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @see Iterables#convertedIterable(Converter, Iterable)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link Iterables#convertedIterable(Converter, Iterable)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput extends Iterable<? extends GValue>, GValue, GOutput> Converter<GInput, Iterable<GOutput>> convertedIterableConverter(
		final Converter<? super GValue, ? extends GOutput> converter) {
		return new ConvertedIterableConverter<GInput, GValue, GOutput>(converter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Iterables() {
	}

}
