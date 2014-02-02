package bee.creative.xml.view;

import java.util.Iterator;
import bee.creative.util.Comparables.Get;

/**
 * Diese Schnittstelle definiert eine iterierbare Auflistung von Elementen.
 * 
 * @author [cc-by] 2013 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ der Elemente.
 */
public interface ListView<GItem> extends Get<GItem>, Iterable<GItem> {

	/**
	 * {@inheritDoc} Wenn der gegebene Index größer als oder gleich der {@link #size() Anzahl der Elemente} ist, wird {@code null} zurück gegeben.
	 */
	@Override
	public GItem get(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Elemente zurück.
	 * 
	 * @return Anzahl der Elemente.
	 */
	public int size();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GItem> iterator();

}