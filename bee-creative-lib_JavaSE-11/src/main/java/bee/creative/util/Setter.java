package bee.creative.util;

/** Diese Schnittstelle definiert einen Adapter zum Schreiben einer Eigenschaft eines gegebenen Datensatzes.
 *
 * @author [cc-by] 2017 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Datensatzes.
 * @param <V> Typ des Werts der Eigenschaft. */
public interface Setter<T, V> {

	/** Diese Methode setzt den Wert der Eigenschaft des gegebenen Datensatzes.
	 *
	 * @param item Datensatz.
	 * @param value Wert der Eigenschaft. */
	void set(T item, V value);

}