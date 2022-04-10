package bee.creative.qs;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.util.Filter;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Menge von Objekten mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public interface QOSet<GI, GISet> extends QO, Iterable<GI> {

	/** Diese Methode gibt die Anzahl der in dieser Menge enthaltenen Objekte zurück.
	 *
	 * @return Objektanzahl. */
	public long size();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Menge leer ist.
	 *
	 * @return {@code true}, nur wenn der {@link #iterator()} kein Objekt liefert. */
	public boolean isEmpty();

	/** Diese Methode gibt das erste Objekt in dieser Menge oder {@code null} zurück.
	 *
	 * @return erstes Objekt oder {@code null}. */
	public GI first();

	/** Diese Methode gibt eine temporäre Kopie dieser Menge zurück.
	 *
	 * @return Kopie dieser Menge. */
	public GISet copy();

	/** Diese Methode gibt die geordnete Mengensicht auf die Objekte dieser Menge zurück.
	 *
	 * @return Geordnete Menge. */
	public GISet order();

	/** Diese Methode gibt eine temporäre Kopie der Menge der Objekte zurück, die vom gegebenen Filter {@link Filter#accept(Object) akzeptiert} werden.
	 *
	 * @param filter Filter.
	 * @return Gefilterte Menge. */
	public GISet having(Filter<? super GI> filter) throws NullPointerException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die in dieser oder der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Vereinigung dieser mit der gegebenen Menge. */
	public GISet union(GISet set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die gleichzeitig in dieser und nicht in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Diese Menge ohne die gegebenen Menge. */
	public GISet except(GISet set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die gleichzeitig in dieser und in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Schitt dieser mit der gegebenen Menge. */
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

	/** Diese Methode gibt eine {@link #order() geordnete} Kopie dieser Menge als {@link List} zurück.
	 *
	 * @return geordnete Kopie dieser Menge. */
	public List<GI> toList();

}