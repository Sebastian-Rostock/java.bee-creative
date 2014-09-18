package bee.creative.iam;

import java.util.Iterator;

/**
 * Diese Schnittstelle definiert eine nur lesbare Liste von Elementen, bei welcher jedes Element über einen Index verwaltet und u.a. als {@link ArrayView}
 * gelesen werden kann.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ListView extends Iterable<ArrayView> {

	/**
	 * Diese Methode gibt das {@code itemIndex}-te Element als {@link ArrayView} zurück.
	 * 
	 * @see #item(int, int)
	 * @see #itemSize(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return {@code itemIndex}-tes Element.
	 * @throws IndexOutOfBoundsException Wenn die Eingabe ungültig ist.
	 */
	public ArrayView item(final int itemIndex) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt den {@code index}-ten {@code int} des {@code itemIndex}-ten Elements zurück.
	 * 
	 * @see #itemSize(int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @param index Index des {@code int}s.
	 * @return {@code index}-ter {@code int} des {@code itemIndex}-ten Elements.
	 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist.
	 */
	public int item(final int itemIndex, int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge des {@code itemIndex}-ten Elements zurück.
	 * 
	 * @see #item(int)
	 * @see #item(int, int)
	 * @see #itemCount()
	 * @param itemIndex Index des Elements.
	 * @return Länge des {@code itemIndex}-ten Elements.
	 * @throws IndexOutOfBoundsException Wenn die Eingabe ungültig ist.
	 */
	public int itemSize(int itemIndex) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Elemente zurück.
	 * 
	 * @see #item(int)
	 * @see #item(int, int)
	 * @return Anzahl der Elemente.
	 */
	public int itemCount();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #item(int)
	 * @see #itemCount()
	 */
	@Override
	public Iterator<ArrayView> iterator();

}
