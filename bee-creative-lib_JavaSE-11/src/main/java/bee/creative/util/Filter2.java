package bee.creative.util;

/** Diese Schnittstelle ergänzt einen {@link Filter} insb. um eine Anbindung an Methoden von {@link Filters}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public interface Filter2<GItem> extends Filter<GItem> {

	/** Diese Methode ist eine Abkürzung für {@link Filters#buffer(Filter) Filters.buffer(this)}. */
	public Filter2<GItem> buffer();

	/** Diese Methode ist eine Abkürzung für {@link Filters#buffer(Filter) Filters.buffer(this, mode, hasher)}. */
	public Filter2<GItem> buffer(int mode, Hasher hasher) throws IllegalArgumentException;

	/** Diese Methode ist eine Abkürzung für {@link Filters#conjoin(Filter, Filter) Filters.conjoin(this, that)}. */
	public Filter2<GItem> conjoin(Filter<? super GItem> that) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Filters#disjoin(Filter, Filter) Filters.disjoin(this, that)}. */
	public Filter2<GItem> disjoin(Filter<? super GItem> that) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Filters#negate(Filter) Filters.negate(this)}. */
	public Filter2<GItem> negate();

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronize(Filter) Filters.synchronize(this)}. */
	public Filter2<GItem> synchronize();

	/** Diese Methode ist eine Abkürzung für {@link Filters#synchronize(Filter, Object) Filters.synchronize(this, mutex)}. */
	public Filter2<GItem> synchronize(Object mutex);

	/** Diese Methode ist eine Abkürzung für {@link Filters#translate(Filter, Getter) Filters.translate(this, trans)}. */
	public <GItem2> Filter2<GItem2> translate(Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException;

	/** Diese Methode ist eine Abkürzung für {@link Getters#fromFilter(Filter) Getters.fromFilter(this)}. */
	public Getter3<GItem, Boolean> toGetter();

}
