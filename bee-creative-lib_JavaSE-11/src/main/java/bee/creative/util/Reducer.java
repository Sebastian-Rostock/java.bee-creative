package bee.creative.util;

/** Diese Schnittstelle definiert eine Funktion zur Zusammenfassung eines gegebenen Elements und eines gegebenen vorherigen Ergebniswerts zu einem neuen
 * Ergebniswert.
 *
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <T> Typ des Elements.
 * @param <V> Typ des Ergebniswerts. */
public interface Reducer<T, V> {

	/** Diese Methode liefert den neuen Ergebniswert, der aus dem gegebenen Elements und dem gegebenen vorherigen Ergebniswert abgeleitet wurde.
	 *
	 * @param item Element.
	 * @param value vorheriger Ergebniswert.
	 * @return neuer Ergebniswert. */
	V get(T item, V value);

}