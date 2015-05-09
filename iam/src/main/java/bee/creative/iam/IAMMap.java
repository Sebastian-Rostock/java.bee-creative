package bee.creative.iam;

import java.util.List;

/**
 * Diese Schnittstelle definiert eine Abbildung von Schlüsseln auf Werte, welche beide selbst Zahlenfolgen ({@link IAMArray}) sind.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface IAMMap {

	/**
	 * Diese Methode gibt den Schlüssel des {@code entryIndex}-ten Eintrags als Zahlenfolge zurück. Bei einem ungültigen {@code entryIndex} wird eine leere
	 * Zahlenfolge geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Schlüssel des {@code entryIndex}-ten Eintrags.
	 */
	public IAMArray key(final int entryIndex);

	/**
	 * Diese Methode gibt die {@code index}-te Zahl des Schlüssels des {@code entryIndex}-ten Eintrags zurück. Bei einem ungültigen {@code index} bzw.
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @see #keyLength(int)
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Schlüssels des {@code entryIndex}-ten Eintrags.
	 */
	public int key(final int entryIndex, int index);

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge des Schlüssels des {@code entryIndex}-ten Eintrags zurück ({@code 0..1073741823}). Bei einem ungültigen
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Länge eines Schlüssel.
	 */
	public int keyLength(final int entryIndex);

	/**
	 * Diese Methode gibt den Wert des {@code entryIndex}-ten Eintrags als Zahlenfolge zurück. Bei einem ungültigen {@code entryIndex} wird eine leere Zahlenfolge
	 * geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Wert des {@code entryIndex}-ten Eintrags.
	 */
	public IAMArray value(final int entryIndex);

	/**
	 * Diese Methode gibt die {@code index}-te Zahl des Werts des {@code entryIndex}-ten Eintrags zurück. Bei einem ungültigen {@code index} bzw.
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @see #valueLength(int)
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @param index Index der Zahl.
	 * @return {@code index}-te Zahl des Werts des {@code entryIndex}-ten Eintrags.
	 */
	public int value(final int entryIndex, final int index);

	/**
	 * Diese Methode gibt die Länge der Zahlenfolge des Werts des {@code entryIndex}-ten Eintrags zurück ({@code 0..1073741823}). Bei einem ungültigen
	 * {@code entryIndex} wird {@code 0} geliefert.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Länge eines Werts.
	 */
	public int valueLength(final int entryIndex);

	/**
	 * Diese Methode gibt den {@code entryIndex}-ten Eintrag zurück. Bei einem ungültigen {@code entryIndex} wird ein leerer Eintrag geliefert.
	 * 
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @return {@code entryIndex}-ter Eintrag.
	 */
	public IAMEntry entry(final int entryIndex);

	/**
	 * Diese Methode gibt die Anzahl der Einträge zurück ({@code 0..1073741823}).
	 * 
	 * @return Anzahl der Einträge.
	 */
	public int entryCount();

	/**
	 * Diese Methode gibt {@link List}-Sicht auf die Einträge zurück.
	 * 
	 * @see #entry(int)
	 * @see #entryCount()
	 * @return Einträge.
	 */
	public List<IAMEntry> entries();

	/**
	 * Diese Methode gibt den Index des Eintrags zurück, dessen Schlüssel äquivalenten zum gegebenen Schlüssel ist. Bei erfolgloser Suche wird {@code -1}
	 * geliefert.
	 * 
	 * @param key Schlüssel.
	 * @return Index des Entrags.
	 * @throws NullPointerException Wenn {@code key} {@code null} ist.
	 */
	public int find(final int[] key) throws NullPointerException;

}
