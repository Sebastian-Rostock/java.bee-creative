package bee.creative.util;

import static bee.creative.util.Comparators.comparatorFrom;
import java.util.Comparator;

/** Diese Schnittstelle definiert ein {@link Comparator} mit {@link Comparator3}-Schnittstelle.
 *
 * @author [cc-by] 2025 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Eingabe. */
public interface Comparator2<T> extends Comparator<T> {

	/** Diese Methode liefert die {@link Comparator3}-Schnittstelle zu {@link #compare(Object, Object)}. */
	default Comparator3<T> asComparator() {
		return comparatorFrom(this);
	}

}
