package bee.creative.util;

import java.util.Collection;
import java.util.Iterator;
import bee.creative.util.Converters.AbstractConverter;
import bee.creative.util.Iterators.BuilderIterator;
import bee.creative.util.Iterators.ChainedIterator;
import bee.creative.util.Iterators.ConvertedIterator;
import bee.creative.util.Iterators.EntryIterator;
import bee.creative.util.Iterators.FilteredIterator;
import bee.creative.util.Iterators.IntegerIterator;
import bee.creative.util.Iterators.LimitedIterator;
import bee.creative.util.Iterators.UniqueIterator;
import bee.creative.util.Iterators.UnmodifiableIterator;
import bee.creative.util.Iterators.VoidIterator;
import bee.creative.util.Objects.UseToString;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Iterable}s.
 * 
 * @see Iterator
 * @see Iterators
 * @see Iterable
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Iterables {

	/**
	 * Diese Klasse implementiert das in einem {@link ChainedIterable} verwendete {@link Iterable} über {@link Iterable}s.
	 * 
	 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static class IterableArray<GEntry> implements Iterable<Iterable<? extends GEntry>> {

		/**
		 * Diese Methode gibt ein {@link Iterable} zurück, das die gegebenen {@link Iterable}s liefert. Wenn unter den Eingaben {@link ChainedIterable}s sind,
		 * werden diese zu einem {@link IterableArray} zusammengefasst.
		 * 
		 * @see Iterables#chainedIterable(Iterable...)
		 * @see Iterables#chainedIterable(Iterable, Iterable)
		 * @param <GEntry> Typ der Elemente.
		 * @param iterable1 {@link Iterable} 1.
		 * @param iterable2 {@link Iterable} 2.
		 * @return {@link IterableArray}.
		 */
		@SuppressWarnings ("unchecked")
		public static <GEntry> Iterable<? extends Iterable<? extends GEntry>> valueOf(final Iterable<? extends GEntry> iterable1,
			final Iterable<? extends GEntry> iterable2) {
			if (iterable1 instanceof ChainedIterable<?>) {
				final Iterable<?> iterableA = ((ChainedIterable<?>)iterable1).iterable;
				if (iterableA instanceof IterableArray<?>) {
					final Iterable<?>[] arrayA = ((IterableArray<?>)iterableA).array;
					if (iterable2 instanceof ChainedIterable<?>) {
						final Iterable<?> iterableB = ((ChainedIterable<?>)iterable1).iterable;
						if (iterableB instanceof IterableArray) {
							final Iterable<?>[] arrayB = ((IterableArray<?>)iterableB).array;
							final Iterable<?>[] array = new Iterable<?>[arrayA.length + arrayB.length];
							System.arraycopy(arrayA, 0, array, 0, arrayA.length);
							System.arraycopy(arrayB, 0, array, arrayA.length, arrayB.length);
							return new IterableArray<GEntry>(array);
						}
					}
					if ((iterable2 != null) && (iterable2 != VoidIterable.INSTANCE)) {
						final Iterable<?>[] array = new Iterable<?>[arrayA.length + 1];
						System.arraycopy(arrayA, 0, array, 0, arrayA.length);
						array[arrayA.length] = iterable2;
						return new IterableArray<GEntry>(array);
					}
					return (Iterable<? extends Iterable<? extends GEntry>>)iterableA;
				}
			}
			if (iterable2 instanceof ChainedIterable<?>) {
				final Iterable<?> iterableB = ((ChainedIterable<?>)iterable1).iterable;
				if ((iterable1 != null) && (iterable1 != VoidIterable.INSTANCE)) {
					if (iterableB instanceof IterableArray) {
						final Iterable<?>[] arrayB = ((IterableArray<?>)iterableB).array;
						final Iterable<?>[] array = new Iterable<?>[1 + arrayB.length];
						System.arraycopy(arrayB, 0, array, 1, arrayB.length);
						array[0] = iterable1;
						return new IterableArray<GEntry>(array);
					}
				}
				return (Iterable<? extends Iterable<? extends GEntry>>)iterableB;
			}
			if ((iterable1 != null) && (iterable1 != VoidIterable.INSTANCE)) {
				if ((iterable2 != null) && (iterable2 != VoidIterable.INSTANCE)) return new IterableArray<GEntry>(iterable1, iterable2);
				return new IterableArray<GEntry>(iterable1);
			}
			if ((iterable2 != null) && (iterable2 != VoidIterable.INSTANCE)) return new IterableArray<GEntry>(iterable2);
			return new IterableArray<GEntry>();
		}

		/**
		 * Dieses Feld speichert das Array der {@link Iterable}s.
		 */
		Iterable<?>[] array;

		/**
		 * Dieser Konstruktor initialisiert das Array der {@link Iterable}s.
		 * 
		 * @param array {@link Iterable}-Array.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable}-Array {@code null} ist.
		 */
		public IterableArray(final Iterable<?>... array) throws NullPointerException {
			if (array == null) throw new NullPointerException();
			this.array = array;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Iterable<? extends GEntry>> iterator() {
			return new Iterator<Iterable<? extends GEntry>>() {

				int index = 0;

				int count = IterableArray.this.array.length;

				@Override
				public boolean hasNext() {
					return this.index < this.count;
				}

				@SuppressWarnings ({"unchecked"})
				@Override
				public Iterable<? extends GEntry> next() {
					return (Iterable<? extends GEntry>)IterableArray.this.array[this.index++];
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

			};
		}

	}

	/**
	 * Diese Klasse implementiert ein abstraktes {@link Iterable}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	static abstract class AbstractIterable<GEntry> implements Iterable<GEntry>, UseToString {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten delegierenden {@link Iterable}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 * @param <GEntry2> Typ der Elemente des gegebenen {@link Iterable}.
	 */
	static abstract class AbstractDelegatingIterable<GEntry, GEntry2> extends AbstractIterable<GEntry> {

		/**
		 * Dieses Feld speichert den {@link Iterable}.
		 */
		final Iterable<? extends GEntry2> iterable;

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterable}.
		 * 
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn der gegebene {@link Iterable} {@code null} ist.
		 */
		public AbstractDelegatingIterable(final Iterable<? extends GEntry2> iterable) throws NullPointerException {
			if (iterable == null) throw new NullPointerException();
			this.iterable = iterable;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof AbstractDelegatingIterable<?, ?>)) return false;
			final AbstractDelegatingIterable<?, ?> data = (AbstractDelegatingIterable<?, ?>)object;
			return Objects.equals(this.iterable, data.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert das leere {@link Iterable}.
	 * 
	 * @see VoidIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class VoidIterable extends AbstractIterable<Object> {

		/**
		 * Dieses Feld speichert das {@link VoidIterable}.
		 */
		public static final Iterable<?> INSTANCE = new VoidIterable();

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public Iterator<Object> iterator() {
			return (Iterator<Object>)VoidIterator.INSTANCE;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof VoidIterable);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable} über ein einzelnes, gegebenes Element.
	 * 
	 * @see EntryIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elements.
	 */
	public static final class EntryIterable<GEntry> extends AbstractIterable<GEntry> {

		/**
		 * Dieses Feld speichert das Element.
		 */
		final GEntry entry;

		/**
		 * Dieser Konstruktor initialisiert das Element.
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
			return new EntryIterator<GEntry>(this.entry);
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
			if (object == this) return true;
			if (!(object instanceof EntryIterable<?>)) return false;
			final EntryIterable<?> data = (EntryIterable<?>)object;
			return Objects.equals(this.entry, data.entry);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.entry);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable} über ein einzelnes Element, dass durch einen gegebenen {@link Builder} bereitgestellt wird.
	 * 
	 * @see BuilderIterator
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ des Elements.
	 */
	public static final class BuilderIterable<GEntry> extends AbstractIterable<GEntry> {

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
		public BuilderIterable(final Builder<? extends GEntry> builder) throws NullPointerException {
			if (builder == null) throw new NullPointerException();
			this.builder = builder;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return new BuilderIterator<GEntry>(this.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof BuilderIterable<?>)) return false;
			final BuilderIterable<?> data = (BuilderIterable<?>)object;
			return Objects.equals(this.builder, data.builder);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.builder);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable}, das eine gegebene Anzahl an {@link Integer}s ab dem Wert {@code 0} liefert.
	 * 
	 * @see IntegerIterator
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class IntegerIterable extends AbstractIterable<Integer> {

		/**
		 * Dieses Feld speichert die Anzahl.
		 */
		final int count;

		/**
		 * Dieser Konstruktor initialisiert die Anzahl.
		 * 
		 * @param count Anzahl.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public IntegerIterable(final int count) throws IllegalArgumentException {
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Integer> iterator() {
			return new IntegerIterator(this.count);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof IntegerIterable)) return false;
			final IntegerIterable data = (IntegerIterable)object;
			return this.count == data.count;
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
	 * Diese Klasse implementiert ein {@link Iterable}, der kein Element eines gegebenen {@link Iterable}s mehrfach liefert.
	 * 
	 * @see UniqueIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class UniqueIterable<GEntry> extends AbstractDelegatingIterable<GEntry, GEntry> {

		/**
		 * Dieser Konstruktor initialisiert das {@link Iterable}.
		 * 
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
		 */
		public UniqueIterable(final Iterable<? extends GEntry> iterable) throws NullPointerException {
			super(iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return new UniqueIterator<GEntry>(this.iterable.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof UniqueIterable<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable}, das eine begrenzte Anzahl an Elementen eines gegebenen {@link Iterable}s liefert.
	 * 
	 * @see LimitedIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class LimitedIterable<GEntry> extends AbstractDelegatingIterable<GEntry, GEntry> {

		/**
		 * Dieses Feld speichert die Anzahl.
		 */
		final int count;

		/**
		 * Dieser Konstruktor initialisiert Anzahl und {@link Iterable}.
		 * 
		 * @param count Anzahl.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
		 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
		 */
		public LimitedIterable(final int count, final Iterable<? extends GEntry> iterable) throws NullPointerException, IllegalArgumentException {
			super(iterable);
			if (count < 0) throw new IllegalArgumentException();
			this.count = count;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return new LimitedIterator<GEntry>(this.count, this.iterable.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.count, this.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof LimitedIterable<?>)) return false;
			final LimitedIterable<?> data = (LimitedIterable<?>)object;
			return (this.count == data.count) && Objects.equals(this.iterable, data.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.count, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable}, der nur die von einem gegebenen {@link Filter} akzeptierten Elemente eines gegebenen {@link Iterable}s
	 * liefert.
	 * 
	 * @see FilteredIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class FilteredIterable<GEntry> extends AbstractDelegatingIterable<GEntry, GEntry> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GEntry> filter;

		/**
		 * Dieser Konstruktor initialisiert das {@link Filter} und {@link Iterable}.
		 * 
		 * @param filter {@link Filter}.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public FilteredIterable(final Filter<? super GEntry> filter, final Iterable<? extends GEntry> iterable) throws NullPointerException {
			super(iterable);
			if (filter == null) throw new NullPointerException();
			this.filter = filter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return new FilteredIterator<GEntry>(this.filter, this.iterable.iterator());
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
			if (object == this) return true;
			if (!(object instanceof FilteredIterable<?>)) return false;
			final FilteredIterable<?> data = (FilteredIterable<?>)object;
			return Objects.equals(this.filter, data.filter) && Objects.equals(this.iterable, data.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.filter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert ein verkettetes {@link Iterable}, das alle Elemente der gegebenen {@link Iterable}s in der gegebenen Reihenfolge liefert.
	 * 
	 * @see ChainedIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class ChainedIterable<GEntry> extends AbstractDelegatingIterable<GEntry, Iterable<? extends GEntry>> {

		/**
		 * Dieser Konstruktor initialisiert die {@link Iterable}.
		 * 
		 * @param iterable {@link Iterable}-{@link Iterable}.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
		 */
		public ChainedIterable(final Iterable<? extends Iterable<? extends GEntry>> iterable) throws NullPointerException {
			super(iterable);
		}

		/**
		 * Dieser Konstruktor initialisiert das {@link Iterable} mit der Verkettung der gegebenen.
		 * 
		 * @param iterable1 {@link Iterable} 1.
		 * @param iterable2 {@link Iterable} 2.
		 */
		public ChainedIterable(final Iterable<? extends GEntry> iterable1, final Iterable<? extends GEntry> iterable2) {
			super(IterableArray.valueOf(iterable1, iterable2));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings ("unchecked")
		@Override
		public Iterator<GEntry> iterator() {
			return new ChainedIterator<GEntry>(new ConvertedIterator<Iterable<? extends GEntry>, Iterator<? extends GEntry>>(
				(IterableIteratorConverter<GEntry>)IterableIteratorConverter.INSTANCE, this.iterable.iterator()));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ChainedIterable<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable}, das die von einem gegebenen {@link Converter} konvertierten Elemente eines gegebenen {@link Iterable}s
	 * liefert.
	 * 
	 * @see ConvertedIterator
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterable} s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente.
	 */
	public static final class ConvertedIterable<GInput, GOutput> extends AbstractDelegatingIterable<GOutput, GInput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Converter} und {@link Iterable}.
		 * 
		 * @param converter {@link Converter}.
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConvertedIterable(final Converter<? super GInput, ? extends GOutput> converter, final Iterable<? extends GInput> iterable)
			throws NullPointerException {
			super(iterable);
			if (converter == null) throw new NullPointerException();
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GOutput> iterator() {
			return new ConvertedIterator<GInput, GOutput>(this.converter, this.iterable.iterator());
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
			if (object == this) return true;
			if (!(object instanceof EntryIterable<?>)) return false;
			final ConvertedIterable<?, ?> data = (ConvertedIterable<?, ?>)object;
			return Objects.equals(this.converter, data.converter) && Objects.equals(this.iterable, data.iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.converter, this.iterable);
		}

	}

	/**
	 * Diese Klasse implementiert ein {@link Iterable}, das den {@link Iterator} eins gegebenen {@link Iterable} als {@link UnmodifiableIterator} bereitstellt.
	 * 
	 * @see UnmodifiableIterator
	 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class UnmodifiableIterable<GEntry> extends AbstractDelegatingIterable<GEntry, GEntry> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Iterable}.
		 * 
		 * @param iterable {@link Iterable}.
		 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
		 */
		public UnmodifiableIterable(final Iterable<? extends GEntry> iterable) throws NullPointerException {
			super(iterable);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<GEntry> iterator() {
			return new UnmodifiableIterator<GEntry>(this.iterable.iterator());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof UnmodifiableIterable<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Converter}, der den {@link Iterator} eines {@link Iterable}s ermittelt.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GEntry> Typ der Elemente.
	 */
	public static final class IterableIteratorConverter<GEntry> extends AbstractConverter<Iterable<? extends GEntry>, Iterator<? extends GEntry>> {

		/**
		 * Dieses Feld speichert den {@link IterableIteratorConverter}.
		 */
		public static final IterableIteratorConverter<?> INSTANCE = new IterableIteratorConverter<Object>();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<? extends GEntry> convert(final Iterable<? extends GEntry> input) {
			return input.iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof IterableIteratorConverter);
		}

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
		if ((iterable == null) || (collection == null)) throw new NullPointerException();
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
		if ((collection == null) || (iterable == null)) throw new NullPointerException();
		return Iterators.retainAll(collection, iterable.iterator());
	}

	/**
	 * Diese Methode fügt alle Elemente des gegebenen {@link Iterable}s in die gegebene {@link Collection} ein und gibt nur bei Veränderungen an der
	 * {@link Collection} {@code true} zurück.
	 * 
	 * @see Iterators#appendAll(Collection, Iterator)
	 * @param <GEntry> Typ der Elemente.
	 * @param collection {@link Collection}.
	 * @param iterable {@link Iterable}.
	 * @return {@code true} bei Veränderungen an der {@link Collection}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry> boolean appendAll(final Collection<GEntry> collection, final Iterable<? extends GEntry> iterable) throws NullPointerException {
		if ((collection == null) || (iterable == null)) throw new NullPointerException();
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
		if (iterable == null) throw new NullPointerException();
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
		if ((iterable == null) || (collection == null)) throw new NullPointerException();
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
		if ((collection == null) || (iterable == null)) throw new NullPointerException();
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
		if ((collection == null) || (iterable == null)) throw new NullPointerException();
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
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterable<GEntry> iterable(final Iterable<? extends GEntry> iterable) {
		return (Iterable<GEntry>)((iterable != null) ? iterable : VoidIterable.INSTANCE);
	}

	/**
	 * Diese Methode gibt einen {@link Converter} zurück, der den {@link Iterator} eines {@link Iterable} ermittelt.
	 * 
	 * @see IterableIteratorConverter
	 * @see Converter
	 * @see Iterable#iterator()
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link IterableIteratorConverter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> IterableIteratorConverter<GEntry> iterableIteratorConverter() {
		return (IterableIteratorConverter<GEntry>)IterableIteratorConverter.INSTANCE;
	}

	/**
	 * Diese Methode gibt das leere {@link Iterable} zurück.
	 * 
	 * @see VoidIterable
	 * @see Iterators#voidIterator()
	 * @param <GEntry> Typ der Elemente.
	 * @return {@link VoidIterable}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GEntry> Iterable<GEntry> voidIterable() {
		return (Iterable<GEntry>)VoidIterable.INSTANCE;
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} über das gegebene Element zurück.
	 * 
	 * @see EntryIterable
	 * @see Iterators#entryIterator(Object)
	 * @param <GEntry> Typ des Elements.
	 * @param entry Element.
	 * @return {@link EntryIterable}
	 */
	public static <GEntry> EntryIterable<GEntry> entryIterable(final GEntry entry) {
		return new EntryIterable<GEntry>(entry);
	}

	/**
	 * Diese Methode gibt ein {@link Iterable} über das durch den gegebenen {@link Builder} bereitgestellte Element zurück.
	 * 
	 * @see BuilderIterable
	 * @param <GEntry> Typ des Elements.
	 * @param builder {@link Builder}.
	 * @return {@link BuilderIterable}
	 * @throws NullPointerException Wenn der gegebene {@link Builder} {@code null} ist.
	 */
	public static <GEntry> BuilderIterable<GEntry> builderIterable(final Builder<? extends GEntry> builder) throws NullPointerException {
		return new BuilderIterable<GEntry>(builder);
	}

	/**
	 * Diese Methode erzeugt ein {@link Iterable}, der kein Element eines gegebenen {@link Iterable}s mehrfach liefert, und gibt es zurück.
	 * 
	 * @see UniqueIterable
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@link UniqueIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
	 */
	public static <GEntry> Iterable<GEntry> uniqueIterable(final Iterable<? extends GEntry> iterable) throws NullPointerException {
		return new UniqueIterable<GEntry>(iterable);
	}

	/**
	 * Diese Methode erzeugt ein {@link Iterable}, das maximal die gegebene Anzahl an Elementen des gegebenen {@link Iterable}s liefert, und gibt es zurück.
	 * 
	 * @see LimitedIterable
	 * @param <GEntry> Typ der Elemente.
	 * @param count Anzahl.
	 * @param iterable {@link Iterable}.
	 * @return {@link LimitedIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
	 * @throws IllegalArgumentException Wenn die gegebene Anzahl negativ ist.
	 */
	public static <GEntry> LimitedIterable<GEntry> limitedIterable(final int count, final Iterable<? extends GEntry> iterable) throws NullPointerException,
		IllegalArgumentException {
		return new LimitedIterable<GEntry>(count, iterable);
	}

	/**
	 * Diese Methode erzeugt ein {@link Iterable}, der nur die vom gegebenen {@link Filter} akzeptierten Elemente des gegebenen {@link Iterable}s liefert, und
	 * gibt es zurück.
	 * 
	 * @see FilteredIterable
	 * @param <GEntry> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @param iterable {@link Iterable}.
	 * @return {@link FilteredIterable}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GEntry> FilteredIterable<GEntry> filteredIterable(final Filter<? super GEntry> filter, final Iterable<? extends GEntry> iterable)
		throws NullPointerException {
		return new FilteredIterable<GEntry>(filter, iterable);
	}

	/**
	 * Diese Methode erzeugt ein verkettetes {@link Iterable}, das alle Elemente der gegebenen {@link Iterable}s in der gegebenen Reihenfolge liefert, und gibt es
	 * zurück.
	 * 
	 * @see ChainedIterable
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable {@link Iterable}-{@link Iterable}.
	 * @return {@link ChainedIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
	 */
	public static <GEntry> ChainedIterable<GEntry> chainedIterable(final Iterable<? extends Iterable<? extends GEntry>> iterable) throws NullPointerException {
		return new ChainedIterable<GEntry>(iterable);
	}

	/**
	 * Diese Methode erzeugt ein verkettetes {@link Iterable}, das alle Elemente der gegebenen {@link Iterable}s in der gegebenen Reihenfolge liefert, und gibt es
	 * zurück.
	 * 
	 * @see Iterables#chainedIterable(Iterable)
	 * @see Iterables#chainedIterable(Iterable, Iterable)
	 * @param <GEntry> Typ der Elemente.
	 * @param iterables {@link Iterable}-Array.
	 * @return {@link ChainedIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable}-Array {@code null} ist.
	 */
	public static <GEntry> ChainedIterable<GEntry> chainedIterable(final Iterable<? extends GEntry>... iterables) throws NullPointerException {
		if (iterables == null) throw new NullPointerException();
		if (iterables.length == 2) return Iterables.chainedIterable(iterables[0], iterables[1]);
		return Iterables.chainedIterable(new IterableArray<GEntry>(iterables.clone()));
	}

	/**
	 * Diese Methode erzeugt ein verkettetes {@link Iterable}, das alle Elemente der gegebenen {@link Iterable}s in der gegebenen Reihenfolge liefert, und gibt es
	 * zurück.
	 * 
	 * @see ChainedIterable
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable1 {@link Iterable} 1.
	 * @param iterable2 {@link Iterable} 2.
	 * @return {@link ChainedIterable}.
	 */
	public static <GEntry> ChainedIterable<GEntry> chainedIterable(final Iterable<? extends GEntry> iterable1, final Iterable<? extends GEntry> iterable2) {
		return new ChainedIterable<GEntry>(iterable1, iterable2);
	}

	/**
	 * Diese Methode erzeugt ein {@link Iterable}, das die vom gegebenen {@link Converter} konvertierten Elemente des gegebenen {@link Iterable}s liefert, und
	 * gibt es zurück.
	 * 
	 * @see ConvertedIterable
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Converter}s sowie der Elemente des gegebenen {@link Iterable}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Elemente des erzeugten {@link Iterable}s.
	 * @param converter {@link Converter}.
	 * @param iterable {@link Iterable}.
	 * @return {@link ConvertedIterable}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> ConvertedIterable<GInput, GOutput> convertedIterable(final Converter<? super GInput, ? extends GOutput> converter,
		final Iterable<? extends GInput> iterable) throws NullPointerException {
		return new ConvertedIterable<GInput, GOutput>(converter, iterable);
	}

	/**
	 * Diese Methode erzeugt ein {@link Iterable}, das den {@link Iterator} eins gegebenen {@link Iterable} als {@link UnmodifiableIterator} bereitstellt, und
	 * gibt es zurück.
	 * 
	 * @see UnmodifiableIterable
	 * @param <GEntry> Typ der Elemente.
	 * @param iterable {@link Iterable}.
	 * @return {@link UnmodifiableIterable}.
	 * @throws NullPointerException Wenn das gegebene {@link Iterable} {@code null} ist.
	 */
	public static <GEntry> UnmodifiableIterable<GEntry> unmodifiableIterable(final Iterable<? extends GEntry> iterable) throws NullPointerException {
		return new UnmodifiableIterable<GEntry>(iterable);
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Iterables() {
	}

}
