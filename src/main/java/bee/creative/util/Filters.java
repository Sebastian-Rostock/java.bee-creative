package bee.creative.util;

import java.util.Arrays;
import java.util.Collection;
import bee.creative.lang.Objects;

/** Diese Klasse implementiert grundlegende {@link Filter}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public class Filters {

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code item != null}. */
	@SuppressWarnings ("javadoc")
	public static class EmptyFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new EmptyFilter();

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze akzeptiert. Die Akzeptanz ist stets {@code true}. */
	@SuppressWarnings ("javadoc")
	public static class AcceptFilter extends AbstractFilter<Object> {

		public static final Filter<?> INSTANCE = new AcceptFilter();

		@Override
		public boolean accept(final Object item) {
			return true;
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, der alle Datensätze ablehnt. Die Akzeptanz ist stets {@code false}. */
	@SuppressWarnings ("javadoc")
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class ClassFilter<GItem> extends AbstractFilter<GItem> {

		public final Class<?> that;

		public ClassFilter(final Class<?> source) throws NullPointerException {
			this.that = Objects.notNull(source);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class EqualFilter<GItem> extends AbstractFilter<GItem> {

		public final Comparable<? super GItem> that;

		public EqualFilter(final Comparable<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class LowerFilter<GItem> extends AbstractFilter<GItem> {

		public final Comparable<? super GItem> that;

		public LowerFilter(final Comparable<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class HigherFilter<GItem> extends AbstractFilter<GItem> {

		public final Comparable<? super GItem> that;

		public HigherFilter(final Comparable<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final GItem item) {
			return this.that.compareTo(item) < 0;
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Klasse implementiert einen {@link Filter2}, welcher nur die Datensätz akzeptiert, die in einer gegebenen {@link Collection} enthalten sind. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@link Collection#contains(Object) this.that.contains(item)}. */
	@SuppressWarnings ("javadoc")
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class NegationFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> that;

		public NegationFilter(final Filter<? super GItem> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ der Datensätze.
	 * @param <GItem2> Typ der Datensätze des gegebenen {@link Filter}. */
	@SuppressWarnings ("javadoc")
	public static class TranslatedFilter<GItem, GItem2> extends AbstractFilter<GItem> {

		public final Filter<? super GItem2> that;

		public final Getter<? super GItem, ? extends GItem2> trans;

		public TranslatedFilter(final Filter<? super GItem2> that, final Getter<? super GItem, ? extends GItem2> trans) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.trans = Objects.notNull(trans);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class DisjunctionFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> that1;

		public final Filter<? super GItem> that2;

		public DisjunctionFilter(final Filter<? super GItem> that1, final Filter<? super GItem> that2) throws NullPointerException {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ der Datensätze. */
	@SuppressWarnings ("javadoc")
	public static class ConjunctionFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> that1;

		public final Filter<? super GItem> that2;

		public ConjunctionFilter(final Filter<? super GItem> that1, final Filter<? super GItem> that2) throws NullPointerException {
			this.that1 = Objects.notNull(that1);
			this.that2 = Objects.notNull(that2);
		}

		@Override
		public boolean accept(final GItem item) {
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
	 * @param <GItem> Typ des Datensatzes. */
	@SuppressWarnings ("javadoc")
	public static class SynchronizedFilter<GItem> extends AbstractFilter<GItem> {

		public final Filter<? super GItem> that;

		public final Object mutex;

		public SynchronizedFilter(final Filter<? super GItem> that, final Object mutex) throws NullPointerException {
			this.that = Objects.notNull(that);
			this.mutex = Objects.notNull(mutex, this);
		}

		@Override
		public boolean accept(final GItem item) {
			synchronized (this.mutex) {
				return this.that.accept(item);
			}
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class GetterFilter<GItem> extends AbstractFilter<GItem> {

		public final Getter<? super GItem, Boolean> that;

		public GetterFilter(final Getter<? super GItem, Boolean> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final GItem entry) {
			final Boolean result = this.that.get(entry);
			return (result != null) && result.booleanValue();
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class SourceFilter extends AbstractFilter<Object> {

		public final Translator<?, ?> that;

		public SourceFilter(final Translator<?, ?> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final Object item) {
			return this.that.isSource(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	static class TargetFilter extends AbstractFilter<Object> {

		public final Translator<?, ?> that;

		public TargetFilter(final Translator<?, ?> that) throws NullPointerException {
			this.that = Objects.notNull(that);
		}

		@Override
		public boolean accept(final Object item) {
			return this.that.isTarget(item);
		}

		@Override
		public String toString() {
			return Objects.toInvokeString(this, this.that);
		}

	}

	/** Diese Methode liefert den {@link EmptyFilter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Filter2<GItem> empty() {
		return (Filter2<GItem>)EmptyFilter.INSTANCE;
	}

	/** Diese Methode liefert den {@link AcceptFilter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Filter2<GItem> accept() {
		return (Filter2<GItem>)AcceptFilter.INSTANCE;
	}

	/** Diese Methode liefert den {@link RejectFilter}. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Filter2<GItem> reject() {
		return (Filter2<GItem>)RejectFilter.INSTANCE;
	}

	/** Diese Methode liefert den gegebenen {@link Filter} als {@link Filter2}. Wenn er {@code null} ist, wird der {@link EmptyFilter} geliefert. */
	@SuppressWarnings ("unchecked")
	public static <GItem> Filter2<GItem> from(final Filter<? super GItem> that) {
		if (that == null) return Filters.empty();
		if (that instanceof Filter2) return (Filter2<GItem>)that;
		return Filters.translate(that, Getters.<GItem>neutral());
	}

	/** Diese Methode ist eine Abkürzung für {@code value ? Filters.accept() : Filters.reject()}.
	 *
	 * @see #accept()
	 * @see #reject() */
	public static <GItem> Filter2<GItem> fromValue(final boolean value) {
		return value ? Filters.<GItem>accept() : Filters.<GItem>reject();
	}

	/** Diese Methode ist eine Abkürzung für {@link ClassFilter new ClassFilter<>(that)}. */
	public static <GItem> Filter2<GItem> fromClass(final Class<?> that) throws NullPointerException {
		return new ClassFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link EqualFilter new EqualFilter<>(that)}. */
	public static <GItem> Filter2<GItem> fromEqual(final Comparable<? super GItem> that) throws NullPointerException {
		return new EqualFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link LowerFilter new LowerFilter<>(that)}. */
	public static <GItem> Filter2<GItem> fromLower(final Comparable<? super GItem> that) throws NullPointerException {
		return new LowerFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link HigherFilter new HigherFilter<>(that)}. */
	public static <GItem> Filter2<GItem> fromHigher(final Comparable<? super GItem> that) throws NullPointerException {
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

	/** Diese Methode gibt einen {@link Filter2} zu {@link Translator#isSource(Object)} des gegebenen {@link Translator} zurück. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code translator.isSource(item)}. */
	public static Filter2<Object> fromSource(final Translator<?, ?> that) throws NullPointerException {
		return new SourceFilter(that);
	}

	/** Diese Methode gibt einen {@link Filter2} zu {@link Translator#isTarget(Object)} des gegebenen {@link Translator} zurück. Die Akzeptanz eines Datensatzes
	 * {@code item} ist {@code translator.isTarget(item)}. */
	public static Filter2<Object> fromTarget(final Translator<?, ?> that) throws NullPointerException {
		return new TargetFilter(that);
	}

	/** Diese Methode liefert einen {@link Filter2} zu {@link Getter#get(Object)} des gegebenen {@link Getter}. Die Akzeptanz eines Datensatzes {@code item}
	 * entspricht {@code Boolean.TRUE.equals(that.get(item))}.
	 *
	 * @param <GItem> Typ des Datensatzes. */
	public static <GItem> Filter2<GItem> fromGetter(final Getter<? super GItem, Boolean> that) throws NullPointerException {
		return new GetterFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link NegationFilter new NegationFilter<>(that)}. */
	public static <GItem> Filter2<GItem> negate(final Filter<? super GItem> that) throws NullPointerException {
		return new NegationFilter<>(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link DisjunctionFilter new DisjunctionFilter<>(that1, that2)}. */
	public static <GItem> Filter2<GItem> disjoin(final Filter<? super GItem> that1, final Filter<? super GItem> that2) throws NullPointerException {
		return new DisjunctionFilter<>(that1, that2);
	}

	/** Diese Methode ist eine Abkürzung für {@link ConjunctionFilter new ConjunctionFilter<>(that1, that2)}. */
	public static <GItem> Filter2<GItem> conjoin(final Filter<? super GItem> that1, final Filter<? super GItem> that2) throws NullPointerException {
		return new ConjunctionFilter<>(that1, that2);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#fromGetter(Getter) Filters.fromGetter(Getters.fromFilter(that).buffer())}.
	 *
	 * @see Getters#fromFilter(Filter)
	 * @see Getters#buffer(Getter) */
	public static <GItem> Filter2<GItem> buffer(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.fromGetter(Getters.fromFilter(that).buffer());
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#fromGetter(Getter) Filters.fromGetter(Getters.fromFilter(that).buffer(mode, hasher))}.
	 *
	 * @see Getters#fromFilter(Filter)
	 * @see Getters#buffer(Getter, int, Hasher) */
	public static <GItem> Filter2<GItem> buffer(final Filter<? super GItem> that, final int mode, final Hasher hasher)
		throws NullPointerException, IllegalArgumentException {
		return Filters.fromGetter(Getters.fromFilter(that).buffer(mode, hasher));
	}

	/** Diese Methode ist eine Abkürzung für {@link TranslatedFilter new TranslatedFilter<>(that, trans)}. */
	public static <GSource, GTarget> Filter2<GTarget> translate(final Filter<? super GSource> that, final Getter<? super GTarget, ? extends GSource> trans)
		throws NullPointerException {
		return new TranslatedFilter<>(that, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronize(Filter, Object) Filters.synchronize(that, that)}. */
	public static <GItem> Filter2<GItem> synchronize(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.synchronize(that, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link SynchronizedFilter new SynchronizedFilter<>(that, mutex)}. */
	public static <GItem> Filter2<GItem> synchronize(final Filter<? super GItem> that, final Object mutex) throws NullPointerException {
		return new SynchronizedFilter<>(that, mutex);
	}

}
