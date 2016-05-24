package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import bee.creative.util.Pointers.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Filter}.
 * 
 * @see Filter
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Filters {

	/** Dieses Feld speichert den {@link Filter}, der die {@code null}-Eingabe ablehnt und alle anderen Eingaben akzeptiert. */
	public static final Filter<?> NULL_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return input != null;
		}

		@Override
		public String toString() {
			return "NULL_FILTER";
		}

	};

	/** Dieses Feld speichert den {@link Filter}, der jede Eingabe ablehnt. */
	public static final Filter<Object> REJECT_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return false;
		}

		@Override
		public String toString() {
			return "REJECT_FILTER";
		}

	};

	/** Dieses Feld speichert den {@link Filter}, der jede Eingabe akzeptiert. */
	public static final Filter<Object> ACCEPT_FILTER = new Filter<Object>() {

		@Override
		public boolean accept(final Object input) {
			return true;
		}

		@Override
		public String toString() {
			return "ACCEPT_FILTER";
		}

	};

	{}

	/** Diese Methode gibt einen {@link Filter} als Adapter zu einem {@link Converter} zurück.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code Boolean.TRUE.equals(converter.convert(input))}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param converter {@link Converter}.
	 * @return {@link Converter}-Adapter.
	 * @throws NullPointerException Wenn {@code converter} {@code null} ist. */
	public static <GInput> Filter<GInput> converterAdapter(final Converter<? super GInput, Boolean> converter) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				final Boolean result = converter.convert(input);
				return (result != null) && result.booleanValue();
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("converterAdapter", converter);
			}

		};
	}

	/** Diese Methode gibt den {@link Filter} zurück, der alle Eingaben akzeptiert, die nicht {@code null} sind.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code input != null}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link #NULL_FILTER}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> nullFilter() {
		return (Filter<GInput>)Filters.NULL_FILTER;
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der nur die Eingaben akzeptiert, die Instanzen der gegebenen {@link Class} oder ihrer Nachfahren sind.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code inputType.isInstance(input)}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param inputType {@link Class} der akzeptierten Eingaben.
	 * @return {@code type}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code inputType} {@code null} ist. */
	public static <GInput> Filter<GInput> typeFilter(final Class<?> inputType) throws NullPointerException {
		if (inputType == null) throw new NullPointerException("inputType = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return inputType.isInstance(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("typeFilter", inputType);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der jede Eingabe ablehnt.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code false}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link #REJECT_FILTER}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> rejectFilter() {
		return (Filter<GInput>)Filters.REJECT_FILTER;
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der jede Eingabe akzeptiert.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code true}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @return {@link #ACCEPT_FILTER}. */
	@SuppressWarnings ("unchecked")
	public static <GInput> Filter<GInput> acceptFilter() {
		return (Filter<GInput>)Filters.ACCEPT_FILTER;
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
	 * @see #converterAdapter(Converter)
	 * @see Converters#filterAdapter(Filter)
	 * @see Converters#bufferedConverter(int, int, int, Converter)
	 * @param <GInput> Typ der Eingabe.
	 * @param limit Maximum für die Anzahl der Einträge in der internen {@link Map}.
	 * @param mode Modus, in dem die {@link Pointer} auf die Eingabe-Datensätze für die Schlüssel der {@link Map} erzeugt werden ({@link Pointers#HARD},
	 *        {@link Pointers#SOFT}, {@link Pointers#WEAK}).
	 * @param filter {@link Filter}.
	 * @return {@code buffered}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Converters#bufferedConverter(int, int, int, Converter)} eine entsprechende Ausnahme auslöst. */
	public static <GInput> Filter<GInput> bufferedFilter(final int limit, final int mode, final Filter<? super GInput> filter) throws NullPointerException,
		IllegalArgumentException {
		if (filter == null) throw new NullPointerException("filter = null");
		return Filters.converterAdapter(Converters.bufferedConverter(limit, mode, Pointers.HARD, Converters.filterAdapter(filter)));
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die gegebenen Eingaben akzeptiert.
	 * 
	 * @see #containsFilter(Collection)
	 * @param <GInput> Typ der Eingabe.
	 * @param items akzeptierte Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static <GInput> Filter<GInput> containsFilter(final Object... items) throws NullPointerException {
		if (items == null) throw new NullPointerException("items = null");
		if (items.length == 0) return Filters.rejectFilter();
		if (items.length == 1) return Filters.containsFilter(Collections.singleton(items[0]));
		return Filters.containsFilter(new HashSet<>(Arrays.asList(items)));
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die in der gegebenen {@link Collection} enthalten sind.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code collection.contains(input)}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param collection {@link Collection} der akzeptierten Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code collection} {@code null} ist. */
	public static <GInput> Filter<GInput> containsFilter(final Collection<?> collection) throws NullPointerException {
		if (collection == null) throw new NullPointerException("collection = null");
		if (collection.isEmpty()) return Filters.rejectFilter();
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return collection.contains(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("containsFilter", collection);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, deren Ordnung gleich der des gegebenen {@link Comparable} ist.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code comparable.compareTo(input) == 0}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code equal}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> equalFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException("comparable = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return comparable.compareTo(input) == 0;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("equalFilter", comparable);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, deren Ordnung kleiner der des gegebenen {@link Comparable} ist.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code comparable.compareTo(input) >= 0}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code lower}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> lowerFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException("comparable = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return comparable.compareTo(input) >= 0;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("lowerFilter", comparable);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, deren Ordnung größer der des gegebenen {@link Comparable} ist.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code comparable.compareTo(input) <= 0}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code higher}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> higherFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		if (comparable == null) throw new NullPointerException("comparable = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return comparable.compareTo(input) <= 0;
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("higherFilter", comparable);
			}

		};
	}

	/** Diese Methode gibt einen navigierenden {@link Filter} zurück, welcher von seiner Eingabe mit dem gegebenen {@link Field} zur Eingabe des gegebenen
	 * {@link Filter} navigiert.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code filter.accept(field.get(input))}.
	 * 
	 * @param <GInput> Typ der Eingabe des gelieferten {@link Filter} sowie der Eingabe des {@link Field}.
	 * @param <GOutput> Typ der Eingabe des gegebenen {@link Filter} sowie des Werts des {@link Field}.
	 * @param field {@link Field} zur Navigation.
	 * @param filter {@link Field}.
	 * @return {@code navigated}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code field} bzw. {@code filter} {@code null} ist. */
	public static <GInput, GOutput> Filter<GInput> navigatedFilter(final Field<? super GInput, ? extends GOutput> field, final Filter<? super GOutput> filter)
		throws NullPointerException {
		if (field == null) throw new NullPointerException("field = null");
		if (filter == null) throw new NullPointerException("filter = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter.accept(field.get(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("navigatedFilter", field, filter);
			}

		};
	}

	/** Diese Methode gibt einen navigierenden {@link Filter} zurück, welcher von seiner Eingabe mit dem gegebenen {@link Converter} zur Eingabe des gegebenen
	 * {@link Filter} navigiert.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code filter.accept(converter.convert(input))}.
	 * 
	 * @param <GInput> Typ der Eingabe des gelieferten {@link Filter} sowie der Eingabe des {@link Field}.
	 * @param <GOutput> Typ der Eingabe des gegebenen {@link Filter} sowie der Ausgabe des {@link Converter}.
	 * @param converter {@link Converter} zur Navigation.
	 * @param filter {@link Field}.
	 * @return {@code navigated}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code converter} bzw. {@code filter} {@code null} ist. */
	public static <GInput, GOutput> Filter<GInput> navigatedFilter(final Converter<? super GInput, ? extends GOutput> converter,
		final Filter<? super GOutput> filter) throws NullPointerException {
		if (converter == null) throw new NullPointerException("converter = null");
		if (filter == null) throw new NullPointerException("filter = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter.accept(converter.convert(input));
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("navigatedFilter", converter, filter);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von dem gegebenen Filter abgelehnt werden.<br>
	 * Die Akzeptanz einer Eingabe {@code input} ist {@code !filter.accept(input)}.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@code negation}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GInput> Filter<GInput> negationFilter(final Filter<? super GInput> filter) throws NullPointerException {
		if (filter == null) throw new NullPointerException("filter = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return !filter.accept(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("negationFilter", filter);
			}

		};
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
		if (filter1 == null) throw new NullPointerException("filter1 = null");
		if (filter2 == null) throw new NullPointerException("filter2 = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter1.accept(input) || filter2.accept(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("disjunctionFilter", filter1, filter2);
			}

		};
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
		if (filter1 == null) throw new NullPointerException("filter1 = null");
		if (filter2 == null) throw new NullPointerException("filter2 = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter1.accept(input) && filter2.accept(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("conjunctionFilter", filter1, filter2);
			}

		};
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
		if (filter1 == null) throw new NullPointerException("filter1 = null");
		if (filter2 == null) throw new NullPointerException("filter2 = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				return filter1.accept(input) == filter2.accept(input);
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("equivalenceFilter", filter1, filter2);
			}

		};
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der den gegebenen {@link Filter} über {@code synchronized(filter)} synchronisiert.
	 * 
	 * @param <GInput> Typ der Eingabe.
	 * @param filter {@link Filter}.
	 * @return {@code synchronized}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GInput> Filter<GInput> synchronizedFilter(final Filter<? super GInput> filter) throws NullPointerException {
		if (filter == null) throw new NullPointerException("filter = null");
		return new Filter<GInput>() {

			@Override
			public boolean accept(final GInput input) {
				synchronized (filter) {
					return filter.accept(input);
				}
			}

			@Override
			public String toString() {
				return Objects.toInvokeString("synchronizedFilter", filter);
			}

		};
	}

}
