package bee.creative.lang;

/** Diese Schnittstelle definiert eine Methode zur Ermittlung eines Elements zu einem gegebenen Index.
 *
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ der Elemente. */
public interface Array<T> {

	/** Diese Methode gibt das {@code index}-te Element zurück.
	 *
	 * @param index Index.
	 * @return {@code index}-tes Element.
	 * @throws IndexOutOfBoundsException Wenn der gegebene Index ungültig ist. */
	T get(int index) throws IndexOutOfBoundsException;

}