package bee.creative.util;

import static bee.creative.util.Filters.conjoinedFilter;
import static bee.creative.util.Filters.disjoinedFilter;
import static bee.creative.util.Filters.negatedFilter;
import static bee.creative.util.Filters.synchronizedFilter;
import static bee.creative.util.Filters.translatedFilter;
import static bee.creative.util.Getters.getterFrom;
import static bee.creative.util.Getters.BufferedGetter.SOFT_REF_MODE;
import static bee.creative.util.Hashers.naturalHasher;

/** Diese Schnittstelle ergänzt einen {@link Filter} insb. um eine Anbindung an Methoden von {@link Filters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Datensätze. */
public interface Filter2<ITEM> extends Filter<ITEM> {

	/** Diese Methode ist eine Abkürzung für {@link Filters#disjoinedFilter(Filter, Filter) disjoinedFilter(this, that)}. */
	default Filter2<ITEM> or(Filter<? super ITEM> that) throws NullPointerException {
		return disjoinedFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#conjoinedFilter(Filter, Filter) conjoinedFilter(this, that)}. */
	default Filter2<ITEM> and(Filter<? super ITEM> that) throws NullPointerException {
		return conjoinedFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#negatedFilter(Filter) negatedFilter(this)}. */
	default Filter2<ITEM> not() {
		return negatedFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#negatedFilter(Filter) negatedFilter(this)}. */
	default Filter2<ITEM> negate() {
		return negatedFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#disjoinedFilter(Filter, Filter) disjoinedFilter(this, that)}. */
	default Filter2<ITEM> disjoin(Filter<? super ITEM> that) throws NullPointerException {
		return disjoinedFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#conjoinedFilter(Filter, Filter) conjoinedFilter(this, that)}. */
	default Filter2<ITEM> conjoin(Filter<? super ITEM> that) throws NullPointerException {
		return conjoinedFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #buffer(int, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Filter2<ITEM> buffer() {
		return this.buffer(SOFT_REF_MODE, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, int, Hasher) this.toGetter().buffer(mode, hasher)::get}. */
	default Filter2<ITEM> buffer(int mode, Hasher hasher) throws IllegalArgumentException {
		return this.toGetter().buffer(mode, hasher)::get;
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translatedFilter(Filter, Getter) translatedFilter(this, trans)}. */
	default <GItem2> Filter2<GItem2> translate(Getter<? super GItem2, ? extends ITEM> trans) throws NullPointerException {
		return translatedFilter(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Filter2<ITEM> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronizedFilter(Filter, Object) synchronizedFilter(this, mutex)}. */
	default Filter2<ITEM> synchronize(Object mutex) {
		return synchronizedFilter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#getterFrom(Getter) getterFrom(this::accept)}. */
	default Getter3<ITEM, Boolean> toGetter() {
		return getterFrom(this::accept);
	}

}
