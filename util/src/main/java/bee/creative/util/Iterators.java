package bee.creative.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import bee.creative.util.Comparables.Get;
import bee.creative.util.Filters.ContainsFilter;
import bee.creative.util.Filters.NegationFilter;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Iterator}en.
 * 
 * @see Iterator
 * @see Iterable
 * @see Iterables
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Iterators {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Iterator}.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elementes.
	 */
	static abstract class AbstractIterator<GEntry> implements Iterator<GEntry>, UseToString {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Iterator} über ein einzelnes Element.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elementes.
	 */
	static abstract class AbstractSingletonIterator<GEntry> extends AbstractIterator<GEntry> {

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
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw (this.hasNext ? new IllegalStateException() : new UnsupportedOperationException());
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten, delegierenden {@link Iterator}.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GEntry2> Typ der Elemente des gegebenen {@link Iterator}s.
	 */
	static abstract class AbstractDelegatingIterator<GEntry, GEntry2> extends AbstractIterator<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Iterator}.
		 */
		final Iterator<? extends GEntry2> iterator;

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterator}.
		 * 
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 */
		public AbstractDelegatingIterator(final Iterator<? extends GEntry2> iterator) throws NullPointerException {
			if(iterator == null) throw new NullPointerException("iterator is null");
			this.iterator = iterator;
		}

		/**
		 * Diese Methode gibt {@link #iterator} oder seine {@link Class} zurück.
		 * 
		 * @return {@link Object}, dass für {@link #iterator} in {@link #toString()} verwendet werden sollte.
		 */
		Object toStringIterator() {
			return (this.iterator instanceof UseToString) ? this.iterator : this.iterator.getClass();
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
		public void remove() {
			this.iterator.remove();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.toStringIterator());
		}

	}

	/**
	 * Diese Klasse implementiert den leeren {@link Iterator}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VoidIterator extends AbstractIterator<Object> {

		/**
		 * Dieses Feld speichert den {@link VoidIterator}.
		 */
		public static final Iterator<?> INSTANCE = new VoidIterator();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new IllegalStateException();
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der eine gegebene Anzahl an Elementen eines {@link Get} liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class GetIterator<GEntry> extends AbstractIterator<GEntry> {

		/**
		 * Dieses Feld speichert das {@link Get}.
		 */
		final Get<? extends GEntry> get;

		/**
		 * Dieses Feld speichert den maximalen Index.
		 */
		final int count;

		/**
		 * Dieses Feld speichert den aktuellen Wert.
		 */
		int index;

		/**
		 * Dieser Konstruktor initialisiert das {@link Get} und die Anzahl. Die Iteration beginnt bei Index {@code 0}.
		 * 
		 * @param get {@link Get}, dessen Elemente geliefert werden.
		 * @param count Anzahl.
		 * @throws NullPointerException Wenn das gegebene {@link Get} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public GetIterator(final Get<? extends GEntry> get, final int count) throws NullPointerException, IllegalArgumentException {
			this(get, 0, count);
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link Get}, den Index des ersten Elements und die Anzahl.
		 * 
		 * @param get {@link Get}, dessen Elemente geliefert werden.
		 * @param index Index.
		 * @param count Anzahl.
		 * @throws NullPointerException Wenn das gegebene {@link Get} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public GetIterator(final Get<? extends GEntry> get, final int index, final int count) throws NullPointerException, IllegalArgumentException {
			if(get == null) throw new NullPointerException();
			if(count < 0) throw new IllegalArgumentException();
			this.get = get;
			this.index = index;
			this.count = index + count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.index < this.count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			return this.get.get(this.index++);
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
			return Objects.toStringCall(this, this.get, this.count);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator} über ein einzelnes, gegebenes Element.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elements.
	 */
	public static final class EntryIterator<GEntry> extends AbstractSingletonIterator<GEntry> {

		/**
		 * Dieses Feld speichert das Element.
		 */
		final GEntry value;

		/**
		 * Dieser Konstruktor initialisiert das Element.
		 * 
		 * @param entry Element.
		 */
		public EntryIterator(final GEntry entry) {
			this.value = entry;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			super.next();
			return this.value;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator} über ein einzelnes Element, dass durch einen gegebenen {@link Builder} bereitgestellt wird.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elements.
	 */
	public static final class BuilderIterator<GEntry> extends AbstractSingletonIterator<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Builder}.
		 */
		final Builder<? extends GEntry> builder;

		/**
		 * Dieser Konstruktor initialisiert den {@link Builder}.
		 * 
		 * @param builder {@link Builder}.
		 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
		 */
		public BuilderIterator(final Builder<? extends GEntry> builder) throws NullPointerException {
			if(builder == null) throw new NullPointerException();
			this.builder = builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			super.next();
			return this.builder.build();
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der eine gegebene Anzahl an {@link Integer}s ab dem Wert {@code 0} liefert. Ein {@link IntegerIterator},
	 * der mit der Anzahl {@code count} erstellt wird, liefert damit die Werte {@code 0}, {@code 1}, {@code 2}, ..., {@code count-1}.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IntegerIterator extends AbstractIterator<Integer> {

		/**
		 * Dieses Feld speichert die Anzahl.
		 */
		final int count;

		/**
		 * Dieses Feld speichert den aktuellen Wert.
		 */
		int value;

		/**
		 * Dieser Konstruktor initialisiert die Anzahl.
		 * 
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public IntegerIterator(final int count) throws IllegalArgumentException {
			if(count < 0) throw new IllegalArgumentException("count out of range: " + count);
			this.value = 0;
			this.count = count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.value < this.count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Integer next() {
			return Integer.valueOf(this.value++);
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
			return Objects.toStringCall(this, this.count);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der kein Element eines gegebenen {@link Iterator}s mehrfach liefert. Die {@link Collection} zum
	 * Ausschluss von Dopplungen kann im Konstruktor angegeben werden.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class UniqueIterator<GEntry> extends AbstractDelegatingIterator<GEntry, GEntry> {

		/**
		 * Dieses Feld speichert die {@link Collection} zum Ausschluss von Dopplungen.
		 */
		final Collection<GEntry> collection;

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterator} und verwendet ein {@link HashSet} zum Ausschluss von Dopplungen.
		 * 
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 */
		public UniqueIterator(final Iterator<? extends GEntry> iterator) throws NullPointerException {
			this(iterator, new HashSet<GEntry>());
		}

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterator} sowie die {@link Collection} zum Ausschluss von Dopplungen.
		 * 
		 * @param iterator {@link Iterator}.
		 * @param collection {@link Collection} zum Ausschluss von Dopplungen.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public UniqueIterator(final Iterator<? extends GEntry> iterator, final Collection<GEntry> collection) throws NullPointerException {
			super(new FilteredIterator<GEntry>(new NegationFilter<GEntry>(new ContainsFilter<GEntry>(collection)), iterator));
			this.collection = collection;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			final GEntry next = this.iterator.next();
			this.collection.add(next);
			return next;
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der eine begrenzte Anzahl an Elementen eines gegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterator<GEntry> extends AbstractDelegatingIterator<GEntry, GEntry> {

		/**
		 * Dieses Feld speichert die maximale Anzahl der verbleibenden Elementen.
		 */
		int count;

		/**
		 * Dieser Konstruktor initialisiert Anzahl und {@link Iterator}.
		 * 
		 * @param count Anzahl der maximal vom gegebenen {@link Iterator} gelieferten Elemente.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public LimitedIterator(final int count, final Iterator<? extends GEntry> iterator) throws NullPointerException, IllegalArgumentException {
			super(iterator);
			if(count < 0) throw new IllegalArgumentException("count out of range: " + count);
			this.count = count;
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
		public GEntry next() {
			if(this.count == 0) throw new NoSuchElementException();
			this.count--;
			return this.iterator.next();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.count, this.toStringIterator());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der nur die von einem gegebenen {@link Filter} akzeptierten Elemente eines gegebenen {@link Iterator}s
	 * liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterator<GEntry> extends AbstractDelegatingIterator<GEntry, GEntry> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GEntry> filter;

		/**
		 * Dieses Feld speichert {@code true}, wenn ein nächstes Element existiert.
		 */
		Boolean hasNext;

		/**
		 * Dieses Feld speichert das nächste Element.
		 */
		GEntry entry;

		/**
		 * Dieser Konstruktor initialisiert {@link Filter} und {@link Iterator}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public FilteredIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator) throws NullPointerException {
			super(iterator);
			if(filter == null) throw new NullPointerException("filter is null");
			this.filter = filter;
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.filter, this.toStringIterator());
		}

	}

	/**
	 * Diese Klasse implementiert einen verketteten {@link Iterator}, der über alle Elemente der eingegebenen {@link Iterator}en in der gegebenen Reihenfolge
	 * läuft. Wenn einer der eingegebenen {@link Iterator}en {@code null} ist, wird er ausgelassen.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ChainedIterator<GEntry> extends AbstractDelegatingIterator<GEntry, Iterator<? extends GEntry>> {

		/**
		 * Dieses Feld speichert den aktiven {@link Iterator}.
		 */
		Iterator<? extends GEntry> entries;

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterator} über die {@link Iterator}en. Der {@link Iterator} darf {@code null} liefern.
		 * 
		 * @param iterator {@link Iterator} über die {@link Iterator}en.
		 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
		 */
		public ChainedIterator(final Iterator<? extends Iterator<? extends GEntry>> iterator) throws NullPointerException {
			super(iterator);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			while(true){
				while(this.entries == null){
					if(!this.iterator.hasNext()) return false;
					this.entries = this.iterator.next();
				}
				if(this.entries.hasNext()) return true;
				this.entries = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public GEntry next() {
			if(this.entries == null) throw new NoSuchElementException();
			return this.entries.next();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			if(this.entries == null) throw new IllegalStateException();
			this.entries.remove();
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der die vom gegebenen {@link Converter} konvertierten Elemente des gegebenen {@link Iterator}s liefert.
	 * 
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator} s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente.
	 */
	public static final class ConvertedIterator<GInput, GOutput> extends AbstractDelegatingIterator<GOutput, GInput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Converter} und {@link Iterator}.
		 * 
		 * @param converter {@link Converter}.
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConvertedIterator(final Converter<? super GInput, ? extends GOutput> converter, final Iterator<? extends GInput> iterator)
			throws NullPointerException {
			super(iterator);
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
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
		public String toString() {
			return Objects.toStringCall(this, this.converter, this.toStringIterator());
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Iterator}, der die Elemente eines gegebene {@link Iterator}s liefert und dessen {@link #remove()} eine
	 * {@link UnsupportedOperationException} auslöst.
	 * 
	 * @see Iterator#remove()
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class UnmodifiableIterator<GEntry> extends AbstractDelegatingIterator<GEntry, GEntry> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterator}.
		 * 
		 * @param iterator {@link Iterator}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
		 */
		public UnmodifiableIterator(final Iterator<? extends GEntry> iterator) throws NullPointerException {
			super(iterator);
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

	}

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
	 * Diese Methode versucht die gegebenen Anzahl an Elemente im gegebenen {@link Iterator} zu überspringen und gibt die Anzahl der noch zu überspringenden
	 * Elemente zurück. Diese Anzahl ist dann größer als {@code 0}, wenn der gegebene {@link Iterator} via {@link Iterator#hasNext()} anzeigt, dass er keine
	 * weiteren Elemente mehr liefern kann. Wenn die gegebene Anzahl kleiner {@code 0} ist, wird diese Anzahl vermindert um die Anzahl der Elemente des gegebenen
	 * {@link Iterator}s zurück gegeben. Damit bestimmt {@code (-Iterators.skip(iterator, -1) - 1)} die Anzahl der Elemente des {@link Iterator}s {@code iterator}
	 * .
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
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s, die nicht in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung
	 * des {@link Iterator}s {@code true} zurück.
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
	 * Diese Methode entfernt alle Elemente der gegebenen {@link Collection}, die nicht im gegebenen {@link Iterator} vorkommen, und gibt nur bei Veränderung der
	 * {@link Collection} {@code true} zurück.
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
	 * Diese Methode fügt alle Elemente des gegebenen {@link Iterator}s in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
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
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s, die in der gegebenen {@link Collection} vorkommen, und gibt nur bei Veränderung des
	 * {@link Iterator}s {@code true} zurück.
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
	 * Diese Methode entfernt alle Elemente des gegebenen {@link Iterator}s aus der gegebenen {@link Collection} und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
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
	 * Diese Methode gibt den gegebenen {@link Iterator} oder {@link VoidIterator#INSTANCE} zurück.
	 * 
	 * @see Iterators#voidIterator()
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link Iterator} oder {@link VoidIterator#INSTANCE}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterator<GEntry> iterator(final Iterator<? extends GEntry> iterator) {
		return (Iterator<GEntry>)((iterator != null) ? iterator : VoidIterator.INSTANCE);
	}

	/**
	 * Diese Methode gibt den leeren {@link Iterator} zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link VoidIterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterator<GEntry> voidIterator() {
		return (Iterator<GEntry>)VoidIterator.INSTANCE;
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
	 * Diese Methode gibt den {@link Iterator} über das durch den gegebenen {@link Builder} bereitgestellte Element zurück.
	 * 
	 * @param <GEntry> Typ des Elements.
	 * @param builder {@link Builder}.
	 * @return {@link BuilderIterator}
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> builderIterator(final Builder<? extends GEntry> builder) throws NullPointerException {
		return new BuilderIterator<GEntry>(builder);
	}

	/**
	 * Diese Methode erzeugt einen {@link Iterator}, der die gegebene Anzahl an {@link Integer}s ab dem Wert {@code 0} liefert, und gibt ihn zurück.
	 * 
	 * @param count Anzahl.
	 * @return {@link IntegerIterator}.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
	 */
	public static Iterator<Integer> integerIterator(final int count) throws IllegalArgumentException {
		return new IntegerIterator(count);
	}

	/**
	 * Diese Methode erzeugt einen einen {@link Iterator}, der kein Element des gegebenen {@link Iterator}s mehrfach liefert, und gibt ihn zurück. Als
	 * {@link Collection} zum Ausschluss von Dopplungen wird ein {@link HashSet} verwendet.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link UniqueIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> uniqueIterator(final Iterator<? extends GEntry> iterator) throws NullPointerException {
		return new UniqueIterator<GEntry>(iterator);
	}

	/**
	 * Diese Methode erzeugt einen einen {@link Iterator}, der kein Element des gegebenen {@link Iterator}s mehrfach liefert, und gibt ihn zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link UniqueIterator}.
	 * @param collection {@link Collection} zum Ausschluss von Dopplungen.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> uniqueIterator(final Iterator<? extends GEntry> iterator, final Collection<GEntry> collection)
		throws NullPointerException {
		return new UniqueIterator<GEntry>(iterator, collection);
	}

	/**
	 * Diese Methode erzeugt einen einen {@link Iterator}, der die gegebene maximale Anzahl der Elemente des gegebenen {@link Iterator}s liefert, und gibt ihn
	 * zurück.
	 * 
	 * @param <GEntry> Typ der Elemente.
	 * @param count Anzahl der maximal vom gegebenen {@link Iterator} gelieferten Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link LimitedIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
	 */
	public static <GEntry> Iterator<GEntry> limitedIterator(final int count, final Iterator<? extends GEntry> iterator) throws NullPointerException,
		IllegalArgumentException {
		return new LimitedIterator<GEntry>(count, iterator);
	}

	/**
	 * Diese Methode erzeugt einen filternden {@link Iterator}, der nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterator}s
	 * liefert, und gibt ihn zurück.
	 * 
	 * @see Filter
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @param filter {@link Filter}.
	 * @return {@link FilteredIterator}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> filteredIterator(final Filter<? super GEntry> filter, final Iterator<? extends GEntry> iterator)
		throws NullPointerException {
		return new FilteredIterator<GEntry>(filter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert,
	 * und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterator} über die {@link Iterator}en.
	 * @return {@link ChainedIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends Iterator<? extends GEntry>> iterators) throws NullPointerException {
		return new ChainedIterator<GEntry>(iterators);
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert,
	 * und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see ChainedIterator#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator1 {@link Iterator} 1.
	 * @param iterator2 {@link Iterator} 2.
	 * @return {@link ChainedIterator}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends GEntry> iterator1, final Iterator<? extends GEntry> iterator2) {
		return Iterators.chainedIterator(Arrays.asList(iterator1, iterator2));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert,
	 * und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see ChainedIterator#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterator}-Array.
	 * @return {@link ChainedIterator}.
	 * @throws NullPointerException Wenn die gegebenen {@link Iterator}en {@code null} sind.
	 */
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterator<? extends GEntry>... iterators) throws NullPointerException {
		if(iterators == null) throw new NullPointerException("iterators is null");
		return Iterators.chainedIterator(Arrays.asList(iterators));
	}

	/**
	 * Diese Methode erzeugt einen verketteten {@link Iterator}, der alle Elemente der gegebenen {@link Iterator Iteratoren} in der gegebenen Reihenfolge liefert,
	 * und gibt ihn zurück.
	 * 
	 * @see ChainedIterator
	 * @see ChainedIterator#chainedIterator(Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterators {@link Iterable} über die {@link Iterator}en.
	 * @return {@link ChainedIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> chainedIterator(final Iterable<? extends Iterator<? extends GEntry>> iterators) throws NullPointerException {
		return Iterators.chainedIterator(iterators.iterator());
	}

	/**
	 * Diese Methode erzeugt einen konvertierenden {@link Iterator}, der die vom gegebenen {@link Converter Converter} konvertierten Elemente des gegebenen
	 * {@link Iterator}s liefert, und gibt ihn zurück.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterator Iterators}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente des erzeugten {@link Iterator Iterators}.
	 * @param iterator {@link Iterator}.
	 * @param converter {@link Converter}.
	 * @return {@link ConvertedIterator}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> Iterator<GOutput> convertedIterator(final Converter<? super GInput, ? extends GOutput> converter,
		final Iterator<? extends GInput> iterator) throws NullPointerException {
		return new ConvertedIterator<GInput, GOutput>(converter, iterator);
	}

	/**
	 * Diese Methode erzeugt einen unmodifizierbaren {@link Iterator}, und gibt ihn zurück.
	 * 
	 * @see Iterator#remove()
	 * @param <GEntry> Typ der Elemente.
	 * @param iterator {@link Iterator}.
	 * @return {@link UnmodifiableIterator}.
	 * @throws NullPointerException Wenn der gegebene {@link Iterator} {@code null} ist.
	 */
	public static <GEntry> Iterator<GEntry> unmodifiableIterator(final Iterator<? extends GEntry> iterator) throws NullPointerException {
		return new UnmodifiableIterator<GEntry>(iterator);
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Iterators() {
	}

}
