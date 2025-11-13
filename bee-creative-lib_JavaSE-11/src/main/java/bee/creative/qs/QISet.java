package bee.creative.qs;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.util.Iterator3;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Menge von Elementen mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Elemente. */
public interface QISet<ITEM> extends QO, Iterable<ITEM> {

	/** Diese Methode gibt die Anzahl der in dieser Menge enthaltenen Objekte zurück.
	 *
	 * @return Objektanzahl. */
	long size();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Menge leer ist.
	 *
	 * @return {@code true}, nur wenn der {@link #iterator()} kein Objekt liefert. */
	default boolean isEmpty() {
		return !this.iterator().hasNext();
	}

	/** Diese Methode gibt das erste Objekt in dieser Menge oder {@code null} zurück.
	 *
	 * @return erstes Objekt oder {@code null}. */
	default ITEM first() {
		for (var item: this)
			return item;
		return null;
	}

	/** Diese Methode gibt den {@link Iterator3} über die Objekte dieser Menge zurück. Die Methode {@link Iterator#remove()} wird nicht unterstützt.
	 *
	 * @return Iterator. */
	@Override
	Iterator3<ITEM> iterator();

	/** Diese Methode gibt eine Kopie dieser Menge als {@link Set} zurück.
	 *
	 * @return Kopie dieser Menge. */
	default Set<ITEM> toSet() {
		return this.iterator().toSet();
	}

	/** Diese Methode gibt eine Kopie dieser Menge als {@link List} zurück.
	 *
	 * @return Kopie dieser Menge. */
	default List<ITEM> toList() {
		return this.iterator().toList();
	}

}