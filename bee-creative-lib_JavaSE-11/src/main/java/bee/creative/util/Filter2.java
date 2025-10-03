package bee.creative.util;

import static bee.creative.lang.Objects.notNull;
import static bee.creative.util.Getters.BufferedGetter.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Datensätze über {@link Filter2#accept(Object)} akzeptieren oder ablehnen kann.
 *
 * @see Filters
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Datensätze. */
public interface Filter2<ITEM> extends Filter<ITEM> {

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Filter2<ITEM> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, int, Hasher) this.toGetter().buffer(mode, hasher)::get}. */
	default Filter2<ITEM> buffer(int mode, Hasher hasher) throws IllegalArgumentException {
		return this.toGetter().buffer(mode, hasher)::get;
	}

	/** Diese Methode liefert einen {@link Filter2}, der nur die Datensätze akzeptiert, die von diesem {@link Filter2} abgelehnt werden. Die Akzeptanz eines
	 * Datensatzen {@code item} ist {@code !this.accept(item)}. */
	default Filter2<ITEM> negate() {
		return item -> !this.accept(item);
	}

	/** Diese Methode liefert einen {@link Filter2}, der nur die Datensätze akzeptiert, die von diesem oder dem gegebenen {@link Filter2} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(item) || that.accept(item)}. */
	default Filter2<ITEM> disjoin(Filter2<? super ITEM> that) throws NullPointerException {
		notNull(that);
		return item -> this.accept(item) || that.accept(item);
	}

	/** Diese Methode liefert einen {@link Filter2}, der nur die Datensätze akzeptiert, die diesem und dem gegebenen {@link Filter2} akzeptiert werden. Die
	 * Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(item) && that.accept(item)}. */
	default Filter2<ITEM> conjoin(Filter2<? super ITEM> that) throws NullPointerException {
		notNull(that);
		return item -> this.accept(item) && that.accept(item);
	}

	/** Diese Methode liefert einen übersetzten {@link Filter2}, der nur die Datensätze akzeptiert, die über den gegebenen {@link Getter} umgewandelt von diesem
	 * {@link Filter2} akzeptiert werden. Die Akzeptanz eines Datensatzes {@code item} ist {@code this.accept(trans.get(item))}.
	 *
	 * @param <ITEM2> Typ der Datensätze des gelieferten {@link Filter2}. */
	default <ITEM2> Filter2<ITEM2> translate(Getter<? super ITEM2, ? extends ITEM> trans) throws NullPointerException {
		notNull(trans);
		return item -> this.accept(trans.get(item));
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Filter2<ITEM> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode liefert einen {@link Filter2}, der diesen {@link Filter2} mit dem gegebenen Synchronisationsobjekt {@code mutex} über
	 * {@code synchronized(mutex)} synchronisiert. Wenn letzteres {@code null} ist, wird der gelieferte {@link Filter2} verwendet. */
	default Filter2<ITEM> synchronize(Object mutex) {
		return new Filters.SynchronizedFilter<ITEM>(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link #conjoin(Filter2) conjoin(that)}. */
	default Filter2<ITEM> and(Filter2<? super ITEM> that) throws NullPointerException {
		return this.conjoin(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #negate()}. */
	default Filter2<ITEM> not() {
		return this.negate();
	}

	/** Diese Methode ist eine Abkürzung für {@link #disjoin(Filter2) disjoin(that)}. */
	default Filter2<ITEM> or(Filter2<? super ITEM> that) throws NullPointerException {
		return this.disjoin(that);
	}

	@Override
	default Filter2<ITEM> asFilter() {
		return this;
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #accept(Object) this::accept}. */
	default Getter3<ITEM, Boolean> toGetter() {
		return this::accept;
	}

}
