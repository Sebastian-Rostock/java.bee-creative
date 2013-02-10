package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Iterator}en.
 * 
 * @see Iterator
 * @see Iterable
 * @see Iterables
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Iterators {

	/**
	 * Diese Klasse implementiert ein Objekt mit Element.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elementes.
	 */
	static class EntryLink<GEntry> {

		/**
		 * Dieses Feld speichert das Element.
		 */
		final GEntry entry;

		/**
		 * Dieser Konstrukteur initialisiert das Element.
		 * 
		 * @param entry Element
		 */
		public EntryLink(final GEntry entry) {
			this.entry = entry;
		}

		/**
		 * Diese Methode gibt das Element zurück.
		 * 
		 * @return Element.
		 */
		public GEntry entry() {
			return this.entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.entry);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final EntryLink<?> data = (EntryLink<?>)object;
			return Objects.equals(this.entry, data.entry);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Iterator} mit {@link Filter}.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class AbstractIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GEntry> filter;

		/**
		 * Dieses Feld speichert den {@link Iterator}.
		 */
		final Iterator<? extends GEntry> iterator;

		/**
		 * Dieses Feld speichert {@code true}, wenn ein nächstes Element existiert.
		 */
		Boolean hasNext;

		/**
		 * Dieses Feld speichert das nächste Element.
		 */
		GEntry entry;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebenen {@link Iterator} {@code null} ist.
		 */
		public AbstractIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator) throws NullPointerException {
			if(filter == null) throw new NullPointerException("filter is null");
			if(iterator == null) throw new NullPointerException("iterator is null");
			this.filter = filter;
			this.iterator = iterator;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final GEntry next() {
			if(!this.hasNext()) throw new NoSuchElementException();
			this.hasNext = null;
			return this.entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void remove() {
			if(this.hasNext != null) throw new IllegalStateException();
			this.iterator.remove();
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator} über ein Element, das über die {@link Builder}-Schnittstelle bereitgestellt wird.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elementes.
	 */
	public static abstract class EntryIterator<GEntry> implements Iterator<GEntry>, Builder<GEntry> {

		public static final class EntryValueIterator<GEntry> extends EntryIterator<GEntry> {
		
			final GEntry entry;
		
			public EntryValueIterator(GEntry entry) {
				this.entry = entry;
			}
		
			@Override
			public GEntry create() {
				return entry;
			}
		
			@Override
			public String toString() {
				return Objects.toStringCall(this, entry);
			}
		
		}

		public static final class EntryBuilderIterator<GEntry> extends EntryIterator<GEntry> {

			final Builder<? extends GEntry> builder;

			public EntryBuilderIterator(Builder<? extends GEntry> builder) throws NullPointerException {
				if(builder == null) throw new NullPointerException();
				this.builder = builder;
			}

			@Override
			public GEntry create() {
				return builder.create();
			}

			@Override
			public String toString() {
				return Objects.toStringCall(this, builder);
			}
		}

		/**
		 * Diese Methode gibt den {@link Iterator} über das gegebene Element zurück.
		 * 
		 * @param <GEntry> Typ des Elements.
		 * @param entry Element.
		 * @return {@link EntryIterator}
		 */
		public static <GEntry> EntryIterator<GEntry> of(final GEntry entry) {
			return new EntryValueIterator<GEntry>(entry);
		}

		public static <GEntry> EntryIterator<GEntry> of(final Builder<? extends GEntry> builder) throws NullPointerException {
			return new EntryBuilderIterator<GEntry>(builder);
		}

		/**
		 * Dieses Feld speichert den Zustand.
		 */
		boolean hasNext = true;

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
			return this.create();
		}

		/**
		 * Diese Methode gibt das Element zurück.
		 */
		@Override
		public abstract GEntry create();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw (this.hasNext ? new IllegalStateException() : new UnsupportedOperationException());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der eine begrenzte Anzahl der Elemente eines gegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class CountIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert die maximale Anzahl der verbleibenden Elementen.
		 */
		int count;

		/**
		 * Dieses Feld speichert den {@link Iterator}.
		 */
		final Iterator<? extends GEntry> iterator;

		/**
		 * Dieser Konstrukteur initialisiert Anzahl und {@link Iterator}.
		 * 
		 * @param count Anzahl der maximal vom gegebenen {@link Iterator} gelieferten Elemente.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public CountIterator(final int count, final Iterator<? extends GEntry> iterator) throws NullPointerException, IllegalArgumentException {
			if(count < 0) throw new IllegalArgumentException("count out of range: " + count);
			if(iterator == null) throw new NullPointerException("iterator is null");
			this.count = count;
			this.iterator = iterator;
		}

		/**
		 * Diese Methode gibt die maximale Anzahl der verbleibenden Elementen zurück.
		 * 
		 * @return maximal verbleibende Anzahl.
		 */
		public int count() {
			return this.count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return (this.count > 0) && this.iterator.hasNext();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final GEntry next() {
			if(this.count == 0) throw new NoSuchElementException();
			this.count--;
			return this.iterator.next();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final void remove() {
			this.iterator.remove();
		}

	}

	/**
	 * Diese Klasse implementiert einen begrenzten {@link Iterator}, der nur die ersten vom gegebenen {@link Filter} akzeptierten Elemente des eingegebenen {@link Iterator}s liefert und die Iteration beim ersten abgelehnten Element abbricht.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterator<GEntry> extends AbstractIterator<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
		 */
		public LimitedIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator) throws NullPointerException {
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
	 * Diese Klasse implementiert einen filternden {@link Iterator}, der nur die vom gegebenen {@link Filter} akzeptierten Elemente des eingegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterator<GEntry> extends AbstractIterator<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
		 */
		public FilteredIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator) throws NullPointerException {
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
	 * Diese Klasse implementiert einen verketteten {@link Iterator}. Der {@link Iterator} läuft über alle Elemente der eingegebenen {@link Iterator}en in der gegebenen Reihenfolge. Wenn einer der eingegebenen {@link Iterator}en {@code null} ist, wird dieser ausgelassen.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ChainedIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert den aktiven {@link Iterator}.
		 */
		Iterator<? extends GEntry> iterator;

		/**
		 * Dieses Feld speichert den {@link Iterator} über die {@link Iterator}en.
		 */
		final Iterator<? extends Iterator<? extends GEntry>> iterators;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Iterator} über die {@link Iterator}en.
		 * 
		 * @param iterators {@link Iterator} über die {@link Iterator}en.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 */
		public ChainedIterator(final Iterator<? extends Iterator<? extends GEntry>> iterators) throws NullPointerException {
			if(iterators == null) throw new NullPointerException("iterators is null");
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
		public boolean equals(final Object object) {
			return object == this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("chainedIterator", this.iterators);
		}

		/**
		 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
		 * 
		 * @see Iterators.ChainedIterator
		 * @see ChainedIterator#chainedIterator(Iterator)
		 * @param <GEntry> Typ der Elemente.
		 * @param iterators {@link Iterator}-Array.
		 * @return {@link Iterators.ChainedIterator}.
		 * @throws NullPointerException Wenn die gegebenen {@link Iterator}en {@code null} sind.
		 */
		public static <GEntry> Iterators.ChainedIterator<GEntry> chainedIterator(final Iterator<? extends GEntry>... iterators) throws NullPointerException {
			if(iterators == null) throw new NullPointerException("iterators is null");
			return ChainedIterator.chainedIterator(Arrays.asList(iterators));
		}

		/**
		 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
		 * 
		 * @see Iterators.ChainedIterator
		 * @see ChainedIterator#chainedIterator(Iterator)
		 * @param <GEntry> Typ der Elemente.
		 * @param iterator1 {@link Iterator} 1.
		 * @param iterator2 {@link Iterator} 2.
		 * @return {@link Iterators.ChainedIterator}.
		 */
		@SuppressWarnings ("unchecked")
		public static <GEntry> Iterators.ChainedIterator<GEntry> chainedIterator(final Iterator<? extends GEntry> iterator1,
			final Iterator<? extends GEntry> iterator2) {
			return ChainedIterator.chainedIterator(Arrays.asList(iterator1, iterator2));
		}

		/**
		 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
		 * 
		 * @see Iterators.ChainedIterator
		 * @param <GEntry> Typ der Elemente.
		 * @param iterators {@link Iterator} über die {@link Iterator}en.
		 * @return {@link Iterators.ChainedIterator}.
		 * @throws NullPointerException Wenn de rgegebene {@link Iterator} {@code null} ist.
		 */
		public static <GEntry> Iterators.ChainedIterator<GEntry> chainedIterator(final Iterator<? extends Iterator<? extends GEntry>> iterators)
			throws NullPointerException {
			return new Iterators.ChainedIterator<GEntry>(iterators);
		}

		/**
		 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
		 * 
		 * @see Iterators.ChainedIterator
		 * @see ChainedIterator#chainedIterator(Iterator)
		 * @param <GEntry> Typ der Elemente.
		 * @param iterators {@link Iterable} über die {@link Iterator}en.
		 * @return {@link Iterators.ChainedIterator}.
		 */
		public static <GEntry> Iterators.ChainedIterator<GEntry> chainedIterator(final Iterable<? extends Iterator<? extends GEntry>> iterators)
			throws NullPointerException {
			return ChainedIterator.chainedIterator(iterators.iterator());
		}

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Iterator},der die vom gegebenen {@link Converter} konvertierten Elemente des gegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator} s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente.
	 */
	public static final class ConvertedIterator<GInput, GOutput> implements Iterator<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Iterator};
		 */
		final Iterator<? extends GInput> iterator;

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Converter} und {@link Iterator}.
		 * 
		 * @param converter {@link Converter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterator} {@code null} ist.
		 */
		public ConvertedIterator(final Converter<? super GInput, ? extends GOutput> converter, final Iterator<? extends GInput> iterator)
			throws NullPointerException {
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
			if(iterator == null) throw new NullPointerException("iterator is null");
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
	 * Diese Klasse implementiert einen konvertierenden {@link Iterator},der die vom gegebenen {@link Converter} konvertierten Elemente des gegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator} s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente.
	 */
	public static abstract class ConvertedIterator2<GInput, GOutput> implements Iterator<GOutput>, Converter<GInput, GOutput> {

		public static <GInput, GOutput> Iterator<GOutput> valueOf(final Converter<? super GInput, ? extends GOutput> converter, Iterator<? extends GInput> iterator) { // iterator not final
			return new ConvertedIterator2<GInput, GOutput>(iterator) {

				@Override
				public GOutput convert(GInput input) {
					return converter.convert(input);
				}

			};
		}

		/**
		 * Dieses Feld speichert den {@link Iterator};
		 */
		final Iterator<? extends GInput> iterator;

		/**
		 * Dieser Konstrukteur initialisiert {@link Converter} und {@link Iterator}.
		 * 
		 * @param converter {@link Converter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterator} {@code null} ist.
		 */
		public ConvertedIterator2(final Iterator<? extends GInput> iterator) throws NullPointerException {
			if(iterator == null) throw new NullPointerException("iterator is null");
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
			return this.convert(this.iterator.next());
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
			return Objects.toStringCall("convertedIterator", this.iterator);
		}

	}

	/**
	 * Diese Klasse implementiert einen unmodifizierbaren {@link Iterator}.
	 * 
	 * @see Iterator#remove()
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class UnmodifiableIterator<GEntry> implements Iterator<GEntry> {

		/**
		 * Diese Methode erzeugt einen unmodifizierbaren {@link Iterator}, und gibt ihn zurück.
		 * 
		 * @see Iterator#remove()
		 * @param <GEntry> Typ der Elemente.
		 * @param iterator {@link Iterator}.
		 * @return {@link Iterators.UnmodifiableIterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 */
		public static <GEntry> Iterators.UnmodifiableIterator<GEntry> of(final Iterator<? extends GEntry> iterator) throws NullPointerException {
			return new UnmodifiableIterator<GEntry>(iterator);
		}

		/**
		 * Dieses Feld speichert den {@link Iterator};
		 */
		final Iterator<? extends GEntry> iterator;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Iterator}.
		 * 
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
		 */
		public UnmodifiableIterator(final Iterator<? extends GEntry> iterator) throws NullPointerException {
			if(iterator == null) throw new NullPointerException("iterator is null");
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
		public GEntry next() {
			return this.iterator.next();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.iterator);
		}

	}

	/**
	 * Dieses Feld speichert den leeren {@link Iterator}.
	 */
	static final Iterator<Object> VOID_ITERATOR = new Iterator<Object>() {

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
	 * Diese Methode gibt das {@code index}-te Elemente des gegebenen {@link Iterator}s zurück.
	 * 
	 * @param <GItem> Typ des Elements.
	 * @see Iterators#skip(Iterator, int)
	 * @param iterator {@link Iterator}.
	 * @param index Index.
	 * @return {@code index}-tes Element.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 * @throws NoSuchElementException Wenn kein {@code index}-tes Element existiert.
	 */
	public static <GItem> GItem get(final Iterator<? extends GItem> iterator, final int index) throws NullPointerException, NoSuchElementException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if((index < 0) || (Iterators.skip(iterator, index) != 0) || !iterator.hasNext()) throw new NoSuchElementException();
		return iterator.next();
	}

	/**
	 * Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die Anzahl der noch zu überspringenden Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene {@link Iterator} via {@link Iterator#hasNext()} anzeigt, dass er keine weiteren Elemente mehr liefern kann. Wenn die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen {@link Iterator}s zurück gegeben. Damit bestimmt {@code (-Iterators.skip(iterator, -1) - 1)} die Anzahl der Elemente des {@link Iterator}s {@code iterator}.
	 * 
	 * @see Iterator#hasNext()
	 * @param iterator {@link Iterator}.
	 * @param count Anzahl der zu überspringenden Elemente.
	 * @return Anzahl der noch zu überspringenden Elemente.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 */
	public static int skip(final Iterator<?> iterator, int count) throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		while((count != 0) && iterator.hasNext()){
			count--;
			iterator.next();
		}
		return count;
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des {@link Iterator}s {@code true} zurück.
	 * 
	 * @see Collection#retainAll(Collection)
	 * @param iterator {@link Iterator}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean retainAll(final Iterator<?> iterator, final Collection<?> collection) throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if(collection == null) throw new NullPointerException("collection is null");
		boolean modified = false;
		while(iterator.hasNext()){
			if(!collection.contains(iterator.next())){
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator} vorkommen, und gibt nur bei Veränderung der {@link Collection} {@code true} zurück.
	 * 
	 * @see Collection#retainAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean retainAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		if(collection == null) throw new NullPointerException("collection is null");
		if(iterator == null) throw new NullPointerException("iterator is null");
		final List<Object> list = new ArrayList<Object>();
		Iterators.appendAll(list, iterator);
		return collection.retainAll(list);
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link Iterator}s in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der {@link Collection} {@code true} zurück.
	 * 
	 * @see Collection#addAll(Collection)
	 * @param <GEntry> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static <GEntry> boolean appendAll(final Collection<GEntry> collection, final Iterator<? extends GEntry> iterator) throws NullPointerException {
		if(collection == null) throw new NullPointerException("collection is null");
		if(iterator == null) throw new NullPointerException("iterator is null");
		boolean modified = false;
		while(iterator.hasNext()){
			if(collection.add(iterator.next())){
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s und gibt nur bei Veränderung des {@link Iterator}s {@code true} zurück.
	 * 
	 * @see Iterator#remove()
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 */
	public static boolean removeAll(final Iterator<?> iterator) throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		boolean modified = false;
		while(iterator.hasNext()){
			iterator.next();
			iterator.remove();
			modified = true;
		}
		return modified;
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s, die in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des {@link Iterator}s {@code true} zurück.
	 * 
	 * @see Collection#retainAll(Collection)
	 * @param iterator {@link Iterator}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean removeAll(final Iterator<?> iterator, final Collection<?> collection) throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if(collection == null) throw new NullPointerException("collection is null");
		boolean modified = false;
		while(iterator.hasNext()){
			if(collection.contains(iterator.next())){
				iterator.remove();
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s aus der gegebenen {@link Collection} und gibt nur bei Veränderungen an der {@link Collection} {@code true} zurück.
	 * 
	 * @see Collection#removeAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean removeAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		if(collection == null) throw new NullPointerException("collection is null");
		if(iterator == null) throw new NullPointerException("iterator is null");
		boolean modified = false;
		while(iterator.hasNext()){
			if(collection.remove(iterator.next())){
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterator}s in der gegebenen {@link Collection} enthalten sind.
	 * 
	 * @see Collection#containsAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei vollständiger Inklusion.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null} ist.
	 */
	public static boolean containsAll(final Collection<?> collection, final Iterator<?> iterator) throws NullPointerException {
		if(collection == null) throw new NullPointerException("collection is null");
		if(iterator == null) throw new NullPointerException("iterator is null");
		while(iterator.hasNext())
			if(!collection.contains(iterator.next())) return false;
		return true;
	}

	/**
	 * Diese Methode gibt den gegebenen {@link Iterator} oder den leeren {@link Iterator} zurück.
	 * 
	 * @see Iterators#voidIterator()
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link Iterator} oder {@code Void}-{@link Iterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterator<GEntry> iterator(final Iterator<? extends GEntry> iterator) {
		return (Iterator<GEntry>)((iterator == null) ? Iterators.VOID_ITERATOR : iterator);
	}

	/**
	 * Diese Methode gibt den leeren {@link Iterator} zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @return {@code void}-{@link Iterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterator<GEntry> voidIterator() {
		return (Iterator<GEntry>)Iterators.VOID_ITERATOR;
	}

	/**
	 * Diese Methode erzeugt einen einen {@link Iterator}, der die gegebene maximale Anzahl der Elemente des gegebenen {@link Iterator}s liefert, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param count Anzahl der maximal vom gegebenen {@link Iterator} gelieferten Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link CountIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
	 */
	public static <GEntry> CountIterator<GEntry> countIterator(final int count, final Iterator<? extends GEntry> iterator) throws NullPointerException,
		IllegalArgumentException {
		return new CountIterator<GEntry>(count, iterator);
	}

	/**
	 * Diese Methode erzeugt einen begrenzten {@link Iterator}, der nur die ersten vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterator}s liefert sowie die Iteration beim ersten abgelehnten Element abbricht, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @param filter {@link Filter}.
	 * @return {@link LimitedIterator}.
	 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
	 */
	public static <GEntry> LimitedIterator<GEntry> limitedIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
		throws NullPointerException {
		return new LimitedIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterator}, der nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterator}s liefert, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @param filter {@link Filter}.
	 * @return {@link FilteredIterator}.
	 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
	 */
	public static <GEntry> FilteredIterator<GEntry> filteredIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
		throws NullPointerException {
		return new FilteredIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Iterator}, der die vom gegebenen {@link Converter Converter} konvertierten Elemente des gegebenen {@link Iterator}s liefert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator Iterators}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente des erzeugten {@link Iterator Iterators}.
	 * @param iterator {@link Iterator}.
	 * @param converter {@link Converter}.
	 * @return {@link ConvertedIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterator} {@code null} ist.
	 */
	public static <GInput, GOutput> ConvertedIterator<GInput, GOutput> convertedIterator(final Converter<? super GInput, ? extends GOutput> converter,
		final Iterator<? extends GInput> iterator) throws NullPointerException {
		return new ConvertedIterator<GInput, GOutput>(converter, iterator);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Iterators() {
	}

}
