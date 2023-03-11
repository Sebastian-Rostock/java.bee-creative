package bee.creative.lang;

/** Diese Schnittstelle definiert ein {@link #iterator() iterierbares} und nur lesbares Array mit {@link #size() Längenangabe}.
 *
 * @param <GItem> Typ der Elemente. */
public interface Array2<GItem> extends Array<GItem>, Iterable<GItem> {

	/** Diese Methode gibt die Anzahl der Elemente zurück.
	 *
	 * @return Elementanzahl. */
	public int size();

}