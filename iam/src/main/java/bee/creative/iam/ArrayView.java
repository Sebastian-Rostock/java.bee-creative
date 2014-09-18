package bee.creative.iam;

/**
 * Diese Schnittstelle definiert eine nur lesende Sicht auf ein {@code int}-Array.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface ArrayView extends Iterable<Integer> {

	/**
	 * Diese Methode gibt den {@code index}-ten {@code int} zurück.
	 * 
	 * @see #length()
	 * @param index Index.
	 * @return {@code index}-ter {@code int}.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	public int get(final int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge des Arrays zurück.
	 * 
	 * @see #get(int)
	 * @return Länge des Arrays.
	 */
	public int length();

	/**
	 * Diese Methode gibt eine Sicht auf die {@code int}s ab der gegebenen Position als {@link ArrayView} zurück.
	 * 
	 * @param offset Position.
	 * @return {@code int}s ab der gegebenen Position.
	 * @throws IndexOutOfBoundsException Wenn die Eingabe ungültig ist.
	 */
	public ArrayView section(final int offset) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt eine Sicht auf die gegebene Anzahl an {@code int}s ab der gegebenen Position als {@link ArrayView} zurück.
	 * 
	 * @param offset Position.
	 * @param length Anzahl.
	 * @return {@code int}s ab der gegebenen Position.
	 * @throws IndexOutOfBoundsException Wenn der eine der Eingaben ungültig ist.
	 */
	public ArrayView section(final int offset, int length) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt eine Kopie dieses Arrays als pritives {@code int[]} zurück.
	 * 
	 * @see #get(int)
	 * @see #length()
	 * @see #iterator()
	 * @return Kopie.
	 */
	public int[] toArray();

}
