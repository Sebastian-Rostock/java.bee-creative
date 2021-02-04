package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
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

	public static class EmptyFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new EmptyFilter();

	}

	public static class ValueFilter extends AbstractFilter<Object> {

		public static final ValueFilter TRUE = new ValueFilter(true);

		public static final ValueFilter FALSE = new ValueFilter(false);

		public final boolean target;

		public ValueFilter(final boolean source) {
			this.target = source;
		}

		@Override
		public boolean accept(final Object item) {
			return this.target;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	public static class ClassFilter<GItem> extends AbstractFilter<GItem> {

		public final Class<?> target;

		public ClassFilter(final Class<?> source) throws NullPointerException {
			this.target = Objects.notNull(source);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.target.isInstance(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	public static class NegationFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> target;

		public NegationFilter(final Filter<? super GItem> source) {
			this.target = Objects.notNull(source);
		}

		@Override
		public boolean accept(final GItem item) {
			return !this.target.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	public static class TranslatedFilter<GSource, GTarget> extends AbstractFilter<GTarget> {

		public final Filter<? super GSource> target;

		public final Getter<? super GTarget, ? extends GSource> trans;

		public TranslatedFilter(final Getter<? super GTarget, ? extends GSource> trans, final Filter<? super GSource> target) {
			this.target = Objects.notNull(target);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean accept(final GTarget item) {
			return this.target.accept(this.trans.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.trans, this.target);
		}

	}

	public static class DisjunctionFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> target1;

		public final Filter<? super GItem> target2;

		public DisjunctionFilter(final Filter<? super GItem> target1, final Filter<? super GItem> target2) {
			this.target1 = Objects.notNull(target1);
			this.target2 = Objects.notNull(target2);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.target1.accept(item) || this.target2.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target1, this.target2);
		}

	}

	public static class ConjunctionFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> target1;

		public final Filter<? super GItem> target2;

		public ConjunctionFilter(final Filter<? super GItem> target1, final Filter<? super GItem> target2) {
			this.target1 = Objects.notNull(target1);
			this.target2 = Objects.notNull(target2);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.target1.accept(item) && this.target2.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target1, this.target2);
		}

	}

	public static class SynchronizedFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> target;

		public final Object mutex;

		public SynchronizedFilter(final Filter<? super GItem> target, final Object mutex) {
			this.target = Objects.notNull(target);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean accept(final GItem item) {
			synchronized (this.mutex) {
				return this.target.accept(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	public static class GetterFilter<GItem> extends AbstractFilter<GItem> {

		public final Getter<? super GItem, Boolean> target;

		public GetterFilter(final Getter<? super GItem, Boolean> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final GItem entry) {
			final Boolean result = this.target.get(entry);
			return (result != null) && result.booleanValue();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class SourceFilter extends AbstractFilter<Object> {

		public final Translator<?, ?> target;

		public SourceFilter(final Translator<?, ?> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final Object item) {
			return this.target.isSource(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class TargetFilter extends AbstractFilter<Object> {

		public final Translator<?, ?> target;

		public TargetFilter(final Translator<?, ?> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final Object item) {
			return this.target.isTarget(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class LowerFilter<GItem> extends AbstractFilter<GItem> {

		public final Comparable<? super GItem> target;

		public LowerFilter(final Comparable<? super GItem> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.target.compareTo(item) >= 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class HigherFilter<GItem> extends AbstractFilter<GItem> {

		public final Comparable<? super GItem> target;

		public HigherFilter(final Comparable<? super GItem> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.target.compareTo(item) <= 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	static class EqualFilter<GItem> extends AbstractFilter<GItem> {

		public final Comparable<? super GItem> target;

		public EqualFilter(final Comparable<? super GItem> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.target.compareTo(item) == 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	/** Diese Klasse implementiert {@link Filters#fromItems(Collection)} */
	static class ContainsFilter extends AbstractFilter<Object> {

		public final Collection<?> target;

		public ContainsFilter(final Collection<?> target) throws NullPointerException {
			this.target = Objects.notNull(target);
		}

		@Override
		public boolean accept(final Object item) {
			return this.target.contains(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.target);
		}

	}

	/** Diese Methode gibt den {@link Filter} zurück, der alle Elemente akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Elements {@code item} ist
	 * {@code item != null}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @return {@code null}-{@link Filter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Filter<GItem> empty() {
		return (Filter<GItem>)EmptyFilter.INSTANCE;
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der stets die gegebene Akzeptanz liefert.
	 *
	 * @param value Akzeptanz.
	 * @return {@code value}-{@link Filter}. */
	public static Filter<Object> from(final boolean value) {
		return value ? ValueFilter.TRUE : ValueFilter.FALSE;
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

	/** Diese Methode gibt einen {@link Filter} zurück, der nur die Eingaben akzeptiert, die Instanzen der gegebenen {@link Class} oder ihrer Nachfahren sind. Die
	 * Akzeptanz eines Elements {@code item} ist {@code itemType.isInstance(item)}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param itemClass {@link Class} der akzeptierten Eingaben.
	 * @return {@code type}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code itemType} {@code null} ist. */
	public static <GItem> Filter<GItem> fromClass(final Class<?> itemClass) throws NullPointerException {
		return new ClassFilter<>(itemClass);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung gleich der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) == 0}.
	 *
	 * @param <GItem> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code equal}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GItem> Filter<GItem> fromEqual(final Comparable<? super GItem> comparable) throws NullPointerException {
		return new EqualFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung kleiner der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) >= 0}.
	 *
	 * @param <GInput> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code lower}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> fromLower(final Comparable<? super GInput> comparable) throws NullPointerException {
		return new LowerFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Elemente akzeptiert, deren Ordnung größer der des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Elements {@code item} ist {@code comparable.compareTo(item) <= 0}.
	 *
	 * @param <GInput> Typ der Elemente.
	 * @param comparable {@link Comparable} zur Ermittlung des Vergleichswerts.
	 * @return {@code higher}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code comparable} {@code null} ist. */
	public static <GInput> Filter<GInput> fromHigher(final Comparable<? super GInput> comparable) throws NullPointerException {
		return new HigherFilter<>(comparable);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die gegebenen Eingaben akzeptiert.
	 *
	 * @see #fromItems(Collection)
	 * @param items akzeptierte Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code items} {@code null} ist. */
	public static Filter<Object> fromItems(final Object... items) throws NullPointerException {
		if (items.length == 0) return Filters.from(false);
		if (items.length == 1) return Filters.fromItems(java.util.Collections.singleton(items[0]));
		return Filters.fromItems(new HashSet2<>(Arrays.asList(items)));
	}

	/** Diese Methode gibt einen {@link Filter} zurück, welcher nur die Eingaben akzeptiert, die in der gegebenen {@link Collection} enthalten sind. Die Akzeptanz
	 * einer Eingabe {@code input} ist {@code collection.contains(input)}.
	 *
	 * @param collection {@link Collection} der akzeptierten Eingaben.
	 * @return {@code contains}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code collection} {@code null} ist. */
	public static Filter2<Object> fromItems(final Collection<?> collection) throws NullPointerException {
		return new ContainsFilter(collection);
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
	 * @see #toBuffered(Filter)
	 * @param <GItem> Typ der Elemente.
	 * @param filter {@link Filter}.
	 * @return {@code buffered}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GItem> Filter<GItem> toBuffered(final Filter<? super GItem> filter) throws NullPointerException {
		return Filters.toBuffered(Integer.MAX_VALUE, Pointers.SOFT, filter);
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
	public static <GItem> Filter<GItem> toBuffered(final int limit, final int mode, final Filter<? super GItem> filter)
		throws NullPointerException, IllegalArgumentException {
		return Filters.from(Getters.toBuffered(limit, mode, Pointers.HARD, Getters.from(filter)));
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
	public static <GSource, GTarget> Filter2<GTarget> translate(final Getter<? super GTarget, ? extends GSource> toSource,
		final Filter<? super GSource> filter) throws NullPointerException {
		return new TranslatedFilter<>(toSource, filter);
	}

	public static <GItem> Filter<GItem> synchronize(final Filter<? super GItem> target) throws NullPointerException {
		return Filters.synchronize(target, target);
	}

	/** Diese Methode gibt einen {@link Filter} zurück, der den gegebenen {@link Filter} über {@code synchronized(filter)} synchronisiert. Wenn das
	 * Synchronisationsobjekt {@code null} ist, wird der erzeugte {@link Filter} als Synchronisationsobjekt verwendet.
	 *
	 * @param target {@link Filter}.
	 * @param mutex Synchronisationsobjekt oder {@code null}.
	 * @param <GItem> Typ der Elemente.
	 * @return {@code synchronized}-{@link Filter}.
	 * @throws NullPointerException Wenn {@code filter} {@code null} ist. */
	public static <GItem> Filter<GItem> synchronize(final Filter<? super GItem> target, final Object mutex) throws NullPointerException {
		return new SynchronizedFilter<>(target, mutex);
	}

}
