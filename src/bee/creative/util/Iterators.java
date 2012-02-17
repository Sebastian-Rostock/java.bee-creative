package bee.creative.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import bee.creative.util.Converters.ConverterLink;
import bee.creative.util.Filters.FilterLink;

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
	static abstract class BaseIterator<GEntry> extends FilterLink<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert {@code true}, wenn ein nächstes Element existiert.
		 */
		Boolean hasNext;

		/**
		 * Dieses Feld speichert das nächste Element;
		 */
		GEntry entry;

		/**
		 * Dieses Feld speichert den {@link Iterator};
		 */
		final Iterator<? extends GEntry> iterator;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} bzw. der gegebenen {@link Iterator} {@code null}
		 *         ist.
		 */
		public BaseIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
			throws NullPointerException {
			super(filter);
			if(iterator == null) throw new NullPointerException("iterator is null");
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return object == this;
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#limitedIterator(Filter, Iterator)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static final class LimitedIteratorConverter<GEntry> extends FilterLink<GEntry> implements
		Converter<Iterator<? extends GEntry>, Iterator<GEntry>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
		 */
		public LimitedIteratorConverter(final Filter<? super GEntry> filter) throws NullPointerException {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> convert(final Iterator<? extends GEntry> input) {
			return Iterators.limitedIterator(this.filter, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof LimitedIteratorConverter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("limitedIteratorConverter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#filteredIterator(Filter, Iterator)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static final class FilteredIteratorConverter<GEntry> extends FilterLink<GEntry> implements
		Converter<Iterator<? extends GEntry>, Iterator<GEntry>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
		 */
		public FilteredIteratorConverter(final Filter<? super GEntry> filter) throws NullPointerException {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> convert(final Iterator<? extends GEntry> input) {
			return Iterators.filteredIterator(this.filter, input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof FilteredIteratorConverter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("filteredIteratorConverter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#convertedIterator(Converter, Iterator)} in seine Ausgabe überführt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 */
	static final class ConvertedIteratorConverter<GInput extends Iterator<? extends GValue>, GValue, GOutput> extends
		Converters.ConverterLink<GValue, GOutput> implements Converter<GInput, Iterator<GOutput>> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Converter}.
		 * 
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
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
			if(object == this) return true;
			if(!(object instanceof ConvertedIteratorConverter<?, ?, ?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedIteratorConverter", this.converter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator} über ein Element.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elementes.
	 */
	public static final class EntryIterator<GEntry> extends EntryLink<GEntry> implements Iterator<GEntry> {

		/**
		 * Dieses Feld speichert den Zustand.
		 */
		boolean hasNext = true;

		/**
		 * Dieser Konstrukteur initialisiert das Element.
		 * 
		 * @param entry Element
		 */
		public EntryIterator(final GEntry entry) {
			super(entry);
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
		public boolean equals(final Object object) {
			return object == this;
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
	 * Diese Klasse implementiert einen begrenzten {@link Iterator}, der nur die ersten vom gegebenen {@link Filter}
	 * akzeptierten Elemente des eingegebenen {@link Iterator}s liefert und die Iteration beim ersten abgelehnten Element
	 * abbricht.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterator<GEntry> extends BaseIterator<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
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
	 * Diese Klasse implementiert einen filternden {@link Iterator}, der nur die vom gegebenen {@link Filter} akzeptierten
	 * Elemente des eingegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterator<GEntry> extends BaseIterator<GEntry> {

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
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
	 * Diese Klasse implementiert einen verketteten {@link Iterator}. Der {@link Iterator} läuft über alle Elemente der
	 * eingegebenen {@link Iterator}en in der gegebenen Reihenfolge. Wenn einer der eingegebenen {@link Iterator}en
	 * {@code null} ist, wird dieser ausgelassen.
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

	}

	/**
	 * Diese Klasse implementiert einen konvertierenden {@link Iterator},der die vom gegebenen {@link Converter}
	 * konvertierten Elemente des gegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator}
	 *        s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente.
	 */
	public static final class ConvertedIterator<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Iterator<GOutput> {

		/**
		 * Dieses Feld speichert den {@link Iterator};
		 */
		final Iterator<? extends GInput> iterator;

		/**
		 * Dieser Konstrukteur initialisiert {@link Converter} und {@link Iterator}.
		 * 
		 * @param converter {@link Converter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterator} {@code null}
		 *         ist.
		 */
		public ConvertedIterator(final Converter<? super GInput, ? extends GOutput> converter,
			final Iterator<? extends GInput> iterator) throws NullPointerException {
			super(converter);
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
		public boolean equals(final Object object) {
			return object == this;
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
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
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
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
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
	 * Dieses Feld speichert den {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#chainedIterator(Iterator)} in seine Ausgabe überführt.
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
	 * Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die
	 * Anzahl der noch zu überspringenden Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene
	 * {@link Iterator} via {@link Iterator#hasNext()} anzeigt, dass er keine weiteren Elemente mehr liefern kann. Wenn
	 * die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen
	 * {@link Iterator}s zurück gegeben. Damit bestimmt {@code (-Iterators.skip(iterator, -1) - 1)} die Anzahl der
	 * Elemente des {@link Iterator}s {@code iterator}.
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
	 * Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator}
	 * vorkommen, und gibt nur bei Veränderung der {@link Collection} {@code true} zurück.
	 * 
	 * @see Collection#retainAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean retainAll(final Collection<?> collection, final Iterator<?> iterator)
		throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if(collection == null) throw new NullPointerException("collection is null");
		final List<Object> list = new ArrayList<Object>();
		Iterators.appendAll(list, iterator);
		return collection.retainAll(list);
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s, die nicht in der gegebenen {@link Collection}
	 * vorkommen, und gibt nur bei Veränderung des {@link Iterator}s {@code true} zurück.
	 * 
	 * @see Collection#retainAll(Collection)
	 * @param iterator {@link Iterator}.
	 * @param collection {@link Collection}.
	 * @return {@code true} bei Veränderungen am {@link Iterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean retainAll(final Iterator<?> iterator, final Collection<?> collection)
		throws NullPointerException {
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
	 * Diese Methode fügt alle Elemente des gegebenen {@link Iterator}s in die gegebene {@link Collection} ein und gibt
	 * nur bei Veränderungen an der {@link Collection} {@code true} zurück.
	 * 
	 * @see Collection#addAll(Collection)
	 * @param <GEntry> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static <GEntry> boolean appendAll(final Collection<GEntry> collection,
		final Iterator<? extends GEntry> iterator) throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if(collection == null) throw new NullPointerException("collection is null");
		boolean modified = false;
		while(iterator.hasNext()){
			if(collection.add(iterator.next())){
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s aus der gegebenen {@link Collection} und gibt
	 * nur bei Veränderungen an der {@link Collection} {@code true} zurück.
	 * 
	 * @see Collection#removeAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean removeAll(final Collection<?> collection, final Iterator<?> iterator)
		throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if(collection == null) throw new NullPointerException("collection is null");
		boolean modified = false;
		while(iterator.hasNext()){
			if(collection.remove(iterator.next())){
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Diese Methode gibt nur dann {@code true} zurück, wenn alle Elemente des gegebenen {@link Iterator}s in der
	 * gegebenen {@link Collection} enthalten sind.
	 * 
	 * @see Collection#containsAll(Collection)
	 * @param collection {@link Collection}.
	 * @param iterator {@link Iterator}.
	 * @return {@code true} bei vollständiger Inklusion.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} bzw. die gegebene {@link Collection} {@code null}
	 *         ist.
	 */
	public static boolean containsAll(final Collection<?> collection, final Iterator<?> iterator)
		throws NullPointerException {
		if(iterator == null) throw new NullPointerException("iterator is null");
		if(collection == null) throw new NullPointerException("collection is null");
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
	public static <GEntry> Iterator<GEntry> iterator(final Iterator<GEntry> iterator) {
		return ((iterator == null) ? Iterators.<GEntry>voidIterator() : iterator);
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#iterator(Iterator)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Iterators#iterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterators#iterator(Iterator)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterator<GEntry>, Iterator<GEntry>> iteratorConverter() {
		return (Converter<Iterator<GEntry>, Iterator<GEntry>>)Iterators.ITERATOR_CONVERTER;
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
	 * Diese Methode gibt den {@link Iterator} über das gegebene Element zurück.
	 * 
	 * @param <GEntry> Typ des Elements.
	 * @param entry Element.
	 * @return {@link EntryIterator}
	 */
	public static <GEntry> Iterator<GEntry> entryIterator(final GEntry entry) {
		return new EntryIterator<GEntry>(entry);
	}

	/**
	 * Diese Methode gibt den {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#entryIterator(Object)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Iterators#entryIterator(Object)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterators#entryIterator(Object)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<GEntry, Iterator<GEntry>> entryIteratorConverter() {
		return (Converter<GEntry, Iterator<GEntry>>)Iterators.ENTRY_ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen begrenzten {@link Iterator}, der nur die ersten vom gegebenen {@link Filter}
	 * akzeptierten Elemente des gegebenen {@link Iterator}s liefert sowie die Iteration beim ersten abgelehnten Element
	 * abbricht, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @param filter {@link Filter}.
	 * @return {@link LimitedIterator}.
	 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
	 */
	public static <GEntry> Iterator<GEntry> limitedIterator(final Filter<? super GEntry> filter,
		final Iterator<? extends GEntry> iterator) throws NullPointerException {
		return new LimitedIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#limitedIterator(Filter, Iterator)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @see Converter
	 * @see Iterators#limitedIterator(Filter, Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@link Iterators#limitedIterator(Filter, Iterator)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GEntry> Converter<Iterator<? extends GEntry>, Iterator<GEntry>> limitedIteratorConverter(
		final Filter<? super GEntry> filter) throws NullPointerException {
		return new LimitedIteratorConverter<GEntry>(filter);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterator}, der nur die vom gegebenen {@link Filter} akzeptierten
	 * Elemente des gegebenen {@link Iterator}s liefert, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @param filter {@link Filter}.
	 * @return {@link FilteredIterator}.
	 * @throws NullPointerException Wenn {@link Filter} oder {@link Iterator} {@code null} sind.
	 */
	public static <GEntry> Iterator<GEntry> filteredIterator(final Filter<? super GEntry> filter,
		final Iterator<? extends GEntry> iterator) throws NullPointerException {
		return new FilteredIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#filteredIterator(Filter, Iterator)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @see Converter
	 * @see Iterators#filteredIterator(Filter, Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@link Iterators#filteredIterator(Filter, Iterator)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GEntry> Converter<Iterator<? extends GEntry>, Iterator<GEntry>> filteredIteratorConverter(
		final Filter<? super GEntry> filter) throws NullPointerException {
		return new FilteredIteratorConverter<GEntry>(filter);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterator}-{@link Array}.
	 * @return {@link ChainedIterator}.
	 * @throws NullPointerException Wenn die gegebenen {@link Iterator}en {@code null} sind.
	 */
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends GEntry>... iterators)
		throws NullPointerException {
		if(iterators == null) throw new NullPointerException("iterators is null");
		return Iterators.chainedIterator(Arrays.asList(iterators));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator1 {@link Iterator} 1.
	 * @param iterator2 {@link Iterator} 2.
	 * @return {@link ChainedIterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends GEntry> iterator1,
		final Iterator<? extends GEntry> iterator2) {
		return Iterators.chainedIterator(Arrays.asList(iterator1, iterator2));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterator} über die {@link Iterator}en.
	 * @return {@link ChainedIterator}.
	 * @throws NullPointerException Wenn de rgegebene {@link Iterator} {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends Iterator<? extends GEntry>> iterators)
		throws NullPointerException {
		return new ChainedIterator<GEntry>(iterators);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator
	 * Iteratoren} in der gegebenen Reihenfolge liefert, und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterable} über die {@link Iterator}en.
	 * @return {@link ChainedIterator}.
	 */
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterable<? extends Iterator<? extends GEntry>> iterators) {
		if(iterators == null) throw new NullPointerException("iterators is null");
		return Iterators.chainedIterator(iterators.iterator());
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#chainedIterator(Iterator)} in seine Ausgabe überführt.
	 * 
	 * @see Converter
	 * @see Iterators#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link Iterators#chainedIterator(Iterator)}-{@link Converter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Converter<Iterator<? extends Iterator<? extends GEntry>>, Iterator<GEntry>> chainedIteratorConverter() {
		return (Converter<Iterator<? extends Iterator<? extends GEntry>>, Iterator<GEntry>>)Iterators.CHAINED_ITERATOR_CONVERTER;
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Iterator}, der die vom gegebenen {@link Converter Converter}
	 * konvertierten Elemente des gegebenen {@link Iterator}s liefert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator
	 *        Iterators}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente des erzeugten {@link Iterator
	 *        Iterators}.
	 * @param iterator {@link Iterator}.
	 * @param converter {@link Converter}.
	 * @return {@link ConvertedIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} bzw. der gegebene {@link Iterator} {@code null}
	 *         ist.
	 */
	public static <GInput, GOutput> Iterator<GOutput> convertedIterator(
		final Converter<? super GInput, ? extends GOutput> converter, final Iterator<? extends GInput> iterator)
		throws NullPointerException {
		return new ConvertedIterator<GInput, GOutput>(converter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen {@link Converter}, der seine Eingabe mit Hilfe der Methode
	 * {@link Iterators#convertedIterator(Converter, Iterator)} in seine Ausgabe überführt, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @see Iterators#convertedIterator(Converter, Iterator)
	 * @param <GInput> Typ der Eingabe.
	 * @param <GValue> Typ der Elemente.
	 * @param <GOutput> Typ der Ausgabe.
	 * @param converter {@link Converter}.
	 * @return {@link Iterators#convertedIterator(Converter, Iterator)}-{@link Converter}.
	 * @throws NullPointerException Wenn der gegebene {@link Converter} {@code null} ist.
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
