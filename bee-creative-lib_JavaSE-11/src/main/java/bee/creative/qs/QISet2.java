package bee.creative.qs;

import java.util.Arrays;

/** Diese Schnittstelle definiert eine beliebig große änderbare Menge von Elementen mit Bezug zu einem {@link #owner() Graphspeicher}.
 *
 * @author [cc-by] 2023 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ der Elemente. */
public interface QISet2<ITEM> extends QISet<ITEM> {

	/** Diese Methode entfern alle Elemente aus dieser Menge und liefert nur dann {@code true}, wenn die Menge dadurch verändert wurde.
	 *
	 * @return {@code true} bei Änderung der Menge bzw. {@code false} sonst. */
	boolean clear();

	/** Diese Methode fügt das gegebene Element in diese Menge ein und liefert nur dann {@code true}, wenn die Menge dadurch verändert wurde.
	 *
	 * @param item Element zum Einfügen.
	 * @return {@code true} bei Änderung der Menge bzw. {@code false} sonst. */
	default boolean insert(ITEM item) throws NullPointerException, IllegalArgumentException {
		return this.insertAll(Arrays.asList(item));
	}

	/** Diese Methode fügt die gegebenen Elemente in diese Menge ein und liefert nur dann {@code true}, wenn die Menge dadurch verändert wurde.
	 *
	 * @param items Elemente zum Einfügen.
	 * @return {@code true} bei Änderung der Menge bzw. {@code false} sonst. */
	boolean insertAll(Iterable<? extends ITEM> items) throws NullPointerException, IllegalArgumentException;

	/** Diese Methode entfernt das gegebene Element aus dieser Menge und liefert nur dann {@code true}, wenn die Menge dadurch verändert wurde.
	 *
	 * @param item Element zum Entfernen.
	 * @return {@code true} bei Änderung der Menge bzw. {@code false} sonst. */
	default boolean delete(ITEM item) throws NullPointerException, IllegalArgumentException {
		return this.deleteAll(Arrays.asList(item));
	}

	/** Diese Methode entfernt die gegebenen Elemente aus dieser Menge und liefert nur dann {@code true}, wenn die Menge dadurch verändert wurde.
	 *
	 * @param items Elemente zum Entfernen.
	 * @return {@code true} bei Änderung der Menge bzw. {@code false} sonst. */
	boolean deleteAll(Iterable<? extends ITEM> items) throws NullPointerException, IllegalArgumentException;

}
