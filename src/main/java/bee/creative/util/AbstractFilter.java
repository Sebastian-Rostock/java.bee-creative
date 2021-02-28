package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Filter2} als {@link BaseObject}.
 *
 * @author [cc-by] 2021 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Datensätze. */
public abstract class AbstractFilter<GItem> extends BaseObject implements Filter2<GItem> {

	@Override
	public boolean accept(final GItem item) {
		return item != null;
	}

	@Override
	public Filter2<GItem> negate() {
		return Filters.negate(this);
	}

	@Override
	public Filter2<GItem> disjoin(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.disjoin(this, that);
	}

	@Override
	public Filter2<GItem> conjoin(final Filter<? super GItem> that) throws NullPointerException {
		return Filters.conjoin(this, that);
	}

	@Override
	public Filter2<GItem> buffer() {
		return Filters.buffer(this);
	}

	@Override
	public Filter2<GItem> buffer(final int mode, Hasher hasher) throws IllegalArgumentException {
		return Filters.buffer(null, mode, hasher);
	}

	@Override
	public <GItem2> Filter2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Filters.translate(this, trans);
	}

	@Override
	public Filter2<GItem> synchronize() {
		return Filters.synchronize(this);
	}

	@Override
	public Filter2<GItem> synchronize(final Object mutex) {
		return Filters.synchronize(this, mutex);
	}

	@Override
	public Getter3<GItem, Boolean> toGetter() {
		return Getters.fromFilter(this);
	}

}