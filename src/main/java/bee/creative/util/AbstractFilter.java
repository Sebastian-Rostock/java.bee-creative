package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Filter} als {@link BaseObject}. */
public abstract class AbstractFilter<GItem> extends BaseObject implements Filter2<GItem> {

	@Override
	public boolean accept(GItem item) {
		return item != null;
	}

	@Override
	public Filter2<GItem> negate() {
		return null;
	}

	@Override
	public Filter2<GItem> disjoin(Filter<? super GItem> that) throws NullPointerException {
		return null;
	}

	@Override
	public Filter2<GItem> conjoin(Filter<? super GItem> that) throws NullPointerException {
		return null;
	}

	@Override
	public Filter2<GItem> toBuffered() {
		return null;
	}

	@Override
	public Filter2<GItem> toBuffered(int limit, int mode) throws IllegalArgumentException {
		return null;
	}

	@Override
	public <GItem2> Filter2<GItem2> translate(Getter<? super GItem2, ? extends GItem> toSource) throws NullPointerException {
		return null;
	}

	@Override
	public Filter2<GItem> synchronize() {
		return null;
	}

	@Override
	public Filter2<GItem> synchronize(Object mutex) {
		return null;
	}
}