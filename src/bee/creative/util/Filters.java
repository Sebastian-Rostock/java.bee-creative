package bee.creative.util;

import java.util.Map;
import bee.creative.util.Converters.CachedConverter;
import bee.creative.util.Converters.ConverterLink;
import bee.creative.util.Pointers.HardPointer;
import bee.creative.util.Pointers.SoftPointer;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Filter
 * Filtern}.
 * 
 * @see Filter
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Filters {

	/**
	 * Diese Klasse implementiert ein abstraktes Objekt, dass auf einen {@link Filter Filter} verweist.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des gegebenen {@link Filter Filters}.
	 */
	static abstract class FilterLink<GInput> {

		/**
		 * Dieses Feld speichert den {@link Filter Filter}.
		 */
		final Filter<? super GInput> filter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} <code>null</code> ist.
		 */
		public FilterLink(final Filter<? super GInput> filter) throws NullPointerException {
			if(filter == null) throw new NullPointerException();
			this.filter = filter;
		}

		/**
		 * Diese Methode gibt den {@link Filter Filter} zurück.
		 * 
		 * @return {@link Filter Filter}.
		 */
		public Filter<? super GInput> filter() {
			return this.filter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final FilterLink<?> data = (FilterLink<?>)object;
			return Objects.equals(this.filter, data.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen delegierenden {@link Filter Filter}, der seine Berechnungen an zwei gegebene
	 * {@link Filter Filter} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static abstract class JunctionFilter<GInput> implements Filter<GInput> {

		/**
		 * Dieses Feld speichert den primären {@link Filter Filter}.
		 */
		final Filter<? super GInput> filter1;

		/**
		 * Dieses Feld speichert den sekundären {@link Filter Filter}.
		 */
		final Filter<? super GInput> filter2;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter Filter}.
		 * 
		 * @param filter1 primärer {@link Filter Filter}.
		 * @param filter2 sekundärer {@link Filter Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
		 */
		public JunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			if((filter1 == null) || (filter2 == null)) throw new NullPointerException();
			this.filter1 = filter1;
			this.filter2 = filter2;
		}

		/**
		 * Diese Methode gibt den primären {@link Filter Filter} zurück.
		 * 
		 * @return primärer {@link Filter Filter}.
		 */
		public Filter<? super GInput> filter1() {
			return this.filter1;
		}

		/**
		 * Diese Methode gibt den sekundären {@link Filter Filter} zurück.
		 * 
		 * @return sekundärer {@link Filter Filter}.
		 */
		public Filter<? super GInput> filter2() {
			return this.filter2;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.filter1, this.filter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			final JunctionFilter<?> data = (JunctionFilter<?>)object;
			return Objects.equals(this.filter1, data.filter1, this.filter2, data.filter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Inversion (<code>!</code>-Operator) eines gegebenen
	 * {@link Filter Filters} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn der gegebene
	 * {@link Filter Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann ab, wenn der gegebene {@link Filter
	 * Filter} die Eingabe akzeptiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class InvertFilter<GInput> extends FilterLink<GInput> implements Filter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} <code>null</code> ist.
		 */
		public InvertFilter(final Filter<? super GInput> filter) throws NullPointerException {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return !this.filter.accept(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof InvertFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("invertFilter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Disjunktion (<code>||</code>-Operator) zweier
	 * gegebener {@link Filter Filter} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn
	 * mindestens einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab,
	 * wenn die beiden gegebenen {@link Filter Filter} die Eingabe ablehnen.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class DisjunctionFilter<GInput> extends JunctionFilter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter Filter}.
		 * 
		 * @param filter1 primärer {@link Filter Filter}.
		 * @param filter2 sekundärer {@link Filter Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
		 */
		public DisjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			super(filter1, filter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.filter1.accept(input) || this.filter2.accept(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof DisjunctionFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("disjunctionFilter", this.filter1, this.filter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Konjunktion (<code>&amp;&amp;</code>-Operator)
	 * zweier gegebener {@link Filter Filter} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn
	 * die beiden gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn
	 * mindestens einer der gegebenen {@link Filter Filter} die Eingabe ablehnen.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class ConjunctionFilter<GInput> extends JunctionFilter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter Filter}.
		 * 
		 * @param filter1 primärer {@link Filter Filter}.
		 * @param filter2 sekundärer {@link Filter Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
		 */
		public ConjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			super(filter1, filter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.filter1.accept(input) && this.filter2.accept(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConjunctionFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("conjunctionFilter", this.filter1, this.filter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Äquivalenz (<code>==</code>-Operator) zweier
	 * gegebener {@link Filter Filter} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn die
	 * beiden gegebenen {@link Filter Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe genau dann
	 * ab, wenn einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und der andere sie ablehnt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class EquivalenceFilter<GInput> extends JunctionFilter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter Filter}.
		 * 
		 * @param filter1 primärer {@link Filter Filter}.
		 * @param filter2 sekundärer {@link Filter Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
		 */
		public EquivalenceFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			super(filter1, filter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.filter1.accept(input) == this.filter2.accept(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof EquivalenceFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("equivalenceFilter", this.filter1, this.filter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen gepufferten {@link Filter Filter}, der die von einem gegebene {@link Filter
	 * Filter} erzeugten Ausgaben in einer {@link Map Abbildung} von Schlüsseln auf Werte verwaltet. Die Schlüssel werden
	 * dabei über {@link HardPointer Hard-Pointers} auf Eingaben und die Werte als {@link Pointer Verweise} auf die
	 * Ausgaben des gegebenen {@link Filter Filters} realisiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @see Converters#cachedConverter(int, int, int, Converter)
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class CachedFilter<GInput> extends FilterLink<GInput> implements Filter<GInput>,
		Converter<GInput, Boolean> {

		/**
		 * Dieses Feld speichert den {@link Converter Converter}.
		 */
		final CachedConverter<GInput, Boolean> cache;

		/**
		 * Dieser Konstrukteur initialisiert den gepuferten {@link Filter Filter}.
		 * 
		 * @param limit Maximum für die Anzahl der Einträge in der {@link Map Abbildung}.
		 * @param mode Modus, in dem die {@link Pointer Verweise} auf die Eingabe-Datensätze für die Schlüssel der
		 *        {@link Map Abbildung} erzeugt werden.
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der {@link Filter Filter} <code>null</code> ist.
		 * @throws IllegalArgumentException Wenn der gegebene Modi ungültig ist.
		 */
		public CachedFilter(final int limit, final int mode, final Filter<? super GInput> filter) {
			super(filter);
			this.cache = new CachedConverter<GInput, Boolean>(limit, mode, Pointers.HARD, this);
		}

		/**
		 * Diese Methode leert die Abbildung.
		 * 
		 * @see CachedConverter#clear()
		 */
		public void clear() {
			this.cache.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Boolean convert(final GInput input) {
			return Boolean.valueOf(this.filter.accept(input));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.cache.convert(input).booleanValue();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if((object == this) || Objects.equals(object, this.filter)) return true;
			if(!(object instanceof CachedFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("cachedFilter", this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, dessen konvertierte Eingabe von einem gegebenen
	 * {@link Filter Filter} bewertet wird. Der {@link Filter Filter} konvertiert seine Eingabe mit einem gegebenen
	 * {@link Converter Converter} zur Eingabe eines gegebenen {@link Filter Filters}. Der {@link Filter Filter}
	 * akzeptiert eine Eingabe nur dann, wenn der gegebenen {@link Filter Filter} die konvertierte Eingabe akzeptiert und
	 * er lehnt eine Eingabe ab, wenn der gegebenen {@link Filter Filter} die konvertierte Eingabe ablehnt.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des {@link Filter Filters} sowie des gegebenen {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie der Eingabe des gegebenen
	 *        {@link Filter Filters}.
	 */
	public static final class ConvertedFilter<GInput, GOutput> extends ConverterLink<GInput, GOutput> implements
		Filter<GInput> {

		/**
		 * Dieses Feld speichert den {@link Filter Filter}.
		 */
		final Filter<? super GOutput> filter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter Filter} und {@link Converter Converter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @param converter {@link Converter Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} oder der gegebene {@link Converter
		 *         Converter} <code>null</code> ist.
		 */
		public ConvertedFilter(final Filter<? super GOutput> filter,
			final Converter<? super GInput, ? extends GOutput> converter) {
			super(converter);
			if(filter == null) throw new NullPointerException();
			this.filter = filter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.filter.accept(this.converter.convert(input));
		}

		/**
		 * Diese Methode gibt den {@link Filter Filter} zurück.
		 * 
		 * @return {@link Filter Filter}.
		 */
		public Filter<? super GOutput> filter() {
			return this.filter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.filter, this.converter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ConvertedFilter<?, ?>)) return false;
			final ConvertedFilter<?, ?> data = (ConvertedFilter<?, ?>)object;
			return super.equals(object) && Objects.equals(this.filter, data.filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("convertedFilter", this.converter, this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter Filter}, der einen gegebenen {@link Filter Filter} synchronisiert.
	 * Die Synchronisation erfolgt via <code>synchronized(filter)</code> auf dem gegebenen {@link Filter Filter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class SynchronizedFilter<GInput> extends FilterLink<GInput> implements Filter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} <code>null</code> ist.
		 */
		public SynchronizedFilter(final Filter<? super GInput> filter) {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			synchronized(this.filter){
				return this.filter.accept(input);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if((object == this) || Objects.equals(object, this.filter)) return true;
			if(!(object instanceof SynchronizedFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("synchronizedFilter", this.filter);
		}

	}

	/**
	 * Dieses Feld speichert den {@link Filter Filter}, der von {@link Filters#nullFilter()} zurück gegeben wird.
	 */
	static final Filter<?> NULL_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return input != null;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("voidFilter");
		}

	};

	/**
	 * Dieses Feld speichert einen {@link Filter Filter}, der von {@link Filters#defaultFilter(boolean)} zurück gegeben
	 * wird.
	 */
	static final Filter<?> DEFAULT_TRUE_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return true;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("defaultFilter", true);
		}

	};

	/**
	 * Dieses Feld speichert einen {@link Filter Filter}, der von {@link Filters#defaultFilter(boolean)} zurück gegeben
	 * wird.
	 */
	static final Filter<?> DEFAULT_FALSE_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("defaultFilter", false);
		}

	};

	/**
	 * Diese Methode gibt einen {@link Filter Filter} zurück, der alle Eingaben akzeptiert, die nicht <code>null</code>
	 * sind.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link Filter Filter}, der nur <code>null</code>-Eingaben ablehnt.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> nullFilter() {
		return (Filter<GInput>)Filters.NULL_FILTER;
	}

	/**
	 * Diese Methode gibt einen {@link Filter Filter} zurück, dessen {@link Filter#accept(Object) Akzeptanzmethode} für
	 * jede Eingabe den gegebenen Akzeptanzmodus liefert. Wenn der Akzeptanzmodus <code>true</code> ist, akzeptiert der
	 * {@link Filter Filter} jede Eingabe und wenn der Akzeptanzmodus <code>false</code> ist, lehnt der {@link Filter
	 * Filter} jede Eingabe ab.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param accept Akzeptanzmodus.
	 * @return Standatd-{@link Filter Filter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> defaultFilter(final boolean accept) {
		return (Filter<GInput>)(accept ? Filters.DEFAULT_TRUE_FILTER : Filters.DEFAULT_FALSE_FILTER);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Inversion (<code>!</code>-Operator) des gegebenen
	 * {@link Filter Filters} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine
	 * Eingabe nur dann, wenn der gegebene {@link Filter Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann
	 * ab, wenn der gegebene {@link Filter Filter} die Eingabe akzeptiert.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter Filter}.
	 * @return Inversion-{@link Filter Filter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} <code>null</code> ist.
	 */
	public static <GInput> Filter<GInput> invertFilter(final Filter<? super GInput> filter) throws NullPointerException {
		return new InvertFilter<GInput>(filter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Disjunktion (<code>||</code>-Operator) der gegebenen
	 * {@link Filter Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine Eingabe
	 * nur dann, wenn mindestens einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe
	 * genau dann ab, wenn beide gegebenen {@link Filter Filter} die Eingabe ablehnen.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter Filter} 1.
	 * @param filter2 {@link Filter Filter} 2.
	 * @return {@link DisjunctionFilter Disjunction-Filter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
	 */
	public static <GInput> Filter<GInput> disjunctionFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new DisjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Konjunktion (<code>&amp;&amp;</code>-Operator) der
	 * gegebenen {@link Filter Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert
	 * eine Eingabe nur dann, wenn alle der gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die
	 * Eingabe genau dann ab, wenn mindestens einer der gegebenen {@link Filter Filter} die Eingabe ablehnen.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter Filter} 1.
	 * @param filter2 {@link Filter Filter} 2.
	 * @return {@link ConjunctionFilter Conjunction-Filter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
	 */
	public static <GInput> Filter<GInput> conjunctionFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new ConjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Äquivalenz (<code>==</code>-Operator) der gegebenen
	 * {@link Filter Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine Eingabe
	 * nur dann, wenn beide gegebenen {@link Filter Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe
	 * genau dann ab, wenn einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und der andere sie ablehnt.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter Filter} 1.
	 * @param filter2 {@link Filter Filter} 2.
	 * @return {@link EquivalenceFilter Equivalence-Filter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} <code>null</code> ist.
	 */
	public static <GInput> Filter<GInput> equivalenceFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new EquivalenceFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Filter Filter} und gibt ihn zurück. Der erzeugte {@link Filter
	 * Filter} verwaltet die vom gegebenen {@link Filter Filter} erzeugten Ausgaben in einer {@link Map Abbildung} von
	 * Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link HardPointer harte Verweise} auf Eingaben und die Werte
	 * als {@link SoftPointer Soft-Pointers} auf die Ausgaben des gegebenen {@link Filter Filters} realisiert. Die Anzahl
	 * der Einträge in der {@link Map Abbildung} sind nicht beschränkt. Der erzeute {@link Filter Filter} verwendet damit
	 * einen speichersensitiven, assoziativen Cache.
	 * 
	 * @see Filters#cachedFilter(int, int, Filter)
	 * @see Converters#cachedConverter(int, int, int, Converter)
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter Filter}.
	 * @return {@link CachedFilter Cached-Filter}.
	 * @throws NullPointerException Wenn der {@link Filter Filter} <code>null</code> ist.
	 * @throws IllegalArgumentException Wenn der gegebene Modi ungültig ist.
	 */
	public static <GInput> Filter<GInput> cachedFilter(final Filter<? super GInput> filter) throws NullPointerException {
		return Filters.cachedFilter(-1, Pointers.SOFT, filter);
	}

	/**
	 * Diese Methode erzeugt einen gepufferten {@link Filter Filter} und gibt ihn zurück. Der erzeugte {@link Filter
	 * Filter} verwaltet die vom gegebenen {@link Filter Filter} erzeugten Ausgaben in einer {@link Map Abbildung} von
	 * Schlüsseln auf Werte. Die Schlüssel werden dabei über {@link HardPointer harte Verweise} auf Eingaben und die Werte
	 * als {@link Pointer Verweise} auf die Ausgaben des gegebenen {@link Filter Filters} realisiert.
	 * 
	 * @see Converters#cachedConverter(int, int, int, Converter)
	 * @param <GInput> Typ der Eingabe.
	 * @param limit Maximum für die Anzahl der Einträge in der {@link Map Abbildung}.
	 * @param mode Modus, in dem die {@link Pointer Verweise} auf die Eingabe-Datensätze für die Schlüssel der {@link Map
	 *        Abbildung} erzeugt werden.
	 * @param filter {@link Filter Filter}.
	 * @return {@link CachedFilter Cached-Filter}.
	 * @throws NullPointerException Wenn der {@link Filter Filter} <code>null</code> ist.
	 * @throws IllegalArgumentException Wenn der gegebene Modi ungültig ist.
	 */
	public static <GInput> Filter<GInput> cachedFilter(final int limit, final int mode,
		final Filter<? super GInput> filter) throws NullPointerException {
		return new CachedFilter<GInput>(limit, mode, filter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, dessen konvertierte Eingabe vom gegebenen {@link Filter Filter}
	 * bewertet wird, und gibt diesen zurück. Der erzeugte {@link Filter Filter} konvertiert seine Eingabe mit dem
	 * gegebenen {@link Converter Converter} zur Eingabe des gegebenen {@link Filter Filters}. Der erzeugte {@link Filter
	 * Filter} akzeptiert eine Eingabe nur dann, wenn der gegebenen {@link Filter Filter} die konvertierte Eingabe
	 * akzeptiert und er lehnt eine Eingabe ab, wenn der gegebenen {@link Filter Filter} die konvertierte Eingabe ablehnt.
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des {@link Filter Filters} sowie des gegebenen {@link Converter Converters}.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter Converters} sowie der Eingabe des gegebenen
	 *        {@link Filter Filters}.
	 * @param filter {@link Filter Filter}.
	 * @param converter {@link Converter Converter}.
	 * @return {@link ConvertedFilter Converted-Filter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} oder der gegebene {@link Converter Converter}
	 *         <code>null</code> ist.
	 */
	public static <GInput, GOutput> Filter<GInput> convertedFilter(final Filter<? super GOutput> filter,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new ConvertedFilter<GInput, GOutput>(filter, converter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der den gegebenen {@link Filter Filter} synchronisiert, und gibt
	 * diesen zurück. Die Synchronisation erfolgt via <code>synchronized(filter)</code> auf dem gegebenen {@link Filter
	 * Filter}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter Filter}.
	 * @return <code>Synchronized</code>-{@link Filter Filter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} <code>null</code> ist.
	 */
	public static <GInput> Filter<GInput> synchronizedFilter(final Filter<? super GInput> filter)
		throws NullPointerException {
		return new SynchronizedFilter<GInput>(filter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Filters() {
	}

}
