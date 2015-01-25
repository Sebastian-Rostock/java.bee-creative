package bee.creative.iam;

import java.util.List;

/**
 * Diese Schnittstelle definiert eine Zusammenstellung beliebig vieler Listen ({@link IAMList}) und Abbildungen ({@link IAMMap}).
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface IAMIndex {

	/**
	 * Diese Methode gibt die {@code index}-te Abbildung zurück. Bei einem ungültigen {@code index} wird eine leere Abbildung geliefert.
	 * 
	 * @see #mapCount()
	 * @param index Index.
	 * @return {@code index}-te Abbildung.
	 */
	public IAMMap map(final int index);

	/**
	 * Diese Methode gibt die Anzahl der Abbildungen zurück ({@code 0..1073741823}).
	 * 
	 * @see #map(int)
	 * @return Anzahl der Abbildungen.
	 */
	public int mapCount();

	/**
	 * Diese Methode gibt eine {@link List}-Sicht auf die Abbildungen zurück.
	 * 
	 * @see #map(int)
	 * @see #mapCount()
	 * @return Abbildungen.
	 */
	public List<IAMMap> maps();

	/**
	 * Diese Methode gibt die {@code index}-te Liste zurück. Bei einem ungültigen {@code index} wird eine leere Liste geliefert.
	 * 
	 * @see #listCount()
	 * @param index Index.
	 * @return {@code index}-te Liste.
	 */
	public IAMList list(final int index);

	/**
	 * Diese Methode gibt die Anzahl der Listen zurück.
	 * 
	 * @see #list(int)
	 * @return Anzahl der Listen.
	 */
	public int listCount();

	/**
	 * Diese Methode gibt {@link List}-Sicht auf die Listen zurück.
	 * 
	 * @see #list(int)
	 * @see #listCount()
	 * @return Listen.
	 */
	public List<IAMList> lists();

}
