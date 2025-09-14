package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Filter}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Filters {

	/** Diese Methode ist eine Abkürzung für {@code value ? acceptFilter() : rejectFilter()}.
	 *
	 * @see #acceptFilter()
	 * @see #rejectFilter() */
	public static <ITEM> Filter2<ITEM> filterFrom(boolean value) {
		return value ? acceptFilter() : rejectFilter();
	}

	/** Diese Methode liefert den gegebenen {@link Filter} als {@link Filter2}. Wenn er {@code null} ist, wird der {@link EmptyFilter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> filterFrom(final Filter<? super ITEM> that) {
		if (that == null) return Filters.emptyFilter();
		if (that instanceof Filter2) return (Filter2<ITEM>)that;
		return Filters.translateFilter(that, Getters.<ITEM>neutralGetter());
	}

	/** Diese Methode liefert den {@link EmptyFilter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> emptyFilter() {
		return (Filter2<ITEM>)EmptyFilter.INSTANCE;
	}

	/** Diese Methode liefert den {@link AcceptFilter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> acceptFilter() {
		return (Filter2<ITEM>)AcceptFilter.INSTANCE;
	}

	/** Diese Methode liefert den {@link RejectFilter}. */
	@SuppressWarnings ("unchecked")
	public static <ITEM> Filter2<ITEM> rejectFilter() {
		return (Filter2<ITEM>)RejectFilter.INSTANCE;
	}

	/** Diese Methode ist eine Abkürzung für {@link ClassFilter new ClassFilter<>(that)}. */
	public static <ITEM> Filter2<ITEM> fromClass(final Class<?> that) throws NullPointerException {
		return new ClassFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link EqualFilter new EqualFilter<>(that)}. */
	public static <ITEM> Filter2<ITEM> fromEqual(final Comparable<? super ITEM> that) throws NullPointerException {
		return new EqualFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link LowerFilter new LowerFilter<>(that)}. */
	public static <ITEM> Filter2<ITEM> fromLower(final Comparable<? super ITEM> that) throws NullPointerException {
		return new LowerFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link HigherFilter new HigherFilter<>(that)}. */
	public static <ITEM> Filter2<ITEM> fromHigher(final Comparable<? super ITEM> that) throws NullPointerException {
		return new HigherFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromItems(Collection) Filters.fromItems(Iterables.toSet(Arrays.asList(items)))}.
	 *
	 * @see Arrays#asList(Object...)
	 * @see Iterables#toSet(Iterable) */
	public static Filter2<Object> fromItems(final Object... items) throws NullPointerException {
		return Filters.fromItems(Iterables.toSet(Arrays.asList(items)));
	}

	/** Diese Methode ist eine Abkürzung für {@link #fromItems(Collection) Filters.fromItems(Iterables.toSet(that))}.
	 *
	 * @see Iterables#toSet(Iterable) */
	public static Filter2<Object> fromItems(final Iterable<?> that) throws NullPointerException {
		return Filters.fromItems(Iterables.toSet(that));
	}

	/** Diese Methode ist eine Abkürzung für {@link ContainsFilter new ContainsFilter<>(that)}. */
	public static Filter2<Object> fromItems(final Collection<?> that) throws NullPointerException {
		return new ContainsFilter(that);
	}

	/** Diese Methode liefert einen {@link Filter2} zu {@link Getter#get(Object)} des gegebenen {@link Getter}. Die Akzeptanz eines Datensatzes {@code item}
	 * entspricht {@code Boolean.TRUE.equals(that.get(item))}.
	 *
	 * @param <ITEM> Typ des Datensatzes. */
	public static <ITEM> Filter2<ITEM> fromGetter(final Getter<? super ITEM, Boolean> that) throws NullPointerException {
		return new GetterFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link NegationFilter new NegationFilter<>(that)}. */
	public static <ITEM> Filter2<ITEM> negateFilter(final Filter<? super ITEM> that) throws NullPointerException {
		return new NegationFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link DisjunctionFilter new DisjunctionFilter<>(that1, that2)}. */
	public static <ITEM> Filter2<ITEM> disjoinFilter(final Filter<? super ITEM> that1, final Filter<? super ITEM> that2) throws NullPointerException {
		return new DisjunctionFilter<>(that1, that2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConjunctionFilter new ConjunctionFilter<>(that1, that2)}. */
	public static <ITEM> Filter2<ITEM> conjoinFilter(final Filter<? super ITEM> that1, final Filter<? super ITEM> that2) throws NullPointerException {
		return new ConjunctionFilter<>(that1, that2);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#fromGetter(Getter) Filters.fromGetter(Getters.fromFilter(that).buffer())}.
	 *
	 * @see Getters#fromFilter(Filter)
	 * @see Getters#buffer(Getter) */
	public static <ITEM> Filter2<ITEM> bufferFilter(final Filter<? super ITEM> that) throws NullPointerException {
		return Filters.fromGetter(Getters.fromFilter(that).buffer());
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#fromGetter(Getter) Filters.fromGetter(Getters.fromFilter(that).buffer(mode, hasher))}.
	 *
	 * @see Getters#fromFilter(Filter)
	 * @see Getters#buffer(Getter, int, Hasher) */
	public static <ITEM> Filter2<ITEM> bufferFilter(final Filter<? super ITEM> that, final int mode, final Hasher hasher)
		throws NullPointerException, IllegalArgumentException {
		return Filters.fromGetter(Getters.fromFilter(that).buffer(mode, hasher));
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedFilter new TranslatedFilter<>(that, trans)}. */
	public static <GSource, GTarget> Filter2<GTarget> translateFilter(final Filter<? super GSource> that, final Getter<? super GTarget, ? extends GSource> trans)
		throws NullPointerException {
		return new TranslatedFilter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronizeFilter(Filter, Object) Filters.synchronize(that, that)}. */
	public static <ITEM> Filter2<ITEM> synchronizeFilter(final Filter<? super ITEM> that) throws NullPointerException {
		return Filters.synchronizeFilter(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedFilter new SynchronizedFilter<>(that, mutex)}. */
	public static <ITEM> Filter2<ITEM> synchronizeFilter(final Filter<? super ITEM> that, final Object mutex) throws NullPointerException {
		return new SynchronizedFilter<>(that, mutex);
	}

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code item != null}. */

	public static class EmptyFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new EmptyFilter();

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze akzeptiert. Die Akzeptanz ist stets {@code true}. */

	public static class AcceptFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new AcceptFilter();

		@Override
		public boolean accept(final Object item) {
			return true;
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze ablehnt. Die Akzeptanz ist stets {@code false}. */

	public static class RejectFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new RejectFilter();

		@Override
		public boolean accept(final Object item) {
			return false;
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze akzeptiert, die Instanzen einer gegebenen {@link Class} sind. Die Akzeptanz eines
	 * Datensatzes {@code item} ist {@code this.that.isInstance(item)}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class ClassFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Class<?> that;

		public ClassFilter(final Class<?> source) throws NullPointerException {
			this.that = Objects.notNull(source);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that.isInstance(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der nur die Datensätze akzeptiert, deren Ordnung gleich der eines gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.that.compareTo(item) == 0}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class EqualFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Comparable<? super ITEM> that;

		public EqualFilter(final Comparable<? super ITEM> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that.compareTo(item) == 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der nur die Datensätze akzeptiert, deren Ordnung kleiner der eines gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.that.compareTo(item) > 0}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class LowerFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Comparable<? super ITEM> that;

		public LowerFilter(final Comparable<? super ITEM> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that.compareTo(item) > 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der nur die Datensätze akzeptiert, deren Ordnung größer der eines gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.that.compareTo(item) < 0}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class HigherFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Comparable<? super ITEM> that;

		public HigherFilter(final Comparable<? super ITEM> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that.compareTo(item) < 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, welcher nur die Datensätz akzeptiert, die in einer gegebenen {@link Collection} enthalten sind. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@link Collection#contains(Object) this.that.contains(item)}. */

	public static class ContainsFilter extends AbstractFilter<Object> {

		public final Collection<?> that;

		public ContainsFilter(final Collection<?> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final Object item) {
			return this.that.contains(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der nur die Datensätze akzeptiert, die von einem gegebenen {@link Filter} abgelehnt werden. Die
	 * Akzeptanz eines Datensatzen {@code item} ist {@code !this.that.accept(item)}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class NegationFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Filter<? super ITEM> that;

		public NegationFilter(final Filter<? super ITEM> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public Filter2<ITEM> negate() {
			return Filters.filterFrom(this.that);
		}

		@Override
		public boolean accept(final ITEM item) {
			return !this.that.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen übersetzten {@link Filter2}, der nur die Datensätze akzeptiert, die über einen gegebenen {@link Getter} umgewandelt von
	 * einem gegebenen {@link Filter} akzeptiert werden. Die Akzeptanz eines Datensatzes {@code item} ist {@code this.that.accept(this.trans.get(item))}.
	 *
	 * @param <ITEM> Typ der Datensätze.
	 * @param <ITEM2> Typ der Datensätze des gegebenen {@link Filter}. */

	public static class TranslatedFilter<ITEM, ITEM2> extends AbstractFilter<ITEM> {

		public final Filter<? super ITEM2> that;

		public final Getter<? super ITEM, ? extends ITEM2> trans;

		public TranslatedFilter(final Filter<? super ITEM2> that, final Getter<? super ITEM, ? extends ITEM2> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that.accept(this.trans.get(item));
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that, this.trans);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der nur die Datensätze akzeptiert, die von mindestens einem der gegebenen {@link Filter} akzeptiert
	 * werden. Die Akzeptanz eines Datensatzes {@code item} ist {@code this.that1.accept(item) || this.that2.accept(item)}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class DisjunctionFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Filter<? super ITEM> that1;

		public final Filter<? super ITEM> that2;

		public DisjunctionFilter(final Filter<? super ITEM> that1, final Filter<? super ITEM> that2) throws NullPointerException {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that1.accept(item) || this.that2.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that1, this.that2);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der nur die Datensätze akzeptiert, die von beiden gegebenen {@link Filter} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.that1.accept(item) && this.that2.accept(item)}.
	 *
	 * @param <ITEM> Typ der Datensätze. */

	public static class ConjunctionFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Filter<? super ITEM> that1;

		public final Filter<? super ITEM> that2;

		public ConjunctionFilter(final Filter<? super ITEM> that1, final Filter<? super ITEM> that2) throws NullPointerException {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public boolean accept(final ITEM item) {
			return this.that1.accept(item) && this.that2.accept(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that1, this.that2);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, welcher einen gegebenen {@link Filter} über {@code synchronized(this.mutex)} synchronisiert. Wenn dieses
	 * Synchronisationsobjekt {@code null} ist, wird {@code this} verwendet.
	 *
	 * @param <ITEM> Typ des Datensatzes. */

	public static class SynchronizedFilter<ITEM> extends AbstractFilter<ITEM> {

		public SynchronizedFilter(final Filter<? super ITEM> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean accept(final ITEM item) {
			synchronized (this.mutex) {
				return this.that.accept(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

		public final Filter<? super ITEM> that;

		public final Object mutex;

	}

	static class GetterFilter<ITEM> extends AbstractFilter<ITEM> {

		public final Getter<? super ITEM, Boolean> that;

		public GetterFilter(final Getter<? super ITEM, Boolean> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final ITEM entry) {
			final Boolean result = this.that.get(entry);
			return (result != null) && result.booleanValue();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

}
