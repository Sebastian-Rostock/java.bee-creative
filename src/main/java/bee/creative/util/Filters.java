package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import bee.creative.lang.Objects;
import bee.creative.ref.Pointer;
import bee.creative.ref.Pointers;
import bee.creative.ref.SoftPointer;

/** Diese Klasse implementiert grundlegende {@link Filter}.
 *
 * @see Filter
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Filters {

	/** Diese Klasse implementiert {@link Filters#nullFilter()} */
	@SuppressWarnings ("javadoc")
	public static class NullFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new NullFilter();

		@Override
		public boolean accept(final Object item) {
			return item != null;
		}

	}

	/** Diese Klasse implementiert {@link Filters#from(boolean)} */
	@SuppressWarnings ("javadoc")
	public static class ValueFilter extends AbstractFilter<Object> {

		public static final ValueFilter TRUE = new ValueFilter(true);

		public static final ValueFilter FALSE = new ValueFilter(false);

		public final boolean value;

		public ValueFilter(final boolean value) {
			this.value = value;
		}

		@Override
		public boolean accept(final Object item) {
			return this.value;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.value);
		}

	}

	/** Diese Klasse implementiert {@link Filters#from(Class)} */
	@SuppressWarnings ("javadoc")
	public static class ClassFilter<GItem> extends AbstractFilter<GItem> {

		public final Class<?> itemClass;

		public ClassFilter(final Class<?> itemClass) {
			this.itemClass = Objects.notNull(itemClass);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.itemClass.isInstance(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.itemClass);
		}

	}

	/** Diese Klasse implementiert {@link Filters#negate(Filter)} */
	@SuppressWarnings ("javadoc")
	public static class NegationFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> filter;

		public NegationFilter(final Filter<? super GItem> filter) {
			this.filter = Objects.notNull(filter);
		}

		@Override
		public boolean accept(final GItem item) {
			return !this.filter.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter);
		}

	}

	/** Diese Klasse implementiert {@link Filters#concat(Getter, Filter)} */
	@SuppressWarnings ("javadoc")
	public static class TranslatedFilter<GSource, GTarget> extends AbstractFilter<GTarget> {

		public final Getter<? super GTarget, ? extends GSource> toSource;

		public final Filter<? super GSource> filter;

		public TranslatedFilter(final Getter<? super GTarget, ? extends GSource> toSource, final Filter<? super GSource> filter) {
			this.toSource = Objects.notNull(toSource);
			this.filter = Objects.notNull(filter);
		}

		@Override
		public boolean accept(final GTarget item) {
			return this.filter.accept(this.toSource.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.toSource, this.filter);
		}

	}

	/** Diese Klasse implementiert {@link Filters#disjoin(Filter, Filter)} */
	@SuppressWarnings ("javadoc")
	public static class DisjunctionFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> filter1;

		public final Filter<? super GItem> filter2;

		public DisjunctionFilter(final Filter<? super GItem> filter1, final Filter<? super GItem> filter2) {
			this.filter1 = Objects.notNull(filter1);
			this.filter2 = Objects.notNull(filter2);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.filter1.accept(item) || this.filter2.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter1, this.filter2);
		}

	}

	/** Diese Klasse implementiert {@link Filters#conjoin(Filter, Filter)} */
	@SuppressWarnings ("javadoc")
	public static class ConjunctionFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> filter1;

		public final Filter<? super GItem> filter2;

		public ConjunctionFilter(final Filter<? super GItem> filter1, final Filter<? super GItem> filter2) {
			this.filter1 = Objects.notNull(filter1);
			this.filter2 = Objects.notNull(filter2);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.filter1.accept(item) && this.filter2.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter1, this.filter2);
		}

	}

	/** Diese Klasse implementiert {@link Filters#synchronizedFilter(Filter, Object)} */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedFilter<GItem> extends AbstractFilter<GItem> {

		public final Object mutex;

		public final Filter<? super GItem> filter;

		public SynchronizedFilter(final Filter<? super GItem> taret, final Object mutex) {
			this.mutex = Objects.notNull(mutex, this);
			this.filter = Objects.notNull(taret);
		}

		@Override
		public boolean accept(final GItem item) {
			synchronized (this.mutex) {
				return this.filter.accept(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.filter);
		}

	}

	/** Diese Klasse implementiert {@link Filters#from(Getter)}. */
	public static class GetterFilter<GItem> extends AbstractFilter<GItem> {
	
		public final Getter<? super GItem, Boolean> getter;
	
		public GetterFilter(final Getter<? super GItem, Boolean> getter) {
			this.getter = Objects.notNull(getter);
		}
	
		@Override
		public boolean accept(final GItem entry) {
			final Boolean result = this.getter.get(entry);
			return (result != null) && result.booleanValue();
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.getter);
		}
	
	}

	/** Diese Klasse implementiert {@link Filters#fromSource(Translator)}. */
	static class SourceFilter extends AbstractFilter<Object> {
	
		public final Translator<?, ?> translator;
	
		public SourceFilter(final Translator<?, ?> translator) throws NullPointerException {
			this.translator = Objects.notNull(translator);
		}
	
		@Override
		public boolean accept(final Object item) {
			return this.translator.isSource(item);
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}
	
	}

	/** Diese Klasse implementiert {@link Filters#fromTarget(Translator)}. */
	static class TargetFilter extends AbstractFilter<Object> {
	
		public final Translator<?, ?> translator;
	
		public TargetFilter(final Translator<?, ?> translator) throws NullPointerException {
			this.translator = Objects.notNull(translator);
		}
	
		@Override
		public boolean accept(final Object item) {
			return this.translator.isTarget(item);
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.translator);
		}
	
	}

	/** Diese Klasse implementiert {@link Filters#toLowerFilter(Comparable)} */
	static class LowerFilter<GItem> extends AbstractFilter<GItem> {
	
		public final Comparable<? super GItem> comparable;
	
		public LowerFilter(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}
	
		@Override
		public boolean accept(final GItem item) {
			return this.comparable.compareTo(item) >= 0;
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}
	
	}

	/** Diese Klasse implementiert {@link Filters#toHigherFilter(Comparable)} */
	static class HigherFilter<GItem> extends AbstractFilter<GItem> {
	
		public final Comparable<? super GItem> comparable;
	
		public HigherFilter(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}
	
		@Override
		public boolean accept(final GItem item) {
			return this.comparable.compareTo(item) <= 0;
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}
	
	}

	/** Diese Klasse implementiert {@link Filters#toEqualFilter(Comparable)} */
	static class EqualFilter<GItem> extends AbstractFilter<GItem> {
	
		public final Comparable<? super GItem> comparable;
	
		public EqualFilter(final Comparable<? super GItem> comparable) {
			this.comparable = Objects.notNull(comparable);
		}
	
		@Override
		public boolean accept(final GItem item) {
			return this.comparable.compareTo(item) == 0;
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.comparable);
		}
	
	}

	/** Diese Klasse implementiert {@link Filters#fromItems(Collection)} */
	static class ContainsFilter extends AbstractFilter<Object> {
	
		public final Collection<?> collection;
	
		public ContainsFilter(final Collection<?> collection) {
			this.collection = Objects.notNull(collection);
		}
	
		@Override
		public boolean accept(final Object item) {
			return this.collection.contains(item);
		}
	
		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.collection);
		}
	
	}

	/** Diese Methode gibt den {@link Filter} zurück, der alle Elemente akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Elements {@code item} ist
	 * {@code item != null}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @return {@code null}-{@link Filter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Filter<GItem> nullFilter() {
		return (Filter<GItem>)NullFilter.INSTANCE;
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der stets die gegebene Akzeptanz liefert.
	 *
	 * @param value Akzeptanz.
	 * @return {@code value}-{@link Filter}. */
	public static Filter<Object> from(final boolean value) {
		return value ? ValueFilter.TRUE : ValueFilter.FALSE;
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der nur die Eingaben akzeptiert, die Instanzen der gegebenen {@link Class} oder ihrer Nachfahren sind. Die
	 * Akzeptanz eines Elements {@code item} ist {@code itemType.isInstance(item)}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param itemClass {@link Class} der akzeptierten Eingaben.
	 * @return {@code type}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code itemType} {@code null} ist. */
	public static <GItem> Filter<GItem> from(final Class<?> itemClass) throws NullPointerException {
		return new ClassFilter<>(itemClass);
	}

	/** Diese Methode gibt einen {@link Filter} als Adapter zu einem {@link Boolean}-{@link Getter} zurück. Die Akzeptanz einer Eingabe {@code item} entspricht
	 * {@code Boolean.TRUE.equals(getter.get(item))}.
	 *
	 * @param <GItem> Typ des Datensatzes.
	 * @param getter Eigenschaft mit {@link Boolean}-Wert.
	 * @return {@link Filter} als {@link Getter}-Adapter.
	 * @throws NullPointerException Wenn {@code getter} {@code null} ist. */
	public static <GItem> Filter<GItem> from(final Getter<? super GItem, Boolean> getter) throws NullPointerException {
		return new GetterFilter<>(getter);
	}

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isSource(Object)} des gegebenen {@link Translator} zurück. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code translator.isSource(item)}.
	 *
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Quellobjekte des {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> fromSource(final Translator<?, ?> translator) throws NullPointerException {
		return new SourceFilter(translator);
	}

	/** Diese Methode gibt einen {@link Filter} zu {@link Translator#isTarget(Object)} des gegebenen {@link Translator} zurück. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code translator.isTarget(item)}.
	 *
	 * @param translator {@link Translator}.
	 * @return {@link Filter}, der nur Zielobjekte des {@code translator} akzeptiert.
	 * @throws NullPointerException Wenn {@code translator} {@code null} ist. */
	public static Filter<Object> fromTarget(final Translator<?, ?> translator) throws NullPointerException {
		return new TargetFilter(translator);
	}

	/** Diese Methode gibt einen übersetzten {@link Filter} zurück, welcher von seinen Elementen mit dem gegebenen {@link Getter} zu den Elementen des gegebenen
	 * {@link Filter} navigiert. Die Akzeptanz eines Elements {@code item} ist {@code filter.accept(toSource.get(item))}.
	 *
	 * @param <GSource> Typ der Elemente des gegebenen {@link Filter} sowie der Ausgabe des {@link Getter}.
	 * @param <GTarget> Typ der Elemente des gelieferten {@link Filter} sowie der Eingabe des {@link Getter}.
	 * @param toSource {@link Getter} zur Übersetzung.
	 * @param filter {@link Field}.
	 * @return {@code translated}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code toSource} bzw. {@code filter} {@code null} ist. */
	public static <GSource, GTarget> Filter2<GTarget> concat(final Getter<? super GTarget, ? extends GSource> toSource,
		final Filter<? super GSource> filter) throws NullPointerException {
		return new TranslatedFilter<>(toSource, filter);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von dem gegebenen Filter abgelehnt werden. Die Akzeptanz eines
	 * Elements {@code item} ist {@code !filter.accept(item)}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@code negation}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GItem> Filter2<GItem> negate(final Filter<? super GItem> filter) throws NullPointerException {
		return new NegationFilter<>(filter);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von einem der gegebenen {@link Filter} akzeptiert werden Die
	 * Akzeptanz eines Elements {@code item} ist {@code filter1.accept(item) || filter2.accept(item)}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param filter1 erster {@link Filter}.
	 * @param filter2 zweiter {@link Filter}.
	 * @return {@code disjunction}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter1} bzw. {@code filter2} {@code null} ist. */
	public static <GItem> Filter<GItem> disjoin(final Filter<? super GItem> filter1, final Filter<? super GItem> filter2) throws NullPointerException {
		return new DisjunctionFilter<>(filter1, filter2);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die von beiden der gegebenen {@link Filter} akzeptiert werden Die
	 * Akzeptanz eines Elements {@code item} ist {@code filter1.accept(item) && filter2.accept(item)}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param filter1 erster {@link Filter}.
	 * @param filter2 zweiter {@link Filter}.
	 * @return {@code conjunction}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter1} bzw. {@code filter2} {@code null} ist. */
	public static <GItem> Filter<GItem> conjoin(final Filter<? super GItem> filter1, final Filter<? super GItem> filter2) throws NullPointerException {
		return new ConjunctionFilter<>(filter1, filter2);
	}

	/** Diese Methode gibt einen gepufferten {@link Filter} zurück, der die zu seinen Eingaben über den gegebenen {@link Filter} ermittelten Akzeptanzen intern in
	 * einer {@link Map} zur Wiederverwendung vorhält. Die Schlüssel der {@link Map} werden dabei als {@link SoftPointer} auf Elemente bestückt.
	 *
	 * @see #bufferedFilter(Filter)
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@code buffered}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GItem> Filter<GItem> bufferedFilter(final Filter<? super GItem> filter) throws NullPointerException {
		return Filters.bufferedFilter(Integer.MAX_VALUE, Pointers.SOFT, filter);
	}

	/** Diese Methode gibt einen gepufferten {@link Filter} zurück, der die zu seinen Eingaben über den gegebenen {@link Filter} ermittelten Akzeptanzen intern in
	 * einer {@link Map} zur Wiederverwendung vorhält. Die Schlüssel der {@link Map} werden dabei als {@link Pointer} auf Elemente bestückt.
	 *
	 * @see Filters#from(Getter)
	 * @see Getters#from(Filter)
	 * @see Getters#toBuffered(int, int, int, Getter)
	 * @param <GItem> Typ der Elemente.
	 * @param limit Maximum für die Anzahl der Einträge in der internen {@link Map}.
	 * @param mode Modus der {@link Pointer}, die auf die Elemente als Schlüssel der {@link Map} erzeugt werden ({@link Pointers#HARD}, {@link Pointers#SOFT},
	 *        {@link Pointers#WEAK}).
	 * @param filter {@link Filter}.
	 * @return {@code buffered}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist.
	 * @throws IllegalArgumentException Wenn {@link Getters#toBuffered(int, int, int, Getter)} eine entsprechende Ausnahme auslöst. */
	public static <GItem> Filter<GItem> bufferedFilter(final int limit, final int mode, final Filter<? super GItem> filter)
		throws NullPointerException, IllegalArgumentException {
		return Filters.from(Getters.toBuffered(limit, mode, Pointers.HARD, Getters.from(filter)));
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der den gegebenen {@link Filter} über {@code synchronized(filter)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Filter} als Synchronisationsobjekt verwendet.
	 * @param target {@link Filter}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @return {@code synchronized}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GItem> Filter<GItem> synchronizedFilter(final Filter<? super GItem> target, final Object mutex) throws NullPointerException {
		return new SynchronizedFilter<>(target, mutex);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung gleich der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) == 0}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code equal}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GItem> Filter<GItem> toEqualFilter(final Comparable<? super GItem> comparable) throws NullPointerException {
		return new EqualFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung kleiner der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) >= 0}.
	 *
	 * @param <GInput> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code lower}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> toLowerFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		return new LowerFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung größer der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) <= 0}.
	 *
	 * @param <GInput> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code higher}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> toHigherFilter(final Comparable<? super GInput> comparable) throws NullPointerException {
		return new HigherFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die gegebenen Eingaben akzeptiert.
	 *
	 * @see #fromItems(Collection)
	 * @param items akzeptierte Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static Filter<Object> fromItems(final Object... items) throws NullPointerException {
		if (items.length == 0) return from(false);
		if (items.length == 1) return Filters.fromItems(java.util.Collections.singleton(items[0]));
		return Filters.fromItems(new HashSet2<>(Arrays.asList(items)));
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die in der gegebenen {@link Collection} enthalten sind. Die Akzeptanz
	 * einer Eingabe {@code input} ist {@code collection.contains(input)}.
	 *
	 * @param collection {@link Collection} der akzeptierten Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code collection} {@code null} ist. */
	public static Filter<Object> fromItems(final Collection<?> collection) throws NullPointerException {
		return new ContainsFilter(collection);
	}

}
