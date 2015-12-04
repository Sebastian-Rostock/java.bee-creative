package bee.creative.fem;

/**
 * Diese Schnittstelle definiert eine Funktion, deren {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} mit {@link FEMFrame Rahmendaten} aufgerufen wird
 * und einen Ergebniswert liefert.<br>
 * Aus den {@link FEMFrame#params() Parameterwerten} sowie dem {@link FEMFrame#context() Kontextobjekt} der Rahmendaten können hierbei Informationen für die
 * Berechnungen extrahiert werden. Des Weiteren kann auch der Zustand des Kontextobjekts modifiziert werden.
 * 
 * @see FEMValue
 * @see FEMFrame
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface FEMFunction {

	/**
	 * Diese Methode führt Berechnungen mit den gegebenen Rahmendaten durch und gibt den ermittelten deren Ergebniswert zurück.
	 * 
	 * @param frame Rahmendaten.
	 * @return Ergebniswert.
	 */
	public FEMValue invoke(FEMFrame frame);

}
