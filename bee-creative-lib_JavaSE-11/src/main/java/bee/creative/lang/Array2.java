package bee.creative.lang;

import bee.creative.util.Iterator2;
import bee.creative.util.Iterators;

/** Diese Schnittstelle definiert ein {@link #iterator() iterierbares} und nur lesbares Array mit {@link #size() Längenangabe}.
 *
 * @param <GItem> Typ der Elemente.
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Array2<GItem> extends Array<GItem>, Iterable<GItem> {

	/** Diese Methode gibt die Anzahl der Elemente zurück.
	 *
	 * @return Elementanzahl. */
	int size();

	@Override
	default Iterator2<GItem> iterator() {
		return Iterators.fromArray(this, 0, this.size());
	}

}