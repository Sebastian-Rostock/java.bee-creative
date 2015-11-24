package bee.creative.fem;

/**
 * Diese Schnittstelle definiert eine Funktion, deren {@link FEMFunction#execute(FEMScope) Berechnungsmethode} mit einem Ausführungskontext aufgerufen wird und einen
 * Ergebniswert zurück gibt. Aus dem {@link FEMScope#context() Kontextobjekt} des Ausführungskontexts können hierbei Informationen für die Berechnungen extrahiert
 * oder auch der Zustand dieses Objekts modifiziert werden.
 * 
 * @see FEMValue
 * @see FEMScope
 * @see Functions
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface FEMFunction {

	/**
	 * Diese Methode führt Berechnungen im gegebenen Ausführungskontext durch und gibt deren Ergebniswert zurück.
	 * 
	 * @param scope Ausführungskontext.
	 * @return Ergebniswert.
	 */
	public FEMValue execute(FEMScope scope);

}
