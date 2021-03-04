package bee.creative.qs;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Menge von {@link #popAll() entfernbaren} Objekten mit Bezug zu einem {@link #owner()
 * Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public interface QXSet<GI, GISet> extends QOSet<GI, GISet> {

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Objekte aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde.
	 *
	 * @return {@code true} bei Änderung des Graphspeicherinhalts bzw. {@code false} sonst. */
	public boolean popAll();

	/** Diese Methode gibt eine Mengensicht auf die Objekte mit dem gegebenen Speicherzustand zurück.
	 *
	 * @param state {@code true} für die im {@link QS Graphspeicher} gespeicherten Objekte bzw. {@code false} für die temporären Objekte.
	 * @return Objekte mit dem gegebenen Speicherzustand. */
	public GISet havingState(boolean state);

}