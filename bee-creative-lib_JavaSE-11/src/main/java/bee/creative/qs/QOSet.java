package bee.creative.qs;

import bee.creative.util.Filter;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine filter- und kopier- und komponierbare Menge von Elementen mit Bezug zu einem
 * {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <E> Typ der Elemente.
 * @param <T> Typ dieser Menge. */
public interface QOSet<E, T> extends QISet<E> {

	/** Diese Methode liefert eine temporäre Kopie dieser Menge, sofern diese Menge keine ist. Andernfalls wird {@code this} geliefert.
	 *
	 * @see #copy(Filter)
	 * @return Kopie dieser Menge oder {@code this}. */
	T copy();

	/** Diese Methode liefert eine temporäre Kopie der Menge der Elemente, die vom gegebenen Filter {@link Filter#accepts(Object) akzeptiert} werden.
	 *
	 * @param filter Filter.
	 * @return Gefilterte Menge. */
	T copy(Filter<? super E> filter) throws NullPointerException;

	/** Diese Methode liefert die geordnete Mengensicht auf die Elemente dieser Menge, sofern diese Menge keine ist. Andernfalls wird {@code this} geliefert.
	 *
	 * @return Geordnete Menge. */
	T order();

	/** Diese Methode liefert die Mengensicht auf die Elemente, die in dieser oder der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Vereinigung dieser mit der gegebenen Menge. */
	T union(T set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert die Mengensicht auf die Elemente, die gleichzeitig in dieser und nicht in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return diese Menge ohne die gegebenen Menge. */
	T except(T set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode liefert die Mengensicht auf die Elemente, die gleichzeitig in dieser und in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Schitt dieser mit der gegebenen Menge. */
	T intersect(T set) throws NullPointerException, IllegalArgumentException;

}