package bee.creative.lang;

/** Diese Schnittstelle definiert ein Berechnun mit Ergebis, die beliebige Ausnahmen auslösen kann.
 *
 * @param <T> Typ des Ergebisses.
 * @author [cc-by] 2024 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Gettable2<T> {

	/** Diese Methode liefert das Ergebis der Berechnung.
	 *
	 * @return Ergebnis.
	 * @throws Exception Wenn während der Berechnung ein Fehler eintritt. */
	T get() throws Exception;

}