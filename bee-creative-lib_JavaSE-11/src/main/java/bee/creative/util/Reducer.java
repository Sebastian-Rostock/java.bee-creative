package bee.creative.util;

/** Diese Schnittstelle definiert eine Funktion zur Zusammenfassung eines gegebenen Elements und eines gegebenen vorherigen Ergebniswerts zu einem neuen
 * Ergebniswert.
 * 
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 * @param <GItem> Typ des Elements.
 * @param <GValue> Typ des Ergebniswerts. */
public interface Reducer<GItem, GValue> {

	/** Diese Methode liefert den neuen Ergebniswert, der aus dem gegebenen Elements und dem gegebenen vorherigen Ergebniswert abgeleitet wurde.
	 * 
	 * @param item Element.
	 * @param value vorheriger Ergebniswert.
	 * @return neuer Ergebniswert. */
	GValue get(GItem item, GValue value);

}