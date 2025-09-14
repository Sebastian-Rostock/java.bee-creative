package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Filter} insb. um eine Anbindung an Methoden von {@link Filters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public interface Filter2<GItem> extends Filter<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Filters#negateFilter(Filter) Filters.negate(this)}. */
	default Filter2<GItem> negate() {
		return Filters.negateFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#disjoinFilter(Filter, Filter) Filters.disjoin(this, that)}. */
	default Filter2<GItem> disjoin(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.disjoinFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#conjoinFilter(Filter, Filter) Filters.conjoin(this, that)}. */
	default Filter2<GItem> conjoin(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.conjoinFilter(this, that);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#bufferFilter(Filter) Filters.buffer(this)}. */
	default Filter2<GItem> buffer() {
		return Filters.bufferFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#bufferFilter(Filter) Filters.buffer(this, mode, hasher)}. */
	default Filter2<GItem> buffer(final int mode, final Hasher hasher) throws IllegalArgumentException {
		return Filters.bufferFilter(this, mode, hasher);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#translateFilter(Filter, Getter) Filters.translate(this, trans)}. */
	default <GItem2> Filter2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Filters.translateFilter(this, trans);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronizeFilter(Filter) Filters.synchronize(this)}. */
	default Filter2<GItem> synchronize() {
		return Filters.synchronizeFilter(this);
	}

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronizeFilter(Filter, Object) Filters.synchronize(this, mutex)}. */
	default Filter2<GItem> synchronize(final Object mutex) {
		return Filters.synchronizeFilter(this, mutex);
	}

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromFilter(Filter) Getters.fromFilter(this)}. */
	default Getter3<GItem, Boolean> toGetter() {
		return Getters.fromFilter(this);
	}

}
