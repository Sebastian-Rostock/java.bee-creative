package bee.creative.util;

public interface Filter2<GItem> extends Filter<GItem> {

	public Filter2<GItem> negate();

	public Filter2<GItem> disjoin(final Filter<? super GItem> that) throws NullPointerException;

	public Filter2<GItem> conjoin(final Filter<? super GItem> that) throws NullPointerException;

	public Filter2<GItem> toBuffered();

	public Filter2<GItem> toBuffered(final int limit, final int mode) throws IllegalArgumentException;

	public <GItem2> Filter2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> toSource) throws NullPointerException;

	public Filter2<GItem> synchronize();

	public Filter2<GItem> synchronize(final Object mutex);

}
