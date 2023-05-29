package bee.creative.qs;

import bee.creative.util.Filter;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine filter- und kopier- und komponierbare Menge von Objekten mit Bezug zu einem {@link #owner()
 * Graphspeicher}.
 *
 * @author [cc-by] 2020 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge.
 * @param <GISet> Typ dieser Menge. */
public interface QOSet<GI, GISet> extends QISet<GI> {

	/** Diese Methode gibt eine temporäre Kopie dieser Menge zurück.
	 *
	 * @return Kopie dieser Menge. */
	GISet copy();

	/** Diese Methode gibt die geordnete Mengensicht auf die Objekte dieser Menge zurück.
	 *
	 * @return Geordnete Menge. */
	GISet order();

	/** Diese Methode gibt eine temporäre Kopie der Menge der Objekte zurück, die vom gegebenen Filter {@link Filter#accept(Object) akzeptiert} werden.
	 *
	 * @param filter Filter.
	 * @return Gefilterte Menge. */
	GISet having(Filter<? super GI> filter) throws NullPointerException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die in dieser oder der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Vereinigung dieser mit der gegebenen Menge. */
	GISet union(GISet set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die gleichzeitig in dieser und nicht in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Diese Menge ohne die gegebenen Menge. */
	GISet except(GISet set) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode gibt die Mengensicht auf die Objekte zurück, die gleichzeitig in dieser und in der gegebenen Menge enthalten sind.
	 *
	 * @param set Menge.
	 * @return Schitt dieser mit der gegebenen Menge. */
	GISet intersect(GISet set) throws NullPointerException, IllegalArgumentException;

}