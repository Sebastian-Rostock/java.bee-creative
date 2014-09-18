package bee.creative.iam;

/**
 * Diese Schnittstelle definiert eine nur lesbare Sicht auf den Eintrag einer Abbildung.
 * 
 * @see MapView#entry(int)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface EntryView {

	/**
	 * Diese Methode gibt den Schlüssel als {@link ArrayView} zurück.
	 * 
	 * @see MapView#key(int)
	 * @return Schlüssel.
	 */
	public ArrayView key();

	/**
	 * Diese Methode gibt den {@code index}-ten {@code int} des Schlüssels zurück.
	 * 
	 * @see MapView#key(int, int)
	 * @see #keySize()
	 * @param index Index des {@code int}s.
	 * @return {@code index}-ter {@code int} des Schlüssels.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	public int key(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Zahlen in einem Schlüssel zurück.
	 * 
	 * @return Größe der Schlüssel.
	 */
	public int keySize();

	/**
	 * Diese Methode gibt den Wert als {@link ArrayView} zurück.
	 * 
	 * @see MapView#value(int)
	 * @return Wert.
	 */
	public ArrayView value();

	/**
	 * Diese Methode gibt den {@code index}-ten {@code int} des Werts zurück.
	 * 
	 * @see MapView#value(int, int)
	 * @see #valueSize()
	 * @param index Index des {@code int}s.
	 * @return {@code index}-ter {@code int} des Werts.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist.
	 */
	public int value(int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Anzahl der Zahlen in einem Wert zurück.
	 * 
	 * @return Größe der Werte.
	 */
	public int valueSize();

}