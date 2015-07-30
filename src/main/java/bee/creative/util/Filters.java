package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Diese Klasse implementiert Hilfsmethoden und Hilfsklassen zur Konstruktion und Verarbeitung von {@link Filter}n.
 * 
 * @see Filter
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public class Filters {

	/**
	 * Diese Klasse implementiert einen abstrakten {@link Filter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static abstract class AbstractFilter<GInput> implements Filter<GInput> {

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
	 * Diese Klasse implementiert einen abstrakten, delegierenden {@link Filter}, der seine Berechnungen an zwei gegebene {@link Filter} delegiert.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	static abstract class AbstractJunctionFilter<GInput> implements Filter<GInput> {

		/**
		 * Dieses Feld speichert den primären {@link Filter}.
		 */
		final Filter<? super GInput> filter1;

		/**
		 * Dieses Feld speichert den sekundären {@link Filter}.
		 */
		final Filter<? super GInput> filter2;

		/**
		 * Dieser Konstruktor initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public AbstractJunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			if ((filter1 == null) || (filter2 == null)) throw new NullPointerException();
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
			if (object == this) return true;
			if (!(object instanceof AbstractJunctionFilter<?>)) return false;
			final AbstractJunctionFilter<?> data = (AbstractJunctionFilter<?>)object;
			return Objects.equals(this.filter1, data.filter1) && Objects.equals(this.filter2, data.filter2);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.filter1, this.filter2);
		}

	}

	/**
	 * Diese Klasse implementiert einen abstrakten, delegierenden {@link Filter}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 * @param <GInput2> Typ der Eingabe des gegebenen {@link Filter}s.
	 */
	static abstract class AbstractDelegatingFilter<GInput, GInput2> implements Filter<GInput> {

		/**
		 * Dieses Feld speichert den {@link Filter}.
		 */
		final Filter<? super GInput2> filter;

		/**
		 * Dieser Konstruktor initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public AbstractDelegatingFilter(final Filter<? super GInput2> filter) throws NullPointerException {
			if (filter == null) throw new NullPointerException();
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
			if (object == this) return true;
			if (!(object instanceof AbstractDelegatingFilter<?, ?>)) return false;
			final AbstractDelegatingFilter<?, ?> data = (AbstractDelegatingFilter<?, ?>)object;
			return Objects.equals(this.filter, data.filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Filter}, der die {@code null}-Eingabe ablehnat und alle anderen Eingaben akzeptiert. Die Eingabeakzeptanz für eine
	 * Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>input != null</pre>
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class NullFilter extends AbstractFilter<Object> {

		/**
		 * Dieses Feld speichert den {@link NullFilter}.
		 */
		public static final NullFilter INSTANCE = new NullFilter();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final Object input) {
			return input != null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof NullFilter);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Filter}, der nur die Eingaben akzeptiert, die Instanzen einer gegebenen Klasse oder ihrer Nachfahren sind.
	 * 
	 * @see Class#isInstance(Object)
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class ClassFilter<GInput> extends AbstractFilter<GInput> {

		/**
		 * Dieses Feld speichert die Klasse.
		 */
		final Class<?> clazz;

		/**
		 * Dieser Konstruktor initialisiert die Klasse.
		 * 
		 * @param clazz Klasse.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ClassFilter(final Class<?> clazz) throws NullPointerException {
			if (clazz == null) throw new NullPointerException();
			this.clazz = clazz;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			return this.clazz.isInstance(input);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.clazz);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if (object == this) return true;
			if (!(object instanceof ClassFilter<?>)) return false;
			final ClassFilter<?> data = (ClassFilter<?>)object;
			return Objects.equals(this.clazz, data.clazz);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Filter}, der jede Eingabe akzeptiert. Die Eingabeakzeptanz ist immer {@code true}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class AcceptFilter extends AbstractFilter<Object> {

		/**
		 * Dieses Feld speichert den {@link AcceptFilter}.
		 */
		public static final AcceptFilter INSTANCE = new AcceptFilter();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final Object input) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof AcceptFilter);
		}

	}

	/**
	 * Diese Klasse implementiert den {@link Filter}, der jede Eingabe ablehnt. Die Eingabeakzeptanz ist immer {@code false}.
	 * 
	 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 */
	public static final class RejectFilter extends AbstractFilter<Object> {

		/**
		 * Dieses Feld speichert den {@link RejectFilter}.
		 */
		public static final RejectFilter INSTANCE = new RejectFilter();

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final Object input) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			return (object == this) || (object instanceof RejectFilter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der die Negation ({@code !}-Operator) eines gegebenen {@link Filter}s berechnet. Der {@link Filter}
	 * akzeptiert eine Eingabe nur dann, wenn der gegebene {@link Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann ab, wenn der gegebene
	 * {@link Filter} die Eingabe akzeptiert. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie einen {@link Filter} {@code filter} ergibt sich aus:
	 * 
	 * <pre>!filter.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class NegationFilter<GInput> extends AbstractDelegatingFilter<GInput, GInput> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public NegationFilter(final Filter<? super GInput> filter) throws NullPointerException {
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
			if (object == this) return true;
			if (!(object instanceof NegationFilter<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der nur die in einer gegebenen {@link Collection} enthaltenen Eingaben akzeptiert. Die Eingabeakzeptanz
	 * für eine Eingabe {@code input} sowie eine {@link Collection} {@code collection} ergibt sich aus:
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
		 * Dieser Konstruktor initialisiert die {@link Collection}.
		 * 
		 * @param collection {@link Collection}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public ContainsFilter(final Collection<?> collection) {
			if (collection == null) throw new NullPointerException();
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
			if (object == this) return true;
			if (!(object instanceof ContainsFilter<?>)) return false;
			final ContainsFilter<?> data = (ContainsFilter<?>)object;
			return Objects.equals(this.collection, data.collection);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.collection);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, dessen konvertierte Eingabe von einem gegebenen {@link Filter} bewertet wird. Der {@link Filter}
	 * konvertiert seine Eingabe mit einem gegebenen {@link Converter} zur Eingabe eines gegebenen {@link Filter}s. Der {@link Filter} akzeptiert eine Eingabe nur
	 * dann, wenn der gegebenen {@link Filter} die konvertierte Eingabe akzeptiert und er lehnt eine Eingabe ab, wenn der gegebenen {@link Filter} die
	 * konvertierte Eingabe ablehnt. Die Eingabeakzeptanz für eine Eingabe {@code input}, einen {@link Filter} {@code filter} sowie einen {@link Converter}
	 * {@code converter} ergibt sich aus:
	 * 
	 * <pre>filter.accept(converter.convert(input))</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe des {@link Filter}s sowie des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Eingabe des gegebenen {@link Filter}s.
	 */
	public static final class ConvertedFilter<GInput, GOutput> extends AbstractDelegatingFilter<GInput, GOutput> {

		/**
		 * Dieses Feld speichert den {@link Converter}.
		 */
		final Converter<? super GInput, ? extends GOutput> converter;

		/**
		 * Dieser Konstruktor initialisiert {@link Filter} und {@link Converter}.
		 * 
		 * @param filter {@link Filter}.
		 * @param converter {@link Converter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
		 */
		public ConvertedFilter(final Converter<? super GInput, ? extends GOutput> converter, final Filter<? super GOutput> filter) {
			super(filter);
			if (filter == null) throw new NullPointerException();
			if (converter == null) throw new NullPointerException();
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
			if (object == this) return true;
			if (!(object instanceof ConvertedFilter<?, ?>)) return false;
			final ConvertedFilter<?, ?> data = (ConvertedFilter<?, ?>)object;
			return Objects.equals(this.converter, data.converter) && Objects.equals(this.filter, data.filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return Objects.toStringCall(this, this.converter, this.filter);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der die Disjunktion ({@code ||}-Operator) zweier gegebener {@link Filter} berechnet. Der {@link Filter}
	 * akzeptiert eine Eingabe nur dann, wenn mindestens einer der gegebenen {@link Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab, wenn
	 * die beiden gegebenen {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie zwei {@link Filter} {@code filter1} und
	 * {@code filter2} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) || filter2.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class DisjunctionFilter<GInput> extends AbstractJunctionFilter<GInput> {

		/**
		 * Dieser Konstruktor initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
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
			if (object == this) return true;
			if (!(object instanceof DisjunctionFilter<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der die Konjunktion ({@code &amp;&amp;}-Operator) zweier gegebener {@link Filter} berechnet. Der
	 * {@link Filter} akzeptiert eine Eingabe nur dann, wenn die beiden gegebenen {@link Filter} die Eingabe akzeptiert und er lehnt die Eingabe genau dann ab,
	 * wenn mindestens einer der gegebenen {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie zwei {@link Filter}
	 * {@code filter1} und {@code filter2} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) &amp;&amp; filter2.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class ConjunctionFilter<GInput> extends AbstractJunctionFilter<GInput> {

		/**
		 * Dieser Konstruktor initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
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
			if (object == this) return true;
			if (!(object instanceof ConjunctionFilter<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der die Äquivalenz ({@code ==}-Operator) zweier gegebener {@link Filter} berechnet. Der {@link Filter}
	 * akzeptiert eine Eingabe nur dann, wenn die beiden gegebenen {@link Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die Eingabe genau dann ab,
	 * wenn einer der gegebenen {@link Filter} die Eingabe akzeptiert und der andere sie ablehnt. Die Eingabeakzeptanz für eine Eingabe {@code input} sowie zwei
	 * {@link Filter} {@code filter1} und {@code filter2} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) == filter2.accept(input)</pre>
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class EquivalenceFilter<GInput> extends AbstractJunctionFilter<GInput> {

		/**
		 * Dieser Konstruktor initialisiert die {@link Filter}.
		 * 
		 * @param filter1 primärer {@link Filter}.
		 * @param filter2 sekundärer {@link Filter}.
		 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
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
			if (object == this) return true;
			if (!(object instanceof EquivalenceFilter<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Klasse implementiert einen {@link Filter}, der einen gegebenen {@link Filter} synchronisiert. Die Synchronisation erfolgt via
	 * {@code synchronized(this)}.
	 * 
	 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
	 * @param <GInput> Typ der Eingabe.
	 */
	public static final class SynchronizedFilter<GInput> extends AbstractDelegatingFilter<GInput, GInput> {

		/**
		 * Dieser Konstruktor initialisiert den {@link Filter}.
		 * 
		 * @param filter {@link Filter}.
		 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
		 */
		public SynchronizedFilter(final Filter<? super GInput> filter) {
			super(filter);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final GInput input) {
			synchronized (this) {
				return this.filter.accept(input);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(final Object object) {
			if ((object == this) || Objects.equals(object, this.filter)) return true;
			if (!(object instanceof SynchronizedFilter<?>)) return false;
			return super.equals(object);
		}

	}

	/**
	 * Diese Methode gibt den {@link Filter} zurück, der alle Eingaben akzeptiert, die nicht {@code null} sind. Die Eingabeakzeptanz für eine Eingabe
	 * {@code input} ergibt sich aus:
	 * 
	 * <pre>input != null</pre>
	 * 
	 * @see NullFilter#INSTANCE
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link NullFilter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> nullFilter() {
		return (Filter<GInput>)NullFilter.INSTANCE;
	}

	public static <GItem> Filter<GItem> comparableFilterEQ(final Comparable<? super GItem> comparable) {
		return new Filter<GItem>() {

			@Override
			public boolean accept(final GItem input) {
				return comparable.compareTo(input) == 0;
			}

		};
	}

	public static <GItem> Filter<GItem> comparableFilterNE(final Comparable<? super GItem> comparable) {
		return negationFilter(comparableFilterEQ(comparable));
	}

	public static <GItem> Filter<GItem> comparableFilterGT(final Comparable<? super GItem> comparable) {
		return new Filter<GItem>() {

			@Override
			public boolean accept(final GItem input) {
				return comparable.compareTo(input) <= 0;
			}

		};
	}

	public static <GItem> Filter<GItem> comparableFilterGE(final Comparable<? super GItem> comparable) {
		return negationFilter(comparableFilterLT(comparable));
	}

	public static <GItem> Filter<GItem> comparableFilterLT(final Comparable<? super GItem> comparable) {
		return new Filter<GItem>() {

			@Override
			public boolean accept(final GItem input) {
				return comparable.compareTo(input) >= 0;
			}

		};
	}

	public static <GItem> Filter<GItem> comparableFilterLE(final Comparable<? super GItem> comparable) {
		return negationFilter(comparableFilterGT(comparable));
	}

	/**
	 * Diese Methode gibt einen {@link Filter} zurück, dessen {@link Filter#accept(Object)}-Methode der nur die Eingaben akzeptiert, die Instanzen der gegebenen
	 * {@link Class} oder ihrer Nachfahren sind.
	 * 
	 * @see ClassFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param clazz {@link Class}.
	 * @return {@link ClassFilter}.
	 */
	public static <GInput> ClassFilter<GInput> classFilter(final Class<?> clazz) {
		return new ClassFilter<GInput>(clazz);
	}

	/**
	 * Diese Methode gibt einen {@link Filter} zurück, dessen {@link Filter#accept(Object)}-Methode jede Eingabe akzeptiert. Die Eingabeakzeptanz ist immer
	 * {@code true}.
	 * 
	 * @see AcceptFilter#INSTANCE
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link AcceptFilter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> acceptFilter() {
		return (Filter<GInput>)AcceptFilter.INSTANCE;
	}

	/**
	 * Diese Methode gibt einen {@link Filter} zurück, dessen {@link Filter#accept(Object)}-Methode jede Eingabe ablehnt. Die Eingabeakzeptanz ist immer
	 * {@code false}.
	 * 
	 * @see RejectFilter#INSTANCE
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link RejectFilter}.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> rejectFilter() {
		return (Filter<GInput>)RejectFilter.INSTANCE;
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Negation ({@code !}-Operator) des gegebenen {@link Filter}s berechnet, und gibt diesen zurück. Der
	 * erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn der gegebene {@link Filter} die Eingabe ablehnt und er lehnt eine Eingabe nur dann ab, wenn
	 * der gegebene {@link Filter} die Eingabe akzeptiert. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>!filter.accept(input)</pre>
	 * 
	 * @see NegationFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@link NegationFilter}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> negationFilter(final Filter<? super GInput> filter) throws NullPointerException {
		if (filter == null) throw new NullPointerException();
		if (filter == AcceptFilter.INSTANCE) return Filters.rejectFilter();
		if (filter == RejectFilter.INSTANCE) return Filters.acceptFilter();
		if (filter instanceof NegationFilter<?>) return (Filter<GInput>)((NegationFilter<?>)filter).filter;
		return new NegationFilter<GInput>(filter);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der nur die gegebenen Eingaben akzeptiert, und gibt diesen zurück.
	 * 
	 * @see #containsFilter(Collection)
	 * @param <GInput> Typ der Eingabe.
	 * @param inputs Eingaben.
	 * @return {@link Filter}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static <GInput> Filter<GInput> containsFilter(final Object... inputs) throws NullPointerException {
		if (inputs.length == 0) return Filters.<GInput>rejectFilter();
		if (inputs.length == 1) return Filters.containsFilter(Collections.singleton(inputs[0]));
		return Filters.containsFilter(new HashSet<Object>(Arrays.asList(inputs)));
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der nur die in der gegebenen {@link Collection} enthaltenen Eingaben akzeptiert, und gibt diesen zurück. Die
	 * Eingabeakzeptanz für eine Eingabe {@code input} sowie eine {@link Collection} {@code collection} ergibt sich aus:
	 * 
	 * <pre>collection.contains(input)</pre>
	 * 
	 * @see ContainsFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param collection {@link Collection}.
	 * @return {@link ContainsFilter}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static <GInput> ContainsFilter<GInput> containsFilter(final Collection<?> collection) throws NullPointerException {
		return new ContainsFilter<GInput>(collection);
	}

	public static <GInput, GOutput> Filter<GInput> fieldFilter(final Field<? super GInput, ? extends GOutput> field, final Filter<? super GOutput> filter)
		throws NullPointerException {
		if (filter == null) throw new NullPointerException();
		if (field == null) throw new NullPointerException();
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter.accept(field.get(input));
			}

		};
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, dessen konvertierte Eingabe vom gegebenen {@link Filter} bewertet wird, und gibt diesen zurück. Der erzeugte
	 * {@link Filter} konvertiert seine Eingabe mit dem gegebenen {@link Converter} zur Eingabe des gegebenen {@link Filter}s. Der erzeugte {@link Filter}
	 * akzeptiert eine Eingabe nur dann, wenn der gegebenen {@link Filter} die konvertierte Eingabe akzeptiert und er lehnt eine Eingabe ab, wenn der gegebenen
	 * {@link Filter} die konvertierte Eingabe ablehnt. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>filter.accept(converter.convert(input))</pre>
	 * 
	 * @see Converter
	 * @see ConvertedFilter
	 * @param <GInput> Typ der Eingabe des {@link Filter}s sowie des gegebenen {@link Converter}s.
	 * @param <GOutput> Typ der Ausgabe des gegebenen {@link Converter}s sowie der Eingabe des gegebenen {@link Filter Filters}.
	 * @param converter {@link Converter}.
	 * @param filter {@link Filter}.
	 * @return {@link ConvertedFilter}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput, GOutput> Filter<GInput> convertedFilter(final Converter<? super GInput, ? extends GOutput> converter,
		final Filter<? super GOutput> filter) throws NullPointerException {
		if (filter == null) throw new NullPointerException();
		if (converter == null) throw new NullPointerException();
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter.accept(converter.convert(input));
			}

		};
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Disjunktion ({@code ||}-Operator) der gegebenen {@link Filter} berechnet, und gibt diesen zurück. Der
	 * erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn mindestens einer der gegebenen {@link Filter} die Eingabe akzeptiert und er lehnt die
	 * Eingabe genau dann ab, wenn beide gegebenen {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) || filter2.accept(input)</pre>
	 * 
	 * @see DisjunctionFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter} 1.
	 * @param filter2 {@link Filter} 2.
	 * @return {@link DisjunctionFilter}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput> Filter<GInput> disjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2)
		throws NullPointerException {
		if ((filter1 instanceof NegationFilter<?>) && (filter2 instanceof NegationFilter<?>))
			return Filters.negationFilter(Filters.conjunctionFilter(Filters.negationFilter(filter1), Filters.negationFilter(filter2)));
		return new DisjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Konjunktion ({@code &amp;&amp;}-Operator) der gegebenen {@link Filter} berechnet, und gibt diesen
	 * zurück. Der erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn alle der gegebenen {@link Filter} die Eingabe akzeptiert und er lehnt die
	 * Eingabe genau dann ab, wenn mindestens einer der gegebenen {@link Filter} die Eingabe ablehnen. Die Eingabeakzeptanz für eine Eingabe {@code input} ergibt
	 * sich aus:
	 * 
	 * <pre>filter1.accept(input) &amp;&amp; filter2.accept(input)</pre>
	 * 
	 * @see ConjunctionFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter} 1.
	 * @param filter2 {@link Filter} 2.
	 * @return {@link ConjunctionFilter}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput> Filter<GInput> conjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2)
		throws NullPointerException {
		if ((filter1 instanceof NegationFilter<?>) && (filter2 instanceof NegationFilter<?>))
			return Filters.negationFilter(Filters.disjunctionFilter(Filters.negationFilter(filter1), Filters.negationFilter(filter2)));
		return new ConjunctionFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der die Äquivalenz ({@code ==}-Operator) der gegebenen {@link Filter} berechnet, und gibt diesen zurück. Der
	 * erzeugte {@link Filter} akzeptiert eine Eingabe nur dann, wenn beide gegebenen {@link Filter} die Eingabe akzeptieren bzw. ablehnen und er lehnt die
	 * Eingabe genau dann ab, wenn einer der gegebenen {@link Filter} die Eingabe akzeptiert und der andere sie ablehnt. Die Eingabeakzeptanz für eine Eingabe
	 * {@code input} ergibt sich aus:
	 * 
	 * <pre>filter1.accept(input) == filter2.accept(input)</pre>
	 * 
	 * @see EquivalenceFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 {@link Filter} 1.
	 * @param filter2 {@link Filter} 2.
	 * @return {@link EquivalenceFilter}.
	 * @throws NullPointerException Wenn eine der Eingaben {@code null} ist.
	 */
	public static <GInput> EquivalenceFilter<GInput> equivalenceFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2)
		throws NullPointerException {
		return new EquivalenceFilter<GInput>(filter1, filter2);
	}

	/**
	 * Diese Methode erzeugt einen {@link Filter}, der den gegebenen {@link Filter} synchronisiert, und gibt diesen zurück.
	 * 
	 * @see SynchronizedFilter
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@link SynchronizedFilter}.
	 * @throws NullPointerException Wenn die Eingabe {@code null} ist.
	 */
	public static <GInput> SynchronizedFilter<GInput> synchronizedFilter(final Filter<? super GInput> filter) throws NullPointerException {
		return new SynchronizedFilter<GInput>(filter);
	}

	/**
	 * Dieser Konstruktor ist versteckt und verhindert damit die Erzeugung von Instanzen der Klasse.
	 */
	Filters() {
	}

}
