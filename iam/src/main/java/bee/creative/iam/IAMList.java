package bee.creative.iam;

import java.util.List;

/**
 * Diese Schnittstelle definiert eine geordnete Liste von Elementen, welche selbst Zahlenfolgen ({@link IAMArray}) sind.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface IAMList {

	/**
	 * Diese Methode gibt das {@code itemIndex}-te Element als Zahlenfolge zurück. Bei einem ungültigen {@code itemIndex} wird eine leere Zahlenfolge geliefert.
	 * 
	 * @see #item(int, int)
	 * @see #itemLength(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return {@code itemIndex}-tes Element.
	 */
	public IAMArray item(final int itemIndex);

	/**
	 * Diese Methode gibt die {@code index}-te Zahl des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code index} bzw. {@code itemIndex} wird
	 * {@code 0} geliefert.
	 * 
	 * @see #itemLength(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des {@code itemIndex}-ten Elements.
	 */
	public int item(final int itemIndex, int index);

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge des {@code itemIndex}-ten Elements zurück. Bei einem ungültigen {@code itemIndex} wird {@code 0} geliefert.
	 * 
	 * @see #item(int)
	 * @see #item(int, int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return Länge des {@code itemIndex}-ten Elements.
	 */
	public int itemLength(int itemIndex);

	/**
	 * Diese Methode gibt die Anzahl der Elemente zurück ({@code 0..1073741823}).
	 * 
	 * @see #item(int)
	 * @see #item(int, int)
	 * @return Anzahl der Elemente.
	 */
	public int itemCount();

	/**
	 * Diese Methode gibt {@link List}-Sicht auf die Elemente zurück.
	 * 
	 * @see #item(int)
	 * @see #itemCount()
	 * @return Elemente.
	 */
	public List<IAMArray> items();

}
