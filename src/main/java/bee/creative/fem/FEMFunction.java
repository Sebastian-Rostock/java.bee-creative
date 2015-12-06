package bee.creative.fem;

/**
 * Diese Schnittstelle definiert eine Funktion, deren {@link FEMFunction#invoke(FEMFrame) Berechnungsmethode} mit einem {@link FEMFrame Stapelrahmen} aufgerufen
 * wird und einen Ergebniswert liefert.<br>
 * Aus den {@link FEMFrame#params() Parameterwerten} sowie dem {@link FEMFrame#context() Kontextobjekt} der Stapelrahmens können hierbei Informationen für die
 * Berechnungen extrahiert werden. Der Zustand des Kontextobjekts kann auch modifiziert werden.
 * 
 * @see FEMValue
 * @see FEMFrame
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface FEMFunction {

	/**
	 * Diese Methode führt Berechnungen im gegebenen Stapelrahmen durch und gibt den ermittelten Ergebniswert zurück.
	 * 
	 * @param frame Stapelrahmen.
	 * @return Ergebniswert.
	 */
	public FEMValue invoke(FEMFrame frame);

}
