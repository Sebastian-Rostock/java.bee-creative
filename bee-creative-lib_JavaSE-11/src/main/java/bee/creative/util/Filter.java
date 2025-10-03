package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.BufferedGetter.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;
import static bee.creative.util.Iterables.iterableFromArray;
import static bee.creative.util.Iterables.iterableToSet;
import java.util.Collection;
import bee.creative.lang.Objects;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Datensätze über {@link Filter#accept(Object)} akzeptieren oder ablehnen kann.
 *
 * @see FilterUtil
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Datensätze. */
public interface Filter<ITEM> {

	/** Diese Methode ist eine Abkürzung für {@code value ? acceptFilter() : rejectFilter()}.
	 *
	 * @see #acceptFilter()
	 * @see #rejectFilter() */
	static <ITEM> Filter<ITEM> filterFrom(boolean value) {
		return value ? acceptFilter() : rejectFilter();
	}

	/** Diese Methode liefert den gegebenen {@link Filter}, sofern dieser nicht {@code null} ist. Anernfalls liefet sie {@link #emptyFilter()}. */
	@SuppressWarnings ("unchecked")
	static <ITEM> Filter<ITEM> filterFrom(Filter<? super ITEM> that) {
		return that == null ? emptyFilter() : (Filter<ITEM>)that;
	}

	/** Diese Methode liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung größer als die des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) < 0}. */
	static <ITEM> Filter<ITEM> filterFromEqual(Comparable<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		return (item) -> that.compareTo(item) == 0;
	}

	/** Diese Klasse liefert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung kleiner als die des gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code that.compareTo(item) > 0}. */
	static <ITEM> Filter<ITEM> filterFromLower(Comparable<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		return (item) -> that.compareTo(item) > 0;
	}

	/** Diese Klasse implementiert einen {@link Filter}, der nur die Datensätze akzeptiert, deren Ordnung größer der eines gegebenen {@link Comparable} ist. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.that.compareTo(item) < 0}.
	 *
	 * @param <ITEM> Typ der Datensätze. */
	static <ITEM> Filter<ITEM> filterFromHigher(Comparable<? super ITEM> that) throws NullPointerException {
		Objects.notNull(that);
		return (item) -> that.compareTo(item) < 0;
	}

	/** Diese Methode ist eine Abkürzung für {@link #filterFromItems(Collection) filterFromItems(iterableFromArray(items))}.
	 *
	 * @see Iterables#iterableFromArray(Object...) */
	static Filter<Object> filterFromItems(Object... items) throws NullPointerException {
		return filterFromItems(iterableFromArray(items));
	}

	/** Diese Methode ist eine Abkürzung für {@link #filterFromItems(Collection) filterFromItems(iterableToSet(that))}.
	 *
	 * @see Iterables#iterableToSet(Iterable) */
	static Filter<Object> filterFromItems(Iterable<?> that) throws NullPointerException {
		return filterFromItems(iterableToSet(that));
	}

	/** Diese Methode liefet den {@link Filter} zu {@link Collection#contains(Object)}. Die Akzeptanz eines Datensatzes {@code item} ist
	 * {@code that.contains(item)}. */
	static Filter<Object> filterFromItems(Collection<?> that) throws NullPointerException {
		Objects.notNull(that);
		return that::contains;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze akzeptiert, die nicht {@code null} sind. Die Akzeptanz eines Datensatzes {@code item} ist
	 * {@code item != null}. */
	@SuppressWarnings ("unchecked")
	static <ITEM> Filter<ITEM> emptyFilter() {
		return (Filter<ITEM>)FilterUtil.emptyFilter;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze akzeptiert. Die Akzeptanz ist stets {@code true}. */
	@SuppressWarnings ("unchecked")
	static <ITEM> Filter<ITEM> acceptFilter() {
		return (Filter<ITEM>)FilterUtil.acceptFilter;
	}

	/** Diese Methode liefert einen {@link Filter}, der alle Datensätze ablehnt. Die Akzeptanz ist stets {@code false}. */
	@SuppressWarnings ("unchecked")
	static <ITEM> Filter<ITEM> rejectFilter() {
		return (Filter<ITEM>)FilterUtil.rejectFilter;
	}

	/** Diese Methode liefert nur dann {@code true}, wenn der gegebene Datensatz akzeptiert wird.
	 *
	 * @param item Datensatz.
	 * @return Akzeptanz. */
	boolean accept(ITEM item);

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Filter<ITEM> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, int, Hasher) this.toGetter().buffer(mode, hasher)::get}. */
	default Filter<ITEM> buffer(int mode, Hasher hasher) throws IllegalArgumentException {
		return this.toGetter().buffer(mode, hasher)::get;
	}

	/** Diese Methode liefert einen {@link Filter}, der nur die Datensätze akzeptiert, die diesem und dem gegebenen {@link Filter} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(item) && that.accept(item)}. */
	default Filter<ITEM> conjoin(Filter<? super ITEM> that) throws NullPointerException {
		notNull(that);
		return item -> this.accept(item) && that.accept(item);
	}

	/** Diese Methode liefert einen {@link Filter}, der nur die Datensätze akzeptiert, die von diesem oder dem gegebenen {@link Filter} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(item) || that.accept(item)}. */
	default Filter<ITEM> disjoin(Filter<? super ITEM> that) throws NullPointerException {
		notNull(that);
		return item -> this.accept(item) || that.accept(item);
	}

	/** Diese Methode liefert einen {@link Filter}, der nur die Datensätze akzeptiert, die von diesem {@link Filter} abgelehnt werden. Die Akzeptanz eines
	 * Datensatzen {@code item} ist {@code !this.accept(item)}. */
	default Filter<ITEM> negate() {
		return item -> !this.accept(item);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Filter<ITEM> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode liefert einen {@link Filter}, der diesen {@link Filter} mit dem gegebenen Synchronisationsobjekt {@code mutex} über
	 * {@code synchronized(mutex)} synchronisiert. Wenn letzteres {@code null} ist, wird der gelieferte {@link Filter} verwendet. */
	default Filter<ITEM> synchronize(Object mutex) {
		return new FilterUtil.SynchronizedFilter<>(this, mutex);
	}

	/** Diese Methode liefert einen übersetzten {@link Filter}, der nur die Datensätze akzeptiert, die über den gegebenen {@link Getter} umgewandelt von diesem
	 * {@link Filter} akzeptiert werden. Die Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(trans.get(item))}.
	 *
	 * @param <ITEM2> Typ der Datensätze des gelieferten {@link Filter}. */
	default <ITEM2> Filter<ITEM2> translate(Getter<? super ITEM2, ? extends ITEM> trans) throws NullPointerException {
		notNull(trans);
		return item -> this.accept(trans.get(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link #conjoin(Filter) conjoin(that)}. */
	default Filter<ITEM> and(Filter<? super ITEM> that) throws NullPointerException {
		return this.conjoin(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #negate()}. */
	default Filter<ITEM> not() {
		return this.negate();
	}

	/** Diese Methode ist eine Abkürzung für {@link #disjoin(Filter) disjoin(that)}. */
	default Filter<ITEM> or(Filter<? super ITEM> that) throws NullPointerException {
		return this.disjoin(that);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #accept(Object) this::accept}. */
	default Getter3<ITEM, Boolean> toGetter() {
		return this::accept;
	}

}
