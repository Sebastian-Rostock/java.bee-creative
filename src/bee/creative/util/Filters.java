package bee.creative.util;

import bee.creative.util.Converters.ConverterLink;

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
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
		 */
		public FilterLink(final Filter<? super GInput> filter) throws NullPointerException {
			if(filter == null) throw new NullPointerException("Filter is null");
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
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
		 */
		public JunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			if(filter1 == null) throw new NullPointerException("Filter1 is null");
			if(filter2 == null) throw new NullPointerException("Filter2 is null");
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
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Inversion ({@code !}-Operator) eines gegebenen
	 * {@link Filter Filters} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn der gegebene
	 * {@link Filter Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann ab, wenn der gegebene {@link Filter
	 * Filter} die Eingabe akzeptiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class InverseFilter<GInput> extends FilterLink<GInput> implements Filter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
		 */
		public InverseFilter(final Filter<? super GInput> filter) throws NullPointerException {
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
			if(!(object instanceof InverseFilter<?>)) return false;
			return super.equals(object);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall("inverseFilter", this.filter);
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
		 *         Converter} {@code null} ist.
		 */
		public ConvertedFilter(final Filter<? super GOutput> filter,
			final Converter<? super GInput, ? extends GOutput> converter) {
			super(converter);
			if(filter == null) throw new NullPointerException("Filter is null");
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
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Disjunktion ({@code ||}-Operator) zweier gegebener
	 * {@link Filter Filter} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn mindestens einer
	 * der gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn die beiden
	 * gegebenen {@link Filter Filter} die Eingabe ablehnen.
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
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Konjunktion ({@code &amp;&amp;}-Operator) zweier
	 * gegebener {@link Filter Filter} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn die
	 * beiden gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn
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
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter Filter}, der die Äquivalenz ({@code ==}-Operator) zweier gegebener
	 * {@link Filter Filter} berechnet. Der {@link Filter Filter} akzeptiert eine Eingabe nur dann, wenn die beiden
	 * gegebenen {@link Filter Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe genau dann ab, wenn
	 * einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und der andere sie ablehnt.
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
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter Filter}, der einen gegebenen {@link Filter Filter} synchronisiert.
	 * Die Synchronisation erfolgt via {@code synchronized(filter)} auf dem gegebenen {@link Filter Filter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class SynchronizedFilter<GInput> extends FilterLink<GInput> implements Filter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter Filter}.
		 * 
		 * @param filter {@link Filter Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
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
			return Objects.toStringCall("nullFilter");
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
	 * Diese Methode gibt den {@link Filter Filter} zurück, der alle Eingaben akzeptiert, die nicht {@code null} sind.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link Filter Filter}, der nur {@code null}-Eingaben ablehnt.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> nullFilter() {
		return (Filter<GInput>)Filters.NULL_FILTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Inversion ({@code !}-Operator) des gegebenen
	 * {@link Filter Filters} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine
	 * Eingabe nur dann, wenn der gegebene {@link Filter Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann
	 * ab, wenn der gegebene {@link Filter Filter} die Eingabe akzeptiert.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter Filter}.
	 * @return {@link InverseFilter Inverse-Filter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
	 */
	public static <GInput> Filter<GInput> inverseFilter(final Filter<? super GInput> filter) throws NullPointerException {
		return new InverseFilter<GInput>(filter);
	}

	/**
	 * Diese Methode gibt einen {@link Filter Filter} zurück, dessen {@link Filter#accept(Object) Akzeptanzmethode} für
	 * jede Eingabe den gegebenen Akzeptanzmodus liefert. Wenn der Akzeptanzmodus {@code true} ist, akzeptiert der
	 * {@link Filter Filter} jede Eingabe und wenn der Akzeptanzmodus {@code false} ist, lehnt der {@link Filter Filter}
	 * jede Eingabe ab.
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
	 *         {@code null} ist.
	 */
	public static <GInput, GOutput> Filter<GInput> convertedFilter(final Filter<? super GOutput> filter,
		final Converter<? super GInput, ? extends GOutput> converter) throws NullPointerException {
		return new ConvertedFilter<GInput, GOutput>(filter, converter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Disjunktion ({@code ||}-Operator) der gegebenen
	 * {@link Filter Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine Eingabe
	 * nur dann, wenn mindestens einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe
	 * genau dann ab, wenn beide gegebenen {@link Filter Filter} die Eingabe ablehnen.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter Filter} 1.
	 * @param filter2 {@link Filter Filter} 2.
	 * @return {@link DisjunctionFilter Disjunction-Filter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
	 */
	public static <GInput> Filter<GInput> disjunctionFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new DisjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Konjunktion ({@code &amp;&amp;}-Operator) der gegebenen
	 * {@link Filter Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine Eingabe
	 * nur dann, wenn alle der gegebenen {@link Filter Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann
	 * ab, wenn mindestens einer der gegebenen {@link Filter Filter} die Eingabe ablehnen.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter Filter} 1.
	 * @param filter2 {@link Filter Filter} 2.
	 * @return {@link ConjunctionFilter Conjunction-Filter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
	 */
	public static <GInput> Filter<GInput> conjunctionFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new ConjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der die Äquivalenz ({@code ==}-Operator) der gegebenen
	 * {@link Filter Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter Filter} akzeptiert eine Eingabe
	 * nur dann, wenn beide gegebenen {@link Filter Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe
	 * genau dann ab, wenn einer der gegebenen {@link Filter Filter} die Eingabe akzeptiert und der andere sie ablehnt.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter Filter} 1.
	 * @param filter2 {@link Filter Filter} 2.
	 * @return {@link EquivalenceFilter Equivalence-Filter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter Filter} {@code null} ist.
	 */
	public static <GInput> Filter<GInput> equivalenceFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new EquivalenceFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter Filter}, der den gegebenen {@link Filter Filter} synchronisiert, und gibt
	 * diesen zurück. Die Synchronisation erfolgt via {@code synchronized(filter)} auf dem gegebenen {@link Filter Filter}
	 * .
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter Filter}.
	 * @return {@link SynchronizedFilter Synchronized-Filter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter Filter} {@code null} ist.
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
