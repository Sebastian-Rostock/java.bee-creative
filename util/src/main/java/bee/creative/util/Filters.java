package bee.creative.util;

import java.util.Collection;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Filter}n.
 * 
 * @see Filter
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public final class Filters {

	/**
	 * Diese Klasse implementiert einen abstrakten delegierenden {@link Filter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GInput2> Typ der Eingabe des gegebenen {@link Filter}s.
	 */
	static abstract class AbstractFilter<GInput, GInput2> implements Filter<GInput> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GInput2> filter;

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} {@code null} ist.
		 */
		public AbstractFilter(final Filter<? super GInput2> filter) throws NullPointerException {
			if(filter == null) throw new NullPointerException("filter is null");
			this.filter = filter;
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
			final AbstractFilter<?, ?> data = (AbstractFilter<?, ?>)object;
			return Objects.equals(this.filter, data.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen delegierenden {@link Filter}, der seine Berechnungen an zwei gegebene
	 * {@link Filter} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static abstract class JunctionFilter<GInput> implements Filter<GInput> {

		/**
		 * Dieses Feld speichert den primären {@link Filter}.
		 */
		final Filter<? super GInput> filter1;

		/**
		 * Dieses Feld speichert den sekundären {@link Filter}.
		 */
		final Filter<? super GInput> filter2;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
		 */
		public JunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			if(filter1 == null) throw new NullPointerException("filter1 is null");
			if(filter2 == null) throw new NullPointerException("filter2 is null");
			this.filter1 = filter1;
			this.filter2 = filter2;
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
			return Objects.equals(this.filter1, data.filter1) && Objects.equals(this.filter2, data.filter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der die Inversion ({@code !}-Operator) eines gegebenen
	 * {@link Filter}s berechnet. Der {@link Filter} akzeptiert eine Eingabe nur dann, wenn der gegebene {@link Filter}
	 * die Eingabe ablehnt und er lehnt eine Eingabe nur dann ab, wenn der gegebene {@link Filter} die Eingabe akzeptiert.
	 * Die Eingabeakzeptanz für eine Eingabe {@code input} sowie einen {@link Filter} {@code filter} ergibt sich aus:
	 * 
	 * <pre>!filter.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class InverseFilter<GInput> extends AbstractFilter<GInput, GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter}, der nur die in einer gegebenen {@link Collection} enthaltenen
	 * Eingaben akzeptiert. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie eine {@link Collection}
	 * {@code collection} ergibt sich aus:
	 * 
	 * <pre>collection.contains(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class ContainsFilter<GInput> implements Filter<GInput> {

		/**
		 * Dieses Feld speichert die {@link Collection}.
		 */
		final Collection<?> collection;

		/**
		 * Dieser Konstrukteur initialisiert die {@link Collection}.
		 * 
		 * @param collection {@link Collection}.
		 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
		 */
		public ContainsFilter(final Collection<?> collection) {
			if(collection == null) throw new NullPointerException("collection is null");
			this.collection = collection;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.collection.contains(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if(object == this) return true;
			if(!(object instanceof ContainsFilter<?>)) return false;
			final ContainsFilter<?> data = (ContainsFilter<?>)object;
			return Objects.equals(this.collection, data.collection);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, dessen konvertierte Eingabe von einem gegebenen {@link Filter}
	 * bewertet wird. Der {@link Filter} konvertiert seine Eingabe mit einem gegebenen {@link Converter} zur Eingabe eines
	 * gegebenen {@link Filter}s. Der {@link Filter} akzeptiert eine Eingabe nur dann, wenn der gegebenen {@link Filter}
	 * die konvertierte Eingabe akzeptiert und er lehnt eine Eingabe ab, wenn der gegebenen {@link Filter} die
	 * konvertierte Eingabe ablehnt. Die Eingabeakzeptanz für eine Eingabe {@code input}, einen {@link Filter}
	 * {@code filter} sowie einen {@link Converter} {@code converter} ergibt sich aus:
	 * 
	 * <pre>filter.accept(converter.convert(input))</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des {@link Filter}s sowie des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Eingabe des gegebenen {@link Filter}s.
	 */
	public static final class ConvertedFilter<GInput, GOutput> extends AbstractFilter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstrukteur initialisiert {@link Filter} und {@link Converter}.
		 * 
		 * @param filter {@link Filter}.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} oder der gegebene {@link Converter} {@code null}
		 *         ist.
		 */
		public ConvertedFilter(final Converter<? super GInput, ? extends GOutput> converter,
			final Filter<? super GOutput> filter) {
			super(filter);
			if(converter == null) throw new NullPointerException("converter is null");
			this.converter = converter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.filter.accept(this.converter.convert(input));
		}

		/**
		 * Diese Methode gibt den {@link Filter} zurück.
		 * 
		 * @return {@link Filter}.
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
			return Objects.equals(this.converter, data.converter) && Objects.equals(this.filter, data.filter);
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
	 * Diese Klasse implementiert einen {@link Filter}, der die Disjunktion ({@code ||}-Operator) zweier gegebener
	 * {@link Filter} berechnet. Der {@link Filter} akzeptiert eine Eingabe nur dann, wenn mindestens einer der gegebenen
	 * {@link Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn die beiden gegebenen
	 * {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie zwei {@link Filter}
	 * {@code filter1} und {@code filter2} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) || filter2.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class DisjunctionFilter<GInput> extends JunctionFilter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter}, der die Konjunktion ({@code &amp;&amp;}-Operator) zweier gegebener
	 * {@link Filter} berechnet. Der {@link Filter} akzeptiert eine Eingabe nur dann, wenn die beiden gegebenen
	 * {@link Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn mindestens einer der gegebenen
	 * {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie zwei {@link Filter}
	 * {@code filter1} und {@code filter2} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) &amp;&amp; filter2.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class ConjunctionFilter<GInput> extends JunctionFilter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter}, der die Äquivalenz ({@code ==}-Operator) zweier gegebener
	 * {@link Filter} berechnet. Der {@link Filter} akzeptiert eine Eingabe nur dann, wenn die beiden gegebenen
	 * {@link Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe genau dann ab, wenn einer der
	 * gegebenen {@link Filter} die Eingabe akzeptiert und der andere sie ablehnt. Die Eingabeakzeptanz für eine Eingabe
	 * {@code input} sowie zwei {@link Filter} {@code filter1} und {@code filter2} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) == filter2.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class EquivalenceFilter<GInput> extends JunctionFilter<GInput> {

		/**
		 * Dieser Konstrukteur initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
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
	 * Diese Klasse implementiert einen {@link Filter}, der einen gegebenen {@link Filter} synchronisiert. Die
	 * Synchronisation erfolgt via {@code synchronized(filter)} auf dem gegebenen {@link Filter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class SynchronizedFilter<GInput> extends AbstractFilter<GInput, GInput> {

		/**
		 * Dieser Konstrukteur initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn der gegebene {@link Filter} {@code null} ist.
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
	 * Dieses Feld speichert den {@link Filter}, der von {@link Filters#nullFilter()} zurück gegeben wird.
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
	 * Dieses Feld speichert einen {@link Filter}, der von {@link Filters#acceptFilter()} zurück gegeben wird.
	 */
	static final Filter<?> ACCEPT_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return true;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("acceptFilter", true);
		}

	};

	/**
	 * Dieses Feld speichert einen {@link Filter}, der von {@link Filters#rejectFilter()} zurück gegeben wird.
	 */
	static final Filter<?> REJECT_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringCall("rejectFilter", false);
		}

	};

	/**
	 * Diese Methode gibt den {@link Filter} zurück, der alle Eingaben akzeptiert, die nicht {@code null} sind. Die
	 * Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>input != null</pre>
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link Filter}, der nur {@code null}-Eingaben ablehnt.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> nullFilter() {
		return (Filter<GInput>)Filters.NULL_FILTER;
	}

	/**
	 * Diese Methode gibt einen {@link Filter} zurück, dessen {@link Filter#accept(Object)}-Methode jede Eingabe
	 * akzeptiert. Die Eingabeakzeptanz ist immer {@code true}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@code accept}-{@link Filter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> acceptFilter() {
		return (Filter<GInput>)Filters.ACCEPT_FILTER;
	}

	/**
	 * Diese Methode gibt einen {@link Filter} zurück, dessen {@link Filter#accept(Object)}-Methode jede Eingabe ablehnt.
	 * Die Eingabeakzeptanz ist immer {@code false}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@code reject}-{@link Filter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> rejectFilter() {
		return (Filter<GInput>)Filters.REJECT_FILTER;
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Inversion ({@code !}-Operator) des gegebenen {@link Filter}s
	 * berechnet, und gibt diesen zurück. Der erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn der gegebene
	 * {@link Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann ab, wenn der gegebene {@link Filter} die
	 * Eingabe akzeptiert. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>!filter.accept(input)</pre>
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@link InverseFilter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter} {@code null} ist.
	 */
	public static <GInput> InverseFilter<GInput> inverseFilter(final Filter<? super GInput> filter)
		throws NullPointerException {
		return new InverseFilter<GInput>(filter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der nur die in der gegebenen {@link Collection} enthaltenen Eingaben
	 * akzeptiert, und gibt diesen zurück. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie eine
	 * {@link Collection} {@code collection} ergibt sich aus:
	 * 
	 * <pre>collection.contains(input)</pre>
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param collection {@link Collection}.
	 * @return {@link ContainsFilter}.
	 * @throws NullPointerException Wenn die gegebene {@link Collection} {@code null} ist.
	 */
	public static <GInput> ContainsFilter<GInput> containsFilter(final Collection<?> collection)
		throws NullPointerException {
		return new ContainsFilter<GInput>(collection);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, dessen konvertierte Eingabe vom gegebenen {@link Filter} bewertet wird,
	 * und gibt diesen zurück. Der erzeugte {@link Filter} konvertiert seine Eingabe mit dem gegebenen {@link Converter}
	 * zur Eingabe des gegebenen {@link Filter}s. Der erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn der
	 * gegebenen {@link Filter} die konvertierte Eingabe akzeptiert und er lehnt eine Eingabe ab, wenn der gegebenen
	 * {@link Filter} die konvertierte Eingabe ablehnt. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich
	 * aus:
	 * 
	 * <pre>filter.accept(converter.convert(input))</pre>
	 * 
	 * @see Converter
	 * @param <GInput> Typ der Eingabe des {@link Filter}s sowie des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Eingabe des gegebenen {@link Filter
	 *        Filters}.
	 * @param converter {@link Converter}.
	 * @param filter {@link Filter}.
	 * @return {@link ConvertedFilter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter} oder der gegebene {@link Converter} {@code null} ist.
	 */
	public static <GInput, GOutput> ConvertedFilter<GInput, GOutput> convertedFilter(
		final Converter<? super GInput, ? extends GOutput> converter, final Filter<? super GOutput> filter)
		throws NullPointerException {
		return new ConvertedFilter<GInput, GOutput>(converter, filter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Disjunktion ({@code ||}-Operator) der gegebenen {@link Filter}
	 * berechnet, und gibt diesen zurück. Der erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn mindestens
	 * einer der gegebenen {@link Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn beide
	 * gegebenen {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) || filter2.accept(input)</pre>
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter} 1.
	 * @param filter2 {@link Filter} 2.
	 * @return {@link DisjunctionFilter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GInput> DisjunctionFilter<GInput> disjunctionFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new DisjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Konjunktion ({@code &amp;&amp;}-Operator) der gegebenen
	 * {@link Filter} berechnet, und gibt diesen zurück. Der erzeugte {@link Filter} akzeptiert eine Eingabe nur dann,
	 * wenn alle der gegebenen {@link Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn
	 * mindestens einer der gegebenen {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe
	 * {@code input} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) &amp;&amp; filter2.accept(input)</pre>
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter} 1.
	 * @param filter2 {@link Filter} 2.
	 * @return {@link ConjunctionFilter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GInput> ConjunctionFilter<GInput> conjunctionFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new ConjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Äquivalenz ({@code ==}-Operator) der gegebenen {@link Filter}
	 * berechnet, und gibt diesen zurück. Der erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn beide
	 * gegebenen {@link Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe genau dann ab, wenn einer
	 * der gegebenen {@link Filter} die Eingabe akzeptiert und der andere sie ablehnt. Die Eingabeakzeptanz für eine
	 * Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) == filter2.accept(input)</pre>
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter} 1.
	 * @param filter2 {@link Filter} 2.
	 * @return {@link EquivalenceFilter}.
	 * @throws NullPointerException Wenn einer der gegebenen {@link Filter} {@code null} ist.
	 */
	public static <GInput> EquivalenceFilter<GInput> equivalenceFilter(final Filter<? super GInput> filter1,
		final Filter<? super GInput> filter2) throws NullPointerException {
		return new EquivalenceFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der den gegebenen {@link Filter} synchronisiert, und gibt diesen
	 * zurück. Die Synchronisation erfolgt via {@code synchronized(filter)} auf dem gegebenen {@link Filter} .
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@link SynchronizedFilter}.
	 * @throws NullPointerException Wenn der gegebene {@link Filter} {@code null} ist.
	 */
	public static <GInput> SynchronizedFilter<GInput> synchronizedFilter(final Filter<? super GInput> filter)
		throws NullPointerException {
		return new SynchronizedFilter<GInput>(filter);
	}

	/**
	 * Dieser Konstrukteur ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Filters() {
	}

}