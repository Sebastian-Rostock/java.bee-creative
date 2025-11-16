package bee.creative.qs;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Menge von {@link #popAll() entfernbaren} Elementen mit Bezug zu einem {@link #owner()
 * Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente.
 * @param <T> Typ dieser Menge. */
public interface QXSet<E, T> extends QOSet<E, T> {

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Elemente aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn
	 * dadurch der Inhalt des Graphspeichers verändert wurde.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	boolean popAll();

	/** Diese Methode gibt eine Mengensicht auf die Elemente mit dem gegebenen Speicherzustand zurück.
	 *
	 * @param state {@code true} für die im {@link QS Graphspeicher} gespeicherten Objekte bzw. {@code false} für die temporären Objekte.
	 * @return Objekte mit dem gegebenen Speicherzustand. */
	T havingState(boolean state);

}