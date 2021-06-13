package bee.creative.util;

import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert ein abstraktes {@link Comparable2} als {@link BaseObject}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe. */
public abstract class AbstractComparable<GItem> extends BaseObject implements Comparable2<GItem> {

	@Override
	public int compareTo(final GItem item) {
		return 0;
	}

	@Override
	public Comparable2<GItem> concat(final Comparable<? super GItem> that) throws NullPointerException {
		return Comparables.concat(this, that);
	}

	@Override
	public Comparable2<GItem> optionalize() {
		return Comparables.optionalize(this);
	}

	@Override
	public Comparable2<GItem> reverse() {
		return Comparables.reverse(this);
	}

	@Override
	public <GItem2> Comparable2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Comparables.translate(this, trans);
	}

}