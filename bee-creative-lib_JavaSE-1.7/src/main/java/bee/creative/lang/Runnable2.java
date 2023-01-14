package bee.creative.lang;

/** Diese Schnittstelle definiert ein Berechnung, die beliebige Ausnahmen auslösen kann.
 *
 * @author [cc-by] 2012 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/] */
public interface Runnable2 {

	/** Diese Methode führt die Berechnung aus.
	 *
	 * @throws Throwable Wenn während der Berechnung ein Fehler eintritt. */
	public void run() throws Throwable;

}