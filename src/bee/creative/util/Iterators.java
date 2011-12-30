package bee.creative.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Iterator
 * Iteratoren}.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */

public final class Iterators {

	/**
	 * Diese Klasse implementiert einen {@link Iterator Iterator} mit {@link Filter Filter}.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class BaseIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert <code>true</code>, wenn ein nächstes Element existiert.
		 */
		Boolean hasNext;

		/**
		 * Dieses Feld speichert das nächste Element;
		 */
		GEntry entry;

		/**
		 * Dieses Feld speichert den {@link Filter Filter}.
		 */
		final Filter<? super GEntry> filter;

		/**
		 * Dieses Feld speichert den {@link Iterator Iterator};
		 */
		final Iterator<? extends GEntry> iterator;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter Filter} und {@link Iterator Iterator}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param iterator {@link Iterator Iterator}.
		 * @throws NullPointerException Wenn {@link Filter Filter} oder {@link Iterator Iterator} <code>null</code> sind.
		 */
		public BaseIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
			throws NullPointerException {
			if((filter == null) || (iterator == null)) throw new NullPointerException();
			this.filter = filter;
			this.iterator = iterator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			if(!this.hasNext()) throw new NoSuchElementException();
			this.hasNext = null;
			return this.entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if(this.hasNext != null) throw new IllegalStateException();
			this.iterator.remove();
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator Iterator} mit {@link Filter Filter}.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class BaseIteratorConverter<GEntry> implements
		Converter<Iterator<? extends GEntry>, Iterator<GEntry>> {

		/**
		 * Dieses Feld speichert den {@link Filter Filter}.
		 */
		final Filter<? super GEntry> filter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Filter Filter} <code>null</code> ist.
		 */
		public BaseIteratorConverter(final Filter<? super GEntry> filter) throws NullPointerException {
			if(filter == null) throw new NullPointerException();
			this.filter = filter;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator Iterator} über ein Element.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elementes.
	 */
	public static final class EntryIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert den Zustand.
		 */
		boolean hasNext;

		/**
		 * Dieses Feld speichert das Element.
		 */
		final GEntry entry;

		/**
		 * Dieser Konstrukteur initialisiert das Element.
		 * 
		 * @param entry Element
		 */
		public EntryIterator(final GEntry entry) {
			this.entry = entry;
			this.hasNext = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.hasNext;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			if(!this.hasNext) throw new NoSuchElementException();
			this.hasNext = false;
			return this.entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw (this.hasNext ? new IllegalStateException() : new UnsupportedOperationException());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("entryIterator", this.entry);
		}

	}

	/**
	 * Diese Klasse implementiert einen begrenzten {@link Iterator Iterator}, der nur die ersten vom gegebenen
	 * {@link Filter Filter} akzeptierten Elemente des eingegebenen {@link Iterator Iterators} liefert und die Iteration
	 * beim ersten abgelehnten Element abbricht.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterator<GEntry> extends BaseIterator<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter Filter} und {@link Iterator Iterator}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param iterator {@link Iterator Iterator}.
		 * @throws NullPointerException Wenn {@link Filter Filter} oder {@link Iterator Iterator} <code>null</code> sind.
		 */
		public LimitedIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
			throws NullPointerException {
			super(filter, iterator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			if(this.hasNext != null) return this.hasNext.booleanValue();
			if(!this.iterator.hasNext()) return (this.hasNext = Boolean.FALSE).booleanValue();
			return (this.hasNext = Boolean.valueOf(this.filter.accept(this.entry = this.iterator.next()))).booleanValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("limitedIterator", this.filter, this.iterator);
		}

	}

	/**
	 * Diese Klasse implementiert einen filternden {@link Iterator Iterator}, der nur die vom gegebenen {@link Filter
	 * Filter} akzeptierten Elemente des eingegebenen {@link Iterator Iterators} liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterator<GEntry> extends BaseIterator<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter Filter} und {@link Iterator Iterator}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param iterator {@link Iterator Iterator}.
		 * @throws NullPointerException Wenn {@link Filter Filter} oder {@link Iterator Iterator} <code>null</code> sind.
		 */
		public FilteredIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
			throws NullPointerException {
			super(filter, iterator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			if(this.hasNext != null) return this.hasNext.booleanValue();
			while(this.iterator.hasNext())
				if(this.filter.accept(this.entry = this.iterator.next())) return (this.hasNext = Boolean.TRUE).booleanValue();
			return (this.hasNext = Boolean.FALSE).booleanValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("filteredIterator", this.filter, this.iterator);
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Iterator Iterator}. Der {@link Iterator Iterator} läuft über
	 * alle Elemente der eingegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge. Wenn einer der
	 * eingegebenen {@link Iterator Iteratoren} <code>null</code> ist, wird dieser ausgelassen.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ChainedIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert den aktiven {@link Iterator Iterator}.
		 */
		Iterator<? extends GEntry> iterator;

		/**
		 * Dieses Feld speichert den {@link Iterator Iterator} über die {@link Iterator Iteratoren}.
		 */
		final Iterator<? extends Iterator<? extends GEntry>> iterators;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Iterator Iterator} über die {@link Iterator Iteratoren}.
		 * 
		 * @param iterators {@link Iterator Iterator} über die {@link Iterator Iteratoren}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator Iterator} <code>null</code> ist.
		 */
		public ChainedIterator(final Iterator<? extends Iterator<? extends GEntry>> iterators) throws NullPointerException {
			if(iterators == null) throw new NullPointerException();
			this.iterators = iterators;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			if(this.iterator == null) throw new NoSuchElementException();
			return this.iterator.next();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			while(true){
				while(this.iterator == null){
					if(!this.iterators.hasNext()) return false;
					this.iterator = this.iterators.next();
				}
				if(this.iterator.hasNext()) return true;
				this.iterator = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if(this.iterator == null) throw new IllegalStateException();
			this.iterator.remove();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedIterator", this.iterators);
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Iterator Iterator},der die vom gegebenen {@link Converter
	 * Converter} konvertierten Elemente des gegebenen {@link Iterator Iterators} liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter Converters} sowie der Elemente des gegebenen
	 *        {@link Iterable Iterables}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie der Elemente.
	 */
	public static final class ConvertedIterator<GInput, GOutput> implements Iterator<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieses Feld speichert den {@link Iterator Iterator};
		 */
		final Iterator<? extends GInput> iterator;

		/**
		 * Dieser Konstrukteur initialisiert {@link Converter Converter} und {@link Iterator Iterator}.
		 * 
		 * @param converter {@link Converter Converter}.
		 * @param iterator {@link Iterator Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} bzw. der gegebene {@link Iterable
		 *         Iterable} <code>null</code> ist.
		 */
		public ConvertedIterator(final Converter<? super GInput, ? extends GOutput> converter,
			final Iterator<? extends GInput> iterator) throws NullPointerException {
			if((converter == null) || (iterator == null)) throw new NullPointerException();
			this.converter = converter;
			this.iterator = iterator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GOutput next() {
			return this.converter.convert(this.iterator.next());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			this.iterator.remove();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedIterator", this.converter, this.iterator);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#convertedIterator(Converter, Iterator)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class ConvertedIteratorConverter<GInput extends Iterator<? extends GValue>, GValue, GOutput> extends
		Converters.BaseConverter<GInput, Iterator<GOutput>, GValue, GOutput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter Converter}.
		 * 
		 * @param converter {@link Converter Converter.}
		 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
		 */
		public ConvertedIteratorConverter(final Converter<? super GValue, ? extends GOutput> converter) {
			super(converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GOutput> convert(final GInput input) {
			return Iterators.convertedIterator(this.converter, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || ((object instanceof ConvertedIteratorConverter<?, ?, ?>) && super.equals(object));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("iteratorConverter", this.converter);
		}

	}

	/**
	 * Dieses Feld speichert den leeren {@link Iterator Iterator}.
	 */
	static final Iterator<?> VOID_ITERATOR = new Iterator<Object>() {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new IllegalStateException();
		}

		@Override
		public String toString() {
			return Objects.toStringCall("voidIterator");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#iterator(Iterator)} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> ITERATOR_CONVERTER = new Converter<Iterator<?>, Iterator<?>>() {

		@Override
		public Iterator<?> convert(final Iterator<?> input) {
			return Iterators.iterator(input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("iteratorConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#entryIterator(Object)} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> ENTRY_ITERATOR_CONVERTER = new Converter<Object, Iterator<?>>() {

		@Override
		public Iterator<?> convert(final Object input) {
			return Iterators.entryIterator(input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("entryIteratorConverter");
		}

	};

	/**
	 * Dieses Feld speichert den {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#chainedIterator} in seine Ausgabe überführt.
	 */
	static final Converter<?, ?> CHAINED_ITERATOR_CONVERTER = new Converter<Iterator<Iterator<?>>, Iterator<?>>() {

		@Override
		public Iterator<?> convert(final Iterator<Iterator<?>> input) {
			return Iterators.chainedIterator(input);
		}

		@Override
		public String toString() {
			return Objects.toStringCall("chainedIteratorConverter");
		}

	};

	/**
	 * Diese Methode gibt den gegebenen {@link Iterator Iterator} oder den leeren {@link Iterator Iterator} zurück.
	 * 
	 * @see Iterators#voidIterator()
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator Iterator}.
	 * @return {@link Iterator Iterator} oder <code>Void</code>-{@link Iterator Iterator}.
	 */
	public static final <GEntry> Iterator<GEntry> iterator(final Iterator<GEntry> iterator) {
		return ((iterator == null) ? Iterators.<GEntry>voidIterator() : iterator);
	}

	/**
	 * Diese Methode gibt den {@link Converter Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#iterator(Iterator)} in seine Ausgabe überführt.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterators#iterator(Iterator)}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterator<GEntry>, Iterator<GEntry>> iteratorConverter() {
		return (Converter<Iterator<GEntry>, Iterator<GEntry>>)Iterators.ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode gibt den leeren {@link Iterator Iterator} zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @return <code>Void</code>-{@link Iterator Iterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static final <GEntry> Iterator<GEntry> voidIterator() {
		return (Iterator<GEntry>)Iterators.VOID_ITERATOR;
	}

	/**
	 * Diese Methode gibt den {@link Iterator Iterator} über das gegebene Element zurück.
	 * 
	 * @param <GEntry> Typ des Elements.
	 * @param entry Element.
	 * @return Element-{@link Iterator Iterator}
	 */
	public static final <GEntry> Iterator<GEntry> entryIterator(final GEntry entry) {
		return new EntryIterator<GEntry>(entry);
	}

	/**
	 * Diese Methode gibt den {@link Converter Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#entryIterator(Object)} in seine Ausgabe überführt.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterators#entryIterator(Object)}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<GEntry, Iterator<GEntry>> entryIteratorConverter() {
		return (Converter<GEntry, Iterator<GEntry>>)Iterators.ENTRY_ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen begrenzten {@link Iterator Iterator}, der nur die ersten vom gegebenen {@link Filter
	 * Filter} akzeptierten Elemente des gegebenen {@link Iterator Iterators} liefert sowie die Iteration beim ersten
	 * abgelehnten Element abbricht, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator Iterator}.
	 * @param filter {@link Filter Filter}.
	 * @return {@link LimitedIterator Limited-Iterator}.
	 * @throws NullPointerException Wenn {@link Filter Filter} oder {@link Iterator Iterator} <code>null</code> sind.
	 */
	public static final <GEntry> Iterator<GEntry> limitedIterator(final Filter<? super GEntry> filter,
		final Iterator<? extends GEntry> iterator) throws NullPointerException {
		return new LimitedIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterator Iterator}, der nur die vom gegebenen {@link Filter Filter}
	 * akzeptierten Elemente des gegebenen {@link Iterator Iterators} liefert, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator Iterator}.
	 * @param filter {@link Filter Filter}.
	 * @return {@link FilteredIterator Filtered-Iterator}.
	 * @throws NullPointerException Wenn {@link Filter Filter} oder {@link Iterator Iterator} <code>null</code> sind.
	 */
	public static final <GEntry> Iterator<GEntry> filteredIterator(final Filter<? super GEntry> filter,
		final Iterator<? extends GEntry> iterator) throws NullPointerException {
		return new FilteredIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see Iterators#chainedIterator(Iterator)
	 * @see Iterators#chainedIterator(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterator Iterator}-{@link Array Array}.
	 * @return {@link ChainedIterator Chained-Iterator}.
	 * @throws NullPointerException Wenn die gegebenen {@link Iterator Iteratoren} <code>null</code> sind.
	 */
	public static final <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends GEntry>... iterators)
		throws NullPointerException {
		if(iterators == null) throw new NullPointerException();
		return Iterators.chainedIterator(Arrays.asList(iterators));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see Iterators#chainedIterator(Iterator)
	 * @see Iterators#chainedIterator(Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator1 {@link Iterator Iterator} 1.
	 * @param iterator2 {@link Iterator Iterator} 2.
	 * @return {@link ChainedIterator Chained-Iterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static final <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends GEntry> iterator1,
		final Iterator<? extends GEntry> iterator2) {
		return Iterators.chainedIterator(Arrays.asList(iterator1, iterator2));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterator Iterator} über die {@link Iterator Iteratoren}.
	 * @return {@link ChainedIterator Chained-Iterator}.
	 * @throws NullPointerException Wenn de rgegebene {@link Iterator Iterator} <code>null</code> ist.
	 */
	public static final <GEntry> Iterator<GEntry> chainedIterator(
		final Iterator<? extends Iterator<? extends GEntry>> iterators) throws NullPointerException {
		return new ChainedIterator<GEntry>(iterators);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterable Iterable} über die {@link Iterator Iteratoren}.
	 * @return {@link ChainedIterator Chained-Iterator}.
	 */
	public static final <GEntry> Iterator<GEntry> chainedIterator(
		final Iterable<? extends Iterator<? extends GEntry>> iterators) {
		if(iterators == null) throw new NullPointerException();
		return Iterators.chainedIterator(iterators.iterator());
	}

	/**
	 * Diese Methode gibt einen {@link Converter Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#chainedIterator(Iterator)} in seine Ausgabe überführt.
	 * 
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterators#chainedIterator(Iterator) Iterator}-{@link Converter Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterator<? extends Iterator<? extends GEntry>>, Iterator<GEntry>> chainedIteratorConverter() {
		return (Converter<Iterator<? extends Iterator<? extends GEntry>>, Iterator<GEntry>>)Iterators.CHAINED_ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter Converter} und gibt ihn zurück.
	 * 
	 * @see Converters#chainedConverter(Converter, Converter)
	 * @see Iterators#convertedIteratorConverter(Converter)
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter Converters}.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter Converters} sowie der Eingabe des zweiten
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter Converters}.
	 * @param converter1 erster {@link Converter Converter}.
	 * @param converter2 zweiter {@link Converter Converter.}
	 * @return {@link Converters.ChainedConverter Chained-Converter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GValue, GOutput> Converter<GInput, Iterator<GOutput>> chainedIteratorConverter(
		final Converter<? super GInput, ? extends Iterator<GValue>> converter1,
		final Converter<? super GValue, ? extends GOutput> converter2) {
		return Converters.<GInput, Iterator<GValue>, Iterator<GOutput>>chainedConverter(converter1,
			Iterators.<Iterator<GValue>, GValue, GOutput>convertedIteratorConverter(converter2));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Converter Converter} und gibt ihn zurück.
	 * 
	 * @see Converters#chainedConverter(Converter, Converter)
	 * @see Iterators#chainedIteratorConverter(Converter, Converter)
	 * @see Iterators#chainedIteratorConverter()
	 * @param <GInput> Typ der Eingabe sowie der Eingabe des ersten {@link Converter Converters}.
	 * @param <GValue> Typ der Ausgabe des ersten {@link Converter Converters} sowie der Eingabe des zweiten
	 *        {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe sowie der Ausgabe des zweiten {@link Converter Converters}.
	 * @param converter1 erster {@link Converter Converter}.
	 * @param converter2 zweiter {@link Converter Converter.}
	 * @return {@link Converters.ChainedConverter Chained-Converter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput, GValue, GOutput> Converter<GInput, Iterator<GOutput>> chainedIteratorIteratorConverter(
		final Converter<? super GInput, ? extends Iterator<GValue>> converter1,
		final Converter<? super GValue, ? extends Iterator<GOutput>> converter2) {
		return Converters.chainedConverter(Iterators.chainedIteratorConverter(converter1, converter2),
			Iterators.<GOutput>chainedIteratorConverter());
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Iterator Iterator}, der die vom gegebenen {@link Converter
	 * Converter} konvertierten Elemente des gegebenen {@link Iterator Iterators} liefert, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter Converters} sowie der Elemente des gegebenen
	 *        {@link Iterator Iterators}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie der Elemente des erzeugten
	 *        {@link Iterator Iterators}.
	 * @param iterator {@link Iterator Iterator}.
	 * @param converter {@link Converter Converter}.
	 * @return {@link ConvertedIterator Converted-Iterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} bzw. der gegebene {@link Iterable
	 *         Iterable} <code>null</code> ist.
	 */
	public static final <GInput, GOutput> Iterator<GOutput> convertedIterator(
		final Converter<? super GInput, ? extends GOutput> converter, final Iterator<? extends GInput> iterator)
		throws NullPointerException {
		return new ConvertedIterator<GInput, GOutput>(converter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#convertedIterator(Converter, Iterator)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter Converter.}
	 * @return {@link ConvertedIteratorConverter Iterator-Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter Converter} <code>null</code> ist.
	 */
	public static <GInput extends Iterator<? extends GValue>, GValue, GOutput> Converter<GInput, Iterator<GOutput>> convertedIteratorConverter(
		final Converter<? super GValue, ? extends GOutput> converter) {
		return new ConvertedIteratorConverter<GInput, GValue, GOutput>(converter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Iterators() {
	}

}
