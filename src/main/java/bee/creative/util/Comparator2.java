package bee.creative.util;

import java.util.Comparator;

public interface Comparator2<GItem> extends Comparator<GItem> {

	public Comparator2<GItem> toDefault();

	public Comparator2<Iterable<? extends GItem>> toIterable();

	public Comparator2<GItem> toReverse();

	public <GItem2> Comparator<GItem2> toTranslated(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException;

}