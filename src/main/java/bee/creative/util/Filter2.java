package bee.creative.util;

/** Diese Schnittstelle definiert .
 * 
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public interface Filter2<GItem> extends Filter<GItem> {

	public Filter2<GItem> negate();

	public Filter2<GItem> disjoin(final Filter<? super GItem> that) throws NullPointerException;

	public Filter2<GItem> conjoin(final Filter<? super GItem> that) throws NullPointerException;

	public Filter2<GItem> buffer();

	public Filter2<GItem> buffer(int mode, Hasher hasher) throws IllegalArgumentException;

	public <GItem2> Filter2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException;

	public Filter2<GItem> synchronize();

	public Filter2<GItem> synchronize(final Object mutex);

	public Getter3<GItem, Boolean> toGetter();

}
