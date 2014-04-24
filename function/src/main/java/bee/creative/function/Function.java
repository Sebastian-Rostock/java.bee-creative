package bee.creative.function;

/**
 * Diese Schnittstelle definiert eine Funktion, deren {@link Function#execute(Scope) Berechnungsmethode} mit einem Ausführungskontext aufgerufen wird und einen
 * Ergebniswert zurück gibt. Aus dem {@link Scope#context() Kontextobjekt} des Ausführungskontexts können hierbei Informationen für die Berechnungen extrahiert
 * oder auch der Zustand dieses Objekts modifiziert werden.
 * 
 * @see Value
 * @see Scope
 * @see Functions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Function {

	/**
	 * Diese Methode führt Berechnungen im gegebenen Ausführungskontext durch und gibt deren Ergebniswert zurück.
	 * 
	 * @param scope Ausführungskontext.
	 * @return Ergebniswert.
	 */
	public Value execute(Scope scope);

}
