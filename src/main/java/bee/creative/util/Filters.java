package bee.creative.util;

import java.util.Collections;
import java.util.Map;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;
import bee.creative.ref.SoftPointer;
import bee.creative.util.Objects.BaseObject;

/** Diese Klasse implementiert grundlegende {@link Filter}.
 *
 * @see Filter
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Filters {

	public static class NullFilter extends BaseObject implements Filter<Object> {

		public static final Filter<?> INSTANCE = new NullFilter();

		@Override
		public boolean accept(final Object input) {
			return input != null;
		}

	}

	public static class ValueFilter implements Filter<Object> {

		public static final Filter<?> TRUE = new ValueFilter(true);

		public static final Filter<?> FALSE = new ValueFilter(false);

		public final boolean value;

		public ValueFilter(final boolean value) {
			this.value = value;
		}

		@Override
		public boolean accept(final Object input) {
			return this.value;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.value);
		}

	}

	public static class ClassFilter<GInput> implements Filter<GInput> {

		public final Class<?> inputClass;

		public ClassFilter(final Class<?> inputClass) {
			this.inputClass = Objects.assertNotNull(inputClass);
		}

		@Override
		public boolean accept(final GInput input) {
			return this.inputClass.isInstance(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.inputClass);
		}

	}

	public static class NegationFilter<GInput> implements Filter<GInput> {

		public final Filter<? super GInput> filter;

		public NegationFilter(final Filter<? super GInput> filter) {
			this.filter = Objects.assertNotNull(filter);
		}

		@Override
		public boolean accept(final GInput input) {
			return !this.filter.accept(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter);
		}

	}

	public static class NavigatedFilter<GInput, GOutput> implements Filter<GInput> {

		public final Getter<? super GInput, ? extends GOutput> navigator;

		public final Filter<? super GOutput> filter;

		public NavigatedFilter(final Getter<? super GInput, ? extends GOutput> navigator, final Filter<? super GOutput> filter) {
			this.navigator = Objects.assertNotNull(navigator);
			this.filter = Objects.assertNotNull(filter);
		}

		@Override
		public boolean accept(final GInput input) {
			return this.filter.accept(this.navigator.get(input));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.navigator, this.filter);
		}

	}

	public static class DisjunctionFilter<GInput> implements Filter<GInput> {

		public final Filter<? super GInput> filter2;

		public final Filter<? super GInput> filter1;

		public DisjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			this.filter1 = Objects.assertNotNull(filter1);
			this.filter2 = Objects.assertNotNull(filter2);
		}

		@Override
		public boolean accept(final GInput input) {
			return this.filter1.accept(input) || this.filter2.accept(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter1, this.filter2);
		}

	}

	public static class ConjunctionFilter<GInput> implements Filter<GInput> {

		public final Filter<? super GInput> filter1;

		public final Filter<? super GInput> filter2;

		public ConjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			this.filter1 = Objects.assertNotNull(filter1);
			this.filter2 = Objects.assertNotNull(filter2);
		}

		@Override
		public boolean accept(final GInput input) {
			return this.filter1.accept(input) && this.filter2.accept(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter1, this.filter2);
		}

	}

	public static class EquivalenceFilter<GInput> implements Filter<GInput> {

		public final Filter<? super GInput> filter1;

		public final Filter<? super GInput> filter2;

		public EquivalenceFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2) {
			this.filter1 = Objects.assertNotNull(filter1);
			this.filter2 = Objects.assertNotNull(filter2);
		}

		@Override
		public boolean accept(final GInput input) {
			return this.filter1.accept(input) == this.filter2.accept(input);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter1, this.filter2);
		}

	}

	public static class SynchronizedFilter<GInput> implements Filter<GInput> {

		public final Object mutex;

		public final Filter<? super GInput> filter;

		public SynchronizedFilter(final Object mutex, final Filter<? super GInput> filter) {
			this.mutex = mutex != null ? mutex : this;
			this.filter = Objects.assertNotNull(filter);
		}

		@Override
		public boolean accept(final GInput input) {
			synchronized (this.mutex) {
				return this.filter.accept(input);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter);
		}

	}

	static class FilterGetter<GInput> implements Getter<GInput, Boolean> {

		public final Filter<? super GInput> filter;

		public FilterGetter(final Filter<? super GInput> filter) {
			this.filter = filter;
		}

		@Override
		public Boolean get(final GInput input) {
			return Boolean.valueOf(this.filter.accept(input));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter);
		}

	}

	/** Diese Methode gibt den {@link Filter} zurück, der alle Eingaben akzeptiert, die nicht {@code null} sind.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code input != null}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link #NULL_FILTER}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> nullFilter() {
		return (Filter<GInput>)NullFilter.INSTANCE;
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der nur die Eingaben akzeptiert, die Instanzen der gegebenen {@link Class} oder ihrer Nachfahren sind.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code inputType.isInstance(input)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param inputClass {@link Class} der akzeptierten Eingaben.
	 * @return {@code type}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code inputType} {@code null} ist. */
	public static <GInput> Filter<GInput> classFilter(final Class<?> inputClass) throws NullPointerException {
		return new ClassFilter<>(inputClass);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der stets die gegebene Akzeptanz liefert.
	 * 
	 * @param <GInput> Typ der ignorierten Eingabe.
	 * @param value Akzeptanz.
	 * @return {@code value}-{@link Filter}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> valueFilter(final boolean value) {
		return (Filter<GInput>)(value ? ValueFilter.TRUE : ValueFilter.FALSE);
	}

	/** Diese Methode gibt einen gepufferten {@link Filter} zurück, der die zu seinen Eingaben über den gegebenen {@link Filter} ermittelten Akzeptanzen intern in
	 * einer {@link Map} zur Wiederverwendung vorhält. Die Schlüssel der {@link Map} werden dabei als {@link SoftPointer} auf Eingaben bestückt.
	 *
	 * @see #bufferedFilter(Filter)
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@code buffered}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GInput> Filter<GInput> bufferedFilter(final Filter<? super GInput> filter) throws NullPointerException {
		return Filters.bufferedFilter(Integer.MAX_VALUE, Pointers.SOFT, filter);
	}

	/** Diese Methode gibt einen gepufferten {@link Filter} zurück, der die zu seinen Eingaben über den gegebenen {@link Filter} ermittelten Akzeptanzen intern in
	 * einer {@link Map} zur Wiederverwendung vorhält. Die Schlüssel der {@link Map} werden dabei als {@link Pointer} auf Eingaben bestückt.
	 *
	 * @see Getters#toFilter(Getter)
	 * @see Filters#toGetter(Filter)
	 * @see Getters#bufferedGetter(int, int, int, Getter)
	 * @param <GInput> Typ der Eingabe.
	 * @param limit Maximum für die Anzahl der Einträge in der internen {@link Map}.
	 * @param mode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden ({@link Pointers#HARD},
	 *        {@link Pointers#SOFT}, {@link Pointers#WEAK}).
	 * @param filter {@link Filter}.
	 * @return {@code buffered}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Getters#bufferedGetter(int, int, int, Getter)} eine entsprechende Ausnahme auslöst. */
	public static <GInput> Filter<GInput> bufferedFilter(final int limit, final int mode, final Filter<? super GInput> filter)
		throws NullPointerException, IllegalArgumentException {
		return Getters.toFilter(Getters.bufferedGetter(limit, mode, Pointers.HARD, Filters.toGetter(filter)));
	}

	/** Diese Methode gibt einen navigierenden {@link Filter} zurück, welcher von seiner Eingabe mit dem gegebenen {@link Getter} zur Eingabe des gegebenen
	 * {@link Filter} navigiert.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code filter.accept(navigator.get(input))}.
	 *
	 * @param <GInput> Typ der Eingabe des gelieferten {@link Filter} sowie der Eingabe des {@link Field}.
	 * @param <GOutput> Typ der Eingabe des gegebenen {@link Filter} sowie der Ausgabe des {@link Getter}.
	 * @param navigator {@link Getter} zur Navigation.
	 * @param filter {@link Field}.
	 * @return {@code navigated}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code navigator} bzw. {@code filter} {@code null} ist. */
	public static <GInput, GOutput> Filter<GInput> navigatedFilter(final Getter<? super GInput, ? extends GOutput> navigator,
		final Filter<? super GOutput> filter) throws NullPointerException {
		return new NavigatedFilter<>(navigator, filter);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von dem gegebenen Filter abgelehnt werden.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code !filter.accept(input)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@code negation}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GInput> Filter<GInput> negationFilter(final Filter<? super GInput> filter) throws NullPointerException {
		return new NegationFilter<>(filter);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von einem der gegebenen {@link Filter} akzeptiert werden <br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code filter1.accept(input) || filter2.accept(input)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 erster {@link Filter}.
	 * @param filter2 zweiter {@link Filter}.
	 * @return {@code disjunction}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter1} bzw. {@code filter2} {@code null} ist. */
	public static <GInput> Filter<GInput> disjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2)
		throws NullPointerException {
		return new DisjunctionFilter<>(filter1, filter2);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von beiden der gegebenen {@link Filter} akzeptiert werden <br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code filter1.accept(input) && filter2.accept(input)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 erster {@link Filter}.
	 * @param filter2 zweiter {@link Filter}.
	 * @return {@code conjunction}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter1} bzw. {@code filter2} {@code null} ist. */
	public static <GInput> Filter<GInput> conjunctionFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2)
		throws NullPointerException {
		return new ConjunctionFilter<>(filter1, filter2);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von beiden der gegebenen {@link Filter} akzeptiert bzw. abgelehnt
	 * werden <br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code filter1.accept(input) == filter2.accept(input)}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param filter1 erster {@link Filter}.
	 * @param filter2 zweiter {@link Filter}.
	 * @return {@code equivalence}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter1} bzw. {@code filter2} {@code null} ist. */
	public static <GInput> Filter<GInput> equivalenceFilter(final Filter<? super GInput> filter1, final Filter<? super GInput> filter2)
		throws NullPointerException {
		return new EquivalenceFilter<>(filter1, filter2);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der den gegebenen {@link Filter} über {@code synchronized(filter)} synchronisiert.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@code synchronized}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GInput> Filter<GInput> synchronizedFilter(final Object mutex, final Filter<? super GInput> filter) throws NullPointerException {
		return new SynchronizedFilter<>(mutex, filter);
	}

	/** Diese Methode gibt einen {@link Getter} als Adapter zu einem {@link Filter} zurück.<br>
	 * Für eine Eingabe {@code input} liefert er die Ausgabe {@code Boolean.valueOf(filter.accept(input))}.
	 *
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@link Filter}-Adapter.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GInput> Getter<GInput, Boolean> toGetter(final Filter<? super GInput> filter) throws NullPointerException {
		return new FilterGetter<>(filter);
	}

}
