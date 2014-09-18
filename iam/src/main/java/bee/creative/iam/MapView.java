package bee.creative.iam;

import java.util.Iterator;

/**
 * Diese Schnittstelle definiert eine nur lesbare Abbildung von Schlüsseln auf Werte, bei welcher jeder Schlüssel und jeder Wert über einen Index verwaltet und
 * u.a. als {@link ArrayView} gelesen werden kann. Die Einträge der Abbildung können dazu auch als {@link EntryView} glesen werden.
 * 
 * @author [cc-by] 2014 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface MapView extends Iterable<EntryView> {

	/**
	 * Diese Methode gibt den Schlüssel des {@code entryIndex}-ten Eintrags als {@link ArrayView} zurück.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Schlüssel des {@code entryIndex}-ten Eintrags.
	 * @throws IndexOutOfBoundsException Wenn die Eingabe ungültig ist.
	 */
	public ArrayView key(final int entryIndex) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt den {@code index}-ten {@code int} des Schlüssels des {@code entryIndex}-ten Eintrags zurück.
	 * 
	 * @see #keySize()
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @param index Index des {@code int}s.
	 * @return {@code index}-ter {@code int} des Schlüssels des {@code entryIndex}-ten Eintrags.
	 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist.
	 */
	public int key(final int entryIndex, int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge eines Schlüssels zurück.
	 * 
	 * @return Länge eines Schlüssel.
	 */
	public int keySize();

	/**
	 * Diese Methode gibt den Wert des {@code entryIndex}-ten Eintrags als {@link ArrayView} zurück.
	 * 
	 * @param entryIndex Index des Eintrags.
	 * @return Wert des {@code entryIndex}-ten Eintrags.
	 * @throws IndexOutOfBoundsException Wenn die Eingabe ungültig ist.
	 */
	public ArrayView value(final int entryIndex) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt den {@code index}-ten {@code int} des Werts des {@code entryIndex}-ten Eintrags zurück.
	 * 
	 * @see #valueSize()
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @param index Index des {@code int}s.
	 * @return {@code index}-ter {@code int} des Werts des {@code entryIndex}-ten Eintrags.
	 * @throws IndexOutOfBoundsException Wenn eine der Eingaben ungültig ist.
	 */
	public int value(final int entryIndex, int index) throws IndexOutOfBoundsException;

	/**
	 * Diese Methode gibt die Länge eines Werts zurück.
	 * 
	 * @return Länge eines Werts.
	 */
	public int valueSize();

	/**
	 * Diese Methode gibt den {@code itemIndex}-ten Eintrag als {@link EntryView} zurück.
	 * 
	 * @see #entryCount()
	 * @param entryIndex Index des Eintrags.
	 * @return {@code entryIndex}-ter Eintrags.
	 * @throws IndexOutOfBoundsException Wenn die Eingabe ungültig ist.
	 */
	public EntryView entry(int entryIndex);

	/**
	 * Diese Methode gibt die Anzahl der Einträge zurück.
	 * 
	 * @return Anzahl der Einträge.
	 */
	public int entryCount();

	/**
	 * {@inheritDoc}
	 * 
	 * @see #entry(int)
	 * @see #entryCount()
	 */
	@Override
	public Iterator<EntryView> iterator();

	/**
	 * Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag gefunden wurde ist der Rückgabewert {@code -1}.
	 * 
	 * @param key Schlüssel.
	 * @return Index des Entrags oder {@code -1}.
	 */
	public int find(final int key);

	/**
	 * Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag gefunden wurde ist der Rückgabewert {@code -1}.
	 * 
	 * @param key1 erster {@code int} des Schlüssels.
	 * @param key2 zweiter {@code int} des Schlüssels.
	 * @return Index des Entrags oder {@code -1}.
	 */
	public int find(final int key1, final int key2);

	/**
	 * Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag gefunden wurde ist der Rückgabewert {@code -1}.
	 * 
	 * @param key1 erster {@code int} des Schlüssels.
	 * @param key2 zweiter {@code int} des Schlüssels.
	 * @param key3 dritter {@code int} des Schlüssels.
	 * @return Index des Entrags oder {@code -1}.
	 */
	public int find(final int key1, final int key2, final int key3);

	/**
	 * Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag gefunden wurde ist der Rückgabewert {@code -1}.
	 * 
	 * @param key1 erster {@code int} des Schlüssels.
	 * @param key2 zweiter {@code int} des Schlüssels.
	 * @param key3 dritter {@code int} des Schlüssels.
	 * @param key4 vierter {@code int} des Schlüssels.
	 * @return Index des Entrags oder {@code -1}.
	 */
	public int find(final int key1, final int key2, final int key3, final int key4);

	/**
	 * Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag gefunden wurde ist der Rückgabewert {@code -1}.
	 * 
	 * @param key1 erster {@code int} des Schlüssels.
	 * @param key2 zweiter {@code int} des Schlüssels.
	 * @param key3 dritter {@code int} des Schlüssels.
	 * @param key4 vierter {@code int} des Schlüssels.
	 * @param key5 fünfter {@code int} des Schlüssels.
	 * @return Index des Entrags oder {@code -1}.
	 */
	public int find(final int key1, final int key2, final int key3, final int key4, final int key5);

	/**
	 * Diese Methode gibt den Index des Eintrags mit dem gegebenen Schlüssel zurück. Wenn kein solcher Eintrag gefunden wurde ist der Rückgabewert {@code -1}.
	 * 
	 * @param key Schlüssel.
	 * @return Index des Entrags oder {@code -1}.
	 */
	public int find(final int... key);

}
