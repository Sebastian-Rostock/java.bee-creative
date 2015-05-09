package bee.creative.iam;

/**
 * Diese Schnittstelle definiert eine Zahlenfolge, welche in einer Liste ({@link IAMList}) für die Elemente sowie einer Abbildung ({@link IAMMap}) für die
 * Schlüssel und Werte der Einträge ({@code IAMEntry}) verwendet wird.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface IAMArray extends Iterable<Integer>, Comparable<IAMArray> {

	/**
	 * Diese Methode gibt die {@code index}-te Zahl zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 * 
	 * @see #length()
	 * @param index Index.
	 * @return {@code index}-te Zahl.
	 */
	public int get(final int index);

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge zurück ({@code 0..1073741823}).
	 * 
	 * @see #get(int)
	 * @return Länge.
	 */
	public int length();

	/**
	 * Diese Methode gibt den Streuwert zurück.
	 * 
	 * <pre>
	 * int result = 0x811C9DC5;
	 * for (int i = 0; i < length(); i++)
	 *   result = (result * 0x01000193) ^ get(i);
	 * return result;
	 * </pre>
	 * 
	 * @return Streuwert.
	 */
	public int hash();

	/**
	 * Diese Methode gibt nur dann true zurück, wenn diese Zahlenfolge gleich der gegebenen Zahlenfolge ist.
	 * 
	 * <pre>
	 * if (length() != value.length()) return false;
	 * for (int i = 0; i < length(); i++)
	 *   if (get(i) != value.get(i)) return false;
	 * return true;
	 * </pre>
	 * 
	 * @param value Zahlenfolge.
	 * @return Vergleichswert.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public boolean equals(IAMArray value) throws NullPointerException;

	/**
	 * Diese Methode gibt eine Zahl kleiner, gleich oder größer als {@code 0} zurück, wenn die Ordnung dieser Zahlenfolge lexikografisch kleiner, gleich bzw.
	 * größer als die der gegebenen Zahlenfolge ist.
	 * 
	 * <pre>
	 * for (int i = 0, result; i < min(length(), value.length()); i++)
	 *   if ((result = get(i) – value.get(i)) != 0) return result;
	 * return length() – value.length();
	 * </pre>
	 * 
	 * @param value Zahlenfolge.
	 * @return Vergleichswert der Ordnungen.
	 * @throws NullPointerException Wenn {@code value} {@code null} ist.
	 */
	public int compare(IAMArray value) throws NullPointerException;

	/**
	 * Diese Methode gibt einen Abschnitt dieser Zahlenfolge ab der gegebenen Position und mit der gegebenen Länge zurück. Wenn der Abschnitt nicht innerhalb der
	 * Zahlenfolge liegt oder die Länge kleiner als {@code 1} ist, wird eine leere Zahlenfolge geliefert.
	 * 
	 * @param offset Beginn des Abschnitts.
	 * @param length Länge des Abschnitts.
	 * @return Abschnitt.
	 */
	public IAMArray section(final int offset, int length);

	/**
	 * Diese Methode gibt eine Kopie der Zahlenfolge als {@code int[]} zurück.
	 * 
	 * @see #get(int)
	 * @see #length()
	 * @return Kopie der Zahlenfolge.
	 */
	public int[] toArray();

}
