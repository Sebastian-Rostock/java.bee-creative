package bee.creative.util;

import java.util.Comparator;
import bee.creative.lang.Objects.BaseObject;

/** Diese Klasse implementiert einen abstrakten {@link Comparator2} als {@link BaseObject}.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Eingabe. */
public abstract class AbstractComparator<GItem> extends BaseObject implements Comparator2<GItem> {

	@Override
	public int compare(final GItem o1, final GItem o2) {
		return 0;
	}

	@Override
	public Comparator2<GItem> concat(final Comparator<? super GItem> that) throws NullPointerException {
		return Comparators.concat(this, that);
	}

	@Override
	public Comparator2<Iterable<? extends GItem>> iterable() {
		return Comparators.iterable(this);
	}

	@Override
	public Comparator2<GItem> optionalize() {
		return Comparators.optionalize(this);
	}

	@Override
	public Comparator2<GItem> reverse() {
		return Comparators.reverse(this);
	}

	@Override
	public <GItem2> Comparator2<GItem2> translate(final Getter<? super GItem2, ? extends GItem> trans) throws NullPointerException {
		return Comparators.translate(this, trans);
	}

}