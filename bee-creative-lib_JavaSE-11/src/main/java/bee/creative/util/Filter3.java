package bee.creative.util;

import static bee.creative.util.Filters.conjoinedFilter;
import static bee.creative.util.Filters.disjoinedFilter;
import static bee.creative.util.Filters.negatedFilter;
import static bee.creative.util.Filters.synchronizedFilter;
import static bee.creative.util.Filters.translatedFilter;
import static bee.creative.util.Getters.RefMode.SOFT_REF;
import static bee.creative.util.Hashers.naturalHasher;
import bee.creative.util.Getters.RefMode;

/** Diese Schnittstelle definiert eine Filtermethode, die gegebene Datensätze über {@link Filter3#accepts(Object)} akzeptieren oder ablehnen kann.
 *
 * @see Filters
 * @author [cc-by] 2010 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Datensätze. */
public interface Filter3<T> extends Filter<T> {

	/** Diese Methode ist eine Abkürzung für {@link #buffer(RefMode, Hasher) this.buffer(SOFT_REF_MODE, naturalHasher())}. */
	default Filter3<T> buffer() {
		return this.buffer(SOFT_REF, naturalHasher());
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#bufferedGetter(Getter, RefMode, Hasher) this.asGetter().buffer(mode, hasher)::get}. */
	default Filter3<T> buffer(RefMode mode, Hasher hasher) throws IllegalArgumentException {
		return this.asGetter().buffer(mode, hasher)::get;
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#negatedFilter(Filter) negatedFilter(this)}. */
	default Filter3<T> negate() {
		return negatedFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#disjoinedFilter(Filter, Filter) disjoinedFilter(this, that)}. */
	default Filter3<T> disjoin(Filter<? super T> that) throws NullPointerException {
		return disjoinedFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#conjoinedFilter(Filter, Filter) conjoinedFilter(this, that)}. */
	default Filter3<T> conjoin(Filter<? super T> that) throws NullPointerException {
		return conjoinedFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translatedFilter(Filter, Getter) translatedFilter(this, trans)}. */
	default <T2> Filter3<T2> translate(Getter<? super T2, ? extends T> trans) throws NullPointerException {
		return translatedFilter(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link #synchronize(Object) this.synchronize(this)}. */
	default Filter3<T> synchronize() {
		return this.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronizedFilter(Filter, Object) synchronizedFilter(this, mutex)}. */
	default Filter3<T> synchronize(Object mutex) {
		return synchronizedFilter(this, mutex);
	}

	/** Diese Methode liefert den {@link Getter3} zu {@link #accepts(Object)}. */
	default Getter3<T, Boolean> asGetter() {
		return this::accepts;
	}

	/** Diese Methode ist eine Abkürzung für {@link #conjoin(Filter) conjoin(that)}. */
	default Filter3<T> and(Filter<? super T> that) throws NullPointerException {
		return this.conjoin(that);
	}

	/** Diese Methode ist eine Abkürzung für {@link #negate()}. */
	default Filter3<T> not() {
		return this.negate();
	}

	/** Diese Methode ist eine Abkürzung für {@link #disjoin(Filter) disjoin(that)}. */
	default Filter3<T> or(Filter<? super T> that) throws NullPointerException {
		return this.disjoin(that);
	}

}
