package bee.creative.iam;

/**
 * Diese Schnittstelle definiert einen Eintrag einer Abbildung ({@link IAMMap}) und besteht aus einem Schlüssel und einem Wert, welche selbst Zahlenfolgen (
 * {@link IAMArray}) sind.
 * 
 * @see IAMMap#entry(int)
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface IAMEntry {

	/**
	 * Diese Methode gibt den Schlüssel als Zahlenfolge zurück.
	 * 
	 * @see IAMMap#key(int)
	 * @return Schlüssel.
	 */
	public IAMArray key();

	/**
	 * Diese Methode gibt die {@code index}-te Zahl des Schlüssels zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 * 
	 * @see IAMMap#key(int, int)
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Schlüssels.
	 */
	public int key(int index);

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge des Schlüssels zurück ({@code 0..1073741823}).
	 * 
	 * @see IAMMap#keyLength(int)
	 * @return Größe der Schlüssel.
	 */
	public int keyLength();

	/**
	 * Diese Methode gibt den Wert als Zahlenfolge zurück.
	 * 
	 * @see IAMMap#value(int)
	 * @return Wert.
	 */
	public IAMArray value();

	/**
	 * Diese Methode gibt die {@code index}-te Zahl des Werts zurück. Bei einem ungültigen {@code index} wird {@code 0} geliefert.
	 * 
	 * @see IAMMap#value(int, int)
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Werts.
	 */
	public int value(int index);

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge des Werts zurück ({@code 0..1073741823}).
	 * 
	 * @see IAMMap#valueLength(int)
	 * @return Größe der Werte.
	 */
	public int valueLength();

}