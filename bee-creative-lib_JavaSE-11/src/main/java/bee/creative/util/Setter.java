package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Schreiben einer Eigenschaft eines gegebenen Datensatzes.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <ITEM> Typ des Datensatzes.
 * @param <VALUE> Typ des Werts der Eigenschaft. */
public interface Setter<ITEM, VALUE> {

	/** Diese Methode setzt den Wert der Eigenschaft des gegebenen Datensatzes.
	 *
	 * @param item Datensatz.
	 * @param value Wert der Eigenschaft. */
	void set(ITEM item, VALUE value);

}