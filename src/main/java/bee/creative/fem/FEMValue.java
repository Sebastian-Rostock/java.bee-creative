package bee.creative.fem;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer {@link FEMFunction Funktion} oder als Parameter in den {@link FEMFrame Rahmendaten} zur
 * Auswertung einer Funktion verwendet werden kann. Ein solcher Wert besitzt dazu {@link FEMValue#data() Nutzdaten} mit einem bestimmeten
 * {@link FEMValue#type() Datentyp}.<br>
 * Die Konvertierung der Nutzdaten in einen gegebenen Datentyp {@code type} kann im Rahmen eines gegebenen {@link FEMContext Kontextobjekts} {@code context}
 * über den Befehl {@code context.dataOf(this, type)} erfolgen.
 * 
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface FEMValue {

	/**
	 * Diese Methode gibt den Datentyp zurück.
	 * 
	 * @return Datentyp.
	 */
	public FEMType<?> type();

	/**
	 * Diese Methode gibt die Nutzdaten zurück.
	 * 
	 * @return Nutzdaten.
	 */
	public Object data();

}
