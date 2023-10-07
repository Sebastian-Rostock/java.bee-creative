package bee.creative.qs;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import bee.creative.util.Iterator2;

/** Diese Schnittstelle definiert eine beliebig große Sicht auf eine Menge von Objekten mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GI> Typ der Einträge. */
public interface QISet<GI> extends QO, Iterable<GI> {

	/** Diese Methode gibt die Anzahl der in dieser Menge enthaltenen Objekte zurück.
	 *
	 * @return Objektanzahl. */
	long size();

	/** Diese Methode gibt nur dann {@code true} zurück, wenn diese Menge leer ist.
	 *
	 * @return {@code true}, nur wenn der {@link #iterator()} kein Objekt liefert. */
	boolean isEmpty();

	/** Diese Methode gibt das erste Objekt in dieser Menge oder {@code null} zurück.
	 *
	 * @return erstes Objekt oder {@code null}. */
	GI first();

	/** Diese Methode gibt den {@link Iterator2} über die Objekte dieser Menge zurück. Die Methode {@link Iterator#remove()} wird nicht unterstützt.
	 *
	 * @return Iterator. */
	@Override
	Iterator2<GI> iterator();

	/** Diese Methode gibt eine Kopie dieser Menge als {@link Set} zurück.
	 *
	 * @return Kopie dieser Menge. */
	default Set<GI> toSet(){
		return this.iterator().toSet();
	}

	/** Diese Methode gibt eine Kopie dieser Menge als {@link List} zurück.
	 *
	 * @return geordnete Kopie dieser Menge. */
	default List<GI> toList() {
		return this.iterator().toList();
	}

}