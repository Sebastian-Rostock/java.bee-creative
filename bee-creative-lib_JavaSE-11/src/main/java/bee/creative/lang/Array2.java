package bee.creative.lang;

import static bee.creative.util.Iterators.iteratorFromArray;
import bee.creative.util.Iterable2;
import bee.creative.util.Iterator3;

/** Diese Schnittstelle definiert ein {@link #iterator() iterierbares} und nur lesbares Array mit {@link #size() Längenangabe}.
 *
 * @param <T> Typ der Elemente.
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Array2<T> extends Array<T>, Iterable2<T> {

	/** Diese Methode gibt die Anzahl der Elemente zurück.
	 *
	 * @return Elementanzahl. */
	int size();

	@Override
	default Iterator3<T> iterator() {
		return iteratorFromArray(this, 0, this.size());
	}

}