package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import bee.creative.util.Converters.BaseConverter;
import bee.creative.util.Converters.ChainedConverter;
import bee.creative.util.Converters.IterableConverter;
import bee.creative.util.Iterators.ChainedIterator;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Iterable
 * Iterablen}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Iterables {

	/**
	 * Diese Klasse implementiert den leeren {@link Iterable Iterable}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	static final class VoidIterable implements Iterable<Object>, UseToString {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Object> iterator() {
			return Iterators.voidIterator();
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
	 * Diese Klasse implementiert einen filternden {@link Iterable Iterable}, der nur die vom gegebenen {@link Filter
	 * Filter} akzeptierten Elemente des gegebenen {@link Iterable Iterables} liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class BaseIterable<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * Dieses Feld speichert den {@link Filter Filter}.
		 */
		final Filter<? super GEntry> filter;

		/**
		 * Dieses Feld speichert den {@link Iterable Iterable}.
		 */
		final Iterable<? extends GEntry> iterable;

		/**
		 * Dieser Konstrukteur initialisiert das {@link Filter Filter} und {@link Iterable Iterable}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param iterable {@link Iterable Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} bzw. der gegebene {@link Iterable Iterable}
		 *         <code>null</code> ist.
		 */
		public BaseIterable(final Filter<? super GEntry> filter, final Iterable<? extends GEntry> iterable)
			throws NullPointerException {
			if((filter == null) || (iterable == null)) throw new NullPointerException();
			this.filter = filter;
			this.iterable = iterable;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterable Iterable} über ein gegebenes Element.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elements.
	 */
	public static final class EntryIterable<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * Dieses Feld speichert das Element.
		 */
		final GEntry entry;

		/**
		 * Dieser Konstrukteur initialisiert das Element.
		 * 
		 * @param entry Element.
		 */
		public EntryIterable(final GEntry entry) {
			this.entry = entry;
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
		public String toString() {
			return Objects.toStringCall("entryIterable", this.entry);
		}

	}

	/**
	 * Diese Klasse implementiert einen filternden {@link Iterable Iterable}, der nur die vom gegebenen {@link Filter
	 * Filter} akzeptierten Elemente des gegebenen {@link Iterable Iterables} liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterable<GEntry> extends BaseIterable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Filter Filter} und {@link Iterable Iterable}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param iterable {@link Iterable Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} bzw. der gegebene {@link Iterable Iterable}
		 *         <code>null</code> ist.
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
		public String toString() {
			return Objects.toStringCall("limitedIterable", this.filter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert einen begrenzten {@link Iterable Iterable}, der nur die ersten vom gegebenen
	 * {@link Filter Filter} akzeptierten Elemente des eingegebenen {@link Iterable Iterables} liefert und die Iteration
	 * beim ersten abgelehnten Element abbricht.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterable<GEntry> extends BaseIterable<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert das {@link Filter Filter} und {@link Iterable Iterable}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param iterable {@link Iterable Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} bzw. der gegebene {@link Iterable Iterable}
		 *         <code>null</code> ist.
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
		public String toString() {
			return Objects.toStringCall("filteredIterable", this.filter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Iterable Iterable}, der alle Elemente der gegebenen
	 * {@link Iterable Iterables} in der gegebenen Reihenfolge liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ChainedIterable<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * Dieses Feld speichert das {@link Iterable Iterable} über die verketteten {@link Iterable Iterable}.
		 */
		final Iterable<? extends Iterable<? extends GEntry>> iterables;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Iterable Iterable}.
		 * 
		 * @param iterables {@link Iterable Iterable}-{@link Iterable Iterable}.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable Iterable}-{@link Iterable Iterable}
		 *         <code>null</code> ist.
		 */
		public ChainedIterable(final Iterable<? extends Iterable<? extends GEntry>> iterables) throws NullPointerException {
			if(iterables == null) throw new NullPointerException();
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
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedIterable", this.iterables);
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Iterable Iterable}, der die vom gegebenen {@link Converter
	 * Converter} konvertierten Elemente des gegebenen {@link Iterable Iterables} liefert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter Converters} sowie der Elemente des gegebenen
	 *        {@link Iterable Iterables}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie der Elemente.
	 */
	public static final class ConvertedIterable<GInput, GOutput> implements Iterable<GOutput>, UseToString {

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieses Feld speichert den {@link Iterable Iterable}.
		 */
		final Iterable<? extends GInput> iterable;

		/**
		 * Dieser Konstrukteur initialisiert {@link Converter Converter} und {@link Iterable Iterable}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @param iterable {@link Iterable Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} bzw. der gegebene {@link Iterable
		 *         Iterable} <code>null</code> ist.
		 */
		public ConvertedIterable(final Converter<? super GInput, ? extends GOutput> converter,
			final Iterable<? extends GInput> iterable) throws NullPointerException {
			if((converter == null) || (iterable == null)) throw new NullPointerException();
			this.converter = converter;
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
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedIterable", this.converter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link convertedIterable} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	public static final class IterableConverter<GInput extends Iterable<? extends GValue>, GValue, GOutput>
	
	extends Converters.BaseConverter<GInput, Iterable<GOutput>, GValue, GOutput> {
	
		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter.}
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 */
		public IterableConverter(final Converter<? super GValue, ? extends GOutput> converter) {
			super(converter);
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterable<GOutput> convert(final GInput input) {
			return convertedIterable(this.converter, input);
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || ((object instanceof IterableConverter<?, ?, ?>) && super.equals(object));
		}
	
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("iterableConverter", this.converter);
		}
	
	}

	/**
	 * Dieses Feld speichert den leeren {@link Iterable Iterable}.
	 */
	static final Iterable<?> VOID_ITERABLE = new VoidIterable();

	/**
	 * Diese Methode gibt den gegebenen {@link Iterable Iterable} oder den leeren {@link Iterable Iterable} zurück.
	 * 
	 * @see Iterables#voidIterable()
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable {@link Iterable Iterable}.
	 * @return {@link Iterable Iterable} oder <code>Void</code>-{@link Iterable Iterable}.
	 */
	public static <GEntry> Iterable<GEntry> iterable(final Iterable<GEntry> iterable) {
		return ((iterable == null) ? Iterables.<GEntry>voidIterable() : iterable);
	}

	/**
	 * Diese Methode gibt den leeren {@link Iterable Iterable} zurück.
	 * 
	 * @see Iterators#voidIterator()
	 * @param <GEntry> Typ der Elemente.
	 * @return <code>Void</code>- {@link Iterable Iterable}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterable<GEntry> voidIterable() {
		return (Iterable<GEntry>)Iterables.VOID_ITERABLE;
	}

	/**
	 * Diese Methode gibt den {@link Iterable Iterable} über das gegebene Element zurück.
	 * 
	 * @see Iterators#entryIterator(Object)
	 * @param <GEntry> Typ des Elements.
	 * @param entry Element.
	 * @return {@link EntryIterable Entry-Iterable}
	 */
	public static <GEntry> Iterable<GEntry> entryIterable(final GEntry entry) {
		return new EntryIterable<GEntry>(entry);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterable Iterable}, der nur die vom gegebenen {@link Filter Filter}
	 * akzeptierten Elemente des gegebenen {@link Iterable Iterables} liefert, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter Filter}.
	 * @param iterable {@link Iterable Iterable}.
	 * @return {@link LimitedIterable Limited-Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} bzw. der gegebene {@link Iterable Iterable}
	 *         <code>null</code> ist.
	 */
	public static <GEntry> Iterable<GEntry> limitedIterable(final Filter<? super GEntry> filter,
		final Iterable<? extends GEntry> iterable) throws NullPointerException {
		return new LimitedIterable<GEntry>(filter, iterable);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterable Iterable}, der nur die vom gegebenen {@link Filter Filter}
	 * akzeptierten Elemente des gegebenen {@link Iterable Iterables} liefert, und gibt ihn zurück.
	 * 
	 * @see Iterators#filteredIterator(Filter, Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter Filter}.
	 * @param iterable {@link Iterable Iterable}.
	 * @return {@link FilteredIterable Filtered-Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} bzw. der gegebene {@link Iterable Iterable}
	 *         <code>null</code> ist.
	 */
	public static <GEntry> Iterable<GEntry> filteredIterable(final Filter<? super GEntry> filter,
		final Iterable<? extends GEntry> iterable) throws NullPointerException {
		return new FilteredIterable<GEntry>(filter, iterable);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterable Iterable}, der alle Elemente der gegebenen {@link Iterable
	 * Iterables} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterables {@link Iterable Iterable}-{@link Iterable Iterable}.
	 * @return {@link ChainedIterator Chained-Iterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable Iterable}-{@link Iterable Iterable}
	 *         <code>null</code> ist.
	 */
	public static <GEntry> Iterable<GEntry> chainedIterable(final Iterable<? extends Iterable<? extends GEntry>> iterables)
		throws NullPointerException {
		return new ChainedIterable<GEntry>(iterables);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterable Iterable}, der alle Elemente der gegebenen {@link Iterable
	 * Iterables} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see Iterables#chainedIterable(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterables {@link Iterable Iterable}-{@link Array Array}.
	 * @return {@link ChainedIterator Chained-Iterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable Iterable}-{@link Array Array} <code>null</code> ist.
	 */
	public static <GEntry> Iterable<GEntry> chainedIterable(final Iterable<? extends GEntry>... iterables)
		throws NullPointerException {
		if(iterables == null) throw new NullPointerException();
		return Iterables.chainedIterable(Arrays.asList(iterables));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterable Iterable}, der alle Elemente der gegebenen {@link Iterable
	 * Iterables} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see Iterables#chainedIterable(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable1 {@link Iterable Iterable} 1.
	 * @param iterable2 {@link Iterable Iterable} 2.
	 * @return {@link ChainedIterator Chained-Iterable}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterable<GEntry> chainedIterable(final Iterable<? extends GEntry> iterable1,
		final Iterable<? extends GEntry> iterable2) {
		return Iterables.chainedIterable(Arrays.asList(iterable1, iterable2));
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Iterable Iterable}, der die vom gegebenen {@link Converter
	 * Converter} konvertierten Elemente des gegebenen {@link Iterable Iterables} liefert, und gibt ihn zurück.
	 * 
	 * @see Iterators#convertedIterator(Converter, Iterator)
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter Converters} sowie der Elemente des gegebenen
	 *        {@link Iterable Iterables}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie der Elemente des erzeugten
	 *        {@link Iterable Iterables}.
	 * @param converter {@link Converter Converter}.
	 * @param iterable {@link Iterable Iterable}.
	 * @return {@link ConvertedIterable Converted-Iterable}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} bzw. der gegebene {@link Iterable
	 *         Iterable} <code>null</code> ist.
	 */
	public static <GInput, GOutput> Iterable<GOutput> convertedIterable(
		final Converter<? super GInput, ? extends GOutput> converter, final Iterable<? extends GInput> iterable)
		throws NullPointerException {
		return new ConvertedIterable<GInput, GOutput>(converter, iterable);
	}

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der ein {@link Iterable Iterable} in einen {@link Iterator
	 * Iterator} umwandelt.
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
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link chainedIterable} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> ITERABLE_ITERABLE_ITERABLE_CONVERTER =
		new Converter<Iterable<Iterable<?>>, Iterable<?>>() {
	
			@Override
			public Iterable<?> convert(final Iterable<Iterable<?>> input) {
				return chainedIterable(input);
			}
	
			@Override
			public String toString() {
				return Objects.toStringCall("iterableIterableIterableConverter");
			}
	
		};

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Iterables() {
	}

	/**
	 * Diese Methode gibt einen {@link Converter Converter} zurück, der ein {@link Iterable Iterable} in einen
	 * {@link Iterator Iterator} umwandelt.
	 * 
	 * @see Iterable#iterator()
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterable Iterable}-{@link Iterator Iterator}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterable<? extends GEntry>, Iterator<GEntry>> iterableIteratorConverter() {
		return (Converter<Iterable<? extends GEntry>, Iterator<GEntry>>)Iterables.ITERABLE_ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode gibt einen {@link Converter Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link chainedIterable} in seine Ausgabe überführt.
	 * 
	 * @see chainedIterable
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link chainedIterable Iterable}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterable<? extends Iterable<? extends GEntry>>, Iterable<GEntry>> iterableIterableIterableConverter() {
		return (Converter<Iterable<? extends Iterable<? extends GEntry>>, Iterable<GEntry>>)Iterables.ITERABLE_ITERABLE_ITERABLE_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link convertedIterable} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter Converter.}
	 * @return {@link IterableConverter Iterable-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput extends Iterable<? extends GValue>, GValue, GOutput> Converter<GInput, Iterable<GOutput>> iterableConverter(
		final Converter<? super GValue, ? extends GOutput> converter) {
		return new IterableConverter<GInput, GValue, GOutput>(converter);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter Converter} und gibt ihn zurück.
	 * 
	 * @see Converters#chainedConverter(Converter, Converter)
	 * @see iterableConverter
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter Converters}.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter Converters} sowie der Eingabe des zweiten
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter Converters}.
	 * @param converter1 erster {@link Converter Converter}.
	 * @param converter2 zweiter {@link Converter Converter.}
	 * @return {@link Converters.ChainedConverter Chained-Converter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GValue, GOutput> Converter<GInput, Iterable<GOutput>> chainedIterableConverter(
		final Converter<? super GInput, ? extends Iterable<GValue>> converter1,
		final Converter<? super GValue, ? extends GOutput> converter2) {
		return Converters.<GInput, Iterable<GValue>, Iterable<GOutput>>chainedConverter(converter1,
			<Iterable<GValue>, GValue, GOutput>iterableConverter(converter2));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter Converter} und gibt ihn zurück.
	 * 
	 * @see Converters#chainedConverter(Converter, Converter)
	 * @see Iterables#chainedIterableConverter(Converter, Converter)
	 * @see iterableIterableIterableConverter
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter Converters}.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter Converters} sowie der Eingabe des zweiten
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter Converters}.
	 * @param converter1 erster {@link Converter Converter}.
	 * @param converter2 zweiter {@link Converter Converter.}
	 * @return {@link Converters.ChainedConverter Chained-Converter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GValue, GOutput> Converter<GInput, Iterable<GOutput>> chainedIterableIterableConverter(
		final Converter<? super GInput, ? extends Iterable<GValue>> converter1,
		final Converter<? super GValue, ? extends Iterable<GOutput>> converter2) {
		return Converters.chainedConverter(Iterables.chainedIterableConverter(converter1, converter2),
			<GOutput>iterableIterableIterableConverter());
	}

}
