package bee.creative.qs;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Menge von Objekten mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge. */
public interface QXSet<GI, GISet> extends QX, Iterable<GI> {

	/** Diese Methode gibt die Anzahl der in dieser Megne enthaltenen Objekte zurück.
	 * 
	 * @return Objektanzahl. */
	public long size();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Menge nicht leer ist.
	 *
	 * @return {@code true}, wenn {@link #size()} keine {@code 0} liefert und {@code false} sonst. */
	public boolean hasAny();

	/** Diese Methode fügt alle in dieser Menge enthaltenen Objekte in den {@link #owner() Graphspeicher} eine und gibt nur dann {@code true} zurück, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde.
	 * 
	 * @return {@code true} bei Änderung des Graphspeicherinhalts und {@code false} sonst. */
	public boolean putAll();

	/** Diese Methode entfernt alle in dieser Menge enthaltenen Objekte aus dem {@link #owner() Graphspeicher} und gibt nur dann {@code true} zurück, wenn dadurch
	 * der Inhalt des Graphspeichers verändert wurde.
	 * 
	 * @return {@code true} bei Änderung des Graphspeicherinhalts und {@code false} sonst. */
	public boolean popAll();

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die in dieser oder der gegebenen Menge enthalten sind.
	 * 
	 * @param set Objektmegne.
	 * @return Vereinigung dieser mit der gegebenen Objektmenge. */
	public GISet union(GISet set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die gleichzeitig in dieser und nicht in der gegebenen Menge enthalten sind.
	 * 
	 * @param set Objektmegne.
	 * @return Differenz dieser ohne der gegebenen Objektmenge. */
	public GISet except(GISet set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die gleichzeitig in dieser und in der gegebenen Menge enthalten sind.
	 * 
	 * @param set Objektmegne.
	 * @return Schitt dieser mit der gegebenen Objektmenge. */
	public GISet intersect(GISet set) throws NullPointerException, IllegalArgumentException;

}