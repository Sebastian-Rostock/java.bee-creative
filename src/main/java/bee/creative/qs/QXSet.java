package bee.creative.qs;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	 * @return {@code true}, wenn der {@link #iterator()} mindestend ein Objekt liefert und {@code false} sonst. */
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

	/** Diese Methode gibt den {@link Iterator} über die Objekte dieser Menge zurück. Die Methode {@link Iterator#remove()} wird nicht unterstützt.
	 *
	 * @return Iterator. */
	@Override
	public Iterator<GI> iterator();

	/** Diese Methode gibt eine Kopie dieser Menge als {@link Set} zurück.
	 * 
	 * @return Kopie dieser Menge. */
	public Set<GI> toSet();

	/** Diese Methode gibt eine aufsteigend geordnete Kopie dieser Menge als {@link List} zurück.
	 * 
	 * @return geordnete Kopie dieser Menge. */
	public List<GI> toList();

	/** Diese Methode gibt eine temporäre Kopie dieser Menge zurück.
	 * 
	 * @return Kopie dieser Menge. */
	public GISet toCopy();

}