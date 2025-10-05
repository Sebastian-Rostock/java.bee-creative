package bee.creative.qs;

import bee.creative.util.Filter;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine filter- und kopier- und komponierbare Menge von Elementen mit Bezug zu einem
 * {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Elemente.
 * @param <THIS> Typ dieser Menge. */
public interface QOSet<ITEM, THIS> extends QISet<ITEM> {

	/** Diese Methode liefert eine temporäre Kopie dieser Menge, sofern diese Menge keine ist. Andernfalls wird {@code this} geliefert.
	 *
	 * @see #copy(Filter)
	 * @return Kopie dieser Menge oder {@code this}. */
	THIS copy();

	/** Diese Methode liefert eine temporäre Kopie der Menge der Elemente, die vom gegebenen Filter {@link Filter#accepts(Object) akzeptiert} werden.
	 *
	 * @param filter Filter.
	 * @return Gefilterte Menge. */
	THIS copy(Filter<? super ITEM> filter) throws NullPointerException;

	/** Diese Methode liefert die geordnete Mengensicht auf die Elemente dieser Menge, sofern diese Menge keine ist. Andernfalls wird {@code this} geliefert.
	 *
	 * @return Geordnete Menge. */
	THIS order();

	/** Diese Methode liefert die Mengensicht auf die Elemente, die in dieser oder der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Vereinigung dieser mit der gegebenen Menge. */
	THIS union(THIS set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert die Mengensicht auf die Elemente, die gleichzeitig in dieser und nicht in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return diese Menge ohne die gegebenen Menge. */
	THIS except(THIS set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert die Mengensicht auf die Elemente, die gleichzeitig in dieser und in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Schitt dieser mit der gegebenen Menge. */
	THIS intersect(THIS set) throws NullPointerException, IllegalArgumentException;

}