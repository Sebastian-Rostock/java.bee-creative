package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Filter} insb. um eine Anbindung an Methoden von {@link Filters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public interface Filter2<GItem> extends Filter<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Filters#negate(Filter) Filters.negate(this)}. */
	default Filter2<GItem> negate() {
		return Filters.negate(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#disjoin(Filter, Filter) Filters.disjoin(this, that)}. */
	default Filter2<GItem> disjoin(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.disjoin(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#conjoin(Filter, Filter) Filters.conjoin(this, that)}. */
	default Filter2<GItem> conjoin(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.conjoin(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#buffer(Filter) Filters.buffer(this)}. */
	default Filter2<GItem> buffer() {
		return Filters.buffer(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#buffer(Filter) Filters.buffer(this, mode, hasher)}. */
	default Filter2<GItem> buffer(final int mode, final Hasher hasher) throws IllegalArgumentException {
		return Filters.buffer(this, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translate(Filter, Getter) Filters.translate(this, trans)}. */
	default <GItem2> Filter2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Filters.translate(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronize(Filter) Filters.synchronize(this)}. */
	default Filter2<GItem> synchronize() {
		return Filters.synchronize(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronize(Filter, Object) Filters.synchronize(this, mutex)}. */
	default Filter2<GItem> synchronize(final Object mutex) {
		return Filters.synchronize(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromFilter(Filter) Getters.fromFilter(this)}. */
	default Getter3<GItem, Boolean> toGetter() {
		return Getters.fromFilter(this);
	}

}
