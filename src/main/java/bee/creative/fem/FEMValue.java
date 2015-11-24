package bee.creative.fem;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer {@link FEMFunction Funktion} oder als Parameter in einem {@link FEMScope Ausführungskontext}
 * verwendet werden kann. Ein solcher Wert besitzt dazu {@link FEMValue#data() Nutzdaten} mit einem bestimmeten {@link FEMValue#type() Datentyp}.
 * <p>
 * Die Konvertierung der Nutzdaten in einen gegebenen Datentyp {@code type} erfolgt im Rahmen eines gegebenen {@link Context Kontextobjekts} {@code context}
 * über den Befehl {@code context.dataOf(this, type)}.
 * 
 * @see Values
 * @see Context
 * @see Context#DEFAULT
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
