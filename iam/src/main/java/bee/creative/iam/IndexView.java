package bee.creative.iam;

import java.util.List;

/**
 * Diese Schnittstelle definiert eine nur lesbare Sammlung beliebig vieler {@link MapView}s und {@link ListView}s.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface IndexView {

	/**
	 * Diese Methode gibt die {@code index}-te Abbildung das zurück.
	 * 
	 * @see #mapCount()
	 * @param index Index.
	 * @return {@code index}-te Abbildung.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	public MapView map(final int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Abbildungen zurück.
	 * 
	 * @see #map(int)
	 * @return Anzahl der Abbildungen.
	 */
	public int mapCount();

	/**
	 * Diese Methode gibt die Abbildungen als nur lesbare {@link List} zurück.
	 * 
	 * @see #map(int)
	 * @see #mapCount()
	 * @return Abbildungen.
	 */
	public List<MapView> maps();

	/**
	 * Diese Methode gibt die {@code index}-te Liste das zurück.
	 * 
	 * @see #listCount()
	 * @param index Index.
	 * @return {@code index}-te Liste.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	public ListView list(final int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Listen zurück.
	 * 
	 * @see #list(int)
	 * @return Anzahl der Listen.
	 */
	public int listCount();

	/**
	 * Diese Methode gibt die Listen als nur lesbare {@link List} zurück.
	 * 
	 * @return Listen.
	 */
	public List<ListView> lists();

}
