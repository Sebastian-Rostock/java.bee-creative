package bee.creative.function;

/**
 * Diese Schnittstelle definiert einen Wert, der als Ergebnis einer {@link Function Funktion} oder als Parameter in einem {@link Scope Ausführungskontext}
 * verwendet werden kann. Ein solcher Wert besitzt dazu {@link Value#data() Nutzdaten} mit einem bestimmeten {@link Value#type() Datentyp}.
 * <p>
 * Die Konvertierung der Nutzdaten in einen gegebenen Datentyp {@code type} erfolgt im Rahmen eines gegebenen {@link Context Kontextobjekts} {@code context}
 * über den Befehl {@code context.dataOf(this, type)}.
 * 
 * @see Values
 * @see Context
 * @see Context#DEFAULT
 * @author [cc-by] 2011 Sebastian Rostock [http://creativecommons.org/licenses/by/3.0/de/]
 */
public interface Value {

	/**
	 * Diese Methode gibt den Datentyp zurück.
	 * 
	 * @return Datentyp.
	 */
	public Type<?> type();

	/**
	 * Diese Methode gibt die Nutzdaten zurück.
	 * 
	 * @return Nutzdaten.
	 */
	public Object data();

}
